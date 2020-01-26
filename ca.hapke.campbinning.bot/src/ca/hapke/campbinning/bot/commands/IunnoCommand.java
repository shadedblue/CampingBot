package ca.hapke.campbinning.bot.commands;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBot;
import ca.hapke.campbinning.bot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class IunnoCommand implements TextCommand {
	protected CampingBot bot;
	private RespondWithImage images;

	public IunnoCommand(CampingBot bot) {
		this.bot = bot;
		images = new RespondWithImage(bot);
		images.add(new ImageLink("http://www.hapke.ca/images/iunno.gif", ImageLink.GIF));
	}

	@Override
	public TextCommandResult textCommand(CampingUser campingFromUser, List<MessageEntity> entities, Long chatId,
			Message message) {
		return images.sendImage(BotCommand.IunnoGoogleIt, chatId, null);
	}

	@Override
	public boolean isMatch(String msg, List<MessageEntity> entities) {
		String msgLower = msg.toLowerCase().trim();
		return msgLower.endsWith("/" + BotCommand.IunnoGoogleIt.command);
	}

}
