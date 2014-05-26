package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.ACTUAL_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.AGE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.AGREEMENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ASSIMILATED;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.BONUS_AGE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.BONUS_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.BONUS_START;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.BR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CONTEXT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CONTRACT_END;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CONTRACT_START;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.DELAY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.END;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.EXTRA_PAY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FEMALE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FULL_TIME;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.GENDER;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.GROSS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.GUARANTEE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.GUARANTEED;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.GUARANTEED_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.HOLIDAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.IMS_RATE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.INDEFINITE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.IRPF_PERCENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.IT_RATE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.LEAVE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.LIQUID;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MALE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MORE_THAN_65;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PAY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PAY_MONTHS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PAY_WEEKS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SALARY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SALARY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SALARY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SALARY_MONTHS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SALARY_WEEKS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SELF;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SENIORITY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SETTLE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SHORT_CONTRACT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.START;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SYSTEM;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TOTAL_BENEFITS_IT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WEEK_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_WEEKS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_YEARS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.parse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.PegasusSolver;
import org.apache.commons.math3.analysis.solvers.UnivariateSolver;

import com.code.aon.common.AonException;
import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.person.enumeration.Gender;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.OrderByList;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.calendar.enumeration.DayType;
import com.esferalia.aon.payroll.IrpfOutcome;
import com.esferalia.aon.payroll.Pair;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.CompositePayments;
import com.esferalia.aon.payroll.calculator.ContextFunctions;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.HierarchyDeductions;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.payroll.calculator.IContractCost;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractEmbargo;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.ISystemPayment;
import com.esferalia.aon.payroll.calculator.LRUCache;
import com.esferalia.aon.payroll.calculator.OnlyPaymentContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.SalaryExpressionException;
import com.esferalia.aon.payroll.calculator.UndefinedContextVariablesException;
import com.esferalia.aon.payroll.calculator.UndefinedTotalPaymentException;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext;
import com.esferalia.aon.payroll.irpf.IrpfCalculator;
import com.esferalia.aon.payroll.irpf.sql.SQLIrpfCalculatorContext;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementLevelCategoryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementLevelColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractPaymentColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseActivityColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseCccColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PayrollWorkplaceColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PersonColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RegistryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryColumns;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.CheckException;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionContext.ExpressionExceptionWrapper;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionImpl;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpression;
import com.esferalia.aon.salary.expression.IExpressionVariable;
import com.esferalia.aon.salary.expression.ITimedObject;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.InvalidVariables;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.TimedObject;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;
import com.esferalia.aon.salary.expression.Variables.NotFoundHandler;

