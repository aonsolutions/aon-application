package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS;
import static com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SalaryEmbargo.SALARY_EMBARGO;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext.PERSON_REGISTRY;

import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.annotation.WebServlet;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Order;
import com.code.aon.ql.OrderByList;
import com.code.aon.ql.ast.IdentExpression;
import com.code.aon.ql.ast.RelationalExpression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.shared.CalculateService;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.RoundSalaryBuilder;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLExtraSalaryCalculatorContext;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.WorkplaceColumns;
import com.esferalia.aon.salary.AbstractSalary;
import com.esferalia.aon.salary.CompositeSalaryBuilder;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.enumeration.SalaryTypeVisitor;
import com.esferalia.aon.salary.expression.ExpressionException;

@WebServlet(
		name = "CalculateServlet", 
		urlPatterns = { 
				"/aon_gwt_aio/calculate", 
				"/aon_gwt_payroll/calculate" 
		}
)
public class CalculateServlet extends HttpServlet implements CalculateService {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_PATTERN);

	private static class SkipSalaryException extends SalaryException {

	}

	private static class CalculatorListener extends SmartContractSalaryCalculator.Listener{
		
		List<String> errors = new ArrayList<String>();
		
		@Override
		public void onCheckError(String message) {
			errors.add(message);
		}
		
		@Override
		public void onCheckError(IContractBonus bonus, String message) {
			errors.add(message);
		}

		@Override
		public void onCheckError(IContractPayment payment, String message) {
			errors.add(message);
		}
	}

	private static class SalaryBBuilder {

		public static interface ISalaryBuilderExtended<T extends ISalary> extends ISalaryBuilder<T> {

			public void start() throws SalaryException;

			public void finish() throws SalaryException;

			public void counterpart(ISQLSalary salary) throws SalaryException;

		}

		static class CompositeSalaryBuilderExtended<T extends ISalary>
				extends CompositeSalaryBuilder<T, ISalaryBuilderExtended<T>> implements ISalaryBuilderExtended<T> {
			public CompositeSalaryBuilderExtended(ISalaryBuilderExtended<T>... builders) {
				super(builders);
			}

			// ----------------------------------------------------------------

			@Override
			public void start() throws SalaryException {
				for (ISalaryBuilderExtended<T> builder : getBuilders())
					builder.start();
			}

			@Override
			public void finish() throws SalaryException {
				for (ISalaryBuilderExtended<T> builder : getBuilders())
					builder.finish();
			}

			@Override
			public void counterpart(ISQLSalary salary) throws SalaryException {
				for (ISalaryBuilderExtended<T> builder : getBuilders())
					builder.counterpart(salary);
			}
		}

		static class SalaryBuilderExtended extends SalaryBuilder implements ISalaryBuilderExtended<Salary> {

			// ----------------------------------------------------------------
			@Override
			public void start() throws SalaryException {
			}

			@Override
			public void finish() throws SalaryException {
			}

			@Override
			public void counterpart(ISQLSalary salary) throws SalaryException {
			}

		}

		static class RoundSalaryBuilderExtended<T extends ISalary> extends RoundSalaryBuilder<T>
				implements ISalaryBuilderExtended<T> {

			public RoundSalaryBuilderExtended(ISalaryBuilderExtended<T> salaryBuilder, Function<Double, Double> f) {
				super(salaryBuilder, f);
			}

			@Override
			public void start() throws SalaryException {
				((ISalaryBuilderExtended<T>) salaryBuilder).start();

			}

			@Override
			public void finish() throws SalaryException {
				((ISalaryBuilderExtended<T>) salaryBuilder).finish();

			}

			@Override
			public void counterpart(ISQLSalary salary) throws SalaryException {
				((ISalaryBuilderExtended<T>) salaryBuilder).counterpart(salary);
			}

		}

		static class JooqSalarySaver<T extends ISalary> extends JooqSalaryBuilder<T>
				implements ISalaryBuilderExtended<T> {

			private boolean autoCommit;
			private Connection connection;

			public JooqSalarySaver(Connection connection) {
				super(connection);
				this.connection = connection;
			}

			// ----------------------------------------------------------------
			@Override
			public void start() {
				try {
					autoCommit = connection.getAutoCommit();
					connection.setAutoCommit(false);
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public void finish() {
				try {
					execute();
					connection.commit();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					try {
						connection.setAutoCommit(autoCommit);
					} catch (SQLException e) {
						throw new RuntimeException(e);
					}
					;
				}
			}

			@Override
			public void counterpart(ISQLSalary salary) throws SalaryException {
				if (salary != null)
					throw new SkipSalaryException();

			}
		}

		static class JooqSalaryDuplicator extends JooqSalarySaver {

			public JooqSalaryDuplicator(Connection connection) {
				super(connection);
			}

			// ----------------------------------------------------------------

			@Override
			public void counterpart(ISQLSalary salary) throws SalaryException {
				// NOOP
			}
		}

		static class JooqSalaryOverwriter extends JooqSalaryDuplicator {

			Collection<Integer> toRemove;

			public JooqSalaryOverwriter(Connection connection) {
				super(connection);
				toRemove = new LinkedList<Integer>();
			}

			// ----------------------------------------------------------------

			@Override
			public int execute() {
				delete();
				return super.execute();
			}

			// ----------------------------------------------------------------
			@Override
			public void counterpart(ISQLSalary salary) throws SalaryException {
				if (salary != null)
					toRemove.add(salary.getInt(SQLConstants.SALARY, SalaryColumns.ID));
			}

			private void delete() {
				getDSLContext().delete(SALARY_DATA).where(SALARY_DATA.SALARY.in(toRemove)).execute();
				getDSLContext().delete(SALARY_COST).where(SALARY_COST.SALARY.in(toRemove)).execute();
				getDSLContext().delete(SALARY_BONUS).where(SALARY_BONUS.SALARY.in(toRemove)).execute();
				getDSLContext().delete(SALARY_PAYMENT).where(SALARY_PAYMENT.SALARY.in(toRemove)).execute();
				getDSLContext().delete(SALARY_DEDUCTION).where(SALARY_DEDUCTION.SALARY.in(toRemove)).execute();
				getDSLContext().delete(SALARY_EMBARGO).where(SALARY_EMBARGO.SALARY.in(toRemove)).execute();
				getDSLContext().delete(SALARY).where(SALARY.ID.in(toRemove)).execute();
			}
		}

		private boolean save;
		private boolean duplicate;
		private boolean overwrite;
		private Connection connection;

		public ISalaryBuilderExtended<?> build() throws SQLException {
			List<ISalaryBuilderExtended<?>> builders = new ArrayList<ISalaryBuilderExtended<?>>();

			builders.add(new SalaryBuilderExtended());

			if (overwrite) {
				builders.add(new JooqSalaryOverwriter(connection));
			} else if (duplicate) {
				builders.add(new JooqSalaryDuplicator(connection));
			} else if (save) {
				builders.add(new RoundSalaryBuilderExtended<Salary>(new JooqSalarySaver<Salary>(connection),
						d -> Math.round(d * 1000.00) / 1000.00));
			}

			return new CompositeSalaryBuilderExtended(builders.toArray(new ISalaryBuilderExtended[builders.size()]));
		}

		public SalaryBBuilder setSave(boolean save) {
			this.save = save;
			return this;
		}

		public SalaryBBuilder setDuplicate(boolean duplicate) {
			this.duplicate = duplicate;
			return this;
		}

		public SalaryBBuilder setOverwrite(boolean overwrite) {
			this.overwrite = overwrite;
			return this;
		}

		public SalaryBBuilder setConnection(Connection connection) {
			this.connection = connection;
			return this;
		}

	}

	static interface ISQLSalary extends ISalary {
		public Integer getInt(String table, String column);
	}

	static class AbstractSQLSalary extends AbstractSalary implements ISQLSalary {
		public Integer getInt(String table, String column) {
			return null;
		}
	}

	private static class Salaries implements Iterator<ISQLSalary> {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		private boolean didNext = false;
		private boolean hasNext = false;

		public Salaries(Connection connection, Date startDate, Date endDate, Criteria criteria) throws SQLException {

			// @formatter:off
			String sql = "SELECT * " + " FROM contract" + " INNER JOIN registry AS " + PERSON_REGISTRY
					+ " ON (contract.person = " + PERSON_REGISTRY + ".id)"
					+ " INNER JOIN workplace ON (contract.workplace = workplace.id)"
					+ " INNER JOIN enterprise ON ( workplace.enterprise = enterprise.registry) "
					+ " LEFT JOIN salary ON (salary.contract = contract.id" + " AND salary.start_date >= ? "
					+ " AND salary.end_date <= ?" + " AND salary.type = ? )" + " WHERE contract.start_date <= ? "
					+ " AND ( contract.end_date  IS NULL" + " OR contract.end_date >= ? )";
			// @formatter:on

			sql = CriteriaUtilities.toSQLString(criteria, sql);

			java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
			java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());

			stmt = connection.prepareStatement(sql);

			stmt.setDate(1, sqlStartDate);
			stmt.setDate(2, sqlEndDate);
			stmt.setInt(3, SalaryType.SALARY.ordinal());
			stmt.setDate(4, sqlEndDate);
			stmt.setDate(5, sqlStartDate);

			rs = stmt.executeQuery();

		}

		public Integer getInt(String table, String col) {
			try {
				Number number = ((Number) rs.getObject(table + "." + col));
				return number != null ? number.intValue() : null;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		public void close() throws SQLException {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}

		private boolean hasSalary() {
			try {
				return rs.getObject(SQLConstants.SALARY + "." + SalaryColumns.ID) != null;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		// ----------------------------------------------------------------

		@Override
		public ISQLSalary next() {
			try {
				if (!didNext)
					hasNext = rs.next();
				didNext = false;
				return hasSalary() ? nextSQLSalary() : null;
			} catch (SQLException e) {
				throw new NoSuchElementException();
			}
		}

		@Override
		public boolean hasNext() {
			try {
				if (!didNext) {
					hasNext = rs.next();
					didNext = true;
				}
				return hasNext;
			} catch (SQLException e) {
				return false;
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		private ISQLSalary nextSQLSalary() {

			return new AbstractSQLSalary() {

				// ----------------------------------------------------------------
				Map<String, Object> data = new HashMap<String, Object>() {
					{
						try {
							ResultSetMetaData rsMetaData = rs.getMetaData();
							for (int i = 1; i <= rsMetaData.getColumnCount(); i++)
								put(rsMetaData.getTableName(i) + "." + rsMetaData.getColumnName(i), rs.getObject(i));
						} catch (SQLException e) {
							throw new RuntimeException(e);
						}
					}
				};

				@Override
				public Integer getInt(String table, String col) {
					Number number = ((Number) data.get(table + "." + col));
					return number != null ? number.intValue() : null;
				}

				// ----------------------------------------------------------------
				@Override
				public Double getTotalPayment() {
					return getSalaryDouble(SalaryColumns.TOTAL_PAYMENT);
				}

				@Override
				public Double getTotalDeduction() {
					return getSalaryDouble(SalaryColumns.TOTAL_DEDUCTION);
				}

				@Override
				public Double getTotalLiquid() {
					return getSalaryDouble(SalaryColumns.TOTAL_LIQUID);
				}

				// ----------------------------------------------------------------
				private Double getSalaryDouble(String column) {
					Number number = (Number) data.get(SQLConstants.SALARY + "." + column);
					return number != null ? number.doubleValue() : null;
				}
			};
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			doJson(req, resp);
		} catch (SQLException e) {
			throw new ServletException(e);
		} catch (ParseException e) {
			throw new ServletException(e);
		}

	}

	private void doJson(HttpServletRequest req, HttpServletResponse resp)
			throws ParseException, SQLException, IOException {
		PrintStream os = null;
		Salaries salaries = null;
		String domain = req.getServerName();
		Connection conn = AonServletUtils.getConnection(domain);
		try {

			resp.setContentType("application/json;charset=UTF-8");


			SalaryBBuilder.ISalaryBuilderExtended salaryBuilder = new SalaryBBuilder().setSave(save(req))
					.setOverwrite(overwrite(req)).setDuplicate(duplicate(req)).setConnection(conn).build();

			os = new PrintStream(resp.getOutputStream(), false, "UTF-8");

			Date endDate = getEndDate(req);
			Date startDate = getStartDate(req);
			Date issueDate = getIssueDate(req);
			Criteria criteria = getCriteria(req);

			criteria.addOrder(SQLConstants.WORKPLACE + "." + SQLConstants.WorkplaceColumns.ID);
			criteria.addOrder(SQLConstants.CONTRACT + "." + SQLConstants.ContractColumns.ID);

			SalaryType salaryType = getSalaryType(req);
			ISQLContractSalaryCalculatorContext ctx =  
			salaryType.accept(new SalaryTypeVisitor<ISQLContractSalaryCalculatorContext>() {

				@Override
				public ISQLContractSalaryCalculatorContext visitSalary(SalaryType salaryType) {
					try {
						return new SQLContractSalaryCalculatorContext(conn, startDate, endDate,
								issueDate, criteria);
					} catch (ExpressionException | SQLException e) {
						throw new RuntimeException(e);
					}
				}

				@Override
				public ISQLContractSalaryCalculatorContext visitExtra(SalaryType salaryType) {
					try {
						int extra = getExtra(req);
						Calendar issueCalendar = Calendar.getInstance();
						issueCalendar.setTime(issueDate);
						int year = issueCalendar.get(Calendar.YEAR);
						return new SQLExtraSalaryCalculatorContext(conn,
								extra,
								year,
								issueDate, 
								criteria);
					} catch (ParseException | SQLException e) {
						throw new RuntimeException(e);
					}
				}

				@Override
				public ISQLContractSalaryCalculatorContext visitSettle(SalaryType salaryType) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public ISQLContractSalaryCalculatorContext visitDelay(SalaryType salaryType) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public ISQLContractSalaryCalculatorContext visitNotEnjoyedVacations(SalaryType salaryType) {
					// TODO Auto-generated method stub
					return null;
				}
			});
			
//			ISQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(conn, startDate, endDate,
//					issueDate, criteria);

			Date endCheckDate = getEndCheckDate(req);
			Date startCheckDate = getStartCheckDate(req);

			salaries = new Salaries(conn, startCheckDate, endCheckDate, criteria);

			doJson(ctx, salaries, salaryBuilder, os, criteria.getOrderByList());

		} finally {
			conn.close();
			if (os != null)
				os.flush();
			if (salaries != null)
				salaries.close();

		}

	}

	// ------------------------------------------------------------------------

	private static void doJson(ISQLContractSalaryCalculatorContext sqlCtx, Salaries others,
			SalaryBBuilder.ISalaryBuilderExtended<ISalary> salaryBuilder, PrintStream os, OrderByList orderBy)
					throws IOException {

		try {

			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();

			calculator.setSalaryBuilder(salaryBuilder);
//			calculator.setListener();

			salaryBuilder.start();
			ISQLSalary other = null;
			while (sqlCtx.next()) {

				while (others.hasNext() && (compare(sqlCtx, others, orderBy) >= 0))
					other = others.next();

				try {
					salaryBuilder.counterpart(other);
				} catch (SkipSalaryException e) {
					continue;
				}

				os.print('{');
				os.printf("\"startDate\":\"%1$tY-%1$tm-%1$td\"", sqlCtx.getStartDate());
				os.printf(",\"endDate\":\"%1$tY-%1$tm-%1$td\"", sqlCtx.getEndDate());
				os.printf(",\"employeeId\":\"%d\"", sqlCtx.getInt(SQLConstants.CONTRACT, ContractColumns.ID));
				os.printf(",\"employeeName\":\"%s\"", sqlCtx.getEmployeeName());
				os.printf(",\"enterpriseId\":\"%s\"",
						sqlCtx.getInt(SQLConstants.ENTERPRISE, EnterpriseColumns.REGISTRY));
				os.printf(",\"enterpriseName\":\"%s\"", sqlCtx.getEnterpriseName());
				os.printf(",\"workplaceId\":\"%s\"", sqlCtx.getInt(SQLConstants.WORKPLACE, WorkplaceColumns.ID));
				os.printf(",\"workplaceName\":\"%s\"",
						sqlCtx.getString(SQLConstants.WORKPLACE, WorkplaceColumns.DESCRIPTION));

				try {

					ISalary salary = calculator.calculate(sqlCtx);

					os.printf(",\"totalLiquid\":\"%s\"", Double.toString(salary.getTotalLiquid()));
					os.printf(",\"totalPayment\":\"%s\"", Double.toString(salary.getTotalPayment()));
					os.printf(",\"totalDeduction\":\"%s\"", Double.toString(salary.getTotalDeduction()));

					if (other != null && counter(sqlCtx, other)) {
						os.printf(",\"counterTotalLiquid\":\"%s\"", Double.toString(other.getTotalLiquid()));
						os.printf(",\"counterTotalPayment\":\"%s\"", Double.toString(other.getTotalPayment()));
						os.printf(",\"counterTotalDeduction\":\"%s\"", Double.toString(other.getTotalDeduction()));
					}
					
					os.printf(",\"errors\":[");
					
					os.printf("]");
					

				} catch (Throwable e) {
					e.printStackTrace();
				} finally {
					os.print('}');
					os.flush();
				}
			}
			salaryBuilder.finish();

		} catch (SQLException exception) {
		} catch (SalaryException exception) {
		} catch (ExpressionException exception) {
		} finally {
		}

	}

	private static int compare(ISQLContractSalaryCalculatorContext ctx, Salaries salaries, OrderByList orderBy) {

		int compare = 0;
		for (Order order : orderBy.getOrders()) {
			IdentExpression expression = order.getExpression();
			String strings[] = expression.getName().split("\\.");
			String table = strings[0];
			String column = strings[1];
			compare = ctx.getInt(table, column) - salaries.getInt(table, column);
			if (compare != 0)
				break;
		}
		return compare;
	}

	private static boolean counter(ISQLContractSalaryCalculatorContext ctx, ISQLSalary salary) {
		return ctx.getInt(SQLConstants.CONTRACT, ContractColumns.ID)
				.equals(salary.getInt(SQLConstants.CONTRACT, ContractColumns.ID));
	}

	private static boolean save(HttpServletRequest request) throws ParseException {
		return getBoolean(request, SAVE);
	}

	private static Integer getExtra(HttpServletRequest request) throws ParseException {
		String extra = request.getParameter(EXTRA);
		return StringUtils.isBlank(extra) ? null : Integer.parseInt(extra);
	}


	private static Date getEndDate(HttpServletRequest request) throws ParseException {
		return getDate(request, END_DATE);
	}

	private static Date getStartDate(HttpServletRequest request) throws ParseException {
		return getDate(request, START_DATE);
	}

	private static Date getIssueDate(HttpServletRequest request) throws ParseException {
		return getDate(request, ISSUE_DATE);
	}

	private static Date getEndCheckDate(HttpServletRequest request) throws ParseException {
		return getDate(request, END_CHECK_DATE);
	}

	private static Date getStartCheckDate(HttpServletRequest request) throws ParseException {
		return getDate(request, START_CHECK_DATE);
	}

	private static SalaryType getSalaryType(HttpServletRequest request) throws ParseException {
		String value = request.getParameter(SALARY_TYPE);
		return StringUtils.isBlank(value) ? SalaryType.SALARY : SalaryType.valueOf(value);
	}

	private static Date getDate(HttpServletRequest request, String name) throws ParseException {
		String value = request.getParameter(name);
		return value != null ? DATE_FORMAT.parse(value) : null;

	}

	private static boolean getBoolean(HttpServletRequest request, String name) throws ParseException {
		String value = request.getParameter(name);
		return StringUtils.isBlank(value) ? false : Boolean.parseBoolean(value);

	}

	private static String getString(HttpServletRequest request, String name) throws ParseException {
		return request.getParameter(name);
	}

	private static boolean duplicate(HttpServletRequest request) throws ParseException {
		return getBoolean(request, DUPLICATE);
	}

	private static boolean overwrite(HttpServletRequest request) throws ParseException {
		return getBoolean(request, OVERWRITE);
	}

	private static Criteria getCriteria(HttpServletRequest req) {
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

		// criteria.addOrder(PERSON_REGISTRY + "." + RegistryColumns.NAME);

		return criteria;
	}

	private static RelationalExpression getEnperprisesExpression(HttpServletRequest request) {
		String enterprises[] = request.getParameterValues(ENPERPRISES);
		return enterprises != null && enterprises.length > 0 ? ExpressionUtilities.getInExpression(
				SQLConstants.ENTERPRISE + "." + EnterpriseColumns.REGISTRY, Arrays.asList(enterprises)) : null;
	}

	private static RelationalExpression getWorkPlacesExpression(HttpServletRequest request) {
		String workplaces[] = request.getParameterValues(WORKPLACES);
		return workplaces != null && workplaces.length > 0 ? ExpressionUtilities
				.getInExpression(SQLConstants.WORKPLACE + "." + WorkplaceColumns.ID, Arrays.asList(workplaces)) : null;
	}

	private static RelationalExpression getEmployeesExpression(HttpServletRequest request) {
		String employees[] = request.getParameterValues(EMPLOYEES);
		return employees != null && employees.length > 0 ? ExpressionUtilities
				.getInExpression(SQLConstants.CONTRACT + "." + ContractColumns.ID, Arrays.asList(employees)) : null;
	}

}
