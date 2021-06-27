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
import java.util.List;

import javax.persistence.EntityManager;

import com.javadude.antxr.scanner.BasicCrimsonXMLTokenStream;

import ca.hapke.calendaring.event.CalendaredEvent;
import ca.hapke.calendaring.event.StartupMode;
import ca.hapke.calendaring.timing.ByCalendar;
import ca.hapke.calendaring.timing.ByFrequency;
import ca.hapke.calendaring.timing.TimesProvider;
import ca.hapke.campingbot.api.CampingSerializable;
import ca.hapke.campingbot.api.ConfigSerializer;
import ca.hapke.campingbot.api.PostConfigInit;
import ca.hapke.campingbot.channels.CampingChatManager;
import ca.hapke.campingbot.commands.EnhanceCommand;
import ca.hapke.campingbot.commands.HypeCommand;
import ca.hapke.campingbot.commands.PartyEverydayCommand;
import ca.hapke.campingbot.commands.spell.SpellCommand;
import ca.hapke.campingbot.log.DatabaseConsumer;
import ca.hapke.campingbot.response.InsultGenerator;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.users.CampingUserMonitor;
import ca.hapke.campingbot.xml.ConfigParser;
import ca.hapke.campingbot.xml.ContentParser;
import ca.hapke.campingbot.xml.OutputFormatter;

/**
 * @author Nathan Hapke
 */
public class ConfigXmlSerializer implements CalendaredEvent<Void>, ConfigSerializer, PostConfigInit {
	private TimesProvider<Void> times;
	private boolean shouldSave = false;

	@Override
	public void doWork(ByCalendar<Void> event, Void value) {
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
	private static final String OLD_FILENAME = "config.xml";
	private static final String SETTINGS_FILENAME = "settings.xml";
	private static final String CONTENT_FILENAME = "content.xml";
	private static final boolean CONTENT_FROM_XML = true;
	private CampingSerializable[] serializables;
	private CampingSystem cs;
	private SpellCommand sg;
	private HypeCommand hype;
//	private AitaCommand aita;
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
//		this.aita = aita;
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
		return save(OLD_FILENAME);
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
		try {
			loadSettings(SETTINGS_FILENAME);
			return true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public void init() {
		if (CONTENT_FROM_XML) {
			try {
				loadContent(CONTENT_FILENAME);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean loadSettings(String fn) throws IOException, ClassNotFoundException {
		File f = getFileNotInBinFolder(protectionDomain, fn);
		BasicCrimsonXMLTokenStream stream1 = new BasicCrimsonXMLTokenStream(
				new FileReader(f, Charset.forName(CHARSET_TO_USE)), ConfigParser.class, false, false);
		ConfigParser conf = new ConfigParser(stream1);
		try {
			conf.document(cs);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean loadContent(String fn) throws IOException, ClassNotFoundException {
		CampingUserMonitor userMonitor = um;

		EntityManager jpaManager = DatabaseConsumer.getInstance().getManager();
		String query = "SELECT u FROM CampingUser u ";
		try {
			jpaManager.getTransaction().begin();
			List<CampingUser> dbUsers = jpaManager.createQuery(query, CampingUser.class).getResultList();
			for (CampingUser u : dbUsers) {
				um.addUser(u);
			}
			if (dbUsers.size() > 0) {
				// disable that part of the parser
				userMonitor = null;
			}
		} catch (Exception e1) {
			System.err.println(e1.getLocalizedMessage());
		} finally {
			jpaManager.getTransaction().rollback();
		}

		File f = getFileNotInBinFolder(protectionDomain, fn);
		BasicCrimsonXMLTokenStream stream2 = new BasicCrimsonXMLTokenStream(
				new FileReader(f, Charset.forName(CHARSET_TO_USE)), ContentParser.class, false, false);
		ContentParser cont = new ContentParser(stream2);
		try {
			cont.document(sg, hype, pc, cm, userMonitor, ig, ec);
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
