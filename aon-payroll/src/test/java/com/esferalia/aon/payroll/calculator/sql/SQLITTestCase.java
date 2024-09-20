package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.AGREEMENT_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_MAX;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_MIN;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE_MIN;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.DIRECT_PAY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FRIDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.LEAVE_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MENSTRUATION_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.NATURAL_MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.OCCUPATIONAL_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PARTIAL_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PATERNITY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PATERNITY_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREGNANCY_39_WEEK_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREGNANCY_STOP_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_GROUP;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SATURDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SUNDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.THURSDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TUESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WEDNESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C200;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfYear;
import static java.lang.String.format;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.ContractLeaveRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseActivityRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseCccRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.ScopeRecord;
import com.esferalia.aon.jooq.tables.records.WorkplaceRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.payroll.IrpfOutcome;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryData;
import com.esferalia.aon.payroll.SalaryPayment;
//import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.CCCType;
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
import com.esferalia.aon.salary.expression.UndefinedVariablesException;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.util.AonDateUtils;

public class SQLITTestCase extends AbstractSQLTestCase {

	protected static final double DELTA = 0.009;
	
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
		assertEquals(1, results.size());
		assertEquals(startITDate, results.get(0).getValue());
		
		for ( int i = 1; i < 12 ; i++ ) {
			startDate = AonDateUtils.add(getFirstDayOfMonth(getToday()), MONTH,i);
			endDate = getLastDayOfMonth(startDate);
			ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			results = ctx.getExpressionContext().eval(ContextVariable.IT_START.getName(), startDate, endDate, java.util.Date.class);
			System.out.println(startDate + " : " + startITDate + ", " + results.get(0).getValue());
			assertEquals(1, results.size());
			assertEquals(startITDate, results.get(0).getValue());
		}
	}
	
	@Test
	public void testDuracionIT() throws ExpressionException, SQLException,
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
		
		List<ITimedResult<Integer>> results = ctx.getExpressionContext().eval(ContextVariable.IT_LENGTH.getName(), startDate, endDate, Integer.class);
		assertEquals(1, results.size());
		int itLenght = get(endDate, DAY_OF_MONTH) - get(startITDate, DAY_OF_MONTH) +1 ;
		assertEquals(itLenght, (int)results.get(0).getValue());
		
		try {
			ctx.getExpressionContext().eval(ContextVariable.IT_END.getName(), startDate, endDate, Date.class);
			fail(String.format("'%s' must not be defined ", ContextVariable.IT_END));
		} catch ( UndefinedVariablesException e ) {
		}

		for ( int i = 1; i < 12 ; i++ ) {
			startDate = AonDateUtils.add(getFirstDayOfMonth(getToday()), MONTH,i);
			endDate = getLastDayOfMonth(startDate);
			ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			results = ctx.getExpressionContext().eval(ContextVariable.IT_LENGTH.getName(), startDate, endDate, Integer.class);
			System.out.println(startDate + " : " + startITDate + ", " + results.get(0).getValue());
			assertEquals(1, results.size());
			itLenght += get(endDate, DAY_OF_MONTH);
			assertEquals(itLenght, (int)results.get(0).getValue());
		}
	}

	@Test
	public void testDuracionITI() throws ExpressionException, SQLException,
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
		Date endIt = add(startITDate, DAY_OF_MONTH, 33 );
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				endIt, null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		List<ITimedResult<Object>> results = ctx.getExpressionContext().eval(ContextVariable.IT_LENGTH.getName(), startDate, endDate);
		assertEquals(1, results.size());
		assertEquals(34, ((Number)results.get(0).getValue()).intValue());

		results = ctx.getExpressionContext().eval(ContextVariable.IT_END.getName(), startDate, endDate);
		assertEquals(1, results.size());
		assertEquals(endIt, (Date)results.get(0).getValue());
		
		startDate = AonDateUtils.add(getFirstDayOfMonth(getToday()), MONTH,1);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		results = ctx.getExpressionContext().eval(ContextVariable.IT_LENGTH.getName(), startDate, endDate);
		System.out.println(startDate + " : " + startITDate + ", " + results.get(0).getValue());
		assertEquals(1, results.size());
		assertEquals(34, ((Number)results.get(0).getValue()).intValue());

	}

	@Test
	public void testDuracionITII() throws ExpressionException, SQLException,
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
		Date endIt = add(startITDate, DAY_OF_MONTH, 10 );
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				endIt, null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		List<ITimedResult<Integer>> results = ctx.getExpressionContext().eval(ContextVariable.IT_LENGTH.getName(), startDate, endDate, Integer.class);
		assertEquals(1, results.size());
		assertEquals(11, (int)results.get(0).getValue());
		
		startITDate = add(endIt, DAY_OF_MONTH,5);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, null);

		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		results = ctx.getExpressionContext().eval(ContextVariable.IT_LENGTH.getName(), startDate, endDate, Integer.class);
		assertEquals(2, results.size());
		assertEquals(11, (int)results.get(0).getValue());
		int itLenght = get(endDate, DAY_OF_MONTH) - get(startITDate, DAY_OF_MONTH) +1 ;
		assertEquals(itLenght, (int)results.get(1).getValue());

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

		assertEquals(4, salary.getSalaryPayments().size());

	}

	@Test
	public void testDuracionITIV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext,
				add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH,-100),
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, null);
		//@formatter:on
		
		Date startITDate = add(add(getFirstDayOfMonth(getToday()), MONTH,-1), DAY_OF_MONTH,10);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, null);

		Date startDate = add(getFirstDayOfMonth(getToday()), MONTH,-1);
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		List<ITimedResult<Integer>> results = ctx.getExpressionContext().eval(ContextVariable.IT_LENGTH.getName(), startDate, endDate, Integer.class);
		assertEquals(1, results.size());
		int itLenght = get(getToday(), DAY_OF_MONTH) + (AonDateUtils.getMax(startITDate, DAY_OF_MONTH) - get(startITDate, DAY_OF_MONTH) + 1);
		assertEquals(itLenght, (int)results.get(0).getValue());
		
