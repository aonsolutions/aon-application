/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.AGREEMENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FRIDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_GROUP;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SATURDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SUNDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SYSTEM;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.THURSDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TUESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WEDNESDAY_HOURS;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getMax;
import static java.lang.String.format;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

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
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.CheckException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.RemoveException;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;
import com.esferalia.aon.watson.util.AonDateUtils;

import static org.junit.jupiter.api.Assertions.*;

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
		assertEquals(today, new Date(date.get(0).getValue().getTime()));
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
		assertEquals(getLastDayOfMonth(today), new Date(date.get(0).getValue().getTime()));
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
		
		assertEquals(add(today, Calendar.MONTH, 1), new Date(date.get(0).getValue().getTime()));

		List<ITimedResult<Integer>>  month = 
				ctx.getExpressionContext().eval(
						String.format("MES(FECHA(%d,%d,%d))", 
								get(today, Calendar.YEAR),
								get(today, Calendar.MONTH)+1,
								get(today, Calendar.DATE)
						)
						, startDate
						, endDate, Integer.class);
		
		assertEquals(get(today, Calendar.MONTH)+1, (int)month.get(0).getValue());
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
		
		assertEquals(add(today, Calendar.YEAR, 1), new Date(date.get(0).getValue().getTime()));

		List<ITimedResult<Integer>>  month = 
				ctx.getExpressionContext().eval(
						String.format("AÑO(FECHA(%d,%d,%d))", 
								get(today, Calendar.YEAR),
								get(today, Calendar.MONTH)+1,
								get(today, Calendar.DATE)
						)
						, startDate
						, endDate, Integer.class);
		
		assertEquals(get(today, Calendar.YEAR), (int)month.get(0).getValue());
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
		
		assertEquals(add(today, Calendar.DAY_OF_MONTH, 1), new Date(date.get(0).getValue().getTime()));

		List<ITimedResult<Integer>>  month = 
				ctx.getExpressionContext().eval(
						String.format("DIA(FECHA(%d,%d,%d))", 
								get(today, Calendar.YEAR),
								get(today, Calendar.MONTH)+1,
								get(today, Calendar.DATE)
						)
						, startDate
						, endDate, Integer.class);
		
		assertEquals(get(today, Calendar.DATE), (int)month.get(0).getValue());
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

		assertEquals(Boolean.FALSE, compare.get(0).getValue());

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
		

		assertEquals(10, (int)days.get(0).getValue());

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
		

		assertEquals(100, (int)days.get(0).getValue());

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
		

		assertEquals(1, firstDayOfMonth.size());
		assertEquals(getFirstDayOfMonth(today), firstDayOfMonth.get(0).getValue());


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
		

		assertEquals(1, lastDayOfMonth.size());
		assertEquals(getLastDayOfMonth(getToday()), lastDayOfMonth.get(0).getValue());


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
		

		assertEquals(1, lastDayOfYear.size());
		assertEquals(AonDateUtils.getLastDayOfYear(getToday()), lastDayOfYear.get(0).getValue());


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
		

		assertEquals(1, firstDayOfYear.size());
		assertEquals(AonDateUtils.getFirstDayOfYear(today), firstDayOfYear.get(0).getValue());


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
		.forEach(result-> assertEquals((double) getMax(end, DAY_OF_MONTH), result.getValue(), String.format("%s('%s')", SYSTEM, MONTH_DAYS)));
		

		ctx.getExpressionContext().eval(String.format("%s", MONTH_DAYS), getToday(), end, Double.class)
		.stream()
		.forEach(result-> assertEquals(666.00, result.getValue(), String.format("%s", MONTH_DAYS)));
	}

	@Test
	public void testSystemFunctionAtContractData() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, getToday(),
				new HashMap<String, String>() {
					{
						put(MONTH_DAYS.getName(), "666.00");
						put("SYS_DIAS_MES", String.format("%s('%s')", SYSTEM, MONTH_DAYS));
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
		
		ctx.getExpressionContext().eval("SYS_DIAS_MES", getToday(), end, Double.class)
		.stream()
		.forEach(result-> assertEquals((double) getMax(end, DAY_OF_MONTH), result.getValue(), String.format("%s('%s')", SYSTEM, MONTH_DAYS)));
		

		ctx.getExpressionContext().eval(String.format("%s", MONTH_DAYS), getToday(), end, Double.class)
		.stream()
		.forEach(result-> assertEquals(666.00, result.getValue(), String.format("%s", MONTH_DAYS)));
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
		.forEach(result-> assertEquals((double) getMax(end, DAY_OF_MONTH), result.getValue(), String.format("%s('%s')", SYSTEM, MONTH_DAYS)));
		ctx.getExpressionContext().eval(String.format("%s('%s')", AGREEMENT, MONTH_DAYS), getToday(), end, Double.class)
		.stream()
		.forEach(result-> assertEquals(69.00, result.getValue(), String.format("%s('%s')", AGREEMENT, MONTH_DAYS)));
		ctx.getExpressionContext().eval(String.format("%s", MONTH_DAYS), getToday(), end, Double.class)
		.stream()
		.forEach(result-> assertEquals(666.00, result.getValue(), String.format("%s", MONTH_DAYS)));
		
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
		.forEach(result-> assertEquals((double) getMax(end, DAY_OF_MONTH), result.getValue(), String.format("%s('%s')", SYSTEM, MONTH_DAYS)));
		ctx.getExpressionContext().eval(String.format("%s('%s')", AGREEMENT, MONTH_DAYS), getToday(), end, Double.class)
		.stream()
		.forEach(result-> assertEquals(96.00, result.getValue(), String.format("%s('%s')", AGREEMENT, MONTH_DAYS)));
		ctx.getExpressionContext().eval(String.format("%s", MONTH_DAYS), getToday(), end, Double.class)
		.stream()
		.forEach(result-> assertEquals(666.00, result.getValue(), String.format("%s", MONTH_DAYS)));

	
		startDate = getToday();
		endDate = getLastDayOfMonth(startDate);
		addData(aonContext, category.getDomain(), category, startDate, endDate, 			
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
		.forEach(result-> assertEquals((double) getMax(end, DAY_OF_MONTH), result.getValue(), String.format("%s('%s')", SYSTEM, MONTH_DAYS)));
		ctx.getExpressionContext().eval(String.format("%s('%s')", AGREEMENT, MONTH_DAYS), getToday(), end, Double.class)
		.stream()
		.forEach(result-> assertEquals(99.00, result.getValue(), String.format("%s('%s')", AGREEMENT, MONTH_DAYS)));
		ctx.getExpressionContext().eval(String.format("%s", MONTH_DAYS), getToday(), end, Double.class)
		.stream()
		.forEach(result-> assertEquals(666.00, result.getValue(), String.format("%s", MONTH_DAYS)));
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
				assertEquals(e.getMessage(), "Hello World!!!");
				return;
		} 
		fail();
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
				assertEquals(e.getVariableNames()[0], "NO_DEFINIDA");
				return;
		} 
		fail();
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
		
		assertEquals(1, result.size());
		assertEquals(100.00, result.get(0).getValue());
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
		
		assertEquals(2, result.size());
		assertEquals(100.00 * 10 / get(endDate, DAY_OF_MONTH) , result.get(0).getValue());
		assertEquals(200.00 * (get(endDate, DAY_OF_MONTH) -10)/ get(endDate, DAY_OF_MONTH) , result.get(1).getValue());
		
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
				assertEquals(e.getMessage(), "Hello World!!!");
				return;
		} 
		fail();
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
				assertEquals(e.getMessage(), "Hello World!!!");
				return;
		} 
		fail();
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
	
		assertEquals(1, result.size());
		assertEquals(100.00, result.get(0).getValue());
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
	
		assertEquals(1, result.size());
		assertEquals(100.00, result.get(0).getValue());
	}

	@Test
	public void testInputFunctionX() throws ExpressionException, SQLException {

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
		
		try {
 			List<ITimedResult<Double>> result =  ctx.getExpressionContext().eval("INPUT('/*user*/ANTIGÜEDAD(100.00, TRIENIO)/**/','Hello World!!!');REMOVE();", 
					startDate
					,endDate, 
					Double.class);
			fail();
		} catch ( RemoveException e ) {
			
		}
	
		try {
 			List<ITimedResult<Double>> result =  ctx.getExpressionContext().eval("INPUT(\"/*user*/ANTIGÜEDAD(100.00, TRIENIO)/**/\",\"Hello World!!!\");REMOVE();", 
					startDate
					,endDate, 
					Double.class);
			fail();
		} catch ( RemoveException e ) {
			
		}

		try {
 			List<ITimedResult<Double>> result =  ctx.getExpressionContext().eval("INPUT(\"/*user*/ANTIGÜEDAD(100.00, TRIENIO)/**/\",DIAS_MES);REMOVE();", 
					startDate
					,endDate, 
					Double.class);
			fail();
		} catch ( RemoveException e ) {
			
		}

	}
	
	@Test
	public void testInputFunctionXI() throws ExpressionException, SQLException {

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
		
		List<ITimedResult<Double>> result =  ctx.getExpressionContext().eval("INPUT(' /*user*/(1.00) * ANTIGÜEDAD(100.00, TRIENIO)/**/ '   ,  ' Hello World!!!'  );", 
				startDate
				,endDate, 
				Double.class);
	
		assertEquals(1, result.size());
		assertEquals(100.00, result.get(0).getValue());

		result =  ctx.getExpressionContext().eval("INPUT(\" /*user*/(1.00) * ANTIGÜEDAD(100.00, TRIENIO)/**/ \"   ,  \" Hello World!!!\"  );", 
				startDate
				,endDate, 
				Double.class);
	
		assertEquals(1, result.size());
		assertEquals(100.00, result.get(0).getValue());
	
		result =  ctx.getExpressionContext().eval("INPUT(\" /*user*/(1.00) * ANTIGÜEDAD(100.00, TRIENIO)/**/ \"   ,  ' Hello World!!!' );", 
				startDate
				,endDate, 
				Double.class);
	
		assertEquals(1, result.size());
		assertEquals(100.00, result.get(0).getValue());
	
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
	
		assertEquals(2, results.size());
		
		double monthDays = get(endDate, DAY_OF_MONTH);
		double workedDays = monthDays - 10;
		
		assertEquals(startDate, results.get(0).getPeriod().getStart());
		assertEquals(add(startIT, DAY_OF_MONTH,-1), results.get(0).getPeriod().getEnd());
		assertEquals(1000.00*10/workedDays, results.get(0).getValue());

		assertEquals(add(endIT, DAY_OF_MONTH,1), results.get(1).getPeriod().getStart());
		assertEquals(endDate, results.get(1).getPeriod().getEnd());
		assertEquals(1000.00*(monthDays-20)/workedDays, results.get(1).getValue());
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
	
		assertEquals(1, results.size());
		
		assertEquals(startDate, results.get(0).getPeriod().getStart());
		assertEquals(endDate, results.get(0).getPeriod().getEnd());
		assertEquals(1000.00, results.get(0).getValue());

	}
	
	@Test
	public void testFractionFunctionXI() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		ContractRecord contract = newContract(aonContext, add(getToday(), Calendar.YEAR, -5), Collections.emptyMap());
		
		addData(aonContext, contract, startDate, endDate, "KILOMETROS", "10.00");
