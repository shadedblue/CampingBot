package ca.hapke.campingbot.tests.processors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ca.hapke.campingbot.processors.EnhanceBrooklyn99Processor;
import junit.framework.TestCase;

/**
 * @author Nathan Hapke
 */
class Brooklyn99ProcessorTests extends TestCase {

	private static EnhanceBrooklyn99Processor processor;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		processor = new EnhanceBrooklyn99Processor();
	}

	@Test
	void testCoolRepeater() {
		String result = processor.processString("Cool!", false);
		assert (result.length() >= 21);
		assertTrue(result.endsWith("!"));

		result = processor.processString("beforeCOOLafter", false);
		assert (result.length() >= 21);
		assertTrue(result.endsWith("after"));
		assertTrue(result.startsWith("before"));
	}

}
