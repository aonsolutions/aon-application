package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;

import org.junit.Test;

import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;

import junit.framework.Assert;

public class SQLLiquidTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.001;

	@Test
	public void testLiquidI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:off
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
		
		

		ContractRecord contract = newContract(aonContext,  
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
				}, new String[] { 
						"( P_1 + P_2 )* 0.10 ",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
						"NETO(3333.00)" ,
						}
				, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * 2.00/100" 
				}, category);
		//@formatter:on
		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		for ( SalaryPayment s : salary.getSalaryPayments() ) 
			System.out.println(s.getDescription() + " = " + s.getAmount() +", " + s.getQuote());
		
		System.out.println("BASE :" + salary.getCommonBase());
		
		
		//@formatter:off
		Assert.assertEquals(
				3333.00, 
				salary.getTotalLiquid() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testLiquidII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:off
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
		
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.add(getFirstDayOfMonth(getToday()), Calendar.MONTH, -2),
				new HashMap<String, String>() {
				}, new String[] { 
						"( P_1 + P_2 )* 0.10 ",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
						"NETO(3333.00 * DIAS_TRABAJADOS / DIAS_MES)" ,
						}
				, new String[] {
//						"BASE_CGC * 0.10", 
//						"BASE_CGP * 0.05",
//						"BASE_IRPF * 2.00/100" 
				}, category);
		
		
		
		//@formatter:on
		
		//Calendar
		
		Date startDate = AonDateUtils.add(getFirstDayOfMonth(getToday()), Calendar.MONTH, -1);
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		double commonBase = salary.getCommonBase();
		
		
		startDate = AonDateUtils.add(startDate, Calendar.MONTH,1);
		endDate = getLastDayOfMonth(startDate);
		int monthDays = AonDateUtils.get(endDate, Calendar.DAY_OF_MONTH);
		double br = 3333.00 * ( 1 + 1.00/6 ) / monthDays; //195.48387096774195
		double itDays = ( monthDays - 20 ) ;
		

		addPrestITs(aonContext, contract);

		Date startIt = AonDateUtils.add(startDate, Calendar.DAY_OF_MONTH, 20);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startIt, null, null);
		
		ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		for ( SalaryPayment s : salary.getSalaryPayments() ) 
//TODO: Use log instead 			System.out.println(s.getDescription() + " = " + s.getAmount() +", " + s.getQuote());
		
		
		monthDays = AonDateUtils.get(endDate, Calendar.DAY_OF_MONTH); 
		int workedDays = AonDateUtils.get(startIt, Calendar.DAY_OF_MONTH) -1; 
		
		//@formatter:off
		Assert.assertEquals(
				3333.00 / monthDays * workedDays + ( br * 1.00 * itDays) , 
				salary.getTotalLiquid() 
				, DELTA);
		//@formatter:on
		Assert.assertEquals(
				commonBase, 
				salary.getCommonBase() 
				, DELTA);
		
		
	}

	@Test
	public void testLiquidIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:off
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
		
		

		ContractRecord contract = newContract(aonContext,  
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
				}, new String[] { 
						"( P_1 + P_2 )* 0.10 ",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
						"NETO(3333.00)" ,
						}
				, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * 2.00/100" 
				}, category);
		//@formatter:on
		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder)
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		jooqSalaryBuilder.execute();


		startDate = add(startDate, Calendar.MONTH,1);
		endDate = getLastDayOfMonth(startDate);
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		
		
		//@formatter:off
		Assert.assertEquals(
				3333.00, 
				salary.getTotalLiquid() 
				, DELTA);
		//@formatter:on
		
		
	}

	protected static PaymentConceptRecord addPrestITs(AONContext aonContext, ContractRecord contract) {
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 1.00 * %s_1_3",  COMMON_DISEASE_DAYS),
				"_P"
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 1.00 * %s_4_15",  COMMON_DISEASE_DAYS),
				"_P"
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.60 * %s_16_20",  COMMON_DISEASE_DAYS),
				"_P"
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s_21",  COMMON_DISEASE_DAYS),
				"_P"
				);
		return prestIT;
	}
	

}
