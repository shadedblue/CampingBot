package ca.hapke.campingbot.commands;

import java.text.NumberFormat;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.api.CampingBotEngine;
import ca.hapke.campingbot.api.CampingSerializable;
import ca.hapke.campingbot.category.CategoriedItems;
import ca.hapke.campingbot.category.HasCategories;
import ca.hapke.campingbot.channels.CampingChat;
import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.SlashCommand;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.NoopCommandResult;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.util.StagedJob;
import ca.hapke.campingbot.xml.OutputFormatter;
import ca.hapke.util.CollectionUtil;

/**
 * @author Nathan Hapke
 */
public class HypeCommand extends AbstractCommand implements CampingSerializable, HasCategories<String>, SlashCommand {
	private static final String HYPE_CONTAINER = "Hype";
	public static final String HYPE_CATEGORY = "hype";
	private static final String DICK_CATEGORY = "dick";
	public static final SlashCommandType SlashHype = new SlashCommandType(HYPE_CONTAINER, "hype",
			BotCommandIds.SILLY_RESPONSE | BotCommandIds.TEXT | BotCommandIds.USE);
	private static final SlashCommandType[] SLASH_COMMANDS = new SlashCommandType[] { SlashHype };

	@Override
	public SlashCommandType[] getSlashCommandsToRespondTo() {
		return SLASH_COMMANDS;
	}

	private CategoriedItems<String> categories;
	private boolean shouldSave = false;

	private List<String> hypes;
	private CampingBotEngine bot;
	private List<String> dicks;
	private NumberFormat nf;

	public HypeCommand(CampingBotEngine bot) {
		this.bot = bot;
		categories = new CategoriedItems<String>(HYPE_CATEGORY, DICK_CATEGORY);
		dicks = categories.getList(DICK_CATEGORY);
		hypes = categories.getList(HYPE_CATEGORY);

		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(0);
	}

	public class HypeJob extends StagedJob<HypeJobDetails> {
		public HypeJob(HypeJobDetails first) {
			super(first);
		}
	}

	private HypeJob instance;

	public void softStart(HypeJobDetails details) {
		if (instance == null || instance.isComplete()) {
			instance = new HypeJob(details);
			instance.start();
		} else {
			instance.add(details);
		}
	}

	@Override
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, CampingChat chat,
			CampingUser campingFromUser) throws TelegramApiException {
		Long chatId = chat.chatId;
		String hype = null;
		String incoming = message.getText();
		if (incoming.contains(" ")) {
			String searchTerm = incoming.substring(incoming.indexOf(' ') + 1);
			hype = CollectionUtil.search(searchTerm, hypes);
		} else {
			hype = CollectionUtil.getRandom(hypes);
		}
		HypeJobDetails details = new HypeJobDetails(campingFromUser, chatId, hype, bot, dicks);
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

	@Override
	public List<String> getCategory(String name) {
		return categories.getList(name);
	}

	public void setHypesAndDicks(List<String> h, List<String> d) {
		if (h != null)
			categories.putAll(HYPE_CATEGORY, h);
		if (d != null)
			categories.putAll(DICK_CATEGORY, d);
	}

	protected List<String> getHypes() {
		return hypes;
	}

	@Override
	public boolean shouldSave() {
		return shouldSave;
	}

	@Override
	public void getXml(OutputFormatter of) {
		String tag = "countdown";
		of.start(tag);
		of.tagAndValue(HYPE_CATEGORY, hypes);
		of.tagAndValue(DICK_CATEGORY, dicks);
		of.finish(tag);

		shouldSave = false;
	}
}
