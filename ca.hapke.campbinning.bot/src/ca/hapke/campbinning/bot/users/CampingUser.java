package ca.hapke.campbinning.bot.users;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

import ca.hapke.campbinning.bot.util.CampingUtil;

/**
 * @author Nathan Hapke
 */
public class CampingUser {
	public class Birthday implements Comparable<Birthday> {

		public Birthday(int month, int day) {
			this.month = month;
			this.day = day;
			birthdayString = Month.of(month).getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault()) + " "
					+ CampingUtil.ordinal(day);
		}

		private int month;
		private int day;
		private String birthdayString;

		public int getMonth() {
			return month;
		}

		public int getDay() {
			return day;
		}

		public String getKey() {
			return month + "$" + day;
		}

		@Override
		public int compareTo(Birthday that) {
			if (that == null)
				return 1;

			if (this.month != that.month)
				return this.month - that.month;
			if (this.day != that.day)
				return this.day - that.day;
			return 0;
		}

		@Override
		public String toString() {
			return birthdayString;
		}

	}

	private int telegramId = -1;
	private final int campingId;
	private String username;
	private String firstname;
	private String lastname;
	private String nickname;

	private Birthday birthday;

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

	public String getNickname() {
		return nickname;
	}

	public void setBirthday(int month, int day) {
		if (birthday != null)
			return;

		if (month == -1 || day == -1 || month > 12 || day > 31)
			return;

		birthday = new Birthday(month, day);

		support.firePropertyChange("birthdayMonth", -1, month);
		support.firePropertyChange("birthdayDay", -1, day);
	}

	public boolean hasBirthday() {
		return birthday != null;
	}

	public Birthday getBirthday() {
		return birthday;
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

		support.firePropertyChange("nickname", oldVal, nickname);
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

	public String getDisplayName() {
		if (CampingUtil.isNonNull(nickname))
			return nickname;
		if (firstname != null)
			return firstname;
		if (username != null)
			return CampingUtil.prefixAt(username);
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
