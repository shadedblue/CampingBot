package ca.hapke.campbinning.bot.commands;

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
}
