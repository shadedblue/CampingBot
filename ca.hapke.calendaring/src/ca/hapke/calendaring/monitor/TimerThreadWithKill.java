package ca.hapke.calendaring.monitor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nathan Hapke
 */
public abstract class TimerThreadWithKill extends Thread {

	private long millis;
	private static List<TimerThreadWithKill> threads = new ArrayList<>();
	static {
		Runtime.getRuntime().addShutdownHook(new Thread("shutdown thread") {
			@Override
			public void run() {
				shutdownThreads();
			}
		});
	}

	public static void shutdownThreads() {
		for (TimerThreadWithKill t : threads) {
			t.kill = true;
		}
	}

	public TimerThreadWithKill(String name, long millis) {
		super(name);
		threads.add(this);
		this.millis = millis;
	}

	protected volatile boolean kill = false;
	private boolean running = false;

	@Override
	public final void run() {
		startupCode();
		while (!kill) {
			running = true;
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
				System.err.println("THREAD SLEEP INTERRUPTED -- TTWK:25");
				System.out.println(Thread.interrupted());
				e.printStackTrace();
			}
			try {
				doWork();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		shutdownCode();
		running = false;
	}

	protected void startupCode() {
	}

	protected void shutdownCode() {
	}

	protected abstract void doWork();

	public boolean isRunning() {
		return running;
	}
}
