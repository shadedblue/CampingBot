package ca.hapke.campbinning.bot.commands;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import ca.hapke.campbinning.bot.CampingSerializable;
import ca.hapke.campbinning.bot.CampingUtil;
import ca.hapke.campbinning.bot.Resources;
import ca.hapke.campbinning.bot.category.CategoriedItems;
import ca.hapke.campbinning.bot.category.HasCategories;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.users.CampingUserMonitor;
import ca.hapke.campbinning.bot.xml.OutputFormatter;

/**
 * @author Nathan Hapke
 */
public class CountdownGenerator extends CampingSerializable implements HasCategories {

	private static final String HYPE_CATEGORY = "hype";
	// Month is 0-indexed for some stupid inconsistent reason...
	private ZonedDateTime countdownTarget = new GregorianCalendar(2019, 11, 27, 14, 10, 00).toZonedDateTime();
	private List<String> hypes;
	private Resources res;
	private MbiyfCommand ballsCommand;
	private ZoneId zone = TimeZone.getDefault().toZoneId();
	private CategoriedItems<String> categories;

	public CountdownGenerator(Resources res, MbiyfCommand ballsCommand) {
		this.res = res;
		this.ballsCommand = ballsCommand;
		categories = new CategoriedItems<String>(HYPE_CATEGORY);
		hypes = categories.getList(HYPE_CATEGORY);
	}

	@Override
	public String getContainerName() {
		return "Countdown";
	}

	public void setHypes(List<String> h) {
		categories.putAll(HYPE_CATEGORY, h);
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

	// public List<String> getHypes() {
	// return hypes;
	// }

	public String countdownCommand(CampingUserMonitor userMonitor, Long chatId) {
		GregorianCalendar now = new GregorianCalendar();
		StringBuilder sb = new StringBuilder();

		Temporal targetEvent;
		if (countdownTarget == null || now.before(countdownTarget)) {
			// CampingUser rtv = userMonitor.monitor(558638791, null, null,
			// null);
			// CampingUser andrew = userMonitor.monitor(642767839, null, null,
			// null);
			CampingUser jamieson = userMonitor.monitor(708570894, null, null, null);
			CampingUser target = jamieson;

			sb.append("PARTY AT ");
			sb.append(target.getDisplayName().toUpperCase());

			sb.append("'S HOUSE COUNTDOWN\n");

			targetEvent = countdownTarget;
		} else {
			sb.append("MBIY\\[F]RIDAY COUNTDOWN\n");
			targetEvent = ZonedDateTime.ofInstant(ballsCommand.getNearestFutureEnablement(), zone);
		}

		for (int i = 0; i < 5; i++) {
			sb.append(res.getRandomFace());
		}
		sb.append("\n");
		sb.append(res.getRandomBall());
		sb.append(" ");
		Duration d = Duration.between(now.toZonedDateTime(), targetEvent);

		long daysPart = d.toDaysPart();
		if (daysPart > 0)
			addTime(sb, daysPart, "day");
		addTime(sb, d.toHoursPart(), "hour");
		if (daysPart == 0)
			addTime(sb, d.toMinutesPart(), "minute");

		sb.append("\n");

		sb.append(res.getRandomBall());
		sb.append(" ");
		sb.append(CampingUtil.getRandom(hypes));
		String out = sb.toString().trim();
		return out;
	}

	private void addTime(StringBuilder sb, long amt, String unit) {
		if (amt > 0) {
			sb.append(amt);

			sb.append(" ");
			sb.append(unit);
			if (amt > 1)
				sb.append("s");
			sb.append(" ");
		}
	}

	@Override
	public void getXml(OutputFormatter of) {
		String tag = "countdown";
		of.start(tag);
		of.tagAndValue(HYPE_CATEGORY, hypes);
		of.finish(tag);
	}

}
