package ca.hapke.campingbot.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;

/**
 * @author Nate Hapke
 */
public class ImageCache {
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

		private Image getFirstFrame() {
			if (scaledFrames == null)
				return null;

			Image firstFrame = null;
			for (int i = 0; i < scaledFrames.length; i++) {
				Image f = scaledFrames[i];
				if (f != null) {
					firstFrame = f;
					break;
				}
			}
			return firstFrame;
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

	private static final String GIF = "gif";

	private static ImageCache instance = new ImageCache();

	public static ImageCache getInstance() {
		return instance;
	}

	private ImageCache() {
	}

	private static final String DELIMITER = "$";

	private Map<String, Sprite> cache = new HashMap<>();
	private Map<Sprite, String> reverseCache = new HashMap<>();

	public Sprite getImage(String folder, String filename) {
		String key = getFilenameKey(folder, filename);
		Sprite sprite = cache.get(key);

		if (sprite == null) {
			Class<? extends ImageCache> cls = this.getClass();
			URL resource = cls.getResource(cls.getSimpleName() + ".class");
			String protocol = resource.getProtocol();

			try {
				if (Objects.equals(protocol, "jar")) {
					InputStream in = searchJar(folder, filename);
					BufferedImage image = ImageIO.read(in);
					if (image != null) {
						sprite = new Sprite(key, image);
					}

				} else if (Objects.equals(protocol, "file")) {
					File f;
					f = searchFilesystem(folder, filename);
					if (f == null) {
						f = searchViaClassloader(folder, filename);
					}
					try {
						if (!isGif(f)) {
							BufferedImage image = ImageIO.read(f);
							if (image != null)
								sprite = new Sprite(key, image);

						} else {
							// load animated gif frames
							ImageReader reader = ImageIO.getImageReadersByFormatName(GIF).next();
							ImageInputStream iis = ImageIO.createImageInputStream(f);
							reader.setInput(iis, false);
							int numFrames = reader.getNumImages(true);
							if (numFrames > 0) {
								Image[] images = new Image[numFrames];
								int[] delays = new int[numFrames];
								for (int i = 0; i < numFrames; i++) {
									BufferedImage frame = reader.read(i);
									images[i] = frame;
									IIOMetadataNode root = (IIOMetadataNode) reader.getImageMetadata(i)
											.getAsTree("javax_imageio_gif_image_1.0");
									IIOMetadataNode gce = (IIOMetadataNode) root
											.getElementsByTagName("GraphicControlExtension").item(0);

									int delay = Integer.valueOf(gce.getAttribute("delayTime"));
									delays[i] = delay;
								}
								sprite = new Sprite(key, images, delays);
							}
						}
					} catch (Exception e) {
					}
				}
				cache.put(key, sprite);
				reverseCache.put(sprite, key);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sprite;
	}

	public static Image scaleToTileSize(Image image, int tileSize) {
		Image result = image;
		int w = image.getWidth(null);
		int h = image.getHeight(null);
		boolean tooBig = w > tileSize || h > tileSize;
		if (tooBig) {
			int x, y;
			if (w > h) {
				x = tileSize;
				y = -1;
			} else {
				x = -1;
				y = tileSize;
			}
			result = image.getScaledInstance(x, y, Image.SCALE_SMOOTH);
		}
		return result;
	}

	public static Image scaleToHeight(Image image, int h2) {
		Image result = image;
		int w1 = image.getWidth(null);
		int h1 = image.getHeight(null);
		double aspect = ((double) w1) / h1;
		int w2 = (int) (h2 * aspect);
		result = image.getScaledInstance(w2, h2, Image.SCALE_SMOOTH);

		return result;
	}

	private boolean isGif(File f) {
		if (f == null)
			return false;

		String name = f.getName();
		int len = name.length();
		if (len > 3) {
			String ext = name.substring(len - 3, len);
			if (GIF.equalsIgnoreCase(ext)) {
				return true;
			}
		}
		return false;
	}

	public InputStream searchJar(String folder, String filename) {
		String path;
		if (folder != null && folder.length() > 0) {
			path = '/' + folder + '/' + filename;
		} else {
			path = '/' + filename;
		}
		InputStream in = getClass().getResourceAsStream(path);
		return in;
	}

	public File searchViaClassloader(String folder, String filename) {
		ProtectionDomain pd = getClass().getProtectionDomain();
		ClassLoader cl = pd.getClassLoader();

		File f;
		int i = 0;
		String fullFilename = (folder != null ? (folder + File.separatorChar) : "") + filename;
		while (i < 10) {
			URL res = cl.getResource(fullFilename);
			try {
				URI uri = res.toURI();

				f = new File(uri);
				if (f.exists())
					return f;
			} catch (Exception e) {
				System.err.println("[CL]: URI Exception");
			}
			fullFilename = ".." + File.separatorChar + fullFilename;
			i++;
		}
		System.err.println("File not found [CL]: Folder[" + folder + "] Filename[" + filename + "]");
		return null;
	}

	public File searchFilesystem(String folder, String filename) {
		String userDir = System.getProperty("user.dir");
		File f;
		File dir = new File(userDir);

		int i = 0;
		while (i < 10) {
			String slashFolder = folder != null ? (File.separatorChar + folder) : "";
			String path = dir.getAbsolutePath() + slashFolder + File.separatorChar + filename;
			f = new File(path);
			if (f.exists())
				return f;
			dir = dir.getParentFile();
			if (dir == null || !dir.canRead())
				break;
			i++;
		}
		System.err.println("File not found [FS]: Folder[" + folder + "] Filename[" + filename + "]");
		return null;
	}

	private static String getFilenameKey(String folder, String filename) {
		return folder + DELIMITER + filename;
	}
}