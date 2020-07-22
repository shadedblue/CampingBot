// $ANTXR : "LoadStatsParser.antxr" -> "LoadStatsParser.java"$
// GENERATED CODE - DO NOT EDIT!

    package ca.hapke.campbinning.bot.xml;
    
    import ca.hapke.campbinning.bot.*;
    import ca.hapke.campbinning.bot.users.*;
    import ca.hapke.campbinning.bot.channels.*;
    import ca.hapke.campbinning.bot.commands.*;
    import ca.hapke.campbinning.bot.commands.response.*;
    import ca.hapke.campbinning.bot.commands.voting.aita.*;
    import java.util.*;

import com.javadude.antxr.TokenBuffer;
import com.javadude.antxr.TokenStreamException;
import com.javadude.antxr.TokenStreamIOException;
import com.javadude.antxr.ANTXRException;
import com.javadude.antxr.LLkParser;
import com.javadude.antxr.Token;
import com.javadude.antxr.TokenStream;
import com.javadude.antxr.RecognitionException;
import com.javadude.antxr.NoViableAltException;
import com.javadude.antxr.MismatchedTokenException;
import com.javadude.antxr.SemanticException;
import com.javadude.antxr.ParserSharedInputState;
import com.javadude.antxr.collections.impl.BitSet;

// ANTXR XML Mode Support
import com.javadude.antxr.scanner.XMLToken;
import com.javadude.antxr.scanner.Attribute;
import java.util.Map;
import java.util.HashMap;


