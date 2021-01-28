package ca.hapke.campingbot.afd2021;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
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
import ca.hapke.campingbot.response.SendResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.fragments.CaseChoice;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.users.CampingUserMonitor;
import ca.hapke.util.CollectionUtil;
import ca.hapke.util.StringUtil;

/**
 * Callback format for ids: (1) telegramId they're voting for.
 * 
 * @author Nathan Hapke
 */
public class AfdHotPotato extends AbstractCommand implements CallbackCommand, SlashCommand {

	private static final String POTATO = "potato";
	private static final String HOT_POTATO = "HotPotato";
	// TODO Remove, only for testing
	public static final SlashCommandType SlashPotato = new SlashCommandType(HOT_POTATO, POTATO,
			BotCommandIds.SILLY_RESPONSE | BotCommandIds.VOTING | BotCommandIds.USE);
	private static final SlashCommandType[] SLASH_COMMANDS = new SlashCommandType[] { SlashPotato };

	@Override
	public SlashCommandType[] getSlashCommandsToRespondTo() {
		return SLASH_COMMANDS;
	}

	public static final ResponseCommandType HotPotatoCommand = new ResponseCommandType(HOT_POTATO,
			BotCommandIds.SILLY_RESPONSE | BotCommandIds.VOTING | BotCommandIds.USE);
	private static final int MAX_TOSSES = 5;
	private CampingBot bot;
//	private CampingChat chat;
	private Map<Integer, List<Integer>> userToVotesMap = new HashMap<>();
	private List<CampingUser> targets;
	private Map<CampingUser, String> initialsMap = new HashMap<>();
	private Message bannerMessage;
	private List<CampingChat> allowedChats;

	private final CampingUserMonitor userMonitor = CampingUserMonitor.getInstance();
	private final CampingChatManager chatMonitor;
	private int nextVotingId = 0;

	public AfdHotPotato(CampingBot bot) {
		this.bot = bot;
		chatMonitor = CampingChatManager.getInstance(bot);
		allowedChats = chatMonitor.getAnnounceChats();
	}

	public void init() {
		targets = new ArrayList<CampingUser>();

		CampingUser user = userMonitor.getUser(554436051);
		initialsMap.put(user, "NH");
		targets.add(user);

		CampingUser user2 = userMonitor.getUser(763960317);
		initialsMap.put(user2, "RH");
		targets.add(user2);

		CampingUser user3 = userMonitor.getUser(1053967313);
		initialsMap.put(user3, "CDB");
		targets.add(user3);
	}

	private List<Integer> getVotes(int user) {
		List<Integer> votes = userToVotesMap.get(user);
		if (votes == null) {
			votes = new ArrayList<Integer>(MAX_TOSSES);
			userToVotesMap.put(user, votes);
		}
		return votes;
	}

	@Override
	public EventItem reactToCallback(CallbackId id, CallbackQuery callbackQuery) {
		CampingUser user = userMonitor.monitor(callbackQuery.getFrom());
		int fromUser = user.getTelegramId();

		List<Integer> votes = getVotes(fromUser);
		int n = votes.size();
		String resultText;
		if (n >= MAX_TOSSES) {
			resultText = "Done!";
		} else {
			int[] ids = id.getIds();
			int targetId = ids[0];
			votes.add(targetId);
			resultText = StringUtil.ordinal(n) + " choice is: ";
		}

//		id.g
		AnswerCallbackQuery answer = new AnswerCallbackQuery();
		answer.setText(resultText);
		answer.setCallbackQueryId(callbackQuery.getId());
		try {
			bot.execute(answer);
			String display = initialsMap.get(user);
			return new EventItem(HotPotatoCommand, user, null, chatMonitor.get(bannerMessage.getChat().getId()),
					bannerMessage.getMessageId(), display, null);
		} catch (Exception e) {
			return new EventItem(e.getLocalizedMessage());
		}
	}

	@Override
	public String getCommandName() {
		return POTATO;
	}

	@Override
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, Long chatId,
			CampingUser campingFromUser) throws TelegramApiException {
		// SAFETY FOR TESTING
		if (!chatAllowed(chatId))
			return null;
		nextVotingId++;
		TextCommandResult result = new TextCommandResult(HotPotatoCommand);
		CampingUser firstPerson = CollectionUtil.getRandom(targets);
		result.add(firstPerson, CaseChoice.Upper);
		result.add(": YOU HAVE NO CHANCE TO SURVIVE... MAKE YOUR TIME.");
		result.setKeyboard(createVotingKeyboard());

		SendResult sent = result.send(bot, chatId);
		bannerMessage = sent.outgoingMsg;
		return result;
	}

	private boolean chatAllowed(Long chatId) {
		for (CampingChat campingChat : allowedChats) {
			if (campingChat.getChatId() == chatId)
				return true;
		}
		return false;
	}

	private ReplyKeyboard createVotingKeyboard() {
		int n = targets.size();
		String[] buttons = new String[n];
		String[] values = new String[n];
		for (int i = 0; i < n; i++) {
			CampingUser user = targets.get(i);
			buttons[i] = initialsMap.get(user);
			CallbackId id = new CallbackId(POTATO, nextVotingId, user.getTelegramId());
			values[i] = id.getResult();
		}
		return createKeyboard(buttons, values);
	}

}
