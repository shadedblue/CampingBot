package ca.hapke.campingbot.tests.processors;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ca.hapke.campingbot.processors.EnhanceNumberProcessor;

/**
 * @author Nathan Hapke
 */
class EnhanceNumberProcessorTests {

	private static EnhanceNumberProcessor processor;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		processor = new EnhanceNumberProcessor();
	}

	@Test
	void testCoupleNumbers() {
		String result = processor.processString("text 123 words 456 etc.", false);
		System.out.println(result);
	}

	@Test
	void testNoNumbers() {
		String original = "text words etc.";
		String result = processor.processString(original, false);
		assertEquals(original, result);
	}
}
