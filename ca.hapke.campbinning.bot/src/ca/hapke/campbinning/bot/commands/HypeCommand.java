package ca.hapke.campbinning.bot.commands;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.BotConstants;
import ca.hapke.campbinning.bot.CampingBotEngine;
import ca.hapke.campbinning.bot.category.CategoriedItems;
import ca.hapke.campbinning.bot.category.HasCategories;
import ca.hapke.campbinning.bot.channels.CampingChat;
import ca.hapke.campbinning.bot.channels.CampingChatManager;
import ca.hapke.campbinning.bot.response.CommandResult;
import ca.hapke.campbinning.bot.response.EditTextCommandResult;
import ca.hapke.campbinning.bot.response.SendResult;
import ca.hapke.campbinning.bot.response.TextCommandResult;
import ca.hapke.campbinning.bot.response.fragments.CaseChoice;
import ca.hapke.campbinning.bot.response.fragments.ResultFragment;
import ca.hapke.campbinning.bot.response.fragments.TextFragment;
import ca.hapke.campbinning.bot.response.fragments.TextStyle;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.util.CampingUtil;

/**
 * @author Nathan Hapke
 */
public class HypeCommand extends AbstractCommand implements HasCategories<String>, SlashCommand {
	private static final BotCommand[] SLASH_COMMANDS = new BotCommand[] { BotCommand.Hype };

	@Override
	public BotCommand[] getSlashCommandsToRespondTo() {
		return SLASH_COMMANDS;
	}

	private static final Character[] blotsFull = new Character[] { '▓', '█', '█' };
	private static final Character[] blotsFade = new Character[] { '░', '▙', '▟', '▛', '▜', '▞', '▚' };
	private static final Character[] blotsPartial = new Character[] { '▔', '▖', '▗', '▘', '▝' };

	private class EditingMessageThread extends Thread {
		private static final int EDIT_DELAY = 2000;
		private TextCommandResult target;
		private CampingUser campingFromUser;

		public EditingMessageThread(TextCommandResult result, CampingUser campingFromUser) {
			this.target = result;
			this.campingFromUser = campingFromUser;
		}

		@Override
		public void run() {
			int edits = 1;
			int attempts = 1;
			Message message = null;
			Long chatId = null;
			CampingChat chat = null;
			while (edits <= EDIT_COUNT && attempts <= EDIT_COUNT) {
				try {
					Thread.sleep(EDIT_DELAY);
				} catch (InterruptedException e) {
				}
				if (message == null) {
					SendResult result = target.getResult();
					if (result != null) {
						message = result.outgoingMsg;
						chatId = message.getChatId();
						chat = chatManager.get(chatId);
					}
					attempts++;
				}
				if (message != null) {
					List<ResultFragment> frags = createNumbers(edits);
					sendEdit(message, chat, frags);
					edits++;
				}
			}
			if (!BotConstants.DEBUG) {
				String txt = CampingUtil.getRandom(hypes);
				List<ResultFragment> frags = createText(txt, EDIT_COUNT, true);
				sendEdit(message, chat, frags);
			}
		}

		public void sendEdit(Message message, CampingChat chat, List<ResultFragment> frags) {
			BotCommand cmd = BotCommand.Hype;
			Integer telegramId = message.getMessageId();
			Integer eventTime = message.getDate();

			EditTextCommandResult edit = new EditTextCommandResult(cmd, message, frags);
			try {
				SendResult result = edit.send(bot, chat.chatId);
				bot.logSendResult(telegramId, campingFromUser, eventTime, chat, cmd, edit, result);
			} catch (TelegramApiException e) {
				bot.logFailure(telegramId, campingFromUser, eventTime, chat, cmd, e);
			}
		}

	}

	private static final int DIGITS = 6;
	private static final int LINES = 8;
	private static final int QTY = 3;
	private static final int TITLE_BAR_WIDTH = (DIGITS + 1) * QTY - 1;
	private static final int EDIT_COUNT = BotConstants.DEBUG ? 0 : 5;

	private static final String HYPE_CONTAINER = "Hype";
	private static final String DICKS_CATEGORY = "Dicks";
	private CategoriedItems<String> categories;

	private List<String> hypes;
	private CampingBotEngine bot;
	private CampingChatManager chatManager;
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
		chatManager = CampingChatManager.getInstance(bot);
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

	@Override
	public CommandResult respondToSlashCommand(BotCommand command, Message message, Long chatId,
			CampingUser campingFromUser) {
		List<ResultFragment> frags = createNumbers(0);
		TextCommandResult result = new TextCommandResult(BotCommand.Hype, frags);
		if (!BotConstants.DEBUG)
			new EditingMessageThread(result, campingFromUser).start();
		return result;
	}

