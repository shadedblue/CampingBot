package ca.hapke.campingbot.voting;

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

/**
 * @author Nathan Hapke
 */
public class UfcTracker extends VoteTracker<Integer> {

	private String a;
	private String b;
	private int round;
	private int rounds;
	private UfcFight fight;
	private Message msg;

	public UfcTracker(CampingBotEngine bot, CampingUser ranter, CampingUser activater, Long chatId, Message activation,
			Message msg, UfcFight fight, int round) throws TelegramApiException {
		super(bot, ranter, activater, chatId, activation, msg, 1, UfcCommand.UFC_COMMAND, false);
		this.msg = msg;
		this.fight = fight;
		this.a = fight.a;
		this.b = fight.b;
		this.round = round;
		this.rounds = fight.rounds;
	}

	/**
	 * Advance to next round
	 */
	public UfcTracker(UfcTracker previousRound) throws TelegramApiException {
		super(previousRound.bot, previousRound.ranter, previousRound.activater, previousRound.chatId,
				previousRound.activation, previousRound.topic, 1, UfcCommand.UFC_COMMAND, false);
		this.fight = previousRound.fight;
		this.a = previousRound.fight.a;
		this.b = previousRound.fight.b;
		this.round = previousRound.round + 1;
		this.rounds = previousRound.fight.rounds;
	}

	@Override
	protected boolean createOptions(List<VotingOption<Integer>> optionsList) {
		optionsList.add(new VotingOption<Integer>("10-8", "10-8 " + a, -2));
		optionsList.add(new VotingOption<Integer>("10-9", "10-9 " + a, -1));
		optionsList.add(new VotingOption<Integer>("9-10", "10-9 " + b, 1));
		optionsList.add(new VotingOption<Integer>("8-10", "10-8 " + b, 2));
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
	protected CallbackId createCallbackId(String command, int updateId, int i) {
		return new CallbackId(command, updateId, fight.fightNumber, round, i);
	}

	@Override
	public float getScore() {
		// TODO Auto-generated method stub
		Map<Integer, Integer> valueMap = cluster.getValueMap();
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

	@Override
	protected int getOptionId(CallbackId id) {
		return id.getIds()[2];
	}

}
