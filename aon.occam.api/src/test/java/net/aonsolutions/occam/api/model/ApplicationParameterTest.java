package net.aonsolutions.occam.api.model;

import org.junit.jupiter.api.Test;

class ApplicationParameterTest {
	
	@Test
	void testApplicationParameter() {
		ApplicationParameter expected = AonMocker.mock(ApplicationParameter.class);
		ApplicationParameter actual = new ApplicationParameter()
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setParam(expected.getParam())
			.setValue(expected.getValue())
		;
		AonAsserts.assertClassEquals(expected, actual);
	}
}
