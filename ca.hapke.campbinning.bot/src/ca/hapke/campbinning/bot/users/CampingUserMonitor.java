package ca.hapke.campbinning.bot.users;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.User;

import ca.hapke.campbinning.bot.CampingBot;
import ca.hapke.campbinning.bot.CampingSerializable;
import ca.hapke.campbinning.bot.users.CampingUser.Birthday;
import ca.hapke.campbinning.bot.util.CampingUtil;
import ca.hapke.campbinning.bot.xml.OutputFormatter;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

/**
 * Singleton
 * 
 * @author Nathan Hapke
 */
public class CampingUserMonitor implements CampingSerializable {

	private boolean shouldSave = false;
	public static final int UNKNOWN_USER_ID = -1;
	private static CampingUserMonitor instance = new CampingUserMonitor();

	public static CampingUserMonitor getInstance() {
		return instance;
	}

	private CampingUserMonitor() {
		ListEventListener<CampingUser> listChangeListener = new ListEventListener<CampingUser>() {
			@Override
			public void listChanged(ListEvent<CampingUser> listChanges) {
				shouldSave = true;
			}
		};
		users.addListEventListener(listChangeListener);
	}

	private final ObservableElementList.Connector<CampingUser> userConnector = GlazedLists
			.beanConnector(CampingUser.class);
	private final EventList<CampingUser> users = GlazedLists
			.threadSafeList(new ObservableElementList<>(new BasicEventList<CampingUser>(), userConnector));

	private int nextCampingId = 1;
	private Set<Integer> usedCampingIds = new HashSet<>();
	private final Map<Integer, CampingUser> telegramIdMap = new HashMap<Integer, CampingUser>();
	private final Map<String, CampingUser> usernameMap = new HashMap<String, CampingUser>();

	public int getNextCampingId() {
		usedCampingIds.add(nextCampingId);
		return nextCampingId++;
	}

	public int getNextCampingId(int suggestedId) {
		if (suggestedId == CampingUserMonitor.UNKNOWN_USER_ID || usedCampingIds.contains(suggestedId)) {
			return getNextCampingId();
		} else {
			usedCampingIds.add(suggestedId);
			nextCampingId = Math.max(suggestedId + 1, nextCampingId);
			return suggestedId;
		}
	}

	public void setNextCampingId(int suggestedId) {
		this.nextCampingId = Math.max(suggestedId, nextCampingId);
	}

	public CampingUser find(Integer key) {
		return telegramIdMap.get(key);
	}

	public CampingUser find(String key) {
		return usernameMap.get(CampingUtil.generateUsernameKey(key));
	}

	public CampingUser getUser(MessageEntity targeting) {
		if (targeting == null)
			return null;
		String text = targeting.getText();
		User user = targeting.getUser();

		CampingUser result = getUser(user, text);
		if (result == null)
			return monitor(targeting);
		return result;
	}

	public CampingUser getUser(User user) {
		return getUser(user, user.getFirstName());
	}

	public CampingUser getUser(int id) {
		return telegramIdMap.get(id);
	}

	public CampingUser getUser(User user, String text) {
		CampingUser result = null;
		if (user != null) {
			result = telegramIdMap.get(user.getId());
		}
		if (result == null) {
			result = getUser(text);
		}
		return result;
	}

	public CampingUser getUser(String username) {
		return usernameMap.get(CampingUtil.generateUsernameKey(username));
	}

	public CampingUser monitor(User fromUser) {
		Integer id = fromUser.getId();
		int telegramId = id != null ? id.intValue() : UNKNOWN_USER_ID;
		String username = fromUser.getUserName();

		String lastname = fromUser.getLastName();
		String firstname = fromUser.getFirstName();
		return monitor(telegramId, username, firstname, lastname);
	}

	public CampingUser monitor(User fromUser, List<MessageEntity> entities) {
		CampingUser campingFromUser = null;
		if (fromUser != null) {
			campingFromUser = monitor(fromUser);
		}
		if (entities != null) {
			for (MessageEntity msgEnt : entities) {
				monitor(msgEnt);
			}
		}
		return campingFromUser;
	}

	public CampingUser monitor(MessageEntity msgEnt) {
		String type = msgEnt.getType();
		if (CampingBot.TEXT_MENTION.equalsIgnoreCase(type)) {
			return monitor(msgEnt.getUser());
		}
		if (CampingBot.MENTION.equalsIgnoreCase(type)) {
			return monitor(UNKNOWN_USER_ID, msgEnt.getText(), null, null);
		}

		return null;
	}

	public CampingUser monitor(int id, String username, String firstname, String lastname) {
		return monitor(UNKNOWN_USER_ID, id, username, firstname, lastname);
	}

	public CampingUser monitor(int campingIdInt, int telegramId, String username, String firstname, String lastname) {
		String usernameKey = CampingUtil.generateUsernameKey(username);

		CampingUser target = telegramIdMap.get(telegramId);
		CampingUser usernameTarget = null;
		if (username != null && !username.equalsIgnoreCase("null")) {
			usernameTarget = usernameMap.get(usernameKey);
		}

		if (target == null && usernameTarget == null) {
			// never seen this guy before
			target = new CampingUser(campingIdInt, telegramId, username, firstname, lastname);
			users.add(target);
			if (telegramId != UNKNOWN_USER_ID)
				telegramIdMap.put(telegramId, target);
			if (username != null)
				usernameMap.put(usernameKey, target);

			shouldSave = true;

			return target;

		} else if (target == null && usernameTarget != null) {
			if (telegramId != UNKNOWN_USER_ID) {
				// learned the id
				usernameTarget.setId(telegramId);
				telegramIdMap.put(telegramId, usernameTarget);

				shouldSave = true;
			}
			target = usernameTarget;
		} else if (target != null && usernameTarget == null) {
			if (username != null) {
				// learned the username
				target.setUsername(username);
				usernameMap.put(usernameKey, target);

				shouldSave = true;
			}
		}
		if (target != null && usernameTarget != null && target != usernameTarget) {
			target.mergeFrom(usernameTarget);
			usernameMap.put(usernameKey, target);
			users.remove(usernameTarget);

			shouldSave = true;
		}
		if (target != null) {
			target.setFirstname(firstname);
			target.setLastname(lastname);

			shouldSave = true;
		}
		return target;
	}

	public EventList<CampingUser> getUsers() {
		return users;
	}

	@Override
	public void getXml(OutputFormatter of) {
		String usersTag = "users";
		of.start(usersTag);
		of.tagAndValue("nextCampingId", nextCampingId);
		// sb.append("<" + usersTag + ">");

		for (CampingUser u : users) {
			String userTag = "user";
			of.start(userTag);

			of.tagAndValue("campingId", u.getCampingId());
			of.tagAndValue("id", u.getTelegramId());

			String usernameTag = "username";
			String username = u.getUsername();
			if (username != null && !"null".equals(username))
				username = CampingUtil.prefixAt(username);
			of.tagAndValue(usernameTag, username);

			of.tagAndValue("first", u.getFirstname());
			of.tagAndValue("last", u.getLastname());
			of.tagAndValue("nickname", u.getNickname());
			Birthday bday = u.getBirthday();
			if (bday != null) {
				of.tagAndValue("birthdayMonth", bday.getMonth());
				of.tagAndValue("birthdayDay", bday.getDay());
			}

			of.finish(userTag);
		}
		of.finish(usersTag);
		shouldSave = false;
	}

	@Override
	public boolean shouldSave() {
		return shouldSave;
	}
}
