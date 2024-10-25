package net.aonsolutions.occam.api.model;

import org.junit.jupiter.api.Test;

class UserUserScopeTest {
	
	@Test
	void testUserScope() {
		UserScope expected = AonMocker.mock(UserScope.class);
		UserScope actual = new UserScope()
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setUserId(expected.getUserId())
			.setScope(expected.getScope())
		;
		AonAsserts.assertClassEquals(expected, actual);
	}
}
