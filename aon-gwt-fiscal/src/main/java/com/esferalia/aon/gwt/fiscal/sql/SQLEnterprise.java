package com.esferalia.aon.gwt.fiscal.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.fiscal.shared.Enterprise;
import com.esferalia.aon.gwt.fiscal.shared.AonSQLException;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.DomainColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RegistryColumns;

public class SQLEnterprise {
	private static Logger LOGGER = Logger.getLogger(SQLEnterprise.class
			.getName());

	//@formatter:off
	private static final String SELECT_ENTERPRISES = "SELECT "
		+ SQLConstants.ENTERPRISE + "." + EnterpriseColumns.REGISTRY + ","
		+ SQLConstants.ENTERPRISE + "." + EnterpriseColumns.DOMAIN + ","
		+ SQLConstants.REGISTRY   + "." + RegistryColumns.DOCUMENT   + ","
		+ SQLConstants.REGISTRY   + "." + RegistryColumns.NAME
		+ " FROM " + SQLConstants.ENTERPRISE 
		+ " INNER JOIN " + SQLConstants.REGISTRY 
			+ " ON " + SQLConstants.ENTERPRISE + "." + EnterpriseColumns.REGISTRY 
			+ "=" + SQLConstants.REGISTRY + "."+ RegistryColumns.ID
		+ " INNER JOIN " + SQLConstants.DOMAIN
			+ " ON " + SQLConstants.ENTERPRISE + "." + EnterpriseColumns.DOMAIN
			+ "=" + SQLConstants.DOMAIN + "."+ DomainColumns.ID
		+ " WHERE  ( " + SQLConstants.ENTERPRISE + "." + EnterpriseColumns.DOMAIN + " = ?"
		+" OR ( " + SQLConstants.DOMAIN + "."+ DomainColumns.PARENT + " IS NOT NULL "
		+" AND " + SQLConstants.ENTERPRISE + "." + EnterpriseColumns.DOMAIN+ " IN ("
			+ "SELECT " + DomainColumns.ID + " FROM " + SQLConstants.DOMAIN 
			+ " WHERE " + SQLConstants.DOMAIN + "."+ DomainColumns.PARENT + " =  ? )))"
		+" AND " + SQLConstants.DOMAIN + "." + DomainColumns.ACTIVE + "=1";
	
	private static final String ENTERPRISES_SUGGEST_DOCUMENT = 
			" AND ( " + SQLConstants.REGISTRY + "." + RegistryColumns.DOCUMENT +
			" LIKE ? OR " + SQLConstants.REGISTRY + "." + RegistryColumns.NAME +
			" LIKE ?)"; 
	//@formatter:on

	public static ArrayList<Enterprise> getEnterprises(int domain,
			String query, Connection conn) throws AonSQLException {
		String select = SELECT_ENTERPRISES;
		if (!SQLUtils.isEmpty(query)) {
			select += ENTERPRISES_SUGGEST_DOCUMENT;
		}
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(select, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, domain);
			stmt.setInt(2, domain);
			if (!SQLUtils.isEmpty(query)) {
				stmt.setString(3, "%" + query + "%");
				stmt.setString(4, "%" + query + "%");
			}
			LOGGER.log(Level.INFO, "ENTERPRISE SUGGEST ( dom: " + domain
					+ "),(qry: " + query + ")");
			rs = stmt.executeQuery();
			ArrayList<Enterprise> list = new ArrayList<Enterprise>();
			Enterprise enterprise = null;
			while (rs.next()) {
				enterprise = new Enterprise();
				enterprise.setId(rs.getInt(SQLConstants.ENTERPRISE + "."
						+ SQLConstants.EnterpriseColumns.REGISTRY));
				enterprise.setDomain(rs.getInt(SQLConstants.ENTERPRISE + "."
						+ SQLConstants.EnterpriseColumns.DOMAIN));
				enterprise.setDocument(rs.getString(SQLConstants.REGISTRY + "."
						+ SQLConstants.RegistryColumns.DOCUMENT));
				enterprise.setName(rs.getString(SQLConstants.REGISTRY + "."
						+ SQLConstants.RegistryColumns.NAME));
				list.add(enterprise);
			}
			return list;
		} catch (Throwable e) {
			throw new AonSQLException(e);
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
		}
	}

}
