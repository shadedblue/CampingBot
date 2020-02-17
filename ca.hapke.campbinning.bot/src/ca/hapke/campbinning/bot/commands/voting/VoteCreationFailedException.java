package ca.hapke.campbinning.bot.commands.voting;

/**
 * TODO Eliminate... Move strings into {@link VotingManager}
 * 
 * @author Nathan Hapke
 */
public class VoteCreationFailedException extends Exception {
	private static final long serialVersionUID = 60775625369182115L;
	public static final String NO_TOPIC_PROVIDED = "Reply to the topic you would like to vote on!";
	public static final String ALREADY_BEING_VOTED_ON = "Topic already being voted on";

	public VoteCreationFailedException(String reason) {
		super(reason);
	}

}
