package ca.hapke.campbinning.bot;

import ca.hapke.campbinning.bot.xml.OutputFormatter;

/**
 * @author Nathan Hapke
 */
public interface CampingSerializable {
	public boolean shouldSave();

	public void getXml(OutputFormatter of);

}
