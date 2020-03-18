package com.esferalia.aon.occam.jooq.test;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod140Context;
import com.esferalia.aon.occam.api.model.fiscal.Mod140Params;
import com.esferalia.aon.occam.impl.jooq.dao.Mod140DAO;
import com.esferalia.aon.watson.server.AonDateUtils;

import net.aonsolutions.core.pool.AonConnectionException;


public class Mod140Test {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "mac.ecastellano.dev";
	private static int DOMAIN_ID = 476;
	private static String USER = "mac";
	

	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( org.mariadb.jdbc.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID,USER);
	}
	
	// @Test
	public void testInvoices() throws IOException {
		Mod140Context m140ctx = new Mod140Context();
		m140ctx.setDocument("72575077A");
		
		Mod140Params params = new Mod140Params();
		params.setDomain(DOMAIN_ID);
		params.setFromDate(AonDateUtils.getYearFirstDay(2014));
		params.setToDate(AonDateUtils.getYearLastDay(2014));
		
		File file = new File("/tmp/mod140.txt");
		FileOutputStream fos = new FileOutputStream(file);
		OutputStreamWriter writer = new OutputStreamWriter(fos,"ISO-8859-1");
		Mod140DAO.getInvoices(ctx, m140ctx, params, writer);
		writer.flush();
	}
		
	@AfterClass
	public static void afterClass() {
		ctx.finalize();
	}
	
}
