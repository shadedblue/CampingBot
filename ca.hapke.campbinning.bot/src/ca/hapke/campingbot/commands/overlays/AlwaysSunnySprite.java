package ca.hapke.campingbot.commands.overlays;

import java.awt.Image;

import ca.hapke.campingbot.CampingSystem;
import ca.hapke.campingbot.util.ImageCache;
import ca.hapke.campingbot.util.ImageCache.Sprite;

/**
 * @author Nathan Hapke
 */
public class AlwaysSunnySprite {

	public final String filename;
	public final double locationPct;
	public final double heightPct;

	private Image img;

	public AlwaysSunnySprite(String filename, double locationPct, double heightPct) {
		this.filename = filename;
		this.locationPct = locationPct;
		this.heightPct = heightPct;
	}

	public Image getImage() {
		if (img == null) {
			ImageCache cache = ImageCache.getInstance();
			Sprite overlay = cache.getImage(CampingSystem.getInstance().getAssetsFolder(), filename);
			img = overlay.getFrame(0);
		}
		return img;
	}
}
