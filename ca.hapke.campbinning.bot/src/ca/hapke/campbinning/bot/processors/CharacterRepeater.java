package ca.hapke.campbinning.bot.processors;

/**
 * @author Nathan Hapke
 */
public class CharacterRepeater extends MessageProcessor {
	private int total;
	private int[] t, s;

	public CharacterRepeater(boolean highPower) {
		super(true);
		if (highPower)
			t = new int[] { 5, 73, 12, 6, 3, 1 };
		else
			t = new int[] { 3, 83, 9, 4, 2, 1 };

		s = new int[t.length];
		for (int i = 0; i < t.length; i++) {
			total += t[i];
			s[i] = total;
		}
	}

	@Override
	protected String internalProcessStringFragment(String value, boolean useMarkupV2) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < value.length(); i++) {
			char ch = value.charAt(i);

			if (ch == '*' || ch == '\\' || ch == '[' || ch == ']' || ch == '/') {
				sb.append(ch);
			} else {
				int x = (int) (Math.random() * total);

				int j = 0;
				while (j < s.length && x > s[j])
					j++;
				for (int k = 0; k < j; k++) {
					sb.append(ch);
				}
			}
		}
		return sb.toString();
	}

}
