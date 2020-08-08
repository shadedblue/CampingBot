package ca.hapke.campbinning.bot.commands;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBot;
import ca.hapke.campbinning.bot.response.CommandResult;
import ca.hapke.campbinning.bot.response.ImageCommandResult;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.util.ImageLink;

/**
 * @author Nathan Hapke
 */
public class IunnoCommand extends AbstractCommand implements TextCommand, SlashCommand {
	private static final BotCommand[] SLASH_COMMANDS = new BotCommand[] { BotCommand.IunnoGoogleIt };
	private static final String IUNNO_CATEGORY = "Iunno";
	protected CampingBot bot;
	private ImageLink iunnoImg = new ImageLink("http://www.hapke.ca/images/iunno.gif", ImageLink.GIF);

	public IunnoCommand(CampingBot bot) {
		this.bot = bot;
	}

	@Override
	public CommandResult textCommand(CampingUser campingFromUser, List<MessageEntity> entities, Long chatId,
			Message message) {
		ImageCommandResult result = new ImageCommandResult(BotCommand.IunnoGoogleIt, iunnoImg);
		result.setReplyToOriginalMessageIfPossible(message);
		return result;
	}

	@Override
	public boolean isMatch(String msg, Message message) {
		String msgLower = msg.toLowerCase().trim();
		return msgLower.endsWith("/" + BotCommand.IunnoGoogleIt.command);
	}

	@Override
	public String getCommandName() {
		return IUNNO_CATEGORY;
	}

	@Override
	public BotCommand[] getSlashCommandsToRespondTo() {
		return SLASH_COMMANDS;
	}

	@Override
	public CommandResult respondToSlashCommand(BotCommand command, Message message, Long chatId,
			CampingUser campingFromUser) {
		return textCommand(campingFromUser, message.getEntities(), chatId, message);
	}
}
