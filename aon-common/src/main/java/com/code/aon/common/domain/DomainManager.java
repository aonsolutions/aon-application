package com.code.aon.common.domain;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.annotations.Heritable;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;


public class DomainManager {
	
	private static final String DOMAIN_PROPERTY = ".domain";

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
	public static void removeDomainProvider(IDomainProvider domainProvider) {
		boolean removed = getDomainManager().getDomainProviders().remove(domainProvider);
		if (removed) {
			System.out.println(" DomianProvider removed.");	
		}
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
	
	public synchronized static boolean isDomainManagementAvailable() {
		ensureCurrentDomain();
		return getDomainProvider().isDomainManagementAvailable();
	}
	public synchronized static boolean isParentDomainUserInChildDomain() {
		ensureCurrentDomain();
		int userDomain = getDomainProvider().getUserDomain();
		int current = getDomainProvider().getCurrentDomain();
		return (current != userDomain); 
	}

	public synchronized static Expression getCurrentDomainExpression(Class<?> entityClass) {
		String alias = entityClass.getSimpleName() + DOMAIN_PROPERTY;
		if ( (getDomainProvider().getParentDomain() != null) &&
				getDomainProvider().isEnableHeredity() && 
				entityClass.isAnnotationPresent(Heritable.class) ) {
			Object[] values = new Object[]{getDomainProvider().getParentDomain(), getCurrentDomain()};
			return ExpressionUtilities.getInExpression(alias, values);
		}
		return ExpressionUtilities.getEqualExpression(alias, getCurrentDomain());
	}

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