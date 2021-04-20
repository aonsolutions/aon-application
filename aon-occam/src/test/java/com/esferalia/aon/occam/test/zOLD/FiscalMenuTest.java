package com.esferalia.aon.occam.test.zOLD;


import org.json.JSONArray;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalMatrixParams;
import com.esferalia.aon.occam.impl.jooq.dao.FiscalMenuDAO;
import com.mysql.jdbc.Driver;


public class FiscalMenuTest {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "mac.ecastellano.euk";
	private static int DOMAIN_ID = 553;
	private static String USER = "mac";
	
	public static void main(String[] args) throws ClassNotFoundException {
		Class.forName( Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID,USER);
		JSONArray domains = FiscalMenuDAO.getDomainsModels(ctx, DOMAIN_ID, new FiscalMatrixParams().setYear(2018));
		System.out.println( domains.toString(1) );
	}	
	
}
