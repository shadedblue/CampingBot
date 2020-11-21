package ca.hapke.campingbot.ui;

import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.util.CampingUtil;
import ca.odell.glazedlists.gui.AdvancedTableFormat;

/**
 * @author Nathan Hapke
 */
public class TableFormatCampingUser extends CampingTableFormat implements AdvancedTableFormat<CampingUser> {

	public TableFormatCampingUser() {
		super(new String[] { "C-ID", "T-ID", "Username", "First", "Last", "Nickname", "Interaction", "B-Day" },
				new int[] { 50, 100, 150, 100, 100, 250, 75, 75 });
	}

	@Override
	public Object getColumnValue(CampingUser u, int column) {
		switch (column) {
		case 0:
			return u.getCampingId();
		case 1:
			return u.getTelegramId();
		case 2:
			return CampingUtil.removePrefixAt(u.getUsername());
		case 3:
			return CampingUtil.blankTheNull(u.getFirstname());
		case 4:
			return CampingUtil.blankTheNull(u.getLastname());
		case 5:
			return CampingUtil.blankTheNull(u.getNickname());
		case 6:
			return u.isSeenInteraction();
		case 7:
			return u.getBirthday();
		}
		throw new IllegalStateException();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getColumnClass(int column) {
		switch (column) {
		case 0:
		case 1:
			return Integer.class;
		case 2:
		case 3:
		case 4:
		case 5:
			return String.class;
		case 6:
			return Boolean.class;
		case 7:
			return CampingUser.Birthday.class;
		}
		throw new IllegalStateException();
	}

}