//		
		
		Date startOffDate = add(startDate, Calendar.DAY_OF_MONTH, 10);
		Date endOffDate = add(startOffDate, Calendar.DAY_OF_MONTH, 9);
		addData(aonContext, contract, startOffDate, endOffDate, ContextVariable.OFF_DAYS, "10.00");
		
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		//@formatter:on
		
//		ctx.getExpressionContext().eval("KILOMETROS=FRACCIONAR(CONTEXT,KILOMETROS)", 
//				startDate
//				,endDate, 
//				Double.class);

		ctx.getExpressionContext().eval("KMS=FRACCIONAR(CONTEXT,KILOMETROS)", 
				startDate
				,endDate, 
				Double.class);
	
		List<ITimedResult<Double>> results =  ctx.getExpressionContext().eval("KMS", 
				startDate
				,endDate, 
				Double.class);
		assertEquals(2, results.size());
		
		assertEquals(startDate, results.get(0).getPeriod().getStart());
		assertEquals(add(startOffDate, Calendar.DAY_OF_MONTH, -1), results.get(0).getPeriod().getEnd());

		assertEquals(add(endOffDate, Calendar.DAY_OF_MONTH, 1), results.get(1).getPeriod().getStart());
		assertEquals(endDate, results.get(1).getPeriod().getEnd());
		
		assertEquals(10.00, results.get(0).getValue() + results.get(1).getValue() /*+ results.get(2).getValue()*/);

	}

	@Test
	public void testFractionFunctionXII() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date firstDayOfYear = getFirstDayOfYear(getToday());
		
		ContractRecord contract = newContract(
				aonContext, 
				firstDayOfYear, 
				Collections.emptyMap());
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MILLISECOND,0);
		calendar.set(Calendar.DAY_OF_MONTH,1);
		calendar.set(Calendar.MONTH,Calendar.APRIL);
		
		Date aprilStart = new Date(calendar.getTimeInMillis());
		calendar.set(Calendar.DAY_OF_MONTH, 15);
		Date april15 =  new Date(calendar.getTimeInMillis());
		
		calendar.set(Calendar.DAY_OF_MONTH, 16);
		Date april16 =  new Date(calendar.getTimeInMillis());
		
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date aprilEnd = new Date(calendar.getTimeInMillis());

