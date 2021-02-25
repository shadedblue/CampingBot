package ca.hapke.campingbot.voting;

import java.util.HashMap;
import java.util.Map;

import ca.hapke.campingbot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class UfcFight {
	public final String a, b;
	public final int rounds;
	public final int fightNumber;
//	private Map<CampingUser, List<Integer>> userToVotes = new HashMap<>();
	private Map<CampingUser, JudgingCard> judgingCards = new HashMap<>();

	public UfcFight(int fightNumber, String a, String b, int rounds) {
		this.a = a;
		this.b = b;
		this.rounds = rounds;
		this.fightNumber = fightNumber;
	}

//	public void setVote(CampingUser user, int round, Integer vote) {
//		List<Integer> votes = getVoteList(user);
//		while (votes.size() < round) {
//			votes.add(0);
//		}
//		votes.set(round, vote);
//	}
//
//	private List<Integer> getVoteList(CampingUser user) {
//		List<Integer> votes = userToVotes.get(user);
//		if (votes == null) {
//			votes = new ArrayList<>();
//			userToVotes.put(user, votes);
//		}
//		return votes;
//	}

	public JudgingCard getCard(CampingUser user) {
		JudgingCard card = judgingCards.get(user);
		if (card == null) {
			card = new JudgingCard(rounds);
			judgingCards.put(user, card);
		}
		return card;
	}

	public void setVote(CampingUser user, int round, int vote) {
		int a = -1;
		int b = -1;
		switch (vote) {
		case 0:
			a = 10;
			b = 8;
			break;
		case 1:
			a = 10;
			b = 9;
			break;
		case 2:
			a = 9;
			b = 10;
			break;
		case 3:
			a = 8;
			b = 10;
			break;
		}
		if (a >= 0 && b >= 0) {
			JudgingCard card = getCard(user);
			card.setVote(round, a, b);
//			card.as[round - 1] = a;
//			card.bs[round - 1] = b;
		}
	}

	public boolean isVotingComplete() {
		for (JudgingCard c : judgingCards.values()) {
			if (!c.isComplete()) {
				return false;
			}
		}
		return true;
	}

	public Map<CampingUser, JudgingCard> getJudgingCards() {
		return judgingCards;
	}
}
