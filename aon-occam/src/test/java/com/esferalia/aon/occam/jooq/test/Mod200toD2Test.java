package com.esferalia.aon.occam.jooq.test;


import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Mod2002013toD2;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Mod2002014toD2;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.core.pool.AonConnectionException;


public class Mod200toD2Test {
	
	private static final NumberFormat F = NumberFormat.getInstance(new Locale("ES"));
	static {
		F.setMinimumFractionDigits(2);
	}

	private static int DOMAIN_ID = 802;
	private static String DOMAIN_NAME = "GOLDWIN-masdemar.aibanez.net";
	private static String LOGIN = "admin";
		

	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( org.mariadb.jdbc.Driver.class.getName() );
	}
	
	// @Test
	public void testInitialize() throws IOException {
		Mod2002014 mod200 = FISCAL.getMod2002014ByYear(DOMAIN_NAME, DOMAIN_ID, LOGIN, 2014);
		Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
		Mod2002014toD2.fillBalance(ctx, mod200, 2014);
		System.out.println(AonStringUtils.repeat('-', 104));
		System.out.println(AonStringUtils.center("BALANCE DE SITUACION 2014", 104));
		System.out.println(AonStringUtils.repeat('-', 104));
		toString(ctx);
		System.out.println(AonStringUtils.repeat('-', 104));
		
		System.out.println();
		System.out.println();
		
		Mod2002013 mod2002013 = FISCAL.getMod2002013ByYear(DOMAIN_NAME, DOMAIN_ID, LOGIN, 2013);
		Map<D2DepositHeaderKey, Double> ctx2013 = new LinkedHashMap<D2DepositHeaderKey, Double>();
		Mod2002013toD2.fillBalance(ctx2013, mod2002013);
		System.out.println(AonStringUtils.repeat('-', 104));
		System.out.println(AonStringUtils.center("BALANCE DE SITUACION 2013", 104));
		System.out.println(AonStringUtils.repeat('-', 104));
		toString(ctx2013);
		System.out.println(AonStringUtils.repeat('-', 104));
		
		System.out.println();
		System.out.println();
		
		ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
		Mod2002014toD2.fillPyg(ctx, mod200, 2014);
		System.out.println(AonStringUtils.repeat('-', 104));
		System.out.println(AonStringUtils.center("BALANCE DE PERDIDAS Y GANANCIAS 2014", 104));
		System.out.println(AonStringUtils.repeat('-', 104));
		toString(ctx);
		System.out.println(AonStringUtils.repeat('-', 104));

		System.out.println();
		System.out.println();
		
		ctx2013 = new LinkedHashMap<D2DepositHeaderKey, Double>();
		Mod2002013toD2.fillPyg(ctx2013, mod2002013);
		System.out.println(AonStringUtils.repeat('-', 104));
		System.out.println(AonStringUtils.center("BALANCE DE PERDIDAS Y GANANCIAS 2013", 104));
		System.out.println(AonStringUtils.repeat('-', 104));
		toString(ctx2013);
		System.out.println(AonStringUtils.repeat('-', 104));

		System.out.println();
		System.out.println();
		
		ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
		Mod2002014toD2.fillEcpn(ctx, mod200, 2014);
		System.out.println(AonStringUtils.repeat('-', 104));
		System.out.println(AonStringUtils.center("BALANCE E.C.P.N. 2014", 104));
		System.out.println(AonStringUtils.repeat('-', 104));
		toString(ctx);
		System.out.println(AonStringUtils.repeat('-', 104));

		System.out.println();
		System.out.println();
		
		ctx2013 = new LinkedHashMap<D2DepositHeaderKey, Double>();
		Mod2002013toD2.fillEcpn(ctx2013, mod2002013);
		System.out.println(AonStringUtils.repeat('-', 104));
		System.out.println(AonStringUtils.center("BALANCE E.C.P.N. 2013", 104));
		System.out.println(AonStringUtils.repeat('-', 104));
		toString(ctx2013);
		System.out.println(AonStringUtils.repeat('-', 104));

		System.out.println();
		System.out.println();
		
		ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
		Mod2002014toD2.fillEcpn2(ctx, mod200,2014);
		System.out.println(AonStringUtils.repeat('-', 104));
		System.out.println(AonStringUtils.center("BALANCE E.C.P.N. (II) 2014", 104));
		System.out.println(AonStringUtils.repeat('-', 104));
		toString(ctx);
		System.out.println(AonStringUtils.repeat('-', 104));
		
		System.out.println();
		System.out.println();

		ctx2013 = new LinkedHashMap<D2DepositHeaderKey, Double>();
		Mod2002013toD2.fillEcpn2(ctx2013, mod2002013);
		System.out.println(AonStringUtils.repeat('-', 104));
		System.out.println(AonStringUtils.center("BALANCE E.C.P.N. (II) 2013", 104));
		System.out.println(AonStringUtils.repeat('-', 104));
		toString(ctx2013);
		System.out.println(AonStringUtils.repeat('-', 104));
	}

	@AfterClass
	public static void afterClass() {
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
