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

@SuppressWarnings("all")
public interface LoadStatsParserTokenTypes {
	int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	// "<camping>" = 4
	int XML_END_TAG = 5;
	// "<party>" = 6
	// "<voting>" = 7
	// "<excessives>" = 8
	// "<assholes>" = 9
	// "<insults>" = 10
	// "<mediocres>" = 11
	// "<nices>" = 12
	// "<system>" = 13
	// "<db>" = 14
	// "<countdown>" = 15
	// "<spell>" = 16
	// "<adjectives>" = 17
	// "<hypes>" = 18
	// "<items>" = 19
	// "<exclamations>" = 20
	// "<chats>" = 21
	// "<chat>" = 22
	// "<users>" = 23
	// "<user>" = 24
	// "<birthdayMonth>" = 25
	int PCDATA = 26;
	// "<birthdayDay>" = 27
	// "<nextCampingId>" = 28
	// "<token>" = 29
	// "<botUsername>" = 30
	// "<announceChat>" = 31
	// "<adminUser>" = 32
	// "<dbHost>" = 33
	// "<dbPort>" = 34
	// "<dbUser>" = 35
	// "<dbPass>" = 36
	// "<item>" = 37
	// "<campingId>" = 38
	// "<id>" = 39
	// "<username>" = 40
	// "<nickname>" = 41
	// "<first>" = 42
	// "<last>" = 43
	// "<type>" = 44
	// "<allowed>" = 45
}
