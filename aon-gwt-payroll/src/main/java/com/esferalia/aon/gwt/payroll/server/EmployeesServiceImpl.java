
package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.gwt.common.server.AonServletUtils.commit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.disableAutoCommit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.enableAutoCommit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.getConnection;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.rollback;
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
import static com.esferalia.aon.watson.server.AonDateUtils.addMonths;
import static com.esferalia.aon.watson.server.AonDateUtils.getMonthFirstDay;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.stream.Collectors.summingDouble;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
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
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

import javax.faces.context.FacesContext;

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
import com.esferalia.aon.google.sql.SQLConstants.PersonColumns;
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
import com.esferalia.aon.gwt.payroll.jooq.JooqCertifica2;
import com.esferalia.aon.gwt.payroll.jooq.JooqContractAttach;
import com.esferalia.aon.gwt.payroll.jooq.JooqContractExtension;
import com.esferalia.aon.gwt.payroll.jooq.JooqContractPDF;
import com.esferalia.aon.gwt.payroll.jooq.JooqContractSEPE;
import com.esferalia.aon.gwt.payroll.jooq.JooqContractTransform;
import com.esferalia.aon.gwt.payroll.jooq.JooqContrataContract;
import com.esferalia.aon.gwt.payroll.jooq.JooqDeductions;
import com.esferalia.aon.gwt.payroll.jooq.JooqEmployee;
import com.esferalia.aon.gwt.payroll.jooq.JooqEmployeeCalendar;
import com.esferalia.aon.gwt.payroll.jooq.JooqEmployeeCalendarNew;
import com.esferalia.aon.gwt.payroll.jooq.JooqEmployeeContractPayments;
import com.esferalia.aon.gwt.payroll.jooq.JooqEmployeeContractVariables;
import com.esferalia.aon.gwt.payroll.jooq.JooqEmployeeEvents;
import com.esferalia.aon.gwt.payroll.jooq.JooqEmployeeIrpf;
import com.esferalia.aon.gwt.payroll.jooq.JooqEmployees;
import com.esferalia.aon.gwt.payroll.jooq.JooqEnterprise;
import com.esferalia.aon.gwt.payroll.jooq.JooqEvents;
import com.esferalia.aon.gwt.payroll.jooq.JooqPayments;
import com.esferalia.aon.gwt.payroll.jooq.JooqPayrollSalaries;
import com.esferalia.aon.gwt.payroll.jooq.JooqWorkplace;
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
import com.esferalia.aon.gwt.payroll.shared.Certifica2Info;
import com.esferalia.aon.gwt.payroll.shared.CompositeDeduction;
import com.esferalia.aon.gwt.payroll.shared.CompositePayment;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.ContractConceptCalc;
import com.esferalia.aon.gwt.payroll.shared.ContractExtension;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.ContractPaymentData;
import com.esferalia.aon.gwt.payroll.shared.ContractTransform;
import com.esferalia.aon.gwt.payroll.shared.ContractVariable;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Employee.Dismissal;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarUpdate;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsUpdate;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeIrpf;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.EventEmployee;
import com.esferalia.aon.gwt.payroll.shared.Events;
import com.esferalia.aon.gwt.payroll.shared.EventsWorkplace;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.ITData;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
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
import com.esferalia.aon.gwt.payroll.util.DraftPayrollBuilder;
import com.esferalia.aon.gwt.payroll.util.SettleBuilder;
import com.esferalia.aon.gwt.payroll.util.Utilities;
import com.esferalia.aon.in.payroll.pdf.JooqEnterpriseSalaryBuilder;
import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.in.payroll.pdf.maker.PdfMaker;
import com.esferalia.aon.in.payroll.pdf.maker.enterprisepayroll.beans.EnterprisePayroll;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseData;
import com.esferalia.aon.occam.api.model.PayrollWorkplace;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddressFilter;
import com.esferalia.aon.occam.api.model.Settle;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.payroll.ContractData;
import com.esferalia.aon.occam.api.model.registry.RDirStaff;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.security.CertificateNotFoundException;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.ContractAttachType;
import com.esferalia.aon.occam.api.model.type.ContractType;
import com.esferalia.aon.occam.api.model.type.ContractType.ContractTypeRecord;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.RDirStaffDAO;
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
import com.esferalia.aon.payroll.SalaryEmbargo;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.SalaryPaymentsFactory;
import com.esferalia.aon.payroll.agreement.AgreementUpdate;
import com.esferalia.aon.payroll.calculator.CollectSalaryBuilder;
import com.esferalia.aon.payroll.calculator.GenericContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
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
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryDataColumns;
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
import com.esferalia.aon.salary.data.IData;
import com.esferalia.aon.salary.deduction.Deductions;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.CheckException;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionContext.DeferredException;
import com.esferalia.aon.salary.expression.ExpressionContext.ExpressionExceptionWrapper;
import com.esferalia.aon.salary.expression.ExpressionContext.RemoveVariableError;
import com.esferalia.aon.salary.expression.ExpressionContext.RemovedExpressionVariable;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpression;
import com.esferalia.aon.salary.expression.IExpressionVariable;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.InvalidVariables;
import com.esferalia.aon.salary.expression.TimedObject;
import com.esferalia.aon.salary.expression.TimedResult;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.salary.payment.Payments;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.utils.ReportUtils;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import aon.sepe.objects.Certificates;
import aon.sepe.objects.Contract.ContractBuilder;
import aon.sepe.objects.Contract.DiscontinuoReason;
import aon.sepe.objects.Contract.JndType;
import aon.sepe.objects.Contract.OfferType;
import aon.sepe.objects.Contract.Over52Years;
import aon.sepe.objects.Contract.SexType;
import aon.sepe.objects.CopyBasic;
import jakarta.servlet.annotation.WebServlet;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import solutions.aon.seg.social.ServicioRED;
import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.invalid.DataDoesNotExist;
import solutions.aon.seg.social.object.Employee.EmployeeBuilder;
import solutions.aon.seg.social.object.SituationType;
import solutions.aon.seg.social.object.WorkerLiquidation;
import solutions.aon.sepe.Sepe;
import solutions.aon.sepe.exceptions.SepeException;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
@WebServlet(name = "EmployeesGWTServlet", urlPatterns = { "/aon_gwt_aio/employees", "/aon_gwt_aio/agreement",
		"/aon_gwt_payroll/employees", "/aon_gwt_payroll/agreement" })
