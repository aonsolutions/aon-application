package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.payroll.sql.SQLConstants.CONTRACT;
import static com.esferalia.aon.payroll.sql.SQLConstants.ENTERPRISE;
import static com.esferalia.aon.payroll.sql.SQLConstants.SALARY;
import static com.esferalia.aon.watson.util.AonStringUtils.equalsIgnoreCase;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;

import org.jooq.tools.json.JSONObject;

import com.esferalia.aon.google.sql.SQLConstants.PersonColumns;
import com.esferalia.aon.google.sql.SQLConstants.UserScopeColumns;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.client.EnterprisesService;
import com.esferalia.aon.gwt.payroll.jooq.JooqActivity;
import com.esferalia.aon.gwt.payroll.jooq.JooqAgrarian;
import com.esferalia.aon.gwt.payroll.jooq.JooqAgreement;
import com.esferalia.aon.gwt.payroll.jooq.JooqCRA;
import com.esferalia.aon.gwt.payroll.jooq.JooqContrataContract;
import com.esferalia.aon.gwt.payroll.jooq.JooqEmployeeAFI;
import com.esferalia.aon.gwt.payroll.jooq.JooqEmployeePeculiarities;
import com.esferalia.aon.gwt.payroll.jooq.JooqEnterprise;
import com.esferalia.aon.gwt.payroll.jooq.JooqIT;
import com.esferalia.aon.gwt.payroll.jooq.JooqMail;
import com.esferalia.aon.gwt.payroll.jooq.JooqPayments;
import com.esferalia.aon.gwt.payroll.jooq.JooqSSBonus;
import com.esferalia.aon.gwt.payroll.jooq.JooqWorkplace;
import com.esferalia.aon.gwt.payroll.shared.AFIChanges;
import com.esferalia.aon.gwt.payroll.shared.ActivitiesCCC;
import com.esferalia.aon.gwt.payroll.shared.Activity;
import com.esferalia.aon.gwt.payroll.shared.ActivityInfo;
import com.esferalia.aon.gwt.payroll.shared.AgrarianJourney;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.BankAccount;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.CCC;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.CNO;
import com.esferalia.aon.gwt.payroll.shared.CRA;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.ContractAttach;
import com.esferalia.aon.gwt.payroll.shared.ContractClause;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseInfo;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseStatus;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseStatus.AndEnterpriseStatus;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.esferalia.aon.gwt.payroll.shared.OutOfDateException;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Peculiarities;
import com.esferalia.aon.gwt.payroll.shared.SSBonusData;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceInfo;
import com.esferalia.aon.gwt.payroll.sql.SQLUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.security.Certificate;
import com.esferalia.aon.occam.api.model.security.CertificateNotFoundException;
import com.esferalia.aon.payroll.calculator.sql.SQLPayrollConstants;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementPaymentColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.BonusConceptColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractBonusColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
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
import com.esferalia.aon.payroll.sql.SQLConstants.SystemDeductionColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SystemPaymentColumns;
import com.esferalia.aon.payroll.tgss.cra.Cra;
import com.esferalia.aon.payroll.tgss.cra.MainCRAGenerator;
import com.esferalia.aon.salary.enumeration.SalaryType;

import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.exceptions.ForbiddenException;
import solutions.aon.seg.social.exceptions.SegSocialException;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
@WebServlet(
		name = "EnterprisesGWTServlet", 
		urlPatterns = { 
				"/aon_gwt_aio/enterprises",
				"/aon_gwt_payroll/enterprises"
		})
