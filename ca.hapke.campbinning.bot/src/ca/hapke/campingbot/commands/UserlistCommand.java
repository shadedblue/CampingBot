package ca.hapke.campingbot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.AccessLevel;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.SlashCommand;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.fragments.TextStyle;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.users.CampingUser.Birthday;
import ca.hapke.campingbot.users.CampingUserMonitor;

/**
 * @author Nathan Hapke
 */
public class UserlistCommand extends SlashCommand {

	private static final String prettyName = "Userlist";
	private static final String slashCommand = "userlist";

	private static final SlashCommandType SLASH_USERLIST = new SlashCommandType(prettyName, slashCommand,
			BotCommandIds.REGULAR_CHAT);
	private static final SlashCommandType[] SLASH_COMMAND_TYPES = new SlashCommandType[] { SLASH_USERLIST };
	private CampingUserMonitor userMonitor;

	public UserlistCommand() {
		userMonitor = CampingUserMonitor.getInstance();
	}

	@Override
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, Long chatId,
			CampingUser campingFromUser) throws TelegramApiException {
		TextCommandResult result = new TextCommandResult(SLASH_USERLIST);

		for (CampingUser user : userMonitor.getUsers()) {
			result.add(user.getUsername(), TextStyle.Bold);
			result.add(" " + user.getCampingId());
			result.add(" " + user.getTelegramId());
			result.add(" ");
			result.add(user.getLastname());
			result.add(", ");
			result.add(user.getFirstname());
			result.add(": ");
			result.add(user.getBirthdayMonth());
			result.add("/");
			result.add(user.getBirthdayDay());
			result.add("=>");
			Birthday birthday = user.getBirthday();
			if (birthday != null) {
				result.add(birthday.toString());
			} else {
				result.add("null");
			}
			result.add("\n");
		}

		return result;
	}

	@Override
	public SlashCommandType[] getSlashCommandsToRespondTo() {
		return SLASH_COMMAND_TYPES;
	}

	@Override
	public String getCommandName() {
		return prettyName;
	}

	@Override
	protected void appendHelpText(SlashCommandType cmd, TextCommandResult result) {
		result.add("This command lists all users, along with their IDs and birthdays.");
	}
}
