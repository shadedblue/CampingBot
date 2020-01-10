package ca.hapke.campbinning.bot.ui;

import ca.hapke.campbinning.bot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public interface IStatus {
	public void statusChanged(String connected, CampingUser me);
}