public class EnterprisesServiceImpl extends AonRemoteServiceServlet implements
		EnterprisesService {

	@Override
	public Integer getDomain(String domain) {
		try {
			return AonServletUtils.getDomainID(domain);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public ContextDescriptor getContext(String domain) {
		SalaryDraft draft = new SalaryDraft();

		draft.setStartDate(DateUtils.getFirstDayOfMonth());
		draft.setEndDate(DateUtils.getLastDayOfMonth());
		draft.setIssueDate(draft.getEndDate());
		draft.setChargeDate(draft.getEndDate());
		Employee employee = new Employee();
		employee.setId(-1);
		draft.setEmployee(employee);
		draft.setType(Type.SALARY);

		return EmployeesServiceImpl.getDraftContext(domain, draft);
	}

	@Override
	public Bonus saveBonusConcept(String domain, Bonus bonus) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);

			Integer domainID = AonServletUtils.getDomainID(domain);

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
		}
	}

	@Override
	public Payment savePaymentConcept(String domain, Payment payment) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);

			Integer domainID = AonServletUtils.getDomainID(domain);

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
		}
	}

	@Override
	public Deduction saveDeductionConcept(String domain, Deduction deduction) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);

			Integer domainID = AonServletUtils.getDomainID(domain);

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
		}
	}

	@Override
	public void deleteBonusConcept(String domain, Bonus bonus) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);

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
	public void deleteDeductionConcept(String domain, Deduction deduction) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);

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
	public void deletePaymentConcept(String domain, Payment payment) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);

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
	public void deleteAgreement(String domain, Agreement agreement) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			Integer domainId = AonServletUtils.getDomainID(domain);
			JooqAgreement.deleteAgreement(connection, domainId, agreement);

		} catch(SQLException e) {
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
	public List<Enterprise> getEnterprises(String domain, String user, int offset, int limit) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			Integer domainId = AonServletUtils.getDomainID(domain);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domain);
			Integer userId = AonServletUtils.getUserID(connection, user, domainId, parentDomainId);
			return getEnterprises(connection, userId, domainId, offset, limit);

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
	public List<Extra> getWorkplacesExtras(String domain, List<Integer> workplaceIds) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);

			return JooqAgreement.getWorkplacesExtras(connection, workplaceIds.toArray(new Integer[workplaceIds.size()]));

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
	public List<Agreement> getAgreements(String domain, int offset, int limit) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			Integer domainID = AonServletUtils.getDomainID(domain);
			Integer parentDomainID = AonServletUtils.getParentDomainID(domain);

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
		}
	}
	
	@Override
	public Agreement getAgreement(String domain, Integer agreementId) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			Integer domainID = AonServletUtils.getDomainID(domain);

			return JooqAgreement.getAgreement(connection, agreementId);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} catch (NullPointerException e) {
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
	public void updateAgreementId(String domain, Agreement agreement) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			JooqAgreement.trashRestoreAgreement(connection, agreement.getId(), true);
		} catch(SQLException e) {
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
	public Agreement copyAgreement(String domain, Agreement agreement) {
		
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);						
			return JooqAgreement.copyAgreement(connection, getDomain(domain), agreement.getId());
		} catch(SQLException e) {
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
	public List<Bonus> getBonusConcepts(String domain, int offset, int limit) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			Integer domainID = AonServletUtils.getDomainID(domain);
			Integer parentDomainID = AonServletUtils.getParentDomainID(domain);

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
		}
	}

	@Override
	public List<Deduction> getDeductionConcepts(String domain, int offset, int limit) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			Integer domainID = AonServletUtils.getDomainID(domain);
			Integer parentDomainID = AonServletUtils.getParentDomainID(domain);

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
		}
	}

	@Override
	public List<Payment> getPaymentConcepts(String domain, int offset, int limit) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			Integer domainID = AonServletUtils.getDomainID(domain);
			Integer parentDomainID = AonServletUtils.getParentDomainID(domain);

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
		}
	}

	@Override
	public List<Cost> getEnterprisesCosts(String domain, List<Integer> enterpriseIds) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);

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

	@Override
	public List<Employee> getCCCEmployees(String domain, java.util.Date month, List<Integer> cccIds) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);

			return getCCCEmployees(connection, new java.sql.Date(month.getTime()), cccIds);

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
					+ " LEFT JOIN " + SQLConstants.SALARY + " ON (" 
					+ SQLConstants.ENTERPRISE_CCC + "." + EnterpriseCccColumns.CCC + " = " + SQLConstants.SALARY+ "." + SalaryColumns.CCC  
					+ " AND " + SQLConstants.ENTERPRISE_CCC + "." + EnterpriseCccColumns.DOMAIN + " = " + SQLConstants.SALARY+ "." + SalaryColumns.DOMAIN  
