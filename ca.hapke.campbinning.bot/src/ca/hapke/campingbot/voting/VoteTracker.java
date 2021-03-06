package ca.hapke.campingbot.voting;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.api.CampingBotEngine;
import ca.hapke.campingbot.callback.api.CallbackId;
import ca.hapke.campingbot.channels.CampingChat;
import ca.hapke.campingbot.channels.CampingChatManager;
import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.log.EventItem;
import ca.hapke.campingbot.log.EventLogger;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.EditTextCommandResult;
import ca.hapke.campingbot.response.SendResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.UnpinUtil;
import ca.hapke.campingbot.response.fragments.ResultFragment;
import ca.hapke.campingbot.response.fragments.TextFragment;
import ca.hapke.campingbot.response.fragments.TextStyle;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.users.CampingUserMonitor;
import ca.hapke.util.TimeFormatter;

/**
 * @author Nathan Hapke
 *
 */
public abstract class VoteTracker<T> {
	public abstract float getScore();

	protected CampingBotEngine bot;
	protected final CampingUser ranter;
	protected final CampingUser activater;
	protected final Long chatId;
	protected CampingChat chat;
	protected boolean completed = false;
	protected NumberFormat nf;
	protected Message voteTrackingMessage;
	protected Message bannerMessage;
	protected String previousBanner;
	protected Message topicMessage;
	protected final long creationTime = System.currentTimeMillis();
	protected long completionTime = System.currentTimeMillis() + getVotingTime();
	protected TimeFormatter formatter = new TimeFormatter(1, "", false, true);
	protected String[] shortButtons;
	protected String[] buttonCallbackIds;
	protected String[] longDescriptions;

//	protected Map<CampingUser, Integer> votes = new HashMap<>();
//	protected Set<CampingUser> votesNotApplicable = new HashSet<>();
//	protected final Map<Integer, T> valueMap = new HashMap<Integer, T>();

	protected final VoteCluster<T> cluster;

	protected boolean addNa;
	protected int naIndex;

	protected static final TextFragment VOTING_COMPLETED = new TextFragment("Voting Completed!");
	protected static final String NOT_APPLICABLE_SHORT = "N/A";
	protected static final String NOT_APPLICABLE_LONG = "Not Applicable";
	protected final int naQuorum;
	private SendResult bannerResult;
	private SendResult voteTrackingResult;
	private TextCommandResult bannerText;
	protected final boolean allowExtendComplete;
	protected final Message topic;
	protected final String command;
	protected final Message activation;
	private boolean beginComplete = false;

	protected final List<VoteChangedListener<T>> voteListeners = new ArrayList<VoteChangedListener<T>>();

	public VoteTracker(CampingBotEngine bot, CampingUser ranter, CampingUser activater, Long chatId, Message activation,
			Message topic, int naQuorum, String command, boolean allowExtendComplete) throws TelegramApiException {
		this.ranter = ranter;
		this.activater = activater;
		this.chatId = chatId;
		this.activation = activation;
		this.topic = topic;

		this.naQuorum = naQuorum;
		this.bot = bot;
		this.command = command;
		this.allowExtendComplete = allowExtendComplete;
		this.chat = CampingChatManager.getInstance(bot).get(chatId);
		this.nf = NumberFormat.getInstance();
		cluster = createCluster();
		nf.setMinimumFractionDigits(0);
		nf.setMaximumFractionDigits(1);
		voteListeners.add(new InternalVoteListener());
	}

	protected VoteCluster<T> createCluster() {
		return new VoteCluster<T>(this);
	}

