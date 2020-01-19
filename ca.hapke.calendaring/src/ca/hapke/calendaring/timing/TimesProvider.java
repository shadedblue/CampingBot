package ca.hapke.calendaring.timing;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Nathan Hapke
 */
public class TimesProvider<T> {
	private List<ByTimeOfCalendar<T>> times = new ArrayList<>();
//	private EventList<Instant> futures = new BasicEventList<>(); 
//	private EventList<Instant> pasts = new BasicEventList<>(); 
	private Map<ByTimeOfCalendar<T>, ZonedDateTime> futures = new HashMap<>();
	private Map<ByTimeOfCalendar<T>, ZonedDateTime> pasts = new HashMap<>();

	public Collection<ZonedDateTime> getNearestFutures() {
		return futures.values();
	}

	public Collection<ZonedDateTime> getNearestPasts() {
		return pasts.values();
	}

	public void add(ByTimeOfCalendar<T> t) {
		times.add(t);
		generateFutureAndPasts(ZonedDateTime.now(), t);
	}

	public void run(ByTimeOfCalendar<T> time) {

		generateFutureAndPasts();

	}

	private void generateFutureAndPasts() {
		generateFutureAndPasts(ZonedDateTime.now());
	}

	/**
	 * public for testing
	 */
	public void generateFutureAndPasts(ZonedDateTime when) {
		for (ByTimeOfCalendar<T> time : times) {
			generateFutureAndPasts(when, time);
		}
	}

	private void generateFutureAndPasts(ZonedDateTime when, ByTimeOfCalendar<T> time) {
		time.generateNearestEvents(when);

		futures.put(time, time.getFuture());
		pasts.put(time, time.getPast());
	}
}
