/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;

/**
 * @author rtrepiana
 *
 */
public class SQLAdditionalHoursTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000000001;

	@Test
	public void testBaseMin()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfMonth(getToday()), 
				new HashMap<String, String>() {
					{
						put(ContextVariable.PARTIAL_FACTOR.getName(), "0.50");
						put(ContextVariable.TC2.getName(), ContractCode.C200.getValue());
						put(ContextVariable.CGP_BASE_MIN.getName(), ContextVariable.CGC_BASE_MIN.getName());
						put(ContextVariable.CGC_BASE_MIN.getName(),"1166.70 * 0.50" );
						put("BASE_CGC_MIN_HORA","7.03" );
						
					}
				});
		
		
		
		addPayment(aonContext, 
				contract, 
				contract.getStartDate(), 
				contract.getEndDate(), 
				"SALARIO_BASE", 
				"100.00 * DIAS_TRABAJADOS / DIAS_MES", 
				"_P", 
				"_P", 
				PaymentType.CRA_0001, 
				SalaryType.SALARY);

		addPayment(aonContext, 
				contract, 
				contract.getStartDate(), 
				contract.getEndDate(), 
				"HORAS_COMPL", 
				"4.03 * HORAS_COMPLEMENTARIAS", 
				"_P", 
				"_P", 
				PaymentType.CRA_0057, 
				SalaryType.SALARY);
		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;
		
		addData(aonContext, contract, startDate, endDate, ContextVariable.ADDITIONAL_HOURS, 10.00);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);

		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>() ;
		calculator.setSalaryBuilder(getSalaryBuilder());
		Salary salary = calculator.calculate(ctx);
		for ( com.esferalia.aon.payroll.SalaryPayment payment: salary.getSalaryPayments())
			System.out.println(payment.getExpression() + "= " + payment.getAmount() + "," + payment.getQuote());
		
		Assert.assertEquals(1166.70 * 0.5 + 7.03 * 10.00, salary.getCommonBase(), DELTA);
		Assert.assertEquals(1166.70 * 0.5 + 7.03 * 10.00, salary.getProfessionalBase(), DELTA);
		//Assert.assertEquals(100.00 * 0.5 + 4.03 * 10.00, salary.getRawCommonBase(), DELTA);
		
		String cgcBase = salary.getSalaryData(ContextVariable.CGC_BASE.getName());
		Assert.assertEquals(1166.70 * 0.5 + 7.03 * 10.00, Double.parseDouble(cgcBase), DELTA);
		String cgpBase = salary.getSalaryData(ContextVariable.CGP_BASE.getName());
		Assert.assertEquals(1166.70 * 0.5 + 7.03 * 10.00, Double.parseDouble(cgpBase), DELTA);
