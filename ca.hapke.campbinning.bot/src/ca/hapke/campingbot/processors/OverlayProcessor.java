package ca.hapke.campingbot.processors;

import java.awt.Point;

/**
 * @author Nathan Hapke
 */
public class OverlayProcessor extends MessageProcessor {

	private String[] msg;

	public OverlayProcessor(boolean enabled) {
		super(enabled);
	}

	public void setMessage(String... msg) {
		this.msg = msg;

	}

	@Override
	protected String internalProcessStringFragment(String value, boolean useMarkupV2) {
		String[] unders = value.split("\n");
		Point outer = findDimensions(unders);
		Point inner = findDimensions(msg);
		int before = 2;
		int after = 2;
		int xStart = centre(outer.x, inner.x, before, after);
		int yStart = centre(outer.y, inner.y, 1, 1);

		StringBuilder sb = new StringBuilder(value.length());

		int y = 0;
		for (; y < yStart; y++) {
			sb.append(unders[y]);
			sb.append('\n');
		}

		int fullInnerWidth = inner.x + before + after - 2;
		String topBottom = " *" + "-".repeat(fullInnerWidth) + "* ";
		overlay(sb, unders[y], topBottom, xStart);
		y++;

		for (int j = 0; j < msg.length; j++, y++) {
			String over = msg[j];
			String under = unders[y];

			int overLength = over.length();
			int left = (fullInnerWidth - overLength) / 2;
			int right = fullInnerWidth - left - overLength;
			String top = " |" + " ".repeat(left) + over + " ".repeat(right) + "| ";
			overlay(sb, under, top, xStart);
		}

		overlay(sb, unders[y], topBottom, xStart);
		y++;
		for (; y < unders.length; y++) {
			sb.append(unders[y]);
			sb.append('\n');
		}

		return sb.toString();
	}

	private void overlay(StringBuilder sb, String under, String over, int xStart) {
		int i = 0;
		for (; i < xStart; i++) {
			char c = under.charAt(i);
			sb.append(c);
		}
		sb.append(over);
		i += over.length();
		for (; i < under.length(); i++) {
			char c = under.charAt(i);
			sb.append(c);
		}

		sb.append('\n');
	}

	private int centre(int outer, int inner, int before, int after) {
		return (outer - (inner + before + after)) / 2;
	}

	private Point findDimensions(String[] split) {
		int w = 0;
		for (String s : split) {
			w = Math.max(w, s.length());
		}
		return new Point(w, split.length);
	}
}
