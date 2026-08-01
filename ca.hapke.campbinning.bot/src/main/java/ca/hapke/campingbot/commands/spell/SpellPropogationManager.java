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

	private Map<CampingUser, LinkedList<CastStruct>> outgoingDealt = new HashMap<>();
	private Map<CampingUser, LinkedList<CastStruct>> damageReceived = new HashMap<>();
	private Map<CampingUser, Long> deadTimestamps = new HashMap<>();
	private Map<CampingUser, LinkedList<PendingCast>> pendingCasts;
	private CampingUser me;

	public SpellPropogationManager(Map<CampingUser, LinkedList<PendingCast>> pendingCasts) {
		this.pendingCasts = pendingCasts;
	}

	public static long getTimestamp() {
		return System.currentTimeMillis();
	}

	public int getWaits(CampingUser user) {
		LinkedList<CastStruct> times = getTimes(outgoingDealt, user);

		expireTimes(times);
		int previousOffensives = times.size();

		int pendingQty = 0;
		LinkedList<PendingCast> casts = pendingCasts.get(user);
		if (casts != null) {
			pendingQty = casts.size();
		}
		return previousOffensives + pendingQty;
	}

	protected void expireTimes(LinkedList<CastStruct> times) {
		expireTimes(times, getTimestamp());
	}

	protected void expireTimes(LinkedList<CastStruct> times, long now) {
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
		if (victim == null || caster == null) {
			System.err.println("Fizzle!");
			return ComboType.Fizzle;
		}

		long now = getTimestamp();
		if (deadTimestamps.containsKey(caster)) {
			Long t = deadTimestamps.get(caster);
			if (now - t >= EXPIRY_DURATION) {
				deadTimestamps.remove(caster);
			} else {
				System.err.println("Dead!");
				return ComboType.Dead;
			}
		}

		LinkedList<CastStruct> offenseTimes = getTimes(outgoingDealt, caster);
		expireTimes(offenseTimes);
		offenseTimes.add(new CastStruct(victim, now));

		LinkedList<CastStruct> casterDefenseTimes = getTimes(damageReceived, caster);

		int completedFromVictim = 0;
		for (CastStruct cs : casterDefenseTimes) {
			CampingUser smiter = cs.user;

			if (smiter.equals(victim)) {
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

		System.out.println(
				"Victim has landed " + completedFromVictim + " and " + pendingFromVictimSize + " pending" + "");
		if (completedFromVictim + pendingFromVictimSize >= 2) {
			// Combo Breaker -- victim cannot cast for a while
			deadTimestamps.put(victim, now + EXPIRY_DURATION);
			cancelPending(victim);
			return ComboType.Breaker;
		}

		int damageLandedOnVictim = countDamage(offenseTimes, victim);
		System.out.println("Caster has landed " + damageLandedOnVictim);
		if (damageLandedOnVictim >= KO_QTY) {
			eliminate3(offenseTimes, victim);

			if (victim.equals(me)) {
				cancelPending(caster);
				deadTimestamps.put(caster, now + EXPIRY_DURATION);
				return ComboType.Revenge;
			} else {
				cancelPending(victim);
				deadTimestamps.put(victim, now + EXPIRY_DURATION);
				return ComboType.KO;
			}
		}

		LinkedList<CastStruct> victimHitByTimes = getTimes(damageReceived, victim);
		expireTimes(victimHitByTimes);
		victimHitByTimes.add(new CastStruct(caster, now));
		Set<CampingUser> victimizedBy = new HashSet<>();
		for (CastStruct cs : victimHitByTimes) {
			CampingUser smiter = cs.user;
			victimizedBy.add(smiter);
		}
		if (victimizedBy.size() >= 3) {
			deadTimestamps.put(victim, now + EXPIRY_DURATION);
			return ComboType.GangBang;
		}

		return ComboType.Normal;
	}

	private int countDamage(LinkedList<CastStruct> offenseTimes, CampingUser victim) {
		int n = 0;
		for (CastStruct cast : offenseTimes) {
			if (cast.user.equals(victim)) {
				n++;
			}
		}
		return n;
	}

	private void eliminate3(LinkedList<CastStruct> offenseTimes, CampingUser victim) {
		int n = 3;
		int i = 0;
		while (n > 0 && i < offenseTimes.size()) {
			CastStruct cast = offenseTimes.get(i);
			if (cast.user.equals(victim)) {
				offenseTimes.remove(cast);
				n--;
			} else {
				i++;
			}
		}
	}

	private void cancelPending(CampingUser from) {
		pendingCasts.remove(from);
	}

	private static <K, V> LinkedList<V> getTimes(Map<K, LinkedList<V>> timestamps, K value) {
		LinkedList<V> l = timestamps.get(value);
		if (l == null) {
			l = new LinkedList<>();
			timestamps.put(value, l);
		}
		return l;
	}

	// need setter so this happens after config load
	public void setMe(CampingUser me) {
		if (this.me == null)
			this.me = me;
	}

}
