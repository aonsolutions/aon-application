/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Calendar.CALENDAR;
import static com.esferalia.aon.jooq.tables.Holiday.HOLIDAY;
import static com.esferalia.aon.jooq.tables.HolidayDetail.HOLIDAY_DETAIL;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.esferalia.aon.calendar.enumeration.DayType;
import com.esferalia.aon.jooq.tables.records.CalendarRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.HolidayRecord;
import com.esferalia.aon.jooq.tables.records.PayrollWorkplaceRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.watson.util.AonDateUtils;


/**
 * @author rtrepiana
 *
 */
public class SQLEfectiveDaysTestCase extends AbstractSQLTestCase {

	@Test
	public void testCalendarI() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext, getFirstDayOfYear(getToday()), Collections.emptyMap());
		
		HolidayRecord holiday = newHoliday(aonContext, contract.getDomain());

		
		CalendarRecord calendar = newCalendar(aonContext, contract.getDomain(), holiday.getId() 
				,DayType.WORKING_DAY
				,DayType.WORKING_DAY
				,DayType.WORKING_DAY
				,DayType.WORKING_DAY
				,DayType.WORKING_DAY
				,DayType.WORKING_DAY
				,DayType.WORKING_DAY
				);
		updatePayrollWorkplace(aonContext, contract.getDomain(), contract.getWorkplace(), calendar.getId());
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		
		double actualDays = 0.00;
		Calendar day = Calendar.getInstance();
		day.setTime(startDate);
		while ( day.getTime().compareTo(endDate)<= 0 ){
			int dayOfWeek = day.get(Calendar.DAY_OF_WEEK);
			if ( dayOfWeek == Calendar.SUNDAY )
				;
			else if ( dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.WEDNESDAY ) 
				addHolidays(aonContext, holiday.getDomain(), holiday.getId(), new Date []{
					new Date(day.getTime().getTime())
				});
			else 
				actualDays ++;
			
			day.add(Calendar.DAY_OF_MONTH, 1);
		}


		ISQLContractSalaryCalculatorContext ctx =
		getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		
		List<ITimedResult<Double>> results = ctx.getExpressionContext().eval(ContextVariable.ACTUAL_DAYS.getName(), startDate, endDate, Double.class);
		
		
		double ctxActualDays = 0;
		for ( ITimedResult<Double> result: results )
			ctxActualDays += result.getValue();
		
		Assert.assertEquals(ContextVariable.ACTUAL_DAYS.getName(), actualDays, ctxActualDays);

	}
	
	@Test
	public void testCalendarII() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext, getFirstDayOfYear(getToday()), Collections.emptyMap());
		
		HolidayRecord parentI = newHoliday(aonContext, contract.getDomain());
		HolidayRecord holiday = newHoliday(aonContext, contract.getDomain());
		updateHoliday(aonContext, holiday.getId(), parentI.getId());

		
		CalendarRecord calendar = newCalendar(aonContext, contract.getDomain(), holiday.getId() 
				,DayType.WORKING_DAY
				,DayType.WORKING_DAY
				,DayType.WORKING_DAY
				,DayType.WORKING_DAY
				,DayType.WORKING_DAY
				,DayType.WORKING_DAY
				,DayType.WORKING_DAY
				);
		updatePayrollWorkplace(aonContext, contract.getDomain(), contract.getWorkplace(), calendar.getId());
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		
		double actualDays = 0.00;
		Calendar day = Calendar.getInstance();
		day.setTime(startDate);
		while ( day.getTime().compareTo(endDate)<= 0 ){
			int dayOfWeek = day.get(Calendar.DAY_OF_WEEK);
			if ( dayOfWeek == Calendar.SUNDAY )
				;
			else if ( dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.WEDNESDAY ) 
				addHolidays(aonContext, holiday.getDomain(), holiday.getId(), new Date []{
					new Date(day.getTime().getTime())
				});
			else if ( dayOfWeek == Calendar.TUESDAY || dayOfWeek == Calendar.THURSDAY ) 
				addHolidays(aonContext, parentI.getDomain(), parentI.getId(), new Date []{
					new Date(day.getTime().getTime())
				});
			else 
				actualDays ++;
			
			day.add(Calendar.DAY_OF_MONTH, 1);
		}


		ISQLContractSalaryCalculatorContext ctx =
		getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		
		List<ITimedResult<Double>> results = ctx.getExpressionContext().eval(ContextVariable.ACTUAL_DAYS.getName(), startDate, endDate, Double.class);
		
		
		double ctxActualDays = 0;
		for ( ITimedResult<Double> result: results )
			ctxActualDays += result.getValue();
		
		Assert.assertEquals(ContextVariable.ACTUAL_DAYS.getName(), actualDays, ctxActualDays);

		
	}

	
	@Test
	public void testCalendarIII() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext, getFirstDayOfYear(getToday()), Collections.emptyMap());
		
		HolidayRecord parentII = newHoliday(aonContext, contract.getDomain());
		HolidayRecord parentI = newHoliday(aonContext, contract.getDomain());
		updateHoliday(aonContext, parentI.getId(), parentII.getId());
		HolidayRecord holiday = newHoliday(aonContext, contract.getDomain());
		updateHoliday(aonContext, holiday.getId(), parentI.getId());

		
		CalendarRecord calendar = newCalendar(aonContext, contract.getDomain(), holiday.getId() 
				,DayType.WORKING_DAY
				,DayType.WORKING_DAY
				,DayType.WORKING_DAY
				,DayType.WORKING_DAY
				,DayType.WORKING_DAY
				,DayType.WORKING_DAY
				,DayType.WORKING_DAY
				);
		updatePayrollWorkplace(aonContext, contract.getDomain(), contract.getWorkplace(), calendar.getId());
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		
		double actualDays = 0.00;
		Calendar day = Calendar.getInstance();
		day.setTime(startDate);
		while ( day.getTime().compareTo(endDate)<= 0 ){
			int dayOfWeek = day.get(Calendar.DAY_OF_WEEK);
			if ( dayOfWeek == Calendar.SUNDAY )
				;
			else if ( dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.WEDNESDAY ) 
				addHolidays(aonContext, holiday.getDomain(), holiday.getId(), new Date []{
					new Date(day.getTime().getTime())
				});
			else if ( dayOfWeek == Calendar.TUESDAY || dayOfWeek == Calendar.THURSDAY ) 
				addHolidays(aonContext, parentI.getDomain(), parentI.getId(), new Date []{
					new Date(day.getTime().getTime())
				});
			else if ( dayOfWeek == Calendar.FRIDAY ) 
				addHolidays(aonContext, parentI.getDomain(), parentII.getId(), new Date []{
					new Date(day.getTime().getTime())
				});
			else 
				actualDays ++;
			
			day.add(Calendar.DAY_OF_MONTH, 1);
		}


		ISQLContractSalaryCalculatorContext ctx =
		getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		
		List<ITimedResult<Double>> results = ctx.getExpressionContext().eval(ContextVariable.ACTUAL_DAYS.getName(), startDate, endDate, Double.class);
		
		
		double ctxActualDays = 0;
		for ( ITimedResult<Double> result: results )
			ctxActualDays += result.getValue();
		
		Assert.assertEquals(ContextVariable.ACTUAL_DAYS.getName(), actualDays, ctxActualDays);

		
	}

	@Test
	public void testCalendarIV() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext, getFirstDayOfYear(getToday()), Collections.emptyMap());
		
		HolidayRecord parentIII = newHoliday(aonContext, contract.getDomain());
		HolidayRecord parentII = newHoliday(aonContext, contract.getDomain());
		updateHoliday(aonContext, parentII.getId(), parentIII.getId());
		HolidayRecord parentI = newHoliday(aonContext, contract.getDomain());
		updateHoliday(aonContext, parentI.getId(), parentII.getId());
		HolidayRecord holiday = newHoliday(aonContext, contract.getDomain());
		updateHoliday(aonContext, holiday.getId(), parentI.getId());

		
		CalendarRecord calendar = newCalendar(aonContext, contract.getDomain(), holiday.getId() 
				,DayType.WORKING_DAY
				,DayType.WORKING_DAY
				,DayType.WORKING_DAY
				,DayType.WORKING_DAY
				,DayType.WORKING_DAY
				,DayType.WORKING_DAY
				,DayType.WORKING_DAY
				);
		updatePayrollWorkplace(aonContext, contract.getDomain(), contract.getWorkplace(), calendar.getId());
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		
		double actualDays = 0.00;
		Calendar day = Calendar.getInstance();
		day.setTime(startDate);
		while ( day.getTime().compareTo(endDate)<= 0 ){
			int dayOfWeek = day.get(Calendar.DAY_OF_WEEK);
			if ( dayOfWeek == Calendar.SUNDAY )
				;
			else if ( dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.WEDNESDAY ) 
				addHolidays(aonContext, holiday.getDomain(), holiday.getId(), new Date []{
					new Date(day.getTime().getTime())
				});
			else if ( dayOfWeek == Calendar.TUESDAY || dayOfWeek == Calendar.THURSDAY ) 
				addHolidays(aonContext, parentI.getDomain(), parentI.getId(), new Date []{
					new Date(day.getTime().getTime())
				});
			else if ( dayOfWeek == Calendar.FRIDAY ) 
				addHolidays(aonContext, parentII.getDomain(), parentII.getId(), new Date []{
					new Date(day.getTime().getTime())
				});
			else if ( dayOfWeek == Calendar.SATURDAY ) 
				addHolidays(aonContext, parentIII.getDomain(), parentIII.getId(), new Date []{
					new Date(day.getTime().getTime())
				});
			else 
				actualDays ++;
			
			day.add(Calendar.DAY_OF_MONTH, 1);
		}


		ISQLContractSalaryCalculatorContext ctx =
		getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		
		List<ITimedResult<Double>> results = ctx.getExpressionContext().eval(ContextVariable.ACTUAL_DAYS.getName(), startDate, endDate, Double.class);
		
		
		double ctxActualDays = 0;
		for ( ITimedResult<Double> result: results )
			ctxActualDays += result.getValue();
		
		Assert.assertEquals(ContextVariable.ACTUAL_DAYS.getName(), 0.00, ctxActualDays);

		
	}

	@Test
	public void testCalendarV() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(
				aonContext, 
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>(){
					{
						put("TC2", ContractCode.C100.getValue());
					}
				}
				);
		
		HolidayRecord holiday = newHoliday(aonContext, contract.getDomain());

		
		CalendarRecord calendar = newCalendar(aonContext, contract.getDomain(), holiday.getId() 
				,DayType.WORKING_DAY
				,DayType.WORKING_DAY
				,DayType.WORKING_DAY
				,DayType.WORKING_DAY
				,DayType.WORKING_DAY
				,DayType.NOT_WORKING_DAY
				,DayType.NOT_WORKING_DAY
				);
		
		updatePayrollWorkplace(aonContext, contract.getDomain(), contract.getWorkplace(), calendar.getId());
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		
		double actualDays = 0.00;
		Calendar day = Calendar.getInstance();
		day.setTime(startDate);
		while ( day.getTime().compareTo(endDate)<= 0 ){

			switch (day.get(Calendar.DAY_OF_WEEK)) {
			case Calendar.SUNDAY:
			case Calendar.SATURDAY:
				break;
			default:
				actualDays ++;
			}
			day.add(Calendar.DAY_OF_MONTH, 1);
		}

		day.setTime(startDate);
		while ( day.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY )
			day.add(Calendar.DAY_OF_MONTH, 1);
		
		day.add(Calendar.DAY_OF_MONTH, 1);
		Date secondWeekStart = new Date(day.getTimeInMillis());
		while ( day.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY )
			day.add(Calendar.DAY_OF_MONTH, 1);

		Date secondWeekEnd = new Date(day.getTimeInMillis());
		
		addData(aonContext, 
				contract, 
				secondWeekStart, 
				secondWeekEnd, 
				"HORAS_MARTES",
				"-1");
		actualDays--;
		
		ISQLContractSalaryCalculatorContext ctx =
		getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		List<ITimedResult<Double>> results = ctx.getExpressionContext().eval(ContextVariable.ACTUAL_DAYS.getName(), startDate, endDate, Double.class);
		double ctxActualDays = 0;
		for ( ITimedResult<Double> result: results )
			ctxActualDays += result.getValue();
		Assert.assertEquals(ContextVariable.ACTUAL_DAYS.getName(), actualDays, ctxActualDays);

		day.setTime(secondWeekEnd);
		day.add(Calendar.DAY_OF_MONTH, 1);
		while ( day.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY )
			day.add(Calendar.DAY_OF_MONTH, 1);
		
		day.add(Calendar.DAY_OF_MONTH, 1);
		Date thirdWeekStart = new Date(day.getTimeInMillis());
		while ( day.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY )
			day.add(Calendar.DAY_OF_MONTH, 1);

		Date thirdWeekEnd = new Date(day.getTimeInMillis());
		
		addData(aonContext, 
				contract, 
				thirdWeekStart, 
				thirdWeekEnd, 
				"HORAS_JUEVES",
				"NADA");
		actualDays--;

		ctx =
		getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		results = ctx.getExpressionContext().eval(ContextVariable.ACTUAL_DAYS.getName(), startDate, endDate, Double.class);
		ctxActualDays = 0;
		for ( ITimedResult<Double> result: results )
			ctxActualDays += result.getValue();
		Assert.assertEquals(ContextVariable.ACTUAL_DAYS.getName(), actualDays, ctxActualDays);

	
		addData(aonContext, 
				contract, 
				thirdWeekStart, 
				thirdWeekEnd, 
				"HORAS_VIERNES",
				"null");
		actualDays--;

		ctx =
		getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		results = ctx.getExpressionContext().eval(ContextVariable.ACTUAL_DAYS.getName(), startDate, endDate, Double.class);
		ctxActualDays = 0;
		for ( ITimedResult<Double> result: results )
			ctxActualDays += result.getValue();
		Assert.assertEquals(ContextVariable.ACTUAL_DAYS.getName(), actualDays, ctxActualDays);

		addData(aonContext, 
				contract, 
				thirdWeekStart, 
				thirdWeekEnd, 
				"HORAS_LUNES",
				"8");

		ctx =
		getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		results = ctx.getExpressionContext().eval(ContextVariable.ACTUAL_DAYS.getName(), startDate, endDate, Double.class);
		ctxActualDays = 0;
		for ( ITimedResult<Double> result: results )
			ctxActualDays += result.getValue();
		Assert.assertEquals(ContextVariable.ACTUAL_DAYS.getName(), actualDays, ctxActualDays);

	}

	// ------------------------------------------------------------------------
	
	private HolidayRecord newHoliday(AONContext aonContext, Integer domain) {
		return
		aonContext.getDslContext()
		.insertInto(HOLIDAY)
		.set(HOLIDAY.DOMAIN, domain)
		.set(HOLIDAY.DESCRIPTION, "")
		.returning()
		.fetchOne()
		;
	}

	private void updateHoliday(AONContext aonContext, Integer holiday, Integer parent) {
		aonContext.getDslContext()
		.update(HOLIDAY)
		.set(HOLIDAY.HOLIDAY_, parent)
		.where(HOLIDAY.ID.eq(holiday))
		.execute()
		;
	}

	private CalendarRecord newCalendar(AONContext aonContext, Integer domain, Integer holiday, DayType...dayTypes) {
		return
		aonContext.getDslContext()
		.insertInto(CALENDAR)
		.set(CALENDAR.DOMAIN, domain)
		.set(CALENDAR.HOLIDAY, holiday)
		.set(CALENDAR.DESCRIPTION, "")
		.set(CALENDAR.MONDAY, (byte)dayTypes[0].ordinal())
		.set(CALENDAR.TUESDAY, (byte)dayTypes[1].ordinal())
		.set(CALENDAR.WEDNESDAY, (byte)dayTypes[2].ordinal())
		.set(CALENDAR.THURSDAY, (byte)dayTypes[3].ordinal())
		.set(CALENDAR.FRIDAY, (byte)dayTypes[4].ordinal())
		.set(CALENDAR.SATURDAY, (byte)dayTypes[5].ordinal())
		.set(CALENDAR.SUNDAY, (byte)dayTypes[6].ordinal())
		.returning()
		.fetchOne()
		;
	}

	private void addHolidays(AONContext aonContext, Integer domain, Integer holiday, Date ...holidays) {
		for ( Date day: holidays )
			aonContext.getDslContext()
			.insertInto(HOLIDAY_DETAIL)
			.set(HOLIDAY_DETAIL.DOMAIN, domain)
			.set(HOLIDAY_DETAIL.HOLIDAY, holiday)
			.set(HOLIDAY_DETAIL.DATE, day)
			.set(HOLIDAY_DETAIL.DESCRIPTION, "")
			.execute()
			;
	}

	private PayrollWorkplaceRecord newPayrollWorkplace(AONContext aonContext, Integer domain, Integer workplace, Integer calendar) {
		//@formatter:off
		return
		aonContext.getDslContext()
		.insertInto(PAYROLL_WORKPLACE)
		.set(PAYROLL_WORKPLACE.DOMAIN, domain)
		.set(PAYROLL_WORKPLACE.CALENDAR, calendar)
		.set(PAYROLL_WORKPLACE.WORKPLACE, workplace)
		.returning()
		.fetchOne()
		;
		//@formatter:on
	}
	
	private void updatePayrollWorkplace(AONContext aonContext, Integer domain, Integer workplace, Integer calendar) {
		//@formatter:off
		aonContext.getDslContext()
		.update(PAYROLL_WORKPLACE)
		.set(PAYROLL_WORKPLACE.CALENDAR, calendar)
		.set(PAYROLL_WORKPLACE.WORKPLACE, workplace)
		.where(PAYROLL_WORKPLACE.WORKPLACE.eq(workplace))
		.execute()
		;
		//@formatter:on
	}
}
