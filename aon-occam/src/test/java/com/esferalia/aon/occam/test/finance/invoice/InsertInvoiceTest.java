package com.esferalia.aon.occam.test.finance.invoice;


import org.junit.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.Repeat;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.InvoiceFaker;
import com.esferalia.aon.occam.test.faker.InvoiceFaker.InvoiceFakerParams;


public class InsertInvoiceTest extends AbstractOccamTest {

	private void testInsert( Invoice invoice) {
		Invoice inserted = AON.insertInvoice(DOMAIN_NAME, DOMAIN_ID, USER, invoice);
		Invoice selected = AON.getInvoice(DOMAIN_NAME, DOMAIN_ID, USER,inserted.getId());
		Asserts.assertEqualsInvoice(inserted, selected);
	}
	
	@Test
	@Repeat(10)
	public void testRandomInvoiceInsert() {
		InvoiceFakerParams params = new InvoiceFakerParams(ctx).setIssueDate(AonRandom.today());
		testInsert( InvoiceFaker.getRandom(params) );
	}
	
	@Test
	public void testInvoicePurchaseExtracommunityInsert() {
		InvoiceFakerParams params = new InvoiceFakerParams(ctx).setIssueDate(AonRandom.today());
		testInsert( InvoiceFaker.getPurchaseExtracommunity(params) );
	}
	
	@Test
	public void testInvoicePurchaseExtracommunityVatImportInsert() {
		InvoiceFakerParams params = new InvoiceFakerParams(ctx).setIssueDate(AonRandom.today());
		testInsert( InvoiceFaker.getPurchaseExtracommunityVatImport(params) );
	}
	
	@Test
	public void testInvoicePurchaseCanCeuInsert() {
		InvoiceFakerParams params = new InvoiceFakerParams(ctx).setIssueDate(AonRandom.today());
		testInsert( InvoiceFaker.getPurchaseCanCeu(params) );
	}
	
	@Test
	public void testInvoicePurchaseCanCeuVatImportInsert() {
		InvoiceFakerParams params = new InvoiceFakerParams(ctx).setIssueDate(AonRandom.today());
		testInsert( InvoiceFaker.getPurchaseCanCeuVatImport(params) );
	}

}
