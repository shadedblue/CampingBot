package ca.hapke.campingbot.voting;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class RantCommand extends VotingCommand<Integer> {

	static final String RANT = "rant";
	private static final SlashCommandType SlashRantActivation = new SlashCommandType("RantActivation", RANT,
			BotCommandIds.RANT | BotCommandIds.VOTING | BotCommandIds.SET);

	public RantCommand(CampingBot campingBot) {
		super(campingBot, SlashRantActivation);
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
