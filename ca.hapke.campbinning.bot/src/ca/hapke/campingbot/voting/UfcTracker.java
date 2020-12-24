package ca.hapke.campingbot.voting;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.api.CampingBotEngine;
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

	public UfcTracker(CampingBotEngine bot, CampingUser ranter, CampingUser activater, Long chatId, Message activation,
			Message fight, String a, String b, int round, int rounds) throws TelegramApiException {
		super(bot, ranter, activater, chatId, activation, fight, 1, UfcCommand.UFC_COMMAND, false);
		this.a = a;
		this.b = b;
		this.round = round;
		this.rounds = rounds;
	}

	@Override
	public float getScore() {
		// TODO Auto-generated method stub
		return averageVoteValues(valueMap);
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

	@Override
	public List<ResultFragment> getBannerString() {
		List<ResultFragment> sb = new ArrayList<>();
		sb.add(new TextFragment(a, TextStyle.Bold));
		sb.add(new TextFragment(" vs. "));
		sb.add(new TextFragment(b, TextStyle.Bold));
		sb.add(new TextFragment("\nRound " + round + "/" + rounds));
		return sb;
	}

}
