package ca.hapke.campbinning.bot.users;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.util.CampingUtil;

/**
 * @author Nathan Hapke
 */
public class CampingUser {
	private int telegramId = -1;
	private final int campingId;
	private String username;
	private String firstname;
	private String lastname;
	private String nickname;

	private long lastUpdate;
	private int birthdayMonth = -1;
	private int birthdayDay = -1;

	// For GlazedLists to autosort
	private PropertyChangeSupport support = new PropertyChangeSupport(this);
	public static final String UNKNOWN_TARGET = "unknown target";

	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		support.addPropertyChangeListener(pcl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		support.removePropertyChangeListener(pcl);
	}

	public CampingUser(int telegramId, String username, String firstname, String lastname) {
		this.campingId = CampingUserMonitor.getInstance().getNextCampingId();
		this.telegramId = telegramId;
		this.username = username;
		this.firstname = firstname;
		this.lastname = lastname;
	}

	public CampingUser(int suggestedId, int telegramId, String username, String firstname, String lastname) {
		this.campingId = CampingUserMonitor.getInstance().getNextCampingId(suggestedId);
		this.telegramId = telegramId;
		this.username = username;
		this.firstname = firstname;
		this.lastname = lastname;
	}

	public int getCampingId() {
		return campingId;
	}

	public int getTelegramId() {
		return telegramId;
	}

	public String getUsername() {
		return username;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public String getNickname() {
		return nickname;
	}

	public void setBirthday(int month, int day) {
		if (birthdayMonth != -1 || birthdayDay != -1)
			return;

		if (month == -1 || day == -1)
			return;

		int oldMonth = birthdayMonth;
		float oldDay = birthdayDay;
		birthdayMonth = month;
		birthdayDay = day;

		support.firePropertyChange("birthdayMonth", oldMonth, birthdayMonth);
		support.firePropertyChange("birthdayDay", oldDay, birthdayDay);
	}

	public int getBirthdayMonth() {
		return birthdayMonth;
	}

	public int getBirthdayDay() {
		return birthdayDay;
	}

	public boolean hasBirthday() {
		return birthdayMonth != -1 && birthdayDay != -1;
	}

	public String getBirthday() {
		String result = "";
		if (birthdayMonth != -1 && birthdayDay != -1)
			result = birthdayMonth + "/" + birthdayDay;
		return result;
	}

	public void mergeFrom(CampingUser other) {
		if (username == null)
			setUsername(other.username);
		if (firstname == null)
			setUsername(other.firstname);
		if (lastname == null)
			setUsername(other.lastname);
		if (nickname == null)
			setNickname(other.nickname);

		setLastUpdate(Math.max(other.lastUpdate, this.lastUpdate));

	}

	public void setId(Integer id) {
		if (this.telegramId == -1) {
			this.telegramId = id;
			support.firePropertyChange("id", null, id);
		}
	}

	public void setUsername(String username) {
		if (this.username == null && username != null) {
			this.username = username;
			support.firePropertyChange("username", null, username);
		}
	}

	public void setFirstname(String firstname) {
		if (this.firstname == null && firstname != null) {
			this.firstname = firstname;
			support.firePropertyChange("firstname", null, firstname);
		}
	}

	public void setLastname(String lastname) {
		if (this.lastname == null && lastname != null) {
			this.lastname = lastname;
			support.firePropertyChange("lastname", null, lastname);
		}
	}

	public void setNickname(String value) {
		String oldVal = nickname;
		nickname = value;
		String newVal = nickname;

		support.firePropertyChange("nickname", oldVal, newVal);
	}

	public void setLastUpdate(Long lastUpdate) {
		long oldLastUpdate = this.lastUpdate;
		this.lastUpdate = lastUpdate;
		support.firePropertyChange("lastUpdate", oldLastUpdate, lastUpdate);
	}

	public void increment(BotCommand spell) {
		long now = System.currentTimeMillis();

		switch (spell) {
		case MBIYF:
		case MBIYFDipshit:
		case Spell:
		case SpellDipshit:
		case RantActivatorInitiation:
		case AitaActivatorInitiation:
		case PleasureModel:
		case PartyEveryday:
			// you don't get credit unless it gets completed as a rant.
			setLastUpdate(now);
			break;
		case VoteActivatorComplete:
		case AllBalls:
		case AllFaces:
		case AllNicknames:
		case Countdown:
		case NicknameConversion:

		case VoteTopicInitiation:
		case VoteTopicComplete:
		case VoteInitiationFailed:
		case Vote:
		case Reload:
		case SetNickname:
			// case Stats:
			// case StatsEndOfWeek:
			// case Test:
		case RegularChatReply:
		case RegularChatUpdate:
		case SetNicknameRejected:
		case UiString:
			// case RegularChatAnimation:
		case RegularChatGif:
		case RegularChatPhoto:
		case RegularChatEdit:
		case RegularChatVideo:
		case RegularChatSticker:
			break;
		}
	}

	public void resetStats() {
		// setLastUpdate(0l);
	}

	public boolean equals(CampingUser that) {
		if (that == null)
			return false;
		if (telegramId != -1 && telegramId == that.telegramId)
			return true;
		if (username != null && username.equalsIgnoreCase(that.username))
			return true;
		return false;
	}

	@Override
	public String toString() {
		String middle;
		if (username != null) {
			middle = " \"" + CampingUtil.prefixAt(username) + "\" ";
		} else {
			middle = " ";
		}
		return "CampingUser [#" + telegramId + " " + firstname + middle + lastname + "]";
	}

	public String target() {
		return "[" + getDisplayName() + "](tg://user?id=" + telegramId + ")";
	}

	public String getDisplayName() {
		if (CampingUtil.isNonNull(nickname))
			return nickname;
		if (firstname != null)
			return firstname;
		return CampingUser.UNKNOWN_TARGET;
	}

	public String getFirstOrUserName() {
		if (CampingUtil.isNonNull(firstname))
			return firstname;
		return CampingUtil.prefixAt(username);
	}

	public String getNameForLog() {
		StringBuilder sb = new StringBuilder();
		if (firstname != null)
			sb.append(firstname);
		boolean bracket = sb.length() > 0 && username != null;
		if (bracket) {
			sb.append("(");
		}
		if (username != null)
			sb.append(CampingUtil.prefixAt(username));
		if (bracket) {
			sb.append(")");
		}
		return sb.toString();
	}

}
