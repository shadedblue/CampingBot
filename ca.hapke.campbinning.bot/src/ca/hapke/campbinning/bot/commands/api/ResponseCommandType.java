package ca.hapke.campbinning.bot.commands.api;

/**
 * @author Nathan Hapke
 */
public class ResponseCommandType extends CommandType {

	public ResponseCommandType(String prettyName, long id) {
		super(prettyName, id);
	}

	@Override
	public boolean isForDb() {
		return true;
	}

}