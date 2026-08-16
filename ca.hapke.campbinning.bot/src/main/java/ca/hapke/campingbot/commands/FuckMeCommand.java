package ca.hapke.campingbot.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ca.hapke.campingbot.BotChoicePriority;
import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.CampingSystem;
import ca.hapke.campingbot.callback.api.CallbackCommand;
import ca.hapke.campingbot.callback.api.CallbackId;
import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.SlashCommand;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.commands.api.TextCommand;
import ca.hapke.campingbot.log.EventItem;
import ca.hapke.campingbot.processors.BlotProcessor;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.EditTextCommandResult;
import ca.hapke.campingbot.response.ImageCommandResult;
import ca.hapke.campingbot.response.SendResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.fragments.ResultFragment;
import ca.hapke.campingbot.response.fragments.TextFragment;
import ca.hapke.campingbot.response.fragments.TextStyle;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.users.CampingUserMonitor;
import ca.hapke.campingbot.util.CollectionUtil;
import ca.hapke.campingbot.util.ImageLink;

/**
 * @author Nathan Hapke
 */
public class FuckMeCommand extends SlashCommand implements TextCommand, CallbackCommand {

	public static final boolean DEBUG = true;

	private class FuTracker {
		private static final char[] letters = { 'F', 'U', 'C', 'K', 'Y', 'O', 'U' };
		private final String[] shortButtons = { "F", "U", "C", "K", "Y", "O", "U" };
		private boolean[] filledIn = new boolean[letters.length];

		private static int nextTrackerId = 1;

		private final int trackerId;
		private String[] buttonCallbackIds;
		private Message message;
		private final Integer originalMessageId;

		private long lastCampingUser = -1;

		private FuTracker(Integer originalMessageId) {
			this.originalMessageId = originalMessageId;
			trackerId = nextTrackerId++;
			buttonCallbackIds = new String[letters.length];
			for (int i = 0; i < letters.length; i++) {
				buttonCallbackIds[i] = FUCK_ME + AbstractCommand.DELIMITER + trackerId + AbstractCommand.DELIMITER + i;
			}
		}

		protected List<ResultFragment> getText() {
			StringBuilder result = new StringBuilder();
			result.append(getBlots(20));
			result.append("\n");

			result.append(getBlots(5));
			result.append(" FUCK ME? ");
			result.append(getBlots(5));
			result.append("\n");

			result.append(getBlots(2));
			result.append(" ");
			result.append(getLetter(0));
			result.append(" ");
			result.append(getLetter(1));
			result.append(" ");
			result.append(getLetter(2));
			result.append(" ");
			result.append(getLetter(3));
			result.append("  ");
			result.append(getLetter(4));
			result.append(" ");
			result.append(getLetter(5));
			result.append(" ");
			result.append(getLetter(6));
			result.append(" ");
			result.append(getBlots(2));
			result.append("\n");

			result.append(getBlots(20));
			result.append("\n");
			TextFragment f1 = new TextFragment(result.toString(), TextStyle.Preformatted);
			TextFragment f2 = new TextFragment("want to buy a letter, asshole?", TextStyle.Italic);
			return List.of(f1, f2);
		}

		private char getLetter(int i) {
			if (i >= 0 && i < 7 && filledIn[i]) {
				return letters[i];
			}
			return '_';
		}

		private boolean isComplete() {
			for (int i = 0; i < filledIn.length; i++) {
				if (!filledIn[i]) {
					return false;
				}
			}
			return true;
		}

		private String getBlots(int qty) {
			StringBuilder sb = new StringBuilder(qty);
			for (int i = 0; i < qty; i++) {
				sb.append(CollectionUtil.getRandom(BlotProcessor.blotsFull));
			}
			return sb.toString();
		}

		protected InlineKeyboardMarkup getKeyboard() {
			if (isComplete())
				return null;
			return AbstractCommand.createKeyboard(shortButtons, buttonCallbackIds, 4);
		}

		public void setMessage(Message msg) {
			if (this.message == null)
				this.message = msg;
		}

		public int countLetters() {
			int count = 0;
			for (int i = 0; i < filledIn.length; i++) {
				if (filledIn[i])
					count++;
			}
			return count;
		}

		public boolean canFillIn(CampingUser user) {
			if (DEBUG && CampingSystem.getInstance().isAdmin(user)) {
				return true;
			}
			if (user.getCampingId() == lastCampingUser) {
				return false;
			}
			return true;
		}

		public boolean fillIn(int letterIndex, CampingUser campingUser) {
			if (letterIndex >= 0 && letterIndex < filledIn.length) {
				if (!filledIn[letterIndex]) {
					filledIn[letterIndex] = true;
					shortButtons[letterIndex] = "✅";
					lastCampingUser = campingUser.getCampingId();
					return true;
				}
			}
			return false;
		}
	}

