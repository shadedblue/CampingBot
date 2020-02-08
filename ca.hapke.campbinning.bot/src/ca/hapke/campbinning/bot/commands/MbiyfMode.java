package ca.hapke.campbinning.bot.commands;

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

	public MbiyfType getType() {
		return type;
	}

	public boolean isEnablement() {
		switch (type) {
		case Birthday:
		case Friday:
			return true;
		case Off:
		}
		return false;
	}

	public List<CampingUser> getRestrictedToUsers() {
		return restrictedToUsers;
	}

	public MbiyfMode(MbiyfType type, List<CampingUser> restrictedToUsers) {
		this.type = type;
		this.restrictedToUsers = restrictedToUsers;
	}
}
