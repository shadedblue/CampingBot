package ca.hapke.campbinning.bot.mbiyf;

import java.util.List;

import ca.hapke.campbinning.bot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class MbiyfMode {
	private final MbiyfType type;
	private final List<CampingUser> restrictedToUsers;

	public MbiyfMode(MbiyfType enablement) {
		this(enablement, null);
	}

	public MbiyfMode(MbiyfType type, List<CampingUser> restrictedToUsers) {
		this.type = type;
		this.restrictedToUsers = restrictedToUsers;
	}

	public MbiyfType getType() {
		return type;
	}

	public boolean isEnablement() {
		switch (type) {
		case Birthday:
		case Friday:
		case Asshole:
		case Special:
			return true;
		case Off:
		}
		return false;
	}

	public List<CampingUser> getRestrictedToUsers() {
		return restrictedToUsers;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MbiyfMode[");
		builder.append(isEnablement() ? "en" : "dis");
		builder.append("able ");
		if (type != null) {
			builder.append("type=");
			builder.append(type);
		}
		if (restrictedToUsers != null) {
			builder.append("\nrestrictedTo=");
			builder.append(restrictedToUsers);
		}
		builder.append("]");
		return builder.toString();
	}
}
