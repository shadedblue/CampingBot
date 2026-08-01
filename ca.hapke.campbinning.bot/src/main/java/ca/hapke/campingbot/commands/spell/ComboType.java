package ca.hapke.campingbot.commands.spell;

/**
 * @author Nathan Hapke
 */
public enum ComboType {
	Normal(true),
	GangBang(true),
	Fizzle(false),
	KO(true),
	Breaker(true),
	Dead(false),
	Revenge(true);

	public final boolean sendsSpell;

	private ComboType(boolean sendsSpell) {
		this.sendsSpell = sendsSpell;
	}
}
