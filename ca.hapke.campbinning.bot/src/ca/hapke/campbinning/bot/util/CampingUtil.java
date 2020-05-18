package ca.hapke.campbinning.bot.util;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import ca.hapke.campbinning.bot.CampingBot;

/**
 * @author Nathan Hapke
 */
public abstract class CampingUtil {

	public static <T> T getRandom(List<T> l) {
		if (l == null || l.size() == 0)
			return null;

		int i = (int) (Math.random() * l.size());
		return l.get(i);
	}

	public static <T> T getRandom(T[] l) {
		int i = getRandomIndex(l);
		if (i == -1)
			return null;
		return l[i];
	}

	public static <T> int getRandomIndex(T[] l) {
		if (l == null)
			return -1;
		int length = l.length;
		return getRandomIndex(length);
	}

	public static <T> int getRandomIndex(Collection<T> l) {
		if (l == null)
			return -1;
		int length = l.size();
		return getRandomIndex(length);
	}

	private static int getRandomIndex(int length) {
		if (length == 0)
			return -1;
		return (int) (Math.random() * length);
	}

	public static String prefixAt(String input) {
		if (input.charAt(0) != '@') {
			input = "@" + input;
		}
		return input;
	}

	public static String generateUsernameKey(String username) {
		if (username == null)
			return null;
		String result = removePrefixAt(username);
		return result.toLowerCase().trim();
	}

	public static String removePrefixAt(String input) {
		String result;
		if (input != null && input.length() > 0 && input.charAt(0) == '@') {
			result = input.substring(1);
		} else {
			result = input;
		}
		return result;
	}

	public static boolean isNonNull(String nickname) {
		return nickname != null && !CampingBot.STRING_NULL.equalsIgnoreCase(removePrefixAt(nickname));
	}

	public static boolean notEmptyOrNull(String x) {
		return x != null && x.length() > 0 && !CampingBot.STRING_NULL.equalsIgnoreCase(x);
	}

	public static boolean matchOne(String target, String... accepts) {
		for (int i = 0; i < accepts.length; i++) {
			String s = accepts[i];
			if (s != null && s.length() > 0 && s.equalsIgnoreCase(target))
				return true;
		}
		return false;
	}

	/**
	 * Stolen from jdk.internal.joptsimple.internal.Strings
	 */
	public static String join(String[] pieces, String separator) {
		return join(asList(pieces), separator);
	}

	/**
	 * Stolen from jdk.internal.joptsimple.internal.Strings
	 */
	public static String join(Iterable<String> pieces, String separator) {
		StringBuilder buffer = new StringBuilder();

		for (Iterator<String> iter = pieces.iterator(); iter.hasNext();) {
			buffer.append(iter.next());

			if (iter.hasNext())
				buffer.append(separator);
		}

		return buffer.toString();
	}

	public static String[] suffixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };

	public static String ordinal(int i) {
		switch (i % 100) {
		case 11:
		case 12:
		case 13:
			return i + "th";
		default:
			return i + suffixes[i % 10];

		}
	}
}
