package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.BR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FRIDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PARTIAL_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SALARY_START;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SATURDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SUNDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.THURSDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TOTAL_LIQUID;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TOTAL_PAYMENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TUESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WEDNESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C300;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getMax;
import static java.lang.String.format;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.payment.IPayment;

import static org.junit.jupiter.api.Assertions.*;

public class SQLBRTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.001;
	
	
	
	@Test
	public void testBRI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] {});

		ContractRecord contract = newContract(aonContext, getToday(),
				new HashMap<String, String>() {
					{
						put(MONTH_DAYS.getName(), format("%d", 30));
					}
				}, new String[] { "0.10 * P_1 * 1.00",
						"3000.00 * DIAS_TRABAJADOS / DIAS_MES" },
				new String[] {}, category);
		//@formatter:off

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		double br = (3000.00 * 1.10)/ 30;

		// Same day of job start. Without salaries.
		ctx.getExpressionContext().setVariable("TODAY", getToday(), startDate, endDate);
		assertEquals(br, ctx.getExpressionContext().eval("BR(TODAY)", startDate, endDate, Double.class).get(0).getValue(), DELTA);
		
	}

	@Test
	public void testBRII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });

		ContractRecord contract = newContract(aonContext, getToday(),
				new HashMap<String, String>() {
					{
						put(MONTH_DAYS.getName(), format("%d", 30));
					}
				}, new String[] { "( P_1 + P_2 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" }, 
						new String[] {
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" }, category);
		//@formatter:off
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		double br = (1750.00 * 1.10) * (1 + 1.00 / 12 + 1.00 / 12) / 30;

		// Same day of job start. Without salaries.
		ctx.getExpressionContext().setVariable("TODAY", getToday(), startDate, endDate);
		assertEquals(br, ctx.getExpressionContext().eval("BR(TODAY)", startDate, endDate, Double.class).get(0).getValue(), DELTA);
		
		ctx.getExpressionContext()
		.eval(String.format("%s(%s)", BR,SALARY_START.getName() ), startDate, endDate)
		.stream().forEach(result->assertEquals(br, ((Number) result.getValue()).doubleValue(), DELTA) );


		// Next month of job start. But without salaries.
		ctx.getExpressionContext().setVariable("NEXT_MONTH", addMonths(getToday(), 1), startDate, endDate);
		assertEquals(br, ctx.getExpressionContext().eval("BR(NEXT_MONTH)", startDate, endDate, Double.class).get(0).getValue(), DELTA);

		
		
		// Save a salry for first month
		JooqSalaryBuilder salaryBuilder = new JooqSalaryBuilder(connection);
		new SmartContractSalaryCalculator<ISalary>(salaryBuilder).calculate(ctx);
		salaryBuilder.execute();

		addPayment(aonContext, contract, "100", "100");
		// Next month of job start. But now with salaries.
		assertEquals(br, ctx.getExpressionContext().eval("BR(NEXT_MONTH)", startDate, endDate, Double.class).get(0).getValue(), 0.005);

		// Next two month of job start. Without salaries.
		ctx.getExpressionContext().setVariable("NEXT_MONTH2", addMonths(getToday(), 2), startDate, endDate);
		assertEquals(br + (100d / 30d), ctx.getExpressionContext().eval("BR(NEXT_MONTH2)", startDate, endDate, Double.class).get(0).getValue(), 0.005);

	}
	

	@Test
	public void testCommonDiseaseITI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
				{
					put("DIAS_MES", "30"); // Monthly quote
				}
				},
				new String[] {
				"( P_1 + P_2 ) * 0.10",
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"TRACE('DIAS_TRABAJADOS=%f\r\n',DIAS_TRABAJADOS);0.00"}, 
				new String[] {
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				"BASE_IRPF * 0.00/100" }, category);
		addPayment(aonContext, contract, addConcept(aonContext, "PREST_IT"),
				"TRACE('BASE_REGULADORA=%f\r\n',BASE_REGULADORA); "
				//+"TRACE('DIAS_ENFERMEDAD_COMUN = %d \r\n',DIAS_ENFERMEDAD_COMUN);"
				+"TRACE('%1$td/%1$tm/%1$tY \r\n',INICIO_IT);"
				+"0.00", 
				//"DIAS_ENFERMEDAD_COMUN * BASE_REGULADORA"
				"DIAS_COTIZADOS * BASE_REGULADORA"
				);
		//@formatter:on

		Date startITDate = getToday();
		Date endITDate = startITDate;
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				endITDate, null);
		System.out.println("IT [" + startITDate + "...]");

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		ISalary salary = calculator.calculate(ctx);

		int monthDays = get(endDate, DAY_OF_MONTH);
		assertEquals(1750.00 * 1.10
				* (monthDays - 1) / 30, salary.getTotalPayment(), DELTA, format("%s :", TOTAL_PAYMENT));
		assertEquals((1750.00 * 1.10)
				* (1 + 1.00 / 12 + 1.00 / 12), salary.getCommonBase(), DELTA, format("%s :", CGC_BASE));

		startDate = getFirstDayOfMonth(add(getToday(), MONTH, 1));
		endDate = getLastDayOfMonth(startDate);

		monthDays = 30; //get(endDate, DAY_OF_MONTH);
		int leaveOffset = (int) (Math.random() * get(endDate, DAY_OF_MONTH));
		startITDate = add(endDate, Calendar.DAY_OF_MONTH, (-1) * leaveOffset);
		endITDate = null;
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				endITDate, null);
		System.out.println("IT [" + startITDate + "...]");
		ctx = getContractSalaryCalculatorContext(connection, startDate,
				endDate, endDate, contract);

		salary = calculator.calculate(ctx);
		int workDays = get(endDate, DAY_OF_MONTH) - (leaveOffset + 1);
		assertEquals(1750.00 * 1.10
				* (workDays) / monthDays, salary.getTotalPayment(), DELTA, format("%s :", TOTAL_PAYMENT));
		assertEquals((1750.00 * 1.10)
				* (1 + 1.00 / 12 + 1.00 / 12), salary.getCommonBase(), DELTA, format("%s :", CGC_BASE));
		System.out.println("CGC_BASE [" + startDate + "..." + endDate + "] :"
				+ salary.getCommonBase());

		startDate = getFirstDayOfMonth(add(getToday(), MONTH, 2));
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(connection, startDate,
				endDate, endDate, contract);

		salary = calculator.calculate(ctx);
		assertEquals(0.00, salary.getTotalPayment(), DELTA, format("%s :", TOTAL_PAYMENT));
		assertEquals((1750.00 * 1.10)
				* (1 + 1.00 / 12 + 1.00 / 12), salary.getCommonBase(), DELTA, format("%s :", CGC_BASE));
		System.out.println("CGC_BASE [" + startDate + "..." + endDate + "] :"
				+ salary.getCommonBase());

		startDate = getFirstDayOfMonth(add(getToday(), MONTH, 3));
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(connection, startDate,
				endDate, endDate, contract);

		salary = calculator.calculate(ctx);
		assertEquals(0.00, salary.getTotalPayment(), DELTA, format("%s :", TOTAL_PAYMENT));
		assertEquals((1750.00 * 1.10)
				* (1 + 1.00 / 12 + 1.00 / 12), salary.getCommonBase(), DELTA, format("%s :", CGC_BASE));
		System.out.println("CGC_BASE [" + startDate + "..." + endDate + "] :"
				+ salary.getCommonBase());

		startDate = getFirstDayOfMonth(add(getToday(), MONTH, 4));
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(connection, startDate,
				endDate, endDate, contract);

		salary = calculator.calculate(ctx);
		assertEquals(0.00, salary.getTotalPayment(), DELTA, format("%s :", TOTAL_PAYMENT));
		assertEquals((1750.00 * 1.10)
				* (1 + 1.00 / 12 + 1.00 / 12), salary.getCommonBase(), DELTA, format("%s :", CGC_BASE));
		System.out.println("CGC_BASE [" + startDate + "..." + endDate + "] :"
				+ salary.getCommonBase());
	}

	@Test
	public void testCommonDiseaseITWithExtraHoursI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
				{
					put("DIAS_MES", "30"); // Monthly quote
				}
				},
				new String[] {
				"( P_1 + P_2 ) * 0.10",
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"TRACE('DIAS_TRABAJADOS=%f\r\n',DIAS_TRABAJADOS);0.00"}, 
				new String[] {
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				"BASE_IRPF * 0.00/100" }, category);
		addPayment(aonContext, contract,addConcept(aonContext, "PREST_IT"),
				"TRACE('BASE_REGULADORA=%f\r\n',BASE_REGULADORA); "
				//+"TRACE('DIAS_ENFERMEDAD_COMUN = %d \r\n',DIAS_ENFERMEDAD_COMUN);"
				+"TRACE('%1$td/%1$tm/%1$tY \r\n',INICIO_IT);"
				+"0.00", 
				//"DIAS_ENFERMEDAD_COMUN * BASE_REGULADORA"
				"DIAS_COTIZADOS * BASE_REGULADORA"
				);
		;
		addPayment(aonContext, 
				contract, 
				addConcept(aonContext, "HORAS_EXTRAORDINARIAS_FZA"), 
				"HORAS EXTRAORDINARIAS FUERZA MAYOR ", 
				"66.66", 
				"_P", 
				"_P", 
				PaymentType.CRA_0003);
		//@formatter:on

		Date startITDate = getToday();
		Date endITDate = startITDate;
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				endITDate, null);
		System.out.println("IT [" + startITDate + "...]");

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		ISalary salary = calculator.calculate(ctx);

		int monthDays = get(endDate, DAY_OF_MONTH);
		assertEquals(1750.00 * 1.10
				* (monthDays - 1) / 30 + 66.66, salary.getTotalPayment(), DELTA, format("%s :", TOTAL_PAYMENT));
		assertEquals((1750.00 * 1.10)
				* (1 + 1.00 / 12 + 1.00 / 12), salary.getCommonBase(), DELTA, format("%s :", CGC_BASE));

		startDate = getFirstDayOfMonth(add(getToday(), MONTH, 1));
		endDate = getLastDayOfMonth(startDate);

		monthDays = 30; //get(endDate, DAY_OF_MONTH);
		int leaveOffset = (int) 13; //(Math.random() * 20);
		startITDate = add(endDate, Calendar.DAY_OF_MONTH, (-1) * leaveOffset);
		endITDate = null;
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				endITDate, null);
		System.out.println("IT [" + startITDate + "...]");
		ctx = getContractSalaryCalculatorContext(connection, startDate,
				endDate, endDate, contract);

		salary = calculator.calculate(ctx);
		int workDays = get(endDate, DAY_OF_MONTH) - (leaveOffset + 1);
		assertEquals(1750.00 * 1.10
				* (workDays) / monthDays + 66.66, salary.getTotalPayment(), DELTA, format("%s :", TOTAL_PAYMENT));
		assertEquals((1750.00 * 1.10)
				* (1 + 1.00 / 12 + 1.00 / 12), salary.getCommonBase(), DELTA, format("%s :", CGC_BASE));
		System.out.println("CGC_BASE [" + startDate + "..." + endDate + "] :"
				+ salary.getCommonBase());

		startDate = getFirstDayOfMonth(add(getToday(), MONTH, 2));
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(connection, startDate,
				endDate, endDate, contract);

		salary = calculator.calculate(ctx);
		assertEquals(0.00, salary.getTotalPayment(), DELTA, format("%s :", TOTAL_PAYMENT));
		assertEquals((1750.00 * 1.10)
				* (1 + 1.00 / 12 + 1.00 / 12), salary.getCommonBase(), DELTA, format("%s :", CGC_BASE));
		System.out.println("CGC_BASE [" + startDate + "..." + endDate + "] :"
				+ salary.getCommonBase());

		startDate = getFirstDayOfMonth(add(getToday(), MONTH, 3));
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(connection, startDate,
				endDate, endDate, contract);

		salary = calculator.calculate(ctx);
		assertEquals(0.00, salary.getTotalPayment(), DELTA, format("%s :", TOTAL_PAYMENT));
		assertEquals((1750.00 * 1.10)
				* (1 + 1.00 / 12 + 1.00 / 12), salary.getCommonBase(), DELTA, format("%s :", CGC_BASE));
		System.out.println("CGC_BASE [" + startDate + "..." + endDate + "] :"
				+ salary.getCommonBase());

		startDate = getFirstDayOfMonth(add(getToday(), MONTH, 4));
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(connection, startDate,
				endDate, endDate, contract);

		salary = calculator.calculate(ctx);
		assertEquals(0.00, salary.getTotalPayment(), DELTA, format("%s :", TOTAL_PAYMENT));
		assertEquals((1750.00 * 1.10)
				* (1 + 1.00 / 12 + 1.00 / 12), salary.getCommonBase(), DELTA, format("%s :", CGC_BASE));
		System.out.println("CGC_BASE [" + startDate + "..." + endDate + "] :"
				+ salary.getCommonBase());
	}


	@Test
	public void testCommonDiseaseITWithPayment() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.DECEMBER;
						this.start = "01/12";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				format("BRUTO(2000 * %s / %s)", ContextVariable.WORKED_DAYS , ContextVariable.MONTH_DAYS )}, 
				new String[] {
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				"BASE_IRPF * 0.00/100" }, category);
		addPayment(aonContext, contract,addConcept(aonContext, "PREST_IT"),
				"0.00 ", 
				"DIAS_ENFERMEDAD_COMUN * BASE_REGULADORA");
		//@formatter:on

		Date startITDate = getToday();
		int itDays = (int) (Math.random() * (getMax(startITDate, DAY_OF_MONTH) - get(
				startITDate, DAY_OF_MONTH))) + 1;
		Date endITDate = addDays(startITDate, itDays - 1);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				endITDate, null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		ISalary salary = calculator.calculate(ctx);

		int monthDays = get(endDate, DAY_OF_MONTH);
		assertEquals(2000.00
				* (monthDays - itDays) / monthDays, salary.getTotalPayment(), DELTA, format("%s :", TOTAL_PAYMENT));
		assertEquals(2000.00, salary.getCommonBase(), DELTA, format("%s :", CGC_BASE));

	}

	@Test
	public void testCommonDiseaseITWithLiquid() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.DECEMBER;
						this.start = "01/12";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				format("NETO(2000 * %s / %s)", ContextVariable.WORKED_DAYS , ContextVariable.MONTH_DAYS )}, 
				new String[] {
				"BASE_CGC * 0.0", 
				"BASE_CGP * 0.00",
				"BASE_IRPF * 0.00/100" }, category);
		addPayment(aonContext, contract,addConcept(aonContext, "PREST_IT"),
				"0.00 ", 
				"DIAS_ENFERMEDAD_COMUN * BASE_REGULADORA");
		//@formatter:on

		Date startITDate = getToday();
		int itDays = (int) (Math.random() * (getMax(startITDate, DAY_OF_MONTH) - get(
				startITDate, DAY_OF_MONTH))) + 1;
		Date endITDate = addDays(startITDate, itDays - 1);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				endITDate, null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		ISalary salary = calculator.calculate(ctx);

		int monthDays = get(endDate, DAY_OF_MONTH);
		assertEquals(2000.00
				* (monthDays - itDays) / monthDays, salary.getTotalLiquid(), DELTA, format("%s :", TOTAL_LIQUID));
		assertEquals(2000.00, salary.getCommonBase(), DELTA, format("%s :", CGC_BASE));

	}

	@Test
	public void testCommonDiseaseITWithLiquidI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				format("NETO(2000 * %s / %s)", ContextVariable.WORKED_DAYS , ContextVariable.MONTH_DAYS )}, 
				new String[] {
				"BASE_CGC * 0.0", 
				"BASE_CGP * 0.00",
				"BASE_IRPF * 0.00/100" }
				, null);
		addPayment(aonContext, contract,addConcept(aonContext, "PREST_IT"),
				"0.00 ", 
				"DIAS_ENFERMEDAD_COMUN * BASE_REGULADORA");
		//@formatter:on

		
		Date startITDate = set(set(getToday(), Calendar.DATE, 9), Calendar.MONTH, Calendar.APRIL);
		int itDays = 18;
		Date endITDate = addDays(startITDate, itDays - 1);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				endITDate, null);

		Date startDate = getFirstDayOfMonth(startITDate);
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		Salary salary = calculator.calculate(ctx);
		
		int monthDays = get(endDate, DAY_OF_MONTH);
		assertEquals(2000.00
				* (monthDays - itDays) / monthDays, salary.getTotalLiquid(), DELTA, format("%s :", TOTAL_LIQUID));
		assertEquals(2000.00, salary.getCommonBase(), DELTA, format("%s :", CGC_BASE));

	}

	@Test
	public void testCommonDiseaseITWithLiquidII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				format("NETO(2000 * %s / %s)", ContextVariable.WORKED_DAYS , ContextVariable.MONTH_DAYS )}, 
				new String[] {
				"BASE_CGC * 0.0", 
				"BASE_CGP * 0.00",
				"BASE_IRPF * 0.00/100" }
				, null);
		addPayment(aonContext, contract,addConcept(aonContext, "PREST_IT"),
				"0.00 ", 
				"DIAS_ENFERMEDAD_COMUN * BASE_REGULADORA");
		//@formatter:on

		
		Date startITDate = set(set(getToday(), Calendar.DATE, 9), Calendar.MONTH, Calendar.APRIL);
		int itDays = 6;
		Date endITDate = addDays(startITDate, itDays - 1);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				endITDate, null);

		Date startDate = getFirstDayOfMonth(startITDate);
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		Salary salary = calculator.calculate(ctx);
		
		int monthDays = get(endDate, DAY_OF_MONTH);
		assertEquals(2000.00, salary.getCommonBase(), DELTA, format("%s :", CGC_BASE));
		assertEquals(2000.00
				* (monthDays - itDays) / monthDays, salary.getTotalLiquid(), DELTA, format("%s :", TOTAL_LIQUID));

	}

	@Test
	public void testCommonDiseaseITWithPaymentAndIRPF()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		//@formatter:on

		Date startITDate = getToday();
		int maxItDays = (int) ((getMax(startITDate, DAY_OF_MONTH) - get(startITDate, DAY_OF_MONTH))) + 1;
		for ( int itDays = 9; itDays <= maxItDays; itDays++) {

			ContractRecord contract = newContract(aonContext, 
					new String[] {
					format("BRUTO(2000 * %s / %s)", ContextVariable.WORKED_DAYS , ContextVariable.MONTH_DAYS )}, 
					new String[] {
					"BASE_IRPF * PORCENTAJE_IRPF/100" }, category);
			addPayment(aonContext, contract,addConcept(aonContext, "PREST_IT"),
					"TRACE('BASE_REGULADORA=%f\r\n',DIAS_ENFERMEDAD_COMUN * BASE_REGULADORA); "
					+"0.00 ", 
					"DIAS_ENFERMEDAD_COMUN * BASE_REGULADORA");

			Date endITDate = addDays(startITDate, itDays - 1);
			addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
					endITDate, null);
			
			System.out.println("IT [" + startITDate + "..." + endITDate + "]" + " days: " + itDays);

			Date startDate = getFirstDayOfMonth(getToday());
			Date endDate = getLastDayOfMonth(startDate);
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);

			SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();
			calculator.setSalaryBuilder(new SalaryBuilder() {
				@Override
				public void addPayment(Double amount, Double quote, Double tax, String description,
						java.util.Date startDate, java.util.Date endDate, IPayment payment,
						Map<String, ITimedVariable<?>> context) {
					System.out.println("Payment [" + description + "] : " + amount + " quote: " + quote + " tax: " + tax + " startDate: " + startDate + " endDate: " + endDate);
					context.entrySet().stream().forEach(entry->{
						System.out.println("Context [" + entry.getKey() + "] : " + entry.getValue().getValue(entry.getValue().getPeriod()));
					});
					super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				}
			});

			Salary salary = calculator.calculate(ctx);
			
