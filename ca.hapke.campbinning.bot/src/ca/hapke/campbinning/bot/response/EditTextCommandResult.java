package ca.hapke.campbinning.bot.response;

import java.io.Serializable;
import java.util.List;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campbinning.bot.BotConstants;
import ca.hapke.campbinning.bot.CampingBotEngine;
import ca.hapke.campbinning.bot.commands.api.CommandType;
import ca.hapke.campbinning.bot.response.fragments.ResultFragment;

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
		send.setChatId(chatId);
		send.setMessageId(message.getMessageId());
		String text = bot.getProcessor().process(fragments, true);

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
