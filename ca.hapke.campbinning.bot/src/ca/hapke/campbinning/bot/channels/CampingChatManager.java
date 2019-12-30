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
			Chat tChat;
			String chatname = "unknown";
			try {
				tChat = bot.execute(new GetChat(chatId));
				if (tChat.isGroupChat() || tChat.isSuperGroupChat())
					chatname = tChat.getTitle();
				else
					chatname = tChat.getFirstName();

			} catch (TelegramApiException e1) {
			}
			chat = new CampingChat(chatId, chatname);
			chatEvents.add(chat);
			chats.put(chatId, chat);
		}
		return chat;
	}

	public EventList<CampingChat> getChatList() {
		return chatEvents;
	}
}