	private static final String FU = "fu";
	private static final String FUCK_ME = "FuckMe?";
	private static final String FUCK = "fuck";
	private static final SlashCommandType FU_COMMAND = new SlashCommandType(FUCK_ME, FU,
			BotCommandIds.SILLY_RESPONSE | BotCommandIds.TEXT | BotCommandIds.USE);

	private SlashCommandType[] cmds = new SlashCommandType[] { FU_COMMAND };
	private ImageLink fuImage = new ImageLink("http://www.hapke.ca/images/fu.mp4", ImageLink.GIF);
	private CampingBot bot;

	protected final Map<Integer, FuTracker> inProgress = new HashMap<>();

	public FuckMeCommand(CampingBot bot) {
		this.bot = bot;
	}

	@Override
	public SlashCommandType[] getSlashCommandsToRespondTo() {
		return cmds;
	}

	private CommandResult sendFu(Message message) {
		TextCommandResult tcr = new TextCommandResult(FU_COMMAND);
		tcr.setReplyTo(message.getMessageId());
		FuTracker tracker = new FuTracker(message.getMessageId());
		tcr.add(tracker.getText());
		Long chatId = message.getChatId();

		tcr.setKeyboard(tracker.getKeyboard());

		try {
			SendResult sendResult = tcr.send(bot, chatId);
			Message msg = sendResult.outgoingMsg;
			tracker.setMessage(msg);
			inProgress.put(tracker.trackerId, tracker);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
		return tcr;
	}

	@Override
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, Long chatId,
			CampingUser campingFromUser) throws TelegramApiException {
		return sendFu(message);
	}

	@Override
	public CommandResult textCommand(CampingUser campingFromUser, List<MessageEntity> entities, Long chatId,
			Message message) throws TelegramApiException {
		return sendFu(message);
	}

	@Override
	public boolean isMatch(String msg, Message message) {
		CampingUser targetUser = bot.findTarget(message, false, true, BotChoicePriority.Only);
		return targetUser != null && msg.toLowerCase().contains(FUCK);
	}

	@Override
	public String getCommandName() {
		return FUCK_ME;
	}

	@Override
	public void appendHelpText(SlashCommandType cmd, TextCommandResult result) {
		result.add("No fuck youuuuuuu!");
	}

	@Override
	public EventItem reactToCallback(CallbackId id, CallbackQuery callbackQuery) {
		System.out.println(id);
		FuTracker tracker = inProgress.get(id.getUpdateId());
		if (tracker == null)
			return null;

		int letterIndex = (int) id.getIds()[0];
		CampingUser campingUser = CampingUserMonitor.getInstance().monitor(callbackQuery.getFrom());

		if (!tracker.canFillIn(campingUser)) {
			AnswerCallbackQuery answer = new AnswerCallbackQuery();
			answer.setCallbackQueryId(callbackQuery.getId());
			answer.setText("Let someone else do the next letter, you piece of shit!");
			try {
				bot.execute(answer);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
			return null;
		}

		if (!tracker.fillIn(letterIndex, campingUser)) {
			AnswerCallbackQuery answer = new AnswerCallbackQuery();
			answer.setCallbackQueryId(callbackQuery.getId());
			answer.setText("Letter already filled in!");
			try {
				bot.execute(answer);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
			return null;
		}

		{
			int count = tracker.countLetters();

			AnswerCallbackQuery answer = new AnswerCallbackQuery();
			answer.setCallbackQueryId(callbackQuery.getId());
			if (count < 7)
				answer.setText(count + "/7");
			else
				answer.setText("Damn straight, MFer!");
			try {
				bot.execute(answer);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		}

		User telegramUser = callbackQuery.getFrom();
		CampingUser user = CampingUserMonitor.getInstance().getUser(telegramUser);
		Long chatId = callbackQuery.getMessage().getChatId();

		Message message = tracker.message;
		EditTextCommandResult editCommand = new EditTextCommandResult(FU_COMMAND, message, tracker.getText(),
				tracker.getKeyboard());
		SendResult editResult = null;
		try {
			editResult = editCommand.send(bot, chatId);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
		EventItem editEventItem = new EventItem(FU_COMMAND, user, chatId, editResult);
		if (tracker.isComplete()) {
			inProgress.remove(tracker.trackerId);

			ImageCommandResult result = new ImageCommandResult(FU_COMMAND, fuImage);
			result.setReplyTo(tracker.originalMessageId);
			try {
				result.send(bot, chatId);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		}

		return editEventItem;
	}
}
