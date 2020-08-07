package ca.hapke.campbinning.bot.commands.response.afd;

import ca.hapke.campbinning.bot.commands.response.MessageProcessor;
import ca.hapke.campbinning.bot.commands.response.fragments.ResultFragment;

/**
 * @author Nathan Hapke
 */
public class FontGarbler extends MessageProcessor {

	private static final char MIN = ' ';
	private static final char MAX = 'z';
	private final double pReplacement;
	private char[][] font;

	public FontGarbler(boolean highPower) {
		if (highPower)
			pReplacement = 0.4;
		else
			pReplacement = 0.2;

		font = new char[MAX - MIN + 1][];
		add(' ', "Œ ֍↔⊞");
//		add('', "");
		add('!', "¡ᖎ");
		add('\"', "");
		add('#', "ᕯ");
		add('$', "€₤₿");
		add('%', "");
		add('&', "");
		add('(', "ᕳ");
		add(')', "");
		add('+', "");
		add(',', "");
		add('-', "");
		add('.', "");
		add('0', "₀Ҩ⊝⊙");
		add('1', "₁⅟");
		add('2', "Ⅱ");
		add('3', "Ҙ⅓");
		add('4', "⁴₄");
		add('5', "⁵₅⅕");
		add('6', "Ⅵ⁶₆⅙");
		add('7', "⁷₇⅐");
		add('8', "⁸₈⅛");
		add('9', "⁹₉⅑");
		add(':', "");
		add(';', "");
		add('<', "");
		add('=', "");
		add('>', "");
		add('?', "");
		add('@', "");
		add('A', "Æᗉ");
		add('B', "βᗵ");
		add('C', "ÇƆᘓ€");
		add('D', "Ðᗟ");
		add('E', "ƎƩ");
		add('F', "℉");
		add('G', "ĞǤ");
		add('H', "ӇӉℌ");
		add('I', "Ἷ");
		add('J', "ᓓ");
		add('K', "ĸӃ");
		add('L', "");
		add('M', "");
		add('N', "ŅӤ");
		add('O', "ŒὊ");
		add('P', "¶ᓏ");
		add('Q', "");
		add('R', "ŖƦ");
		add('S', "ᔖ∮∯∰");
		add('T', "Ͳ™");
		add('U', "Ʊ");
		add('V', "∛");
		add('W', "Ψὦ");
		add('X', "χ");
		add('Y', "ƔȲ");
		add('Z', "Ƶ");
		add('`', "");
		add('a', "æ");
		add('b', "Ҍ");
		add('c', "ς");
		add('d', "");
		add('e', "ǝҿєӚ");
		add('f', "");
		add('g', "");
		add('h', "ĥӊℎ");
		add('i', "");
		add('j', "");
		add('k', "ʞҟ");
		add('l', "");
		add('m', "ɱ");
		add('n', "ñӥռᾔ");
		add('o', "δσ");
		add('p', "þρҏք");
		add('q', "ο");
		add('r', "я");
		add('s', "Ց");
		add('t', "τէ");
		add('u', "µǖմ");
		add('v', "ν");
		add('w', "ʍω");
		add('x', "җ");
		add('y', "ȳʎ");
		add('z', "ɀ");
//		add('', "");
//		add('', "");
//		add('', "");
//		add('', "");
//		add('', "");
//		add('', "");
//		add('', "");
//		add('', "");
//		add('', "");
//		add('', "");
//		add('', "");
//		add('', "");
//		add('', "");
//		add('', "");
//		add('', "");
//		add('', "");
//		add('', "");
//		add('', "");
//		add('', "");
//		add('', "");
//		add('', "");
//		add('', "");
//		add('', "");
//		add('', "");
//		add('', "");

	}

	private void add(char c, String reps) {
		int i = c - MIN;
		char[] vals = reps.toCharArray();
		font[i] = vals;

	}

	@Override
	protected String internalProcessStringFragment(String value, boolean useMarkupV2) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < value.length(); i++) {
			char ch = value.charAt(i);
			sb.append(f(ch));
		}
		return sb.toString();
	}

	private char f(char in) {
		int i = in - MIN;
		char[] vals;
		if (Math.random() < pReplacement && i >= 0 && i < font.length) {
			vals = font[i];
			if (vals != null && vals.length > 0) {
				int j = (int) (Math.random() * vals.length);
				return vals[j];
			}
		}
		return in;
	}

	@Override
	protected ResultFragment[] internalBeforeStringAssembled(ResultFragment[] fragments) {
		return fragments;
	}

	@Override
	protected String internalAfterStringAssembled(String value) {
		return value;
	}

	@Override
	protected String internalProcessImageUrl(String url) {
		return url;
	}

}
