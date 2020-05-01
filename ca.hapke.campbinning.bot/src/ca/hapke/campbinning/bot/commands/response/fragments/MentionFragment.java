package ca.hapke.campbinning.bot.commands.response.fragments;

import ca.hapke.campbinning.bot.commands.response.MessageProcessor;
import ca.hapke.campbinning.bot.users.CampingUser;

/**
 * If Target is not in the channel, it won't display on the client. Not my fault.
 * 
 * @author Nathan Hapke
 */
public class MentionFragment extends ResultFragment {
	private CampingUser target;
	private String prefix, suffix;
	private MentionDisplay displayMode;

	public MentionFragment(CampingUser target) {
		this(target, MentionDisplay.Nickname, CaseChoice.Normal, null, null);
	}

	public MentionFragment(CampingUser target, MentionDisplay displayMode, CaseChoice caseChoice, String prefix,
			String suffix) {
		super(caseChoice, TextStyle.Normal);
		this.target = target;
		this.displayMode = displayMode;
		this.prefix = prefix;
		this.suffix = suffix;
	}

	@Override
	public String getValue(MessageProcessor processor) {
		String display;
		display = getDisplayText();
		if (prefix != null)
			display = prefix + display;
		if (suffix != null)
			display = display + suffix;

		display = casify(display);
		display = processor.processString(display);
		display = markup(display);

		int telegramId = target.getTelegramId();

		return "[" + display + "](tg://user?id=" + telegramId + ")";
	}

	public String getDisplayText() {
		String display;
		switch (displayMode) {
		case Nickname:
			display = target.getDisplayName();
			break;
		case Username:
			display = target.getUsername();
			break;
		case First:
		default:
			display = target.getFirstOrUserName();
			break;

		}
		return display;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Mention[");
		builder.append(target.getUsername());
		builder.append(" => ");
		builder.append(getDisplayText());
		builder.append("]");
		return builder.toString();
	}

}
