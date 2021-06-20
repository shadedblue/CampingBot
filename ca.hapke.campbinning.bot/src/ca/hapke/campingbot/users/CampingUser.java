package ca.hapke.campingbot.users;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

import ca.hapke.campingbot.BotConstants;
import ca.hapke.campingbot.log.DatabaseConsumer;
import ca.hapke.campingbot.util.CampingUtil;
import ca.hapke.util.StringUtil;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * @author Nathan Hapke
 */
@Entity
@Table(name = CampingUser.USER_TABLE)
public class CampingUser implements Serializable {
	private static final long serialVersionUID = 3805021853368529014L;
	public static final String USER_TABLE = "Users";

	public class Birthday implements Comparable<Birthday> {
		public Birthday(int month, int day) {
			this.month = month;
			this.day = day;
			birthdayString = Month.of(month).getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault()) + " "
					+ StringUtil.ordinal(day);
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

	private long telegramId = -1;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long campingId;
	private String username;
	private String firstname;
	private String lastname;
	private String nickname;
	private String initials;
	private int birthdayMonth = -1;
	private int birthdayDay = -1;

	@Transient
	private Birthday birthday;
	private boolean seenInteraction = false;

	// For GlazedLists to autosort
	@Transient
	private PropertyChangeSupport support = new PropertyChangeSupport(this);
	public static final String UNKNOWN_TARGET = "unknown target";

	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		support.addPropertyChangeListener(pcl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		support.removePropertyChangeListener(pcl);
	}

//	public CampingUser(long telegramId, String username, String firstname, String lastname) {
//		this.campingId = CampingUserMonitor.getInstance().getNextCampingId();
//		this.telegramId = telegramId;
//		this.username = username;
//		this.firstname = firstname;
//		this.lastname = lastname;
//	}
	public CampingUser() {

	}

	public CampingUser(long suggestedId, long telegramId, String username, String firstname, String lastname,
			String initials) {
//		this.campingId = CampingUserMonitor.getInstance().getNextCampingId(suggestedId);
		this.telegramId = telegramId;
		this.username = username;
		this.firstname = firstname;
		this.lastname = lastname;
		if (initials != null) {
			this.initials = initials;
		} else {
			String f = getInitial(firstname);
			String l = getInitial(lastname);
			this.initials = (f + l).toUpperCase();
		}
	}

	private String getInitial(String name) {
		if (name == null || BotConstants.STRING_NULL.equalsIgnoreCase(name) || name.length() == 0)
			return "";
		return name.substring(0, 1);
	}

	public long getCampingId() {
		return campingId;
	}

	public long getTelegramId() {
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

	public String getInitials() {
		return initials;
	}

	public String getNickname() {
		return nickname;
	}

	public boolean hasBirthday() {
		return birthday != null;
	}

	public Birthday getBirthday() {
		return birthday;
	}

	public boolean isSeenInteraction() {
		return seenInteraction;
	}

	public void mergeFrom(CampingUser other) {
		if (telegramId < 0)
			setTelegramId(other.telegramId);
		if (username == null)
			setUsername(other.username);
		if (firstname == null)
			setUsername(other.firstname);
		if (lastname == null)
			setUsername(other.lastname);
		if (nickname == null)
			setNickname(other.nickname);
		if (initials == null)
			setInitials(other.initials);
		if (birthday == null && other.birthday != null)
			setBirthday(other.birthday.month, other.birthday.day);
		setSeenInteraction(other.seenInteraction);
	}

	public void setTelegramId(Long id) {
		if (this.telegramId == -1) {
			this.telegramId = id;
			support.firePropertyChange("telegramId", null, id);

			updatePersistence();
		}
	}

	public void setCampingId(Long id) {
		if (this.campingId == -1) {
			this.campingId = id;
			support.firePropertyChange("campingId", null, id);

			updatePersistence();
		}
	}

	public void setUsername(String username) {
		if (this.username == null && username != null) {
			this.username = username;
			support.firePropertyChange("username", null, username);

			updatePersistence();
		}
	}

	public void setFirstname(String firstname) {
		if (this.firstname == null && firstname != null) {
			this.firstname = firstname;
			support.firePropertyChange("firstname", null, firstname);

			updatePersistence();
		}
	}

	public void setLastname(String lastname) {
		if (this.lastname == null && lastname != null) {
			this.lastname = lastname;
			support.firePropertyChange("lastname", null, lastname);

			updatePersistence();
		}
	}

	public void setNickname(String value) {
		if (nickname != null && nickname.equals(value))
			return;
		String oldVal = nickname;
		nickname = value;

		support.firePropertyChange("nickname", oldVal, nickname);
		updatePersistence();
	}

	public void setInitials(String value) {
		if (initials != null && initials.equals(value))
			return;

		String oldVal = initials;
		initials = value;

		support.firePropertyChange("initials", oldVal, initials);
		updatePersistence();
	}

	public void setSeenInteraction(boolean seenInteraction) {
		if (this.seenInteraction == false && seenInteraction) {
			this.seenInteraction = true;
			support.firePropertyChange("seenInteraction", false, true);
			updatePersistence();
		}
	}

	public void setBirthday(int month, int day) {
		if (birthday != null)
			return;

		if (month == -1 || day == -1 || month > 12 || day > 31)
			return;

		birthday = new Birthday(month, day);

		setBirthdayMonth(month);
		setBirthdayDay(day);
	}

	public void setBirthdayDay(int day) {
		if (birthday == null)
			birthday = new Birthday(-1, day);
		this.birthday.day = day;
		this.birthdayDay = day;
		support.firePropertyChange("birthdayDay", -1, day);
	}

	public void setBirthdayMonth(int month) {
		if (birthday == null)
			birthday = new Birthday(month, -1);
		this.birthday.month = month;
		this.birthdayMonth = month;
		support.firePropertyChange("birthdayMonth", -1, month);
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

	private void updatePersistence() {
		DatabaseConsumer db = DatabaseConsumer.getInstance();
		if (db != null) {
			EntityManager mgr = db.getManager();
			mgr.getTransaction().begin();
			mgr.persist(this);
			mgr.getTransaction().commit();
		}
	}
}
