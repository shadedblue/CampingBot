package ca.hapke.campbinning.bot.ui;

import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.util.CampingUtil;
import ca.odell.glazedlists.gui.AdvancedTableFormat;

/**
 * @author Nathan Hapke
 */
public class TableFormatCampingUser extends CampingTableFormat implements AdvancedTableFormat<CampingUser> {

	public TableFormatCampingUser() {
		super(new String[] { "Camping ID", "Telegram ID", "Username", "First", "Last", "Nickname",
				// "Balls",
				"Spells", "Rants", "RScore", "RActv", "Victim",
				// "Score",
				"Last Update" },
				new int[] { 100, 100, 150, 100, 100, 150,
						// 50,
						50, 50, 50, 50, 50,
						// 50,
						150 });
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
			return u.getSpellCount();
		case 7:
			return u.getRantCount();
		case 8:
			return u.getRantScore();
		case 9:
			return u.getRantActivation();
		case 10:
			return u.getVictimCount();
		case 11:
			return u.getLastUpdate();

		// return u.getBallsCount();
		// return u.getScore();
		// case 12:
		// case 13:
		}
		throw new IllegalStateException();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getColumnClass(int column) {
		switch (column) {
		case 0:
		case 1:
		case 6:
		case 7:
		case 8:
		case 10:
			return Integer.class;
		case 2:
		case 3:
		case 4:
		case 5:
			return String.class;
		case 9:
			return Float.class;
		case 11:
			return Long.class;
		}
		throw new IllegalStateException();
	}

}
