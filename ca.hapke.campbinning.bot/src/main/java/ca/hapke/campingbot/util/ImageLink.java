package ca.hapke.campingbot.util;

/**
 * @author Nathan Hapke
 */
public class ImageLink {
	public static final int STATIC = 1;
	public static final int GIF = 2;

	public final String url;
	public final int type;

	public ImageLink(String url, int type) {
		this.url = url;
		this.type = type;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ImageLink [");
		builder.append(type);
		builder.append(": ");
		if (url != null) {
			builder.append(url);
		}
		builder.append("]");
		return builder.toString();
	}
}
