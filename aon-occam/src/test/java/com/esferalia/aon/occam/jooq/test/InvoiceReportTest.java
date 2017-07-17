package com.esferalia.aon.occam.jooq.test;


import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.aonsolutions.core.pool.AonConnectionException;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.watson.server.AonDateUtils;


public class InvoiceReportTest {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "sig.sig.ecastellano.dev";
	private static Integer DOMAIN_ID = 5;
	private static String LOGIN = "jgarcia";
	
	

	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( org.gjt.mm.mysql.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID,LOGIN);
	}
	
	@Test
	public void testInvoices() throws IOException {
		
		final Date fromDate = AonDateUtils.getYearFirstDay(2014);
		final Date toDate = AonDateUtils.getYearLastDay(2014);

		Byte[] types = new Byte[] { 
			 InvoiceType.PURCHASE.value()
			,InvoiceType.SALES.value()
			//InvoiceType.EXPENSES.value()
			//InvoiceType.UNDEDUCTIBLE.value()
		};
//		User user = SecurityDAO.getUser(ctx, "mac"); 
//		User user = SecurityDAO.getUser(ctx, "jgarcia");
		User user = SecurityDAO.getUser(ctx, "aperez");
		Integer[] scopes = SecurityDAO.getUserScopes(ctx, user.getId());
		
		System.out.println((scopes==null?"SCOPES NULL":"SCOPES NOT NULL"));
		
		AON.getInvoiceDetails(DOMAIN_NAME, DOMAIN_ID, LOGIN
				,p -> {
					Filter f = p.getDomainProperty().eq(DOMAIN_ID)
							.and(p.getTypeProperty().in(types) )
							.and(p.getStartIssueDateProperty().ge(fromDate))
							.and(p.getEndIssueDateProperty().le(toDate));
					if (scopes != null){
						f = f.and(p.getScopeProperty().in( scopes ));
					}
					return f;
				}
				
				)
		.forEach(detail -> detail.getDescription());
		
	}
		
	@AfterClass
	public static void afterClass() {
		ctx.finalize();
	}
	
}
