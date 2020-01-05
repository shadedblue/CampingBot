package ca.hapke.campbinning.bot.commands.voting;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campbinning.bot.CampingBotEngine;
import ca.hapke.campbinning.bot.category.CategoriedItems;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.util.CampingUtil;

/**
 * @author Nathan Hapke
 */
public class AitaTracker extends VoteTracker<Float> {
	static final int NOT_QUORUM = 2;
	public static final String[] assholeLevels = new String[] { "asshole", "mediocre", "nice" };
	private CategoriedItems<String> resultCategories;

	public AitaTracker(CampingBotEngine bot, CampingUser ranter, CampingUser activater, Long chatId, Message activation,
			Message topic, CategoriedItems<String> resultCategories) throws TelegramApiException {
		super(bot, ranter, activater, chatId, activation, topic, NOT_QUORUM);

		this.resultCategories = resultCategories;
	}

	@Override
	protected boolean createOptions(List<VotingOption<Float>> optionsList) {
		optionsList.add(new VotingOption<Float>("YTA", "You're The Asshole", 1f));
		optionsList.add(new VotingOption<Float>("ESH", "Everybody Sucks Here", 0.5f));
		optionsList.add(new VotingOption<Float>("NTA", "You're The Asshole", 0f));
		return true;
	}

	@Override
	public float getScore() {
		int notApplicable = votesNotApplicable.size();
		int count = votes.size() + notApplicable;

		float score = 0;
		for (String v : votes.values()) {
			Float pts = valueMap.get(v);
			if (pts != null)
				score += pts.floatValue();
		}
		return score * 100f / count;
	}

	@Override
	public String getBannerTitle() {
		return "Am I The Asshole?";
	}

	@Override
	protected long getVotingTime() {
		return 10 * 60 * 1000;
	}

	@Override
	protected String getScoreSuffix() {
		return "%";
	}

	@Override
	public void addVotesTextSuffix(StringBuilder sb, boolean completed, float score) {
		if (completed) {
			List<String> category;
			int i;
			if (score >= 75) {
				i = 0;
			} else if (score >= 40) {
				i = 1;
			} else {
				i = 2;
			}
			category = resultCategories.getList(assholeLevels[i]);
			String response = CampingUtil.getRandom(category);
			if (response != null && response.length() > 0) {
				sb.append("\n\n");
				sb.append(response);
			}
		}
	}

	@Override
	protected boolean shouldShowVotesInCategories() {
		return true;
	}

}
