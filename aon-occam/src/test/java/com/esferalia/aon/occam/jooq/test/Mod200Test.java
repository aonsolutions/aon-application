package com.esferalia.aon.occam.jooq.test;


import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015;


public class Mod200Test {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "planconsulting.ecastellano.dev";
	private static int DOMAIN_ID = 4079;
	private static String LOGIN = "jvarona";
	

	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( org.gjt.mm.mysql.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID, LOGIN);
	}
	
	@Test
	public void testInitialize() throws IOException {
		Mod2002015 mod200 = new Mod2002015();
		mod200.setYear(2015);
		mod200.setDomain(DOMAIN_ID);
		mod200 = FISCAL.initializeNewMod2002015(DOMAIN_NAME, DOMAIN_ID, LOGIN, mod200);
		FISCAL.initializeMod2002015(DOMAIN_NAME, DOMAIN_ID, LOGIN, mod200);
		for (int i = 0 ; i < 5 ; i++) {
			Date start = new Date();
			FISCAL.calculateMod2002015(mod200);
			Date end = new Date();
			long ms = (end.getTime() - start.getTime());
			System.out.println(i + " - " + ms + " ms.");
		}
	}
		
	@AfterClass
	public static void afterClass() {
		ctx.finalize();
	}
	
}
