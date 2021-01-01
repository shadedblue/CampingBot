package ca.hapke.campingbot.events;

import ca.hapke.calendaring.event.CalendaredEvent;
import ca.hapke.calendaring.event.StartupMode;
import ca.hapke.calendaring.timing.ByTimeOfYear;
import ca.hapke.calendaring.timing.TimesProvider;
import ca.hapke.campingbot.api.CampingBotEngine;
import ca.hapke.campingbot.channels.CampingChat;
import ca.hapke.campingbot.channels.CampingChatManager;
import ca.hapke.campingbot.commands.PleasureModelCommand;
import ca.hapke.campingbot.response.ImageCommandResult;
import ca.hapke.campingbot.util.ImageLink;

/**
 * @author Nathan Hapke
 */
public class HappyNewYearEvent implements CalendaredEvent<Void> {

	private static final String HNY_CAPTION = "HAPPY NEW YEAR, MOTHER FUCKERS!";
	private TimesProvider<Void> times = new TimesProvider<Void>(new ByTimeOfYear<Void>(1, 1, 0, 0, null));
	private CampingBotEngine bot;
	private CampingChatManager chatMgr;

	public HappyNewYearEvent(CampingBotEngine bot) {
		this.bot = bot;
		this.chatMgr = CampingChatManager.getInstance(bot);
	}

	@Override
	public TimesProvider<Void> getTimeProvider() {
		return times;
	}

	@Override
	public void doWork(Void value) {
		for (CampingChat chat : chatMgr.getAnnounceChats()) {
			ImageLink image = new ImageLink("http://www.hapke.ca/images/farva-spray.gif", ImageLink.GIF);
			ImageCommandResult send = new ImageCommandResult(PleasureModelCommand.PleasureModelCommand, image);
			send.add(HNY_CAPTION);

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
