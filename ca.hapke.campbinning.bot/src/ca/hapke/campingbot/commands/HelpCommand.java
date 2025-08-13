package ca.hapke.campingbot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.SlashCommand;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.fragments.TextStyle;
import ca.hapke.campingbot.users.CampingUser;

/**
 * 
 * @author Nathan Hapke
 */
public class HelpCommand extends SlashCommand {

	private static final SlashCommandType SLASH_HELP = new SlashCommandType("help", "help", BotCommandIds.REGULAR_CHAT);
	private static final SlashCommandType[] SLASH_COMMAND_TYPES = new SlashCommandType[] { SLASH_HELP };
	private static final boolean DEBUG = true;
	private final CampingBot bot;

	public HelpCommand(CampingBot bot) {
		this.bot = bot;
	}

	@Override
	public SlashCommandType[] getSlashCommandsToRespondTo() {
		return SLASH_COMMAND_TYPES;
	}

	@Override
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, Long chatId,
			CampingUser campingFromUser) throws TelegramApiException {
		TextCommandResult result = new TextCommandResult(command);

		String msg = message.getText();
		String[] bySpaces = msg.split(" ");
		if (bySpaces.length > 1) {
			String cmdName = bySpaces[1];
			if (DEBUG)
				System.out.println("Help command requested for: " + cmdName);
			SlashCommandType cmdType = bot.getSlashCommandType(cmdName);
			if (DEBUG)
				System.out.println("Command type found: " + cmdType);
			SlashCommand cmd = bot.getSlashCommand(cmdType);
			if (DEBUG)
				System.out.println("Command found: " + cmd);
			if (cmd != null && cmdType != null) {
				cmd.getHelpText(cmdType);
				return result;
			} else {
				result.add("No command found with name: " + cmdName);
				return result;
			}
		}

		result.add("List of Commands:", TextStyle.Underline);
		result.newLine();
		bot.appendCommandList(result);
		result.newLine();
		result.newLine();
		result.add("For more information on a command, type /help <command name>.", TextStyle.Italic);
		return result;
	}

	@Override
	public String getCommandName() {
		return SLASH_HELP.getPrettyName();
	}

	@Override
	protected void appendHelpText(SlashCommandType cmd, TextCommandResult result) {
		// Not used
	}
}
