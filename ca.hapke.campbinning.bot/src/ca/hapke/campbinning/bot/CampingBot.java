package ca.hapke.campbinning.bot;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.calendaring.monitor.CalendarMonitor;
import ca.hapke.campbinning.bot.category.HasCategories;
import ca.hapke.campbinning.bot.commands.CountdownGenerator;
import ca.hapke.campbinning.bot.commands.IunnoCommand;
import ca.hapke.campbinning.bot.commands.MbiyfCommand;
import ca.hapke.campbinning.bot.commands.PartyEverydayCommand;
import ca.hapke.campbinning.bot.commands.PleasureModelCommand;
import ca.hapke.campbinning.bot.commands.SpellDipshitException;
import ca.hapke.campbinning.bot.commands.SpellGenerator;
import ca.hapke.campbinning.bot.commands.inline.InlineCommand;
import ca.hapke.campbinning.bot.commands.inline.NicknameConversionCommand;
import ca.hapke.campbinning.bot.commands.inline.SpellInlineCommand;
import ca.hapke.campbinning.bot.commands.response.CommandResult;
import ca.hapke.campbinning.bot.commands.response.TextCommandResult;
import ca.hapke.campbinning.bot.commands.response.fragments.MentionFragment;
import ca.hapke.campbinning.bot.commands.response.fragments.TextFragment;
import ca.hapke.campbinning.bot.commands.voting.VotingManager;
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
	private VotingManager voting = new VotingManager(this);
	private SpellGenerator spellGen = new SpellGenerator();

	private MbiyfCommand ballsCommand;
	private PleasureModelCommand pleasureCommand;
	private IunnoCommand iunnoCommand;
	private PartyEverydayCommand partyCommand;

	private CountdownGenerator countdownGen;
	private DatabaseConsumer databaseConsumer;

	private InlineCommand nicknameConverter;
	private InlineCommand spellInline;

	private CampingXmlSerializer serializer;

	private HasCategories[] hasCategories;

	private CalendarMonitor calMonitor;

	public CampingBot() {
		nicknameConverter = new NicknameConversionCommand();
		pleasureCommand = new PleasureModelCommand(this);
		iunnoCommand = new IunnoCommand(this);
		partyCommand = new PartyEverydayCommand(this);
		databaseConsumer = new DatabaseConsumer(system, eventLogger);

		ballsCommand = new MbiyfCommand(this, res);
		countdownGen = new CountdownGenerator(res, ballsCommand);

		spellInline = new SpellInlineCommand(spellGen);

		serializer = new CampingXmlSerializer(system, spellGen, countdownGen, voting, partyCommand, userMonitor);

		res.loadAllEmoji();
		serializer.load();

		ballsCommand.init();

		textCommands.add(ballsCommand);
		textCommands.add(voting);
		textCommands.add(pleasureCommand);
		textCommands.add(iunnoCommand);
		textCommands.add(partyCommand);
		inlineCommands.add(spellInline);
		inlineCommands.add(nicknameConverter);
		callbackCommands.add(voting);

		calMonitor = CalendarMonitor.getInstance();
		calMonitor.add(serializer);
		calMonitor.add(databaseConsumer);
		// CampingIntervalThread.put(userMonitor);
		// CampingIntervalThread.put(sundayStats);
		calMonitor.add(ballsCommand);
		calMonitor.add(voting);

		hasCategories = new HasCategories[] { spellGen, countdownGen, voting, partyCommand };
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
	protected CommandResult reactToSlashCommandInText(BotCommand command, Message message, Long chatId,
			CampingUser campingFromUser) throws TelegramApiException {
		CommandResult result = null;
//		String rest = null;

		String value;

		switch (command) {
		case NicknameConversion:
		case MBIYF:
		case MBIYFDipshit:
		case PleasureModel:
		case PartyEveryday:
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
		case SpellDipshit:
			// NOOP, but must be before Spell
			break;

		case AllBalls:
			// sendMsg(chatId, res.listBalls());
			value = res.listBalls();
			result = new TextCommandResult(command, new TextFragment(value));
			break;
		case AllFaces:
//			sendMsg(chatId, res.listFaces());
			value = res.listFaces();
			result = new TextCommandResult(command, new TextFragment(value));
			break;
		// case Stats:
		// sendMsg(chatId, stats.statsCommand(chatId));
		// break;

		case IunnoGoogleIt:
			return iunnoCommand.textCommand(campingFromUser, null, chatId, message);

		case Spell:
//			try {
//				rest = spellCommand(campingFromUser, message);
//				sendMsg(chatId, rest);
//			} catch (SpellDipshitException e) {
//				command = BotCommand.SpellDipshit;
//				sendMsg(chatId, campingFromUser, SpellDipshitException.YA_DIPSHIT);
//			}
			result = spellCommand(campingFromUser, message);
			break;

		case RantActivatorInitiation:
		case AitaActivatorInitiation:
//			try {
//				// if (command == BotCommand.RantActivatorInitiation)
//				// rest = voting.startRant(this, message, chatId,
//				// campingFromUser);
//				// else if (command == BotCommand.AitaActivatorInitiation)
//				// rest = voting.startAita(this, message, chatId,
//				// campingFromUser);
//				rest = voting.startVoting(command, this, message, chatId, campingFromUser);
//			} catch (VoteCreationFailedException rcfe) {
//				command = BotCommand.VoteInitiationFailed;
//				String reason = rcfe.getMessage();
//				rest = reason;
//				sendMsg(chatId, campingFromUser, reason);
//			}
			result = voting.startVoting(command, this, message, chatId, campingFromUser);
			break;

		case Countdown:
			result = countdownGen.countdownCommand(userMonitor, chatId);
			break;

		case AllNicknames:
			result = allNicknamesCommand();
			break;
		case SetNickname:
			result = setNicknameCommand(campingFromUser, message);

			break;

		case SetNicknameRejected:
			// must be after SetNickname:
			break;
		case Reload:
			result = reloadCommand(campingFromUser);
			break;
		// case Test:
		// rest = testCommand(campingFromUser, chatId);
		// break;
		case UiString:
			break;
		}

		// return rest;
//		if (rest != null)
//			return new TextCommandResult(command, rest);
//		else
		return result;
	}

	private CommandResult spellCommand(CampingUser campingFromUser, Message message)  {
		List<MessageEntity> entities = message.getEntities();
		CampingUser targetUser = findTarget(entities);
		if (targetUser == null) {
			return new TextCommandResult(BotCommand.SpellDipshit, new MentionFragment(campingFromUser),
					new TextFragment(SpellDipshitException.YA_DIPSHIT));
		}

		CommandResult out = new TextCommandResult(BotCommand.Spell, spellGen.cast(targetUser));
		SpellGenerator.countSpellActivation(campingFromUser, targetUser);
		return out;
	}

	private CommandResult reloadCommand(CampingUser fromUser) {
		CommandResult result = new TextCommandResult(BotCommand.Reload).add(fromUser);
		if (system.isAdmin(fromUser)) {
			res.loadAllEmoji();
			serializer.load();
			result.add(": Done!");
		} else {
			result.add(": Access Denied!");
		}
		return result;
	}

	// public String testCommand(CampingUser fromUser, Long chatId) {
	// return "";
	// }

	private CommandResult setNicknameCommand(CampingUser campingFromUser, Message message) {

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
						return new TextCommandResult(BotCommand.SetNicknameRejected).add(campingFromUser).add(": ")
								.add(NicknameRejectedException.CANT_GIVE_YOURSELF_A_NICKNAME);
					} else {
						targetUser.setNickname(newNickname);
						CommandResult sb = new TextCommandResult(BotCommand.SetNickname);
						sb.add(targetUser.getFirstOrUserName());
						sb.add("'s nickname changed to: ");
						sb.add(targetUser);
						return sb;
					}
				}
			} else {
				return new TextCommandResult(BotCommand.SetNicknameRejected).add(campingFromUser).add(": ")
						.add(NicknameRejectedException.USER_NOT_FOUND);
			}
		}
		return new TextCommandResult(BotCommand.SetNicknameRejected).add(campingFromUser).add(": ")
				.add(NicknameRejectedException.INVALID_SYNTAX);
	}

	private CommandResult allNicknamesCommand() {
		CommandResult sb = new TextCommandResult(BotCommand.AllNicknames);
		for (CampingUser u : userMonitor.getUsers()) {
			String first = u.getFirstname();
			String nick = u.getNickname();
			if (CampingUtil.notEmptyOrNull(nick) && CampingUtil.notEmptyOrNull(first)) {
				sb.add("*");
				sb.add(first);
				sb.add("*: ");
				sb.add(nick);
				sb.add("\n");
			}

		}
		return sb;
	}

}