//		for ( int i = 1; i < 12 ; i++ ) {
//			startDate = AonDateUtils.add(getFirstDayOfMonth(getToday()), MONTH,i);
//			endDate = getLastDayOfMonth(startDate);
//			ctx = getContractSalaryCalculatorContext(
//					connection, startDate, endDate, endDate, contract);
//			results = ctx.getExpressionContext().eval(ContextVariable.IT_LENGTH.getName(), startDate, endDate, Integer.class);
//			System.out.println(startDate + " : " + startITDate + ", " + results.get(0).getValue());
//			assertEquals(1, results.size());
//			itLenght += get(endDate, DAY_OF_MONTH);
//			assertEquals(itLenght, (int)results.get(0).getValue());
//		}
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

		assertEquals(0.00, salary.getTotalPayment());
		assertEquals(0.00, salary.getTotalLiquid());
		assertEquals(get(endDate, DAY_OF_MONTH) * 100.00, salary.getCommonBase());

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

		assertEquals(0.00, salary.getTotalPayment());
		assertEquals(0.00, salary.getTotalLiquid());
		assertEquals(get(endDate, DAY_OF_MONTH) * 100.00, salary.getCommonBase());

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
//				"1000.00"
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
		
		addPayment(aonContext, contract, startDate,endDate, "1000.00");
		
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ISalaryCalculator<Salary, ISQLContractSalaryCalculatorContext> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		assertEquals(3500.00, salary.getCommonBase());

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

		assertEquals(0.00, salary.getTotalPayment());
		assertEquals(0.00, salary.getTotalLiquid());
		assertEquals(get(endDate, DAY_OF_MONTH) * 100.00, salary.getCommonBase());

	}

	@Test
	public void testPregnancyCostIT() throws ExpressionException, SQLException,
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
		
		
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT,  "BASE_REGULADORA * 0.60 * DIAS_INTERRUPCION_EMBARAZO_1_20",	"BASE_REGULADORA * DIAS_COTIZADOS" );
		addPayment(aonContext, contract, prestIT,  "BASE_REGULADORA * 0.75 * DIAS_INTERRUPCION_EMBARAZO_21",	"BASE_REGULADORA * DIAS_COTIZADOS" );
		
		Date startITDate = getToday();
		addIT(aonContext, contract, LeaveType.PREGNANCY_STOP, startITDate,
				null, null);

		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ISalaryCalculator<Salary, ISQLContractSalaryCalculatorContext> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);
		
		int monthDays = get(endDate, Calendar.DAY_OF_MONTH);
		int activeDays = get(startITDate, Calendar.DAY_OF_MONTH);
		int itDays = monthDays -activeDays;
		double br = 1750.00 / monthDays ;
		assertEquals( 1750.00 , salary.getCommonBase(), DELTA);
		assertEquals( 
			1750.00 * activeDays / monthDays 
			+ ( Math.min(20.00, itDays ) * br * 0.60 ) 
			+ ( Math.max(0.00, itDays - 20) * br * 0.75 ) 
			, salary.getTotalPayment(), DELTA);
		
		
		startDate = add(startDate, Calendar.MONTH,2);
		endDate = getLastDayOfMonth(startDate);
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		salary = calculator.calculate(ctx);
		
		assertEquals( 1750.00 , salary.getCommonBase(), DELTA);
		assertEquals( 1750.00 * 0.75 , salary.getTotalPayment(), DELTA);

	}

	@Test
	public void testPregnancy39WeekIT() throws ExpressionException, SQLException,
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
		
		
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT,  "BASE_REGULADORA * 0.60 * DIAS_SEMANA_39_EMBARAZO_1_20",	"BASE_REGULADORA * DIAS_COTIZADOS" );
		addPayment(aonContext, contract, prestIT,  "BASE_REGULADORA * 0.75 * DIAS_SEMANA_39_EMBARAZO_21",	"BASE_REGULADORA * DIAS_COTIZADOS" );
		
		Date startITDate = getToday();
		addIT(aonContext, contract, LeaveType.PREGNANCY_39_WEEK, startITDate,
				null, null);

		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ISalaryCalculator<Salary, ISQLContractSalaryCalculatorContext> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);
		
		int monthDays = get(endDate, Calendar.DAY_OF_MONTH);
		int activeDays = get(startITDate, Calendar.DAY_OF_MONTH);
		int itDays = monthDays -activeDays;
		double br = 1750.00 / monthDays ;
		assertEquals( 1750.00 , salary.getCommonBase(), DELTA);
		assertEquals( 
			1750.00 * activeDays / monthDays 
			+ ( Math.min(20.00, itDays ) * br * 0.60 ) 
			+ ( Math.max(0.00, itDays - 20) * br * 0.75 ) 
			, salary.getTotalPayment(), DELTA);
		
		
		startDate = add(startDate, Calendar.MONTH,2);
		endDate = getLastDayOfMonth(startDate);
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		salary = calculator.calculate(ctx);
		
		assertEquals( 1750.00 , salary.getCommonBase(), DELTA);
		assertEquals( 1750.00 * 0.75 , salary.getTotalPayment(), DELTA);

	}

	@Test
	public void testMenstruationIT() throws ExpressionException, SQLException,
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
		
		
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT,  "BASE_REGULADORA * 0.60 * DIAS_MENSTRUACION_1_20",	"BASE_REGULADORA * DIAS_COTIZADOS" );
		addPayment(aonContext, contract, prestIT,  "BASE_REGULADORA * 0.75 * DIAS_MENSTRUACION_21",	"BASE_REGULADORA * DIAS_COTIZADOS" );
		
		Date startITDate = getToday();
		addIT(aonContext, contract, LeaveType.MENSTRUATION, startITDate,
				null, null);

		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ISalaryCalculator<Salary, ISQLContractSalaryCalculatorContext> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);
		
		int monthDays = get(endDate, Calendar.DAY_OF_MONTH);
		int activeDays = get(startITDate, Calendar.DAY_OF_MONTH) -1 ;
		int itDays = monthDays -activeDays;
		double br = 1750.00 / monthDays ;
		assertEquals( 1750.00 , salary.getCommonBase(), DELTA);
		assertEquals( 
			1750.00 * activeDays / monthDays 
			+ ( Math.min(20.00, itDays ) * br * 0.60 ) 
			+ ( Math.max(0.00, itDays - 20) * br * 0.75 ) 
			, salary.getTotalPayment(), DELTA);
		
		
		startDate = add(startDate, Calendar.MONTH,2);
		endDate = getLastDayOfMonth(startDate);
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		salary = calculator.calculate(ctx);
		
		assertEquals( 1750.00 , salary.getCommonBase(), DELTA);
		assertEquals( 1750.00 * 0.75 , salary.getTotalPayment(), DELTA);

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

		assertEquals(4 /*payments*/ * 2 /*tramos activo*/, salary.getSalaryPayments().size());

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

		assertEquals(2, salary.getSalaryPayments().size());

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

		assertEquals(2, salary.getSalaryPayments().size());

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
		

		assertEquals( salary.getCommonBase() * 4.70d/100, salary.getTotalDeduction(), DELTA);

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
		

		assertEquals( salary.getCommonBase() * 4.70d/100, salary.getTotalDeduction(), DELTA);

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
		

		assertEquals( salary.getCommonBase() * 4.70d/100, salary.getTotalDeduction(), DELTA);

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
		

		assertEquals( salary.getCommonBase() * 4.70d/100, salary.getTotalDeduction(), DELTA);

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
		

		assertEquals( salary.getProfessionalBase() * 1.65d/100, salary.getTotalDeduction(), DELTA);

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
		.peek(data->assertEquals(1750.00 * 4/30.00, Double.parseDouble(data.getExpression())))
		.count();
		
		assertEquals(1, count);
		
		// + 3
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == startITDate.getDate())
		.peek(data->assertEquals(100.00 * 3.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);
		
		// + 2  (Here ADJUST)
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 8)
		.peek(data->assertEquals(data.getEndDate().getDate(),10))
		.peek(data->assertEquals(100.00 * 2.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);
		
		// + 21
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 11)
		.peek(data->assertEquals(1750.00 * 21/30.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);
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
		.peek(data->assertEquals(1750.00 * 4/31.00, Double.parseDouble(data.getExpression())))
		.count();
		
		assertEquals(1, count);
		
		// 3
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == startITDate.getDate())
		.peek(data->assertEquals(100.00 * 3.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);
		
		// 3
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 8)
		.peek(data->assertEquals(100.00 * 3.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);
		
		// 21
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 11)
		.peek(data->assertEquals(1750.00 * 21/31.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);
		
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
		.peek(data->assertEquals(1750.00 * 4/30.00, Double.parseDouble(data.getExpression())))
		.count();
		
		assertEquals(1, count);
		
		// + 3
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == startITDate.getDate())
		.peek(data->assertEquals(100.00 * 3.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);
		
		// + 12
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 8)
		.peek(data->assertEquals(data.getEndDate().getDate(),19))
		.peek(data->assertEquals(100.00 * 12.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);
		
		// + 5
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 20)
		.peek(data->assertEquals(data.getEndDate().getDate(),24))
		.peek(data->assertEquals(100.00 * 5.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);
		
		// + 6 ( ADJUST )
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 25)
		.peek(data->assertEquals(data.getEndDate().getDate(),31))
		.peek(data->assertEquals(100.00 * 6.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);
		
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
		.peek(data->assertEquals(100.00 * 30.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);

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
		.peek(data->assertEquals(100.00 * 30.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);
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
		.peek(data->assertEquals(1750.00 * 4/30.00 * 0.5, Double.parseDouble(data.getExpression())))
		.count();
		
		assertEquals(1, count);
		
		// + 3
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == startITDate.getDate())
		.peek(data->assertEquals(100.00 * 3.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);
		
		// + 12
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 8)
		.peek(data->assertEquals(data.getEndDate().getDate(),19))
		.peek(data->assertEquals(100.00 * 12.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);
		
		// + 5
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 20)
		.peek(data->assertEquals(data.getEndDate().getDate(),24))
		.peek(data->assertEquals(100.00 * 5.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);
		
		// + 7 ( NOT ADJUST PARTIAL)
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 25)
		.peek(data->assertEquals(data.getEndDate().getDate(),31))
		.peek(data->assertEquals(100.00 * 7.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);
		
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
		.peek(data->assertEquals(100.00 * 30.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);

		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		salary = calculator.calculate(ctx);
		
		// One period , 31 ( NO adjust partial ) 
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.peek(data->assertEquals(100.00 * 31.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);
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
		.peek(data->assertEquals(1750.00 * 4/31.00, Double.parseDouble(data.getExpression())))
		.count();
		
		assertEquals(1, count);
		
		// + 3
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == startITDate.getDate())
		.peek(data->assertEquals(100.00 * 3.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);
		
		// + 3  (No ADJUST)
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 8)
		.peek(data->assertEquals(data.getEndDate().getDate(),10))
		.peek(data->assertEquals(100.00 * 3.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);
		
		// + 21
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 11)
		.peek(data->assertEquals(1750.00 * 21/31.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);
		//  = 30
		
		
	}

	@Test
	public void testCommonDiseaseITQuoteDaysVI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date contractStartDate = add(getFirstDayOfYear(getToday()), Calendar.YEAR,-1);
		Date contractEndDate = add(getLastDayOfMonth(getFirstDayOfYear(getToday())), Calendar.DAY_OF_MONTH,-1);
		
		cleanSystemData(aonContext);
		
		addSystemData(aonContext, 
				contractStartDate, 
				null, 
				new HashMap<String, String>(){
			{
				put(MONTH_DAYS.getName(), 
						String.format("[ \"01\": %s ][%s]", "30", QUOTE_GROUP.getName() ) );
			}
		});
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext,
				contractStartDate,
				contractEndDate,
				new HashMap<String, String>(){
					{
						put(QUOTE_GROUP.getName(), "'01'" );
					}
				},
				new String[] {}, 
				new String[] {
				"TRACE('BASE_REGULADORA=%f\r\n', BASE_REGULADORA);0.00",
				"TRACE('BASE_CGP=%f\r\n', BASE_CGP);BASE_CGC * 1.55 / 100",
				"TRACE('BASE_CGP=%f\r\n', BASE_CGP);BASE_CGC * 0.10 / 100",
				"TRACE('DIAS_COTIZADOS=%f\r\n', DIAS_COTIZADOS);0.00",
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


		Date startITDate = add(contractEndDate, Calendar.DAY_OF_MONTH, -100 );
		
		Date endITDate = contractEndDate;		
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				endITDate, 100.00);

		Date startDate = getFirstDayOfMonth(contractEndDate);
		Date endDate = contractEndDate;
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		
		salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.forEach(data->System.out.println(data.getName() + " = " + data.getExpression() + "(" + data.getStartDate() + "..." + data.getEndDate() + ")"));
		;

		// (No ADJUST)
		long count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 1)
		.peek(data->assertEquals(data.getEndDate().getDate(),30))
		.peek(data->assertEquals(100.00 * 30.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);
		
	}
 
	@Test
	public void testCommonDiseaseITQuoteDaysVII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date contractStartDate = add(getFirstDayOfYear(getToday()), Calendar.YEAR,-1);
		Date contractEndDate = add(getLastDayOfMonth(getFirstDayOfYear(getToday())), Calendar.DAY_OF_MONTH,-1);
		
		cleanSystemData(aonContext);
		
		addSystemData(aonContext, 
				contractStartDate, 
				null, 
				new HashMap<String, String>(){
			{
				put(MONTH_DAYS.getName(), 
						String.format("[ \"01\": %s ][%s]", "30", QUOTE_GROUP.getName() ) );
			}
		});
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext,
				contractStartDate,
				contractEndDate,
				new HashMap<String, String>(){
					{
						put(QUOTE_GROUP.getName(), "'01'" );
					}
				},
				new String[] {}, 
				new String[] {
				"TRACE('BASE_REGULADORA=%f\r\n', BASE_REGULADORA);0.00",
				"TRACE('BASE_CGP=%f\r\n', BASE_CGP);BASE_CGC * 1.55 / 100",
				"TRACE('BASE_CGP=%f\r\n', BASE_CGP);BASE_CGC * 0.10 / 100",
				"TRACE('DIAS_COTIZADOS=%f\r\n', DIAS_COTIZADOS);0.00",
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


		Date startITDate = add(contractEndDate, Calendar.DAY_OF_MONTH, -100 );
		
		Date endITDate = contractEndDate;		
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				endITDate, 100.00);

		Date startDate = getFirstDayOfMonth(contractEndDate);
		Date endDate = getLastDayOfMonth(contractEndDate);
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		
		salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.forEach(data->System.out.println(data.getName() + " = " + data.getExpression() + "(" + data.getStartDate() + "..." + data.getEndDate() + ")"));
		;

		// (No ADJUST)
		long count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 1)
		.peek(data->assertEquals(data.getEndDate().getDate(),30))
		.peek(data->assertEquals(100.00 * 30.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);
		
	}

	@Test
	public void testCommonDiseaseITQuoteDaysVIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date contractStartDate = add(getFirstDayOfYear(getToday()), Calendar.YEAR,-1);
		Date contractEndDate = add(getLastDayOfMonth(getFirstDayOfYear(getToday())), Calendar.DAY_OF_MONTH,-6);
		
		cleanSystemData(aonContext);
		
		addSystemData(aonContext, 
				contractStartDate, 
				null, 
				new HashMap<String, String>(){
			{
				put(MONTH_DAYS.getName(), 
						String.format("[ \"01\": %s ][%s]", "30", QUOTE_GROUP.getName() ) );
			}
		});
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext,
				contractStartDate,
				contractEndDate,
				new HashMap<String, String>(){
					{
						put(QUOTE_GROUP.getName(), "'01'" );
					}
				},
				new String[] {}, 
				new String[] {
				"TRACE('BASE_REGULADORA=%f\r\n', BASE_REGULADORA);0.00",
				"TRACE('BASE_CGP=%f\r\n', BASE_CGP);BASE_CGC * 1.55 / 100",
				"TRACE('BASE_CGP=%f\r\n', BASE_CGP);BASE_CGC * 0.10 / 100",
				"TRACE('DIAS_COTIZADOS=%f\r\n', DIAS_COTIZADOS);0.00",
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


		Date startITDate = add(contractEndDate, Calendar.DAY_OF_MONTH, -100 );
		
		Date endITDate = contractEndDate;		
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				endITDate, 100.00);

		Date startDate = getFirstDayOfMonth(contractEndDate);
		Date endDate = getLastDayOfMonth(contractEndDate);
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		
		salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.forEach(data->System.out.println(data.getName() + " = " + data.getExpression() + "(" + data.getStartDate() + "..." + data.getEndDate() + ")"));
		;

		// (No ADJUST)
		long count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 1)
		.peek(data->assertEquals(data.getEndDate().getDate(),25))
		.peek(data->assertEquals(100.00 * 25.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);
		
	}

	@Test
	public void testCommonDiseaseITQuoteDaysIX() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date contractStartDate = getFirstDayOfYear(getToday());
		
		cleanSystemData(aonContext);
		
		addSystemData(aonContext, 
				contractStartDate, 
				null, 
				new HashMap<String, String>(){
			{
				put(MONTH_DAYS.getName(), 
					String.format("[ \"01\": %s ][%s]", "30", QUOTE_GROUP.getName() ) );
			}
		});
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext,
				contractStartDate,
				null,
				new HashMap<String, String>(){
					{
						put(QUOTE_GROUP.getName(), "'01'" );
						put(TC2.getName(), format("\"%s\"",
							C200.getValue()));
        					put(MONDAY_HOURS.getName(), format("%d", 4));
        					put(TUESDAY_HOURS.getName(), format("%d", 4));
        					put(WEDNESDAY_HOURS.getName(), format("%d", 4));
        					put(THURSDAY_HOURS.getName(), format("%d", 4));
        					put(FRIDAY_HOURS.getName(), format("%d", 4));
					}
				},
				new String[] {}, 
				new String[] {
				"TRACE('BASE_REGULADORA=%f\r\n', BASE_REGULADORA);0.00",
				"TRACE('BASE_CGP=%f\r\n', BASE_CGP);BASE_CGC * 1.55 / 100",
				"TRACE('BASE_CGP=%f\r\n', BASE_CGP);BASE_CGC * 0.10 / 100",
				"TRACE('DIAS_COTIZADOS=%f\r\n', DIAS_COTIZADOS);0.00",
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


		Date startITDate = add(contractStartDate, DAY_OF_MONTH, 4);
		Date endITDate = add(startITDate, DAY_OF_MONTH, 9);
		
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,endITDate, 100.00);

		Date startDate = getFirstDayOfMonth(startITDate);
		Date endDate = getLastDayOfMonth(startDate);
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		
		salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.forEach(data->System.out.println(data.getName() + " = " + data.getExpression() + "(" + data.getStartDate() + "..." + data.getEndDate() + ")"));
		;

		// (No ADJUST)
		double quotaDays = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.QUOTE_DAYS.getName()))
		.map( SalaryData::getExpression )
		.peek(e -> System.out.println("DIAS_COTIZADOS = "+ e))
		.collect(Collectors.summingDouble(Double::parseDouble));
		assertEquals(31, quotaDays, 0.00);
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
		.peek(data->assertEquals(1750.00 * 4/30.00, Double.parseDouble(data.getExpression())))
		.count();
		
		assertEquals(1, count);
		
		// + 5 ( Here ADJUST )
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == startITDate.getDate())
		.peek(data->assertEquals(100.00 * 5.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);		
		
		// + 21
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 11)
		.peek(data->assertEquals(1750.00 * 21/30.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);
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
		.peek(data->assertEquals(1750.00 * 4/30.00, Double.parseDouble(data.getExpression())))
		.count();
		
		assertEquals(1, count);
		
		// + 26 ( Here ADJUST )
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == startITDate.getDate())
		.peek(data->assertEquals(100.00 * 26.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);		
		
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
		.peek(data->assertEquals(100.00 * 30.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);			startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		salary = calculator.calculate(ctx);
		
		// One period , 30 ( YES adjust ) 
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.peek(data->assertEquals(100.00 * 30.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);
		
	}
	
	@Test
	public void testCommonOccupationalDiseaseITQuoteDaysI() throws ExpressionException, SQLException,
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
		calendar.set(Calendar.DAY_OF_MONTH, 4 );
		Date startITDate = new Date(calendar.getTimeInMillis());
		
		calendar.set(Calendar.DAY_OF_MONTH, 10 );
		Date endITDate = new Date(calendar.getTimeInMillis());		
		
		addIT(aonContext, contract, LeaveType.COMMON_OCCUPATIONAL_DISEASE, startITDate,
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
		.peek(data->assertEquals(1750.00 * 4/30.00, Double.parseDouble(data.getExpression())))
		.count();
		
		assertEquals(1, count);
		
		// + 5 ( Here ADJUST )
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == add(startITDate,DAY_OF_MONTH,1).getDate())
		.peek(data->assertEquals(100.00 * 5.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);		
		
		// + 21
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 11)
		.peek(data->assertEquals(1750.00 * 21/30.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);
		//  = 30
		
		
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
		.peek(data->assertEquals(1750.00 * 4/30.00, Double.parseDouble(data.getExpression())))
		.count();
		
		assertEquals(1, count);
		
		// + 26 ( Here ADJUST )
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == startITDate.getDate())
		.peek(data->assertEquals(100.00 * 26.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);		
		
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
			.peek(data->assertEquals(100.00 * 30.00, Double.parseDouble(data.getExpression())))
			.count();
			assertEquals(1, count);			
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
		.peek(data->assertEquals(100.00 * 9.00, Double.parseDouble(data.getExpression())))
		.count();
		
		assertEquals(1, count);
		
		// + 21
		count = salary.getSalaryDatas().stream()
		.filter(data->data.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(data->data.getStartDate().getDate() == 11)
		.peek(data->assertEquals(1750.00 * 21/30.00, Double.parseDouble(data.getExpression())))
		.count();
		assertEquals(1, count);
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

		assertEquals(get(_365Date, DAY_OF_MONTH)* 100.00 * 0.75,salary.getTotalPayment() );
		assertEquals(30.00 * 100.00, salary.getCommonBase() );
		
		assertEquals( get(_365Date, DAY_OF_MONTH)* 100.00 * 1.65 / 100.00 , 
				salary.getSocialSecurityContributions(), DELTA);

		assertEquals(
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
				String.format("BASE_REGULADORA * %s",  QUOTE_DAYS),
				PaymentType.CRA_0001
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

		assertEquals(get(_365Date, DAY_OF_MONTH)* 100.00 * 0.75,salary.getTotalPayment() );
		assertEquals(30.00 * 100.00, salary.getCommonBase() );
		
		assertEquals( get(_365Date, DAY_OF_MONTH)* 100.00 * 1.65 / 100.00 , 
				salary.getSocialSecurityContributions(), DELTA);

		assertEquals(
				(30.00 * 100.00* 23.60 / 100.00)+ 
				(-1)*get(_365Date, DAY_OF_MONTH)* 100.00 * 0.75
				
				, salary.getTotalEnterprise()
				, DELTA );

		startDate = add(startDate, Calendar.MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		salary = calculator.calculate(ctx);
		
		cleanSystemCosts(aonContext);

		assertEquals(0.00,salary.getTotalPayment() );
		assertEquals(30.00 * 100.00 , salary.getCommonBase() );
		assertEquals((30.00 * 100.00* 23.60 / 100.00)
				, salary.getTotalEnterprise()
				, DELTA );
		
		assertEquals( 0.00, 
				salary.getSocialSecurityContributions(), DELTA);

	}

	@Test
	public void testCommonDiseaseIT365RedefinedV() throws ExpressionException, SQLException,
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
				String.format("BASE_REGULADORA * %s",  QUOTE_DAYS),
				PaymentType.CRA_0001
				);
		//@formatter:on

		Date startITDate = add(getFirstDayOfMonth(getToday()), Calendar.DAY_OF_MONTH , 9);
		
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate, null, 100.00);
		
		Date _365Date = AonDateUtils.add(startITDate, Calendar.DAY_OF_MONTH,94);
		
		Date _366Date = AonDateUtils.add(startITDate, Calendar.DAY_OF_MONTH,95);
		addData(aonContext, contract, startITDate, null, ContextVariable.DIRECT_PAY_START, 
				String.format("%s(%d,%d,%d)",ContextVariable.DATE,get(_366Date, YEAR), get(_366Date, MONTH)+1, get(_366Date, DAY_OF_MONTH) ));

		Date startDate = getFirstDayOfMonth(_365Date);
		Date endDate = getLastDayOfMonth(startDate);
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		assertEquals(get(_365Date, DAY_OF_MONTH)* 100.00 * 0.75,salary.getTotalPayment() );
		assertEquals(30.00 * 100.00, salary.getCommonBase() );
		
		assertEquals( get(_365Date, DAY_OF_MONTH)* 100.00 * 1.65 / 100.00 , 
				salary.getSocialSecurityContributions(), DELTA);

		assertEquals(
				(30.00 * 100.00* 23.60 / 100.00)+ 
				(-1)*get(_365Date, DAY_OF_MONTH)* 100.00 * 0.75
				
				, salary.getTotalEnterprise()
				, DELTA );

		startDate = add(startDate, Calendar.MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		salary = calculator.calculate(ctx);
		
		cleanSystemCosts(aonContext);

		assertEquals(0.00,salary.getTotalPayment() );
		assertEquals(30.00 * 100.00 , salary.getCommonBase() );
		assertEquals((30.00 * 100.00* 23.60 / 100.00)
				, salary.getTotalEnterprise()
				, DELTA );
		
		assertEquals( 0.00, 
				salary.getSocialSecurityContributions(), DELTA);

	}

	@Test
	public void testProfessionalDiseaseIT365I() throws ExpressionException, SQLException,
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

		assertEquals(get(_365Date, DAY_OF_MONTH)* 100.00 * 0.75,salary.getTotalPayment() );
		assertEquals( 30 * 100.00 , salary.getCommonBase() );
		
		assertEquals( get(_365Date, DAY_OF_MONTH)* 100.00 * 1.65 / 100.00 , 
				salary.getSocialSecurityContributions(), DELTA);

		assertEquals((30.00* 100.00 * 23.60/100.00)+(-1)*get(_365Date, DAY_OF_MONTH)* 100.00 * 0.75, salary.getTotalEnterprise() );

		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);

		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		calculator.setSalaryBuilder(new SalaryBuilder());
		salary = calculator.calculate(ctx);

		cleanSystemCosts(aonContext);

		assertEquals(0.00,salary.getTotalPayment() );
		assertEquals(30 * 100.00, salary.getCommonBase() );
		
		assertEquals( 0.00 , salary.getSocialSecurityContributions(), DELTA);

		assertEquals((30.00 * 100.00* 23.60 / 100.00)
				, salary.getTotalEnterprise()
				, DELTA );
	}
	
	@Test
	public void testProfessionalDiseaseIT365II() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemCosts(aonContext);
		
		
		//@formatter:offhttp://www.marca.com/motor/formula1/2016/01/08/5690143c268e3e041d8b457d.html?cid=GEN35403
		ContractRecord contract = newContract(aonContext,
				add( getFirstDayOfYear(getToday()), Calendar.YEAR, -2 ),
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

		Date startITDate = add(getFirstDayOfMonth(getToday()), Calendar.MONTH , -9);
		
		addIT(aonContext, contract, LeaveType.OCCUPATIONAL_DISEASE, startITDate, null, 100.00);
		
		Date _366Date = add(getFirstDayOfMonth(getToday()), Calendar.DAY_OF_MONTH , 9);
		Date startDate = getFirstDayOfMonth(_366Date);
		Date endDate = getLastDayOfMonth(startDate);
		
		addData(aonContext, contract, startITDate, null, ContextVariable.DIRECT_PAY_START, 
				String.format("%s(%d,%d,%d)",ContextVariable.DATE,get(_366Date, YEAR), get(_366Date, MONTH)+1, get(_366Date, DAY_OF_MONTH) ));
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		cleanSystemCosts(aonContext);
		Date _365Date = add(_366Date, Calendar.DAY_OF_MONTH , -1);
		assertEquals(get(_365Date, DAY_OF_MONTH)* 100.00 * 0.75,salary.getTotalPayment() );
		assertEquals(30 * 100.00, salary.getCommonBase() );
		

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

		assertEquals(get(_365Date, DAY_OF_MONTH) * 100.00 * 0.75,salary.getTotalPayment() );
		assertEquals(30 *100.00 , salary.getCommonBase() );
		
		assertEquals( get(_365Date, DAY_OF_MONTH)* 100.00 * 1.65 / 100.00 , 
				salary.getSocialSecurityContributions(), DELTA);

		assertEquals((30.00*100.00 * 23.60/100.00)+(-1)*get(_365Date, DAY_OF_MONTH)* 100.00 * 0.75, salary.getTotalEnterprise() );

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
		
		PaymentConceptRecord prestIT = addConcept(aonContext, "PREST_IT");

		// @formatter:off
		addSSRegimePayment(aonContext 
				,SSRegimeType.GENERAL 
				,getFirstDayOfYear(getToday()) 
				,prestIT
				,PaymentType.CRA_0001
				,"0.00"
				,String.format("BASE_REGULADORA * 1.00 * %s_1_3",  COMMON_DISEASE_DAYS)
				,"_P"
				);
		addSSRegimePayment(aonContext 
				,SSRegimeType.GENERAL 
				,getFirstDayOfYear(getToday()) 
				,prestIT
				,PaymentType.CRA_0001
				,String.format("BASE_REGULADORA * 0.60 * %s_4_15",  COMMON_DISEASE_DAYS)
				,String.format("BASE_REGULADORA * 1.00 * %s_4_15",  COMMON_DISEASE_DAYS)
				,"_P"
				);
		addSSRegimePayment(aonContext 
				,SSRegimeType.GENERAL 
				,getFirstDayOfYear(getToday()) 
				,prestIT
				,PaymentType.CRA_0001
				,String.format("BASE_REGULADORA * 0.60 * %s_16_20",  COMMON_DISEASE_DAYS)
				,String.format("BASE_REGULADORA * 1.00 * %s_16_20",  COMMON_DISEASE_DAYS)
				,"_P"
				);
		addSSRegimePayment(aonContext 
				,SSRegimeType.GENERAL 
				,getFirstDayOfYear(getToday()) 
				,prestIT
				,PaymentType.CRA_0001
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
		
		assertEquals(1310.40 + ( 1310.40 / 6 ), salary.getCommonBase(), DELTA);
		

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
						put("BASE_CGC_MIN","1000.00 * DIAS_NOMINA / DIAS_MES");						
					}
				});

		// @formatter:off
		
		PaymentConceptRecord prestIT = addConcept(aonContext, ContextVariable.PREST_IT);
		
		
		addSSRegimePayment(aonContext 
				,SSRegimeType.GENERAL 
				,getFirstDayOfYear(getToday())
				,prestIT
				,PaymentType.CRA_0001
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
		
		int monthDays = get(endDate, DAY_OF_MONTH);
		
		assertEquals( 1000.00, salary.getCommonBase(), DELTA );
		
		for ( com.esferalia.aon.payroll.SalaryData d: salary.getSalaryDatas()) {
			if ( d.getName().equals("BASE_CGC") ) {
				if ( d.getStartDate().equals(startIt) ) 
					assertEquals(1000.00 / monthDays, Double.parseDouble(d.getExpression()), DELTA);
				else if ( d.getStartDate().equals(startDate) ) 
					assertEquals(1000.00 / monthDays * ( monthDays -1 ), Double.parseDouble(d.getExpression()), DELTA); // TODO : DELTA????
				else 
					fail("Unexpected BASE_CGC");
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
						put("BASE_CGC_MIN","1000.00 * DIAS_NOMINA / DIAS_MES");						
					}
				});

		// @formatter:off
		
		PaymentConceptRecord prestIT = addConcept(aonContext, ContextVariable.PREST_IT);
		
		
		addSSRegimePayment(aonContext 
				,SSRegimeType.GENERAL 
				,getFirstDayOfYear(getToday())
				,prestIT
				,PaymentType.CRA_0001
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
		
		assertEquals( 1000.00, salary.getCommonBase(), DELTA );
		
		for ( com.esferalia.aon.payroll.SalaryData d: salary.getSalaryDatas()) {
			if ( d.getName().equals("BASE_CGC") ) {
				if ( d.getStartDate().equals(startIt) ) 
					assertEquals(1000.00/30.00, Double.parseDouble(d.getExpression()), DELTA);
				else if ( d.getStartDate().equals(startDate) ) 
					assertEquals(1000.00 * get(d.getEndDate(),  DAY_OF_MONTH) / 30.00, Double.parseDouble(d.getExpression()), DELTA); // TODO : DELTA????
				else 
					fail("Unexpected BASE_CGC");
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
						put("BASE_CGC_MIN","1000.00 * DIAS_NOMINA/DIAS_MES");						
					}
				});

		// @formatter:off
		
		PaymentConceptRecord prestIT = addConcept(aonContext, ContextVariable.PREST_IT);
		
		
		addSSRegimePayment(aonContext 
				,SSRegimeType.GENERAL 
				,getFirstDayOfYear(getToday())
				,prestIT
				,PaymentType.CRA_0001
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
		
		assertEquals( 1000.00, salary.getCommonBase(), DELTA );
		
		for (SalaryPayment payment : salary.getSalaryPayments()) {
			System.out.println(payment.getDescription() + " = " + payment.getAmount() + " (" + payment.getQuote() + ")");
		}

		for ( com.esferalia.aon.payroll.SalaryData d: salary.getSalaryDatas()) {
			if ( d.getName().equals("BASE_CGC") ) {
				if ( d.getStartDate().equals(startIt) ) 
					assertEquals(0.00, Double.parseDouble(d.getExpression()), DELTA);
				else if ( d.getStartDate().equals(startDate) ) 
					assertEquals(1000.00, Double.parseDouble(d.getExpression()), DELTA); // TODO : DELTA????
				else 
					fail("Unexpected BASE_CGC");
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
						put("BASE_CGC_MIN","1000.00 * DIAS_NOMINA / DIAS_MES");						
					}
				});

		// @formatter:off
		
		PaymentConceptRecord prestIT = addConcept(aonContext, ContextVariable.PREST_IT);
		
		
		addSSRegimePayment(aonContext 
				,SSRegimeType.GENERAL 
				,getFirstDayOfYear(getToday())
				,prestIT
				,PaymentType.CRA_0001
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
		
		int monthDays = get(endDate, DAY_OF_MONTH);
		
		assertEquals( 1000.00, salary.getCommonBase(), DELTA );
		
		for ( com.esferalia.aon.payroll.SalaryData d: salary.getSalaryDatas()) {
			if ( d.getName().equals("BASE_CGC") ) {
				if ( d.getStartDate().equals(startIt) ) 
					assertEquals(1000.00 / monthDays * 2.00, Double.parseDouble(d.getExpression()), DELTA);
				else if ( d.getStartDate().equals(startDate) ) 
					assertEquals(1000.00 / monthDays * ( monthDays - 2.00), Double.parseDouble(d.getExpression()), DELTA); // TODO : DELTA????
				else 
					fail("Unexpected BASE_CGC");
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
						put("BASE_CGC_MIN","1000.00 * DIAS_NOMINA / DIAS_MES");						
					}
				});

		// @formatter:off
		
		PaymentConceptRecord prestIT = addConcept(aonContext, ContextVariable.PREST_IT);
		
		
		addSSRegimePayment(aonContext 
				,SSRegimeType.GENERAL 
				,getFirstDayOfYear(getToday())
				,prestIT
				,PaymentType.CRA_0001
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
		
		int monthDays = get(endDate, DAY_OF_MONTH);
		
		assertEquals( 1000.00, salary.getCommonBase(), DELTA );
		
		for ( com.esferalia.aon.payroll.SalaryData d: salary.getSalaryDatas()) {
			if ( d.getName().equals("BASE_CGC") ) {
				if ( d.getStartDate().equals(startIt) ) 
					assertEquals(1000.00 / monthDays* 2.00, Double.parseDouble(d.getExpression()), DELTA);
				else if ( d.getStartDate().equals(startDate) ) 
					assertEquals(1000.00 / monthDays * ( monthDays -2 ), Double.parseDouble(d.getExpression()), DELTA); // TODO : DELTA????
				else 
					fail("Unexpected BASE_CGC");
			}
		}
		

	}

	@Test
	public void testBaseMinITVI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		PaymentConceptRecord pay = addConcept(aonContext, "PAGA");
		PaymentConceptRecord salarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord antiguedad = addConcept(aonContext, "ANTIGUEDAD");

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				},
				new Payment [] {
						new Payment() {
							{
								this.concept = salarioBase.getId();
								this.expression = "1000.00 * DIAS_TRABAJADOS / DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = antiguedad.getId();
								this.expression = "SALARIO_BASE * 0.10";
							}
						},
						new Payment() {
							{
								this.concept = pay.getId();
								this.expression = "SALARIO_BASE / 12 ";
							}
						},
						new Payment() {
							{
								this.concept = pay.getId();
								this.expression = "SALARIO_BASE / 12 ";
							}
						}
				});


		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
						put("DIAS_MES","30.00");						
						put("BASE_CGC_MIN","1000.00 * DIAS_NOMINA / DIAS_MES");						
					}
				},
				new String[] {
				}, 
				new String[] {
				}, category);
		
		addPrestITs(aonContext, contract);

		//@formatter:on
		
		//addPrestITs(aonContext, contract);
		
		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH,28);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, null );

		Date startDate = add(getFirstDayOfMonth(getToday()), Calendar.MONTH, 1 );
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}

		
		assertEquals(1100.00 + ( 1000.00/6), salary.getCommonBase(), DELTA);
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
				}, 
				new String[] {
				}, null);
		//@formatter:on

		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH,10);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				startITDate, null);
		
		addPayment(aonContext, contract, getFirstDayOfMonth(startITDate), getLastDayOfMonth(startITDate), "666.00");
		
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

		fail();

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
				assertEquals(ExpressionScope.CONTRACT,((IExpressionVariable<?>) factor).getExpression().getScope());
			}
		});
		Salary salary = calculator.calculate(ctx);
		
		assertEquals(0.00, salary.getTotalPayment());
		assertEquals(0.00, salary.getTotalLiquid());
		assertEquals(get(endDate, DAY_OF_MONTH) * 100.00, salary.getCommonBase(), DELTA);
		
		
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
					assertTrue(context.containsKey(ContextVariable.PATERNITY_FACTOR.getName()));
			}
			
		});
		salary = calculator.calculate(ctx);

		int monthDays = get(endDate, Calendar.DATE) ;
		int workedDays = get(startITDate, Calendar.DATE) -1;
		assertEquals(1750.00 * workedDays / monthDays, salary.getTotalPayment(), DELTA);
		assertEquals(1750.00 * workedDays / monthDays + (monthDays - workedDays ) * 100.00, salary.getCommonBase(), DELTA);
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
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				"HORAS_TRABAJADAS * 0.00"}, 
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

		assertEquals(1750.00 * 1/2, salary.getTotalPayment(), DELTA);
		assertEquals(get(endDate, DAY_OF_MONTH) * 100.00 * 0.50 + 1750.00 * 1/2 , salary.getCommonBase(), DELTA);
		
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
					assertEquals(1, datas.size());
					assertEquals(startDate, datas.get(0).getStartDate());
					assertEquals(endDate, datas.get(0).getEndDate());
					assertEquals(1750.00 * 0.5,
							Double.parseDouble(datas.get(0).getExpression()));

					// 635 o 634 Base de Accidentes de Trabajo.
					datas = s.getContextData().get(CGP_BASE.getName());
					assertEquals(1, datas.size());
					assertEquals(startDate, datas.get(0).getStartDate());
					assertEquals(endDate, datas.get(0).getEndDate());
					assertEquals(1750.00 * 0.5,
							Double.parseDouble(datas.get(0).getExpression()), DELTA);

					datas = s.getContextData()
							.get(MATERNITY_BASE.getName());
					assertEquals(1, datas.size());
					assertEquals(startDate, datas.get(0).getStartDate());
					assertEquals(endDate, datas.get(0).getEndDate());
					assertEquals(100.00 * get(endDate, DAY_OF_MONTH) * 0.5,
							Double.parseDouble(datas.get(0).getExpression()), DELTA);

					datas = s.getContextData()
							.get(WORKED_HOURS.getName());
					assertEquals(1, datas.size());
					assertEquals(startDate, datas.get(0).getStartDate());
					assertEquals(endDate, datas.get(0).getEndDate());
					long workDays = new Period(startDate, endDate).daysStream()
					.filter(day -> day.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
					.filter(day -> day.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY)
					.count();
					assertEquals(workDays * 8 * 0.5,
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
					assertEquals(2, datas.size());
					
					assertEquals(start, datas.get(0).getStartDate());
					assertEquals(add(startITDate, DAY_OF_MONTH, -1), datas.get(0).getEndDate());
					assertEquals(1750.00 * workDays / monthDays,
							Double.parseDouble(datas.get(0).getExpression()), DELTA);

					assertEquals(startITDate, datas.get(1).getStartDate());
					assertEquals(end, datas.get(1).getEndDate());
					assertEquals(1750.00 * itDays / monthDays * 0.5,
							Double.parseDouble(datas.get(1).getExpression()), DELTA);
					

					// 601 o 611 Base de Accidentes de Trabajo.
					datas = s.getContextData().get(CGP_BASE.getName());
					assertEquals(2, datas.size());
					assertEquals(2, datas.size());
					
					assertEquals(start, datas.get(0).getStartDate());
					assertEquals(add(startITDate, DAY_OF_MONTH, -1), datas.get(0).getEndDate());
					assertEquals(1750.00 * workDays / monthDays,
							Double.parseDouble(datas.get(0).getExpression()), DELTA);

					assertEquals(startITDate, datas.get(1).getStartDate());
					assertEquals(end, datas.get(1).getEndDate());
					assertEquals(1750.00 * itDays / monthDays * 0.5,
							Double.parseDouble(datas.get(1).getExpression()), DELTA);

					datas = s.getContextData()
							.get(MATERNITY_BASE.getName());
					assertEquals(1, datas.size());
					assertEquals(startITDate, datas.get(0).getStartDate());
					assertEquals(end, datas.get(0).getEndDate());
					assertEquals(100.00 * itDays * 0.5,
							Double.parseDouble(datas.get(0).getExpression()), DELTA);

					datas = s.getContextData()
							.get(WORKED_HOURS.getName());
					assertEquals(2, datas.size());
					assertEquals(start, datas.get(0).getStartDate());
					assertEquals(add(startITDate, DAY_OF_MONTH, -1), datas.get(0).getEndDate());

					assertEquals(startITDate, datas.get(1).getStartDate());
					assertEquals(end, datas.get(1).getEndDate());
					long workingDays = new Period(datas.get(1).getStartDate(), datas.get(1).getEndDate())
					.daysStream()
					.filter(day -> day.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
					.filter(day -> day.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY)
					.count();
					assertEquals(workingDays * 8 * 0.5,
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
		
		int monthDays = get(startDate, Calendar.DAY_OF_MONTH);
		assertEquals(1750.00 * 9 / 30 + 1750.00 * 21 / 30 * 0.5 , salary.getTotalPayment(), DELTA);
		assertEquals(1750.00, salary.getCommonBase(), DELTA);
		//assertEquals(get(endDate, DAY_OF_MONTH) * 100.00 * 0.50 + 1750.00 * 1/2 , salary.getCommonBase());
		
		
	}
	
	@Test
	public void testPaternityITAndATEP() throws ExpressionException, SQLException,
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
		addPayment(aonContext, contract, maternity, 
				"DIAS_PATERNIDAD * 0", 
				"DIAS_PATERNIDAD * BASE_REGULADORA"
				);
		addPrestITs(aonContext, contract);
		
		Date startITDate = getToday() ;
		addIT(aonContext, contract, LeaveType.PATERNITY, startITDate,
				null, null);
		addData(aonContext, contract, startITDate,
				null,ContextVariable.PATERNITY_FACTOR.getName(), "0.5");
	
		addIT(aonContext, contract, LeaveType.OCCUPATIONAL_DISEASE, startITDate,
				null, null);

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
				System.out.println(payment.getDescription() + " = " + amount + "," + quote + "[" + startDate + ".." + endDate +"]");
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				ITimedVariable<?> factor = context.get(ContextVariable.PATERNITY_FACTOR.getName() );
				if ( factor == null )
					return;
				assertEquals(ExpressionScope.CONTRACT,((IExpressionVariable<?>) factor).getExpression().getScope());
				
			}
		});
		Salary salary = calculator.calculate(ctx);
		
		assertEquals(1750.00, salary.getCommonBase(), DELTA);
		assertEquals(1750.00/2.00, salary.getTotalPayment());
		
		
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
				System.out.println(payment.getDescription() + " = " + amount + "," + quote + "[" + startDate + ".." + endDate +"]");
				if ( payment.getName().equals(ContextVariable.MATERNITY.getName()) )
					assertTrue(context.containsKey(ContextVariable.PATERNITY_FACTOR.getName()));
			}
			
		});
		salary = calculator.calculate(ctx);

		int monthDays = get(endDate, Calendar.DATE) ;
		int workedDays = get(startITDate, Calendar.DATE) -1;
		assertEquals(
				1750.00 * workedDays / monthDays 
				+ 1750.00 * (monthDays - workedDays ) / monthDays * 0.5, salary.getTotalPayment(), DELTA);
		assertEquals(1750.00 , salary.getCommonBase(), DELTA);
	}


	@Test
	public void testPaternityITAndEC() throws ExpressionException, SQLException,
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
		addPayment(aonContext, contract, maternity, 
				"DIAS_PATERNIDAD * 0", 
				"DIAS_PATERNIDAD * BASE_REGULADORA"
				);
		addPrestITs(aonContext, contract);
		
		Date startITDate = getToday() ;
		addIT(aonContext, contract, LeaveType.PATERNITY, startITDate,
				null, null);
		addData(aonContext, contract, startITDate,
				null,ContextVariable.PATERNITY_FACTOR.getName(), "0.5");
	
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, null);

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
				//System.out.println(payment.getDescription() + " = " + amount + "," + quote + "[" + startDate + ".." + endDate +"]");
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				ITimedVariable<?> factor = context.get(ContextVariable.PATERNITY_FACTOR.getName() );
				if ( factor == null )
					return;
				assertEquals(ExpressionScope.CONTRACT,((IExpressionVariable<?>) factor).getExpression().getScope());
				
			}
		});
		Salary salary = calculator.calculate(ctx);
		
		assertEquals(1750.00, salary.getCommonBase(), DELTA);
		assertEquals(1750.00/2.00, salary.getTotalPayment(), DELTA);
		
		
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
				System.out.println(payment.getDescription() + " = " + amount + "," + quote + "[" + startDate + ".." + endDate +"]");
				if ( payment.getName().equals(ContextVariable.MATERNITY.getName()) )
					assertTrue(context.containsKey(ContextVariable.PATERNITY_FACTOR.getName()));
			}
			
		});
		salary = calculator.calculate(ctx);

		int monthDays = get(endDate, Calendar.DATE) ;
		int workedDays = get(startITDate, Calendar.DATE) -1;
		assertEquals(1750.00 , salary.getCommonBase(), DELTA);
		assertEquals(
				1750.00 * workedDays / monthDays 
				+ 1750.00 * (monthDays - workedDays ) / monthDays * 0.5, salary.getTotalPayment(), DELTA);
	}


	@Test
	public void testPaternityITAndECII() throws ExpressionException, SQLException,
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
		addPayment(aonContext, contract, maternity, 
				"DIAS_PATERNIDAD * 0", 
				"DIAS_PATERNIDAD * BASE_REGULADORA"
				);
		addPrestITs(aonContext, contract);
		
		Date startPaternityDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH, 6) ;
		addIT(aonContext, contract, LeaveType.PATERNITY, startPaternityDate,
				null, null);
		addData(aonContext, contract, startPaternityDate,
				null,ContextVariable.PATERNITY_FACTOR.getName(), "0.5");
	
		Date startITDate = add(startPaternityDate, DAY_OF_MONTH, 5);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, null);

		Date startDate = add(getFirstDayOfMonth(getToday()),MONTH,1);
		Date endDate = getLastDayOfMonth(startDate);
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

		
		
		startDate = getFirstDayOfMonth(getToday());
		endDate = getLastDayOfMonth(startDate);
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		calculator.setSalaryBuilder(new SalaryBuilder(){
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				System.out.println(payment.getDescription() + " = " + amount + "," + quote + "[" + startDate + ".." + endDate +"]");
				if ( payment.getName().equals(ContextVariable.MATERNITY.getName()) )
					assertTrue(context.containsKey(ContextVariable.PATERNITY_FACTOR.getName()));
			}
			
		});
		Salary salary = calculator.calculate(ctx);

		int monthDays = get(endDate, Calendar.DATE) ;
		int workedDays = get(startPaternityDate, Calendar.DATE) -1;
		assertEquals(1750.00 , salary.getCommonBase(), DELTA);
		assertEquals(
				1750.00 * workedDays / monthDays 
				+ 1750.00 * (monthDays - workedDays ) / monthDays * 0.5, salary.getTotalPayment(), DELTA);
	}


	@Test
	public void testPaternityITPartial() throws ExpressionException, SQLException,
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
		}, 
		new String[] {						
		"BASE_CGC * 0.10", 
		"BASE_CGP * 0.05",
		"BASE_IRPF * PORCENTAJE_IRPF/100" 
		}, null);
		//@formatter:on
		
		PaymentConceptRecord maternity = addConcept(aonContext, ContextVariable.MATERNITY.getName());
		addPayment(aonContext, contract, maternity, "DIAS_PATERNIDAD * 0", "DIAS_PATERNIDAD * BASE_REGULADORA");
		
		Date startDate = add(getFirstDayOfMonth(getToday()),MONTH,1);
		Date endDate = getLastDayOfMonth(startDate);

		Date startITDate = getToday() ;
		Date endITDate = add(startDate, Calendar.DAY_OF_MONTH, 5);
		
		addIT(aonContext, contract, LeaveType.PATERNITY, startITDate,endITDate, 1750.00/30);
		addData(aonContext, contract, startITDate, endITDate, ContextVariable.DIRECT_PAY_START, "FECHA("+ (get(startITDate, Calendar.YEAR)+2) +",01,01)");

		addData(aonContext, contract, startITDate, endITDate, ContextVariable.PATERNITY_FACTOR, 0.50);
	

		addData(aonContext, contract, startDate, endITDate, ContextVariable.PATERNITY_FACTOR, 0.50);
		addData(aonContext, contract, startDate, endITDate, ContextVariable.DIRECT_PAY_START, "FECHA("+ (get(startITDate, Calendar.YEAR)+2) +",01,01)");
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder(){
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				System.out.println(description + "[" + startDate + ", " + endDate +"]: " + amount );
			}
		});
		Salary salary = calculator.calculate(ctx);
		
		int monthDays = get(endDate, Calendar.DAY_OF_MONTH );
		int paternityDays = get(endITDate, Calendar.DAY_OF_MONTH);
		assertEquals(
				1750.00 * paternityDays / monthDays * 0.5 + 
				1750.00 * ( monthDays - paternityDays ) / monthDays , salary.getTotalPayment(), DELTA);
		
		
	}

	
	@Test
	public void testPaternityITPartialII() throws ExpressionException, SQLException,
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
		}, 
		new String[] {						
		"BASE_CGC * 0.10", 
		"BASE_CGP * 0.05",
		"BASE_IRPF * PORCENTAJE_IRPF/100" 
		}, null);
		//@formatter:on
		
		PaymentConceptRecord maternity = addConcept(aonContext, ContextVariable.MATERNITY.getName());
		addPayment(aonContext, contract, maternity, "DIAS_PATERNIDAD * 0", "DIAS_PATERNIDAD * BASE_REGULADORA");
		
		Date startDate = add(getFirstDayOfMonth(getToday()),MONTH,1);
		Date endDate = getLastDayOfMonth(startDate);

		Date startITDate = getToday() ;
		Date endITDate = add(startDate, Calendar.DAY_OF_MONTH, 5);
		
		addIT(aonContext, contract, LeaveType.PATERNITY, startITDate,endITDate, 1750.00/30);
		addData(aonContext, contract, startITDate, add(startDate, Calendar.DAY_OF_MONTH,-1), ContextVariable.PATERNITY_FACTOR, 0.50);
		addData(aonContext, contract, startDate, endITDate, ContextVariable.PATERNITY_FACTOR, 0.50);
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder(){
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				System.out.println(description + "[" + startDate + ", " + endDate +"]: " + amount );
			}
		});
		Salary salary = calculator.calculate(ctx);
		
		int monthDays = get(endDate, Calendar.DAY_OF_MONTH );
		int paternityDays = get(endITDate, Calendar.DAY_OF_MONTH);
		assertEquals(
				1750.00 * paternityDays / monthDays * 0.5 + 
				1750.00 * ( monthDays - paternityDays ) / monthDays , salary.getTotalPayment(), DELTA);
		
		
	}

	@Test
	public void testPaternityITPartialIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
		getFirstDayOfMonth(getToday()),
		new HashMap<String,String>(){
			{
				put(MONTH_DAYS.getName(), "30.00");
				put(PARTIAL_FACTOR.getName(), "0.75");
			}
		},
		new String[] {
		"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
		"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
		}, 
		new String[] {						
		"BASE_CGC * 0.10", 
		"BASE_CGP * 0.05",
		"BASE_IRPF * PORCENTAJE_IRPF/100" 
		}, null);
		//@formatter:on
		
		Date startITDate = getToday() ;
		Date endITDate = add(startITDate, Calendar.MONTH, 5);
		
		addIT(aonContext, contract, LeaveType.PATERNITY, startITDate,endITDate, 1750.00/30);
		addData(aonContext, contract, startITDate, endITDate, ContextVariable.PATERNITY_FACTOR, 0.50);
		
		Date startDate = getFirstDayOfMonth(add(startITDate,MONTH,1));
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
				System.out.println(description + "[" + startDate + ", " + endDate +"]: " + amount );
			}
		});
		Salary salary = calculator.calculate(ctx);
		
		int monthDays = get( endDate, Calendar.DAY_OF_MONTH );
		
		assertEquals( 1750.00 * 0.75 * 0.50 , salary.getTotalPayment(), DELTA);
		
		
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
		
		assertEquals(( 1750.00/ 30.00 ) * (10 + 6 * 0.75) , salary.getTotalPayment());

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

		assertEquals(66.66 * 30.00, salary.getCommonBase(), DELTA);
		assertEquals(0.75 * 66.66 * get(endDate, DAY_OF_MONTH) , salary.getTotalPayment(), DELTA);
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
		assertEquals(1, updated);
		
		
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);
		double br = 1750.00 / 30.00;
		assertEquals(0.75 * br * get(endDate, DAY_OF_MONTH) , salary.getTotalPayment(), DELTA);
		assertEquals(br * 30.00, salary.getCommonBase(), DELTA);
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

		//assertEquals(66.66 * 10.00, salary.getCommonBase(), DELTA);
		long quoteDays = Math.round(salary.getCommonBase() / 66.66); //Double.parseDouble(salary.getSalaryData(QUOTE_DAYS.toString()));
		System.out.println("DIAS COTIZADOS : " + quoteDays );
		//System.out.println("NICIO CONTRATO : " + get(contractStart, DAY_OF_MONTH) );
		assertEquals(0.75 * 66.66 * /*(30.00 - get(contractStart, DAY_OF_MONTH) + 1 )*/ 10 , salary.getTotalPayment(), DELTA);
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

		assertEquals(0.75 * 1750.00/30.00 * get(endDate, DAY_OF_MONTH) , salary.getTotalPayment(), DELTA);
		assertEquals(1750.00/30.00 * 30.00, salary.getCommonBase(), DELTA);
	}
	
	@Test
	public void testITPrevious2ContractStartV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext,
				getToday(),
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
				String.format("BASE_REGULADORA * 0.60 * %s_16_20",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s_21",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		//@formatter:on

		Date startITDate = add(contract.getStartDate(), DAY_OF_MONTH, -19 ); 
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, null);

		Date startDate = getFirstDayOfMonth(contract.getStartDate());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);
		
		salary.getSalaryPayments().forEach( p -> System.out.println(p.getExpression() +":" + p.getAmount() + "/ " + p.getQuote() ));
		
		//salary.getSalaryDatas().forEach( p -> System.out.println(p.getName() + " = " + p.getExpression() + "(" + p.getStartDate() + ".." + p.getEndDate() +")" ));

		int it21Days =  get(endDate, DAY_OF_MONTH) - get(contract.getStartDate(),DAY_OF_MONTH);
		
		assertEquals(
				0.75 * 1750.00/30.00 * it21Days 
				+  0.60 * 1750.00/30.00 , salary.getTotalPayment(), DELTA);
		if ( get(contract.getStartDate(),DAY_OF_MONTH) == 1 ) {
		    assertEquals(1750.00/30.00 * 30.00, salary.getCommonBase(), DELTA);
		}
		else {
		    assertEquals(1750.00/30.00 * Math.min( it21Days + 1, 30.00 ), salary.getCommonBase(), DELTA);
		}
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

		assertEquals(1750.00, salary.getCommonBase(), DELTA);
		assertEquals(1750.00/30.00 * get(endDate, DAY_OF_MONTH), salary.getTotalPayment(), DELTA);

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

		assertEquals(1750.00, salary.getCommonBase(), DELTA);
		assertEquals(1750.00/30.00 * get(endDate, DAY_OF_MONTH), salary.getTotalPayment(), DELTA);
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

		assertEquals(1750.00, salary.getCommonBase(), DELTA);
		assertEquals(1750.00/30.00 * get(endDate, DAY_OF_MONTH), salary.getTotalPayment(), DELTA);

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

		assertEquals(1750.00, salary.getCommonBase(), DELTA);
		assertEquals(1750.00/30.00 * get(endDate, DAY_OF_MONTH), salary.getTotalPayment(), DELTA);
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
						this.start = "01/01";
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

		assertEquals(1750.00, salary.getCommonBase(), DELTA);
		int activeDays =  get(startITDate, DAY_OF_MONTH) -1 ;
		assertEquals(1500.00 / 30 * activeDays 
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

		assertEquals(1750.00, salary.getCommonBase(), DELTA);
		assertEquals(1750.00/30.00 * get(endDate, DAY_OF_MONTH), salary.getTotalPayment(), DELTA);
	}
	
	@Disabled("BRUTO it's no yet 'SMART' supported")
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

		assertEquals(1750.00, salary.getCommonBase(), DELTA);
		assertEquals(1750.00/30.00 * get(endDate, DAY_OF_MONTH), salary.getTotalPayment(), DELTA);

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

		assertEquals(1750.00, salary.getCommonBase(), DELTA);
		assertEquals(1750.00/30.00 * get(endDate, DAY_OF_MONTH), salary.getTotalPayment(), DELTA);
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

		assertEquals(1750.00, salary.getCommonBase(), DELTA);
		assertEquals(1750.00/30.00 * get(endDate, DAY_OF_MONTH), salary.getTotalPayment(), DELTA);

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

		assertEquals(1750.00, salary.getCommonBase(), DELTA);
		assertEquals(1750.00/30.00 * get(endDate, DAY_OF_MONTH), salary.getTotalPayment(), DELTA);
	}

	@Test
	public void testITWithConstantVII() throws ExpressionException, SQLException,
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
		Date endITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH,13);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				endITDate, null);
		
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
			System.out.println("1-."+ payment.getName() + " = " + payment.getAmount() + ", " + payment.getQuote()
					+ " (" + payment.getExpression() + ")");
		

		assertEquals(1750.00, salary.getCommonBase(), DELTA);
		assertEquals(1750.00/30.00 * get(endDate, DAY_OF_MONTH), salary.getTotalPayment(), DELTA);

		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		calculator.setSalaryBuilder(new SalaryBuilder());
		salary = calculator.calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments())
			System.out.println("2-." + payment.getName() + " = " + payment.getAmount() + ", " + payment.getQuote()
					+ " (" + payment.getExpression() + ")");

		assertEquals(1750.00, salary.getCommonBase(), DELTA);
		assertEquals(1750.00, salary.getTotalPayment(), DELTA);
	}
	
	
	@Test
	public void testITWithConstantVIII() throws ExpressionException, SQLException,
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
		Date endITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH,13);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				endITDate, null);
		
		startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH,20);
		endITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH,26);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				endITDate, null);

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
			System.out.println("1-." + payment.getName() + " = " + payment.getAmount() + ", " + payment.getQuote()
					+ " (" + payment.getExpression() + ")");

		assertEquals(1750.00, salary.getCommonBase(), DELTA);
		assertEquals(1750.00/30.00 * get(endDate, DAY_OF_MONTH), salary.getTotalPayment(), DELTA);

		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		calculator.setSalaryBuilder(new SalaryBuilder());
		salary = calculator.calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments())
			System.out.println("2-." + payment.getName() + " = " + payment.getAmount() + ", " + payment.getQuote()
					+ " (" + payment.getExpression() + ")");

		assertEquals(1750.00, salary.getCommonBase(), DELTA);
		assertEquals(1750.00, salary.getTotalPayment(), DELTA);
	}
	

	
	@Test
	@Disabled("Not yet fixed")
	public void testITWithConstantIX() throws ExpressionException, SQLException,
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
				"__P0" ,
				"__P1"
				}, 
				new String[] {
				}, 
				null);
		//@formatter:on

		addPrestITs(aonContext, contract);
		
		//addData(aonContext, contract, startContract, endDate, "__P0", "250.00");;

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

		assertEquals(1750.00, salary.getCommonBase(), DELTA);
		assertEquals(1750.00/30.00 * get(endDate, DAY_OF_MONTH), salary.getTotalPayment(), DELTA);

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

		assertEquals(1750.00, salary.getCommonBase(), DELTA);
		assertEquals(1750.00/30.00 * get(endDate, DAY_OF_MONTH), salary.getTotalPayment(), DELTA);
	}

	@Test
	public void testITPartialNoHours() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		int lastDayOfMonth = get(getLastDayOfMonth(getToday()), Calendar.DAY_OF_WEEK);
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(add(getToday(), Calendar.YEAR, -1)),
				new HashMap<String, String>() {
					{
						put(TC2.getName(),format("\"%s\"", C200.getValue()));
						put(AGREEMENT_HOURS.getName(), format("%d", 40));
						
						put(SUNDAY_HOURS.getName(), format(Locale.US,"%f", lastDayOfMonth == Calendar.SUNDAY ? 0.00 : 20.00/6.00 ));
						put(MONDAY_HOURS.getName(), format(Locale.US,"%f", lastDayOfMonth == Calendar.MONDAY ? 0.00 : 20.00/6.00));
						put(TUESDAY_HOURS.getName(), format(Locale.US,"%f", lastDayOfMonth == Calendar.TUESDAY ? 0.00 : 20.00/6.00));
						put(WEDNESDAY_HOURS.getName(), format(Locale.US,"%f", lastDayOfMonth == Calendar.WEDNESDAY ? 0.00 : 20.00/6.00));
						put(THURSDAY_HOURS.getName(), format(Locale.US,"%f", lastDayOfMonth == Calendar.THURSDAY ? 0.00 : 20.00/6.00));
						put(FRIDAY_HOURS.getName(), format(Locale.US,"%f", lastDayOfMonth == Calendar.FRIDAY ? 0.00 : 20.00/6.00));
						put(SATURDAY_HOURS.getName(), format(Locale.US,"%f", lastDayOfMonth == Calendar.SATURDAY ? 0.00 : 20.00/6.00));
					}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				"TRACE('HORAS_TRABAJADAS:%f\r\n', HORAS_TRABAJADAS);0.00",
				"TRACE('DIAS_TRABAJADOS:%f\r\n', DIAS_TRABAJADOS);0.00"
				}, 
				new String[] {
				},
				null,
				null);
		//@formatter:on

		Date startITDate = add(getToday(), Calendar.MONTH,-1);
		Date endITDate = add(getLastDayOfMonth(getToday()), Calendar.DAY_OF_MONTH,-1);
		addIT(aonContext, contract, 
				LeaveType.COMMON_DISEASE, 
				startITDate,
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

		
		int monthDays = get(endDate, Calendar.DAY_OF_MONTH);
		
		assertEquals(1750.00 / monthDays * 0.50, salary.getTotalPayment(), DELTA);
		String workedHours = salary.getSalaryData(ContextVariable.WORKED_HOURS.getName());
		//assertEquals(40.00/ 7.00 * 0.50, Double.parseDouble(workedHours), DELTA);
	}

	@Test
	public void testCommonDiseaseIT365RedefinedII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemPayments(aonContext);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, null);
		//@formatter:on

		addPrestITs(aonContext, contract);

		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH,10);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				startITDate, null);
		
		addData(aonContext, contract, startITDate, null, ContextVariable.DIRECT_PAY_START.getName(), 
				String.format("FECHA(%d,%d,%d)", get(startITDate, YEAR),get(startITDate, MONTH)+1, get(startITDate, DAY_OF_MONTH) ));

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
			if ( PREST_IT.equals(payment.getName() ))
					assertEquals("DIAS_ENFERMEDAD_COMUN_366 * 0.00", payment.getExpression());	
		}

		assertEquals(5, salary.getSalaryPayments().size());

	}

	@Test
	public void testCommonDiseaseIT365RedefinedIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemPayments(aonContext);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, null);
		//@formatter:on

		addPrestITs(aonContext, contract);

		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH,10);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				startITDate, null);
		
		addData(aonContext, contract, startITDate, null, ContextVariable.DIRECT_PAY_START.getName(), 
				String.format("FECHA(%d,%d,%d)", get(startITDate, YEAR),get(startITDate, MONTH)+1, get(startITDate, DAY_OF_MONTH) ));

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
			if ( PREST_IT.equals(payment.getName() ))
					assertEquals("DIAS_ENFERMEDAD_COMUN_366 * 0.00", payment.getExpression());	
		}

		assertEquals(5, salary.getSalaryPayments().size());

		startITDate = add(add(getFirstDayOfMonth(getToday()), Calendar.MONTH, 1), DAY_OF_MONTH,10);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				startITDate, null);

		startDate = getFirstDayOfMonth(startITDate);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculator = new SmartContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());
		salary = calculator.calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
			if ( PREST_IT.equals(payment.getName() ))
				assertEquals(String.format("BASE_REGULADORA * 1.00 * %s_1_3 * (isdef %s ? %s : 1.00)",  COMMON_DISEASE_DAYS, LEAVE_FACTOR, LEAVE_FACTOR), payment.getExpression());	
		}
	}

	@Test
	public void testProfessionalDiseaseIT365RedefinedII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemPayments(aonContext);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, null);
		//@formatter:on

		addPrestITs(aonContext, contract);

		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH,10);
		addIT(aonContext, contract, LeaveType.OCCUPATIONAL_DISEASE, startITDate,
				startITDate, null);
		
		addData(aonContext, contract, startITDate, null, ContextVariable.DIRECT_PAY_START.getName(), 
				String.format("FECHA(%d,%d,%d)", get(startITDate, YEAR),get(startITDate, MONTH)+1, get(startITDate, DAY_OF_MONTH) ));

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
			if ( PREST_IT.equals(payment.getName() ))
					assertEquals("DIAS_ENFERMEDAD_PROFESIONAL_366 * 0.00", payment.getExpression());	
		}

		assertEquals(5, salary.getSalaryPayments().size());

	}

	@Test
	public void testProfessionalDiseaseIT365RedefinedIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemPayments(aonContext);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, null);
		//@formatter:on

		addPrestITs(aonContext, contract);

		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH,10);
		addIT(aonContext, contract, LeaveType.OCCUPATIONAL_DISEASE, startITDate,
				startITDate, null);
		
		addData(aonContext, contract, startITDate, null, ContextVariable.DIRECT_PAY_START.getName(), 
				String.format("FECHA(%d,%d,%d)", get(startITDate, YEAR),get(startITDate, MONTH)+1, get(startITDate, DAY_OF_MONTH) ));

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
			if ( PREST_IT.equals(payment.getName() ))
					assertEquals("DIAS_ENFERMEDAD_PROFESIONAL_366 * 0.00", payment.getExpression());	
		}

		assertEquals(5, salary.getSalaryPayments().size());

		startITDate = add(add(getFirstDayOfMonth(getToday()), Calendar.MONTH, 1), DAY_OF_MONTH,10);
		addIT(aonContext, contract, LeaveType.OCCUPATIONAL_DISEASE, startITDate,
				startITDate, null);

		startDate = getFirstDayOfMonth(startITDate);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculator = new SmartContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());
		salary = calculator.calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}
		assertEquals(5, salary.getSalaryPayments().size());
	}

	
	@Test
	public void testProfessionalDiseaseIT365RedefinedIV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemPayments(aonContext);

		Date contractStart = add(getToday(), Calendar.YEAR, -10 );
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext,
				contractStart,
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, null);
		//@formatter:on

		addPrestITs(aonContext, contract);

		
		
		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH,10);

		Date firstITDate = add(startITDate, Calendar.DAY_OF_MONTH, -400);
		ContractLeaveRecord firstIT = 
		addIT(aonContext, contract, LeaveType.OCCUPATIONAL_DISEASE, firstITDate,
				add(firstITDate, Calendar.DAY_OF_MONTH, 360), null);

		addIT(aonContext, contract, LeaveType.OCCUPATIONAL_DISEASE, startITDate,
				null, null, firstIT.getId());
		
		addData(aonContext, contract, startITDate, null, ContextVariable.DIRECT_PAY_START.getName(), 
				String.format("FECHA(%d,%d,%d)", get(startITDate, YEAR)+1,get(startITDate, MONTH), get(startITDate, DAY_OF_MONTH) ));

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
					+ " (" + payment.getExpression() + "," + payment.getQuote() + ")");
			if ( PREST_IT.equals(payment.getName() ))
				assertNotEquals("DIAS_ENFERMEDAD_PROFESIONAL_366 * 0.00", payment.getExpression());	
		}

	}

	@Test
	public void testCommonDiseaseIT365RedefinedIV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemPayments(aonContext);

		Date contractStart = add(getToday(), Calendar.YEAR, -10 );
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext,
				contractStart,
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, null);
		//@formatter:on

		addPrestITs(aonContext, contract);

		
		
		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH,10);

		Date firstITDate = add(startITDate, Calendar.DAY_OF_MONTH, -400);
		ContractLeaveRecord firstIT = 
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, firstITDate,
				add(firstITDate, Calendar.DAY_OF_MONTH, 360), null);

		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, null, firstIT.getId());
		
		addData(aonContext, contract, startITDate, null, ContextVariable.DIRECT_PAY_START.getName(), 
				String.format("FECHA(%d,%d,%d)", get(startITDate, YEAR)+1,get(startITDate, MONTH), get(startITDate, DAY_OF_MONTH) ));

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
					+ " (" + payment.getExpression() + "," + payment.getQuote() + ")");
			if ( PREST_IT.equals(payment.getName() ))
				assertNotEquals("DIAS_ENFERMEDAD_COMUN_366 * 0.00", payment.getExpression());	
		}

	}

	@Test
	public void testBaseMinIT365() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);

		Date contractStart = add(getToday(), Calendar.YEAR, -10 );
		
		addSystemData(aonContext, contractStart, null, 
			new HashMap<String, String>() {
			{
				put("BASE_CGC_MIN","1000.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/DIAS_MES) * COEFICIENTE_PARCIALIDAD");						
				put("BASE_CGP_MIN","1000.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/DIAS_MES) * COEFICIENTE_PARCIALIDAD");						
			}
		});
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext,
				contractStart,
				new String[] {
				"25.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"150.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, null);

		//@formatter:on

		addPrestITs(aonContext, contract);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);
		assertEquals(1000.00, salary.getCommonBase(), DELTA);
		
		
		Date startITDate = add(getToday(), DAY_OF_MONTH, -400);


		ContractLeaveRecord firstIT = 
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, 1.00);

		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		salary = calculator.calculate(ctx);
		

		double quote = 0.00;
		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
		    	quote += payment.getQuote();
