package ca.hapke.campbinning.bot;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.calendaring.monitor.CalendarMonitor;
import ca.hapke.campbinning.bot.category.HasCategories;
import ca.hapke.campbinning.bot.commands.CountdownGenerator;
import ca.hapke.campbinning.bot.commands.IunnoCommand;
import ca.hapke.campbinning.bot.commands.MbiyfCommand;
import ca.hapke.campbinning.bot.commands.PartyEverydayCommand;
import ca.hapke.campbinning.bot.commands.PleasureModelCommand;
import ca.hapke.campbinning.bot.commands.SpellGenerator;
import ca.hapke.campbinning.bot.commands.inline.InlineCommand;
import ca.hapke.campbinning.bot.commands.inline.NicknameCommand;
import ca.hapke.campbinning.bot.commands.inline.SpellInlineCommand;
import ca.hapke.campbinning.bot.commands.response.CommandResult;
import ca.hapke.campbinning.bot.commands.response.TextCommandResult;
import ca.hapke.campbinning.bot.commands.response.fragments.TextFragment;
import ca.hapke.campbinning.bot.commands.voting.VotingManager;
import ca.hapke.campbinning.bot.log.DatabaseConsumer;
import ca.hapke.campbinning.bot.users.CampingUser;

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

	private NicknameCommand nicknameCommand;
	private InlineCommand spellInline;

	private CampingXmlSerializer serializer;

	private List<HasCategories<String>> hasCategories = new ArrayList<>();

	private CalendarMonitor calMonitor;

	public CampingBot() {
		nicknameCommand = new NicknameCommand();
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
		inlineCommands.add(nicknameCommand);
		callbackCommands.add(voting);

		calMonitor = CalendarMonitor.getInstance();
		calMonitor.add(serializer);
		calMonitor.add(databaseConsumer);
		calMonitor.add(ballsCommand);
		calMonitor.add(voting);

		hasCategories.add(spellGen);
		hasCategories.add(countdownGen);
		hasCategories.add(voting);
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
			result = spellGen.spellCommand(campingFromUser, findTarget(message.getEntities()), message);
			break;

		case RantActivatorInitiation:
		case AitaActivatorInitiation:
			result = voting.startVoting(command, this, message, chatId, campingFromUser);
			break;

		case Countdown:
			result = countdownGen.countdownCommand(userMonitor, chatId);
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

}