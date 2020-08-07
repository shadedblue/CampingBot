package ca.hapke.campbinning.bot.commands.response;

import ca.hapke.campbinning.bot.commands.response.fragments.ResultFragment;

/**
 * Escapes required characters for Telegram MarkupV2 format
 * 
 * @author Nathan Hapke
 */
public class DefaultMessageProcessor extends MessageProcessor {

	private final static String[] escapeFrom = new String[] { "_", "*", "[", "]", "(", ")", "~", "`", ">", "#", "+",
			"-", "=", "|", "{", "}", ".", "!" };
	private final static String[] escapeTo;
	static {
		escapeTo = new String[escapeFrom.length];
		for (int i = 0; i < escapeFrom.length; i++) {
			String from = escapeFrom[i];
			String result = "\\" + from;
			escapeTo[i] = result;
		}
	}

	@Override
	protected ResultFragment[] internalBeforeStringAssembled(ResultFragment[] fragments) {
		return fragments;
	}

	@Override
	protected String internalProcessStringFragment(String value, boolean useMarkupV2) {
		if (useMarkupV2) {
			for (int i = 0; i < escapeFrom.length; i++) {
				String from = escapeFrom[i];
				String to = escapeTo[i];

				value = value.replace(from, to);
			}
		}
		return value;
	}

	@Override
	protected String internalAfterStringAssembled(String value) {
		return value;
	}

	@Override
	protected String internalProcessImageUrl(String url) {
		return url;
	}
}
