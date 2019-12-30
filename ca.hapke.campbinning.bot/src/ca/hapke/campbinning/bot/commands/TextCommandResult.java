package ca.hapke.campbinning.bot.commands;

import ca.hapke.campbinning.bot.BotCommand;

/**
 * @author Nathan Hapke
 *
 */
public class TextCommandResult {

	public final BotCommand cmd;
	public final String msg;
	public final boolean shouldSendMsg;

	public TextCommandResult(BotCommand cmd, String msg, boolean shouldSendMsg) {
		this.cmd = cmd;
		this.msg = msg;
		this.shouldSendMsg = shouldSendMsg;
	}

}
