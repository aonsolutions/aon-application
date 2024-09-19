package com.esferalia.aon.occam.impl.jooq.dao.invoice;



import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.InvoiceFaker;
import com.esferalia.aon.watson.server.io.AonIOUtils;


class InvoiceDaoSettersTest extends AbstractOccamTest {
	
	
	@RepeatedTest( 5 )
	void testInsertInvoice() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		invoice = InvoiceDAO.validate(ctx,invoice);
		InvoiceDAO.save(ctx,invoice);
		Optional<Invoice> newInvoiceOpt = InvoiceDAO.getFull(ctx, invoice.getId());
		assertTrue(newInvoiceOpt.isPresent());
		Asserts.assertEqualsFullInvoice(invoice, newInvoiceOpt.get());
	}
	
	@RepeatedTest( 5 )
	void testInsertAndGetInvoice() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		invoice = InvoiceDAO.validate(ctx,invoice);
		Invoice newInvoice = InvoiceDAO.saveAndGet(ctx,invoice);
		assertNotNull(newInvoice);
		Asserts.assertEqualsFullInvoice(invoice, newInvoice);
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
	
	@Test
	void testCRUDE() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		Attach attach = new Attach()
			.setDescription("Factura")
			.setMimeType(MimeType.PDF);
		try (InputStream in = InvoiceDaoSettersTest.class.getResourceAsStream("/com/esferalia/aon/occam/impl/jooq/dao/invoice/Factura.pdf")) {
			byte[] data = assertDoesNotThrow(() -> AonIOUtils.toByteArray(in)); 
			attach.setData( data );
		} catch (IOException e) {
			e.printStackTrace();
			fail( e );
		}
		invoice.setAttach(attach);
		
		invoice = InvoiceDAO.validate(ctx,invoice);
		InvoiceAttachAutoComplete.completeInvoiceAttach(ctx, invoice);
		
		InvoiceDAO.save(ctx,invoice);
		Optional<Invoice> newInvoiceOpt = InvoiceDAO.getFull(ctx, invoice.getId());
		assertTrue(newInvoiceOpt.isPresent());
		Asserts.assertEqualsFullInvoice(invoice, newInvoiceOpt.get());
		
		InvoiceDAO.delete(ctx,invoice.getId());
	}

}
