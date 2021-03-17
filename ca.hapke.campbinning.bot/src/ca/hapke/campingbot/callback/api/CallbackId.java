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
	private final long[] ids;
	private String result;

	public CallbackId(String command, int updateId, List<Long> ids) {
		this(command, updateId, unbox(ids));
	}

	private static long[] unbox(List<Long> input) {
		long[] output = new long[input.size()];

		for (int j = 0; j < input.size(); j++) {
			long k = input.get(j);
			output[j] = k;

		}
		return output;
	}

	public CallbackId(String command, int updateId, long... ids) {
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
		long[] ids = new long[words.length - 2];
		for (int i = 2; i < words.length; i++) {
			String s = words[i];
			ids[i - 2] = Long.parseLong(s);
		}
		return new CallbackId(command, messageId, ids);
	}

	private String createId() {
		String[] inputs = new String[ids.length + 2];
		inputs[0] = command;
		inputs[1] = Integer.toString(updateId);
		for (int i = 0; i < ids.length; i++) {
			inputs[i + 2] = Long.toString(ids[i]);
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

	public long[] getIds() {
		return ids;
	}

}
