package com.esferalia.aon.occam.jooq.test;


import java.util.LinkedList;

import org.junit.AfterClass;
import org.junit.Test;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014;

import junit.framework.Assert;


public class Mod390Test {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "alhymotion-mac.ecastellano.dev";
	private static int DOMAIN_ID = 61;
	private static String USER = "mac";
	

	@Test
	public void testByDomains() {
		LinkedList<Mod390> list = FISCAL.getMod390s(DOMAIN_NAME, DOMAIN_ID, USER);
		System.out.println( list.size() );
	}
	@Test
	public void testById() {
		Mod3902014 mod390 = FISCAL.getMod3902014(DOMAIN_NAME, DOMAIN_ID, USER, 125);
		Assert.assertEquals( 125, (int) mod390.getId());
	}
		
	@AfterClass
	public static void afterClass() {
		ctx.finalize();
	}
	
}
