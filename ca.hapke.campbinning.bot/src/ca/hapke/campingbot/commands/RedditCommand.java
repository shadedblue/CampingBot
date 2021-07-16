package ca.hapke.campingbot.commands;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.vdurmont.emoji.EmojiManager;

import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.CommandType;
import ca.hapke.campingbot.commands.api.SlashCommand;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.commands.api.TextCommand;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.fragments.CaseChoice;
import ca.hapke.campingbot.response.fragments.EmojiFragment;
import ca.hapke.campingbot.response.fragments.InsultFragment;
import ca.hapke.campingbot.response.fragments.InsultFragment.Perspective;
import ca.hapke.campingbot.response.fragments.LinkFragment;
import ca.hapke.campingbot.response.fragments.TextFragment;
import ca.hapke.campingbot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class RedditCommand extends AbstractCommand implements SlashCommand, TextCommand {

	private static final String BEGINNING = "^";
//	private static final String SLASH_R = "(/)?r/";
	private static final String R_SLASH = "/?r/";
//	private static final String SLASH_R_PATTERN = SLASH_R + "[A-Za-z0-9]+";
	private static final Pattern INSIDE_WORD_PATTERN = Pattern.compile(BEGINNING + R_SLASH + "[A-Za-z0-9]+\\b");
	private static final Pattern ANY_R_PATTERN = Pattern.compile(R_SLASH + "[A-Za-z0-9]+\\b");
	private static final TextFragment DO_U = new TextFragment("DO U EVEN REDDIT, ");
	private static final TextFragment QUESTION_MARK = new TextFragment("? ");
	private static final String REDDIT_COMMAND = "reddit";
	private static final String REDDIT_PRETTY = "Reddit";
	private static final SlashCommandType REDDIT_COMMAND_TYPE = new SlashCommandType(REDDIT_PRETTY, REDDIT_COMMAND,
			BotCommandIds.TEXT | BotCommandIds.REPLY | BotCommandIds.USE);
	private static final SlashCommandType[] slashCommands = new SlashCommandType[] { REDDIT_COMMAND_TYPE };
	private static EmojiFragment mf = new EmojiFragment(EmojiManager.getForAlias("middle_finger"));

	@Override
	public String getCommandName() {
		return REDDIT_COMMAND;
	}

	@Override
	public SlashCommandType[] getSlashCommandsToRespondTo() {
		return slashCommands;
	}

	@Override
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, Long chatId,
			CampingUser campingFromUser) throws TelegramApiException {
		Message target = message.getReplyToMessage();
		if (target == null)
			return respondFailure(command);
		String msg = target.getText();
		CommandResult sub = findSubreddit(command, msg);
		if (sub != null)
			return sub;
		else
			return respondFailure(command);
	}

	private CommandResult findSubreddit(SlashCommandType command, String msg) {
		String[] split = msg.split("\\s");
		for (String s : split) {
			Matcher matcher = INSIDE_WORD_PATTERN.matcher(s);
			boolean matches = matcher.find();
			if (matches) {
				int start = matcher.start();
				int end = matcher.end();
				String str = s.substring(start, end);
				String lower = str.toLowerCase();
				return linkSubreddit(command, str, lower);
			}
		}

		return null;

	}

	private CommandResult linkSubreddit(CommandType command, String display, String subreddit) {
		TextCommandResult result = new TextCommandResult(command);
		while (subreddit.startsWith("/"))
			subreddit = subreddit.substring(1);
//				result.setDisableWebPagePreview(true);
		result.add(new LinkFragment("https://www.reddit.com/" + subreddit, display));
		return result;
	}

	private CommandResult respondFailure(SlashCommandType command) {
		return new TextCommandResult(command).add(DO_U).add(new InsultFragment(Perspective.You, CaseChoice.Upper))
				.add(QUESTION_MARK).add(mf);
	}

	@Override
	public CommandResult textCommand(CampingUser campingFromUser, List<MessageEntity> entities, Long chatId,
			Message message) {
		String msg = message.getText();
		return findSubreddit(REDDIT_COMMAND_TYPE, msg);
	}

	@Override
	public boolean isMatch(String msg, Message message) {
		Matcher matcher = ANY_R_PATTERN.matcher(msg);
		boolean matches = matcher.find();
		return matches;
	}

	@Override
	public String provideUiStatus() {
		return null;
	}
}
