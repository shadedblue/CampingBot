package ca.hapke.campingbot.commands;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.SlashCommand;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.commands.api.TextCommand;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.ImageCommandResult;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.util.ImageLink;

/**
 * @author Nathan Hapke
 */
public class IunnoCommand extends AbstractCommand implements TextCommand, SlashCommand {
	private static final SlashCommandType SlashIunno = new SlashCommandType("IunnoGoogleIt", "iunno",
			BotCommandIds.SILLY_RESPONSE | BotCommandIds.GIF);
	private static final SlashCommandType[] SLASH_COMMANDS = new SlashCommandType[] { SlashIunno };
	private static final String IUNNO_CATEGORY = "Iunno";
	protected CampingBot bot;
	private ImageLink iunnoImg = new ImageLink("http://www.hapke.ca/images/iunno.gif", ImageLink.GIF);

	public IunnoCommand(CampingBot bot) {
		this.bot = bot;
	}

	@Override
	public CommandResult textCommand(CampingUser campingFromUser, List<MessageEntity> entities, Long chatId,
			Message message) {
		ImageCommandResult result = new ImageCommandResult(SlashIunno, iunnoImg);
		result.setReplyToOriginalMessageIfPossible(message);
		return result;
	}

	@Override
	public boolean isMatch(String msg, Message message) {
		String msgLower = msg.toLowerCase().trim();
		return msgLower.endsWith("/" + SlashIunno.slashCommand);
	}

	@Override
	public String getCommandName() {
		return IUNNO_CATEGORY;
	}

	@Override
	public SlashCommandType[] getSlashCommandsToRespondTo() {
		return SLASH_COMMANDS;
	}

	@Override
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, Long chatId,
			CampingUser campingFromUser) {
		return textCommand(campingFromUser, message.getEntities(), chatId, message);
	}

	@Override
	public String provideUiStatus() {
		return null;
	}
}
