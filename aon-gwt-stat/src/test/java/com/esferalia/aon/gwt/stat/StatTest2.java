package com.esferalia.aon.gwt.stat;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;

import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.esferalia.aon.jooq.tables.Invoice;
import com.esferalia.aon.occam.api.AONContext;

public class StatTest2 {
	
	//mostrar por consola las facturas de un dominio entre fechas

	private static final int DOMAIN_ID = 400;
	private static final String DOMAIN_NAME = "mac.eirazu.dev";
	private static final String USER_NAME = "mac";
	
	private static AONContext ctx;
	
	@BeforeClass
	public static void beforeClass() {
		ctx = AONContext.getAONContext( DOMAIN_NAME, DOMAIN_ID, USER_NAME );
		
	}
	
	@Test
	public void testEmptyDomain() throws ClassNotFoundException, SQLException {
		System.out.println( 
		ctx.getDslContext()
			.select( INVOICE.ID)
			.from(INVOICE)
			.where(INVOICE.DOMAIN.eq(DOMAIN_ID))
			.limit(50)
			.fetch()
			);
	}
	
	@AfterClass
	public static void afterClass() {
		ctx.close();
	}

	
}