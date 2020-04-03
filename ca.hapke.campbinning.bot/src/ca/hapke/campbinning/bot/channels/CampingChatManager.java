package ca.hapke.campbinning.bot.channels;

import java.util.HashMap;
import java.util.Map;

import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campbinning.bot.CampingBotEngine;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

/**
 * @author Nathan Hapke
 */
public class CampingChatManager {

	public static final String UNKNOWN = "unknown";
	private static CampingChatManager instance = new CampingChatManager();

	public static CampingChatManager getInstance() {
		return instance;
	}

	private CampingChatManager() {
	}

	private final Map<Long, CampingChat> chats = new HashMap<>();
	private final EventList<CampingChat> chatEvents = GlazedLists.threadSafeList(new BasicEventList<CampingChat>());

	public CampingChat get(Long chatId, CampingBotEngine bot) {
		CampingChat chat = chats.get(chatId);
		if (chat == null) {
			String chatname = UNKNOWN;
			chatname = findChatName(chatId, bot);
			chat = new CampingChat(chatId, chatname);
			chatEvents.add(chat);
			chats.put(chatId, chat);
		} else if (chat.getChatname().equalsIgnoreCase(UNKNOWN)) {
			chat.setChatname(findChatName(chatId, bot));
		}
		return chat;
	}

	public String findChatName(Long chatId, CampingBotEngine bot) {
		Chat tChat;
		String chatname = UNKNOWN;
		try {
			tChat = bot.execute(new GetChat(chatId));
			if (tChat.isGroupChat() || tChat.isSuperGroupChat())
				chatname = tChat.getTitle();
			else
				chatname = tChat.getFirstName();

		} catch (TelegramApiException e1) {
		}
		return chatname;
	}

	public EventList<CampingChat> getChatList() {
		return chatEvents;
	}
}
