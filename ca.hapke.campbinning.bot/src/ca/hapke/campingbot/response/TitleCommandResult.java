package ca.hapke.campingbot.response;

import java.util.List;

import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatTitle;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.api.CampingBotEngine;
import ca.hapke.campingbot.commands.api.CommandType;
import ca.hapke.campingbot.processors.MessageProcessor;
import ca.hapke.campingbot.response.fragments.ResultFragment;

/**
 * @author Nathan Hapke
 */
public class TitleCommandResult extends CommandResult {

	public TitleCommandResult(CommandType cmd) {
		super(cmd);
	}

	public TitleCommandResult(CommandType cmd, ResultFragment... fragments) {
		super(cmd, fragments);
	}

	public TitleCommandResult(CommandType cmd, List<ResultFragment> fragments) {
		super(cmd, fragments);
	}

	@Override
	public SendResult sendInternal(CampingBotEngine bot, Long chatId) throws TelegramApiException {
		MessageProcessor processor = bot.getProcessor();
		String msg = processor.process(false, this.fragments);

		SetChatTitle sct = new SetChatTitle(Long.toString(chatId), msg);
		Boolean success = bot.execute(sct);

		return new SendResult("Title change: " + msg, null, success);
	}

}