	public void begin() throws TelegramApiException {
		if (beginComplete)
			return;
		else
			beginComplete = true;

		List<VotingOption<T>> optionsList = new ArrayList<>();
		addNa = createOptions(optionsList);

		int count = optionsList.size() + (addNa ? 1 : 0);
		shortButtons = new String[count];
		longDescriptions = new String[count];
		buttonCallbackIds = new String[count];
		int updateId = topic.getMessageId();
		for (int i = 0; i < optionsList.size(); i++) {
			VotingOption<T> opt = optionsList.get(i);
			String shortStr = opt.shortButton;
			String longStr = opt.longDescription;
			T value = opt.value;

			setOption(command, updateId, i, shortStr, longStr, value);
		}

		if (addNa) {
			naIndex = count - 1;
			setOption(command, updateId, naIndex, NOT_APPLICABLE_SHORT, NOT_APPLICABLE_LONG, null);
		} else {
			naIndex = -1;
		}
		List<ResultFragment> bannerFrags = getBannerString();
		bannerText = new TextCommandResult(VotingCommand.VoteTopicInitiationCommand, bannerFrags);
		bannerText.setReplyTo(activation.getMessageId());
		bannerText.setKeyboard(getKeyboard());
		bannerResult = bannerText.send(bot, chatId);
		previousBanner = bannerResult.msg;
		bannerMessage = bannerResult.outgoingMsg;

		TextCommandResult votesText = new TextCommandResult(VotingCommand.VoteTopicInitiationCommand,
				getVotesText(completed));
		votesText.setReplyTo(topic.getMessageId());
		voteTrackingResult = votesText.send(bot, chatId);
		voteTrackingMessage = voteTrackingResult.outgoingMsg;

		this.topicMessage = topic;

		int bannerId = bannerMessage.getMessageId();
		try {
			PinChatMessage pinBanner = new PinChatMessage(Long.toString(chatId), bannerId);
			pinBanner.setDisableNotification(Boolean.valueOf(true));
			bot.execute(pinBanner);
		} catch (TelegramApiException e) {
			EventLogger.getInstance().add(new EventItem(VotingCommand.VoteCommandFailedCommand, activater, null, chat,
					bannerId, "Failed to pin banner", bannerMessage));
		}
	}

	protected void setOption(String command, int updateId, long i, String shortStr, String longStr, T value) {
		CallbackId id = createCallbackId(command, updateId, i);
		String callbackId = id.getResult();

		int j = (int) i;
		shortButtons[j] = shortStr;
		longDescriptions[j] = longStr;
		buttonCallbackIds[j] = callbackId;
		Map<Long, T> valueMap = cluster.getValueMap();
		valueMap.put(i, value);
	}

	/**
	 * Overrideable if you want to do something fancy.
	 */
	protected String createClusterKey() {
		return "default";
	}

	/**
	 * Overrideable if you want to do something fancy.
	 */
	protected long getOptionId(CallbackId id) {
		return id.getIds()[0];
	}

	/**
	 * Overrideable if you want to do something fancy.
	 */
	protected CallbackId createCallbackId(String command, int updateId, long i) {
		return new CallbackId(command, updateId, i);
	}

	/**
	 * @return add Not Applicable?
	 */
	protected abstract boolean createOptions(List<VotingOption<T>> optionsList);

	/**
	 * @return milliseconds
	 */
	protected long getVotingTime() {
		return 45 * 60 * 1000;
	}

	public EventItem react(CallbackId id, CallbackQuery callbackQuery) throws TelegramApiException {
		if (completed)
			return null;
		EventItem output = null;

//		String display = longDescriptions[id.getIds()[0]];

		CampingUser user = CampingUserMonitor.getInstance().monitor(callbackQuery.getFrom());

		Map<Long, T> valueMap = cluster.getValueMap();
		Map<CampingUser, Long> votes = cluster.getVotes();
		Set<CampingUser> votesNotApplicable = cluster.getVotesNotApplicable();
		long optionId = getOptionId(id);

		Long previousVote = votes.get(user);
		boolean prevVoteNA = votesNotApplicable.contains(user);

		boolean voteChanged = false;
		if (previousVote == null && !prevVoteNA) {
			// first vote
			voteChanged = true;
		} else if (previousVote != null && optionId != previousVote) {
			// had a non-n/a vote, and new vote is different
			voteChanged = true;
		} else if (optionId == naIndex && !prevVoteNA) {
			// new vote is n/a, and wasn't before
			voteChanged = true;
		} else if (optionId != naIndex && prevVoteNA) {
			// new vote is not n/a, and was before
			voteChanged = true;
		}

		if (optionId == naIndex) {
			votes.remove(user);
			votesNotApplicable.add(user);
			if (votesNotApplicable.size() >= naQuorum) {
				complete();

				for (VoteChangedListener<T> vcl : voteListeners) {
					EventItem result = vcl.completedByUser(callbackQuery, user, optionId);
					if (result != null)
						output = result;
				}
			}

		} else {
			votes.put(user, optionId);
			votesNotApplicable.remove(user);
		}
		if (voteChanged) {
			for (VoteChangedListener<T> vcl : voteListeners) {
				EventItem result = vcl.changed(callbackQuery, user, optionId);
				if (result != null)
					output = result;
			}
		} else {
			for (VoteChangedListener<T> vcl : voteListeners) {
				EventItem result = vcl.confirmed(callbackQuery, user, optionId);
				if (result != null)
					output = result;
			}
		}

		return output;
	}

