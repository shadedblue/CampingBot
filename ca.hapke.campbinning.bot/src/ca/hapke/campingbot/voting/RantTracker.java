package ca.hapke.campingbot.voting;

import java.util.List;
import java.util.Map;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.api.CampingBotEngine;
import ca.hapke.campingbot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class RantTracker extends VoteTracker<Integer> {
	static final int NOT_QUORUM = 2;

	public RantTracker(CampingBotEngine bot, CampingUser ranter, CampingUser activater, Long chatId, Message activation,
			Message rant) throws TelegramApiException {
		super(bot, ranter, activater, chatId, activation, rant, NOT_QUORUM, RantCommand.RANT, true);
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
		Map<Integer, Integer> valueMap = cluster.getValueMap();
		return averageVoteValues(valueMap);
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
