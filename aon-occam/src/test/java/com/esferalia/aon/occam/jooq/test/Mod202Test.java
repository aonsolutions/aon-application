package com.esferalia.aon.occam.jooq.test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.server.fiscal.format.Mod202Writer;

import net.aonsolutions.core.pool.AonConnectionException;

public class Mod202Test {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "miguelsilvestre.ecastellano.dev";
	private static int DOMAIN_ID = 2155;
	private static String USER_NAME = "admin";
	

	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( org.mariadb.jdbc.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID,USER_NAME);
	}
	
	// @Test
	public void testFile() throws IOException {
		Writer writer = new OutputStreamWriter( new FileOutputStream( "/tmp/mod202.txt" ),"ISO-8859-15");  
		List<Mod202> list = FISCAL.getMod202s(DOMAIN_NAME, DOMAIN_ID, USER_NAME);
		for (Mod202 mod202 : list) {
			Mod202 m202 = FISCAL.getMod202(DOMAIN_NAME, DOMAIN_ID, USER_NAME, mod202.getId());
			Mod202Writer.fillWriter(m202,writer);
		}
		writer.flush();
		writer.close();
	}
		
	@AfterClass
	public static void afterClass() {
		ctx.finalize();
	}
	
}
