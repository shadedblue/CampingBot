package ca.hapke.campbinning.bot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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

import ca.hapke.campbinning.bot.channels.CampingChat;
import ca.hapke.campbinning.bot.channels.CampingChatManager;
import ca.hapke.campbinning.bot.commands.CallbackCommand;
import ca.hapke.campbinning.bot.commands.TextCommand;
import ca.hapke.campbinning.bot.commands.inline.InlineCommand;
import ca.hapke.campbinning.bot.commands.response.CommandResult;
import ca.hapke.campbinning.bot.commands.response.DefaultMessageProcessor;
import ca.hapke.campbinning.bot.commands.response.MessageProcessor;
import ca.hapke.campbinning.bot.commands.response.SendResult;
import ca.hapke.campbinning.bot.log.EventItem;
import ca.hapke.campbinning.bot.log.EventLogger;
import ca.hapke.campbinning.bot.ui.IStatus;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.users.CampingUserMonitor;

/**
 * @author Nathan Hapke
 */
public abstract class CampingBotEngine extends TelegramLongPollingBot {

	public static final String MARKDOWN = "Markdown";
	public static final String TEXT_MENTION = "text_mention";
	public static final String MENTION = "mention";

	private boolean online = false;
	private User me;
	protected CampingUser meCamping;
	private List<IStatus> statusMonitors = new ArrayList<>();

	protected EventLogger eventLogger = EventLogger.getInstance();
	protected CampingChatManager chatManager = CampingChatManager.getInstance();
	protected CampingUserMonitor userMonitor = CampingUserMonitor.getInstance();
	protected CampingSystem system = CampingSystem.getInstance();

	protected List<CallbackCommand> callbackCommands = new ArrayList<>();
	protected List<TextCommand> textCommands = new ArrayList<>();
	protected List<InlineCommand> inlineCommands = new ArrayList<>();

	protected MessageProcessor processor = new DefaultMessageProcessor();

	private class ConnectionMonitor implements IStatus {

		@Override
		public void statusOffline(String username) {
			online = false;
		}

		@Override
		public void statusOnline(CampingUser meCamping) {
			online = true;
		}

	}

	public CampingBotEngine() {
		statusMonitors.add(new ConnectionMonitor());
	}

