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
import net.aonsolutions.occam.api.invoicing.InvoiceTax;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;


@ExtendWith(TimingExtension.class)	
class InvoiceTaxTest extends AbstractOccamTest {

	@Test()
	void dirtyIdTest() {
		InvoiceTax d = new InvoiceTax();
		d.setId(1);
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyDoTaxTypeTest() {
		InvoiceTax d = new InvoiceTax();
		d.setTaxType(TaxType.RETENTION);
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyPercentTest() {
		InvoiceTax d = new InvoiceTax();
		d.setPercent(Double.valueOf(1.5));
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtySurchargePercentTest() {
		InvoiceTax d = new InvoiceTax();
		d.setSurchargePercent(Double.valueOf(1.5));
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyDeductiblePercentTest() {
		InvoiceTax d = new InvoiceTax();
		d.setDeductiblePercent(Double.valueOf(1.5));
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyVatDeductionTypeTest() {
		InvoiceTax d = new InvoiceTax();
		d.setVatDeductionType(VatDeductionType.NON_TAXABLE);
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyWithholdingTypeTest() {
		InvoiceTax d = new InvoiceTax();
		d.setWithholdingType(WithholdingType.M190_F_02_1);
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyMarkTrueTest() {
		InvoiceTax d = new InvoiceTax();
		d.setPercent( Double.valueOf(7.5) );
		d.setId( null );
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyMarkFalseTest() {
		InvoiceTax d = new InvoiceTax();
		d.setId( null );
		d.setPercent( null );
		assertFalse(d.isDirty());
	}
	
	@Test()
	void equalsTest() {
		InvoiceTax d1 = new InvoiceTax();
		InvoiceTax d2 = null;
		assertNotEquals(d1,d2);
		assertEquals(d1,d1);
		d2 = new InvoiceTax();
		assertEquals(d1,d2);
		d1.setId(1);
		assertNotEquals(d1,d2);
		d2.setId(1);
		assertEquals(d1,d2);
	}

}