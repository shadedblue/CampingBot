package ca.hapke.campbinning.bot.commands.response.fragments;

import com.vdurmont.emoji.Emoji;

import ca.hapke.campbinning.bot.commands.response.MessageProcessor;

/**
 * @author Nathan Hapke
 */
public class EmojiFragment extends ResultFragment {
	private Emoji target;

	public EmojiFragment(Emoji target) {
		this.target = target;
	}

	@Override
	/**
	 * No changes to emoji.. for now?
	 */
	public String getValue(MessageProcessor processor) {
		return target.getUnicode().trim();
	}

}
