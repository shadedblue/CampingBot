package ca.hapke.campingbot.ui;

import ca.hapke.campingbot.channels.CampingChat;
import ca.hapke.campingbot.channels.ChatAllowed;
import ca.hapke.campingbot.channels.ChatType;
import ca.odell.glazedlists.gui.AdvancedTableFormat;

/**
 * @author Nathan Hapke
 */
public class TableFormatCampingChat extends CampingTableFormat implements AdvancedTableFormat<CampingChat> {

	public TableFormatCampingChat() {
		super(new String[] { "Name", "ID", "Type", "Status", "Announce?" }, new int[] { 125, 110, 80, 100, 75 });
	}

	@Override
	public Object getColumnValue(CampingChat cc, int column) {
		switch (column) {
		case 0:
			return cc.getChatname();
		case 1:
			return cc.chatId;
		case 2:
			return cc.getType();
		case 3:
			return cc.getAllowed();
		case 4:
			return cc.isAnnounce();
		}
		throw new IllegalStateException();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getColumnClass(int column) {
		switch (column) {
		case 0:
			return String.class;
		case 1:
			return Long.class;
		case 2:
			return ChatType.class;
		case 3:
			return ChatAllowed.class;
		case 4:
			return Boolean.class;

		}
		throw new IllegalStateException();
	}

}
