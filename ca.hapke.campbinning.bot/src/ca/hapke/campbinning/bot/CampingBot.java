package ca.hapke.campbinning.bot;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.calendaring.monitor.CalendarMonitor;
import ca.hapke.campbinning.bot.category.HasCategories;
import ca.hapke.campbinning.bot.commands.CountdownGenerator;
import ca.hapke.campbinning.bot.commands.HypeCommand;
import ca.hapke.campbinning.bot.commands.IunnoCommand;
import ca.hapke.campbinning.bot.commands.MbiyfCommand;
import ca.hapke.campbinning.bot.commands.PartyEverydayCommand;
import ca.hapke.campbinning.bot.commands.PleasureModelCommand;
import ca.hapke.campbinning.bot.commands.SpellGenerator;
import ca.hapke.campbinning.bot.commands.StatusCommand;
import ca.hapke.campbinning.bot.commands.inline.HideItInlineCommand;
import ca.hapke.campbinning.bot.commands.inline.InlineCommandBase;
import ca.hapke.campbinning.bot.commands.inline.NicknameCommand;
import ca.hapke.campbinning.bot.commands.inline.SpellInlineCommand;
import ca.hapke.campbinning.bot.commands.response.CommandResult;
import ca.hapke.campbinning.bot.commands.response.TextCommandResult;
import ca.hapke.campbinning.bot.commands.response.fragments.TextFragment;
import ca.hapke.campbinning.bot.commands.voting.aita.AitaCommand;
import ca.hapke.campbinning.bot.commands.voting.rant.RantCommand;
import ca.hapke.campbinning.bot.log.DatabaseConsumer;
import ca.hapke.campbinning.bot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class CampingBot extends CampingBotEngine {

	public static final String STRING_NULL = "null";

	private Resources res = new Resources();
//	private VotingManager voting;
	private AitaCommand aitaCommand;
	private RantCommand rantCommand;

	private StatusCommand statusCommand;
	private MbiyfCommand ballsCommand;
	private PleasureModelCommand pleasureCommand;
	private IunnoCommand iunnoCommand;
	private PartyEverydayCommand partyCommand;

	private CountdownGenerator countdownGen;
	private HypeCommand hypeCommand;
	private DatabaseConsumer databaseConsumer;
	private SpellGenerator spellCommand;

	private HideItInlineCommand hideItInline;
	private InlineCommandBase spellInline;
	private NicknameCommand nicknameCommand;

	private CampingXmlSerializer serializer;

	private List<HasCategories<String>> hasCategories = new ArrayList<>();

	private CalendarMonitor calMonitor;

//	private AfdTextCommand afdText;
//	private AfdMatrixPictures afdMatrix;
//	private AprilFoolsDayEnabler afdEnabler;

	public CampingBot() {
		statusCommand = new StatusCommand();
		addStatusUpdate(statusCommand);
		spellCommand = new SpellGenerator(this);
		nicknameCommand = new NicknameCommand();
		pleasureCommand = new PleasureModelCommand(this);
		iunnoCommand = new IunnoCommand(this);
		partyCommand = new PartyEverydayCommand(this);
		databaseConsumer = new DatabaseConsumer(system, eventLogger);

		ballsCommand = new MbiyfCommand(this, res);
		rantCommand = new RantCommand(this);
		aitaCommand = new AitaCommand(this, ballsCommand);
		countdownGen = new CountdownGenerator(res, ballsCommand);
		hypeCommand = new HypeCommand(this, countdownGen);

		hideItInline = new HideItInlineCommand(this);
		spellInline = new SpellInlineCommand(spellCommand);

		serializer = new CampingXmlSerializer(system, spellCommand, countdownGen, aitaCommand, partyCommand,
				chatManager, userMonitor);

		res.loadAllEmoji();
		serializer.load();

//		AprilFoolsDayProcessor afdp = new AprilFoolsDayProcessor();
//		afdp.addAtEnd(processor);

//		DarkModeMessageProcessor dmp = new DarkModeMessageProcessor();
//		dmp.addAtEnd(afdp);

//		processor = dmp;

//		CampingChat chat = chatManager.get(system.getAnnounceChat());
//		afdText = new AfdTextCommand(this, afdp, chat);
//		afdMatrix = new AfdMatrixPictures(this, chat);
//		afdEnabler = new AprilFoolsDayEnabler(afdText, afdMatrix, afdp);

		ballsCommand.init();

		addTextCommand(ballsCommand);
		addTextCommand(aitaCommand);
		addTextCommand(rantCommand);
		addTextCommand(pleasureCommand);
		addTextCommand(iunnoCommand);
		addTextCommand(partyCommand);
//		addTextCommand(afdText);

		addInlineCommand(spellInline);
		addInlineCommand(nicknameCommand);
		addInlineCommand(hideItInline);
//		inlineCommands.add(spellInline);
//		inlineCommands.add(nicknameCommand);
//		inlineCommands.add(hideItInline);

		addCallbackCommand(hideItInline);
		addCallbackCommand(aitaCommand);
		addCallbackCommand(rantCommand);

		calMonitor = CalendarMonitor.getInstance();
		calMonitor.add(serializer);
		calMonitor.add(databaseConsumer);
		calMonitor.add(ballsCommand);
		calMonitor.add(aitaCommand);
		calMonitor.add(rantCommand);
//		calMonitor.add(afdMatrix);
//		calMonitor.add(afdEnabler);

//		calMonitor.add(dmp);

		hasCategories.add(spellCommand);
		hasCategories.add(countdownGen);
		hasCategories.add(hypeCommand);
		hasCategories.add(aitaCommand);
		hasCategories.add(partyCommand);
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

	public List<HasCategories<String>> getCategories() {
		return hasCategories;
	}

	@Override
	protected CommandResult reactToSlashCommandInText(BotCommand command, Message message, Long chatId,
			CampingUser campingFromUser) throws TelegramApiException {
		CommandResult result = null;
		switch (command) {
		case NicknameConversion:
		case Mbiyf:
		case MbiyfDipshit:
		case MbiyfAnnouncement:
		case PleasureModel:
		case PartyEveryday:
			// NOOP
			break;
		case VoteTopicComplete:
		case Vote:
		case VoteActivatorComplete:
		case VoteTopicInitiation:
		case VoteInitiationFailed:
		case Talk:
			// NOOP : internal events, not responses
			break;
		case SpellDipshit:
		case SetNicknameRejected:
			// Resultant events. Not Commands
			break;

		case AllBalls:
			result = new TextCommandResult(command, new TextFragment(res.listBalls()));
			break;
		case AllFaces:
			result = new TextCommandResult(command, new TextFragment(res.listFaces()));
			break;
		case IunnoGoogleIt:
			return iunnoCommand.textCommand(campingFromUser, null, chatId, message);
		case Spell:
			result = spellCommand.spellCommand(campingFromUser, message);
			break;

		case RantActivatorInitiation:
			result = rantCommand.startVoting(command, this, message, chatId, campingFromUser);
			break;
		case AitaActivatorInitiation:
			result = aitaCommand.startVoting(command, this, message, chatId, campingFromUser);
			break;

		case Countdown:
			result = countdownGen.countdownCommand(userMonitor, chatId);
			break;

		case Hype:
			result = hypeCommand.hypeCommand(campingFromUser);
			break;

		case AllNicknames:
			result = nicknameCommand.allNicknamesCommand();
			break;
		case SetNickname:
			result = nicknameCommand.setNicknameCommand(campingFromUser, message);
			break;
		case Reload:
			result = reloadCommand(campingFromUser);
			break;
		case UiString:
			break;
		case Status:
			result = statusCommand.statusCommand();
			break;
		default:
			break;
		}

		return result;
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

	public Resources getRes() {
		return res;
	}

}