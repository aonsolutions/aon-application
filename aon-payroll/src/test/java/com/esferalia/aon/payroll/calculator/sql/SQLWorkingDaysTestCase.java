/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKING_DAYS;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.jooq.tables.records.CalendarRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.HolidayRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.watson.util.AonDateUtils;

/**
 * @author rtrepiana
 *
 */
public class SQLWorkingDaysTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000000001;


	@Test
	public void testWorkDaysFromNothing()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
		getFirstDayOfMonth(getToday())
		,Collections.emptyMap()
		,new String[] { 
			WORKING_DAYS.getName(),
		},
		new String[] {},
			null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double workDays = 
		new Period(startDate,endDate).daysStream()
		.map( calendar -> calendar.get(Calendar.DAY_OF_WEEK))
		.filter(dayOfWeek -> 
				dayOfWeek == Calendar.MONDAY 
				|| dayOfWeek == Calendar.TUESDAY 
				|| dayOfWeek == Calendar.WEDNESDAY 
				|| dayOfWeek == Calendar.THURSDAY 
				|| dayOfWeek == Calendar.FRIDAY )
		.count();
		
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getDescription() + " = " + p.getAmount());

		for ( SalaryPayment p: salary.getSalaryPayments())
			assertEquals(workDays, p.getAmount(), 0.00);
		
		assertEquals(workDays, salary.getTotalPayment(), 0.00);

	}

	@Test
	public void testWorkDaysWithHoursI()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
		getFirstDayOfMonth(getToday())
		,new HashMap() {
			{
				put(ContextVariable.MONDAY_HOURS.getName(), "6.00" );
				put(ContextVariable.TUESDAY_HOURS.getName(), "6.00" );
				put(ContextVariable.WEDNESDAY_HOURS.getName(), "6.00" );
				put(ContextVariable.THURSDAY_HOURS.getName(), "6.00" );
				put(ContextVariable.FRIDAY_HOURS.getName(), "6.00" );
				put(ContextVariable.SATURDAY_HOURS.getName(), "6.00" );
			}
		}
		,new String[] { 
			WORKING_DAYS.getName(),
		},
		new String[] {},
		null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double workDays = 
		new Period(startDate,endDate).daysStream()
		.map( calendar -> calendar.get(Calendar.DAY_OF_WEEK))
		.filter(dayOfWeek -> 
				dayOfWeek == Calendar.MONDAY 
				|| dayOfWeek == Calendar.TUESDAY 
				|| dayOfWeek == Calendar.WEDNESDAY 
				|| dayOfWeek == Calendar.THURSDAY 
				|| dayOfWeek == Calendar.FRIDAY 
				|| dayOfWeek == Calendar.SATURDAY 
				)
		.count();
		
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getDescription() + " = " + p.getAmount());

		for ( SalaryPayment p: salary.getSalaryPayments())
			assertEquals(workDays, p.getAmount(), 0.00);
		
		assertEquals(workDays, salary.getTotalPayment(), 0.00);

	}

	@Test
	public void testWorkDaysWithHoursII()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
		getFirstDayOfMonth(getToday())
		,new HashMap() {
			{
				put(ContextVariable.MONDAY_HOURS.getName(), "6.00" );
				put(ContextVariable.TUESDAY_HOURS.getName(), "6.00" );
				put(ContextVariable.WEDNESDAY_HOURS.getName(), "6.00" );
				put(ContextVariable.THURSDAY_HOURS.getName(), "6.00" );
				put(ContextVariable.FRIDAY_HOURS.getName(), "6.00" );
				put(ContextVariable.SATURDAY_HOURS.getName(), "6.00" );
				put(ContextVariable.SUNDAY_HOURS.getName(), "0.00" );
			}
		}
		,new String[] { 
			WORKING_DAYS.getName(),
		},
		new String[] {},
		null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double workDays = 
		new Period(startDate,endDate).daysStream()
		.map( calendar -> calendar.get(Calendar.DAY_OF_WEEK))
		.count();
		
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getDescription() + " = " + p.getAmount());

		for ( SalaryPayment p: salary.getSalaryPayments())
			assertEquals(workDays, p.getAmount(), 0.00);
		
		assertEquals(workDays, salary.getTotalPayment(), 0.00);

	}

	@Test
	public void testWorkDaysWithCalendar()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Integer domainId = newDomain(aonContext).getId();
		Integer holidayId = newHoliday(
				aonContext, 
				domainId, 
				null //parentId, 
				)
				.getId();
		
		CalendarRecord calendar = newCalendar(aonContext, 
				domainId, 
				holidayId, 
				null,//mondayHours, 
				8.00,//tuesdayHours, 
				8.00,//wednesdayHours, 
				8.00,//thursdayHours, 
				8.00,//fridayHours, 
				8.00,//saturdayHours, 
				null//sundayHours
				)
				;
		

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfMonth(getToday()),
				new HashMap<String, String>() {
					{
					}
				},
				new String[] { 
						WORKING_DAYS.getName()},
				new String[] { 
						
				},
				null,
				calendar);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double workDays = 
		new Period(startDate,endDate).daysStream()
		.map( c -> c.get(Calendar.DAY_OF_WEEK))
		.filter(dayOfWeek -> 
				dayOfWeek == Calendar.TUESDAY 
				|| dayOfWeek == Calendar.WEDNESDAY 
				|| dayOfWeek == Calendar.THURSDAY 
				|| dayOfWeek == Calendar.FRIDAY 
				|| dayOfWeek == Calendar.SATURDAY )
		.count();
		
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getDescription() + " = " + p.getAmount());

		for ( SalaryPayment p: salary.getSalaryPayments())
			assertEquals(workDays, p.getAmount(), 0.00);
		
		assertEquals(workDays, salary.getTotalPayment(), 0.00);

	}

	@Test
	public void testWorkDaysWithCalendarAndHolidays()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		Date holiday  = getToday();
		
		Integer domainId = newDomain(aonContext).getId();
		Integer holidayId = newHoliday(
				aonContext, 
				domainId, 
				null, //parentId,
				holiday
				)
				.getId();
		
		CalendarRecord calendar = newCalendar(aonContext, 
				domainId, 
				holidayId, 
				null,//mondayHours, 
				8.00,//tuesdayHours, 
				8.00,//wednesdayHours, 
				8.00,//thursdayHours, 
				8.00,//fridayHours, 
				8.00,//saturdayHours, 
				null//sundayHours
				)
				;
		

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfMonth(getToday()),
				new HashMap<String, String>() {
					{
					}
				},
				new String[] { 
						WORKING_DAYS.getName()},
				new String[] { 
						
				},
				null,
				calendar);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double workDays = 
		new Period(startDate,endDate).daysStream()
		.filter(c -> c.get(Calendar.DAY_OF_MONTH) != AonDateUtils.get(holiday, Calendar.DAY_OF_MONTH))
		.map( c -> c.get(Calendar.DAY_OF_WEEK))
		.filter(dayOfWeek -> 
				dayOfWeek == Calendar.TUESDAY 
				|| dayOfWeek == Calendar.WEDNESDAY 
				|| dayOfWeek == Calendar.THURSDAY 
				|| dayOfWeek == Calendar.FRIDAY 
				|| dayOfWeek == Calendar.SATURDAY )
		.count();
		
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getDescription() + " = " + p.getAmount());

		for ( SalaryPayment p: salary.getSalaryPayments())
			assertEquals(workDays, p.getAmount(), 0.00);
		
		assertEquals(workDays, salary.getTotalPayment(), 0.00);

	}


	@Test
	public void testWorkDaysWithCalendarAndHolidaysWhitoutDays()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		Date holiday  = getToday();
		
		Integer domainId = newDomain(aonContext).getId();
		
		Integer parentHolidayId = newHoliday(
				aonContext, 
				domainId, 
				null, //parentId,
				holiday
				)
				.getId();

		Integer childHolidayId = newHoliday(
				aonContext, 
				domainId, 
				parentHolidayId //parentId,
				)
				.getId();
		
		CalendarRecord calendar = newCalendar(aonContext, 
				domainId, 
				childHolidayId, 
				null,//mondayHours, 
				8.00,//tuesdayHours, 
				8.00,//wednesdayHours, 
				8.00,//thursdayHours, 
				8.00,//fridayHours, 
				8.00,//saturdayHours, 
				null//sundayHours
				)
				;
		

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfMonth(getToday()),
				new HashMap<String, String>() {
					{
					}
				},
				new String[] { 
						WORKING_DAYS.getName()},
				new String[] { 
						
				},
				null,
				calendar);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double workDays = 
		new Period(startDate,endDate).daysStream()
		.filter(c -> c.get(Calendar.DAY_OF_MONTH) != AonDateUtils.get(holiday, Calendar.DAY_OF_MONTH))
		.map( c -> c.get(Calendar.DAY_OF_WEEK))
		.filter(dayOfWeek -> 
				dayOfWeek == Calendar.TUESDAY 
				|| dayOfWeek == Calendar.WEDNESDAY 
				|| dayOfWeek == Calendar.THURSDAY 
				|| dayOfWeek == Calendar.FRIDAY 
				|| dayOfWeek == Calendar.SATURDAY )
		.count();
		
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getDescription() + " = " + p.getAmount());

		for ( SalaryPayment p: salary.getSalaryPayments())
			assertEquals(workDays, p.getAmount(), 0.00);
		
		assertEquals(workDays, salary.getTotalPayment(), 0.00);

	}

	@Test
	public void testWorkDaysWithCalendarAndHolidaysWhitoutDaysII()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		Date holiday  = getToday();
		
		Integer domainId = newDomain(aonContext).getId();
		
		Integer parentHolidayId = newHoliday(
				aonContext, 
				domainId, 
				null //parentId,
				)
				.getId();

		Integer childHolidayId = newHoliday(
				aonContext, 
				domainId, 
				parentHolidayId, //parentId,
				holiday
				)
				.getId();
		
		CalendarRecord calendar = newCalendar(aonContext, 
				domainId, 
				childHolidayId, 
				null,//mondayHours, 
				8.00,//tuesdayHours, 
				8.00,//wednesdayHours, 
				8.00,//thursdayHours, 
				8.00,//fridayHours, 
				8.00,//saturdayHours, 
				null//sundayHours
				)
				;
		

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfMonth(getToday()),
				new HashMap<String, String>() {
					{
					}
				},
				new String[] { 
						WORKING_DAYS.getName()},
				new String[] { 
						
				},
				null,
				calendar);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double workDays = 
		new Period(startDate,endDate).daysStream()
		.filter(c -> c.get(Calendar.DAY_OF_MONTH) != AonDateUtils.get(holiday, Calendar.DAY_OF_MONTH))
		.map( c -> c.get(Calendar.DAY_OF_WEEK))
		.filter(dayOfWeek -> 
				dayOfWeek == Calendar.TUESDAY 
				|| dayOfWeek == Calendar.WEDNESDAY 
				|| dayOfWeek == Calendar.THURSDAY 
				|| dayOfWeek == Calendar.FRIDAY 
				|| dayOfWeek == Calendar.SATURDAY )
		.count();
		
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getDescription() + " = " + p.getAmount());

		for ( SalaryPayment p: salary.getSalaryPayments())
			assertEquals(workDays, p.getAmount(), 0.00);
		
		assertEquals(workDays, salary.getTotalPayment(), 0.00);

	}

	@Test
	public void testWorkDaysWithCalendarAndHolidaysWhitoutDaysIII()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		Date holiday  = getToday();
		
		Integer domainId = newDomain(aonContext).getId();
		
		Integer granParentHolidayId = newHoliday(
				aonContext, 
				domainId, 
				null, //parentId,
				holiday
				)
				.getId();

		Integer parentHolidayId = newHoliday(
				aonContext, 
				domainId, 
				granParentHolidayId //parentId,
				)
				.getId();

		Integer childHolidayId = newHoliday(
				aonContext, 
				domainId, 
				parentHolidayId //parentId,
				)
				.getId();
		
		CalendarRecord calendar = newCalendar(aonContext, 
				domainId, 
				childHolidayId, 
				null,//mondayHours, 
				8.00,//tuesdayHours, 
				8.00,//wednesdayHours, 
				8.00,//thursdayHours, 
				8.00,//fridayHours, 
				8.00,//saturdayHours, 
				null//sundayHours
				)
				;
		

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfMonth(getToday()),
				new HashMap<String, String>() {
					{
					}
				},
				new String[] { 
						WORKING_DAYS.getName()},
				new String[] { 
						
				},
				null,
				calendar);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double workDays = 
		new Period(startDate,endDate).daysStream()
		.filter(c -> c.get(Calendar.DAY_OF_MONTH) != AonDateUtils.get(holiday, Calendar.DAY_OF_MONTH))
		.map( c -> c.get(Calendar.DAY_OF_WEEK))
		.filter(dayOfWeek -> 
				dayOfWeek == Calendar.TUESDAY 
				|| dayOfWeek == Calendar.WEDNESDAY 
				|| dayOfWeek == Calendar.THURSDAY 
				|| dayOfWeek == Calendar.FRIDAY 
				|| dayOfWeek == Calendar.SATURDAY )
		.count();
		
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getDescription() + " = " + p.getAmount());

		for ( SalaryPayment p: salary.getSalaryPayments())
			assertEquals(workDays, p.getAmount(), 0.00);
		
		assertEquals(workDays, salary.getTotalPayment(), 0.00);

	}

	@Test
	public void testWorkDaysWithCalendarAndHolidaysLoop()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		Date holiday  = getToday();
		
		Integer domainId = newDomain(aonContext).getId();
		
		HolidayRecord granParentHoliday = newHoliday(
				aonContext, 
				domainId, 
				null, //parentId,
				holiday
				)
				;

		Integer parentHolidayId = newHoliday(
				aonContext, 
				domainId, 
				granParentHoliday.getId() //parentId,
				)
				.getId();

		Integer childHolidayId = newHoliday(
				aonContext, 
				domainId, 
				parentHolidayId //parentId,
				)
				.getId();
		
		granParentHoliday.setHoliday(childHolidayId);
		granParentHoliday.update();
		
		CalendarRecord calendar = newCalendar(aonContext, 
				domainId, 
				childHolidayId, 
				null,//mondayHours, 
				8.00,//tuesdayHours, 
				8.00,//wednesdayHours, 
				8.00,//thursdayHours, 
				8.00,//fridayHours, 
				8.00,//saturdayHours, 
				null//sundayHours
				)
				;
		

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfMonth(getToday()),
				new HashMap<String, String>() {
					{
					}
				},
				new String[] { 
						WORKING_DAYS.getName()},
				new String[] { 
						
				},
				null,
				calendar);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double workDays = 
		new Period(startDate,endDate).daysStream()
		.filter(c -> c.get(Calendar.DAY_OF_MONTH) != AonDateUtils.get(holiday, Calendar.DAY_OF_MONTH))
		.map( c -> c.get(Calendar.DAY_OF_WEEK))
		.filter(dayOfWeek -> 
				dayOfWeek == Calendar.TUESDAY 
				|| dayOfWeek == Calendar.WEDNESDAY 
				|| dayOfWeek == Calendar.THURSDAY 
				|| dayOfWeek == Calendar.FRIDAY 
				|| dayOfWeek == Calendar.SATURDAY )
		.count();
		
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getDescription() + " = " + p.getAmount());

		for ( SalaryPayment p: salary.getSalaryPayments())
			assertEquals(workDays, p.getAmount(), 0.00);
		
		assertEquals(workDays, salary.getTotalPayment(), 0.00);

	}

	// ------------------------------------------------------------------------

}
