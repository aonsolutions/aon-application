package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.Calendar.DATE;

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
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;

public class SQLGrossTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000001;

	@Test
	public void testGross() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:off
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.expression = "0.00";
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
				new HashMap<String, String>() {
				}, new String[] { 
						"( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
						"BRUTO(3333.00 * DIAS_TRABAJADOS / DIAS_MES)" ,
						}
				, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" }, category);
		//@formatter:on
		
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
//		Date startIt = getToday();
//		Date endIt = AonDateUtils.add(getToday(), Calendar.DATE, 100);
//		
//		addIT(aonContext, 
//				contract, 
//				LeaveType.COMMON_DISEASE, 
//				startIt,
//				endIt, 
//				null);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		//@formatter:off
		Assert.assertEquals(
				3333.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testGrossI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:off
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.expression = "0.00";
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
				getToday(),
				new HashMap<String, String>() {
				}, new String[] { 
						"( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
						"BRUTO(3333.00 * DIAS_TRABAJADOS / DIAS_MES)" ,
						}
				, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" }, category);
		//@formatter:on
		
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		int monthDays = AonDateUtils.getMax(getToday(), DATE) ;
		int workDays = AonDateUtils.getMax(getToday(), DATE) - get(getToday(), DATE) +1;
		
		//@formatter:off
		Assert.assertEquals(
				3333.00 * workDays / monthDays, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		System.out.println("BRUTO [3333.00]: " + (3333.00 * workDays / monthDays));
		
		
	}

	@Test
	public void testGrossII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:off
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.expression = "0.00";
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
				new HashMap<String, String>() {
				}, new String[] { 
						"( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
						"BRUTO(3333.00 * DIAS_TRABAJADOS / DIAS_MES)" ,
						}
				, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" }, category);
		//@formatter:on
		
		Date startIt = getToday();
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				null, 
				null);
		

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		int monthDays = AonDateUtils.getMax(getToday(), DATE) ;
		int workDays = get(getToday(), DATE)-1;
		
		//@formatter:off
		Assert.assertEquals(
				3333.00 * workDays / monthDays, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		System.out.println("BRUTO [3333.00]: " + (3333.00 * workDays / monthDays));
		
		
	}

	@Test
	public void testGrossIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:off
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.expression = "0.00";
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
				new HashMap<String, String>() {
				}, new String[] { 
						"( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
						"BRUTO(3333.00 * DIAS_TRABAJADOS / DIAS_MES)" ,
						"TRACE('DIAS_TRABAJADOS = %f\r\n',DIAS_TRABAJADOS);0.00"
						}
				, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" }, category);
		//@formatter:on
		
		Date startIt = getToday();
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				null, 
				null);
		
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT, String.format("500 * 0.90 * %s_4_15",  COMMON_DISEASE_DAYS));
		addPayment(aonContext, contract, prestIT, String.format("500 * 0.90 * %s_16_20",  COMMON_DISEASE_DAYS));
		addPayment(aonContext, contract, prestIT, String.format("500 * 0.90 * %s_21",  COMMON_DISEASE_DAYS));

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
//		for(com.esferalia.aon.payroll.SalaryPayment payment: salary.getSalaryPayments())
//			System.out.println(payment.getName() +  " = " + payment.getAmount() + " (" + payment.getExpression() +")");

		int monthDays = AonDateUtils.getMax(getToday(), DATE) ;
		int workDays = get(getToday(), DATE)-1;
		
		//@formatter:off
		Assert.assertEquals(
				(3333.00 * workDays / monthDays)
				+(500 * 0.90) * Math.max( monthDays - workDays -3, 0), 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		System.out.println("BRUTO [3333.00]: " + (3333.00 * workDays / monthDays) + ", " + salary.getTotalPayment()  );
		
		
	}
	// ------------------------------------------------------------------------


}
