package ca.hapke.campbinning.bot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.vdurmont.emoji.EmojiManager;

import ca.hapke.campbinning.bot.commands.api.BotCommandIds;
import ca.hapke.campbinning.bot.commands.api.SlashCommand;
import ca.hapke.campbinning.bot.commands.api.SlashCommandType;
import ca.hapke.campbinning.bot.response.CommandResult;
import ca.hapke.campbinning.bot.response.TextCommandResult;
import ca.hapke.campbinning.bot.response.fragments.CaseChoice;
import ca.hapke.campbinning.bot.response.fragments.EmojiFragment;
import ca.hapke.campbinning.bot.response.fragments.InsultFragment;
import ca.hapke.campbinning.bot.response.fragments.InsultFragment.Perspective;
import ca.hapke.campbinning.bot.response.fragments.LinkFragment;
import ca.hapke.campbinning.bot.response.fragments.TextFragment;
import ca.hapke.campbinning.bot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class RedditCommand extends AbstractCommand implements SlashCommand {

	private static final TextFragment DO_U = new TextFragment("DO U EVEN REDDIT, ");
	private static final TextFragment QUESTION_MARK = new TextFragment("? ");
	private static final String REDDIT_COMMAND = "reddit";
	private static final String REDDIT_PRETTY = "Reddit";
	private static final SlashCommandType[] slashCommands = new SlashCommandType[] { new SlashCommandType(REDDIT_PRETTY,
			REDDIT_COMMAND, BotCommandIds.TEXT | BotCommandIds.REPLY | BotCommandIds.USE) };
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
			return null;
		String msg = target.getText();
		String[] split = msg.split("\\s");
		for (String s : split) {
			String lower = s.toLowerCase();
			if (lower.startsWith("r/") || lower.startsWith("/r/")) {
				TextCommandResult result = new TextCommandResult(command);
//				result.setDisableWebPagePreview(true);
				result.add(new LinkFragment("https://www.reddit.com/" + lower, s));
				return result;
			}
		}
		return new TextCommandResult(command).add(DO_U).add(new InsultFragment(Perspective.You, CaseChoice.Upper))
				.add(QUESTION_MARK).add(mf);
	}

}
