package net.aonsolutions.occam.test.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.config.Booking;
import net.aonsolutions.occam.api.constants.AonStatus;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)	
class BookingTest extends AbstractOccamTest {

	@Test()
	void dirtyOwnerTest() {
		Booking d = new Booking();
		d.setOwner(AonRandom.string(10));
		assertTrue(d.isDirty());
	}
	@Test()
	void dirtyexpirationDateTest() {
		Booking d = new Booking();
		d.setExpirationDate(AonRandom.getFutureDate());
		assertTrue(d.isDirty());
	}
	@Test()
	void dirtyDomainManagementTest() {
		Booking d = new Booking().setDomainManagement(true);
		assertTrue(d.isDirty());
	}
	@Test()
	void dirtyDisableDomainManagementTest() {
		Booking d = new Booking().setDisableDomainManagement(true);
		assertTrue(d.isDirty());
	}
	@Test()
	void dirtyMaxDefinedUsersTest() {
		Booking d = new Booking();
		d.setMaxDefinedUsers(Integer.MAX_VALUE );
		assertTrue(d.isDirty());
	}
	@Test()
	void dirtyAonCustomerTest() {
		Booking d = new Booking();
		d.setAonCustomer( Integer.MAX_VALUE );
		assertTrue(d.isDirty());
	}
	@Test()
	void dirtyAonStatusTest() {
		Booking d = new Booking();
		d.setAonStatus( AonStatus.BILLABLE );
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyMarkTrueTest() {
		Booking d = new Booking();
		d.setAonStatus( AonStatus.BILLABLE );
		d.setAonCustomer( null );
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyMarkFalseTest() {
		Booking d = new Booking();
		d.setDomainManagement(false);
		d.setDisableDomainManagement(false);
		d.setMaxDefinedUsers(null);
		d.setAonCustomer( null );
		d.setAonStatus( null );
		assertFalse(d.isDirty());
	}
	
}
