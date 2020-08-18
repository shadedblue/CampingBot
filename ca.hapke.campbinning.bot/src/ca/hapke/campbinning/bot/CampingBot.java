package ca.hapke.campbinning.bot;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.calendaring.monitor.CalendarMonitor;
import ca.hapke.campbinning.bot.category.HasCategories;
import ca.hapke.campbinning.bot.commands.AbstractCommand;
import ca.hapke.campbinning.bot.commands.CountdownCommand;
import ca.hapke.campbinning.bot.commands.EnhanceCommand;
import ca.hapke.campbinning.bot.commands.HypeCommand;
import ca.hapke.campbinning.bot.commands.IunnoCommand;
import ca.hapke.campbinning.bot.commands.PartyEverydayCommand;
import ca.hapke.campbinning.bot.commands.PleasureModelCommand;
import ca.hapke.campbinning.bot.commands.SpellCommand;
import ca.hapke.campbinning.bot.commands.StatusCommand;
import ca.hapke.campbinning.bot.commands.inline.HideItCommand;
import ca.hapke.campbinning.bot.commands.inline.NicknameCommand;
import ca.hapke.campbinning.bot.commands.voting.VoteManagementCommands;
import ca.hapke.campbinning.bot.commands.voting.aita.AitaCommand;
import ca.hapke.campbinning.bot.commands.voting.rant.RantCommand;
import ca.hapke.campbinning.bot.log.DatabaseConsumer;
import ca.hapke.campbinning.bot.mbiyf.MbiyfCommand;
import ca.hapke.campbinning.bot.response.TextCommandResult;
import ca.hapke.campbinning.bot.response.fragments.TextStyle;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.users.CampingUserMonitor;

/**
 * @author Nathan Hapke
 */
public class CampingBot extends CampingBotEngine {

	private Resources res = new Resources();
	private AitaCommand aitaCommand;
	private RantCommand rantCommand;
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

	private List<HasCategories<String>> hasCategories = new ArrayList<>();

	private CalendarMonitor calMonitor;

	public CampingBot() {
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
		aitaCommand = new AitaCommand(this, ballsCommand);
		voteManagementCommands = new VoteManagementCommands(rantCommand, aitaCommand);
		countdownGen = new CountdownCommand(res, ballsCommand);
		hypeCommand = new HypeCommand(this, countdownGen);

		hideItCommand = new HideItCommand(this, databaseConsumer);

		statusCommand = new StatusCommand(hideItCommand);
		addStatusUpdate(statusCommand);

		serializer = new CampingXmlSerializer(system, spellCommand, countdownGen, aitaCommand, partyCommand,
				chatManager, userMonitor, insultGenerator, enhanceCommand);

		hasCategories.add(insultGenerator);

		addCommand(spellCommand);
		addCommand(nicknameCommand);
		addCommand(hideItCommand);
		addCommand(ballsCommand);
		addCommand(aitaCommand);
		addCommand(rantCommand);
		addCommand(pleasureCommand);
		addCommand(iunnoCommand);
		addCommand(partyCommand);
		addCommand(countdownGen);
		addCommand(enhanceCommand);
		addCommand(hypeCommand);
		addCommand(statusCommand);
		addCommand(voteManagementCommands);
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
				TextCommandResult result = new TextCommandResult(BotCommand.PleasureModel);
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
		calMonitor.add((CampingXmlSerializer) serializer);
		calMonitor.add(databaseConsumer);
		calMonitor.add(ballsCommand);
		calMonitor.add(aitaCommand);
		calMonitor.add(rantCommand);
	}

	public CampingUser getMeCamping() {
		return meCamping;
	}

	public List<HasCategories<String>> getCategories() {
		return hasCategories;
	}

	public Resources getRes() {
		return res;
	}

}