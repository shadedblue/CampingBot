package ca.hapke.campingbot.commands;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import ca.hapke.campingbot.BotChoicePriority;
import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.category.CategoriedItems;
import ca.hapke.campingbot.channels.CampingChat;
import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.ResponseCommandType;
import ca.hapke.campingbot.commands.api.TextCommand;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.ImageCommandResult;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.util.ImageLink;
import ca.hapke.util.CollectionUtil;

/**
 * @author Nathan Hapke
 */
public class PleasureModelCommand extends AbstractCommand implements TextCommand {
	public static final ResponseCommandType PleasureModelCommand = new ResponseCommandType("PleasureModel",
			BotCommandIds.PLEASURE | BotCommandIds.GIF);

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

	@Override
	public boolean isMatch(String msg, Message message) {
		CampingUser targetUser = bot.findTarget(message, true, true, BotChoicePriority.Only);
		return targetUser != null && msg.toLowerCase().contains(PLEASURE_MODEL);
	}

	@Override
	public CommandResult textCommand(CampingUser campingFromUser, List<MessageEntity> entities, CampingChat chat,
			Message message) {
		ImageLink img = CollectionUtil.getRandom(images);
		return new ImageCommandResult(PleasureModelCommand, img);
	}

	@Override
	public String getCommandName() {
		return PLEASURE_MODEL;
	}

}
