package ca.hapke.campingbot.response;

import java.util.List;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.BotConstants;
import ca.hapke.campingbot.api.CampingBotEngine;
import ca.hapke.campingbot.commands.api.CommandType;
import ca.hapke.campingbot.processors.MessageProcessor;
import ca.hapke.campingbot.response.fragments.ResultFragment;

/**
 * @author Nathan Hapke
 *
 */
public class TextCommandResult extends CommandResult {

	private boolean disableWebPagePreview = false;

	public TextCommandResult(CommandType cmd) {
		super(cmd);
	}

	public TextCommandResult(CommandType cmd, ResultFragment... fragments) {
		super(cmd, fragments);
	}

	public TextCommandResult(CommandType cmd, List<ResultFragment> fragments) {
		super(cmd, fragments);
	}

	public void setDisableWebPagePreview(boolean disableWebPagePreview) {
		this.disableWebPagePreview = disableWebPagePreview;
	}

	@Override
	public SendResult sendInternal(CampingBotEngine bot, Long chatId) throws TelegramApiException {
		MessageProcessor processor = bot.getProcessor();
		String msg = processor.process(true, this.fragments);
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
		if (disableWebPagePreview)
			send.disableWebPagePreview();
		send.setParseMode(BotConstants.MARKDOWN);
		return bot.execute(send);
	}

}