	protected EventItem showBanner(CallbackQuery callback, String displayToUser, CampingUser user) {
		String callbackQueryId = callback.getId();
		AnswerCallbackQuery answer = new AnswerCallbackQuery();

		answer.setText(displayToUser);
		answer.setCallbackQueryId(callbackQueryId);
		try {
			bot.execute(answer);
			return new EventItem(VotingCommand.VoteCommand, user, chat, bannerMessage.getMessageId(), displayToUser,
					topicMessage.getMessageId());
		} catch (Exception e) {
			return new EventItem(e.getLocalizedMessage());
		}
	}

	private class InternalVoteListener extends VoteChangedAdapter<T> {
		@Override
		public EventItem changed(CallbackQuery callbackQuery, CampingUser user, long optionId) {
			String display = longDescriptions[(int) optionId];
			String voteDisplayToUser = "You voted: " + display + "!";

			EventItem result = showBanner(callbackQuery, voteDisplayToUser, user);
			updateVoteTracker(user);
			return result;
		}

		@Override
		public EventItem confirmed(CallbackQuery callbackQuery, CampingUser user, long optionId) {
			String display = longDescriptions[(int) optionId];
			String voteDisplayToUser = "Your vote was already: " + display + "!";

			return showBanner(callbackQuery, voteDisplayToUser, user);
		}

		@Override
		public EventItem completedByUser(CallbackQuery callbackQuery, CampingUser user, long optionId) {
			return showBanner(callbackQuery, "You completed the voting!", user);
		}
	}

	protected InlineKeyboardMarkup getKeyboard() {
		return AbstractCommand.createKeyboard(shortButtons, buttonCallbackIds);
	}

	public void update() {
		EventLogger logger = EventLogger.getInstance();

		EditTextCommandResult editCmd = new EditTextCommandResult(VotingCommand.VoteCommand, bannerMessage,
				getBannerString());

		if (!completed)
			editCmd.setKeyboard(getKeyboard());

		try {
			SendResult result = editCmd.send(bot, chatId);
			logger.add(new EventItem(VotingCommand.VoteCommand, null, chat, bannerMessage.getMessageId(), result.msg));
		} catch (TelegramApiException e) {
			// HACK commented out, because this fails if same text... the time thing.

//			logger.add(new EventItem(VotingCommand.VoteCommand, null, null, chat, bannerMessage.getMessageId(), //
//					"Failed to update banner", voteTrackingMessage));
		}
	}

	public void updateBannerFinished() {
		EventLogger logger = EventLogger.getInstance();
		try {
			EditTextCommandResult edit = new EditTextCommandResult(VotingCommand.VoteTopicCompleteCommand,
					bannerMessage, VOTING_COMPLETED);
			SendResult result = edit.send(bot, chatId);
			logger.add(new EventItem(VotingCommand.VoteTopicCompleteCommand, null, chat,
					voteTrackingMessage.getMessageId(), result.msg));
		} catch (TelegramApiException e2) {
			logger.add(new EventItem(VotingCommand.VoteTopicCompleteCommand, null, null, chat,
					bannerMessage.getMessageId(), "Failed to update banner on Complete", voteTrackingMessage));
		}
	}

	protected void updateVoteTracker(CampingUser user) {
		EventLogger logger = EventLogger.getInstance();
		try {
			List<ResultFragment> votesText = getVotesText(completed);
			EditTextCommandResult editCmd = new EditTextCommandResult(VotingCommand.VoteCommand, voteTrackingMessage,
					votesText);
			SendResult result = editCmd.send(bot, chatId);
			logger.add(new EventItem(VotingCommand.VoteCommand, user, chat, voteTrackingMessage.getMessageId(),
					result.msg));
		} catch (Exception e) {
			logger.add(new EventItem(VotingCommand.VoteCommandFailedCommand, user, null, chat,
					bannerMessage.getMessageId(), "Failed to update tracker", voteTrackingMessage));
		}
	}

	public CommandResult createCompletionResult() {
		List<ResultFragment> votes = getVotesText(true);
		return new TextCommandResult(VotingCommand.VoteTopicCompleteCommand, votes);
	}

	public void sendFinishedVotingMessage() {
		Integer messageId = topicMessage.getMessageId();
		EventLogger logger = EventLogger.getInstance();

		try {
			CommandResult completionMsg = createCompletionResult();
			completionMsg.setReplyTo(messageId);
			SendResult result = completionMsg.sendInternal(bot, chatId);

			logger.add(new EventItem(VotingCommand.VoteTopicCompleteCommand, ranter, chat, result));
		} catch (TelegramApiException e) {
			logger.add(new EventItem(e.getLocalizedMessage()));
		}
	}

