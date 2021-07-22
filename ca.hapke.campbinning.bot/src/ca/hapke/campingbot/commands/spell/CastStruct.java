package ca.hapke.campingbot.commands.spell;

import ca.hapke.campingbot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class CastStruct {
	public final CampingUser user;
	public final Long time;

	public CastStruct(CampingUser user, Long time) {
		this.user = user;
		this.time = time;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CastStruct [");
		builder.append(user);
		builder.append(" @ ");
		builder.append(time);
		builder.append("]");
		return builder.toString();
	}
}
