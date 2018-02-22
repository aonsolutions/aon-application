package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.calculator.sql.SQLSystemExpressionContextFactory.DEFAULT_AGRREEMENT_HOURS;
import static com.esferalia.aon.payroll.calculator.sql.SQLSystemExpressionContextFactory.DEFAULT_DAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ACTUAL_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.AGE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.AGREEMENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.AGREEMENT_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ASSIMILATED;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.BONUS_AGE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.BONUS_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.BONUS_START;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.BR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CONTEXT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CONTRACT_END;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CONTRACT_START;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.DELAY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.DIRECT_PAY_START;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.END;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.EVERYTHING;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.EXTRA_PAY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FEMALE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FRIDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FULL_TIME;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.GENDER;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.GROSS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.GUARANTEE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.GUARANTEED_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.HOLIDAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.IMS_RATE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.INDEFINITE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.IRPF_PERCENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.IT_RATE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.IT_START;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.LEAVE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.LIQUID;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MALE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MORE_THAN_65;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.NATURAL_MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.OCCUPATION;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PARTIAL_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PATERNITY_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PAYMENT_VARIABLE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PAY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_GROUP;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.REDEFINE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.REGULATORY_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SALARY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SALARY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SALARY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SATURDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SELF;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SENIORITY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SENIORITY_START;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SETTLE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SHORT_CONTRACT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.START;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRIKE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRIKE_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SUNDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SYSTEM;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.THURSDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TUESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WEDNESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WEEK_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_YEARS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.parse;
import static com.esferalia.aon.salary.expression.ExpressionContext.getCurrentBindings;
import static com.esferalia.aon.watson.util.AonDateUtils.add;
import static com.esferalia.aon.watson.util.AonDateUtils.getMax;
import static com.esferalia.aon.watson.util.AonUtils.ifnull;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.FRIDAY;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.THURSDAY;
import static java.util.Calendar.TUESDAY;
import static java.util.Calendar.WEDNESDAY;
import static java.util.stream.Collectors.summingDouble;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BisectionSolver;
import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.apache.commons.math3.analysis.solvers.IllinoisSolver;
import org.apache.commons.math3.analysis.solvers.MullerSolver;
import org.apache.commons.math3.analysis.solvers.MullerSolver2;
import org.apache.commons.math3.analysis.solvers.PegasusSolver;
import org.apache.commons.math3.analysis.solvers.UnivariateSolver;
import org.hibernate.event.def.OnLockVisitor;
import org.mvel2.util.MethodStub;

import com.code.aon.AonVersion;
import com.code.aon.common.AonException;
import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.person.enumeration.Gender;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.OrderByList;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.calendar.enumeration.DayType;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.DelegateCollection;
import com.esferalia.aon.payroll.IrpfOutcome;
import com.esferalia.aon.payroll.Pair;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.AbstractContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.CompositeCosts;
import com.esferalia.aon.payroll.calculator.CompositePayments;
import com.esferalia.aon.payroll.calculator.ContextFunctions;
import com.esferalia.aon.payroll.calculator.ContractLeaveLoader.Leave;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.DomainPayments;
import com.esferalia.aon.payroll.calculator.HierarchyDeductions;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.payroll.calculator.IContractCost;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractEmbargo;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.IHasPayment;
import com.esferalia.aon.payroll.calculator.ISystemCost;
import com.esferalia.aon.payroll.calculator.ISystemDeduction;
import com.esferalia.aon.payroll.calculator.ISystemPayment;
import com.esferalia.aon.payroll.calculator.LRUCache;
import com.esferalia.aon.payroll.calculator.OnlyPaymentContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.SalaryExpressionException;
import com.esferalia.aon.payroll.calculator.UndefinedContextVariablesException;
import com.esferalia.aon.payroll.calculator.UndefinedTotalPaymentException;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.LeaveTypeVisitor;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext;
import com.esferalia.aon.payroll.irpf.IrpfCalculator;
import com.esferalia.aon.payroll.irpf.sql.SQLIrpfCalculatorContext;
import com.esferalia.aon.payroll.sql.AbstractSQL;
import com.esferalia.aon.payroll.sql.AbstractSQL.ContractData;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementLevelCategoryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractLeaveColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractPaymentColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseActivityColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseCccColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PayrollWorkplaceColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PersonColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RegistryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryColumns;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.CheckException;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionContext.DeferredExpressionException;
import com.esferalia.aon.salary.expression.ExpressionContext.ExpressionExceptionWrapper;
import com.esferalia.aon.salary.expression.ExpressionContext.MacroException;
import com.esferalia.aon.salary.expression.ExpressionContext.RemovedExpressionVariable;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionImpl;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IConstantVariable;
import com.esferalia.aon.salary.expression.IExpression;
import com.esferalia.aon.salary.expression.IExpressionVariable;
import com.esferalia.aon.salary.expression.ITimedObject;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.InterruptedException;
import com.esferalia.aon.salary.expression.InvalidVariables;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.RemoveException;
import com.esferalia.aon.salary.expression.TimedObject;
import com.esferalia.aon.salary.expression.TimedResult;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;
import com.esferalia.aon.salary.expression.Variables.NotFoundHandler;
import com.esferalia.aon.salary.expression.Variables.PeriodMap;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.AonUtils;
import com.google.api.client.repackaged.com.google.common.base.Throwables;

