package ca.hapke.campbinning.bot.commands.response.fragments;

import ca.hapke.campbinning.bot.commands.response.MessageProcessor;

/**
 * @author Nathan Hapke
 */
public class TextFragment extends ResultFragment {
	private String value;

	public TextFragment(String value) {
		this.value = value;
	}

	@Override
	public String getValue(MessageProcessor processor) {
		return processor.processString(value);
	}

}
