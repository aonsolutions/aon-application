package com.esferalia.aon.gwt.fiscal.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseColumns;

public class SQLFiscalUtils {

	private static final String ENTERPRISE_SELECT = "SELECT " + EnterpriseColumns.REGISTRY 
			+ " FROM " + SQLConstants.ENTERPRISE  
			+ " WHERE " + EnterpriseColumns.DOMAIN + " = ?";
	
	
	public static Integer getEnterpriseId(Connection conn, Integer domain) throws SQLException {
		PreparedStatement selectStmt = null;
		ResultSet rs = null;
		try {
			selectStmt = conn.prepareStatement(ENTERPRISE_SELECT,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			selectStmt.setInt(1, domain);
			rs = selectStmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
			throw new IllegalArgumentException("No existe Empresa para el dominio " + domain);
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (selectStmt != null) {
				selectStmt.close();
			}
		}
	}

}
