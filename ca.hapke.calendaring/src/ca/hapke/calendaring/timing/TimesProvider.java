package ca.hapke.calendaring.timing;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nathan Hapke
 */
public class TimesProvider<T> {
	// For GlazedLists to autosort
	private PropertyChangeSupport support = new PropertyChangeSupport(this);

	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		support.addPropertyChangeListener(pcl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		support.removePropertyChangeListener(pcl);
	}

	///

	private List<ByCalendar<T>> times = new ArrayList<>();
	private ZonedDateTime lastExecTime = null;

	@SafeVarargs
	public TimesProvider(ByCalendar<T>... inputs) {
		for (ByCalendar<T> x : inputs) {
			times.add(x);
		}
		generateNearestEvents();
	}

	public TimesProvider(List<ByCalendar<T>> inputs) {
		addAll(inputs);
	}

	public TimesProvider(ByCalendar<T> input) {
		add(input);
	}

	public void add(ByCalendar<T> input) {
		times.add(input);
		generateNearestEvents();
	}

	public void addAll(List<ByCalendar<T>> inputs) {
		times.addAll(inputs);
		generateNearestEvents();
	}

	public void remove(ByCalendar<T> x) {
		times.remove(x);
		generateNearestEvents();
	}

	public void clear() {
		times.clear();
		generateNearestEvents();
	}

	public void generateNearestEvents() {
		generateNearestEvents(ZonedDateTime.now());
	}

	public void generateNearestEvents(ZonedDateTime when) {
		for (ByCalendar<T> t : times) {
			t.generateNearestEvents(when);
			use(t, when);
		}
	}

//	private void use(ByCalendar<T> t) {
//		use(t, ZonedDateTime.now());
//	}

	private void use(ByCalendar<T> t, ZonedDateTime when) {
		if (!times.contains(t))
			times.add(t);

		t.generateNearestEvents(when);
	}

	public ByCalendar<T> getMostNearestPast() {
		ByCalendar<T> result = null;
		ZonedDateTime nearest = null;

		for (ByCalendar<T> t : times) {
			ZonedDateTime past = t.getPast();
			if (nearest == null || past.isAfter(nearest)) {
				nearest = past;
				result = t;
			}
		}

		return result;
	}

	public ByCalendar<T> getNearestFuture() {
		ByCalendar<T> result = null;
		ZonedDateTime nearest = null;

		for (ByCalendar<T> t : times) {
			ZonedDateTime f = t.getFuture();
			if (nearest == null || f.isBefore(nearest)) {
				nearest = f;
				result = t;
			}
		}

		return result;
	}

	public ZonedDateTime getLastExecTime() {
		return lastExecTime;
	}

	public void setLastExecTime() {
		setLastExecTime(ZonedDateTime.now());
	}

	public void setLastExecTime(ZonedDateTime time) {
		ZonedDateTime oldTime = this.lastExecTime;
		this.lastExecTime = time;
		support.firePropertyChange("lastExecTime", oldTime, lastExecTime);
	}
}
