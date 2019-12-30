package ca.hapke.campbinning.bot.commands;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import ca.hapke.campbinning.bot.users.CampingUser;

/**
 * @author Nathan Hapke
 *
 */
public abstract class TextCommand {

	public abstract TextCommandResult textCommand(CampingUser campingFromUser, List<MessageEntity> entities,
			Long chatId);

	public abstract boolean isMatch(String msg, List<MessageEntity> entities);
}
