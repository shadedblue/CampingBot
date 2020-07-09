package ca.hapke.campbinning.bot.commands;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

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

//	public String createQueryId(int... ids) {
//		String[] inputs = new String[ids.length + 1];
//		inputs[0] = getCommandName();
//		for (int i = 0; i < ids.length; i++) {
//			inputs[i + 1] = Integer.toString(ids[i]);
//		}
//
//		return String.join(DELIMITER, inputs);
//	}

//	public String createQueryId(int updateId, List<Integer> convertedIds) {
//		int[] inputs = new int[convertedIds.size() + 1];
//		inputs[0] = updateId;
//		for (int i = 0; i < convertedIds.size(); i++) {
//			inputs[i + 1] = convertedIds.get(i);
//		}
//		return createQueryId(inputs);
//	}

}