//			System.out.println(payment.getName() + " = " + payment.getAmount()
//					+ " (" + payment.getExpression() + "," + payment.getQuote() + ")");
			assertEquals(DIRECT_PAY.getName(), payment.getName());	
		}
        	assertEquals(1000.00, salary.getCommonBase(), DELTA);
        	assertEquals(1000.00, salary.getProfessionalBase(), DELTA);
        	assertEquals(1000.00, quote, DELTA);
		
		startDate = getFirstDayOfMonth(startITDate);
		endDate = getLastDayOfMonth(startDate);
		
		ctx = getContractSalaryCalculatorContext(
			connection, startDate, endDate, endDate, contract);

		calculator = new SmartContractSalaryCalculator<Salary>();
        	calculator.setSalaryBuilder(new SalaryBuilder());
        	salary = calculator.calculate(ctx);
        
        	quote = 0.00;
        	for (com.esferalia.aon.payroll.SalaryPayment payment : salary
        			.getSalaryPayments()) {
        	    	quote += payment.getQuote();
        		System.out.println(payment.getName() + " = " + payment.getAmount()
        				+ " (" + payment.getExpression() + "," + payment.getQuote() + ")");
        	}
        	
        	assertEquals(1000.00, salary.getCommonBase(), DELTA);
        	assertEquals(1000.00, salary.getProfessionalBase(), DELTA);
        	assertEquals(1000.00, quote, DELTA);

        	assertEquals(1000.00, quote, DELTA);
		assertEquals(1000.00, salary.getCommonBase(), DELTA);
		assertEquals(1000.00, salary.getProfessionalBase(), DELTA);
		
		startDate = getFirstDayOfMonth(add(startITDate, Calendar.DAY_OF_MONTH, 265));
		endDate = getLastDayOfMonth(startDate);
		
		ctx = getContractSalaryCalculatorContext(
			connection, startDate, endDate, endDate, contract);

		calculator = new SmartContractSalaryCalculator<Salary>();
        	calculator.setSalaryBuilder(new SalaryBuilder());
        	salary = calculator.calculate(ctx);
        
        	quote = 0.00;
        	for (com.esferalia.aon.payroll.SalaryPayment payment : salary
        			.getSalaryPayments()) {
        	    	quote += payment.getQuote();
        		System.out.println(payment.getName() + " = " + payment.getAmount()
        				+ " (" + payment.getExpression() + "," + payment.getQuote() + ")");
        	}
        	
        	assertEquals(1000.00, salary.getCommonBase(), DELTA);
        	assertEquals(1000.00, salary.getProfessionalBase(), DELTA);
        	assertEquals(1000.00, quote, DELTA);

		startDate = getFirstDayOfMonth(add(startITDate, Calendar.DAY_OF_MONTH, 365));
		endDate = getLastDayOfMonth(startDate);
		
		ctx = getContractSalaryCalculatorContext(
			connection, startDate, endDate, endDate, contract);

		calculator = new SmartContractSalaryCalculator<Salary>();
        	calculator.setSalaryBuilder(new SalaryBuilder());
        	salary = calculator.calculate(ctx);
        
        	quote = 0.00;
        	for (com.esferalia.aon.payroll.SalaryPayment payment : salary
        			.getSalaryPayments()) {
        	    	quote += payment.getQuote();
        		System.out.println(payment.getName() + " = " + payment.getAmount()
        				+ " (" + payment.getExpression() + "," + payment.getQuote() + ")");
        	}
        	
        	assertEquals(1000.00, salary.getCommonBase(), DELTA);
        	assertEquals(1000.00, salary.getProfessionalBase(), DELTA);
        	assertEquals(1000.00, quote, DELTA);
	}

	@Test
	public void testBaseMinITMaternity() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);

		Date contractStart = add(getToday(), Calendar.YEAR, -10 );
		
		addSystemData(aonContext, contractStart, null, 
			new HashMap<String, String>() {
			{
				put("BASE_CGC_MIN","1000.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/DIAS_MES) * COEFICIENTE_PARCIALIDAD");						
				put("BASE_CGP_MIN","1000.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/DIAS_MES) * COEFICIENTE_PARCIALIDAD");						
			}
		});
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext,
				contractStart,
				new String[] {
				"25.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"150.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, null);

		//@formatter:on

		addPrestITs(aonContext, contract);
		PaymentConceptRecord maternityIT = addConcept(aonContext, MATERNITY.getName(),PaymentType.CRA_0000);
		addPayment(aonContext, contract, maternityIT, 
			String.format("%s * 0.00",  MATERNITY_DAYS),
			String.format("BASE_REGULADORA * %s * (isdef %s ? %s : 1.00)",  QUOTE_DAYS, MATERNITY_FACTOR, MATERNITY_FACTOR)
			);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);
		assertEquals(1000.00, salary.getCommonBase(), DELTA);
		
		
		Date startITDate = add(getToday(), Calendar.MONTH, -1);

		addIT(aonContext, contract, LeaveType.MATERNITY, startITDate,
				null, 1.00);

		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		salary = calculator.calculate(ctx);
		

		double quote = 0.00;
		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
		    	quote += payment.getQuote();
        		System.out.println(payment.getName() + " = " + payment.getAmount()
			+ " (" + payment.getExpression() + "," + payment.getQuote() + ")");
			assertEquals(MATERNITY.getName(), payment.getName());	
		}
        	assertEquals(1000.00, salary.getCommonBase(), DELTA);
        	assertEquals(1000.00, salary.getProfessionalBase(), DELTA);
        	assertEquals(1000.00, quote, DELTA);
		
        	System.out.println("=======================================");
        	
		startDate = getFirstDayOfMonth(startITDate);
		endDate = getLastDayOfMonth(startDate);
		
		ctx = getContractSalaryCalculatorContext(
			connection, startDate, endDate, endDate, contract);

		calculator = new SmartContractSalaryCalculator<Salary>();
        	calculator.setSalaryBuilder(new SalaryBuilder());
        	salary = calculator.calculate(ctx);
        
        	quote = 0.00;
        	for (com.esferalia.aon.payroll.SalaryPayment payment : salary
        			.getSalaryPayments()) {
        	    	quote += payment.getQuote();
        		System.out.println(payment.getName() + " = " + payment.getAmount()
        				+ " (" + payment.getExpression() + "," + payment.getQuote() + ")");
        	}
        	
        	assertEquals(1000.00, salary.getCommonBase(), DELTA);
        	assertEquals(1000.00, salary.getProfessionalBase(), DELTA);
        	assertEquals(1000.00, quote, DELTA);

	}

	@Test
	public void testBaseMinITMaternityPartial() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);

		Date contractStart = add(getToday(), Calendar.YEAR, -10 );
		
		addSystemData(aonContext, contractStart, null, 
			new HashMap<String, String>() {
			{
				put("BASE_CGC_MIN","1000.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/DIAS_MES) * COEFICIENTE_PARCIALIDAD");						
				put("BASE_CGP_MIN","1000.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/DIAS_MES) * COEFICIENTE_PARCIALIDAD");						
			}
		});
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext,
				contractStart,
				new String[] {
				"25.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"150.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, null);

		//@formatter:on

		addPrestITs(aonContext, contract);
		PaymentConceptRecord maternityIT = addConcept(aonContext, MATERNITY.getName(),PaymentType.CRA_0000);
		addPayment(aonContext, contract, maternityIT, 
			String.format("%s * 0.00",  MATERNITY_DAYS),
			String.format("BASE_REGULADORA * %s * (isdef %s ? %s : 1.00)",  QUOTE_DAYS, MATERNITY_FACTOR, MATERNITY_FACTOR)
			);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);
		assertEquals(1000.00, salary.getCommonBase(), DELTA);
		
		
		Date startITDate = add(getToday(), Calendar.MONTH, -1);

		addIT(aonContext, contract, LeaveType.MATERNITY, startITDate,
				null, 1.00);
		addData(aonContext, contract, startITDate, null, MATERNITY_FACTOR, 0.5);

		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		salary = calculator.calculate(ctx);
		

		double quote = 0.00;
		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
		    	quote += payment.getQuote();
        		System.out.println(payment.getName() + " = " + payment.getAmount()
			+ " (" + payment.getExpression() + "," + payment.getQuote() + ")");
			//assertEquals(MATERNITY.getName(), payment.getName());	
		}
        	assertEquals(1000.00, salary.getCommonBase(), DELTA);
        	assertEquals(1000.00, salary.getProfessionalBase(), DELTA);
        	assertEquals(1000.00, quote, DELTA);
		
        	System.out.println("=======================================");
        	
		startDate = getFirstDayOfMonth(startITDate);
		endDate = getLastDayOfMonth(startDate);
		
		ctx = getContractSalaryCalculatorContext(
			connection, startDate, endDate, endDate, contract);

		calculator = new SmartContractSalaryCalculator<Salary>();
        	calculator.setSalaryBuilder(new SalaryBuilder());
        	salary = calculator.calculate(ctx);
        
        	quote = 0.00;
        	for (com.esferalia.aon.payroll.SalaryPayment payment : salary
        			.getSalaryPayments()) {
        	    	quote += payment.getQuote();
        		System.out.println(payment.getName() + " = " + payment.getAmount()
        				+ " (" + payment.getExpression() + "," + payment.getQuote() + ")");
        	}
        	
        	assertEquals(1000.00, salary.getCommonBase(), DELTA);
        	assertEquals(1000.00, salary.getProfessionalBase(), DELTA);
        	assertEquals(1000.00, quote, DELTA);

	}
	
	@Test
	public void testBaseMinITPaternity() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);

		Date contractStart = add(getToday(), Calendar.YEAR, -10 );
		
		addSystemData(aonContext, contractStart, null, 
			new HashMap<String, String>() {
			{
				put("BASE_CGC_MIN","1000.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/DIAS_MES) * COEFICIENTE_PARCIALIDAD");						
				put("BASE_CGP_MIN","1000.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/DIAS_MES) * COEFICIENTE_PARCIALIDAD");						
			}
		});
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext,
				contractStart,
				new String[] {
				"25.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"150.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, null);

		//@formatter:on

		addPrestITs(aonContext, contract);
		PaymentConceptRecord maternityIT = addConcept(aonContext, MATERNITY.getName(),PaymentType.CRA_0000);
		addPayment(aonContext, contract, maternityIT, 
			String.format("%s * 0.00",  PATERNITY_DAYS),
			String.format("BASE_REGULADORA * %s * (isdef %s ? %s : 1.00)",  QUOTE_DAYS, PATERNITY_FACTOR, PATERNITY_FACTOR)
			);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);
		assertEquals(1000.00, salary.getCommonBase(), DELTA);
		
		
		Date startITDate = add(getToday(), Calendar.MONTH, -1);

		addIT(aonContext, contract, LeaveType.PATERNITY, startITDate,
				null, 1.00);

		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		salary = calculator.calculate(ctx);
		

		double quote = 0.00;
		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
		    	quote += payment.getQuote();
        		System.out.println(payment.getName() + " = " + payment.getAmount()
			+ " (" + payment.getExpression() + "," + payment.getQuote() + ")");
			assertEquals(MATERNITY.getName(), payment.getName());	
		}
        	assertEquals(1000.00, salary.getCommonBase(), DELTA);
        	assertEquals(1000.00, salary.getProfessionalBase(), DELTA);
        	assertEquals(1000.00, quote, DELTA);
		
        	System.out.println("=======================================");
        	
		startDate = getFirstDayOfMonth(startITDate);
		endDate = getLastDayOfMonth(startDate);
		
		ctx = getContractSalaryCalculatorContext(
			connection, startDate, endDate, endDate, contract);

		calculator = new SmartContractSalaryCalculator<Salary>();
        	calculator.setSalaryBuilder(new SalaryBuilder());
        	salary = calculator.calculate(ctx);
        
        	quote = 0.00;
        	for (com.esferalia.aon.payroll.SalaryPayment payment : salary
        			.getSalaryPayments()) {
        	    	quote += payment.getQuote();
        		System.out.println(payment.getName() + " = " + payment.getAmount()
        				+ " (" + payment.getExpression() + "," + payment.getQuote() + ")");
        	}
        	
        	assertEquals(1000.00, salary.getCommonBase(), DELTA);
        	assertEquals(1000.00, salary.getProfessionalBase(), DELTA);
        	assertEquals(1000.00, quote, DELTA);

	}

	@Test
	public void testUnknowConstantITV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				add(getFirstDayOfYear(getToday()),Calendar.YEAR, -1),
				new String[] {
				"250.00" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, 
				null);
		//@formatter:on
		
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "DIAS_MES", "30.00");
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);


		Date startITDate = 
				add(endDate, DAY_OF_MONTH,-5);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate, null, null);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		int workDays = (get(startITDate, Calendar.DAY_OF_MONTH) -1 );
		System.out.println("workDays:" + workDays + "," + startITDate );
		assertEquals( (1750.0 ) * workDays/30.00, salary.getTotalPayment());

	}

	@Test
	public void testCommonDiseaseAtLackI() throws ExpressionException, SQLException,
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
		addPrestITs(aonContext, contract);
		
		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH,10);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE_AT_LACK, startITDate,
				null, null);

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
		
		int monthDays = get(endDate, DAY_OF_MONTH);
		assertEquals( 1750.00 * 10 / monthDays , salary.getTotalPayment(), DELTA);
		assertEquals( 1750.00 , salary.getCommonBase(), DELTA);

		assertEquals( salary.getCommonBase() * 4.70d/100, salary.getTotalDeduction(), DELTA);

	}

	@Test
	public void testITDaysI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemPayments(aonContext);

		Date startOfYear = getFirstDayOfYear(getToday());
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				startOfYear,
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
		addPrestITs(aonContext, contract);

		Date startITDate = add(add(startOfYear, MONTH, 3), DAY_OF_MONTH,5);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, 1750.00/30.00);

		Date startDate = add(getFirstDayOfMonth(startITDate), Calendar.MONTH, 1);
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

		
		assertEquals( (1750.00/30.00 ) * get(endDate, DAY_OF_MONTH) , salary.getTotalPayment(), 0.05);
	}

	@Test
	public void testITDaysII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemPayments(aonContext);

		Date startOfYear = getFirstDayOfYear(getToday());
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				startOfYear,
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
		addPrestITs(aonContext, contract);

		Date startITDate = add(add(startOfYear, MONTH, 3), DAY_OF_MONTH,5);
		addIT(aonContext, contract, LeaveType.OCCUPATIONAL_DISEASE, startITDate,
				null, 1750.00/30.00);

		Date startDate = add(getFirstDayOfMonth(startITDate), Calendar.MONTH, 1);
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

		
		assertEquals( (1750.00 / 30.00 ) * get(endDate, Calendar.DAY_OF_MONTH) , salary.getTotalPayment(), 0.05);
	}

	@Test
	public void testITDaysIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemPayments(aonContext);

		Date startOfYear = getFirstDayOfYear(getToday());
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				startOfYear,
				new HashMap<String,String>(){
				{
					put(MONTH_DAYS.getName(), "30");
					put(QUOTE_GROUP.getName(), "\"01\"");
					put(PARTIAL_FACTOR.getName(), "0.5" );
				}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, null);
		//@formatter:on
		addPrestITs(aonContext, contract);

		Date startITDate = add(add(startOfYear, MONTH, 2), DAY_OF_MONTH,11);
		addIT(aonContext, contract, LeaveType.OCCUPATIONAL_DISEASE, startITDate,
				null, null);

		Date startDate = getFirstDayOfMonth(startITDate);
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

		
		assertEquals( (1750.00 / 30.00 ) * 31 * 0.5 , salary.getTotalPayment(), 0.05);
		assertEquals( (1750.00 / 30.00 ) * 31 * 0.5 , salary.getCommonBase(), 0.05);

	}

	@Test
	public void testBasesMinITI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);

		Date firstDayofYear = getFirstDayOfYear(getToday());
		Date firstDayOfPrevYear = add(firstDayofYear, Calendar.YEAR, -1);
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
		firstDayOfPrevYear,
		new String[] {
		"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
		"250.00 * DIAS_TRABAJADOS / DIAS_MES"
		}, 
		new String[] {
		}, null);
		//@formatter:on

		addPrestITs(aonContext, contract);
		
		addSystemData(
		aonContext, 
		firstDayofYear, 
		null, 
		new HashMap<String,String>(){
		{
			put(CGC_BASE_MIN.getName(), "1500.00 * DIAS_NOMINA / DIAS_MES");
		}
		});
		
		Date startDate = getFirstDayOfMonth(getLastDayOfYear(firstDayOfPrevYear));
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;
		
		ISQLContractSalaryCalculatorContext ctx = 
		getContractSalaryCalculatorContext(
		connection, 
		startDate, 
		endDate, 
		issueDate, 
		contract);
		
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = 
		new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();
		
		

		Date startITDate = add(firstDayofYear, DAY_OF_MONTH,5);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, null);

		startDate = getFirstDayOfMonth(startITDate);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
		connection, 
		startDate, 
		endDate, 
		endDate, 
		contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}
		
		salary.getSalaryDatas()
		.stream()
		.filter(d -> d.getName().equals(CGC_BASE.getName()))
		.forEach(d -> {
			System.out.println(d.getName() + " = "+ d.getExpression() + "[" + d.getStartDate() + ".." + d.getEndDate() + "]");
		});

		assertEquals(1500.00, salary.getCommonBase());

	}

	@Test
	public void testBasesMinITII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);

		Date firstDayofYear = getFirstDayOfYear(getToday());
		Date firstDayOfPrevYear = add(firstDayofYear, Calendar.YEAR, -1);
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
		firstDayOfPrevYear,
		new String[] {
		"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
		"250.00 * DIAS_TRABAJADOS / DIAS_MES"
		}, 
		new String[] {
		}, null);
		//@formatter:on

		addPrestITs(aonContext, contract);
		
		addSystemData(
		aonContext, 
		firstDayofYear, 
		null, 
		new HashMap<String,String>(){
		{
			put(CGC_BASE_MIN.getName(), "1500.00 * DIAS_NOMINA / DIAS_MES");
			put(CGP_BASE_MIN.getName(), "1250.00 * DIAS_NOMINA / DIAS_MES");
		}
		});
		
		Date startDate = getFirstDayOfMonth(getLastDayOfYear(firstDayOfPrevYear));
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;
		
		ISQLContractSalaryCalculatorContext ctx = 
		getContractSalaryCalculatorContext(
		connection, 
		startDate, 
		endDate, 
		issueDate, 
		contract);
		
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = 
		new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();
		
		

		Date startITDate = add(firstDayofYear, DAY_OF_MONTH,5);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, null);

		startDate = getFirstDayOfMonth(startITDate);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
		connection, 
		startDate, 
		endDate, 
		endDate, 
		contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}
		
		salary.getSalaryDatas()
		.stream()
		.filter(d -> d.getName().equals(CGC_BASE.getName()))
		.forEach(d -> {
			System.out.println(d.getName() + " = "+ d.getExpression() + "[" + d.getStartDate() + ".." + d.getEndDate() + "]");
		});

		assertEquals(1500.00, salary.getCommonBase());
