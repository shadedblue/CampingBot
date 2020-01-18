package ca.hapke.campbinning.bot;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import ca.hapke.campbinning.bot.util.CampingUtil;

/**
 * @author Nathan Hapke
 */
public enum BotCommand {

	AllFaces("allfaces", true, true, BotCommandIds.BALLS | BotCommandIds.USE),
	AllBalls("allballs", true, true, BotCommandIds.BALLS | BotCommandIds.USE),
	// Stats("stats", true, false),
	// StatsEndOfWeek("stats-eow", true, false),
	Countdown("countdown", true, false),

	// Activator is the user who invokes /rant
	RantActivatorInitiation("rant", true, true, BotCommandIds.RANT | BotCommandIds.VOTING | BotCommandIds.SET),
	AitaActivatorInitiation("aita", true, true, BotCommandIds.AITA | BotCommandIds.VOTING | BotCommandIds.SET),
	VoteInitiationFailed(null, true, true, BotCommandIds.VOTING | BotCommandIds.FAILURE),
	// Ranter is the person who did the complaining
	VoteTopicInitiation(null, true, true, BotCommandIds.VOTING | BotCommandIds.REGULAR_CHAT | BotCommandIds.SET),
	VoteActivatorComplete(null, true, true, BotCommandIds.VOTING | BotCommandIds.FINISH),
	VoteTopicComplete(null, true, true, BotCommandIds.VOTING | BotCommandIds.REGULAR_CHAT | BotCommandIds.FINISH),

	// TODO add Rant events for Completion as non-rant?

	Vote(null, true, true, BotCommandIds.VOTING | BotCommandIds.USE),
	AllNicknames("allnicknames", false, false),
	SetNickname("setnickname", true, true, BotCommandIds.NICKNAME | BotCommandIds.SET),
	SetNicknameRejected(null, true, true, BotCommandIds.NICKNAME | BotCommandIds.FAILURE),
	Spell("spell", true, true, BotCommandIds.SPELL | BotCommandIds.USE),
	SpellDipshit(null, true, true, BotCommandIds.SPELL | BotCommandIds.FAILURE),
	Reload("reload", true, false),
	// Test("test", true, false),
	MBIYF(null, true, true, BotCommandIds.BALLS | BotCommandIds.USE),
	MBIYFDipshit(null, true, true, BotCommandIds.BALLS | BotCommandIds.FAILURE),
	PleasureModel(null, true, true, BotCommandIds.PLEASURE),
	PartyEveryday(null, true, true, BotCommandIds.PARTY),
	UiString(null, true, false),
	NicknameConversion(null, true, true, BotCommandIds.NICKNAME | BotCommandIds.USE),
	RegularChatUpdate(null, true, true, BotCommandIds.REGULAR_CHAT | BotCommandIds.TEXT),
	RegularChatReply(null, true, true, BotCommandIds.REGULAR_CHAT | BotCommandIds.REPLY),
	RegularChatEdit(null, true, true, BotCommandIds.REGULAR_CHAT | BotCommandIds.EDIT),
	RegularChatGif(null, true, true, BotCommandIds.REGULAR_CHAT | BotCommandIds.GIF),
	RegularChatSticker(null, true, true, BotCommandIds.REGULAR_CHAT | BotCommandIds.STICKER),
	RegularChatPhoto(null, true, true, BotCommandIds.REGULAR_CHAT | BotCommandIds.PIC),
	RegularChatVideo(null, true, true, BotCommandIds.REGULAR_CHAT | BotCommandIds.VID);

	private static final String BOT_COMMAND = "bot_command";

	public final String command;
	private boolean forUi;
	private boolean forDb;
	public final long id;

	private BotCommand(String command, boolean forUi, boolean forDb) {
		this(command, forUi, forDb, 0);
	}

	/**
	 * @param command The /command that the bot should respond to in the chat
	 * @param forUi
	 * @param forDb
	 * @param id      For logging purposes
	 */
	private BotCommand(String command, boolean forUi, boolean forDb, long id) {
		this.command = command;
		this.forUi = forUi;
		this.forDb = forDb;
		this.id = id;
	}

	public boolean isForUi() {
		return forUi;
	}

	public boolean isForDb() {
		return forDb;
	}

	public static BotCommand fromText(String msg) {
		if (msg == null)
			return null;
		for (BotCommand bc : values()) {
			if (msg.equalsIgnoreCase(bc.command))
				return bc;
		}
		return null;
	}

	public static BotCommand findCommand(Update update, User me) {
		Message message = update.getMessage();
		List<MessageEntity> entities = message.getEntities();
		if (entities != null) {
			for (MessageEntity msgEnt : entities) {
				String type = msgEnt.getType();
				if (BOT_COMMAND.equalsIgnoreCase(type) && msgEnt.getOffset() == 0) {
					boolean targetsMe;
					String msg = msgEnt.getText();
					String command = msg;
					int start = msg.indexOf('/');
					int at = msg.indexOf('@');
					int length = msg.length();
					if (at > 0) {
						command = msg.substring(start + 1, at);
						String target = msg.substring(at + 1, length);
						targetsMe = CampingUtil.matchOne(target, me.getFirstName(), me.getUserName(), me.getLastName());
					} else {
						command = msg.substring(start + 1);
						targetsMe = true;
					}

					if (targetsMe) {
						return BotCommand.fromText(command);
					}
				}
			}
		}

		return null;
	}
}
