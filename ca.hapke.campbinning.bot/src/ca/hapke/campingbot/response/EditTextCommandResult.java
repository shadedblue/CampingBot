package ca.hapke.campingbot.response;

import java.io.Serializable;
import java.util.List;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.BotConstants;
import ca.hapke.campingbot.api.CampingBotEngine;
import ca.hapke.campingbot.commands.api.CommandType;
import ca.hapke.campingbot.response.fragments.ResultFragment;

/**
 * @author Nathan Hapke
 */
public class EditTextCommandResult extends CommandResult {

	private Message message;

	public EditTextCommandResult(CommandType cmd, Message message) {
		super(cmd);
		this.message = message;
	}

	public EditTextCommandResult(CommandType cmd, Message message, ResultFragment... fragments) {
		super(cmd, fragments);
		this.message = message;
	}

	public EditTextCommandResult(CommandType cmd, Message message, List<ResultFragment> fragments) {
		super(cmd, fragments);
		this.message = message;
	}

	@Override
	public SendResult sendInternal(CampingBotEngine bot, Long chatId) throws TelegramApiException {

		EditMessageText send = new EditMessageText();
		send.setChatId(Long.toString(chatId));
		send.setMessageId(message.getMessageId());
		String text = bot.getProcessor().process(true, fragments);

		String old = message.getText();
		if (old.equalsIgnoreCase(text))
			return new SendResult("Edit ignored: same text as before.", message, null);

		if (keyboard != null && keyboard instanceof InlineKeyboardMarkup)
			send.setReplyMarkup((InlineKeyboardMarkup) keyboard);

		send.setText(text);
		send.setParseMode(BotConstants.MARKDOWN);
		/**
		 * On success, if edited message is sent by the bot, the edited Message is returned, otherwise True is returned.
		 */
		Serializable out = bot.execute(send);
		if (out instanceof Message) {
			Message msg = (Message) out;
			return new SendResult(text, message, msg);
		} else {
			return new SendResult("Edit failed", message, null);
		}
	}

}
