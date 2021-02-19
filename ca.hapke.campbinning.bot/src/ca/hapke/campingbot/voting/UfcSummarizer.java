package ca.hapke.campingbot.voting;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.log.EventItem;
import ca.hapke.campingbot.log.EventLogger;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.EditTextCommandResult;
import ca.hapke.campingbot.response.SendResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.fragments.ResultFragment;
import ca.hapke.campingbot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class UfcSummarizer {

	private UfcFight fight;
	private Map<Integer, UfcTracker> roundToTrackerMap = new HashMap<>();
	private UpdateListener ul = new UpdateListener();
	private Message msg;
	private CampingBot bot;
	private Long chatId;

	public UfcSummarizer(UfcFight fight, CampingBot bot, Long chatId) {
		this.fight = fight;
		this.bot = bot;
		this.chatId = chatId;
	}

	private class UpdateListener implements VoteChangedListener<Integer> {
//
//		private int round;
//
//		public UpdateListener(int round) {
//			this.round = round;
//		}

		@Override
		public EventItem changed(CallbackQuery callbackQuery, CampingUser user, int optionId) {
			sendOrUpdate(user);
			return null;
		}

		@Override
		public EventItem confirmed(CallbackQuery callbackQuery, CampingUser user, int optionId) {
			return null;
		}

		@Override
		public EventItem completedByUser(CallbackQuery callbackQuery, CampingUser user, int optionId) {
			return null;
		}

		@Override
		public EventItem completedAutomatic() {
			return null;
		}

	}

	public void addTracker(int round, UfcTracker ufcTracker) {
		roundToTrackerMap.put(round, ufcTracker);
		ufcTracker.addListener(ul);
	}

	private class JudgingCard {
		int[] as, bs;

		private JudgingCard(int rounds) {
			as = new int[rounds];
			bs = new int[rounds];
		}
	}

	private JudgingCard getCard(Map<CampingUser, JudgingCard> judgingCards, CampingUser user) {
		JudgingCard card = judgingCards.get(user);
		if (card == null) {
			card = new JudgingCard(fight.rounds);
			judgingCards.put(user, card);
		}
		return card;
	}

	public void sendOrUpdate(CampingUser u) {
		CommandResult result;
		if (msg == null) {
			result = new TextCommandResult(UfcCommand.SlashUfcActivation);
		} else {
			result = new EditTextCommandResult(UfcCommand.SlashUfcActivation, msg);
		}

		result.add("Judging Summary");
		Map<CampingUser, JudgingCard> judgingCards = new HashMap<>();
		for (Entry<Integer, UfcTracker> e : roundToTrackerMap.entrySet()) {
			Integer round = e.getKey();
			UfcTracker tracker = e.getValue();
			VoteCluster<Integer> cluster = tracker.cluster;
			Map<CampingUser, Integer> votes = cluster.getVotes();
			for (Entry<CampingUser, Integer> voteEntry : votes.entrySet()) {
				CampingUser user = voteEntry.getKey();
				int vote = voteEntry.getValue();
				JudgingCard card = getCard(judgingCards, user);
				if (vote > 0) {
					card.as[round - 1] = 10 - vote;
					card.bs[round - 1] = 10;
				} else if (vote < 0) {
					card.as[round - 1] = 10;
					card.bs[round - 1] = 10 + vote;
				}
			}
		}
		for (Entry<CampingUser, JudgingCard> judgeAndCard : judgingCards.entrySet()) {
			CampingUser judge = judgeAndCard.getKey();
			JudgingCard card = judgeAndCard.getValue();
			int a = 0, b = 0;
			for (int i = 0; i < card.as.length; i++) {
				a += card.as[i];
				b += card.bs[i];
			}
			result.add(ResultFragment.NEWLINE);
			result.add(judge);
			result.add(" scores it: " + a + "-" + b);
		}

		try {
			SendResult sent = result.send(bot, chatId);
			if (msg == null) {
				msg = sent.outgoingMsg;
			}
			EventItem ei = new EventItem(UfcCommand.SlashUfcActivation, u, chatId, sent);
			EventLogger.getInstance().add(ei);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
