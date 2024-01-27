package ca.hapke.campingbot.voting;

import org.telegram.telegrambots.meta.api.objects.Message;

import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.SlashCommand;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.fragments.InsultFragment;
import ca.hapke.campingbot.response.fragments.InsultFragment.Perspective;
import ca.hapke.campingbot.response.fragments.TextFragment;
import ca.hapke.campingbot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class VoteManagementCommands extends AbstractCommand implements SlashCommand {
	private static final SlashCommandType SlashVoteExtend = new SlashCommandType("VoteExtend", "extend",
			BotCommandIds.VOTING | BotCommandIds.FINISH);
	private static final SlashCommandType SlashVoteForceComplete = new SlashCommandType("VoteForceComplete", "complete",
			BotCommandIds.VOTING | BotCommandIds.USE);
	private static final SlashCommandType[] SLASH_COMMANDS = new SlashCommandType[] { SlashVoteExtend,
			SlashVoteForceComplete };
	private static final TextFragment ONLY_BROUGHT_UP_OR_ACTIVATED = new TextFragment(
			"Only the person who started brought it up, or activated voting can use this, ");
	private static final TextFragment NO_VOTE_PROVIDED = new TextFragment("Reply to the vote, ");
	private static final TextFragment NOT_VOTING_ON_THAT = new TextFragment("We aren't voting on that, ");

	private VotingCommand<?>[] commands;

	public VoteManagementCommands(VotingCommand<?>... commands) {
		this.commands = commands;
	}

	@Override
	public String getCommandName() {
		return "complete";
	}

	public CommandResult completeVoting(Message message, CampingUser campingFromUser) {
		Message replyTo = message.getReplyToMessage();
		Integer messageId = message.getMessageId();
		if (replyTo == null) {
			return new TextCommandResult(VotingCommand.VoteCommandFailedCommand, NO_VOTE_PROVIDED,
					new InsultFragment(Perspective.You)).setReplyTo(messageId);
		}

		VoteTracker<?> tracker = findTracker(replyTo);
		if (tracker == null) {
			return new TextCommandResult(VotingCommand.VoteCommandFailedCommand, NOT_VOTING_ON_THAT,
					new InsultFragment(Perspective.You)).setReplyTo(messageId);
		}
		if (campingFromUser == tracker.activater || campingFromUser == tracker.ranter) {
			tracker.complete();
			return null;
		} else {

			return new TextCommandResult(VotingCommand.VoteCommandFailedCommand, ONLY_BROUGHT_UP_OR_ACTIVATED,
					new InsultFragment(Perspective.You)).setReplyTo(messageId);
		}
	}

	public CommandResult extendVoting(Message message, CampingUser campingFromUser) {
		Message replyTo = message.getReplyToMessage();
		Integer messageId = message.getMessageId();
		if (replyTo == null) {
			return new TextCommandResult(VotingCommand.VoteCommandFailedCommand, NO_VOTE_PROVIDED,
					new InsultFragment(Perspective.You)).setReplyTo(messageId);
		}

		VoteTracker<?> tracker = findTracker(replyTo);
		if (tracker == null) {
			return new TextCommandResult(VotingCommand.VoteCommandFailedCommand, NOT_VOTING_ON_THAT,
					new InsultFragment(Perspective.You)).setReplyTo(messageId);
		}
		if (campingFromUser == tracker.activater || campingFromUser == tracker.ranter) {
			tracker.extend();
			tracker.update();
			return new TextCommandResult(SlashVoteExtend, new TextFragment("10 minutes added!"))
					.setReplyTo(tracker.bannerMessage.getMessageId());
		} else {

			return new TextCommandResult(VotingCommand.VoteCommandFailedCommand, ONLY_BROUGHT_UP_OR_ACTIVATED,
					new InsultFragment(Perspective.You)).setReplyTo(messageId);
		}
	}

	public VoteTracker<?> findTracker(Message replyTo) {
		VoteTracker<?> tracker = null;
		Integer id = replyTo.getMessageId();
		String key = Integer.toString(id);
		for (VotingCommand<?> command : commands) {
			tracker = command.voteOnMessages.get(key);
			if (tracker != null)
				break;
			tracker = command.voteOnBanners.get(id);
			if (tracker != null)
				break;

		}
		return tracker;
	}

	@Override
	public SlashCommandType[] getSlashCommandsToRespondTo() {
		return SLASH_COMMANDS;
	}

	@Override
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, Long chatId,
			CampingUser campingFromUser) {
		if (command == SlashVoteForceComplete)
			return completeVoting(message, campingFromUser);
		if (command == SlashVoteExtend)
			return extendVoting(message, campingFromUser);
		return null;
	}
}