	public void complete() {
		if (completed)
			return;

		completed = true;
		updateBannerFinished();
		UnpinUtil.unpinSpecific(chatId, bot, bannerMessage);

		sendFinishedVotingMessage();
	}

	public List<ResultFragment> getBannerString() {
		List<ResultFragment> sb = new ArrayList<>();
		String bannerTitle = getBannerTitle();
		sb.add(new TextFragment(bannerTitle));

		sb.add(new TextFragment(" ("));
		sb.add(new TextFragment(formatter.toPrettyString(completionTime)));
		sb.add(new TextFragment(" left)"));
		for (int i = 0; i < shortButtons.length && i < longDescriptions.length; i++) {
			String shorter = shortButtons[i];
			String longer = longDescriptions[i];

			sb.add(new TextFragment("\n"));
			sb.add(new TextFragment(shorter, TextStyle.Bold));
			sb.add(new TextFragment(": "));
			sb.add(new TextFragment(longer));
		}

		if (allowExtendComplete) {
			sb.add(new TextFragment("\nOriginal speaker, or activater may reply to this with /complete or /extend.",
					TextStyle.Italic));
		}
		return sb;
	}

	public abstract String getBannerTitle();

	protected List<ResultFragment> getVotesText(boolean completed) {
		List<ResultFragment> output = new ArrayList<>();

//		Map<Integer, T> valueMap = cluster.getValueMap(id);
		Map<CampingUser, Long> votes = cluster.getVotes();
		Set<CampingUser> votesNotApplicable = cluster.getVotesNotApplicable();

		int notApplicable = votesNotApplicable.size();
		int naturalVotes = votes.size();
		int count = naturalVotes + notApplicable;
		float score = getScore();
		String scoreStr;
		if (naturalVotes > 0) {
			scoreStr = nf.format(score);
			String scoreSuffix = getScoreSuffix();
			if (scoreSuffix != null && scoreSuffix.length() > 0)
				scoreStr = scoreStr + scoreSuffix;
		} else {
			scoreStr = "(n/a)";
		}

		addVotesTextPrefix(completed, output);

		if (shouldShowVotesInCategories()) {
			int[] votesByCategories = new int[shortButtons.length];
			for (long vote : votes.values()) {
				votesByCategories[(int) vote]++;
			}
			if (addNa) {
				votesByCategories[naIndex] = votesNotApplicable.size();
			}

			for (int i = 0; i < shortButtons.length; i++) {
				String txt = shortButtons[i];
				output.add(new TextFragment("\n"));
				output.add(new TextFragment(txt, TextStyle.Bold));
				output.add(new TextFragment(": "));
				output.add(new TextFragment(Integer.toString(votesByCategories[i])));
			}
		}

		output.add(new TextFragment("\n--------\nTotal Votes: "));
		output.add(new TextFragment(Integer.toString(count), TextStyle.Bold));

		output.add(new TextFragment("\n"));
		if (completed)
			output.add(new TextFragment("Final"));
		else
			output.add(new TextFragment("Current"));
		output.add(new TextFragment(" Score: "));
		output.add(new TextFragment(scoreStr, TextStyle.Bold));
		addVotesTextSuffix(output, completed, score);

		return output;
	}

	protected abstract boolean shouldShowVotesInCategories();

	public void addVotesTextPrefix(boolean completed, List<ResultFragment> sb) {
		if (completed)
			sb.add(VOTING_COMPLETED);
	}

	public void addVotesTextSuffix(List<ResultFragment> sb, boolean completed, float score) {
	}

	protected abstract String getScoreSuffix();

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

	public void extend() {
		completionTime += 10 * 60 * 1000;
	}

	public long getCompletionTime() {
		return completionTime;
	}

	public boolean isCompleted() {
		return completed;
	}

	public TextCommandResult getBannerText() {
		return bannerText;
	}

	public boolean addListener(VoteChangedListener<T> e) {
		return voteListeners.add(e);
	}

	protected float averageVoteValues(Map<Long, ? extends Number> map) {
		Map<CampingUser, Long> votes = cluster.getVotes();
		Set<CampingUser> votesNotApplicable = cluster.getVotesNotApplicable();
		int count = votes.size() + votesNotApplicable.size();
		if (count == 0)
			return 0;

		float sum = 0;
		for (Long v : votes.values()) {
			Number pts = map.get(v);
			sum += pts.floatValue();
		}
		float avg = sum / count;
		return avg;
	}

	public String getKey() {
		int messageId = topicMessage.getMessageId();
		return Integer.toString(messageId);
	}

	public VoteCluster<T> getCluster() {
		return cluster;
	}
}