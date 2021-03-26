/**
 * 
 */
package ca.hapke.campingbot.users;

import java.util.Comparator;

/**
 * @author Nathan Hapke
 */
public class CampingUserDefaultComparator implements Comparator<CampingUser> {
	@Override
	public int compare(CampingUser a, CampingUser b) {
		long diff = a.getCampingId() - b.getCampingId();

		if (diff < 0)
			return -1;
		if (diff > 0)
			return 1;
		return 0;
	}
}