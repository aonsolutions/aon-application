package com.esferalia.aon.occam.jooq.test;


import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.esferalia.aon.occam.api.AONContext;

import net.aonsolutions.core.pool.AonConnectionException;


public class FeeTest {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "garajeolabe.aibanez.net";
	private static int DOMAIN_ID = 596;
	private static String USER = "mac";
	
	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( org.mariadb.jdbc.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID,USER);
	}
	
	// ------------------------------------ FEE
	
	
	
	@AfterClass
	public static void afterClass() {
		ctx.finalize();
	}
	
}
