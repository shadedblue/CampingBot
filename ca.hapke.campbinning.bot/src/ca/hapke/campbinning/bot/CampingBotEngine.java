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
import ca.hapke.campbinning.bot.commands.TextCommandResult;
import ca.hapke.campbinning.bot.commands.inline.InlineCommand;
import ca.hapke.campbinning.bot.log.EventItem;
import ca.hapke.campbinning.bot.log.EventLogger;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.users.CampingUserMonitor;

/**
 * @author Nathan Hapke
 */
public abstract class CampingBotEngine extends TelegramLongPollingBot {

	public static final String MARKDOWN = "Markdown";
	public static final String TEXT_MENTION = "text_mention";
	public static final String MENTION = "mention";

	private User me;
	protected CampingUser meCamping;

	protected EventLogger eventLogger = EventLogger.getInstance();
	protected CampingChatManager chatManager = CampingChatManager.getInstance();
	protected CampingUserMonitor userMonitor = CampingUserMonitor.getInstance();
	protected CampingSystem system = CampingSystem.getInstance();

	protected List<CallbackCommand> callbackCommands = new ArrayList<>();
	protected List<TextCommand> textCommands = new ArrayList<>();
	protected List<InlineCommand> inlineCommands = new ArrayList<>();

	@Override
	public void onUpdateReceived(Update update) {
		EventItem event = null;
		Integer eventTime = null;

		// res.getCampbinningStickerPack(this);
		if (me == null) {
			try {
				me = getMe();
				meCamping = userMonitor.monitor(me);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		}

		if (update.hasCallbackQuery()) {
			CallbackQuery callbackQuery = update.getCallbackQuery();
			for (CallbackCommand callback : callbackCommands) {
				event = callback.reactToCallback(callbackQuery);

			}
		}
		if (update.hasInlineQuery()) {

			InlineQuery inlineQuery = update.getInlineQuery();
			if (inlineQuery.hasQuery()) {

				List<InlineQueryResult> results = new ArrayList<>();
				int updateId = update.getUpdateId();

				String input = inlineQuery.getQuery();

				for (InlineCommand inline : inlineCommands) {
					InlineQueryResult r = inline.provideInlineQuery(input, updateId);
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

			ChosenInlineQuery inlineChosen = update.getChosenInlineQuery();
			String[] words = inlineChosen.getResultId().split(InlineCommand.INLINE_DELIMITER);
			Integer inlineMessageId = Integer.parseInt(words[1]);

			User from = inlineChosen.getFrom();
			CampingUser campingFromUser = userMonitor.getUser(from);
			if (words.length >= 1) {

				for (InlineCommand inline : inlineCommands) {
					String commandName = inline.getCommandName();
					if (commandName.equalsIgnoreCase(words[0])) {
						event = inline.chosenInlineQuery(words, campingFromUser, inlineMessageId);
					}
				}

			}
		}

		Message message = update.getMessage();
		Integer telegramId;
		if (update.hasEditedMessage()) {
			message = update.getEditedMessage();
			telegramId = message.getMessageId();
			List<MessageEntity> entities = message.getEntities();

			CampingUser campingFromUser = userMonitor.monitor(message.getFrom(), entities);

			Long chatId = message.getChatId();
			CampingChat chat = chatManager.get(chatId, this);

			String afterText = message.getText();
			eventTime = message.getEditDate();

			event = new EventItem(BotCommand.RegularChatEdit, campingFromUser, eventTime, chat, telegramId, afterText,
					null);
		}

		if (message != null) {
			User fromUser = message.getFrom();
			List<MessageEntity> entities = message.getEntities();

			CampingUser campingFromUser = userMonitor.monitor(fromUser, entities);
			CampingChat chat = null;
			telegramId = message.getMessageId();
			Long chatId = message.getChatId();
			if (chatId != null)
				chat = chatManager.get(chatId, this);

			BotCommand command = null;
			String rest = "";
			Object extraData = null;
			eventTime = message.getDate();

			if (message.hasAnimation()) {
				// responds to GIFs
				command = BotCommand.RegularChatGif;
				Animation animation = message.getAnimation();
				rest = animation.getFileId();
				extraData = animation.getDuration();
			} else if (message.hasPhoto()) {
				command = BotCommand.RegularChatPhoto;
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
						if (rest.length() > 0)
							rest = rest + "\n";
						rest = rest + photo.toString();
					}
					extraData = photosLargest.size();
				} catch (Exception e) {
				}
			} else if (message.hasVideo()) {
				command = BotCommand.RegularChatVideo;
				Video video = message.getVideo();
				rest = video.toString();
				extraData = video.getDuration();
			} else if (message.hasSticker()) {
				command = BotCommand.RegularChatSticker;
				Sticker stick = message.getSticker();
				rest = stick.getEmoji();
				extraData = stick.getSetName();
			}

			if (update.hasMessage() && message.hasText()) {
				String originalMsg = message.getText();

				if (command == null) {
					command = BotCommand.findCommand(update, me);
				}

				try {
					if (command != null) {
						TextCommandResult result = reactToSlashCommandInText(command, message, chatId, campingFromUser);
						if (result != null) {
							rest = result.msg;
							command = result.cmd;
						}
					} else {
						String msg = originalMsg.toLowerCase().trim();

						// react to non /commands that occur in regular text
						TextCommandResult result = null;
						for (TextCommand textCommand : textCommands) {
							if (textCommand.isMatch(msg, entities)) {
								result = textCommand.textCommand(campingFromUser, entities, chatId);
								if (result != null)
									break;
							}
						}
						if (result != null) {
							rest = result.msg;
							if (result.shouldSendMsg)
								sendMsg(chatId, rest);
							command = result.cmd;
						}

						// still not doing anything, just log it.
						if (command == null) {
							/*- figure out what type of message it is...
							 *  ie: regular message/edit/delete
							 */
							Message replyTo = message.getReplyToMessage();
							if (replyTo != null) {
								command = BotCommand.RegularChatReply;
								extraData = replyTo.getMessageId();
							} else {
								command = BotCommand.RegularChatUpdate;
							}
							rest = originalMsg;
						}
					}
				} catch (TelegramApiException e) {
					event = new EventItem(command, campingFromUser, eventTime, chat, telegramId,
							"Exception: " + e.getMessage(), null);

				}
			}
			if (event == null && command != null)
				event = new EventItem(command, campingFromUser, eventTime, chat, telegramId, rest, extraData);
		}
		if (event != null)
			eventLogger.add(event);

	}

	protected abstract TextCommandResult reactToSlashCommandInText(BotCommand command, Message message, Long chatId,
			CampingUser campingFromUser) throws TelegramApiException;

	public void sendMsg(Long chatId, CampingUser target, String msg) {
		if (target != null)
			msg = target.target() + ": " + msg;
		sendMsg(chatId, msg);
	}

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
}