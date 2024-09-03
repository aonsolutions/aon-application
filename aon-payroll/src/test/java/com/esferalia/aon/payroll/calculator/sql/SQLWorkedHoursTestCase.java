/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.FRIDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PARTIAL_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SALARY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SATURDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SUNDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.THURSDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TUESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WEDNESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_HOURS;
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
import static java.lang.String.format;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.jooq.tables.records.CalendarRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.payroll.IrpfOutcome;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.IExpression;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;


/**
 * @author rtrepiana
 *
 */
public class SQLWorkedHoursTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000000001;

	private static ContractCode PARTIAL_TIME[] = { C200, C209, C230, C239, C250,
			C289, C501, C502, C503, C508, C510, C518, C520, C530, C540, C541,
			C550, C552, };

	private static ContractCode FULL_TIME[] = { C100, C109, C130, C139,
			C150,
			C189, // indefinite fulltime
			C401, C402, C403, C408, C410, C418, C420, C421, C430, C441, C450,
			C452, // partial & temp fulltime
	};

	@Test
	public void testFullTimeWorkHoursActualDays()
			throws ExpressionException, SQLException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfMonth(getToday()), new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(FULL_TIME).getValue()));
					}
				});

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;
		
		int actualDays[] = { Calendar.MONDAY, Calendar.WEDNESDAY, Calendar.THURSDAY};
		Arrays.sort(actualDays);
		
		
		new Period(startDate, endDate).daysStream()
		.filter(day -> Arrays.binarySearch(actualDays,day.get(Calendar.DAY_OF_WEEK)) >= 0 )
		.forEach( day -> addData(aonContext, contract, new java.sql.Date(day.getTimeInMillis()), new java.sql.Date(day.getTimeInMillis()), ContextVariable.ACTUAL_DAYS, "1"))
		;

		long days = 
		new Period(startDate, endDate).daysStream()
		.filter(day -> Arrays.binarySearch(actualDays,day.get(Calendar.DAY_OF_WEEK)) >= 0 ).count();

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);

		List<ITimedVariable<Object>> workedHours = ctx.getExpressionContext()
				.getVariables(WORKED_HOURS);

		double hours = 0.00;
		for (ITimedVariable<Object> workedHour : workedHours)
			hours += ((Number) workedHour.getValue(workedHour.getPeriod()))
					.doubleValue();

		assertEquals(days * 8.00,  hours, 0.00,WORKED_HOURS.getName());
	}

	@Test
	public void testFullTimeWorkHoursActualDaysII()
			throws ExpressionException, SQLException {
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
				null,//wednesdayHours, 
				8.00,//thursdayHours, 
				null,//fridayHours, 
				8.00,//saturdayHours, 
				null//sundayHours
				)
				;

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfMonth(getToday()),
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(FULL_TIME).getValue()));
					}
				},
				new String[] { 
				},
				new String[] { 
						
				},
				null,
				calendar);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;
		
		int actualDays[] = { Calendar.MONDAY, Calendar.WEDNESDAY, Calendar.FRIDAY, Calendar.SUNDAY};
		Arrays.sort(actualDays);
		
		
		long days = 
		new Period(startDate, endDate).daysStream()
		.filter(day -> Arrays.binarySearch(actualDays,day.get(Calendar.DAY_OF_WEEK)) < 0 )
		.peek( day -> System.out.println( "**********>" + day.getTime() ))
		.count();

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);

		List<ITimedVariable<Object>> workedHours = ctx.getExpressionContext()
				.getVariables(WORKED_HOURS);

		double hours = 0.00;
		for (ITimedVariable<Object> workedHour : workedHours) {
			hours += ((Number) workedHour.getValue(workedHour.getPeriod()))
					.doubleValue();
		}

		assertEquals(days * 8.00,  hours, 0.00,WORKED_HOURS.getName());
	}

	@Test
	public void testPartialTimeWorkHoursI()
			throws ExpressionException, SQLException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfMonth(getToday()), new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(PARTIAL_TIME).getValue()));
						put(MONDAY_HOURS.getName(), format("%d", 1));
						put(TUESDAY_HOURS.getName(), format("%d", 1));
						put(WEDNESDAY_HOURS.getName(), format("%d", 1));
						put(THURSDAY_HOURS.getName(), format("%d", 1));
						put(FRIDAY_HOURS.getName(), format("%d", 1));
						put(SATURDAY_HOURS.getName(), format("%d", 1));
						put(SUNDAY_HOURS.getName(), format("%d", 1));
					}
				});

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);

		List<ITimedVariable<Object>> workedHours = ctx.getExpressionContext()
				.getVariables(WORKED_HOURS);

		double hours = 0.00;
		for (ITimedVariable<Object> workedHour : workedHours)
			hours += ((Number) workedHour.getValue(workedHour.getPeriod()))
					.doubleValue();

		int days = get(endDate, DAY_OF_MONTH);

		assertEquals(days, (int) hours,WORKED_HOURS.getName());
	}

	@Test
	public void testPartialTimeWorkHoursII()
			throws ExpressionException, SQLException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfMonth(getToday()), new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(PARTIAL_TIME).getValue()));
						put(MONDAY_HOURS.getName(), format("%d", 4));
						put(TUESDAY_HOURS.getName(), format("%d", 4));
						put(WEDNESDAY_HOURS.getName(), format("%d", 4));
						put(THURSDAY_HOURS.getName(), format("%d", 4));
						put(FRIDAY_HOURS.getName(), format("%d", 4));
					}
				});

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);

		List<ITimedVariable<Object>> workedHours = ctx.getExpressionContext()
				.getVariables(WORKED_HOURS);

		double hours = 0.00;
		for (ITimedVariable<Object> workedHour : workedHours)
			hours += ((Number) workedHour.getValue(workedHour.getPeriod()))
					.doubleValue();

		double expected = new Period(
				startDate, endDate)
						.daysStream()
						.collect(Collectors.summingDouble(day -> (day
								.get(DAY_OF_WEEK) == Calendar.SUNDAY
								|| day.get(DAY_OF_WEEK) == Calendar.SATURDAY)
										? 0.00 : 4.00));

		assertEquals(expected, hours,WORKED_HOURS.getName());
	}

	@Test
	public void testPartialTimeWorkHoursIII()
			throws ExpressionException, SQLException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, getToday(),
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(PARTIAL_TIME).getValue()));
						put(MONDAY_HOURS.getName(), format("%d", 4));
						put(TUESDAY_HOURS.getName(), format("%d", 4));
						put(WEDNESDAY_HOURS.getName(), format("%d", 4));
						put(THURSDAY_HOURS.getName(), format("%d", 4));
						put(FRIDAY_HOURS.getName(), format("%d", 4));
						put(SATURDAY_HOURS.getName(), format("%d", 4));
					}
				});

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);

		List<ITimedVariable<Object>> workedHours = ctx.getExpressionContext()
				.getVariables(WORKED_HOURS);

		double hours = 0.00;
		for (ITimedVariable<Object> workedHour : workedHours)
			hours += ((Number) workedHour.getValue(workedHour.getPeriod()))
					.doubleValue();

		double expected = new Period(getToday(), endDate).daysStream()
				.collect(Collectors.summingDouble(
						day -> (day.get(DAY_OF_WEEK) == Calendar.SUNDAY) ? 0.00
								: 4.00));
		// TODO: 
		if ( expected == 0.00 ) 
			expected = 8.00;

		assertEquals(expected, hours,WORKED_HOURS.getName());
	}

	@Test
	@Disabled("Upps")
	public void testPartialTimeWorkHoursIV()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, getToday(),
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(PARTIAL_TIME).getValue()));
						put(MONDAY_HOURS.getName(), format("%d", 0));
						put(TUESDAY_HOURS.getName(), format("%d", 0));
						put(WEDNESDAY_HOURS.getName(), format("%d", 4));
						put(THURSDAY_HOURS.getName(), format("%d", 4));
						put(FRIDAY_HOURS.getName(), format("%d", 4));
						put(SATURDAY_HOURS.getName(), format("%d", 4));
					}
				},
				new String[] { "250.00 * DIAS_TRABAJADOS / DIAS_MES",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES" },
				new String[] { "BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" },
				null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);

		List<ITimedVariable<Object>> workedHours = ctx.getExpressionContext()
				.getVariables(WORKED_HOURS);

		double hours = 0.00;
		for (ITimedVariable<Object> workedHour : workedHours)
			hours += ((Number) workedHour.getValue(workedHour.getPeriod()))
					.doubleValue();

		double expected = new Period(getToday(), endDate).daysStream()
				.collect(Collectors.summingDouble(
						day -> (day.get(DAY_OF_WEEK) == Calendar.SUNDAY
								|| day.get(DAY_OF_WEEK) == Calendar.MONDAY
								|| day.get(DAY_OF_WEEK) == Calendar.TUESDAY)
										? 0.00 : 4.00));

		assertEquals(expected, hours,WORKED_HOURS.getName());

		JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder(connection);
		new ContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();

		List<ContextData> datas = AON
				.getSalaries(aonContext,
						props -> props.getContractProperty()
								.eq(contract.getId()))
				.findFirst().get().getContextData().get(WORKED_HOURS.getName());
		;

		hours = 0.00;
		for (ContextData data : datas)
			hours += Double.parseDouble(data.getExpression());

		assertEquals(expected, hours,WORKED_HOURS.getName());
	}

	@Test
	public void testPartialTimeWorkHoursV()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, getToday(),
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(PARTIAL_TIME).getValue()));
						put(WORKED_HOURS.getName(), format("%d", 22));
					}
				},
				new String[] { 
						"250.00 * DIAS_TRABAJADOS / DIAS_MES",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES", 
						"HORAS_TRABAJADAS * 66.6" 
				},
				new String[] { "BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" },
				null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);

		List<ITimedVariable<Object>> workedHours = ctx.getExpressionContext()
				.getVariables(WORKED_HOURS);

		double hours = 0.00;
		for (ITimedVariable<Object> workedHour : workedHours)
			hours += ((Number) workedHour.getValue(workedHour.getPeriod()))
					.doubleValue();

		assertEquals(22.00, hours,WORKED_HOURS.getName());

		JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder(connection);
		new ContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();

		List<ContextData> datas = AON
				.getSalaries(aonContext,
						props -> props.getContractProperty()
								.eq(contract.getId()))
				.findFirst().get().getContextData().get(WORKED_HOURS.getName());
		;

		hours = 0.00;
		for (ContextData data : datas)
			hours += Double.parseDouble(data.getExpression());

		assertEquals(22.00, hours,WORKED_HOURS.getName());
	}

	@Test
	public void testPartialTimeWorkHoursVI()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(PARTIAL_TIME).getValue()));
						put(MONDAY_HOURS.getName(), format("%d", 0));
						put(TUESDAY_HOURS.getName(), format("%d", 0));
						put(WEDNESDAY_HOURS.getName(), format("%d", 4));
						put(THURSDAY_HOURS.getName(), format("%d", 4));
						put(FRIDAY_HOURS.getName(), format("%d", 8));
						put(SATURDAY_HOURS.getName(), format("%d", 8));
						put(SUNDAY_HOURS.getName(), format("%d", 0));
					}
				},
				new String[] { 
						"3000.00 * DIAS_TRABAJADOS / DIAS_MES ",
						"HORAS_TRABAJADAS * 0.00" 
					},
				new String[] {
						
				},
				null);
		
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);

		JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder(connection);
		new ContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();

		com.esferalia.aon.occam.api.model.Salary salary = AON
				.getSalaries(aonContext,
						props -> props.getContractProperty()
								.eq(contract.getId()))
				.findFirst().get();
		;
		
		Map<Integer,Double> weekHours = new HashMap<Integer,Double>();
		weekHours.put(Calendar.MONDAY, 0.00);
		weekHours.put(Calendar.TUESDAY, 0.00);
		weekHours.put(Calendar.WEDNESDAY,4.00);
		weekHours.put(Calendar.THURSDAY, 4.00);
		weekHours.put(Calendar.FRIDAY, 8.00);
		weekHours.put(Calendar.SATURDAY, 8.00);
		weekHours.put(Calendar.SUNDAY, 0.00);
		
		double expectedHours = 0.00;
		Calendar calendar = Calendar.getInstance();
		for ( calendar.setTime(startDate); calendar.getTime().compareTo(endDate) <= 0  ; calendar.add(Calendar.DAY_OF_MONTH, 1))
			expectedHours += weekHours.get(calendar.get(Calendar.DAY_OF_WEEK));
		
		List<ContextData> workedHours = 
				salary.getContextData().get(ContextVariable.WORKED_HOURS.getName());
		double actualHours = 0.00;
		for ( ContextData workedHour: workedHours) {
			actualHours += Double.parseDouble(workedHour.getExpression());
		}
		
		assertEquals(expectedHours, actualHours, 0.00);
		assertEquals(3000.00 * 24.00/40.00, salary.getTotalPayment(), 0.00);
	}

	@Test
	public void testPartialTimeWorkHoursVII()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(PARTIAL_TIME).getValue()));
						put(MONDAY_HOURS.getName(), format("%d", 0));
						put(TUESDAY_HOURS.getName(), format("%d", 0));
						put(WEDNESDAY_HOURS.getName(), format("%d", 4));
						put(THURSDAY_HOURS.getName(), format("%d", 4));
						put(FRIDAY_HOURS.getName(), format("%d", 8));
						put(SATURDAY_HOURS.getName(), format("%d", 8));
						put(SUNDAY_HOURS.getName(), format("%d", 0));
						
						put(PARTIAL_FACTOR.getName(), format("%f",0.60));
					}
				},
				new String[] { 
						"3000.00 * DIAS_TRABAJADOS / DIAS_MES",
						"HORAS_TRABAJADAS * 0.00"
				},
				new String[] {
						
				},
				null);
		
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);

		JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder(connection);
		new ContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();

		com.esferalia.aon.occam.api.model.Salary salary = AON
				.getSalaries(aonContext,
						props -> props.getContractProperty()
								.eq(contract.getId()))
				.findFirst().get();
		;
		
		Map<Integer,Double> weekHours = new HashMap<Integer,Double>();
		weekHours.put(Calendar.MONDAY, 0.00);
		weekHours.put(Calendar.TUESDAY, 0.00);
		weekHours.put(Calendar.WEDNESDAY,4.00);
		weekHours.put(Calendar.THURSDAY, 4.00);
		weekHours.put(Calendar.FRIDAY, 8.00);
		weekHours.put(Calendar.SATURDAY, 8.00);
		weekHours.put(Calendar.SUNDAY, 0.00);
		
		double expectedHours = 0.00;
		Calendar calendar = Calendar.getInstance();
		for ( calendar.setTime(startDate); calendar.getTime().compareTo(endDate) <= 0  ; calendar.add(Calendar.DAY_OF_MONTH, 1))
			expectedHours += weekHours.get(calendar.get(Calendar.DAY_OF_WEEK));
		
		List<ContextData> workedHours = 
				salary.getContextData().get(ContextVariable.WORKED_HOURS.getName());
		double actualHours = 0.00;
		for ( ContextData workedHour: workedHours) {
			actualHours += Double.parseDouble(workedHour.getExpression());
		}
		
		assertEquals(expectedHours, actualHours, 0.00);
		assertEquals(3000.00 * 24.00/40.00, salary.getTotalPayment(), 0.00);
	}

	@Test
	@Disabled("Upps ...")
	public void testPartialTimeWorkHoursVIII()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(PARTIAL_TIME).getValue()));
						
						put(PARTIAL_FACTOR.getName(), format("%f",0.60));
					}
				},
				new String[] { 
						"3000.00 * DIAS_TRABAJADOS / DIAS_MES"},
				new String[] {
						
				},
				null);
		
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);

		JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder(connection);
		new ContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();

		com.esferalia.aon.occam.api.model.Salary salary = AON
				.getSalaries(aonContext,
						props -> props.getContractProperty()
								.eq(contract.getId()))
				.findFirst().get();
		;
		
		Map<Integer,Double> weekHours = new HashMap<Integer,Double>();
		weekHours.put(Calendar.MONDAY, 0.00);
		weekHours.put(Calendar.TUESDAY, 0.00);
		weekHours.put(Calendar.WEDNESDAY,4.00);
		weekHours.put(Calendar.THURSDAY, 4.00);
		weekHours.put(Calendar.FRIDAY, 8.00);
		weekHours.put(Calendar.SATURDAY, 8.00);
		weekHours.put(Calendar.SUNDAY, 0.00);
		
		double expectedHours = 0.00;
		Calendar calendar = Calendar.getInstance();
		for ( calendar.setTime(startDate); calendar.getTime().compareTo(endDate) <= 0  ; calendar.add(Calendar.DAY_OF_MONTH, 1))
			expectedHours += weekHours.get(calendar.get(Calendar.DAY_OF_WEEK));
		
		List<ContextData> workedHours = 
				salary.getContextData().get(ContextVariable.WORKED_HOURS.getName());
		double actualHours = 0.00;
		for ( ContextData workedHour: workedHours) {
			actualHours += Double.parseDouble(workedHour.getExpression());
		}
		
		assertEquals(expectedHours, actualHours, 0.00);
		assertEquals(3000.00 * 24.00/40.00, salary.getTotalPayment(), 0.00);
	}

	@Test
	public void testHolidaysWorkHoursI()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Set<Integer> weekend = new HashSet<Integer>();
		weekend.add(Calendar.SATURDAY);
		weekend.add(Calendar.SUNDAY);
		
		Date date = getToday();
		while ( weekend.contains(get(date, Calendar.DAY_OF_WEEK)))
			date = add(date, Calendar.DAY_OF_MONTH, 1);
		
		Date holiday = date;
		
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
				8.00,//mondayHours, 
				8.00,//tuesdayHours, 
				8.00,//wednesdayHours, 
				8.00,//thursdayHours, 
				8.00,//fridayHours, 
				null,//saturdayHours, 
				null//sundayHours
				)
				;
		

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, getToday(),
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(PARTIAL_TIME).getValue()));
						put(MONDAY_HOURS.getName(), format("%d", 8));
						put(TUESDAY_HOURS.getName(), format("%d", 8));
						put(WEDNESDAY_HOURS.getName(), format("%d", 8));
						put(THURSDAY_HOURS.getName(), format("%d", 8));
						put(FRIDAY_HOURS.getName(), format("%d", 8));
					}
				},
				new String[] { 
						"250.00 * DIAS_TRABAJADOS / DIAS_MES",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"HORAS_TRABAJADAS * 0.00",
						"TRACE('HORAS_TRABAJADAS = %f\r\n', HORAS_TRABAJADAS); 0.00"},
				new String[] { 
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" },
				null,
				calendar);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);

