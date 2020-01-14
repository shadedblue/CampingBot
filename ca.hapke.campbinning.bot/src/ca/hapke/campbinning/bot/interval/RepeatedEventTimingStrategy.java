package ca.hapke.campbinning.bot.interval;

/**
 * @author Nathan Hapke
 */
public abstract class RepeatedEventTimingStrategy {
	private long nextExec;
//	private long prevExec = -1;
	protected final StartupMode startupMode;
	protected final TimeGenerationPoint timePoint;
	protected final RepeatedEventTask task;

	public RepeatedEventTimingStrategy(StartupMode startupMode, TimeGenerationPoint timePoint, RepeatedEventTask task) {
		this.startupMode = startupMode;
		this.timePoint = timePoint;
		this.task = task;
	}

	/**
	 * @return true if the event was run
	 */
	public boolean poll() {
		long now = System.currentTimeMillis();
		boolean ran = false;
		if (nextExec < now) {
//			prevExec = now;
			ran = runEvent(ran);
		}

		return ran;
	}

	/**
	 * @return true if the event was run
	 */
	public boolean startupPoll() {
//		long now = System.currentTimeMillis();
		boolean ran = false;
		if (startupMode == StartupMode.Always || (startupMode == StartupMode.Conditional && task.shouldRun())) {
//			prevExec = now;
			ran = runEvent(ran);
		} else {
			nextExec = generateNextExecTime();
		}

		return ran;
	}

	private boolean runEvent(boolean ran) {
		if (timePoint == TimeGenerationPoint.BeforeRun)
			nextExec = generateNextExecTime();

		if (task.shouldRun()) {
			task.run();
			ran = true;
		}

		if (timePoint == TimeGenerationPoint.AfterRun)
			nextExec = generateNextExecTime();
		return ran;
	}

	public abstract long generateNextExecTime();
}
