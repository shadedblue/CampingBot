package ca.hapke.campbinning.bot.commands.voting;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.calendaring.event.CalendaredEvent;
import ca.hapke.calendaring.event.StartupMode;
import ca.hapke.calendaring.timing.ByFrequency;
import ca.hapke.calendaring.timing.TimesProvider;
import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBot;
import ca.hapke.campbinning.bot.CampingBotEngine;
import ca.hapke.campbinning.bot.CampingSerializable;
import ca.hapke.campbinning.bot.category.CategoriedItems;
import ca.hapke.campbinning.bot.category.HasCategories;
import ca.hapke.campbinning.bot.commands.CallbackCommand;
import ca.hapke.campbinning.bot.commands.TextCommand;
import ca.hapke.campbinning.bot.commands.response.CommandResult;
import ca.hapke.campbinning.bot.commands.response.TextCommandResult;
import ca.hapke.campbinning.bot.commands.response.fragments.TextFragment;
import ca.hapke.campbinning.bot.log.EventItem;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.users.CampingUserMonitor;
import ca.hapke.campbinning.bot.xml.OutputFormatter;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

/**
 * @author Nathan Hapke
 */
@SuppressWarnings("rawtypes")
public class VotingManager extends CampingSerializable
		implements CalendaredEvent<Void>, CallbackCommand, TextCommand, HasCategories {

	private Map<Integer, VoteTracker> voteOnMessages = new HashMap<Integer, VoteTracker>();
	private Map<Integer, VoteTracker> voteOnBanners = new HashMap<Integer, VoteTracker>();

	private EventList<VoteTracker> inProgress = GlazedLists.threadSafeList(new BasicEventList<VoteTracker>());

	private CategoriedItems<String> resultCategories;
	private CampingBot bot;
	private TimesProvider<Void> times;
	public static final String ALREADY_BEING_VOTED_ON = "Topic already being voted on";
	public static final String NO_TOPIC_PROVIDED = "Reply to the topic you would like to vote on!";

	public VotingManager(CampingBot campingBot) {
		this.bot = campingBot;
		times = new TimesProvider<Void>(new ByFrequency<Void>(null, 15, ChronoUnit.SECONDS));
		resultCategories = new CategoriedItems<>(AitaTracker.assholeLevels);
	}

	@Override
	public TimesProvider<Void> getTimeProvider() {
		return times;
	}

	@Override
	public StartupMode getStartupMode() {
		return StartupMode.Never;
	}

	@Override
	public void doWork(Void value) {
		for (int i = 0; i < inProgress.size(); i++) {
			VoteTracker r = inProgress.get(i);

			long now = System.currentTimeMillis();
			if (now > r.getCompletionTime() || r.isCompleted()) {
				r.complete();
//				float score = r.getScore();
				r.getActivater().increment(BotCommand.VoteActivatorComplete);
				inProgress.remove(r);
				continue;
			} else {
				r.update();
			}
		}
	}

	public CommandResult startVoting(BotCommand type, CampingBotEngine bot, Message activation, Long chatId,
			CampingUser activater) {
//		String rest;
		CommandResult result;

		try {
			Message topic = activation.getReplyToMessage();
			if (topic != null) {
				result = startVotingInternal(type, bot, activation, chatId, activater, topic);
			} else {
				result = new TextCommandResult(BotCommand.VoteInitiationFailed,
						new TextFragment(VotingManager.NO_TOPIC_PROVIDED));
			}
		} catch (Exception e) {
			result = new TextCommandResult(BotCommand.VoteInitiationFailed, new TextFragment(e.getMessage()));
		}
		return result;
	}

	private CommandResult startVotingInternal(BotCommand type, CampingBotEngine bot, Message activation, Long chatId,
			CampingUser activater, Message topic) throws TelegramApiException {
//		String rest;
		VoteTracker tracker = null;
		Integer rantMessageId = topic.getMessageId();
		if (voteOnMessages.containsKey(rantMessageId)) {
//			throw new VoteCreationFailedException(VoteCreationFailedException.ALREADY_BEING_VOTED_ON);
			return new TextCommandResult(BotCommand.VoteInitiationFailed,
					new TextFragment(VotingManager.ALREADY_BEING_VOTED_ON));
		} else {
			CampingUserMonitor uM = CampingUserMonitor.getInstance();
			CampingUser ranter = uM.monitor(topic.getFrom());
			switch (type) {
			case AitaActivatorInitiation:
				tracker = new AitaTracker(bot, ranter, activater, chatId, activation, topic, resultCategories);
				break;
			case RantActivatorInitiation:
				tracker = new RantTracker(bot, ranter, activater, chatId, activation, topic);
				break;
			default:
				return null;
			}

			inProgress.add(tracker);
			voteOnMessages.put(rantMessageId, tracker);
			voteOnBanners.put(tracker.getBanner().getMessageId(), tracker);
//			rest = topic.getText();
			ranter.increment(BotCommand.RantActivatorInitiation);
		}
		return new TextCommandResult(BotCommand.VoteTopicInitiation, new TextFragment(tracker.getBannerTitle()),
				new TextFragment(topic.getText()));
	}

	@Override
	public EventItem reactToCallback(CallbackQuery callbackQuery) {
		Integer callbackMessageId = callbackQuery.getMessage().getMessageId();
		VoteTracker rant = voteOnBanners.get(callbackMessageId);

		try {
			if (rant != null) {
				EventItem react = rant.react(callbackQuery);
				return react;
			}
		} catch (TelegramApiException e) {
		}
		return null;
	}

	@Override
	public boolean shouldRun() {
		boolean result = false;
		if (inProgress.getReadWriteLock().readLock().tryLock()) {
			try {
				result = inProgress.size() > 0;
			} finally {
				inProgress.getReadWriteLock().readLock().unlock();
			}
		}
		return result;
	}

	@Override
	public List<String> getCategoryNames() {
		return resultCategories.getCategoryNames();
	}

	@Override
	public void addItem(String category, String value) {
		resultCategories.put(category, value);
	}

	@Override
	public CommandResult textCommand(CampingUser campingFromUser, List<MessageEntity> entities, Long chatId,
			Message message) {

		BotCommand type = null;
		String msgLower = message.getText().toLowerCase().trim();
		if (msgLower.endsWith("/" + BotCommand.AitaActivatorInitiation.command))
			type = BotCommand.AitaActivatorInitiation;
		else if (msgLower.endsWith("/" + BotCommand.RantActivatorInitiation.command))
			type = BotCommand.RantActivatorInitiation;

		if (type != null) {
			try {
				return startVotingInternal(type, bot, message, chatId, campingFromUser, message);
			} catch (TelegramApiException e) {
			}
		}
		return null;
	}

	@Override
	public boolean isMatch(String msg, List<MessageEntity> entities) {
		msg = msg.toLowerCase().trim();
		if (msg.endsWith("/" + BotCommand.AitaActivatorInitiation.command))
			return true;
		if (msg.endsWith("/" + BotCommand.RantActivatorInitiation.command))
			return true;
		return false;
	}

	@Override
	public String getContainerName() {
		return "Voting";
	}

	@Override
	public void getXml(OutputFormatter of) {
		String tag = "voting";
		of.start(tag);
		of.tagCategories(resultCategories);
		of.finish(tag);
	}

}
