package ca.hapke.campbinning.bot.interval;

/**
 * @author Nathan Hapke
 *
 */
public interface IntervalByExecutionTime {
	public long getNextExecutionTime();

	public void doWork();

	public boolean shouldRun();

	void generateNextExecTime();
}
