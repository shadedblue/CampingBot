package ca.hapke.campingbot.response.fragments;

import ca.hapke.campingbot.processors.MessageProcessor;

/**
 * @author Nathan Hapke
 */
public class TextFragment extends ResultFragment {
	private String value;

	public TextFragment(String value) {
		super(CaseChoice.Normal, TextStyle.Normal);
		this.value = value;
	}

	public TextFragment(String value, TextStyle ts) {
		super(CaseChoice.Normal, ts);
		this.value = value;
	}

	public TextFragment(String value, CaseChoice cc) {
		super(cc, TextStyle.Normal);
		this.value = value;
	}

	public TextFragment(String value, CaseChoice cc, TextStyle ts) {
		super(cc, ts);
		this.value = value;
	}

	@Override
	public String getValue(MessageProcessor processor, boolean useMarkupV2) {
		String cased = casify(value);
		String p = processor.processString(cased, useMarkupV2);
		return markup(p);
	}

	@Override
	public String toString() {
		return "TextFragment [" + value + "]";
	}

	@Override
	public ResultFragment transform(MessageProcessor proc, boolean useMarkupV2) {
		String v2 = proc.processString(value, useMarkupV2);

		return new TextFragment(v2, caseChoice, textStyle);
	}

}
