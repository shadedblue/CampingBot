package ca.hapke.campbinning.bot.channels;

import java.util.HashMap;
import java.util.Map;

import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campbinning.bot.CampingBotEngine;
import ca.hapke.campbinning.bot.CampingSerializable;
import ca.hapke.campbinning.bot.xml.OutputFormatter;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;

/**
 * TODO add EventChangeListener to events, so that if a Chat gets added, we add it to the Map also.
 * 
 * @author Nathan Hapke
 */
public class CampingChatManager implements CampingSerializable {
	private boolean shouldSave = false;
	private static CampingChatManager instance;
	private CampingBotEngine bot;

	public static CampingChatManager getInstance(CampingBotEngine bot) {
		if (instance == null) {
			instance = new CampingChatManager(bot);
		}
		return instance;
	}

	private CampingChatManager(CampingBotEngine bot) {
		this.bot = bot;
	}

	private final Map<Long, CampingChat> chats = new HashMap<>();
	private final ObservableElementList.Connector<CampingChat> chatConnector = GlazedLists
			.beanConnector(CampingChat.class);
	private final EventList<CampingChat> chatEvents = GlazedLists
			.threadSafeList(new ObservableElementList<>(new BasicEventList<CampingChat>(), chatConnector));

	public CampingChat get(Long chatId) {
		CampingChat chat = chats.get(chatId);
		if (chat == null) {
			chat = new CampingChat(chatId);
			chatEvents.add(chat);
			chats.put(chatId, chat);
		}

		if (shouldUpdateChatDetails(chat) && bot != null && bot.isOnline()) {
			String chatname;
			try {
				Chat tChat = bot.execute(new GetChat(chatId));
				shouldSave = true;

				if (tChat.isGroupChat() || tChat.isSuperGroupChat()) {
					chatname = tChat.getTitle();
					chat.setType(ChatType.Group);
				} else {
					chatname = tChat.getFirstName();
					chat.setType(ChatType.SingleUser);
				}
				chat.setChatname(chatname);
			} catch (TelegramApiException e) {
			}
		}
		return chat;
	}

	public boolean shouldUpdateChatDetails(CampingChat chat) {
		String chatname = chat.getChatname();
		return chatname == null || chatname == CampingChat.UNKNOWN;
	}

	public EventList<CampingChat> getChatList() {
		return chatEvents;
	}

	@Override
	public boolean shouldSave() {
		return shouldSave;
	}

	@Override
	public void getXml(OutputFormatter of) {
		String innerTag = "chat";
		String outerTag = innerTag + "s";
		of.start(outerTag);

		for (CampingChat c : chatEvents) {
			of.start(innerTag);

			of.tagAndValue("id", c.getChatId());
			of.tagAndValue("type", c.getType().toString());
			of.tagAndValue("allowed", c.getAllowed().toString());

			of.finish(innerTag);
		}

		of.finish(outerTag);
		shouldSave = false;
	}
}
