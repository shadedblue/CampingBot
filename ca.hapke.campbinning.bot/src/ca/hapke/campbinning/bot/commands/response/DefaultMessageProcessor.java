package ca.hapke.campbinning.bot.commands.response;

/**
 * Does nothing
 * 
 * @author Nathan Hapke
 */
public class DefaultMessageProcessor extends MessageProcessor {

	@Override
	protected String internalProcessString(String value) {
		return value;
	}

}
