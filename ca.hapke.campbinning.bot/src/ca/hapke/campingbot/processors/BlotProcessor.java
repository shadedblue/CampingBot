package ca.hapke.campingbot.processors;

import ca.hapke.util.CollectionUtil;

/**
 * @author Nathan Hapke
 */
public class BlotProcessor extends MessageProcessor {

	private static final double SENSITIVITY = 0.4;
	private Character[] blots;
	public static final Character[] blotsAll;
	public static final Character[] blotsPartial = new Character[] { '▔', '▖', '▗', '▘', '▝' };
	public static final Character[] blotsFade = new Character[] { '░', '▙', '▟', '▛', '▜', '▞', '▚' };
	public static final Character[] blotsFull = new Character[] { '▓', '█', '█' };
	static {
		blotsAll = new Character[blotsFull.length + blotsFade.length + blotsPartial.length];
		int x = 0;
		for (int i = 0; i < blotsFull.length; i++) {
			blotsAll[x] = blotsFull[i];
			x++;
		}
		for (int i = 0; i < blotsFade.length; i++) {
			blotsAll[x] = blotsFade[i];
			x++;
		}
		for (int i = 0; i < blotsPartial.length; i++) {
			blotsAll[x] = blotsPartial[i];
			x++;
		}
	}

	public BlotProcessor(boolean enabled, Character[] blots) {
		super(enabled);
		this.blots = blots;
	}

	@Override
	protected String internalProcessStringFragment(String value, boolean useMarkupV2) {
		StringBuilder sb = new StringBuilder(value.length());
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (c >= 48 && c <= 122 && Math.random() < SENSITIVITY) {
				c = CollectionUtil.getRandom(blots);
			}
			sb.append(c);
		}

		return sb.toString();
	}

}
