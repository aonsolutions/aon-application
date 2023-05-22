package net.aonsolutions.occam.test.accounting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.accounting.Account;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)	
class AccountTest extends AbstractOccamTest {

	@Test()
	void dirtyIdTest() {
		Account d = new Account();
		d.setId(1);
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyDomainTest() {
		Account d = new Account();
		d.setDomain(1);
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyCodeTest() {
		Account d = new Account();
		d.setCode(AonRandom.string(9));
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyDescriptionTest() {
		Account d = new Account();
		d.setDescription(AonRandom.string(10));
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyAliasTest() {
		Account d = new Account();
		d.setAlias(AonRandom.string(10));
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyActiveTest() {
		Account d = new Account();
		d.setActive( true );
		assertTrue(d.isDirty());
	}


	@Test()
	void dirtyMarkTrueTest() {
		Account d = new Account();
		d.setDescription( "dddddd" );
		d.setId( null );
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyMarkFalseTest() {
		Account d = new Account();
		d.setId( null );
		d.setDescription( null );
		assertFalse(d.isDirty());
	}
	
	@Test()
	void selectedMarkTest() {
		Account d = new Account();
		d.setSelected( true );
		assertTrue(d.isSelected());
	}
	
	@Test()
	void equalsTest() {
		Account d1 = new Account();
		Account d2 = null;
		assertNotEquals(d1,d2);
		assertEquals(d1,d1);
		d2 = new Account();
		assertEquals(d1,d2);
		d1.setId(1);
		assertNotEquals(d1,d2);
		d2.setId(1);
		assertEquals(d1,d2);
	}

}