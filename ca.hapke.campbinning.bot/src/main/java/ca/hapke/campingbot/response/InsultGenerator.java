package ca.hapke.campingbot.response;

import java.util.List;

import ca.hapke.campingbot.category.CategoriedStringsPersisted;
import ca.hapke.campingbot.category.HasCategories;

/**
 * Singleton for the InsultFragments to find me
 * 
 * @author Nathan Hapke
 */
public class InsultGenerator implements HasCategories<String> {
	private static InsultGenerator instance = new InsultGenerator();

	public static InsultGenerator getInstance() {
		return instance;
	}

	private InsultGenerator() {
		categories = new CategoriedStringsPersisted(INSULTS_CONTAINER, INSULT_CATEGORY);
	}

	private static final String INSULT_CATEGORY = "insult";
	public static final String INSULTS_CONTAINER = "Insults";
	private CategoriedStringsPersisted categories;

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


}
