package net.aonsolutions.occam.test.invoicing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.constants.Country;
import net.aonsolutions.occam.api.constants.DocumentType;
import net.aonsolutions.occam.api.constants.InvoiceType;
import net.aonsolutions.occam.api.constants.TransactionType;
import net.aonsolutions.occam.api.invoicing.Invoice;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonFaker;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)	
class InvoiceTest extends AbstractOccamTest {

	@Test()
	void dirtyIdTest() {
		Invoice d = new Invoice();
		d.setId(1);
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyDomainTest() {
		Invoice d = new Invoice();
		d.setDomain(1);
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyActivityTest() {
		Invoice d = new Invoice();
		d.setActivity( AonFaker.getActivity());
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtySeriesTest() {
		Invoice d = new Invoice();
		d.setSeries(AonRandom.string(9));
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtynumberTest() {
		Invoice d = new Invoice();
		d.setNumber(1);
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyReferenceCodeTest() {
		Invoice d = new Invoice();
		d.setReferenceCode(AonRandom.string(9));
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyIssueDateCodeTest() {
		Invoice d = new Invoice();
		d.setIssueDate(AonRandom.today());
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyTaxDateCodeTest() {
		Invoice d = new Invoice();
		d.setTaxDate(AonRandom.today());
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyConfidentialTest() {
		Invoice d = new Invoice();
		d.setConfidential( true );
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyRegistryTest() {
		Invoice d = new Invoice();
		d.setRegistry(1);
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyDocumentTest() {
		Invoice d = new Invoice();
		d.setDocument(AonRandom.string(9));
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyDocumentTypeTest() {
		Invoice d = new Invoice();
		d.setDocumentType(DocumentType.NIE);
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyDocumentCountryTest() {
		Invoice d = new Invoice();
		d.setDocumentCountry(Country.NA);
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyNameTest() {
		Invoice d = new Invoice();
		d.setName(AonRandom.string(9));
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyScopeTest() {
		Invoice d = new Invoice();
		d.setScope(AonFaker.getScope());
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyTypeTest() {
		Invoice d = new Invoice();
		d.setType(InvoiceType.EXPENSES);
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyTransactionTest() {
		Invoice d = new Invoice();
		d.setTransaction(TransactionType.EXTRACOMMUNITY);
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtySurchargeTest() {
		Invoice d = new Invoice();
		d.setSurcharge( true );
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyWithholdingTest() {
		Invoice d = new Invoice();
		d.setWithholding( true );
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyWithholdingFarmerTest() {
		Invoice d = new Invoice();
		d.setWithholdingFarmer( true );
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyVatAccrualPaymentTest() {
		Invoice d = new Invoice();
		d.setVatAccrualPayment( true );
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyInvestmentTest() {
		Invoice d = new Invoice();
		d.setInvestment( true );
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyServiceTest() {
		Invoice d = new Invoice();
		d.setService( true );
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyAnnulledTest() {
		Invoice d = new Invoice();
		d.setAnnulled( true );
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyTotalTest() {
		Invoice d = new Invoice();
		d.setTotal( 1.0 );
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyDetailTest() {
		Invoice d = new Invoice();
		d.addDetail( AonFaker.getInvoiceDetail());
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyBreakdownTest() {
		Invoice d = new Invoice();
		d.addBreakdown( AonFaker.getInvoiceBreakdown());
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyMarkTrueTest() {
		Invoice d = new Invoice();
		d.setName( "dddddd" );
		d.setId( null );
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyMarkFalseTest() {
		Invoice d = new Invoice();
		d.setId( null );
		d.setName( null );
		assertFalse(d.isDirty());
	}
	
	@Test()
	void equalsTest() {
		Invoice d1 = new Invoice();
		Invoice d2 = null;
		assertNotEquals(d1,d2);
		assertEquals(d1,d1);
		d2 = new Invoice();
		assertEquals(d1,d2);
		d1.setId(1);
		assertNotEquals(d1,d2);
		d2.setId(1);
		assertEquals(d1,d2);
	}

}