package ca.hapke.campbinning.bot.commands;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import ca.hapke.campbinning.bot.log.EventItem;

/**
 * @author Nathan Hapke
 *
 */
public interface CallbackCommand {

	public EventItem reactToCallback(CallbackQuery callbackQuery);

}