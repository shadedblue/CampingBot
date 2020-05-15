package ca.hapke.campbinning.bot.commands;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBot;
import ca.hapke.campbinning.bot.category.CategoriedItems;
import ca.hapke.campbinning.bot.commands.response.CommandResult;
import ca.hapke.campbinning.bot.commands.response.ImageCommandResult;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.util.CampingUtil;

/**
 * @author Nathan Hapke
 */
public class PleasureModelCommand implements TextCommand {

	public static final String PLEASURE_MODEL = "pleasure model";

	protected CampingBot bot;
	private CategoriedItems<ImageLink> categories;
	private List<ImageLink> images;

	public PleasureModelCommand(CampingBot bot) {
		this.bot = bot;
		categories = new CategoriedItems<ImageLink>(PLEASURE_MODEL);
		images = categories.getList(PLEASURE_MODEL);
		images.add(new ImageLink("http://www.hapke.ca/images/lame.jpg", ImageLink.STATIC));
		images.add(new ImageLink("http://www.hapke.ca/images/business-time.gif", ImageLink.GIF));
	}

	/**
	 * FIXME only detects if @CampingBot is last entity
	 */
	@Override
	public boolean isMatch(String msg, Message message) {
		CampingUser targetUser = bot.findTarget(message);
		CampingUser meCamping = bot.getMeCamping();
		return targetUser == meCamping && msg.contains(PLEASURE_MODEL) && msg.endsWith("?");
	}

	@Override
	public CommandResult textCommand(CampingUser campingFromUser, List<MessageEntity> entities, Long chatId,
			Message message) {
		ImageLink img = CampingUtil.getRandom(images);
		return new ImageCommandResult(BotCommand.PleasureModel, img);
	}

}
