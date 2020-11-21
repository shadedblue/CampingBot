package ca.hapke.campingbot.commands.api;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.users.CampingUser;

/**
 * @author Nathan Hapke
 *
 */
public interface TextCommand {

	public abstract CommandResult textCommand(CampingUser campingFromUser, List<MessageEntity> entities, Long chatId,
			Message message);

	public abstract boolean isMatch(String msg, Message message);
}
