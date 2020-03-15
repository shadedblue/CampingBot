package ca.hapke.campbinning.bot.commands;

import java.util.ArrayList;
import java.util.List;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingBot;
import ca.hapke.campbinning.bot.CampingSerializable;
import ca.hapke.campbinning.bot.category.CategoriedItems;
import ca.hapke.campbinning.bot.category.HasCategories;
import ca.hapke.campbinning.bot.commands.response.CommandResult;
import ca.hapke.campbinning.bot.commands.response.fragments.MentionFragment;
import ca.hapke.campbinning.bot.commands.response.fragments.ResultFragment;
import ca.hapke.campbinning.bot.commands.response.fragments.TextFragment;
import ca.hapke.campbinning.bot.commands.response.fragments.TextStyle;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.util.CampingUtil;
import ca.hapke.campbinning.bot.xml.OutputFormatter;

/**
 * @author Nathan Hapke
 */
public class SpellGenerator extends CampingSerializable implements HasCategories<String> {

	private static final String ADJECTIVE_CATEGORY = "adjective";
	private static final String EXCLAMATION_CATEGORY = "exclamation";
	private static final String ITEM_CATEGORY = "item";
	private CampingBot bot;

	public SpellGenerator(CampingBot bot) {
		this.bot = bot;
		categories = new CategoriedItems<String>(ADJECTIVE_CATEGORY, ITEM_CATEGORY, EXCLAMATION_CATEGORY);
		adjectives = categories.getList(ADJECTIVE_CATEGORY);
		items = categories.getList(ITEM_CATEGORY);
		exclamations = categories.getList(EXCLAMATION_CATEGORY);
	}

	@Override
	public String getContainerName() {
		return "Spell";
	}

	private CategoriedItems<String> categories;
	private List<String> adjectives;
	private List<String> items;
	private List<String> exclamations;

	public CommandResult spellCommand(CampingUser campingFromUser, CampingUser targetUser) {
		SpellResult result = createSpell(campingFromUser, targetUser);
		countSpellActivation(campingFromUser, targetUser);
		return result.provideCommandResult();
	}

	public SpellResult createSpell(CampingUser campingFromUser, CampingUser targetUser) {
		SpellResult result;
		if (targetUser == null) {
			result = new SpellResult(campingFromUser, targetUser, SpellFailure.Dipshit);
		} else if (targetUser == bot.getMeCamping()) {
			result = new SpellResult(campingFromUser, targetUser, SpellFailure.NotMe);
		} else {
			result = new SpellResult(campingFromUser, targetUser, cast(targetUser));
		}
		return result;
	}

	public List<ResultFragment> cast(CampingUser target) {
		String adj = CampingUtil.getRandom(adjectives);
		String item = CampingUtil.getRandom(items);
		String excl = CampingUtil.getRandom(exclamations);

		String punc = hasPunc(excl) ? "" : "!";

		List<ResultFragment> out = new ArrayList<ResultFragment>();
		out.add(new TextFragment("I cast the "));
		out.add(new TextFragment(adj, TextStyle.Bold));
		out.add(new TextFragment(" of "));
		out.add(new TextFragment(item, TextStyle.Bold));
		out.add(new TextFragment(" on "));
		out.add(new MentionFragment(target));
		out.add(new TextFragment(" and yell \""));
		out.add(new TextFragment(excl + punc, TextStyle.Bold));
		out.add(new TextFragment("\""));
		return out;
	}

	public void setAdjectives(List<String> adjectives) {
		if (categories.putAll(ADJECTIVE_CATEGORY, adjectives))
			shouldSave = true;
	}

	public void setItems(List<String> items) {
		if (categories.putAll(ITEM_CATEGORY, items))
			shouldSave = true;
	}

	public void setExclamations(List<String> exclamations) {
		if (categories.putAll(EXCLAMATION_CATEGORY, exclamations))
			shouldSave = true;
	}

	@Override
	public List<String> getCategoryNames() {
		return categories.getCategoryNames();
	}

	@Override
	public void addItem(String category, String value) {
		if (categories.put(category, value))
			shouldSave = true;
	}

	private boolean hasPunc(String excl) {
		char last = excl.charAt(excl.length() - 1);
		return last == '.' || last == '!' || last == '?';
	}

	@Override
	public void getXml(OutputFormatter of) {
		String outerTag = "spell";
		of.start(outerTag);
		of.tagCategories(categories);
		of.finish(outerTag);
	}

	public static void countSpellActivation(CampingUser fromUser, CampingUser targetUser) {
		fromUser.increment(BotCommand.Spell);
	}

}