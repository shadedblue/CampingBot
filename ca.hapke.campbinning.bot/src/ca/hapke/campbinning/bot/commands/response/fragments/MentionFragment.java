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
	private CaseChoice caseChoice;
	private String prefix, suffix;

	public MentionFragment(CampingUser target) {
		this.target = target;
		this.caseChoice = CaseChoice.Normal;
	}

	public MentionFragment(CampingUser target, CaseChoice caseChoice, String prefix, String suffix) {
		this.target = target;
		this.caseChoice = caseChoice;
		this.prefix = prefix;
		this.suffix = suffix;
	}

	@Override
	public String getValue(MessageProcessor processor) {
		String display = target.getDisplayName();
		if (prefix != null)
			display = prefix + display;
		if (suffix != null)
			display = display + suffix;

		switch (caseChoice) {
		case Normal:
			// noop
			break;
		case Lower:
			display = display.toLowerCase();
			break;
		case Upper:
			display = display.toUpperCase();
			break;

		}
		int telegramId = target.getTelegramId();

		display = processor.processString(display);
		return "[" + display + "](tg://user?id=" + telegramId + ")";
	}

}
