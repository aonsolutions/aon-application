package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_MAX;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_MIN;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE_MAX;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE_MIN;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

import org.junit.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;

import junit.framework.Assert;

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
		addPayment(aonContext, contract, ere, "SELF.addBonus('ERTE EXONERACIÓN DE CUOTAS','CUOTA_EMPRESARIAL * COEFICIENTE_ERE_FZA_EXONERADO');0.00" , 
		String.format("%s * BASE_REGULADORA", getDaysVariable()));
		

		Date startDate = getFirstDayOfMonth(getToday());
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

		Assert.assertEquals(
				(1750.00 * 1.10) * 0.50
						, salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals(
				(1750.00 * 1.10), salary.getCommonBase(),
				DELTA);

		Assert.assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);

		Assert.assertEquals(
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
		addPayment(aonContext, contract, ere, "SELF.addBonus('ERTE EXONERACIÓN DE CUOTAS','CUOTA_EMPRESARIAL * COEFICIENTE_ERE_FZA_EXONERADO');0.00" , 
		String.format("%s * BASE_REGULADORA", getDaysVariable()));
		

		Date startDate = getFirstDayOfMonth(getToday());
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

		Assert.assertEquals(
				(1750.00 * 1.10) * 0.00
						, salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals(
				(1750.00 * 1.10), salary.getCommonBase(),
				DELTA);

		Assert.assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);

		Assert.assertEquals(
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
		addPayment(aonContext, contract, ere, "SELF.addBonus('ERTE EXONERACIÓN DE CUOTAS','CUOTA_EMPRESARIAL * COEFICIENTE_ERE_FZA_EXONERADO');0.00" , 
		String.format("%s * BASE_REGULADORA", getDaysVariable()));
		

		Date startDate = getFirstDayOfMonth(getToday());
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

		Assert.assertEquals(
				(1750.00 * 1.10) * 10 / monthDays 
				, salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals(
				(1750.00 * 1.10), salary.getCommonBase(),
				DELTA);

		Assert.assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);

		Assert.assertEquals(
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
		addPayment(aonContext, contract, ere, "SELF.addBonus('ERTE EXONERACIÓN DE CUOTAS','CUOTA_EMPRESARIAL * COEFICIENTE_ERE_FZA_EXONERADO');0.00" , 
		String.format("%s * BASE_REGULADORA", getDaysVariable()));
		

		Date startDate = getFirstDayOfMonth(getToday());
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

		Assert.assertEquals(
				(1750.00 * 1.10) * 10 / monthDays + (1750.00 * 1.10) * workDays / monthDays * 0.80 
				, salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals(
				(1750.00 * 1.10), salary.getCommonBase(),
				DELTA);

		Assert.assertEquals(
				salary.getTotalPayment() * 0.15 
				, salary.getSocialSecurityContributions(),
				DELTA);

		Assert.assertEquals(
				salary.getTotalPayment() * ((23.6+4+1.50)/100.00) 
				, salary.getTotalEnterprise(),
				DELTA);
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
