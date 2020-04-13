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

	// private Map<CampingUser, ParticularChatUserOptions> userToOptions = new
	// HashMap<CampingUser, ParticularChatUserOptions>();
	// private final EventList<ParticularChatUserOptions> allUserOptions =
	// GlazedLists
	// .threadSafeList(new BasicEventList<ParticularChatUserOptions>());

	// For GlazedLists to autosort
	private PropertyChangeSupport support = new PropertyChangeSupport(this);
	public static final String UNKNOWN_TARGET = "unknown target";

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
		if (this.chatname == null || this.chatname == UNKNOWN) {

			String oldVal = this.chatname;
			this.chatname = chatname;

			support.firePropertyChange("chatname", oldVal, chatname);
		}
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
			ChatAllowed oldVal = this.allowed;
			this.allowed = allowed;
			support.firePropertyChange("allowed", oldVal, allowed);
		}
	}

	public void setAllowed(String s) {
		ChatAllowed all = ChatAllowed.valueOf(s);
		setAllowed(all);
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
		builder.append("]");
		return builder.toString();
	}

//	@Override
//	public String toString() {
//		return "Chat[" + chatname + " #" + chatId + "]";
//	}

}
