package ca.hapke.campingbot.api;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.Video;
import org.telegram.telegrambots.meta.api.objects.games.Animation;
import org.telegram.telegrambots.meta.api.objects.inlinequery.ChosenInlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import ca.hapke.campingbot.BotChoicePriority;
import ca.hapke.campingbot.BotConstants;
import ca.hapke.campingbot.CampingSystem;
import ca.hapke.campingbot.callback.api.CallbackCommand;
import ca.hapke.campingbot.callback.api.CallbackId;
import ca.hapke.campingbot.category.HasCategories;
import ca.hapke.campingbot.channels.CampingChat;
import ca.hapke.campingbot.channels.CampingChatManager;
import ca.hapke.campingbot.channels.ChatAllowed;
import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.CommandType;
import ca.hapke.campingbot.commands.api.InlineCommand;
import ca.hapke.campingbot.commands.api.InputType;
import ca.hapke.campingbot.commands.api.SlashCommand;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.commands.api.TextCommand;
import ca.hapke.campingbot.log.EventItem;
import ca.hapke.campingbot.log.EventLogger;
import ca.hapke.campingbot.processors.DefaultMessageProcessor;
import ca.hapke.campingbot.processors.MessageProcessor;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.InsultGenerator;
import ca.hapke.campingbot.response.SendResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.fragments.TextFragment;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.users.CampingUserMonitor;
import ca.hapke.util.StringUtil;
import ca.odell.glazedlists.FilterList;

/**
 * @author Nathan Hapke
 */
public abstract class CampingBotEngine extends TelegramLongPollingBot {

	private boolean online = false;
	private User me;
	protected CampingUser meCamping;
	private Set<IStatus> statusMonitors = new HashSet<>();

	protected EventLogger eventLogger = EventLogger.getInstance();
	protected CampingChatManager chatManager = CampingChatManager.getInstance(this);
	protected CampingUserMonitor userMonitor = CampingUserMonitor.getInstance();
	protected CampingSystem system = CampingSystem.getInstance();

	private List<CallbackCommand> callbackCommands = new ArrayList<>();
	private Map<String, CallbackCommand> callbackMap = new HashMap<>();
	private List<TextCommand> textCommands = new ArrayList<>();
	private List<InlineCommand> inlineCommands = new ArrayList<>();
	private Map<String, InlineCommand> inlineMap = new HashMap<>();
	private Multimap<SlashCommandType, SlashCommand> slashCommands = ArrayListMultimap.create();
	private Map<String, SlashCommandType> slashCommandMap = new HashMap<>();

	protected MessageProcessor processor = new DefaultMessageProcessor();

	protected InsultGenerator insultGenerator = InsultGenerator.getInstance();
	protected ConfigSerializer serializer;
	protected List<HasCategories<String>> hasCategories = new ArrayList<>();

	private class ConnectionMonitor implements IStatus {

		@Override
		public void statusOffline() {
			online = false;
		}

		@Override
		public void statusOnline() {
			online = true;
		}

		@Override
		public void statusMeProvided(CampingUser me) {

		}

		@Override
		public void connectFailed(TelegramApiException e) {
			online = false;
		}

	}

	public CampingBotEngine() {
		statusMonitors.add(new ConnectionMonitor());

	}

	public final void init() {
		serializer.load();
		FilterList<CampingUser> admins = userMonitor.getAdminUsers();
		for (CampingUser admin : admins) {
			CampingChat chat = chatManager.get((long) admin.getTelegramId());
			ChatAllowed current = chat.getAllowed();
			switch (current) {
			case Allowed:
			case Disallowed:
				break;
			case New:
			case NewAnnounced:
				chat.setAllowed(ChatAllowed.Allowed);
				break;
			}
		}
		postConfigInit();
		if (system.isConnectOnStartup()) {
			new Thread("ConnectOnStartup") {
				@Override
				public void run() {
					connect();
				}
			}.start();
		}
	}

	protected abstract void postConfigInit();

