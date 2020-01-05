package ca.hapke.campbinning.bot.commands.voting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campbinning.bot.CampingBotEngine;
import ca.hapke.campbinning.bot.category.CategoriedItems;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.util.CampingUtil;

/**
 * @author Nathan Hapke
 */
public class AitaTracker extends VoteTracker<String> {
	private static final String YTA = "YTA";
	private static final String ESH = "ESH";
	private static final String NTA = "NTA";
	private static final String N_A = "N/A";
	static final String[] buttonText = new String[] { YTA, ESH, NTA, N_A };
	static final String[] fullText = new String[] { "You're The Asshole", "Everybody Sucks Here",
			"Not The Asshole (... unlikely)", "Not Applicable" };
	static final String[] buttonValue = new String[] { YTA, ESH, NTA, NOT_APPLICABLE };
	static final int NOT_QUORUM = 2;
	public static final String[] assholeLevels = new String[] { "asshole", "mediocre", "nice" };
	private Map<String, Float> valueMap;
	private CategoriedItems<String> resultCategories;

	public AitaTracker(CampingBotEngine bot, CampingUser ranter, CampingUser activater, Long chatId, Message activation,
			Message topic, CategoriedItems<String> resultCategories) throws TelegramApiException {
		super(bot, ranter, activater, chatId, activation, topic, buttonText, buttonValue, NOT_QUORUM);
		this.resultCategories = resultCategories;

		valueMap = new HashMap<>();
		valueMap.put(YTA, 1f);
		valueMap.put(ESH, 0.5f);
		valueMap.put(NTA, 0f);
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
	public String getBannerText() {
		StringBuilder sb = new StringBuilder();
		sb.append("Am I The Asshole? (");
		sb.append(formatter.toPrettyString(completionTime));
		sb.append(" left)");
		for (int i = 0; i < buttonText.length && i < fullText.length; i++) {
			String shorter = buttonText[i];
			String longer = fullText[i];

			sb.append("\n*");
			sb.append(shorter);
			sb.append("*: ");
			sb.append(longer);
		}
		return sb.toString();
	}

	@Override
	protected long getVotingTime() {
		return 10 * 60 * 1000;
	}

	@Override
	protected String processVote(String vote) {
		return vote;
	}

	@Override
	protected String makeStringFromVote(String vote) {
		return vote;
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