@SuppressWarnings("all")
public class LoadStatsParser extends com.javadude.antxr.LLkParser       implements LoadStatsParserTokenTypes
 {
	// ANTXR XML Mode Support
	private static Map<String, String> __xml_namespaceMap = new HashMap<String, String>();
	public static Map<String, String> getNamespaceMap() {return __xml_namespaceMap;}
	public static String resolveNamespace(String prefix) {
		if (prefix == null || "".equals(prefix))
			return "";
		return __xml_namespaceMap.get(prefix);
	}


protected LoadStatsParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public LoadStatsParser(TokenBuffer tokenBuf) {
  this(tokenBuf,1);
}

protected LoadStatsParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public LoadStatsParser(TokenStream lexer) {
  this(lexer,1);
}

public LoadStatsParser(ParserSharedInputState state) {
  super(state,1);
  tokenNames = _tokenNames;
}

	public final void document(
		 CampingSystem cs, SpellGenerator sg, CountdownGenerator countdownGen, AitaCommand aita, PartyEverydayCommand pc, CampingChatManager cm, CampingUserMonitor um, InsultGenerator ig
	) throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			__xml_camping( cs, sg, countdownGen, aita, pc, cm, um, ig);
			match(Token.EOF_TYPE);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
	}
	
	public final void __xml_camping(
		CampingSystem cs, SpellGenerator sg, CountdownGenerator countdownGen, AitaCommand aita, PartyEverydayCommand pc, CampingChatManager cm, CampingUserMonitor um, InsultGenerator ig
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(4);
			{
			__xml_system(cs);
			{
			switch ( LA(1)) {
			case 16:
			{
				__xml_spell(sg);
				break;
			}
			case XML_END_TAG:
			case 6:
			case 7:
			case 10:
			case 15:
			case 21:
			case 23:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case 15:
			{
				__xml_countdown(countdownGen);
				break;
			}
			case XML_END_TAG:
			case 6:
			case 7:
			case 10:
			case 21:
			case 23:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case 7:
			{
				__xml_voting(aita);
				break;
			}
			case XML_END_TAG:
			case 6:
			case 10:
			case 21:
			case 23:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case 6:
			{
				__xml_party(pc);
				break;
			}
			case XML_END_TAG:
			case 10:
			case 21:
			case 23:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case 10:
			{
				__xml_insults(ig);
				break;
			}
			case XML_END_TAG:
			case 21:
			case 23:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case 21:
			{
				__xml_chats(cm);
				break;
			}
			case XML_END_TAG:
			case 23:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case 23:
			{
				__xml_users(um);
				break;
			}
			case XML_END_TAG:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
	}
	
	public final void __xml_system(
		CampingSystem cs
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(13);
			{
			
					String token,botUsername,adminUser = null,announceChat=null; 
				
			token=__xml_token();
			botUsername=__xml_botUsername();
			{
			switch ( LA(1)) {
			case 33:
			{
				adminUser=__xml_adminUser();
				break;
			}
			case XML_END_TAG:
			case 14:
			case 25:
			case 32:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case 32:
			{
				announceChat=__xml_announceChat();
				break;
			}
			case XML_END_TAG:
			case 14:
			case 25:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case 25:
			{
				__xml_connectOnStartup(cs);
				break;
			}
			case XML_END_TAG:
			case 14:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case 14:
			{
				__xml_db(cs);
				break;
			}
			case XML_END_TAG:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			
					cs.setToken(token);
					cs.setBotUsername(botUsername);
					if (adminUser != null && !"null".equalsIgnoreCase(adminUser)) {
						int adminUser2 = Integer.parseInt(adminUser);
						cs.setAdminUser(adminUser2);
					} 
					if (announceChat != null && !"null".equalsIgnoreCase(announceChat)) {
						long ac = Long.parseLong(announceChat);
						cs.setAnnounceChat(ac);
					} 
				
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final void __xml_spell(
		SpellGenerator sg
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(16);
			{
			List<String> a,i,e;
			a=__xml_adjectives();
			i=__xml_items();
			e=__xml_exclamations();
			
					sg.setAdjectives(a);
					sg.setItems(i);
					sg.setExclamations(e);
				
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
	}
	
	public final void __xml_countdown(
		CountdownGenerator countdownGen
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(15);
			{
			List<String> h;
			h=__xml_hypes();
			
					countdownGen.setHypes(h);
				
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
	}
	
	public final void __xml_voting(
		AitaCommand aita
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(7);
			{
			__xml_assholes(aita);
			__xml_mediocres(aita);
			__xml_nices(aita);
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_4);
		}
	}
	
	public final void __xml_party(
		PartyEverydayCommand pc
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(6);
			{
			__xml_excessives(pc);
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_5);
		}
	}
	
	public final void __xml_insults(
		InsultGenerator ig
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(10);
			{
			String val;
			{
			_loop148:
			do {
				if ((LA(1)==38)) {
					val=__xml_item();
					ig.addItem("insult", val);
				}
				else {
					break _loop148;
				}
				
			} while (true);
			}
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_6);
		}
	}
	
	public final void __xml_chats(
		CampingChatManager cm
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(21);
			{
			{
			_loop188:
			do {
				if ((LA(1)==22)) {
					__xml_chat(cm);
				}
				else {
					break _loop188;
				}
				
			} while (true);
			}
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
	}
	
	public final void __xml_users(
		CampingUserMonitor um
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(23);
			{
			String campingId = null;
			{
			switch ( LA(1)) {
			case 29:
			{
				campingId=__xml_nextCampingId();
				break;
			}
			case XML_END_TAG:
			case 24:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			_loop195:
			do {
				if ((LA(1)==24)) {
					__xml_user(um);
				}
				else {
					break _loop195;
				}
				
			} while (true);
			}
			
					if (campingId != null && !"null".equalsIgnoreCase(campingId)) {
						int campingIdInt = Integer.parseInt(campingId);
						um.setNextCampingId(campingIdInt);
					} 
				
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_8);
		}
	}
	
	public final void __xml_excessives(
		PartyEverydayCommand pc
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(8);
			{
			String val;
			{
			_loop140:
			do {
				if ((LA(1)==38)) {
					val=__xml_item();
					pc.addItem("excessive", val);
				}
				else {
					break _loop140;
				}
				
			} while (true);
			}
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_8);
		}
	}
	
	public final void __xml_assholes(
		AitaCommand aita
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(9);
			{
			String val;
			{
			_loop144:
			do {
				if ((LA(1)==38)) {
					val=__xml_item();
					aita.addItem("asshole", val);
				}
				else {
					break _loop144;
				}
				
			} while (true);
			}
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
	}
	
	public final void __xml_mediocres(
		AitaCommand aita
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(11);
			{
			String val;
			{
			_loop152:
			do {
				if ((LA(1)==38)) {
					val=__xml_item();
					aita.addItem("mediocre", val);
				}
				else {
					break _loop152;
				}
				
			} while (true);
			}
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_10);
		}
	}
	
	public final void __xml_nices(
		AitaCommand aita
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(12);
			{
			String val;
			{
			_loop156:
			do {
				if ((LA(1)==38)) {
					val=__xml_item();
					aita.addItem("nice", val);
				}
				else {
					break _loop156;
				}
				
			} while (true);
			}
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_8);
		}
	}
	
	public final String  __xml_item() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(38);
			{
			pcdata = LT(1);
			match(PCDATA);
			value = pcdata.getText();
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_11);
		}
		return value;
	}
	
	public final String  __xml_token() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(30);
			{
			pcdata = LT(1);
			match(PCDATA);
			value = pcdata.getText();
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_12);
		}
		return value;
	}
	
	public final String  __xml_botUsername() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(31);
			{
			pcdata = LT(1);
			match(PCDATA);
			value = pcdata.getText();
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_13);
		}
		return value;
	}
	
	public final String  __xml_adminUser() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(33);
			{
			pcdata = LT(1);
			match(PCDATA);
			value = pcdata.getText();
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_14);
		}
		return value;
	}
	
	public final String  __xml_announceChat() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(32);
			{
			pcdata = LT(1);
			match(PCDATA);
			value = pcdata.getText();
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_15);
		}
		return value;
	}
	
	public final void __xml_connectOnStartup(
		CampingSystem cs
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(25);
			{
			pcdata = LT(1);
			match(PCDATA);
			
					try {
						String value = pcdata.getText();
						boolean tf = Boolean.parseBoolean(value);
						cs.setConnectOnStartup(tf);
					} catch (Exception e) {
					}
				
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_16);
		}
	}
	
	public final void __xml_db(
		CampingSystem cs
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(14);
			{
			
					String host,port,user,pass; 
				
			host=__xml_dbHost();
			port=__xml_dbPort();
			user=__xml_dbUser();
			pass=__xml_dbPass();
			
					int port2 = 5432;
					if (!"null".equalsIgnoreCase(port)) {
						port2 = Integer.parseInt(port);
					} 
					cs.enableDb(host,port2,user,pass);
				
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_8);
		}
	}
	
	public final String  __xml_dbHost() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(34);
			{
			pcdata = LT(1);
			match(PCDATA);
			value = pcdata.getText();
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_17);
		}
		return value;
	}
	
	public final String  __xml_dbPort() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(35);
			{
			pcdata = LT(1);
			match(PCDATA);
			value = pcdata.getText();
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_18);
		}
		return value;
	}
	
	public final String  __xml_dbUser() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(36);
			{
			pcdata = LT(1);
			match(PCDATA);
			value = pcdata.getText();
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_19);
		}
		return value;
	}
	
	public final String  __xml_dbPass() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(37);
			{
			pcdata = LT(1);
			match(PCDATA);
			value = pcdata.getText();
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_8);
		}
		return value;
	}
	
	public final List<String>  __xml_hypes() throws RecognitionException, TokenStreamException {
		List<String> vals = null;
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(18);
			{
			vals = new ArrayList<String>(); String val;
			{
			_loop176:
			do {
				if ((LA(1)==38)) {
					val=__xml_item();
					vals.add(val);
				}
				else {
					break _loop176;
				}
				
			} while (true);
			}
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_8);
		}
		return vals;
	}
	
	public final List<String>  __xml_adjectives() throws RecognitionException, TokenStreamException {
		List<String> vals = null;
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(17);
			{
			vals = new ArrayList<String>(); String val;
			{
			_loop172:
			do {
				if ((LA(1)==38)) {
					val=__xml_item();
					vals.add(val);
				}
				else {
					break _loop172;
				}
				
			} while (true);
			}
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_20);
		}
		return vals;
	}
	
	public final List<String>  __xml_items() throws RecognitionException, TokenStreamException {
		List<String> vals = null;
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(19);
			{
			vals = new ArrayList<String>(); String val;
			{
			_loop180:
			do {
				if ((LA(1)==38)) {
					val=__xml_item();
					vals.add(val);
				}
				else {
					break _loop180;
				}
				
			} while (true);
			}
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_21);
		}
		return vals;
	}
	
	public final List<String>  __xml_exclamations() throws RecognitionException, TokenStreamException {
		List<String> vals = null;
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(20);
			{
			vals = new ArrayList<String>(); String val;
			{
			_loop184:
			do {
				if ((LA(1)==38)) {
					val=__xml_item();
					vals.add(val);
				}
				else {
					break _loop184;
				}
				
			} while (true);
			}
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_8);
		}
		return vals;
	}
	
	public final void __xml_chat(
		CampingChatManager cm
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(22);
			{
			
					CampingChat chat;
					long chatId = 0;
					String idStr, type, allowed;
				
			idStr=__xml_id();
			type=__xml_type();
			allowed=__xml_allowed();
			
					if (idStr != null && !"null".equalsIgnoreCase(idStr)) {
						chatId = Long.parseLong(idStr);
					} 
					if (chatId == 0)
						return;
					chat = cm.get(chatId);
					chat.setType(type);
					chat.setAllowed(allowed);
				
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_22);
		}
	}
	
	public final String  __xml_id() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(40);
			{
			pcdata = LT(1);
			match(PCDATA);
			value = pcdata.getText();
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_23);
		}
		return value;
	}
	
	public final String  __xml_type() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(46);
			{
			pcdata = LT(1);
			match(PCDATA);
			value = pcdata.getText();
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_24);
		}
		return value;
	}
	
	public final String  __xml_allowed() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(47);
			{
			pcdata = LT(1);
			match(PCDATA);
			value = pcdata.getText();
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_8);
		}
		return value;
	}
	
	public final String  __xml_nextCampingId() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(29);
			{
			pcdata = LT(1);
			match(PCDATA);
			value = pcdata.getText();
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_25);
		}
		return value;
	}
	
	public final void __xml_user(
		CampingUserMonitor um
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(24);
			{
				String campingId = "null", id, username, first, last, nickname, birthdayMonth = null, birthdayDay = null, lastUpdate, interaction = null;
				CampingUser user;
			
			{
			switch ( LA(1)) {
			case 39:
			{
				campingId=__xml_campingId();
				break;
			}
			case 40:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			id=__xml_id();
			username=__xml_username();
			first=__xml_first();
			last=__xml_last();
			nickname=__xml_nickname();
			{
			switch ( LA(1)) {
			case 45:
			{
				interaction=__xml_interaction();
				break;
			}
			case XML_END_TAG:
			case 27:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case 27:
			{
				birthdayMonth=__xml_birthdayMonth();
				birthdayDay=__xml_birthdayDay();
				break;
			}
			case XML_END_TAG:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			
						
					int campingIdInt = CampingUserMonitor.UNKNOWN_USER_ID;
					if (!"null".equalsIgnoreCase(campingId)) {
						campingIdInt = Integer.parseInt(campingId);
					} 
					
					int idInt = CampingUserMonitor.UNKNOWN_USER_ID;
					if (!"null".equalsIgnoreCase(id)) {
						idInt = Integer.parseInt(id);
					} 
					if ("null".equalsIgnoreCase(username))
						username = null;
					boolean inter = false;
					if (interaction != null) {
						try {
							inter = Boolean.parseBoolean(interaction);
						} catch (Exception e) {
						}
					}
						
					user = um.monitor(campingIdInt, idInt, username, first, last, inter);
					user.setNickname(nickname, false);
					
					int bM = -1, bD=-1;
					if (birthdayMonth != null)
						bM = Integer.parseInt(birthdayMonth);
					if (birthdayDay != null)
						bD = Integer.parseInt(birthdayDay);
					if (bM > 0 && bD > 0) {
						user.setBirthday(bM, bD);
					}
				
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_25);
		}
	}
	
	public final String  __xml_campingId() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(39);
			{
			pcdata = LT(1);
			match(PCDATA);
			value = pcdata.getText();
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_26);
		}
		return value;
	}
	
	public final String  __xml_username() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(41);
			{
			pcdata = LT(1);
			match(PCDATA);
			value = pcdata.getText();
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_27);
		}
		return value;
	}
	
	public final String  __xml_first() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(43);
			{
			pcdata = LT(1);
			match(PCDATA);
			value = pcdata.getText();
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_28);
		}
		return value;
	}
	
	public final String  __xml_last() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(44);
			{
			pcdata = LT(1);
			match(PCDATA);
			value = pcdata.getText();
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_29);
		}
		return value;
	}
	
	public final String  __xml_nickname() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(42);
			{
			pcdata = LT(1);
			match(PCDATA);
			value = pcdata.getText();
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_30);
		}
		return value;
	}
	
	public final String  __xml_interaction() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(45);
			{
			pcdata = LT(1);
			match(PCDATA);
			value = pcdata.getText();
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_31);
		}
		return value;
	}
	
	public final String  __xml_birthdayMonth() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(27);
			{
			pcdata = LT(1);
			match(PCDATA);
			value = pcdata.getText();
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_32);
		}
		return value;
	}
	
	public final String  __xml_birthdayDay() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(28);
			{
			pcdata = LT(1);
			match(PCDATA);
			value = pcdata.getText();
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_8);
		}
		return value;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"\"<camping>\"",
		"XML_END_TAG",
		"\"<party>\"",
		"\"<voting>\"",
		"\"<excessives>\"",
		"\"<assholes>\"",
		"\"<insults>\"",
		"\"<mediocres>\"",
		"\"<nices>\"",
		"\"<system>\"",
		"\"<db>\"",
		"\"<countdown>\"",
		"\"<spell>\"",
		"\"<adjectives>\"",
		"\"<hypes>\"",
		"\"<items>\"",
		"\"<exclamations>\"",
		"\"<chats>\"",
		"\"<chat>\"",
		"\"<users>\"",
		"\"<user>\"",
		"\"<connectOnStartup>\"",
		"PCDATA",
		"\"<birthdayMonth>\"",
		"\"<birthdayDay>\"",
		"\"<nextCampingId>\"",
		"\"<token>\"",
		"\"<botUsername>\"",
		"\"<announceChat>\"",
		"\"<adminUser>\"",
		"\"<dbHost>\"",
		"\"<dbPort>\"",
		"\"<dbUser>\"",
		"\"<dbPass>\"",
		"\"<item>\"",
		"\"<campingId>\"",
		"\"<id>\"",
		"\"<username>\"",
		"\"<nickname>\"",
		"\"<first>\"",
		"\"<last>\"",
		"\"<interaction>\"",
		"\"<type>\"",
		"\"<allowed>\""
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 10585312L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 10519776L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 10487008L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 10486880L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 10486816L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 10485792L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 8388640L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 32L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { 2048L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = { 4096L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	private static final long[] mk_tokenSet_11() {
		long[] data = { 274877906976L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
	private static final long[] mk_tokenSet_12() {
		long[] data = { 2147483648L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
	private static final long[] mk_tokenSet_13() {
		long[] data = { 12918472736L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());
	private static final long[] mk_tokenSet_14() {
		long[] data = { 4328538144L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_14 = new BitSet(mk_tokenSet_14());
	private static final long[] mk_tokenSet_15() {
		long[] data = { 33570848L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_15 = new BitSet(mk_tokenSet_15());
	private static final long[] mk_tokenSet_16() {
		long[] data = { 16416L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_16 = new BitSet(mk_tokenSet_16());
	private static final long[] mk_tokenSet_17() {
		long[] data = { 34359738368L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_17 = new BitSet(mk_tokenSet_17());
	private static final long[] mk_tokenSet_18() {
		long[] data = { 68719476736L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_18 = new BitSet(mk_tokenSet_18());
	private static final long[] mk_tokenSet_19() {
		long[] data = { 137438953472L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_19 = new BitSet(mk_tokenSet_19());
	private static final long[] mk_tokenSet_20() {
		long[] data = { 524288L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_20 = new BitSet(mk_tokenSet_20());
	private static final long[] mk_tokenSet_21() {
		long[] data = { 1048576L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_21 = new BitSet(mk_tokenSet_21());
	private static final long[] mk_tokenSet_22() {
		long[] data = { 4194336L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_22 = new BitSet(mk_tokenSet_22());
	private static final long[] mk_tokenSet_23() {
		long[] data = { 72567767433216L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_23 = new BitSet(mk_tokenSet_23());
	private static final long[] mk_tokenSet_24() {
		long[] data = { 140737488355328L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_24 = new BitSet(mk_tokenSet_24());
	private static final long[] mk_tokenSet_25() {
		long[] data = { 16777248L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_25 = new BitSet(mk_tokenSet_25());
	private static final long[] mk_tokenSet_26() {
		long[] data = { 1099511627776L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_26 = new BitSet(mk_tokenSet_26());
	private static final long[] mk_tokenSet_27() {
		long[] data = { 8796093022208L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_27 = new BitSet(mk_tokenSet_27());
	private static final long[] mk_tokenSet_28() {
		long[] data = { 17592186044416L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_28 = new BitSet(mk_tokenSet_28());
	private static final long[] mk_tokenSet_29() {
		long[] data = { 4398046511104L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_29 = new BitSet(mk_tokenSet_29());
	private static final long[] mk_tokenSet_30() {
		long[] data = { 35184506306592L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_30 = new BitSet(mk_tokenSet_30());
	private static final long[] mk_tokenSet_31() {
		long[] data = { 134217760L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_31 = new BitSet(mk_tokenSet_31());
	private static final long[] mk_tokenSet_32() {
		long[] data = { 268435456L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_32 = new BitSet(mk_tokenSet_32());
	
	}
