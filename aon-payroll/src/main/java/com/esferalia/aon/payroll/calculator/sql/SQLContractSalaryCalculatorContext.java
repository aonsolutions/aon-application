package com.esferalia.aon.payroll.calculator.sql;

import static com.code.aon.common.util.CommonUtil.getDaysBetweenDates;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.EnterpriseData.ENTERPRISE_DATA;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext.SQLNoItContractSalaryCalculatorContext.split;
import static com.esferalia.aon.payroll.calculator.sql.SQLSystemExpressionContextFactory.DEFAULT_AGRREEMENT_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ABS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ACTUAL_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.AGE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.AGREEMENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.AGREEMENT_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ASSIMILATED;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.BONUS_AGE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.BONUS_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.BONUS_START;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.BR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CONTEXT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CONTRACT_END;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CONTRACT_START;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.DELAY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.DIRECT_PAY_START;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.DROP_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.DROP_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.EFECTIVE_END;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.EFECTIVE_START;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.END;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE_BACK;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.EVERYTHING;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.EXTRA_PAY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FEMALE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FRIDAY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FRIDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FULL_ERE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FULL_TIME;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.GENDER;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.GROSS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.GUARANTEE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.GUARANTEED_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.HOLIDAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.IMS_RATE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.INDEFINITE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.IRPF_PERCENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.IT_END;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.IT_LENGTH;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.IT_RATE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.IT_START;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.LIQUID;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MALE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONDAY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MORE_THAN_65;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.NATURAL_MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.NON_WORKED_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.NON_WORKING;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.OCCUPATION;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.OFF_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PARTIAL_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PATERNITY_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PAYMENT_VARIABLE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_GROUP;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.REDEFINE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.REGULATORY_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SALARY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SALARY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SALARY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SATURDAY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SATURDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SELF;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SENIORITY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SENIORITY_START;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SETTLE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SHORT_CONTRACT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.START;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRIKE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRIKE_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SUM;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SUNDAY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SUNDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SYSTEM;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.THURSDAY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.THURSDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TODAY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TOTAL_LIQUID;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TOTAL_WORKED_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TUESDAY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TUESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WEDNESDAY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WEDNESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WEEK_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_YEARS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKING_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORK_DAYS;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.PegasusSolver;
import org.apache.commons.math3.analysis.solvers.UnivariateSolver;
import org.jooq.Condition;
import org.jooq.DSLContext;
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
import com.esferalia.aon.jooq.tables.records.ContractDataRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.payroll.DelegateCollection;
import com.esferalia.aon.payroll.DelegateIterator;
import com.esferalia.aon.payroll.IrpfOutcome;
import com.esferalia.aon.payroll.Pair;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.AbstractContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.CompositeCollection;
import com.esferalia.aon.payroll.calculator.CompositeCosts;
import com.esferalia.aon.payroll.calculator.CompositePayments;
import com.esferalia.aon.payroll.calculator.ContextFunctions;
import com.esferalia.aon.payroll.calculator.ContractLeaveLoader;
import com.esferalia.aon.payroll.calculator.ContractLeaveLoader.Leave;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.DelegateSystemPayment;
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
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.TaxCalculator;
import com.esferalia.aon.payroll.calculator.UndefinedContextVariablesException;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.LeaveTypeVisitor;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext;
import com.esferalia.aon.payroll.irpf.IrpfCalculator;
import com.esferalia.aon.payroll.irpf.sql.SQLIrpfCalculatorContext;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementColumns;
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
import com.esferalia.aon.salary.enumeration.BonusType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.CheckException;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionContext.DeferredExpressionException;
import com.esferalia.aon.salary.expression.ExpressionContext.ExpressionExceptionWrapper;
import com.esferalia.aon.salary.expression.ExpressionContext.MacroException;
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
import com.esferalia.aon.salary.expression.TimedObject;
import com.esferalia.aon.salary.expression.TimedResult;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;
import com.esferalia.aon.salary.expression.Variables;
import com.esferalia.aon.salary.expression.Variables.NotFoundHandler;
import com.esferalia.aon.salary.expression.Variables.NotFoundVariableError;
import com.esferalia.aon.salary.expression.Variables.PeriodMap;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.AonUtils;

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


	private static final String COST_SQL = "SELECT *" 
			+ ", " + ExpressionScope.CONTRACT.ordinal() + " AS " + SQLContractCost.SCOPE_ALIAS 
			+ " FROM contract_cost" 
			+ " WHERE contract = ? " 
			+ " AND start_date <= ? "
			+ " AND ( end_date IS NULL" + " OR end_date >= ? )";

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
			+ ", ( SELECT sum(amount) FROM salary_embargo INNER JOIN salary on ( salary_embargo.salary = salary.id ) WHERE contract_embargo=contract_embargo.id AND issue_date < ? ) AS "
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
			+ " ON payment_concept = payment_concept.id" + " WHERE start_date <= ? " + " AND ( end_date IS NULL"
			+ " OR end_date >= ? )" + " AND " + SQLContractPayment.PAYMENT_ALIAS + ".domain <= 0 "
			//+ " ORDER BY payment_concept.code "
			;

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
				+ " AND ( contract_data.start_date <= ? )"
				+ " AND ( contract_data.end_date IS NULL OR contract_data.end_date >= ? ) "
				+ ")"
			+ " LEFT JOIN contract ON ( contract_leave.contract = contract.id ) "
			+ " WHERE"
			+ " ( contract.person IN ( SELECT person FROM contract WHERE id = ? ) )" 
			+ " AND contract_leave.start_date <= ? "
			+ " AND ( contract_leave.end_date IS NULL " + " OR contract_leave.end_date >= ? )"
			+ " AND contract_leave.id >= 0 "
			+ " ORDER BY FIELD(contract_leave.type,0,1,6,7,8,2,3,4,5)"
			+ ", FIELD(contract_data.name, '" + PATERNITY_FACTOR + "','" +MATERNITY_FACTOR + "','" + DIRECT_PAY_START + "')"
			;

	private static final int CACHE_SIZE = 25;

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

	private static final Map<Integer, ContextVariable> WEEK_DAYS_VARIABLES = new HashMap<Integer, ContextVariable>() {
		{
			put(SUNDAY, SUNDAY_DAYS);
			put(MONDAY, MONDAY_DAYS);
			put(TUESDAY, TUESDAY_DAYS);
			put(WEDNESDAY, WEDNESDAY_DAYS);
			put(THURSDAY, THURSDAY_DAYS);
			put(FRIDAY, FRIDAY_DAYS);
			put(SATURDAY, SATURDAY_DAYS);
		}
	};

	private static final class NoopSQLContractLeaveLoader extends SQLContractLeaveLoader {
		private NoopSQLContractLeaveLoader(Date startDate, Date endDate) {
			super(startDate, endDate);
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public Long getCommonDiseaseDays(Period p) {
			return 0L;
		}

		@Override
		public Long getLeaveDays(Period p, LeaveType type) {
			return 0L;
		}

		@Override
		public SortedSet<Leave> getLeaves() {
			return Collections.emptySortedSet();
		}

		@Override
		public Long getProfessionalDiseaseDays(Period p) {
			return 0L;
		}

		@Override
		public Long getLeavesDays(Period p) {
			return 0L;
		}

		@Override
		public boolean isLeaveDay(Calendar day) {
			return false;
		}

		@Override
		public void clear() {
		}

		@Override
		public void clean(ExpressionContext exprCtx, Leave leave) {
		}

		@Override
		public void loadContractLeave(ResultSet rs, ExpressionContext exprCtx)
				throws SQLException, ExpressionException {
		}

		@Override
		public void loadContractLeave(Integer id, Date leaveStart, Date leaveEnd, long parentDays, LeaveType type,
				String dailyRegBase, ExpressionContext exprCtx) throws ExpressionException {
		}

		@Override
		public void loadContractLeave(Integer id, Date leaveStart, Date leaveEnd, long parentDays, LeaveType type,
				Double dailyRegBase, ExpressionContext exprCtx) throws ExpressionException {
		}

		@Override
		protected void remove(Leave leave) {
		}

		@Override
		protected void add(Leave leave) {
		}
	}

	public static interface NextHook {
		default void beforeLoadLeaves(ExpressionContext ctx) throws ExpressionException{};
		void beforeLoadDaysContextVariables(ExpressionContext ctx) throws ExpressionException;
	}

	protected class ContractExpressionContext extends ExpressionContext {

		private ContractExpressionContext(ExpressionContext expressionContext, NotFoundHandler notFoundHandler) {
			super(expressionContext, notFoundHandler);
		}

		@Override
		public <T> List<ITimedResult<T>> eval(String script, Date start, Date end, Class<T> toType)
				throws ExpressionException, UndefinedVariablesException {
			try {
				script = zeroGuarantee(script, start, end);
				return super.eval(script, start, end, toType);
			} catch (UndefinedVariablesException e) {

				if (e.getExpression() == null)
					e.setExpression(script);
				throw e;
			}
		}

		// --------------------------------------------------------------------
		protected String zeroGuarantee(String script, Date start, Date end ) {
			if (script == null)
				return null;
			
			if ( !script.contains(GUARANTEE))
				return script;
			
			Set<String> inputs = getVarNames(script);
			try {
				List<PeriodMap> bindingsList = getBindingsNew(inputs, start, end);
				if ( bindingsList.size() > 0 )
					return script;
			} catch (UndefinedVariablesException e) {
			}

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
		private Integer regime;
		private Integer agreementId;
		private Integer agreementLevelId;

		public AgreementContextKey(Integer regime, Integer domain, Integer agreementId, Integer agreementLevelId) {
			this.domain = domain;
			this.regime = regime;
			this.agreementId = agreementId;
			this.agreementLevelId = agreementLevelId;
		}

		public Integer getDomain() {
			return domain;
		}
		
		public Integer getRegime() {
			return regime;
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
					&& AonUtils.equals(regime, ((AgreementContextKey) obj).regime)
					&& AonUtils.equals(agreementId, ((AgreementContextKey) obj).agreementId)
					&& AonUtils.equals(agreementLevelId, ((AgreementContextKey) obj).agreementLevelId);
		}

		@Override
		public int hashCode() {
			return AonUtils.hashCode(domain) + AonUtils.hashCode(regime) + AonUtils.hashCode(agreementId) + AonUtils.hashCode(agreementLevelId);
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
					
					Date itStart = ContractLeaveLoader.getStartDate(type, leaveStart);

					if (start < 0)
						return;
					Date leaveEnd4End = leaveEnd;
					Date leaveEnd4Length = leaveEnd == null ? Period.max(endDate, new Date()) : leaveEnd;
					leaveEnd = Period.min(endDate, leaveEnd);
					
					Period leavePeriod = new Period(itStart, leaveEnd);

					Calendar leaveCalendar = Calendar.getInstance();
					leaveCalendar.setTime(itStart);
//					leaveCalendar.add(Calendar.DATE, start /*- (int) parentDays*/);
					
					if ( itStart.compareTo(startDate) < 0 )
						parentDays = Math.max(parentDays - getDaysBetweenDates(itStart, startDate), 0);
					
					leaveCalendar.add(Calendar.DATE, Math.max(start - (int) parentDays, 0));
					Date guarenteeStart = leaveCalendar.getTime();
					leaveCalendar.add(Calendar.DATE, end - start);
					Date guarenteeEnd = Period.min(leaveEnd, leaveCalendar.getTime());
					
					if ( startDate.after(guarenteeEnd ))
						return;
					if ( guarenteeStart.after(guarenteeEnd ))
						return;

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
						if ( startDate.after(p.getEnd() ))
							continue;
						long leaveParentDays = CommonUtil.getDaysBetweenDates(itStart, p.getStart());
						super.loadContractLeave(id, p.getStart(), p.getEnd(), parentDays + leaveParentDays, type,
								dailyRegBase, exprCtx);
					}

					// Adds 'BASE_REGULADORA' variable for guaranteed period
					exprCtx.setVariable(IT_START, itStart, guarenteeStart, guarenteeEnd);
					exprCtx.setVariable(IT_LENGTH, new Period(itStart, leaveEnd4Length), guarenteeStart, guarenteeEnd);
					if ( leaveEnd4End != null ) exprCtx.setVariable(IT_END, leaveEnd4End, guarenteeStart, guarenteeEnd);
					
					Period period = new Period(Period.max(guarenteeStart, startDate), guarenteeEnd);
//					int guaranteedDays = (int) getGuaranteedDays(exprCtx,period);
//					exprCtx.setVariable(GUARANTEED_DAYS, guaranteedDays, guarenteeStart, guarenteeEnd);

					exprCtx.putVariable(GUARANTEED_DAYS, new ITimedVariable<Double>() {
						
						@Override
						public Period getPeriod() {
							return period;
						}
						@Override
						public Double getValue(Period p ) {
							return getGuaranteedDays(exprCtx,p);
						}
					});

					ExpressionImpl exp = new ExpressionImpl();
					exp.setName(EVERYTHING.getName());
					if (dailyRegBase != null) {
						//exp.setExpression(String.format("%f * %d ", dailyRegBase, guaranteedDays));
						exp.setExpression(String.format(Locale.US, "%f * %s ", dailyRegBase, GUARANTEED_DAYS));
					} else {
						//exp.setExpression(String.format("SELF.br(%s) * %d", IT_START, guaranteedDays));
						exp.setExpression(String.format(Locale.US,"SELF.br(%s) * %s", IT_START, GUARANTEED_DAYS));
					}
					exprCtx.addLazyExpression(exp, guarenteeStart, guarenteeEnd);

//					exprCtx.putVariable(ContextVariable.REGULATORY_BASE,
//							new TimedObject<Double>(0.00, guarenteeStart, guarenteeEnd));
					
					exprCtx.putVariable(ContextVariable.REGULATORY_BASE,
					new ITimedVariable() {
						@Override
						public Object getValue(Period period) {
							try {
								return SQLNoItContractSalaryCalculatorContext.this.br(itStart);
							} catch (ExpressionException | SalaryException | SQLException e) {
								return 0.00;
							}
						}
						
						@Override
						public Period getPeriod() {
							return new Period(guarenteeStart, guarenteeEnd);
						}
					});

					exprCtx.setVariable(ContextVariable.LEAVE_DAYS, 0, guarenteeStart, guarenteeEnd);

					type.accept(new LeaveTypeVisitor<Void>() {

						@Override
						public Void visitCommonDisease(LeaveType leaveType) {
							exprCtx.setVariable(ContextVariable.COMMON_DISEASE_DAYS, 0, guarenteeStart, guarenteeEnd);
							return null;
						}
						
						@Override
						public Void visitCommonDiseaseAtLack(LeaveType leaveType) {
							exprCtx.setVariable(ContextVariable.COMMON_DISEASE_LACK_DAYS, 0, guarenteeStart, guarenteeEnd);
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
						
						@Override
						public Void visitCommonProfessionalDisease(LeaveType leaveType) {
							return visitOcupationalDisease(leaveType);
						}
						
						@Override
						public Void visitMenstruation(LeaveType leaveType) {
							exprCtx.setVariable(ContextVariable.MENSTRUATION_DAYS, 0, guarenteeStart, guarenteeEnd);
							return null;
						}
						
						@Override
						public Void visitPregnacyStop(LeaveType leaveType) {
							exprCtx.setVariable(ContextVariable.PREGNANCY_STOP_DAYS, 0, guarenteeStart, guarenteeEnd);
							return null;
						}
						
						@Override
						public Void visitPregnacy39Week(LeaveType leaveType) {
							exprCtx.setVariable(ContextVariable.PREGNANCY_39_WEEK_DAYS, 0, guarenteeStart, guarenteeEnd);
							return null;
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
				TimedResult<Double> result = new TimedResult<Double>(guarentee, period, getCurrentBindings().getRead());
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
				protected String zeroGuarantee(String script, Date start,Date end) {
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
			
			getEreDaysVars().forEach( v -> ctx.removeVariable(v) );
			getEreFactorsVars().forEach( v -> ctx.removeVariable(v) );

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
			for ( w++; w < worked.size(); w++ )
				periods.add(worked.get(w));

			return periods;
		}

		@Override
		protected double getWorkDays(ExpressionContext ctx, Period p) {
			Double workDays = super.getWorkDays(ctx, p);
			return getDays(workDays, ctx, p);
		}
		
		public void throwGuarenteeException() throws GuarenteeException {
			if ( guarentees!= null && !guarentees.isEmpty()  ) {
				throw new GuarenteeException(guarentees);
			}
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

			if (p.getStart().equals(getStart())/* && p.getEnd().equals(getEnd())*/)
				return workDays;

			if (p.getStart().equals(getStartDate()) /*&& p.getEnd().equals(getEndDate())*/)
				return workDays;

			return super.leaveLoader.getAdjustDays(ctx, p, workDays.longValue());
		}

		@Override
		protected void onContractLeaveLoaded(ResultSet rs, ExpressionContext ctx) {
			// Skip load GUARANTEE, that is already loaded
		}
		
		@Override
		protected double getActiveDays(Period p) {
			try {
				double activeDays = getExpressionContext().getVariables(ContextVariable.ACTIVE_DAYS, p.getStart(), p.getEnd())
				.stream()
				.map ( v -> v.getValue(v.getPeriod()) )
				.filter( v -> (v != null) && (v instanceof Number ))
				.collect(Collectors.summingDouble( v -> ((Number) v).doubleValue() ))
				;
				return activeDays;
			} catch (Throwable t ) {
				return 0.00;
			}
		}
		
		@Override
		protected ISQLContractSalaryCalculatorContext getPaymentCalculatorContext(Connection conn, Date startDate,
				Date endDate, Date issueDate, Criteria criteria, double x) {
			return super.getPaymentCalculatorContext(conn, startDate, endDate, issueDate, criteria, x, (ctx) -> {
				getEreDaysVars().forEach( v -> ctx.removeVariable(v) );
				getEreFactorsVars().forEach( v -> ctx.removeVariable(v) );
			});
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
	private PreparedStatement costStmt;

	private Collection<IContractPayment> contractPayments;
	private SQLContractDeduction sqlContractDeduction;
	private SQLContractCost sqlContractCost;
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
	
	private Set<IContractBonus> contextBonus; 

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
		initCostStmt();
		initBonusStmt();
		initEmbargoStmt();
		initCeventStmt();
		initLeaveStmt();
		initSystemCosts();
		initSystemDeductions();
		initSystemPayments();

		this.contractPayments = Collections.emptyList();

		this.sqlContractDeduction = new SQLContractDeduction();
		this.sqlContractCost = new SQLContractCost();
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
		this.contextBonus = new HashSet<IContractBonus>();

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
	public String getEmployeeCity() {
		return "-";
	}

	@Override
	public String getEmployeeAddress() {
		return "-";
	}

	@Override
	public String getEnterpriseCity() {
		//TODO: hacer un JOIN en el MAIN_SQL con geozone
//		String geozoneName = getString(SQLConstants.GEOZONE, SQLConstants.GeozoneColumns.NAME);
		String geozone = getString(SQLConstants.RADDRESS, SQLConstants.RaddressColumns.GEOZONE);
		return geozone;
	}

	@Override
	public String getEnterpriseDocument() {
		return getString(ENTERPRISE_REGISTRY, RegistryColumns.DOCUMENT);
	}

	@Override
	public SSRegimeType getSSRegime() {
		int ordinal = getInt(SQLConstants.CONTRACT, ContractColumns.SS_REGIME);
		return getSSsRegimeType(ordinal);
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
	public Collection<ISystemPayment> getSystemPayments() {
		return systemPayments;
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
			int id = getId();
			paymentStmt.setInt(1, id);
			ResultSet rs = paymentStmt.executeQuery();
			this.contractPayments = SQLCollections.contractPaymentsCollection(rs);
			
			Collection<IContractPayment> contractAgreementPayments = getAgreementPayments();
			
			return new CompositePayments(this.contractPayments, contractAgreementPayments, /*getDefaultAgreementPayments(),*/ getCCCPayments(), getSSRegimePayments()) {
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
			embargoStmt.setInt(2, id);
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
		try {
			this.sqlContractCost.close();
			int id = getId();
			costStmt.setInt(1, id);
			ResultSet rs = costStmt.executeQuery();
			this.sqlContractCost.setResultSet(rs);
			CompositeCosts hierarchyCosts = new CompositeCosts(this.sqlContractCost,
					getCCCCosts(), getSSRegimeCosts()) {
				@Override
				protected int getLevel(IContractCost item) {
					return ((ISystemCost) item).getDomain();
				}
			};
			return hierarchyCosts;
		} catch (SQLException e) {
			throw new AonException(e);
		}

//		return new CompositeCosts(getCCCCosts(), getSSRegimeCosts()) {
//			@Override
//			protected int getLevel(IContractCost item) {
//				return ((ISystemCost) item).getDomain();
//			}
//		};
	}

	@Override
	public Collection<IContractBonus> getContractBonus() throws AonException {
		try {
			this.sqlContractBonus.close();
			int id = getId();
			bonusStmt.setInt(1, id);
			ResultSet rs = bonusStmt.executeQuery();
			this.sqlContractBonus.setResultSet(rs);
			return new CompositeCollection<IContractBonus>( contextBonus, sqlContractBonus );
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
		if (this.costStmt != null) {
			this.costStmt.close();
			this.costStmt = null;
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
	    	Period defPeriod = new Period(this.contractStartDate, this.contractEndDate); 
	    	this.contractExpressionContext.getVariables(name).stream().map( v -> v.getPeriod())
	    	.collect(() -> new HashSet<Period>(Collections.singleton(defPeriod)), Set::add, Set::addAll)
	    	.forEach( p -> this.contractExpressionContext.setVariable(name, t, p.getStart(), p.getEnd()));
	}

	public <T> T getVariable(ContextVariable var, Class<T> toType) {
		return getVariable(var.getName(), toType);
	}

	public <T> List<T> getValues(ContextVariable var, Class<T> toType) {
		List<ITimedVariable<T>> variables = this.contractExpressionContext.getVariables(var.getName(), this.contractStartDate, this.contractEndDate);
		return variables.stream().map(v -> v.getValue(v.getPeriod())).collect(Collectors.toList());
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

	public List<Period> getPeriods(ContextVariable var) {
		 return this.contractExpressionContext
			.getVariables(var.getName())
			.stream()
			.map(v->v.getPeriod())
			.collect(Collectors.toList())
			;
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

	public void addBonus (String description, String expression) {
		addBonus(getStartDate(), null, description, expression);
	}

	public void addBonus (String name, String description, String expression) {
		addBonus(name, getStartDate(), null, description, expression);
	}

	public void addBonus (Date startDate, String description, String expression) {
		addBonus(startDate, null, description, expression);
	}
	
	public void addBonus (Date startDate, Date endDate, String description, String expression) {
		addBonus(null, startDate, endDate, description, expression);
	}
	
	public void addBonus (String name, Date startDate, Date endDate, String description, String expression) {
		IContractBonus bonus = new IContractBonus() {
			
			@Override
			public Date getStartDate() {
				return startDate;
			}
			
			@Override
			public Date getEndDate() {
				return endDate;
			}
			
			@Override
			public BonusType getType() {
				return null;
			}
			
			@Override
			public double getAmount() {
				return 0;
			}
			
			@Override
			public boolean isReadOnly() {
				return true;
			}
			
			@Override
			public ExpressionScope getScope() {
				return ExpressionScope.SYSTEM;
			}
			
			@Override
			public String getName() {
				return name;
			}
			
			@Override
			public Integer getId() {
				return (int)(Math.random()*Integer.MAX_VALUE);
			}
			
			@Override
			public String getExpression() {
				return String.format("/*read-only*/%s/**/", expression);
			}
			
			@Override
			public String getDescription() {
				return description;
			}
			
			@Override
			public boolean equals(Object obj) {
				if ( this == obj )
					return true;
				
				if (!( obj instanceof IContractBonus ))
					return false;
				
				IContractBonus bonus = (IContractBonus) obj;
				return ( AonUtils.equals(this.getStartDate(), bonus.getStartDate())
						&& AonUtils.equals(this.getEndDate(), bonus.getEndDate())
						&& AonUtils.equals( this.getExpression(), bonus.getExpression())
						&& AonUtils.equals( this.getDescription(), bonus.getDescription()) )
						;
					
			}
			
			@Override
			public int hashCode() {
			    int hash = 7;
			    hash = 31 * hash + (startDate == null ? 0 : startDate.hashCode());
			    hash = 31 * hash + (endDate == null ? 0 : endDate.hashCode());
			    hash = 31 * hash + (expression == null ? 0 : expression.hashCode());
			    hash = 31 * hash + (description == null ? 0 : description.hashCode());
			    return hash;
			}

		};
		
		contextBonus.add(bonus);
	}

//	protected Collection<ISystemPayment> getDefaultAgreementPayments() {
//		
//		
//		if ( getAgreementKey() != null ) 
//			return Collections.emptyList();
//		
//		return new DelegateCollection<ISystemPayment>(agreementPayments.get(getDefaultAgreementKey())) {
//			@Override
//			public Iterator<ISystemPayment> iterator() {
//				return new DelegateIterator<ISystemPayment>(super.iterator()) {
//					@Override
//					public boolean hasNext() {
//						try {
//							return !SQLContractSalaryCalculatorContext.this.sqlContractPayment.getResultSet().isAfterLast() && super.hasNext();
//						} catch (SQLException e) {
//							return false;
//						}
//					}
//				};
//			}
//		};
//	}

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

	protected Integer getSSRegimeId() {
		Object id = getObject(SQLConstants.CONTRACT, ContractColumns.SS_REGIME);
		return id == null ? null : (Integer) id;
	}

	protected Integer getAgreementId() {
		Object id = getObject(SQLConstants.AGREEMENT, AgreementColumns.ID);
		return id == null ? null : (Integer) id;
	}

	protected Integer getAgreementDomain() {
		Object domain = getObject(SQLConstants.AGREEMENT, AgreementColumns.DOMAIN);
		return domain == null ? null : (Integer) domain;
	}

	protected Integer getEnterpriseId() {
		Object domain = getObject(SQLConstants.ENTERPRISE, EnterpriseColumns.REGISTRY);
		return domain == null ? null : (Integer) domain;
	}

	protected Integer getEnterpriseDomain() {
		Object domain = getObject(SQLConstants.ENTERPRISE, EnterpriseColumns.DOMAIN);
		return domain == null ? null : (Integer) domain;
	}

	protected AgreementKey getAgreementKey() {
		Object id = getObject(SQLConstants.AGREEMENT, AgreementColumns.ID);
		Object domain = getObject(SQLConstants.AGREEMENT, AgreementColumns.DOMAIN);
		Object regime = getObject(SQLConstants.CONTRACT, ContractColumns.SS_REGIME);
		return id == null ? null : new AgreementKey((Integer) id, (Integer) domain, (Integer) regime);
	}

	protected AgreementKey getDefaultAgreementKey() {
		return new AgreementKey(0, 0, 0);
	}

	protected double getActiveDays(Period p) {
		
		int startDay = AonDateUtils.get(p.getStart(), DAY_OF_MONTH);
		//The first day of the month has value 1. ??? 
		if ( startDay == 1 )
			return 0.00; 
		
		Date start  = AonDateUtils.getFirstDayOfMonth(p.getStart());
		Date end  = AonDateUtils.getLastDayOfMonth(p.getStart()); //AonDateUtils.add(p.getStart(), DAY_OF_MONTH,-1);
		
		double activeDays = getExpressionContext().getVariables(ContextVariable.ACTIVE_DAYS, start, end).stream()
		.map ( v -> v.getValue(v.getPeriod()) )
		.filter( v -> ( v != null ) && ( v instanceof Number ))
		.collect(Collectors.summingDouble( v -> ((Number) v).doubleValue() ))
		;

		if (activeDays > 0.00 )
			return activeDays;
		
		Date contractStart = getContractStartate();
		if ( contractStart.compareTo(p.getStart() ) == 0) 
			return getActiveDaysFromAnotherContract(contractStart);
		
		end  = AonDateUtils.add(p.getStart(), Calendar.DAY_OF_MONTH,-1);
		
		activeDays = getExpressionContext().getVariables(ContextVariable.WORKED_DAYS, start, end)
		.stream().map( v -> (Double) v.getValue(v.getPeriod()) ).collect(Collectors.summingDouble( v -> v ))
		;
		
		double partialFactor = getCurrentBindings().get(PARTIAL_FACTOR,
				obj -> ((Number) obj).doubleValue(), 1.00);
		activeDays /= partialFactor;
		
		for ( ContextVariable variable: new ContextVariable[] {ContextVariable.DROP_DAYS, ContextVariable.STRIKE_FACTOR, ContextVariable.NON_WORKED_DAYS} ) {
		    try {
			activeDays += getExpressionContext().eval(variable.getName(), start, end).stream()
			.map( r -> ((Number) r.getValue()).doubleValue() ).collect(Collectors.summingDouble( v -> v ));
		    } catch( Exception e ){
		    }
		}


		return activeDays;
	}

	protected double getActiveDaysFromAnotherContract(Date contractStart) {
		Date endDate = AonDateUtils.add(contractStart, Calendar.DAY_OF_MONTH,-1);
		Date startDate = AonDateUtils.getFirstDayOfMonth(contractStart);
		
		ResultSet rs  = null;
		PreparedStatement stmt = null;
		try {
			stmt = 
			connection.prepareStatement(
			"SELECT "
			+ "contract.end_date "
			+ ", contract.start_date "
			+ "FROM contract  "
			+ "WHERE contract.person = ? "
			+ "AND contract.enterprise_ccc = ? "
			+ "AND contract.start_date <= ? "
			+ "AND contract.end_date >= ? "
			+ "AND contract.end_date < ? "
			);
			

			stmt.setInt(1, getInt(SQLConstants.CONTRACT, ContractColumns.PERSON) );
			stmt.setInt(2, getInt(SQLConstants.CONTRACT, ContractColumns.ENTERPRISE_CCC) );
			stmt.setDate(3, toSqlDate(endDate) );
			stmt.setDate(4, toSqlDate(startDate) );
			stmt.setDate(5, toSqlDate(contractStart));
			
			int days = 0;
			rs = stmt.executeQuery();
			while ( rs.next() ) {
				Date contractEndDate = rs.getDate(ContractColumns.END_DATE);
				Date contractStartDate = rs.getDate(ContractColumns.START_DATE);
				rs.getDate(ContractColumns.END_DATE);
				days += new Period(Period.max(contractStartDate, startDate), contractEndDate).daysStream().count();
			}
			return days;
			
		} catch (Exception e) {
		} finally {
			if ( rs != null ) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if ( stmt != null ) {
				try {
					stmt.close();
				} catch (SQLException e) {
				}
			}
		}

		return 0.00;
	}

	protected AgreementKey getEnterpriseAgreementKey() {
		Object id = getObject(SQLConstants.AGREEMENT, AgreementColumns.ID);
		Object domain = getObject(SQLConstants.ENTERPRISE, EnterpriseColumns.DOMAIN);
		Object regime = getObject(SQLConstants.CONTRACT, ContractColumns.SS_REGIME);
		return id == null ? null : new AgreementKey((Integer) id, (Integer) domain, (Integer) regime);
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
		paymentSql = orderBy(paymentSql, CODE);
		this.paymentStmt = this.connection.prepareStatement(paymentSql);
		this.paymentStmt.setDate(2, toSqlDate(this.getEnd()));
		this.paymentStmt.setDate(3, toSqlDate(this.startDate));
	}

	private void initCostStmt() throws SQLException {
		this.costStmt = this.connection.prepareStatement(COST_SQL);
		this.costStmt.setDate(2, toSqlDate(this.getEnd()));
		this.costStmt.setDate(3, toSqlDate(this.startDate));
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
		this.embargoStmt.setDate(1, toSqlDate(this.issueDate));
		this.embargoStmt.setDate(3, toSqlDate(this.getEnd()));
		this.embargoStmt.setDate(4, toSqlDate(this.startDate));
	}

	private void initCeventStmt() throws SQLException {

		String sql = orderBy(CDATA_SQL, order);
		this.ceventStmt = this.connection.prepareStatement(sql);
	}

	private void initLeaveStmt() throws SQLException {
		this.cleaveStmt = this.connection.prepareStatement(CLEAVE_SQL);
		this.cleaveStmt.setDate(1, toSqlDate(this.getEnd()));
		this.cleaveStmt.setDate(2, toSqlDate(this.startDate));
		this.cleaveStmt.setDate(4, toSqlDate(this.getEnd()));
		this.cleaveStmt.setDate(5, toSqlDate(this.startDate));
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

		Integer ssRegimeId = getSSRegimeId();
		Integer agreementId = getAgreementId();
		Integer agreementLevelId = getAgreementLevel();
		Integer agreementDomain = getAgreementDomain();

		AgreementContextKey agreementAndLevelKey = new AgreementContextKey(ssRegimeId, agreementDomain, agreementId,
				agreementLevelId);

		ExpressionContext agreementCtx = agreementExpressionContexts.get(agreementAndLevelKey);

		Integer enterpriseDomain = getEnterpriseDomain();
		AgreementContextKey enterpriseAndLevel = new AgreementContextKey(ssRegimeId, enterpriseDomain, agreementId,
				agreementLevelId);
		ExpressionContext enterpriseCtx = agreementExpressionContexts.get(enterpriseAndLevel);

		ExpressionContext ctx = new ExpressionContext(agreementCtx);
		ctx.add(enterpriseCtx);

		return ctx;
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
				payments.add(new DelegateSystemPayment(systemPayment) {
					@Override
					public ExpressionScope getScope() {
						return ExpressionScope.APPLICATION;
					}
				});
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

	public ISQLContractSalaryCalculatorContext getNoItContractSalaryCalculatorContext() {

		final Criteria contractCriteria = new Criteria();
		contractCriteria.addExpression(criteria.getExpression());
		contractCriteria.addEqualExpression(SQLConstants.CONTRACT + "." + ContractColumns.ID, getId());

		ISQLContractSalaryCalculatorContext ctx = getNoItCalculatorContext(connection, startDate, endDate, endDate, contractCriteria, -1,
				Integer.MAX_VALUE - 1);
		return ctx;
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

		Period guaranteePeriod = new Period(
				getCurrentBindings().getPeriod().getStart(),
				getCurrentBindings().getPeriod().getEnd());

		if (leaveLoader.isEmpty())
			throw new UndefinedContextVariablesException(ContextVariable.LEAVE_DAYS);

		Double totalPayment = getVariable(ContextVariable.TOTAL_PAYMENT, Double.class);
		// Assert all PREST_IT payments have been calculated.
		// Double prestIts = getVariable(PREST_IT, Double.class);
		// if (prestIts == null && totalPayment == null)
		//  throw new UndefinedVariablesException(PREST_IT);
		if ( totalPayment == null )
			for ( Leave leave : leaveLoader.getLeaves() )
				if ( Period.compare(leave.getStart(), getEndDate() ) <= 0 
						&& !hasVariable(PREST_IT, leave.getStart(), leave.getEnd()) )
					throw new UndefinedVariablesException(PREST_IT);
		

		final Criteria contractCriteria = new Criteria();
		contractCriteria.addExpression(criteria.getExpression());
		contractCriteria.addEqualExpression(SQLConstants.CONTRACT + "." + ContractColumns.ID, getId());
		IContractSalaryCalculatorContext ctx = null;
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
			throwGuarenteeException(ctx);
		} catch (GuarenteeException e) {
			Double extraPayment = getVariable(ContextVariable.EXTRA_PAYMENT, Double.class);
			if ( totalPayment != null && extraPayment != null )
			    totalPayment -= extraPayment;
			
			List<ITimedResult<Double>> guarenteeResults = e.getGuarentees(guaranteePeriod);
			
			double guarenteed = 0.00;
			long guarenteeDays = 0;
			for ( ITimedResult<Double> r : guarenteeResults ) {
				guarenteed += fixGuarantee(r);
				guarenteeDays += days(r.getPeriod());
			}
						
			if ( guarenteeResults.size() == 1 
					&& AonUtils.equals(guarentee, guarenteed /*guarenteeResults.get(0).getValue()*/))
				return onConstantGuarantee(guarenteed, totalPayment);
			
			long allDays = days(contractStartDate, contractEndDate);

			// all salary days guaranteed, easier.
			if ( allDays == guarenteeDays ) 
				return onAllGuarantee(guarenteed, totalPayment);

			for ( Period p : getPeriods(WORKED_DAYS.getName()))
				guarenteeDays += days(p);

			
			return onGuarantee(guarenteeResults);
		} catch ( ClassCastException e){
			e.printStackTrace();
			//
		} catch (SalaryException e) {
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		return 0.00;
	}

	protected void throwGuarenteeException(IContractSalaryCalculatorContext ctx) throws GuarenteeException {
		((SQLNoItContractSalaryCalculatorContext)ctx).throwGuarenteeException();
	}
	

	protected double fixGuarantee( ITimedResult<Double> result) {
		
		if ( getSalaryType() == SalaryType.EXTRA)
			return result.getValue();

		Period period = result.getPeriod();
		double quoteGuarenteed = result.getValue();
		double paidGuarenteed = result.getValue();

		double br = getDoubleVariable(ContextVariable.REGULATORY_BASE.getName(),period.getStart(), period.getEnd() );
		double quoteDays = getVariables(ContextVariable.QUOTE_DAYS.getName(), period.getStart(), period.getEnd(), Collectors.summingDouble( v -> (Double) v.getValue(v.getPeriod()) ));
		
		
		if ( quoteGuarenteed <=  br ) {
			quoteGuarenteed *= quoteDays;
			paidGuarenteed *= period.daysStream().count();
		}
		
		double prestIt = 
				getVariables(ContextVariable.PREST_IT,period.getStart(), period.getEnd(), Collectors.summingDouble( v -> (Double) v.getValue(v.getPeriod()) ));
		
		double max = br * period.daysStream().count();
		
		if ( (quoteGuarenteed < prestIt ) 
			&& (quoteGuarenteed + prestIt) <= max) {
			quoteGuarenteed += prestIt;
			paidGuarenteed += prestIt;
			return paidGuarenteed < max ? paidGuarenteed : quoteGuarenteed;
		} else {
			return quoteGuarenteed;
		}
		
		
	}

	protected Object onAllGuarantee(Double guarenteed, Double totalPayment) throws UndefinedContextVariablesException {
		if ( totalPayment == null )
			throw new UndefinedContextVariablesException(ContextVariable.TOTAL_PAYMENT); 
		return Math.max(0.00, guarenteed - totalPayment);
	}

	protected Object onConstantGuarantee(Double constant, Double totalPayment) throws UndefinedContextVariablesException {
		if ( totalPayment == null )
			throw new UndefinedContextVariablesException(ContextVariable.TOTAL_PAYMENT); 
		return Math.max(0.00, constant - totalPayment);
	}

	protected Object onGuarantee(List<ITimedResult<Double>> guarenteeResults) {
		double guarantee = 0.00;
		for (ITimedResult<Double> guarenteeResult : guarenteeResults) {
			
			Period period = guarenteeResult.getPeriod();
			Date start = period.getStart();
			Date end = period.getEnd();
			double value = guarenteeResult.getValue();
			
			value = fixGuarantee(guarenteeResult);
			
			double max = getDayDoubleVariable( ContextVariable.CGC_BASE.getName() , start, end );			
			if ( max > 0.00 ) {
				value = Math.min(max, value);
			}
			
			double prestIt = getDayDoubleVariable(PREST_IT, start,end);

			guarantee += value - prestIt;
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
		
		UnivariateFunction univariateFunction = new UnivariateFunction() {

			@Override
			public double value(double solve) {

				try {
					IContractSalaryCalculatorContext ctx = getLiquidCalculatorContext(connection, start, end, issueDate,
							contractCriteria, solve, liquid);
					//ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
					SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>() {
						protected TaxCalculator getTaxCalculator(IContractSalaryCalculatorContext ctx) {
							return TaxCalculator.getTaxCalculator(ctx);
						};
					};
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

					ISalary salary = calculator.calculate(((ISQLContractSalaryCalculatorContext) ctx));
					
					double zero = liquid - salary.getTotalLiquid();
					
					
					if ( Math.abs(zero) <= solver.getAbsoluteAccuracy())
						SQLContractSalaryCalculatorContext.this.onLiquid(salary);
					
					return zero;

				} catch (SalaryException e) {
					throw new RuntimeException(e);
				}
			}

		};
		
		try {
			result = getLiquidStartValue(start, liquid);
			if ( Math.abs(univariateFunction.value(result)) <=  solver.getAbsoluteAccuracy() ) {
				System.out.println("Wonderfull NETO calculated in one step!!!. " );
				return result;
			}
		} catch ( Exception e ) {
			
		}
		
		result = solver.solve(Byte.MAX_VALUE, univariateFunction, -2.00 * liquid, 2.00 * liquid, liquid);

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
					ISQLContractSalaryCalculatorContext ctx = getPaymentCalculatorContext(connection, start, end, end,
							contractCriteria, x);
					SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>() {
						// skip extras from BRUTO
						protected TaxCalculator getTaxCalculator(IContractSalaryCalculatorContext ctx) {
							return TaxCalculator.getTaxCalculator(ctx);
						};
					};
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

	protected IContractSalaryCalculatorContext getLiquidCalculatorContext(Connection conn, Date startDate, Date endDate,
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

							@Override
							protected void loadDaysContextVariables(ContractExpressionContext ctx) throws ExpressionException {
								
								getEreDaysVars().forEach( v -> ctx.removeVariable(v) );
								getEreFactorsVars().forEach( v -> ctx.removeVariable(v) );

								super.loadDaysContextVariables(ctx);
							}
							
						};
						ctx.leaveLoader = new NoopSQLContractLeaveLoader(startDate, endDate);
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
								Date birthDate = SQLContractSalaryCalculatorContext.this.getDate(SQLConstants.PERSON, SQLConstants.PersonColumns.BIRTH_DATE);
								return birthDate != null ? AonDateUtils.get(birthDate, Calendar.YEAR) : 0;
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

	protected ISQLContractSalaryCalculatorContext getPaymentCalculatorContext(Connection conn, Date startDate, Date endDate,
			Date issueDate, Criteria criteria, final double x) {
		return getPaymentCalculatorContext(conn, startDate, endDate, issueDate, criteria, x, (ctx) -> {});
	}
	
	protected ISQLContractSalaryCalculatorContext getPaymentCalculatorContext(Connection conn, Date startDate, Date endDate,
			Date issueDate, Criteria criteria, final double x, NextHook hook) {
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
				public Collection<IContractPayment> getContractPayments() throws AonException {
					return new FilterCollection<>(p -> p.getMonth() == null , super.getContractPayments());
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
			ctx.next(hook);
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

	public Object agreement(String name) throws ExpressionException, SQLException {
		ExpressionContext agreementCtx = getAgreementContext();
		return agreementCtx.getVariable(name, startDate, getEnd(), Object.class);
	}

	public Object system(String name) throws ExpressionException, SQLException {
		try {
			ExpressionContext systemExpressionCtx = agreementContextFactory.getSystemExpressionContext();
			return systemExpressionCtx.eval(name, startDate, getEnd(), Object.class).stream().map( ITimedResult::getValue ).findAny().orElseThrow(() -> new UndefinedVariablesException(name));
		} catch ( Exception e ) {
			return implicitExpressionContext.eval(name, startDate, getEnd(), Object.class).stream().map( ITimedResult::getValue ).findAny().orElseThrow(() -> new UndefinedVariablesException(name));
		}
	}

	@Override
	public double getIrpf() {
		Calendar endCalendar = Calendar.getInstance();
//		endCalendar.setTime(startDate);
		endCalendar.setTime(issueDate);
		endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date endMonth = endCalendar.getTime();
		endCalendar.set(Calendar.DAY_OF_YEAR, endCalendar.getActualMaximum(Calendar.DAY_OF_YEAR));
		Date endYear = endCalendar.getTime();

		Criteria contractCriteria = getContractCriteria();

		IIrpfCalculatorContext irpfCalculatorContext = getIrpfCalculatorContext(connection, startDate, endMonth,
				contractCriteria);

		
		IrpfOutcome irpfOutcome = IrpfCalculator.calculateIrpf(irpfCalculatorContext, endMonth);

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
			
			@Override
			protected void loadDaysContextVariables(ContractExpressionContext ctx) throws ExpressionException {
				
				getEreDaysVars().forEach( v -> ctx.removeVariable(v) );
				getEreFactorsVars().forEach( v -> ctx.removeVariable(v) );

				super.loadDaysContextVariables(ctx);
			}
			
		};
		ctx.leaveLoader = new NoopSQLContractLeaveLoader(this.startDate, this.getEnd()); 
		ctx.liquids.putAll(this.liquids);
		ctx.payments.putAll(this.payments);
		//new SQLContractLeaveLoader(this.startDate, this.getEnd()); //leaveLoader;
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
					Date birthDate = SQLContractSalaryCalculatorContext.this.getDate(SQLConstants.PERSON, SQLConstants.PersonColumns.BIRTH_DATE);
					return birthDate != null ? AonDateUtils.get(birthDate, Calendar.YEAR) : 0;
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

		return getQuoteBase() * 12 / 365;

	}

	protected double getQuoteBase() throws ExpressionException, SQLException, SalaryException {

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

		return (totalPayment + extraPayProration) * monthDays / quoteDys ;

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


	private boolean isWorkingDay(DayType type) {
		return type == 
				DayType.WORKING_DAY 
				|| type == DayType.CONTINUOUS_TIME 
				|| type == DayType.OTHER; 
	}


	private boolean isHoliday(Calendar day) {
		Date date = day.getTime();
		ITimedVariable<?> holidays = this.contractExpressionContext.getVariable(HOLIDAYS, date, date);
		if (holidays == null)
			return false;

		try {
			Period period = holidays.getPeriod();
			Object value = holidays.getValue(period);
			int days = (int) Double.parseDouble(value.toString());

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
	    ITimedVariable<?> nonWorking = this.contractExpressionContext.getVariable(NON_WORKING, date, date);
	    if (nonWorking != null ) {
		Period period = nonWorking.getPeriod();
		Object value = nonWorking.getValue(period);
		int days = (int) Double.parseDouble(value.toString());
		if ( days > 0 ) {
		    return true;
		}
	    }
		
	    ContextVariable weekHoursVar = WEEK_HOURS_VARIABLES.get(day.get(DAY_OF_WEEK));
	    ITimedVariable<?> hours = this.contractExpressionContext.getVariable(weekHoursVar, date, date);
	    if (hours == null) {
		return false;
	    }

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

	private boolean isWorkingDay(Calendar day) {
		Date date = day.getTime();
		
		ContextVariable weekHoursVar = WEEK_HOURS_VARIABLES.get(day.get(DAY_OF_WEEK));
		ITimedVariable<?> hours = this.contractExpressionContext.getVariable(weekHoursVar, date, date);
		if (hours == null)
			return false;

		try {
			Period period = hours.getPeriod();
			Object value = hours.getValue(period);
			if ( value == null )
				return false;
			
			return Double.parseDouble(value.toString()) >= 0.00;

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
		
		boolean  hasDaysHours = hasDefinedDaysHours();
		
		ICalendar calendar = getCalendar();
		Calendar end = Calendar.getInstance();
		end.setTime(endDate);
		Calendar day = Calendar.getInstance();
		day.setTime(startDate);
		while (end.after(day) || end.equals(day)) {
			DayType type = calendar.getDayType(day);
			
			Double dayHours = getDayHours(day);

			if ( DayType.HOLIDAY != type && dayHours != null && dayHours > 0.00 ) {
				days++;
			} else if (!hasDaysHours && isActualDay(type, day) && !leaveLoader.isLeaveDay(day) && !isHoliday(day) && !isNotWorkingDay(day)) {
				days++;
			}
			day.add(Calendar.DATE, 1);
		}
		return days;
	}
	
	private boolean hasDefinedDaysHours() {
		for ( ContextVariable variable : WEEK_HOURS_VARIABLES.values()) {
			boolean effectiveDefined = 
			getValues(variable, Object.class).stream()
			.filter(Objects::nonNull)
			.filter(Number.class::isInstance)
			.map(v -> ((Number)v).doubleValue())
			.filter( v -> v > 0.00)
			.count() > 0 ;
			
			if ( effectiveDefined )
				return true;
			
		}
		return false;
	}
	
	private Double getDayHours( Calendar day ) {
		try {
			ContextVariable dayHoursVar = getDayHoursVar(day);
			return  
			this.contractExpressionContext.eval(dayHoursVar.getName(), day.getTime(), day.getTime(), Number.class)
			.stream()
			.map(ITimedResult::getValue)
			.filter(Objects::nonNull)
			.collect(Collectors.summingDouble(Number::doubleValue));
		} catch (ExpressionException e) {
			return null;
		}
	}

	protected Long getLeaveDays(Period p) {
		return leaveLoader.getLeavesDays(p);
	}

	private double getWorkingDays(Date startDate, Date endDate) {
		long days = 0;

		ICalendar calendar = getCalendar();
		Calendar end = Calendar.getInstance();
		end.setTime(endDate);
		Calendar day = Calendar.getInstance();
		day.setTime(startDate);
		while (end.after(day) || end.equals(day)) {
			DayType type = calendar.getDayType(day);
			if (isWorkingDay(type) || isWorkingDay(day)) {
				days++;
			}
			day.add(Calendar.DATE, 1);
		}
		return days;
	}

	protected double getWorkDays(ExpressionContext ctx, Period p) {
		
		Date workStart = Period.max(p.getStart(), contractStartDate);
		Date workEnd = Period.min(p.getEnd(), contractEndDate);
		
		double workEndDay = AonDateUtils.get(workEnd, DAY_OF_MONTH);
		double monthDays = AonDateUtils.getMax(p.getStart(), DAY_OF_MONTH);
		
		double availableDays = getAvailableDays(workStart,workEnd);
		
		if ( workEndDay == monthDays ) {
			double ctxMonthDays = getContexVariable(ctx, p, MONTH_DAYS);
			double prevAdjustDays = getActiveDays(p) ;
			availableDays = (availableDays + prevAdjustDays)  == monthDays ? (ctxMonthDays - prevAdjustDays) : availableDays;			
		}

		double workedDays = availableDays /*- leaveDays*/;
		
		for ( ContextVariable ereFactor: ContextVariable.ERE_FACTORS ) {
			try {
				getCurrentBindings().get(ereFactor);
				workedDays *= 1.00 - getContexVariable(ctx, p, ereFactor);
			}
			catch ( Exception e ) {
				
			}
		}

		workedDays *= 1.00 - getCurrentBindings().get(STRIKE_FACTOR, obj -> ((Number) obj).doubleValue(), 0.00);
		getCurrentBindings().get(STRIKE_DAYS);
		
//		if ( workEndDay < monthDays )
//			return workedDays; // Not the last period or doesn't work the full month. 
//
//		double ctxMonthDays = getContexVariable(ctx, p, MONTH_DAYS);
//
//		double prevAdjustDays = getActiveDays(p) ;
//		
//		return (workedDays + prevAdjustDays)  == monthDays ? (ctxMonthDays - prevAdjustDays) : workedDays;
		
		return workedDays;
	}
	

	private double getDays(ExpressionContext ctx, Period p, double factor) {
		Long availableDays = getAvailableDays(p.getStart(), p.getEnd());
		double monthDays = getMax(p.getStart(), DAY_OF_MONTH);
		double ctxMonthDays = getContexVariable(ctx, p, MONTH_DAYS);
		return (availableDays == monthDays ? ctxMonthDays : availableDays) * factor;
	}

	private double getEreDays(ExpressionContext ctx, Period p, double factor, List<Pair<ContextVariable,ITimedVariable<Object>>> others ) {
		double availableDays = getAvailableDays(p.getStart(), p.getEnd());
		double naturalMonthDays = getMax(p.getStart(), DAY_OF_MONTH);
		double ctxMonthDays = getContexVariable(ctx, p, MONTH_DAYS);
		
		//availableDays *= factor;
		
		if ( availableDays == naturalMonthDays )
			return ctxMonthDays * factor;
		
		if ( ctxMonthDays == naturalMonthDays )
			return availableDays * factor;

		if ( isPartial(ctx, p) && isInIT(ctx, p) )
			return availableDays * factor;
					
		Date ctxEndDate = getEndDate();
		if ( getLastDayOfMonth(ctxEndDate).after(ctxEndDate))
			return availableDays * factor;
			
		
		Period adjust = p;
		try {
			adjust = getPeriod4Adjust(others);
		} catch ( NoSuchElementException e) {
			
		}
		
//		if ( p.compareTo(adjust) != 0 )
		if ( !AonUtils.equals(p.getEnd(), adjust.getEnd() ) )
			return availableDays * factor;
				
		return (availableDays + (30 - naturalMonthDays)) * factor;
		//return (availableDays) + (30 - naturalMonthDays);
	}

	private boolean isPartial(ExpressionContext ctx, Period p) {
		try {
			return ctx.getVariables(ContextVariable.PARTIAL_FACTOR, p.getStart(), p.getEnd())
			.stream().map( v -> v.getValue(v.getPeriod()))
			.filter( v -> v != null && v instanceof Number )
			.anyMatch( v -> ((Number)v).doubleValue() < 1.00);
		} catch ( Exception e) {
			return false;
		}
	}
	
	private boolean isInIT(ExpressionContext ctx, Period p) {
		return getLeavesPeriods().size() > 0 ;
	}

	private Period getPeriod4Adjust(List<Pair<ContextVariable,ITimedVariable<Object>>> others) {
		return 
		others.stream().map(pair -> pair.snd)
		.filter(v -> ((Number)v.getValue(v.getPeriod())).doubleValue() == 1.00)
		.reduce((v1,v2) -> v2)
		.map(v -> v.getPeriod())
		.orElseGet(() ->  
		others.stream().map(pair -> pair.snd)
		.filter(v -> ((Number)v.getValue(v.getPeriod())).doubleValue() < 1.00)
		.reduce((v1,v2) -> v2)
		.map(v -> v.getPeriod())
		.orElseThrow()
		);
	}
	
	

	private double getQuoteDays(ExpressionContext ctx, Period p, double factor) {
		Long availableDays = getAvailableDays(p.getStart(), p.getEnd());
		double monthDays = getMax(p.getStart(), DAY_OF_MONTH);
		double ctxMonthDays = getFirstContexVariable(ctx, p, MONTH_DAYS);
		double prevAdjustDays = getActiveDays(p) ;
		
		return (( availableDays + prevAdjustDays ) == monthDays ? (ctxMonthDays - prevAdjustDays) : availableDays);
	}

	private int getSeniorityYears(Period period) {
		Date start = getSeniorityDate();
		try {
			start = getCurrentBindings().get(SENIORITY_START, v -> (Date) v, start );
			
		} catch (Throwable t ) {
			t.printStackTrace();
			
		}
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
	
	public Double sum(String varName) {
		
		// Get Period
		Date firsDayOfMonth = AonDateUtils.getFirstDayOfMonth(getStartDate());
		Date lastDayOfMonth = AonDateUtils.getLastDayOfMonth(getStartDate());
		
		Stream<com.esferalia.aon.occam.api.model.Salary> salariesData = AON.getSalaryData(
				new AONContext(connection), 
				p -> p.getIsSalaryProperty().eq(true)
					.and(p.getSSProperty().eq(getSocialSecurityNumber()))
					.and(p.getStartDateProperty().ge(firsDayOfMonth))
					.and(p.getEndDateProperty().le(lastDayOfMonth))
				);
		
		List<ContextData> varNameCtxData = new ArrayList<ContextData>();
		salariesData.forEach(s -> varNameCtxData.addAll(s.getContextData(varName, firsDayOfMonth, lastDayOfMonth)));

		Stream<ContextData> filteredVarNameCtxData = varNameCtxData.stream().filter(ctxData -> !ctxData.getStartDate().equals(getStartDate()) && !ctxData.getEndDate().equals(getEndDate()));
		
		Double sum = filteredVarNameCtxData.collect(Collectors.summingDouble(ctxData -> {
						try { 
							return Double.parseDouble(ctxData.getExpression()); 
						} catch (Exception e){
							return 0.00;
						}
					}));
		
		return sum;
	}

	public Object br(Date date) throws ExpressionException, SQLException, SalaryException {
		
		double br = getSavedBr(date);

		if (br > 0.00)
			return br;
		
		br = getL00Br(date);
		if (br > 0.00)
			return br;

		// No salaries are present.
		return calculateBr(date.before(contractStartDate) ? contractStartDate: date);
	}

	public double getSavedBr(Date date) {
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
		return br;
	}

	public double getL00Br(Date date) {
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
			Stream<com.esferalia.aon.occam.api.model.Salary> salaries = 
			AON.getSalaries(new AONContext(connection),
			p -> p.getIsL00Property().eq(true)
			.and(p.getContractProperty().eq(contractId))
			.and(p.getStartDateProperty().le(prevEndMonth))
			.and(p.getEndDateProperty().ge(prevStartMonth)));
			
			Pair<Double, Double> pair = new Pair<Double, Double>(0.00, 0.00);
			salaries.forEach(s-> {
				pair.fst += s.getCommonContingenciesBase();
				pair.snd += s.getContextData(QUOTE_DAYS.getName(), summingDouble(Double::parseDouble));
			} )
			;
			br = pair.fst / pair.snd;
			
			salaries.close();
		} catch (Throwable t) {

		}
		return br;
	}

	public Object calculateBr(Date date) throws ExpressionException, SQLException, SalaryException {
		int contractId = getId();
		ISQLContractSalaryCalculatorContext ctx = (ISQLContractSalaryCalculatorContext) getNoItSalary(connection, date,
				SalaryType.SALARY, contractId);
		try {
			Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()) {
				@Override
				protected void resolvePayment(IContractPayment contractPayment,  Date start, Date end, Date issueDate,
						ExpressionContext expressionContext,
						com.esferalia.aon.payroll.calculator.TaxCalculator taxCalculator,
						com.esferalia.aon.payroll.calculator.QuoteCalculator quoteCalculator,
						List<Period> leavePeriods,
						List<Period> strikePeriods) throws AonException {
					try {
						super.resolvePayment(contractPayment, start, end, issueDate, expressionContext, taxCalculator,
								quoteCalculator, leavePeriods, strikePeriods);
					} catch (SalaryExpressionException e) {
						// e.printStackTrace();
					}
				};
				@Override
				protected java.util.List<com.esferalia.aon.salary.expression.ITimedResult<Double>> fixGuaranteedResults(
						IContractPayment contractPayment,
						java.util.List<com.esferalia.aon.salary.expression.ITimedResult<Double>> results,
						java.util.List<Period> its, Date start, Date end, ExpressionContext expressionContext)
						throws UnsupportedOperationException {
					return Collections.emptyList();
				};
				
				protected TaxCalculator getTaxCalculator(IContractSalaryCalculatorContext ctx) {
					return TaxCalculator.getTaxCalculator(ctx);
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

	protected boolean isFullTime() {
		String tc2 = getCurrentBindings().get(TC2, obj -> obj.toString(), "100");
		if (tc2 == null) {
			throw new ExpressionExceptionWrapper(new UndefinedVariablesException(TC2.getName()));
		}
		return ("14".indexOf(tc2.charAt(0)) != -1);
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
				day -> !contractExpressionContext.containsVariable(getDayHoursVar(day), day.getTime(), day.getTime()))
				.forEach(day -> onUndefinedData(
						new ExpressionImpl().setName(getDayHoursVar(day).getName()).setScope(ExpressionScope.SYSTEM), null,
						day.getTime(), day.getTime(), getDayHoursVar(day).getName()));

		double workedHours =  p.daysStream()
				.map(day -> contractExpressionContext.getVariable(getDayHoursVar(day), day.getTime(), day.getTime(), Number.class))
				.filter(hours -> hours != null && hours.doubleValue() > 0.00 )
				.collect(Collectors.summingDouble(hours -> hours.doubleValue()))
				;
			
		if ( workedHours != 0 )
			return workedHours;
		
		Number workedDays = contractExpressionContext.getVariable(WORKED_DAYS, p.getStart(), p.getEnd(), Number.class);
		if ( workedDays == null  || workedDays.doubleValue() == 0.00  ) 
			return 0.00;
		
		Number agreementHours = contractExpressionContext.getVariable(AGREEMENT_HOURS, p.getStart(), p.getEnd(), Number.class);
		if ( agreementHours == null  || agreementHours.doubleValue() == 0.00  ) 
			return 0.00;
		
		return agreementHours.doubleValue()  * workedDays.doubleValue() / 5.00 ; 
				
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

	private boolean isFullTime(Period p) {
		String tc2 = getCurrentBindings().get(TC2, obj -> obj.toString());
		if (tc2 == null) {
			throw new ExpressionExceptionWrapper(new UndefinedVariablesException(TC2.getName()));
		}
		return ("14".indexOf(tc2.charAt(0)) != -1);
	}
	

	private boolean isShortContract() {
		Date endDate = getContractEndDate();
		if (endDate == null) {
			return false;
		}
		Date startDate = getContractStartate();
		long naturalDays = CommonUtil.getDaysBetweenDates(startDate, endDate) + 1;
		if (naturalDays < 6) {
			return true;
		}
		return false;
	}

	protected Date getContractStartate() {
		return getDate(SQLConstants.CONTRACT, ContractColumns.START_DATE);
	}

	protected Date getContractEndDate() {
		return getDate(SQLConstants.CONTRACT, ContractColumns.END_DATE);
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


	private <R,A> R getVariables(String name, Date start, Date end, Collector<? super ITimedVariable<?>, A, R> collector) {
		return 
		this.contractExpressionContext
		.getVariables(name, start, end)
		.stream().collect(collector)
		;
		
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

	private double getAverageVariable(String name, Date start, Date end) {
		List<ITimedVariable<Number>> vars = this.contractExpressionContext.getVariables(name);
		
		int days = 0;
		double sum = 0.00;
		
		Period p = new Period(start, end);
		for (ITimedVariable<Number> var : vars) {
			Period period = var.getPeriod();
			Period intersect = period.intersect(p);
			if (intersect == null)
				continue;

			Number value = var.getValue(period);
			if (value == null)
				continue;
			days += days(intersect);
			sum += value.doubleValue() * days(intersect);
		}

		return sum / days;
	}

	private boolean containsVariable(Object name) {
		return this.contractExpressionContext.containsVariable(name, this.contractStartDate, this.contractEndDate);
	}

	private boolean isConstantVariable(Object name) {
		Object last = null;
		Date endDate = null;
		Date startDate = null;
		List<ITimedVariable<Object>> list =this.contractExpressionContext.getVariables(name, this.contractStartDate, this.contractEndDate);
		for ( ITimedVariable<Object> var : list  ) {
			Period p = var.getPeriod();
			Object v = var.getValue(p);
			if ( last == null  )
				last = v;
			else if ( !last.equals(v) )
				return false;
			endDate = endDate == null ? p.getEnd() : Period.max(endDate, p.getEnd());
			startDate = startDate == null ? p.getStart() : Period.min(startDate, p.getStart());
		}
		
		return ( Period.compare(startDate, contractStartDate) == 0 )
				&& ( Period.compare(endDate, contractEndDate) == 0 );
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

		this.contractStartDate = Period.max(getContractStartate(), startDate);
		this.contractEndDate = Period.min(getContractEndDate(), getEnd());

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
				return new Period(startDate, getEnd());
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

		this.implicitExpressionContext = new ExpressionContext(agreementCtx, this);

		// TODO: Tiene que ir aqui ???
		SalaryType salaryType = getSalaryType();
		this.implicitExpressionContext.setVariable(SALARY, salaryType == SalaryType.SALARY, startDate, getEnd());
		this.implicitExpressionContext.setVariable(SETTLE, salaryType == SalaryType.SETTLE, startDate, getEnd());
		this.implicitExpressionContext.setVariable(DELAY, salaryType == SalaryType.DELAY, startDate, getEnd());
		this.implicitExpressionContext.setVariable(EXTRA_PAY, salaryType == SalaryType.EXTRA, startDate, getEnd());
		
		this.implicitExpressionContext.setVariable(TODAY,
				DateUtils.truncate(new Date(), DAY_OF_MONTH), startDate, getEnd());
		this.implicitExpressionContext.setVariable(CONTRACT_START,
				getContractStartate(), startDate, getEnd());
		
		this.implicitExpressionContext.setVariable(CONTRACT_END, salaryType == SalaryType.SETTLE ? contractEndDate
				: getContractEndDate(), startDate, getEnd());

		if (!this.implicitExpressionContext.containsVariable(IRPF_PERCENT, startDate, getEnd()))
			this.implicitExpressionContext.putVariable(IRPF_PERCENT, irpf);

		this.implicitExpressionContext.putVariable(START, start);
		this.implicitExpressionContext.putVariable(END, end);

		this.implicitExpressionContext.setVariable(EFECTIVE_START,
				contractStartDate, startDate, getEnd());
		this.implicitExpressionContext.setVariable(EFECTIVE_END,
				contractEndDate, startDate, getEnd());

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

		this.implicitExpressionContext.putVariable(ContextVariable.REGIME, new LazyTimedConstant<CCCType>() {
			@Override
			public CCCType create() {
				return getCCCType();
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

//		this.implicitExpressionContext.putVariable(FULL_ERE,
//				new ActiveTimedExpressionVariable<Boolean>(FULL_ERE.name(), ExpressionScope.CONTRACT) {
//					@Override
//					public Period getPeriod() {
//						return new Period(startDate, endDate);
//					}
//
//					@Override
//					public Boolean getValue(Period period) {
//						return isFullERE(period);
//					}
//				});

//		this.implicitExpressionContext.putVariable(ERE_BACK,
//				new ActiveTimedExpressionVariable<Boolean>(ERE_BACK.name(), ExpressionScope.CONTRACT) {
//					@Override
//					public Period getPeriod() {
//						return new Period(startDate, endDate);
//					}
//
//					@Override
//					public Boolean getValue(Period period) {
//						return isEREBack(period);
//					}
//				});

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
		if (getSalaryType() == SalaryType.SETTLE) {
			this.implicitExpressionContext.putVariable("SALARIO_DIA",
					new LazyTimedExpressionConstant<Double>("SALARIO_DIA", ExpressionScope.CONTRACT) {
						@Override
						public Double create() {
							try {
								return getDaySalary();
							} catch (Exception e) {
								//throw new ExpressionExceptionWrapper(new InvalidVariables(e.getMessage(), getName()));
								throw new ExpressionExceptionWrapper(new UndefinedVariablesException(getName()));
							}
						}
					});
			this.implicitExpressionContext.putVariable("SALARIO_MES",
					new LazyTimedExpressionConstant<Double>("SALARIO_MES", ExpressionScope.CONTRACT) {
						@Override
						public Double create() {
							try {
								return getQuoteBase();
							} catch (Exception e) {
								throw new ExpressionExceptionWrapper(new InvalidVariables(e.getMessage(), getName()));
							}
						}
					});
		}

		this.implicitExpressionContext.putVariable(ContextVariable.MONTHLY_SALARY, new LazyTimedConstant<Boolean>() {
			@Override
			public Boolean create() {
				return isMonthly();
			}
		});
		/*
		 * this.implicitExpressionContext.addVariable(COMPENSATION_DAYS, new
		 * LazyTimedVariable<Double>() {
		 * 
		 * @Override public Double create() { return getCompensationDays(); }
		 * });
		 */

		this.implicitExpressionContext.setVariable(SENIORITY_START,
				getDate(SQLConstants.CONTRACT, ContractColumns.SENIORITY_DATE), startDate, getEnd());
		
		agreementCtx.getVariables(SENIORITY_START.getName(), startDate, getEnd()).forEach(v -> {
			try {
				implicitExpressionContext.addExpression(((IExpressionVariable) v).getExpression(), startDate, getEnd());
			} catch (Exception e) {	
			}
		});
		
		this.contractExpressionContext = newContractExpressionContext(this.implicitExpressionContext, this);

		this.contractExpressionContext.setVariable(CONTEXT, contractExpressionContext, startDate, getEnd());
		this.contractExpressionContext.setVariable(SELF, this, startDate, getEnd());

		// TODO: at implicitExpressionContext ?
		loadExpression(this.contractExpressionContext, BR, "def(x){ SELF.br(x)};", this.startDate, this.getEnd());
		
		loadExpression(this.contractExpressionContext, SUM, "def(x){ SELF.sum(x)};", this.startDate, this.getEnd());
		
		loadExpression(this.contractExpressionContext, GROSS,
				String.format(
						"def(x){ x=%1$s.checkParametersNotConstant(x, '%4$s', '%2$s', '%3$s'); return %1$s.gross(x, %2$s, %3$s ); };",
						SELF, START, END, GROSS),
				this.startDate, this.getEnd());
//		loadExpression(this.contractExpressionContext, LIQUID,
//				String.format(
//						"def(x){ x=%1$s.checkParametersNotConstant(x, '%4$s', '%2$s', '%3$s'); return %1$s.liquid(x, %2$s, %3$s ); };",
//						SELF, START, END, LIQUID),
//				this.startDate, this.getEnd());
		MethodStub __netoStub = new MethodStub(SQLContractSalaryCalculatorContext.class, "__neto");
		this.contractExpressionContext.setVariable("__NETO", __netoStub, this.startDate, this.getEnd());
		MethodStub netoStub = new MethodStub(SQLContractSalaryCalculatorContext.class, "neto");
		this.contractExpressionContext.setVariable(LIQUID, netoStub, this.startDate, this.getEnd());
		
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
		
		autoFracionate(contractExpressionContext, contractStartDate, contractEndDate, ContextVariable.ADDITIONAL_HOURS);
	}
	
	private static void autoFracionate(ExpressionContext context, Date contractStartDate, Date contractEndDate, ContextVariable ...contextVars) {
		Period contractPeriod = new Period(contractStartDate, contractEndDate );
		List<Period> workedPeriods = context.getPeriods(ContextVariable.WORKED_DAYS);
		
		for (ContextVariable contextVar : contextVars) {
			
			List<ITimedVariable<Object>> varVariables = context.getVariables(contextVar.getName());
			if ( varVariables.isEmpty() )
				continue;
			
			context.removeVariable(contextVar.getName());
			
			for (Period workedPeriod : workedPeriods) {
				
				context.putVariable(contextVar.getName(), new ITimedVariable<Double>() {
					@Override
					public Period getPeriod() {
						return workedPeriod;
					}
					
					@Override
					public Double getValue(Period period) {
					    	
						double amount =  
						varVariables.stream()
						.filter( v -> period.contains(v.getPeriod()))
						.filter( v -> v.getValue(period) instanceof Number )
						.map( v -> (( Number) v.getValue(v.getPeriod())).doubleValue() )						
						.collect(Collectors.summingDouble( AonNumberUtils::toDouble ));
						
						if ( amount > 0.00 )
						    return amount;
						
						amount = 
						varVariables.stream()
						.filter( v -> period.intersects(v.getPeriod()))
						.map( v -> v.getValue(v.getPeriod()))
						.filter( Number.class::isInstance )
						.collect(Collectors.summingDouble( n -> AonNumberUtils.toDouble((Number)n)));

						return ContextFunctions.fractionate(context, amount);
					}
				});
			}
		}
		
	}
	
	
	
	public static Double neto(Double liquido) throws MacroException {
		
		throw new MacroException() {
			@Override
			public String doMacro(String expr) {
				return expr.replaceAll(String.format("%s\\s*\\(", ContextVariable.LIQUID),
						String.format("%s\\(%s,", "__NETO", ContextVariable.SELF));
			}
		};
	}

	public static Object __neto(SQLContractSalaryCalculatorContext ctx, Double liquido) 
	throws ExpressionException, SalaryException, SQLException {
		liquido = ctx.checkParametersNotConstant(liquido, LIQUID, START.getName(), END.getName(),"__NETO");
		return ctx.liquid(liquido, 
		getCurrentBindings().get(START, v -> (Date)v ),  
		getCurrentBindings().get(END, v -> (Date)v ));
	}

	public static Object __neto(SQLContractSalaryCalculatorContext ctx, Double bruto, Double liquido) 
	throws ExpressionException, SalaryException, SQLException {
		liquido = ctx.checkParametersNotConstant(liquido, LIQUID, START.getName(), END.getName(),"__NETO");
		Date start = getCurrentBindings().get(START, v -> (Date)v );
		Date end = getCurrentBindings().get(END, v -> (Date)v );
		
		ctx.addBonus("", 
		String.format(Locale.ROOT,
		"CHECK(%3$s(%2$s-%1$.2f) < 0.01, \""
		+ "<div>El TOTAL L&Iacute;QUIDO no coincide con el introducido inicialmente %1$.2f.</div>"
		+"<div>&nbsp;</div><div class='aon-text-right'><span class='aon-icon aon-icon-logo' />aon Solutions</div>"
		+ "\")", 
		liquido, 
		TOTAL_LIQUID,
		ABS
		));
		
		return ctx.gross(bruto, start, end);
	}

	public static Double neto(Double liquido, Double bruto) throws MacroException {
		throw new MacroException() {
			@Override
			public String doMacro(String expr) {
				return expr.replaceAll(String.format("%s\\s*\\(", ContextVariable.LIQUID),
						String.format("%s\\(%s,", "__NETO", ContextVariable.SELF));
				
			}
		};
	}

	protected ContractExpressionContext newContractExpressionContext(ExpressionContext expressionContext,
			NotFoundHandler notFoundHandler) {
		return new ContractExpressionContext(expressionContext, notFoundHandler);
	}

	protected void loadDaysContextVariables(ContractExpressionContext ctx) throws ExpressionException {
		
		fixItDaysWhenIfDays(ctx);
		
		loadWeekHoursContextVariable(ctx);

		loadTotalsContextVars(ctx);

		List<Period> contract = getMonths(contractStartDate, contractEndDate);
		
		List<Period> weekHours = ctx.getPeriods(WEEK_HOURS);
		List<Period> partialFactor = ctx.getPeriods(PARTIAL_FACTOR);
		
		weekHours = Period.sub(weekHours, partialFactor);

		List<Period> contractHours = new ArrayList<>();
	
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
							boolean definedWeekHours = false;
							for ( ContextVariable hourVar : WEEK_HOURS_VARIABLES.values() ) {
								definedWeekHours |= getCurrentBindings().look(hourVar,
										obj -> ((Number) obj).doubleValue(), 0.00) > 0.00;
							}

							if ( (getVariable(FULL_TIME, p, Boolean.class) != Boolean.TRUE) || definedWeekHours  /*!isFullTime()*/) {
								
								double agreementWeekHours = getCurrentBindings().get(AGREEMENT_HOURS,
										obj -> ((Number) obj).doubleValue(), DEFAULT_AGRREEMENT_HOURS);

								double weekHours = getCurrentBindings().get(WEEK_HOURS,
										obj -> ((Number) obj).doubleValue(), DEFAULT_AGRREEMENT_HOURS);

								
								double wholeFactor = weekHours / agreementWeekHours;

								if (isWholeMonth(p) /* && false */ ) {
									return wholeFactor;
								} else if (isAllMonth()  ) {
									return wholeFactor;
								} else if (isMonthly()  ) {
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
			
			for ( Period salaryp : Period.sub(period, getPeriods(SALARY_DAYS)) ) {
				ITimedVariable<Double> salaryDays = new ITimedVariable<Double>() {
					@Override
					public Period getPeriod() {
						return salaryp;
					}

					@Override
					public Double getValue(Period p) {
						return getQuoteDays(ctx, p, 1.00);
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
		
		for (ITimedVariable<Object> strikeFactor : ctx.getVariables(STRIKE_FACTOR)) {
			Period period = strikeFactor.getPeriod();
			Object value = strikeFactor.getValue(period);
			if (!(value instanceof Number))
				continue;
			if (((Number) value).doubleValue() >= 1.00)
				intersects = Period.sub(intersects, Collections.singletonList(period));
			else
				intersects = SQLNoItContractSalaryCalculatorContext.split(intersects,
						Collections.singletonList(period));
			
		}
		
		for (ContextVariable var : new ContextVariable[] { QUOTE_GROUP, OCCUPATION, TC2, PARTIAL_FACTOR }) {
			List<Period> periods = getPeriods(ctx, var.getName());
			quote = Period.intersect(quote, periods);
			intersects = Period.intersect(intersects, periods);
		}
		
		List<Period> offs = ctx.getPeriods(OFF_DAYS);
		intersects = Period.sub(intersects, offs);

		List<Period> nons = getPeriods(ctx, NON_WORKED_DAYS, v -> v != null && ((Number)v).doubleValue() > 0.00);
		intersects = Period.sub(intersects, nons);
		

		// DropDays
		List<Period> drops = ctx.getPeriods(DROP_FACTOR);
		intersects = Period.sub(intersects, drops);
		
		for(Period p: drops) {
			ITimedVariable<Double> dropDays = new ITimedVariable<Double>() {
				@Override
				public Period getPeriod() {
					return p;
				}

				@Override
				public Double getValue(Period p) {
					Long availableDays = getAvailableDays(p.getStart(), p.getEnd());
					return availableDays.doubleValue();
					
//					if ( !isWholeMonth() )
//						return availableDays.doubleValue();
//					
//					double monthDays = getMax(p.getStart(), DAY_OF_MONTH);
//					double ctxMonthDays = getContexVariable(ctx, p, MONTH_DAYS);
//					return ctxMonthDays == monthDays ? availableDays : (30 - (monthDays - availableDays));
				}

			};
			
			ctx.putVariable(DROP_DAYS, dropDays);
			ctx.putVariable(SALARY_DAYS, dropDays);
			
			
			ITimedVariable<Double> workedFactor = new ITimedVariable<Double>() {
				@Override
				public Period getPeriod() {
					return p;
				}

				@Override
				public Double getValue(Period p) {
					
					double workedFactor = getCurrentBindings().get(PARTIAL_FACTOR,
							obj -> ((Number) obj).doubleValue(), 1.00);
					
					for ( ContextVariable ereFactor: ContextVariable.ERE_FACTORS )
						workedFactor *= (1.00 - getCurrentBindings().get(ereFactor,
							obj -> ((Number) obj).doubleValue(), 0.00));
					
					return workedFactor;
				}

			};
			ctx.putVariable(WORKED_FACTOR, workedFactor);
			
		}

		

		// ITs
		List<Period> leaves = getLeavesPeriods();
		intersects = Period.sub(intersects, leaves);
		
		List<Pair<ContextVariable,ITimedVariable<Object>>> ereFactorsPairs = 
		Arrays.stream(ContextVariable.ERE_FACTORS)
		.flatMap(c -> join(ctx.getVariables(c)).stream().map(v -> new Pair<ContextVariable,ITimedVariable<Object>>(c, v)))
		.sorted( (p1,p2) -> p1.snd.getPeriod().compareTo(p2.snd.getPeriod()) )
		.filter( p -> p.snd.getValue(p.snd.getPeriod()) instanceof Number )
		.collect(Collectors.toList())
		;
		
		// ERE
		for (Pair<ContextVariable,ITimedVariable<Object>> ereFactorPair : ereFactorsPairs) {
			ContextVariable ereFactorVar = ereFactorPair.fst;
			ITimedVariable<Object> ereFactor = ereFactorPair.snd;
					
			//Period period = ereFactor.getPeriod();
			
			for ( Period period : split(Collections.singletonList(ereFactor.getPeriod()), leaves) ) {
			
			Object value = ereFactor.getValue(period);		
			

			double factor = ((Number) value).doubleValue();
			if (((Number) value).doubleValue() >= 1.00)
				intersects = Period.sub(intersects, Collections.singletonList(period));
			else
				intersects = split(intersects,
						Collections.singletonList(period));
			
			ITimedVariable<Double> ereDays = new ITimedVariable<Double>() {
				@Override
				public Period getPeriod() {
					return period;
				}

				@Override
				public Double getValue(Period p) {
					ctx.readVariable(ereFactorVar, p.getStart(), p.getEnd(), Number.class);
					return getEreDays(ctx, p, factor, ereFactorsPairs);
				}

			};
			
			ITimedVariable<Double> quoteDays = new ITimedVariable<Double>() {
				@Override
				public Period getPeriod() {
					return period;
				}

				@Override
				public Double getValue(Period p) {
					return getEreDays(ctx, p, 1.00, ereFactorsPairs);
				}

			};

			String daysVar = ereFactorVar.getName()
			.replaceAll(ERE_FACTOR.getName(), ERE_DAYS.getName());
			
			ctx.putVariable(daysVar, ereDays);
			
			ITimedVariable<?> prevQuoteDays = getExpressionContext().getVariable(QUOTE_DAYS, period.getStart(),period.getEnd());		
			if (prevQuoteDays == null ) {
				ctx.putVariable(QUOTE_DAYS.getName(), quoteDays);
			}

			Date ereStartDate = getStartDate(ereFactorVar, period);

//			ITimedVariable<Double> ereBase = new ITimedVariable<Double>() {
//				@Override
//				public Period getPeriod() {
//					return period;
//				}
//
//				@Override
//				public Double getValue(Period p) {
//					try {
//						return  (Double) br(ereStartDate);
//					} catch (ExpressionException | SalaryException | SQLException e) {
//						throw new ExpressionExceptionWrapper(
//								new UndefinedContextVariablesException(REGULATORY_BASE));
//					}
//				}
//
//			};
//			ITimedVariable<?> userBr = getExpressionContext().getVariable(REGULATORY_BASE, period.getStart(),
//					period.getEnd());
//			
//			
//			if (userBr == null)
//				ctx.putVariable(REGULATORY_BASE, ereBase);
			
			for ( Period p : Period.sub(period, getPeriods(REGULATORY_BASE))) {
				ITimedVariable<Double> ereBase = new ITimedVariable<Double>() {
					@Override
					public Period getPeriod() {
						return p;
					}

					@Override
					public Double getValue(Period p) {
						try {
							return  (Double) br(ereStartDate);
						} catch (ExpressionException | SalaryException | SQLException e) {
							throw new ExpressionExceptionWrapper(
									new UndefinedContextVariablesException(REGULATORY_BASE));
						}
					}

				};
				
				ctx.putVariable(REGULATORY_BASE, ereBase);
			}
			
//			ITimedVariable<Boolean> ereBack = new ITimedVariable<Boolean>() {
//				@Override
//				public Period getPeriod() {
//					return period;
//				}
//
//				@Override
//				public Boolean getValue(Period p) {
//					return isEREBack(period);
//				}
//
//			};
//			
//			ctx.putVariable(ERE_BACK, ereBack );
			getEREBack(period).forEach(v -> ctx.putVariable(ERE_BACK, v ));
			
			getFullERE(period).forEach(v -> ctx.putVariable(FULL_ERE, v ));
			;
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
//						if ( getVariable(FULL_TIME, p, Boolean.class) != Boolean.TRUE /*!isFullTime()*/) {
							return workDays * getCurrentBindings().get(PARTIAL_FACTOR,
									obj -> ((Number) obj).doubleValue(), 1.00);
//						}
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
			}

			ITimedVariable<Double> workDays = new ITimedVariable<Double>() {
				@Override
				public Period getPeriod() {
					return period;
				}

				@Override
				public Double getValue(Period p) {
					return getWorkDays(ctx, p);
				}

			};
			ITimedVariable<?> userWorkDays = getExpressionContext().getVariable(WORK_DAYS, period.getStart(),
					period.getEnd());

			if (userWorkDays == null) {
				ctx.putVariable(WORK_DAYS, workDays);
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
					
					List<Calendar> workedList = p.daysStream()
					.filter(day -> !isHoliday(day))
					.filter(day -> getDayType(day) != DayType.HOLIDAY)
					.collect(Collectors.toList());
					
					if ( workedList.isEmpty() )
						return 0.00;
					
					Double workedHours =  
						workedList.stream()
						.map(day -> ctx.getVariable(DAYS.get(day.get(DAY_OF_WEEK)), day.getTime(),day.getTime(), Number.class))
						.filter(hours -> hours != null && hours.doubleValue() > 0.00 )
						.collect(Collectors.summingDouble(Number::doubleValue));
					
					for ( ContextVariable ereFactorVar : ContextVariable.ERE_FACTORS ) {
						Number ereFactor =  ctx.getVariable(ereFactorVar, p.getStart(), p.getEnd(), Number.class);
						if ( ereFactor == null  || ereFactor.doubleValue() == 0.00  ) 
							continue;
						workedHours *= 1.00 - ereFactor.doubleValue();
					}
					
					if ( workedHours != 0 )
						return workedHours;
					
					Number agreementHours = ctx.getVariable(AGREEMENT_HOURS, p.getStart(), p.getEnd(), Number.class);
					if ( agreementHours == null  || agreementHours.doubleValue() == 0.00  ) 
						return 0.00;

					double actualDays = ctx.getVariables(ACTUAL_DAYS, p.getStart(), p.getEnd()).stream()
					.collect(Collectors.summingDouble(v -> ((Number) v.getValue(v.getPeriod())).doubleValue()));
					if ( actualDays > 0.00  ) 
						return agreementHours.doubleValue() / 5.00 * actualDays ;
					
					
					Number workedDays = ctx.getVariable(WORKED_DAYS, p.getStart(), p.getEnd(), Number.class);
					if ( workedDays == null  || workedDays.doubleValue() == 0.00  ) 
						return 0.00;
					
					
					return agreementHours.doubleValue()  * workedDays.doubleValue() / 5.00 ; 
				}

			};

			ITimedVariable<?> userWorkedHours = getExpressionContext().getVariable(WORKED_HOURS, period.getStart(),
					period.getEnd());

			if (userWorkedHours == null) {
				ctx.putVariable(WORKED_HOURS, workedHours);
			} else {
			}


			ITimedVariable<Double> quoteDays = new ITimedVariable<Double>() {
				@Override
				public Period getPeriod() {
					return period;
				}

				@Override
				public Double getValue(Period p) {
					return getQuoteDays(ctx, p, 1.00);
				}

			};

			ITimedVariable<?> userQuoteDays = getExpressionContext().getVariable(QUOTE_DAYS, period.getStart(),
					period.getEnd());

			if (userQuoteDays == null) {
				ctx.putVariable(QUOTE_DAYS, quoteDays);
			} else {
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
			}
			
			ITimedVariable<Double> workingDays = new ITimedVariable<Double>() {
				@Override
				public Period getPeriod() {
					return period;
				}

				@Override
				public Double getValue(Period p) {
					return getWorkingDays(p.getStart(), p.getEnd());
				}

			};

			ITimedVariable<?> userWorkingDays = getExpressionContext().getVariable(WORKING_DAYS, period.getStart(),
					period.getEnd());
			
			if (userWorkingDays == null) {
				ctx.putVariable(WORKING_DAYS, workingDays);
			} else {
			}

			class WeekDays implements ITimedVariable<Double> {
				private int dayOfWeek;
				
				WeekDays(int weekDay ) {
					this.dayOfWeek = weekDay;
				}
				
				@Override
				public Period getPeriod() {
					return period;
				}

				@Override
				public Double getValue(Period p) {
					return p.daysStream()
							.filter(d -> !isHoliday(d) )
							.filter(d -> d.get(DAY_OF_WEEK) == dayOfWeek )
							.collect(Collectors.summingDouble(d -> 1.00));
				}

			};
			
			for ( Map.Entry<Integer, ContextVariable> var : WEEK_DAYS_VARIABLES.entrySet())
				ctx.putVariable(var.getValue(), new WeekDays(var.getKey()));
			
			ITimedVariable<Double> workedFactor = new ITimedVariable<Double>() {
				@Override
				public Period getPeriod() {
					return period;
				}

				@Override
				public Double getValue(Period p) {
					
					double workedFactor = getCurrentBindings().get(PARTIAL_FACTOR,
							obj -> ((Number) obj).doubleValue(), 1.00);
					
					for ( ContextVariable ereFactor: ContextVariable.ERE_FACTORS )
						workedFactor *= (1.00 - getCurrentBindings().get(ereFactor,
							obj -> ((Number) obj).doubleValue(), 0.00));
					
					return workedFactor;
				}

			};
			ctx.putVariable(WORKED_FACTOR, workedFactor);

//			ITimedVariable<Boolean> ereBack = new ITimedVariable<Boolean>() {
//				@Override
//				public Period getPeriod() {
//					return period;
//				}
//
//				@Override
//				public Boolean getValue(Period p) {
//					return isEREBack(period);
//				}
//
//			};
			ITimedVariable<?> userEreBack = getExpressionContext().getVariable(ERE_BACK, period.getStart(),
					period.getEnd());

			if (userEreBack == null) {
				getEREBack(period).forEach(v -> ctx.putVariable(ERE_BACK, v ));
			} else {
			}

		}
		
		for ( Period period : nons ) {
			
		    ITimedVariable<Double> quoteDays = new ITimedVariable<Double>() {
			@Override
			public Period getPeriod() {
			    return period;
			}

			@Override
			public Double getValue(Period p) {
			    return getQuoteDays(ctx, p, 1.00);
			}

		    };
		    ITimedVariable<?> userQuoteDays = getExpressionContext().getVariable(QUOTE_DAYS, period.getStart(),
			    period.getEnd());

		    if (userQuoteDays == null) {
			ctx.putVariable(QUOTE_DAYS, quoteDays);
		    } else {
		    }
		    
		}
		
		// TGSS Periods ...
		
		List<Period> ssPeriods = getPeriods(MONTH_DAYS);
		for (ContextVariable ctxVar : new ContextVariable [] {
										TC2,
										UNEMPLOY_EMPLOYEE_PERCENT}) {
			
			List<Period> varPeriods = getPeriods(ctxVar);
			ssPeriods= split(ssPeriods, varPeriods);
			
		}
		
		ssPeriods = split(ssPeriods, leaves);
		for ( ContextVariable ctxVar : new ContextVariable [] {
					MONTH_DAYS
					,REGULATORY_BASE
		}) {
				
			List<ITimedVariable<Object>> aonTimedVars = ctx.getVariables(ctxVar);
			
			for ( ITimedVariable<Object> aonTimedVar : aonTimedVars ) {				
				
				for ( Period ssPeriod: ssPeriods ) {

					Period intersect = aonTimedVar.getPeriod().intersect(ssPeriod);

					if ( intersect == null || ssPeriod.equals(aonTimedVar.getPeriod()))
						continue;

					ITimedVariable<?> ssTimedVar = 
					Variables.getNarrowVariable(aonTimedVar, intersect);
					
					ctx.putVariable(ctxVar, ssTimedVar);
				}
			
			}
		
		}
		
		overrideHolidaysContextVariable(ctx);
		
	}

	private static List<Period> getPeriods(ContractExpressionContext ctx, ContextVariable var, Predicate<Object>  predicate) {
		return ctx.getPeriods(var).stream()
		.flatMap( p -> {
			try {
				return ctx.dryEval(var.getName(), p.getStart(), p.getEnd(), Object.class)
				.stream().filter( r -> predicate.test(r.getValue())).map( ITimedResult::getPeriod);
			} catch (ExpressionException e) {
				return Stream.empty();
			}
		}).collect(Collectors.toList());
	}

	private void loadTotalsContextVars(ContractExpressionContext ctx) {
		ITimedVariable<Double> totalWorkedDays = new ITimedVariable<Double>() {
			@Override
			public Period getPeriod() {
				return new Period(
						SQLContractSalaryCalculatorContext.this.startDate, 
						SQLContractSalaryCalculatorContext.this.getEnd());
			}

			@Override
			public Double getValue(Period p) {
				List<Period> periods = getPeriods(WORKED_DAYS);
				double workDays = 0.00;
				for ( Period period : periods )
					workDays += getWorkDays(ctx, period);			
				
				try {
					double avgPartialFactor = getAverageVariable(
							PARTIAL_FACTOR.getName(), 
							SQLContractSalaryCalculatorContext.this.startDate, 
							SQLContractSalaryCalculatorContext.this.getEnd());
					return workDays * ( avgPartialFactor == 0.00 ? 1.00 : avgPartialFactor );
				} catch (ExpressionExceptionWrapper e) {
				}
				return workDays;
			}

		};
		ctx.putVariable(TOTAL_WORKED_DAYS, totalWorkedDays);
	}
	
	private void fixItDaysWhenIfDays(ContractExpressionContext ctx) {
		List<Period> ifPeriods = ctx.getPeriods(ContextVariable.IF_DAYS);
		if ( ifPeriods.isEmpty() ) 
			return;
		
		List<Period> itPeriods = ctx.getPeriods(ContextVariable.LEAVE_DAYS);
		if ( itPeriods.isEmpty() ) 
			return;
		
		ctx.removeVariable(ContextVariable.LEAVE_DAYS);		
		
		for ( Period p : Period.sub(itPeriods, ifPeriods) ) {
			ctx.setVariable(ContextVariable.LEAVE_DAYS, p.daysStream().count(), p.getStart(), p.getEnd());
		}
		
		itPeriods = ctx.getPeriods(ContextVariable.COMMON_DISEASE_DAYS_21);
		
		if ( itPeriods.size() > 0 ) {  
			ctx.removeVariable(ContextVariable.COMMON_DISEASE_DAYS_21);	
			for ( Period p : Period.sub(itPeriods, ifPeriods) )
				ctx.setVariable(ContextVariable.COMMON_DISEASE_DAYS_21, p.daysStream().count(), p.getStart(), p.getEnd());
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


	}

	private void overrideHolidaysContextVariable(ContractExpressionContext ctx) {
		ctx.getVariables(HOLIDAYS).forEach( v -> {
			ctx.removeVariable(HOLIDAYS, v.getPeriod().getStart(), v.getPeriod().getEnd());
			ctx.putVariable(HOLIDAYS, new ITimedVariable<Double>() {

				@Override
				public Period getPeriod() {
					return v.getPeriod();
				}

				@Override
				public Double getValue(Period period) {
					return Math.min( period.getDays(), (( Number) v.getValue(period)).doubleValue());
				}
			});
		});
		;

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

	private double getFirstContexVariable(ExpressionContext ctx, Period p, ContextVariable var) {
		ITimedVariable<?> timedVariable = ctx.getVariable(var, p.getStart(), p.getEnd());
		if (timedVariable == null)
			throw new ExpressionExceptionWrapper(new UndefinedContextVariablesException(var));
		try {
			return ((Number) timedVariable.getValue(p)).doubleValue();
		} catch (ExpressionExceptionWrapper e) {
		}

		try {
			return ctx.eval(var.getName(), p.getStart(), p.getEnd(), Double.class).stream()
					.findFirst().get().getValue();
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
			
			Date dbContractEndDate = getDate(SQLConstants.CONTRACT, ContractColumns.END_DATE);
			
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
				dataEnd = ( dataEnd != null && dataEnd.equals(dbContractEndDate))? null: dataEnd; 
				Date start = Period.max(dataStart, startDate);
				Date end = Period.min(dataEnd, endDate);

				try {
					ctx.addExpression(expr, start, end);

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
					ctx.addExpression(expr, period.getStart(), period.getEnd());

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

	protected void onIrpf(IrpfOutcome irpfOutcome) {
		if (listener != null)
			listener.onIrpf(irpfOutcome);

	}

	protected void onUndefinedData(IExpression expression, String message, Date start, Date end, String... variables) {
		if (listener != null)
			for (String variable : variables)
				listener.onUndefinedData(expression, variable, message, start, end);

	}

	protected void onLiquid(ISalary salary) {
		if (listener != null)
			listener.onLiquid(salary);
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
		} catch ( IllegalArgumentException e ) {
			if (listener == null)
				return;
			listener.onInvalidLeave(start, end);
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


		
		if ( name.equals(DIRECT_PAY_START.getName())  ) {
			try {
				ctx.addExpression(expr, start, end);
			} catch (Exception e) {
			}
			return;
		}
			
		if ( Period.compare(end, start) < 0)
			return;
		
		Date leaveStart = rs.getDate(SQLConstants.CONTRACT_LEAVE + "." + ContractLeaveColumns.START_DATE);
		Date leaveEnd = rs.getDate(SQLConstants.CONTRACT_LEAVE + "." + ContractLeaveColumns.END_DATE);
		
		try {
			List<ITimedResult<Number>> factors = ctx.addExpression(expr, start, end, Number.class);
			
			for ( ITimedResult<Number> factor: factors ) {
				Date periodStart = Period.max(leaveStart, factor.getPeriod().getStart());
				Date periodEnd = Period.min(leaveEnd, factor.getPeriod().getEnd());
				if ( Period.compare(periodStart,periodEnd) > 0 )
					continue;
				Period period = new Period(periodStart,periodEnd);
				
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
			this.cleaveStmt.setDate(1, toSqlDate(this.getEnd()));
			this.cleaveStmt.setDate(2, toSqlDate(this.startDate));
			this.cleaveStmt.setInt(3, getId());
			this.cleaveStmt.setDate(4, toSqlDate(this.getEnd()));
			this.cleaveStmt.setDate(5, toSqlDate(this.startDate));
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
	
	private boolean isMonthly() {
		int lastDayOfMonth = AonDateUtils.get(AonDateUtils.getLastDayOfMonth(contractEndDate), Calendar.DAY_OF_MONTH);
		
		ITimedVariable<?> monthDaysVar = null; 
		try {
			monthDaysVar  = getExpressionContext().getVariable(MONTH_DAYS, contractStartDate, contractEndDate);
		} catch (Exception e) {
			try {
				getExpressionContext().eval(MONTH_DAYS.getName(), contractStartDate, contractEndDate);
				monthDaysVar  = getExpressionContext().getVariable(MONTH_DAYS, contractStartDate, contractEndDate);
			} catch (Exception e1) {
				return false;
			}
		}
		if ( monthDaysVar == null )
			return false;
		
		Number monthDays = 30.00;
		
		try {
			monthDays = (Number) monthDaysVar.getValue(monthDaysVar.getPeriod());
		}catch ( Exception e ) {
			try {
				getExpressionContext().eval(MONTH_DAYS.getName(), contractStartDate, contractEndDate);
				monthDaysVar  = getExpressionContext().getVariable(MONTH_DAYS, contractStartDate, contractEndDate);
				monthDays = (Number) monthDaysVar.getValue(monthDaysVar.getPeriod());
			} catch (Exception e1) {
				return false;
			}
		}
		
		if ( monthDays == null )
			return false;
		
		if ( monthDays.doubleValue() != 30.00)
			return false;
		
		if ( lastDayOfMonth != 30 )
			return true;
		
		if (!( monthDaysVar instanceof IExpressionVariable<?> ))
			return false; // we can't assure 
		
		String expression = ((IExpressionVariable<?>) monthDaysVar).getExpression().getExpression();
	
		Map<String,Object> map = new HashMap<String,Object>();
		map.put(QUOTE_GROUP.getName(), getQuoteGroup());
		map.put(NATURAL_MONTH_DAYS.getName(), 22);
		try {
			monthDays = ExpressionContext.eval(expression, map, Number.class);
			return monthDays.doubleValue() == 30.00;
		} catch ( Exception e ) {
			return false;
		}
	
	}

	private boolean isAllMonth() {
		return AonDateUtils.get(contractStartDate, Calendar.DATE ) == 1 
				&& AonDateUtils.get(contractEndDate, Calendar.DATE ) == AonDateUtils.get(AonDateUtils.getLastDayOfMonth(contractEndDate), Calendar.DATE)
				&& isConstantVariable(WEEK_HOURS);
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
			sql = orderBy(sql, CODE);
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
		getExpressionContext().getCurrentBindings().get(func);
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
//				try {
//					deferred.eval( contractExpressionContext, Object.class);
//				} catch ( UndefinedVariablesException e){
//					return true;
//				} catch (ExpressionException e) {
//					// TODO: return true ? Really it's undefined
//				}
			} catch (Throwable e) { 
				// TODO: return true ? Really it's undefined
			} 
		}
		return false;
	}
	
	protected boolean isWholeMonth() {
		return isWholeMonth(new Period(this.contractStartDate,this.contractEndDate));
	}
	
	private Double getLiquidStartValue(Date date, double liquid) {
		PaymentVariable paymentVar = getVariable(ContextVariable.PAYMENT_VARIABLE, PaymentVariable.class);
		IContractPayment payment = paymentVar.getPayment();
		String expression = payment.getExpression();
		String description = payment.getDescription();
		
		int contractId = getId();
		Date prevEndMonth = getLastDayOfMonth(add(date, Calendar.MONTH, -1));
		Date prevStartMonth = getFirstDayOfMonth(prevEndMonth);

		return 
		AON.getSalaries(new AONContext(connection),
		p -> p.getIsSalaryProperty().eq(true)
		.and(p.getContractProperty().eq(contractId))
		.and(p.getStartDateProperty().le(prevEndMonth))
		.and(p.getEndDateProperty().ge(prevStartMonth)))
		.filter(salary -> salary.getTotalLiquid().equals(liquid) )
		.findAny()
		.orElseThrow( IllegalStateException::new )
		.getPayments().stream()
		.filter( p -> AonStringUtils.equals(p.getExpression(),expression) || AonStringUtils.equals(p.getDescription(),description) )
		.findAny()
		.orElseThrow( IllegalStateException::new )
		.getAmount()
		;
	}
	
	private Date getStartDate (ContextVariable var, Period p) {
		List<ContractDataRecord> datas =
		new AONContext(connection)
		.getDslContext()
		.select()
		.from(CONTRACT_DATA)
		.where(CONTRACT_DATA.CONTRACT.eq(getId()))
		.and(CONTRACT_DATA.NAME.eq(var.getName()))
		.and(CONTRACT_DATA.START_DATE.le(toSqlDate(p.getEnd())))
		.orderBy(CONTRACT_DATA.START_DATE.desc())
		.fetchInto(CONTRACT_DATA);
		
		if ( datas.isEmpty()) 
			return p.getStart(); // ???
		
		Date startDate = datas.get(0).getStartDate();
		
		for (int i = 1 ; i < datas.size() ; i++) {
			Date endDate = datas.get(i).getEndDate();
			if ( endDate.before(add(startDate, DAY_OF_MONTH,-1)))
				break;
			startDate = datas.get(i).getStartDate();
		}
		
		
		return startDate;
	}
	
	private List<ITimedVariable<Boolean>> getFullERE (Period p) {
		
		Calendar ereStart = Calendar.getInstance();
		ereStart.set(2020, Calendar.MARCH, 23, 0, 0, 0);

		Map<Integer, List<ContractDataRecord>> contractEreFactorsMap = 
		getContractEreFactorsMap(new Period(Period.min(ereStart.getTime(), p.getStart()), p.getEnd()));
		
		Date enterpiseBackDate = null;
		
		
		for (Map.Entry<Integer, List<ContractDataRecord>> entry : contractEreFactorsMap.entrySet()) {
			List<ContractDataRecord> ereFactors = entry.getValue();
			ereFactors = ereFactors.stream().filter(c -> c.getId() != null ).collect(Collectors.toList());
			if ( ereFactors == null || ereFactors.isEmpty() )
				continue; 
			
			Collections.sort(ereFactors, (c1,c2) -> c1.getStartDate().compareTo(c2.getStartDate()));
			
			int i = 0; 
			double factor = Double.parseDouble(ereFactors.get(i).getExpression());
			Date employeeBackDate = ereFactors.get(i).getEndDate();
			for ( i= 1 ; i < ereFactors.size(); i++ ) {
				try {
					double newFactor = Double.parseDouble(ereFactors.get(i).getExpression());
					if ( factor !=  newFactor )
						break;
					Date newStartDate = add(ereFactors.get(i).getStartDate(), DAY_OF_MONTH,-1);
					if ( Period.compare(employeeBackDate, newStartDate) < 0) 
						break;
					employeeBackDate = ereFactors.get(i).getEndDate();
				} catch ( NullPointerException | NumberFormatException e) {
				}
				
			}
			
			enterpiseBackDate = Period.min(enterpiseBackDate, employeeBackDate);
		}
		
		try {
			enterpiseBackDate = getEnterpriseBackDate(p);
		} catch ( NotFoundVariableError e ) {
			; 
		}
		
		if ( Period.compare( enterpiseBackDate, p.getStart() ) < 0 )
			return Collections.singletonList(new TimedObject<Boolean>(false, p));
		
		if ( Period.compare( enterpiseBackDate, p.getEnd() ) >= 0 )
			return Collections.singletonList(new TimedObject<Boolean>(true, p));
		
		List<ITimedVariable<Boolean>>  fullEres = new ArrayList<ITimedVariable<Boolean>>(2);
		fullEres.add(new TimedObject<Boolean>(true, new Period ( p.getStart(), enterpiseBackDate)));
		fullEres.add(new TimedObject<Boolean>(false , new Period (add(enterpiseBackDate, DAY_OF_MONTH,1), p.getEnd())));
		
		
		return fullEres;
	}	
	
	
	private Date getEnterpriseBackDate (Period p) {
		
		java.sql.Date start = toSqlDate(p.getStart()); 
		java.sql.Date end = toSqlDate(p.getEnd()); 
		
		AONContext aonContext = new AONContext(connection); 
		DSLContext dslContext = aonContext.getDslContext();

		return 
		dslContext
		.select()
		.from(ENTERPRISE_DATA)
		.where(ENTERPRISE_DATA.ENTERPRISE.eq(getEnterpriseId() ))
		.and(ENTERPRISE_DATA.NAME.eq(FULL_ERE.getName()))
		.and(ENTERPRISE_DATA.START_DATE.le(end))
		.and((ENTERPRISE_DATA.END_DATE.isNull().or(ENTERPRISE_DATA.END_DATE.ge(start))))
		.fetchOptional(ENTERPRISE_DATA.START_DATE)
		.orElseThrow(() -> new NotFoundVariableError(FULL_ERE.getName()))
		;
		
	}
	
	private Map<Integer, List<ContractDataRecord>> getContractEreFactorsMap(Period p) {
		// only active employees, of course
		Condition activeEmployees = 
		CONTRACT.SS_REGIME.ne((byte)SSRegimeType.SELF_EMPLOYED.ordinal())
		.and(CONTRACT.START_DATE.le(toSqlDate(p.getEnd())))
		.and(CONTRACT.END_DATE.isNull().or(CONTRACT.END_DATE.ge(toSqlDate(p.getStart()))))
		;
		
		Condition activeEreFactors = 
		CONTRACT_DATA.NAME.eq(ContextVariable.ERE_FACTOR_FORCE_OFF.getName())
		.and(CONTRACT_DATA.START_DATE.le(toSqlDate(p.getEnd())))
		.and(CONTRACT_DATA.END_DATE.isNull().or(CONTRACT_DATA.END_DATE.ge(toSqlDate(p.getStart()))))
		;
		
		return
		new AONContext(connection)
		.getDslContext()
		.select()
		.from(WORKPLACE)
		.innerJoin(CONTRACT).on(WORKPLACE.ID.eq(CONTRACT.WORKPLACE).and(activeEmployees))
		.leftJoin(CONTRACT_DATA).on(CONTRACT.ID.eq(CONTRACT_DATA.CONTRACT).and(activeEreFactors))
		.where(WORKPLACE.ENTERPRISE.eq(getInt(SQLConstants.ENTERPRISE, EnterpriseColumns.REGISTRY)))
		.fetchGroups(CONTRACT.ID, r ->r.into(CONTRACT_DATA))
		;
		
	}
	
	private Boolean isFullERE (Period p) {

		Calendar c = Calendar.getInstance();
		c.set(2020, Calendar.MARCH, 23, 0, 0, 0);

		Map<Integer, List<ContractDataRecord>> contractEreFactorsMap = 
		getContractEreFactorsMap(new Period(c.getTime(),p.getEnd()));
		
		Date start = p.getStart();
		for (Map.Entry<Integer, List<ContractDataRecord>> entry : contractEreFactorsMap.entrySet()) {
			List<ContractDataRecord> ereFactors = entry.getValue();
			if ( ereFactors == null || ereFactors.isEmpty() )
				return false; // this employee isn't at ERE.
			
			start  = p.getStart();
			for ( ContractDataRecord ereFactor : ereFactors ) {
				if ( ereFactor.getId() == null ) 
					return false;
				
				if ( ereFactor.getStartDate().after(start))
					return false; 
				
				try {
					double factor = Double.parseDouble(ereFactor.getExpression());
					if ( factor < 1.00 )
						return false;
				} catch ( NullPointerException | NumberFormatException e) {
					return false;
				}
				
				Date endDate = ereFactor.getEndDate() == null ? 
				p.getEnd() : ereFactor.getEndDate();
				
				start = DateUtils.addDays(endDate,1);
			}
		}
		
		return start.after(p.getEnd());
		
		
	}
	
	private Boolean isEREBack (Period p) {
		Calendar c = Calendar.getInstance();
		c.set(2020, Calendar.APRIL, 30, 0, 0, 0);
		return isEREBack(c.getTime(), p);
	}
	
	private List<ITimedVariable<Boolean>> getEREBack (Period p) {
		return isEREBack(p) ?  Collections.singletonList( new TimedObject<Boolean>(true,p)) : Collections.emptyList();
	}

	private Boolean isEREBack (Date date, Period p) {
		
		if ( Period.compare(p.getEnd(), date) <= 0 )
			return false;
		
		double currentEreFactor = 0.00;
		List<ITimedVariable<?>> ereFactorVars = getVariables(ContextVariable.ERE_FACTOR_FORCE_OFF.getName(), p.getStart(), p.getEnd(), Collectors.toList());
		if ( ereFactorVars.size() > 0  ) {
			Collections.sort(ereFactorVars, (v1,v2) -> v1.getPeriod().compareTo(v2.getPeriod()));
			ITimedVariable<?> lastEreFactorVar = ereFactorVars.get(ereFactorVars.size()-1);
			if ( Period.compare(lastEreFactorVar.getPeriod().getEnd(), p.getEnd() ) >= 0 ) {
				try {
					currentEreFactor = ((Number)lastEreFactorVar.getValue(lastEreFactorVar.getPeriod())).doubleValue();
				} catch ( Exception e ) {
					
				}
			}
		}
		
		double startEreFactor = 0.00;
		List<String> ereExpressions =
		new AONContext(connection)
		.getDslContext()
		.select()
		.from(CONTRACT_DATA)
		.where(CONTRACT_DATA.CONTRACT.eq(getId()))
		.and(CONTRACT_DATA.NAME.eq(ContextVariable.ERE_FACTOR_FORCE_OFF.getName()))
		.and(CONTRACT_DATA.START_DATE.le(toSqlDate(date)))
		.and(CONTRACT_DATA.END_DATE.ge(toSqlDate(date)))
		.fetch(CONTRACT_DATA.EXPRESSION)
		;
		
		if ( ereExpressions.isEmpty() ) 
			return false;
		
		for (String expression : ereExpressions) {
			try {
				startEreFactor = Double.parseDouble(expression);
			} catch (NullPointerException | NumberFormatException e) {
			}
		}
		
		
		return currentEreFactor < startEreFactor;
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
			public Double visitCommonDiseaseAtLack(LeaveType leaveType) {
				// TODO Auto-generated method stub
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
			
			@Override
			public Double visitCommonProfessionalDisease(LeaveType leaveType) {
				return visitOcupationalDisease(leaveType);
			}
			
			@Override
			public Double visitMenstruation(LeaveType leaveType) {
        			// TODO Auto-generated method stub
        			return null;
			}
			
			@Override
			public Double visitPregnacyStop(LeaveType leaveType) {
        			// TODO Auto-generated method stub
        			return null;
			}
			
			@Override
			public Double visitPregnacy39Week(LeaveType leaveType) {
        			// TODO Auto-generated method stub
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

	protected static ContextVariable getDayHoursVar(Calendar calendar) {
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
	
	
	private static Collection<ContextVariable> getEreDaysVars() {
		return
		Arrays.stream(ContextVariable.values())
		.filter( v -> v.getName().startsWith(ERE_DAYS.getName()))
		.collect(Collectors.toList());
	}
	
	private static Collection<ContextVariable> getEreFactorsVars() {
		return
		Arrays.stream(ContextVariable.values())
		.filter( v -> v.getName().startsWith(ERE_FACTOR.getName()))
		.collect(Collectors.toList());
	}
	
	private static <T extends Object> List<ITimedVariable<T>> join(List<ITimedVariable<T>> list) {
		if ( list.isEmpty()  ) 
			return list;
		
		list.sort( (v1, v2) -> v1.getPeriod().compareTo(v2.getPeriod()));
		Stack<ITimedVariable<T>> joined = new Stack<ITimedVariable<T>>();
		joined.push( list.get(0) );
		
		for ( int i = 1; i < list.size() ; i++) {
			ITimedVariable<T> v1 = joined.peek();
			ITimedVariable<T> v2 = list.get(i);
			T value1 = v1.getValue(v1.getPeriod());
			T value2 = v2.getValue(v2.getPeriod());
			Date enDate1 = v1.getPeriod().getEnd();
			Date startDate1 = AonDateUtils.add(enDate1, DAY_OF_MONTH,1);
			Date startDate2 = v2.getPeriod().getStart();
			if (AonUtils.notEquals(value1, value2)) {
				joined.push(v2);
			} else if ( startDate2.after(startDate1) ) {
				joined.push(v2);
			} else {
				startDate1 = v1.getPeriod().getStart();
				Date endDate2 = v2.getPeriod().getEnd();
				joined.pop();
				joined.push(new TimedObject<T>(value1, new Period( startDate1, endDate2 )));
			}
		}
		
		return joined.stream().collect(Collectors.toList());
	}
	
	private SSRegimeType getSSsRegimeType(int ordinal ) {
		SSRegimeType [] ssRegimeTypes = SSRegimeType.values();
		if ( ordinal < 0 ) 
			return SSRegimeType.GENERAL;
		if ( ordinal >= ssRegimeTypes.length ) 
			return SSRegimeType.GENERAL;
		
		SSRegimeType ssRegimeType = ssRegimeTypes[ordinal];
		switch (ssRegimeType) {
		case ARTIST:
		case AGRICULTURAL:
			return SSRegimeType.GENERAL;
		default:
			return ssRegimeType;
		}
	}
	

}
