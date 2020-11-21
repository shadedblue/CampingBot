package ca.hapke.campingbot.afd2020;

import java.time.temporal.ChronoUnit;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.calendaring.event.CalendaredEvent;
import ca.hapke.calendaring.event.StartupMode;
import ca.hapke.calendaring.timing.ByFrequency;
import ca.hapke.calendaring.timing.TimesProvider;
import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.channels.CampingChat;
import ca.hapke.campingbot.commands.PleasureModelCommand;
import ca.hapke.campingbot.log.EventItem;
import ca.hapke.campingbot.log.EventLogger;
import ca.hapke.campingbot.response.ImageCommandResult;
import ca.hapke.campingbot.response.SendResult;
import ca.hapke.campingbot.util.ImageLink;

/**
 * @author Nathan Hapke
 */
public class AfdMatrixPictures implements CalendaredEvent<Void> {

	private boolean enabled = false;

	private TimesProvider<Void> times;

	private int i = 1;
	private static final String[] captions;

	private CampingBot bot;

	private CampingChat chat;
	static {
		captions = new String[57];
		captions[1] = "Would you like the red pill, or the blue pill?";
		captions[2] = "HOLY SHIT. I know kung fu!";
		captions[3] = "Show me";
		captions[4] = "Hit me. If you can...";
		captions[9] = "Okay... soo.... what do you need?";
		captions[10] = "Guns. Lots of Guns.";
		captions[13] = "Would you please remove any metallic items you are carrying? keys, loose change...";
		captions[15] = "Holy shit!";
		captions[33] = "Find them and destroy them!";
		captions[50] = "Dodge this";
		captions[53] = "... no";
	}

	public AfdMatrixPictures(CampingBot bot, CampingChat chat) {
		this.bot = bot;
		this.chat = chat;
		times = new TimesProvider<Void>(new ByFrequency<Void>(null, 10, ChronoUnit.MINUTES));
	}

	@Override
	public TimesProvider<Void> getTimeProvider() {
		return times;
	}

	@Override
	public void doWork(Void value) {
		if (!bot.isOnline() && i <= 57)
			return;

		ImageLink image = new ImageLink("http://www.hapke.ca/images/matrix-" + i + ".jpg", ImageLink.STATIC);
		ImageCommandResult send = new ImageCommandResult(PleasureModelCommand.PleasureModelCommand, image);
		String caption = captions[i];
		if (caption != null)
			send.add(caption);

		sendImage(send, caption);
		i++;

	}

	public void sendImage(ImageCommandResult send, String caption) {
		try {
			SendResult result = send.send(bot, chat.chatId);

			Message outgoingMsg = result.outgoingMsg;
			EventItem ei = new EventItem(PleasureModelCommand.PleasureModelCommand, bot.getMeCamping(), outgoingMsg.getDate(), chat,
					outgoingMsg.getMessageId(), caption, null);
			EventLogger.getInstance().add(ei);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean shouldRun() {
		return enabled;
	}

	@Override
	public StartupMode getStartupMode() {
		return StartupMode.Never;
	}

	public void enable(boolean on) {
		if (enabled && !on) {
			// finishing
			ImageLink image = new ImageLink("http://www.hapke.ca/images/42069.jpg", ImageLink.STATIC);
			String caption = "APRIL FOOLS, MOTHER FUCKERS";
			ImageCommandResult send = new ImageCommandResult(PleasureModelCommand.PleasureModelCommand, image);
			send.add(caption);

			sendImage(send, caption);
		}
		enabled = on;
	}

}
