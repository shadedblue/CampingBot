package ca.hapke.campingbot.voting;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import ca.hapke.campingbot.log.EventItem;
import ca.hapke.campingbot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public interface VoteChangedListener<T> {
	public EventItem changed(int optionId, CallbackQuery callbackQuery, CampingUser user);

	public EventItem confirmed(int optionId, CallbackQuery callbackQuery, CampingUser user);

	public EventItem completedByUser(int optionId, CallbackQuery callbackQuery, CampingUser user);

	public EventItem completedAutomatic();
}
