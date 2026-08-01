package ca.hapke.campingbot.voting.ufc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.api.CampingBotEngine;
import ca.hapke.campingbot.callback.api.CallbackId;
import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.response.fragments.ResultFragment;
import ca.hapke.campingbot.response.fragments.TextFragment;
import ca.hapke.campingbot.response.fragments.TextStyle;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.voting.VoteTracker;
import ca.hapke.campingbot.voting.VotingOption;

/**
 * @author Nathan Hapke
 */
public class UfcTracker extends VoteTracker<Integer> {
	static final int TEN_EIGHT = -2;
	static final int TEN_NINE = -1;
	static final int NINE_TEN = 1;
	static final int EIGHT_TEN = 2;

	private String a;
	private String b;
	private int round;
	private int rounds;
	private UfcFight fight;
	private Message msg;
	private UfcSummarizer summarizer;

	public UfcTracker(CampingBotEngine bot, CampingUser ranter, CampingUser activater, Long chatId, Message activation,
			Message msg, UfcFight fight, int round, UfcSummarizer summarizer) throws TelegramApiException {
		super(bot, ranter, activater, chatId, activation, msg, 2, UfcCommand.UFC_COMMAND, false);
		this.msg = msg;
		this.fight = fight;
		this.summarizer = summarizer;
		this.a = fight.a;
		this.b = fight.b;
		this.round = round;
		this.rounds = fight.rounds;

		summarizer.addTracker(round, this);
	}

	/**
	 * Advance to next round
	 */
	public UfcTracker(UfcTracker previousRound) throws TelegramApiException {
		this(previousRound.bot, previousRound.ranter, previousRound.activater, previousRound.chatId,
				previousRound.activation, previousRound.topic, previousRound.fight, previousRound.round + 1,
				previousRound.summarizer);
	}

	@Override
	protected boolean createOptions(List<VotingOption<Integer>> optionsList) {
		optionsList.add(new VotingOption<Integer>("10-8", "10-8 " + a, TEN_EIGHT));
		optionsList.add(new VotingOption<Integer>("10-9", "10-9 " + a, TEN_NINE));
		optionsList.add(new VotingOption<Integer>("9-10", "10-9 " + b, NINE_TEN));
		optionsList.add(new VotingOption<Integer>("8-10", "10-8 " + b, EIGHT_TEN));
		return true;
	}

	@Override
	public List<ResultFragment> getBannerString() {
		List<ResultFragment> sb = new ArrayList<>();
		sb.add(new TextFragment(a, TextStyle.Bold));
		sb.add(new TextFragment(" vs. "));
		sb.add(new TextFragment(b, TextStyle.Bold));
		sb.add(new TextFragment("\nRound " + round + "/" + rounds));
		return sb;
	}

	@Override
	protected CallbackId createCallbackId(String command, int updateId, long i) {
		return new CallbackId(command, updateId, fight.fightNumber, round, i);
	}

	@Override
	public float getScore() {
		// TODO Auto-generated method stub
		Map<Long, Integer> valueMap = cluster.getValueMap();
		return averageVoteValues(valueMap);
	}

	@Override
	public String getBannerTitle() {
		return "FIGHT!";
	}

	@Override
	protected boolean shouldShowVotesInCategories() {
		return true;
	}

	@Override
	protected String getScoreSuffix() {
		return "";
	}

	public int getRound() {
		return round;
	}

	public int getRounds() {
		return rounds;
	}

	@Override
	protected String createClusterKey() {
		return fight.fightNumber + AbstractCommand.DELIMITER + round;
	}

	/**
	 * TopicMessageId:Fight#:Round#
	 */
	@Override
	public String getKey() {
		return super.getKey() + AbstractCommand.DELIMITER + createClusterKey();
	}

	@Override
	protected long getOptionId(CallbackId id) {
		return id.getIds()[2];
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UfcTracker [");
		builder.append(a);
		builder.append(" VS ");
		builder.append(b);
		builder.append(" r");
		builder.append(round);
		return builder.toString();
	}

	/**
	 * NOOP Handled by {@link UfcSummarizer}
	 */
	@Override
	public void sendFinishedVotingMessage() {
	}
}
