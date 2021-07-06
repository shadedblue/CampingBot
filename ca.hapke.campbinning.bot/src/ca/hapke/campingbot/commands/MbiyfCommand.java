package ca.hapke.campingbot.commands;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import ca.hapke.campingbot.BotChoicePriority;
import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.Resources;
import ca.hapke.campingbot.api.PostConfigInit;
import ca.hapke.campingbot.category.CategoriedImageLinks;
import ca.hapke.campingbot.category.CategoriedItems;
import ca.hapke.campingbot.channels.CampingChat;
import ca.hapke.campingbot.channels.CampingChatManager;
import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.ResponseCommandType;
import ca.hapke.campingbot.commands.api.TextCommand;
import ca.hapke.campingbot.log.EventItem;
import ca.hapke.campingbot.log.EventLogger;
import ca.hapke.campingbot.processors.CrazyCaseProcessor;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.ImageCommandResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.fragments.CaseChoice;
import ca.hapke.campingbot.response.fragments.MentionDisplay;
import ca.hapke.campingbot.response.fragments.MentionFragment;
import ca.hapke.campingbot.response.fragments.TextFragment;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.users.CampingUser.Birthday;
import ca.hapke.campingbot.users.CampingUserMonitor;
import ca.hapke.campingbot.util.ImageLink;
import ca.odell.glazedlists.EventList;

/**
 * @author Nathan Hapke
 */
public class MbiyfCommand extends AbstractCommand implements TextCommand, CalendaredEvent<MbiyfMode>, PostConfigInit {

	public static final ResponseCommandType MbiyfAnnouncementCommand = new ResponseCommandType("MbiyfAnnouncement",
			BotCommandIds.BALLS | BotCommandIds.SET);
	public static final ResponseCommandType MbiyfCommand = new ResponseCommandType("Mbiyf",
			BotCommandIds.BALLS | BotCommandIds.USE);
	public static final ResponseCommandType MbiyfDipshitCommand = new ResponseCommandType("MbiyfDipshit",
			BotCommandIds.BALLS | BotCommandIds.FAILURE);

	private static final String BALLS = "balls";
	private static final String MBIYF = "mbiyf";
	private static final TextFragment EXCLAMATION = new TextFragment("!");
	private static final TextFragment APOSTROPHE_S = new TextFragment("'s ");
	private static final TextFragment IN = new TextFragment(" in ");
	private static final TextFragment MY = new TextFragment("My ");
	private static final TextFragment NOT_BALLSING_MYSELF = new TextFragment(": Fuck you, I'm not ballsing myself!");
	private static final int BIRTHDAY_ENABLE_LENGTH_HOURS = 17;
	private static final int ENABLE_HOUR = 7;
	private static final int ENABLE_MIN = 0;
	private static final int DISABLE_HOUR = 0;

	private CampingBot bot;
	private Resources res;
	private boolean enabled = false;
	private boolean shouldAnnounce = false;
	private TimesProvider<MbiyfMode> times = new TimesProvider<>();
	private List<CampingUser> userRestriction;
	private MbiyfType mode = MbiyfType.Off;
	private CategoriedItems<ImageLink> mbiyfImages;
	private static final String FRIDAY_IMAGES = "MBIYFriday";
//	private List<ImageLink> fridayImages;
//	private List<ImageLink> birthdayImages;
	private static final String BIRTHDAY_IMAGES = "MBirthdayIYF";

	public final static String[] ballsTriggers = new String[] { BALLS, MBIYF };
	private CrazyCaseProcessor crazyCase = new CrazyCaseProcessor();

	public MbiyfCommand(CampingBot campingBot, Resources res) {
		this.bot = campingBot;
		this.res = res;

		this.mbiyfImages = new CategoriedImageLinks(FRIDAY_IMAGES);
//		fridayImages = mbiyfImages.getList(FRIDAY_IMAGES);
		for (int i = 1; i <= 7; i++) {
			String url = "http://www.hapke.ca/images/mbiyf" + i + ".mp4";
			ImageLink lnk = new ImageLink(url, ImageLink.GIF);
			mbiyfImages.put(FRIDAY_IMAGES, lnk);
//			fridayImages.add(lnk);
		}
//		birthdayImages = mbiyfImages.getList(BIRTHDAY_IMAGES);
		for (int i = 1; i <= 4; i++) {
			String url = "http://www.hapke.ca/images/birthday" + i + ".mp4";
			ImageLink lnk = new ImageLink(url, ImageLink.GIF);
			mbiyfImages.put(BIRTHDAY_IMAGES, lnk);
//			birthdayImages.add(lnk);
		}
	}

