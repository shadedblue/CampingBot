package ca.hapke.campbinning.bot.commands.response.fragments;

import ca.hapke.campbinning.bot.commands.response.MessageProcessor;
import ca.hapke.campbinning.bot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class MentionFragment extends ResultFragment {
	private CampingUser target;

	public MentionFragment(CampingUser target) {
		this.target = target;
	}

	@Override
	public String getValue(MessageProcessor processor) {
		String display = target.getDisplayName();
		int telegramId = target.getTelegramId();

		display = processor.processString(display);
		return "[" + display + "](tg://user?id=" + telegramId + ")";
	}

}
