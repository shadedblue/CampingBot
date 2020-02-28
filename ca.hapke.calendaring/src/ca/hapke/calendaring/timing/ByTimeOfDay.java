package ca.hapke.calendaring.timing;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @author Nathan Hapke
 */
public class ByTimeOfDay<T> extends ByCalendar<T> {

	public final int h;
	public final int m;

	public ByTimeOfDay(int h, int m, T value) {
		super(value, 1, ChronoUnit.DAYS);
		this.h = h;
		this.m = m;
	}

	@Override
	public ZonedDateTime generateATargetTime(ZonedDateTime when) {
		when = when.truncatedTo(ChronoUnit.MINUTES);

		LocalTime lt = when.toLocalTime();
		int hoursAhead = h - lt.getHour();
		int minsAhead = m - lt.getMinute();

		ZonedDateTime target;
		target = when.plus(minsAhead, ChronoUnit.MINUTES);
		target = target.plus(hoursAhead, ChronoUnit.HOURS);

		return target;
	}

}
