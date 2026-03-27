package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_MAX;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_MIN;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE_MAX;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE_MIN;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.bonus.IBonus;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;
import com.esferalia.aon.watson.util.AonDateUtils;


public class SQLEreForceOffTestCase extends SQLERETestCase {
	@Override
	protected ContextVariable getDaysVariable() {
		return ContextVariable.ERE_DAYS_FORCE_OFF;
	}
	@Override
	protected ContextVariable getEreVariable() {
		return ContextVariable.ERE_FORCE_OFF;
	}
	
	@Override
	protected ContextVariable getFactorVariable() {
		return ContextVariable.ERE_FACTOR_FORCE_OFF;
	}
	
	@Override
	protected ContextVariable getBaseVariable() {
		return ContextVariable.ERE_BASE_FORCE_OFF;
	}	
	@Test
	public void testEREOffI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemCosts(aonContext);
		
		addCosts(aonContext);
		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put(CGC_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGP_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { 
						"( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", 
						}
			, new String[] {
						"TRACE('DIAS_TRABAJADOS = %f\r\n', DIAS_TRABAJADOS);0.00", 
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
						"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05"}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startEre = getFirstDayOfMonth(getToday());
		//int ereDays = (int) (Math.random() * (getMax(getToday(), DAY_OF_MONTH) - 1));
		//Date endEre = add(startEre, DAY_OF_MONTH, ereDays - 1);

		addData(aonContext, contract, startEre, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.50");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext, getEreVariable().getName());
		addPayment(aonContext, contract, ere, "SELF.addBonus('ERTE EXONERACI�N DE CUOTAS','CUOTA_EMPRESARIAL * COEFICIENTE_ERE_FZA_EXONERADO');0.00" , 
		String.format("%s * BASE_REGULADORA", getDaysVariable()));
		

		Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 2);
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println("P: " + payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}
		
		for (com.esferalia.aon.payroll.SalaryCost cost : salary
				.getSalaryCosts()) {
			System.out.println(cost.getName() + " = " + cost.getAmount()
					+ " (" + cost.getExpression() + ")");
		}

		for (com.esferalia.aon.payroll.SalaryBonus bonus : salary
				.getSalaryBonus()) {
			System.out.println(bonus.getName() + " = " + bonus.getAmount()
					+ " (" + bonus.getDescription() + ")");
		}
		
		cleanSystemCosts(aonContext);

		assertEquals(
				(1750.00 * 1.10) * 0.50
						, salary.getTotalPayment(),
				DELTA);

		assertEquals(
				(1750.00 * 1.10), salary.getCommonBase(),
				DELTA);

		assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);

		assertEquals(
				salary.getTotalPayment() * ((23.6+4+1.50)/100.00) 
				, salary.getTotalEnterprise(),
				DELTA);
	}
	
	@Test
	public void testEREOffII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemCosts(aonContext);
		
		addCosts(aonContext);
		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put(CGC_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGP_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
						"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05"}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startEre = getFirstDayOfMonth(getToday()) ;
		//int ereDays = (int) (Math.random() * (getMax(getToday(), DAY_OF_MONTH) - 1));
		//Date endEre = add(startEre, DAY_OF_MONTH, ereDays - 1);

		addData(aonContext, contract, startEre, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1.00");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext, getEreVariable().getName());
		addPayment(aonContext, contract, ere, "SELF.addBonus('ERTE EXONERACI�N DE CUOTAS','CUOTA_EMPRESARIAL * COEFICIENTE_ERE_FZA_EXONERADO');0.00" , 
		String.format("%s * BASE_REGULADORA", getDaysVariable()));
		

		Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 3);
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

