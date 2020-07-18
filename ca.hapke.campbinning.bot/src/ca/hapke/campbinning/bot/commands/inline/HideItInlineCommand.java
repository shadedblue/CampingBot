package ca.hapke.campbinning.bot.commands.inline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	private LoadingCache<String, HiddenText> providedQueries;
	private Map<String, HiddenText> confirmedMessages;
	private LoadingCache<Integer, String> confirmedTopics;
	private CampingBotEngine bot;
	static final Character[] blots = new Character[] { '░', '▀', '█', '▄', '▒', '▙', '▟', '▛', '▜', '▀', '▔', '▖', '▗',
			'▘', '▝' };
	private int nextTopicId = 1;

	public HideItInlineCommand(CampingBotEngine bot) {
		this.bot = bot;

		confirmedTopics = CacheBuilder.newBuilder().expireAfterWrite(48, TimeUnit.HOURS)
				.build(new CacheLoader<Integer, String>() {
					@Override
					public String load(Integer key) throws Exception {
						return null;
					}
				});
		providedQueries = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).maximumSize(1000)
				.build(new CacheLoader<String, HiddenText>() {
					@Override
					public HiddenText load(String key) throws Exception {
						return null;
					}
				});
		confirmedMessages = new HashMap<>(100);
	}

	@Override
	public String getCommandName() {
		return INLINE_HIDE;
	}

	@Override
	public EventItem chosenInlineQuery(Update update, CallbackId id, CampingUser campingFromUser, String resultText) {
		Integer queryId = id.getUpdateId();
		HiddenText details;
		String fullId = id.getResult();
		try {
			details = providedQueries.get(fullId);
		} catch (ExecutionException e) {
			details = null;
		}
		if (details == null)
			return new EventItem("Could not Choose HideIt: " + fullId);

		String topic = details.getTopic();
		if (topic != null) {
			topic = details.getTopic().trim();
			if (!confirmedTopics.asMap().containsValue(topic)) {
				confirmedTopics.put(nextTopicId, topic);
				nextTopicId++;
			}
		}

		confirmedMessages.put(fullId, details);

		EventItem item = new EventItem(BotCommand.HideIt, campingFromUser, null, null, queryId, details.getClearText(),
				null);
		return item;
	}

	@Override
	public List<InlineQueryResult> provideInlineQuery(Update update, String input, int updateId,
			MessageProcessor processor) {
		boolean containsDash = input.contains("-");
		String typedTopic = null, spoiler;
		if (containsDash) {
			String[] topicAndSpoiler = input.split("-");
			typedTopic = topicAndSpoiler[0];
			spoiler = input.substring(typedTopic.length() + 1).trim();
			typedTopic = typedTopic.trim();
		} else {
			spoiler = input;
		}

		int qty = 2 + (int) (confirmedTopics.size());
		List<InlineQueryResult> output = new ArrayList<>(qty);

		createInlineOption(output, updateId, null, input);
		if (containsDash) {
			createInlineOption(output, updateId, typedTopic, spoiler);
		}
		for (String topic : confirmedTopics.asMap().values()) {
			if (topic.equalsIgnoreCase(typedTopic))
				continue;
			createInlineOption(output, updateId, topic, input);
		}

		return output;

	}

	public void createInlineOption(List<InlineQueryResult> output, int updateId, String topic, String textToHide) {
		CallbackId callbackId = new CallbackId(getCommandName(), updateId, output.size());
		String queryId = callbackId.getResult();
		String blotText = createBlotText(textToHide, topic);
		HiddenText item = new HiddenText(topic, textToHide, blotText);
		providedQueries.put(queryId, item);

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
		output.add(article);
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
			out[0] = topic + " -";
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
		String hideId = id.getResult();

		HiddenText details = confirmedMessages.get(hideId);
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

	/**
	 * HACK only for bug-testing
	 */
	@Deprecated
	public Map<String, HiddenText> getConfirmedMessages() {
		return confirmedMessages;
	}

	/**
	 * HACK only for bug-testing
	 */
	@Deprecated
	public LoadingCache<Integer, String> getConfirmedTopics() {
		return confirmedTopics;
	}

}
