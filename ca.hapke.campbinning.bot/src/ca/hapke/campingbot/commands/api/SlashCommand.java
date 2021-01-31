package ca.hapke.campingbot.commands.api;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.AccessLevel;
import ca.hapke.campingbot.channels.CampingChat;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public interface SlashCommand {
	public SlashCommandType[] getSlashCommandsToRespondTo();

	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, CampingChat chat,
			CampingUser campingFromUser) throws TelegramApiException;

	public AccessLevel accessRequired();
}
