package ca.hapke.campingbot.commands.api;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import ca.hapke.campingbot.AccessLevel;
import ca.hapke.campingbot.callback.api.CallbackId;

/**
 * @author Nathan Hapke
 */
public abstract class AbstractCommand {

	public static final String DELIMITER = ":";

	public abstract String getCommandName();

	public static InlineKeyboardMarkup createKeyboard(String[] buttons, String[] values) {
		InlineKeyboardMarkup board = new InlineKeyboardMarkup();
		List<InlineKeyboardButton> row = new ArrayList<>();

		List<List<InlineKeyboardButton>> fullKeyboard = new ArrayList<List<InlineKeyboardButton>>();

		int across = 0;
		for (int i = 0; i < buttons.length; i++) {
			String text = buttons[i];
			String value = values[i];
			InlineKeyboardButton b = new InlineKeyboardButton(text);
			b.setCallbackData(value);
			row.add(b);
			across++;
			if (across >= 5) {
				across = 0;
				fullKeyboard.add(row);
				row = new ArrayList<>();
			}
		}
		fullKeyboard.add(row);
		board.setKeyboard(fullKeyboard);
		return board;
	}

	public static InlineKeyboardMarkup createKeyboard(String[][] buttonsByRow, String[][] valuesByRow) {
		InlineKeyboardMarkup board = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> fullKeyboard = new ArrayList<List<InlineKeyboardButton>>();

		for (int j = 0; j < buttonsByRow.length && j < valuesByRow.length; j++) {
			List<InlineKeyboardButton> row = new ArrayList<>();
			String[] buttons = buttonsByRow[j];
			String[] values = valuesByRow[j];
			for (int i = 0; i < buttons.length && i < values.length; i++) {
				String text = buttons[i];
				String value = values[i];
				InlineKeyboardButton b = new InlineKeyboardButton(text);
				b.setCallbackData(value);
				row.add(b);
			}

			fullKeyboard.add(row);
		}
		board.setKeyboard(fullKeyboard);
		return board;
	}

	public CallbackId createQueryId(int updateId, long... ids) {
		return new CallbackId(getCommandName(), updateId, ids);
	}

	public CallbackId createQueryId(int updateId, List<Long> ids) {
		return new CallbackId(getCommandName(), updateId, ids);
	}

	/**
	 * Default provided for subclasses that implement {@link SlashCommand}
	 */
	public AccessLevel accessRequired() {
		return AccessLevel.User;
	}

}