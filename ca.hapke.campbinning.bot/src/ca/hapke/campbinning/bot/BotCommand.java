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
public enum BotCommand implements CommandType {

	AllFaces("allfaces", true, BotCommandIds.BALLS | BotCommandIds.USE),
	AllBalls("allballs", true, BotCommandIds.BALLS | BotCommandIds.USE),
	Countdown("countdown", false),

	// Activator is the user who invokes /rant
	RantActivatorInitiation("rant", true, BotCommandIds.RANT | BotCommandIds.VOTING | BotCommandIds.SET),
	AitaActivatorInitiation("aita", true, BotCommandIds.AITA | BotCommandIds.VOTING | BotCommandIds.SET),
	VoteInitiationFailed(null, true, BotCommandIds.VOTING | BotCommandIds.FAILURE),
	// Ranter is the person who did the complaining
	VoteTopicInitiation(null, true, BotCommandIds.VOTING | BotCommandIds.REGULAR_CHAT | BotCommandIds.SET),
	VoteActivatorComplete(null, true, BotCommandIds.VOTING | BotCommandIds.FINISH),
	VoteTopicComplete(null, true, BotCommandIds.VOTING | BotCommandIds.REGULAR_CHAT | BotCommandIds.FINISH),

	// TODO add Rant events for Completion as non-rant?

	Vote(null, true, BotCommandIds.VOTING | BotCommandIds.USE),
	AllNicknames("allnicknames", false),
	SetNickname("setnickname", true, BotCommandIds.NICKNAME | BotCommandIds.SET),
	SetNicknameRejected(null, true, BotCommandIds.NICKNAME | BotCommandIds.FAILURE),
	Spell("spell", true, BotCommandIds.SPELL | BotCommandIds.USE),
	SpellDipshit(null, true, BotCommandIds.SPELL | BotCommandIds.FAILURE),
	Reload("reload", false),
	// Test("test", false),
	MBIYF(null, true, BotCommandIds.BALLS | BotCommandIds.USE),
	MBIYFDipshit(null, true, BotCommandIds.BALLS | BotCommandIds.FAILURE),
	PleasureModel(null, true, BotCommandIds.PLEASURE | BotCommandIds.GIF),
	IunnoGoogleIt("iunno", true, BotCommandIds.SILLY_RESPONSE | BotCommandIds.GIF),
	PartyEveryday(null, true, BotCommandIds.SILLY_RESPONSE | BotCommandIds.GIF),
	UiString(null, false),
	NicknameConversion(null, true, BotCommandIds.NICKNAME | BotCommandIds.USE);

	private static final String BOT_COMMAND = "bot_command";

	public final String command;
	private final boolean forDb;
	private final long id;

	private BotCommand(String command, boolean forDb) {
		this(command, forDb, 0);
	}

	/**
	 * @param command The /command that the bot should respond to in the chat
	 * @param forUi
	 * @param forDb
	 * @param id      For logging purposes
	 */
	private BotCommand(String command, boolean forDb, long id) {
		this.command = command;
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
