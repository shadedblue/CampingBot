package ca.hapke.campingbot.afd2021;

import java.util.ArrayList;
import java.util.List;

import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.commands.UpdatingMessageJobDetails;
import ca.hapke.campingbot.processors.BlotProcessor;
import ca.hapke.campingbot.response.fragments.ResultFragment;
import ca.hapke.campingbot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class HotPotatoRevealJobDetails extends UpdatingMessageJobDetails {
	private List<List<ResultFragment>> fragSets;
	private BlotProcessor blotter;
	private AybBetweenRoundsImages betweenRounds;
	private int steps;

	public HotPotatoRevealJobDetails(CampingBot bot, Long chatId, List<List<ResultFragment>> fragSets,
			AybBetweenRoundsImages betweenRounds) {
		super(bot, AfdHotPotato.HotPotatoCommand, chatId);
		this.fragSets = fragSets;
		this.betweenRounds = betweenRounds;
		this.blotter = new BlotProcessor(true, BlotProcessor.blotsAll, 0.8);
		steps = fragSets.size() + 1;
	}

	@Override
	public int getNumSteps() {
		return steps;
	}

	@Override
	public int getNumAttempts(int step) {
		return 3;
	}

	@Override
	public boolean isRequireCompletion(int step) {
		return true;
	}

	@Override
	public int getDelay(int step) {
		return 1000;
	}

	@Override
	public boolean doStep(int step, int attempt) {
		List<ResultFragment> out = generateFragments(step);
		CampingUser fromUser = bot.getMeCamping();
		if (step == 0) {
			return attemptSend(fromUser, out);
		} else if (step < fragSets.size()) {
			boolean result = attemptEdit(fromUser, out);
			return result;
		} else {
			betweenRounds.begin();
			return true;
		}
//		return false;
	}

	protected List<ResultFragment> generateFragments(int step) {
		List<ResultFragment> result = new ArrayList<>();
		int i = 0;
		for (; i <= step && i < fragSets.size(); i++) {
			List<ResultFragment> stage = fragSets.get(i);
			for (ResultFragment frag : stage) {
				result.add(frag);
			}
		}
		if (i < fragSets.size()) {
			List<ResultFragment> stage = fragSets.get(i);
			for (ResultFragment frag : stage) {
				ResultFragment f2 = frag.transform(blotter, true);
				result.add(f2);
			}
		}
		return result;
	}

	@Override
	public boolean shouldAbort() {
		return false;
	}

}
