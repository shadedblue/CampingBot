package ca.hapke.campbinning.bot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campbinning.bot.AccessLevel;
import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.response.CommandResult;
import ca.hapke.campbinning.bot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public interface SlashCommand {
	public BotCommand[] getSlashCommandsToRespondTo();

	public CommandResult respondToSlashCommand(BotCommand command, Message message, Long chatId,
			CampingUser campingFromUser) throws TelegramApiException;

	public AccessLevel accessRequired();
}
