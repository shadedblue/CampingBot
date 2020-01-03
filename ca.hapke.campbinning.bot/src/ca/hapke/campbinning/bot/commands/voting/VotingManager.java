package ca.hapke.campbinning.bot.commands.voting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBotEngine;
import ca.hapke.campbinning.bot.CampingSerializable;
import ca.hapke.campbinning.bot.category.CategoriedItems;
import ca.hapke.campbinning.bot.category.HasCategories;
import ca.hapke.campbinning.bot.commands.CallbackCommand;
import ca.hapke.campbinning.bot.interval.IntervalBySeconds;
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
public class VotingManager extends CampingSerializable implements IntervalBySeconds, CallbackCommand, HasCategories {

	private Map<Integer, VoteTracker> voteOnMessages = new HashMap<Integer, VoteTracker>();
	private Map<Integer, VoteTracker> voteOnBanners = new HashMap<Integer, VoteTracker>();

	private EventList<VoteTracker> inProgress = GlazedLists.threadSafeList(new BasicEventList<VoteTracker>());

	private CategoriedItems<String> resultCategories;

	public VotingManager() {
		resultCategories = new CategoriedItems<>(AitaTracker.assholeLevels);
		for (String s : AitaTracker.assholeLevels) {
			for (int i = 0; i < 3; i++) {
				resultCategories.put(s, s.substring(0, 1) + i);
			}
		}
	}

	@Override
	public int getSeconds() {
		return 60;
	}

	@Override
	public void doWork() {
		for (int i = 0; i < inProgress.size(); i++) {
			VoteTracker r = inProgress.get(i);

			long now = System.currentTimeMillis();
			if (now > r.getCompletionTime() || r.isCompleted()) {
				r.complete();
				float score = r.getScore();
				r.getRanter().completeRant(score);
				r.getActivater().increment(BotCommand.VoteActivatorComplete);
				inProgress.remove(r);
				continue;
			} else {
				r.update();
			}
		}
	}

	public String startAita(CampingBotEngine bot, Message activation, Long chatId, CampingUser activater)
			throws TelegramApiException, VoteCreationFailedException {
		String rest;

		Message topic = activation.getReplyToMessage();
		if (topic != null) {
			Integer rantMessageId = topic.getMessageId();
			if (voteOnMessages.containsKey(rantMessageId)) {
				throw new VoteCreationFailedException(VoteCreationFailedException.ALREADY_BEING_VOTED_ON);
			} else {
				CampingUserMonitor uM = CampingUserMonitor.getInstance();
				CampingUser ranter = uM.monitor(topic.getFrom());

				VoteTracker rant = new AitaTracker(bot, ranter, activater, chatId, activation, topic, resultCategories);

				inProgress.add(rant);
				voteOnMessages.put(rantMessageId, rant);
				voteOnBanners.put(rant.getBanner().getMessageId(), rant);
				rest = topic.getText();
				ranter.increment(BotCommand.RantActivatorInitiation);
			}
		} else {
			throw new VoteCreationFailedException(VoteCreationFailedException.NO_TOPIC_PROVIDED);
		}
		return rest;
	}

	public String startRant(CampingBotEngine bot, Message activation, Long chatId, CampingUser activater)
			throws TelegramApiException, VoteCreationFailedException {
		String rest;

		Message topic = activation.getReplyToMessage();
		if (topic != null) {
			Integer rantMessageId = topic.getMessageId();
			if (voteOnMessages.containsKey(rantMessageId)) {
				throw new VoteCreationFailedException(VoteCreationFailedException.ALREADY_BEING_VOTED_ON);
			} else {
				CampingUserMonitor uM = CampingUserMonitor.getInstance();
				CampingUser ranter = uM.monitor(topic.getFrom());

				VoteTracker tracker = new RantTracker(bot, ranter, activater, chatId, activation, topic);

				inProgress.add(tracker);
				voteOnMessages.put(rantMessageId, tracker);
				voteOnBanners.put(tracker.getBanner().getMessageId(), tracker);
				rest = topic.getText();
				ranter.increment(BotCommand.RantActivatorInitiation);
			}
		} else {
			throw new VoteCreationFailedException(VoteCreationFailedException.NO_TOPIC_PROVIDED);
		}
		return rest;
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
