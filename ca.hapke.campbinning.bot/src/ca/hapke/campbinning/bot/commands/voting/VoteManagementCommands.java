package ca.hapke.campbinning.bot.commands.voting;

import org.telegram.telegrambots.meta.api.objects.Message;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.commands.AbstractCommand;
import ca.hapke.campbinning.bot.commands.SlashCommand;
import ca.hapke.campbinning.bot.response.CommandResult;
import ca.hapke.campbinning.bot.response.TextCommandResult;
import ca.hapke.campbinning.bot.response.fragments.InsultFragment;
import ca.hapke.campbinning.bot.response.fragments.TextFragment;
import ca.hapke.campbinning.bot.response.fragments.InsultFragment.Perspective;
import ca.hapke.campbinning.bot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class VoteManagementCommands extends AbstractCommand implements SlashCommand {
	private static final BotCommand[] SLASH_COMMANDS = new BotCommand[] { BotCommand.VoteExtend, BotCommand.VoteForceComplete };
	private static final TextFragment ONLY_BROUGHT_UP_OR_ACTIVATED = new TextFragment(
			"Only the person who started brought it up, or activated voting can use this, ");
	private static final TextFragment NO_VOTE_PROVIDED = new TextFragment("Reply to the vote, ");
	private static final TextFragment NOT_VOTING_ON_THAT = new TextFragment("We aren't voting on that, ");

	private VotingCommand[] commands;

	public VoteManagementCommands(VotingCommand... commands) {
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
			return new TextCommandResult(BotCommand.VoteCommandFailed, NO_VOTE_PROVIDED,
					new InsultFragment(Perspective.You)).setReplyTo(messageId);
		}

		VoteTracker tracker = findTracker(replyTo);
		if (tracker == null) {
			return new TextCommandResult(BotCommand.VoteCommandFailed, NOT_VOTING_ON_THAT,
					new InsultFragment(Perspective.You)).setReplyTo(messageId);
		}
		if (campingFromUser == tracker.activater || campingFromUser == tracker.ranter) {
			tracker.complete();
			return null;
		} else {

			return new TextCommandResult(BotCommand.VoteCommandFailed, ONLY_BROUGHT_UP_OR_ACTIVATED,
					new InsultFragment(Perspective.You)).setReplyTo(messageId);
		}
	}

	public CommandResult extendVoting(Message message, CampingUser campingFromUser) {
		Message replyTo = message.getReplyToMessage();
		Integer messageId = message.getMessageId();
		if (replyTo == null) {
			return new TextCommandResult(BotCommand.VoteCommandFailed, NO_VOTE_PROVIDED,
					new InsultFragment(Perspective.You)).setReplyTo(messageId);
		}

		VoteTracker tracker = findTracker(replyTo);
		if (tracker == null) {
			return new TextCommandResult(BotCommand.VoteCommandFailed, NOT_VOTING_ON_THAT,
					new InsultFragment(Perspective.You)).setReplyTo(messageId);
		}
		if (campingFromUser == tracker.activater || campingFromUser == tracker.ranter) {
			tracker.extend();
			tracker.update();
			return new TextCommandResult(BotCommand.VoteExtend, new TextFragment("10 minutes added!"))
					.setReplyTo(tracker.bannerMessage.getMessageId());
		} else {

			return new TextCommandResult(BotCommand.VoteCommandFailed, ONLY_BROUGHT_UP_OR_ACTIVATED,
					new InsultFragment(Perspective.You)).setReplyTo(messageId);
		}
	}

	public VoteTracker findTracker(Message replyTo) {
		VoteTracker tracker = null;
		Integer key = replyTo.getMessageId();
		for (VotingCommand command : commands) {
			tracker = command.voteOnMessages.get(key);
			if (tracker != null)
				break;
			tracker = command.voteOnBanners.get(key);
			if (tracker != null)
				break;

		}
		return tracker;
	}

	@Override
	public BotCommand[] getSlashCommandsToRespondTo() {
		return SLASH_COMMANDS;
	}

	@Override
	public CommandResult respondToSlashCommand(BotCommand command, Message message, Long chatId, CampingUser campingFromUser) {
		if (command == BotCommand.VoteForceComplete) {
			return completeVoting(message, campingFromUser);
		} else if (command == BotCommand.VoteExtend) {
			return extendVoting(message, campingFromUser);
		}
		return null;
	}

}
