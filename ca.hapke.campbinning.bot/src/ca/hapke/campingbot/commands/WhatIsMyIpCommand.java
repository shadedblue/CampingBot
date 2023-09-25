package ca.hapke.campingbot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.AccessLevel;
import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.SlashCommand;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class WhatIsMyIpCommand extends AbstractCommand implements SlashCommand {

	private String ip = "?";

	public WhatIsMyIpCommand() {
		try (java.util.Scanner s = new java.util.Scanner(new java.net.URL("https://api.ipify.org").openStream(),
				"UTF-8").useDelimiter("\\A")) {
			ip = s.next();
		} catch (java.io.IOException e) {
			ip = "unknown... " + e.getMessage();
		}
	}

	private static final String MY_IP = "myip";
	private static final String PRETTY_MY_IP = "What Is My Ip?";
	public static final SlashCommandType SlashMyIp = new SlashCommandType(PRETTY_MY_IP, MY_IP,
			BotCommandIds.TEXT | BotCommandIds.USE);
	private static final SlashCommandType[] SLASH_COMMANDS = new SlashCommandType[] { SlashMyIp };

	@Override
	public SlashCommandType[] getSlashCommandsToRespondTo() {
		return SLASH_COMMANDS;
	}

	@Override
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, Long chatId,
			CampingUser campingFromUser) throws TelegramApiException {
		String resultText = "My current IP address is " + ip;

		CommandResult result = new TextCommandResult(SlashMyIp);
		result.add(resultText);
		return result;
	}

	@Override
	public String getCommandName() {
		return MY_IP;
	}

	@Override
	public AccessLevel accessRequired() {
		return AccessLevel.Admin;
	}

	@Override
	public String provideUiStatus() {
		return "IP Address: " + ip;
	}
}
