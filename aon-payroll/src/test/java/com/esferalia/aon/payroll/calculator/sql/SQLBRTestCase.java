package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.BR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SALARY_START;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TOTAL_LIQUID;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TOTAL_PAYMENT;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
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
import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;

public class SQLBRTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000001;

	@Test
	public void testBRI() throws ExpressionException, SQLException,
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
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" }, new String[] {
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" }, category);
		//@formatter:off
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();

		double br = (1750.00 * 1.10) * (1 + 1.00 / 12 + 1.00 / 12) / 30;

		// Same day of job start. Without salaries.
		Assert.assertEquals(br, (Double) ctx.br(getToday()), DELTA);
		
		ctx.getExpressionContext()
		.eval(String.format("%s(%s)", BR,SALARY_START.getName() ), startDate, endDate)
		.stream().forEach(result->Assert.assertEquals(br, ((Number) result.getValue()).doubleValue(), DELTA) );


		// Next month of job start. But without salaries.
		Assert.assertEquals(br, (Double) ctx.br(addMonths(getToday(), 1)),
				DELTA);

		
		
		// Save a salry for first month
		JooqSalaryBuilder salaryBuilder = new JooqSalaryBuilder(connection);
		new ContractSalaryCalculator<ISalary>(salaryBuilder).calculate(ctx);
		salaryBuilder.execute();

		addPayment(aonContext, contract, "100", "100");
		// Next month of job start. But now with salaries.
		Assert.assertEquals(br, (Double) ctx.br(addMonths(getToday(), 1)),
				0.005);

		// Next two month of job start. Without salaries.
		Assert.assertEquals(br + (100d / 30d),
				(Double) ctx.br(addMonths(getToday(), 2)), 0.005);

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
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				"( P_1 + P_2 ) * 0.10",
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" }, 
				new String[] {
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				"BASE_IRPF * 0.00/100" }, category);
		addPayment(aonContext, contract,
				"0.00 ", 
				"DIAS_ENFERMEDAD_COMUN * BASE_REGULADORA");
		//@formatter:on

		Date startITDate = getToday();
		Date endITDate = startITDate;
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				endITDate, null);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();

		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		ISalary salary = calculator.calculate(ctx);

		int monthDays = get(endDate, DAY_OF_MONTH);
		Assert.assertEquals(format("%s :", TOTAL_PAYMENT), 1750.00 * 1.10
				* (monthDays - 1) / monthDays, salary.getTotalPayment(), DELTA);
		Assert.assertEquals(format("%s :", CGC_BASE), (1750.00 * 1.10)
				* (1 + 1.00 / 12 + 1.00 / 12), salary.getCommonBase(), DELTA);

		startDate = getFirstDayOfMonth(add(getToday(), MONTH, 1));
		endDate = getLastDayOfMonth(startDate);

		monthDays = get(endDate, DAY_OF_MONTH);
		int leaveOffset = (int) (Math.random() * monthDays);
		startITDate = add(endDate, Calendar.DAY_OF_MONTH, (-1) * leaveOffset);
		endITDate = null;
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				endITDate, null);
		ctx = new SQLContractSalaryCalculatorContext(connection, startDate,
				endDate, endDate, criteria);
		ctx.next();

		salary = calculator.calculate(ctx);
		Assert.assertEquals(format("%s :", TOTAL_PAYMENT), 1750.00 * 1.10
				* (monthDays - (leaveOffset + 1)) / monthDays,
				salary.getTotalPayment(), DELTA);
		Assert.assertEquals(format("%s :", CGC_BASE), (1750.00 * 1.10)
				* (1 + 1.00 / 12 + 1.00 / 12), salary.getCommonBase(), DELTA);

		startDate = getFirstDayOfMonth(add(getToday(), MONTH, 2));
		endDate = getLastDayOfMonth(startDate);
		ctx = new SQLContractSalaryCalculatorContext(connection, startDate,
				endDate, endDate, criteria);
		ctx.next();

		salary = calculator.calculate(ctx);
		Assert.assertEquals(format("%s :", TOTAL_PAYMENT), 0.00,
				salary.getTotalPayment(), DELTA);
		Assert.assertEquals(format("%s :", CGC_BASE), (1750.00 * 1.10)
				* (1 + 1.00 / 12 + 1.00 / 12), salary.getCommonBase(), DELTA);
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

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();

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

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();

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
	public void testCommonDiseaseITWithPaymentAndIRPF() throws ExpressionException,
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
				"BASE_IRPF * PORCENTAJE_IRPF/100" }, category);
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

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();

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
	public void testCommonDiseaseITWithLiquidAndIRPF() throws ExpressionException,
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
				"BASE_IRPF * PORCENTAJE_IRPF/100" });
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

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();

		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		ISalary salary = calculator.calculate(ctx);

		int monthDays = get(endDate, DAY_OF_MONTH);
		Assert.assertEquals(format("%s :", TOTAL_LIQUID), 2000.00
				* (monthDays - itDays) / monthDays, salary.getTotalLiquid(),
				DELTA);
		//Assert.assertEquals(format("%s :", CGC_BASE), 2000.00,
		//		salary.getCommonBase(), DELTA);

	}
	// ------------------------------------------------------------------------

	protected final void addIT(AONContext aonContext, ContractRecord contract,
			LeaveType type, Date startDate, Date endDate, Double regulatoryBase) {
		aonContext.getDslContext()
				.insertInto(CONTRACT_LEAVE)
				.set(CONTRACT_LEAVE.DOMAIN, contract.getDomain())
				.set(CONTRACT_LEAVE.CONTRACT, contract.getId())
				.set(CONTRACT_LEAVE.START_DATE, startDate)
				.set(CONTRACT_LEAVE.END_DATE, endDate)
				.set(CONTRACT_LEAVE.DAILY_REG_BASE, regulatoryBase)
				// .set(CONTRACT_LEAVE.DAILY_CGC_BASE, regulatoryBase)
				// .set(CONTRACT_LEAVE.DAILY_CGP_BASE, regulatoryBase)
				.set(CONTRACT_LEAVE.DISCHARGE_CAUSE, (byte) type.ordinal())
				.execute();

	}

	protected final void addPayment(AONContext aonContext,
			ContractRecord contract, String expression, String quoteExpression) {
		aonContext
				.getDslContext()
				.insertInto(CONTRACT_PAYMENT)
				.set(CONTRACT_PAYMENT.DOMAIN, contract.getDomain())
				.set(CONTRACT_PAYMENT.CONTRACT, contract.getId())
				.set(CONTRACT_PAYMENT.START_DATE, contract.getStartDate())
				.set(CONTRACT_PAYMENT.END_DATE, contract.getEndDate())
				.set(CONTRACT_PAYMENT.EXPRESSION, expression)
				.set(CONTRACT_PAYMENT.QUOTE_EXPRESSION, quoteExpression)
				.set(CONTRACT_PAYMENT.TYPE,
						(byte) PaymentType.CRA_0001.ordinal())
				.set(CONTRACT_PAYMENT.SALARY_TYPE,
						(byte) SalaryType.SALARY.ordinal()).execute();

	}

}
