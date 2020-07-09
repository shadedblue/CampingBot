package ca.hapke.campbinning.bot.commands.voting.rant;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campbinning.bot.CampingBotEngine;
import ca.hapke.campbinning.bot.commands.voting.VoteTracker;
import ca.hapke.campbinning.bot.commands.voting.VotingOption;
import ca.hapke.campbinning.bot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class RantTracker extends VoteTracker<Integer> {
	static final int NOT_QUORUM = 2;

	public RantTracker(CampingBotEngine bot, CampingUser ranter, CampingUser activater, Long chatId, Message activation,
			Message rant) throws TelegramApiException {
		super(bot, ranter, activater, chatId, activation, rant, NOT_QUORUM, RantCommand.RANT);
	}

	@Override
	protected boolean createOptions(List<VotingOption<Integer>> optionsList) {
		optionsList.add(new VotingOption<Integer>("+2", "Smoking Lady", 2));
		optionsList.add(new VotingOption<Integer>("+1", "Regular Complaining", 1));
		optionsList.add(new VotingOption<Integer>("0", "(yawn)", 0));
		optionsList.add(new VotingOption<Integer>("-1", "Pssshhh", -1));
		return true;
	}

	@Override
	public String getBannerTitle() {
		return "Rant Scoring!";
	}

	@Override
	public float getScore() {
		int count = votes.size() + votesNotApplicable.size();
		if (count == 0)
			return 0;

		int sum = 0;
		for (Integer v : votes.values()) {
			Integer pts = valueMap.get(v);
			sum += pts.intValue();
		}
		float avg = ((float) sum) / count;
		return avg;
	}

	@Override
	protected long getVotingTime() {
		return 45 * 60 * 1000;
	}

	@Override
	protected String getScoreSuffix() {
		return "/2";
	}

	@Override
	protected boolean shouldShowVotesInCategories() {
		return true;
	}
}
