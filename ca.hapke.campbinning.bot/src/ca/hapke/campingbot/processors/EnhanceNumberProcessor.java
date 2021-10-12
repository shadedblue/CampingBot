package ca.hapke.campingbot.processors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Nathan Hapke
 */
public class EnhanceNumberProcessor extends MessageProcessor {

	private Pattern p;
	private static final String[] trailers = new String[] { "420", "69", "8008135" };

	public EnhanceNumberProcessor() {
		super(true);
		p = Pattern.compile("\\d+");
	}

	@Override
	protected String internalProcessStringFragment(String value, boolean useMarkupV2) {
		Matcher m = p.matcher(value);
//		if (m.matches()) {
		StringBuilder sb = new StringBuilder();
		int end = 0;
		while (m.find()) {
			int start = m.start();
			// before this match
			sb.append(value.substring(end, start));

			end = m.end();
			String originalNumber = value.substring(start, end);
			sb.append(originalNumber);
			String trailer = trailers[(int) (trailers.length * Math.random())];
			sb.append('.');
			sb.append(trailer);

		}
		sb.append(value.substring(end));
		return sb.toString();
//		} else {
//			return value;
//		}
	}
}
