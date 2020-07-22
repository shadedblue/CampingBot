package ca.hapke.campbinning.bot;

/**
 * @author Nathan Hapke
 *
 */
public abstract class BotCommandIds {
	public static final long REGULAR_CHAT = 1;
	public static final long TEXT = 2;
	public static final long REPLY = 2 << 1;
	public static final long EDIT = 2 << 2;
	public static final long GIF = 2 << 3;
	public static final long PIC = 2 << 4;
	public static final long VID = 2 << 5;
	public static final long STICKER = 2 << 6;
	public static final long THREAD = 2 << 7;

	public static final long INLINE = 2 << 15;

	public static final long USE = 2 << 16;
	public static final long SET = 2 << 17;
	public static final long FAILURE = 2 << 18;
	public static final long FINISH = 2 << 19;

	public static final long NICKNAME = 2 << 24;
	public static final long RANT = 2 << 25;
	public static final long SPELL = 2 << 26;
	public static final long PLEASURE = 2 << 27;
	public static final long BALLS = 2 << 27;
	public static final long AITA = 2 << 28;
	public static final long VOTING = 2 << 29;
	public static final long SILLY_RESPONSE = 2 << 30;

}
