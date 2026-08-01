package ca.hapke.campingbot.api;

/**
 * @author Nathan Hapke
 */
public interface CategoryChangedListener<T> {

//	public void categoryAdded(String category);

	public void itemAdded(String category, T item);
}
