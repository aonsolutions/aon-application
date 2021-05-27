package com.esferalia.aon.gwt.fiscal.test.accounting;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import com.esferalia.aon.gwt.fiscal.shared.mod130.Model130AEATScript;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.impl.jooq.dao.Mod130DAO;


public class AccountingBreakdownFormatterTest {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "sig.aonsolutions.es";
	private static int DOMAIN_ID = 5;
	private static String USER = "jgarcia";
	
	
	public static void main(String[] args) throws ClassNotFoundException, FileNotFoundException {
		
		Class.forName( org.mariadb.jdbc.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID, USER);

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
