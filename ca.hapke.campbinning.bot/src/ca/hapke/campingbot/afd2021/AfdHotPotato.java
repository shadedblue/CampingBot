package ca.hapke.campingbot.afd2021;

import java.util.ArrayList;
import java.util.Collections;
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
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.users.CampingUserMonitor;
import ca.hapke.util.CollectionUtil;
import ca.hapke.util.StringUtil;

/**
 * Callback format for ids: (1) telegramId they're voting for.
 * 
 * @author Nathan Hapke
 */
public class AfdHotPotato extends AbstractCommand implements CallbackCommand, SlashCommand, IStage {

	private static final String POTATO = "potato";
	private static final String HOT_POTATO = "HotPotato";
	private static final String RESULT = "result";
	private static final String HOT_POTATO_RESULT = "HotPotatoResult";
	// TODO Remove, only for testing
	public static final SlashCommandType SlashPotato = new SlashCommandType(HOT_POTATO, POTATO,
			BotCommandIds.SILLY_RESPONSE | BotCommandIds.VOTING | BotCommandIds.USE);
	public static final SlashCommandType SlashResult = new SlashCommandType(HOT_POTATO_RESULT, RESULT,
			BotCommandIds.SILLY_RESPONSE | BotCommandIds.VOTING | BotCommandIds.FINISH);
	private static final SlashCommandType[] SLASH_COMMANDS = new SlashCommandType[] { SlashPotato, SlashResult };

	@Override
	public SlashCommandType[] getSlashCommandsToRespondTo() {
		return SLASH_COMMANDS;
	}

	public static final ResponseCommandType HotPotatoCommand = new ResponseCommandType(HOT_POTATO,
			BotCommandIds.SILLY_RESPONSE | BotCommandIds.VOTING | BotCommandIds.USE);
	static final int MAX_TOSSES = 5;
	private CampingBot bot;

	private Message bannerMessage;
	private List<CampingChat> allowedChats;

	private final CampingChatManager chatMonitor;
	private final CampingUserMonitor userMonitor;
	private int nextVotingId = 0;
	private AfdPlayerManager playerManager;

	public AfdHotPotato(CampingBot bot) {
		this.bot = bot;
		this.playerManager = new AfdPlayerManager();
		userMonitor = CampingUserMonitor.getInstance();
		chatMonitor = CampingChatManager.getInstance(bot);
		allowedChats = chatMonitor.getAnnounceChats();
	}

	public void init() {
		// TODO add all users
		playerManager.add(554436051, "NH");
		playerManager.add(763960317, "RH");
		playerManager.add(1053967313, "CDB");
	}

	@Override
	public EventItem reactToCallback(CallbackId id, CallbackQuery callbackQuery) {
		CampingUser user = userMonitor.monitor(callbackQuery.getFrom());
		int fromUserId = user.getTelegramId();

		List<CampingUser> votes = playerManager.getVotes(fromUserId);
		int n = votes.size();
		String resultText;
		if (n >= MAX_TOSSES) {
			resultText = "Done!";
		} else {
			int[] ids = id.getIds();
			int targetId = ids[0];
			CampingUser votedFor = userMonitor.getUser(targetId);
			votes.add(votedFor);
			n++;
			resultText = StringUtil.ordinal(n) + " choice is: " + playerManager.getInitials(votedFor);
		}

		AnswerCallbackQuery answer = new AnswerCallbackQuery();
		answer.setText(resultText);
		answer.setCallbackQueryId(callbackQuery.getId());
		try {
			bot.execute(answer);
			return new EventItem(HotPotatoCommand, user, null, chatMonitor.get(bannerMessage.getChat().getId()),
					bannerMessage.getMessageId(), resultText, null);
		} catch (Exception e) {
			return new EventItem(e.getLocalizedMessage());
		}
	}

	@Override
	public String getCommandName() {
		return POTATO;
	}

