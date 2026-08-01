package ca.hapke.campingbot.afd2021;

/**
 * @author Nathan Hapke
 */
public interface IStage {

	boolean add(StageListener e);

	boolean remove(StageListener e);

	void begin();

}