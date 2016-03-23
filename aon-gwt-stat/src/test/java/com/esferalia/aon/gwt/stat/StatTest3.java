package com.esferalia.aon.gwt.stat;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.watson.server.AonDateUtils;

public class StatTest3 {
	
	private static final int DOMAIN_ID = 400;
	private static final String DOMAIN_NAME = "mac.eirazu.dev";
	private static final String USER_NAME = "mac";
	
	private static AONContext ctx;
	private static AONContext ctx3;

	
	@BeforeClass
	public static void beforeClass() {
		ctx = AONContext.getAONContext( DOMAIN_NAME, DOMAIN_ID, USER_NAME );
		ctx3 = AONContext.getAONContext( DOMAIN_NAME, DOMAIN_ID, USER_NAME );
	}
	
	@Test
	//segunda forma, la más eficaz
	//en vez de hacer la sql aquí, la llama de StatDAO
	public void testInvoices3() throws ClassNotFoundException, SQLException {
		StatDAO.getInvoices3(ctx3)
		.stream()
		.forEach(is -> System.out.println(is.getId() + " -- " + is.getDomain() + " -- " + is.getRegistry() + " -- " + is.getRname() + " -- " + is.getNationality() )); 

	}
	
	
	@AfterClass
	public static void afterClass() {
		ctx.close();
		ctx3.close();
	}
}
