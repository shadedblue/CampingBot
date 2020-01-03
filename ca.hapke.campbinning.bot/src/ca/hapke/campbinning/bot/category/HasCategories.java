package ca.hapke.campbinning.bot.category;

import java.util.List;

/**
 * @author Nathan Hapke
 *
 */
public interface HasCategories {
	public List<String> getCategoryNames();

	public void addItem(String category, String value);

	public String getContainerName();
}
