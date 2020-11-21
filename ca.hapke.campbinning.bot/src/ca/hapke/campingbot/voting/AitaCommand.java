package ca.hapke.campingbot.voting;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.api.CampingSerializable;
import ca.hapke.campingbot.category.CategoriedItems;
import ca.hapke.campingbot.category.HasCategories;
import ca.hapke.campingbot.commands.MbiyfCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.xml.OutputFormatter;

/**
 * @author Nathan Hapke
 */
public class AitaCommand extends VotingCommand<Float> implements CampingSerializable, HasCategories<String> {

	static final String AITA = "aita";
	private MbiyfCommand ballsCommand;
	private CategoriedItems<String> resultCategories;
	private boolean shouldSave = false;

	private static final String SOMEONE_ELSE_ACTIVATED = " is the asshole!";
	private static final SlashCommandType SlashAitaActivation = new SlashCommandType("AitaActivation", AITA,
			BotCommandIds.AITA | BotCommandIds.VOTING | BotCommandIds.SET);

	public AitaCommand(CampingBot campingBot, MbiyfCommand ballsCommand) {
		super(campingBot, SlashAitaActivation);
		this.ballsCommand = ballsCommand;
		resultCategories = new CategoriedItems<>(AitaTracker.assholeLevels);
	}

	@Override
	public List<String> getCategoryNames() {
		return resultCategories.getCategoryNames();
	}

	@Override
	public List<String> getCategory(String name) {
		return resultCategories.getList(name);
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
