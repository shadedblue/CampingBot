package ca.hapke.campbinning.bot.commands.processors;

import ca.hapke.campbinning.bot.commands.response.fragments.ResultFragment;

/**
 * @author Nathan Hapke
 */
public class SwitchableProcessor extends MessageProcessor {

	public SwitchableProcessor(boolean e, MessageProcessor p) {
		super(e);
		pipe = p;
	}

	private MessageProcessor pipe;

	public MessageProcessor getPipe() {
		return pipe;
	}

	@Override
	protected ResultFragment[] internalBeforeStringAssembled(ResultFragment[] fragments) {
		if (enabled) {
			fragments = pipe.beforeStringAssembled(fragments);
		}
		return fragments;
	}

	@Override
	protected String internalAfterStringAssembled(String value) {
		if (enabled) {
			value = pipe.afterStringAssembled(value);
		}
		return value;
	}

	@Override
	protected String internalProcessImageUrl(String url) {
		if (enabled) {
			url = pipe.processImageUrl(url);
		}
		return url;
	}

	@Override
	protected String internalProcessStringFragment(String value, boolean useMarkupV2) {
		if (enabled) {
			value = pipe.processString(value, useMarkupV2);
		}
		return value;
	}
}