//			salary.getSalaryPayments().stream().forEach(payment->{
//				System.out.println("Payment [" + payment.getExpression() + "] : " + payment.getAmount());
//			});

			int monthDays = get(endDate, DAY_OF_MONTH);
			assertEquals(2000.00
					* (monthDays - itDays) / monthDays, salary.getTotalPayment(), DELTA, format("%s :", TOTAL_LIQUID));
			assertEquals(2000.00, salary.getCommonBase(), DELTA, format("%s :", CGC_BASE));
		}
		

	}

	@Test
	public void testCommonDiseaseITWithLiquidAndIRPF()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				
				format("NETO(2000 * %s / %s)", ContextVariable.WORKED_DAYS , ContextVariable.MONTH_DAYS ),
				//"TRACE('NETO( %f )\r\n', P_0)"
				
				}, 
				new String[] {
				"BASE_IRPF > 0.00 ? BASE_IRPF * PORCENTAJE_IRPF/100 : 0.00" });
		//@formatter:on

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		double totalIrpf = calculator.calculate(
				getContractSalaryCalculatorContext(connection, startDate,
						endDate, endDate, contract)).getTotalIrpf();

		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH, 20);
		Date endITDate = getLastDayOfMonth(startITDate);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				endITDate, null);
		addPayment(aonContext, contract,
				"0.00 ",
				"X=BASE_REGULADORA;if( X > 0.00){TRACE('BR=%f\r\n',X);}DIAS_ENFERMEDAD_COMUN * X");

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		ISalary salary = calculator.calculate(ctx);

		int monthDays = get(endDate, DAY_OF_MONTH);

		int workDays = get(startITDate, DAY_OF_MONTH) - 1;
		assertEquals(2000.00 * workDays
				/ monthDays, salary.getTotalLiquid(), 0.5, format("%s :", TOTAL_LIQUID));


