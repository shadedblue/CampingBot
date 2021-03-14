package ca.hapke.campingbot.commands.spell;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import ca.hapke.campingbot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class SpellPropogationManager {

	// private static final int DELAY_MULTIPLIER = 15;
	private static final long EXPIRY_DURATION = 10 * 60 * 1000l;
	private static final String DELIMITER = "$";

	private Map<CampingUser, LinkedList<Long>> delayTimestamps = new HashMap<>();
	private Map<String, LinkedList<Long>> comboTimestamps = new HashMap<>();
	private Map<CampingUser, Long> deadTimestamps = new HashMap<>();

	public static long getTimestamp() {
		return System.currentTimeMillis();
	}

	public int getWaits(CampingUser user) {
		LinkedList<Long> times = getTimes(delayTimestamps, user);
		long now = getTimestamp();

		expireTimes(times, now);

		int wait = times.size();

		times.add(now);

		return wait;
	}

	protected void expireTimes(LinkedList<Long> times) {
		expireTimes(times, getTimestamp());
	}

	protected void expireTimes(LinkedList<Long> times, long now) {
		while (!times.isEmpty()) {
			Long t = times.getFirst();
			if (now - t >= EXPIRY_DURATION) {
				times.removeFirst();
			} else {
				break;
			}
		}
	}

	public ComboType getComboResult(CampingUser caster, CampingUser victim) {
		long now = getTimestamp();
		if (deadTimestamps.containsKey(caster)) {
			Long t = deadTimestamps.get(caster);
			if (now - t >= EXPIRY_DURATION) {
				deadTimestamps.remove(caster);
			} else {
				return ComboType.Dead;
			}
		}

		String offenseKey = getInvocationKey(caster, victim);
		String defenseKey = getInvocationKey(victim, caster);

		LinkedList<Long> offenseTimes = getTimes(comboTimestamps, offenseKey);
		expireTimes(offenseTimes);
		offenseTimes.add(now);

		if (comboTimestamps.containsKey(defenseKey)) {
			LinkedList<Long> defenseTimes = getTimes(comboTimestamps, defenseKey);
			expireTimes(defenseTimes);
			if (defenseTimes.size() > 0) {
				// TODO victim cannot cast for a while
				deadTimestamps.put(victim, now + EXPIRY_DURATION);
				return ComboType.Breaker;
			}
		}

		if (offenseTimes.size() >= 3) {
			offenseTimes.removeFirst();
			offenseTimes.removeFirst();
			offenseTimes.removeFirst();
			deadTimestamps.put(victim, now + EXPIRY_DURATION);
			return ComboType.KO;
		}
		return ComboType.Normal;
	}

	private static <T> LinkedList<Long> getTimes(Map<T, LinkedList<Long>> timestamps, T value) {
		LinkedList<Long> l = timestamps.get(value);
		if (l == null) {
			l = new LinkedList<>();
			timestamps.put(value, l);
		}
		return l;
	}

	private static String getInvocationKey(CampingUser caster, CampingUser victim) {
		return caster.getCampingId() + DELIMITER + victim.getCampingId();
	}

}
