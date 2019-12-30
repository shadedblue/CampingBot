package ca.hapke.campbinning.bot.commands;

import java.time.Duration;
import java.util.GregorianCalendar;
import java.util.List;

import ca.hapke.campbinning.bot.CampingSerializable;
import ca.hapke.campbinning.bot.CampingUtil;
import ca.hapke.campbinning.bot.Resources;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.users.CampingUserMonitor;
import ca.hapke.campbinning.bot.xml.OutputFormatter;

/**
 * @author Nathan Hapke
 */
public class CountdownGenerator extends CampingSerializable {

	// Month is 0-indexed for some stupid inconsistent reason...
	private GregorianCalendar countdownTarget = new GregorianCalendar(2019, 11, 27, 14, 10, 00);
	private List<String> hypes;
	private Resources res;

	public CountdownGenerator(Resources res) {
		this.res = res;
	}

	public void setHypes(List<String> h) {
		this.hypes = h;
	}

	public List<String> getHypes() {
		return hypes;
	}

	public String countdownCommand(CampingUserMonitor userMonitor, Long chatId) {
		StringBuilder sb = new StringBuilder();
		// CampingUser rtv = userMonitor.monitor(558638791, null, null, null);
		// CampingUser andrew = userMonitor.monitor(642767839, null, null,
		// null);
		CampingUser jamieson = userMonitor.monitor(708570894, null, null, null);
		CampingUser target = jamieson;

		sb.append("PARTY AT ");
		sb.append(target.getDisplayName().toUpperCase());

		sb.append("'S HOUSE COUNTDOWN\n");
//		for (int i = 0; i < 15; i++) {
//			sb.append("-");
//		}
		for (int i = 0; i < 5; i++) {
			sb.append(res.getRandomFace());
		}
		sb.append("\n");
		sb.append(res.getRandomBall());
		sb.append(" ");
		GregorianCalendar now = new GregorianCalendar();
		Duration d = Duration.between(now.toZonedDateTime(), countdownTarget.toZonedDateTime());

		addTime(sb, d.toDaysPart(), "day");
		addTime(sb, d.toHoursPart(), "hour");
		// addTime(sb, d.toMinutesPart(), "minute");

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
		of.tagAndValue("hype", hypes);
		of.finish(tag);
	}

}
