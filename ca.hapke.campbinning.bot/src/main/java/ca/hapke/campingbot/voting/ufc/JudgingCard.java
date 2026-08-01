package ca.hapke.campingbot.voting.ufc;

/**
 * @author Nathan Hapke
 */
public class JudgingCard {
	private int[] as;
	private int[] bs;
	private int rounds;

	public JudgingCard(int rounds) {
		this.rounds = rounds;
		as = new int[rounds];
		bs = new int[rounds];
	}

	public void setVote(int round, int a, int b) {
		if (round >= 1 && round <= as.length) {
			as[round - 1] = a;
			bs[round - 1] = b;
		}
	}

	public boolean hasVote(int round) {
		if (round >= 1 && round <= as.length) {
			return as[round - 1] > 0 && bs[round - 1] > 0;
		}
		return false;
	}

	public boolean isComplete() {
		for (int round = 0; round < rounds; round++) {
			if (as[round] <= 0 || bs[round] <= 0) {
				return false;
			}
		}
		return true;
	}

	public int getA(int round) {
		if (round >= 1 && round <= as.length) {
			return as[round - 1];
		}
		return -1;
	}

	public int getB(int round) {
		if (round >= 1 && round <= bs.length) {
			return bs[round - 1];
		}
		return -1;
	}

	public int getRounds() {
		return rounds;
	}

	public int getATotal() {
		int a = 0;
		for (int round = 1; round <= rounds; round++) {
			a += getA(round);
		}
		return a;
	}

	public int getBTotal() {
		int b = 0;
		for (int round = 1; round <= rounds; round++) {
			b += getB(round);
		}
		return b;
	}

	public JudgeDecision getDecision() {
		int a, b;
		a = getATotal();
		b = getBTotal();
		if (a == b)
			return JudgeDecision.Draw;
		if (a > b)
			return JudgeDecision.A;
		return JudgeDecision.B;
	}
}