//		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
//				.getSalaryPayments()) {
//			System.out.println(payment.getName() + " = " + payment.getAmount()
//					+ " (" + payment.getExpression() + ")");
//		}
		
		for (com.esferalia.aon.payroll.SalaryCost cost : salary
				.getSalaryCosts()) {
			System.out.println(cost.getName() + " = " + cost.getAmount()
					+ " (" + cost.getExpression() + ")");
		}

		for (com.esferalia.aon.payroll.SalaryBonus bonus : salary
				.getSalaryBonus()) {
			System.out.println(bonus.getName() + " = " + bonus.getAmount()
					+ " (" + bonus.getDescription() + ")");
		}

		assertEquals(
				(1750.00 * 1.10) * 0.00
						, salary.getTotalPayment(),
				DELTA);

		assertEquals(
				(1750.00 * 1.10), salary.getCommonBase(),
				DELTA);

		assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);

		assertEquals(
				0.00 
				, salary.getTotalEnterprise(),
				DELTA);
	}
	
	@Test
	public void testEREOffIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemCosts(aonContext);
		
		addCosts(aonContext);
		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put(CGC_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGP_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
						"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05"}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startEre = getFirstDayOfMonth(getToday()) ;
		startEre = add(startEre, Calendar.DAY_OF_MONTH, 10);
		//int ereDays = (int) (Math.random() * (getMax(getToday(), DAY_OF_MONTH) - 1));
		//Date endEre = add(startEre, DAY_OF_MONTH, ereDays - 1);

		addData(aonContext, contract, startEre, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1.00");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext, getEreVariable().getName());
		addPayment(aonContext, contract, ere, "SELF.addBonus('ERTE EXONERACI�N DE CUOTAS','CUOTA_EMPRESARIAL * COEFICIENTE_ERE_FZA_EXONERADO');0.00" , 
		String.format("%s * BASE_REGULADORA", getDaysVariable()));
		

		Date startDate = getFirstDayOfMonth(getToday()) ;
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println("P. "+ payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}
		
		for (com.esferalia.aon.payroll.SalaryCost cost : salary
				.getSalaryCosts()) {
			System.out.println("C. "+ cost.getName() + " = " + cost.getAmount()
					+ " (" + cost.getExpression() + ")");
		}

		for (com.esferalia.aon.payroll.SalaryBonus bonus : salary
				.getSalaryBonus()) {
			System.out.println("B. "+ bonus.getName() + " = " + bonus.getAmount()
					+ " (" + bonus.getDescription() + ")");
		}
		
		int monthDays = AonDateUtils.getMax(endDate, Calendar.DAY_OF_MONTH);
		int ereDays = AonDateUtils.get(startEre, Calendar.DAY_OF_MONTH);
		int workDays = monthDays - ereDays;

		assertEquals(
				(1750.00 * 1.10) * 10 / monthDays 
				, salary.getTotalPayment(),
				DELTA);

		assertEquals(
				(1750.00 * 1.10), salary.getCommonBase(),
				DELTA);

		assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);

		assertEquals(
				salary.getTotalPayment() * ((23.6+4+1.50)/100.00) 
				, salary.getTotalEnterprise(),
				DELTA);
	}

	@Test
	public void testEREOffIV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemCosts(aonContext);
		
		addCosts(aonContext);
		
		ContractRecord contract = newContract(aonContext,
			getFirstDayOfYear(getToday()), new HashMap<String, String>() {
				{
					put(CGC_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGP_BASE_MIN.getName(), Integer.toString(Integer.MIN_VALUE));
					put(CGC_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
					put(CGP_BASE_MAX.getName(), Integer.toString(Integer.MAX_VALUE));
				}
			},
			new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
			, new String[] {
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
						"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05"}
			, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		Date startEre = getFirstDayOfMonth(getToday()) ;
		startEre = add(startEre, Calendar.DAY_OF_MONTH, 10);
		//int ereDays = (int) (Math.random() * (getMax(getToday(), DAY_OF_MONTH) - 1));
		//Date endEre = add(startEre, DAY_OF_MONTH, ereDays - 1);

		addData(aonContext, contract, startEre, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.20");
					}
				});
		
		PaymentConceptRecord ere = addConcept(aonContext, getEreVariable().getName());
		addPayment(aonContext, contract, ere, "SELF.addBonus('ERTE EXONERACI�N DE CUOTAS','CUOTA_EMPRESARIAL * COEFICIENTE_ERE_FZA_EXONERADO');0.00" , 
		String.format("%s * BASE_REGULADORA", getDaysVariable()));
		

		Date startDate = getFirstDayOfMonth(getToday()) ;
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println("P. "+ payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}
		
		for (com.esferalia.aon.payroll.SalaryCost cost : salary
				.getSalaryCosts()) {
			System.out.println("C. "+ cost.getName() + " = " + cost.getAmount()
					+ " (" + cost.getExpression() + ")");
		}

		for (com.esferalia.aon.payroll.SalaryBonus bonus : salary
				.getSalaryBonus()) {
			System.out.println("B. "+ bonus.getName() + " = " + bonus.getAmount()
					+ " (" + bonus.getDescription() + ")");
		}
		
		int monthDays = AonDateUtils.getMax(endDate, Calendar.DAY_OF_MONTH);
		int ereDays = AonDateUtils.get(startEre, Calendar.DAY_OF_MONTH);
		int workDays = monthDays - 10;

		assertEquals(
				(1750.00 * 1.10) * 10 / monthDays + (1750.00 * 1.10) * workDays / monthDays * 0.80 
				, salary.getTotalPayment(),
				DELTA);

		assertEquals(
				(1750.00 * 1.10), salary.getCommonBase(),
				DELTA);

		assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);

		assertEquals(
				salary.getTotalPayment() * ((23.6+4+1.50)/100.00) 
				, salary.getTotalEnterprise(),
				DELTA);
	}
	
	@Test
	@Disabled("'REINCORPRADO_ERE' has been deprecated")
	public void testVariableREINCORPRADO_EREI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), Collections.emptyMap());
		
		
		Date startDate = add(getFirstDayOfYear(getToday()), MONTH, 4);
		Date endDate = getLastDayOfMonth(startDate);

		Date endEREDate = add(getFirstDayOfYear(getToday()), MONTH, 4);
		endEREDate = add(endEREDate, DAY_OF_MONTH, 23);
		
		Date backDate = add(endEREDate, DAY_OF_MONTH, 1);
		
		Date startEre = add(startDate, DAY_OF_MONTH, -40 );

		addData(aonContext
				, contract
				, startEre
				, endEREDate
				, new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1");
					}
				});


		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		List<ITimedResult<Boolean>> ereBack = ctx.getExpressionContext().eval(
				"REINCORPORADO_ERE", startDate, endDate, Boolean.class);

		assertEquals(backDate, ereBack.get(0).getPeriod().getStart());
		assertEquals(Boolean.TRUE, ereBack.get(0).getValue());

	
		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ereBack = ctx.getExpressionContext().eval(
				"REINCORPORADO_ERE", startDate, endDate, Boolean.class);
		assertEquals(1, ereBack.size());
		assertEquals(startDate, ereBack.get(0).getPeriod().getStart());
		assertEquals(endDate, ereBack.get(0).getPeriod().getEnd());
		assertEquals(Boolean.TRUE, ereBack.get(0).getValue());
	}

	@Test
	@Disabled("'REINCORPRADO_ERE' has been deprecated")
	public void testVariableREINCORPRADO_EREII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), Collections.emptyMap());
		
		
		Date startDate = add(getFirstDayOfYear(getToday()), MONTH, 4);
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		try {
		List<ITimedResult<Boolean>> ereBack = ctx.getExpressionContext().eval(
				"REINCORPORADO_ERE", startDate, endDate, Boolean.class);
		fail();
		} catch ( UndefinedVariablesException e ) {
			
		}

		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		try {
		List<ITimedResult<Boolean>> ereBack = ctx.getExpressionContext().eval(
				"REINCORPORADO_ERE", startDate, endDate, Boolean.class);
		fail();
		} catch ( UndefinedVariablesException e ) {
			
		}
	}
	
	@Test
	@Disabled("'REINCORPRADO_ERE' has been deprecated")
	public void testVariableREINCORPRADO_EREIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), Collections.emptyMap());
		
		
		Date startDate = add(getFirstDayOfYear(getToday()), MONTH, 4);
		Date endDate = getLastDayOfMonth(startDate);

		Date endEREDate = add(getFirstDayOfYear(getToday()), MONTH, 4);
		endEREDate = add(endEREDate, DAY_OF_MONTH, 11);
		
		Date backDate = add(endEREDate, DAY_OF_MONTH, 1);

		Date startEre = add(startDate, DAY_OF_MONTH, -16 );
		
		addData(aonContext
				, contract
				, startEre
				, endEREDate
				, new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1");
					}
				});


		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		List<ITimedResult<Boolean>> ereBack = ctx.getExpressionContext().eval(
				"REINCORPORADO_ERE", startDate, endDate, Boolean.class);

		assertEquals(1, ereBack.size());
		assertEquals(backDate, ereBack.get(0).getPeriod().getStart());
		assertEquals(Boolean.TRUE, ereBack.get(0).getValue());
		
		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ereBack = ctx.getExpressionContext().eval(
				"REINCORPORADO_ERE", startDate, endDate, Boolean.class);
		assertEquals(1, ereBack.size());
		assertEquals(startDate, ereBack.get(0).getPeriod().getStart());
		assertEquals(endDate, ereBack.get(0).getPeriod().getEnd());
		assertEquals(Boolean.TRUE, ereBack.get(0).getValue());
		
	}
	
	@Test
	@Disabled("'REINCORPRADO_ERE' has been deprecated")
	public void testVariableREINCORPRADO_EREIV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), Collections.emptyMap());
		
		
		Date startDate = add(getFirstDayOfYear(getToday()), MONTH, 4);
		Date endDate = getLastDayOfMonth(startDate);

		Date endEREDate = add(getFirstDayOfYear(getToday()), MONTH, 4);
		endEREDate = add(endEREDate, DAY_OF_MONTH, 10);
		
		Date backDate = add(endEREDate, DAY_OF_MONTH, 1);

		addData(aonContext
				, contract
				, startDate
				, endEREDate
				, new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1");
					}
				});


		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		try {
		List<ITimedResult<Boolean>> ereBack = ctx.getExpressionContext().eval(
				"REINCORPORADO_ERE", startDate, endDate, Boolean.class);
		fail();
		} catch ( UndefinedVariablesException e ) {
			
		}
		
		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		try {
		List<ITimedResult<Boolean>> ereBack = ctx.getExpressionContext().eval(
				"REINCORPORADO_ERE", startDate, endDate, Boolean.class);
		fail();
		} catch ( UndefinedVariablesException e ) {
			
		}
		
	}

	@Test
	@Disabled("'REINCORPRADO_ERE' has been deprecated")
	public void testVariableREINCORPRADO_EREV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), Collections.emptyMap());
		
		
		Date startDate = add(getFirstDayOfYear(getToday()), MONTH, 4);
		Date endDate = getLastDayOfMonth(startDate);

		Date endEREDate = add(getFirstDayOfYear(getToday()), MONTH, 4);
		endEREDate = add(endEREDate, DAY_OF_MONTH, 11);
		
		Date backDate = add(endEREDate, DAY_OF_MONTH, 1);

		addData(aonContext
				, contract
				, startDate
				, endEREDate
				, new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1");
					}
				});

		addData(aonContext
				, contract
				, backDate
				, null
				, new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1");
					}
				});

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		try {
		List<ITimedResult<Boolean>> ereBack = ctx.getExpressionContext().eval(
				"REINCORPORADO_ERE", startDate, endDate, Boolean.class);
		fail();
		} catch ( UndefinedVariablesException e ) {
			
		}
		
		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		try {
		List<ITimedResult<Boolean>> ereBack = ctx.getExpressionContext().eval(
				"REINCORPORADO_ERE", startDate, endDate, Boolean.class);
		fail();
		} catch ( UndefinedVariablesException e ) {
			
		}
		
	}

	@Test
	@Disabled("'REINCORPRADO_ERE' has been deprecated")
	public void testVariableREINCORPRADO_EREVI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), Collections.emptyMap());
		
		
		Date startDate = add(getFirstDayOfYear(getToday()), MONTH, 4);
		Date endDate = getLastDayOfMonth(startDate);

		Date endEREDate = add(getFirstDayOfYear(getToday()), MONTH, 4);
		endEREDate = add(endEREDate, DAY_OF_MONTH, 11);
		
		Date startEre = add(startDate, DAY_OF_MONTH, -5 );

		Date backDate = add(endEREDate, DAY_OF_MONTH, 1);

		addData(aonContext
				, contract
				, startEre
				, endEREDate
				, new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1");
					}
				});

		addData(aonContext
				, contract
				, backDate
				, null
				, new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.9");
					}
				});

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		List<ITimedResult<Boolean>> ereBack = ctx.getExpressionContext().eval(
				"REINCORPORADO_ERE", startDate, endDate, Boolean.class);

		assertEquals(1, ereBack.size());
		assertEquals(backDate, ereBack.get(0).getPeriod().getStart());
		assertEquals(Boolean.TRUE, ereBack.get(0).getValue());
		
		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ereBack = ctx.getExpressionContext().eval(
				"REINCORPORADO_ERE", startDate, endDate, Boolean.class);
		assertEquals(1, ereBack.size());
		assertEquals(startDate, ereBack.get(0).getPeriod().getStart());
		assertEquals(endDate, ereBack.get(0).getPeriod().getEnd());
		assertEquals(Boolean.TRUE, ereBack.get(0).getValue());
	}

	@Test
	@Disabled("'REINCORPRADO_ERE' has been deprecated")
	public void testVariableREINCORPRADO_EREVII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), Collections.emptyMap());
		
		
		Date startDate = add(getFirstDayOfYear(getToday()), MONTH, 4);
		Date endDate = getLastDayOfMonth(startDate);

		Date endEREDate = add(getFirstDayOfYear(getToday()), MONTH, 4);
		endEREDate = add(endEREDate, DAY_OF_MONTH, 11);
		
		Date backDate = add(endEREDate, DAY_OF_MONTH, 1);

		Date startEre = add(startDate, MONTH, -1 );
		
		addData(aonContext
				, contract
				, startEre
				, endEREDate
				, new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1");
					}
				});

		addData(aonContext
				, contract
				, backDate
				, endDate
				, new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.9");
					}
				});

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		List<ITimedResult<Boolean>> ereBack = ctx.getExpressionContext().eval(
				"REINCORPORADO_ERE", startDate, endDate, Boolean.class);
		assertEquals(1, ereBack.size());
		assertEquals(backDate, ereBack.get(0).getPeriod().getStart());
		assertEquals(Boolean.TRUE, ereBack.get(0).getValue());

	}

	@Test
	@Disabled("'REINCORPRADO_ERE' has been deprecated")
	public void testVariableREINCORPRADO_EREVIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), Collections.emptyMap());
		
		
		Date startDate = add(getFirstDayOfYear(getToday()), MONTH, 4);
		Date endDate = getLastDayOfMonth(startDate);

		Date endEREDate = add(getFirstDayOfYear(getToday()), MONTH, 4);
		endEREDate = add(endEREDate, DAY_OF_MONTH, 11);
		
		Date startEre = add(startDate, DAY_OF_MONTH, -22 );

		Date backDate = add(endEREDate, DAY_OF_MONTH, 1);

		addData(aonContext
				, contract
				, startEre
				, endEREDate
				, new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.5");
					}
				});


		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		List<ITimedResult<Boolean>> ereBack = ctx.getExpressionContext().eval(
				"REINCORPORADO_ERE", startDate, endDate, Boolean.class);

		assertEquals(1, ereBack.size());
		assertEquals(backDate, ereBack.get(0).getPeriod().getStart());
		assertEquals(Boolean.TRUE, ereBack.get(0).getValue());
		
		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ereBack = ctx.getExpressionContext().eval(
				"REINCORPORADO_ERE", startDate, endDate, Boolean.class);
		assertEquals(1, ereBack.size());
		assertEquals(startDate, ereBack.get(0).getPeriod().getStart());
		assertEquals(endDate, ereBack.get(0).getPeriod().getEnd());
		assertEquals(Boolean.TRUE, ereBack.get(0).getValue());
	}

	@Test
	@Disabled("'REINCORPRADO_ERE' has been deprecated")
	public void testVariableREINCORPRADO_EREIX() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), Collections.emptyMap());
		
		
		Date startDate = add(getFirstDayOfYear(getToday()), MONTH, 4);
		Date endDate = getLastDayOfMonth(startDate);

		Date endEREDate = add(getFirstDayOfYear(getToday()), MONTH, 4);
		endEREDate = add(endEREDate, DAY_OF_MONTH, 11);
		
		Date startEre = add(startDate, MONTH, -2 );

		Date backDate = add(endEREDate, DAY_OF_MONTH, 1);

		addData(aonContext
				, contract
				, startEre
				, endEREDate
				, new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.75");
					}
				});

		addData(aonContext
				, contract
				, backDate
				, null
				, new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.50");
					}
				});

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		List<ITimedResult<Boolean>> ereBack = ctx.getExpressionContext().eval(
				"REINCORPORADO_ERE", startDate, endDate, Boolean.class);

		assertEquals(1, ereBack.size());
		assertEquals(backDate, ereBack.get(0).getPeriod().getStart());
		assertEquals(Boolean.TRUE, ereBack.get(0).getValue());
		
		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ereBack = ctx.getExpressionContext().eval(
				"REINCORPORADO_ERE", startDate, endDate, Boolean.class);
		assertEquals(1, ereBack.size());
		assertEquals(startDate, ereBack.get(0).getPeriod().getStart());
		assertEquals(endDate, ereBack.get(0).getPeriod().getEnd());
		assertEquals(Boolean.TRUE, ereBack.get(0).getValue());
	}

	@Test
	@Disabled("'REINCORPRADO_ERE' has been deprecated")
	public void testVariableREINCORPRADO_EREX() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), Collections.emptyMap());
		
		
		Date startDate = add(getFirstDayOfYear(getToday()), MONTH, 4);
		Date endDate = getLastDayOfMonth(startDate);

		Date endEREDate = add(getFirstDayOfYear(getToday()), MONTH, 4);
		endEREDate = add(endEREDate, DAY_OF_MONTH, 11);
		
		Date backDate = add(endEREDate, DAY_OF_MONTH, 1);

		addData(aonContext
				, contract
				, startDate
				, endEREDate
				, new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.50");
					}
				});

		addData(aonContext
				, contract
				, backDate
				, null
				, new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.50");
					}
				});

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		try {
		List<ITimedResult<Boolean>> ereBack = ctx.getExpressionContext().eval(
				"REINCORPORADO_ERE", startDate, endDate, Boolean.class);
		fail();
		} catch ( UndefinedVariablesException e ) {
			
		}
		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		try {
		List<ITimedResult<Boolean>> ereBack = ctx.getExpressionContext().eval(
				"REINCORPORADO_ERE", startDate, endDate, Boolean.class);
		fail();
		} catch ( UndefinedVariablesException e ) {
			
		}
	}

	@Test
	public void testVariableERE_TOTALI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), Collections.emptyMap());

		Date ereDay = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH, 10);

		addData(aonContext, contract, ereDay, ereDay,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1");
					}
				});

		Date startDate = getFirstDayOfMonth(ereDay);
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		List<ITimedResult<Boolean>> fullEre = ctx.getExpressionContext().eval(
				"ERE_TOTAL", startDate, endDate, Boolean.class);

		assertEquals(1, fullEre.size());
		assertEquals(ereDay, fullEre.get(0).getPeriod().getStart());
		assertEquals(ereDay, fullEre.get(0).getPeriod().getEnd());
		assertEquals(Boolean.TRUE, fullEre.get(0).getValue());
	}

	@Test
	public void testVariableERE_TOTALII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), Collections.emptyMap());

		Date startDate = add(getFirstDayOfYear(getToday()), MONTH, 4);
		Date endDate = getLastDayOfMonth(startDate);

		addData(aonContext, contract, startDate, endDate,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1.00");
					}
				});


		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		List<ITimedResult<Boolean>> fullEre = ctx.getExpressionContext().eval(
				"ERE_TOTAL", startDate, endDate, Boolean.class);

		assertEquals(1, fullEre.size());
		assertEquals(startDate, fullEre.get(0).getPeriod().getStart());
		assertEquals(endDate, fullEre.get(0).getPeriod().getEnd());
		assertEquals(Boolean.TRUE, fullEre.get(0).getValue());

	}

	@Test
	public void testVariableERE_TOTALIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), Collections.emptyMap());

		Date startDate = add(getFirstDayOfYear(getToday()), MONTH, 4);
		Date endDate = getLastDayOfMonth(startDate);

		new Period(startDate, endDate).daysStream().forEach(c -> {
		Date day = new java.sql.Date(c.getTimeInMillis());
		addData(aonContext, contract, day , day,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1.00");
					}
				});
		});


		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		List<ITimedResult<Boolean>> fullEre = ctx.getExpressionContext().eval(
				"ERE_TOTAL", startDate, endDate, Boolean.class);

		assertEquals(1, fullEre.size());
		assertEquals(startDate, fullEre.get(0).getPeriod().getStart());
		assertEquals(endDate, fullEre.get(0).getPeriod().getEnd());
		assertEquals(Boolean.TRUE, fullEre.get(0).getValue());
	}

	@Test
	public void testVariableERE_TOTALIV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), Collections.emptyMap());

		Date startDate = add(getFirstDayOfYear(getToday()), MONTH, 4);
		Date endDate = getLastDayOfMonth(startDate);

		new Period(startDate, endDate).daysStream()
		.filter(c -> c.get(Calendar.DAY_OF_MONTH) != 13 ).forEach(c -> {
		Date day = new java.sql.Date(c.getTimeInMillis());
		addData(aonContext, contract, day , day,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1.00");
					}
				});
		});


		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		List<ITimedResult<Boolean>> fullEre = ctx.getExpressionContext().eval(
				"ERE_TOTAL", startDate, endDate, Boolean.class);
		
		Date backDate = add(startDate, DAY_OF_MONTH, 11);
		assertEquals(2, fullEre.size());
		assertEquals(startDate, fullEre.get(0).getPeriod().getStart());
		assertEquals(backDate, fullEre.get(0).getPeriod().getEnd());
		assertEquals(Boolean.TRUE, fullEre.get(0).getValue());

		backDate = add(startDate, DAY_OF_MONTH, 13);
		assertEquals(backDate, fullEre.get(1).getPeriod().getStart());
		assertEquals(endDate, fullEre.get(1).getPeriod().getEnd());
		assertEquals(Boolean.FALSE, fullEre.get(1).getValue());
	}

	@Test
	public void testVariableERE_TOTALV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), Collections.emptyMap());

		Date startDate = add(getFirstDayOfYear(getToday()), MONTH, 4);
		Date endDate = getLastDayOfMonth(startDate);

		new Period(startDate, endDate).daysStream()
		.filter(c -> c.get(Calendar.DAY_OF_MONTH) != 13 ).forEach(c -> {
		Date day = new java.sql.Date(c.getTimeInMillis());
		addData(aonContext, contract, day , day,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1.00");
					}
				});
		});


		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		List<ITimedResult<Boolean>> fullEre = ctx.getExpressionContext().eval(
				"ERE_TOTAL", startDate, endDate, Boolean.class);
		Date backDate = add(startDate, DAY_OF_MONTH, 11);
		assertEquals(2, fullEre.size());
		assertEquals(startDate, fullEre.get(0).getPeriod().getStart());
		assertEquals(backDate, fullEre.get(0).getPeriod().getEnd());
		assertEquals(Boolean.TRUE, fullEre.get(0).getValue());

		backDate = add(startDate, DAY_OF_MONTH, 13);
		assertEquals(backDate, fullEre.get(1).getPeriod().getStart());
		assertEquals(endDate, fullEre.get(1).getPeriod().getEnd());
		assertEquals(Boolean.FALSE, fullEre.get(1).getValue());


		Date _13DayOfMonth = add(startDate, Calendar.DAY_OF_MONTH, 12);
		fullEre = ctx.getExpressionContext().eval(
				"ERE_TOTAL", add(_13DayOfMonth, Calendar.DAY_OF_MONTH,1), endDate, Boolean.class);

		assertEquals(1, fullEre.size());
		assertEquals(backDate, fullEre.get(0).getPeriod().getStart());
		assertEquals(endDate, fullEre.get(0).getPeriod().getEnd());
		assertEquals(Boolean.FALSE, fullEre.get(0).getValue());
	}

	@Test
	public void testVariableERE_TOTALVI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), Collections.emptyMap());

		Date startDate = add(getFirstDayOfYear(getToday()), MONTH, 4);
		Date endDate = getLastDayOfMonth(startDate);

		new Period(startDate, endDate).daysStream()
		.forEach(c -> {
		Date day = new java.sql.Date(c.getTimeInMillis());
		addData(aonContext, contract, day , day,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), c.get(Calendar.DAY_OF_MONTH) != 13 ? "1.00" : "0.97");
					}
				});
		});


		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		Date _13DayOfMonth = add(startDate, Calendar.DAY_OF_MONTH, 12);
		List<ITimedResult<Boolean>> fullEre = ctx.getExpressionContext().eval(
				"ERE_TOTAL", startDate, endDate, Boolean.class);

		Date backDate = add(startDate, DAY_OF_MONTH, 11);
		assertEquals(3, fullEre.size());
		assertEquals(startDate, fullEre.get(0).getPeriod().getStart());
		assertEquals(backDate, fullEre.get(0).getPeriod().getEnd());
		assertEquals(Boolean.TRUE, fullEre.get(0).getValue());

		backDate = add(startDate, DAY_OF_MONTH, 12);
		assertEquals(backDate, fullEre.get(1).getPeriod().getStart());
		assertEquals(backDate, fullEre.get(1).getPeriod().getEnd());
		assertEquals(Boolean.FALSE, fullEre.get(1).getValue());
		
		backDate = add(startDate, DAY_OF_MONTH, 13);
		assertEquals(backDate, fullEre.get(2).getPeriod().getStart());
		assertEquals(endDate, fullEre.get(2).getPeriod().getEnd());
		assertEquals(Boolean.FALSE, fullEre.get(2).getValue());


		fullEre = ctx.getExpressionContext().eval(
				"ERE_TOTAL", add(_13DayOfMonth, Calendar.DAY_OF_MONTH,1), endDate, Boolean.class);

		backDate = add(startDate, DAY_OF_MONTH, 13);
		assertEquals(backDate, fullEre.get(0).getPeriod().getStart());
		assertEquals(endDate, fullEre.get(0).getPeriod().getEnd());
		assertEquals(Boolean.FALSE, fullEre.get(0).getValue());
		
	}

	@Test
	public void testVariableERE_TOTALVII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), Collections.emptyMap());

		Date startDate = add(getFirstDayOfYear(getToday()), MONTH, 4);
		Date endDate = getLastDayOfMonth(startDate);

		addData(aonContext, contract, startDate, endDate,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1.00");
					}
				});


		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		List<ITimedResult<Boolean>> fullEre = ctx.getExpressionContext().eval(
				"ERE_TOTAL", startDate, endDate, Boolean.class);

		assertEquals(1, fullEre.size());
		assertEquals(startDate, fullEre.get(0).getPeriod().getStart());
		assertEquals(endDate, fullEre.get(0).getPeriod().getEnd());
		assertEquals(Boolean.TRUE, fullEre.get(0).getValue());
		
		ContractRecord contractII =
		newContract(aonContext, 
				SSRegimeType.GENERAL, //ssRegimeType, 
				CCCType.PRINCIPAL, 
				contract.getStartDate(), 
				null, 
				Collections.emptyMap(), 
				new String [] {}, 
				new String [] {}, 
				null, //category, 
				contract.getDomain(), //domainId, 
				contract.getPerson(), //personId, 
				contract.getWorkplace(), //workplaceId, 
				contract.getEnterpriseCcc(), //enterpriseCccId, 
				contract.getEnterpriseActivity() //enterpriseActivityId
				);
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		fullEre = ctx.getExpressionContext().eval(
				"ERE_TOTAL", startDate, endDate, Boolean.class);

		assertEquals(1, fullEre.size());
		assertEquals(startDate, fullEre.get(0).getPeriod().getStart());
		assertEquals(endDate, fullEre.get(0).getPeriod().getEnd());
		assertEquals(Boolean.TRUE, fullEre.get(0).getValue());		
		
		addData(aonContext, contractII, startDate, add(startDate, Calendar.DAY_OF_MONTH, 11),
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1.00");
					}
				});
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		fullEre = ctx.getExpressionContext().eval(
				"ERE_TOTAL", startDate, endDate, Boolean.class);

		assertEquals(2, fullEre.size());
		assertEquals(Boolean.TRUE, fullEre.get(0).getValue());
		assertEquals(Boolean.FALSE, fullEre.get(1).getValue());

		addData(aonContext, contractII, add(startDate, Calendar.DAY_OF_MONTH, 12), endDate,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1.00");
					}
				});
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		fullEre = ctx.getExpressionContext().eval(
				"ERE_TOTAL", startDate, endDate, Boolean.class);

		assertEquals(1, fullEre.size());
		assertEquals(startDate, fullEre.get(0).getPeriod().getStart());
		assertEquals(endDate, fullEre.get(0).getPeriod().getEnd());
		assertEquals(Boolean.TRUE, fullEre.get(0).getValue());
		
		ContractRecord contracts [] = new  ContractRecord [33];
		
		for ( int i = 0; i < 33; i ++ ) {
			contracts[i]=
					newContract(aonContext, 
							SSRegimeType.GENERAL, //ssRegimeType, 
							CCCType.PRINCIPAL, 
							contract.getStartDate(), 
							null, 
							Collections.emptyMap(), 
							new String [] {}, 
							new String [] {}, 
							null, //category, 
							contract.getDomain(), //domainId, 
							contract.getPerson(), //personId, 
							contract.getWorkplace(), //workplaceId, 
							contract.getEnterpriseCcc(), //enterpriseCccId, 
							contract.getEnterpriseActivity() //enterpriseActivityId
							);
			addData(aonContext, contracts[i], startDate, endDate,
					new HashMap<String, String>() {
						{
							put(getFactorVariable().getName(), "1.00");
						}
					});			
		}
		fullEre = ctx.getExpressionContext().eval(
				"ERE_TOTAL", startDate, endDate, Boolean.class);

		assertEquals(1, fullEre.size());
		assertEquals(startDate, fullEre.get(0).getPeriod().getStart());
		assertEquals(endDate, fullEre.get(0).getPeriod().getEnd());
		assertEquals(Boolean.TRUE, fullEre.get(0).getValue());
		
		addData(aonContext, contracts[22], startDate, endDate,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.30");
					}
				});			
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		fullEre = ctx.getExpressionContext().eval(
				"ERE_TOTAL", startDate, endDate, Boolean.class);
		assertEquals(1, fullEre.size());
		assertEquals(startDate, fullEre.get(0).getPeriod().getStart());
		assertEquals(endDate, fullEre.get(0).getPeriod().getEnd());
		assertEquals(Boolean.TRUE, fullEre.get(0).getValue());

		ContractRecord contractIII =
		newContract(aonContext, 
				SSRegimeType.GENERAL, //ssRegimeType, 
				CCCType.PRINCIPAL, 
				contract.getStartDate(), 
				null, 
				Collections.emptyMap(), 
				new String [] {}, 
				new String [] {}, 
				null, //category, 
				contract.getDomain(), //domainId, 
				contract.getPerson(), //personId, 
				contract.getWorkplace(), //workplaceId, 
				contract.getEnterpriseCcc(), //enterpriseCccId, 
				contract.getEnterpriseActivity() //enterpriseActivityId
				);
		addData(aonContext
				, contractIII
				, startDate
				, add(startDate, DAY_OF_MONTH, 10)
				, new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.30");
					}
				});			
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		fullEre = ctx.getExpressionContext().eval(
				"ERE_TOTAL", startDate, endDate, Boolean.class);

		assertEquals(2, fullEre.size());
		assertEquals(startDate, fullEre.get(0).getPeriod().getStart());
		assertEquals( add(startDate, DAY_OF_MONTH, 10), fullEre.get(0).getPeriod().getEnd());
		assertEquals(Boolean.TRUE, fullEre.get(0).getValue());
		assertEquals(add(startDate, DAY_OF_MONTH, 11), fullEre.get(1).getPeriod().getStart());
		assertEquals( endDate, fullEre.get(1).getPeriod().getEnd());
		assertEquals(Boolean.FALSE, fullEre.get(1).getValue());		
	}

	@Test
	public void testVariableERE_TOTALVIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), Collections.emptyMap());

		Date startDate = add(getFirstDayOfYear(getToday()), MONTH, 4);
		Date endDate = getLastDayOfMonth(startDate);

		addData(aonContext, contract, startDate, endDate,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1.00");
					}
				});


		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		List<ITimedResult<Boolean>> fullEre = ctx.getExpressionContext().eval(
				"ERE_TOTAL", startDate, endDate, Boolean.class);

		assertEquals(1, fullEre.size());
		assertEquals(startDate, fullEre.get(0).getPeriod().getStart());
		assertEquals(endDate, fullEre.get(0).getPeriod().getEnd());
		assertEquals(Boolean.TRUE, fullEre.get(0).getValue());
		
		ContractRecord contractII =
		newContract(aonContext, 
				SSRegimeType.GENERAL, //ssRegimeType, 
				CCCType.PRINCIPAL, 
				contract.getStartDate(), 
				null, 
				Collections.emptyMap(), 
				new String [] {}, 
				new String [] {}, 
				null, //category, 
				contract.getDomain(), //domainId, 
				contract.getPerson(), //personId, 
				contract.getWorkplace(), //workplaceId, 
				contract.getEnterpriseCcc(), //enterpriseCccId, 
				contract.getEnterpriseActivity() //enterpriseActivityId
				);
		
		 ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
		 
		 fullEre = ctx.getExpressionContext().eval(
				"ERE_TOTAL", startDate, endDate, Boolean.class);

		assertEquals(1, fullEre.size());
		assertEquals(startDate, fullEre.get(0).getPeriod().getStart());
		assertEquals(endDate, fullEre.get(0).getPeriod().getEnd());
		assertEquals(Boolean.TRUE, fullEre.get(0).getValue());	
		
		addData(aonContext, contractII, startDate, add(startDate, Calendar.DAY_OF_MONTH, 11),
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1.00");
					}
				});
		
		 ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
		 
		fullEre = ctx.getExpressionContext().eval(
				"ERE_TOTAL", startDate, endDate, Boolean.class);

		assertEquals(2, fullEre.size());
		assertEquals(startDate, fullEre.get(0).getPeriod().getStart());
		assertEquals(Boolean.TRUE, fullEre.get(0).getValue());

		assertEquals(endDate, fullEre.get(1).getPeriod().getEnd());
		assertEquals(Boolean.FALSE, fullEre.get(1).getValue());

		addData(aonContext, contractII, add(startDate, Calendar.DAY_OF_MONTH, 12), endDate,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1.00");
					}
				});
		
		 ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
		 
		fullEre = ctx.getExpressionContext().eval(
				"ERE_TOTAL", startDate, endDate, Boolean.class);

		assertEquals(1, fullEre.size());

		assertEquals(Boolean.TRUE, fullEre.get(0).getValue());
		ContractRecord contracts [] = new  ContractRecord [33];
		
		for ( int i = 0; i < 33; i ++ ) {
			contracts[i]=
					newContract(aonContext, 
							SSRegimeType.GENERAL, //ssRegimeType, 
							CCCType.PRINCIPAL, 
							contract.getStartDate(), 
							null, 
							Collections.emptyMap(), 
							new String [] {}, 
							new String [] {}, 
							null, //category, 
							contract.getDomain(), //domainId, 
							contract.getPerson(), //personId, 
							contract.getWorkplace(), //workplaceId, 
							contract.getEnterpriseCcc(), //enterpriseCccId, 
							contract.getEnterpriseActivity() //enterpriseActivityId
							);
			addData(aonContext, contracts[i], startDate, endDate,
					new HashMap<String, String>() {
						{
							put(getFactorVariable().getName(), "0.50");
						}
					});			
		}
		fullEre = ctx.getExpressionContext().eval(
				"ERE_TOTAL", startDate, endDate, Boolean.class);

		assertEquals(1, fullEre.size());

		assertEquals(Boolean.TRUE, fullEre.get(0).getValue());
		
		addData(aonContext, contracts[22], startDate, endDate,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.30");
					}
				});			
		fullEre = ctx.getExpressionContext().eval(
				"ERE_TOTAL", startDate, endDate, Boolean.class);
		assertEquals(1, fullEre.size());
		assertEquals(Boolean.TRUE, fullEre.get(0).getValue());
	}

	@Test
	public void testNewBonusI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemCosts(aonContext);
		cleanSystemData(aonContext);
		addSSRegimeData(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), null,
				new HashMap<String, String>() {
					{
						put("PORCENTAJE_CGC_E", "23.60");
					}
				});

		addSSRegimeCost(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), "CGC_E",
				DeductionType.COMMON_CONTINGENCY,
				"BASE_CGC_E * PORCENTAJE_CGC_E/100");
		
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), new HashMap<String, String>() {
					{
					}
				},
				new String[] { "( P_1 + P_2 )* 0.10 ",
							"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
							"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
				, new String[] {
							"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
							"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05"}
				, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		

		Date startDate = add(getFirstDayOfYear(getToday()), MONTH, 4);
		Date endDate = getLastDayOfMonth(startDate);
		
		PaymentConceptRecord ere = addConcept(aonContext, getEreVariable().getName());
		addPayment(aonContext, 
				contract, ere, 
				String.format("isdef %1$s ? (SELF.addBonus('EXPDTE. REG. DE EMPL. FZA. MAYOR EXONERADO','CUOTA_EMPRESARIAL * %1$s * (isdef PORCENTAJE_EXONERADO ? PORCENTAJE_EXONERADO : 100.0)/100.0');0.0) : HIDE()" , getFactorVariable().getName() ), 
				String.format("%s * BASE_REGULADORA",getDaysVariable().getName()));
		
		addSystemData(aonContext
		, startDate
		, add(startDate, Calendar.DAY_OF_MONTH, 12)
		, new HashMap<String, String>(){{
			put("PORCENTAJE_EXONERADO", "100.00");
		}});
		
		addSystemData(aonContext
		, add(startDate, Calendar.DAY_OF_MONTH, 13)
		,endDate
		, new HashMap<String, String>(){{
			put("PORCENTAJE_EXONERADO", "ERE_TOTAL ? 100.00 : 60.00");
		}});

//		addBonus(aonContext, 
//		contract, addBonusConcept(aonContext, BonusType.ERE, ""),
//		String.format("_FRACC(CONTEXT,'CUOTA_EMPRESARIAL')* %s *(isdef PORCENTAJE_EXONERADO ? PORCENTAJE_EXONERADO/100:1)", getFactorVariable().getName())
//		);
		addData(aonContext, contract, startDate, endDate,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1");
					}
				});

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println("_P " + payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getQuote() + ")");
		}
		
		double costs = 0.00;
		for (com.esferalia.aon.payroll.SalaryCost cost : salary
				.getSalaryCosts()) {
			System.out.println("_C " +cost.getName() + " = " + cost.getAmount()
					);
			costs += cost.getAmount();
		}
		
		double bonuses = 0.00;
		for (com.esferalia.aon.payroll.SalaryBonus bonus : salary
				.getSalaryBonus()) {
			System.out.println("_B " + bonus.getName() + " = " + bonus.getAmount()
					);
			bonuses += bonus.getAmount();
		}
		
		org.junit.assertEquals(costs, bonuses, DELTA);
		
		ContractRecord contractII =
		newContract(aonContext, 
				SSRegimeType.GENERAL, //ssRegimeType, 
				CCCType.PRINCIPAL, 
				contract.getStartDate(), 
				null, 
				Collections.emptyMap(), 
				new String[] { "( P_1 + P_2 )* 0.10 ",
				"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
				"250.00 * DIAS_TRABAJADOS / DIAS_MES", },
			 	new String[] {
				"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
				"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05"},
				null, //category, 
				contract.getDomain(), //domainId, 
				contract.getPerson(), //personId, 
				contract.getWorkplace(), //workplaceId, 
				contract.getEnterpriseCcc(), //enterpriseCccId, 
				contract.getEnterpriseActivity() //enterpriseActivityId
				);
		
		addData(aonContext, contractII, startDate, add(startDate, DAY_OF_MONTH, 10),
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1");
					}
				});
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);
		
		bonuses = 0.00;
		for (com.esferalia.aon.payroll.SalaryBonus bonus : salary
				.getSalaryBonus()) {
			System.out.println(bonus.getName() + " = " + bonus.getAmount()
					);
			bonuses += bonus.getAmount();
		}
		
		int monthDays = get(endDate, DAY_OF_MONTH);
		double expected = costs / monthDays * 13 + costs / monthDays * ( monthDays - 13 ) * 0.60;
		org.junit.assertEquals(expected, bonuses, DELTA);
		
		addData(aonContext, contractII, startDate, endDate,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.6");
					}
				});

		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);
		
		bonuses = 0.00;
		for (com.esferalia.aon.payroll.SalaryBonus bonus : salary
				.getSalaryBonus()) {
			System.out.println(bonus.getName() + " = " + bonus.getAmount()
					);
			bonuses += bonus.getAmount();
		}
		
		org.junit.assertEquals(costs, bonuses, DELTA);

		addData(aonContext, contractII, startDate, endDate,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1.0");
					}
				});

		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);
		
		bonuses = 0.00;
		for (com.esferalia.aon.payroll.SalaryBonus bonus : salary
				.getSalaryBonus()) {
			System.out.println(bonus.getName() + " = " + bonus.getAmount()
					);
			bonuses += bonus.getAmount();
		}
		
		org.junit.assertEquals(costs, bonuses, DELTA);
	}
	
	@Test
	public void testNewBonusII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemCosts(aonContext);
		cleanSystemData(aonContext);
		addSSRegimeData(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), null,
				new HashMap<String, String>() {
					{
						put("PORCENTAJE_CGC_E", "23.60");
					}
				});

		addSSRegimeCost(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), "CGC_E",
				DeductionType.COMMON_CONTINGENCY,
				"BASE_CGC_E * PORCENTAJE_CGC_E/100");
		
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), new HashMap<String, String>() {
					{
					}
				},
				new String[] { "( P_1 + P_2 )* 0.10 ",
							"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
							"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
				, new String[] {
							"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
							"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05"}
				, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		

		Date startDate = add(getFirstDayOfYear(getToday()), MONTH, 4);
		Date endDate = getLastDayOfMonth(startDate);
		
		PaymentConceptRecord ere = addConcept(aonContext, getEreVariable().getName());
		addPayment(aonContext, 
				contract, ere, 
				String.format("isdef %1$s ? (SELF.addBonus('EXPDTE. REG. DE EMPL. FZA. MAYOR EXONERADO','CUOTA_EMPRESARIAL * %1$s * (isdef PORCENTAJE_EXONERADO ? PORCENTAJE_EXONERADO : 100.0)/100.0');0.0) : HIDE()" , getFactorVariable().getName() ), 
				String.format("%s * BASE_REGULADORA",getDaysVariable().getName()));
		
		addSystemData(aonContext
		, startDate
		, add(startDate, Calendar.DAY_OF_MONTH, 12)
		, new HashMap<String, String>(){{
			put("PORCENTAJE_EXONERADO", "100.00");
		}});
		
		addSystemData(aonContext
		, add(startDate, Calendar.DAY_OF_MONTH, 13)
		,endDate
		, new HashMap<String, String>(){{
			put("PORCENTAJE_EXONERADO", "ERE_TOTAL ? 100.00 : 60.00");
		}});

//		addBonus(aonContext, 
//		contract, addBonusConcept(aonContext, BonusType.ERE, ""),
//		String.format("_FRACC(CONTEXT,'CUOTA_EMPRESARIAL')* %s *(isdef PORCENTAJE_EXONERADO ? PORCENTAJE_EXONERADO/100:1)", getFactorVariable().getName())
//		);
		addData(aonContext, contract, startDate, add(startDate, Calendar.DAY_OF_MONTH, 15),
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1.0");
					}
				});
		addData(aonContext, contract, add(startDate, Calendar.DAY_OF_MONTH, 16), endDate,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1.0");
					}
				});

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		double bonuses [] = {0.00};
		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
					@Override
					public void addBonus(Double amount, String description, java.util.Date startDate,
							java.util.Date endDate, IBonus bonus, Map<String, ITimedVariable<?>> context) {
						// TODO Auto-generated method stub
						System.out.println("_B " + bonus.getName() + " = " + amount
								+ ", " + startDate + ".." + endDate );
						bonuses[0] += amount;
						super.addBonus(amount, description, startDate, endDate, bonus, context);
					}
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println("_P " + payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getQuote() + ")");
		}
		
		double costs = 0.00;
		for (com.esferalia.aon.payroll.SalaryCost cost : salary
				.getSalaryCosts()) {
			System.out.println("_C " +cost.getName() + " = " + cost.getAmount()
					);
			costs += cost.getAmount();
		}
		
