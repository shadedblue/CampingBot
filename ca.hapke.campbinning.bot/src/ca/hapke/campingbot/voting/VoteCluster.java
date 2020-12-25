package ca.hapke.campingbot.voting;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ca.hapke.campingbot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class VoteCluster<T> {
	private final Map<String, Map<CampingUser, Integer>> votesCluster = new HashMap<>();
	private final Map<String, Set<CampingUser>> votesNotApplicableCluster = new HashMap<>();
	private final Map<String, Map<Integer, T>> valueMapCluster = new HashMap<>();
	private VoteTracker<T> tracker;

	public VoteCluster(VoteTracker<T> voteTracker) {
		this.tracker = voteTracker;
	}

	public void createCluster(String id) {
		votesCluster.put(id, new HashMap<>());
		votesNotApplicableCluster.put(id, new HashSet<>());
		valueMapCluster.put(id, new HashMap<>());
	}

	public Map<CampingUser, Integer> getVotes() {
		String key = tracker.createClusterKey();
		return getVotes(key);
	}

	public Set<CampingUser> getVotesNotApplicable() {
		String key = tracker.createClusterKey();
		return getVotesNotApplicable(key);
	}

	public Map<Integer, T> getValueMap() {
		String key = tracker.createClusterKey();
		return getValueMap(key);
	}

	private Map<CampingUser, Integer> getVotes(String key) {
		if (!valueMapCluster.containsKey(key))
			createCluster(key);
		return votesCluster.get(key);
	}

	private Set<CampingUser> getVotesNotApplicable(String key) {
		if (!valueMapCluster.containsKey(key))
			createCluster(key);
		return votesNotApplicableCluster.get(key);
	}

	private Map<Integer, T> getValueMap(String key) {
		if (!valueMapCluster.containsKey(key))
			createCluster(key);
		return valueMapCluster.get(key);
	}

}
