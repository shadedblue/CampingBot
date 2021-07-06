package ca.hapke.campingbot.category;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.hapke.campingbot.log.CategoriedPersistence;
import ca.hapke.campingbot.log.DatabaseConsumer;

/**
 * @author Nathan Hapke
 */
public class PersistedCategoriedStrings extends CategoriedStrings {

	private Map<List<String>, CategoriedPersistence> persistenceMap = new HashMap<>();

	public PersistedCategoriedStrings(String... categoryNames) {
		super(categoryNames);
		// TODO Auto-generated constructor stub
	}

	public void loadPersistence(String category, List<String> values, CategoriedPersistence persistence) {
		if (!contains(category)) {
			names.add(category);
			data.put(category, values);
		} else {
			List<String> oldList = data.get(category);

			// merge old list into new one (without duplicates)
			for (String t : oldList) {
				if (!values.contains(t)) {
					values.add(t);
				}
			}
			data.put(category, values);
			persistenceMap.put(values, persistence);
		}
	}

	@Override
	public boolean put(String cat, String item) {
		boolean result = super.put(cat, item);
		if (result) {
			CategoriedPersistence persistence = persistenceMap.get(getList(cat));
			DatabaseConsumer.getInstance().updatePersistence(persistence);
		}
		return result;
	}

	@Override
	public boolean putAll(String cat, Collection<String> items) {
		boolean result = super.putAll(cat, items);
		if (result) {
			CategoriedPersistence persistence = persistenceMap.get(getList(cat));
			DatabaseConsumer.getInstance().updatePersistence(persistence);
		}
		return result;
	}

	@Override
	public boolean putAll(String cat, String... items) {
		boolean result = super.putAll(cat, items);
		if (result) {
			CategoriedPersistence persistence = persistenceMap.get(getList(cat));
			DatabaseConsumer.getInstance().updatePersistence(persistence);
		}
		return result;
	}
}