public class EmployeesServiceImpl extends AonRemoteServiceServlet
		implements EmployeesService, StatisticsService, CalendarService, EmployeeEventsService {

	private static final String CONTRACT_MAX_END_DATE = "contract_max_end_date";

	public static final String REMOVE = "REMOVE()";

	private static final Map<Object, Object> JR_HTML_EXPORTER_PARAMS = new HashMap<Object, Object>() {
		{
			put(JRHtmlExporterParameter.HTML_HEADER, "<div class='page page-shadow' >");
			put(JRHtmlExporterParameter.BETWEEN_PAGES_HTML, "</div><div class='page page-shadow' >");
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
				Enterprise enterprise = getEnterprise(registryIDs[i], userID, conn);
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
	public List<Salary> getSalaries(String domain, Employee employee) throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			if (employee != null) {
				return notAtEnterpriseSite() ? getSiteSalaries(conn, employee.getId())
						: getSalaries(conn, employee.getId());
			} else {
				throw new IllegalArgumentException("EmployeeSite Not implemented yet");
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
	public List<Irpf> getIrpfs(String domain, Employee employee) throws IllegalArgumentException {
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
	public List<Extra> getExtras(String domain, List<Employee> employees) throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			Integer ids[] = employees.stream().map(e -> e.getId()).toArray(Integer[]::new);
			return JooqAgreement.getEmployeesExtras(conn, ids);
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
			JooqEmployees.insert2Person(conn, domainID, employee.getPerson(), employee.getDocument());
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
	public List<Employee> getEmployees(String domain, int workplaceId, Date fromDate, String pattern, int offset,
			int limit) throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			return JooqEmployees.getEmployees(conn, workplaceId, fromDate, pattern, offset, limit);
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
	public List<Employee> getTrashEmployees(String domain, int workplaceId) throws IllegalArgumentException {
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
	public Map<Integer, String> getHolidayDescription(String domain) throws IllegalArgumentException {

		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			Integer domainID = AonServletUtils.getDomainID(domain);
			Integer parentDomainID = AonServletUtils.getParentDomainID(domain);
			return JooqCalendar.getHolidayDescription(conn, parentDomainID, domainID);
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
	public CalendarDraft getCalendar(String domain, int workplaceId, Integer pattern, Integer year)throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domain)) {
			return JooqCalendar.getCalendar(connection, workplaceId, pattern, year);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void saveHolidaysAndDays(String domain, int workplaceId, String holidayDescription, Integer holidayListBox,
			Map<Date, String> map, CalendarDraft.DayType daysTypes[], Integer year) throws IllegalArgumentException {

		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			Integer domainID = AonServletUtils.getDomainID(domain);
			JooqCalendar.insertHolidays(conn, domainID, workplaceId, holidayDescription, holidayListBox, map,
					daysTypes, year);
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
	public void updateHolidayCalendar(String domain, int workplaceId, Integer holidayId, CalendarDraft.DayType daysTypes[]) throws IllegalArgumentException {

		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			Integer domainID = AonServletUtils.getDomainID(domain);
			JooqCalendar.updateHolidayCalendar(conn, domainID, workplaceId, holidayId, daysTypes);
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
	public void deletePropertyHoliday(String domain, Integer id, Date date) throws IllegalArgumentException {

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
	public List<Cost> getWorkplaceCosts(String domain, int workplaceId) throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			List<Cost> costs = notAtEnterpriseSite() ? getSiteWorkplaceCosts(conn, workplaceId)
					: getSLDWorkplaceCosts(conn, workplaceId);
			return costs;
		} catch (SQLException e) {
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
	public List<Cost> getEnterpriseCosts(String domain, int enterpriseId) throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			return notAtEnterpriseSite() ? getSiteEnterpriseCosts(conn, enterpriseId)
					: getEnterpriseCosts(conn, enterpriseId);
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
	public String getIrpfReceiptHTML(String domain, final Irpf irpf, int zoom) throws IllegalArgumentException {

		try {
			HashMap<Object, Object> parameters = new HashMap<Object, Object>(JR_HTML_EXPORTER_PARAMS);
			parameters.put(JRHtmlExporterParameter.ZOOM_RATIO, zoom / 100.00f /*
																				 * not roud to int
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
				public Collection<?> getCollection(boolean arg0) throws ManagerBeanException {
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
	public String getSalaryReceiptHTML(String domain, Salary salary, int zoom) throws IllegalArgumentException {

		Map<Object, Object> parameters = new HashMap<Object, Object>(JR_HTML_EXPORTER_PARAMS);
		parameters.put(JRHtmlExporterParameter.ZOOM_RATIO, zoom / 100.00f /*
																			 * not roud to int
																			 */);

		Map<Object, Object> images = new HashMap<Object, Object>();
		parameters.put(JRHtmlExporterParameter.IMAGES_MAP, images);
		String imagesUri = String.format("jasper_image/salary/%d/", salary.getId());
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

		Map<Object, Object> parameters = new HashMap<Object, Object>(JR_HTML_EXPORTER_PARAMS);
		parameters.put(JRHtmlExporterParameter.ZOOM_RATIO, zoom / 100.00f /*
																			 * not roud to int
																			 */);

		Map<Object, Object> images = new HashMap<Object, Object>();
		parameters.put(JRHtmlExporterParameter.IMAGES_MAP, images);

		String imagesUri = String.format("jasper_image/salary/%d/%d/%d/%d/", cost.getMonth(), cost.getYear(),
				cost.getWorkplaceId(), cost.getEnterpriseId());

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
	public String getCostReceiptPDF(String domain, Cost cost, Salary.Type types[]) throws IllegalArgumentException {
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			printCostReceiptPDF(domain, cost, types, os);

			byte data[] = os.toByteArray();

			ByteArrayInputStream is = new ByteArrayInputStream(data);

			Writer writer = new StringWriter();
			encodeURIComponent(MimeType.PDF.getName(), is, writer);

			is.close();
			writer.flush();
			String dataUri = writer.toString();
			writer.close();

			return dataUri;
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getCostReceiptHTML(String domain, Cost cost, Salary.Type types[], int zoom)
			throws IllegalArgumentException {
		try {
			com.esferalia.aon.occam.api.model.type.SalaryType[] occamTypes = null;
			if (types != null) {
				occamTypes = new com.esferalia.aon.occam.api.model.type.SalaryType[types.length];
				for (int i = 0; i < types.length; i++) {
					if (types[i] != null)
						occamTypes[i] = com.esferalia.aon.occam.api.model.type.SalaryType.valueOf(types[i].toString());
					else
						occamTypes[i] = null;
				}
			}

			ByteArrayOutputStream oos = new ByteArrayOutputStream();

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.MONTH, cost.getMonth());
			calendar.set(Calendar.YEAR, cost.getYear());
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			Date startDate = calendar.getTime();

			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

			Date endDate = calendar.getTime();

			JooqEnterpriseSalaryBuilder.generateEnterprisePayroll(oos, domain, "", startDate, endDate,
					cost.getEnterpriseId(), cost.getWorkplaceId(), occamTypes);

			byte bytes[] = oos.toByteArray();
						
			InputStream data = new ByteArrayInputStream(bytes);

			StringWriter writer = new StringWriter();

			encodeURIComponent(MimeType.PDF.getName(), data, writer);

			return writer.toString();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getSLDCalcReceiptHTML(String domain, String user, Cost cost, Salary.Type types[], int zoom)
			throws IllegalArgumentException {
		try {
			ReportManager reportManager = new StatelessReportManager();
			reportManager.setOutputFormat(OutputFormat.HTML);

			SalaryType salaryTypes[] = new SalaryType[types.length];
			for (int i = 0; i < types.length; i++)
				salaryTypes[i] = SalaryType.values()[types[i].ordinal()];

			// AON.getCertificate(domain, domainId, login, userId);
			// SistemaRED.getCosts(certificateData, certificatePassword, certificateType,
			// regimen, ccc, startDate, endDate);

			reportManager.setCollectionProvider(getSLDSalariesProvider(domain, user, cost, salaryTypes, false));

			Map<Object, Object> parameters = new HashMap<Object, Object>(JR_HTML_EXPORTER_PARAMS);

			// Really I hate this spaghetti piece of code.
			// For pass 'month' & 'year' to a report, we
			// must put it in a controller ?????.
			// SalaryExpenseController controller = (SalaryExpenseController) AonUtil
			// .getRegisteredBean(IPayrollConstants.SALARY_EXPENSE_CONTROLLER_NAME);
			// controller.setShowSalaryExpenseWindow(false);
			// controller.setYear(cost.getYear());
			// Month month = Month.getMonthByValue(cost.getMonth());
			// controller.setMonth(month);
			parameters.put("#{salaryExpense.getYear}", cost.getYear());
			parameters.put("#{salaryExpense.getMonth}", cost.getMonth());

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			parameters.put(JRHtmlExporterParameter.ZOOM_RATIO, zoom / 100.00f /*
																				 * not round to int
																				 */);
			Map<Object, Object> images = new HashMap<Object, Object>();
			parameters.put(JRHtmlExporterParameter.IMAGES_MAP, images);

			String imagesUri = String.format("jasper_image/salary/%d/%d/%d/%d/", cost.getMonth(), cost.getYear(),
					cost.getWorkplaceId(), cost.getEnterpriseId());

			parameters.put(JRHtmlExporterParameter.IMAGES_URI, imagesUri);

			reportManager.execute(out, IPayrollConstants.COST_REPORT, parameters);

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

		Map<Object, Object> parameters = new HashMap<Object, Object>(JR_HTML_EXPORTER_PARAMS);
		parameters.put(JRHtmlExporterParameter.ZOOM_RATIO, zoom / 100.00f /*
																			 * not roud to int
																			 */);

		Map<Object, Object> images = new HashMap<Object, Object>();
		parameters.put(JRHtmlExporterParameter.IMAGES_MAP, images);
		String imagesUri = String.format("jasper_image/salary/%d/", salaryDraft.getEmployee().getId());

		parameters.put(JRHtmlExporterParameter.IMAGES_URI, imagesUri);

		String html = getSalaryDraftReceiptHTML(domain, salaryDraft, parameters);

		for (Entry<Object, Object> image : images.entrySet()) {
			String name = String.format("%s%s", imagesUri, image.getKey());
			JasperImageServlet.saveImage(name, (byte[]) image.getValue());
		}

		return html;

	}

	@Override
	public String getAgreementDraftReceiptHTML(String domain, AgreementDraft agreementDraft, int levelId,
			Salary.Type type, int zoom) throws IllegalArgumentException {

		Map<Object, Object> parameters = new HashMap<Object, Object>(JR_HTML_EXPORTER_PARAMS);
		parameters.put(JRHtmlExporterParameter.ZOOM_RATIO, zoom / 100.00f /*
																			 * not roud to int
																			 */);

		Map<Object, Object> images = new HashMap<Object, Object>();
		parameters.put(JRHtmlExporterParameter.IMAGES_MAP, images);
		String imagesUri = String.format("jasper_image/agreement/%d/%d", agreementDraft.getId(), levelId);

		parameters.put(JRHtmlExporterParameter.IMAGES_URI, imagesUri);

		String html = getAgreementDraftReceiptHTML(domain, agreementDraft, levelId, type, parameters);

		for (Entry<Object, Object> image : images.entrySet()) {
			String name = String.format("%s%s", imagesUri, image.getKey());
			JasperImageServlet.saveImage(name, (byte[]) image.getValue());
		}

		return html;
	}

	@Override
	public String getIrpfDraftReceiptHTML(String domain, SalaryDraft salaryDraft, int zoom)
			throws IllegalArgumentException {

		Map<Object, Object> parameters = new HashMap<Object, Object>(JR_HTML_EXPORTER_PARAMS);
		parameters.put(JRHtmlExporterParameter.ZOOM_RATIO, zoom / 100.00f /*
																			 * not roud to int
																			 */);

		parameters.put(JRParameter.REPORT_LOCALE, new Locale("es", "ES"));
		String html = getIrpfDraftReceiptHTML(domain, salaryDraft, parameters);

		return html;

	}

	@Override
	public List<Result> eval(String domain, String expression, SalaryDraft salaryDraft)
			throws IllegalArgumentException, EvalException {
		List<ITimedResult<Double>> results = eval(domain, expression, salaryDraft, Double.class);
		List<Result> returnList = new ArrayList<Result>(results.size());
		for (ITimedResult<Double> result : results) {
			returnList.add(new Result(cast(result), cast(result.getContext())));
		}

		return returnList;
	}

	@Override
	public List<Result> eval(String domain, String expression, AgreementDraft agreementDraft, int levelId)
			throws IllegalArgumentException, EvalException {
		List<ITimedResult<Double>> results = eval(domain, expression, agreementDraft, levelId, Double.class);

		List<Result> returnList = new ArrayList<Result>(results.size());
		for (ITimedResult<Double> result : results) {
			returnList.add(new Result(cast(result), cast(result.getContext())));
		}

		return returnList;
	}
	
	@Override
	public List<Result> evalAgreement(String domain, String expression, Date startDate, int levelId)
			throws IllegalArgumentException, EvalException {
		List<ITimedResult<Double>> results = eval(domain, expression, startDate, levelId, Double.class);

		List<Result> returnList = new ArrayList<Result>(results.size());
		for (ITimedResult<Double> result : results) {
			returnList.add(new Result(cast(result), cast(result.getContext())));
		}

		return returnList;
	}

	@Override
	public ContextDescriptor getContext(String domain, SalaryDraft salaryDraft) {
		// return new ContextDescriptor();
		return getDraftContext(domain, salaryDraft);
	}

	@Override
	public ContextDescriptor getContext(String domain, AgreementDraft agreementDraft, int levelId)
			throws IllegalArgumentException {
		return getDraftContext(domain, agreementDraft, levelId);
	}
	
	@Override
	public ContextDescriptor getAgreementContext(String domain, int levelId, Date startDate, Date endDate)
			throws IllegalArgumentException {
		return getAgreementDraftContext(domain, startDate, endDate, levelId);
	}

	@Override
	public Double calculateIrpf(String domain, SalaryDraft salaryDraft) throws IllegalArgumentException {
		try {
			return 0.00;// getIrpf(salaryDraft);
		} finally {
		}

	}

	@Override
	public SalaryDraft calculateSalaryDraft(String domain, SalaryDraft salaryDraft) throws IllegalArgumentException {
		calculate(domain, salaryDraft, new SmartContractSalaryCalculator<ISalary>() {
			@Override
			protected void fillData(IContractSalaryCalculatorContext ctx) throws SalaryException {
				super.fillData(ctx);
				fillData(ctx, new ContextVariable[] { ContextVariable.SENIORITY });
			}
		});
		return salaryDraft;
	}

	@Override
	public SalaryDraft calculateSalaryDraft(String domain, SalaryDraft salaryDraft, Date sections[])
			throws IllegalArgumentException {

		long draftDays = new com.esferalia.aon.salary.expression.Period(salaryDraft.getStartDate(),
				salaryDraft.getEndDate()).daysStream().count();

		calculate(domain, salaryDraft, new SmartContractSalaryCalculator<ISalary>() {

			@Override
			protected List<ITimedResult<Double>> fixConstantResult(IContractPayment contractPayment,
					ITimedResult<Double> result, Date start, Date end, ExpressionContext expressionContext)
					throws UnsupportedOperationException, UndefinedVariablesException {
				long sectionDays = new com.esferalia.aon.salary.expression.Period(start, end).daysStream().count();
				ITimedResult<Double> sectionResult = new TimedResult<Double>(
						result.getValue() / draftDays * sectionDays, result.getPeriod(), result.getContext());
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

				ITimedResult<Double> fixed = new TimedResult<Double>(value, result.getPeriod(), result.getContext());

				return Collections.singletonList(fixed);
			}

		}, sections);
		return salaryDraft;
	}

	@Override
	public SalaryDraft syncSalaryDraft(String domain, String user, SalaryDraft salaryDraft)
			throws IllegalArgumentException {
		try (Connection conn = AonServletUtils.getConnection(domain)) {
			syncBonus(conn, domain, user, salaryDraft);
		} catch (Throwable t) {

		}
		calculate(domain, salaryDraft, new SmartContractSalaryCalculator<ISalary>());
		return salaryDraft;
	}

	@Override
	public Map<String, String> getEventsVariables(String domain, Integer workplaceId, Integer agreementId,
			Date startDate, Date endDate) throws IllegalArgumentException {
		if (agreementId == null)
			return Collections.emptyMap();

		try {
			Integer domainID = AonServletUtils.getDomainID(domain);
			Integer parentDomainID = AonServletUtils.getParentDomainID(domain);

			return getWorkplaceEventsVariables(domain, workplaceId, agreementId, startDate, endDate, domainID,
					parentDomainID);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}

	}

	@Override
	public Map<String, String> getWorkplaceEventsVariables(String domain, Integer workplaceId, Integer agreementId,
			Date startDate, Date endDate) throws IllegalArgumentException {

		if (agreementId == null)
			return Collections.emptyMap();

		try {
			Integer domainID = AonServletUtils.getDomainID(domain);
			Integer parentDomainID = AonServletUtils.getParentDomainID(domain);

			return getEventsVariables(domain, workplaceId, agreementId, startDate, endDate, domainID, parentDomainID);

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
					+ ", StartDate : " + startDate + ", endDate : " + endDate);

			// Get info
			connection = AonServletUtils.getConnection(domain);
			Integer domainID = AonServletUtils.getDomainID(domain);
			Integer parentDomainID = AonServletUtils.getParentDomainID(domain);

			Integer agreementId = SQLEvents.getAgreementId(connection, employeeId);

			// ContextResult returned
			ContextDescriptor contextResult = new ContextDescriptor();

			// Criteria
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(SQLConstants.CONTRACT + "." + ContractColumns.ID, employeeId);

			Date iteratorDate = DateUtils.copyDateOnly(startDate);
			while (iteratorDate.before(endDate)) {

				if (connection.isClosed())
					connection = AonServletUtils.getConnection(domain);

				// Context
				SQLContractSalaryCalculatorContext context = new SQLContractSalaryCalculatorContext(connection,
						iteratorDate, AonDateUtils.getMonthLastDay(iteratorDate),
						AonDateUtils.getMonthLastDay(iteratorDate), criteria);

				if (context.next()) {

					// Salary Calculator
					SmartContractSalaryCalculator<com.esferalia.aon.payroll.Salary> smartContractSalaryCalculator = new SmartContractSalaryCalculator<>(
							new SalaryBuilder());

					smartContractSalaryCalculator.setListener(new GenericContractSalaryCalculator.IListener() {

						@Override
						public void onUndefinedData(IContractDeduction deduction, String variableName, String message) {
						}

						@Override
						public void onUndefinedData(IContractDeduction deduction, RemovedExpressionVariable<?> var) {
						}

						@Override
						public void onUndefinedData(IContractPayment payment, String variableName, String message) {
							if (payment.getScope() != ExpressionScope.SYSTEM && !variableName.contains("DIAS_"))
								contextResult.add(variableName);
						}

						@Override
						public void onUndefinedData(IContractPayment payment, RemovedExpressionVariable<?> var) {
						}

						@Override
						public void onRemove(IContractPayment payment) {
						}

						@Override
						public void onRemove(IContractDeduction payment) {
						}

						@Override
						public void onRemove(IContractBonus bonus) {
						}

						@Override
						public void onInvalidData(IContractBonus bonus, String variableName, String message) {
						}

						@Override
						public void onInvalidData(IContractDeduction deduction, String variableName, String message) {
						}

						@Override
						public void onInvalidData(IContractPayment payment, String variableName, String message) {
						}

						@Override
						public void onInvalidData(String variableName, String message) {
						}

						@Override
						public void onCompileError(IContractBonus bonus, String message) {
						}

						@Override
						public void onCompileError(IContractDeduction deduction, String message) {
						}

						@Override
						public void onCompileError(IContractPayment payment, String message) {
						}

						@Override
						public void onCompileError(String variableName, String message) {
						}

						@Override
						public void onCheckError(IContractBonus bonus, String message) {
						}

						@Override
						public void onCheckError(IContractDeduction deduction, String message) {
						}

						@Override
						public void onCheckError(IContractPayment payment, String message) {
						}

						@Override
						public void onCheckError(String message) {
						}

					});

					// Salary
					try {
						smartContractSalaryCalculator.calculate(context);
					} catch (SalaryException e) {
						e.printStackTrace();
					}

					// ContextDrescriptor
					ContextDescriptor contextDescriptor = getContext(connection, context, startDate, endDate);

//					ContextDescriptor contextDescriptorPayments = getEmployeePayments(connection, employeeId, agreementId,
//							startDate, endDate, domainID, parentDomainID);

					ContextDescriptor contextDescriptorPayments = getEmployeePayments(connection, employeeId,
							agreementId, iteratorDate, AonDateUtils.getMonthLastDay(iteratorDate), domainID,
							parentDomainID);

					contextDescriptorPayments.mixAll(contextDescriptor);

					for (String key : contextDescriptorPayments.getVariables()) {
						if (!contextDescriptorPayments.getList(key).isEmpty()) {
							for (VariableDescriptor variable : contextDescriptorPayments.getList(key)) {
								if (Number.class != variable.getType() || null == variable.getScope()
										|| Scope.APPLICATION == variable.getScope()
										|| Scope.SYSTEM == variable.getScope())
									continue;

								contextResult.add(key, variable);
							}
						} else
							contextResult.add(key);
					}
				}

				iteratorDate = AonDateUtils.addMonths(iteratorDate, 1);
			}

			return contextResult;

		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO: handle exception
			}
		}
	}

	@Override
	public List<Variable> getVariables(String domain, SalaryDraft salaryDraft, Date startDate, Date endDate,
			String[] names) throws IllegalArgumentException {
		Connection connection = null;
		try {
			salaryDraft.setStartDate(startDate);
			salaryDraft.setEndDate(endDate);
			connection = AonServletUtils.getConnection(domain);
			SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext> ctx = EmployeesServiceHelper
					.getSalaryCalculatorContext(connection, salaryDraft, null);

			Map<String, boolean[]> defined = EmployeesServiceHelper.getDefinedMap(ctx);

			List<Variable> variables = new LinkedList<Variable>();
			for (String name : names) {

				List<ITimedResult<Object>> results = Collections.emptyList();
				try {
					results = ctx.getExpressionContext().eval(name, startDate, endDate);
				} catch (DeferredException e) {
					e.eval(ctx.getExpressionContext(), Object.class);
					results = ctx.getExpressionContext().eval(name, startDate, endDate);
				} catch (Exception e) {
					continue;
				}

				for (ITimedResult<Object> var : results) {

					IExpression expression = var instanceof IExpressionVariable<?>
							? ((IExpressionVariable<?>) var).getExpression()
							: null;

					ContextVariable contextVariable = ContextVariable.getVariableByName(name);
					try {

						Object value = var.getValue(var.getPeriod());

						if (value instanceof String || value instanceof Boolean || value instanceof Number)
							variables.add(new StringVariable.Builder().setName(name)
									.setStartDate(var.getPeriod().getStart()).setEndDate(var.getPeriod().getEnd())
									.setValue(var.getValue(var.getPeriod())).setDefined(defined.get(name))
									.setExpression(expression != null ? expression.getExpression() : null)
									.setImplicit(contextVariable != null && contextVariable.isInternal())
									.setScope((expression != null && expression.getScope() != null)
											? Scope.values()[expression.getScope().ordinal()]
											: null)
									.create());
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
			EmployeesServiceHelper.calculate(connection, agreementDraft, AonServletUtils.getDomainID(domain),
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
	public AgreementDraft saveAgreementDraft(String domain, String userLogin, AgreementDraft agreementDraft) throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			disableAutoCommit(conn);
			SQLAgreementDraft.save(conn, agreementDraft, agreementDraft.getDomain(),
					AonServletUtils.getParentDomainID(conn, agreementDraft.getDomain()), userLogin);
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
	public void saveITDataPerson(String domain, Map<Integer, LinkedHashMap<Integer, ITDataPerson>> inserts,
			Map<Integer, LinkedHashMap<Integer, ITDataPerson>> deletes,
			Map<Integer, LinkedHashMap<Integer, ITDataPerson>> updates) throws IllegalArgumentException {
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
	public Map<String, String> getAvaiableEmployees(String domain) {

		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			Integer domainID = AonServletUtils.getDomainID(domain);
			Integer parentDomainID = AonServletUtils.getParentDomainID(domain);

			disableAutoCommit(conn);
			return JooqEmployees.getAvaiableEmployees(conn, domainID, parentDomainID);
		} catch (SQLException ex) {
			rollback(conn);
			ex.printStackTrace();
			throw new IllegalArgumentException(ex);
		} finally {

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException ex) {
				}
			}
		}

	}

	@Override
	public Employee pasteContract(String domain, int workplaceId, int contractId, String document, Date startDate,
			Date endDate, boolean check) throws IllegalArgumentException {

		Connection conn = null;
		try {

			conn = AonServletUtils.getConnection(domain);
			Integer domainID = AonServletUtils.getDomainID(domain);
			disableAutoCommit(conn);
			Employee employee = JooqEmployees.paste(conn, domainID, workplaceId, contractId, document, startDate,
					endDate, check);
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
	public void moveContractId(String domain, Employee employee) throws IllegalArgumentException {
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
	public void deleteContract(String domain, Employee employee) throws IllegalArgumentException {

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
	public void saveSalaryDraft(String domain, SalaryDraft salaryDraft) throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			Integer domainID = AonServletUtils.getDomainID(domain);
			Integer parentDomainID = AonServletUtils.getParentDomainID(domain);
			disableAutoCommit(conn);
			SQLSalaryDraft.save(conn, salaryDraft, domainID, parentDomainID);
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
	public SalaryDraft saveSalary(String domain, String user, SalaryDraft salaryDraft) throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			// syncBonus(conn, domain, user, salaryDraft);
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
	public SalaryDraft saveSalary(String domain, SalaryDraft salaryDraft, Date sections[])
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

		Connection conn = null;
		try {
		    	conn = AonServletUtils.getConnection(domain);
			ByteArrayOutputStream reportOut = new ByteArrayOutputStream();
			com.esferalia.aon.payroll.Salary salary = EmployeesServiceHelper.calculate(conn, draft, new SmartContractSalaryCalculator<>(new SalaryBuilder()));

			DraftPayrollBuilder.generatePayroll(reportOut, domain, salary);
			byte [] reportByteArray = reportOut.toByteArray();

			ByteArrayInputStream reportInput = new ByteArrayInputStream(reportByteArray);

			Writer stringWriter = new StringWriter();
			encodeURIComponent(mime, reportInput, stringWriter);

			reportOut.close();
			reportInput.close();
			stringWriter.flush();
			String dataUri = stringWriter.toString();
			stringWriter.close();

			return dataUri;

		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} catch (SalaryException | CanNotCreatePdfException e) {
			throw new IllegalArgumentException(e);
		}finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
		}
	}

	@Override
	public String getAgreementDraftReceipt(String domain, AgreementDraft agreementDraft, List<Variable> context, int levelId, Type type,
			String mime) throws IllegalArgumentException {

		try {

			ByteArrayOutputStream reportOut = new ByteArrayOutputStream();
			com.esferalia.aon.payroll.Salary salary = getSalary(domain, agreementDraft, context, levelId);

			try {
				DraftPayrollBuilder.generatePayroll(reportOut, domain, salary);
			} catch (SalaryException | CanNotCreatePdfException e) {
			}

			byte reportByteArray[] = reportOut.toByteArray();

			ByteArrayInputStream reportInput = new ByteArrayInputStream(reportByteArray);

			Writer stringWriter = new StringWriter();
			encodeURIComponent(mime, reportInput, stringWriter);

			reportOut.close();
			reportInput.close();
			stringWriter.flush();
			String dataUri = stringWriter.toString();
			stringWriter.close();

			return dataUri;

		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Settle draft
	 * 
	 * @param domain
	 * @param draft
	 * @param mime
	 * @return [String] datauri
	 * @throws IllegalArgumentException
	 */
	@Override
	public String getSettleDraftReceipt(String domain, final SalaryDraft draft, String mime)
			throws IllegalArgumentException {

		try {
			ByteArrayOutputStream reportOut = new ByteArrayOutputStream();
			Settle settle = getSettle(domain, draft);

			try {
				SettleBuilder.printDraftSettle(settle, reportOut, new Locale("Es"),
						Utilities.getLogo(domain).orElse(new ByteArrayInputStream(new byte[0])),
						Utilities.getSignature(domain).orElse(new ByteArrayInputStream(new byte[0])));
			} catch (CanNotCreatePdfException ignored) {
			}

			byte reportByteArray[] = reportOut.toByteArray();
			ByteArrayInputStream reportInput = new ByteArrayInputStream(reportByteArray);

			Writer stringWriter = new StringWriter();
			encodeURIComponent(mime, reportInput, stringWriter);

			reportOut.close();
			reportInput.close();
			stringWriter.flush();
			String dataUri = stringWriter.toString();
			stringWriter.close();

			return dataUri;

		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

//	@SuppressWarnings("unchecked")
//	@Override
//	public String getSalaryDraftReceipt(String domain, final SalaryDraft draft, String mime)
//			throws IllegalArgumentException {
//
//		try {
//			ReportManager reportManager = new StatelessReportManager();
//			reportManager.setOutputFormat(OutputFormat.PDF);
//
//			ICollectionProvider provider = new ICollectionProvider() {
//
//				@Override
//				public Collection<?> getCollection() {
//					try {
//						return getCollection(true);
//					} catch (ManagerBeanException e) {
//									return null;
//					}
//				}
//
//				@Override
//				public Collection<?> getCollection(boolean arg0)
//						throws ManagerBeanException {
//					return Collections.singletonList(getSalary(domain, draft));
//				}
//
//			};
//
//			reportManager.setCollectionProvider(provider);
//
//			ByteArrayOutputStream reportOut = new ByteArrayOutputStream();
//
//			String salaryReport = getSalaryReport(domain, toSalaryType(draft.getType()));
//
//			reportManager.execute(reportOut, salaryReport);
//
//			byte reportByteArray[] = reportOut.toByteArray();
//
//			ByteArrayInputStream reportInput = new ByteArrayInputStream(
//					reportByteArray);
//
//			Writer stringWriter = new StringWriter();
//			encodeURIComponent(mime, reportInput, stringWriter);
//
//			reportOut.close();
//			reportInput.close();
//			stringWriter.flush();
//			String dataUri = stringWriter.toString();
//			stringWriter.close();
//
//			return dataUri;
//
//		} catch (ReportException e) {
//			throw new IllegalArgumentException(e);
//		} catch (IOException e) {
//			throw new IllegalArgumentException(e);
//		}
//	}

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
				public Collection<?> getCollection(boolean arg0) throws ManagerBeanException {
					return Collections.singletonList(getIrpfOutcome(domain, draft));
				}

			};

			reportManager.setCollectionProvider(provider);

			ByteArrayOutputStream reportOut = new ByteArrayOutputStream();
			reportManager.execute(reportOut, "irpf");

			byte reportByteArray[] = reportOut.toByteArray();

			ByteArrayInputStream reportInput = new ByteArrayInputStream(reportByteArray);

			Writer stringWriter = new StringWriter();
			encodeURIComponent(mime, reportInput, stringWriter);

			reportOut.close();
			reportInput.close();
			stringWriter.flush();
			String dataUri = stringWriter.toString();
			stringWriter.close();

			return dataUri;

		} catch (ReportException e) {
			throw new IllegalArgumentException(getRootCause(e).getMessage());
		} catch (IOException e) {
			throw new IllegalArgumentException(getRootCause(e).getMessage());
		}

	}

	@Override
	public String getSalaryPreviewReceiptHTML(String domain, SalaryPreview salaryPreview, int zoom)
			throws IllegalArgumentException {
		Map<Object, Object> parameters = new HashMap<Object, Object>(JR_HTML_EXPORTER_PARAMS);
		parameters.put(JRHtmlExporterParameter.ZOOM_RATIO, zoom / 100.00f /*
																			 * not roud to int
																			 */);

		Map<Object, Object> images = new HashMap<Object, Object>();
		parameters.put(JRHtmlExporterParameter.IMAGES_MAP, images);
		String imagesUri = String.format("jasper_image/salary/%d/", salaryPreview.getEmployee().getId());

		parameters.put(JRHtmlExporterParameter.IMAGES_URI, imagesUri);

		String html = getSalaryPreviewReceiptHTML(domain, salaryPreview, parameters);

		for (Entry<Object, Object> image : images.entrySet()) {
			String name = String.format("%s%s", imagesUri, image.getKey());
			JasperImageServlet.saveImage(name, (byte[]) image.getValue());
		}

		return html;
	}

	@Override
	public List<Payment> getAvailablePayments(String domain, int employeeId) throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);

			int domainId = AonServletUtils.getDomainID(domain);

			List<Payment> paymentConcepts = JooqPayments.getPaymentConcepts(conn, 0, 0);
//			List<Payment> paymentConcepts = JooqPayments.getPaymentConcepts(
//					conn, domainId, AonServletUtils.getParentDomainID(domain));
			List<Payment> employeePayments = Collections.emptyList();
			/* getEmployeePayments(conn, employeeId); */
			List<Payment> enterprisePayments = Collections.emptyList();
			/* getEnterprisePayments(conn, domainId); */
			List<Payment> systemPayments = JooqPayments.getPayments(conn, 0, 0);
			systemPayments = systemPayments.stream().filter(p -> !isDefault(p)).collect(Collectors.toList());

			paymentConcepts = sub(paymentConcepts, systemPayments);

			List<Payment> payments = new ArrayList<Payment>(
					paymentConcepts.size() + employeePayments.size() + enterprisePayments.size());

			payments.addAll(paymentConcepts);
			payments.addAll(employeePayments);
			payments.addAll(enterprisePayments);

//			System.out.println("----- Payment Suggest -----");
//			payments.forEach(paymentIt -> System.out.println(paymentIt.getType().getCode() + " - " + paymentIt.getDescription() + " --> " + paymentIt.getExpression() + " (id : " + paymentIt.getId() + ")"));
			
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
	public List<Deduction> getAvailableDeductions(String domain, int employeeId) throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			Integer domainId = AonServletUtils.getDomainID(domain);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domain);

			return JooqDeductions.getConcepts(conn, domainId, parentDomainId);

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
	public List<Bonus> getAvailableBonuses(String domain, int employeeId) throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			List<Bonus> availableBonuses = new ArrayList<Bonus>();
			for (Bonus bonus : EmployeesServiceHelper.getAvailableBonuses(conn, employeeId, 0))
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
	public Events getEvents(String domain, Integer workplaceId, Date startDate, Date endDate, int offset, int limit,
			String names[]) {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			return SQLEvents.getEvents(conn, workplaceId, startDate, endDate, offset, limit, names);

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
	public Period getAvailPeriod(String domain, Integer workplaceId, String name) throws IllegalArgumentException {
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
	public Statistics getEnterpriseStats(String domain, int enterpriseId) throws IllegalArgumentException {
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
	public Statistics getWorkplaceStats(String domain, int workplaceId) throws IllegalArgumentException {
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
	public ITData getEnterpriseITData(String domain, int enterpriseId) throws IllegalArgumentException {
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
	public ITData getWorkplaceITData(String domain, int workplaceId) throws IllegalArgumentException {
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
	public SortedSet<Date> getChanges(String domain, Agreement agreement) throws IllegalArgumentException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);

			Integer domainId = AonServletUtils.getDomainID(domain);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domain);

			return parentDomainId != null
					? SQLAgreementDraft.getDatesWithChanges(conn, agreement.getId(), domainId, parentDomainId,
							agreement.getDomain())
					: SQLAgreementDraft.getDatesWithChanges(conn, agreement.getId(), domainId, agreement.getDomain());

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

	private String getSalaryReport(String domain, SalaryType salaryType) throws ReportException {
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
	private String getSalaryReceiptHTML(String domain, Salary salary, Map<Object, Object> parameters)
			throws IllegalArgumentException {

		try {

			String salaryReport = getSalaryReport(domain, toSalaryType(salary.getType()));

			Condition condition = com.esferalia.aon.jooq.tables.Salary.SALARY.ID.eq(salary.getId());

			FacesContext ctx = FacesContext.getCurrentInstance();
			ctx.getViewRoot().setLocale(new Locale("es", "ES"));
			ReportManager reportManager = new StatelessReportManager();

			reportManager.setOutputFormat(OutputFormat.HTML);
			reportManager.setCollectionProvider(new PayrollServletUtils.SalaryProvider(domain, condition, null));

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			reportManager.execute(out, salaryReport, parameters);

			return out.toString();

		} catch (ReportException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private String getSalaryReceiptHTML(String domain, Cost cost, Salary.Type types[], Map<Object, Object> parameters)
			throws IllegalArgumentException {
		try {
			ReportManager reportManager = new StatelessReportManager();
			reportManager.setOutputFormat(OutputFormat.HTML);

			SalaryType salaryTypes[] = new SalaryType[types.length];
			for (int i = 0; i < types.length; i++)
				salaryTypes[i] = SalaryType.values()[types[i].ordinal()];

			reportManager.setCollectionProvider(getSalariesProvider(domain, cost, salaryTypes, false));

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
	private String getSalaryPreviewReceiptHTML(String domain, final SalaryPreview draft, Map<Object, Object> parameters)
			throws IllegalArgumentException {

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
				public Collection<?> getCollection(boolean arg0) throws ManagerBeanException {
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
	private String getSalaryDraftReceiptHTML(String domain, final SalaryDraft draft, Map<Object, Object> parameters)
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
				public Collection<?> getCollection(boolean arg0) throws ManagerBeanException {
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
	private String getAgreementDraftReceiptHTML(String domain, final AgreementDraft draft, final int levelId,
			Salary.Type type, Map<Object, Object> parameters) throws IllegalArgumentException {

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
				public Collection<?> getCollection(boolean arg0) throws ManagerBeanException {
					return Collections.singletonList(getSalary(domain, draft, Collections.emptyList(), levelId));
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
	private String getIrpfDraftReceiptHTML(String domain, final SalaryDraft draft, Map<Object, Object> parameters)
			throws IllegalArgumentException {

		try {
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
				public Collection<?> getCollection(boolean arg0) throws ManagerBeanException {
					return Collections.singletonList(getIrpfOutcome(domain, draft));
				}

			};

			reportManager.setCollectionProvider(provider);

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			String irpfReport = "irpf";
			reportManager.execute(out, irpfReport, parameters);

			return out.toString();

		} catch (ReportException e) {
			Throwable cause = getRootCause(e);
			throw new IllegalArgumentException(cause);
		}
	}

	// ------------------------------------------------- Private Static methods

	// Note that below methods can be moved to another place safely.

	private static ICollectionProvider getSalariesProvider(String domain, Cost cost, SalaryType types[])
			throws ManagerBeanException {
		boolean asEnterpriseSite = notAtEnterpriseSite();
		boolean calc = !notAtEnterpriseSite();
		SalaryFilter filter = asEnterpriseSite ? new SiteFilter() : null;
		return getSalariesProvider(domain, cost, types, filter, calc);
	}

	private static ICollectionProvider getSalariesProvider(String domain, Cost cost, SalaryType types[], boolean calc)
			throws ManagerBeanException {
		boolean asEnterpriseSite = notAtEnterpriseSite();
		SalaryFilter filter = asEnterpriseSite ? new SiteFilter() : null;
		return getSalariesProvider(domain, cost, types, filter, calc);
	}

	private ICollectionProvider getSLDSalariesProvider(String domainName, String userLogin, Cost cost,
			SalaryType types[], boolean calc) throws ManagerBeanException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			Calendar calendar = getDate(cost);
			Date startDate = calendar.getTime();
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			Date endDate = calendar.getTime();

			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");

			Map<String, Map<String, Map<String, WorkerLiquidation>>> cccSldCosts = new HashMap<String, Map<String, Map<String, WorkerLiquidation>>>();

			PAYROLL.getCCCStream(domainName, domainId, userLogin).forEach(ccc -> {
				try {
					Map<String, Map<String, WorkerLiquidation>> cccSldCost = SistemaRED.getCosts(
							certificate.getData(), certificate.getPassword(), certificate.getType(),
							ccc.getCccRegimeCode(), ccc.getCcc(), startDate, endDate);
					cccSldCosts.put(ccc.getCcc(), cccSldCost);

				} catch (DataDoesNotExist e) {

				} catch (SegSocialException e) {
					throw new IllegalArgumentException(e);
				}
			});

			if (cccSldCosts.isEmpty())
				throw new IllegalArgumentException(new DataDoesNotExist("NO EXISTEN DATOS PARA LA FECHA INTRODUCIDA"));

			ICollectionProvider salariesProvider = getSalariesProvider(domainName, cost, types, calc);
			Collection<?> salaries = salariesProvider.getCollection(true);

			List<com.esferalia.aon.payroll.Salary> sldSalaries = new ArrayList<com.esferalia.aon.payroll.Salary>();

			for (Object object : salaries) {
				com.esferalia.aon.payroll.Salary salary = (com.esferalia.aon.payroll.Salary) object;

				WorkerLiquidation liquidation = cccSldCosts.getOrDefault(salary.getCcc(), Collections.emptyMap())
						.getOrDefault(getLiquidacion(salary.getType()), Collections.emptyMap())
						.get(salary.getSocialSecurityNumber());

				if (liquidation == null)
					continue;

				com.esferalia.aon.payroll.SalaryData totalEnterprise = new com.esferalia.aon.payroll.SalaryData();
				totalEnterprise.setName("TOTAL_ENTERPRISE");
				totalEnterprise
						.setExpression(Double.toString(Math.round(salary.getTotalEnterprise() * 100.00) / 100.00));
				salary.getSalaryDatas().add(totalEnterprise);
				salary.setTotalEnterprise(Optional.ofNullable(liquidation.getTotalLiquidBusinessFee())
						.map(d -> Double.parseDouble(d.toString())).orElse(0.00));

				com.esferalia.aon.payroll.SalaryData socialSecurityContributions = new com.esferalia.aon.payroll.SalaryData();
				socialSecurityContributions.setName("SOCIAL_SECURITY_CONTRIBUTIONS");
				socialSecurityContributions.setExpression(
						Double.toString(Math.round(salary.getSocialSecurityContributions() * 100.00) / 100.00));
				salary.getSalaryDatas().add(socialSecurityContributions);
				salary.setSocialSecurityContributions(Optional.ofNullable(liquidation.getTotalLiquidWorkerFee())
						.map(d -> Double.parseDouble(d.toString())).orElse(0.00));

				sldSalaries.add(salary);

				cccSldCosts.getOrDefault(salary.getCcc(), Collections.emptyMap())
						.getOrDefault(getLiquidacion(salary.getType()), Collections.emptyMap())
						.remove(salary.getSocialSecurityNumber());

			}

			Map<String, com.esferalia.aon.occam.api.model.Person> personsMap = AON
					.getPersonStream(domainName, domainId, userLogin, p -> p.getDomainProperty().eq(domainId))
					.collect(Collectors.toMap(p -> p.getSocialSecurityNum(), p -> p));

			cccSldCosts.forEach((ccc, l_map) -> l_map.forEach((l, naf_map) -> naf_map.forEach((naf, w) -> sldSalaries
					.add(newSalary(l, ccc, w, Optional.ofNullable(personsMap.get(w.getNss())))))));

			return new ICollectionProvider() {

				@Override
				public Collection getCollection(boolean forceRefresh) throws ManagerBeanException {
					return sldSalaries;
				}

				@Override
				public Collection getCollection() {
					return sldSalaries;
				}
			};

		} catch (SQLException e) {
			throw new ManagerBeanException(e);
		} catch (IllegalArgumentException e) {
			throw new ManagerBeanException(e.getCause());
		}

		// throw new IllegalArgumentException( new DataDoesNotExist());

	}

	protected static ICollectionProvider getSalariesProvider(String domain, Cost cost, SalaryType types[],
			SalaryFilter filter, boolean calc) throws ManagerBeanException {

		Condition condition = null;
		Criteria sqlCriteria = new Criteria();

		Calendar calendar = getDate(cost);
		Date startDate = calendar.getTime();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date endDate = calendar.getTime();

		if (cost.getWorkplaceId() != 0) {

			sqlCriteria.addEqualExpression(WORKPLACE + "." + WorkplaceColumns.ID, cost.getWorkplaceId());
			condition = com.esferalia.aon.jooq.tables.Workplace.WORKPLACE.ID.eq(cost.getWorkplaceId());
		} else {
			sqlCriteria.addEqualExpression(ENTERPRISE + "." + EnterpriseColumns.REGISTRY, cost.getEnterpriseId());
			condition = com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE.REGISTRY.eq(cost.getEnterpriseId());
		}

		sqlCriteria.addLessThanOrEqualExpression(CONTRACT + "." + ContractColumns.START_DATE, endDate);
		sqlCriteria.addExpression(ExpressionUtilities.getOrExpression(
				ExpressionUtilities.getNullExpression(CONTRACT + "." + ContractColumns.END_DATE), ExpressionUtilities
						.getGreaterThanOrEqualExpression(CONTRACT + "." + ContractColumns.END_DATE, startDate)));

		sqlCriteria.addOrder(SQLConstants.WORKPLACE + "." + WorkplaceColumns.ID);
		sqlCriteria.addOrder(SQLContractSalaryCalculatorContext.PERSON_REGISTRY + "." + RegistryColumns.NAME);

		java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
		java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());

		condition = condition
				.and(com.esferalia.aon.jooq.tables.Salary.SALARY.CHARGE_DATE.between(sqlStartDate, sqlEndDate));

		SortField<?> sortFields[] = { com.esferalia.aon.jooq.tables.Workplace.WORKPLACE.ID.asc(),
				com.esferalia.aon.jooq.tables.Salary.SALARY.EMPLOYEE_NAME.asc(),
				com.esferalia.aon.jooq.tables.Salary.SALARY.CHARGE_DATE.asc() };

		Byte btypes[] = new Byte[types.length];
		for (int i = 0; i < types.length; i++)
			btypes[i] = (byte) types[i].ordinal();
		condition = condition.and(com.esferalia.aon.jooq.tables.Salary.SALARY.TYPE.in(btypes));

		return calc
				? new PayrollServletUtils.CalcSalaryProvider(domain, startDate, endDate, sqlCriteria, types,
						new PayrollServletUtils.SalaryProvider(domain, condition, filter, sortFields))
				: new PayrollServletUtils.SalaryProvider(domain, condition, filter, sortFields);

	}

	private static Calendar getDate(Cost cost) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, cost.getYear());
		calendar.set(Calendar.MONTH, cost.getMonth());
		calendar.set(Calendar.DAY_OF_MONTH, 1); // The first day of the month
												// has value 1.
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar;
	}

	private static List<Salary> getSalaries(Connection connection, Integer contractId) throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {

			String sql = "SELECT * " + " FROM " + SALARY + " WHERE " + SALARY + "." + SalaryColumns.CONTRACT + " = ?"
					+ " AND " + SALARY + "." + SalaryColumns.TYPE + " < " + SalaryType.L00.ordinal() + " ORDER BY "
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

			salary.setType(getSalaryType((Integer) rs.getObject(SalaryColumns.TYPE)));

			salaries.add(salary);
		}

		return salaries;
	}

	private static List<Salary> getSiteSalaries(Connection connection, Integer contractId) throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {

			String sql = "SELECT * " + " FROM " + SALARY + " LEFT JOIN " + SQLConstants.SALARY_DATA + " ON ( "
					+ SQLConstants.SALARY + "." + SalaryColumns.ID + " = " + SQLConstants.SALARY_DATA + "."
					+ SalaryDataColumns.SALARY + " AND " + SQLConstants.SALARY_DATA + "." + SalaryDataColumns.NAME
					+ " =  ? " + ")" + " WHERE " + SALARY + "." + SalaryColumns.CONTRACT + " = ?" + " AND " + SALARY
					+ "." + SalaryColumns.TYPE + " < " + SalaryType.L00.ordinal() + " )" + " AND ( "
					+ SQLConstants.SALARY_DATA + "." + SalaryDataColumns.ID + " IS NULL " + " OR "
					+ SQLConstants.SALARY_DATA + "." + SalaryDataColumns.EXPRESSION + " <= UTC_DATE() ) " + " ORDER BY "
					+ SALARY + "." + SalaryColumns.END_DATE + " ASC";
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

	private static List<Irpf> getIrpfOutcomes(Connection connection, Integer contractId) throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {

			String sql = "SELECT *" + " FROM " + IRPF_DATA + ", " + IRPF_RESULT + " LEFT JOIN " + IRPF_REGULARIZATION
					+ " USING ( " + IrpfResultColumns.CONTRACT + ", " + IrpfResultColumns.EFFECTIVE_DATE + " ) "
					+ " WHERE " + IRPF_DATA + "." + IrpfDataColumns.CONTRACT + " =  ? " + " AND " + IRPF_DATA + "."
					+ IrpfDataColumns.CONTRACT + " = " + IRPF_RESULT + "." + IrpfResultColumns.CONTRACT + " AND ( "
					+ IRPF_DATA + "." + IrpfDataColumns.END_DATE + " IS NULL" + " OR " + IRPF_DATA + "."
					+ IrpfDataColumns.END_DATE + " >= " + IRPF_RESULT + "." + IrpfResultColumns.EFFECTIVE_DATE + " )";

			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, contractId);
			rs = stmt.executeQuery();

			List<Irpf> outcomes = new LinkedList<Irpf>();
			while (rs.next()) {
				Irpf outcome = new Irpf();

				IrpfData irpfData = new IrpfData();
				irpfData.setId(rs.getInt(IRPF_DATA + "." + IrpfDataColumns.ID));

				IrpfResult irpfResult = new IrpfResult();
				irpfResult.setId(rs.getInt(IRPF_RESULT + "." + IrpfResultColumns.ID));

				outcome.setIrpfData(irpfData);
				outcome.setIrpfResult(irpfResult);

				// Be care that irpf regularization may not exist .
				Object irpfRegularizationId = rs.getObject(IRPF_REGULARIZATION + "." + IrpfRegularizationColumns.ID);
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

	private static List<Cost> getEnterpriseCosts(Connection connection, Integer enterpriseId) throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			String yearCol = "YEAR";
			String monthCol = "MONTH";

			// We asume that one enterprise one domain. This way SELECT it's
			// more clear.
			String sql = "SELECT" + " MONTH(" + SALARY + "." + SalaryColumns.ISSUE_DATE + ") " + monthCol + ", YEAR("
					+ SALARY + "." + SalaryColumns.ISSUE_DATE + ") " + yearCol + " FROM " + ENTERPRISE + ", " + SALARY
					+ " WHERE " + ENTERPRISE + "." + EnterpriseColumns.DOMAIN + " = " + SALARY + "."
					+ SalaryColumns.DOMAIN + " AND " + ENTERPRISE + "." + EnterpriseColumns.REGISTRY + " = ?" + " AND "
					+ SALARY + "." + SalaryColumns.TYPE + " < " + SalaryType.L00.ordinal() + " GROUP BY 1, 2"
					+ " ORDER BY 2 , 1 ASC ";

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

	private static List<Cost> getSiteEnterpriseCosts(Connection connection, Integer enterpriseId) throws SQLException {

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
			String sql = "SELECT" + " MONTH(" + SALARY + "." + SalaryColumns.CHARGE_DATE + ") " + monthCol + ", YEAR("
					+ SALARY + "." + SalaryColumns.CHARGE_DATE + ") " + yearCol + ", COUNT(*) AS " + allCol
					+ ",(COUNT( IF(" + SQLConstants.SALARY_DATA + "." + SalaryDataColumns.EXPRESSION
					+ " <= UTC_DATE(),1,NULL))" + " +  COUNT( IF(" + SQLConstants.SALARY_DATA + "."
					+ SalaryDataColumns.ID + " IS NULL,1,NULL))) AS " + showCol + ",(SELECT COUNT(*)" + " FROM "
					+ CONTRACT + " WHERE " + ContractColumns.DOMAIN + " = " + ENTERPRISE + "."
					+ EnterpriseColumns.DOMAIN + " AND " + CONTRACT + "." + ContractColumns.START_DATE
					+ " <= LAST_DAY(CHARGE_DATE) " + " AND ( " + CONTRACT + "." + ContractColumns.END_DATE + " IS NULL"
					+ " OR " + CONTRACT + "." + ContractColumns.END_DATE
					+ " >=  DATE_FORMAT(CHARGE_DATE, '%Y-%m-01') )) AS " + contractsCol

					+ " FROM " + ENTERPRISE + ", " + SALARY + " LEFT JOIN " + SQLConstants.SALARY_DATA + " ON ( "
					+ SQLConstants.SALARY + "." + SalaryColumns.ID + " = " + SQLConstants.SALARY_DATA + "."
					+ SalaryDataColumns.SALARY + " AND " + SQLConstants.SALARY_DATA + "." + SalaryDataColumns.NAME
					+ " =  ? " + ")" + " WHERE " + ENTERPRISE + "." + EnterpriseColumns.DOMAIN + " = " + SALARY + "."
					+ SalaryColumns.DOMAIN + " AND " + ENTERPRISE + "." + EnterpriseColumns.REGISTRY + " = ? "
					+ " GROUP BY 1, 2" + " HAVING " + showCol + " >= 1" + " ORDER BY 2 , 1 ASC ";
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

	private static List<Cost> getEnterpriseCosts(ResultSet rs, Integer enterpriseId, String yearCol, String monthCol)
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

		if (!costs.isEmpty())
			Collections.reverse(costs);

		return costs;
	}

	private static List<Cost> getWorkplaceCosts(Connection connection, Integer workplaceId) throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			String yearCol = "YEAR";
			String monthCol = "MONTH";

			String sql = "SELECT" + " MONTH(" + SALARY + "." + SalaryColumns.CHARGE_DATE + ") " + monthCol + ", YEAR("
					+ SALARY + "." + SalaryColumns.CHARGE_DATE + ") " + yearCol + " FROM " + WORKPLACE + ", " + CONTRACT
					+ ", " + SALARY + " WHERE" + " " + WORKPLACE + "." + WorkplaceColumns.ID + " = " + CONTRACT + "."
					+ ContractColumns.WORKPLACE + " AND " + CONTRACT + "." + ContractColumns.ID + " = " + SALARY + "."
					+ SalaryColumns.CONTRACT + " AND " + WORKPLACE + "." + WorkplaceColumns.ID + " = ?" + " AND "
					+ SALARY + "." + SalaryColumns.TYPE + " < " + SalaryType.L00.ordinal() + " GROUP BY 1, 2"
					+ " ORDER BY 2 , 1 ASC ";

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

	private static List<Cost> getSLDWorkplaceCosts(Connection connection, Integer workplaceId) throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			String yearCol = "YEAR";
			String monthCol = "MONTH";

			// We asume that one enterprise one domain. This way SELECT it's
			// more clear.
			
			String sql = "SELECT" + " MONTH(" + SALARY + "." + SalaryColumns.ISSUE_DATE + ") " + monthCol + ", YEAR("
					+ SALARY + "." + SalaryColumns.ISSUE_DATE + ") " + yearCol + " FROM " + WORKPLACE + ", " + SALARY
					+ " WHERE " + WORKPLACE + "." + WorkplaceColumns.DOMAIN + " = " + SALARY + "."
					+ SalaryColumns.DOMAIN + " AND " + WORKPLACE + "." + WorkplaceColumns.ID + " = ?" + " AND " + SALARY
					+ "." + SalaryColumns.TYPE + " < " + SalaryType.L00.ordinal() + " GROUP BY 1, 2"
					+ " ORDER BY 2 , 1 ASC ";

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

//		ResultSet rs = null;
//		PreparedStatement stmt = null;
//
//		try {
//			String startCol = "START";
//			String endCol = "END";
//
//			String sql = "SELECT" + " MIN(" + CONTRACT + "." + ContractColumns.START_DATE + ") " + startCol
//					+ ", MAX(IFNULL(" + CONTRACT + "." + ContractColumns.END_DATE + ",CURDATE())) " + endCol + " FROM "
//					+ WORKPLACE + ", " + CONTRACT + " WHERE" + " " + WORKPLACE + "." + WorkplaceColumns.ID + " = "
//					+ CONTRACT + "." + ContractColumns.WORKPLACE + " AND " + WORKPLACE + "." + WorkplaceColumns.ID
//					+ " = ?";
//
//			stmt = connection.prepareStatement(sql);
//			stmt.setInt(1, workplaceId);
//			rs = stmt.executeQuery();
//
//			if (rs.next() && rs.getDate(startCol) != null) {
//				return getWorkplaceCosts(workplaceId, rs.getDate(startCol), rs.getDate(endCol));
//			}
//
//			return Collections.emptyList();
//		} finally {
//			if (rs != null) {
//				rs.close();
//			}
//			if (stmt != null) {
//				stmt.close();
//			}
//		}
	}

	private static List<Cost> getSiteWorkplaceCosts(Connection connection, Integer workplaceId) throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			String yearCol = "YEAR";
			String monthCol = "MONTH";
			// @formatter:off
			String sql = "SELECT" + " MONTH(" + SALARY + "." + SalaryColumns.CHARGE_DATE + ") " + monthCol + ", YEAR("
					+ SALARY + "." + SalaryColumns.CHARGE_DATE + ") " + yearCol

					+ ", MIN(" + SALARY + "." + SalaryColumns.CHARGE_DATE + ") AS CHARGE_DATE"
					+ ", COUNT(*) AS SALARIES " + ",(COUNT( IF(" + SQLConstants.SALARY_DATA + "."
					+ SalaryDataColumns.EXPRESSION + " <= UTC_DATE(),1,NULL)) +  COUNT( IF(" + SQLConstants.SALARY_DATA
					+ "." + SalaryDataColumns.ID + " IS NULL,1,NULL))) AS VISIBLES" + ",(SELECT COUNT(*) FROM "
					+ CONTRACT + " WHERE " + ContractColumns.WORKPLACE + " = " + WORKPLACE + "." + WorkplaceColumns.ID
					+ " AND " + CONTRACT + "." + ContractColumns.START_DATE + " <= LAST_DAY(CHARGE_DATE) " + " AND ( "
					+ CONTRACT + "." + ContractColumns.END_DATE + " IS NULL OR " + CONTRACT + "."
					+ ContractColumns.END_DATE + " >=  DATE_FORMAT(CHARGE_DATE, '%Y-%m-01') )) AS CONTRACTS"

					+ " FROM " + WORKPLACE + ", " + CONTRACT + ", " + SALARY

					+ " LEFT JOIN " + SQLConstants.SALARY_DATA + " ON ( " + SQLConstants.SALARY + "." + SalaryColumns.ID
					+ " = " + SQLConstants.SALARY_DATA + "." + SalaryDataColumns.SALARY + " AND "
					+ SQLConstants.SALARY_DATA + "." + SalaryDataColumns.NAME + " =  ? " + ")"

					+ " WHERE" + " " + WORKPLACE + "." + WorkplaceColumns.ID + " = " + CONTRACT + "."
					+ ContractColumns.WORKPLACE + " AND " + CONTRACT + "." + ContractColumns.ID + " = " + SALARY + "."
					+ SalaryColumns.CONTRACT + " AND " + SALARY + "." + SalaryColumns.TYPE + " < "
					+ SalaryType.L00.ordinal() + " AND " + WORKPLACE + "." + WorkplaceColumns.ID + " = ?"
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

	private static List<Cost> getWorkplaceCosts(ResultSet rs, Integer workplaceId, String yearCol, String monthCol)
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

		if (!costs.isEmpty())
			Collections.reverse(costs);

		return costs;
	}

	private static List<Cost> getWorkplaceCosts(Integer workplaceId, Date startDate, Date endDate) throws SQLException {
		List<Cost> costs = new LinkedList<Cost>();

		for (Date date = getMonthFirstDay(startDate); date
				.before(getMonthFirstDay(endDate)); date = addMonths(date, 1)) {

			Cost cost = new Cost();
			cost.setYear(AonDateUtils.getYear(date));
			cost.setMonth(AonDateUtils.getMonth(date));
			cost.setWorkplaceId(workplaceId);

			costs.add(cost);
		}

		return costs;

	}

	private static List<Activity> getEnterpriseActivities(Connection connection, Integer enterpriseId)
			throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			// @formatter:off
			String sql = "SELECT * " + " FROM " + ENTERPRISE_ACTIVITY + " WHERE " + EnterpriseActivityColumns.ENTERPRISE
					+ " = ? ";
			// @formatter:on

			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, enterpriseId);
			rs = stmt.executeQuery();

			List<Activity> activities = new LinkedList<Activity>();
			while (rs.next()) {

				Activity activity = new Activity();
				activity.setId(rs.getInt(EnterpriseActivityColumns.ID));
				activity.setDescription(rs.getString(EnterpriseActivityColumns.DESCRIPTION));
				activity.setCnae2009(rs.getInt(EnterpriseActivityColumns.CNAE2009));

				getEnterpriseActivityCCCs(connection, activity.getId()).forEach(ccc -> activity.addCcc(ccc));

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

	private static List<CCC> getEnterpriseActivityCCCs(Connection connection, Integer enterpriseActivityId)
			throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;
		List<CCC> cccs = new LinkedList<CCC>();

		try {
			// @formatter:off
			String sql = "SELECT * " + " FROM " + ENTERPRISE_CCC + " INNER JOIN " + SQLConstants.GEOZONE + " ON ( "
					+ SQLConstants.ENTERPRISE_CCC + "." + EnterpriseCccColumns.GEOZONE + " = " + SQLConstants.GEOZONE
					+ "." + GeozoneColumns.ID + ")" + " WHERE " + EnterpriseCccColumns.ENTERPRISE_ACTIVITY + " = ? ";
			// @formatter:on

			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, enterpriseActivityId);
			rs = stmt.executeQuery();

			while (rs.next()) {

				CCC ccc = new CCC();
				ccc.setId(rs.getInt(EnterpriseCccColumns.ID));
				ccc.setGeozone(rs.getString(SQLConstants.GEOZONE + "." + GeozoneColumns.CODE));
				ccc.setCode(rs.getString(SQLConstants.ENTERPRISE_CCC + "." + EnterpriseCccColumns.CCC));
				ccc.setRegime(JooqEnterprise
						.getSSRegime(rs.getInt(SQLConstants.ENTERPRISE_CCC + "." + EnterpriseCccColumns.TYPE))
						.getCode());
				cccs.add(ccc);
			}

		} catch (Throwable t) {

		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				rs.close();
			}
		}
		return cccs;
	}

	private static List<BankAccount> getEnterpriseBankAccounts(Connection connection, Integer enterpriseId)
			throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			// @formatter:off
			String sql = "SELECT * " + " FROM " + RBANK + " WHERE " + RbankColumns.REGISTRY + " = ? ";
			// @formatter:on

			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, enterpriseId);
			rs = stmt.executeQuery();

			List<BankAccount> bankAccounts = new LinkedList<BankAccount>();
			while (rs.next()) {

				BankAccount bankAccount = new BankAccount();
				bankAccount.setId(rs.getInt(RbankColumns.ID));
				bankAccount.setBic(rs.getString(RbankColumns.BIC));
				bankAccount.setAlias(rs.getString(RbankColumns.ALIAS));
				bankAccount.setAccount(rs.getString(RbankColumns.BANK_ACCOUNT));

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

	private static List<Employee> getCCCEmployees(Connection connection, String ccc) throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			// @formatter:off
			String sql = "SELECT " + " " + SQLConstants.PERSON + "." + PersonColumns.SOCIAL_SECURITY_NUM + ", "
					+ SQLConstants.REGISTRY + "." + RegistryColumns.NAME + " FROM " + SQLConstants.CONTRACT
					+ " INNER JOIN " + SQLConstants.PERSON + " ON (" + SQLConstants.CONTRACT + "."
					+ ContractColumns.PERSON + " = " + SQLConstants.PERSON + "." + PersonColumns.REGISTRY + ")"
					+ " INNER JOIN " + SQLConstants.REGISTRY + " ON (" + SQLConstants.PERSON + "."
					+ PersonColumns.REGISTRY + " = " + SQLConstants.REGISTRY + "." + RegistryColumns.ID + ")"
					+ " INNER JOIN " + SQLConstants.ENTERPRISE_CCC + " ON (" + SQLConstants.CONTRACT + "."
					+ ContractColumns.ENTERPRISE_CCC + " = " + SQLConstants.ENTERPRISE_CCC + "."
					+ EnterpriseCccColumns.ID + ")" + " WHERE " + EnterpriseCccColumns.CCC + " = ? " + " GROUP BY 1,2";
			// @formatter:on

			stmt = connection.prepareStatement(sql);
			stmt.setString(1, ccc);
			rs = stmt.executeQuery();

			List<Employee> employees = new LinkedList<>();
			while (rs.next()) {

				Employee employee = new Employee();
				employee.setSocialSecurity(rs.getString(1));
				employee.setName(rs.getString(2));
				employees.add(employee);
			}

			return employees;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	private static Enterprise getEnterprise(Integer registryID, Integer userID, Connection connection)
			throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			String sql = "SELECT"

					+ " " + REGISTRY + ".*" + "," + ENTERPRISE + ".*" + "," + WORKPLACE + ".*" + "," + PAYROLL_WORKPLACE
					+ ".*" + "," + AGREEMENT + ".*" + "," + ENTERPRISE_ACTIVITY + ".*" + "," + ENTERPRISE_CCC + ".*"

					+ "," + CONTRACT + "." + ContractColumns.ID + ",MAX(IFNULL(" + CONTRACT + "."
					+ ContractColumns.END_DATE + ",'9999-12-31')) AS " + CONTRACT_MAX_END_DATE

					+ " FROM " + REGISTRY + ", " + ENTERPRISE

					+ " LEFT JOIN " + WORKPLACE + " ON ( " + ENTERPRISE + "." + EnterpriseColumns.REGISTRY + " = "
					+ WORKPLACE + "." + WorkplaceColumns.ENTERPRISE + " )"

					+ " LEFT JOIN " + PAYROLL_WORKPLACE + " ON ( " + WORKPLACE + "." + WorkplaceColumns.ID + " = "
					+ PAYROLL_WORKPLACE + "." + PayrollWorkplaceColumns.WORKPLACE + ")"

					+ " LEFT JOIN " + AGREEMENT + " ON ( " + PAYROLL_WORKPLACE + "." + PayrollWorkplaceColumns.AGREEMENT
					+ " = " + AGREEMENT + "." + AgreementColumns.ID + " )"

					+ " LEFT JOIN " + ENTERPRISE_ACTIVITY + " ON ( " + PAYROLL_WORKPLACE + "."
					+ PayrollWorkplaceColumns.ENTERPRISE_ACTIVITY + " = " + ENTERPRISE_ACTIVITY + "."
					+ EnterpriseActivityColumns.ID + " )"

					+ " LEFT JOIN " + ENTERPRISE_CCC + " ON ( " + ENTERPRISE_CCC + "."
					+ EnterpriseCccColumns.ENTERPRISE_ACTIVITY + " = " + ENTERPRISE_ACTIVITY + "."
					+ EnterpriseActivityColumns.ID + " )"

					+ " LEFT JOIN " + CONTRACT + " ON ( " + " " + CONTRACT + "." + ContractColumns.ID + " > 0 "
					+ " AND " + WORKPLACE + "." + WorkplaceColumns.ID + " = " + CONTRACT + "."
					+ ContractColumns.WORKPLACE + ")"

					+ " WHERE " + REGISTRY + "." + RegistryColumns.ID + " = ?" + " AND " + REGISTRY + "."
					+ RegistryColumns.ID + " = " + ENTERPRISE + "." + EnterpriseColumns.REGISTRY + " AND " + WORKPLACE
					+ "." + WorkplaceColumns.SCOPE + " IN ( SELECT " + UserScopeColumns.SCOPE + " FROM " + USER_SCOPE
					+ " WHERE " + UserScopeColumns.USER_ID + " = ? " + " UNION SELECT scope.id FROM scope INNER JOIN "
					+ DOMAIN + " ON ( scope.domain = " + DOMAIN + "." + DomainColumns.ID + " ) " + "INNER JOIN " + USER
					+ " ON ( " + DOMAIN + "." + DomainColumns.PARENT + " = " + USER + "." + UserColumns.DOMAIN + " ) )"

					+ " GROUP BY" + " " + ENTERPRISE + "." + EnterpriseColumns.REGISTRY + "," + WORKPLACE + "."
					+ WorkplaceColumns.ID + "," + PAYROLL_WORKPLACE + "." + PayrollWorkplaceColumns.ID + "," + AGREEMENT
					+ "." + AgreementColumns.ID + "," + ENTERPRISE_ACTIVITY + "." + EnterpriseActivityColumns.ID + ","
					+ ENTERPRISE_CCC + "." + EnterpriseCccColumns.ID

					+ " ORDER BY " + " UPPER(" + WORKPLACE + "." + WorkplaceColumns.DESCRIPTION + " )";

			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, registryID);

			stmt.setInt(2, userID);

			rs = stmt.executeQuery();

			EnterpriseHandler enterpriseHandler = new EnterpriseHandler();

			WorkplaceHandler workplaceHandler = new WorkplaceHandler(enterpriseHandler);

			CCCHandler cccHandler = new CCCHandler(workplaceHandler);

			groups(rs, enterpriseHandler, workplaceHandler, cccHandler);

			Enterprise enterprise = enterpriseHandler.getEnterprise();

			if (enterprise == null)
				return null;

			List<Activity> activities = getEnterpriseActivities(connection, enterprise.getId());
			enterprise.setActivities(activities);

			List<BankAccount> bankAccounts = getEnterpriseBankAccounts(connection, enterprise.getId());
			enterprise.setBankAccounts(bankAccounts);

			for (Activity activity : enterprise.getActivities()) {
				for (CCC ccc : activity.getCccs()) {
					try {
						ccc.setEmployees(getCCCEmployees(connection, ccc.getCode()));
					} catch (Exception e) {
						// show must go on :-)
					}
				}
			}

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

	private static com.esferalia.aon.payroll.Salary getDBSalary(String domain, SalaryDraft salaryDraft)
			throws ManagerBeanException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			return getDBSalary(conn, salaryDraft);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	private static com.esferalia.aon.payroll.Salary getSSSalary(String domain, SalaryDraft salaryDraft)
			throws ManagerBeanException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			return getSSSalary(conn, salaryDraft);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	private static com.esferalia.aon.payroll.Salary getDBSalary(Connection connection, SalaryDraft salaryDraft)
			throws ManagerBeanException {

		byte type = (byte) salaryDraft.getType().ordinal();
		Integer contract = salaryDraft.getEmployee().getId();
		java.sql.Date sqlStartDate = new java.sql.Date(salaryDraft.getStartDate().getTime());
		java.sql.Date sqlEndDate = new java.sql.Date(salaryDraft.getEndDate().getTime());
		java.sql.Date sqlIssueDate = new java.sql.Date(salaryDraft.getIssueDate().getTime());

		Condition condition = com.esferalia.aon.jooq.tables.Salary.SALARY.CONTRACT.eq(contract)
				.and(com.esferalia.aon.jooq.tables.Salary.SALARY.TYPE.eq(type))
				.and(com.esferalia.aon.jooq.tables.Salary.SALARY.START_DATE.eq(sqlStartDate))
				.and(com.esferalia.aon.jooq.tables.Salary.SALARY.END_DATE.eq(sqlEndDate));

		// This approach is very conservative, now only checks issue date for extras.
		if (salaryDraft.getType() == Salary.Type.EXTRA)
			condition = condition.and(com.esferalia.aon.jooq.tables.Salary.SALARY.ISSUE_DATE.eq(sqlIssueDate));

		for (com.esferalia.aon.payroll.Salary salary : PayrollServletUtils.getSalary(connection, condition))
			return salary;

		return null;
	}

	private static com.esferalia.aon.payroll.Salary getSSSalary(Connection connection, SalaryDraft salaryDraft)
			throws ManagerBeanException {

		Type salaryDraftType = salaryDraft.getType();
		if (salaryDraftType == Type.EXTRA)
			return null;

		byte type;
		switch (salaryDraftType) {
		case L13:
			type = (byte) Type.L13.ordinal();
		default:
			type = (byte) Type.L00.ordinal();
		}

		Integer contract = salaryDraft.getEmployee().getId();
		java.sql.Date sqlStartDate = new java.sql.Date(salaryDraft.getStartDate().getTime());
		java.sql.Date sqlEndDate = new java.sql.Date(salaryDraft.getEndDate().getTime());

		Condition condition = com.esferalia.aon.jooq.tables.Salary.SALARY.CONTRACT.eq(contract)
				.and(com.esferalia.aon.jooq.tables.Salary.SALARY.TYPE.eq(type))
				.and(com.esferalia.aon.jooq.tables.Salary.SALARY.START_DATE.eq(sqlStartDate))
				.and(com.esferalia.aon.jooq.tables.Salary.SALARY.END_DATE.eq(sqlEndDate));

		for (com.esferalia.aon.payroll.Salary salary : PayrollServletUtils.getSalary(connection, condition))
			return salary;

		return null;
	}

	private static List<Variable> getDBSalaryData(String domain, ISalary salary) throws ManagerBeanException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			return getDBSalaryData(conn, salary);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	private static Collection<FiscalModel> getFiscalModels(Connection conn, ISalary salary) throws ManagerBeanException {
		AONContext aonContext = new AONContext(conn);
		return AON.getFiscalModels(aonContext, new com.esferalia.aon.occam.api.model.Salary().setId(salary.getId()));
	}

	private static List<Variable> getDBSalaryData(Connection conn, ISalary salary) throws ManagerBeanException {

		AONContext aonContext = new AONContext(conn);
		DSLContext dslContext = aonContext.getDslContext();

		Variable vars[] = dslContext.select().from(com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA)
				.where(com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA.SALARY.eq(salary.getId()))
				.fetchInto(com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA).stream().map(data -> {
					Variable var = new StringVariable();
					var.setName(data.getName());
					var.setStartDate(data.getStartDate());
					var.setEndDate(data.getEndDate());
					var.setValue(data.getExpression());
					var.setDomain(data.getDomain());
					return var;
				}).toArray(Variable[]::new);

		return Arrays.asList(vars);

	}

	private static void calculate(String domain, SalaryDraft draft,
			GenericContractSalaryCalculator<ISalary, ISQLContractSalaryCalculatorContext> salaryCalculator) {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			calculate(conn, draft, salaryCalculator);
		} catch (SQLException e) {
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

	private static void calculate(Connection conn, SalaryDraft draft,
			GenericContractSalaryCalculator<ISalary, ISQLContractSalaryCalculatorContext> salaryCalculator) {

		SalaryDraftBuilder salaryDraftBuilder = new SalaryDraftBuilder(draft);
		RoundSalaryBuilder<ISalary> roundSalaryBuilder = new RoundSalaryBuilder<ISalary>(salaryDraftBuilder,
				EmployeesServiceHelper.round(2));
		try {
			EmployeesServiceHelper.calculate(conn, draft, roundSalaryBuilder, salaryDraftBuilder, salaryCalculator);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			salaryDraftBuilder.clearDb();
			com.esferalia.aon.payroll.Salary dbSalary = getDBSalary(conn, draft);
			if (dbSalary != null) {
				salaryDraftBuilder.setDbSalary(dbSalary);
				salaryDraftBuilder.setDbSalaryData(getDBSalaryData(conn, dbSalary));
				salaryDraftBuilder.setFiscalModels(getFiscalModels(conn, dbSalary));
			}
		} catch (SalaryException e) {
		} catch (ManagerBeanException e) {
		}

		try {
			salaryDraftBuilder.clearSs();
			com.esferalia.aon.payroll.Salary ssSalary = getSSSalary(conn, draft);
			if (ssSalary != null) {
				salaryDraftBuilder.setSsSalary(ssSalary);
			}
		} catch (SalaryException e) {
		} catch (ManagerBeanException e) {
		}
	}

	private static void calculate(String domain, SalaryDraft draft,
			GenericContractSalaryCalculator<ISalary, ISQLContractSalaryCalculatorContext> salaryCalculator,
			Date sections[]) {

		SalaryDraftBuilder salaryDraftBuilder = new SalaryDraftBuilder(draft);
		CollectSalaryBuilder<ISalary> collectSalaryBuilder = new CollectSalaryBuilder<ISalary>();
		Date startDate = draft.getStartDate();
		Date endDate = draft.getEndDate();
		try {
			for (Date section : sections) {
				draft.setEndDate(AonDateUtils.add(section, Calendar.DAY_OF_MONTH, -1));
				calculate(domain, draft, collectSalaryBuilder, salaryDraftBuilder, salaryCalculator);
				draft.setStartDate(section);
			}
			draft.setEndDate(endDate);

			draft.addDraftVariable(getActiveDaysVar(collectSalaryBuilder));
			calculate(domain, draft, collectSalaryBuilder, salaryDraftBuilder, salaryCalculator);

			collectSalaryBuilder.collect(salaryDraftBuilder);

		} catch (Throwable e) {
			e.printStackTrace();
		}
		draft.setStartDate(startDate);

		try {
			salaryDraftBuilder.clearDb();
			com.esferalia.aon.payroll.Salary dbSalary = getDBSalary(domain, draft);
			if (dbSalary != null) {
				salaryDraftBuilder.setDbSalary(dbSalary);
				salaryDraftBuilder.setDbSalaryData(getDBSalaryData(domain, dbSalary));
			}
		} catch (SalaryException e) {
		} catch (ManagerBeanException e) {
		}

		try {
			salaryDraftBuilder.clearSs();
			com.esferalia.aon.payroll.Salary ssSalary = getSSSalary(domain, draft);
			if (ssSalary != null) {
				salaryDraftBuilder.setSsSalary(ssSalary);
			}
		} catch (SalaryException e) {
		} catch (ManagerBeanException e) {
		}
	}

	private static Map<String, String> getEventsVariables(String domain, Integer workplaceId, Integer agreementId,
			Date startDate, Date endDate, Integer domainId, Integer parentDomainId) throws SQLException {
		Connection connection = null;
		try {

			AgreementDraft agreementDraft = new AgreementDraft();
			agreementDraft.setId(agreementId);
			agreementDraft.setDomain(domainId);
			agreementDraft.setStartDate(startDate);
			agreementDraft.setEndDate(endDate);

			connection = AonServletUtils.getConnection(domain);

			EmployeesServiceHelper.calculate(connection, agreementDraft, domainId, parentDomainId);

			Map<String, String> variables = new HashMap<String, String>();

			for (String variable : agreementDraft.getVariables()) {
				if (!variable.contains("DIAS_") && !AonStringUtils.equalsIgnoreCase(variable, "TRUE")
						&& !AonStringUtils.equalsIgnoreCase(variable, "FALSE"))

					variables.put(variable, variable);
			}

			for (Variable variable : agreementDraft.getSalaryTable().getAllVariables()) {
				variables.remove(variable.getName());
			}

			return variables;

		} finally {
			if (connection != null)
				connection.close();
		}
	}

	private static Map<String, String> getWorkplaceEventsVariables(String domain, Integer workplaceId,
			Integer agreementId, Date startDate, Date endDate, Integer domainId, Integer parentDomainId)
			throws SQLException {

		Connection connection = null;

		try {
			connection = AonServletUtils.getConnection(domain);

			Map<String, String> variables = new HashMap<String, String>();

			SalaryTable salaryTable = SQLAgreementDraft.getSalaryTable(connection, agreementId, startDate, endDate,
					domainId, parentDomainId);

			for (Variable variable : salaryTable.getAllVariables()) {
				System.out.println(variable.getName() + ", Scope : " + variable.getScope() + ", Start : "
						+ variable.getStartDate() + ", End : " + variable.getEndDate());

				variables.put(variable.getName(),
						null == variable.getValue() ? variable.getExpression() : variable.getValue().toString());
			}

//			Set<Payment> payments = SQLEvents.getPayments(connection,
//					workplaceId, startDate, endDate);
//
//			if (agreementId != null) {
//				// payments.addAll(SQLAgreementDraft.getPayments(connection,
//				// agreementId, startDate, endDate));
//				payments.addAll(SQLAgreementDraft.getPaymentsAux(connection,
//						agreementId, startDate, endDate));
//			}
//
//			for (Payment payment : payments) {
//
//				if (AonStringUtils.equals(REMOVE, payment.getExpression()))
//					continue;
//				try {
//					Set<String> paymentVars = ExpressionContext
//							.getVariableSet(payment.getExpression());
//	
//					for (String var : paymentVars) {
//						if (var.endsWith("_ACTUAL"))
//							continue; // This is awfull ... very awful
//						variables.put(var, String.format("%s",
//								payment.getDescription(), payment.getExpression()));
//					}
//				}catch ( Exception e ) {
//					//TODO: Error
//				}
//
//				variables.remove(payment.getName());
//			}
//			
//			// Filter ContextVariable
//			for (ContextVariable ctxVar : ContextVariable.values())
//				variables.remove(ctxVar.getName());
//
//			// Clean system variables.
//			Set<String> systemVars = getSystemVariables(connection, startDate,
//					endDate);
//			for (String var : systemVars) {
//				System.out.println(var);
//				variables.remove(var);
//			}
//			
//			SQLAgreementDraft.get
//
//			Set<Level> levels = SQLAgreementDraft.getLevels(connection,
//					agreementId, domainId, parentDomainId);
//
//			SalaryTable salaryTable = SQLAgreementDraft.getSalaryTable(
//					connection, agreementId, startDate, endDate,domainId, parentDomainId);
//
//			//�Que variables se filtran aqui?
//			Set<String> names = variables.keySet();
//			for (Level level : levels) {
//				Iterator<String> namesIt = names.iterator();
//				while (namesIt.hasNext()) {
//					String name = namesIt.next();
//					if (salaryTable.get(level.getId(), name) != null)
//						namesIt.remove();
//				}
//			}

			return variables;

		} finally {
			if (connection != null)
				connection.close();
		}
	}

	private ContextDescriptor getEmployeePayments(Connection connection, Integer employeeId, Integer agreementId,
			Date startDate, Date endDate, Integer domainID, Integer parentDomainID) throws SQLException {

		ArrayList<String> eraseAgreements = new ArrayList<>();

		try {
			Set<Payment> payments = SQLEvents.getEmployeePayments(connection, employeeId, startDate, endDate);

			if (agreementId != null) {

				eraseAgreements = SQLAgreementDraft.getEraseAgreement(connection, agreementId, startDate, endDate);

				payments.addAll(SQLAgreementDraft.getPaymentsAux(connection, agreementId, startDate, endDate));
			}

			ContextDescriptor result = new ContextDescriptor();

			for (Payment payment : payments) {

				if (AonStringUtils.equals(REMOVE, payment.getExpression()))
					continue;

				try {
					Set<String> paymentVars = ExpressionContext.getVariableSet(payment.getExpression());
					for (String var : paymentVars) {
						if (var.endsWith("_ACTUAL"))
							continue; // This is awfull ... very awful
						result.add(var);
					}
				} catch (Exception e) {
					// TODO: Error...
				}

				result.remove(payment.getName());

			}

			// Add Filter Allways Variables
			// eraseAgreements.add("INICIO_ANTIGUEDAD");
			eraseAgreements.add("DIAS_MES");
			eraseAgreements.add("INICIO_CONTRATO");
			eraseAgreements.add("SALARIO_BASE");
			eraseAgreements.add("INICIO_NOMINA");
			eraseAgreements.add("SALARIO_MENSUAL");
			eraseAgreements.add("TRIENIO");
			// TODO: Esto es una prueba
			eraseAgreements.add("KMS");

			// Filter Agreement Variables
			for (String varName : eraseAgreements)
				result.remove(varName);

			Set<Level> levels = null;
			SalaryTable salaryTable = null;

			if (null != agreementId)
				levels = SQLAgreementDraft.getLevels(connection, agreementId, domainID, parentDomainID);

			if (null != agreementId)
				salaryTable = SQLAgreementDraft.getSalaryTable(connection, agreementId, startDate, endDate, domainID,
						parentDomainID);

			if (null != levels && null != salaryTable) {
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

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		} finally {
			if (connection != null)
				connection.close();
		}
	}

	@SuppressWarnings("unchecked")
	private static void calculateAndSave(Connection conn, SalaryDraft draft) throws SQLException {
		if (draft.hasDbSalary())
			deleteSalaries(conn, draft.getDbId());
		if (draft.getType() == Type.SETTLE)
			deleteAllSettles(conn, draft.getEmployee().getId());

		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(conn);
		RoundSalaryBuilder<ISalary> jooqRoundSalaryBuilder = new RoundSalaryBuilder<ISalary>(jooqSalaryBuilder,
				EmployeesServiceHelper.round(2));
		jooqSalaryBuilder.setListener(new SalaryBuilderListener());
		SalaryDraftBuilder salaryDraftBuilder = new SalaryDraftBuilder(draft);
		RoundSalaryBuilder<ISalary> draftRoundSalaryBuilder = new RoundSalaryBuilder<ISalary>(salaryDraftBuilder,
				EmployeesServiceHelper.round(2));
		CompositeSalaryBuilder<ISalary, ISalaryBuilder<ISalary>> compositeSalaryBuilder = new CompositeSalaryBuilder<ISalary, ISalaryBuilder<ISalary>>(
				draftRoundSalaryBuilder, jooqRoundSalaryBuilder);
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
			com.esferalia.aon.payroll.Salary dbSalary = getDBSalary(conn, draft);
			if (dbSalary != null) {
				salaryDraftBuilder.setDbSalary(dbSalary);
				salaryDraftBuilder.setDbSalaryData(getDBSalaryData(conn, dbSalary));
				salaryDraftBuilder.setFiscalModels(getFiscalModels(conn, dbSalary));
			}
		} catch (SalaryException e) {
		} catch (ManagerBeanException e) {
		}

		try {
			salaryDraftBuilder.clearSs();
			com.esferalia.aon.payroll.Salary ssSalary = getSSSalary(conn, draft);
			if (ssSalary != null) {
				salaryDraftBuilder.setSsSalary(ssSalary);
			}
		} catch (SalaryException e) {
		} catch (ManagerBeanException e) {
		}
	}

	@SuppressWarnings("unchecked")
	private static void calculateAndSave(Connection conn, SalaryDraft draft, Date sections[]) throws SQLException {
		if (draft.hasDbSalary())
			deleteSalaries(conn, draft.getDbId());
		if (draft.getType() == Type.SETTLE)
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
			for (Date section : sections) {
				draft.setEndDate(AonDateUtils.add(section, Calendar.DAY_OF_MONTH, -1));
				EmployeesServiceHelper.calculate(conn, draft, collectSalaryBuilder, salaryDraftBuilder,
						salaryCalculator);
				draft.setStartDate(section);
			}
			draft.setEndDate(endDate);

			draft.addDraftVariable(getActiveDaysVar(collectSalaryBuilder));

			EmployeesServiceHelper.calculate(conn, draft, collectSalaryBuilder, salaryDraftBuilder, salaryCalculator);

			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(conn);
			RoundSalaryBuilder<ISalary> roundSalaryBuilder = new RoundSalaryBuilder<ISalary>(jooqSalaryBuilder,
					EmployeesServiceHelper.round(2));
			jooqSalaryBuilder.setListener(new SalaryBuilderListener());
			CompositeSalaryBuilder<ISalary, ISalaryBuilder<ISalary>> compositeSalaryBuilder = new CompositeSalaryBuilder<ISalary, ISalaryBuilder<ISalary>>(
					salaryDraftBuilder, roundSalaryBuilder);
			collectSalaryBuilder.collect(compositeSalaryBuilder);

			jooqSalaryBuilder.execute();
			conn.commit();
		} finally {
			conn.setAutoCommit(autocommit);
		}
		draft.setStartDate(startDate);

		try {
			salaryDraftBuilder.clearDb();
			com.esferalia.aon.payroll.Salary dbSalary = getDBSalary(conn, draft);
			if (dbSalary != null) {
				salaryDraftBuilder.setDbSalary(dbSalary);
				salaryDraftBuilder.setDbSalaryData(getDBSalaryData(conn, dbSalary));
				salaryDraftBuilder.setFiscalModels(getFiscalModels(conn, dbSalary));
			} else {
				// draft.clearDb();
			}
		} catch (SalaryException e) {
		} catch (ManagerBeanException e) {
		}

		try {
			salaryDraftBuilder.clearSs();
			com.esferalia.aon.payroll.Salary ssSalary = getSSSalary(conn, draft);
			if (ssSalary != null) {
				salaryDraftBuilder.setSsSalary(ssSalary);
			}
		} catch (SalaryException e) {
		} catch (ManagerBeanException e) {
		}
	}

	private static NumberVariable getActiveDaysVar(CollectSalaryBuilder<?> builder) {
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

	private static <T extends ISalaryBuilder<ISalary>, L extends SalaryDraftBuilder> void calculate(String domain,
			SalaryDraft draft, T salaryBuilder, L draftBuilder,
			GenericContractSalaryCalculator<ISalary, ISQLContractSalaryCalculatorContext> calculator) {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			EmployeesServiceHelper.calculate(conn, draft, salaryBuilder, draftBuilder, calculator);
		} catch (SQLException e) {
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

	private static <T> List<ITimedResult<T>> eval(String domain, String expression, SalaryDraft draft, Class<T> toType)
			throws EvalException {
		Connection conn = null;

		try {
			conn = AonServletUtils.getConnection(domain);
			ISalaryCalculatorContext ctx = EmployeesServiceHelper.getSalaryCalculatorContext(conn, draft, null);
			return ctx.getExpressionContext().eval(expression, ctx.getStartDate(), ctx.getEndDate(), toType);

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

	private static <T> List<ITimedResult<T>> eval(String domain, String expression, AgreementDraft draft, int levelId,
			Class<T> toType) throws EvalException {
		Connection conn = null;

		try {
			conn = getConnection(domain);
			ISalaryCalculatorContext ctx = getSalaryCalculatorContext(conn, draft, -1);
			return ctx.getExpressionContext().eval(expression, ctx.getStartDate(), ctx.getEndDate(), toType);
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
	
	private static <T> List<ITimedResult<T>> eval(String domain, String expression, Date startDate, int levelId,
			Class<T> toType) throws EvalException {
		Connection conn = null;

		try {
			conn = getConnection(domain);
			ISalaryCalculatorContext ctx = getSalaryCalculatorContext(conn, startDate, -1);
			return ctx.getExpressionContext().eval(expression, ctx.getStartDate(), ctx.getEndDate(), toType);
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

			IContractSalaryCalculatorContext calculatorCtx = EmployeesServiceHelper.getSalaryCalculatorContext(conn,
					draft, null);

			return getContext(conn, calculatorCtx, draft.getStartDate(), draft.getEndDate());

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

	protected static ContextDescriptor getContext(Connection conn, IContractSalaryCalculatorContext calculatorCtx,
			Date startDate, Date endDate) {
		try {

			ExpressionContext expressionContext = notNull(calculatorCtx.getExpressionContext(),
					calculatorCtx.getSystemExpressionContext());

			Date start = notNull(calculatorCtx.getStartDate(), startDate);

			Date end = notNull(calculatorCtx.getEndDate(), endDate);

			ContextDescriptor contextDescriptor = new ContextDescriptor();

			Map<String, String> descriptions = getSystemDescriptions(conn, start, end);

			Set<String> varNames = expressionContext.variablesSet();

			for (String varName : varNames) {
				Object value = null;
				List<ITimedVariable<Object>> vars = expressionContext.getVariables(varName, start, end);

				for (ITimedVariable<Object> var : vars) {
					try {
						value = var.getValue(var.getPeriod());
					} catch (Throwable e) {

					}

					if (value == null)
						continue;

					String description = null;

					ContextVariable ctxVar = ContextVariable.getVariableByName(varName);

					if (ctxVar != null) {
						try {
							description = ctxVar.getDescription(new Locale("es", "ES"));
						} catch (MissingResourceException e) {
						}
						if (description != null)
							description = String.format(description, start, end);
					}
					if (description == null)
						description = descriptions.get(varName);

					if (Function.class == value.getClass()) {
						Class<?> type = ctxVar != null ? ctxVar.getType().getJavaType() : Object.class;
						contextDescriptor.add(varName, description, type, ((Function) value).getParameters());
					} else if (MethodStub.class == value.getClass()) {
						Method method = ((MethodStub) value).getMethod();
						contextDescriptor.add(varName, description, method.getReturnType(), method.getParameterTypes());
					} else {
						Class<?> type = value.getClass();
						if (ContextDescriptor.isKnownType(type)) {
							VariableDescriptor variableDescriptor = new VariableDescriptor();
							variableDescriptor.setType(type);
							variableDescriptor.setValue(value.toString());
							variableDescriptor.setDescription(description);
							variableDescriptor.setStartDate(var.getPeriod().getStart());
							variableDescriptor.setEndDate(var.getPeriod().getEnd());

							if (var instanceof IExpressionVariable<?>) {
								try {
									IExpression expression = ((IExpressionVariable<?>) var).getExpression();
									variableDescriptor.setExpression(expression.getExpression());

									variableDescriptor.setScope((expression.getScope() != null)
											? Scope.values()[expression.getScope().ordinal()]
											: null);
								} catch (Exception e) {
									System.out.println("Var name failed : " + varName);
								}

							}
							contextDescriptor.add(varName, variableDescriptor);
						}
					}
				}

			}

//			for(String key : contextDescriptor.getVariables())
//				for(VariableDescriptor var : contextDescriptor.getList(key))
//					System.out.println("CONTEXT :"+key+", descripcion :"+var.getDescription()+", expresion :"+var.getExpression()
//					+", value :"+var.getValue()+", startDate :"+var.getStartDate()+", endDate :"+var.getEndDate());

			return contextDescriptor;

		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	protected static ContextDescriptor getDraftContext(String domain, AgreementDraft draft, int levelId) {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);

			IContractSalaryCalculatorContext calculatorCtx = getSalaryCalculatorContext(conn, draft, levelId);

			return getContext(conn, calculatorCtx, draft.getStartDate(), draft.getEndDate());

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
	
	protected static ContextDescriptor getAgreementDraftContext(String domain, Date startDate, Date endDate, int levelId) {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);

			IContractSalaryCalculatorContext calculatorCtx = getSalaryCalculatorContext(conn, startDate, levelId);

			return getContext(conn, calculatorCtx, startDate, endDate);

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

	private static ISalary getSalary(SalaryDraft draft) {
		return new ISalary() {

			@Override
			public boolean isFullTime() {
				throw new NoSuchMethodError();
			}

			@Override
			public SalaryType getType() {
				return SalaryType.values()[draft.getType().ordinal()];
			}

			@Override
			public Double getTotalPayment() {
				return draft.getTotalPayment();
			}

			@Override
			public Double getTotalLiquid() {
				return draft.getTotalLiquid();
			}

			@Override
			public Double getTotalIrpf() {
				throw new NoSuchMethodError();
			}

			@Override
			public Double getTotalEnterprise() {
				return draft.getTotalEnterprise();
			}

			@Override
			public Double getTotalDeduction() {
				return draft.getTotalDeduction();
			}

			@Override
			public Integer getTimeUnits() {
				return draft.getTimeUnits();
			}

			@Override
			public Date getStartDate() {
				return draft.getStartDate();
			}

			@Override
			public String getSocialSecurityNumber() {
				return draft.getEmployeeSS();
			}

			@Override
			public Double getSocialSecurityContributions() {
				throw new NoSuchMethodError();
			}

			@Override
			public Date getSeniorityDate() {
				return draft.getEmployeeSeniorityDate();
			}

			@Override
			public Double getRemuneration() {
				return draft.getRemuneration();
			}

			@Override
			public Integer getRegistration() {
				throw new NoSuchMethodError();
			}

			@Override
			public Double getRawCommonBase() {
				return draft.getRawCgcBase();
			}

			@Override
			public String getQuoteGroup() {
				return draft.getEmployeeQuoteGroup();
			}

			@Override
			public Double getProfessionalBase() {
				return draft.getDbGgpBase();
			}

			@Override
			public Payments getPayments() throws SalaryException {
				throw new NoSuchMethodError();
			}

			@Override
			public Collection<IPayment> getPaymentS() throws SalaryException {
				ArrayList<IPayment> payments = new ArrayList<IPayment>();

				for (Payment payment : draft.getPayments()) {
					addPaymentToList(payments, payment);
				}
				return payments;
			}
			
			

			@Override
			public Map<String, List<IData>> getDataS() throws SalaryException {
				Map<String, List<IData>> map = new HashMap<>();
				if (draft != null && draft.getDraftContext() != null) {
					draft.getDraftContext().forEach(cData -> {
						String name = cData.getName();
						List<IData> dataList = map.getOrDefault(name, new ArrayList<>());
						addDataToList(dataList, cData);
						map.put(name, dataList);
					});
				}
				if (draft != null && draft.getContext() != null) {
					draft.getContext().forEach(cData -> {
						String name = cData.getName();
						List<IData> dataList = map.getOrDefault(name, new ArrayList<>());
						addDataToList(dataList, cData);
						map.put(name, dataList);
					});
				}
				return map;
			}
			
			public void addDataToList(List<IData> list, Variable variable) {
				try {
					IData data = new IData() {
						
						@Override
						public String getValue() {								
							return variable.getValue() != null ? variable.getValue().toString() : null;
						}
						
						@Override
						public Date getStartDate() {
							return variable.getStartDate();
						}
						
						@Override
						public String getName() {
							return variable.getName();
						}
						
						@Override
						public Date getEndDate() {
							return variable.getEndDate();
						}
					};
					list.add(data);
				} catch (Exception ignored) {
				}
			}

			public void addPaymentToList(List<IPayment> list, Payment payment) {

				if (payment instanceof CompositePayment) {

					CompositePayment compositePayment = (CompositePayment) payment;
					for (Payment child : compositePayment.getChilds()) {
						addPaymentToList(list, child);
					}
				} else {
					try {
						IPayment pm = new IPayment() {

							@Override
							public PaymentType getType() {
								return PaymentType.values()[payment.getType().ordinal()];
							}

							@Override
							public String getName() {
								return payment.getName();
							}

							@Override
							public String getDescription() {
								return payment.getDescription();
							}

							@Override
							public double getAmount() {
								return Optional.ofNullable(payment.getAmount()).orElse(0.00);
							}

							@Override
							public String getExpression() {
								return payment.getExpression();
							}
						};
						list.add(pm);

					} catch (Exception ignored) {
					}
				}
			}

			@Override
			public Double getOvertimeBase() {
				return draft.gethExtraBase();
			}

			@Override
			public Double getNonEstructuralOvertimeBase() {
				return draft.getNonHExtraBase();
			}

			@Override
			public Date getIssueDate() {
				return draft.getIssueDate();
			}

			@Override
			public Double getIrpfBase() {
				return draft.getIrpfBase();
			}

			@Override
			public Double getInMoneyIrpfBase() {
			    return draft.getMoneyIrpfBase();
			}
			
			@Override
			public Double getInKindIrpfBase() {
				return draft.getInkindIrpfBase();
			}

			@Override
			public Integer getId() {
				return draft.getId();
			}

			@Override
			public Double getExtraPayProration() {
				return draft.getProrationBase();
			}

			@Override
			public String getEnterpriseName() {
				return draft.getEnterpriseName();
			}

			@Override
			public String getEnterpriseDocument() {
				return draft.getEnterpriseDocument();
			}

			@Override
			public Costs getEnterpriseCosts() throws SalaryException {
				throw new NoSuchMethodError();
			}

			@Override
			public String getEnterpriseAddress() {
				return draft.getEnterpriseAddress();
			}

			@Override
			public Date getEndDate() {
				return draft.getEndDate();
			}

			@Override
			public String getEmployeeName() {
				return draft.getEmployeeName();
			}

			@Override
			public String getEmployeeDocument() {
				return draft.getEmployeeDocument();
			}

			@SuppressWarnings("unchecked")
			@Override
			public <T extends IDeduction> Collection<T> getEmbargoS() throws SalaryException {
				ArrayList<IDeduction> embargos = new ArrayList<>();

				for (Deduction embargo : draft.getEmbargos()) {
					addDeductionToList(embargos, embargo);
				}

				return (Collection<T>) embargos;
			}

			public void addDeductionToList(List<IDeduction> list, Deduction deduction) {

				if (deduction instanceof CompositeDeduction) {
					CompositeDeduction compositeDeduction = (CompositeDeduction) deduction;
					for (Deduction child : compositeDeduction.getChilds()) {
						addDeductionToList(list, child);
					}
				} else {
					try {
						IDeduction pm = new IDeduction() {

							@Override
							public DeductionType getType() {

								/**
								 * @TODO fix this... :(
								 */
								com.esferalia.aon.gwt.payroll.shared.Deduction.Type type = deduction.getType();
								if (type == null)
									type = type.OTHER;

								return DeductionType.values()[type.ordinal()];
							}

							@Override
							public String getName() {
								return deduction.getName();
							}

							@Override
							public String getDescription() {
								return deduction.getDescription();
							}

							@Override
							public double getAmount() {
								return AonNumberUtils.zeroIfNull(deduction.getAmount());
							}

							@Override
							public String getExpression() {
								return deduction.getExpression();
							}
						};

						list.add(pm);

					} catch (Exception ignored) {
					}
				}
			}

			@Override
			public Deductions getDeductions() throws SalaryException {
				return null;
			}

			@SuppressWarnings("unchecked")
			@Override
			public <T extends IDeduction> Collection<T> getDeductionS() throws SalaryException {
				List<IDeduction> deductions = new ArrayList<>();
				List<Deduction> draftDeductions = draft.getDeductions();

				for (Deduction draftDeduction : draftDeductions) {
					addDeductionToList(deductions, draftDeduction);
				}

				return (Collection<T>) deductions;
			}

			@Override
			public <T extends IDeduction> Collection<T> getCostS() throws SalaryException {
				List<IDeduction> costs = new ArrayList<>();

				for (Deduction cost : draft.getCosts()) {
					addDeductionToList(costs, cost);
				}

				return (Collection<T>) costs;
			}

			@Override
			public Double getCommonBase() {
				return draft.getCgcBase();
			}

			@Override
			public Date getChargeDate() {
				return draft.getChargeDate();
			}

			@Override
			public String getCcc() {
				return draft.getEnterpriseCCC();
			}

			@Override
			public String getCategory() {
				return draft.getEmployeeAgreementCategory();
			}
		};
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

					@Override
					public Collection<SalaryEmbargo> getEmbargoS() throws SalaryException {
						return getSalaryEmbargos();
					}
				};
				// default ones
				salary.setTotalIrpf(0.00);
			};
		};

		SmartContractSalaryCalculator<com.esferalia.aon.payroll.Salary> calculator = new SmartContractSalaryCalculator<com.esferalia.aon.payroll.Salary>();

		calculator.setSalaryBuilder(salaryBuilder);

		Connection conn = null;
		ISQLContractSalaryCalculatorContext ctx;
		try {
			conn = AonServletUtils.getConnection(domain);
			ctx = EmployeesServiceHelper.getSalaryCalculatorContext(conn, draft, null);
			com.esferalia.aon.payroll.Salary salary = (com.esferalia.aon.payroll.Salary) calculator.calculate(ctx);

			Payments payments = new Payments();
			salary.getSalaryPayments().forEach(p -> SalaryPaymentsFactory.managePayment(payments, p));
			try {
				salary.setPayments(payments);
			} catch (SalaryException e) {
			}

			Deductions deductions = new Deductions();
			salary.getSalaryDeductions().forEach(d -> SalaryDeductionsFactory.manageDeductions(deductions, d));
			deductions.setTotal(getOrZero(salary.getTotalDeduction()));
			deductions.setSocialSecurityContributions(getOrZero(salary.getSocialSecurityContributions()));
			try {
				salary.setDeductions(deductions);
			} catch (SalaryException e) {
			}

			Costs costs = new Costs();
			salary.getSalaryCosts().forEach(c -> SalaryCostsFactory.manageCosts(costs, c));
			try {
				salary.setEnterpriseCosts(costs);
			} catch (SalaryException e) {
			}

			Bonuses bonuses = new Bonuses();
			salary.getSalaryBonus().forEach(c -> bonuses.setTotal(bonuses.getTotal() + c.getAmount()));
			try {
				salary.setBonuses(bonuses);
			} catch (SalaryException e) {
			}

			Contract contract = PayrollServletUtils.getContract(conn, draft.getEmployee().getId());

			salary.setContract(contract);

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
	
	
	private static String getActualSettleCause(String causeStr) {
		if (causeStr == null)
			return "";
		if (AonStringUtils.equals(ContextVariable.UNFAIR.getName(), causeStr)) {
			return Dismissal.UNFAIR.getDescription();
		} else if (AonStringUtils.equals(ContextVariable.TEMP_COMPLETE.getName(), causeStr)) {
			return Dismissal.TEMP_END.getDescription();
		} else if (AonStringUtils.equals(ContextVariable.WORK_COMPLETE.getName(), causeStr)) {
			return Dismissal.WORK_END.getDescription();
		} else if (AonStringUtils.equals(ContextVariable.CONTRACT_COMPLETE.getName(), causeStr)) {
			return Dismissal.DEFINITE_END.getDescription();
		} else if (AonStringUtils.equals(ContextVariable.OBJECTIVE.getName(), causeStr)) {
			return Dismissal.OBJECTIVE.getDescription();
		} else if (AonStringUtils.equals(ContextVariable.CONDITIONS_CHANGE.getName(), causeStr)) {
			return Dismissal.CONDITIONS_CHANGE.getDescription();
		} else if (AonStringUtils.equals(ContextVariable.RETIREMENT.getName(), causeStr)) {
			return Dismissal.RETIREMENT.getDescription();
		} else if (AonStringUtils.equals(ContextVariable.NOT_PASS_TRIAL_PERIOD.getName(), causeStr)) {
			return Dismissal.NOT_PASS_TRIAL_PERIOD.getDescription();
		} else if (AonStringUtils.equals(ContextVariable.DEATH_OF_EMPLOYEE.getName(), causeStr)) {
			return Dismissal.DEATH_OF_EMPLOYEE.getDescription();
		}
		return "";
	}

	/**
	 * Just transpile Isalary to Settle
	 * 
	 * @param domain - The domain to search in
	 * @param draft  - The salaryDraft
	 * @return [Settle] The settle.
	 */
	private static Settle getSettle(String domain, SalaryDraft draft) {

		ISalary salary = getSalary(domain, draft);
		Settle settle = new Settle();

		String compCause = "";
		//GETTING THE SETTLE CAUSE FROM DRAFT CONTEXT
		if (draft != null && draft.getContext() != null) {
			compCause = draft.getContext().stream()
					.filter(variable -> variable != null && AonStringUtils.equals(variable.getName(), ContextVariable.COMPENSATION_CAUSE.getName()))
					.map(variable -> getActualSettleCause(variable.getExpression()))
					.findFirst().orElse("");
		}
		
		settle.setEmployeeName(salary.getEmployeeName())
				.setEmployeeCategory(salary.getCategory())
				.setEmployeeDocument(salary.getEmployeeDocument())
				.setEmployeeQuoteGroup(salary.getQuoteGroup())
				.setEmployeeSeniorityDate(salary.getSeniorityDate())
				.setEnterpriseAddress(salary.getEnterpriseAddress())
				.setEnterpriseCCC(salary.getCcc())
				.setEnterpriseDocument(salary.getEnterpriseDocument())
				.setEnterpriseName(salary.getEnterpriseName())
				.setEndDate(salary.getEndDate())
				.setIssueDate(salary.getIssueDate())
				.setTotalDeduction(salary.getTotalDeduction())
				.setTotalPayment(salary.getTotalPayment())
				.setTotalEnterprise(salary.getTotalEnterprise())
				.setTotalIrpf(salary.getTotalIrpf())
				.setTotalLiquid(salary.getTotalLiquid())
				.setSalaryDays(salary.getTimeUnits())
				.setStartDate(salary.getStartDate());

		settle.setCause(compCause != null ? compCause : "");

		try ( CloseableAONContext ctx = AONContext.getAONContext(domain, "") ) {
			// LinkedList<CompanyAdministrator> dirStaff = CompanyDAO.getDirStaff(ctx,
			// ctx.getDomainId());
			LinkedList<RDirStaff> dirStaff = RDirStaffDAO.getRepresentativeLabor(ctx, ctx.getDomainId());
			if (dirStaff != null && !dirStaff.isEmpty()) {
				String staffDocument = dirStaff.get(0).getDocument();
				String staffName = dirStaff.get(0).getName();

				settle.setRepresentativeDocument(staffDocument);
				settle.setRepresentativeName(staffName);
			}
		}


		try {
			for (IDeduction deduction : salary.getDeductionS()) {

				String name = deduction.getName();
				byte type = (byte) deduction.getType().ordinal();
				String description = deduction.getDescription();
				settle.addDeduction(type, name, description, deduction.getAmount(), type);
			}
		} catch (SalaryException ignored) {
		}

		try {
			for (IDeduction embargo : salary.getEmbargoS()) {
				String description = embargo.getDescription();
				settle.addEmbargo(description, embargo.getAmount());
			}
		} catch (SalaryException ignored) {
		}

		try {
			for (IPayment payment : salary.getPaymentS())
				settle.addPayment(payment.getName(), payment.getExpression(), payment.getDescription(),
						payment.getAmount(), 0.00, (byte) payment.getType().ordinal());
		} catch (SalaryException ignored) {
		}

		try {
			for (IDeduction cost : salary.getCostS()) {
				settle.addCost((byte) cost.getType().ordinal(), "0", cost.getDescription(), cost.getAmount(),
						(byte) cost.getType().ordinal());
			}
		} catch (SalaryException ignored) {
		}

		return settle;
	}

	private static com.esferalia.aon.payroll.Salary getSalary(String domain, AgreementDraft draft, List<Variable> context, int levelId) {

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

					@Override
					public Collection<SalaryEmbargo> getEmbargoS() throws SalaryException {
						return super.getSalaryEmbargos();
					}
				};
				// default ones
				salary.setTotalIrpf(0.00);
			};
		};

		SmartContractSalaryCalculator<com.esferalia.aon.payroll.Salary> calculator = new SmartContractSalaryCalculator<com.esferalia.aon.payroll.Salary>();

		calculator.setSalaryBuilder(salaryBuilder);

		Connection conn = null;
		ISQLContractSalaryCalculatorContext ctx;
		try {
			conn = getConnection(domain);
			ctx = getSalaryCalculatorContext(conn, draft, context, levelId);
			com.esferalia.aon.payroll.Salary salary = (com.esferalia.aon.payroll.Salary) calculator.calculate(ctx);

			Payments payments = new Payments();
			salary.getSalaryPayments().forEach(p -> SalaryPaymentsFactory.managePayment(payments, p));
			try {
				salary.setPayments(payments);
			} catch (SalaryException e) {
			}

			Deductions deductions = new Deductions();
			salary.getSalaryDeductions().forEach(d -> SalaryDeductionsFactory.manageDeductions(deductions, d));
			deductions.setTotal(getOrZero(salary.getTotalDeduction()));
			deductions.setSocialSecurityContributions(getOrZero(salary.getSocialSecurityContributions()));
			try {
				salary.setDeductions(deductions);
			} catch (SalaryException e) {
			}

			Costs costs = new Costs();
			salary.getSalaryCosts().forEach(c -> SalaryCostsFactory.manageCosts(costs, c));
			try {
				salary.setEnterpriseCosts(costs);
			} catch (SalaryException e) {
			}

			Bonuses bonuses = new Bonuses();
			salary.getSalaryBonus().forEach(c -> bonuses.setTotal(bonuses.getTotal() + c.getAmount()));
			try {
				salary.setBonuses(bonuses);
			} catch (SalaryException e) {
			}

			// fill salary , ugly code
			salary.setEmployeeDocument(AonStringUtils.repeat(" ", 9));
			String levelDescription = AonStringUtils.repeat(" ", 2);
			String draftDescription = AonStringUtils.repeat(" ", 12);
			String categoryDescription = AonStringUtils.repeat(" ", 12);

			for (Level level : draft.getLevels()) {
				if (level.getId() == levelId) {

					String description = level.getDescription();
					if (!AonStringUtils.isBlank(description))
						levelDescription = description;

					Map<Integer, Set<String>> categoriesMap = draft.getCategoriesMap();
					if (categoriesMap != null) {
						Set<String> categories = categoriesMap.get(levelId);
						if (categories != null) {
							for (String category : categories) {
								if (!AonStringUtils.isBlank(category)) {
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
			salary.setCcc(AonStringUtils.repeat(" ", 11));
			salary.setSocialSecurityNumber(AonStringUtils.repeat(" ", 10));

			Contract contract = new Contract();
			contract.setId(0);
			Person person = new Person();
			person.setId(0);
			contract.setPerson(person);

			salary.setContract(contract);

			WorkPlace workPlace = new WorkPlace();
			contract.setWorkPlace(workPlace);

			try {
				EnterpriseCCC enterpriseCCC = null; // getDefaultHEnterpriseCCC();

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
				salary.setEnterpriseDocument(enterprise.getRegistry().getDocument());
				salary.setEnterpriseName(enterprise.getRegistry().getFullName());
				/*
				 * RegistryAddress rAddress = enterprise.getRegistry() .getDefaultAddress(); if
				 * (rAddress != null) { salary.setEnterpriseAddress(rAddress.getFullAddress());
				 * workPlace.setAddress(rAddress); }
				 */
				workPlace.setEnterprise(enterprise);
				person.setRegistry(enterprise.getRegistry());

			} catch (Exception e) {
				if (!AonStringUtils.isBlank(draft.getDescription()))
					draftDescription = draft.getDescription();
				salary.setEnterpriseName(draftDescription);
				salary.setEnterpriseDocument(AonStringUtils.repeat(" ", 9));
				salary.setEnterpriseAddress(AonStringUtils.repeat(" ", 25));
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
			contractCriteria.addEqualExpression(SQLConstants.CONTRACT + "." + ContractColumns.ID,
					draft.getEmployee().getId());

			class IrpfListener implements IListener {
				private IrpfOutcome irpfOutcome;

				@Override
				public void onIrpf(IrpfOutcome irpfOutcome) {
					this.irpfOutcome = irpfOutcome;
				}

				@Override
				public void onUndefinedData(IExpression expression, String variableName, String message, Date start,
						Date end) {
				}

				@Override
				public void onRedefinedImplicit(String name, ITimedVariable<?> redefined, ITimedVariable<?> implicit) {
				}

			}

			IrpfListener listener = new IrpfListener();
			SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext> draftCtx = EmployeesServiceHelper
					.getSalaryCalculatorContext(conn, draft, null);

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

	protected static com.esferalia.aon.payroll.IrpfOutcome getIrpfOutcome(Irpf irpf) {
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

	private static ISQLContractSalaryCalculatorContext getSalaryCalculatorContext(final Connection conn,
			final AgreementDraft draft, int levelId) throws ExpressionException, SQLException {

		Map<String, Object> data = new HashMap<String, Object>();
		data.put(ContextVariable.QUOTE_GROUP.getName(), "01");
		data.put(ContextVariable.TC2.getName(), ContractCode.C100.getValue());

		return getSalaryCalculatorContextImpl(conn, draft, levelId, data);
	}
	
	private static ISQLContractSalaryCalculatorContext getSalaryCalculatorContext(final Connection conn,
			final Date startDate, int levelId) throws ExpressionException, SQLException {

		Map<String, Object> data = new HashMap<String, Object>();
		data.put(ContextVariable.QUOTE_GROUP.getName(), "01");
		data.put(ContextVariable.TC2.getName(), ContractCode.C100.getValue());

		return getSalaryCalculatorContextImpl(conn, startDate, levelId, data);
	}

	private static ISQLContractSalaryCalculatorContext getSalaryCalculatorContext(final Connection conn,
			final AgreementDraft draft, List<Variable> vars, int levelId) throws ExpressionException, SQLException {
		
		HashMap<String, Object> data = new HashMap<>();
		vars.forEach( var -> data.put(var.getName(), var.getValue()));
		
		return getSalaryCalculatorContextImpl(conn, draft, levelId, data);
	}

	private static ISQLContractSalaryCalculatorContext getSalaryCalculatorContext(final Connection conn,
			final AgreementDraft draft, Map<String, Object> data, int levelId) throws ExpressionException, SQLException {

		return getSalaryCalculatorContextImpl(conn, draft, levelId, data);
	}

	private static ISQLContractSalaryCalculatorContext getSalaryCalculatorContextImpl(Connection conn,
			final AgreementDraft draft, int levelId, Map<String, Object> data)
			throws ExpressionException, SQLException {

		Date startDate = draft.getStartDate();
		Date endDate = DateUtils.getLastDayOfMonth(startDate);

		SQLAgreementSalaryCalculatorContext sqlAgreementSalaryCalculatorContext = new SQLAgreementSalaryCalculatorContext(
				conn, startDate, endDate, levelId);

		sqlAgreementSalaryCalculatorContext.next(ctx -> data.entrySet().stream().forEach(entry -> ctx
				.putVariable(entry.getKey(), new TimedObject<Object>(entry.getValue(), startDate, endDate))));

		return sqlAgreementSalaryCalculatorContext;
	}
	
	private static ISQLContractSalaryCalculatorContext getSalaryCalculatorContextImpl(Connection conn,
			final Date startDate, int levelId, Map<String, Object> data)
			throws ExpressionException, SQLException {

		Date endDate = DateUtils.getLastDayOfMonth(startDate);

		SQLAgreementSalaryCalculatorContext sqlAgreementSalaryCalculatorContext = new SQLAgreementSalaryCalculatorContext(
				conn, startDate, endDate, levelId);

		sqlAgreementSalaryCalculatorContext.next(ctx -> data.entrySet().stream().forEach(entry -> ctx
				.putVariable(entry.getKey(), new TimedObject<Object>(entry.getValue(), startDate, endDate))));

		return sqlAgreementSalaryCalculatorContext;
	}

	private static ISalary getSalary(String domain, SalaryPreview draft) {
		SalaryBuilder salaryBuilder = new SalaryBuilder();

		SmartContractSalaryCalculator<com.esferalia.aon.payroll.Salary> calculator = new SmartContractSalaryCalculator<com.esferalia.aon.payroll.Salary>();

		calculator.setSalaryBuilder(salaryBuilder);

		Connection conn = null;

		ISQLContractSalaryCalculatorContext ctx;
		try {
			conn = AonServletUtils.getConnection(domain);

			ctx = getSQLContractSalaryCalculatorContext(conn, draft);
			com.esferalia.aon.payroll.Salary salary = (com.esferalia.aon.payroll.Salary) calculator.calculate(ctx);

			Contract contract = PayrollServletUtils.getContract(conn, draft.getEmployee().getId());

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
				if (conn != null)
					conn.close();
			} catch (SQLException e) {

			}
		}

	}

	private static ISQLContractSalaryCalculatorContext getSQLContractSalaryCalculatorContext(Connection conn,
			SalaryPreview preview) throws ExpressionException, SQLException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(tableCol(CONTRACT, ContractColumns.ID), preview.getEmployee().getId());

		Date startDate = preview.getStartDate();
		Date endDate = preview.getEndDate();
		Date issueDate = preview.getIssueDate();

		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(conn, startDate, endDate,
				issueDate, criteria);
		ctx.next();
		return ctx;
	}

	private static void groups(ResultSet rs, GroupHandler... handlers) throws SQLException {

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
			enterprise.setName(rs.getString(tableCol(REGISTRY, RegistryColumns.NAME)));
			enterprise.setDomain(rs.getInt(tableCol(ENTERPRISE, RegistryColumns.DOMAIN)));

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
			workplace.setId(rs.getInt(tableCol(WORKPLACE, WorkplaceColumns.ID)));
			workplace.setDescription(rs.getString(tableCol(WORKPLACE, WorkplaceColumns.DESCRIPTION)));

			Object contractId = rs.getObject(tableCol(CONTRACT, ContractColumns.ID));
			workplace.setActive(contractId != null && rs.getBoolean(tableCol(WORKPLACE, WorkplaceColumns.ACTIVE)));
			if (contractId != null) {
				workplace.setDate(rs.getDate(CONTRACT_MAX_END_DATE));
			}

			Object agreementId = rs.getObject(tableCol(PAYROLL_WORKPLACE, PayrollWorkplaceColumns.AGREEMENT));
			if (agreementId != null) {
				Agreement agreement = new Agreement();
				agreement.setId((Integer) agreementId);
				agreement.setDescription(rs.getString(tableCol(AGREEMENT, AgreementColumns.DESCRIPTION)));
				agreement.setSSNumber(rs.getString(tableCol(AGREEMENT, AgreementColumns.SS_NUMBER)));
				agreement.setDomain((Integer) rs.getObject(tableCol(AGREEMENT, AgreementColumns.DOMAIN)));
				workplace.setAgreement(agreement);
			}

			Object activityId = rs.getObject(tableCol(PAYROLL_WORKPLACE, PayrollWorkplaceColumns.ENTERPRISE_ACTIVITY));
			if (activityId != null) {
				Activity activity = new Activity();
				activity.setId((Integer) agreementId);
				activity.setDescription(
						rs.getString(tableCol(ENTERPRISE_ACTIVITY, EnterpriseActivityColumns.DESCRIPTION)));
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
			if (id == null)
				return;

			ccc = new CCC();
			ccc.setId(rs.getInt(tableCol(ENTERPRISE_CCC, EnterpriseCccColumns.ID)));
			ccc.setCode(rs.getString(tableCol(ENTERPRISE_CCC, EnterpriseCccColumns.CCC)));
			ccc.setGeozone(rs.getString(tableCol(ENTERPRISE_CCC, EnterpriseCccColumns.GEOZONE)));
			ccc.setRegime(JooqEnterprise.getSSRegime(rs.getInt(tableCol(ENTERPRISE_CCC, EnterpriseCccColumns.TYPE)))
					.getCode());
			if (null != workplaceHandler.getWorkplace().getActivity())
				workplaceHandler.getWorkplace().getActivity().addCcc(ccc);
		}

	}

	private static Set<String> getSystemVariables(Connection conn, Date start, Date end) throws SQLException {
		return getSystemDescriptions(conn, start, end).keySet();
	}

	private static Map<String, String> getSystemDescriptions(Connection conn, Date start, Date end)
			throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {

			stmt = conn.prepareStatement("SELECT " + SystemDataColumns.NAME + ", " + SystemDataColumns.COMMENTS
					+ " FROM " + SQLConstants.SYSTEM_DATA + " WHERE " + SystemDataColumns.START_DATE + " <= ? "
					+ " AND ( " + SystemDataColumns.END_DATE + " IS NULL " + " OR " + SystemDataColumns.END_DATE
					+ " >= ? " + ") ");
			stmt.setDate(1, new java.sql.Date(end.getTime()));
			stmt.setDate(2, new java.sql.Date(start.getTime()));

			Map<String, String> descriptions = new HashMap<String, String>();

			rs = stmt.executeQuery();
			while (rs.next()) {
				descriptions.put(rs.getString(SystemDataColumns.NAME), rs.getString(SystemDataColumns.COMMENTS));
			}
			return descriptions;
		} finally {

		}
	}

	private static void deleteAllSettles(Connection conn, int id) throws SQLException {
		ResultSet rs = null;
		PreparedStatement settleStmt = null;
		try {
			settleStmt = conn
					.prepareStatement(String.format("SELECT %s " + "FROM %s " + "WHERE %s = ? " + "AND %s = ? ",
							SalaryColumns.ID, SALARY, SalaryColumns.CONTRACT, SalaryColumns.TYPE));

			settleStmt.setInt(1, id);
			settleStmt.setInt(2, SalaryType.SETTLE.ordinal());
			rs = settleStmt.executeQuery();
			ArrayList<Integer> ids = new ArrayList<Integer>();
			while (rs.next())
				ids.add(rs.getInt(SalaryColumns.ID));

			JooqPayrollSalaries.deleteSalaries(conn, ids);
		} finally {
			if (rs != null)
				rs.close();
			if (rs != null)
				settleStmt.close();
		}

	}

	private static void deleteSalaries(Connection conn, int... ids) throws SQLException {

		boolean autoCommit = conn.getAutoCommit();
		try {
			conn.setAutoCommit(false);
			
			JooqPayrollSalaries.deleteSalaries(conn, Arrays.stream(ids).boxed().collect(Collectors.toList()));

			conn.commit();
			
		} finally {
			conn.rollback();
			conn.setAutoCommit(autoCommit);

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

			ContextVariable contextVariable = ContextVariable.getVariableByName(entry.getKey());
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
	public EmployeeEventsData getEmployeeEvents(String domain, int contract,
			ArrayList<String> employeeContractVariables) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			return JooqEmployeeEvents.getEmployeeEvents(connection, contract, employeeContractVariables);

		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (connection != null) {
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
		try (Connection connection = AonServletUtils.getConnection(domain)) {
			return JooqEmployeeEvents.getEmployeeEventsByContract(connection, contractId, employeeContractVariablesDB);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public EmployeeEventsData setEmployeeEventsByContract(String domainName, Integer contractId,
			EmployeeEventsData employeeEventsData) {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			JooqEmployeeEvents.setEmployeeEvents(connection, contractId, employeeEventsData);
			return employeeEventsData;
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
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
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
				}
			}
		}

	}

	@Override
	public WorkplaceEmployees getWorkplaceEmployeesEvents(String domainName, Integer workplaceId)
			throws IllegalArgumentException {

		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			return JooqEvents.getWorkplaceEmployeesEvents(connection, workplaceId);

		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public WorkplaceEmployees getWorkplaceEmployees(String domainName, Workplace workplace) {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			
			WorkplaceEmployees workplaceEmployees = JooqEvents.getWorkplaceEmployees(connection, workplace, domainId);
			workplaceEmployees.setAgreements(JooqAgreement.getAgreements(connection, true, domainId, parentDomainId));
			workplaceEmployees.setActivitiesCCC(JooqWorkplace.getActivitiesCCC(domainId, connection));
			workplaceEmployees.setWorkplaces(JooqWorkplace.getWorkplaces(domainId, connection));
			workplaceEmployees.setPayMethods(JooqWorkplace.getPayMethods(connection, domainId));

			return workplaceEmployees;
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
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
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	@Override
	public EmployeeContractInfo getEmployeeInfoDataBase(String domainName, String login, Integer employeeContract, Workplace workplace) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			// Get EmployeeContractInfo
			Integer domainId = AonServletUtils.getDomainID(domainName);
			EmployeeContractInfo employeeContractInfo = JooqEmployee.getEmployeeInfo(connection, employeeContract);
			Attach cto = AON.getAttach(domainName, domainId, login, f -> f.getTypeProperty().eq(ContractAttachType.COPYCONTRACT.getValue().byteValue()).and(f.getContractProperty().eq(employeeContract)), AttachType.CONTRACT);
			employeeContractInfo.getContractInfo().setHasCto(null != cto && cto.getId() != null);
			Attach cbc = AON.getAttach(domainName, domainId, login, f -> f.getTypeProperty().eq(ContractAttachType.COPYBASIC.getValue().byteValue()).and(f.getContractProperty().eq(employeeContract)), AttachType.CONTRACT);
			employeeContractInfo.getContractInfo().setHasCbc(null != cbc && cbc.getId() != null);
			return employeeContractInfo;
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}

	}

	@Override
	public EmployeeContractInfo setEmployeeInfoDataBase(String domainName, EmployeeContractInfo newEmployeeInfo) {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			return JooqEmployee.setEmployeeInfo(connection, newEmployeeInfo, domainName);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public EmployeeContractInfo createEmployeeContract(String domainName, EmployeeContractInfo employeeContractData) {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			return JooqEmployee.createEmployeeContract(connection, employeeContractData);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private static boolean notAtEnterpriseSite() {
		return false;
	}

	private static double getOrZero(Double value) {
		return value != null ? value : 0.00;
	}

	@Override
	public String setEmployeeAFIChanges(String domain, Integer contractId, Date newDate, boolean isChangeContract,
			String tc2, boolean isQuoteContract, Integer quoteGroup, boolean isOcupationContract, String ocupation) {

		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domain);
			return JooqEmployee.setEmployeeAFIChanges(connection, contractId, newDate, isChangeContract, tc2,
					isQuoteContract, quoteGroup, isOcupationContract, ocupation);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (connection != null) {
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
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	// ----- Payroll Salaries

	@Override
	public Period getSalariesDates(String domainName, String userLogin, SalaryInfoFilter filter) {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);
			return JooqPayrollSalaries.getSalariesDates(connection, domainId, userId, filter);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public List<SalaryInfo> getSalaries(String domainName, String userLogin, SalaryInfoFilter filter) {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);
			return JooqPayrollSalaries.getSalaries(connection, domainId, userId, filter);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void deleteSalaries(String domainName, ArrayList<Integer> ids) {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			JooqPayrollSalaries.deleteSalaries(connection, ids);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public WorkplaceEmployees getWorkplaceActiveEmployees(String domainName, String userLogin, Integer workplaceId) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domainName);
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);
			return JooqPayrollSalaries.getWorkplaceActiveEmployees(connection, domainId, userId, workplaceId);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	@Override
	public List<EmployeeInfo> getEnterpriseActiveEmployees(String domainName, String userLogin, Integer enterpriseId) {
		Connection connection = null;
		try {
			connection = AonServletUtils.getConnection(domainName);
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);
			return JooqPayrollSalaries.getEnterpriseActiveEmployees(connection, domainId, userId, enterpriseId);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	// ----- New employee calendar

	@Override
	public EmployeeCalendarInfo getEmployeeCalendarInfo(String domainName, Integer contractId)
			throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			return JooqEmployeeCalendarNew.getEmployeeCalendar(connection, contractId);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void setEmployeeCalendarInfo(String domainName, Integer contractId,
			EmployeeCalendarInfo employeeCalendarInfo) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			JooqEmployeeCalendarNew.setEmployeeCalendar(connection, contractId, employeeCalendarInfo);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void resetEmployeeCalendarInfo(String domainName, Integer contractId) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			JooqEmployeeCalendarNew.resetEmployeeCalendar(connection, contractId);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public EmployeeEventsData setEmployeeEvents(String domainName, Integer idEmployee,
			EmployeeEventsData employeeEventsData) {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			return JooqEmployeeEvents.setEmployeeEvents(connection, idEmployee, employeeEventsData);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public ArrayList<EventEmployee> setEventsDraft(String domainName, ArrayList<EventEmployee> eventEmployees) {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			return JooqEvents.setEventsDraft(connection, eventEmployees);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void generateCertifaca2(String domainName, String userLogin, Integer contractId) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			JooqCertifica2.createCertifica2DB(connection, domainName, userLogin, domainId, contractId, null);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void generateCertifaca2(String domainName, String userLogin, Integer contractId, Certifica2Info certifica2Info)
			throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			if (AonStringUtils.isNotBlank(certifica2Info.getProfesionalCategory()))
				JooqCertifica2.insertCNOToDB(connection, domainId, contractId, certifica2Info.getProfesionalCategory(),
						certifica2Info.getStartDate(), certifica2Info.getEndDate());
			JooqCertifica2.createCertifica2DB(connection, domainName, userLogin, domainId, contractId, certifica2Info.getSuspensionCode());
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	// ------------------------------------------------- TGSS Files

	@Override
	public String getEmployeeTa(String domainName, String userLogin, Integer contractId, String situation,
			String regimen, String ctaCti, String nss, Date fecha) throws IllegalArgumentException {

		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			// TA from DB
			String base64Pdf = AonStringUtils.equalsIgnoreCase(situation, "ALTA")
					? JooqContractAttach.getContractTA(connection, contractId)
					: JooqContractAttach.getContractTAEnd(connection, contractId);

			// If not exist download
			if (AonStringUtils.isBlank(base64Pdf)) {
				Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");

				byte[] data = ServicioRED.getTADuplicatePOST(new ByteArrayInputStream(certificate.getData()),
						certificate.getPassword(), certificate.getType(), ctaCti, regimen,
						SituationType.valueOf(situation), nss, fecha);

				if (AonStringUtils.equalsIgnoreCase(situation, "ALTA"))
					JooqContractAttach.setContractTA(connection, domainId, contractId, data);
				else
					JooqContractAttach.setContractTAEnd(connection, domainId, contractId, data);

				base64Pdf = Base64.getEncoder().encodeToString(data);
			}

			Writer stringWriter = new StringWriter();
			encodeURIComponent("application/pdf", base64Pdf, stringWriter);

			stringWriter.flush();
			String dataUri = stringWriter.toString();
			stringWriter.close();

			return dataUri;
		} catch (SQLException | IOException | SegSocialException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	@Override
	public String getEmployeeIdc(String domainName, String userLogin, Integer contractId, Date date)
			throws IllegalArgumentException {

		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			// Get certificate
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");
			
			com.esferalia.aon.occam.api.model.payroll.Contract contract = 
					PAYROLL.
					getContract(domainName, domainId, userLogin, p -> p.getIdProperty().eq(contractId))
					.orElseThrow(() -> new IOException() );
					String ccc = contract.getEnterpriseCCC();
					String naf = contract.getPersonSsNumber();
					String regime = contract.getEnterpriseCCCRegime().getCode();	
					
			byte[] data = SistemaRED.getIDC(new ByteArrayInputStream(certificate.getData()), certificate.getPassword(), certificate.getType(), regime, ccc, naf, date);
			String base64Pdf = Base64.getEncoder().encodeToString(data);
					
			Writer stringWriter = new StringWriter();
			encodeURIComponent("application/pdf", base64Pdf, stringWriter);

			stringWriter.flush();
			String dataUri = stringWriter.toString();
			stringWriter.close();

			return dataUri;

		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	@Override
	public String checkEmployeeIdc(String domainName, String userLogin, Integer contractId, Date date, String idcDataUri)
			throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			String idcBase64 = decodeURIComponent(idcDataUri);
			
			String base64Pdf = EmployeesServiceHelper.checkIDCNSS(connection, domainName, parentDomainId, userLogin, userId, contractId, date, idcBase64);
			
			Writer stringWriter = new StringWriter();
			encodeURIComponent("application/pdf", base64Pdf, stringWriter);

			stringWriter.flush();
			String dataUri = stringWriter.toString();
			stringWriter.close();

			return dataUri;


		} catch (SQLException | IOException | SegSocialException | UnknownPDFException  e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	@Override
	public String getEmployeeIdcPlNss(String domainName, String userLogin, Integer contractId, Date date)
			throws IllegalArgumentException {

		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			String base64Pdf = EmployeesServiceHelper.getIDCNSS(connection, domainName, domainId, userLogin, userId,
					contractId, date);

			Writer stringWriter = new StringWriter();
			encodeURIComponent("application/pdf", base64Pdf, stringWriter);

			stringWriter.flush();
			String dataUri = stringWriter.toString();
			stringWriter.close();

			return dataUri;

		} catch (SQLException | IOException | SegSocialException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	@Override
	public List<Date> getEmployeeIdcDates(String domainName, String userLogin, Integer contractId, Date date)
			throws IllegalArgumentException {

		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);
			return EmployeesServiceHelper.getIDCDates(connection, domainName, domainId, userLogin, userId, contractId,
					date);

		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public EmployeeStatus getEmployeeStatus(String domainName, String userLogin, Integer contractId) {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);
			return EmployeesServiceHelper.getStatus(connection, domainName, domainId, userLogin, userId, contractId);
		} catch (SQLException | IOException | SegSocialException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void fillContract(String domainName, Integer contractId, Integer contractType, String formativeLvl, boolean isTransform)
			throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);

			byte[] pdfBytes = JooqContractPDF.contractFill(connection, domainId, parentDomainId, contractId,
					contractType, formativeLvl, isTransform);

			if(isTransform) JooqContractPDF.saveDraftContractTransform(domainName, domainId, contractId, pdfBytes);
			else JooqContractPDF.saveDraftContract(domainName, domainId, contractId, pdfBytes);
			
		} catch (SQLException | IllegalArgumentException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	@Override
	public void fillBasicCopy(String domainName, Integer contractId, Integer contractType, String formativeLvl) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);

			byte[] pdfBytes = JooqContractPDF.copyBasicFill(connection, domainId, parentDomainId, contractId,
					contractType, formativeLvl);

			JooqContractPDF.saveDraftCopyBasic(domainName, domainId, contractId, pdfBytes);
			
		} catch (SQLException | IllegalArgumentException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	@Override
	public void fillContractExtension(String domainName, EmployeeInfo employeeData, ContractInfo contractData) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);

			byte[] pdfBytes = JooqContractPDF.contractExtensionFill(connection, domainId, parentDomainId, employeeData, contractData);

			JooqContractPDF.saveDraftContractExtension(domainName, domainId, contractData.getContractId(), pdfBytes);
			
		} catch (SQLException | IllegalArgumentException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	@Override
	public void fillContractRelocation(String domainName, Integer contractId, Map<String, String> contractRelocationInfo) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);

			byte[] pdfBytes = JooqContractPDF.contractRelocationFill(connection, contractId, contractRelocationInfo);

			JooqContractPDF.saveDraftContractRelocation(domainName, domainId, contractId, pdfBytes);
			
		} catch (SQLException | IllegalArgumentException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	@Override
	public void setData(String domainName, String user, Integer contractId, ArrayList<Variable> data) {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, user, domainId, parentDomainId);

			data.stream().findFirst().ifPresent(d -> {
				Map<String, List<Variable>> map = EmployeesServiceHelper.getSSContractData(connection, domainName,
						domainId, user, userId, contractId, d.getStartDate());
				List<Variable> list = map.getOrDefault(d.getName(), Collections.emptyList());

				ContractData contractDatas[] = list.stream()
						.map(v -> new ContractData().setName(v.getName()).setExpression(v.getExpression())
								.setStartDate(v.getStartDate()).setEndDate(v.getEndDate()))
						.toArray(ContractData[]::new);

				PAYROLL.setContractData(domainName, domainId, user, f -> f.getIdProperty().eq(contractId),
						contractDatas);

			});

		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private static List<Integer> getConceptIds(List<Payment> systemPayments) {
		return systemPayments.stream().filter(p -> p.getConceptId() != null).map(p -> p.getConceptId()).distinct()
				.collect(Collectors.toList());
	}

	private static List<Payment> sub(List<Payment> paymentConcepts, List<Payment> systemPayments) {
		List<Integer> systemConceptsIds = getConceptIds(systemPayments);
		return paymentConcepts.stream()
				.filter(p -> AonStringUtils.isBlank(p.getName()) || !systemConceptsIds.contains(p.getId()))
				.collect(Collectors.toList());
	}

	private static boolean isDefault(Payment p) {
		return AonStringUtils.startsWith(p.getExpression(), "/*default*/");
	}

	private static void syncBonus(Connection connection, String currentDomainName, String currentUser,
			SalaryDraft salaryDraft) throws SQLException {
		Integer domainId = AonServletUtils.getDomainID(currentDomainName);
		Integer parentDomainId = AonServletUtils.getParentDomainID(currentDomainName);
		Integer userId = AonServletUtils.getUserID(connection, currentUser, domainId, parentDomainId);

		SistemaREDServlet.addBonus(currentUser, currentDomainName, domainId, userId, salaryDraft.getStartDate(),
				salaryDraft.getRegime(), salaryDraft.getEnterpriseCCC(), salaryDraft.getEmployeeSS());
	}

	private static String getLiquidacion(SalaryType salaryType) {
		switch (salaryType) {
		case DELAY:
			return "L90";
		case SETTLE:
			return "L13";
		default:
			return "L00";
		}
	}

	private static SalaryType getLiquidacion(String type) {
		switch (type) {
		case "L90":
			return SalaryType.DELAY;
		case "L13":
			return SalaryType.SETTLE;
		default:
			return SalaryType.SALARY;
		}
	}

	private static com.esferalia.aon.payroll.Salary newSalary(String type, String ccc, WorkerLiquidation liquidation,
			Optional<com.esferalia.aon.occam.api.model.Person> person) {
		com.esferalia.aon.payroll.Salary salary = PayrollServletUtils.newSalary();
		salary.setType(getLiquidacion(type));

		salary.setCcc(ccc);

		salary.setSocialSecurityNumber(liquidation.getNss());
		salary.setEmployeeName(person.map(p -> p.getName()).orElse(liquidation.getCaf()));

		salary.setRemuneration(
				Optional.ofNullable(liquidation.getCcBase()).map(d -> Double.parseDouble(d.toString())).orElse(0.00));
		salary.setTotalPayment(
				Optional.ofNullable(liquidation.getCcBase()).map(d -> Double.parseDouble(d.toString())).orElse(0.00));

		salary.setTotalEnterprise(Optional.ofNullable(liquidation.getTotalLiquidBusinessFee())
				.map(d -> Double.parseDouble(d.toString())).orElse(0.00));
		salary.setSocialSecurityContributions(Optional.ofNullable(liquidation.getTotalLiquidWorkerFee())
				.map(d -> Double.parseDouble(d.toString())).orElse(0.00));

		salary.setTotalDeduction(Optional.ofNullable(liquidation.getCcTotalFee())
				.map(d -> Double.parseDouble(d.toString())).orElse(0.00));
		salary.setTotalLiquid(Optional.ofNullable(liquidation.getTotalLiquidTotalFee())
				.map(d -> Double.parseDouble(d.toString())).orElse(0.00));

		// Buff !!!!.
		Payments payments = new Payments();
		try {
			salary.setPayments(payments);
		} catch (SalaryException e) {
		}

		Deductions deductions = new Deductions();
		deductions.setTotal(salary.getTotalDeduction());
		deductions.setSocialSecurityContributions(salary.getSocialSecurityContributions());
		try {
			salary.setDeductions(deductions);
		} catch (SalaryException e) {
		}

		Costs costs = new Costs();
		try {
			salary.setEnterpriseCosts(costs);
		} catch (SalaryException e) {
		}

		Bonuses bonuses = new Bonuses();
		try {
			salary.setBonuses(bonuses);
		} catch (SalaryException e) {
		}

		return salary;
	}

	// ------------------------------------------------- SEPE Files

	@Override
	public String getEmployeeCbc(String domainName, String userLogin, String ipf, Integer contractId, Date startDate,
			Date endDate, String sepeIde) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			// Copy Contract
			String base64Pdf = JooqContractAttach.getCopyBasic(connection, contractId);

			// If not exist download
			if (null == base64Pdf) {
				Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "SEPE");
				InputStream certificateInputStream = new ByteArrayInputStream(certificate.getData());

				byte [] pdfBytes = AonStringUtils.isBlank(sepeIde) ? Sepe.getCopyBasicPdf(certificateInputStream, certificate.getPassword(), certificate.getType(), ipf, startDate, endDate)
						: Sepe.getCopyBasicPdf(certificateInputStream, certificate.getPassword(), certificate.getType(), sepeIde);
				JooqContractAttach.setCopyBasic(connection, domainId, contractId, pdfBytes);
				base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);
			}

			Writer stringWriter = new StringWriter();
			encodeURIComponent("application/pdf", base64Pdf, stringWriter);

			stringWriter.flush();
			String dataUri = stringWriter.toString();
			stringWriter.close();

			return dataUri;

		} catch (SQLException | SepeException | IOException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	@Override
	public String getEmployeeCbcTransform(String domainName, String userLogin, String cif, String ipf, Integer contractId, Date startDate, String sepeIde) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			// Copy Contract
			String base64Pdf = JooqContractAttach.getCopyBasic(connection, contractId);

			System.out.println("getEmployeeCbcTransform()\ncif : " + cif + "\nipf : " + ipf + "\nstartDate : " + startDate + "\nsepeIde : " + sepeIde);

			// If not exist download
			if (null == base64Pdf) {
				Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "SEPE");
				InputStream certificateInputStream = new ByteArrayInputStream(certificate.getData());

				byte[] pdfBytes = AonStringUtils.isBlank(sepeIde) ?  Sepe.getTransformationCopyBasicPdf(certificateInputStream, certificate.getPassword(), certificate.getType(), ipf, cif, startDate)
						: Sepe.getTransformationCopyBasicPdf(certificateInputStream, certificate.getPassword(), certificate.getType(), sepeIde);
				JooqContractAttach.setCopyBasic(connection, domainId, contractId, pdfBytes);
				base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);
			}

			Writer stringWriter = new StringWriter();
			encodeURIComponent("application/pdf", base64Pdf, stringWriter);

			stringWriter.flush();
			String dataUri = stringWriter.toString();
			stringWriter.close();

			return dataUri;

		} catch (SQLException | SepeException | IOException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	@Override
	public String getEmployeeCto(String domainName, String userLogin, String ipf, Integer contractId, Date startDate, Date endDate, String sepeIde) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			// Copy Contract
			String base64Pdf = JooqContractAttach.getCopyContract(connection, contractId);

			// If not exist download
			if (null == base64Pdf) {
				Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "SEPE");
				InputStream certificateInputStream = new ByteArrayInputStream(certificate.getData());

				byte [] pdfBytes = AonStringUtils.isBlank(sepeIde) ? Sepe.getContratoPdf(certificateInputStream, certificate.getPassword(), certificate.getType(), ipf, startDate, endDate)
						: Sepe.getContratoPdf(certificateInputStream, certificate.getPassword(), certificate.getType(), sepeIde);
				JooqContractAttach.setCopyContract(connection, domainId, contractId, pdfBytes);
				base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);
			}

			Writer stringWriter = new StringWriter();
			encodeURIComponent("application/pdf", base64Pdf, stringWriter);

			stringWriter.flush();
			String dataUri = stringWriter.toString();
			stringWriter.close();

			return dataUri;

		} catch (SQLException | SepeException | IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	@Override
	public String getEmployeeCtoTransform(String domainName, String userLogin, String cif, String ipf, Integer contractId, Date startDate, String sepeIde) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			// Copy Contract
			String base64Pdf = JooqContractAttach.getCopyContractTransform(connection, contractId);
			
			System.out.println("getEmployeeCtoTransform()\ncif : " + cif + "\nipf : " + ipf + "\nstartDate : " + startDate + "\nsepeIde : " + sepeIde);

			// If not exist download
			if (null == base64Pdf) {
				Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "SEPE");
				InputStream certificateInputStream = new ByteArrayInputStream(certificate.getData());

				byte [] pdfBytes = AonStringUtils.isBlank(sepeIde) ? Sepe.getTransformationPdf(certificateInputStream, certificate.getPassword(), certificate.getType(), ipf, cif, startDate)
						: Sepe.getTransformationPdf(certificateInputStream, certificate.getPassword(), certificate.getType(), sepeIde);
				
				JooqContractAttach.setCopyContractTransform(connection, domainId, contractId, pdfBytes);
				base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);
			}

			Writer stringWriter = new StringWriter();
			encodeURIComponent("application/pdf", base64Pdf, stringWriter);

			stringWriter.flush();
			String dataUri = stringWriter.toString();
			stringWriter.close();

			return dataUri;

		} catch (SQLException | SepeException | IOException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	@Override
	public String getEmployeeCtoExtension(String domainName, String userLogin, String cif, String ipf, Integer contractId, Date extensionDate, Integer extensionNum, String sepeExtensionId) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "SEPE");
			InputStream certificateInputStream = new ByteArrayInputStream(certificate.getData());

			byte[] pdfBytes = Sepe.getContractExtensionPdf(certificateInputStream, certificate.getPassword(), certificate.getType(), ipf, cif, extensionDate, null == extensionNum ? 1 : extensionNum, sepeExtensionId);

			String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);

			Writer stringWriter = new StringWriter();
			encodeURIComponent("application/pdf", base64Pdf, stringWriter);

			stringWriter.flush();
			String dataUri = stringWriter.toString();
			stringWriter.close();

			return dataUri;

		} catch (SQLException | SepeException | IOException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}



	@Override
	public String getCertifica2PDF(String domainName, String userLogin, Integer contractId, String nif, Date endDate)
			throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			// Get domain id
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			// Copy Contract
			String base64Pdf = JooqContractAttach.getCertifica2PDF(connection, contractId);

			// If not exist download
			if (null == base64Pdf) {
				Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "SEPE");
				byte [] pdfBytes = Sepe.certEnterprisePdf(new ByteArrayInputStream(certificate.getData()),
						certificate.getPassword(), certificate.getType(), nif, endDate);

				JooqContractAttach.setCertifica2PDF(connection, domainId, contractId, pdfBytes);
				base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);
			}

			Writer stringWriter = new StringWriter();
			encodeURIComponent("application/pdf", base64Pdf, stringWriter);

			stringWriter.flush();
			String dataUri = stringWriter.toString();
			stringWriter.close();

			return dataUri;

		} catch (Exception e) {
			e.printStackTrace();
			if(e instanceof CertificateNotFoundException)
				throw new IllegalArgumentException("No existe certificado SEPE. Por favor introduzcalo desde el apartado Gesti\u00F3n Certificados");
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	// ------------------------------------------------- TGSS Comunications

	@Override
	public void sendEmployeeAlta(String domainName, String userLogin, EmployeeContractInfo employeeContractInfo)
			throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			// Domain, parentDomain and User id
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			// Get certificate
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");

			// Create nssList for ipfxnaf
			ArrayList<String> nssList = new ArrayList<String>();
			nssList.add(employeeContractInfo.getEmployeeInfo().getSsNumber());

			// Get employees ipdxnaf
			Collection<solutions.aon.seg.social.object.Employee> employeesAux = SistemaRED.ipfxnaf(
					new ByteArrayInputStream(certificate.getData()), certificate.getPassword(),
					certificate.getType(), nssList);

			solutions.aon.seg.social.object.Employee eemployeeAux = (solutions.aon.seg.social.object.Employee) employeesAux
					.toArray()[0];

			// Create employee object
			solutions.aon.seg.social.object.Employee employee = createEmployee(new Domain().setId(domainId).setName(domainName), new User().setLogin(userLogin), employeeContractInfo, eemployeeAux.getIpf());

			System.out.println(employee);

			// sendAlta
			SistemaRED.sendAlta(new ByteArrayInputStream(certificate.getData()), certificate.getPassword(),
					certificate.getType(), employee);
			
			JooqContrataContract.setSSStatus(domainName, employeeContractInfo.getContractInfo().getContractId());

		} catch (Exception e) {
			if (e instanceof solutions.aon.seg.social.exception.CertificateNotFoundException)
				throw new IllegalArgumentException("No existe certificado TGSS para realizar esta comunicacion");
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	@Override
	public void sendEmployeeBaja(String domainName, String userLogin, EmployeeContractInfo employeeContractInfo)
			throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			// Domain, parentDomain and User id
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			// Get certificate
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");

			// Create nssList for ipfxnaf
			ArrayList<String> nssList = new ArrayList<String>();
			nssList.add(employeeContractInfo.getEmployeeInfo().getSsNumber());

			// Get employees ipdxnaf
			Collection<solutions.aon.seg.social.object.Employee> employeesAux = SistemaRED.ipfxnaf(
					new ByteArrayInputStream(certificate.getData()), certificate.getPassword(),
					certificate.getType(), nssList);

			solutions.aon.seg.social.object.Employee eemployeeAux = (solutions.aon.seg.social.object.Employee) employeesAux
					.toArray()[0];

			// Create employee object
			solutions.aon.seg.social.object.Employee employee = createEmployee(new Domain().setId(domainId).setName(domainName), new User().setLogin(userLogin), employeeContractInfo,
					eemployeeAux.getIpf());

			System.out.println("sendEmployeeBaja \n" + employee.toString());

			// sendBaja
			SistemaRED.sendBaja(new ByteArrayInputStream(certificate.getData()), certificate.getPassword(),
					certificate.getType(), employee);

		} catch (Exception e) {
			if (e instanceof solutions.aon.seg.social.exception.CertificateNotFoundException)
				throw new IllegalArgumentException("No existe certificado TGSS para realizar esta comunicacion");
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	@Override
	public void movPrevDelete(String domainName, String userLogin, String situation, String regimen, String ctaCti,
			String nss, Date fecha) throws IllegalArgumentException {

		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			// Domain, parentDomain and User id
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			// Get certificate
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");

			InputStream certificateInputStream = new ByteArrayInputStream(certificate.getData());

			// movPrevDelete
			SistemaRED.movPrevDelete(certificateInputStream, certificate.getPassword(), certificate.getType(),
					SituationType.valueOf(situation), regimen, ctaCti, nss, fecha);

		} catch (Exception e) {
			if (e instanceof solutions.aon.seg.social.exception.CertificateNotFoundException)
				throw new IllegalArgumentException("No existe certificado TGSS para realizar esta comunicacion");
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	@Override
	public void altaConsolidadaDelete(String domainName, String userLogin, String situation, String regimen,
			String ctaCti, String nss, Date fecha) throws IllegalArgumentException {

		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			// Domain, parentDomain and User id
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			// Get certificate
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");

			InputStream certificateInputStream = new ByteArrayInputStream(certificate.getData());

			ArrayList<String> nssList = new ArrayList<>();
			nssList.add(nss);

			Collection<solutions.aon.seg.social.object.Employee> employeeCollection = SistemaRED
					.ipfxnaf(certificateInputStream, certificate.getPassword(), certificate.getType(), nssList);
			solutions.aon.seg.social.object.Employee employee = (solutions.aon.seg.social.object.Employee) employeeCollection
					.toArray()[0];

			// altaConsolidadaDelete
			SistemaRED.removeMovConsolidated(certificateInputStream, certificate.getPassword(), certificate.getType(),
					SituationType.valueOf(situation), regimen, ctaCti, nss, employee.getIpf(), fecha);

		} catch (Exception e) {
			if (e instanceof solutions.aon.seg.social.exception.CertificateNotFoundException)
				throw new IllegalArgumentException("No existe certificado TGSS para realizar esta comunicacion");
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	@Override
	public void cambioCoef(String domainName, String userLogin, EmployeeContractInfo employeeContractInfo, String coef,
			Date fecha) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			// Domain, parentDomain and User id
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			// Get certificate
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");

			System.out.println("DATOS: \n" + employeeContractInfo.getEmployeeInfo().getDocument() + "\n"
					+ employeeContractInfo.getEmployeeInfo().getSurName() + "\n"
					+ employeeContractInfo.getEmployeeInfo().getSecondSurName() + "\n"
					+ employeeContractInfo.getContractInfo().getCompleteCCC().substring(0, 4) + "\n"
					+ employeeContractInfo.getContractInfo().getCompleteCCC().substring(4,
							employeeContractInfo.getContractInfo().getCompleteCCC().length())
					+ "\n" + coef + "\n" + fecha);

			// Get employee nafxipf
			if(AonStringUtils.isBlank(employeeContractInfo.getEmployeeInfo().getSsNumber())) {
				solutions.aon.seg.social.object.Employee employeeAux = SistemaRED.nafxipf(
						new ByteArrayInputStream(certificate.getData()), certificate.getPassword(),
						certificate.getType(), employeeContractInfo.getEmployeeInfo().getDocument(),
						employeeContractInfo.getEmployeeInfo().getSurName(),
						employeeContractInfo.getEmployeeInfo().getSecondSurName());
	
				System.out.println(employeeAux.getNss());
				
				employeeContractInfo.getEmployeeInfo().setSsNumber(employeeAux.getNss());
			
			}

			// Parse coef
			if(AonStringUtils.isNotBlank(coef)) {
				Double coefD = Double.parseDouble(coef);
				if (coefD != null) {
					coefD = coefD * 1000;
					String coefStr = coefD.intValue() + "";
					coef = AonNumberUtils.equals(1000, coefD.intValue()) ? "000" : AonStringUtils.leftPad(coefStr, 3, '0');
				}
			}
			
			// cambioContratoCoef
			SistemaRED.cambioContratoCoef(new ByteArrayInputStream(certificate.getData()), certificate.getPassword(),
					certificate.getType(), employeeContractInfo.getEmployeeInfo().getDocument(),
					employeeContractInfo.getContractInfo().getCompleteCCC().substring(0, 4),
					employeeContractInfo.getContractInfo().getCompleteCCC().substring(4,
							employeeContractInfo.getContractInfo().getCompleteCCC().length()),
					employeeContractInfo.getEmployeeInfo().getSsNumber(), fecha, Optional.empty(), coef);

		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof solutions.aon.seg.social.exception.CertificateNotFoundException)
				throw new IllegalArgumentException("No existe certificado TGSS para realizar esta comunicacion");
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	@Override
	public void cambioContrato(String domainName, String userLogin, EmployeeContractInfo employeeContractInfo, String tc2, String partialityCoef, Date fecha) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			// Domain, parentDomain and User id
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			// Get certificate
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");

			System.out.println("DATOS: \n" + employeeContractInfo.getEmployeeInfo().getDocument() + "\n"
					+ employeeContractInfo.getEmployeeInfo().getSurName() + "\n"
					+ employeeContractInfo.getEmployeeInfo().getSecondSurName() + "\n"
					+ employeeContractInfo.getContractInfo().getCompleteCCC().substring(0, 4) + "\n"
					+ employeeContractInfo.getContractInfo().getCompleteCCC().substring(4,
							employeeContractInfo.getContractInfo().getCompleteCCC().length())
					+ "\n" + tc2 + "\n" + fecha);

			// Get employee nafxipf
//			solutions.aon.seg.social.object.Employee employeeAux = SistemaRED.nafxipf(
//					new ByteArrayInputStream(certificate.getData()), certificate.getPassword(),
//					certificate.getType(), employeeContractInfo.getEmployeeInfo().getDocument(),
//					employeeContractInfo.getEmployeeInfo().getSurName(),
//					employeeContractInfo.getEmployeeInfo().getSecondSurName());
//
//			System.out.println(employeeAux.getNss());
			
			// Parse coef
			if(AonStringUtils.isNotBlank(partialityCoef)) {
				Double coefD = Double.parseDouble(partialityCoef);
				if (coefD != null) {
					coefD = coefD * 1000;
					String coefStr = coefD.intValue() + "";
					partialityCoef = AonNumberUtils.equals(1000, coefD.intValue()) ? "000" : AonStringUtils.leftPad(coefStr, 3, '0');
				}
			}

			// cambioContratoCoef
			SistemaRED.cambioContratoCoef(new ByteArrayInputStream(certificate.getData()),
					certificate.getPassword(), certificate.getType(),
					employeeContractInfo.getEmployeeInfo().getDocument(),
					employeeContractInfo.getContractInfo().getCompleteCCC().substring(0, 4),
					employeeContractInfo.getContractInfo().getCompleteCCC().substring(4,
							employeeContractInfo.getContractInfo().getCompleteCCC().length()),
					employeeContractInfo.getEmployeeInfo().getSsNumber(), fecha, Optional.of(tc2), partialityCoef);

		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof solutions.aon.seg.social.exception.CertificateNotFoundException)
				throw new IllegalArgumentException("No existe certificado TGSS para realizar esta comunicacion");
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	@Override
	public void cambioGrupCtz(String domainName, String userLogin, EmployeeContractInfo employeeContractInfo, String grup_ctz, Date fecha) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			// Domain, parentDomain and User id
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			// Get certificate
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");

			System.out.println("DATOS: \n" + employeeContractInfo.getEmployeeInfo().getDocument() + "\n"
					+ employeeContractInfo.getEmployeeInfo().getSurName() + "\n"
					+ employeeContractInfo.getEmployeeInfo().getSecondSurName() + "\n"
					+ employeeContractInfo.getContractInfo().getCompleteCCC().substring(0, 4) + "\n"
					+ employeeContractInfo.getContractInfo().getCompleteCCC().substring(4,
							employeeContractInfo.getContractInfo().getCompleteCCC().length())
					+ "\n" + grup_ctz + "\n" + fecha);

			// Get employee nafxipf
			solutions.aon.seg.social.object.Employee employeeAux = SistemaRED.nafxipf(
					new ByteArrayInputStream(certificate.getData()), certificate.getPassword(),
					certificate.getType(), employeeContractInfo.getEmployeeInfo().getDocument(),
					employeeContractInfo.getEmployeeInfo().getSurName(),
					employeeContractInfo.getEmployeeInfo().getSecondSurName());

			System.out.println(employeeAux.getNss());

			// cambioGrupCtz
			SistemaRED.cambioGrupCtz(new ByteArrayInputStream(certificate.getData()), certificate.getPassword(),
					certificate.getType(), employeeContractInfo.getEmployeeInfo().getDocument(),
					employeeContractInfo.getContractInfo().getCompleteCCC().substring(0, 4),
					employeeContractInfo.getContractInfo().getCompleteCCC().substring(4,
							employeeContractInfo.getContractInfo().getCompleteCCC().length()),
					employeeAux.getNss(), grup_ctz, fecha);

		} catch (Exception e) {
			if (e instanceof solutions.aon.seg.social.exception.CertificateNotFoundException)
				throw new IllegalArgumentException("No existe certificado TGSS para realizar esta comunicacion");
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	@Override
	public void cambioOcupacion(String domainName, String userLogin, EmployeeContractInfo employeeContractInfo, String ocup, Date fecha) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			// Domain, parentDomain and User id
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			// Get certificate
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");

			System.out.println("DATOS: \n" + employeeContractInfo.getEmployeeInfo().getDocument() + "\n"
					+ employeeContractInfo.getEmployeeInfo().getSurName() + "\n"
					+ employeeContractInfo.getEmployeeInfo().getSecondSurName() + "\n"
					+ employeeContractInfo.getContractInfo().getCompleteCCC().substring(0, 4) + "\n"
					+ employeeContractInfo.getContractInfo().getCompleteCCC().substring(4,
							employeeContractInfo.getContractInfo().getCompleteCCC().length())
					+ "\n" + ocup + "\n" + fecha);

			// Get employee nafxipf
			solutions.aon.seg.social.object.Employee employeeAux = SistemaRED.nafxipf(
					new ByteArrayInputStream(certificate.getData()), certificate.getPassword(),
					certificate.getType(), employeeContractInfo.getEmployeeInfo().getDocument(),
					employeeContractInfo.getEmployeeInfo().getSurName(),
					employeeContractInfo.getEmployeeInfo().getSecondSurName());

			System.out.println(employeeAux.getNss());

			// cambioOcupacion
			SistemaRED.cambioOcupacion(new ByteArrayInputStream(certificate.getData()),
					certificate.getPassword(), certificate.getType(),
					employeeContractInfo.getEmployeeInfo().getDocument(),
					employeeContractInfo.getContractInfo().getCompleteCCC().substring(0, 4),
					employeeContractInfo.getContractInfo().getCompleteCCC().substring(4,
							employeeContractInfo.getContractInfo().getCompleteCCC().length()),
					employeeAux.getNss(), ocup, fecha);

		} catch (Exception e) {
			if (e instanceof solutions.aon.seg.social.exception.CertificateNotFoundException)
				throw new IllegalArgumentException("No existe certificado TGSS para realizar esta comunicacion");
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	@Override
	public void cambioCno(String domainName, String userLogin, EmployeeContractInfo employeeContractInfo, String cno, Date fecha) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			// Domain, parentDomain and User id
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			// Get certificate
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");

			System.out.println("DATOS: \n" + employeeContractInfo.getEmployeeInfo().getDocument() + "\n"
					+ employeeContractInfo.getEmployeeInfo().getSurName() + "\n"
					+ employeeContractInfo.getEmployeeInfo().getSecondSurName() + "\n"
					+ employeeContractInfo.getContractInfo().getCompleteCCC().substring(0, 4) + "\n"
					+ employeeContractInfo.getContractInfo().getCompleteCCC().substring(4,employeeContractInfo.getContractInfo().getCompleteCCC().length())
					+ "\n" + cno + "\n" + fecha);

			// Get employee nafxipf
			solutions.aon.seg.social.object.Employee employeeAux = SistemaRED.nafxipf(
					new ByteArrayInputStream(certificate.getData()), certificate.getPassword(),
					certificate.getType(), employeeContractInfo.getEmployeeInfo().getDocument(),
					employeeContractInfo.getEmployeeInfo().getSurName(),
					employeeContractInfo.getEmployeeInfo().getSecondSurName());

			System.out.println(employeeAux.getNss());

			// cambioOcupacion
			SistemaRED.cambioCno(new ByteArrayInputStream(certificate.getData()),
					certificate.getPassword(), certificate.getType(),
					employeeContractInfo.getEmployeeInfo().getDocument(),
					employeeContractInfo.getContractInfo().getCompleteCCC().substring(0, 4),
					employeeContractInfo.getContractInfo().getCompleteCCC().substring(4,
							employeeContractInfo.getContractInfo().getCompleteCCC().length()),
					employeeAux.getNss(), cno, fecha);

		} catch (Exception e) {
			if (e instanceof solutions.aon.seg.social.exception.CertificateNotFoundException)
				throw new IllegalArgumentException("No existe certificado TGSS para realizar esta comunicacion");
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	@Override
	public void cambioCatProf(String domainName, String userLogin, EmployeeContractInfo employeeContractInfo, String cat, Date fecha) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			// Domain, parentDomain and User id
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			// Get certificate
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");

			System.out.println("DATOS: \n" + employeeContractInfo.getEmployeeInfo().getDocument() + "\n"
					+ employeeContractInfo.getEmployeeInfo().getSurName() + "\n"
					+ employeeContractInfo.getEmployeeInfo().getSecondSurName() + "\n"
					+ employeeContractInfo.getContractInfo().getCompleteCCC().substring(0, 4) + "\n"
					+ employeeContractInfo.getContractInfo().getCompleteCCC().substring(4,
							employeeContractInfo.getContractInfo().getCompleteCCC().length())
					+ "\n" + cat + "\n" + fecha);

			// Get employee nafxipf
			solutions.aon.seg.social.object.Employee employeeAux = SistemaRED.nafxipf(
					new ByteArrayInputStream(certificate.getData()), certificate.getPassword(),
					certificate.getType(), employeeContractInfo.getEmployeeInfo().getDocument(),
					employeeContractInfo.getEmployeeInfo().getSurName(),
					employeeContractInfo.getEmployeeInfo().getSecondSurName());

			System.out.println(employeeAux.getNss());

			// cambioCatProf
			SistemaRED.cambioCatProf(new ByteArrayInputStream(certificate.getData()), certificate.getPassword(),
					certificate.getType(), employeeContractInfo.getEmployeeInfo().getDocument(),
					employeeContractInfo.getContractInfo().getCompleteCCC().substring(0, 4),
					employeeContractInfo.getContractInfo().getCompleteCCC().substring(4,
							employeeContractInfo.getContractInfo().getCompleteCCC().length()),
					employeeAux.getNss(), cat, fecha);

		} catch (Exception e) {
			if (e instanceof solutions.aon.seg.social.exception.CertificateNotFoundException)
				throw new IllegalArgumentException("No existe certificado TGSS para realizar esta comunicacion");
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	// ------------------------------------------------- SEPE Comunications
	
	@Override
	public void sendLlamamientoSEPE(String domainName, String userLogin, EmployeeContractInfo employeeContractInfo) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "SEPE");
			InputStream certificateIS = new ByteArrayInputStream(certificate.getData());
			
			String cif = employeeContractInfo.getContractInfo().getEnterpriseCIF();
			String ccc = employeeContractInfo.getContractInfo().getCompleteCCC();
			String nif = employeeContractInfo.getEmployeeInfo().getDocument();
			Date startDate = employeeContractInfo.getContractInfo().getStartDate();
			Date endDate = employeeContractInfo.getContractInfo().getEndDate();
			
			// Esto a lo mejor hay que consultarlo por que no se si es el IDE del ultimo contrato existente
			String ide = employeeContractInfo.getContractInfo().getSepeId();

			Sepe.sendLlamamiento(certificateIS, certificate.getPassword(), certificate.getType(), cif, ccc, nif, startDate, endDate, ide);

			JooqContrataContract.setSepeStatus(domainName, employeeContractInfo.getContractInfo().getContractId());

		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	@Override
	public void sendContractoSEPE(String domainName, String userLogin, EmployeeContractInfo employeeContractInfo) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "SEPE");
			InputStream certificateIS = new ByteArrayInputStream(certificate.getData());

			if (AonStringUtils.isEmpty(employeeContractInfo.getContractSpecificData().getFormativeLevel()))
				employeeContractInfo.setContractSpecificData(JooqContractSEPE.getContractSpecificData(connection,
						employeeContractInfo.getContractInfo().getContractId()));

			aon.sepe.objects.Contract cto = createContract(domainName, domainId, userLogin, employeeContractInfo,
					employeeContractInfo.getEmployeeInfo().getDocument());

			System.out.println(cto.toString());

			// Devuelve el ide del contrato en el SEPE
			String ide = Sepe.sendContract(certificateIS, certificate.getPassword(), certificate.getType(), cto);

			// Set Sepe Ide
			if (AonStringUtils.isBlank(employeeContractInfo.getContractInfo().getSepeId()) && AonStringUtils.isNotBlank(ide)) {
				JooqContrataContract.setSepeId(domainName, employeeContractInfo.getContractInfo().getContractId(), ide);
				employeeContractInfo.getContractInfo().setSepeId(ide);
			}
			
			JooqContrataContract.setSepeStatus(domainName, employeeContractInfo.getContractInfo().getContractId());

		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	@Override
	public void sendContractoCBSEPE(String domainName, String userLogin, EmployeeContractInfo employeeContractInfo) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "SEPE");
			InputStream certificateIS = new ByteArrayInputStream(certificate.getData());

			// Data
			String ipf = employeeContractInfo.getEmployeeInfo().getDocument();
			Date startDate = employeeContractInfo.getContractInfo().getStartDate();
			Date endDate = employeeContractInfo.getContractInfo().getEndDate();
			String signBasicCopy = employeeContractInfo.getContractSpecificData().getSignBasicCopy();
			if(AonStringUtils.isBlank(signBasicCopy))
				throw new IllegalArgumentException("El tipo de firma de copia basica es obligatorio");
			Integer signType = Integer.parseInt(AonStringUtils.isBlank(signBasicCopy) ? "1" : signBasicCopy);
			String workplaceAddress = employeeContractInfo.getContractInfo().getWorkplaceFullAddress();
			String restContract = employeeContractInfo.getContractSpecificData().getBasicCopy();

			Sepe.sendContratoCopyBasic(certificateIS, certificate.getPassword(), certificate.getType(), ipf, startDate,
					endDate, CopyBasic.FirmType.values()[signType-1], workplaceAddress, restContract);

		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	@Override
	public void sendContractoCBTransformSEPE(String domainName, String userLogin, EmployeeContractInfo employeeContractInfo) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "SEPE");
			InputStream certificateIS = new ByteArrayInputStream(certificate.getData());

			// Data
			String ipf = employeeContractInfo.getEmployeeInfo().getDocument();
			Date startDate = employeeContractInfo.getContractInfo().getStartDate();
			
			aon.sepe.objects.CopyBasic copyBasic = createCopyBasic(employeeContractInfo);
			
			Sepe.sendTransformationCopyBasic(certificateIS, certificate.getPassword(), certificate.getType(), copyBasic, ipf, startDate);

		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	@Override
	public void sendContractExtension(String domainName, String userLogin, EmployeeContractInfo employeeContractInfo) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "SEPE");
			InputStream certificateIS = new ByteArrayInputStream(certificate.getData());

			aon.sepe.objects.ContractExtension contractExtension = new aon.sepe.objects.ContractExtension();
			contractExtension.setSepeId(employeeContractInfo.getContractInfo().getSepeId());
			contractExtension.setStartDate(employeeContractInfo.getContractInfo().getExtensionDate());
			contractExtension.setEndDate(employeeContractInfo.getContractInfo().getEndDate());
			contractExtension.setCif(employeeContractInfo.getEmployeeInfo().getDocument());
			contractExtension.setRegime(employeeContractInfo.getContractInfo().getCompleteCCC().substring(0, 4));
			contractExtension.setCtaCti(employeeContractInfo.getContractInfo().getCompleteCCC().substring(4,
					employeeContractInfo.getContractInfo().getCompleteCCC().length()));
			contractExtension.setDiscontinuo(employeeContractInfo.getContractInfo().isDiscontinuos());
			contractExtension.setConvenio(null != employeeContractInfo.getContractInfo().getAgreementLevelId());
			
			System.out.println(contractExtension.toString());
			
			String sepeExtensionId = Sepe.sendContractExtension(certificateIS, certificate.getPassword(), certificate.getType(), contractExtension);
			
			// Set Sepe Extension Ide
			if (AonStringUtils.isBlank(employeeContractInfo.getContractInfo().getSepeExtensionId()) && AonStringUtils.isNotBlank(sepeExtensionId)) {
				JooqContrataContract.setSepeExtensionId(domainName, employeeContractInfo.getContractInfo().getContractId(), sepeExtensionId);
				employeeContractInfo.getContractInfo().setSepeExtensionId(sepeExtensionId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	@Override
	public void removeContractoSEPE(String domainName, String userLogin, EmployeeContractInfo employeeContractInfo)throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			// Get domain id
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "SEPE");

			// Get contract SEPE id
			String sepeId = AonStringUtils.isBlank(employeeContractInfo.getContractSpecificData().getIde()) ?
					employeeContractInfo.getContractInfo().getSepeId() : employeeContractInfo.getContractSpecificData().getIde();

			// Remove Contrato from SEPE
			Sepe.removeContrato(new ByteArrayInputStream(certificate.getData()), certificate.getPassword(), certificate.getType(), sepeId);

			// Remove SEPE id from DB
			JooqContrataContract.removeSepeId(domainName, employeeContractInfo.getContractInfo().getContractId(), sepeId);
			
			// Remove CBC & CTO documents
			JooqContractAttach.removeCopyContract(connection, employeeContractInfo.getContractInfo().getContractId());
			JooqContractAttach.removeCopyBasic(connection, employeeContractInfo.getContractInfo().getContractId());
			JooqContrataContract.removeSepeId(domainName, employeeContractInfo.getContractInfo().getContractId(), sepeId);

		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	@Override
	public void sendCertifica2(String domainName, String userLogin, Integer contractId, Certifica2Info certifica2Info) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			// Get domain id
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "SEPE");

			// Create certificates
			Certificates certificates = JooqCertifica2.createCertificates(connection, domainName, userLogin, contractId, certifica2Info.getSuspensionCode());

			System.out.println(certificates.toString());

			// Send certificates
			byte[] certifica2PDF = Sepe.certEnterprise(new ByteArrayInputStream(certificate.getData()),
					certificate.getPassword(), certificate.getType(), certificates);

			if (null != certifica2PDF && certifica2PDF.length > 0)
				JooqContractAttach.setCertifica2PDF(connection, domainId, contractId, certifica2PDF);

		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof CertificateNotFoundException)
				throw new IllegalArgumentException("No existe certificado SEPE para realizar esta comunicacion");
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	@Override
	public void sendContractTransform(String domainName, String userLogin, EmployeeContractInfo employeeContractInfo) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "SEPE");
			InputStream certificateIS = new ByteArrayInputStream(certificate.getData());

			aon.sepe.objects.Contract cto = createContract(domainName, domainId, userLogin, employeeContractInfo, employeeContractInfo.getEmployeeInfo().getDocument());

			System.out.println("CTO\n" + cto.toString());

			aon.sepe.objects.CopyBasic copyBasic = createCopyBasic(employeeContractInfo);

			System.out.println("COPY BASIC\n" + copyBasic.toString());

			Sepe.sendTransformation(certificateIS, certificate.getPassword(), certificate.getType(), cto, copyBasic);

		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	@Override
	public void removeContractTransform(String domainName, String userLogin, String transformIde, Integer contractId) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "SEPE");
			InputStream certificateIS = new ByteArrayInputStream(certificate.getData());

			Sepe.removeTransformation(certificateIS, certificate.getPassword(), certificate.getType(), transformIde);
			JooqContractSEPE.deleteContractTransformData(connection, contractId);

		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	@Override
	public Map<String, String> getSepeComunicationData(String domainName, String userLogin, String ipf, Date date,
			Integer contractId) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "SEPE");
			InputStream certificateIS = new ByteArrayInputStream(certificate.getData());

			aon.sepe.objects.Contract sepeContract = Sepe.getContractData(certificateIS, certificate.getPassword(),
					certificate.getType(), ipf, date, date);

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			Map<String, String> result = new HashMap<>();

			result.put("ide", sepeContract.getSepeId());
			result.put("comunicationDate", dateFormat.format(sepeContract.getDateComContract()));

			System.out.println("------------- Contract SEPE -------------\n");
			System.out.println(sepeContract.toString());
			
			JooqContractSEPE.setSepeComunications(connection, domainId, contractId, sepeContract);

			return result;

		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	@Override
	public Map<String, String> getSepeTransformComunicationData(String domainName, String userLogin, String document, String enterpriseCif, Date originalStartDate, String sepeId, Integer contractId) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "SEPE");
			InputStream certificateIS = new ByteArrayInputStream(certificate.getData());

			aon.sepe.objects.Contract sepeContractData = Sepe.getTransformationData(certificateIS, certificate.getPassword(), certificate.getType(), document, enterpriseCif, originalStartDate, Optional.of(sepeId));

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			Map<String, String> result = new HashMap<>();

			result.put("trasnformIde", sepeContractData.getSepeId());
			result.put("comunicationTransformDate", dateFormat.format(sepeContractData.getDateComContract()));

			JooqContractSEPE.setSepeTransformIde(connection, domainId, contractId, sepeContractData.getSepeId());
			JooqContractSEPE.setSepeTransformComunicationDate(connection, domainId, contractId, sepeContractData.getDateComContract());

			return result;

		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	@Override
	public Map<String, String> getSepeExtensionComunicationData(String domainName, String userLogin, String document, String enterpriseCif, Date originalStartDate, String sepeId, Integer contractId) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "SEPE");
			InputStream certificateIS = new ByteArrayInputStream(certificate.getData());

			aon.sepe.objects.Contract sepeContractData = Sepe.getContractExtensionData(certificateIS, certificate.getPassword(), certificate.getType(), document, enterpriseCif, originalStartDate, Optional.ofNullable(sepeId));
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			Map<String, String> result = new HashMap<>();

			result.put("extensionIde", sepeContractData.getSepeId());
			result.put("comunicationExtensionDate", dateFormat.format(sepeContractData.getDateComContract()));

			JooqContractSEPE.setSepeExtensionIde(connection, domainId, contractId, sepeContractData.getSepeId());
			JooqContractSEPE.setSepeExtensionComunicationDate(connection, domainId, contractId, sepeContractData.getDateComContract());

			return result;

		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	// ------------------------------------------------- SEPE Methods

	@Override
	public Certifica2Info getCertifica2Info(String domainName, String userLogin, Integer contractId) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			Certifica2Info certifica2Info = JooqCertifica2.getCertifica2Info(connection, domainName, userLogin, contractId, null);
			System.out.println(certifica2Info);
			return certifica2Info;
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}

	// ------------------------------------------------- EmployeeContractPayments

	@Override
	public ContractPaymentData getContractPayements(String domainName, Integer contractId) {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			// Get domain id
			Integer domainId = AonServletUtils.getDomainID(domainName);
			return JooqEmployeeContractPayments.getContractConceptCalcs(connection, domainId, contractId);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void updateContractPayments(String domainName, Integer contractId, ContractPaymentData contractPaymentData) {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			JooqEmployeeContractPayments.updateContractPayments(connection, contractPaymentData);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void createContractPayment(String domainName, Integer contractId, ContractConceptCalc contractConceptCalc) {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			// Get domain id
			Integer domainId = AonServletUtils.getDomainID(domainName);
			JooqEmployeeContractPayments.createContractPayment(connection, domainId, contractId, contractConceptCalc);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void createContractPayment(String domainName, Integer contractId,
			List<ContractConceptCalc> contractConceptCalcList) {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			// Get domain id
			Integer domainId = AonServletUtils.getDomainID(domainName);
			JooqEmployeeContractPayments.createContractPayment(connection, domainId, contractId,
					contractConceptCalcList);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	// ------------------------------------------------- ContractExtension

	@Override
	public void contractExtension(String domainName, ContractExtension contractExtension) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			contractExtension.setDomainId(domainId);
			JooqContractExtension.createContractExtension(connection, contractExtension);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void deleteContractExtension(String domainName, Integer contractId) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			JooqContractExtension.deleteContractExtension(connection, contractId);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	// ------------------------------------------------- ContractTransform

	@Override
	public void contractTransform(String domainName, ContractTransform contractTransform) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			JooqContractTransform.createContractTransform(connection, contractTransform);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}
	
	@Override
	public void deleteContractTransform(String domainName, Integer contractId) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			JooqContractTransform.removeContractTransform(connection, contractId);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void removeContractTransform(String domainName, Integer contractId) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			JooqContractTransform.removeContractTransform(connection, contractId);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	// ------------------------------------------------- EmployeeIrpf

	@Override
	public List<EmployeeIrpf> getEmployeeIrpf(String domainName, String ssNumber, String document, Date startDate)
			throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			return JooqEmployeeIrpf.getEmployeeIrpf(connection, domainId, ssNumber, document, startDate);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void setEmployeeIrpf(String domainName, Integer contractId, String fullName, String document,
			String ssNumber, List<EmployeeIrpf> employeeIrpfs) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			JooqEmployeeIrpf.setEmployeeIrpf(connection, domainId, contractId, fullName, document, ssNumber,
					employeeIrpfs);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	// ------------------------------------------------- ContractVariables

	@Override
	public List<ContractVariable> getContractVariables(String domainName, Integer contractId)
			throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			return JooqEmployeeContractVariables.getContractVariables(connection, contractId);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	@Override
	public void updateContractVariables(String domainName, List<ContractVariable> contractVariables)
			throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			JooqEmployeeContractVariables.updateContractVariables(connection, contractVariables);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	@Override
	public void createContractVariable(String domainName, Integer contractId, ContractVariable contractVariable)
			throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			JooqEmployeeContractVariables.createContractVariable(connection, domainId, contractId, contractVariable);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	// ------------------------------------------------- Auxiliar Methods

	private solutions.aon.seg.social.object.Employee createEmployee(Domain domain, User user, EmployeeContractInfo employeeContractInfo, String ipf) {

		EmployeeBuilder builder = new EmployeeBuilder();

		builder.setNss(employeeContractInfo.getEmployeeInfo().getSsNumber());
		builder.setName(employeeContractInfo.getEmployeeInfo().getName());
		builder.setBirthDate(employeeContractInfo.getEmployeeInfo().getBirthdate());
		builder.setIpf(ipf);
		builder.setFra(employeeContractInfo.getContractInfo().getStartDate());
		
		Date endDate = employeeContractInfo.getContractInfo().getEndDate();
		Date holidayDate = employeeContractInfo.getContractInfo().getHolidaysDate();
		
		builder.setFrb(employeeContractInfo.getContractInfo().getEndDate());
		if(null != endDate && null != holidayDate && holidayDate.after(endDate))
			builder.setFrv(employeeContractInfo.getContractInfo().getHolidaysDate());
		if(null != employeeContractInfo.getContractInfo().getHolidaysDate())
			builder.setAsociativeSA(employeeContractInfo.getContractInfo().getSAA());
		builder.setRegime(employeeContractInfo.getContractInfo().getCompleteCCC().substring(0, 4));
		builder.setCtaCti(employeeContractInfo.getContractInfo().getCompleteCCC().substring(4,
				employeeContractInfo.getContractInfo().getCompleteCCC().length()));
		builder.setGc(employeeContractInfo.getContractInfo().getQuoteGroup());
		builder.setContract(employeeContractInfo.getContractInfo().getContractType());
		
		String cno = employeeContractInfo.getContractInfo().getCno();
		builder.setCno(employeeContractInfo.getContractInfo().getCno());

		String agreementColective = employeeContractInfo.getContractInfo().getAgreementColective();
		agreementColective = null == agreementColective ? getDefaultAgreement(domain, user, employeeContractInfo.getContractInfo().getWorkplaceId()) : agreementColective;

		builder.setColec(agreementColective);
		builder.setMdctz(employeeContractInfo.getContractInfo().getMdctz());

		Double coef = employeeContractInfo.getContractInfo().getPartialityCoef();
		if (coef != null && coef < 1.0) {
			coef = coef * 1000;
			builder.setFactor(coef);
			String coefStr = coef.toString();
			builder.setCoef(AonStringUtils.leftPad(coefStr, 3, '0'));
			builder.setFactor(coef);
		}

		builder.setOcup(employeeContractInfo.getContractInfo().getOcupation());

		String settleReason = employeeContractInfo.getContractInfo().getSettleReason();
		if (AonStringUtils.isNotBlank(settleReason))
			builder.setSituation(settleReason);

		String rlce = employeeContractInfo.getContractInfo().getRlce();
		if (AonStringUtils.isNotBlank(rlce))
			builder.setRlce(rlce);

		String employeesColective = employeeContractInfo.getContractInfo().getEmployeesColective();
		if (AonStringUtils.isNotBlank(employeesColective))
			builder.setCollective(employeesColective);

		return builder.build();
	}
	
	private String getDefaultAgreement(Domain domain, User user, Integer workplaceId) {
		if(null != workplaceId) {
			com.esferalia.aon.occam.api.model.Workplace workplace = AON.getWorkplace(domain.getName(), domain.getId(), user.getLogin(), f -> f.getIdProperty().eq(workplaceId));
			
			if(null != workplace) {
				PayrollWorkplace parollWorkplace = AON.getPayrollWorkpalce(domain.getName(), domain.getId(), user.getLogin(), f -> f.getDomainProperty().eq(domain.getId()).and(f.getWorkplaceProperty().eq(workplace.getId())));
			
				if(null != parollWorkplace.getAgreement()) {
					try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin())) {
						AgreementRecord agreementRecord = ctx.getDslContext().selectFrom(com.esferalia.aon.jooq.tables.Agreement.AGREEMENT)
							.where(com.esferalia.aon.jooq.tables.Agreement.AGREEMENT.ID.eq(parollWorkplace.getAgreement()))
							.fetchOne();
						
						if(null == agreementRecord) return getEnterpriseAgreement(domain, user);
						else return AonStringUtils.isBlank(agreementRecord.getSsNumber()) ? getEnterpriseAgreement(domain, user) : agreementRecord.getSsNumber();  
					} catch (Exception e) {
						return getEnterpriseAgreement(domain, user);
					}
				} else return getEnterpriseAgreement(domain, user);
			} else return getEnterpriseAgreement(domain, user);
			
		} else return getEnterpriseAgreement(domain, user);
	}
	
	private String getEnterpriseAgreement(Domain domain, User user) {
		LinkedList<EnterpriseData> enterpriseDataList = AON.getEnterpriseDataList(domain.getName(), domain.getId(), user.getLogin(), f -> f.getDomainProperty().eq(domain.getId()));
		Optional<EnterpriseData> enterpriseAgreementOpt = enterpriseDataList.stream().filter(data -> AonStringUtils.equalsIgnoreCase(data.getName(), "agreement")).findFirst();
		
		if(enterpriseAgreementOpt.isEmpty()) return "60888888888888";
		else {
			try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin())) {
				AgreementRecord agreementRecord = ctx.getDslContext().selectFrom(com.esferalia.aon.jooq.tables.Agreement.AGREEMENT)
					.where(com.esferalia.aon.jooq.tables.Agreement.AGREEMENT.ID.eq(Integer.parseInt(enterpriseAgreementOpt.get().getExpression())))
					.fetchOne();
				
				if(null == agreementRecord) return "60888888888888";
				else return AonStringUtils.isBlank(agreementRecord.getSsNumber()) ? "60888888888888" : agreementRecord.getSsNumber();  
			} catch (Exception e) {
				return "60888888888888";
			}
		}
	}

	private CopyBasic createCopyBasic(EmployeeContractInfo employeeContractInfo) throws IllegalArgumentException {
		CopyBasic copyBasic = new CopyBasic();
		copyBasic.setFini(employeeContractInfo.getContractInfo().getStartDate());
		copyBasic.setFend(employeeContractInfo.getContractInfo().getEndDate());
		copyBasic.setIpf(employeeContractInfo.getEmployeeInfo().getDocument());
		copyBasic.setWorkAddress(employeeContractInfo.getContractInfo().getWorkplaceFullAddress());

		String signBasicCopy = employeeContractInfo.getContractSpecificData().getSignBasicCopy();
		Integer signType = Integer.parseInt(AonStringUtils.isBlank(signBasicCopy) ? "1" : signBasicCopy);
		copyBasic.setFirmType(CopyBasic.FirmType.values()[signType]);

		if(AonStringUtils.isBlank(employeeContractInfo.getContractSpecificData().getBasicCopy()))
			throw new IllegalArgumentException("El campo \"Texto copia basica\" es obligatorio");
		
		copyBasic.setRestContract(employeeContractInfo.getContractSpecificData().getBasicCopy());

		return copyBasic;
	}

	private aon.sepe.objects.Contract createContract(String domainName, Integer domainId, String login,
			EmployeeContractInfo employeeContractInfo, String ipf) {
		com.esferalia.aon.occam.api.model.Workplace wp = AON.getWorkplace(domainName, domainId, login,
				f -> f.getIdProperty().eq(employeeContractInfo.getContractInfo().getWorkplaceId()));
		RegistryAddressFilter filter = f -> f.getIdProperty().eq(wp.getAddress());
		RegistryAddress workAddress = AON.get(domainName, domainId, login, filter);

		RegistryAddressFilter employeefilter = f -> f.getRegistryProperty()
				.eq(employeeContractInfo.getEmployeeInfo().getEmployeeId());
		RegistryAddress employeeAddress = AON.get(domainName, domainId, login, employeefilter);
		ContractBuilder builder = new ContractBuilder();

		builder.setCifEnterprise(employeeContractInfo.getContractInfo().getEnterpriseCIF());
		builder.setRegimen(employeeContractInfo.getContractInfo().getCompleteCCC().substring(0, 4));
		builder.setCtaCti(employeeContractInfo.getContractInfo().getCompleteCCC().substring(4,
				employeeContractInfo.getContractInfo().getCompleteCCC().length()));
		builder.setNss(employeeContractInfo.getEmployeeInfo().getSsNumber());
		builder.setIpf(ipf);
		builder.setName(employeeContractInfo.getEmployeeInfo().getName());
		int gender = Integer.parseInt(employeeContractInfo.getEmployeeInfo().getGender() + "");
		if (2 == gender)
			throw new IllegalArgumentException(
					"El sexo del trabajador es requerido. Debe rellenarlo en la pesta\u00F1a Datos Afiliaci\u00F3n");
		builder.setSex(SexType.values()[gender]);
		builder.setSurname(employeeContractInfo.getEmployeeInfo().getSurName());
		builder.setLastSurname(employeeContractInfo.getEmployeeInfo().getSecondSurName());
		builder.setCodNationality(
				Country.safeValueOf(employeeContractInfo.getEmployeeInfo().getNationalityCode()).getIsoCode());
		builder.setCodPaisDom(employeeAddress.getCountry() != null ? employeeAddress.getCountry().getIsoCode()
				: Country.ES.getIsoCode());
		builder.setCodMunDom(employeeContractInfo.getEmployeeInfo().getAddressCity());
		if (AonStringUtils.isEmpty(employeeContractInfo.getContractSpecificData().getFormativeLevel()))
			throw new IllegalArgumentException(
					"Nivel formativo obligatorio. Debe rellenarlo en la pesta\u00F1a Datos SEPE");
		builder.setCodFormativo(Integer.parseInt(employeeContractInfo.getContractSpecificData().getFormativeLevel()));
		builder.setCodOccupation(employeeContractInfo.getContractSpecificData().getCno());
		builder.setCodPaisWork(
				workAddress.getCountry() != null ? workAddress.getCountry().getIsoCode() : Country.ES.getIsoCode());
		builder.setCodMunWork(workAddress.getMunicipalityCode());
		Integer contractType = Integer.parseInt(employeeContractInfo.getContractInfo().getContractType());
		builder.setCodContract(contractType.toString());
		if (contractType == 410 || contractType == 510) {
			String interimCause = employeeContractInfo.getContractSpecificData().getSustitucionCause();
			if (AonStringUtils.isBlank(interimCause))
				throw new IllegalArgumentException("La interinidad es obligatoria para este tipo de contrato. Debe rellenarlo en la pesta\u00F1a Datos SEPE");
			builder.setInterinidad(interimCause);
		}
		if(contractType == 402 || contractType == 502) {
			String employeesColective = employeeContractInfo.getContractInfo().getEmployeesColective();
			if(AonStringUtils.isBlank(employeesColective))
				throw new IllegalArgumentException("El colectivo de trabajadores es obligatorio para los contratos de tipo 402 y 502. Debe rellenarlo en la pesta\u00F1a Datos Afiliaci\u00f3n");
			if(AonStringUtils.equalsIgnoreCase(employeesColective, "967")) {
				builder.setPrevisible(false);
			} else {
				Date startDate = employeeContractInfo.getContractInfo().getStartDate();
				Date endDate = employeeContractInfo.getContractInfo().getEndDate();
				if(null == endDate) throw new IllegalArgumentException("La fecha fin es obligatoria para los contratos de tipo 402 y 502 con situaci\u00f3n previsible. Debe rellenarlo en la pesta\u00F1a Datos Afiliaci\u00f3n");
				int daysBetween = DateUtils.getDaysBetween(startDate, endDate);
				builder.setPrevisible(daysBetween <= 90);
			}
		} else
			builder.setPrevisible(false);
		
		builder.setTitulacion(employeeContractInfo.getContractSpecificData().getAcademicTitulation());
		
		Boolean disc = employeeContractInfo.getContractSpecificData().getDisc();
		if(null != disc) {
			builder.setDiscontinuo(disc);
			String discReason = employeeContractInfo.getContractSpecificData().getDiscReason();
			if(disc && AonStringUtils.isBlank(discReason))
				throw new IllegalArgumentException("Si la transformaci\u00f3n es con indicador de discontinuidad, es obligatorio rellenar el motivo de la discontinuidad");
			builder.setDiscontinuoReason(AonStringUtils.equals(discReason, "P") ? DiscontinuoReason.PRORROGA_TACITA : DiscontinuoReason.INCAPACIDAD_TRANSITORIA);
		} else
			builder.setDiscontinuo(false);
		
		Boolean trueDate = employeeContractInfo.getContractSpecificData().getTrueDate();
		builder.setNoCertainDate(null != trueDate && trueDate);
		
		if(contractType == 420)
			builder.setPlanRecovery(employeeContractInfo.getContractSpecificData().getPlanRecovery());
		
		ContractTypeRecord contractTypeRecord = null;
		try {
			ContractType contractTypeC = new ContractType();
			contractTypeRecord = contractTypeC.getContractType(Integer.parseInt(employeeContractInfo.getContractInfo().getContractType()));
		} catch (Exception e) {
			// Nothing to do here
		}
		
		builder.setDateIniContract((null == contractTypeRecord || !contractTypeRecord.isTransform()) ? employeeContractInfo.getContractInfo().getStartDate() : employeeContractInfo.getContractInfo().getTransformDate());
		
		if((contractType == 420 || contractType == 520) && null == employeeContractInfo.getContractInfo().getEndDate())
			throw new IllegalArgumentException("La fecha fin es obligatoria para los contratos de tipo 420 y 520. Debe rellenarlo en la pesta\u00F1a Datos Afiliaci\u00f3n");
		
		if(employeeContractInfo.getContractSpecificData().getDisabilityB()) {
			builder.setDiscapacidad(true);
			builder.setDiscapacidadType(employeeContractInfo.getContractSpecificData().getDisability());
			builder.setCollectiveType(employeeContractInfo.getContractSpecificData().getBonusColective());
		} else builder.setDiscapacidad(false);
		
		if(employeeContractInfo.getContractSpecificData().getOlderThan52()) {
			builder.setOver52Years(AonStringUtils.equalsIgnoreCase(employeeContractInfo.getContractSpecificData().getOtherLegislations(), "001") ? Over52Years.REASS : Over52Years.RESTO_SUB);
		}
		
		if(employeeContractInfo.getContractSpecificData().getBonus()) {
			builder.setBonus(true);
			builder.setCollectiveType(employeeContractInfo.getContractSpecificData().getBonusType());
		} else builder.setBonus(false);
		
		builder.setDateFinContract(employeeContractInfo.getContractInfo().getEndDate());
		builder.setOldDateIniContract(employeeContractInfo.getContractInfo().getOriginalStartDate());
		builder.setOldDateFinContract(employeeContractInfo.getContractInfo().getOriginalEndDate());
		builder.setDateBirth(employeeContractInfo.getEmployeeInfo().getBirthdate());
		builder.setDateComContract(employeeContractInfo.getContractInfo().getTransformDate() == null ? 
				employeeContractInfo.getContractInfo().getStartDate() : employeeContractInfo.getContractInfo().getTransformDate());
		builder.setOffer(OfferType.NO);
		builder.setJndType(JndType.safeValueOf(employeeContractInfo.getContractSpecificData().getJourneyType()));
		builder.setDurationTypeJndHour(employeeContractInfo.getContractSpecificData().getJourneyDurationHours());
		builder.setDurationTypeJndMin(employeeContractInfo.getContractSpecificData().getJourneyDurationMinutes());
		builder.setDurationTypeCvnHour(employeeContractInfo.getContractSpecificData().getAgreementHours());
		builder.setDurationTypeCvnMin(employeeContractInfo.getContractSpecificData().getAgreementMinutes());
		builder.setDurationFormationHour(employeeContractInfo.getContractSpecificData().getFormationHours());
		builder.setDurationFormationMin(employeeContractInfo.getContractSpecificData().getFormationMinutes());

		builder.setWrittenContract(employeeContractInfo.getContractSpecificData().getWritenContract());

		return builder.build();
	}

	public static void printCostReceiptPDF(String domain, Cost cost, Salary.Type[] types, OutputStream os) {
		try {
			SalaryType[] salaryTypes = new SalaryType[types.length];
			for (int i = 0; i < types.length; i++)
				salaryTypes[i] = SalaryType.values()[types[i].ordinal()];

			ICollectionProvider salariesProvider = getSalariesProvider(domain, cost, salaryTypes, false);

			EnterprisePayroll enterprisePayroll = EmployeesServiceHelper.geteEnterprisePayroll("N\u00D3MINA DE EMPRESA",
					null, getDate(cost).getTime(), salariesProvider);

			PdfMaker.printEnterprisePayroll(enterprisePayroll, os, Optional.of(new Locale("Es")));

		} catch (IOException | ManagerBeanException | CanNotCreatePdfException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public Date checkAndUpdateServiAgreement(String domainName, String userLogin, Integer agreementId, String ssNumber,
			Integer lastDateYear) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			return AgreementUpdate.checkAndUpdateServiAgreement(connection, domainId, userLogin, agreementId, ssNumber,
					lastDateYear);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public List<Certifica2Info> getSalariesOccam(String domainName, String login, ITEmployee itEmployee, Date startDate,
			Date endDate) {

		List<Certifica2Info> certs = new ArrayList<>();
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			String ccc = itEmployee.getContractInfo().getCompleteCCC().substring(4, itEmployee.getContractInfo().getCompleteCCC().length());
			String naf = itEmployee.getEmployeeInfo().getSsNumber();

			AON.getSalaryData(new Domain().setId(domainId).setName(domainName), login,
				f -> f.getCCCProperty().eq(ccc)
					.and(f.getSSProperty().eq(naf))
					.and(f.getStartDateProperty().ge(startDate))
					.and(f.getEndDateProperty().le(endDate))
					.and(
						f.getIsSalaryProperty().eq(true)
//						.or(f.getIsDelayProperty().eq(true))
						.or(f.getIsSettlementProperty().eq(true))
					)
			)
			.sorted((o1, o2) -> o2.getStartDate().compareTo(o1.getStartDate()))
			.forEach(salary -> {				
				salary.getContextData()
				.entrySet()
				.stream()
				.filter(d-> d.getKey().equals("DIAS_COTIZADOS"))
				.map(d-> d.getValue())
				.flatMap(Collection::stream)
				.distinct()
				.sorted((o1, o2) -> o2.getStartDate().compareTo(o1.getStartDate()))
				.forEach(dt->{
					Date start = dt.getStartDate();
					Date end = dt.getEndDate();
					
					Double baseCgc   = salary.getContextData("BASE_CGC", start, end).stream().map(d-> d.getExpression()).collect(summingDouble(Double::parseDouble));
					Double baseCgp   = salary.getContextData("BASE_CGP", start, end).stream().map(d-> d.getExpression()).collect(summingDouble(Double::parseDouble));
					Double quoteDays = salary.getContextData("DIAS_COTIZADOS", start, end).stream().map(d-> d.getExpression()).collect(summingDouble(Double::parseDouble));

					if (quoteDays != null && quoteDays > 0) {
						Certifica2Info cert = new Certifica2Info();
						cert.setStartDate(start);
						cert.setBaseCgc(baseCgc != null ? baseCgc : 0.00);
						cert.setBaseUnemployment(baseCgp != null ? baseCgp : 0.00);
						cert.setSettleQuoteDays(quoteDays.intValue());
						certs.add(cert);
					}
					
					System.out.println("startDate:"+dt.getStartDate()+" endDate:"+dt.getEndDate()+" value:"+dt.getExpression());
				});
			});
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
		return certs;
	}
	
	private static Throwable getRootCause(Throwable throwable) {
		Throwable cause = throwable;
		while ( cause.getCause() != null )
			cause = cause.getCause();
		return cause;
	}

}