	@Override
	public void onUpdateReceived(Update update) {
		InputType inputType = null;
		EventItem inputEvent = null;
		Integer telegramId = null;
		String inputRest = "";
		CampingUser campingFromUser = null;
		EventItem outputEvent = null;
		Integer eventTime = null;

		// res.getCampbinningStickerPack(this);
		if (me == null) {
			try {
				me = getMe();
				meCamping = userMonitor.monitor(me);
				for (IStatus status : statusMonitors) {
					status.statusOnline(meCamping);
				}
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		}

		if (update.hasCallbackQuery()) {
			CallbackQuery callbackQuery = update.getCallbackQuery();
			for (CallbackCommand callback : callbackCommands) {
				outputEvent = callback.reactToCallback(callbackQuery);
				if (outputEvent != null)
					break;
			}
		}
		if (update.hasInlineQuery()) {
			InlineQuery inlineQuery = update.getInlineQuery();
			if (inlineQuery.hasQuery()) {

				List<InlineQueryResult> results = new ArrayList<>();
				int updateId = update.getUpdateId();

				String input = inlineQuery.getQuery();

				for (InlineCommand inline : inlineCommands) {
					InlineQueryResult r = inline.provideInlineQuery(input, updateId, processor);
					if (r != null)
						results.add(r);
				}

				// send options to client
				if (results.size() > 0) {
					AnswerInlineQuery answer = new AnswerInlineQuery();
					answer.setInlineQueryId(inlineQuery.getId());
					answer.setResults(results);

					try {
						execute(answer);
					} catch (TelegramApiException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		if (update.hasChosenInlineQuery()) {
			// User chose an inline spell or @username conversion
			inputType = InputType.InlineChatUpdate;
			ChosenInlineQuery inlineChosen = update.getChosenInlineQuery();
			String fullInput = inlineChosen.getResultId();
			String[] words = fullInput.split(InlineCommand.INLINE_DELIMITER);
			telegramId = Integer.parseInt(words[1]);

			campingFromUser = userMonitor.getUser(inlineChosen.getFrom());
			if (words.length >= 1) {

				inputRest = inlineChosen.getQuery();

				for (InlineCommand inline : inlineCommands) {
					String commandName = inline.getCommandName();
					if (commandName.equalsIgnoreCase(words[0])) {
						outputEvent = inline.chosenInlineQuery(words, campingFromUser, telegramId, inputRest);

						if (outputEvent != null)
							break;
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
			chat = chatManager.get(chatId, this);
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
			inputEvent = new EventItem(inputType, campingFromUser, eventTime, chat, telegramId, inputRest,
					inputExtraData);
		}
		eventLogger.add(inputEvent);

		// PROCESS FOR OUTPUT

		if (message != null) {
			if (update.hasMessage() && message.hasText()) {
				String originalMsg = message.getText();
				BotCommand outputCommand = null;
				CommandResult outputResult = null;

				outputCommand = BotCommand.findCommand(update, me);

				try {
					if (outputCommand != null) {
						outputResult = reactToSlashCommandInText(outputCommand, message, chatId, campingFromUser);

					} else {
						String msg = originalMsg.toLowerCase().trim();

						// react to non /commands that occur in regular text
//						CommandResult result = null;
						for (TextCommand textCommand : textCommands) {
							if (textCommand.isMatch(msg, entities)) {
								outputResult = textCommand.textCommand(campingFromUser, entities, chatId, message);
								if (outputResult != null) {
									outputCommand = outputResult.getCmd();
									break;
								}
							}
						}
					}

					if (outputResult != null) {
						SendResult sendResult = outputResult.send(this, chatId, processor);
						outputEvent = new EventItem(outputCommand, campingFromUser, eventTime, chat, telegramId,
								sendResult.msg, sendResult.extraData);
					}
				} catch (TelegramApiException e) {
					outputEvent = new EventItem(outputCommand, campingFromUser, eventTime, chat, telegramId,
							"Exception: " + e.getMessage(), null);

				}
			}

		}
		if (outputEvent != null)
			eventLogger.add(outputEvent);

	}

	protected abstract CommandResult reactToSlashCommandInText(BotCommand command, Message message, Long chatId,
			CampingUser campingFromUser) throws TelegramApiException;

	public Message sendMsg(Long chatId, String msg) {
		return sendMsg(chatId, (Message) null, msg);
	}

	public Message sendMsg(Long chatId, Message replyTo, String msg) {
		SendMessage send = new SendMessage(chatId, msg);
		if (replyTo != null)
			send.setReplyToMessageId(replyTo.getMessageId());
		send.setParseMode(MARKDOWN);
		try {
			return execute(send);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
		return null;
	}

	public CampingUser findTarget(List<MessageEntity> entities) {
		CampingUser targetUser = null;
		int minOffset = -1;
		if (entities == null)
			return null;
		for (MessageEntity msgEnt : entities) {
			String type = msgEnt.getType();
			int offset = msgEnt.getOffset();
			if (minOffset == -1 || offset < minOffset) {
				if (MENTION.equalsIgnoreCase(type)) {
					// usernamed victim: the text is their
					// @username
					targetUser = userMonitor.monitor(msgEnt);
				} else if (TEXT_MENTION.equalsIgnoreCase(type)) {
					// non-usernamed victim: we get the User
					// struct
					targetUser = userMonitor.getUser(msgEnt.getUser());
				} else {
					continue;
				}
			}
		}
		return targetUser;
	}

	public void addStatusUpdate(IStatus status) {
		this.statusMonitors.add(status);
	}

	public boolean isOnline() {
		return online;
	}
}