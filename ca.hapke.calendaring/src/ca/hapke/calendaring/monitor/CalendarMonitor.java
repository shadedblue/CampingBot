package ca.hapke.calendaring.monitor;

import java.time.ZonedDateTime;

import ca.hapke.calendaring.event.CalendaredEvent;
import ca.hapke.calendaring.event.StartupMode;
import ca.hapke.calendaring.timing.ByCalendar;
import ca.hapke.calendaring.timing.TimesProvider;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

/**
 * @author Nathan Hapke
 */
@SuppressWarnings("rawtypes")
public class CalendarMonitor extends TimerThreadWithKill {
	private static CalendarMonitor instance;

	public static CalendarMonitor getInstance() {
		if (instance == null || instance.kill) {
			instance = new CalendarMonitor();
			instance.start();
		}
		return instance;
	}

	private CalendarMonitor() {
		super("CalendarMonitorThread", 1000);
	}

	private EventList<CalendaredEvent<?>> calendaredEvents = GlazedLists
			.threadSafeList(new BasicEventList<CalendaredEvent>());

	@Override
	protected void doWork() {
		ZonedDateTime now = ZonedDateTime.now();
		for (CalendaredEvent<?> event : calendaredEvents) {
			work(now, event);
		}
	}

	/**
	 * Helper method for doWork so that we can parameterize and bind the <T> types, without breaking inheritance
	 */
	private <T> void work(ZonedDateTime now, CalendaredEvent<T> event) {
		TimesProvider<T> timeProvider = event.getTimeProvider();
		ByCalendar<T> future = timeProvider.getNearestFuture();
		if (future.getFuture().isBefore(now)) {
			invoke(event, timeProvider, future);
		}
	}

	private <T> void invoke(CalendaredEvent<T> event, TimesProvider<T> timeProvider, ByCalendar<T> calendarPoint) {
		ZonedDateTime now = ZonedDateTime.now();
		if (event.shouldRun()) {
			try {
				event.doWork(calendarPoint.value);

				boolean expired = calendarPoint.activate();
				if (expired) {
					timeProvider.remove(calendarPoint);
				}
			} catch (Exception e) {
			}
			timeProvider.setLastExecTime();
		}
		timeProvider.generateNearestEvents(now);
	}

	public <T> boolean add(CalendaredEvent<T> e) {
		StartupMode startupMode = e.getStartupMode();
		if (startupMode == StartupMode.Always || startupMode == StartupMode.Conditional) {
			TimesProvider<T> timeProvider = e.getTimeProvider();
			invoke(e, timeProvider, timeProvider.getMostNearestPast());
		}
		if (!calendaredEvents.contains(e))
			return calendaredEvents.add(e);
		else
			return false;
	}

	public <T> boolean remove(CalendaredEvent<T> e) {
		return calendaredEvents.remove(e);
	}

	public void updateAllNextTimes() {
		for (CalendaredEvent event : calendaredEvents) {
			TimesProvider timeProvider = event.getTimeProvider();
			timeProvider.generateNearestEvents(ZonedDateTime.now());
		}
	}

	public EventList<CalendaredEvent<?>> getEvents() {
		return calendaredEvents;
	}
}
