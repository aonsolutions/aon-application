package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.payroll.sql.SQLConstants.CONTRACT;
import static com.esferalia.aon.payroll.sql.SQLConstants.ENTERPRISE;
import static com.esferalia.aon.payroll.sql.SQLConstants.SALARY;
import static com.esferalia.aon.watson.server.AonDateUtils.getMonthFirstDay;
import static com.esferalia.aon.watson.server.AonDateUtils.getMonthLastDay;
import static com.esferalia.aon.watson.util.AonStringUtils.equalsIgnoreCase;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;

import org.jooq.DSLContext;
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
import com.esferalia.aon.gwt.payroll.jooq.JooqComunicaEnterpriseSettings;
import com.esferalia.aon.gwt.payroll.jooq.JooqContractAttach;
import com.esferalia.aon.gwt.payroll.jooq.JooqContractBonus;
import com.esferalia.aon.gwt.payroll.jooq.JooqContractClauses;
import com.esferalia.aon.gwt.payroll.jooq.JooqContractOtherInfo;
import com.esferalia.aon.gwt.payroll.jooq.JooqContractSEPE;
import com.esferalia.aon.gwt.payroll.jooq.JooqContrataContract;
import com.esferalia.aon.gwt.payroll.jooq.JooqDigitalCertificate;
import com.esferalia.aon.gwt.payroll.jooq.JooqDigitalCertificateNew;
import com.esferalia.aon.gwt.payroll.jooq.JooqEmployee;
import com.esferalia.aon.gwt.payroll.jooq.JooqEmployeeAFI;
import com.esferalia.aon.gwt.payroll.jooq.JooqEmployeeContractPayments;
import com.esferalia.aon.gwt.payroll.jooq.JooqEmployeePeculiarities;
import com.esferalia.aon.gwt.payroll.jooq.JooqEmployees;
import com.esferalia.aon.gwt.payroll.jooq.JooqEnterprise;
import com.esferalia.aon.gwt.payroll.jooq.JooqIT;
import com.esferalia.aon.gwt.payroll.jooq.JooqMail;
import com.esferalia.aon.gwt.payroll.jooq.JooqMainCCC;
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
import com.esferalia.aon.gwt.payroll.shared.CertificateInfo;
import com.esferalia.aon.gwt.payroll.shared.ComunicaEnterpriseSettings;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.ContractAttach;
import com.esferalia.aon.gwt.payroll.shared.ContractClause;
import com.esferalia.aon.gwt.payroll.shared.ContractConcepts;
import com.esferalia.aon.gwt.payroll.shared.ContractSpecificData;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.DigitalCertificate;
import com.esferalia.aon.gwt.payroll.shared.DigitalCertificate.CertificateType;
import com.esferalia.aon.gwt.payroll.shared.DigitalCertificateNew;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeSegSocial;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseContext;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseInfo;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseStatus;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseStatus.AndEnterpriseStatus;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.IT;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.esferalia.aon.gwt.payroll.shared.MainCCCInfo;
import com.esferalia.aon.gwt.payroll.shared.OutOfDateException;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Peculiarities;
import com.esferalia.aon.gwt.payroll.shared.SSBonusData;
import com.esferalia.aon.gwt.payroll.shared.SSPECData;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.SecondaryUserCertificate;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceInfo;
import com.esferalia.aon.gwt.payroll.sql.SQLUtils;
import com.esferalia.aon.in.payroll.SistemaRED2AON;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.security.Certificate;
import com.esferalia.aon.occam.api.model.security.CertificateNotFoundException;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.occam.api.model.type.DeductionType.Visitor;
import com.esferalia.aon.payroll.Pair;
import com.esferalia.aon.payroll.agreement.AgreementParser;
import com.esferalia.aon.payroll.agreement.ServiAgreementsFilter;
import com.esferalia.aon.payroll.calculator.sql.SQLPayrollConstants;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
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
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import aon.sepe.objects.Contract;
import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.exception.ForbiddenException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.invalid.NotAllowedContributionAccount;
import solutions.aon.seg.social.object.SecondaryUser;
import solutions.aon.sepe.Sepe;
import solutions.aon.sepe.exceptions.SepeException;

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
		try(Connection connection = AonServletUtils.getConnection(domain)) {
			Integer domainID = AonServletUtils.getDomainID(domain);
			Integer parentDomainID = AonServletUtils.getParentDomainID(domain);
			return JooqAgreement.getAgreements(connection, offset, limit, domainID, parentDomainID);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<Agreement> getTrashAgreements(String domain, int offset, int limit) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			Integer domainID = AonServletUtils.getDomainID(domain);
			Integer parentDomainID = AonServletUtils.getParentDomainID(domain);

			return JooqAgreement.getTrashAgreements(connection, offset, limit,
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
	public String getDeleteAgreementMessage(String domain, Agreement agreement) {
		try(Connection connection = AonServletUtils.getConnection(domain)) {
			return JooqAgreement.getDeleteAgreementMessage(connection, agreement);
		} catch (SQLException e) {
			throw new RuntimeException(e);
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
					+ " AND " + SQLConstants.SALARY + "." + SalaryColumns.TYPE + " < " + Salary.Type.L00.ordinal()
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
					ccc.setRegime(JooqEnterprise.getSSRegime(rs.getInt(SQLConstants.ENTERPRISE_CCC +"."+EnterpriseCccColumns.TYPE)).getCode());
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
					+ " WHERE " + ENTERPRISE + "." + EnterpriseColumns.DOMAIN + " = " + SALARY + "." + SalaryColumns.DOMAIN
					+ " AND " + SALARY + "." + SalaryColumns.TYPE + " < " + SalaryType.L00.ordinal()
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
			stmt.setDate(parameterIndex++, toSqlDate(getMonthLastDay(month)));
			stmt.setDate(parameterIndex++, toSqlDate(getMonthFirstDay(month)));

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
	public List<Workplace> getWorkplaces(String domain) {
		try(Connection connection = AonServletUtils.getConnection(domain)) {
			Integer domainId = AonServletUtils.getDomainID(domain);
			return JooqWorkplace.getWorkplaces(domainId, connection);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Map<String, String> getPayMethods(String domain) {
		try(Connection connection = AonServletUtils.getConnection(domain)) {
			Integer domainId = AonServletUtils.getDomainID(domain);
			return JooqWorkplace.getPayMethods(connection, domainId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ActivitiesCCC getActivityCCC(String domain) {
		try(Connection connection = AonServletUtils.getConnection(domain)) {
			Integer domainId = AonServletUtils.getDomainID(domain);
			return JooqWorkplace.getActivitiesCCC(domainId, connection);
		} catch (SQLException e) {
			throw new RuntimeException(e);
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
	public String getDeleteCCCMessage(String domain, ArrayList<Integer> cccIds) {
		try(Connection connection = AonServletUtils.getConnection(domain)) {
			Integer domainId = AonServletUtils.getDomainID(domain);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domain);
			return JooqActivity.getDeleteCCCMessage(connection, cccIds);
		} catch (SQLException e) {
			throw new RuntimeException(e);
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
		try(Connection connection = AonServletUtils.getConnection(domain)) {
			Integer domainID = AonServletUtils.getDomainID(domain);
			Integer parentDomainID = AonServletUtils.getParentDomainID(domain);

			EnterpriseInfo enterpriseInfo = JooqEnterprise.getEnterpriseInfo(connection, enterpriseId);
			enterpriseInfo.setAgreements(JooqAgreement.getAgreements(connection, 0, Integer.MAX_VALUE, domainID, parentDomainID));
			enterpriseInfo.setScopes(JooqEnterprise.getEnterpriseScopes(connection, enterpriseId));
			
			return enterpriseInfo;
		} catch (SQLException e) {
			throw new RuntimeException(e);
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
	public List<CRA> getCRAs(String domain, String user, long liquidDateTime) {
		try (Connection connection = AonServletUtils.getConnection(domain)) {
			Integer domainId = AonServletUtils.getDomainID(domain);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domain);
			Integer userId = AonServletUtils.getUserID(connection, user, domainId, parentDomainId);
			return JooqCRA.getDomainCRAs(domainId, userId, liquidDateTime, connection);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String createNewCRA(String domainName, long findingDate, List<String> cccList, ArrayList<Integer> cccIdList, Integer cccId, String craType) {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			
			Boolean existAnySalary = Cra.existAnySalary(cccList, findingDate, connection);
			
			if(Boolean.FALSE.equals(existAnySalary)) {
				return "No existe n\u00F3minas con valores para notificar en el CRA";
			}
			
			JSONObject mainCRAJSON = Cra.getMainCRAByCRA(cccList, findingDate, connection);
			String agrarianAFI = MainCRAGenerator.generateMainCRA(mainCRAJSON);
			
			Integer domainId = AonServletUtils.getDomainID(domainName);
			
			return JooqCRA.setMainCra(domainId, cccList, cccIdList, agrarianAFI, findingDate, craType, connection);
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void deleteCRA(String domainName, Integer craBatchId) {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			JooqCRA.deleteMainCRA(craBatchId, connection);
		} catch (SQLException e) {
			throw new RuntimeException(e);
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
	public List<SSPECData> getEmployeeSSPECs(String currentDomainName, String currentUser, Integer contractId) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(currentDomainName);
			Integer domainId = AonServletUtils.getDomainID(currentDomainName);
			
			syncWithIdcs(currentDomainName, currentUser, contractId, connection);
			
			return getPECs(currentDomainName, domainId, currentUser, contractId);
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
	public List<SSBonusData> getEmployeeSSBonuses(String currentDomainName, String currentUser, Integer contractId) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(currentDomainName);
			
			syncWithIdcs(currentDomainName, currentUser, contractId, connection);
			
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
	public void setEmployeeAFIChanges(String currentDomainName, Integer contractId, AFIChanges afiChangesMap) {
		try(Connection connection = AonServletUtils.getConnection(currentDomainName)) {
			JooqEmployeeAFI.setEmployeeAFI(connection, contractId, afiChangesMap);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public AFIChanges getEmployeeAFIChanges(String currentDomainName, Integer contractId) {
		try(Connection connection = AonServletUtils.getConnection(currentDomainName)) {
			return JooqEmployeeAFI.getEmployeeAFI(connection, contractId);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	// ---------------------------------------------- JooqMail.java

	@Override
	public List<MailAccount> getDomainMailAccounts(String domainName, String currentUser) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, currentUser, domainId, parentDomainId);
			return JooqMail.getMailAccounts(connection, userId, domainId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getPayrollEmailSendTo(String domainName, com.esferalia.aon.gwt.payroll.client.PayrollEmailDialog.Type type, Integer enterpriseID) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			return JooqMail.getPayrollEmailSendTo(connection, type, enterpriseID);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getPayrollEmailBody(String domainName, com.esferalia.aon.gwt.payroll.client.PayrollEmailDialog.Type type, HashMap<String, String> params) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			return JooqMail.getPayrollEmailBody(connection, type, params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String checkEmployeesEmails(String domainName, ArrayList<Integer> salaryIds) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			return JooqMail.checkEmployeesEmails(connection, salaryIds);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String sendPayrollEmail(String domainName, com.esferalia.aon.gwt.payroll.client.PayrollEmailDialog.Type type, HashMap<String, String> params, String from, String to, String cc, String cco, String bodyHTML) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			return JooqMail.sendPayrollEmail(connection, type, params, from, to, cc, cco, bodyHTML);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	// ----------------------------------------------

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
			return JooqEnterprise.getEnterprisesCCCInfo(connection, userId, domainId, findPeriodTime);

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
	public EmployeeContractInfo getEmployeeInfo(String domainName, Integer contractId) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			return JooqContrataContract.getEmployeeInfo(connection, contractId);
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
	public List<ITEmployee> getWorkplaceEmployeeITInfo(String domainName, Boolean allEmployees, Integer workplaceId) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			return JooqIT.getWorkplaceEmployeeITInfo(connection, workplaceId, allEmployees);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<ITEmployee> getEmployeeITInfo(String domainName, Integer contractId) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			return JooqIT.getEmployeeITInfo(connection, contractId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<ITEmployee> getEmployeesITInfo(String domainName, Integer itIds []) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			return JooqIT.getEmployeesITInfo(connection, itIds);
//			return JooqIT.getEmployeesITInfo(connection, domainId, false);
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

			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");
			
			List<CCC> cccs = getEnterprises(connection, userId, domainId, 0, Short.MAX_VALUE).stream()
			.filter(e -> enterpriseId == null || e.getId().equals(enterpriseId) )
			.flatMap(e -> e.getActivities().stream() )
			.flatMap(a -> a.getCccs().stream())
			.collect(Collectors.toList());
			
			List<Integer> cccIds = cccs.stream().map( ccc-> ccc.getId() ).collect(Collectors.toList());
			
			Date today = new Date(System.currentTimeMillis()); //TODO:  TimeoOne ????
			List<Employee> aonEmployees = getCCCEmployees(connection, today, cccIds);
			aonEmployees.forEach(e -> System.out.println(e.getName() + " : " + e.getStartDate() + "..." + e.getEndDate() ));

			AndEnterpriseStatus enterpriseStatus = new AndEnterpriseStatus();
			
			Map<String, CCC> cccsMap = cccs.stream().collect(Collectors.toMap(ccc -> ccc.getRegime()+ccc.getCode(), ccc -> ccc, (ccc1, ccc2) -> ccc1));
			
			for ( CCC ccc: cccsMap.values() ) {
				Collection<solutions.aon.seg.social.object.Employee> ssEmployees = 
				SistemaRED.getEmployees(certificate.getCertificate(), certificate.getPassword(), certificate.getType(), ccc.getRegime(), ccc.getCode());
				
				for ( solutions.aon.seg.social.object.Employee ssEmployee :  ssEmployees) {
					
					
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
		} catch ( NotAllowedContributionAccount e) {
			return new EnterpriseStatus.NotAuthorizedCCC();
		} catch ( CertificateNotFoundException e) {
			return new EnterpriseStatus.CredentialsNotFound();
		} catch ( SegSocialException e  ) {
			return new EnterpriseStatus.UnknownError().setMessage(e.getMessage());
		} catch (  SQLException e ) {
			throw new RuntimeException(e);
		} 
	}
	
	@Override
	public List<ContractAttach> getContractAttachments(String domainName, Integer contractId) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			return JooqContractAttach.getContractAttachments(connection, domainId, contractId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<ContractAttach> setContractAttachments(String domainName, Integer contractId, List<ContractAttach> contractAttachments) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			return JooqContractAttach.setContractAttachments(connection, contractAttachments);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<ContractAttach> createContractAttach(String domainName, ContractAttach contractAttach) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			return JooqContractAttach.createContractAttach(connection, domainId, contractAttach);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<ContractAttach> deleteContractAttach(String domainName, ContractAttach contractAttach) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			return JooqContractAttach.deleteContractAttach(connection, domainId, contractAttach);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<ContractClause> getContractClauses(String domainName, Integer contractId) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			return JooqContractClauses.getContractClauses(connection, contractId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<ContractClause> setContractClauses(String domainName, Integer contractId, List<ContractClause> contractClauses) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			return JooqContractClauses.setContractClauses(connection, contractClauses);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<ContractClause> createContractClause(String domainName, ContractClause contractClause) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			return JooqContractClauses.createContractClause(connection, contractClause);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<ContractClause> deleteContractClause(String domainName, ContractClause contractClause) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			return JooqContractClauses.deleteContractClause(connection, contractClause);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Map<String, String> getContractOtherInfo(String domainName, Integer contractId, String contractType) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			return JooqContractOtherInfo.getContractOtherInfo(connection, domainId, parentDomainId, contractId, contractType);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Map<String, String> setContractOtherInfo(String domainName, Integer contractId, String contractType, Map<String, String> contractOtherInfo) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			return JooqContractOtherInfo.setContractOtherInfo(connection, domainId, parentDomainId, contractId, contractOtherInfo);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public ContractSpecificData getContractSpecificData(String domainName, Integer contractId) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			return JooqContractSEPE.getContractSpecificData(connection, contractId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setContractSpecificData(String domainName, EmployeeContractInfo employeeContractData) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			JooqContractSEPE.setContractSpecificData(connection, domainId, employeeContractData);
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

	
//	public List<DigitalCertificate> getDigitalCertificatesToken(String domainName, String token) {
//		Domain domain = AON.getDomain(domainName, 0, "", f -> f.getNameProperty().eq(domainName));
//		User user = AON_SOLUTIONS.getUser(domain, token);
//		try(Connection connection = AonServletUtils.getConnection(domain.getName())) {
//			return JooqDigitalCertificate.getDigitalCertificates(connection, domain.getId(), user.getId());
//		} catch (SQLException e) {
//			throw new RuntimeException(e);
//		}
//	}
	
	@Override
	public DigitalCertificate getDigitalCertificateTGSS(String domainName, String userLogin) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);
			return JooqDigitalCertificate.getDigitalCertificateTGSS(connection, domainId, userId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<DigitalCertificate> getDigitalCertificatesSEPE(String domainName) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			return JooqDigitalCertificate.getDigitalCertificatesSEPE(connection, domainId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		//TODO: if token is empty call getDigitalCertificatesToken
	}
	
	@Override
	public void deleteDigitalCertificate(String domainName, DigitalCertificate digitalCertificate) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			JooqDigitalCertificate.deleteDigitalCertificate(connection, domainId, digitalCertificate);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public MainCCCInfo getMainCCCInfoDataBase(String domainName, String userLogin) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);
			return JooqMainCCC.getMainCCCInfo(connection, domainId, userId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setMainCCCInfoDataBase(String domainName, String userLogin, MainCCCInfo mainCCCInfo) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);
			JooqMainCCC.setMainCCCInfo(connection, domainId, userId, mainCCCInfo);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<SSBonusData> getContractBonus(String domainName, Integer contractId) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			return JooqContractBonus.getContractBonus(connection, contractId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private static List<SSPECData> getPECs(String domainName, Integer domainId, String user, Integer contractId) {
		List<SSPECData> pecs = new ArrayList<SSPECData>();
		
		Date startDate = AonDateUtils.getSqlDate(1900, Calendar.JANUARY, 1);
		
		PAYROLL.getContract(domainName, domainId, user, p -> p.getIdProperty().eq(contractId))
		.ifPresent(contract -> {
			String ccc = contract.getEnterpriseCCC();
			String naf = contract.getPersonSsNumber();
			
			com.esferalia.aon.occam.api.model.Cost costs [] = 
			PAYROLL.getCosts(domainName, domainId, user, ccc, naf, startDate, null);
			
			for (com.esferalia.aon.occam.api.model.Cost cost : costs)
				if ( isPEC(cost.getExpression()) )
					pecs.add(newSSPECData(
							cost.getType(), 
							cost.getStartDate(), 
							cost.getEndDate(), 
							String.format("%s. %s %s", cost.getDescription(), "CUOTA EMPRESARIAL", getName(cost.getType())), 
							cost.getExpression()));
						
			com.esferalia.aon.occam.api.model.Bonus bonuses [] = 
			PAYROLL.getBonuses(domainName, domainId, user, ccc, naf, startDate, null);
			for (com.esferalia.aon.occam.api.model.Bonus bonus : bonuses)
				if ( isPEC(bonus.getExpression()) )
					pecs.add(newSSPECData(
							bonus.getType(), 
							bonus.getStartDate(), 
							bonus.getEndDate(), 
							String.format("%s. %s %s", bonus.getDescription(), "CUOTA EMPRESARIAL", getName(bonus.getExpression())), 
							bonus.getExpression()));
			
			com.esferalia.aon.occam.api.model.Deduction deductions [] = 
			PAYROLL.getDeductions(domainName, domainId, user, ccc, naf, startDate, null);
			for (com.esferalia.aon.occam.api.model.Deduction deduction : deductions)
				if ( isPEC(deduction.getExpression()) )
					pecs.add(newSSPECData(
							deduction.getType(),
							deduction.getStartDate(), 
							deduction.getEndDate(), 
							String.format("%s. %s %s", deduction.getDescription(), "CUOTA TRABAJADOR", getName(deduction.getType())), 
							deduction.getExpression()));
			
			com.esferalia.aon.occam.api.model.payroll.ContractData datas [] = 
			PAYROLL.getData(domainName, domainId, user, ccc, naf, startDate, null);
			for (com.esferalia.aon.occam.api.model.payroll.ContractData data : datas) {
				ContextVariable contextVar = ContextVariable.getVariableByName(data.getName());
				if ( contextVar == null )
					continue;
				
				switch (contextVar) {
				case IT_RATE:
					pecs.add(newSSPECData(
						null,
						data.getStartDate(), 
						data.getEndDate(), 
						String.format("I.T.: %.2f %%", Double.parseDouble(data.getExpression())), 
						data.getExpression()));
					break;
				case IMS_RATE:
					pecs.add(newSSPECData(
						null,
						data.getStartDate(), 
						data.getEndDate(), 
						String.format("I.M.S.: %.2f %%", Double.parseDouble(data.getExpression())), 
						data.getExpression()));
					break;
				case UNEMPLOY_EMPLOYEE_PERCENT:
					pecs.add(newSSPECData(
						null,
						data.getStartDate(), 
						data.getEndDate(), 
						String.format("DESEMPLEO TRABAJADOR: %.2f %%", Double.parseDouble(data.getExpression())), 
						data.getExpression()));
					break;
				case UNEMPLOY_ENTERPRISE_PERCENT:
					pecs.add(newSSPECData(
						null,
						data.getStartDate(), 
						data.getEndDate(), 
						String.format("DESEMPLEO EMPRESA:  %.2f %%", Double.parseDouble(data.getExpression())), 
						data.getExpression()));
					break;
				default:
					break;
				}
			}
		});
		
		
		return pecs;
	}
	
	private static boolean isPEC(String expression) {
		return AonStringUtils.startsWithIgnoreCase(expression, "/*epoch");
	}
	
	private static String getName (DeductionType type) {
		if ( type == null )
			return "";
		return type.accept(new Visitor<String>() {
			@Override
			public String visitIT(DeductionType deductionType) {
				return "IT";
			}

			@Override
			public String visitJobTraining(DeductionType deductionType) {
				return "FORMACI\u00D3N PROFESIONAL";
			}

			@Override
			public String visitIMS(DeductionType deductionType) {
				return "IMS";
			}
			
			@Override
			public String visitFogasa(DeductionType deductionType) {
				return "FOGASA";
			}
			
			@Override
			public String visitProfessionalContigency(DeductionType deductionType) {
				return "AT y EP";
			}
			
			@Override
			public String visitUnemployent(DeductionType deductionType) {
				return "DESEMPLEO";
			}
			
			@Override
			public String visitCommonContigency(DeductionType deductionType) {
				return "CONTINGENCIAS COMUNES";
			}
			
		});
	}

	private static String getName(String expression) {
		MatchResult matchResult = 
		RegExp.compile("quota:([0-9]+)").exec(expression);
		if ( matchResult == null ) 
			return "";
		
		String quota = matchResult.getGroup(1);

		switch (quota) {
			case "01":
				return "AT Y EP, CUOTAS DE RECAUDACI\u00D3N";
			case "02":
				return "DESEMPLEO";
			case "03":
				return "CONTINGENCIAS COMUNES";
			case "04":
				return "DESEMPLEO";
			case "05":
				return "POR DESEMPLEO";
			case "06":
				return "DESEMPLEO, FORMACI\u00D3N PROFESIONAL Y FOGASA";
			case "07":
				return "CONTINGENCIAS COMUNES, DESEMPLEO,";
			case "08":
				return "TOTALIDAD";
			case "09":
				return "CONTINGENCIAS COMUNES, EXCEPTO IT";
			case "10":
				return "CONTINGENCIAS COMUNES EXCEPTO IT,  DESEMPLEO,";
			case "11":
				return "PROTECCI\u00D3N FAMILIAR Y FOGASA CUOTA TOTAL";
			case "12":
				return "DESEMPLEO Y FOGASA CUOTA TOTAL";
			case "13":
				return "FOGASA CUOTA TOTAL";
			case "14":
				return "CONTINGENCIAS COMUNES Y OTRAS COTIZACIONES";
			case "15":
				return "IT CONTINGENCIAS COMUNES, ASISTENCIA SANITARIA,";
			case "16":
				return "IT CONTINGENCIAS COMUNES, ASISTENCIA SANITARIA,";
			case "17":
				return "CUOTA OBRERA CONTINGENCIAS COMUNES. AT Y EP Y";
			case "18":
				return "CUOTA OBRERA CONTINGENCIAS COMUNES. AT Y EP Y";
			case "19":
				return "AT Y EP Y OTRAS COTIZACIONES";
			case "20":
				return "ASISTENCIA SANITARIA Y PRESTACI\u00D3N FARMACE\u00DATICA";
			case "21":
				return "CONTINGENCIAS COMUNES, ASISTENCIA SANITARIA Y";
			case "22":
				return "JUBILACI\u00D3N";
			case "23":
				return "IT CONTINGENCIAS COMUNES, MATERNIDAD, PROTECCI\u00D3N";
			case "24":
				return "IT CONTINGENCIAS COMUNES";
			case "25":
				return "IT, PROTECCI\u00D3N A LA FAMILIA, ASISTENCIA SANITARIA,";
			case "26":
				return "PRESTACI\u00D3N FARMACE\u00DATICA";
			case "27":
				return "IT DE AT, DESEMPLEO, FOGASA";
			case "28":
				return "IT DE AT, FOGASA";
			case "29":
				return "IT, MATERNIDAD, AT Y EP, PRESTACI\u00D3N A LA FAMILIA,";
			case "30":
				return "IT, MATERNIDAD, AT Y EP, OTRAS COTIZACIONES";
			case "31":
				return "IT, INCAPACIDAD PERMANENTE, MUERTE Y";
			case "32":
				return "JUBILACI\u00D3N, IT, INCAPACIDAD PERMANENTE, MUERTE Y";
			case "33":
				return "JUBILACI\u00D3N, IT, INCAPACIDAD PERMANENTE, MUERTE Y";
			case "34":
				return "JUBILACI\u00D3N, IT, INCAPACIDAD PERMANENTE, MUERTE Y";
			case "35":
				return "IT CONTINGENCIAS COMUNES, PRESTACI\u00D3N A LA FAMILIA,";
			case "36":
				return "PRESTACI\u00D3N A LA FAMILIA, DESEMPLEO";
			case "37":
				return "JUBILACI\u00D3N, INCAPACIDAD PERMANENTE CONTINGENCIAS";
			case "38":
				return "IT, AT Y EP";
			case "39":
				return "IT";
			case "40":
				return "CONTINGENCIAS COMUNES, DESEMPLEO";
			case "41":
				return "AT Y EP";
			case "42":
				return "SISTEMA ESPECIAL DEL TOMATE FRESCO";
			case "43":
				return "CONTINGENCIAS COMUNES";
			case "44":
				return "IT CONTINGENCIAS COMUNES, DESEMPLEO Y FOGASA";
			case "45":
				return "IT CONTINGENCIAS COMUNES Y FOGASA";
			case "46":
				return "CONTINGENCIAS COMUNES IT";
			case "47":
				return "CONTINGENCIAS COMUNES/BASE MÍNIMA RETA";
			case "48":
				return "CONTINGENCIAS COMUNES OBLIGATORIA-IT/REA";
			case "49":
				return "APORTACI\u00D3N EMPRESARIAL CC/HOGAR";
			case "50":
				return "CONTINGENCIAS COMUNES -IT/CUENTA PROPIA";
			case "51":
				return "HORAS EXTRAS";
			case "52":
				return "CONTINGENCIAS OBLIGATORIAS/BASE MÍNIMA";
			case "53":
				return "P.F. DESEMPLEO, FOGASA, FORMACI\u00D3N PROFESIONAL";
			case "54":
				return "CONTINGENCIAS COMUNES -CUOTA TRABAJADOR";
			case "55":
				return "CONTINGENCIAS COMUNES - CUOTA OBRERA, AT Y EP, OC";
			case "56":
				return "CONTINGENCIAS COMUNES - DIFERENCIAS BASE";
			case "57":
				return "";
			case "58":
				return "CONTINGENCIA COM\u00DAN - ERE";
			case "59":
				return "CONTRATO FORMACI\u00D3N - SIN EXCLUSIONES";
			case "60":
				return "AS, PF, IT, MA, DE, FGS Y FP ";
			case "61":
				return "- JUBILACI\u00D3N, INCAPACIDAD PERMANENTE,";
			case "62":
				return "FOGASA, FORMACI\u00D3N PROFESIONAL";
			case "63":
				return "S.E.A. - PRESTACIONES DE CORTA DURACI\u00D3N";
			case "64":
				return "CONTINGENCIAS COMUNES, INCAPACIDAD TEMPORAL, OTRAS";
			case "65":
				return "FOGASA - CESE ACTIVIDAD - OBLIGACI\u00D3N";
			case "68":
				return "CONTINGENCIAS COMUNES Y PROFESIONALES";
			case "69":
				return "FOGASA/CESE ACTIVIDAD";
			case "70":
				return "C.C. S./ HORAS COMPLEMENTARIAS";
			case "71":
				return "CONTINGENCIAS COMUNES -IT/BASE MÍNIMA RET";
			case "72":
				return "MATERNIDAD/PATERNIDAD TIEMPO PARCIAL";
			case "73":
				return "C.EMP.C.C. T.PLANA 3A-MAT/PAT T.PARC.";
			case "74":
				return "JUB, IPCC, MSCC, D.-CUOTA TOTAL";
			case "75":
				return "SIN HORAS EXTRAS";
			case "76":
				return "TIPO COTIZACI\u00D3N IT AT";
			case "77":
				return "TIPO COTIZACI\u00D3N ISM AT";
			case "78":
				return "FORMACI\u00D3N PROFESIONAL";
			case "79":
				return "DESEMPLEO Y FORMACI\u00D3N PROFESIONAL";
			case "80":
				return "DECREMENTO BBCC SOBRE TIEMPO COMPLETO";
			case "81":
				return "CONTINGENCIAS COMUNES Y PROFESIONALES - BBCC MEDIA 12";
			default : 
				return "";
		}
	}
	
	
	private static <T extends Enum<?>> SSPECData newSSPECData(T type, java.util.Date startDate, java.util.Date endDate, String description, String expression) {
		Boolean isSystem = true;
		Byte ordinal = type == null ? null : (byte) type.ordinal();
		return new SSPECData(isSystem, startDate, endDate, description, ordinal, expression);
	}
	
	private static void syncWithIdcs(String currentDomainName, String currentUser, Integer contractId, Connection connection)
			throws SQLException {
		Integer domainId = AonServletUtils.getDomainID(currentDomainName);
		Integer parentDomainId = AonServletUtils.getParentDomainID(currentDomainName); 
		Integer userId = AonServletUtils.getUserID(connection, currentUser, domainId, parentDomainId);			
		
		PAYROLL.getContract(
				currentDomainName, 
				parentDomainId, 
				currentUser, 
				p -> p.getIdProperty().eq(contractId))
		.ifPresent( contract -> SistemaRED2AON.syncWithIdcs(
				currentUser, 
				currentDomainName, 
				domainId, 
				userId, 
				contract.getSsRegime().getCode(), 
				contract.getEnterpriseCCC(), 
				contract.getPersonSsNumber(),
				contract.getEndDate() ) );
	}

	@Override
	public List<SecondaryUserCertificate> getSecondaryUsers(String domainName, String userLogin) throws IllegalArgumentException {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName); 
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);	
			
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");
			
			InputStream is = new ByteArrayInputStream(certificate.getCertificate());
			Collection<SecondaryUser> secondaryUsersCollection = SistemaRED.getSecondaryUsers(is, certificate.getPassword(), certificate.getType());
			
			List<SecondaryUser> secondaryUsers = new ArrayList<SecondaryUser>(secondaryUsersCollection);
			List<SecondaryUserCertificate> secondaryUsersCertificate = new ArrayList<SecondaryUserCertificate>();
			
			for(SecondaryUser secondaryUser : secondaryUsers) {
				secondaryUsersCertificate.add(new SecondaryUserCertificate(
						secondaryUser.getAuthoritation(),
						secondaryUser.getAuthoritationEntity(),
						secondaryUser.getMainUserName(),
						secondaryUser.getMainUserIpf(),
						secondaryUser.getMainUserNaf(),
						secondaryUser.getName(),
						secondaryUser.getProvince(),
						secondaryUser.getIpf(),
						secondaryUser.getNaf(),
						secondaryUser.getSituation(),
						secondaryUser.getSituationDate(),
						secondaryUser.getTelephone(),
						secondaryUser.getFax(),
						secondaryUser.getMobile(),
						secondaryUser.getMail()
				));
			}
			
			return secondaryUsersCertificate;
			
		} catch (SQLException | SegSocialException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	@Override
	public void deleteSecondaryUser(String domainName, String userLogin, String ipfType, String ipf) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName); 
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);	
			
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");
			
			InputStream is = new ByteArrayInputStream(certificate.getCertificate());
			SistemaRED.deleteSecondaryUser(is, certificate.getPassword(), certificate.getType(), ipfType, ipf);
			
		} catch (SQLException | SegSocialException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void createSecondaryUser(String domainName, String userLogin, String ipfType, String ipf, String naf) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName); 
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);	
			
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");
			
			InputStream is = new ByteArrayInputStream(certificate.getCertificate());
			SistemaRED.registerSecondaryUserByNie(is, certificate.getPassword(), certificate.getType(), ipfType, ipf, naf);
			
		} catch (SQLException | SegSocialException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public EmployeeSegSocial getIpfxNaf(String domainName, String userLogin, ArrayList<String> nssList) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName); 
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);	
			
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");
			
			InputStream is = new ByteArrayInputStream(certificate.getCertificate());
			
			Collection<solutions.aon.seg.social.object.Employee> employeeCollection = SistemaRED.ipfxnaf(is, certificate.getPassword(), certificate.getType(), nssList);
			solutions.aon.seg.social.object.Employee employee = (solutions.aon.seg.social.object.Employee) employeeCollection.toArray()[0];
			
			EmployeeSegSocial employeeSegSocial = new EmployeeSegSocial(
					employee.getNss(), 
					employee.getName().orElse(null), 
					employee.getBirthDate().orElse(null), 
					employee.getIpf());
			
			return employeeSegSocial;
		} catch (SQLException | SegSocialException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public EmployeeSegSocial getNafxIpf(String domainName, String userLogin, String ipf, String apellido1, String apellido2) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName); 
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);	
			
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");
			
			InputStream is = new ByteArrayInputStream(certificate.getCertificate());
			
			solutions.aon.seg.social.object.Employee employee = SistemaRED.nafxipf(is, certificate.getPassword(), certificate.getType(), ipf, apellido1, apellido2);
			
			EmployeeSegSocial employeeSegSocial = new EmployeeSegSocial(
					employee.getNss(), 
					employee.getName().orElse(null), 
					employee.getBirthDate().orElse(null), 
					employee.getIpf());
			
			return employeeSegSocial;
		} catch (SQLException | SegSocialException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean createITCertificate(String domainName, String userLogin, String affiliationNumber,
			String regime, String contributionAccount, String docType, String docNum, String applicantType,
			String reason, java.util.Date dateFrom, java.util.Date dateTo, float baseCC, float baseCP, int days) {
		
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName); 
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);	
			
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");
			
			float newBaseCC = JooqEmployee.getBaseCC(connection, docNum, dateFrom);
//			baseCC = 0 == newBaseCC ? baseCC : newBaseCC;
			
			float newBaseCP = JooqEmployee.getBaseCP(connection, docNum, dateFrom);
//			baseCP = 0 == newBaseCP ? baseCP : newBaseCP;
			
			days = JooqEmployee.getDays(connection, docNum, dateFrom);
			
			Boolean recordCertificate = SistemaRED.recordCertificate(certificate.getCertificate(), certificate.getPassword(), certificate.getType(), affiliationNumber, regime, contributionAccount, docType, docNum, applicantType, reason, dateFrom, dateTo, baseCC, baseCP, days);
			return recordCertificate;
		
		} catch (SQLException | SegSocialException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void deleteComunicateIT(String domainName, String userLogin, String affiliationNumber,
			String regime, String contributionAccount, java.util.Date dateFrom, java.util.Date dateTo,
			java.util.Date startDate) {
		
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName); 
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);	
			
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");
			
			SistemaRED.voidPaternity(certificate.getCertificate(), certificate.getPassword(), certificate.getType(), affiliationNumber, regime, contributionAccount, dateFrom, dateTo, Optional.of(startDate));
		
		} catch (SQLException | SegSocialException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setComunicationIT(String domainName, String userLogin, ITEmployee itEmployee, IT it) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			JooqIT.setComunicationIT(connection, domainId, itEmployee, it);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int getServiAgreement(String domainName, String serviAgreementCode) throws IllegalArgumentException  {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			
			AONContext ctx = new AONContext(connection);
			DSLContext dslContext = ctx.getDslContext();
			
			Pair<Integer,String> agreementLog = AgreementParser.getAgreement(dslContext, serviAgreementCode, domainId);
			
			String log = agreementLog.getSecond();
			if(!AonStringUtils.isBlank(log)) {
				JooqMail.sendAgreementLogMail(log);
			}
			
			Integer agreementId = agreementLog.getFirst();
			return agreementId;
		
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("El convenio con c\u00F3digo " + serviAgreementCode + " (ServiConvenios) no es accesible en este momento. Por favor p\u00F3ngase en contacto con el departamento de soporte para poder ayudarle.");
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public boolean checkIfRectificative(String domainName, java.util.Date findingDate,
			ArrayList<Integer> selectedCCCList) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			AONContext ctx = new AONContext(connection);
			DSLContext dslContext = ctx.getDslContext();
			
			return JooqCRA.checkIfRectificative(dslContext, findingDate, selectedCCCList);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void registerITBaja(String domainName, String userLogin, String regime, String ccc, String naf,
			String contingency, String situation_employee, String licenseNumber,
			String cias, String occupation, java.util.Date startdate, String contractType,
			float baseCot, int cotDays, java.util.Date fATEP, String accidentType) {
		
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName); 
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);	
			
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");
			
			SistemaRED.registerITBaja(
					certificate.getCertificate(), 
					certificate.getPassword(), 
					certificate.getType(), 
					regime, 
					ccc, 
					naf, 
					SistemaRED.Contingencies.valueOf(contingency), 
					SistemaRED.SituationEmployee.valueOf(situation_employee), 
					Optional.of(licenseNumber), 
					Optional.of(cias), 
					Optional.of(occupation), 
					startdate, 
					SistemaRED.ContractType.valueOf(contractType), 
					baseCot, 
					cotDays,
					Optional.of(fATEP), 
					Optional.of(SistemaRED.AccidentType.valueOf(accidentType)));
			
		} catch (SQLException | SegSocialException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void registerITConfirmation(String domainName, String userLogin, String regime, String ccc, String naf,
			String contingency, String situation_employee, String licenseNumber,
			String cias, java.util.Date fbaja, java.util.Date fconfirmation,
			String npartConfimation) {
		
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName); 
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);	
			
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");
			
			SistemaRED.registerITConfirmation(
					certificate.getCertificate(), 
					certificate.getPassword(), 
					certificate.getType(), 
					regime, 
					ccc, 
					naf, 
					SistemaRED.Contingencies.values()[Integer.parseInt(contingency)], 
					SistemaRED.SituationEmployee.values()[Integer.parseInt(situation_employee)], 
					Optional.of(licenseNumber), 
					Optional.of(cias), 
					fbaja, 
					fconfirmation, 
					Optional.of(npartConfimation));
			
		} catch (SQLException | SegSocialException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void registerITAlta(String domainName, String userLogin, String regime, String ccc, String naf,
			String contingency, String situation_employee, String licenseNumber,
			String cias, java.util.Date fbaja, java.util.Date falta, java.util.Date fATEP,
			String accidentType, String causeType) {
		
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName); 
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);	
			
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");
			
			SistemaRED.registerITAlta(
					certificate.getCertificate(), 
					certificate.getPassword(), 
					certificate.getType(), 
					regime, 
					ccc, 
					naf, 
					SistemaRED.Contingencies.values()[Integer.parseInt(contingency)], 
					SistemaRED.SituationEmployee.values()[Integer.parseInt(situation_employee)], 
					Optional.of(licenseNumber), 
					Optional.of(cias), 
					fbaja, 
					falta, 
					Optional.of(fATEP), 
					Optional.of(SistemaRED.AccidentType.values()[Integer.parseInt(accidentType)]), 
					SistemaRED.CauseType.values()[Integer.parseInt(causeType)]);
			
		} catch (SQLException | SegSocialException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void removeIT(String domainName, String userLogin, String regime, String ccc, String naf, String partType,
			java.util.Date dateBj, java.util.Date dateProcess) {
		
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName); 
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);	
			
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");
			
			SistemaRED.removeIT(
					certificate.getCertificate(), 
					certificate.getPassword(),
					certificate.getType(), 
					regime, 
					ccc, 
					naf, 
					SistemaRED.PartType.values()[Integer.parseInt(partType)], 
					dateBj, 
					dateProcess);
			
		} catch (SQLException | SegSocialException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getContratoSepe(String domainName, String userLogin, String ipf, java.util.Date startDate, java.util.Date endDate) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName); 
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);	
			
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "SEPE");
			InputStream is = new ByteArrayInputStream(certificate.getCertificate());
			
			Contract contract = Sepe.getContractData(is, certificate.getPassword(), certificate.getType(), ipf, startDate, endDate);
			
			return null == contract ? null : contract.getSepeId();
			
		} catch (SQLException | SepeException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<EmployeeContractInfo> getTrashEmployeesInfo(String domainName) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			return JooqContrataContract.getTrashEmployeesInfo(connection, domainId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void restoreContract(String domainName, Integer contractId) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			JooqEmployees.moveContractId(connection, contractId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void delete4EverContract(String domainName, Integer contractId) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			JooqEmployees.delete(connection, contractId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Map<String, String> getServiAgreements(String domainName, String userLogin) {
		DomainUserRoles domainUserRoles = getDomainUserRoles(domainName, userLogin);
		return ServiAgreementsFilter.getServiAgreementsMap(domainUserRoles.isConvenios());
	}
	
	@Override
	public DomainUserRoles getDomainUserRoles(String domainName, String userLogin) {
		
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName); 
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);	
			
			DomainUserRoles domainUserRoles = SECURITY.getDomainUserRoles(domainName, domainId, userLogin, userId);
			return domainUserRoles;
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public boolean hasCertificateSEPE(String domainName, String userLogin) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName); 
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);	
			
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "SEPE");
			
			return null != certificate;
			
		} catch (Exception e) {
			return false;
		}
	}
	
	protected static Date toSqlDate(java.util.Date date) {
		return date == null ? null : new java.sql.Date(date.getTime());
	}

	@Override
	public ComunicaEnterpriseSettings getComunicaEnterpriseSettings(String domainName, String userLogin) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName); 
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);	
			
			return JooqComunicaEnterpriseSettings.getComunicaEnterpriseSettings(connection, domainId, userId);
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setComunicaEnterpriseSettings(String domainName, String userLogin, ComunicaEnterpriseSettings comunicaEnterpriseSettings) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName); 
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);	
			
			JooqComunicaEnterpriseSettings.setComunicaEnterpriseSettings(connection, domainId, userId, comunicaEnterpriseSettings);
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Integer getEnterpriseId(String domainName) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			return JooqEnterprise.getEnterpriseId(connection, domainId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void verifyCertificate(String domainName, String userLogin, CertificateType certificateType) throws IllegalArgumentException {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName); 
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);	
			
			Certificate certificate = null;
			
			if(certificateType == CertificateType.TGSS) {
				certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");
				InputStream certificateIS = new ByteArrayInputStream(certificate.getCertificate());
				SistemaRED.validateCert(certificateIS, certificate.getPassword(), certificate.getType());
			}
			
			if(certificateType == CertificateType.SEPE) {
				certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "SEPE");
				InputStream certificateIS = new ByteArrayInputStream(certificate.getCertificate());
				Sepe.validateCert(certificateIS, certificate.getPassword(), certificate.getType());
			}
			
		} catch (SQLException | SepeException | SegSocialException e) {
			throw new IllegalArgumentException(e.getMessage());
		} 
	}

	@Override
	public ContractConcepts getAllConcepts(String domainName, String currentUser) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			return JooqEmployeeContractPayments.getAllConcepts(connection, domainId);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e.getMessage());
		} 
	}

	@Override
	public EnterpriseContext getEnterpriseContext(String domainName) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			
			EnterpriseContext enterpriseContext = new EnterpriseContext();
			enterpriseContext.setWorkplaces(JooqWorkplace.getWorkplaces(domainId, connection));
			enterpriseContext.setAgreements(JooqAgreement.getAgreements(connection, 0, Integer.MAX_VALUE, domainId, parentDomainId));
			enterpriseContext.setActivitiesCCC(JooqWorkplace.getActivitiesCCC(domainId, connection));
			enterpriseContext.setPayMethods(JooqWorkplace.getPayMethods(connection, domainId));
			
			return enterpriseContext;
		} catch (SQLException e) {
			throw new IllegalArgumentException(e.getMessage());
		} 
	}
	
	// ----------------------------------------------------------------- DigitalCertificates (New)

	@Override
	public List<DigitalCertificateNew> getDigitalCertificates(String domainName, String userLogin) throws IllegalArgumentException {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);
			return JooqDigitalCertificateNew.getDigitalCertificates(connection, domainId, userId);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void deleteDigitalCertificate(String domainName, DigitalCertificateNew digitalCertificate) throws IllegalArgumentException {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			JooqDigitalCertificateNew.deleteDigitalCertificate(connection, digitalCertificate);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void verifyCertificate(String domainName, String userLogin, Integer rattachId,
			List<com.esferalia.aon.gwt.payroll.shared.DigitalCertificateNew.CertificateType> tags) throws IllegalArgumentException {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {	
			Certificate certificate = JooqDigitalCertificateNew.getCertificate(connection, rattachId);

			for(com.esferalia.aon.gwt.payroll.shared.DigitalCertificateNew.CertificateType tag : tags) {
				if(tag == com.esferalia.aon.gwt.payroll.shared.DigitalCertificateNew.CertificateType.TGSS) {
					InputStream certificateIS = new ByteArrayInputStream(certificate.getCertificate());
					SistemaRED.validateCert(certificateIS, certificate.getPassword(), certificate.getType());
				}

				if(tag == com.esferalia.aon.gwt.payroll.shared.DigitalCertificateNew.CertificateType.SEPE) {
					InputStream certificateIS = new ByteArrayInputStream(certificate.getCertificate());
					Sepe.validateCert(certificateIS, certificate.getPassword(), certificate.getType());
				}
			}

		} catch (SQLException | SepeException | SegSocialException e) {
			if(AonStringUtils.equalsIgnoreCase(e.getMessage(), "java.io.IOException: keystore password was incorrect"))
				throw new IllegalArgumentException("Contrase\u00F1a incorrecta");
			
			throw new IllegalArgumentException(e.getMessage());
		} 
	}


	@Override
	public List<SecondaryUserCertificate> getSecondaryUsers(String domainName, String userLogin, Integer rattachId) throws IllegalArgumentException {
		try(Connection connection = AonServletUtils.getConnection(domainName)){
			Certificate certificate = null;
			
			if(rattachId == null) {
				Integer domainId = AonServletUtils.getDomainID(domainName);
				Integer parentDomainId = AonServletUtils.getParentDomainID(domainName); 
				Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);	
				
				certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");
			} else
				certificate = JooqDigitalCertificateNew.getCertificate(connection, rattachId);
			
			InputStream is = new ByteArrayInputStream(certificate.getCertificate());
			Collection<SecondaryUser> secondaryUsersCollection = SistemaRED.getSecondaryUsers(is, certificate.getPassword(), certificate.getType());
			
			List<SecondaryUser> secondaryUsers = new ArrayList<>(secondaryUsersCollection);
			List<SecondaryUserCertificate> secondaryUsersCertificate = new ArrayList<>();
			
			for(SecondaryUser secondaryUser : secondaryUsers) {
				secondaryUsersCertificate.add(new SecondaryUserCertificate(
						secondaryUser.getAuthoritation(),
						secondaryUser.getAuthoritationEntity(),
						secondaryUser.getMainUserName(),
						secondaryUser.getMainUserIpf(),
						secondaryUser.getMainUserNaf(),
						secondaryUser.getName(),
						secondaryUser.getProvince(),
						secondaryUser.getIpf(),
						secondaryUser.getNaf(),
						secondaryUser.getSituation(),
						secondaryUser.getSituationDate(),
						secondaryUser.getTelephone(),
						secondaryUser.getFax(),
						secondaryUser.getMobile(),
						secondaryUser.getMail()
				));
			}
			
			return secondaryUsersCertificate;
			
		} catch (SQLException | SegSocialException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	@Override
	public void deleteSecondaryUser(String domainName, String userLogin, Integer rattachId, String ipfType, String ipf) throws IllegalArgumentException  {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Certificate certificate = null;
			
			if(rattachId == null) {
				Integer domainId = AonServletUtils.getDomainID(domainName);
				Integer parentDomainId = AonServletUtils.getParentDomainID(domainName); 
				Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);	
				
				certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");
			} else
				certificate = JooqDigitalCertificateNew.getCertificate(connection, rattachId);
			
			InputStream is = new ByteArrayInputStream(certificate.getCertificate());
			SistemaRED.deleteSecondaryUser(is, certificate.getPassword(), certificate.getType(), ipfType, ipf);
			
		} catch (SQLException | SegSocialException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	@Override
	public void createSecondaryUser(String domainName, String userLogin, Integer rattachId, String ipfType, String ipf, String naf) throws IllegalArgumentException  {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			Certificate certificate = null;
			
			if(rattachId == null) {
				Integer domainId = AonServletUtils.getDomainID(domainName);
				Integer parentDomainId = AonServletUtils.getParentDomainID(domainName); 
				Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);	
				
				certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");
			} else
				certificate = JooqDigitalCertificateNew.getCertificate(connection, rattachId);
			
			InputStream is = new ByteArrayInputStream(certificate.getCertificate());
			SistemaRED.registerSecondaryUserByNie(is, certificate.getPassword(), certificate.getType(), ipfType, ipf, naf);
			
		} catch (SQLException | SegSocialException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public CertificateInfo validateCertJava(String domainName, Integer rattachId)  throws IllegalArgumentException {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			return JooqDigitalCertificateNew.validateCertJava(connection, rattachId);
		} catch (SQLException | IllegalArgumentException e) {
			if(AonStringUtils.equalsIgnoreCase(e.getMessage(), "keystore password was incorrect"))
				throw new IllegalArgumentException("Contrase\u00F1a incorrecta");
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	@Override
	public void setContractBonus(String currentDomainName, EmployeeContractInfo employeeContractData) {
		// TODO Auto-generated method stub
	}
	
}
