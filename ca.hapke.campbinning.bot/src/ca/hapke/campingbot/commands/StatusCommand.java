package ca.hapke.campingbot.commands;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Map.Entry;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.AccessLevel;
import ca.hapke.campingbot.api.IStatus;
import ca.hapke.campingbot.channels.CampingChat;
import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.SlashCommand;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.commands.inline.HideItCommand;
import ca.hapke.campingbot.commands.inline.HideItMessage;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.fragments.TextStyle;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.util.TimeFormatter;

/**
 * @author Nathan Hapke
 */
public class StatusCommand extends AbstractCommand implements IStatus, SlashCommand {
	private static final String STATUS = "Status";
	public static final SlashCommandType SlashStatus = new SlashCommandType(STATUS, "status",
			BotCommandIds.TEXT | BotCommandIds.USE);
	private static final SlashCommandType[] SLASH_COMMANDS = new SlashCommandType[] { SlashStatus };
	private TimeFormatter tf = new TimeFormatter(2, ", ", false, false);
	private ZonedDateTime onlineTime;
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("LLLL d, h:mm:ss a");
	private HideItCommand hideIt;

	public StatusCommand(HideItCommand hideIt) {
		this.hideIt = hideIt;
	}

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
	public void connectFailed(TelegramApiException e) {
		onlineTime = null;
	}

	@Override
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, CampingChat chat,
			CampingUser campingFromUser) throws TelegramApiException {
		TextCommandResult r = new TextCommandResult(SlashStatus);
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
		r.add("\nHide It", TextStyle.Bold);

		Map<String, String> confirmedTopics = hideIt.getTopics().asMap();
		r.add("\n");
		r.add("Topics (" + confirmedTopics.size() + ") ", TextStyle.Italic);
		for (Map.Entry<String, String> e : confirmedTopics.entrySet()) {
			r.add(e.getValue());
			r.add(" ");
		}

		Map<Integer, HideItMessage> msgs = hideIt.getConfirmedMessages();
		r.add("\n");
		r.add("Messages (" + msgs.size() + ") ", TextStyle.Italic);
		for (Entry<Integer, HideItMessage> e : msgs.entrySet()) {
			r.add("\n");
			r.add(e.getKey());
			r.add(": ");
			r.add(e.getValue().getClearText());
		}
		return r;
	}

	@Override
	public SlashCommandType[] getSlashCommandsToRespondTo() {
		return SLASH_COMMANDS;
	}

	@Override
	public String getCommandName() {
		return STATUS;
	}

	@Override
	public AccessLevel accessRequired() {
		return AccessLevel.Admin;
	}
}
