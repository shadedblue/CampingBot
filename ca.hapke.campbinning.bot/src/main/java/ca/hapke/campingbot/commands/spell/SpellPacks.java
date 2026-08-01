package ca.hapke.campingbot.commands.spell;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;

import ca.hapke.campingbot.category.CategoriedItems;
import ca.hapke.campingbot.category.CategoriedStringsPersisted;
import ca.hapke.campingbot.commands.api.AbstractCommand;

/**
 * @author Nathan Hapke
 */
public class SpellPacks {

	private static final String ALIAS_CATEGORY = "alias";
	private Map<String, String> aliasResolver = new HashMap<>();
	private Map<String, CategoriedItems<String>> categoriesByGenre = new HashMap<>();
	private List<String> categories = new ArrayList<>();
	private ListModel<String> pm = new AbstractListModel<String>() {
		private static final long serialVersionUID = -2375270006785426367L;

		@Override
		public int getSize() {
			return categories.size();
		}

		@Override
		public String getElementAt(int i) {
			return categories.get(i);
		}
	};

	public CategoriedItems<String> get(String genre, boolean shouldCreate) {
		CategoriedItems<String> c = categoriesByGenre.get(genre);
		if (c == null) {
			String resolved = aliasResolver.get(genre.toLowerCase());
			c = categoriesByGenre.get(resolved);
			categories.add(genre);
		}
		if (c == null && shouldCreate) {
			String spell_genre = SpellCommand.SPELL + AbstractCommand.DELIMITER + genre;
			c = new CategoriedStringsPersisted(spell_genre, SpellCommand.ITEM_CATEGORY,
					SpellCommand.EXCLAMATION_CATEGORY, ALIAS_CATEGORY);
			categoriesByGenre.put(genre, c);
			List<String> aliases = c.getListView(ALIAS_CATEGORY);
			for (String a : aliases) {
				this.aliasResolver.put(a.toLowerCase(), genre);
			}
		}
		return c;
	}

	public void addAliases(String genre, Collection<String> toAdd) {
		CategoriedItems<String> pack = categoriesByGenre.get(genre);
		for (String a : toAdd) {
			if (pack.put(ALIAS_CATEGORY, a))
				this.aliasResolver.put(a.toLowerCase(), genre);
		}
	}

	public void addAlias(String genre, String toAdd) {
		CategoriedItems<String> pack = categoriesByGenre.get(genre);
		if (pack.put(ALIAS_CATEGORY, toAdd))
			this.aliasResolver.put(toAdd.toLowerCase(), genre);
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

	public List<String> getAliases(String genre) {
		CategoriedItems<String> pack = categoriesByGenre.get(genre);
		return pack.getListView(ALIAS_CATEGORY);
	}

	public int size() {
		return categoriesByGenre.size();
	}

	public ListModel<String> getPacksModel() {
		return pm;
	}

}
