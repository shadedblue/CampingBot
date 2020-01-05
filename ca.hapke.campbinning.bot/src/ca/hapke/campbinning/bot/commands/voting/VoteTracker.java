package ca.hapke.campbinning.bot.commands.voting;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.users.CampingUserMonitor;
import ca.hapke.campbinning.bot.util.TimeFormatter;

/**
 * @author Nathan Hapke
 *
 */
public abstract class VoteTracker<T> {

	public abstract float getScore();


	public abstract String getBannerText();

	protected CampingBotEngine bot;
	protected final CampingUser ranter;
	protected final CampingUser activater;
	protected final Long chatId;
	protected CampingChat chat;
	protected Map<CampingUser, T> votes = new HashMap<>();
	protected Set<CampingUser> votesNotApplicable = new HashSet<>();
	protected boolean completed = false;
	protected NumberFormat nf;
	protected Message voteTrackingMessage;
	protected String previousVotes;
	protected Message bannerMessage;
	protected String previousBanner;
	protected Message topicMessage;
	protected final long creationTime = System.currentTimeMillis();
	protected final long completionTime = System.currentTimeMillis() + getVotingTime();
	protected TimeFormatter formatter = new TimeFormatter(1, "", false, true);
	protected final String[] buttonText;
	protected final String[] buttonValue;

	protected static final String VOTING_COMPLETED = "Voting Completed!\n";
	protected static final String NOT_APPLICABLE = "na";
	protected final int naQuorum;

	public VoteTracker(CampingBotEngine bot, CampingUser ranter, CampingUser activater, Long chatId, Message activation,
			Message topic, String[] buttonText, String[] buttonValue, int naQuorum) throws TelegramApiException {
		this.ranter = ranter;
		this.activater = activater;
		this.chatId = chatId;
		this.buttonText = buttonText;
		this.buttonValue = buttonValue;
		this.naQuorum = naQuorum;
		this.bot = bot;
		this.chat = CampingChatManager.getInstance().get(chatId, bot);
		this.nf = NumberFormat.getInstance();

		nf.setMinimumFractionDigits(0);
		nf.setMaximumFractionDigits(1);

		previousBanner = getBannerText();
		SendMessage out = new SendMessage(chatId, previousBanner);
		out.setReplyMarkup(getKeyboard());
		out.setParseMode("Markdown");
		out.setReplyToMessageId(activation.getMessageId());
		bannerMessage = bot.execute(out);

		previousVotes = getVotesText(completed);
		out = new SendMessage(chatId, previousVotes);
		out.setParseMode("Markdown");
		out.setReplyToMessageId(topic.getMessageId());
		voteTrackingMessage = bot.execute(out);
		this.topicMessage = topic;
	}

	/**
	 * @return milliseconds
	 */
	protected abstract long getVotingTime();

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
			if (NOT_APPLICABLE.equalsIgnoreCase(vote)) {
				votes.remove(user);
				votesNotApplicable.add(user);
				if (votesNotApplicable.size() >= naQuorum)
					complete();
			} else {
				T score = processVote(vote);
				try {
					votes.put(user, score);
					votesNotApplicable.remove(user);
				} catch (NumberFormatException e) {
					votes.remove(user);
					votesNotApplicable.add(user);
				}
			}
			String newVotesMessage = getVotesText(completed);
			if (voteChanged) {
				if (attemptMessageEdit(voteTrackingMessage, newVotesMessage, previousVotes))
					previousVotes = newVotesMessage;
			}

