package ca.hapke.campingbot.category;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.hapke.util.CollectionUtil;

/**
 * Could make this a Keyed type that extends Comparable<K>
 * 
 * @author Nathan Hapke
 */
public abstract class CategoriedItems<T> {
	protected List<String> names = new ArrayList<>();
	protected Map<String, List<T>> data = new HashMap<>();

	public CategoriedItems(String... categoryNames) {
		for (String k : categoryNames) {
			addCategory(k);
		}
	}

	protected List<T> addCategory(String cat) {
		return addCategory(cat, new ArrayList<T>());
	}

	protected List<T> addCategory(String cat, List<T> result) {
		names.add(cat);
		data.put(cat, result);
		return result;
	}

	public boolean contains(String s) {
		return names.contains(s);
	}

	public T getRandom(String cat) {
		return CollectionUtil.getRandom(getList(cat));
	}

	public abstract T search(String cat, String term);

	public int getSize(String cat) {
		return getList(cat).size();
	}

	protected List<T> getList(String cat) {
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
		if (list.contains(item))
			return false;
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
