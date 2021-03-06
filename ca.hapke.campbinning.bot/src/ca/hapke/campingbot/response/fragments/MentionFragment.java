package ca.hapke.campingbot.response.fragments;

import ca.hapke.campingbot.processors.MessageProcessor;
import ca.hapke.campingbot.users.CampingUser;

/**
 * If Target is not in the channel, it won't display on the client. Not my fault.
 * 
 * @author Nathan Hapke
 */
public class MentionFragment extends ResultFragment {
	private CampingUser target;
	private String prefix, suffix;
	private String display;

	public MentionFragment(CampingUser target) {
		this(target, MentionDisplay.Nickname, CaseChoice.Normal, TextStyle.Normal, null, null);
	}

	public MentionFragment(CampingUser target, TextStyle textStyle) {
		this(target, MentionDisplay.Nickname, CaseChoice.Normal, textStyle, null, null);
	}

	public MentionFragment(CampingUser target, CaseChoice caseChoice) {
		this(target, MentionDisplay.Nickname, caseChoice, TextStyle.Normal, null, null);
	}

	public MentionFragment(CampingUser target, MentionDisplay displayMode, CaseChoice caseChoice) {
		this(target, displayMode, caseChoice, TextStyle.Normal, null, null);
	}

	public MentionFragment(CampingUser target, MentionDisplay displayMode, TextStyle textStyle) {
		this(target, displayMode, CaseChoice.Normal, textStyle, null, null);
	}

	public MentionFragment(CampingUser target, MentionDisplay displayMode, CaseChoice caseChoice, TextStyle textStyle,
			String prefix, String suffix) {
		super(caseChoice, textStyle);
		this.target = target;
		this.prefix = prefix;
		this.suffix = suffix;

		switch (displayMode) {
		case Nickname:
			display = target.getDisplayName();
			break;
		case Username:
			display = target.getUsername();
			break;
		case Initials:
			String initials = target.getInitials();
			if (initials.length() > 0) {
				display = '@' + initials;
				break;
			}
		case First:
		default:
			display = target.getFirstOrUserName();
			break;
		}
	}

	/**
	 * Only for calling by transform();
	 */
	private MentionFragment(CampingUser target, String transformedDisplay, CaseChoice caseChoice, TextStyle textStyle,
			String prefix, String suffix) {
		super(caseChoice, textStyle);
		this.target = target;
		this.prefix = prefix;
		this.suffix = suffix;
		this.display = transformedDisplay;
	}

	@Override
	public String getValue(MessageProcessor processor, boolean useMarkupV2) {
		String display = getDisplayText();
		if (prefix != null)
			display = prefix + display;
		if (suffix != null)
			display = display + suffix;

		display = casify(display);
		display = processor.processString(display, useMarkupV2);
		display = markup(display);

		if (useMarkupV2) {
			long telegramId = target.getTelegramId();
			return "[" + display + "](tg://user?id=" + telegramId + ")";
		} else {
			return display;
		}
	}

	public String getDisplayText() {
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

	@Override
	public ResultFragment transform(MessageProcessor proc, boolean useMarkupV2) {
		String transformedDisplay = proc.processString(display, useMarkupV2);

		return new MentionFragment(target, transformedDisplay, caseChoice, textStyle, prefix, suffix);
	}

}
