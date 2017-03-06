package com.esferalia.aon.occam.jooq.test;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.fiscal.mod130.Model130AEATScript;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.impl.jooq.dao.Mod130DAO;


public class AccountingBreakdownFormatterTest {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "sig.aonsolutions.es";
	private static int DOMAIN_ID = 5;
	private static String USER = "jgarcia";
	
	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( org.gjt.mm.mysql.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID, USER);
	}
	
	@Test
	public void testInitializeAlava() throws IOException {
		Mod130 mod130 = FISCAL.initializeMod130(DOMAIN_NAME, DOMAIN_ID, USER, null);
		mod130.setAdministration(Administration.COMMON_TERRITORY);
		System.out.println("INITIALIZED!");
		mod130 = FISCAL.createMod130(DOMAIN_NAME, DOMAIN_ID, USER, mod130);
		FileOutputStream fos = new FileOutputStream("/home/ecastellano/test.html");
		PrintWriter writer = new PrintWriter(fos);
		writer.println(
				Mod130DAO.getMod130Info(ctx, mod130, Model130AEATScript.C01 , FiscalModelKeyInfo.ACT_ACCOUNT)
		);
		writer.flush();
		writer.close();
	}

}
