package ca.hapke.campbinning.bot.commands.response.fragments;

import ca.hapke.campbinning.bot.commands.response.MessageProcessor;

/**
 * @author Nathan Hapke
 */
public abstract class ResultFragment {
	public static final TextFragment SPACE = new TextFragment(" ");
	public static final TextFragment COLON_SPACE = new TextFragment(": ");
	public static final TextFragment NEWLINE = new TextFragment("\n");

	protected CaseChoice caseChoice;
	protected TextStyle textStyle;

	public ResultFragment() {
		this(CaseChoice.Normal, TextStyle.Normal);
	}

	public ResultFragment(CaseChoice caseChoice) {
		this(caseChoice, TextStyle.Normal);
	}

	public ResultFragment(TextStyle textStyle) {
		this(CaseChoice.Normal, textStyle);
	}

	public ResultFragment(CaseChoice caseChoice, TextStyle textStyle) {
		this.caseChoice = caseChoice;
		this.textStyle = textStyle;
	}

	public abstract String getValue(MessageProcessor processor);

	public String casify(String s) {
		switch (caseChoice) {
		case Normal:
			// noop
			break;
		case Lower:
			s = s.toLowerCase();
			break;
		case Upper:
			s = s.toUpperCase();
			break;

		}
		return s;
	}

	public String markup(String p) {
		return textStyle.getPre() + p + textStyle.getPost();
	}
}
