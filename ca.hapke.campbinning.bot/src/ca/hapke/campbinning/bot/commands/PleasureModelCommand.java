package ca.hapke.campbinning.bot.commands;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBot;
import ca.hapke.campbinning.bot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class PleasureModelCommand extends RespondWithImage {
	public static final String PLEASURE_MODEL = "pleasure model";

	private static final String[] urls = new String[] { "http://www.hapke.ca/images/lame.jpg",
			"http://www.hapke.ca/images/business-time.gif" };
	private static final int[] imageTypes = new int[] { STATIC, GIF };

	public PleasureModelCommand(CampingBot bot) {
		super(bot, urls, imageTypes);
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
	protected BotCommand getCommandType() {
		return BotCommand.PleasureModel;
	}

}
