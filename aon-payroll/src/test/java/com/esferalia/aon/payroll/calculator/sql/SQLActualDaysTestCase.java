/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.AGREEMENT_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FRIDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PARTIAL_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SATURDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRIKE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRIKE_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SUNDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.THURSDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TUESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WEDNESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WEEK_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C100;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C109;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C130;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C139;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C150;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C189;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C200;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C209;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C230;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C239;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C250;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C289;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C401;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C402;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C403;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C408;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C410;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C418;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C420;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C421;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C430;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C441;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C450;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C452;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C501;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C502;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C503;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C508;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C510;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C518;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C520;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C530;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C540;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C541;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C550;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C552;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getMax;
import static java.lang.String.format;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.junit.Test;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseActivityRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseCccRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.ScopeRecord;
import com.esferalia.aon.jooq.tables.records.WorkplaceRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.TimedResult;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;

import junit.framework.Assert;

/**
 * @author rtrepiana
 *
 */

public class SQLActualDaysTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000000001;

	private static ContractCode PARTIAL_TIME[] = { C200, C209, C230, C239,
			C250, C289, C501, C502, C503, C508, C510, C518, C520, C530, C540,
			C541, C550, C552, };

	private static ContractCode FULL_TIME[] = { C100, C109, C130, C139,
			C150,
			C189, // indefinite fulltime
			C401, C402, C403, C408, C410, C418, C420, C421, C430, C441, C450,
			C452, // partial & temp fulltime
	};

	@Test
	public void testFullTimeActualDaysI() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date firstDayOfYear = getFirstDayOfYear(getToday());
		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(
		aonContext, 
		firstDayOfYear,
		new HashMap<String, String>() {
			{
				put(TC2.getName(),
						format("\"%s\"", SQLWorkedDaysTestCase.random(FULL_TIME).getValue()));
			}
		});
		
		for ( int i = 0; i < 12; i++) {
			Date startDate = add(firstDayOfYear, Calendar.MONTH, i);
			Date endDate = getLastDayOfMonth(startDate);
			ISQLContractSalaryCalculatorContext ctx = 
			getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
			List<ITimedResult<Number>> actualDays = 
			ctx.getExpressionContext().eval(ContextVariable.ACTUAL_DAYS.getName(), startDate, endDate, Number.class);
			
			long calculatedActualDays = 
			actualDays.stream()
			.peek(SQLActualDaysTestCase::trace)
			.map(ITimedResult::getValue)
			.collect(Collectors.summingLong(Number::longValue));
			
			long expectedActualDays = getSumExpectedActualDays(startDate, endDate);
			
			org.junit.Assert.assertEquals(expectedActualDays, calculatedActualDays);
		}
	}
	
	@Test
	public void testPartialTimeActualDaysI() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date firstDayOfYear = getFirstDayOfYear(getToday());
		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(
		aonContext, 
		firstDayOfYear,
		new HashMap<String, String>() {
			{
				put(TC2.getName(),
						format("\"%s\"", SQLWorkedDaysTestCase.random(PARTIAL_TIME).getValue()));
			}
		});
		
		for ( int i = 0; i < 12; i++) {
			Date startDate = add(firstDayOfYear, Calendar.MONTH, i);
			Date endDate = getLastDayOfMonth(startDate);
			ISQLContractSalaryCalculatorContext ctx = 
			getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
			List<ITimedResult<Number>> actualDays = 
			ctx.getExpressionContext().eval(ContextVariable.ACTUAL_DAYS.getName(), startDate, endDate, Number.class);
			
			long calculatedActualDays = 
			actualDays.stream()
			.peek(SQLActualDaysTestCase::trace)
			.map(ITimedResult::getValue)
			.collect(Collectors.summingLong(Number::longValue));
			
			long expectedActualDays = getSumExpectedActualDays(startDate, endDate);
			
			org.junit.Assert.assertEquals(expectedActualDays, calculatedActualDays);
		}
		
	}

	@Test
	public void testPartialTimeActualDaysII() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date firstDayOfYear = getFirstDayOfYear(getToday());
		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(
		aonContext, 
		firstDayOfYear,
		new HashMap<String, String>() {
			{
				put(TC2.getName(),
						format("\"%s\"", SQLWorkedDaysTestCase.random(PARTIAL_TIME).getValue()));
				put(ContextVariable.WEDNESDAY_HOURS.getName(), "4.00");
				put(ContextVariable.SATURDAY_HOURS.getName(), "4.00");
				put(ContextVariable.SUNDAY_HOURS.getName(), "4.00");
			}
		});
		
		for ( int i = 0; i < 12; i++) {
			Date startDate = add(firstDayOfYear, Calendar.MONTH, i);
			Date endDate = getLastDayOfMonth(startDate);
			ISQLContractSalaryCalculatorContext ctx = 
			getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
			List<ITimedResult<Number>> actualDays = 
			ctx.getExpressionContext().eval(ContextVariable.ACTUAL_DAYS.getName(), startDate, endDate, Number.class);
			
			long calculatedActualDays = 
			actualDays.stream()
			.peek(SQLActualDaysTestCase::trace)
			.map(ITimedResult::getValue)
			.collect(Collectors.summingLong(Number::longValue));
			
			long expectedActualDays = getSumExpectedActualDays(startDate, endDate, Calendar.WEDNESDAY, Calendar.SATURDAY, Calendar.SUNDAY );
			
			org.junit.Assert.assertEquals(expectedActualDays, calculatedActualDays);
		}
		
	}

	@Test
	public void testPartialTimeActualDaysITI() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date firstDayOfYear = getFirstDayOfYear(getToday());
		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(
		aonContext, 
		firstDayOfYear,
		new HashMap<String, String>() {
			{
				put(TC2.getName(),
						format("\"%s\"", SQLWorkedDaysTestCase.random(PARTIAL_TIME).getValue()));
				put(ContextVariable.WEDNESDAY_HOURS.getName(), "4.00");
				put(ContextVariable.SATURDAY_HOURS.getName(), "4.00");
				put(ContextVariable.SUNDAY_HOURS.getName(), "4.00");
			}
		});
		
		for ( int i = 0; i < 12; i++) {
			Date startDate = add(firstDayOfYear, Calendar.MONTH, i);
			Date endDate = getLastDayOfMonth(startDate);
			
			Date startIT = add( startDate, Calendar.DAY_OF_MONTH, (int)(Math.random() * 15));
			Date endIT = add( startIT, Calendar.DAY_OF_MONTH, (int)(Math.random() * 10));
			addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startIT, endIT, null);
			
			ISQLContractSalaryCalculatorContext ctx = 
			getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
			List<ITimedResult<Number>> actualDays = 
			ctx.getExpressionContext().eval(ContextVariable.ACTUAL_DAYS.getName(), startDate, endDate, Number.class);
			
			long calculatedActualDays = 
			actualDays.stream()
			.peek(SQLActualDaysTestCase::trace)
			.map(ITimedResult::getValue)
			.collect(Collectors.summingLong(Number::longValue));
			
			
			long expectedActualDays = getSumExpectedActualDays(startDate, startIT.equals(startDate) ? startDate : add(startIT, Calendar.DAY_OF_MONTH,-1), Calendar.WEDNESDAY, Calendar.SATURDAY, Calendar.SUNDAY );
			expectedActualDays += getSumExpectedActualDays(add(endIT, Calendar.DAY_OF_MONTH,1), endDate, Calendar.WEDNESDAY, Calendar.SATURDAY, Calendar.SUNDAY );
			
			org.junit.Assert.assertEquals(expectedActualDays, calculatedActualDays);
			
			Collections.sort(actualDays, (r1,r2) -> r1.getPeriod().compareTo(r2.getPeriod()));
			org.junit.Assert.assertEquals(2, actualDays.size());
			
			expectedActualDays = getSumExpectedActualDays(startDate, add(startIT, Calendar.DAY_OF_MONTH,-1), Calendar.WEDNESDAY, Calendar.SATURDAY, Calendar.SUNDAY );
			calculatedActualDays = actualDays.get(0).getValue().intValue();
			org.junit.Assert.assertEquals(expectedActualDays, calculatedActualDays);
			org.junit.Assert.assertEquals(actualDays.get(0).getPeriod().getStart(), startDate);
			
			expectedActualDays = getSumExpectedActualDays(add(endIT, Calendar.DAY_OF_MONTH,1), endDate, Calendar.WEDNESDAY, Calendar.SATURDAY, Calendar.SUNDAY );
			calculatedActualDays = actualDays.get(1).getValue().intValue();
			org.junit.Assert.assertEquals(expectedActualDays, calculatedActualDays);
			org.junit.Assert.assertEquals(actualDays.get(1).getPeriod().getEnd(), endDate);
		}
		
	}

	// ----------------------------------------------------
	
	private static void trace(ITimedResult<Number> result) {
		System.out.println(result.getValue() + "[" + result.getPeriod().getStart() +".." +result.getPeriod().getEnd() + "]" );
	}
	

	private static int getSumExpectedActualDays( Date startDate, Date endDate) {
		return getExpectedActualDays(startDate, endDate).size();
	}

	private static int getSumExpectedActualDays( Date startDate, Date endDate, int ...weekDays ) {
		return getExpectedActualDays(startDate, endDate, weekDays).size();
	}

	private static List<ITimedResult<Integer>> getExpectedActualDays( Date startDate, Date endDate ) {
		return getExpectedActualDays(startDate, endDate, 
				Calendar.MONDAY, 
				Calendar.TUESDAY, 
				Calendar.WEDNESDAY, 
				Calendar.THURSDAY, 
				Calendar.FRIDAY);
	}

	private static List<ITimedResult<Integer>> getExpectedActualDays( Date startDate, Date endDate, int ...weekDays ) {
		
		return 
		new Period(startDate, endDate).daysStream()
		.filter(day -> contains(weekDays, day.get(Calendar.DAY_OF_WEEK)))
		.map (day -> new TimedResult<Integer>(1, new Period(day.getTime(),  day.getTime()), null))
		.collect(Collectors.toList())
		;
		
	}
	
	private static boolean contains(int [] weekDays, int weekDay) {
		return Arrays.stream(weekDays).anyMatch( d -> d == weekDay);
	}
}
