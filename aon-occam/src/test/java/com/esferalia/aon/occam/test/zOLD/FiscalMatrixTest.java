package com.esferalia.aon.occam.test.zOLD;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import org.json.JSONArray;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalMatrixParams;
import com.esferalia.aon.occam.impl.jooq.dao.FiscalMenuDAO;
import com.mysql.jdbc.Driver;

import net.aonsolutions.core.pool.AonConnectionException;


public class FiscalMatrixTest {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "romero.ecastellano.euk";
	private static Integer DOMAIN_ID = 15625;
	private static String USER = "noemi";
	
	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID,USER);
	}
	
	@AfterClass
	public static void afterClass() {
		ctx.finalize();
	}

	@Test
	public void testMatrix() throws IOException {
		FiscalMatrixParams p = new FiscalMatrixParams();
		p.setMadeModelsVisible(true);
		p.setYear(2021);
		
		Date now = new Date();
		JSONArray arrray = FiscalMenuDAO.getDomainsModels(ctx, DOMAIN_ID, p);
		System.out.println(arrray.toString(1));
		System.out.println("Models ..: " + arrray.length());
		System.out.println(" Time: " + ((new Date()).getTime() - now.getTime()) + "ms." );
	}
}
