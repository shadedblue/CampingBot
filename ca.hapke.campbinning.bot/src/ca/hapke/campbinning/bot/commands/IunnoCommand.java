package ca.hapke.campbinning.bot.commands;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBot;
import ca.hapke.campbinning.bot.category.CategoriedItems;
import ca.hapke.campbinning.bot.category.HasCategories;
import ca.hapke.campbinning.bot.commands.response.CommandResult;
import ca.hapke.campbinning.bot.commands.response.ImageCommandResult;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.util.CampingUtil;

/**
 * @author Nathan Hapke
 */
public class IunnoCommand implements TextCommand, HasCategories<ImageLink> {
	private static final String IUNNO_CATEGORY = "Iunno";
	protected CampingBot bot;
	private CategoriedItems<ImageLink> categories;
	private List<ImageLink> images;

	public IunnoCommand(CampingBot bot) {
		this.bot = bot;
		categories = new CategoriedItems<ImageLink>(IUNNO_CATEGORY);
		categories.put(IUNNO_CATEGORY, new ImageLink("http://www.hapke.ca/images/iunno.gif", ImageLink.GIF));
		images = categories.getList(IUNNO_CATEGORY);
	}

	@Override
	public CommandResult textCommand(CampingUser campingFromUser, List<MessageEntity> entities, Long chatId,
			Message message) {
		ImageLink img = CampingUtil.getRandom(images);
		return new ImageCommandResult(BotCommand.IunnoGoogleIt, img);
	}

	@Override
	public boolean isMatch(String msg, List<MessageEntity> entities) {
		String msgLower = msg.toLowerCase().trim();
		return msgLower.endsWith("/" + BotCommand.IunnoGoogleIt.command);
	}

	@Override
	public List<String> getCategoryNames() {
		return categories.getCategoryNames();
	}

	@Override
	public void addItem(String category, ImageLink value) {
		categories.put(category, value);
	}

	@Override
	public String getContainerName() {
		return IUNNO_CATEGORY;
	}

}
