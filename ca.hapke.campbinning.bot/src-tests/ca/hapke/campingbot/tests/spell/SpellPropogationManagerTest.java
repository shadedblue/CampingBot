package ca.hapke.campingbot.tests.spell;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.hapke.campingbot.commands.spell.ComboType;
import ca.hapke.campingbot.commands.spell.PendingCast;
import ca.hapke.campingbot.commands.spell.SpellPropogationManager;
import ca.hapke.campingbot.commands.spell.SpellResult;
import ca.hapke.campingbot.response.fragments.ResultFragment;
import ca.hapke.campingbot.tests.UsersForTesting;
import ca.hapke.campingbot.users.CampingUser;

/**
 * @author Nathan Hapke
 */
public class SpellPropogationManagerTest {

	private static final int SHORT_DELAY = 50;
	private SpellPropogationManager propMgr;
	private Map<CampingUser, LinkedList<PendingCast>> pendingCasts;
	private UsersForTesting users = new UsersForTesting();

	@BeforeEach
	void setUp() throws Exception {
		pendingCasts = new HashMap<>();
		propMgr = new SpellPropogationManager(pendingCasts);
		propMgr.setMe(users.bot);
	}

	@AfterEach
	void tearDown() throws Exception {
		pendingCasts = null;
		propMgr = null;
	}

	@Test
	void testWaits() throws InterruptedException {
		assertEquals(0, propMgr.getWaits(users.rtv));
		assertEquals(0, propMgr.getWaits(users.nh));

		ComboType first = propMgr.getComboResult(users.rtv, users.nh);
		Thread.sleep(SHORT_DELAY);

		assertEquals(ComboType.Normal, first);
		assertEquals(1, propMgr.getWaits(users.rtv));
		assertEquals(0, propMgr.getWaits(users.nh));

		ComboType second = propMgr.getComboResult(users.rtv, users.nh);
		Thread.sleep(SHORT_DELAY);

		assertEquals(ComboType.Normal, second);
		assertEquals(2, propMgr.getWaits(users.rtv));
		assertEquals(0, propMgr.getWaits(users.nh));
		assertEquals(0, propMgr.getWaits(users.jm));

		ComboType jm = propMgr.getComboResult(users.jm, users.rtv);
		Thread.sleep(SHORT_DELAY);

		assertEquals(ComboType.Normal, jm);
		assertEquals(2, propMgr.getWaits(users.rtv));
		assertEquals(0, propMgr.getWaits(users.nh));
		assertEquals(1, propMgr.getWaits(users.jm));

		ComboType third = propMgr.getComboResult(users.rtv, users.nh);
		Thread.sleep(SHORT_DELAY);

		assertEquals(ComboType.KO, third);
		assertEquals(0, propMgr.getWaits(users.rtv));
		assertEquals(0, propMgr.getWaits(users.nh));
		assertEquals(1, propMgr.getWaits(users.jm));
	}

	@Test
	void test3CastsKo() {
		ComboType first = propMgr.getComboResult(users.rtv, users.nh);
		assertEquals(ComboType.Normal, first);

		ComboType second = propMgr.getComboResult(users.rtv, users.nh);
		assertEquals(ComboType.Normal, second);

		ComboType third = propMgr.getComboResult(users.rtv, users.nh);
		assertEquals(ComboType.KO, third);
	}

	@Test
	void test2CastedBreaker() {
		ComboType first = propMgr.getComboResult(users.rtv, users.nh);
		assertEquals(ComboType.Normal, first);

		ComboType second = propMgr.getComboResult(users.rtv, users.nh);
		assertEquals(ComboType.Normal, second);

		ComboType third = propMgr.getComboResult(users.nh, users.rtv);
		assertEquals(ComboType.Breaker, third);
	}

	@Test
	void test1Casted1PendingBreaker() {
		ComboType first = propMgr.getComboResult(users.rtv, users.nh);
		assertEquals(ComboType.Normal, first);

		LinkedList<PendingCast> ll = new LinkedList<>();
		List<ResultFragment> frags = null;
		ll.add(new PendingCast(users.nh, new SpellResult(users.rtv, users.nh, frags), null, 1));
		pendingCasts.put(users.rtv, ll);

		ComboType third = propMgr.getComboResult(users.nh, users.rtv);
		assertEquals(ComboType.Breaker, third);
	}

	@Test
	void test0Casted2PendingBreaker() {
		LinkedList<PendingCast> ll = new LinkedList<>();
		List<ResultFragment> frags = null;
		ll.add(new PendingCast(users.nh, new SpellResult(users.rtv, users.nh, frags), null, 1));
		ll.add(new PendingCast(users.nh, new SpellResult(users.rtv, users.nh, frags), null, 2));
		pendingCasts.put(users.rtv, ll);

		ComboType third = propMgr.getComboResult(users.nh, users.rtv);
		assertEquals(ComboType.Breaker, third);
	}

	@Test
	void test3CastedGangBang() {
		ComboType first = propMgr.getComboResult(users.rtv, users.aa);
		assertEquals(ComboType.Normal, first);

		ComboType second = propMgr.getComboResult(users.jm, users.aa);
		assertEquals(ComboType.Normal, second);

		ComboType third = propMgr.getComboResult(users.nh, users.aa);
		assertEquals(ComboType.GangBang, third);
	}

	@Test
	void test3CastedBotRevenge() {
		ComboType first = propMgr.getComboResult(users.rtv, users.bot);
		assertEquals(ComboType.Normal, first);

		ComboType second = propMgr.getComboResult(users.rtv, users.bot);
		assertEquals(ComboType.Normal, second);

		ComboType third = propMgr.getComboResult(users.rtv, users.bot);
		assertEquals(ComboType.Revenge, third);
	}
}
