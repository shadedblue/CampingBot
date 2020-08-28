package ca.hapke.campbinning.bot.commands.inline;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;

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

import ca.hapke.campbinning.bot.CampingBotEngine;
import ca.hapke.campbinning.bot.commands.api.BotCommandIds;
import ca.hapke.campbinning.bot.commands.api.ResponseCommandType;
import ca.hapke.campbinning.bot.commands.callback.CallbackCommand;
import ca.hapke.campbinning.bot.commands.callback.CallbackId;
import ca.hapke.campbinning.bot.log.DatabaseConsumer;
import ca.hapke.campbinning.bot.log.EventItem;
import ca.hapke.campbinning.bot.log.EventLogger;
import ca.hapke.campbinning.bot.processors.MessageProcessor;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.users.CampingUserMonitor;
import ca.hapke.campbinning.bot.util.CampingUtil;

/**
 * @author Nathan Hapke
 */
public class HideItCommand extends InlineCommandBase implements CallbackCommand {
	public static final ResponseCommandType HideItSendCommand = new ResponseCommandType("HideItSend",
			BotCommandIds.INLINE | BotCommandIds.TEXT | BotCommandIds.SET);
	public static final ResponseCommandType HideItRevealCommand = new ResponseCommandType("HideItReveal",
			BotCommandIds.INLINE | BotCommandIds.TEXT | BotCommandIds.USE);

	private static final String SPACE = " ";
	private static final String INLINE_HIDE = "hide";
	private LoadingCache<String, HiddenText> providedQueries;
	private LoadingCache<Integer, HideItMessage> confirmedCache;
	private LoadingCache<Integer, String> confirmedTopics;
	private CampingBotEngine bot;
	static final Character[] blots = new Character[] { '░', '▀', '█', '▄', '▒', '▙', '▟', '▛', '▜', '▀', '▔', '▖', '▗',
			'▘', '▝' };
	private int nextTopicId = 1;
	private DatabaseConsumer db;

	public HideItCommand(CampingBotEngine bot, DatabaseConsumer db) {
		this.bot = bot;
		this.db = db;

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
		confirmedCache = CacheBuilder.newBuilder().expireAfterWrite(48, TimeUnit.HOURS)
				.build(new CacheLoader<Integer, HideItMessage>() {
					@Override
					public HideItMessage load(Integer key) throws Exception {
						EntityManager manager = db.getManager();
						HideItMessage hm = manager.find(HideItMessage.class, key);
						return hm;
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
		HideItMessage msg = new HideItMessage(queryId, details.getClearText());
		add(msg);

		EventItem item = new EventItem(HideItSendCommand, campingFromUser, null, null, queryId, details.getClearText(),
				null);
		return item;
	}

	private void add(HideItMessage msg) {
		confirmedCache.put(msg.getMessageId(), msg);

		try {
			EntityManager manager = db.getManager();
			manager.getTransaction().begin();
			manager.persist(msg);
			manager.getTransaction().commit();
		} catch (Exception e) {
			EventLogger.getInstance().add(new EventItem("Could not save HideIt" + e.getLocalizedMessage()));
		}
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

		boolean providePartial = spoiler.contains("*");

		int qty = 2 + (providePartial ? 2 : 1) * (int) (confirmedTopics.size());
		List<InlineQueryResult> output = new ArrayList<>(qty);

		output.add(createInlineOption(0, updateId, null, input, false));
		if (providePartial) {
			output.add(createInlineOption(1, updateId, null, input, true));
		}
		if (containsDash) {
			output.add(createInlineOption(output.size(), updateId, typedTopic, spoiler, false));
			if (providePartial) {
				output.add(createInlineOption(output.size(), updateId, typedTopic, spoiler, true));
			}
		}
		for (String topic : confirmedTopics.asMap().values()) {
			if (topic.equalsIgnoreCase(typedTopic))
				continue;
			output.add(createInlineOption(output.size(), updateId, topic, input, false));
			if (providePartial) {
				output.add(createInlineOption(output.size(), updateId, topic, input, true));
			}
		}

//		System.out.println(input);
//		for (InlineQueryResult x : output) {
//			System.out.println(x);
//		}
//		System.out.println("----------");
		return output;

	}

	public InlineQueryResultArticle createInlineOption(int i, int updateId, String topic, String textToHide,
			boolean partial) {
		CallbackId callbackId = new CallbackId(getCommandName(), updateId, i);
		String queryId = callbackId.getResult();
		String blotText = createBlotText(textToHide, topic, partial);
		if (partial)
			textToHide = removeStars(textToHide);
		HiddenText item = new HiddenText(topic, textToHide, blotText);
		providedQueries.put(queryId, item);

		InputTextMessageContent content = new InputTextMessageContent();
		content.setDisableWebPagePreview(true);
		content.setMessageText(blotText);

		InlineQueryResultArticle article = new InlineQueryResultArticle();

		article.setReplyMarkup(InlineCommandBase.createKeyboard(new String[] { "Show" }, new String[] { queryId }));
		String label;
		if (topic != null) {
			label = blotText;
		} else {
			label = "(No topic) " + blotText;
		}
		article.setTitle("HideIt: " + label);

		article.setId(queryId);
		article.setInputMessageContent(content);
		return article;
	}

	private String removeStars(String textToHide) {
		int length = textToHide.length();
		StringBuilder sb = new StringBuilder(length);
		for (int j = 0; j < length; j++) {
			char c = textToHide.charAt(j);
			if (c != '*') {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public String createBlotText(String clear, String topic, boolean partial) {
		boolean partialHiding = false;
		String[] inputWords = clear.split(SPACE);
		int outLen = inputWords.length;

		int adjust = 0;
		if (topic != null) {
			adjust = 1;
		}
		String[] out = new String[outLen + adjust];

		if (topic != null) {
			out[0] = topic + " -";
		}
		for (int i = 0; i < outLen; i++) {
			int length = inputWords[i].length();
			char[] word = new char[length];
			for (int j = 0, k = 0; j < length; j++) {
				char c = inputWords[i].charAt(j);
				if (partial && c == '*') {
					partialHiding = !partialHiding;
				} else {
					if (partial && !partialHiding) {
						word[k] = c;
					} else {
						word[k] = CampingUtil.getRandom(HideItCommand.blots);
					}
					k++;
				}
			}
			out[i + adjust] = new String(word);
		}
		return CampingUtil.join(out, SPACE);
	}

	@Override
	public EventItem reactToCallback(CallbackId id, CallbackQuery callbackQuery) {
		String callbackQueryId = callbackQuery.getId();

		HideItMessage details = null;
		try {
			details = confirmedCache.get(id.getUpdateId());
		} catch (Exception e1) {
			return new EventItem(
					"Could not process HideIt callback: " + callbackQueryId + " : " + e1.getLocalizedMessage());
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
			return new EventItem(HideItRevealCommand, user, null, null, null, displayToUser, null);
		} catch (Exception e) {
			return new EventItem(e.getLocalizedMessage());
		}
	}

	public Map<Integer, HideItMessage> getConfirmedMessages() {
		return confirmedCache.asMap();
	}

	public LoadingCache<Integer, String> getConfirmedTopics() {
		return confirmedTopics;
	}
}
