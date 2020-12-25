// $ANTXR : "ConfigParser.antxr" -> "ConfigParser.java"$
// GENERATED CODE - DO NOT EDIT!

    package ca.hapke.campingbot.xml;
    
    import ca.hapke.campingbot.*;
    import ca.hapke.campingbot.users.*;
    import ca.hapke.campingbot.channels.*;
    import ca.hapke.campingbot.commands.*;
    import ca.hapke.campingbot.response.*;
    import ca.hapke.campingbot.voting.*;
    import java.util.*;

@SuppressWarnings("all")
public interface ConfigParserTokenTypes {
	int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	// "<camping>" = 4
	int XML_END_TAG = 5;
	// "<party>" = 6
	// "<enhance>" = 7
	// "<rickrolls>" = 8
	// "<overs>" = 9
	// "<excessives>" = 10
	// "<insults>" = 11
	// "<system>" = 12
	// "<db>" = 13
	// "<countdown>" = 14
	// "<spell>" = 15
	// "<adjectives>" = 16
	// "<hypes>" = 17
	// "<dicks>" = 18
	// "<items>" = 19
	// "<exclamations>" = 20
	// "<chats>" = 21
	// "<chat>" = 22
	// "<users>" = 23
	// "<user>" = 24
	// "<connectOnStartup>" = 25
	int PCDATA = 26;
	// "<announce>" = 27
	// "<birthdayMonth>" = 28
	// "<birthdayDay>" = 29
	// "<nextCampingId>" = 30
	// "<token>" = 31
	// "<botUsername>" = 32
	// "<adminUser>" = 33
	// "<dbHost>" = 34
	// "<dbPort>" = 35
	// "<dbUser>" = 36
	// "<dbPass>" = 37
	// "<dbDb>" = 38
	// "<item>" = 39
	// "<campingId>" = 40
	// "<id>" = 41
	// "<username>" = 42
	// "<nickname>" = 43
	// "<first>" = 44
	// "<last>" = 45
	// "<name>" = 46
	// "<interaction>" = 47
	// "<type>" = 48
	// "<allowed>" = 49
}
