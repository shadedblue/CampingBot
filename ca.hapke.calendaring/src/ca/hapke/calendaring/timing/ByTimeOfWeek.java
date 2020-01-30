package ca.hapke.calendaring.timing;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @author Nathan Hapke
 *
 */
public class ByTimeOfWeek<T> extends ByCalendar<T> {

	public final DayOfWeek day;
	public final int h;
	public final int m;

	public ByTimeOfWeek(DayOfWeek day, int h, int m, T value) {
		super(value, 1, ChronoUnit.WEEKS);
		this.day = day;
		this.h = h;
		this.m = m;
	}

	@Override
	public ZonedDateTime generateATargetTime(ZonedDateTime when) {
		// TODO if future is already created, just translate it by the calendar amount?

		when = when.truncatedTo(ChronoUnit.MINUTES);
		ZonedDateTime target;
		LocalDate ld = when.toLocalDate();
		DayOfWeek whenDay = ld.getDayOfWeek();
		LocalTime lt = when.toLocalTime();

		// FIXME Change to TemporalAdjuster?
		int daysAhead = day.ordinal() - whenDay.ordinal();
		int hoursAhead = h - lt.getHour();
		int minsAhead = m - lt.getMinute();
		target = when.plus(daysAhead, ChronoUnit.DAYS);
		target = target.plus(hoursAhead, ChronoUnit.HOURS);
		target = target.plus(minsAhead, ChronoUnit.MINUTES);

		return target;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ByTimeOfWeek [");
		builder.append(day);
		builder.append(" ");
		builder.append(h);
		builder.append(":");
		builder.append(m);
		builder.append("]=");
		builder.append(this.value);
		return builder.toString();
	}

}
