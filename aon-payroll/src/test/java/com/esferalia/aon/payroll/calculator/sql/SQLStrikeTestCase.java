package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_MAX;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_MIN;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE_MAX;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE_MIN;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRIKE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRIKE_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_DAYS;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getMax;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;

public class SQLStrikeTestCase extends AbstractSQLTestCase {

	protected static final double DELTA = 0.000001;

	@Test
	public void testStrikeI() throws ExpressionException, SQLException,
			SalaryException {
		
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), Collections.emptyMap());

		Date strikeDay = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH, 10);

		addData(aonContext, contract, strikeDay, strikeDay,
				new HashMap<String, String>() {
					{
						put(STRIKE_FACTOR.getName(), "1");
					}
				});

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		List<ITimedResult<Double>> workDays = ctx.getExpressionContext().eval(
				WORKED_DAYS.getName(), startDate, endDate, Double.class);

		assertEquals(2, workDays.size());

		assertEquals((double)get(strikeDay, DAY_OF_MONTH)-1, workDays.get(0).getValue());
		assertEquals(
				workDays.get(0).getPeriod(),
				new Period(getFirstDayOfMonth(strikeDay), add(strikeDay,
						DAY_OF_MONTH, -1)));

		assertEquals(
				workDays.get(1).getValue(),
				(double) (getMax(strikeDay, DAY_OF_MONTH) - get(strikeDay,
						DAY_OF_MONTH)));
		assertEquals(workDays.get(1).getPeriod(),
				new Period(add(strikeDay, DAY_OF_MONTH, +1),
						getLastDayOfMonth(getToday())));

	}

	@Test
	public void testStrikeII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), Collections.emptyMap());

		Date startStrike = getFirstDayOfMonth(getToday());
		int strikeDays = (int) (Math.random() * (getMax(getToday(), DAY_OF_MONTH) - 1));
		Date endStrike = add(startStrike, DAY_OF_MONTH, strikeDays);

		addData(aonContext, contract, startStrike, endStrike,
				new HashMap<String, String>() {
					{
						put(STRIKE_FACTOR.getName(), "1");
					}
				});

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		List<ITimedResult<Double>> workDays = ctx.getExpressionContext().eval(
				"DIAS_TRABAJADOS", startDate, endDate, Double.class);

		assertEquals(1, workDays.size());

		assertEquals(
				workDays.get(0).getValue(),
				(double) (getMax(getToday(), DAY_OF_MONTH) - get(endStrike,
						DAY_OF_MONTH)));
		assertEquals(workDays.get(0).getPeriod(),
				new Period(add(endStrike, DAY_OF_MONTH, +1),
						getLastDayOfMonth(getToday())));

	}
	
	@Test
	public void testStrikeIII() throws ExpressionException, SQLException,
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
							put(STRIKE_FACTOR.getName(), "1");
						}
					});
		}

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		List<ITimedResult<Double>> workDays = ctx.getExpressionContext().eval(
				WORKED_DAYS.getName(), startDate, endDate, Double.class);

		assertEquals(11, workDays.size());

		assertEquals(2.00, workDays.get(0).getValue());
		assertEquals(workDays.get(0).getPeriod(),
				new Period(start, add(start, DAY_OF_MONTH, 1)));

		for (int i = 1; i < 10; i++) {
			Date date = add(start, DAY_OF_MONTH, i * 2 + 1);
			assertEquals(1.00, workDays.get(i).getValue());
			assertEquals(workDays.get(i).getPeriod(), new Period(date,
					date));
		}

		assertEquals((double) (getMax(getToday(), DAY_OF_MONTH) - 21),
				workDays.get(10).getValue());
		assertEquals(workDays.get(10).getPeriod(),
				new Period(add(start, DAY_OF_MONTH, 21),
						getLastDayOfMonth(getToday())));

	}
	
	@Test
	public void testStrikeIV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), Collections.emptyMap());

		Date startDate = getFirstDayOfMonth(getToday());

		addData(aonContext, contract, startDate, null,
				new HashMap<String, String>() {
					{
						put(STRIKE_FACTOR.getName(), "1");
					}
				});

		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		try {
			List<ITimedResult<Double>> workDays = ctx.getExpressionContext()
					.eval("DIAS_TRABAJADOS", startDate, endDate, Double.class);
			fail();
		} catch (UndefinedVariablesException e) {
			assertEquals("DIAS_TRABAJADOS", e.getVariableNames()[0]);
			return;
		}
		fail();

	}

	@Test
	public void testStrikeV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), Collections.emptyMap());

		Date startDate = getFirstDayOfMonth(getToday());

		addData(aonContext, contract, startDate, null,
				new HashMap<String, String>() {
					{
						put(STRIKE_FACTOR.getName(), "0.75");
					}
				});

		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		List<ITimedResult<Double>> workDays = ctx.getExpressionContext().eval(
				"DIAS_TRABAJADOS", startDate, endDate, Double.class);

		assertEquals(1, workDays.size());
		assertEquals(workDays.get(0).getPeriod(), new Period(startDate,
				endDate));
		assertEquals(workDays.get(0).getValue(),
				get(endDate, DAY_OF_MONTH) * 0.25);

	}
	
	
	@Test
	public void testStrikeVIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put(CGC_BASE_MIN.getName(), "858.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)");
					put(CGP_BASE_MIN.getName(), "858.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)");
					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
						"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05",
						"TRACE('DIAS_NOMINA = %f\r\n', DIAS_NOMINA); 0.00"
						}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startStrike = getFirstDayOfMonth(getToday());
		int strikeDays = (int) (Math.random() * (getMax(getToday(), DAY_OF_MONTH) - 1));
		Date endStrike = add(startStrike, DAY_OF_MONTH, strikeDays - 1);

		addData(aonContext, contract, startStrike, endStrike,
				new HashMap<String, String>() {
					{
						put(STRIKE_FACTOR.getName(), "1");
					}
				});
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		System.out.println("Strike Days: " + strikeDays);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new ContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		assertEquals(
				(1750.00 * 1.10) * (get(endDate, DAY_OF_MONTH) - (strikeDays))
						/ get(endDate, DAY_OF_MONTH), salary.getTotalPayment(),
				DELTA);

		assertEquals(
				salary.getTotalPayment(), 
				salary.getCommonBase(),
				DELTA);

		assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);
	}
	
	@Test
	public void testStrikeIX() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put(CGC_BASE_MIN.getName(), "858.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)");
					put(CGP_BASE_MIN.getName(), "858.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)");
					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
						"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05",
						"TRACE('DIAS_NOMINA = %f\r\n', DIAS_NOMINA); 0.00"
						}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startStrike = getFirstDayOfMonth(getToday());
		int strikeDays = (int) (Math.random() * (getMax(getToday(), DAY_OF_MONTH) - 1));
		Date endStrike = add(startStrike, DAY_OF_MONTH, strikeDays - 1);

		addData(aonContext, contract, startStrike, endStrike,
				new HashMap<String, String>() {
					{
						put(STRIKE_FACTOR.getName(), "1");
					}
				});
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		System.out.println("Strike Days: " + strikeDays);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new ContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		assertEquals(
				(1750.00 * 1.10) * (get(endDate, DAY_OF_MONTH) - (strikeDays))
						/ get(endDate, DAY_OF_MONTH), salary.getTotalPayment(),
				DELTA);

		assertEquals(
				salary.getTotalPayment(), 
				salary.getCommonBase(),
				DELTA);

		assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);
	}



	@Test
	public void testStrikeX() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put(CGC_BASE_MIN.getName(), "858.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)");
					put(CGP_BASE_MIN.getName(), "858.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)");
					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { "100.00",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
						"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05",
						"TRACE('DIAS_NOMINA = %f\r\n', DIAS_NOMINA); 0.00"
						}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startStrike = getFirstDayOfMonth(getToday());
		int strikeDays = (int) (Math.random() * (getMax(getToday(), DAY_OF_MONTH) - 1));
		Date endStrike = add(startStrike, DAY_OF_MONTH, strikeDays - 1);

		addData(aonContext, contract, startStrike, endStrike,
				new HashMap<String, String>() {
					{
						put(STRIKE_FACTOR.getName(), "1");
					}
				});
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		System.out.println("Strike Days: " + strikeDays);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		assertEquals(
				((1850.00 ) * (get(endDate, DAY_OF_MONTH) - (strikeDays)) / get(endDate, DAY_OF_MONTH) ) 
				, salary.getTotalPayment(),
				DELTA);

		assertEquals(
				salary.getTotalPayment(), 
				salary.getCommonBase(),
				DELTA);

		assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);
		
		long count = 
		salary.getSalaryDatas()
		.stream().filter( data -> data.getName().equals(CGC_BASE.getName()))
		.peek( data -> System.out.println(data.getName() + " = " + data.getExpression() )) 
		.count()
		;
		
		
		assertEquals(
				1 
				, count);
	}

	@Test
	public void testStrikeXI() throws ExpressionException, SQLException,
			SalaryException {
		
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date contractStart = getFirstDayOfYear(getToday());
		
		ContractRecord contract = newContract(aonContext,
				contractStart, Collections.emptyMap());

		addData(aonContext, contract, contractStart, null, ContextVariable.MONTH_DAYS, "30.00");

		
		Date startDate = add(getFirstDayOfYear(getToday()), MONTH, 2);
		
		Date strikeDay = add(startDate, DAY_OF_MONTH, 10);

		addData(aonContext, contract, strikeDay, strikeDay,
				new HashMap<String, String>() {
					{
						put(STRIKE_DAYS.getName(), "1.00");
						put(STRIKE_FACTOR.getName(), "1.00");
					}
				});
		
		addPayment(aonContext, contract, "1200.00 * DIAS_TRABAJADOS / DIAS_MES");
		
		
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);
		
		assertEquals(1200.00 * 29 / 30.00, salary.getTotalPayment(), DELTA);
		

	}
}
