/**
 * 
 */
package ca.hapke.campbinning.bot.users;

import java.util.Comparator;

/**
 * @author Nathan Hapke
 */
public class CampingUserDefaultComparator implements Comparator<CampingUser> {
	@Override
	public int compare(CampingUser a, CampingUser b) {
		return a.getCampingId() - b.getCampingId();
	}
}