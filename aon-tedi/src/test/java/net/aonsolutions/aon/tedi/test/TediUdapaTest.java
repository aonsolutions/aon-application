package net.aonsolutions.aon.tedi.test;

import java.sql.SQLException;
import java.util.LinkedList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.finance.InvoiceRecorder;
import com.esferalia.aon.occam.api.model.tedi.TediResult;

import net.aonsolutions.aon.tedi.TEDI;
import net.aonsolutions.core.pool.AonConnectionException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TediUdapaTest {
	
	private static AONContext ctx;
	private static String DOMAIN_NAME = "udapa.ecastellano.euk";
	private static int DOMAIN_ID = 3049;
	private static String USER = "montse";

	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		try {
			ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID,USER);
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}
	
	@AfterClass
	public static void afterClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		if (ctx != null) {
			ctx.close();
		}
	}
	
	@After
	public void afterEach() {
		System.out.println("");
	}

	@Test
	public void test004GetVerifiedInvoices() throws Exception {
		System.out.println( "\t-test Tedi get Udapa verified invoices");
		LinkedList<TediResult> results = TEDI.getVerifiedInvoices(ctx, true);
		Assert.assertNotNull(results);
		for (TediResult result : results) {
			if ( result.isImportable() ) {
				AccountEntry[] entries = InvoiceRecorder.recordInvoice( result.getAccountingInvoice() );
				if (entries != null) {
					for (AccountEntry entry :  entries ){
						AccountEntryPrinter.print(entry);
					}
				}
			} else {
				System.out.println( "[NO IMPORTABLE]");
			}
		}
		System.out.print( " [OK]");
	}
	
}
