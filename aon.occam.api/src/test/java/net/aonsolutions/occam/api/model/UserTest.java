package net.aonsolutions.occam.api.model;

import org.junit.jupiter.api.Test;

class UserTest {
	
	@Test
	void testUser() {
		User expected = AonMocker.mock(User.class);
		User actual = new User()
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setName(expected.getName())
			.setLogin(expected.getLogin())
			.setActive(expected.isActive())
		;
		AonAsserts.assertClassEquals(expected, actual);
	}
}
