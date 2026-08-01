package ca.hapke.campingbot.util;

/**
 * @author Nathan Hapke
 */
public interface JobDetails {
	public int getNumSteps();

	public int getNumAttempts(int step);

	public boolean isRequireCompletion(int step);

	public int getDelay(int step);

	/**
	 * @return completed this step?
	 */
	public boolean doStep(int step, int attempt);

	public boolean shouldAbort();
}
