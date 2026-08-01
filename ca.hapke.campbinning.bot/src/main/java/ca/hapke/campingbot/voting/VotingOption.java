package ca.hapke.campingbot.voting;

/**
 * @author Nathan Hapke
 *
 */
public class VotingOption<T> {
	public final String shortButton, longDescription;
	public final T value;

	public VotingOption(String shortButton, String longDescription, T value) {
		this.shortButton = shortButton;
		this.longDescription = longDescription;
		this.value = value;
	}
}
