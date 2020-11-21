package ca.hapke.campingbot.api;

import ca.hapke.campingbot.xml.OutputFormatter;

/**
 * @author Nathan Hapke
 */
public interface CampingSerializable {
	public boolean shouldSave();

	public void getXml(OutputFormatter of);

}
