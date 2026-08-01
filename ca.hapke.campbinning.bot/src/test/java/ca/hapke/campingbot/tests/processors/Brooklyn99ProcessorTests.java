package ca.hapke.campingbot.tests.processors;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.hapke.campingbot.processors.EnhanceBrooklyn99Processor;

/**
 * @author Nathan Hapke
 */
class Brooklyn99ProcessorTests {

	private EnhanceBrooklyn99Processor processor;

	@BeforeEach
	void setUp() throws Exception {
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
