package com.esferalia.aon.occam.jooq.test;


import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.server.fiscal.format.mod202.Mod202Writer;


public class Mod202Test {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "miguelsilvestre.ecastellano.dev";
	private static int DOMAIN_ID = 2155;
	

	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( org.gjt.mm.mysql.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID);
	}
	
	@Test
	public void testFile() throws IOException {
		Writer writer = new OutputStreamWriter( System.out );  
		List<Mod202> list = AON.getMod202s(DOMAIN_NAME, DOMAIN_ID);
		for (Mod202 mod202 : list) {
			Mod202Writer.fill(writer, mod202);
		}
		writer.flush();
		writer.close();
	}
		
	@AfterClass
	public static void afterClass() {
		ctx.finalize();
	}
	
}
