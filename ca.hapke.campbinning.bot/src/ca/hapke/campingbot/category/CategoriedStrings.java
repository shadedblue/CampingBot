package ca.hapke.campingbot.category;

import java.util.List;

import ca.hapke.util.CollectionUtil;

/**
 * @author Nathan Hapke
 */
public class CategoriedStrings extends CategoriedItems<String> {

	public CategoriedStrings(String... categoryNames) {
		super(categoryNames);
	}

	@Override
	public String search(String cat, String term) {
		List<String> list = getList(cat);
		return CollectionUtil.search(term, list);
	}

}
