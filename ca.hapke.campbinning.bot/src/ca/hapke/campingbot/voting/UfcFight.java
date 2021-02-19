package ca.hapke.campingbot.voting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.hapke.campingbot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class UfcFight {
	public final String a, b;
	public final int rounds;
	public final int fightNumber;
	private Map<CampingUser, List<Integer>> userToVotes = new HashMap<>();

	public UfcFight(int fightNumber, String a, String b, int rounds) {
		this.a = a;
		this.b = b;
		this.rounds = rounds;
		this.fightNumber = fightNumber;
	}

	public void setVote(CampingUser user, int round, Integer vote) {
		List<Integer> votes = getVoteList(user);
		while (votes.size() < round) {
			votes.add(0);
		}
		votes.set(round, vote);
	}

	private List<Integer> getVoteList(CampingUser user) {
		List<Integer> votes = userToVotes.get(user);
		if (votes == null) {
			votes = new ArrayList<>();
			userToVotes.put(user, votes);
		}
		return votes;
	}
}
