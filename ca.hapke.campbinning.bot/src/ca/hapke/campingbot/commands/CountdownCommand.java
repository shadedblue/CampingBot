package ca.hapke.campingbot.commands;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.GregorianCalendar;

import org.telegram.telegrambots.meta.api.objects.Message;

import ca.hapke.campingbot.Resources;
import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.SlashCommand;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.fragments.CaseChoice;
import ca.hapke.campingbot.response.fragments.ResultFragment;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.users.CampingUserMonitor;
import ca.hapke.util.TimeFormatter;

/**
 * @author Nathan Hapke
 */
public class CountdownCommand extends AbstractCommand implements SlashCommand {

	private static final SlashCommandType SlashCountdown = new SlashCommandType("Countdown", "countdown",
			BotCommandIds.SILLY_RESPONSE | BotCommandIds.TEXT | BotCommandIds.USE);

	private static final SlashCommandType[] SLASH_COMMANDS = new SlashCommandType[] { SlashCountdown };

	@Override
	public SlashCommandType[] getSlashCommandsToRespondTo() {
		return SLASH_COMMANDS;
	}

	public static final String COUNTDOWN_CONTAINER = "Countdown";
	// Month is 0-indexed for some stupid inconsistent reason...
	private ZonedDateTime countdownTarget = new GregorianCalendar(2021, 5, 20, 23, 0, 00).toZonedDateTime();
	private Resources res;
	private MbiyfCommand ballsCommand;
	private TimeFormatter tf = new TimeFormatter(2, " ", false, true);
	private CampingUserMonitor userMonitor = CampingUserMonitor.getInstance();

	public CountdownCommand(Resources res, MbiyfCommand ballsCommand) {
		this.res = res;
		this.ballsCommand = ballsCommand;
	}

	@Override
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, Long chatId,
			CampingUser campingFromUser) {
		ZonedDateTime now = new GregorianCalendar().toZonedDateTime();
		CommandResult result = new TextCommandResult(CountdownCommand.SlashCountdown);

		ZonedDateTime targetEvent;
		if (countdownTarget != null && now.isBefore(countdownTarget)) {
//			int rtv = 558638791;
//			int andrew = 642767839;
//			int jamieson = 708570894;
//			CampingUser target = userMonitor.getUser(jamieson);
//			result.add(new MentionFragment(target, MentionDisplay.Nickname, CaseChoice.Upper, null, null));
//			result.add(" TELLS CSL AND HUSBANDCHOAD TO SSUUUCCCKKKK IIIITTTTT", CaseChoice.Upper);
			result.add("RICK & MORTY SEASON 5", CaseChoice.Upper);

//			result.add(new MentionFragment(target, MentionDisplay.Nickname, CaseChoice.Upper, null, "'s"));

			targetEvent = countdownTarget;
		} else {
			result.add("MBIYFRIDAY COUNTDOWN");
			targetEvent = ballsCommand.getNearestFutureEnablement();
		}

		result.add(ResultFragment.NEWLINE);
		for (int i = 0; i < 5; i++) {
			result.add(res.getRandomFaceEmoji());
		}
		result.add(ResultFragment.NEWLINE);
		result.add(res.getRandomBallEmoji());
		result.add(ResultFragment.SPACE);
		result.add(tf.toPrettyString(targetEvent));
//		result.add(countWeekdays(now.toLocalDate(), targetEvent.toLocalDate()) + " SHIFTS");

		return result;
	}

	@Override
	public String getCommandName() {
		return COUNTDOWN_CONTAINER;
	}

	/**
	 * From https://stackoverflow.com/questions/4600034/calculate-number-of-weekdays-between-two-dates-in-java/44942039
	 */
	public static long countWeekdays(final LocalDate start, final LocalDate end) {
		final DayOfWeek startW = start.getDayOfWeek();
		final DayOfWeek endW = end.getDayOfWeek();

		final long days = ChronoUnit.DAYS.between(start, end);
		final long daysWithoutWeekends = days - 2 * ((days + startW.getValue()) / 7);

		// adjust for starting and ending on a Sunday:
		return daysWithoutWeekends + (startW == DayOfWeek.SUNDAY ? 1 : 0) + (endW == DayOfWeek.SUNDAY ? 1 : 0);
	}

	@Override
	public String provideUiStatus() {
		return countdownTarget.toString();
	}
}
