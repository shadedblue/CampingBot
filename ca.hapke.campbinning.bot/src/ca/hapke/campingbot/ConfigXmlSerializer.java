package ca.hapke.campingbot;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.ProtectionDomain;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.javadude.antxr.scanner.BasicCrimsonXMLTokenStream;

import ca.hapke.calendaring.timing.ByFrequency;
import ca.hapke.calendaring.timing.TimesProvider;
import ca.hapke.campingbot.api.ConfigSerializer;
import ca.hapke.campingbot.api.PostConfigInit;
import ca.hapke.campingbot.category.HasPersistedCategories;
import ca.hapke.campingbot.channels.CampingChat;
import ca.hapke.campingbot.channels.CampingChatManager;
import ca.hapke.campingbot.commands.EnhanceCommand;
import ca.hapke.campingbot.commands.HypeCommand;
import ca.hapke.campingbot.commands.PartyEverydayCommand;
import ca.hapke.campingbot.commands.spell.SpellCommand;
import ca.hapke.campingbot.log.CategoriedPersistence;
import ca.hapke.campingbot.log.DatabaseConsumer;
import ca.hapke.campingbot.response.InsultGenerator;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.users.CampingUserMonitor;
import ca.hapke.campingbot.xml.ConfigParser;
import ca.hapke.campingbot.xml.ContentParser;

/**
 * @author Nathan Hapke
 */
public class ConfigXmlSerializer implements ConfigSerializer, PostConfigInit {
	private TimesProvider<Void> times;
	private boolean shouldSave = false;

	private static final String CHARSET_TO_USE = "UTF-16";
	private static final String OLD_FILENAME = "config.xml";
	private static final String SETTINGS_FILENAME = "settings.xml";
	private static final String CONTENT_FILENAME = "content.xml";
	private static final boolean CONTENT_FROM_XML = true;
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
		times = new TimesProvider<Void>(new ByFrequency<Void>(null, 1, ChronoUnit.MINUTES));
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

		DatabaseConsumer db = DatabaseConsumer.getInstance();
		EntityManager jpaManager = db.getManager();
		String query = "SELECT u FROM " + CampingUser.class.getName() + " u ";
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

		query = "SELECT c FROM " + CampingChat.class.getName() + " c ";
		CampingChatManager chatMonitor = cm;
		try {
			jpaManager.getTransaction().begin();
			List<CampingChat> chats = jpaManager.createQuery(query, CampingChat.class).getResultList();
//			for (CampingUser u : dbUsers) {
//				um.addUser(u);
//			}
			for (CampingChat chat : chats) {
				chatMonitor.add(chat);
			}
			if (chats.size() > 0) {
				// disable that part of the parser
				chatMonitor = null;
			}
		} catch (Exception e1) {
			System.err.println(e1.getLocalizedMessage());
		} finally {
			jpaManager.getTransaction().rollback();
		}

		HasPersistedCategories[] holders = { ig };
//		String[] ids = { InsultGenerator.INSULTS_CONTAINER };

//		List<CategoriedPersistence> shouldAdd = new ArrayList<>();
		for (HasPersistedCategories holder : holders) {
			boolean addCats = false;
			boolean loadedCats = false;
			CategoriedPersistence cats = null;

			for (String category : holder.getCategoryNames()) {
				String container = holder.getContainerName();
				try {
					jpaManager.getTransaction().begin();

					query = "SELECT c FROM " + CategoriedPersistence.class.getName() + " c WHERE c."
							+ CategoriedPersistence.CONTAINER_NAME + " = ?1 AND c."
							+ CategoriedPersistence.CATEGORY_NAME + " = ?2";
					Query q = jpaManager.createQuery(query);
					q.setParameter(1, container);
					q.setParameter(2, category);
					cats = (CategoriedPersistence) q.getSingleResult();
					loadedCats = true;

				} catch (Exception e1) {
					System.err.println(e1.getLocalizedMessage());
				} finally {
					jpaManager.getTransaction().rollback();

					if (cats == null) {
						cats = new CategoriedPersistence();
						cats.setContainer(container);
						cats.setCategory(category);
						addCats = true;
					}
				}

				if (addCats && cats != null) {
					db.addPersistence(cats);
				}
				if (loadedCats && cats != null) {
					holder.loadPersistence(cats);
				}
			}
		}

		File f = getFileNotInBinFolder(protectionDomain, fn);
		BasicCrimsonXMLTokenStream stream2 = new BasicCrimsonXMLTokenStream(
				new FileReader(f, Charset.forName(CHARSET_TO_USE)), ContentParser.class, false, false);
		ContentParser cont = new ContentParser(stream2);
		try {
			cont.document(sg, hype, pc, chatMonitor, userMonitor, ig, ec);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
