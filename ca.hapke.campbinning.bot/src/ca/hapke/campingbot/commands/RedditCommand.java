package ca.hapke.campingbot.commands;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.CommandType;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.commands.api.TextCommand;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.fragments.LinkFragment;
import ca.hapke.campingbot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class RedditCommand extends AbstractCommand implements TextCommand {

	private static final String BEGINNING = "^";
	private static final String R_SLASH = "/?r/";
	private static final Pattern INSIDE_WORD_PATTERN = Pattern.compile(BEGINNING + R_SLASH + "[A-Za-z0-9]+\\b");
	private static final Pattern ANY_R_PATTERN = Pattern.compile(R_SLASH + "[A-Za-z0-9]+\\b");
	private static final String REDDIT_COMMAND = "reddit";
	private static final String REDDIT_PRETTY = "Reddit";
	private static final SlashCommandType REDDIT_COMMAND_TYPE = new SlashCommandType(REDDIT_PRETTY, REDDIT_COMMAND,
			BotCommandIds.TEXT | BotCommandIds.REPLY | BotCommandIds.USE);

	@Override
	public String getCommandName() {
		return REDDIT_COMMAND;
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
		result.add(new LinkFragment("https://www.reddit.com/" + subreddit, display));
		return result;
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
}
