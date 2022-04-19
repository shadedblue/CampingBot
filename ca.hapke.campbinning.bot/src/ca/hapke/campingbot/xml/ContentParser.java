// $ANTXR : "ContentParser.antxr" -> "ContentParser.java"$
// GENERATED CODE - DO NOT EDIT!

    package ca.hapke.campingbot.xml;
    
    import ca.hapke.campingbot.*;
    import ca.hapke.campingbot.users.*;
    import ca.hapke.campingbot.channels.*;
    import ca.hapke.campingbot.commands.*;
    import ca.hapke.campingbot.commands.spell.*;
    import ca.hapke.campingbot.response.*;
    import ca.hapke.campingbot.voting.*;
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
public class ContentParser extends com.javadude.antxr.LLkParser       implements ContentParserTokenTypes
 {
	// ANTXR XML Mode Support
	private static Map<String, String> __xml_namespaceMap = new HashMap<String, String>();
	public static Map<String, String> getNamespaceMap() {return __xml_namespaceMap;}
	public static String resolveNamespace(String prefix) {
		if (prefix == null || "".equals(prefix))
			return "";
		return __xml_namespaceMap.get(prefix);
	}


protected ContentParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public ContentParser(TokenBuffer tokenBuf) {
  this(tokenBuf,1);
}

protected ContentParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public ContentParser(TokenStream lexer) {
  this(lexer,1);
}

public ContentParser(ParserSharedInputState state) {
  super(state,1);
  tokenNames = _tokenNames;
}

	public final void document(
		 SpellCommand sg, HypeCommand hype, PartyEverydayCommand pc, CampingChatManager cm, CampingUserMonitor um, InsultGenerator ig, EnhanceCommand ec
	) throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			__xml_camping( sg, hype, pc, cm, um, ig, ec);
			match(Token.EOF_TYPE);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
	}
	
	public final void __xml_camping(
		SpellCommand sg, HypeCommand hype,  PartyEverydayCommand pc, CampingChatManager cm, CampingUserMonitor um, InsultGenerator ig, EnhanceCommand ec
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(4);
			{
			{
			switch ( LA(1)) {
			case 13:
			{
				__xml_spell(sg);
				break;
			}
			case XML_END_TAG:
			case 6:
			case 7:
			case 11:
			case 12:
			case 21:
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
			switch ( LA(1)) {
			case 12:
			{
				__xml_countdown(hype);
				break;
			}
			case XML_END_TAG:
			case 6:
			case 7:
			case 11:
			case 21:
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
			switch ( LA(1)) {
			case 6:
			{
				__xml_party(pc);
				break;
			}
			case XML_END_TAG:
			case 7:
			case 11:
			case 21:
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
			switch ( LA(1)) {
			case 11:
			{
				__xml_insults(ig);
				break;
			}
			case XML_END_TAG:
			case 7:
			case 21:
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
			switch ( LA(1)) {
			case 7:
			{
				__xml_enhance(ec);
				break;
			}
			case XML_END_TAG:
			case 21:
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
			switch ( LA(1)) {
			case 21:
			{
				__xml_chats(cm);
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
			switch ( LA(1)) {
			case 24:
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
	
	public final void __xml_spell(
		SpellCommand sg
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(13);
			{
			
					List<String> a;
				
			a=__xml_adjectives();
			
					sg.setAdjectives(a);
				
			{
			_loop66:
			do {
				if ((LA(1)==14)) {
					__xml_pack(sg);
				}
				else {
					break _loop66;
				}
				
			} while (true);
			}
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final void __xml_countdown(
		HypeCommand hype
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(12);
			{
			List<String> h, d;
			h=__xml_hypes();
			d=__xml_dicks();
			
					hype.setHypesAndDicks(h, d);
				
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
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
			recover(ex,_tokenSet_3);
		}
	}
	
	public final void __xml_insults(
		InsultGenerator ig
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(11);
			{
			String val;
			{
			_loop60:
			do {
				if ((LA(1)==31)) {
					val=__xml_item();
					ig.addItem("insult", val);
				}
				else {
					break _loop60;
				}
				
			} while (true);
			}
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_4);
		}
	}
	
	public final void __xml_enhance(
		EnhanceCommand ec
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(7);
			{
			__xml_rickrolls(ec);
			__xml_overs(ec);
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
			match(21);
			{
			{
			_loop97:
			do {
				if ((LA(1)==22)) {
					__xml_chat(cm);
				}
				else {
					break _loop97;
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
			match(24);
			{
			String nextId = null;
			{
			switch ( LA(1)) {
			case 30:
			{
				nextId=__xml_nextCampingId();
				break;
			}
			case XML_END_TAG:
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
			_loop110:
			do {
				if ((LA(1)==25)) {
					__xml_user(um);
				}
				else {
					break _loop110;
				}
				
			} while (true);
			}
			
					if (nextId != null && um != null && !"null".equalsIgnoreCase(nextId)) {
						int nextIdInt = Integer.parseInt(nextId);
						um.setNextCampingId(nextIdInt);
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
			match(10);
			{
			String val;
			{
			_loop56:
			do {
				if ((LA(1)==31)) {
					val=__xml_item();
					pc.addItem("excessive", val);
				}
				else {
					break _loop56;
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
	
	public final void __xml_rickrolls(
		EnhanceCommand ec
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(8);
			{
			String val;
			{
			_loop48:
			do {
				if ((LA(1)==31)) {
					val=__xml_item();
					ec.addItem("rickroll", val);
				}
				else {
					break _loop48;
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
	
	public final void __xml_overs(
		EnhanceCommand ec
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(9);
			{
			String val;
			{
			_loop52:
			do {
				if ((LA(1)==31)) {
					val=__xml_item();
					ec.addItem("over", val);
				}
				else {
					break _loop52;
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
			recover(ex,_tokenSet_9);
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
			_loop81:
			do {
				if ((LA(1)==31)) {
					val=__xml_item();
					vals.add(val);
				}
				else {
					break _loop81;
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
		return vals;
	}
	
	public final List<String>  __xml_dicks() throws RecognitionException, TokenStreamException {
		List<String> vals = null;
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(18);
			{
			vals = new ArrayList<String>(); String val;
			{
			_loop85:
			do {
				if ((LA(1)==31)) {
					val=__xml_item();
					vals.add(val);
				}
				else {
					break _loop85;
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
			_loop77:
			do {
				if ((LA(1)==31)) {
					val=__xml_item();
					vals.add(val);
				}
				else {
					break _loop77;
				}
				
			} while (true);
			}
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_11);
		}
		return vals;
	}
	
	public final void __xml_pack(
		SpellCommand sg
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(14);
			{
			
					String name;
					List<String> aliases = null,i,e;
			name=__xml_name();
			{
			switch ( LA(1)) {
			case 15:
			{
				aliases=__xml_aliases();
				break;
			}
			case 19:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			i=__xml_items();
			e=__xml_exclamations();
			
					sg.setValues(name, aliases, i, e);
				
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_11);
		}
	}
	
	public final String  __xml_name() throws RecognitionException, TokenStreamException {
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
			recover(ex,_tokenSet_12);
		}
		return value;
	}
	
	public final List<String>  __xml_aliases() throws RecognitionException, TokenStreamException {
		List<String> vals = null;
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(15);
			{
			vals = new ArrayList<String>(); String val;
			{
			_loop73:
			do {
				if ((LA(1)==31)) {
					val=__xml_item();
					vals.add(val);
				}
				else {
					break _loop73;
				}
				
			} while (true);
			}
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_13);
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
			_loop89:
			do {
				if ((LA(1)==31)) {
					val=__xml_item();
					vals.add(val);
				}
				else {
					break _loop89;
				}
				
			} while (true);
			}
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_14);
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
			_loop93:
			do {
				if ((LA(1)==31)) {
					val=__xml_item();
					vals.add(val);
				}
				else {
					break _loop93;
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
			match(22);
			{
			
					CampingChat chat = null;
					long chatId = 0;
					String idStr, name, type, allowed;
				
			idStr=__xml_id();
			type=__xml_type();
			name=__xml_name();
			allowed=__xml_allowed();
			
					if (cm != null) {
						if (idStr != null && !"null".equalsIgnoreCase(idStr)) {
							chatId = Long.parseLong(idStr);
						} 
						if (chatId == 0)
							return;
						chat = cm.get(chatId);
						chat.setChatname(name);
						chat.setType(type);
						chat.setAllowed(allowed);
					}
				
			{
			switch ( LA(1)) {
			case 23:
			{
				__xml_actives(chat);
				break;
			}
			case XML_END_TAG:
			case 26:
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
			case 26:
			{
				__xml_announce(chat);
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
			recover(ex,_tokenSet_15);
		}
	}
	
	public final String  __xml_id() throws RecognitionException, TokenStreamException {
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
	
	public final String  __xml_type() throws RecognitionException, TokenStreamException {
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
			recover(ex,_tokenSet_17);
		}
		return value;
	}
	
	public final String  __xml_allowed() throws RecognitionException, TokenStreamException {
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
			recover(ex,_tokenSet_18);
		}
		return value;
	}
	
	public final void __xml_actives(
		CampingChat chat
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(23);
			{
			
					if (chat == null)
						return;
					long id; 
					String value;
				
			{
			_loop105:
			do {
				if ((LA(1)==31)) {
					value=__xml_item();
					
								if (value != null && !"null".equalsIgnoreCase(value)) {
									try {
										id = Long.parseLong(value);
										chat.addActiveUser(id);
									} catch (NumberFormatException e) {
									}
								}	 
						
				}
				else {
					break _loop105;
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
	}
	
	public final void __xml_announce(
		CampingChat chat
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(26);
			{
			pcdata = LT(1);
			match(PCDATA);
			
					if (chat == null)
						return;
					try {
						String value = pcdata.getText();
						boolean tf = Boolean.parseBoolean(value);
						chat.setAnnounce(tf);
					} catch (Exception e) {
					}
				
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
	}
	
	public final String  __xml_nextCampingId() throws RecognitionException, TokenStreamException {
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
			recover(ex,_tokenSet_20);
		}
		return value;
	}
	
	public final void __xml_user(
		CampingUserMonitor um
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(25);
			{
				String campingId = "null", id, username, first, last, initials = null, nickname, birthdayMonth = null, birthdayDay = null, lastUpdate, interaction = null;
				CampingUser user;
			
			{
			switch ( LA(1)) {
			case 32:
			{
				campingId=__xml_campingId();
				break;
			}
			case 33:
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
			{
			switch ( LA(1)) {
			case 38:
			{
				initials=__xml_initials();
				break;
			}
			case 35:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			nickname=__xml_nickname();
			{
			switch ( LA(1)) {
			case 40:
			{
				interaction=__xml_interaction();
				break;
			}
			case XML_END_TAG:
			case 28:
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
			case 28:
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
			
					if (um == null)
						return;
						
					int campingIdInt = CampingUserMonitor.UNKNOWN_USER_ID;
					if (!"null".equalsIgnoreCase(campingId)) {
						campingIdInt = Integer.parseInt(campingId);
					} 
					
					long idInt = CampingUserMonitor.UNKNOWN_USER_ID;
					if (!"null".equalsIgnoreCase(id)) {
						idInt = Long.parseLong(id);
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
						
					user = um.monitor(campingIdInt, idInt, username, first, last, initials, inter);
					user.setNickname(nickname);
					
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
			recover(ex,_tokenSet_20);
		}
	}
	
	public final String  __xml_campingId() throws RecognitionException, TokenStreamException {
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
			recover(ex,_tokenSet_21);
		}
		return value;
	}
	
	public final String  __xml_username() throws RecognitionException, TokenStreamException {
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
			recover(ex,_tokenSet_22);
		}
		return value;
	}
	
	public final String  __xml_first() throws RecognitionException, TokenStreamException {
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
			recover(ex,_tokenSet_23);
		}
		return value;
	}
	
	public final String  __xml_last() throws RecognitionException, TokenStreamException {
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
	
	public final String  __xml_initials() throws RecognitionException, TokenStreamException {
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
			recover(ex,_tokenSet_25);
		}
		return value;
	}
	
	public final String  __xml_nickname() throws RecognitionException, TokenStreamException {
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
			recover(ex,_tokenSet_26);
		}
		return value;
	}
	
	public final String  __xml_interaction() throws RecognitionException, TokenStreamException {
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
			recover(ex,_tokenSet_27);
		}
		return value;
	}
	
	public final String  __xml_birthdayMonth() throws RecognitionException, TokenStreamException {
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
			recover(ex,_tokenSet_28);
		}
		return value;
	}
	
	public final String  __xml_birthdayDay() throws RecognitionException, TokenStreamException {
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
			recover(ex,_tokenSet_7);
		}
		return value;
	}
	
	public final String  __xml_token() throws RecognitionException, TokenStreamException {
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
			recover(ex,_tokenSet_0);
		}
		return value;
	}
	
	public final String  __xml_botUsername() throws RecognitionException, TokenStreamException {
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
			recover(ex,_tokenSet_0);
		}
		return value;
	}
	
	public final String  __xml_adminUser() throws RecognitionException, TokenStreamException {
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
			recover(ex,_tokenSet_0);
		}
		return value;
	}
	
	public final String  __xml_connectOnStartup() throws RecognitionException, TokenStreamException {
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
			recover(ex,_tokenSet_0);
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
		"\"<enhance>\"",
		"\"<rickrolls>\"",
		"\"<overs>\"",
		"\"<excessives>\"",
		"\"<insults>\"",
		"\"<countdown>\"",
		"\"<spell>\"",
		"\"<pack>\"",
		"\"<aliases>\"",
		"\"<adjectives>\"",
		"\"<hypes>\"",
		"\"<dicks>\"",
		"\"<items>\"",
		"\"<exclamations>\"",
		"\"<chats>\"",
		"\"<chat>\"",
		"\"<actives>\"",
		"\"<users>\"",
		"\"<user>\"",
		"\"<announce>\"",
		"PCDATA",
		"\"<birthdayMonth>\"",
		"\"<birthdayDay>\"",
		"\"<nextCampingId>\"",
		"\"<item>\"",
		"\"<campingId>\"",
		"\"<id>\"",
		"\"<username>\"",
		"\"<nickname>\"",
		"\"<first>\"",
		"\"<last>\"",
		"\"<initials>\"",
		"\"<name>\"",
		"\"<interaction>\"",
		"\"<type>\"",
		"\"<allowed>\"",
		"\"<token>\"",
		"\"<botUsername>\"",
		"\"<adminUser>\"",
		"\"<connectOnStartup>\""
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 18880736L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 18876640L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 18876576L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 18874528L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 18874400L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 16777248L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 32L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 512L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { 2147483680L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = { 262144L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	private static final long[] mk_tokenSet_11() {
		long[] data = { 16416L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
	private static final long[] mk_tokenSet_12() {
		long[] data = { 4398047068160L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
	private static final long[] mk_tokenSet_13() {
		long[] data = { 524288L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());
	private static final long[] mk_tokenSet_14() {
		long[] data = { 1048576L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_14 = new BitSet(mk_tokenSet_14());
	private static final long[] mk_tokenSet_15() {
		long[] data = { 4194336L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_15 = new BitSet(mk_tokenSet_15());
	private static final long[] mk_tokenSet_16() {
		long[] data = { 2216203124736L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_16 = new BitSet(mk_tokenSet_16());
	private static final long[] mk_tokenSet_17() {
		long[] data = { 549755813888L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_17 = new BitSet(mk_tokenSet_17());
	private static final long[] mk_tokenSet_18() {
		long[] data = { 75497504L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_18 = new BitSet(mk_tokenSet_18());
	private static final long[] mk_tokenSet_19() {
		long[] data = { 67108896L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_19 = new BitSet(mk_tokenSet_19());
	private static final long[] mk_tokenSet_20() {
		long[] data = { 33554464L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_20 = new BitSet(mk_tokenSet_20());
	private static final long[] mk_tokenSet_21() {
		long[] data = { 8589934592L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_21 = new BitSet(mk_tokenSet_21());
	private static final long[] mk_tokenSet_22() {
		long[] data = { 68719476736L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_22 = new BitSet(mk_tokenSet_22());
	private static final long[] mk_tokenSet_23() {
		long[] data = { 137438953472L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_23 = new BitSet(mk_tokenSet_23());
	private static final long[] mk_tokenSet_24() {
		long[] data = { 309237645312L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_24 = new BitSet(mk_tokenSet_24());
	private static final long[] mk_tokenSet_25() {
		long[] data = { 34359738368L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_25 = new BitSet(mk_tokenSet_25());
	private static final long[] mk_tokenSet_26() {
		long[] data = { 1099780063264L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_26 = new BitSet(mk_tokenSet_26());
	private static final long[] mk_tokenSet_27() {
		long[] data = { 268435488L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_27 = new BitSet(mk_tokenSet_27());
	private static final long[] mk_tokenSet_28() {
		long[] data = { 536870912L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_28 = new BitSet(mk_tokenSet_28());
	
	}
