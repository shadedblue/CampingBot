package ca.hapke.campbinning.bot.commands.inline;

import java.util.HashMap;
import java.util.Map;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBotEngine;
import ca.hapke.campbinning.bot.commands.CallbackCommand;
import ca.hapke.campbinning.bot.commands.response.MessageProcessor;
import ca.hapke.campbinning.bot.log.EventItem;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.users.CampingUserMonitor;
import ca.hapke.campbinning.bot.util.CampingUtil;

/**
 * @author Nathan Hapke
 */
public class HideItInlineCommand extends InlineCommand implements CallbackCommand {

	private static final String SPACE = " ";
//	private static final String UNSPECIFIED = "unspecified";
	private static final String INLINE_HIDE = "hide";
	private Map<String, String> resultMap = new HashMap<>();
	private int nextTextId = 1;
	private CampingBotEngine bot;
	private static final Character[] blots = new Character[] { '░', '▀', '█', '▄', '▒' };

	public HideItInlineCommand(CampingBotEngine bot) {
		this.bot = bot;
	}

	@Override
	public String getCommandName() {
		return INLINE_HIDE;
	}

	@Override
	public EventItem chosenInlineQuery(String[] words, CampingUser campingFromUser, Integer inlineMessageId,
			String resultText) {
		if (words.length < 2)
			return null;

		String queryId = words[1];
		String text = resultMap.get(queryId);

		EventItem item = new EventItem(BotCommand.HideIt, campingFromUser, null, null, inlineMessageId, text, queryId);
		return item;
	}

	@Override
	public InlineQueryResult provideInlineQuery(String input, int updateId, MessageProcessor processor) {
		String topic, rest;
		boolean hasTopic = input.contains("-");
		if (hasTopic) {
			String[] words = input.split("-");
			topic = words[0];
			rest = input.substring(topic.length() + 1).trim();
			topic = topic.trim();
//			System.out.println(rest);
		} else {
			topic = null;
			rest = input;
		}

		InputTextMessageContent content = new InputTextMessageContent();

		content.setDisableWebPagePreview(true);
//		String spell = processor.process(outputSpell);
		String hideText = blot(topic, rest);
		content.setMessageText(hideText);
//		content.setParseMode(CampingBotEngine.MARKDOWN);

		InlineQueryResultArticle article = new InlineQueryResultArticle();

		int id = nextTextId;
		nextTextId++;

		String queryId = createQueryId(updateId, id);
		article.setReplyMarkup(InlineCommand.createKeyboard(new String[] { "Show" }, new String[] { queryId }));
//		if (hasTopic) {
//		
//		}else {
		article.setTitle("HideIt: " + hideText);
//		}

		resultMap.put(queryId, rest);
		article.setId(queryId);
		article.setInputMessageContent(content);
		return article;
	}

	private String blot(String topic, String rest) {
		String[] words = rest.split(SPACE);
		int outLen = words.length;

		int adjust = 0;
		if (topic != null) {
			adjust = 1;
		}
		String[] out = new String[outLen + adjust];

		if (topic != null) {
			out[0] = "[" + topic + "]";
		}
		for (int i = 0; i < outLen; i++) {
			int length = words[i].length();
			char[] word = new char[length];
			for (int j = 0; j < length; j++) {
				word[j] = CampingUtil.getRandom(blots);
			}
			out[i + adjust] = new String(word);
//			out[i] = CampingUtil.repeat('Z', length);
		}
		return CampingUtil.join(out, SPACE);
	}

	@Override
	public EventItem reactToCallback(CallbackQuery callbackQuery) {

		String callbackQueryId = callbackQuery.getId();
		String hideId = callbackQuery.getData();
		String displayToUser = resultMap.get(hideId);

		AnswerCallbackQuery answer = new AnswerCallbackQuery();
		answer.setText(displayToUser);
		answer.setCallbackQueryId(callbackQueryId);
		answer.setShowAlert(true);
		try {
			bot.execute(answer);

			User fromUser = callbackQuery.getFrom();
			CampingUser user = CampingUserMonitor.getInstance().monitor(fromUser);
			return new EventItem(BotCommand.HideIt, user, null, null, null, displayToUser, null);
		} catch (Exception e) {
			return new EventItem(e.getLocalizedMessage());
		}
	}

}
