/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.FRIDAY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONDAY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SATURDAY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SUNDAY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.THURSDAY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TUESDAY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WEDNESDAY_DAYS;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.FRIDAY;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.THURSDAY;
import static java.util.Calendar.TUESDAY;
import static java.util.Calendar.WEDNESDAY;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;


/**
 * @author rtrepiana
 *
 */
public class SQLWeekDaysTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000000001;


	@Test
	public void testWeekDaysI()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfMonth(getToday())
		, Collections.emptyMap()
		,new String[] { 
				MONDAY_DAYS.getName(),
				TUESDAY_DAYS.getName(),
				WEDNESDAY_DAYS.getName(),
				THURSDAY_DAYS.getName(),
				FRIDAY_DAYS.getName(),
				SATURDAY_DAYS.getName(),
				SUNDAY_DAYS.getName()
				 },
		new String[] {},
			null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		Set<SalaryPayment> salaryPayments = salary.getSalaryPayments();
		assertEquals(7, salaryPayments.size());
		
		String weekVarDays [] = new String[8];
		weekVarDays[MONDAY] = MONDAY_DAYS.getName();
		weekVarDays[TUESDAY] = TUESDAY_DAYS.getName();
		weekVarDays[WEDNESDAY] = WEDNESDAY_DAYS.getName();
		weekVarDays[THURSDAY] = THURSDAY_DAYS.getName();
		weekVarDays[FRIDAY] = FRIDAY_DAYS.getName();
		weekVarDays[SATURDAY] = SATURDAY_DAYS.getName();
		weekVarDays[SUNDAY] = SUNDAY_DAYS.getName();
		
		Map<String, Double> weekDays = new HashMap<String,Double>();
		for ( String var : weekVarDays )
			weekDays.put(var, 0.00);
		
		while ( startDate.compareTo(endDate) <= 0 ) {
			int dayOfWeek = AonDateUtils.get(startDate, DAY_OF_WEEK);
			weekDays.put(weekVarDays[dayOfWeek], weekDays.get(weekVarDays[dayOfWeek])+1);
			startDate = add(startDate, Calendar.DAY_OF_MONTH, 1);
		}

		for ( SalaryPayment p: salaryPayments)
			System.out.println(p.getDescription() + " = " +  weekDays.get(p.getDescription()) + ", " + p.getAmount());
		
		for ( SalaryPayment p: salaryPayments)
			assertEquals(weekDays.get(p.getDescription()), p.getAmount());
		

	}
	// ------------------------------------------------------------------------

	@Test
	public void testWeekDaysII()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				getToday()
		, Collections.emptyMap()
		,new String[] { 
				MONDAY_DAYS.getName(),
				TUESDAY_DAYS.getName(),
				WEDNESDAY_DAYS.getName(),
				THURSDAY_DAYS.getName(),
				FRIDAY_DAYS.getName(),
				SATURDAY_DAYS.getName(),
				SUNDAY_DAYS.getName()
				 },
		new String[] {},
			null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		Set<SalaryPayment> salaryPayments = salary.getSalaryPayments();
		assertEquals(7, salaryPayments.size());
		
		String weekVarDays [] = new String[8];
		weekVarDays[MONDAY] = MONDAY_DAYS.getName();
		weekVarDays[TUESDAY] = TUESDAY_DAYS.getName();
		weekVarDays[WEDNESDAY] = WEDNESDAY_DAYS.getName();
		weekVarDays[THURSDAY] = THURSDAY_DAYS.getName();
		weekVarDays[FRIDAY] = FRIDAY_DAYS.getName();
		weekVarDays[SATURDAY] = SATURDAY_DAYS.getName();
		weekVarDays[SUNDAY] = SUNDAY_DAYS.getName();
		
		Map<String, Double> weekDays = new HashMap<String,Double>();
		for ( String var : weekVarDays )
			weekDays.put(var, 0.00);
		
		Date today = getToday();
		while ( today.compareTo(endDate) <= 0 ) {
			int dayOfWeek = AonDateUtils.get(today, DAY_OF_WEEK);
			weekDays.put(weekVarDays[dayOfWeek], weekDays.get(weekVarDays[dayOfWeek])+1);
			today = add(today, Calendar.DAY_OF_MONTH, 1);
		}

		for ( SalaryPayment p: salaryPayments)
			System.out.println(p.getDescription() + " = " +  weekDays.get(p.getDescription()) + ", " + p.getAmount());
		
		for ( SalaryPayment p: salaryPayments)
			assertEquals(weekDays.get(p.getDescription()), p.getAmount());
		

	}

	@Test
	public void testWeekDaysIII()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfMonth(getToday())
		, Collections.emptyMap()
		,new String[] { 
				MONDAY_DAYS.getName(),
				TUESDAY_DAYS.getName(),
				WEDNESDAY_DAYS.getName(),
				THURSDAY_DAYS.getName(),
				FRIDAY_DAYS.getName(),
				SATURDAY_DAYS.getName(),
				SUNDAY_DAYS.getName()
				 },
		new String[] {},
			null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;
		
		Date startIt = add(startDate, Calendar.DAY_OF_MONTH, 10);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startIt, endDate, null);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		Set<SalaryPayment> salaryPayments = salary.getSalaryPayments();
		assertEquals(7, salaryPayments.size());
		
		String weekVarDays [] = new String[8];
		weekVarDays[MONDAY] = MONDAY_DAYS.getName();
		weekVarDays[TUESDAY] = TUESDAY_DAYS.getName();
		weekVarDays[WEDNESDAY] = WEDNESDAY_DAYS.getName();
		weekVarDays[THURSDAY] = THURSDAY_DAYS.getName();
		weekVarDays[FRIDAY] = FRIDAY_DAYS.getName();
		weekVarDays[SATURDAY] = SATURDAY_DAYS.getName();
		weekVarDays[SUNDAY] = SUNDAY_DAYS.getName();
		
		Map<String, Double> weekDays = new HashMap<String,Double>();
		for ( String var : weekVarDays )
			weekDays.put(var, 0.00);
		
		while ( startDate.compareTo(startIt) < 0 ) {
			int dayOfWeek = AonDateUtils.get(startDate, DAY_OF_WEEK);
			weekDays.put(weekVarDays[dayOfWeek], weekDays.get(weekVarDays[dayOfWeek])+1);
			startDate = add(startDate, Calendar.DAY_OF_MONTH, 1);
		}

		for ( SalaryPayment p: salaryPayments)
			System.out.println(p.getDescription() + " = " +  weekDays.get(p.getDescription()) + ", " + p.getAmount());
		
		for ( SalaryPayment p: salaryPayments)
			assertEquals(weekDays.get(p.getDescription()), p.getAmount(),p.getDescription());
		

	}
}
