package net.aonsolutions.occam.test.accounting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.accounting.AccountingPeriod;
import net.aonsolutions.occam.api.constants.AccountingPeriodStatus;
import net.aonsolutions.occam.api.metadata.AccountingPeriodMetadata;
import net.aonsolutions.occam.api.metadata.AccountingPeriodMetadata.AccountingPeriodMetadataVisitor;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)	
class AccountingPeriodTest extends AbstractOccamTest {

	@Test()
	void dirtyIdTest() {
		AccountingPeriod d = new AccountingPeriod();
		d.setId(1);
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyDomainTest() {
		AccountingPeriod d = new AccountingPeriod();
		d.setDomain(1);
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyNameTest() {
		AccountingPeriod d = new AccountingPeriod();
		d.setName(AonRandom.string(9));
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyStartDateTest() {
		AccountingPeriod d = new AccountingPeriod();
		d.setStartDate(AonRandom.today());
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyEndDateTest() {
		AccountingPeriod d = new AccountingPeriod();
		d.setEndDate( AonRandom.today());
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyStatusTest() {
		AccountingPeriod d = new AccountingPeriod();
		d.setStatus( AccountingPeriodStatus.OPENING );
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyMarkTrueTest() {
		AccountingPeriod d = new AccountingPeriod();
		d.setName( "dddddd" );
		d.setId( null );
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyMarkFalseTest() {
		AccountingPeriod d = new AccountingPeriod();
		d.setId( null );
		d.setName( null );
		assertFalse(d.isDirty());
	}
	
	@Test()
	void selectedMarkTest() {
		AccountingPeriod d = new AccountingPeriod();
		d.setSelected( true );
		assertTrue(d.isSelected());
	}
	
	@Test()
	void equalsTest() {
		AccountingPeriod d1 = new AccountingPeriod();
		AccountingPeriod d2 = null;
		assertNotEquals(d1,d2);
		assertEquals(d1,d1);
		d2 = new AccountingPeriod();
		assertEquals(d1,d2);
		d1.setId(1);
		assertNotEquals(d1,d2);
		d2.setId(1);
		assertEquals(d1,d2);
	}

	@Test()
	void metadataVisitorTest() {
		AccountingPeriodMetadataVisitor<AccountingPeriodMetadata> visitor = new AccountingPeriodMetadataVisitor<>() {
			@Override public AccountingPeriodMetadata visitId() { return AccountingPeriodMetadata.ID; }
			@Override public AccountingPeriodMetadata visitDomain() { return  AccountingPeriodMetadata.DOMAIN; }
			@Override public AccountingPeriodMetadata visitName() {return AccountingPeriodMetadata.NAME; }
			@Override public AccountingPeriodMetadata visitStartDate() { return AccountingPeriodMetadata.START_DATE; }
			@Override public AccountingPeriodMetadata visitEndDate() { return AccountingPeriodMetadata.END_DATE; }
			@Override public AccountingPeriodMetadata visitStatus() { return AccountingPeriodMetadata.STATUS; }
			@Override public AccountingPeriodMetadata visitDefaultPeriod() { return AccountingPeriodMetadata.DEFAULT_PERIOD; }
			@Override public AccountingPeriodMetadata visitAudit() { return AccountingPeriodMetadata.AUDIT; }
		};
		
		Arrays.stream(AccountingPeriodMetadata.values()).forEach( dm -> {
			assertEquals( dm, dm.visit(visitor));
		});		
	}
}