package com.esferalia.aon.gwt.fiscal.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.fiscal.shared.AonSQLException;
import com.esferalia.aon.gwt.fiscal.shared.Company;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.CompanyColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RegistryColumns;

public class SQLCompany {
	private static Logger LOGGER = Logger.getLogger(SQLCompany.class.getName());

	//@formatter:off
	private static final String SELECT_COMPANY = "SELECT "
		+ SQLConstants.COMPANY   + "." + CompanyColumns.REGISTRY + " " + CompanyColumns.REGISTRY + ","
		+ SQLConstants.REGISTRY   + "." + RegistryColumns.DOCUMENT + " " + RegistryColumns.DOCUMENT + ","
		+ SQLConstants.REGISTRY   + "." + RegistryColumns.NAME + " " + RegistryColumns.NAME
		+ " FROM " + SQLConstants.COMPANY 
		+ " INNER JOIN " + SQLConstants.REGISTRY 
			+ " ON " + SQLConstants.COMPANY + "." + CompanyColumns.REGISTRY 
			+ "=" + SQLConstants.REGISTRY + "."+ RegistryColumns.ID
		+ " WHERE " + SQLConstants.COMPANY + "." + CompanyColumns.DOMAIN + " = ?";
	//@formatter:on

	public static Company getCompany(int domain, Connection conn)
			throws AonSQLException {
		String select = SELECT_COMPANY;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(select, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, domain);
			LOGGER.log(Level.INFO, "COMPANY ( dom: " + domain + ")");
			rs = stmt.executeQuery();
			Company company = new Company();
			while (rs.next()) {
				company.setId(rs.getInt(CompanyColumns.REGISTRY));
				company.setDocument(rs.getString(SQLConstants.RegistryColumns.DOCUMENT));
				company.setName(rs.getString(SQLConstants.RegistryColumns.NAME));
			}
			return company;
		} catch (Throwable e) {
			throw new AonSQLException(e);
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
		}
	}

}
