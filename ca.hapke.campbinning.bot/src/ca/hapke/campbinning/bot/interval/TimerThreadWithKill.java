package ca.hapke.campbinning.bot.interval;

/**
 * @author Nathan Hapke
 */
public abstract class TimerThreadWithKill extends Thread {

	private long millis;

	public TimerThreadWithKill(String name, long millis) {
		super(name);
		this.millis = millis;
	}

	protected boolean kill = false;

	@Override
	public final void run() {

		while (!kill) {
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
				kill = true;
			}
			try {
				doWork();
			} catch (Exception e) {
			}
		}
	}

	protected abstract void doWork();
}
