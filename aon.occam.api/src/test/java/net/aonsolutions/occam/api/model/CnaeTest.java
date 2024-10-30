package net.aonsolutions.occam.api.model;

import org.junit.jupiter.api.Test;

class CnaeTest {
	
	@Test
	void testCnae() {
		Cnae expected = AonMocker.mock(Cnae.class);
		Cnae actual = new Cnae()
			.setId(expected.getId())
			.setCode(expected.getCode())
			.setTitle(expected.getTitle())
		;
		AonAsserts.assertClassEquals(expected, actual);
	}
	
}
