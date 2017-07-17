package com.esferalia.aon.occam.jooq.test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.aonsolutions.core.pool.AonConnectionException;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelMatrix;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelMatrix.FiscalModelMatrixRow;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.impl.jooq.dao.FiscalMatrixDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;


public class FiscalMatrixTest {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "miguelsilvestre.ecastellano.dev";
	private static Integer DOMAIN_ID = 2155;
	private static String USER = "mac";
	
	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( org.gjt.mm.mysql.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID,USER);
	}
	
	@Test
	public void testMatrix() throws IOException {
		User user = SecurityDAO.getUser(ctx, "admin");
		FiscalModelMatrix matrix = FiscalMatrixDAO.getModelsPanel(ctx, DOMAIN_ID, 2014, user.getId());
		for (FiscalModelMatrixRow row : matrix.getRows()) {
			System.out.println(
			 row.getDomainId() + " - " +
			 row.getDomainName() + " - " +
			 row.getYear() + " - " + 
			 row.getModel() + " - " +
			 row.getAdministration() + " - " +
			 row.getPeriod() + " - " +
			 row.getDocument() + " - " +
			 row.getName() + " - " +
			 Arrays.toString( row.getStatuses())+ " - "
					);
		}
	}
		
	@AfterClass
	public static void afterClass() {
		ctx.finalize();
	}

}
