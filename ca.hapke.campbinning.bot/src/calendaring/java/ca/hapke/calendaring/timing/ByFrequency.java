package ca.hapke.calendaring.timing;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @author Nathan Hapke
 */
public class ByFrequency<T> extends ByCalendar<T> {

	public ByFrequency(T value, int qty, ChronoUnit unit) {
		super(value, qty, unit);
	}

	@Override
	public ZonedDateTime generateATargetTime(ZonedDateTime when) {
		ZonedDateTime trunc = when.truncatedTo(unit);
		ZonedDateTime target = trunc.plus(qty, unit);
		return target;
	}

}
