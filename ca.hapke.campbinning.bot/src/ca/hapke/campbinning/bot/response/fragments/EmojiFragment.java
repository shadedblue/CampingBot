package ca.hapke.campbinning.bot.response.fragments;

import com.vdurmont.emoji.Emoji;

import ca.hapke.campbinning.bot.processors.MessageProcessor;

/**
 * @author Nathan Hapke
 */
public class EmojiFragment extends ResultFragment {
	private Emoji target;

	public EmojiFragment(Emoji target) {
		super(CaseChoice.Normal, TextStyle.Normal);
		this.target = target;
	}

	@Override
	public String getValue(MessageProcessor processor, boolean useMarkupV2) {
		return target.getUnicode();
	}

}
