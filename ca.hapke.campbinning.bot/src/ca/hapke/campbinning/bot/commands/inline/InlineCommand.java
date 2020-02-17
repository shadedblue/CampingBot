package ca.hapke.campbinning.bot.commands.inline;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;

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

	public abstract EventItem chosenInlineQuery(String[] words, CampingUser campingFromUser, Integer inlineMessageId);

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

}