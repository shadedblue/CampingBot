package ca.hapke.campingbot.util;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Nathan Hapke
 */
public class Sprite {
	private static final String ORIGINAL = "original";
	private Image[] originalFrames;
	private Image[] scaledFrames;
	private Map<String, Image[]> scaledMap = new HashMap<>();

	private int[] durations;
	private int[] cumulativeDurations;
	private final int totalDuration;
	private final boolean animated;
	private String scaleKey;
	private final String filenameKey;

	public Sprite(String filenameKey, Image frame) {
		this(filenameKey, new Image[] { frame }, new int[] { 0 });
	}

	public Sprite(String filenameKey, Image[] frames, int[] durations) {
		this.filenameKey = filenameKey;
		this.originalFrames = frames;
		this.scaledFrames = frames;
		this.scaleKey = ORIGINAL;
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
		double x = d % totalDuration;

		if (!animated) {
			return scaledFrames[0];
		}
		int i = 0;
		while (i < cumulativeDurations.length && x < cumulativeDurations[i]) {
			i++;
		}
		i--;
		i = Math.max(0, Math.min(scaledFrames.length - 1, i));
		return scaledFrames[i];
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
		return scaledFrames.length;
	}

	public void resetScale() {
		this.scaledFrames = originalFrames;
		this.scaleKey = ORIGINAL;
	}

	public void scale(int boxWidth, int boxHeight) {
		if (boxWidth <= 0 || boxHeight <= 0) {
			this.scaledFrames = originalFrames;
			this.scaleKey = ORIGINAL;
		}

		Image f1 = originalFrames[0];
		int originalW = f1.getWidth(null);
		int originalH = f1.getHeight(null);

		double originalAspect = ((double) originalW) / originalH;

		double boxAspect = ((double) boxWidth) / boxHeight;
		int w, h;
		if (originalAspect > boxAspect) {
			// image wider, so scale height
			w = boxWidth;
			h = (int) (boxWidth / originalAspect);
		} else {
			w = (int) (boxHeight * originalAspect);
			h = boxHeight;
		}
		if (w <= 0 || h <= 0) {
			this.scaledFrames = originalFrames;
			this.scaleKey = ORIGINAL;
			return;
		}

		scaleKey = w + "$" + h;
		scaledFrames = scaledMap.get(scaleKey);
		if (scaledFrames == null) {
			scaledFrames = new Image[originalFrames.length];
			for (int i = 0; i < originalFrames.length; i++) {
				Image img = originalFrames[i];
				scaledFrames[i] = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
			}
			scaledMap.put(scaleKey, scaledFrames);
		}
	}

	public Image getFirstFrame() {
		if (scaledFrames == null)
			return null;

		for (int i = 0; i < scaledFrames.length; i++) {
			Image f = scaledFrames[i];
			if (f != null) {
				return f;
			}
		}
		return null;
	}

	public int getWidth() {
		Image firstFrame = getFirstFrame();

		if (firstFrame != null) {
			return firstFrame.getWidth(null);
		} else {
			return 0;
		}
	}

	public int getHeight() {
		Image firstFrame = getFirstFrame();

		if (firstFrame != null) {
			return firstFrame.getHeight(null);
		} else {
			return 0;
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Sprite [");
		builder.append(filenameKey);
		builder.append(" scaleKey=");
		builder.append(scaleKey);
		builder.append("]");
		return builder.toString();
	}
}