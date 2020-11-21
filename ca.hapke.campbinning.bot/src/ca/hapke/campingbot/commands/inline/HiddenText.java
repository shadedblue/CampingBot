package ca.hapke.campingbot.commands.inline;

/**
 * 
 * @author Nathan Hapke
 */
public class HiddenText {
	private String topic;
	private String clearText;
	private String blotText;

	public HiddenText(String topic, String clearText, String blotText) {
		this.topic = topic;
		this.clearText = clearText;
		this.blotText = blotText;
	}

	public String getTopic() {
		return topic;
	}

	public String getClearText() {
		return clearText;
	}

	public String getBlotText() {
		return blotText;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HiddenText [");
		if (topic != null) {
			builder.append("topic=");
			builder.append(topic);
			builder.append(" -- ");
		}
		if (clearText != null) {
			builder.append(clearText);
			builder.append(" => ");
		}
		if (blotText != null) {
			builder.append(blotText);
		}
		builder.append("]");
		return builder.toString();
	}
}