	public List<ResultFragment> createNumbers(int i) {
		String txt;
		if (BotConstants.DEBUG) {
			txt = shakeThatAss;
		} else {
			if (Math.random() < 0.1) {
				txt = CampingUtil.getRandom(dicks);
			} else {
				txt = generateNumbersString();
			}
		}

		return createText(txt, i, false);
	}

	public List<ResultFragment> createText(String txt, int i, boolean finishing) {
		List<ResultFragment> frags = new ArrayList<>(5);

		double pct = ((double) i) / EDIT_COUNT;
		TextFragment progress = new TextFragment(createProgressBar(i, pct), CaseChoice.Upper, TextStyle.Preformatted);

		int deviation = (int) (7 * Math.random());
		int intPct = (int) (100 * pct);
		int nPlus = intPct + deviation;
		double n;
		if (finishing) {
			// must be over 100%
			n = nPlus;
		} else if (nPlus >= 100 || Math.random() < 0.5) {
			n = intPct - deviation;
		} else {
			n = nPlus;
		}
		String title = " " + nf.format(n) + "% HYPED ";

		frags.add(progress);
		frags.add(new TextFragment(createTitle(title, false), CaseChoice.Upper, TextStyle.Preformatted));
		progress = new TextFragment(createProgressBar(i, pct), CaseChoice.Upper, TextStyle.Preformatted);
		frags.add(progress);
		frags.add(new TextFragment(txt, CaseChoice.Normal, TextStyle.Preformatted));
//		frags.add(progress);

		return frags;
	}

	private String createProgressBar(int i, double pct) {
		StringBuilder sb = new StringBuilder(TITLE_BAR_WIDTH);
		int width = TITLE_BAR_WIDTH - 4;
//		sb.append("❰");
		sb.append(CampingUtil.getRandom(blotsPartial));
		sb.append(CampingUtil.getRandom(blotsFade));

		int fullBoxes = (int) (width * pct);

		int j = 0;
		while (j < fullBoxes) {
			sb.append(CampingUtil.getRandom(blotsFull));
			j++;
		}

		for (int k = 0; k < 1 && j < width; k++) {
			sb.append(CampingUtil.getRandom(blotsFade));
			j++;
		}

		for (int k = 0; k < 2 && j < width; k++) {
			sb.append(CampingUtil.getRandom(blotsPartial));
			j++;
		}

		while (j < width) {
			sb.append(" ");
			j++;
		}
//		sb.append("❱")
		sb.append(CampingUtil.getRandom(blotsFade));
		sb.append(CampingUtil.getRandom(blotsPartial));
		return createTitle(sb, false);
	}

	public String createTitle(CharSequence title, boolean bounce) {
		StringBuilder sb = new StringBuilder(TITLE_BAR_WIDTH);
		int dashQty = TITLE_BAR_WIDTH - title.length();
		int before, after;

		if (!bounce) {
			before = dashQty / 2;
		} else {
			int loc = (int) ((dashQty - 2) * Math.random());
			before = 1 + loc;
		}
		after = dashQty - before;

		int i = 0;
		for (; i < before - 2; i++) {
			sb.append(CampingUtil.getRandom(blotsPartial));
		}
		if (i < before) {
			sb.append(CampingUtil.getRandom(blotsFade));
			i++;
		}
		if (i < before) {
			sb.append(CampingUtil.getRandom(blotsFull));
		}

		sb.append(title);

		i = 0;
		if (i < after) {
			sb.append(CampingUtil.getRandom(blotsFull));
			i++;
		}
		if (i < after) {
			sb.append(CampingUtil.getRandom(blotsFade));
			i++;
		}
		for (; i < after; i++) {
			sb.append(CampingUtil.getRandom(blotsPartial));
		}
		return sb.toString();
	}

	private String generateNumbersString() {
		String result = "";
		for (int i = 1; i <= LINES; i++) {
			String[] parts = new String[QTY];
			for (int j = 1; j <= QTY; j++) {
				parts[j - 1] = Long.toHexString(generateLong()).toUpperCase();
			}
			result += CampingUtil.join(parts, " ");

			if (i < LINES)
				result += "\n";
		}
		return result;
	}

	public long generateLong() {
		long l = 0;
		do {
			l = (long) (Math.pow(16, DIGITS) * Math.random());
		} while (l < Math.pow(16, DIGITS - 1) - 1);
		return l;
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
}
