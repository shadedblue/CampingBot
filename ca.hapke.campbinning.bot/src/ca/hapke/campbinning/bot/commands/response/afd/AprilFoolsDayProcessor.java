package ca.hapke.campbinning.bot.commands.response.afd;

import ca.hapke.campbinning.bot.commands.response.MessageProcessor;
import ca.hapke.campbinning.bot.commands.response.fragments.ResultFragment;

/**
 * @author Nathan Hapke
 */
public class AprilFoolsDayProcessor extends MessageProcessor {

	private boolean enabled = false;
	private MessageProcessor sillyPipe;

	public MessageProcessor getSillyPipe() {
		return sillyPipe;
	}

	public AprilFoolsDayProcessor(boolean highPower) {
		sillyPipe = new CharacterRepeater(highPower).addAtEnd(new FontGarbler(highPower));
	}

	@Override
	protected ResultFragment[] internalBeforeStringAssembled(ResultFragment[] fragments) {
		if (enabled) {
			fragments = sillyPipe.beforeStringAssembled(fragments);
		}
		return fragments;
	}

	@Override
	protected String internalAfterStringAssembled(String value) {
		if (enabled) {
			value = sillyPipe.afterStringAssembled(value);
		}
		return value;
	}

	@Override
	protected String internalProcessImageUrl(String url) {
		if (enabled) {
			url = sillyPipe.processImageUrl(url);
		}
		return url;
	}

	@Override
	protected String internalProcessStringFragment(String value, boolean useMarkupV2) {
		if (enabled) {
			value = sillyPipe.processString(value, useMarkupV2);
		}
		return value;
	}

	public void enable(boolean on) {
		enabled = on;
	}
}
