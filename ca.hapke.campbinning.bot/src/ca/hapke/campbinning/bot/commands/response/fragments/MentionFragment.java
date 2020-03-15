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

	public MentionFragment(CampingUser target) {
		super(CaseChoice.Normal, TextStyle.Normal);
		this.target = target;
	}

	public MentionFragment(CampingUser target, CaseChoice caseChoice, String prefix, String suffix) {
		super(caseChoice, TextStyle.Normal);
		this.target = target;
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

		display = casify(display);
		display = processor.processString(display);
		display = markup(display);

		int telegramId = target.getTelegramId();

		return "[" + display + "](tg://user?id=" + telegramId + ")";
	}

}
