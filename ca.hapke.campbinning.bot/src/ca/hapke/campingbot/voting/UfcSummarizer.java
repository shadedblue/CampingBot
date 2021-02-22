package ca.hapke.campingbot.voting;

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
		public EventItem changed(CallbackQuery callbackQuery, CampingUser user, int optionId) {
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

	public void sendOrUpdate(CampingUser u) {
		CommandResult result;
		if (msg == null) {
			result = new TextCommandResult(UfcCommand.SlashUfcActivation);
		} else {
			result = new EditTextCommandResult(UfcCommand.SlashUfcActivation, msg);
		}

		result.add("Judging Summary");
		for (Entry<Integer, UfcTracker> e : roundToTrackerMap.entrySet()) {
			Integer round = e.getKey();
			UfcTracker tracker = e.getValue();
			VoteCluster<Integer> cluster = tracker.cluster;
			Map<CampingUser, Integer> votes = cluster.getVotes();
			for (Entry<CampingUser, Integer> voteEntry : votes.entrySet()) {
				CampingUser user = voteEntry.getKey();
				int vote = voteEntry.getValue();
				fight.setVote(user, round, vote);
			}
		}

		boolean completeJudging = fight.isVotingComplete();

		for (Entry<CampingUser, JudgingCard> judgeAndCard : fight.getJudgingCards().entrySet()) {
			CampingUser judge = judgeAndCard.getKey();
			JudgingCard card = judgeAndCard.getValue();
			result.add(ResultFragment.NEWLINE);
			result.add(judge);
			if (completeJudging) {
				int a = 0, b = 0;
				for (int round = 1; round <= card.getRounds(); round++) {
					a += card.getA(round);
					b += card.getB(round);
				}
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
}
