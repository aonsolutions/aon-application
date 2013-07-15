package com.esferalia.aon.gwt.payroll.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.payroll.client.EnterprisesService;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RegistryColumns;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class EnterprisesServiceImpl extends AonRemoteServiceServlet implements
		EnterprisesService {

	@Override
	public List<Enterprise> getEnterprises(int offset, int limit) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection();
			return getEnterprises(connection, offset, limit);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}

	// --------------------------------------------------------- Private methods
	
	public List<Enterprise> getEnterprises(Connection connection, int offset,
			int limit) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement("SELECT * FROM "
					+ SQLConstants.ENTERPRISE + ", " + SQLConstants.REGISTRY + " WHERE "+ EnterpriseColumns.REGISTRY + " = " +  RegistryColumns.ID + " LIMIT ?, ?");
			stmt.setInt(1, offset);
			stmt.setInt(2, limit);

			rs = stmt.executeQuery();

			List<Enterprise> enterprises = new LinkedList<Enterprise>();
			while (rs.next()) {
				Enterprise enterprise = new Enterprise();
				enterprise.setId(rs.getInt(EnterpriseColumns.REGISTRY)); // Not NULL
				enterprise.setName(rs.getString(RegistryColumns.NAME));
				enterprises.add(enterprise);
			}

			return enterprises;

		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();

		}

	}
}
