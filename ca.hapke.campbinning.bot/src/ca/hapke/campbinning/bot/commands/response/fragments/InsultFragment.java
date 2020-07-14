package ca.hapke.campbinning.bot.commands.response.fragments;

import ca.hapke.campbinning.bot.commands.response.InsultGenerator;
import ca.hapke.campbinning.bot.commands.response.MessageProcessor;

/**
 * @author Nathan Hapke
 */
public class InsultFragment extends ResultFragment {

	public enum Perspective {
		Me,
		You;
	}

	private String insult;
	private Perspective p;

	public InsultFragment(Perspective p) {
		this.p = p;
		this.insult = InsultGenerator.getInstance().getInsult();
	}

	@Override
	public String getValue(MessageProcessor processor) {
		String reference = "";
		switch (p) {
		case Me:
			reference = "I'm a ";
			break;
		case You:
			reference = "you ";
			break;

		}
		String output = reference + insult;

		output = casify(output);
		output = processor.processString(output);
		output = markup(output);

		return output;
	}

}
