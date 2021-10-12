package ca.hapke.campingbot.commands;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.category.CategoriedStringsPersisted;
import ca.hapke.campingbot.category.HasCategories;
import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.ResponseCommandType;
import ca.hapke.campingbot.commands.api.TextCommand;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class AmpDetectorCommand extends AbstractCommand implements HasCategories<String>, TextCommand {

	private static final String INSULTS = "Insults";
	private static final String AMP_DETECTOR = "AmpDetector";
	public static final ResponseCommandType AmpDetectorCommand = new ResponseCommandType(AMP_DETECTOR,
			BotCommandIds.SILLY_RESPONSE | BotCommandIds.TEXT);
	private Pattern p;
	private CategoriedStringsPersisted categories = new CategoriedStringsPersisted(AMP_DETECTOR, INSULTS);

	private static final String AMP_REGEX = ".*https://www.google.com/amp/.*";

	public AmpDetectorCommand() {
		p = Pattern.compile(AMP_REGEX);
	}

	@Override
	public CommandResult textCommand(CampingUser campingFromUser, List<MessageEntity> entities, Long chatId,
			Message message) throws TelegramApiException {
		TextCommandResult result = new TextCommandResult(AmpDetectorCommand);
		String insult = categories.getRandom(INSULTS);
		result.add(insult);
		return result;
	}

	@Override
	public boolean isMatch(String msg, Message message) {
		String lowerCase = msg.toLowerCase();
		Matcher m = p.matcher(lowerCase);

		return m.matches();
	}

	@Override
	public String getCommandName() {
		return AMP_DETECTOR;
	}

	@Override
	public String provideUiStatus() {
		return null;
	}

	@Override
	public List<String> getCategoryNames() {
		return categories.getCategoryNames();
	}

	@Override
	public void addItem(String category, String value) {
		categories.put(category, value);
	}

	@Override
	public String getContainerName() {
		return AMP_DETECTOR;
	}

	@Override
	public int getSize(String s) {
		return categories.getSize(s);
	}

}
