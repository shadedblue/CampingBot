package ca.hapke.campbinning.bot.commands;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import ca.hapke.campbinning.bot.AccessLevel;
import ca.hapke.campbinning.bot.commands.callback.CallbackId;

/**
 * @author Nathan Hapke
 */
public abstract class AbstractCommand {

	public static final String DELIMITER = ":";

	public abstract String getCommandName();

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

	public CallbackId createQueryId(int updateId, int... ids) {
		return new CallbackId(getCommandName(), updateId, ids);
	}

	public CallbackId createQueryId(int updateId, List<Integer> ids) {
		return new CallbackId(getCommandName(), updateId, ids);
	}

	/**
	 * Default provided for subclasses that implement {@link SlashCommand}
	 */
	public AccessLevel accessRequired() {
		return AccessLevel.User;
	}
}