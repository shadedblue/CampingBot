package ca.hapke.campingbot.commands;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import ca.hapke.campingbot.api.CampingBotEngine;
import ca.hapke.campingbot.processors.BlotProcessor;
import ca.hapke.campingbot.processors.OverlayProcessor;
import ca.hapke.campingbot.response.fragments.CaseChoice;
import ca.hapke.campingbot.response.fragments.ResultFragment;
import ca.hapke.campingbot.response.fragments.TextFragment;
import ca.hapke.campingbot.response.fragments.TextStyle;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.util.CollectionUtil;
import ca.hapke.util.StringUtil;

/**
 * @author Nathan Hapke
 */
public class HypeJobDetails extends UpdatingMessageJobDetails {
	private final String hype;
	final CampingUser campingFromUser;
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

	private boolean isFailedHype;
	private int bailStep;
	private boolean shouldAbort = false;
	private List<ResultFragment> lastFrags;
	private int lastPct;
	private BlotProcessor blotter = new BlotProcessor(true, BlotProcessor.blotsAll);
	private OverlayProcessor overlayer = new OverlayProcessor(true);

	public HypeJobDetails(CampingUser campingFromUser, Long chatId, String hype, CampingBotEngine bot,
			List<String> dicks) {
		super(bot, HypeCommand.SlashHype, chatId);
		this.hype = hype;
		this.campingFromUser = campingFromUser;
		this.dicks = dicks;

		this.isFailedHype = hype == null;
		if (isFailedHype) {
			bailStep = (int) (EDIT_COUNT * Math.random());
		} else {
			bailStep = Integer.MAX_VALUE;
		}
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
			lastFrags = createNumbers(0);
			return attemptSend(campingFromUser, lastFrags);

		} else if (isFailedHype && step >= bailStep) {
			lastFrags = createFailure(step);
			boolean complete = attemptEdit(campingFromUser, lastFrags);
			if (complete)
				shouldAbort = true;
			return complete;
		} else if (step <= EDIT_COUNT) {
			lastFrags = createNumbers(step);
			return attemptEdit(campingFromUser, lastFrags);
		} else if (step == EDIT_COUNT + 1) {
			lastFrags = createText(hype, EDIT_COUNT, true);
			return attemptEdit(campingFromUser, lastFrags);
		}

		return false;
	}

	public List<ResultFragment> createNumbers(int step) {
		String txt;

		if (Math.random() < 0.1) {
			txt = CollectionUtil.getRandom(dicks);
		} else {
			txt = generateNumbersString();
		}

		return createText(txt, step, false);
	}

	private List<ResultFragment> createFailure(int step) {
		int size = lastFrags.size();
		List<ResultFragment> frags = new ArrayList<>(size);
		for (int i = 0; i < size - 1; i++) {
			ResultFragment frag = lastFrags.get(i);
			frags.add(frag);
		}
		ResultFragment end;
		end = lastFrags.get(size - 1);

		end = end.transform(blotter, true);
		overlayer.setMessage("404", "HYPE", "NOT", "FOUND");
		end = end.transform(overlayer, false);
		frags.add(end);
		return frags;
	}

	public List<ResultFragment> createText(String txt, int step, boolean finishing) {
		List<ResultFragment> frags = new ArrayList<>(5);

		double pct = ((double) step) / EDIT_COUNT;
		TextFragment progress = new TextFragment(createProgressBar(pct), CaseChoice.Upper, TextStyle.Preformatted);

		int deviation = 1 + (int) (6 * Math.random());
		int intPct = (int) (100 * pct);
		lastPct = intPct + deviation;
		if (!finishing && (lastPct >= 100 || Math.random() < 0.5)) {
			lastPct = intPct - deviation;
		}
		String title = " " + nf.format(lastPct) + "% HYPED ";

		frags.add(progress);
		frags.add(new TextFragment(createTitle(title), CaseChoice.Upper, TextStyle.Preformatted));
		progress = new TextFragment(createProgressBar(pct), CaseChoice.Upper, TextStyle.Preformatted);
		frags.add(progress);
//		if (finishing) {
//			
//		} else {
		frags.add(new TextFragment(txt, CaseChoice.Normal, TextStyle.Preformatted));
//		}

		return frags;
	}

	private static String createProgressBar(double pct) {
		StringBuilder sb = new StringBuilder(TITLE_BAR_WIDTH);
		int width = TITLE_BAR_WIDTH - 4;

		sb.append(CollectionUtil.getRandom(BlotProcessor.blotsPartial));
		sb.append(CollectionUtil.getRandom(BlotProcessor.blotsFade));

		int fullBoxes = (int) (width * pct);

		int j = 0;
		while (j < fullBoxes) {
			sb.append(CollectionUtil.getRandom(BlotProcessor.blotsFull));
			j++;
		}

		if (j < width) {
			sb.append(CollectionUtil.getRandom(BlotProcessor.blotsFade));
			j++;
		}

		for (int k = 0; k < 2 && j < width; k++) {
			sb.append(CollectionUtil.getRandom(BlotProcessor.blotsPartial));
			j++;
		}

		while (j < width) {
			sb.append(" ");
			j++;
		}

		sb.append(CollectionUtil.getRandom(BlotProcessor.blotsFade));
		sb.append(CollectionUtil.getRandom(BlotProcessor.blotsPartial));

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
			sb.append(CollectionUtil.getRandom(BlotProcessor.blotsPartial));
			i++;
		}
		if (i < before) {
			sb.append(CollectionUtil.getRandom(BlotProcessor.blotsFade));
			i++;
		}
		if (i < before) {
			sb.append(CollectionUtil.getRandom(BlotProcessor.blotsFull));
		}

		sb.append(title);

		i = 0;
		if (i < after) {
			sb.append(CollectionUtil.getRandom(BlotProcessor.blotsFull));
			i++;
		}
		if (i < after) {
			sb.append(CollectionUtil.getRandom(BlotProcessor.blotsFade));
			i++;
		}
		while (i < after) {
			sb.append(CollectionUtil.getRandom(BlotProcessor.blotsPartial));
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
			result += StringUtil.join(parts, " ");

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

	@Override
	public boolean shouldAbort() {
		return shouldAbort;
	}

}
