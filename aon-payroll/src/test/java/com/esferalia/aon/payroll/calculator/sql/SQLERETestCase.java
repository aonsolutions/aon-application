package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_MAX;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_MIN;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE_MAX;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE_MIN;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_DAYS;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getMax;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;

import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;
import com.esferalia.aon.watson.util.AonDateUtils;

import junit.framework.Assert;

public class SQLERETestCase extends AbstractSQLTestCase {

	protected static final double DELTA = 0.000001;

	
	@Test
	public void testEREI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), Collections.emptyMap());

		Date ereDay = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH, 10);

		addData(aonContext, contract, ereDay, ereDay,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1");
					}
				});

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		List<ITimedResult<Double>> workDays = ctx.getExpressionContext().eval(
				"DIAS_TRABAJADOS", startDate, endDate, Double.class);

		Assert.assertEquals(2, workDays.size());

		Assert.assertEquals((double)get(ereDay, DAY_OF_MONTH)-1, workDays.get(0).getValue());
		Assert.assertEquals(
				workDays.get(0).getPeriod(),
				new Period(getFirstDayOfMonth(ereDay), add(ereDay,
						DAY_OF_MONTH, -1)));

		Assert.assertEquals(
				workDays.get(1).getValue(),
				(double) (getMax(ereDay, DAY_OF_MONTH) - get(ereDay,
						DAY_OF_MONTH)));
		Assert.assertEquals(workDays.get(1).getPeriod(),
				new Period(add(ereDay, DAY_OF_MONTH, +1),
						getLastDayOfMonth(getToday())));

	}

	@Test
	public void testEREII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), Collections.emptyMap());

		Date startEre = getFirstDayOfMonth(getToday());
		int ereDays = (int) (Math.random() * (getMax(getToday(), DAY_OF_MONTH) - 1));
		Date endEre = add(startEre, DAY_OF_MONTH, ereDays);

		addData(aonContext, contract, startEre, endEre,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1");
					}
				});

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		List<ITimedResult<Double>> workDays = ctx.getExpressionContext().eval(
				"DIAS_TRABAJADOS", startDate, endDate, Double.class);

		Assert.assertEquals(1, workDays.size());

		Assert.assertEquals(
				workDays.get(0).getValue(),
				(double) (getMax(getToday(), DAY_OF_MONTH) - get(endEre,
						DAY_OF_MONTH)));
		Assert.assertEquals(workDays.get(0).getPeriod(),
				new Period(add(endEre, DAY_OF_MONTH, +1),
						getLastDayOfMonth(getToday())));

	}

	@Test
	public void testEREIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), Collections.emptyMap());

		Date start = getFirstDayOfMonth(getToday());
		for (int i = 1; i <= 10; i++) {
			Date date = add(start, DAY_OF_MONTH, i * 2);
			addData(aonContext, contract, date, date,
					new HashMap<String, String>() {
						{
							put(getFactorVariable().getName(), "1");
						}
					});
		}

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		List<ITimedResult<Double>> workDays = ctx.getExpressionContext().eval(
				"DIAS_TRABAJADOS", startDate, endDate, Double.class);

		Assert.assertEquals(11, workDays.size());

		Assert.assertEquals(2.00, workDays.get(0).getValue());
		Assert.assertEquals(workDays.get(0).getPeriod(),
				new Period(start, add(start, DAY_OF_MONTH, 1)));

		for (int i = 1; i < 10; i++) {
			Date date = add(start, DAY_OF_MONTH, i * 2 + 1);
			Assert.assertEquals(1.00, workDays.get(i).getValue());
			Assert.assertEquals(workDays.get(i).getPeriod(), new Period(date,
					date));
		}

		Assert.assertEquals((double) (getMax(getToday(), DAY_OF_MONTH) - 21),
				workDays.get(10).getValue());
		Assert.assertEquals(workDays.get(10).getPeriod(),
				new Period(add(start, DAY_OF_MONTH, 21),
						getLastDayOfMonth(getToday())));

	}

	@Test
	public void testEREIV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), Collections.emptyMap());

		Date startDate = getFirstDayOfMonth(getToday());

		addData(aonContext, contract, startDate, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1");
					}
				});

		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		try {
			List<ITimedResult<Double>> workDays = ctx.getExpressionContext()
					.eval("DIAS_TRABAJADOS", startDate, endDate, Double.class);
			Assert.fail();
		} catch (UndefinedVariablesException e) {
			Assert.assertEquals("DIAS_TRABAJADOS", e.getVariableNames()[0]);
			return;
		}
		Assert.fail();

	}

	@Test
	public void testEREV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), Collections.emptyMap());

		Date startDate = getFirstDayOfMonth(getToday());

		addData(aonContext, contract, startDate, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.75");
					}
				});

		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		List<ITimedResult<Double>> workDays = ctx.getExpressionContext().eval(
				"DIAS_TRABAJADOS", startDate, endDate, Double.class);

		Assert.assertEquals(1, workDays.size());
		Assert.assertEquals(workDays.get(0).getPeriod(), new Period(startDate,
				endDate));
		Assert.assertEquals(workDays.get(0).getValue(),
				get(endDate, DAY_OF_MONTH) * 0.25);

	}

	@Test
	public void testEREVI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), Collections.emptyMap());

		Date startEre = getFirstDayOfMonth(getToday());
		int ereDays = (int) (Math.random() * (getMax(getToday(), DAY_OF_MONTH) - 1));
		Date endEre = add(startEre, DAY_OF_MONTH, ereDays);

		addData(aonContext, contract, startEre, endEre,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.60");
					}
				});

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		List<ITimedResult<Double>> workDays = ctx.getExpressionContext().eval(
				"DIAS_TRABAJADOS", startDate, endDate, Double.class);

		Assert.assertEquals(2, workDays.size());

		Assert.assertEquals((double) (get(endEre, DAY_OF_MONTH)) * 0.40,
				workDays.get(0).getValue());
		Assert.assertEquals(new Period(startDate, endEre), workDays.get(0)
				.getPeriod());

		Assert.assertEquals(
				(double) (getMax(getToday(), DAY_OF_MONTH) - get(endEre,
						DAY_OF_MONTH)), workDays.get(1).getValue());
		Assert.assertEquals(new Period(add(endEre, DAY_OF_MONTH, +1),
				getLastDayOfMonth(getToday())), workDays.get(1).getPeriod());

	}

	@Test
	public void testEREVII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), Collections.emptyMap());

		Date start = getFirstDayOfMonth(getToday());
		for (int i = 1; i <= 10; i++) {
			Date date = add(start, DAY_OF_MONTH, i * 2);
			addData(aonContext, contract, date, date,
					new HashMap<String, String>() {
						{
							put(getFactorVariable().getName(), "0.3");
						}
					});
		}

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		List<ITimedResult<Double>> workDays = ctx.getExpressionContext().eval(
				"DIAS_TRABAJADOS", startDate, endDate, Double.class);

		Assert.assertEquals(11 + 10, workDays.size());

		Assert.assertEquals(2.00, workDays.get(0).getValue());
		Assert.assertEquals(workDays.get(0).getPeriod(),
				new Period(start, add(start, DAY_OF_MONTH, 1)));

		Date date = add(start, DAY_OF_MONTH, 1 * 2);
		Assert.assertEquals(1.00 * 0.70, workDays.get(1).getValue());
		Assert.assertEquals(workDays.get(1).getPeriod(), new Period(date, date));
		date = add(start, DAY_OF_MONTH, 1 * 2 + 1);
		Assert.assertEquals(1.00, workDays.get(1 + 1).getValue());
		Assert.assertEquals(workDays.get(1 + 1).getPeriod(), new Period(date,
				date));

		date = add(start, DAY_OF_MONTH, 2 * 2);
		Assert.assertEquals(1.00 * 0.70, workDays.get(3).getValue());
		Assert.assertEquals(workDays.get(3).getPeriod(), new Period(date, date));
		date = add(start, DAY_OF_MONTH, 2 * 2 + 1);
		Assert.assertEquals(1.00, workDays.get(3 + 1).getValue());
		Assert.assertEquals(workDays.get(3 + 1).getPeriod(), new Period(date,
				date));

		date = add(start, DAY_OF_MONTH, 3 * 2);
		Assert.assertEquals(1.00 * 0.70, workDays.get(5).getValue());
		Assert.assertEquals(workDays.get(5).getPeriod(), new Period(date, date));
		date = add(start, DAY_OF_MONTH, 3 * 2 + 1);
		Assert.assertEquals(1.00, workDays.get(5 + 1).getValue());
		Assert.assertEquals(workDays.get(5 + 1).getPeriod(), new Period(date,
				date));

		// ....

		Assert.assertEquals((double) (getMax(getToday(), DAY_OF_MONTH) - 21),
				workDays.get(20).getValue());
		Assert.assertEquals(workDays.get(20).getPeriod(),
				new Period(add(start, DAY_OF_MONTH, 21),
						getLastDayOfMonth(getToday())));

	}

	@Test
	public void testEREVIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put(CGC_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGP_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
						"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05"}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startEre = getFirstDayOfMonth(getToday());
		int ereDays = (int) (0.25/*Math.random()*/ * (getMax(getToday(), DAY_OF_MONTH) - 1));
		Date endEre = add(startEre, DAY_OF_MONTH, ereDays - 1);

		addData(aonContext, contract, startEre, endEre,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext, getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA",getDaysVariable().getName()));
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getQuote() + ")");
		}

		Assert.assertEquals(
				(1750.00 * 1.10) * (get(endDate, DAY_OF_MONTH) - (ereDays))
						/ get(endDate, DAY_OF_MONTH), salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals(
				(1750.00 * 1.10) , salary.getCommonBase(),
				DELTA);

		Assert.assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);
	}

	@Test
	public void testEREIX() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put(CGC_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGP_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
						"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05"}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startEre = getFirstDayOfMonth(getToday());
		//int ereDays = (int) (Math.random() * (getMax(getToday(), DAY_OF_MONTH) - 1));
		//Date endEre = add(startEre, DAY_OF_MONTH, ereDays - 1);

		addData(aonContext, contract, startEre, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.50");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext,getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA", getDaysVariable()));
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}

		Assert.assertEquals(
				(1750.00 * 1.10) * 0.50
						, salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals(
				(1750.00 * 1.10), salary.getCommonBase(),
				DELTA);

		Assert.assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);
	}
	
	@Test
	public void testEREX() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put(MONTH_DAYS.getName(), "30.00");
					put(CGC_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGP_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
						"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05"}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startEre = getFirstDayOfMonth(getToday());
		//int ereDays = (int) (Math.random() * (getMax(getToday(), DAY_OF_MONTH) - 1));
		//Date endEre = add(startEre, DAY_OF_MONTH, ereDays - 1);

		addData(aonContext, contract, startEre, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.50");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext,getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA", getDaysVariable()));
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ", " + payment.getQuote() + ")");
		}

		Assert.assertEquals(
				(1750.00 * 1.10), salary.getCommonBase(),
				DELTA);


		Assert.assertEquals(
				(1750.00 * 1.10) * 0.50
				, salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);
	}

	@Test
	public void testEREXI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put(MONTH_DAYS.getName(), "30.00");
					put(CGC_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGP_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
						"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05"}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startEre = getFirstDayOfMonth(getToday());
		//int ereDays = (int) (Math.random() * (getMax(getToday(), DAY_OF_MONTH) - 1));
		//Date endEre = add(startEre, DAY_OF_MONTH, ereDays - 1);

		addData(aonContext, contract, startEre, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1.00");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext,getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA", getDaysVariable()));
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ", " + payment.getQuote() + ")");
		}

		Assert.assertEquals(
				(1750.00 * 1.10), salary.getCommonBase(),
				DELTA);


		Assert.assertEquals(
				0.00
						, salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);
	}

	@Test
	public void testEREXII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put(MONTH_DAYS.getName(), "30.00");
					put(CGC_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGP_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
						"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05"}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startEre = getFirstDayOfMonth(getToday());
		startEre =add(startEre, Calendar.DAY_OF_MONTH, 9);
		//int ereDays = (int) (Math.random() * (getMax(getToday(), DAY_OF_MONTH) - 1));
		//Date endEre = add(startEre, DAY_OF_MONTH, ereDays - 1);

		addData(aonContext, contract, startEre, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1.00");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext,getEreVariable().getName());
		addPayment(aonContext, contract, ere, "TRACE('BASE_REGULADORA = %f\\r\\n', BASE_REGULADORA);0.00" , String.format("%s * BASE_REGULADORA", getDaysVariable()));
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ", " + payment.getQuote() + ")");
		}

		Assert.assertEquals(
				(1750.00 * 1.10), 
				salary.getCommonBase(),
				DELTA);


		Assert.assertEquals(
				(1750.00 * 1.10) * 9 / 30
				, salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);
	}

	@Test
	public void testEREXIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put(MONTH_DAYS.getName(), "30.00");
					put(CGC_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGP_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
						"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05"}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startEre = getFirstDayOfMonth(getToday());
		//int ereDays = (int) (Math.random() * (getMax(getToday(), DAY_OF_MONTH) - 1));
		startEre = add(startEre, DAY_OF_MONTH, 15);

		addData(aonContext, contract, startEre, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.50");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext,getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA", getDaysVariable()));
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ", " + payment.getQuote() + ")");
		}

		Assert.assertEquals(
				(1750.00 * 1.10), salary.getCommonBase(),
				DELTA);


		Assert.assertEquals(
				(1750.00 * 1.10) * 0.75
				, salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);
	}
	
	@Test
	public void testEREITI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put(MONTH_DAYS.getName(), "30.00");
					put(CGC_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGP_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
						"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05"}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startEre = getFirstDayOfMonth(getToday());
		//int ereDays = (int) (Math.random() * (getMax(getToday(), DAY_OF_MONTH) - 1));
		startEre = add(startEre, DAY_OF_MONTH, 15);
		
		addIT(
		aonContext, 
		contract, 
		LeaveType.COMMON_DISEASE, 
		getFirstDayOfMonth(getToday()), 
		add(startEre, DAY_OF_MONTH,-1), 
		null);
		//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_1_3",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.60 * %s_4_15",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.60 * %s_16_20",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s_21",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		//@formatter:on
		addData(aonContext, contract, startEre, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1.00");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext,getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA", getDaysVariable()));
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new ContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ", " + payment.getQuote() + ")");
		}

		Assert.assertEquals(
				(1750.00 * 1.10), salary.getCommonBase(),
				DELTA);


		Assert.assertEquals(
				1750.00 * 1.10 * 12/30 * 0.60
				, salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals(
				1750.00 * 1.10 * 0.50  * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);
	}
	
	@Test
	public void testEREITII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), 
			new HashMap<String, String>() {
				{
					put(MONTH_DAYS.getName(), "30.00");
					put(CGC_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGP_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
						"TRACE('DIAS_COTIZADOS = %f\r\n', DIAS_COTIZADOS); BASE_CGP * 0.05"}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));
		
		
		Date firstDayOfMarch = add(getFirstDayOfYear(getToday()), Calendar.MONTH, 2  );

		Date startEre = firstDayOfMarch;
		//int ereDays = (int) (Math.random() * (getMax(getToday(), DAY_OF_MONTH) - 1));
		startEre = add(startEre, DAY_OF_MONTH, 25);
		
		Date startIt = firstDayOfMarch;
		startIt = add(startIt, DAY_OF_MONTH, -12);
		Date endIt = add(startIt, DAY_OF_MONTH, 14);
		
		addIT(
		aonContext, 
		contract, 
		LeaveType.COMMON_DISEASE, 
		startIt, 
		endIt, 
		null);
		
		
		//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_1_3",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.60 * %s_4_15",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.60 * %s_16_20",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s_21",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		//@formatter:on
		addData(aonContext, contract, startEre, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1.00");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext,getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA", getDaysVariable()));
		

		Date startDate = firstDayOfMarch;
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new ContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ", " + payment.getQuote() + ")");
		}
		
	

		Assert.assertEquals(
				(1750.00 * 1.10), salary.getCommonBase(),
				DELTA);
		

