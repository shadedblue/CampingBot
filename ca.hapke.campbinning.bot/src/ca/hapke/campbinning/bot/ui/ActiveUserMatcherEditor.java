package ca.hapke.campbinning.bot.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import ca.hapke.campbinning.bot.users.CampingUser;
import ca.odell.glazedlists.matchers.AbstractMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;

/**
 * @author Nathan Hapke
 */
public class ActiveUserMatcherEditor extends AbstractMatcherEditor<CampingUser> {
	private Matcher<CampingUser> activeUsers = new Matcher<CampingUser>() {
		@Override
		public boolean matches(CampingUser item) {
			return item.getTelegramId() > 0 || item.isSeenInteraction();
		}
	};
	private Matcher<CampingUser> allUsers = new Matcher<CampingUser>() {
		@Override
		public boolean matches(CampingUser item) {
			return true;
		}
	};
	private JCheckBox chkFilterUsers;
	private Matcher<CampingUser> activeFilter;

	public ActiveUserMatcherEditor(boolean activeOnly, JCheckBox chkFilterUsers) {
		this.chkFilterUsers = chkFilterUsers;
		setFilter(activeOnly);
		this.chkFilterUsers.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean selected = chkFilterUsers.isSelected();
				setFilter(selected);
			}
		});
	}

	public void setFilter(boolean selected) {
		if (selected) {
			this.activeFilter = activeUsers;
		} else {
			this.activeFilter = allUsers;
		}
		fireChanged(activeFilter);
	}

}
