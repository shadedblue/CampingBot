package ca.hapke.campbinning.bot.commands.voting.rant;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBot;
import ca.hapke.campbinning.bot.commands.voting.VoteInitiationException;
import ca.hapke.campbinning.bot.commands.voting.VoteTracker;
import ca.hapke.campbinning.bot.commands.voting.VotingCommand;
import ca.hapke.campbinning.bot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class RantCommand extends VotingCommand<Integer> {

	static final String RANT = "rant";

	public RantCommand(CampingBot campingBot) {
		super(campingBot, BotCommand.RantActivatorInitiation);
	}

	@Override
	public String getCommandName() {
		return RANT;
	}

	@Override
	protected VoteTracker<Integer> initiateVote(CampingUser ranter, CampingUser activater, Long chatId,
			Message activation, Message topic) throws VoteInitiationException, TelegramApiException {
		return new RantTracker(bot, ranter, activater, chatId, activation, topic);
	}

}