//		assertEquals(1250.00, salary.getProfessionalBase());

	}

	@Test
	public void testBasesMinITIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);

		Date firstDayofYear = getFirstDayOfYear(getToday());
		Date firstDayOfPrevYear = add(firstDayofYear, Calendar.YEAR, -1);
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
		firstDayOfPrevYear,
		new HashMap<String,String>(){
		{
			put(MONTH_DAYS.getName(), "30");
		}
		},
		new String[] {
		"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
		"250.00 * DIAS_TRABAJADOS / DIAS_MES"
		}, 
		new String[] {
		}, null);
		//@formatter:on

		addPrestITs(aonContext, contract);
		
		addSystemData(
		aonContext, 
		firstDayofYear, 
		null, 
		new HashMap<String,String>(){
		{
			put(CGC_BASE_MIN.getName(), "1500.00 * DIAS_NOMINA / DIAS_MES");
			put(CGP_BASE_MIN.getName(), "1250.00 * DIAS_NOMINA / DIAS_MES");
		}
		});
		
		Date startDate = getFirstDayOfMonth(getLastDayOfYear(firstDayOfPrevYear));
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;
		
		ISQLContractSalaryCalculatorContext ctx = 
		getContractSalaryCalculatorContext(
		connection, 
		startDate, 
		endDate, 
		issueDate, 
		contract);
		
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = 
		new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();
		
		

		Date startITDate = add(firstDayofYear, DAY_OF_MONTH,5);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, null);

		startDate = getFirstDayOfMonth(startITDate);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
		connection, 
		startDate, 
		endDate, 
		endDate, 
		contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