//		for (com.esferalia.aon.payroll.SalaryBonus bonus : salary
//				.getSalaryBonus()) {
//			System.out.println("_B " + bonus.getName() + " = " + bonus.getAmount()
//					);
//			bonuses += bonus.getAmount();
//		}
		
		org.junit.assertEquals(costs, bonuses[0], DELTA);
		
	}

	@Test
	@Disabled("'REINCORPRADO_ERE' has been deprecated")
	public void testBackBonusI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemCosts(aonContext);
		cleanSystemData(aonContext);
		addSSRegimeData(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), null,
				new HashMap<String, String>() {
					{
						put("PORCENTAJE_CGC_E", "23.60");
					}
				});

		addSSRegimeCost(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), "CGC_E",
				DeductionType.COMMON_CONTINGENCY,
				"BASE_CGC_E * PORCENTAJE_CGC_E/100");
		
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), new HashMap<String, String>() {
					{
					}
				},
				new String[] { "( P_1 + P_2 )* 0.10 ",
							"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
							"250.00 * DIAS_TRABAJADOS / DIAS_MES", }
				, new String[] {
							"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10", 
							"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05"}
				, newAgreement(aonContext, new Extra[]{}, Collections.emptyMap()));

		

		Date startDate = add(getFirstDayOfYear(getToday()), MONTH, 4);
		Date endDate = getLastDayOfMonth(startDate);
		
		PaymentConceptRecord ere = addConcept(aonContext, getEreVariable().getName());
		addPayment(aonContext, 
				contract, ere, 
				String.format("isdef %1$s ? (SELF.addBonus('EXPDTE. REG. DE EMPL. FZA. MAYOR EXONERADO I','_FRACC(CONTEXT,\"CUOTA_EMPRESARIAL\") * %1$s * (isdef PORCENTAJE_EXONERADO ? PORCENTAJE_EXONERADO : 100.0)/100.0');0.0) : HIDE()" , getFactorVariable().getName() ), 
				String.format("%s * BASE_REGULADORA",getDaysVariable().getName()));
		
		addPayment(
				aonContext 
				, contract
				, ere
				, String.format("REINCORPORADO_ERE ? (SELF.addBonus('EXPDTE. REG. DE EMPL. FZA. MAYOR EXONERADO II','_FRACC(CONTEXT,\"CUOTA_EMPRESARIAL\") * DIAS_TRABAJADOS/DIAS_COTIZADOS * (isdef PORCENTAJE_REINCORPORACION ? PORCENTAJE_REINCORPORACION : 75.0)/100.0');HIDE()) : HIDE()" , getFactorVariable().getName() ), 
				"");

