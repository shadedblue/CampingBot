package ca.hapke.campingbot.api;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public interface IStatus {
	public void statusOffline();

	public void statusOnline(int attemptNumber);

	public void statusMeProvided(CampingUser me);

	public void connectFailed(int attemptNumber, TelegramApiException e);
}