//		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
//				.getSalaryPayments()) {
//			System.out.println(payment.getName() + " = " + payment.getAmount()
//					+ " (" + payment.getExpression() + ")");
//		}
		
		salary.getSalaryDatas()
		.stream()
		.filter(d -> d.getName().equals(CGC_BASE.getName()))
		.forEach(d -> {
			System.out.println(d.getName() + " = "+ d.getExpression() + "[" + d.getStartDate() + ".." + d.getEndDate() + "]");
		});

		assertEquals(1500.00, salary.getCommonBase());
//		assertEquals(1250.00, salary.getProfessionalBase());

	}

	@Test
	public void testBasesMinITIV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);

		Date firstDayofYear = getFirstDayOfYear(getToday());
		Date firstDayOfPrevYear = add(firstDayofYear, Calendar.YEAR, -1);
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
		firstDayOfPrevYear,
		new HashMap<String,String>(){
		{
			put(MONTH_DAYS.getName(), "30");
		}
		},
		new String[] {
		"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
		"250.00 * DIAS_TRABAJADOS / DIAS_MES"
		}, 
		new String[] {
		}, null);
		//@formatter:on

		addPrestITs(aonContext, contract);
		
		addSystemData(
		aonContext, 
		firstDayofYear, 
		null, 
		new HashMap<String,String>(){
		{
			put(CGC_BASE_MIN.getName(), "1500.00 * DIAS_NOMINA / DIAS_MES");
			put(CGP_BASE_MIN.getName(), "1250.00 * DIAS_NOMINA / DIAS_MES");
		}
		});
		
		Date startDate = getFirstDayOfMonth(getLastDayOfYear(firstDayOfPrevYear));
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;
		
		ISQLContractSalaryCalculatorContext ctx = 
		getContractSalaryCalculatorContext(
		connection, 
		startDate, 
		endDate, 
		issueDate, 
		contract);
		
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = 
		new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();
		
		

		Date startITDate = add(firstDayofYear, DAY_OF_MONTH,5);
		addIT(aonContext, contract, LeaveType.OCCUPATIONAL_DISEASE, startITDate,
				null, null);

		startDate = getFirstDayOfMonth(startITDate);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
		connection, 
		startDate, 
		endDate, 
		endDate, 
		contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		
		salary.getSalaryDatas()
		.stream()
		.filter(d -> d.getName().equals(CGC_BASE.getName()))
		.forEach(d -> {
			System.out.println(d.getName() + " = "+ d.getExpression() + "[" + d.getStartDate() + ".." + d.getEndDate() + "]");
		});

		assertEquals(1500.00, salary.getCommonBase());
