package ca.hapke.campingbot.commands;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.AccessLevel;
import ca.hapke.campingbot.BotConstants;
import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.SlashCommand;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.fragments.MentionDisplay;
import ca.hapke.campingbot.response.fragments.MentionFragment;
import ca.hapke.campingbot.response.fragments.ResultFragment;
import ca.hapke.campingbot.response.fragments.TextStyle;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.users.CampingUserMonitor;

/**
 * @author Nathan Hapke
 */
public class SetInitialsCommand extends AbstractCommand implements SlashCommand {
	private static final String SET_INITIALS = "SetInitials";
	public static final SlashCommandType SlashSetInitials = new SlashCommandType(SET_INITIALS, "setinitials",
			BotCommandIds.NICKNAME | BotCommandIds.SET);
	private static final SlashCommandType[] SLASH_COMMANDS = new SlashCommandType[] { SlashSetInitials };

	protected CampingUserMonitor userMonitor = CampingUserMonitor.getInstance();

	@Override
	public String getCommandName() {
		return SET_INITIALS;
	}

	@Override
	public AccessLevel accessRequired() {
		return AccessLevel.Admin;
	}

	@Override
	public SlashCommandType[] getSlashCommandsToRespondTo() {
		return SLASH_COMMANDS;
	}

	@Override
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, Long chatId,
			CampingUser campingFromUser) throws TelegramApiException {
		String originalMsg = message.getText();
		List<MessageEntity> entities = message.getEntities();
		int targetOffset = originalMsg.indexOf(" ") + 1;
		int nickOffset = originalMsg.indexOf(" ", targetOffset) + 1;
		if (targetOffset > 0 && nickOffset > targetOffset + 1) {
			String newInitials = originalMsg.substring(nickOffset);
			MessageEntity targeting = null;

			// TODO Unify with @link CampingBotEngine.findTarget

			for (MessageEntity msgEnt : entities) {
				int offset = msgEnt.getOffset();
				String type = msgEnt.getType();
				if (offset == targetOffset && (BotConstants.MENTION.equalsIgnoreCase(type)
						|| BotConstants.TEXT_MENTION.equalsIgnoreCase(type))) {
					targeting = msgEnt;
				}
			}
			if (targeting != null) {

				CampingUser targetUser = userMonitor.getUser(targeting);
				targetUser.setInitials(newInitials);

				if (targetUser != null) {
					CommandResult result = new TextCommandResult(SlashSetInitials);
					result.add(campingFromUser);
					result.add(ResultFragment.COLON_SPACE);
//					result.add(targetUser.getFirstOrUserName(), TextStyle.Bold);
					result.add(new MentionFragment(targetUser, MentionDisplay.First, TextStyle.Bold));
					result.add("'s initials changed to: ");
					result.add(newInitials, TextStyle.Bold);
					return result;
				}
			}
		}
		return null;
	}

	@Override
	public String provideUiStatus() {
		return null;
	}
}
