package ca.hapke.campbinning.bot.commands;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBot;

/**
 * @author Nathan Hapke
 */
public class PartyEverydayCommand extends RespondWithImage {

	private static final String[] urls = new String[] { "http://www.hapke.ca/images/party-boy1.gif",
			"http://www.hapke.ca/images/party-boy2.gif", "http://www.hapke.ca/images/party-boy3.gif" };
	private static final int[] imageTypes = new int[] { GIF, GIF, GIF };

	public PartyEverydayCommand(CampingBot bot) {
		super(bot, urls, imageTypes);
	}

	@Override
	public boolean isMatch(String msg, List<MessageEntity> entities) {
		String lowerCase = msg.toLowerCase();
		boolean matches = lowerCase.matches(".*pa[r]+ty everyday.*");
		return matches;
	}

	@Override
	protected BotCommand getCommandType() {
		return BotCommand.PartyEveryday;
	}

}
