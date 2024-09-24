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
import com.esferalia.aon.occam.impl.jooq.dao.accounting.invoice.InvoiceTextPrinter;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.InvoiceFaker;
import com.esferalia.aon.watson.server.io.AonIOUtils;


class InvoiceDaoSettersTest extends AbstractOccamTest {
	private void assertInvoice(Invoice invoice) {
		assertInvoice(invoice, false);
	}
	
	private void assertInvoice(Invoice invoice, boolean print) {
		invoice = InvoiceDAO.validate(ctx,invoice);
		if (print) {
			InvoiceTextPrinter.print(invoice);
		}
		InvoiceDAO.save(ctx,invoice);
		Optional<Invoice> newInvoiceOpt = InvoiceDAO.getFull(ctx, invoice.getId());
		assertTrue(newInvoiceOpt.isPresent());
		if (print) {
			InvoiceTextPrinter.print(newInvoiceOpt.get());
		}
		Asserts.assertEqualsFullInvoice(invoice, newInvoiceOpt.get());
		InvoiceDAO.delete(ctx,invoice.getId());
	}
	
	@Test
	void testSalesNational() {
		Invoice invoice = InvoiceFaker.getSalesNational(ctx);
		assertInvoice(invoice);
	}
	@Test
	void testExpensesNational() {
		Invoice invoice = InvoiceFaker.getExpensesNational(ctx);
		assertInvoice(invoice);
	}
	@Test
	void testPurchaseNational() {
		Invoice invoice = InvoiceFaker.getPurchaseNational(ctx);
		assertInvoice(invoice);
	}
	@Test
	void testPurchaseExtracommunity() {
		Invoice invoice = InvoiceFaker.getPurchaseExtracommunity(ctx);
		assertInvoice(invoice);
	}
	@Test
	void testPurchaseExtracommunityVatImport() {
		Invoice invoice = InvoiceFaker.getPurchaseExtracommunityVatImport(ctx);
		assertInvoice(invoice);
	}
	@Test
	void testPurchaseCanCeu() {
		Invoice invoice = InvoiceFaker.getPurchaseCanCeu(ctx);
		assertInvoice(invoice);
	}
	@Test
	void testSalesCanCeuService() {
		Invoice invoice = InvoiceFaker.getSalesCanCeuService(ctx);
		assertInvoice(invoice);
	}
	@Test
	void testSalesCanCeu() {
		Invoice invoice = InvoiceFaker.getSalesCanCeu(ctx);
		assertInvoice(invoice);
	}
	@Test
	void testPurchaseCanCeuVatImport() {
		Invoice invoice = InvoiceFaker.getPurchaseCanCeuVatImport(ctx);	
		assertInvoice(invoice);
	}
	@Test
	void testExpensesRetention() {
		Invoice invoice = InvoiceFaker.getExpensesRetention(ctx);	
		assertInvoice(invoice);
	}
	@Test
	void testPurchaseFarmerRetention() {
		Invoice invoice = InvoiceFaker.getPurchaseFarmerRetention(ctx);	
		assertInvoice(invoice);
	}
	@Test
	void testSalesFarmerRetention() {
		Invoice invoice = InvoiceFaker.getSalesFarmerRetention(ctx);
		assertInvoice(invoice );
	}
	@Test
	void testSalesRetentionInvoice() {
		Invoice invoice = InvoiceFaker.getSalesRetentionInvoice( ctx, AonRandom.getRandomWithholdingType());
		assertInvoice(invoice);
	}
	
	@RepeatedTest( 5 )
	void testInsertInvoice() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		assertInvoice(invoice);
	}
	
	@RepeatedTest( 5 )
	void testInsertAndGetInvoice() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		invoice = InvoiceDAO.validate(ctx,invoice);
		Invoice newInvoice = InvoiceDAO.saveAndGet(ctx,invoice);
		assertNotNull(newInvoice);
		Asserts.assertEqualsFullInvoice(invoice, newInvoice);
		InvoiceDAO.delete(ctx,invoice.getId());
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
		
		InvoiceFaker.randomUpdate(ctx, newInvoice);
		
		InvoiceDAO.save(ctx,newInvoice);
		Optional<Invoice> updInvoiceOpt = InvoiceDAO.getFull(ctx, newInvoice.getId());
		assertTrue(updInvoiceOpt.isPresent());
		Invoice updInvoice = updInvoiceOpt.get();
		Asserts.assertEqualsFullInvoice(newInvoice, updInvoice);
		
		InvoiceDAO.delete(ctx,invoice.getId());

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
