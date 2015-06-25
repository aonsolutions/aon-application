package com.esferalia.aon.occam.jooq.test;


import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
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
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod200toD2Test {
	
	private static final NumberFormat F = NumberFormat.getInstance(new Locale("ES"));
	static {
		F.setMinimumFractionDigits(2);
	}

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
		Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
		Mod2002014toD2.fillBalance(ctx, mod200);
		System.out.println(AonStringUtils.repeat('-', 104));
		System.out.println(AonStringUtils.center("BALANCE DE SITUACION", 104));
		System.out.println(AonStringUtils.repeat('-', 104));
		toString(ctx);
		System.out.println(AonStringUtils.repeat('-', 104));
		
		System.out.println();
		System.out.println();
		
		ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
		Mod2002014toD2.fillPyg(ctx, mod200);
		System.out.println(AonStringUtils.repeat('-', 104));
		System.out.println(AonStringUtils.center("BALANCE DE PERDIDAS Y GANANCIAS", 104));
		System.out.println(AonStringUtils.repeat('-', 104));
		toString(ctx);
		System.out.println(AonStringUtils.repeat('-', 104));

		System.out.println();
		System.out.println();
		
		ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
		Mod2002014toD2.fillEcpn(ctx, mod200);
		System.out.println(AonStringUtils.repeat('-', 104));
		System.out.println(AonStringUtils.center("BALANCE E.C.P.N.", 104));
		System.out.println(AonStringUtils.repeat('-', 104));
		toString(ctx);
		System.out.println(AonStringUtils.repeat('-', 104));

		System.out.println();
		System.out.println();
		
		ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
		Mod2002014toD2.fillEcpn2(ctx, mod200);
		System.out.println(AonStringUtils.repeat('-', 104));
		System.out.println(AonStringUtils.center("BALANCE E.C.P.N. (II)", 104));
		System.out.println(AonStringUtils.repeat('-', 104));
		toString(ctx);
		System.out.println(AonStringUtils.repeat('-', 104));
}

	@AfterClass
	public static void afterClass() {
		ctx.finalize();
	}
	
	private void toString(Map<D2DepositHeaderKey, Double> ctx) {
		for (D2DepositHeaderKey key : ctx.keySet() ) {
			String desc = null;
			try {
				desc = D2DepositHeaderKey.valueOf(key.toString() + "98").getDescription();
			} catch (IllegalArgumentException e ) {
				desc = key.toString(); 
			}
			desc = AonStringUtils.rightPad(AonStringUtils.abbreviate(desc, 77), 80, ".");
			System.out.println( desc + " (" + key.getCode()  + ") "  +
					AonStringUtils.leftPad(F.format(ctx.get(key)), 15)
					);
		}
	}
}
