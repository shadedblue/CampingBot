package ca.hapke.campbinning.bot.channels;

/**
 * @author Nathan Hapke
 */
public class CampingChat {
	public final long chatId;
	private String chatname;
	// private Map<CampingUser, ParticularChatUserOptions> userToOptions = new
	// HashMap<CampingUser, ParticularChatUserOptions>();
	// private final EventList<ParticularChatUserOptions> allUserOptions =
	// GlazedLists
	// .threadSafeList(new BasicEventList<ParticularChatUserOptions>());

	public CampingChat(long chatId, String chatname) {
		this.chatId = chatId;
		this.chatname = chatname;
	}

	public long getChatId() {
		return chatId;
	}

	public String getChatname() {
		return chatname;
	}

	public void setChatname(String chatname) {
		if (chatname == null || chatname.equalsIgnoreCase(CampingChatManager.UNKNOWN)) {
			this.chatname = chatname;
		}
	}

	@Override
	public String toString() {
		return "Chat[" + chatname + " #" + chatId + "]";
	}

}
