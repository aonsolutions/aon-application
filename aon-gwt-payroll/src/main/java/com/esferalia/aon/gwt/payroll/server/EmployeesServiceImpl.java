package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.gwt.common.server.AonServletUtils.commit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.disableAutoCommit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.enableAutoCommit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.getConnection;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.rollback;
import static com.esferalia.aon.gwt.payroll.server.EnterprisesServiceImpl.getSSRegime;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ACTIVE_DAYS;
import static com.esferalia.aon.payroll.sql.SQLConstants.AGREEMENT;
import static com.esferalia.aon.payroll.sql.SQLConstants.CONTRACT;
import static com.esferalia.aon.payroll.sql.SQLConstants.DOMAIN;
import static com.esferalia.aon.payroll.sql.SQLConstants.ENTERPRISE;
import static com.esferalia.aon.payroll.sql.SQLConstants.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.payroll.sql.SQLConstants.ENTERPRISE_CCC;
import static com.esferalia.aon.payroll.sql.SQLConstants.IRPF_DATA;
import static com.esferalia.aon.payroll.sql.SQLConstants.IRPF_REGULARIZATION;
import static com.esferalia.aon.payroll.sql.SQLConstants.IRPF_RESULT;
import static com.esferalia.aon.payroll.sql.SQLConstants.PAYROLL_WORKPLACE;
import static com.esferalia.aon.payroll.sql.SQLConstants.RBANK;
import static com.esferalia.aon.payroll.sql.SQLConstants.REGISTRY;
import static com.esferalia.aon.payroll.sql.SQLConstants.SALARY;
import static com.esferalia.aon.payroll.sql.SQLConstants.USER;
import static com.esferalia.aon.payroll.sql.SQLConstants.USER_SCOPE;
import static com.esferalia.aon.payroll.sql.SQLConstants.WORKPLACE;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SortField;
import org.mvel2.CompileException;
import org.mvel2.ast.Function;
import org.mvel2.util.MethodStub;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.report.OutputFormat;
import com.code.aon.report.ReportException;
import com.code.aon.ui.report.controller.ReportManager;
import com.esferalia.aon.google.sql.SQLConstants.DomainColumns;
import com.esferalia.aon.google.sql.SQLConstants.UserColumns;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.common.shared.EvalException;
import com.esferalia.aon.gwt.common.shared.EvalSyntaxErrorException;
import com.esferalia.aon.gwt.common.shared.EvalWarning;
import com.esferalia.aon.gwt.common.shared.UnknownVariablesWarning;
import com.esferalia.aon.gwt.payroll.client.CalendarService;
import com.esferalia.aon.gwt.payroll.client.EmployeeEventsService;
import com.esferalia.aon.gwt.payroll.client.EmployeesService;
import com.esferalia.aon.gwt.payroll.client.StatisticsService;
import com.esferalia.aon.gwt.payroll.jooq.JooqAgreement;
import com.esferalia.aon.gwt.payroll.jooq.JooqCalendar;
import com.esferalia.aon.gwt.payroll.jooq.JooqDeductions;
import com.esferalia.aon.gwt.payroll.jooq.JooqEmployee;
import com.esferalia.aon.gwt.payroll.jooq.JooqEmployeeCalendar;
import com.esferalia.aon.gwt.payroll.jooq.JooqEmployeeCalendarNew;
import com.esferalia.aon.gwt.payroll.jooq.JooqEmployeeEvents;
import com.esferalia.aon.gwt.payroll.jooq.JooqEmployees;
import com.esferalia.aon.gwt.payroll.jooq.JooqEvents;
import com.esferalia.aon.gwt.payroll.jooq.JooqPayments;
import com.esferalia.aon.gwt.payroll.jooq.JooqPayrollSalaries;
import com.esferalia.aon.gwt.payroll.report.StatelessReportManager;
import com.esferalia.aon.gwt.payroll.server.PayrollServletUtils.SalaryFilter;
import com.esferalia.aon.gwt.payroll.server.PayrollServletUtils.SiteFilter;
import com.esferalia.aon.gwt.payroll.shared.Activity;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Agreement.Level;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft.SalaryTable;
import com.esferalia.aon.gwt.payroll.shared.BankAccount;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.CCC;
import com.esferalia.aon.gwt.payroll.shared.CalendarDraft;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarUpdate;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsUpdate;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Events;
import com.esferalia.aon.gwt.payroll.shared.EventsWorkplace;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.ITData;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson;
import com.esferalia.aon.gwt.payroll.shared.Irpf;
import com.esferalia.aon.gwt.payroll.shared.Irpf.IrpfData;
import com.esferalia.aon.gwt.payroll.shared.Irpf.IrpfRegularization;
import com.esferalia.aon.gwt.payroll.shared.Irpf.IrpfResult;
import com.esferalia.aon.gwt.payroll.shared.NumberVariable;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.esferalia.aon.gwt.payroll.shared.Result;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfoFilter;
import com.esferalia.aon.gwt.payroll.shared.SalaryPreview;
import com.esferalia.aon.gwt.payroll.shared.Statistics;
import com.esferalia.aon.gwt.payroll.shared.StringVariable;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.gwt.payroll.shared.VariableDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.esferalia.aon.gwt.payroll.sql.SQLAgreementDraft;
import com.esferalia.aon.gwt.payroll.sql.SQLEvents;
import com.esferalia.aon.gwt.payroll.sql.SQLITData;
import com.esferalia.aon.gwt.payroll.sql.SQLSalaryDraft;
import com.esferalia.aon.gwt.payroll.sql.SQLStatistics;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.EnterpriseActivity;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.IrpfOutcome;
import com.esferalia.aon.payroll.SalaryBonus;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryCost;
import com.esferalia.aon.payroll.SalaryCostsFactory;
import com.esferalia.aon.payroll.SalaryDeduction;
import com.esferalia.aon.payroll.SalaryDeductionsFactory;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.SalaryPaymentsFactory;
import com.esferalia.aon.payroll.calculator.CollectSalaryBuilder;
import com.esferalia.aon.payroll.calculator.GenericContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext.IListener;
import com.esferalia.aon.payroll.calculator.RoundSalaryBuilder;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLAgreementSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseActivityColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseCccColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.GeozoneColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.IrpfDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.IrpfRegularizationColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.IrpfResultColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PayrollWorkplaceColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RbankColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RegistryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryBonusColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryCostColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryDeductionColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryEmbargoColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryPaymentColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SystemDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.UserScopeColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.WorkplaceColumns;
import com.esferalia.aon.salary.CompositeSalaryBuilder;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.SalaryBuilderListener;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.bonus.Bonuses;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.cost.Costs;
import com.esferalia.aon.salary.deduction.Deductions;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.CheckException;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionContext.DeferredException;
import com.esferalia.aon.salary.expression.ExpressionContext.ExpressionExceptionWrapper;
import com.esferalia.aon.salary.expression.ExpressionContext.RemoveVariableError;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.IExpression;
import com.esferalia.aon.salary.expression.IExpressionVariable;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.InvalidVariables;
import com.esferalia.aon.salary.expression.TimedObject;
import com.esferalia.aon.salary.expression.TimedResult;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;
import com.esferalia.aon.salary.payment.Payments;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.utils.ReportUtils;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class EmployeesServiceImpl extends AonRemoteServiceServlet implements
		EmployeesService, StatisticsService, CalendarService, EmployeeEventsService {

	public static final String REMOVE = "REMOVE()";

	private static final Map<Object, Object> JR_HTML_EXPORTER_PARAMS = new HashMap<Object, Object>() {
		{
			put(JRHtmlExporterParameter.HTML_HEADER,
					"<div class='page page-shadow' >");
			put(JRHtmlExporterParameter.BETWEEN_PAGES_HTML,
					"</div><div class='page page-shadow' >");
			put(JRHtmlExporterParameter.HTML_FOOTER, "</div>");
		}
	};

	// ----------------------------------------------- EmployeesService methods

	@Override
	public Enterprise getEnterprise(String domain, String user) throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			Integer domainId = AonServletUtils.getDomainID(domain); 
			Integer parentDomainId = AonServletUtils.getParentDomainID(domain); 
			Integer userID = AonServletUtils.getUserID(conn, user, domainId, parentDomainId);
			Integer registryID = AonServletUtils.getEnterpriseID(domain);
			return getEnterprise(registryID, userID, conn);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnroreca) {
				}
			}
		}
	}

	@Override
	public Enterprise[] getEnterprises(String domain, String user) throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			Integer domainId = AonServletUtils.getDomainID(domain); 
			Integer parentDomainId = AonServletUtils.getParentDomainID(domain); 
			Integer userID = AonServletUtils.getUserID(conn, user, domainId, parentDomainId);

			Integer registryIDs[] = AonServletUtils.getEnterpriseIDs(domain);
			ArrayList<Enterprise> enterprises = new ArrayList<Enterprise>();
			for (int i = 0; i < registryIDs.length; i++) {
				Enterprise enterprise = getEnterprise(registryIDs[i], userID,
						conn);
				if (enterprise != null) {
					enterprises.add(enterprise);
				}
			}
			return enterprises.toArray(new Enterprise[enterprises.size()]);

		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}

	@Override
	public List<Salary> getSalaries(String domain, Employee employee)
			throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			if (employee != null) {
				return notAtEnterpriseSite() ? getSiteSalaries(conn,
						employee.getId()) : getSalaries(conn, employee.getId());
			} else {
				throw new IllegalArgumentException(
						"EmployeeSite Not implemented yet");
			}
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}

	@Override
	public List<Irpf> getIrpfs(String domain, Employee employee)
			throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			return getIrpfOutcomes(conn, employee.getId());
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}

	@Override
	public List<Extra> getExtras(String domain, List<Employee> employees)
			throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			Integer ids [] = employees.stream()
					.map(e->e.getId())
					.toArray(Integer[]::new);
			return JooqAgreement.getEmployeesExtras(conn, ids );
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}

	@Override
	public void insertPerson(String domain, Employee employee) throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			Integer domainID = AonServletUtils.getDomainID(domain);
			JooqEmployees.insert2Person(conn, domainID,
					employee.getPerson(), employee.getDocument());
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}

	}

	@Override
	public Employee getEmployee(String domain, int employeeId) throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			return JooqEmployees.getEmployee(conn, employeeId);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}

	@Override
	public List<Employee> getEmployees(String domain, int workplaceId, Date fromDate,
			String pattern, int offset, int limit)
			throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
 			return JooqEmployees.getEmployees(conn, workplaceId, fromDate,
					pattern, offset, limit);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}

	@Override
	public List<Employee> getTrashEmployees(String domain, int workplaceId)
			throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			return JooqEmployees.getTrashEmployees(conn, workplaceId);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}

	@Override
	public EmployeeCalendarData getEmployeeCalendar(String domain, int contract) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			return JooqEmployeeCalendar.getEmployeeHour(connection, contract);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
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
	public void setEmployeeCalendar(String domain, int contract, EmployeeCalendarUpdate updateInfo) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			JooqEmployeeCalendar.setEmployeeHour(connection, contract, updateInfo);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}

	}

	@Override
	public Map<Integer, String> getHolidayDescription(String domain)
			throws IllegalArgumentException {

		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			Integer domainID = AonServletUtils.getDomainID(domain); 
			Integer parentDomainID = AonServletUtils.getParentDomainID(domain); 
			return JooqCalendar.getHolidayDescription(conn,
					parentDomainID, domainID);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}

	}

	@Override
	public CalendarDraft getCalendar(String domain, int workplaceId, Integer pattern,
			Integer year) throws IllegalArgumentException {

		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			return JooqCalendar.getCalendar(conn, workplaceId, pattern, year);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}

	@Override
	public void saveHolidaysAndDays(String domain, int workplaceId, String holidayDescription,
			Integer holidayListBox, Map<Date, String> map, CalendarDraft.DayType daysTypes [])
			throws IllegalArgumentException {

		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			Integer domainID = AonServletUtils.getDomainID(domain); 
			JooqCalendar.insertHolidays(conn, domainID, workplaceId,
					holidayDescription, holidayListBox, map, daysTypes);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}

	@Override
	public void deletePropertyHoliday(String domain, Integer id, Date date)
			throws IllegalArgumentException {

		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			JooqCalendar.deletePropertyHoliday(conn, id, date);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}

	}

	@Override
	public List<Cost> getWorkplaceCosts(String domain, int workplaceId)
			throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			return notAtEnterpriseSite() ? getSiteWorkplaceCosts(conn,
					workplaceId) : getWorkplaceCosts(conn, workplaceId);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}

	@Override
	public List<Cost> getEnterpriseCosts(String domain, int enterpriseId)
			throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			return notAtEnterpriseSite() ? getSiteEnterpriseCosts(conn,
					enterpriseId) : getEnterpriseCosts(conn, enterpriseId);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getIrpfReceiptHTML(String domain, final Irpf irpf, int zoom)
			throws IllegalArgumentException {

		try {
			HashMap<Object, Object> parameters = new HashMap<Object, Object>(
					JR_HTML_EXPORTER_PARAMS);
			parameters
					.put(JRHtmlExporterParameter.ZOOM_RATIO, zoom / 100.00f /*
																			 * not
																			 * roud
																			 * to
																			 * int
																			 */);

			FacesContext ctx = FacesContext.getCurrentInstance();
			ctx.getViewRoot().setLocale(new Locale("es", "ES"));

			ReportManager reportManager = new StatelessReportManager();
			reportManager.setOutputFormat(OutputFormat.HTML);

			ICollectionProvider provider = new ICollectionProvider() {

				@Override
				public Collection<?> getCollection() {
					try {
						return getCollection(true);
					} catch (ManagerBeanException e) {
						return null;
					}
				}

				@Override
				public Collection<?> getCollection(boolean arg0)
						throws ManagerBeanException {
					return Collections.singletonList(getIrpfOutcome(irpf));
				}

			};

			reportManager.setCollectionProvider(provider);

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			String irpfReport = "irpf";
			
			reportManager.execute(out, irpfReport, parameters);

			return out.toString();

		} catch (ReportException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public String getSalaryReceiptHTML(String domain, Salary salary, int zoom)
			throws IllegalArgumentException {

		Map<Object, Object> parameters = new HashMap<Object, Object>(
				JR_HTML_EXPORTER_PARAMS);
		parameters.put(JRHtmlExporterParameter.ZOOM_RATIO, zoom / 100.00f /*
																		 * not
																		 * roud
																		 * to
																		 * int
																		 */);

		Map<Object, Object> images = new HashMap<Object, Object>();
		parameters.put(JRHtmlExporterParameter.IMAGES_MAP, images);
		String imagesUri = String.format("jasper_image/salary/%d/",
				salary.getId());
		parameters.put(JRHtmlExporterParameter.IMAGES_URI, imagesUri);

		String html = getSalaryReceiptHTML(domain, salary, parameters);

		for (Entry<Object, Object> image : images.entrySet()) {
			String name = String.format("%s%s", imagesUri, image.getKey());
			JasperImageServlet.saveImage(name, (byte[]) image.getValue());
		}

		return html;
	}

	@Override
	public String getSalaryReceiptHTML(String domain, Cost cost, Salary.Type types[], int zoom)
			throws IllegalArgumentException {

		Map<Object, Object> parameters = new HashMap<Object, Object>(
				JR_HTML_EXPORTER_PARAMS);
		parameters.put(JRHtmlExporterParameter.ZOOM_RATIO, zoom / 100.00f /*
																		 * not
																		 * roud
																		 * to
																		 * int
																		 */);

		Map<Object, Object> images = new HashMap<Object, Object>();
		parameters.put(JRHtmlExporterParameter.IMAGES_MAP, images);

		String imagesUri = String.format("jasper_image/salary/%d/%d/%d/%d/",
				cost.getMonth(), cost.getYear(), cost.getWorkplaceId(),
				cost.getEnterpriseId());

		parameters.put(JRHtmlExporterParameter.IMAGES_URI, imagesUri);
		

		String html = getSalaryReceiptHTML(domain, cost, types, parameters);

		for (Entry<Object, Object> image : images.entrySet()) {
			String name = String.format("%s%s", imagesUri, image.getKey());
			JasperImageServlet.saveImage(name, (byte[]) image.getValue());
		}

		return html;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getCostReceiptHTML(String domain, Cost cost, Salary.Type types[], int zoom)
			throws IllegalArgumentException {
		try {
			ReportManager reportManager = new StatelessReportManager();
			reportManager.setOutputFormat(OutputFormat.HTML);

			SalaryType salaryTypes[] = new SalaryType[types.length];
			for (int i = 0; i < types.length; i++)
				salaryTypes[i] = SalaryType.values()[types[i].ordinal()];

			reportManager.setCollectionProvider(getSalariesProvider(
					domain,
					cost,
					salaryTypes,
					false));

			Map<Object, Object> parameters = new HashMap<Object, Object>(
					JR_HTML_EXPORTER_PARAMS);

			// Really I hate this spaghetti piece of code.
			// For pass 'month' & 'year' to a report, we
			// must put it in a controller ?????.
			//SalaryExpenseController controller = (SalaryExpenseController) AonUtil
			//		.getRegisteredBean(IPayrollConstants.SALARY_EXPENSE_CONTROLLER_NAME);
			//controller.setShowSalaryExpenseWindow(false);
			//controller.setYear(cost.getYear());
			//Month month = Month.getMonthByValue(cost.getMonth());
			//controller.setMonth(month);
			parameters.put("#{salaryExpense.getYear}", cost.getYear());
			parameters.put("#{salaryExpense.getMonth}", cost.getMonth());
			

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			

			parameters
					.put(JRHtmlExporterParameter.ZOOM_RATIO, zoom / 100.00f /*
																			 * not
																			 * round
																			 * to
																			 * int
																			 */);
			Map<Object, Object> images = new HashMap<Object, Object>();
			parameters.put(JRHtmlExporterParameter.IMAGES_MAP, images);

			String imagesUri = String.format(
					"jasper_image/salary/%d/%d/%d/%d/", cost.getMonth(),
					cost.getYear(), cost.getWorkplaceId(),
					cost.getEnterpriseId());

			parameters.put(JRHtmlExporterParameter.IMAGES_URI, imagesUri);

			reportManager.execute(out, IPayrollConstants.COST_REPORT,
					parameters);

			for (Entry<Object, Object> image : images.entrySet()) {
				String name = String.format("%s%s", imagesUri, image.getKey());
				JasperImageServlet.saveImage(name, (byte[]) image.getValue());
			}

			return out.toString();

		} catch (ReportException e) {
			throw new IllegalArgumentException(e);
		} catch (ManagerBeanException e) {
			throw new IllegalArgumentException(e);
		}

	}

	@Override
	public String getSalaryDraftReceiptHTML(String domain, SalaryDraft salaryDraft, int zoom)
			throws IllegalArgumentException {

		Map<Object, Object> parameters = new HashMap<Object, Object>(
				JR_HTML_EXPORTER_PARAMS);
		parameters.put(JRHtmlExporterParameter.ZOOM_RATIO, zoom / 100.00f /*
																		 * not
																		 * roud
																		 * to
																		 * int
																		 */);

		Map<Object, Object> images = new HashMap<Object, Object>();
		parameters.put(JRHtmlExporterParameter.IMAGES_MAP, images);
		String imagesUri = String.format("jasper_image/salary/%d/", salaryDraft
				.getEmployee().getId());

		parameters.put(JRHtmlExporterParameter.IMAGES_URI, imagesUri);

		String html = getSalaryDraftReceiptHTML(domain, salaryDraft, parameters);

		for (Entry<Object, Object> image : images.entrySet()) {
			String name = String.format("%s%s", imagesUri, image.getKey());
			JasperImageServlet.saveImage(name, (byte[]) image.getValue());
		}

		return html;

	}

	@Override
	public String getAgreementDraftReceiptHTML(String domain,AgreementDraft agreementDraft,
			int levelId, Salary.Type type , int zoom)
			throws IllegalArgumentException {

		Map<Object, Object> parameters = new HashMap<Object, Object>(
				JR_HTML_EXPORTER_PARAMS);
		parameters.put(JRHtmlExporterParameter.ZOOM_RATIO, zoom / 100.00f /*
																		 * not
																		 * roud
																		 * to
																		 * int
																		 */);

		Map<Object, Object> images = new HashMap<Object, Object>();
		parameters.put(JRHtmlExporterParameter.IMAGES_MAP, images);
		String imagesUri = String.format("jasper_image/agreement/%d/%d",
				agreementDraft.getId(), levelId);

		parameters.put(JRHtmlExporterParameter.IMAGES_URI, imagesUri);

		String html = getAgreementDraftReceiptHTML(domain, agreementDraft, levelId,
				type, parameters);

		for (Entry<Object, Object> image : images.entrySet()) {
			String name = String.format("%s%s", imagesUri, image.getKey());
			JasperImageServlet.saveImage(name, (byte[]) image.getValue());
		}

		return html;
	}

	@Override
	public String getIrpfDraftReceiptHTML(String domain, SalaryDraft salaryDraft, int zoom)
			throws IllegalArgumentException {

		Map<Object, Object> parameters = new HashMap<Object, Object>(
				JR_HTML_EXPORTER_PARAMS);
		parameters.put(JRHtmlExporterParameter.ZOOM_RATIO, zoom / 100.00f /*
																		 * not
																		 * roud
																		 * to
																		 * int
																		 */);

		parameters.put(JRParameter.REPORT_LOCALE, new Locale("es", "ES"));
		String html = getIrpfDraftReceiptHTML(domain, salaryDraft, parameters);

		return html;

	}

	@Override
	public List<Result> eval(String domain, String expression, SalaryDraft salaryDraft)
			throws IllegalArgumentException, EvalException {
		List<ITimedResult<Double>> results = eval(domain, expression, salaryDraft,
				Double.class);
		List<Result> returnList = new ArrayList<Result>(results.size());
		for (ITimedResult<Double> result : results) {
			returnList.add(new Result(cast(result), cast(result
					.getContext())));
		}
	
		return returnList;
	}

	@Override
	public List<Result> eval(String domain, String expression, AgreementDraft agreementDraft,
			int levelId) throws IllegalArgumentException, EvalException {
		List<ITimedResult<Double>> results = eval(domain, expression,
				agreementDraft, levelId, Double.class);

		List<Result> returnList = new ArrayList<Result>(results.size());
		for (ITimedResult<Double> result : results) {
			returnList.add(new Result(cast(result), cast(result
					.getContext())));
		}

		return returnList;
	}

	@Override
	public ContextDescriptor getContext(String domain, SalaryDraft salaryDraft) {
		//return new ContextDescriptor();
		return getDraftContext(domain, salaryDraft);
	}

	@Override
	public ContextDescriptor getContext(String domain, AgreementDraft agreementDraft,
		int levelId) throws IllegalArgumentException {
		return getDraftContext(domain, agreementDraft, levelId);
	}

	@Override
	public Double calculateIrpf(String domain, SalaryDraft salaryDraft)
			throws IllegalArgumentException {
		try {
			return 0.00;// getIrpf(salaryDraft);
		} finally {
		}

	}

	@Override
	public SalaryDraft calculateSalaryDraft(String domain, SalaryDraft salaryDraft)
			throws IllegalArgumentException {
		calculate(domain, salaryDraft, new SmartContractSalaryCalculator<ISalary>());
		return salaryDraft;
	}

	@Override
	public SalaryDraft calculateSalaryDraft(String domain, SalaryDraft salaryDraft ,Date sections [])
			throws IllegalArgumentException {
		
		long draftDays = new com.esferalia.aon.salary.expression.Period(salaryDraft.getStartDate(),salaryDraft.getEndDate())
		.daysStream().count(); 
		
		calculate(domain, salaryDraft, new SmartContractSalaryCalculator<ISalary>() {
			
			@Override
			protected List<ITimedResult<Double>> fixConstantResult(IContractPayment contractPayment,
					ITimedResult<Double> result, Date start, Date end, ExpressionContext expressionContext)
					throws UnsupportedOperationException, UndefinedVariablesException {
				long sectionDays = new com.esferalia.aon.salary.expression.Period(start, end).daysStream().count();
				ITimedResult<Double> sectionResult = new TimedResult<Double>(result.getValue() / draftDays * sectionDays, result.getPeriod(), result.getContext());
				return super.fixConstantResult(contractPayment, sectionResult, start, end, expressionContext);
			}
			
			@Override
			protected List<ITimedResult<Double>> fixConstantAgreementResult(IContractPayment contractPayment,
					ITimedResult<Double> result, Date start, Date end, ExpressionContext expressionContext)
					throws UnsupportedOperationException, UndefinedVariablesException {

				long days = getDays(result.getPeriod());
				double monthDays = com.esferalia.aon.watson.util.AonDateUtils
						.get(getLastDayOfMonth(result.getPeriod().getEnd()), Calendar.DAY_OF_MONTH);
				
				double factor = getPartialFactor(expressionContext, result.getPeriod());
				
				double value = result.getValue() * days / monthDays * factor;
				
				ITimedResult<Double> fixed = 
						new TimedResult<Double>(value, result.getPeriod(), result.getContext());

				return Collections.singletonList(fixed);
			}
			
			
		}, sections);
		return salaryDraft;
	}

	@Override
	public SalaryDraft calculateSalaryDraft4Dummies(String domain, SalaryDraft salaryDraft)
			throws IllegalArgumentException {
		calculate(domain, salaryDraft, new SmartContractSalaryCalculator<ISalary>());
		return salaryDraft;
	}

	@Override
	public Map<String, String> getEventsVariables(String domain, Integer workplaceId,
			Integer agreementId, Date startDate, Date endDate)
			throws IllegalArgumentException {
		if (agreementId == null)
			return Collections.emptyMap();

		try{
			Integer domainID = AonServletUtils.getDomainID(domain);
			Integer parentDomainID = AonServletUtils.getParentDomainID(domain);
			
			return getWorkplaceEventsVariables(domain, workplaceId, agreementId,
					startDate, endDate, domainID, parentDomainID);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} 

	}
	
	@Override
	public Map<String, String> getWorkplaceEventsVariables(String domain, Integer workplaceId,
			Integer agreementId, Date startDate, Date endDate) throws IllegalArgumentException {
		if (agreementId == null)
			return Collections.emptyMap();

		try{
			Integer domainID = AonServletUtils.getDomainID(domain);
			Integer parentDomainID = AonServletUtils.getParentDomainID(domain);
			
			return getEventsVariables(domain, workplaceId, agreementId,
					startDate, endDate, domainID, parentDomainID);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} 
	}


	@Override
	public ContextDescriptor getEmployeeEventsVariables(String domain, Integer employeeId, Date startDate, Date endDate)
			throws IllegalArgumentException {

		Connection connection = null;

		try {
			System.out.println("ENTRANDO PARA BUSCAR VARIABLES, Domain : " + domain + ", Employee Id : " + employeeId 
					+ ", StartDate : " + startDate + ", endDate : " + endDate );
			
			connection = AonServletUtils.getConnection(domain);
			Integer domainID = AonServletUtils.getDomainID(domain);
			Integer parentDomainID = AonServletUtils.getParentDomainID(domain);

			Integer agreementId = SQLEvents.getAgreementId(connection, employeeId);

			Criteria criteria = new Criteria();
			criteria.addEqualExpression(SQLConstants.CONTRACT + "." + ContractColumns.ID, employeeId);

			SQLContractSalaryCalculatorContext context = new SQLContractSalaryCalculatorContext(connection, startDate, endDate, endDate, criteria);
			context.next();

			ContextDescriptor contextDescriptor = getContext(connection, context, startDate,  endDate);

			ContextDescriptor contextDescriptorPayments = getEmployeePayments(connection, employeeId, agreementId,
					startDate, endDate, domainID, parentDomainID);

			contextDescriptorPayments.mix(contextDescriptor);
			ContextDescriptor contextResult = new ContextDescriptor();

			for (String key : contextDescriptorPayments.getVariables()){
				if(!contextDescriptorPayments.getList(key).isEmpty()){
					contextResult.add(key, contextDescriptorPayments.getList(key));
					continue;
				}

				for(VariableDescriptor variable : contextDescriptorPayments.getList(key)){
					if (Number.class != variable.getType())
						continue;
					if (Scope.AGREEMENT == variable.getScope())
						continue;

					contextResult.add(key, variable);
				}
			}

			System.out.println("Context Variables Size : " +contextResult.getVariables().size());
			
			for(String key : contextResult.getVariables()){
				if(contextResult.getList(key).isEmpty()){
					System.out.println("RESULT :"+key+", value : null, type :null, startDate :null, endDate :null");
					continue;
				}
				for(VariableDescriptor var : contextResult.getList(key))
					System.out.println("RESULT :"+key+", value :"+var.getValue()+", type :"+var.getType()+", startDate :"+var.getStartDate()+", endDate :"+var.getEndDate());
			}

			return contextResult;

		} catch (SQLException | ExpressionException e) {
			throw new IllegalArgumentException(e);
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
	public List<Variable> getVariables(String domain, SalaryDraft salaryDraft, Date startDate,
			Date endDate, String[] names) throws IllegalArgumentException {
		Connection connection = null;
		try {
			salaryDraft.setStartDate(startDate);
			salaryDraft.setEndDate(endDate);
			connection = AonServletUtils.getConnection(domain);
			SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext> ctx = EmployeesServiceHelper.getSalaryCalculatorContext(
					connection, salaryDraft, null);

			Map<String, boolean[]> defined = EmployeesServiceHelper
					.getDefinedMap(ctx);

			List<Variable> variables = new LinkedList<Variable>();
			for (String name : names) {

				List<ITimedResult<Object>> results  = Collections.emptyList();
				try {
					results = ctx.getExpressionContext().eval(name, startDate, endDate);
				} catch ( DeferredException e ){
					e.eval(ctx.getExpressionContext(), Object.class );
					results = ctx.getExpressionContext().eval(name, startDate, endDate);
				} catch ( Exception  e) {
					continue;
				}


				for (ITimedResult<Object> var : results) {

					IExpression expression = var instanceof IExpressionVariable<?> ? ((IExpressionVariable<?>) var)
							.getExpression() : null;


					ContextVariable contextVariable = ContextVariable
							.getVariableByName(name);
					try {

						Object value = var.getValue(var.getPeriod());

						if (value instanceof String || value instanceof Boolean
								|| value instanceof Number)
							variables
									.add(new StringVariable.Builder()
											.setName(name)
											.setStartDate(
													var.getPeriod().getStart())
											.setEndDate(
													var.getPeriod().getEnd())
											.setValue(
													var.getValue(var
															.getPeriod()))
											.setDefined(defined.get(name))
											.setExpression(
													expression != null ? expression
															.getExpression()
															: null)
											.setImplicit(
													contextVariable != null
															&& contextVariable
																	.isInternal())
											.setScope(
													(expression != null && expression
															.getScope() != null) ? Scope
															.values()[expression
															.getScope()
															.ordinal()]
															: null).create());
					} catch (ExpressionExceptionWrapper e) {
						e.printStackTrace();
					}

				}
			}


			return variables;

		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} catch (ExpressionException e) {
			throw new IllegalArgumentException(e);
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
	public AgreementDraft calculateAgreementDraft(String domain, AgreementDraft agreementDraft)
			throws IllegalArgumentException {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			EmployeesServiceHelper.calculate(
					connection,
					agreementDraft,
					AonServletUtils.getDomainID(domain),
					agreementDraft.getDomain());
			return agreementDraft;
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
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
	public AgreementDraft saveAgreementDraft(String domain, AgreementDraft agreementDraft)
			throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			disableAutoCommit(conn);
			SQLAgreementDraft.save(conn, agreementDraft,
					agreementDraft.getDomain() , //AonServletUtils.getDomainID(domain),
					AonServletUtils.getParentDomainID(conn, agreementDraft.getDomain()));
			commit(conn);
			return agreementDraft;
		} catch (Throwable t) {
			rollback(conn);
			t.printStackTrace();
			throw new IllegalArgumentException(t);
		} finally {
			enableAutoCommit(conn);
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}

	@Override
	public void saveITDataPerson(String domain,
			Map<Integer, LinkedHashMap<Integer, ITDataPerson>> inserts,
			Map<Integer, LinkedHashMap<Integer, ITDataPerson>> deletes,
			Map<Integer, LinkedHashMap<Integer, ITDataPerson>> updates)
			throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			Integer domainId = AonServletUtils.getDomainID(domain);
			disableAutoCommit(conn);
			SQLITData.save(conn, domainId, inserts, deletes, updates);
			commit(conn);
		} catch (SQLException ex) {
			rollback(conn);
			ex.printStackTrace();
		} finally {
			enableAutoCommit(conn);
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException ex) {
				}
			}
		}
	}

	@Override
	public Map<String, String> getAvaiableEmployees(String domain)
			throws IllegalArgumentException {

		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			Integer domainID = AonServletUtils.getDomainID(domain);
			Integer parentDomainID = AonServletUtils.getParentDomainID(domain);
			
			disableAutoCommit(conn);
			return JooqEmployees.getAvaiableEmployees(conn, domainID,
					parentDomainID);
		} catch (SQLException ex) {
			rollback(conn);
			ex.printStackTrace();
			throw new IllegalArgumentException(ex);
		} finally {
			enableAutoCommit(conn);
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException ex) {
				}
			}
		}

	}

	@Override
	public Employee pasteContract(String domain, int workplaceId, int contractId,
			String document, Date startDate, Date endDate, boolean check)
			throws IllegalArgumentException {

		Connection conn = null;
		try {

			conn = AonServletUtils.getConnection(domain);
			Integer domainID = AonServletUtils.getDomainID(domain);
			disableAutoCommit(conn);
			Employee employee = JooqEmployees.paste(conn, domainID,
					workplaceId, contractId, document, startDate, endDate,
					check);
			commit(conn);

			return employee;

		} catch (SQLException ex) {
			rollback(conn);
			ex.printStackTrace();
			throw new IllegalArgumentException(ex);
		} finally {
			enableAutoCommit(conn);
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException ex) {
				}
			}
		}

	}

	@Override
	public void moveContractId(String domain, Employee employee)
			throws IllegalArgumentException {
		Connection conn = null;

		try {
			conn = AonServletUtils.getConnection(domain);
			disableAutoCommit(conn);
			JooqEmployees.moveContractId(conn, employee.getId());
			commit(conn);
		} catch (SQLException ex) {
			rollback(conn);
			ex.printStackTrace();
			throw new IllegalArgumentException(ex);
		} finally {
			enableAutoCommit(conn);
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException ex) {
				}
			}
		}
	}

	@Override
	public void deleteContract(String domain, Employee employee)
			throws IllegalArgumentException {

		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			disableAutoCommit(conn);
			JooqEmployees.delete(conn, employee.getId());
			commit(conn);
		} catch (SQLException ex) {
			rollback(conn);
			ex.printStackTrace();
			throw new IllegalArgumentException(ex);
		} finally {
			enableAutoCommit(conn);
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException ex) {
				}
			}
		}

	}

	@Override
	public void saveSalaryDraft(String domain, SalaryDraft salaryDraft)
			throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			Integer domainID = AonServletUtils.getDomainID(domain);
			Integer parentDomainID = AonServletUtils.getParentDomainID(domain);
			disableAutoCommit(conn);
			SQLSalaryDraft.save(conn, salaryDraft, domainID,
					parentDomainID);
			commit(conn);
		} catch (SQLException e) {
			rollback(conn);
			throw new IllegalArgumentException(e);
		} finally {
			enableAutoCommit(conn);
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}

	}

	@Override
	public SalaryDraft saveSalary(String domain, SalaryDraft salaryDraft)
			throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			calculateAndSave(conn, salaryDraft);
			return salaryDraft;
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}

	}

	@Override
	public SalaryDraft saveSalary(String domain, SalaryDraft salaryDraft, Date sections [])
			throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			calculateAndSave(conn, salaryDraft, sections);
			return salaryDraft;
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public String getSalaryDraftReceipt(String domain, final SalaryDraft draft, String mime)
			throws IllegalArgumentException {

		try {
			ReportManager reportManager = new StatelessReportManager();
			reportManager.setOutputFormat(OutputFormat.PDF);

			ICollectionProvider provider = new ICollectionProvider() {

				@Override
				public Collection<?> getCollection() {
					try {
						return getCollection(true);
					} catch (ManagerBeanException e) {
									return null;
					}
				}

				@Override
				public Collection<?> getCollection(boolean arg0)
						throws ManagerBeanException {
					return Collections.singletonList(getSalary(domain, draft));
				}

			};

			reportManager.setCollectionProvider(provider);

			ByteArrayOutputStream reportOut = new ByteArrayOutputStream();

			String salaryReport = getSalaryReport(domain, toSalaryType(draft.getType()));

			reportManager.execute(reportOut, salaryReport);

			byte reportByteArray[] = reportOut.toByteArray();

			ByteArrayInputStream reportInput = new ByteArrayInputStream(
					reportByteArray);

			Writer stringWriter = new StringWriter();
			encodeURIComponent(mime, reportInput, stringWriter);

			reportOut.close();
			reportInput.close();
			stringWriter.flush();
			String dataUri = stringWriter.toString();
			stringWriter.close();

			return dataUri;

		} catch (ReportException e) {
			throw new IllegalArgumentException(e);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getIrpfDraftReceipt(String domain, final SalaryDraft draft, String mime)
			throws IllegalArgumentException {

		try {
			ReportManager reportManager = new StatelessReportManager();
			reportManager.setOutputFormat(OutputFormat.PDF);

			ICollectionProvider provider = new ICollectionProvider() {

				@Override
				public Collection<?> getCollection() {
					try {
						return getCollection(true);
					} catch (ManagerBeanException e) {
									return null;
					}
				}

				@Override
				public Collection<?> getCollection(boolean arg0)
						throws ManagerBeanException {
					return Collections.singletonList(getIrpfOutcome(domain, draft));
				}

			};

			reportManager.setCollectionProvider(provider);

			ByteArrayOutputStream reportOut = new ByteArrayOutputStream();
			reportManager.execute(reportOut, "irpf");

			byte reportByteArray[] = reportOut.toByteArray();

			ByteArrayInputStream reportInput = new ByteArrayInputStream(
					reportByteArray);

			Writer stringWriter = new StringWriter();
			encodeURIComponent(mime, reportInput, stringWriter);

			reportOut.close();
			reportInput.close();
			stringWriter.flush();
			String dataUri = stringWriter.toString();
			stringWriter.close();

			return dataUri;

		} catch (ReportException e) {
			throw new IllegalArgumentException(e);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}

	}

	@Override
	public String getSalaryPreviewReceiptHTML(String domain, SalaryPreview salaryPreview,
			int zoom) throws IllegalArgumentException {
		Map<Object, Object> parameters = new HashMap<Object, Object>(
				JR_HTML_EXPORTER_PARAMS);
		parameters.put(JRHtmlExporterParameter.ZOOM_RATIO, zoom / 100.00f /*
																		 * not
																		 * roud
																		 * to
																		 * int
																		 */);

		Map<Object, Object> images = new HashMap<Object, Object>();
		parameters.put(JRHtmlExporterParameter.IMAGES_MAP, images);
		String imagesUri = String.format("jasper_image/salary/%d/",
				salaryPreview.getEmployee().getId());

		parameters.put(JRHtmlExporterParameter.IMAGES_URI, imagesUri);

		String html = getSalaryPreviewReceiptHTML(domain, salaryPreview, parameters);

		for (Entry<Object, Object> image : images.entrySet()) {
			String name = String.format("%s%s", imagesUri, image.getKey());
			JasperImageServlet.saveImage(name, (byte[]) image.getValue());
		}

		return html;
	}

	@Override
	public List<Payment> getAvailablePayments(String domain, int employeeId)
			throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);

			int domainId = AonServletUtils.getDomainID(domain);


			List<Payment> paymentConcepts = JooqPayments.getPaymentConcepts(
					conn, 0, 0);
//			List<Payment> paymentConcepts = JooqPayments.getPaymentConcepts(
//					conn, domainId, AonServletUtils.getParentDomainID(domain));
			List<Payment> employeePayments = Collections.emptyList();
			/* getEmployeePayments(conn, employeeId); */
			List<Payment> enterprisePayments = Collections.emptyList();
			/* getEnterprisePayments(conn, domainId); */
			List<Payment> systemPayments = JooqPayments.getPayments(conn, 0,0);
			systemPayments = systemPayments.stream()
			.filter(p -> !isDefault(p))
			.collect(Collectors.toList() );

			paymentConcepts  = sub(paymentConcepts, systemPayments);
			
			List<Payment> payments = new ArrayList<Payment>(
					paymentConcepts.size() + employeePayments.size()
							+ enterprisePayments.size());

			payments.addAll(paymentConcepts);
			payments.addAll(employeePayments);
			payments.addAll(enterprisePayments);

			return payments;

		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}



	@Override
	public List<Deduction> getAvailableDeductions(String domain, int employeeId)
			throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			Integer domainId = AonServletUtils.getDomainID(domain);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domain);

			return JooqDeductions.getConcepts(conn, domainId,
					parentDomainId);

		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}

	@Override
	public List<Bonus> getAvailableBonuses(String domain, int employeeId)
			throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			List<Bonus> availableBonuses = new ArrayList<Bonus>();
			for (Bonus bonus : EmployeesServiceHelper.getAvailableBonuses(conn,
					employeeId, 0))
				if (bonus.getType() == null)
					availableBonuses.add(bonus);

			return availableBonuses;

		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}

	@Override
	public void saveEvents(String domain, Events events, Date startDate, Date endDate) {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			int domainId = AonServletUtils.getDomainID(domain);
			SQLEvents.completeEvents(conn, events, startDate, endDate);
			SQLEvents.saveEvents(conn, events, startDate, endDate, domainId);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}

	@Override
	public Events getEvents(String domain, Integer workplaceId, Date startDate, Date endDate,
			int offset, int limit, String names[]) {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			return SQLEvents.getEvents(conn, workplaceId, startDate, endDate,
					offset, limit, names);

		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}

	}

	@Override
	public Period getAvailPeriod(String domain, Integer workplaceId, String name)
			throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			return SQLEvents.getAvailPeriod(conn, workplaceId, name);

		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}

	@Override
	public void delete(String domain, Salary[] salaries) throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);

			int ids[] = new int[salaries.length];
			for (int i = 0; i < salaries.length; i++)
				ids[i] = salaries[i].getId();

			deleteSalaries(conn, ids);

		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}

	@Override
	public Statistics getEnterpriseStats(String domain, int enterpriseId)
			throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);

			return SQLStatistics.getEnterpriseStats(conn, enterpriseId);

		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}

	@Override
	public Statistics getWorkplaceStats(String domain, int workplaceId)
			throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);

			return SQLStatistics.getWorkplaceStats(conn, workplaceId);

		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}

	@Override
	public ITData getEnterpriseITData(String domain, int enterpriseId)
			throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);

			return SQLITData.getEnterpriseITData(conn, enterpriseId);

		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}

	@Override
	public ITData getWorkplaceITData(String domain,int workplaceId)
			throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			return SQLITData.getWorplaceItTData(conn, workplaceId);

		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}

	@Override
	public SortedSet<Date> getChanges(String domain, Agreement agreement)
			throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);

			Integer domainId = AonServletUtils.getDomainID(domain);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domain);

			return parentDomainId != null ? SQLAgreementDraft
					.getDatesWithChanges(conn, agreement.getId(), domainId,
							parentDomainId,agreement.getDomain()) : SQLAgreementDraft
					.getDatesWithChanges(conn, agreement.getId(), domainId, agreement.getDomain());

		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}

	// -------------------------------------------------------- Private methods


	private String getSalaryReport(String domain, SalaryType salaryType)
			throws ReportException {
		Connection conn = null;
		try {
			conn = getConnection(domain);
			int enterpriseId = AonServletUtils.getEnterpriseID(domain);
			return PayrollServletUtils.getSalaryReport(conn, enterpriseId,
					salaryType != null ? salaryType : SalaryType.SALARY);
		} catch (SQLException e) {
			throw new ReportException(e.getLocalizedMessage());
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private String getSalaryReceiptHTML(String domain, Salary salary,
			Map<Object, Object> parameters) throws IllegalArgumentException {

		try {

			String salaryReport = getSalaryReport(domain, toSalaryType(salary.getType()));

			Condition condition = com.esferalia.aon.jooq.tables.Salary.SALARY.ID.eq(salary.getId());
			
			FacesContext ctx = FacesContext.getCurrentInstance();
			ctx.getViewRoot().setLocale(new Locale("es", "ES"));
			ReportManager reportManager = new StatelessReportManager();

			reportManager.setOutputFormat(OutputFormat.HTML);
			reportManager
					.setCollectionProvider(new PayrollServletUtils.SalaryProvider(
							domain, condition, null));

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			reportManager.execute(out, salaryReport, parameters);

			return out.toString();

		} catch (ReportException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private String getSalaryReceiptHTML(String domain, Cost cost, Salary.Type types[],
			Map<Object, Object> parameters) throws IllegalArgumentException {
		try {
			ReportManager reportManager = new StatelessReportManager();
			reportManager.setOutputFormat(OutputFormat.HTML);

			SalaryType salaryTypes[] = new SalaryType[types.length];
			for (int i = 0; i < types.length; i++)
				salaryTypes[i] = SalaryType.values()[types[i].ordinal()];

			reportManager.setCollectionProvider(getSalariesProvider(
					domain,
					cost,
					salaryTypes,
					false ));

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			// TODO: SalaryType???????
			String salaryReport = getSalaryReport(domain, SalaryType.SALARY);
			// TODO: Bufff ThreadLocal ...
			ReportUtils.domain.set(domain);
			
			reportManager.execute(out, salaryReport, parameters);

			return out.toString();

		} catch (ReportException e) {
			throw new IllegalArgumentException(e);
		} catch (ManagerBeanException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private String getSalaryPreviewReceiptHTML(String domain, final SalaryPreview draft,
			Map<Object, Object> parameters) throws IllegalArgumentException {

		try {
			ReportManager reportManager = new StatelessReportManager();
			reportManager.setOutputFormat(OutputFormat.HTML);

			// ISalary salary = getSalary(draft);
			// ICollectionProvider provider = new CollectionProvider(salary);

			ICollectionProvider provider = new ICollectionProvider() {

				@Override
				public Collection<?> getCollection() {
					try {
						return getCollection(true);
					} catch (ManagerBeanException e) {
									return null;
					}
				}

				@Override
				public Collection<?> getCollection(boolean arg0)
						throws ManagerBeanException {
					return Collections.singletonList(getSalary(domain, draft));
				}

			};

			reportManager.setCollectionProvider(provider);

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			String salaryReport = getSalaryReport(domain, toSalaryType(draft.getType()));
			reportManager.execute(out, salaryReport, parameters);

			return out.toString();

		} catch (ReportException e) {
			throw new IllegalArgumentException(e);
		}	}

	@SuppressWarnings("unchecked")
	private String getSalaryDraftReceiptHTML(String domain, final SalaryDraft draft,
			Map<Object, Object> parameters) throws IllegalArgumentException {

		try {
			ReportManager reportManager = new StatelessReportManager();
			reportManager.setOutputFormat(OutputFormat.HTML);

			ICollectionProvider provider = new ICollectionProvider() {

				@Override
				public Collection<?> getCollection() {
					try {
						return getCollection(true);
					} catch (ManagerBeanException e) {
						return null;
					}
				}

				@Override
				public Collection<?> getCollection(boolean arg0)
						throws ManagerBeanException {
					return Collections.singletonList(getSalary(domain, draft));
				}

			};

			reportManager.setCollectionProvider(provider);

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			String salaryReport = getSalaryReport(domain, toSalaryType(draft.getType()));
			reportManager.execute(out, salaryReport, parameters);

			return out.toString();

		} catch (ReportException e) {
			throw new IllegalArgumentException(e);
		}

	}

	@SuppressWarnings("unchecked")
	private String getAgreementDraftReceiptHTML(String domain, final AgreementDraft draft,
			final int levelId, Salary.Type type, Map<Object, Object> parameters)
			throws IllegalArgumentException {

		try {

			ReportManager reportManager = new StatelessReportManager();
			reportManager.setOutputFormat(OutputFormat.HTML);

			ICollectionProvider provider = new ICollectionProvider() {

				@Override
				public Collection<?> getCollection() {
					try {
						return getCollection(true);
					} catch (ManagerBeanException e) {
									return null;
					}
				}

				@Override
				public Collection<?> getCollection(boolean arg0)
						throws ManagerBeanException {
					return Collections.singletonList(getSalary(domain, draft, levelId));
				}

			};

			reportManager.setCollectionProvider(provider);
			reportManager.setBundle(ResourceBundle.getBundle("com.code.aon.common.i18n.messages"));

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			String salaryReport = getSalaryReport(domain, toSalaryType(type));
			reportManager.execute(out, salaryReport, parameters);

			return out.toString();

		} catch (ReportException e) {
			throw new IllegalArgumentException(e);
		} finally {
		}

	}

	@SuppressWarnings("unchecked")
	private String getIrpfDraftReceiptHTML(String domain, final SalaryDraft draft,
			Map<Object, Object> parameters) throws IllegalArgumentException {

		try {			FacesContext ctx = FacesContext.getCurrentInstance();
			ctx.getViewRoot().setLocale(new Locale("es", "ES"));

			ReportManager reportManager = new StatelessReportManager();
			reportManager.setOutputFormat(OutputFormat.HTML);

			ICollectionProvider provider = new ICollectionProvider() {

				@Override
				public Collection<?> getCollection() {
					try {
						return getCollection(true);
					} catch (ManagerBeanException e) {
									return null;
					}
				}

				@Override
				public Collection<?> getCollection(boolean arg0)
						throws ManagerBeanException {
					return Collections.singletonList(getIrpfOutcome(domain, draft));
				}

			};

			reportManager.setCollectionProvider(provider);

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			String irpfReport = "irpf";
			reportManager.execute(out, irpfReport, parameters);

			return out.toString();

		} catch (ReportException e) {
			throw new IllegalArgumentException(e);
		}
	}

	// ------------------------------------------------- Private Static methods

	// Note that below methods can be moved to another place safely.

	private ICollectionProvider getSalariesProvider(String domain, Cost cost,
			SalaryType types[]) throws ManagerBeanException {
		boolean asEnterpriseSite = notAtEnterpriseSite();
		boolean calc = !notAtEnterpriseSite();
		SalaryFilter filter = asEnterpriseSite ? new SiteFilter() : null;
		return getSalariesProvider(domain, cost, types, filter, calc);
	}

	private ICollectionProvider getSalariesProvider(String domain, Cost cost,
			SalaryType types[], boolean calc) throws ManagerBeanException {
		boolean asEnterpriseSite = notAtEnterpriseSite();
		SalaryFilter filter = asEnterpriseSite ? new SiteFilter() : null;
		return getSalariesProvider(domain, cost, types, filter, calc);
	}

	protected static ICollectionProvider getSalariesProvider(String domain, Cost cost,
			SalaryType types[], SalaryFilter filter, boolean calc)
			throws ManagerBeanException {
		
		Condition condition = null;
		Criteria sqlCriteria = new Criteria();

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, cost.getYear());
		calendar.set(Calendar.MONTH, cost.getMonth());
		calendar.set(Calendar.DAY_OF_MONTH, 1); // The first day of the month
												// has value 1.
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		Date startDate = calendar.getTime();

		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date endDate = calendar.getTime();

		if (cost.getWorkplaceId() != 0) {

			sqlCriteria.addEqualExpression(WORKPLACE + "."
					+ WorkplaceColumns.ID, cost.getWorkplaceId());
			condition = com.esferalia.aon.jooq.tables.Workplace.WORKPLACE.ID.eq(cost.getWorkplaceId());
		} else {
			sqlCriteria.addEqualExpression(ENTERPRISE + "."
					+ EnterpriseColumns.REGISTRY, cost.getEnterpriseId());
			condition = com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE.REGISTRY.eq(cost.getEnterpriseId());
		}

		sqlCriteria.addLessThanOrEqualExpression(CONTRACT + "."
				+ ContractColumns.START_DATE, endDate);
		sqlCriteria.addExpression(ExpressionUtilities.getOrExpression(
				ExpressionUtilities.getNullExpression(CONTRACT + "."
						+ ContractColumns.END_DATE),
				ExpressionUtilities.getGreaterThanOrEqualExpression(CONTRACT
						+ "." + ContractColumns.END_DATE, startDate)));

		sqlCriteria
				.addOrder(SQLConstants.WORKPLACE + "." + WorkplaceColumns.ID);
		sqlCriteria.addOrder(SQLContractSalaryCalculatorContext.PERSON_REGISTRY
				+ "." + RegistryColumns.NAME);

		java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
		java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());

		condition = condition.and(com.esferalia.aon.jooq.tables.Salary.SALARY.CHARGE_DATE.between(sqlStartDate, sqlEndDate));

		
		SortField<?> sortFields [] = {
			com.esferalia.aon.jooq.tables.Workplace.WORKPLACE.ID.asc(),
			com.esferalia.aon.jooq.tables.Salary.SALARY.EMPLOYEE_NAME.asc(),
			com.esferalia.aon.jooq.tables.Salary.SALARY.CHARGE_DATE.asc()
		};
		
		Byte btypes [] = new Byte [types.length];
		for ( int i = 0; i < types.length; i++ )
			btypes[i] = (byte) types[i].ordinal();
		condition = condition.and(com.esferalia.aon.jooq.tables.Salary.SALARY.TYPE.in(btypes));

		return calc ? new PayrollServletUtils.CalcSalaryProvider(
				domain,
				startDate,
				endDate, 
				sqlCriteria, 
				types,
				new PayrollServletUtils.SalaryProvider(domain, condition, filter, sortFields))
				: new PayrollServletUtils.SalaryProvider(domain, condition, filter, sortFields);

	}

	private static List<Salary> getSalaries(Connection connection,
			Integer contractId) throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {

			String sql = "SELECT * " + " FROM " + SALARY + " WHERE " + SALARY
					+ "." + SalaryColumns.CONTRACT + " = ?" + " ORDER BY "
					+ SALARY + "." + SalaryColumns.END_DATE + " ASC";

			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, contractId);
			rs = stmt.executeQuery();

			return getSalaries(rs);

		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				rs.close();
			}
		}
	}

	private static List<Salary> getSalaries(ResultSet rs) throws SQLException {

		List<Salary> salaries = new LinkedList<Salary>();
		while (rs.next()) {
			Salary salary = new Salary();
			salary.setId(rs.getInt(SalaryColumns.ID));

			salary.setStartDate(rs.getDate(SalaryColumns.START_DATE));
			salary.setEndDate(rs.getDate(SalaryColumns.END_DATE));
			salary.setIssueDate(rs.getDate(SalaryColumns.ISSUE_DATE));
			salary.setChargeDate(rs.getDate(SalaryColumns.CHARGE_DATE));

			salary.setType(getSalaryType((Integer) rs
					.getObject(SalaryColumns.TYPE)));

			salaries.add(salary);
		}

		return salaries;
	}

	private static List<Salary> getSiteSalaries(Connection connection,
			Integer contractId) throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {

			String sql = "SELECT * " + " FROM " + SALARY + " LEFT JOIN "
					+ SQLConstants.SALARY_DATA + " ON ( " + SQLConstants.SALARY
					+ "." + SalaryColumns.ID + " = " + SQLConstants.SALARY_DATA
					+ "." + SalaryDataColumns.SALARY + " AND "
					+ SQLConstants.SALARY_DATA + "." + SalaryDataColumns.NAME
					+ " =  ? " + ")" + " WHERE " + SALARY + "."
					+ SalaryColumns.CONTRACT + " = ?" + " AND ( "
					+ SQLConstants.SALARY_DATA + "." + SalaryDataColumns.ID
					+ " IS NULL " + " OR " + SQLConstants.SALARY_DATA + "."
					+ SalaryDataColumns.EXPRESSION + " <= UTC_DATE() ) "
					+ " ORDER BY " + SALARY + "." + SalaryColumns.END_DATE
					+ " ASC";
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, ContextVariable.ENTERPRISE_SITE_DATE.getName());
			stmt.setInt(2, contractId);
			rs = stmt.executeQuery();

			return getSalaries(rs);
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				rs.close();
			}
		}
	}

	private static List<Irpf> getIrpfOutcomes(Connection connection,
			Integer contractId) throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {

			String sql = "SELECT *" + " FROM " + IRPF_DATA + ", " + IRPF_RESULT
					+ " LEFT JOIN " + IRPF_REGULARIZATION + " USING ( "
					+ IrpfResultColumns.CONTRACT + ", "
					+ IrpfResultColumns.EFFECTIVE_DATE + " ) " + " WHERE "
					+ IRPF_DATA + "." + IrpfDataColumns.CONTRACT + " =  ? "
					+ " AND " + IRPF_DATA + "." + IrpfDataColumns.CONTRACT
					+ " = " + IRPF_RESULT + "." + IrpfResultColumns.CONTRACT
					+ " AND ( " + IRPF_DATA + "." + IrpfDataColumns.END_DATE
					+ " IS NULL" + " OR " + IRPF_DATA + "."
					+ IrpfDataColumns.END_DATE + " >= " + IRPF_RESULT + "."
					+ IrpfResultColumns.EFFECTIVE_DATE + " )";

			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, contractId);
			rs = stmt.executeQuery();

			List<Irpf> outcomes = new LinkedList<Irpf>();
			while (rs.next()) {
				Irpf outcome = new Irpf();

				IrpfData irpfData = new IrpfData();
				irpfData.setId(rs.getInt(IRPF_DATA + "." + IrpfDataColumns.ID));

				IrpfResult irpfResult = new IrpfResult();
				irpfResult.setId(rs.getInt(IRPF_RESULT + "."
						+ IrpfResultColumns.ID));

				outcome.setIrpfData(irpfData);
				outcome.setIrpfResult(irpfResult);

				// Be care that irpf regularization may not exist .
				Object irpfRegularizationId = rs.getObject(IRPF_REGULARIZATION
						+ "." + IrpfRegularizationColumns.ID);
				if (irpfRegularizationId != null) {
					IrpfRegularization irpfRegularization = new IrpfRegularization();
					irpfRegularization.setId((Integer) irpfRegularizationId);
					outcome.setIrpfRegularization(irpfRegularization);
				}

				outcomes.add(outcome);
			}
			// SELECT * FROM , irpf_data, irpf_result LEFT JOIN
			// irpf_regularization USING ( contract, effective_date ) WHERE
			// contract = ? AND contract = contract AND ( end_date IS NULL OR
			// end_date >= effective_date )
			return outcomes;

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}

		finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				rs.close();
			}
		}
	}

	private static List<Cost> getEnterpriseCosts(Connection connection,
			Integer enterpriseId) throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			String yearCol = "YEAR";
			String monthCol = "MONTH";

			// We asume that one enterprise one domain. This way SELECT it's
			// more clear.
			String sql = "SELECT" + " MONTH(" + SALARY + "."
					+ SalaryColumns.CHARGE_DATE + ") " + monthCol + ", YEAR("
					+ SALARY + "." + SalaryColumns.CHARGE_DATE + ") " + yearCol
					+ " FROM " + ENTERPRISE + ", " + SALARY + " WHERE "
					+ ENTERPRISE + "." + EnterpriseColumns.DOMAIN + " = "
					+ SALARY + "." + SalaryColumns.DOMAIN + " AND "
					+ ENTERPRISE + "." + EnterpriseColumns.REGISTRY + " = ?"
					+ " GROUP BY 1, 2" + " ORDER BY 2 , 1 ASC ";

			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, enterpriseId);
			rs = stmt.executeQuery();

			return getEnterpriseCosts(rs, enterpriseId, yearCol, monthCol);

		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				rs.close();
			}
		}
	}

	private static List<Cost> getSiteEnterpriseCosts(Connection connection,
			Integer enterpriseId) throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			String allCol = "ALLL";
			String yearCol = "YEAR";
			String monthCol = "MONTH";
			String showCol = "VISIBLES";
			String contractsCol = "CONTRACTS";

			// We asume that one enterprise one domain. This way SELECT it's
			// more clear.
			// @formatter:off
			String sql = "SELECT" + " MONTH(" + SALARY + "."
					+ SalaryColumns.CHARGE_DATE + ") " + monthCol + ", YEAR("
					+ SALARY + "." + SalaryColumns.CHARGE_DATE + ") " + yearCol
					+ ", COUNT(*) AS " + allCol + ",(COUNT( IF("
					+ SQLConstants.SALARY_DATA + "."
					+ SalaryDataColumns.EXPRESSION + " <= UTC_DATE(),1,NULL))"
					+ " +  COUNT( IF(" + SQLConstants.SALARY_DATA + "."
					+ SalaryDataColumns.ID + " IS NULL,1,NULL))) AS " + showCol
					+ ",(SELECT COUNT(*)" + " FROM " + CONTRACT + " WHERE "
					+ ContractColumns.DOMAIN + " = " + ENTERPRISE + "."
					+ EnterpriseColumns.DOMAIN + " AND " + CONTRACT + "."
					+ ContractColumns.START_DATE + " <= LAST_DAY(CHARGE_DATE) "
					+ " AND ( " + CONTRACT + "." + ContractColumns.END_DATE
					+ " IS NULL" + " OR " + CONTRACT + "."
					+ ContractColumns.END_DATE
					+ " >=  DATE_FORMAT(CHARGE_DATE, '%Y-%m-01') )) AS "
					+ contractsCol

					+ " FROM " + ENTERPRISE + ", " + SALARY + " LEFT JOIN "
					+ SQLConstants.SALARY_DATA + " ON ( " + SQLConstants.SALARY
					+ "." + SalaryColumns.ID + " = " + SQLConstants.SALARY_DATA
					+ "." + SalaryDataColumns.SALARY + " AND "
					+ SQLConstants.SALARY_DATA + "." + SalaryDataColumns.NAME
					+ " =  ? " + ")" + " WHERE " + ENTERPRISE + "."
					+ EnterpriseColumns.DOMAIN + " = " + SALARY + "."
					+ SalaryColumns.DOMAIN + " AND " + ENTERPRISE + "."
					+ EnterpriseColumns.REGISTRY + " = ? " + " GROUP BY 1, 2"
					+ " HAVING " + showCol + " >= 1" + " ORDER BY 2 , 1 ASC ";
			// @formatter:on

			stmt = connection.prepareStatement(sql);
			stmt.setString(1, ContextVariable.ENTERPRISE_SITE_DATE.getName());
			stmt.setInt(2, enterpriseId);
			rs = stmt.executeQuery();

			return getEnterpriseCosts(rs, enterpriseId, yearCol, monthCol);

		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	private static List<Cost> getEnterpriseCosts(ResultSet rs,
			Integer enterpriseId, String yearCol, String monthCol)
			throws SQLException {
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
			cost.setMonth(month - 1);
			cost.setEnterpriseId(enterpriseId);

			costs.add(cost);
		}

		return costs;
	}

	private static List<Cost> getWorkplaceCosts(Connection connection,
			Integer workplaceId) throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			String yearCol = "YEAR";
			String monthCol = "MONTH";

			String sql = "SELECT" + " MONTH(" + SALARY + "."
					+ SalaryColumns.CHARGE_DATE + ") " + monthCol + ", YEAR("
					+ SALARY + "." + SalaryColumns.CHARGE_DATE + ") " + yearCol
					+ " FROM " + WORKPLACE + ", " + CONTRACT + ", " + SALARY
					+ " WHERE" + " " + WORKPLACE + "." + WorkplaceColumns.ID
					+ " = " + CONTRACT + "." + ContractColumns.WORKPLACE
					+ " AND " + CONTRACT + "." + ContractColumns.ID + " = "
					+ SALARY + "." + SalaryColumns.CONTRACT + " AND "
					+ WORKPLACE + "." + WorkplaceColumns.ID + " = ?"
					+ " GROUP BY 1, 2" + " ORDER BY 2 , 1 ASC ";

			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, workplaceId);
			rs = stmt.executeQuery();

			return getWorkplaceCosts(rs, workplaceId, yearCol, monthCol);
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				rs.close();
			}
		}
	}

	private static List<Cost> getSiteWorkplaceCosts(Connection connection,
			Integer workplaceId) throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			String yearCol = "YEAR";
			String monthCol = "MONTH";
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

					+ ", MIN("
					+ SALARY
					+ "."
					+ SalaryColumns.CHARGE_DATE
					+ ") AS CHARGE_DATE"
					+ ", COUNT(*) AS SALARIES "
					+ ",(COUNT( IF("
					+ SQLConstants.SALARY_DATA
					+ "."
					+ SalaryDataColumns.EXPRESSION
					+ " <= UTC_DATE(),1,NULL)) +  COUNT( IF("
					+ SQLConstants.SALARY_DATA
					+ "."
					+ SalaryDataColumns.ID
					+ " IS NULL,1,NULL))) AS VISIBLES"
					+ ",(SELECT COUNT(*) FROM "
					+ CONTRACT
					+ " WHERE "
					+ ContractColumns.WORKPLACE
					+ " = "
					+ WORKPLACE
					+ "."
					+ WorkplaceColumns.ID
					+ " AND "
					+ CONTRACT
					+ "."
					+ ContractColumns.START_DATE
					+ " <= LAST_DAY(CHARGE_DATE) "
					+ " AND ( "
					+ CONTRACT
					+ "."
					+ ContractColumns.END_DATE
					+ " IS NULL OR "
					+ CONTRACT
					+ "."
					+ ContractColumns.END_DATE
					+ " >=  DATE_FORMAT(CHARGE_DATE, '%Y-%m-01') )) AS CONTRACTS"

					+ " FROM " + WORKPLACE + ", " + CONTRACT + ", " + SALARY

					+ " LEFT JOIN " + SQLConstants.SALARY_DATA + " ON ( "
					+ SQLConstants.SALARY + "." + SalaryColumns.ID + " = "
					+ SQLConstants.SALARY_DATA + "." + SalaryDataColumns.SALARY
					+ " AND " + SQLConstants.SALARY_DATA + "."
					+ SalaryDataColumns.NAME + " =  ? " + ")"

					+ " WHERE" + " " + WORKPLACE + "." + WorkplaceColumns.ID
					+ " = " + CONTRACT + "." + ContractColumns.WORKPLACE
					+ " AND " + CONTRACT + "." + ContractColumns.ID + " = "
					+ SALARY + "." + SalaryColumns.CONTRACT + " AND "
					+ WORKPLACE + "." + WorkplaceColumns.ID + " = ?"
					+ " GROUP BY 1, 2"

					+ " HAVING VISIBLES >= 1" + " ORDER BY 2 , 1 ASC ";
			// @formatter:on

			stmt = connection.prepareStatement(sql);
			stmt.setString(1, ContextVariable.ENTERPRISE_SITE_DATE.getName());
			stmt.setInt(2, workplaceId);
			rs = stmt.executeQuery();

			return getWorkplaceCosts(rs, workplaceId, yearCol, monthCol);
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	private static List<Cost> getWorkplaceCosts(ResultSet rs,
			Integer workplaceId, String yearCol, String monthCol)
			throws SQLException {
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
			cost.setMonth(month - 1);
			cost.setWorkplaceId(workplaceId);

			costs.add(cost);
		}

		return costs;

	}

	private static List<Activity> getEnterpriseActivities(
			Connection connection, Integer enterpriseId) throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			//@formatter:off
			String sql = "SELECT * "
					+ " FROM " + ENTERPRISE_ACTIVITY
					+ " WHERE " + EnterpriseActivityColumns.ENTERPRISE + " = ? ";
			//@formatter:on

			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, enterpriseId);
			rs = stmt.executeQuery();

			List<Activity> activities = new LinkedList<Activity>();
			while (rs.next()) {

				Activity activity = new Activity();
				activity.setId(rs.getInt(EnterpriseActivityColumns.ID));
				activity.setDescription(rs
						.getString(EnterpriseActivityColumns.DESCRIPTION));
				activity.setCnae2009(rs
						.getInt(EnterpriseActivityColumns.CNAE2009));
				
				getEnterpriseActivityCCCs(connection, activity.getId())
				.forEach(ccc -> activity.addCcc(ccc));
				
				activities.add(activity);
				
				
			}

			return activities;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				rs.close();
			}
		}
	}

	private static List<CCC> getEnterpriseActivityCCCs(
			Connection connection, Integer enterpriseActivityId) throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;
		List<CCC> cccs = new LinkedList<CCC>();
		
		try {
			//@formatter:off
			String sql = "SELECT * "
					+ " FROM " + ENTERPRISE_CCC
					+ " INNER JOIN " + SQLConstants.GEOZONE 
					+ " ON ( " + SQLConstants.ENTERPRISE_CCC + "." + EnterpriseCccColumns.GEOZONE 
					+ " = " + SQLConstants.GEOZONE + "." + GeozoneColumns.ID + ")"
					+ " WHERE " + EnterpriseCccColumns.ENTERPRISE_ACTIVITY + " = ? ";
			//@formatter:on

			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, enterpriseActivityId);
			rs = stmt.executeQuery();

			
			while (rs.next()) {

				CCC ccc = new CCC();
				ccc.setId(rs.getInt(EnterpriseCccColumns.ID));
				ccc.setGeozone(rs.getString(SQLConstants.GEOZONE + "." +GeozoneColumns.CODE));
				ccc.setCode(rs.getString(SQLConstants.ENTERPRISE_CCC + "." + EnterpriseCccColumns.CCC));
				ccc.setRegime(getSSRegime(rs.getInt(SQLConstants.ENTERPRISE_CCC + "." + EnterpriseCccColumns.TYPE)).getCode());
				cccs.add(ccc);
			}

		} catch (Throwable t ) {
			
		}
		finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				rs.close();
			}
		}
		return cccs;
	}

	private static List<BankAccount> getEnterpriseBankAccounts(
			Connection connection, Integer enterpriseId) throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			//@formatter:off
			String sql = "SELECT * "
					+ " FROM " + RBANK
					+ " WHERE " + RbankColumns.REGISTRY + " = ? ";
			//@formatter:on

			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, enterpriseId);
			rs = stmt.executeQuery();

			List<BankAccount> bankAccounts = new LinkedList<BankAccount>();
			while (rs.next()) {

				BankAccount bankAccount = new BankAccount();
				bankAccount.setId(rs.getInt(RbankColumns.ID));
				bankAccount.setBic(rs
						.getString(RbankColumns.BIC));
				bankAccount.setAlias(rs
						.getString(RbankColumns.ALIAS));
				bankAccount.setAccount(rs
						.getString(RbankColumns.BANK_ACCOUNT));

				bankAccounts.add(bankAccount);
			}

			return bankAccounts;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				rs.close();
			}
		}
	}

	private static Enterprise getEnterprise(Integer registryID, Integer userID,
			Connection connection) throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			String sql = "SELECT * "
					+ " FROM " + REGISTRY
					+ ", " + ENTERPRISE
					+ " LEFT JOIN " + WORKPLACE
						+ " ON ( " + ENTERPRISE + "." + EnterpriseColumns.REGISTRY + " = " + WORKPLACE + "." + WorkplaceColumns.ENTERPRISE + " )"
					+ " LEFT JOIN "	+ PAYROLL_WORKPLACE
						+ " ON ( " + WORKPLACE + "." + WorkplaceColumns.ID + " = " + PAYROLL_WORKPLACE + "." + PayrollWorkplaceColumns.WORKPLACE + ")"
					+ " LEFT JOIN " + AGREEMENT
						+ " ON ( " + PAYROLL_WORKPLACE + "." + PayrollWorkplaceColumns.AGREEMENT + " = " + AGREEMENT + "." + AgreementColumns.ID + " )"

					+ " LEFT JOIN " + ENTERPRISE_ACTIVITY
						+ " ON ( " + PAYROLL_WORKPLACE + "." + PayrollWorkplaceColumns.ENTERPRISE_ACTIVITY + " = " + ENTERPRISE_ACTIVITY + "." + EnterpriseActivityColumns.ID + " )"
					+ " LEFT JOIN " + ENTERPRISE_CCC
						+ " ON ( " + ENTERPRISE_CCC + "." + EnterpriseCccColumns.ENTERPRISE_ACTIVITY + " = " + ENTERPRISE_ACTIVITY + "." + EnterpriseActivityColumns.ID + " )"

					+ " WHERE " + REGISTRY
					+ "." + RegistryColumns.ID + " = ?" + " AND " + REGISTRY
					+ "." + RegistryColumns.ID + " = " + ENTERPRISE + "."
					+ EnterpriseColumns.REGISTRY + " AND " + WORKPLACE + "."
					+ WorkplaceColumns.SCOPE + " IN ( SELECT "
					+ UserScopeColumns.SCOPE + " FROM " + USER_SCOPE
					+ " WHERE " + UserScopeColumns.USER_ID + " = ? "
					+ " UNION SELECT scope.id FROM scope INNER JOIN " + DOMAIN
					+ " ON ( scope.domain = " + DOMAIN + "." + DomainColumns.ID
					+ " ) INNER JOIN " + USER + " ON ( " + DOMAIN + "."
					+ DomainColumns.PARENT + " = " + USER + "."
					+ UserColumns.DOMAIN + " ) )" + " ORDER BY " + " UPPER("
					+ WORKPLACE + "." + WorkplaceColumns.DESCRIPTION + " )";

			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, registryID);

			stmt.setInt(2, userID);

			rs = stmt.executeQuery();

			EnterpriseHandler enterpriseHandler = new EnterpriseHandler();

			WorkplaceHandler workplaceHandler = new WorkplaceHandler(
					enterpriseHandler);

			CCCHandler cccHandler = new CCCHandler(
					workplaceHandler);

			groups(rs, enterpriseHandler, workplaceHandler, cccHandler);

			Enterprise enterprise = enterpriseHandler.getEnterprise();

			if (enterprise == null)
				return null;

			List<Activity> activities = getEnterpriseActivities(connection,
					enterprise.getId());
			enterprise.setActivities(activities);

			List<BankAccount> bankAccounts = getEnterpriseBankAccounts(connection, enterprise.getId());
			enterprise.setBankAccounts(bankAccounts);


			return enterprise;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
	}


	private static ISalary getDBSalary(String domain, SalaryDraft salaryDraft)
			throws ManagerBeanException {
		Connection conn = null; 
		try {
			conn = AonServletUtils.getConnection(domain);
			return getDBSalary(conn, salaryDraft);
		} catch ( SQLException e ) {
			throw new IllegalArgumentException(e);
		}
		finally {
			if ( conn != null ) {
				try {
					conn.close();
				} catch ( SQLException e ) {
				}
			}
		}
	}

	private static ISalary getDBSalary(Connection connection, SalaryDraft salaryDraft)
			throws ManagerBeanException {
		
		byte type = (byte)salaryDraft.getType().ordinal();
		Integer contract = salaryDraft.getEmployee().getId();
		java.sql.Date sqlStartDate = new java.sql.Date(salaryDraft.getStartDate().getTime());  
		java.sql.Date sqlEndDate = new java.sql.Date(salaryDraft.getEndDate().getTime());  
		java.sql.Date sqlIssueDate = new java.sql.Date(salaryDraft.getIssueDate().getTime());  
		
		Condition condition = 
		com.esferalia.aon.jooq.tables.Salary.SALARY.CONTRACT.eq(contract)
		.and(com.esferalia.aon.jooq.tables.Salary.SALARY.TYPE.eq(type))
		.and(com.esferalia.aon.jooq.tables.Salary.SALARY.START_DATE.eq(sqlStartDate))
		.and(com.esferalia.aon.jooq.tables.Salary.SALARY.END_DATE.eq(sqlEndDate));
		
		// This approach is very conservative, now only checks issue date for extras.
		if (  salaryDraft.getType() == Salary.Type.EXTRA )
			condition = condition
			.and(com.esferalia.aon.jooq.tables.Salary.SALARY.ISSUE_DATE.eq(sqlIssueDate));
		
		for ( ISalary salary : PayrollServletUtils.getSalary(connection, condition) )
			return salary;
		
		return null;
	}

	private static List<Variable> getDBSalaryData(String domain, ISalary salary)
			throws ManagerBeanException {
		Connection conn = null; 
		try {
			conn = AonServletUtils.getConnection(domain);
			return getDBSalaryData(conn, salary);
		} catch ( SQLException e ) {
			throw new IllegalArgumentException(e);
		}
		finally {
			if ( conn != null ) {
				try {
					conn.close();
				} catch ( SQLException e ) {
				}
			}
		}
	}

	private static List<Variable> getDBSalaryData(Connection conn, ISalary salary)
			throws ManagerBeanException {

		AONContext aonContext = new AONContext(conn);
		DSLContext dslContext = aonContext.getDslContext();
		
		Variable vars [] =
		dslContext
		.select()
		.from(com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA)
		.where(com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA.SALARY.eq(salary.getId()))
		.fetchInto(com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA)
		.stream()
		.map( data -> {
			Variable var = new StringVariable();
			var.setName(data.getName());
			var.setStartDate(data.getStartDate());
			var.setEndDate(data.getEndDate());
			var.setValue(data.getExpression());
			var.setDomain(data.getDomain());
			return var;
		})
		.toArray(Variable[]::new )
		;
		
		return Arrays.asList(vars);

	}

	
	private static void calculate(String domain, SalaryDraft draft, GenericContractSalaryCalculator<ISalary,ISQLContractSalaryCalculatorContext> salaryCalculator) {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			calculate(conn, draft, salaryCalculator);
		} catch ( SQLException e ) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnore) {

				}
			}
		}
		
	}
	
	private static void calculate(Connection conn, SalaryDraft draft, GenericContractSalaryCalculator<ISalary,ISQLContractSalaryCalculatorContext> salaryCalculator) {

		SalaryDraftBuilder salaryDraftBuilder = new SalaryDraftBuilder(draft);
		try {
			EmployeesServiceHelper.calculate(conn, draft, salaryDraftBuilder, salaryDraftBuilder, salaryCalculator);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			salaryDraftBuilder.clearDb();
			ISalary dbSalary = getDBSalary(conn, draft);
			if (dbSalary != null) {
				salaryDraftBuilder.setDbSalary(dbSalary);
				salaryDraftBuilder.setDbSalaryData(getDBSalaryData(conn, dbSalary));
			}
		} catch (SalaryException e) {
		} catch (ManagerBeanException e) {
		}

	}

	private static void calculate(String domain, SalaryDraft draft, GenericContractSalaryCalculator<ISalary, ISQLContractSalaryCalculatorContext> salaryCalculator, Date sections []) {

		SalaryDraftBuilder salaryDraftBuilder = new SalaryDraftBuilder(draft);
		CollectSalaryBuilder<ISalary> collectSalaryBuilder = new CollectSalaryBuilder<ISalary>();
		Date startDate = draft.getStartDate();
		Date endDate = draft.getEndDate();
		try {
			for ( Date section : sections ) {
				draft.setEndDate(AonDateUtils.add(section, Calendar.DAY_OF_MONTH, -1));
				calculate(domain, draft, collectSalaryBuilder, salaryDraftBuilder, salaryCalculator);
				draft.setStartDate(section);
			}
			draft.setEndDate(endDate);

			draft.addDraftVariable(getActiveDaysVar(collectSalaryBuilder));
			calculate(domain, draft, collectSalaryBuilder, salaryDraftBuilder, salaryCalculator);

			collectSalaryBuilder.collect(salaryDraftBuilder);

		} catch (Exception e) {
			e.printStackTrace();
		}
		draft.setStartDate(startDate);

		try {
			salaryDraftBuilder.clearDb();
			ISalary dbSalary = getDBSalary(domain, draft);
			if (dbSalary != null) {
				salaryDraftBuilder.setDbSalary(dbSalary);
				salaryDraftBuilder.setDbSalaryData(getDBSalaryData(domain, dbSalary));
			}
		} catch (SalaryException e) {
		} catch (ManagerBeanException e) {
		}

	}
	
	private static Map<String, String> getEventsVariables(String domain,
			Integer workplaceId, Integer agreementId, Date startDate,
			Date endDate, Integer domainId, Integer parentDomainId) throws SQLException {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);

			Set<Payment> payments = SQLEvents.getPayments(connection,
					workplaceId, startDate, endDate);

			if (agreementId != null) {
				// payments.addAll(SQLAgreementDraft.getPayments(connection,
				// agreementId, startDate, endDate));
				payments.addAll(SQLAgreementDraft.getPaymentsAux(connection,
						agreementId, startDate, endDate));
			}

			Map<String, String> variables = new HashMap<String, String>();

			for (Payment payment : payments) {

				if (StringUtils.equals(REMOVE, payment.getExpression()))
					continue;
				try {
					Set<String> paymentVars = ExpressionContext
							.getVariableSet(payment.getExpression());
	
					for (String var : paymentVars) {
						if (var.endsWith("_ACTUAL"))
							continue; // This is awfull ... very awful
						variables.put(var, String.format("%s",
								payment.getDescription(), payment.getExpression()));
					}
				} catch (Exception e ) {
					//TODO : Error
				}

				variables.remove(payment.getName());
			}
			
			// Filter ContextVariable
			for (ContextVariable ctxVar : ContextVariable.values())
				variables.remove(ctxVar.getName());

			// Clean system variables.
			Set<String> systemVars = getSystemVariables(connection, startDate,
					endDate);
			for (String var : systemVars)
				variables.remove(var);
			
			System.out.println("------------------------------- VARIABLES --------------------------");
			for(String var : variables.keySet())
				System.out.println(var);
				
			Set<Level> levels = SQLAgreementDraft.getLevels(connection,
					agreementId, domainId, parentDomainId);

			SalaryTable salaryTable = SQLAgreementDraft.getSalaryTable(
					connection, agreementId, startDate, endDate,domainId, parentDomainId);

			//¿Que variables se filtran aqui?
			System.out.println();
			System.out.println("------------------------------- VARIABLES --------------------------");
			Set<String> names = variables.keySet();
			for (Level level : levels) {
				Iterator<String> namesIt = names.iterator();
				while (namesIt.hasNext()) {
					String name = namesIt.next();
					if (salaryTable.get(level.getId(), name) != null) {
						System.out.println(name);
						namesIt.remove();
					}
				}
			}

			return variables;

		} finally {
			if (connection != null)
				connection.close();
		}
	}

	private static Map<String, String> getWorkplaceEventsVariables(String domain,
			Integer workplaceId, Integer agreementId, Date startDate,
			Date endDate, Integer domainId, Integer parentDomainId) throws SQLException {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);

			Set<Payment> payments = SQLEvents.getPayments(connection,
					workplaceId, startDate, endDate);

			if (agreementId != null) {
				// payments.addAll(SQLAgreementDraft.getPayments(connection,
				// agreementId, startDate, endDate));
				payments.addAll(SQLAgreementDraft.getPaymentsAux(connection,
						agreementId, startDate, endDate));
			}

			Map<String, String> variables = new HashMap<String, String>();

			for (Payment payment : payments) {

				if (StringUtils.equals(REMOVE, payment.getExpression()))
					continue;
				try {
					Set<String> paymentVars = ExpressionContext
							.getVariableSet(payment.getExpression());
	
					for (String var : paymentVars) {
						if (var.endsWith("_ACTUAL"))
							continue; // This is awfull ... very awful
						variables.put(var, String.format("%s",
								payment.getDescription(), payment.getExpression()));
					}
				}catch ( Exception e ) {
					//TODO: Error
				}

				variables.remove(payment.getName());
			}
			
			// Filter ContextVariable
			for (ContextVariable ctxVar : ContextVariable.values())
				variables.remove(ctxVar.getName());

			// Clean system variables.
			Set<String> systemVars = getSystemVariables(connection, startDate,
					endDate);
			for (String var : systemVars) {
				System.out.println(var);
				variables.remove(var);
			}

			Set<Level> levels = SQLAgreementDraft.getLevels(connection,
					agreementId, domainId, parentDomainId);

			SalaryTable salaryTable = SQLAgreementDraft.getSalaryTable(
					connection, agreementId, startDate, endDate,domainId, parentDomainId);

			//¿Que variables se filtran aqui?
			Set<String> names = variables.keySet();
			for (Level level : levels) {
				Iterator<String> namesIt = names.iterator();
				while (namesIt.hasNext()) {
					String name = namesIt.next();
					if (salaryTable.get(level.getId(), name) != null)
						namesIt.remove();
				}
			}

			return variables;

		} finally {
			if (connection != null)
				connection.close();
		}
	}

	private ContextDescriptor getEmployeePayments(Connection connection, Integer employeeId, Integer agreementId,
			Date startDate, Date endDate, Integer domainID, Integer parentDomainID) throws SQLException{

		ArrayList<String> eraseAgreements = new ArrayList<>();

		try {
			Set<Payment> payments = SQLEvents.getEmployeePayments(connection,
					employeeId, startDate, endDate);

			if (agreementId != null) {

				eraseAgreements = SQLAgreementDraft.getEraseAgreement(connection,
						agreementId, startDate, endDate);

				payments.addAll(SQLAgreementDraft.getPaymentsAux(connection,
						agreementId, startDate, endDate));
			}

			ContextDescriptor result = new ContextDescriptor();

			for (Payment payment : payments) {

				if (StringUtils.equals(REMOVE, payment.getExpression()))
					continue;
				
				try {
					Set<String> paymentVars = ExpressionContext
							.getVariableSet(payment.getExpression());
					for (String var : paymentVars) {
						if (var.endsWith("_ACTUAL"))
							continue; // This is awfull ... very awful
						result.add(var);
					}
				} catch ( Exception e ) {
					// TODO: Error...
				}


				result.remove(payment.getName());

			}

			// Add Filter Allways Variables
			//eraseAgreements.add("INICIO_ANTIGUEDAD");
			eraseAgreements.add("DIAS_MES");
			eraseAgreements.add("INICIO_CONTRATO");
			eraseAgreements.add("SALARIO_BASE");
			eraseAgreements.add("INICIO_NOMINA");
			eraseAgreements.add("SALARIO_MENSUAL");
			eraseAgreements.add("TRIENIO");
			//TODO: Esto es una prueba
			eraseAgreements.add("KMS");

			// Filter Agreement Variables
			for (String varName : eraseAgreements)
				result.remove(varName);

			Set<Level> levels = null;
			SalaryTable salaryTable = null;

			if(null != agreementId)
				levels = SQLAgreementDraft.getLevels(connection,
					agreementId, domainID, parentDomainID);

			if(null != agreementId)
				salaryTable = SQLAgreementDraft.getSalaryTable(
					connection, agreementId, startDate, endDate,domainID, parentDomainID);

			if(null != levels && null != salaryTable){
				Set<String> names = result.getVariables();
				for (Level level : levels) {
					Iterator<String> namesIt = names.iterator();
					while (namesIt.hasNext()) {
						String name = namesIt.next();
						if (salaryTable.get(level.getId(), name) != null)
							namesIt.remove();
					}
				}
			}

//			for(String key : result.getVariables())
//				for(VariableDescriptor var : result.getList(key))
//					System.out.println("PAYMENTS :"+key+", descripcion :"+var.getDescription()+", expresion :"+var.getExpression()
//					+", value :"+var.getValue()+", startDate :"+var.getStartDate()+", endDate :"+var.getEndDate());

			return result;

		}catch (Exception e) {
			e.printStackTrace();
			throw e;

		}finally {
			if (connection != null)
				connection.close();
		}
	}

	@SuppressWarnings("unchecked")
	private static void calculateAndSave(Connection conn, SalaryDraft draft)
			throws SQLException {
		if (draft.hasDbSalary())
			deleteSalaries(conn, draft.getDbId());
		if ( draft.getType() == Type.SETTLE )
			deleteAllSettles(conn, draft.getEmployee().getId());
		
		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(conn);
		RoundSalaryBuilder<ISalary> roundSalaryBuilder = new RoundSalaryBuilder<ISalary>(jooqSalaryBuilder,
				d -> Math.round(d*1000.00)/1000.00);
		jooqSalaryBuilder.setListener(new SalaryBuilderListener());
		SalaryDraftBuilder salaryDraftBuilder = new SalaryDraftBuilder(draft);
		CompositeSalaryBuilder<ISalary, ISalaryBuilder<ISalary>> compositeSalaryBuilder = new CompositeSalaryBuilder<ISalary, ISalaryBuilder<ISalary>>(
				salaryDraftBuilder, roundSalaryBuilder);
		SmartContractSalaryCalculator<ISalary> salaryCalculator = new SmartContractSalaryCalculator<ISalary>();

		boolean autocommit = false;
		try {
			autocommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			EmployeesServiceHelper.calculate(conn, draft, compositeSalaryBuilder, salaryDraftBuilder, salaryCalculator);
			jooqSalaryBuilder.execute();
			conn.commit();
		} finally {
			conn.setAutoCommit(autocommit);
		}

		try {
			salaryDraftBuilder.clearDb();
			ISalary dbSalary = getDBSalary(conn, draft);
			if (dbSalary != null) {
				salaryDraftBuilder.setDbSalary(dbSalary);
				salaryDraftBuilder.setDbSalaryData(getDBSalaryData(conn, dbSalary));
			}
		} catch (SalaryException e) {
		} catch (ManagerBeanException e) {
		}

	}

	@SuppressWarnings("unchecked")
	private static void calculateAndSave(Connection conn, SalaryDraft draft, Date sections [])
			throws SQLException {
		if (draft.hasDbSalary())
			deleteSalaries(conn, draft.getDbId());
		if ( draft.getType() == Type.SETTLE )
			deleteAllSettles(conn, draft.getEmployee().getId());

		SalaryDraftBuilder salaryDraftBuilder = new SalaryDraftBuilder(draft);

		boolean autocommit = false;
		Date startDate = draft.getStartDate();
		Date endDate = draft.getEndDate();
		try {
			autocommit = conn.getAutoCommit();
			conn.setAutoCommit(false);

			CollectSalaryBuilder<ISalary> collectSalaryBuilder = new CollectSalaryBuilder<ISalary>();
			SmartContractSalaryCalculator<ISalary> salaryCalculator = new SmartContractSalaryCalculator<ISalary>();
			for ( Date section : sections ) {
				draft.setEndDate(AonDateUtils.add(section, Calendar.DAY_OF_MONTH, -1));
				EmployeesServiceHelper.calculate(conn, draft, collectSalaryBuilder, salaryDraftBuilder, salaryCalculator);
				draft.setStartDate(section);
			}
			draft.setEndDate(endDate);

			draft.addDraftVariable(getActiveDaysVar(collectSalaryBuilder));

			EmployeesServiceHelper.calculate(conn, draft, collectSalaryBuilder, salaryDraftBuilder, salaryCalculator);

			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(conn);
			RoundSalaryBuilder<ISalary> roundSalaryBuilder = new RoundSalaryBuilder<ISalary>(jooqSalaryBuilder,
					d -> Math.round(d*1000.00)/1000.00);
			jooqSalaryBuilder.setListener(new SalaryBuilderListener());
			CompositeSalaryBuilder<ISalary, ISalaryBuilder<ISalary>> compositeSalaryBuilder =
					new CompositeSalaryBuilder<ISalary, ISalaryBuilder<ISalary>>(salaryDraftBuilder, roundSalaryBuilder);
			collectSalaryBuilder.collect(compositeSalaryBuilder);

			jooqSalaryBuilder.execute();
			conn.commit();
		} finally {
			conn.setAutoCommit(autocommit);
		}
		draft.setStartDate(startDate);

		try {
			salaryDraftBuilder.clearDb();
			ISalary dbSalary = getDBSalary(conn, draft);
			if (dbSalary != null) {
				salaryDraftBuilder.setDbSalary(dbSalary);
				salaryDraftBuilder.setDbSalaryData(getDBSalaryData(conn, dbSalary));
			}
			else {
				//draft.clearDb();
			}
		} catch (SalaryException e) {
		} catch (ManagerBeanException e) {
		}

	}


	private static NumberVariable getActiveDaysVar (CollectSalaryBuilder<?> builder ) {
		NumberVariable activeDays = new NumberVariable();

		ITimedVariable<Double> activeDaysVar = builder.getAllActiveDaysVar();

		activeDays.setImplicit(true);
		activeDays.setScope(Scope.SYSTEM);
		activeDays.setName(ACTIVE_DAYS.getName());
		activeDays.setEndDate(null);
		activeDays.setStartDate(activeDaysVar.getPeriod().getStart());
		activeDays.setValue(activeDaysVar.getValue(activeDaysVar.getPeriod()));

		return activeDays;
	}

	private static <T extends ISalaryBuilder<ISalary>, L extends SalaryDraftBuilder> void calculate(
			String domain, SalaryDraft draft, T salaryBuilder, L draftBuilder, GenericContractSalaryCalculator<ISalary,ISQLContractSalaryCalculatorContext> calculator) {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			EmployeesServiceHelper.calculate(conn, draft, salaryBuilder, draftBuilder, calculator);
		} catch ( SQLException e ) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnore) {

				}
			}
		}
	}

	private static <T> List<ITimedResult<T>> eval(String domain, String expression,
			SalaryDraft draft, Class<T> toType) throws EvalException {
		Connection conn = null;

		try {
			conn = AonServletUtils.getConnection(domain);
			ISalaryCalculatorContext ctx = EmployeesServiceHelper.getSalaryCalculatorContext(conn,
					draft, null);
			return ctx.getExpressionContext().eval(expression,
					ctx.getStartDate(), ctx.getEndDate(), toType);

		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} catch (InvalidVariables e) {
			throw new EvalWarning(e.getMessage());
		} catch (CheckException e) {
			throw new EvalWarning(e.getMessage());
		} catch (RemoveVariableError e) {
			return Collections.emptyList();
		} catch (UndefinedVariablesException e) {
			throw new UnknownVariablesWarning(e.getVariableNames());
		} catch (CompileException e) {
			throw new EvalSyntaxErrorException(e.getMessage());
		} catch (ExpressionException e) {
			throw new IllegalArgumentException(e.getMessage());
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnore) {

				}
			}
		}

	}

	private static <T> List<ITimedResult<T>> eval(String domain,
			String expression, AgreementDraft draft, int levelId, Class<T> toType)
			throws EvalException {
		Connection conn = null;

		try {
			conn = getConnection(domain);
			ISalaryCalculatorContext ctx = getSalaryCalculatorContext(conn,
					draft, -1);
			return ctx.getExpressionContext().eval(expression,
					ctx.getStartDate(), ctx.getEndDate(), toType);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} catch (InvalidVariables e) {
			throw new EvalWarning(e.getMessage());
		} catch (CheckException e) {
			throw new EvalWarning(e.getMessage());
		} catch (RemoveVariableError e) {
			return Collections.emptyList();
		} catch (UndefinedVariablesException e) {
			throw new UnknownVariablesWarning(e.getVariableNames());
		} catch (CompileException e) {
			throw new EvalSyntaxErrorException(e.getMessage());
		} catch (ExpressionException e) {
			throw new IllegalArgumentException(e.getMessage());
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnore) {

				}
			}
		}

	}

	protected static ContextDescriptor getDraftContext(String domain, SalaryDraft draft) {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);

			IContractSalaryCalculatorContext calculatorCtx = EmployeesServiceHelper.getSalaryCalculatorContext(
					conn, draft, null);

			return getContext(conn, calculatorCtx, draft.getStartDate(),
					draft.getEndDate());

		} catch (ExpressionException e) {
			throw new IllegalArgumentException(e);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}

	protected static ContextDescriptor getContext(Connection conn,
			IContractSalaryCalculatorContext calculatorCtx, Date startDate,
			Date endDate) {
		try {


			ExpressionContext expressionContext = notNull(
					calculatorCtx.getExpressionContext(),
					calculatorCtx.getSystemExpressionContext());

			Date start = notNull(calculatorCtx.getStartDate(), startDate);

			Date end = notNull(calculatorCtx.getEndDate(), endDate);

			ContextDescriptor contextDescriptor = new ContextDescriptor();

			Map<String, String> descriptions = getSystemDescriptions(conn,
					start, end);

			Set<String> varNames = expressionContext.variablesSet();

			for (String varName : varNames) {
				Object value = null;
				List<ITimedVariable<Object>> vars = expressionContext.getVariables(varName, start, end);

				for ( ITimedVariable<Object> var : vars ) {
					try {
							value = var.getValue(var.getPeriod());
					} catch (Throwable e) {

					}

					if (value == null)
						continue;

					String description = null;

					ContextVariable ctxVar = ContextVariable
							.getVariableByName(varName);

					if (ctxVar != null) {
						try {
							description = ctxVar.getDescription(new Locale("es",
									"ES"));
						} catch (MissingResourceException e) {
						}
						if (description != null)
							description = String.format(description, start, end);
					}
					if (description == null)
						description = descriptions.get(varName);

					if (Function.class == value.getClass()) {
						Class<?> type = ctxVar != null ? ctxVar.getType()
								.getJavaType() : Object.class;
						contextDescriptor.add(varName, description, type,
								((Function) value).getParameters());
					} else if (MethodStub.class == value.getClass()) {
						Method method = ((MethodStub) value).getMethod();
						contextDescriptor.add(varName, description,
								method.getReturnType(), method.getParameterTypes());
					} else {
						Class<?> type = value.getClass();
						if (ContextDescriptor.isKnownType(type)){
							VariableDescriptor variableDescriptor = new VariableDescriptor();
							variableDescriptor.setType(type);
							variableDescriptor.setValue(value.toString());
							variableDescriptor.setDescription(description);
							variableDescriptor.setStartDate(var.getPeriod().getStart());
							variableDescriptor.setEndDate(var.getPeriod().getEnd());

							if ( var instanceof IExpressionVariable<?>) {
								IExpression expression = ((IExpressionVariable<?>) var).getExpression();
								variableDescriptor.setExpression(expression.getExpression());
								variableDescriptor.setScope((expression
										.getScope() != null )? Scope
										.values()[expression
										.getScope()
										.ordinal()]: null );
							}
							contextDescriptor.add(varName, variableDescriptor);
						}
					}
				}

			}


			return contextDescriptor;

		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	protected static ContextDescriptor getDraftContext(String domain, AgreementDraft draft,
			int levelId) {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);

			IContractSalaryCalculatorContext calculatorCtx = getSalaryCalculatorContext(
					conn, draft, levelId);

			return getContext(conn, calculatorCtx, draft.getStartDate(),
					draft.getEndDate());

		} catch (ExpressionException e) {
			throw new IllegalArgumentException(e);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}

	private static ISalary getSalary(String domain, SalaryDraft draft) {

		SalaryBuilder salaryBuilder = new SalaryBuilder() {
			@Override
			public void createNewSalary() {
				this.salary = new com.esferalia.aon.payroll.Salary() {
					@Override
					public Collection<SalaryCost> getCosts() throws SalaryException {
						return getSalaryCosts();
					}
					@Override
					public Collection<SalaryCost> getCostS() throws SalaryException {
						return getSalaryCosts();
					}
					@Override
					public Collection<SalaryBonus> getBonus() throws SalaryException {
						return getSalaryBonus();
					}
					@Override
					public Collection<SalaryPayment> getPaymentS() throws SalaryException {
						return getSalaryPayments();
					}
					@Override
					public Collection<SalaryDeduction> getDeductionS() throws SalaryException {
						return getSalaryDeductions();
					}
				};
				// default ones
				salary.setTotalIrpf(0.00);
			};
		};

		SmartContractSalaryCalculator<com.esferalia.aon.payroll.Salary> calculator =
				new SmartContractSalaryCalculator<com.esferalia.aon.payroll.Salary>();

		calculator.setSalaryBuilder(salaryBuilder);

		Connection conn = null;
		ISQLContractSalaryCalculatorContext ctx;
		try {
			conn = AonServletUtils.getConnection(domain);
			ctx = EmployeesServiceHelper.getSalaryCalculatorContext(conn, draft, null);
			com.esferalia.aon.payroll.Salary salary = (com.esferalia.aon.payroll.Salary) calculator
					.calculate(ctx);
			
			Payments payments = new Payments();
			salary.getSalaryPayments().forEach( p -> SalaryPaymentsFactory.managePayment(payments, p));
			try {salary.setPayments(payments);} catch (SalaryException e) {}
			
			Deductions deductions = new Deductions();
			salary.getSalaryDeductions().forEach( d -> SalaryDeductionsFactory.manageDeductions(deductions, d));
			deductions.setTotal(getOrZero(salary.getTotalDeduction()));
			deductions.setSocialSecurityContributions(getOrZero(salary.getSocialSecurityContributions()));
			try {salary.setDeductions(deductions);} catch (SalaryException e) {}
			
			Costs costs = new Costs();
			salary.getSalaryCosts().forEach( c -> SalaryCostsFactory.manageCosts(costs, c));
			try {salary.setEnterpriseCosts(costs);} catch (SalaryException e) {}
			
			Bonuses bonuses = new Bonuses();
			salary.getSalaryBonus().forEach( c -> bonuses.setTotal(bonuses.getTotal() + c.getAmount()) );
			try {salary.setBonuses(bonuses);} catch (SalaryException e) {}
			

			Contract contract = PayrollServletUtils.getContract(conn, draft
					.getEmployee().getId());

			salary.setContract(contract);
			
			// TODO: Calendar ???
			salary.setIssueYear(ctx.getIssueDate().getYear());
			salary.setIssueMonth(ctx.getIssueDate().getMonth());

			return salary;
		} catch (ExpressionException e) {
			throw new IllegalArgumentException(e);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} catch (SalaryException e) {
			throw new IllegalArgumentException(e);
		} catch (ManagerBeanException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}


	private static com.esferalia.aon.payroll.Salary getSalary( String domain,
			AgreementDraft draft, int levelId) {

		SalaryBuilder salaryBuilder = new SalaryBuilder() {
			@Override
			public void createNewSalary() {
				this.salary = new com.esferalia.aon.payroll.Salary() {
					@Override
					public Collection<SalaryCost> getCosts() throws SalaryException {
						return getSalaryCosts();
					}
					@Override
					public Collection<SalaryCost> getCostS() throws SalaryException {
						return getSalaryCosts();
					}
					@Override
					public Collection<SalaryBonus> getBonus() throws SalaryException {
						return getSalaryBonus();
					}
					@Override
					public Collection<SalaryPayment> getPaymentS() throws SalaryException {
						return getSalaryPayments();
					}
					@Override
					public Collection<SalaryDeduction> getDeductionS() throws SalaryException {
						return getSalaryDeductions();
					}
				};
				// default ones
				salary.setTotalIrpf(0.00);
			};
		};

		SmartContractSalaryCalculator<com.esferalia.aon.payroll.Salary> calculator =
				new SmartContractSalaryCalculator<com.esferalia.aon.payroll.Salary>();

		calculator.setSalaryBuilder(salaryBuilder);

		Connection conn = null;
		ISQLContractSalaryCalculatorContext ctx;
		try {
			conn = getConnection(domain);
			ctx = getSalaryCalculatorContext(conn, draft, levelId);
			com.esferalia.aon.payroll.Salary salary = (com.esferalia.aon.payroll.Salary) calculator
					.calculate(ctx);

			Payments payments = new Payments();
			salary.getSalaryPayments().forEach( p -> SalaryPaymentsFactory.managePayment(payments, p));
			try {salary.setPayments(payments);} catch (SalaryException e) {}
			
			Deductions deductions = new Deductions();
			salary.getSalaryDeductions().forEach( d -> SalaryDeductionsFactory.manageDeductions(deductions, d));
			deductions.setTotal(getOrZero(salary.getTotalDeduction()));
			deductions.setSocialSecurityContributions(getOrZero(salary.getSocialSecurityContributions()));
			try {salary.setDeductions(deductions);} catch (SalaryException e) {}
			
			Costs costs = new Costs();
			salary.getSalaryCosts().forEach( c -> SalaryCostsFactory.manageCosts(costs, c));
			try {salary.setEnterpriseCosts(costs);} catch (SalaryException e) {}
			
			Bonuses bonuses = new Bonuses();
			salary.getSalaryBonus().forEach( c -> bonuses.setTotal(bonuses.getTotal() + c.getAmount()) );
			try {salary.setBonuses(bonuses);} catch (SalaryException e) {}

			
			// fill salary , ugly code
			salary.setEmployeeDocument(StringUtils.repeat(" ", 9));
			String levelDescription = StringUtils.repeat(" ", 2);
			String draftDescription = StringUtils.repeat(" ", 12);
			String categoryDescription = StringUtils.repeat(" ", 12);

			for (Level level : draft.getLevels()) {
				if (level.getId() == levelId) {

					String description = level.getDescription();
					if (!StringUtils.isBlank(description))
						levelDescription = description;

					Map<Integer, Set<String>> categoriesMap = draft
							.getCategoriesMap();
					if (categoriesMap != null) {
						Set<String> categories = categoriesMap.get(levelId);
						if (categories != null) {
							for (String category : categories) {
								if (!StringUtils.isBlank(category)) {
									categoryDescription = category;
									break;
								}
							}
						}
					}

					break;
				}
			}

			salary.setEmployeeName(levelDescription + " " + categoryDescription);
			salary.setCategory(categoryDescription);
			salary.setCcc(StringUtils.repeat(" ", 11));
			salary.setSocialSecurityNumber(StringUtils.repeat(" ", 10));

			Contract contract = new Contract();
			contract.setId(0);
			Person person = new Person();
			person.setId(0);
			contract.setPerson(person);

			salary.setContract(contract);

			WorkPlace workPlace = new WorkPlace();
			contract.setWorkPlace(workPlace);

			try {
				EnterpriseCCC enterpriseCCC = null; //getDefaultHEnterpriseCCC();

				com.code.aon.company.Enterprise enterprise = null;

				if (enterpriseCCC == null) {
					enterprise = getEnterprise(domain);
					enterpriseCCC = new EnterpriseCCC();
					enterpriseCCC.setCcc("");
					EnterpriseActivity enterpriseActivity = new EnterpriseActivity();
					enterpriseActivity.setEnterprise(enterprise);
					enterpriseCCC.setActivity(enterpriseActivity);
					contract.setEnterpriseCCC(enterpriseCCC);
				} 
				salary.setEnterpriseDocument(enterprise.getRegistry()
						.getDocument());
				salary.setEnterpriseName(enterprise.getRegistry().getFullName());
				/*
				RegistryAddress rAddress = enterprise.getRegistry()
						.getDefaultAddress();
				if (rAddress != null) {
					salary.setEnterpriseAddress(rAddress.getFullAddress());
					workPlace.setAddress(rAddress);
				}
				*/
				workPlace.setEnterprise(enterprise);
				person.setRegistry(enterprise.getRegistry());

			} catch (Exception e) {
				if (!StringUtils.isBlank(draft.getDescription()))
					draftDescription = draft.getDescription();
				salary.setEnterpriseName(draftDescription);
				salary.setEnterpriseDocument(StringUtils.repeat(" ", 9));
				salary.setEnterpriseAddress(StringUtils.repeat(" ", 25));
			}

			// TODO: Calendar ???
			salary.setIssueYear(ctx.getIssueDate().getYear());
			salary.setIssueMonth(ctx.getIssueDate().getMonth());

			return salary;
		} catch (ExpressionException e) {
			throw new IllegalArgumentException(e);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} catch (SalaryException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}

	private static IrpfOutcome getIrpfOutcome(String domain, SalaryDraft draft) {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);

			Criteria contractCriteria = new Criteria();
			contractCriteria.addEqualExpression(SQLConstants.CONTRACT + "."
					+ ContractColumns.ID, draft.getEmployee().getId());

			class IrpfListener implements IListener {
				private IrpfOutcome irpfOutcome;

				@Override
				public void onIrpf(IrpfOutcome irpfOutcome) {
					this.irpfOutcome = irpfOutcome;
				}

				@Override
				public void onUndefinedData(IExpression expression,
						String variableName, String message, Date start,
						Date end) {
				}

				@Override
				public void onRedefinedImplicit(String name,
						ITimedVariable<?> redefined, ITimedVariable<?> implicit) {
				}

			}

			IrpfListener listener = new IrpfListener();
			SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext> draftCtx = EmployeesServiceHelper.getSalaryCalculatorContext(
					conn, draft, null);

			draftCtx.setListener(listener);

			draftCtx.getCtx().getIrpf();

			return listener.irpfOutcome;

		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} catch (ExpressionException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}

	}

	protected static com.esferalia.aon.payroll.IrpfOutcome getIrpfOutcome(
			Irpf irpf) {
		IrpfOutcome irpfOutcome = new IrpfOutcome();

//			IManagerBean irpfResultManagerBean = BeanManager
//					.getManagerBean(com.esferalia.aon.payroll.IrpfResult.class);
//			Criteria irpfResultCriteria = new Criteria();
//			irpfResultCriteria.addEqualExpression(irpfResultManagerBean
//					.getFieldName(IEntityAlias.IRPF_RESULT_ID), irpf
//					.getIrpfResult().getId());
//			List<ITransferObject> irpfResults = irpfResultManagerBean
//					.getList(irpfResultCriteria);
//			irpfOutcome
//					.setIrpfResult((com.esferalia.aon.payroll.IrpfResult) irpfResults
//							.get(0));
//
//			IManagerBean irpfDataManagerBean = BeanManager
//					.getManagerBean(com.esferalia.aon.payroll.IrpfData.class);
//
//			Criteria irpfDataCriteria = new Criteria();
//			irpfDataCriteria
//					.addEqualExpression(irpfDataManagerBean
//							.getFieldName(IEntityAlias.IRPF_DATA_ID), irpf
//							.getIrpfData().getId());
//			List<ITransferObject> irpfDatas = irpfDataManagerBean
//					.getList(irpfDataCriteria);
//			com.esferalia.aon.payroll.IrpfData irpfData = (com.esferalia.aon.payroll.IrpfData) irpfDatas
//					.get(0);
//			irpfOutcome.setIrpfData(irpfData);
//
//			Person person = irpfData.getContract().getPerson();
//			Registry registry = irpfData.getContract().getPerson()
//					.getRegistry();
//
//			irpfOutcome.setNif(registry.getDocument());
//			Date birthDate = person.getBirthDate();
//			if (birthDate != null) {
//				irpfOutcome.setBirthYear(birthDate.getYear());
//			}
//
//			if (irpf.getIrpfRegularization() == null)
//				return irpfOutcome;
//
//			IManagerBean irpfRegularizationManagerBean = BeanManager
//					.getManagerBean(com.esferalia.aon.payroll.IrpfRegularization.class);
//			Criteria irpfRegularizationCriteria = new Criteria();
//			irpfRegularizationCriteria.addEqualExpression(
//					irpfRegularizationManagerBean
//							.getFieldName(IEntityAlias.IRPF_REGULARIZATION_ID),
//					irpf.getIrpfRegularization().getId());
//			List<ITransferObject> irpfRegularizations = irpfRegularizationManagerBean
//					.getList(irpfRegularizationCriteria);
//			irpfOutcome
//					.setIrpfRegularization((com.esferalia.aon.payroll.IrpfRegularization) irpfRegularizations
//							.get(0));

		return irpfOutcome;

	}

	private static ISQLContractSalaryCalculatorContext getSalaryCalculatorContext(
			final Connection conn, final AgreementDraft draft, int levelId)
			throws ExpressionException, SQLException {

		Map<String, Object> data = new HashMap<String,Object>();
		data.put(ContextVariable.QUOTE_GROUP.getName(), "01");
		data.put(ContextVariable.TC2.getName(), ContractCode.C100.getValue());

		return getSalaryCalculatorContextImpl(conn, draft, levelId, data);
	}


	private static ISQLContractSalaryCalculatorContext getSalaryCalculatorContextImpl(
			Connection conn, final AgreementDraft draft, int levelId, Map<String,Object> data)
			throws ExpressionException, SQLException {

		Date startDate = draft.getStartDate();
		Date endDate = DateUtils.getLastDayOfMonth(startDate);

		SQLAgreementSalaryCalculatorContext sqlAgreementSalaryCalculatorContext = new SQLAgreementSalaryCalculatorContext(
				conn, startDate, endDate, levelId);

		sqlAgreementSalaryCalculatorContext.next(ctx-> data.entrySet().stream().forEach(entry->ctx.putVariable(entry.getKey(), new TimedObject<Object>(entry.getValue(), startDate, endDate))));

		return sqlAgreementSalaryCalculatorContext;
	}

	private static ISalary getSalary(String domain, SalaryPreview draft) {
		SalaryBuilder salaryBuilder = new SalaryBuilder();

		SmartContractSalaryCalculator<com.esferalia.aon.payroll.Salary> calculator =
				new SmartContractSalaryCalculator<com.esferalia.aon.payroll.Salary>();

		calculator.setSalaryBuilder(salaryBuilder);
		
		Connection conn = null ;

		ISQLContractSalaryCalculatorContext ctx;
		try {
			conn = AonServletUtils.getConnection(domain);
			
			ctx = getSQLContractSalaryCalculatorContext(conn, draft);
			com.esferalia.aon.payroll.Salary salary = (com.esferalia.aon.payroll.Salary) calculator
					.calculate(ctx);

			Contract contract = PayrollServletUtils.getContract(conn, draft
					.getEmployee().getId());

			salary.setContract(contract);

			return salary;
		} catch (ExpressionException e) {
			throw new IllegalArgumentException(e);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} catch (SalaryException e) {
			throw new IllegalArgumentException(e);
		} catch (ManagerBeanException e) {
			throw new IllegalArgumentException(e);
		} finally {
			try {
				if ( conn != null )
					conn.close();
			} catch (SQLException e) {
				
			}
		}

	}

	private static ISQLContractSalaryCalculatorContext getSQLContractSalaryCalculatorContext(
			Connection conn, SalaryPreview preview) throws ExpressionException,
			SQLException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(tableCol(CONTRACT, ContractColumns.ID),
				preview.getEmployee().getId());

		Date startDate = preview.getStartDate();
		Date endDate = preview.getEndDate();
		Date issueDate = preview.getIssueDate();

		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				conn, startDate, endDate, issueDate, criteria);
		ctx.next();
		return ctx;
	}

	private static void groups(ResultSet rs, GroupHandler... handlers)
			throws SQLException {

		Map<String, Object> values = new HashMap<String, Object>();

		while (rs.next()) {
			for (int i = 0; i < handlers.length; i++) {
				GroupHandler handler = handlers[i];
				String col = handler.getCol();
				Object oldValue = values.get(col);
				Object newValue = rs.getObject(col);
				if (!equals(oldValue, newValue)) {
					handler.beginGroup(rs);
					values.put(col, newValue);
				}
			}
		}

	}

	private static boolean equals(Object one, Object another) {
		if (one == another) {
			return true;
		}
		if (one == null || another == null) {
			return false;
		}
		return one.equals(another);
	}

	private static Salary.Type getSalaryType(Integer ordinal) {
		if (ordinal == null || ordinal < 0) {
			return null;
		}

		Salary.Type types[] = Salary.Type.values();

		if (ordinal >= types.length) {
			return null;
		}

		return types[ordinal];
	}

	static String tableCol(String table, String col) {
		return String.format("%1$s.%2$s", table, col);
	}

	private static interface GroupHandler {

		String getCol();

		void beginGroup(ResultSet rs) throws SQLException;
	}

	private static abstract class AbstractHandler implements GroupHandler {

		private String col;

		public AbstractHandler(String table, String col) {
			this(tableCol(table, col));
		}

		public AbstractHandler(String col) {
			this.col = col;
		}

		@Override
		public String getCol() {
			return col;
		}
	}

	private static class EnterpriseHandler extends AbstractHandler {

		private Enterprise enterprise;

		public EnterpriseHandler() {
			super(ENTERPRISE, EnterpriseColumns.REGISTRY);
		}

		public Enterprise getEnterprise() {
			return enterprise;
		}

		@Override
		public void beginGroup(ResultSet rs) throws SQLException {
			enterprise = new Enterprise();
			enterprise.setId(rs.getInt(tableCol(REGISTRY, RegistryColumns.ID)));
			enterprise.setName(rs.getString(tableCol(REGISTRY,
					RegistryColumns.NAME)));
			enterprise.setDomain(rs.getInt(tableCol(ENTERPRISE,
					RegistryColumns.DOMAIN)));

		}

	}

	private static class WorkplaceHandler extends AbstractHandler {

		private Workplace workplace;
		private EnterpriseHandler enterpriseHandler;

		public WorkplaceHandler(EnterpriseHandler enterpriseHandler) {
			super(WORKPLACE, WorkplaceColumns.ID);
			this.enterpriseHandler = enterpriseHandler;
		}

		/**
		 *
		 * @return Last, active workplace.
		 */
		public Workplace getWorkplace() {
			return workplace;
		}

		@Override
		public void beginGroup(ResultSet rs) throws SQLException {
			workplace = new Workplace();
			workplace
					.setId(rs.getInt(tableCol(WORKPLACE, WorkplaceColumns.ID)));
			workplace.setDescription(rs.getString(tableCol(WORKPLACE,
					WorkplaceColumns.DESCRIPTION)));
			workplace.setActive(rs.getBoolean(tableCol(WORKPLACE,
					WorkplaceColumns.ACTIVE)));



			Object agreementId = rs.getObject(tableCol(PAYROLL_WORKPLACE,
					PayrollWorkplaceColumns.AGREEMENT));
			if (agreementId != null) {
				Agreement agreement = new Agreement();
				agreement.setId((Integer) agreementId);
				agreement.setDescription(rs.getString(tableCol(AGREEMENT,
						AgreementColumns.DESCRIPTION)));
				agreement.setDomain((Integer)rs.getObject(tableCol(AGREEMENT,
						AgreementColumns.DOMAIN)));
				workplace.setAgreement(agreement);
			}

			Object activityId = rs.getObject(tableCol(PAYROLL_WORKPLACE,
					PayrollWorkplaceColumns.ENTERPRISE_ACTIVITY));
			if (activityId != null) {
				Activity activity = new Activity();
				activity.setId((Integer) agreementId);
				activity.setDescription(rs.getString(tableCol(ENTERPRISE_ACTIVITY,
						EnterpriseActivityColumns.DESCRIPTION)));
				workplace.setActivity(activity);
			}

			enterpriseHandler.getEnterprise().addWorkplace(workplace);
		}

	}

	private static class CCCHandler extends AbstractHandler {

		private CCC ccc;
		private WorkplaceHandler workplaceHandler;

		public CCCHandler(WorkplaceHandler workplaceHandler) {
			super(ENTERPRISE_CCC, EnterpriseCccColumns.ID);
			this.workplaceHandler = workplaceHandler;
		}

		@Override
		public void beginGroup(ResultSet rs) throws SQLException {

			Object id = rs.getObject(tableCol(ENTERPRISE_CCC, EnterpriseCccColumns.ID));
			if ( id == null )
				return;

			ccc = new CCC();
			ccc.setId(rs.getInt(tableCol(ENTERPRISE_CCC, EnterpriseCccColumns.ID)));
			ccc.setCode(rs.getString(tableCol(ENTERPRISE_CCC, EnterpriseCccColumns.CCC)));
			ccc.setGeozone(rs.getString(tableCol(ENTERPRISE_CCC, EnterpriseCccColumns.GEOZONE)));
			ccc.setRegime(getSSRegime(rs.getInt(tableCol(ENTERPRISE_CCC, EnterpriseCccColumns.TYPE))).getCode());
			workplaceHandler.getWorkplace().getActivity().addCcc(ccc);
		}

	}

	private static Set<String> getSystemVariables(Connection conn, Date start,
			Date end) throws SQLException {
		return getSystemDescriptions(conn, start, end).keySet();
	}

	private static Map<String, String> getSystemDescriptions(Connection conn,
			Date start, Date end) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {

			stmt = conn.prepareStatement("SELECT " + SystemDataColumns.NAME
					+ ", " + SystemDataColumns.COMMENTS + " FROM "
					+ SQLConstants.SYSTEM_DATA + " WHERE "
					+ SystemDataColumns.START_DATE + " <= ? " + " AND ( "
					+ SystemDataColumns.END_DATE + " IS NULL " + " OR "
					+ SystemDataColumns.END_DATE + " >= ? " + ") ");
			stmt.setDate(1, new java.sql.Date(end.getTime()));
			stmt.setDate(2, new java.sql.Date(start.getTime()));

			Map<String, String> descriptions = new HashMap<String, String>();

			rs = stmt.executeQuery();
			while (rs.next()) {
				descriptions.put(rs.getString(SystemDataColumns.NAME),
						rs.getString(SystemDataColumns.COMMENTS));
			}
			return descriptions;
		} finally {

		}
	}

	private static void deleteAllSettles(Connection conn, int  id)
			throws SQLException {
		ResultSet rs = null ; 
		PreparedStatement settleStmt = null;
		try {
			settleStmt = conn.prepareStatement(
			String.format(
					"SELECT %s "
					+ "FROM %s "
					+ "WHERE %s = ? "
					+ "AND %s = ? "
			, SalaryColumns.ID
			, SALARY
			, SalaryColumns.CONTRACT 
			, SalaryColumns.TYPE
			));
			
			settleStmt.setInt(1, id);
			settleStmt.setInt(2, SalaryType.SETTLE.ordinal());
			rs = settleStmt.executeQuery();
			ArrayList<Integer> ids = new ArrayList<Integer>();
			while (rs.next() ) 
				ids.add(rs.getInt(SalaryColumns.ID));
			
			JooqPayrollSalaries.deleteSalaries(conn, ids);
		} finally {
			if ( rs != null )
				rs.close();
			if ( rs != null )
				settleStmt.close();
		}
		
	}
	
	private static void deleteSalaries(Connection conn, int... ids)
			throws SQLException {
		PreparedStatement dataStmt = null;
		PreparedStatement bonusStmt = null;
		PreparedStatement costsStmt = null;
		PreparedStatement paymentStmt = null;
		PreparedStatement deductionStmt = null;
		PreparedStatement embargoStmt = null;
		PreparedStatement salaryStmt = null;

		boolean autoCommit = conn.getAutoCommit();
		try {

			Character questions[] = new Character[ids.length];
			Arrays.fill(questions, 0, ids.length, '?');
			String params = asString(",", questions);

			conn.setAutoCommit(false);

			// First of all clean childs...
			dataStmt = conn
					.prepareStatement(String.format(
							"DELETE FROM %s WHERE %s IN (%s)",
							SQLConstants.SALARY_DATA, SalaryDataColumns.SALARY,
							params));
			for (int i = 1; i <= ids.length; i++)
				dataStmt.setInt(i, ids[i - 1]);
			dataStmt.execute();

			bonusStmt = conn.prepareStatement(String.format(
					"DELETE FROM %s WHERE %s IN (%s)",
					SQLConstants.SALARY_BONUS, SalaryBonusColumns.SALARY,
					params));
			for (int i = 1; i <= ids.length; i++)
				bonusStmt.setInt(i, ids[i - 1]);
			bonusStmt.execute();

			costsStmt = conn
					.prepareStatement(String.format(
							"DELETE FROM %s WHERE %s IN (%s)",
							SQLConstants.SALARY_COST, SalaryCostColumns.SALARY,
							params));
			for (int i = 1; i <= ids.length; i++)
				costsStmt.setInt(i, ids[i - 1]);
			costsStmt.execute();

			paymentStmt = conn.prepareStatement(String.format(
					"DELETE FROM %s WHERE %s IN (%s)",
					SQLConstants.SALARY_PAYMENT, SalaryPaymentColumns.SALARY,
					params));
			for (int i = 1; i <= ids.length; i++)
				paymentStmt.setInt(i, ids[i - 1]);
			paymentStmt.execute();

			deductionStmt = conn.prepareStatement(String.format(
					"DELETE FROM %s WHERE %s IN (%s)",
					SQLConstants.SALARY_DEDUCTION,
					SalaryDeductionColumns.SALARY, params));
			for (int i = 1; i <= ids.length; i++)
				deductionStmt.setInt(i, ids[i - 1]);
			deductionStmt.execute();

			embargoStmt = conn.prepareStatement(String.format(
					"DELETE FROM %s WHERE %s IN (%s)",
					SQLConstants.SALARY_EMBARGO, SalaryEmbargoColumns.SALARY,
					params));
			for (int i = 1; i <= ids.length; i++)
				embargoStmt.setInt(i, ids[i - 1]);
			embargoStmt.execute();

			salaryStmt = conn.prepareStatement(String.format(
					"DELETE FROM %s WHERE %s IN (%s)", SQLConstants.SALARY,
					SalaryColumns.ID, params));
			for (int i = 1; i <= ids.length; i++)
				salaryStmt.setInt(i, ids[i - 1]);
			salaryStmt.execute();

			conn.commit();

		} finally {
			conn.rollback();
			conn.setAutoCommit(autoCommit);

			if (bonusStmt != null)
				bonusStmt.close();
			if (costsStmt != null)
				costsStmt.close();
			if (dataStmt != null)
				dataStmt.close();
			if (paymentStmt != null)
				paymentStmt.close();
			if (embargoStmt != null)
				embargoStmt.close();
			if (deductionStmt != null)
				deductionStmt.close();
			if (salaryStmt != null)
				salaryStmt.close();

		}
	}

	private static <T> String asString(String sep, T array[]) {

		if (array.length == 0)
			return "";

		StringBuffer buff = new StringBuffer();
		buff.append(array[0]);

		for (int i = 1; i < array.length; i++) {
			buff.append(sep);
			buff.append(array[0]);
		}

		return buff.toString();
	}

	private static SalaryType toSalaryType(Salary.Type type) {
		return type != null ? SalaryType.values()[type.ordinal()] : null;
	}
	
	@SafeVarargs
	private static <T> T notNull(T... ts) {
		for (T t : ts) {
			if (t != null)
				return t;
		}
		return null;
	}

	private static NumberVariable cast(ITimedResult<Double> src) {
		NumberVariable target = new NumberVariable();
		target.setValue(src.getValue());
		target.setStartDate(src.getPeriod().getStart());
		target.setEndDate(src.getPeriod().getEnd());
		return target;
	}

	private static Variable cast(ITimedVariable<?> src) {
		Variable target = null;
		Object value = src.getValue(src.getPeriod());
		if (value instanceof MethodStub) {
			throw new ClassCastException();
		} else if (value instanceof Number) {
			target = new NumberVariable();
		} else {
			target = new StringVariable();
		}
		target.setValue(value);
		target.setStartDate(src.getPeriod().getStart());
		target.setEndDate(src.getPeriod().getEnd());
		return target;
	}

	private static List<Variable> cast(Map<String, ITimedVariable<?>> ctx) {
		List<Variable> target = new ArrayList<Variable>(ctx.size());
		for (Map.Entry<String, ITimedVariable<?>> entry : ctx.entrySet()) {

			ContextVariable contextVariable = ContextVariable
					.getVariableByName(entry.getKey());
			if (contextVariable != null && contextVariable.isInternal()) {
				continue;
			}

			try {
				Variable variable = cast(entry.getValue());
				variable.setName(entry.getKey());
				target.add(variable);
			} catch (ClassCastException e) {

			}
		}
		return target;
	}

	@Override
	public EmployeeEventsData getEmployeeEvents(String domain, int contract, ArrayList<String> employeeContractVariables) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			return JooqEmployeeEvents.getEmployeeEvents(connection, contract, employeeContractVariables);

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
	public EmployeeEventsData getEmployeeEventsByContract(String domain, Integer contractId,
			ArrayList<String> employeeContractVariablesDB) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			return JooqEmployeeEvents.getEmployeeEventsByContract(connection, contractId, employeeContractVariablesDB);

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
	public void setEmployeeEvents(String domain, int contract, EmployeeEventsUpdate updateInfo) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			JooqEmployeeEvents.setEmployeeEvents(connection, contract, updateInfo);
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
	public WorkplaceEmployees getWorkplaceEmployeesEvents(String domain, Integer workplaceId) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			return JooqEvents.getWorkplaceEmployeesEvents(connection, workplaceId);
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
	public WorkplaceEmployees getWorkplaceEmployees(String domain, Integer workplaceId) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			return JooqEvents.getWorkplaceEmployees(connection, workplaceId);
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
	public EventsWorkplace setEventsWorkplace(String domain, EventsWorkplace updateEventsWorkplace) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			return JooqEvents.setWorkplaceEmployees(connection, updateEventsWorkplace);
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
	public EmployeeContractInfo getEmployeeInfoDataBase(String domain, Integer employeeContract) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			return JooqEmployee.getEmployeeInfo(connection, employeeContract);
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
	public EmployeeContractInfo setEmployeeInfoDataBase(String domain, EmployeeContractInfo newEmployeeInfo) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			return JooqEmployee.setEmployeeInfo(connection, newEmployeeInfo);
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
	public EmployeeContractInfo createEmployeeContract(String domain, EmployeeContractInfo employeeContractData) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			return JooqEmployee.createEmployeeContract(connection, employeeContractData);
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
	
	private static boolean notAtEnterpriseSite(){
		return false;
	}
	
	private static double getOrZero(Double value) {
		return value != null ? value : 0.00;
	}

	@Override
	public String setEmployeeAFIChanges(String domain, Integer contractId, Date newDate, boolean isChangeContract, String tc2,
			boolean isQuoteContract, Integer quoteGroup, boolean isOcupationContract, String ocupation) {
		
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			return JooqEmployee.setEmployeeAFIChanges(connection, contractId, newDate, isChangeContract, tc2,
					isQuoteContract, quoteGroup, isOcupationContract, ocupation);
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
	public String resetCalendar(String domain, Integer employeeId) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			return JooqEmployeeCalendar.resetCalendar(connection, employeeId);
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
	public List<SalaryInfo> getEmployeeSalaries(String currentDomainName, Integer employeeId) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(currentDomainName);
			return JooqPayrollSalaries.getEmployeeSalaries(connection, employeeId);
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
	public String deleteSalariesDB(String currentDomainName, ArrayList<Integer> ids) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(currentDomainName);
			return JooqPayrollSalaries.deleteSalaries(connection, ids);
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
	public List<SalaryInfo> getWorkplaceSalaries(String currentDomainName, Integer workplaceId) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(currentDomainName);
			return JooqPayrollSalaries.getWorkplaceSalaries(connection, workplaceId);
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
	public WorkplaceEmployees getWorkplaceActiveEmployees(String currentDomainName, Integer workplaceId) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(currentDomainName);
			return JooqPayrollSalaries.getWorkplaceActiveEmployees(connection, workplaceId);
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
	public List<SalaryInfo> getEnterpriseSalaries(String currentDomainName, Integer enterpriseId) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(currentDomainName);
			return JooqPayrollSalaries.getEnterpriseSalaries(connection, enterpriseId);
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
	public List<EmployeeInfo> getEnterpriseActiveEmployees(String currentDomainName, Integer enterpriseId) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(currentDomainName);
			return JooqPayrollSalaries.getEnterpriseActiveEmployees(connection, enterpriseId);
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
	public List<SalaryInfo> getFilterSalaries(String currentDomainName, SalaryInfoFilter filter) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(currentDomainName);
			return JooqPayrollSalaries.getFilterEmployeeSalaries(connection, filter);
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
	
	
	private static List<Integer> getConceptIds(List<Payment> systemPayments) {
		return systemPayments.stream().filter(p -> p.getConceptId() != null).map(p -> p.getConceptId()).distinct().collect(Collectors.toList());
	}

	private static List<Payment> sub(List<Payment> paymentConcepts, List<Payment> systemPayments) {
		List<Integer> systemConceptsIds = getConceptIds(systemPayments);
		return paymentConcepts.stream().filter(p -> AonStringUtils.isBlank(p.getName()) ||  !systemConceptsIds.contains(p.getId())).collect(Collectors.toList());
	}


	private static boolean isDefault(Payment p) {
		return AonStringUtils.startsWith(p.getExpression(), "/*default*/" );
	}
	
	// ----- New employee calendar

	@Override
	public EmployeeCalendarInfo getEmployeeCalendarInfo(String domainName, Integer contractId) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domainName);
			return JooqEmployeeCalendarNew.getEmployeeCalendar(connection, contractId);
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
	public String setEmployeeCalendarInfo(String domainName, Integer contractId, EmployeeCalendarInfo employeeCalendarInfo) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			return JooqEmployeeCalendarNew.setEmployeeCalendar(connection, contractId, employeeCalendarInfo);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public String resetEmployeeCalendarInfo(String domainName, Integer contractId) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			return JooqEmployeeCalendarNew.resetEmployeeCalendar(connection, contractId);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
