package ca.hapke.campingbot.response.fragments;

/**
 * @author Nathan Hapke
 */
public enum TextStyle {
	Normal(""),
	Bold("*"),
	Italic("_"),
	Underline("__"),
	Strikethrough("~"),
	Preformatted("```\n", "\n```");

	private String pre;
	private String post;

	private TextStyle(String m) {
		this(m, m);
	}

	private TextStyle(String pre, String post) {
		this.pre = pre;
		this.post = post;
	}

	protected String getPre() {
		return pre;
	}

	protected String getPost() {
		return post;
	}

}
