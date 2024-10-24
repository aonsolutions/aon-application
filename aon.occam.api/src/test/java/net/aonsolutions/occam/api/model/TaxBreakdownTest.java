package net.aonsolutions.occam.api.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.aonsolutions.occam.api.model.type.TaxType;

class TaxBreakdownTest {
	
	@Test
	void testReverseCalculateInvoice() {
		TaxBreakdown tb = new TaxBreakdown();
		InvoiceTax i1 = new InvoiceTax().setTaxType(TaxType.VAT).setPercentage(21.0).setBase(100);
		InvoiceTax i2 = new InvoiceTax().setTaxType(TaxType.VAT).setPercentage(10.0).setBase(100);
		InvoiceTax i3 = new InvoiceTax().setTaxType(TaxType.VAT).setPercentage(21.0).setSurcharge(5.2).setBase(100);
		InvoiceTax i4 = new InvoiceTax().setTaxType(TaxType.VAT).setPercentage(10.0).setSurcharge(1.4).setBase(100);
		tb.add(i1);
		assertEquals(1, tb.vatStream().count());
		tb.add(i2);
		assertEquals(2, tb.vatStream().count());
		tb.add(i3);
		assertEquals(3, tb.vatStream().count());
		tb.add(i4);
		assertEquals(4, tb.vatStream().count());
		
				
		
	}
}
