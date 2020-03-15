package ca.hapke.campbinning.bot.commands.response.fragments;

/**
 * @author Nathan Hapke
 */
public enum TextStyle {
	Normal(""),
	Bold("*"),
	Italic("_"),
	Underline("__"),
	Strikethrough("~");

	private TextStyle(String m) {
		markup = m;
	}

	public final String markup;
}
