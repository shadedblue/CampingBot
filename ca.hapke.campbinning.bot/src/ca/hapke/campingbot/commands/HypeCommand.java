package ca.hapke.campingbot.commands;

import java.text.NumberFormat;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;

import ca.hapke.campingbot.api.CampingBotEngine;
import ca.hapke.campingbot.category.CategoriedItems;
import ca.hapke.campingbot.category.HasCategories;
import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.SlashCommand;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.NoopCommandResult;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.util.StagedJob;
import ca.hapke.util.CollectionUtil;

/**
 * @author Nathan Hapke
 */
public class HypeCommand extends AbstractCommand implements HasCategories<String>, SlashCommand {
	private static final String HYPE_CONTAINER = "Hype";
	public static final SlashCommandType SlashHype = new SlashCommandType(HYPE_CONTAINER, "hype",
			BotCommandIds.SILLY_RESPONSE | BotCommandIds.TEXT | BotCommandIds.USE);
	private static final SlashCommandType[] SLASH_COMMANDS = new SlashCommandType[] { SlashHype };

	@Override
	public SlashCommandType[] getSlashCommandsToRespondTo() {
		return SLASH_COMMANDS;
	}

	private static final String DICKS_CATEGORY = "Dicks";
	private CategoriedItems<String> categories;

	private List<String> hypes;
	private CampingBotEngine bot;
	private List<String> dicks;
	private NumberFormat nf;

	private static final String goatse = "      ,-----,\r\n" + "    _// __ \\\\\\\\___\r\n" + " C___)/    \\\\(___>\r\n"
			+ "C____)      (____>\r\n" + "C____)\\\\____/(____>\r\n" + " C___)      (___>\r\n" + "     \\\\_____//\r\n"
			+ "      `-----'";
	private static final String randoDicks = "2E3D4A DC8E61 F225EB\r\n" + "11D642 8===IB 4A220C\r\n"
			+ "525991 15E5CD E(oYo)\r\n" + "8===IB BDD041 BS).(1\r\n" + "D346D3 D3B1FF C( Y )\r\n"
			+ "7BE504 8===IB 1A1C95\r\n" + "8F1AA8 2F63A6 2C2377\r\n" + "4227E2 CA505D E4C23D";
	private static final String penisText = "                  _     \r\n" + "                 (_)    \r\n"
			+ " _ __   ___ _ __  _ ___ \r\n" + "| '_ \\\\ / _ \\\\ '_ \\\\| / __|\r\n"
			+ "| |_) |  __/ | | | \\\\__ \\\\\r\n" + "| .__/ \\\\___|_| |_|_|___/\r\n" + "| |                     \r\n"
			+ "|_|                     ";
	private static final String f2069 = "11D64 ,-----, DC8E61\r\n" + "225B_//____\\\\\\\\___225B\r\n"
			+ "3C___)/    \\\\(___>2E3\r\n" + "C____)      (____>2E\r\n" + "C____)\\\\    /(____>2E\r\n"
			+ "7C___) `--' (___>2E3\r\n" + "7BE50\\\\\\\\_____//4A220C\r\n" + "2E3D4A`-----'42069EE";
	private static final String fu = " ████████  ███   ███\r\n" + "░███░░░░  ░███  ░███ \r\n"
			+ "░███      ░███  ░███ \r\n" + "░███████  ░███  ░███ \r\n" + "░███░░░   ░███  ░███ \r\n"
			+ "░███      ░███  ░███ \r\n" + "░███      ░░███████  \r\n" + "░░░        ░░░░░░░    ";
	private static final String shakeThatAss = "3*----*F05B00 B886B1\r\n" + "A|JUST|-----* 7515AD\r\n"
			+ "A*----|SHAKE| 95050B\r\n" + "E0B752*-----* 3E451C\r\n" + "97CF2A D*----*E54900\r\n"
			+ "A06CF8 8|THAT|4*---*\r\n" + "508536 E*----*2|ASS|\r\n" + "1CD7B1 B9F194 7*---*";

	public HypeCommand(CampingBotEngine bot, CountdownCommand cg) {
		this.bot = bot;
		categories = new CategoriedItems<String>(DICKS_CATEGORY);
		dicks = categories.getList(DICKS_CATEGORY);
		hypes = cg.getHypes();

		categories.put(DICKS_CATEGORY, goatse);
		categories.put(DICKS_CATEGORY, randoDicks);
		categories.put(DICKS_CATEGORY, penisText);
		categories.put(DICKS_CATEGORY, f2069);
		categories.put(DICKS_CATEGORY, fu);
		categories.put(DICKS_CATEGORY, shakeThatAss);

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
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, Long chatId,
			CampingUser campingFromUser) {
		String hype = null;
		String incoming = message.getText();
		if (incoming.contains(" ")) {
			String searchTerm = incoming.substring(incoming.indexOf(' '));
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

	@Override
	public List<String> getCategory(String name) {
		return categories.getList(name);
	}
}
