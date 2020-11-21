package ca.hapke.campingbot.commands.api;

/**
 * @author Nathan Hapke
 */
public class SlashCommandType extends ResponseCommandType {

	public final String slashCommand;

	public SlashCommandType(String prettyName, String slashCommand, long id) {
		super(prettyName, id);
		this.slashCommand = slashCommand;
	}
}