	@Override
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, CampingChat chat,
			CampingUser campingFromUser) throws TelegramApiException {
		// SAFETY FOR TESTING
		if (!chatAllowed(chat))
			return null;

		if (command == SlashPotato && bannerMessage == null) {
			List<TextCommandResult> results = beginRound(Collections.singletonList(chat));
			TextCommandResult result = results.get(0);
			return result;
		} else if (command == SlashResult) {
			TextCommandResult result = finishRound();

			return result;
		} else {
			return null;
		}
	}

	@Override
	public void begin() {
		try {
			beginRound(allowedChats);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	public List<TextCommandResult> beginRound(List<CampingChat> chats) throws TelegramApiException {
		List<TextCommandResult> out = new ArrayList<>();
		nextVotingId++;
		for (CampingChat chat : chats) {
			TextCommandResult result = new TextCommandResult(HotPotatoCommand);
//			firstPerson = CollectionUtil.getRandom(targets);
//			result.add(firstPerson, CaseChoice.Upper);
			result.add("YOU HAVE NO CHANCE TO SURVIVE... MAKE YOUR TIME.");
			result.setKeyboard(createVotingKeyboard());

			SendResult sent = result.send(bot, chat.chatId);
			bannerMessage = sent.outgoingMsg;
			out.add(result);
		}
		return out;

	}

	public TextCommandResult finishRound() {
		TextCommandResult result = new TextCommandResult(HotPotatoCommand);

		List<CampingUser> targets = playerManager.getTargets();
		Map<CampingUser, Integer> nextChoice = new HashMap<>(targets.size());
		for (CampingUser target : targets) {
			nextChoice.put(target, 0);
		}

		CampingUser target = CollectionUtil.getRandom(targets);

		int potatoLimit = (int) (Math.random() * targets.size() * MAX_TOSSES);
		int i = 0;
		while (true) {
			result.add(target);
			int index = nextChoice.get(target);
			List<CampingUser> votes = playerManager.getVotes(target);
			CampingUser nextTarget = null;

			boolean boom;
			if (i >= potatoLimit || index >= MAX_TOSSES) {
				boom = true;
			} else {
				try {
					nextTarget = votes.get(index);
					boom = false;
				} catch (Exception e) {
					boom = true;
				}
			}
			if (boom || nextTarget == null) {
				result.add(" GOT BLOWED UP!");
				playerManager.advance(target);
//				targets.remove(target);
//				userToVotesMap.clear();
				bannerMessage = null;

				if (targets.size() == 1) {
					winner(targets.get(0));
				} else {
					startNextRound();
				}
				break;
			} else {
				result.add(" chose ");
				result.add(nextTarget);
				result.newLine();
				result.newLine();
				nextChoice.put(target, index + 1);
				target = nextTarget;
			}

			i++;
		}
		return result;
	}

	private void startNextRound() {

	}

	private void winner(CampingUser target) {
		fullGameStage.complete(true);
	}

	private boolean chatAllowed(CampingChat chat) {
		for (CampingChat cc : allowedChats) {
			if (cc == chat)
				return true;
		}
		return false;
	}

	private ReplyKeyboard createVotingKeyboard() {
		List<CampingUser> targets = playerManager.getTargets();

		int n = targets.size();
		String[] buttons = new String[n];
		String[] values = new String[n];
		for (int i = 0; i < n; i++) {
			CampingUser user = targets.get(i);
			buttons[i] = playerManager.getInitials(user);
			CallbackId id = new CallbackId(POTATO, nextVotingId, user.getTelegramId());
			values[i] = id.getResult();
		}
		return createKeyboard(buttons, values);
	}

	/**
	 * Just to solve Multiple Inheritance
	 */
	private class HotPotatoStage extends Stage {
		@Override
		public void begin2() {
			AfdHotPotato.this.begin();
		}

		@Override
		protected void complete2(boolean success) {

		}

	}

	private HotPotatoStage fullGameStage = new HotPotatoStage();

	@Override
	public boolean add(StageListener e) {
		return fullGameStage.add(e);
	}

	@Override
	public boolean remove(StageListener e) {
		return fullGameStage.remove(e);
	}

}
