package ca.hapke.campbinning.bot.mbiyf;

import java.util.function.UnaryOperator;

import ca.hapke.campbinning.bot.processors.MessageProcessor;

/**
 * @author Nathan Hapke
 */
public class CrazyCaseProcessor extends MessageProcessor {

	private static final UnaryOperator<Character> UPPER = Character::toUpperCase;
	private static final UnaryOperator<Character> LOWER = Character::toLowerCase;
	private static final double[] p = new double[] { 0.75, 0.35, 0.2, 0.25, 0.4, 0.55, 0.75, 0.85, 0.9, 0.95 };

	public CrazyCaseProcessor() {
		super(false);
	}

	@Override
	protected String internalAfterStringAssembled(String value) {
//	protected String internalProcessStringFragment(String value, boolean useMarkupV2) {
		double p;
		char[] out = new char[value.length()];

		UnaryOperator<Character> hitCase = UPPER;
		UnaryOperator<Character> missCase = LOWER;
		UnaryOperator<Character> temp;

		int j = 0;
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (Character.isAlphabetic(c)) {
				p = getP(j);
				if (Math.random() < p) {
					c = hitCase.apply(c);
					temp = hitCase;
					hitCase = missCase;
					missCase = temp;
					j = 0;
				} else {
					j++;
					c = missCase.apply(c);
				}
			}
			out[i] = c;
		}
		value = new String(out);
		return value;
	}

	private double getP(int j) {
		if (j >= p.length)
			j = p.length - 1;
		return p[j];
	}
}
