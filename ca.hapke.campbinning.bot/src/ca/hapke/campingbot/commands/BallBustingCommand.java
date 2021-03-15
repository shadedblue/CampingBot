package ca.hapke.campingbot.commands;

import java.util.HashMap;
import java.util.Map;

import ca.hapke.calendaring.event.CalendaredEvent;
import ca.hapke.calendaring.event.StartupMode;
import ca.hapke.calendaring.timing.ByCalendar;
import ca.hapke.calendaring.timing.ByTimeOfYear;
import ca.hapke.calendaring.timing.TimesProvider;
import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.api.CampingBotEngine;
import ca.hapke.campingbot.channels.CampingChat;
import ca.hapke.campingbot.channels.CampingChatManager;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.ResponseCommandType;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.ImageCommandResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.fragments.CaseChoice;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.users.CampingUserMonitor;
import ca.hapke.campingbot.util.ImageLink;

/**
 * @author Nathan Hapke
 */
public class BallBustingCommand implements CalendaredEvent<Integer>/* , PostConfigInit */ {

	public static final ResponseCommandType BallBustingCommand = new ResponseCommandType("BallBusting",
			BotCommandIds.PLEASURE | BotCommandIds.GIF);

	private TimesProvider<Integer> times = new TimesProvider<Integer>();
	private CampingBotEngine bot;
	private CampingChatManager chatMgr;
	private Map<Integer, String> bustingText = new HashMap<>();
	private Map<Integer, ImageLink> bustingImgs = new HashMap<>();

	public BallBustingCommand(CampingBot bot) {
		this.bot = bot;
		this.chatMgr = CampingChatManager.getInstance(bot);
		Integer rtv = Integer.valueOf(558638791);
		times.add(new ByTimeOfYear<Integer>(3, 14, 19, 54, rtv));
		addContent(rtv, new ImageLink("http://www.hapke.ca/images/ballbusting/rtv-koreans.mp4", ImageLink.GIF),
				": HAPPY BIRTHDAY FROM THESE KOREAN TRANNIES!");
	}

	private void addContent(Integer id, ImageLink image, String msg) {
		if (msg != null)
			bustingText.put(id, msg);
		if (image != null)
			bustingImgs.put(id, image);
	}

//	@Override
//	public void init() {
//		// TODO Auto-generated method stub
//
//	}

	@Override
	public TimesProvider<Integer> getTimeProvider() {
		return times;
	}

	@Override
	public void doWork(ByCalendar<Integer> timingEvent, Integer value) {
		ImageLink img = bustingImgs.get(value);
		String text = bustingText.get(value);
		CampingUser user = CampingUserMonitor.getInstance().getUser(value);
		for (CampingChat chat : chatMgr.getAnnounceChats()) {
			CommandResult send;
			if (img != null) {
				send = new ImageCommandResult(BallBustingCommand, img);
			} else if (text != null) {
				send = new TextCommandResult(BallBustingCommand);
			} else {
				return;
			}
			if (user != null)
				send.add(user, CaseChoice.Upper);
			send.add(text);
			send.sendAndLog(bot, chat);
		}

	}

	@Override
	public boolean shouldRun() {
		return true;
	}

	@Override
	public StartupMode getStartupMode() {
		return StartupMode.Never;
	}
}
