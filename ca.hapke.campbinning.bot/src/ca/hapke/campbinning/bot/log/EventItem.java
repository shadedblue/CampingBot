package ca.hapke.campbinning.bot.log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CommandType;
import ca.hapke.campbinning.bot.channels.CampingChat;
import ca.hapke.campbinning.bot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class EventItem {
	private static final DateFormat df = new SimpleDateFormat("HH:mm:ss");

	public final CommandType command;
	public final CampingUser user;
	public final Integer telegramId;
	public final String rest;
	public final Date d;
	public final CampingChat chat;

	public final Object extraData;

	private static Date createEventTime(Integer editDate) {
		if (editDate != null) {
			long t = editDate.intValue() * 1000l;
			return new Date(t);
		}
		return new Date();
	}

	public EventItem(CommandType command, CampingUser user, Integer ctime, CampingChat chat, Integer telegramId,
			String rest, Object extraData) {
		this.command = command;
		this.user = user;
		this.telegramId = telegramId;

		this.d = createEventTime(ctime);
		this.chat = chat;
		this.rest = rest;
		this.extraData = extraData;

	}

	public EventItem(String input) {
		command = BotCommand.UiString;
		user = null;
		this.telegramId = null;
		d = new Date();
		this.chat = null;
		rest = input;
		extraData = null;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		builder.append(df.format(d));
		builder.append("] ");

		if (telegramId != null) {
			builder.append(telegramId);
			builder.append(":");
		}
		if (command != null) {
			builder.append(command);
			builder.append(" ");
		}
		if (user != null) {
			builder.append(user.getFirstOrUserName());
		}
		if (chat != null) {
			builder.append("[");
			builder.append(chat.getChatname());
			builder.append("]");
//			builder.append(chat.getChatId());
		}
		if (rest != null && rest.length() > 0) {
			builder.append(rest);
		}
		if (extraData != null) {
			builder.append(" + ");
			builder.append(extraData.toString());
		}
		return builder.toString();
	}

}
