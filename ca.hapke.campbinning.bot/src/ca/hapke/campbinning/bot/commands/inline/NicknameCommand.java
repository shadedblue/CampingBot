package ca.hapke.campbinning.bot.commands.inline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBot;
import ca.hapke.campbinning.bot.CampingBotEngine;
import ca.hapke.campbinning.bot.commands.SlashCommand;
import ca.hapke.campbinning.bot.commands.callback.CallbackId;
import ca.hapke.campbinning.bot.commands.response.CommandResult;
import ca.hapke.campbinning.bot.commands.response.MessageProcessor;
import ca.hapke.campbinning.bot.commands.response.TextCommandResult;
import ca.hapke.campbinning.bot.commands.response.fragments.InsultFragment;
import ca.hapke.campbinning.bot.commands.response.fragments.InsultFragment.Perspective;
import ca.hapke.campbinning.bot.commands.response.fragments.MentionFragment;
import ca.hapke.campbinning.bot.commands.response.fragments.ResultFragment;
import ca.hapke.campbinning.bot.commands.response.fragments.TextFragment;
import ca.hapke.campbinning.bot.commands.response.fragments.TextStyle;
import ca.hapke.campbinning.bot.log.EventItem;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.util.CampingUtil;

/**
 * Does the inline responses, as well as nickname change requests (for code-localization)
 * 
 * @author Nathan Hapke
 */
public class NicknameCommand extends InlineCommandBase implements SlashCommand {

	private static final BotCommand[] SLASH_COMMANDS = new BotCommand[] { BotCommand.AllNicknames,
			BotCommand.SetNickname };

	@Override
	public BotCommand[] getSlashCommandsToRespondTo() {
		return SLASH_COMMANDS;
	}

	private static final String INLINE_NICKS = "nicks";
	private static final char[] invalidCharacters = new char[] { '*', '_', '[', ']', '`', '\\', '~' };

	public static final TextFragment INVALID_CHARACTER = new TextFragment(
			"Nickname rejected. Invalid character in nickname.");
	public static final TextFragment CANT_GIVE_YOURSELF_A_NICKNAME = new TextFragment(
			"No fuckin' way.\n#1 rule of nicknames... you can't give yourself a nickname");
	public static final TextFragment USER_NOT_FOUND = new TextFragment("Dunno who you're trying to nickname");
	public static final TextFragment INVALID_SYNTAX = new TextFragment("Invalid syntax, ");

	@Override
	public List<InlineQueryResult> provideInlineQuery(Update update, String input, int updateId,
			MessageProcessor processor) {
		String[] words = input.split(" ");
		List<ResultFragment> out = new ArrayList<>(2 * words.length - 1);
		String converted = null;
		List<Integer> convertedIds = new ArrayList<>();
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			ResultFragment frag = null;
			if (word.length() > 0 && word.charAt(0) == '@') {
				CampingUser cu = userMonitor.getUser(word);
				if (cu != null) {
					frag = new MentionFragment(cu);
					String firstOrUser = cu.getFirstOrUserName();
					if (converted == null) {
						converted = firstOrUser;
					} else {
						converted = converted + ", " + firstOrUser;
					}
					convertedIds.add(cu.getTelegramId());
				}

			}
			if (frag == null)
				frag = new TextFragment(word);

			if (i > 0)
				out.add(ResultFragment.SPACE);
			out.add(frag);
		}
		if (converted == null)
			return null;

		String output = processor.process(out);

		InputTextMessageContent mc = new InputTextMessageContent();
		mc.setDisableWebPagePreview(true);
		mc.setMessageText(output);
		mc.setParseMode(CampingBotEngine.MARKDOWN);

		InlineQueryResultArticle articleUsernameConversion = new InlineQueryResultArticle();
		articleUsernameConversion.setTitle("@usernames converted: " + converted);
		CallbackId fullId = createQueryId(updateId, convertedIds);
		articleUsernameConversion.setId(fullId.getResult());
		articleUsernameConversion.setInputMessageContent(mc);