	@Override
	public void init() {
		CampingUserMonitor monitor = CampingUserMonitor.getInstance();
		List<ByCalendar<MbiyfMode>> targets = new ArrayList<>();
		targets.add(new ByTimeOfWeek<MbiyfMode>(DayOfWeek.FRIDAY, ENABLE_HOUR, 0, new MbiyfMode(MbiyfType.Friday)));
		targets.add(new ByTimeOfWeek<MbiyfMode>(DayOfWeek.SATURDAY, DISABLE_HOUR, 0, new MbiyfMode(MbiyfType.Off)));

		Map<String, List<CampingUser>> birthdayMap = new HashMap<>();
		for (CampingUser u : monitor.getUsers()) {
			Birthday b = u.getBirthday();
			if (b != null) {
				String key = b.getKey();
				List<CampingUser> lst = birthdayMap.get(key);
				if (lst == null) {
					lst = new ArrayList<>();
					birthdayMap.put(key, lst);
				}
				lst.add(u);
			}
		}

		int len = BIRTHDAY_ENABLE_LENGTH_HOURS;
		ChronoUnit unit = ChronoUnit.HOURS;
		for (List<CampingUser> usersByDay : birthdayMap.values()) {
			Birthday birthday = usersByDay.get(0).getBirthday();
			ByTimeOfYear<MbiyfMode> enable = new ByTimeOfYear<MbiyfMode>(birthday.getMonth(), birthday.getDay(),
					ENABLE_HOUR, ENABLE_MIN, new MbiyfMode(MbiyfType.Birthday, usersByDay));
			targets.add(enable);
			ZonedDateTime enableTime = enable.generateATargetTime();
			ByTimeOfYear<MbiyfMode> disable = createDisableAfter(enableTime, len, unit);
			targets.add(disable);
		}

		List<CampingUser> specialUsers = Collections.singletonList(CampingUserMonitor.getInstance().getUser(708570894));
		ByTimeOfYear<MbiyfMode> enableSpecial = new ByTimeOfYear<MbiyfMode>(7, 9, ENABLE_HOUR, ENABLE_MIN,
				new MbiyfMode(MbiyfType.Special, specialUsers));
		targets.add(enableSpecial);
		ZonedDateTime enableSpecialTime = enableSpecial.generateATargetTime();
		ByTimeOfYear<MbiyfMode> disableSpecial = createDisableAfter(enableSpecialTime, len, unit);
		targets.add(disableSpecial);

		times.addAll(targets);
		shouldAnnounce = true;
	}

	public ByTimeOfYear<MbiyfMode> createDisableAfter(ZonedDateTime enableTime, int len, ChronoUnit unit) {
		ZonedDateTime disableTime = enableTime.plus(len, unit);

		ByTimeOfYear<MbiyfMode> disable = new ByTimeOfYear<MbiyfMode>(disableTime.getMonthValue(),
				disableTime.getDayOfMonth(), disableTime.getHour(), disableTime.getMinute(),
				new MbiyfMode(MbiyfType.Off));
		return disable;
	}

