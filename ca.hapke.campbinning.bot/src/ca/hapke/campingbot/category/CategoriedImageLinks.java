package ca.hapke.campingbot.category;

import ca.hapke.campingbot.util.ImageLink;

/**
 * @author Nathan Hapke
 */
public class CategoriedImageLinks extends CategoriedItems<ImageLink> {

	public CategoriedImageLinks(String container, String... categoryNames) {
		super(container, categoryNames);
	}

	@Override
	public ImageLink search(String cat, String term) {
		// TODO Auto-generated method stub
		return null;
	}

}
