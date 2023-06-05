package net.aonsolutions.occam.test.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.config.Booking;
import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.api.config.DomainAudit;
import net.aonsolutions.occam.api.config.Registry;
import net.aonsolutions.occam.api.config.User;
import net.aonsolutions.occam.api.constants.DomainType;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonFaker;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)	
class DomainTest extends AbstractOccamTest {

	@Test()
	void dirtyIdTest() {
		Domain d = new Domain();
		d.setId(1);
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyNameTest() {
		Domain d = new Domain();
		d.setName(AonRandom.string(10));
		assertTrue(d.isDirty());
	}
	@Test()
	void dirtyDescriptionTest() {
		Domain d = new Domain();
		d.setDescription(AonRandom.string(10));
		assertTrue(d.isDirty());
	}
	@Test()
	void dirtyParentTest() {
		Domain d = new Domain();
		d.setParent( new Domain().setId(Integer.MAX_VALUE) );
		assertTrue(d.isDirty());
	}
	@Test()
	void dirtyTypeTest() {
		Domain d = new Domain();
		d.setType( DomainType.ACADEMY );
		assertTrue(d.isDirty());
	}
	@Test()
	void dirtyInheritanceTest() {
		Domain d = new Domain().setInheritance(true);
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyActiveTest() {
		Domain d = new Domain().setActive(true);
		assertTrue(d.isDirty());
	}
	@Test()
	void dirtyScopeTest() {
		Domain d = new Domain();
		d.setScope( AonFaker.getScope() );
		assertTrue(d.isDirty());
	}
	@Test()
	void dirtyMarkTrueTest() {
		Domain d = new Domain();
		d.setType( DomainType.ENTERPRISE );
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
	
	@Test()
	void dirtyCompanyTest() {
		Domain d = new Domain();
		Registry b = new Registry(); 
		b.setName( "ddddd" );
		assertTrue(b.isDirty());
		d.setCompany(b);
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyBooking1Test() {
		Domain d = new Domain();
		Booking b = new Booking(); 
		b.setMaxDefinedUsers( Integer.MAX_VALUE );
		assertTrue(b.isDirty());
		d.setBooking(b);
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyBooking2Test() {
		Domain d = new Domain();
		Booking b = new Booking(); 
		b.setMaxDefinedUsers( null );
		assertFalse(b.isDirty());
		d.setBooking(b);
		assertFalse(d.isDirty());
	}
	
	@Test()
	void dirtyBooking3Test() {
		Domain d = new Domain();
		Booking b = new Booking();
		d.setBooking(b);
		assertFalse(b.isDirty());
		assertFalse(d.isDirty());
	}

	@Test()
	void dirtyBooking4Test() {
		Domain d = new Domain();
		Booking b = new Booking();
		b.setMaxDefinedUsers( Integer.MAX_VALUE );
		d.setBooking(b);
		b.markAsClean();
		d.markAsClean();
		d.setBooking(null);
		assertTrue(d.isDirty());
	}
	@Test()
	void dirtyAuditTest() {
		Domain d = new Domain();
		DomainAudit b = new DomainAudit(); 
		b.setLastAccessDate(AonRandom.futureDate());
		assertTrue(b.isDirty());
		d.setAudit(b);
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyAudit2Test() {
		Domain d = new Domain();
		DomainAudit b = new DomainAudit(); 
		b.setLastAccessDate( null );
		assertFalse(b.isDirty());
		d.setAudit(b);
		assertFalse(d.isDirty());
	}
	
	@Test()
	void dirtyAudit3Test() {
		Domain d = new Domain();
		DomainAudit b = new DomainAudit();
		d.setAudit(b);
		assertFalse(b.isDirty());
		assertFalse(d.isDirty());
	}

	@Test()
	void dirtyAudit4Test() {
		Domain d = new Domain();
		DomainAudit b = new DomainAudit();
		b.setLastAccessDate(AonRandom.futureDate());
		d.setAudit(b);
		b.markAsClean();
		d.markAsClean();
		d.setAudit(null);
		assertTrue(d.isDirty());
	}
	
	@Test()
	void selectedMarkTest() {
		Domain d = new Domain();
		d.setSelected( true );
		assertTrue(d.isSelected());
	}
	
	@Test()
	void equalsTest() {
		Domain d1 = new Domain();
		Domain d2 = null;
		assertNotEquals(d1,d2);
		assertEquals(d1,d1);
		d2 = new Domain();
		assertEquals(d1,d2);
		d1.setId(1);
		assertNotEquals(d1,d2);
		d2.setId(1);
		assertEquals(d1,d2);
	}
	
	@Test()
	void equalsUsers() {
		Domain d1 = new Domain();
		List<User> users = null;
		d1.addUsers(users);
		assertFalse( d1.getUsers().isPresent() );
		users = new LinkedList<>();
		users.add(AonFaker.getUser().setId(1));
		d1.addUsers(users);
		assertTrue( d1.getUsers().isPresent() );
		assertEquals(1, d1.getUsers().get().size() );
		
		users.add(AonFaker.getUser().setId(2));
		users.add(AonFaker.getUser().setId(3));
		d1.addUsers(users);
		assertTrue( d1.getUsers().isPresent() );
		assertEquals(3, d1.getUsers().get().size() );
		
	}
}