package ca.hapke.campbinning.bot.util;

/**
 * @author Nathan Hapke
 */
public abstract class StatusThread extends Thread {
	protected boolean running = false;
	protected boolean complete = false;
	protected boolean kill = false;

	public boolean isKill() {
		return kill;
	}

	public void abort() {
		if (!this.complete)
			this.kill = true;
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isComplete() {
		return complete;
	}

	@Override
	public final void run() {
		running = true;

		try {
			doWork();
		} catch (Exception e) {
			kill = true;
		}

		running = false;
		complete = true;
	}

	protected abstract void doWork();
}
