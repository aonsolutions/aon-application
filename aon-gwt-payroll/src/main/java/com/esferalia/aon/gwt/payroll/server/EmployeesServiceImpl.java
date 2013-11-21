package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.gwt.payroll.server.AonServletUtils.commit;
import static com.esferalia.aon.gwt.payroll.server.AonServletUtils.disableAutoCommit;
import static com.esferalia.aon.gwt.payroll.server.AonServletUtils.enableAutoCommit;
import static com.esferalia.aon.gwt.payroll.server.AonServletUtils.getConnection;
import static com.esferalia.aon.gwt.payroll.server.AonServletUtils.rollback;
import static com.esferalia.aon.payroll.sql.SQLConstants.AGREEMENT;
import static com.esferalia.aon.payroll.sql.SQLConstants.CONTRACT;
import static com.esferalia.aon.payroll.sql.SQLConstants.ENTERPRISE;
import static com.esferalia.aon.payroll.sql.SQLConstants.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.payroll.sql.SQLConstants.IRPF_DATA;
import static com.esferalia.aon.payroll.sql.SQLConstants.IRPF_REGULARIZATION;
import static com.esferalia.aon.payroll.sql.SQLConstants.IRPF_RESULT;
import static com.esferalia.aon.payroll.sql.SQLConstants.PAYMENT_CONCEPT;
import static com.esferalia.aon.payroll.sql.SQLConstants.PAYROLL_WORKPLACE;
import static com.esferalia.aon.payroll.sql.SQLConstants.PERSON;
import static com.esferalia.aon.payroll.sql.SQLConstants.REGISTRY;
import static com.esferalia.aon.payroll.sql.SQLConstants.SALARY;
import static com.esferalia.aon.payroll.sql.SQLConstants.WORKPLACE;

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.faces.context.FacesContext;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import org.apache.commons.lang.StringUtils;
import org.mvel2.CompileException;
import org.mvel2.ast.Function;
import org.mvel2.util.MethodStub;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;
import com.code.aon.report.OutputFormat;
import com.code.aon.report.ReportException;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.gwt.payroll.client.EmployeesService;
import com.esferalia.aon.gwt.payroll.shared.Activity;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft.Level;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft.SalaryTable;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.EvalException;
import com.esferalia.aon.gwt.payroll.shared.EvalSyntaxErrorException;
import com.esferalia.aon.gwt.payroll.shared.EvalWarning;
import com.esferalia.aon.gwt.payroll.shared.Events;
import com.esferalia.aon.gwt.payroll.shared.Irpf;
import com.esferalia.aon.gwt.payroll.shared.Irpf.IrpfData;
import com.esferalia.aon.gwt.payroll.shared.Irpf.IrpfRegularization;
import com.esferalia.aon.gwt.payroll.shared.Irpf.IrpfResult;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.gwt.payroll.shared.SalaryPreview;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.sql.SQLAgreementDraft;
import com.esferalia.aon.gwt.payroll.sql.SQLEvents;
import com.esferalia.aon.gwt.payroll.sql.SQLSalaryDraft;
import com.esferalia.aon.gwt.payroll.sql.SQLSalaryDraftCalculatorContext;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.IrpfOutcome;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractDelayCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext;
import com.esferalia.aon.payroll.irpf.IrpfCalculator;
import com.esferalia.aon.payroll.irpf.sql.SQLIrpfCalculatorContext;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractPaymentColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseActivityColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.IrpfDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.IrpfRegularizationColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.IrpfResultColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PaymentConceptColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PayrollWorkplaceColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PersonColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RegistryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryBonusColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryCostColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryDeductionColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryEmbargoColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryPaymentColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SystemDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.WorkplaceColumns;
import com.esferalia.aon.salary.CompositeSalaryBuilder;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.SalaryBuilderListener;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.enumeration.SalaryTypeVisitor;
import com.esferalia.aon.salary.expression.CheckException;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionContext.ExpressionExceptionWrapper;
import com.esferalia.aon.salary.expression.ExpressionContext.RemoveVariableError;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpressionVariable;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.InvalidVariables;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.salary.SalaryExpenseController;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class EmployeesServiceImpl extends AonRemoteServiceServlet implements
		EmployeesService {

	private static final Map<Object, Object> JR_HTML_EXPORTER_PARAMS = new HashMap<Object, Object>() {
		{
			put(JRHtmlExporterParameter.HTML_HEADER, "<div class='page' >");
			put(JRHtmlExporterParameter.BETWEEN_PAGES_HTML,
					"</div><div class='page' >");
			put(JRHtmlExporterParameter.HTML_FOOTER, "</div>");
		}
	};

	// ----------------------------------------------- EmployeesService methods

	@Override
	public Enterprise getEnterprise() throws IllegalArgumentException {
		Connection conn = null;
		try {
			initFacesContext();
			Integer userID = getUserID();
			Integer registryID = getEnterpriseID();
			conn = getConnection();
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
			releaseFacesContext();
		}
	}

	@Override
	public Enterprise[] getEnterprises() throws IllegalArgumentException {
		Connection conn = null;
		try {
			initFacesContext();
			Integer userID = getUserID();
			int registryIDs[] = getEnterpriseIDs();
			conn = getConnection();
			Enterprise enterprises[] = new Enterprise[registryIDs.length];
			for (int i = 0; i < registryIDs.length; i++) {
				enterprises[i] = getEnterprise(registryIDs[i], userID, conn);
			}
			return enterprises;

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
			releaseFacesContext();
		}
	}

	@Override
	public List<Salary> getSalaries(Employee employee)
			throws IllegalArgumentException {
		Connection conn = null;
		try {
			initFacesContext();
			conn = getConnection();
			if (employee != null) {
				return isAtEnterpriseSite() ? getSiteSalaries(conn,
						employee.getId()) : getSalaries(conn, employee.getId());
			} else {
				Integer personId = getPersonID();
				Integer enterpriseId = getEnterpriseID();
				Date maxChargeDate = Calendar.getInstance().getTime();
				return getSalaries(conn, enterpriseId, personId, maxChargeDate);
			} // EmployeeSite Not implemented yet.
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
			releaseFacesContext();
		}
	}

	@Override
	public List<Irpf> getIrpfs(Employee employee)
			throws IllegalArgumentException {
		Connection conn = null;
		try {
			initFacesContext();
			conn = getConnection();
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
			releaseFacesContext();
		}
	}

	@Override
	public List<Employee> getEmployees(int workplaceId, Date fromDate,
			String pattern, int offset, int limit)
			throws IllegalArgumentException {
		Connection conn = null;
		try {
			initFacesContext();
			conn = getConnection();
			return getEmployees(conn, workplaceId, fromDate, pattern, offset,
					limit);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} catch (com.code.aon.ql.util.ExpressionException e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
			releaseFacesContext();
		}
	}

	@Override
	public List<Cost> getWorkplaceCosts(int workplaceId)
			throws IllegalArgumentException {
		Connection conn = null;
		try {
			initFacesContext();
			conn = getConnection();
			return isAtEnterpriseSite() ? getSiteWorkplaceCosts(conn,
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
			releaseFacesContext();
		}
	}

	@Override
	public List<Cost> getEnterpriseCosts(int enterpriseId)
			throws IllegalArgumentException {
		Connection conn = null;
		try {
			initFacesContext();
			conn = getConnection();
			return isAtEnterpriseSite() ? getSiteEnterpriseCosts(conn,
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
			releaseFacesContext();
		}
	}

	@Override
	public String getIrpfReceiptHTML(final Irpf irpf, int zoom)
			throws IllegalArgumentException {

		try {

			initFacesContext();

			Map<Object, Object> parameters = new HashMap<Object, Object>(
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

			ReportManager reportManager = new ReportManager();
			reportManager.setOutputFormat(OutputFormat.HTML);

			ICollectionProvider provider = new ICollectionProvider() {

				@Override
				public Collection getCollection() {
					try {
						return getCollection(true);
					} catch (ManagerBeanException e) {
						// TODO Auto-generated catch block
						return null;
					}
				}

				@Override
				public Collection getCollection(boolean arg0)
						throws ManagerBeanException {
					return Collections.singletonList(getIrpfOutcome(irpf));
				}

			};

			reportManager.setCollectionProvider(provider);

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			String irpfReport = "irpf2013";
			reportManager.execute(out, irpfReport, parameters);

			return out.toString();

		} catch (ReportException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} finally {
			releaseFacesContext();
		}
	}

	@Override
	public String getSalaryReceiptHTML(Salary salary, int zoom)
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

		String html = getSalaryReceiptHTML(salary, parameters);

		for (Entry<Object, Object> image : images.entrySet()) {
			String name = String.format("%s%s", imagesUri, image.getKey());
			JasperImageServlet.saveImage(name, (byte[]) image.getValue());
		}

		return html;
	}

	@Override
	public String getSalaryReceiptHTML(Cost cost, Salary.Type types [], int zoom)
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

		String html = getSalaryReceiptHTML(cost, types, parameters);

		for (Entry<Object, Object> image : images.entrySet()) {
			String name = String.format("%s%s", imagesUri, image.getKey());
			JasperImageServlet.saveImage(name, (byte[]) image.getValue());
		}

		return html;
	}

	@Override
	public String getCostReceiptHTML(Cost cost, Salary.Type types [], int zoom)
			throws IllegalArgumentException {
		try {
			initFacesContext();

			ReportManager reportManager = new ReportManager();
			reportManager.setOutputFormat(OutputFormat.HTML);
			
			SalaryType salaryTypes [] = new SalaryType[types.length];
			for (int i = 0; i < types.length; i++)
				salaryTypes[i] = SalaryType.values()[types[i].ordinal()];
			
			reportManager.setCollectionProvider(getSalariesProvider(cost, salaryTypes));

			// Really I hate this spaghetti piece of code.
			// For pass 'month' & 'year' to a report, we
			// must put it in a controller ?????.
			SalaryExpenseController controller = (SalaryExpenseController) AonUtil
					.getRegisteredBean(IPayrollConstants.SALARY_EXPENSE_CONTROLLER_NAME);
			controller.setShowSalaryExpenseWindow(false);
			controller.setYear(cost.getYear());
			Month month = Month.getMonthByValue(cost.getMonth());
			controller.setMonth(month);

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			Map<Object, Object> parameters = new HashMap<Object, Object>(
					JR_HTML_EXPORTER_PARAMS);
			parameters
					.put(JRHtmlExporterParameter.ZOOM_RATIO, zoom / 100.00f /*
																			 * not
																			 * round
																			 * to
																			 * int
																			 */);
			reportManager.execute(out, IPayrollConstants.COST_REPORT,
					parameters);

			return out.toString();

		} catch (ReportException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} finally {
			releaseFacesContext();
		}

	}

	@Override
	public String getSalaryDraftReceiptHTML(SalaryDraft salaryDraft, int zoom)
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

		String html = getSalaryDraftReceiptHTML(salaryDraft, parameters);

		for (Entry<Object, Object> image : images.entrySet()) {
			String name = String.format("%s%s", imagesUri, image.getKey());
			JasperImageServlet.saveImage(name, (byte[]) image.getValue());
		}

		return html;

	}

	@Override
	public String getIrpfDraftReceiptHTML(SalaryDraft salaryDraft, int zoom)
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
		String html = getIrpfDraftReceiptHTML(salaryDraft, parameters);

		return html;

	}

	@Override
	public Double eval(String expression, SalaryDraft salaryDraft)
			throws IllegalArgumentException, EvalException {
		try {
			initFacesContext();
			List<ITimedResult<Double>> results = eval(expression, salaryDraft,
					Double.class);
			Double total = 0.00;
			for (ITimedResult<Double> result : results) {
				Double value = result.getValue();
				if (value != null)
					total += value;
			}
			return total;
		} finally {
			releaseFacesContext();
		}
	}

	@Override
	public ContextDescriptor getContext(SalaryDraft salaryDraft) {
		try {
			initFacesContext();
			return getDraftContext(salaryDraft);
		} finally {
			releaseFacesContext();
		}
	}

	@Override
	public SalaryDraft calculateSalaryDraft(SalaryDraft salaryDraft)
			throws IllegalArgumentException {
		try {
			initFacesContext();
			calculate(salaryDraft);
			return salaryDraft;
		} finally {
			releaseFacesContext();
		}

	}

	@Override
	public Map<String, String> getEventsVariables(Integer workplaceId,
			Integer agreementId, Date startDate, Date endDate)
			throws IllegalArgumentException {
		if (agreementId == null)
			return Collections.emptyMap();

		try {
			initFacesContext();
			return getWorkplaceEventsVariables(workplaceId, agreementId,
					startDate, endDate);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			releaseFacesContext();
		}

	}

	@Override
	public AgreementDraft calculateAgreementDraft(AgreementDraft agreementDraft)
			throws IllegalArgumentException {
		try {
			initFacesContext();
			calculate(agreementDraft);
			return agreementDraft;
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			releaseFacesContext();
		}

	}

	@Override
	public void saveAgreementDraft(AgreementDraft agreementDraft)
			throws IllegalArgumentException {
		Connection conn = null;
		try {
			initFacesContext();
			conn = getConnection();
			disableAutoCommit(conn);
			SQLAgreementDraft.save(conn, agreementDraft, getDomainID(),
					getParentDomainID());
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
			releaseFacesContext();
		}

	}

	@Override
	public void saveSalaryDraft(SalaryDraft salaryDraft)
			throws IllegalArgumentException {
		Connection conn = null;
		try {
			initFacesContext();
			conn = getConnection();
			disableAutoCommit(conn);
			SQLSalaryDraft.save(conn, salaryDraft, getDomainID(),
					getParentDomainID());
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
			releaseFacesContext();
		}

	}

	@Override
	public SalaryDraft saveSalary(SalaryDraft salaryDraft)
			throws IllegalArgumentException {
		Connection conn = null;
		try {
			initFacesContext();
			conn = getConnection();
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
			releaseFacesContext();
		}

	}

	@Override
	public String getSalaryDraftReceipt(final SalaryDraft draft, String mime)
			throws IllegalArgumentException {

		try {

			initFacesContext();

			ReportManager reportManager = new ReportManager();
			reportManager.setOutputFormat(OutputFormat.PDF);

			ICollectionProvider provider = new ICollectionProvider() {

				@Override
				public Collection getCollection() {
					try {
						return getCollection(true);
					} catch (ManagerBeanException e) {
						// TODO Auto-generated catch block
						return null;
					}
				}

				@Override
				public Collection getCollection(boolean arg0)
						throws ManagerBeanException {
					return Collections.singletonList(getSalary(draft));
				}

			};

			reportManager.setCollectionProvider(provider);

			ByteArrayOutputStream reportOut = new ByteArrayOutputStream();
			String salaryReport = getSalaryReport();
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
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} finally {
			releaseFacesContext();
		}

	}

	@Override
	public String getIrpfDraftReceipt(final SalaryDraft draft, String mime)
			throws IllegalArgumentException {

		try {

			initFacesContext();

			ReportManager reportManager = new ReportManager();
			reportManager.setOutputFormat(OutputFormat.PDF);

			ICollectionProvider provider = new ICollectionProvider() {

				@Override
				public Collection getCollection() {
					try {
						return getCollection(true);
					} catch (ManagerBeanException e) {
						// TODO Auto-generated catch block
						return null;
					}
				}

				@Override
				public Collection getCollection(boolean arg0)
						throws ManagerBeanException {
					return Collections.singletonList(getIrpfOutcome(draft));
				}

			};

			reportManager.setCollectionProvider(provider);

			ByteArrayOutputStream reportOut = new ByteArrayOutputStream();
			reportManager.execute(reportOut, "irpf2013");

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
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} finally {
			releaseFacesContext();
		}

	}

	@Override
	public String getSalaryPreviewReceiptHTML(SalaryPreview salaryPreview,
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

		String html = getSalaryPreviewReceiptHTML(salaryPreview, parameters);

		for (Entry<Object, Object> image : images.entrySet()) {
			String name = String.format("%s%s", imagesUri, image.getKey());
			JasperImageServlet.saveImage(name, (byte[]) image.getValue());
		}

		return html;
	}

	@Override
	public List<Payment> getAvailablePayments(int employeeId)
			throws IllegalArgumentException {
		Connection conn = null;
		try {
			initFacesContext();

			conn = getConnection();

			int domainId = getDomainID();

			List<Payment> paymentConcepts = getPaymentConcepts(conn, domainId,
					getParentDomainID());
			List<Payment> employeePayments = getEmployeePayments(conn,
					employeeId);
			List<Payment> enterprisePayments = getEnterprisePayments(conn,
					domainId);

			List<Payment> payments = new ArrayList<Payment>(
					paymentConcepts.size() + employeePayments.size()
							+ enterprisePayments.size());

			payments.addAll(paymentConcepts);
			payments.addAll(employeePayments);
			payments.addAll(enterprisePayments);

			return payments;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
			releaseFacesContext();
		}
	}

	@Override
	public void saveEvents(Events events, Date startDate, Date endDate) {
		Connection conn = null;
		try {
			initFacesContext();
			int domainId = getDomainID();
			conn = getConnection();
			SQLEvents.completeEvents(conn, events, startDate, endDate);
			SQLEvents.saveEvents(conn, events, startDate, endDate, domainId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
			releaseFacesContext();
		}
	}

	@Override
	public Events getEvents(Integer workplaceId, Date startDate, Date endDate,
			int offset, int limit, String names[]) {
		Connection conn = null;
		try {
			initFacesContext();
			conn = getConnection();
			return SQLEvents.getEvents(conn, workplaceId, startDate, endDate,
					offset, limit,names);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
			releaseFacesContext();
		}

	}

	@Override
	public Period getAvailPeriod(Integer workplaceId, String name)
			throws IllegalArgumentException {
		Connection conn = null;
		try {
			initFacesContext();
			conn = getConnection();
			return SQLEvents.getAvailPeriod(conn, workplaceId, name);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
			releaseFacesContext();
		}
	}

	@Override
	public void delete(Salary[] salaries) throws IllegalArgumentException {
		Connection conn = null;
		try {
			initFacesContext();
			conn = getConnection();

			int ids[] = new int[salaries.length];
			for (int i = 0; i < salaries.length; i++)
				ids[i] = salaries[i].getId();

			deleteSalaries(conn, ids);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException logOrIgnrore) {
				}
			}
			releaseFacesContext();
		}
	}

	// -------------------------------------------------------- Private methods

	private String getSalaryReport() throws ReportException {
		int enterpriseId = getEnterpriseID();
		Connection conn = null;
		try {
			conn = getConnection();
			return AonServletUtils.getSalaryReport(conn, enterpriseId);
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

	private String getSalaryReceiptHTML(Salary salary,
			Map<Object, Object> parameters) throws IllegalArgumentException {

		try {

			initFacesContext();

			String salaryReport = getSalaryReport();

			IManagerBean beanManager = BeanManager
					.getManagerBean(com.esferalia.aon.payroll.Salary.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(
					beanManager.getFieldName(IEntityAlias.SALARY_ID),
					salary.getId());

			FacesContext ctx = FacesContext.getCurrentInstance();
			ctx.getViewRoot().setLocale(new Locale("es", "ES"));
			ReportManager reportManager = new ReportManager();

			reportManager.setOutputFormat(OutputFormat.HTML);
			reportManager
					.setCollectionProvider(new AonServletUtils.SalaryProvider(
							criteria));

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			reportManager.execute(out, salaryReport, parameters);

			return out.toString();

		} catch (ReportException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} finally {
			releaseFacesContext();
		}

	}

	private String getSalaryReceiptHTML(Cost cost,
			Salary.Type types [], Map<Object, Object> parameters) throws IllegalArgumentException {
		try {

			initFacesContext();

			ReportManager reportManager = new ReportManager();
			reportManager.setOutputFormat(OutputFormat.HTML);

			SalaryType salaryTypes [] = new SalaryType[types.length];
			for (int i = 0; i < types.length; i++)
				salaryTypes[i] = SalaryType.values()[types[i].ordinal()];
			
			reportManager.setCollectionProvider(getSalariesProvider(cost, salaryTypes));

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			String salaryReport = getSalaryReport();
			reportManager.execute(out, salaryReport, parameters);

			return out.toString();

		} catch (ReportException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} finally {
			releaseFacesContext();
		}
	}

	private String getSalaryPreviewReceiptHTML(final SalaryPreview draft,
			Map<Object, Object> parameters) throws IllegalArgumentException {

		try {

			initFacesContext();

			ReportManager reportManager = new ReportManager();
			reportManager.setOutputFormat(OutputFormat.HTML);

			// ISalary salary = getSalary(draft);
			// ICollectionProvider provider = new CollectionProvider(salary);

			ICollectionProvider provider = new ICollectionProvider() {

				@Override
				public Collection getCollection() {
					try {
						return getCollection(true);
					} catch (ManagerBeanException e) {
						// TODO Auto-generated catch block
						return null;
					}
				}

				@Override
				public Collection getCollection(boolean arg0)
						throws ManagerBeanException {
					return Collections.singletonList(getSalary(draft));
				}

			};

			reportManager.setCollectionProvider(provider);

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			String salaryReport = getSalaryReport();
			reportManager.execute(out, salaryReport, parameters);

			return out.toString();

		} catch (ReportException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} finally {
			releaseFacesContext();
		}

	}

	private String getSalaryDraftReceiptHTML(final SalaryDraft draft,
			Map<Object, Object> parameters) throws IllegalArgumentException {

		try {

			initFacesContext();

			ReportManager reportManager = new ReportManager();
			reportManager.setOutputFormat(OutputFormat.HTML);

			ICollectionProvider provider = new ICollectionProvider() {

				@Override
				public Collection getCollection() {
					try {
						return getCollection(true);
					} catch (ManagerBeanException e) {
						// TODO Auto-generated catch block
						return null;
					}
				}

				@Override
				public Collection getCollection(boolean arg0)
						throws ManagerBeanException {
					return Collections.singletonList(getSalary(draft));
				}

			};

			reportManager.setCollectionProvider(provider);

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			String salaryReport = getSalaryReport();
			reportManager.execute(out, salaryReport, parameters);

			return out.toString();

		} catch (ReportException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} finally {
			releaseFacesContext();
		}

	}

	private String getIrpfDraftReceiptHTML(final SalaryDraft draft,
			Map<Object, Object> parameters) throws IllegalArgumentException {

		try {

			initFacesContext();

			FacesContext ctx = FacesContext.getCurrentInstance();
			ctx.getViewRoot().setLocale(new Locale("es", "ES"));

			ReportManager reportManager = new ReportManager();
			reportManager.setOutputFormat(OutputFormat.HTML);

			ICollectionProvider provider = new ICollectionProvider() {

				@Override
				public Collection getCollection() {
					try {
						return getCollection(true);
					} catch (ManagerBeanException e) {
						// TODO Auto-generated catch block
						return null;
					}
				}

				@Override
				public Collection getCollection(boolean arg0)
						throws ManagerBeanException {
					return Collections.singletonList(getIrpfOutcome(draft));
				}

			};

			reportManager.setCollectionProvider(provider);

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			String irpfReport = "irpf2013";
			reportManager.execute(out, irpfReport, parameters);

			return out.toString();

		} catch (ReportException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} finally {
			releaseFacesContext();
		}

	}

	// ------------------------------------------------- Private Static methods

	// Note that below methods can be moved to another place safely.

	private static List<Payment> getPaymentConcepts(Connection connection,
			Integer domainId, Integer parentDomainId) throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {

			String sql = "SELECT " + PAYMENT_CONCEPT + ".* " + " FROM "
					+ PAYMENT_CONCEPT + " WHERE "
					+ PaymentConceptColumns.DOMAIN + " =  ? " + " OR "
					+ PaymentConceptColumns.DOMAIN + " =  ? ";

			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, domainId);
			stmt.setInt(2, parentDomainId != null ? parentDomainId : 0);
			rs = stmt.executeQuery();

			List<Payment> paymentConcepts = new LinkedList<Payment>();
			while (rs.next()) {
				Payment paymentConcept = new Payment();

				paymentConcept.setId(rs.getInt(PaymentConceptColumns.ID));
				paymentConcept
						.setName(rs.getString(PaymentConceptColumns.CODE));
				paymentConcept.setType(getPaymentType(rs
						.getInt(PaymentConceptColumns.TYPE)));
				paymentConcept.setExpression(rs
						.getString(PaymentConceptColumns.EXPRESSION));
				paymentConcept.setIrpfExpression(rs
						.getString(PaymentConceptColumns.IRPF_EXPRESSION));
				paymentConcept.setQuoteExpression(rs
						.getString(PaymentConceptColumns.QUOTE_EXPRESSION));
				paymentConcept.setDescription(rs
						.getString(PaymentConceptColumns.DESCRIPTION));

				paymentConcepts.add(paymentConcept);
			}

			return paymentConcepts;

		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				rs.close();
			}
		}
	}

	private static List<Payment> getEmployeePayments(Connection connection,
			int employeeId) throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {

			String sql = "SELECT *  FROM " + SQLConstants.CONTRACT_PAYMENT
					+ " WHERE " + ContractPaymentColumns.CONTRACT + " =  ? "
					+ " AND " + ContractPaymentColumns.PAYMENT_CONCEPT
					+ " IS NULL ";

			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, employeeId);
			rs = stmt.executeQuery();

			List<Payment> paymentConcepts = new LinkedList<Payment>();
			while (rs.next()) {
				Payment paymentConcept = new Payment();

				paymentConcept.setScope(Scope.CONTRACT);

				paymentConcept.setType(getPaymentType(rs
						.getInt(ContractPaymentColumns.TYPE)));
				paymentConcept.setExpression(rs
						.getString(ContractPaymentColumns.EXPRESSION));
				paymentConcept.setIrpfExpression(rs
						.getString(ContractPaymentColumns.IRPF_EXPRESSION));
				paymentConcept.setQuoteExpression(rs
						.getString(ContractPaymentColumns.QUOTE_EXPRESSION));
				paymentConcept.setDescription(rs
						.getString(ContractPaymentColumns.DESCRIPTION));

				paymentConcepts.add(paymentConcept);
			}

			return paymentConcepts;

		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				rs.close();
			}
		}
	}

	private static List<Payment> getEnterprisePayments(Connection connection,
			int domainId) throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {

			String sql = "SELECT *  FROM " + SQLConstants.CONTRACT_PAYMENT
					+ " WHERE " + ContractPaymentColumns.DOMAIN + " =  ? "
					+ " AND " + ContractPaymentColumns.PAYMENT_CONCEPT
					+ " IS NULL ";

			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, domainId);
			rs = stmt.executeQuery();

			List<Payment> paymentConcepts = new LinkedList<Payment>();
			while (rs.next()) {
				Payment paymentConcept = new Payment();

				paymentConcept.setType(getPaymentType(rs
						.getInt(ContractPaymentColumns.TYPE)));
				paymentConcept.setExpression(rs
						.getString(ContractPaymentColumns.EXPRESSION));
				paymentConcept.setIrpfExpression(rs
						.getString(ContractPaymentColumns.IRPF_EXPRESSION));
				paymentConcept.setQuoteExpression(rs
						.getString(ContractPaymentColumns.QUOTE_EXPRESSION));
				paymentConcept.setDescription(rs
						.getString(ContractPaymentColumns.DESCRIPTION));

				paymentConcepts.add(paymentConcept);
			}

			return paymentConcepts;

		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				rs.close();
			}
		}
	}

	private static ICollectionProvider getSalariesProvider(Cost cost, SalaryType types [])
			throws ManagerBeanException {
		IManagerBean beanManager = BeanManager
				.getManagerBean(com.esferalia.aon.payroll.Salary.class);

		Criteria sqlCriteria = new Criteria();
		Criteria aliasCriteria = new Criteria();

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, cost.getYear());
		calendar.set(Calendar.MONTH, cost.getMonth());
		calendar.set(Calendar.DAY_OF_MONTH, 1); // The first day of the month
												// has value 1.
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		Date startDate = calendar.getTime();

		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date endDate = calendar.getTime();

		if (cost.getWorkplaceId() != 0) {

			sqlCriteria.addEqualExpression(WORKPLACE + "."
					+ WorkplaceColumns.ID, cost.getWorkplaceId());

			aliasCriteria.addEqualExpression(beanManager
					.getFieldName(IEntityAlias.SALARY_CONTRACT_WORK_PLACE_ID),
					cost.getWorkplaceId());
		} else {
			sqlCriteria.addEqualExpression(ENTERPRISE + "."
					+ EnterpriseColumns.REGISTRY, cost.getEnterpriseId());
			aliasCriteria
					.addEqualExpression(
							beanManager
									.getFieldName(IEntityAlias.SALARY_CONTRACT_WORK_PLACE_ENTERPRISE_ID),
							cost.getEnterpriseId());
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

		aliasCriteria.addBetweenExpression(
				beanManager.getFieldName(IEntityAlias.SALARY_CHARGE_DATE),
				startDate, endDate);

		aliasCriteria.addOrder(beanManager
				.getFieldName(IEntityAlias.SALARY_CONTRACT_WORK_PLACE_ID));
		aliasCriteria.addOrder(beanManager
				.getFieldName(IEntityAlias.SALARY_EMPLOYEE_NAME));
		aliasCriteria.addOrder(beanManager
				.getFieldName(IEntityAlias.SALARY_CHARGE_DATE));
		
		aliasCriteria.addInExpression(beanManager
				.getFieldName(IEntityAlias.SALARY_TYPE), types);

		return new AonServletUtils.CalcSalaryProvider(startDate, endDate,
				sqlCriteria, types, new AonServletUtils.SalaryProvider(aliasCriteria));

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

	private static List<Employee> getEmployees(Connection connection,
			Integer workplaceId, Date endDate, String pattern, int offset,
			int limit) throws SQLException,
			com.code.aon.ql.util.ExpressionException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {

			String sql = "SELECT * " + " FROM " + CONTRACT + " ," + PERSON
					+ " WHERE " + ContractColumns.PERSON + " = "
					+ PersonColumns.REGISTRY + " AND "
					+ ContractColumns.WORKPLACE + " = ? " + " AND ( "
					+ ContractColumns.END_DATE + " IS NULL  " + " OR "
					+ ContractColumns.END_DATE + " >= ? )";
			if (pattern != null) {
				/*
				 * Criteria surnameCriteria = new Criteria();
				 * surnameCriteria.addExpression(ExpressionUtilities
				 * .getExpression(pattern, PersonColumns.FIRST_SURNAME)); sql =
				 * CriteriaUtilities.toSQLString(surnameCriteria, sql);
				 */
				sql += " AND CONCAT(" + PersonColumns.FIRST_SURNAME + ","
						+ PersonColumns.SECOND_SURNAME + ","
						+ PersonColumns.NAME + ") LIKE '%" + pattern + "%'";

			}

			sql += " ORDER BY " + PersonColumns.FIRST_SURNAME + " ,"
					+ PersonColumns.SECOND_SURNAME + " ," + PersonColumns.NAME
					+ " ," + ContractColumns.START_DATE + " DESC "
					+ " LIMIT ?, ? ";

			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, workplaceId);

			stmt.setDate(2, new java.sql.Date(endDate.getTime()));

			stmt.setInt(3, offset);
			stmt.setInt(4, limit);

			rs = stmt.executeQuery();

			List<Employee> employees = new LinkedList<Employee>();
			while (rs.next()) {
				Employee employee = new Employee();
				employee.setId(rs.getInt(ContractColumns.ID));
				employee.setStartDate(rs.getDate(ContractColumns.START_DATE));
				employee.setEndDate(rs.getDate(ContractColumns.END_DATE));

				employee.setPerson(rs.getInt(PersonColumns.REGISTRY));
				employee.setName(rs.getString(PersonColumns.NAME));
				employee.setFirstSurname(rs
						.getString(PersonColumns.FIRST_SURNAME));
				employee.setSecondSurName(rs
						.getString(PersonColumns.SECOND_SURNAME));
				employees.add(employee);
			}

			return employees;
		} finally {
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
			String yearCol = "YEAR";
			String monthCol = "MONTH";

			// We asume that one enterprise one domain. This way SELECT it's
			// more clear.
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
					+ ContractColumns.DOMAIN
					+ " = "
					+ ENTERPRISE
					+ "."
					+ EnterpriseColumns.DOMAIN
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

					+ " FROM " + ENTERPRISE + ", " + SALARY + " LEFT JOIN "
					+ SQLConstants.SALARY_DATA + " ON ( " + SQLConstants.SALARY
					+ "." + SalaryColumns.ID + " = " + SQLConstants.SALARY_DATA
					+ "." + SalaryDataColumns.SALARY + " AND "
					+ SQLConstants.SALARY_DATA + "." + SalaryDataColumns.NAME
					+ " =  ? " + ")" + " WHERE " + ENTERPRISE + "."
					+ EnterpriseColumns.DOMAIN + " = " + SALARY + "."
					+ SalaryColumns.DOMAIN + " AND " + ENTERPRISE + "."
					+ EnterpriseColumns.REGISTRY + " = ? " + " GROUP BY 1, 2"
					+ " HAVING SALARIES = VISIBLES AND SALARIES >= CONTRACTS"
					+ " ORDER BY 2 , 1 ASC ";

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

					+ " HAVING SALARIES = VISIBLES AND SALARIES >= CONTRACTS"
					+ " ORDER BY 2 , 1 ASC ";

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
			String sql = "SELECT * " + " FROM " + ENTERPRISE_ACTIVITY
					+ " WHERE " + EnterpriseActivityColumns.ENTERPRISE
					+ " = ? ";

			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, enterpriseId);
			rs = stmt.executeQuery();

			List<Activity> activities = new LinkedList<Activity>();
			while (rs.next()) {

				Activity activity = new Activity();

				activity.setId(rs.getInt(EnterpriseActivityColumns.ID));
				activity.setDescription(rs
						.getString(EnterpriseActivityColumns.DESCRIPTION));

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

	private static Enterprise getEnterprise(Integer registryID, Integer userID,
			Connection connection) throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			String sql = "SELECT * " + " FROM " + REGISTRY + ", " + ENTERPRISE
					+ " LEFT JOIN " + WORKPLACE + " ON ( " + ENTERPRISE + "."
					+ EnterpriseColumns.REGISTRY + " = " + WORKPLACE + "."
					+ WorkplaceColumns.ENTERPRISE + " )" + " LEFT JOIN "
					+ PAYROLL_WORKPLACE + " ON ( " + WORKPLACE + "."
					+ WorkplaceColumns.ID + " = " + PAYROLL_WORKPLACE + "."
					+ PayrollWorkplaceColumns.WORKPLACE + ") LEFT JOIN "
					+ AGREEMENT + " ON ( " + PAYROLL_WORKPLACE + "."
					+ PayrollWorkplaceColumns.AGREEMENT + " = " + AGREEMENT
					+ "." + AgreementColumns.ID + " )" + " WHERE " + REGISTRY
					+ "." + RegistryColumns.ID + " = ?" + " AND " + REGISTRY
					+ "." + RegistryColumns.ID + " = " + ENTERPRISE + "."
					+ EnterpriseColumns.REGISTRY + " ORDER BY " + " UPPER("
					+ WORKPLACE + "." + WorkplaceColumns.DESCRIPTION + " )";

			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, registryID);

			rs = stmt.executeQuery();

			EnterpriseHandler enterpriseHandler = new EnterpriseHandler();

			WorkplaceHandler workplaceHandler = new WorkplaceHandler(
					enterpriseHandler);

			groups(rs, enterpriseHandler, workplaceHandler);

			Enterprise enterprise = enterpriseHandler.getEnterprise();

			List<Activity> activities = getEnterpriseActivities(connection,
					enterprise.getId());
			enterprise.setActivities(activities);

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

	private static ISalary getDBSalary(SalaryDraft salaryDraft)
			throws ManagerBeanException {

		IManagerBean beanManager = BeanManager
				.getManagerBean(com.esferalia.aon.payroll.Salary.class);

		Criteria criteria = new Criteria();

		criteria.addEqualExpression(
				beanManager.getFieldName(IEntityAlias.SALARY_CONTRACT_ID),
				salaryDraft.getEmployee().getId());
		criteria.addEqualExpression(
				beanManager.getFieldName(IEntityAlias.SALARY_TYPE),
				salaryDraft.getType());
		criteria.addEqualExpression(
				beanManager.getFieldName(IEntityAlias.SALARY_START_DATE),
				salaryDraft.getStartDate());
		criteria.addEqualExpression(
				beanManager.getFieldName(IEntityAlias.SALARY_END_DATE),
				salaryDraft.getEndDate());

		List<ITransferObject> list = beanManager.getList(criteria);

		return list.size() > 0 ? (ISalary) list.get(0) : null;
	}

	private static List<Salary> getSalaries(Connection connection,
			Integer enterpriseID, Integer personId, Date toDate)
			throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {

			String sql = "SELECT " + SALARY + ".* " + " FROM " + SALARY + ", "
					+ CONTRACT + ", " + WORKPLACE + " WHERE " + SALARY + "."
					+ SalaryColumns.CONTRACT + " = " + CONTRACT + "."
					+ ContractColumns.ID + " AND " + CONTRACT + "."
					+ ContractColumns.WORKPLACE + " = " + WORKPLACE + "."
					+ WorkplaceColumns.ID + " AND " + CONTRACT + "."
					+ ContractColumns.PERSON + " = ? " + " AND " + WORKPLACE
					+ "." + WorkplaceColumns.ENTERPRISE + " = ? " + " AND "
					+ SALARY + "." + SalaryColumns.CHARGE_DATE + " <= ? "
					+ " ORDER BY " + SALARY + "." + SalaryColumns.END_DATE
					+ " ASC ";

			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, personId);
			stmt.setInt(2, enterpriseID);
			stmt.setDate(3, new java.sql.Date(toDate.getTime()));
			rs = stmt.executeQuery();

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
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				rs.close();
			}
		}
	}

	private static void calculate(SalaryDraft draft) {

		SalaryDraftBuilder salaryBuilder = new SalaryDraftBuilder(draft);
		calculate(draft, salaryBuilder, salaryBuilder);

		try {
			ISalary dbSalary = getDBSalary(draft);
			if (dbSalary != null)
				salaryBuilder.setDbSalary(dbSalary);
		} catch (SalaryException e) {
		} catch (ManagerBeanException e) {
		}

	}

	private static Map<String, String> getWorkplaceEventsVariables(
			Integer workplaceId, Integer agreementId, Date startDate,
			Date endDate) throws SQLException {
		Connection connection = null;
		try {
			connection = getConnection();

			Set<Payment> payments = SQLEvents.getPayments(connection,
					workplaceId, startDate, endDate);

			if (agreementId != null) {
				payments.addAll(SQLAgreementDraft.getPayments(connection,
						agreementId, startDate, endDate));
			}

			Map<String, String> variables = new HashMap<String, String>();

			for (Payment payment : payments) {

				if (StringUtils.equals("REMOVE()", payment.getExpression()))
					continue;

				Set<String> paymentVars = ExpressionContext
						.getVariableSet(payment.getExpression());

				for (String var : paymentVars) {
					if (var.endsWith("_ACTUAL"))
						continue; // This is awfull ... very awful
					variables.put(var, String.format("%s",
							payment.getDescription(), payment.getExpression()));
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

			Set<Level> levels = SQLAgreementDraft.getLevels(connection,
					agreementId);

			SalaryTable salaryTable = SQLAgreementDraft.getSalaryTable(
					connection, agreementId, startDate, endDate);

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

	private static void calculate(AgreementDraft draft) throws SQLException {
		Connection connection = null;
		try {
			connection = getConnection();

			Set<Payment> dbPayments = SQLAgreementDraft.getPayments(connection,
					draft.getId(), draft.getStartDate(), draft.getEndDate());

			Collection<Payment> allPayments = new CompositeItems<Payment>(
					draft.getDraftPayments(), dbPayments);
			Set<String> variables = new HashSet<String>();

			Set<Payment> payments = new HashSet<Payment>();
			for (Payment payment : allPayments) {

				if (StringUtils.equals("REMOVE()", payment.getExpression()))
					continue;

				variables.addAll(ExpressionContext.getVariableSet(
						payment.getExpression(), payment.getIrpfExpression(),
						payment.getQuoteExpression()));
				variables.remove(payment.getName());

				payments.add(payment);
			}

			// Filter ContextVariable
			List<String> contextVariables = new LinkedList<String>();
			for (ContextVariable ctxVar : ContextVariable.values())
				contextVariables.add(ctxVar.getName());
			variables.removeAll(contextVariables);

			// This is awfull ... very awful
			List<String> privateVariables = new LinkedList<String>();
			for (String var : variables) {
				if (var.endsWith("_ACTUAL"))
					privateVariables.add(var);
			}
			variables.removeAll(privateVariables);

			// Clean system variables.
			Set<String> systemVars = getSystemVariables(connection,
					draft.getStartDate(), draft.getEndDate());
			variables.removeAll(systemVars);

			Set<Level> dbLevels = SQLAgreementDraft.getLevels(connection,
					draft.getId());
			Set<Level> draftLevels = draft.getDraftLevels();

			Set<Level> levels = new HashSet<Level>(draftLevels);
			levels.addAll(dbLevels);

			Map<Integer, Set<String>> categories = SQLAgreementDraft
					.getCategories(connection, draft.getId());

			Level agreementData = new Level();
			agreementData.setId(0);
			levels.add(agreementData);

			SalaryTable salaryTable = SQLAgreementDraft.getSalaryTable(
					connection, draft.getId(), draft.getStartDate(),
					draft.getEndDate());

			draft.setLevels(levels);
			draft.setVariables(variables);
			draft.setPayments(payments);
			draft.setSalaryTable(salaryTable);
			draft.setCategoriesMap(categories);

		} finally {
			if (connection != null)
				connection.close();
		}
	}

	private static void calculateAndSave(Connection conn, SalaryDraft draft)
			throws SQLException {
		SQLSalaryDraft.removeSalary(conn, draft);

		SQLSalaryBuilder sqlSalaryBuilder = new SQLSalaryBuilder(conn);
		sqlSalaryBuilder.setListener(new SalaryBuilderListener());
		SalaryDraftBuilder salaryDraftBuilder = new SalaryDraftBuilder(draft);
		CompositeSalaryBuilder compositeSalaryBuilder = new CompositeSalaryBuilder(
				salaryDraftBuilder, sqlSalaryBuilder);
		sqlSalaryBuilder.begin();
		calculate(draft, compositeSalaryBuilder, salaryDraftBuilder);
		sqlSalaryBuilder.commit();

		try {
			ISalary dbSalary = getDBSalary(draft);
			if (dbSalary != null)
				salaryDraftBuilder.setDbSalary(dbSalary);
		} catch (SalaryException e) {
		} catch (ManagerBeanException e) {
		}

	}

	private static <T extends ISalaryBuilder, L extends SalaryDraftBuilder> void calculate(
			SalaryDraft draft, T salaryBuilder, L draftBuilder) {

		ContractSalaryCalculator calculator = new ContractSalaryCalculator();

		calculator.setSalaryBuilder(salaryBuilder);
		calculator.setListener(draftBuilder);

		Connection conn = null;
		IContractSalaryCalculatorContext ctx;
		try {
			conn = getConnection();
			ctx = getSalaryCalculatorContext(conn, draft, draftBuilder);
			draftBuilder.setDefined(getDefinedMap(ctx));
			calculator.calculate(ctx);
		} catch (ExpressionException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} catch (SalaryException e) {
			e.printStackTrace();
			// TODO Auto-generated catch block
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

	private static Map<String, boolean[]> getDefinedMap(
			IContractSalaryCalculatorContext ctx) {
		Map<String, boolean[]> definedMap = new HashMap<String, boolean[]>();

		Date startDate = ctx.getStartDate();
		Date endDate = ctx.getEndDate();

		for (String name : ctx.getSystemExpressionContext().variablesSet()) {
			boolean defined[] = new boolean[Scope.NUM_VALUES];
			defined[Scope.SYSTEM.ordinal()] = true;
			definedMap.put(name, defined);
		}
		// Agreement
		ExpressionContext agreementCtx = ctx.getAgreementExpressionContext();
		for (String name : agreementCtx.variablesSet()) {
			ITimedVariable<?> var = agreementCtx.getVariable(name, startDate,
					endDate);
			if (!(var instanceof IExpressionVariable<?>))
				continue;
			ExpressionScope scope = ((IExpressionVariable<?>) var)
					.getExpression().getScope();
			if (scope != ExpressionScope.AGREEMENT)
				continue;

			boolean defined[] = definedMap.get(name);
			if (defined == null) {
				defined = new boolean[Scope.NUM_VALUES];
				definedMap.put(name, defined);
			}
			defined[Scope.AGREEMENT.ordinal()] = true;
		}

		ExpressionContext implicitCtx = ctx.getImplicitExpressionContext();
		for (String name : ctx.getImplicitExpressionContext().variablesSet()) {

			ITimedVariable<?> var = agreementCtx.getVariable(name, startDate,
					endDate);
			if (var instanceof IExpressionVariable<?>)
				continue;
			// Implicit variables don't come from expression

			boolean defined[] = definedMap.get(name);
			if (defined == null) {
				defined = new boolean[Scope.NUM_VALUES];
				definedMap.put(name, defined);
			}
			defined[Scope.APPLICATION.ordinal()] = true;
		}

		return definedMap;
	}

	private static <T> List<ITimedResult<T>> eval(String expression,
			SalaryDraft draft, Class<T> toType) throws EvalException {
		Connection conn = null;

		try {
			conn = getConnection();
			ISalaryCalculatorContext ctx = getSalaryCalculatorContext(conn,
					draft, null);
			return ctx.getExpressionContext().eval(expression,
					ctx.getStartDate(), ctx.getEndDate(), toType);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} catch (InvalidVariables e) {
			throw new EvalWarning(e.getMessage());
		} catch (CheckException e) {
			throw new EvalWarning(e.getMessage());
		} catch (RemoveVariableError e) {
			return Collections.emptyList();
		} catch (UndefinedVariablesException e) {
			throw new EvalWarning(e.getMessage());
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

	private static ContextDescriptor getDraftContext(SalaryDraft draft) {
		Connection conn = null;
		try {
			conn = getConnection();

			ISalaryCalculatorContext calculatorCtx = getSalaryCalculatorContext(
					conn, draft, null);
			ExpressionContext expressionContext = calculatorCtx
					.getExpressionContext();

			Date start = calculatorCtx.getStartDate();
			Date end = calculatorCtx.getEndDate();

			ContextDescriptor contextDescriptor = new ContextDescriptor();

			Map<String, String> descriptions = getSystemDescriptions(conn,
					start, end);

			Set<String> varNames = expressionContext.variablesSet();

			for (String varName : varNames) {
				Object value = null;
				try {
					value = expressionContext.getVariable(varName, start, end,
							Object.class);
				} catch (Throwable e) {

				}
				if (value == null)
					continue;

				String description = null;

				ContextVariable ctxVar = ContextVariable
						.getVariableByName(varName);

				if (ctxVar != null) {
					description = ctxVar.getDescription(new Locale("es", "ES"));
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
					if (ContextDescriptor.isKnownType(type))
						contextDescriptor.add(varName, description, type,
								value.toString());
				}

			}

			return contextDescriptor;

		} catch (ExpressionException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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

		SalaryBuilder salaryBuilder = new SalaryBuilder();

		ContractSalaryCalculator calculator = new ContractSalaryCalculator();

		calculator.setSalaryBuilder(salaryBuilder);

		Connection conn = null;
		ISalaryCalculatorContext ctx;
		try {
			conn = getConnection();
			ctx = getSalaryCalculatorContext(conn, draft, null);
			com.esferalia.aon.payroll.Salary salary = (com.esferalia.aon.payroll.Salary) calculator
					.calculate(ctx);

			Contract contract = AonServletUtils.getContract(draft.getEmployee()
					.getId());

			salary.setContract(contract);

			// TODO: Calendar ???
			salary.setIssueYear(ctx.getIssueDate().getYear());
			salary.setIssueMonth(ctx.getIssueDate().getMonth());

			return salary;
		} catch (ExpressionException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} catch (SalaryException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
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

	private static com.esferalia.aon.payroll.IrpfOutcome getIrpfOutcome(
			SalaryDraft draft) {
		Connection conn = null;
		try {
			conn = getConnection();
			Calendar endCalendar = Calendar.getInstance();
			endCalendar.setTime(draft.getStartDate());
			endCalendar.set(Calendar.DAY_OF_YEAR,
					endCalendar.getActualMaximum(Calendar.DAY_OF_YEAR));
			Date endYear = endCalendar.getTime();

			Criteria contractCriteria = new Criteria();
			contractCriteria.addEqualExpression(SQLConstants.CONTRACT + "."
					+ ContractColumns.ID, draft.getEmployee().getId());

			IIrpfCalculatorContext irpfCalculatorContext = getIrpfCalculatorContext(
					draft, conn, draft.getStartDate(), endYear,
					contractCriteria);

			return IrpfCalculator.calculateIrpf(irpfCalculatorContext);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
		try {
			IrpfOutcome irpfOutcome = new IrpfOutcome();

			IManagerBean irpfResultManagerBean = BeanManager
					.getManagerBean(com.esferalia.aon.payroll.IrpfResult.class);
			Criteria irpfResultCriteria = new Criteria();
			irpfResultCriteria.addEqualExpression(irpfResultManagerBean
					.getFieldName(IEntityAlias.IRPF_RESULT_ID), irpf
					.getIrpfResult().getId());
			List<ITransferObject> irpfResults = irpfResultManagerBean
					.getList(irpfResultCriteria);
			irpfOutcome
					.setIrpfResult((com.esferalia.aon.payroll.IrpfResult) irpfResults
							.get(0));

			IManagerBean irpfDataManagerBean = BeanManager
					.getManagerBean(com.esferalia.aon.payroll.IrpfData.class);

			Criteria irpfDataCriteria = new Criteria();
			irpfDataCriteria
					.addEqualExpression(irpfDataManagerBean
							.getFieldName(IEntityAlias.IRPF_DATA_ID), irpf
							.getIrpfData().getId());
			List<ITransferObject> irpfDatas = irpfDataManagerBean
					.getList(irpfDataCriteria);
			com.esferalia.aon.payroll.IrpfData irpfData = (com.esferalia.aon.payroll.IrpfData) irpfDatas
					.get(0);
			irpfOutcome.setIrpfData(irpfData);

			Person person = irpfData.getContract().getPerson();
			Registry registry = irpfData.getContract().getPerson()
					.getRegistry();

			irpfOutcome.setNif(registry.getDocument());
			Date birthDate = person.getBirthDate();
			if (birthDate != null) {
				irpfOutcome.setBirthYear(birthDate.getYear());
			}

			if (irpf.getIrpfRegularization() == null)
				return irpfOutcome;

			IManagerBean irpfRegularizationManagerBean = BeanManager
					.getManagerBean(com.esferalia.aon.payroll.IrpfRegularization.class);
			Criteria irpfRegularizationCriteria = new Criteria();
			irpfRegularizationCriteria.addEqualExpression(
					irpfRegularizationManagerBean
							.getFieldName(IEntityAlias.IRPF_REGULARIZATION_ID),
					irpf.getIrpfRegularization().getId());
			List<ITransferObject> irpfRegularizations = irpfRegularizationManagerBean
					.getList(irpfRegularizationCriteria);
			irpfOutcome
					.setIrpfRegularization((com.esferalia.aon.payroll.IrpfRegularization) irpfRegularizations
							.get(0));

			return irpfOutcome;
		} catch (ManagerBeanException e) {
			throw new IllegalArgumentException(e);
		}

	}

	private static IIrpfCalculatorContext getIrpfCalculatorContext(
			SalaryDraft draft, Connection conn, Date startDate, Date endDate,
			Criteria criteria) {
		try {
			SQLContractSalaryCalculatorContext sqlContractSalaryCalculatorCtx = new SQLContractSalaryCalculatorContext(
					conn, startDate, endDate, endDate, criteria) {

				@Override
				protected double getIrpf() {
					return 0.00;
				}

			};

			SQLSalaryDraftCalculatorContext sqlDraftSalaryCalculatorCtx = new SQLSalaryDraftCalculatorContext(
					draft, sqlContractSalaryCalculatorCtx);

			return new SQLIrpfCalculatorContext(conn, startDate, endDate,
					sqlDraftSalaryCalculatorCtx) {
				@Override
				public String getNif() {
					String nif = super.getNif();
					return IrpfCalculator.isValidNif(nif) ? nif
							: IrpfCalculator.DEFAULT_NIF;
				}

				@Override
				public String getApellidosNombre() {
					return "TORVALDS BENEDICT LINUS";
				}

				@Override
				public String getRetenedorNif() {
					String nif = super.getRetenedorNif();
					return IrpfCalculator.isValidNif(nif) ? nif
							: IrpfCalculator.DEFAULT_CIF;
				}

				@Override
				public String getRetenedorApellidosNombre() {
					return "LINUX FOUNDATION";
				}
			};
		} catch (SQLException e) {
			throw new ExpressionExceptionWrapper(new ExpressionException(e));
		} catch (ExpressionException e) {
			throw new ExpressionExceptionWrapper(e);
		}

	}

	private static IContractSalaryCalculatorContext getSalaryCalculatorContext(
			final Connection conn, final SalaryDraft draft,
			final IContractSalaryCalculatorContext.IListener listener)
			throws ExpressionException, SQLException {

		SalaryType salaryType = SalaryType.values()[draft.getType().ordinal()];
		return salaryType
				.accept(new SalaryTypeVisitor<IContractSalaryCalculatorContext>() {
					@Override
					public IContractSalaryCalculatorContext visitSalary(
							SalaryType salaryType) {
						try {
							return getSalaryCalculatorContextImpl(conn, draft,
									listener);
						} catch (SQLException e) {
							throw new IllegalArgumentException(e);
						} catch (ExpressionException e) {
							throw new ExpressionExceptionWrapper(e);
						}
					}

					@Override
					public IContractSalaryCalculatorContext visitDelay(
							SalaryType salaryType) {
						try {
							return getDelayCalculatorContextImpl(conn, draft,
									listener);
						} catch (SQLException e) {
							throw new IllegalArgumentException(e);
						} catch (ExpressionException e) {
							throw new ExpressionExceptionWrapper(e);
						}
					}

					@Override
					public IContractSalaryCalculatorContext visitSettle(
							SalaryType salaryType) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public IContractSalaryCalculatorContext visitExtra(
							SalaryType salaryType) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public IContractSalaryCalculatorContext visitNotEnjoyedVacations(
							SalaryType salaryType) {
						// TODO Auto-generated method stub
						return null;
					}
				});
	}

	private static IContractSalaryCalculatorContext getSalaryCalculatorContextImpl(
			final Connection conn, final SalaryDraft draft,
			IContractSalaryCalculatorContext.IListener listener)
			throws ExpressionException, SQLException {

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(tableCol(CONTRACT, ContractColumns.ID),
				draft.getEmployee().getId());

		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				conn, draft.getStartDate(), draft.getEndDate(),
				draft.getIssueDate(), criteria) {
			@Override
			protected IIrpfCalculatorContext getIrpfCalculatorContext(
					Connection conn, Date startDate, Date endDate,
					Criteria criteria) {
				try {
					SQLContractSalaryCalculatorContext sqlContractSalaryCalculatorCtx = new SQLContractSalaryCalculatorContext(
							conn, startDate, endDate, endDate, criteria) {

						@Override
						protected double getIrpf() {
							return 0.00;
						}

					};

					SQLSalaryDraftCalculatorContext sqlDraftSalaryCalculatorCtx = new SQLSalaryDraftCalculatorContext(
							draft, sqlContractSalaryCalculatorCtx);

					return new SQLIrpfCalculatorContext(conn, startDate,
							endDate, sqlDraftSalaryCalculatorCtx) {
						@Override
						public String getNif() {
							return "87449445H";
						}

						@Override
						public String getApellidosNombre() {
							return "TORVALDS BENEDICT LINUS";
						}

						@Override
						public String getRetenedorNif() {
							return "Z7896423E";
						}

						@Override
						public String getRetenedorApellidosNombre() {
							return "LINUX FOUNDATION";
						}
					};
				} catch (SQLException e) {
					throw new ExpressionExceptionWrapper(
							new ExpressionException(e));
				} catch (ExpressionException e) {
					throw new ExpressionExceptionWrapper(e);
				}
			}

			@Override
			protected ISalaryCalculatorContext getLiquidCalculatorContext(
					Connection conn, Date startDate, Date endDate,
					Date issueDate, Criteria criteria, final double x) {
				try {
					SQLContractSalaryCalculatorContext sqlContractSalaryCalculatorCtx = new SQLContractSalaryCalculatorContext(
							conn, startDate, endDate, issueDate, criteria) {
						@Override
						public Object liquid(double liquid)
								throws ExpressionException, SQLException {
							return x;
						}

						@Override
						protected IIrpfCalculatorContext getIrpfCalculatorContext(
								Connection conn, Date startDate, Date endDate,
								Criteria criteria) {
							try {
								SQLContractSalaryCalculatorContext sqlContractSalaryCalculatorCtx = new SQLContractSalaryCalculatorContext(
										conn, startDate, endDate, endDate,
										criteria) {

									@Override
									protected double getIrpf() {
										return 0.00;
									}

									@Override
									public Object liquid(double liquid)
											throws ExpressionException,
											SQLException {
										return x;
									}

								};

								SQLSalaryDraftCalculatorContext sqlDraftSalaryCalculatorCtx = new SQLSalaryDraftCalculatorContext(
										draft, sqlContractSalaryCalculatorCtx);

								return new SQLIrpfCalculatorContext(conn,
										startDate, endDate,
										sqlDraftSalaryCalculatorCtx) {
									@Override
									public String getNif() {
										return "87449445H";
									}

									@Override
									public String getApellidosNombre() {
										return "TORVALDS BENEDICT LINUS";
									}

									@Override
									public String getRetenedorNif() {
										return "Z7896423E";
									}

									@Override
									public String getRetenedorApellidosNombre() {
										return "LINUX FOUNDATION";
									}
								};
							} catch (SQLException e) {
								throw new ExpressionExceptionWrapper(
										new ExpressionException(e));
							} catch (ExpressionException e) {
								throw new ExpressionExceptionWrapper(e);
							}
						}

					};
					SQLSalaryDraftCalculatorContext sqlDraftSalaryCalculatorCtx = new SQLSalaryDraftCalculatorContext(
							draft, sqlContractSalaryCalculatorCtx);

					sqlDraftSalaryCalculatorCtx.next();
					return sqlDraftSalaryCalculatorCtx;

				} catch (ExpressionException e) {
					throw new ExpressionExceptionWrapper(e);
				} catch (SQLException e) {
					throw new ExpressionExceptionWrapper(
							new ExpressionException(e));
				}
			}
		};

		ctx.setListener(listener);
		ctx.next();

		SalaryDraftCalculatorContext<IContractSalaryCalculatorContext> draftCtx = new SalaryDraftCalculatorContext<IContractSalaryCalculatorContext>(
				draft, ctx);
		draftCtx.setListener(listener);
		return draftCtx;
	}

	private static IContractSalaryCalculatorContext getDelayCalculatorContextImpl(
			final Connection conn, final SalaryDraft draft,
			IContractSalaryCalculatorContext.IListener listener)
			throws ExpressionException, SQLException {

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(tableCol(CONTRACT, ContractColumns.ID),
				draft.getEmployee().getId());

		SQLContractSalaryCalculatorContext ctx = new SQLContractDelayCalculatorContext(
				conn, draft.getStartDate(), draft.getEndDate(),
				draft.getIssueDate(), criteria) {

		};

		ctx.setListener(listener);
		ctx.next();

		SalaryDraftCalculatorContext<IContractSalaryCalculatorContext> draftCtx = new SalaryDraftCalculatorContext<IContractSalaryCalculatorContext>(
				draft, ctx);
		draftCtx.setListener(listener);
		return draftCtx;
	}

	private static ISalary getSalary(SalaryPreview draft) {
		SalaryBuilder salaryBuilder = new SalaryBuilder();

		ContractSalaryCalculator calculator = new ContractSalaryCalculator();

		calculator.setSalaryBuilder(salaryBuilder);

		Connection conn = null;

		IContractSalaryCalculatorContext ctx;
		try {
			conn = getConnection();
			ctx = getSQLContractSalaryCalculatorContext(conn, draft);
			com.esferalia.aon.payroll.Salary salary = (com.esferalia.aon.payroll.Salary) calculator
					.calculate(ctx);

			Contract contract = AonServletUtils.getContract(draft.getEmployee()
					.getId());

			salary.setContract(contract);

			return salary;
		} catch (ExpressionException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} catch (SalaryException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
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

	private static Payment.Type getPaymentType(Integer ordinal) {
		if (ordinal == null || ordinal < 0) {
			return null;
		}

		Payment.Type types[] = Payment.Type.values();

		if (ordinal >= types.length) {
			return null;
		}

		return types[ordinal];
	}

	private static String tableCol(String table, String col) {
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

			Object agreementId = rs.getObject(tableCol(PAYROLL_WORKPLACE,
					PayrollWorkplaceColumns.AGREEMENT));
			if (agreementId != null) {
				Agreement agreement = new Agreement();
				agreement.setId((Integer) agreementId);
				agreement.setDescription(rs.getString(tableCol(AGREEMENT,
						AgreementColumns.DESCRIPTION)));
				workplace.setAgreement(agreement);
			}

			enterpriseHandler.getEnterprise().addWorkplace(workplace);
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

}
