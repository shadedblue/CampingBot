package ca.hapke.campingbot.response;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.api.CampingBotEngine;
import ca.hapke.campingbot.commands.api.CommandType;

/**
 * @author Nathan Hapke
 */
public class NoopCommandResult extends CommandResult {

	public NoopCommandResult(CommandType cmd) {
		super(cmd);
	}

	@Override
	public SendResult sendInternal(CampingBotEngine bot, Long chatId) throws TelegramApiException {
		return null;
	}

}
