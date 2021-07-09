package ca.hapke.campingbot;

import java.security.ProtectionDomain;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.calendaring.event.CalendaredEvent;
import ca.hapke.calendaring.monitor.CalendarMonitor;
import ca.hapke.campingbot.afd2020.AprilFoolsDayEnabler;
import ca.hapke.campingbot.afd2021.AfdHotPotato;
import ca.hapke.campingbot.api.CampingBotEngine;
import ca.hapke.campingbot.category.HasCategories;
import ca.hapke.campingbot.commands.CountdownCommand;
import ca.hapke.campingbot.commands.EnhanceCommand;
import ca.hapke.campingbot.commands.FuckMeCommand;
import ca.hapke.campingbot.commands.HypeCommand;
import ca.hapke.campingbot.commands.IunnoCommand;
import ca.hapke.campingbot.commands.MbiyfCommand;
import ca.hapke.campingbot.commands.PartyEverydayCommand;
import ca.hapke.campingbot.commands.PleasureModelCommand;
import ca.hapke.campingbot.commands.QuantityCommand;
import ca.hapke.campingbot.commands.RedditCommand;
import ca.hapke.campingbot.commands.SetInitialsCommand;
import ca.hapke.campingbot.commands.StatusCommand;
import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.ResponseCommandType;
import ca.hapke.campingbot.commands.inline.HideItCommand;
import ca.hapke.campingbot.commands.inline.NicknameCommand;
import ca.hapke.campingbot.commands.spell.SpellCommand;
import ca.hapke.campingbot.events.BallBustingEvent;
import ca.hapke.campingbot.events.HappyNewYearEvent;
import ca.hapke.campingbot.response.InsultGenerator;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.fragments.TextStyle;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.users.CampingUserMonitor;
import ca.hapke.campingbot.voting.RantCommand;
import ca.hapke.campingbot.voting.VoteManagementCommands;
import ca.hapke.campingbot.voting.ufc.UfcCommand;

/**
 * @author Nathan Hapke
 */
public class CampingBot extends CampingBotEngine {
	private Resources res = new Resources();
	private RantCommand rantCommand;
	private UfcCommand ufcCommand;
	private VoteManagementCommands voteManagementCommands;

	private StatusCommand statusCommand;
	private MbiyfCommand ballsCommand;
	private PleasureModelCommand pleasureCommand;
	private FuckMeCommand fuCommand;
	private EnhanceCommand enhanceCommand;
	private IunnoCommand iunnoCommand;
	private PartyEverydayCommand partyCommand;

	private CountdownCommand countdownGen;
	private HypeCommand hypeCommand;
	private SpellCommand spellCommand;
	private HideItCommand hideItCommand;
	private NicknameCommand nicknameCommand;
	private SetInitialsCommand initialsCommand;
	private RedditCommand redditCommand;
	private QuantityCommand quantityCommand;

	private CalendarMonitor calMonitor;
	private AprilFoolsDayEnabler afdEnabler;

	private BallBustingEvent ballBustingEvent;
	private HappyNewYearEvent happyNewYearEvent;

	public static final ResponseCommandType TalkCommand = new ResponseCommandType("Talk",
			BotCommandIds.REGULAR_CHAT | BotCommandIds.TEXT | BotCommandIds.USE);
	public static final ResponseCommandType LogStringCommand = new ResponseCommandType("LogString", 0);
	private AfdHotPotato potatoCommand;

	public CampingBot(ProtectionDomain protectionDomain) {
		super(protectionDomain);
		spellCommand = new SpellCommand(this);
		nicknameCommand = new NicknameCommand();
		initialsCommand = new SetInitialsCommand();
		pleasureCommand = new PleasureModelCommand(this);
		fuCommand = new FuckMeCommand(this);
		enhanceCommand = new EnhanceCommand(this);
		iunnoCommand = new IunnoCommand(this);
		partyCommand = new PartyEverydayCommand(this);

		ballsCommand = new MbiyfCommand(this, res);
		processor.addAtEnd(ballsCommand.getCrazyCase());

		rantCommand = new RantCommand(this);
		ufcCommand = new UfcCommand(this, res);
		voteManagementCommands = new VoteManagementCommands(rantCommand);

		countdownGen = new CountdownCommand(res, ballsCommand);
		hypeCommand = new HypeCommand(this);
		potatoCommand = new AfdHotPotato(this, res);
		afdEnabler = new AprilFoolsDayEnabler(this, potatoCommand);

		redditCommand = new RedditCommand();
		hideItCommand = new HideItCommand(this, databaseConsumer);
		statusCommand = new StatusCommand(hideItCommand);

		happyNewYearEvent = new HappyNewYearEvent(this);
		ballBustingEvent = new BallBustingEvent(this);

		addStatusUpdate(statusCommand);

		InsultGenerator insultGenerator = InsultGenerator.getInstance();
//		ContentSerializer contentSerializer = new ContentSerializer(protectionDomain, spellCommand, hypeCommand,
//				partyCommand, insultGenerator, enhanceCommand);
//		contentSerializer.load();

		hasCategories.add(insultGenerator);
		quantityCommand = new QuantityCommand(hasCategories);
	}

	@Override
	protected void addCommandsAndEvents() {
		addCommand(quantityCommand);
		addCommand(spellCommand);
		addCommand(nicknameCommand);
		addCommand(initialsCommand);
		addCommand(hideItCommand);
		addCommand(ballsCommand);
		addCommand(ufcCommand);
		addCommand(rantCommand);
		addCommand(pleasureCommand);
		addCommand(fuCommand);
		addCommand(iunnoCommand);
		addCommand(partyCommand);
		addCommand(countdownGen);
		addCommand(enhanceCommand);
		addCommand(hypeCommand);
		addCommand(statusCommand);
		addCommand(voteManagementCommands);
		addCommand(redditCommand);
		addCommand(potatoCommand);

		addEvent(afdEnabler);
		addEvent(databaseConsumer);
		addEvent(happyNewYearEvent);
		addEvent(ballBustingEvent);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addCommand(AbstractCommand command) {
		super.addCommand(command);
		if (command instanceof HasCategories<?>) {
			try {
				hasCategories.add((HasCategories<String>) command);
			} catch (Exception e) {
				// Ignore if it's not a <String>
			}
		}

		if (command instanceof CalendaredEvent<?>) {
			addEvent((CalendaredEvent<?>) command);
		}
	}

	protected void addEvent(CalendaredEvent<?> hasCal) {
		if (this.calMonitor == null)
			calMonitor = CalendarMonitor.getInstance();
		calMonitor.add(hasCal);
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
		super.postConfigInit();
		calMonitor.start();
	}

	public Resources getRes() {
		return res;
	}

}