package ca.hapke.campbinning.bot.response;

import java.io.File;
import java.util.List;

import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBotEngine;
import ca.hapke.campbinning.bot.processors.MessageProcessor;
import ca.hapke.campbinning.bot.response.fragments.ResultFragment;
import ca.hapke.campbinning.bot.util.ImageLink;

/**
 * @author Nathan Hapke
 */
public class ImageCommandResult extends CommandResult {
	private enum SendMode {
		Url,
		File;
	}

	private ImageLink image;
	private SendMode mode;
	private File file;

	public ImageCommandResult(BotCommand cmd, File file) {
		super(cmd);
		this.file = file;
		this.mode = SendMode.File;
	}

	public ImageCommandResult(BotCommand cmd, File file, ResultFragment... fragments) {
		super(cmd, fragments);
		this.file = file;
		this.mode = SendMode.File;
	}

	public ImageCommandResult(BotCommand cmd, File file, List<ResultFragment> fragments) {
		super(cmd, fragments);
		this.file = file;
		this.mode = SendMode.File;
	}

	public ImageCommandResult(BotCommand cmd, ImageLink image) {
		super(cmd);
		this.image = image;
		this.mode = SendMode.Url;
	}

	public ImageCommandResult(BotCommand cmd, ImageLink image, ResultFragment... fragments) {
		super(cmd, fragments);
		this.image = image;
		this.mode = SendMode.Url;
	}

	public ImageCommandResult(BotCommand cmd, ImageLink image, List<ResultFragment> fragments) {
		super(cmd, fragments);
		this.image = image;
		this.mode = SendMode.Url;
	}

	@Override
	public SendResult sendInternal(CampingBotEngine bot, Long chatId) {
		MessageProcessor processor = bot.getProcessor();
		String caption = processor.process(this.fragments, true);
		String url = "";
		try {
			Message outMsg = null;
			switch (mode) {
			case Url:
				url = processor.processImageUrl(image.url);
				switch (image.type) {
				case ImageLink.STATIC:
					SendPhoto p = new SendPhoto();
					p.setPhoto(url);
					outMsg = completePhoto(bot, chatId, caption, p);
					break;
				case ImageLink.GIF:
					SendAnimation ani = new SendAnimation();
					ani.setChatId(chatId);
					ani.setAnimation(url);
					if (caption != null && caption.length() > 0) {
						ani.setCaption(caption);
						ani.setParseMode(CampingBotEngine.MARKDOWN);
					}
					if (replyTo != null)
						ani.setReplyToMessageId(replyTo);
					if (keyboard != null)
						ani.setReplyMarkup(keyboard);
					outMsg = bot.execute(ani);
					break;
				}
				break;
			case File:
				url = file.getAbsolutePath();
				SendPhoto p = new SendPhoto();
				p.setPhoto(file);
				outMsg = completePhoto(bot, chatId, caption, p);
				break;
			}
			if (outMsg == null)
				return null;
			else
				return new SendResult(url, outMsg, caption);
		} catch (TelegramApiException e) {
			return new SendResult(e.getMessage(), null, url);
		}
	}

	public Message completePhoto(CampingBotEngine bot, Long chatId, String caption, SendPhoto p)
			throws TelegramApiException {
		p.setChatId(chatId);
		if (caption != null && caption.length() > 0) {
			p.setCaption(caption);
			p.setParseMode(CampingBotEngine.MARKDOWN);
			if (replyTo != null)
				p.setReplyToMessageId(replyTo);
			if (keyboard != null)
				p.setReplyMarkup(keyboard);
		}
		return bot.execute(p);
	}

}