//		
//		
//		String cgcBaseEnterprise = salary.getSalaryData(ContextVariable.CGC_BASE_ENTERPRISE.getName());
//		Assert.assertEquals(1000.00, Double.parseDouble(cgcBaseEnterprise), DELTA);
//		String cgpBaseEnterprise = salary.getSalaryData(ContextVariable.CGP_BASE_ENTERPRISE.getName());
//		Assert.assertEquals(1100.00, Double.parseDouble(cgpBaseEnterprise), DELTA);

	}
	
	@Test
	public void testBaseOK()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfMonth(getToday()), 
				new HashMap<String, String>() {
					{
						put(ContextVariable.PARTIAL_FACTOR.getName(), "0.50");
						put(ContextVariable.TC2.getName(), ContractCode.C200.getValue());
						put(ContextVariable.CGP_BASE_MIN.getName(), ContextVariable.CGC_BASE_MIN.getName());
						put(ContextVariable.CGC_BASE_MIN.getName(),"1166.70 * 0.50" );
						put("BASE_CGC_MIN_HORA","7.03" );
						
					}
				});
		
		
		
		addPayment(aonContext, 
				contract, 
				contract.getStartDate(), 
				contract.getEndDate(), 
				"SALARIO_BASE", 
				"100.00 * DIAS_TRABAJADOS / DIAS_MES", 
				"_P", 
				"_P", 
				PaymentType.CRA_0001, 
				SalaryType.SALARY);

		addPayment(aonContext, 
				contract, 
				contract.getStartDate(), 
				contract.getEndDate(), 
				"HORAS_COMPL", 
				"10.03 * HORAS_COMPLEMENTARIAS", 
				"_P", 
				"_P", 
				PaymentType.CRA_0057, 
				SalaryType.SALARY);
		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;
		
		addData(aonContext, contract, startDate, endDate, ContextVariable.ADDITIONAL_HOURS, 10.00);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);

		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>() ;
		calculator.setSalaryBuilder(getSalaryBuilder());
		Salary salary = calculator.calculate(ctx);
		for ( com.esferalia.aon.payroll.SalaryPayment payment: salary.getSalaryPayments())
			System.out.println(payment.getExpression() + "= " + payment.getAmount() + "," + payment.getQuote());
		
		Assert.assertEquals(1166.70 * 0.5 + 10.03 * 10.00, salary.getCommonBase(), DELTA);
		Assert.assertEquals(1166.70 * 0.5 + 10.03 * 10.00, salary.getProfessionalBase(), DELTA);
		//Assert.assertEquals(100.00 * 0.5 + 4.03 * 10.00, salary.getRawCommonBase(), DELTA);
		
		String cgcBase = salary.getSalaryData(ContextVariable.CGC_BASE.getName());
		salary.getSalaryDatas().stream().filter( e -> e.getName().equals(ContextVariable.CGC_BASE.getName())).forEach( e -> System.out.println(e.getName() + " = " + e.getExpression() + ", " + e.getStartDate() +"," + e.getEndDate()));
		Assert.assertEquals(1166.70 * 0.5 + 10.03 * 10.00, Double.parseDouble(cgcBase), DELTA);

		String cgpBase = salary.getSalaryData(ContextVariable.CGP_BASE.getName());
		salary.getSalaryDatas().stream().filter( e -> e.getName().equals(ContextVariable.CGP_BASE.getName())).forEach( e -> System.out.println(e.getName() + " = " + e.getExpression() + ", " + e.getStartDate() +"," + e.getEndDate()));
		Assert.assertEquals(1166.70 * 0.5 + 10.03 * 10.00, Double.parseDouble(cgpBase), DELTA);

	}
	
	@Test
	public void testNoFraccionateOK()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfMonth(getToday()), 
				new HashMap<String, String>() {
					{
						//put(ContextVariable.TC2.getName(), "\"100\"");
					    	//put(ContextVariable.CGP_BASE_MIN.getName(), ContextVariable.CGC_BASE_MIN.getName());
					    	//put(ContextVariable.CGC_BASE_MIN.getName(),"1166.70" );
					    	//put("BASE_CGC_MIN_HORA","7.03" );
						
					}
				});
		
		
		
		addPayment(aonContext, 
				contract, 
				contract.getStartDate(), 
				contract.getEndDate(), 
				"SALARIO_BASE", 
				"1066.70 * DIAS_TRABAJADOS / DIAS_MES", 
				"_P", 
				"_P", 
				PaymentType.CRA_0001, 
				SalaryType.SALARY);

		addPayment(aonContext, 
				contract, 
				contract.getStartDate(), 
				contract.getEndDate(), 
				"HORAS_COMPL", 
				"10.00 * HORAS_COMPLEMENTARIAS", 
				"_P", 
				"_P", 
				PaymentType.CRA_0057, 
				SalaryType.SALARY);
		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;
		
		addData(aonContext, contract, startDate ,add(startDate, Calendar.DAY_OF_MONTH, 5),  ContextVariable.QUOTE_GROUP.getName(), "\"05\"");
		addData(aonContext, contract, add(startDate, Calendar.DAY_OF_MONTH, 6),endDate,  ContextVariable.QUOTE_GROUP.getName(), "\"04\"");

		addData(aonContext, contract, startDate, startDate, ContextVariable.ADDITIONAL_HOURS, 5.00);
		addData(aonContext, contract, endDate, endDate, ContextVariable.ADDITIONAL_HOURS, 5.00);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);

		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>() ;
		calculator.setSalaryBuilder(getSalaryBuilder());
		Salary salary = calculator.calculate(ctx);
		for ( com.esferalia.aon.payroll.SalaryPayment payment: salary.getSalaryPayments())
			System.out.println(payment.getDescription() + "= " + payment.getAmount() + "," + payment.getQuote());
		

	}

	protected ISalaryBuilder<Salary> getSalaryBuilder() {
		return new SalaryBuilder();
	}

}
