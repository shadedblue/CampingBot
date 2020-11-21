package ca.hapke.campingbot.api;

import java.io.File;

/**
 * @author Nathan Hapke
 */
public interface ConfigSerializer {

	public File save();

	public boolean load();

}