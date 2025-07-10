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