/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS;
import static com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SalaryEmbargo.SALARY_EMBARGO;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.AGREEMENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_GROUP;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SYSTEM;
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

import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseActivityRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseCccRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.ScopeRecord;
import com.esferalia.aon.jooq.tables.records.WorkplaceRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.CheckException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;
import com.esferalia.aon.watson.util.AonDateUtils;

import junit.framework.Assert;

/**
 * @author rtrepiana
 *
 */
public class SQLFunctionsTestCase extends
		AbstractSQLTestCase {

	private static final double DELTA = 0.000000001;

	@Test
	public void testDateFunctionI() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				newContract(aonContext, getToday(), Collections.emptyMap()));
		//@formatter:on
		
		
		Date today = getToday();
		
		List<ITimedResult<java.util.Date>>  date = 
				ctx.getExpressionContext().eval(
						String.format("FECHA(%d,%d,%d)", 
								get(today, Calendar.YEAR),
								get(today, Calendar.MONTH)+1,
								get(today, Calendar.DATE)
						)
						, startDate
						, endDate, java.util.Date.class);
		Assert.assertEquals(today, new Date(date.get(0).getValue().getTime()));
	}

	@Test
	public void testDateFunctionII() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				newContract(aonContext, getToday(), Collections.emptyMap()));
		//@formatter:on
		
		
		Date today = getToday();
		
		List<ITimedResult<java.util.Date>>  date = 
				ctx.getExpressionContext().eval(
						String.format("FECHA(%d,%d,%d)", 
								get(today, Calendar.YEAR),
								get(today, Calendar.MONTH)+2,
								0
						)
						, startDate
						, endDate, java.util.Date.class);
		Assert.assertEquals(getLastDayOfMonth(today), new Date(date.get(0).getValue().getTime()));
	}

	@Test
	public void testMonthFunction() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				newContract(aonContext, getToday(), Collections.emptyMap()));
		//@formatter:on
		
		Date today = getToday();
		
		List<ITimedResult<java.util.Date>>  date = 
				ctx.getExpressionContext().eval(
						String.format("MES(FECHA(%d,%d,%d),1)", 
								get(today, Calendar.YEAR),
								get(today, Calendar.MONTH)+1,
								get(today, Calendar.DATE)
						)
						, startDate
						, endDate, java.util.Date.class);
		
		Assert.assertEquals(add(today, Calendar.MONTH, 1), new Date(date.get(0).getValue().getTime()));

		List<ITimedResult<Integer>>  month = 
				ctx.getExpressionContext().eval(
						String.format("MES(FECHA(%d,%d,%d))", 
								get(today, Calendar.YEAR),
								get(today, Calendar.MONTH)+1,
								get(today, Calendar.DATE)
						)
						, startDate
						, endDate, Integer.class);
		
		Assert.assertEquals(get(today, Calendar.MONTH)+1, (int)month.get(0).getValue());
	}

	@Test
	public void testYearFunction() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				newContract(aonContext, getToday(), Collections.emptyMap()));
		//@formatter:on
		
		Date today = getToday();
		
		List<ITimedResult<java.util.Date>>  date = 
				ctx.getExpressionContext().eval(
						String.format("AÑO(FECHA(%d,%d,%d),1)", 
								get(today, Calendar.YEAR),
								get(today, Calendar.MONTH)+1,
								get(today, Calendar.DATE)
						)
						, startDate
						, endDate, java.util.Date.class);
		
		Assert.assertEquals(add(today, Calendar.YEAR, 1), new Date(date.get(0).getValue().getTime()));

		List<ITimedResult<Integer>>  month = 
				ctx.getExpressionContext().eval(
						String.format("AÑO(FECHA(%d,%d,%d))", 
								get(today, Calendar.YEAR),
								get(today, Calendar.MONTH)+1,
								get(today, Calendar.DATE)
						)
						, startDate
						, endDate, Integer.class);
		
		Assert.assertEquals(get(today, Calendar.YEAR), (int)month.get(0).getValue());
	}

	@Test
	public void testDayFunction() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				newContract(aonContext, getToday(), Collections.emptyMap()));
		//@formatter:on
		
		Date today = getToday();
		
		List<ITimedResult<java.util.Date>>  date = 
				ctx.getExpressionContext().eval(
						String.format("DIA(FECHA(%d,%d,%d),1)", 
								get(today, Calendar.YEAR),
								get(today, Calendar.MONTH)+1,
								get(today, Calendar.DATE)
						)
						, startDate
						, endDate, java.util.Date.class);
		
		Assert.assertEquals(add(today, Calendar.DAY_OF_MONTH, 1), new Date(date.get(0).getValue().getTime()));

		List<ITimedResult<Integer>>  month = 
				ctx.getExpressionContext().eval(
						String.format("DIA(FECHA(%d,%d,%d))", 
								get(today, Calendar.YEAR),
								get(today, Calendar.MONTH)+1,
								get(today, Calendar.DATE)
						)
						, startDate
						, endDate, Integer.class);
		
		Assert.assertEquals(get(today, Calendar.DATE), (int)month.get(0).getValue());
	}

	@Test
	public void testDateCompare() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				newContract(aonContext, getToday(), Collections.emptyMap()));
		//@formatter:on
		
		Date today = getToday();
		
		List<ITimedResult<Boolean>>  compare = 
				ctx.getExpressionContext().eval(
						String.format("FECHA(%d,%d,%d) < DIA(FECHA(%d,%d,%d),1)", 
								get(today, Calendar.YEAR),
								get(today, Calendar.MONTH)+1,
								get(today, Calendar.DATE),
								get(today, Calendar.YEAR),
								get(today, Calendar.MONTH)+1,
								get(today, Calendar.DATE)
						)
						, startDate
						, endDate, Boolean.class);
		
		compare = 
				ctx.getExpressionContext().eval(
						String.format("FECHA(%d,%d,%d) > DIA(FECHA(%d,%d,%d),1)", 
								get(today, Calendar.YEAR),
								get(today, Calendar.MONTH)+1,
								get(today, Calendar.DATE),
								get(today, Calendar.YEAR),
								get(today, Calendar.MONTH)+1,
								get(today, Calendar.DATE)
						)
						, startDate
						, endDate, Boolean.class);

		Assert.assertEquals(Boolean.FALSE, compare.get(0).getValue());

	}

	@Test
	public void testDaysFunction() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				newContract(aonContext, getToday(), Collections.emptyMap()));
		//@formatter:on
		
		Date today = getToday();
		
		List<ITimedResult<Integer>>  days = 
				ctx.getExpressionContext().eval(
						String.format("DIAS(DIA(FECHA(%d,%d,%d),10),FECHA(%d,%d,%d))", 
								get(today, Calendar.YEAR),
								get(today, Calendar.MONTH)+1,
								get(today, Calendar.DATE),
								get(today, Calendar.YEAR),
								get(today, Calendar.MONTH)+1,
								get(today, Calendar.DATE)
						)
						, startDate
						, endDate, Integer.class);
		

		Assert.assertEquals(10, (int)days.get(0).getValue());

		days = 
				ctx.getExpressionContext().eval(
						String.format("DIAS(DIA(FECHA(%d,%d,%d),100),FECHA(%d,%d,%d))", 
								get(today, Calendar.YEAR),
								get(today, Calendar.MONTH)+1,
								get(today, Calendar.DATE),
								get(today, Calendar.YEAR),
								get(today, Calendar.MONTH)+1,
								get(today, Calendar.DATE)
						)
						, startDate
						, endDate, Integer.class);
		

		Assert.assertEquals(100, (int)days.get(0).getValue());

	}

	@Test
	public void testMonthStartFunction() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				newContract(aonContext, getToday(), Collections.emptyMap()));
		//@formatter:on
		
		Date today = getToday();
		
		List<ITimedResult<java.util.Date>>  firstDayOfMonth = 
				ctx.getExpressionContext().eval(
						String.format("INICIO_MES(FECHA(%d,%d,%d))", 
								get(today, Calendar.YEAR),
								get(today, Calendar.MONTH)+1,
								10
						)
						, startDate
						, endDate, java.util.Date.class);
		

		Assert.assertEquals(1, firstDayOfMonth.size());
		Assert.assertEquals(getFirstDayOfMonth(today), firstDayOfMonth.get(0).getValue());


	}

	@Test
	public void testMonthEndFunction() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				newContract(aonContext, getToday(), Collections.emptyMap()));
		//@formatter:on
		
		
		List<ITimedResult<java.util.Date>>  lastDayOfMonth = 
				ctx.getExpressionContext().eval("FIN_MES(TODAY)" 
						, startDate
						, endDate, java.util.Date.class);
		

		Assert.assertEquals(1, lastDayOfMonth.size());
		Assert.assertEquals(getLastDayOfMonth(getToday()), lastDayOfMonth.get(0).getValue());


	}

	@Test
	public void testYearEndFunction() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				newContract(aonContext, getToday(), Collections.emptyMap()));
		//@formatter:on
		
		
		List<ITimedResult<java.util.Date>>  lastDayOfYear = 
				ctx.getExpressionContext().eval("FIN_AÑO(TODAY)" 
						, startDate
						, endDate, java.util.Date.class);
		

		Assert.assertEquals(1, lastDayOfYear.size());
		Assert.assertEquals(AonDateUtils.getLastDayOfYear(getToday()), lastDayOfYear.get(0).getValue());


	}

	@Test
	public void testYearStartFunction() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				newContract(aonContext, getToday(), Collections.emptyMap()));
		//@formatter:on
		
		Date today = getToday();
		
		List<ITimedResult<java.util.Date>>  firstDayOfYear = 
				ctx.getExpressionContext().eval(
						String.format("INICIO_AÑO(FECHA(%d,%d,%d))", 
								get(today, Calendar.YEAR),
								get(today, Calendar.MONTH)+1,
								10
						)
						, startDate
						, endDate, java.util.Date.class);
		

		Assert.assertEquals(1, firstDayOfYear.size());
		Assert.assertEquals(AonDateUtils.getFirstDayOfYear(today), firstDayOfYear.get(0).getValue());


	}

	@Test
	public void testSystemFunction() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, getToday(),
				new HashMap<String, String>() {
					{
						put(MONTH_DAYS.getName(), "666.00");
					}
				});
		

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		// First of month of contract, 99% will be partial
		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);
		ctx.next();
		
		ctx.getExpressionContext().eval(String.format("%s('%s')", SYSTEM, MONTH_DAYS), getToday(), end, Double.class)
		.stream()
		.forEach(result-> Assert.assertEquals(String.format("%s('%s')", SYSTEM, MONTH_DAYS), (double) getMax(end, DAY_OF_MONTH), result.getValue()));
		

		ctx.getExpressionContext().eval(String.format("%s", MONTH_DAYS), getToday(), end, Double.class)
		.stream()
		.forEach(result-> Assert.assertEquals(String.format("%s", MONTH_DAYS), 666.00, result.getValue()));
	}

	@Test
	public void testAgreementFunction() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		AgreementLevelCategoryRecord category = newAgreement(aonContext, new Extra[0], 				
			new HashMap<String, String>() {
			{
				put(MONTH_DAYS.getName(), "69.00");
			}
		});
		ContractRecord contract = newContract(aonContext, 
		getToday(), 
			new HashMap<String, String>() {
			{
				put(MONTH_DAYS.getName(), "666.00");
			}
		},
		category);
		

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		// First of month of contract, 99% will be partial
		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);
		ctx.next();
		
		ctx.getExpressionContext().eval(String.format("%s('%s')", SYSTEM, MONTH_DAYS), getToday(), end, Double.class)
		.stream()
		.forEach(result-> Assert.assertEquals(String.format("%s('%s')", SYSTEM, MONTH_DAYS), (double) getMax(end, DAY_OF_MONTH), result.getValue()));
		ctx.getExpressionContext().eval(String.format("%s('%s')", AGREEMENT, MONTH_DAYS), getToday(), end, Double.class)
		.stream()
		.forEach(result-> Assert.assertEquals(String.format("%s('%s')", AGREEMENT, MONTH_DAYS), 69.00, result.getValue()));
		ctx.getExpressionContext().eval(String.format("%s", MONTH_DAYS), getToday(), end, Double.class)
		.stream()
		.forEach(result-> Assert.assertEquals(String.format("%s", MONTH_DAYS), 666.00, result.getValue()));
		
		Date startDate = getToday();
		Date endDate = getLastDayOfMonth(startDate);
		addData(aonContext, category, startDate, endDate, 			
			new HashMap<String, String>() {
			{
				put(MONTH_DAYS.getName(), "96.00");
			}
		});
		ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);
		ctx.next();
		ctx.getExpressionContext().eval(String.format("%s('%s')", SYSTEM, MONTH_DAYS), getToday(), end, Double.class)
		.stream()
		.forEach(result-> Assert.assertEquals(String.format("%s('%s')", SYSTEM, MONTH_DAYS), (double) getMax(end, DAY_OF_MONTH), result.getValue()));
		ctx.getExpressionContext().eval(String.format("%s('%s')", AGREEMENT, MONTH_DAYS), getToday(), end, Double.class)
		.stream()
		.forEach(result-> Assert.assertEquals(String.format("%s('%s')", AGREEMENT, MONTH_DAYS), 96.00, result.getValue()));
		ctx.getExpressionContext().eval(String.format("%s", MONTH_DAYS), getToday(), end, Double.class)
		.stream()
		.forEach(result-> Assert.assertEquals(String.format("%s", MONTH_DAYS), 666.00, result.getValue()));

	
		startDate = getToday();
		endDate = getLastDayOfMonth(startDate);
		addData(aonContext, contract.getDomain(), category, startDate, endDate, 			
			new HashMap<String, String>() {
			{
				put(MONTH_DAYS.getName(), "99.00");
			}
		});
		ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);
		ctx.next();
		ctx.getExpressionContext().eval(String.format("%s('%s')", SYSTEM, MONTH_DAYS), getToday(), end, Double.class)
		.stream()
		.forEach(result-> Assert.assertEquals(String.format("%s('%s')", SYSTEM, MONTH_DAYS), (double) getMax(end, DAY_OF_MONTH), result.getValue()));
		ctx.getExpressionContext().eval(String.format("%s('%s')", AGREEMENT, MONTH_DAYS), getToday(), end, Double.class)
		.stream()
		.forEach(result-> Assert.assertEquals(String.format("%s('%s')", AGREEMENT, MONTH_DAYS), 99.00, result.getValue()));
		ctx.getExpressionContext().eval(String.format("%s", MONTH_DAYS), getToday(), end, Double.class)
		.stream()
		.forEach(result-> Assert.assertEquals(String.format("%s", MONTH_DAYS), 666.00, result.getValue()));
	}

	
	@Test
	public void testInputFunctionI() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				newContract(aonContext, getToday(), Collections.emptyMap()));
		//@formatter:on
		
		try {
			ctx.getExpressionContext().eval("INPUT('','Hello World!!!');", 
					startDate
					,endDate, 
					Object.class);
		
		} catch ( CheckException e ) {
				System.out.println(e.getMessage());
				Assert.assertEquals("Hello World!!!", e.getMessage());
				return;
		} 
		Assert.fail();
	}

	@Test
	public void testInputFunctionII() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				newContract(aonContext, getToday(), Collections.emptyMap()));
		//@formatter:on
		
		try {
			ctx.getExpressionContext().eval("INPUT('NO_DEFINIDA * DIAS_TRABAJADOS / DIAS_MES','Hello World!!!');", 
					startDate
					,endDate, 
					Object.class);
		
		} catch ( UndefinedVariablesException e ) {
				System.out.println(e.getVariableNames()[0]);
				Assert.assertEquals("NO_DEFINIDA", e.getVariableNames()[0]);
				return;
		} 
		Assert.fail();
	}

	@Test
	public void testInputFunctionIII() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				newContract(aonContext, startDate, Collections.emptyMap()));
		//@formatter:on
		
			List<ITimedResult<Double>> result = ctx.getExpressionContext().eval("INPUT('100.00 * DIAS_TRABAJADOS / DIAS_MES','Hello World!!!');", 
					startDate
					,endDate, 
					Double.class);
		
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(100.00, result.get(0).getValue());
	}

	@Test
	public void testInputFunctionIV() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		ContractRecord contract = newContract(aonContext, startDate, Collections.emptyMap());

		Date _10date = add(startDate, DAY_OF_MONTH, 9);
		Date _11date = add(startDate, DAY_OF_MONTH, 10);
		addData(aonContext, contract, startDate, _10date, new HashMap<String,String>(){
			{
				put("X", "100");
			}
		});
		addData(aonContext, contract, _11date, endDate, new HashMap<String,String>(){
			{
				put("X", "200");
			}
		});

		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		//@formatter:on
		
		
		List<ITimedResult<Double>> result = ctx.getExpressionContext().eval("INPUT('X * DIAS_TRABAJADOS / DIAS_MES','Hello World!!!');", 
					startDate
					,endDate, 
					Double.class);
		
		Assert.assertEquals(2, result.size());
		Assert.assertEquals(100.00 * 10 / get(endDate, DAY_OF_MONTH) , result.get(0).getValue());
		Assert.assertEquals(200.00 * (get(endDate, DAY_OF_MONTH) -10)/ get(endDate, DAY_OF_MONTH) , result.get(1).getValue());
		
	}
	
	@Test
	public void testInputFunctionVI() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				newContract(aonContext, getToday(), Collections.emptyMap()));
		//@formatter:on
		
		try {
			ctx.getExpressionContext().eval("INPUT('/*user*//**/','Hello World!!!');", 
					startDate
					,endDate, 
					Object.class);
		
		} catch ( CheckException e ) {
				System.out.println(e.getMessage());
				Assert.assertEquals("Hello World!!!", e.getMessage());
				return;
		} 
		Assert.fail();
	}
	
	@Test
	public void testInputFunctionVII() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				newContract(aonContext, getToday(), Collections.emptyMap()));
		//@formatter:on
		
		try {
			ctx.getExpressionContext().eval("INPUT('/*user*/    /**/','Hello World!!!');", 
					startDate
					,endDate, 
					Object.class);
		
		} catch ( CheckException e ) {
				System.out.println(e.getMessage());
				Assert.assertEquals("Hello World!!!", e.getMessage());
				return;
		} 
		Assert.fail();
	}

	@Test
	public void testInputFunctionVIII() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				newContract(aonContext, getToday(), Collections.emptyMap()));
		//@formatter:on
		
		List<ITimedResult<Double>> result =  ctx.getExpressionContext().eval("INPUT('/*user*/ 100.00/**/','Hello World!!!');", 
				startDate
				,endDate, 
				Double.class);
	
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(100.00, result.get(0).getValue());
	}

	@Test
	public void testInputFunctionIX() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				newContract(aonContext, add(getToday(), Calendar.YEAR, -5), Collections.emptyMap()));
		//@formatter:on
		
		List<ITimedResult<Double>> result =  ctx.getExpressionContext().eval("INPUT('/*user*/ANTIGÜEDAD(100.00, TRIENIO)/**/','Hello World!!!');", 
				startDate
				,endDate, 
				Double.class);
	
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(100.00, result.get(0).getValue());
	}

	
	@Test
	public void testFractionFunctionIX() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		ContractRecord contract = newContract(aonContext, add(getToday(), Calendar.YEAR, -5), Collections.emptyMap());
		
		Date startIT = add(startDate, DAY_OF_MONTH, 10);
		Date endIT = add(startIT, DAY_OF_MONTH, 9);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, 
				startIT, 
				endIT, 
				null);
		
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		//@formatter:on
		
		List<ITimedResult<Double>> results =  ctx.getExpressionContext().eval("FRACCIONAR(1000.00)", 
				startDate
				,endDate, 
				Double.class);
	
		Assert.assertEquals(2, results.size());
		
		double monthDays = get(endDate, DAY_OF_MONTH);
		double workedDays = monthDays - 10;
		
		Assert.assertEquals(startDate, results.get(0).getPeriod().getStart());
		Assert.assertEquals(add(startIT, DAY_OF_MONTH,-1), results.get(0).getPeriod().getEnd());
		Assert.assertEquals(1000.00*10/workedDays, results.get(0).getValue());

		Assert.assertEquals(add(endIT, DAY_OF_MONTH,1), results.get(1).getPeriod().getStart());
		Assert.assertEquals(endDate, results.get(1).getPeriod().getEnd());
		Assert.assertEquals(1000.00*(monthDays-20)/workedDays, results.get(1).getValue());
	}

	@Test
	public void testFractionFunctionX() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		ContractRecord contract = newContract(aonContext, add(getToday(), Calendar.YEAR, -5), Collections.emptyMap());
		
		
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		//@formatter:on
		
		List<ITimedResult<Double>> results =  ctx.getExpressionContext().eval("FRACCIONAR(1000.00)", 
				startDate
				,endDate, 
				Double.class);
	
		Assert.assertEquals(1, results.size());
		
		Assert.assertEquals(startDate, results.get(0).getPeriod().getStart());
		Assert.assertEquals(endDate, results.get(0).getPeriod().getEnd());
		Assert.assertEquals(1000.00, results.get(0).getValue());

	}
	
	@Test
	public void testSumFunction() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSalaries(aonContext);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		ContractRecord contract = newContract(aonContext, add(getToday(), Calendar.YEAR, -5), Collections.emptyMap());
		
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		//@formatter:on
		
		List<ITimedResult<Double>> results =  ctx.getExpressionContext().eval("SUM(\"BASE_CGC\")", 
				startDate
				,endDate, 
				Double.class);
	
		Assert.assertEquals(startDate, results.get(0).getPeriod().getStart());
		Assert.assertEquals(endDate, results.get(0).getPeriod().getEnd());
		Assert.assertEquals(0.00, results.get(0).getValue());
		
	}

	@Test
	public void testSumFunctionI() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSalaries(aonContext);
		
		Date firsDayOfMonth = getFirstDayOfMonth(getToday());
		Date lastDayOfMonth = getLastDayOfMonth(getToday());
		
		
		Date endDate = add(firsDayOfMonth, DAY_OF_MONTH, 9);
		ContractRecord contract = newContract(aonContext, "03", firsDayOfMonth, endDate);
		
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
						firsDayOfMonth, 
						lastDayOfMonth, 
						endDate, 
						contract);
		//@formatter:on
		calculateAndSave(connection, ctx);
		
		Date startDate = add(endDate, DAY_OF_MONTH, 5);
		
		contract = newContract(aonContext, "03", startDate, lastDayOfMonth);
		
		//@formatter:off
		ctx = getContractSalaryCalculatorContext(connection, 
						firsDayOfMonth, 
						lastDayOfMonth, 
						endDate, 
						contract);
		//@formatter:on
		
		List<ITimedResult<Double>> results =  ctx.getExpressionContext().eval("SUM(\"BASE_CGC\")", 
				startDate
				,lastDayOfMonth, 
				Double.class);
	
		Assert.assertEquals(1750.00 * 10 / 30, results.get(0).getValue(), DELTA);
		
	}
	
	@Test
	public void testSumFunctionII() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSalaries(aonContext);
		
		Date firsDayOfMonth = getFirstDayOfMonth(getToday());
		Date lastDayOfMonth = getLastDayOfMonth(getToday());
		
		
		Date endDate = add(firsDayOfMonth, DAY_OF_MONTH, 9);
		ContractRecord contract = newContract(aonContext, "03", firsDayOfMonth, endDate);
		
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
						firsDayOfMonth, 
						lastDayOfMonth, 
						endDate, 
						contract);
		//@formatter:on
		calculateAndSave(connection, ctx);
		
		Date startDate = add(endDate, DAY_OF_MONTH, 5);
		
		contract = newContract(aonContext, "03", startDate, lastDayOfMonth);
		
		//@formatter:off
		ctx = getContractSalaryCalculatorContext(connection, 
						firsDayOfMonth, 
						lastDayOfMonth, 
						endDate, 
						contract);
		//@formatter:on
		
		List<ITimedResult<Double>> results =  ctx.getExpressionContext().eval("SUM(\"BASE_CGC\")", 
				startDate
				,lastDayOfMonth, 
				Double.class);
	
		Assert.assertEquals(1750.00 * 10 / 30, results.get(0).getValue(), DELTA);
	
		calculateAndSave(connection, ctx);
		
		// TODO: Esto es necesario?
		// contract = newContract(aonContext, "03", startDate, lastDayOfMonth);
		
		//@formatter:off
		ctx = getContractSalaryCalculatorContext(connection, 
						firsDayOfMonth, 
						lastDayOfMonth, 
						endDate, 
						contract);
		//@formatter:on
		
		results =  ctx.getExpressionContext().eval("SUM(\"BASE_CGC\")", 
				startDate
				,lastDayOfMonth, 
				Double.class);
	
		Assert.assertEquals(1750.00 * 10 / 30, results.get(0).getValue(), DELTA);
		
	}
	
	@Test
	public void testSumFunctionIII() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSalaries(aonContext);
		
		Date firsDayOfMonth = getFirstDayOfMonth(getToday());
		Date lastDayOfMonth = getLastDayOfMonth(getToday());
		
		
		Date endDate = add(firsDayOfMonth, DAY_OF_MONTH, 9);
		ContractRecord contract = newContract(aonContext, "03", firsDayOfMonth, endDate);
		
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
						firsDayOfMonth, 
						lastDayOfMonth, 
						endDate, 
						contract);
		//@formatter:on
		calculateAndSave(connection, ctx);
		
		Date startDate = add(endDate, DAY_OF_MONTH, 5);
		
		contract = newContract(aonContext, "03", startDate, lastDayOfMonth);
		
		//@formatter:off
		ctx = getContractSalaryCalculatorContext(connection, 
						firsDayOfMonth, 
						lastDayOfMonth, 
						endDate, 
						contract);
		//@formatter:on
		
		List<ITimedResult<Double>> results =  ctx.getExpressionContext().eval("SUM(\"BASE_CGC\")", 
				startDate
				,lastDayOfMonth, 
				Double.class);
	
		Assert.assertEquals(1750.00 * 10 / 30, results.get(0).getValue(), DELTA);
	
		calculateAndSave(connection, ctx);
		
		// TODO: Esto es necesario?
		// contract = newContract(aonContext, "03", startDate, lastDayOfMonth);
		
		//@formatter:off
		ctx = getContractSalaryCalculatorContext(connection, 
						firsDayOfMonth, 
						lastDayOfMonth, 
						endDate, 
						contract);
		//@formatter:on
		
		results =  ctx.getExpressionContext().eval("SUM(\"BASE_CGC\")", 
				startDate
				,lastDayOfMonth, 
				Double.class);
	
		Assert.assertEquals(1750.00 * 10 / 30, results.get(0).getValue(), DELTA);
		
	}
	
	@Test
	public void testOnAccountAgreement() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		ContractRecord contract = newContract(aonContext, add(getToday(), Calendar.YEAR, -5), Collections.emptyMap());
		
		
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		//@formatter:on
		
		List<ITimedResult<Double>> results =  ctx.getExpressionContext().eval("A_CUENTA_CONVENIO(1000.00)", 
				startDate
				,endDate, 
				Double.class);
	
		Assert.assertEquals(1, results.size());
		
		Assert.assertEquals(startDate, results.get(0).getPeriod().getStart());
		Assert.assertEquals(endDate, results.get(0).getPeriod().getEnd());
		Assert.assertEquals(1000.00, results.get(0).getValue());

	}

	@Test
	public void testSectionFunctionI() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSalaries(aonContext);
		
		Date firsDayOfYear = getFirstDayOfYear(getToday());
		Date firsDayOfMonth = getFirstDayOfMonth(getToday());
		Date lastDayOfMonth = getLastDayOfMonth(getToday());
		
		int month = get(firsDayOfMonth, Calendar.MONTH);
		
		Date endDate = add(firsDayOfMonth, DAY_OF_MONTH, 9);
		ContractRecord contract = newContract(aonContext,
				
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						String.format("TRAMO(FECHA(2020,%s,10)); BASE_CGC * 0.10", month+1), 
						}
			);
		
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
						firsDayOfMonth, 
						lastDayOfMonth, 
						lastDayOfMonth, 
						contract);
		//@formatter:on
		calculateAndSave(connection, ctx);
		
		AON.getSalaryData(aonContext, props -> props.getContractProperty().eq(contract.getId()))
		.forEach( salary -> {
			List<ContextData> salaryHours = salary.getContextData().get("HORAS_NOMINA");
			org.junit.Assert.assertEquals(2, salaryHours.size());
			salaryHours.sort((s1,s2) -> s1.getStartDate().compareTo(s2.getStartDate()));
			salaryHours.forEach( h -> System.out.println("HORAS_NOMINA :" + h.getExpression() ) );
			org.junit.Assert.assertEquals(salaryHours.get(0).getStartDate(), firsDayOfMonth);
			org.junit.Assert.assertEquals(salaryHours.get(0).getEndDate(), add(firsDayOfMonth, DAY_OF_MONTH,9));
			org.junit.Assert.assertEquals(salaryHours.get(1).getStartDate(), add(firsDayOfMonth, DAY_OF_MONTH,10));
			org.junit.Assert.assertEquals(salaryHours.get(1).getEndDate(), lastDayOfMonth);
		});
		;
		
		
	}
	//------------------------------------------------------------------------
	
	protected ContractRecord newContract(AONContext aonContext, String quoteGroup, Date startDate, Date endDate ) {
		DomainRecord domain = newDomain(aonContext);
		
		ScopeRecord scope = newScope(aonContext, domain.getId());

		EnterpriseActivityRecord enterpriseActivity = newEnterpriseActivity(
				aonContext, 
				domain.getId(), 
				scope.getId(), 
				SSRegimeType.GENERAL);

		EnterpriseCccRecord enterpriseCcc = newEnterpriseCcc(aonContext, 
				domain.getId(), 
				scope.getId(), 
				enterpriseActivity.getId(), 
				CCCType.PRINCIPAL,
				"0123456789" );

		WorkplaceRecord workplace = newWorkplace(aonContext, 
				domain.getId(), 
				scope.getId(), 
				enterpriseActivity.getEnterprise());

		RegistryRecord person = newPerson(
				aonContext, 
				domain.getId(),
				"00000000B");


		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				SSRegimeType.GENERAL, 
				CCCType.PRINCIPAL,			
				startDate, //getFirstDayOfYear(getToday()),
				endDate,
				new HashMap<String, String>() {
					{
						//put(MONTH_DAYS.getName(), String.format("%f", 30.00));
						put(QUOTE_GROUP.getName(), String.format("'%s'", quoteGroup));
						put(MONTH_DAYS.getName(), String.format("{'%s':30}[%s]", quoteGroup, QUOTE_GROUP.getName()));
						
					}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				}, 
				new String[] {						
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				"BASE_IRPF * PORCENTAJE_IRPF/100" ,
				},
				null
				,
				domain.getId(), 			//domainId, 
				person.getId(),				//personId, 
				workplace.getId(),			//workplaceId, 
				enterpriseCcc.getId(),		//enterpriseCccId,
				enterpriseActivity.getId()	//enterpriseActivityId
				);
		

		//@formatter:on
		return contract;
	}
	
	private static int calculateAndSave(Connection connection,
			ISQLContractSalaryCalculatorContext ctx) throws SalaryException {
		JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder(connection);
		new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		return jooqSalaryBuilder.execute();
	}
	
	private static final void cleanSalaries(AONContext aonContext) {
		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=0");
		
		aonContext.getDslContext().delete(SALARY_BONUS).execute();
		aonContext.getDslContext().delete(SALARY_EMBARGO).execute();
		aonContext.getDslContext().delete(SALARY_COST).execute();
		aonContext.getDslContext().delete(SALARY_DEDUCTION).execute();
		aonContext.getDslContext().delete(SALARY_PAYMENT).execute();
		aonContext.getDslContext().delete(SALARY_DATA).execute();
		aonContext.getDslContext().delete(SALARY).execute();

		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=1");
	}
	
}
