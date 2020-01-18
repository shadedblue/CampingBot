package ca.hapke.campbinning.bot.commands;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBot;

/**
 * @author Nathan Hapke
 */
public class PartyEverydayCommand extends RespondWithImage {

	private final ZoneId zone = ZoneId.systemDefault();

	private static final String[] urls = new String[] { "http://www.hapke.ca/images/party-boy1.gif",
			"http://www.hapke.ca/images/party-boy2.gif", "http://www.hapke.ca/images/party-boy3.gif" };
	private static final int[] imageTypes = new int[] { GIF, GIF, GIF };

	public PartyEverydayCommand(CampingBot bot) {
		super(bot, urls, imageTypes);
	}

	@Override
	public boolean isMatch(String msg, List<MessageEntity> entities) {
		Instant now = Instant.now();
		LocalDate ld = LocalDate.ofInstant(now, zone);
		LocalTime lt = LocalTime.ofInstant(now, zone);
		int day = ld.getDayOfWeek().getValue();
		int hours = lt.getHour();
		if (day >= DayOfWeek.MONDAY.getValue() && day <= DayOfWeek.FRIDAY.getValue() && hours >= 8 && hours <= 16) {
			// weekdays SFW
			return false;
		}

		String lowerCase = msg.toLowerCase();
		boolean matches = lowerCase.matches(".*pa[r]+ty everyday.*");
		return matches;
	}

	@Override
	protected BotCommand getCommandType() {
		return BotCommand.PartyEveryday;
	}

}
