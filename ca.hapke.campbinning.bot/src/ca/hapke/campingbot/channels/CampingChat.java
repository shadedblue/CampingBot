package ca.hapke.campingbot.channels;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import ca.hapke.campingbot.log.DatabaseConsumer;

/**
 * @author Nathan Hapke
 */
@Entity
@Table(name = CampingChat.CHAT_TABLE, schema = DatabaseConsumer.SCHEMA)
public class CampingChat {
	private static final int UNKNOWN_ID = 0;
	public static final String CHAT_TABLE = "chats";
	public static final String UNKNOWN = "unknown";
	@Id
	public long chatId = UNKNOWN_ID;
	private String chatname = UNKNOWN;
	private ChatType type = ChatType.Unknown;
	private ChatAllowed allowed = ChatAllowed.New;
	private boolean announce = false;
	@ElementCollection
	@CollectionTable(name = "activeUserId", joinColumns = @JoinColumn(name = "chatId"))
	private Set<Long> activeUserIds;

	// For GlazedLists to autosort
	@Transient
	private PropertyChangeSupport support = new PropertyChangeSupport(this);

	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		support.addPropertyChangeListener(pcl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		support.removePropertyChangeListener(pcl);
	}

	public CampingChat() {
		// necessary for JPA
	}

	public CampingChat(long chatId) {
		this.chatId = chatId;
		activeUserIds = new TreeSet<>();
		addPersistence();
	}

	public long getChatId() {
		return chatId;
	}

	public void setChatId(long chatId) {
		if (this.chatId == UNKNOWN_ID)
			this.chatId = chatId;
		updatePersistence();
	}

	public String getChatname() {
		return chatname;
	}

	public void setChatname(String chatname) {
		String oldVal = this.chatname;
		this.chatname = chatname;

		support.firePropertyChange("chatname", oldVal, chatname);
		updatePersistence();
	}

	public boolean shouldUpdateChatDetails() {
		String oldName = chatname;
		if (oldName == null || oldName == CampingChat.UNKNOWN || CampingChat.UNKNOWN.equalsIgnoreCase(oldName))
			return true;

		return false;
	}

	public ChatType getType() {
		return type;
	}

	public void setType(ChatType type) {
		if ((this.type == null || this.type == ChatType.Unknown) && type != null) {
			ChatType oldVal = this.type;
			this.type = type;
			support.firePropertyChange("type", oldVal, type);
			updatePersistence();
		}
	}

	public void setType(String s) {
		ChatType t = ChatType.valueOf(s);
		setType(t);
	}

	public ChatAllowed getAllowed() {
		return allowed;
	}

	public void setAllowed(ChatAllowed allowed) {
		if (allowed != null) {
			if (allowed == ChatAllowed.NewAnnounced && this.allowed != ChatAllowed.New)
				return;
			ChatAllowed oldVal = this.allowed;
			this.allowed = allowed;
			support.firePropertyChange("allowed", oldVal, allowed);
			updatePersistence();
		}
	}

	public void setAllowed(String s) {
		ChatAllowed all = ChatAllowed.valueOf(s);
		if (all != null)
			setAllowed(all);
	}

	public boolean isAnnounce() {
		return announce;
	}

	public void setAnnounce(boolean announce) {
		boolean oldVal = this.announce;
		this.announce = announce;
		support.firePropertyChange("announce", oldVal, announce);
		updatePersistence();
	}

	public Set<Long> getActiveUserIds() {
		return activeUserIds;
	}

	public void setActiveUserIds(SortedSet<Long> activeUserIds) {
		if (this.activeUserIds == null) {
			this.activeUserIds = activeUserIds;
			updatePersistence();
		}
	}

	public boolean addActiveUser(long e) {
		if (type != ChatType.Group)
			return false;

		boolean add = activeUserIds.add(e);
		if (add)
			updatePersistence();
		return add;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(chatname);
		builder.append("[");
		if (type != null) {
			builder.append(type);
			builder.append(":");
			if (activeUserIds != null && type == ChatType.Group) {
				builder.append(activeUserIds);
			}
		}
		builder.append(chatId);
		if (allowed != null) {
			builder.append("=");
			builder.append(allowed);
		}
		if (announce) {
			builder.append(" +Announce");
		}
		builder.append("]");
		return builder.toString();
	}

	void updatePersistence() {
		DatabaseConsumer db = DatabaseConsumer.getInstance();
		if (db != null) {
			EntityManager mgr = db.getManager();
			mgr.getTransaction().begin();
			mgr.merge(this);
			mgr.getTransaction().commit();
		}
	}

	void addPersistence() {
		DatabaseConsumer db = DatabaseConsumer.getInstance();
		if (db != null) {
			EntityManager mgr = db.getManager();
			mgr.getTransaction().begin();
			mgr.persist(this);
			mgr.getTransaction().commit();
		}
	}
}
