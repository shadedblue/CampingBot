package ca.hapke.campingbot.commands.overlays;

/**
 * 
 * @author Nathan Hapke
 */
public final class AlwaysSunnyOverlaySet {
	public final int delay;
	public final AlwaysSunnySprite[] sprites;

	public AlwaysSunnyOverlaySet(int delay, AlwaysSunnySprite... sprites) {
		this.delay = delay;
		this.sprites = sprites;
	}
}
