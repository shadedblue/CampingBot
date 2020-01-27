package ca.hapke.campbinning.bot.ui;

import ca.hapke.campbinning.bot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public interface IStatus {
	public void statusOffline(String username);

	public void statusOnline(CampingUser meCamping);
}
