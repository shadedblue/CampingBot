package ca.hapke.campingbot.vote2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.callback.api.CallbackCommand;
import ca.hapke.campingbot.callback.api.CallbackId;
import ca.hapke.campingbot.channels.CampingChat;
import ca.hapke.campingbot.channels.CampingChatManager;
import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.ResponseCommandType;
import ca.hapke.campingbot.commands.api.SlashCommand;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.log.EventItem;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.EditTextCommandResult;
import ca.hapke.campingbot.response.SendResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.users.CampingUserMonitor;

/**
 * @author Nathan Hapke
 */
public class TestVote2Command extends AbstractCommand implements CallbackCommand, SlashCommand {

	private static final String TEST = "test";
	private static final SlashCommandType SlashTest = new SlashCommandType("TestVote", TEST,
			BotCommandIds.VOTING | BotCommandIds.SET);
	private SlashCommandType[] SLASH_COMMANDS = new SlashCommandType[] { SlashTest };
	public static final ResponseCommandType TestAddLineCommand = new ResponseCommandType("TestVoteAddLine",
			BotCommandIds.INLINE | BotCommandIds.TEXT | BotCommandIds.SET);

	private CampingBot campingBot;

	private Map<Integer, List<Message>> topicToResultant = new HashMap<>();
//	private Map<Integer, Message> indexToResultantMap = new HashMap<>();
	private Map<Integer, List<String>> topicToLine2 = new HashMap<>();

	private static final String[] shortButtons = new String[] { "A", "B", "C" };

	public TestVote2Command(CampingBot campingBot) {
		this.campingBot = campingBot;
	}

	@Override
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, Long chatId,
			CampingUser campingFromUser) throws TelegramApiException {
		Message replyTo = message.getReplyToMessage();
		if (replyTo == null)
			return null;
		int updateId = replyTo.getMessageId();
		TextCommandResult result = new TextCommandResult(command);
		String line2 = ">";
		List<String> line2s = softCreate(updateId, topicToLine2);
		int index = line2s.size();
		line2s.add(line2);

		String msg = createMsg(updateId, index, line2);
		result.add(msg);
		InlineKeyboardMarkup keyboard = generateKeyboard(command, updateId, index);
		result.setKeyboard(keyboard);

		SendResult sent = result.send(campingBot, chatId);
		List<Message> resultants = getResultantList(updateId);
		resultants.add(sent.outgoingMsg);

//		indexToResultantMap.put(index, sent.outgoingMsg);
		return result;
	}

	@Override
	public EventItem reactToCallback(CallbackId id, CallbackQuery callbackQuery) {
		int topicId = id.getUpdateId();
		int[] ids = id.getIds();
		int index = ids[0];
		int j = ids[1];
		List<String> line2s = softCreate(topicId, topicToLine2);
		List<Message> messages = softCreate(topicId, topicToResultant);
		String line2 = line2s.get(index);
		line2 += shortButtons[j];
		line2s.set(index, line2);

		String text = createMsg(topicId, index, line2);
		Message message = messages.get(index);

		EditTextCommandResult edit = new EditTextCommandResult(TestAddLineCommand, message);
		edit.add(text);
		InlineKeyboardMarkup keyboard = generateKeyboard(SlashTest, topicId, index);
		edit.setKeyboard(keyboard);
		try {
			Long chatId = message.getChatId();
			SendResult result = edit.send(campingBot, chatId);

			CampingUser user = CampingUserMonitor.getInstance().getUser(callbackQuery.getFrom());
			CampingChat chat = CampingChatManager.getInstance(campingBot).get(chatId);
			return new EventItem(TestAddLineCommand, user, chat, result);
		} catch (TelegramApiException e) {
			return new EventItem(e.getLocalizedMessage());
		}
	}

	protected String createMsg(int updateId, int index, String line2) {
		return "Go! [" + updateId + "-" + index + "]\n" + line2;
	}

	protected InlineKeyboardMarkup generateKeyboard(SlashCommandType command, int updateId, int index) {
		String[] buttonCallbackIds = new String[shortButtons.length];
		for (int j = 0; j < shortButtons.length; j++) {
			CallbackId id = new CallbackId(command.slashCommand, updateId, index, j);
			buttonCallbackIds[j] = id.getResult();
		}
		InlineKeyboardMarkup keyboard = AbstractCommand.createKeyboard(shortButtons, buttonCallbackIds);
		return keyboard;
	}

	private List<Message> getResultantList(int updateId) {
		Map<Integer, List<Message>> list = topicToResultant;
		return softCreate(updateId, list);
	}

	protected static <T> List<T> softCreate(int key, Map<Integer, List<T>> list) {
		List<T> lst = list.get(key);
		if (lst == null) {
			lst = new ArrayList<T>();
			list.put(key, lst);
		}
		return lst;
	}

	@Override
	public SlashCommandType[] getSlashCommandsToRespondTo() {
		return SLASH_COMMANDS;
	}

	@Override
	public String getCommandName() {
		return TEST;
	}
}
