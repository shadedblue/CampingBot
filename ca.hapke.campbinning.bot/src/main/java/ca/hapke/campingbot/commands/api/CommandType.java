package ca.hapke.campingbot.commands.api;

/**
 * @author Nathan Hapke
 */
public abstract class CommandType {
	public final long id;
	public final String prettyName;

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
