package ca.hapke.campingbot.processors;

/**
 * Escapes required characters for Telegram MarkupV2 format
 * 
 * @author Nathan Hapke
 */
public class DefaultMessageProcessor extends MessageProcessor {

	public DefaultMessageProcessor() {
		super(true);
	}

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
}
