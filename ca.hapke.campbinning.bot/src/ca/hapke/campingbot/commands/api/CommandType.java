package ca.hapke.campingbot.commands.api;

/**
 * @author Nathan Hapke
 */
public abstract class CommandType {
	protected final long id;
	protected final String prettyName;

	public CommandType(String prettyName, long id) {
		this.prettyName = prettyName;
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public String getPrettyName() {
		return prettyName;
	}

	public abstract boolean isForDb();
}
