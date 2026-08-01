package ca.hapke.campingbot.voting;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import ca.hapke.campingbot.log.EventItem;
import ca.hapke.campingbot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class VoteChangedAdapter<T> implements VoteChangedListener<T> {

	@Override
	public EventItem changed(CallbackQuery callbackQuery, CampingUser user, long optionId) {
		return null;
	}

	@Override
	public EventItem confirmed(CallbackQuery callbackQuery, CampingUser user, long optionId) {
		return null;
	}

	@Override
	public EventItem completedByUser(CallbackQuery callbackQuery, CampingUser user, long optionId) {
		return null;
	}

	@Override
	public EventItem completedAutomatic() {
		return null;
	}

}
