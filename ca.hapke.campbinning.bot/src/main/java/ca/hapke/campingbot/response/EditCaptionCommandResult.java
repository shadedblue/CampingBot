package ca.hapke.campingbot.response;

import java.io.Serializable;
import java.util.List;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.BotConstants;
import ca.hapke.campingbot.api.CampingBotEngine;
import ca.hapke.campingbot.channels.CampingChat;
import ca.hapke.campingbot.channels.CampingChatManager;
import ca.hapke.campingbot.commands.api.CommandType;
import ca.hapke.campingbot.response.fragments.ResultFragment;

/**
 * @author Nathan Hapke
 */
public class EditCaptionCommandResult extends CommandResult {

	private Message message;

	public EditCaptionCommandResult(CommandType cmd, Message message) {
		super(cmd);
		this.message = message;
	}

	public EditCaptionCommandResult(CommandType cmd, Message message, ResultFragment... fragments) {
		super(cmd, fragments);
		this.message = message;
	}

	public EditCaptionCommandResult(CommandType cmd, Message message, List<ResultFragment> fragments) {
		super(cmd, fragments);
		this.message = message;
	}

	@Override
	public SendResult sendInternal(CampingBotEngine bot, Long chatId) throws TelegramApiException {
		String old = message.getCaption();
		String text = bot.getProcessor().process(true, fragments);
		if (old.equalsIgnoreCase(text))
			return new SendResult("Edit ignored: same text as before.", message, null);

		EditMessageCaption edit = new EditMessageCaption();
		Long chatId2 = message.getChatId();
		edit.setChatId(Long.toString(chatId2));
		edit.setMessageId(message.getMessageId());

		if (keyboard != null && keyboard instanceof InlineKeyboardMarkup)
			edit.setReplyMarkup((InlineKeyboardMarkup) keyboard);

		edit.setCaption(text);
		edit.setParseMode(BotConstants.MARKDOWN);

		/**
		 * On success, if edited message is sent by the bot, the edited Message is returned, otherwise True is returned.
		 */
		Serializable out = bot.execute(edit);
		if (out instanceof Message) {
			Message msg = (Message) out;
			return new SendResult(text, message, msg);
		} else {
			return new SendResult("Edit failed", message, null);
		}
	}

	@Override
	public SendResult send(CampingBotEngine bot, Long chatId) throws TelegramApiException {
		return super.send(bot, message.getChatId());
	}

	@Override
	public SendResult sendAndLog(CampingBotEngine bot, CampingChat chat) {
		Long chatId = message.getChatId();
		CampingChat chat2 = CampingChatManager.getInstance(bot).get(chatId);
		return super.sendAndLog(bot, chat2);
	}

}
