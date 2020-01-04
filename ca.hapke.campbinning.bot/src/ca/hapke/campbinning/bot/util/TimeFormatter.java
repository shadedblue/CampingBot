package ca.hapke.campbinning.bot.util;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * TODO create minimumUnitSize
 * 
 * @author Nathan Hapke
 */
public class TimeFormatter {
	private static final String[] units = new String[] { "day", "hour", "minute", "second" };
	private final int unitCount;
	private final String delimiter;
	private final boolean futureEnabled;
	private final boolean pastEnabled;
	private boolean sequentialUnitsOnly;

	public TimeFormatter(int unitCount, String delimiter, boolean tensesEnabled, boolean sequentialUnitsOnly) {
		this.unitCount = unitCount;
		this.delimiter = delimiter;
		this.futureEnabled = tensesEnabled;
		this.pastEnabled = tensesEnabled;
		this.sequentialUnitsOnly = sequentialUnitsOnly;
	}

	private String makeTime(int amt, String unit) {
		StringBuilder sb = new StringBuilder();
		if (amt != 0) {
			int abs = Math.abs(amt);
			sb.append(abs);

			sb.append(" ");
			sb.append(unit);
			if (abs != 1)
				sb.append("s");
		}
		return sb.toString();
	}

	public String toPrettyString(long target) {
		Instant inst = Instant.ofEpochMilli(target);
		ZonedDateTime t = inst.atZone(ZoneId.systemDefault());
		return toPrettyString(t);
	}
	public String toPrettyString(ZonedDateTime target) {
		List<String> pieces = new ArrayList<>(unitCount);

		// GregorianCalendar now =;
		ZonedDateTime nowZoned = new GregorianCalendar().toZonedDateTime();
		Duration d = Duration.between(nowZoned, target);

		int i = 0;
		int unitsUsed = 0;
		boolean usedLastUnit = false;
		boolean hasData = false;
		while (i < units.length && unitsUsed < unitCount && (!sequentialUnitsOnly || usedLastUnit || !hasData)) {
			int qty = getUnit(d, i);
			usedLastUnit = (qty != 0);
			if (qty == 0) {

			} else {
				String unit = units[i];
				pieces.add(makeTime(qty, unit));

				hasData = true;
				unitsUsed++;
			}
			i++;
		}
		
		String out = CampingUtil.join(pieces, delimiter);
		boolean before = nowZoned.isBefore(target);
		boolean after = nowZoned.isAfter(target);
		if (futureEnabled && before) {
			out = out + " from now";
		} else if (pastEnabled && after) {
			out = out + " ago";
		}
		return out;
	}

	private int getUnit(Duration d, int i) {

		switch (i) {
		case 0:
			return (int) d.toDaysPart();
		case 1:
			return d.toHoursPart();
		case 2:
			return d.toMinutesPart();
		case 3:
			return d.toSecondsPart();
		}
		return 0;
	}
}
