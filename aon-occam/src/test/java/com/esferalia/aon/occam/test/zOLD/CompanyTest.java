package com.esferalia.aon.occam.test.zOLD;


import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.mysql.jdbc.Driver;

import net.aonsolutions.core.pool.AonConnectionException;


public class CompanyTest {

	private static CloseableAONContext ctx;
	private static String DOMAIN_NAME = "mac.ecastellano.dev";
	private static int DOMAIN_ID = 553;
	private static String USER = "mac";
	
	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID,USER);
	}
	
// @Test
//	public void testEnterprise() throws IOException {
//		List<Enterprise> list = CompanyDAO.getParentEnterprises(ctx,
//				p -> p.getParentDomainProperty().eq(DOMAIN_ID)
//							.and((p.getNameProperty().like("%ine%") )
//							.or(p.getAliasProperty().like("%ine%") )
//							.or(p.getDocumentProperty().like("%ine%"))));
//		for (Enterprise enterprise : list) {
//			System.out.println(enterprise.getId() + " --> " + enterprise.toString());	
//		}
//	}

	@AfterClass
	public static void afterClass() {
		ctx.close();
	}
	
}
