package ca.hapke.campbinning.bot.xml;

import java.util.List;

/**
 * @author Nathan Hapke
 */
public class OutputFormatter {
	private StringBuilder sb = new StringBuilder();
	private int indent = 0;

	public String output() {
		return sb.toString();
	}

	public void start(String tag) {
		start(tag, true);
	}

	public void start(String tag, boolean line) {
		indent++;
		indent();
		sb.append("<" + tag + ">");
		if (line)
			newLine();
	}

	public void finish(String tag) {
		finish(tag, true);
	}

	public void finish(String tag, boolean line) {
		if (line)
			indent();
		indent--;

		sb.append("</" + tag + ">");
		newLine();
	}

	private void indent() {
		for (int i = 0; i < indent; i++) {
			sb.append("  ");
		}
	}

	public void newLine() {
		sb.append("\n");
	}

	public void tagAndValue(String tag, List<String> list) {
		String outerTag = tag + "s";
		start(outerTag);

		if (list != null) {
			for (String a : list) {
				tagAndValue(tag, a);
			}
		}
		finish(outerTag);
	}

	public void tagAndValue(String tag, String value) {
		start(tag, false);
		sb.append(value);
		finish(tag, false);
	}

	public void tagAndValue(String tag, long value) {
		tagAndValue(tag, Long.toString(value));
	}

	public void tagAndValue(String tag, int value) {
		tagAndValue(tag, Integer.toString(value));
	}

	public void tagAndValue(String tag, float value) {
		tagAndValue(tag, Float.toString(value));
	}
}
