package ca.hapke.campbinning.bot.commands.response.fragments;

import ca.hapke.campbinning.bot.commands.response.MessageProcessor;

/**
 * @author Nathan Hapke
 */
public abstract class ResultFragment {

	public abstract String getValue(MessageProcessor processor);
}
