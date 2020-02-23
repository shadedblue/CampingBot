package ca.hapke.campbinning.bot.commands;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import com.vdurmont.emoji.Emoji;

import ca.hapke.calendaring.event.CalendaredEvent;
import ca.hapke.calendaring.event.StartupMode;
import ca.hapke.calendaring.timing.ByCalendar;
import ca.hapke.calendaring.timing.ByTimeOfWeek;
import ca.hapke.calendaring.timing.ByTimeOfYear;
import ca.hapke.calendaring.timing.TimesProvider;
import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBot;
import ca.hapke.campbinning.bot.Resources;
import ca.hapke.campbinning.bot.commands.response.CommandResult;
import ca.hapke.campbinning.bot.commands.response.TextCommandResult;
import ca.hapke.campbinning.bot.commands.response.fragments.TextFragment;
import ca.hapke.campbinning.bot.log.EventItem;
import ca.hapke.campbinning.bot.log.EventLogger;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.users.CampingUserMonitor;

/**
 * @author Nathan Hapke
 */
public class MbiyfCommand implements TextCommand, CalendaredEvent<MbiyfMode> {
	private static final int ENABLE_LENGTH_HOURS = 17;
	private static final int ENABLE_HOUR = 7;
	private static final int ENABLE_MIN = 0;
	private static final int DISABLE_HOUR = 0;
	private static final int COUNT = 6;
	private static final int REPEATS = 2;

	private CampingBot bot;
	private Resources res;
	private boolean enabled = false;
	private boolean shouldAnnounce = false;
	private TimesProvider<MbiyfMode> times;
	private List<CampingUser> userRestriction;

	public final static String[] ballsTriggers = new String[] { "balls", "mbiyf" };
	private static final long CAMPING_CHAT_ID = -1001288464383l;
//	private static final long TESTING_CHAT_ID = -371511001l;
	private static final long ANNOUNCE_CHAT_ID = CAMPING_CHAT_ID;

	public MbiyfCommand(CampingBot campingBot, Resources res) {
		this.bot = campingBot;
		this.res = res;
	}

	public void init() {
		CampingUserMonitor monitor = CampingUserMonitor.getInstance();
		List<ByCalendar<MbiyfMode>> targets = new ArrayList<>();
		targets.add(new ByTimeOfWeek<MbiyfMode>(DayOfWeek.FRIDAY, ENABLE_HOUR, 0, new MbiyfMode(MbiyfType.Friday)));
		targets.add(new ByTimeOfWeek<MbiyfMode>(DayOfWeek.SATURDAY, DISABLE_HOUR, 0, new MbiyfMode(MbiyfType.Off)));

		Map<String, List<CampingUser>> birthdayMap = new HashMap<>();
		for (CampingUser u : monitor.getUsers()) {
			if (u.hasBirthday()) {
				String key = u.getBirthdayMonth() + "$" + u.getBirthdayDay();
				List<CampingUser> lst = birthdayMap.get(key);
				if (lst == null) {
					lst = new ArrayList<>();
					birthdayMap.put(key, lst);
				}
				lst.add(u);
			}
		}

		for (List<CampingUser> usersByDay : birthdayMap.values()) {
			CampingUser u = usersByDay.get(0);
			ByTimeOfYear<MbiyfMode> enable = new ByTimeOfYear<MbiyfMode>(u.getBirthdayMonth(), u.getBirthdayDay(),
					ENABLE_HOUR, ENABLE_MIN, new MbiyfMode(MbiyfType.Birthday, usersByDay));
			targets.add(enable);

			ZonedDateTime disableTime = enable.generateATargetTime().plus(ENABLE_LENGTH_HOURS, ChronoUnit.HOURS);

			ByTimeOfYear<MbiyfMode> disable = new ByTimeOfYear<MbiyfMode>(disableTime.getMonthValue(),
					disableTime.getDayOfMonth(), disableTime.getHour(), disableTime.getMinute(),
					new MbiyfMode(MbiyfType.Off));
			targets.add(disable);
		}
		times = new TimesProvider<MbiyfMode>(targets);
		shouldAnnounce = true;
	}

