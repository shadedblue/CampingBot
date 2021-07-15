package ca.hapke.campingbot.commands.spell;

import ca.hapke.campingbot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class CastStruct {
	public final CampingUser caster;
	public final Long time;

	public CastStruct(CampingUser caster, Long time) {
		this.caster = caster;
		this.time = time;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CastStruct [");
		builder.append(caster);
		builder.append(" @ ");
		builder.append(time);
		builder.append("]");
		return builder.toString();
	}
}
