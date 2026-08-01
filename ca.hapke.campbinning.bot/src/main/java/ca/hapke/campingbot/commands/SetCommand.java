package ca.hapke.campingbot.commands;

import java.time.DateTimeException;
import java.time.MonthDay;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.AccessLevel;
import ca.hapke.campingbot.BotChoicePriority;
import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.SlashCommand;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.fragments.ResultFragment;
import ca.hapke.campingbot.response.fragments.TextStyle;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.users.CampingUser.Birthday;

/**
 * @author Nathan Hapke
 */
public class SetCommand extends SlashCommand {
	private static final String REMOVE = "remove";
	private static final String BIRTHDAY = "birthday";
	private static final String BDAY = "bday";
	private static final String INITIALS = "initials";
	private static final String DASH = "-";
	private static final String SET = "Set";
	public static final SlashCommandType SlashSet = new SlashCommandType(SET, "set",
			BotCommandIds.NICKNAME | BotCommandIds.SET);
	private static final SlashCommandType[] SLASH_COMMANDS = new SlashCommandType[] { SlashSet };

	private final CampingBot bot;

	public SetCommand(CampingBot bot) {
		this.bot = bot;
	}

	@Override
	public String getCommandName() {
		return SET;
	}

	@Override
	public AccessLevel accessRequired() {
		return AccessLevel.Admin;
	}

	@Override
	public SlashCommandType[] getSlashCommandsToRespondTo() {
		return SLASH_COMMANDS;
	}

	@Override
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, Long chatId,
			CampingUser campingFromUser) throws TelegramApiException {
		CommandResult result = new TextCommandResult(command);

		String originalMsg = message.getText();
		String[] words = originalMsg.split(" ");
		boolean error = false;
		if (words.length < 4) {
			result.add(campingFromUser);
			result.add(ResultFragment.COLON_SPACE);
			result.add("Format: /set @user field value");
			addFieldInfo(result);

			error = true;
		}

		if (error) {
			return result;
		}

		CampingUser target = this.bot.findTarget(message, false, true, BotChoicePriority.Last);

		String cmd = words[2];
		if (INITIALS.equalsIgnoreCase(cmd)) {
			String initials = words[3];
			target.setInitials(initials);

			result.add(target);
			result.add("'s initials set to ");
			result.add(initials);
		} else if (BIRTHDAY.equalsIgnoreCase(cmd) || BDAY.equalsIgnoreCase(cmd)) {
			String bday = words[3];
			if (bday.contains(DASH)) {
				String[] split = bday.split(DASH);
				try {
					int m = Integer.parseInt(split[0]);
					int d = Integer.parseInt(split[1]);

					MonthDay monthDay = MonthDay.of(m, d);

					Birthday bd = target.setBirthday(monthDay);

					if (bd != null) {
						result.add(target);
						result.add("'s birthday set to ");
						result.add(bd.toString());
					} else {
						result.add("Value must be in the format m-d");
						error = true;
					}
				} catch (NumberFormatException e) {
					result.add("Value must be in the format m-d, or '");
					result.add(REMOVE);
					result.add("'");
					error = true;
				} catch (DateTimeException dte) {
					result.add(dte.getMessage());
					error = true;
				}
			} else if (REMOVE.equalsIgnoreCase(bday)) {
				target.setBirthday(null);
				result.add(target);
				result.add("'s birthday removed");
			} else {
				result.add("Value must be in the format m-d, or '");
				result.add(REMOVE);
				result.add("'");
			}
		} else {
			addFieldInfo(result);
		}

		return result;
	}

	protected void addFieldInfo(CommandResult result) {
		result.newLine();
		result.add("Fields you can set are: ");
		result.newLine();
		result.add(ResultFragment.DASH_SPACE);
		result.add(INITIALS, TextStyle.Bold);
		result.newLine();

		result.add(ResultFragment.DASH_SPACE);
		result.add(BIRTHDAY, TextStyle.Bold);
		result.add("/");
		result.add(BDAY, TextStyle.Bold);
		result.add(": formatted in the style MonthNumber-DayNumber... Ex: 4-20 for April 20th");
	}
	

	@Override
	public void appendHelpText(SlashCommandType cmd, TextCommandResult result) {
		result.add("Set a user's initials or birthday.");
		result.newLine();
		result.add("Format: /set @user field value");
		result.newLine();
		addFieldInfo(result);
	}
}
