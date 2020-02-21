package ca.hapke.campbinning.bot.category;

import java.util.List;

/**
 * @author Nathan Hapke
 *
 */
public interface HasCategories<T> {
	public List<String> getCategoryNames();

	public void addItem(String category, T value);

	public String getContainerName();
}
