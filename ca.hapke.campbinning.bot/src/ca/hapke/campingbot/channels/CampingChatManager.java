package ca.hapke.campingbot.channels;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.api.CampingBotEngine;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.ResponseCommandType;
import ca.hapke.campingbot.log.DatabaseConsumer;
import ca.hapke.campingbot.log.EventItem;
import ca.hapke.campingbot.log.EventLogger;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.SendResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.users.CampingUserMonitor;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import ca.odell.glazedlists.matchers.Matcher;

/**
 * @author Nathan Hapke
 */
public class CampingChatManager {
	public static final ResponseCommandType JoinThreadCommand = new ResponseCommandType("JoinThread",
			BotCommandIds.THREAD | BotCommandIds.SET);
	private static CampingChatManager instance;
	private CampingBotEngine bot;

	public static CampingChatManager getInstance(CampingBotEngine bot) {
		if (instance == null) {
			instance = new CampingChatManager(bot);
		}
		return instance;
	}

	public static CampingChatManager getInstance() {
		return instance;
	}

	private CampingChatManager(CampingBotEngine bot) {
		this.bot = bot;
	}

	private final Map<Long, CampingChat> chats = new HashMap<>();
	private EventList<CampingChat> baseList = new ObservableElementList<>(new BasicEventList<CampingChat>(),
			GlazedLists.beanConnector(CampingChat.class));
	private final EventList<CampingChat> chatEvents = GlazedLists.threadSafeList(GlazedLists.readOnlyList(baseList));
	private final EventList<CampingChat> announceChats = GlazedLists
			.readOnlyList(new FilterList<CampingChat>(baseList, new Matcher<CampingChat>() {
				@Override
				public boolean matches(CampingChat item) {
					return item.isAnnounce();
				}
			}));

	public void load() {
		List<CampingChat> incoming = DatabaseConsumer.getInstance().loadChats();
		for (CampingChat cc : incoming) {
			add(cc);
		}
	}

	public CampingChat get(Long chatId) {
		boolean shouldNotify = false;
		CampingChat chat = chats.get(chatId);
		if (chat == null) {
			chat = new CampingChat(chatId);
			baseList.add(chat);
			chats.put(chatId, chat);
			shouldNotify = true;
		}

		if (chat.shouldUpdateChatDetails() && bot != null && bot.isOnline()) {
			updateChat(chatId, chat);
		}
		if (shouldNotify)
			notifyNewChat(chat);
		return chat;
	}

	public void add(CampingChat chat) {
		baseList.add(chat);
		Long chatId = chat.getChatId();
		chats.put(chatId, chat);

		if (chat.shouldUpdateChatDetails() && bot != null && bot.isOnline()) {
			updateChat(chatId, chat);
		}
	}

	public void updateChat(Long chatId, CampingChat chat) {
		String chatname = null;
		try {
			Chat tChat = bot.execute(new GetChat(Long.toString(chatId)));

			if (tChat.isGroupChat() || tChat.isSuperGroupChat()) {
				chatname = tChat.getTitle();
				chat.setType(ChatType.Group);
			} else if (tChat.isUserChat()) {
				chatname = tChat.getFirstName();
				chat.setType(ChatType.SingleUser);
			}

			if (chatname != null) {
				chat.setChatname(chatname);
			}
		} catch (TelegramApiException e) {
		}
	}

	private void notifyNewChat(CampingChat chat) {
		FilterList<CampingUser> admins = CampingUserMonitor.getInstance().getAdminUsers();
		for (CampingUser user : admins) {
			Long chatId = (long) user.getTelegramId();

			CommandResult msg = new TextCommandResult(JoinThreadCommand)
					.add("Invited to: " + chat.getChatname() + " (" + chat.chatId + ")");

			EventItem ei;
			try {
				SendResult result = msg.send(bot, chatId);
				ei = new EventItem(null, user, chat, result);
			} catch (TelegramApiException e) {
				ei = new EventItem("Failed to notify admin: " + user.getNameForLog() + "(" + user.getTelegramId()
						+ ") about new chat: " + chatId);
			}
			EventLogger.getInstance().add(ei);
		}
	}

	public EventList<CampingChat> getChatList() {
		return chatEvents;
	}

	public EventList<CampingChat> getAnnounceChats() {
		return announceChats;
	}
}
