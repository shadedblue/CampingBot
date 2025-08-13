package ca.hapke.campingbot.commands;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;

import ca.hapke.campingbot.api.CampingBotEngine;
import ca.hapke.campingbot.category.CategoriedStringsPersisted;
import ca.hapke.campingbot.category.HasCategories;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.SlashCommand;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.NoopCommandResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.users.CampingUserMonitor;
import ca.hapke.campingbot.util.StagedJob;

/**
 * @author Nathan Hapke
 */
public class HypeCommand extends SlashCommand implements HasCategories<String> {
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

	private CategoriedStringsPersisted categories;
	private CampingBotEngine bot;

	public HypeCommand(CampingBotEngine bot) {
		this.bot = bot;
		categories = new CategoriedStringsPersisted(HYPE_CONTAINER, HYPE_CATEGORY, DICK_CATEGORY);
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
		categories.put(category, value);
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

	@Override
	public String provideUiStatus() {
		String out;
		if (instance == null) {
			out = "No active hype";
		} else {
			out = instance.toString();
		}
		return out;
	}

	@Override
	public void appendHelpText(SlashCommandType cmd, TextCommandResult result) {
		result.add("You can also search for a particular string in the hypes.\n");
		result.add("If you reply to a message, it will search for a hype from that user.");
	}
}
