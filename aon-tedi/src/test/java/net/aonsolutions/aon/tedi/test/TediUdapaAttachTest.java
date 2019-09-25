package net.aonsolutions.aon.tedi.test;

import java.sql.SQLException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.esferalia.aon.occam.api.AONContext;

import net.aonsolutions.aon.tedi.TEDI;
import net.aonsolutions.core.pool.AonConnectionException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TediUdapaAttachTest {
	
	private static AONContext ctx;
	private static String DOMAIN_NAME = "udapa.ecastellano.euk";
	private static int DOMAIN_ID = 3049;
	private static String USER = "montse";
	private static String UUID = "aPV0NI5Dsyh6yGN7XvdY";
	
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
	public void test004GetInvoiceAttach() throws Exception {
		System.out.println( "\t-test Tedi get Udapa verified invoices");
		Object result = TEDI.getInvoiceAttach(DOMAIN_NAME, DOMAIN_ID, true, USER, UUID);
		Assert.assertNotNull(result);
		System.out.print( " [OK]");
	}
	
}