public class SQLContractSalaryCalculatorContext extends AbstractContractSalaryCalculatorContext
		implements IContractSalaryCalculatorContext, NotFoundHandler, ISQLContractSalaryCalculatorContext {

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

	private static final SSRegimeType SS_REGIMES[] = SSRegimeType.class.getEnumConstants();

	// @formatter:off
	private static final String MAIN_SQL = "SELECT * " + " FROM contract"
			+ " LEFT JOIN enterprise_ccc ON (contract.enterprise_ccc = enterprise_ccc.id)"
			+ " LEFT JOIN enterprise_activity ON (contract.enterprise_activity = enterprise_activity.id)"
			+ " LEFT JOIN agreement_level ON (contract.agreement_level = agreement_level.id)"
//			+ " LEFT JOIN agreement_level_category ON (contract.agreement_level_category = agreement_level_category.id)"
//			+ " LEFT JOIN agreement_level ON (agreement_level.id = agreement_level_category.agreement_level)"
			+ " LEFT JOIN agreement ON (agreement.id = agreement_level.agreement)" + ", person" + ", registry AS "
			+ PERSON_REGISTRY + ", workplace"
			+ " LEFT JOIN payroll_workplace ON (payroll_workplace.workplace = workplace.id)" + ", enterprise"
			+ ", registry AS " + ENTERPRISE_REGISTRY + " LEFT JOIN customer ON (customer.registry = "
			+ ENTERPRISE_REGISTRY + ".id)" + ", raddress" + " WHERE contract.person = person.registry" // INNER
																										// JOIN:
																										// person
																										// is
																										// NOT
																										// NULL
			+ " AND person.registry = person_registry.id" // INNER JOIN: //
															// registry is NOT
															// NULL
			+ " AND contract.workplace = workplace.id" // INNER JOIN: workplace
														// is NOT NULL
			+ " AND workplace.enterprise = enterprise.registry" // INNER JOIN:
																// enterprise is
																// NOT NULL
			+ " AND enterprise.registry = enterprise_registry.id" // INNER JOIN:
																	// registry
																	// is NOT
																	// NULL
			+ " AND workplace.address = raddress.id" // INNER JOIN: address is
														// NOT NULL
			+ " AND contract.start_date <= ? " + " AND ( contract.end_date  IS NULL" + " OR contract.end_date >= ? )";
	// @formatter:on

	private static final String PAYMENT_SQL = "SELECT * " + ", " + ExpressionScope.CONTRACT.ordinal() + " AS "
			+ SQLContractPayment.SCOPE_ALIAS + " FROM contract_payment AS " + SQLContractPayment.PAYMENT_ALIAS
			+ " LEFT JOIN  payment_concept" // LEFT
											// JOIN:
											// payment_concept
											// puede
											// ser
											// NULL
			+ "	ON payment_concept = payment_concept.id" + " WHERE contract = ? " + " AND start_date <= ? "
			+ " AND ( end_date IS NULL " + " OR end_date >= ? )";

	private static final String PAYMENTS_FILTER[] = {
			" AND salary_type IN (" + SalaryType.SALARY.ordinal() + " ," + SalaryType.EXTRA.ordinal() + ")", // SalaryType.SALARY
			" AND salary_type = " + SalaryType.EXTRA.ordinal() + " ", // SalaryType.EXTRA
			" AND salary_type = " + SalaryType.SETTLE.ordinal() + " ", // SalaryType.SETTLE
			" AND salary_type = " + SalaryType.DELAY.ordinal() + " ", // SalaryType.DELAY
			" AND salary_type = " + SalaryType.NOT_ENJOYED_VACATIONS.ordinal() + " ", // SalaryType.NOT_ENJOYED_VACATIONS
	};
	// " AND salary_type IN ("+SalaryType.SALARY.ordinal()+"
	// ,"+SalaryType.EXTRA.ordinal()+")"

	private static final String DEDUCTION_SQL = "SELECT *" + ", " + ExpressionScope.CONTRACT.ordinal() + " AS "
			+ SQLContractDeduction.SCOPE_ALIAS + " FROM contract_deduction" + " LEFT JOIN  deduction_concept" // LEFT
																												// JOIN:
																												// deduction_concept
																												// puede
																												// ser
																												// NULL
			+ "	ON deduction_concept = deduction_concept.id" + " WHERE contract = ? " + " AND start_date <= ? "
			+ " AND ( end_date IS NULL" + " OR end_date >= ? )";

	private static final String BONUS_SQL = "SELECT *" + " FROM contract_bonus" + " LEFT JOIN  bonus_concept" // LEFT
																												// JOIN:
																												// bonus_concept
																												// puede
																												// ser
																												// NULL
			+ "	ON bonus_concept = bonus_concept.id" + " WHERE contract = ? " + " AND start_date <= ? "
			+ " AND ( end_date IS NULL" + " OR end_date >= ? )";

	private static final String EMBARGO_SQL = "SELECT *"
			+ ", ( SELECT sum(amount) FROM salary_embargo WHERE contract_embargo=contract_embargo.id  ) AS "
			+ EMBARGO_PAID + " FROM contract_embargo" + " WHERE contract = ? " + " AND start_date <= ? "
			+ " AND ( end_date IS NULL" + " OR end_date >= ? )" + " ORDER BY start_date"; // ORDER
																							// BY
																							// :
																							// El
																							// primero
																							// que
																							// llega
																							// cobra

	private static final String SYSTEM_COST_SQL = "SELECT *" + " FROM system_cost" + " WHERE start_date <= ? "
			+ " AND ( end_date IS NULL" + " OR end_date >= ? )" + " AND system_cost.domain <= 0 ";

	private static final String SYSTEM_DEDUCTION_SQL = "SELECT *" + ", " + ExpressionScope.SYSTEM.ordinal() + " AS "
			+ SQLContractDeduction.SCOPE_ALIAS + " FROM system_deduction" + " LEFT JOIN  deduction_concept" // LEFT
																											// JOIN:
																											// deduction_concept
																											// puede
																											// ser
																											// NULL
			+ "	ON deduction_concept = deduction_concept.id" + " WHERE start_date <= ? " + " AND ( end_date IS NULL"
			+ " OR end_date >= ? )" + " AND system_deduction.domain <= 0 ";

	private static final String SYSTEM_PAYMENT_SQL = "SELECT *" + ", " + ExpressionScope.SYSTEM.ordinal() + " AS "
			+ SQLContractPayment.SCOPE_ALIAS + " FROM system_payment AS " + SQLContractPayment.PAYMENT_ALIAS
			+ " LEFT JOIN  payment_concept" // LEFT
											// JOIN:
											// payment_concept
											// puede
											// ser
											// NULL
			+ "	ON payment_concept = payment_concept.id" + " WHERE start_date <= ? " + " AND ( end_date IS NULL"
			+ " OR end_date >= ? )" + " AND " + SQLContractPayment.PAYMENT_ALIAS + ".domain <= 0 ";

	private static final String CDATA_SQL = "SELECT * " + " FROM contract_data" + " WHERE contract = ? "
			+ "AND start_date <= ? " + " AND ( end_date IS NULL " + " OR end_date >= ? )"
			+ " ORDER BY IF( name LIKE '%_GARANTIZADO',1,0)"
	// + ", IF(ISNULL(end_date),0,1) ASC,end_date DESC "
	// //IF(ISNULL(end_date),0,1),end_date DESC
	;

	public static final String CLEAVE_SQL_PARENT_DAYS = "dias";

	private static final String CLEAVE_SQL = 
			"SELECT contract_leave.* ," 
				+ "( SELECT sum(DATEDIFF(end_date,start_date)+1)"
				+ " FROM contract_leave AS parent" 
				+ " WHERE ( parent.id=contract_leave.parent"
				+ " OR parent=contract_leave.parent )" 
				+ " AND parent.start_date < contract_leave.start_date )" 
				+ " AS "+ CLEAVE_SQL_PARENT_DAYS 
				+ ", contract_data.* "
			+ " FROM contract_leave" 
			+ " LEFT JOIN contract_data ON ( "
				+ " contract_leave.contract = contract_data.contract "
				+ " AND contract_data.name IN ('" + PATERNITY_FACTOR + "','" +MATERNITY_FACTOR + "','" +DIRECT_PAY_START + "')"
				+ " AND ( contract_leave.end_date  IS NULL OR contract_data.start_date <= contract_leave.end_date )"
				+ " AND ( contract_data.end_date IS NULL OR contract_data.end_date >= contract_leave.start_date ) "
				+ ")"
			+ " WHERE contract_leave.contract = ? " 
			+ " AND contract_leave.start_date <= ? "
			+ " AND ( contract_leave.end_date IS NULL " + " OR contract_leave.end_date >= ? )";

	private static final int CACHE_SIZE = 25;

	private static final List<ContextVariable> DAYS_CONTEXT_VARIABLES = Arrays.asList(new ContextVariable[] {
			WEEK_HOURS, ERE_DAYS, QUOTE_DAYS, WORKED_DAYS, SALARY_DAYS, PARTIAL_FACTOR, REGULATORY_BASE });

	private static final Map<Integer, ContextVariable> WEEK_HOURS_VARIABLES = new HashMap<Integer, ContextVariable>() {
		{
			put(SUNDAY, SUNDAY_HOURS);
			put(MONDAY, MONDAY_HOURS);
			put(TUESDAY, TUESDAY_HOURS);
			put(WEDNESDAY, WEDNESDAY_HOURS);
			put(THURSDAY, THURSDAY_HOURS);
			put(FRIDAY, FRIDAY_HOURS);
			put(SATURDAY, SATURDAY_HOURS);
		}
	};

	public static interface NextHook {
		default void beforeLoadLeaves(ExpressionContext ctx) throws ExpressionException{};
		void beforeLoadDaysContextVariables(ExpressionContext ctx) throws ExpressionException;
	}

	private class ContractExpressionContext extends ExpressionContext {

		private ContractExpressionContext(ExpressionContext expressionContext, NotFoundHandler notFoundHandler) {
			super(expressionContext, notFoundHandler);
		}

		@Override
		public <T> List<ITimedResult<T>> eval(String script, Date start, Date end, Class<T> toType)
				throws ExpressionException, UndefinedVariablesException {
			try {
				script = zeroGuarantee(script);
				return super.eval(script, start, end, toType);
			} catch (UndefinedVariablesException e) {

				if (e.getExpression() == null)
					e.setExpression(script);
				throw e;
			}
		}

		// --------------------------------------------------------------------
		protected String zeroGuarantee(String script) {
			if (script == null)
				return null;
			return script.replaceAll(String.format("%s\\w*\\(([^(),]|\\(([^\\)]*)\\))*", GUARANTEE),
					String.format("%s(0", GUARANTEE));

		}

	}

	public static class PaymentVariable implements IHasPayment<IContractPayment> {

		private IContractPayment payment;

		public PaymentVariable(IContractPayment payment) {
			this.payment = payment;
		}

		public Integer getMES() {
			return payment.getMonth() != null ? payment.getMonth().getValue() + 1 : null;
		}
		
		@Override
		public IContractPayment getPayment() {
			return payment;
		}

	}

	public static class AgreementContextKey {

		private Integer domain;
		private Integer agreementId;
		private Integer agreementLevelId;

		public AgreementContextKey(Integer domain, Integer agreementId, Integer agreementLevelId) {
			this.domain = domain;
			this.agreementId = agreementId;
			this.agreementLevelId = agreementLevelId;
		}

		public Integer getDomain() {
			return domain;
		}

		public Integer getAgreementId() {
			return agreementId;
		}

		public Integer getAgreementLevelId() {
			return agreementLevelId;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof AgreementContextKey && AonUtils.equals(domain, ((AgreementContextKey) obj).domain)
					&& AonUtils.equals(agreementId, ((AgreementContextKey) obj).agreementId)
					&& AonUtils.equals(agreementLevelId, ((AgreementContextKey) obj).agreementLevelId);
		}

		@Override
		public int hashCode() {
			return AonUtils.hashCode(domain) + AonUtils.hashCode(agreementId) + AonUtils.hashCode(agreementLevelId);
		}

	}

	public static class CCCContextKey {

		private CCCType cccType;
		private SSRegimeType ssRegime;

		public CCCContextKey(CCCType cccType, SSRegimeType ssRegime) {
			this.cccType = cccType;
			this.ssRegime = ssRegime;
		}

		public CCCType getCCC() {
			return cccType;
		};

		public SSRegimeType getSSRegime() {
			return ssRegime;
		};

	}

	protected static class GuarenteeException extends SalaryException {

		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		List<ITimedResult<Double>> guarentees;

		public GuarenteeException(List<ITimedResult<Double>> guarentees) {
			this.guarentees = guarentees;
		}

		public List<ITimedResult<Double>> getGuarentees(Period p) {
			List<ITimedResult<Double>> list = new ArrayList<ITimedResult<Double>>();

			guarentees.stream().filter(result -> p.contains(result.getPeriod())).forEach(list::add);

			return list;
		}

		public List<ITimedResult<Double>> getGuarentee(Date start, Date end) {
			return getGuarentees(new Period(start, end));
		}

	}

	private static class ExtraDays implements ITimedVariable<Number> {

		private ITimedVariable<Number> monthDays;

		public ExtraDays(ITimedVariable<Number> monthDays) {
			this.monthDays = monthDays;
		}

		@Override
		public Period getPeriod() {
			return monthDays.getPeriod();
		}

		@Override
		public Number getValue(Period period) {
			Number value = monthDays.getValue(period);
			return value != null ? value.doubleValue() * 12 : null;
		}

	}

	public static class SQLGuaranteeContractSalaryCalculatorContext extends SQLNoItContractSalaryCalculatorContext {

		protected static interface Listener {
			void onGuarantee(double guarantee, int start, int end);
		}

		private Listener listener;

		public SQLGuaranteeContractSalaryCalculatorContext(Connection connection, Date startDate, Date endDate,
				Date issueDate, Criteria criteria, int start, int end) throws SQLException, ExpressionException {
			super(connection, startDate, endDate, issueDate, criteria, start, end);
		}

		@Override
		public Object guarantee(double guarantee, int start, int end) throws ExpressionException {
			if (this.start == start && this.end == end) {
				listener.onGuarantee(guarantee, start, end);
				return guarantee;
			} else {
				return super.guarantee(guarantee);
			}
		}

	}

	public static class SQLNoItContractSalaryCalculatorContext extends SQLContractSalaryCalculatorContext {

		protected int end;
		protected int start;
		protected int guaranteed;
		protected Date lastLeaveEnd = new Date(0);
		protected List<Period> guaranteePeriods = new ArrayList<Period>();
		protected List<ITimedResult<Double>> guarentees = new ArrayList<ITimedResult<Double>>();

		public SQLNoItContractSalaryCalculatorContext(Connection connection, final Date startDate, final Date endDate,
				Date issueDate, Criteria criteria, final int start, final int end)
				throws SQLException, ExpressionException {

			super(connection, startDate, endDate, issueDate, criteria);
			this.start = start + 1;
			this.end = end + 1;
			// with this, we assure no leave I.T.
			super.leaveLoader = new SQLContractLeaveLoader(startDate, endDate) {

				@Override
				public void loadContractLeave(Integer id, Date leaveStart, Date leaveEnd, long parentDays,
						LeaveType type, Double dailyRegBase, ExpressionContext exprCtx) throws ExpressionException {

					if (start < 0)
						return;

					Period leavePeriod = new Period(leaveStart, leaveEnd);

					Calendar leaveCalendar = Calendar.getInstance();
					leaveCalendar.setTime(leaveStart);
					leaveCalendar.add(Calendar.DATE, start - (int) parentDays);
					Date guarenteeStart = leaveCalendar.getTime();
					leaveCalendar.add(Calendar.DATE, end - start);
					Date guarenteeEnd = Period.min(leaveEnd, leaveCalendar.getTime());

					lastLeaveEnd = Period.max(leaveEnd, lastLeaveEnd);

					Period guarenteePeriod = new Period(guarenteeStart, guarenteeEnd);

					SQLNoItContractSalaryCalculatorContext.this.guaranteePeriods.add(guarenteePeriod);
					SQLNoItContractSalaryCalculatorContext.this.guaranteed++;

					try {
						Method guarantee = SQLContractSalaryCalculatorContext.class.getMethod("guaranteee",
								double.class);
						exprCtx.setVariable(GUARANTEE, new MethodStub(guarantee), guarenteeStart, guarenteeEnd);
					} catch (SecurityException e) {
					} catch (NoSuchMethodException e) {
					}

					List<Period> leavePeriods = leavePeriod.sub(guarenteePeriod);
					for (Period p : leavePeriods) {
						long leaveParentDays = CommonUtil.getDaysBetweenDates(leaveStart, p.getStart());
						super.loadContractLeave(id, p.getStart(), p.getEnd(), parentDays + leaveParentDays, type,
								dailyRegBase, exprCtx);
					}

					// Adds 'BASE_REGULADORA' variable for guaranteed period
					exprCtx.setVariable(IT_START, leaveStart, guarenteeStart, guarenteeEnd);
					
					int guaranteedDays = (int) getGuaranteedDays(exprCtx,
							new Period(Period.max(guarenteeStart, startDate), guarenteeEnd));
					exprCtx.setVariable(GUARANTEED_DAYS, guaranteedDays, guarenteeStart, guarenteeEnd);

					ExpressionImpl exp = new ExpressionImpl();
					exp.setName(EVERYTHING.getName());
					if (dailyRegBase != null) {
						exp.setExpression(String.format("%f * %d ", dailyRegBase, guaranteedDays));
					} else {
						exp.setExpression(String.format("SELF.br(%s) * %d", IT_START, guaranteedDays));
					}
					exprCtx.addLazyExpression(exp, guarenteeStart, guarenteeEnd);

					exprCtx.putVariable(ContextVariable.REGULATORY_BASE,
							new TimedObject<Double>(0.00, guarenteeStart, guarenteeEnd));

					type.accept(new LeaveTypeVisitor<Void>() {

						@Override
						public Void visitCommonDisease(LeaveType leaveType) {
							exprCtx.setVariable(ContextVariable.COMMON_DISEASE_DAYS, 0, guarenteeStart, guarenteeEnd);
							return null;
						}

						@Override
						public Void visitOcupationalDisease(LeaveType leaveType) {
							exprCtx.setVariable(ContextVariable.OCCUPATIONAL_DISEASE_DAYS, 0, guarenteeStart,
									guarenteeEnd);
							return null;
						}

						@Override
						public Void visitMaternity(LeaveType leaveType) {
							exprCtx.setVariable(ContextVariable.MATERNITY_DAYS, 0, guarenteeStart, guarenteeEnd);
							return null;
						}

						@Override
						public Void visitPaternity(LeaveType leaveType) {
							exprCtx.setVariable(ContextVariable.PATERNITY_DAYS, 0, guarenteeStart, guarenteeEnd);
							return null;
						}

						@Override
						public Void visitPregnacyRisk(LeaveType leaveType) {
							return null;
						}

						@Override
						public Void visitBreastFeedingRisk(LeaveType leaveType) {
							return null;
						}

						@Override
						public Void visitNonOcupationalDisease(LeaveType leaveType) {
							return visitCommonDisease(leaveType);
						}

					});

				}
			};
		}

		@Override
		public double getIrpf() {
			return 0.00;
		}
		
//		@Override
//		public Collection<IContractPayment> getContractPayments() throws AonException {
//			Collection<IContractPayment> payments =  super.getContractPayments();
//			return new FilterCollection<IContractPayment>( p -> p.getType() != PaymentType.CRA_0055 , payments);
//		}

		@Override
		public Object guarantee(double guarentee, int start, int end) throws ExpressionException {
			if (this.start == start && this.end == end) {
				Period period = getCurrentBindings().getPeriod();
				TimedResult<Double> result = new TimedResult<Double>(guarentee, period, Collections.emptyMap());
				guarentees.add(result);
				// if ( --guaranteed == 0 )
				if (period.getEnd().equals(getGuaranteeEnd()))
					throw new SalaryExpressionException(new GuarenteeException(guarentees));
				else
					return guarentee;

			}
			return super.guarantee(guarentee);

		}

		@Override
		protected ContractExpressionContext newContractExpressionContext(ExpressionContext expressionContext,
				NotFoundHandler notFoundHandler) {
			return new ContractExpressionContext(expressionContext, notFoundHandler) {
				@Override
				protected String zeroGuarantee(String script) {
					return script;
				}
			};
		}

		@Override
		protected List<Period> splitWorkedDays(List<Period> worked) {

			List<Period> leaves = guaranteePeriods;

			Collections.sort(leaves);
			Collections.sort(worked);

			return split(worked, leaves);
		}

		@Override
		protected void loadDaysContextVariables(ContractExpressionContext ctx) throws ExpressionException {
			ctx.removeVariable(ERE_DAYS);
			ctx.removeVariable(ERE_FACTOR);
			super.loadDaysContextVariables(ctx);
		}

		public static List<Period> split(List<Period> worked, List<Period> leaves) {

			if (worked.isEmpty() || leaves.isEmpty())
				return worked;

			List<Period> periods = new LinkedList<Period>();

			int l = 0;
			int w = 0;
			Period work = worked.get(w);
			Period leave = leaves.get(l);
			// while (w < worked.size() && l < leaves.size()) {
			do {

				if (work.getEnd() != null && leave.getStart().after(work.getEnd())) {
					periods.add(work);
					if (++w >= worked.size())
						return periods;
					work = worked.get(w);
					continue;
				} // leave after work, try with next work

				if (leave.getEnd() != null && leave.getEnd().before(work.getStart())) {
					if (++l >= leaves.size())
						break;
					leave = leaves.get(l);
					continue;
				} // leave before work, try with next leave

				Period intersect = work.intersect(leave);

				if (work.getStart().before(intersect.getStart()))
					periods.add(new Period(work.getStart(), add(intersect.getStart(), DAY_OF_MONTH, -1)));

				periods.add(new Period(intersect.getStart(), intersect.getEnd()));

				if (intersect.getEnd() != null && (work.getEnd() == null || work.getEnd().after(intersect.getEnd()))) {
					work = new Period(add(intersect.getEnd(), DAY_OF_MONTH, 1), work.getEnd());
					if (++l >= leaves.size())
						break;
					leave = leaves.get(l);
				} // leave ends before work, try next leave with this remain
				else {
					if (++w >= worked.size())
						return periods;
					work = worked.get(w);
				} // leave until ends of work, try next work with this leave.

			} while (true);

			periods.add(work);

			return periods;
		}

		@Override
		protected double getWorkDays(ExpressionContext ctx, Period p) {
			Double workDays = super.getWorkDays(ctx, p);
			return getDays(workDays, ctx, p);
		}

		protected double getGuaranteedDays(ExpressionContext ctx, Period p) {

			Long availableDays = getAvailableDays(p.getStart(), p.getEnd());

			if (p.getEnd().before(lastLeaveEnd))
				return availableDays.doubleValue();

			return super.leaveLoader.getAdjustDays(ctx, p, availableDays);
		}

		protected double getDays(Double workDays, ExpressionContext ctx, Period p) {
			
			if ( isWholeMonth(p)  ) 
				return workDays;
			
			if ( p.getStart().after(getStartDate()) && isLastMonthPeriod(p))
				return getRemainDays(workDays, ctx, p);
	
			if (p.getEnd().before(lastLeaveEnd))
				return workDays;

			if (p.getStart().equals(getStart()) && p.getEnd().equals(getEnd()))
				return workDays;

			if (p.getStart().equals(getStartDate()) && p.getEnd().equals(getEndDate()))
				return workDays;

			return super.leaveLoader.getAdjustDays(ctx, p, workDays.longValue());
		}

		@Override
		protected void onContractLeaveLoaded(ResultSet rs, ExpressionContext ctx) {
			// Skip load GUARANTEE, that is already loaded
		}

		// --------------------------------------------------------------------

		private Date getGuaranteeEnd() {
			return guaranteePeriods.get(guaranteePeriods.size() - 1).getEnd();
		}
		
		private double getRemainDays(Double workDays, ExpressionContext ctx, Period p) {
			
			double realMonthDays = getMax(p.getEnd(), DAY_OF_MONTH);
			double ctxMonthDays = super.getContexVariable(ctx, p, MONTH_DAYS);
			return ctxMonthDays - ( realMonthDays - workDays );
		}
		
		
		
	}

	/**
	 * Clase base para implementar variables pesadas con evaluaci\F3n perezosa.
	 * Las clases hijas \FAnicamente deber\E1n implementar el m\E9todo 'V
	 * getValue()'.
	 * 
	 * @author rtrepiana
	 * 
	 * @param <V>
	 */
	protected abstract class LazyTimedVariable<V> implements ITimedVariable<V> {
		private V value;
		private boolean initialized = false;

		protected Period period = new Period(startDate, getEnd());

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

	protected abstract class LazyTimedExpressionVariable<V> extends LazyTimedVariable<V>
			implements IExpressionVariable<V> {

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

	protected abstract class LazyTimedConstant<V> extends LazyTimedVariable<V> implements IConstantVariable {

	}

	protected abstract class LazyTimedExpressionConstant<V> extends LazyTimedExpressionVariable<V>
			implements IConstantVariable {
		public LazyTimedExpressionConstant(String name, ExpressionScope scope) {
			super(name, scope);
		}
	}

	protected abstract class ActiveTimedVariable<V> implements ITimedVariable<V> {

		protected Period period = new Period(startDate, getEnd());

		@Override
		public Period getPeriod() {
			return period;
		}

	}

	protected abstract class ActiveTimedExpressionVariable<V> extends ActiveTimedVariable<V>
			implements IExpressionVariable<V> {

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

		public DeferredTimedVariable(IExpression expression, Date start, Date end) {
			this.period = new Period(start, end);
			this.script = expression.getExpression();
			Set<String> vars = Collections.singleton(expression.getName());
		}

		@Override
		public Object getValue(Period period) {
			try {
				List<ITimedResult<Object>> timedObjects = getExpressionContext().eval(script, period.getStart(),
						period.getEnd(), Object.class);

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
			criteria.addOrExpression(
					ExpressionUtilities.getEqualExpression(ContractPaymentColumns.SALARY_TYPE, type.ordinal()));
		}
		return criteria;
	}

	private Criteria criteria;
	public Connection connection;

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
	private ContractExpressionContext contractExpressionContext;
	private ExpressionContext implicitExpressionContext;
	private SQLContractLeaveLoader leaveLoader;

	private Date contractStartDate;
	private Date contractEndDate;
	private Collection<ISystemCost> systemCosts;
	private Collection<ISystemDeduction> systemDeductions;
	private Collection<ISystemPayment> systemPayments;

	private SQLCnae2009 cnae2009;
	private LRUCache<Integer, ICalendar> calendars;
	private SQLCalendarFactory calendarFactory;
	private LRUCache<AgreementKey, Collection<ISystemPayment>> agreementPayments;
	private SQLAgreementPaymentsFactory agreementPaymentsFactory;
	private LRUCache<AgreementContextKey, ExpressionContext> agreementExpressionContexts;
	private SQLAgreementContextFactory agreementContextFactory;

	private LRUCache<CCCContextKey, ExpressionContext> cccExpressionContexts;

	private Criteria paymentsCriteria;
	private OrderByList order;

	private IListener listener;

	private Map<Double, Double> liquids;
	private Map<Double, Double> payments;

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

	public SQLContractSalaryCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate,
			Criteria criteria) throws SQLException, ExpressionException {
		this(connection, startDate, endDate, issueDate, criteria,
				getPaymentsCriteria(SalaryType.SALARY, SalaryType.EXTRA));
	}

	public SQLContractSalaryCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate,
			Criteria criteria, OrderByList order) throws SQLException, ExpressionException {
		this(connection, startDate, endDate, issueDate, criteria,
				getPaymentsCriteria(SalaryType.SALARY, SalaryType.EXTRA), order);
	}

	public SQLContractSalaryCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate,
			Date chargeDate, Criteria criteria) throws SQLException, ExpressionException {
		this(connection, startDate, endDate, issueDate, chargeDate, criteria,
				getPaymentsCriteria(SalaryType.SALARY, SalaryType.EXTRA), NEWER);
	}

	public SQLContractSalaryCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate,
			Date chargeDate, Criteria criteria, OrderByList order) throws SQLException, ExpressionException {
		this(connection, startDate, endDate, issueDate, chargeDate, criteria,
				getPaymentsCriteria(SalaryType.SALARY, SalaryType.EXTRA), order);
	}

	public SQLContractSalaryCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate,
			Criteria criteria, Criteria paymentsCriteria, OrderByList order) throws SQLException, ExpressionException {
		this(connection, startDate, endDate, issueDate, null, criteria, paymentsCriteria, order);
	}

	public SQLContractSalaryCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate,
			Criteria criteria, Criteria paymentsCriteria) throws SQLException, ExpressionException {
		this(connection, startDate, endDate, issueDate, null, criteria, paymentsCriteria, NEWER);
	}

	public SQLContractSalaryCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate,
			Date chargeDate, Criteria criteria, Criteria paymentsCriteria) throws SQLException, ExpressionException {
		this(connection, startDate, endDate, issueDate, chargeDate, criteria, paymentsCriteria, NEWER);
	}

	public SQLContractSalaryCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate,
			Date chargeDate, Criteria criteria, Criteria paymentsCriteria, OrderByList order, Object... args)
			throws SQLException, ExpressionException {
		this.connection = connection;

		this.startDate = new Date(DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH).getTime());
		this.endDate = new Date(DateUtils.truncate(endDate, Calendar.DAY_OF_MONTH).getTime());
		this.issueDate = new Date(issueDate.getTime());
		this.chargeDate = chargeDate != null ? new Date(chargeDate.getTime()) : null;

		this.criteria = criteria;
		this.paymentsCriteria = paymentsCriteria;
		this.order = order;

		initResultSet(args);
		initPaymentStmt();
		initDeductionStmt();
		initBonusStmt();
		initEmbargoStmt();
		initCeventStmt();
		initLeaveStmt();
		initSystemCosts();
		initSystemDeductions();
		initSystemPayments();

		this.sqlContractPayment = new SQLContractPayment();
		this.sqlContractDeduction = new SQLContractDeduction();
		this.sqlContractBonus = new SQLContractBonus();
		this.sqlContractEmbargo = new SQLContractEmbargo();

		this.cnae2009 = new SQLCnae2009(connection, this.startDate, this.getEnd());

		calendarFactory = new SQLCalendarFactory(connection, this.startDate, this.getEnd());
		this.calendars = new LRUCache<Integer, ICalendar>(CACHE_SIZE, calendarFactory);
		calendarFactory.setCache(calendars); // TODO: Todo en la misma clase???

		agreementPaymentsFactory = new SQLAgreementPaymentsFactory(connection, this.startDate, this.getEnd(),
				this.paymentsCriteria);
		this.agreementPayments = new LRUCache<AgreementKey, Collection<ISystemPayment>>(CACHE_SIZE,
				agreementPaymentsFactory);

		// cccExpressionContexts = new LRUCache<CCCContextKey,
		// ExpressionContext>(
		// CACHE_SIZE, new SQLSystemExpressionContextFactory(connection,
		// this.startDate, this.getEnd(), order));

		agreementContextFactory = new SQLAgreementContextFactory(connection, this::getCCCExpressionContext,
				this.startDate, this.getEnd(), order);
		this.agreementExpressionContexts = new LRUCache<AgreementContextKey, ExpressionContext>(CACHE_SIZE,
				agreementContextFactory);
		this.leaveLoader = new SQLContractLeaveLoader(this.startDate, this.getEnd());

		this.liquids = new HashMap<Double, Double>();
		this.payments = new HashMap<Double, Double>();

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
		try {
			return getCccExpressionContexts().get(new CCCContextKey(getCCCType(), getSSRegime()));
		} catch (Exception e) {
			return getCccExpressionContexts().get(new CCCContextKey(null, null));
			// TODO: This is very simple, too much
		}
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
		/* TODO A\F1adir la tabla y columnas a las constantes. */

		String streetType = getString(SQLConstants.RADDRESS, SQLConstants.RaddressColumns.STREET_TYPE);
		String address = getString(SQLConstants.RADDRESS, SQLConstants.RaddressColumns.ADDRESS);
		String number = getString(SQLConstants.RADDRESS, SQLConstants.RaddressColumns.NUMBER);
		String address2 = getString(SQLConstants.RADDRESS, SQLConstants.RaddressColumns.ADDRESS2);
		String address3 = getString(SQLConstants.RADDRESS, SQLConstants.RaddressColumns.ADDRESS3);
		String zip = getString(SQLConstants.RADDRESS, SQLConstants.RaddressColumns.ZIP);
		String city = getString(SQLConstants.RADDRESS, SQLConstants.RaddressColumns.CITY);
		String geozone = getString(SQLConstants.RADDRESS, SQLConstants.RaddressColumns.GEOZONE);

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
		int ordinal = getInt(SQLConstants.CONTRACT, ContractColumns.SS_REGIME);
		return SSRegimeType.values()[ordinal];
	}

	@Override
	public String getCategory() {
		String category = getString(SQLConstants.CONTRACT, ContractColumns.CATEGORY_DESCRIPTION);
//		if (AonStringUtils.isNotBlank(category))
		return category;

//		return getString(SQLConstants.AGREEMENT_LEVEL_CATEGORY, AgreementLevelCategoryColumns.DESCRIPTION);
	}

	@Override
	public String getQuoteGroup() {
		return contractExpressionContext.getVariable(ContextVariable.QUOTE_GROUP, startDate, getEnd(), String.class);
	}

	@Override
	public String getEmployeeName() {
		String name = getString(SQLConstants.PERSON, PersonColumns.NAME);
		String firstSurname = getString(SQLConstants.PERSON, PersonColumns.FIRST_SURNAME);
		String secondSurname = getString(SQLConstants.PERSON, PersonColumns.SECOND_SURNAME);

		StringBuffer employeeName = new StringBuffer();

		if (StringUtils.isNotBlank(firstSurname)) {
			employeeName.append(firstSurname.trim());
		}
		if (StringUtils.isNotBlank(secondSurname)) {
			employeeName.append(SPACE);
			employeeName.append(secondSurname.trim());
		}
		if (StringUtils.isNotBlank(name)) {
			employeeName.append(COMMA);
			employeeName.append(SPACE);
			employeeName.append(name.trim());
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
		AgreementKey agreementKey = getAgreementKey();
		Collection<ISystemPayment> payments = agreementPayments.get(agreementKey);

		AgreementKey enterpriseAgreementKey = getEnterpriseAgreementKey();

		if (AonUtils.equals(agreementKey, enterpriseAgreementKey))
			return new DomainPayments(payments);

		Collection<ISystemPayment> enterprisePayments = agreementPayments.get(enterpriseAgreementKey);
		return new DomainPayments(enterprisePayments, payments);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection<IContractPayment> getContractPayments() throws AonException {
		try {
			this.sqlContractPayment.close();
			int id = getId();
			paymentStmt.setInt(1, id);
			ResultSet rs = paymentStmt.executeQuery();
			this.sqlContractPayment.setResultSet(rs);

			return new CompositePayments(this.sqlContractPayment, getAgreementPayments(), getSSRegimePayments()) {
				@Override
				public Iterator<IContractPayment> iterator() {
					Iterator<IContractPayment> iterator = super.iterator();
					return new Iterator<IContractPayment>() {
						@Override
						public boolean hasNext() {
							return iterator.hasNext();
						}

						@Override
						public IContractPayment next() {
							IContractPayment nextPayment = iterator.next();
							return nextPayment;
						}

					};
				}
			};
		} catch (SQLException e) {
			throw new AonException(e);
		}
	}

	@Override
	public Collection<IContractDeduction> getContractDeductions() throws AonException {

		try {
			this.sqlContractDeduction.close();
			int id = getId();
			deductionStmt.setInt(1, id);
			ResultSet rs = deductionStmt.executeQuery();
			this.sqlContractDeduction.setResultSet(rs);
			HierarchyDeductions hierarchyDeductions = new HierarchyDeductions(this.sqlContractDeduction,
					getCCCDeductions().iterator(), getSSRegimeDeductions().iterator());
			return hierarchyDeductions;
		} catch (SQLException e) {
			throw new AonException(e);
		}

	}

	@Override
	public Collection<IContractEmbargo> getContractEmbargos() throws AonException {
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
	@SuppressWarnings("unchecked")
	public Collection<IContractCost> getContractCosts() throws AonException {
		return new CompositeCosts(getCCCCosts(), getSSRegimeCosts()) {
			@Override
			protected int getLevel(IContractCost item) {
				return ((ISystemCost) item).getDomain();
			}
		};
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

	public static final Pattern ACTUAL_VAR_PATTERN = Pattern.compile("(\\w+)_ACTUAL");

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

		if (var == null)
			return null;

		Matcher matcher = ACTUAL_VAR_PATTERN.matcher(var);

		if (!matcher.matches())
			return null;

		List<ITimedVariable<?>> values = new ArrayList<ITimedVariable<?>>(1);

		String srcVar = matcher.group(1);

		values.add(new ActualVar(srcVar));

		return values;
	}

	@Override
	public IListener getListener() {
		return listener;
	}

	@Override
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
		Object cna2009 = getObject(SQLConstants.ENTERPRISE_ACTIVITY, EnterpriseActivityColumns.CNAE2009);
		return cna2009 != null ? (Integer) cna2009 : null;
	}

	public CCCType getCCCType() {
		int ordinal = getInt(SQLConstants.ENTERPRISE_CCC, EnterpriseCccColumns.TYPE);
		return CCCType.values()[ordinal];
	}

	public boolean next() throws SQLException, ExpressionException {
		return next((ctx) -> {
		});
	}

	public final boolean next(NextHook hook) throws SQLException, ExpressionException {

		boolean next = this.resultSet.next();
		if (next) {
			initContractExpressionCtx(hook);
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

	public String getString(String tableLabel, String columnLabel) {
		try {
			return this.resultSet.getString(tableLabel + "." + columnLabel);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public <T> void addVariable(String name, T t) {
		this.contractExpressionContext.setVariable(name, t, this.contractStartDate, this.contractEndDate);
	}

	public <T> T getVariable(ContextVariable var, Class<T> toType) {
		return getVariable(var.getName(), toType);
	}

	public <T> T getVariable(ContextVariable var, Period p, Class<T> toType) {
		return this.contractExpressionContext.getVariable(var.getName(), p.getStart(), p.getEnd(), toType);
	}

	public <T> T getVariable(String name, Class<T> toType) {
		return this.contractExpressionContext.getVariable(name, this.contractStartDate, this.contractEndDate, toType);
	}

	public Boolean hasVariable(String name, Date start, Date end) {
		List<ITimedVariable<Object>> vars = this.contractExpressionContext.getVariables(name, start, end);
		if ( vars.isEmpty()  )
			return false;
		
		Period periods [] = vars.stream().map(v-> v.getPeriod()).sorted().toArray(Period[]::new);
		for ( int i= 1; i < periods.length; i++)
			if ( Period.compare(add(periods[i-1].getEnd(), DAY_OF_MONTH,1),periods[i].getStart()) != 0 ) 
				return false;
		
		if ( Period.compare(periods[0].getStart(), start) > 0 )
			return false;
		
		return Period.compare(periods[periods.length-1].getEnd(), end) >= 0; 
	}

	public Period []  getPeriods(String varName) {
		return this.contractExpressionContext.getVariables(varName).stream()
				.map(v->v.getPeriod()).toArray(Period[]::new);
	}

	public static java.sql.Date toSqlDate(Date date) {
		return new java.sql.Date(date.getTime());
	}

	public static Object guaranteee(double guarentee) throws ExpressionException {
		throw new MacroException() {
			@Override
			public String doMacro(String expr) {
				return expr.replaceAll(ContextVariable.GUARANTEE, "SELF.guarantee");
			}
		};
	}

	public static Object guaranteee(double guarentee, int start) throws ExpressionException {
		return guaranteee(guarentee);
	}

	public static Object guaranteee(double guarentee, int start, int end) throws ExpressionException {
		return guaranteee(guarentee);
	}

	public Connection getConnection() {
		return connection;
	}

	public IIrpfCalculatorContext getIrpfCalculatorContext() {
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(startDate);
		endCalendar.set(Calendar.DAY_OF_YEAR, endCalendar.getActualMaximum(Calendar.DAY_OF_YEAR));
		Date endYear = endCalendar.getTime();

		Criteria contractCriteria = new Criteria();
		contractCriteria.addExpression(criteria.getExpression());
		contractCriteria.addEqualExpression(SQLConstants.CONTRACT + "." + ContractColumns.ID, getId());

		return getIrpfCalculatorContext(connection, startDate, endYear, contractCriteria);
	}

	public void loadContractLeave(Integer id, Date leaveStart, Date leaveEnd, long parentDays, LeaveType type,
			Double dailyRegBase, ExpressionContext exprCtx) throws ExpressionException {
		leaveLoader.loadContractLeave(id, leaveStart, leaveEnd, parentDays, type, dailyRegBase, exprCtx);
	}

	public void loadContractLeave(Integer id, Date leaveStart, Date leaveEnd, long parentDays, LeaveType type,
			String dailyRegBase, ExpressionContext exprCtx) throws ExpressionException {
		leaveLoader.loadContractLeave(id, leaveStart, leaveEnd, parentDays, type, dailyRegBase, exprCtx);
	}

	public void clean(ExpressionContext exprCtx, Leave leave) {
		leaveLoader.clean(exprCtx, leave);
	}

	public SortedSet<Leave> getLeaves() {
		return leaveLoader.getLeaves();
	}

	public List<Period> getLeavesPeriods() {
		List<Period> periods = new ArrayList<Period>();
		for (Leave l : leaveLoader.getLeaves())
			periods.add(l);
		return periods;
	}

	protected ISalaryCalculatorContext getLiquidCalculatorContext(final double solve, final double liquid) {

		Criteria contractCriteria = new Criteria();
		contractCriteria.addExpression(criteria.getExpression());
		contractCriteria.addEqualExpression(SQLConstants.CONTRACT + "." + ContractColumns.ID, getId());

		return getLiquidCalculatorContext(connection, startDate, getEnd(), issueDate, contractCriteria, solve, liquid);
	}

	// ------------------------------------------------------- Protected methods

	protected Date getEnd() {
		return this.endDate;
	}

	protected Date getStart() {
		return this.startDate;
	}

	protected Criteria getCriteria() {
		return criteria;
	}

	protected String getMainSql(Object... args) {
		return MAIN_SQL;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.agreementContextFactory = null;
	}

	protected Integer getAgreementId() {
		Object id = getObject(SQLConstants.AGREEMENT, AgreementColumns.ID);
		return id == null ? null : (Integer) id;
	}

	protected Integer getAgreementDomain() {
		Object domain = getObject(SQLConstants.AGREEMENT, AgreementColumns.DOMAIN);
		return domain == null ? null : (Integer) domain;
	}

	protected Integer getEnterpriseDomain() {
		Object domain = getObject(SQLConstants.ENTERPRISE, EnterpriseColumns.DOMAIN);
		return domain == null ? null : (Integer) domain;
	}

	protected AgreementKey getAgreementKey() {
		Object id = getObject(SQLConstants.AGREEMENT, AgreementColumns.ID);
		Object domain = getObject(SQLConstants.AGREEMENT, AgreementColumns.DOMAIN);
		return id == null ? null : new AgreementKey((Integer) id, (Integer) domain);
	}

	protected double getActiveDays(Period p) {
		
		int startDay = AonDateUtils.get(p.getStart(), DAY_OF_MONTH);
		//The first day of the month has value 1. ??? 
		if ( startDay == 1 )
			return 0.00; 
		
		Date start  = AonDateUtils.getFirstDayOfMonth(p.getStart());
		Date end  = AonDateUtils.getLastDayOfMonth(p.getStart()); //AonDateUtils.add(p.getStart(), DAY_OF_MONTH,-1);
		
		return getExpressionContext().getVariables(ContextVariable.ACTIVE_DAYS, start, end)
		.stream().map( v -> (Double) v.getValue(v.getPeriod()) ).collect(Collectors.summingDouble( v -> v ))
		;

	}

	protected AgreementKey getEnterpriseAgreementKey() {
		Object id = getObject(SQLConstants.AGREEMENT, AgreementColumns.ID);
		Object domain = getObject(SQLConstants.ENTERPRISE, EnterpriseColumns.DOMAIN);
		return id == null ? null : new AgreementKey((Integer) id, (Integer) domain);
	}

	protected List<Period> splitWorkedDays(List<Period> periods) {
		return periods;
	}

	// --------------------------------------------------------- Private methods

	private void initResultSet(Object... args) throws SQLException {
		String sql = getMainSql(args);
		if (this.criteria != null) {
			sql = CriteriaUtilities.toSQLString(this.criteria, sql);
		}
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setDate(1, toSqlDate(this.getEnd()));
		stmt.setDate(2, toSqlDate(this.startDate));
		resultSet = stmt.executeQuery();
	}

	private void initPaymentStmt() throws SQLException {
		String paymentSql = CriteriaUtilities.toSQLString(paymentsCriteria, PAYMENT_SQL);
		paymentSql = orderBy(paymentSql, order == OLDER ? NEWER : OLDER);
		this.paymentStmt = this.connection.prepareStatement(paymentSql);
		this.paymentStmt.setDate(2, toSqlDate(this.getEnd()));
		this.paymentStmt.setDate(3, toSqlDate(this.startDate));
	}

	private void initDeductionStmt() throws SQLException {
		this.deductionStmt = this.connection.prepareStatement(DEDUCTION_SQL);
		this.deductionStmt.setDate(2, toSqlDate(this.getEnd()));
		this.deductionStmt.setDate(3, toSqlDate(this.startDate));
	}

	private void initBonusStmt() throws SQLException {
		this.bonusStmt = this.connection.prepareStatement(BONUS_SQL);
		this.bonusStmt.setDate(2, toSqlDate(this.getEnd()));
		this.bonusStmt.setDate(3, toSqlDate(this.startDate));
	}

	private void initEmbargoStmt() throws SQLException {
		this.embargoStmt = this.connection.prepareStatement(EMBARGO_SQL);
		this.embargoStmt.setDate(2, toSqlDate(this.getEnd()));
		this.embargoStmt.setDate(3, toSqlDate(this.startDate));
	}

	private void initCeventStmt() throws SQLException {

		String sql = orderBy(CDATA_SQL, order);
		this.ceventStmt = this.connection.prepareStatement(sql);
	}

	private void initLeaveStmt() throws SQLException {
		this.cleaveStmt = this.connection.prepareStatement(CLEAVE_SQL);
		this.cleaveStmt.setDate(2, toSqlDate(this.getEnd()));
		this.cleaveStmt.setDate(3, toSqlDate(this.startDate));
	}

	private Integer getDomain() {
		Object value = getObject(SQLConstants.CONTRACT, ContractColumns.DOMAIN);
		return value == null ? null : (Integer) value;
	}

	private Integer getAgreementLevel() {
		Object value = getObject(SQLConstants.CONTRACT, ContractColumns.AGREEMENT_LEVEL);
//		Object value = getObject(SQLConstants.AGREEMENT_LEVEL_CATEGORY, AgreementLevelCategoryColumns.AGREEMENT_LEVEL);
		return value == null ? null : (Integer) value;
	}

	private ExpressionContext getAgreementContext() throws SQLException, ExpressionException {

		Integer agreementId = getAgreementId();
		Integer agreementLevelId = getAgreementLevel();
		Integer agreementDomain = getAgreementDomain();

		AgreementContextKey agreementAndLevelKey = new AgreementContextKey(agreementDomain, agreementId,
				agreementLevelId);

		ExpressionContext agreementCtx = agreementExpressionContexts.get(agreementAndLevelKey);

		Integer enterpriseDomain = getEnterpriseDomain();
		AgreementContextKey enterpriseAndLevel = new AgreementContextKey(enterpriseDomain, agreementId,
				agreementLevelId);
		ExpressionContext enterpriseCtx = agreementExpressionContexts.get(enterpriseAndLevel);

		ExpressionContext ctx = new ExpressionContext(agreementCtx);
		ctx.add(enterpriseCtx);

		return ctx;
	}

	private void onRedefinedImplicitAtAgreement() {
		if (listener == null)
			return;

		Integer agreementId = getAgreementId();
		Integer agreementLevelId = getAgreementLevel();
		Integer agreementDomain = getAgreementDomain();

		AgreementContextKey agreementAndLevelKey = new AgreementContextKey(agreementDomain, agreementId,
				agreementLevelId);

		agreementContextFactory.getImplicitRedefined(agreementAndLevelKey)
				.forEach((name, pair) -> onRedefinedImplicit(name, pair.getLeft(), pair.getRight()));
	}

	private Collection<IContractCost> getCCCCosts() throws AonException {
		List<IContractCost> costs = new ArrayList<IContractCost>(systemPayments.size());
		CCCType cccType = getCCCType();
		for (ISystemCost systemCost : systemCosts) {
			if (filter(systemCost, cccType)) {
				costs.add(systemCost);
			}
		}
		return costs;

	}

	private Collection<IContractCost> getSSRegimeCosts() throws AonException {
		List<IContractCost> costs = new ArrayList<IContractCost>(systemCosts.size());
		SSRegimeType ssRegime = getSSRegime();
		for (ISystemCost systemCost : systemCosts) {
			if (filter(systemCost, ssRegime)) {
				costs.add(systemCost);
			}
		}
		return costs;

	}

	private Collection<IContractPayment> getCCCPayments() throws AonException {
		List<IContractPayment> payments = new ArrayList<IContractPayment>(systemPayments.size());
		CCCType cccType = getCCCType();
		for (ISystemPayment systemPayment : systemPayments) {
			if (filter(systemPayment, cccType)) {
				payments.add(systemPayment);
			}
		}
		return payments;
	}

	private Collection<IContractPayment> getSSRegimePayments() throws AonException {
		List<IContractPayment> payments = new ArrayList<IContractPayment>(systemPayments.size());
		SSRegimeType ssRegime = getSSRegime();
		for (ISystemPayment systemPayment : systemPayments) {
			if (filter(systemPayment, ssRegime)) {
				payments.add(systemPayment);
			}
		}
		return payments;

	}

	private Collection<IContractDeduction> getCCCDeductions() throws AonException {
		List<IContractDeduction> deductions = new ArrayList<IContractDeduction>(systemDeductions.size());
		CCCType cccType = getCCCType();
		for (ISystemDeduction systemDeduction : systemDeductions) {
			if (filter(systemDeduction, cccType)) {
				deductions.add(systemDeduction);
			}
		}
		return deductions;

	}

	private Collection<IContractDeduction> getSSRegimeDeductions() throws AonException {
		List<IContractDeduction> deductions = new ArrayList<IContractDeduction>(systemDeductions.size());
		SSRegimeType ssRegime = getSSRegime();
		for (ISystemDeduction systemDeduction : systemDeductions) {
			if (filter(systemDeduction, ssRegime)) {
				deductions.add(systemDeduction);
			}
		}
		return deductions;
	}

	private boolean filter(ISystemCost systemCost, CCCType cccType) {
		return systemCost.getDomain() == getDomain(cccType);
	}

	private boolean filter(ISystemCost systemCost, SSRegimeType ssRegime) {
		return systemCost.getDomain() == getDomain(ssRegime);
	}

	private boolean filter(ISystemPayment systemPayment, SSRegimeType ssRegime) {
		return systemPayment.getDomain() == getDomain(ssRegime);
	}

	private boolean filter(ISystemPayment systemPayment, CCCType cccType) {
		return systemPayment.getDomain() == getDomain(cccType);
	}

	private boolean filter(ISystemDeduction systemDeduction, SSRegimeType ssRegime) {
		return systemDeduction.getDomain() == getDomain(ssRegime);
	}

	private boolean filter(ISystemDeduction systemDeduction, CCCType cccType) {
		return systemDeduction.getDomain() == getDomain(cccType);
	}

	/*
	 * Devuelve el <code>ICalendar</code> asociado con el contrato (trabajador),
	 * si no tiene calendario propio devuelve el de su centro de trabajo o el
	 * del sistema ( calendario estatal ) si el centro tampoco tiene calendario
	 * propio.
	 */
	private ICalendar getCalendar() {
		Object calendarId = getObject(SQLConstants.CONTRACT, ContractColumns.CALENDAR);
		if (calendarId == null) {
			calendarId = getObject(SQLConstants.PAYROLL_WORKPLACE, PayrollWorkplaceColumns.CALENDAR);
		}
		return calendars.get((Integer) calendarId); // (Integer) null devuelve
													// null, perfecto.
	}

	public static Date resetTime(Date date) {
		if (date == null)
			return null;

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		// Set time fields to zero
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		// Put iterator back in the Date object
		return cal.getTime();
	}

	public Object getNoItSalary(Connection conn, Date date, SalaryType type, Integer contractId) {

		final Criteria contractCriteria = new Criteria();
		contractCriteria.addExpression(criteria.getExpression());
		contractCriteria.addEqualExpression(SQLConstants.CONTRACT + "." + ContractColumns.ID, getId());

		Date startDate = getFirstDayOfMonth(date);
		Date endDate = getLastDayOfMonth(date);
		return getNoItCalculatorContext(connection, startDate, endDate, endDate, contractCriteria, -1,
				Integer.MAX_VALUE - 1);
	}

	public Object gross(double gross, Date start, Date end) throws ExpressionException, SQLException, SalaryException {

		return paymentImpl(gross, 0.005, start, end);
	}

	public Object paymentImpl(double payment, double accuracy, Date start, Date end)
			throws ExpressionException, SQLException, SalaryException {
		try {
			return solvePayment(new PegasusSolver(accuracy), payment, start, end);
		} catch (Throwable t) {
			throw new CheckException(t.getMessage());
		}

	}

	public Object liquid(double liquid, Date start, Date end)
			throws ExpressionException, SQLException, SalaryException {
		return liquidImpl(liquid, 0.005, start, end);
	}

	public Object liquidImpl(double liquid, double accuracy, Date start, Date end)
			throws ExpressionException, SQLException, SalaryException {

		try {
			return solveLiquid(new PegasusSolver(accuracy), liquid, start, end);
		} catch (Throwable t) {
			throw new CheckException(t.getMessage());
		}
	}

	public Object guaranteeWarn(String message) throws ExpressionException {

		if (leaveLoader.isEmpty())
			throw new UndefinedContextVariablesException(ContextVariable.LEAVE_DAYS);

		throw new CheckException(message);
	}

	public Object guarantee(double guarentee) throws ExpressionException {
		return guarantee(guarentee, 1, Integer.MAX_VALUE);
	}

	public Object guarantee(double guarentee, int start) throws ExpressionException {
		return guarantee(guarentee, start, Integer.MAX_VALUE);
	}

	public Object guarantee(double guarentee, int start, int end) throws ExpressionException {

		Period guaranteePeriod = new Period(getCurrentBindings().getPeriod().getStart(),
				getCurrentBindings().getPeriod().getEnd());

		if (leaveLoader.isEmpty())
			throw new UndefinedContextVariablesException(ContextVariable.LEAVE_DAYS);

		Double totalPayment = getVariable(ContextVariable.TOTAL_PAYMENT, Double.class);
		// Assert all PREST_IT payments have been calculated.
		//Double prestIts = getVariable(PREST_IT, Double.class);
		//if (prestIts == null && totalPayment == null)
		//	throw new UndefinedVariablesException(PREST_IT);
		if ( totalPayment == null )
			for ( Leave leave : leaveLoader.getLeaves() )
				if ( !hasVariable(PREST_IT, leave.getStart(), leave.getEnd()) )
					throw new UndefinedVariablesException(PREST_IT);
		

		final Criteria contractCriteria = new Criteria();
		contractCriteria.addExpression(criteria.getExpression());
		contractCriteria.addEqualExpression(SQLConstants.CONTRACT + "." + ContractColumns.ID, getId());
		ISalaryCalculatorContext ctx = null;
		try {
			ctx = getNoItCalculatorContext(connection, startDate, getEnd(), issueDate, contractCriteria, start - 1,
					end - 1);
		} catch (RuntimeException e) {
			throw e;
		}

		ContractSalaryCalculator<Salary> calculator = new OnlyPaymentContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		try {
			calculator.calculate(ctx);
		} catch (GuarenteeException e) {

			List<ITimedResult<Double>> guarenteeResults = e.getGuarentees(guaranteePeriod);

			return onGuarantee(guarenteeResults);

		} catch (SalaryException e) {
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		return 0.00;
	}

	protected Object onGuarantee(List<ITimedResult<Double>> guarenteeResults) {
		double guarantee = 0.00;
		for (ITimedResult<Double> guarenteeResult : guarenteeResults) {
			double prestIt = getDayDoubleVariable(PREST_IT, guarenteeResult.getPeriod().getStart(),
					guarenteeResult.getPeriod().getEnd());
			guarantee += guarenteeResult.getValue() - prestIt;
		}

		return guarantee;
	}

	protected double solveLiquid(UnivariateSolver solver, final double liquid, Date start, Date end) {

		Double result = SQLContractSalaryCalculatorContext.this.liquids.get(liquid);
		if (result != null)
			return result;

		final Criteria contractCriteria = new Criteria();
		contractCriteria.addExpression(criteria.getExpression());
		contractCriteria.addEqualExpression(SQLConstants.CONTRACT + "." + ContractColumns.ID, getId());

		result = solver.solve(Byte.MAX_VALUE, new UnivariateFunction() {

			@Override
			public double value(double solve) {

				try {
					ISalaryCalculatorContext ctx = getLiquidCalculatorContext(connection, start, end, issueDate,
							contractCriteria, solve, liquid);
					ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
					calculator.setSalaryBuilder(new SalaryBuilder());

					// TODO: Warning a bit tricky.
					ExpressionContext expressionCtx = SQLContractSalaryCalculatorContext.this.getExpressionContext();

					((ISQLContractSalaryCalculatorContext) ctx).setListener(irpf -> {
						if (irpf != null)
							expressionCtx.setVariable(ContextVariable.IRPF_PERCENT, irpf.getIrpfResult().getIrpf(),
									start, end);
						else
							expressionCtx.setVariable(ContextVariable.IRPF_PERCENT, 0.00, start, end);
					});

					ISalary salary = calculator.calculate(ctx);

					return liquid - salary.getTotalLiquid();

				} catch (SalaryException e) {
					throw new RuntimeException(e);
				}
			}

		}, -2.00 * liquid, 2.00 * liquid, liquid);

		SQLContractSalaryCalculatorContext.this.liquids.put(liquid, result);

		return result;
	}

	protected double solvePayment(UnivariateSolver solver, final double payment, Date start, Date end) {
		Double result = SQLContractSalaryCalculatorContext.this.payments.get(payment);
		if (result != null)
			return result;

		final Criteria contractCriteria = new Criteria();
		contractCriteria.addExpression(criteria.getExpression());
		contractCriteria.addEqualExpression(SQLConstants.CONTRACT + "." + ContractColumns.ID, getId());

		result = solver.solve(Byte.MAX_VALUE, new UnivariateFunction() {

			@Override
			public double value(double x) {

				try {
					ISalaryCalculatorContext ctx = getPaymentCalculatorContext(connection, start, end, end,
							contractCriteria, x);
					ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
					calculator.setSalaryBuilder(new SalaryBuilder());

					ISalary salary = calculator.calculate(ctx);
					return payment - salary.getTotalPayment();
				} catch (SalaryException e) {
					throw new RuntimeException(e);
				}
			}

		}, -10.00 * payment, 10.0 * payment, payment);

		SQLContractSalaryCalculatorContext.this.payments.put(payment, result);

		return result;
	}

	protected ISQLContractSalaryCalculatorContext getNoItCalculatorContext(Connection conn, Date startDate,
			Date endDate, Date issueDate, Criteria criteria, int start, int end) {
		SQLContractSalaryCalculatorContext ctx;
		try {
			ctx = new SQLNoItContractSalaryCalculatorContext(conn, startDate, endDate, issueDate, criteria, start, end);
			ctx.next();
			return ctx;
		} catch (ExpressionException e) {
			throw new ExpressionExceptionWrapper(e);
		} catch (SQLException e) {
			throw new ExpressionExceptionWrapper(new ExpressionException(e));
		}
	}

	protected ISalaryCalculatorContext getLiquidCalculatorContext(Connection conn, Date startDate, Date endDate,
			Date issueDate, Criteria criteria, final double solve, final double liquid) {
		SQLContractSalaryCalculatorContext ctx;
		try {
			ctx = new SQLContractSalaryCalculatorContext(connection, startDate, endDate, issueDate, criteria) {

				@Override
				public double getIrpf() {
					try {
						return super.getIrpf();
					} catch (Exception e) {
						onIrpf(null);
						throw e;
					}
				}

				@Override
				public Object liquid(double liquid, Date start, Date end) throws ExpressionException, SQLException {
					return solve;
				}

				@Override
				public Object gross(double gross, Date start, Date end)
						throws ExpressionException, SQLException, SalaryException {
					throw new InterruptedException(String.format(
							"Lo sentimos. La funci\u00F3n NETO es incompatible con la funci\u00F3n BRUTO. Elija una de las dos. :-("));
				}

				@Override
				protected ISQLContractSalaryCalculatorContext getNoItCalculatorContext(Connection conn, Date startDate,
						Date endDate, Date issueDate, Criteria criteria, int start, int end) {
					SQLContractSalaryCalculatorContext ctx;
					try {
						ctx = new SQLNoItContractSalaryCalculatorContext(conn, startDate, endDate, issueDate, criteria,
								start, end) {

							@Override
							public Object liquid(double liquid, Date start, Date end)
									throws ExpressionException, SQLException {
								return solve;
							};

							@Override
							public Object gross(double gross, Date start, Date end)
									throws ExpressionException, SQLException, SalaryException {
								throw new InterruptedException(String.format(
										"Lo sentimos. La funci\u00F3n NETO es incompatible con la funci\u00F3n BRUTO. Elija una de las dos. :-("));
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

				@Override
				protected IIrpfCalculatorContext getIrpfCalculatorContext(Connection conn, Date startDate, Date endDate,
						Criteria criteria) {
					try {
						SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(conn, startDate,
								endDate, endDate, criteria) {

							@Override
							public double getIrpf() {
								return 0.00;
							};

							@Override
							public Object liquid(double _liquid, Date start, Date end)
									throws ExpressionException, SQLException {
								return solve * (_liquid / liquid);
							}

						};
						ctx.leaveLoader = leaveLoader;
						return new SQLIrpfCalculatorContext(connection, startDate, endDate, ctx) {
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

							@Override
							public int getAñoNacimiento() {
								return 1969;
							};

						};
					} catch (SQLException e) {
						throw new ExpressionExceptionWrapper(new ExpressionException(e));
					} catch (ExpressionException e) {
						throw new ExpressionExceptionWrapper(e);
					}
				}

			};
			ctx.leaveLoader = leaveLoader;
			ctx.next();
			return ctx;
		} catch (ExpressionException e) {
			throw new ExpressionExceptionWrapper(e);
		} catch (SQLException e) {
			throw new ExpressionExceptionWrapper(new ExpressionException(e));
		}
	}

	protected ISalaryCalculatorContext getPaymentCalculatorContext(Connection conn, Date startDate, Date endDate,
			Date issueDate, Criteria criteria, final double x) {
		SQLContractSalaryCalculatorContext ctx;
		try {
			ctx = new SQLContractSalaryCalculatorContext(connection, startDate, endDate, issueDate, criteria) {

				@Override
				public double getIrpf() {
					return 0.00;
				}

				@Override
				public Object gross(double gross, Date start, Date end) throws ExpressionException, SQLException {
					return x;
				}

				@Override
				public Object liquid(double liquid, Date start, Date end)
						throws ExpressionException, SQLException, SalaryException {
					throw new InterruptedException(String.format(
							"Lo sentimos. La funci\u00F3n BRUTO es incompatible con la funci\u00F3n NETO. Elija una de las dos. :-("));
				}

				@Override
				protected ISQLContractSalaryCalculatorContext getNoItCalculatorContext(Connection conn, Date startDate,
						Date endDate, Date issueDate, Criteria criteria, int start, int end) {
					SQLContractSalaryCalculatorContext ctx;
					try {
						ctx = new SQLNoItContractSalaryCalculatorContext(conn, startDate, endDate, issueDate, criteria,
								start, end) {

							@Override
							public double getIrpf() {
								return 0.00;
							}

							@Override
							public Object gross(double gross, Date start, Date end)
									throws ExpressionException, SQLException {
								return x;
							}

							public Object liquid(double liquid)
									throws ExpressionException, SQLException, SalaryException {
								throw new InterruptedException(String.format(
										"Lo sentimos. La funci\u00F3n BRUTO es incompatible con la funci\u00F3n NETO. Elija una de las dos. :-("));
							};

						};

						ctx.next();
						return ctx;
					} catch (ExpressionException e) {
						throw new ExpressionExceptionWrapper(e);
					} catch (SQLException e) {
						throw new ExpressionExceptionWrapper(new ExpressionException(e));
					}
				}

				@Override
				public Collection<IContractDeduction> getContractDeductions() throws AonException {
					return Collections.emptyList();
				}

				@Override
				public Collection<IContractEmbargo> getContractEmbargos() throws AonException {
					return Collections.emptyList();
				}

				@Override
				public Collection<IContractBonus> getContractBonus() throws AonException {
					return Collections.emptyList();
				}

				@Override
				public Collection<IContractCost> getContractCosts() throws AonException {
					return Collections.emptyList();
				}
			};
			ctx.leaveLoader = leaveLoader;
			ctx.next();
			return ctx;
		} catch (ExpressionException e) {
			throw new ExpressionExceptionWrapper(e);
		} catch (SQLException e) {
			throw new ExpressionExceptionWrapper(new ExpressionException(e));
		}
	}

	public Object agreement() throws ExpressionException, SQLException {
		List<ITimedVariable<Object>> vars = getExpressionContext().getVariables(PAYMENT_VARIABLE);
		if (vars == null || vars.isEmpty())
			throw new ExpressionExceptionWrapper(new UndefinedVariablesException(PAYMENT_VARIABLE));

		String name = ((PaymentVariable) vars.get(0)).payment.getName();
		if (AonStringUtils.isEmpty(name))
			throw new ExpressionExceptionWrapper(new UndefinedVariablesException(PAYMENT_VARIABLE));

		Double total = 0.00;
		for (IContractPayment payment : getAgreementPayments()) {
			List<ITimedResult<Double>> results = getExpressionContext().eval(payment.getExpression(),
					payment.getStartDate(), payment.getEndDate(), Double.class);
			for (ITimedResult<Double> result : results)
				total += result.getValue();
		}
		return total;
	}

	public Object redefined(String name, Object value) throws ExpressionException, SQLException {

		ITimedVariable<?> implicit = contractExpressionContext.getVariable(name, getStart(), getEnd());
		if (implicit == null)
			return value;

		ITimedVariable<?> redefined = new TimedObject<Object>(value, getStart(), getEnd());

		onRedefinedImplicit(name, redefined, implicit);

		return value;
	}

	public Object agreement(String name) throws ExpressionException, SQLException {
		ExpressionContext agreementCtx = getAgreementContext();
		return agreementCtx.getVariable(name, startDate, getEnd(), Object.class);
	}

	public Object system(String name) throws ExpressionException, SQLException {
		ExpressionContext systemCtx = agreementContextFactory.getSystemExpressionContext();
		Object value = systemCtx.getVariable(name, startDate, getEnd(), Object.class);
		if (value != null)
			return value;
		return implicitExpressionContext.getVariable(name, startDate, getEnd(), Object.class);
	}

	@Override
	public double getIrpf() {
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(startDate);
		endCalendar.set(Calendar.DAY_OF_YEAR, endCalendar.getActualMaximum(Calendar.DAY_OF_YEAR));
		Date endYear = endCalendar.getTime();

		Criteria contractCriteria = getContractCriteria();

		IIrpfCalculatorContext irpfCalculatorContext = getIrpfCalculatorContext(connection, startDate, endYear,
				contractCriteria);

		IrpfOutcome irpfOutcome = IrpfCalculator.calculateIrpf(irpfCalculatorContext, endYear);

		onIrpf(irpfOutcome);

		return irpfOutcome.getIrpfResult().getIrpf();
	}

	protected Criteria getContractCriteria() {
		Criteria contractCriteria = new Criteria();
		contractCriteria.addExpression(criteria.getExpression());
		contractCriteria.addEqualExpression(SQLConstants.CONTRACT + "." + ContractColumns.ID, getId());
		return contractCriteria;
	}

	protected IIrpfCalculatorContext getIrpfCalculatorContext(Connection conn, Date startDate, Date endDate,
			Criteria criteria) {
		try {
			ISQLContractSalaryCalculatorContext ctx = getUnderlyingIrpfSQLCalculatorContext(conn, startDate, endDate,
					criteria);
			return getIrpfCalculatorContext(conn, startDate, endDate, ctx);
		} catch (SQLException e) {
			throw new ExpressionExceptionWrapper(new ExpressionException(e));
		} catch (ExpressionException e) {
			throw new ExpressionExceptionWrapper(e);
		}

	}

	protected SQLContractSalaryCalculatorContext getUnderlyingIrpfSQLCalculatorContext(Connection conn, Date startDate,
			Date endDate, Criteria criteria) throws SQLException, ExpressionException {

		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(conn, startDate, endDate,
				issueDate, criteria) {
			@Override
			public double getIrpf() {
				return 0.00;
			}
			
			
		};
		ctx.leaveLoader = new SQLContractLeaveLoader(this.startDate, this.getEnd()); //leaveLoader; 
		return ctx;
	}

	protected IIrpfCalculatorContext getIrpfCalculatorContext(Connection conn, Date startDate, Date endDate,
			ISQLContractSalaryCalculatorContext ctx) {
		try {
			return new SQLIrpfCalculatorContext(connection, startDate, endDate, ctx) {
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

	protected double getDayVarSalary() {
		return 0.00;

	}

	protected double getDaySalary() throws ExpressionException, SQLException, SalaryException {

		Calendar contractEnd = Calendar.getInstance();
		contractEnd.setTime(contractEndDate);
		contractEnd.set(Calendar.DATE, 1);
		Date monthStart = contractEnd.getTime();
		contractEnd.set(Calendar.DATE, contractEnd.getActualMaximum(Calendar.DATE));
		Date monthEnd = contractEnd.getTime();

		Criteria contractCriteria = new Criteria();
		contractCriteria.addExpression(criteria.getExpression());
		contractCriteria.addEqualExpression(SQLConstants.CONTRACT + "." + ContractColumns.ID, getId());

		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(connection, monthStart,
				monthEnd, monthEnd, contractCriteria) {
			@Override
			public double getIrpf() {
				return 0.00;
				// TODO: sure
			}

			@Override
			protected double getDaySalary() throws ExpressionException, SQLException, SalaryException {
				throw new CheckException(
						"Imposible calcular el salario regulador de la indemnizaci\u00F3n por despido");
			}
			
			@Override
			protected void loadContractLeave(ExpressionContext ctx) throws SQLException, ExpressionException {
			}

			@Override
			public Collection<IContractBonus> getContractBonus() throws AonException {
				return Collections.emptyList();
			}

			@Override
			public Collection<IContractCost> getContractCosts() throws AonException {
				return Collections.emptyList();
			}

			@Override
			public Collection<IContractEmbargo> getContractEmbargos() throws AonException {
				return Collections.emptyList();
			}

			@Override
			public Collection<IContractDeduction> getContractDeductions() throws AonException {
				return Collections.emptyList();
			}

			@Override
			public void loadContractLeave(Integer id, Date leaveStart, Date leaveEnd, long parentDays, LeaveType type,
					Double dailyRegBase, ExpressionContext exprCtx) throws ExpressionException {
			}

			@Override
			public void loadContractLeave(Integer id, Date leaveStart, Date leaveEnd, long parentDays, LeaveType type,
					String dailyRegBase, ExpressionContext exprCtx) throws ExpressionException {
			}
		};

		ctx.next();

		SalaryBuilder salaryBuilder = new SalaryBuilder();
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(salaryBuilder);
		Salary salary = calculator.calculate(ctx);

		double quoteDys = 
				getContexVariable(ctx.getExpressionContext(), new Period(ctx.getStartDate(), ctx.getEndDate()), ContextVariable.QUOTE_DAYS);
		
		double monthDays = 0;
		monthDays = getContexVariable(ctx.getExpressionContext(), new Period(ctx.getStartDate(), ctx.getEndDate()), MONTH_DAYS);
				

		double totalPayment = salary.getTotalPayment();
		double extraPayProration = salary.getExtraPayProration();

		return (totalPayment + extraPayProration) * monthDays / quoteDys * 12 / 365;

	}

	public static long getAvailableDays(Date start, Date end) {
		long workedDays = CommonUtil.getDaysBetweenDates(start, end);
		workedDays += 1;
		return workedDays;
	}

	private boolean isActualDay(DayType type, Calendar day) {
		int dayOfWeek = day.get(Calendar.DAY_OF_WEEK);

		if (dayOfWeek == Calendar.SUNDAY) {
			return false;
		}

		return type == DayType.WORKING_DAY || type == DayType.CONTINUOUS_TIME || type == DayType.OTHER; 
	}

	private boolean isHoliday(Calendar day) {
		Date date = day.getTime();
		ITimedVariable<?> holidays = this.contractExpressionContext.getVariable(HOLIDAYS, date, date);
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

	private boolean isNotWorkingDay(Calendar day) {
		Date date = day.getTime();
		
		ContextVariable weekHoursVar = WEEK_HOURS_VARIABLES.get(day.get(DAY_OF_WEEK));
		ITimedVariable<?> hours = this.contractExpressionContext.getVariable(weekHoursVar, date, date);
		if (hours == null)
			return false;

		try {
			Period period = hours.getPeriod();
			Object value = hours.getValue(period);
			if ( value == null )
				return true;
			
			return Double.parseDouble(value.toString()) == -1;

		} catch (Error e) {
			return false;
		}

	}

	private int getWeekDaysOf(DayType dayType) {
		int days = 0;

		ICalendar calendar = getCalendar();

		for (int i = Calendar.SUNDAY; i <= Calendar.SATURDAY; ++i) {
			if (dayType == calendar.getWeekDayType(i))
				days++;
		}

		return days;
	}

	/*
	 * Calculate 'DIAS_EFECTIVOS' for the contract (employee). Be care of leaves
	 * and agreement.
	 */
	private double getActualDays() {
		return getActualDays(contractStartDate, contractEndDate);
	}

	private double getActualDays(Date startDate, Date endDate) {
		long days = 0;

		ICalendar calendar = getCalendar();
		Calendar end = Calendar.getInstance();
		end.setTime(endDate);
		Calendar day = Calendar.getInstance();
		day.setTime(startDate);
		while (end.after(day) || end.equals(day)) {
			DayType type = calendar.getDayType(day);
			if (isActualDay(type, day) && !leaveLoader.isLeaveDay(day) && !isHoliday(day) && !isNotWorkingDay(day)) {
				days++;
			}
			day.add(Calendar.DATE, 1);
		}
		return days;
	}

	protected Long getLeaveDays(Period p) {
		return leaveLoader.getLeavesDays(p);
	}

	protected double getWorkDays(ExpressionContext ctx, Period p) {
		
		Date workStart = Period.max(p.getStart(), contractStartDate);
		Date workEnd = Period.min(p.getEnd(), contractEndDate);
		
		Long availableDays = getAvailableDays(workStart,workEnd);

		// Long leaveDays = getLeaveDays(p);

		double workedDays = availableDays /*- leaveDays*/;
		workedDays *= 1.00 - getCurrentBindings().get(ERE_FACTOR, obj -> ((Number) obj).doubleValue(), 0.00);
		workedDays -= getCurrentBindings().get(STRIKE_DAYS, obj -> ((Number) obj).doubleValue(), 0.00);
		
		double workEndDay = AonDateUtils.get(workEnd, DAY_OF_MONTH);
		double monthDays = AonDateUtils.getMax(p.getStart(), DAY_OF_MONTH);
		
		if ( workEndDay < monthDays )
			return workedDays; // Not the last period or doesn't work the full month. 

		double ctxMonthDays = getContexVariable(ctx, p, MONTH_DAYS);

		double prevAdjustDays = getActiveDays(p) ;
		
		return (workedDays + prevAdjustDays)  == monthDays ? (ctxMonthDays - prevAdjustDays) : workedDays;
	}
	

	private double getDays(ExpressionContext ctx, Period p, double factor) {
		Long availableDays = getAvailableDays(p.getStart(), p.getEnd());
		double monthDays = getMax(p.getStart(), DAY_OF_MONTH);
		double ctxMonthDays = getContexVariable(ctx, p, MONTH_DAYS);
		return availableDays == monthDays ? ctxMonthDays : availableDays;
	}

	private int getSeniorityYears(Period period) {
		Date start = getSeniorityDate();
		Date end = period.getStart();
		return getYears(start, end);
	}

	private double getWorkedYears(Date start, Date end) {

		double years = 0;
		double months = 0;

		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(start);
		
		int startDayOfMonth = startCalendar.get(Calendar.DAY_OF_MONTH);
		
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(end);

		int endDayOfMonth = endCalendar.get(Calendar.DAY_OF_MONTH);

		startCalendar.add(Calendar.YEAR, 1);
		while (startCalendar.getTime().compareTo(end) <= 0) {
			years++;
			startCalendar.add(Calendar.YEAR, 1);
		}

		startCalendar.add(Calendar.YEAR, -1);

		startCalendar.add(Calendar.MONTH, 1);
		while (startCalendar.getTime().compareTo(end) <= 0) {
			months++;
			startCalendar.add(Calendar.MONTH, 1);
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(start);
		if (calendar.get(Calendar.DAY_OF_MONTH) == calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
			calendar.setTime(end);
			if (calendar.get(Calendar.DAY_OF_MONTH) == calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
				return years + months / 12d;
		}
		
		if ( startDayOfMonth == endDayOfMonth )
			return years + months / 12d;
		
		startCalendar.add(Calendar.MONTH, -1);
		long days = CommonUtil.getDaysBetweenDates(startCalendar.getTime(), end);
		if (days > 0)
			months++;

		return years + months / 12d;

	}

	public Object br(Date date) throws ExpressionException, SQLException, SalaryException {

		int contractId = getId();
		
		boolean fullTime = true ;
		try {
			fullTime = isFullTime();
		} catch ( Throwable t ) {
		}

		Date prevEndMonth = getLastDayOfMonth(add(date, Calendar.MONTH, -1));
		Date prevStartMonth = getLastDayOfMonth(add(date, Calendar.MONTH, fullTime ? -1: -3 ));

		double br = 0.00;

		try {
			Stream<com.esferalia.aon.occam.api.model.Salary> salaries = AON.getSalaries(new AONContext(connection),
					p -> p.getIsSalaryProperty().eq(true).and(p.getContractProperty().eq(contractId))
							.and(p.getStartDateProperty().le(prevEndMonth)).and(p.getEndDateProperty().ge(prevStartMonth)));
			
			Pair<Double, Double> pair = new Pair<Double, Double>(0.00, 0.00);
			salaries.forEach(s-> {
				pair.fst += s.getCommonContingenciesBase();
				pair.snd += s.getContextData(QUOTE_DAYS.getName(), summingDouble(Double::parseDouble));
			} )
			;
			br = pair.fst / pair.snd;
			
			salaries.close();
		} catch (Throwable t) {
			Stream<com.esferalia.aon.occam.api.model.Salary> salaries = AON.getSalaries(new AONContext(connection),
					p -> p.getIsSalaryProperty().eq(true).and(p.getContractProperty().eq(contractId))
							.and(p.getStartDateProperty().le(prevEndMonth)).and(p.getEndDateProperty().ge(prevStartMonth)));
			Pair<Double, Double> pair = new Pair<Double, Double>(0.00, 0.00);
			salaries.forEach(s-> {
				pair.fst += s.getCommonContingenciesBase();
				pair.snd += ( s.getSalaryDays()
						* ifnull(
								s.getContextData(MONTH_DAYS.getName(),
										summingDouble(Double::parseDouble)),
								(double) getMax(s.getStartDate(), DAY_OF_MONTH))
						/ getMax(s.getStartDate(), DAY_OF_MONTH));
			} )
			;
			br = pair.fst / pair.snd;

			salaries.close();

		}

		if (br > 0.00)
			return br;

		// No salaries are present.
		return calculateBr(date.before(contractStartDate) ? contractStartDate: date);
	}

	public Object calculateBr(Date date) throws ExpressionException, SQLException, SalaryException {
		int contractId = getId();
		ISQLContractSalaryCalculatorContext ctx = (ISQLContractSalaryCalculatorContext) getNoItSalary(connection, date,
				SalaryType.SALARY, contractId);
		try {
			Salary salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()) {
				@Override
				protected void resolvePayment(IContractPayment contractPayment, Date start, Date end, Date issueDate,
						ExpressionContext expressionContext,
						com.esferalia.aon.payroll.calculator.TaxCalculator taxCalculator,
						com.esferalia.aon.payroll.calculator.QuoteCalculator quoteCalculator) throws AonException {
					try {
						super.resolvePayment(contractPayment, start, end, issueDate, expressionContext, taxCalculator,
								quoteCalculator);
					} catch (SalaryExpressionException e) {
						// e.printStackTrace();
					}
				};
			}.calculate(ctx);
			Object br =  salary.getCommonBase() / ctx.getExpressionContext().getVariable(QUOTE_DAYS, ctx.getStartDate(),
					ctx.getEndDate(), Double.class);
			return br;
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}
	

	public Object br(Date start, Date end) throws ExpressionException, SalaryException, SQLException {
		int count = 0;
		double br = 0.00;
		for (Date date = start; date.compareTo(end) <= 0; date = add(date, Calendar.MONTH, 1)) {
			count++;
			br += (double) br(getLastDayOfMonth(date));
		}

		return br / count;
	}

	private double getAdvanceNoticeDays() {

		Date advanceNoticeDate = null;

		try {
			advanceNoticeDate = parse(String.valueOf(getVariable("FECHA_PREAVISO", Object.class)));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
		}

		if (advanceNoticeDate == null)
			throw new ExpressionExceptionWrapper(new UndefinedVariablesException("FECHA_PREAVISO"));

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

	private double getWorkedHours(Period p) {

		p.daysStream().filter(
				day -> !contractExpressionContext.containsVariable(getDayHours(day), day.getTime(), day.getTime()))
				.forEach(day -> onUndefinedData(
						new ExpressionImpl().setName(getDayHours(day).getName()).setScope(ExpressionScope.SYSTEM), null,
						day.getTime(), day.getTime(), getDayHours(day).getName()));

		return p.daysStream()
				.map(day -> contractExpressionContext.getVariable(getDayHours(day), day.getTime(), day.getTime(), Number.class))
				.filter(hours -> hours != null && hours.doubleValue() > 0.00 )
				.collect(Collectors.summingDouble(hours -> hours.doubleValue()))
				;
			
				
	}

	private double getSalaryHours(Period p) {
		return getWorkedHours(p);
	}

	private boolean isIndefinite() {
		String tc2 = getCurrentBindings().get(TC2, obj -> obj.toString());
		if (tc2 == null) {
			throw new ExpressionExceptionWrapper(new UndefinedVariablesException(TC2.getName()));
		}
		return ("123".indexOf(tc2.charAt(0)) != -1);
	}

	private boolean isFullTime() {
		String tc2 = getCurrentBindings().get(TC2, obj -> obj.toString());
		if (tc2 == null) {
			throw new ExpressionExceptionWrapper(new UndefinedVariablesException(TC2.getName()));
		}
		return ("14".indexOf(tc2.charAt(0)) != -1);
	}

	private boolean isFullTime(Period p) {
		String tc2 = getCurrentBindings().get(TC2, obj -> obj.toString());
		if (tc2 == null) {
			throw new ExpressionExceptionWrapper(new UndefinedVariablesException(TC2.getName()));
		}
		return ("14".indexOf(tc2.charAt(0)) != -1);
	}
	
	private Date getContractStart() {
		return getDate(SQLConstants.CONTRACT, ContractColumns.START_DATE);
	}

	private boolean isShortContract() {
		Date endDate = getDate(SQLConstants.CONTRACT, ContractColumns.END_DATE);
		if (endDate == null) {
			return false;
		}
		Date startDate = getDate(SQLConstants.CONTRACT, ContractColumns.START_DATE);
		long naturalDays = CommonUtil.getDaysBetweenDates(startDate, endDate) + 1;
		if (naturalDays < 7) {
			return true;
		}
		return false;
	}

	private boolean isAssimilatted() {
		Object object = getObject(SQLConstants.ENTERPRISE_CCC, EnterpriseCccColumns.TYPE);
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
		Double value = this.contractExpressionContext.getVariable(name, this.contractStartDate, this.contractEndDate,
				Double.class);
		return value != null ? value : 0.00;
	}

	private double getDoubleVariable(String name, Date start, Date end) {
		Double value = this.contractExpressionContext.getVariable(name, start, end, Double.class);
		return value != null ? value : 0.00;
	}

	private double getDayDoubleVariable(String name, Date start, Date end) {
		List<ITimedVariable<Double>> vars = this.contractExpressionContext.getVariables(name);

		double sum = 0.00;
		Period p = new Period(start, end);
		for (ITimedVariable<Double> var : vars) {
			Period period = var.getPeriod();
			Period intersect = period.intersect(p);
			if (intersect == null)
				continue;

			Double value = var.getValue(period);
			if (value != null)
				sum += value / days(period) * days(intersect);
		}

		return sum;
	}

	private boolean containsVariable(Object name) {
		return this.contractExpressionContext.containsVariable(name, this.contractStartDate, this.contractEndDate);
	}

	private boolean containsVariable(Object name, Period p) {
		return this.contractExpressionContext.containsVariable(name, p.getStart(), p.getEnd());
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

	protected ITimedVariable<Number> getExtraDays(ITimedVariable<Number> monthDays) {
		return new ExtraDays(monthDays);
	}

	/*
	 * Inicializa el contexto dentro del cual se calcular\E1n ejecutar\E1n las
	 * percepciones y deducciones de trabajador.
	 */
	protected void initContractExpressionCtx(NextHook hook) throws SQLException, ExpressionException {

		this.contractStartDate = Period.max(getDate(SQLConstants.CONTRACT, ContractColumns.START_DATE), startDate);
		this.contractEndDate = Period.min(getDate(SQLConstants.CONTRACT, ContractColumns.END_DATE), getEnd());

		if (this.contractExpressionContext != null) {
			this.contractExpressionContext = null;
		}

		Period period = new Period(startDate, getEnd());

		// ActiveTimedVariable<Double> cgcBase = new
		// ActiveTimedVariable<Double>() {
		// @Override
		// public Double getValue(Period p) {
		// throw new ExpressionExceptionWrapper(
		// new UndefinedTotalPaymentException());
		// }
		// };

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

		ActiveTimedVariable<Double> bonusDays = new ActiveTimedVariable<Double>() {
			@Override
			public Double getValue(Period p) {
				Date bonusStart = Period.max(sqlContractBonus.getStartDate(), p.getStart());
				Date bonusEnd = Period.min(sqlContractBonus.getEndDate(), p.getEnd());
				long bonusDays = getAvailableDays(bonusStart, bonusEnd);
				return bonusDays == AonDateUtils.getMax(bonusStart, Calendar.DATE) ? 30.00 : bonusDays * 1.00;
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
			public Period getPeriod() {
				return new Period(startDate, getIrpfDate().after(getEnd()) ? getIrpfDate() : getEnd());
			}

			@Override
			public Double create() {
				try {
					return getIrpf();
				} catch (ExpressionExceptionWrapper e) {
					throw e;
				} catch (Throwable throwable) {
					throw throwable;
				}
			}
		};

		ExpressionContext agreementCtx = getAgreementContext();
		onRedefinedImplicitAtAgreement();

		this.implicitExpressionContext = new ExpressionContext(agreementCtx, this);

		// TODO: Tiene que ir aqui ???
		SalaryType salaryType = getSalaryType();
		this.implicitExpressionContext.setVariable(SALARY, salaryType == SalaryType.SALARY, startDate, getEnd());
		this.implicitExpressionContext.setVariable(SETTLE, salaryType == SalaryType.SETTLE, startDate, getEnd());
		this.implicitExpressionContext.setVariable(DELAY, salaryType == SalaryType.DELAY, startDate, getEnd());
		this.implicitExpressionContext.setVariable(EXTRA_PAY, salaryType == SalaryType.EXTRA, startDate, getEnd());

		this.implicitExpressionContext.setVariable(CONTRACT_START,
				getDate(SQLConstants.CONTRACT, ContractColumns.START_DATE), startDate, getEnd());
		this.implicitExpressionContext.setVariable(SENIORITY_START,
				getDate(SQLConstants.CONTRACT, ContractColumns.SENIORITY_DATE), startDate, getEnd());

		this.implicitExpressionContext.setVariable(CONTRACT_END, salaryType == SalaryType.SETTLE ? contractEndDate
				: getDate(SQLConstants.CONTRACT, ContractColumns.END_DATE), startDate, getEnd());

		this.implicitExpressionContext.putVariable(IRPF_PERCENT, irpf);

		this.implicitExpressionContext.putVariable(START, start);
		this.implicitExpressionContext.putVariable(END, end);

		if (!this.implicitExpressionContext.containsVariable(SENIORITY, startDate, getEnd()))
			this.implicitExpressionContext.putVariable(SENIORITY, new ActiveTimedVariable<Integer>() {
				@Override
				public Integer getValue(Period period) {
					return getSeniorityYears(period);
				}
			});

		this.implicitExpressionContext.putVariable(BONUS_DAYS, bonusDays);

		this.implicitExpressionContext.putVariable(INDEFINITE, new LazyTimedConstant<Boolean>() {
			@Override
			public Boolean create() {
				return isIndefinite();
			}
		});

		this.implicitExpressionContext.putVariable(FULL_TIME, new ActiveTimedVariable<Boolean>() {
			@Override
			public Boolean getValue(Period period) {
				return isFullTime(period);
			}
		});

		this.implicitExpressionContext.putVariable(ASSIMILATED, new LazyTimedConstant<Boolean>() {
			@Override
			public Boolean create() {
				return isAssimilatted();
			}
		});

		this.implicitExpressionContext.putVariable(MORE_THAN_65, new LazyTimedConstant<Boolean>() {
			@Override
			public Boolean create() {
				return false;
			}
		});

		this.implicitExpressionContext.putVariable(SHORT_CONTRACT, new LazyTimedConstant<Boolean>() {
			@Override
			public Boolean create() {
				return isShortContract();
			}
		});

		this.implicitExpressionContext.putVariable(IT_RATE, new LazyTimedConstant<Double>() {
			@Override
			public Double create() {
				return getItRate();
			}
		});

		this.implicitExpressionContext.putVariable(IMS_RATE, new LazyTimedConstant<Double>() {
			@Override
			public Double create() {
				return getImsRate();
			}
		});

		this.implicitExpressionContext.putVariable(BONUS_AGE, new LazyTimedVariable<Integer>() {
			@Override
			public Integer create() {
				return getYears(sqlContractBonus.getStartDate(), contractStartDate);
			}
		});

		this.implicitExpressionContext.putVariable(BONUS_START, new LazyTimedVariable<Date>() {
			@Override
			public Date create() {
				return sqlContractBonus.getStartDate();
			}
		});

		this.implicitExpressionContext.putVariable(WORKED_YEARS,
				new ActiveTimedExpressionVariable<Double>(WORKED_YEARS.name(), ExpressionScope.CONTRACT) {
					@Override
					public Period getPeriod() {
						return new Period(startDate, endDate);
					}

					@Override
					public Double getValue(Period period) {
						return getWorkedYears(period.getStart(), period.getEnd());
					}
				});
		this.implicitExpressionContext.putVariable("DIAS_PREAVISO",
				new LazyTimedExpressionVariable<Double>("DIAS_PREAVISO", ExpressionScope.CONTRACT) {
					@Override
					public Double create() {
						return getAdvanceNoticeDays();
					}
				});

		this.implicitExpressionContext.putVariable("SALARIO_VARIABLE_DIA",
				new LazyTimedExpressionConstant<Double>("SALARIO_VARIABLE_DIA", ExpressionScope.CONTRACT) {
					@Override
					public Double create() {
						return getDayVarSalary();
					}
				});

		// TODO: Sure ???
		if (getSalaryType() == SalaryType.SETTLE)
			this.implicitExpressionContext.putVariable("SALARIO_DIA",
					new LazyTimedExpressionConstant<Double>("SALARIO_DIA", ExpressionScope.CONTRACT) {
						@Override
						public Double create() {
							try {
								return getDaySalary();
							} catch (Exception e) {
								throw new ExpressionExceptionWrapper(new InvalidVariables(e.getMessage(), getName()));
							}
						}
					});

		/*
		 * this.implicitExpressionContext.addVariable(COMPENSATION_DAYS, new
		 * LazyTimedVariable<Double>() {
		 * 
		 * @Override public Double create() { return getCompensationDays(); }
		 * });
		 */

		this.contractExpressionContext = newContractExpressionContext(this.implicitExpressionContext, this);

		this.contractExpressionContext.setVariable(CONTEXT, contractExpressionContext, startDate, getEnd());
		this.contractExpressionContext.setVariable(SELF, this, startDate, getEnd());

		// TODO: at implicitExpressionContext ?
		loadExpression(this.contractExpressionContext, BR, "def(x){ SELF.br(x)};", this.startDate, this.getEnd());
		loadExpression(this.contractExpressionContext, GROSS,
				String.format(
						"def(x){ x=%1$s.checkParametersNotConstant(x, '%4$s', '%2$s', '%3$s'); return %1$s.gross(x, %2$s, %3$s ); };",
						SELF, START, END, GROSS),
				this.startDate, this.getEnd());
		loadExpression(this.contractExpressionContext, LIQUID,
				String.format(
						"def(x){ x=%1$s.checkParametersNotConstant(x, '%4$s', '%2$s', '%3$s'); return %1$s.liquid(x, %2$s, %3$s ); };",
						SELF, START, END, LIQUID),
				this.startDate, this.getEnd());
		loadExpression(this.contractExpressionContext, SYSTEM, "def(x){ SELF.system(x)};", this.startDate,
				this.getEnd());
		loadExpression(this.contractExpressionContext, AGREEMENT, "def(x){ SELF.agreement(x)};", this.startDate,
				this.getEnd());

		loadExpression(this.contractExpressionContext, REDEFINE, "def(x,v){ SELF.redefined(x,v)};", this.startDate,
				this.getEnd());

		hook.beforeLoadLeaves(contractExpressionContext);

		loadContractLeave(this.contractExpressionContext);
		loadContractData(this.contractExpressionContext);
		loadPersonData(this.contractExpressionContext);

//		if (!containsVariable(ACTUAL_DAYS)) {
//			this.contractExpressionContext.putVariable(ACTUAL_DAYS, new LazyTimedVariable<Double>() {
//				@Override
//				public Double create() {
//					return getActualDays();
//				}
//			});
//		}

		hook.beforeLoadDaysContextVariables(contractExpressionContext);
		// --------------------------------------------------------------------
		// WEEK_HOURS, WORKED_DAYS and so on. These variables
		//
		loadDaysContextVariables(contractExpressionContext);
		
		ContextFunctions.loadDaysFunctions(contractExpressionContext, contractStartDate, contractEndDate);
	}

	protected ContractExpressionContext newContractExpressionContext(ExpressionContext expressionContext,
			NotFoundHandler notFoundHandler) {
		return new ContractExpressionContext(expressionContext, notFoundHandler);
	}

	protected void loadDaysContextVariables(ContractExpressionContext ctx) throws ExpressionException {

		loadWeekHoursContextVariable(ctx);

		List<Period> contract = getMonths(contractStartDate, contractEndDate);
		
		List<Period> weekHours = ctx.getPeriods(WEEK_HOURS);
		List<Period> contractHours = new ArrayList<Period>();
		contractHours.addAll(Period.sub(contract, weekHours));
		contractHours.addAll(weekHours);
		
		List<Period> agreeementHours = ctx.getPeriods(AGREEMENT_HOURS);

		Collections.sort(contractHours);
		Collections.sort(agreeementHours);
		
		List<Period> intersects = Period.intersect(contractHours, agreeementHours);
		intersects = Period.intersect(intersects, contract);

		for (Period period : intersects) {

			if (!containsVariable(PARTIAL_FACTOR, period)) {
				ITimedVariable<Double> partial_factor = new ITimedVariable<Double>() {
					@Override
					public Period getPeriod() {
						return period;
					}

					@Override
					public Double getValue(Period p) {
						try {
							if ( getVariable(FULL_TIME, p, Boolean.class) != Boolean.TRUE /*!isFullTime()*/) {
								double agreementWeekHours = getCurrentBindings().get(AGREEMENT_HOURS,
										obj -> ((Number) obj).doubleValue(), DEFAULT_AGRREEMENT_HOURS);

								double weekHours = getCurrentBindings().get(WEEK_HOURS,
										obj -> ((Number) obj).doubleValue(), DEFAULT_AGRREEMENT_HOURS);
								
								double wholeFactor = weekHours / agreementWeekHours;

								if (isWholeMonth(p) /* && false */ ) {
									return wholeFactor;
								} else {
									
									ICalendar calendar = getCalendar();
									double agreementDayHours = agreementWeekHours / getWeekDaysOf(DayType.WORKING_DAY);

									double hours[] = { 0.00, 0.00 };
									p.daysStream().forEach(day -> {
										double dayHours = getCurrentBindings().get(
												WEEK_HOURS_VARIABLES.get(day.get(DAY_OF_WEEK)),
												h -> ((Number) h).doubleValue(), -1.00);
										hours[0] += dayHours > 0.00 ? dayHours : 0.00;
										// if ( dayHours < 0 )
										// hours[1] = 0.00;
										// else if (dayHours > 0.00)
										// hours[1] = agreementDayHours;
										// else if ( calendar.getDayType(day) ==
										// DayType.WORKING_DAY)
										// hours[1] = agreementDayHours;
										// else
										// hours[1] = 0.00;
										if (dayHours == -1.00)
											hours[1] += 0.00;
										else
											hours[1] += dayHours > 0.00
													|| calendar.getDayType(day) == DayType.WORKING_DAY
															? agreementDayHours : 0.00;
									});
									

									double incompleteFactor = hours[1] == 0.00 ? 0.00 : (hours[0] / hours[1]);
									
									if ( incompleteFactor != wholeFactor ) 
										onMistakenPartialFactor(hours[1], hours[0], wholeFactor);								

									return incompleteFactor;
								}
							}

						} catch (ExpressionExceptionWrapper e) {
						}
						return 1.00;
					}

				};

				ctx.putVariable(PARTIAL_FACTOR, partial_factor);

			}

			if (!containsVariable(SALARY_DAYS, period)) {
				ITimedVariable<Double> salaryDays = new ITimedVariable<Double>() {
					@Override
					public Period getPeriod() {
						return period;
					}

					@Override
					public Double getValue(Period p) {
						return getDays(ctx, p, 1.00);
					}

				};
				ctx.putVariable(SALARY_DAYS, salaryDays);
			}
			if (!containsVariable(SALARY_HOURS, period)) {
				ITimedVariable<Double> salaryHours = new ITimedVariable<Double>() {
					@Override
					public Period getPeriod() {
						return period;
					}

					@Override
					public Double getValue(Period p) {
						return getSalaryHours(p);
					}

				};
				ctx.putVariable(SALARY_HOURS, salaryHours);
			}

		}

		List<Period> quote = contract;
		List<Period> strike = ctx.getPeriods(STRIKE_FACTOR);
		if (strike != null && !strike.isEmpty()) {
			quote = Period.sub(quote, strike);
			intersects = Period.sub(intersects, strike);
		}
		for (ContextVariable var : new ContextVariable[] { QUOTE_GROUP, OCCUPATION, TC2, PARTIAL_FACTOR }) {
			List<Period> periods = getPeriods(ctx, var.getName());
			quote = Period.intersect(quote, periods);
			intersects = Period.intersect(intersects, periods);
		}

		// for (Period period : quote) {
		// if (!containsVariable(QUOTE_DAYS, period)) {
		// ITimedVariable<Double> quoteDays = new ITimedVariable<Double>() {
		// @Override
		// public Period getPeriod() {
		// return period;
		// }
		//
		// @Override
		// public Double getValue(Period p) {
		// return getDays(ctx, p, 1.00);
		// }
		//
		// };
		// ctx.putVariable(QUOTE_DAYS, quoteDays);
		// }
		// }

		// ITs
		List<Period> leaves = getLeavesPeriods();
		intersects = Period.sub(intersects, leaves);

		if (!containsVariable(ERE_DAYS)) {

			// ERE
			for (ITimedVariable<Object> ereFactor : ctx.getVariables(ERE_FACTOR)) {
				Period period = ereFactor.getPeriod();
				Object value = ereFactor.getValue(period);
				if (!(value instanceof Number))
					continue;

				double factor = ((Number) value).doubleValue();
				if (((Number) value).doubleValue() >= 1.00)
					intersects = Period.sub(intersects, Collections.singletonList(period));
				else
					intersects = SQLNoItContractSalaryCalculatorContext.split(intersects,
							Collections.singletonList(period));

				ITimedVariable<Double> ereDays = new ITimedVariable<Double>() {
					@Override
					public Period getPeriod() {
						return period;
					}

					@Override
					public Double getValue(Period p) {
						return getDays(ctx, p, factor);
					}

				};
				ctx.putVariable(ERE_DAYS, ereDays);

				ITimedVariable<Double> ereBase = new ITimedVariable<Double>() {
					@Override
					public Period getPeriod() {
						return period;
					}

					@Override
					public Double getValue(Period p) {
						try {
							return (Double) br(p.getStart());
						} catch (ExpressionException | SalaryException | SQLException e) {
							throw new ExpressionExceptionWrapper(
									new UndefinedContextVariablesException(REGULATORY_BASE));
						}
					}

				};
				ctx.putVariable(REGULATORY_BASE, ereBase);
			}
		}

		intersects = splitWorkedDays(intersects);

		for (Period period : intersects) {

			ITimedVariable<Double> workedDays = new ITimedVariable<Double>() {
				@Override
				public Period getPeriod() {
					return period;
				}

				@Override
				public Double getValue(Period p) {
					double workDays = getWorkDays(ctx, p);
					try {
						if ( getVariable(FULL_TIME, p, Boolean.class) != Boolean.TRUE /*!isFullTime()*/) {
							return workDays * getCurrentBindings().get(PARTIAL_FACTOR,
									obj -> ((Number) obj).doubleValue(), 1.00);
						}
					} catch (ExpressionExceptionWrapper e) {
					}
					return workDays;
				}

			};
			ITimedVariable<?> userWorkedDays = getExpressionContext().getVariable(WORKED_DAYS, period.getStart(),
					period.getEnd());

			if (userWorkedDays == null) {
				ctx.putVariable(WORKED_DAYS, workedDays);
			} else {
				onRedefinedImplicit(WORKED_DAYS.getName(), userWorkedDays, workedDays);
			}

			ITimedVariable<Double> workedHours = new ITimedVariable<Double>() {
				private Map<Integer, ContextVariable> DAYS = new HashMap<Integer, ContextVariable>() {
					{
						put(MONDAY, MONDAY_HOURS);
						put(TUESDAY, TUESDAY_HOURS);
						put(WEDNESDAY, WEDNESDAY_HOURS);
						put(THURSDAY, THURSDAY_HOURS);
						put(FRIDAY, FRIDAY_HOURS);
						put(SATURDAY, SATURDAY_HOURS);
						put(SUNDAY, SUNDAY_HOURS);
					}
				};

				@Override
				public Period getPeriod() {
					return period;
				}

				@Override
				public Double getValue(Period p) {

					return p.daysStream()
							.filter(day -> !isHoliday(day))
							.filter(day -> getDayType(day) != DayType.HOLIDAY)
							.map(day -> ctx.getVariable(DAYS.get(day.get(DAY_OF_WEEK)), day.getTime(),
									day.getTime(), Number.class))
							.filter(hours -> hours != null && hours.doubleValue() > 0.00 )
							.collect(Collectors.summingDouble(hours -> hours.doubleValue()));
				}

			};

			ITimedVariable<?> userWorkedHours = getExpressionContext().getVariable(WORKED_HOURS, period.getStart(),
					period.getEnd());

			if (userWorkedHours == null) {
				ctx.putVariable(WORKED_HOURS, workedHours);
			} else {
				onRedefinedImplicit(WORKED_HOURS.getName(), userWorkedHours, workedHours);
			}

			ITimedVariable<Double> quoteDays = new ITimedVariable<Double>() {
				@Override
				public Period getPeriod() {
					return period;
				}

				@Override
				public Double getValue(Period p) {
					return getDays(ctx, p, 1.00);
				}

			};

			ITimedVariable<?> userQuoteDays = getExpressionContext().getVariable(QUOTE_DAYS, period.getStart(),
					period.getEnd());

			if (userQuoteDays == null) {
				ctx.putVariable(QUOTE_DAYS, quoteDays);
			} else {
				onRedefinedImplicit(QUOTE_DAYS.getName(), userQuoteDays, quoteDays);
			}
			
			
			ITimedVariable<Double> actualDays = new ITimedVariable<Double>() {
				@Override
				public Period getPeriod() {
					return period;
				}

				@Override
				public Double getValue(Period p) {
					return getActualDays(p.getStart(), p.getEnd());
				}

			};

			ITimedVariable<?> userActualDays = getExpressionContext().getVariable(ACTUAL_DAYS, period.getStart(),
					period.getEnd());
			
			if (userActualDays == null) {
				ctx.putVariable(ACTUAL_DAYS, actualDays);
			} else {
				onRedefinedImplicit(QUOTE_DAYS.getName(), userActualDays, actualDays);
			}
		}

	}

	private void loadWeekHoursContextVariable(ContractExpressionContext ctx) {

		List<Period> contract = getMonths(contractStartDate, contractEndDate);
		List<Period> redefined = ctx.getPeriods(WEEK_HOURS);
		contract = Period.sub(contract, redefined);

		ContextVariable[] WEEK_DAYS = { MONDAY_HOURS, TUESDAY_HOURS, WEDNESDAY_HOURS, THURSDAY_HOURS, FRIDAY_HOURS,
				SATURDAY_HOURS, SUNDAY_HOURS };

		List<Period> intersects = Arrays.stream(WEEK_DAYS).map(var -> ctx.getPeriods(var))
				.filter(periods -> periods != null && !periods.isEmpty())
				.reduce(contract, (a, b) -> Period.intersect(a, b));

		class WeekHours implements ITimedVariable<Double> {

			Period period;

			public WeekHours(Period period) {
				this.period = period;
			}

			@Override
			public Period getPeriod() {
				return period;
			}

			@Override
			public Double getValue(Period p) {
				return Arrays.stream(WEEK_DAYS).collect(Collectors.summingDouble(
						(var -> getCurrentBindings().get(var, obj -> ((Number) obj).doubleValue(), 0.00))));
			}

		}

		for (Period period : intersects) {
			ctx.putVariable(WEEK_HOURS, new WeekHours(period));
		}

		for (Period period : redefined) {

			ITimedVariable<?> userWeekHours = ctx.getVariable(WEEK_HOURS, period.getStart(), period.getEnd());
			onRedefinedImplicit(WEEK_HOURS.getName(), userWeekHours, new WeekHours(period));
		}

	}


	private double getContexVariable(ExpressionContext ctx, Period p, ContextVariable var) {
		ITimedVariable<?> timedVariable = ctx.getVariable(var, p.getStart(), p.getEnd());
		if (timedVariable == null)
			throw new ExpressionExceptionWrapper(new UndefinedContextVariablesException(var));
		try {
			return ((Number) timedVariable.getValue(p)).doubleValue();
		} catch (ExpressionExceptionWrapper e) {
		}

		try {
			return ctx.eval(var.getName(), p.getStart(), p.getEnd(), Double.class).stream()
					.collect(Collectors.summingDouble(r -> r.getValue()));
		} catch (ExpressionException e) {
			throw new ExpressionExceptionWrapper(e);
		}
	}

	/*
	 * Carga, ejecuta los datos del contrato 'contract_data' para este periodo.
	 * Ejecuta porque al valor de una variable no tiene porque ser un literal,
	 * puede ser una expresi\F3n ej : '15 / 100' o 'DIAS_TRABAJADOS * 0.01'
	 */
	protected void loadContractData(ExpressionContext ctx) throws SQLException {
		loadContractData(ctx, contractStartDate, contractEndDate);
		Date irpfDate = getIrpfDate();
		if (irpfDate != null && irpfDate.after(contractEndDate)) {
			loadContractData(ctx, irpfDate, irpfDate);
		}
	}

	protected void loadExpression(ExpressionContext ctx, String name, String script, Date start, Date end)
			throws SQLException {
		ExpressionImpl expr = new ExpressionImpl();
		expr.setName(name);
		expr.setExpression(script);
		expr.setScope(ExpressionScope.CONTRACT);
		try {
			ctx.addExpression(expr, start, end);
		} catch (Exception ignore) {
			ignore.printStackTrace();
			// TODO: \BF Que hacemos con esta excepcion ?
		}
	}

	/*
	 * Carga, ejecuta los datos del contrato 'contract_data' para este periodo.
	 * Ejecuta porque al valor de una variable no tiene porque ser un literal,
	 * puede ser una expresi\F3n ej : '15 / 100' o 'DIAS_TRABAJADOS * 0.01'
	 */
	protected void loadContractData(ExpressionContext ctx, Date startDate, Date endDate) throws SQLException {
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

				ITimedVariable<?> implicit = ctx.getVariable(expr.getName(), start, end);
				try {
					List<ITimedResult<Object>> results = ctx.addExpression(expr, start, end);

					onRedefinedImplicit(ctx, expr.getName(), expr.getExpression(), implicit, results);

				} catch (UndefinedVariablesException e) {
					failed.add(new TimedObject<IExpression>(expr, new Period(start, end)));
				} catch (CheckException e) {

				} catch (Exception e) {
					// TODO: \BF Que hacemos con esta excepcion ?
				}
			}

			for (ITimedObject<IExpression> timedExpr : failed) {
				try {
					Period period = timedExpr.getPeriod();
					IExpression expr = timedExpr.getValue();

					ITimedVariable<?> implicit = ctx.getVariable(expr.getName(), period.getStart(), period.getEnd());
					List<ITimedResult<Object>> results = ctx.addExpression(expr, period.getStart(), period.getEnd());

					onRedefinedImplicit(ctx, expr.getName(), expr.getExpression(), implicit, results);

				} catch (UndefinedVariablesException e) {
					onUndefinedData(timedExpr.getValue(), e.getMessage(), timedExpr.getPeriod().getStart(),
							timedExpr.getPeriod().getEnd(), e.getVariableNames());
				} catch (Exception e) {
				}
			}

		} finally {
			if (rs != null) {
				rs.close();
			}
		}
	}

	protected void onRedefinedImplicit(ExpressionContext ctx, String name, String expr, ITimedVariable<?> implicit,
			List<ITimedResult<Object>> results) {
		if (listener == null)
			return;
		if (results == null)
			return;
		if (results.isEmpty())
			return;

		if (implicit == null)
			return;

		if (implicit instanceof IExpressionVariable<?> && ((IExpressionVariable<?>) implicit).getExpression().getScope()
				.compareTo(ExpressionScope.AGREEMENT) >= 0)
			return;

		if (isUndefined(implicit))
			return;

		if (isSystem(name, expr))
			return;

		onRedefinedImplicit(name, results.get(0), implicit);
	}

	protected void onIrpf(IrpfOutcome irpfOutcome) {
		if (listener != null)
			listener.onIrpf(irpfOutcome);

	}

	protected void onUndefinedData(IExpression expression, String message, Date start, Date end, String... variables) {
		if (listener != null)
			for (String variable : variables)
				listener.onUndefinedData(expression, variable, message, start, end);

	}

	protected void onRedefinedImplicit(String name, ITimedVariable<?> redefined, ITimedVariable<?> implicit) {
		if (listener == null)
			return;

		listener.onRedefinedImplicit(name, redefined, implicit);
	}

	protected void onMistakenPartialFactor(double monthHours, double workedHours, double factor) {
		if (listener == null)
			return;

		listener.onMistakenPartialFactor(monthHours, workedHours, factor);
	}

	protected void onContractLeaveLoaded(ResultSet rs, ExpressionContext ctx) throws SQLException {
		Date leaveStart = rs.getDate(ContractLeaveColumns.START_DATE);
		final Date start = Period.max(leaveStart, getStart());
		Date leaveEnd = rs.getDate(ContractLeaveColumns.END_DATE);
		final Date end = Period.min(leaveEnd, getEnd());

		try {
			Method guarantee = SQLContractSalaryCalculatorContext.class.getMethod("guaranteee", double.class);
			ctx.setVariable(GUARANTEE, new MethodStub(guarantee), start, end);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}

	}

	protected void loadLeaveContractFactor(ResultSet rs, ExpressionContext ctx)
			throws SQLException{
		
		String name = rs.getString(SQLConstants.CONTRACT_DATA + "." + ContractDataColumns.NAME);
		if ( name == null )
			return;
		
		String expression = rs.getString(SQLConstants.CONTRACT_DATA + "." + ContractDataColumns.EXPRESSION); 
		
		ExpressionImpl expr = new ExpressionImpl();
		expr.setName(name);
		expr.setExpression(expression);
		expr.setScope(ExpressionScope.CONTRACT);
		Date dataStart = rs.getDate(SQLConstants.CONTRACT_DATA + "." + ContractDataColumns.START_DATE);
		Date dataEnd = rs.getDate(SQLConstants.CONTRACT_DATA + "." + ContractDataColumns.END_DATE);
		Date start = Period.max(dataStart, startDate);
		Date end = Period.min(dataEnd, endDate);


		Date leaveStart = rs.getDate(SQLConstants.CONTRACT_LEAVE + "." + ContractLeaveColumns.START_DATE);
		Date leaveEnd = rs.getDate(SQLConstants.CONTRACT_LEAVE + "." + ContractLeaveColumns.END_DATE);
		
		if ( name.equals(DIRECT_PAY_START.getName())) {
			try {
				ctx.addExpression(expr, start, end);
			} catch (ExpressionException e) {
				e.printStackTrace();
			}
			return;
		}
			
		
		try {
			List<ITimedResult<Number>> factors = ctx.addExpression(expr, start, end, Number.class);
			
			for ( ITimedResult<Number> factor: factors ) {
				Period period = new Period(
						Period.max(leaveStart, factor.getPeriod().getStart()), 
						Period.min(leaveEnd, factor.getPeriod().getEnd()));
				
				double workDayHours  = getWorkDayHours(ctx, period.getStart(), period.getEnd());
				
				double fullWorkedHours = period
				.daysStream()
				.filter(day -> getDayType(day) == DayType.WORKING_DAY)
				.collect(Collectors.summingDouble(day -> workDayHours ))
				;
				
				ctx.setVariable(ContextVariable.WORKED_HOURS, 
						fullWorkedHours * factor.getValue().doubleValue(), 
						period.getStart(), 
						period.getEnd());
			}
			
			return;

		} catch (ExpressionException e) {
			e.printStackTrace();
		} 
		
		
		
	}
	
	protected  void loadContractLeave(ExpressionContext ctx) throws SQLException, ExpressionException {
		ResultSet rs = null;
		try {
			cleaveStmt.setInt(1, getId());
			rs = cleaveStmt.executeQuery();
			leaveLoader.clear();
			while (rs.next()) {
				loadLeaveContractFactor(rs, ctx);
				leaveLoader.loadContractLeave(rs, ctx);
				onContractLeaveLoaded(rs, ctx);
			}

		} finally {
			if (rs != null) {
				rs.close();
			}
		}
	}
	


	private DayType getDayType ( Calendar day ) {
		ICalendar calendar = getCalendar();
		return calendar.getDayType(day);
	}
	
	private double getWorkDayHours( ExpressionContext ctx, Date start, Date end) {
		double weekWorkDays = getWeekDaysOf(DayType.WORKING_DAY);
		Number agreementWeekHours = ctx.containsVariable(AGREEMENT_HOURS,start,end) ? 
				ctx.getVariable(AGREEMENT_HOURS,start,end, Number.class) : DEFAULT_AGRREEMENT_HOURS;
		return agreementWeekHours.doubleValue() / weekWorkDays;
	}
	

	private void loadPersonData(ExpressionContext ctx) throws SQLException {

		ctx.setVariable(MALE, Gender.MALE.ordinal(), contractStartDate, contractEndDate);
		ctx.setVariable(FEMALE, Gender.FEMALE.ordinal(), contractStartDate, contractEndDate);
		Integer gender = getInt(SQLConstants.PERSON, PersonColumns.GENDER);
		if (gender != null) {
			ctx.setVariable(GENDER, gender, contractStartDate, contractEndDate);
		}
		Date birthDate = getDate(SQLConstants.PERSON, PersonColumns.BIRTH_DATE);
		if (birthDate != null) {
			int age = getYears(birthDate, contractStartDate);
			ctx.setVariable(AGE, age, contractStartDate, contractEndDate);
		}

	}

	private void initSystemCosts() throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(SYSTEM_COST_SQL);
			java.sql.Date sqlEndDate = new java.sql.Date(this.getEnd().getTime());
			java.sql.Date sqlStartDate = new java.sql.Date(this.startDate.getTime());
			stmt.setDate(1, sqlEndDate);
			stmt.setDate(2, sqlStartDate);
			rs = stmt.executeQuery();
			systemCosts = SQLCollections.systemCostsCollection(rs);
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
			java.sql.Date sqlEndDate = new java.sql.Date(this.getEnd().getTime());
			java.sql.Date sqlStartDate = new java.sql.Date(this.startDate.getTime());
			stmt.setDate(1, sqlEndDate);
			stmt.setDate(2, sqlStartDate);

			rs = stmt.executeQuery();
			systemDeductions = SQLCollections.systemDeductionsCollection(rs);
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
			String sql = CriteriaUtilities.toSQLString(paymentsCriteria, SYSTEM_PAYMENT_SQL);
			stmt = connection.prepareStatement(sql);
			java.sql.Date sqlEndDate = new java.sql.Date(this.getEnd().getTime());
			java.sql.Date sqlStartDate = new java.sql.Date(this.startDate.getTime());
			stmt.setDate(1, sqlEndDate);
			stmt.setDate(2, sqlStartDate);

			rs = stmt.executeQuery();
			systemPayments = SQLCollections.systemPaymentsCollection(rs);
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

	private ExpressionContext getCCCExpressionContext() {
		return getCccExpressionContexts().get(new CCCContextKey(getCCCType(), getSSRegime()));
	}

	private List<Period> getPeriods(ExpressionContext ctx, String varName) {
		Period contract = new Period(contractStartDate, contractEndDate);

		List<ITimedVariable<Object>> vars = ctx.getVariables(varName);

		if (vars == null || vars.isEmpty())
			return Collections.singletonList(contract);

		List<Period> periods = joinEquals(vars);
		List<Period> holes = Period.sub(contract, periods);

		periods.addAll(holes);
		Collections.sort(periods);

		return periods;
	}

	private List<Period> joinEquals(List<ITimedVariable<Object>> vars) {

		ArrayList<TimedObject<Object>> join = new ArrayList<TimedObject<Object>>();
		ITimedVariable<Object> var = vars.get(0);
		join.add(new TimedObject<Object>(var.getValue(var.getPeriod()), var.getPeriod().getStart(),
				var.getPeriod().getEnd()));
		for (int i = 1; i < vars.size(); i++) {
			TimedObject<Object> last = join.get(join.size() - 1);

			ITimedVariable<Object> next = vars.get(i);
			Date lastEnd = last.getPeriod().getEnd();
			Date nextStart = next.getPeriod().getStart();
			Object lastValue = last.getValue(last.getPeriod());
			Object nextValue = next.getValue(next.getPeriod());

			Date afterEnd = AonDateUtils.add(lastEnd, Calendar.DAY_OF_MONTH, 1);

			if (Period.compare(afterEnd, nextStart) == 0 && AonUtils.equals(lastValue, nextValue)) {
				join.set(join.size() - 1,
						new TimedObject<Object>(nextValue, last.getPeriod().getStart(), next.getPeriod().getEnd()));
			} else {
				join.add(new TimedObject<Object>(nextValue, nextStart, next.getPeriod().getEnd()));
			}
		}
		List<Period> periods = new ArrayList<Period>();
		for (TimedObject<Object> obj : join)
			periods.add(obj.getPeriod());
		return periods;
	}

	public <T> T checkParametersNotConstant(T t, String func, String... params) {
		if (listener == null)
			return t;
		getExpressionContext().getCurrentBindings().getPeriod();
		Map<String, ITimedVariable<?>> read = getExpressionContext().getCurrentBindings().getRead();
		for (String name : read.keySet()) {
			if (name.equals(SELF))
				continue;
			if (name.equals(func))
				continue;
			if (Arrays.stream(params).anyMatch(p -> name.equals(p)))
				continue;

			return t;
		}

		return listener.onConstantParameter(func, t, getExpressionContext());
	}

	public LRUCache<CCCContextKey, ExpressionContext> getCccExpressionContexts() {
		if (cccExpressionContexts == null)
			cccExpressionContexts = new LRUCache<CCCContextKey, ExpressionContext>(CACHE_SIZE,
					new SQLSystemExpressionContextFactory(connection, this.startDate, this.getEnd(), order));
		return cccExpressionContexts;
	}
	
	protected boolean isUndefined (ITimedVariable<?> var) {
		try {
			var.getValue(var.getPeriod());
		} catch ( ExpressionExceptionWrapper wrapper){
			try {
				throw wrapper.getCause();
			} catch ( DeferredExpressionException deferred){
				try {
					deferred.eval( contractExpressionContext, Object.class);
				} catch ( UndefinedVariablesException e){
					return true;
				} catch (ExpressionException e) {
					// TODO: return true ? Really it's undefined
				}
			} catch (Throwable e) { 
				// TODO: return true ? Really it's undefined
			} 
		}
		return false;
	}
	// ------------------------------------------------------------------------

	/**
	 * ORDER BY literal.
	 */
	private static final String ORDER_BY = " ORDER BY "; //$NON-NLS-1$

	public static Integer getDomain(CCCType cccType) {
		return (-1) * (cccType.ordinal() + 100);
	}

	public static Integer getDomain(SSRegimeType ssRegime) {
		return (-1) * ssRegime.ordinal();
	}

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

	protected static ISalary getSalary(Connection connection, Date date, SalaryType type, Integer contractID)
			throws SQLException, ExpressionException, SalaryException {
		Date startDate = CommonUtil.getMonthFirstDay(date);

		// Se calcula un dia anterior a la fecha de baja.
		Date endDate = prev(date);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(SQLConstants.CONTRACT + "." + ContractColumns.ID, contractID);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(connection, startDate, endDate,
				endDate, criteria) {
			@Override
			public Collection<IContractDeduction> getContractDeductions() throws AonException {
				return Collections.emptyList();
			}

			@Override
			public Collection<IContractEmbargo> getContractEmbargos() throws AonException {
				return Collections.emptyList();
			}

			@Override
			public Collection<IContractCost> getContractCosts() throws AonException {
				// TODO Ap\E9ndice de m\E9todo generado autom\E1ticamente
				return Collections.emptyList();
			}

			@Override
			public Collection<IContractBonus> getContractBonus() throws AonException {
				// TODO Ap\E9ndice de m\E9todo generado autom\E1ticamente
				return Collections.emptyList();
			}
		};
		if (!ctx.next())
			return null;

		SalaryBuilder salaryBuilder = new SalaryBuilder();
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(salaryBuilder);

		return calculator.calculate(ctx);

	}

	public static Salary getDbSalary(Connection connection, Date date, SalaryType type, Integer contractID)
			throws SQLException {

		LeaveType type_ = LeaveType.COMMON_DISEASE;
		type_.accept(new LeaveTypeVisitor<Double>() {

			@Override
			public Double visitCommonDisease(LeaveType leaveType) {
				// TODO Ap\E9ndice de m\E9todo generado autom\E1ticamente

				return null;
			}

			@Override
			public Double visitOcupationalDisease(LeaveType leaveType) {
				// TODO Ap\E9ndice de m\E9todo generado autom\E1ticamente
				return null;
			}

			@Override
			public Double visitMaternity(LeaveType leaveType) {
				// TODO Ap\E9ndice de m\E9todo generado autom\E1ticamente
				return null;
			}

			@Override
			public Double visitPaternity(LeaveType leaveType) {
				// TODO Ap\E9ndice de m\E9todo generado autom\E1ticamente
				return null;
			}

			@Override
			public Double visitPregnacyRisk(LeaveType leaveType) {
				// TODO Ap\E9ndice de m\E9todo generado autom\E1ticamente
				return null;
			}

			@Override
			public Double visitBreastFeedingRisk(LeaveType leaveType) {
				// TODO Ap\E9ndice de m\E9todo generado autom\E1ticamente
				return null;
			}

			@Override
			public Double visitNonOcupationalDisease(LeaveType leaveType) {
				// TODO Ap\E9ndice de m\E9todo generado autom\E1ticamente
				return null;
			}

		});

		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement("SELECT * " + " FROM " + SQLConstants.SALARY + " WHERE "
					+ SalaryColumns.CONTRACT + "= ? " + " AND " + SalaryColumns.TYPE + "= ? " + " AND "
					+ SalaryColumns.START_DATE + " <= ? " + " AND " + SalaryColumns.END_DATE + " >= ? ");

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
			salary.setTotalDeduction(rs.getDouble(SalaryColumns.TOTAL_DEDUCTION));
			salary.setTotalLiquid(rs.getDouble(SalaryColumns.TOTAL_LIQUID));
			salary.setTotalEnterprise(rs.getDouble(SalaryColumns.TOTAL_ENTERPRISE));
			salary.setIssueDate(rs.getDate(SalaryColumns.ISSUE_DATE));
			salary.setRemuneration(rs.getDouble(SalaryColumns.REMUNERATION));
			salary.setExtraPayProration(rs.getDouble(SalaryColumns.PRO_EXT_BASE));
			salary.setItBase(rs.getDouble(SalaryColumns.IT_BASE));
			salary.setRawCommonBase(rs.getDouble(SalaryColumns.RAW_CGC_BASE));
			salary.setCommonBase(rs.getDouble(SalaryColumns.CGC_BASE));
			salary.setOvertimeBase(rs.getDouble(SalaryColumns.HEXTRA_BASE));
			salary.setNonEstructuralOvertimeBase(rs.getDouble(SalaryColumns.NON_HEXTRA_BASE));
			salary.setProfessionalBase(rs.getDouble(SalaryColumns.CGP_BASE));
			salary.setMoneyIrpfBase(rs.getDouble(SalaryColumns.MONEY_IRPF_BASE));
			salary.setInkindIrpfBase(rs.getDouble(SalaryColumns.INKIND_IRPF_BASE));
			salary.setIrpfBase(rs.getDouble(SalaryColumns.IRPF_BASE));
			salary.setSocialSecurityContributions(rs.getDouble(SalaryColumns.SOCIAL_SECURITY_CONTRIBUTIONS));
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

	public static Date prev(Date date) {
		return addDays2Date(date, -1);
	}

	protected static Date next(Date date) {
		return addDays2Date(date, 1);
	}

	protected static Date addDays2Date(Date date, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, days);
		return calendar.getTime();
	}

	protected static long days(Period p) {
		return CommonUtil.getDaysBetweenDates(p.getStart(), p.getEnd()) + 1;
	}

	protected static long days(Date start, Date end) {
		return CommonUtil.getDaysBetweenDates(start, end) + 1;
	}

	protected static List<Period> getMonths(Date startDate, Date endDate) {
		List<Period> months = new ArrayList<Period>();

		Date monthStart = startDate;
		Date monthEnd = getLastDayOfMonth(startDate);
		while (monthEnd.before(endDate)) {
			months.add(new Period(monthStart, monthEnd));
			monthStart = getFirstDayOfMonth(add(monthStart, MONTH, 1));
			monthEnd = getLastDayOfMonth(monthStart);
		}
		months.add(new Period(monthStart, endDate));

		return months;
	}

	protected static Date getFirstDayOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}

	protected static Date getLastDayOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return calendar.getTime();
	}

	protected static ContextVariable getDayHours(Calendar calendar) {
		return getDayOfWeekHours(calendar.get(Calendar.DAY_OF_WEEK));
	}

	protected static ContextVariable getDayOfWeekHours(int dayOfWeek) {
		Map<Integer, ContextVariable> week_days_hours = new HashMap<Integer, ContextVariable>() {
			{
				put(MONDAY, MONDAY_HOURS);
				put(TUESDAY, TUESDAY_HOURS);
				put(WEDNESDAY, WEDNESDAY_HOURS);
				put(THURSDAY, THURSDAY_HOURS);
				put(FRIDAY, FRIDAY_HOURS);
				put(SATURDAY, SATURDAY_HOURS);
				put(SUNDAY, SUNDAY_HOURS);
			}
		};
		return week_days_hours.get(dayOfWeek);

	}

	protected static boolean isSystem(String name, String expr) {
		return AonStringUtils.isNotEmpty(expr)
				&& expr.matches("\\s*SISTEMA\\s*\\(\\s*['\"]" + name + "['\"]\\s*\\)\\s*;*\\s*");
	}

	protected static boolean isWholeMonth(Period period) {
		return AonDateUtils.get(period.getStart(), Calendar.DAY_OF_MONTH) == 1 && AonDateUtils.get(period.getEnd(),
				Calendar.DAY_OF_MONTH) == AonDateUtils.getMax(period.getEnd(), Calendar.DAY_OF_MONTH);
	}

	protected static boolean isLastMonthPeriod(Period period) {
		return AonDateUtils.get(period.getEnd(),
				Calendar.DAY_OF_MONTH) == AonDateUtils.getMax(period.getEnd(), Calendar.DAY_OF_MONTH);
	}

	@Override
	public Date getDate(String tableLabel, String columnLabel) {
		try {
			return this.resultSet.getDate(tableLabel + "." + columnLabel);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
