package ca.hapke.campingbot.tests;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

/**
 * @author Nathan Hapke
 */
@Suite
@SelectPackages({ "ca.hapke.campingbot.tests.spell", "ca.hapke.campingbot.tests.processors", "ca.hapke.campingbot.tests.user" })
public class CampingBotTests {

}
