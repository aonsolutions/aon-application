/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.AGREEMENT_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FRIDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ISREAD;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.THURSDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TUESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WEDNESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C100;
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
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.lang.String.format;
import static java.util.Calendar.DAY_OF_MONTH;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

import org.junit.Test;

import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryData;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;

/**
 * @author rtrepiana
 *
 */
public class SQLPartialTimeTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000000001;

	private static ContractCode PARTIAL_TIME[] = { C200, C209, C230, C239, C250,
			C289, C501, C502, C503, C508, C510, C518, C520, C530, C540, C541,
			C550, C552, };


	@Test
	public void testPartialMonthlyI()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfMonth(getToday()),
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(PARTIAL_TIME).getValue()));

						put(MONDAY_HOURS.getName(), format("%d", 4));
						put(TUESDAY_HOURS.getName(), format("%d", 4));
						put(WEDNESDAY_HOURS.getName(), format("%d", 4));
						put(THURSDAY_HOURS.getName(), format("%d", 4));
						put(FRIDAY_HOURS.getName(), format("%d", 4));
						
						put(MONTH_DAYS.getName(), format("%f", 30.00));
					}
				},

				new String[] { 
						"250.00 * DIAS_TRABAJADOS / DIAS_MES",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES" 
				},

				new String[] { 
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" },
				null);

		Date startDate = getFirstDayOfMonth(getToday());
		for ( int i = 0; i < 12 ; i++ ) {
			Date endDate = getLastDayOfMonth(startDate);
			Date issueDate = endDate;
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, issueDate, contract);
			Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
			org.junit.Assert.assertEquals( 1750.00 / 2,  salary.getTotalPayment() , DELTA );
			for ( SalaryData data: salary.getSalaryDatas() ) {
				System.out.println(data.getName() + "= " + data.getExpression() );
			}
			startDate = add(startDate, Calendar.MONTH, 1); 
			
		}

	}

	@Test
	public void testPartialMonthlyII()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfMonth(getToday()),
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								C200.getValue()));

						put(MONDAY_HOURS.getName(), "4");
						put(TUESDAY_HOURS.getName(), "4");
						put(WEDNESDAY_HOURS.getName(), "2");
						
						put(MONTH_DAYS.getName(), "30.00");
					}
				},

				new String[] { 
						"250.00 * DIAS_TRABAJADOS / DIAS_MES",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES" 
				},

				new String[] { 
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" },
				null);

		
		
		Date startDate = contract.getStartDate();
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;
		
		int workedDays = ( 30 - get(startDate, Calendar.DAY_OF_MONTH) )+1;
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		org.junit.Assert.assertEquals( 1750.00 / 4 * workedDays / 30 ,  salary.getTotalPayment() , DELTA );
	}


	@Test
	public void testPartialMonthlyIII()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getToday(),
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(PARTIAL_TIME).getValue()));

						put(MONDAY_HOURS.getName(), format("%d", 4));
						put(TUESDAY_HOURS.getName(), format("%d", 4));
						put(WEDNESDAY_HOURS.getName(), format("%d", 2));
						
						put(MONTH_DAYS.getName(), "30.00");
					}
				},

				new String[] { 
						"250.00 * DIAS_TRABAJADOS / DIAS_MES",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES" 
				},

				new String[] { 
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" },
				null);

		
		
		Date startDate = contract.getStartDate();
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;
		
		int workedDays = Math.min(30, ( get(endDate, Calendar.DAY_OF_MONTH) - get(startDate, Calendar.DAY_OF_MONTH) )+1);
		if ( workedDays ==  get(endDate, Calendar.DAY_OF_MONTH) )
			workedDays = 30;
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		org.junit.Assert.assertEquals( 1750.00 / 4 * workedDays / 30 ,  salary.getTotalPayment() , DELTA );
	}

	@Test
	public void testPartialMonthlyIV()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getToday(),
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(PARTIAL_TIME).getValue()));

						put(ContextVariable.QUOTE_GROUP.getName(), "\"06\"");

						put(MONDAY_HOURS.getName(), format("%d", 4));
						put(TUESDAY_HOURS.getName(), format("%d", 4));
						put(WEDNESDAY_HOURS.getName(), format("%d", 2));
						
						put(MONTH_DAYS.getName(), "[\"06\":30,\"08\": DIAS_NATURALES_MES][GRUPO_COTIZACION]");
					}
				},

				new String[] { 
						"250.00 * DIAS_TRABAJADOS / DIAS_MES",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES" 
				},

				new String[] { 
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" },
				null);

		
		
		Date startDate = contract.getStartDate();
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;
		
		int workedDays = Math.min(30, ( get(endDate, Calendar.DAY_OF_MONTH) - get(startDate, Calendar.DAY_OF_MONTH) )+1);
		if ( workedDays ==  get(endDate, Calendar.DAY_OF_MONTH) )
			workedDays = 30;
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		org.junit.Assert.assertEquals( 1750.00 / 4 * workedDays / 30 ,  salary.getTotalPayment() , DELTA );
		
		
		
	}

	@Test
	public void testPartialMonthlyVI()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()),
				getToday(),
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(PARTIAL_TIME).getValue()));

						put(ContextVariable.QUOTE_GROUP.getName(), "\"06\"");

						put(MONDAY_HOURS.getName(), format("%d", 4));
						put(TUESDAY_HOURS.getName(), format("%d", 4));
						put(WEDNESDAY_HOURS.getName(), format("%d", 2));
						
						put(MONTH_DAYS.getName(), "[\"06\":30,\"08\": DIAS_NATURALES_MES][GRUPO_COTIZACION]");
					}
				},

				new String[] { 
						"250.00 * DIAS_TRABAJADOS / DIAS_MES",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES" 
				},

				new String[] { 
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" },
				null);

		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = contract.getEndDate();
		Date issueDate = endDate;
		
		int endDateOfMonth = get(endDate, Calendar.DAY_OF_MONTH );
		int lastdayOfMonth = AonDateUtils.getMax(getToday(), Calendar.DAY_OF_MONTH);
		int workedDays = endDateOfMonth == lastdayOfMonth ? 30 : Math.min(30, endDateOfMonth );

		//int workedDays = Math.min(30,get(endDate, Calendar.DAY_OF_MONTH));
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		org.junit.Assert.assertEquals( 1750.00 / 4 * workedDays / 30 ,  salary.getTotalPayment() , DELTA );
		
		
		
	}

	@Test	
	public void testPartialMonthlyVII()
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

						put(ContextVariable.QUOTE_GROUP.getName(), "\"06\"");

						put(MONDAY_HOURS.getName(), format("%d", 4));
						put(TUESDAY_HOURS.getName(), format("%d", 4));
						put(WEDNESDAY_HOURS.getName(), format("%d", 2));
						
						put(MONTH_DAYS.getName(), "[\"06\":30,\"08\": DIAS_NATURALES_MES][GRUPO_COTIZACION]");
					}
				},

				new String[] { 
						"250.00 * DIAS_TRABAJADOS / DIAS_MES",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES" 
				},

				new String[] { 
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" },
				null);

		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;
		
		Date startItDate = add(startDate, Calendar.DAY_OF_MONTH, 10);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startItDate, null, null);

		int workedDays = 10;
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		org.junit.Assert.assertEquals( 1750.00 / 4 * workedDays / 30 ,  salary.getTotalPayment() , DELTA );
		
		for ( SalaryData data : salary.getSalaryDatas())
			System.out.println(data.getName() + " = " + data.getExpression());
		
		
		System.out.println(salary.getSalaryData(ContextVariable.WORKED_DAYS.getName(), Number.class));
		
		
	}

	@Test
	public void testPartialMonthlyVIII()
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

						put(ContextVariable.QUOTE_GROUP.getName(), "\"06\"");

						put(MONDAY_HOURS.getName(), format("%d", 4));
						put(TUESDAY_HOURS.getName(), format("%d", 4));
						put(WEDNESDAY_HOURS.getName(), format("%d", 2));
						
						put(MONTH_DAYS.getName(), "[\"06\":30,\"08\": DIAS_NATURALES_MES][GRUPO_COTIZACION]");
					}
				},

				new String[] { 
						"250.00 * DIAS_TRABAJADOS / DIAS_MES",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES" 
				},

				new String[] { 
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" },
				null);

		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;
		
		Date startItDate = add(startDate, DAY_OF_MONTH, 10);
		Date endItDate = add(startItDate, DAY_OF_MONTH, 10);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startItDate, endItDate, null);

		int workedDays = 10 + ( get(endDate, DAY_OF_MONTH) - get(endItDate, DAY_OF_MONTH)) ;
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		org.junit.Assert.assertEquals( 1750.00 / 4 * workedDays / 30 ,  salary.getTotalPayment() , DELTA );
		
		
		
	}

	@Test
	public void testPartialMonthlyX()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfMonth(getToday()),
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(PARTIAL_TIME).getValue()));

						put(MONDAY_HOURS.getName(), format("%d", 4));
						put(TUESDAY_HOURS.getName(), format("%d", 4));
						put(WEDNESDAY_HOURS.getName(), format("%d", 4));
						put(THURSDAY_HOURS.getName(), format("%d", 4));
						put(FRIDAY_HOURS.getName(), format("%d", 4));
						
						put(ContextVariable.CGC_BASE_MIN.getName(), format("%s('%s') ? 66.66 * %s : 0.00", ContextVariable.ISREAD, ContextVariable.WORKED_HOURS, ContextVariable.WORKED_HOURS ));
						
					}
				},

				new String[] { 
						"250.00 * DIAS_TRABAJADOS / DIAS_MES",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES" 
				},

				new String[] { 
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" },
				null);

		Date startDate = getFirstDayOfMonth(getToday());
		for ( int i = 0; i < 12 ; i++ ) {
			Date endDate = getLastDayOfMonth(startDate);
			Date issueDate = endDate;
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, issueDate, contract);
			Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
			
			org.junit.Assert.assertEquals( 1750.00 / 2,  salary.getCommonBase() , DELTA );

			startDate = add(startDate, Calendar.MONTH, 1); 
			
		}

	}

	@Test
	public void testPartialMonthlyXI()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfMonth(getToday()),
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(PARTIAL_TIME).getValue()));

						put(MONDAY_HOURS.getName(), format("%d", 4));
						put(TUESDAY_HOURS.getName(), format("%d", 4));
						put(WEDNESDAY_HOURS.getName(), format("%d", 4));
						put(THURSDAY_HOURS.getName(), format("%d", 4));
						put(FRIDAY_HOURS.getName(), format("%d", 4));
						
						put(ContextVariable.CGC_BASE_MIN.getName(), format("%s('%s') ? 66.66 * %s : 0.00", ISREAD, WORKED_HOURS, WORKED_HOURS ));
						
					}
				},

				new String[] { 
						"250.00 * DIAS_TRABAJADOS / DIAS_MES",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"0.10 * HORAS_TRABAJADAS" 
				},

				new String[] { 
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" },
				null);

		Date startDate = getFirstDayOfMonth(getToday());
		for ( int i = 0; i < 12 ; i++ ) {
			Date endDate = getLastDayOfMonth(startDate);
			Date issueDate = endDate;
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, issueDate, contract);
			Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
			double salaryHours = salary.getSalaryData(WORKED_HOURS.getName(), Number.class ).doubleValue();
			org.junit.Assert.assertEquals( 66.66 * salaryHours,  salary.getCommonBase() , DELTA );

			startDate = add(startDate, Calendar.MONTH, 1); 
			
		}

	}

	@Test
	public void testPartialMonthlyXII()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		addSystemData(aonContext, getFirstDayOfMonth(getToday()), null, 				
		new HashMap<String, String>() {
			{
				put(ContextVariable.IS_MONTHLY_DAILY, format("def() { !%s('%s') } ", ContextVariable.ISREAD, ContextVariable.WORKED_HOURS));
				
			}
		});
		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfMonth(getToday()),
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(PARTIAL_TIME).getValue()));

						put(MONDAY_HOURS.getName(), format("%d", 4));
						put(TUESDAY_HOURS.getName(), format("%d", 4));
						put(WEDNESDAY_HOURS.getName(), format("%d", 4));
						put(THURSDAY_HOURS.getName(), format("%d", 4));
						put(FRIDAY_HOURS.getName(), format("%d", 4));
						
						put(ContextVariable.CGC_BASE_MIN.getName(), format(" %s() ? 0.00 : 66.66 * %s ", ContextVariable.IS_MONTHLY_DAILY, ContextVariable.WORKED_HOURS ));
						
					}
				},

				new String[] { 
						"250.00 * DIAS_TRABAJADOS / DIAS_MES",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES" 
				},

				new String[] { 
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" },
				null);
		
		Date startDate = getFirstDayOfMonth(getToday());
		for ( int i = 0; i < 12 ; i++ ) {
			Date endDate = getLastDayOfMonth(startDate);
			Date issueDate = endDate;
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, issueDate, contract);
			Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
			
			org.junit.Assert.assertEquals( 1750.00 / 2,  salary.getCommonBase() , DELTA );

			startDate = add(startDate, Calendar.MONTH, 1); 
			
		}
		
		cleanSystemData(aonContext);

	}

	@Test
	public void testPartialMonthlyXIII()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		addSystemData(aonContext, getFirstDayOfMonth(getToday()), null, 				
		new HashMap<String, String>() {
			{
				put(ContextVariable.IS_MONTHLY_DAILY, format("def() { !%s('%s') } ", ContextVariable.ISREAD, ContextVariable.WORKED_HOURS));
				
			}
		});
		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfMonth(getToday()),
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(PARTIAL_TIME).getValue()));

						put(MONDAY_HOURS.getName(), format("%d", 4));
						put(TUESDAY_HOURS.getName(), format("%d", 4));
						put(WEDNESDAY_HOURS.getName(), format("%d", 4));
						put(THURSDAY_HOURS.getName(), format("%d", 4));
						put(FRIDAY_HOURS.getName(), format("%d", 4));
						
						put(ContextVariable.CGC_BASE_MIN.getName(), format(" %s() ? 0.00 : 66.66 * %s", ContextVariable.IS_MONTHLY_DAILY, ContextVariable.WORKED_HOURS ));
						
					}
				},

				new String[] { 
						"0.10 * HORAS_TRABAJADAS", 
						"250.00 * DIAS_TRABAJADOS / DIAS_MES",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES" 
				},

				new String[] { 
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" },
				null);
		
		Date startDate = getFirstDayOfMonth(getToday());
		for ( int i = 0; i < 12 ; i++ ) {
			Date endDate = getLastDayOfMonth(startDate);
			Date issueDate = endDate;
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, issueDate, contract);
			Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
			double salaryHours = salary.getSalaryData(WORKED_HOURS.getName(), Number.class ).doubleValue();
			org.junit.Assert.assertEquals( 66.66 * salaryHours,  salary.getCommonBase() , DELTA );
			startDate = add(startDate, Calendar.MONTH, 1); 
			
		}
		
		cleanSystemData(aonContext);

	}

	@Test
	public void testPartialMonthlyXIV()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfMonth(getToday()),
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",C100.getValue()));

						put(MONDAY_HOURS.getName(), format("%d", 4));
						put(TUESDAY_HOURS.getName(), format("%d", 4));
						put(WEDNESDAY_HOURS.getName(), format("%d", 4));
						put(THURSDAY_HOURS.getName(), format("%d", 4));
						put(FRIDAY_HOURS.getName(), format("%d", 4));
						
						put(MONTH_DAYS.getName(), format("%f", 30.00));
					}
				},

				new String[] { 
						"250.00 * DIAS_TRABAJADOS / DIAS_MES",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES" 
				},

				new String[] { 
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" },
				null);

		Date startDate = getFirstDayOfMonth(getToday());
		for ( int i = 0; i < 12 ; i++ ) {
			Date endDate = getLastDayOfMonth(startDate);
			Date issueDate = endDate;
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, issueDate, contract);
			Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
			org.junit.Assert.assertEquals( 1750.00 / 2,  salary.getTotalPayment() , DELTA );
			for ( SalaryData data: salary.getSalaryDatas() ) {
				System.out.println(data.getName() + "= " + data.getExpression() );
			}
			startDate = add(startDate, Calendar.MONTH, 1); 
			
		}

	}

	@Test
	public void testPartialMonthlyXV()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfMonth(getToday()),
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",C100.getValue()));
						put(ContextVariable.PARTIAL_FACTOR.getName(), "0.5");
						put(MONTH_DAYS.getName(), "30.00");
					}
				},

				new String[] { 
						"250.00 * DIAS_TRABAJADOS / DIAS_MES",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES" 
				},

				new String[] { 
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" },
				null);

		Date startDate = getFirstDayOfMonth(getToday());
		for ( int i = 0; i < 12 ; i++ ) {
			Date endDate = getLastDayOfMonth(startDate);
			Date issueDate = endDate;
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, issueDate, contract);
			Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
			for ( SalaryData data: salary.getSalaryDatas() ) {
				System.out.println(data.getName() + "= " + data.getExpression() );
			}
			org.junit.Assert.assertEquals( 1750.00 / 2,  salary.getTotalPayment() , DELTA );
			startDate = add(startDate, Calendar.MONTH, 1); 
			
		}

	}

	@Test
	public void testPartialMonthlyXVI()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		AgreementRecord agreement = newAgreement(aonContext);
		
		AgreementLevelCategoryRecord category = newAgreementCategory(aonContext, agreement);
		
		addData(aonContext, agreement, getFirstDayOfMonth(getToday()),
				Collections.singletonMap(AGREEMENT_HOURS.getName(), format("%f", 35.00)));

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfMonth(getToday()),
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(PARTIAL_TIME).getValue()));

						put(MONDAY_HOURS.getName(), format("%d", 4));
						put(TUESDAY_HOURS.getName(), format("%d", 4));
						put(WEDNESDAY_HOURS.getName(), format("%d", 4));
						put(THURSDAY_HOURS.getName(), format("%d", 4));
						put(FRIDAY_HOURS.getName(), format("%d", 4));
						
						put(MONTH_DAYS.getName(), format("%f", 30.00));
						
						
					}
				},

				new String[] { 
						"250.00 * DIAS_TRABAJADOS / DIAS_MES",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES" 
				},

				new String[] { 
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
//						"BASE_IRPF * PORCENTAJE_IRPF/100" 
						},
				category);

		Date startDate = getFirstDayOfMonth(getToday());
		for ( int i = 0; i < 12 ; i++ ) {
			Date endDate = getLastDayOfMonth(startDate);
			Date issueDate = endDate;
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, issueDate, contract);
			Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
			org.junit.Assert.assertEquals( 1750.00  /( 35.00 / 20.00),  salary.getTotalPayment() , DELTA );
			for ( SalaryData data: salary.getSalaryDatas() ) {
				System.out.println(data.getName() + "= " + data.getExpression() );
			}
			startDate = add(startDate, Calendar.MONTH, 1); 
			
		}

	}

	@Test
	public void testAdjustPartialMonthlyI()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",C200.getValue()));
						put(MONTH_DAYS.getName(), "30.00");
					}
				},

				new String[] { 
						"250.00 * DIAS_TRABAJADOS / DIAS_MES",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES" 
				},

				new String[] { 
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" },
				null);

		Date startDate = getFirstDayOfYear(getToday());
		for ( int i = 0; i < 12 ; i++ ) {
			Date endDate = getLastDayOfMonth(startDate);
			Date issueDate = endDate;
			
			addData(aonContext, contract, startDate, add(startDate, DAY_OF_MONTH , 14 ), ContextVariable.PARTIAL_FACTOR, 0.25);
			addData(aonContext, contract, add(startDate, DAY_OF_MONTH , 15 ), endDate, ContextVariable.PARTIAL_FACTOR, 0.75);

			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, issueDate, contract);
			Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
