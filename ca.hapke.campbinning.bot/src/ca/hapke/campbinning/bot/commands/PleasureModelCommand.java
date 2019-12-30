package ca.hapke.campbinning.bot.commands;

import java.util.List;

import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBot;
import ca.hapke.campbinning.bot.CampingUtil;
import ca.hapke.campbinning.bot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class PleasureModelCommand extends TextCommand {
	public static final String PLEASURE_MODEL = "pleasure model";

	private static final int STATIC = 1;
	private static final int GIF = 2;
	private static final String[] urls = new String[] { "http://www.hapke.ca/images/lame.jpg",
			"http://www.hapke.ca/images/business-time.gif" };
	private static final int[] imageTypes = new int[] { STATIC, GIF };

	private CampingBot bot;

	public PleasureModelCommand(CampingBot campingBot) {
		this.bot = campingBot;
	}

	/**
	 * FIXME only detects if @CampingBot is last entity
	 */
	@Override
	public TextCommandResult textCommand(CampingUser campingFromUser, List<MessageEntity> entities, Long chatId) {

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

				return new TextCommandResult(BotCommand.PleasureModel, url, false);
			} catch (TelegramApiException e) {
			}
		}
		return null;
	}

	@Override
	public boolean isMatch(String msg, List<MessageEntity> entities) {
		CampingUser targetUser = bot.findTarget(entities);
		CampingUser meCamping = bot.getMeCamping();
		return targetUser == meCamping && msg.contains(PLEASURE_MODEL) && msg.endsWith("?");
	}

}
