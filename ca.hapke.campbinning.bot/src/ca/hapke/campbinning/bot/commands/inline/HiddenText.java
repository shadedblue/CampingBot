package ca.hapke.campbinning.bot.commands.inline;

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

	public String getBlotText() {
		return blotText;
	}

	public String getClearText() {
		return clearText;
	}

}