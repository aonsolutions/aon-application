package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.gwt.payroll.server.AonServletUtils.commit;
import static com.esferalia.aon.gwt.payroll.server.AonServletUtils.disableAutoCommit;
import static com.esferalia.aon.gwt.payroll.server.AonServletUtils.enableAutoCommit;
import static com.esferalia.aon.gwt.payroll.server.AonServletUtils.getConnection;
import static com.esferalia.aon.gwt.payroll.server.AonServletUtils.rollback;
import static com.esferalia.aon.payroll.sql.SQLConstants.CONTRACT;
import static com.esferalia.aon.payroll.sql.SQLConstants.ENTERPRISE;
import static com.esferalia.aon.payroll.sql.SQLConstants.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.payroll.sql.SQLConstants.PAYMENT_CONCEPT;
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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.mvel2.CompileException;
import org.mvel2.ast.Function;
import org.mvel2.util.MethodStub;

import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.common.enumeration.Month;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.report.OutputFormat;
import com.code.aon.report.ReportException;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.gwt.payroll.client.EmployeesService;
import com.esferalia.aon.gwt.payroll.shared.Activity;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.EvalException;
import com.esferalia.aon.gwt.payroll.shared.EvalSyntaxErrorException;
import com.esferalia.aon.gwt.payroll.shared.EvalWarning;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.gwt.payroll.shared.SalaryPreview;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.sql.SQLSalaryDraft;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractPaymentColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseActivityColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PaymentConceptColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PersonColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RegistryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SystemDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.WorkplaceColumns;
import com.esferalia.aon.salary.CompositeSalaryBuilder;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.SalaryBuilderListener;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.expression.CheckException;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.InvalidVariables;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;
import com.esferalia.aon.salary.expression.ExpressionContext.RemoveVariableError;
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

	public Enterprise getEnterprise() throws IllegalArgumentException {
		try {
			initFacesContext();
			Integer registryID = getEnterpriseID();
			Connection connection = getConnection();
			return getEnterprise(registryID, connection);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		} finally {
			releaseFacesContext();
		}
	}

	public List<Salary> getSalaries(Employee employee)
			throws IllegalArgumentException {
		try {
			initFacesContext();
			Connection connection = getConnection();
			if (employee != null) {
				return getSalaries(connection, employee.getId());
			} else {
				Integer personId = getPersonID();
				Integer enterpriseId = getEnterpriseID();
				Date maxChargeDate = Calendar.getInstance().getTime();
				return getSalaries(connection, enterpriseId, personId,
						maxChargeDate);
			}
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			releaseFacesContext();
		}
	}

	public List<Employee> getEmployees(int workplaceId, Date fromDate,
			String pattern, int offset, int limit)
			throws IllegalArgumentException {
		try {
			initFacesContext();
			Connection connection = getConnection();
			return getEmployees(connection, workplaceId, fromDate, pattern,
					offset, limit);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} catch (com.code.aon.ql.util.ExpressionException e) {
			throw new IllegalArgumentException(e);
		} finally {
			releaseFacesContext();
		}
	}

	public List<Cost> getWorkplaceCosts(int workplaceId)
			throws IllegalArgumentException {
		try {
			initFacesContext();
			Connection connection = getConnection();
			return getWorkplaceCosts(connection, workplaceId);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			releaseFacesContext();
		}
	}

	public List<Cost> getEnterpriseCosts(int enterpriseId)
			throws IllegalArgumentException {
		try {
			initFacesContext();
			Connection connection = getConnection();
			return getEnterpriseCosts(connection, enterpriseId);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			releaseFacesContext();
		}
	}

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

	public String getSalaryReceiptHTML(Cost cost, int zoom)
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

		String html = getSalaryReceiptHTML(cost, parameters);

		for (Entry<Object, Object> image : images.entrySet()) {
			String name = String.format("%s%s", imagesUri, image.getKey());
			JasperImageServlet.saveImage(name, (byte[]) image.getValue());
		}

		return html;
	}

	public String getSalaryReceiptHTML(Salary salary,
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

	public String getSalaryReceiptHTML(Cost cost, Map<Object, Object> parameters)
			throws IllegalArgumentException {
		try {

			initFacesContext();

			ReportManager reportManager = new ReportManager();
			reportManager.setOutputFormat(OutputFormat.HTML);
			reportManager.setCollectionProvider(getSalariesProvider(cost));

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

	public String getCostReceiptHTML(Cost cost, int zoom)
			throws IllegalArgumentException {
		try {
			initFacesContext();

			ReportManager reportManager = new ReportManager();
			reportManager.setOutputFormat(OutputFormat.HTML);

			reportManager.setCollectionProvider(getSalariesProvider(cost));

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
	public Double eval(String expression, SalaryDraft salaryDraft)
			throws IllegalArgumentException, EvalException {
		try {
			initFacesContext();
			List<ITimedResult<Double>> results = eval(expression, salaryDraft,
					Double.class);
			Double total = 0.00;
			for (ITimedResult<Double> result : results) {
				Double value = result.getValue();
				if (value != null )
					total += value;
			}
			return total;
		} finally {
			releaseFacesContext();
		}
	}

	public ContextDescriptor getContext(SalaryDraft salaryDraft) {
		try {
			initFacesContext();
			return getDraftContext(salaryDraft);
		} finally {
			releaseFacesContext();
		}
	}

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

	public void saveSalaryDraft(SalaryDraft salaryDraft)
			throws IllegalArgumentException {
		Connection connection = null;
		try {
			initFacesContext();
			connection = getConnection();
			disableAutoCommit(connection);
			SQLSalaryDraft.save(connection, salaryDraft, getDomainID(),
					getParentDomainID());
			commit(connection);
		} catch (SQLException e) {
			rollback(connection);
			throw new IllegalArgumentException(e);
		} finally {
			enableAutoCommit(connection);
			releaseFacesContext();
		}

	}

	public SalaryDraft saveSalary(SalaryDraft salaryDraft)
			throws IllegalArgumentException {
		Connection connection = null;
		try {
			initFacesContext();
			connection = getConnection();
			calculateAndSave(connection, salaryDraft);
			return salaryDraft;
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			releaseFacesContext();
		}

	}

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

	public String getSalaryPreviewReceiptHTML(final SalaryPreview draft,
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

	public String getSalaryDraftReceiptHTML(final SalaryDraft draft,
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

	public List<Payment> getAvailablePayments(int employeeId)
			throws IllegalArgumentException {

		try {
			initFacesContext();

			Connection conn = getConnection();

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
			releaseFacesContext();
		}
	}

	// -----------------------------------------
	// Private members...
	// -----------------------------------------

	private String getSalaryReport() throws ReportException {
		int enterpriseId = getEnterpriseID();
		Connection connection = getConnection();
		try {
			return AonServletUtils.getSalaryReport(connection, enterpriseId);
		} catch (SQLException e) {
			throw new ReportException(e.getLocalizedMessage());
		}
	}

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

	private static ICollectionProvider getSalariesProvider(Cost cost)
			throws ManagerBeanException {
		IManagerBean beanManager = BeanManager
				.getManagerBean(com.esferalia.aon.payroll.Salary.class);
		Criteria criteria = new Criteria();

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
			criteria.addEqualExpression(beanManager
					.getFieldName(IEntityAlias.SALARY_CONTRACT_WORK_PLACE_ID),
					cost.getWorkplaceId());
		} else {
			criteria.addEqualExpression(
					beanManager
							.getFieldName(IEntityAlias.SALARY_CONTRACT_WORK_PLACE_ENTERPRISE_ID),
					cost.getEnterpriseId());
		}

		criteria.addBetweenExpression(
				beanManager.getFieldName(IEntityAlias.SALARY_END_DATE),
				startDate, endDate);

		criteria.addOrder(beanManager
				.getFieldName(IEntityAlias.SALARY_CONTRACT_WORK_PLACE_ID));
		criteria.addOrder(beanManager
				.getFieldName(IEntityAlias.SALARY_EMPLOYEE_NAME));

		return new AonServletUtils.SalaryProvider(criteria);
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
				Criteria surnameCriteria = new Criteria();
				surnameCriteria.addExpression(ExpressionUtilities
						.getExpression(pattern, PersonColumns.FIRST_SURNAME));
				sql = CriteriaUtilities.toSQLString(surnameCriteria, sql);
			}

			sql += " ORDER BY " + PersonColumns.FIRST_SURNAME + " ,"
					+ PersonColumns.SECOND_SURNAME + " ,"
					+ ContractColumns.START_DATE + " DESC " + " LIMIT ?, ? ";

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
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				rs.close();
			}
		}
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
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				rs.close();
			}
		}
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

	private static Enterprise getEnterprise(Integer registryID,
			Connection connection) throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			String sql = "SELECT * " + " FROM " + REGISTRY + ", " + ENTERPRISE
					+ " LEFT JOIN " + WORKPLACE + " ON ( " + ENTERPRISE + "."
					+ EnterpriseColumns.REGISTRY + " = " + WORKPLACE + "."
					+ WorkplaceColumns.ENTERPRISE + " )" + " WHERE " + REGISTRY
					+ "." + RegistryColumns.ID + " = ?" + " AND " + REGISTRY
					+ "." + RegistryColumns.ID + " = " + ENTERPRISE + "."
					+ EnterpriseColumns.REGISTRY;

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

	private static void calculate(SalaryDraft draft,
			ISalaryBuilder salaryBuilder,
			ContractSalaryCalculator.IListener listener) {

		ContractSalaryCalculator calculator = new ContractSalaryCalculator();

		calculator.setSalaryBuilder(salaryBuilder);
		calculator.setListener(listener);

		ISalaryCalculatorContext ctx;
		try {

			ctx = getSalaryCalculatorContext(draft);
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
		}
	}

	private static <T> List<ITimedResult<T>> eval(String expression,
			SalaryDraft draft, Class<T> toType) throws EvalException {
		try {

			ISalaryCalculatorContext ctx = getSalaryCalculatorContext(draft);
			return ctx.getExpressionContext().eval(expression,
					ctx.getStartDate(), ctx.getEndDate(), toType);
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		}catch ( InvalidVariables e ) {
			throw new EvalWarning(e.getMessage());
		}catch ( CheckException e ) {
			throw new EvalWarning(e.getMessage());
		}catch ( RemoveVariableError e ){
			return Collections.emptyList();
		}catch ( UndefinedVariablesException e ) {
			throw new EvalWarning(e.getMessage());
		}catch ( CompileException e ) {
			throw new EvalSyntaxErrorException(e.getMessage());
		}catch (ExpressionException e) {
			throw new IllegalArgumentException(e.getMessage());
		} 

	}

	private static ContextDescriptor getDraftContext(SalaryDraft draft) {
		try {
			ISalaryCalculatorContext calculatorCtx = getSalaryCalculatorContext(draft);
			ExpressionContext expressionContext = calculatorCtx
					.getExpressionContext();

			Date start = calculatorCtx.getStartDate();
			Date end = calculatorCtx.getEndDate();

			ContextDescriptor contextDescriptor = new ContextDescriptor();

			Map<String, String> descriptions = getSystemDescriptions(
					getConnection(), start, end);

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
					if ( ContextDescriptor.isKnownType(type))
						contextDescriptor.add(varName, description,
								type, value.toString());
				}

			}

			return contextDescriptor;

		} catch (ExpressionException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		}
	}

	private static ISalary getSalary(SalaryDraft draft) {

		SalaryBuilder salaryBuilder = new SalaryBuilder();

		ContractSalaryCalculator calculator = new ContractSalaryCalculator();

		calculator.setSalaryBuilder(salaryBuilder);

		ISalaryCalculatorContext ctx;
		try {
			ctx = getSalaryCalculatorContext(draft);
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
		}
	}

	private static ISalaryCalculatorContext getSalaryCalculatorContext(
			SalaryDraft draft) throws ExpressionException, SQLException {
		IContractSalaryCalculatorContext ctx = getSalaryCalculatorContext((SalaryPreview) draft);
		SalaryDraftCalculatorContext draftCtx = new SalaryDraftCalculatorContext(
				draft, ctx);

		return draftCtx;
	}

	private static ISalary getSalary(SalaryPreview draft) {
		SalaryBuilder salaryBuilder = new SalaryBuilder();

		ContractSalaryCalculator calculator = new ContractSalaryCalculator();

		calculator.setSalaryBuilder(salaryBuilder);

		ISalaryCalculatorContext ctx;
		try {
			ctx = getSalaryCalculatorContext(draft);
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
		}

	}

	private static IContractSalaryCalculatorContext getSalaryCalculatorContext(
			SalaryPreview preview) throws ExpressionException, SQLException {

		Connection connection = getConnection();

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(tableCol(CONTRACT, ContractColumns.ID),
				preview.getEmployee().getId());

		Date startDate = preview.getStartDate();
		Date endDate = preview.getEndDate();
		Date issueDate = preview.getIssueDate();

		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, criteria);
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

			enterpriseHandler.getEnterprise().addWorkplace(workplace);
		}

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

}
