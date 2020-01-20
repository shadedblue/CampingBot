package ca.hapke.calendaring.timing;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @author Nathan Hapke
 */
public class ByTimeOfYear<T> extends ByTimeOfCalendar<T> {

	public final int month;
	public final int d;
	public final int h;
	public final int min;

	public ByTimeOfYear(int month, int d, int h, int min, T value) {
		super(value, 1, ChronoUnit.YEARS);
		this.month = month;
		this.d = d;
		this.h = h;
		this.min = min;
	}

	@Override
	public ZonedDateTime generateATargetTime(ZonedDateTime when) {
		when = when.truncatedTo(ChronoUnit.MINUTES);
		ZonedDateTime target = when.withMonth(month).withDayOfMonth(d).withHour(h).withMinute(min);

		return target;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ByTimeOfYear [");
		builder.append(month);
		builder.append("/");
		builder.append(d);
		builder.append(" ");
		builder.append(h);
		builder.append(":");
		builder.append(min);
		builder.append("]=");
		builder.append(this.value);
		return builder.toString();
	}
}
