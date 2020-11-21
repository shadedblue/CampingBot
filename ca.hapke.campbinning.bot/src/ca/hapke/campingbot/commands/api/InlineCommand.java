package ca.hapke.campingbot.commands.api;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;

import ca.hapke.campingbot.callback.api.CallbackId;
import ca.hapke.campingbot.log.EventItem;
import ca.hapke.campingbot.processors.MessageProcessor;
import ca.hapke.campingbot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public interface InlineCommand {
	public abstract String getCommandName();

	public EventItem chosenInlineQuery(Update update, CallbackId id, CampingUser campingFromUser, String resultText);

	public List<InlineQueryResult> provideInlineQuery(Update update, String input, int updateId,
			MessageProcessor processor);

}