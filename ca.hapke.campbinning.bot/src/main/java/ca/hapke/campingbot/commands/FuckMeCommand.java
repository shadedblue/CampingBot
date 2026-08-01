package ca.hapke.campingbot.commands;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.BotChoicePriority;
import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.SlashCommand;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.commands.api.TextCommand;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.ImageCommandResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.util.ImageLink;

/**
 * @author Nathan Hapke
 */
public class FuckMeCommand extends SlashCommand implements TextCommand {

	private static final String FU = "fu";
	private static final String FUCK_ME = "Fuck me?";
	private static final String FUCK = "fuck";
	private static final SlashCommandType FU_COMMAND = new SlashCommandType(FUCK_ME, FU,
			BotCommandIds.SILLY_RESPONSE | BotCommandIds.TEXT | BotCommandIds.USE);

	private SlashCommandType[] cmds = new SlashCommandType[] { FU_COMMAND };
	private ImageLink fuImage = new ImageLink("http://www.hapke.ca/images/fu.mp4", ImageLink.GIF);
	private CampingBot bot;

	public FuckMeCommand(CampingBot bot) {
		this.bot = bot;
	}

	@Override
	public SlashCommandType[] getSlashCommandsToRespondTo() {
		return cmds;
	}

	private CommandResult sendFu(Message message) {
		ImageCommandResult result = new ImageCommandResult(FU_COMMAND, fuImage);
		result.setReplyTo(message.getMessageId());
		return result;
	}

	@Override
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, Long chatId,
			CampingUser campingFromUser) throws TelegramApiException {
		return sendFu(message);
	}

	@Override
	public CommandResult textCommand(CampingUser campingFromUser, List<MessageEntity> entities, Long chatId,
			Message message) throws TelegramApiException {
		return sendFu(message);
	}

	@Override
	public boolean isMatch(String msg, Message message) {
		CampingUser targetUser = bot.findTarget(message, false, true, BotChoicePriority.Only);
		return targetUser != null && msg.toLowerCase().contains(FUCK);
	}

	@Override
	public String getCommandName() {
		return FUCK_ME;
	}

	@Override
	public void appendHelpText(SlashCommandType cmd, TextCommandResult result) {
		result.add("No fuck youuuuuuu!");
	}
}
