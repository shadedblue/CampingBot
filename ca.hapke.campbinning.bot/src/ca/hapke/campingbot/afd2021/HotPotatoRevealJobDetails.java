package ca.hapke.campingbot.afd2021;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.commands.api.CommandType;
import ca.hapke.campingbot.response.SendResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.fragments.ResultFragment;
import ca.hapke.campingbot.util.JobDetails;

/**
 * @author Nathan Hapke
 */
public class HotPotatoRevealJobDetails implements JobDetails {

	private static final CommandType cmd = AfdHotPotato.HotPotatoCommand;
	private List<ResultFragment> frags;
	private Message targetMessage;
	private CampingBot bot;
	private Long chatId;

	public HotPotatoRevealJobDetails(CampingBot bot, Long chatId, List<ResultFragment> frags) {
		this.bot = bot;
		this.chatId = chatId;
		this.frags = frags;
	}

	@Override
	public int getNumSteps() {
		return 1;
//		return frags.size();
	}

	@Override
	public int getNumAttempts(int step) {
		return 3;
	}

	@Override
	public boolean isRequireCompletion(int step) {
		return true;
	}

	@Override
	public int getDelay(int step) {
		return 2000;
	}

	@Override
	public boolean doStep(int step, int attempt) {
		if (step == 0) {
			TextCommandResult result = new TextCommandResult(cmd);
			for (int i = 0; i < frags.size(); i++) {
				ResultFragment frag = frags.get(i);
				result.add(frag);
			}
			SendResult sendResult;
			try {
				sendResult = result.send(bot, chatId);
				targetMessage = sendResult.outgoingMsg;
				bot.logSendResult(targetMessage.getMessageId(), bot.getMeCamping(), chatId, cmd, result, sendResult);
				return true;
			} catch (TelegramApiException e) {
				bot.logFailure(targetMessage.getMessageId(), bot.getMeCamping(), chatId, cmd, e);
				return false;
			}

		} else {

		}

		return false;
	}

	@Override
	public boolean shouldAbort() {
		// TODO Auto-generated method stub
		return false;
	}

}
