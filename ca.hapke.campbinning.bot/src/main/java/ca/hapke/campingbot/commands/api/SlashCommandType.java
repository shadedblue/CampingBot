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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SlashCommandType [");
		if (prettyName != null) {
			builder.append(prettyName);
		}
		if (slashCommand != null) {
			builder.append(" /");
			builder.append(slashCommand);
		}
		builder.append(" id=");
		builder.append(Long.toHexString(id));
		builder.append("]");
		return builder.toString();
	}
}
