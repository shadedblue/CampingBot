package ca.hapke.campingbot.commands;

import java.text.NumberFormat;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;

import ca.hapke.campingbot.api.CampingBotEngine;
import ca.hapke.campingbot.category.CategoriedStrings;
import ca.hapke.campingbot.category.HasCategories;
import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.SlashCommand;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.NoopCommandResult;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.users.CampingUserMonitor;
import ca.hapke.campingbot.util.StagedJob;

/**
 * @author Nathan Hapke
 */
public class HypeCommand extends AbstractCommand implements HasCategories<String>, SlashCommand {
	private static final String HYPE_CONTAINER = "Hype";
	public static final String HYPE_CATEGORY = "hype";
	public static final String DICK_CATEGORY = "dick";
	public static final SlashCommandType SlashHype = new SlashCommandType(HYPE_CONTAINER, "hype",
			BotCommandIds.SILLY_RESPONSE | BotCommandIds.TEXT | BotCommandIds.USE);
	private static final SlashCommandType[] SLASH_COMMANDS = new SlashCommandType[] { SlashHype };

	@Override
	public SlashCommandType[] getSlashCommandsToRespondTo() {
		return SLASH_COMMANDS;
	}

	private CategoriedStrings categories;
	private boolean shouldSave = false;

//	private List<String> hypes;
	private CampingBotEngine bot;
//	private List<String> dicks;
	private NumberFormat nf;

	public HypeCommand(CampingBotEngine bot) {
		this.bot = bot;
		categories = new CategoriedStrings(HYPE_CATEGORY, DICK_CATEGORY);
//		dicks = categories.getList(DICK_CATEGORY);
//		hypes = categories.getList(HYPE_CATEGORY);

		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(0);
	}

	private StagedJob<HypeJobDetails> instance;

	public void softStart(HypeJobDetails details) {
		if (instance == null || instance.isComplete()) {
			instance = new StagedJob<HypeJobDetails>(details);
			instance.start();
		} else {
			instance.add(details);
		}
	}

	@Override
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, Long chatId,
			CampingUser campingFromUser) {
		String hype = null;
		String incoming = message.getText();
		if (incoming.contains(" ")) {
			String searchTerm = incoming.substring(incoming.indexOf(' ') + 1);
			hype = categories.search(HYPE_CATEGORY, searchTerm);
//					CollectionUtil.search(searchTerm, hypes);
		} else {
			Message replyToMessage = message.getReplyToMessage();
			if (replyToMessage != null) {
				CampingUser target = CampingUserMonitor.getInstance().getUser(replyToMessage.getFrom());
				String searchTerm = '(' + target.getInitials() + ')';
				hype = categories.search(HYPE_CATEGORY, searchTerm);
			} else {
				hype = categories.getRandom(HYPE_CATEGORY);
			}
		}
		HypeJobDetails details = new HypeJobDetails(campingFromUser, chatId, hype, bot, categories);
		softStart(details);
		return new NoopCommandResult(SlashHype);
	}

	@Override
	public List<String> getCategoryNames() {
		return categories.getCategoryNames();
	}

	@Override
	public void addItem(String category, String value) {
		if (categories.put(category, value))
			shouldSave = true;
	}

	@Override
	public String getContainerName() {
		return HYPE_CONTAINER;
	}

	@Override
	public String getCommandName() {
		return HYPE_CONTAINER;
	}

	public void setHypesAndDicks(List<String> h, List<String> d) {
		if (h != null)
			categories.putAll(HYPE_CATEGORY, h);
		if (d != null)
			categories.putAll(DICK_CATEGORY, d);
	}

	@Override
	public int getSize(String s) {
		return categories.getSize(s);
	}

}
