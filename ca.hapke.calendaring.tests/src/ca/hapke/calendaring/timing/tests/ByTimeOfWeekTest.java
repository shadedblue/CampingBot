package ca.hapke.calendaring.timing.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.GregorianCalendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.hapke.calendaring.timing.ByTimeOfWeek;

/**
 * @author Nathan Hapke
 */
public class ByTimeOfWeekTest {

	private static final int FRIDAY_JAN_2020_DOW = 3;
	private static final DayOfWeek FRIDAY = DayOfWeek.FRIDAY;
	private ByTimeOfWeek<Boolean> fridayAm;

	@Before
	public void setUp() throws Exception {
		fridayAm = new ByTimeOfWeek<Boolean>(FRIDAY, 7, 0, true);

	}

	@After
	public void tearDown() throws Exception {
		fridayAm = null;
	}

	@Test
	public void testGenerateTargetTimeJan15() {

		GregorianCalendar mainTime;
		ZonedDateTime when;

		// Wed, Jan 15, 12:35pm
		mainTime = new GregorianCalendar(2020, 0, 15, 12, 35);
		when = mainTime.toZonedDateTime();

		ZonedDateTime target = fridayAm.generateATargetTime(when);
		LocalDateTime targetDT = target.toLocalDateTime();

		int tDOM = targetDT.get(ChronoField.DAY_OF_MONTH);
		int tDOW = targetDT.get(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH);
		boolean ten = tDOM == 10;
		boolean seventeen = tDOM == 17;
		assertTrue(ten || seventeen);
		// Jan 1/2020 is a Wednesday, thus day 3 is Friday.
		assertEquals(FRIDAY_JAN_2020_DOW, tDOW);

		///

		fridayAm.generateNearestEvents(when);

		///

		int nearPastDOM = 10;
		int nearFutureDOM = 17;

		LocalDateTime pastDT = fridayAm.getPast().toLocalDateTime();

		int pDOM = pastDT.get(ChronoField.DAY_OF_MONTH);
		int pDOW = pastDT.get(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH);

		assertEquals(nearPastDOM, pDOM);
		assertEquals(FRIDAY_JAN_2020_DOW, pDOW);

		LocalDateTime futureDT = fridayAm.getFuture().toLocalDateTime();

		int fDOM = futureDT.get(ChronoField.DAY_OF_MONTH);
		int fDOW = futureDT.get(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH);

		assertEquals(nearFutureDOM, fDOM);
		assertEquals(FRIDAY_JAN_2020_DOW, fDOW);
	}

	/**
	 * future goes into next month
	 */
	@Test
	public void testGenerateTargetTimeMar30() {
		GregorianCalendar mainTime = new GregorianCalendar(2020, 2, 30, 12, 35);
		// mar 27, apr 3
		testDaysAndMonths(mainTime, 3, 27, 4, 3);
	}

	/**
	 * goes into prev month (with leap-year)
	 */
	@Test
	public void testGenerateTargetTimeMar3_2020() {
		GregorianCalendar mainTime = new GregorianCalendar(2020, 2, 3, 12, 35);
		// feb 28, mar 6
		testDaysAndMonths(mainTime, 2, 28, 3, 6);
	}

	/**
	 * goes into prev month (no leap-year)
	 */
	@Test
	public void testGenerateTargetTimeMar3_2021() {
		GregorianCalendar mainTime = new GregorianCalendar(2021, 2, 3, 12, 35);
		// feb 28, mar 6
		testDaysAndMonths(mainTime, 2, 26, 3, 5);
	}

	private void testDaysAndMonths(GregorianCalendar mainTime, int nearPastM, int nearPastDOM, int nearFutureM,
			int nearFutureDOM) {
		fridayAm.generateNearestEvents(mainTime.toZonedDateTime());

		LocalDateTime pastDT = fridayAm.getPast().toLocalDateTime();

		int pDOM = pastDT.get(ChronoField.DAY_OF_MONTH);
		int pM = pastDT.get(ChronoField.MONTH_OF_YEAR);

		assertEquals(nearPastDOM, pDOM);
		assertEquals(nearPastM, pM);

		LocalDateTime futureDT = fridayAm.getFuture().toLocalDateTime();

		int fDOM = futureDT.get(ChronoField.DAY_OF_MONTH);
		int fM = futureDT.get(ChronoField.MONTH_OF_YEAR);

		assertEquals(nearFutureDOM, fDOM);
		assertEquals(nearFutureM, fM);
	}

//	@Test
//	void testGenerateNearestEvents() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testTranslateFuturePast() {
//		fail("Not yet implemented");
//	}

}
