package ca.hapke.campingbot.commands.spell;

import ca.hapke.campingbot.channels.CampingChat;
import ca.hapke.campingbot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class PendingCast {

	public final CampingUser victim;
	public final SpellResult result;
	public final CampingChat chat;
	public int waits;

	public PendingCast(CampingUser victim, SpellResult result, CampingChat chat, int waits) {
		this.victim = victim;
		this.result = result;
		this.chat = chat;
		this.waits = waits;
	}

}