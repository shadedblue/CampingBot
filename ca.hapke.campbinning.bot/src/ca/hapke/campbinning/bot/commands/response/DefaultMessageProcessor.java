package ca.hapke.campbinning.bot.commands.response;

/**
 * Does nothing
 * 
 * @author Nathan Hapke
 */
public class DefaultMessageProcessor extends MessageProcessor {

	@Override
	protected String internalProcessString(String value) {
		boolean upper = Math.random() < 0.5;
		if (upper)
			return value.toUpperCase();
		else
			return value.toLowerCase();
	}

}
