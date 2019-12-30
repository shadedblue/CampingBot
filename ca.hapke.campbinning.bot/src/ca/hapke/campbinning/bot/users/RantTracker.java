package ca.hapke.campbinning.bot.users;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ocpsoft.prettytime.PrettyTime;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBotEngine;
import ca.hapke.campbinning.bot.channels.CampingChat;
import ca.hapke.campbinning.bot.channels.CampingChatManager;
import ca.hapke.campbinning.bot.log.EventItem;
import ca.hapke.campbinning.bot.log.EventLogger;

/**
 * @author Nathan Hapke
 */
public class RantTracker {
	private static final String VOTING_COMPLETED = "Voting Completed!";
	private static final String NOT_RANT = "nr";
	private static final String[] buttonText = new String[] { "-1", "0", "+1", "+2", "Not Rant" };
	private static final String[] buttonValue = new String[] { "-1", "0", "1", "2", NOT_RANT };
	private static final int NOT_RANT_QUORUM = 2;

	private static long getRantVotingTime() {
		return 60 * 60 * 1000;
	}

	private CampingBotEngine bot;
	private final CampingUser ranter, activater;
	private final Long chatId;
	private CampingChat chat;

	private Map<CampingUser, Integer> votes = new HashMap<>();
	private Set<CampingUser> votesNotRant = new HashSet<>();
	private boolean completed = false;

	private NumberFormat nf;
	private Message voteTrackingMessage;
	private String previousVotes;
	private Message bannerMessage;
	private String previousBanner;
	private Message rantMessage;
	private final long creationTime = System.currentTimeMillis();
	private final long completionTime = System.currentTimeMillis() + getRantVotingTime();
	private PrettyTime formatter = new PrettyTime();

	public RantTracker(CampingBotEngine bot, CampingUser ranter, CampingUser activater, Long chatId, Message activation,
			Message rant) throws TelegramApiException {
		this.ranter = ranter;
		this.activater = activater;
		this.chatId = chatId;
		this.chat = CampingChatManager.getInstance().get(chatId, bot);

		this.nf = NumberFormat.getInstance();
		this.bot = bot;
		nf.setMinimumFractionDigits(0);
		nf.setMaximumFractionDigits(1);

		previousBanner = getBannerText();
		SendMessage out = new SendMessage(chatId, previousBanner);
		out.setReplyMarkup(getKeyboard());
		out.setParseMode("Markdown");
		out.setReplyToMessageId(activation.getMessageId());
		bannerMessage = bot.execute(out);

		previousVotes = getVotesText();
		out = new SendMessage(chatId, previousVotes);
		out.setParseMode("Markdown");
		out.setReplyToMessageId(rant.getMessageId());
		voteTrackingMessage = bot.execute(out);
		this.rantMessage = rant;
	}

	private InlineKeyboardMarkup getKeyboard() {
		InlineKeyboardMarkup board = new InlineKeyboardMarkup();
		List<InlineKeyboardButton> row = new ArrayList<>();
		for (int i = 0; i < buttonText.length && i < buttonValue.length; i++) {
			String text = buttonText[i];
			String value = buttonValue[i];
			row.add(new InlineKeyboardButton(text).setCallbackData(value));
		}
		List<List<InlineKeyboardButton>> fullKeyboard = new ArrayList<List<InlineKeyboardButton>>();
		fullKeyboard.add(row);
		board.setKeyboard(fullKeyboard);
		return board;
	}

	public void update() {
		String newBanner = getBannerText();
		if (attemptMessageEdit(bannerMessage, newBanner, previousBanner))
			previousBanner = newBanner;
	}

	public String getBannerText() {
		return "Rant Scoring! (" + formatter.formatDuration(new Date(completionTime)) + " left)";
	}

	private String getVotesText() {
		int notRant = votesNotRant.size();
		int count = votes.size() + notRant;
		String avg = count > 0 ? nf.format(getScore()) : "-";
		String completePrefix = completed ? VOTING_COMPLETED + "\n" : "";
		return completePrefix + "Votes: *" + count + "*\nRant Quality: *" + avg + "*\nNot Rant Votes: *" + notRant
				+ "*";
	}

