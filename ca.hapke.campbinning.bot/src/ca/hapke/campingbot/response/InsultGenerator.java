package ca.hapke.campingbot.response;

import java.util.List;

import ca.hapke.campingbot.category.HasCategories;
import ca.hapke.campingbot.category.HasPersistedCategories;
import ca.hapke.campingbot.category.PersistedCategoriedStrings;
import ca.hapke.campingbot.log.CategoriedPersistence;

/**
 * Singleton for the InsultFragments to find me
 * 
 * @author Nathan Hapke
 */
public class InsultGenerator implements HasCategories<String>, HasPersistedCategories {
	private static InsultGenerator instance = new InsultGenerator();

	public static InsultGenerator getInstance() {
		return instance;
	}

	private InsultGenerator() {
		categories = new PersistedCategoriedStrings(INSULT_CATEGORY);
	}

	private static final String INSULT_CATEGORY = "insult";
	public static final String INSULTS_CONTAINER = "Insults";
	private PersistedCategoriedStrings categories;

	@Override
	public List<String> getCategoryNames() {
		return categories.getCategoryNames();
	}

	@Override
	public void addItem(String category, String value) {
		categories.put(category, value);
	}

	@Override
	public String getContainerName() {
		return INSULTS_CONTAINER;
	}

	public String getInsult() {
		return categories.getRandom(INSULT_CATEGORY);
	}

	@Override
	public int getSize(String s) {
		return categories.getSize(s);
	}

	@Override
	public void loadPersistence(CategoriedPersistence cats) {
		List<String> insults = cats.getValues();
		categories.loadPersistence(INSULT_CATEGORY, insults, cats);
	}

}
