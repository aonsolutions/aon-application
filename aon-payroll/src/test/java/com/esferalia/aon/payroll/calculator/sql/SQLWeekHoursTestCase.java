/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.FRIDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PARTIAL_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SATURDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SUNDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.THURSDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TUESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WEDNESDAY_HOURS;
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
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.lang.String.format;
import static java.util.Calendar.DAY_OF_MONTH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;

/**
 * @author rtrepiana
 *
 */
public class SQLWeekHoursTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000000001;

	private static ContractCode PARTIAL_TIME[] = { C200, C209, C230, C239, C250,
			C289, C501, C502, C503, C508, C510, C518, C520, C530, C540, C541,
			C550, C552, };

	@Test
	public void testPartialTimeWeekHoursI()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfMonth(getToday())
		, new HashMap<String, String>() {
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
				}
		,new String[] { 
				"250.00 * DIAS_TRABAJADOS / DIAS_MES",
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES" },
		new String[] { 
				"BASE_CGC * 0.10", "BASE_CGP * 0.05",
				//"BASE_IRPF * PORCENTAJE_IRPF/100" 
				},
			null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);

		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		new ContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();

		Map<String, List<ContextData>> datas = AON
				.getSalaries(aonContext,
						props -> props.getContractProperty()
								.eq(contract.getId()))
				.findFirst().get().getContextData();
		;

		for ( String hoursVar : new String[]{						
						MONDAY_HOURS.getName(),
						TUESDAY_HOURS.getName(),
						WEDNESDAY_HOURS.getName(),
						THURSDAY_HOURS.getName(),
						FRIDAY_HOURS.getName(),
						SATURDAY_HOURS.getName(),
						SUNDAY_HOURS.getName()} ) 
		{
			List<ContextData> data = datas.get(hoursVar);
			assertEquals(1, data.size(),hoursVar);
			assertEquals(startDate,data.get(0).getStartDate(),hoursVar);
			assertEquals(endDate, data.get(0).getEndDate(),hoursVar);
			assertEquals(1.00, Double.parseDouble(data.get(0).getExpression()),hoursVar);
		}

	}
	// ------------------------------------------------------------------------

	@Test
	public void testPartialTimeWeekHoursII()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date contractStartDate =  add(getFirstDayOfMonth(getToday()), Calendar.DAY_OF_MONTH, 13);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
		contractStartDate
		, new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(PARTIAL_TIME).getValue()));
						put(MONDAY_HOURS.getName(), format("%d", 2));
						put(TUESDAY_HOURS.getName(), format("%d", 2));
						put(WEDNESDAY_HOURS.getName(), format("%d", 6));
						put(THURSDAY_HOURS.getName(), format("%d", 6));
						put(FRIDAY_HOURS.getName(), format("%d", 6));
					}
				}
		,new String[] { 
				"250.00 * DIAS_TRABAJADOS / DIAS_MES",
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES" },
		new String[] { 
				"BASE_CGC * 0.10", "BASE_CGP * 0.05",
				//"BASE_IRPF * PORCENTAJE_IRPF/100" 
				},
			null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);

		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		new ContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();

		Map<String, List<ContextData>> datas = AON
				.getSalaries(aonContext,
						props -> props.getContractProperty()
								.eq(contract.getId()))
				.findFirst().get().getContextData();
		;

		for ( String hoursVar : new String[]{						
						MONDAY_HOURS.getName(),
						TUESDAY_HOURS.getName()}
		) 
		{
			List<ContextData> data = datas.get(hoursVar);
			assertEquals( 1, data.size(),hoursVar);
			assertEquals(contractStartDate,data.get(0).getStartDate(),hoursVar);
			assertEquals(endDate, data.get(0).getEndDate(),hoursVar);
			assertEquals( 2.00, Double.parseDouble(data.get(0).getExpression()),hoursVar);
		}

		for ( String hoursVar : new String[]{						
				WEDNESDAY_HOURS.getName(),
				THURSDAY_HOURS.getName(),
				FRIDAY_HOURS.getName()
				}
		) 
		{
			List<ContextData> data = datas.get(hoursVar);
			assertEquals(1, data.size(),hoursVar);
			assertEquals(contractStartDate,data.get(0).getStartDate(),hoursVar);
			assertEquals(endDate, data.get(0).getEndDate(),hoursVar);
			assertEquals( 6.00, Double.parseDouble(data.get(0).getExpression()),hoursVar);
		}

		for ( String hoursVar : new String[]{						
				SUNDAY_HOURS.getName(),
				SATURDAY_HOURS.getName()
				}
		) 
		{
			List<ContextData> data = datas.get(hoursVar);
			assertEquals(1, data.size(),hoursVar);
			assertEquals(contractStartDate,data.get(0).getStartDate(),hoursVar);
			assertEquals(endDate, data.get(0).getEndDate(),hoursVar);
			assertEquals(-1.00, Double.parseDouble(data.get(0).getExpression()),hoursVar);
		}
	}
	@Test
	public void testPartialTimeWeekHoursIII()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date contractStartDate =  add(getFirstDayOfMonth(getToday()), Calendar.DAY_OF_MONTH, 13);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
		contractStartDate
		, new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(PARTIAL_TIME).getValue()));
						put(MONDAY_HOURS.getName(), format("%d", 2));
						put(TUESDAY_HOURS.getName(), format("%d", 2));
						put(WEDNESDAY_HOURS.getName(), format("%d", 6));
						put(THURSDAY_HOURS.getName(), format("%d", 6));
						put(FRIDAY_HOURS.getName(), format("%d", 6));
						put(PARTIAL_FACTOR.getName(), format("%f", 0.75));
					}
				}
		,new String[] { 
				"250.00 * DIAS_TRABAJADOS / DIAS_MES",
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES" },
		new String[] { 
				"BASE_CGC * 0.10", "BASE_CGP * 0.05",
				//"BASE_IRPF * PORCENTAJE_IRPF/100" 
				},
			null);

		Date startDate = getFirstDayOfMonth(contractStartDate);
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);

		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		new ContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();

		Map<String, List<ContextData>> datas = AON
				.getSalaries(aonContext,
						props -> props.getContractProperty()
								.eq(contract.getId()))
				.findFirst().get().getContextData();
		;

		for ( String hoursVar : new String[]{						
						MONDAY_HOURS.getName(),
						TUESDAY_HOURS.getName()}
		) 
		{
			List<ContextData> data = datas.get(hoursVar);
			assertEquals( 1, data.size(),hoursVar);
			assertEquals(contractStartDate,data.get(0).getStartDate(),hoursVar);
			assertEquals(endDate, data.get(0).getEndDate(),hoursVar);
			assertEquals( 2.00, Double.parseDouble(data.get(0).getExpression()),hoursVar);
		}

		for ( String hoursVar : new String[]{						
				WEDNESDAY_HOURS.getName(),
				THURSDAY_HOURS.getName(),
				FRIDAY_HOURS.getName()
				}
		) 
		{
			List<ContextData> data = datas.get(hoursVar);
			assertEquals( 1, data.size(),hoursVar);
			assertEquals(contractStartDate,data.get(0).getStartDate(),hoursVar);
			assertEquals(endDate, data.get(0).getEndDate(),hoursVar);
			assertEquals( 6.00, Double.parseDouble(data.get(0).getExpression()),hoursVar);
		}
	}

	@Test
	public void testFullTimeWeekHoursIV()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date contractStartDate =  add(getFirstDayOfMonth(getToday()), Calendar.DAY_OF_MONTH, 13);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
		contractStartDate
		, new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								"100"));
					}
				}
		,new String[] { 
				"250.00 * DIAS_TRABAJADOS / DIAS_MES",
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES" },
		new String[] { 
				"BASE_CGC * 0.10", "BASE_CGP * 0.05",
				//"BASE_IRPF * PORCENTAJE_IRPF/100" 
				},
			null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);

		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		new ContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();

		Map<String, List<ContextData>> datas = AON
				.getSalaries(aonContext,
						props -> props.getContractProperty()
								.eq(contract.getId()))
				.findFirst().get().getContextData();
		;

		for ( String hoursVar : new String[]{						
				MONDAY_HOURS.getName(),
				TUESDAY_HOURS.getName(),
				WEDNESDAY_HOURS.getName(),
				THURSDAY_HOURS.getName(),
				FRIDAY_HOURS.getName(),
				SUNDAY_HOURS.getName(),
				SATURDAY_HOURS.getName()
				}
		) 
		{
			List<ContextData> data = datas.get(hoursVar);
			assertNull(data);
		}
		
	}

	@Test
	public void testPartialTimeWeekHoursV()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date contractStartDate =  getLastDayOfMonth(getToday());
		Date contractEndDate =  add(contractStartDate, DAY_OF_MONTH,1);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
		contractStartDate,
		contractEndDate
		, new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(PARTIAL_TIME).getValue()));
						put(MONDAY_HOURS.getName(), format("%d", 2));
						put(TUESDAY_HOURS.getName(), format("%d", 2));
						put(WEDNESDAY_HOURS.getName(), format("%d", 6));
						put(THURSDAY_HOURS.getName(), format("%d", 6));
						put(FRIDAY_HOURS.getName(), format("%d", 6));
						put(PARTIAL_FACTOR.getName(), format("%f", 0.75));
					}
				}
		,new String[] { 
				"250.00 * DIAS_TRABAJADOS / DIAS_MES",
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES" },
		new String[] { 
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				//"BASE_IRPF * PORCENTAJE_IRPF/100" 
				},
			null);

		Date startDate = getFirstDayOfMonth(contractEndDate);
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);

		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		new ContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();

		Map<String, List<ContextData>> datas = AON
				.getSalaries(aonContext,
						props -> props.getContractProperty()
								.eq(contract.getId()))
				.findFirst().get().getContextData();
		;

		for ( String hoursVar : new String[]{						
						MONDAY_HOURS.getName(),
						TUESDAY_HOURS.getName()}
		) 
		{
			List<ContextData> data = datas.get(hoursVar);
			assertEquals( 1, data.size(),hoursVar);
			assertEquals(startDate,data.get(0).getStartDate(),hoursVar);
			assertEquals(contractEndDate, data.get(0).getEndDate(),hoursVar);
			assertEquals( 2.00, Double.parseDouble(data.get(0).getExpression()),hoursVar);
		}

		for ( String hoursVar : new String[]{						
				WEDNESDAY_HOURS.getName(),
				THURSDAY_HOURS.getName(),
				FRIDAY_HOURS.getName()
				}
		) 
		{
			List<ContextData> data = datas.get(hoursVar);
			assertEquals( 1, data.size(),hoursVar);
			assertEquals(startDate,data.get(0).getStartDate(),hoursVar);
			assertEquals(contractEndDate, data.get(0).getEndDate(),hoursVar);
			assertEquals( 6.00, Double.parseDouble(data.get(0).getExpression()),hoursVar);
		}
	}

	@Test
	public void testPartialTimeWeekHoursVI()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date contractStartDate =  add(getFirstDayOfMonth(getToday()), Calendar.DAY_OF_MONTH, 13);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
		contractStartDate
		, new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(PARTIAL_TIME).getValue()));
						put(MONDAY_HOURS.getName(), format("%d", 2));
						put(TUESDAY_HOURS.getName(), format("%d", 2));
						put(WEDNESDAY_HOURS.getName(), format("%d", 6));
						put(THURSDAY_HOURS.getName(), format("%d", 6));
						put(FRIDAY_HOURS.getName(), format("%d", 6));
						put(PARTIAL_FACTOR.getName(), format("%f", 0.75));
					}
				}
		,new String[] { 
				"250.00 * DIAS_TRABAJADOS / DIAS_MES",
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES" },
		new String[] { 
				"BASE_CGC * 0.10", "BASE_CGP * 0.05",
				//"BASE_IRPF * PORCENTAJE_IRPF/100" 
				},
			null);
		
		
		// Next month PARTIAL_FACTOR until 11th day
		Date startDate = getFirstDayOfMonth(add(contractStartDate, Calendar.MONTH,1));
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		addData(aonContext, contract, startDate, add(startDate, DAY_OF_MONTH,10), ContextVariable.PARTIAL_FACTOR, "0.333");

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);

		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		new ContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();

		Map<String, List<ContextData>> datas = AON
				.getSalaries(aonContext,
						props -> props.getContractProperty()
								.eq(contract.getId()))
				.findFirst().get().getContextData();
		;

		for ( String hoursVar : new String[]{						
						MONDAY_HOURS.getName(),
						TUESDAY_HOURS.getName()}
		) 
		{
			List<ContextData> data = datas.get(hoursVar);
			assertEquals( 1, data.size(),hoursVar);
			assertEquals(startDate,data.get(0).getStartDate(),hoursVar);
			assertEquals(endDate, data.get(0).getEndDate(),hoursVar);
			assertEquals( 2.00, Double.parseDouble(data.get(0).getExpression()),hoursVar);
		}

		for ( String hoursVar : new String[]{						
				WEDNESDAY_HOURS.getName(),
				THURSDAY_HOURS.getName(),
				FRIDAY_HOURS.getName()
				}
		) 
		{
			List<ContextData> data = datas.get(hoursVar);
			assertEquals( 1, data.size(),hoursVar);
			assertEquals(startDate,data.get(0).getStartDate(),hoursVar);
			assertEquals(endDate, data.get(0).getEndDate(),hoursVar);
			assertEquals( 6.00, Double.parseDouble(data.get(0).getExpression()),hoursVar);
		}
	}

	@Test
	public void testPartialTimeWeekHoursVII()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date contractStartDate =  add(getFirstDayOfMonth(getToday()), Calendar.DAY_OF_MONTH, 13);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
		contractStartDate
		, new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(PARTIAL_TIME).getValue()));
						put(MONDAY_HOURS.getName(), format("%d", 2));
						put(TUESDAY_HOURS.getName(), format("%d", 2));
						put(WEDNESDAY_HOURS.getName(), format("%d", 6));
						put(THURSDAY_HOURS.getName(), format("%d", 6));
						put(FRIDAY_HOURS.getName(), format("%d", 6));
