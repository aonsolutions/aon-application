package com.esferalia.aon.occam.jooq.test;


import java.sql.SQLException;
import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Mod390;


public class Mod390Test {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "alhymotion-mac.ecastellano.dev";
	private static int DOMAIN_ID = 61;
	

	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( org.gjt.mm.mysql.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID);
	}
	@Test
	public void testByDomains() {
		ArrayList<Mod390> list = AON.getMod390s(DOMAIN_NAME, DOMAIN_ID);
		System.out.println( list.size() );
	}
	@Test
	public void testById() {
		Mod390 mod390 = AON.getMod390(DOMAIN_NAME, DOMAIN_ID, 125);
		Assert.assertEquals( 125, (int) mod390.getId());
	}
		
	@AfterClass
	public static void afterClass() {
		ctx.finalize();
	}
	
}
