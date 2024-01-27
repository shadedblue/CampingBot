package ca.hapke.campingbot.afd2021;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.calendaring.event.CalendaredEvent;
import ca.hapke.calendaring.event.StartupMode;
import ca.hapke.calendaring.monitor.CalendarMonitor;
import ca.hapke.calendaring.timing.ByCalendar;
import ca.hapke.calendaring.timing.TimesProvider;
import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.Resources;
import ca.hapke.campingbot.afd2020.AprilFoolsDayEnabler;
import ca.hapke.campingbot.api.PostConfigInit;
import ca.hapke.campingbot.callback.api.CallbackCommand;
import ca.hapke.campingbot.callback.api.CallbackId;
import ca.hapke.campingbot.channels.CampingChat;
import ca.hapke.campingbot.channels.CampingChatManager;
import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.ResponseCommandType;
import ca.hapke.campingbot.log.EventItem;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.EditCaptionCommandResult;
import ca.hapke.campingbot.response.ImageCommandResult;
import ca.hapke.campingbot.response.SendResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.fragments.MentionFragment;
import ca.hapke.campingbot.response.fragments.ResultFragment;
import ca.hapke.campingbot.response.fragments.TextFragment;
import ca.hapke.campingbot.response.fragments.TextStyle;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.users.CampingUserMonitor;
import ca.hapke.campingbot.util.ImageLink;
import ca.hapke.campingbot.util.StagedJob;
import ca.hapke.util.CollectionUtil;
import ca.hapke.util.StringUtil;

/*-
 * Callback format for ids: 
 * (0) chatId they're voting inside of 
 * (1) telegramId they're voting for.
 * 
 * @author Nathan Hapke
 */
