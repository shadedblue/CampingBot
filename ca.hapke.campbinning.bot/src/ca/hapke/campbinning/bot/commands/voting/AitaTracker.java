package ca.hapke.campbinning.bot.commands.voting;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.calendaring.timing.ByTimeOfYear;
import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBotEngine;
import ca.hapke.campbinning.bot.category.CategoriedItems;
import ca.hapke.campbinning.bot.commands.ImageLink;
import ca.hapke.campbinning.bot.commands.MbiyfCommand;
import ca.hapke.campbinning.bot.commands.MbiyfMode;
import ca.hapke.campbinning.bot.commands.MbiyfType;
import ca.hapke.campbinning.bot.commands.response.CommandResult;
import ca.hapke.campbinning.bot.commands.response.ImageCommandResult;
import ca.hapke.campbinning.bot.commands.response.fragments.ResultFragment;
import ca.hapke.campbinning.bot.commands.response.fragments.TextFragment;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.util.CampingUtil;

/**
 * @author Nathan Hapke
 */
public class AitaTracker extends VoteTracker<Float> {
	private static final String ASSHOLE_IMAGES = "assholeImages";
	private static final int ESH_THRESHOLD = 40;
	private static final int YTA_THRESHOLD = 66;
	static final int NOT_QUORUM = 2;
	public static final String[] assholeLevels = new String[] { "asshole", "mediocre", "nice" };
	private CategoriedItems<String> resultTexts;
	private CategoriedItems<ImageLink> resultImages;
	private List<ImageLink> assholeImages;
	private MbiyfCommand ballsCommand;

	public AitaTracker(CampingBotEngine bot, CampingUser ranter, Long chatId, Message activation, Message topic,
			CategoriedItems<String> resultCategories, MbiyfCommand ballsCommand) throws TelegramApiException {
		super(bot, ranter, ranter, chatId, activation, topic, NOT_QUORUM);

		this.resultTexts = resultCategories;
		this.ballsCommand = ballsCommand;
		this.resultImages = new CategoriedItems<ImageLink>(ASSHOLE_IMAGES);
		assholeImages = resultImages.getList(ASSHOLE_IMAGES);
		for (int i = 1; i <= 7; i++) {
			String url = "http://www.hapke.ca/images/asshole" + i + ".mp4";
			ImageLink lnk = new ImageLink(url, ImageLink.GIF);
			assholeImages.add(lnk);
		}
	}

	@Override
	protected boolean createOptions(List<VotingOption<Float>> optionsList) {
		optionsList.add(new VotingOption<Float>("YTA", "You're The Asshole", 1f));
		optionsList.add(new VotingOption<Float>("ESH", "Everybody Sucks Here", 0.5f));
		optionsList.add(new VotingOption<Float>("NTA", "Not The Asshole (... unlikely)", 0f));
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
		return 45 * 60 * 1000;
	}

	@Override
	protected String getScoreSuffix() {
		return "%";
	}

	@Override
	public void addVotesTextSuffix(List<ResultFragment> sb, boolean completed, float score) {
		if (completed) {
			List<String> category;
			int i;
			if (score >= YTA_THRESHOLD) {
				i = 0;
			} else if (score >= ESH_THRESHOLD) {
				i = 1;
			} else {
				i = 2;
			}
			category = resultTexts.getList(assholeLevels[i]);
			String response = CampingUtil.getRandom(category);
			if (response != null && response.length() > 0) {
				sb.add(new TextFragment("\n\n"));
				sb.add(new TextFragment(response));
			}
		}
	}

	@Override
	public CommandResult createCompletionResult() {
		if (getScore() >= YTA_THRESHOLD) {
			List<ResultFragment> votes = getVotesText(true);
			ImageCommandResult icr = new ImageCommandResult(BotCommand.VoteTopicComplete,
					CampingUtil.getRandom(assholeImages), votes);

			MbiyfMode enable = new MbiyfMode(MbiyfType.Asshole, Collections.singletonList(ranter));
			ballsCommand.doWork(enable);
			ZonedDateTime enableTime = ZonedDateTime.now();
			ByTimeOfYear<MbiyfMode> disableEvent = ballsCommand.createDisableAfter(enableTime, 1, ChronoUnit.HOURS); 
			disableEvent.setRepeats(1);
			ballsCommand.getTimeProvider().add(disableEvent);
			return icr;
		}
		return super.createCompletionResult();
	}

	@Override
	protected boolean shouldShowVotesInCategories() {
		return true;
	}

}
