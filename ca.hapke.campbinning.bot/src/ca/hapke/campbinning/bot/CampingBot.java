package ca.hapke.campbinning.bot;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campbinning.bot.category.HasCategories;
import ca.hapke.campbinning.bot.commands.CountdownGenerator;
import ca.hapke.campbinning.bot.commands.MbiyfCommand;
import ca.hapke.campbinning.bot.commands.PleasureModelCommand;
import ca.hapke.campbinning.bot.commands.SpellDipshitException;
import ca.hapke.campbinning.bot.commands.SpellGenerator;
import ca.hapke.campbinning.bot.commands.TextCommandResult;
import ca.hapke.campbinning.bot.commands.inline.InlineCommand;
import ca.hapke.campbinning.bot.commands.inline.NicknameConversionCommand;
import ca.hapke.campbinning.bot.commands.inline.SpellInlineCommand;
import ca.hapke.campbinning.bot.commands.voting.VoteCreationFailedException;
import ca.hapke.campbinning.bot.commands.voting.VotingManager;
import ca.hapke.campbinning.bot.interval.CampingIntervalThread;
import ca.hapke.campbinning.bot.log.DatabaseConsumer;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.users.NicknameRejectedException;
import ca.hapke.campbinning.bot.util.CampingUtil;

/**
 * @author Nathan Hapke
 */
public class CampingBot extends CampingBotEngine {

	public static final String STRING_NULL = "null";

	private Resources res = new Resources();
	private VotingManager voting = new VotingManager();
	private SpellGenerator spellGen = new SpellGenerator();

	private MbiyfCommand ballsCommand = new MbiyfCommand(this, res);

	private CountdownGenerator countdownGen = new CountdownGenerator(res, ballsCommand);
	private DatabaseConsumer databaseConsumer = new DatabaseConsumer(system, eventLogger);

	private InlineCommand nicknameConverter = new NicknameConversionCommand();
	private InlineCommand spellInline = new SpellInlineCommand(spellGen);

	private CampingXmlSerializer serializer = new CampingXmlSerializer(system,
			// sundayStats,
			spellGen, countdownGen, userMonitor);

	private HasCategories[] hasCategories;

	public CampingBot() {
		res.loadAllEmoji();
		serializer.load();

		textCommands.add(ballsCommand);
		textCommands.add(new PleasureModelCommand(this));
		inlineCommands.add(spellInline);
		inlineCommands.add(nicknameConverter);
		callbackCommands.add(voting);

		CampingIntervalThread.put(serializer);
		CampingIntervalThread.put(databaseConsumer);
		// CampingIntervalThread.put(userMonitor);
		// CampingIntervalThread.put(sundayStats);
		CampingIntervalThread.put(ballsCommand);
		CampingIntervalThread.put(voting);

		hasCategories = new HasCategories[] { spellGen, countdownGen, voting };
	}

	@Override
	public String getBotToken() {
		return system.getToken();
	}

	@Override
	public String getBotUsername() {
		return system.getBotUsername();
	}

	public CampingUser getMeCamping() {
		return meCamping;
	}

	public HasCategories[] getCategories() {
		return hasCategories;
	}