//					+ " AND " + SQLConstants.SALARY + "." + SalaryColumns.TYPE + " IN ( " + Salary.Type.SALARY.ordinal() + ")" 
					+ ")"
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
						+ " )"
						+ " OR " + SQLConstants.DOMAIN + "." + DomainColumns.SCOPE + " IS NULL"
						+ " )"     
					
					+ " AND " + SQLConstants.DOMAIN + "." + DomainColumns.ACTIVE + " = 1 " 

					+ " ORDER BY " + SQLConstants.REGISTRY + "." + RegistryColumns.ID
					+ ", " + SQLConstants.ENTERPRISE_ACTIVITY + "." + EnterpriseActivityColumns.ID
					+ ", " + SQLConstants.ENTERPRISE_CCC + "." + EnterpriseCccColumns.ID
					+ ", " + SQLConstants.SALARY + "." + SalaryColumns.SOCIAL_SECURITY_NUMBER
					+ ", " + SQLConstants.SALARY + "." + SalaryColumns.START_DATE
					+ " LIMIT ?, ?"
					);
			// @formatter:on
			int i = 1;
			stmt.setInt(i++, domainId);
			stmt.setInt(i++, domainId);
			stmt.setInt(i++, userId);
			
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MONTH, -3);
			calendar.set(Calendar.DAY_OF_MONTH,1);
			calendar.add(Calendar.DAY_OF_MONTH,-1);
