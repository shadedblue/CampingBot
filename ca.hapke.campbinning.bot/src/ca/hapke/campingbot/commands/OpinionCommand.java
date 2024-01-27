package ca.hapke.campingbot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.SlashCommand;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.users.CampingUserMonitor;

/**
 * @author Nathan Hapke
 */
public class OpinionCommand extends AbstractCommand implements SlashCommand {
	private static final String OPINION = "Opinion";
	public static final SlashCommandType SlashOpinion = new SlashCommandType(OPINION, "opinion",
			BotCommandIds.TEXT | BotCommandIds.USE);
	private static final SlashCommandType[] SLASH_COMMANDS = new SlashCommandType[] { SlashOpinion };

	private CampingUserMonitor userMonitor = CampingUserMonitor.getInstance();

	@Override
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, Long chatId,
			CampingUser campingFromUser) throws TelegramApiException {
		Message targetMsg = message.getReplyToMessage();
		if (targetMsg == null)
			return null;

		User targetUser = targetMsg.getFrom();
		if (targetUser == null)
			return null;
		CampingUser targetCamping = userMonitor.getUser(targetUser);

		String initials = campingFromUser.getInitials();
		if (initials == null || initials.length() == 0) {
			initials = campingFromUser.getDisplayName();
		}
		CommandResult result = new TextCommandResult(command);
		result.add("(");
		result.add(initials);
		result.add(") Look out, ");
		result.add(targetCamping.getFirstname());
		result.add(" has an opinion");
		return result;
	}

	@Override
	public String getCommandName() {
		return OPINION;
	}

	@Override
	public SlashCommandType[] getSlashCommandsToRespondTo() {
		return SLASH_COMMANDS;
	}
}