//		Assert.assertEquals(
//				1750.00 * 1.10 * 12/30 * 0.60
//				, salary.getTotalPayment(),
//				DELTA);
//
//		Assert.assertEquals(
//				1750.00 * 1.10 * 0.50  * 0.15 
//				, salary.getSocialSecurityContributions(),
//				DELTA);
	}

	@Test
	public void testMultipleEREI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put(MONTH_DAYS.getName(), "30.00");
					put(CGC_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGP_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
						"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05"}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startEreI = getFirstDayOfMonth(getToday());
		//int ereDays = (int) (Math.random() * (getMax(getToday(), DAY_OF_MONTH) - 1));
		startEreI = add(startEreI, DAY_OF_MONTH, 15);
		Date endEreI = add(startEreI, DAY_OF_MONTH, 10);

		addData(aonContext, contract, startEreI, endEreI,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1.00");
					}
				});
		Date startEreII = add(endEreI, DAY_OF_MONTH, 1);
		
		addData(aonContext, contract, startEreII, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.50");
					}
				});

		PaymentConceptRecord ere = addConcept(aonContext,getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA", getDaysVariable()));
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ", " + payment.getQuote() + ")");
		}

		Assert.assertEquals(
				(1750.00 * 1.10), salary.getCommonBase(),
				DELTA);


		Assert.assertEquals(
				(1750.00 * 1.10) * 15/30 + (1750.00 * 1.10) * 4 / 30 *0.5
				, salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);
	}
	
	@Test
	public void testMultipleEREII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		int salarioBase = addConcept(aonContext, "SALARIO_BASE", PaymentType.CRA_0001).getId();
		int plusSalarial = addConcept(aonContext, "PLUS_SALARIAL", PaymentType.CRA_0001).getId();
		PaymentConceptRecord pagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext, new Extra[] { 
		new Extra() {
			{
				this.expression = "(SALARIO_BASE + PLUS_SALARIAL)/12";
				this.month = Month.JULY;
				this.start = "01/07 -1";
				this.end = "30/06";
				this.issue = "01/07";
				this.concept = pagaExtra.getId();
			}
		}, new Extra() {
			{
				this.expression = "(SALARIO_BASE + PLUS_SALARIAL)/12";
				this.month = Month.DECEMBER;
				this.start = "01/01";
				this.end = "31/12";
				this.issue = "15/12";
				this.concept = pagaExtra.getId();
			}
		}, 
		
		}, new Payment[] 
		{ 
		new Payment() {
			{
				this.concept = salarioBase;
				this.expression = "1500.00 * DIAS_TRABAJADOS / DIAS_MES";
			}
		}, 
		new Payment() {
			{
				this.concept = plusSalarial;
				this.expression = "250.00 * DIAS_TRABAJADOS / DIAS_MES";
			}
		}, 
//		new Payment() {
//			{
//				this.expression = "PLUS_TRANSPORTE";
//			}
//		} 
		});

		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put(MONTH_DAYS.getName(), "30.00");