//		ctx.getExpressionContext().getVariables(TC2).forEach(v -> System.out.println(v.getValue(v.getPeriod())));
		
		List<ITimedResult<Object>> workedHours = ctx.getExpressionContext().eval(WORKED_HOURS.toString(), getToday(), endDate);

//		List<ITimedVariable<Object>> workedHours = ctx.getExpressionContext()
//				.getVariables(WORKED_HOURS);

		double hours = 0.00;
		for (ITimedVariable<Object> workedHour : workedHours)
			hours += ((Number) workedHour.getValue(workedHour.getPeriod()))
					.doubleValue();

		double expected = new Period(getToday(), endDate)
				.daysStream()
				.peek( 
				day -> System.out.println(day.get(DAY_OF_MONTH) +" == " + get(holiday, DAY_OF_MONTH)
				+ "," + (day.get(DAY_OF_MONTH) == get(holiday, DAY_OF_MONTH)) ))
				.collect(Collectors.summingDouble(
						day -> (day.get(DAY_OF_MONTH) == get(holiday, DAY_OF_MONTH)
								|| day.get(DAY_OF_WEEK) == Calendar.SUNDAY
								|| day.get(DAY_OF_WEEK) == Calendar.SATURDAY)
										? 0.00 : 8.00));
		//if (expected == 0 ) 
		//	expected =  ( get(endDate, DAY_OF_MONTH) - get(getToday(), DAY_OF_MONTH) + 1 ) * 8 /*40/7.00*/; 
			
		if (expected > 0.00 )
			assertEquals(expected, hours,WORKED_HOURS.getName());

		JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder(connection);
		new ContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();

		List<ContextData> datas = AON
				.getSalaries(aonContext,
						props -> props.getContractProperty()
								.eq(contract.getId()))
				.findFirst().get().getContextData().get(WORKED_HOURS.getName());
		;

		hours = 0.00;
		for (ContextData data : datas)
			hours += Double.parseDouble(data.getExpression());
		
		// TODO: 
		expected = new Period(getToday(), endDate)
				.daysStream()
				.collect(Collectors.summingDouble(
						day -> (day.get(DAY_OF_MONTH) == get(holiday, DAY_OF_MONTH)
								|| day.get(DAY_OF_WEEK) == Calendar.SUNDAY
								|| day.get(DAY_OF_WEEK) == Calendar.SATURDAY)
										? 0.00 : 8.00));
