package ca.hapke.campbinning.bot;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.calendaring.monitor.CalendarMonitor;
import ca.hapke.campbinning.bot.category.HasCategories;
import ca.hapke.campbinning.bot.commands.CountdownGenerator;
import ca.hapke.campbinning.bot.commands.HypeCommand;
import ca.hapke.campbinning.bot.commands.ImageEnhanceCommand;
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
import ca.hapke.campbinning.bot.commands.response.InsultGenerator;
import ca.hapke.campbinning.bot.commands.response.TextCommandResult;
import ca.hapke.campbinning.bot.commands.response.fragments.TextFragment;
import ca.hapke.campbinning.bot.commands.voting.VoteManagementCommands;
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
	private AitaCommand aitaCommand;
	private RantCommand rantCommand;
	private VoteManagementCommands voteManagementCommands;

	private StatusCommand statusCommand;
	private MbiyfCommand ballsCommand;
	private PleasureModelCommand pleasureCommand;
	private ImageEnhanceCommand enhanceCommand;
	private IunnoCommand iunnoCommand;
	private PartyEverydayCommand partyCommand;

	private CountdownGenerator countdownGen;
	private HypeCommand hypeCommand;
	private DatabaseConsumer databaseConsumer;
	private SpellGenerator spellCommand;

	private HideItInlineCommand hideItInline;
	private InlineCommandBase spellInline;
	private NicknameCommand nicknameCommand;

	private List<HasCategories<String>> hasCategories = new ArrayList<>();

	private CalendarMonitor calMonitor;

	private InsultGenerator insultGenerator;

//	private AfdTextCommand afdText;
//	private AfdMatrixPictures afdMatrix;
//	private AprilFoolsDayEnabler afdEnabler;

	public CampingBot() {
		statusCommand = new StatusCommand();
		addStatusUpdate(statusCommand);
		spellCommand = new SpellGenerator(this);
		nicknameCommand = new NicknameCommand();
		pleasureCommand = new PleasureModelCommand(this);
		enhanceCommand = new ImageEnhanceCommand(this);
		iunnoCommand = new IunnoCommand(this);
		partyCommand = new PartyEverydayCommand(this);
		databaseConsumer = new DatabaseConsumer(system, eventLogger);

		ballsCommand = new MbiyfCommand(this, res);
		rantCommand = new RantCommand(this);
		aitaCommand = new AitaCommand(this, ballsCommand);
		voteManagementCommands = new VoteManagementCommands(rantCommand, aitaCommand);
		countdownGen = new CountdownGenerator(res, ballsCommand);
		hypeCommand = new HypeCommand(this, countdownGen);

		hideItInline = new HideItInlineCommand(this);
		spellInline = new SpellInlineCommand(spellCommand);
		insultGenerator = InsultGenerator.getInstance();
		serializer = new CampingXmlSerializer(system, spellCommand, countdownGen, aitaCommand, partyCommand,
				chatManager, userMonitor, insultGenerator);

//		AprilFoolsDayProcessor afdp = new AprilFoolsDayProcessor();
//		afdp.addAtEnd(processor);

//		DarkModeMessageProcessor dmp = new DarkModeMessageProcessor();
//		dmp.addAtEnd(afdp);

//		processor = dmp;

//		CampingChat chat = chatManager.get(system.getAnnounceChat());
//		afdText = new AfdTextCommand(this, afdp, chat);
//		afdMatrix = new AfdMatrixPictures(this, chat);
//		afdEnabler = new AprilFoolsDayEnabler(afdText, afdMatrix, afdp);

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

		hasCategories.add(spellCommand);
		hasCategories.add(countdownGen);
		hasCategories.add(hypeCommand);
		hasCategories.add(aitaCommand);
		hasCategories.add(partyCommand);
		hasCategories.add(insultGenerator);
	}

	@Override
	protected void postConfigInit() {
		res.loadAllEmoji();
		ballsCommand.init();

		calMonitor = CalendarMonitor.getInstance();
		calMonitor.add((CampingXmlSerializer) serializer);
		calMonitor.add(databaseConsumer);
		calMonitor.add(ballsCommand);
		calMonitor.add(aitaCommand);
		calMonitor.add(rantCommand);
//		calMonitor.add(afdMatrix);
//		calMonitor.add(afdEnabler);

//		calMonitor.add(dmp);
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
		switch (command) {
		case NicknameConversion:
		case Mbiyf:
		case MbiyfDipshit:
		case MbiyfAnnouncement:
		case PleasureModel:
		case PartyEveryday:
		case HideIt:
			// NOOP
			break;
		case VoteTopicComplete:
		case Vote:
		case VoteActivatorComplete:
		case VoteTopicInitiation:
		case VoteCommandFailed:
		case Talk:
		case UiString:
			// NOOP : internal events, not responses
			break;
		case SpellDipshit:
		case SetNicknameRejected:
			// Resultant events. Not Commands
			break;

		case AllBalls:
			return new TextCommandResult(command, new TextFragment(res.listBalls()));

		case AllFaces:
			return new TextCommandResult(command, new TextFragment(res.listFaces()));

		case IunnoGoogleIt:
			return iunnoCommand.textCommand(campingFromUser, null, chatId, message);
		case Spell:
			return spellCommand.spellCommand(campingFromUser, message);

		case RantActivatorInitiation:
			return rantCommand.startVoting(command, this, message, chatId, campingFromUser);

		case AitaActivatorInitiation:
			return aitaCommand.startVoting(command, this, message, chatId, campingFromUser);

		case VoteForceComplete:
			return voteManagementCommands.completeVoting(message, campingFromUser);

		case VoteExtend:
			return voteManagementCommands.extendVoting(message, campingFromUser);

		case Countdown:
			return countdownGen.countdownCommand(userMonitor, chatId);

		case Hype:
			return hypeCommand.hypeCommand(campingFromUser);

		case AllNicknames:
			return nicknameCommand.allNicknamesCommand();

		case SetNickname:
			return nicknameCommand.setNicknameCommand(campingFromUser, message);

		case Reload:
			return reloadCommand(campingFromUser);

		case Status:
			return statusCommand.statusCommand();
		case ImageEnhance:
			return enhanceCommand.enhanceCommand(message);

		}
		return null;

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