package net.aonsolutions.occam.test.invoicing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.constants.Country;
import net.aonsolutions.occam.api.constants.DocumentType;
import net.aonsolutions.occam.api.constants.InvoiceType;
import net.aonsolutions.occam.api.constants.TransactionType;
import net.aonsolutions.occam.api.invoicing.Invoice;
import net.aonsolutions.occam.api.metadata.InvoiceMetadata;
import net.aonsolutions.occam.api.metadata.InvoiceMetadata.InvoiceMetadataVisitor;
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
		assertTrue(d.getDirtySet().contains( InvoiceMetadata.ID ));
	}
	
	@Test()
	void dirtyDomainTest() {
		Invoice d = new Invoice();
		d.setDomain(1);
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceMetadata.DOMAIN ));
	}
	
	@Test()
	void dirtyActivityTest() {
		Invoice d = new Invoice();
		d.setActivity( AonFaker.getActivity());
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceMetadata.ACTIVITY));
	}

	@Test()
	void dirtySeriesTest() {
		Invoice d = new Invoice();
		d.setSeries(AonRandom.string(9));
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceMetadata.SERIES));
	}
	
	@Test()
	void dirtyNumberTest() {
		Invoice d = new Invoice();
		d.setNumber(1);
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceMetadata.NUMBER));
	}

	@Test()
	void dirtyReferenceCodeTest() {
		Invoice d = new Invoice();
		d.setReferenceCode(AonRandom.string(9));
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceMetadata.REFERENCE_CODE));
	}
	
	@Test()
	void dirtyIssueDateCodeTest() {
		Invoice d = new Invoice();
		d.setIssueDate(AonRandom.today());
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceMetadata.ISSUE_DATE));
	}

	@Test()
	void dirtyTaxDateCodeTest() {
		Invoice d = new Invoice();
		d.setTaxDate(AonRandom.today());
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceMetadata.TAX_DATE));
	}

	@Test()
	void dirtyConfidentialTest() {
		Invoice d = new Invoice();
		d.setConfidential( true );
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceMetadata.CONFIDENTIAL));
	}

	@Test()
	void dirtyRegistryTest() {
		Invoice d = new Invoice();
		d.setRegistry(1);
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceMetadata.REGISTRY));
	}

	@Test()
	void dirtyDocumentTest() {
		Invoice d = new Invoice();
		d.setDocument(AonRandom.string(9));
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceMetadata.DOCUMENT));
	}

	@Test()
	void dirtyDocumentTypeTest() {
		Invoice d = new Invoice();
		d.setDocumentType(DocumentType.NIE);
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceMetadata.DOCUMENT_TYPE));
	}

	@Test()
	void dirtyDocumentCountryTest() {
		Invoice d = new Invoice();
		d.setDocumentCountry(Country.NA);
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceMetadata.DOCUMENT_COUNTRY));
	}
	
	@Test()
	void dirtyNameTest() {
		Invoice d = new Invoice();
		d.setName(AonRandom.string(9));
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceMetadata.NAME));
	}

	@Test()
	void dirtyScopeTest() {
		Invoice d = new Invoice();
		d.setScope(AonFaker.getScope());
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceMetadata.SCOPE));
	}
	
	@Test()
	void dirtyTypeTest() {
		Invoice d = new Invoice();
		d.setType(InvoiceType.EXPENSES);
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceMetadata.TYPE));
	}
	
	@Test()
	void dirtyTransactionTest() {
		Invoice d = new Invoice();
		d.setTransaction(TransactionType.EXTRACOMMUNITY);
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceMetadata.TRANSACTION));
	}

	@Test()
	void dirtySurchargeTest() {
		Invoice d = new Invoice();
		d.setSurcharge( true );
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceMetadata.SURCHARGE));
	}

	@Test()
	void dirtyWithholdingTest() {
		Invoice d = new Invoice();
		d.setWithholding( true );
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceMetadata.WITHHOLDING));
	}

	@Test()
	void dirtyWithholdingFarmerTest() {
		Invoice d = new Invoice();
		d.setWithholdingFarmer( true );
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceMetadata.WITHHOLDING_FARMER));
	}

	@Test()
	void dirtyVatAccrualPaymentTest() {
		Invoice d = new Invoice();
		d.setVatAccrualPayment( true );
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceMetadata.VAT_ACCRUAL_PAYMENT));
	}
	
	@Test()
	void dirtyInvestmentTest() {
		Invoice d = new Invoice();
		d.setInvestment( true );
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceMetadata.INVESTMENT));
	}

	@Test()
	void dirtyServiceTest() {
		Invoice d = new Invoice();
		d.setService( true );
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceMetadata.SERVICE));
	}

	@Test()
	void dirtyAnnulledTest() {
		Invoice d = new Invoice();
		d.setAnnulled( true );
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceMetadata.ANNULLED));
	}

	@Test()
	void dirtyTotalTest() {
		Invoice d = new Invoice();
		d.setTotal( 1.0 );
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceMetadata.TOTAL));
	}

	@Test()
	void dirtyDetailTest() {
		Invoice d = new Invoice();
		d.addDetail( AonFaker.getInvoiceDetail());
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceMetadata.DETAILS));
	}

	@Test()
	void dirtyBreakdownTest() {
		Invoice d = new Invoice();
		d.addBreakdown( AonFaker.getInvoiceBreakdown());
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceMetadata.BREAKDOWN));
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
	void documentNumberTest() {
		Invoice d = new Invoice();
		assertEquals("?-??????", d.getDocumentNumber());
	}

	@Test()
	void metadataVisitorTest() {
		InvoiceMetadataVisitor<Boolean,InvoiceMetadata> visitor = new InvoiceMetadataVisitor<Boolean, InvoiceMetadata>() {
			@Override public Boolean visitId(InvoiceMetadata t) {return t == InvoiceMetadata.ID; }
			@Override public Boolean visitDomain(InvoiceMetadata t) {return t == InvoiceMetadata.DOMAIN; }
			@Override public Boolean visitActivity(InvoiceMetadata t) {return t == InvoiceMetadata.ACTIVITY; }
			@Override public Boolean visitSeries(InvoiceMetadata t) {return t == InvoiceMetadata.SERIES; }
			@Override public Boolean visitNumber(InvoiceMetadata t) {return t == InvoiceMetadata.NUMBER; }
			@Override public Boolean visitReferenceCode(InvoiceMetadata t) {return t == InvoiceMetadata.REFERENCE_CODE; }
			@Override public Boolean visitIssueDate(InvoiceMetadata t) {return t == InvoiceMetadata.ISSUE_DATE; }
			@Override public Boolean visitTaxDate(InvoiceMetadata t) {return t == InvoiceMetadata.TAX_DATE; }
			@Override public Boolean visitConfidential(InvoiceMetadata t) {return t == InvoiceMetadata.CONFIDENTIAL; }
			@Override public Boolean visitRegistry(InvoiceMetadata t) {return t == InvoiceMetadata.REGISTRY; }
			@Override public Boolean visitDocument(InvoiceMetadata t) {return t == InvoiceMetadata.DOCUMENT; }
			@Override public Boolean visitDocumentType(InvoiceMetadata t) {return t == InvoiceMetadata.DOCUMENT_TYPE; }
			@Override public Boolean visitDocumentCountry(InvoiceMetadata t) {return t == InvoiceMetadata.DOCUMENT_COUNTRY; }
			@Override public Boolean visitName(InvoiceMetadata t) {return t == InvoiceMetadata.NAME; }
			@Override public Boolean visitScope(InvoiceMetadata t) {return t == InvoiceMetadata.SCOPE; }
			@Override public Boolean visitType(InvoiceMetadata t) {return t == InvoiceMetadata.TYPE; }
			@Override public Boolean visitTransaction(InvoiceMetadata t) {return t == InvoiceMetadata.TRANSACTION; }
			@Override public Boolean visitSurcharge(InvoiceMetadata t) {return t == InvoiceMetadata.SURCHARGE; }
			@Override public Boolean visitWithholding(InvoiceMetadata t) {return t == InvoiceMetadata.WITHHOLDING; }
			@Override public Boolean visitWithholdingFarmer(InvoiceMetadata t) {return t == InvoiceMetadata.WITHHOLDING_FARMER; }
			@Override public Boolean visitVatAccrualPayment(InvoiceMetadata t) {return t == InvoiceMetadata.VAT_ACCRUAL_PAYMENT; }
			@Override public Boolean visitInvestment(InvoiceMetadata t) {return t == InvoiceMetadata.INVESTMENT; }
			@Override public Boolean visitService(InvoiceMetadata t) {return t == InvoiceMetadata.SERVICE; }
			@Override public Boolean visitAnnulled(InvoiceMetadata t) {return t == InvoiceMetadata.ANNULLED; }
			@Override public Boolean visitTotal(InvoiceMetadata t) {return t == InvoiceMetadata.TOTAL; }
			@Override public Boolean visitAudit(InvoiceMetadata t) {return t == InvoiceMetadata.AUDIT; }
			@Override public Boolean visitDetails(InvoiceMetadata t) {return t == InvoiceMetadata.DETAILS; }
			@Override public Boolean visitBreakdown(InvoiceMetadata t) {return t == InvoiceMetadata.BREAKDOWN; }
		}; 
		Arrays.stream(InvoiceMetadata.values()).forEach( dt -> assertTrue(dt.visit(visitor, dt)));		
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