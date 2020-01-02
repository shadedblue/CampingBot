package ca.hapke.campbinning.bot.commands;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import com.vdurmont.emoji.Emoji;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBot;
import ca.hapke.campbinning.bot.Resources;
import ca.hapke.campbinning.bot.interval.IntervalByExecutionTime;
import ca.hapke.campbinning.bot.interval.TimeOfWeek;
import ca.hapke.campbinning.bot.log.EventItem;
import ca.hapke.campbinning.bot.log.EventLogger;
import ca.hapke.campbinning.bot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class MbiyfCommand extends TextCommand implements IntervalByExecutionTime {
	private static final int COUNT = 6;
	private static final int REPEATS = 2;

	private final ZoneId zone = ZoneId.systemDefault();
	private CampingBot bot;
	private Resources res;
	private long nextExecTime;
	private boolean enabled = false;
	private boolean shouldAnnounce = false;
	private List<TimeOfWeek<Boolean>> timeEvents = new ArrayList<>();
	private TimeOfWeek<Boolean> nextExecEvent;
	private Instant nearestFutureEnablement;
	public final static String[] ballsTriggers = new String[] { "balls", "mbiyf" };
	private static final long CAMPING_CHAT_ID = -1001288464383l;
//	private static final long TESTING_CHAT_ID = -371511001l;
	private static final long ANNOUNCE_CHAT_ID = CAMPING_CHAT_ID;

	public MbiyfCommand(CampingBot campingBot, Resources res) {
		this.bot = campingBot;
		this.res = res;
		// timeEvents.add(new TimeOfWeek<Boolean>(DayOfWeek.TUESDAY, 17, 0, true));
		// timeEvents.add(new TimeOfWeek<Boolean>(DayOfWeek.TUESDAY, 23, 0, false));
		timeEvents.add(new TimeOfWeek<Boolean>(DayOfWeek.FRIDAY, 7, 0, true));
		timeEvents.add(new TimeOfWeek<Boolean>(DayOfWeek.SATURDAY, 0, 0, false));

		findStartupMode();
		doWork();
		generateNextExecTime();
		shouldAnnounce = true;
	}

	@Override
	public TextCommandResult textCommand(CampingUser campingFromUser, List<MessageEntity> entities, Long chatId) {
		CampingUser targetUser = bot.findTarget(entities);
		if (targetUser == bot.getMeCamping()) {
			campingFromUser.decrement(BotCommand.MBIYF);
			return new TextCommandResult(BotCommand.MBIYFDipshit, "Fuck you, I'm not ballsing myself!", true);
		}

		String ball = res.getRandomBall();
		String face = res.getRandomFace();

		if (targetUser == null)
			return null;

		targetUser.victimize(BotCommand.MBIYF);
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
		Instant now = Instant.now().truncatedTo(ChronoUnit.MINUTES);

		List<Instant> thisWeekInstants = createThisWeekInstants(now);

		Instant nearestPast = null;
		TimeOfWeek<Boolean> currentEvent = null;
		for (int i = 0; i < thisWeekInstants.size(); i++) {
			Instant then = thisWeekInstants.get(i);

			// translate them to the past to find the most recent one
			if (then.isAfter(now))
				then = then.minus(7, ChronoUnit.DAYS);

			if (nearestPast == null || then.isAfter(nearestPast)) {
				nearestPast = then;
				currentEvent = timeEvents.get(i);
			}
		}

		// the doWork activates it.
		nextExecEvent = currentEvent;
	}

	@Override
	public void generateNextExecTime() {
		Instant now = Instant.now().truncatedTo(ChronoUnit.MINUTES);

		List<Instant> thisWeekInstants = createThisWeekInstants(now);

		Instant nearestFuture = null;
		nearestFutureEnablement = null;
		for (int i = 0; i < thisWeekInstants.size(); i++) {
			Instant then = thisWeekInstants.get(i);

			// translate them to the future to find the next one
			if (now.isAfter(then) || now.equals(then))
				then = then.plus(7, ChronoUnit.DAYS);

			if (nearestFuture == null || then.isBefore(nearestFuture)) {
				nearestFuture = then;
				nextExecEvent = timeEvents.get(i);
			}

			if (nearestFutureEnablement == null || then.isBefore(nearestFutureEnablement)) {
				nearestFutureEnablement = then;
			}
		}

		nextExecTime = nearestFuture.toEpochMilli();
	}

	public List<Instant> createThisWeekInstants(Instant now) {
		LocalDate ld = LocalDate.ofInstant(now, zone);
		DayOfWeek day = ld.getDayOfWeek();
		LocalTime lt = LocalTime.ofInstant(now, zone);

		Instant then;

		List<Instant> thisWeekInstants = new ArrayList<>(timeEvents.size());
		// TimeOfWeek<Boolean> target = timeEvents.get(0);
		for (TimeOfWeek<Boolean> target : timeEvents) {
			int daysAhead = target.day.ordinal() - day.ordinal();
			int hoursAhead = target.h - lt.getHour();
			int minsAhead = target.m - lt.getMinute();
			then = now.plus(daysAhead, ChronoUnit.DAYS);
			then = then.plus(hoursAhead, ChronoUnit.HOURS);
			then = then.plus(minsAhead, ChronoUnit.MINUTES);
			thisWeekInstants.add(then);
		}
		return thisWeekInstants;
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
		return nextExecTime;
	}

	public Instant getNearestFutureEnablement() {
		return nearestFutureEnablement;
	}
}
