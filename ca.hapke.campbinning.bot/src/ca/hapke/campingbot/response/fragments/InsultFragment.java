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
	private String reference = "";

	public InsultFragment(Perspective p) {
		this(p, CaseChoice.Normal);
	}

	public InsultFragment(Perspective p, CaseChoice cc) {
		super(cc);
		this.p = p;

		switch (p) {
		case Me:
			reference = "I'm a ";
			break;
		case You:
			reference = "you ";
			break;

		}
		this.insult = InsultGenerator.getInstance().getInsult();
	}

	private InsultFragment(String insult, Perspective p, String reference) {
		this.insult = insult;
		this.p = p;
		this.reference = reference;
	}

	@Override
	public String getValue(MessageProcessor processor, boolean useMarkupV2) {

		String output = reference + insult;

		output = casify(output);
		output = processor.processString(output, useMarkupV2);
		output = markup(output);

		return output;
	}

	@Override
	public ResultFragment transform(MessageProcessor proc, boolean useMarkupV2) {
		String ref2 = proc.processString(reference, useMarkupV2);
		String insult2 = proc.processString(insult, useMarkupV2);
		return new InsultFragment(insult2, p, ref2);
	}

}
