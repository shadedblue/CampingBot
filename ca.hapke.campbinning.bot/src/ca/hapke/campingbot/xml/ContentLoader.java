package ca.hapke.campingbot.xml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.ProtectionDomain;
import java.util.List;

import javax.persistence.EntityManager;

import com.javadude.antxr.scanner.BasicCrimsonXMLTokenStream;

import ca.hapke.campingbot.api.PostConfigInit;
import ca.hapke.campingbot.channels.CampingChat;
import ca.hapke.campingbot.channels.CampingChatManager;
import ca.hapke.campingbot.commands.EnhanceCommand;
import ca.hapke.campingbot.commands.HypeCommand;
import ca.hapke.campingbot.commands.PartyEverydayCommand;
import ca.hapke.campingbot.commands.spell.SpellCommand;
import ca.hapke.campingbot.log.DatabaseConsumer;
import ca.hapke.campingbot.response.InsultGenerator;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.users.CampingUserMonitor;

/**
 * @author Nathan Hapke
 */
public class ContentLoader extends AbstractLoader implements PostConfigInit {
	private static final String CONTENT_FILENAME = "content.xml";
	private static final boolean CONTENT_FROM_XML = true;
	private SpellCommand sg;
	private HypeCommand hype;
	private CampingUserMonitor um;
	private PartyEverydayCommand pc;
	private CampingChatManager cm;
	private InsultGenerator ig;
	private EnhanceCommand ec;

	public ContentLoader(ProtectionDomain protectionDomain, SpellCommand sg, HypeCommand hype,
			PartyEverydayCommand partyCommand, CampingChatManager cm, CampingUserMonitor um, InsultGenerator ig,
			EnhanceCommand ec) {
		super(protectionDomain);
		this.sg = sg;
		this.hype = hype;
		this.pc = partyCommand;
		this.cm = cm;
		this.um = um;
		this.ig = ig;
		this.ec = ec;
	}

	@Override
	public boolean load() {
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
