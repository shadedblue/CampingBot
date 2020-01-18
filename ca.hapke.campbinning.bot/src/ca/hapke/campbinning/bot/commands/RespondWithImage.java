package ca.hapke.campbinning.bot.commands;

import java.util.List;

import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBot;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.util.CampingUtil;

/**
 * @author Nathan Hapke
 */
public abstract class RespondWithImage implements TextCommand {

	protected static final int STATIC = 1;
	protected static final int GIF = 2;
	protected CampingBot bot;
	private String[] urls;
	private int[] imageTypes;

	public RespondWithImage(CampingBot bot, String[] urls, int[] imageTypes) {
		this.bot = bot;
		this.urls = urls;
		this.imageTypes = imageTypes;
	}

	@Override
	public TextCommandResult textCommand(CampingUser campingFromUser, List<MessageEntity> entities, Long chatId,
			Message message) {

		int i = CampingUtil.getRandomIndex(urls);
		if (i >= 0) {
			String url = urls[i];

			try {
				switch (imageTypes[i]) {
				case STATIC:
					SendPhoto p = new SendPhoto();
					p.setChatId(chatId);
					p.setPhoto(url);

					bot.execute(p);
					break;
				case GIF:
					SendAnimation ani = new SendAnimation();
					ani.setAnimation(url);
					ani.setChatId(chatId);
					bot.execute(ani);
					break;
				}

				return new TextCommandResult(getCommandType(), url, false);
			} catch (TelegramApiException e) {
			}
		}
		return null;
	}

	protected abstract BotCommand getCommandType();

}