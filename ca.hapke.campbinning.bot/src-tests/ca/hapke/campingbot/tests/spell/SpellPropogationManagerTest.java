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
import ca.hapke.campingbot.users.CampingUser;
import ca.hapke.campingbot.users.CampingUserMonitor;

/**
 * @author Nathan Hapke
 */
public class SpellPropogationManagerTest {

	private SpellPropogationManager propMgr;
	private Map<CampingUser, LinkedList<PendingCast>> pendingCasts;
	private CampingUserMonitor um = CampingUserMonitor.getInstance();

	private CampingUser rtv;
	private CampingUser nh;
	private CampingUser jm;
	private CampingUser aa;
	private CampingUser bot;

	public SpellPropogationManagerTest() {
		rtv = um.monitor(1l, "robtheviking", "R", "S", true);
		nh = um.monitor(2l, "shadedblue", "N", "H", true);
		jm = um.monitor(3l, "jakeford", "J", "M", true);
		aa = um.monitor(4l, "aandy", "A", "A", true);
		bot = um.monitor(5l, "devbot", "D", "B", true);
	}

	@BeforeEach
	void setUp() throws Exception {
		pendingCasts = new HashMap<>();
		propMgr = new SpellPropogationManager(pendingCasts);
		propMgr.setMe(bot);
	}

	@AfterEach
	void tearDown() throws Exception {
		pendingCasts = null;
		propMgr = null;
	}

	@Test
	void test3CastsKo() {
		ComboType first = propMgr.getComboResult(rtv, nh);
		assertEquals(ComboType.Normal, first);

		ComboType second = propMgr.getComboResult(rtv, nh);
		assertEquals(ComboType.Normal, second);

		ComboType third = propMgr.getComboResult(rtv, nh);
		assertEquals(ComboType.KO, third);
	}

	@Test
	void test2CastedBreaker() {
		ComboType first = propMgr.getComboResult(rtv, nh);
		assertEquals(ComboType.Normal, first);

		ComboType second = propMgr.getComboResult(rtv, nh);
		assertEquals(ComboType.Normal, second);

		ComboType third = propMgr.getComboResult(nh, rtv);
		assertEquals(ComboType.Breaker, third);
	}

	@Test
	void test1Casted1PendingBreaker() {
		ComboType first = propMgr.getComboResult(rtv, nh);
		assertEquals(ComboType.Normal, first);

		LinkedList<PendingCast> ll = new LinkedList<>();
		List<ResultFragment> frags = null;
		ll.add(new PendingCast(nh, new SpellResult(rtv, nh, frags), null, 1));
		pendingCasts.put(rtv, ll);

		ComboType third = propMgr.getComboResult(nh, rtv);
		assertEquals(ComboType.Breaker, third);
	}

	@Test
	void test0Casted2PendingBreaker() {
		LinkedList<PendingCast> ll = new LinkedList<>();
		List<ResultFragment> frags = null;
		ll.add(new PendingCast(nh, new SpellResult(rtv, nh, frags), null, 1));
		ll.add(new PendingCast(nh, new SpellResult(rtv, nh, frags), null, 2));
		pendingCasts.put(rtv, ll);

		ComboType third = propMgr.getComboResult(nh, rtv);
		assertEquals(ComboType.Breaker, third);
	}

	@Test
	void test3CastedGangBang() {
		ComboType first = propMgr.getComboResult(rtv, aa);
		assertEquals(ComboType.Normal, first);

		ComboType second = propMgr.getComboResult(jm, aa);
		assertEquals(ComboType.Normal, second);

		ComboType third = propMgr.getComboResult(nh, aa);
		assertEquals(ComboType.GangBang, third);
	}

	@Test
	void test3CastedBotRevenge() {
		ComboType first = propMgr.getComboResult(rtv, bot);
		assertEquals(ComboType.Normal, first);

		ComboType second = propMgr.getComboResult(rtv, bot);
		assertEquals(ComboType.Normal, second);

		ComboType third = propMgr.getComboResult(rtv, bot);
		assertEquals(ComboType.Revenge, third);
	}
}