	@Override
	public CommandResult textCommand(CampingUser campingFromUser, List<MessageEntity> entities, Long chatId,
			Message message) {
		CampingUser targetUser = bot.findTarget(message, false, true, BotChoicePriority.Last);

		if (userRestriction != null && !userRestriction.contains(targetUser))
			return null;
		if (targetUser == null)
			return null;

		CampingUser me = bot.getMeCamping();
		if (targetUser == me && !userRestriction.contains(me)) {
			return new TextCommandResult(MbiyfDipshitCommand, new MentionFragment(campingFromUser),
					NOT_BALLSING_MYSELF);
		}

		Emoji ball = res.getRandomBallEmoji();
		Emoji face = res.getRandomFaceEmoji();
		CommandResult result;

		if (targetUser == me) {
			result = new TextCommandResult(MbiyfCommand).add(campingFromUser).add(APOSTROPHE_S).add(ball).add(IN)
					.add(MY);
		} else {
			result = new TextCommandResult(MbiyfCommand, MY).add(ball).add(IN).add(targetUser).add(APOSTROPHE_S);
		}

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
	public void doWork(ByCalendar<MbiyfMode> event, MbiyfMode value) {
		mode = value.getType();
		enabled = value != null && value.isEnablement();
		crazyCase.setEnabled(enabled);
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
		EventList<CampingChat> announceChats = CampingChatManager.getInstance(bot).getAnnounceChats();
		for (CampingChat chat : announceChats) {
			long chatId = chat.getChatId();
			if (chatId == -1)
				return;
			MbiyfType type = value.getType();
			CommandResult result = null;
			switch (type) {
			case Birthday:
				result = announceBirthday();
				break;
			case Friday:
				result = announceFriday();
				break;
			case Asshole:
				result = announceAsshole();
				break;
			case Special:
				result = announceSpecial();
				break;
			case Off:
				break;
			}

			if (result != null) {
				result.send(bot, chatId);
			}
		}
	}

	public CommandResult announceBirthday() throws TelegramApiException {
		Emoji cake = res.getCake();

		ImageLink image = mbiyfImages.getRandom(BIRTHDAY_IMAGES);
		ImageCommandResult result = new ImageCommandResult(MbiyfAnnouncementCommand, image);
		result.add("M");
		result.add(res.getRandomBallEmoji());
		result.add("I ");
		appendNames(result, true);
		result.add(" ");
		result.add(cake);
		result.add(res.getRandomFaceEmoji());

		return result;
	}

	private CommandResult announceSpecial() {
		ImageLink image = mbiyfImages.getRandom(FRIDAY_IMAGES);
		ImageCommandResult result = new ImageCommandResult(MbiyfAnnouncementCommand, image);

		result.add("APPARANTLY TODAY IS SPECIAL FOR ");
		appendNames(result, false);
		result.add(" SO M");
		result.add(res.getRandomBallEmoji());
		result.add("IY");
		result.add(res.getRandomFaceEmoji());
		result.add("!");

		return result;

	}

	public CommandResult announceAsshole() throws TelegramApiException {
		ImageLink image = mbiyfImages.getRandom(FRIDAY_IMAGES);
		ImageCommandResult result = new ImageCommandResult(MbiyfAnnouncementCommand, image);
		result.add(userRestriction.get(0));
		result.add(": M");
		result.add(res.getRandomBallEmoji());
		result.add("IYAssh");
		result.add(res.getRandomFaceEmoji());
		result.add("le!");

		return result;
	}

	public CommandResult announceFriday() throws TelegramApiException {
		ImageLink image = mbiyfImages.getRandom(FRIDAY_IMAGES);
		ImageCommandResult result = new ImageCommandResult(MbiyfAnnouncementCommand, image);
		result.add("It's M");
		result.add(res.getRandomBallEmoji());
		result.add("IY");
		result.add(res.getRandomFaceEmoji());
		result.add("riday motha'uckas!");

		return result;
	}

	private void appendNames(CommandResult cr, boolean addApostropheS) {
		if (userRestriction != null) {
			int size = userRestriction.size();
			for (int i = 0; i < size; i++) {
				CampingUser u = userRestriction.get(i);
				if (i > 0) {
					int last = size - 1;
					if (i < last) {
						cr.add(", ");

					} else if (i == last) {
						cr.add(" AND ");
					}
				}
				cr.add(new MentionFragment(u, MentionDisplay.First, CaseChoice.Upper));
			}
			if (addApostropheS)
				cr.add("'S");
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

	@Override
	public String getCommandName() {
		return MBIYF;
	}

	public CrazyCaseProcessor getCrazyCase() {
		return crazyCase;
	}
}