			AnswerCallbackQuery answer = new AnswerCallbackQuery();
			answer.setText("Received your vote of: " + display + "!");
			answer.setCallbackQueryId(callbackQueryId);
			try {
				bot.execute(answer);
				user.increment(BotCommand.Vote);
				return new EventItem(BotCommand.Vote, user, null, chat, topicMessage.getMessageId(), display,
						topicMessage.getMessageId());
			} catch (Exception e) {
				return new EventItem(e.getLocalizedMessage());
			}

		}

		return null;
	}

	protected abstract T processVote(String vote);

	protected InlineKeyboardMarkup getKeyboard() {
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

	public void complete() {
		if (completed)
			return;
	
		completed = true;
		String newMsg = VOTING_COMPLETED;
		if (attemptMessageEdit(bannerMessage, newMsg, previousBanner))
			previousBanner = newMsg;
	
		String votes = getVotesText(true);
		SendMessage completionMsg = new SendMessage(chatId, votes);
		completionMsg.setParseMode("Markdown");
		Integer messageId = topicMessage.getMessageId();
		completionMsg.setReplyToMessageId(messageId);
		EventLogger logger = EventLogger.getInstance();
		try {
			bot.execute(completionMsg);
			if (votesNotApplicable.size() < naQuorum) {
				logger.add(
						new EventItem(BotCommand.VoteTopicComplete, ranter, null, chat, messageId, votes, messageId));
				logger.add(new EventItem(BotCommand.VoteActivatorComplete, activater, null, chat, messageId, "",
						messageId));
			}
		} catch (TelegramApiException e) {
			logger.add(new EventItem(e.getLocalizedMessage()));
		}
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

	protected String getVote(CampingUser user) {
		if (votesNotApplicable.contains(user))
			return NOT_APPLICABLE;
		else {
			T vote = votes.get(user);
			if (vote != null)
				return makeStringFromVote(vote);
		}
		return null;
	}

	protected String getVotesText(boolean completed) {
		int notApplicable = votesNotApplicable.size();
		int naturalVotes = votes.size();
		int count = naturalVotes + notApplicable;
		float score = getScore();
		String scoreStr;
		if (naturalVotes > 0) {
			scoreStr = nf.format(score) ;
			String scoreSuffix = getScoreSuffix();
			if (scoreSuffix != null && scoreSuffix.length() > 0)
				scoreStr = scoreStr + scoreSuffix;
		} else {
			scoreStr = "(n/a)";
		}

		StringBuilder sb = new StringBuilder();
		addVotesTextPrefix(completed, sb);


		if (shouldShowVotesInCategories()) {
			int[] votes = new int[buttonValue.length];
			for (T vote : this.votes.values()) {
				for (int i = 0; i < buttonValue.length; i++) {
					String txt = buttonValue[i];
					if (vote instanceof String && txt.equalsIgnoreCase((String) vote)) {
						votes[i]++;
						break;
					}
				}
			}
			for (int i = 0; i < buttonValue.length; i++) {
				String txt = buttonValue[i];
				if (NOT_APPLICABLE.equalsIgnoreCase(txt)) {
					votes[i] = votesNotApplicable.size();
					break;
				}
			}
			for (int i = 0; i < buttonText.length; i++) {
				String txt = buttonText[i];
				sb.append("\n*");
				sb.append(txt);
				sb.append("*: ");
				sb.append(votes[i]);
			}
		}

		sb.append("\n--------\nTotal Votes: *");
		sb.append(count);

		sb.append("*\n");
		if (completed)
			sb.append("Final");
		else
			sb.append("Current");
		sb.append(" Score: *");
		sb.append(scoreStr);
		sb.append("*");

		// if (!completed) {
		// sb.append("\nNot Applicable Votes: *");
		// sb.append(notApplicable);
		// sb.append("*");
		// }
		addVotesTextSuffix(sb, completed, score);

		return sb.toString();
	}

	protected abstract boolean shouldShowVotesInCategories();

	public void addVotesTextPrefix(boolean completed, StringBuilder sb) {
		if (completed)
			sb.append(VOTING_COMPLETED);
	}

	public void addVotesTextSuffix(StringBuilder sb, boolean completed, float score) {
	}

	protected abstract String getScoreSuffix();

	protected abstract String makeStringFromVote(T vote);

	public CampingUser getRanter() {
		return ranter;
	}

	public CampingUser getActivater() {
		return activater;
	}

	public Message getBanner() {
		return bannerMessage;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public long getCompletionTime() {
		return completionTime;
	}

	public boolean isCompleted() {
		return completed;
	}

}