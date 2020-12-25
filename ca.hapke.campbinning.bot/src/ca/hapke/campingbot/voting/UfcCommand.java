package ca.hapke.campingbot.voting;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.log.EventItem;
import ca.hapke.campingbot.log.EventLogger;
import ca.hapke.campingbot.users.CampingUser;

/**
 * TODO change currentRound to a Map<MessageId -> UfcRound>
 * 
 * @author Nathan Hapke
 */
public class UfcCommand extends VotingCommand<Integer> {
	private class CreateNextTracker implements VoteChangedListener<Integer> {
		private boolean firstAction = false;

		@Override
		public EventItem changed(int optionId, CallbackQuery callbackQuery, CampingUser user) {
			if (firstAction == false) {
				firstAction = true;
				new DelayThenCreate(currentRound.getRound() < currentRound.getRounds()).start();
			}
			return null;
		}

		@Override
		public EventItem confirmed(int optionId, CallbackQuery callbackQuery, CampingUser user) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public EventItem completedByUser(int optionId, CallbackQuery callbackQuery, CampingUser user) {
			new DelayThenCreate(false).start();
			return null;
		}

		@Override
		public EventItem completedAutomatic() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	private class DelayThenCreate extends Thread {
		private boolean nextRound;

		public DelayThenCreate(boolean nextRound) {
			this.nextRound = nextRound;
		}

		@Override
		public void run() {
			Integer topicId = topic.getMessageId();
			try {
				Thread.sleep(10 * 1000);
//				boolean nextRound = currentRound.getRound() < currentRound.getRounds();
				createNextTracker(topicId);
			} catch (Exception e) {
				EventLogger.getInstance().add(new EventItem(SlashUfcActivation, activater, chatId, topicId,
						"Could not track next round: ", e.getLocalizedMessage()));
			}
		}

		protected void createNextTracker(Integer topicId) throws TelegramApiException {
			if (nextRound) {
				currentRound = new UfcTracker(currentRound);
			} else {
				ticketIndex++;
				UfcFight fight = ticket.get(ticketIndex);
				currentRound = new UfcTracker(bot, ranter, activater, chatId, activation, topic, fight, 1);
			}
			currentRound.addListener(new CreateNextTracker());
			String key = createKey(topicId);
			addTracker(currentRound, key);
		}
	}

	static final String UFC_COMMAND = "ufc";

	private static final SlashCommandType SlashUfcActivation = new SlashCommandType("UfcActivation", UFC_COMMAND,
			BotCommandIds.VOTING | BotCommandIds.SET);

	private List<UfcFight> ticket = new ArrayList<>();
	private int ticketIndex = 0;
	private UfcTracker currentRound;

	private CampingUser ranter;
	private CampingUser activater;
	private Message topic;
	private Message activation;
	private Long chatId;

	public UfcCommand(CampingBot campingBot) {
		super(campingBot, SlashUfcActivation);
	}

	@Override
	protected VoteTracker<Integer> initiateVote(CampingUser ranter, CampingUser activater, Long chatId,
			Message activation, Message topic) throws VoteInitiationException, TelegramApiException {
		this.ranter = ranter;
		this.activater = activater;
		this.chatId = chatId;
		this.activation = activation;
		this.topic = topic;
		try {
			String input = topic.getText();
			String[] lines = input.split("\n");
//			for (String line : lines) {
			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];
				String[] parts = line.split("-");
				String a = parts[0].trim();
				String b = parts[1].trim();
				int rounds = Integer.parseInt(parts[2].trim());
				ticket.add(new UfcFight(i, a, b, rounds));
			}

			UfcFight first = ticket.get(0);
			currentRound = createTracker(first, 1);
			return currentRound;
		} catch (Exception e) {
			throw new VoteInitiationException("Give the 'FighterA - FigherB - Rounds' syntax");
		}
	}

	private UfcTracker createTracker(UfcFight first, int round) throws TelegramApiException {
		UfcTracker firstVote = new UfcTracker(bot, ranter, activater, chatId, activation, topic, first, round);
		firstVote.addListener(new CreateNextTracker());
		return firstVote;
	}

	@Override
	public String getCommandName() {
		return UFC_COMMAND;
	}

	@Override
	protected String createKey(int messageId) {
		return messageId + AbstractCommand.DELIMITER + currentRound.getRound() + AbstractCommand.DELIMITER
				+ currentRound.getRounds();
	}

}
