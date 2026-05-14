package com.esferalia.aon.occam.impl.jooq.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;

import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceCalculatorDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.accounting.entry.AccountEntryPrinter;
import com.esferalia.aon.occam.test.faker.InvoiceFaker;
import com.esferalia.aon.occam.test.faker.InvoiceFaker.InvoiceFakerParams;

public class InvoiceRecorderDAOTest extends AbstractOccamTest {
	
	@Test 
	@Ignore
	public void test_sale_invoice() {
		ctx.getDslContext().transaction( configuration -> {
			System.out.println( "Testing sale invoice" );
			InvoiceFakerParams params = new InvoiceFakerParams(ctx).setIssueDate( new Date( ) );
			Invoice invoice = InvoiceFaker.getRandomSales(params);
			AccountEntry ae = null;
			try {
				InvoiceCalculatorDAO.calculate( ctx, invoice);
				invoice = InvoiceDAO.save(ctx,invoice);
				ae = InvoiceRecorderDAO.getInvoiceEntry(ctx, invoice, true);
			} catch (Throwable e) {
				throw print( invoice, ae, e);
			}
		});
	}
	
	@Test
	@Ignore
	public void test_sale_retention_invoice() {
		ctx.getDslContext().transaction( configuration -> {
			System.out.println( "Testing sale retention invoice" );
			InvoiceFakerParams params = new InvoiceFakerParams(ctx).setIssueDate( new Date( ) );
			Invoice invoice = InvoiceFaker.getSalesRetention(params);
			AccountEntry ae = null;
			try {
				InvoiceCalculatorDAO.calculate( ctx, invoice);
				invoice = InvoiceDAO.save(ctx,invoice);
				ae = InvoiceRecorderDAO.getInvoiceEntry(ctx, invoice, true);
				assertEquals( ae.getDebitSum(), ae.getCreditSum(), 0.0001);
			} catch (Throwable e) {
				throw print( invoice, ae, e);
			}
		});
		
	}

	@Test 
	public void test_purchse_invoice() {
		ctx.getDslContext().transaction( configuration -> {
			System.out.println( "Testing purchase invoice" );
			InvoiceFakerParams params = new InvoiceFakerParams(ctx).setIssueDate( new Date( ) );
			Invoice invoice = InvoiceFaker.getPurchaseNational(params);
			AccountEntry ae = null;
			try {
				InvoiceCalculatorDAO.calculate( ctx, invoice);
				invoice = InvoiceDAO.save(ctx,invoice);
				ae = InvoiceRecorderDAO.getInvoiceEntry(ctx, invoice, true);
				assertNotNull( ae );
				assertEquals( ae.getDebitSum(), ae.getCreditSum(), 0.0001);
				print( invoice, ae );
			} catch (Throwable e) {
				e.printStackTrace();
				throw print( invoice, ae, e);
			}
		});
		
	}

	@Test 
	@Ignore
	public void test_purchse_retention_invoice() {
		ctx.getDslContext().transaction( configuration -> {
			System.out.println( "Testing purchase retention invoice" );
			InvoiceFakerParams params = new InvoiceFakerParams(ctx).setIssueDate( new Date( ) );
			Invoice invoice = InvoiceFaker.getPurchaseFarmerRetention(params);
			AccountEntry ae = null;
			try {
				InvoiceCalculatorDAO.calculate( ctx, invoice);
				invoice = InvoiceDAO.save(ctx,invoice);
				ae = InvoiceRecorderDAO.getInvoiceEntry(ctx, invoice, true);
				assertEquals( ae.getDebitSum(), ae.getCreditSum(), 0.0001);
			} catch (Throwable e) {
				throw print( invoice, ae, e);
			}
		});
		
	}

	private void print(Invoice invoice, AccountEntry ae) {
		System.out.println( );
		InvoiceTextPrinter.print(System.out, invoice);
		System.out.println( );
		AccountEntryPrinter.print(System.out, ae, false);
	}
	private Throwable print(Invoice invoice, AccountEntry ae, Throwable e) {
		e.printStackTrace();
		print(invoice, ae);
		return e;
	}
}