	public void connect() {
		try {
			TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
			api.registerBot(this);
			for (IStatus status : statusMonitors) {
				status.statusOnline();
			}
		} catch (TelegramApiException e) {
			for (IStatus status : statusMonitors) {
				status.connectFailed(e);
			}
		}
	}

	@Override
	public void onUpdateReceived(Update update) {
		InputType inputType = null;
		Integer telegramId = null;
		String inputRest = "";
		CampingUser campingFromUser = null;
		Integer eventTime = null;

		if (me == null) {
			try {
				me = getMe();
				meCamping = userMonitor.monitor(me);
				for (IStatus status : statusMonitors) {
					status.statusMeProvided(meCamping);
				}
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		}

		if (update.hasCallbackQuery()) {
			CallbackQuery callbackQuery = update.getCallbackQuery();
			EventItem outputEvent = null;

			String fullId = callbackQuery.getData();
			CallbackId id = CallbackId.fromString(fullId);
			CallbackCommand command = callbackMap.get(id.getCommand());
			if (command != null) {
				outputEvent = command.reactToCallback(id, callbackQuery);
				if (outputEvent != null) {
					eventLogger.add(outputEvent);
				}
			}
		}
		if (update.hasInlineQuery()) {
			InlineQuery inlineQuery = update.getInlineQuery();
			String input = inlineQuery.getQuery();
			if (input != null) {

				List<InlineQueryResult> results = new ArrayList<>();
				int updateId = update.getUpdateId();

				for (InlineCommand inline : inlineCommands) {
					List<InlineQueryResult> r = inline.provideInlineQuery(update, input, updateId, processor);
					if (r != null) {
						for (InlineQueryResult result : r) {
							if (result != null)
								results.add(result);
						}
					}
				}

				// send options to client
				if (results.size() > 0) {
					AnswerInlineQuery answer = new AnswerInlineQuery();
					answer.setInlineQueryId(inlineQuery.getId());
					answer.setResults(results);
					answer.setCacheTime(30);
					answer.setIsPersonal(true);

					try {
						execute(answer);
					} catch (TelegramApiException e) {
						// Too old. Don't care.
					}
				}
			}
		}
		if (update.hasChosenInlineQuery()) {
			// User chose an inline spell, @username conversion, or HideIt
			inputType = InputType.InlineChatUpdate;
			ChosenInlineQuery inlineChosen = update.getChosenInlineQuery();
			String fullId = inlineChosen.getResultId();
			CallbackId id = CallbackId.fromString(fullId);

			campingFromUser = userMonitor.getUser(inlineChosen.getFrom());
			if (id != null) {
				InlineCommand command = inlineMap.get(id.getCommand());
				if (command != null) {
					inputRest = inlineChosen.getQuery();

					EventItem outputEvent = command.chosenInlineQuery(update, id, campingFromUser, inputRest);

					if (outputEvent != null) {
						eventLogger.add(outputEvent);
					}
				}
			}
		}

		// FIGURE OUT WHAT THE INPUT IS

		Message message;
		List<MessageEntity> entities = null;
		Long chatId = null;
		CampingChat chat = null;
		if (update.hasEditedMessage()) {
			message = update.getEditedMessage();
		} else {
			message = update.getMessage();
		}
		if (message != null) {
			telegramId = message.getMessageId();
			entities = message.getEntities();

			campingFromUser = userMonitor.monitor(message.getFrom(), entities);
			chatId = message.getChatId();
			chat = chatManager.get(chatId);
		}

		Object inputExtraData = null;
		if (update.hasEditedMessage()) {
			inputType = InputType.RegularChatEdit;
			inputRest = message.getText();
			eventTime = message.getEditDate();
		} else if (message != null) {
			eventTime = message.getDate();

			if (message.hasAnimation()) {
				// responds to GIFs
				inputType = InputType.RegularChatGif;
				Animation animation = message.getAnimation();
				inputRest = animation.getFileId();
				inputExtraData = animation.getDuration();
			} else if (message.hasPhoto()) {
				inputType = InputType.RegularChatPhoto;
				try {
					List<PhotoSize> photos = message.getPhoto();
					Map<String, PhotoSize> photosLargest = new HashMap<>(photos.size());

					for (PhotoSize photo : photos) {
						String key = photo.getFileId();
						PhotoSize currentLargest = photosLargest.get(key);
						if (currentLargest == null || photo.getHeight() > currentLargest.getHeight())
							photosLargest.put(key, photo);
					}
					for (PhotoSize photo : photosLargest.values()) {
						if (inputRest.length() > 0)
							inputRest = inputRest + "\n";
						inputRest = inputRest + photo.toString();
					}
					inputExtraData = photosLargest.size();
				} catch (Exception e) {
				}
			} else if (message.hasVideo()) {
				inputType = InputType.RegularChatVideo;
				Video video = message.getVideo();
				inputRest = video.toString();
				inputExtraData = video.getDuration();
			} else if (message.hasSticker()) {
				inputType = InputType.RegularChatSticker;
				Sticker stick = message.getSticker();
				inputRest = stick.getEmoji();
				inputExtraData = stick.getSetName();
			} else if (message.getNewChatTitle() != null) {
				// title changed
				inputType = InputType.ChatUpdate;
				chatManager.updateChat(chatId, chat);
			} else {
				/*- figure out what type of message it is...
				 *  ie: regular message/edit/delete
				 */
				inputRest = message.getText();
				Message replyTo = message.getReplyToMessage();
				if (replyTo != null) {
					inputType = InputType.RegularChatReply;
					inputExtraData = replyTo.getMessageId();
				} else {
					inputType = InputType.RegularChatUpdate;
				}
			}
		}
		if (inputType != null) {
			EventItem inputEvent = null;
			inputEvent = new EventItem(inputType, campingFromUser, eventTime, chat, telegramId, inputRest,
					inputExtraData);
			eventLogger.add(inputEvent);
		}

		// PROCESS FOR OUTPUT

		if (message != null) {
			if (update.hasMessage() && message.hasText()) {
				String originalMsg = message.getText();
				SlashCommandType outputCommand = null;
				CommandResult outputResult = null;

				outputCommand = findCommand(update, me);

				try {
					switch (chat.getAllowed()) {
					case Disallowed:
					case NewAnnounced:
						break;
					case New:
						outputResult = new TextCommandResult(CampingChatManager.JoinThreadCommand, new TextFragment(
								"An administrator must approve this channel before commands are enabled."));
						chat.setAllowed(ChatAllowed.NewAnnounced);
						break;
					case Allowed:
						if (outputCommand != null) {
							outputResult = reactToSlashCommandInText(outputCommand, message, chatId, campingFromUser);

						} else {
							String msg = originalMsg.toLowerCase().trim();

							// react to non /commands that occur in regular text
//						CommandResult result = null;
							for (TextCommand textCommand : textCommands) {
								if (textCommand.isMatch(msg, message)) {
									outputResult = textCommand.textCommand(campingFromUser, entities, chatId, message);
									if (outputResult != null) {
										break;
									}
								}
							}
						}

						break;

					}
					if (outputResult != null) {
						SendResult sendResult = outputResult.send(this, chatId);
						logSendResult(sendResult.outgoingMsg.getMessageId(), campingFromUser, eventTime, chat,
								outputCommand, outputResult, sendResult);
					}

				} catch (TelegramApiException e) {
					logFailure(telegramId, campingFromUser, eventTime, chat, outputCommand, e);
				}
			}

		}

	}

	public SlashCommandType findCommand(Update update, User me) {
		Message message = update.getMessage();
		List<MessageEntity> entities = message.getEntities();
		if (entities != null) {
			for (MessageEntity msgEnt : entities) {
				String type = msgEnt.getType();
				if (BotConstants.BOT_COMMAND.equalsIgnoreCase(type) && msgEnt.getOffset() == 0) {
					boolean targetsMe;
					String msg = msgEnt.getText();
					String command = msg;
					int start = msg.indexOf('/');
					int at = msg.indexOf('@');
					int length = msg.length();
					if (at > 0) {
						command = msg.substring(start + 1, at);
						String target = msg.substring(at + 1, length);
						targetsMe = StringUtil.matchOne(target, me.getFirstName(), me.getUserName(), me.getLastName());
					} else {
						command = msg.substring(start + 1);
						targetsMe = true;
					}

					if (targetsMe) {
						return slashCommandMap.get(command);
					}
				}
			}
		}

		return null;
	}

	public void logFailure(Integer telegramId, CampingUser campingFromUser, Long chatId, CommandType outputCommand,
			TelegramApiException e) {
		CampingChat chat = chatManager.get(chatId);
		logFailure(telegramId, campingFromUser, null, chat, outputCommand, e);
	}

	public void logFailure(Integer telegramId, CampingUser campingFromUser, Integer eventTime, Long chatId,
			CommandType outputCommand, TelegramApiException e) {
		CampingChat chat = chatManager.get(chatId);
		logFailure(telegramId, campingFromUser, eventTime, chat, outputCommand, e);
	}

	public void logFailure(Integer telegramId, CampingUser campingFromUser, Integer eventTime, CampingChat chat,
			CommandType outputCommand, TelegramApiException e) {
		if (eventTime == null)
			eventTime = getNow();
		EventItem outputEvent = null;
		outputEvent = new EventItem(outputCommand, campingFromUser, eventTime, chat, telegramId,
				"Exception: " + e.getLocalizedMessage(), null);
		eventLogger.add(outputEvent);
		System.err.println(e.toString());
		e.printStackTrace();
	}

	public void logSendResult(Integer telegramId, CampingUser campingFromUser, Long chatId, CommandType outputCommand,
			CommandResult outputResult, SendResult sendResult) {
		CampingChat chat = chatManager.get(chatId);
		logSendResult(telegramId, campingFromUser, null, chat, outputCommand, outputResult, sendResult);
	}

	public void logSendResult(Integer telegramId, CampingUser campingFromUser, Integer eventTime, Long chatId,
			CommandType outputCommand, CommandResult outputResult, SendResult sendResult) {
		CampingChat chat = chatManager.get(chatId);
		logSendResult(telegramId, campingFromUser, eventTime, chat, outputCommand, outputResult, sendResult);
	}

	public void logSendResult(Integer telegramId, CampingUser campingFromUser, Integer eventTime, CampingChat chat,
			CommandType outputCommand, CommandResult outputResult, SendResult sendResult) {
		if (eventTime == null)
			eventTime = getNow();
		// command may change to a Rejected
		CommandType cmd = outputResult.getCmd();
		CommandType resultCommand = cmd != null ? cmd : outputCommand;
		EventItem outputEvent = null;
		outputEvent = new EventItem(resultCommand, campingFromUser, eventTime, chat, telegramId, sendResult.msg,
				sendResult.extraData);
		eventLogger.add(outputEvent);
	}

	private static int getNow() {
		return (int) (ZonedDateTime.now().toInstant().toEpochMilli() / 1000);
	}

	protected CommandResult reactToSlashCommandInText(SlashCommandType command, Message message, Long chatId,
			CampingUser campingFromUser) throws TelegramApiException {
		for (SlashCommand sc : slashCommands.get(command)) {
			if (!system.hasAccess(campingFromUser, sc)) {
				return new TextCommandResult(command).add(campingFromUser).add(": Access denied, you ")
						.add(insultGenerator.getInsult());
			}
			CommandResult result = sc.respondToSlashCommand(command, message, chatId, campingFromUser);
			if (result != null)
				return result;
		}
		return null;

	}

	public CampingUser findTarget(Message message, boolean replyFirst, boolean frontToBack,
			BotChoicePriority priority) {
		CampingUser currentChoice = null;
		if (replyFirst) {
			Message replyTo = message.getReplyToMessage();
			if (replyTo != null) {
				currentChoice = CampingUserMonitor.getInstance().getUser(replyTo.getFrom());
			}
		}

		List<MessageEntity> entities = message.getEntities();
		int currentOffset;
		if (frontToBack)
			currentOffset = Integer.MIN_VALUE;
		else
			currentOffset = Integer.MAX_VALUE;

		if (entities != null) {
			for (MessageEntity msgEnt : entities) {
				String type = msgEnt.getType();
				int offset = msgEnt.getOffset();

				CampingUser possibleChoice = null;
				if (BotConstants.MENTION.equalsIgnoreCase(type)) {
					// usernamed victim: the text is their @username
					possibleChoice = userMonitor.monitor(msgEnt);
				} else if (BotConstants.TEXT_MENTION.equalsIgnoreCase(type)) {
					// non-usernamed victim: we get the User struct
					possibleChoice = userMonitor.getUser(msgEnt.getUser());
				} else {
					// other Entity types should be ignored
					continue;
				}

				if (isBetterSelection(frontToBack, offset, currentOffset, currentChoice, possibleChoice, priority)) {
					if (BotConstants.MENTION.equalsIgnoreCase(type)) {
						currentChoice = possibleChoice;
						currentOffset = offset;
					} else if (BotConstants.TEXT_MENTION.equalsIgnoreCase(type)) {
						currentChoice = possibleChoice;
						currentOffset = offset;
					}
				}
			}
		}

		if (currentChoice == null) {
			Message replyTo = message.getReplyToMessage();
			if (replyTo != null) {
				currentChoice = CampingUserMonitor.getInstance().getUser(replyTo.getFrom());
			}
		}
		return currentChoice;
	}

	private boolean isBetterSelection(boolean frontToBack, int offset, int currentOffset, CampingUser currentChoice,
			CampingUser possibleChoice, BotChoicePriority priority) {
		int possibleId = possibleChoice.getTelegramId();
		int myId = meCamping.getTelegramId();
		if (possibleId == myId && priority == BotChoicePriority.Never)
			return false;
		if (possibleId != myId && priority == BotChoicePriority.Only)
			return false;
		if (currentChoice == null)
			return true;
		if (priority == BotChoicePriority.First) {
			if (currentChoice == meCamping)
				return false;
			if (possibleChoice == meCamping)
				return true;
		}
		if (possibleId == myId && priority == BotChoicePriority.Last)
			return false;

		if (frontToBack)
			return offset < currentOffset;
		else
			return offset > currentOffset;
	}

	public void addStatusUpdate(IStatus status) {
		this.statusMonitors.add(status);
	}

	public void addCommand(AbstractCommand command) {
		if (command instanceof InlineCommand) {
			InlineCommand ic = (InlineCommand) command;
			addInlineCommand(ic);
		}
		if (command instanceof TextCommand) {
			TextCommand tc = (TextCommand) command;
			addTextCommand(tc);
		}
		if (command instanceof CallbackCommand) {
			CallbackCommand cc = (CallbackCommand) command;
			addCallbackCommand(cc);
		}
		if (command instanceof SlashCommand) {
			SlashCommand sc = (SlashCommand) command;
			SlashCommandType[] cmds = sc.getSlashCommandsToRespondTo();
			if (cmds != null) {
				for (SlashCommandType key : cmds) {
					slashCommands.put(key, sc);
					slashCommandMap.put(key.slashCommand, key);
				}
			}
		}
	}

	private final void addInlineCommand(InlineCommand ic) {
		inlineCommands.add(ic);
		inlineMap.put(ic.getCommandName(), ic);
	}

	private final void addTextCommand(TextCommand tc) {
		textCommands.add(tc);
	}

	private final void addCallbackCommand(CallbackCommand cc) {
		callbackCommands.add(cc);
		callbackMap.put(cc.getCommandName(), cc);
	}

	public boolean isOnline() {
		return online;
	}

	public MessageProcessor getProcessor() {
		return processor;
	}

	@Override
	public String getBotToken() {
		return system.getToken();
	}

	@Override
	public String getBotUsername() {
		return system.getBotUsername();
	}

	public List<HasCategories<String>> getCategories() {
		return hasCategories;
	}

	public CampingUser getMeCamping() {
		return meCamping;
	}
}