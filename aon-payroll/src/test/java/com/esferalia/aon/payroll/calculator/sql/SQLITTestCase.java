package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_MAX;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_MIN;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.DIRECT_PAY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FRIDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY_BASE;
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
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C200;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.lang.String.format;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.payroll.IrpfOutcome;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryPayment;
//import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculator;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpressionVariable;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.util.AonDateUtils;

import junit.framework.Assert;

public class SQLITTestCase extends AbstractSQLTestCase {

	protected static final double DELTA = 0.004;
	
	@Test
	public void testInicioIT() throws ExpressionException, SQLException,
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
				null, null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		List<ITimedResult<java.util.Date>> results = ctx.getExpressionContext().eval(ContextVariable.IT_START.getName(), startDate, endDate, java.util.Date.class);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(startITDate, results.get(0).getValue());
		
		for ( int i = 1; i < 12 ; i++ ) {
			startDate = AonDateUtils.add(getFirstDayOfMonth(getToday()), MONTH,i);
			endDate = getLastDayOfMonth(startDate);
			ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			results = ctx.getExpressionContext().eval(ContextVariable.IT_START.getName(), startDate, endDate, java.util.Date.class);
			System.out.println(startDate + " : " + startITDate + ", " + results.get(0).getValue());
			Assert.assertEquals(1, results.size());
			Assert.assertEquals(startITDate, results.get(0).getValue());
		}
	}
	
	
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
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

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
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

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
					private static final long serialVersionUID = 1L;

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
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

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
		ISalaryCalculator<Salary, ISQLContractSalaryCalculatorContext> calculator = new SmartContractSalaryCalculator<Salary>();

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
		ISalaryCalculator<Salary, ISQLContractSalaryCalculatorContext> calculator = new SmartContractSalaryCalculator<Salary>();

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
				this.expression= "P_0 + P_1 ";
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
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

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

		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount() + ", "+ p.getQuote() );

		Assert.assertEquals(4 /*payments*/ * 2 /*tramos activo*/, salary.getSalaryPayments().size());

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
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

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
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

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
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

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
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

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
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

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
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

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
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

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
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
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
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

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
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
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
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

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
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
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
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

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
		calculator = new SmartContractSalaryCalculator<Salary>();

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
		calculator = new SmartContractSalaryCalculator<Salary>();

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
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
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
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

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
		calculator = new SmartContractSalaryCalculator<Salary>();

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
		calculator = new SmartContractSalaryCalculator<Salary>();

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
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
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
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

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
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
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
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

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
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
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
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

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
		calculator = new SmartContractSalaryCalculator<Salary>();

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
		calculator = new SmartContractSalaryCalculator<Salary>();

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
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
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
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

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
			calculator = new SmartContractSalaryCalculator<Salary>();
	
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
		calculator = new SmartContractSalaryCalculator<Salary>();

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
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
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
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

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
	public void testCommonDiseaseIT365Redefined() throws ExpressionException, SQLException,
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
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
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
		
		Date _365Date = AonDateUtils.add(startITDate, Calendar.DAY_OF_MONTH,494);
		
		Date _366Date = AonDateUtils.add(startITDate, Calendar.DAY_OF_MONTH,495);
		addData(aonContext, contract, startITDate, null, ContextVariable.DIRECT_PAY_START, 
				String.format("%s(%d,%d,%d)",ContextVariable.DATE,get(_366Date, YEAR), get(_366Date, MONTH)+1, get(_366Date, DAY_OF_MONTH) ));

		Date startDate = getFirstDayOfMonth(_365Date);
		Date endDate = getLastDayOfMonth(startDate);
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

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
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
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
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		cleanSystemCosts(aonContext);

		Assert.assertEquals(get(_365Date, DAY_OF_MONTH)* 100.00 * 0.75,salary.getTotalPayment() );
		Assert.assertEquals(30.00* 100.00 , salary.getCommonBase() );
		
		Assert.assertEquals( get(_365Date, DAY_OF_MONTH)* 100.00 * 1.65 / 100.00 , 
				salary.getSocialSecurityContributions(), DELTA);

		Assert.assertEquals((30.00* 100.00 * 23.60/100.00)+(-1)*get(_365Date, DAY_OF_MONTH)* 100.00 * 0.75, salary.getTotalEnterprise() );

	}
	
	@Test
	public void testProfessionalDiseaseIT365Redefined() throws ExpressionException, SQLException,
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
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
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
		
		Date _365Date = AonDateUtils.add(startITDate, Calendar.DAY_OF_MONTH,394);

		Date _366Date = AonDateUtils.add(startITDate, Calendar.DAY_OF_MONTH,395);
		addData(aonContext, contract, startITDate, null, ContextVariable.DIRECT_PAY_START, 
				String.format("%s(%d,%d,%d)",ContextVariable.DATE,get(_366Date, YEAR), get(_366Date, MONTH)+1, get(_366Date, DAY_OF_MONTH) ));
		
		Date startDate = getFirstDayOfMonth(_366Date);
		Date endDate = getLastDayOfMonth(startDate);
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();
		
		//ctx.getExpressionContext().eval(ContextVariable.DIRECT_PAY_START.getName(), startDate, endDate);

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		cleanSystemCosts(aonContext);

		Assert.assertEquals(get(_365Date, DAY_OF_MONTH)* 100.00 * 0.75,salary.getTotalPayment() );
		Assert.assertEquals(30.00*100.00 , salary.getCommonBase() );
		
		Assert.assertEquals( get(_365Date, DAY_OF_MONTH)* 100.00 * 1.65 / 100.00 , 
				salary.getSocialSecurityContributions(), DELTA);

		Assert.assertEquals((30.00*100.00 * 23.60/100.00)+(-1)*get(_365Date, DAY_OF_MONTH)* 100.00 * 0.75, salary.getTotalEnterprise() );

	}

	@Test
	public void testBaseIT() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		
		addSSRegimeData(aonContext, 
				SSRegimeType.GENERAL, 
				getFirstDayOfYear(getToday()), 
				null, 
				new HashMap<String, String>() {
					{
						put("BASE_CGC_MIN","764.40 * (DIAS_COTIZADOS == DIAS_MES ? 1 : DIAS_COTIZADOS/30)");						
						put("BASE_CGC_MAX","3642.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)");						
					}
				});

		// @formatter:off
		addSSRegimePayment(aonContext 
				,SSRegimeType.GENERAL 
				,getFirstDayOfYear(getToday()) 
				,PaymentType.CRA_0000
				,"0.00"
				,String.format("BASE_REGULADORA * 1.00 * %s_1_3",  COMMON_DISEASE_DAYS)
				,"_P"
				);
		addSSRegimePayment(aonContext 
				,SSRegimeType.GENERAL 
				,getFirstDayOfYear(getToday()) 
				,PaymentType.CRA_0000
				,String.format("BASE_REGULADORA * 0.60 * %s_4_15",  COMMON_DISEASE_DAYS)
				,String.format("BASE_REGULADORA * 1.00 * %s_4_15",  COMMON_DISEASE_DAYS)
				,"_P"
				);
		addSSRegimePayment(aonContext 
				,SSRegimeType.GENERAL 
				,getFirstDayOfYear(getToday()) 
				,PaymentType.CRA_0000
				,String.format("BASE_REGULADORA * 0.60 * %s_16_20",  COMMON_DISEASE_DAYS)
				,String.format("BASE_REGULADORA * 1.00 * %s_16_20",  COMMON_DISEASE_DAYS)
				,"_P"
				);
		addSSRegimePayment(aonContext 
				,SSRegimeType.GENERAL 
				,getFirstDayOfYear(getToday()) 
				,PaymentType.CRA_0000
				,String.format("BASE_REGULADORA * 0.75 * %s_21",  COMMON_DISEASE_DAYS)
				,String.format("BASE_REGULADORA * 1.00 * %s_21",  COMMON_DISEASE_DAYS)
				,"_P"
				);
		

		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "INPUT(\"/*user*/SALARIO_BASE/**/\",\"\")";
						this.quoteExpression = "_P/12";
						this.month = Month.DECEMBER;
						this.start = "01/07";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, 
				new Extra() {
					{
						this.expression = "INPUT(\"/*user*/SALARIO_BASE/**/\",\"\")";
						this.quoteExpression = "_P/12";
						this.month = Month.JULY;
						this.start = "01/01";
						this.end = "30/06";
						this.issue = "15/07";
					}
				}, 
				});

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						}
				, new String[] {
//						"BASE_CGC * 0.10", 
//						"BASE_CGP * 0.05",
//						"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, 
				category);
		//@formatter:on
		
		PaymentConceptRecord sbase = addConcept(aonContext, "SALARIO_BASE");
		addPayment(aonContext, contract, sbase, "1310.40 * DIAS_TRABAJADOS / DIAS_MES");



		
		Date startIt = add(getFirstDayOfMonth(getToday()), Calendar.DAY_OF_MONTH, 13);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				null, 
				null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount() + ", " +  p.getQuote() );

		for ( com.esferalia.aon.payroll.SalaryData d: salary.getSalaryDatas())
			if ( d.getName().startsWith("BASE_CGC") )
				System.out.println(d.getName() + " = " + d.getExpression() + ", " +  d.getStartDate() + "..." + d.getEndDate() );
		
		Assert.assertEquals(1310.40 + ( 1310.40 / 6 ), salary.getCommonBase(), DELTA);
		

	}
	
	@Test
	public void testBaseMinIT() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		
		addSSRegimeData(aonContext, 
				SSRegimeType.GENERAL, 
				getFirstDayOfYear(getToday()), 
				null, 
				new HashMap<String, String>() {
					{
						put("BASE_CGC_MIN","1000.00");						
					}
				});

		// @formatter:off
		
		PaymentConceptRecord prestIT = addConcept(aonContext, ContextVariable.PREST_IT);
		
		
		addSSRegimePayment(aonContext 
				,SSRegimeType.GENERAL 
				,getFirstDayOfYear(getToday())
				,prestIT
				,PaymentType.CRA_0000
				,"0.00"
				,String.format("BASE_REGULADORA * 1.00 * %s_1_3",  COMMON_DISEASE_DAYS)
				,"_P"
				,SalaryType.SALARY);
		
		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						}
				, new String[] {
				}, 
				null);
		//@formatter:on
		
		PaymentConceptRecord sbase = addConcept(aonContext, "SALARIO_BASE");
		addPayment(aonContext, contract, sbase, "500.00 * DIAS_TRABAJADOS / DIAS_MES");
		
		Date startIt = getLastDayOfMonth(getFirstDayOfYear(getToday()));;
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				null, 
				1.00);

		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		Assert.assertEquals( 1001.00, salary.getCommonBase(), DELTA );
		
		for ( com.esferalia.aon.payroll.SalaryData d: salary.getSalaryDatas()) {
			if ( d.getName().startsWith("BASE_CGC") ) {
				if ( d.getStartDate().equals(startIt) ) 
					Assert.assertEquals(1.00, Double.parseDouble(d.getExpression()), DELTA);
				else if ( d.getStartDate().equals(startDate) ) 
					Assert.assertEquals(1000.00, Double.parseDouble(d.getExpression()), DELTA); // TODO : DELTA????
				else 
					Assert.fail("Unexpected BASE_CGC");
			}
		}
		

	}

	@Test
	public void testBaseMinITII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		
		addSSRegimeData(aonContext, 
				SSRegimeType.GENERAL, 
				getFirstDayOfYear(getToday()), 
				null, 
				new HashMap<String, String>() {
					{
						put("BASE_CGC_MIN","1000.00");						
					}
				});

		// @formatter:off
		
		PaymentConceptRecord prestIT = addConcept(aonContext, ContextVariable.PREST_IT);
		
		
		addSSRegimePayment(aonContext 
				,SSRegimeType.GENERAL 
				,getFirstDayOfYear(getToday())
				,prestIT
				,PaymentType.CRA_0000
				,String.format("BASE_REGULADORA * 0.00 * %s_1_3",  COMMON_DISEASE_DAYS)
				,String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				,"_P"
				,SalaryType.SALARY);
		
		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						}
				, new String[] {
				}, 
				null);
		//@formatter:on
		
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), MONTH_DAYS.getName(), "30.00");
		
		PaymentConceptRecord sbase = addConcept(aonContext, "SALARIO_BASE");
		addPayment(aonContext, contract, sbase, "500.00 * DIAS_TRABAJADOS / DIAS_MES");
		
		Date startIt = add(getLastDayOfMonth(getFirstDayOfYear(getToday())), DAY_OF_MONTH, -1);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				null, 
				1.00);

		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		Assert.assertEquals( 1001.00, salary.getCommonBase(), DELTA );
		
		for ( com.esferalia.aon.payroll.SalaryData d: salary.getSalaryDatas()) {
			if ( d.getName().startsWith("BASE_CGC") ) {
				if ( d.getStartDate().equals(startIt) ) 
					Assert.assertEquals(1.00, Double.parseDouble(d.getExpression()), DELTA);
				else if ( d.getStartDate().equals(startDate) ) 
					Assert.assertEquals(1000.00, Double.parseDouble(d.getExpression()), DELTA); // TODO : DELTA????
				else 
					Assert.fail("Unexpected BASE_CGC");
			}
		}
		

	}

	@Test
	public void testBaseMinITIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		
		addSSRegimeData(aonContext, 
				SSRegimeType.GENERAL, 
				getFirstDayOfYear(getToday()), 
				null, 
				new HashMap<String, String>() {
					{
						put("BASE_CGC_MIN","1000.00");						
					}
				});

		// @formatter:off
		
		PaymentConceptRecord prestIT = addConcept(aonContext, ContextVariable.PREST_IT);
		
		
		addSSRegimePayment(aonContext 
				,SSRegimeType.GENERAL 
				,getFirstDayOfYear(getToday())
				,prestIT
				,PaymentType.CRA_0000
				,String.format("BASE_REGULADORA * 0.00 * %s_1_3",  COMMON_DISEASE_DAYS)
				,String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				,"_P"
				,SalaryType.SALARY);
		
		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						}
				, new String[] {
				}, 
				null);
		//@formatter:on
		
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), MONTH_DAYS.getName(), "30.00");
		
		PaymentConceptRecord sbase = addConcept(aonContext, "SALARIO_BASE");
		addPayment(aonContext, contract, sbase, "500.00 * DIAS_TRABAJADOS / DIAS_MES");
		
		Date startIt = getLastDayOfMonth(getFirstDayOfYear(getToday()));
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				null, 
				1.00);

		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		Assert.assertEquals( 1000.00, salary.getCommonBase(), DELTA );
		
		for ( com.esferalia.aon.payroll.SalaryData d: salary.getSalaryDatas()) {
			if ( d.getName().startsWith("BASE_CGC") ) {
				if ( d.getStartDate().equals(startIt) ) 
					Assert.assertEquals(0.00, Double.parseDouble(d.getExpression()), DELTA);
				else if ( d.getStartDate().equals(startDate) ) 
					Assert.assertEquals(1000.00, Double.parseDouble(d.getExpression()), DELTA); // TODO : DELTA????
				else 
					Assert.fail("Unexpected BASE_CGC");
			}
		}
		

	}

	@Test
	public void testBaseMinITIV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		
		addSSRegimeData(aonContext, 
				SSRegimeType.GENERAL, 
				getFirstDayOfYear(getToday()), 
				null, 
				new HashMap<String, String>() {
					{
						put("BASE_CGC_MIN","1000.00");						
					}
				});

		// @formatter:off
		
		PaymentConceptRecord prestIT = addConcept(aonContext, ContextVariable.PREST_IT);
		
		
		addSSRegimePayment(aonContext 
				,SSRegimeType.GENERAL 
				,getFirstDayOfYear(getToday())
				,prestIT
				,PaymentType.CRA_0000
				,String.format("BASE_REGULADORA * 0.00 * %s_1_3",  COMMON_DISEASE_DAYS)
				,String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				,"_P"
				,SalaryType.SALARY);
		
		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						}
				, new String[] {
				}, 
				null);
		//@formatter:on
		
		PaymentConceptRecord sbase = addConcept(aonContext, "SALARIO_BASE");
		addPayment(aonContext, contract, sbase, "500.00 * DIAS_TRABAJADOS / DIAS_MES");
		
		Date startIt = add(getLastDayOfMonth(getFirstDayOfYear(getToday())), DAY_OF_MONTH, -1);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				null, 
				1.00);

		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		Assert.assertEquals( 1002.00, salary.getCommonBase(), DELTA );
		
		for ( com.esferalia.aon.payroll.SalaryData d: salary.getSalaryDatas()) {
			if ( d.getName().startsWith("BASE_CGC") ) {
				if ( d.getStartDate().equals(startIt) ) 
					Assert.assertEquals(2.00, Double.parseDouble(d.getExpression()), DELTA);
				else if ( d.getStartDate().equals(startDate) ) 
					Assert.assertEquals(1000.00, Double.parseDouble(d.getExpression()), DELTA); // TODO : DELTA????
				else 
					Assert.fail("Unexpected BASE_CGC");
			}
		}
		

	}

	@Test
	public void testBaseMinITV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		
		addSSRegimeData(aonContext, 
				SSRegimeType.GENERAL, 
				getFirstDayOfYear(getToday()), 
				null, 
				new HashMap<String, String>() {
					{
						put("BASE_CGC_MIN","1000.00");						
					}
				});

		// @formatter:off
		
		PaymentConceptRecord prestIT = addConcept(aonContext, ContextVariable.PREST_IT);
		
		
		addSSRegimePayment(aonContext 
				,SSRegimeType.GENERAL 
				,getFirstDayOfYear(getToday())
				,prestIT
				,PaymentType.CRA_0000
				,String.format("BASE_REGULADORA * 0.00 * %s_1_3",  COMMON_DISEASE_DAYS)
				,String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				,"_P"
				,SalaryType.SALARY);
		
		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						}
				, new String[] {
				}, 
				null);
		//@formatter:on
		
		PaymentConceptRecord sbase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord garantizado = addConcept(aonContext, "GARANTIZADO");
		
		addPayment(aonContext, contract, sbase, "500.00 * DIAS_TRABAJADOS / DIAS_MES");
		addPayment(aonContext, contract, garantizado, "GTZDO(TODO)", "0.00", PaymentType.CRA_0055);
		
		Date startIt = add(getLastDayOfMonth(getFirstDayOfYear(getToday())), DAY_OF_MONTH, -1);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				null, 
				1.00);

		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		Assert.assertEquals( 1002.00, salary.getCommonBase(), DELTA );
		
		for ( com.esferalia.aon.payroll.SalaryData d: salary.getSalaryDatas()) {
			if ( d.getName().startsWith("BASE_CGC") ) {
				if ( d.getStartDate().equals(startIt) ) 
					Assert.assertEquals(2.00, Double.parseDouble(d.getExpression()), DELTA);
				else if ( d.getStartDate().equals(startDate) ) 
					Assert.assertEquals(1000.00, Double.parseDouble(d.getExpression()), DELTA); // TODO : DELTA????
				else 
					Assert.fail("Unexpected BASE_CGC");
			}
		}
		

	}

	@Test
	public void testWarningQuoteIT() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				"666.00"
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
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setListener( new SmartContractSalaryCalculator.Listener() {
			@Override
			public void onCheckError(IContractPayment payment, String message) {
				if ( "666.00".equals(payment.getExpression()) )
					throw new AssertionError("OK");
			}
		});

		calculator.setSalaryBuilder(new SalaryBuilder());
		try {
			Salary salary = calculator.calculate(ctx);
		} catch ( AssertionError e ){
			if ( "OK".equals(e.getMessage()))
					return;
		}

		Assert.fail();

	}

	@Test
	public void testPaternityIT() throws ExpressionException, SQLException,
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
				getFirstDayOfMonth(getToday()),
				Collections.emptyMap(),
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
		
		PaymentConceptRecord maternity = addConcept(aonContext, ContextVariable.MATERNITY.getName());
		addPayment(aonContext, contract, maternity, "DIAS_PATERNIDAD * 0", "DIAS_PATERNIDAD * BASE_REGULADORA");
		
		Date startITDate = getToday() ;
		addIT(aonContext, contract, LeaveType.PATERNITY, startITDate,
				null, 100.00);

	
		Date startDate = add(getFirstDayOfMonth(getToday()),MONTH,1);
		Date endDate = getLastDayOfMonth(startDate);
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder(){
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				ITimedVariable<?> factor = context.get(ContextVariable.PATERNITY_FACTOR.getName() );
				if ( factor == null )
					return;
				Assert.assertEquals(ExpressionScope.CONTRACT,((IExpressionVariable<?>) factor).getExpression().getScope());
			}
		});
		Salary salary = calculator.calculate(ctx);
		
		Assert.assertEquals(0.00, salary.getTotalPayment());
		Assert.assertEquals(0.00, salary.getTotalLiquid());
		Assert.assertEquals(get(endDate, DAY_OF_MONTH) * 100.00, salary.getCommonBase(), DELTA);
		
		
		startDate = getFirstDayOfMonth(getToday());
		endDate = getLastDayOfMonth(startDate);
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder(){
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				if ( payment.getName().equals(ContextVariable.MATERNITY.getName()) )
					Assert.assertTrue(context.containsKey(ContextVariable.PATERNITY_FACTOR.getName()));
			}
			
		});
		salary = calculator.calculate(ctx);

		int monthDays = get(endDate, Calendar.DATE) ;
		int workedDays = get(startITDate, Calendar.DATE) -1;
		Assert.assertEquals(1750.00 * workedDays / monthDays, salary.getTotalPayment(), DELTA);
		Assert.assertEquals(1750.00 * workedDays / monthDays + (monthDays - workedDays ) * 100.00, salary.getCommonBase(), DELTA);
	}

	@Test
	public void testPaternityITI() throws ExpressionException, SQLException,
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
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"}, 
				new String[] {						
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, null);
		//@formatter:on
		
		PaymentConceptRecord maternity = addConcept(aonContext, ContextVariable.MATERNITY.getName());
		addPayment(aonContext, contract, maternity, "DIAS_PATERNIDAD * 0" , "DIAS_PATERNIDAD * BASE_REGULADORA");
		
		int dayOfIt = (int ) (Math.floor(Math.random() * (29 - 2)) + 2);
		Date startITDate = add(getFirstDayOfMonth(getToday()), Calendar.DAY_OF_MONTH,dayOfIt);
		addIT(aonContext, contract, LeaveType.PATERNITY, startITDate,
				null, 100.00);
		addData(aonContext, contract, startITDate,
				null,ContextVariable.PATERNITY_FACTOR.getName(), "0.5");

		
		Date startDate = add(getFirstDayOfMonth(getToday()),MONTH,1);
		Date endDate = getLastDayOfMonth(startDate);
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		Assert.assertEquals(1750.00 * 1/2, salary.getTotalPayment(), DELTA);
		Assert.assertEquals(get(endDate, DAY_OF_MONTH) * 100.00 * 0.50 + 1750.00 * 1/2 , salary.getCommonBase(), DELTA);
		
		// Cret@ 
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculator = new SmartContractSalaryCalculator<Salary>();

		JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();
		
		AON.getSalaryData(aonContext,
				props -> props.getContractProperty().eq(contract.getId()))
				.forEach(s -> {

					// 500 Base de contingencias comunes.
					List<ContextData> datas = s.getContextData()
							.get(CGC_BASE.getName());
					Assert.assertEquals(1, datas.size());
					Assert.assertEquals(startDate, datas.get(0).getStartDate());
					Assert.assertEquals(endDate, datas.get(0).getEndDate());
					Assert.assertEquals(1750.00 * 0.5,
							Double.parseDouble(datas.get(0).getExpression()));

					// 635 o 634 Base de Accidentes de Trabajo.
					datas = s.getContextData().get(CGP_BASE.getName());
					Assert.assertEquals(1, datas.size());
					Assert.assertEquals(startDate, datas.get(0).getStartDate());
					Assert.assertEquals(endDate, datas.get(0).getEndDate());
					Assert.assertEquals(1750.00 * 0.5,
							Double.parseDouble(datas.get(0).getExpression()), DELTA);

					datas = s.getContextData()
							.get(MATERNITY_BASE.getName());
					Assert.assertEquals(1, datas.size());
					Assert.assertEquals(startDate, datas.get(0).getStartDate());
					Assert.assertEquals(endDate, datas.get(0).getEndDate());
					Assert.assertEquals(100.00 * get(endDate, DAY_OF_MONTH) * 0.5,
							Double.parseDouble(datas.get(0).getExpression()), DELTA);

					datas = s.getContextData()
							.get(WORKED_HOURS.getName());
					Assert.assertEquals(1, datas.size());
					Assert.assertEquals(startDate, datas.get(0).getStartDate());
					Assert.assertEquals(endDate, datas.get(0).getEndDate());
					long workDays = new Period(startDate, endDate).daysStream()
					.filter(day -> day.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
					.filter(day -> day.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY)
					.count();
					Assert.assertEquals(workDays * 8 * 0.5,
							Double.parseDouble(datas.get(0).getExpression()), DELTA);
					
				});
		;

		// Cret@ 
		ctx = getContractSalaryCalculatorContext(
				connection, 
				getFirstDayOfMonth(startITDate), 
				getLastDayOfMonth(startITDate), 
				getLastDayOfMonth(startITDate), 
				contract);
		calculator = new SmartContractSalaryCalculator<Salary>();

		jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();
		
		AON.getSalaryData(aonContext,
				props -> props.getContractProperty().eq(contract.getId())
						.and(props.getStartDateProperty().eq(getFirstDayOfMonth(startITDate)))
						)
				.forEach(s -> {
					Date start  =getFirstDayOfMonth(startITDate);
					Date end  =getLastDayOfMonth(startITDate);
					int monthDays = get(end, DAY_OF_MONTH);
					int workDays = get(startITDate, DAY_OF_MONTH)-1;
					int itDays = monthDays - workDays;
					// 500 Base de contingencias comunes.
					List<ContextData> datas = s.getContextData()
							.get(CGC_BASE.getName());
					Assert.assertEquals(2, datas.size());
					
					Assert.assertEquals(start, datas.get(0).getStartDate());
					Assert.assertEquals(add(startITDate, DAY_OF_MONTH, -1), datas.get(0).getEndDate());
					Assert.assertEquals(1750.00 * workDays / monthDays,
							Double.parseDouble(datas.get(0).getExpression()), DELTA);

					Assert.assertEquals(startITDate, datas.get(1).getStartDate());
					Assert.assertEquals(end, datas.get(1).getEndDate());
					Assert.assertEquals(1750.00 * itDays / monthDays * 0.5,
							Double.parseDouble(datas.get(1).getExpression()), DELTA);
					

					// 601 o 611 Base de Accidentes de Trabajo.
					datas = s.getContextData().get(CGP_BASE.getName());
					Assert.assertEquals(2, datas.size());
					Assert.assertEquals(2, datas.size());
					
					Assert.assertEquals(start, datas.get(0).getStartDate());
					Assert.assertEquals(add(startITDate, DAY_OF_MONTH, -1), datas.get(0).getEndDate());
					Assert.assertEquals(1750.00 * workDays / monthDays,
							Double.parseDouble(datas.get(0).getExpression()), DELTA);

					Assert.assertEquals(startITDate, datas.get(1).getStartDate());
					Assert.assertEquals(end, datas.get(1).getEndDate());
					Assert.assertEquals(1750.00 * itDays / monthDays * 0.5,
							Double.parseDouble(datas.get(1).getExpression()), DELTA);

					datas = s.getContextData()
							.get(MATERNITY_BASE.getName());
					Assert.assertEquals(1, datas.size());
					Assert.assertEquals(startITDate, datas.get(0).getStartDate());
					Assert.assertEquals(end, datas.get(0).getEndDate());
					Assert.assertEquals(100.00 * itDays * 0.5,
							Double.parseDouble(datas.get(0).getExpression()), DELTA);

					datas = s.getContextData()
							.get(WORKED_HOURS.getName());
					Assert.assertEquals(2, datas.size());
					Assert.assertEquals(start, datas.get(0).getStartDate());
					Assert.assertEquals(add(startITDate, DAY_OF_MONTH, -1), datas.get(0).getEndDate());

					Assert.assertEquals(startITDate, datas.get(1).getStartDate());
					Assert.assertEquals(end, datas.get(1).getEndDate());
					long workingDays = new Period(datas.get(1).getStartDate(), datas.get(1).getEndDate())
					.daysStream()
					.filter(day -> day.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
					.filter(day -> day.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY)
					.count();
					Assert.assertEquals(workingDays * 8 * 0.5,
							Double.parseDouble(datas.get(1).getExpression()), DELTA);
				});
		;
	}

	@Test
	public void testPaternityIT30() throws ExpressionException, SQLException,
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
				getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
					{
						put(MONTH_DAYS.getName(), "30");
					}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"}, 
				new String[] {						
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, null);
		//@formatter:on
		
		PaymentConceptRecord maternity = addConcept(aonContext, ContextVariable.MATERNITY.getName());
		addPayment(aonContext, contract, maternity, "DIAS_PATERNIDAD * 0" , "DIAS_COTIZADOS * COEFICIENTE_PATERNIDAD * BASE_REGULADORA");
		
		Date startITDate = add(getFirstDayOfYear(getToday()), DAY_OF_MONTH,9);
		addIT(aonContext, contract, LeaveType.PATERNITY, startITDate,
				null, 1750.00/30);
		addData(aonContext, contract, startITDate,
				null,ContextVariable.PATERNITY_FACTOR.getName(), "0.5");

		
		Date startDate =getFirstDayOfMonth(startITDate);
		Date endDate = getLastDayOfMonth(startDate);
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		Assert.assertEquals(1750.00 * 9 / 30 + 1750.00 * 21 / 30 * 0.5 , salary.getTotalPayment(), DELTA);
		Assert.assertEquals(1750.00, salary.getCommonBase(), DELTA);
		//Assert.assertEquals(get(endDate, DAY_OF_MONTH) * 100.00 * 0.50 + 1750.00 * 1/2 , salary.getCommonBase());
		
		
	}
	
	@Test
	public void testRedefinedIRPFIT() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = 
				newContract(aonContext, 
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				}, 
				new String[] {
				}, null);
		//@formatter:on
		
		
		PaymentConceptRecord prestIT = addConcept(aonContext, "PREST_IT");
		addPayment(aonContext, contract, prestIT 
				,String.format("BASE_REGULADORA * 0.00 * %s_1_3",  COMMON_DISEASE_DAYS)
				,String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);

		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH,10);
		Date endITDate = add(startITDate, DAY_OF_MONTH,2);
		
		addData(aonContext, contract, startITDate, null, ContextVariable.IRPF_PERCENT.getName(), "6.00");

		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				endITDate, null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		addPayment(aonContext, contract, startDate, endDate , "200.00");
		addPayment(aonContext, contract, add(startDate, Calendar.DAY_OF_MONTH,1), null , "NETO(200.00)");
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract, new IContractSalaryCalculatorContext.IListener() {
					
					@Override
					public void onIrpf(IrpfOutcome irpfOutcome) {
						// TODO Auto-generated method stub
					}
					
					@Override
					public void onRedefinedImplicit(String name, ITimedVariable<?> redefined, ITimedVariable<?> implicit) {
						System.out.println(name + " = " + redefined.getValue(redefined.getPeriod()) + ", " + implicit.getValue(implicit.getPeriod()));
					}

				});
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}

	}
	
	@Test
	public void testProfessionalDiseaseITOutOfBoundsI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startContrat = getFirstDayOfMonth(getToday());
		Date endContract =  add(startContrat, DAY_OF_MONTH,15);

		//@formatter:off
		ContractRecord contract = newContract
				(aonContext,
				startContrat,
				endContract,
				new HashMap<String,String>(){
					{
						put(ContextVariable.MONTH_DAYS.getName(), "30");
					}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, 
				null);
		//@formatter:on

		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s",  OCCUPATIONAL_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);

		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH,10);
		Date endITDate = add(startITDate, DAY_OF_MONTH,30);
		addIT(aonContext, contract, LeaveType.OCCUPATIONAL_DISEASE, startITDate,
				endITDate, null);


		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}
		
		Assert.assertEquals(( 1750.00/ 30.00 ) * (10 + 6 * 0.75) , salary.getTotalPayment());

	}
	
	@Test
	public void testITPrevious2ContractStartI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfMonth(getToday()),
				new HashMap<String,String>(){
					{
						put(MONTH_DAYS.getName(), "30");
					}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, null);

		//@formatter:on

		//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s_21",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		//@formatter:on

		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH, -25 );
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, 66.66);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		Assert.assertEquals(66.66 * 30.00, salary.getCommonBase(), DELTA);
		Assert.assertEquals(0.75 * 66.66 * /*30.00*/ get(endDate, DAY_OF_MONTH) , salary.getTotalPayment(), DELTA);
	}

	@Test
	public void testITPrevious2ContractStartII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		//@formatter:off
		Date contractStart = add(getToday(), YEAR, -1 ); 
		ContractRecord contract = newContract(aonContext,
				contractStart,
				new HashMap<String,String>(){
					{
						put(MONTH_DAYS.getName(), "30");
					}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, null);

		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s_21",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH, -25 );
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, null);
		
		Date stopDate = getFirstDayOfMonth(getToday());
		Date startDate = getFirstDayOfMonth(contractStart);
		
		while ( startDate.before(stopDate) ) {
			Date endDate = getLastDayOfMonth(startDate);
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			
			new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(startDate, MONTH, 1);
		}
		

		//@formatter:on

		startDate = stopDate;
		contract.setStartDate(stopDate);
		int updated = contract.update();
		Assert.assertEquals(1, updated);
		
		
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);
		double br = 1750.00 / 30.00;
		Assert.assertEquals(0.75 * br * /*30.00*/ get(endDate, DAY_OF_MONTH) , salary.getTotalPayment(), DELTA);
		Assert.assertEquals(br * 30.00, salary.getCommonBase(), DELTA);
	}

	@Test
	public void testITPrevious2ContractStartIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		//@formatter:off
		Date contractStart = add( getLastDayOfMonth(getToday()), DAY_OF_MONTH, -9 );
		ContractRecord contract = newContract(aonContext,
				add( getLastDayOfMonth(getToday()), DAY_OF_MONTH, -9 ),
				new HashMap<String,String>(){
					{
						put(MONTH_DAYS.getName(), "30");
					}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, null);

		//@formatter:on

		//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s_21",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		//@formatter:on

		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH, -25 );
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, 66.66);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		//Assert.assertEquals(66.66 * 10.00, salary.getCommonBase(), DELTA);
		long quoteDays = Math.round(salary.getCommonBase() / 66.66); //Double.parseDouble(salary.getSalaryData(QUOTE_DAYS.toString()));
		System.out.println("DIAS COTIZADOS : " + quoteDays );
		//System.out.println("NICIO CONTRATO : " + get(contractStart, DAY_OF_MONTH) );
		Assert.assertEquals(0.75 * 66.66 * /*(30.00 - get(contractStart, DAY_OF_MONTH) + 1 )*/ 10 , salary.getTotalPayment(), DELTA);
	}
	
	@Test
	public void testITPrevious2ContractStartIV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfMonth(getToday()),
				new HashMap<String,String>(){
					{
						put(MONTH_DAYS.getName(), "30");
					}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, null);

		//@formatter:on

		//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s_21",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		//@formatter:on

		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH, -25 );
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		Assert.assertEquals(0.75 * 1750.00/30.00 * /*30.00*/ get(endDate, DAY_OF_MONTH) , salary.getTotalPayment(), DELTA);
		Assert.assertEquals(1750.00/30.00 * 30.00, salary.getCommonBase(), DELTA);
	}
	
	@Test
	public void testITWithConstantI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startContract = add(getFirstDayOfMonth(getToday()), MONTH, -2 );
		//@formatter:off
		ContractRecord contract = newContract(
				aonContext,
				startContract,
				new HashMap<String,String>(){
					{
						put(MONTH_DAYS.getName(), "30");
					}
				},
				new String[] {
				"250.00" ,
				"1500.00"
				}, 
				new String[] {
				}, 
				null);
		//@formatter:on

		addPrestITs(aonContext, contract);


		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH,10);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, null);
		
		Date brEndDate = add(getFirstDayOfMonth(startITDate), DAY_OF_MONTH,-1);
		Date brStartDate = getFirstDayOfMonth(brEndDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, brStartDate, brEndDate, brEndDate, contract);
		JooqSalaryBuilder<Salary> builder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(builder).calculate(ctx);
		builder.execute();
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>(); 

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments())
			System.out.println(payment.getName() + " = " + payment.getAmount() + ", " + payment.getQuote()
					+ " (" + payment.getExpression() + ")");

		Assert.assertEquals(1750.00, salary.getCommonBase(), DELTA);
		Assert.assertEquals(1750.00/30.00 * get(endDate, DAY_OF_MONTH), salary.getTotalPayment(), DELTA);

		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		calculator.setSalaryBuilder(new SalaryBuilder());
		salary = calculator.calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments())
			System.out.println(payment.getName() + " = " + payment.getAmount() + ", " + payment.getQuote()
					+ " (" + payment.getExpression() + ")");

		Assert.assertEquals(1750.00, salary.getCommonBase(), DELTA);
		Assert.assertEquals(1750.00/30.00 * get(endDate, DAY_OF_MONTH), salary.getTotalPayment(), DELTA);
	}


	
	@Test
	public void testITWithConstantII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startContract = add(getFirstDayOfMonth(getToday()), MONTH, -2 );
		//@formatter:off
		ContractRecord contract = newContract(
				aonContext,
				startContract,
				new HashMap<String,String>(){
					{
						put(MONTH_DAYS.getName(), "30");
					}
				},
				new String[] {
				}, 
				new String[] {
				}, 
				null);
		PaymentConceptRecord salarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord paga = addConcept(aonContext, "PAGA");
		PaymentConceptRecord antiguedad = addConcept(aonContext, "ANTIGUEDAD");
		addPayment(aonContext, contract, startContract, null, salarioBase, "SALARIO BASE", "1500.00", "_P", "_P", PaymentType.CRA_0001);
		addPayment(aonContext, contract, startContract, null, paga, "PAGA EXTRA", "100.00", "_P", "_P", PaymentType.CRA_0004);
		addPayment(aonContext, contract, startContract, null, paga, "PAGA EXTRA", "100.00", "_P", "_P", PaymentType.CRA_0004);
		addPayment(aonContext, contract, startContract, null, antiguedad, "ANTIGÜEDAD", "50.00", "_P", "_P", PaymentType.CRA_0001);
		
		//@formatter:on
		
		addPrestITs(aonContext, contract);

		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH,10);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, null);
		
		Date brEndDate = add(getFirstDayOfMonth(startITDate), DAY_OF_MONTH,-1);
		Date brStartDate = getFirstDayOfMonth(brEndDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, brStartDate, brEndDate, brEndDate, contract);
		JooqSalaryBuilder<Salary> builder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(builder).calculate(ctx);
		builder.execute();
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>(); 

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments())
			System.out.println(payment.getName() + " = " + payment.getAmount() + ", " + payment.getQuote()
					+ " (" + payment.getExpression() + ")");

		Assert.assertEquals(1750.00, salary.getCommonBase(), DELTA);
		Assert.assertEquals(1750.00/30.00 * get(endDate, DAY_OF_MONTH), salary.getTotalPayment(), DELTA);

		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		calculator.setSalaryBuilder(new SalaryBuilder());
		salary = calculator.calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments())
			System.out.println(payment.getName() + " = " + payment.getAmount() + ", " + payment.getQuote()
					+ " (" + payment.getExpression() + ")");

		Assert.assertEquals(1750.00, salary.getCommonBase(), DELTA);
		Assert.assertEquals(1750.00/30.00 * get(endDate, DAY_OF_MONTH), salary.getTotalPayment(), DELTA);
	}


	@Test
	public void testITWithConstantIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		PaymentConceptRecord salarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord antiguedad = addConcept(aonContext, "ANTIGUEDAD");

		Date startContract = add(getFirstDayOfMonth(getToday()), MONTH, -2 );
		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "SALARIO_BASE + ANTIGUEDAD";
						this.month = Month.DECEMBER;
						this.start = "01/12";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, 
				new Extra() {
					{
						this.expression = "SALARIO_BASE + ANTIGUEDAD";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, 
				});
		AgreementRecord agreement = 
				getAgreement(aonContext, category.getAgreementLevel());
		
		addPayment(aonContext, agreement, startContract, new Payment() {
			{
				this.expression = "1450.00";
				this.concept = salarioBase.getId();
			}
		});
		addPayment(aonContext, agreement, startContract, new Payment() {
			{
				this.expression = "50.00";
				this.concept = antiguedad.getId();
			}
		});
		ContractRecord contract = newContract(
				aonContext,
				startContract,
				new HashMap<String,String>(){
					{
						put(MONTH_DAYS.getName(), "30");
					}
				},
				new String[] {
				}, 
				new String[] {
				}, 
				category);
		//@formatter:off

		
		//@formatter:on
		
		addPrestITs(aonContext, contract);

		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH,10);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, null);
		
		Date brEndDate = add(getFirstDayOfMonth(startITDate), DAY_OF_MONTH,-1);
		Date brStartDate = getFirstDayOfMonth(brEndDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, brStartDate, brEndDate, brEndDate, contract);
		JooqSalaryBuilder<Salary> builder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(builder).calculate(ctx);
		builder.execute();
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>(); 

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments())
			System.out.println(payment.getName() + " = " + payment.getAmount() + ", " + payment.getQuote()
					+ " (" + payment.getExpression() + ")");

		Assert.assertEquals(1750.00, salary.getCommonBase(), DELTA);
		int activeDays =  get(startITDate, DAY_OF_MONTH) -1 ;
		Assert.assertEquals(1500.00 / 30 * activeDays 
							+ 1750.00/30.00 * ( get(endDate, DAY_OF_MONTH) - activeDays ) , 
							
							salary.getTotalPayment(), DELTA);

		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		calculator.setSalaryBuilder(new SalaryBuilder());
		salary = calculator.calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments())
			System.out.println(payment.getName() + " = " + payment.getAmount() + ", " + payment.getQuote()
					+ " (" + payment.getExpression() + ")");

		Assert.assertEquals(1750.00, salary.getCommonBase(), DELTA);
		Assert.assertEquals(1750.00/30.00 * get(endDate, DAY_OF_MONTH), salary.getTotalPayment(), DELTA);
	}
	
	@Ignore("BRUTO it's no yet 'SMART' supported")
	@Test
	public void testITWithConstantIV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startContract = add(getFirstDayOfMonth(getToday()), MONTH, -2 );
		//@formatter:off
		ContractRecord contract = newContract(
				aonContext,
				startContract,
				new HashMap<String,String>(){
					{
						put(MONTH_DAYS.getName(), "30");
					}
				},
				new String[] {
				}, 
				new String[] {
				}, 
				null);
		PaymentConceptRecord salarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord plus = addConcept(aonContext, "PLUS");
		PaymentConceptRecord antiguedad = addConcept(aonContext, "ANTIGUEDAD");
		addPayment(aonContext, contract, startContract, null, salarioBase, "SALARIO BASE", "1100.00 * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0001);
		addPayment(aonContext, contract, startContract, null, antiguedad, "ANTIGÜEDAD", "50.00 * DIAS_TRABAJADOS / DIAS_MES" , "_P", "_P", PaymentType.CRA_0001);
		addPayment(aonContext, contract, startContract, null, plus, "PLUS", "BRUTO(1750.00)", "_P", "_P", PaymentType.CRA_0001);
		
		//@formatter:on
		
		addPrestITs(aonContext, contract);


		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH,10);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, null);
		
		Date brEndDate = add(getFirstDayOfMonth(startITDate), DAY_OF_MONTH,-1);
		Date brStartDate = getFirstDayOfMonth(brEndDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, brStartDate, brEndDate, brEndDate, contract);
		JooqSalaryBuilder<Salary> builder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(builder).calculate(ctx);
		builder.execute();
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>(); 

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments())
			System.out.println(payment.getName() + " = " + payment.getAmount() + ", " + payment.getQuote()
					+ " (" + payment.getExpression() + ")");

		Assert.assertEquals(1750.00, salary.getCommonBase(), DELTA);
		Assert.assertEquals(1750.00/30.00 * get(endDate, DAY_OF_MONTH), salary.getTotalPayment(), DELTA);

		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		calculator.setSalaryBuilder(new SalaryBuilder());
		salary = calculator.calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments())
			System.out.println(payment.getName() + " = " + payment.getAmount() + ", " + payment.getQuote()
					+ " (" + payment.getExpression() + ")");

		Assert.assertEquals(1750.00, salary.getCommonBase(), DELTA);
		Assert.assertEquals(1750.00/30.00 * get(endDate, DAY_OF_MONTH), salary.getTotalPayment(), DELTA);
	}

	@Test
	public void testITWithConstantVI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date startContract = add(getFirstDayOfMonth(getToday()), MONTH, -2 );
		//@formatter:off
		ContractRecord contract = newContract(
				aonContext,
				startContract,
				new HashMap<String,String>(){
					{
						put(MONTH_DAYS.getName(), "30");
					}
				},
				new String[] {
				}, 
				new String[] {
				}, 
				null);
		addPayment(aonContext, contract, startContract, null, "SALARIO BASE", "1500.00", "_P", "_P", PaymentType.CRA_0001, SalaryType.SALARY);
		addPayment(aonContext, contract, startContract, null, "PAGA EXTRA", "100.00", "_P", "_P", PaymentType.CRA_0004, SalaryType.SALARY);
		addPayment(aonContext, contract, startContract, null, "PAGA EXTRA", "100.00", "_P", "_P", PaymentType.CRA_0004, SalaryType.SALARY);
		addPayment(aonContext, contract, startContract, null, "ANTIGÜEDAD", "50.00", "_P", "_P", PaymentType.CRA_0001, SalaryType.SALARY);
		
		//@formatter:on
		
		addPrestITs(aonContext, contract);

		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH,10);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, null);
		
		Date brEndDate = add(getFirstDayOfMonth(startITDate), DAY_OF_MONTH,-1);
		Date brStartDate = getFirstDayOfMonth(brEndDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, brStartDate, brEndDate, brEndDate, contract);
		JooqSalaryBuilder<Salary> builder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(builder).calculate(ctx);
		builder.execute();
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>(); 

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments())
			System.out.println(payment.getName() + " = " + payment.getAmount() + ", " + payment.getQuote()
					+ " (" + payment.getExpression() + ")");

		Assert.assertEquals(1750.00, salary.getCommonBase(), DELTA);
		Assert.assertEquals(1750.00/30.00 * get(endDate, DAY_OF_MONTH), salary.getTotalPayment(), DELTA);

		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		calculator.setSalaryBuilder(new SalaryBuilder());
		salary = calculator.calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments())
			System.out.println(payment.getName() + " = " + payment.getAmount() + ", " + payment.getQuote()
					+ " (" + payment.getExpression() + ")");

		Assert.assertEquals(1750.00, salary.getCommonBase(), DELTA);
		Assert.assertEquals(1750.00/30.00 * get(endDate, DAY_OF_MONTH), salary.getTotalPayment(), DELTA);
	}


	protected PaymentConceptRecord addPrestITs(AONContext aonContext, ContractRecord contract) {
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
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 1.00 * %s_21",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		return prestIT;
	}
}
