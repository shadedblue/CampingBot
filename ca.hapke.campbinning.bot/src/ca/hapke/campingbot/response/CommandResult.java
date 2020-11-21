package ca.hapke.campingbot.response;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.vdurmont.emoji.Emoji;

import ca.hapke.campingbot.api.CampingBotEngine;
import ca.hapke.campingbot.commands.api.CommandType;
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

	public CommandResult add(ResultFragment e) {
		fragments.add(e);
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

	public CommandResult add(Emoji e) {
		fragments.add(new EmojiFragment(e));
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
			result = sendInternal(bot, chatId);
		}
		return result;
	}

	public abstract SendResult sendInternal(CampingBotEngine bot, Long chatId) throws TelegramApiException;

//	public SendResult getResult() {
//		return result;
//	}

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