//		if (expected == 0 ) 
//			expected =  ( get(endDate, DAY_OF_MONTH) - get(getToday(), DAY_OF_MONTH) + 1 ) * 8 /*40/7.00*/; 
		if ( expected > 0.0 )
			assertEquals(expected, hours,WORKED_HOURS.getName());
		
		

		datas = AON
				.getSalaries(aonContext,
						props -> props.getContractProperty()
								.eq(contract.getId()))
				.findFirst().get().getContextData().get(PARTIAL_FACTOR.getName());
		;

		if (expected > 0.00 )
			for (ContextData data : datas)
				assertEquals(1.00, Double.parseDouble(data.getExpression()),PARTIAL_FACTOR.getName());
		
	}


	@Test
	public void testPartialTimeSalaryHoursI()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, getToday(),
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(PARTIAL_TIME).getValue()));

						put(MONDAY_HOURS.getName(), format("%d", 4));
						put(TUESDAY_HOURS.getName(), format("%d", 4));
						put(WEDNESDAY_HOURS.getName(), format("%d", 4));
						put(THURSDAY_HOURS.getName(), format("%d", 4));
						put(FRIDAY_HOURS.getName(), format("%d", 4));
					}
				},

				new String[] { "250.00 * DIAS_TRABAJADOS / DIAS_MES",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES" },

				new String[] { "BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" },
				null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);

		List<ITimedVariable<Object>> workedHours = ctx.getExpressionContext()
				.getVariables(WORKED_HOURS);

		List<ITimedVariable<Object>> salaryHours = ctx.getExpressionContext()
				.getVariables(SALARY_HOURS);

		for (int i = 0; i < workedHours.size(); i++)
			assertEquals(
					workedHours.get(i).getValue(workedHours.get(i).getPeriod()),
					salaryHours.get(i)
							.getValue(salaryHours.get(i).getPeriod()));
		
	}

	@Test
	public void testPartialTimeWorkedHoursIT()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(PARTIAL_TIME).getValue()));

						put(MONDAY_HOURS.getName(), format("%d", 4));
						put(TUESDAY_HOURS.getName(), format("%d", 4));
						put(WEDNESDAY_HOURS.getName(), format("%d", 4));
						put(THURSDAY_HOURS.getName(), format("%d", 4));
						put(FRIDAY_HOURS.getName(), format("%d", 4));
					}
				},

				new String[] { "250.00 * DIAS_TRABAJADOS / DIAS_MES",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES" },

				new String[] { "BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" },
				null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		Date startIt = add(startDate, Calendar.DAY_OF_MONTH, 10);
		Date endIt = add(startDate, Calendar.DAY_OF_MONTH, 20);

		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startIt, endIt,
				null);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);

		List<ITimedVariable<Object>> workedHours = ctx.getExpressionContext()
				.getVariables(WORKED_HOURS);

		assertEquals(2, workedHours.size());
		assertEquals(
				new Period(startDate, add(startIt, DAY_OF_MONTH, -1)),
				workedHours.get(0).getPeriod());
		assertEquals(new Period(add(endIt, DAY_OF_MONTH, 1), endDate),
				workedHours.get(1).getPeriod());

	}

	@Test
	public void testPartialTimeUndefinedWorkedHours()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(PARTIAL_TIME).getValue()));

						put(ContextVariable.QUOTE_GROUP.getName(), "'07'");
					}
				},

				new String[] {
						"TRACE('DIAS_TRABAJADOS = %f\r\n', DIAS_TRABAJADOS); 0.00", 
						"TRACE('TIEMPO_COMPLETO = %s\r\n', TIEMPO_COMPLETO); 0.00", 
						},
				new String[] {}, null);

		addSystemData(aonContext, getFirstDayOfYear(getToday()), null,
				new HashMap() {
					{
						put("BASE_CGC_MIN",
								"[ \"01\":(TIEMPO_COMPLETO ? 1056.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 6.37 * HORAS_NOMINA), \"02\":(TIEMPO_COMPLETO ? 876.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 5.28 * HORAS_NOMINA), \"03\":(TIEMPO_COMPLETO ? 762.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.59 * HORAS_NOMINA), \"04\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA), \"05\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA), \"06\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA), \"07\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA), \"08\":(TIEMPO_COMPLETO ? 25.22 * DIAS_NOMINA : 4.56 * HORAS_NOMINA), \"09\":(TIEMPO_COMPLETO ? 25.22 * DIAS_NOMINA : 4.56 * HORAS_NOMINA), \"10\":(TIEMPO_COMPLETO ? 25.22* DIAS_NOMINA : 4.56 * HORAS_NOMINA), \"11\":(TIEMPO_COMPLETO ? 25.22 * DIAS_NOMINA : 4.56 * HORAS_NOMINA) ] [GRUPO_COTIZACION]");
					}
				});

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);
		ctx.setListener(new IContractSalaryCalculatorContext.IListener() {
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				// TODO Auto-generated method stub
			}
			public void onUndefinedData(IExpression expression, String variableName, String message, java.util.Date start, java.util.Date end) {
				
				System.out.printf("%s , %tF..%tF \r\n", variableName, start, end  );
			};
		} );
		new ContractSalaryCalculator<Salary>(
				new SalaryBuilder())
		.calculate(ctx);


	}

	@Test
	public void testPartialTimeHolidaysWorkedHours()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(PARTIAL_TIME).getValue()));

						put(MONDAY_HOURS.getName(), format("%d", 4));
						put(TUESDAY_HOURS.getName(), format("%d", 4));
						put(WEDNESDAY_HOURS.getName(), format("%d", 4));
						put(FRIDAY_HOURS.getName(), format("%d", 4));
					}
				},

				new String[] { "250.00 * DIAS_TRABAJADOS / DIAS_MES",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES" },

				new String[] { "BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" },
				null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;
		

		
		Date startHolidays = add(endDate, Calendar.DAY_OF_MONTH, -9);
		Date endWorkedDays = add(startHolidays, Calendar.DAY_OF_MONTH, -1);
		addData(aonContext, contract, startHolidays, endDate, ContextVariable.HOLIDAYS, "10");

//		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), ContextVariable.THURSDAY_HOURS, "4");

		addData(aonContext, contract, contract.getStartDate(), endWorkedDays, ContextVariable.THURSDAY_HOURS, "4");
		addData(aonContext, contract, startHolidays, endDate, ContextVariable.THURSDAY_HOURS, "4");
		addData(aonContext, contract, add(endDate, Calendar.DAY_OF_MONTH,1), contract.getEndDate(), ContextVariable.THURSDAY_HOURS, "4");
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);

		double expectedHours =  
		new Period(startDate, endWorkedDays).daysStream()
		.filter(d -> d.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY )
		.filter(d -> d.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY )
		.peek( d -> System.out.println(d.get(Calendar.DAY_OF_MONTH) + "-." + d.get(Calendar.DAY_OF_WEEK)))
		.count() * 4.00;
		
		double workedHours = ctx.getExpressionContext()
		.getVariables(WORKED_HOURS).stream()
//		.peek( v -> System.out.println("****"+ v.getPeriod().getStart() + "," + v.getPeriod().getEnd() + ":" + v.getValue(v.getPeriod())) )
		.collect(Collectors.summingDouble(v -> ((Number)v.getValue(v.getPeriod())).doubleValue() ))
		;
		

		assertEquals(expectedHours, workedHours, 0.00 );
		
	}

	// ------------------------------------------------------------------------

	protected static <T> T random(T arr[]) {
		return arr[(int) ((int) (Math.random() * arr.length))];
	}

}
