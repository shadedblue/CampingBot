package ca.hapke.campbinning.bot.commands.voting;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campbinning.bot.CampingBotEngine;
import ca.hapke.campbinning.bot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class RantTracker extends VoteTracker<Integer> {
	static final String[] rantButtonText = new String[] { "-1", "0", "+1", "+2", "Not Rant" };
	static final String[] rantButtonValue = new String[] { "-1", "0", "1", "2", NOT_APPLICABLE };
	static final int NOT_QUORUM = 2;

	public RantTracker(CampingBotEngine bot, CampingUser ranter, CampingUser activater, Long chatId, Message activation,
			Message rant) throws TelegramApiException {
		super(bot, ranter, activater, chatId, activation, rant, rantButtonText, rantButtonValue, NOT_QUORUM);
	}

	@Override
	public String getBannerText() {
		return "Rant Scoring! (" + formatter.toPrettyString(completionTime) + " left)";
	}



	@Override
	public float getScore() {
		int count = votes.size() + votesNotApplicable.size();
		if (count == 0)
			return 0;

		int sum = 0;
		for (int x : votes.values()) {
			sum += x;
		}
		float avg = ((float) sum) / count;
		return avg;
	}


	@Override
	protected long getVotingTime() {
		return 10 * 60 * 1000;
	}

	@Override
	protected Integer processVote(String vote) {
		return Integer.parseInt(vote);
	}

	@Override
	protected String makeStringFromVote(Integer vote) {
		return vote.toString();
	}

	@Override
	protected String getScoreSuffix() {
		return "/2";
	}

	@Override
	protected boolean shouldShowVotesInCategories() {
		return false;
	}
}
