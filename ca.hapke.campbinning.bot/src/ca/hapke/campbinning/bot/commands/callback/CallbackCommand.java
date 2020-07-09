package ca.hapke.campbinning.bot.commands.callback;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import ca.hapke.campbinning.bot.log.EventItem;

/**
 * @author Nathan Hapke
 *
 */
public interface CallbackCommand {
	public String getCommandName();

	public EventItem reactToCallback(CallbackId id, CallbackQuery callbackQuery);

}