package ca.hapke.campbinning.bot.commands.response;

import java.util.List;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBotEngine;
import ca.hapke.campbinning.bot.commands.response.fragments.ResultFragment;

/**
 * @author Nathan Hapke
 *
 */
public class TextCommandResult extends CommandResult {

	public TextCommandResult(BotCommand cmd) {
		super(cmd);
	}

	public TextCommandResult(BotCommand cmd, ResultFragment... fragments) {
		super(cmd, fragments);
	}

	public TextCommandResult(BotCommand cmd, List<ResultFragment> fragments) {
		super(cmd, fragments);
	}

	@Override
	public SendResult send(CampingBotEngine bot, Long chatId) throws TelegramApiException {
		MessageProcessor processor = bot.getProcessor();
		String msg = processor.process(this.fragments);
		bot.sendMsg(chatId, msg);
		return new SendResult(msg, null);
	}

}
