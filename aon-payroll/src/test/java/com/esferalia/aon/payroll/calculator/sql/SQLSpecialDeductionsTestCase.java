package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractDeduction.CONTRACT_DEDUCTION;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.watson.util.AonDateUtils;

import static org.junit.jupiter.api.Assertions.*;

public class SQLSpecialDeductionsTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000001;

	@Test
	public void testBASE_CGC_I() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), Collections.emptyMap());
		addPayment(aonContext, contract, SalaryType.SETTLE, "500");

		//@formatter:off
		addDeduction(
				aonContext,
				contract,
				"BASE_CGC = /*user*/ 100.00 /**/; BUILDER.setCgcBase(BASE_CGC); REMOVE();",
				getFirstDayOfMonth(getToday()),
				getLastDayOfMonth(getToday()));
		//@formatter:on
		//@formatter:off
		addDeduction(
				aonContext,
				contract,
				"BASE_CGP = /*user*/ 100.00 /**/; BUILDER.setCgpBase(BASE_CGC); REMOVE();",
				getFirstDayOfMonth(getToday()),
				getLastDayOfMonth(getToday()));
		//@formatter:on
		addDeduction(
				aonContext,
				contract,
				"BASE_CGC * 0.50",
				contract.getStartDate(),
				contract.getEndDate());

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		SQLContractSettleCalculatorContext ctx = new SQLContractSettleCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();

		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder(){
			@Override
			public void addDeduction(Double amount, String description,
					java.util.Date start, java.util.Date end,
					IDeduction deduction,
					Map<String, ITimedVariable<?>> context) {
				super.addDeduction(amount, description, start, end, deduction, context);
				System.out.println("amount : " + amount );
			}
		});
		ISalary salary = calculator.calculate(ctx);
		
		
		assertEquals(String.format("%s :", ContextVariable.TOTAL_PAYMENT), 500.00, salary.getTotalPayment());
		assertEquals(String.format("%s :", ContextVariable.CGC_BASE), 100.00, salary.getCommonBase());
		assertEquals(String.format("%s :", ContextVariable.TOTAL_LIQUID), 500.00 - ( 100*0.50) , salary.getTotalLiquid());
	}

	@Test
	public void testLessThan30DaysArt151I() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		Date startContractDate = getFirstDayOfMonth(getToday());
		Date endContractDate = add(startContractDate, Calendar.DAY_OF_MONTH, 25);
		
		ContractRecord contract = newContract(aonContext, 
				startContractDate,
				endContractDate,
				Collections.emptyMap(),
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
					"BASE_CGC * 4.70/100", 
					"BASE_CGP * 1.55/100",
					"BASE_CGP * 0.10/100",
					"(ART_151_CORTA_DURACION && FIN == FIN_CONTRATO && FIN == FIN_CONTRATO)? 26.57 :HIDE()",
				}, null);

		addSystemData(aonContext, contract.getStartDate(), contract.getEndDate(), 
		new HashMap<String, String>(){
			{
				put("ART_151_CORTA_DURACION", "DIAS(FIN_CONTRATO,INICIO_CONTRATO) < 30");
			}
		});

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		Date startDate = startContractDate;
		Date endDate = getLastDayOfMonth(startContractDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);

		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<>();
		calculator.setSalaryBuilder(new SalaryBuilder(){
			@Override
			public void addDeduction(Double amount, String description,
					java.util.Date start, java.util.Date end,
					IDeduction deduction,
					Map<String, ITimedVariable<?>> context) {
				super.addDeduction(amount, description, start, end, deduction, context);
				System.out.println("amount : " + amount );
			}
		});
		ISalary salary = calculator.calculate(ctx);
		
		int monthDays = get(endDate, Calendar.DAY_OF_MONTH);
		double expected = 1750.00 * 26 / monthDays;
		assertEquals(String.format("%s :", ContextVariable.TOTAL_PAYMENT), expected, salary.getTotalPayment(), DELTA);
		assertEquals(String.format("%s :", ContextVariable.CGC_BASE), expected , salary.getCommonBase(), DELTA);
		assertEquals(String.format("%s :", ContextVariable.TOTAL_LIQUID), expected - ( expected * 6.35/100.00) - 26.57 , salary.getTotalLiquid(), DELTA);
	}

	@Test
	public void testLessThan30DaysArt151II() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		Date startContractDate = add(getFirstDayOfMonth(getToday()), Calendar.DAY_OF_MONTH, 20 );
		Date endContractDate = add(startContractDate, Calendar.DAY_OF_MONTH, 25);
		
		ContractRecord contract = newContract(aonContext, 
				startContractDate,
				endContractDate,
				Collections.emptyMap(),
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
					"BASE_CGC * 4.70/100", 
					"BASE_CGP * 1.55/100",
					"BASE_CGP * 0.10/100",
					"(ART_151_CORTA_DURACION && FIN == FIN_CONTRATO)? 26.57 :HIDE()",
				}, null);

		addSystemData(aonContext, contract.getStartDate(), contract.getEndDate(), 
		new HashMap<String, String>(){
			{
				put("ART_151_CORTA_DURACION", "DIAS(FIN_CONTRATO,INICIO_CONTRATO) < 30");
			}
		});
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		Date startDate = getFirstDayOfMonth(endContractDate);
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);

		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<>();
		calculator.setSalaryBuilder(new SalaryBuilder(){
			@Override
			public void addDeduction(Double amount, String description,
					java.util.Date start, java.util.Date end,
					IDeduction deduction,
					Map<String, ITimedVariable<?>> context) {
				super.addDeduction(amount, description, start, end, deduction, context);
				System.out.println("amount : " + amount );
			}
		});
		ISalary salary = calculator.calculate(ctx);
		
		int monthDays = get(endDate, Calendar.DAY_OF_MONTH);
		int workedDays = get(endContractDate, Calendar.DAY_OF_MONTH);
		double expected = 1750.00 * workedDays / monthDays;
		org.junit.assertEquals(String.format("%s :", ContextVariable.TOTAL_PAYMENT), expected, salary.getTotalPayment(),DELTA);
		org.junit.assertEquals(String.format("%s :", ContextVariable.CGC_BASE), expected , salary.getCommonBase(),DELTA);
		org.junit.assertEquals(String.format("%s :", ContextVariable.TOTAL_LIQUID), expected - ( expected * 6.35/100.00) - 26.57 , salary.getTotalLiquid(), DELTA);

		// *************** EUKE
		// Añado DELTA al assert
		// aunque realmente no se si la filosofía de los test es que el resultado sea exactamente igual
		// en tal caso, salary.getTotalLiquid() no está redondeado. o algo así ...
		// 
		// El error era:
		// 		junit.framework.AssertionFailedError: 
		//				TOTAL_LIQUIDO : expected:<792.8675> but was:<792.8675000000001>
		assertEquals(String.format("%s :", ContextVariable.TOTAL_LIQUID), expected - ( expected * 6.35/100.00) - 26.57 , salary.getTotalLiquid(), DELTA);
	
		startDate = getFirstDayOfMonth(startContractDate);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);

		calculator = new SmartContractSalaryCalculator<>();
		calculator.setSalaryBuilder(new SalaryBuilder(){
			@Override
			public void addDeduction(Double amount, String description,
					java.util.Date start, java.util.Date end,
					IDeduction deduction,
					Map<String, ITimedVariable<?>> context) {
				super.addDeduction(amount, description, start, end, deduction, context);
				System.out.println("amount : " + amount );
			}
		});
		salary = calculator.calculate(ctx);
		
		monthDays = get(endDate, Calendar.DAY_OF_MONTH);
		workedDays = monthDays - ( get(startContractDate, Calendar.DAY_OF_MONTH) -1 );
		expected = 1750.00 * workedDays / monthDays;
		org.junit.assertEquals(String.format("%s :", ContextVariable.TOTAL_PAYMENT), expected, salary.getTotalPayment(), DELTA);
		org.junit.assertEquals(String.format("%s :", ContextVariable.CGC_BASE), expected , salary.getCommonBase(), DELTA);
		org.junit.assertEquals(String.format("%s :", ContextVariable.TOTAL_LIQUID), expected - ( expected * 6.35/100.00), salary.getTotalLiquid(), DELTA);
	}

	// ------------------------------------------------------------------------

	protected final void addDeduction(AONContext aonContext,
			ContractRecord contract, String expression, Date startDate,
			Date endDate) {
		aonContext
				.getDslContext()
				.insertInto(CONTRACT_DEDUCTION)
				.set(CONTRACT_DEDUCTION.DOMAIN, contract.getDomain())
				.set(CONTRACT_DEDUCTION.CONTRACT, contract.getId())
				.set(CONTRACT_DEDUCTION.START_DATE, contract.getStartDate())
				.set(CONTRACT_DEDUCTION.END_DATE, contract.getEndDate())
				.set(CONTRACT_DEDUCTION.EXPRESSION, expression)
				.set(CONTRACT_DEDUCTION.TYPE,
						(byte) DeductionType.OTHER.ordinal()).execute();

	}
	

	protected final void addPayment(AONContext aonContext,
			ContractRecord contract, SalaryType type,  String expression) {
		aonContext
				.getDslContext()
				.insertInto(CONTRACT_PAYMENT)
				.set(CONTRACT_PAYMENT.DOMAIN, contract.getDomain())
				.set(CONTRACT_PAYMENT.CONTRACT, contract.getId())
				.set(CONTRACT_PAYMENT.START_DATE, contract.getStartDate())
				.set(CONTRACT_PAYMENT.END_DATE, contract.getEndDate())
				.set(CONTRACT_PAYMENT.EXPRESSION, expression)
				.set(CONTRACT_PAYMENT.QUOTE_EXPRESSION, expression)
				.set(CONTRACT_PAYMENT.TYPE,
						(byte) PaymentType.CRA_0001.ordinal())
				.set(CONTRACT_PAYMENT.SALARY_TYPE,
						(byte) type.ordinal()).execute();

	}

}
