package ca.hapke.campingbot.tests;

import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.users.CampingUserMonitor;

/**
 * @author Nathan Hapke
 */
public class UsersForTesting {

	public final CampingUser rtv;
	public final CampingUser nh;
	public final CampingUser jm;
	public final CampingUser aa;
	public final CampingUser bot;
	public final CampingUserMonitor um;

	public UsersForTesting() {
		um = CampingUserMonitor.getInstance();
		bot = um.monitor(1l, "devbot", "D", "B", true);
		rtv = um.monitor(5l, "robtheviking", "R", "S", true);
		nh = um.monitor(2l, "shadedblue", "N", "H", true);
		jm = um.monitor(3l, "jakeford", "J", "M", true);
		aa = um.monitor(4l, "aandy", "A", "A", true);
	}
}
