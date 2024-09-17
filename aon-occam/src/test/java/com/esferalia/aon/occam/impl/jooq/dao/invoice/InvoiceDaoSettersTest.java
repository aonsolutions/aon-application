package com.esferalia.aon.occam.impl.jooq.dao.invoice;



import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.RepeatedTest;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.InvoiceFaker;


class InvoiceDaoSettersTest extends AbstractOccamTest {
	
	
	@RepeatedTest( 10 )
	void testInsertInvoice() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		invoice = InvoiceDAO.validate(ctx,invoice);
		InvoiceDAO.save(ctx,invoice);
		Optional<Invoice> newInvoiceOpt = InvoiceDAO.getFull(ctx, invoice.getId());
		assertTrue(newInvoiceOpt.isPresent());
		Asserts.assertEqualsFullInvoice(invoice, newInvoiceOpt.get());
	}
	

}
