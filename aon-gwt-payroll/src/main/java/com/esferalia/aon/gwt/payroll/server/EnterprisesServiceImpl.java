package com.esferalia.aon.gwt.payroll.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.esferalia.aon.gwt.payroll.client.EnterprisesService;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
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

	private List<Agreement> getAgreements(Connection connection, int offset,
			int limit, Integer domainID, Integer parentDomainID)
			throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement("SELECT "
					+ SQLConstants.AGREEMENT + "." + AgreementColumns.ID + ", "
					+ SQLConstants.AGREEMENT + "."
					+ AgreementColumns.DESCRIPTION + " ," + "COUNT("
					+ SQLConstants.CONTRACT + "." + ContractColumns.ID
					+ " ) + COUNT(" + "EMPLOYEE." + ContractColumns.ID + ") AS EMPLOYEES "
					+ " ," 
					+ "COUNT(" + SQLConstants.AGREEMENT_DATA + "."
					+ AgreementDataColumns.ID + ") + COUNT("
					+ SQLConstants.AGREEMENT_PAYMENT + "."
					+ AgreementPaymentColumns.ID + ") + COUNT("
					+ SQLConstants.AGREEMENT_EXTRA + "."
					+ AgreementExtraColumns.ID + ") + COUNT("
					+ SQLConstants.AGREEMENT_LEVEL + "."
					+ AgreementLevelColumns.ID + ") + COUNT("
					+ SQLConstants.AGREEMENT_LEVEL_DATA + "."
					+ AgreementLevelDataColumns.ID + ") + COUNT("
					+ SQLConstants.AGREEMENT_LEVEL_CATEGORY + "."
					+ AgreementLevelCategoryColumns.ID + ") AS REDEFINED "

					+ " FROM " + SQLConstants.AGREEMENT + " LEFT JOIN "
					+ SQLConstants.AGREEMENT_LEVEL + " ON ("
					+ SQLConstants.AGREEMENT + "." + AgreementColumns.ID
					+ " = " + SQLConstants.AGREEMENT_LEVEL + "."
					+ AgreementLevelColumns.AGREEMENT + " AND "
					+ SQLConstants.AGREEMENT_LEVEL + "."
					+ AgreementLevelColumns.DOMAIN + " = ? )" + " LEFT JOIN "
					+ SQLConstants.AGREEMENT_LEVEL_DATA + " ON ( "
					+ SQLConstants.AGREEMENT_LEVEL + ".id = "
					+ SQLConstants.AGREEMENT_LEVEL_DATA + "."
					+ AgreementLevelDataColumns.AGREEMENT_LEVEL + " AND "
					+ SQLConstants.AGREEMENT_LEVEL_DATA + "."
					+ AgreementLevelDataColumns.DOMAIN + " = ? )"
					+ " LEFT JOIN " + SQLConstants.AGREEMENT_LEVEL_CATEGORY
					+ " ON ( " + SQLConstants.AGREEMENT_LEVEL + ".id = "
					+ SQLConstants.AGREEMENT_LEVEL_CATEGORY + "."
					+ AgreementLevelCategoryColumns.AGREEMENT_LEVEL + " AND "
					+ SQLConstants.AGREEMENT_LEVEL_CATEGORY + "."
					+ AgreementLevelCategoryColumns.DOMAIN + " = ? )"
					+ " LEFT JOIN " + SQLConstants.CONTRACT + " ON (  "
					+ SQLConstants.AGREEMENT_LEVEL_CATEGORY + ".id = "
					+ SQLConstants.CONTRACT + "."
					+ ContractColumns.AGREEMENT_LEVEL_CATEGORY + " AND "
					+ SQLConstants.CONTRACT + "." + ContractColumns.DOMAIN
					+ " = ? )" + " LEFT JOIN " + SQLConstants.AGREEMENT_DATA
					+ " ON (" + SQLConstants.AGREEMENT + "."
					+ AgreementColumns.ID + " = " + SQLConstants.AGREEMENT_DATA
					+ "." + AgreementDataColumns.AGREEMENT + " AND "
					+ SQLConstants.AGREEMENT_DATA + "."
					+ AgreementDataColumns.DOMAIN + " = ? )" + " LEFT JOIN "
					+ SQLConstants.AGREEMENT_PAYMENT + " ON ("
					+ SQLConstants.AGREEMENT + "." + AgreementColumns.ID
					+ " = " + SQLConstants.AGREEMENT_PAYMENT + "."
					+ AgreementPaymentColumns.AGREEMENT + " AND "
					+ SQLConstants.AGREEMENT_PAYMENT + "."
					+ AgreementPaymentColumns.DOMAIN + " = ? )" + " LEFT JOIN "
					+ SQLConstants.AGREEMENT_EXTRA + " ON ("
					+ SQLConstants.AGREEMENT + "." + AgreementColumns.ID
					+ " = " + SQLConstants.AGREEMENT_EXTRA + "."
					+ AgreementExtraColumns.AGREEMENT + " AND "
					+ SQLConstants.AGREEMENT_EXTRA + "."
					+ AgreementExtraColumns.DOMAIN + " = ? )"
					+ " LEFT JOIN " + SQLConstants.AGREEMENT_LEVEL
					+ " AS LEVEL ON (" + SQLConstants.AGREEMENT + "."
					+ AgreementColumns.ID + " = LEVEL."
					+ AgreementLevelColumns.AGREEMENT + " AND LEVEL."
					+ AgreementLevelColumns.DOMAIN + " = "
					+ SQLConstants.AGREEMENT + "." + AgreementColumns.DOMAIN
					+ " )" + " LEFT JOIN "
					+ SQLConstants.AGREEMENT_LEVEL_CATEGORY
					+ " AS CATEGORY ON (" + "LEVEL." + AgreementLevelColumns.ID
					+ " = CATEGORY."
					+ AgreementLevelCategoryColumns.AGREEMENT_LEVEL
					+ " AND CATEGORY." + AgreementLevelColumns.DOMAIN + " = "
					+ SQLConstants.AGREEMENT + "." + AgreementColumns.DOMAIN
					+ " )" + " LEFT JOIN " + SQLConstants.CONTRACT
					+ " AS EMPLOYEE ON (" + "CATEGORY."
					+ AgreementLevelCategoryColumns.ID + " = EMPLOYEE."
					+ ContractColumns.AGREEMENT_LEVEL_CATEGORY
					+ " AND EMPLOYEE." + ContractColumns.DOMAIN + " = ? )"

					+ " WHERE " + SQLConstants.AGREEMENT + "."
					+ AgreementColumns.DOMAIN + " IN (? "
					+ (parentDomainID != null ? ",?" : "") + ")" + " GROUP BY "
					+ SQLConstants.AGREEMENT + "." + AgreementColumns.ID + ", "
					+ SQLConstants.AGREEMENT + "."
					+ AgreementColumns.DESCRIPTION 
					+ " ORDER BY "
					+ SQLConstants.AGREEMENT + "."
					+ AgreementColumns.DESCRIPTION 
					// + " LIMIT ?, ?"
					);

			int i = 1;
			stmt.setInt(i++, domainID); // AgreementLevel
			stmt.setInt(i++, domainID); // AgreementLevelData
			stmt.setInt(i++, domainID); // AgreementLevelCategory
			stmt.setInt(i++, domainID); // Contract
			stmt.setInt(i++, domainID); // AgreementData
			stmt.setInt(i++, domainID); // AgreementPayment
			stmt.setInt(i++, domainID); // AgreementExtra
			stmt.setInt(i++, domainID); // Contract / Employee

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

				agreement.setEmployees(rs.getInt("EMPLOYEEs"));

				agreement.setRedefined(rs.getInt("REDEFINED"));

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
