package ca.hapke.campbinning.bot.interval;

import java.time.DayOfWeek;

/**
 * @author Nathan Hapke
 *
 */
public class TimeOfWeek<T> {

	public final DayOfWeek day;
	public final int h;
	public final int m;
	public final T value;

	public TimeOfWeek(DayOfWeek day, int h, int m, T value) {
		this.day = day;
		this.h = h;
		this.m = m;
		this.value = value;
	}

}
