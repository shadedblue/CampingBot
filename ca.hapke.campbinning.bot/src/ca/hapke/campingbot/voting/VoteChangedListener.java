package ca.hapke.campingbot.voting;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import ca.hapke.campingbot.log.EventItem;
import ca.hapke.campingbot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public interface VoteChangedListener<T> {
	public EventItem changed(CallbackQuery callbackQuery, CampingUser user, int optionId);

	public EventItem confirmed(CallbackQuery callbackQuery, CampingUser user, int optionId);

	public EventItem completedByUser(CallbackQuery callbackQuery, CampingUser user, int optionId);

	public EventItem completedAutomatic();
}
