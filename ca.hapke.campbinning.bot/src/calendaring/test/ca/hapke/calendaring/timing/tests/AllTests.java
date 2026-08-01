package ca.hapke.calendaring.timing.tests;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * @author Nathan Hapke
 */
@Suite
@SelectClasses({ ByTimeOfWeekTest.class, ByTimeOfYearTest.class })
public class AllTests {

}
