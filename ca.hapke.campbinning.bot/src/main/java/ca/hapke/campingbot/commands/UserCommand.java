package ca.hapke.campingbot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.BotChoicePriority;
import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.CampingSystem;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.SlashCommand;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.fragments.CaseChoice;
import ca.hapke.campingbot.response.fragments.MentionDisplay;
import ca.hapke.campingbot.response.fragments.MentionFragment;
import ca.hapke.campingbot.response.fragments.ResultFragment;
import ca.hapke.campingbot.response.fragments.TextStyle;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.users.CampingUser.Birthday;

/**
 * @author Nathan Hapke
 */
public class UserCommand extends SlashCommand {

	private static final String prettyName = "User Information";
	private static final String slashCommand = "user";

	private static final SlashCommandType SLASH_USER = new SlashCommandType(prettyName, slashCommand,
			BotCommandIds.REGULAR_CHAT);
	private static final SlashCommandType[] SLASH_COMMAND_TYPES = new SlashCommandType[] { SLASH_USER };
	private final CampingBot bot;

	public UserCommand(CampingBot bot) {
		this.bot = bot;
	}

	@Override
	public SlashCommandType[] getSlashCommandsToRespondTo() {
		return SLASH_COMMAND_TYPES;
	}

	@Override
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, Long chatId,
			CampingUser campingFromUser) throws TelegramApiException {
		CommandResult result = new TextCommandResult(command);
		CampingUser target = this.bot.findTarget(message, false, true, BotChoicePriority.Last);

		if (target == null) {
			result.add(campingFromUser).add(": No user chosen");
			return result;
		}

		result.add("Username", TextStyle.Bold);
		result.add(ResultFragment.COLON_SPACE);
		String username = target.getUsername();
		if (username == null || username.length() == 0) {
			result.add("N/A");
		} else {
			result.add(new MentionFragment(target, MentionDisplay.Username, CaseChoice.Normal));
		}
		result.newLine();

		long userId = target.getTelegramId();
		result.add("Telegram ID", TextStyle.Bold);
		result.add(ResultFragment.COLON_SPACE);
		result.add(userId);
		result.newLine();

		long botId = target.getCampingId();
		result.add("Bot ID", TextStyle.Bold);
		result.add(ResultFragment.COLON_SPACE);
		result.add(botId);
		result.newLine();

		result.add("Name", TextStyle.Bold);
		result.add(ResultFragment.COLON_SPACE);
		result.add(target.getFirstname());
		result.add(" ");
		result.add(target.getLastname());
		result.newLine();

		String initials = target.getInitials();
		result.add("Initials", TextStyle.Bold);
		result.add(ResultFragment.COLON_SPACE);
		if (initials == null || initials.length() == 0) {
			result.add("Not Set");
		} else {
			result.add(initials);
		}
		result.newLine();

		result.add("Birthday", TextStyle.Bold);
		result.add(ResultFragment.COLON_SPACE);
		Birthday bday = target.getBirthday();
		if (bday == null) {
			result.add("Unknown");
		} else {
			result.add(bday.toString());
		}
		result.newLine();

		String nickname = target.getNickname();
		result.add("Nickname", TextStyle.Bold);
		result.add(ResultFragment.COLON_SPACE);
		if (initials == null || initials.length() == 0) {
			result.add("Not Set");
		} else {
			result.add(nickname);
		}
		result.newLine();
		result.add("Access Level: ", TextStyle.Bold);

		if (CampingSystem.getInstance().isAdmin(target)) {
			result.add("Admin");
		} else {
			result.add("User");
		}

		return result;
	}

	@Override
	public String getCommandName() {
		return prettyName;
	}

	@Override
	public void appendHelpText(SlashCommandType cmd, TextCommandResult result) {
		result.add("Get information about a user.");
		result.newLine();
		result.add("Usage: ");
		result.add("/user [username|reply to message]");
		result.newLine();
		result.add("If no user is specified, the command will use the user who sent the command.");
	}
}
