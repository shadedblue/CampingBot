// $ANTXR : "ConfigParser.antxr" -> "ConfigParser.java"$
// GENERATED CODE - DO NOT EDIT!

    package ca.hapke.campingbot.xml;
    
    import ca.hapke.campingbot.*;
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
public class ConfigParser extends com.javadude.antxr.LLkParser       implements ConfigParserTokenTypes
 {
	// ANTXR XML Mode Support
	private static Map<String, String> __xml_namespaceMap = new HashMap<String, String>();
	public static Map<String, String> getNamespaceMap() {return __xml_namespaceMap;}
	public static String resolveNamespace(String prefix) {
		if (prefix == null || "".equals(prefix))
			return "";
		return __xml_namespaceMap.get(prefix);
	}


protected ConfigParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public ConfigParser(TokenBuffer tokenBuf) {
  this(tokenBuf,1);
}

protected ConfigParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public ConfigParser(TokenStream lexer) {
  this(lexer,1);
}

public ConfigParser(ParserSharedInputState state) {
  super(state,1);
  tokenNames = _tokenNames;
}

	public final void document(
		 CampingSystem cs
	) throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			__xml_camping( cs);
			match(Token.EOF_TYPE);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
	}
	
	public final void __xml_camping(
		CampingSystem cs
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(4);
			{
			__xml_system(cs);
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
			match(6);
			{
			
					String token,botUsername,adminUser = null; 
				
			token=__xml_token();
			botUsername=__xml_botUsername();
			{
			switch ( LA(1)) {
			case 11:
			{
				adminUser=__xml_adminUser();
				break;
			}
			case XML_END_TAG:
			case 7:
			case 18:
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
			case 18:
			{
				__xml_connectOnStartup(cs);
				break;
			}
			case XML_END_TAG:
			case 7:
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
				
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final String  __xml_token() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(8);
			{
			pcdata = LT(1);
			match(PCDATA);
			value = pcdata.getText();
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
		return value;
	}
	
	public final String  __xml_botUsername() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(10);
			{
			pcdata = LT(1);
			match(PCDATA);
			value = pcdata.getText();
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
		return value;
	}
	
	public final String  __xml_adminUser() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(11);
			{
			pcdata = LT(1);
			match(PCDATA);
			value = pcdata.getText();
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_4);
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
			match(18);
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
			recover(ex,_tokenSet_5);
		}
	}
	
	public final void __xml_db(
		CampingSystem cs
	) throws RecognitionException, TokenStreamException {
		
		Token  __xml_startTag = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(7);
			{
			
					String host,port,user,pass,db,driver; 
				
			host=__xml_dbHost();
			port=__xml_dbPort();
			user=__xml_dbUser();
			pass=__xml_dbPass();
			db=__xml_dbDb();
			driver=__xml_dbDriver();
			
					int port2 = 5432;
					if (!"null".equalsIgnoreCase(port)) {
						port2 = Integer.parseInt(port);
					} 
					cs.enableDb(host, port2, user, pass, db, driver);
				
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final String  __xml_dbHost() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(12);
			{
			pcdata = LT(1);
			match(PCDATA);
			value = pcdata.getText();
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_6);
		}
		return value;
	}
	
	public final String  __xml_dbPort() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(13);
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
	
	public final String  __xml_dbUser() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(14);
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
	
	public final String  __xml_dbPass() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(15);
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
	
	public final String  __xml_dbDb() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(16);
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
	
	public final String  __xml_dbDriver() throws RecognitionException, TokenStreamException {
		String value = null;
		
		Token  __xml_startTag = null;
		Token  pcdata = null;
		
		try {      // for error handling
			__xml_startTag = LT(1);
			match(17);
			{
			pcdata = LT(1);
			match(PCDATA);
			value = pcdata.getText();
			}
			match(XML_END_TAG);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
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
		"\"<system>\"",
		"\"<db>\"",
		"\"<token>\"",
		"PCDATA",
		"\"<botUsername>\"",
		"\"<adminUser>\"",
		"\"<dbHost>\"",
		"\"<dbPort>\"",
		"\"<dbUser>\"",
		"\"<dbPass>\"",
		"\"<dbDb>\"",
		"\"<dbDriver>\"",
		"\"<connectOnStartup>\""
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 32L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 1024L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 264352L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 262304L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 160L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 8192L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 16384L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 32768L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { 65536L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = { 131072L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	
	}
