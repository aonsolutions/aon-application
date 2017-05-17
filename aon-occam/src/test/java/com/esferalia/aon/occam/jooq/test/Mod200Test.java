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
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.DoubleVariable2016;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016CorrectionKey;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2016.Mod2002016DAO;


public class Mod200Test {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "proorgan-masdemar.ecastellano.dev";
	private static int DOMAIN_ID = 804;
	private static String LOGIN = "luis";
	

	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( org.gjt.mm.mysql.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID, LOGIN);
	}
	
	@Test
	public void testInitialize() throws IOException {
		Mod2002016 mod200 = FISCAL.getMod2002016ByYear(DOMAIN_NAME, DOMAIN_ID, LOGIN, 2016);
//		for (int i = 0 ; i < 25 ; i++) {
			Date start = new Date();
			for (Mod2002016CorrectionKey ck : Mod2002016CorrectionKey.values()) {
				if (ck.getIncrease() != null) {
					DoubleVariable2016 dv = new DoubleVariable2016(ck.getIncrease());	
					dv.setValue(1.0);
					mod200.getKeysMap().put(ck.getIncrease(), dv);
				}
				if (ck.getDecrease() != null) {
					DoubleVariable2016 dv = new DoubleVariable2016(ck.getDecrease());	
					dv.setValue(1.0);
					mod200.getKeysMap().put(ck.getDecrease(), dv);
				}
			}
			Mod2002016DAO.calculate(mod200,false);
			Date end = new Date();
			long ms = (end.getTime() - start.getTime());
			System.out.println(  ms + " ms.");
//		}
	}
		
	@AfterClass
	public static void afterClass() {
		ctx.finalize();
	}
	
}
