package com.esferalia.aon.occam.jooq.test;


import java.io.IOException;

import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;


public class Mod180Test {

	private static String DOMAIN_NAME = "mac.ecastellano.dev";
	private static int DOMAIN_ID = 760;
	private static String USER = "mac";
	

	// @Test(expected=NullPointerException.class)
	public void testGetByIdNull() throws IOException {
		Mod180 mod180 = FISCAL.getMod180(DOMAIN_NAME, DOMAIN_ID,USER, 97987987);
		mod180.getId(); // NullPointer
	}
		
	// @Test
	public void testGetById() throws IOException {
		Mod180 mod180 = FISCAL.getMod180(DOMAIN_NAME, DOMAIN_ID, USER, 36);
		mod180.getId(); // Not NullPointer
	}
}
