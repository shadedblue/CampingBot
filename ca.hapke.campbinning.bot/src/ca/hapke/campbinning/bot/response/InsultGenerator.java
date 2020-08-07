package ca.hapke.campbinning.bot.response;

import java.util.List;

import ca.hapke.campbinning.bot.CampingSerializable;
import ca.hapke.campbinning.bot.category.CategoriedItems;
import ca.hapke.campbinning.bot.category.HasCategories;
import ca.hapke.campbinning.bot.util.CampingUtil;
import ca.hapke.campbinning.bot.xml.OutputFormatter;

/**
 * Singleton for the InsultFragments to find me
 * 
 * @author Nathan Hapke
 */
public class InsultGenerator implements HasCategories<String>, CampingSerializable {
	private static InsultGenerator instance = new InsultGenerator();

	public static InsultGenerator getInstance() {
		return instance;
	}

	private InsultGenerator() {
		categories = new CategoriedItems<>(INSULT_CATEGORY);
		insultList = categories.getList(INSULT_CATEGORY);
	}

	private boolean shouldSave = false;
	private static final String INSULT_CATEGORY = "insult";
	private static final String INSULTS_CONTAINER = "Insults";
	private CategoriedItems<String> categories;
	private List<String> insultList;

	@Override
	public List<String> getCategoryNames() {
		return categories.getCategoryNames();
	}

	@Override
	public void addItem(String category, String value) {
		if (categories.put(category, value))
			shouldSave = true;
	}

	@Override
	public String getContainerName() {
		return INSULTS_CONTAINER;
	}

	@Override
	public boolean shouldSave() {
		return shouldSave;
	}

	@Override
	public void getXml(OutputFormatter of) {
//		of.start(XML_TAG);
//
//		of.finish(XML_TAG);
		of.tagAndValue(INSULT_CATEGORY, categories.getList(INSULT_CATEGORY));

		shouldSave = false;
	}

	public String getInsult() {
		return CampingUtil.getRandom(insultList);
	}

}
