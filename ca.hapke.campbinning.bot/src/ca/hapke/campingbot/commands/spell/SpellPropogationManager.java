package ca.hapke.campingbot.commands.spell;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import ca.hapke.campingbot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class SpellPropogationManager {

	private static final int KO_QTY = 3;
	private static final long EXPIRY_DURATION = 10 * 60 * 1000l;

	private Map<CampingUser, LinkedList<Long>> offensiveCasts = new HashMap<>();
	private Map<CampingUser, LinkedList<CastStruct>> defensiveTargets = new HashMap<>();
	private Map<CampingUser, Long> deadTimestamps = new HashMap<>();
	private Map<CampingUser, LinkedList<PendingCast>> pendingCasts;

	public SpellPropogationManager(Map<CampingUser, LinkedList<PendingCast>> pendingCasts) {
		this.pendingCasts = pendingCasts;
	}

	public static long getTimestamp() {
		return System.currentTimeMillis();
	}

	public int getWaits(CampingUser user) {
		LinkedList<Long> times = getTimes(offensiveCasts, user);

		expireOffensiveTimes(times);

		return times.size();
	}

	protected void expireOffensiveTimes(LinkedList<Long> times) {
		expireOffensiveTimes(times, getTimestamp());
	}

	protected void expireDefensiveTimes(LinkedList<CastStruct> times) {
		expireDefensiveTimes(times, getTimestamp());
	}

	protected void expireOffensiveTimes(LinkedList<Long> times, long now) {
		while (!times.isEmpty()) {
			Long t = times.getFirst();
			if (now - t >= EXPIRY_DURATION) {
				times.removeFirst();
			} else {
				break;
			}
		}
	}

	protected void expireDefensiveTimes(LinkedList<CastStruct> times, long now) {
		while (!times.isEmpty()) {
			Long t = times.getFirst().time;
			if (now - t >= EXPIRY_DURATION) {
				times.removeFirst();
			} else {
				break;
			}
		}
	}

	public ComboType getComboResult(CampingUser caster, CampingUser victim) {
		if (victim == null || caster == null)
			return ComboType.Fizzle;

		long now = getTimestamp();
		if (deadTimestamps.containsKey(caster)) {
			Long t = deadTimestamps.get(caster);
			if (now - t >= EXPIRY_DURATION) {
				deadTimestamps.remove(caster);
			} else {
				return ComboType.Dead;
			}
		}

		LinkedList<Long> offenseTimes = getTimes(offensiveCasts, caster);
		expireOffensiveTimes(offenseTimes);
		offenseTimes.add(now);

		LinkedList<CastStruct> casterDefenseTimes = getTimes(defensiveTargets, caster);

//			List<CastStruct> completedFromCaster = new ArrayList<>(KO_QTY);
		int completedFromVictim = 0;
		for (CastStruct cs : casterDefenseTimes) {
			CampingUser smiter = cs.caster;

			if (smiter.equals(victim)) {
//					completedFromCaster.add(cs);
				completedFromVictim++;
			}
		}

		Set<PendingCast> pendingFromVictim = new HashSet<>();
		LinkedList<PendingCast> victimsPending = pendingCasts.get(victim);
		if (victimsPending != null) {
			for (PendingCast pendingCast : victimsPending) {
				if (pendingCast.victim.equals(caster)) {
					pendingFromVictim.add(pendingCast);
				}
			}
		}

		int pendingFromVictimSize = pendingFromVictim.size();
		if (completedFromVictim + pendingFromVictimSize >= 2) {
			// victim cannot cast for a while
			deadTimestamps.put(victim, now + EXPIRY_DURATION);

			// TODO cancel the castersPending's?

			return ComboType.Breaker;
		}

		if (offenseTimes.size() >= KO_QTY) {
			offenseTimes.removeFirst();
			offenseTimes.removeFirst();
			offenseTimes.removeFirst();
			deadTimestamps.put(victim, now + EXPIRY_DURATION);
			return ComboType.KO;
		}

		LinkedList<CastStruct> victimDefenseTimes = getTimes(defensiveTargets, victim);
		expireDefensiveTimes(victimDefenseTimes);
		victimDefenseTimes.add(new CastStruct(caster, now));
		Set<CampingUser> victimRecentlyHitBy = new HashSet<>();
		for (CastStruct cs : victimDefenseTimes) {
			CampingUser smiter = cs.caster;
			victimRecentlyHitBy.add(smiter);
		}
		if (victimRecentlyHitBy.size() >= 3) {
			deadTimestamps.put(victim, now + EXPIRY_DURATION);
			return ComboType.GangBang;
		}

		return ComboType.Normal;
	}

	private static <K, V> LinkedList<V> getTimes(Map<K, LinkedList<V>> timestamps, K value) {
		LinkedList<V> l = timestamps.get(value);
		if (l == null) {
			l = new LinkedList<>();
			timestamps.put(value, l);
		}
		return l;
	}

//	private static String getInvocationKey(CampingUser caster, CampingUser victim) {
//		return caster.getCampingId() + DELIMITER + victim.getCampingId();
//	}

}
