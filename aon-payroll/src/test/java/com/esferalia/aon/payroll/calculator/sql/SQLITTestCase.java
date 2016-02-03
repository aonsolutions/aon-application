package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_MAX;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_MIN;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.DIRECT_PAY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FRIDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.NATURAL_MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.OCCUPATIONAL_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_GROUP;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.THURSDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TUESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WEDNESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C200;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.lang.String.format;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.util.AonDateUtils;

import junit.framework.Assert;

public class SQLITTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000001;
	
	
	@Test
	public void testCommonDiseaseITI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, null);
		//@formatter:on

		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH,10);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				startITDate, null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}

		Assert.assertEquals(4, salary.getSalaryPayments().size());

	}


	@Test
	public void testMaternityIT() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {						
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, null);
		//@formatter:on
		
		PaymentConceptRecord maternity = addConcept(aonContext, MATERNITY.getName());
		addPayment(aonContext, contract, maternity, "DIAS_MATERNIDAD * 0", "DIAS_MATERNIDAD * BASE_REGULADORA");
		
		Date startITDate = getToday();
		addIT(aonContext, contract, LeaveType.MATERNITY, startITDate,
				null, 100.00);

		
		Date startDate = add(getFirstDayOfMonth(getToday()),MONTH,1);
		Date endDate = getLastDayOfMonth(startDate);
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		Assert.assertEquals(0.00, salary.getTotalPayment());
		Assert.assertEquals(0.00, salary.getTotalLiquid());
		Assert.assertEquals(get(endDate, DAY_OF_MONTH) * 100.00, salary.getCommonBase());

	}

	@Test
	public void testMaternityITII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		addSystemData(aonContext, getFirstDayOfYear(getToday()), null, 
				new HashMap<String,String>(){
			{
				put(CGC_BASE_MIN.getName(), "(1000.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30))");
			}
		});

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				"0.00"
							}, 
				new String[] {						
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, null);
		//@formatter:on
		
		PaymentConceptRecord maternity = addConcept(aonContext, MATERNITY.getName());
		addPayment(aonContext, contract, maternity, "DIAS_MATERNIDAD * 0", "DIAS_MATERNIDAD * BASE_REGULADORA");
		
		Date startITDate = getToday();
		addIT(aonContext, contract, LeaveType.MATERNITY, startITDate,
				null, 100.00);

		
		Date startDate = add(getFirstDayOfMonth(getToday()),MONTH,1);
		Date endDate = getLastDayOfMonth(startDate);
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		Assert.assertEquals(0.00, salary.getTotalPayment());
		Assert.assertEquals(0.00, salary.getTotalLiquid());
		Assert.assertEquals(get(endDate, DAY_OF_MONTH) * 100.00, salary.getCommonBase());

	}

	@Test
	public void testMaternityITIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		addSystemData(aonContext, getFirstDayOfYear(getToday()), null, 
				new HashMap<String,String>(){
			{
				put(CGC_BASE_MAX.getName(), "(3500.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30))");
			}
		});

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				"1000.00"
							}, 
				new String[] {						
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
//				"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, null);
		//@formatter:on
		
		PaymentConceptRecord maternity = addConcept(aonContext, MATERNITY.getName());
		addPayment(aonContext, contract, maternity, "DIAS_MATERNIDAD * 0", "DIAS_MATERNIDAD * BASE_REGULADORA");
		
		Date startITDate = getToday();
		addIT(aonContext, contract, LeaveType.MATERNITY, startITDate,
				null, 100.00);

		
		Date startDate = add(getFirstDayOfMonth(getToday()),MONTH,1);
		Date endDate = getLastDayOfMonth(startDate);
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		Assert.assertEquals(3500.00, salary.getCommonBase());

	}

	@Test
	public void testPregnancyRiskIT() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);


		//@formatter:off
		ContractRecord contract = newContract(aonContext,
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {						
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, null);
		//@formatter:on
		
		
		PaymentConceptRecord maternity = addConcept(aonContext, MATERNITY.getName());
		addPayment(aonContext, contract, maternity, "DIAS_MATERNIDAD * 0", "DIAS_MATERNIDAD * BASE_REGULADORA");
		
		Date startITDate = getToday();
		addIT(aonContext, contract, LeaveType.PREGNANCY_RISK, startITDate,
				null, 100.00);

		
		Date startDate = add(getFirstDayOfMonth(getToday()),MONTH,1);
		Date endDate = getLastDayOfMonth(startDate);
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		Assert.assertEquals(0.00, salary.getTotalPayment());
		Assert.assertEquals(0.00, salary.getTotalLiquid());
		Assert.assertEquals(get(endDate, DAY_OF_MONTH) * 100.00, salary.getCommonBase());

	}

	@Test
	public void testCommonDiseaseITII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:off
		
		AgreementRecord agreement = newAgreement(aonContext);
		addPayments(aonContext, agreement, getFirstDayOfYear(getToday()), 
				new Payment []{
			new Payment(){
				{
				this.expression= "P_0 + P_1";
				this.salary = SalaryType.EXTRA;
				}
			},
			new Payment(){
				{
				this.expression= "P_0 + P_1";
				this.salary = SalaryType.EXTRA;
				}
			}
		});
		
		AgreementLevelCategoryRecord category = newAgreementCategory(aonContext, agreement);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, category);
		//@formatter:on

		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH, 10);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				startITDate, null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder() {
			private int zeros = 0;

			@Override
			public void addZeroPayment(Double quote, Double tax,
					java.util.Date startDate, java.util.Date endDate,
					IPayment payment, Map<String, ITimedVariable<?>> context) {
				addPayment(0.00, quote, tax,
						zeros++ + "-." + payment.getDescription() + " "
								+ startDate + "..." + endDate, startDate,
						endDate, payment, context);
			}
		});
		Salary salary = calculator.calculate(ctx);

		Assert.assertEquals(8, salary.getSalaryPayments().size());

	}

	@Test
	public void testCommonDiseaseITIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, null);
		//@formatter:on

		Date startITDate =getFirstDayOfMonth(getToday());
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				startITDate, null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}

		Assert.assertEquals(2, salary.getSalaryPayments().size());

	}

	@Test
	public void testCommonDiseaseITIV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, null);
		//@formatter:on

		Date startITDate =getLastDayOfMonth(getToday());
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				startITDate, null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}

		Assert.assertEquals(2, salary.getSalaryPayments().size());

	}

	@Test
	public void testCommonDiseaseITV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				"TRACE('BASE_CGC=%f\r\n', BASE_CGC);BASE_CGC * 4.70 / 100"
				}, null);
		//@formatter:on
		
		addPayment(aonContext, contract, (String) null, "DIAS_ENFERMEDAD_COMUN_1_3 * BASE_REGULADORA");

		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH,10);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				startITDate, null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryDeduction deduction : salary
				.getSalaryDeductions()) {
			System.out.println(deduction.getExpression() + " = " + deduction.getAmount());
		}
		

		Assert.assertEquals( salary.getCommonBase() * 4.70d/100, salary.getTotalDeduction(), DELTA);

	}

	@Test
	public void testCommonDiseaseITVI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				"TRACE('BASE_CGC=%f\r\n', BASE_CGC);BASE_CGC * 4.70 / 100"
				}, null);
		//@formatter:on
		
		addPayment(aonContext, contract, (String) null, "DIAS_ENFERMEDAD_COMUN_1_3 * BASE_REGULADORA");
		addPayment(aonContext, contract, (String) null, "DIAS_ENFERMEDAD_COMUN_4_15 * BASE_REGULADORA");

		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH,10);
		Date endITDate = add(startITDate, DAY_OF_MONTH, 5);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				endITDate, null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryDeduction deduction : salary
				.getSalaryDeductions()) {
			System.out.println(deduction.getExpression() + " = " + deduction.getAmount());
		}
		

		Assert.assertEquals( salary.getCommonBase() * 4.70d/100, salary.getTotalDeduction(), DELTA);

	}

	@Test
	public void testCommonDiseaseITVII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				"TRACE('BASE_CGC=%f\r\n', BASE_CGC);BASE_CGC * 4.70 / 100.00"
				}, null);
		//@formatter:on
		
		addPayment(aonContext, contract, (String) null, "DIAS_ENFERMEDAD_COMUN_1_3 * BASE_REGULADORA");
		addPayment(aonContext, contract, (String) null, "DIAS_ENFERMEDAD_COMUN_4_15 * BASE_REGULADORA");
		addPayment(aonContext, contract, (String) null, "DIAS_ENFERMEDAD_COMUN_16_20 * BASE_REGULADORA");

		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH,10);
		Date endITDate = add(startITDate, DAY_OF_MONTH, 5);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryDeduction deduction : salary
				.getSalaryDeductions()) {
			System.out.println(deduction.getExpression() + " = " + deduction.getAmount());
		}
		

		Assert.assertEquals( salary.getCommonBase() * 4.70d/100, salary.getTotalDeduction(), DELTA);

	}

	@Test
	public void testCommonDiseaseITVIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				"TRACE('BASE_CGC=%f\r\n', BASE_CGC);BASE_CGC * 4.70 / 100.00"
				}, null);
		//@formatter:on
		
		addPayment(aonContext, contract, (String) null, "DIAS_ENFERMEDAD_COMUN_1_3 * BASE_REGULADORA");
		addPayment(aonContext, contract, (String) null, "DIAS_ENFERMEDAD_COMUN_4_15 * BASE_REGULADORA");
		addPayment(aonContext, contract, (String) null, "DIAS_ENFERMEDAD_COMUN_16_20 * BASE_REGULADORA");
		addPayment(aonContext, contract, (String) null, "DIAS_ENFERMEDAD_COMUN_21 * BASE_REGULADORA");

		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH,10);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, null);

		Date startDate = add(getFirstDayOfMonth(getToday()),MONTH,1);
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryDeduction deduction : salary
				.getSalaryDeductions()) {
			System.out.println(deduction.getExpression() + " = " + deduction.getAmount());
		}
		

		Assert.assertEquals( salary.getCommonBase() * 4.70d/100, salary.getTotalDeduction(), DELTA);

	}
	@Test
	public void testCommonDiseaseITIX() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				"TRACE('BASE_CGP=%f\r\n', BASE_CGP);BASE_CGC * 1.55 / 100",
				"TRACE('BASE_CGP=%f\r\n', BASE_CGP);BASE_CGC * 0.10 / 100"
				}, null);
		//@formatter:on
		
		addPayment(aonContext, contract, (String) null, "DIAS_ENFERMEDAD_COMUN_1_3 * 100");

		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH,10);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				startITDate, null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryDeduction deduction : salary
				.getSalaryDeductions()) {
			System.out.println(deduction.getExpression() + " = " + deduction.getAmount());
		}
		

		Assert.assertEquals( salary.getProfessionalBase() * 1.65d/100, salary.getTotalDeduction(), DELTA);

	}

	// ------------------------------------------------------------------------

	@Test
	public void testCommonDiseaseITQuoteDaysI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext,
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String, String>(){
					{
						put(ContextVariable.MONTH_DAYS.getName(), "30");
					}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				"TRACE('BASE_CGP=%f\r\n', BASE_CGP);BASE_CGC * 1.55 / 100",
				"TRACE('BASE_CGP=%f\r\n', BASE_CGP);BASE_CGC * 0.10 / 100",
				"TRACE('DIAS_IT_COTIZADOS=%f\r\n', DIAS_IT_COTIZADOS);0.00",
				}, null);
		//@formatter:on
		
		//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT.getName());
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_1_3",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_4_15",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_16_20",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_21",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		//@formatter:on

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		calendar.set(Calendar.MONTH, Calendar.MAY);
		calendar.set(Calendar.DAY_OF_MONTH, 5 );
		Date startITDate = new Date(calendar.getTimeInMillis());
		
		calendar.set(Calendar.DAY_OF_MONTH, 10 );
		Date endITDate = new Date(calendar.getTimeInMillis());		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				endITDate, 100.00);

		Date startDate = getFirstDayOfMonth(startITDate);
		Date endDate = getLastDayOfMonth(startITDate);
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		
		salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.forEach(data->System.out.println(data.getName() + " = " + data.getExpression() + "(" + data.getStartDate() + "..." + data.getEndDate() + ")"));
		;
		// + 4
		long count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == startDate.getDate())
		.peek(data->Assert.assertEquals(1750.00 * 4/30.00, Double.parseDouble(data.getExpression())))
		.count();
		
		Assert.assertEquals(1, count);
		
		// + 3
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == startITDate.getDate())
		.peek(data->Assert.assertEquals(100.00 * 3.00, Double.parseDouble(data.getExpression())))
		.count();
		Assert.assertEquals(1, count);
		
		// + 2  (Here ADJUST)
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 8)
		.peek(data->Assert.assertEquals(data.getEndDate().getDate(),10))
		.peek(data->Assert.assertEquals(100.00 * 2.00, Double.parseDouble(data.getExpression())))
		.count();
		Assert.assertEquals(1, count);
		
		// + 21
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 11)
		.peek(data->Assert.assertEquals(1750.00 * 21/30.00, Double.parseDouble(data.getExpression())))
		.count();
		Assert.assertEquals(1, count);
		//  = 30
		
		
	}


	@Test
	public void testCommonDiseaseITQuoteDaysII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext,
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String, String>(){
					{
						put(ContextVariable.MONTH_DAYS.getName(), ContextVariable.NATURAL_MONTH_DAYS.getName() );
					}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				"TRACE('BASE_CGP=%f\r\n', BASE_CGP);BASE_CGC * 1.55 / 100",
				"TRACE('BASE_CGP=%f\r\n', BASE_CGP);BASE_CGC * 0.10 / 100",
				"TRACE('DIAS_IT_COTIZADOS=%f\r\n', DIAS_IT_COTIZADOS);0.00",
				}, null);
		//@formatter:on
		
		//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT.getName());
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_1_3",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_4_15",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_16_20",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_21",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		//@formatter:on

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		calendar.set(Calendar.MONTH, Calendar.MAY);
		calendar.set(Calendar.DAY_OF_MONTH, 5 );
		Date startITDate = new Date(calendar.getTimeInMillis());
		
		calendar.set(Calendar.DAY_OF_MONTH, 10 );
		Date endITDate = new Date(calendar.getTimeInMillis());
		
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				endITDate, 100.00);

		Date startDate = getFirstDayOfMonth(startITDate);
		Date endDate = getLastDayOfMonth(startITDate);
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		
		salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.forEach(data->System.out.println(data.getName() + " = " + data.getExpression() + "(" + data.getStartDate() + "..." + data.getEndDate() + ")"));
		;
		// 4
		long count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == startDate.getDate())
		.peek(data->Assert.assertEquals(1750.00 * 4/31.00, Double.parseDouble(data.getExpression())))
		.count();
		
		Assert.assertEquals(1, count);
		
		// 3
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == startITDate.getDate())
		.peek(data->Assert.assertEquals(100.00 * 3.00, Double.parseDouble(data.getExpression())))
		.count();
		Assert.assertEquals(1, count);
		
		// 3
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 8)
		.peek(data->Assert.assertEquals(100.00 * 3.00, Double.parseDouble(data.getExpression())))
		.count();
		Assert.assertEquals(1, count);
		
		// 21
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 11)
		.peek(data->Assert.assertEquals(1750.00 * 21/31.00, Double.parseDouble(data.getExpression())))
		.count();
		Assert.assertEquals(1, count);
		
		// 31

	}
	
	@Test
	public void testCommonDiseaseITQuoteDaysIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		//@formatter:offhttp://www.marca.com/motor/formula1/2016/01/08/5690143c268e3e041d8b457d.html?cid=GEN35403
		ContractRecord contract = newContract(aonContext,
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String, String>(){
					{
						put(ContextVariable.MONTH_DAYS.getName(), "30");
					}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				"TRACE('BASE_CGP=%f\r\n', BASE_CGP);BASE_CGC * 1.55 / 100",
				"TRACE('BASE_CGP=%f\r\n', BASE_CGP);BASE_CGC * 0.10 / 100",
				"TRACE('DIAS_IT_COTIZADOS=%f\r\n', DIAS_IT_COTIZADOS);0.00",
				}, null);
		//@formatter:on
		
		//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT.getName());
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_1_3",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_4_15",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_16_20",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_21",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		//@formatter:on

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		calendar.set(Calendar.MONTH, Calendar.MAY);
		calendar.set(Calendar.DAY_OF_MONTH, 5 );
		Date startITDate = new Date(calendar.getTimeInMillis());
		
		
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, 100.00);

		Date startDate = getFirstDayOfMonth(startITDate);
		Date endDate = getLastDayOfMonth(startITDate);
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		
		salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.forEach(data->System.out.println(data.getName() + " = " + data.getExpression() + "(" + data.getStartDate() + "..." + data.getEndDate() + ")"));
		;
		// + 4
		long count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 1)
		.peek(data->Assert.assertEquals(1750.00 * 4/30.00, Double.parseDouble(data.getExpression())))
		.count();
		
		Assert.assertEquals(1, count);
		
		// + 3
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == startITDate.getDate())
		.peek(data->Assert.assertEquals(100.00 * 3.00, Double.parseDouble(data.getExpression())))
		.count();
		Assert.assertEquals(1, count);
		
		// + 12
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 8)
		.peek(data->Assert.assertEquals(data.getEndDate().getDate(),19))
		.peek(data->Assert.assertEquals(100.00 * 12.00, Double.parseDouble(data.getExpression())))
		.count();
		Assert.assertEquals(1, count);
		
		// + 5
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 20)
		.peek(data->Assert.assertEquals(data.getEndDate().getDate(),24))
		.peek(data->Assert.assertEquals(100.00 * 5.00, Double.parseDouble(data.getExpression())))
		.count();
		Assert.assertEquals(1, count);
		
		// + 6 ( ADJUST )
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 25)
		.peek(data->Assert.assertEquals(data.getEndDate().getDate(),31))
		.peek(data->Assert.assertEquals(100.00 * 6.00, Double.parseDouble(data.getExpression())))
		.count();
		Assert.assertEquals(1, count);
		
		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		salary = calculator.calculate(ctx);
		
		// One period , 30 ( NO adjust ) 
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.peek(data->Assert.assertEquals(100.00 * 30.00, Double.parseDouble(data.getExpression())))
		.count();
		Assert.assertEquals(1, count);

		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		salary = calculator.calculate(ctx);
		
		// One period , 30 ( YES adjust ) 
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.peek(data->Assert.assertEquals(100.00 * 30.00, Double.parseDouble(data.getExpression())))
		.count();
		Assert.assertEquals(1, count);
	}
	

	@Test
	public void testCommonDiseaseITQuoteDaysIV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		//@formatter:offhttp://www.marca.com/motor/formula1/2016/01/08/5690143c268e3e041d8b457d.html?cid=GEN35403
		ContractRecord contract = newContract(aonContext,
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String, String>(){
					{
						put(ContextVariable.MONTH_DAYS.getName(), "30");
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								C200.getValue()));
						put(MONDAY_HOURS.getName(), format("%d", 4));
						put(TUESDAY_HOURS.getName(), format("%d", 4));
						put(WEDNESDAY_HOURS.getName(), format("%d", 4));
						put(THURSDAY_HOURS.getName(), format("%d", 4));
						put(FRIDAY_HOURS.getName(), format("%d", 4));
					}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				"TRACE('BASE_CGP=%f\r\n', BASE_CGP);BASE_CGC * 1.55 / 100",
				"TRACE('BASE_CGP=%f\r\n', BASE_CGP);BASE_CGC * 0.10 / 100",
				"TRACE('DIAS_IT_COTIZADOS=%f\r\n', DIAS_IT_COTIZADOS);0.00",
				}, null);
		//@formatter:on
		
		//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT.getName());
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_1_3",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_4_15",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_16_20",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_21",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		//@formatter:on

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		calendar.set(Calendar.MONTH, Calendar.MAY);
		calendar.set(Calendar.DAY_OF_MONTH, 5 );
		Date startITDate = new Date(calendar.getTimeInMillis());
		
		
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, 100.00);

		Date startDate = getFirstDayOfMonth(startITDate);
		Date endDate = getLastDayOfMonth(startITDate);
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		
		salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.forEach(data->System.out.println(data.getName() + " = " + data.getExpression() + "(" + data.getStartDate() + "..." + data.getEndDate() + ")"));
		;
		// + 4
		long count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 1)
		.peek(data->Assert.assertEquals(1750.00 * 4/30.00 * 0.5, Double.parseDouble(data.getExpression())))
		.count();
		
		Assert.assertEquals(1, count);
		
		// + 3
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == startITDate.getDate())
		.peek(data->Assert.assertEquals(100.00 * 3.00, Double.parseDouble(data.getExpression())))
		.count();
		Assert.assertEquals(1, count);
		
		// + 12
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 8)
		.peek(data->Assert.assertEquals(data.getEndDate().getDate(),19))
		.peek(data->Assert.assertEquals(100.00 * 12.00, Double.parseDouble(data.getExpression())))
		.count();
		Assert.assertEquals(1, count);
		
		// + 5
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 20)
		.peek(data->Assert.assertEquals(data.getEndDate().getDate(),24))
		.peek(data->Assert.assertEquals(100.00 * 5.00, Double.parseDouble(data.getExpression())))
		.count();
		Assert.assertEquals(1, count);
		
		// + 7 ( NO ADJUST )
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 25)
		.peek(data->Assert.assertEquals(data.getEndDate().getDate(),31))
		.peek(data->Assert.assertEquals(100.00 * 7.00, Double.parseDouble(data.getExpression())))
		.count();
		Assert.assertEquals(1, count);
		
		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		salary = calculator.calculate(ctx);
		
		// One period , 30 ( NO adjust ) 
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.peek(data->Assert.assertEquals(100.00 * 30.00, Double.parseDouble(data.getExpression())))
		.count();
		Assert.assertEquals(1, count);

		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		salary = calculator.calculate(ctx);
		
		// One period , 31 ( NO adjust ) 
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.peek(data->Assert.assertEquals(100.00 * 31.00, Double.parseDouble(data.getExpression())))
		.count();
		Assert.assertEquals(1, count);
	}
	

	@Test
	public void testCommonDiseaseITQuoteDaysV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		
		addSystemData(aonContext, 
				getFirstDayOfYear(getToday()), 
				null, 
				new HashMap<String, String>(){
			{
				put(MONTH_DAYS.getName(), 
						String.format("[ \"10\": %s ][%s]", NATURAL_MONTH_DAYS.getName(), QUOTE_GROUP.getName() ) );
			}
		});
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext,
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String, String>(){
					{
						put(QUOTE_GROUP.getName(), "'10'" );
					}
				},
				new String[] {}, 
				new String[] {
				"TRACE('BASE_CGP=%f\r\n', BASE_CGP);BASE_CGC * 1.55 / 100",
				"TRACE('BASE_CGP=%f\r\n', BASE_CGP);BASE_CGC * 0.10 / 100",
				"TRACE('DIAS_IT_COTIZADOS=%f\r\n', DIAS_IT_COTIZADOS);0.00",
				}, null);
		//@formatter:on
		
		//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT.getName());
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_1_3",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_4_15",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_16_20",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_21",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract,  
				"250.00 * DIAS_TRABAJADOS / DIAS_MES", "_P"
				);
		addPayment(aonContext, contract,  
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES", "_P"
				);
		//@formatter:on

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		calendar.set(Calendar.MONTH, Calendar.MAY);
		calendar.set(Calendar.DAY_OF_MONTH, 5 );
		Date startITDate = new Date(calendar.getTimeInMillis());
		
		calendar.set(Calendar.DAY_OF_MONTH, 10 );
		Date endITDate = new Date(calendar.getTimeInMillis());		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				endITDate, 100.00);

		Date startDate = getFirstDayOfMonth(startITDate);
		Date endDate = getLastDayOfMonth(startITDate);
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		
		salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.forEach(data->System.out.println(data.getName() + " = " + data.getExpression() + "(" + data.getStartDate() + "..." + data.getEndDate() + ")"));
		;
		// + 4
		long count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == startDate.getDate())
		.peek(data->Assert.assertEquals(1750.00 * 4/31.00, Double.parseDouble(data.getExpression())))
		.count();
		
		Assert.assertEquals(1, count);
		
		// + 3
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == startITDate.getDate())
		.peek(data->Assert.assertEquals(100.00 * 3.00, Double.parseDouble(data.getExpression())))
		.count();
		Assert.assertEquals(1, count);
		
		// + 3  (No ADJUST)
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 8)
		.peek(data->Assert.assertEquals(data.getEndDate().getDate(),10))
		.peek(data->Assert.assertEquals(100.00 * 3.00, Double.parseDouble(data.getExpression())))
		.count();
		Assert.assertEquals(1, count);
		
		// + 21
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 11)
		.peek(data->Assert.assertEquals(1750.00 * 21/31.00, Double.parseDouble(data.getExpression())))
		.count();
		Assert.assertEquals(1, count);
		//  = 30
		
		
	}

	@Test
	public void testOccupationalDiseaseITQuoteDaysI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext,
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String, String>(){
					{
						put(ContextVariable.MONTH_DAYS.getName(), "30");
					}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				"TRACE('BASE_CGP=%f\r\n', BASE_CGP);BASE_CGC * 1.55 / 100",
				"TRACE('BASE_CGP=%f\r\n', BASE_CGP);BASE_CGC * 0.10 / 100",
				"TRACE('DIAS_IT_COTIZADOS=%f\r\n', DIAS_IT_COTIZADOS);0.00",
				}, null);
		//@formatter:on
		
		//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT.getName());
		addPayment(aonContext, contract, prestIT, 
				String.format("%s * BASE_REGULADORA * 0.75",  OCCUPATIONAL_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		//@formatter:on

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		calendar.set(Calendar.MONTH, Calendar.MAY);
		calendar.set(Calendar.DAY_OF_MONTH, 5 );
		Date startITDate = new Date(calendar.getTimeInMillis());
		
		calendar.set(Calendar.DAY_OF_MONTH, 10 );
		Date endITDate = new Date(calendar.getTimeInMillis());		
		
		addIT(aonContext, contract, LeaveType.OCCUPATIONAL_DISEASE, startITDate,
				endITDate, 100.00);

		Date startDate = getFirstDayOfMonth(startITDate);
		Date endDate = getLastDayOfMonth(startITDate);
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		
		salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.forEach(data->System.out.println(data.getName() + " = " + data.getExpression() + "(" + data.getStartDate() + "..." + data.getEndDate() + ")"));
		;
		// + 4
		long count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 1)
		.peek(data->Assert.assertEquals(1750.00 * 4/30.00, Double.parseDouble(data.getExpression())))
		.count();
		
		Assert.assertEquals(1, count);
		
		// + 5 ( Here ADJUST )
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == startITDate.getDate())
		.peek(data->Assert.assertEquals(100.00 * 5.00, Double.parseDouble(data.getExpression())))
		.count();
		Assert.assertEquals(1, count);		
		
		// + 21
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 11)
		.peek(data->Assert.assertEquals(1750.00 * 21/30.00, Double.parseDouble(data.getExpression())))
		.count();
		Assert.assertEquals(1, count);
		//  = 30
		
		
	}


	@Test
	public void testOccupationalDiseaseITQuoteDaysII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext,
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String, String>(){
					{
						put(ContextVariable.MONTH_DAYS.getName(), "30");
					}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				"TRACE('BASE_CGP=%f\r\n', BASE_CGP);BASE_CGC * 1.55 / 100",
				"TRACE('BASE_CGP=%f\r\n', BASE_CGP);BASE_CGC * 0.10 / 100",
				"TRACE('DIAS_IT_COTIZADOS=%f\r\n', DIAS_IT_COTIZADOS);0.00",
				}, null);
		//@formatter:on
		
		//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT.getName());
		addPayment(aonContext, contract, prestIT, 
				String.format("%s * BASE_REGULADORA * 0.75",  OCCUPATIONAL_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		//@formatter:on

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, 0);	
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		calendar.set(Calendar.MONTH, Calendar.MAY);
		calendar.set(Calendar.DAY_OF_MONTH, 5 );
		Date startITDate = new Date(calendar.getTimeInMillis());
		
		calendar.set(Calendar.DAY_OF_MONTH, 10 );
		
		addIT(aonContext, contract, LeaveType.OCCUPATIONAL_DISEASE, startITDate,
				null, 100.00);

		Date startDate = getFirstDayOfMonth(startITDate);
		Date endDate = getLastDayOfMonth(startITDate);
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		
		salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.forEach(data->System.out.println(data.getName() + " = " + data.getExpression() + "(" + data.getStartDate() + "..." + data.getEndDate() + ")"));
		;
		// + 4
		long count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 1)
		.peek(data->Assert.assertEquals(1750.00 * 4/30.00, Double.parseDouble(data.getExpression())))
		.count();
		
		Assert.assertEquals(1, count);
		
		// + 26 ( Here ADJUST )
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == startITDate.getDate())
		.peek(data->Assert.assertEquals(100.00 * 26.00, Double.parseDouble(data.getExpression())))
		.count();
		Assert.assertEquals(1, count);		
		
		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		salary = calculator.calculate(ctx);
		
		// One period , 30 ( NO adjust ) 
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.peek(data->Assert.assertEquals(100.00 * 30.00, Double.parseDouble(data.getExpression())))
		.count();
		Assert.assertEquals(1, count);			startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		salary = calculator.calculate(ctx);
		
		// One period , 30 ( YES adjust ) 
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.peek(data->Assert.assertEquals(100.00 * 30.00, Double.parseDouble(data.getExpression())))
		.count();
		Assert.assertEquals(1, count);
		
	}
	
	
	@Test
	public void testMaternityITQuoteDaysI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext,
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String, String>(){
					{
						put(ContextVariable.MONTH_DAYS.getName(), "30");
					}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				"TRACE('BASE_CGP=%f\r\n', BASE_CGP);BASE_CGC * 1.55 / 100",
				"TRACE('BASE_CGP=%f\r\n', BASE_CGP);BASE_CGC * 0.10 / 100",
				"TRACE('DIAS_IT_COTIZADOS=%f\r\n', DIAS_IT_COTIZADOS);0.00",
				}, null);
		//@formatter:on
		
		//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT.getName());
		addPayment(aonContext, contract, prestIT, 
				String.format("%s * BASE_REGULADORA",  MATERNITY_DAYS),
				String.format("BASE_REGULADORA *  %s",  QUOTE_DAYS)
				);
		//@formatter:on

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		calendar.set(Calendar.MONTH, Calendar.MAY);
		calendar.set(Calendar.DAY_OF_MONTH, 5 );
		Date startITDate = new Date(calendar.getTimeInMillis());
		
		calendar.set(Calendar.MONTH, Calendar.OCTOBER);
		calendar.set(Calendar.DAY_OF_MONTH, 10 );
		Date endITDate = new Date(calendar.getTimeInMillis());		
		
		addIT(aonContext, contract, LeaveType.MATERNITY, startITDate,
				endITDate, 100.00);

		Date startDate = getFirstDayOfMonth(startITDate);
		Date endDate = getLastDayOfMonth(startITDate);
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		
		salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.forEach(data->System.out.println(data.getName() + " = " + data.getExpression() + "(" + data.getStartDate() + "..." + data.getEndDate() + ")"));
		;
		// + 4
		long count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 1)
		.peek(data->Assert.assertEquals(1750.00 * 4/30.00, Double.parseDouble(data.getExpression())))
		.count();
		
		Assert.assertEquals(1, count);
		
		// + 26 ( Here ADJUST )
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == startITDate.getDate())
		.peek(data->Assert.assertEquals(100.00 * 26.00, Double.parseDouble(data.getExpression())))
		.count();
		Assert.assertEquals(1, count);		
		
		for ( int i = 0; i < 4; i++ ) {
			startDate = add(startDate, MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
			
			ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			calculator = new ContractSalaryCalculator<Salary>();
	
			calculator.setSalaryBuilder(new SalaryBuilder());
			salary = calculator.calculate(ctx);
			
			// One period , 30 
			count = salary.getSalaryDatas().stream()
			.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
			.peek(data->Assert.assertEquals(100.00 * 30.00, Double.parseDouble(data.getExpression())))
			.count();
			Assert.assertEquals(1, count);			
		}
		
		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		salary = calculator.calculate(ctx);
		
		// + 9
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 1)
		.peek(data->Assert.assertEquals(100.00 * 9.00, Double.parseDouble(data.getExpression())))
		.count();
		
		Assert.assertEquals(1, count);
		
		// + 21
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 11)
		.peek(data->Assert.assertEquals(1750.00 * 21/30.00, Double.parseDouble(data.getExpression())))
		.count();
		Assert.assertEquals(1, count);
	}
	
	@Test
	public void testCommonDiseaseIT365() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemCosts(aonContext);
		
		addSSRegimeCost(aonContext, 
				SSRegimeType.GENERAL, 
				AonDateUtils.getFirstDayOfYear(getToday()), 
				"CGC_E", 
				DeductionType.ADVANCE_PAYMENT, 
				"BASE_CGC_E * 23.60/100");
		addSSRegimeCost(aonContext, 
				SSRegimeType.GENERAL, 
				AonDateUtils.getFirstDayOfYear(getToday()), 
				"ECSS_E", 
				DeductionType.ADVANCE_PAYMENT, 
				"NOMINA ? ( -1 * DIAS_ENFERMEDAD_COMUN_21 * BASE_REGULADORA * 0.75  ) : REMOVE()");
		
		
		//@formatter:offhttp://www.marca.com/motor/formula1/2016/01/08/5690143c268e3e041d8b457d.html?cid=GEN35403
		ContractRecord contract = newContract(aonContext,
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String, String>(){
					{
						put(ContextVariable.MONTH_DAYS.getName(), "30");
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								ContractCode.C100.getValue()));
					}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				"TRACE('BASE_CGP=%f\r\n', BASE_CGP);BASE_CGC * 1.55 / 100",
				"TRACE('BASE_CGP=%f\r\n', BASE_CGP);BASE_CGC * 0.10 / 100",
				"TRACE('DIAS_IT_COTIZADOS=%f\r\n', DIAS_IT_COTIZADOS);0.00",
				}, null);
		//@formatter:on
		
		//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT.getName());
		PaymentConceptRecord directPay = addConcept(aonContext, DIRECT_PAY.getName());
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s_21",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, directPay, 
				String.format("BASE_REGULADORA * 0.00 * %s_366",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * %s",  QUOTE_DAYS)
				);
		//@formatter:on

		Date startITDate = add(getFirstDayOfMonth(getToday()), Calendar.DAY_OF_MONTH , 9);
		
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate, null, 100.00);
		
		Date _365Date = AonDateUtils.add(startITDate, Calendar.DAY_OF_MONTH,364);
		Date startDate = getFirstDayOfMonth(_365Date);
		Date endDate = getLastDayOfMonth(startDate);
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		cleanSystemCosts(aonContext);

		Assert.assertEquals(get(_365Date, DAY_OF_MONTH)* 100.00 * 0.75,salary.getTotalPayment() );
		Assert.assertEquals(30.00 * 100.00 , salary.getCommonBase() );
		
		Assert.assertEquals( get(_365Date, DAY_OF_MONTH)* 100.00 * 1.65 / 100.00 , 
				salary.getSocialSecurityContributions(), DELTA);

		Assert.assertEquals(
				(30.00 * 100.00* 23.60 / 100.00)+ 
				(-1)*get(_365Date, DAY_OF_MONTH)* 100.00 * 0.75
				
				, salary.getTotalEnterprise()
				, DELTA );

	}

	@Test
	public void testProfessionalDiseaseIT365() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemCosts(aonContext);
		
		addSSRegimeCost(aonContext, 
				SSRegimeType.GENERAL, 
				AonDateUtils.getFirstDayOfYear(getToday()), 
				"CGC_E", 
				DeductionType.ADVANCE_PAYMENT, 
				"BASE_CGC_E * 23.60/100");
		addSSRegimeCost(aonContext, 
				SSRegimeType.GENERAL, 
				AonDateUtils.getFirstDayOfYear(getToday()), 
				"ATEP_E", 
				DeductionType.ADVANCE_PAYMENT, 
				"NOMINA ? ( -1 * DIAS_ENFERMEDAD_PROFESIONAL * BASE_REGULADORA * 0.75 ) : REMOVE()");
		
		
		//@formatter:offhttp://www.marca.com/motor/formula1/2016/01/08/5690143c268e3e041d8b457d.html?cid=GEN35403
		ContractRecord contract = newContract(aonContext,
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String, String>(){
					{
						put(ContextVariable.MONTH_DAYS.getName(), "30");
						put(ContextVariable.TC2.getName(), format("\"%s\"",
								ContractCode.C100.getValue()));
					}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				"TRACE('BASE_CGP=%f\r\n', BASE_CGP);BASE_CGC * 1.55 / 100",
				"TRACE('BASE_CGP=%f\r\n', BASE_CGP);BASE_CGC * 0.10 / 100",
				"TRACE('DIAS_IT_COTIZADOS=%f\r\n', DIAS_IT_COTIZADOS);0.00",
				}, null);
		//@formatter:on
		
		//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT.getName());
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s",  OCCUPATIONAL_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		PaymentConceptRecord directPay = addConcept(aonContext, DIRECT_PAY.getName());
		addPayment(aonContext, contract, directPay, 
				String.format("BASE_REGULADORA * 0.00 * %s_366",  OCCUPATIONAL_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		//@formatter:on

		Date startITDate = add(getFirstDayOfMonth(getToday()), Calendar.DAY_OF_MONTH , 9);
		
		addIT(aonContext, contract, LeaveType.OCCUPATIONAL_DISEASE, startITDate, null, 100.00);
		
		Date _365Date = AonDateUtils.add(startITDate, Calendar.DAY_OF_MONTH,364);
		Date startDate = getFirstDayOfMonth(_365Date);
		Date endDate = getLastDayOfMonth(startDate);
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		cleanSystemCosts(aonContext);

		Assert.assertEquals(get(_365Date, DAY_OF_MONTH)* 100.00 * 0.75,salary.getTotalPayment() );
		Assert.assertEquals(30.00* 100.00 , salary.getCommonBase() );
		
		Assert.assertEquals( get(_365Date, DAY_OF_MONTH)* 100.00 * 1.65 / 100.00 , 
				salary.getSocialSecurityContributions(), DELTA);

		Assert.assertEquals((30.00* 100.00 * 23.60/100.00)+(-1)*get(_365Date, DAY_OF_MONTH)* 100.00 * 0.75, salary.getTotalEnterprise() );

	}
	
	
}
