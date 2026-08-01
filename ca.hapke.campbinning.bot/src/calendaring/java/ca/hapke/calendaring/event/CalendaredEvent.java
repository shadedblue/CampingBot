package ca.hapke.calendaring.event;

import ca.hapke.calendaring.timing.ByCalendar;
import ca.hapke.calendaring.timing.TimesProvider;

/**
 * @author Nathan Hapke
 */
public interface CalendaredEvent<T> {
	public TimesProvider<T> getTimeProvider();

	public void doWork(ByCalendar<T> timingEvent, T value);

	public boolean shouldRun();

	public StartupMode getStartupMode();
}
