package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.RelationalExpression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.gwt.payroll.shared.CalculateService;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator.IListener;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLSalaryBuilder;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.WorkplaceColumns;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.ISalaryBuilderListener;
import com.esferalia.aon.salary.expression.ExpressionContext.RemovedExpressionVariable;
import com.esferalia.aon.salary.expression.ExpressionException;

public class CalculateServlet extends HttpServlet implements CalculateService {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			DATE_FORMAT_PATTERN);

	private static final DecimalFormatSymbols ES_DECIMAL_FOMAT_SYMBOLS = new DecimalFormatSymbols(
			new Locale("es", "ES"));
	private static final DecimalFormat SECONDS_FORMAT = new DecimalFormat(
			"#,##0.000", ES_DECIMAL_FOMAT_SYMBOLS);
	private static final DecimalFormat INTEGER_FORMAT = new DecimalFormat(
			"#,###", ES_DECIMAL_FOMAT_SYMBOLS);
	private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat(
			"#,##0.00", ES_DECIMAL_FOMAT_SYMBOLS);

	private static class SalaryBuilderListener implements
			ISalaryBuilderListener, IListener {

		private int row = 0;
		private PrintWriter out;

		public SalaryBuilderListener(PrintWriter out) {
			this.out = out;
		}

		private String getRowStyle() {
			return row++ % 2 == 0 ? "aon-dataTable-row-even"
					: "aon-dataTable-row-odd";
		}

		private void printMsg(String html, String styles) {
			if (html != null)
				out.printf("<div class='%s aon-iCon aon-bold %s'>%s</div>",
						styles, getRowStyle(), html);
		}

		// --------------------------------------
		// ISalaryBuilderListener
		// --------------------------------------
		@Override
		public boolean isDebugEnabled() {
			return true;
		}

		@Override
		public void onError(String msg) {
			printMsg(msg, "aon-icon-wrong");
		}

		@Override
		public void onWarning(String msg) {
			printMsg(msg, "aon-icon-errorwarning");
		}

		@Override
		public void onInfo(String msg) {
			printMsg(msg, "aon-icon-info");
		}

		@Override
		public void onDebug(String msg) {
			out.printf("<div class='aon-iCon %s' >%s</div>", getRowStyle(),
					msg);
		}

		// --------------------------------------
		// IListener
		// --------------------------------------
		@Override
		public void onCheckError(String message) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onInvalidData(String variableName, String message) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCompileError(String variableName, String message) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCheckError(IContractPayment payment, String message) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCompileError(IContractPayment payment, String message) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUndefinedData(IContractPayment payment,
				RemovedExpressionVariable<?> var) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onInvalidData(IContractPayment payment,
				String variableName, String message) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUndefinedData(IContractPayment payment,
				String variableName, String message) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCheckError(IContractDeduction deduction, String message) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCompileError(IContractDeduction deduction, String message) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUndefinedData(IContractDeduction deduction,
				RemovedExpressionVariable<?> var) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onInvalidData(IContractDeduction deduction,
				String variableName, String message) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUndefinedData(IContractDeduction deduction,
				String variableName, String message) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCheckError(IContractBonus bonus, String message) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCompileError(IContractBonus bonus, String message) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onInvalidData(IContractBonus bonus, String variableName,
				String message) {
			// TODO Auto-generated method stub

		}

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		long salaries = 0;
		long startTimeMillis = System.currentTimeMillis();

		SalaryBuilderListener listener = new SalaryBuilderListener(
				resp.getWriter());
		Connection connection = null;
		try {

			boolean save = getSave(req);

			Date startDate = getStartDate(req);
			Date endDate = getEndDate(req);
			Date issueDate = getIssueDate(req);

			listener.onInfo("Calculando n&oacute;minas para el periodo de liquidaci&oacute;n : <span class='aon-input-required'>"
					+ DATE_FORMAT.format(startDate)
					+ " - "
					+ DATE_FORMAT.format(endDate) + "</span>.");

			RelationalExpression employeesExpr = getEmployeesExpression(req);
			RelationalExpression workPlacesExpr = getWorkPlacesExpression(req);
			RelationalExpression enterprisesExpr = getEnperprisesExpression(req);

			Criteria criteria = new Criteria();
			if (employeesExpr != null)
				criteria.addExpression(employeesExpr);
			if (workPlacesExpr != null)
				criteria.addExpression(workPlacesExpr);
			if (enterprisesExpr != null)
				criteria.addExpression(enterprisesExpr);

			ServletContext ctx = getServletContext();
			AonServletUtils.initFacesContext(ctx, req, resp);

			connection = AonServletUtils.getConnection();

			SQLContractSalaryCalculatorContext sqlContractSalaryCalculatorContext = new SQLContractSalaryCalculatorContext(
					connection, startDate, endDate, issueDate, criteria);

			ContractSalaryCalculator calculator = new ContractSalaryCalculator();

			ISalaryBuilder salaryBuilder = null;
			if (save) {
				salaryBuilder = new SQLSalaryBuilder(connection);
				((SQLSalaryBuilder) salaryBuilder).begin();
			} else {
				salaryBuilder = new SalaryBuilder();
			}

			salaryBuilder.setListener(listener);

			calculator.setSalaryBuilder(salaryBuilder);
			calculator.setListener(listener);
			
			long total_calc = 0; 
			while (sqlContractSalaryCalculatorContext.next()) {
				try {
					long start_calc = System.currentTimeMillis();
					ISalary salary = calculator
							.calculate(sqlContractSalaryCalculatorContext);
					long end_calc = System.currentTimeMillis();
					total_calc += end_calc - start_calc;
//					employeeId = sqlContractSalaryCalculatorContext.getId();
/*					listener.onDebug(String
							.format("Calculada n&oacute;mina de <a class='aon-icon-employee aon-iCon aon-link aon-input-required' onclick='showEmployee(%d)' >&nbsp;%s</a>."
									+ " L&iacute;quido total a percibir <a class='aon-icon-draft aon-iCon aon-link aon-input-required' onclick='showSalaryDraft(%d,\"%s\",\"%s\")' >&nbsp;%s</a>",
									employeeId, salary.getEmployeeName(), 
									employeeId, DATE_FORMAT.format(startDate), DATE_FORMAT.format(endDate), CURRENCY_FORMAT.format(salary.getTotalLiquid())));
*/
					salaries++;
				} catch (Exception e) {
					listener.onError(e.getMessage());
				}
			}

			if (save) {
				((SQLSalaryBuilder) salaryBuilder).commit();
			}
			
			System.out.printf("Calculate Time : %d ms \r\n", total_calc );

		} catch (SQLException exception) {
			exception.printStackTrace();
			listener.onError(exception.getMessage());

		} catch (ParseException exception) {
			listener.onError(exception.getMessage());
		} catch (ExpressionException exception) {
			listener.onError(exception.getMessage());
		} finally {
			AonServletUtils.releaseFacesContext();
			long endTimeMillis = System.currentTimeMillis();
			double elapsedTime = (endTimeMillis - startTimeMillis) / 1000.00;
			listener.onInfo("N&oacute;minas procesadas <span class='aon-input-required' > "
					+ INTEGER_FORMAT.format(salaries)
					+ " </span>. Tiempo transcurrido: <span class='aon-input-required' >"
					+ SECONDS_FORMAT.format(elapsedTime) + " segundos</span>.");
			if ( connection != null ){
				try {
					connection.close();
				} catch ( SQLException logOrIgnore ){
					
				}
			}
				
		}

	}

	private static boolean getSave(HttpServletRequest request)
			throws ParseException {
		return request.getParameter(SAVE) != null;
	}

	private static Date getEndDate(HttpServletRequest request)
			throws ParseException {
		return getDate(request, END_DATE);
	}

	private static Date getStartDate(HttpServletRequest request)
			throws ParseException {
		return getDate(request, START_DATE);
	}

	private static Date getIssueDate(HttpServletRequest request)
			throws ParseException {
		return getDate(request, ISSUE_DATE);
	}

	private static Date getDate(HttpServletRequest request, String name)
			throws ParseException {
		String value = request.getParameter(name);
		return value != null ? DATE_FORMAT.parse(value) : null;

	}

	private static RelationalExpression getEnperprisesExpression(
			HttpServletRequest request) {
		String enterprises[] = request.getParameterValues(ENPERPRISES);
		return enterprises != null && enterprises.length > 0 ? ExpressionUtilities
				.getInExpression(SQLConstants.ENTERPRISE + "."
						+ EnterpriseColumns.REGISTRY,
						Arrays.asList(enterprises)) : null;
	}

	private static RelationalExpression getWorkPlacesExpression(
			HttpServletRequest request) {
		String workplaces[] = request.getParameterValues(WORKPLACES);
		return workplaces != null && workplaces.length > 0 ? ExpressionUtilities
				.getInExpression(SQLConstants.WORKPLACE + "."
						+ WorkplaceColumns.ID, Arrays.asList(workplaces))
				: null;
	}

	private static RelationalExpression getEmployeesExpression(
			HttpServletRequest request) {
		String employees[] = request.getParameterValues(EMPLOYEES);
		return employees != null && employees.length > 0 ? ExpressionUtilities
				.getInExpression(SQLConstants.CONTRACT + "."
						+ ContractColumns.ID, Arrays.asList(employees)) : null;
	}

}
