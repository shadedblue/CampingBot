package ca.hapke.campbinning.bot.commands.response;

import java.util.List;

import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBotEngine;
import ca.hapke.campbinning.bot.commands.ImageLink;
import ca.hapke.campbinning.bot.commands.response.fragments.ResultFragment;

/**
 * @author Nathan Hapke
 */
public class ImageCommandResult extends CommandResult {

	private ImageLink image;

	public ImageCommandResult(BotCommand cmd, ImageLink image) {
		super(cmd);
		this.image = image;
	}

	public ImageCommandResult(BotCommand cmd, ImageLink image, ResultFragment... fragments) {
		super(cmd, fragments);
		this.image = image;
	}

	public ImageCommandResult(BotCommand cmd, ImageLink image, List<ResultFragment> fragments) {
		super(cmd, fragments);
		this.image = image;
	}

	@Override
	public SendResult send(CampingBotEngine bot, Long chatId) {
		MessageProcessor processor = bot.getProcessor();
		String caption = processor.process(this.fragments);
		String url = processor.processImageUrl(image.url);
		try {
			switch (image.type) {
			case ImageLink.STATIC:
				SendPhoto p = new SendPhoto();
				p.setChatId(chatId);
				p.setPhoto(url);
				if (caption != null && caption.length() > 0) {
					p.setCaption(caption);
				}
				bot.execute(p);
				break;
			case ImageLink.GIF:
				SendAnimation ani = new SendAnimation();
				ani.setChatId(chatId);
				ani.setAnimation(url);
				if (caption != null && caption.length() > 0) {
					ani.setCaption(caption);
				}
				bot.execute(ani);
				break;
			}
			return new SendResult(url, caption);
		} catch (TelegramApiException e) {
			return new SendResult(e.getMessage(), url);
		}
	}

}
