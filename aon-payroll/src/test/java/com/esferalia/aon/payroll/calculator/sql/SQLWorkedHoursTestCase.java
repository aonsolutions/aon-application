/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.FRIDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SATURDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SUNDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.THURSDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TUESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WEDNESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C200;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C209;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C230;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C239;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C250;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C289;
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
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.lang.String.format;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import junit.framework.Assert;

import org.junit.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;

/**
 * @author rtrepiana
 *
 */
public class SQLWorkedHoursTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000000001;

	private static ContractCode PARTIAL_TIME[] = { C200, C209, C230, C239,
			C250, C289, C501, C502, C503, C508, C510, C518, C520, C530, C540,
			C541, C550, C552, };

	@Test
	public void testPartialTimeWorkHoursI() throws ExpressionException,
			SQLException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfMonth(getToday()), new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(),
								format("\"%s\"", random(PARTIAL_TIME)
										.getValue()));
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

		Assert.assertEquals(WORKED_HOURS.getName(), days, (int) hours);
	}

	@Test
	public void testPartialTimeWorkHoursII() throws ExpressionException,
			SQLException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfMonth(getToday()), new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(),
								format("\"%s\"", random(PARTIAL_TIME)
										.getValue()));
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


		double expected = new Period(startDate, endDate)
				.daysStream()
				.collect(
						Collectors.summingDouble(day -> (day.get(DAY_OF_WEEK) == Calendar.SUNDAY || day
								.get(DAY_OF_WEEK) == Calendar.SATURDAY) ? 0.00
								: 4.00));

		Assert.assertEquals(WORKED_HOURS.getName(), expected,  hours);
	}

	@Test
	public void testPartialTimeWorkHoursIII() throws ExpressionException,
			SQLException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				getToday(), new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(),
								format("\"%s\"", random(PARTIAL_TIME)
										.getValue()));
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


		double expected = new Period(getToday(), endDate)
				.daysStream()
				.collect(
						Collectors.summingDouble(day -> ( day
								.get(DAY_OF_WEEK) == Calendar.SATURDAY) ? 0.00
								: 4.00));

		Assert.assertEquals(WORKED_HOURS.getName(), expected,  hours);
	}

	@Test
	public void testPartialTimeWorkHoursIV() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				getToday(), new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(),
								format("\"%s\"", random(PARTIAL_TIME)
										.getValue()));
						put(MONDAY_HOURS.getName(), format("%d", 0));
						put(TUESDAY_HOURS.getName(), format("%d", 0));
						put(WEDNESDAY_HOURS.getName(), format("%d", 4));
						put(THURSDAY_HOURS.getName(), format("%d", 4));
						put(FRIDAY_HOURS.getName(), format("%d", 4));
						put(SATURDAY_HOURS.getName(), format("%d", 4));
					}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {						
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, null);

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


		double expected = new Period(getToday(), endDate)
				.daysStream()
				.collect(
						Collectors.summingDouble(day -> ( 
								day.get(DAY_OF_WEEK) == Calendar.SATURDAY ||
								day.get(DAY_OF_WEEK) == Calendar.MONDAY ||
								day.get(DAY_OF_WEEK) == Calendar.TUESDAY)
								? 0.00
								: 4.00));

		Assert.assertEquals(WORKED_HOURS.getName(), expected,  hours);
		
		JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder(connection);
		new ContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();
		
		List<ContextData> datas = 
		AON.getSalaries(aonContext, props->props.getContractProperty().eq(contract.getId()))
		.findFirst().get().getContextData().get(WORKED_HOURS.getName());
		;
		
		
		hours = 0.00;
		for ( ContextData data: datas)
			hours += Double.parseDouble(data.getExpression());

		Assert.assertEquals(WORKED_HOURS.getName(), expected,  hours);
	}

	@Test
	public void testPartialTimeWorkHoursV() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				getToday(), new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(),
								format("\"%s\"", random(PARTIAL_TIME)
										.getValue()));
						put(WORKED_HOURS.getName(), format("%d", 22));
					}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {						
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, null);

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



		Assert.assertEquals(WORKED_HOURS.getName(), 22.00,  hours);
		
		JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder(connection);
		new ContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();
		
		List<ContextData> datas = 
		AON.getSalaries(aonContext, props->props.getContractProperty().eq(contract.getId()))
		.findFirst().get().getContextData().get(WORKED_HOURS.getName());
		;
		
		
		hours = 0.00;
		for ( ContextData data: datas)
			hours += Double.parseDouble(data.getExpression());

		Assert.assertEquals(WORKED_HOURS.getName(), 22.00,  hours);
	}
	// ------------------------------------------------------------------------

	protected static <T> T random(T arr[]) {
		return arr[(int) ((int) (Math.random() * arr.length))];
	}

}
