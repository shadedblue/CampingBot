package ca.hapke.campingbot.xml;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.ProtectionDomain;

import ca.hapke.campingbot.api.ConfigSerializer;

/**
 * @author Nathan Hapke
 */
public abstract class AbstractLoader implements ConfigSerializer {

	protected static final String CHARSET_TO_USE = "UTF-16";
	protected ProtectionDomain protectionDomain;

	public AbstractLoader(ProtectionDomain protectionDomain) {
		this.protectionDomain = protectionDomain;
	}

	public File getFileNotInBinFolder(String fn) throws IOException {
		return getFileNotInBinFolder(protectionDomain, fn);
	}

	public static File getFileNotInBinFolder(ProtectionDomain protectionDomain, String fn) throws IOException {
		URL url = protectionDomain.getCodeSource().getLocation();

		File f;
		File dir = new File(url.getFile());
		int i = 0;
		while (i < 100) {
			f = new File(dir.getAbsolutePath() + File.separatorChar + fn);
			if (f.exists())
				return f;
			dir = dir.getParentFile();
			if (dir == null || !dir.canRead())
				return null;
			i++;
		}
		System.err.println("File not found");
		return null;
	}
}