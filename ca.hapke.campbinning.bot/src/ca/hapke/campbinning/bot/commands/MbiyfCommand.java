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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.vdurmont.emoji.Emoji;

import ca.hapke.calendaring.event.CalendaredEvent;
import ca.hapke.calendaring.event.StartupMode;
import ca.hapke.calendaring.timing.ByCalendar;
import ca.hapke.calendaring.timing.ByTimeOfWeek;
import ca.hapke.calendaring.timing.ByTimeOfYear;
import ca.hapke.calendaring.timing.TimesProvider;
import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBot;
import ca.hapke.campbinning.bot.CampingSystem;
import ca.hapke.campbinning.bot.Resources;
import ca.hapke.campbinning.bot.category.CategoriedItems;
import ca.hapke.campbinning.bot.commands.response.CommandResult;
import ca.hapke.campbinning.bot.commands.response.ImageCommandResult;
import ca.hapke.campbinning.bot.commands.response.TextCommandResult;
import ca.hapke.campbinning.bot.commands.response.fragments.CaseChoice;
import ca.hapke.campbinning.bot.commands.response.fragments.MentionDisplay;
import ca.hapke.campbinning.bot.commands.response.fragments.MentionFragment;
import ca.hapke.campbinning.bot.commands.response.fragments.TextFragment;
import ca.hapke.campbinning.bot.log.EventItem;
import ca.hapke.campbinning.bot.log.EventLogger;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.users.CampingUserMonitor;
import ca.hapke.campbinning.bot.util.CampingUtil;

/**
 * @author Nathan Hapke
 */
public class MbiyfCommand implements TextCommand, CalendaredEvent<MbiyfMode> {
	private static final TextFragment EXCLAMATION = new TextFragment("!");
	private static final TextFragment APOSTROPHE_S = new TextFragment("'s ");
	private static final TextFragment IN = new TextFragment(" in ");
	private static final TextFragment MY = new TextFragment("My ");
	private static final TextFragment NOT_BALLSING_MYSELF = new TextFragment(": Fuck you, I'm not ballsing myself!");
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
	private MbiyfType mode = MbiyfType.Off;
	private CategoriedItems<ImageLink> mbiyFridayImages;
	private List<ImageLink> fridayImages;

	public final static String[] ballsTriggers = new String[] { "balls", "mbiyf" };
	private static final String FRIDAY_IMAGES = "mbiyfImages";

	public MbiyfCommand(CampingBot campingBot, Resources res) {
		this.bot = campingBot;
		this.res = res;

		this.mbiyFridayImages = new CategoriedItems<ImageLink>(FRIDAY_IMAGES);
		fridayImages = mbiyFridayImages.getList(FRIDAY_IMAGES);
		for (int i = 1; i <= 4; i++) {
			String url = "http://www.hapke.ca/images/mbiyf" + i + ".mp4";
			ImageLink lnk = new ImageLink(url, ImageLink.GIF);
			fridayImages.add(lnk);
		}
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

		CampingUser targetUser = bot.findTarget(message);

		if (userRestriction != null && !userRestriction.contains(targetUser))
			return null;

		if (targetUser == bot.getMeCamping()) {
			return new TextCommandResult(BotCommand.MbiyfDipshit, new MentionFragment(campingFromUser),
					NOT_BALLSING_MYSELF);
		}

		Emoji ball = res.getRandomBallEmoji();
		Emoji face = res.getRandomFaceEmoji();

		if (targetUser == null)
			return null;

		campingFromUser.increment(BotCommand.Mbiyf);
		CommandResult result = new TextCommandResult(BotCommand.Mbiyf, MY).add(ball).add(IN).add(targetUser)
				.add(APOSTROPHE_S);
		if (mode == MbiyfType.Birthday) {
			result.add(res.getCake());
		}
		result.add(face).add(EXCLAMATION);
		return result;
	}

	@Override
	public boolean isMatch(String msg, Message message) {
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
		mode = value.getType();
		enabled = value != null && value.isEnablement();
		userRestriction = value.getRestrictedToUsers();
		boolean makeAnnouncement = shouldAnnounce && bot.isOnline();
		StringBuilder sb = new StringBuilder();
		EventItem e;
		try {
			if (enabled && makeAnnouncement) {
				announce(value);
			}
			sb.append(value.toString());
			sb.append(" Announce?[");
			sb.append(makeAnnouncement);
			sb.append("]");
			e = new EventItem(sb.toString());
		} catch (TelegramApiException ex) {
			e = new EventItem(ex.getMessage());
		}
		EventLogger.getInstance().add(e);
	}

	public void announce(MbiyfMode value) throws TelegramApiException {
		long chatId = CampingSystem.getInstance().getAnnounceChat();
		if (chatId == -1)
			return;
		MbiyfType type = value.getType();
		CommandResult result = null;
		switch (type) {
		case Birthday:
			result = announceBirthday(value);
			break;
		case Friday:
			result = announceFriday(value);
			break;
		case Off:
			break;
		}

		if (result != null) {
			result.send(bot, chatId);
		}
	}

	public CommandResult announceBirthday(MbiyfMode value) throws TelegramApiException {
		Emoji cake = res.getCake();

//		StringBuilder sb = new StringBuilder();
		TextCommandResult sb = new TextCommandResult(BotCommand.MbiyfAnnouncement);
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
				sb.add(emoji);
			}
		}
		sb.add("\n");
		Emoji poopUni = res.getBall("poop");
		sb.add(poopUni);
		sb.add("OHHHHH SHITTTT");
		sb.add(poopUni);

		sb.add("\nHEY ");
		appendBirthdayNames(sb);
		sb.add("...\n");

		for (int i = bar.size() - 1; i >= 0; i--) {
			Emoji emoji = bar.get(i);
			for (int j = 0; j < REPEATS; j++) {
				sb.add(emoji);
			}
		}

		sb.add("\n\nWATCH OUT FOR MY\n");

		int qty = 6;
		List<Emoji> emojis = new ArrayList<Emoji>(qty);
		getQty(res::getRandomBallEmoji, emojis, qty);
		for (Emoji emoji : emojis) {
			sb.add(emoji);
		}

		sb.add("\nIN YOUR\n");
		for (int i = 0; i < qty; i++) {
			sb.add(cake);
		}

		sb.add("\n\nHAPPY BIRTHDAY\n...AND KISS MY ASS");

		return sb;
	}

	public CommandResult announceFriday(MbiyfMode value) throws TelegramApiException {
		ImageLink image = CampingUtil.getRandom(fridayImages);
		ImageCommandResult result = new ImageCommandResult(BotCommand.MbiyfAnnouncement, image);
		result.add("It's M");
		result.add(res.getRandomBallEmoji());
		result.add("IY");
		result.add(res.getRandomFaceEmoji());
		result.add("riday motha'uckas!");

		return result;
	}

	private void appendBirthdayNames(TextCommandResult sb) {
		if (userRestriction != null) {
			int size = userRestriction.size();
			for (int i = 0; i < size; i++) {
				CampingUser u = userRestriction.get(i);
				if (i > 0) {
					int last = size - 1;
					if (i < last) {
						sb.add(", ");

					} else if (i == last) {
						sb.add(" AND ");
					}
				}
				sb.add(new MentionFragment(u, MentionDisplay.First, CaseChoice.Upper, null, null));
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
