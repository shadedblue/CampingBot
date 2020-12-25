package ca.hapke.campingbot.voting;

/**
 * @author Nathan Hapke
 */
public class UfcFight {
	public final String a, b;
	public final int rounds;
	public final int fightNumber;

	public UfcFight(int fightNumber, String a, String b, int rounds) {
		this.a = a;
		this.b = b;
		this.rounds = rounds;
		this.fightNumber = fightNumber;
	}
}
