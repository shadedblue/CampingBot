package ca.hapke.campingbot.afd2021;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.users.CampingUserMonitor;

/**
 * @author Nathan Hapke
 */
public class AfdPlayerManager {

	private Map<CampingUser, List<CampingUser>> userToVotesMap = new HashMap<>();
	private List<CampingUser> targets = new ArrayList<CampingUser>();

	private Map<CampingUser, String> initialsMap = new HashMap<>();

	private final CampingUserMonitor userMonitor = CampingUserMonitor.getInstance();

	public void add(int id, String initials) {
		CampingUser user = userMonitor.getUser(id);
		initialsMap.put(user, initials);
		targets.add(user);
	}

	public List<CampingUser> getVotes(CampingUser user) {
		List<CampingUser> votes = userToVotesMap.get(user);
		if (votes == null) {
			votes = new ArrayList<CampingUser>(AfdHotPotato.MAX_TOSSES);
			userToVotesMap.put(user, votes);
		}
		return votes;
	}

	public List<CampingUser> getVotes(int user) {
		return getVotes(userMonitor.getUser(user));
	}

	public List<CampingUser> getTargets() {
		return targets;
	}

	protected String getInitials(CampingUser user) {
		return initialsMap.get(user);
	}

	public void advance(CampingUser killed) {
		targets.remove(killed);
		userToVotesMap.clear();
	}
}
