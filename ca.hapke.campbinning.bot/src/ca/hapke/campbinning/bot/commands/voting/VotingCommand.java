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
import ca.hapke.campbinning.bot.commands.SlashCommand;
import ca.hapke.campbinning.bot.commands.TextCommand;
import ca.hapke.campbinning.bot.commands.callback.CallbackCommandBase;
import ca.hapke.campbinning.bot.commands.callback.CallbackId;
import ca.hapke.campbinning.bot.commands.response.CommandResult;
import ca.hapke.campbinning.bot.commands.response.TextCommandResult;
import ca.hapke.campbinning.bot.commands.response.fragments.InsultFragment;
import ca.hapke.campbinning.bot.commands.response.fragments.InsultFragment.Perspective;
import ca.hapke.campbinning.bot.commands.response.fragments.MentionFragment;
import ca.hapke.campbinning.bot.commands.response.fragments.TextFragment;
import ca.hapke.campbinning.bot.log.EventItem;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.users.CampingUserMonitor;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

/**
 * @author Nathan Hapke
 */
@SuppressWarnings("rawtypes")
public abstract class VotingCommand extends CallbackCommandBase
		implements CalendaredEvent<Void>, TextCommand, SlashCommand {

	protected final Map<Integer, VoteTracker> voteOnMessages = new HashMap<Integer, VoteTracker>();
	protected final Map<Integer, VoteTracker> voteOnBanners = new HashMap<Integer, VoteTracker>();

	protected final EventList<VoteTracker> inProgress = GlazedLists.threadSafeList(new BasicEventList<VoteTracker>());

	protected final CampingBot bot;
	private TimesProvider<Void> times;
//	protected final BotCommand respondsTo;
	private BotCommand[] SLASH_COMMANDS;
	private static final TextFragment ALREADY_BEING_VOTED_ON = new TextFragment("Topic already being voted on, ");
	private static final TextFragment NO_TOPIC_PROVIDED = new TextFragment(
			"Reply to the topic you would like to vote on, ");

	public VotingCommand(CampingBot campingBot, BotCommand respondsTo) {
		this.SLASH_COMMANDS = new BotCommand[] { respondsTo };
		this.bot = campingBot;
		times = new TimesProvider<Void>(new ByFrequency<Void>(null, 15, ChronoUnit.SECONDS));
	}

	@Override
	public BotCommand[] getSlashCommandsToRespondTo() {
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
	public void doWork(Void value) {
		for (int i = 0; i < inProgress.size(); i++) {
			VoteTracker r = inProgress.get(i);

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
	public CommandResult respondToSlashCommand(BotCommand command, Message message, Long chatId, CampingUser campingFromUser) {
		CommandResult result;

		try {
			Message topic = message.getReplyToMessage();
			if (topic != null) {
				result = startVotingInternal(command, bot, message, chatId, campingFromUser, topic);
			} else {
				result = new TextCommandResult(BotCommand.VoteCommandFailed, NO_TOPIC_PROVIDED,
						new InsultFragment(Perspective.You));
			}
		} catch (Exception e) {
			result = new TextCommandResult(BotCommand.VoteCommandFailed, new TextFragment(e.getMessage()));
		}
		return result;
	}

	private CommandResult startVotingInternal(BotCommand type, CampingBotEngine bot, Message activation, Long chatId,
			CampingUser activater, Message topic) throws TelegramApiException {
		VoteTracker tracker = null;
		TextCommandResult output = null;
		Integer rantMessageId = topic.getMessageId();
		if (voteOnMessages.containsKey(rantMessageId)) {
			return new TextCommandResult(BotCommand.VoteCommandFailed, ALREADY_BEING_VOTED_ON,
					new InsultFragment(Perspective.You));
		} else {
			CampingUserMonitor uM = CampingUserMonitor.getInstance();
			CampingUser ranter = uM.monitor(topic.getFrom());

			try {
				tracker = initiateVote(ranter, activater, chatId, activation, topic);
			} catch (VoteInitiationException e) {
				output = new TextCommandResult(BotCommand.VoteCommandFailed, new MentionFragment(activater));
				output.add(e.getMessage());
			}

			if (tracker != null) {
				inProgress.add(tracker);
				voteOnMessages.put(rantMessageId, tracker);
				voteOnBanners.put(tracker.getBanner().getMessageId(), tracker);
			}
		}
		if (output == null && tracker != null) {
			output = tracker.getBannerText();
		}
		return output;
	}

	protected abstract VoteTracker initiateVote(CampingUser ranter, CampingUser activater, Long chatId,
			Message activation, Message topic) throws VoteInitiationException, TelegramApiException;

	@Override
	public EventItem reactToCallback(CallbackId id, CallbackQuery callbackQuery) {
		int callbackMessageId = id.getUpdateId();
		VoteTracker rant = voteOnMessages.get(callbackMessageId);

		try {
			if (rant != null) {
				EventItem react = rant.react(id, callbackQuery);
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
	public CommandResult textCommand(CampingUser campingFromUser, List<MessageEntity> entities, Long chatId,
			Message message) {

		BotCommand type = null;
		String msgLower = message.getText().toLowerCase().trim();
		for (BotCommand respondsTo : SLASH_COMMANDS) {
			if (msgLower.endsWith("/" + respondsTo.command))
				type = respondsTo;

			if (type != null) {
				try {
					return startVotingInternal(type, bot, message, chatId, campingFromUser, message);
				} catch (TelegramApiException e) {
				}
			}
		}
		return null;
	}

	@Override
	public boolean isMatch(String msg, Message message) {
		msg = msg.toLowerCase().trim();
		for (BotCommand respondsTo : SLASH_COMMANDS) {
			if (msg.endsWith("/" + respondsTo.command))
				return true;
		}
		return false;
	}

}
