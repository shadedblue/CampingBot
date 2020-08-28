package ca.hapke.campbinning.bot.commands.api;

/**
 * @author Nathan Hapke
 */
public class SlashCommandType extends ResponseCommandType {

	public final String slashCommand;

	public SlashCommandType(String prettyName, String slashCommand, long id) {
		super(prettyName, id);
		this.slashCommand = slashCommand;
	}

	@Override
	public long getId() {
		return id;
	}
}
