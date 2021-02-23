package ca.hapke.campingbot;

import java.security.ProtectionDomain;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.calendaring.monitor.CalendarMonitor;
import ca.hapke.campingbot.api.CampingBotEngine;
import ca.hapke.campingbot.category.HasCategories;
import ca.hapke.campingbot.commands.CountdownCommand;
import ca.hapke.campingbot.commands.EnhanceCommand;
import ca.hapke.campingbot.commands.HypeCommand;
import ca.hapke.campingbot.commands.IunnoCommand;
import ca.hapke.campingbot.commands.MbiyfCommand;
import ca.hapke.campingbot.commands.PartyEverydayCommand;
import ca.hapke.campingbot.commands.PleasureModelCommand;
import ca.hapke.campingbot.commands.QuantityCommand;
import ca.hapke.campingbot.commands.RedditCommand;
import ca.hapke.campingbot.commands.SpellCommand;
import ca.hapke.campingbot.commands.StatusCommand;
import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.ResponseCommandType;
import ca.hapke.campingbot.commands.inline.HideItCommand;
import ca.hapke.campingbot.commands.inline.NicknameCommand;
import ca.hapke.campingbot.log.DatabaseConsumer;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.fragments.TextStyle;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.users.CampingUserMonitor;
import ca.hapke.campingbot.voting.RantCommand;
import ca.hapke.campingbot.voting.UfcCommand;
import ca.hapke.campingbot.voting.VoteManagementCommands;

/**
 * @author Nathan Hapke
 */
public class CampingBot extends CampingBotEngine {

	private Resources res = new Resources();
	private RantCommand rantCommand;
//	private TestVote2Command testCommand;
	private UfcCommand ufcCommand;
	private VoteManagementCommands voteManagementCommands;

	private StatusCommand statusCommand;
	private MbiyfCommand ballsCommand;
	private PleasureModelCommand pleasureCommand;
	private EnhanceCommand enhanceCommand;
	private IunnoCommand iunnoCommand;
	private PartyEverydayCommand partyCommand;

	private CountdownCommand countdownGen;
	private HypeCommand hypeCommand;
	private DatabaseConsumer databaseConsumer;
	private SpellCommand spellCommand;
	private HideItCommand hideItCommand;
	private NicknameCommand nicknameCommand;

	private CalendarMonitor calMonitor;
	private RedditCommand redditCommand;

	public static final ResponseCommandType TalkCommand = new ResponseCommandType("Talk",
			BotCommandIds.REGULAR_CHAT | BotCommandIds.TEXT | BotCommandIds.USE);
	public static final ResponseCommandType LogStringCommand = new ResponseCommandType("LogString", 0);

	public CampingBot(ProtectionDomain protectionDomain) {
		spellCommand = new SpellCommand(this);
		nicknameCommand = new NicknameCommand();
		pleasureCommand = new PleasureModelCommand(this);
		enhanceCommand = new EnhanceCommand(this);
		iunnoCommand = new IunnoCommand(this);
		partyCommand = new PartyEverydayCommand(this);
		databaseConsumer = new DatabaseConsumer(system, eventLogger);

		ballsCommand = new MbiyfCommand(this, res);
		processor.addAtEnd(ballsCommand.getCrazyCase());
		rantCommand = new RantCommand(this);
		ufcCommand = new UfcCommand(this, res);
		voteManagementCommands = new VoteManagementCommands(rantCommand/* , ufcCommand */);
		countdownGen = new CountdownCommand(res, ballsCommand);
		hypeCommand = new HypeCommand(this);
		redditCommand = new RedditCommand();

		hideItCommand = new HideItCommand(this, databaseConsumer);

		statusCommand = new StatusCommand(hideItCommand);
		addStatusUpdate(statusCommand);

		serializer = new ConfigXmlSerializer(protectionDomain, system, spellCommand, hypeCommand, partyCommand,
				chatManager, userMonitor, insultGenerator, enhanceCommand);

		hasCategories.add(insultGenerator);
		QuantityCommand quantityCommand = new QuantityCommand(hasCategories);
		addCommand(quantityCommand);
		addCommand(spellCommand);
		addCommand(nicknameCommand);
		addCommand(hideItCommand);
		addCommand(ballsCommand);
		addCommand(rantCommand);
		addCommand(ufcCommand);
		addCommand(pleasureCommand);
		addCommand(iunnoCommand);
		addCommand(partyCommand);
		addCommand(countdownGen);
		addCommand(enhanceCommand);
		addCommand(hypeCommand);
		addCommand(statusCommand);
		addCommand(voteManagementCommands);
		addCommand(redditCommand);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addCommand(AbstractCommand command) {
		super.addCommand(command);
		if (command instanceof HasCategories) {
			try {
				hasCategories.add((HasCategories<String>) command);
			} catch (Exception e) {
				// Ignore if it's not a <String>
			}
		}
	}

	@Override
	public void onUpdateReceived(Update update) {
		super.onUpdateReceived(update);
		Message message = update.getMessage();
		if (message != null) {
			int messageId = message.getMessageId();
			if (messageId % 25000 == 0) {
				User from = message.getFrom();
				CampingUser cu = CampingUserMonitor.getInstance().getUser(from);
				TextCommandResult result = new TextCommandResult(PleasureModelCommand.PleasureModelCommand);
				result.add("DING DING DING! We have a winner!", TextStyle.Underline);
				result.add("\nMessage #");
				result.add("" + messageId, TextStyle.Bold);
				result.add(" sent by ");
				result.add(cu);
				result.add("!");
				try {
					result.send(this, message.getChatId());
				} catch (TelegramApiException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void postConfigInit() {
		res.loadAllEmoji();
		ballsCommand.init();

		calMonitor = CalendarMonitor.getInstance();
		calMonitor.add((ConfigXmlSerializer) serializer);
		calMonitor.add(databaseConsumer);
		calMonitor.add(ballsCommand);
		calMonitor.add(rantCommand);
	}

	public Resources getRes() {
		return res;
	}

}