package ca.hapke.campbinning.bot.commands;

import java.util.Collections;
import java.util.List;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.commands.response.TextCommandResult;
import ca.hapke.campbinning.bot.commands.response.fragments.MentionFragment;
import ca.hapke.campbinning.bot.commands.response.fragments.ResultFragment;
import ca.hapke.campbinning.bot.commands.response.fragments.TextFragment;
import ca.hapke.campbinning.bot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class SpellResult {
	public static final String NO_VICTIM_COMMAND = "Spells must be cast upon a victim, ya dipshit.";
	public static final String NO_VICTIM_INLINE = "I'm a dipshit, and didn't pick a victim to cast a spell on!";

	public static final String NOT_BOT_COMMAND = "Foolish infidel. Spells are only to be cast upon mere mortals.";
	public static final String NOT_BOT_INLINE = "I should know better than trying to cast a spell on our supreme overlord!";
	public static final String UNKNOWN_FAIL = "Something went wrong in the bit-bucket... and the spell fizzled!?";

	private SpellFailure failure;
	private List<ResultFragment> spell;
	private boolean success;

	private CampingUser fromUser;
	private CampingUser target;

	public SpellResult(CampingUser campingFromUser, CampingUser target, List<ResultFragment> spell) {
		success = true;
		this.fromUser = campingFromUser;
		this.target = target;
		this.spell = spell;
	}

	public SpellResult(CampingUser campingFromUser, CampingUser target, SpellFailure failure) {
		success = false;
		this.fromUser = campingFromUser;
		this.target = target;
		this.failure = failure;
	}

	public TextCommandResult provideCommandResult() {
		TextCommandResult out;
		if (success) {
			out = new TextCommandResult(BotCommand.Spell, spell);
		} else {
			switch (failure) {
			case Dipshit:
				out = new TextCommandResult(BotCommand.SpellDipshit, new MentionFragment(fromUser),
						new TextFragment(": "), new TextFragment(NO_VICTIM_COMMAND));
				break;
			case NotMe:
				out = new TextCommandResult(BotCommand.SpellDipshit, new MentionFragment(fromUser),
						new TextFragment(": "), new TextFragment(NOT_BOT_COMMAND));
				break;
			default:
				out = new TextCommandResult(BotCommand.SpellDipshit, new MentionFragment(fromUser),
						new TextFragment(": "), new TextFragment(UNKNOWN_FAIL));
				break;
			}
		}
		return out;
	}

	public List<ResultFragment> provideInlineResult() {
		List<ResultFragment> out;
		if (success) {
			out = spell;
		} else {
			switch (failure) {
			case Dipshit:
				out = Collections.singletonList(new TextFragment(NO_VICTIM_INLINE));
				break;
			case NotMe:
				out = Collections.singletonList(new TextFragment(NOT_BOT_INLINE));
				break;
			default:
				out = Collections.singletonList(new TextFragment(UNKNOWN_FAIL));
				break;
			}
		}
		return out;
	}

	public boolean isSuccess() {
		return success;
	}

	public CampingUser getFromUser() {
		return fromUser;
	}

	public CampingUser getTarget() {
		return target;
	}
}
