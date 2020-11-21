package ca.hapke.campingbot.xml;

import java.util.List;

import ca.hapke.campingbot.category.CategoriedItems;

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
		indent();
		sb.append("<" + tag + ">");
		indent++;
		if (line)
			newLine();
	}

	public void finish(String tag) {
		finish(tag, true);
	}

	public void finish(String tag, boolean line) {
		indent--;
		if (line)
			indent();

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
				tagAndValue("item", a);
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

	public void tagAndValue(String tag, boolean value) {
		tagAndValue(tag, Boolean.toString(value));
	}

	public void tagAndValue(String tag, float value) {
		tagAndValue(tag, Float.toString(value));
	}

	public void tagCategories(CategoriedItems<String> categories) {
		for (String c : categories.getCategoryNames()) {
			tagAndValue(c, categories.getList(c));
		}
	}
}
