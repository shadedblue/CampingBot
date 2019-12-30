package ca.hapke.campbinning.bot.commands;

import java.util.HashMap;
import java.util.Map;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBotEngine;
import ca.hapke.campbinning.bot.interval.IntervalBySeconds;
import ca.hapke.campbinning.bot.log.EventItem;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.users.CampingUserMonitor;
import ca.hapke.campbinning.bot.users.RantTracker;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

/**
 * @author Nathan Hapke
 */
public class RantManager implements IntervalBySeconds, CallbackCommand {
	private Map<Integer, RantTracker> rantMessages = new HashMap<Integer, RantTracker>();
	private Map<Integer, RantTracker> rantBanners = new HashMap<Integer, RantTracker>();

	private EventList<RantTracker> rantsInProgress = GlazedLists.threadSafeList(new BasicEventList<RantTracker>());

	@Override
	public int getSeconds() {
		return 60;
	}

	@Override
	public void doWork() {
		for (int i = 0; i < rantsInProgress.size(); i++) {
			RantTracker r = rantsInProgress.get(i);

			long now = System.currentTimeMillis();
			if (now > r.getCompletionTime() || r.isCompleted()) {
				r.complete();
				float score = r.getScore();
				r.getRanter().completeRant(score);
				r.getActivater().increment(BotCommand.RantActivatorComplete);
				rantsInProgress.remove(r);
				continue;
			} else {
				r.update();
			}
		}
	}

	public String startRant(CampingBotEngine bot, Message rantActivation, Long chatId, CampingUser activater)
			throws TelegramApiException, RantCreationFailedException {
		String rest;

		Message actualRant = rantActivation.getReplyToMessage();
		if (actualRant != null) {
			Integer rantMessageId = actualRant.getMessageId();
			if (rantMessages.containsKey(rantMessageId)) {
				throw new RantCreationFailedException(RantCreationFailedException.ALREADY_BEING_VOTED_ON);
			} else {
				CampingUserMonitor uM = CampingUserMonitor.getInstance();
				CampingUser ranter = uM.monitor(actualRant.getFrom());

				RantTracker rant = new RantTracker(bot, ranter, activater, chatId, rantActivation, actualRant);

				rantsInProgress.add(rant);
				rantMessages.put(rantMessageId, rant);
				rantBanners.put(rant.getBanner().getMessageId(), rant);
				rest = actualRant.getText();
				ranter.increment(BotCommand.RantActivatorInitiation);
			}
		} else {
			throw new RantCreationFailedException(RantCreationFailedException.NO_RANT_PROVIDED);
		}
		return rest;
	}

	@Override
	public EventItem reactToCallback(CallbackQuery callbackQuery) {
		Integer callbackMessageId = callbackQuery.getMessage().getMessageId();
		RantTracker rant = rantBanners.get(callbackMessageId);

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
		if (rantsInProgress.getReadWriteLock().readLock().tryLock()) {
			try {
				result = rantsInProgress.size() > 0;
			} finally {
				rantsInProgress.getReadWriteLock().readLock().unlock();
			}
		}
		return result;
	}
}