	public void complete() {
		if (completed)
			return;

		completed = true;
		String newMsg = VOTING_COMPLETED;
		if (attemptMessageEdit(bannerMessage, newMsg, previousBanner))
			previousBanner = newMsg;

		String votes = getVotesText();
		SendMessage completionMsg = new SendMessage(chatId, votes);
		completionMsg.setParseMode("Markdown");
		Integer messageId = rantMessage.getMessageId();
		completionMsg.setReplyToMessageId(messageId);
		try {
			bot.execute(completionMsg);
			EventLogger logger = EventLogger.getInstance();
			if (votesNotRant.size() < NOT_RANT_QUORUM) {
				logger.add(
						new EventItem(BotCommand.RantRanterComplete, ranter, null, chat, messageId, votes, messageId));
				logger.add(new EventItem(BotCommand.RantActivatorComplete, activater, null, chat, messageId, "",
						messageId));
			}
		} catch (TelegramApiException e) {
		}
	}

	public EventItem react(CallbackQuery callbackQuery) throws TelegramApiException {
		if (completed)
			return null;

		String data = callbackQuery.getData();
		String callbackQueryId = callbackQuery.getId();

		String vote = null, display = null;
		for (int i = 0; i < buttonValue.length; i++) {
			String value = buttonValue[i];
			if (value.equalsIgnoreCase(data)) {
				vote = value;
				display = buttonText[i];
				break;
			}
		}
		if (vote != null) {
			CampingUser user = CampingUserMonitor.getInstance().monitor(callbackQuery.getFrom());

			boolean voteChanged = !vote.equalsIgnoreCase(getVote(user));
			if (NOT_RANT.equalsIgnoreCase(vote)) {
				votes.remove(user);
				votesNotRant.add(user);
				if (votesNotRant.size() >= NOT_RANT_QUORUM)
					complete();
			} else {
				Integer score = Integer.valueOf(vote);
				try {
					votes.put(user, score);
					votesNotRant.remove(user);
				} catch (NumberFormatException e) {
					votes.remove(user);
					votesNotRant.add(user);
				}
			}
			String newVotesMessage = getVotesText();
			if (voteChanged) {
				if (attemptMessageEdit(voteTrackingMessage, newVotesMessage, previousVotes))
					previousVotes = newVotesMessage;
			}

			AnswerCallbackQuery answer = new AnswerCallbackQuery();
			answer.setText("Received your rant vote of: " + display + "!");
			answer.setCallbackQueryId(callbackQueryId);
			try {
				bot.execute(answer);
				user.increment(BotCommand.RantVote);
				return new EventItem(BotCommand.RantVote, user, null, chat, rantMessage.getMessageId(), display,
						rantMessage.getMessageId());
			} catch (Exception e) {
			}

		}

		return null;
	}

	public boolean attemptMessageEdit(Message msg, String newMsg, String previousMsg) {
		if (newMsg == null || newMsg.equalsIgnoreCase(previousMsg))
			return false;

		EditMessageText update = new EditMessageText();
		update.setChatId(chatId);
		if (msg == bannerMessage && !completed)
			update.setReplyMarkup(getKeyboard());
		update.setMessageId(msg.getMessageId());
		update.setText(newMsg);
		update.setParseMode("Markdown");
		try {
			bot.execute(update);
		} catch (TelegramApiException e) {
			return false;
		}
		return true;
	}

	private String getVote(CampingUser user) {
		if (votesNotRant.contains(user))
			return NOT_RANT;
		else {
			Integer integer = votes.get(user);
			if (integer != null)
				return integer.toString();
		}
		return null;
	}

	public float getScore() {
		int count = votes.size() + votesNotRant.size();
		if (count == 0)
			return 0;

		int sum = 0;
		for (int x : votes.values()) {
			sum += x;
		}
		float avg = ((float) sum) / count;
		return avg;
	}

	public CampingUser getRanter() {
		return ranter;
	}

	public CampingUser getActivater() {
		return activater;
	}

	public Message getBanner() {
		return bannerMessage;
	}

	public boolean isCompleted() {
		return completed;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public long getCompletionTime() {
		return completionTime;
	}

}
