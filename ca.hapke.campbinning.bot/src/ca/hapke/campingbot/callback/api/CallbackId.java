package ca.hapke.campingbot.callback.api;

import java.util.List;

import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.util.StringUtil;

/**
 * @author Nathan Hapke
 */
public class CallbackId {
	private final String command;
	private final int updateId;
	private final int[] ids;
	private String result;

	public CallbackId(String command, int updateId, List<Integer> ids) {
		this(command, updateId, unbox(ids));
	}

	private static int[] unbox(List<Integer> input) {
		int[] output = new int[input.size()];

		for (int j = 0; j < input.size(); j++) {
			int k = input.get(j);
			output[j] = k;

		}
		return output;
	}

	public CallbackId(String command, int updateId, int... ids) {
		this.command = command;
		this.updateId = updateId;
		this.ids = ids;
		result = createId();
	}

	public static CallbackId fromString(String input) {
		String[] words = input.split(AbstractCommand.DELIMITER);
		if (words.length <= 1)
			return null;

		String command = words[0];
		int messageId = Integer.parseInt(words[1]);
		int[] ids = new int[words.length - 2];
		for (int i = 2; i < words.length; i++) {
			String s = words[i];
			ids[i - 2] = Integer.parseInt(s);
		}
		return new CallbackId(command, messageId, ids);
	}

	private String createId() {
		String[] inputs = new String[ids.length + 2];
		inputs[0] = command;
		inputs[1] = Integer.toString(updateId);
		for (int i = 0; i < ids.length; i++) {
			inputs[i + 2] = Integer.toString(ids[i]);
		}
		return StringUtil.join(inputs, AbstractCommand.DELIMITER);
	}

	public String getResult() {
		return result;
	}

	public String getCommand() {
		return command;
	}

	public int getUpdateId() {
		return updateId;
	}

	public int[] getIds() {
		return ids;
	}

}
