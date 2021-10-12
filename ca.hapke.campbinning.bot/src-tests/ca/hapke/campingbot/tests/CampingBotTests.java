package ca.hapke.campingbot.tests;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.runner.RunWith;

/**
 * @author Nathan Hapke
 */
@RunWith(JUnitPlatform.class)
@SelectPackages({ "ca.hapke.campingbot.tests.spell", "ca.hapke.campingbot.tests.processors" })
public class CampingBotTests {

}
