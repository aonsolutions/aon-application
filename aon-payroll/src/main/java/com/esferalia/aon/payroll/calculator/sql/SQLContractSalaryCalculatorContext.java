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
import static com.esferalia.aon.payroll.enumeration.ContextVariable.END;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.EXTRA_PAY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FEMALE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FRIDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FULL_TIME;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.GENDER;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.GROSS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.GUARANTEE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.HOLIDAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.IMS_RATE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.INDEFINITE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.IRPF_PERCENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.IT_RATE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.LEAVE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.LIQUID;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MALE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MORE_THAN_65;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PARTIAL_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PAYMENT_VARIABLE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SALARY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SALARY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SALARY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SATURDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SELF;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SENIORITY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SETTLE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SHORT_CONTRACT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.START;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRIKE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SUNDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SYSTEM;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.THURSDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TUESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WEDNESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WEEK_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_YEARS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.parse;
import static com.esferalia.aon.salary.expression.ExpressionContext.getCurrentBindings;
import static com.esferalia.aon.watson.util.AonDateUtils.add;
import static com.esferalia.aon.watson.util.AonDateUtils.getMax;
import static com.esferalia.aon.watson.util.AonStringUtils.equals;
import static com.esferalia.aon.watson.util.AonUtils.ifnull;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
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

import org.ajax4jsf.renderkit.ProducerContext;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.PegasusSolver;
import org.apache.commons.math3.analysis.solvers.UnivariateSolver;
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
import com.esferalia.aon.payroll.IrpfOutcome;
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
import com.esferalia.aon.payroll.calculator.ISystemCost;
import com.esferalia.aon.payroll.calculator.ISystemDeduction;
import com.esferalia.aon.payroll.calculator.ISystemPayment;
import com.esferalia.aon.payroll.calculator.LRUCache;
import com.esferalia.aon.payroll.calculator.OnlyPaymentContractSalaryCalculator;
//import com.esferalia.aon.payroll.calculator.SQLNoItContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.SalaryExpressionException;
import com.esferalia.aon.payroll.calculator.UndefinedContextVariablesException;
import com.esferalia.aon.payroll.calculator.UndefinedTotalPaymentException;
//import com.esferalia.aon.payroll.calculator.SQLContractLeaveLoader.Leave;
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
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementLevelCategoryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractDataColumns;
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
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.CheckException;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionContext.ExpressionExceptionWrapper;
import com.esferalia.aon.salary.expression.ExpressionContext.MacroException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionImpl;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpression;
import com.esferalia.aon.salary.expression.IExpressionVariable;
import com.esferalia.aon.salary.expression.ITimedObject;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.InterruptedException;
import com.esferalia.aon.salary.expression.InvalidVariables;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.TimedObject;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;
import com.esferalia.aon.salary.expression.Variables.NotFoundHandler;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.AonUtils;
import com.sun.mail.imap.protocol.ListInfo;

