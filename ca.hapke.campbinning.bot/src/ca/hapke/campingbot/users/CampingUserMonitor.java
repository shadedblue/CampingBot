package ca.hapke.campingbot.users;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.User;

import ca.hapke.campingbot.BotConstants;
import ca.hapke.campingbot.CampingSystem;
import ca.hapke.campingbot.log.DatabaseConsumer;
import ca.hapke.campingbot.users.CampingUser.Birthday;
import ca.hapke.campingbot.util.CampingUtil;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import ca.odell.glazedlists.matchers.Matcher;

/**
 * Singleton
 * 
 * @author Nathan Hapke
 */
public class CampingUserMonitor {

	public static final int UNKNOWN_USER_ID = -1;
	private static CampingUserMonitor instance = new CampingUserMonitor();

	public static CampingUserMonitor getInstance() {
		return instance;
	}

	private CampingUserMonitor() {
	}

	private final ObservableElementList.Connector<CampingUser> userConnector = GlazedLists
			.beanConnector(CampingUser.class);
	private final EventList<CampingUser> users = GlazedLists
			.threadSafeList(new ObservableElementList<>(new BasicEventList<CampingUser>(), userConnector));

	private long nextCampingId = 1;
	private Set<Long> usedCampingIds = new HashSet<>();
	private final Map<Long, CampingUser> telegramIdMap = new HashMap<>();
	private final Map<String, CampingUser> usernameMap = new HashMap<>();
	private final FilterList<CampingUser> adminUsers = new FilterList<>(users, new Matcher<CampingUser>() {
		@Override
		public boolean matches(CampingUser item) {
			return CampingSystem.getInstance().isAdmin(item);
		}
	});

	public void load() {
		List<CampingUser> incoming = DatabaseConsumer.getInstance().loadUsers();
		for (CampingUser u : incoming) {
			addUser(u);
		}
	}

	public long getNextCampingId() {
		usedCampingIds.add(nextCampingId);
		return nextCampingId++;
	}

	public long getNextCampingId(long suggestedId) {
		if (suggestedId == CampingUserMonitor.UNKNOWN_USER_ID || usedCampingIds.contains(suggestedId)) {
			return getNextCampingId();
		} else {
			usedCampingIds.add(suggestedId);
			nextCampingId = Math.max(suggestedId + 1, nextCampingId);
			return suggestedId;
		}
	}

	public void setNextCampingId(long suggestedId) {
		this.nextCampingId = Math.max(suggestedId, nextCampingId);
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

	public CampingUser getUser(long id) {
		return telegramIdMap.get(id);
	}

	public CampingUser getUser(User user, String text) {
		CampingUser result = null;
		if (user != null) {
			Long id = user.getId();
			result = telegramIdMap.get(id);
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
		Long id = fromUser.getId();
		long telegramId = id != null ? id.intValue() : UNKNOWN_USER_ID;
		String username = fromUser.getUserName();

		String lastname = fromUser.getLastName();
		String firstname = fromUser.getFirstName();
		return monitor(telegramId, username, firstname, lastname, true);
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
		if (BotConstants.TEXT_MENTION.equalsIgnoreCase(type)) {
			return monitor(msgEnt.getUser());
		}
		if (BotConstants.MENTION.equalsIgnoreCase(type)) {
			return monitor(UNKNOWN_USER_ID, msgEnt.getText(), null, null, false);
		}

		return null;
	}

	public CampingUser monitor(long id, String username, String firstname, String lastname, boolean interacting) {
		return monitor(UNKNOWN_USER_ID, id, username, firstname, lastname, null, interacting);
	}

	public CampingUser monitor(long campingIdInt, long telegramId, String username, String firstname, String lastname,
			String initials, boolean interacting) {
		String usernameKey = CampingUtil.generateUsernameKey(username);

		CampingUser target = telegramIdMap.get(telegramId);
		CampingUser usernameTarget = null;
		if (username != null && !username.equalsIgnoreCase("null")) {
			usernameTarget = usernameMap.get(usernameKey);
		}

		if (target == null && usernameTarget == null) {
			// never seen this guy before
			target = new CampingUser(campingIdInt, telegramId, username, firstname, lastname, initials);
			target.setSeenInteraction(interacting);
			addUser(target);

		} else if (target == null && usernameTarget != null) {
			if (telegramId != UNKNOWN_USER_ID) {
				// learned the id
				usernameTarget.setTelegramId(telegramId);
				usernameTarget.setSeenInteraction(interacting);
				telegramIdMap.put(telegramId, usernameTarget);
			}
			target = usernameTarget;
		} else if (target != null && usernameTarget == null) {
			if (username != null) {
				// learned the username
				target.setUsername(username);
				usernameMap.put(usernameKey, target);
			}
		}
		if (target != null && usernameTarget != null && target != usernameTarget) {
			target.mergeFrom(usernameTarget);
			usernameMap.put(usernameKey, target);
			users.remove(usernameTarget);
			target.setSeenInteraction(usernameTarget.isSeenInteraction());
		}
		if (target != null) {
			target.setFirstname(firstname);
			target.setLastname(lastname);
			target.setSeenInteraction(interacting);
		}
		return target;
	}

	public void addUser(CampingUser target) {
		// force bday object to get created
		Birthday bday = target.getBirthday();
		users.add(target);

		long telegramId = target.getTelegramId();
		String username = target.getUsername();
		if (telegramId != UNKNOWN_USER_ID)
			telegramIdMap.put(telegramId, target);
		if (username != null && !"null".equalsIgnoreCase(username)) {
			String usernameKey = CampingUtil.generateUsernameKey(username);
			usernameMap.put(usernameKey, target);
		}
	}

	public EventList<CampingUser> getUsers() {
		return users;
	}

	public FilterList<CampingUser> getAdminUsers() {
		return adminUsers;
	}
}
