package ca.hapke.campingbot.commands.api;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.AccessLevel;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.fragments.TextStyle;
import ca.hapke.campingbot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public abstract class SlashCommand extends AbstractCommand {
	public abstract SlashCommandType[] getSlashCommandsToRespondTo();

	public abstract CommandResult respondToSlashCommand(SlashCommandType command, Message message, Long chatId,
			CampingUser campingFromUser) throws TelegramApiException;

	public AccessLevel accessRequired() {
		return AccessLevel.User;
	}

	public TextCommandResult getHelpText(SlashCommandType cmd) {
		TextCommandResult result = new TextCommandResult(cmd);
		result.add(cmd.prettyName, TextStyle.Underline);
		appendHelpText(cmd, result);
		return result;
	}

	protected abstract void appendHelpText(SlashCommandType cmd, TextCommandResult result);
}
