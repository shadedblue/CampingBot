package ca.hapke.campbinning.bot.commands.api;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campbinning.bot.AccessLevel;
import ca.hapke.campbinning.bot.response.CommandResult;
import ca.hapke.campbinning.bot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public interface SlashCommand {
	public SlashCommandType[] getSlashCommandsToRespondTo();

	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, Long chatId,
			CampingUser campingFromUser) throws TelegramApiException;

	public AccessLevel accessRequired();
}
