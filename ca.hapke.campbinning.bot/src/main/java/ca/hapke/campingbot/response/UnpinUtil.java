package ca.hapke.campingbot.response;

import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.UnpinAllChatMessages;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.UnpinChatMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.api.CampingBotEngine;
import ca.hapke.campingbot.log.EventItem;
import ca.hapke.campingbot.log.EventLogger;
import ca.hapke.campingbot.voting.VotingCommand;

/**
 * @author Nathan Hapke
 */
public abstract class UnpinUtil {

	public static EventItem unpinAll(CampingBotEngine bot, long chatId) {

		boolean success = false;
		try {
			String chatString = Long.toString(chatId);
			UnpinAllChatMessages unpin = new UnpinAllChatMessages(chatString);

			success = bot.execute(unpin);
		} catch (TelegramApiException ex) {
		}

		EventItem ei;
		if (success) {
			ei = new EventItem(VotingCommand.VoteCommand, null, chatId, null, "Unpinned all");
		} else {
			ei = new EventItem(VotingCommand.VoteCommandFailedCommand, null, chatId, null, "Failed to unpin all");
		}
		EventLogger.getInstance().add(ei);

		return ei;
	}

	public static EventItem unpinSpecific(long chatId, CampingBotEngine bot, Message msg) {
		boolean success = false;
		try {
			String chatString = Long.toString(chatId);
			Message pinnedMsg = bot.execute(new GetChat(chatString)).getPinnedMessage();
			if (pinnedMsg != null && msg.getMessageId().equals(pinnedMsg.getMessageId())) {
				UnpinChatMessage unpin = new UnpinChatMessage(chatString);
				success = bot.execute(unpin);
			}
		} catch (TelegramApiException e1) {
		}

		EventItem ei;
		if (success) {
			ei = new EventItem(VotingCommand.VoteCommand, null, chatId, msg.getMessageId(), "Unpinned banner", msg);
		} else {
			ei = new EventItem(VotingCommand.VoteCommandFailedCommand, null, chatId, msg.getMessageId(),
					"Failed to unpin", msg);
		}
		EventLogger.getInstance().add(ei);

		return ei;
	}
}
