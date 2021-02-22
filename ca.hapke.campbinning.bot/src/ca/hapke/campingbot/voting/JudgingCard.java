package ca.hapke.campingbot.voting;

public class JudgingCard {
	private int[] as;
	private int[] bs;
	private int rounds;

	public JudgingCard(int rounds) {
		this.rounds = rounds;
		as = new int[rounds];
		bs = new int[rounds];

		for (int i = 0; i < as.length; i++) {
			as[i] = -1;
			bs[i] = -1;
		}
	}

	public void setVote(int round, int a, int b) {
		if (round >= 1 && round <= as.length) {
			as[round - 1] = a;
			bs[round - 1] = b;
		}
	}

	public boolean hasVote(int round) {
		if (round >= 1 && round < as.length) {
			return as[round - 1] != -1 && bs[round - 1] != -1;
		}
		return false;
	}

	public boolean isComplete() {
		for (int round = 0; round < rounds; round++) {
			if (as[round] == -1 || bs[round] == -1) {
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
}