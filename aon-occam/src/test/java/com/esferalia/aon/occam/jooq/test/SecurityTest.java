package com.esferalia.aon.occam.jooq.test;


import java.io.IOException;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AonRole;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;


public class SecurityTest {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "sig.sig.ecastellano.dev";
	private static int DOMAIN_ID = 5;
	
	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( org.gjt.mm.mysql.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID);
	}
	
	@Test
	public void testRoles() throws IOException {
		User user = SecurityDAO.getUser(ctx, "aperez");
		System.out.println();
		System.out.println("User aperez");
		System.out.println("------------");
		for (AonRole role : AonRole.values()) {
			System.out.println(role.getValue() + " ...: " + user.hasRole(role));	
		}
	}
		
	@Test
	public void testScopes() throws IOException {
		User user = SecurityDAO.getUser(ctx, "asesor");
		Integer[] scopes = SecurityDAO.getUserScopes(ctx, user.getId());
		System.out.println();
		System.out.println("User asesor");
		System.out.println("------------");
		if(scopes != null) {
			for (Integer scope : scopes) {
				System.out.println("Scope ...: " + scope);	
			}
		}

		user = SecurityDAO.getUser(ctx, "aperez");
		scopes = SecurityDAO.getUserScopes(ctx, user.getId());
		System.out.println();
		System.out.println("User aperez");
		System.out.println("------------");
		for (Integer scope : scopes) {
			System.out.println("Scope ...: " + scope);	
		}
	}

	@AfterClass
	public static void afterClass() {
		ctx.finalize();
	}
	
}
