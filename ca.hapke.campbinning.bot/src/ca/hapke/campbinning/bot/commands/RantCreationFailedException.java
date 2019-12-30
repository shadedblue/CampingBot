package ca.hapke.campbinning.bot.commands;

/**
 * @author Nathan Hapke
 *
 */
public class RantCreationFailedException extends Exception {
	public static final String NO_RANT_PROVIDED = "Reply to the rant you would like to vote on!";
	public static final String ALREADY_BEING_VOTED_ON = "Rant already being voted on";

	public RantCreationFailedException(String reason) {
		super(reason);
	}


}
