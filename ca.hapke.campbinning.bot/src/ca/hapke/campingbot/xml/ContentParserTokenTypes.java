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

@SuppressWarnings("all")
public interface ContentParserTokenTypes {
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
	// "<countdown>" = 12
	// "<spell>" = 13
	// "<pack>" = 14
	// "<aliases>" = 15
	// "<adjectives>" = 16
	// "<hypes>" = 17
	// "<dicks>" = 18
	// "<items>" = 19
	// "<exclamations>" = 20
	// "<chats>" = 21
	// "<chat>" = 22
	// "<actives>" = 23
	// "<users>" = 24
	// "<user>" = 25
	// "<announce>" = 26
	int PCDATA = 27;
	// "<birthdayMonth>" = 28
	// "<birthdayDay>" = 29
	// "<nextCampingId>" = 30
	// "<item>" = 31
	// "<campingId>" = 32
	// "<id>" = 33
	// "<username>" = 34
	// "<nickname>" = 35
	// "<first>" = 36
	// "<last>" = 37
	// "<initials>" = 38
	// "<name>" = 39
	// "<interaction>" = 40
	// "<type>" = 41
	// "<allowed>" = 42
	// "<token>" = 43
	// "<botUsername>" = 44
	// "<adminUser>" = 45
	// "<connectOnStartup>" = 46
}
