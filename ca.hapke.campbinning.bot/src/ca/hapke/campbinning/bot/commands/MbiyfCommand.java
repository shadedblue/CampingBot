package ca.hapke.campbinning.bot.commands;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import com.vdurmont.emoji.Emoji;

import ca.hapke.calendaring.timing.ByTimeOfCalendar;
import ca.hapke.calendaring.timing.ByTimeOfWeek;
import ca.hapke.calendaring.timing.ByTimeOfYear;
import ca.hapke.calendaring.timing.TimesProvider;
import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBot;
import ca.hapke.campbinning.bot.Resources;
import ca.hapke.campbinning.bot.interval.IntervalByExecutionTime;
import ca.hapke.campbinning.bot.log.EventItem;
import ca.hapke.campbinning.bot.log.EventLogger;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.users.CampingUserMonitor;

/**
 * @author Nathan Hapke
 */
public class MbiyfCommand implements TextCommand, IntervalByExecutionTime {
	private static final int ENABLE_LENGTH_HOURS = 17;
	private static final int ENABLE_HOUR = 7;
	private static final int DISABLE_HOUR = 0;
	private static final int ENABLE_MIN = 0;
	private static final int COUNT = 6;
	private static final int REPEATS = 2;

	private CampingBot bot;
	private Resources res;
	private boolean enabled = false;
	private boolean shouldAnnounce = false;
	private TimesProvider<Boolean> times = new TimesProvider<>();
	private ByTimeOfCalendar<Boolean> nextExecEvent;

	public final static String[] ballsTriggers = new String[] { "balls", "mbiyf" };
	private static final long CAMPING_CHAT_ID = -1001288464383l;
//	private static final long TESTING_CHAT_ID = -371511001l;
	private static final long ANNOUNCE_CHAT_ID = CAMPING_CHAT_ID;

	public MbiyfCommand(CampingBot campingBot, Resources res) {
		this.bot = campingBot;
		this.res = res;
		times.use(new ByTimeOfWeek<Boolean>(DayOfWeek.FRIDAY, ENABLE_HOUR, 0, true));
		times.use(new ByTimeOfWeek<Boolean>(DayOfWeek.SATURDAY, DISABLE_HOUR, 0, false));
	}

	public void init() {
		CampingUserMonitor monitor = CampingUserMonitor.getInstance();
		for (CampingUser u : monitor.getUsers()) {
			if (u.hasBirthday()) {
				ByTimeOfYear<Boolean> enable = new ByTimeOfYear<Boolean>(u.getBirthdayMonth(), u.getBirthdayDay(),
						ENABLE_HOUR, ENABLE_MIN, true);
				times.use(enable);

				ZonedDateTime disableTime = enable.generateATargetTime().plus(ENABLE_LENGTH_HOURS, ChronoUnit.HOURS);

				ByTimeOfYear<Boolean> disable = new ByTimeOfYear<Boolean>(disableTime.getMonthValue(),
						disableTime.getDayOfMonth(), disableTime.getHour(), disableTime.getMinute(), false);
				times.use(disable);
			}
		}

		findStartupMode();
		doWork();
		generateNextExecTime();
		shouldAnnounce = true;
	}

	@Override
	public TextCommandResult textCommand(CampingUser campingFromUser, List<MessageEntity> entities, Long chatId,
			Message message) {
		CampingUser targetUser = bot.findTarget(entities);
		if (targetUser == bot.getMeCamping()) {
			return new TextCommandResult(BotCommand.MBIYFDipshit, "Fuck you, I'm not ballsing myself!", true);
		}

		String ball = res.getRandomBall();
		String face = res.getRandomFace();

		if (targetUser == null)
			return null;

		campingFromUser.increment(BotCommand.MBIYF);
		String msg = "My " + ball + " in " + targetUser.target() + "'s " + face + "!";
		return new TextCommandResult(BotCommand.MBIYF, msg, true);
	}

	@Override
	public boolean isMatch(String msg, List<MessageEntity> entities) {
		if (!enabled)
			return false;

		for (String trigger : ballsTriggers) {
			if (msg.contains(trigger)) {
				return true;
			}
		}
		return false;
	}

	private void findStartupMode() {
		// the doWork activates it.
		nextExecEvent = times.getMostNearestPast();
	}

	@Override
	public void generateNextExecTime() {
		times.generateNearestEvents();
	}

	@Override
	public void doWork() {
		enabled = nextExecEvent.value;
		if (enabled && shouldAnnounce) {
			announce();
		}
		EventLogger.getInstance().add(new EventItem("MbiyFriday announcement: " + (enabled ? "en" : "dis") + "abled"));
	}

	public void announce() {
		StringBuilder sb = new StringBuilder();

		List<Emoji> bar = new ArrayList<>();
		Emoji add = res.getFace("smirk");
		if (add != null)
			bar.add(add);
		add = res.getBall("boom");
		if (add != null)
			bar.add(add);
		add = res.getBall("fire");
		if (add != null)
			bar.add(add);

		for (int i = 0; i < bar.size(); i++) {
			String emoji = bar.get(i).getUnicode();
			for (int j = 0; j < REPEATS; j++) {
				sb.append(emoji);
			}
		}
		sb.append("\n");
		String poopUni = res.getBall("poop").getUnicode();
		sb.append(poopUni);
		sb.append("OHHHHH SHITTTT");
		sb.append(poopUni);
		sb.append("\nIt's MBIYFriday motha'uckas!\n");

		for (int i = bar.size() - 1; i >= 0; i--) {
			String emoji = bar.get(i).getUnicode();
			for (int j = 0; j < REPEATS; j++) {
				sb.append(emoji);
			}
		}

		sb.append("\n\nPREPARE YOUR\n");
		List<Emoji> emojis = new ArrayList<Emoji>(COUNT);

		getTen(res::getRandomFaceEmoji, emojis);
		for (Emoji emoji : emojis) {
			sb.append(emoji.getUnicode());
		}
		sb.append("\nFOR\n");
		emojis.clear();

		getTen(res::getRandomBallEmoji, emojis);
		for (Emoji emoji : emojis) {
			sb.append(emoji.getUnicode());
		}

		String out = sb.toString();
		bot.sendMsg(ANNOUNCE_CHAT_ID, out);
	}

	private void getTen(Supplier<Emoji> s, List<Emoji> emojis) {
		while (emojis.size() < COUNT) {
			Emoji val = s.get();
			if (!emojis.contains(val))
				emojis.add(val);
		}
	}

	@Override
	public boolean shouldRun() {
		return true;
	}

	@Override
	public long getNextExecutionTime() {
		return times.getNearestFuture().getFuture().toEpochSecond() * 1000;
	}

	/**
	 * FIXME may be disablement.
	 */
	public ZonedDateTime getNearestFutureEnablement() {
		return times.getNearestFuture().getFuture();
	}
}
