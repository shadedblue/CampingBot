package ca.hapke.campbinning.bot.channels;

import java.util.HashMap;
import java.util.Map;

import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBotEngine;
import ca.hapke.campbinning.bot.CampingSerializable;
import ca.hapke.campbinning.bot.response.CommandResult;
import ca.hapke.campbinning.bot.response.TextCommandResult;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.users.CampingUserMonitor;
import ca.hapke.campbinning.bot.xml.OutputFormatter;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.matchers.Matcher;

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
		ListEventListener<CampingChat> listChangeListener = new ListEventListener<CampingChat>() {
			@Override
			public void listChanged(ListEvent<CampingChat> listChanges) {
				shouldSave = true;
			}
		};
		chatEvents.addListEventListener(listChangeListener);
	}

	private final Map<Long, CampingChat> chats = new HashMap<>();
	private final ObservableElementList.Connector<CampingChat> chatConnector = GlazedLists
			.beanConnector(CampingChat.class);
	private final EventList<CampingChat> chatEvents = GlazedLists
			.threadSafeList(new ObservableElementList<>(new BasicEventList<CampingChat>(), chatConnector));
	private final FilterList<CampingChat> announceChats = new FilterList<>(chatEvents, new Matcher<CampingChat>() {
		@Override
		public boolean matches(CampingChat item) {
			return item.isAnnounce();
		}
	});

	public CampingChat get(Long chatId) {
		boolean shouldNotify = false;
		CampingChat chat = chats.get(chatId);
		if (chat == null) {
			chat = new CampingChat(chatId);
			chatEvents.add(chat);
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

	public void updateChat(Long chatId, CampingChat chat) {
		String chatname = null;
		try {
			Chat tChat = bot.execute(new GetChat(chatId));
			shouldSave = true;

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

			CommandResult msg = new TextCommandResult(BotCommand.JoinThread)
					.add("Invited to: " + chat.getChatname() + " (" + chat.chatId + ")");
			try {
				msg.send(bot, chatId);
			} catch (TelegramApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public EventList<CampingChat> getChatList() {
		return chatEvents;
	}

	public FilterList<CampingChat> getAnnounceChats() {
		return announceChats;
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
			of.tagAndValue("name", c.getChatname());
			of.tagAndValue("allowed", c.getAllowed().toString());
			of.tagAndValue("announce", c.isAnnounce());

			of.finish(innerTag);
		}

		of.finish(outerTag);
		shouldSave = false;
	}
}
