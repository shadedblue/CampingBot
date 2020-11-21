package ca.hapke.campingbot.response.fragments;

import ca.hapke.campingbot.processors.MessageProcessor;
import ca.hapke.campingbot.response.InsultGenerator;

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
		this(p, CaseChoice.Normal);
	}

	public InsultFragment(Perspective p, CaseChoice cc) {
		super(cc);
		this.p = p;
		this.insult = InsultGenerator.getInstance().getInsult();
	}

	@Override
	public String getValue(MessageProcessor processor, boolean useMarkupV2) {
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
		output = processor.processString(output, useMarkupV2);
		output = markup(output);

		return output;
	}

}
