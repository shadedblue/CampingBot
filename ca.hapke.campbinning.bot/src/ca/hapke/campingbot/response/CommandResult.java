package ca.hapke.campingbot.response;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.vdurmont.emoji.Emoji;

import ca.hapke.campingbot.api.CampingBotEngine;
import ca.hapke.campingbot.channels.CampingChat;
import ca.hapke.campingbot.channels.CampingChatManager;
import ca.hapke.campingbot.commands.api.CommandType;
import ca.hapke.campingbot.log.EventItem;
import ca.hapke.campingbot.log.EventLogger;
import ca.hapke.campingbot.response.fragments.CaseChoice;
import ca.hapke.campingbot.response.fragments.EmojiFragment;
import ca.hapke.campingbot.response.fragments.MentionFragment;
import ca.hapke.campingbot.response.fragments.ResultFragment;
import ca.hapke.campingbot.response.fragments.TextFragment;
import ca.hapke.campingbot.response.fragments.TextStyle;
import ca.hapke.campingbot.users.CampingUser;

/*-
 * TODO add {@link CampingUser} from for logging? 
 * TODO or... unify with {@link EventItem}?
 * 
 * @author Nathan Hapke
 */
public abstract class CommandResult {
	protected Integer replyTo;
	protected ReplyKeyboard keyboard;
	// protected boolean sent = false;
	private SendResult result;

	protected List<ResultFragment> fragments;

	protected final CommandType cmd;

	public CommandResult(CommandType cmd) {
		this.cmd = cmd;
		this.fragments = new ArrayList<>();
	}

	public CommandResult(CommandType cmd, ResultFragment... fragments) {
		this.cmd = cmd;
		if (fragments == null) {
			this.fragments = new ArrayList<>();
		} else {
			this.fragments = new ArrayList<>(fragments.length);
			for (ResultFragment f : fragments) {
				this.fragments.add(f);
			}
		}
	}

	public CommandResult(CommandType cmd, List<ResultFragment> fragments) {
		this.cmd = cmd;
		this.fragments = fragments;
	}

//	public boolean isSent() {
//		return sent;
//	}

	public CommandResult add(List<ResultFragment> frags) {
		for (ResultFragment e : frags) {
			fragments.add(e);
		}
		return this;
	}

	public CommandResult add(ResultFragment... frags) {
		for (ResultFragment e : frags) {
			fragments.add(e);
		}
		return this;
	}

	public CommandResult add(String msg) {
		fragments.add(new TextFragment(msg));
		return this;
	}

	public CommandResult add(Integer msg) {
		fragments.add(new TextFragment(Integer.toString(msg)));
		return this;
	}

	public CommandResult add(Long msg) {
		fragments.add(new TextFragment(Long.toString(msg)));
		return this;
	}

	public CommandResult add(Integer msg, TextStyle style) {
		fragments.add(new TextFragment(Integer.toString(msg), style));
		return this;
	}

	public CommandResult add(String msg, CaseChoice style) {
		fragments.add(new TextFragment(msg, style));
		return this;
	}

	public CommandResult add(String msg, TextStyle style) {
		fragments.add(new TextFragment(msg, style));
		return this;
	}

	public CommandResult add(String msg, CaseChoice c, TextStyle t) {
		fragments.add(new TextFragment(msg, c, t));
		return this;
	}

	public CommandResult add(CampingUser cu) {
		fragments.add(new MentionFragment(cu));
		return this;
	}

	public CommandResult add(CampingUser cu, TextStyle style) {
		fragments.add(new MentionFragment(cu, style));
		return this;
	}

	public CommandResult add(CampingUser cu, CaseChoice style) {
		fragments.add(new MentionFragment(cu, style));
		return this;
	}

	public CommandResult add(Emoji e) {
		fragments.add(new EmojiFragment(e));
		return this;
	}

	public CommandResult newLine() {
		if (fragments.size() > 0)
			fragments.add(ResultFragment.NEWLINE);
		return this;
	}

	public CommandType getCmd() {
		return cmd;
	}

	public CommandResult setReplyToOriginalMessageIfPossible(Message message) {
		Message replyTo = message.getReplyToMessage();
		if (replyTo != null)
			setReplyTo(replyTo.getMessageId());
		return this;
	}

	public CommandResult setReplyTo(Integer replyTo) {
		if (this.replyTo == null)
			this.replyTo = replyTo;
		return this;
	}

	public CommandResult setKeyboard(ReplyKeyboard keyboard) {
		if (this.keyboard == null)
			this.keyboard = keyboard;
		return this;
	}

	public SendResult send(CampingBotEngine bot, Long chatId) throws TelegramApiException {
		if (result == null) {
			int x = fragments.size() - 1;
			if (x >= 0 && fragments.get(x) == ResultFragment.NEWLINE) {
				fragments.remove(x);
			}
			result = sendInternal(bot, chatId);
		}
		return result;
	}

	public SendResult sendAndLog(CampingBotEngine bot, long chatId) {
		if (result == null) {
			sendAndLog(bot, CampingChatManager.getInstance(bot).get(chatId));
		}
		return result;
	}

	public SendResult sendAndLog(CampingBotEngine bot, CampingChat chat) {
		if (result == null) {
			EventLogger logger = EventLogger.getInstance();
			try {
				result = send(bot, chat.chatId);

				Message outgoingMsg = result.outgoingMsg;
				String text = getTextForLog(outgoingMsg);
				Integer date;
				Integer messageId;
				if (outgoingMsg != null) {
					date = outgoingMsg.getDate();
					messageId = outgoingMsg.getMessageId();
				} else {
					date = null;
					messageId = null;
				}
				EventItem ei = new EventItem(getCmd(), bot.getMeCamping(), date, chat, messageId, text, null);
				logger.add(ei);
			} catch (TelegramApiException e) {
				logger.add(new EventItem(e.getLocalizedMessage()));
			}
		}
		return result;
	}

	protected String getTextForLog(Message outgoingMsg) {
		if (outgoingMsg == null)
			return "";
		else
			return outgoingMsg.getText();
	}

	public abstract SendResult sendInternal(CampingBotEngine bot, Long chatId) throws TelegramApiException;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CommandResult [");
		builder.append(cmd);
		builder.append("]");
		if (fragments != null) {
//			builder.append("fragments=");
//			builder.append(fragments);
//			builder.append(", ");
			for (ResultFragment f : fragments) {
				builder.append("\n  ");
				builder.append(f);
			}
		}
		if (result != null) {
			builder.append("\nresult=");
			builder.append(result);
		}
		return builder.toString();
	}
}
