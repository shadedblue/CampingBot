package ca.hapke.campbinning.bot.channels;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author Nathan Hapke
 */
public class CampingChat {
	public static final String UNKNOWN = "unknown";
	public final long chatId;
	private String chatname = UNKNOWN;
	private ChatType type = ChatType.Unknown;
	private ChatAllowed allowed = ChatAllowed.New;
	private boolean announce = false;

	// For GlazedLists to autosort
	private PropertyChangeSupport support = new PropertyChangeSupport(this);

	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		support.addPropertyChangeListener(pcl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		support.removePropertyChangeListener(pcl);
	}

	public CampingChat(long chatId) {
		this.chatId = chatId;
	}

	public long getChatId() {
		return chatId;
	}

	public String getChatname() {
		return chatname;
	}

	public void setChatname(String chatname) {
		String oldVal = this.chatname;
		this.chatname = chatname;

		support.firePropertyChange("chatname", oldVal, chatname);
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
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(chatname);
		builder.append("[");
		if (type != null) {
			builder.append(type);
			builder.append(":");
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
}
