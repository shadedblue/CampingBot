package ca.hapke.campbinning.bot.commands.inline;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBotEngine;
import ca.hapke.campbinning.bot.commands.callback.CallbackCommand;
import ca.hapke.campbinning.bot.commands.callback.CallbackId;
import ca.hapke.campbinning.bot.commands.response.MessageProcessor;
import ca.hapke.campbinning.bot.log.EventItem;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.users.CampingUserMonitor;
import ca.hapke.campbinning.bot.util.CampingUtil;

/**
 * @author Nathan Hapke
 */
public class HideItInlineCommand extends InlineCommandBase implements CallbackCommand {

	private static final String SPACE = " ";
	private static final String INLINE_HIDE = "hide";
	// TODO convert to Cache, because it creates a shitload of Updates as you type.
	private LoadingCache<String, HiddenText> clearTextMap;
	private LoadingCache<Integer, String> topicCache;
	private CampingBotEngine bot;
	static final Character[] blots = new Character[] { '░', '▀', '█', '▄', '▒', '▙', '▟', '▛', '▜', '▀', '▔', '▖', '▗',
			'▘', '▝' };
	private int nextTopicId = 1;

	public HideItInlineCommand(CampingBotEngine bot) {
		this.bot = bot;

		topicCache = CacheBuilder.newBuilder().expireAfterWrite(48, TimeUnit.HOURS).maximumSize(10)
				.build(new CacheLoader<Integer, String>() {
					@Override
					public String load(Integer key) throws Exception {
						return null;
					}
				});
		clearTextMap = CacheBuilder.newBuilder().expireAfterWrite(48, TimeUnit.HOURS)
				.build(new CacheLoader<String, HiddenText>() {
					@Override
					public HiddenText load(String key) throws Exception {
						return null;
					}
				});
	}

	@Override
	public String getCommandName() {
		return INLINE_HIDE;
	}

	@Override
	public EventItem chosenInlineQuery(Update update, CallbackId id, CampingUser campingFromUser, String resultText) {

		Integer queryId = id.getUpdateId();
		HiddenText details;
		String fullId = id.getId();
		try {
			details = clearTextMap.get(fullId);
		} catch (ExecutionException e) {
			details = null;
		}
		if (details == null)
			return new EventItem("Could not Choose HideIt: " + fullId);

		String topic = details.getTopic();
		if (topic != null)
			topicCache.put(nextTopicId, topic);

		String text = details.getClearText();
		EventItem item = new EventItem(BotCommand.HideIt, campingFromUser, null, null, queryId, text, null);
		return item;
	}

	@Override
	public InlineQueryResult[] provideInlineQuery(Update update, String input, int updateId,
			MessageProcessor processor) {
		boolean containsDash = input.contains("-");
		int prefixQty = containsDash ? 2 : 1;

		int qty = prefixQty + (int) (topicCache.size());
		InlineQueryResult[] output = new InlineQueryResult[qty];

		output[0] = createInlineOption(updateId, null, input, 0);
		if (containsDash) {
			String[] topicAndSpoiler = input.split("-");
			String t = topicAndSpoiler[0];

			String clearText = input.substring(t.length() + 1).trim();
			t = t.trim();

			output[1] = createInlineOption(updateId, t, clearText, 1);
		}
		int i = prefixQty;
		for (String topic : topicCache.asMap().values()) {
			output[i] = createInlineOption(updateId, topic, input, i);
			i++;
		}

		return output;

	}

	public InlineQueryResultArticle createInlineOption(int updateId, String topic, String textToHide, int i) {
		CallbackId callbackId = new CallbackId(getCommandName(), updateId, i);
		String queryId = callbackId.getId();
		String blotText = createBlotText(textToHide, topic);
		HiddenText item = new HiddenText(topic, textToHide, blotText);
		clearTextMap.put(queryId, item);

		InputTextMessageContent content = new InputTextMessageContent();
		content.setDisableWebPagePreview(true);
		content.setMessageText(blotText);

		InlineQueryResultArticle article = new InlineQueryResultArticle();

		article.setReplyMarkup(InlineCommandBase.createKeyboard(new String[] { "Show" }, new String[] { queryId }));
		String label;
		if (topic != null) {
			label = topic;
		} else {
			label = "No topic";
		}
		article.setTitle("HideIt: " + label);

		article.setId(queryId);
		article.setInputMessageContent(content);
		return article;
	}

	public String createBlotText(String clear, String topic) {
		String[] words = clear.split(SPACE);
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
				word[j] = CampingUtil.getRandom(HideItInlineCommand.blots);
			}
			out[i + adjust] = new String(word);
		}
		return CampingUtil.join(out, SPACE);
	}

	@Override
	public EventItem reactToCallback(CallbackId id, CallbackQuery callbackQuery) {

		String callbackQueryId = callbackQuery.getId();
		String hideId = id.getId();

		HiddenText details;
		try {
			details = clearTextMap.get(hideId);
		} catch (ExecutionException e1) {
			details = null;
		}
		if (details == null) {
			return new EventItem("Could not process HideIt callback: " + callbackQueryId);
		}
		String displayToUser = details.getClearText();

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