//						put(PARTIAL_FACTOR.getName(), format("%f", 0.75));
					}
				}
		,new String[] { 
				"250.00 * DIAS_TRABAJADOS / DIAS_MES",
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES" },
		new String[] { 
				"BASE_CGC * 0.10", "BASE_CGP * 0.05",
				//"BASE_IRPF * PORCENTAJE_IRPF/100" 
				},
			null);
		
		
		// Next month PARTIAL_FACTOR until 11th day
		Date startDate = getFirstDayOfMonth(add(contractStartDate, Calendar.MONTH,1));
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;
		
		
		Date breakDate = add(startDate, DAY_OF_MONTH,10);
		addData(aonContext, contract, breakDate, null, MONDAY_HOURS, "3");
		addData(aonContext, contract, breakDate, null, TUESDAY_HOURS, "3");
		addData(aonContext, contract, breakDate, null, WEDNESDAY_HOURS, "3");
		addData(aonContext, contract, breakDate, null, THURSDAY_HOURS, "3");
		addData(aonContext, contract, breakDate, null, FRIDAY_HOURS, "3");

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);

		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		new ContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();

		Map<String, List<ContextData>> datas = AON
				.getSalaries(aonContext,
						props -> props.getContractProperty()
								.eq(contract.getId()))
				.findFirst().get().getContextData();
		;

		for ( String hoursVar : new String[]{						
						MONDAY_HOURS.getName(),
						TUESDAY_HOURS.getName()}
		) 
		{
			List<ContextData> data = datas.get(hoursVar);
			assertEquals( 2, data.size(),hoursVar);
			
			assertEquals(startDate,data.get(0).getStartDate(),hoursVar);
			assertEquals(add(breakDate, DAY_OF_MONTH,-1), data.get(0).getEndDate(),hoursVar);
			assertEquals( 2.00, Double.parseDouble(data.get(0).getExpression()),hoursVar);

			assertEquals(breakDate,data.get(1).getStartDate(),hoursVar);
			assertEquals(endDate, data.get(1).getEndDate(),hoursVar);
			assertEquals( 3.00, Double.parseDouble(data.get(1).getExpression()),hoursVar);
		}

		for ( String hoursVar : new String[]{						
				WEDNESDAY_HOURS.getName(),
				THURSDAY_HOURS.getName(),
				FRIDAY_HOURS.getName()
				}
		) 
		{
			List<ContextData> data = datas.get(hoursVar);
			assertEquals( 2, data.size(),hoursVar);
			assertEquals(startDate,data.get(0).getStartDate(),hoursVar);
			assertEquals(add(breakDate, DAY_OF_MONTH,-1), data.get(0).getEndDate(),hoursVar);
			assertEquals( 6.00, Double.parseDouble(data.get(0).getExpression()),hoursVar);

			assertEquals(breakDate,data.get(1).getStartDate(),hoursVar);
			assertEquals(endDate, data.get(1).getEndDate(),hoursVar);
			assertEquals( 3.00, Double.parseDouble(data.get(1).getExpression()),hoursVar);
		}
	}

	@Test
	public void testPartialTimeWeekHoursVIII()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date contractStartDate =  add(getFirstDayOfMonth(getToday()), Calendar.DAY_OF_MONTH, 13);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
		contractStartDate
		, new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(PARTIAL_TIME).getValue()));
						put(MONDAY_HOURS.getName(), format("%d", 2));
						put(TUESDAY_HOURS.getName(), format("%d", 2));
						put(WEDNESDAY_HOURS.getName(), format("%d", 6));
						put(THURSDAY_HOURS.getName(), format("%d", 6));
						put(FRIDAY_HOURS.getName(), format("%d", 6));
					}
				}
		,new String[] { 
				"250.00 * DIAS_TRABAJADOS / DIAS_MES",
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES" },
		new String[] { 
				"BASE_CGC * 0.10", "BASE_CGP * 0.05",
				//"BASE_IRPF * PORCENTAJE_IRPF/100" 
				},
			null);
		
		
		// Next month PARTIAL_FACTOR until 11th day
		Date startDate = getFirstDayOfMonth(add(contractStartDate, Calendar.MONTH,1));
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;
		
		
		Date breakDate = add(startDate, DAY_OF_MONTH,10);
		addData(aonContext, contract, breakDate, null, PARTIAL_FACTOR, "0.666");
		addData(aonContext, contract, breakDate, null, MONDAY_HOURS, "3");
		addData(aonContext, contract, breakDate, null, TUESDAY_HOURS, "3");
		addData(aonContext, contract, breakDate, null, WEDNESDAY_HOURS, "3");
		addData(aonContext, contract, breakDate, null, THURSDAY_HOURS, "3");
		addData(aonContext, contract, breakDate, null, FRIDAY_HOURS, "3");

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);

		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		new ContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();

		Map<String, List<ContextData>> datas = AON
				.getSalaries(aonContext,
						props -> props.getContractProperty()
								.eq(contract.getId()))
				.findFirst().get().getContextData();
		;

		for ( String hoursVar : new String[]{						
						MONDAY_HOURS.getName(),
						TUESDAY_HOURS.getName()}
		) 
		{
			List<ContextData> data = datas.get(hoursVar);
			assertEquals( 2, data.size(),hoursVar);
			
			assertEquals(startDate,data.get(0).getStartDate(),hoursVar);
			assertEquals(add(breakDate, DAY_OF_MONTH,-1), data.get(0).getEndDate(),hoursVar);
			assertEquals( 2.00, Double.parseDouble(data.get(0).getExpression()),hoursVar);

			assertEquals(breakDate,data.get(1).getStartDate(),hoursVar);
			assertEquals(endDate, data.get(1).getEndDate(),hoursVar);
			assertEquals( 3.00, Double.parseDouble(data.get(1).getExpression()),hoursVar);
		}

		for ( String hoursVar : new String[]{						
				WEDNESDAY_HOURS.getName(),
				THURSDAY_HOURS.getName(),
				FRIDAY_HOURS.getName()
				}
		) 
		{
			List<ContextData> data = datas.get(hoursVar);
			assertEquals( 2, data.size(),hoursVar);
			assertEquals(startDate,data.get(0).getStartDate(),hoursVar);
			assertEquals(add(breakDate, DAY_OF_MONTH,-1), data.get(0).getEndDate(),hoursVar);
			assertEquals( 6.00, Double.parseDouble(data.get(0).getExpression()),hoursVar);

			assertEquals(breakDate,data.get(1).getStartDate(),hoursVar);
			assertEquals(endDate, data.get(1).getEndDate(),hoursVar);
			assertEquals( 3.00, Double.parseDouble(data.get(1).getExpression()),hoursVar);
		}
	}
	@Test
	public void testNullTimeWeekHoursI()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfMonth(getToday())
		, new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(PARTIAL_TIME).getValue()));
						put(MONDAY_HOURS.getName(), format("%d", 1));
						put(TUESDAY_HOURS.getName(), format("%d", 1));
						put(WEDNESDAY_HOURS.getName(), format("%d", 1));
						put(THURSDAY_HOURS.getName(), format("%d", 1));
						put(FRIDAY_HOURS.getName(), format("%d", 1));
						put(SATURDAY_HOURS.getName(), format("%d", 1));
						put(SUNDAY_HOURS.getName(), null);
					}
				}
		,new String[] { 
				"250.00 * DIAS_TRABAJADOS / DIAS_MES",
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES" },
		new String[] { "BASE_CGC * 0.10", "BASE_CGP * 0.05",
				//"BASE_IRPF * PORCENTAJE_IRPF/100" 
				},
			null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);

		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();

		Map<String, List<ContextData>> datas = AON
				.getSalaries(aonContext,
						props -> props.getContractProperty()
								.eq(contract.getId()))
				.findFirst().get().getContextData();
		;

		for ( String hoursVar : new String[]{						
						MONDAY_HOURS.getName(),
						TUESDAY_HOURS.getName(),
						WEDNESDAY_HOURS.getName(),
						THURSDAY_HOURS.getName(),
						FRIDAY_HOURS.getName(),
						SATURDAY_HOURS.getName()} ) 
		{
			List<ContextData> data = datas.get(hoursVar);
			assertEquals( 1, data.size(),hoursVar);
			assertEquals(startDate,data.get(0).getStartDate(),hoursVar);
			assertEquals(endDate, data.get(0).getEndDate(),hoursVar);
			assertEquals( 1.00, Double.parseDouble(data.get(0).getExpression()),hoursVar);
		}

		for ( String hoursVar : new String[]{						
				SUNDAY_HOURS.getName()} ) 
		{
			List<ContextData> data = datas.get(hoursVar);
			assertEquals( 1, data.size(),hoursVar);
			assertEquals(startDate,data.get(0).getStartDate(),hoursVar);
			assertEquals(endDate, data.get(0).getEndDate(),hoursVar);
			assertEquals( 0.00, Double.parseDouble(data.get(0).getExpression()),hoursVar);
		}

	}
	
	@Test
	public void testEmptyTimeWeekHoursI()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfMonth(getToday())
		, new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(PARTIAL_TIME).getValue()));
						put(MONDAY_HOURS.getName(), format("%d", 1));
						put(TUESDAY_HOURS.getName(), format("%d", 1));
						put(WEDNESDAY_HOURS.getName(), format("%d", 1));
						put(THURSDAY_HOURS.getName(), format("%d", 1));
						put(FRIDAY_HOURS.getName(), format("%d", 1));
						put(SATURDAY_HOURS.getName(), format("%d", 1));
						put(SUNDAY_HOURS.getName(), " ");
					}
				}
		,new String[] { 
				"250.00 * DIAS_TRABAJADOS / DIAS_MES",
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES" },
		new String[] { "BASE_CGC * 0.10", "BASE_CGP * 0.05",
				//"BASE_IRPF * PORCENTAJE_IRPF/100" 
				},
			null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);

		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		new ContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();

		Map<String, List<ContextData>> datas = AON
				.getSalaries(aonContext,
						props -> props.getContractProperty()
								.eq(contract.getId()))
				.findFirst().get().getContextData();
		;

		for ( String hoursVar : new String[]{						
						MONDAY_HOURS.getName(),
						TUESDAY_HOURS.getName(),
						WEDNESDAY_HOURS.getName(),
						THURSDAY_HOURS.getName(),
						FRIDAY_HOURS.getName(),
						SATURDAY_HOURS.getName()} ) 
		{
			List<ContextData> data = datas.get(hoursVar);
			assertEquals( 1, data.size(),hoursVar);
			assertEquals(startDate,data.get(0).getStartDate(),hoursVar);
			assertEquals(endDate, data.get(0).getEndDate(),hoursVar);
			assertEquals( 1.00, Double.parseDouble(data.get(0).getExpression()),hoursVar);
		}

		for ( String hoursVar : new String[]{						
				SUNDAY_HOURS.getName()} ) 
		{
			List<ContextData> data = datas.get(hoursVar);
			assertEquals( 1, data.size(),hoursVar);
			assertEquals(startDate,data.get(0).getStartDate(),hoursVar);
			assertEquals(endDate, data.get(0).getEndDate(),hoursVar);
			assertEquals( 0.00, Double.parseDouble(data.get(0).getExpression()),hoursVar);
		}

	}
	// ------------------------------------------------------------------------
	protected static <T> T random(T arr[]) {
		return arr[(int) ((int) (Math.random() * arr.length))];
	}

}
