/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.AGREEMENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SYSTEM;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
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

import junit.framework.Assert;

import org.junit.Test;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.expression.CheckException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;
import com.esferalia.aon.salary.expression.ExpressionContext.ExpressionExceptionWrapper;
import com.esferalia.aon.watson.util.AonDateUtils;

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
								get(today, Calendar.MONTH),
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
				newContract(aonContext, getToday(), Collections.emptyMap()));
		//@formatter:on
		
		try {
			ctx.getExpressionContext().eval("INPUT('/*user*/ANTIGÜEDAD(,)/**/','Hello World!!!');", 
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
	//------------------------------------------------------------------------
	
}
