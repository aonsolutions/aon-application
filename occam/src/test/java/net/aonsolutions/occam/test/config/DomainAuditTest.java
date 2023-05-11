package net.aonsolutions.occam.test.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.config.DomainAudit;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)	
class DomainAuditTest extends AbstractOccamTest {

	@Test()
	void dirtyLastAccessUserTest() {
		DomainAudit d = new DomainAudit();
		d.setLastAccessUser(AonRandom.string(10));
		assertTrue(d.isDirty());
	}
	@Test()
	void dirtyLastAccessDateTest() {
		DomainAudit d = new DomainAudit();
		d.setLastAccessDate(AonRandom.getFutureDate());
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyMarkTrueTest() {
		DomainAudit d = new DomainAudit();
		d.setLastAccessDate(AonRandom.getFutureDate());
		d.setLastAccessUser( null );
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyMarkFalseTest() {
		DomainAudit d = new DomainAudit();
		d.setLastAccessDate( null );
		d.setLastAccessUser( null );
		assertFalse(d.isDirty());
	}
}
