package com.code.aon.common.domain;

import static java.beans.Introspector.decapitalize;

import java.beans.Introspector;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;

import com.code.aon.common.annotations.Heritable;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;

import jakarta.persistence.Column;


public class DomainManager {
	
	private static final String DOMAIN_PROPERTY = ".domain";

	private static DomainManager domainManager;
	
	private IDomainProvider domainProvider;
	
	private DomainManager() {
		this.domainProvider = new UniqueDomainProvider();		
	}
	
	private static DomainManager getDomainManager() {
		if (domainManager == null) {
			domainManager = new DomainManager();	
		}
		return domainManager; 
	}
	
	public static void setDomainProvider(IDomainProvider domainProvider) {
		getDomainManager().domainProvider = domainProvider;
	}

	public static IDomainProvider getDomainProvider() {
		return getDomainManager().domainProvider;
	}

	public synchronized static Integer getCurrentDomain() {
		return getDomainProvider().getCurrentDomain();
	}
	
	public synchronized static Integer getParentDomain() {
		return getDomainProvider().getParentDomain();
	}
	
	public synchronized static boolean isDomainManagementAvailable() {
		return getDomainProvider().isDomainManagementAvailable();
	}
	
	public synchronized static boolean isParentDomainUserInChildDomain() {
		Integer userDomain = getDomainProvider().getUserDomain();
		Integer parent = getDomainProvider().getParentDomain();
		return userDomain.equals(parent); 
	}
	
	private static boolean isHeritable(Class<?> entityClass) {
		if ( getDomainProvider().getParentDomain() != null ) {
			Heritable heritable = entityClass.getAnnotation(Heritable.class);
			if ( heritable != null ) {
				if ( heritable.force() || getDomainProvider().isEnableHeredity() ) {
					return true;
				} 
			}
		}
		return false;
	}

	public synchronized static Expression getCurrentDomainExpression(Class<?> entityClass) {
	    String domainProperty = getDomainProperty(entityClass);

	    String alias = entityClass.getSimpleName() + domainProperty;
	    if (isHeritable(entityClass)) {
		Object[] values = new Object[] { getDomainProvider().getParentDomain(), getCurrentDomain() };
		return ExpressionUtilities.getInExpression(alias, values);
	    }
	    return ExpressionUtilities.getEqualExpression(alias, getCurrentDomain());
	}


	public synchronized static String getSQLWhereClause(String columnidentifier) {
		return getSQLWhereClause(columnidentifier,false);
	}
	public synchronized static String getSQLWhereClause(String columnidentifier, Class<?> entityClass) {
		return getSQLWhereClause(columnidentifier,isHeritable(entityClass));
	}
	public synchronized static String getSQLWhereClause(String columnidentifier, boolean heritable) {
		String defaultReturn = (" " + columnidentifier + " = " + getCurrentDomain() + " "); 
		if (heritable && getDomainProvider().isEnableHeredity() ) {
			Integer parent = getDomainProvider().getParentDomain();
			if (parent == null) {
				return defaultReturn;
			}
			return (" " 
					+ columnidentifier
					+ " IN ( " 
					+ parent.toString()
					+","
					+ getCurrentDomain() 
					+ ") ");
		}
		return defaultReturn;
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
	
	/**
	 * @param entityClass
	 * @return
	 */
	private synchronized static String getDomainProperty(Class<?> entityClass) {
	    return  Arrays.stream(entityClass.getMethods())
	    .filter( m -> m.getAnnotation(DomainFilter.class) != null)
	    .map( m -> "."+ decapitalize(m.getName().substring(3)))
	    .findAny().orElse(DOMAIN_PROPERTY);
	}

}