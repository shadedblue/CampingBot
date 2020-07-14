// $ANTXR : "LoadStatsParser.antxr" -> "LoadStatsParser.java"$
// GENERATED CODE - DO NOT EDIT!

    package ca.hapke.campbinning.bot.xml;
    
    import ca.hapke.campbinning.bot.*;
    import ca.hapke.campbinning.bot.users.*;
    import ca.hapke.campbinning.bot.channels.*;
    import ca.hapke.campbinning.bot.commands.*;
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
		 CampingSystem cs, SpellGenerator sg, CountdownGenerator countdownGen, AitaCommand aita, PartyEverydayCommand pc, CampingChatManager cm, CampingUserMonitor um
	) throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			__xml_camping( cs, sg, countdownGen, aita, pc, cm, um);
			match(Token.EOF_TYPE);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
	}
	
	public final void __xml_camping(
		CampingSystem cs, SpellGenerator sg, CountdownGenerator countdownGen, AitaCommand aita, PartyEverydayCommand pc, CampingChatManager cm, CampingUserMonitor um
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(4);
			{
			__xml_system(cs);
			{
			switch ( LA(1)) {
			case 15:
			{
				__xml_spell(sg);
				break;
			}
			case XML_END_TAG:
			case 6:
			case 7:
			case 14:
			case 20:
			case 22:
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
				__xml_countdown(countdownGen);
				break;
			}
			case XML_END_TAG:
			case 6:
			case 7:
			case 20:
			case 22:
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
			case 20:
			case 22:
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
			case 20:
			case 22:
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
			case 20:
			{
				__xml_chats(cm);
				break;
			}
			case XML_END_TAG:
			case 22:
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
			case 22:
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
			match(12);
			{
			
					String token,botUsername,adminUser = null,announceChat=null; 
				
			token=__xml_token();
			botUsername=__xml_botUsername();
			{
			switch ( LA(1)) {
			case 31:
			{
				adminUser=__xml_adminUser();
				break;
			}
			case XML_END_TAG:
			case 13:
			case 30:
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
			case 30:
			{
				announceChat=__xml_announceChat();
				break;
			}
			case XML_END_TAG:
			case 13:
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
			case 13:
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
			match(15);
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
			match(14);
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
	
	public final void __xml_chats(
		CampingChatManager cm
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(20);
			{
			{
			_loop171:
			do {
				if ((LA(1)==21)) {
					__xml_chat(cm);
				}
				else {
					break _loop171;
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
	
	public final void __xml_users(
		CampingUserMonitor um
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(22);
			{
			String campingId = null;
			{
			switch ( LA(1)) {
			case 27:
			{
				campingId=__xml_nextCampingId();
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
			_loop178:
			do {
				if ((LA(1)==23)) {
					__xml_user(um);
				}
				else {
					break _loop178;
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
			recover(ex,_tokenSet_7);
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
			_loop128:
			do {
				if ((LA(1)==36)) {
					val=__xml_item();
					pc.addItem("excessive", val);
				}
				else {
					break _loop128;
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
			_loop132:
			do {
				if ((LA(1)==36)) {
					val=__xml_item();
					aita.addItem("asshole", val);
				}
				else {
					break _loop132;
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
	
	public final void __xml_mediocres(
		AitaCommand aita
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(10);
			{
			String val;
			{
			_loop136:
			do {
				if ((LA(1)==36)) {
					val=__xml_item();
					aita.addItem("mediocre", val);
				}
				else {
					break _loop136;
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
	
	public final void __xml_nices(
		AitaCommand aita
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(11);
			{
			String val;
			{
			_loop140:
			do {
				if ((LA(1)==36)) {
					val=__xml_item();
					aita.addItem("nice", val);
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
			recover(ex,_tokenSet_7);
		}
	}
	
	public final String  __xml_item() throws RecognitionException, TokenStreamException {
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
			recover(ex,_tokenSet_10);
		}
		return value;
	}
	
	public final String  __xml_token() throws RecognitionException, TokenStreamException {
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
			recover(ex,_tokenSet_11);
		}
		return value;
	}
	
	public final String  __xml_botUsername() throws RecognitionException, TokenStreamException {
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
			recover(ex,_tokenSet_12);
		}
		return value;
	}
	
	public final String  __xml_adminUser() throws RecognitionException, TokenStreamException {
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
	
	public final String  __xml_announceChat() throws RecognitionException, TokenStreamException {
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
			recover(ex,_tokenSet_14);
		}
		return value;
	}
	
	public final void __xml_db(
		CampingSystem cs
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(13);
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
			recover(ex,_tokenSet_7);
		}
	}
	
	public final String  __xml_dbHost() throws RecognitionException, TokenStreamException {
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
	
	public final String  __xml_dbPort() throws RecognitionException, TokenStreamException {
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
			recover(ex,_tokenSet_16);
		}
		return value;
	}
	
	public final String  __xml_dbUser() throws RecognitionException, TokenStreamException {
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
	
	public final String  __xml_dbPass() throws RecognitionException, TokenStreamException {
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
			recover(ex,_tokenSet_7);
		}
		return value;
	}
	
	public final List<String>  __xml_hypes() throws RecognitionException, TokenStreamException {
		List<String> vals = null;
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(17);
			{
			vals = new ArrayList<String>(); String val;
			{
			_loop159:
			do {
				if ((LA(1)==36)) {
					val=__xml_item();
					vals.add(val);
				}
				else {
					break _loop159;
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
		return vals;
	}
	
	public final List<String>  __xml_adjectives() throws RecognitionException, TokenStreamException {
		List<String> vals = null;
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(16);
			{
			vals = new ArrayList<String>(); String val;
			{
			_loop155:
			do {
				if ((LA(1)==36)) {
					val=__xml_item();
					vals.add(val);
				}
				else {
					break _loop155;
				}
				
			} while (true);
			}
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_18);
		}
		return vals;
	}
	
	public final List<String>  __xml_items() throws RecognitionException, TokenStreamException {
		List<String> vals = null;
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(18);
			{
			vals = new ArrayList<String>(); String val;
			{
			_loop163:
			do {
				if ((LA(1)==36)) {
					val=__xml_item();
					vals.add(val);
				}
				else {
					break _loop163;
				}
				
			} while (true);
			}
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_19);
		}
		return vals;
	}
	
	public final List<String>  __xml_exclamations() throws RecognitionException, TokenStreamException {
		List<String> vals = null;
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(19);
			{
			vals = new ArrayList<String>(); String val;
			{
			_loop167:
			do {
				if ((LA(1)==36)) {
					val=__xml_item();
					vals.add(val);
				}
				else {
					break _loop167;
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
		return vals;
	}
	
	public final void __xml_chat(
		CampingChatManager cm
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(21);
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
			recover(ex,_tokenSet_20);
		}
	}
	
	public final String  __xml_id() throws RecognitionException, TokenStreamException {
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
			recover(ex,_tokenSet_21);
		}
		return value;
	}
	
	public final String  __xml_type() throws RecognitionException, TokenStreamException {
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
			recover(ex,_tokenSet_22);
		}
		return value;
	}
	
	public final String  __xml_allowed() throws RecognitionException, TokenStreamException {
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
			recover(ex,_tokenSet_7);
		}
		return value;
	}
	
	public final String  __xml_nextCampingId() throws RecognitionException, TokenStreamException {
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
			recover(ex,_tokenSet_23);
		}
		return value;
	}
	
	public final void __xml_user(
		CampingUserMonitor um
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(23);
			{
				String campingId = "null", id, username, first, last, nickname, birthdayMonth = null, birthdayDay = null, lastUpdate;
				CampingUser user;
			{
			switch ( LA(1)) {
			case 37:
			{
				campingId=__xml_campingId();
				break;
			}
			case 38:
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
			case 24:
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
					
					int bM = -1, bD=-1;
					if (birthdayMonth != null)
						bM = Integer.parseInt(birthdayMonth);
					if (birthdayDay != null)
						bD = Integer.parseInt(birthdayDay);
						
					user = um.monitor(campingIdInt, idInt, username, first, last);
					user.setNickname(nickname);
					
					if (bM > 0 && bD > 0) {
						user.setBirthday(bM, bD);
					}
				
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_23);
		}
	}
	
	public final String  __xml_campingId() throws RecognitionException, TokenStreamException {
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
			recover(ex,_tokenSet_24);
		}
		return value;
	}
	
	public final String  __xml_username() throws RecognitionException, TokenStreamException {
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
			recover(ex,_tokenSet_25);
		}
		return value;
	}
	
	public final String  __xml_first() throws RecognitionException, TokenStreamException {
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
			recover(ex,_tokenSet_26);
		}
		return value;
	}
	
	public final String  __xml_last() throws RecognitionException, TokenStreamException {
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
			recover(ex,_tokenSet_27);
		}
		return value;
	}
	
	public final String  __xml_nickname() throws RecognitionException, TokenStreamException {
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
			recover(ex,_tokenSet_28);
		}
		return value;
	}
	
	public final String  __xml_birthdayMonth() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(24);
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
	
	public final String  __xml_birthdayDay() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(26);
			{
			pcdata = LT(1);
			match(PCDATA);
			value = pcdata.getText();
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
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
		"\"<birthdayMonth>\"",
		"PCDATA",
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
		"\"<type>\"",
		"\"<allowed>\""
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 5292256L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 5259488L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 5243104L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 5242976L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 5242912L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 4194336L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 32L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 1024L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { 2048L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = { 68719476768L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	private static final long[] mk_tokenSet_11() {
		long[] data = { 536870912L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
	private static final long[] mk_tokenSet_12() {
		long[] data = { 3221233696L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
	private static final long[] mk_tokenSet_13() {
		long[] data = { 1073750048L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());
	private static final long[] mk_tokenSet_14() {
		long[] data = { 8224L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_14 = new BitSet(mk_tokenSet_14());
	private static final long[] mk_tokenSet_15() {
		long[] data = { 8589934592L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_15 = new BitSet(mk_tokenSet_15());
	private static final long[] mk_tokenSet_16() {
		long[] data = { 17179869184L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_16 = new BitSet(mk_tokenSet_16());
	private static final long[] mk_tokenSet_17() {
		long[] data = { 34359738368L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_17 = new BitSet(mk_tokenSet_17());
	private static final long[] mk_tokenSet_18() {
		long[] data = { 262144L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_18 = new BitSet(mk_tokenSet_18());
	private static final long[] mk_tokenSet_19() {
		long[] data = { 524288L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_19 = new BitSet(mk_tokenSet_19());
	private static final long[] mk_tokenSet_20() {
		long[] data = { 2097184L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_20 = new BitSet(mk_tokenSet_20());
	private static final long[] mk_tokenSet_21() {
		long[] data = { 9345848836096L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_21 = new BitSet(mk_tokenSet_21());
	private static final long[] mk_tokenSet_22() {
		long[] data = { 17592186044416L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_22 = new BitSet(mk_tokenSet_22());
	private static final long[] mk_tokenSet_23() {
		long[] data = { 8388640L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_23 = new BitSet(mk_tokenSet_23());
	private static final long[] mk_tokenSet_24() {
		long[] data = { 274877906944L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_24 = new BitSet(mk_tokenSet_24());
	private static final long[] mk_tokenSet_25() {
		long[] data = { 2199023255552L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_25 = new BitSet(mk_tokenSet_25());
	private static final long[] mk_tokenSet_26() {
		long[] data = { 4398046511104L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_26 = new BitSet(mk_tokenSet_26());
	private static final long[] mk_tokenSet_27() {
		long[] data = { 1099511627776L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_27 = new BitSet(mk_tokenSet_27());
	private static final long[] mk_tokenSet_28() {
		long[] data = { 16777248L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_28 = new BitSet(mk_tokenSet_28());
	private static final long[] mk_tokenSet_29() {
		long[] data = { 67108864L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_29 = new BitSet(mk_tokenSet_29());
	
	}
