package ca.hapke.calendaring.timing;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nathan Hapke
 */
public class TimesProvider<T> {
	private List<ByTimeOfCalendar<T>> times = new ArrayList<>();
//	private EventList<Instant> futures = new BasicEventList<>(); 
//	private EventList<Instant> pasts = new BasicEventList<>(); 
//	private Map<ByTimeOfCalendar<T>, ZonedDateTime> futures = new HashMap<>();
//	private Map<ByTimeOfCalendar<T>, ZonedDateTime> pasts = new HashMap<>();

//	public Collection<ZonedDateTime> getNearestFutures() {
//		return futures.values();
//	}
//
//	public Collection<ZonedDateTime> getNearestPasts() {
//		return pasts.values();
//	}
	public void generateNearestEvents() {
		generateNearestEvents(ZonedDateTime.now());
	}

	public void generateNearestEvents(ZonedDateTime when) {
		for (ByTimeOfCalendar<T> t : times) {
			t.generateNearestEvents(when);
			use(t, when);
		}
	}

	public void use(ByTimeOfCalendar<T> t) {
		use(t, ZonedDateTime.now());
	}

	public void use(ByTimeOfCalendar<T> t, ZonedDateTime when) {
		if (!times.contains(t))
			times.add(t);

		t.generateNearestEvents(when);

//		futures.put(t, t.getFuture());
//		pasts.put(t, t.getPast());
	}

	public ByTimeOfCalendar<T> getMostNearestPast() {
		ByTimeOfCalendar<T> result = null;
		ZonedDateTime nearest = null;

		for (ByTimeOfCalendar<T> t : times) {
			ZonedDateTime past = t.getPast();
			if (nearest == null || past.isAfter(nearest)) {
				nearest = past;
				result = t;
			}
		}

		return result;
	}

	public ByTimeOfCalendar<T> getNearestFuture() {
		ByTimeOfCalendar<T> result = null;
		ZonedDateTime nearest = null;

		for (ByTimeOfCalendar<T> t : times) {
			ZonedDateTime f = t.getFuture();
			if (nearest == null || f.isBefore(nearest)) {
				nearest = f;
				result = t;
			}
		}

		return result;
	}
}
