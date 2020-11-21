package ca.hapke.campingbot.util;

import ca.hapke.campingbot.BotConstants;

/**
 * @author Nathan Hapke
 */
public abstract class CampingUtil {

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
		return nickname != null && !BotConstants.STRING_NULL.equalsIgnoreCase(removePrefixAt(nickname));
	}

	public static boolean notEmptyOrNull(String x) {
		return x != null && x.length() > 0 && !BotConstants.STRING_NULL.equalsIgnoreCase(x);
	}

	public static String blankTheNull(String x) {
		if (x == null || BotConstants.STRING_NULL.equalsIgnoreCase(x))
			return "";
		else
			return x;
	}
}
