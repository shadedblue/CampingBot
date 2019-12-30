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
		Integer aId = a.getTelegramId();
		Integer bId = b.getTelegramId();
		if (aId != null && bId != null)
			return aId.compareTo(bId);
		String aUser = a.getUsername();
		String bUser = b.getUsername();
		if (aUser != null && bUser != null)
			return aUser.compareTo(bUser);

		if (aId == null)
			return 1;
		return -1;
	}
}