package ca.hapke.campbinning.bot.commands.inline;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import ca.hapke.campbinning.bot.commands.response.MessageProcessor;
import ca.hapke.campbinning.bot.log.EventItem;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.users.CampingUserMonitor;

/**
 * @author Nathan Hapke
 *
 */
public abstract class InlineCommand {

	protected CampingUserMonitor userMonitor = CampingUserMonitor.getInstance();
	public static final String INLINE_DELIMITER = ":";

	public abstract String getCommandName();

	public abstract EventItem chosenInlineQuery(String[] words, CampingUser campingFromUser, Integer inlineMessageId,
			String resultText);

	public abstract InlineQueryResult provideInlineQuery(String input, int updateId, MessageProcessor processor);

	public String createQueryId(int... ids) {
		String[] inputs = new String[ids.length + 1];
		inputs[0] = getCommandName();
		for (int i = 0; i < ids.length; i++) {
			inputs[i + 1] = Integer.toString(ids[i]);
		}

		return String.join(InlineCommand.INLINE_DELIMITER, inputs);
	}

	public String createQueryId(int updateId, List<Integer> convertedIds) {
		int[] inputs = new int[convertedIds.size() + 1];
		inputs[0] = updateId;
		for (int i = 0; i < convertedIds.size(); i++) {
			inputs[i + 1] = convertedIds.get(i);
		}
		return createQueryId(inputs);
	}

	public static InlineKeyboardMarkup createKeyboard(String[] buttons, String[] values) {
		InlineKeyboardMarkup board = new InlineKeyboardMarkup();
		List<InlineKeyboardButton> row = new ArrayList<>();
		for (int i = 0; i < buttons.length; i++) {
			String text = buttons[i];
			String value = values[i];
			row.add(new InlineKeyboardButton(text).setCallbackData(value));
		}
		List<List<InlineKeyboardButton>> fullKeyboard = new ArrayList<List<InlineKeyboardButton>>();
		fullKeyboard.add(row);
		board.setKeyboard(fullKeyboard);
		return board;
	}
}