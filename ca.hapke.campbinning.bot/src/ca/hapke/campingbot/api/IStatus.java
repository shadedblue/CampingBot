package ca.hapke.campingbot.api;

import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import ca.hapke.campingbot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public interface IStatus {
	public void statusOffline();

	public void statusOnline();

	public void statusMeProvided(CampingUser me);

	public void connectFailed(TelegramApiRequestException e);
}
