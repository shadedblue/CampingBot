package ca.hapke.campingbot.commands.spell;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ca.hapke.campingbot.category.CategoriedItems;

/**
 * @author Nathan Hapke
 */
public class SpellPacks {

	private Map<String, CategoriedItems<String>> categoriesByGenre = new HashMap<>();

	static final String DELIMITER = ":";

	public CategoriedItems<String> get(String genre) {
		CategoriedItems<String> c = categoriesByGenre.get(genre);
		if (c == null) {
			c = new CategoriedItems<String>(SpellCommand.ADJECTIVE_CATEGORY, SpellCommand.ITEM_CATEGORY,
					SpellCommand.EXCLAMATION_CATEGORY);
			categoriesByGenre.put(genre, c);
		}
		return c;
	}

	public List<String> getCategoryNames() {
		List<String> out = new ArrayList<>(categoriesByGenre.size() * 3);
		for (Entry<String, CategoriedItems<String>> byGenre : categoriesByGenre.entrySet()) {
			String genre = byGenre.getKey();
			for (String cat : byGenre.getValue().getCategoryNames()) {
				String x = genre + DELIMITER + cat;
				out.add(x);
			}
		}

		return out;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public CategoriedItems<String> getRandomPack() {
		int size = categoriesByGenre.size();
		int[] qtys = new int[size];
		int i = 0;
		int total = 0;
		Collection<CategoriedItems<String>> values = categoriesByGenre.values();
		CategoriedItems[] genres = new CategoriedItems[size];
		genres = values.toArray(genres);
		for (CategoriedItems items : genres) {
			int n = items.getList(SpellCommand.EXCLAMATION_CATEGORY).size();
			qtys[i] = n;
			total += n;
			i++;
		}

		int x = (int) (Math.random() * total);
		for (int j = 0; j < size; j++) {
			int qty = qtys[j];
			if (x <= qty) {
				return genres[j];
			}
			x -= qty;
		}
		return genres[size - 1];
	}

	public Set<Entry<String, CategoriedItems<String>>> entrySet() {
		return categoriesByGenre.entrySet();
	}
}
