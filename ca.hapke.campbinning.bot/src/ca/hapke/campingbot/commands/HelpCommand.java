package ca.hapke.campingbot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.SlashCommand;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.fragments.TextFragment;
import ca.hapke.campingbot.response.fragments.TextStyle;
import ca.hapke.campingbot.users.CampingUser;

/**
 * 
 * @author Nathan Hapke
 */
public class HelpCommand extends AbstractCommand implements SlashCommand {

	private static final SlashCommandType SLASH_HELP = new SlashCommandType("help", "help", BotCommandIds.REGULAR_CHAT);
	private static final SlashCommandType[] SLASH_COMMAND_TYPES = new SlashCommandType[] { SLASH_HELP };
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
		TextCommandResult result = new TextCommandResult(command,
				new TextFragment("List of Commands", TextStyle.Underline));
		result.newLine();
		bot.appendCommandList(result);
		return result;
	}

	@Override
	public String getCommandName() {
		return SLASH_HELP.getPrettyName();
	}
}
