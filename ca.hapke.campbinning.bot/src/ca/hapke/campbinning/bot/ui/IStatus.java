package ca.hapke.campbinning.bot.ui;

import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import ca.hapke.campbinning.bot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public interface IStatus {
	public void statusOffline();

	public void statusOnline();

	public void statusMeProvided(CampingUser me);

	public void connectFailed(TelegramApiRequestException e);
}
