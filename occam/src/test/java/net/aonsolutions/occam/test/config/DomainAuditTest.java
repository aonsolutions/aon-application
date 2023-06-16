package net.aonsolutions.occam.test.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.config.DomainAudit;
import net.aonsolutions.occam.api.metadata.DomainAuditMetadata;
import net.aonsolutions.occam.api.metadata.DomainAuditMetadata.DomainAuditMetadataVisitor;
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
		d.setLastAccessDate(AonRandom.futureDate());
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyMarkTrueTest() {
		DomainAudit d = new DomainAudit();
		d.setLastAccessDate(AonRandom.futureDate());
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
	
	@Test()
	void equalsTest() {
		DomainAudit d1 = new DomainAudit();
		DomainAudit d2 = null;
		assertNotEquals(d1,d2);
		assertEquals(d1,d1);
		d2 = new DomainAudit();
		assertEquals(d1,d2);
		d1.setId(1);
		assertNotEquals(d1,d2);
		d2.setId(1);
		assertEquals(d1,d2);
	}
	
	@Test()
	void metadataVisitorTest() {
		DomainAuditMetadataVisitor<DomainAuditMetadata> visitor = new DomainAuditMetadataVisitor<>() {
			@Override public DomainAuditMetadata visitId() { return DomainAuditMetadata.ID; }
			@Override public DomainAuditMetadata visitLastAccessUser() { return DomainAuditMetadata.LAST_ACCESS_USER; }
			@Override public DomainAuditMetadata visitLastAccessDate() { return DomainAuditMetadata.LAST_ACCESS_DATE; }
			@Override public DomainAuditMetadata visitCreationUser() { return DomainAuditMetadata.CREATION_USER; }
			@Override public DomainAuditMetadata visitCreationDate() { return DomainAuditMetadata.CREATION_DATE; }
			@Override public DomainAuditMetadata visitModificationUser() { return DomainAuditMetadata.MODIFICATION_USER; }
			@Override public DomainAuditMetadata visitModificationDate() { return DomainAuditMetadata.MODIFICATION_DATE; }
		};

		Arrays.stream(DomainAuditMetadata.values()).forEach( dm -> {
			assertEquals( dm, dm.visit(visitor));
		});		
	}	
}
