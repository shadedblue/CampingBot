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

	public final String processString(String value) {
		String result = internalProcessString(value);
		if (next != null)
			result = next.internalProcessString(result);
		return result;
	}

	protected abstract String internalProcessString(String value);

	public MessageProcessor getNext() {
		return next;
	}

	public MessageProcessor pushNext(MessageProcessor add) {
		add.next = next;
		next = add;
		return this;
	}

	public MessageProcessor addAtEnd(MessageProcessor add) {
		MessageProcessor end = this;
		while (end.next != null) {
			end = end.next;
		}
		end.next = add;
		return this;
	}

	public String process(List<ResultFragment> fragments) {
		String[] results = new String[fragments.size()];
		for (int i = 0; i < fragments.size(); i++) {
			results[i] = fragments.get(i).getValue(this);
		}

		return String.join("", results);
	}

	public String process(ResultFragment[] fragments) {
		String[] results = new String[fragments.length];
		for (int i = 0; i < fragments.length; i++) {
			results[i] = fragments[i].getValue(this);
		}

		return String.join("", results);
	}
}