//					put(CGC_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
//					put(CGP_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
//					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
//					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { 
						"( SALARIO_BASE + PLUS_SALARIAL )* 0.10 ",
//						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
//						"250.00 * DIAS_TRABAJADOS / DIAS_MES", 
			}
			, new String[] {
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
						"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05"
			}
			, category);
		
		addPayment(aonContext, contract, pagaExtra, "SALARIO_BASE + PLUS_SALARIAL", "_P", PaymentType.CRA_0004);
		addPayment(aonContext, contract, pagaExtra, "SALARIO_BASE + PLUS_SALARIAL", "_P", PaymentType.CRA_0004);

		Date startEreI = getFirstDayOfMonth(getToday());
		//int ereDays = (int) (Math.random() * (getMax(getToday(), DAY_OF_MONTH) - 1));
		startEreI = add(startEreI, DAY_OF_MONTH, 15);
		Date endEreI = add(startEreI, DAY_OF_MONTH, 10);

		addData(aonContext, contract, startEreI, endEreI,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1.00");
					}
				});
		Date startEreII = add(endEreI, DAY_OF_MONTH, 1);
		
		addData(aonContext, contract, startEreII, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.50");
					}
				});

		PaymentConceptRecord ere = addConcept(aonContext,getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA", getDaysVariable()));
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ", " + payment.getQuote() + ")");
		}

		Assert.assertEquals(
				(1750.00 * 1.10) + (1750.00/6), salary.getCommonBase(),
				DELTA);


		Assert.assertEquals(
				(1750.00 * 1.10 * 15/30) + (1750.00 * 1.10 * 4 / 30 *0.5) + (1750.00/6 * 15/30 ) + (1750.00/6 * 4 / 30* 0.5 )  
				, salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);
	}

	@Test
	public void testMultipleEREIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		int salarioBase = addConcept(aonContext, "SALARIO_BASE", PaymentType.CRA_0001).getId();
		int plusSalarial = addConcept(aonContext, "PLUS_SALARIAL", PaymentType.CRA_0001).getId();
		PaymentConceptRecord pagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext, new Extra[] { 
		new Extra() {
			{
				this.expression = "(SALARIO_BASE + PLUS_SALARIAL)";
				this.month = Month.JULY;
				this.start = "01/07 -1";
				this.end = "30/06";
				this.issue = "01/07";
				this.concept = pagaExtra.getId();
			}
		}, new Extra() {
			{
				this.expression = "(SALARIO_BASE + PLUS_SALARIAL)";
				this.month = Month.DECEMBER;
				this.start = "01/01";
				this.end = "31/12";
				this.issue = "15/12";
				this.concept = pagaExtra.getId();
			}
		}, 
		
		}, new Payment[] 
		{ 
		new Payment() {
			{
				this.concept = salarioBase;
				this.expression = "1500.00 * DIAS_TRABAJADOS / DIAS_MES";
			}
		}, 
		new Payment() {
			{
				this.concept = plusSalarial;
				this.expression = "250.00 * DIAS_TRABAJADOS / DIAS_MES";
			}
		}, 
//		new Payment() {
//			{
//				this.expression = "PLUS_TRANSPORTE";
//			}
//		} 
		});

		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put(MONTH_DAYS.getName(), "30.00");
//					put(CGC_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
//					put(CGP_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
//					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
//					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { 
						"( SALARIO_BASE + PLUS_SALARIAL )* 0.10 ",
