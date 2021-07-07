package ca.hapke.campingbot.xml;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.security.ProtectionDomain;

import com.javadude.antxr.scanner.BasicCrimsonXMLTokenStream;

import ca.hapke.campingbot.CampingSystem;

/**
 * @author Nathan Hapke
 */
public class ConfigLoader extends AbstractLoader {

	private CampingSystem cs;
	private static final String SETTINGS_FILENAME = "settings.xml";

	public ConfigLoader(ProtectionDomain protectionDomain, CampingSystem cs) {
		super(protectionDomain);
		this.cs = cs;
	}

	@Override
	public boolean load() {
		try {
			File f = getFileNotInBinFolder(protectionDomain, SETTINGS_FILENAME);
			BasicCrimsonXMLTokenStream stream1 = new BasicCrimsonXMLTokenStream(
					new FileReader(f, Charset.forName(CHARSET_TO_USE)), ConfigParser.class, false, false);
			ConfigParser conf = new ConfigParser(stream1);
			conf.document(cs);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
