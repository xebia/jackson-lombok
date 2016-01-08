package com.xebia.jacksonlombok;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Confirm jackson-lombok build on jackson 2.6 IS NOT compatible jackson 2.5.
 */
public class CompatibilityBrokenConfirmationTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private JacksonLombokAnnotationIntrospectorTest testSuccessWithJackson26;

	@Before
	public void setUp() {
		testSuccessWithJackson26 = new JacksonLombokAnnotationIntrospectorTest();
	}

	@Test
	public void ensureJacksonLombokBuildWithJackson26IsNotCompatibleWithJackson25Test()
			throws IOException {
		thrown.expect(JsonMappingException.class);
		// We know the same test case is successes with jackson 2.6.
		// But next line thrown exception because running with jackson 2.5.
		testSuccessWithJackson26.testJacksonUnableToDeserialize();

		fail("Fail if reached."
			+ "We know desilialization is fail because jackson internal modification from 2.5 to 2.6");
	}
}
