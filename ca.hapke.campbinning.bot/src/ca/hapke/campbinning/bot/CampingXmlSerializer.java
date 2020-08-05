package ca.hapke.campbinning.bot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.time.temporal.ChronoUnit;

import com.javadude.antxr.scanner.BasicCrimsonXMLTokenStream;

import ca.hapke.calendaring.event.CalendaredEvent;
import ca.hapke.calendaring.event.StartupMode;
import ca.hapke.calendaring.timing.ByFrequency;
import ca.hapke.calendaring.timing.TimesProvider;
import ca.hapke.campbinning.bot.channels.CampingChatManager;
import ca.hapke.campbinning.bot.commands.CountdownGenerator;
import ca.hapke.campbinning.bot.commands.EnhanceCommand;
import ca.hapke.campbinning.bot.commands.PartyEverydayCommand;
import ca.hapke.campbinning.bot.commands.SpellGenerator;
import ca.hapke.campbinning.bot.commands.response.InsultGenerator;
import ca.hapke.campbinning.bot.commands.voting.aita.AitaCommand;
import ca.hapke.campbinning.bot.users.CampingUserMonitor;
import ca.hapke.campbinning.bot.xml.LoadStatsParser;
import ca.hapke.campbinning.bot.xml.OutputFormatter;

/**
 * @author Nathan Hapke
 */
public class CampingXmlSerializer implements CalendaredEvent<Void>, ConfigSerializer {
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
	private static final String FILENAME = "camping.xml";
	private CampingSerializable[] serializables;
	private CampingSystem cs;
	private SpellGenerator sg;
	private CountdownGenerator countdownGen;
	private AitaCommand aita;
	private CampingUserMonitor um;
	private PartyEverydayCommand pc;
	private CampingChatManager cm;
	private InsultGenerator ig;
	private EnhanceCommand ec;

	public CampingXmlSerializer(CampingSystem cs, SpellGenerator sg, CountdownGenerator countdownGen, AitaCommand aita,
			PartyEverydayCommand partyCommand, CampingChatManager cm, CampingUserMonitor um, InsultGenerator ig,
			EnhanceCommand ec) {
		this.cs = cs;
		this.sg = sg;
		this.countdownGen = countdownGen;
		this.aita = aita;
		this.pc = partyCommand;
		this.cm = cm;
		this.um = um;
		this.ig = ig;
		this.ec = ec;
		this.serializables = new CampingSerializable[] { cs, sg, countdownGen, aita, pc, ig, ec, cm, um };
		times = new TimesProvider<Void>(new ByFrequency<Void>(null, 1, ChronoUnit.MINUTES));
	}

	@Override
	public File save() {
		return save(FILENAME);
	}

	public File save(String fn) {
		try {
			File f = getFileNotInBinFolder(fn);
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

	public static File getFileNotInBinFolder(String fn) throws IOException {
		File f = new File(fn);
		File dir = f.getCanonicalFile().getParentFile();
		boolean remakeF = false;
		while (dir.isDirectory() && dir.getName().equalsIgnoreCase("bin")) {
			dir = dir.getParentFile();
			remakeF = true;
		}
		if (remakeF) {
			f = new File(dir.getAbsolutePath() + File.pathSeparator + fn);
		}
		return f;
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
			File f = getFileNotInBinFolder(fn);
			return load(f);
		} catch (Exception ex) {
		}

		return false;
	}

	public boolean load(File f) throws IOException, ClassNotFoundException {
		BasicCrimsonXMLTokenStream stream = new BasicCrimsonXMLTokenStream(
				new FileReader(f, Charset.forName(CHARSET_TO_USE)), LoadStatsParser.class, false, false);
		LoadStatsParser parser = new LoadStatsParser(stream);
		try {
			parser.document(cs, sg, countdownGen, aita, pc, cm, um, ig, ec);
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
