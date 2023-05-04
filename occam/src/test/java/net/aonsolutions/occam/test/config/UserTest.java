package net.aonsolutions.occam.test.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.api.config.User;
import net.aonsolutions.occam.api.constants.AonStatus;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)	
class UserTest extends AbstractOccamTest {

	@Test()
	void dirtyIdTest() {
		User u = new User();
		u.setId(1);
		assertTrue(u.isDirty());
	}
	
	@Test()
	void dirtyDomainTest() {
		User u = new User();
		u.setDomain(1);
		assertTrue(u.isDirty());
	}

	@Test()
	void dirtyNameTest() {
		User u = new User();
		u.setName(AonRandom.string(10));
		assertTrue(u.isDirty());
	}
	@Test()
	void dirtyLoginTest() {
		User u = new User();
		u.setLogin(AonRandom.string(10));
		assertTrue(u.isDirty());
	}
	@Test()
	void dirtyActiveTest() {
		User u = new User();
		u.setActive( !u.isActive());
		assertTrue(u.isDirty());
	}
	
	@Test()
	void dirtyMarkTrueTest() {
		Domain d = new Domain();
		d.setAonStatus( AonStatus.BILLABLE );
		d.setId( null );
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyMarkFalseTest() {
		Domain d = new Domain();
		d.setId( null );
		d.setName( null );
		assertFalse(d.isDirty());
	}
	
}
