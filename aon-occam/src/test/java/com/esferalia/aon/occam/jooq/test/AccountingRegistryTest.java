package com.esferalia.aon.occam.jooq.test;


import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.aonsolutions.core.pool.AonConnectionException;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.watson.util.AonStringUtils;


public class AccountingRegistryTest {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "mac.ecastellano.dev";
	private static int DOMAIN_ID = 400;
	private static String USER = "mac";
	
	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( com.mysql.jdbc.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID,USER);
	}
	
	@Test
	public void testEnterprise() throws IOException {
		String query = "350";
		final String q = (!AonStringUtils.contains(query, AonStringUtils.PERCENT))
			 	?(AonStringUtils.PERCENT + query + AonStringUtils.PERCENT)
				:(query);
		List<AccountingRegistry> list = ACCOUNTING.getAccountingRegistries(DOMAIN_NAME, DOMAIN_ID,USER,
				p -> p.getDocumentProperty().like(q)
					 .or(p.getNameProperty().like(q))
					 .or(p.getAliasProperty().like(q))
					 .or(p.getAccountCodeProperty().like(q))
					 .or(p.getAccountDescriptionProperty().like(q))
				).collect(Collectors.toCollection(LinkedList::new));
		
		for (AccountingRegistry ar : list) {
			System.out.println(ar.getId() + " --> " + ar.toString());	
		}
	}

	@AfterClass
	public static void afterClass() {
		ctx.finalize();
	}
	
}
