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
import ca.hapke.campingbot.response.EditCaptionCommandResult;
import ca.hapke.campingbot.response.ImageCommandResult;
import ca.hapke.campingbot.response.SendResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.fragments.TextStyle;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.users.CampingUserMonitor;
import ca.hapke.campingbot.util.ImageLink;
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

	// TODO should be a Map based on channel
	private Message bannerMessage;
	private List<CampingChat> allowedChats;

	private final CampingChatManager chatMonitor;
	private final CampingUserMonitor userMonitor;
	private int roundNumber = 0;
	private AfdPlayerManager playerManager;
	private ImageLink noChance = AfdImagesStage.getAybImgUrl("sr", 1);

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
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, Long chatId,
			CampingUser campingFromUser) throws TelegramApiException {
		// SAFETY FOR TESTING
		if (!chatAllowed(chatId))
			return null;

		if (command == SlashPotato && bannerMessage == null) {
			CampingChat chat = CampingChatManager.getInstance(bot).get(chatId);
			List<CommandResult> results = beginRound(Collections.singletonList(chat));
			CommandResult result = results.get(0);
			return result;
		} else if (command == SlashResult) {
			CommandResult result = finishRound();

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

	public List<CommandResult> beginRound(List<CampingChat> chats) throws TelegramApiException {
		List<CommandResult> out = new ArrayList<>();
		roundNumber++;
		for (CampingChat chat : chats) {
//			TextCommandResult result = new TextCommandResult(HotPotatoCommand);
			ImageCommandResult result = new ImageCommandResult(HotPotatoCommand, noChance);
//			firstPerson = CollectionUtil.getRandom(targets);
//			result.add(firstPerson, CaseChoice.Upper);
			addRoundNumber(result);
			result.setKeyboard(createVotingKeyboard());

			SendResult sent = result.send(bot, chat.chatId);
			bannerMessage = sent.outgoingMsg;
			out.add(result);
		}
		return out;

	}

	protected void addRoundNumber(CommandResult result) {
		result.add("ROUND ", TextStyle.Bold);
		result.add(roundNumber, TextStyle.Bold);
		result.add("\n");
		result.add(playerManager.getTargets().size());
		result.add(" PLAYERS REMAIN!");
	}

	public TextCommandResult finishRound() {
		TextCommandResult result = new TextCommandResult(HotPotatoCommand);

		List<CampingUser> targets = playerManager.getTargets();
		Map<CampingUser, Integer> nextChoice = new HashMap<>(targets.size());
		for (CampingUser target : targets) {
			nextChoice.put(target, 0);
		}

		CampingUser target = CollectionUtil.getRandom(targets);

		int tossesLeft = (int) (Math.random() * targets.size() * MAX_TOSSES);
//		int i = 0;
		while (true) {

			result.add(tossesLeft);
			result.add(" left: ");
			result.add(target);
			int index = nextChoice.get(target);
			List<CampingUser> votes = playerManager.getVotes(target);
			CampingUser nextTarget = null;

			boolean boom;
			if (tossesLeft <= 0 || index >= MAX_TOSSES) {
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

				try {
					EditCaptionCommandResult editBanner = new EditCaptionCommandResult(HotPotatoCommand, bannerMessage);
					addRoundNumber(editBanner);
					editBanner.sendAndLog(bot, null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				result.add(" GOT BLOWED UP!");
				playerManager.advance(target);
//				targets.remove(target);
//				userToVotesMap.clear();
				bannerMessage = null;

				if (targets.size() == 1) {
					winner(targets.get(0));
				} else {
					advanceToNextRound(target);
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

			tossesLeft--;
		}
		return result;
	}

	private void advanceToNextRound(CampingUser killed) {
		AybBetweenRoundsImages betweenRounds = new AybBetweenRoundsImages(bot, killed);
		betweenRounds.add(new StageListener() {

			@Override
			public void stageComplete(boolean success) {
				try {
					beginRound(allowedChats);
				} catch (TelegramApiException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void stageBegan() {
			}
		});
		betweenRounds.begin();
	}

	private void winner(CampingUser target) {
		fullGameStage.complete(true);
	}

	private boolean chatAllowed(Long chatId) {
		for (CampingChat cc : allowedChats) {
			if (cc.chatId == chatId)
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
			CallbackId id = new CallbackId(POTATO, roundNumber, user.getTelegramId());
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
