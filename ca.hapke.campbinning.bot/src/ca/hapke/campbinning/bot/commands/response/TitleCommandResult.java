package ca.hapke.campbinning.bot.commands.response;

import java.util.List;

import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatTitle;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBotEngine;
import ca.hapke.campbinning.bot.commands.processors.MessageProcessor;
import ca.hapke.campbinning.bot.commands.response.fragments.ResultFragment;

/**
 * @author Nathan Hapke
 */
public class TitleCommandResult extends CommandResult {

	public TitleCommandResult(BotCommand cmd) {
		super(cmd);
	}

	public TitleCommandResult(BotCommand cmd, ResultFragment... fragments) {
		super(cmd, fragments);
	}

	public TitleCommandResult(BotCommand cmd, List<ResultFragment> fragments) {
		super(cmd, fragments);
	}

	@Override
	public SendResult sendInternal(CampingBotEngine bot, Long chatId) throws TelegramApiException {
		MessageProcessor processor = bot.getProcessor();
		String msg = processor.process(this.fragments, false);

		SetChatTitle sct = new SetChatTitle(chatId, msg);
		Boolean success = bot.execute(sct);

		return new SendResult("Title change: " + msg, null, success);
	}

}
