package ca.hapke.campingbot.commands.spell;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import ca.hapke.campingbot.category.CategoriedItems;
import ca.hapke.campingbot.category.CategoriedStrings;
import ca.hapke.campingbot.commands.api.AbstractCommand;

/**
 * @author Nathan Hapke
 */
public class SpellPacks {

	private Map<String, CategoriedItems<String>> categoriesByGenre = new HashMap<>();
	private Map<String, String> aliasResolver = new HashMap<>();
	private Map<String, Set<String>> allAliases = new HashMap<>();

	public CategoriedItems<String> get(String genre, boolean shouldCreate) {
		CategoriedItems<String> c = categoriesByGenre.get(genre);
		if (c == null) {
			String resolved = aliasResolver.get(genre.toLowerCase());
			c = categoriesByGenre.get(resolved);
		}
		if (c == null && shouldCreate) {
			c = new CategoriedStrings(SpellCommand.ITEM_CATEGORY, SpellCommand.EXCLAMATION_CATEGORY);
			categoriesByGenre.put(genre, c);
		}
		return c;
	}

	public void addAliases(String genre, Collection<String> toAdd) {
		Set<String> current = allAliases.get(genre);
		if (current == null) {
			current = new TreeSet<>();
			allAliases.put(genre, current);
		}
		current.addAll(toAdd);
		for (String a : toAdd) {
			this.aliasResolver.put(a.toLowerCase(), genre);
		}
	}

	public List<String> addCategoryNames(List<String> out) {
		for (Entry<String, CategoriedItems<String>> byGenre : categoriesByGenre.entrySet()) {
			String genre = byGenre.getKey();
			for (String cat : byGenre.getValue().getCategoryNames()) {
				String x = genre + AbstractCommand.DELIMITER + cat;
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
			int n = items.getSize(SpellCommand.EXCLAMATION_CATEGORY);
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
			j++;
		}
		return genres[size - 1];
	}

	public Set<Entry<String, CategoriedItems<String>>> entrySet() {
		return categoriesByGenre.entrySet();
	}

	public Set<String> getAliases(String genre) {
		return allAliases.get(genre);
	}

	public int size() {
		return categoriesByGenre.size();
	}
}
