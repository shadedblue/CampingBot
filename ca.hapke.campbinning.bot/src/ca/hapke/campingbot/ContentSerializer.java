//package ca.hapke.campingbot;
//
//import java.io.File;
//import java.io.FileReader;
//import java.io.IOException;
//import java.net.URL;
//import java.nio.charset.Charset;
//import java.security.ProtectionDomain;
//
//import com.javadude.antxr.scanner.BasicCrimsonXMLTokenStream;
//
//import ca.hapke.campingbot.commands.EnhanceCommand;
//import ca.hapke.campingbot.commands.HypeCommand;
//import ca.hapke.campingbot.commands.PartyEverydayCommand;
//import ca.hapke.campingbot.commands.spell.SpellCommand;
//import ca.hapke.campingbot.response.InsultGenerator;
//import ca.hapke.campingbot.xml.ContentParser;
//
///**
// * @author Nathan Hapke
// */
//public class ContentSerializer {
//
//	private static final String CHARSET_TO_USE = "UTF-16";
//	private static final String FILENAME = "content.xml";
//	private SpellCommand sg;
//	private HypeCommand hype;
//	private PartyEverydayCommand pc;
//	private InsultGenerator ig;
//	private EnhanceCommand ec;
//	private ProtectionDomain protectionDomain;
//
//	public ContentSerializer(ProtectionDomain protectionDomain, SpellCommand sg, HypeCommand hype,
//			PartyEverydayCommand partyCommand, InsultGenerator ig, EnhanceCommand ec) {
//		this.protectionDomain = protectionDomain;
//		this.sg = sg;
//		this.hype = hype;
//		this.pc = partyCommand;
//		this.ig = ig;
//		this.ec = ec;
//	}
//
//	public static File getFileNotInBinFolder(ProtectionDomain protectionDomain, String fn) throws IOException {
//		URL url = protectionDomain.getCodeSource().getLocation();
//
//		File f;
//		File dir = new File(url.getFile());
//		int i = 0;
//		while (i < 100) {
//			f = new File(dir.getAbsolutePath() + File.separatorChar + fn);
//			if (f.exists())
//				return f;
//			dir = dir.getParentFile();
//			if (dir == null || !dir.canRead())
//				return null;
//			i++;
//		}
//		System.err.println("File not found");
//		return null;
//	}
//
//	public boolean load() {
//		try {
//			loadContent(FILENAME);
//			return true;
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		return false;
//	}
//
//	private boolean loadContent(String fn) throws IOException, ClassNotFoundException {
//		File f = getFileNotInBinFolder(protectionDomain, fn);
//		BasicCrimsonXMLTokenStream stream1 = new BasicCrimsonXMLTokenStream(
//				new FileReader(f, Charset.forName(CHARSET_TO_USE)), ContentParser.class, false, false);
//		ContentParser conf = new ContentParser(stream1);
//		try {
//			conf.document(sg, hype, pc, null, null, ig, ec);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//		return true;
//	}
//
////	public boolean loadContent(String fn) throws IOException, ClassNotFoundException {
////
////		CampingChatManager chatMonitor = CampingChatManager.getInstance();
////		CampingUserMonitor userMonitor = CampingUserMonitor.getInstance();
////
////		File f = getFileNotInBinFolder(protectionDomain, fn);
////		BasicCrimsonXMLTokenStream stream2 = new BasicCrimsonXMLTokenStream(
////				new FileReader(f, Charset.forName(CHARSET_TO_USE)), ContentParser.class, false, false);
////		ContentParser cont = new ContentParser(stream2);
////		try {
////			cont.document(sg, hype, pc, chatMonitor, userMonitor, ig, ec);
////		} catch (Exception e) {
////			e.printStackTrace();
////			return false;
////		}
////		return true;
////	}
//}
