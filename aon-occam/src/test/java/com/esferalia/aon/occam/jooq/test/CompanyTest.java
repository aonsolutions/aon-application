package com.esferalia.aon.occam.jooq.test;


import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;

import net.aonsolutions.core.pool.AonConnectionException;


public class CompanyTest {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "mac.ecastellano.dev";
	private static int DOMAIN_ID = 553;
	private static String USER = "mac";
	
	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( org.mariadb.jdbc.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID,USER);
	}
	
	// @Test
	public void testEnterprise() throws IOException {
		List<Enterprise> list = CompanyDAO.getParentEnterprises(ctx,
				p -> p.getParentDomainProperty().eq(DOMAIN_ID)
							.and((p.getNameProperty().like("%ine%") )
							.or(p.getAliasProperty().like("%ine%") )
							.or(p.getDocumentProperty().like("%ine%"))));
		for (Enterprise enterprise : list) {
			System.out.println(enterprise.getId() + " --> " + enterprise.toString());	
		}
	}

	@AfterClass
	public static void afterClass() {
		ctx.finalize();
	}
	
}
