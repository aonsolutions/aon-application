package com.esferalia.aon.occam.test.zOLD;


import java.io.IOException;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AonRole;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.mysql.jdbc.Driver;

import net.aonsolutions.core.pool.AonConnectionException;


public class SecurityTest {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "mac.ecastellano.dev";
	private static int DOMAIN_ID = 553;
	private static String USER = "mac";
	
	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID,USER);
	}
	
	// @Test
	public void testRoles() throws IOException {
		User user = SecurityDAO.getUser(ctx, "mac");
		System.out.println();
		System.out.println("User mac");
		System.out.println("------------");
		for (AonRole role : AonRole.values()) {
			System.out.println(role.getValue() + " ...: " + user.hasRole(role));	
		}
		System.out.println("hasAdminRole...:" +user.hasAdminRole());
		System.out.println("hasGuestRole...:" +user.hasGuestRole());
		System.out.println("hasConfigRole...:" +user.hasConfigRole());
		System.out.println("hasAuditorRole...:" +user.hasAuditorRole());
		System.out.println("hasConfidentialityRole...:" +user.hasConfidentialityRole());
		System.out.println("hasProductRole...:" +user.hasProductRole());
		System.out.println("hasCommercialRole...:" +user.hasCommercialRole());
		System.out.println("hasSaleRole...:" +user.hasSaleRole());
		System.out.println("hasPurchaseRole...:" +user.hasPurchaseRole());
		System.out.println("hasWarehouseRole...:" +user.hasWarehouseRole());
		System.out.println("hasAccountingRole...:" +user.hasAccountingRole());
		System.out.println("hasFinanceRole...:" +user.hasFinanceRole());
		System.out.println("hasStatisticsRole...:" +user.hasStatisticsRole());
		System.out.println("hasTaskMonitoringRole...:" +user.hasTaskMonitoringRole());
		System.out.println("hasESignatureRole...:" +user.hasESignatureRole());
		System.out.println("hasSysAdminRole...:" +user.hasSysAdminRole());
		System.out.println("hasTGCRole...:" +user.hasTGCRole());
		System.out.println("hasDOCUMENTRole...:" +user.hasDOCUMENTRole());
		System.out.println("hasDocumentManagerRole...:" +user.hasDocumentManagerRole());
		System.out.println("hasPayrollRole...:" +user.hasPayrollRole());
		System.out.println("hasFiscalRole...:" +user.hasFiscalRole());
		System.out.println("hasAccountingManagerRole...:" +user.hasAccountingManagerRole());
		
	}
		
	// @Test
	public void testScopes() throws IOException {
		User user = SecurityDAO.getUser(ctx, "mac");
		Integer[] scopes = SecurityDAO.getUserScopes(ctx, user.getId());
		System.out.println();
		System.out.println("User mac");
		System.out.println("------------");
		if(scopes != null) {
			for (Integer scope : scopes) {
				System.out.println("Scope ...: " + scope);	
			}
		}

		user = SecurityDAO.getUser(ctx, "mac");
		scopes = SecurityDAO.getUserScopes(ctx, user.getId());
		System.out.println();
		System.out.println("User mac");
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
