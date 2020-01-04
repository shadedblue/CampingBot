package ca.hapke.campbinning.bot.interval;

import java.time.LocalDateTime;

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


}