package ca.hapke.campbinning.bot.processors;

import java.util.List;

import ca.hapke.campbinning.bot.response.fragments.ResultFragment;

/**
 * Visitor pattern? Fragments call back to this with the parts that should be processed... then the fragments
 * re-assemble an output value
 * 
 * @author Nathan Hapke
 */
public abstract class MessageProcessor {

	protected MessageProcessor next;
	protected boolean enabled = false;

	public MessageProcessor(boolean enabled) {
		this.enabled = enabled;
	}

	public final ResultFragment[] beforeStringAssembled(ResultFragment[] fragments) {
		if (enabled)
			fragments = internalBeforeStringAssembled(fragments);
		if (next != null)
			fragments = next.beforeStringAssembled(fragments);
		return fragments;
	}

	public final String processString(String value, boolean useMarkupV2) {
		if (enabled)
			value = internalProcessStringFragment(value, useMarkupV2);
		if (next != null)
			value = next.processString(value, useMarkupV2);
		return value;
	}

	public final String afterStringAssembled(String value) {
		if (enabled)
			value = internalAfterStringAssembled(value);
		if (next != null)
			value = next.afterStringAssembled(value);
		return value;
	}

	public final String processImageUrl(String url) {
		if (enabled)
			url = internalProcessImageUrl(url);
		if (next != null)
			url = next.processImageUrl(url);
		return url;
	}

	protected ResultFragment[] internalBeforeStringAssembled(ResultFragment[] fragments) {
		return fragments;
	}

	protected String internalProcessStringFragment(String value, boolean useMarkupV2) {
		return value;
	}

	protected String internalAfterStringAssembled(String value) {
		return value;
	}

	protected String internalProcessImageUrl(String url) {
		return url;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public MessageProcessor addAtEnd(MessageProcessor add) {
		if (add != null) {
			MessageProcessor end = this;
			while (end.next != null) {
				end = end.next;
			}
			end.next = add;
		}
		return this;
	}

	public String process(List<ResultFragment> fragments, boolean useMarkupV2) {
		ResultFragment[] a = new ResultFragment[fragments.size()];
		a = fragments.toArray(a);
		return process(a, useMarkupV2);
	}

	public String process(ResultFragment[] fragments, boolean useMarkupV2) {
		if (fragments == null)
			return null;

		fragments = beforeStringAssembled(fragments);

		int size = fragments.length;
		String[] results = new String[size];
		for (int i = 0; i < size; i++) {
			ResultFragment f = fragments[i];
			if (f == null)
				results[i] = "";
			else
				results[i] = f.getValue(this, useMarkupV2);
		}

		String result = String.join("", results);

		result = afterStringAssembled(result);
		return result;
	}

}
