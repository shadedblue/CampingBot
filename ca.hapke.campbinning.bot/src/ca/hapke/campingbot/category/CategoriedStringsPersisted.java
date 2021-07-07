package ca.hapke.campingbot.category;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.hapke.campingbot.log.CategoriedPersistence;
import ca.hapke.campingbot.log.DatabaseConsumer;

/**
 * CategoriedPersistence is connected to the List<> in the super-class and notifies the DB as appropriate
 * 
 * @author Nathan Hapke
 */
public class CategoriedStringsPersisted extends CategoriedStrings {

	private Map<List<String>, CategoriedPersistence> persistenceMap = new HashMap<>();
	private String container;

	public CategoriedStringsPersisted(String container, String... categoryNames) {
		// HACK need to set the category before calls to add them, so we have to duplicate the code here... annoyingly.
		super();
		this.container = container;
		for (String k : categoryNames) {
			addCategory(k);
		}
	}

	@Override
	protected List<String> addCategory(String cat) {
		List<String> list = data.get(cat);
		if (list != null)
			return list;
		CategoriedPersistence cp = DatabaseConsumer.getInstance().loadCategoriedStrings(/* null, */container, cat);
		list = cp.getValues();
		persistenceMap.put(list, cp);
		return super.addCategory(cat, list);
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
