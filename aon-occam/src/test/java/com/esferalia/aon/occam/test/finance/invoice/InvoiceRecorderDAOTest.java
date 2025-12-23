package com.esferalia.aon.occam.test.finance.invoice;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceRecorderDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.accounting.entry.AccountEntryPrinter;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.InvoiceFaker;
import com.esferalia.aon.occam.test.faker.InvoiceFaker.InvoiceFakerParams;

public class InvoiceRecorderDAOTest extends AbstractOccamTest {
	
	@Test
	public void generationTest() {
		InvoiceFakerParams params = new InvoiceFakerParams(ctx).setIssueDate(AonRandom.today());
		Invoice invoice = InvoiceFaker.getRandom(params);
		AccountEntry entry = InvoiceRecorderDAO.getInvoiceEntry(ctx, invoice);
		AccountEntryPrinter.print(System.out, entry);
	}

}
