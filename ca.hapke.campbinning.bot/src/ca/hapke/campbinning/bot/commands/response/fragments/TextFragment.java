package ca.hapke.campbinning.bot.commands.response.fragments;

import ca.hapke.campbinning.bot.commands.response.MessageProcessor;

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
	public String getValue(MessageProcessor processor) {
		String cased = casify(value);
		String p = processor.processString(cased);
		return markup(p);
	}

	@Override
	public String toString() {
		return "TextFragment [" + value + "]";
	}

}
