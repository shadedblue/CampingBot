package ca.hapke.campingbot.afd2024;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Consumer;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.calendaring.event.CalendaredEvent;
import ca.hapke.calendaring.event.StartupMode;
import ca.hapke.calendaring.timing.ByCalendar;
import ca.hapke.calendaring.timing.ByFrequency;
import ca.hapke.calendaring.timing.TimesProvider;
import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.Resources;
import ca.hapke.campingbot.afd2021.AybTopicChanger;
import ca.hapke.campingbot.channels.CampingChat;
import ca.hapke.campingbot.channels.CampingChatManager;
import ca.hapke.campingbot.commands.PleasureModelCommand;
import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.ResponseCommandType;
import ca.hapke.campingbot.commands.api.TextCommand;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.ImageCommandResult;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.util.ImageLink;
import ca.odell.glazedlists.EventList;

public class AfdTooManyDicks extends AbstractCommand implements CalendaredEvent<Void>, TextCommand {

	private CampingBot bot;
	private Resources res;

	private boolean enabled = false;
	private TimesProvider<Void> times;
	private int i = 1;

	private static final String[] captions;

	private static final String TOO_MANY_DICKS = "TooManyDicks";
	private EventList<CampingChat> announceChats;

	private AybTopicChanger topicChanger;

	public static final ResponseCommandType TooManyDicksCommand = new ResponseCommandType(TOO_MANY_DICKS,
			BotCommandIds.SILLY_RESPONSE | BotCommandIds.VOTING | BotCommandIds.USE);

	static {
		captions = new String[16];
		captions[1] = "Going to the party\n" + "Sippin' on Bacardi\n" + "Wanna meet a hottie";
		captions[2] = "But there's Adam, Steve and Marty\n" + "There's Billy, Todd and Tommy\n"
				+ "They're on leave from the army";
		captions[3] = "The only boobs I'll see tonight will be made of origami";
		captions[4] = "Tell the players, make it understood\n" + "It ain't no good if there's too much wood\n"
				+ "Make sure you know before you go";
		captions[5] = "The dance floor bro-hoe ratio\n" + "Five to one is a brodeo\n"
				+ "Tell Steve and Mike it's time to go";
		captions[6] = "Wait outside all night to find\n" + "Twenty dudes in a conga line";
		captions[7] = "Easy to fix" + "TOO MANY DICKS ON THE DANCE FLOOR!\n" + "Spread out the dicks";
		captions[8] = "Too many dudes\n" + "With too many dicks";
		captions[9] = "Too close to my shit\n" + "Too hard to meet chicks";
		captions[10] = "I need better odds\n" + "More broads, less rods";
		captions[11] = "I came to do battle\n" + "Scadaddle with the cattle prods";
		captions[12] = "Too many men\n" + "Too many boys\n" + "Too many misters\n" + "Not enough sisters";
		captions[13] = "Too much time on, too many hands\n" + "Not enough ladies, too many mans";
		captions[14] = "Too many dongs\n" + "Too many schlongs\n" + "Now sing this song";
		captions[15] = "Too many dicks on the dance floor\n" + "Too many dicks on the dance floor\n"
				+ "Too many dicks\n" + "Too many dicks on the dance floor\n" + "Too many dicks\n"
				+ "Too many dicks on the dance floor";
	}

	public AfdTooManyDicks(CampingBot bot, Resources res) {
		this.bot = bot;
		this.res = res;
		CampingChatManager chatMonitor = CampingChatManager.getInstance(bot);
		announceChats = chatMonitor.getAnnounceChats();
		times = new TimesProvider<Void>(new ByFrequency<Void>(null, 1, ChronoUnit.MINUTES));
		topicChanger = new AybTopicChanger(bot, res);
	}

	@Override
	public TimesProvider<Void> getTimeProvider() {
		return times;
	}

	@Override
	public void doWork(ByCalendar<Void> event, Void value) {
		if (!bot.isOnline() || i >= captions.length)
			return;

		ImageLink image = new ImageLink("http://www.hapke.ca/images/afd24/dick" + i + ".jpg", ImageLink.STATIC);
		ImageCommandResult send = new ImageCommandResult(PleasureModelCommand.PleasureModelCommand, image);
		send.add("TOO MANY DICKS ON THE DANCE FLOOR!");
		send.newLine();
		send.newLine();
		String caption = captions[i];
		if (caption != null)
			send.add(caption);

		for (CampingChat chat : announceChats) {
			send.sendAndLog(bot, chat);
		}
		i++;

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

			for (CampingChat chat : announceChats) {
				send.sendAndLog(bot, chat);
			}
		}
		enabled = on;
	}

	@Override
	public CommandResult textCommand(CampingUser campingFromUser, List<MessageEntity> entities, Long chatId,
			Message message) throws TelegramApiException {
		for (CampingChat chat : announceChats) {
			Consumer<CampingChat> changer = topicChanger.createTopicChanger();
			changer.accept(chat);
		}
		return null;
	}

	@Override
	public boolean isMatch(String msg, Message message) {
		return Math.random() < 0.10;
	}

	@Override
	public String getCommandName() {
		return TOO_MANY_DICKS;
	}
}
