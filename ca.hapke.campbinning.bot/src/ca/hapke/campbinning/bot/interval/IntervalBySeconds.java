/**
 * 
 */
package ca.hapke.campbinning.bot.interval;

/**
 * @author Nathan Hapke
 */
public interface IntervalBySeconds {
	public int getSeconds();

	public void doWork();

	public boolean shouldRun();
}