//			stmt.setDate(i++, new java.sql.Date(calendar.getTimeInMillis()));

			stmt.setInt(i++, offset);
			stmt.setInt(i++, limit);
			
			CCC ccc = null;
			Employee employee = null;
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
					ccc.setRegime(getSSRegime(rs.getInt(SQLConstants.ENTERPRISE_CCC +"."+EnterpriseCccColumns.TYPE)).getCode());
					ccc.setType(rs.getByte(SQLConstants.ENTERPRISE_CCC +"."+EnterpriseCccColumns.TYPE));
					
					activity.addCcc(ccc);
				}

				String employeeSS = (String) rs.getObject(SQLConstants.SALARY +"."+SalaryColumns.SOCIAL_SECURITY_NUMBER);
				if ( employeeSS == null )
					continue;
				Date startDate =  (Date) rs.getObject(SQLConstants.SALARY +"."+SalaryColumns.START_DATE);
				Integer employeeId = (employeeSS + ""  + startDate).hashCode();
				if ( employee == null || !employee.getId().equals(employeeId)) {
					employee = new Employee();
					employee.setId(employeeId.hashCode());
					employee.setSocialSecurity(employeeSS);
					employee.setStartDate(startDate);
					employee.setEndDate(rs.getDate(SQLConstants.SALARY +"."+SalaryColumns.END_DATE));
					employee.setName(rs.getString(SQLConstants.SALARY +"."+SalaryColumns.EMPLOYEE_NAME));
					employee.setDocument(rs.getString(SQLConstants.SALARY +"."+SalaryColumns.EMPLOYEE_DOCUMENT));
					employee.setSeniorityDate(rs.getDate(SQLConstants.SALARY +"."+SalaryColumns.SENIORITY_DATE));
					
					ccc.addEmployee(employee);
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

	private static List<Employee> getCCCEmployees(Connection connection,
			Date month,List<Integer> cccIds) throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {

			// We asume that one enterprise one domain. This way SELECT it's
			// more clear.
			// @formatter:off
			String sql = "SELECT * "
					+ " FROM "
					+ CONTRACT
					+ " INNER JOIN " + SQLConstants.PERSON + " ON (" + CONTRACT + "." + ContractColumns.PERSON  + " = " + SQLConstants.PERSON + "." + PersonColumns.REGISTRY + ")"
					+ " WHERE "
					+ " " + CONTRACT + "." + ContractColumns.START_DATE + " <=  ?"  
					+ " AND (" + CONTRACT + "." + ContractColumns.END_DATE + " >=  ? "
					+ " OR " + CONTRACT + "." + ContractColumns.END_DATE + " IS NULL "
					+ " )"
					+ " AND " + CONTRACT + "." + ContractColumns.ENTERPRISE_CCC + " IN ( " + StringUtils.reduce(Collections.nCopies( cccIds.size(), "?"), ", ") + " )"
					;
			// @formatter:on

			stmt = connection.prepareStatement(sql);
			int parameterIndex = 1;
			stmt.setDate(parameterIndex++, month);
			stmt.setDate(parameterIndex++, month);

			for (Integer cccId : cccIds) {
				stmt.setInt(parameterIndex++, cccId);
			}

			rs = stmt.executeQuery();

			List<Employee> employees = new LinkedList<Employee>();
			while (rs.next()) {
				
				Employee employee = new Employee();
				employee.setId(rs.getInt(CONTRACT + "." + ContractColumns.ID));
				employee.setStartDate(rs.getDate(CONTRACT + "." + ContractColumns.START_DATE));
				employee.setEndDate(rs.getDate(CONTRACT + "." + ContractColumns.END_DATE));
				employee.setSeniorityDate(rs.getDate(CONTRACT + "." + ContractColumns.SENIORITY_DATE));

				employee.setName(rs.getString(SQLConstants.PERSON +"." + PersonColumns.NAME));
				employee.setFirstSurname(rs.getString(SQLConstants.PERSON +"." + PersonColumns.FIRST_SURNAME));
				employee.setSecondSurName(rs.getString(SQLConstants.PERSON +"." + PersonColumns.SECOND_SURNAME));
				
				employee.setSocialSecurity(rs.getString(SQLConstants.PERSON +"." + PersonColumns.SOCIAL_SECURITY_NUM));
				
				employees.add(employee);
			}

			return employees;

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
	public void moveAgreement2Parent(String domain, Agreement agreement) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			Integer parentDomainID = AonServletUtils.getParentDomainID(domain);
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
		}
	
	}
	
	@Override
	public Integer getParentDomain(String domain) {		
		try {
			return AonServletUtils.getParentDomainID(domain);
		} catch ( SQLException e ) {
			throw new IllegalArgumentException(e);			
		}
	}
	
	
	public static SSRegimeType getSSRegime( int cccType ) {
		Map<CCCType, SSRegimeType> regimes = new HashMap<CCCType, SSRegimeType>(){
			{
				put(CCCType.AGRICULTURAL, SSRegimeType.AGRICULTURAL);
				put(CCCType.ARTIST, SSRegimeType.ARTIST);
				put(CCCType.HOME_EMPLOYEES, SSRegimeType.DOMESTIC_EMPLOYEES);
			}
		};
		
		try {
			return regimes.getOrDefault(CCCType.values()[cccType], SSRegimeType.GENERAL);
		} catch ( Throwable t){
			return SSRegimeType.GENERAL;
		}
	}

	@Override
	public WorkplaceInfo getWorkplaceInfo(String domain, Integer workplaceId) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			return JooqWorkplace.getWorkplaceInfo(connection, workplaceId);
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
	public WorkplaceInfo setWorkplaceInfo(String domain, WorkplaceInfo workplaceInfo) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			return JooqWorkplace.setWorkplaceInfo(connection, workplaceInfo);
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
	public List<Workplace> getWorkplaces(Workplace workplace, String domainName) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domainName);
			Integer domainId = AonServletUtils.getDomainID(domainName);
			return JooqWorkplace.getWorkplaces(workplace, domainId, connection);
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
	public ActivitiesCCC getActivitiesCCC(Workplace workplace, String domainName) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domainName);
			Integer domainId = AonServletUtils.getDomainID(domainName);
			return JooqWorkplace.getActivitiesCCC(workplace, domainId, connection);
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
	public ActivityInfo getActivityInfoDataBase(Integer activityId, String domain) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			return JooqActivity.getActivity(activityId, connection);
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
	public ActivityInfo updateActivityInfoDataBase(ActivityInfo activityInfo, String domain) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			return JooqActivity.updateActivity(activityInfo, connection);
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
	public ActivityInfo createActivityInfoDataBase(ActivityInfo activityInfo, String domain) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			return JooqActivity.createActivity(activityInfo, connection);
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
	public Map<String, String> getCNAE2009(String domain) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			return JooqActivity.getCNAE2009(connection);
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
	public Map<Integer, String> getEnterpiseAddresses(Integer enterpriseId, String domain) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			return JooqEnterprise.getEnterpriseAddresses(connection, enterpriseId);
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
	public Map<Integer, String> getEnterpiseCalendars(Integer enterpriseId, String domain) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			return JooqEnterprise.getEnterpriseCalendars(connection, enterpriseId);
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
	public Map<Integer, String> getEnterpiseActivities(Integer enterpriseId, String domain) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			return JooqEnterprise.getEnterpriseActivities(connection, enterpriseId);
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
	public WorkplaceInfo createWorkplaceInfo(WorkplaceInfo workplaceInfo, Integer enterpriseId, String domain) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			return JooqWorkplace.createWorkplace(connection, workplaceInfo, enterpriseId);
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
	public Map<Integer, String> getEnterpiseScopes(Integer enterpriseId, String domain) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			return JooqEnterprise.getEnterpriseScopes(connection, enterpriseId);
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
	public EnterpriseInfo getEnterpriseInfo(Integer enterpriseId, String domain) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			return JooqEnterprise.getEnterpriseInfo(connection, enterpriseId);
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
	public EnterpriseInfo updateEnterprise(EnterpriseInfo enterpriseInfo, String domain) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			return JooqEnterprise.setEnterpriseInfo(connection, enterpriseInfo);
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
	public Map<Integer, List<AgrarianJourney>> getAgrarianJourney(long findingDate, List<String> cccList, String domain) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			return JooqAgrarian.getAgrarianJourney(findingDate, cccList, connection);
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
	public List<CRA> getCRAs(String domain, String user) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			Integer domainId = AonServletUtils.getDomainID(domain);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domain);
			Integer userId = AonServletUtils.getUserID(connection, user, domainId, parentDomainId);
			return JooqCRA.getDomainCRAs(domainId, parentDomainId, userId, connection);
