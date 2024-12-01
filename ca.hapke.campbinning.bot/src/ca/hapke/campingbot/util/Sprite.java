package ca.hapke.campingbot.util;

import java.awt.Image;

/**
 * @author Nate Hapke
 */
public class Sprite {
	private Image[] frames;
	private int[] durations;
	private int[] cumulativeDurations;
	private final int totalDuration;
	private final boolean animated;

	public Sprite(Image frame) {
		this(new Image[] { frame }, new int[] { 0 });
	}

	public Sprite(Image[] frames, int[] durations) {
		this.frames = frames;
		this.durations = durations;
		int frameCount = durations.length;
		cumulativeDurations = new int[frameCount];
		int total = 0;
		for (int i = 0; i < frameCount; i++) {
			total += durations[i];
			cumulativeDurations[i] = total;
		}
		totalDuration = total;

		animated = frameCount > 1 && totalDuration > 0;
	}

	public Image getFrame(double d) {
		if (!animated) {
			return frames[0];
		}
		int i = 0;
		while (i < cumulativeDurations.length && d < cumulativeDurations[i]) {
			i++;
		}
		i--;
		i = Math.max(0, Math.min(frames.length - 1, i));
		return frames[i];
	}

	public int getDuration(int i) {
		return getDurations()[i];
	}

	/**
	 * Used for scaled images.
	 */
	public int[] getDurations() {
		return durations;
	}

	public int getTotalDuration() {
		return totalDuration;
	}

	public boolean isAnimated() {
		return animated;
	}

	public int size() {
		return frames.length;
	}
}