		return Collections.singletonList(articleUsernameConversion);
	}

	@Override
	public EventItem chosenInlineQuery(Update update, CallbackId id, CampingUser campingFromUser, String resultText) {
		int[] ids = id.getIds();
		List<String> targets = new ArrayList<>(ids.length);
		for (int i = 0; i < ids.length; i++) {
			CampingUser target = userMonitor.getUser(ids[i]);
			if (target != null)
				targets.add(target.getFirstOrUserName());
		}

		String rest = CampingUtil.join(targets, ", ");
		EventItem event = new EventItem(BotCommand.NicknameConversion, campingFromUser, null, null, id.getUpdateId(),
				resultText, rest);
		return event;
	}

	public CommandResult setNicknameCommand(CampingUser campingFromUser, Message message) {
		String originalMsg = message.getText();
		List<MessageEntity> entities = message.getEntities();
		int targetOffset = originalMsg.indexOf(" ") + 1;
		int nickOffset = originalMsg.indexOf(" ", targetOffset) + 1;
		if (targetOffset > 0 && nickOffset > targetOffset + 1) {
			String newNickname = originalMsg.substring(nickOffset);
			MessageEntity targeting = null;

			// TODO Unify with @link CampingBotEngine.findTarget

			for (MessageEntity msgEnt : entities) {
				int offset = msgEnt.getOffset();
				String type = msgEnt.getType();
				if (offset == targetOffset && (CampingBotEngine.MENTION.equalsIgnoreCase(type)
						|| CampingBotEngine.TEXT_MENTION.equalsIgnoreCase(type))) {
					targeting = msgEnt;
				}
			}
			if (targeting != null) {
				CampingUser targetUser = userMonitor.getUser(targeting);

				if (targetUser != null) {
					if (targetUser == campingFromUser) {
						return new TextCommandResult(BotCommand.SetNicknameRejected).add(campingFromUser)
								.add(ResultFragment.COLON_SPACE).add(CANT_GIVE_YOURSELF_A_NICKNAME);
					} else if (rejectNickname(newNickname)) {
						return new TextCommandResult(BotCommand.SetNicknameRejected).add(campingFromUser)
								.add(ResultFragment.COLON_SPACE).add(INVALID_CHARACTER);
					} else {
						String oldNick = targetUser.getNickname();
						targetUser.setNickname(newNickname, true);

						CommandResult result = new TextCommandResult(BotCommand.SetNickname);
						result.add(campingFromUser);
						result.add(ResultFragment.COLON_SPACE);
						result.add(targetUser.getFirstOrUserName(), TextStyle.Bold);
						result.add("'s nickname changed.");
						if (oldNick != null && oldNick.length() > 0
								&& !CampingBot.STRING_NULL.equalsIgnoreCase(oldNick)) {
							result.add("\nFrom: ");
							result.add(oldNick);
						}
						result.add("\nTo: ");
						result.add(targetUser);
						return result;
					}
				}
			} else {
				return new TextCommandResult(BotCommand.SetNicknameRejected).add(campingFromUser)
						.add(ResultFragment.COLON_SPACE).add(USER_NOT_FOUND);
			}
		}
		return new TextCommandResult(BotCommand.SetNicknameRejected).add(campingFromUser)
				.add(ResultFragment.COLON_SPACE).add(INVALID_SYNTAX).add(new InsultFragment(Perspective.You));
	}

	private boolean rejectNickname(String nickname) {
		for (char c : invalidCharacters) {
			if (nickname.indexOf(c) >= 0)
				return true;
		}
		return false;
	}

	public CommandResult allNicknamesCommand() {
		CommandResult sb = new TextCommandResult(BotCommand.AllNicknames);
		for (CampingUser u : userMonitor.getUsers()) {
			String first = u.getFirstname();
			String nick = u.getNickname();
			if (CampingUtil.notEmptyOrNull(nick) && CampingUtil.notEmptyOrNull(first)) {

				sb.add(first, TextStyle.Bold);
				sb.add(ResultFragment.COLON_SPACE);
				sb.add(nick);
				sb.add(ResultFragment.NEWLINE);
			}

		}
		return sb;
	}

	@Override
	public String getCommandName() {
		return INLINE_NICKS;
	}

	@Override
	public CommandResult respondToSlashCommand(BotCommand command, Message message, Long chatId, CampingUser campingFromUser) {
		if (command == BotCommand.AllNicknames)
			return allNicknamesCommand();
		if (command == BotCommand.SetNickname)
			return setNicknameCommand(campingFromUser, message);

		return null;
	}
}
