package ca.hapke.campbinning.bot.commands;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.commands.response.CommandResult;
import ca.hapke.campbinning.bot.commands.response.TextCommandResult;
import ca.hapke.campbinning.bot.commands.response.fragments.TextStyle;
import ca.hapke.campbinning.bot.ui.IStatus;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.util.TimeFormatter;

/**
 * @author Nathan Hapke
 */
public class StatusCommand implements IStatus {
	private TimeFormatter tf = new TimeFormatter(2, ", ", false, false);
	private ZonedDateTime onlineTime;
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("LLLL d, h:m:s a");

	@Override
	public void statusOffline() {
		onlineTime = null;
	}

	@Override
	public void statusOnline() {
		onlineTime = ZonedDateTime.now();
	}

	@Override
	public void statusMeProvided(CampingUser me) {
	}

	@Override
	public void connectFailed(TelegramApiRequestException e) {
		onlineTime = null;
	}

	public CommandResult statusCommand() {
		TextCommandResult r = new TextCommandResult(BotCommand.Status);
		r.add("Online Since", TextStyle.Bold);
		r.add(": ");
		if (onlineTime != null) {
			r.add(onlineTime.format(formatter));
			r.add("\nDuration", TextStyle.Bold);
			r.add(": ");
			r.add(tf.toPrettyString(onlineTime));
		} else {
			r.add("???");
		}
		return r;
	}

}
