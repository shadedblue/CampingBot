package ca.hapke.campbinning.bot;

import java.util.List;

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
		if (l == null || l.length == 0)
			return -1;
		return (int) (Math.random() * l.length);
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

}
