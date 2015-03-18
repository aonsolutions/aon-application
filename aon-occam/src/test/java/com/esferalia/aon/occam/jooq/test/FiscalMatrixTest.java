package com.esferalia.aon.occam.jooq.test;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.impl.jooq.dao.FiscalMatrixDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;


public class FiscalMatrixTest {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "miguelsilvestre.ecastellano.dev";
	private static Integer DOMAIN_ID = 2155;
	
	

	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( org.gjt.mm.mysql.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID);
	}
	
	@Test
	public void testMatrix() throws IOException {
		
		User user = SecurityDAO.getUser(ctx, "admin");
		FiscalMatrixDAO.getModelsPanel(ctx, DOMAIN_ID, 2014, user.getId())
		.forEach(item -> System.out.println(item));
	}
		
	@AfterClass
	public static void afterClass() {
		ctx.finalize();
	}

}