//		assertEquals(2000.00 + totalIrpf, //				salary.getCommonBase(), 0.9, format("%s :", CGC_BASE));

	}

	@Test
	public void testCommonDiseaseITWithGTZDO() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.DECEMBER;
						this.start = "01/12";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
				{
					put("DIAS_MES", "30"); // Monthly quote
				}
				},
				new String[] {
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" 
				}, 
				new String[] {
				}, category);
		addPayment(aonContext, contract, addConcept(aonContext, "GARANTIZADO"), 
				"GTZDO( P_0 + P_1,1,365*2)", 
				"0.00",
				PaymentType.CRA_0055);
		PaymentConceptRecord prestIT = addConcept(aonContext, "PREST_IT");
		addPayment(aonContext, contract, prestIT ,
				"TRACE('DIAS_ENFERMEDAD_COMUN * BASE_REGULADORA=%f\r\n',BASE_REGULADORA * DIAS_ENFERMEDAD_COMUN); "
				+"0.00", 
//				"DIAS_ENFERMEDAD_COMUN * BASE_REGULADORA"
				"DIAS_COTIZADOS * BASE_REGULADORA"
				);
		//@formatter:on

		Date startITDate = getToday();
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);
		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments())
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		assertEquals(1750.00, salary.getTotalPayment(), DELTA, format("%s :", TOTAL_PAYMENT));
		assertEquals(1750.00, salary.getCommonBase(), DELTA, format("%s :", CGC_BASE));

		startDate = getFirstDayOfMonth(add(getToday(), MONTH, 1));
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(connection, startDate,
				endDate, endDate, contract);
		salary = calculator.calculate(ctx);
		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments())
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		assertEquals(1750.00, salary.getTotalPayment(), DELTA, format("%s :", TOTAL_PAYMENT));
		assertEquals(1750.00, salary.getCommonBase(), DELTA, format("%s :", CGC_BASE));

		for (int i = 1; i <= 10; i++) {
			startDate = getFirstDayOfMonth(add(getToday(), MONTH, i));
			endDate = getLastDayOfMonth(startDate);
			ctx = getContractSalaryCalculatorContext(connection, startDate,
					endDate, endDate, contract);
			salary = calculator.calculate(ctx);
			System.out.println(startDate + "," + salary.getTotalPayment());
			
			assertEquals(1750.00, salary.getTotalPayment(), DELTA, format("%s :", TOTAL_PAYMENT));
			assertEquals(1750.00, salary.getCommonBase(), DELTA, format("%s :", CGC_BASE));
		}

	}

	@Test
	public void testCommonDiseaseITWithGTZDOII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.DECEMBER;
						this.start = "01/12";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
				{
					put("DIAS_MES", "30"); // Monthly quote
				}
				},
				new String[] {
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" 
				}, 
				new String[] {
				}, category);
		addPayment(aonContext, contract, addConcept(aonContext, "GARANTIZADO"),
				"GTZDO( P_0+ P_1,1)", 
				"0.00", PaymentType.CRA_0055);
		PaymentConceptRecord prestIT = addConcept(aonContext, "PREST_IT");
		addPayment(aonContext, contract, prestIT,
				"TRACE('DIAS_ENFERMEDAD_COMUN * BASE_REGULADORA=%f\r\n',BASE_REGULADORA * DIAS_ENFERMEDAD_COMUN); "
				+"0.00", 
				"DIAS_COTIZADOS * BASE_REGULADORA");
		//@formatter:on

		Date startITDate = getToday();
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());
		ISalary salary = calculator.calculate(ctx);
		assertEquals(1750.00, salary.getTotalPayment(), DELTA, format("%s :", TOTAL_PAYMENT));
		assertEquals(1750.00, salary.getCommonBase(), DELTA, format("%s :", CGC_BASE));

		for (int i = 1; i <= 10; i++) {
			startDate = getFirstDayOfMonth(add(getToday(), MONTH, i));
			endDate = getLastDayOfMonth(startDate);
			ctx = getContractSalaryCalculatorContext(connection, startDate,
					endDate, endDate, contract);
			salary = calculator.calculate(ctx);
			assertEquals(1750.00, salary.getTotalPayment(), DELTA, format("%s :", TOTAL_PAYMENT));
			assertEquals(1750.00, salary.getCommonBase(), DELTA, format("%s :", CGC_BASE));
		}

	}

	@Test
	public void testCommonDiseaseITWithGTZDOIII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.DECEMBER;
						this.start = "01/12";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
				{
					put("DIAS_MES", "30"); // Monthly quote
				}
				},
				new String[] {
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" 
				}, 
				new String[] {
				}, category);
		addPayment(aonContext, contract,addConcept(aonContext, "GARANTIZADO"), 
				"GTZDO( P_0+ P_1)", 
				"0.00"
				,PaymentType.CRA_0055);
		PaymentConceptRecord prestIT = addConcept(aonContext, "PREST_IT");
		addPayment(aonContext, contract, prestIT,
				"TRACE('DIAS_ENFERMEDAD_COMUN * BASE_REGULADORA=%f\r\n',BASE_REGULADORA * DIAS_ENFERMEDAD_COMUN); "
				+"0.00", 
				"DIAS_COTIZADOS * BASE_REGULADORA"
				);
		//@formatter:on

		Date startITDate = getToday();
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());
		ISalary salary = calculator.calculate(ctx);
		assertEquals(1750.00, salary.getTotalPayment(), DELTA, format("%s :", TOTAL_PAYMENT));
		assertEquals(1750.00, salary.getCommonBase(), DELTA, format("%s :", CGC_BASE));

		for (int i = 1; i <= 10; i++) {
			startDate = getFirstDayOfMonth(add(getToday(), MONTH, i));
			endDate = getLastDayOfMonth(startDate);
			ctx = getContractSalaryCalculatorContext(connection, startDate,
					endDate, endDate, contract);
			salary = calculator.calculate(ctx);
			assertEquals(1750.00, salary.getTotalPayment(), DELTA, format("%s :", TOTAL_PAYMENT));
			assertEquals(1750.00, salary.getCommonBase(), DELTA, format("%s :", CGC_BASE));
		}

	}

	@Test
	public void testBRPartialTimeI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] {});

		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
						//put(MONTH_DAYS.getName(), "30.00");
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								ContractCode.C200.getValue()));
					}
				}, new String[] { 
						"0.10 * P_1",
						"3000.00 * DIAS_TRABAJADOS / DIAS_MES" },
				new String[] {}, 
				category);

		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		int januaryDays = get(endDate, DAY_OF_MONTH);
		Date januaryStartDate = startDate;
		Date januaryEndDate = endDate;
		addData(aonContext, 
				contract, 
				startDate, 
				endDate, 
				new HashMap<String, String>() {
					{
						put(MONDAY_HOURS.getName(), format("%d", 4));
						put(TUESDAY_HOURS.getName(), format("%d", 4));
						put(WEDNESDAY_HOURS.getName(), format("%d", 4));
						put(THURSDAY_HOURS.getName(), format("%d", 4));
						put(FRIDAY_HOURS.getName(), format("%d", 4));
					}
				}
				);

		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		int februaryDays = get(endDate, DAY_OF_MONTH);
		Date februaryStartDate = startDate;
		Date februaryEndDate = endDate;
		addData(aonContext, 
				contract, 
				startDate, 
				endDate, 
				new HashMap<String, String>() {
					{
						put(MONDAY_HOURS.getName(), format("%d", 3));
						put(TUESDAY_HOURS.getName(), format("%d", 3));
						put(WEDNESDAY_HOURS.getName(), format("%d", 3));
						put(THURSDAY_HOURS.getName(), format("%d", 3));
						put(FRIDAY_HOURS.getName(), format("%d", 3));
					}
				}
				);

		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		int marchDays = get(endDate, DAY_OF_MONTH);
		Date marchStartDate = startDate;
		Date marchEndDate = endDate;
		addData(aonContext, 
				contract, 
				startDate, 
				endDate, 
				new HashMap<String, String>() {
					{
						put(MONDAY_HOURS.getName(), format("%d", 2));
						put(TUESDAY_HOURS.getName(), format("%d", 2));
						put(WEDNESDAY_HOURS.getName(), format("%d", 2));
						put(THURSDAY_HOURS.getName(), format("%d", 2));
						put(FRIDAY_HOURS.getName(), format("%d", 2));
					}
				}
				);

		//@formatter:off

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		double br = (3000.00 * 1.10 * ( 2.00/8.00 ) ) / (marchDays);
		
		// Same day of job start. Without salaries.
		ctx.getExpressionContext().setVariable("TODAY", startDate, startDate, endDate);
		assertEquals(br, ctx.getExpressionContext().eval("BR(TODAY)", startDate, endDate, Double.class).get(0).getValue(), DELTA);
		
		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		int aprilDays = get(endDate, DAY_OF_MONTH);
		addData(aonContext, 
				contract, 
				startDate, 
				endDate, 
				new HashMap<String, String>() {
					{
						put(MONDAY_HOURS.getName(), format("%d", 6));
						put(TUESDAY_HOURS.getName(), format("%d", 6));
						put(WEDNESDAY_HOURS.getName(), format("%d", 6));
						put(THURSDAY_HOURS.getName(), format("%d", 6));
						put(FRIDAY_HOURS.getName(), format("%d", 6));
					}
				}
				);
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		br = (3000.00 * 1.10 * ( 6.00/8.00 ) ) / (aprilDays);

		ctx.getExpressionContext().setVariable("TODAY", startDate, startDate, endDate);
		assertEquals(br, ctx.getExpressionContext().eval("BR(TODAY)", startDate, endDate, Double.class).get(0).getValue(), DELTA);
		
		// Save MARCH
		ctx = getContractSalaryCalculatorContext(
				connection, marchStartDate, marchEndDate, marchEndDate, contract);
		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		br = (3000.00 * 1.10 * ( 2.00/8.00 ) ) / (marchDays);
		ctx.getExpressionContext().setVariable("TODAY", startDate, startDate, endDate);
		assertEquals(br, ctx.getExpressionContext().eval("BR(TODAY)", startDate, endDate, Double.class).get(0).getValue(), DELTA);
		
		// Save FEBRUARY
		ctx = getContractSalaryCalculatorContext(
				connection, februaryStartDate, februaryEndDate, februaryEndDate, contract);
		jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();

		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		br = (3000.00 * 1.10 * ( 2.00/8.00 + 3.00/8.00) ) / (marchDays+ februaryDays);
		ctx.getExpressionContext().setVariable("TODAY", startDate, startDate, endDate);
		assertEquals(br, ctx.getExpressionContext().eval("BR(TODAY)", startDate, endDate, Double.class).get(0).getValue(), DELTA);

		// Save JANUARY
		ctx = getContractSalaryCalculatorContext(
				connection, januaryStartDate, januaryEndDate, januaryEndDate, contract);
		jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();

		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		br = (3000.00 * 1.10 * ( 2.00/8.00 + 3.00/8.00 + 4.00/8.00) ) / (marchDays+ februaryDays+ januaryDays);
		ctx.getExpressionContext().setVariable("TODAY", startDate, startDate, endDate);
		assertEquals(br, ctx.getExpressionContext().eval("BR(TODAY)", startDate, endDate, Double.class).get(0).getValue(), DELTA);
	}

	@Test
	public void testBRPartialTimeMaternityI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		Date startDate = getFirstDayOfYear(getToday());
		 
		ContractRecord contract = newContract(aonContext, 
				startDate,
				new HashMap<String, String>() {
					{
						put(MONTH_DAYS.getName(), "30.00");
						put(ContextVariable.TC2.getName(), format("\"%s\"",
						ContractCode.C501.getValue()));
					}
				}, new String[] { 
						"3000.00 * DIAS_TRABAJADOS / DIAS_MES" 
				},
				new String[] {}, 
				null);

		addData(aonContext, 
				contract, 
				startDate, 
				null, 
				new HashMap<String, String>() {
					{
						put(MONDAY_HOURS.getName(), "4.00");
						put(TUESDAY_HOURS.getName(), "4.00");
						put(WEDNESDAY_HOURS.getName(), "4.00");
						put(THURSDAY_HOURS.getName(), "4.00");
						put(FRIDAY_HOURS.getName(), "4.00");
						put(SATURDAY_HOURS.getName(), "0.00");
						put(SUNDAY_HOURS.getName(), "0.00");
					}
				}
				);
		
		//@formatter:off
		
		Date endDate = getLastDayOfMonth(startDate);
		
		int days = 0;
		int months = 0;
		
		months++;
		// JANUARY
		smartCalculateAndSave(connection, getContractSalaryCalculatorContext(
			connection, startDate, endDate, endDate, contract));
		days += get(endDate, Calendar.DAY_OF_MONTH);
		
		months++;
		// FEBRUARY
		startDate = add(startDate, Calendar.MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		smartCalculateAndSave(connection, getContractSalaryCalculatorContext(
			connection, startDate, endDate, endDate, contract));
		days += get(endDate, Calendar.DAY_OF_MONTH);

		months++;
		// MARCH
		startDate = add(startDate, Calendar.MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		smartCalculateAndSave(connection, getContractSalaryCalculatorContext(
			connection, startDate, endDate, endDate, contract));
		days += get(endDate, Calendar.DAY_OF_MONTH);
		
		months++;
		// APRIL
		startDate = add(startDate, Calendar.MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		smartCalculateAndSave(connection, getContractSalaryCalculatorContext(
			connection, startDate, endDate, endDate, contract));
		days += get(endDate, Calendar.DAY_OF_MONTH);
		
		
		months++;
		// MAY
		startDate = add(startDate, Calendar.MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		smartCalculateAndSave(connection, getContractSalaryCalculatorContext(
			connection, startDate, endDate, endDate, contract));
		days += get(endDate, Calendar.DAY_OF_MONTH);


		months++;
		// JUNE
		startDate = add(startDate, Calendar.MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		smartCalculateAndSave(connection, getContractSalaryCalculatorContext(
			connection, startDate, endDate, endDate, contract));
		days += get(endDate, Calendar.DAY_OF_MONTH);
		
		months++;
		// JULY
		startDate = add(startDate, Calendar.MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		smartCalculateAndSave(connection, getContractSalaryCalculatorContext(
			connection, startDate, endDate, endDate, contract));
		days += get(endDate, Calendar.DAY_OF_MONTH);

		// AUGUST
		startDate = add(startDate, Calendar.MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		smartCalculateAndSave(connection, getContractSalaryCalculatorContext(
			connection, startDate, endDate, endDate, contract));

		//SEPTEMBER
		startDate = add(startDate, Calendar.MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		Date startIt = add(startDate, DAY_OF_MONTH, 10);
		addIT(aonContext, contract, LeaveType.PATERNITY, startIt, null, null);
		
		double br = 1500.00 * months / ( days );
		

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		
		assertEquals(br, ctx.getExpressionContext().eval("BASE_REGULADORA", startDate, endDate, Double.class).get(0).getValue(), DELTA);
	}

	@Test
	public void testBRPartialTimeMaternityII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		Date contractStartDate = add(getToday(), Calendar.YEAR, -2);
		ContractRecord contract = newContract(aonContext, 
				contractStartDate,
				new HashMap<String, String>() {
					{
						put(MONTH_DAYS.getName(), "30.00");
						put(ContextVariable.TC2.getName(), format("\"%s\"",
						ContractCode.C501.getValue()));
					}
				}, new String[] { 
						"3000.00 * DIAS_TRABAJADOS / DIAS_MES" 
				},
				new String[] {}, 
				null);

		addData(aonContext, 
				contract, 
				contractStartDate, 
				null, 
				new HashMap<String, String>() {
					{
						put(MONDAY_HOURS.getName(), "4.00");
						put(TUESDAY_HOURS.getName(), "4.00");
						put(WEDNESDAY_HOURS.getName(), "4.00");
						put(THURSDAY_HOURS.getName(), "4.00");
						put(FRIDAY_HOURS.getName(), "4.00");
						put(SATURDAY_HOURS.getName(), "0.00");
						put(SUNDAY_HOURS.getName(), "0.00");
					}
				}
				);
		
		//@formatter:off
		int days = 0;
		Date startDate = getFirstDayOfMonth(contractStartDate);
		for ( int i = 0; i < 12; i++ ) {
			Date endDate = getLastDayOfMonth(startDate);
			smartCalculateAndSave(connection, getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract)) ;
			//System.out.println( "Nómina [" + startDate + "," + endDate + "] : "  );
			days += get(endDate, Calendar.DAY_OF_MONTH) - get(Period.max(startDate, contractStartDate), Calendar.DAY_OF_MONTH) +1; 
			startDate = add(startDate, Calendar.MONTH, 1);
		}
		
		//AON.getSalaries(aonContext, f -> f.getContractProperty().eq(contract.getId()))
		//.forEach( s -> System.out.println("Nómina [" + s.getStartDate() + "," + s.getEndDate() + "] : "  + s.getCommonContingenciesBase()));

		startDate = add(startDate, Calendar.MONTH, 1);
		Date endDate = getLastDayOfMonth(startDate);
		Date startIt = add(startDate, DAY_OF_MONTH, 10);
		addIT(aonContext, contract, LeaveType.MATERNITY, startIt, null, null);
		
		
		
		int firstMonthDays = get(getLastDayOfMonth(contractStartDate), Calendar.DAY_OF_MONTH) - get(contractStartDate, Calendar.DAY_OF_MONTH) + 1;
		double br = ( 1500.00 / 30 * Math.min(firstMonthDays, 30) + 1500.00 * 11 ) / Math.min(days, 365);
		
		//System.out.println("Days :"  + days  + ",  First month days :" + firstMonthDays +", BR :" + br );

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		
		//ctx.getExpressionContext().setVariable("TODAY", startIt, startDate, endDate);
		//assertEquals(br, ctx.getExpressionContext().eval("BR(TODAY)", startDate, endDate, Double.class).get(0).getValue(), DELTA);
		assertEquals(br, ctx.getExpressionContext().eval("BASE_REGULADORA", startDate, endDate, Double.class).get(0).getValue(), DELTA);
	}
	
	
	@Test
	public void testBRPartialTimeMaternity365() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		Date contractStartDate = add(getToday(), Calendar.YEAR, -2);
		ContractRecord contract = newContract(aonContext, 
				contractStartDate,
				new HashMap<String, String>() {
					{
						put(MONTH_DAYS.getName(), "30.00");
						put(ContextVariable.TC2.getName(), format("\"%s\"",
						ContractCode.C501.getValue()));
					}
				}, new String[] { 
						"3000.00 * DIAS_TRABAJADOS / DIAS_MES" 
				},
				new String[] {}, 
				null);

		addData(aonContext, 
				contract, 
				contractStartDate, 
				null, 
				new HashMap<String, String>() {
					{
						put(MONDAY_HOURS.getName(), "4.00");
						put(TUESDAY_HOURS.getName(), "4.00");
						put(WEDNESDAY_HOURS.getName(), "4.00");
						put(THURSDAY_HOURS.getName(), "4.00");
						put(FRIDAY_HOURS.getName(), "4.00");
						put(SATURDAY_HOURS.getName(), "0.00");
						put(SUNDAY_HOURS.getName(), "0.00");
					}
				}
				);
		
		//@formatter:off
		Date startDate = getFirstDayOfMonth(contractStartDate);
		for ( int i = 0; i < 13; i++ ) {
			startDate = add(startDate, Calendar.MONTH, 1);
			Date endDate = getLastDayOfMonth(startDate);
			smartCalculateAndSave(connection, getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract));
		}
		
		

		startDate = add(startDate, Calendar.MONTH, 2);
		Date endDate = getLastDayOfMonth(startDate);
		Date startIt = add(startDate, DAY_OF_MONTH, 10);
		addIT(aonContext, contract, LeaveType.MATERNITY, startIt, null, null);
		
		
		
		double br = 1500.00 * 12 / 365;
		

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		
		//ctx.getExpressionContext().setVariable("TODAY", startIt, startDate, endDate);
		//assertEquals(br, ctx.getExpressionContext().eval("BR(TODAY)", startDate, endDate, Double.class).get(0).getValue(), DELTA);
		assertEquals(br, ctx.getExpressionContext().eval("BASE_REGULADORA", startDate, endDate, Double.class).get(0).getValue(), DELTA);
	}
	

	@Test
	public void testMaternityI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
				{
					put("DIAS_MES", "30"); // Monthly quote
				}
				},
				new String[] {
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" }, 
				new String[] {
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				"BASE_IRPF * 0.00/100" }, category);
		//@formatter:on

		Date startDate = add(contract.getStartDate() ,Calendar.MONTH, 4 );
		
		Date startITDate = add(startDate , Calendar.DAY_OF_MONTH, 10 );
		Date endITDate = add(startITDate, Calendar.MONTH, 6  );
		addIT(aonContext, contract, LeaveType.MATERNITY, startITDate,
				endITDate, null);
		
		Date brDate = add(startITDate ,Calendar.MONTH, -2 );
		addPayment(aonContext, contract, brDate, getLastDayOfMonth(brDate), "500.00");
		
		Date endDate = getLastDayOfMonth(startDate);
		for ( Date date = contract.getStartDate(); date.before(startDate); date = add(date, Calendar.MONTH, 1)) {
			smartCalculateAndSave(connection, getContractSalaryCalculatorContext(
				connection, getFirstDayOfMonth(date), getLastDayOfMonth(date), getLastDayOfMonth(date), contract));
		}


		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		
		double br = ( 1750.00 * (1.00 + 1.00 / 6 )  + 500.00 ) / 30;

		assertEquals(br, ctx.getExpressionContext().eval("BASE_REGULADORA", startDate, endDate, Double.class).get(0).getValue(), DELTA);
		

	}
	
	@Test
	public void testMaternityII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		Date contractStartDate = add( getFirstDayOfYear(getToday()), Calendar.DAY_OF_MONTH, 11);
		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				contractStartDate,
				new HashMap<String,String>(){
				{
					put("DIAS_MES", "30"); // Monthly quote
				}
				},
				new String[] {
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				"250.00 * DIAS_TRABAJADOS / DIAS_MES", 
				"25.00 * DIAS_EFECTIVOS "
				}, 
				new String[] {
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				"BASE_IRPF * 0.00/100" }, null);
		//@formatter:on

		Date startDate = getFirstDayOfMonth(add(contract.getStartDate() ,Calendar.MONTH, 1 ));
		
		Date startITDate = add(startDate , Calendar.DAY_OF_MONTH, 10 );
		addIT(aonContext, contract, LeaveType.MATERNITY, startITDate,
				null, null);
		
		Date endDate = getLastDayOfMonth(startDate);
		for ( Date date = contract.getStartDate(); date.before(startDate); date = add(date, Calendar.MONTH, 1)) {
			smartCalculateAndSave(connection, getContractSalaryCalculatorContext(
				connection, getFirstDayOfMonth(date), getLastDayOfMonth(date), getLastDayOfMonth(date), contract));
		}
		
		com.esferalia.aon.occam.api.model.Salary salary = AON.getSalaries(aonContext, f -> f.getContractProperty().eq(contract.getId())).findFirst().orElseThrow();
		


		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		
		double br = salary.getCommonContingenciesBase() / salary.getSalaryDays();

		assertEquals(br, ctx.getExpressionContext().eval("BASE_REGULADORA", startDate, endDate, Double.class).get(0).getValue(), DELTA);
		

	}
	
	@Test
	public void testBRFijoDiscontinuoPeriods() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		Date startDate = getFirstDayOfYear(getToday());
		 
		ContractRecord contract = newContract(
				aonContext, 
				startDate,
				new HashMap<String, String>() {
					{
						put(MONTH_DAYS.getName(), "30.00");
						put(PARTIAL_FACTOR.getName(), "1.00");
						//put(TC2.getName(), format("\"%s\"",C300.getValue()));
						
					}
				}, new String[] { 
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES" 
				},
				new String[] {}, 
				null);
		
		
		addData(aonContext, contract, contract.getStartDate(), add(contract.getStartDate(), Calendar.DAY_OF_MONTH, 31+28+10), TC2, format("\"%s\"",C300.getValue()) );
		addData(aonContext, contract, add(contract.getStartDate(), Calendar.DAY_OF_MONTH, 31+28+11), null, TC2, format("\"%s\"",C300.getValue()) );
		
		//@formatter:off
		
		Date endDate = getLastDayOfMonth(startDate);
		
		int days = 0;
		int months = 0;
		
		// JANUARY
		smartCalculateAndSave(connection, getContractSalaryCalculatorContext(
			connection, startDate, endDate, endDate, contract));
		
		months++;
		// FEBRUARY
		startDate = add(startDate, Calendar.MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		smartCalculateAndSave(connection, getContractSalaryCalculatorContext(
			connection, startDate, endDate, endDate, contract));
		days += get(endDate, Calendar.DAY_OF_MONTH);

		months++;
		// MARCH
		startDate = add(startDate, Calendar.MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		//addPayment(aonContext, contract, startDate, endDate, "TRAMO("+get(startDate, Calendar.YEAR)+",3,15);0.00" );
		smartCalculateAndSave(connection, getContractSalaryCalculatorContext(
			connection, startDate, endDate, endDate, contract));
		days += get(endDate, Calendar.DAY_OF_MONTH);
		
		months++;
		// APRIL
		startDate = add(startDate, Calendar.MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		smartCalculateAndSave(connection, getContractSalaryCalculatorContext(
			connection, startDate, endDate, endDate, contract));
		days += get(endDate, Calendar.DAY_OF_MONTH);
		
		// MAY
		startDate = add(startDate, Calendar.MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		smartCalculateAndSave(connection, getContractSalaryCalculatorContext(
			connection, startDate, endDate, endDate, contract));

		// JUNE
		startDate = add(startDate, Calendar.MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		Date startIt = add(startDate, DAY_OF_MONTH, 10);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startIt, null, null);
		
		double br = 1500.00 * months / ( days );
		

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		
		assertEquals(br, ctx.getExpressionContext().eval("BASE_REGULADORA", startDate, endDate, Double.class).get(0).getValue(), DELTA);
	}
	// ------------------------------------------------------------------------

}