//		addData(aonContext, contract, firstDayOfYear, april15, new HashMap<String, String>(){
//			{
//				put(ContextVariable.MONDAY_HOURS.getName(), "4.00");
//				put(ContextVariable.TUESDAY_HOURS.getName(), "4.00");
//				put(ContextVariable.WEDNESDAY_HOURS.getName(), "4.00");
//				put(ContextVariable.THURSDAY_HOURS.getName(), "4.00");
//				put(ContextVariable.FRIDAY_HOURS.getName(), "4.00");
//			}
//		});
//		
//		addData(aonContext, contract, april16, null, new HashMap<String, String>(){
//			{
//				put(ContextVariable.MONDAY_HOURS.getName(), "2.00");
//				put(ContextVariable.TUESDAY_HOURS.getName(), "2.00");
//				put(ContextVariable.WEDNESDAY_HOURS.getName(), "2.00");
//				put(ContextVariable.THURSDAY_HOURS.getName(), "2.00");
//				put(ContextVariable.FRIDAY_HOURS.getName(), "2.00");
//			}
//		});
		
		addData(aonContext, contract, firstDayOfYear, april15, new HashMap<String, String>(){
			{
				put(ContextVariable.PARTIAL_FACTOR.getName(), "0.50");
			}
		});
		
		addData(aonContext, contract, april16, null, new HashMap<String, String>(){
			{
				put(ContextVariable.PARTIAL_FACTOR.getName(), "0.25");
			}
		});
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				aprilStart, 
				aprilEnd, 
				aprilEnd, 
				contract);
		//@formatter:on
		
		List<ITimedResult<Double>> results =  ctx.getExpressionContext().eval("FRACCIONAR(1000.00)", 
				aprilStart
				,aprilEnd, 
				Double.class);
	
		assertEquals(2, results.size());
		
		assertEquals(aprilStart, results.get(0).getPeriod().getStart());
		assertEquals(april15, results.get(0).getPeriod().getEnd());
		assertEquals(1000.00/3*2, results.get(0).getValue());

		assertEquals(april16, results.get(1).getPeriod().getStart());
		assertEquals(aprilEnd, results.get(1).getPeriod().getEnd());
		assertEquals(1000.00/3, results.get(1).getValue());
	}

	@Test
	@Disabled
	public void testFractionFunctionFebruary() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date firstDayOfYear = getFirstDayOfYear(getToday());
		
		ContractRecord contract = newContract(
				aonContext, 
				firstDayOfYear, 
				Collections.emptyMap());
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MILLISECOND,0);
		calendar.set(Calendar.DAY_OF_MONTH,1);
		calendar.set(Calendar.MONTH,Calendar.FEBRUARY);
		
		Date februaryStart = new Date(calendar.getTimeInMillis());
		calendar.set(Calendar.DAY_OF_MONTH, 11);
		Date february11 =  new Date(calendar.getTimeInMillis());
		
		calendar.set(Calendar.DAY_OF_MONTH, 12);
		Date february12 =  new Date(calendar.getTimeInMillis());
		
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date februaryEnd = new Date(calendar.getTimeInMillis());
		
		addData(aonContext, contract, firstDayOfYear, null, Collections.singletonMap(ContextVariable.MONTH_DAYS.getName(), "30.0"));
		addData(aonContext, contract, firstDayOfYear, february11, Collections.singletonMap(ContextVariable.PARTIAL_FACTOR.getName(), "0.80"));
		addData(aonContext, contract, february12, null, Collections.singletonMap(ContextVariable.PARTIAL_FACTOR.getName(), "0.70"));
		
		

		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				februaryStart, 
				februaryEnd, 
				februaryEnd, 
				contract);
		//@formatter:on
		
		List<ITimedResult<Double>> results =  ctx.getExpressionContext().eval("FRACCIONAR(1000.00)", 
				februaryStart
				,februaryEnd, 
				Double.class);
	
		assertEquals(2, results.size());
		
		assertEquals(februaryStart, results.get(0).getPeriod().getStart());
		assertEquals(february11, results.get(0).getPeriod().getEnd());

		assertEquals(february12, results.get(1).getPeriod().getStart());
		assertEquals(februaryEnd, results.get(1).getPeriod().getEnd());
		
		System.out.println("1-. " + results.get(0).getValue() );
		System.out.println("2-. " + results.get(1).getValue() );
		
		assertEquals(1000.00 , results.get(0).getValue() + results.get(1).getValue() );
		
		assertEquals(1000.00/29 * 11, results.get(0).getValue());
		assertEquals(1000.00/29 * 18, results.get(1).getValue());
	}

	@Test
	public void testFractionFunctionXIII() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date firstDayOfMonth = getFirstDayOfMonth(getToday());
		Date startDate = add(firstDayOfMonth, Calendar.DAY_OF_MONTH, 16);
		Date endDate = getLastDayOfMonth(startDate);
		ContractRecord contract = newContract(aonContext, startDate, Collections.singletonMap(ContextVariable.PARTIAL_FACTOR.getName(), "0.50"));
		
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				getFirstDayOfMonth(startDate), 
				getLastDayOfMonth(endDate), 
				getLastDayOfMonth(endDate), 
				contract);
		//@formatter:on
		
		List<ITimedResult<Double>> results =  
		ctx.getExpressionContext().eval("FRACCIONAR(1000.00)", 
				getFirstDayOfMonth(startDate)
				,getLastDayOfMonth(endDate), 
				Double.class);
	
		assertEquals(1, results.size());
		
		assertEquals(startDate, results.get(0).getPeriod().getStart());
		assertEquals(endDate, results.get(0).getPeriod().getEnd());
		assertEquals(1000.00 , results.get(0).getValue());

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
	
		assertEquals(startDate, results.get(0).getPeriod().getStart());
		assertEquals(endDate, results.get(0).getPeriod().getEnd());
		assertEquals(0.00, results.get(0).getValue());
		
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
	
		assertEquals(1750.00 * 10 / 30, results.get(0).getValue(), DELTA);
		
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
	
		assertEquals(1750.00 * 10 / 30, results.get(0).getValue(), DELTA);
	
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
	
		assertEquals(1750.00 * 10 / 30, results.get(0).getValue(), DELTA);
		
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
	
		assertEquals(1750.00 * 10 / 30, results.get(0).getValue(), DELTA);
	
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
	
		assertEquals(1750.00 * 10 / 30, results.get(0).getValue(), DELTA);
		
	}
	
	@Test
	public void testSumFunctionIV() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		ContractRecord contract = newContract(aonContext, add(getToday(), Calendar.YEAR, -5), Collections.emptyMap());
		
		double sum = 0.00;
		for ( Date date = startDate; date.compareTo(endDate) <= 0 ; date = add(date, DAY_OF_MONTH,3)) {
		    sum++;
		    addData(aonContext, contract, date, date, "UNA_VARIABLE", "1");
		    
		}
		
		
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		//@formatter:on
		
		List<ITimedResult<Double>> results =  ctx.getExpressionContext().eval("SUM(UNA_VARIABLE)", 
				startDate
				,endDate, 
				Double.class);
	
		assertEquals(startDate, results.get(0).getPeriod().getStart());
		assertEquals(endDate, results.get(0).getPeriod().getEnd());
		assertEquals(sum, results.get(0).getValue());
		
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
	
		assertEquals(1, results.size());
		
		assertEquals(startDate, results.get(0).getPeriod().getStart());
		assertEquals(endDate, results.get(0).getPeriod().getEnd());
		assertEquals(1000.00, results.get(0).getValue());

	}

	@Test
	public void testCalcCompensations() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		ContractRecord contract = newContract(aonContext, add(getToday(), Calendar.YEAR, -5), Collections.emptyMap());
		
		
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
			getSQLContractSettleContext(connection, 
			contract.getStartDate(), 
			getToday(), 
			contract);
		//@formatter:on
		
		Date startDate = contract.getStartDate(); //getFirstDayOfMonth(getToday());
		Date endDate = getToday(); //getLastDayOfMonth(getToday());

		List<ITimedResult<Object>> results =  ctx.getExpressionContext().eval("CGPJ_INDEMNIZACIONES = CALCULO_INDEMNIZACIONES(INICIO_NOMINA, FIN_NOMINA, 66.66);", 
				startDate,
				endDate, 
				Object.class);
		
		assertEquals(1, results.size());
		assertEquals(endDate, results.get(0).getPeriod().getEnd());
		assertEquals(startDate, results.get(0).getPeriod().getStart());
		
		Map<Integer,Map<String,Object>> compensations = (Map<Integer,Map<String,Object>>) results.get(0).getValue();
		
		compensations.values().forEach( compensation -> System.out.println( 
		compensation.get("title") + " " + 
		compensation.get("description") + " " + 
		compensation.get("amount") 
		+"[" + compensation.get("days") + "]") );
		
	}

	@Test
	public void testEvalTemplate() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		ContractRecord contract = newContract(aonContext, add(getToday(), Calendar.YEAR, -5), Collections.emptyMap());
		
		
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
			getSQLContractSettleContext(connection, 
			contract.getStartDate(), 
			getToday(), 
			contract);
		//@formatter:on

		// 1. DESPIDO IMPROCEDENTE 										-- Salario diario x meses x 2,75: 11182.22[1827]
		// 2. EXTINCIÓN DEL CONTRATO POR VOLUNTAD DEL TRABAJADOR EN CASO DE INCUMPLIMIENTO GRAVE DEL EMPRESARIO -- Salario diario x meses x 2,75: 11182.22[1827]
		// 3. EXTINCIÓN POR CAUSAS OBJETIVAS PROCEDENTE Y TRABAJADOR INDEFINIDO NO FIJO 			-- Salario diario x meses x 20 / 12: 6777.1[1827]
		// 4. DESPIDO COLECTIVO PROCEDENTE 									-- Salario diario x meses x 20 / 12: 6777.1[1827]
		// 5. MOVILIDAD GEOGRÁFICA 										-- Salario diario x meses x 20 / 12: 6777.1[1827]
		// 6. MODIFICACIÓN SUSTANCIAL DE CONDICIONES DE TRABAJO 						-- Salario diario x meses x 20 / 12: 6777.1[1827]
		// 7. EXTINCIÓN DEL CONTRATO TEMPORAL - Contrato celebrado a partir del 1-1-2015 			-- Salario diario x dias x 12 / 365: 4003.98[1827]
		
		Map<Integer, Map<String, Object>> cgpjIndemnizaciones = new HashMap<>();
		Map<String, Object> indemnizacion = new HashMap<>();
		indemnizacion.put("title", "1. DESPIDO IMPROCEDENTE");
		indemnizacion.put("description", "-- Salario diario x meses x 2,75:");
		indemnizacion.put("amount", 11182.22);
		indemnizacion.put("days", 1827);
		cgpjIndemnizaciones.put(1, indemnizacion);
		indemnizacion = new HashMap<>();
		indemnizacion.put("title", "2. EXTINCIÓN DEL CONTRATO POR VOLUNTAD DEL TRABAJADOR EN CASO DE INCUMPLIMIENTO GRAVE DEL EMPRESARIO");
		indemnizacion.put("description", "-- Salario diario x meses x 2,75:");
		indemnizacion.put("amount", 11182.22);
		indemnizacion.put("days", 1827);
		cgpjIndemnizaciones.put(2, indemnizacion);
		indemnizacion = new HashMap<>();
		indemnizacion.put("title", "3. EXTINCIÓN POR CAUSAS OBJETIVAS PROCEDENTE Y TRABAJADOR INDEFINIDO NO FIJO");
		indemnizacion.put("description", "- Salario diario x meses x 20 / 12:");
		indemnizacion.put("amount", 11182.22);
		indemnizacion.put("days", 1827);
		cgpjIndemnizaciones.put(3, indemnizacion);
		indemnizacion = new HashMap<>();
		indemnizacion.put("title", "4. DESPIDO COLECTIVO PROCEDENTE");
		indemnizacion.put("description", "-- Salario diario x meses x 20 / 12:");
		indemnizacion.put("amount", 11182.22);
		indemnizacion.put("days", 1827);
		cgpjIndemnizaciones.put(4, indemnizacion);
		indemnizacion = new HashMap<>();
		indemnizacion.put("title", "5. MOVILIDAD GEOGRÁFICA");
		indemnizacion.put("description", "-- Salario diario x meses x 20 / 12:");
		indemnizacion.put("amount", 11182.22);
		indemnizacion.put("days", 1827);
		cgpjIndemnizaciones.put(5, indemnizacion);
		indemnizacion = new HashMap<>();
		indemnizacion.put("title", "6. MODIFICACIÓN SUSTANCIAL");
		indemnizacion.put("description", "-- Salario diario x meses x 20 / 12:");
		indemnizacion.put("amount", 11182.22);
		indemnizacion.put("days", 1827);
		cgpjIndemnizaciones.put(6, indemnizacion);
		indemnizacion = new HashMap<>();
		indemnizacion.put("title", "7. EXTINCIÓN DEL CONTRATO TEMPORAL");
		indemnizacion.put("description", "-- Contrato celebrado a partir del 1-1-2015 - Salario diario x dias x 12 / 365:");
		indemnizacion.put("amount", 11182.22);
		indemnizacion.put("days", 1827);
		cgpjIndemnizaciones.put(7, indemnizacion);
		
		ctx.getExpressionContext().setVariable("CGPJ_INDEMNIZACIONES", cgpjIndemnizaciones, startDate, endDate);

		try {
    		List<ITimedResult<Object>> results =  
    		ctx.getExpressionContext().eval("MSG_CGPJ_INDEMNIZACIONES='<table><tbody></tbody></table><table><tbody>@foreach{value:values}<tr><td>@{value.title} @{value.description}</td><td>@{value.amount}</td></tr>@end{}</tbody></table><div class=\\'aon-text-right\\'><span class=\\'aon-icon aon-icon-logo\\' />aon Solutions</div>';AVISO(EVAL_TEMPLATE(CGPJ_INDEMNIZACIONES,MSG_CGPJ_INDEMNIZACIONES));", 
    				startDate,
    				endDate, 
    				Object.class);
		} catch ( CheckException e ) {
	    		System.out.println(e.getMessage());
	    		return;
		}
		
		fail();
    		
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
		int year = get(firsDayOfMonth, Calendar.YEAR);
		
		Date endDate = add(firsDayOfMonth, DAY_OF_MONTH, 9);
		ContractRecord contract = newContract(aonContext,
				
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						String.format("TRAMO(FECHA(%s,%s,10)); BASE_CGC * 0.10", year , month+1), 
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
			assertEquals(2, salaryHours.size());
			salaryHours.sort((s1,s2) -> s1.getStartDate().compareTo(s2.getStartDate()));
			salaryHours.forEach( h -> System.out.println("HORAS_NOMINA :" + h.getExpression() ) );
			assertEquals(salaryHours.get(0).getStartDate(), firsDayOfMonth);
			assertEquals(salaryHours.get(0).getEndDate(), add(firsDayOfMonth, DAY_OF_MONTH,9));
			assertEquals(salaryHours.get(1).getStartDate(), add(firsDayOfMonth, DAY_OF_MONTH,10));
			assertEquals(salaryHours.get(1).getEndDate(), lastDayOfMonth);
		});
		;
		
		
	}

	
	@Test
	public void testSectionFunctionBasesMin() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSalaries(aonContext);
		
		Date firsDayOfYear = getFirstDayOfYear(getToday());
		Date firstDayOfMonth = getFirstDayOfMonth(getToday());
		Date lastDayOfMonth = getLastDayOfMonth(getToday());
		
		int month = get(firstDayOfMonth, Calendar.MONTH);
		int year = get(firstDayOfMonth, Calendar.YEAR);
		
		Date endDate = add(firstDayOfMonth, DAY_OF_MONTH, 9);
		ContractRecord contract = newContract(
			aonContext,
			firsDayOfYear,
			new HashMap<String, String>(){
				{
				put(MONTH_DAYS.getName(), "30.00");
				put(QUOTE_GROUP.getName(), "\"07\"");
				put(ContextVariable.TC2.getName(), format("\"%s\"", 
						ContractCode.C200.getValue()));

				put(MONDAY_HOURS.getName(), format("%d", 0));
				put(TUESDAY_HOURS.getName(), format("%d", 0));
				put(WEDNESDAY_HOURS.getName(), format("%d", 0));
				put(THURSDAY_HOURS.getName(), format("%d", 0));
				put(FRIDAY_HOURS.getName(), format("%d", 0));
				put(SATURDAY_HOURS.getName(), format("%d", 2));
				put(SUNDAY_HOURS.getName(), format("%d", 3));

				}
			},
			new String[] { 
				"1125.90 * DIAS_TRABAJADOS / DIAS_MES",
			}
			, new String[] {
				String.format("TRAMO(FECHA(%s,%d,1))", year,  get(firstDayOfMonth,MONTH)+1), 
			},
			null
			);
		addSystemData(aonContext, contract.getStartDate(), null, new HashMap<String, String>(){{
			put("POR_HORAS","def () { isdef CONTEXT ? UTILIZADA('HORAS_TRABAJADAS') : FALSO() }");
			put("HORAS_NOMINA_MIN","1");
			put("HORAS_NOMINA","MAX(1,FLOOR([\"07\":(POR_HORAS() ? HORAS_TRABAJADAS : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/DIAS_MES) / 6.33 * COEFICIENTE_TRABAJADO)][GRUPO_COTIZACION]))");
			put("BASE_CGC_MIN", "[ \"07\":(MAX(6.78, (POR_HORAS() ? 6.78 * HORAS_NOMINA : 1125.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD)))] [GRUPO_COTIZACION]"); 
		}});
		
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
						firstDayOfMonth, 
						lastDayOfMonth, 
						lastDayOfMonth, 
						contract);
		//@formatter:on
		calculateAndSave(connection, ctx);
		
		AON.getSalaryData(aonContext, props -> props.getContractProperty().eq(contract.getId()))
		.forEach( salary -> {
			List<ContextData> salaryHours = salary.getContextData().get("HORAS_NOMINA");
			assertEquals(2, salaryHours.size());
			salaryHours.sort((s1,s2) -> s1.getStartDate().compareTo(s2.getStartDate()));
			salaryHours.forEach( h -> System.out.println("HORAS_NOMINA :" + h.getExpression() + ", "+ h.getStartDate() ) );
			assertEquals(salaryHours.get(0).getStartDate(), firstDayOfMonth);
			assertEquals(salaryHours.get(0).getEndDate(), firstDayOfMonth);
			
			assertEquals(salaryHours.get(1).getStartDate(), add(firstDayOfMonth, DAY_OF_MONTH,1));
			assertEquals(salaryHours.get(1).getEndDate(), lastDayOfMonth);
			assertEquals(1.00, Double.parseDouble(salaryHours.get(0).getExpression()), 0.00);

			List<ContextData> baseCgc = salary.getContextData().get("BASE_CGC");
			assertEquals(2, baseCgc.size());
			baseCgc.sort((s1,s2) -> s1.getStartDate().compareTo(s2.getStartDate()));
			baseCgc.forEach( h -> System.out.println("BASE_CGC :" + h.getExpression() + ", "+ h.getStartDate() ) );
			assertEquals(baseCgc.get(0).getStartDate(), firstDayOfMonth);
			assertEquals(baseCgc.get(0).getEndDate(), firstDayOfMonth);
			assertEquals(6.78, Double.parseDouble(baseCgc.get(0).getExpression()), 0.00);
			
			assertEquals(baseCgc.get(1).getStartDate(), add(firstDayOfMonth, DAY_OF_MONTH,1));
			assertEquals(baseCgc.get(1).getEndDate(), lastDayOfMonth);
			assertEquals(5.00/40.00 * 1125.90 - 6.78, Double.parseDouble(baseCgc.get(1).getExpression()), 0.00);
		});
		;
		
		
	}

	@Test
	public void testSectionFunctionBasesMinII() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSalaries(aonContext);
		
		Date firsDayOfYear = getFirstDayOfYear(getToday());
		Date firstDayOfMonth = getFirstDayOfMonth(getToday());
		Date lastDayOfMonth = getLastDayOfMonth(getToday());
		
		int month = get(firstDayOfMonth, Calendar.MONTH);
		int year = get(firstDayOfMonth, Calendar.YEAR);
		
		Date endDate = add(firstDayOfMonth, DAY_OF_MONTH, 9);
		ContractRecord contract = newContract(
			aonContext,
			firsDayOfYear,
			new HashMap<String, String>(){
				{
				put(MONTH_DAYS.getName(), "30.00");
				put(QUOTE_GROUP.getName(), "\"07\"");
				put(ContextVariable.TC2.getName(), format("\"%s\"", 
						ContractCode.C200.getValue()));

				put(MONDAY_HOURS.getName(), format("%d", 0));
				put(TUESDAY_HOURS.getName(), format("%d", 0));
				put(WEDNESDAY_HOURS.getName(), format("%d", 0));
				put(THURSDAY_HOURS.getName(), format("%d", 0));
				put(FRIDAY_HOURS.getName(), format("%d", 0));
				put(SATURDAY_HOURS.getName(), format("%d", 2));
				put(SUNDAY_HOURS.getName(), format("%d", 3));

				}
			},
			new String[] { 
				"1125.90 * DIAS_TRABAJADOS / DIAS_MES",
			}
			, new String[] {
				String.format("TRAMO(FECHA(%s,4,29))", year ), 
			},
			null
			);
		addSystemData(aonContext, contract.getStartDate(), null, new HashMap<String, String>(){{
			put("POR_HORAS","def () { isdef CONTEXT ? UTILIZADA('HORAS_TRABAJADAS') : FALSO() }");
			put("HORAS_NOMINA_MIN","1");
			put("HORAS_NOMINA","MAX(1,FLOOR([\"07\":(POR_HORAS() ? HORAS_TRABAJADAS : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/DIAS_MES) / 6.33 * COEFICIENTE_TRABAJADO)][GRUPO_COTIZACION]))");
			put("BASE_CGC_MIN", "[ \"07\":(MAX(6.78, (POR_HORAS() ? 6.78 * HORAS_NOMINA : 1125.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD)))] [GRUPO_COTIZACION]"); 
		}});
		
		Date firstDayOfApril = add(firsDayOfYear, Calendar.MONTH,3);
		Date lastDayOfApril = getLastDayOfMonth(firstDayOfApril);
		
		
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
						firstDayOfApril, 
						lastDayOfApril,  
						lastDayOfApril, 
						contract);
		//@formatter:on
		calculateAndSave(connection, ctx);
		
		AON.getSalaryData(aonContext, props -> props.getContractProperty().eq(contract.getId()))
		.forEach( salary -> {
			List<ContextData> salaryHours = salary.getContextData().get("HORAS_NOMINA");
			assertEquals(2, salaryHours.size());
			salaryHours.sort((s1,s2) -> s1.getStartDate().compareTo(s2.getStartDate()));
			salaryHours.forEach( h -> System.out.println("HORAS_NOMINA :" + h.getExpression() + ", "+ h.getStartDate() ) );
			assertEquals(salaryHours.get(0).getStartDate(), firstDayOfApril);
			assertEquals(salaryHours.get(0).getEndDate(), add(firstDayOfApril, DAY_OF_MONTH,28));
			
			assertEquals(salaryHours.get(1).getStartDate(), lastDayOfApril);
			assertEquals(salaryHours.get(1).getEndDate(), lastDayOfApril);
			assertEquals(1.00, Double.parseDouble(salaryHours.get(1).getExpression()), 0.00);

			List<ContextData> baseCgc = salary.getContextData().get("BASE_CGC");
			assertEquals(2, baseCgc.size());
			baseCgc.sort((s1,s2) -> s1.getStartDate().compareTo(s2.getStartDate()));
			baseCgc.forEach( h -> System.out.println("BASE_CGC :" + h.getExpression() + ", "+ h.getStartDate() ) );
			assertEquals(baseCgc.get(0).getStartDate(), firstDayOfApril);
			assertEquals(baseCgc.get(0).getEndDate(), add(firstDayOfApril, DAY_OF_MONTH,28));
			assertEquals(5.00/40.00 * 1125.90 - 6.78, Double.parseDouble(baseCgc.get(0).getExpression()), 0.00);
			
			assertEquals(baseCgc.get(1).getStartDate(), lastDayOfApril);
			assertEquals(baseCgc.get(1).getEndDate(), lastDayOfApril);
			assertEquals(6.78, Double.parseDouble(baseCgc.get(1).getExpression()), 0.00);
		});
		;
		
		
	}

	@Test
	public void testScopeFunctionI() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		
		
		AgreementLevelCategoryRecord category = newAgreement(aonContext, new Extra[] {},
			new HashMap<String, String>() {
			    {
				put("SALARIO_MENSUAL", "1000.00");
			    }
			});
		
		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				newContract(
				aonContext, 
				getToday(), 
				new HashMap<String,String>(){{
				    put("DIAS_MES", "30.00");
				}} ,
				new String[0], 
				new String[0],
				category
				));
		//@formatter:on
		
		
		Date today = getToday();
		
		List<ITimedResult<ExpressionScope>>  scopes = 
				ctx.getExpressionContext().eval(
						String.format("AMBITO('%s')",
							ContextVariable.IRPF_PERCENT.getName()
						)
						, startDate
						, endDate, ExpressionScope.class );
		
		assertEquals(ExpressionScope.APPLICATION, scopes.get(0).getValue());

		ctx.getExpressionContext().eval(
			String.format("AMBITO('%s') == APPLICATION ",
				ContextVariable.IRPF_PERCENT.getName()
			)
			, startDate
			, endDate, Boolean.class )
		.forEach( r -> assertTrue(r.getValue()) );

		scopes = 
			ctx.getExpressionContext().eval(
					String.format("AMBITO('%s')",
						ContextVariable.MONTH_DAYS.getName()
					)
					, startDate
					, endDate, ExpressionScope.class );
	
		assertEquals(ExpressionScope.CONTRACT, scopes.get(0).getValue());

		ctx.getExpressionContext().eval(
			String.format("AMBITO('%s') == CONTRACT ",
				ContextVariable.MONTH_DAYS.getName()
			)
			, startDate
			, endDate, Boolean.class )
		.forEach( r -> assertTrue(r.getValue()) );

		scopes = 
			ctx.getExpressionContext().eval(
					String.format("AMBITO('SALARIO_MENSUAL')"
					)
					, startDate
					, endDate, ExpressionScope.class );
	
		assertEquals(ExpressionScope.AGREEMENT, scopes.get(0).getValue());

		ctx.getExpressionContext().eval(
			String.format("AMBITO('SALARIO_MENSUAL') == AGREEMENT "
			)
			, startDate
			, endDate, Boolean.class )
		.forEach( r -> assertTrue(r.getValue()) );

		ctx.getExpressionContext().eval(
			String.format("AMBITO('SALARIO_MENSUAL') < CONTRACT "
			)
			, startDate
			, endDate, Boolean.class )
		.forEach( r -> assertTrue(r.getValue()) );

		ctx.getExpressionContext().eval(
			String.format("AMBITO('SALARIO_MENSUAL') < APPLICATION "
			)
			, startDate
			, endDate, Boolean.class )
		.forEach( r -> assertFalse(r.getValue()) );
	}

	@Test
	public void testSumIfDefI() throws ExpressionException, SQLException {

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
				newContract(aonContext, getToday(), new HashMap<String, String>(){{
				    put("VAR1", "1.00");
				    put("VAR2", "1.00");
				    put("VAR3", "1.00");
				    put("VAR4", "1.00");
				    put("VAR5", "1.00");
				    put("VAR6", "1.00");
				    put("VAR7", "1.00");
				    put("VAR8", "1.00");
				    put("VAR9", "1.00");
				}} ));
		//@formatter:on
		
		
		Date today = getToday();
		
		List<ITimedResult<Number>>  sum = 
				ctx.getExpressionContext().eval(
						String.format("SUMIFDEF('VAR1', 'VAR2', 'VAR3', 'VAR4', 'VAR5', 'VAR6', 'VAR7', 'VAR8', 'VAR9')")
						, startDate
						, endDate, Number.class);
		assertEquals(9.0, sum.get(0).getValue());
	}

	@Test
	public void testSumIfDefII() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		//@formatter:off
		ContractRecord contract = newContract(aonContext, startDate, Collections.emptyMap() );
		for(int i =1 ; i<= 9 ; i++ ) {
		    addData(aonContext, contract, startDate, startDate, "VAR"+i, "1.00");
		}
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		//@formatter:on
		
		
		
		List<ITimedResult<Number>>  sum = 
				ctx.getExpressionContext().eval(
						String.format("SUMIFDEF('VAR1', 'VAR2', 'VAR3', 'VAR4', 'VAR5', 'VAR6', 'VAR7', 'VAR8', 'VAR9')")
						, startDate
						, endDate, Number.class);
		assertEquals(9.0, sum.stream().collect(Collectors.summingDouble( r -> r.getValue().doubleValue()) ), 0.00);
	}

	@Test
	public void testSumIfDefIII() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		//@formatter:off
		ContractRecord contract = newContract(aonContext, startDate, Collections.emptyMap() );
		for(int i =1 ; i<= 9 ; i++ ) {
		    addData(aonContext, contract, startDate, startDate, "VAR"+i, "1.00");
		}
		for(int i =1 ; i<= 9 ; i++ ) {
		    addData(aonContext, contract, endDate, endDate, "VAR"+i, "1.00");
		}
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		//@formatter:on
		
		
		
		List<ITimedResult<Number>>  sum = 
				ctx.getExpressionContext().eval(
						String.format("SUMIFDEF('VAR1', 'VAR2', 'VAR3', 'VAR4', 'VAR5', 'VAR6', 'VAR7', 'VAR8', 'VAR9')")
						, startDate
						, endDate, Number.class);
		assertEquals(18.0, sum.stream().collect(Collectors.summingDouble( r -> r.getValue().doubleValue()) ), 0.00);
	}

	@Test
	public void testSumIfDefIV() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		//@formatter:off
		ContractRecord contract = newContract(aonContext, startDate, Collections.emptyMap() );
		for(int i =1 ; i<= 4 ; i++ ) {
		    addData(aonContext, contract, endDate, endDate, "VAR"+i, "1.00");
		}
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		//@formatter:on
		
		
		
		List<ITimedResult<Number>>  sum = 
				ctx.getExpressionContext().eval(
						String.format("SUMIFDEF('VAR1', 'VAR2', 'VAR3', 'VAR4', 'VAR5', 'VAR6', 'VAR7', 'VAR8', 'VAR9')")
						, startDate
						, endDate, Number.class);
		assertEquals(4.0, sum.stream().collect(Collectors.summingDouble( r -> r.getValue().doubleValue()) ), 0.00);
	}

	@Test
	public void testSumIfDefV() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		//@formatter:off
		ContractRecord contract = newContract(aonContext, startDate, Collections.emptyMap() );
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		//@formatter:on
		
		
		
		List<ITimedResult<Number>>  sum = 
				ctx.getExpressionContext().eval(
						String.format("SUMIFDEF('VAR1', 'VAR2', 'VAR3', 'VAR4', 'VAR5', 'VAR6', 'VAR7', 'VAR8', 'VAR9')")
						, startDate
						, endDate, Number.class);
		assertEquals(0.0, sum.stream().collect(Collectors.summingDouble( r -> r.getValue().doubleValue()) ), 0.00);
	}
	
	@Test
	public void testSumIfDefDays() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		//@formatter:off
		ContractRecord contract = newContract(aonContext, startDate, Collections.emptyMap() );
		//@formatter:on
		
		Date startDropDate = add(startDate, Calendar.DAY_OF_MONTH, 5);
		Date endDropDate = add(startDate, Calendar.DAY_OF_MONTH, 10);
		SQLSpecialDaysTestCase.addDropContractData(aonContext, contract, startDropDate, endDropDate);
		
		Date startITDate = add(startDate, Calendar.DAY_OF_MONTH, 15);
		Date endITDate = add(startDate, Calendar.DAY_OF_MONTH, 20);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate, endITDate, null);
		
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);

		List<ITimedResult<Number>>  sum = 
				ctx.getExpressionContext().eval(
						String.format("SUMIFDEF('DIAS_TRABAJADOS')")
						, startDate
						, endDate, Number.class);
		assertEquals(get(endDate, Calendar.DAY_OF_MONTH) - 12 , sum.stream().collect(Collectors.summingDouble( r -> r.getValue().doubleValue()) ), 0.00);

		sum = 
				ctx.getExpressionContext().eval(
						String.format("SUMIFDEF('DIAS_TRABAJADOS', 'DIAS_AUSENCIA')")
						, startDate
						, endDate, Number.class);
		assertEquals(get(endDate, Calendar.DAY_OF_MONTH) -6 , sum.stream().collect(Collectors.summingDouble( r -> r.getValue().doubleValue()) ), 0.00);
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
	
	
}
