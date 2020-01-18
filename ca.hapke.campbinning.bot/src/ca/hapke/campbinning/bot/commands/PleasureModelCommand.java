package ca.hapke.campbinning.bot.commands;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBot;
import ca.hapke.campbinning.bot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class PleasureModelCommand implements TextCommand {

	public static final String PLEASURE_MODEL = "pleasure model";

	protected CampingBot bot;
	private RespondWithImage images;

	public PleasureModelCommand(CampingBot bot) {
		this.bot = bot;
		images = new RespondWithImage(bot);
		images.add(new ImageLink("http://www.hapke.ca/images/lame.jpg", ImageLink.STATIC));
		images.add(new ImageLink("http://www.hapke.ca/images/business-time.gif", ImageLink.GIF));
	}

	/**
	 * FIXME only detects if @CampingBot is last entity
	 */
	@Override
	public boolean isMatch(String msg, List<MessageEntity> entities) {
		CampingUser targetUser = bot.findTarget(entities);
		CampingUser meCamping = bot.getMeCamping();
		return targetUser == meCamping && msg.contains(PLEASURE_MODEL) && msg.endsWith("?");
	}

	@Override
	public TextCommandResult textCommand(CampingUser campingFromUser, List<MessageEntity> entities, Long chatId,
			Message message) {
		return images.sendImage(BotCommand.PleasureModel, chatId, null);
	}

}
