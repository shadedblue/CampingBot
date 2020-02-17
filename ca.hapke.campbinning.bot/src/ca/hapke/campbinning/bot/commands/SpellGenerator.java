package ca.hapke.campbinning.bot.commands;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingSerializable;
import ca.hapke.campbinning.bot.category.CategoriedItems;
import ca.hapke.campbinning.bot.category.HasCategories;
import ca.hapke.campbinning.bot.commands.response.CommandResult;
import ca.hapke.campbinning.bot.commands.response.TextCommandResult;
import ca.hapke.campbinning.bot.commands.response.fragments.MentionFragment;
import ca.hapke.campbinning.bot.commands.response.fragments.ResultFragment;
import ca.hapke.campbinning.bot.commands.response.fragments.TextFragment;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.util.CampingUtil;
import ca.hapke.campbinning.bot.xml.OutputFormatter;

/**
 * @author Nathan Hapke
 */
public class SpellGenerator extends CampingSerializable implements HasCategories {

	private static final String ADJECTIVE_CATEGORY = "adjective";
	private static final String EXCLAMATION_CATEGORY = "exclamation";
	private static final String ITEM_CATEGORY = "item";

	public SpellGenerator() {
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
	public static final String YA_DIPSHIT = "Spells must be cast upon a victim, ya dipshit.";
	public static final String IM_A_DIPSHIT = "I'm a dipshit, and didn't pick a victim to cast a spell on!";

	public CommandResult spellCommand(CampingUser campingFromUser, CampingUser targetUser, Message message) {
		if (targetUser == null) {
			return new TextCommandResult(BotCommand.SpellDipshit, new MentionFragment(campingFromUser),
					new TextFragment(YA_DIPSHIT));
		}

		CommandResult out = new TextCommandResult(BotCommand.Spell, cast(targetUser));
		SpellGenerator.countSpellActivation(campingFromUser, targetUser);
		return out;
	}

	public ResultFragment[] cast(CampingUser target) {
		String adj = CampingUtil.getRandom(adjectives);
		String item = CampingUtil.getRandom(items);
		String excl = CampingUtil.getRandom(exclamations);

		String punc = hasPunc(excl) ? "" : "!";
		TextFragment a = new TextFragment("I cast the *" + adj + "* of *" + item + "* on ");
		MentionFragment b = new MentionFragment(target);
		TextFragment c = new TextFragment(" and yell \"*" + excl + punc + "\"*");
		return new ResultFragment[] { a, b, c };
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