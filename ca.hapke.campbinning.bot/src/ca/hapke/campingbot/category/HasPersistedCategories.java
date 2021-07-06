package ca.hapke.campingbot.category;

import java.util.List;

import ca.hapke.campingbot.log.CategoriedPersistence;

/**
 * @author Nathan Hapke
 */
public interface HasPersistedCategories {

	public void loadPersistence(CategoriedPersistence cats);

	public String getContainerName();

	public List<String> getCategoryNames();
}
