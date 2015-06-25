package com.esferalia.aon.occam.jooq.test;


import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Mod2002014toD2;


public class Mod200toD2Test {

	private static AONContext ctx;
//	private static String DOMAIN_NAME = "a50111111-masdemar.ecastellano.dev";
//	private static int DOMAIN_ID = 5151;
	private static int DOMAIN_ID = 802;
	private static String DOMAIN_NAME = "GOLDWIN-masdemar.ecastellano.dev";
		

	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( org.gjt.mm.mysql.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID);
	}
	
	@Test
	public void testInitialize() throws IOException {
		Mod2002014 mod200 = AON.getMod2002014ByYear(DOMAIN_NAME, DOMAIN_ID, 2014);
		Map<D2DepositHeaderKey, Double> ctx = new HashMap<D2DepositHeaderKey, Double>();
		Mod2002014toD2.fill(ctx, mod200);
		for (D2DepositHeaderKey key : ctx.keySet() ) {
			System.out.println( key.getCode()  + " = "  + ctx.get(key));
		}
	}
		
	@AfterClass
	public static void afterClass() {
		ctx.finalize();
	}
	
}
