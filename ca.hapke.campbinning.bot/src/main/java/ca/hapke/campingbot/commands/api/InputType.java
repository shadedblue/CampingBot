package ca.hapke.campingbot.commands.api;

/**
 * @author Nathan Hapke
 */
public class InputType extends CommandType {

	public static final InputType ChatUpdate = new InputType("ChatUpdate", false,
			BotCommandIds.THREAD | BotCommandIds.SET);
	public static final InputType RegularChatUpdate = new InputType("RegularChatUpdate", true,
			BotCommandIds.REGULAR_CHAT | BotCommandIds.TEXT);
	public static final InputType RegularChatReply = new InputType("RegularChatReply", true,
			BotCommandIds.REGULAR_CHAT | BotCommandIds.REPLY);
	public static final InputType RegularChatEdit = new InputType("RegularChatEdit", true,
			BotCommandIds.REGULAR_CHAT | BotCommandIds.EDIT);
	public static final InputType RegularChatGif = new InputType("RegularChatGif", true,
			BotCommandIds.REGULAR_CHAT | BotCommandIds.GIF);
	public static final InputType RegularChatSticker = new InputType("RegularChatSticker", true,
			BotCommandIds.REGULAR_CHAT | BotCommandIds.STICKER);
	public static final InputType RegularChatPhoto = new InputType("RegularChatPhoto", true,
			BotCommandIds.REGULAR_CHAT | BotCommandIds.PIC);
	public static final InputType RegularChatVideo = new InputType("RegularChatVideo", true,
			BotCommandIds.REGULAR_CHAT | BotCommandIds.VID);
	public static final InputType InlineChatUpdate = new InputType("InlineChatUpdate", true,
			BotCommandIds.REGULAR_CHAT | BotCommandIds.TEXT | BotCommandIds.INLINE);

	private final boolean forDb;

	private InputType(String prettyName, boolean forDb, long id) {
		super(prettyName, id);
		this.forDb = forDb;
	}

	@Override
	public boolean isForDb() {
		return forDb;
	}

}
