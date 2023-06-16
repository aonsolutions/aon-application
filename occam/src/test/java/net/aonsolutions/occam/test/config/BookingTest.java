package net.aonsolutions.occam.test.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.config.Booking;
import net.aonsolutions.occam.api.constants.AonStatus;
import net.aonsolutions.occam.api.metadata.BookingMetadata;
import net.aonsolutions.occam.api.metadata.BookingMetadata.BookingMetadataVisitor;
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
		d.setExpirationDate(AonRandom.futureDate());
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
	
	@Test()
	void equalsTest() {
		Booking d1 = new Booking();
		Booking d2 = null;
		assertNotEquals(d1,d2);
		assertEquals(d1,d1);
		d2 = new Booking();
		assertEquals(d1,d2);
		d1.setId(1);
		assertNotEquals(d1,d2);
		d2.setId(1);
		assertEquals(d1,d2);
	}
	
	@Test()
	void metadataVisitorTest() {
		BookingMetadataVisitor<BookingMetadata> visitor = new BookingMetadataVisitor<>() {
			@Override public BookingMetadata visitId() { return BookingMetadata.ID; }
			@Override public BookingMetadata visitExpirationDate() { return BookingMetadata.EXPIRATION_DATE; }
			@Override public BookingMetadata visitOwner() { return BookingMetadata.OWNER; }
			@Override public BookingMetadata visitDomainManagement() { return BookingMetadata.DOMAIN_MANAGEMENT; }
			@Override public BookingMetadata visitDisableDomainManagement() { return BookingMetadata.DISABLE_DOMAIN_MANAGEMENT; }
			@Override public BookingMetadata visitMaxDefinedUsers() { return BookingMetadata.MAX_DEFINED_USERS; }
			@Override public BookingMetadata visitAonCustomer() { return BookingMetadata.AON_CUSTOMER; }
			@Override public BookingMetadata visitAonStatus() { return BookingMetadata.AON_STATUS; }
		};

		Arrays.stream(BookingMetadata.values()).forEach( dm -> {
			assertEquals( dm, dm.visit(visitor));
		});		
	}
}
