package ca.hapke.calendaring.timing;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @author Nathan Hapke
 */
public class ByTimeOfHour<T> extends ByCalendar<T> {

	public final int m;

	public ByTimeOfHour(int m, T value) {
		super(value, 1, ChronoUnit.HOURS);
		this.m = m;
	}

	@Override
	public ZonedDateTime generateATargetTime(ZonedDateTime when) {
		when = when.truncatedTo(ChronoUnit.MINUTES);
		LocalTime lt = when.toLocalTime();
		int minsAhead = m - lt.getMinute();

		ZonedDateTime target = when.plus(minsAhead, ChronoUnit.MINUTES);

		return target;
	}

}