//						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
//						"250.00 * DIAS_TRABAJADOS / DIAS_MES", 
			}
			, new String[] {
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
						"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05"
			}
			, category);
		
		addPayment(aonContext, contract, pagaExtra, "SALARIO_BASE + PLUS_SALARIAL", "_P", PaymentType.CRA_0004);
		addPayment(aonContext, contract, pagaExtra, "SALARIO_BASE + PLUS_SALARIAL", "_P", PaymentType.CRA_0004);

		Date startEreI = getFirstDayOfMonth(getToday());
		//int ereDays = (int) (Math.random() * (getMax(getToday(), DAY_OF_MONTH) - 1));
		startEreI = add(startEreI, DAY_OF_MONTH, 15);
		Date endEreI = add(startEreI, DAY_OF_MONTH, 10);

		addData(aonContext, contract, startEreI, endEreI,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1.00");
					}
				});
		Date startEreII = add(endEreI, DAY_OF_MONTH, 1);
		
		addData(aonContext, contract, startEreII, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.50");
					}
				});

		PaymentConceptRecord ere = addConcept(aonContext,getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA", getDaysVariable()));
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ", " + payment.getQuote() + ")");
		}

		Assert.assertEquals(
				(1750.00 * 1.10) + (1750.00/6), salary.getCommonBase(),
				DELTA);


		Assert.assertEquals(
				(1750.00 * 1.10 * 15/30) + (1750.00 * 1.10 * 4 / 30 *0.5) + (1750.00/6 * 15/30 ) + (1750.00/6 * 4 / 30* 0.5 )  
				, salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);
	}
	
	@Test
	public void testMultipleEREIV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		int salarioBase = addConcept(aonContext, "SALARIO_BASE", PaymentType.CRA_0001).getId();
		int plusSalarial = addConcept(aonContext, "PLUS_SALARIAL", PaymentType.CRA_0001).getId();
		PaymentConceptRecord pagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext, new Extra[] { 
		new Extra() {
			{
				this.expression = "(SALARIO_BASE + PLUS_SALARIAL)";
				this.month = Month.JULY;
				this.start = "01/07 -1";
				this.end = "30/06";
				this.issue = "01/07";
				this.concept = pagaExtra.getId();
			}
		}, new Extra() {
			{
				this.expression = "(SALARIO_BASE + PLUS_SALARIAL)";
				this.month = Month.DECEMBER;
				this.start = "01/01";
				this.end = "31/12";
				this.issue = "15/12";
				this.concept = pagaExtra.getId();
			}
		}, 
		
		}, new Payment[] 
		{ 
		new Payment() {
			{
				this.concept = salarioBase;
				this.expression = "50.00 * DIAS_TRABAJADOS";
			}
		}, 
		new Payment() {
			{
				this.concept = plusSalarial;
				this.expression = "12.50 * DIAS_TRABAJADOS";
			}
		}
		});

		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put(MONTH_DAYS.getName(), "30.00");
				}
			},
			new String[] { 
//						"12.50 * DIAS_EFECTIVOS", 
			}
			, new String[] {
					"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
					"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05"
			}
			, category);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);


		double commanBase = salary.getCommonBase();

		Date startEreI = getFirstDayOfMonth(getToday());
		//int ereDays = (int) (Math.random() * (getMax(getToday(), DAY_OF_MONTH) - 1));
		//startEreI = add(startEreI, DAY_OF_MONTH, 15);
		Date endEreI = add(startEreI, DAY_OF_MONTH, 14);

		addData(aonContext, contract, startEreI, endEreI,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1.00");
					}
				});
		Date startEreII = add(endEreI, DAY_OF_MONTH, 1);
		
		addData(aonContext, contract, startEreII, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.75");
					}
				});

		PaymentConceptRecord ere = addConcept(aonContext,getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA", getDaysVariable()));
		

		startDate = getFirstDayOfMonth(getToday());
		endDate = getLastDayOfMonth(startDate);

		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ", " + payment.getQuote() + ")");
		}

		for (com.esferalia.aon.payroll.SalaryDeduction deduction : salary
				.getSalaryDeductions()) {
			System.out.println(deduction.getName() + " = " + deduction.getAmount()
					+ " (" + deduction.getExpression() + ")");
		}
		
		Assert.assertEquals(
				commanBase, 
				salary.getCommonBase(),
				DELTA);


		Assert.assertEquals(
				1875.00 / 2 * 0.25  
				, salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals(
				(salary.getTotalPayment() + salary.getTotalPayment()/6) * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);
	}

	
	@Test
	public void testEREGROSSI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put(MONTH_DAYS.getName(), "30.00");
					put(CGC_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGP_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { 
						"( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", 
						"BRUTO(2500.00*DIAS_TRABAJADOS/DIAS_MES)"
						}
			, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05"
				}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startEre = getFirstDayOfMonth(getToday());
		int ereDays = 10 ; //(int) (0.25/*Math.random()*/ * (getMax(getToday(), DAY_OF_MONTH) - 1));
		Date endEre = add(startEre, DAY_OF_MONTH, ereDays - 1);

		addData(aonContext, contract, startEre, endEre,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext, getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA",getDaysVariable().getName()));
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getQuote() + ")");
		}

		Assert.assertEquals(
				(2500.00) * (get(endDate, DAY_OF_MONTH) - (ereDays))
						/ get(endDate, DAY_OF_MONTH), salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals(
				(2500.00) , salary.getCommonBase(),
				DELTA);

		Assert.assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);
	}
	
	@Test
	public void testEREGROSSII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put(MONTH_DAYS.getName(), "30.00");
					put(CGC_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGP_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { 
						"( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", 
						"BRUTO(2500.00*DIAS_TRABAJADOS/DIAS_MES)"
						}
			, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05"
				}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startEre = getFirstDayOfMonth(getToday());
		Date endEre = null;

		addData(aonContext, contract, startEre, endEre,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext, getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA",getDaysVariable().getName()));
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getQuote() + ")");
		}

		Assert.assertEquals(
				0.00, salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals(
				(2500.00) , salary.getCommonBase(),
				DELTA);

		Assert.assertEquals(
				0.00
				, salary.getSocialSecurityContributions(),
				DELTA);
	}
	
	@Ignore("BRUTO it's no yet 'SMART' supported")
	@Test
	public void testEREGROSSIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put(MONTH_DAYS.getName(), "30.00");
					put(CGC_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGP_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { 
						"( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", 
						"BRUTO(2500.00)"
						}
			, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05"
				}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startEre = getFirstDayOfMonth(getToday());
		int ereDays = 10 ; //(int) (0.25/*Math.random()*/ * (getMax(getToday(), DAY_OF_MONTH) - 1));
		Date endEre = add(startEre, DAY_OF_MONTH, ereDays - 1);

		addData(aonContext, contract, startEre, endEre,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext, getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * 2500.00/30",getDaysVariable().getName()));
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getQuote() + ")");
		}

		Assert.assertEquals(
				(2500.00) , salary.getCommonBase(),
				DELTA);


		Assert.assertEquals(
				(2500.00) * (get(endDate, DAY_OF_MONTH) - (ereDays))
						/ get(endDate, DAY_OF_MONTH), salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);
	}
	

	@Test
	public void testEREBRI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put(MONTH_DAYS.getName(), "30.00");
					put(CGC_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGP_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
						"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05"}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startEre = getFirstDayOfMonth(getToday());
		//int ereDays = (int) (Math.random() * (getMax(getToday(), DAY_OF_MONTH) - 1));
		//Date endEre = add(startEre, DAY_OF_MONTH, ereDays - 1);

		addData(aonContext, contract, startEre, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1.00");
						put("BASE_REGULADORA", "100.00");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext,getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA", getDaysVariable()));
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ", " + payment.getQuote() + ")");
		}

		Assert.assertEquals(
				100.00 * 30, salary.getCommonBase(),
				DELTA);


		Assert.assertEquals(
				0.00
						, salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);
	}

	@Test
	public void testEREBRII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put(MONTH_DAYS.getName(), "30.00");
					put(CGC_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGP_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
						"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05"}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startEre = getFirstDayOfMonth(getToday());
		//int ereDays = (int) (Math.random() * (getMax(getToday(), DAY_OF_MONTH) - 1));
		//Date endEre = add(startEre, DAY_OF_MONTH, ereDays - 1);

		addData(aonContext, contract, startEre, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.50");
						put("BASE_REGULADORA", "100.00");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext,getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA", getDaysVariable()));
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ", " + payment.getQuote() + ")");
		}

		Assert.assertEquals(
				1750.00 * 1.10 / 2 + 100.00 * 15 , salary.getCommonBase(),
				DELTA);


		Assert.assertEquals(
				1750.00 * 1.10 / 2
						, salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);
	}

	@Test
	public void testERESecondMonthI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put(CGC_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGP_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
						"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05"}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startEre = add(getFirstDayOfMonth(getToday()),DAY_OF_MONTH, 10);

		addData(aonContext, contract, startEre, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext, getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA",getDaysVariable().getName()));
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);


		Assert.assertEquals(
				(1750.00 * 1.10) * 10
						/ get(endDate, DAY_OF_MONTH), salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals(
				(1750.00 * 1.10) , salary.getCommonBase(),
				DELTA);

		Assert.assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);
		
		startDate = AonDateUtils.add(startDate, Calendar.MONTH, 1);
		endDate = getLastDayOfMonth(startDate);

		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getQuote() + ")");
		}

		Assert.assertEquals(
				0.00, salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals(
				(1750.00 * 1.10) , salary.getCommonBase(),
				DELTA);

		Assert.assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);
	}

	@Test
	public void testERESecondMonthIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put("DIAS_MES", "30.0");
					put(CGC_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGP_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
						"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05"}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startEre = add(getFirstDayOfMonth(getToday()),DAY_OF_MONTH, 10);

		addData(aonContext, contract, startEre, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.5");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext, getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA",getDaysVariable().getName()));
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);


		Assert.assertEquals(
				(1750.00 * 1.10) * 20
						/ 30, salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals(
				(1750.00 * 1.10) , salary.getCommonBase(),
				DELTA);

		Assert.assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);
		
		startDate = AonDateUtils.add(startDate, Calendar.MONTH, 1);
		endDate = getLastDayOfMonth(startDate);

		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getQuote() + ")");
		}

		Assert.assertEquals(
				(1750.00 * 1.10) * 0.5, salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals(
				(1750.00 * 1.10) , salary.getCommonBase(),
				DELTA);

		Assert.assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);
	}

	@Test
	public void testERESecondMonthII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put("DIAS_MES", "30.00");
					put(CGC_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGP_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
						"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05"}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startEre = add(getFirstDayOfMonth(getToday()),DAY_OF_MONTH, 10);

		addData(aonContext, contract, startEre, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext, getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA",getDaysVariable().getName()));
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = 
		new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(
				jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();


//		Assert.assertEquals(
//				(1750.00 * 1.10) * 10
//						/ get(endDate, DAY_OF_MONTH), salary.getTotalPayment(),
//				DELTA);
//
//		Assert.assertEquals(
//				(1750.00 * 1.10) , salary.getCommonBase(),
//				DELTA);
//
//		Assert.assertEquals(
//				salary.getTotalPayment() * 0.15 
//				, salary.getSocialSecurityContributions(),
//				DELTA);
		
		startDate = AonDateUtils.add(startDate, Calendar.MONTH, 1);
		endDate = getLastDayOfMonth(startDate);

		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getQuote() + ")");
		}

		Assert.assertEquals(
				0.00, salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals(
				(1750.00 * 1.10) , salary.getCommonBase(),
				DELTA);

		Assert.assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);
	}
	
	@Test
	public void testERESecondMonthIV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put("DIAS_MES", "30.00");
					put(CGC_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGP_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
						"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05"}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startEre = add(getFirstDayOfMonth(getToday()),DAY_OF_MONTH, 10);

		addData(aonContext, contract, startEre, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.5");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext, getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA",getDaysVariable().getName()));
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = 
		new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(
				jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();


//		Assert.assertEquals(
//				(1750.00 * 1.10) * 10
//						/ get(endDate, DAY_OF_MONTH), salary.getTotalPayment(),
//				DELTA);
//
//		Assert.assertEquals(
//				(1750.00 * 1.10) , salary.getCommonBase(),
//				DELTA);
//
//		Assert.assertEquals(
//				salary.getTotalPayment() * 0.15 
//				, salary.getSocialSecurityContributions(),
//				DELTA);
		
		startDate = AonDateUtils.add(startDate, Calendar.MONTH, 1);
		endDate = getLastDayOfMonth(startDate);

		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getQuote() + ")");
		}

		Assert.assertEquals(
				1750.00 * 1.10 / 2, salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals(
				(1750.00 * 1.10) , salary.getCommonBase(),
				DELTA);

		Assert.assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);
	}

	@Test
	public void testERESalaryHoursI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		addSystemSalaryHours(aonContext);
		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put("DIAS_MES", "30.00");
					put("GRUPO_COTIZACION", "\"01\"");
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05"}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startEre = add(getFirstDayOfMonth(getToday()),DAY_OF_MONTH, 10);

		addData(aonContext, contract, startEre, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.5");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext, getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA",getDaysVariable().getName()));
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = 
		new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(
				jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();

		
		AON.getSalaryData(aonContext, p -> p.getContractProperty().eq(contract.getId()))
		.forEach(s -> {
			List<ContextData> salaryHours = s.getContextData().get(ContextVariable.SALARY_HOURS.getName());
			org.junit.Assert.assertEquals(2, salaryHours.size());
			Collections.sort(salaryHours, (d1,d2)-> d1.getStartDate().compareTo(d2.getEndDate()));
			
			org.junit.Assert.assertEquals(startDate, salaryHours.get(0).getStartDate());
			org.junit.Assert.assertEquals(add(startEre, DAY_OF_MONTH,-1), salaryHours.get(0).getEndDate());
			org.junit.Assert.assertEquals(Math.floor(1466.40/8.83 * 10 /30) , Double.parseDouble( salaryHours.get(0).getExpression()), DELTA);
			
			
			org.junit.Assert.assertEquals(startEre, salaryHours.get(1).getStartDate());
			org.junit.Assert.assertEquals(endDate, salaryHours.get(1).getEndDate());
			org.junit.Assert.assertEquals(Math.floor(1466.40/8.83 * 20 /30 /2) , Double.parseDouble( salaryHours.get(1).getExpression()), DELTA);
		});
		;
		
	}

	@Test
	public void testERESalaryHoursII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		addSystemSalaryHours(aonContext);
		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put("DIAS_MES", "30.00");
					put("GRUPO_COTIZACION", "\"09\"");
					put("COEFICIENTE_PARCIALIDAD", "0.5");
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05"}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startEre = add(getFirstDayOfMonth(getToday()),DAY_OF_MONTH, 10);

		addData(aonContext, contract, startEre, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.35");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext, getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA",getDaysVariable().getName()));
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = 
		new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(
				jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();

		
		AON.getSalaryData(aonContext, p -> p.getContractProperty().eq(contract.getId()))
		.forEach(s -> {
			List<ContextData> salaryHours = s.getContextData().get(ContextVariable.SALARY_HOURS.getName());
			org.junit.Assert.assertEquals(2, salaryHours.size());
			Collections.sort(salaryHours, (d1,d2)-> d1.getStartDate().compareTo(d2.getEndDate()));
			
			org.junit.Assert.assertEquals(startDate, salaryHours.get(0).getStartDate());
			org.junit.Assert.assertEquals(add(startEre, DAY_OF_MONTH,-1), salaryHours.get(0).getEndDate());
			org.junit.Assert.assertEquals(Math.floor(1466.40/8.83 * 10 /30 /2) , Double.parseDouble( salaryHours.get(0).getExpression()), DELTA);
			
			
			org.junit.Assert.assertEquals(startEre, salaryHours.get(1).getStartDate());
			org.junit.Assert.assertEquals(endDate, salaryHours.get(1).getEndDate());
			org.junit.Assert.assertEquals(Math.floor(1466.40/8.83 * 20 /30 /2 * 0.65) , Double.parseDouble( salaryHours.get(1).getExpression()), DELTA);
		});
		;
		
	}

	@Test
	public void testERESalaryHoursIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		addSystemSalaryHours(aonContext);
		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put("TC2", "\"200\"");
