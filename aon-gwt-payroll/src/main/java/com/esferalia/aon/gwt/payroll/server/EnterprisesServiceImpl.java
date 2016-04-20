package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.payroll.sql.SQLConstants.ENTERPRISE;
import static com.esferalia.aon.payroll.sql.SQLConstants.SALARY;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.code.aon.jaas.vendor.tomcat.HttpServletRequestValve;
import com.esferalia.aon.google.sql.SQLConstants.UserScopeColumns;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.client.EnterprisesService;
import com.esferalia.aon.gwt.payroll.jooq.JooqAgreement;
import com.esferalia.aon.gwt.payroll.jooq.JooqPayments;
import com.esferalia.aon.gwt.payroll.shared.Activity;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.BankAccount;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.CCC;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.sql.SQLUtils;
import com.esferalia.aon.payroll.calculator.sql.SQLPayrollConstants;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementPaymentColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.BonusConceptColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.CompanyColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractBonusColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractDeductionColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractPaymentColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.DeductionConceptColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.DomainColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseActivityColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseCccColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.GeozoneColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PaymentConceptColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RbankColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RegistryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ScopeColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SystemDeductionColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SystemPaymentColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.UserColumns;
import com.esferalia.aon.salary.enumeration.SalaryType;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class EnterprisesServiceImpl extends AonRemoteServiceServlet implements
		EnterprisesService {

	@Override
	public Integer getDomain() {
		try {
			initFacesContext();
			return getDomainID();
		} finally {
			releaseFacesContext();
		}
	}

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
	public Bonus saveBonusConcept(Bonus bonus) {
		Connection connection = null;
		try {
			initFacesContext();
			connection = AonServletUtils.getConnection();

			Integer domainID = getDomainID();

			if (bonus.getId() != null && domainID.equals(bonus.getDomain())) {
				updateBonusConcept(connection, bonus);
			} else {
				int bonusID = insertBonusConcept(connection, bonus, domainID);
				if (bonus.getId() != null) {
					updateBonuses(connection, domainID, bonus.getId(), bonusID);
				} // end if: Update all bonuses of this domain that references
					// old concept.
				bonus.setId(bonusID);
				bonus.setDomain(domainID);
			}

			return bonus;

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
	public Payment savePaymentConcept(Payment payment) {
		Connection connection = null;
		try {
			initFacesContext();
			connection = AonServletUtils.getConnection();

			Integer domainID = getDomainID();

			if (payment.getId() != null && domainID.equals(payment.getDomain())) {
				updatePaymentConcept(connection, payment);
			} else {
				int paymentID = insertPaymentConcept(connection, payment,
						domainID);
				if (payment.getId() != null) {
					updatePayments(connection, domainID, payment.getId(),
							paymentID);
				} // end if: Update all payments of this domain that references
					// old concept.
				payment.setId(paymentID);
				payment.setDomain(domainID);
			}

			return payment;

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
	public Deduction saveDeductionConcept(Deduction deduction) {
		Connection connection = null;
		try {
			initFacesContext();
			connection = AonServletUtils.getConnection();

			Integer domainID = getDomainID();

			if (deduction.getId() != null
					&& domainID.equals(deduction.getDomain())) {
				updateDeductionConcept(connection, deduction);
			} else {
				int deductionID = insertDeductionConcept(connection, deduction,
						domainID);
				if (deduction.getId() != null) {
					updateDeductions(connection, domainID, deduction.getId(),
							deductionID);
				} // end if: Update all deductions of this domain that
					// references old concept.
				deduction.setId(deductionID);
				deduction.setDomain(domainID);
			}

			return deduction;

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
	public void deleteBonusConcept(Bonus bonus) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection();

			deleteBonusConcept(connection, bonus);

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

	@Override
	public void deleteDeductionConcept(Deduction deduction) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection();

			deleteDeductionConcept(connection, deduction);

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

	@Override
	public void deletePaymentConcept(Payment payment) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection();

			deletePaymentConcept(connection, payment);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} catch (RuntimeException e) {
			
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}
	
	@Override
	public void deleteAgreement(Agreement agreement) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection();
			Integer domain = getDomain();
			JooqAgreement.deleteAgreement(connection, domain, agreement);

		} catch(SQLException e) {
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
	public List<Enterprise> getEnterprises(int offset, int limit) {
		Connection connection = null;
		try {
			initFacesContext();
			connection = AonServletUtils.getConnection();
			return getEnterprises(connection, getUserID(), getDomainID(), offset, limit);

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

			return JooqAgreement.getAgreements(connection, offset, limit,
					domainID, parentDomainID);

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
	public void updateAgreementId(Agreement agreement) {
		Connection connection = null;
		try {
			initFacesContext();
			connection = AonServletUtils.getConnection();
			Integer domainID = getDomain();
			JooqAgreement.updateAgreementId(connection, domainID, agreement);
		} catch(SQLException e) {
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
	public Agreement copyAgreement(Agreement agreement) {
		
		Connection connection = null;
		try {
			initFacesContext();
			connection = AonServletUtils.getConnection();						
			return JooqAgreement.copyAgreement(connection, getDomain(), agreement.getId());
		} catch(SQLException e) {
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

			return JooqPayments.getPaymentConcepts(connection, offset, limit,
					domainID, parentDomainID);

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
	public List<Cost> getEnterprisesCosts(List<Integer> enterpriseIds) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection();

			return getEnterpriseCosts(connection, enterpriseIds);

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

	private static void deletePaymentConcept(Connection connection,
			Payment payment) throws SQLException {
		PreparedStatement stmt = null;
		try {
			// @formatter:off
			stmt = connection.prepareStatement("DELETE " + " WHERE "
					+ PaymentConceptColumns.ID + " = ? ");
			// @formatter:on

			stmt.setInt(1, payment.getId());

			stmt.executeUpdate();

		} finally {
			if (stmt != null)
				stmt.close();
		}
	}

	private static void updatePaymentConcept(Connection connection,
			Payment payment) throws SQLException {
		PreparedStatement stmt = null;
		try {
			// @formatter:off
			stmt = connection.prepareStatement("UPDATE "
					+ SQLConstants.PAYMENT_CONCEPT + " SET "
					+ PaymentConceptColumns.CODE + " = ? "
					+ PaymentConceptColumns.TYPE + " = ? "
					+ PaymentConceptColumns.DESCRIPTION + " = ? "
					+ PaymentConceptColumns.EXPRESSION + " = ? "
					+ PaymentConceptColumns.IRPF_EXPRESSION + " = ? "
					+ PaymentConceptColumns.QUOTE_EXPRESSION + " = ? "
					+ " WHERE " + PaymentConceptColumns.ID + " = ? ");
			// @formatter:on

			stmt.setString(1, payment.getName());

			Payment.Type type = payment.getType();
			if (type != null)
				stmt.setInt(2, type.ordinal());
			else
				stmt.setNull(2, Types.INTEGER);

			stmt.setString(3, payment.getDescription());
			stmt.setString(4, payment.getExpression());
			stmt.setString(5, payment.getIrpfExpression());
			stmt.setString(6, payment.getQuoteExpression());

			stmt.setInt(7, payment.getId());

			stmt.executeUpdate();

		} finally {
			if (stmt != null)
				stmt.close();
		}
	}

	private static int insertPaymentConcept(Connection connection,
			Payment payment, Integer domainId) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			// @formatter:off
			stmt = connection.prepareStatement("INSERT INTO "
					+ SQLConstants.PAYMENT_CONCEPT + " ( "
					+ PaymentConceptColumns.CODE + ", "
					+ PaymentConceptColumns.TYPE + ", "
					+ PaymentConceptColumns.DESCRIPTION + ", "
					+ PaymentConceptColumns.EXPRESSION + ", "
					+ PaymentConceptColumns.IRPF_EXPRESSION + ", "
					+ PaymentConceptColumns.QUOTE_EXPRESSION + ", "
					+ PaymentConceptColumns.DOMAIN
					+ " VALUES ( ?,?,?,?,?,?,? ) ",
					new String[] { PaymentConceptColumns.ID });
			// @formatter:on

			stmt.setString(1, payment.getName());

			Payment.Type type = payment.getType();
			if (type != null)
				stmt.setInt(2, type.ordinal());
			else
				stmt.setNull(2, Types.INTEGER);

			stmt.setString(3, payment.getDescription());
			stmt.setString(4, payment.getExpression());
			stmt.setString(5, payment.getIrpfExpression());
			stmt.setString(6, payment.getQuoteExpression());

			stmt.setInt(7, domainId);

			stmt.executeUpdate();

			rs = stmt.getGeneratedKeys();
			rs.next();
			return rs.getInt(1);

		} finally {
			if (stmt != null)
				stmt.close();
		}
	}

	private static void updatePayments(Connection connection, Integer domainID,
			Integer oldConceptID, Integer newConceptID) throws SQLException {
		PreparedStatement stmt = null;
		try {
			// @formatter:off
			stmt = connection.prepareStatement("UPDATE "
					+ SQLConstants.SYSTEM_PAYMENT + " SET "
					+ SystemPaymentColumns.PAYMENT_CONCEPT + " = ? "
					+ " WHERE " + SystemPaymentColumns.DOMAIN + " = ? "
					+ " AND " + SystemPaymentColumns.PAYMENT_CONCEPT + " = ? ");
			// @formatter:on

			stmt.setInt(1, newConceptID);
			stmt.setInt(2, domainID);
			stmt.setInt(3, oldConceptID);

			stmt.executeUpdate();
			stmt.close();

			// @formatter:off
			stmt = connection.prepareStatement("UPDATE "
					+ SQLConstants.AGREEMENT_PAYMENT + " SET "
					+ AgreementPaymentColumns.PAYMENT_CONCEPT + " = ? "
					+ " WHERE " + AgreementPaymentColumns.DOMAIN + " = ? "
					+ " AND " + AgreementPaymentColumns.PAYMENT_CONCEPT
					+ " = ? ");
			// @formatter:on

			stmt.setInt(1, newConceptID);
			stmt.setInt(2, domainID);
			stmt.setInt(3, oldConceptID);

			stmt.executeUpdate();
			stmt.close();

			// @formatter:off
			stmt = connection.prepareStatement("UPDATE "
					+ SQLConstants.CONTRACT_PAYMENT + " SET "
					+ ContractPaymentColumns.PAYMENT_CONCEPT + " = ? "
					+ " WHERE " + ContractPaymentColumns.DOMAIN + " = ? "
					+ " AND " + ContractPaymentColumns.PAYMENT_CONCEPT
					+ " = ? ");
			// @formatter:on

			stmt.setInt(1, newConceptID);
			stmt.setInt(2, domainID);
			stmt.setInt(3, oldConceptID);

			stmt.executeUpdate();

		} finally {
			if (stmt != null)
				stmt.close();
		}
	}

	private static void deleteDeductionConcept(Connection connection,
			Deduction deduction) throws SQLException {
		PreparedStatement stmt = null;
		try {
			// @formatter:off
			stmt = connection.prepareStatement("DELETE " + " WHERE "
					+ DeductionConceptColumns.ID + " = ? ");
			// @formatter:on

			stmt.setInt(1, deduction.getId());

			stmt.executeUpdate();

		} finally {
			if (stmt != null)
				stmt.close();
		}
	}

	private static void updateDeductionConcept(Connection connection,
			Deduction deduction) throws SQLException {
		PreparedStatement stmt = null;
		try {
			// @formatter:off
			stmt = connection.prepareStatement("UPDATE "
					+ SQLConstants.DEDUCTION_CONCEPT + " SET "
					+ DeductionConceptColumns.CODE + " = ? "
					+ DeductionConceptColumns.TYPE + " = ? "
					+ DeductionConceptColumns.DESCRIPTION + " = ? "
					+ DeductionConceptColumns.EXPRESSION + " = ? " + " WHERE "
					+ DeductionConceptColumns.ID + " = ? ");
			// @formatter:on

			stmt.setString(1, deduction.getName());

			Deduction.Type type = deduction.getType();
			if (type != null)
				stmt.setInt(2, type.ordinal());
			else
				stmt.setNull(2, Types.INTEGER);

			stmt.setString(3, deduction.getDescription());
			stmt.setString(4, deduction.getExpression());

			stmt.setInt(5, deduction.getId());

			stmt.executeUpdate();
		} finally {
			if (stmt != null)
				stmt.close();
		}
	}

	private static int insertDeductionConcept(Connection connection,
			Deduction deduction, Integer domainId) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			// @formatter:off
			stmt = connection.prepareStatement(
					"INSERT INTO " + SQLConstants.DEDUCTION_CONCEPT + " ( "
							+ DeductionConceptColumns.CODE + ", "
							+ DeductionConceptColumns.TYPE + ", "
							+ DeductionConceptColumns.DESCRIPTION + ", "
							+ DeductionConceptColumns.EXPRESSION + ", "
							+ DeductionConceptColumns.DOMAIN
							+ " VALUES ( ?,?,?,?,? ) ",
					new String[] { DeductionConceptColumns.ID });
			// @formatter:on

			stmt.setString(1, deduction.getName());

			Deduction.Type type = deduction.getType();
			if (type != null)
				stmt.setInt(2, type.ordinal());
			else
				stmt.setNull(2, Types.INTEGER);

			stmt.setString(3, deduction.getDescription());
			stmt.setString(4, deduction.getExpression());
			stmt.setInt(5, domainId);

			stmt.executeUpdate();

			rs = stmt.getGeneratedKeys();
			rs.next();
			return rs.getInt(1);

		} finally {
			if (stmt != null)
				stmt.close();
		}
	}

	private static void updateDeductions(Connection connection,
			Integer domainID, Integer oldConceptID, Integer newConceptID)
			throws SQLException {
		PreparedStatement stmt = null;
		try {
			// @formatter:off
			stmt = connection.prepareStatement("UPDATE "
					+ SQLConstants.SYSTEM_DEDUCTION + " SET "
					+ SystemDeductionColumns.DEDUCTION_CONCEPT + " = ? "
					+ " WHERE " + SystemDeductionColumns.DOMAIN + " = ? "
					+ " AND " + SystemDeductionColumns.DEDUCTION_CONCEPT
					+ " = ? ");
			// @formatter:on

			stmt.setInt(1, newConceptID);
			stmt.setInt(2, domainID);
			stmt.setInt(3, oldConceptID);

			stmt.executeUpdate();
			stmt.close();

			// @formatter:off
			stmt = connection.prepareStatement("UPDATE "
					+ SQLConstants.CONTRACT_DEDUCTION + " SET "
					+ ContractDeductionColumns.DEDUCTION_CONCEPT + " = ? "
					+ " WHERE " + ContractDeductionColumns.DOMAIN + " = ? "
					+ " AND " + ContractDeductionColumns.DEDUCTION_CONCEPT
					+ " = ? ");
			// @formatter:on

			stmt.setInt(1, newConceptID);
			stmt.setInt(2, domainID);
			stmt.setInt(3, oldConceptID);

			stmt.executeUpdate();

		} finally {
			if (stmt != null)
				stmt.close();
		}
	}

	private static void deleteBonusConcept(Connection connection, Bonus bonus)
			throws SQLException {
		PreparedStatement stmt = null;
		try {
			// @formatter:off
			stmt = connection.prepareStatement("DELETE " + " WHERE "
					+ BonusConceptColumns.ID + " = ? ");
			// @formatter:on

			stmt.setInt(1, bonus.getId());

			stmt.executeUpdate();

		} finally {
			if (stmt != null)
				stmt.close();
		}
	}

	private static void updateBonusConcept(Connection connection, Bonus bonus)
			throws SQLException {
		PreparedStatement stmt = null;
		try {
			// @formatter:off
			stmt = connection.prepareStatement("UPDATE "
					+ SQLConstants.BONUS_CONCEPT + " SET "
					+ BonusConceptColumns.TYPE + " = ? "
					+ BonusConceptColumns.DESCRIPTION + " = ? "
					+ BonusConceptColumns.EXPRESSION + " = ? " + " WHERE "
					+ BonusConceptColumns.ID + " = ? ");
			// @formatter:on

			Bonus.Type type = bonus.getType();
			if (type != null)
				stmt.setInt(1, type.ordinal());
			else
				stmt.setNull(1, Types.INTEGER);

			stmt.setString(2, bonus.getDescription());
			stmt.setString(3, bonus.getExpression());

			stmt.setInt(4, bonus.getId());

			stmt.executeUpdate();
		} finally {
			if (stmt != null)
				stmt.close();
		}
	}

	private static int insertBonusConcept(Connection connection, Bonus bonus,
			Integer domainId) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			// @formatter:off
			stmt = connection.prepareStatement("INSERT INTO "
					+ SQLConstants.BONUS_CONCEPT + " ( "
					+ BonusConceptColumns.TYPE + ", "
					+ BonusConceptColumns.DESCRIPTION + ", "
					+ BonusConceptColumns.EXPRESSION + ", "
					+ BonusConceptColumns.DOMAIN + " VALUES ( ?,?,?,? ) ",
					new String[] { DeductionConceptColumns.ID });
			// @formatter:on

			Bonus.Type type = bonus.getType();
			if (type != null)
				stmt.setInt(1, type.ordinal());
			else
				stmt.setNull(1, Types.INTEGER);

			stmt.setString(2, bonus.getDescription());
			stmt.setString(3, bonus.getExpression());
			stmt.setInt(4, domainId);

			stmt.executeUpdate();

			rs = stmt.getGeneratedKeys();
			rs.next();
			return rs.getInt(1);

		} finally {
			if (stmt != null)
				stmt.close();
		}
	}

	private static void updateBonuses(Connection connection, Integer domainID,
			Integer oldConceptID, Integer newConceptID) throws SQLException {
		PreparedStatement stmt = null;
		try {
			// @formatter:off
			stmt = connection.prepareStatement("UPDATE "
					+ SQLConstants.CONTRACT_BONUS + " SET "
					+ ContractBonusColumns.BONUS_CONCEPT + " = ? " + " WHERE "
					+ ContractBonusColumns.DOMAIN + " = ? " + " AND "
					+ ContractBonusColumns.BONUS_CONCEPT + " = ? ");
			// @formatter:on

			stmt.setInt(1, newConceptID);
			stmt.setInt(2, domainID);
			stmt.setInt(3, oldConceptID);

			stmt.executeUpdate();

		} finally {
			if (stmt != null)
				stmt.close();
		}
	}


	private static List<Enterprise> getEnterprises(Connection connection, int userId,
			int domainId, int offset, int limit) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			// @formatter:off
			stmt = connection.prepareStatement(
					"SELECT * FROM "
					+ SQLConstants.ENTERPRISE 
					+ ", " + SQLConstants.REGISTRY
					+ " LEFT JOIN " + SQLConstants.RBANK + " ON (" + SQLConstants.REGISTRY + "."+ RegistryColumns.ID + " = " + SQLConstants.RBANK +"." + RbankColumns.REGISTRY + ")"
					+ ", " + SQLConstants.DOMAIN 
					+ ", " + SQLConstants.ENTERPRISE_ACTIVITY 
					+ ", " + SQLConstants.ENTERPRISE_CCC
					+ ", " + SQLConstants.GEOZONE
					
					+ " WHERE " + SQLConstants.ENTERPRISE +"."+ EnterpriseColumns.REGISTRY + " = " + SQLConstants.REGISTRY + "." + RegistryColumns.ID
					+ " AND " + SQLConstants.ENTERPRISE + "." + EnterpriseColumns.DOMAIN + " = " + SQLConstants.DOMAIN + "." + DomainColumns.ID 

					+ " AND " + SQLConstants.ENTERPRISE + "." + EnterpriseColumns.REGISTRY + " = " + SQLConstants.ENTERPRISE_ACTIVITY+ "." + EnterpriseActivityColumns.ENTERPRISE
					+ " AND " + SQLConstants.ENTERPRISE_ACTIVITY + "." + EnterpriseActivityColumns.ID + " = " + SQLConstants.ENTERPRISE_CCC+ "." + EnterpriseCccColumns.ENTERPRISE_ACTIVITY
					+ " AND " + SQLConstants.ENTERPRISE_CCC + "." + EnterpriseCccColumns.GEOZONE + " = " + SQLConstants.GEOZONE+ "." + GeozoneColumns.ID

					+ " AND ( " + SQLConstants.DOMAIN + "." + DomainColumns.ID + " = ? " 
						+ " OR " + SQLConstants.DOMAIN + "." + DomainColumns.PARENT + " = ? " + ")"
					
					+" AND (" + SQLConstants.DOMAIN + "." + DomainColumns.SCOPE 
						+ " IN ("
						+ " SELECT " + UserScopeColumns.SCOPE + " FROM " + SQLConstants.USER_SCOPE + " WHERE " + UserScopeColumns.USER_ID + " =  ? "
//						+ " UNION SELECT " + SQLConstants.SCOPE + "." + ScopeColumns.ID + " FROM " + SQLConstants.SCOPE 
//							+ " INNER JOIN " + SQLConstants.DOMAIN + " ON ( " + SQLConstants.DOMAIN + "." + DomainColumns.ID + " = " + SQLConstants.SCOPE + "." + ScopeColumns.DOMAIN + ")"     
//							+ " INNER JOIN " + SQLConstants.USER + " ON ( " + SQLConstants.DOMAIN + "." + DomainColumns.PARENT + " = " + SQLConstants.USER + "." + UserColumns.DOMAIN + ")"     
						+ " )"
						+ " OR " + SQLConstants.DOMAIN + "." + DomainColumns.SCOPE + " IS NULL"
						+ " )"     
					
					+ " AND " + SQLConstants.DOMAIN + "." + DomainColumns.ACTIVE + " = 1 " 
					
					+ " ORDER BY " + SQLConstants.REGISTRY + "." + RegistryColumns.ID
					+ ", " + SQLConstants.ENTERPRISE_ACTIVITY + "." + EnterpriseActivityColumns.ID
					+ ", " + SQLConstants.ENTERPRISE_CCC + "." + EnterpriseCccColumns.ID
					+ " LIMIT ?, ?"
					);
			// @formatter:on

			stmt.setInt(1, domainId);
			stmt.setInt(2, domainId);
			stmt.setInt(3, userId);

			stmt.setInt(4, offset);
			stmt.setInt(5, limit);
			
			CCC ccc = null;
			Activity activity = null;
			Enterprise enterprise = null;

			
			rs = stmt.executeQuery();
			
			List<Enterprise> enterprises = new LinkedList<Enterprise>();
			while (rs.next()) {
				Integer registry = rs.getInt(SQLConstants.ENTERPRISE +"." + EnterpriseColumns.REGISTRY);
				
				if ( enterprise == null || !enterprise.getId().equals(registry) ) {
					enterprise = new Enterprise();
					enterprise.setId(registry); // Not
//					enterprise.setName(rs.getString(SQLConstants.REGISTRY +"." + RegistryColumns.NAME));
					enterprise.setName(rs.getString(SQLConstants.DOMAIN +"." + DomainColumns.DESCRIPTION));
					enterprise.setDomain(rs.getInt(SQLConstants.ENTERPRISE +"." + EnterpriseColumns.DOMAIN));
					enterprises.add(enterprise);
				}
				
				Integer rbankId = (Integer) rs.getObject(SQLConstants.RBANK +"."+RbankColumns.ID);
				if ( rbankId != null && enterprise.getBankAccounts().stream().noneMatch(a->a.getId().equals(rbankId)) ){
					BankAccount bankAccount = new BankAccount();
					bankAccount.setId(rbankId);
					bankAccount.setBic(rs.getString(SQLConstants.RBANK+"."+RbankColumns.BIC));
					bankAccount.setAlias(rs.getString(SQLConstants.RBANK+"."+RbankColumns.ALIAS));
					bankAccount.setAccount(rs.getString(SQLConstants.RBANK+"."+RbankColumns.BANK_ACCOUNT));
					enterprise.addBankAccount(bankAccount);
				}

				Integer activityId = (Integer) rs.getObject(SQLConstants.ENTERPRISE_ACTIVITY +"."+EnterpriseActivityColumns.ID);
				if ( activityId == null )
					continue;
				
				if ( activity == null || !activity.getId().equals(activityId) ){
					activity = new Activity();
					activity.setId(activityId);
					activity.setCnae2009(rs.getInt(SQLConstants.ENTERPRISE_ACTIVITY +"."+EnterpriseActivityColumns.CNAE));
					activity.setDescription(rs.getString(SQLConstants.ENTERPRISE_ACTIVITY +"."+EnterpriseActivityColumns.DESCRIPTION));
					enterprise.addActivity(activity);
				}
				
				Integer cccId = (Integer) rs.getObject(SQLConstants.ENTERPRISE_CCC +"."+EnterpriseCccColumns.ID);
				if ( cccId == null )
					continue;

				if ( ccc == null || !ccc.getId().equals(cccId) ){
					ccc = new CCC();
					ccc.setId( cccId );
					ccc.setCode(rs.getString(SQLConstants.ENTERPRISE_CCC +"."+EnterpriseCccColumns.CCC));
					ccc.setGeozone(rs.getString(SQLConstants.GEOZONE +"."+GeozoneColumns.CODE));
					activity.addCcc(ccc);
				}
			}

			return enterprises;

		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();

		}

	}

	private static List<Bonus> getBonusConcepts(Connection connection,
			int offset, int limit, Integer domainID, Integer parentDomainID)
			throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			// @formatter:off
			stmt = connection.prepareStatement("SELECT *"
					+ " FROM "
					+ SQLConstants.BONUS_CONCEPT
					+ " WHERE "
					+ BonusConceptColumns.DOMAIN
					+ " =  ? "
					+ " OR "
					+ BonusConceptColumns.DOMAIN
					+ " = ? "
					+ (parentDomainID != null ? " OR "
							+ BonusConceptColumns.DOMAIN + " = ? " : "")
					+ " ORDER BY " + BonusConceptColumns.DESCRIPTION);
			// @formatter:on

			stmt.setInt(1, domainID);
			stmt.setInt(2, SQLPayrollConstants.DOMAIN_ZERO);
			if (parentDomainID != null)
				stmt.setInt(3, parentDomainID);

			List<Bonus> bonuses = new LinkedList<Bonus>();

			rs = stmt.executeQuery();
			while (rs.next()) {
				Bonus bonus = new Bonus();

				bonus.setId(rs.getInt(BonusConceptColumns.ID)); // not null
				bonus.setDomain(rs.getInt(BonusConceptColumns.DOMAIN));
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

	private static List<Deduction> getDeductionConcepts(Connection connection,
			int offset, int limit, Integer domainID, Integer parentDomainID)
			throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			// @formatter:off
			stmt = connection.prepareStatement("SELECT *"
					+ " FROM "
					+ SQLConstants.DEDUCTION_CONCEPT
					+ " WHERE "
					+ DeductionConceptColumns.DOMAIN
					+ " =  ? "
					+ " OR "
					+ DeductionConceptColumns.DOMAIN
					+ " = ? "
					+ (parentDomainID != null ? " OR "
							+ DeductionConceptColumns.DOMAIN + " = ? " : "")
					+ " ORDER BY " + DeductionConceptColumns.DESCRIPTION);
			// @formatter:on

			stmt.setInt(1, domainID);
			stmt.setInt(2, SQLPayrollConstants.DOMAIN_ZERO);
			if (parentDomainID != null)
				stmt.setInt(3, parentDomainID);

			List<Deduction> deductions = new LinkedList<Deduction>();

			rs = stmt.executeQuery();
			while (rs.next()) {
				Deduction deduction = new Deduction();

				deduction.setId(rs.getInt(DeductionConceptColumns.ID));
				deduction.setDomain(rs.getInt(DeductionConceptColumns.DOMAIN));
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

	private static List<Cost> getEnterpriseCosts(Connection connection,
			List<Integer> enterpriseIds) throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			String yearCol = "YEAR";
			String monthCol = "MONTH";
			String salariesCol = "SALARIES";
			String extrasCol = "EXTRAS";
			String settlesCol = "SETTLES";
			String delaysCol = "DELAYS";

			;

			// We asume that one enterprise one domain. This way SELECT it's
			// more clear.
			// @formatter:off
			String sql = "SELECT" + " MONTH("
					+ SALARY
					+ "."
					+ SalaryColumns.CHARGE_DATE
					+ ") "
					+ monthCol
					+ ", YEAR("
					+ SALARY
					+ "."
					+ SalaryColumns.CHARGE_DATE
					+ ") "
					+ yearCol
					+ ", COUNT( IF("
					+ SALARY
					+ "."
					+ SalaryColumns.TYPE
					+ "= ? "
					+ ",1,NULL)) "
					+ salariesCol
					+ ", COUNT( IF("
					+ SALARY
					+ "."
					+ SalaryColumns.TYPE
					+ "= ? "
					+ ",1,NULL)) "
					+ extrasCol
					+ ", COUNT( IF("
					+ SALARY
					+ "."
					+ SalaryColumns.TYPE
					+ "= ? "
					+ ",1,NULL)) "
					+ settlesCol
					+ ", COUNT( IF("
					+ SALARY
					+ "."
					+ SalaryColumns.TYPE
					+ "= ? "
					+ ",1,NULL)) "
					+ delaysCol
					+ " FROM "
					+ ENTERPRISE
					+ ", "
					+ SALARY
					+ " WHERE "
					+ ENTERPRISE
					+ "."
					+ EnterpriseColumns.DOMAIN
					+ " = "
					+ SALARY
					+ "."
					+ SalaryColumns.DOMAIN
					+ (enterpriseIds.size() == 0 ? "" : " AND "
							+ ENTERPRISE
							+ "."
							+ EnterpriseColumns.REGISTRY
							+ " IN ( "
							+ StringUtils.reduce(Collections.nCopies(
									enterpriseIds.size(), "?"), ", ") + " )")
					+ " GROUP BY 1, 2" + " ORDER BY 2 , 1 ASC ";
			// @formatter:on

			stmt = connection.prepareStatement(sql);
			int parameterIndex = 1;
			stmt.setInt(parameterIndex++, SalaryType.SALARY.ordinal());
			stmt.setInt(parameterIndex++, SalaryType.EXTRA.ordinal());
			stmt.setInt(parameterIndex++, SalaryType.SETTLE.ordinal());
			stmt.setInt(parameterIndex++, SalaryType.DELAY.ordinal());

			for (Integer enterpriseId : enterpriseIds) {
				stmt.setInt(parameterIndex++, enterpriseId);
			}

			rs = stmt.executeQuery();

			List<Cost> costs = new LinkedList<Cost>();
			while (rs.next()) {

				int month = rs.getInt(monthCol);
				// MySQL MONTH(date) function returns the month for date,
				// in the range 1 to 12 for January to December, or 0 for
				// dates such as '0000-00-00' or '2008-00-00' that have a zero
				// month part.
				if (month == 0) {
					continue;
				}

				Cost cost = new Cost();
				int year = rs.getInt(yearCol);

				cost.setYear(year);
				cost.setMonth(month - 1); // MONTH (date) returns the month for date, in the range 1 to 12 
				cost.setSalariesCount(rs.getInt(salariesCol));
				cost.setExtrasCount(rs.getInt(extrasCol));
				cost.setSettlesCount(rs.getInt(settlesCol));
				cost.setDelaysCount(rs.getInt(delaysCol));

				costs.add(cost);
			}

			return costs;

		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
	}
	
	@Override
	public void moveAgreement2Parent(Agreement agreement) {
		Connection connection = null;
		try {
			initFacesContext();
			connection = AonServletUtils.getConnection();
			Integer parentDomainID = getParentDomainID();
			JooqAgreement.moveAgreement2ParentDomain(connection, parentDomainID, agreement.getId());
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
	public Integer getParentDomain() {		
		try {
			initFacesContext();			
			return getParentDomainID();
		} finally {
			releaseFacesContext();
		}
	}
	
	
}
