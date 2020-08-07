package ca.hapke.campbinning.bot.commands;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBot;
import ca.hapke.campbinning.bot.category.CategoriedItems;
import ca.hapke.campbinning.bot.category.HasCategories;
import ca.hapke.campbinning.bot.response.CommandResult;
import ca.hapke.campbinning.bot.response.ImageCommandResult;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.util.CampingUtil;
import ca.hapke.campbinning.bot.util.ImageLink;

/**
 * @author Nathan Hapke
 */
public class IunnoCommand extends AbstractCommand implements TextCommand, HasCategories<ImageLink>, SlashCommand {
	private static final BotCommand[] SLASH_COMMANDS = new BotCommand[] { BotCommand.IunnoGoogleIt };
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
		ImageCommandResult result = new ImageCommandResult(BotCommand.IunnoGoogleIt, img);
		result.setReplyToOriginalMessageIfPossible(message);
		return result;
	}

	@Override
	public boolean isMatch(String msg, Message message) {
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

	@Override
	public String getCommandName() {
		return IUNNO_CATEGORY;
	}

	@Override
	public BotCommand[] getSlashCommandsToRespondTo() {
		return SLASH_COMMANDS;
	}

	@Override
	public CommandResult respondToSlashCommand(BotCommand command, Message message, Long chatId,
			CampingUser campingFromUser) {
		return textCommand(campingFromUser, message.getEntities(), chatId, message);
	}
}
