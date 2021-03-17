package ca.hapke.campingbot.category;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Could make this a Keyed type that extends Comparable<K>
 * 
 * @author Nathan Hapke
 */
public class CategoriedItems<T> {
	private List<String> names = new ArrayList<>();
	private Map<String, List<T>> data = new HashMap<>();

	public CategoriedItems(String... categoryNames) {
		for (String k : categoryNames) {
			addCategory(k);
		}
	}

	public List<T> addCategory(String cat) {
		names.add(cat);

		List<T> result = new ArrayList<T>();
		data.put(cat, result);
		return result;
	}

	public boolean contains(String s) {
		return names.contains(s);
	}

	public List<T> getList(String cat) {
		List<T> list = data.get(cat);
		if (list == null)
			list = addCategory(cat);

		return list;
	}

	public List<String> getCategoryNames() {
		return names;
	}

	public boolean put(String cat, T item) {
		if (item == null || (item instanceof String && "null".equalsIgnoreCase((String) item)))
			return false;
		List<T> list = getList(cat);
		return list.add(item);
	}

	public boolean putAll(String cat, Collection<T> items) {
		boolean result = false;
		List<T> list = getList(cat);
		for (T item : items) {
			if (item == null || (item instanceof String && "null".equalsIgnoreCase((String) item)))
				continue;

			if (list.add(item))
				result = true;
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public boolean putAll(String cat, T... items) {
		boolean result = false;
		List<T> list = getList(cat);
		for (T item : items) {
			if (item == null || (item instanceof String && "null".equalsIgnoreCase((String) item)))
				continue;

			if (list.add(item))
				result = true;
		}
		return result;
	}
}
