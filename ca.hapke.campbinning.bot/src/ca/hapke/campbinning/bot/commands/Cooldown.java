package ca.hapke.campbinning.bot.commands;

import java.time.Instant;

/**
 * @author Nathan Hapke
 */
public class Cooldown {
	private final int seconds;
	private long lastExec = -1;

	public Cooldown(int seconds) {
		this.seconds = seconds;
	}

	public boolean isReady() {
		return lastExec < 0 || lastExec + seconds < Instant.now().getEpochSecond();
	}

	public int getSeconds() {
		return seconds;
	}

	public void setExec() {
		this.lastExec = Instant.now().getEpochSecond();
	}

}
