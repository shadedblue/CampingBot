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
public class UfcCommand extends VotingCommand<Integer> {

	static final String UFC_COMMAND = "ufc";

	private static final SlashCommandType SlashUfcActivation = new SlashCommandType("UfcActivation", UFC_COMMAND,
			BotCommandIds.VOTING | BotCommandIds.SET);

	public UfcCommand(CampingBot campingBot) {
		super(campingBot, SlashUfcActivation);
	}

	@Override
	protected VoteTracker<Integer> initiateVote(CampingUser ranter, CampingUser activater, Long chatId,
			Message activation, Message fight) throws VoteInitiationException, TelegramApiException {
		String[] parts = fight.getText().split("-");
		if (parts.length < 3) {
			throw new VoteInitiationException("Give the 'FighterA - FigherB - Rounds' syntax");
		}
		int rounds = Integer.parseInt(parts[2].trim());
		String a = parts[0].trim();
		String b = parts[1].trim();
		return new UfcTracker(bot, ranter, activater, chatId, activation, fight, a, b, 1, rounds);
	}

	@Override
	public String getCommandName() {
		return UFC_COMMAND;
	}

}
