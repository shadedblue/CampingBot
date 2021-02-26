package ca.hapke.campingbot.voting.ufc;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.Resources;
import ca.hapke.campingbot.callback.api.CallbackId;
import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.log.EventItem;
import ca.hapke.campingbot.log.EventLogger;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.voting.VoteChangedAdapter;
import ca.hapke.campingbot.voting.VoteInitiationException;
import ca.hapke.campingbot.voting.VoteTracker;
import ca.hapke.campingbot.voting.VotingCommand;

/**
 * @author Nathan Hapke
 */
public class UfcCommand extends VotingCommand<Integer> {
	private class CreateNextTracker extends VoteChangedAdapter<Integer> {
		private boolean firstAction = false;

		@Override
		public EventItem changed(CallbackQuery callbackQuery, CampingUser user, int optionId) {
			if (firstAction == false) {
				firstAction = true;
				new DelayThenCreate(currentRound.getRound() < currentRound.getRounds()).start();
			}
			return null;
		}

		@Override
		public EventItem completedByUser(CallbackQuery callbackQuery, CampingUser user, int optionId) {
			new DelayThenCreate(false).start();
			return null;
		}

	}

	private class DelayThenCreate extends Thread {
		private static final int DELAY_BETWEEN_ROUNDS_SEC = 5 * 60 + 30;
//		private static final int DELAY_BETWEEN_ROUNDS_SEC = 20;
		private boolean nextRound;

		public DelayThenCreate(boolean nextRound) {
			this.nextRound = nextRound;
		}

		@Override
		public void run() {
			Integer topicId = topic.getMessageId();
			try {
				Thread.sleep(DELAY_BETWEEN_ROUNDS_SEC * 1000);
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
				summarizer = new UfcSummarizer(fight, bot, chatId, res);
				currentRound = new UfcTracker(bot, ranter, activater, chatId, activation, topic, fight, 1, summarizer);
			}
			currentRound.addListener(new CreateNextTracker());
			addTracker(currentRound);
		}
	}

	static final String UFC_COMMAND = "ufc";
	static final String READY_COMMAND = "ready";

	static final SlashCommandType SlashUfcActivation = new SlashCommandType("UfcActivation", UFC_COMMAND,
			BotCommandIds.VOTING | BotCommandIds.SET);
//	private static final SlashCommandType SlashReadyUfc = new SlashCommandType("ReadyUfc", READY_COMMAND,
//			BotCommandIds.VOTING | BotCommandIds.SET);

	private List<UfcFight> ticket = new ArrayList<>();
	private int ticketIndex = 0;
	private UfcSummarizer summarizer;
	private UfcTracker currentRound;

	private CampingUser ranter;
	private CampingUser activater;
	private Message topic;
	private Message activation;
	private Long chatId;
	private Resources res;

	public UfcCommand(CampingBot campingBot, Resources res) {
		super(campingBot, SlashUfcActivation);
		this.res = res;
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

	private UfcTracker createTracker(UfcFight fight, int round) throws TelegramApiException {
		summarizer = new UfcSummarizer(fight, bot, chatId, res);
		UfcTracker firstVote = new UfcTracker(bot, ranter, activater, chatId, activation, topic, fight, round,
				summarizer);
		firstVote.addListener(new CreateNextTracker());
		return firstVote;
	}

	@Override
	public String getCommandName() {
		return UFC_COMMAND;
	}

	/**
	 * TopicMessageId:Fight#:Round#
	 */
	@Override
	protected String createKey(CallbackId id) {
		int messageId = id.getUpdateId();
		int[] ids = id.getIds();
		int fightId = ids[0];
		int round = ids[1];
		return messageId + AbstractCommand.DELIMITER + fightId + AbstractCommand.DELIMITER + round;
	}

}
