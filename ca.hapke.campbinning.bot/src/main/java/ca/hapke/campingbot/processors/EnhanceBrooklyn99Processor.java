package ca.hapke.campingbot.processors;

/**
 * @author Nathan Hapke
 */
public class EnhanceBrooklyn99Processor extends MessageProcessor {

	private static final String COOL = "cool";

	public EnhanceBrooklyn99Processor() {
		super(true);
	}

	@Override
	protected String internalProcessStringFragment(String value, boolean useMarkupV2) {
		String lower = value.toLowerCase();
		if (lower.contains(COOL)) {
			StringBuilder sb = new StringBuilder();
			int i = 0;
			int last = 0;
			while ((i = lower.indexOf(COOL, i)) >= 0) {
				String preserveCase = value.substring(i, i + 4);
				sb.append(value.substring(last, i));
				int repeats = (int) (Math.random() * 5 + 5);
				for (int j = 0; j < repeats; j++) {
					sb.append(preserveCase);
				}
				sb.append("...");
				repeats = (int) (Math.random() * 3 + 2);
				for (int j = 0; j < repeats; j++) {
					sb.append("nodoubt");
				}
				last = i;
				i += 4;
			}
			sb.append(value.substring(last));
			return sb.toString();
		} else
			return value;
	}

}
