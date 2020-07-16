package ca.hapke.campbinning.bot;

import java.io.File;

/**
 * @author Nathan Hapke
 */
public interface ConfigSerializer {

	public File save();

	public boolean load();

}