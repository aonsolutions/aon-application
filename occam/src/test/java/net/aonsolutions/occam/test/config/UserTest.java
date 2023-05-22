package net.aonsolutions.occam.test.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.config.User;
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
		User d = new User();
		d.setLogin( "dddd" );
		d.setId( null );
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyMarkFalseTest() {
		User d = new User();
		d.setId( null );
		d.setName( null );
		assertFalse(d.isDirty());
	}
	
	@Test()
	void selectedMarkTest() {
		User d = new User();
		d.setSelected( true );
		assertTrue(d.isSelected());
	}
	
	@Test()
	void equalsTest() {
		User d1 = new User();
		User d2 = null;
		assertNotEquals(d1,d2);
		assertEquals(d1,d1);
		d2 = new User();
		assertEquals(d1,d2);
		d1.setId(1);
		assertNotEquals(d1,d2);
		d2.setId(1);
		assertEquals(d1,d2);
	}
	
}
