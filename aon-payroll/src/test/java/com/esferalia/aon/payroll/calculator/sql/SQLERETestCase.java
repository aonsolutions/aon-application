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

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.sql.AbstractSQLTestCase.Extra;
import com.esferalia.aon.payroll.calculator.sql.AbstractSQLTestCase.Payment;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;

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
