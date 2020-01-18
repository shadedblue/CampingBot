package ca.hapke.campbinning.bot.commands;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBot;
import ca.hapke.campbinning.bot.util.CampingUtil;

/**
 * @author Nathan Hapke
 */
public class RespondWithImage {

	protected CampingBot bot;
	protected final List<ImageLink> images = new ArrayList<>();

	public RespondWithImage(CampingBot bot) {
		this.bot = bot;
	}

	public TextCommandResult sendImage(BotCommand commandType, Long chatId, String caption) {

		ImageLink image = CampingUtil.getRandom(images);
		if (image != null) {
			String url = image.url;

			try {
				String msg = url;
				switch (image.type) {
				case ImageLink.STATIC:
					SendPhoto p = new SendPhoto();
					p.setChatId(chatId);
					p.setPhoto(url);
					if (caption != null) {
						p.setCaption(caption);
						msg = msg + " " + caption;
					}
					bot.execute(p);
					break;
				case ImageLink.GIF:
					SendAnimation ani = new SendAnimation();
					ani.setChatId(chatId);
					ani.setAnimation(url);
					if (caption != null) {
						ani.setCaption(caption);
						msg = msg + " " + caption;
					}
					bot.execute(ani);
					break;
				}

				return new TextCommandResult(commandType, msg, false);
			} catch (TelegramApiException e) {
			}
		}
		return null;
	}

	public boolean add(ImageLink e) {
		return images.add(e);
	}

}