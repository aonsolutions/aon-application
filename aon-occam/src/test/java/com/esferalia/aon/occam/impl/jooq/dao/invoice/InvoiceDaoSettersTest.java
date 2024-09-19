package com.esferalia.aon.occam.impl.jooq.dao.invoice;



import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.InvoiceFaker;
import com.esferalia.aon.watson.util.AonCollectionUtils;


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
	
	@RepeatedTest( 10 )
	void testUpdateInvoice() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		invoice = InvoiceDAO.validate(ctx,invoice);
		InvoiceDAO.save(ctx,invoice);
		Optional<Invoice> newInvoiceOpt = InvoiceDAO.getFull(ctx, invoice.getId());
		assertTrue(newInvoiceOpt.isPresent());
		Invoice newInvoice = newInvoiceOpt.get();
		Asserts.assertEqualsFullInvoice(invoice, newInvoice);
		
		newInvoice.setComments("Comentario");
		
		InvoiceDAO.save(ctx,newInvoice);
		Optional<Invoice> updInvoiceOpt = InvoiceDAO.getFull(ctx, newInvoice.getId());
		assertTrue(updInvoiceOpt.isPresent());
		Invoice updInvoice = updInvoiceOpt.get();
		Asserts.assertEqualsFullInvoice(newInvoice, updInvoice);

	}
	

}
