package ca.hapke.campbinning.bot.interval;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author Nathan Hapke
 */
public class ExecutionTimeTracker<T> {
	private final T item;
	private long time;

	// For GlazedLists to autosort
	private PropertyChangeSupport support = new PropertyChangeSupport(this);

	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		support.addPropertyChangeListener(pcl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		support.removePropertyChangeListener(pcl);
	}

	public ExecutionTimeTracker(T item) {
		this(item, System.currentTimeMillis());
	}

	public ExecutionTimeTracker(T item, long time) {
		this.item = item;
		this.time = time;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		long oldTime = this.time;
		this.time = time;
		support.firePropertyChange("time", oldTime, time);
	}

	public T getItem() {
		return item;
	}
}