	@Override
	protected TextCommandResult reactToSlashCommandInText(BotCommand command, Message message, Long chatId,
			CampingUser campingFromUser) throws TelegramApiException {
		String rest = null;
		switch (command) {
		case NicknameConversion:
		case MBIYF:
		case MBIYFDipshit:
		case RegularChatUpdate:
		case RegularChatReply:
		case PleasureModel:
			// NOOP
			break;
		case VoteTopicComplete:
		case Vote:
		case VoteActivatorComplete:
		case VoteTopicInitiation:
		case VoteInitiationFailed:
			// case StatsEndOfWeek:
			// NOOP : internal events, not responses
			break;

		case AllBalls:
			sendMsg(chatId, res.listBalls());
			break;
		case AllFaces:
			sendMsg(chatId, res.listFaces());
			break;
		// case Stats:
		// sendMsg(chatId, stats.statsCommand(chatId));
		// break;

		case SpellDipshit:
			// NOOP, but must be before Spell
			break;
		case Spell:
			try {
				rest = spellCommand(campingFromUser, message);
				sendMsg(chatId, rest);
			} catch (SpellDipshitException e) {
				command = BotCommand.SpellDipshit;
				sendMsg(chatId, campingFromUser, SpellDipshitException.YA_DIPSHIT);
			}
			break;

		case RantActivatorInitiation:
		case AitaActivatorInitiation:
			try {
				if (command == BotCommand.RantActivatorInitiation)
					rest = voting.startRant(this, message, chatId, campingFromUser);
				else if (command == BotCommand.AitaActivatorInitiation)
					rest = voting.startAita(this, message, chatId, campingFromUser);

			} catch (VoteCreationFailedException rcfe) {
				command = BotCommand.VoteInitiationFailed;
				String reason = rcfe.getMessage();
				rest = reason;
				sendMsg(chatId, campingFromUser, reason);
			}
			break;


		case Countdown:
			String countdown = countdownGen.countdownCommand(userMonitor, chatId);
			sendMsg(chatId, countdown);
			rest = countdown;
			break;

		case AllNicknames:
			rest = allNicknamesCommand();
			sendMsg(chatId, rest);
			break;
		case SetNickname:
			try {
				rest = setNicknameCommand(campingFromUser, message);
			} catch (NicknameRejectedException e) {
				command = BotCommand.SetNicknameRejected;
				rest = e.getMessage();
			}
			sendMsg(chatId, campingFromUser, rest);
			break;

		case SetNicknameRejected:
			// must be after SetNickname:
			break;
		case Reload:
			rest = reloadCommand(campingFromUser);
			sendMsg(chatId, campingFromUser, rest);
			break;
		// case Test:
		// rest = testCommand(campingFromUser, chatId);
		// break;
		case UiString:
			break;
		// case RegularChatAnimation:
		case RegularChatGif:
		case RegularChatPhoto:
		case RegularChatEdit:
		case RegularChatVideo:
		case RegularChatSticker:
			break;
		}

		// return rest;
		if (rest != null)
			return new TextCommandResult(command, rest, false);
		else
			return null;
	}

	private String spellCommand(CampingUser campingFromUser, Message message) throws SpellDipshitException {
		List<MessageEntity> entities = message.getEntities();
		CampingUser targetUser = findTarget(entities);
		if (targetUser == null) {
			throw new SpellDipshitException();
		}

		String out = spellGen.cast(targetUser.target());
		SpellGenerator.countSpellActivation(campingFromUser, targetUser);
		return out;
	}

	private String reloadCommand(CampingUser fromUser) {
		String result;
		if (system.isAdmin(fromUser)) {
			res.loadAllEmoji();
			serializer.load();
			result = " Done!";
		} else {
			result = " Access Denied!";
		}
		return result;
	}

	// public String testCommand(CampingUser fromUser, Long chatId) {
	// return "";
	// }

	private String setNicknameCommand(CampingUser campingFromUser, Message message) throws NicknameRejectedException {
		String originalMsg = message.getText();
		List<MessageEntity> entities = message.getEntities();
		int targetOffset = originalMsg.indexOf(" ") + 1;
		int nickOffset = originalMsg.indexOf(" ", targetOffset) + 1;
		if (targetOffset > 0 && nickOffset > targetOffset + 1) {
			String newNickname = originalMsg.substring(nickOffset);
			MessageEntity targeting = null;

			for (MessageEntity msgEnt : entities) {
				int offset = msgEnt.getOffset();
				String type = msgEnt.getType();
				if (offset == targetOffset && (MENTION.equalsIgnoreCase(type) || TEXT_MENTION.equalsIgnoreCase(type))) {
					targeting = msgEnt;
				}
			}
			if (targeting != null) {
				CampingUser targetUser = userMonitor.getUser(targeting);

				if (targetUser != null) {
					if (targetUser == campingFromUser) {
						throw new NicknameRejectedException(NicknameRejectedException.CANT_GIVE_YOURSELF_A_NICKNAME);
					} else {
						targetUser.setNickname(newNickname);

						return targetUser.getFirstOrUserName() + "'s nickname changed to: " + targetUser.target();
					}
				}
			} else {
				throw new NicknameRejectedException(NicknameRejectedException.USER_NOT_FOUND);
			}
		}
		throw new NicknameRejectedException(NicknameRejectedException.INVALID_SYNTAX);
	}

	private String allNicknamesCommand() {
		StringBuilder sb = new StringBuilder();
		for (CampingUser u : userMonitor.getUsers()) {
			String first = u.getFirstname();
			String nick = u.getNickname();
			if (CampingUtil.notEmptyOrNull(nick) && CampingUtil.notEmptyOrNull(first)) {
				sb.append("*");
				sb.append(first);
				sb.append("*: ");
				sb.append(nick);
				sb.append("\n");
			}

		}
		String out = sb.toString();
		return out;
	}

}