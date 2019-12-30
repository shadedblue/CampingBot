package ca.hapke.campbinning.bot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.javadude.antxr.scanner.BasicCrimsonXMLTokenStream;

import ca.hapke.campbinning.bot.commands.CountdownGenerator;
import ca.hapke.campbinning.bot.commands.SpellGenerator;
import ca.hapke.campbinning.bot.interval.IntervalBySeconds;
import ca.hapke.campbinning.bot.users.CampingUserMonitor;
import ca.hapke.campbinning.bot.xml.LoadStatsParser;
import ca.hapke.campbinning.bot.xml.OutputFormatter;

/**
 * @author Nathan Hapke
 */
public class CampingXmlSerializer implements IntervalBySeconds {

	@Override
	public int getSeconds() {
		return 60;
	}

	private boolean shouldSave = false;

	@Override
	public void doWork() {
		if (shouldSave) {
			File file = save();
			if (file != null)
				shouldSave = false;
		}
	}

	@Override
	public boolean shouldRun() {
		for (CampingSerializable s : serializables) {
			if (s.shouldSave) {
				shouldSave = true;
				break;
			}
		}
		return shouldSave;
	}

	private static final String FILENAME = "camping.xml";
	private CampingSerializable[] serializables;
	// private SundayStatsReset sundayStats;
	private CampingSystem cs;
	private SpellGenerator sg;
	private CountdownGenerator countdownGen;
	private CampingUserMonitor um;

	public CampingXmlSerializer(CampingSystem cs, // SundayStatsReset
													// sundayStats,
			SpellGenerator sg, CountdownGenerator countdownGen, CampingUserMonitor um) {
		this.cs = cs;
		// this.sundayStats = sundayStats;
		this.sg = sg;
		this.countdownGen = countdownGen;
		this.um = um;
		this.serializables = new CampingSerializable[] {
				// sundayStats,
				cs, sg, countdownGen, um };
	}

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
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
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
		BasicCrimsonXMLTokenStream stream = new BasicCrimsonXMLTokenStream(new FileReader(f), LoadStatsParser.class,
				false, false);
		LoadStatsParser parser = new LoadStatsParser(stream);
		try {
			parser.document(cs,
					// sundayStats,
					sg, countdownGen, um);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
