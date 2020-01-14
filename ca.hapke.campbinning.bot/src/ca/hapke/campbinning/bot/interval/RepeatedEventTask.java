package ca.hapke.campbinning.bot.interval;

/**
 * @author Nathan Hapke
 */
public interface RepeatedEventTask {
	public void run();

	public boolean shouldRun();

}
