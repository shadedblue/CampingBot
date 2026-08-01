package ca.hapke.campingbot.afd2021;

/**
 * @author Nathan Hapke
 */
public interface StageListener {
	public void stageBegan();

	public void stageComplete(boolean success);
}
