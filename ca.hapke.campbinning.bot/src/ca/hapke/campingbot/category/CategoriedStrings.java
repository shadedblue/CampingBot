package ca.hapke.campingbot.category;

import java.util.List;

import ca.hapke.util.CollectionUtil;

/**
 * @author Nathan Hapke
 */
public class CategoriedStrings extends CategoriedItems<String> {

	public CategoriedStrings(String container, String... categoryNames) {
		super(container, categoryNames);
	}

	@Override
	public String search(String cat, String term) {
		List<String> list = getList(cat);
		return CollectionUtil.search(term, list);
	}

}
