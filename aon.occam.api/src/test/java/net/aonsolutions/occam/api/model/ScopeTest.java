package net.aonsolutions.occam.api.model;

import org.junit.jupiter.api.Test;

class ScopeTest {
	
	@Test
	void testScope() {
		Scope expected = AonMocker.mock(Scope.class);
		Scope actual = new Scope()
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setDescription(expected.getDescription())
		;
		AonAsserts.assertClassEquals(expected, actual);
	}
}
