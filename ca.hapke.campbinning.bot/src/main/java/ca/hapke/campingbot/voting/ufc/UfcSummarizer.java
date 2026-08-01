package ca.hapke.campingbot.voting.ufc;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.vdurmont.emoji.Emoji;

import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.Resources;
import ca.hapke.campingbot.log.EventItem;
import ca.hapke.campingbot.log.EventLogger;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.EditTextCommandResult;
import ca.hapke.campingbot.response.SendResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.UnpinUtil;
import ca.hapke.campingbot.response.fragments.ResultFragment;
import ca.hapke.campingbot.response.fragments.TextStyle;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.voting.VoteChangedAdapter;
import ca.hapke.campingbot.voting.VoteCluster;

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
	private Emoji checkEmoji;
	private Emoji xEmoji;

	public UfcSummarizer(UfcFight fight, CampingBot bot, Long chatId, Resources res) {
		this.fight = fight;
		this.bot = bot;
		this.chatId = chatId;

		checkEmoji = res.getCheck();
		xEmoji = res.getX();
	}

	private class UpdateListener extends VoteChangedAdapter<Integer> {
		@Override
		public EventItem changed(CallbackQuery callbackQuery, CampingUser user, long optionId) {
			if (roundToTrackerMap.size() >= fight.rounds) {
				sendOrUpdate(user);
			}
			return null;
		}
	}

	public void addTracker(int round, UfcTracker ufcTracker) {
		roundToTrackerMap.put(round, ufcTracker);
		ufcTracker.addListener(ul);
	}

	/**
	 * TODO give it ~15s in case of a mis-click?
	 */
	public void sendOrUpdate(CampingUser u) {
		CommandResult result;
		if (msg == null) {
			result = new TextCommandResult(UfcCommand.SlashUfcActivation);
		} else {
			result = new EditTextCommandResult(UfcCommand.SlashUfcActivation, msg);
		}

		// build the score-cards from scratch
		// TODO when a person votes for the first time, build their card then, and update it on the fly
		for (Entry<Integer, UfcTracker> e : roundToTrackerMap.entrySet()) {
			Integer round = e.getKey();
			UfcTracker tracker = e.getValue();
			VoteCluster<Integer> cluster = tracker.getCluster();
			Map<CampingUser, Long> votes = cluster.getVotes();
			for (Entry<CampingUser, Long> voteEntry : votes.entrySet()) {
				CampingUser user = voteEntry.getKey();
				long vote = voteEntry.getValue();
				fight.setVote(user, round, (int) vote);
			}
		}

		boolean completeJudging = fight.isVotingComplete();
//		System.out.println("Is the fight complete?" + completeJudging);
		if (completeJudging) {
			result.add("Ladies and Gentlemen, after " + fight.rounds
					+ " rounds, we go to the judges' scorecards for a decision...");
		} else {
			result.add("Judging In Progress...");
		}

		result.add(ResultFragment.NEWLINE);
		for (Entry<CampingUser, JudgingCard> judgeAndCard : fight.getJudgingCards().entrySet()) {
			CampingUser judge = judgeAndCard.getKey();
			JudgingCard card = judgeAndCard.getValue();
			result.add(judge);

			if (completeJudging) {
				int a = card.getATotal(), b = card.getBTotal();

				result.add(" scores it: " + a + "-" + b);
			} else {
				for (int round = 1; round <= card.getRounds(); round++) {
					if (card.hasVote(round)) {
						result.add(checkEmoji);
					} else {
						result.add(xEmoji);
					}
				}
			}
			result.add(ResultFragment.NEWLINE);
		}
		if (completeJudging) {
			PanelDecision overall = fight.getDecision();
			switch (overall) {
			case Incomplete:
				// Never happens
				break;
			case MajorityDraw:
				result.add("This fight is a ");
				result.add("MAJORITY DRAW", TextStyle.Bold);
				break;
			case Split:
			case Unanimous:
				result.add("For your winner by ");
				result.add(overall == PanelDecision.Split ? "SPLIT DECISION" : "UNANIMOUS DECISION", TextStyle.Bold);
				result.add(": ");
				result.add(fight.getWinner(), TextStyle.Bold);
				result.add("!");
				break;
			}

			for (UfcTracker t : roundToTrackerMap.values()) {
				t.complete();
			}
			UnpinUtil.unpinAll(bot, chatId);
		}

		try {
			SendResult sent = result.send(bot, chatId);
			if (msg == null) {
				msg = sent.outgoingMsg;
			}
			EventItem ei = new EventItem(UfcCommand.SlashUfcActivation, u, chatId, sent);
			EventLogger.getInstance().add(ei);
		} catch (TelegramApiException e) {
		}

	}

	public boolean hasFight(UfcFight fight) {
		return this.fight.equals(fight);

	}
}