//			return JooqCRA.getDomainCRAs(getDomain(domainStr), connection);
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
	public String createNewCRA(String domainName, long findingDate, List<String> cccList, ArrayList<Integer> cccIdList, Integer cccId, String craType) {
		Connection connection = null;
		
		try {
			connection = AonServletUtils.getConnection(domainName);
			
			JSONObject mainCRAJSON = Cra.getMainCRAByCRA(cccList, findingDate, connection);
			String agrarianAFI = MainCRAGenerator.generateMainCRA(mainCRAJSON);
			
			return JooqCRA.setMainCra(domainName, cccId.toString(), cccList, cccIdList, agrarianAFI, findingDate, craType, connection);
			
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
	public String deleteCRA(String domainName, Integer craBatchId) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domainName);
			return JooqCRA.deleteMainCRA(craBatchId, connection);
			
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
	public Peculiarities getEmployeePeculiarities(String domain, Integer contractId) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			return JooqEmployeePeculiarities.getPeculiarities(domain, contractId, connection);
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
	public String setEmployeePeculiarities(String domain, Integer contractId, Peculiarities peculiarities) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			return JooqEmployeePeculiarities.setPeculiarities(domain, contractId, peculiarities, connection);
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
	public List<SSBonusData> getEmployeeSSBonuses(String currentDomainName, Integer contractId) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(currentDomainName);
			return JooqSSBonus.getSSBonus(connection, contractId);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if ( connection != null ) {
				try {
					connection.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	@Override
	public List<SSBonusData> getBonusConcepts(String currentDomainName) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(currentDomainName);
			return JooqSSBonus.getExistingSSBonus(connection);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if ( connection != null ) {
				try {
					connection.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	@Override
	public List<SSBonusData> setEmployeeSSBonuses(String currentDomainName, Integer contractId, List<SSBonusData> ssBonuses) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(currentDomainName);
			return JooqSSBonus.setSSBonus(connection, contractId, ssBonuses);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if ( connection != null ) {
				try {
					connection.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	@Override
	public String setEmployeeAFIChanges(String currentDomainName, Integer contractId, AFIChanges afiChangesMap) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(currentDomainName);
			return JooqEmployeeAFI.setEmployeeAFI(connection, contractId, afiChangesMap);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if ( connection != null ) {
				try {
					connection.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	@Override
	public AFIChanges getEmployeeAFIChanges(String currentDomainName, Integer contractId) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(currentDomainName);
			return JooqEmployeeAFI.getEmployeeAFI(connection, contractId);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if ( connection != null ) {
				try {
					connection.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	@Override
	public List<MailAccount> getDomainMailAccounts(String currentDomainName, String currentUser) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(currentDomainName);
			Integer domainId = AonServletUtils.getDomainID(currentDomainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(currentDomainName);
			Integer userId = AonServletUtils.getUserID(connection, currentUser, domainId, parentDomainId);
			return JooqMail.getMailAccounts(connection, userId, domainId);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if ( connection != null ) {
				try {
					connection.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	@Override
	public String getPayrollEmailSendTo(String currentDomainName, Integer enterpriseID) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(currentDomainName);
			return JooqMail.getPayrollEmailSendTo(connection, enterpriseID);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if ( connection != null ) {
				try {
					connection.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	@Override
	public String getPayrollEmailBody(String currentDomainName, String paramsBase64) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(currentDomainName);
			return JooqMail.getPayrollEmailBody(connection, paramsBase64);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if ( connection != null ) {
				try {
					connection.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	@Override
	public String sendPayrollEmail(String currentDomainName, String from, String to, String cc, String cco, String bodyHTML) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(currentDomainName);
			return JooqMail.sendPayrollEmail(connection, from, to, cc, cco, bodyHTML);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if ( connection != null ) {
				try {
					connection.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	@Override
	public String checkEmployeesEmails(String currentDomainName, ArrayList<Integer> salaryIds) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(currentDomainName);
			return JooqMail.checkEmployeesEmails(connection, salaryIds);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if ( connection != null ) {
				try {
					connection.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	@Override
	public String sendPayrollEmailToEmployees(String currentDomainName, String from, String cc, String cco,
			String bodyHTML, String completeURL) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(currentDomainName);
			return JooqMail.sendPayrollEmailToEmployees(connection, from, cc, cco, bodyHTML, completeURL);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if ( connection != null ) {
				try {
					connection.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	@Override
	public String checkCreateNewCRA(String currentDomainName, long findingDate, ArrayList<Integer> cccList) {
		try(Connection connection = AonServletUtils.getConnection(currentDomainName)){
			return JooqCRA.checkCreateNewCRA(connection, findingDate, cccList);
		}catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} 
	}

	@Override
	public List<CCCInfo> getEnterprisesCCCInfo(String domain, String user, long findPeriodTime) {
		try(Connection connection = AonServletUtils.getConnection(domain)) {
			Integer domainId = AonServletUtils.getDomainID(domain);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domain);
			Integer userId = AonServletUtils.getUserID(connection, user, domainId, parentDomainId);
			return JooqEnterprise.getEnterprisesCCCInfo(connection, userId, domainId, parentDomainId, findPeriodTime);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<EmployeeContractInfo> getEmployeesInfo(String domainName, Boolean allEmployees) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			return JooqContrataContract.getEmployeesInfo(connection, domainId, allEmployees);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<ITEmployee> getEmployeesITInfo(String domainName, Boolean allEmployees) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			return JooqIT.getEmployeesITInfo(connection, domainId, allEmployees);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String deleteIT(String domainName, Integer itId) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			return JooqIT.deleteIT(connection, domainId, itId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String createUpdateITEmployee(String domainName, ITEmployee employeeITInfo) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			return JooqIT.createUpdateITEmployee(connection, domainId, employeeITInfo);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public EnterpriseStatus getEnterpriseStatus(String domainName, String userLogin, Integer enterpriseId) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			
			
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId );
			
			List<CCC> cccs = getEnterprises(connection, userId, domainId, 0, Short.MAX_VALUE).stream()
			.filter(e -> e.getId().equals(enterpriseId))
			.flatMap(e -> e.getActivities().stream() )
			.flatMap(a -> a.getCccs().stream())
			.collect(Collectors.toList());
			
			List<Integer> cccIds = cccs.stream().map( ccc-> ccc.getId() ).collect(Collectors.toList());
			
			Date today = new Date(System.currentTimeMillis()); //TODO:  TimeoOne ????
			List<Employee> aonEmployees = getCCCEmployees(connection, today, cccIds);
			aonEmployees.forEach(e -> System.out.println(e.getName() + " : " + e.getStartDate() + "..." + e.getEndDate() ));

			AndEnterpriseStatus enterpriseStatus = new AndEnterpriseStatus();
			
			for ( CCC ccc: cccs ) {
				Collection<solutions.aon.seg.social.objects.Employee> ssEmployees = 
				SistemaRED.getEmployees(certificate.getCertificate(), certificate.getPassword(), certificate.getType(), ccc.getRegime(), ccc.getCode());
				
				for ( solutions.aon.seg.social.objects.Employee ssEmployee :  ssEmployees) {
					
					
					String dni = ssEmployee.getIpf();
					String naf = ssEmployee.getNss();
					java.util.Date date = ssEmployee.getFra();
					String name = ssEmployee.getName().orElse(null); // TODO
					
					
					List<Employee> found  = aonEmployees.stream()
					.filter(e -> equalsIgnoreCase(e.getDocument(), dni) || equalsIgnoreCase(e.getSocialSecurity(), naf))
					.collect(Collectors.toList());
					
					if ( found.isEmpty() ) {
						enterpriseStatus.and(
						new EnterpriseStatus.AffiliatedNotFound()
						.setDni(dni)
						.setNaf(naf)
						.setDate(date)
						.setName(name)
						.setCcc(ccc.getCode())
						.setRegime(ccc.getRegime())
						);
					} else if ( found.stream().allMatch( e -> e.getId() < 0 )){
						found.stream().map( e -> e.getId() ).findAny().ifPresent( id ->
						enterpriseStatus.and(
						new EnterpriseStatus.AffiliatedAtTrash()
						.setId(id)
						.setDni(dni)
						.setNaf(naf)
						.setDate(date)
						.setName(name)
						.setCcc(ccc.getCode())
						.setRegime(ccc.getRegime())
						));
					}
				}
			}
			
			
			try {
				EnterpriseStatus.isUp2Date(enterpriseStatus); 
				enterpriseStatus.and(new EnterpriseStatus.Up2Date());
			} catch ( OutOfDateException e ) {	
			}
			
			EnterpriseStatus.trace(enterpriseStatus);
			
			return enterpriseStatus;				
			
		} catch ( ForbiddenException e) {
			return new EnterpriseStatus.Forbidden();
		} catch ( CertificateNotFoundException e) {
			return new EnterpriseStatus.CredentialsNotFound();
		} catch (  SQLException | SegSocialException e ) {
			throw new RuntimeException(e);
		} 
	}
	
	@Override
	public List<ContractAttach> getContractAttachments(String domainName, Integer contractId) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			return JooqContrataContract.getContractAttachments(connection, domainId, contractId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<ContractAttach> setContractAttachments(String domainName, Integer contractId, List<ContractAttach> contractAttachments) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			return JooqContrataContract.setContractAttachments(connection, domainId, contractId, contractAttachments);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<ContractAttach> createContractAttach(String domainName, ContractAttach contractAttach) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			return JooqContrataContract.createContractAttach(connection, domainId, contractAttach);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<ContractAttach> deleteContractAttach(String domainName, ContractAttach contractAttach) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			return JooqContrataContract.deleteContractAttach(connection, domainId, contractAttach);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<ContractClause> getContractClauses(String domainName, Integer contractId) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			return JooqContrataContract.getContractClauses(connection, domainId, contractId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<ContractClause> setContractClauses(String domainName, Integer contractId, List<ContractClause> contractClauses) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			return JooqContrataContract.setContractClauses(connection, domainId, contractId, contractClauses);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<ContractClause> createContractClause(String domainName, ContractClause contractClause) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			return JooqContrataContract.createContractClause(connection, domainId, contractClause);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<ContractClause> deleteContractClause(String domainName, ContractClause contractClause) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			return JooqContrataContract.deleteContractClause(connection, domainId, contractClause);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Map<String, String> getContractOtherInfo(String domainName, Integer contractId, String contractType) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			return JooqContrataContract.getContractOtherInfo(connection, domainId, contractId, contractType);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Map<String, CNO> getCNOs(String domainName) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			return JooqContrataContract.getCNOs(connection);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Map<String, String> setContractOtherInfo(String domainName, Integer contractId, String contractType, Map<String, String> contractOtherInfo) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			return JooqContrataContract.setContractOtherInfo(connection, domainId, contractId, contractType, contractOtherInfo);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	
}
