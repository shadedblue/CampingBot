package ca.hapke.campbinning.bot.commands.inline;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBotEngine;
import ca.hapke.campbinning.bot.log.EventItem;
import ca.hapke.campbinning.bot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class NicknameConversionCommand extends InlineCommand {

	private static final String INLINE_NICKS = "nicks";
	@Override
	public InlineQueryResult provideInlineQuery(String input, int updateId) {
		String[] words = input.split(" ");
		String[] out = new String[words.length];
		String converted = null;
		List<Integer> convertedIds = new ArrayList<>();
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			String newWord = word;
			if (word.length() > 0 && word.charAt(0) == '@') {
				CampingUser cu = userMonitor.getUser(word);
				if (cu != null) {
					newWord = cu.target();
					String firstOrUser = cu.getFirstOrUserName();
					if (converted == null) {
						converted = firstOrUser;
					} else {
						converted = converted + ", " + firstOrUser;
					}
					convertedIds.add(cu.getTelegramId());
				}

			}
			out[i] = newWord;
		}
		String output = String.join(" ", out);



		InputTextMessageContent mc = new InputTextMessageContent();
		mc.setDisableWebPagePreview(true);
		mc.setMessageText(output);
		mc.setParseMode(CampingBotEngine.MARKDOWN);

		InlineQueryResultArticle articleUsernameConversion = new InlineQueryResultArticle();
		if (converted == null)
			converted = "None";
		articleUsernameConversion.setTitle("@usernames converted: " + converted);
		articleUsernameConversion.setId(createQueryId(updateId, convertedIds));
		articleUsernameConversion.setInputMessageContent(mc);

		return articleUsernameConversion;
	}

	@Override
	public EventItem chosenInlineQuery(String[] words, CampingUser campingFromUser, Integer inlineMessageId) {
		if (words.length < 2)
			return null;

		String[] targets = new String[words.length - 2];
		for (int i = 0; i < targets.length; i++) {
			CampingUser target = userMonitor.getUser(Integer.parseInt(words[i + 2]));
			targets[i] = target.getFirstOrUserName();
		}

		String rest = String.join(", ", targets);
		EventItem event = new EventItem(BotCommand.NicknameConversion, campingFromUser, null, null, inlineMessageId,
				rest,
				targets.length);
		return event;
	}

	@Override
	public String getCommandName() {
		return INLINE_NICKS;
	}
}
