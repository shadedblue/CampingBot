package ca.hapke.campbinning.bot.tests.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.hapke.campbinning.bot.users.CampingUser;
import ca.hapke.campbinning.bot.users.CampingUserMonitor;
import ca.odell.glazedlists.EventList;

/**
 * @author Nathan Hapke
 */
public class CampingUserMonitorTest {

	private CampingUserMonitor monitor = CampingUserMonitor.getInstance();

	@BeforeEach
	public void setUp() throws Exception {
	}

	@AfterEach
	public void tearDown() throws Exception {
	}

	@Test
	public void testMerge() {
		EventList<CampingUser> users = monitor.getUsers();
		assertEquals(0, users.size());

		CampingUser usernameUser = monitor.monitor(-1, "@user", null, null, false);
		assertEquals(1, users.size());
		assertNull(usernameUser.getFirstname(), "has Firstname");
		assertFalse(usernameUser.isSeenInteraction());

		CampingUser firstLastUser = monitor.monitor(123, null, "first", "last", true);
		assertEquals(2, users.size());
		assertNull(firstLastUser.getUsername(), "has @username");
		assertTrue(firstLastUser.isSeenInteraction());

		assertTrue(usernameUser != firstLastUser, "usernameUser == firstLastUser");

		CampingUser usernameWithMatchIdUser = monitor.monitor(123, "@user", null, null, false);
		assertTrue(usernameWithMatchIdUser.isSeenInteraction());
		assertEquals(1, users.size());
	}

}