//					put("DIAS_MES", "30.00");
					put("GRUPO_COTIZACION", "\"10\"");
					put("HORAS_LUNES", "1");
					put("HORAS_MARTES", "1");
					put("HORAS_MIERCOLES", "1");
					put("HORAS_JUEVES", "1");
					put("HORAS_VIERNES", "1");
					put("HORAS_SABADO", "0");
					put("HORAS_DOMINGO", "0");
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", 
						"HORAS_TRABAJADAS * 1 "}
			, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05"}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startEre = add(getFirstDayOfMonth(getToday()),DAY_OF_MONTH, 10);

		addData(aonContext, contract, startEre, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.25");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext, getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA",getDaysVariable().getName()));
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = 
		new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(
				jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();

		
		AON.getSalaryData(aonContext, p -> p.getContractProperty().eq(contract.getId()))
		.forEach(s -> {
			
			//s.getContextData().get(ContextVariable.WORKED_HOURS.getName()).forEach(d-> System.out.println(d.getExpression()));
			
			List<ContextData> salaryHours = s.getContextData().get(ContextVariable.SALARY_HOURS.getName());
			org.junit.Assert.assertEquals(2, salaryHours.size());
			Collections.sort(salaryHours, (d1,d2)-> d1.getStartDate().compareTo(d2.getEndDate()));
			
			org.junit.Assert.assertEquals(startDate, salaryHours.get(0).getStartDate());
			org.junit.Assert.assertEquals(add(startEre, DAY_OF_MONTH,-1), salaryHours.get(0).getEndDate());
			
			double hours = 
			new Period(salaryHours.get(0).getStartDate(), salaryHours.get(0).getEndDate())
			.daysStream().collect(Collectors.summingDouble(c -> {
				switch (c.get(Calendar.DAY_OF_WEEK)) {
				case Calendar.SUNDAY:
				case Calendar.SATURDAY:					
					return 0.0;

				default:
					return 1.0;
				}
			}));
			
			org.junit.Assert.assertEquals(hours, Double.parseDouble( salaryHours.get(0).getExpression()), DELTA);
			
			org.junit.Assert.assertEquals(startEre, salaryHours.get(1).getStartDate());
			org.junit.Assert.assertEquals(endDate, salaryHours.get(1).getEndDate());
			hours = 
			new Period(salaryHours.get(1).getStartDate(), salaryHours.get(1).getEndDate())
			.daysStream().collect(Collectors.summingDouble(c -> {
				switch (c.get(Calendar.DAY_OF_WEEK)) {
				case Calendar.SUNDAY:
				case Calendar.SATURDAY:					
					return 0.0;

				default:
					return 1.0;
				}
			}));
			
			org.junit.Assert.assertEquals(Math.floor(hours * 0.75) , Double.parseDouble( salaryHours.get(1).getExpression()), DELTA);
		});
		;
		
	}

	@Test
	public void testEREBRFebruaryI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		 
		
		ContractRecord contract = newContract(aonContext,
			add(getFirstDayOfYear(getToday()), Calendar.YEAR, -1 ), 
			new HashMap<String, String>() {
				{
					put("DIAS_MES", "30.00");
					put("TC2", "\"200\"");
					put("GRUPO_COTIZACION", "\"10\"");
					put("HORAS_LUNES", "4");
					put("HORAS_MARTES", "4");
					put("HORAS_MIERCOLES", "4");
					put("HORAS_JUEVES", "4");
					put("HORAS_VIERNES", "4");
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGC * 0.05",
						"TRACE('BASE_REG = %f\r\n', BASE_REGULADORA); 0.00",
//						"TRACE('BASE_CGP = %f\r\n', BASE_CGP * 0.05); BASE_CGC * 0.00",
						}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));
		

		Date december = add(getFirstDayOfYear(getToday()), MONTH, -1 );
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, december, getLastDayOfMonth(december), getLastDayOfMonth(december), contract);
		
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = 
		new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(
				jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();
		
		

		Date january = add(getFirstDayOfYear(getToday()), MONTH, 0 );
		
		addData(aonContext, contract, january, null, ContextVariable.MONDAY_HOURS, "6.00");
		addData(aonContext, contract, january, null, ContextVariable.TUESDAY_HOURS, "6.00");
		addData(aonContext, contract, january, null, ContextVariable.WEDNESDAY_HOURS, "6.00");
		addData(aonContext, contract, january, null, ContextVariable.THURSDAY_HOURS, "6.00");
		addData(aonContext, contract, january, null, ContextVariable.FRIDAY_HOURS, "6.00");		
		
		ctx = getContractSalaryCalculatorContext(
				connection, january, getLastDayOfMonth(january), getLastDayOfMonth(january), contract);
		
		jooqSalaryBuilder = 
		new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(
				jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();

		Date february = add(getFirstDayOfYear(getToday()), MONTH, 1 );
		ctx = getContractSalaryCalculatorContext(
				connection, february, getLastDayOfMonth(february), getLastDayOfMonth(february), contract);
		
		jooqSalaryBuilder = 
		new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(
				jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();
				
		
				
		Date march = add(february, MONTH ,1);
		ctx = getContractSalaryCalculatorContext(
				connection, march, getLastDayOfMonth(march), getLastDayOfMonth(march), contract);
		
		jooqSalaryBuilder = 
		new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(
				jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();

		Date startEre = add( march ,DAY_OF_MONTH, 10);

		addData(aonContext, contract, startEre, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.5");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext, getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA",getDaysVariable().getName()));
		

		Date startDate = getFirstDayOfMonth(march);
		Date endDate = getLastDayOfMonth(startDate);

		ctx = getContractSalaryCalculatorContext(
		connection, startDate, endDate, endDate, contract);

		ISalary salary = calculate(ctx);

//		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
//				.getSalaryPayments()) {
//			System.out.println(payment.getName() + " = " + payment.getAmount()
//					+ " (" + payment.getQuote() + ")");
//		}

		Assert.assertEquals(
				1750.00 * 1.10 * 10/30 * 0.75 
				+ 1750.00 * 1.10 * 20/30 * 0.75/2
				, 
				salary.getTotalPayment(),
				DELTA);

		double br = ( 1750.00 * 1.10 * 0.50
				+1750.00 * 1.10 * 0.75 
				+ 1750.00 * 1.10 * 0.75) 
				/ 90.00; 
		Assert.assertEquals(
				1750.00 * 1.10 * 20/30 * 0.75
				+ br * 10, 
				salary.getCommonBase(),
				DELTA);
		
		Assert.assertEquals(
				salary.getCommonBase() 
				, salary.getProfessionalBase(),
				DELTA);
		
//		for (com.esferalia.aon.payroll.SalaryDeduction deduction : salary
//				.getSalaryDeductions()) {
//			System.out.println(deduction.getName() + " = " + deduction.getAmount()
//					);
//		}

		Assert.assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);
		
	}
	
	@Test
	public void testEREBRAprilI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		 
		
		ContractRecord contract = newContract(aonContext,
			add(getFirstDayOfYear(getToday()), Calendar.YEAR, -1 ), 
			new HashMap<String, String>() {
				{
					put("DIAS_MES", "30.00");
					put("TC2", "\"200\"");
					put("GRUPO_COTIZACION", "\"10\"");
					put("HORAS_LUNES", "4");
					put("HORAS_MARTES", "4");
					put("HORAS_MIERCOLES", "4");
					put("HORAS_JUEVES", "4");
					put("HORAS_VIERNES", "4");
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGC * 0.05",
						"TRACE('BASE_REG = %f\r\n', BASE_REGULADORA); 0.00",
						"TRACE('DIAS_ERE = %f\r\n', " + getDaysVariable().getName()+  "); 0.00",
						}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));
		

		Date december = add(getFirstDayOfYear(getToday()), MONTH, -1 );
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, december, getLastDayOfMonth(december), getLastDayOfMonth(december), contract);
		
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = 
		new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(
				jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();
		
		

		Date january = add(getFirstDayOfYear(getToday()), MONTH, 0 );
		
		addData(aonContext, contract, january, null, ContextVariable.MONDAY_HOURS, "6.00");
		addData(aonContext, contract, january, null, ContextVariable.TUESDAY_HOURS, "6.00");
		addData(aonContext, contract, january, null, ContextVariable.WEDNESDAY_HOURS, "6.00");
		addData(aonContext, contract, january, null, ContextVariable.THURSDAY_HOURS, "6.00");
		addData(aonContext, contract, january, null, ContextVariable.FRIDAY_HOURS, "6.00");		
		
		ctx = getContractSalaryCalculatorContext(
				connection, january, getLastDayOfMonth(january), getLastDayOfMonth(january), contract);
		
		jooqSalaryBuilder = 
		new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(
				jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();

		Date february = add(getFirstDayOfYear(getToday()), MONTH, 1 );
		ctx = getContractSalaryCalculatorContext(
				connection, february, getLastDayOfMonth(february), getLastDayOfMonth(february), contract);
		
		jooqSalaryBuilder = 
		new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(
				jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();
				
		
				
		Date march = add(february, MONTH ,1);
		ctx = getContractSalaryCalculatorContext(
				connection, march, getLastDayOfMonth(march), getLastDayOfMonth(march), contract);
		
		jooqSalaryBuilder = 
		new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(
				jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();

		Date startEre = add( march ,DAY_OF_MONTH, 10);

		addData(aonContext, contract, startEre, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.5");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext, getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA",getDaysVariable().getName()));
		
		Date april = add(march, MONTH ,1);
		Date startDate = getFirstDayOfMonth(april);
		Date endDate = getLastDayOfMonth(startDate);

		ctx = getContractSalaryCalculatorContext(
		connection, startDate, endDate, endDate, contract);

		ISalary salary = calculate(ctx);

		Assert.assertEquals(
				1750.00 * 1.10 * 0.75 / 2.00
				, 
				salary.getTotalPayment(),
				DELTA);

		double br = ( 1750.00 * 1.10 * 0.50
				+1750.00 * 1.10 * 0.75 
				+ 1750.00 * 1.10 * 0.75) 
				/ 90.00; 
		Assert.assertEquals(
				br * 15 
				+ 1750.00 * 1.10 * 0.75 /2.00, 
				salary.getCommonBase(),
				DELTA);
		
		Assert.assertEquals(
				salary.getCommonBase() 
				, salary.getProfessionalBase(),
				DELTA);
		
		Assert.assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);
		
	}

	@Test
	public void testEREBRAprilII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		 
		
		ContractRecord contract = newContract(aonContext,
			add(getFirstDayOfYear(getToday()), Calendar.YEAR, -1 ), 
			new HashMap<String, String>() {
				{
					put("DIAS_MES", "30.00");
					put("TC2", "\"200\"");
					put("GRUPO_COTIZACION", "\"10\"");
					put("HORAS_LUNES", "4");
					put("HORAS_MARTES", "4");
					put("HORAS_MIERCOLES", "4");
					put("HORAS_JUEVES", "4");
					put("HORAS_VIERNES", "4");
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGC * 0.05",
						"TRACE('BASE_REG = %f\r\n', BASE_REGULADORA); 0.00",
						"TRACE('DIAS_ERE = %f\r\n', " + getDaysVariable().getName()+  "); 0.00",
						}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));
		

		Date december = add(getFirstDayOfYear(getToday()), MONTH, -1 );
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, december, getLastDayOfMonth(december), getLastDayOfMonth(december), contract);
		
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = 
		new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(
				jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();
		
		

		Date january = add(getFirstDayOfYear(getToday()), MONTH, 0 );
		
		addData(aonContext, contract, january, null, ContextVariable.MONDAY_HOURS, "6.00");
		addData(aonContext, contract, january, null, ContextVariable.TUESDAY_HOURS, "6.00");
		addData(aonContext, contract, january, null, ContextVariable.WEDNESDAY_HOURS, "6.00");
		addData(aonContext, contract, january, null, ContextVariable.THURSDAY_HOURS, "6.00");
		addData(aonContext, contract, january, null, ContextVariable.FRIDAY_HOURS, "6.00");		
		
		ctx = getContractSalaryCalculatorContext(
				connection, january, getLastDayOfMonth(january), getLastDayOfMonth(january), contract);
		
		jooqSalaryBuilder = 
		new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(
				jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();

		Date february = add(getFirstDayOfYear(getToday()), MONTH, 1 );
		ctx = getContractSalaryCalculatorContext(
				connection, february, getLastDayOfMonth(february), getLastDayOfMonth(february), contract);
		
		jooqSalaryBuilder = 
		new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(
				jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();
				
		
				
		Date march = add(february, MONTH ,1);
		ctx = getContractSalaryCalculatorContext(
				connection, march, getLastDayOfMonth(march), getLastDayOfMonth(march), contract);
		
		jooqSalaryBuilder = 
		new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(
				jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();

		Date startEre = add( march ,DAY_OF_MONTH, 10);

		addData(aonContext, contract, startEre, getLastDayOfMonth(startEre),
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.5");
					}
				});
		Date startEreII = add(getLastDayOfMonth(startEre), DAY_OF_MONTH,1);
		addData(aonContext, contract, startEreII, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.5");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext, getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA",getDaysVariable().getName()));
		
		Date april = add(march, MONTH ,1);
		Date startDate = getFirstDayOfMonth(april);
		Date endDate = getLastDayOfMonth(startDate);

		ctx = getContractSalaryCalculatorContext(
		connection, startDate, endDate, endDate, contract);

		ISalary salary = calculate(ctx);

		Assert.assertEquals(
				1750.00 * 1.10 * 0.75 / 2.00
				, 
				salary.getTotalPayment(),
				DELTA);

		double br = ( 1750.00 * 1.10 * 0.50
				+1750.00 * 1.10 * 0.75 
				+ 1750.00 * 1.10 * 0.75) 
				/ 90.00; 
		Assert.assertEquals(
				br * 15 
				+ 1750.00 * 1.10 * 0.75 /2.00, 
				salary.getCommonBase(),
				DELTA);
		
		Assert.assertEquals(
				salary.getCommonBase() 
				, salary.getProfessionalBase(),
				DELTA);
		
		Assert.assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);
		
	}

	@Test
	public void testEREBRMayII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		 
		
		ContractRecord contract = newContract(aonContext,
			add(getFirstDayOfYear(getToday()), Calendar.YEAR, -1 ), 
			new HashMap<String, String>() {
				{
					put("DIAS_MES", "30.00");
					put("TC2", "\"200\"");
					put("GRUPO_COTIZACION", "\"10\"");
					put("HORAS_LUNES", "4");
					put("HORAS_MARTES", "4");
					put("HORAS_MIERCOLES", "4");
					put("HORAS_JUEVES", "4");
					put("HORAS_VIERNES", "4");
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGC * 0.05",
						"TRACE('BASE_REG = %f\r\n', BASE_REGULADORA); 0.00",
						"TRACE('DIAS_ERE = %f\r\n', " + getDaysVariable().getName()+  "); 0.00",
						}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));
		

		Date december = add(getFirstDayOfYear(getToday()), MONTH, -1 );
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, december, getLastDayOfMonth(december), getLastDayOfMonth(december), contract);
		
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = 
		new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(
				jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();
		
		

		Date january = add(getFirstDayOfYear(getToday()), MONTH, 0 );
		
		addData(aonContext, contract, january, null, ContextVariable.MONDAY_HOURS, "6.00");
		addData(aonContext, contract, january, null, ContextVariable.TUESDAY_HOURS, "6.00");
		addData(aonContext, contract, january, null, ContextVariable.WEDNESDAY_HOURS, "6.00");
		addData(aonContext, contract, january, null, ContextVariable.THURSDAY_HOURS, "6.00");
		addData(aonContext, contract, january, null, ContextVariable.FRIDAY_HOURS, "6.00");		
		
		ctx = getContractSalaryCalculatorContext(
				connection, january, getLastDayOfMonth(january), getLastDayOfMonth(january), contract);
		
		jooqSalaryBuilder = 
		new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(
				jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();

		Date february = add(getFirstDayOfYear(getToday()), MONTH, 1 );
		ctx = getContractSalaryCalculatorContext(
				connection, february, getLastDayOfMonth(february), getLastDayOfMonth(february), contract);
		
		jooqSalaryBuilder = 
		new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(
				jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();
				
		
				
		Date march = add(february, MONTH ,1);
		ctx = getContractSalaryCalculatorContext(
				connection, march, getLastDayOfMonth(march), getLastDayOfMonth(march), contract);
		
		jooqSalaryBuilder = 
		new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(
				jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();

		Date startEre = add( march ,DAY_OF_MONTH, 10);

		addData(aonContext, contract, startEre, getLastDayOfMonth(startEre),
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.5");
					}
				});
		Date startEreII = add(getLastDayOfMonth(startEre), DAY_OF_MONTH,1);
		addData(aonContext, contract, startEreII,  getLastDayOfMonth(startEreII),
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.5");
					}
				});
		
		Date startEreIII = add(getLastDayOfMonth(startEreII), DAY_OF_MONTH,1);
		addData(aonContext, contract, startEreIII,  null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.5");
					}
				});

		PaymentConceptRecord ere = addConcept(aonContext, getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA",getDaysVariable().getName()));
		
		Date april = add(march, MONTH ,1);
		ctx = getContractSalaryCalculatorContext(
				connection, april, getLastDayOfMonth(april), getLastDayOfMonth(april), contract);
		
		jooqSalaryBuilder = 
		new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(
				jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();		
		
		
		Date may = add(april, MONTH ,1);
		Date startDate = getFirstDayOfMonth(may);
		Date endDate = getLastDayOfMonth(startDate);

		ctx = getContractSalaryCalculatorContext(
		connection, startDate, endDate, endDate, contract);

		ISalary salary = calculate(ctx);

		org.junit.Assert.assertEquals(may, salary.getStartDate());
		
		Assert.assertEquals(
				1750.00 * 1.10 * 0.75 / 2.00
				, 
				salary.getTotalPayment(),
				DELTA);

		double br = ( 1750.00 * 1.10 * 0.50
				+1750.00 * 1.10 * 0.75 
				+ 1750.00 * 1.10 * 0.75) 
				/ 90.00; 
		Assert.assertEquals(
				br * 15 
				+ 1750.00 * 1.10 * 0.75 /2.00, 
				salary.getCommonBase(),
				DELTA);
		
		Assert.assertEquals(
				salary.getCommonBase() 
				, salary.getProfessionalBase(),
				DELTA);
		
		Assert.assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);
		
	}

	@Test
	public void testMEJORAEREI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put(CGC_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGP_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
						"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05"}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startEre = getFirstDayOfMonth(getToday());
		int ereDays = (int) (0.25/*Math.random()*/ * (getMax(getToday(), DAY_OF_MONTH) - 1));
		Date endEre = add(startEre, DAY_OF_MONTH, ereDays - 1);

		addData(aonContext, contract, startEre, endEre,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext, getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA",getDaysVariable().getName()));
		
		PaymentConceptRecord mejora = addConcept(aonContext, "MEJORA", PaymentType.CRA_0000);
		addPayment(aonContext, contract, mejora, "MEJORAS DE PRESTACIONES DISTINTAS A LAS DE IT", "100.00", "_P", "_P", PaymentType.CRA_0000);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getQuote() + ")");
		}

		Assert.assertEquals(
				(1750.00 * 1.10) * (get(endDate, DAY_OF_MONTH) - (ereDays))/ get(endDate, DAY_OF_MONTH) + 100.00
				, salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals(
				(1750.00 * 1.10) , salary.getCommonBase(),
				DELTA);

		Assert.assertEquals(
				(salary.getTotalPayment() - 100) * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);
	}

	@Test
	public void testMEJORAEREII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put(CGC_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGP_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
						"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05"}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startEre = getFirstDayOfMonth(getToday());

		addData(aonContext, contract, startEre, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext, getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA",getDaysVariable().getName()));
		
		PaymentConceptRecord mejora = addConcept(aonContext, "MEJORA", PaymentType.CRA_0000);
		addPayment(aonContext, contract, mejora, "MEJORAS DE PRESTACIONES DISTINTAS A LAS DE IT", "100.00", "_P", "_P", PaymentType.CRA_0000);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getQuote() + ")");
		}

		Assert.assertEquals(
				100.00
				, salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals(
				(1750.00 * 1.10) , salary.getCommonBase(),
				DELTA);

		Assert.assertEquals(
				(salary.getTotalPayment() - 100) * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);
	}

	@Test
	public void testMEJORAEREIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put(CGC_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGP_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
						"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05"}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startEre = getFirstDayOfMonth(getToday());

		addData(aonContext, contract, startEre, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext, getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA",getDaysVariable().getName()));
		
		PaymentConceptRecord mejora = addConcept(aonContext, "MEJORA", PaymentType.CRA_0000);
		addPayment(aonContext, contract, mejora, "MEJORAS DE PRESTACIONES DISTINTAS A LAS DE IT", "BASE_REGULADORA * 0.10", "_P", "_P", PaymentType.CRA_0000);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getQuote() + ")");
		}

		Assert.assertEquals(
				1750 * 1.10 * 0.10
				, salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals(
				(1750.00 * 1.10) , salary.getCommonBase(),
				DELTA);

		Assert.assertEquals(
				0.00
				, salary.getSocialSecurityContributions(),
				DELTA);
	}

	@Test
	public void testMEJORAEREIV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put(CGC_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGP_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
						"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05"}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startEre = getFirstDayOfMonth(getToday());

		addData(aonContext, contract, startEre, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext, getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA",getDaysVariable().getName()));
		
		PaymentConceptRecord mejora = addConcept(aonContext, "MEJORA", PaymentType.CRA_0000);
		addPayment(aonContext, contract, mejora, "MEJORAS DE PRESTACIONES DISTINTAS A LAS DE IT", "BASE_REGULADORA * 0.80", "_P", "_P", PaymentType.CRA_0000);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getQuote() + ")");
		}

		Assert.assertEquals(
				1750 * 1.10 * 0.10
				, salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals(
				(1750.00 * 1.10) , salary.getCommonBase(),
				DELTA);

		Assert.assertEquals(
				0.00
				, salary.getSocialSecurityContributions(),
				DELTA);
	}

	@Test
	public void testMEJORAEREVI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put(CGC_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGP_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
						"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05"}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startEre = getFirstDayOfMonth(getToday());

		addData(aonContext, contract, startEre, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext, getEreVariable().getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA",getDaysVariable().getName()));
		
		PaymentConceptRecord mejora = addConcept(aonContext, "MEJORA", PaymentType.CRA_0000);
		addPayment(aonContext, contract, mejora, "MEJORAS DE PRESTACIONES DISTINTAS A LAS DE IT", "1750.00*1.10", "_P", "_P", PaymentType.CRA_0000);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getQuote() + ")");
		}

		Assert.assertEquals(
				1750 * 1.10 * 0.30
				, salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals(
				(1750.00 * 1.10) , salary.getCommonBase(),
				DELTA);

		Assert.assertEquals(
				0.00
				, salary.getSocialSecurityContributions(),
				DELTA);
	}

	protected ISalary calculate (ISQLContractSalaryCalculatorContext ctx) throws SalaryException {
		return new SmartContractSalaryCalculator<Salary>(
		new SalaryBuilder(){
		}).calculate(ctx);
		
	}

	private void addSystemSalaryHours(AONContext aonContext) {
		addSystemData(aonContext, getFirstDayOfYear(getToday()), null, new HashMap<String, String>(){
			{
				
				put("HORAS_NOMINA", "MAX(1,FLOOR("
						+ "[ "
						+ " \"01\":(POR_HORAS() ? HORAS_TRABAJADAS : 1466.40 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) / 8.83 * COEFICIENTE_TRABAJADO)"
						+ ",\"02\":(POR_HORAS() ? HORAS_TRABAJADAS : 1215.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) / 7.32 * COEFICIENTE_TRABAJADO)"
						+ ",\"03\":(POR_HORAS() ? HORAS_TRABAJADAS : 1057.80 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) / 6.37 * COEFICIENTE_TRABAJADO)"
						+ ",\"04\":(POR_HORAS() ? HORAS_TRABAJADAS : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) / 6.33 * COEFICIENTE_TRABAJADO)"
						+ ",\"05\":(POR_HORAS() ? HORAS_TRABAJADAS : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) / 6.33 * COEFICIENTE_TRABAJADO)"
						+ ",\"06\":(POR_HORAS() ? HORAS_TRABAJADAS : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) / 6.33 * COEFICIENTE_TRABAJADO)"
						+ ",\"07\":(POR_HORAS() ? HORAS_TRABAJADAS : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) / 6.33 * COEFICIENTE_TRABAJADO)"
						+ ",\"08\":(POR_HORAS() ? HORAS_TRABAJADAS : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) / 6.33 * COEFICIENTE_TRABAJADO)"
						+ ",\"09\":(POR_HORAS() ? HORAS_TRABAJADAS : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) / 6.33 * COEFICIENTE_TRABAJADO)"
						+ ",\"10\":(POR_HORAS() ? HORAS_TRABAJADAS : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) / 6.33 * COEFICIENTE_TRABAJADO)"
						+ ",\"11\":(POR_HORAS() ? HORAS_TRABAJADAS : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) / 6.33 * COEFICIENTE_TRABAJADO)"
						+ "] [GRUPO_COTIZACION]"
						+ ")"
						+ ")");
				
				put("POR_HORAS", "def () { isdef CONTEXT ? UTILIZADA('HORAS_TRABAJADAS') : FALSO() }");
				
			}
		});
	}

	protected ContextVariable getEreVariable() {
		return ERE;
	}	

	protected ContextVariable getFactorVariable() {
		return ERE_FACTOR;
	}
	protected ContextVariable getDaysVariable() {
		return ERE_DAYS;
	}
}
