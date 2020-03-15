package ca.hapke.campbinning.bot.commands.response;

import java.util.List;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBotEngine;
import ca.hapke.campbinning.bot.commands.response.fragments.ResultFragment;

/**
 * Can be used for logging to UI only, when the message(s) has already been sent... such as by Vote Initiation
 * 
 * @author Nathan Hapke
 */
public class NoopCommandResult extends CommandResult {

	public NoopCommandResult(BotCommand cmd, ResultFragment... fragments) {
		super(cmd, fragments);
	}

	public NoopCommandResult(BotCommand cmd, List<ResultFragment> fragments) {
		super(cmd, fragments);
	}

	@Override
	public SendResult send(CampingBotEngine bot, Long chatId) {
		MessageProcessor processor = bot.getProcessor();
		String msg = processor.process(this.fragments);
		return new SendResult(msg, null);
	}

}
