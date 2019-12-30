package ca.hapke.campbinning.bot.users;

/**
 * @author Nathan Hapke
 */
public class NicknameRejectedException extends Exception {

	private static final long serialVersionUID = -3018859494070428449L;
	public static final String CANT_GIVE_YOURSELF_A_NICKNAME = "No fuckin' way.\n#1 rule of nicknames... you can't give yourself a nickname";
	public static final String USER_NOT_FOUND = "Dunno who you're trying to nickname";
	public static final String INVALID_SYNTAX = "Invalid syntax, DUMB ASS.";

	public NicknameRejectedException(String reason) {
		super(reason);
	}

}
