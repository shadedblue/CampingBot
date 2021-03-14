package ca.hapke.campingbot.commands.spell;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.telegram.telegrambots.meta.api.objects.Message;

import ca.hapke.calendaring.monitor.CalendarMonitor;
import ca.hapke.campingbot.BotChoicePriority;
import ca.hapke.campingbot.CampingBot;
import ca.hapke.campingbot.api.CampingSerializable;
import ca.hapke.campingbot.category.CategoriedItems;
import ca.hapke.campingbot.category.HasCategories;
import ca.hapke.campingbot.channels.CampingChat;
import ca.hapke.campingbot.channels.CampingChatManager;
import ca.hapke.campingbot.commands.api.AbstractCommand;
import ca.hapke.campingbot.commands.api.BotCommandIds;
import ca.hapke.campingbot.commands.api.ResponseCommandType;
import ca.hapke.campingbot.commands.api.SlashCommand;
import ca.hapke.campingbot.commands.api.SlashCommandType;
import ca.hapke.campingbot.response.CommandResult;
import ca.hapke.campingbot.response.fragments.MentionFragment;
import ca.hapke.campingbot.response.fragments.ResultFragment;
import ca.hapke.campingbot.response.fragments.TextFragment;
import ca.hapke.campingbot.response.fragments.TextStyle;
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.xml.OutputFormatter;
import ca.hapke.util.CollectionUtil;
import ca.hapke.util.StringUtil;

/**
 * @author Nathan Hapke
 */
public class SpellCommand extends AbstractCommand implements HasCategories<String>, CampingSerializable, SlashCommand {

	private static final String SPELL = "Spell";

	public static final SlashCommandType SlashSpellCommand = new SlashCommandType(SPELL, "spell",
			BotCommandIds.SPELL | BotCommandIds.USE);
	public static final ResponseCommandType SpellDipshitCommand = new ResponseCommandType("SpellDipshit",
			BotCommandIds.SPELL | BotCommandIds.FAILURE);

	private static final SlashCommandType[] SLASH_COMMANDS = new SlashCommandType[] { SlashSpellCommand };

	private static final TextFragment CASTS_THE = new TextFragment(" casts the ");
	private static final TextFragment OF = new TextFragment(" of ");
	private static final TextFragment ON = new TextFragment(" on ");
	private static final TextFragment AND_YELLS = new TextFragment(" and yells \"");
	private static final TextFragment END_QUOTE = new TextFragment("\"");

	static final String ADJECTIVE_CATEGORY = "adjective";
	static final String EXCLAMATION_CATEGORY = "exclamation";
	static final String ITEM_CATEGORY = "item";

	private CampingBot bot;
	private boolean shouldSave = false;

	private SpellPacks categoriesByGenre = new SpellPacks();
	private SpellCastingManager castManager;

	private CategoriedItems<String> categories;
	private List<String> adjectives;

	public SpellCommand(CampingBot bot) {
		this.bot = bot;
		castManager = new SpellCastingManager(bot);
		CalendarMonitor.getInstance().add(castManager);

		categories = new CategoriedItems<String>(ADJECTIVE_CATEGORY);
		adjectives = categories.getList(ADJECTIVE_CATEGORY);
	}

	@Override
	public CommandResult respondToSlashCommand(SlashCommandType command, Message message, Long chatId,
			CampingUser campingFromUser) {
		CampingUser targetUser = bot.findTarget(message, false, true, BotChoicePriority.Last);
		SpellResult result;
		if (targetUser == null) {
			result = new SpellResult(campingFromUser, targetUser, SpellFailure.Dipshit);
		} else {
			result = new SpellResult(campingFromUser, targetUser, cast(campingFromUser, targetUser));
		}

		CampingChat chat = CampingChatManager.getInstance(bot).get(chatId);
//		boolean immediate = 
		castManager.softCast(campingFromUser, targetUser, result, chat);

//		if (immediate)
//			return result.provideCommandResult();
//		else
		return null;
	}

	public List<ResultFragment> cast(CampingUser caster, CampingUser victim) {
		CategoriedItems<String> categories = categoriesByGenre.getRandomPack();

		List<String> items = categories.getList(ITEM_CATEGORY);
		List<String> exclamations = categories.getList(EXCLAMATION_CATEGORY);

		String adj = CollectionUtil.getRandom(adjectives);
		String item = CollectionUtil.getRandom(items);
		String excl = CollectionUtil.getRandom(exclamations);
		if (!StringUtil.endsWithPunctuation(excl)) {
			excl += "!";
		}

		List<ResultFragment> out = new ArrayList<ResultFragment>();
		out.add(new MentionFragment(caster));
		out.add(CASTS_THE);
		out.add(new TextFragment(adj, TextStyle.Bold));
		out.add(OF);
		out.add(new TextFragment(item, TextStyle.Bold));
		out.add(ON);
		out.add(new MentionFragment(victim));
		out.add(AND_YELLS);
		out.add(new TextFragment(excl, TextStyle.Bold));

		out.add(END_QUOTE);
		return out;
	}

	@Override
	public String getContainerName() {
		return SPELL;
	}

	@Override
	public SlashCommandType[] getSlashCommandsToRespondTo() {
		return SLASH_COMMANDS;
	}

	public void setAdjectives(List<String> adjectives) {
		if (categories.putAll(ADJECTIVE_CATEGORY, adjectives))
			shouldSave = true;
	}

	public void setValues(String genre, List<String> items, List<String> exclamations) {
		CategoriedItems<String> categories = categoriesByGenre.get(genre);

		if (categories.putAll(ITEM_CATEGORY, items))
			shouldSave = true;

		if (categories.putAll(EXCLAMATION_CATEGORY, exclamations))
			shouldSave = true;
	}

	@Override
	public List<String> getCategoryNames() {
		List<String> extras = categories.getCategoryNames();
		int extraSize = extras.size();
		List<String> out = new ArrayList<>(categoriesByGenre.size() * 2 + extraSize);
		out.addAll(extras);
		categoriesByGenre.addCategoryNames(out);
		return out;
	}

	@Override
	public List<String> getCategory(String name) {
		if (this.categories.contains(name)) {
			return this.categories.getList(name);
		}
		try {
			int splitter = name.indexOf(SpellPacks.DELIMITER);
			String genre = name.substring(0, splitter);
			String category = name.substring(splitter + 1);

			CategoriedItems<String> categories = categoriesByGenre.get(genre);
			return categories.getList(category);
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public void addItem(String category, String value) {
		CategoriedItems<String> cats;
		if (category.contains(DELIMITER)) {
			int splitter = category.indexOf(SpellPacks.DELIMITER);
			String genre = category.substring(0, splitter);
			category = category.substring(splitter + 1);

			cats = categoriesByGenre.get(genre);
		} else {
			cats = this.categories;
		}
		if (cats.put(category, value))
			shouldSave = true;
	}

	@Override
	public String getCommandName() {
		return SPELL;
	}

	@Override
	public boolean shouldSave() {
		return shouldSave;
	}

	@Override
	public void getXml(OutputFormatter of) {
		String outerTag = "spell";
		of.start(outerTag);

		// for adjectives
		of.tagCategories(categories);

		for (Map.Entry<String, CategoriedItems<String>> e : categoriesByGenre.entrySet()) {
			String innerTag = "pack";
			of.start(innerTag);
			of.tagAndValue("name", e.getKey());
			of.tagCategories(e.getValue());
			of.finish(innerTag);
		}

//		of.tagCategories(categories);
		of.finish(outerTag);

		shouldSave = false;
	}
}