package ca.hapke.campingbot.commands.overlays;

/**
 * 
 * @author Nathan Hapke
 */
public final class AlwaysSunnyOverlaySet {
	public final String name;
	public final int delay;
	public final AlwaysSunnySprite[] sprites;

	public AlwaysSunnyOverlaySet(String name, int delay, AlwaysSunnySprite... sprites) {
		this.name = name;
		this.delay = delay;
		this.sprites = sprites;
	}
}