public class SQLContractSalaryCalculatorContext implements
		IContractSalaryCalculatorContext, NotFoundHandler,
		ISQLContractSalaryCalculatorContext {

	public static final String PERSON_REGISTRY = "person_registry";
	public static final String ENTERPRISE_REGISTRY = "enterprise_registry";

	public static final String EMBARGO_PAID = "embargo_paid";
	public static final String EMPTY = "";
	public static final String SPACE = " ";
	public static final String COMMA = ",";
	public static final String DOT = ".";
	public static final String OPEN_BRACKET = "(";
	public static final String CLOSE_BRACKET = ")";

	private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");

	//@formatter:off
	private static final String MAIN_SQL = "SELECT * "
			+ " FROM contract"
			+ " LEFT JOIN enterprise_ccc ON (contract.enterprise_ccc = enterprise_ccc.id)"
			+ " LEFT JOIN enterprise_activity ON (contract.enterprise_activity = enterprise_activity.id)"
			+ " LEFT JOIN agreement_level_category ON (contract.agreement_level_category = agreement_level_category.id)"
			+ " LEFT JOIN agreement_level ON (agreement_level.id = agreement_level_category.agreement_level)"
			+ ", person"
			+ ", registry AS " + PERSON_REGISTRY
			+ ", workplace" 
			+ " LEFT JOIN payroll_workplace ON (payroll_workplace.workplace = workplace.id)"
			+ ", enterprise"
			+ ", registry AS " + ENTERPRISE_REGISTRY
			+ " LEFT JOIN customer ON (customer.registry = " + ENTERPRISE_REGISTRY + ".id)"
			+ ", raddress"
			+ " WHERE contract.person = person.registry" // INNER JOIN: person is NOT NULL
			+ " AND person.registry = person_registry.id" // INNER JOIN: // registry is NOT NULL
			+ " AND contract.workplace = workplace.id" // INNER JOIN: workplace is NOT NULL
			+ " AND workplace.enterprise = enterprise.registry" // INNER JOIN: enterprise is NOT NULL
			+ " AND enterprise.registry = enterprise_registry.id" // INNER JOIN: registry is NOT  NULL
			+ " AND workplace.address = raddress.id" // INNER JOIN: address is NOT NULL
			+ " AND contract.start_date <= ? "
			+ " AND ( contract.end_date  IS NULL" + " OR contract.end_date >= ? )" ;
	//@formatter:on

	private static final String PAYMENT_SQL = "SELECT * "
			+ ", "
			+ ExpressionScope.CONTRACT.ordinal()
			+ " AS "
			+ SQLContractPayment.SCOPE_ALIAS
			+ " FROM contract_payment AS "
			+ SQLContractPayment.PAYMENT_ALIAS
			+ " LEFT JOIN  payment_concept" // LEFT JOIN: payment_concept puede
											// ser NULL
			+ "	ON payment_concept = payment_concept.id"
			+ " WHERE contract = ? " + " AND start_date <= ? "
			+ " AND ( end_date IS NULL " + " OR end_date >= ? )";

	private static final String PAYMENTS_FILTER[] = {
			" AND salary_type IN (" + SalaryType.SALARY.ordinal() + " ,"
					+ SalaryType.EXTRA.ordinal() + ")", // SalaryType.SALARY
			" AND salary_type = " + SalaryType.EXTRA.ordinal() + " ", // SalaryType.EXTRA
			" AND salary_type = " + SalaryType.SETTLE.ordinal() + " ", // SalaryType.SETTLE
			" AND salary_type = " + SalaryType.DELAY.ordinal() + " ", // SalaryType.DELAY
			" AND salary_type = " + SalaryType.NOT_ENJOYED_VACATIONS.ordinal()
					+ " ", // SalaryType.NOT_ENJOYED_VACATIONS
	};
	// " AND salary_type IN ("+SalaryType.SALARY.ordinal()+" ,"+SalaryType.EXTRA.ordinal()+")"

	private static final String DEDUCTION_SQL = "SELECT *"
			+ ", "
			+ ExpressionScope.CONTRACT.ordinal()
			+ " AS "
			+ SQLContractDeduction.SCOPE_ALIAS
			+ " FROM contract_deduction"
			+ " LEFT JOIN  deduction_concept" // LEFT JOIN: deduction_concept
												// puede ser NULL
			+ "	ON deduction_concept = deduction_concept.id"
			+ " WHERE contract = ? " + " AND start_date <= ? "
			+ " AND ( end_date IS NULL" + " OR end_date >= ? )";

	private static final String BONUS_SQL = "SELECT *"
			+ " FROM contract_bonus"
			+ " LEFT JOIN  bonus_concept" // LEFT JOIN: bonus_concept puede ser
											// NULL
			+ "	ON bonus_concept = bonus_concept.id" + " WHERE contract = ? "
			+ " AND start_date <= ? " + " AND ( end_date IS NULL"
			+ " OR end_date >= ? )";

	private static final String EMBARGO_SQL = "SELECT *"
			+ ", ( SELECT sum(amount) FROM salary_embargo WHERE contract_embargo=contract_embargo.id  ) AS "
			+ EMBARGO_PAID + " FROM contract_embargo" + " WHERE contract = ? "
			+ " AND start_date <= ? " + " AND ( end_date IS NULL"
			+ " OR end_date >= ? )" + " ORDER BY start_date"; // ORDER BY : El
																// primero que
																// llega cobra

	private static final String SYSTEM_COST_SQL = "SELECT *"
			+ " FROM system_cost" + " WHERE start_date <= ? "
			+ " AND ( end_date IS NULL" + " OR end_date >= ? )"
			+ " AND system_cost.domain = ? ";

	private static final String SYSTEM_DEDUCTION_SQL = "SELECT *"
			+ ", "
			+ ExpressionScope.SYSTEM.ordinal()
			+ " AS "
			+ SQLContractDeduction.SCOPE_ALIAS
			+ " FROM system_deduction"
			+ " LEFT JOIN  deduction_concept" // LEFT JOIN: deduction_concept
												// puede ser NULL
			+ "	ON deduction_concept = deduction_concept.id"
			+ " WHERE start_date <= ? " + " AND ( end_date IS NULL"
			+ " OR end_date >= ? )" + " AND system_deduction.domain = ? ";

	private static final String SYSTEM_PAYMENT_SQL = "SELECT *"
			+ ", "
			+ ExpressionScope.SYSTEM.ordinal()
			+ " AS "
			+ SQLContractPayment.SCOPE_ALIAS
			+ " FROM system_payment AS "
			+ SQLContractPayment.PAYMENT_ALIAS
			+ " LEFT JOIN  payment_concept" // LEFT JOIN: payment_concept puede
											// ser NULL
			+ "	ON payment_concept = payment_concept.id"
			+ " WHERE start_date <= ? "
			+ " AND ( end_date IS NULL"
			+ " OR end_date >= ? )"
			+ " AND "
			+ SQLContractPayment.PAYMENT_ALIAS
			+ ".domain IN ( "
			+ StringUtils.repeat("?", ",",
					SSRegimeType.class.getEnumConstants().length) + ") ";

	private static final String CDATA_SQL = "SELECT * " + " FROM contract_data"
			+ " WHERE contract = ? " + "AND start_date <= ? "
			+ " AND ( end_date IS NULL " + " OR end_date >= ? )"
			+ " ORDER BY IF( name LIKE '%_GARANTIZADO',1,0)"
	// + ", IF(ISNULL(end_date),0,1) ASC,end_date DESC "
	// //IF(ISNULL(end_date),0,1),end_date DESC
	;

	public static final String CLEAVE_SQL_PARENT_DAYS = "dias";

	private static final String CLEAVE_SQL = "SELECT * ,"
			+ "( SELECT sum(DATEDIFF(end_date,start_date)+1)"
			+ " FROM contract_leave AS parent"
			+ " WHERE ( parent.id=contract_leave.parent"
			+ "  OR parent=contract_leave.parent )"
			+ " AND parent.start_date < contract_leave.start_date )" + " AS "
			+ CLEAVE_SQL_PARENT_DAYS + " FROM contract_leave"
			+ " WHERE contract = ? " + "AND start_date <= ? "
			+ " AND ( end_date IS NULL " + " OR end_date >= ? )";

	private static final int CACHE_SIZE = 25;

	public static class AgreementContextKey extends Pair<Integer, Integer> {

		public AgreementContextKey(Integer agreementId, Integer agreementLevelId) {
			super(agreementId, agreementLevelId);
		}

		public Integer getAgreementId() {
			return getFirst();
		};

		public Integer getAgreementLevelId() {
			return getSecond();
		};

	}

	static class GuarenteeException extends SalaryException {

		private double guarentee;

		public GuarenteeException(double guarentee) {
			this.guarentee = guarentee;
		}

		public double getGuarentee() {
			return guarentee;
		}
	}

	protected static class SQLNoItContractSalaryCalculatorContext extends
			SQLContractSalaryCalculatorContext {

		public SQLNoItContractSalaryCalculatorContext(Connection connection,
				Date startDate, Date endDate, Date issueDate, Criteria criteria)
				throws SQLException, ExpressionException {

			super(connection, startDate, endDate, issueDate, criteria);
			// with this, we assure no leave I.T.
			super.leaveLoader = new SQLContractLeaveLoader(startDate, endDate) {
				@Override
				public void loadContractLevae(ResultSet rs,
						ExpressionContext exprCtx) throws SQLException,
						ExpressionException {
				}
			};
		}

		@Override
		public Object liquid(double liquid) throws ExpressionException,
				SQLException, SalaryException {
			throw new UndefinedContextVariablesException(
					ContextVariable.TOTAL_LIQUID);
		}

		@Override
		public Object guarantee(double guarentee) throws ExpressionException {
			throw new SalaryExpressionException(new GuarenteeException(
					guarentee));
		}

	}

	/**
	 * Clase base para implementar variables pesadas con evaluación perezosa.
	 * Las clases hijas únicamente deberán implementar el método 'V getValue()'.
	 * 
	 * @author rtrepiana
	 * 
	 * @param <V>
	 */
	protected abstract class LazyTimedVariable<V> implements ITimedVariable<V> {
		private V value;
		private boolean initialized = false;

		protected Period period = new Period(startDate, endDate);

		@Override
		public Period getPeriod() {
			return period;
		}

		@Override
		public V getValue(Period p) {
			if (!initialized) {
				value = create();
				initialized = true;
			}

			return value;
		}

		public abstract V create();
	}

	protected abstract class LazyTimedExpressionVariable<V> extends
			LazyTimedVariable<V> implements IExpressionVariable<V> {

		ExpressionImpl expression;

		public LazyTimedExpressionVariable(String name, ExpressionScope scope) {
			expression = new ExpressionImpl() {
				@Override
				public String getExpression() {
					V value = getValue(period);
					return value != null ? String.valueOf(value) : null;
				}
			};
			expression.setName(name);
			expression.setScope(scope);
		}

		// --------------------------------------------------------------------

		@Override
		public IExpression getExpression() {
			return expression;
		}

		@Override
		public Map<String, ITimedVariable<?>> getContext() {
			return Collections.emptyMap();
		}

		// ---------------------------------------------------------------------
		protected String getName() {
			return expression.getName();
		}
	}

	protected abstract class ActiveTimedVariable<V> implements
			ITimedVariable<V> {

		protected Period period = new Period(startDate, endDate);

		@Override
		public Period getPeriod() {
			return period;
		}

	}

	protected abstract class ActiveTimedExpressionVariable<V> extends
			ActiveTimedVariable<V> implements IExpressionVariable<V> {

		ExpressionImpl expression;

		public ActiveTimedExpressionVariable(String name, ExpressionScope scope) {
			expression = new ExpressionImpl() {
				@Override
				public String getExpression() {
					V value = getValue(period);
					return value != null ? String.valueOf(value) : null;
				}
			};
			expression.setName(name);
			expression.setScope(scope);
		}

		// --------------------------------------------------------------------

		@Override
		public IExpression getExpression() {
			return expression;
		}

		@Override
		public Map<String, ITimedVariable<?>> getContext() {
			return Collections.emptyMap();
		}

		// ---------------------------------------------------------------------
		protected String getName() {
			return expression.getName();
		}
	}

	protected class DeferredTimedVariable implements ITimedVariable<Object> {

		private String script;
		protected Period period;

		@Override
		public Period getPeriod() {
			return period;
		}

		public DeferredTimedVariable(IExpression expression, Date start,
				Date end) {
			this.period = new Period(start, end);
			this.script = expression.getExpression();
			Set<String> vars = Collections.singleton(expression.getName());
		}

		@Override
		public Object getValue(Period period) {
			try {
				List<ITimedResult<Object>> timedObjects = getExpressionContext()
						.eval(script, period.getStart(), period.getEnd(),
								Object.class);

				return timedObjects.get(0).getValue();
			} catch (ExpressionException e) {
				e.printStackTrace();
				return null;
			}
		}

	}

	public static Criteria getPaymentsCriteria(SalaryType... types) {
		Criteria criteria = new Criteria();
		for (SalaryType type : types) {
			criteria.addOrExpression(ExpressionUtilities.getEqualExpression(
					ContractPaymentColumns.SALARY_TYPE, type.ordinal()));
		}
		return criteria;
	}

	private Criteria criteria;
	private Connection connection;

	private Date issueDate;
	private Date chargeDate;
	private Date startDate;
	private Date endDate;
	private ResultSet resultSet;
	private ResultSet paymentResultSet;
	private PreparedStatement ceventStmt;
	private PreparedStatement cleaveStmt;
	private PreparedStatement paymentStmt;
	private PreparedStatement deductionStmt;
	private PreparedStatement bonusStmt;
	private PreparedStatement embargoStmt;

	private SQLContractPayment sqlContractPayment;
	private SQLContractDeduction sqlContractDeduction;
	private SQLContractBonus sqlContractBonus;
	private SQLContractEmbargo sqlContractEmbargo;
	private ExpressionContext contractExpressionContext;
	private ExpressionContext implicitExpressionContext;
	private SQLContractLeaveLoader leaveLoader;

	private Date contractStartDate;
	private Date contractEndDate;
	private Collection<IContractCost> systemCosts;
	private Collection<IContractDeduction> systemDeductions;
	private Collection<ISystemPayment> systemPayments;

	private SQLCnae2009 cnae2009;
	private LRUCache<Integer, ICalendar> calendars;
	private SQLCalendarFactory calendarFactory;
	private LRUCache<Integer, Collection<IContractPayment>> agreementPayments;
	private SQLAgreementPaymentsFactory agreementPaymentsFactory;
	private LRUCache<AgreementContextKey, ExpressionContext> agreementExpressionContexts;
	private SQLAgreementContextFactory agreementContextFactory;

	private Criteria paymentsCriteria;
	private OrderByList order;

	private IListener listener;

	/*
	 * public SQLContractSalaryCalculatorContext(Connection connection, Date
	 * startDate, Date endDate) throws SQLException, ExpressionException {
	 * this(connection, startDate, endDate, Calendar.getInstance().getTime());
	 * 
	 * }
	 * 
	 * public SQLContractSalaryCalculatorContext(Connection connection, Date
	 * startDate, Date endDate, Date issueDate) throws SQLException,
	 * ExpressionException { this(connection, startDate, endDate, issueDate,
	 * null); }
	 */

	public SQLContractSalaryCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Criteria criteria)
			throws SQLException, ExpressionException {
		this(connection, startDate, endDate, issueDate, criteria,
				getPaymentsCriteria(SalaryType.SALARY, SalaryType.EXTRA));
	}

	public SQLContractSalaryCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Criteria criteria,
			OrderByList order) throws SQLException, ExpressionException {
		this(connection, startDate, endDate, issueDate, criteria,
				getPaymentsCriteria(SalaryType.SALARY, SalaryType.EXTRA), order);
	}

	public SQLContractSalaryCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Date chargeDate,
			Criteria criteria) throws SQLException, ExpressionException {
		this(connection, startDate, endDate, issueDate, chargeDate, criteria,
				getPaymentsCriteria(SalaryType.SALARY, SalaryType.EXTRA), NEWER);
	}

	public SQLContractSalaryCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Date chargeDate,
			Criteria criteria, OrderByList order) throws SQLException,
			ExpressionException {
		this(connection, startDate, endDate, issueDate, chargeDate, criteria,
				getPaymentsCriteria(SalaryType.SALARY, SalaryType.EXTRA), order);
	}

	public SQLContractSalaryCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Criteria criteria,
			Criteria paymentsCriteria, OrderByList order) throws SQLException,
			ExpressionException {
		this(connection, startDate, endDate, issueDate, null, criteria,
				paymentsCriteria, order);
	}

	public SQLContractSalaryCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Criteria criteria,
			Criteria paymentsCriteria) throws SQLException, ExpressionException {
		this(connection, startDate, endDate, issueDate, null, criteria,
				paymentsCriteria, NEWER);
	}

	public SQLContractSalaryCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Date chargeDate,
			Criteria criteria, Criteria paymentsCriteria) throws SQLException,
			ExpressionException {
		this(connection, startDate, endDate, issueDate, chargeDate, criteria,
				paymentsCriteria, NEWER);
	}

	public SQLContractSalaryCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Date chargeDate,
			Criteria criteria, Criteria paymentsCriteria, OrderByList order,
			Object... args) throws SQLException, ExpressionException {
		this.connection = connection;

		this.startDate = new Date(DateUtils.truncate(startDate,
				Calendar.DAY_OF_MONTH).getTime());
		this.endDate = new Date(DateUtils.truncate(endDate,
				Calendar.DAY_OF_MONTH).getTime());
		this.issueDate = new Date(issueDate.getTime());
		this.chargeDate = chargeDate != null ? new Date(chargeDate.getTime())
				: null;

		this.criteria = criteria;
		this.paymentsCriteria = paymentsCriteria;
		this.order = order;

		initResultSet(args);
		initPaymentStmt();
		initDeductionStmt();
		initBonusStmt();
		// initEmbargoStmt();
		initCeventStmt();
		initLeaveStmt();
		initSystemCosts();
		initSystemDeductions();
		initSystemPayments();

		this.sqlContractPayment = new SQLContractPayment();
		this.sqlContractDeduction = new SQLContractDeduction();
		this.sqlContractBonus = new SQLContractBonus();
		this.sqlContractEmbargo = new SQLContractEmbargo();

		this.cnae2009 = new SQLCnae2009(connection, this.startDate,
				this.endDate);

		calendarFactory = new SQLCalendarFactory(connection, this.startDate,
				this.endDate);
		this.calendars = new LRUCache<Integer, ICalendar>(CACHE_SIZE,
				calendarFactory);
		calendarFactory.setCache(calendars); // TODO: Todo en la misma clase???

		agreementPaymentsFactory = new SQLAgreementPaymentsFactory(connection,
				this.startDate, this.endDate, this.paymentsCriteria);
		this.agreementPayments = new LRUCache<Integer, Collection<IContractPayment>>(
				CACHE_SIZE, agreementPaymentsFactory);

		agreementContextFactory = new SQLAgreementContextFactory(connection,
				this.startDate, this.endDate, order);
		this.agreementExpressionContexts = new LRUCache<AgreementContextKey, ExpressionContext>(
				CACHE_SIZE, agreementContextFactory);
		this.leaveLoader = new SQLContractLeaveLoader(this.startDate,
				this.endDate);

	}

	@Override
	public SalaryType getSalaryType() {
		return SalaryType.SALARY;
	}

	@Override
	public Date getChargeDate() {
		return this.chargeDate != null ? chargeDate : getEndDate();
	}

	@Override
	public Date getIrpfDate() {
		return this.chargeDate != null ? chargeDate : getEndDate();
	}

	@Override
	public Date getIssueDate() {
		return this.issueDate;
	}

	@Override
	public Date getStartDate() {
		return this.contractStartDate;
	}

	@Override
	public Date getEndDate() {
		return this.contractEndDate;
	}

	@Override
	public ISalaryProxy getSalaryProxy() {
		return new SQLSalaryProxy(getId(), getDomain());
	}

	@Override
	public ExpressionContext getExpressionContext() {
		return contractExpressionContext;
	}

	@Override
	public ExpressionContext getSystemExpressionContext() {
		return agreementContextFactory.getSystemExpressionContext();
	}

	@Override
	public ExpressionContext getImplicitExpressionContext() {
		return this.implicitExpressionContext;
	}

	@Override
	public ExpressionContext getAgreementExpressionContext() {
		try {
			return getAgreementContext();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} catch (ExpressionException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getCcc() {
		return getString(SQLConstants.ENTERPRISE_CCC, EnterpriseCccColumns.CCC);
	}

	@Override
	public String getEnterpriseName() {
		String name = getString(ENTERPRISE_REGISTRY, RegistryColumns.NAME);
		if (!StringUtils.isEmpty(name)) {
			return name;
		} else {
			return StringUtils.EMPTY;
		}
	}

	@Override
	public String getEnterpriseAddress() {
		/* TODO Añadir la tabla y columnas a las constantes. */

		String streetType = getString(SQLConstants.RADDRESS,
				SQLConstants.RaddressColumns.STREET_TYPE);
		String address = getString(SQLConstants.RADDRESS,
				SQLConstants.RaddressColumns.ADDRESS);
		String number = getString(SQLConstants.RADDRESS,
				SQLConstants.RaddressColumns.NUMBER);
		String address2 = getString(SQLConstants.RADDRESS,
				SQLConstants.RaddressColumns.ADDRESS2);
		String address3 = getString(SQLConstants.RADDRESS,
				SQLConstants.RaddressColumns.ADDRESS3);
		String zip = getString(SQLConstants.RADDRESS,
				SQLConstants.RaddressColumns.ZIP);
		String city = getString(SQLConstants.RADDRESS,
				SQLConstants.RaddressColumns.CITY);
		String geozone = getString(SQLConstants.RADDRESS,
				SQLConstants.RaddressColumns.GEOZONE);

		StringBuffer buf = new StringBuffer();
		buf.append(streetType == null ? EMPTY : streetType);
		buf.append(streetType == null ? EMPTY : DOT);
		buf.append(streetType == null ? EMPTY : SPACE);
		buf.append(StringUtils.isEmpty(address) ? EMPTY : address);
		buf.append(StringUtils.isEmpty(number) ? EMPTY : SPACE);
		buf.append(StringUtils.isEmpty(number) ? EMPTY : number);
		buf.append(StringUtils.isEmpty(address2) ? EMPTY : COMMA);
		buf.append(StringUtils.isEmpty(address2) ? EMPTY : SPACE);
		buf.append(StringUtils.isEmpty(address2) ? EMPTY : address2);
		buf.append(StringUtils.isEmpty(address3) ? EMPTY : SPACE);
		buf.append(StringUtils.isEmpty(address3) ? EMPTY : OPEN_BRACKET);
		buf.append(StringUtils.isEmpty(address3) ? EMPTY : address3);
		buf.append(StringUtils.isEmpty(address3) ? EMPTY : CLOSE_BRACKET);
		buf.append(StringUtils.isEmpty(zip) ? EMPTY : SPACE);
		buf.append(StringUtils.isEmpty(zip) ? EMPTY : OPEN_BRACKET);
		buf.append(StringUtils.isEmpty(zip) ? EMPTY : zip);
		buf.append(StringUtils.isEmpty(zip) ? EMPTY : CLOSE_BRACKET);
		buf.append(StringUtils.isEmpty(city) ? EMPTY : SPACE);
		buf.append(StringUtils.isEmpty(city) ? EMPTY : city);

		return StringUtils.abbreviate(buf.toString(), 64); // Avoid truncate
	}

	@Override
	public String getEnterpriseDocument() {
		return getString(ENTERPRISE_REGISTRY, RegistryColumns.DOCUMENT);
	}

	@Override
	public SSRegimeType getSSRegime() {
		int ordinal = getInt(SQLConstants.CONTRACT, ContractColumns.SS_REGIME); // 'ss_regime'
																				// is
																				// NOT
																				// NULL
		return SSRegimeType.values()[ordinal];
	}

	@Override
	public String getCategory() {
		return getString(SQLConstants.AGREEMENT_LEVEL_CATEGORY,
				AgreementLevelCategoryColumns.DESCRIPTION);
	}

	@Override
	public String getQuoteGroup() {
		return contractExpressionContext.getVariable(
				ContextVariable.QUOTE_GROUP, startDate, endDate, String.class);
	}

	@Override
	public String getEmployeeName() {
		String name = getString(SQLConstants.PERSON, PersonColumns.NAME);
		String firstSurname = getString(SQLConstants.PERSON,
				PersonColumns.FIRST_SURNAME);
		String secondSurname = getString(SQLConstants.PERSON,
				PersonColumns.SECOND_SURNAME);

		StringBuffer employeeName = new StringBuffer();

		if (!StringUtils.isEmpty(firstSurname)) {
			employeeName.append(firstSurname);
		}
		if (!StringUtils.isEmpty(secondSurname)) {
			employeeName.append(SPACE);
			employeeName.append(secondSurname);
		}
		if (!StringUtils.isEmpty(name)) {
			employeeName.append(COMMA);
			employeeName.append(SPACE);
			employeeName.append(name);
		}

		return employeeName.toString();
	}

	@Override
	public String getEmployeeDocument() {
		return getString(PERSON_REGISTRY, RegistryColumns.DOCUMENT);
	}

	@Override
	public String getSocialSecurityNumber() {
		return getString(SQLConstants.PERSON, PersonColumns.SOCIAL_SECURITY_NUM);
	}

	@Override
	public Integer getRegistration() {
		return getInt(SQLConstants.CONTRACT, ContractColumns.REGISTRATION);
	}

	@Override
	public Date getSeniorityDate() {
		return getDate(SQLConstants.CONTRACT, ContractColumns.SENIORITY_DATE);
	}

	@Override
	public Collection<IContractPayment> getAgreementPayments() {
		Integer agreementId = getAgreement();
		return agreementPayments.get(agreementId);
	}

	@Override
	public Collection<IContractPayment> getContractPayments()
			throws AonException {
		try {
			this.sqlContractPayment.close();
			int id = getId();
			paymentStmt.setInt(1, id);
			ResultSet rs = paymentStmt.executeQuery();
			this.sqlContractPayment.setResultSet(rs);
			// return new HierarchyPayments(
			// this.sqlContractPayment,
			// getAgreementPayments().iterator(),
			// this.systemPayments.iterator());

			return new CompositePayments(this.sqlContractPayment,
					getAgreementPayments(), getSystemPayments());
		} catch (SQLException e) {
			throw new AonException(e);
		}
	}

	@Override
	public Collection<IContractDeduction> getContractDeductions()
			throws AonException {

		try {
			this.sqlContractDeduction.close();
			int id = getId();
			deductionStmt.setInt(1, id);
			ResultSet rs = deductionStmt.executeQuery();
			this.sqlContractDeduction.setResultSet(rs);
			HierarchyDeductions hierarchyDeductions = new HierarchyDeductions(
					this.sqlContractDeduction, this.systemDeductions.iterator());
			return hierarchyDeductions;
		} catch (SQLException e) {
			throw new AonException(e);
		}

	}

	@Override
	public Collection<IContractEmbargo> getContractEmbargos()
			throws AonException {
		try {

			this.sqlContractEmbargo.close();
			int id = getId();
			embargoStmt.setInt(1, id);
			ResultSet rs = embargoStmt.executeQuery();
			this.sqlContractEmbargo.setResultSet(rs);
			return this.sqlContractEmbargo;
		} catch (SQLException e) {
			throw new AonException(e);
		}
	}

	@Override
	public Collection<IContractCost> getContractCosts() throws AonException {
		return this.systemCosts;
	}

	@Override
	public Collection<IContractBonus> getContractBonus() throws AonException {
		try {
			this.sqlContractBonus.close();
			int id = getId();
			bonusStmt.setInt(1, id);
			ResultSet rs = bonusStmt.executeQuery();
			this.sqlContractBonus.setResultSet(rs);
			return sqlContractBonus;
		} catch (SQLException e) {
			throw new AonException(e);
		}
	}

	public static final Pattern ACTUAL_VAR_PATTERN = Pattern
			.compile("(\\w+)_ACTUAL");

	private class ActualVar extends ActiveTimedVariable<Object> {

		private String var;

		public ActualVar(String var) {
			this.var = var;
		}

		@Override
		public Object getValue(Period period) {
			Date date = getChargeDate();
			ExpressionContext ctx = getExpressionContext();
			return ctx.getVariable(var, date, date, Object.class);
		}

	}

	@Override
	public List<ITimedVariable<?>> get(String var) {
		Matcher matcher = ACTUAL_VAR_PATTERN.matcher(var);

		if (!matcher.matches()) {
			return null;
		}
		List<ITimedVariable<?>> values = new ArrayList<ITimedVariable<?>>(1);

		String srcVar = matcher.group(1);

		values.add(new ActualVar(srcVar));

		return values;
	}

	@Override
	public IListener getListener() {
		return listener;
	}

	public void setListener(IListener listener) {
		this.listener = listener;
	}

	public OrderByList getOrder() {
		return order;
	}

	public int getId() {
		return getInt(SQLConstants.CONTRACT, ContractColumns.ID);
	}

	public Integer getCnae2009() {
		Object cna2009 = getObject(SQLConstants.ENTERPRISE_ACTIVITY,
				EnterpriseActivityColumns.CNAE2009);
		return cna2009 != null ? (Integer) cna2009 : null;
	}

	public boolean next() throws SQLException, ExpressionException {

		boolean next = this.resultSet.next();
		if (next) {
			initContractExpressionCtx();
		} else {
			close();
		}
		return next;
	}

	public void close() throws SQLException {
		if (this.resultSet != null) {
			this.resultSet.close();
			this.resultSet = null;
		}
		if (this.ceventStmt != null) {
			this.ceventStmt.close();
			this.ceventStmt = null;
		}
		if (this.cleaveStmt != null) {
			this.cleaveStmt.close();
			this.cleaveStmt = null;
		}
		if (this.paymentStmt != null) {
			this.paymentStmt.close();
			this.paymentStmt = null;
		}
		if (this.deductionStmt != null) {
			this.deductionStmt.close();
			this.deductionStmt = null;
		}
		if (this.bonusStmt != null) {
			this.bonusStmt.close();
			this.bonusStmt = null;
		}
		if (this.embargoStmt != null) {
			this.embargoStmt.close();
			this.embargoStmt = null;
		}
		if (this.agreementExpressionContexts != null) {
			this.agreementExpressionContexts.clear();
			this.agreementExpressionContexts = null;
		}
		if (this.agreementContextFactory != null) {
			this.agreementContextFactory.close();
			// this.agreementContextFactory = null;
		}
		if (this.agreementExpressionContexts != null) {
			this.agreementExpressionContexts.clear();
			this.agreementExpressionContexts = null;
		}
		if (this.agreementPaymentsFactory != null) {
			this.agreementPaymentsFactory.close();
			this.agreementPaymentsFactory = null;
		}
		if (this.calendars != null) {
			this.calendars.clear();
			this.calendars = null;
		}
		if (this.calendarFactory != null) {
			this.calendarFactory.close();
			this.calendarFactory = null;
		}
		if (this.systemDeductions != null) {
			this.systemDeductions.clear();
			this.systemDeductions = null;
		}
		if (this.systemPayments != null) {
			this.systemPayments.clear();
			this.systemPayments = null;
		}
		if (this.systemCosts != null) {
			this.systemCosts.clear();
			this.systemCosts = null;
		}
	}

	public void setOrder(OrderByList order) {
		this.order = order;
	}

	public Object getObject(String tableLabel, String columnLabel) {
		try {
			return this.resultSet.getObject(tableLabel + "." + columnLabel);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public Integer getInt(String tableLabel, String columnLabel) {
		try {
			return this.resultSet.getInt(tableLabel + "." + columnLabel);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public Date getDate(String tableLabel, String columnLabel) {
		try {
			return this.resultSet.getDate(tableLabel + "." + columnLabel);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public String getString(String tableLabel, String columnLabel) {
		try {
			return this.resultSet.getString(tableLabel + "." + columnLabel);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public <T> void addVariable(String name, T t) {
		this.contractExpressionContext.addVariable(name, t,
				this.contractStartDate, this.contractEndDate);
	}

	public <T> T getVariable(ContextVariable var, Class<T> toType) {
		return getVariable(var.getName(), toType);
	}

	public <T> T getVariable(String name, Class<T> toType) {
		return this.contractExpressionContext.getVariable(name,
				this.contractStartDate, this.contractEndDate, toType);
	}

	public static java.sql.Date toSqlDate(Date date) {
		return new java.sql.Date(date.getTime());
	}

	public Connection getConnection() {
		return connection;
	}

	public IIrpfCalculatorContext getIrpfCalculatorContext() {
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(startDate);
		endCalendar.set(Calendar.DAY_OF_YEAR,
				endCalendar.getActualMaximum(Calendar.DAY_OF_YEAR));
		Date endYear = endCalendar.getTime();

		Criteria contractCriteria = new Criteria();
		contractCriteria.addExpression(criteria.getExpression());
		contractCriteria.addEqualExpression(SQLConstants.CONTRACT + "."
				+ ContractColumns.ID, getId());

		return getIrpfCalculatorContext(connection, startDate, endYear,
				contractCriteria);
	}

	protected ISalaryCalculatorContext getLiquidCalculatorContext(final double x) {

		Criteria contractCriteria = new Criteria();
		contractCriteria.addExpression(criteria.getExpression());
		contractCriteria.addEqualExpression(SQLConstants.CONTRACT + "."
				+ ContractColumns.ID, getId());

		return getLiquidCalculatorContext(connection, startDate, endDate,
				issueDate, contractCriteria, x);
	}

	// ------------------------------------------------------- Protected methods

	protected Date getEnd() {
		return this.endDate;
	}

	protected Date getStart() {
		return this.startDate;
	}

	protected String getMainSql(Object... args) {
		return MAIN_SQL;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.agreementContextFactory = null;
	}

	protected Integer getAgreement() {
		Object value = getObject(SQLConstants.AGREEMENT_LEVEL,
				AgreementLevelColumns.AGREEMENT);
		return value == null ? null : (Integer) value;
	}

	// --------------------------------------------------------- Private methods

	private void initResultSet(Object... args) throws SQLException {
		String sql = getMainSql(args);
		if (this.criteria != null) {
			sql = CriteriaUtilities.toSQLString(this.criteria, sql);
		}
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setDate(1, toSqlDate(this.endDate));
		stmt.setDate(2, toSqlDate(this.startDate));
		resultSet = stmt.executeQuery();
	}

	private void initPaymentStmt() throws SQLException {
		String paymentSql = CriteriaUtilities.toSQLString(paymentsCriteria,
				PAYMENT_SQL);
		paymentSql = orderBy(paymentSql, order == OLDER ? NEWER : OLDER);
		this.paymentStmt = this.connection.prepareStatement(paymentSql);
		this.paymentStmt.setDate(2, toSqlDate(this.endDate));
		this.paymentStmt.setDate(3, toSqlDate(this.startDate));
	}

	private void initDeductionStmt() throws SQLException {
		this.deductionStmt = this.connection.prepareStatement(DEDUCTION_SQL);
		this.deductionStmt.setDate(2, toSqlDate(this.endDate));
		this.deductionStmt.setDate(3, toSqlDate(this.startDate));
	}

	private void initBonusStmt() throws SQLException {
		this.bonusStmt = this.connection.prepareStatement(BONUS_SQL);
		this.bonusStmt.setDate(2, toSqlDate(this.endDate));
		this.bonusStmt.setDate(3, toSqlDate(this.startDate));
	}

	private void initEmbargoStmt() throws SQLException {
		this.embargoStmt = this.connection.prepareStatement(EMBARGO_SQL);
		this.embargoStmt.setDate(2, toSqlDate(this.endDate));
		this.embargoStmt.setDate(3, toSqlDate(this.startDate));
	}

	private void initCeventStmt() throws SQLException {

		String sql = orderBy(CDATA_SQL, order);
		this.ceventStmt = this.connection.prepareStatement(sql);
	}

	private void initLeaveStmt() throws SQLException {
		this.cleaveStmt = this.connection.prepareStatement(CLEAVE_SQL);
		this.cleaveStmt.setDate(2, toSqlDate(this.endDate));
		this.cleaveStmt.setDate(3, toSqlDate(this.startDate));
	}

	private Integer getDomain() {
		Object value = getObject(SQLConstants.CONTRACT, ContractColumns.DOMAIN);
		return value == null ? null : (Integer) value;
	}

	private Integer getAgreementLevel() {
		Object value = getObject(SQLConstants.AGREEMENT_LEVEL_CATEGORY,
				AgreementLevelCategoryColumns.AGREEMENT_LEVEL);
		return value == null ? null : (Integer) value;
	}

	private ExpressionContext getAgreementContext() throws SQLException,
			ExpressionException {
		Integer agreementId = getAgreement();
		Integer agreementLevelId = getAgreementLevel();
		AgreementContextKey agreementAndLevel = new AgreementContextKey(
				agreementId, agreementLevelId);
		return agreementExpressionContexts.get(agreementAndLevel);
	}

	private Collection<IContractPayment> getSystemPayments()
			throws AonException {
		List<IContractPayment> payments = new ArrayList<IContractPayment>(
				systemPayments.size());
		for (ISystemPayment systemPayment : systemPayments) {
			if (filter(systemPayment)) {
				payments.add(systemPayment);
			}
		}
		return payments;

	}

	private boolean filter(ISystemPayment systemPayment) {
		return systemPayment.getDomain() == (-1) * getSSRegime().ordinal();
	}

	/*
	 * Devuelve el <code>ICalendar</code> asociado con el contrato (trabajador),
	 * si no tiene calendario propio devuelve el de su centro de trabajo o el
	 * del sistema ( calendario estatal ) si el centro tampoco tiene calendario
	 * propio.
	 */
	private ICalendar getCalendar() {
		Object calendarId = getObject(SQLConstants.CONTRACT,
				ContractColumns.CALENDAR);
		if (calendarId == null) {
			calendarId = getObject(SQLConstants.PAYROLL_WORKPLACE,
					PayrollWorkplaceColumns.CALENDAR);
		}
		return calendars.get((Integer) calendarId); // (Integer) null devuelve
													// null, perfecto.
	}

	public Object br(Date startDate) throws ExpressionException, SQLException,
			SalaryException {

		ISalary salary = getDbSalary(connection, startDate, SalaryType.SALARY,
				getId());
		if (salary == null)
			salary = getSalary(connection, startDate, SalaryType.SALARY,
					getId());
		if (salary == null)
			throw new ExpressionException(); // TODO: Alert somebody that we
												// can't calculate proper BR.

		int days = salary.getTimeUnits();
		return salary.getCommonBase() / days;
	}

	public Object gross(double gross) throws ExpressionException, SQLException,
			SalaryException {
		return grossImpl(gross);
	}

	public Object grossImpl(double gross) throws ExpressionException,
			SQLException, SalaryException {
		Double totalPayment = getVariable(ContextVariable.TOTAL_PAYMENT,
				Double.class);
		if (totalPayment == null)
			throw new UndefinedTotalPaymentException();

		Double totalGross = getVariable("__GROSS", Double.class);
		if (totalGross == null) {
			totalGross = gross;
		} else {
			totalGross += gross;
		}

		addVariable("__GROSS", totalGross);

		return totalGross - totalPayment;
	}

	public Object liquid(double liquid) throws ExpressionException,
			SQLException, SalaryException {

		return liquidImpl(liquid, 0.005);
	}

	public Object liquidImpl(double liquid, double accuracy)
			throws ExpressionException, SQLException, SalaryException {
		try {
			return solveLiquid(new PegasusSolver(accuracy), liquid);
		} catch (Throwable t) {
			t.printStackTrace();
			return 0.00;
		}
	}

	public Object guaranteeWarn(String message) throws ExpressionException {

		if (leaveLoader.isEmpty())
			throw new UndefinedContextVariablesException(
					ContextVariable.LEAVE_DAYS);

		throw new CheckException(message);
	}

	public Object guarantee(double guarentee) throws ExpressionException {

		if (leaveLoader.isEmpty())
			throw new UndefinedContextVariablesException(
					ContextVariable.LEAVE_DAYS);

		final Criteria contractCriteria = new Criteria();
		contractCriteria.addExpression(criteria.getExpression());
		contractCriteria.addEqualExpression(SQLConstants.CONTRACT + "."
				+ ContractColumns.ID, getId());

		ISalaryCalculatorContext ctx = getNoItCalculatorContext(connection,
				startDate, endDate, issueDate, contractCriteria);

		ContractSalaryCalculator calculator = new OnlyPaymentContractSalaryCalculator();
		calculator.setSalaryBuilder(new SalaryBuilder());

		try {
			calculator.calculate(ctx);
		} catch (GuarenteeException e) {

			if (e.guarentee == guarentee)
				throw new ContextFunctions.UselessGuaranteeException(guarentee);
			if (e.guarentee < guarentee)
				throw new ContextFunctions.UselessGuaranteeException(e.guarentee, guarentee);

			return e.guarentee - guarentee;
		} catch (SalaryException e) {
			throw new RuntimeException(e);
		}
		return 0.00;
	}

	protected double solveLiquid(UnivariateSolver solver, final double liquid) {

		final Criteria contractCriteria = new Criteria();
		contractCriteria.addExpression(criteria.getExpression());
		contractCriteria.addEqualExpression(SQLConstants.CONTRACT + "."
				+ ContractColumns.ID, getId());

		double result = solver.solve(Byte.MAX_VALUE, new UnivariateFunction() {

			@Override
			public double value(double x) {
				try {
					ISalaryCalculatorContext ctx = getLiquidCalculatorContext(
							connection, startDate, endDate, issueDate,
							contractCriteria, x);
					ContractSalaryCalculator calculator = new ContractSalaryCalculator();
					calculator.setSalaryBuilder(new SalaryBuilder());

					ISalary salary = calculator.calculate(ctx);

					// TODO: Warning a bit tricky.
					ExpressionContext expressionCtx = SQLContractSalaryCalculatorContext.this
							.getExpressionContext();
					expressionCtx.addVariable(
							ContextVariable.IRPF_PERCENT,
							ctx.getExpressionContext().getVariable(
									ContextVariable.IRPF_PERCENT, startDate,
									endDate));

					return liquid - salary.getTotalLiquid();
				} catch (SalaryException e) {
					throw new RuntimeException(e);
				}
			}

		}, -3 * liquid, 3 * liquid, 0);

		return result;
	}

	protected ISQLContractSalaryCalculatorContext getNoItCalculatorContext(
			Connection conn, Date startDate, Date endDate, Date issueDate,
			Criteria criteria) {
		SQLContractSalaryCalculatorContext ctx;
		try {
			ctx = new SQLNoItContractSalaryCalculatorContext(conn, startDate,
					endDate, issueDate, criteria);
			ctx.next();
			return ctx;
		} catch (ExpressionException e) {
			throw new ExpressionExceptionWrapper(e);
		} catch (SQLException e) {
			throw new ExpressionExceptionWrapper(new ExpressionException(e));
		}
	}

	protected ISalaryCalculatorContext getLiquidCalculatorContext(
			Connection conn, Date startDate, Date endDate, Date issueDate,
			Criteria criteria, final double x) {
		SQLContractSalaryCalculatorContext ctx;
		try {
			ctx = new SQLContractSalaryCalculatorContext(connection, startDate,
					endDate, issueDate, criteria) {

				@Override
				public Object liquid(double liquid) throws ExpressionException,
						SQLException {
					return x;
				}

				@Override
				protected ISQLContractSalaryCalculatorContext getNoItCalculatorContext(
						Connection conn, Date startDate, Date endDate,
						Date issueDate, Criteria criteria) {
					SQLContractSalaryCalculatorContext ctx;
					try {
						ctx = new SQLNoItContractSalaryCalculatorContext(conn,
								startDate, endDate, issueDate, criteria) {

							public Object liquid(double liquid)
									throws ExpressionException, SQLException,
									SalaryException {
								return x;
							};
						};
						ctx.next();
						return ctx;
					} catch (ExpressionException e) {
						throw new ExpressionExceptionWrapper(e);
					} catch (SQLException e) {
						throw new ExpressionExceptionWrapper(
								new ExpressionException(e));
					}
				}

				@Override
				protected IIrpfCalculatorContext getIrpfCalculatorContext(
						Connection conn, Date startDate, Date endDate,
						Criteria criteria) {
					try {
						return new SQLIrpfCalculatorContext(connection,
								startDate, endDate,
								new SQLContractSalaryCalculatorContext(conn,
										startDate, endDate, endDate, criteria) {
									@Override
									public Object liquid(double liquid)
											throws ExpressionException,
											SQLException {
										return x;
									}

								}) {
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

							public int getAñoNacimiento() {
								return 1969;
							};

						};
					} catch (SQLException e) {
						throw new ExpressionExceptionWrapper(
								new ExpressionException(e));
					} catch (ExpressionException e) {
						throw new ExpressionExceptionWrapper(e);
					}
				}

			};
			ctx.next();
			return ctx;
		} catch (ExpressionException e) {
			throw new ExpressionExceptionWrapper(e);
		} catch (SQLException e) {
			throw new ExpressionExceptionWrapper(new ExpressionException(e));
		}
	}

	public Object agreement(String name) throws ExpressionException,
			SQLException {
		ExpressionContext agreementCtx = getAgreementContext();
		return agreementCtx.getVariable(name, startDate, endDate, Object.class);
	}

	public Object system(String name) throws ExpressionException, SQLException {
		ExpressionContext systemCtx = agreementContextFactory
				.getSystemExpressionContext();
		Object value = systemCtx.getVariable(name, startDate, endDate,
				Object.class);
		if (value != null)
			return value;
		return implicitExpressionContext.getVariable(name, startDate, endDate,
				Object.class);
	}

	protected IIrpfCalculatorContext getIrpfCalculatorContext(Connection conn,
			Date startDate, Date endDate, Criteria criteria) {
		try {
			return new SQLIrpfCalculatorContext(connection, startDate, endDate,
					criteria) {
				@Override
				public String getNif() {
					return "87449445H";
				}

				@Override
				public String getApellidosNombre() {
					return "TORVALDS BENEDICT LINUS";
				}

				@Override
				public int getAñoNacimiento() {
					return 1969;
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
			throw new ExpressionExceptionWrapper(new ExpressionException(e));
		} catch (ExpressionException e) {
			throw new ExpressionExceptionWrapper(e);
		}

	}

	protected double getIrpf() {
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(startDate);
		endCalendar.set(Calendar.DAY_OF_YEAR,
				endCalendar.getActualMaximum(Calendar.DAY_OF_YEAR));
		Date endYear = endCalendar.getTime();

		Criteria contractCriteria = new Criteria();
		contractCriteria.addExpression(criteria.getExpression());
		contractCriteria.addEqualExpression(SQLConstants.CONTRACT + "."
				+ ContractColumns.ID, getId());

		IIrpfCalculatorContext irpfCalculatorContext = getIrpfCalculatorContext(
				connection, startDate, endYear, contractCriteria);

		IrpfOutcome irpfOutcome = IrpfCalculator
				.calculateIrpf(irpfCalculatorContext);

		onIrpf(irpfOutcome);

		return irpfOutcome.getIrpfResult().getIrpf();
	}

	protected double getDayVarSalary() {
		return 0.00;

	}

	protected double getDaySalary() throws ExpressionException, SQLException,
			SalaryException {

		Calendar contractEnd = Calendar.getInstance();
		contractEnd.setTime(contractEndDate);
		contractEnd.set(Calendar.DATE, 1);
		Date monthStart = contractEnd.getTime();
		contractEnd.set(Calendar.DATE,
				contractEnd.getActualMaximum(Calendar.DATE));
		Date monthEnd = contractEnd.getTime();

		Criteria contractCriteria = new Criteria();
		contractCriteria.addExpression(criteria.getExpression());
		contractCriteria.addEqualExpression(SQLConstants.CONTRACT + "."
				+ ContractColumns.ID, getId());

		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, monthStart, monthEnd, monthEnd, contractCriteria) {
			// TODO : skip leaves
		};

		ctx.next();

		SalaryBuilder salaryBuilder = new SalaryBuilder();
		ContractSalaryCalculator calculator = new ContractSalaryCalculator();
		calculator.setSalaryBuilder(salaryBuilder);
		ISalary salary = calculator.calculate(ctx);

		double totalPayment = salary.getTotalPayment();
		double extraPayProration = salary.getExtraPayProration();

		return (totalPayment + extraPayProration) * 12 / 365;

	}

	private long getAvailableDays(Date start, Date end) {
		long workedDays = CommonUtil.getDaysBetweenDates(start, end);
		workedDays += 1;
		return workedDays;
	}

	private boolean isActualDay(DayType type, Calendar day) {
		int dayOfWeek = day.get(Calendar.DAY_OF_WEEK);

		if (dayOfWeek == Calendar.SUNDAY) {
			return false;
		}

		return type == DayType.WORKING_DAY || type == DayType.CONTINUOUS_TIME
				|| type == DayType.OTHER; // TODO: Estos tipos de dias son un
											// cachondeo ¿ OTHER,
											// CONTINUOUS_TIME ?
	}

	private boolean isHoliday(Calendar day) {
		Date date = day.getTime();
		ITimedVariable<?> holidays = this.contractExpressionContext
				.getVariable(HOLIDAYS, date, date);
		if (holidays == null)
			return false;

		try {
			Period period = holidays.getPeriod();
			Object value = holidays.getValue(period);
			int days = Integer.parseInt(value.toString());

			Calendar holiday = Calendar.getInstance();
			holiday.setTime(period.getStart());
			holiday.add(Calendar.DAY_OF_MONTH, days);

			return day.compareTo(holiday) <= 0;

		} catch (Error e) {
			return false;
		}

	}

	/*
	 * Calculate 'DIAS_EFECTIVOS' for the contract (employee). Be care of leaves
	 * and agreement.
	 */
	private double getActualDays() {
		long days = 0;

		ICalendar calendar = getCalendar();
		Calendar end = Calendar.getInstance();
		end.setTime(contractEndDate);
		Calendar day = Calendar.getInstance();
		day.setTime(contractStartDate);
		while (end.after(day) || end.equals(day)) {
			DayType type = calendar.getDayType(day);
			if (isActualDay(type, day) && !leaveLoader.isLeaveDay(day)
					&& !isHoliday(day)) {
				days++;
			}
			day.add(Calendar.DATE, 1);
		}
		return days;
	}

	protected Long getLeaveDays(Period p) {
		return leaveLoader.getLeavesDays(p);
	}

	private double getWorkDays(Period p) {

		Long availableDays = getAvailableDays(p.getStart(), p.getEnd());

		Long leaveDays = getLeaveDays(p);

		Long workedDays = availableDays - leaveDays;

		return workedDays; // TODO : Can't be negative
	}

	private double getWorkWeeks(Period p) {
		double workedDays = getWorkDays(p);
		double workedWeeks = workedDays / 7;
		return Math.ceil(workedWeeks);
	}

	private double getExtraDays() {
		Long availableDays = getAvailableDays(startDate, endDate);
		return availableDays;
	}

	private double getExtraWeeks() {
		double extraDays = getExtraDays();
		double extraWeeks = extraDays * 52 / 365;
		return Math.round(extraWeeks);
	}

	private double getExtraMonths() {
		double extraDays = getExtraDays();
		double extraMonths = extraDays * 12 / 365;
		return Math.max(1, Math.round(extraMonths));
	}

	private double getSalaryDays(Date start, Date end) {
		Long availableDays = getAvailableDays(start, end);
		return availableDays;
	}

	private double getSalaryWeeks(Date start, Date end) {
		double salaryDays = getSalaryDays(start, end);
		double salaryWeeks = salaryDays * 52 / 365;
		return Math.round(salaryWeeks);
	}

	private double getSalaryMonths(Date start, Date end) {
		double salaryDays = getSalaryDays(start, end);
		double salaryMonths = salaryDays * 12 / 365;
		return Math.max(1, Math.round(salaryMonths));
	}

	private int getSeniorityYears() {
		Date start = getSeniorityDate();
		Date end = getStartDate();
		return getYears(start, end);
	}

	private double getWorkedYears(Date start, Date end) {

		double years = 0;
		double months = 0;

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(start);

		calendar.add(Calendar.YEAR, 1);
		while (calendar.getTime().compareTo(end) <= 0) {
			years++;
			calendar.add(Calendar.YEAR, 1);
		}

		calendar.add(Calendar.YEAR, -1);

		calendar.add(Calendar.MONTH, 1);
		while (calendar.getTime().compareTo(end) <= 0) {
			months++;
			calendar.add(Calendar.MONTH, 1);
		}

		calendar.add(Calendar.MONTH, -1);
		long days = CommonUtil.getDaysBetweenDates(calendar.getTime(), end);
		if (days > 0)
			months++;

		return years + months / 12d;

	}

	private double getAdvanceNoticeDays() {

		Date advanceNoticeDate = null;

		try {
			advanceNoticeDate = parse(String.valueOf(getVariable(
					"FECHA_PREAVISO", Object.class)));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
		}

		if (advanceNoticeDate == null)
			throw new ExpressionExceptionWrapper(
					new UndefinedVariablesException("FECHA_PREAVISO"));

		return CommonUtil.getDaysBetweenDates(advanceNoticeDate, getEndDate());
	}

	private int getYears(Date start, Date end) {

		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(start);

		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(end);

		int years = 0;

		startCalendar.add(Calendar.YEAR, 1);
		while (startCalendar.compareTo(endCalendar) <= 0) {
			years++;
			startCalendar.add(Calendar.YEAR, 1);
		}

		return years;
	}

	private double getSalaryHours() {
		Number weekHours = getVariable(WEEK_HOURS, Number.class);
		if (weekHours == null) {
			throw new ExpressionExceptionWrapper(
					new UndefinedVariablesException(WEEK_HOURS.getName(),
							SALARY_HOURS.getName()));
		}
		Double salaryDays = getVariable(SALARY_DAYS, Double.class);

		return salaryDays == null ? null : Math.ceil(salaryDays
				* weekHours.doubleValue() / 7); // TODO : ¿ Se redondean las
												// horas hacia arriba ?
	}

	private boolean isIndefinite() {
		String tc2 = getVariable(TC2, String.class);
		if (tc2 == null) {
			throw new ExpressionExceptionWrapper(
					new UndefinedVariablesException(TC2.getName()));
		}
		return ("123".indexOf(tc2.charAt(0)) != -1);
	}

	private boolean isFullTime() {
		String tc2 = getVariable(TC2, String.class);
		if (tc2 == null) {
			throw new ExpressionExceptionWrapper(
					new UndefinedVariablesException(TC2.getName()));
		}
		return ("14".indexOf(tc2.charAt(0)) != -1);
	}

	private boolean isShortContract() {
		Date endDate = getDate(SQLConstants.CONTRACT, ContractColumns.END_DATE);
		if (endDate == null) {
			return false;
		}
		Date startDate = getDate(SQLConstants.CONTRACT,
				ContractColumns.START_DATE);
		long naturalDays = CommonUtil.getDaysBetweenDates(startDate, endDate) + 1;
		if (naturalDays < 7) {
			return true;
		}
		return false;
	}

	private boolean isAssimilatted() {
		Object object = getObject(SQLConstants.ENTERPRISE_CCC,
				EnterpriseCccColumns.TYPE);
		if (object == null) {
			return false;
		}
		Integer ordinal = (Integer) object;
		return ordinal == CCCType.ASSIMILATEDS.ordinal();
	}

	private Double getItRate() {
		Integer cnae2009 = getCnae2009();
		return cnae2009 != null ? this.cnae2009.getItRate(cnae2009) : 0.00;
	}

	private Double getImsRate() {
		Integer cnae2009 = getCnae2009();
		return cnae2009 != null ? this.cnae2009.getImsRate(cnae2009) : 0.00;
	}

	private double getDoubleVariable(String name) {
		Double value = this.contractExpressionContext.getVariable(name,
				this.contractStartDate, this.contractEndDate, Double.class);
		return value != null ? value : 0.00;
	}

	private boolean containsVariable(Object name) {
		return this.contractExpressionContext.containsVariable(name,
				this.contractStartDate, this.contractEndDate);
	}

	private double getTotalBenefitsIt() {
		double totalBenefitsIt = 0.00;

		totalBenefitsIt += getDoubleVariable("ECEMP");
		totalBenefitsIt += getDoubleVariable("ECSS");
		totalBenefitsIt += getDoubleVariable("ATEP");

		return totalBenefitsIt;
	}

	protected double getGuarenteed() {
		double guarenteed = 0.00;

		Set<String> vars = contractExpressionContext.variablesSet();
		for (String var : vars) {
			if (var.endsWith("_GARANTIZADO")) {
				guarenteed += getDoubleVariable(var);
			}
		}

		return guarenteed;
	}

	/*
	 * Inicializa el contexto dentro del cual se calcularán ejecutarán las
	 * percepciones y deducciones de trabajador.
	 */
	protected void initContractExpressionCtx() throws SQLException,
			ExpressionException {

		this.contractStartDate = Period.max(
				getDate(SQLConstants.CONTRACT, ContractColumns.START_DATE),
				startDate);
		this.contractEndDate = Period.min(
				getDate(SQLConstants.CONTRACT, ContractColumns.END_DATE),
				endDate);

		if (this.contractExpressionContext != null) {
			this.contractExpressionContext = null;
		}

		Period period = new Period(startDate, endDate);

		LazyTimedVariable<Double> extraDays = new LazyTimedVariable<Double>() {
			@Override
			public Double create() {
				return getExtraDays();
			}
		};

		ActiveTimedVariable<Double> salaryDays = new ActiveTimedVariable<Double>() {
			@Override
			public Double getValue(Period p) {
				return getSalaryDays(p.getStart(), p.getEnd());
			}
		};

		ActiveTimedVariable<Date> start = new ActiveTimedVariable<Date>() {
			@Override
			public Date getValue(Period p) {
				return p.getStart();
			}
		};

		ActiveTimedVariable<Date> end = new ActiveTimedVariable<Date>() {
			@Override
			public Date getValue(Period p) {
				return p.getEnd();
			}
		};

		ActiveTimedVariable<Double> workedDays = new ActiveTimedVariable<Double>() {
			@Override
			public Double getValue(Period p) {
				return getWorkDays(p);
			}
		};

		ActiveTimedVariable<Double> workedWeeks = new ActiveTimedVariable<Double>() {
			@Override
			public Double getValue(Period p) {
				return getWorkWeeks(p);
			}
		};

		ActiveTimedVariable<Double> bonusDays = new ActiveTimedVariable<Double>() {
			@Override
			public Double getValue(Period p) {
				return (double) getAvailableDays(p.getStart(), p.getEnd());
			}
		};

		ActiveTimedVariable<Double> guarenteedDays = new ActiveTimedVariable<Double>() {
			@Override
			public Double getValue(Period p) {
				return (double) leaveLoader.getCommonDiseaseDays(p)
						+ (double) leaveLoader.getProfessionalDiseaseDays(p);
			}
		};

		LazyTimedVariable<Double> irpf = new LazyTimedVariable<Double>() {
			@Override
			public Double create() {
				try {
					return getIrpf();
				} catch (Throwable t) {
					// TODO : Sure?
					return 0.00;
				}
			}
		};

		this.implicitExpressionContext = new ExpressionContext(
				getAgreementContext(), this);

		// TODO: Tiene que ir aqui ???
		SalaryType salaryType = getSalaryType();
		this.implicitExpressionContext.addVariable(SALARY,
				salaryType == SalaryType.SALARY, startDate, endDate);
		this.implicitExpressionContext.addVariable(SETTLE,
				salaryType == SalaryType.SETTLE, startDate, endDate);
		this.implicitExpressionContext.addVariable(DELAY,
				salaryType == SalaryType.DELAY, startDate, endDate);
		this.implicitExpressionContext.addVariable(EXTRA_PAY,
				salaryType == SalaryType.EXTRA, startDate, endDate);

		this.implicitExpressionContext.addVariable(CONTRACT_START,
				getDate(SQLConstants.CONTRACT, ContractColumns.START_DATE),
				startDate, endDate);
		this.implicitExpressionContext.addVariable(
				CONTRACT_END,
				salaryType == SalaryType.SETTLE ? contractEndDate : getDate(
						SQLConstants.CONTRACT, ContractColumns.END_DATE),
				startDate, endDate);

		this.implicitExpressionContext.addVariable(IRPF_PERCENT, irpf);

		this.implicitExpressionContext.addVariable(START, start);
		this.implicitExpressionContext.addVariable(END, end);

		this.implicitExpressionContext.addVariable(WORKED_DAYS, workedDays);
		this.implicitExpressionContext.addVariable(WORKED_WEEKS, workedWeeks);
		this.implicitExpressionContext.addVariable(QUOTE_DAYS, workedDays);

		this.implicitExpressionContext.addVariable(SALARY_DAYS, salaryDays);

		this.implicitExpressionContext.addVariable(SENIORITY,
				new LazyTimedVariable<Integer>() {
					@Override
					public Integer create() {
						return getSeniorityYears();
					}
				});

		this.implicitExpressionContext.addVariable(SALARY_MONTHS,
				new ActiveTimedVariable<Double>() {
					@Override
					public Double getValue(Period period) {
						return getSalaryMonths(period.getStart(),
								period.getEnd());
					};
				});
		this.implicitExpressionContext.addVariable(SALARY_WEEKS,
				new ActiveTimedVariable<Double>() {
					@Override
					public Double getValue(Period period) {
						return getSalaryWeeks(period.getStart(),
								period.getEnd());
					};
				});

		this.implicitExpressionContext.addVariable(PAY_DAYS, extraDays);

		this.implicitExpressionContext.addVariable(PAY_MONTHS,
				new LazyTimedVariable<Double>() {
					@Override
					public Double create() {
						return getExtraMonths();
					}
				});
		this.implicitExpressionContext.addVariable(PAY_WEEKS,
				new LazyTimedVariable<Double>() {
					@Override
					public Double create() {
						return getExtraWeeks();
					}
				});

		this.implicitExpressionContext.addVariable(SALARY_HOURS,
				new LazyTimedVariable<Double>() {
					@Override
					public Double create() {
						return getSalaryHours();
					}
				});
		this.implicitExpressionContext.addVariable(BONUS_DAYS, bonusDays);

		this.implicitExpressionContext.addVariable(INDEFINITE,
				new LazyTimedVariable<Boolean>() {
					@Override
					public Boolean create() {
						return isIndefinite();
					}
				});

		this.implicitExpressionContext.addVariable(FULL_TIME,
				new LazyTimedVariable<Boolean>() {
					@Override
					public Boolean create() {
						return isFullTime();
					}
				});

		this.implicitExpressionContext.addVariable(ASSIMILATED,
				new LazyTimedVariable<Boolean>() {
					@Override
					public Boolean create() {
						return isAssimilatted();
					}
				});

		this.implicitExpressionContext.addVariable(MORE_THAN_65,
				new LazyTimedVariable<Boolean>() {
					@Override
					public Boolean create() {
						return false;
					}
				});

		this.implicitExpressionContext.addVariable(SHORT_CONTRACT,
				new LazyTimedVariable<Boolean>() {
					@Override
					public Boolean create() {
						return isShortContract();
					}
				});

		this.implicitExpressionContext.addVariable(IT_RATE,
				new LazyTimedVariable<Double>() {
					@Override
					public Double create() {
						return getItRate();
					}
				});

		this.implicitExpressionContext.addVariable(IMS_RATE,
				new LazyTimedVariable<Double>() {
					@Override
					public Double create() {
						return getImsRate();
					}
				});

		this.implicitExpressionContext.addVariable(BONUS_AGE,
				new LazyTimedVariable<Integer>() {
					@Override
					public Integer create() {
						return getYears(sqlContractBonus.getStartDate(),
								contractStartDate);
					}
				});

		this.implicitExpressionContext.addVariable(BONUS_START,
				new LazyTimedVariable<String>() {
					@Override
					public String create() {
						return DATE_FORMAT.format(sqlContractBonus
								.getStartDate());
					}
				});

		this.implicitExpressionContext.addVariable(WORKED_YEARS,
				new ActiveTimedExpressionVariable<Double>(WORKED_YEARS.name(),
						ExpressionScope.CONTRACT) {
					@Override
					public Double getValue(Period period) {
						return getWorkedYears(period.getStart(),
								period.getEnd());
					}
				});
		this.implicitExpressionContext.addVariable("DIAS_PREAVISO",
				new LazyTimedExpressionVariable<Double>("DIAS_PREAVISO",
						ExpressionScope.CONTRACT) {
					@Override
					public Double create() {
						return getAdvanceNoticeDays();
					}
				});

		this.implicitExpressionContext.addVariable("SALARIO_DIA",
				new LazyTimedExpressionVariable<Double>("SALARIO_DIA",
						ExpressionScope.CONTRACT) {
					@Override
					public Double create() {
						try {
							return getDaySalary();
						} catch (Exception e) {
							throw new ExpressionExceptionWrapper(
									new InvalidVariables(e.getMessage(),
											getName()));
						}
					}
				});

		this.implicitExpressionContext.addVariable("SALARIO_VARIABLE_DIA",
				new LazyTimedExpressionVariable<Double>("SALARIO_VARIABLE_DIA",
						ExpressionScope.CONTRACT) {
					@Override
					public Double create() {
						return getDayVarSalary();
					}
				});
		/*
		 * this.implicitExpressionContext.addVariable(COMPENSATION_DAYS, new
		 * LazyTimedVariable<Double>() {
		 * 
		 * @Override public Double create() { return getCompensationDays(); }
		 * });
		 */

		this.contractExpressionContext = new ExpressionContext(
				this.implicitExpressionContext, this);

		this.contractExpressionContext.addVariable(CONTEXT,
				contractExpressionContext, startDate, endDate);
		this.contractExpressionContext.addVariable(SELF, this, startDate,
				endDate);

		loadExpression(this.contractExpressionContext, BR,
				"def(x){ SELF.br(x)};", this.startDate, this.endDate);
		loadContractLeave(this.contractExpressionContext);
		loadContractData(this.contractExpressionContext);
		loadPersonData(this.contractExpressionContext);

		loadExpression(this.contractExpressionContext, GROSS,
				"def(x){ SELF.gross(x)};", this.startDate, this.endDate);
		loadExpression(this.contractExpressionContext, LIQUID,
				"def(x){ SELF.liquid(x)};", this.startDate, this.endDate);
		loadExpression(this.contractExpressionContext, SYSTEM,
				"def(x){ SELF.system(x)};", this.startDate, this.endDate);
		loadExpression(this.contractExpressionContext, AGREEMENT,
				"def(x){ SELF.agreement(x)};", this.startDate, this.endDate);
		loadExpression(this.contractExpressionContext, GUARANTEE,
				"def(x){ SELF.guarantee(x)};", this.startDate, this.endDate);

		if (!containsVariable(ACTUAL_DAYS)) {
			// Los 'DIAS_EFECTIVOS' son pesados de calcular ( necesitan de
			// querys adicionales...)
			this.contractExpressionContext.addVariable(ACTUAL_DAYS,
					new LazyTimedVariable<Double>() {
						@Override
						public Double create() {
							return getActualDays();
						}
					});
		}

		long leaveDays = leaveLoader.getLeavesDays(period);

		this.contractExpressionContext.addVariable(LEAVE_DAYS, leaveDays,
				this.startDate, this.endDate);

		if (leaveDays == 0) {
			this.contractExpressionContext.addVariable(GUARANTEED_DAYS, 0,
					this.startDate, this.endDate);
			return;
		} // Si no hay bajas...

		/*
		 * long guarenteedDays = leaveLoader.getCommonDiseaseDays(period) +
		 * leaveLoader.getProfessionalDiseaseDays(period);
		 * 
		 * this.contractExpressionContext.addVariable(GUARANTEED_DAYS,
		 * guarenteedDays, this.startDate, this.endDate );
		 */

		this.contractExpressionContext.addVariable(GUARANTEED_DAYS,
				guarenteedDays);

		double guaranteed = getGuarenteed();

		this.contractExpressionContext.addVariable(GUARANTEED, guaranteed,
				this.startDate, this.endDate);

		if (guaranteed == 0.00) {
			return;
		}

		this.contractExpressionContext.addVariable(TOTAL_BENEFITS_IT,
				new LazyTimedVariable<Double>() {
					@Override
					public Double create() {
						return getTotalBenefitsIt();
					}
				});

	}

	/*
	 * Carga, ejecuta los datos del contrato 'contract_data' para este periodo.
	 * Ejecuta porque al valor de una variable no tiene porque ser un literal,
	 * puede ser una expresión ej : '15 / 100' o 'DIAS_TRABAJADOS * 0.01'
	 */
	private void loadContractData(ExpressionContext ctx) throws SQLException {
		loadContractData(ctx, contractStartDate, contractEndDate);
		Date irpfDate = getIrpfDate();
		if (irpfDate != null && irpfDate.after(contractEndDate)) {
			loadContractData(ctx, irpfDate, irpfDate);
		}
	}

	private void loadExpression(ExpressionContext ctx, String name,
			String script, Date start, Date end) throws SQLException {
		ExpressionImpl expr = new ExpressionImpl();
		expr.setName(name);
		expr.setExpression(script);
		expr.setScope(ExpressionScope.CONTRACT);
		try {
			ctx.addExpression(expr, start, end);
		} catch (Exception ignore) {
			ignore.printStackTrace();
			// TODO: ¿ Que hacemos con esta excepcion ?
		}
	}

	/*
	 * Carga, ejecuta los datos del contrato 'contract_data' para este periodo.
	 * Ejecuta porque al valor de una variable no tiene porque ser un literal,
	 * puede ser una expresión ej : '15 / 100' o 'DIAS_TRABAJADOS * 0.01'
	 */
	private void loadContractData(ExpressionContext ctx, Date startDate,
			Date endDate) throws SQLException {
		ResultSet rs = null;
		try {
			List<ITimedObject<IExpression>> failed = new LinkedList<ITimedObject<IExpression>>();

			ceventStmt.setInt(1, getId());
			ceventStmt.setDate(2, toSqlDate(endDate));
			ceventStmt.setDate(3, toSqlDate(startDate));
			rs = ceventStmt.executeQuery();
			while (rs.next()) {
				ExpressionImpl expr = new ExpressionImpl();
				expr.setName(rs.getString(ContractDataColumns.NAME));
				expr.setExpression(rs.getString(ContractDataColumns.EXPRESSION));
				expr.setScope(ExpressionScope.CONTRACT);
				Date dataStart = rs.getDate(ContractDataColumns.START_DATE);
				Date dataEnd = rs.getDate(ContractDataColumns.END_DATE);
				Date start = Period.max(dataStart, startDate);
				Date end = Period.min(dataEnd, endDate);
				try {
					ctx.addExpression(expr, start, end);
				} catch (UndefinedVariablesException e) {
					failed.add(new TimedObject<IExpression>(expr, new Period(
							start, end)));
				} catch (CheckException e) {

				} catch (Exception e) {
					// TODO: ¿ Que hacemos con esta excepcion ?
				}
			}

			for (ITimedObject<IExpression> timedExpr : failed) {
				try {
					Period period = timedExpr.getPeriod();
					IExpression expr = timedExpr.getValue();
					ctx.addExpression(expr, period.getStart(), period.getEnd());
				} catch (UndefinedVariablesException e) {
					onUndefinedData(timedExpr.getValue(), e.getMessage(),
							timedExpr.getPeriod().getStart(), timedExpr
									.getPeriod().getEnd(), e.getVariableNames());
				} catch (Exception e) {
				}
			}

		} finally {
			if (rs != null) {
				rs.close();
			}
		}
	}

	protected void onIrpf(IrpfOutcome irpfOutcome) {
		if (listener != null)
			listener.onIrpf(irpfOutcome);

	}

	protected void onUndefinedData(IExpression expression, String message,
			Date start, Date end, String... variables) {
		if (listener != null)
			for (String variable : variables)
				listener.onUndefinedData(expression, variable, message, start,
						end);

	}

	private void loadPersonData(ExpressionContext ctx) throws SQLException {

		ctx.addVariable(MALE, Gender.MALE.ordinal(), contractStartDate,
				contractEndDate);
		ctx.addVariable(FEMALE, Gender.FEMALE.ordinal(), contractStartDate,
				contractEndDate);
		Integer gender = getInt(SQLConstants.PERSON, PersonColumns.GENDER);
		if (gender != null) {
			ctx.addVariable(GENDER, gender, contractStartDate, contractEndDate);
		}
		Date birthDate = getDate(SQLConstants.PERSON, PersonColumns.BIRTH_DATE);
		if (birthDate != null) {
			int age = getYears(birthDate, contractStartDate);
			ctx.addVariable(AGE, age, contractStartDate, contractEndDate);
		}

	}

	private void loadContractLeave(ExpressionContext ctx) throws SQLException,
			ExpressionException {
		ResultSet rs = null;
		try {
			cleaveStmt.setInt(1, getId());
			rs = cleaveStmt.executeQuery();
			leaveLoader.loadContractLevae(rs, ctx);
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
	}

	private void initSystemCosts() throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(SYSTEM_COST_SQL);
			java.sql.Date sqlEndDate = new java.sql.Date(this.endDate.getTime());
			java.sql.Date sqlStartDate = new java.sql.Date(
					this.startDate.getTime());
			stmt.setDate(1, sqlEndDate);
			stmt.setDate(2, sqlStartDate);
			stmt.setInt(3, SQLPayrollConstants.DOMAIN_ZERO);
			rs = stmt.executeQuery();
			systemCosts = SQLCollections.costsCollection(rs);
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

	private void initSystemDeductions() throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(SYSTEM_DEDUCTION_SQL);
			java.sql.Date sqlEndDate = new java.sql.Date(this.endDate.getTime());
			java.sql.Date sqlStartDate = new java.sql.Date(
					this.startDate.getTime());
			stmt.setDate(1, sqlEndDate);
			stmt.setDate(2, sqlStartDate);
			stmt.setInt(3, SQLPayrollConstants.DOMAIN_ZERO);
			rs = stmt.executeQuery();
			systemDeductions = SQLCollections.deductionsCollection(rs);
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

	private void initSystemPayments() throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			String sql = CriteriaUtilities.toSQLString(paymentsCriteria,
					SYSTEM_PAYMENT_SQL);
			stmt = connection.prepareStatement(sql);
			java.sql.Date sqlEndDate = new java.sql.Date(this.endDate.getTime());
			java.sql.Date sqlStartDate = new java.sql.Date(
					this.startDate.getTime());
			stmt.setDate(1, sqlEndDate);
			stmt.setDate(2, sqlStartDate);

			for (int i = 0; i < SSRegimeType.class.getEnumConstants().length; i++) {
				stmt.setInt(i + 3, i * (-1));
			}

			rs = stmt.executeQuery();
			systemPayments = SQLCollections.systemPaymentsCollection(rs);
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

	/**
	 * ORDER BY literal.
	 */
	private static final String ORDER_BY = " ORDER BY "; //$NON-NLS-1$

	protected static String orderBy(String stmt, OrderByList orderBy) {
		StringBuffer buffer = new StringBuffer(stmt);

		if (orderBy == null || orderBy.size() == 0) {
			return buffer.toString();
		}

		if (buffer.indexOf(ORDER_BY) == -1) {
			buffer.append(ORDER_BY);
		} else {
			buffer.append(", ");
		}

		buffer.append(orderBy.toString());
		return buffer.toString();
	}

	protected static ISalary getSalary(Connection connection, Date date,
			SalaryType type, Integer contractID) throws SQLException,
			ExpressionException, SalaryException {
		Date startDate = CommonUtil.getMonthFirstDay(date);

		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(date);
		endCalendar.add(Calendar.DAY_OF_MONTH, -1);
		Date endDate = endCalendar.getTime();

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(SQLConstants.CONTRACT + "."
				+ ContractColumns.ID, contractID);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		if (!ctx.next())
			return null;

		SalaryBuilder salaryBuilder = new SalaryBuilder();
		ContractSalaryCalculator calculator = new ContractSalaryCalculator();
		calculator.setSalaryBuilder(salaryBuilder);
		return calculator.calculate(ctx);

	}

	protected static ISalary getDbSalary(Connection connection, Date date,
			SalaryType type, Integer contractID) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement("SELECT * " + " FROM "
					+ SQLConstants.SALARY + " WHERE " + SalaryColumns.CONTRACT
					+ "= ? " + " AND " + SalaryColumns.TYPE + "= ? " + " AND "
					+ SalaryColumns.START_DATE + " <= ? " + " AND "
					+ SalaryColumns.END_DATE + " >= ? ");

			stmt.setInt(1, contractID);
			stmt.setInt(2, type.ordinal());
			java.sql.Date sqlDate = toSqlDate(date);
			stmt.setDate(3, sqlDate);
			stmt.setDate(4, sqlDate);

			rs = stmt.executeQuery();
			if (!rs.next())
				return null;

			Salary salary = new Salary();
			salary.setStartDate(rs.getDate(SalaryColumns.START_DATE));
			salary.setEndDate(rs.getDate(SalaryColumns.END_DATE));
			salary.setTimeUnits(rs.getInt(SalaryColumns.TIME_UNITS));
			salary.setTotalPayment(rs.getDouble(SalaryColumns.TOTAL_PAYMENT));
			salary.setTotalDeduction(rs
					.getDouble(SalaryColumns.TOTAL_DEDUCTION));
			salary.setTotalLiquid(rs.getDouble(SalaryColumns.TOTAL_LIQUID));
			salary.setTotalEnterprise(rs
					.getDouble(SalaryColumns.TOTAL_ENTERPRISE));
			salary.setIssueDate(rs.getDate(SalaryColumns.ISSUE_DATE));
			salary.setRemuneration(rs.getDouble(SalaryColumns.REMUNERATION));
			salary.setExtraPayProration(rs
					.getDouble(SalaryColumns.PRO_EXT_BASE));
			salary.setItBase(rs.getDouble(SalaryColumns.IT_BASE));
			salary.setRawCommonBase(rs.getDouble(SalaryColumns.RAW_CGC_BASE));
			salary.setCommonBase(rs.getDouble(SalaryColumns.CGC_BASE));
			salary.setOvertimeBase(rs.getDouble(SalaryColumns.HEXTRA_BASE));
			salary.setNonEstructuralOvertimeBase(rs
					.getDouble(SalaryColumns.NON_HEXTRA_BASE));
			salary.setProfessionalBase(rs.getDouble(SalaryColumns.CGP_BASE));
			salary.setMoneyIrpfBase(rs.getDouble(SalaryColumns.MONEY_IRPF_BASE));
			salary.setInkindIrpfBase(rs
					.getDouble(SalaryColumns.INKIND_IRPF_BASE));
			salary.setIrpfBase(rs.getDouble(SalaryColumns.IRPF_BASE));
			salary.setSocialSecurityContributions(rs
					.getDouble(SalaryColumns.SOCIAL_SECURITY_CONTRIBUTIONS));
			salary.setTotalIrpf(rs.getDouble(SalaryColumns.TOTAL_IRPF));
			salary.setChargeDate(rs.getDate(SalaryColumns.CHARGE_DATE));
			return salary;

		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

}
