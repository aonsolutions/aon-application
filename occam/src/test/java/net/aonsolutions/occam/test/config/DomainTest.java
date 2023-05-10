package net.aonsolutions.occam.test.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.api.constants.AonStatus;
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
	void dirtyOwnerTest() {
		Domain d = new Domain();
		d.setOwner(AonRandom.string(10));
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
	void dirtySubDomainSuffixTest() {
		Domain d = new Domain();
		d.setSubDomainSuffix(AonRandom.string(10));
		assertTrue(d.isDirty());
	}
	@Test()
	void dirtyEnableHeredityTest() {
		Domain d = new Domain().setEnableHeredity(true);
		assertTrue(d.isDirty());
	}
	@Test()
	void dirtyDomainManagementTest() {
		Domain d = new Domain().setDomainManagement(true);
		assertTrue(d.isDirty());
	}
	@Test()
	void dirtyDisableDomainManagementTest() {
		Domain d = new Domain().setDisableDomainManagement(true);
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
	void dirtyMaxDefinedUsersTest() {
		Domain d = new Domain();
		d.setMaxDefinedUsers(Integer.MAX_VALUE );
		assertTrue(d.isDirty());
	}
	@Test()
	void dirtyMaxDocumentSizeTest() {
		Domain d = new Domain();
		d.setMaxDocumentSize(Integer.MAX_VALUE );
		assertTrue(d.isDirty());
	}
	@Test()
	void dirtyMaxTotalDocumentSizeTest() {
		Domain d = new Domain();
		d.setMaxTotalDocumentSize(Integer.MAX_VALUE );
		assertTrue(d.isDirty());
	}
	@Test()
	void dirtyLastAccessUserTest() {
		Domain d = new Domain();
		d.setLastAccessUser(AonRandom.string(10));
		assertTrue(d.isDirty());
	}
	@Test()
	void dirtyLastAccessDateTest() {
		Domain d = new Domain();
		d.setLastAccessDate(AonRandom.getFutureDate());
		assertTrue(d.isDirty());
	}
	@Test()
	void dirtyexpirationDateTest() {
		Domain d = new Domain();
		d.setExpirationDate(AonRandom.getFutureDate());
		assertTrue(d.isDirty());
	}
	@Test()
	void dirtyCreationUserTest() {
		Domain d = new Domain();
		d.setCreationUser(AonRandom.string(10));
		assertTrue(d.isDirty());
	}
	@Test()
	void dirtyCreationDateTest() {
		Domain d = new Domain();
		d.setCreationDate( AonRandom.getFutureDate());
		assertTrue(d.isDirty());
	}
	@Test()
	void dirtyModificationUserTest() {
		Domain d = new Domain();
		d.setModificationUser(AonRandom.string(10));
		assertTrue(d.isDirty());
	}
	@Test()
	void dirtyModificationDateTest() {
		Domain d = new Domain();
		d.setModificationDate( AonRandom.getFutureDate());
		assertTrue(d.isDirty());
	}
	@Test()
	void dirtyAonCustomerTest() {
		Domain d = new Domain();
		d.setAonCustomer( Integer.MAX_VALUE );
		assertTrue(d.isDirty());
	}
	@Test()
	void dirtyAonStatusTest() {
		Domain d = new Domain();
		d.setAonStatus( AonStatus.BILLABLE );
		assertTrue(d.isDirty());
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
