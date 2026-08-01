package ca.hapke.campingbot.processors;

import java.util.function.UnaryOperator;

/**
 * @author Nathan Hapke
 */
public class CrazyCaseProcessor extends MessageProcessor {

	private static final UnaryOperator<Character> UPPER = Character::toUpperCase;
	private static final UnaryOperator<Character> LOWER = Character::toLowerCase;
	private static final double[] p = new double[] { 0.75, 0.35, 0.2, 0.25, 0.4, 0.55, 0.75, 0.85, 0.9, 0.95 };
	private int hits = 0;
	private UnaryOperator<Character> hitCase = UPPER;
	private UnaryOperator<Character> missCase = LOWER;

	public CrazyCaseProcessor() {
		super(false);
	}

	@Override
	protected String internalProcessStringFragment(String value, boolean useMarkupV2) {
		double p;
		char[] out = new char[value.length()];

		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (Character.isAlphabetic(c)) {
				p = getP(hits);
				if (Math.random() < p) {
					c = hitCase.apply(c);
					flip();
					hits = 0;
				} else {
					hits++;
					c = missCase.apply(c);
				}
			}
			out[i] = c;
		}
		value = new String(out);
		return value;
	}

	protected void flip() {
		UnaryOperator<Character> temp = hitCase;
		hitCase = missCase;
		missCase = temp;
	}

	private double getP(int j) {
		if (j >= p.length)
			j = p.length - 1;
		return p[j];
	}
}
