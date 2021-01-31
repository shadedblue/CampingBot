package ca.hapke.campingbot.afd2021;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.hapke.calendaring.event.CalendaredEvent;
import ca.hapke.calendaring.event.StartupMode;
import ca.hapke.calendaring.monitor.CalendarMonitor;
import ca.hapke.calendaring.timing.ByFrequency;
import ca.hapke.calendaring.timing.TimesProvider;
import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.channels.CampingChat;
import ca.hapke.campingbot.channels.CampingChatManager;
import ca.hapke.campingbot.commands.PleasureModelCommand;
import ca.hapke.campingbot.response.ImageCommandResult;
import ca.hapke.campingbot.util.ImageLink;
import ca.odell.glazedlists.EventList;

/**
 * @author Nathan Hapke
 */
public abstract class AfdImagesStage<T> extends Stage implements CalendaredEvent<T> {

	protected final List<ImageLink> images;
	protected final Map<ImageLink, String> captionMap;
	protected final TimesProvider<T> times;
	protected final CampingBot bot;
	private boolean enabled = false;
	protected int i = 0;
	private EventList<CampingChat> announceChats;

	protected abstract void populateImages(List<ImageLink> images, Map<ImageLink, String> captionMap);

	public AfdImagesStage(CampingBot bot) {
		this.bot = bot;
		times = new TimesProvider<T>(getFrequency());

		images = new ArrayList<>();
		captionMap = new HashMap<ImageLink, String>();

		announceChats = CampingChatManager.getInstance(bot).getAnnounceChats();
	}

	protected abstract ByFrequency<T> getFrequency();

	@Override
	public final void doWork(T value) {
		if (!bot.isOnline()) {
			complete(false);
			return;
		}
		if (i >= images.size()) {
			complete(true);
			return;
		}
		ImageLink image = images.get(i);
		for (CampingChat chat : announceChats) {
			ImageCommandResult send = new ImageCommandResult(PleasureModelCommand.PleasureModelCommand, image);
			String caption = captionMap.get(image);
			if (caption != null)
				send.add(caption);

			send.sendAndLog(bot, chat);
		}
		i++;
	}

	@Override
	protected final void begin2() {
		populateImages(images, captionMap);
		enabled = true;
		times.generateNearestEvents();
		CalendarMonitor.getInstance().add(this);
	}

	@Override
	protected void complete2(boolean success) {
		enabled = false;
		CalendarMonitor.getInstance().remove(this);
	}

	@Override
	public boolean shouldRun() {
		return enabled;
	}

	@Override
	public TimesProvider<T> getTimeProvider() {
		return times;
	}

	protected ImageLink getAybImgUrl(String category, int i) {
		return new ImageLink(
				"http://www.hapke.ca/images/afd21/ayb-" + category + "-" + (i < 10 ? "0" : "") + i + ".png",
				ImageLink.STATIC);
	}

	@Override
	public StartupMode getStartupMode() {
		return StartupMode.Conditional;
	}
}
