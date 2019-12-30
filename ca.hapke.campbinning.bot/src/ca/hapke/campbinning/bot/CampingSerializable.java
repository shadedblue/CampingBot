package ca.hapke.campbinning.bot;

import ca.hapke.campbinning.bot.xml.OutputFormatter;

/**
 * @author Nathan Hapke
 */
public abstract class CampingSerializable {
	protected boolean shouldSave = false;

	public abstract void getXml(OutputFormatter of);

}
