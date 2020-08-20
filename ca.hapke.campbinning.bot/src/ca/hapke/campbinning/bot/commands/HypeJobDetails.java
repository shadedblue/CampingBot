package ca.hapke.campbinning.bot.commands;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBotEngine;
import ca.hapke.campbinning.bot.response.EditTextCommandResult;
import ca.hapke.campbinning.bot.response.SendResult;
import ca.hapke.campbinning.bot.response.TextCommandResult;
import ca.hapke.campbinning.bot.response.fragments.CaseChoice;
import ca.hapke.campbinning.bot.response.fragments.ResultFragment;
import ca.hapke.campbinning.bot.response.fragments.TextFragment;
import ca.hapke.campbinning.bot.response.fragments.TextStyle;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.util.CampingUtil;
import ca.hapke.campbinning.bot.util.JobDetails;

/**
 * @author Nathan Hapke
 */
public class HypeJobDetails implements JobDetails {
	private final String hype;
	private final CampingUser campingFromUser;
	private final Long chatId;

	private final CampingBotEngine bot;
	private final List<String> dicks;

	private final static NumberFormat nf;
	static {
		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(0);
	}
	private static final int DIGITS = 6;
	private static final int LINES = 8;
	private static final int QTY = 3;
	private static final int TITLE_BAR_WIDTH = (DIGITS + 1) * QTY - 1;
	private static final int EDIT_COUNT = 5;

	private static final Character[] blotsFull = new Character[] { '▓', '█', '█' };
	private static final Character[] blotsFade = new Character[] { '░', '▙', '▟', '▛', '▜', '▞', '▚' };
	private static final Character[] blotsPartial = new Character[] { '▔', '▖', '▗', '▘', '▝' };

	private Message targetMessage;

	public HypeJobDetails(CampingUser campingFromUser, Long chatId, String hype, CampingBotEngine bot,
			List<String> dicks) {
		this.hype = hype;
		this.campingFromUser = campingFromUser;
		this.chatId = chatId;
		this.bot = bot;
		this.dicks = dicks;
	}

	@Override
	public int getNumSteps() {
		return 2 + EDIT_COUNT;
	}

	@Override
	public int getNumAttempts(int step) {
		return 5;
	}

	@Override
	public boolean isRequireCompletion(int step) {
		return step == 0 || step == 1 + EDIT_COUNT;
	}

	@Override
	public int getDelay(int step) {
		return 1750;
	}

	@Override
	public boolean doStep(int step, int attempt) {
		if (step == 0) {
			List<ResultFragment> frags = createNumbers(0);
			TextCommandResult result = new TextCommandResult(BotCommand.Hype, frags);

			SendResult sendResult;
			try {
				sendResult = result.send(bot, chatId);
				targetMessage = sendResult.outgoingMsg;
				bot.logSendResult(targetMessage.getMessageId(), campingFromUser, chatId, BotCommand.Hype, result,
						sendResult);
				return true;
			} catch (TelegramApiException e) {
				bot.logFailure(targetMessage.getMessageId(), campingFromUser, chatId, BotCommand.Hype, e);
				return false;
			}
		} else if (step <= EDIT_COUNT) {
			List<ResultFragment> frags = createNumbers(step);
			return attemptEdit(frags);
		} else if (step == EDIT_COUNT + 1) {
			List<ResultFragment> frags = createText(hype, EDIT_COUNT, true);
			return attemptEdit(frags);
		}

		return false;
	}

	private boolean attemptEdit(List<ResultFragment> frags) {
		Integer telegramId = targetMessage.getMessageId();

		EditTextCommandResult edit = new EditTextCommandResult(BotCommand.Hype, targetMessage, frags);
		SendResult result;
		try {
			result = edit.send(bot, chatId);
			bot.logSendResult(telegramId, campingFromUser, chatId, BotCommand.Hype, edit, result);
			return true;
		} catch (TelegramApiException e) {
			bot.logFailure(telegramId, campingFromUser, chatId, BotCommand.Hype, e);
			return false;
		}
	}

	public List<ResultFragment> createNumbers(int step) {
		String txt;

		if (Math.random() < 0.1) {
			txt = CampingUtil.getRandom(dicks);
		} else {
			txt = generateNumbersString();
		}

		return createText(txt, step, false);
	}

	public List<ResultFragment> createText(String txt, int step, boolean finishing) {
		List<ResultFragment> frags = new ArrayList<>(5);

		double pct = ((double) step) / EDIT_COUNT;
		TextFragment progress = new TextFragment(createProgressBar(pct), CaseChoice.Upper, TextStyle.Preformatted);

		int deviation = 1 + (int) (6 * Math.random());
		int intPct = (int) (100 * pct);
		int n = intPct + deviation;
		if (!finishing && (n >= 100 || Math.random() < 0.5)) {
			n = intPct - deviation;
		}
		String title = " " + nf.format(n) + "% HYPED ";

		frags.add(progress);
		frags.add(new TextFragment(createTitle(title), CaseChoice.Upper, TextStyle.Preformatted));
		progress = new TextFragment(createProgressBar(pct), CaseChoice.Upper, TextStyle.Preformatted);
		frags.add(progress);
		frags.add(new TextFragment(txt, CaseChoice.Normal, finishing ? TextStyle.Normal : TextStyle.Preformatted));

		return frags;
	}

	private static String createProgressBar(double pct) {
		StringBuilder sb = new StringBuilder(TITLE_BAR_WIDTH);
		int width = TITLE_BAR_WIDTH - 4;

		sb.append(CampingUtil.getRandom(blotsPartial));
		sb.append(CampingUtil.getRandom(blotsFade));

		int fullBoxes = (int) (width * pct);

		int j = 0;
		while (j < fullBoxes) {
			sb.append(CampingUtil.getRandom(blotsFull));
			j++;
		}

		if (j < width) {
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

		sb.append(CampingUtil.getRandom(blotsFade));
		sb.append(CampingUtil.getRandom(blotsPartial));

		return createTitle(sb);
	}

	public static String createTitle(CharSequence title) {
		StringBuilder sb = new StringBuilder(TITLE_BAR_WIDTH);
		int dashQty = TITLE_BAR_WIDTH - title.length();
		int before, after;

		before = dashQty / 2;
		after = dashQty - before;

		int i = 0;
		while (i < before - 2) {
			sb.append(CampingUtil.getRandom(blotsPartial));
			i++;
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
		while (i < after) {
			sb.append(CampingUtil.getRandom(blotsPartial));
			i++;
		}
		return sb.toString();
	}

	private static String generateNumbersString() {
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

	public static long generateLong() {
		long l = 0;
		do {
			l = (long) (Math.pow(16, DIGITS) * Math.random());
		} while (l < Math.pow(16, DIGITS - 1) - 1);
		return l;
	}
}
