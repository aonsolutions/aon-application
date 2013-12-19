package com.esferalia.aon.gwt.fiscal.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.fiscal.shared.AonSQLException;
import com.esferalia.aon.gwt.fiscal.shared.Domain;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.DomainColumns;

public class SQLDomain {
	private static Logger LOGGER = Logger.getLogger(SQLDomain.class.getName());

	//@formatter:off
	
	private static final String SELECT_DOMAIN = "SELECT "
		+ SQLConstants.DOMAIN + "." + DomainColumns.ID + " " + DomainColumns.ID + ","
		+ SQLConstants.DOMAIN + "." + DomainColumns.NAME + " " + DomainColumns.NAME + ","
		+ SQLConstants.DOMAIN + "." + DomainColumns.PARENT + " " +DomainColumns.PARENT + ","
		+ SQLConstants.DOMAIN + "." + DomainColumns.TYPE + " " +DomainColumns.TYPE + ","
		+ SQLConstants.DOMAIN + "." + DomainColumns.DOMAINMANAGEMENT + " " + DomainColumns.DOMAINMANAGEMENT + ","
		+ SQLConstants.DOMAIN + "." + DomainColumns.ACTIVE + " " + DomainColumns.ACTIVE
		+ " FROM " + SQLConstants.DOMAIN 
		+ " WHERE " + SQLConstants.DOMAIN + "." + DomainColumns.ID + " = ?";
	//@formatter:on

	public static Domain getDomain(int domain, Connection conn)
			throws AonSQLException {
		String select = SELECT_DOMAIN;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(select, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, domain);
			LOGGER.log(Level.INFO, "GET Domain ( dom: " + domain + ")");
			rs = stmt.executeQuery();
			Domain dom = new Domain();
			while (rs.next()) {
				dom.setId(rs.getInt(DomainColumns.ID));
				dom.setName(rs.getString(DomainColumns.NAME));
				rs.getInt(DomainColumns.PARENT);
				boolean parent = (rs.wasNull()); 
				boolean domainManagement = rs.getBoolean(DomainColumns.DOMAINMANAGEMENT);
				if (!parent) {
					dom.setChild(true);
					dom.setStandalone(false);
					dom.setParent(false);
				} else {
					if (domainManagement) {
						dom.setParent(true);
						dom.setStandalone(false);
						dom.setChild(false);
					} else {
						dom.setParent(false);
						dom.setStandalone(true);
						dom.setChild(false);
					}
				}
			}
			return dom;
		} catch (Throwable e) {
			throw new AonSQLException(e);
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
		}
	}

}
