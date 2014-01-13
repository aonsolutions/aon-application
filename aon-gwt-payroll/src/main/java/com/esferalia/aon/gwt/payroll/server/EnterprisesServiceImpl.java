package com.esferalia.aon.gwt.payroll.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.payroll.client.EnterprisesService;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.sql.SQLUtils;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementLevelCategoryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementLevelColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.BonusConceptColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.DeductionConceptColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.DomainColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PaymentConceptColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RegistryColumns;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class EnterprisesServiceImpl extends AonRemoteServiceServlet implements
		EnterprisesService {
	
	@Override
	public ContextDescriptor getContext() {
		SalaryDraft draft = new SalaryDraft();
		
		draft.setStartDate(DateUtils.getFirstDayOfMonth());
		draft.setEndDate(DateUtils.getLastDayOfMonth());
		draft.setIssueDate(draft.getEndDate());
		draft.setChargeDate(draft.getEndDate());
		Employee employee = new Employee();
		employee.setId(-1);
		draft.setEmployee(employee);
		draft.setType(Type.SALARY);
		
		
		return EmployeesServiceImpl.getDraftContext(draft);
	}
	
	
	@Override
	public void saveBonusConcept(Bonus bonus) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void savePaymentConcept(Payment payment) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void saveDeductionConcept(Deduction deduction) {
		// TODO Auto-generated method stub
		
	}
	
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

	@Override
	public List<Bonus> getBonusConcepts(int offset, int limit) {
		Connection connection = null;
		try {
			initFacesContext();
			connection = AonServletUtils.getConnection();
			Integer domainID = getDomainID();
			Integer parentDomainID = getParentDomainID();

			return getBonusConcepts(connection, offset, limit, domainID,
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

	@Override
	public List<Deduction> getDeductionConcepts(int offset, int limit) {
		Connection connection = null;
		try {
			initFacesContext();
			connection = AonServletUtils.getConnection();
			Integer domainID = getDomainID();
			Integer parentDomainID = getParentDomainID();

			return getDeductionConcepts(connection, offset, limit, domainID,
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

	@Override
	public List<Payment> getPaymentConcepts(int offset, int limit) {
		Connection connection = null;
		try {
			initFacesContext();
			connection = AonServletUtils.getConnection();
			Integer domainID = getDomainID();
			Integer parentDomainID = getParentDomainID();

			return getPaymentConcepts(connection, offset, limit, domainID,
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

	private List<Bonus> getBonusConcepts(Connection connection, int offset,
			int limit, Integer domainID, Integer parentDomainID)
			throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			//@formatter:off
			stmt = connection.prepareStatement("SELECT *" + 
					" FROM " + SQLConstants.BONUS_CONCEPT+ 
					" WHERE " + BonusConceptColumns.DOMAIN + " =  ? " + 
					" OR " + BonusConceptColumns.DOMAIN + " = ? " + 
					(parentDomainID != null ? " OR " + BonusConceptColumns.DOMAIN + " = ? " : "") +
					" ORDER BY " + BonusConceptColumns.DESCRIPTION 
					);
			//@formatter:on

			stmt.setInt(1, domainID);
			stmt.setInt(2, 0);
			if (parentDomainID != null)
				stmt.setInt(3, parentDomainID);

			List<Bonus> bonuses = new LinkedList<Bonus>();

			rs = stmt.executeQuery();
			while (rs.next()) {
				Bonus bonus = new Bonus();

				bonus.setId(rs.getInt(BonusConceptColumns.ID)); // not null
				bonus.setDescription(rs
						.getString(BonusConceptColumns.DESCRIPTION));
				bonus.setExpression(rs
						.getString(BonusConceptColumns.EXPRESSION));
				bonus.setType(SQLUtils.getType(
						rs.getObject(BonusConceptColumns.TYPE),
						Bonus.Type.class));

				bonuses.add(bonus);

			}

			return bonuses;

		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

	private List<Payment> getPaymentConcepts(Connection connection, int offset,
			int limit, Integer domainID, Integer parentDomainID)
			throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			//@formatter:off
			stmt = connection.prepareStatement("SELECT *" + 
					" FROM " + SQLConstants.PAYMENT_CONCEPT + 
					" WHERE " + PaymentConceptColumns.DOMAIN + " =  ? " + 
					" OR " + PaymentConceptColumns.DOMAIN + " = ? " + 
					(parentDomainID != null ? " OR " + PaymentConceptColumns.DOMAIN + " = ? " : "") + 
					" ORDER BY " + PaymentConceptColumns.DESCRIPTION 
					);
			//@formatter:on

			stmt.setInt(1, domainID);
			stmt.setInt(2, 0);
			if (parentDomainID != null)
				stmt.setInt(3, parentDomainID);

			List<Payment> payments = new LinkedList<Payment>();

			rs = stmt.executeQuery();
			while (rs.next()) {
				Payment payment = new Payment();

				payment.setId(rs.getInt(PaymentConceptColumns.ID)); // not null
				payment.setName(rs.getString(PaymentConceptColumns.CODE));
				payment.setDescription(rs
						.getString(PaymentConceptColumns.DESCRIPTION));
				payment.setExpression(rs
						.getString(PaymentConceptColumns.EXPRESSION));
				payment.setIrpfExpression(rs
						.getString(PaymentConceptColumns.IRPF_EXPRESSION));
				payment.setQuoteExpression(rs
						.getString(PaymentConceptColumns.QUOTE_EXPRESSION));
				payment.setType(SQLUtils.getType(
						rs.getObject(PaymentConceptColumns.TYPE),
						Payment.Type.class));

				payments.add(payment);

			}

			return payments;

		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

	private List<Deduction> getDeductionConcepts(Connection connection,
			int offset, int limit, Integer domainID, Integer parentDomainID)
			throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			//@formatter:off
			stmt = connection.prepareStatement("SELECT *" + 
					" FROM " + SQLConstants.DEDUCTION_CONCEPT+ 
					" WHERE " + DeductionConceptColumns.DOMAIN + " =  ? " + 
					" OR " + DeductionConceptColumns.DOMAIN + " = ? " + 
					(parentDomainID != null ? " OR " + DeductionConceptColumns.DOMAIN + " = ? " : "") +
					" ORDER BY " + DeductionConceptColumns.DESCRIPTION 
					);
			//@formatter:on

			stmt.setInt(1, domainID);
			stmt.setInt(2, 0);
			if (parentDomainID != null)
				stmt.setInt(3, parentDomainID);

			List<Deduction> deductions = new LinkedList<Deduction>();

			rs = stmt.executeQuery();
			while (rs.next()) {
				Deduction deduction = new Deduction();

				deduction.setId(rs.getInt(DeductionConceptColumns.ID)); // not
																		// null
				deduction.setName(rs.getString(DeductionConceptColumns.CODE));
				deduction.setDescription(rs
						.getString(DeductionConceptColumns.DESCRIPTION));
				deduction.setExpression(rs
						.getString(DeductionConceptColumns.EXPRESSION));
				deduction.setType(SQLUtils.getType(
						rs.getObject(DeductionConceptColumns.TYPE),
						Deduction.Type.class));

				deductions.add(deduction);

			}

			return deductions;

		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

}
