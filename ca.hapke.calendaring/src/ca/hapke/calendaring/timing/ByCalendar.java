package ca.hapke.calendaring.timing;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @author Nathan Hapke
 */
public abstract class ByCalendar<T> {

	protected ZonedDateTime future;
	protected ZonedDateTime past;

	public final T value;
	protected final int qty;
	protected final ChronoUnit unit;

	public ByCalendar(T value, int qty, ChronoUnit unit) {
		this.value = value;
		this.qty = qty;
		this.unit = unit;
	}

	public void generateNearestEvents(ZonedDateTime when) {
		ZonedDateTime target = generateATargetTime(when);
		translateFuturePast(when, target);
	}

	public ZonedDateTime generateATargetTime() {
		return generateATargetTime(ZonedDateTime.now());
	}

	public abstract ZonedDateTime generateATargetTime(ZonedDateTime when);

	public ZonedDateTime getFuture() {
		return future;
	}

	public ZonedDateTime getPast() {
		return past;
	}

	protected void translateFuturePast(ZonedDateTime when, ZonedDateTime target) {
		past = target.minus(qty, unit);
		future = target;

		while (true) {
			boolean pastCorrect = past.isBefore(when);
			boolean futureCorrect = future != null && future.isAfter(when);
			if (pastCorrect && futureCorrect)
				break;

			if (pastCorrect) {
				past = past.plus(qty, unit);
				future = future.plus(qty, unit);
			} else if (futureCorrect) {

				past = past.minus(qty, unit);
				future = future.minus(qty, unit);
			} else {
				System.err.println("temporal screwed up");
				break;
			}
		}
	}

}