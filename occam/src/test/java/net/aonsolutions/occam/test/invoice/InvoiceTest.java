package net.aonsolutions.occam.test.invoice;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.config.Activity;
import net.aonsolutions.occam.api.constants.Country;
import net.aonsolutions.occam.api.constants.DocumentType;
import net.aonsolutions.occam.api.constants.InvoiceType;
import net.aonsolutions.occam.api.invoice.Invoice;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)	
class InvoiceTest extends AbstractOccamTest {

	@Test()
	void dirtyIdTest() {
		Invoice i = new Invoice();
		i.setId(1);
		assertTrue(i.isDirty());
	}
	
	@Test()
	void dirtyDomainTest() {
		Invoice i = new Invoice();
		i.setDomain( Integer.MAX_VALUE);
		assertTrue(i.isDirty());
	}

	@Test()
	void dirtyTypeTest() {
		Invoice i = new Invoice();
		i.setType( InvoiceType.PURCHASE );
		assertTrue(i.isDirty());
	}
	
	@Test()
	void dirtySeriesTest() {
		Invoice i = new Invoice();
		i.setSeries(AonRandom.string(4));
		assertTrue(i.isDirty());
	}

	@Test()
	void dirtyReferenceCodeTest() {
		Invoice i = new Invoice();
		i.setReferenceCode(AonRandom.string(10));
		assertTrue(i.isDirty());
	}

	@Test()
	void dirtyIssueDateTest() {
		Invoice i = new Invoice();
		i.setIssueDate( AonRandom.getFutureDate());
		assertTrue(i.isDirty());
	}

	@Test()
	void dirtyTaxDateTest() {
		Invoice i = new Invoice();
		i.setTaxDate( AonRandom.getFutureDate());
		assertTrue(i.isDirty());
	}
	
	@Test()
	void dirtyConfidentialTest() {
		Invoice i = new Invoice();
		i.setConfidential( !i.isConfidential());
		assertTrue(i.isDirty());
	}
	
	@Test()
	void dirtyRegistryTest() {
		Invoice i = new Invoice();
		i.setRegistry( Integer.MAX_VALUE);
		assertTrue(i.isDirty());
	}
	
	@Test()
	void dirtyRegistryDocumentTest() {
		Invoice i = new Invoice();
		i.setRegistryDocument(AonRandom.string(10));
		assertTrue(i.isDirty());
	}
	
	@Test()
	void dirtyRegistryDocumentTypeTest() {
		Invoice i = new Invoice();
		i.setRegistryDocumentType( DocumentType.CIF );
		assertTrue(i.isDirty());
	}

	@Test()
	void dirtyRegistryDocumentCountryTest() {
		Invoice i = new Invoice();
		i.setRegistryDocumentCountry( Country.AM );
		assertTrue(i.isDirty());
	}

	@Test()
	void dirtyRegistryNameTest() {
		Invoice i = new Invoice();
		i.setRegistryName(AonRandom.string(10));
		assertTrue(i.isDirty());
	}
	
	@Test()
	void dirtyNumberTest() {
		Invoice i = new Invoice();
		i.setNumber( Integer.MAX_VALUE);
		assertTrue(i.isDirty());
	}

	@Test()
	void dirtyCreationUserTest() {
		Invoice i = new Invoice();
		i.setCreationUser(AonRandom.string(10));
		assertTrue(i.isDirty());
	}
	@Test()
	void dirtyCreationDateTest() {
		Invoice i = new Invoice();
		i.setCreationDate( AonRandom.getFutureDate());
		assertTrue(i.isDirty());
	}
	@Test()
	void dirtyModificationUserTest() {
		Invoice i = new Invoice();
		i.setModificationUser(AonRandom.string(10));
		assertTrue(i.isDirty());
	}
	@Test()
	void dirtyModificationDateTest() {
		Invoice i = new Invoice();
		i.setModificationDate( AonRandom.getFutureDate());
		assertTrue(i.isDirty());
	}
	
	@Test()
	void dirtyActivityTest() {
		Invoice i = new Invoice();
		i.setActivity( new Activity() );
		assertTrue(i.isDirty());
	}
	
	@Test()
	void dirtyMarkTrueTest() {
		Invoice i = new Invoice();
		i.setNumber( Integer.MAX_VALUE);
		i.setId( null );
		assertTrue(i.isDirty());
	}
	
	@Test()
	void dirtyMarkFalseTest() {
		Invoice i = new Invoice();
		i.setId( null );
		i.setSeries( null );
		assertFalse(i.isDirty());
	}
	
}