public class SQLContractSalaryCalculatorContext extends
		AbstractContractSalaryCalculatorContext implements
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

	private static final SSRegimeType SS_REGIMES[] = SSRegimeType.class
			.getEnumConstants();

	// @formatter:off
	private static final String MAIN_SQL = "SELECT * "
			+ " FROM contract"
			+ " LEFT JOIN enterprise_ccc ON (contract.enterprise_ccc = enterprise_ccc.id)"
			+ " LEFT JOIN enterprise_activity ON (contract.enterprise_activity = enterprise_activity.id)"
			+ " LEFT JOIN agreement_level_category ON (contract.agreement_level_category = agreement_level_category.id)"
			+ " LEFT JOIN agreement_level ON (agreement_level.id = agreement_level_category.agreement_level)"
			+ " LEFT JOIN agreement ON (agreement.id = agreement_level.agreement)"
			+ ", person"
			+ ", registry AS "
			+ PERSON_REGISTRY
			+ ", workplace"
			+ " LEFT JOIN payroll_workplace ON (payroll_workplace.workplace = workplace.id)"
			+ ", enterprise"
			+ ", registry AS "
			+ ENTERPRISE_REGISTRY
			+ " LEFT JOIN customer ON (customer.registry = "
			+ ENTERPRISE_REGISTRY
			+ ".id)"
			+ ", raddress"
			+ " WHERE contract.person = person.registry" // INNER JOIN: person
															// is NOT NULL
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
			+ " AND contract.start_date <= ? "
			+ " AND ( contract.end_date  IS NULL"
			+ " OR contract.end_date >= ? )";
	// @formatter:on

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
			+ " AND system_cost.domain <= 0 ";

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
			+ " OR end_date >= ? )" + " AND system_deduction.domain <= 0 ";

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
			+ " WHERE start_date <= ? " + " AND ( end_date IS NULL"
			+ " OR end_date >= ? )" + " AND "
			+ SQLContractPayment.PAYMENT_ALIAS + ".domain <= 0 ";

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

	public static class PaymentVariable {

		private IContractPayment payment;

		public PaymentVariable(IContractPayment payment) {
			this.payment = payment;
		}

		public Integer getMES() {
			return payment.getMonth() != null ? payment.getMonth().getValue() + 1
					: null;
		}

	}
	

	public static class AgreementContextKey {

		private Integer domain;
		private Integer agreementId;
		private Integer agreementLevelId;

		public AgreementContextKey(Integer domain, Integer agreementId,
				Integer agreementLevelId) {
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
			return obj instanceof AgreementContextKey
					&& AonUtils.equals(domain,
							((AgreementContextKey) obj).domain)
					&& AonUtils.equals(agreementId,
							((AgreementContextKey) obj).agreementId)
					&& AonUtils.equals(agreementLevelId,
							((AgreementContextKey) obj).agreementLevelId);
		}

		@Override
		public int hashCode() {
			return AonUtils.hashCode(domain) + AonUtils.hashCode(agreementId)
					+ AonUtils.hashCode(agreementLevelId);
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

		private double guarentee;

		public GuarenteeException(double guarentee) {
			this.guarentee = guarentee;
		}

		public double getGuarentee() {
			return guarentee;
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

	public static class SQLNoItContractSalaryCalculatorContext extends
			SQLContractSalaryCalculatorContext {

		private int end;
		private int start;

		public SQLNoItContractSalaryCalculatorContext(Connection connection,
				final Date startDate, final Date endDate, Date issueDate,
				Criteria criteria, final int start, final int end)
				throws SQLException, ExpressionException {

			super(connection, startDate, endDate, issueDate, criteria);
			this.start = start + 1;
			this.end = end + 1;
			// with this, we assure no leave I.T.
			super.leaveLoader = new SQLContractLeaveLoader(startDate, endDate) {
				@Override
				public void loadContractLeave(Integer id, Date leaveStart,
						Date leaveEnd, long parentDays, LeaveType type,
						Double dailyRegBase, ExpressionContext exprCtx)
						throws ExpressionException {

					Period leavePeriod = new Period(leaveStart, leaveEnd);

					Calendar leaveCalendar = Calendar.getInstance();
					leaveCalendar.setTime(leaveStart);
					leaveCalendar.add(Calendar.DATE, start - (int) parentDays);
					Date guarenteeStart = leaveCalendar.getTime();
					leaveCalendar.add(Calendar.DATE, end - start);
					Date guarenteeEnd = leaveCalendar.getTime();

					Period guarenteePeriod = new Period(guarenteeStart,
							guarenteeEnd);

					List<Period> leavePeriods = leavePeriod
							.sub(guarenteePeriod);
					for (Period p : leavePeriods) {
						long leaveParentDays = CommonUtil.getDaysBetweenDates(
								leaveStart, p.getStart());
						super.loadContractLeave(id, p.getStart(), p.getEnd(),
								parentDays + leaveParentDays, type,
								dailyRegBase, exprCtx);
					}

					exprCtx.putVariable(ContextVariable.REGULATORY_BASE,
							new TimedObject<Double>(0.00, guarenteeStart,
									guarenteeEnd));

					type.accept(new LeaveTypeVisitor<Void>() {

						@Override
						public Void visitCommonDisease(LeaveType leaveType) {
							exprCtx.setVariable(
									ContextVariable.COMMON_DISEASE_DAYS, 0,
									guarenteeStart, guarenteeEnd);
							return null;
						}

						@Override
						public Void visitOcupationalDisease(LeaveType leaveType) {
							exprCtx.setVariable(
									ContextVariable.OCCUPATIONAL_DISEASE_DAYS,
									0, guarenteeStart, guarenteeEnd);
							return null;
						}

						@Override
						public Void visitMaternity(LeaveType leaveType) {
							exprCtx.setVariable(ContextVariable.MATERNITY_DAYS,
									0, guarenteeStart, guarenteeEnd);
							return null;
						}

						@Override
						public Void visitPaternity(LeaveType leaveType) {
							exprCtx.setVariable(ContextVariable.PATERNITY_DAYS,
									0, guarenteeStart, guarenteeEnd);
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
						public Void visitNonOcupationalDisease(
								LeaveType leaveType) {
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

		@Override
		public Object guarantee(double guarentee, int start, int end)
				throws ExpressionException {
			if (this.start == start && this.end == end) {
				throw new SalaryExpressionException(new GuarenteeException(
						guarentee));
			}
			return super.guarantee(guarentee);
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
	private ExpressionContext contractExpressionContext;
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

		this.cnae2009 = new SQLCnae2009(connection, this.startDate,
				this.endDate);

		calendarFactory = new SQLCalendarFactory(connection, this.startDate,
				this.endDate);
		this.calendars = new LRUCache<Integer, ICalendar>(CACHE_SIZE,
				calendarFactory);
		calendarFactory.setCache(calendars); // TODO: Todo en la misma clase???

		agreementPaymentsFactory = new SQLAgreementPaymentsFactory(connection,
				this.startDate, this.endDate, this.paymentsCriteria);
		this.agreementPayments = new LRUCache<AgreementKey, Collection<ISystemPayment>>(
				CACHE_SIZE, agreementPaymentsFactory);

		cccExpressionContexts = new LRUCache<CCCContextKey, ExpressionContext>(
				CACHE_SIZE, new SQLSystemExpressionContextFactory(connection,
						this.startDate, this.endDate, order));

		agreementContextFactory = new SQLAgreementContextFactory(connection,
				this::getCCCExpressionContext, this.startDate, this.endDate,
				order);
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
		try {
			return cccExpressionContexts.get(new CCCContextKey(getCCCType(),
					getSSRegime()));
		} catch (Exception e) {
			return cccExpressionContexts.get(new CCCContextKey(null, null));
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
		int ordinal = getInt(SQLConstants.CONTRACT, ContractColumns.SS_REGIME);
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
		AgreementKey agreementKey = getAgreementKey();
		Collection<ISystemPayment> payments = agreementPayments
				.get(agreementKey);

		AgreementKey enterpriseAgreementKey = getEnterpriseAgreementKey();
		Collection<ISystemPayment> enterprisePayments = agreementPayments
				.get(enterpriseAgreementKey);
		return new DomainPayments(enterprisePayments, payments);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection<IContractPayment> getContractPayments()
			throws AonException {
		try {
			this.sqlContractPayment.close();
			int id = getId();
			paymentStmt.setInt(1, id);
			ResultSet rs = paymentStmt.executeQuery();
			this.sqlContractPayment.setResultSet(rs);

			return new CompositePayments(this.sqlContractPayment,
					getAgreementPayments(), getSSRegimePayments()) {
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
							if (nextPayment != null)
								SQLContractSalaryCalculatorContext.this
										.getExpressionContext()
										.setVariable(
												ContextVariable.PAYMENT_VARIABLE,
												new PaymentVariable(nextPayment),
												nextPayment.getStartDate(),
												nextPayment.getEndDate());
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
	public Collection<IContractDeduction> getContractDeductions()
			throws AonException {

		try {
			this.sqlContractDeduction.close();
			int id = getId();
			deductionStmt.setInt(1, id);
			ResultSet rs = deductionStmt.executeQuery();
			this.sqlContractDeduction.setResultSet(rs);
			HierarchyDeductions hierarchyDeductions = new HierarchyDeductions(
					this.sqlContractDeduction, getCCCDeductions().iterator(),
					getSSRegimeDeductions().iterator());
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
//		return new HierarchyDeductions(getCCCCosts().iterator(),
//				getSSRegimeCosts().iterator());
		return new CompositeCosts(getCCCCosts(), getSSRegimeCosts());
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
		Object cna2009 = getObject(SQLConstants.ENTERPRISE_ACTIVITY,
				EnterpriseActivityColumns.CNAE2009);
		return cna2009 != null ? (Integer) cna2009 : null;
	}

	public CCCType getCCCType() {
		int ordinal = getInt(SQLConstants.ENTERPRISE_CCC,
				EnterpriseCccColumns.TYPE);
		return CCCType.values()[ordinal];
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

	public String getString(String tableLabel, String columnLabel) {
		try {
			return this.resultSet.getString(tableLabel + "." + columnLabel);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public <T> void addVariable(String name, T t) {
		this.contractExpressionContext.setVariable(name, t,
				this.contractStartDate, this.contractEndDate);
	}

	public <T> T getVariable(ContextVariable var, Class<T> toType) {
		return getVariable(var.getName(), toType);
	}

	public <T> T getVariable(ContextVariable var, Period p, Class<T> toType) {
		return this.contractExpressionContext.getVariable(var.getName(),
				p.getStart(), p.getEnd(), toType);
	}

	public <T> T getVariable(String name, Class<T> toType) {
		return this.contractExpressionContext.getVariable(name,
				this.contractStartDate, this.contractEndDate, toType);
	}

	public static java.sql.Date toSqlDate(Date date) {
		return new java.sql.Date(date.getTime());
	}

	public static Object guaranteee(double guarentee)
			throws ExpressionException {
		throw new MacroException() {
			@Override
			public String doMacro(String expr) {
				return expr.replaceAll(ContextVariable.GUARANTEE,
						"SELF.guarantee");
			}
		};
	}

	public static Object guaranteee(double guarentee, int start)
			throws ExpressionException {
		return guaranteee(guarentee);
	}

	public static Object guaranteee(double guarentee, int start, int end)
			throws ExpressionException {
		return guaranteee(guarentee);
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

	public void loadContractLeave(Integer id, Date leaveStart, Date leaveEnd,
			long parentDays, LeaveType type, Double dailyRegBase,
			ExpressionContext exprCtx) throws ExpressionException {
		leaveLoader.loadContractLeave(id, leaveStart, leaveEnd, parentDays,
				type, dailyRegBase, exprCtx);
	}

	public void loadContractLeave(Integer id, Date leaveStart, Date leaveEnd,
			long parentDays, LeaveType type, String dailyRegBase,
			ExpressionContext exprCtx) throws ExpressionException {
		leaveLoader.loadContractLeave(id, leaveStart, leaveEnd, parentDays,
				type, dailyRegBase, exprCtx);
	}

	public void clean(ExpressionContext exprCtx, Leave leave) {
		leaveLoader.clean(exprCtx, leave);
	}

	public SortedSet<Leave> getLeaves() {
		return leaveLoader.getLeaves();
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
		Object domain = getObject(SQLConstants.AGREEMENT,
				AgreementColumns.DOMAIN);
		return domain == null ? null : (Integer) domain;
	}

	protected Integer getEnterpriseDomain() {
		Object domain = getObject(SQLConstants.ENTERPRISE,
				EnterpriseColumns.DOMAIN);
		return domain == null ? null : (Integer) domain;
	}

	protected AgreementKey getAgreementKey() {
		Object id = getObject(SQLConstants.AGREEMENT, AgreementColumns.ID);
		Object domain = getObject(SQLConstants.AGREEMENT,
				AgreementColumns.DOMAIN);
		return id == null ? null : new AgreementKey((Integer) id,
				(Integer) domain);
	}

	protected AgreementKey getEnterpriseAgreementKey() {
		Object id = getObject(SQLConstants.AGREEMENT, AgreementColumns.ID);
		Object domain = getObject(SQLConstants.ENTERPRISE,
				EnterpriseColumns.DOMAIN);
		return id == null ? null : new AgreementKey((Integer) id,
				(Integer) domain);
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

		Integer agreementId = getAgreementId();
		Integer agreementLevelId = getAgreementLevel();
		Integer agreementDomain = getAgreementDomain();

		AgreementContextKey agreementAndLevelKey = new AgreementContextKey(
				agreementDomain, agreementId, agreementLevelId);

		ExpressionContext agreementCtx = agreementExpressionContexts
				.get(agreementAndLevelKey);

		Integer enterpriseDomain = getEnterpriseDomain();
		AgreementContextKey enterpriseAndLevel = new AgreementContextKey(
				enterpriseDomain, agreementId, agreementLevelId);
		ExpressionContext enterpriseCtx = agreementExpressionContexts
				.get(enterpriseAndLevel);

		ExpressionContext ctx = new ExpressionContext(agreementCtx);
		ctx.add(enterpriseCtx);

		return ctx;
	}

	private Collection<IContractCost> getCCCCosts() throws AonException {
		List<IContractCost> costs = new ArrayList<IContractCost>(
				systemPayments.size());
		CCCType cccType = getCCCType();
		for (ISystemCost systemCost : systemCosts) {
			if (filter(systemCost, cccType)) {
				costs.add(systemCost);
			}
		}
		return costs;

	}

	private Collection<IContractCost> getSSRegimeCosts() throws AonException {
		List<IContractCost> costs = new ArrayList<IContractCost>(
				systemCosts.size());
		SSRegimeType ssRegime = getSSRegime();
		for (ISystemCost systemCost : systemCosts) {
			if (filter(systemCost, ssRegime)) {
				costs.add(systemCost);
			}
		}
		return costs;

	}

	private Collection<IContractPayment> getCCCPayments() throws AonException {
		List<IContractPayment> payments = new ArrayList<IContractPayment>(
				systemPayments.size());
		CCCType cccType = getCCCType();
		for (ISystemPayment systemPayment : systemPayments) {
			if (filter(systemPayment, cccType)) {
				payments.add(systemPayment);
			}
		}
		return payments;
	}

	private Collection<IContractPayment> getSSRegimePayments()
			throws AonException {
		List<IContractPayment> payments = new ArrayList<IContractPayment>(
				systemPayments.size());
		SSRegimeType ssRegime = getSSRegime();
		for (ISystemPayment systemPayment : systemPayments) {
			if (filter(systemPayment, ssRegime)) {
				payments.add(systemPayment);
			}
		}
		return payments;

	}

	private Collection<IContractDeduction> getCCCDeductions()
			throws AonException {
		List<IContractDeduction> deductions = new ArrayList<IContractDeduction>(
				systemDeductions.size());
		CCCType cccType = getCCCType();
		for (ISystemDeduction systemDeduction : systemDeductions) {
			if (filter(systemDeduction, cccType)) {
				deductions.add(systemDeduction);
			}
		}
		return deductions;

	}

	private Collection<IContractDeduction> getSSRegimeDeductions()
			throws AonException {
		List<IContractDeduction> deductions = new ArrayList<IContractDeduction>(
				systemDeductions.size());
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

	private boolean filter(ISystemDeduction systemDeduction,
			SSRegimeType ssRegime) {
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
		Object calendarId = getObject(SQLConstants.CONTRACT,
				ContractColumns.CALENDAR);
		if (calendarId == null) {
			calendarId = getObject(SQLConstants.PAYROLL_WORKPLACE,
					PayrollWorkplaceColumns.CALENDAR);
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

	public Object getNoItSalary(Connection conn, Date date, SalaryType type,
			Integer contractId) {

		final Criteria contractCriteria = new Criteria();
		contractCriteria.addExpression(criteria.getExpression());
		contractCriteria.addEqualExpression(SQLConstants.CONTRACT + "."
				+ ContractColumns.ID, getId());
		SQLNoItContractSalaryCalculatorContext ctx;

		Date startDate = getFirstDayOfMonth(date);
		Date endDate = getLastDayOfMonth(date);
		return getNoItCalculatorContext(connection, startDate, endDate,
				endDate, contractCriteria, 0, Integer.MAX_VALUE - 1);
	}

	public Object gross(double gross, Date start, Date end)
			throws ExpressionException, SQLException, SalaryException {
		return paymentImpl(gross, 0.005, start, end);
	}

	public Object paymentImpl(double payment, double accuracy, Date start,
			Date end) throws ExpressionException, SQLException, SalaryException {
		try {
			return solvePayment(new PegasusSolver(accuracy), payment, start,
					end);
		} catch (Throwable t) {
			throw new CheckException(t.getMessage());
		}

	}

	public Object liquid(double liquid, Date start, Date end)
			throws ExpressionException, SQLException, SalaryException {
		return liquidImpl(liquid, 0.005, start, end);
	}

	public Object liquidImpl(double liquid, double accuracy, Date start,
			Date end) throws ExpressionException, SQLException, SalaryException {
		try {
			return solveLiquid(new PegasusSolver(accuracy), liquid, start, end);
		} catch (Throwable t) {
			throw new CheckException(t.getMessage());
		}
	}

	public Object guaranteeWarn(String message) throws ExpressionException {

		if (leaveLoader.isEmpty())
			throw new UndefinedContextVariablesException(
					ContextVariable.LEAVE_DAYS);

		throw new CheckException(message);
	}

	public Object guarantee(double guarentee) throws ExpressionException {
		return guarantee(guarentee, 1, Integer.MAX_VALUE);
	}

	public Object guarantee(double guarentee, int start)
			throws ExpressionException {
		return guarantee(guarentee, start, Integer.MAX_VALUE);
	}

	public Object guarantee(double guarentee, int start, int end)
			throws ExpressionException {

		if (leaveLoader.isEmpty())
			throw new UndefinedContextVariablesException(
					ContextVariable.LEAVE_DAYS);

		// Assert all PREST_IT payments have been calculated.
		Double totalPayment = getVariable(ContextVariable.TOTAL_PAYMENT,
				Double.class);
		if (totalPayment == null)
			throw new UndefinedTotalPaymentException();

		final Criteria contractCriteria = new Criteria();
		contractCriteria.addExpression(criteria.getExpression());
		contractCriteria.addEqualExpression(SQLConstants.CONTRACT + "."
				+ ContractColumns.ID, getId());
		ISalaryCalculatorContext ctx = null;
		try {
			ctx = getNoItCalculatorContext(connection, startDate, endDate,
					issueDate, contractCriteria, start - 1, end - 1);
		} catch (RuntimeException e) {
			throw e;
		}

		ContractSalaryCalculator<Salary> calculator = new OnlyPaymentContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		try {
			calculator.calculate(ctx);
		} catch (GuarenteeException e) {

			if (e.guarentee == guarentee)
				throw new ContextFunctions.UselessGuaranteeException(guarentee);
			if (e.guarentee < guarentee)
				throw new ContextFunctions.UselessGuaranteeException(
						e.guarentee, guarentee);

			// Gets PREST. IT for GTZDO periods.
			double prestIt = 0.00;
			List<ITimedVariable<Object>> leaves = ctx.getExpressionContext()
					.getVariables(LEAVE_DAYS.getName(), ctx.getStartDate(),
							ctx.getEndDate());
			Date guaranteeStart = ctx.getStartDate();
			for (ITimedVariable<Object> leave : leaves) {
				Period leavePeriod = leave.getPeriod();
				Date leaveStart = leavePeriod.getStart();
				if (leaveStart.compareTo(guaranteeStart) > 0)
					prestIt += getDayDoubleVariable(PREST_IT.getName(),
							guaranteeStart, prev(leaveStart));

				guaranteeStart = next(leavePeriod.getEnd());
			}
			if (guaranteeStart.compareTo(ctx.getEndDate()) <= 0)
				prestIt += getDayDoubleVariable(PREST_IT.getName(),
						guaranteeStart, ctx.getEndDate());

			return e.guarentee - guarentee - prestIt;

		} catch (SalaryException e) {
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		return 0.00;
	}

	protected double solveLiquid(UnivariateSolver solver, final double liquid,
			Date start, Date end) {

		final Criteria contractCriteria = new Criteria();
		contractCriteria.addExpression(criteria.getExpression());
		contractCriteria.addEqualExpression(SQLConstants.CONTRACT + "."
				+ ContractColumns.ID, getId());

		double result = solver.solve(Byte.MAX_VALUE, new UnivariateFunction() {

			@Override
			public double value(double x) {
				try {
					ISalaryCalculatorContext ctx = getLiquidCalculatorContext(
							connection, start, end, issueDate,
							contractCriteria, x);
					ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
					calculator.setSalaryBuilder(new SalaryBuilder());

					// TODO: Warning a bit tricky.
					ExpressionContext expressionCtx = SQLContractSalaryCalculatorContext.this
							.getExpressionContext();
					((ISQLContractSalaryCalculatorContext) ctx)
							.setListener(irpf -> expressionCtx.setVariable(
									ContextVariable.IRPF_PERCENT, irpf
											.getIrpfResult().getIrpf(), start,
									end));

					ISalary salary = calculator.calculate(ctx);

					return liquid - salary.getTotalLiquid();
				} catch (SalaryException e) {
					throw new RuntimeException(e);
				}
			}

		}, -20 * liquid, 20 * liquid, 0);

		return result;
	}

	protected double solvePayment(UnivariateSolver solver,
			final double payment, Date start, Date end) {
		final Criteria contractCriteria = new Criteria();
		contractCriteria.addExpression(criteria.getExpression());
		contractCriteria.addEqualExpression(SQLConstants.CONTRACT + "."
				+ ContractColumns.ID, getId());

		double result = solver.solve(Byte.MAX_VALUE, new UnivariateFunction() {

			@Override
			public double value(double x) {
				try {
					ISalaryCalculatorContext ctx = getPaymentCalculatorContext(
							connection, start, end, end, contractCriteria, x);
					ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
					calculator.setSalaryBuilder(new SalaryBuilder());

					ISalary salary = calculator.calculate(ctx);
					return payment - salary.getTotalPayment();
				} catch (SalaryException e) {
					throw new RuntimeException(e);
				}
			}

		}, -20 * payment, 20 * payment, 0);
		return result;
	}

	protected ISQLContractSalaryCalculatorContext getNoItCalculatorContext(
			Connection conn, Date startDate, Date endDate, Date issueDate,
			Criteria criteria, int start, int end) {
		SQLContractSalaryCalculatorContext ctx;
		try {
			ctx = new SQLNoItContractSalaryCalculatorContext(conn, startDate,
					endDate, issueDate, criteria, start, end);
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
				public Object liquid(double liquid, Date start, Date end)
						throws ExpressionException, SQLException {
					return x;
				}

				@Override
				public Object gross(double gross, Date start, Date end)
						throws ExpressionException, SQLException,
						SalaryException {
					throw new InterruptedException(
							String.format("Lo sentimos. La funci\u00F3n NETO es incompatible con la funci\u00F3n BRUTO. Elija una de las dos. :-("));
				}

				@Override
				protected ISQLContractSalaryCalculatorContext getNoItCalculatorContext(
						Connection conn, Date startDate, Date endDate,
						Date issueDate, Criteria criteria, int start, int end) {
					SQLContractSalaryCalculatorContext ctx;
					try {
						ctx = new SQLNoItContractSalaryCalculatorContext(conn,
								startDate, endDate, issueDate, criteria, start,
								end) {

							@Override
							public Object liquid(double liquid, Date start,
									Date end) throws ExpressionException,
									SQLException {
								return x;
							};

							@Override
							public Object gross(double gross, Date start,
									Date end) throws ExpressionException,
									SQLException, SalaryException {
								throw new InterruptedException(
										String.format("Lo sentimos. La funci\u00F3n NETO es incompatible con la funci\u00F3n BRUTO. Elija una de las dos. :-("));
							}

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
						SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
								conn, startDate, endDate, endDate, criteria) {

							@Override
							public double getIrpf() {
								return 0.00;
							};

							@Override
							public Object liquid(double liquid, Date start,
									Date end) throws ExpressionException,
									SQLException {
								return x;
							}

						};
						ctx.leaveLoader = leaveLoader;
						return new SQLIrpfCalculatorContext(connection,
								startDate, endDate, ctx) {
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
						throw new ExpressionExceptionWrapper(
								new ExpressionException(e));
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

	protected ISalaryCalculatorContext getPaymentCalculatorContext(
			Connection conn, Date startDate, Date endDate, Date issueDate,
			Criteria criteria, final double x) {
		SQLContractSalaryCalculatorContext ctx;
		try {
			ctx = new SQLContractSalaryCalculatorContext(connection, startDate,
					endDate, issueDate, criteria) {

				@Override
				public double getIrpf() {
					return 0.00;
				}

				@Override
				public Object gross(double gross, Date start, Date end)
						throws ExpressionException, SQLException {
					return x;
				}

				@Override
				public Object liquid(double liquid, Date start, Date end)
						throws ExpressionException, SQLException,
						SalaryException {
					throw new InterruptedException(
							String.format("Lo sentimos. La funci\u00F3n BRUTO es incompatible con la funci\u00F3n NETO. Elija una de las dos. :-("));
				}

				@Override
				protected ISQLContractSalaryCalculatorContext getNoItCalculatorContext(
						Connection conn, Date startDate, Date endDate,
						Date issueDate, Criteria criteria, int start, int end) {
					SQLContractSalaryCalculatorContext ctx;
					try {
						ctx = new SQLNoItContractSalaryCalculatorContext(conn,
								startDate, endDate, issueDate, criteria, start,
								end) {

							@Override
							public double getIrpf() {
								return 0.00;
							}

							@Override
							public Object gross(double gross, Date start,
									Date end) throws ExpressionException,
									SQLException {
								return x;
							}

							public Object liquid(double liquid)
									throws ExpressionException, SQLException,
									SalaryException {
								throw new InterruptedException(
										String.format("Lo sentimos. La funci\u00F3n BRUTO es incompatible con la funci\u00F3n NETO. Elija una de las dos. :-("));
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
				public Collection<IContractDeduction> getContractDeductions()
						throws AonException {
					return Collections.emptyList();
				}

				@Override
				public Collection<IContractEmbargo> getContractEmbargos()
						throws AonException {
					return Collections.emptyList();
				}

				@Override
				public Collection<IContractBonus> getContractBonus()
						throws AonException {
					return Collections.emptyList();
				}

				@Override
				public Collection<IContractCost> getContractCosts()
						throws AonException {
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
		List<ITimedVariable<Object>> vars = getExpressionContext()
				.getVariables(PAYMENT_VARIABLE);
		if (vars == null || vars.isEmpty())
			throw new ExpressionExceptionWrapper(
					new UndefinedVariablesException(PAYMENT_VARIABLE));
		
		String name  = ((PaymentVariable)vars.get(0)).payment.getName();
		if ( AonStringUtils.isEmpty(name))
			throw new ExpressionExceptionWrapper(
					new UndefinedVariablesException(PAYMENT_VARIABLE));
		
		Double total = 0.00;
		for (IContractPayment payment : getAgreementPayments()) {
			List<ITimedResult<Double>> results = getExpressionContext().eval(payment.getExpression(), 
					payment.getStartDate(), 
					payment.getEndDate(),
					Double.class);
			for (ITimedResult<Double> result : results)
				total += result.getValue();
		}
		return total;
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

	@Override
	public double getIrpf() {
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(startDate);
		endCalendar.set(Calendar.DAY_OF_YEAR,
				endCalendar.getActualMaximum(Calendar.DAY_OF_YEAR));
		Date endYear = endCalendar.getTime();

		Criteria contractCriteria = getContractCriteria();

		IIrpfCalculatorContext irpfCalculatorContext = getIrpfCalculatorContext(
				connection, startDate, endYear, contractCriteria);

		IrpfOutcome irpfOutcome = IrpfCalculator
				.calculateIrpf(irpfCalculatorContext);

		onIrpf(irpfOutcome);

		return irpfOutcome.getIrpfResult().getIrpf();
	}
	
	protected Criteria getContractCriteria(){
		Criteria contractCriteria = new Criteria();
		contractCriteria.addExpression(criteria.getExpression());
		contractCriteria.addEqualExpression(SQLConstants.CONTRACT + "."
				+ ContractColumns.ID, getId());
		return contractCriteria;
	}
	
	protected IIrpfCalculatorContext getIrpfCalculatorContext(Connection conn,
			Date startDate, Date endDate, Criteria criteria) {
		try {
			ISQLContractSalaryCalculatorContext ctx = getUnderlyingIrpfSQLCalculatorContext(
					conn, startDate, endDate, criteria);
			return getIrpfCalculatorContext(conn, startDate, endDate, ctx);
		} catch (SQLException e) {
			throw new ExpressionExceptionWrapper(new ExpressionException(e));
		} catch (ExpressionException e) {
			throw new ExpressionExceptionWrapper(e);
		}

	}


	protected SQLContractSalaryCalculatorContext getUnderlyingIrpfSQLCalculatorContext(
			Connection conn, Date startDate, Date endDate, Criteria criteria)
			throws SQLException, ExpressionException {
		
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				conn, startDate, endDate, issueDate, criteria) {
			@Override
			public double getIrpf() {
				return 0.00;
			}
		};
		ctx.leaveLoader = leaveLoader;
		return ctx;
	}

	protected IIrpfCalculatorContext getIrpfCalculatorContext(Connection conn,
			Date startDate, Date endDate, ISQLContractSalaryCalculatorContext ctx) {
		try {
			return new SQLIrpfCalculatorContext(connection, startDate, endDate,
					ctx) {
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
			@Override
			public double getIrpf() {
				return 0.00;
				// TODO: sure
			}

			@Override
			protected double getDaySalary() throws ExpressionException,
					SQLException, SalaryException {
				throw new CheckException(
						"Imposible calcular el salario regulador de la indemnizaci\u00F3n por despido");
			}

			@Override
			public Collection<IContractBonus> getContractBonus()
					throws AonException {
				return Collections.emptyList();
			}

			@Override
			public Collection<IContractCost> getContractCosts()
					throws AonException {
				return Collections.emptyList();
			}

			@Override
			public Collection<IContractEmbargo> getContractEmbargos()
					throws AonException {
				return Collections.emptyList();
			}

			@Override
			public Collection<IContractDeduction> getContractDeductions()
					throws AonException {
				return Collections.emptyList();
			}

			@Override
			public void loadContractLeave(Integer id, Date leaveStart,
					Date leaveEnd, long parentDays, LeaveType type,
					Double dailyRegBase, ExpressionContext exprCtx)
					throws ExpressionException {
			}

			@Override
			public void loadContractLeave(Integer id, Date leaveStart,
					Date leaveEnd, long parentDays, LeaveType type,
					String dailyRegBase, ExpressionContext exprCtx)
					throws ExpressionException {
			}
		};

		ctx.next();

		SalaryBuilder salaryBuilder = new SalaryBuilder();
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(salaryBuilder);
		Salary salary = calculator.calculate(ctx);

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
											// cachondeo \BF OTHER,
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

	protected double getWorkDays(ExpressionContext ctx, Period p) {

		Long availableDays = getAvailableDays(
				Period.max(p.getStart(), contractStartDate),
				Period.min(p.getEnd(), contractEndDate));

		Long leaveDays = getLeaveDays(p);

		double workedDays = availableDays - leaveDays;
		workedDays -= getCurrentBindings().get(ERE_DAYS,
				obj -> ((Number) obj).doubleValue(), 0.00);
		workedDays -= getCurrentBindings().get(STRIKE_DAYS,
				obj -> ((Number) obj).doubleValue(), 0.00);

		double monthDays = getMax(p.getStart(), DAY_OF_MONTH);
		double ctxMonthDays = getContexVariable(ctx, p, MONTH_DAYS);

		return workedDays * ctxMonthDays / monthDays;

	}

	protected double getQuoteDays(ExpressionContext ctx, Period p) {

		Long quoteDays = days(Period.max(p.getStart(), contractStartDate),
				Period.min(p.getEnd(), contractEndDate));
		double monthDays = getMax(p.getStart(), DAY_OF_MONTH);
		double ctxMonthDays = getContexVariable(ctx, p, MONTH_DAYS);
		return quoteDays * ctxMonthDays / monthDays;

	}

	private double getSalaryDays(ExpressionContext ctx, Period p) {
		Long availableDays = getAvailableDays(p.getStart(), p.getEnd());
		double monthDays = getMax(p.getStart(), DAY_OF_MONTH);
		double ctxMonthDays = getContexVariable(ctx, p, MONTH_DAYS);
		return availableDays * ctxMonthDays / monthDays;
	}

	private int getSeniorityYears(Period period) {
		Date start = getSeniorityDate();
		Date end = period.getStart();
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

	public Object br(Date date) throws ExpressionException, SQLException,
			SalaryException {

		int contractId = getId();

		Date prevMonth = getLastDayOfMonth(add(date, Calendar.MONTH, -1));

		Stream<com.esferalia.aon.occam.api.model.Salary> salaries = AON
				.getSalaries(
						new AONContext(connection),
						p -> p.getIsSalaryProperty().eq(true)
								.and(p.getContractProperty().eq(contractId))
								.and(p.getStartDateProperty().le(prevMonth))
								.and(p.getEndDateProperty().ge(prevMonth)));
		double br = salaries
				.collect(Collectors.summingDouble(s -> s
						.getCommonContingenciesBase()
						/ (s.getSalaryDays()
								* ifnull(
										s.getContextData(
												MONTH_DAYS.getName(),
												summingDouble(Double::parseDouble)),
										(double) getMax(s.getStartDate(),
												DAY_OF_MONTH)) / getMax(
									s.getStartDate(), DAY_OF_MONTH))));
		salaries.close();
		if (br > 0.00)
			return br;

		// If no salaries are present, the result is 0.

		SQLNoItContractSalaryCalculatorContext ctx = (SQLNoItContractSalaryCalculatorContext) getNoItSalary(
				connection, date, SalaryType.SALARY, contractId);
		Salary salary = new ContractSalaryCalculator<Salary>(
				new SalaryBuilder()).calculate(ctx);

		return salary.getCommonBase()
				/ ctx.getVariable(QUOTE_DAYS, Double.class);

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
				* weekHours.doubleValue() / 7); // TODO : \BF Se redondean las
												// horas hacia arriba ?
	}

	private boolean isIndefinite() {
		String tc2 = getCurrentBindings().get(TC2, obj -> obj.toString());
		if (tc2 == null) {
			throw new ExpressionExceptionWrapper(
					new UndefinedVariablesException(TC2.getName()));
		}
		return ("123".indexOf(tc2.charAt(0)) != -1);
	}

	private boolean isFullTime() {
		String tc2 = getCurrentBindings().get(TC2, obj -> obj.toString());
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

	private double getDoubleVariable(String name, Date start, Date end) {
		Double value = this.contractExpressionContext.getVariable(name, start,
				end, Double.class);
		return value != null ? value : 0.00;
	}

	private double getDayDoubleVariable(String name, Date start, Date end) {
		List<ITimedVariable<Double>> vars = this.contractExpressionContext
				.getVariables(name);

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
		return this.contractExpressionContext.containsVariable(name,
				this.contractStartDate, this.contractEndDate);
	}

	private boolean containsVariable(Object name, Period p) {
		return this.contractExpressionContext.containsVariable(name,
				p.getStart(), p.getEnd());
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

	protected ITimedVariable<Number> getExtraDays(
			ITimedVariable<Number> monthDays) {
		return new ExtraDays(monthDays);
	}

	/*
	 * Inicializa el contexto dentro del cual se calcular\E1n ejecutar\E1n las
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

		ActiveTimedVariable<Double> cgcBase = new ActiveTimedVariable<Double>() {
			@Override
			public Double getValue(Period p) {
				throw new ExpressionExceptionWrapper(
						new UndefinedTotalPaymentException());
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

		ActiveTimedVariable<Double> bonusDays = new ActiveTimedVariable<Double>() {
			@Override
			public Double getValue(Period p) {
				Date bonusStart = Period.max(sqlContractBonus.getStartDate(),p.getStart());
				Date bonusEnd = Period.min(sqlContractBonus.getEndDate(),p.getEnd());
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

		this.implicitExpressionContext = new ExpressionContext(agreementCtx,
				this) {
			@Override
			public void putVariable(final Object name,
					ITimedVariable<?> implicitVariable) {
				if (!isDef(name)) {
					super.putVariable(name, implicitVariable);
				} else {

					final ITimedVariable<?> redefinedVariable = getVariable(
							name, implicitVariable.getPeriod().getStart(),
							implicitVariable.getPeriod().getStart());
					super.putVariable(name, new IExpressionVariable() {
						@Override
						public Map<String, ITimedVariable<?>> getContext() {
							return Collections.emptyMap();
						}

						@Override
						public IExpression getExpression() {
							return new IExpression() {

								@Override
								public boolean isReadOnly() {
									return false;
								}

								@Override
								public ExpressionScope getScope() {
									return ExpressionScope.AGREEMENT;
								}

								@Override
								public String getName() {
									return name.toString();
								}

								@Override
								public String getExpression() {
									return null;
								}
							};
						}

						@Override
						public Period getPeriod() {
							return redefinedVariable.getPeriod();
						}

						@Override
						public Object getValue(Period period) {
							if (SQLContractSalaryCalculatorContext.this.listener != null)
								SQLContractSalaryCalculatorContext.this.listener
										.onRedefinedImplicit(name.toString(),
												redefinedVariable,
												implicitVariable);

							return redefinedVariable.getValue(period);
						};

					});
				}

			}

		};

		// TODO: Tiene que ir aqui ???
		SalaryType salaryType = getSalaryType();
		this.implicitExpressionContext.setVariable(SALARY,
				salaryType == SalaryType.SALARY, startDate, endDate);
		this.implicitExpressionContext.setVariable(SETTLE,
				salaryType == SalaryType.SETTLE, startDate, endDate);
		this.implicitExpressionContext.setVariable(DELAY,
				salaryType == SalaryType.DELAY, startDate, endDate);
		this.implicitExpressionContext.setVariable(EXTRA_PAY,
				salaryType == SalaryType.EXTRA, startDate, endDate);

		this.implicitExpressionContext.setVariable(CONTRACT_START,
				getDate(SQLConstants.CONTRACT, ContractColumns.START_DATE),
				startDate, endDate);
		this.implicitExpressionContext.setVariable(
				CONTRACT_END,
				salaryType == SalaryType.SETTLE ? contractEndDate : getDate(
						SQLConstants.CONTRACT, ContractColumns.END_DATE),
				startDate, endDate);

		this.implicitExpressionContext.putVariable(IRPF_PERCENT, irpf);

		this.implicitExpressionContext.putVariable(START, start);
		this.implicitExpressionContext.putVariable(END, end);

		this.implicitExpressionContext.putVariable(CGC_BASE, cgcBase);

		this.implicitExpressionContext.putVariable(SENIORITY,
				new ActiveTimedVariable<Integer>() {
					@Override
					public Integer getValue(Period period) {
						return getSeniorityYears(period);
					}
				});

		this.implicitExpressionContext.putVariable(SALARY_HOURS,
				new LazyTimedVariable<Double>() {
					@Override
					public Double create() {
						return getSalaryHours();
					}
				});
		this.implicitExpressionContext.putVariable(BONUS_DAYS, bonusDays);

		this.implicitExpressionContext.putVariable(INDEFINITE,
				new LazyTimedVariable<Boolean>() {
					@Override
					public Boolean create() {
						return isIndefinite();
					}
				});

		this.implicitExpressionContext.putVariable(FULL_TIME,
				new LazyTimedVariable<Boolean>() {
					@Override
					public Boolean create() {
						return isFullTime();
					}
				});

		this.implicitExpressionContext.putVariable(ASSIMILATED,
				new LazyTimedVariable<Boolean>() {
					@Override
					public Boolean create() {
						return isAssimilatted();
					}
				});

		this.implicitExpressionContext.putVariable(MORE_THAN_65,
				new LazyTimedVariable<Boolean>() {
					@Override
					public Boolean create() {
						return false;
					}
				});

		this.implicitExpressionContext.putVariable(SHORT_CONTRACT,
				new LazyTimedVariable<Boolean>() {
					@Override
					public Boolean create() {
						return isShortContract();
					}
				});

		this.implicitExpressionContext.putVariable(IT_RATE,
				new LazyTimedVariable<Double>() {
					@Override
					public Double create() {
						return getItRate();
					}
				});

		this.implicitExpressionContext.putVariable(IMS_RATE,
				new LazyTimedVariable<Double>() {
					@Override
					public Double create() {
						return getImsRate();
					}
				});

		this.implicitExpressionContext.putVariable(BONUS_AGE,
				new LazyTimedVariable<Integer>() {
					@Override
					public Integer create() {
						return getYears(sqlContractBonus.getStartDate(),
								contractStartDate);
					}
				});

		this.implicitExpressionContext.putVariable(BONUS_START,
				new LazyTimedVariable<String>() {
					@Override
					public String create() {
						return DATE_FORMAT.format(sqlContractBonus
								.getStartDate());
					}
				});

		this.implicitExpressionContext.putVariable(WORKED_YEARS,
				new ActiveTimedExpressionVariable<Double>(WORKED_YEARS.name(),
						ExpressionScope.CONTRACT) {
					@Override
					public Double getValue(Period period) {
						return getWorkedYears(period.getStart(),
								period.getEnd());
					}
				});
		this.implicitExpressionContext.putVariable("DIAS_PREAVISO",
				new LazyTimedExpressionVariable<Double>("DIAS_PREAVISO",
						ExpressionScope.CONTRACT) {
					@Override
					public Double create() {
						return getAdvanceNoticeDays();
					}
				});

		this.implicitExpressionContext.putVariable("SALARIO_VARIABLE_DIA",
				new LazyTimedExpressionVariable<Double>("SALARIO_VARIABLE_DIA",
						ExpressionScope.CONTRACT) {
					@Override
					public Double create() {
						return getDayVarSalary();
					}
				});

		// TODO: Sure ???
		if (getSalaryType() == SalaryType.SETTLE)
			this.implicitExpressionContext.putVariable("SALARIO_DIA",
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

		/*
		 * this.implicitExpressionContext.addVariable(COMPENSATION_DAYS, new
		 * LazyTimedVariable<Double>() {
		 * 
		 * @Override public Double create() { return getCompensationDays(); }
		 * });
		 */

		this.contractExpressionContext = new ExpressionContext(
				this.implicitExpressionContext, this);

		this.contractExpressionContext.setVariable(CONTEXT,
				contractExpressionContext, startDate, endDate);
		this.contractExpressionContext.setVariable(SELF, this, startDate,
				endDate);

		// TODO: at implicitExpressionContext ?
		loadExpression(this.contractExpressionContext, BR,
				"def(x){ SELF.br(x)};", this.startDate, this.endDate);
		loadExpression(this.contractExpressionContext, GROSS, String.format(
				"def(x){ %s.gross(x, %s, %s )};", SELF, START, END),
				this.startDate, this.endDate);
		loadExpression(this.contractExpressionContext, LIQUID, String.format(
				"def(x){ %s.liquid(x, %s, %s )};", SELF, START, END),
				this.startDate, this.endDate);
		loadExpression(this.contractExpressionContext, SYSTEM,
				"def(x){ SELF.system(x)};", this.startDate, this.endDate);
		loadExpression(this.contractExpressionContext, AGREEMENT,
				"def(x){ SELF.agreement(x)};", this.startDate, this.endDate);

		loadContractLeave(this.contractExpressionContext);
		loadContractData(this.contractExpressionContext);
		loadPersonData(this.contractExpressionContext);

		try {
			Method guarantee = SQLContractSalaryCalculatorContext.class
					.getMethod("guaranteee", double.class);
			this.contractExpressionContext.setVariable(GUARANTEE,
					new MethodStub(guarantee), this.startDate, this.endDate);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (!containsVariable(ACTUAL_DAYS)) {
			// Los 'DIAS_EFECTIVOS' son pesados de calcular ( necesitan de
			// querys adicionales...)
			this.contractExpressionContext.putVariable(ACTUAL_DAYS,
					new LazyTimedVariable<Double>() {
						@Override
						public Double create() {
							return getActualDays();
						}
					});
		}
		// --------------------------------------------------------------------
		// WEEK_HOURS, WORKED_DAYS and so on. These variables
		//
		loadDaysContextVariables(contractExpressionContext);
	}

	private void loadDaysContextVariables(ExpressionContext ctx)
			throws ExpressionException {

		if (!containsVariable(WEEK_HOURS)) {

			List<Period> contract = getMonths(contractStartDate,
					contractEndDate);
			ContextVariable[] week_days = { MONDAY_HOURS, TUESDAY_HOURS,
					WEDNESDAY_HOURS, THURSDAY_HOURS, FRIDAY_HOURS,
					SATURDAY_HOURS, SUNDAY_HOURS };

			List<Period> intersects = Arrays.stream(week_days)
					.map(var -> ctx.getPeriods(var))
					.filter(periods -> periods != null && !periods.isEmpty())
					.reduce(contract, (a, b) -> Period.intersect(a, b));
			for (Period period : intersects) {
				ITimedVariable<Double> weeks_hours = new ITimedVariable<Double>() {
					@Override
					public Period getPeriod() {
						return period;
					}

					@Override
					public Double getValue(Period p) {
						return Arrays
								.stream(week_days)
								.collect(
										Collectors
												.summingDouble((var -> getCurrentBindings()
														.get(var,
																obj -> ((Number) obj)
																		.doubleValue(),
																Arrays.asList(
																		SATURDAY_HOURS,
																		SUNDAY_HOURS)
																		.contains(
																				var) ? 0.00
																		: DEFAULT_DAY_HOURS))));
					}

				};

				ctx.putVariable(WEEK_HOURS, weeks_hours);
			}

		}

		List<Period> contract = getMonths(contractStartDate, contractEndDate);
		List<Period> weekHours = ctx.getPeriods(WEEK_HOURS);
		List<Period> agreeementHours = ctx.getPeriods(AGREEMENT_HOURS);

		List<Period> intersects = Period.intersect(weekHours, agreeementHours);
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
							if (!isFullTime())
								return getCurrentBindings().get(WEEK_HOURS,
										obj -> ((Number) obj).doubleValue(),
										DEFAULT_AGRREEMENT_HOURS)
										/ getCurrentBindings().get(
												AGREEMENT_HOURS,
												obj -> ((Number) obj)
														.doubleValue(),
												DEFAULT_AGRREEMENT_HOURS);
						} catch (ExpressionExceptionWrapper e) {
						}
						return 1.00;
					}

				};

				ctx.putVariable(PARTIAL_FACTOR, partial_factor);
			}

			if (!containsVariable(WORKED_DAYS, period)) {
				ITimedVariable<Double> workedDays = new ITimedVariable<Double>() {
					@Override
					public Period getPeriod() {
						return period;
					}

					@Override
					public Double getValue(Period p) {
						double workDays = getWorkDays(ctx, p);
						try {
							if (!isFullTime()) {
								return workDays
										* getCurrentBindings().get(
												PARTIAL_FACTOR,
												obj -> ((Number) obj)
														.doubleValue(), 1.00);
							}
						} catch (ExpressionExceptionWrapper e) {
						}
						return workDays;
					}

				};
				ctx.putVariable(WORKED_DAYS, workedDays);
			} else {
			}
			if (!containsVariable(QUOTE_DAYS, period)) {
				ITimedVariable<Double> quoteDays = new ITimedVariable<Double>() {
					@Override
					public Period getPeriod() {
						return period;
					}

					@Override
					public Double getValue(Period p) {
						return getQuoteDays(ctx, p);
					}

				};
				ctx.putVariable(QUOTE_DAYS, quoteDays);
			}
			if (!containsVariable(SALARY_DAYS, period)) {
				ITimedVariable<Double> salaryDays = new ITimedVariable<Double>() {
					@Override
					public Period getPeriod() {
						return period;
					}

					@Override
					public Double getValue(Period p) {
						return getSalaryDays(ctx, p);
					}

				};
				ctx.putVariable(SALARY_DAYS, salaryDays);
			}

		}
	}

	private double getContexVariable(ExpressionContext ctx, Period p,
			ContextVariable var) {
		ITimedVariable<?> agreementHours = ctx.getVariable(var, p.getStart(),
				p.getEnd());
		if (agreementHours == null)
			throw new ExpressionExceptionWrapper(
					new UndefinedContextVariablesException(var));
		return ((Number) agreementHours.getValue(p)).doubleValue();
	}

	/*
	 * Carga, ejecuta los datos del contrato 'contract_data' para este periodo.
	 * Ejecuta porque al valor de una variable no tiene porque ser un literal,
	 * puede ser una expresi\F3n ej : '15 / 100' o 'DIAS_TRABAJADOS * 0.01'
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
			// TODO: \BF Que hacemos con esta excepcion ?
		}
	}

	/*
	 * Carga, ejecuta los datos del contrato 'contract_data' para este periodo.
	 * Ejecuta porque al valor de una variable no tiene porque ser un literal,
	 * puede ser una expresi\F3n ej : '15 / 100' o 'DIAS_TRABAJADOS * 0.01'
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
					// TODO: \BF Que hacemos con esta excepcion ?
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

		ctx.setVariable(MALE, Gender.MALE.ordinal(), contractStartDate,
				contractEndDate);
		ctx.setVariable(FEMALE, Gender.FEMALE.ordinal(), contractStartDate,
				contractEndDate);
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

	private void loadContractLeave(ExpressionContext ctx) throws SQLException,
			ExpressionException {
		ResultSet rs = null;
		try {
			cleaveStmt.setInt(1, getId());
			rs = cleaveStmt.executeQuery();
			leaveLoader.loadContractLeave(rs, ctx);
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
			java.sql.Date sqlEndDate = new java.sql.Date(this.endDate.getTime());
			java.sql.Date sqlStartDate = new java.sql.Date(
					this.startDate.getTime());
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
			String sql = CriteriaUtilities.toSQLString(paymentsCriteria,
					SYSTEM_PAYMENT_SQL);
			stmt = connection.prepareStatement(sql);
			java.sql.Date sqlEndDate = new java.sql.Date(this.endDate.getTime());
			java.sql.Date sqlStartDate = new java.sql.Date(
					this.startDate.getTime());
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
		return cccExpressionContexts.get(new CCCContextKey(getCCCType(),
				getSSRegime()));
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

	protected static ISalary getSalary(Connection connection, Date date,
			SalaryType type, Integer contractID) throws SQLException,
			ExpressionException, SalaryException {
		Date startDate = CommonUtil.getMonthFirstDay(date);

		// Se calcula un dia anterior a la fecha de baja.
		Date endDate = prev(date);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(SQLConstants.CONTRACT + "."
				+ ContractColumns.ID, contractID);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria) {
			@Override
			public Collection<IContractDeduction> getContractDeductions()
					throws AonException {
				return Collections.emptyList();
			}

			@Override
			public Collection<IContractEmbargo> getContractEmbargos()
					throws AonException {
				return Collections.emptyList();
			}

			@Override
			public Collection<IContractCost> getContractCosts()
					throws AonException {
				// TODO Ap\E9ndice de m\E9todo generado autom\E1ticamente
				return Collections.emptyList();
			}

			@Override
			public Collection<IContractBonus> getContractBonus()
					throws AonException {
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

	public static Salary getDbSalary(Connection connection, Date date,
			SalaryType type, Integer contractID) throws SQLException {

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
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return calendar.getTime();
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
