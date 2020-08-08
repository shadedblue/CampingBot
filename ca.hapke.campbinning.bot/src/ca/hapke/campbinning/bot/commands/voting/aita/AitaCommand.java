package ca.hapke.campbinning.bot.commands.voting.aita;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBot;
import ca.hapke.campbinning.bot.CampingSerializable;
import ca.hapke.campbinning.bot.category.CategoriedItems;
import ca.hapke.campbinning.bot.category.HasCategories;
import ca.hapke.campbinning.bot.commands.voting.VoteInitiationException;
import ca.hapke.campbinning.bot.commands.voting.VoteTracker;
import ca.hapke.campbinning.bot.commands.voting.VotingCommand;
import ca.hapke.campbinning.bot.mbiyf.MbiyfCommand;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.xml.OutputFormatter;

/**
 * @author Nathan Hapke
 */
public class AitaCommand extends VotingCommand<Float> implements CampingSerializable, HasCategories<String> {

	static final String AITA = "aita";
	private MbiyfCommand ballsCommand;
	private CategoriedItems<String> resultCategories;
	private boolean shouldSave = false;

	private static final String SOMEONE_ELSE_ACTIVATED = " is the asshole!";

	public AitaCommand(CampingBot campingBot, MbiyfCommand ballsCommand) {
		super(campingBot, BotCommand.AitaActivatorInitiation);
		this.ballsCommand = ballsCommand;
		resultCategories = new CategoriedItems<>(AitaTracker.assholeLevels);
	}

	@Override
	public List<String> getCategoryNames() {
		return resultCategories.getCategoryNames();
	}

	@Override
	public void addItem(String category, String value) {
		resultCategories.put(category, value);
		shouldSave = true;
	}

	@Override
	public boolean shouldSave() {
		return shouldSave;
	}

	@Override
	public String getCommandName() {
		return AITA;
	}

	@Override
	public String getContainerName() {
		return AITA;
	}

	@Override
	public void getXml(OutputFormatter of) {
		String tag = "voting";
		of.start(tag);
		of.tagCategories(resultCategories);
		of.finish(tag);

		shouldSave = false;
	}

	@Override
	protected VoteTracker<Float> initiateVote(CampingUser ranter, CampingUser activater, Long chatId,
			Message activation, Message topic) throws VoteInitiationException, TelegramApiException {
		if (ranter != activater) {
			throw new VoteInitiationException(SOMEONE_ELSE_ACTIVATED);
		} else {
			return new AitaTracker(bot, ranter, chatId, activation, topic, resultCategories, ballsCommand);
		}
	}
}
