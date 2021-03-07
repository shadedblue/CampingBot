package ca.hapke.campingbot.voting;

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
import ca.hapke.calendaring.timing.ByCalendar;
import ca.hapke.calendaring.timing.ByFrequency;
import ca.hapke.calendaring.timing.TimesProvider;
import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.api.CampingBotEngine;
import ca.hapke.campingbot.callback.api.CallbackCommandBase;
import ca.hapke.campingbot.callback.api.CallbackId;
import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.ResponseCommandType;
import ca.hapke.campingbot.commands.api.SlashCommand;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.commands.api.TextCommand;
import ca.hapke.campingbot.log.EventItem;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.fragments.InsultFragment;
import ca.hapke.campingbot.response.fragments.InsultFragment.Perspective;
import ca.hapke.campingbot.response.fragments.MentionFragment;
import ca.hapke.campingbot.response.fragments.TextFragment;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.users.CampingUserMonitor;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

/**
 * @author Nathan Hapke
 */
public abstract class VotingCommand<T> extends CallbackCommandBase
		implements CalendaredEvent<Void>, TextCommand, SlashCommand {
	public static final ResponseCommandType VoteTopicInitiationCommand = new ResponseCommandType("VoteTopicInitiation",
			BotCommandIds.VOTING | BotCommandIds.REGULAR_CHAT | BotCommandIds.SET);
	public static final ResponseCommandType VoteActivatorCompleteCommand = new ResponseCommandType(
			"VoteActivatorComplete", BotCommandIds.VOTING | BotCommandIds.FINISH);
	public static final ResponseCommandType VoteTopicCompleteCommand = new ResponseCommandType("VoteTopicComplete",
			BotCommandIds.VOTING | BotCommandIds.REGULAR_CHAT | BotCommandIds.FINISH);
	public static final ResponseCommandType VoteCommandFailedCommand = new ResponseCommandType("VoteCommandFailed",
			BotCommandIds.VOTING | BotCommandIds.FAILURE);

	public static final ResponseCommandType VoteCommand = new ResponseCommandType("Vote",
			BotCommandIds.VOTING | BotCommandIds.USE);

	protected final Map<String, VoteTracker<T>> voteOnMessages = new HashMap<String, VoteTracker<T>>();
	protected final Map<Integer, VoteTracker<T>> voteOnBanners = new HashMap<Integer, VoteTracker<T>>();

	protected final EventList<VoteTracker<T>> inProgress = GlazedLists
			.threadSafeList(new BasicEventList<VoteTracker<T>>());

	protected final CampingBot bot;
	private TimesProvider<Void> times;
//	protected final BotCommand respondsTo;
	private SlashCommandType[] SLASH_COMMANDS;
	private static final TextFragment ALREADY_BEING_VOTED_ON = new TextFragment("Topic already being voted on, ");
	private static final TextFragment NO_TOPIC_PROVIDED = new TextFragment(
			"Reply to the topic you would like to vote on, ");

	public VotingCommand(CampingBot campingBot, SlashCommandType... respondsTo) {
		this.SLASH_COMMANDS = respondsTo;
		this.bot = campingBot;
		times = new TimesProvider<Void>(new ByFrequency<Void>(null, 15, ChronoUnit.SECONDS));
	}

	@Override
	public SlashCommandType[] getSlashCommandsToRespondTo() {
		return SLASH_COMMANDS;
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
	public void doWork(ByCalendar<Void> event, Void value) {
		for (int i = 0; i < inProgress.size(); i++) {
			VoteTracker<T> r = inProgress.get(i);

			long now = System.currentTimeMillis();
			if (now > r.getCompletionTime() || r.isCompleted()) {
				r.complete();
				inProgress.remove(r);
				continue;
			} else {
				r.update();
			}
		}
	}

	@Override
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, Long chatId,
			CampingUser campingFromUser) {
		CommandResult result;

		try {
			Message topic = message.getReplyToMessage();
			if (topic != null) {
				result = startVotingInternal(bot, message, chatId, campingFromUser, topic);
			} else {
				result = new TextCommandResult(VoteCommandFailedCommand, NO_TOPIC_PROVIDED,
						new InsultFragment(Perspective.You));
			}
		} catch (Exception e) {
			result = new TextCommandResult(VoteCommandFailedCommand, new TextFragment(e.getMessage()));
		}
		return result;
	}

	private CommandResult startVotingInternal(CampingBotEngine bot, Message activation, Long chatId,
			CampingUser activater, Message topic) throws TelegramApiException {
		VoteTracker<T> tracker = null;
		TextCommandResult output = null;
		Integer targetMessageId = topic.getMessageId();

		// if (voteOnMessages.containsKey(targetMessageId)) {
		boolean alreadyVoting = false;
		for (String s : voteOnMessages.keySet()) {
			try {
				s = s.substring(0, s.indexOf(AbstractCommand.DELIMITER));
			} catch (Exception e) {
			}
			if (s.equals(targetMessageId.toString())) {
				alreadyVoting = true;
				break;
			}
		}
		if (alreadyVoting) {
			return new TextCommandResult(VoteCommandFailedCommand, ALREADY_BEING_VOTED_ON,
					new InsultFragment(Perspective.You));
		} else {
			CampingUserMonitor uM = CampingUserMonitor.getInstance();
			CampingUser ranter = uM.monitor(topic.getFrom());

			try {
				tracker = initiateVote(ranter, activater, chatId, activation, topic);
				addTracker(tracker);
			} catch (VoteInitiationException e) {
				output = new TextCommandResult(VoteCommandFailedCommand, new MentionFragment(activater));
				output.add(e.getMessage());
			}

		}
		if (output == null && tracker != null) {
			output = tracker.getBannerText();
		}
		return output;
	}

	protected void addTracker(VoteTracker<T> tracker) throws TelegramApiException {
		if (tracker != null) {
			tracker.begin();
			inProgress.add(tracker);
			String key = tracker.getKey();
			voteOnMessages.put(key, tracker);
			voteOnBanners.put(tracker.getBanner().getMessageId(), tracker);
		}
	}

	protected abstract VoteTracker<T> initiateVote(CampingUser ranter, CampingUser activater, Long chatId,
			Message activation, Message topic) throws VoteInitiationException, TelegramApiException;

	@Override
	public EventItem reactToCallback(CallbackId id, CallbackQuery callbackQuery) {
		String key = createKey(id);
		VoteTracker<T> v = voteOnMessages.get(key);
		try {
			if (v != null) {
				return v.react(id, callbackQuery);
			} else {
				return new EventItem("Failed to find the tracker for key: " + key);
			}
		} catch (TelegramApiException e) {
			return new EventItem("Failed to react to callback: " + callbackQuery.getData());
		}
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
	public CommandResult textCommand(CampingUser campingFromUser, List<MessageEntity> entities, Long chatId,
			Message message) throws TelegramApiException {

		String msgLower = message.getText().toLowerCase().trim();
		for (SlashCommandType respondsTo : SLASH_COMMANDS) {
			if (msgLower.endsWith("/" + respondsTo.slashCommand))
				return startVotingInternal(bot, message, chatId, campingFromUser, message);

		}
		return null;
	}

	@Override
	public boolean isMatch(String msg, Message message) {
		msg = msg.toLowerCase().trim();
		for (SlashCommandType respondsTo : SLASH_COMMANDS) {
			if (msg.endsWith("/" + respondsTo.slashCommand))
				return true;
		}
		return false;
	}

	protected String createKey(CallbackId id) {
		int messageId = id.getUpdateId();
		return Integer.toString(messageId);
	}

}