public class AfdHotPotato extends AbstractCommand
		implements CallbackCommand, IStage, CalendaredEvent<Void>, PostConfigInit {
	private static final String POTATO = "potato";
	private static final String HOT_POTATO = "HotPotato";

	public static final ResponseCommandType HotPotatoCommand = new ResponseCommandType(HOT_POTATO,
			BotCommandIds.SILLY_RESPONSE | BotCommandIds.VOTING | BotCommandIds.USE);
	static final int MAX_TOSSES = 5;

	private CampingBot bot;

	private Map<Long, Message> bannerMessageByChatId = new HashMap<>();
	private List<CampingChat> allowedChats;

	private final CampingChatManager chatMonitor;
	private final CampingUserMonitor userMonitor;
	private int roundNumber = 0;
	private AfdPlayerManager playerManager;
	private ImageLink noChance = AfdImagesStage.getAybImgUrl("sr", 1);

	private TimesProvider<Void> times = new TimesProvider<>();
	private boolean calendarActivated = false;
	private Resources res;
	private AybTopicChanger topicChanger;

	public AfdHotPotato(CampingBot bot, Resources res) {
		this.bot = bot;
		this.res = res;
		this.playerManager = new AfdPlayerManager();
		userMonitor = CampingUserMonitor.getInstance();
		chatMonitor = CampingChatManager.getInstance(bot);
		allowedChats = chatMonitor.getAnnounceChats();
		topicChanger = new AybTopicChanger(bot, res);
	}

	@Override
	public void init() {
		playerManager.add(642767839, "AA");
		playerManager.add(898821867, "DM");
		playerManager.add(708570894, "JA");
		playerManager.add(768167311, "JB");
		playerManager.add(696411365, "JM");

		playerManager.add(943017286, "KA");
		playerManager.add(554436051, "NH");
		playerManager.add(763960317, "RH");
		playerManager.add(720319686, "RS");
		playerManager.add(558638791, "RTV");
	}

	@Override
	public EventItem reactToCallback(CallbackId id, CallbackQuery callbackQuery) {
		CampingUser user = userMonitor.monitor(callbackQuery.getFrom());
		long fromUserId = user.getTelegramId();

		List<CampingUser> votes = playerManager.getVotes(fromUserId);
		int n = votes.size();
		String resultText;

		long[] ids = id.getIds();
		long chatId = ids[0];
		long targetId = ids[1];

		if (n >= MAX_TOSSES) {
			resultText = "NO MORE THROWS OF THE BOMB!";
		} else {
			CampingUser votedFor = userMonitor.getUser(targetId);
			votes.add(votedFor);
			n++;
			resultText = StringUtil.ordinal(n).toUpperCase() + " THROW GO TO " + playerManager.getInitials(votedFor);
		}

		AnswerCallbackQuery answer = new AnswerCallbackQuery();
		answer.setText(resultText);
		answer.setCallbackQueryId(callbackQuery.getId());
		try {
			bot.execute(answer);
			Message bannerMessage = bannerMessageByChatId.get(chatId);
			return new EventItem(HotPotatoCommand, user, null, chatMonitor.get(chatId), bannerMessage.getMessageId(),
					resultText, null);
		} catch (Exception e) {
			return new EventItem(e.getLocalizedMessage());
		}
	}

	@Override
	public String getCommandName() {
		return POTATO;
	}

	@Override
	public void doWork(ByCalendar<Void> timingEvent, Void value) {
		for (CampingChat chat : allowedChats) {
			finishRound(chat.getChatId());
		}
	}

	@Override
	public void begin() {
		try {
			beginRound(allowedChats);
			if (!calendarActivated) {
				calendarActivated = true;
				times.add(AprilFoolsDayEnabler.ROUND_LENGTH);
			}
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	public void beginRound(List<CampingChat> chats) throws TelegramApiException {
		roundNumber++;
		Consumer<CampingChat> changer = topicChanger.createTopicChanger();

		for (CampingChat chat : chats) {
			ImageCommandResult result = new ImageCommandResult(HotPotatoCommand, noChance);
			addRoundNumber(result);
			long chatId = chat.getChatId();
			result.setKeyboard(createVotingKeyboard(chatId));

			SendResult sent = result.sendAndLog(bot, chat);
			Message bannerMessage = sent.outgoingMsg;
			bannerMessageByChatId.put(chatId, bannerMessage);

			// --------
			changer.accept(chat);
		}

	}

	protected void addRoundNumber(CommandResult result) {
		result.add("HOT POTATO BASE-GAME -- ROUND ", TextStyle.Bold);
		result.add(roundNumber, TextStyle.Bold);
		result.add("\n");
		result.add(playerManager.getTargets().size());
		result.add(" PLAYERS ALIVE!");
		result.add(ResultFragment.NEWLINE);
		result.add("WHO YOU WANT SELECT FOR SET UP THROW THE BOMB?");
	}

	public TextCommandResult finishRound(Long chatId) {
		List<List<ResultFragment>> fragStages = new ArrayList<>();

		List<CampingUser> targets = playerManager.getTargets();
		Map<CampingUser, Integer> nextChoice = new HashMap<>(targets.size());
		for (CampingUser target : targets) {
			nextChoice.put(target, 0);
		}

		CampingUser target = CollectionUtil.getRandom(targets);

		int tossesLeft = (int) (Math.random() * targets.size() * MAX_TOSSES);
		List<ResultFragment> stage = new ArrayList<>();
		stage.add(new TextFragment("WAR WAS BEGINNING!\n"));
		stage.add(new TextFragment("THIS POTATO-BOMB MAY ZIG " + tossesLeft + " TIMES\n"));
		stage.add(ResultFragment.NEWLINE);
		fragStages.add(stage);

		AybBetweenRoundsImages betweenRounds = null;

		while (true) {
			stage = new ArrayList<>();
			stage.add(new TextFragment("" + tossesLeft));
			stage.add(new TextFragment(" zigs left: "));

			stage.add(new MentionFragment(target));
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
					Message bannerMessage = bannerMessageByChatId.get(chatId);
					EditCaptionCommandResult editBanner = new EditCaptionCommandResult(HotPotatoCommand, bannerMessage);
					addRoundNumber(editBanner);
					editBanner.sendAndLog(bot, null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				stage.add(new TextFragment(" ...", TextStyle.Bold));
				playerManager.advance(target);
				bannerMessageByChatId.remove(chatId);

				betweenRounds = createImagesBetweenStages(targets, target);
				fragStages.add(stage);
				break;
			} else {
				stage.add(new TextFragment(" set up "));
				stage.add(new MentionFragment(nextTarget));
				stage.add(new TextFragment(" the bomb!\n"));
				nextChoice.put(target, index + 1);
				target = nextTarget;
			}

			tossesLeft--;
			fragStages.add(stage);
		}

		HotPotatoRevealJobDetails details = new HotPotatoRevealJobDetails(bot, chatId, fragStages, betweenRounds);
		StagedJob<HotPotatoRevealJobDetails> job = new StagedJob<HotPotatoRevealJobDetails>(details);
		job.start();
		return null;
	}

	protected AybBetweenRoundsImages createImagesBetweenStages(List<CampingUser> remainingInGame, CampingUser target) {
		AybBetweenRoundsImages betweenRounds;
		betweenRounds = new AybBetweenRoundsImages(bot, target);
		if (remainingInGame.size() == 1) {
			CampingUser winner = remainingInGame.get(0);
			fullGameStage.complete(true);
			betweenRounds.add(new StageListener() {
				@Override
				public void stageBegan() {
				}

				@Override
				public void stageComplete(boolean success) {
					times.clear();
					calendarActivated = false;
					CalendarMonitor.getInstance().remove(AfdHotPotato.this);
					AybEndGameImages endImages = new AybEndGameImages(bot, winner);
					endImages.begin();
				}
			});
		} else {
			betweenRounds.add(new StageListener() {
				@Override
				public void stageBegan() {
				}

				@Override
				public void stageComplete(boolean success) {
					try {
						beginRound(allowedChats);
					} catch (TelegramApiException e) {
						e.printStackTrace();
					}
				}
			});
		}
		return betweenRounds;
	}

	private ReplyKeyboard createVotingKeyboard(long chatId) {
		List<CampingUser> targets = playerManager.getTargets();
		int total = targets.size();

		int rows = (int) Math.ceil(total / 5d);
		String[][] buttonsByRow = new String[rows][];
		String[][] valuesByRow = new String[rows][];
		int complete = 0;
		int row = 0;
		while (complete < total) {
			int across = (int) Math.ceil(((double) total) / rows);
			int n = Math.min(total - complete, across);

			String[] buttons = new String[n];
			String[] values = new String[n];
			for (int i = 0; i < n; i++) {
				CampingUser user = targets.get(complete + i);
				buttons[i] = playerManager.getInitials(user);
				CallbackId id = new CallbackId(POTATO, roundNumber, chatId, user.getTelegramId());
				values[i] = id.getResult();
			}
			buttonsByRow[row] = buttons;
			valuesByRow[row] = values;
			row++;
			complete += n;
		}
		return createKeyboard(buttonsByRow, valuesByRow);
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

	@Override
	public TimesProvider<Void> getTimeProvider() {
		return times;
	}

	@Override
	public boolean shouldRun() {
		return calendarActivated;
	}

	@Override
	public StartupMode getStartupMode() {
		return StartupMode.Never;
	}

	public AybTopicChanger getTopicChanger() {
		return topicChanger;
	}
}
