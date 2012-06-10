package com.code.aon.common.domain;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.util.BasicPrincipal;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;


public class DomainManager {
	
//	private static final String COMPANY = "company.";

	private static DomainManager domainManager;
	
	private List<IDomainProvider> domainProviders;
	
	private DomainManager() {
		setDomainProviders(new LinkedList<IDomainProvider>());		
	}
	
	private static DomainManager getDomainManager() {
		if (domainManager == null) {
			domainManager = new DomainManager();	
		}
		return domainManager; 
	}
	
	private List<IDomainProvider> getDomainProviders() {
		return domainProviders;
	}

	private void setDomainProviders(List<IDomainProvider> domainProviders) {
		this.domainProviders = domainProviders;
	}
	
	public static void addDomainProvider(IDomainProvider domainProvider) {
		getDomainManager().getDomainProviders().add(domainProvider);
	}
	
	public synchronized static IDomainProvider getDomainProvider() {
		for (IDomainProvider domainProvider : getDomainManager().getDomainProviders()) {
			if (domainProvider.accept()) {
				return domainProvider;
			}
		}
		// TODO PELIGRO!!
		if (getDomainManager().getDomainProviders().size() == 0) {
			IDomainProvider domainProvider = new UniqueDomainProvider();
			addDomainProvider( domainProvider );
			return domainProvider;
		}
		throw new IllegalStateException("No hay un proveedor de dominios activo!");
	}

	private static void ensureCurrentDomain() {
		if (getDomainProvider() == null) {
			throw new IllegalStateException("No se ha definido un proveedor de Domain.");
		}
		if (getDomainProvider().getCurrentDomain() == null) {
			throw new IllegalStateException("El proveedor de Domain, no tiene un Domain activo.");
		}
	}

	public synchronized static Integer getCurrentDomain() {
		ensureCurrentDomain();
		return getDomainProvider().getCurrentDomain();
	}

	public synchronized static boolean isParentDomain() {
		ensureCurrentDomain();
		return getDomainProvider().isParentDomain();
	}
	
	public synchronized static boolean isDomainManagementAvailable() {
		ensureCurrentDomain();
		return getDomainProvider().isDomainManagementAvailable();
	}
	public synchronized static boolean isParentDomainUserInChildDomain() {
		ensureCurrentDomain();
		AuthPrincipal principal = BasicPrincipal.getAuthPrincipal();
		int userDomain = principal.getDomainId();
		int current = getDomainProvider().getCurrentDomain();
		return (current != userDomain); 
	}

	public synchronized static Expression getCurrentDomainExpression(String alias) {
		Expression exp = null;
//		if ( isParentFilterApplicable(alias) ) {
//			exp = ExpressionUtilities.getInExpression(alias, getDomainProvider().getDomainFilter());
//		} else {
			exp = ExpressionUtilities.getEqualExpression(alias, getCurrentDomain());
//		}
		return exp;
	}
	
//	private static boolean isParentFilterApplicable(String alias) {
//		return ( !StringUtils.startsWithIgnoreCase(alias, COMPANY)
//				&& DomainManager.isParentDomain() 
//				&& DomainManager.isDomainManagementAvailable());
//	}

	public synchronized static String getSQLWhereClause(String columnidentifier) {
		// TODO esta clausula, debería completarse con los dominios 
		// parents o como quiera que se haga cuando se piense.
		return (" " + columnidentifier + " = " + getCurrentDomain() + " ");
	}

	public synchronized static String getStaticSQLWhereClause(String columnidentifier) {
		// TODO esta clausula, debería completarse con los dominios 
		// parents o como quiera que se haga cuando se piense.
		return (" " + columnidentifier + " = ? ");
	}

	public synchronized static int fillHostVariables(PreparedStatement stmt, int parameterIndex) throws SQLException {
		// TODO esta clausula, debería completarse con los dominios 
		// parents o como quiera que se haga cuando se piense.
		stmt.setInt(parameterIndex, getCurrentDomain());
		// Devuelve el número de variables asignadas.
		return 1;
	}
	
}