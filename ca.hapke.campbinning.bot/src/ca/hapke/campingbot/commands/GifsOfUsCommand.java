package ca.hapke.campingbot.commands;

import java.util.HashMap;
import java.util.Map;

import org.telegram.telegrambots.meta.api.objects.Message;

import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.SlashCommand;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.ImageCommandResult;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.util.ImageLink;

/**
 * @author Nathan Hapke
 */
public class GifsOfUsCommand extends AbstractCommand implements SlashCommand {
	//@formatter:off
	private static final String[][] inputs = { 
			{ "GifsUs:Iunno",			"iunno", 		"http://www.hapke.ca/images/iunno.gif" }, 
			{ "GifsUs:JamiesonMaybe", 	"maybe", 		"http://www.hapke.ca/images/jamieson-maybe.mp4" },
			{ "GifsUs:JakeK", 			"k", 			"http://www.hapke.ca/images/jake-k.mp4" }, 
			{ "GifsUs:ReubenLesbians", 	"lesbians", 	"http://www.hapke.ca/images/reuben-lesbians.mp4" },
			{ "GifsUs:NateUgh", 		"ugh", 			"http://www.hapke.ca/images/nate-ugh.mp4" }, 
			{ "GifsUs:JustinYeah", 		"yeah", 		"http://www.hapke.ca/images/justin-yeah.mp4" } 
	};
	//@formatter:on
	private static final SlashCommandType[] SLASH_COMMANDS;
	private static final Map<SlashCommandType, ImageLink> commandToUrlMap = new HashMap<>();
	private static final String GIFS_OF_US_CATEGORY = "GifsOfUs";
	protected CampingBot bot;

	static {
		int qty = inputs.length;
		SLASH_COMMANDS = new SlashCommandType[qty];
		for (int i = 0; i < inputs.length; i++) {
			String[] strings = inputs[i];
			String longName = strings[0];
			String command = strings[1];
			String url = strings[2];
			SlashCommandType cmd = new SlashCommandType(longName, command,
					BotCommandIds.SILLY_RESPONSE | BotCommandIds.GIF);
			SLASH_COMMANDS[i] = cmd;
			commandToUrlMap.put(cmd, new ImageLink(url, ImageLink.GIF));
		}
	}

	public GifsOfUsCommand(CampingBot bot) {
		this.bot = bot;
	}

	@Override
	public String getCommandName() {
		return GIFS_OF_US_CATEGORY;
	}

	@Override
	public SlashCommandType[] getSlashCommandsToRespondTo() {
		return SLASH_COMMANDS;
	}

	@Override
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, Long chatId,
			CampingUser campingFromUser) {
		String msg = message.getText().toLowerCase().trim();
		for (SlashCommandType cmd : SLASH_COMMANDS) {
			if (msg.startsWith("/" + cmd.slashCommand)) {
				ImageLink img = commandToUrlMap.get(cmd);
				ImageCommandResult result = new ImageCommandResult(cmd, img);
				result.setReplyToOriginalMessageIfPossible(message);
				return result;
			}
		}
		return null;
	}
}
