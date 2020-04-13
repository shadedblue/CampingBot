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

	private EventList<CalendaredEvent> calendaredEvents = GlazedLists.threadSafeList(new BasicEventList<>());

	@Override
	protected void doWork() {
		ZonedDateTime now = ZonedDateTime.now();
		for (CalendaredEvent event : calendaredEvents) {
			TimesProvider timeProvider = event.getTimeProvider();
			ByCalendar future = timeProvider.getNearestFuture();
			if (future.getFuture().isBefore(now)) {
				invoke(event, timeProvider, future);
			}
		}
	}

	public void updateAllNextTimes() {
		for (CalendaredEvent event : calendaredEvents) {
			TimesProvider timeProvider = event.getTimeProvider();
			timeProvider.generateNearestEvents(ZonedDateTime.now());
		}
	}

	@SuppressWarnings("unchecked")
	private void invoke(CalendaredEvent event, TimesProvider timeProvider, ByCalendar calendarPoint) {
		ZonedDateTime now = ZonedDateTime.now();
		if (event.shouldRun()) {
			event.doWork(calendarPoint.value);
			timeProvider.setLastExecTime();
		}
		timeProvider.generateNearestEvents(now);
	}

	public boolean add(CalendaredEvent e) {
		StartupMode startupMode = e.getStartupMode();
		if (startupMode == StartupMode.Always || startupMode == StartupMode.Conditional) {
			TimesProvider timeProvider = e.getTimeProvider();
			invoke(e, timeProvider, timeProvider.getMostNearestPast());
		}
		return calendaredEvents.add(e);
	}

	public EventList<CalendaredEvent> getEvents() {
		return calendaredEvents;
	}
}
