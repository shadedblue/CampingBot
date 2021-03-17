package ca.hapke.campingbot.commands;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.api.CampingBotEngine;
import ca.hapke.campingbot.commands.api.CommandType;
import ca.hapke.campingbot.response.EditTextCommandResult;
import ca.hapke.campingbot.response.SendResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.fragments.ResultFragment;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.util.JobDetails;

/**
 * @author Nathan Hapke
 */
public abstract class UpdatingMessageJobDetails implements JobDetails {
	private Message targetMessage;
	protected final CampingBotEngine bot;
	protected final CommandType cmd;
	protected final Long chatId;

	public UpdatingMessageJobDetails(CampingBotEngine bot, CommandType cmd, Long chatId) {
		this.bot = bot;
		this.cmd = cmd;
		this.chatId = chatId;
	}

	protected boolean attemptSend(CampingUser user, List<ResultFragment> frags) {
		TextCommandResult result = new TextCommandResult(cmd, frags);

		SendResult sendResult;
		try {
			sendResult = result.send(bot, chatId);
			targetMessage = sendResult.outgoingMsg;
			bot.logSendResult(targetMessage.getMessageId(), user, chatId, cmd, result, sendResult);
			return true;
		} catch (TelegramApiException e) {
			bot.logFailure(targetMessage.getMessageId(), user, chatId, cmd, e);
			return false;
		}
	}

	protected boolean attemptEdit(CampingUser fromUser, List<ResultFragment> frags) {
		Integer telegramId = targetMessage.getMessageId();

		EditTextCommandResult edit = new EditTextCommandResult(cmd, targetMessage, frags);
		SendResult result;
		try {
			result = edit.send(bot, chatId);
			bot.logSendResult(telegramId, fromUser, chatId, cmd, edit, result);
			return true;
		} catch (TelegramApiException e) {
			bot.logFailure(telegramId, fromUser, chatId, cmd, e);
			return false;
		}
	}

}