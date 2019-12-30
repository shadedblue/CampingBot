package ca.hapke.campbinning.bot.interval;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.ocpsoft.prettytime.PrettyTime;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;

/**
 * Lazy singleton
 * 
 * @author Nathan Hapke
 */
public class CampingIntervalThread extends TimerThreadWithKill {
	private EventList<ExecutionTimeTracker<IntervalBySeconds>> regularBySeconds = new ObservableElementList<>(
			GlazedLists.threadSafeList(new BasicEventList<>()), GlazedLists.beanConnector(ExecutionTimeTracker.class));
	private EventList<IntervalByExecutionTime> regularByExecutionTime = GlazedLists
			.threadSafeList(new BasicEventList<>());

	private static CampingIntervalThread instance;

	public static void put(IntervalBySeconds key) {
		getInstance().regularBySeconds.add(new ExecutionTimeTracker<IntervalBySeconds>(key));
	}

	public static void put(IntervalByExecutionTime key) {
		getInstance().regularByExecutionTime.add(key);
	}

	public EventList<ExecutionTimeTracker<IntervalBySeconds>> getRegularBySeconds() {
		return regularBySeconds;
	}

	public EventList<IntervalByExecutionTime> getRegularByExecutionTime() {
		return regularByExecutionTime;
	}

	public static CampingIntervalThread getInstance() {
		if (instance == null || instance.kill) {
			instance = new CampingIntervalThread();
			instance.start();
		}
		return instance;
	}

	private CampingIntervalThread() {
		super("CampingRegularIntervalThread", 1000);
	}

	@Override
	protected void doWork() {
		for (ExecutionTimeTracker<IntervalBySeconds> item : regularBySeconds) {
			IntervalBySeconds regularInterval = item.getItem();
			long lastRun = item.getTime();
			long now = System.currentTimeMillis();
			int secondsInterval = regularInterval.getSeconds();
			if (lastRun + secondsInterval * 1000 <= now) {
				item.setTime(now);
				try {
					if (regularInterval.shouldRun())
						regularInterval.doWork();
				} catch (Exception e) {
				}
			}
		}

		for (IntervalByExecutionTime item : regularByExecutionTime) {
			long now = System.currentTimeMillis();
			long targetTime = item.getNextExecutionTime();
			if (targetTime <= now && targetTime >= 0) {
				if (item.shouldRun()) {
					try {
						item.doWork();
					} catch (Exception e) {
						e.printStackTrace();
					}
					item.generateNextExecTime();
				}
			}
		}
	}

	@Override
	public String toString() {
		LocalDateTime now = LocalDateTime.now();
		StringBuilder builder = new StringBuilder();
		builder.append("CampingIntervalThread [");
		if (regularBySeconds.size() > 0) {
			builder.append("By Seconds\n");

			for (ExecutionTimeTracker<IntervalBySeconds> item : regularBySeconds) {
				IntervalBySeconds sec = item.getItem();
				long time = item.getTime();
				builder.append(sec.toString());
				builder.append(" - ");
				builder.append(sec.getSeconds());
				builder.append(" - ");
				LocalDateTime t = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());

				java.time.Duration duration = java.time.Duration.between(t, now);
				builder.append(duration.toSecondsPart());
				builder.append(" seconds ago");
				builder.append('\n');
			}
			// builder.append(", ");
			builder.append('\n');
		}
		PrettyTime pt = new PrettyTime();
		if (regularByExecutionTime.size() > 0) {
			builder.append("By Execution Times\n");
			for (IntervalByExecutionTime ext : regularByExecutionTime) {
				builder.append(ext.toString());
				builder.append(" - ");
				Date duration = new Date(ext.getNextExecutionTime());
				builder.append(pt.format(duration));
				builder.append('\n');
			}
		}
		builder.append("]");
		return builder.toString();
	}
}