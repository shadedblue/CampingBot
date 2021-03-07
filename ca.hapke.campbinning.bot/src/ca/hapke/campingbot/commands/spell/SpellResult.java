package ca.hapke.campingbot.commands.spell;

import java.util.List;

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
	public static final TextFragment NO_VICTIM_INLINE = new TextFragment(
			", and didn't pick a victim to cast a spell on!");

	public static final TextFragment NOT_BOT_COMMAND = new TextFragment(
			"Foolish infidel. Spells are only to be cast upon mere mortals.");
	public static final TextFragment UNKNOWN_FAIL = new TextFragment(
			"Something went wrong in the bit-bucket... and the spell fizzled!?");

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
			out = new TextCommandResult(SpellCommand.SlashSpellCommand, spell);
		} else {
			switch (failure) {
			case Dipshit:
				out = new TextCommandResult(SpellCommand.SpellDipshitCommand, new MentionFragment(fromUser),
						ResultFragment.COLON_SPACE, NO_VICTIM_COMMAND, new InsultFragment(Perspective.You));
				break;
			default:
				out = new TextCommandResult(SpellCommand.SpellDipshitCommand, new MentionFragment(fromUser),
						ResultFragment.COLON_SPACE, UNKNOWN_FAIL);
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
