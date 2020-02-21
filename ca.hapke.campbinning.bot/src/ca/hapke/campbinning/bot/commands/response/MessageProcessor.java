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
		if (fragments == null)
			return null;
		int size = fragments.size();
		String[] results = new String[size];
		for (int i = 0; i < size; i++) {
			ResultFragment f = fragments.get(i);
			if (f == null)
				results[i] = "";
			else
				results[i] = f.getValue(this);
		}

		return String.join("", results);
	}

	public String process(ResultFragment[] fragments) {
		if (fragments == null)
			return null;
		int size = fragments.length;
		String[] results = new String[size];
		for (int i = 0; i < size; i++) {
			ResultFragment f = fragments[i];
			if (f == null)
				results[i] = "";
			else
				results[i] = f.getValue(this);
		}

		return String.join("", results);
	}
}
