package ca.hapke.campbinning.bot.commands.response;

import java.util.List;

import ca.hapke.campbinning.bot.commands.response.fragments.ResultFragment;

/**
 * Visitor pattern? Fragments call back to this with the parts that should be processed... then the fragments
 * re-assemble an output value
 * 
 * @author Nathan Hapke
 */
public abstract class MessageProcessor {

	protected MessageProcessor next;

	public final ResultFragment[] beforeStringAssembled(ResultFragment[] fragments) {
		fragments = internalBeforeStringAssembled(fragments);
		if (next != null)
			fragments = next.beforeStringAssembled(fragments);
		return fragments;
	}

	public final String processString(String value, boolean useMarkupV2) {
		String result = internalProcessStringFragment(value, useMarkupV2);
		if (next != null)
			result = next.processString(result, useMarkupV2);
		return result;
	}

	public final String afterStringAssembled(String value) {
		String result = internalAfterStringAssembled(value);
		if (next != null)
			result = next.afterStringAssembled(result);
		return result;
	}

	public final String processImageUrl(String url) {
		String result = internalProcessImageUrl(url);
		if (next != null)
			result = next.processImageUrl(url);
		return result;
	}

	protected abstract ResultFragment[] internalBeforeStringAssembled(ResultFragment[] fragments);

	protected abstract String internalProcessStringFragment(String value, boolean useMarkupV2);

	protected abstract String internalAfterStringAssembled(String value);

	protected abstract String internalProcessImageUrl(String url);

	public MessageProcessor getNext() {
		return next;
	}

	public MessageProcessor addAtEnd(MessageProcessor add) {
		MessageProcessor end = this;
		while (end.next != null) {
			end = end.next;
		}
		end.next = add;
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