//		addSystemData(aonContext
//		, startDate
//		, add(startDate, Calendar.DAY_OF_MONTH, 11)
//		, new HashMap<String, String>(){{
//			put("PORCENTAJE_EXONERADO", "100.00");
//		}});
		
		addSystemData(aonContext
//		, add(startDate, Calendar.DAY_OF_MONTH, 12)
		, startDate
		,endDate
		, new HashMap<String, String>(){{
			put("PORCENTAJE_EXONERADO", "ERE_TOTAL ? 100.00 : 60.00");
		}});

		Date endEREDate = add(getFirstDayOfYear(getToday()), MONTH, 4);
		endEREDate = add(endEREDate, DAY_OF_MONTH, 11);
		
		Date startEre = add(startDate, DAY_OF_MONTH, -11);
		
		Date backDate = add(endEREDate, DAY_OF_MONTH, 1);

		addData(aonContext, contract, startEre, endEREDate,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "1");
					}
				});

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println("_P " + payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getQuote() + ")");
		}
		
		double costs = 0.00;
		for (com.esferalia.aon.payroll.SalaryCost cost : salary
				.getSalaryCosts()) {
			System.out.println("_C " +cost.getName() + " = " + cost.getAmount()
					);
			costs += cost.getAmount();
		}
		
		double bonuses = 0.00;
		for (com.esferalia.aon.payroll.SalaryBonus bonus : salary
				.getSalaryBonus()) {
			System.out.println("_B " + bonus.getName() + " = " + bonus.getAmount()
					);
			bonuses += bonus.getAmount();
		}
		
		int monthDays = get(endDate, DAY_OF_MONTH);
		double off = costs / monthDays * 12 + costs / monthDays * (monthDays -12 ) * 0.75 ;
		org.junit.assertEquals(off, bonuses, DELTA);
		
		
		addData(aonContext, contract, backDate, null,
				new HashMap<String, String>() {
					{
						put(getFactorVariable().getName(), "0.5");
					}
				});
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		salary = new SmartContractSalaryCalculator<Salary>(
				new SalaryBuilder(){
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println("_P " + payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getQuote() + ")");
		}
		
		costs = 0.00;
		for (com.esferalia.aon.payroll.SalaryCost cost : salary
				.getSalaryCosts()) {
			System.out.println("_C " +cost.getName() + " = " + cost.getAmount()
					);
			costs += cost.getAmount();
		}
		
		bonuses = 0.00;
		for (com.esferalia.aon.payroll.SalaryBonus bonus : salary
				.getSalaryBonus()) {
			System.out.println("_B " + bonus.getName() + " = " + bonus.getAmount() + ", " + bonus.getDescription()
					);
			bonuses += bonus.getAmount();
		}
		
		
		off = costs / monthDays * 12
			+(costs / monthDays * (monthDays -12 ) * 0.60) * 0.5 
			+(costs / monthDays * (monthDays -12 ) * 0.75) * 0.5 ;
		org.junit.assertEquals(off, bonuses, DELTA);
		
	}

	private void addCosts(AONContext aonContext) {
		addCCCCost(
				aonContext, 
				CCCType.PRINCIPAL, 
				getFirstDayOfYear(getToday()), 
				"CGC_E", 
				DeductionType.COMMON_CONTINGENCY, 
				"TRACE('BASE_CGC_E = %f\\r\\n', BASE_CGC_E);BASE_CGC_E * 23.6/100");
		addCCCCost(
				aonContext, 
				CCCType.PRINCIPAL, 
				getFirstDayOfYear(getToday()), 
				"FP_E", 
				DeductionType.COMMON_CONTINGENCY, 
				"TRACE('BASE_CGP_E = %f\\r\\n', BASE_CGC_E);BASE_CGP_E * 1.50/100");
		addCCCCost(
				aonContext, 
				CCCType.PRINCIPAL, 
				getFirstDayOfYear(getToday()), 
				"FOGASA_E", 
				DeductionType.COMMON_CONTINGENCY, 
				"BASE_CGP_E * 4.00/100");
	}
	
	
}
