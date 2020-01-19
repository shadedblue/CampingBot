package ca.hapke.calendaring.timing.tests;

import static org.junit.Assert.assertEquals;

import java.time.ZonedDateTime;
import java.util.GregorianCalendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.hapke.calendaring.timing.ByTimeOfYear;

/**
 * @author Nathan Hapke
 */
public class ByTimeOfYearTest {
//	private TimeOfYear<Boolean> canadaDayParade = new TimeOfYear<Boolean>(7, 1, 10, 30, true);

	private GregorianCalendar canadaDayParade = new GregorianCalendar(2020, 6, 1, 10, 30);
	private ByTimeOfYear<Boolean> stPatrick5Pm = new ByTimeOfYear<Boolean>(3, 17, 17, 0, true);
	private ByTimeOfYear<Boolean> christmasLunch = new ByTimeOfYear<Boolean>(12, 25, 12, 30, false);

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGenerateATargetTimeAfterMain() {
//		stPatrick5Pm.generateATargetTime(canadaDayParade.toZonedDateTime());
		stPatrick5Pm.generateNearestEvents(canadaDayParade.toZonedDateTime());

		ZonedDateTime pastYear = stPatrick5Pm.getPast();
		assertEquals(2020, pastYear.getYear());

		ZonedDateTime futureYear = stPatrick5Pm.getFuture();
		assertEquals(2021, futureYear.getYear());
	}

	@Test
	public void testGenerateATargetTimeBeforeMain() {
		christmasLunch.generateNearestEvents(canadaDayParade.toZonedDateTime());

		ZonedDateTime pastYear = christmasLunch.getPast();
		assertEquals(2019, pastYear.getYear());

		ZonedDateTime futureYear = christmasLunch.getFuture();
		assertEquals(2020, futureYear.getYear());
	}

}
