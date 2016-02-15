package com.esferalia.aon.occam.jooq.test;


import java.io.IOException;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;


public class Mod200Test {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "mac.ecastellano.dev";
	private static int DOMAIN_ID = 234;
	private static String LOGIN = "mac";
	

	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( org.gjt.mm.mysql.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID, LOGIN);
	}
	
	@Test
	public void testInitialize() throws IOException {
		Mod2002013 mod200 = new Mod2002013();
		mod200.setYear(2013);
		mod200.setDomain(DOMAIN_ID);
		mod200 = AON.initializeNewMod2002013(DOMAIN_NAME, DOMAIN_ID, LOGIN, mod200);
		AON.initializeMod2002013(DOMAIN_NAME, DOMAIN_ID, LOGIN, mod200);
	}
		
	@AfterClass
	public static void afterClass() {
		ctx.finalize();
	}
	
}
