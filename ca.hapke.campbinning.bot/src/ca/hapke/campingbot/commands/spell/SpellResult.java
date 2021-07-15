package ca.hapke.campingbot.commands.spell;

import java.util.List;

import ca.hapke.campingbot.commands.api.ResponseCommandType;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.TextCommandResult;
import ca.hapke.campingbot.response.fragments.InsultFragment;
import ca.hapke.campingbot.response.fragments.InsultFragment.Perspective;
import ca.hapke.campingbot.response.fragments.MentionFragment;
import ca.hapke.campingbot.response.fragments.ResultFragment;
import ca.hapke.campingbot.response.fragments.TextFragment;
import ca.hapke.campingbot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class SpellResult {
	public static final TextFragment NO_VICTIM_COMMAND = new TextFragment("Spells must be cast upon a victim, ");
	public static final TextFragment NOT_BOT_COMMAND = new TextFragment(
			"Spells are only to be cast upon mere mortals, ");
	public static final TextFragment UNKNOWN_FAIL = new TextFragment(
			"Something went wrong in the bit-bucket... and the spell fizzled!?");

	private SpellFailure failure;
	private List<ResultFragment> spell;
	private boolean success;

	private CampingUser fromUser;
	private CampingUser target;

	public SpellResult(CampingUser campingFromUser, CampingUser target, List<ResultFragment> spell) {
		if (target != null) {
			success = true;
			this.fromUser = campingFromUser;
			this.target = target;
			this.spell = spell;
		} else {
			success = false;
			this.fromUser = campingFromUser;
			this.failure = SpellFailure.NoVictim;
		}
	}

	public SpellResult(CampingUser campingFromUser, CampingUser target, SpellFailure failure) {
		success = false;
		this.fromUser = campingFromUser;
		this.target = target;
		this.failure = failure;
	}

	public CommandResult provideCommandResult(CommandResult result) {
		if (result == null) {
			ResponseCommandType cmd;
			if (success) {
				cmd = SpellCommand.SlashSpellCommand;
			} else {
				cmd = SpellCommand.SpellDipshitCommand;
			}
			result = new TextCommandResult(cmd);
		}

		if (success) {
			result.add(spell);
		} else {
			switch (failure) {
			case NoVictim:
				result.add(new MentionFragment(fromUser), ResultFragment.COLON_SPACE, NO_VICTIM_COMMAND,
						new InsultFragment(Perspective.You));
				break;
			case CastAtBot:
				result.add(new MentionFragment(fromUser), ResultFragment.COLON_SPACE, NOT_BOT_COMMAND,
						new InsultFragment(Perspective.You));
				break;
			default:
				result.add(new MentionFragment(fromUser), ResultFragment.COLON_SPACE, UNKNOWN_FAIL);
				break;
			}
		}
		return result;
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
