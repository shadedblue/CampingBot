package ca.hapke.campbinning.bot.response;

import java.util.List;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBotEngine;
import ca.hapke.campbinning.bot.processors.MessageProcessor;
import ca.hapke.campbinning.bot.response.fragments.ResultFragment;

/**
 * @author Nathan Hapke
 *
 */
public class TextCommandResult extends CommandResult {

	public TextCommandResult(BotCommand cmd) {
		super(cmd);
	}

	public TextCommandResult(BotCommand cmd, ResultFragment... fragments) {
		super(cmd, fragments);
	}

	public TextCommandResult(BotCommand cmd, List<ResultFragment> fragments) {
		super(cmd, fragments);
	}

	@Override
	public SendResult sendInternal(CampingBotEngine bot, Long chatId) throws TelegramApiException {
		MessageProcessor processor = bot.getProcessor();
		String msg = processor.process(this.fragments, true);
		Message outgoing = sendMsg(bot, chatId, msg);
		SendResult result = new SendResult(msg, outgoing, null);
		return result;
	}

	private Message sendMsg(CampingBotEngine bot, Long chatId, String msg) throws TelegramApiException {
		SendMessage send = new SendMessage(chatId, msg);
		if (replyTo != null)
			send.setReplyToMessageId(replyTo);
		if (keyboard != null)
			send.setReplyMarkup(keyboard);
		send.setParseMode(CampingBotEngine.MARKDOWN);
		return bot.execute(send);
	}

}
