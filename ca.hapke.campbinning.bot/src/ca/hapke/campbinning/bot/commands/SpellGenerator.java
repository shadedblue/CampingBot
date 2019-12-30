package ca.hapke.campbinning.bot.commands;

import java.util.List;

import ca.hapke.campbinning.bot.BotCommand;
import ca.hapke.campbinning.bot.CampingSerializable;
import ca.hapke.campbinning.bot.CampingUtil;
import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.xml.OutputFormatter;

/**
 * @author Nathan Hapke
 */
public class SpellGenerator extends CampingSerializable {

	private List<String> adjectives;
	private List<String> items;
	private List<String> exclamations;

	public void setAdjectives(List<String> adjectives) {
		this.adjectives = adjectives;
	}

	public void setItems(List<String> items) {
		this.items = items;
	}

	public void setExclamations(List<String> exclamations) {
		this.exclamations = exclamations;
	}

	public String cast(String target) {
		String adj = CampingUtil.getRandom(adjectives);
		String item = CampingUtil.getRandom(items);
		String excl = CampingUtil.getRandom(exclamations);

		String punc = hasPunc(excl) ? "" : "!";
		return "I cast the *" + adj + "* of *" + item + "* on " + target + " and yell \"*" + excl + punc + "\"*";
	}

	private boolean hasPunc(String excl) {
		char last = excl.charAt(excl.length() - 1);
		return last == '.' || last == '!' || last == '?';
	}

	public List<String> getAdjectives() {
		return adjectives;
	}

	public List<String> getItems() {
		return items;
	}

	public List<String> getExclamations() {
		return exclamations;
	}

	@Override
	public void getXml(OutputFormatter of) {
		String outerTag = "spell";
		of.start(outerTag);
		of.tagAndValue("adjective", adjectives);
		of.tagAndValue("item", items);
		of.tagAndValue("exclamation", exclamations);
		of.finish(outerTag);
	}

	public static void countSpellActivation(CampingUser fromUser, CampingUser targetUser) {
		fromUser.increment(BotCommand.Spell);
		targetUser.victimize(BotCommand.Spell);
	}

}