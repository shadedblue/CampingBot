package ca.hapke.campbinning.bot;

/**
 * @author Nathan Hapke
 */
public enum InputType implements CommandType {

	RegularChatUpdate(true, BotCommandIds.REGULAR_CHAT | BotCommandIds.TEXT),
	RegularChatReply(true, BotCommandIds.REGULAR_CHAT | BotCommandIds.REPLY),
	RegularChatEdit(true, BotCommandIds.REGULAR_CHAT | BotCommandIds.EDIT),
	RegularChatGif(true, BotCommandIds.REGULAR_CHAT | BotCommandIds.GIF),
	RegularChatSticker(true, BotCommandIds.REGULAR_CHAT | BotCommandIds.STICKER),
	RegularChatPhoto(true, BotCommandIds.REGULAR_CHAT | BotCommandIds.PIC),
	RegularChatVideo(true, BotCommandIds.REGULAR_CHAT | BotCommandIds.VID),
	InlineChatUpdate(true, BotCommandIds.REGULAR_CHAT | BotCommandIds.TEXT | BotCommandIds.INLINE);

	private final boolean forDb;
	private final long id;

	private InputType(boolean forDb, long id) {
		this.forDb = forDb;
		this.id = id;
	}

	@Override
	public boolean isForDb() {
		return forDb;
	}

	@Override
	public long getId() {
		return id;
	}
}
