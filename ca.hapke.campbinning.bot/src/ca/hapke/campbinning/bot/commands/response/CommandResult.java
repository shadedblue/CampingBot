package ca.hapke.campbinning.bot.commands.response;

import java.util.ArrayList;
import java.util.List;

import com.vdurmont.emoji.Emoji;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBotEngine;
import ca.hapke.campbinning.bot.commands.response.fragments.EmojiFragment;
import ca.hapke.campbinning.bot.commands.response.fragments.MentionFragment;
import ca.hapke.campbinning.bot.commands.response.fragments.ResultFragment;
import ca.hapke.campbinning.bot.commands.response.fragments.TextFragment;
import ca.hapke.campbinning.bot.users.CampingUser;

/*-
 * TODO add {@link CampingUser} from for logging? 
 * TODO or... unify with {@link EventItem}?
 * 
 * @author Nathan Hapke
 */
public abstract class CommandResult {
//	protected boolean sent = false;

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

	public CommandResult add(String s) {
		fragments.add(new TextFragment(s));
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

	public abstract SendResult send(CampingBotEngine bot, Long chatId, MessageProcessor processor);

}
