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
	protected TextStyle style;

	public ResultFragment(CaseChoice caseChoice, TextStyle style) {
		this.caseChoice = caseChoice;
		this.style = style;
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
		return style.getPre() + p + style.getPost();
	}
}
