package ca.hapke.campbinning.bot.commands.response;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.vdurmont.emoji.Emoji;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBotEngine;
import ca.hapke.campbinning.bot.commands.response.fragments.CaseChoice;
import ca.hapke.campbinning.bot.commands.response.fragments.EmojiFragment;
import ca.hapke.campbinning.bot.commands.response.fragments.MentionFragment;
import ca.hapke.campbinning.bot.commands.response.fragments.ResultFragment;
import ca.hapke.campbinning.bot.commands.response.fragments.TextFragment;
import ca.hapke.campbinning.bot.commands.response.fragments.TextStyle;
import ca.hapke.campbinning.bot.users.CampingUser;

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

	protected final BotCommand cmd;

	public CommandResult(BotCommand cmd) {
		this.cmd = cmd;
		this.fragments = new ArrayList<>();
	}

	public CommandResult(BotCommand cmd, ResultFragment... fragments) {
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

	public CommandResult(BotCommand cmd, List<ResultFragment> fragments) {
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

	public BotCommand getCmd() {
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

	public SendResult getResult() {
		return result;
	}

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
