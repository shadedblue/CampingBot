package ca.hapke.campingbot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.ProtectionDomain;
import java.time.temporal.ChronoUnit;

import com.javadude.antxr.scanner.BasicCrimsonXMLTokenStream;

import ca.hapke.calendaring.event.CalendaredEvent;
import ca.hapke.calendaring.event.StartupMode;
import ca.hapke.calendaring.timing.ByFrequency;
import ca.hapke.calendaring.timing.TimesProvider;
import ca.hapke.campingbot.api.CampingSerializable;
import ca.hapke.campingbot.api.ConfigSerializer;
import ca.hapke.campingbot.channels.CampingChatManager;
import ca.hapke.campingbot.commands.EnhanceCommand;
import ca.hapke.campingbot.commands.HypeCommand;
import ca.hapke.campingbot.commands.PartyEverydayCommand;
import ca.hapke.campingbot.commands.SpellCommand;
import ca.hapke.campingbot.response.InsultGenerator;
import ca.hapke.campingbot.users.CampingUserMonitor;
import ca.hapke.campingbot.xml.ConfigParser;
import ca.hapke.campingbot.xml.OutputFormatter;

/**
 * @author Nathan Hapke
 */
public class ConfigXmlSerializer implements CalendaredEvent<Void>, ConfigSerializer {
	private TimesProvider<Void> times;
	private boolean shouldSave = false;

	@Override
	public void doWork(Void value) {
		if (shouldSave) {
			File file = save();
			if (file != null)
				shouldSave = false;
		}
	}

	@Override
	public boolean shouldRun() {
		for (CampingSerializable s : serializables) {
			if (s.shouldSave()) {
				shouldSave = true;
				break;
			}
		}
		return shouldSave;
	}

	private static final String CHARSET_TO_USE = "UTF-16";
	private static final String FILENAME = "config.xml";
	private CampingSerializable[] serializables;
	private CampingSystem cs;
	private SpellCommand sg;
	private HypeCommand hype;
	private CampingUserMonitor um;
	private PartyEverydayCommand pc;
	private CampingChatManager cm;
	private InsultGenerator ig;
	private EnhanceCommand ec;
	private ProtectionDomain protectionDomain;

	public ConfigXmlSerializer(ProtectionDomain protectionDomain, CampingSystem cs, SpellCommand sg, HypeCommand hype,
			PartyEverydayCommand partyCommand, CampingChatManager cm, CampingUserMonitor um, InsultGenerator ig,
			EnhanceCommand ec) {
		this.protectionDomain = protectionDomain;
		this.cs = cs;
		this.sg = sg;
		this.hype = hype;
		this.pc = partyCommand;
		this.cm = cm;
		this.um = um;
		this.ig = ig;
		this.ec = ec;
		this.serializables = new CampingSerializable[] { cs, sg, hype, pc, ig, ec, cm, um };
		times = new TimesProvider<Void>(new ByFrequency<Void>(null, 1, ChronoUnit.MINUTES));
	}

	@Override
	public File save() {
		return save(FILENAME);
	}

	public File save(String fn) {
		try {
			File f = getFileNotInBinFolder(protectionDomain, fn);
			if (f.exists()) {
				File backup = new File(getBackupFilename(fn));
				if (backup.exists())
					backup.delete();
				f.renameTo(backup);
			}
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), CHARSET_TO_USE));
			OutputFormatter of = new OutputFormatter();

			String camping = "camping";
			of.start(camping);
			for (CampingSerializable s : serializables) {
				s.getXml(of);
			}
			of.finish(camping);

			output.write(of.output());
			output.flush();
			output.close();
			return f;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private String getBackupFilename(String fn) {
		return fn + ".backup";
	}

	public static File getFileNotInBinFolder(ProtectionDomain protectionDomain, String fn) throws IOException {
//		ProtectionDomain protectionDomain = CampingXmlSerializer.class.getProtectionDomain();
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

	@Override
	public boolean load() {
		boolean result = load(FILENAME);
		if (!result)
			result = load(getBackupFilename(FILENAME));
		return result;
	}

	public boolean load(String fn) {
		try {
			File f = getFileNotInBinFolder(protectionDomain, fn);
			return load(f);
		} catch (Exception ex) {
		}

		return false;
	}

	public boolean load(File f) throws IOException, ClassNotFoundException {
		BasicCrimsonXMLTokenStream stream = new BasicCrimsonXMLTokenStream(
				new FileReader(f, Charset.forName(CHARSET_TO_USE)), ConfigParser.class, false, false);
		ConfigParser parser = new ConfigParser(stream);
		try {
			parser.document(cs, sg, hype, pc, cm, um, ig, ec);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public TimesProvider<Void> getTimeProvider() {
		return times;
	}

	@Override
	public StartupMode getStartupMode() {
		return StartupMode.Never;
	}
}
