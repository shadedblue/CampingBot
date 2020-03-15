package ca.hapke.campbinning.bot.commands.response;

/**
 * Does nothing
 * 
 * @author Nathan Hapke
 */
public class DefaultMessageProcessor extends MessageProcessor {

	@Override
	protected String internalProcessStringAssembled(String value) {
		return value;
	}

	@Override
	protected String internalProcessStringFragment(String value) {
		return value;
	}

	@Override
	protected String internalProcessImageUrl(String url) {
		return url;
	}

}
