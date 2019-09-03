package es.translogia.tedi.aon;

import java.util.LinkedList;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import es.translogia.tedi.baloo.Tedi;
import es.translogia.tedi.ewok.TediInvoice;
import es.translogia.tedi.ewok.TediInvoiceStatus;
import es.translogia.tedi.json.TediInvoiceJSON;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TediBalooTest {
	
	private static Tedi TEDI = null;

	@BeforeClass
	public static void beforeClass() {
		// TEDI = Tedi.login("aibanez@aonsolutions.es", "test");
		TEDI = Tedi.login("jgarcia@aonsolutions.es", "test");
	}
	
	@After
	public void afterEach() {
		System.out.println("");
	}

	
	@Test
	public void test001PutInvoices() throws Exception {
		int times = 20;
		System.out.print( "\t-test Tedi put UDAPA " + times + " invoices");
		for (int i = 0; i < times ; i ++) {
			JSONObject inv = TediUdapaFaker.getTediInvoice();
			TediInvoice invoice = TediInvoiceJSON.fromJSON(inv);
			invoice.setStatus(TediInvoiceStatus.inbox);
			invoice = TEDI.putInvoice(invoice);
			Assert.assertNotNull(invoice);
			System.out.print( ".");
		}
		System.out.print( " [OK]");
	}
	
	@Test
	public void test002PutAndGetInvoice() throws Exception {
		System.out.print( "\t-test Tedi put and get UDAPA invoice");
		JSONObject inv = TediUdapaFaker.getTediInvoice();
		TediInvoice put = TediInvoiceJSON.fromJSON(inv);
		put = TEDI.putInvoice(put);
		TediInvoice get = TEDI.getInvoice(put.getCompany(), put.getUuid());
		Assert.assertEquals(put.getUuid(), get.getUuid());
		System.out.print( " [OK]");
	}

	@Test
	public void test003PutWrongInvoice() throws Exception {
		System.out.print( "\t-test Tedi put wrong invoice");
		JSONObject inv = TediUdapaFaker.getTediInvoice();
		TediInvoice invoice = TediInvoiceJSON.fromJSON(inv);
		invoice.setCompany(null);
		try {
			invoice = TEDI.putInvoice(invoice);
			Assert.fail("Invoice was wrong!");
		} catch( Throwable t ) {
			Assert.assertNotNull(t);
			System.out.print( " [OK]");
		}
	}

	@Test
	public void test004GetInvoices() throws Exception {
		System.out.print( "\t-test Tedi get Udapa verified invoices");
		LinkedList<TediInvoice> invoices = TEDI.getVerifiedInvoices("F01131978");
		Assert.assertNotNull(invoices);
		System.out.print( " .... " +invoices.size()+ " invoices found.");
		System.out.print( " [OK]");
	}
	
}
