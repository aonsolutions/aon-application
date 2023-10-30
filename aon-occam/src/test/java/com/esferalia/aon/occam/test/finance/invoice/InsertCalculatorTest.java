package com.esferalia.aon.occam.test.finance.invoice;


import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceCalculator;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.InvoiceFaker;
import com.esferalia.aon.occam.test.faker.InvoiceFaker.InvoiceFakerParams;


public class InsertCalculatorTest extends AbstractOccamTest {

	@Test
	public void testRandomInvoiceInsert() {
		AonConfiguration config = AON.getConfiguration(ctx,null);
		InvoiceFakerParams params = new InvoiceFakerParams(ctx, config).setIssueDate(AonRandom.today());
		// Invoice invoice = InvoiceFaker.getRandom(params);
		Invoice invoice = InvoiceFaker.getExpensesRetention( params );
		InvoiceCalculator.generateTaxBreakdown(invoice);
		InvoicePrinter.print(invoice);
		assertNotEquals("Invoice has no total!",0.0 ,invoice.getTotal() );
		
		Asserts.assertEqualsDouble("VAT base" , invoice.getTaxableBase(), invoice.getTaxBreakdown().getVatBase() );
		Asserts.assertEqualsDouble("VAT quota", invoice.getVatQuota(), invoice.getTaxBreakdown().getVatQuota() );
		
		Asserts.assertEqualsDouble("IRPF quota", invoice.getRetentionQuota(), invoice.getTaxBreakdown().getRetentionQuota() );
		
		Asserts.assertEqualsDouble("TOTAL", invoice.getTotal(), InvoiceCalculator.getTotal(invoice) );
	}
}