//			for ( SalaryPayment payment: salary.getSalaryPayments() ) {
//				System.out.println(payment.getName() + " = " + payment.getAmount()  + ", " + payment.getQuote() );
//			}
//			for ( SalaryData data: salary.getSalaryDatas() ) {
//				System.out.println(data.getName() + " = " + data.getExpression() );
//			}
			org.junit.Assert.assertEquals( 1750.00 / 2,  salary.getTotalPayment() , DELTA );
			org.junit.Assert.assertEquals( 1750.00 / 2,  salary.getCommonBase() , DELTA );
			startDate = add(startDate, Calendar.MONTH, 1); 
			
		}

	}

	@Test
	public void testNotAdjustPartialMonthlyII()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",C200.getValue()));
						put(MONTH_DAYS.getName(), "30.00");
					}
				},

				new String[] { 
						"250.00 * DIAS_TRABAJADOS / DIAS_MES",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES" 
				},

				new String[] { 
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" },
				null);

		Date startDate = getFirstDayOfYear(getToday());
		for ( int i = 0; i < 12 ; i++ ) {
			Date endDate = getLastDayOfMonth(startDate);
			Date issueDate = endDate;

			addData(aonContext, contract, startDate, endDate, ContextVariable.PARTIAL_FACTOR, 0.50);

			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, issueDate, contract);
			Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);

			org.junit.Assert.assertEquals( 1750.00 / 2,  salary.getTotalPayment() , DELTA );
			org.junit.Assert.assertEquals( 1750.00 / 2,  salary.getCommonBase() , DELTA );
			startDate = add(startDate, Calendar.MONTH, 1); 
			
		}

		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 1.00 * %s_1_3",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 1.00 * %s_4_15",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 1.00 * %s_16_20",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);

		startDate = getFirstDayOfYear(getToday());
		for ( int i = 0; i < 12 ; i++ ) {
			Date endDate = getLastDayOfMonth(startDate);
			Date issueDate = endDate;
			
//			addData(aonContext, contract, startDate, add(startDate, DAY_OF_MONTH , 14 ), ContextVariable.PARTIAL_FACTOR, 0.25);
//			addData(aonContext, contract, add(startDate, DAY_OF_MONTH , 15 ), endDate, ContextVariable.PARTIAL_FACTOR, 0.75);
			addIT(aonContext, contract, LeaveType.COMMON_DISEASE, add(startDate, DAY_OF_MONTH , 15 ), endDate, null);
			
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, issueDate, contract);
			Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
			for ( SalaryPayment payment: salary.getSalaryPayments() ) {
				System.out.println(payment.getName() + " = " + payment.getAmount()  + ", " + payment.getQuote() );
			}
			for ( SalaryData data: salary.getSalaryDatas() ) {
				System.out.println(data.getName() + " = " + data.getExpression() );
			}
			int monthDays = get(endDate, DAY_OF_MONTH);
			int itDays = monthDays - 15;
			org.junit.Assert.assertEquals( 1750.00 / 2 / 30 * 15 +  1750.00 / 2 / monthDays * itDays ,  salary.getTotalPayment() , DELTA );
			org.junit.Assert.assertEquals( 1750.00 / 2 / 30 * 15 + 1750.00 / 2 / monthDays * itDays ,  salary.getCommonBase() , DELTA );
			startDate = add(startDate, Calendar.MONTH, 1); 
			
		}
	}

	
	@Test
	public void testPartialWeekI()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				add(getFirstDayOfYear(getToday()), Calendar.MONTH, -6),
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								random(PARTIAL_TIME).getValue()));
//						put(MONTH_DAYS.getName(), format("%f", 30.00));
					}
				},

				new String[] { 
//						"250.00 * DIAS_TRABAJADOS / DIAS_MES",
						"1750.00 * DIAS_TRABAJADOS / DIAS_MES" ,
						"TRACE('COEFICIENTE : %f \r\n', COEFICIENTE_PARCIALIDAD);0.00"
				},

				new String[] { 
//						"BASE_CGC * 0.10", 
//						"BASE_CGP * 0.05",
//						"BASE_IRPF * PORCENTAJE_IRPF/100" 
						},
				null);


	
		Date startDate = add(getFirstDayOfMonth(getToday()), Calendar.MONTH, 1);
		Date endDate = getLastDayOfMonth(startDate);
		
		Date _15HoursEndDate = add(startDate, Calendar.DAY_OF_MONTH, 9);
		addData(aonContext, contract, contract.getStartDate(), _15HoursEndDate, new HashMap<String, String>() {
					{
						put(MONDAY_HOURS.getName(), format("%d", 3));
						put(TUESDAY_HOURS.getName(), format("%d", 3));
						put(WEDNESDAY_HOURS.getName(), format("%d", 3));
						put(THURSDAY_HOURS.getName(), format("%d", 3));
						put(FRIDAY_HOURS.getName(), format("%d", 3));
					}
				});
		
		Date _20HoursStartDate = add(startDate, Calendar.DAY_OF_MONTH, 10);
		addData(aonContext, contract, _20HoursStartDate, null, new HashMap<String, String>() {
			{
				put(MONDAY_HOURS.getName(), format("%d", 4));
				put(TUESDAY_HOURS.getName(), format("%d", 4));
				put(WEDNESDAY_HOURS.getName(), format("%d", 4));
				put(THURSDAY_HOURS.getName(), format("%d", 4));
				put(FRIDAY_HOURS.getName(), format("%d", 4));
			}
		});
 
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
		connection, startDate, endDate, endDate, contract);
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment payment: salary.getSalaryPayments() ) {
			System.out.println(payment.getName() + " = " + payment.getAmount()  + ", " + payment.getQuote() );
		}
		
		int monthDays = get(endDate, Calendar.DAY_OF_MONTH );
		
		org.junit.Assert.assertEquals( 
		1750.00 / monthDays * 10  * ( 15.00 / 40.00) +
		1750.00 / monthDays * (monthDays-10)  * ( 20.00 / 40.00) 
		,  salary.getTotalPayment() , DELTA );

	}

	// ------------------------------------------------------------------------

	protected static <T> T random(T arr[]) {
		return arr[(int) ((int) (Math.random() * arr.length))];
	}

}
