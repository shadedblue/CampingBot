package ca.hapke.campbinning.bot.commands.inline;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * @author Nathan Hapke
 */
public class TopicManager {
	private LoadingCache<String, String> confirmed;

	public TopicManager() {
		confirmed = CacheBuilder.newBuilder().expireAfterWrite(48, TimeUnit.HOURS)
				.build(new CacheLoader<String, String>() {
					@Override
					public String load(String key) throws Exception {
						return null;
					}
				});
	}

	private static String createKey(String value) {
		return value.toLowerCase().strip();
	}

	public boolean add(String topic) {
		String key = createKey(topic);
		if (confirmed.getIfPresent(key) != null)
			return false;

		confirmed.put(key, topic);
		return true;
	}

	public long size() {
		return confirmed.size();
	}

	public ConcurrentMap<String, String> asMap() {
		return confirmed.asMap();
	}

}
