package ca.hapke.campbinning.bot;

/**
 * @author Nathan Hapke
 */
public enum InputType implements CommandType {

	RegularChatUpdate(true, true, BotCommandIds.REGULAR_CHAT | BotCommandIds.TEXT),
	RegularChatReply(true, true, BotCommandIds.REGULAR_CHAT | BotCommandIds.REPLY),
	RegularChatEdit(true, true, BotCommandIds.REGULAR_CHAT | BotCommandIds.EDIT),
	RegularChatGif(true, true, BotCommandIds.REGULAR_CHAT | BotCommandIds.GIF),
	RegularChatSticker(true, true, BotCommandIds.REGULAR_CHAT | BotCommandIds.STICKER),
	RegularChatPhoto(true, true, BotCommandIds.REGULAR_CHAT | BotCommandIds.PIC),
	RegularChatVideo(true, true, BotCommandIds.REGULAR_CHAT | BotCommandIds.VID),
	InlineChatUpdate(true, true, BotCommandIds.REGULAR_CHAT | BotCommandIds.TEXT | BotCommandIds.INLINE);

	private final boolean forUi;
	private final boolean forDb;
	private final long id;

	private InputType(boolean forUi, boolean forDb, long id) {
		this.forUi = forUi;
		this.forDb = forDb;
		this.id = id;
	}

	@Override
	public boolean isForUi() {
		return forUi;
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
