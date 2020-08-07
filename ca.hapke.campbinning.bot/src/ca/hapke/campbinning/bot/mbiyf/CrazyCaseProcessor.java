package ca.hapke.campbinning.bot.mbiyf;

import ca.hapke.campbinning.bot.commands.processors.MessageProcessor;

/**
 * @author Nathan Hapke
 */
public class CrazyCaseProcessor extends MessageProcessor {

	private static final double PROB_UPPER = 0.75;
	private static final double PROB_LOWER = 1 - PROB_UPPER;

	public CrazyCaseProcessor() {
		super(false);
	}

	@Override
	protected String internalProcessStringFragment(String value, boolean useMarkupV2) {
		boolean upper = true;
		double p;
		char[] out = new char[value.length()];
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (Character.isAlphabetic(c)) {
				if (upper)
					p = PROB_UPPER;
				else
					p = PROB_LOWER;
				if (Math.random() < p)
					c = Character.toUpperCase(c);
				else
					c = Character.toLowerCase(c);
				upper = !upper;
			}
			out[i] = c;
		}
		value = new String(out);
		return value;
	}
}
