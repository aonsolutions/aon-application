package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.BR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SALARY_START;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TOTAL_LIQUID;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TOTAL_PAYMENT;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getMax;
import static java.lang.String.format;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Test;

import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;

public class SQLBRTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000001;

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
		Assert.assertEquals(br, ctx.getExpressionContext().eval("BR(TODAY)", startDate, endDate, Double.class).get(0).getValue(), DELTA);
		
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
		Assert.assertEquals(br, ctx.getExpressionContext().eval("BR(TODAY)", startDate, endDate, Double.class).get(0).getValue(), DELTA);
		
		ctx.getExpressionContext()
		.eval(String.format("%s(%s)", BR,SALARY_START.getName() ), startDate, endDate)
		.stream().forEach(result->Assert.assertEquals(br, ((Number) result.getValue()).doubleValue(), DELTA) );


		// Next month of job start. But without salaries.
		ctx.getExpressionContext().setVariable("NEXT_MONTH", addMonths(getToday(), 1), startDate, endDate);
		Assert.assertEquals(br, ctx.getExpressionContext().eval("BR(NEXT_MONTH)", startDate, endDate, Double.class).get(0).getValue(), DELTA);

		
		
		// Save a salry for first month
		JooqSalaryBuilder salaryBuilder = new JooqSalaryBuilder(connection);
		new ContractSalaryCalculator<ISalary>(salaryBuilder).calculate(ctx);
		salaryBuilder.execute();

		addPayment(aonContext, contract, "100", "100");
		// Next month of job start. But now with salaries.
		Assert.assertEquals(br, ctx.getExpressionContext().eval("BR(NEXT_MONTH)", startDate, endDate, Double.class).get(0).getValue(), 0.005);

		// Next two month of job start. Without salaries.
		ctx.getExpressionContext().setVariable("NEXT_MONTH2", addMonths(getToday(), 2), startDate, endDate);
		Assert.assertEquals(br + (100d / 30d), ctx.getExpressionContext().eval("BR(NEXT_MONTH2)", startDate, endDate, Double.class).get(0).getValue(), 0.005);

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
				"( P_1 + P_2 ) * 0.10",
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" }, 
				new String[] {
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				"BASE_IRPF * 0.00/100" }, category);
		addPayment(aonContext, contract,
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

		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		ISalary salary = calculator.calculate(ctx);

		int monthDays = 30 ;//get(endDate, DAY_OF_MONTH);
		Assert.assertEquals(format("%s :", TOTAL_PAYMENT), 1750.00 * 1.10
				* (monthDays - 1) / monthDays, salary.getTotalPayment(), DELTA);
		Assert.assertEquals(format("%s :", CGC_BASE), (1750.00 * 1.10)
				* (1 + 1.00 / 12 + 1.00 / 12), salary.getCommonBase(), DELTA);

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
		Assert.assertEquals(format("%s :", TOTAL_PAYMENT), 1750.00 * 1.10
				* (workDays) / monthDays,
				salary.getTotalPayment(), DELTA);
		Assert.assertEquals(format("%s :", CGC_BASE), (1750.00 * 1.10)
				* (1 + 1.00 / 12 + 1.00 / 12), salary.getCommonBase(), DELTA);
		System.out.println("CGC_BASE [" + startDate + "..." + endDate + "] :"
				+ salary.getCommonBase());

		startDate = getFirstDayOfMonth(add(getToday(), MONTH, 2));
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(connection, startDate,
				endDate, endDate, contract);

		salary = calculator.calculate(ctx);
		Assert.assertEquals(format("%s :", TOTAL_PAYMENT), 0.00,
				salary.getTotalPayment(), DELTA);
		Assert.assertEquals(format("%s :", CGC_BASE), (1750.00 * 1.10)
				* (1 + 1.00 / 12 + 1.00 / 12), salary.getCommonBase(), DELTA);
		System.out.println("CGC_BASE [" + startDate + "..." + endDate + "] :"
				+ salary.getCommonBase());

		startDate = getFirstDayOfMonth(add(getToday(), MONTH, 3));
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(connection, startDate,
				endDate, endDate, contract);

		salary = calculator.calculate(ctx);
		Assert.assertEquals(format("%s :", TOTAL_PAYMENT), 0.00,
				salary.getTotalPayment(), DELTA);
		Assert.assertEquals(format("%s :", CGC_BASE), (1750.00 * 1.10)
				* (1 + 1.00 / 12 + 1.00 / 12), salary.getCommonBase(), DELTA);
		System.out.println("CGC_BASE [" + startDate + "..." + endDate + "] :"
				+ salary.getCommonBase());

		startDate = getFirstDayOfMonth(add(getToday(), MONTH, 4));
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(connection, startDate,
				endDate, endDate, contract);

		salary = calculator.calculate(ctx);
		Assert.assertEquals(format("%s :", TOTAL_PAYMENT), 0.00,
				salary.getTotalPayment(), DELTA);
		Assert.assertEquals(format("%s :", CGC_BASE), (1750.00 * 1.10)
				* (1 + 1.00 / 12 + 1.00 / 12), salary.getCommonBase(), DELTA);
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
		addPayment(aonContext, contract,
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

		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		ISalary salary = calculator.calculate(ctx);

		int monthDays = get(endDate, DAY_OF_MONTH);
		Assert.assertEquals(format("%s :", TOTAL_PAYMENT), 2000.00
				* (monthDays - itDays) / monthDays, salary.getTotalPayment(),
				DELTA);
		Assert.assertEquals(format("%s :", CGC_BASE), 2000.00,
				salary.getCommonBase(), DELTA);

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
		addPayment(aonContext, contract,
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

		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		ISalary salary = calculator.calculate(ctx);

		int monthDays = get(endDate, DAY_OF_MONTH);
		Assert.assertEquals(format("%s :", TOTAL_LIQUID), 2000.00
				* (monthDays - itDays) / monthDays, salary.getTotalLiquid(),
				DELTA);
		Assert.assertEquals(format("%s :", CGC_BASE), 2000.00,
				salary.getCommonBase(), DELTA);

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
				"BASE_IRPF * PORCENTAJE_IRPF/100" }, category);
		addPayment(aonContext, contract,
				"TRACE('BASE_REGULADORA=%f\r\n',DIAS_ENFERMEDAD_COMUN * BASE_REGULADORA); "
				+"0.00 ", 
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

		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		ISalary salary = calculator.calculate(ctx);

		int monthDays = get(endDate, DAY_OF_MONTH);
		Assert.assertEquals(format("%s :", TOTAL_LIQUID), 2000.00
				* (monthDays - itDays) / monthDays, salary.getTotalPayment(),
				DELTA);
		Assert.assertEquals(format("%s :", CGC_BASE), 2000.00,
				salary.getCommonBase(), DELTA);

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
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
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
		Assert.assertEquals(format("%s :", TOTAL_LIQUID), 2000.00 * workDays
				/ monthDays, salary.getTotalLiquid(), 0.5);


//		Assert.assertEquals(format("%s :", CGC_BASE), 2000.00 + totalIrpf,
//				salary.getCommonBase(), 0.9);

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
		addPayment(aonContext, contract,
				"GTZDO( P_0 + P_1,1,365*2)", 
				"0.00");
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
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);
		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments())
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		Assert.assertEquals(format("%s :", TOTAL_PAYMENT), 1750.00,
				salary.getTotalPayment(), DELTA);
		Assert.assertEquals(format("%s :", CGC_BASE), 1750.00,
				salary.getCommonBase(), DELTA);

		startDate = getFirstDayOfMonth(add(getToday(), MONTH, 1));
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(connection, startDate,
				endDate, endDate, contract);
		salary = calculator.calculate(ctx);
		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments())
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		Assert.assertEquals(format("%s :", TOTAL_PAYMENT), 1750.00,
				salary.getTotalPayment(), DELTA);
		Assert.assertEquals(format("%s :", CGC_BASE), 1750.00,
				salary.getCommonBase(), DELTA);

		for (int i = 1; i <= 10; i++) {
			startDate = getFirstDayOfMonth(add(getToday(), MONTH, i));
			endDate = getLastDayOfMonth(startDate);
			ctx = getContractSalaryCalculatorContext(connection, startDate,
					endDate, endDate, contract);
			salary = calculator.calculate(ctx);
			System.out.println(startDate + "," + salary.getTotalPayment());
			
			Assert.assertEquals(format("%s :", TOTAL_PAYMENT), 1750.00,
					salary.getTotalPayment(), DELTA);
			Assert.assertEquals(format("%s :", CGC_BASE), 1750.00,
					salary.getCommonBase(), DELTA);
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
		addPayment(aonContext, contract,
				"GTZDO( P_0+ P_1,1)", 
				"0.00");
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
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());
		ISalary salary = calculator.calculate(ctx);
		Assert.assertEquals(format("%s :", TOTAL_PAYMENT), 1750.00,
				salary.getTotalPayment(), DELTA);
		Assert.assertEquals(format("%s :", CGC_BASE), 1750.00,
				salary.getCommonBase(), DELTA);

		for (int i = 1; i <= 10; i++) {
			startDate = getFirstDayOfMonth(add(getToday(), MONTH, i));
			endDate = getLastDayOfMonth(startDate);
			ctx = getContractSalaryCalculatorContext(connection, startDate,
					endDate, endDate, contract);
			salary = calculator.calculate(ctx);
			Assert.assertEquals(format("%s :", TOTAL_PAYMENT), 1750.00,
					salary.getTotalPayment(), DELTA);
			Assert.assertEquals(format("%s :", CGC_BASE), 1750.00,
					salary.getCommonBase(), DELTA);
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
		addPayment(aonContext, contract,
				"GTZDO( P_0+ P_1)", 
				"0.00");
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
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());
		ISalary salary = calculator.calculate(ctx);
		Assert.assertEquals(format("%s :", TOTAL_PAYMENT), 1750.00,
				salary.getTotalPayment(), DELTA);
		Assert.assertEquals(format("%s :", CGC_BASE), 1750.00,
				salary.getCommonBase(), DELTA);

		for (int i = 1; i <= 10; i++) {
			startDate = getFirstDayOfMonth(add(getToday(), MONTH, i));
			endDate = getLastDayOfMonth(startDate);
			ctx = getContractSalaryCalculatorContext(connection, startDate,
					endDate, endDate, contract);
			salary = calculator.calculate(ctx);
			Assert.assertEquals(format("%s :", TOTAL_PAYMENT), 1750.00,
					salary.getTotalPayment(), DELTA);
			Assert.assertEquals(format("%s :", CGC_BASE), 1750.00,
					salary.getCommonBase(), DELTA);
		}

	}

	// ------------------------------------------------------------------------

}
