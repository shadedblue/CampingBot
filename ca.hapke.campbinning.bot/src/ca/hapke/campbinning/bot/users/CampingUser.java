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
	private int ballsCount;
	private int victimCount;
	private int spellCount;
	private int rantCount;
	private float rantScore;
	private int rantActivation;
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

	public int getBallsCount() {
		return ballsCount;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public String getNickname() {
		return nickname;
	}

	public int getVictimCount() {
		return victimCount;
	}

	public int getRantCount() {
		return rantCount;
	}

	public float getRantScore() {
		return rantScore;
	}

	public int getSpellCount() {
		return spellCount;
	}

	public void completeRant(float score) {
		setRant(rantCount + 1, rantScore + score);
	}

	public void setRant(int count, float score) {
		int oldCount = rantCount;
		float oldScore = rantScore;
		rantCount = count;
		rantScore = score;

		support.firePropertyChange("rantCount", oldCount, rantCount);
		support.firePropertyChange("rantScore", oldScore, rantScore);
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

	public String getBirthday() {
		String result = "";
		if (birthdayMonth != -1 && birthdayDay != -1)
			result = birthdayMonth + "/" + birthdayDay;
		return result;
	}

	public int getRantActivation() {
		return rantActivation;
	}

	public void setRantActivation(int rantActivation) {
		int oldCount = this.rantActivation;
		this.rantActivation = rantActivation;
		support.firePropertyChange("rantActivation", oldCount, rantActivation);
	}

	public void victimize(BotCommand cmd) {
		setVictimCount(victimCount + 1);
	}

	public void setSpellCount(int spellCount) {
		int oldCount = spellCount;
		this.spellCount = spellCount;
		support.firePropertyChange("spellCount", oldCount, spellCount);
	}

	public void setVictimCount(int victimCount) {
		int oldCount = victimCount;
		this.victimCount = victimCount;
		support.firePropertyChange("victimCount", oldCount, victimCount);
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

		setBalls(this.ballsCount + other.ballsCount);
		setVictimCount(victimCount + other.victimCount);
		setSpellCount(spellCount + other.spellCount);
		setRant(rantCount + other.rantCount, rantScore + other.rantScore);
		setRantActivation(rantActivation + other.rantActivation);
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

	public void setBalls(Integer bcInt) {
		int oldVal = ballsCount;
		ballsCount = bcInt;
		support.firePropertyChange("ballsCount", oldVal, ballsCount);
	}

	public void setLastUpdate(Long lastUpdate) {
		long oldLastUpdate = this.lastUpdate;
		this.lastUpdate = lastUpdate;
		support.firePropertyChange("lastUpdate", oldLastUpdate, lastUpdate);
	}

	public void decrement(BotCommand spell) {
		long now = System.currentTimeMillis();

		switch (spell) {
		case MBIYF:
		case MBIYFDipshit:
			setBalls(ballsCount - 1);
			setLastUpdate(now);
			break;

		case Spell:
			setSpellCount(spellCount - 1);
			setLastUpdate(now);
			break;
		case NicknameConversion:
		case AllBalls:
		case AllFaces:
		case AllNicknames:
		case Countdown:
		case RantActivatorInitiation:
		case AitaActivatorInitiation:
		case VoteActivatorComplete:
		case VoteTopicInitiation:
		case VoteTopicComplete:
		case VoteInitiationFailed:
		case Vote:
		case Reload:
		case SetNickname:
			// case Stats:
			// case StatsEndOfWeek:
			// case Test:
		case PartyEveryday:
		case PleasureModel:
		case RegularChatReply:
		case RegularChatUpdate:
		case SetNicknameRejected:
		case SpellDipshit:
		case UiString:
			// case RegularChatAnimation:
		case RegularChatEdit:
		case RegularChatVideo:
		case RegularChatGif:
		case RegularChatPhoto:
		case RegularChatSticker:
			break;
		}
	}

	public void increment(BotCommand spell) {
		long now = System.currentTimeMillis();

		switch (spell) {
		case MBIYF:
			setBalls(ballsCount + 1);
			setLastUpdate(now);
			break;
		case MBIYFDipshit:
		case Spell:
			setSpellCount(spellCount + 1);
			setLastUpdate(now);
			break;
		case VoteActivatorComplete:
			setRantActivation(rantActivation + 1);
			break;
		case AllBalls:
		case AllFaces:
		case AllNicknames:
		case Countdown:
		case NicknameConversion:
		case RantActivatorInitiation:
		case AitaActivatorInitiation:
			// you don't get credit unless it gets completed as a non-rant.
		case VoteTopicInitiation:
		case VoteTopicComplete:
		case VoteInitiationFailed:
		case Vote:
		case Reload:
		case SetNickname:
			// case Stats:
			// case StatsEndOfWeek:
			// case Test:
		case PleasureModel:
		case PartyEveryday:
		case RegularChatReply:
		case RegularChatUpdate:
		case SetNicknameRejected:
		case SpellDipshit:
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
		setBalls(0);
		setRant(0, 0);
		setRantActivation(0);
		setVictimCount(0);
		setSpellCount(0);
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

	public float getScore() {
		return ballsCount + rantScore + spellCount + ((victimCount + rantActivation) / 2f);
	}

}