	@Override
	public CommandResult textCommand(CampingUser campingFromUser, List<MessageEntity> entities, Long chatId,
			Message message) {
		CampingUser targetUser = bot.findTarget(entities);
		if (userRestriction != null && !userRestriction.contains(targetUser))
			return null;

		if (targetUser == bot.getMeCamping()) {
			return new TextCommandResult(BotCommand.MBIYFDipshit,
					new TextFragment("Fuck you, I'm not ballsing myself!"));
		}

		Emoji ball = res.getRandomBallEmoji();
		Emoji face = res.getRandomFaceEmoji();

		if (targetUser == null)
			return null;

		campingFromUser.increment(BotCommand.MBIYF);
		CommandResult result = new TextCommandResult(BotCommand.MBIYF).add("My ").add(ball).add(" in ").add(targetUser)
				.add("'s ").add(face).add("!");
		return result;
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

	@Override
	public void doWork(MbiyfMode value) {
		enabled = value != null && value.isEnablement();
		userRestriction = value.getRestrictedToUsers();
		boolean makeAnnouncement = shouldAnnounce && bot.isOnline();
		if (enabled && makeAnnouncement) {
			announce(value);
		}
		StringBuilder sb = new StringBuilder();
		sb.append(value.toString());
		sb.append(" Announce?[");
		sb.append(makeAnnouncement);
		sb.append("]");
		EventLogger.getInstance().add(new EventItem(sb.toString()));
	}

	public void announce(MbiyfMode value) {
		MbiyfType type = value.getType();
		switch (type) {
		case Birthday:
			announceBirthday(value);
			break;
		case Friday:
			announceFriday(value);
			break;
		case Off:
			break;
		}

	}

	public void announceBirthday(MbiyfMode value) {
		Emoji cake = res.getBall("cake");

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

		sb.append("\nHEY ");
		appendBirthdayNames(sb);
		sb.append("...\n");

		for (int i = bar.size() - 1; i >= 0; i--) {
			String emoji = bar.get(i).getUnicode();
			for (int j = 0; j < REPEATS; j++) {
				sb.append(emoji);
			}
		}

		sb.append("\n\nWATCH OUT FOR MY\n");

		int qty = 6;
		List<Emoji> emojis = new ArrayList<Emoji>(qty);
		getQty(res::getRandomBallEmoji, emojis, qty);
		for (Emoji emoji : emojis) {
			sb.append(emoji.getUnicode());
		}

		sb.append("\nIN YOUR\n");
		for (int i = 0; i < qty; i++) {
			sb.append(cake.getUnicode());
		}

		sb.append("\n\nHAPPY BIRTHDAY\n...AND KISS MY ASS");

		String out = sb.toString();
		bot.sendMsg(ANNOUNCE_CHAT_ID, out);
	}

	public void announceFriday(MbiyfMode value) {
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

		sb.append("\n\n");

		sb.append("PREPARE YOUR\n");
		List<Emoji> emojis = new ArrayList<Emoji>(COUNT);

		getQty(res::getRandomFaceEmoji, emojis, COUNT);
		for (Emoji emoji : emojis) {
			sb.append(emoji.getUnicode());
		}
		sb.append("\nFOR\n");
		emojis.clear();

		getQty(res::getRandomBallEmoji, emojis, COUNT);
		for (Emoji emoji : emojis) {
			sb.append(emoji.getUnicode());
		}

		String out = sb.toString();
		bot.sendMsg(ANNOUNCE_CHAT_ID, out);
	}

	private void appendBirthdayNames(StringBuilder sb) {
		if (userRestriction != null) {
			int size = userRestriction.size();
			for (int i = 0; i < size; i++) {
				CampingUser u = userRestriction.get(i);
				if (i > 0) {
					int last = size - 1;
					if (i < last) {
						sb.append(", ");

					} else if (i == last) {
						sb.append(" AND ");
					}
				}
				sb.append(u.getFirstname().toUpperCase());
			}
		}
	}

	private void getQty(Supplier<Emoji> s, List<Emoji> emojis, int count) {
		while (emojis.size() < count) {
			Emoji val = s.get();
			if (!emojis.contains(val))
				emojis.add(val);
		}
	}

	@Override
	public boolean shouldRun() {
		return true;
	}

	/**
	 * FIXME may be disablement.
	 */
	public ZonedDateTime getNearestFutureEnablement() {
		return times.getNearestFuture().getFuture();
	}

	@Override
	public TimesProvider<MbiyfMode> getTimeProvider() {
		return times;
	}

	@Override
	public StartupMode getStartupMode() {
		ByCalendar<MbiyfMode> past = times.getMostNearestPast();
		if (past.value.isEnablement())
			return StartupMode.Always;
		else
			return StartupMode.Never;

	}
}
