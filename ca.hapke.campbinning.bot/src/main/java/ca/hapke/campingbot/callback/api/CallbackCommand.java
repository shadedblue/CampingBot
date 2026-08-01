package ca.hapke.campingbot.callback.api;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import ca.hapke.campingbot.log.EventItem;

/**
 * @author Nathan Hapke
 *
 */
public interface CallbackCommand {
	public String getCommandName();

	public EventItem reactToCallback(CallbackId id, CallbackQuery callbackQuery);

}