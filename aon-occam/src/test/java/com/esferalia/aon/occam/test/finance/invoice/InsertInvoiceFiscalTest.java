package com.esferalia.aon.occam.test.finance.invoice;


import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.InvoiceFaker;
import com.esferalia.aon.occam.test.faker.InvoiceFaker.InvoiceFakerParams;


public class InsertInvoiceFiscalTest extends AbstractOccamTest {

	@Test
	public void testRandomInvoiceInsert() {
		AonConfiguration config = AON.getConfiguration(ctx,null);
		InvoiceFakerParams params = new InvoiceFakerParams(ctx, config).setIssueDate(AonRandom.today());
		Invoice invoice = InvoiceFaker.getRandom(params);
		Invoice inserted = AON.insertInvoice(DOMAIN_NAME, DOMAIN_ID, USER, invoice);
		System.out.println(  inserted.getId() );
		Invoice selectInserted= AON.getInvoice(DOMAIN_NAME, DOMAIN_ID, USER,inserted.getId());
		Asserts.assertEqualsInvoice(inserted, selectInserted);
		
		selectInserted.setIssueDate(AonRandom.yesterday());
		selectInserted.setTaxDate(AonRandom.tomorrow());
		Invoice updated = AON.updateInvoice(DOMAIN_NAME, DOMAIN_ID, USER, selectInserted);
		Invoice selectUpdated = AON.getInvoice(DOMAIN_NAME, DOMAIN_ID, USER,inserted.getId());
		Asserts.assertEqualsInvoice(updated, selectUpdated);
		
	}
	
}
