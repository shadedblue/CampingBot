package ca.hapke.campbinning.bot.ui;

import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.util.CampingUtil;
import ca.odell.glazedlists.gui.AdvancedTableFormat;

/**
 * @author Nathan Hapke
 */
public class TableFormatCampingUser extends CampingTableFormat implements AdvancedTableFormat<CampingUser> {

	public TableFormatCampingUser() {
		super(new String[] { "C-ID", "T-ID", "Username", "First", "Last", "Nickname", "B-Day", "Last Update" },
				new int[] { 75, 100, 150, 100, 100, 250, 75, 100 });
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
			return u.getFirstname();
		case 4:
			return u.getLastname();
		case 5:
			return u.getNickname();
		case 6:
			return u.getBirthday();
		case 7:
			return u.getLastUpdate();
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
		case 6:
			return String.class;
		case 7:
			return Long.class;
		}
		throw new IllegalStateException();
	}

}
