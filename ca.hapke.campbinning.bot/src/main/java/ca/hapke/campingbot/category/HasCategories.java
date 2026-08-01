package ca.hapke.campingbot.category;

import java.util.List;

/**
 * @author Nathan Hapke
 *
 */
public interface HasCategories<T> {
	public List<String> getCategoryNames();

	public void addItem(String category, T value);

	public String getContainerName();

	public int getSize(String s);

}
