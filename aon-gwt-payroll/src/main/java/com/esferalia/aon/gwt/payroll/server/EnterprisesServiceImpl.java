package com.esferalia.aon.gwt.payroll.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.payroll.client.EnterprisesService;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.payroll.AgreementLevel;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementExtraColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementLevelCategoryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementLevelColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementLevelDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementPaymentColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.DomainColumns;
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
			initFacesContext();
			connection = AonServletUtils.getConnection();
			return getEnterprises(connection, getDomainID(), offset, limit);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
			releaseFacesContext();
		}
	}

	@Override
	public List<Agreement> getAgreements(int offset, int limit) {
		Connection connection = null;
		try {
			initFacesContext();
			connection = AonServletUtils.getConnection();
			Integer domainID = getDomainID();
			Integer parentDomainID = getParentDomainID();

			return getAgreements(connection, offset, limit, domainID,
					parentDomainID);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
			releaseFacesContext();
		}
	}

	// --------------------------------------------------------- Private methods

	private List<Enterprise> getEnterprises(Connection connection,
			int domainId, int offset, int limit) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement("SELECT * FROM "
					+ SQLConstants.ENTERPRISE + ", " + SQLConstants.REGISTRY
					+ ", " + SQLConstants.DOMAIN + " WHERE "
					+ EnterpriseColumns.REGISTRY + " = "
					+ SQLConstants.REGISTRY + "." + RegistryColumns.ID
					+ " AND " + SQLConstants.ENTERPRISE + "."
					+ EnterpriseColumns.DOMAIN + " = " + SQLConstants.DOMAIN
					+ "." + DomainColumns.ID + " AND ( " + SQLConstants.DOMAIN
					+ "." + DomainColumns.ID + " = ? " + " OR "
					+ SQLConstants.DOMAIN + "." + DomainColumns.PARENT
					+ " = ? " + ")" + " LIMIT ?, ?");
			stmt.setInt(1, domainId);
			stmt.setInt(2, domainId);

			stmt.setInt(3, offset);
			stmt.setInt(4, limit);

			rs = stmt.executeQuery();

			List<Enterprise> enterprises = new LinkedList<Enterprise>();
			while (rs.next()) {
				Enterprise enterprise = new Enterprise();
				enterprise.setId(rs.getInt(EnterpriseColumns.REGISTRY)); // Not
																			// NULL
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

	private static String LEVELS_WITHOUT_CATEGORIES = "LEVELS_WO";

	private static List<Agreement> getAgreements(Connection connection,
			int offset, int limit, Integer domainID, Integer parentDomainID)
			throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {

			//@formatter:off
			stmt = connection.prepareStatement("SELECT "
					+ SQLConstants.AGREEMENT + "." + AgreementColumns.ID 
					+ ", " + SQLConstants.AGREEMENT + "." + AgreementColumns.DESCRIPTION 
					+ ", ( SELECT count(*)"
						+ " FROM " + SQLConstants.AGREEMENT_LEVEL 
						+ " WHERE " + SQLConstants.AGREEMENT_LEVEL +"."+ AgreementLevelColumns.AGREEMENT + " = " + SQLConstants.AGREEMENT +"."+ AgreementColumns.ID 
						+ " AND " + SQLConstants.AGREEMENT_LEVEL +"."+ AgreementLevelColumns.ID + " NOT IN"
							+ " ( SELECT " + AgreementLevelCategoryColumns.AGREEMENT_LEVEL 
							+ " FROM " + SQLConstants.AGREEMENT_LEVEL_CATEGORY 
							+ " WHERE " + AgreementLevelCategoryColumns.AGREEMENT_LEVEL + " = " + SQLConstants.AGREEMENT_LEVEL + "." + AgreementLevelColumns.ID + "))"
					+ " AS " + LEVELS_WITHOUT_CATEGORIES
					+ " FROM " + SQLConstants.AGREEMENT 
					+ " WHERE " + SQLConstants.AGREEMENT + "." + AgreementColumns.DOMAIN + " IN ( ? " + (parentDomainID != null ? ",?" : "") + ")" 
					+ " ORDER BY " + SQLConstants.AGREEMENT + "." + AgreementColumns.DESCRIPTION 
					// + " LIMIT ?, ?"
					);
			//@formatter:on

			int i = 1;

			stmt.setInt(i++, domainID);
			if (parentDomainID != null)
				stmt.setInt(i++, parentDomainID);
			// stmt.setInt(i++, offset);
			// stmt.setInt(i++, limit);

			rs = stmt.executeQuery();

			List<Agreement> agreements = new LinkedList<Agreement>();
			while (rs.next()) {
				Agreement agreement = new Agreement();
				agreement.setId(rs.getInt(SQLConstants.AGREEMENT + "."
						+ AgreementColumns.ID)); // Not NULL
				agreement.setDescription(rs.getString(SQLConstants.AGREEMENT
						+ "." + AgreementColumns.DESCRIPTION));
				agreement.setLevelsWithoutCategories(rs
						.getInt(LEVELS_WITHOUT_CATEGORIES) > 0);
				// agreement.setEmployees(rs.getInt("EMPLOYEEs"));
				// agreement.setRedefined(rs.getInt("REDEFINED"));
				agreements.add(agreement);
			}

			return agreements;

		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();

		}

	}

}