//		assertEquals(1250.00, salary.getProfessionalBase());

	}

	@Test
	public void testBasesMinITV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);

		Date firstDayofYear = getFirstDayOfYear(getToday());


		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				firstDayofYear,
		new HashMap<String,String>(){
		{
			put(MONTH_DAYS.getName(), "30.00");
			put(PARTIAL_FACTOR.getName(), "0.75");
		}
		},
		new String[] {
		"1250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
		"500.00 * DIAS_TRABAJADOS / DIAS_MES",
		"IMPORTE_KM * KMS",
		}, 
		new String[] {
		}, null);
		//@formatter:on
		
		aonContext.getDslContext()
		.select()
		.from(PAYMENT_CONCEPT)
		.innerJoin(CONTRACT_PAYMENT).onKey()
		.where(PAYMENT_CONCEPT.EXPRESSION.eq("IMPORTE_KM * KMS"))
		.fetchInto(PAYMENT_CONCEPT)
		.forEach( r -> {
			r.setType((byte) 50 );
			r.update();
		});
		addData(aonContext, contract, firstDayofYear, null, "IMPORTE_KM", "0.25");
		addData(aonContext, contract, firstDayofYear, null, "KMS", "0");
		
		addPrestITs(aonContext, contract);
		
		addSystemData(
		aonContext, 
		firstDayofYear, 
		null, 
		new HashMap<String,String>(){
		{
			put(CGC_BASE_MIN.getName(), "(MAX(7.03, (FALSO() ? 7.03 * HORAS_NOMINA : (MODALIDAD_MENSUAL ? 1166.70 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD : 38.89 * DIAS_NOMINA * COEFICIENTE_PARCIALIDAD))))");
			put(CGP_BASE_MIN.getName(), "MAX(7.03, ((COEFICIENTE_PARCIALIDAD < 1.00) ? BASE_CGC_MIN : 1166.70 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)))");
		}
		});
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;
		
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startDate,
				null, 1750.00 /30.00);

		ISQLContractSalaryCalculatorContext ctx = 
		getContractSalaryCalculatorContext(
		connection, 
		startDate, 
		endDate, 
		issueDate, 
		contract);
		
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		double baseCgp = 
		salary.getSalaryDatas()
		.stream()
		.filter(d -> d.getName().equals(CGP_BASE.getName()))
		.peek(d -> {
			System.out.println(d.getName() + " = "+ d.getExpression() + "[" + d.getStartDate() + ".." + d.getEndDate() + "]");
		})
		.map(SalaryData::getExpression)
		.collect(Collectors.summingDouble(Double::parseDouble))
		;
		// It's partial so not adjust.
		long monthDays = get(endDate, Calendar.DAY_OF_MONTH);
		if ( endDate.equals(getLastDayOfMonth(endDate)))
			assertEquals(1750.00 / 30.00 * monthDays, baseCgp, 0.05);
		else
		    assertEquals(1750.00 / 30.00 * monthDays, baseCgp, 0.05);
	}

	@Test
	public void testPaymentITI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

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
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, null);
		
		addPrestITs(aonContext, contract);
		//@formatter:on
		
		Date startITDate = add(getFirstDayOfMonth(getToday()), Calendar.MONTH, 1);
		addIT(aonContext, contract, LeaveType.OCCUPATIONAL_DISEASE, startITDate,
				null , null);

		Date startDate = add(getFirstDayOfMonth(getToday()), Calendar.MONTH, 1);
		Date endDate = getLastDayOfMonth(startDate);
		for ( int i = 0; i < 11 ; i++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();
			calculator.setSalaryBuilder(new SalaryBuilder());
			Salary salary = calculator.calculate(ctx);
			int days = get(endDate, Calendar.DAY_OF_MONTH);
			assertEquals( days * 1750.00/30.00 , salary.getTotalPayment(), DELTA );
			System.out.println(startDate + "..." + endDate + "... OK ");
			startDate = add(startDate, Calendar.MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}

	}
	
	@Test
	public void testPaymentITII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

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
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, null);
		
		addPrestITs(aonContext, contract);
		//@formatter:on
		
		Date startITDate = add(getFirstDayOfMonth(getToday()), Calendar.MONTH, 1);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null , null);

		Date startDate = add(getFirstDayOfMonth(getToday()), Calendar.MONTH, 1);
		Date endDate = getLastDayOfMonth(startDate);
		for ( int i = 0; i < 11 ; i++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();
			calculator.setSalaryBuilder(new SalaryBuilder());
			Salary salary = calculator.calculate(ctx);
			int days = get(endDate, Calendar.DAY_OF_MONTH);
			assertEquals( days * 1750.00/30.00 , salary.getTotalPayment(), DELTA );
			System.out.println(startDate + "..." + endDate + "... OK ");
			startDate = add(startDate, Calendar.MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}

	}

	@Test
	public void testCovid19DiseaseITAt31() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

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
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, null);
		//@formatter:on
		
		addPrestITs(aonContext, contract);

		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		addIT(aonContext, contract, LeaveType.COMMON_OCCUPATIONAL_DISEASE, endDate,
				null, null);
		
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

		assertEquals(2, salary.getSalaryPayments().size());

	}

	@Test
	public void testIfDaysIT() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				add(getFirstDayOfYear(getToday()), Calendar.MONTH, -6),
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, null);
		//@formatter:on

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		Date startITDate = add(startDate, Calendar.MONTH,-2);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, null);
		

		addData(aonContext, contract, add(startDate, Calendar.DAY_OF_MONTH,10), add(startDate, Calendar.DAY_OF_MONTH,20), ContextVariable.IF_DAYS, "10");
		
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		List<ITimedResult<Number>> results = ctx.getExpressionContext().eval(ContextVariable.LEAVE_DAYS.getName(), startDate, endDate, Number.class);
		assertEquals(2, results.size());
		assertEquals(startDate, results.get(0).getPeriod().getStart());
		assertEquals(add(startDate, Calendar.DAY_OF_MONTH,9), results.get(0).getPeriod().getEnd());
	
		assertEquals(add(startDate, Calendar.DAY_OF_MONTH,21), results.get(1).getPeriod().getStart());
		assertEquals(endDate, results.get(1).getPeriod().getEnd());

		results = ctx.getExpressionContext().eval(ContextVariable.COMMON_DISEASE_DAYS_21.getName(), startDate, endDate, Number.class);
		assertEquals(2, results.size());
		assertEquals(startDate, results.get(0).getPeriod().getStart());
		assertEquals(add(startDate, Calendar.DAY_OF_MONTH,9), results.get(0).getPeriod().getEnd());
	
		assertEquals(add(startDate, Calendar.DAY_OF_MONTH,21), results.get(1).getPeriod().getStart());
		assertEquals(endDate, results.get(1).getPeriod().getEnd());
	}
	
	@Test
	public void testWrongDirectPayStart() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

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
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, null);
		//@formatter:on
		
		addPrestITs(aonContext, contract);

		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		Date startIT = add(startDate, Calendar.DAY_OF_MONTH, 10);

		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startIT,
				null, null);
		
		int year = get(startDate, Calendar.YEAR);
		addData(aonContext, contract, startIT, null, ContextVariable.DIRECT_PAY_START, "FECHA(2020,1,1)");
		
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

		assertEquals(1750.00, salary.getCommonBase());

	}


	@Test
	public void testSurrogateITI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		DomainRecord domain = 
				newDomain(aonContext);
		ScopeRecord scope = 
				newScope(aonContext, domain.getId());
		EnterpriseActivityRecord enterpriseActivity = 
				newEnterpriseActivity(
				aonContext, 
				domain.getId(),
				scope.getId(), 
				SSRegimeType.GENERAL);

		EnterpriseCccRecord enterpriseCcc = 
				newEnterpriseCcc(
				aonContext, 
				domain.getId(), 
				scope.getId(),
				enterpriseActivity.getId(), 
				CCCType.PRINCIPAL, 
				"231546798");

		WorkplaceRecord workplace = 
				newWorkplace(
				aonContext, 
				domain.getId(), 
				scope.getId(),
				enterpriseActivity.getEnterprise(), 
				null );

		RegistryRecord person = 
				newPerson(aonContext, 
				domain.getId(), 
				"33568418N", 
				"864297531");

		Date startContractDateI = getFirstDayOfMonth(getToday()); 
		Date endContractDateI = add(startContractDateI, Calendar.DAY_OF_MONTH, 10);
		
		//@formatter:off
		ContractRecord contractI = 
		newContract(aonContext,
				SSRegimeType.GENERAL,  
				CCCType.PRINCIPAL, 
				startContractDateI,
				endContractDateI,
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
				}, 
				null, 
				domain.getId(),  
				person.getId(),  
				workplace.getId(),  
				enterpriseCcc.getId(),  
				enterpriseActivity.getId() 
				);
		
		addPrestITs(aonContext, contractI);

		Date startContractDateII = add(endContractDateI, Calendar.DAY_OF_MONTH, 1);	; 

		ContractRecord contractII =
		newContract(aonContext, 
				SSRegimeType.GENERAL,  
				CCCType.PRINCIPAL, 
				startContractDateII, 
				null, 
				new HashMap<String,String>(){
				{
					put(MONTH_DAYS.getName(), "30");
				}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String [] {}, 
				null, 
				domain.getId(),  
				person.getId(),  
				workplace.getId(),  
				enterpriseCcc.getId(),  
				enterpriseActivity.getId() 
		);
		//@formatter:on
		
		addPrestITs(aonContext, contractII);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		Date startIT = add(startDate, Calendar.DAY_OF_MONTH, 2);

		addIT(aonContext, contractI, LeaveType.COMMON_DISEASE, startIT,null, null);
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contractI);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
//			System.out.println(payment.getName() + " = " + payment.getAmount()
//					+ " (" + payment.getExpression() + ")");
		}
		
		long prestIt1_3 =
		salary.getSalaryPayments().stream().filter( p -> p.getExpression().contains("_1_3")).count();
		long prestIt4_15 =
		salary.getSalaryPayments().stream().filter( p -> p.getExpression().contains("_4_15")).count();
		
		assertEquals(1, prestIt1_3);
		assertEquals(1, prestIt4_15);

		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contractII);
		calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		salary = calculator.calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}
		
		prestIt1_3 =
		salary.getSalaryPayments().stream().filter( p -> p.getExpression().contains("_1_3")).count();
		prestIt4_15 =
		salary.getSalaryPayments().stream().filter( p -> p.getExpression().contains("_4_15")).count();
		
		assertEquals(0, prestIt1_3);
		assertEquals(1, prestIt4_15);
	}

	// ------------------------------------------------------------------------
	

	protected static PaymentConceptRecord addPrestITs(AONContext aonContext, ContractRecord contract) {
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT,PaymentType.CRA_0000);
		PaymentConceptRecord directIT = addConcept(aonContext, DIRECT_PAY.getName(),PaymentType.CRA_0000);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 1.00 * %s_1_3 * (isdef %s ? %s : 1.00)",  COMMON_DISEASE_DAYS, LEAVE_FACTOR, LEAVE_FACTOR),
				String.format("BASE_REGULADORA * %s * (isdef %s ? %s : 1.00)",  QUOTE_DAYS, LEAVE_FACTOR, LEAVE_FACTOR)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 1.00 * %s_4_15 * (isdef %s ? %s : 1.00)",  COMMON_DISEASE_DAYS, LEAVE_FACTOR, LEAVE_FACTOR),
				String.format("BASE_REGULADORA * %s * (isdef %s ? %s : 1.00)",  QUOTE_DAYS, LEAVE_FACTOR, LEAVE_FACTOR)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 1.00 * %s_16_20 * (isdef %s ? %s : 1.00)",  COMMON_DISEASE_DAYS, LEAVE_FACTOR, LEAVE_FACTOR),
				String.format("BASE_REGULADORA * %s * (isdef %s ? %s : 1.00)",  QUOTE_DAYS, LEAVE_FACTOR, LEAVE_FACTOR)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 1.00 * %s_21 * (isdef %s ? %s : 1.00)",  COMMON_DISEASE_DAYS, LEAVE_FACTOR, LEAVE_FACTOR),
				String.format("BASE_REGULADORA * %s * (isdef %s ? %s : 1.00)",  QUOTE_DAYS, LEAVE_FACTOR, LEAVE_FACTOR)
				);
		addPayment(aonContext, contract, directIT, 
				String.format("DIAS_ENFERMEDAD_COMUN_366 * 0.00",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * %s * (isdef %s ? %s : 1.00)",  QUOTE_DAYS, LEAVE_FACTOR, LEAVE_FACTOR)
				);
		addPayment(aonContext, contract, directIT, 
				String.format("DIAS_ENFERMEDAD_PROFESIONAL_366 * 0.00",  OCCUPATIONAL_DISEASE_DAYS),
				String.format("BASE_REGULADORA * %s * (isdef %s ? %s : 1.00)",  QUOTE_DAYS, LEAVE_FACTOR, LEAVE_FACTOR)
				);
		addPayment(aonContext, contract, prestIT, 
				"DIAS_ENFERMEDAD_COMUN_CARENCIA * 0.00",
				String.format("BASE_REGULADORA * %s * (isdef %s ? %s : 1.00)",  QUOTE_DAYS, LEAVE_FACTOR, LEAVE_FACTOR)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 1.00 * %s * (isdef %s ? %s : 1.00)",  OCCUPATIONAL_DISEASE_DAYS, LEAVE_FACTOR, LEAVE_FACTOR),
				String.format("BASE_REGULADORA * %s * (isdef %s ? %s : 1.00)",  QUOTE_DAYS, LEAVE_FACTOR, LEAVE_FACTOR)
				);
		
		for ( ContextVariable daysVariable : new ContextVariable [] {MENSTRUATION_DAYS, PREGNANCY_STOP_DAYS, PREGNANCY_39_WEEK_DAYS}  ) {
			addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.60 * %s_1_20 * (isdef %s ? %s : 1.00)",  daysVariable, LEAVE_FACTOR, LEAVE_FACTOR),
				String.format("BASE_REGULADORA * %s * (isdef %s ? %s : 1.00)",  QUOTE_DAYS, LEAVE_FACTOR, LEAVE_FACTOR)
				);
			addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s_21 * (isdef %s ? %s : 1.00)",  daysVariable, LEAVE_FACTOR, LEAVE_FACTOR),
				String.format("BASE_REGULADORA * %s * (isdef %s ? %s : 1.00)",  QUOTE_DAYS, LEAVE_FACTOR, LEAVE_FACTOR)
				);
		}

		return prestIT;
	}
	
	
}
