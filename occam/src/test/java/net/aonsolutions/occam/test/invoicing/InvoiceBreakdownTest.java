package net.aonsolutions.occam.test.invoicing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.constants.TaxType;
import net.aonsolutions.occam.api.constants.VatDeductionType;
import net.aonsolutions.occam.api.constants.WithholdingType;
import net.aonsolutions.occam.api.invoicing.Invoice;
import net.aonsolutions.occam.api.invoicing.InvoiceBreakdown;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;


@ExtendWith(TimingExtension.class)	
class InvoiceBreakdownTest extends AbstractOccamTest {

	@Test()
	void dirtyIdTest() {
		InvoiceBreakdown d = new InvoiceBreakdown();
		d.setId(1);
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyDoTaxTypeTest() {
		InvoiceBreakdown d = new InvoiceBreakdown();
		d.setTaxType(TaxType.RETENTION);
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyBaseTest() {
		InvoiceBreakdown d = new InvoiceBreakdown();
		d.setBase(Double.valueOf(1.5));
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyPercentTest() {
		InvoiceBreakdown d = new InvoiceBreakdown();
		d.setPercent(Double.valueOf(1.5));
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyQuotaTest() {
		InvoiceBreakdown d = new InvoiceBreakdown();
		d.setQuota(Double.valueOf(1.5));
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtySurchargePercentTest() {
		InvoiceBreakdown d = new InvoiceBreakdown();
		d.setSurchargePercent(Double.valueOf(1.5));
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtySurchargeQuotaTest() {
		InvoiceBreakdown d = new InvoiceBreakdown();
		d.setSurchargeQuota(Double.valueOf(1.5));
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyDeductiblePercentTest() {
		InvoiceBreakdown d = new InvoiceBreakdown();
		d.setDeductiblePercent(Double.valueOf(1.5));
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyDeductibleQuotaTest() {
		InvoiceBreakdown d = new InvoiceBreakdown();
		d.setDeductibleQuota(Double.valueOf(1.5));
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyVatDeductionTypeTest() {
		InvoiceBreakdown d = new InvoiceBreakdown();
		d.setVatDeductionType(VatDeductionType.NON_TAXABLE);
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyWithholdingTypeTest() {
		InvoiceBreakdown d = new InvoiceBreakdown();
		d.setWithholdingType(WithholdingType.M190_F_02_1);
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