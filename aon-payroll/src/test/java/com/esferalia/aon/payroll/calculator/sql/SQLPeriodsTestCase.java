package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.Calendar.DAY_OF_MONTH;

import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryData;
import com.esferalia.aon.payroll.SalaryDeduction;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.calculator.RoundSalaryBuilder;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;

public class SQLPeriodsTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.001;

	@Test
	public void testDesmplPeriodsI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);


		ContractRecord contract = newContract(aonContext,  
				getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						"1250.00 * DIAS_TRABAJADOS / DIAS_MES" 
						}
				, new String[] {
						"BASE_CGC * PORCENTAJE_CGC/100", 
						"BASE_CGP * PORCENTAJE_FP/100",
						"BASE_CGP * ( isdef PORCENTAJE_DESMPL ? PORCENTAJE_DESMPL : PORCENTAJE_DESMPL=(INDEFINIDO ? 1.55 : 1.60 ))/100"
				}, null);
		//@formatter:on
		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		addData(aonContext, contract, startDate, endDate, ContextVariable.CGC_EMPLOYEE_PERCENT, "4.6");
		addData(aonContext, contract, startDate, endDate, ContextVariable.FP_EMPLOYEE_PERCENT, "0.5");
		
		Date desmplEndDate = add(startDate, Calendar.DAY_OF_MONTH, 10);
		Date desmplStartDate = add(desmplEndDate, Calendar.DAY_OF_MONTH, 1);
		addData(aonContext, contract, startDate, desmplEndDate, ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT, "1.55");
		addData(aonContext, contract, desmplStartDate, endDate, ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT, "1.60");
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		for ( SalaryPayment s : salary.getSalaryPayments() ) 
			System.out.println(s.getDescription() + " = " + s.getAmount() + "," + s.getType());

		for ( SalaryDeduction s : salary.getSalaryDeductions() ) 
			System.out.println(s.getDescription() + " = " + s.getAmount() + "," + s.getType());
		
		List<SalaryData> salaryData = 
		salary.getSalaryDatas().stream()
		.sorted((d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate()) )
		.filter( d -> d.getName().equals(ContextVariable.CGC_BASE.getName())).collect(Collectors.toList());
		
		junit.framework.Assert.assertEquals(2, salaryData.size());
		
		Assert.assertEquals(salaryData.get(0).getStartDate(), startDate);
		Assert.assertEquals(salaryData.get(0).getEndDate(), desmplEndDate);

		Assert.assertEquals(salaryData.get(1).getStartDate(), desmplStartDate);
		Assert.assertEquals(salaryData.get(1).getEndDate(), endDate);
		
	}


	@Test
	public void testDesmplPeriodsII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);


		ContractRecord contract = newContract(aonContext,  
				getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						"1250.00 * DIAS_TRABAJADOS / DIAS_MES" 
						}
				, new String[] {
						"BASE_CGC * PORCENTAJE_CGC/100", 
						"BASE_CGP * PORCENTAJE_FP/100",
						"BASE_CGP * ( isdef PORCENTAJE_DESMPL ? PORCENTAJE_DESMPL : PORCENTAJE_DESMPL=(INDEFINIDO ? 1.55 : 1.60 ))/100"
				}, null);
		//@formatter:on
		
		
		// January
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		addData(aonContext, contract, startDate, endDate, ContextVariable.CGC_EMPLOYEE_PERCENT, "4.6");
		addData(aonContext, contract, startDate, endDate, ContextVariable.FP_EMPLOYEE_PERCENT, "0.5");
		
		Date desmplEndDate = add(startDate, Calendar.DAY_OF_MONTH, 10);
		Date desmplStartDate = add(desmplEndDate, Calendar.DAY_OF_MONTH, 1);
		addData(aonContext, contract, startDate, desmplEndDate, ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT, "1.55");
		addData(aonContext, contract, desmplStartDate, endDate, ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT, "1.60");
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		for ( SalaryPayment s : salary.getSalaryPayments() ) 
			System.out.println(s.getDescription() + " = " + s.getAmount() + "," + s.getType());

		for ( SalaryDeduction s : salary.getSalaryDeductions() ) 
			System.out.println(s.getDescription() + " = " + s.getAmount() + "," + s.getType());
		
		List<SalaryData> salaryData = 
		salary.getSalaryDatas().stream()
		.sorted((d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate()) )
		.filter( d -> d.getName().equals(ContextVariable.CGC_BASE.getName())).collect(Collectors.toList());
		
		junit.framework.Assert.assertEquals(2, salaryData.size());
		
		Assert.assertEquals(salaryData.get(0).getStartDate(), startDate);
		Assert.assertEquals(salaryData.get(0).getEndDate(), desmplEndDate);
		Assert.assertEquals(1250.00 / 31.00 * 11, Double.parseDouble(salaryData.get(0).getExpression()), 0.001);

		Assert.assertEquals(salaryData.get(1).getStartDate(), desmplStartDate);
		Assert.assertEquals(salaryData.get(1).getEndDate(), endDate);
		Assert.assertEquals(1250.00 / 31.00 * 20, Double.parseDouble(salaryData.get(1).getExpression()), 0.001);
		
	}

	@Test
	public void testDesmplPeriodsITII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);


		ContractRecord contract = newContract(aonContext,  
				getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						"1250.00 * DIAS_TRABAJADOS / DIAS_MES" 
						}
				, new String[] {
						"BASE_CGC * PORCENTAJE_CGC/100", 
						"BASE_CGP * PORCENTAJE_FP/100",
						"BASE_CGP * ( isdef PORCENTAJE_DESMPL ? PORCENTAJE_DESMPL : PORCENTAJE_DESMPL=(INDEFINIDO ? 1.55 : 1.60 ))/100"
				}, null);
		//@formatter:on
		
		SQLITTestCase.addPrestITs(aonContext, contract);
		
		// January
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		addIT(aonContext, contract, LeaveType.OCCUPATIONAL_DISEASE, startDate, endDate, null);

		addData(aonContext, contract, startDate, endDate, ContextVariable.CGC_EMPLOYEE_PERCENT, "4.6");
		addData(aonContext, contract, startDate, endDate, ContextVariable.FP_EMPLOYEE_PERCENT, "0.5");
		
		Date desmplEndDate = add(startDate, Calendar.DAY_OF_MONTH, 10);
		Date desmplStartDate = add(desmplEndDate, Calendar.DAY_OF_MONTH, 1);
		addData(aonContext, contract, startDate, desmplEndDate, ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT, "1.55");
		addData(aonContext, contract, desmplStartDate, endDate, ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT, "1.60");
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		for ( SalaryPayment s : salary.getSalaryPayments() ) 
			System.out.println(s.getDescription() + " = " + s.getAmount() + "," + s.getType() + ","+ s.getExpression());

		for ( SalaryDeduction s : salary.getSalaryDeductions() ) 
			System.out.println(s.getDescription() + " = " + s.getAmount() + "," + s.getType());
		
		List<SalaryData> salaryData = 
		salary.getSalaryDatas().stream()
		.sorted((d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate()) )
		.filter( d -> d.getName().equals(ContextVariable.CGC_BASE.getName())).collect(Collectors.toList());
		
		junit.framework.Assert.assertEquals(2, salaryData.size());
		
		Assert.assertEquals(salaryData.get(0).getStartDate(), startDate);
		Assert.assertEquals(salaryData.get(0).getEndDate(), desmplEndDate);
		Assert.assertEquals(1250.00 / 31.00 * 11, Double.parseDouble(salaryData.get(0).getExpression()), 0.001);

		Assert.assertEquals(salaryData.get(1).getStartDate(), desmplStartDate);
		Assert.assertEquals(salaryData.get(1).getEndDate(), endDate);
		Assert.assertEquals(1250.00 / 31.00 * 20, Double.parseDouble(salaryData.get(1).getExpression()), 0.001);
		
	}

	@Test
	public void testDesmplPeriodsAdjustII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);


		ContractRecord contract = newContract(aonContext,  
				getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						"1250.00 * DIAS_TRABAJADOS / DIAS_MES" 
						}
				, new String[] {
						"BASE_CGC * PORCENTAJE_CGC/100", 
						"BASE_CGP * PORCENTAJE_FP/100",
						"BASE_CGP * ( isdef PORCENTAJE_DESMPL ? PORCENTAJE_DESMPL : PORCENTAJE_DESMPL=(INDEFINIDO ? 1.55 : 1.60 ))/100"
				}, null);
		//@formatter:on
		
		
		// January
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		addData(aonContext, contract, startDate, null, ContextVariable.MONTH_DAYS, "30.00");
		
		addData(aonContext, contract, startDate, endDate, ContextVariable.CGC_EMPLOYEE_PERCENT, "4.6");
		addData(aonContext, contract, startDate, endDate, ContextVariable.FP_EMPLOYEE_PERCENT, "0.5");
		
		Date desmplEndDate = add(startDate, Calendar.DAY_OF_MONTH, 10);
		Date desmplStartDate = add(desmplEndDate, Calendar.DAY_OF_MONTH, 1);
		addData(aonContext, contract, startDate, desmplEndDate, ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT, "1.55");
		addData(aonContext, contract, desmplStartDate, endDate, ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT, "1.60");
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		for ( SalaryPayment s : salary.getSalaryPayments() ) 
			System.out.println(s.getDescription() + " = " + s.getAmount() + "," + s.getType());

		for ( SalaryDeduction s : salary.getSalaryDeductions() ) 
			System.out.println(s.getDescription() + " = " + s.getAmount() + "," + s.getType());
		
		List<SalaryData> salaryData = 
		salary.getSalaryDatas().stream()
		.sorted((d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate()) )
		.filter( d -> d.getName().equals(ContextVariable.CGC_BASE.getName())).collect(Collectors.toList());
		
		junit.framework.Assert.assertEquals(2, salaryData.size());
		
		Assert.assertEquals(salaryData.get(0).getStartDate(), startDate);
		Assert.assertEquals(salaryData.get(0).getEndDate(), desmplEndDate);
		Assert.assertEquals(1250.00 / 30.00 * 11, Double.parseDouble(salaryData.get(0).getExpression()), 0.001);

		Assert.assertEquals(salaryData.get(1).getStartDate(), desmplStartDate);
		Assert.assertEquals(salaryData.get(1).getEndDate(), endDate);
		Assert.assertEquals(1250.00 / 30.00 * 19, Double.parseDouble(salaryData.get(1).getExpression()), 0.001);
		
	}


	public void __testDesmplPeriodsITII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);


		ContractRecord contract = newContract(aonContext,  
				getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						"1250.00 * DIAS_TRABAJADOS / DIAS_MES" 
						}
				, new String[] {
						"BASE_CGC * PORCENTAJE_CGC/100", 
						"BASE_CGP * PORCENTAJE_FP/100",
						"BASE_CGP * ( isdef PORCENTAJE_DESMPL ? PORCENTAJE_DESMPL : PORCENTAJE_DESMPL=(INDEFINIDO ? 1.55 : 1.60 ))/100"
				}, null);
		//@formatter:on
		
		SQLITTestCase.addPrestITs(aonContext, contract);
		// January
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		Date startITDate = add(startDate, Calendar.DAY_OF_MONTH, 5);
		Date endITDate = add(startITDate, Calendar.DAY_OF_MONTH, 10);
		
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate, endITDate, null);
		
		addData(aonContext, contract, startDate, endDate, ContextVariable.CGC_EMPLOYEE_PERCENT, "4.6");
		addData(aonContext, contract, startDate, endDate, ContextVariable.FP_EMPLOYEE_PERCENT, "0.5");
		
		Date desmplEndDate = add(startDate, Calendar.DAY_OF_MONTH, 10 );
		Date desmplStartDate = add(desmplEndDate, Calendar.DAY_OF_MONTH, 1);
		addData(aonContext, contract, startDate, desmplEndDate, ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT, "1.55");
		addData(aonContext, contract, desmplStartDate, endDate, ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT, "1.60");
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		for ( SalaryPayment s : salary.getSalaryPayments() ) 
			System.out.println(s.getDescription() + " = " + s.getAmount() + "," + s.getType());

		for ( SalaryDeduction s : salary.getSalaryDeductions() ) 
			System.out.println(s.getDescription() + " = " + s.getAmount() + "," + s.getType());
		
		List<SalaryData> salaryData = 
		salary.getSalaryDatas().stream()
		.filter( d -> d.getName().equals(ContextVariable.CGC_BASE.getName()))
		.sorted( (d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate()) )
		.collect(Collectors.toList())
		;
		
		junit.framework.Assert.assertEquals(4, salaryData.size());
		
		Assert.assertEquals(salaryData.get(0).getStartDate(), startDate);
		Assert.assertEquals(salaryData.get(0).getEndDate(), add(startITDate, Calendar.DAY_OF_MONTH,-1));
		//Assert.assertEquals(1250.00 / 31.00 * 6, Double.parseDouble(salaryData.get(0).getExpression()), 0.001);

		Assert.assertEquals(salaryData.get(1).getStartDate(), startITDate);
		Assert.assertEquals(salaryData.get(1).getEndDate(), add(startITDate, Calendar.DAY_OF_MONTH,2));

		Assert.assertEquals(salaryData.get(2).getStartDate(), add(startITDate, Calendar.DAY_OF_MONTH,3));
		Assert.assertEquals(salaryData.get(2).getEndDate(), endITDate);
		
		Assert.assertEquals(salaryData.get(3).getStartDate(), add(endITDate, Calendar.DAY_OF_MONTH,1));
		Assert.assertEquals(salaryData.get(3).getEndDate(), endDate);
	}

	@Test
	public void testTC2PeriodsITII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);


		ContractRecord contract = newContract(aonContext,  
				getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						"1250.00 * DIAS_TRABAJADOS / DIAS_MES" 
						}
				, new String[] {
						"BASE_CGC * PORCENTAJE_CGC/100", 
						"BASE_CGP * PORCENTAJE_FP/100",
						"BASE_CGP * ( isdef PORCENTAJE_DESMPL ? PORCENTAJE_DESMPL : PORCENTAJE_DESMPL=(INDEFINIDO ? 1.55 : 1.60 ))/100"
				}, null);
		//@formatter:on
		
		SQLITTestCase.addPrestITs(aonContext, contract);
		
		// January
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		addIT(aonContext, contract, LeaveType.OCCUPATIONAL_DISEASE, startDate, endDate, null);

		Date tc2EndDate = add(startDate, Calendar.DAY_OF_MONTH, 10);
		Date tc2StartDate = add(tc2EndDate, Calendar.DAY_OF_MONTH, 1);
		addData(aonContext, contract, startDate, tc2EndDate, ContextVariable.TC2, "\"501\"");
		addData(aonContext, contract, tc2StartDate, endDate, ContextVariable.TC2, "\"289\"");
		addData(aonContext, contract, startDate, tc2EndDate, ContextVariable.PARTIAL_FACTOR, "0.50");
		addData(aonContext, contract, tc2StartDate, endDate, ContextVariable.PARTIAL_FACTOR, "0.50");
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		for ( SalaryPayment s : salary.getSalaryPayments() ) 
			System.out.println(s.getDescription() + " = " + s.getAmount() + "," + s.getType() + ","+ s.getExpression());

		for ( SalaryDeduction s : salary.getSalaryDeductions() ) 
			System.out.println(s.getDescription() + " = " + s.getAmount() + "," + s.getType());
		
		List<SalaryData> salaryData = 
		salary.getSalaryDatas().stream()
		.sorted((d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate()) )
		.filter( d -> d.getName().equals(ContextVariable.CGC_BASE.getName())).collect(Collectors.toList());
		
		junit.framework.Assert.assertEquals(2, salaryData.size());
		
		Assert.assertEquals(salaryData.get(0).getStartDate(), startDate);
		Assert.assertEquals(salaryData.get(0).getEndDate(), tc2EndDate);
		Assert.assertEquals(1250.00 / 31.00 * 11 * 0.5, Double.parseDouble(salaryData.get(0).getExpression()), 0.001);

		Assert.assertEquals(salaryData.get(1).getStartDate(), tc2StartDate);
		Assert.assertEquals(salaryData.get(1).getEndDate(), endDate);
		Assert.assertEquals(1250.00 / 31.00 * 20 * 0.5, Double.parseDouble(salaryData.get(1).getExpression()), 0.001);
		
		
		Assert.assertEquals(1250.00 * 0.5  , salary.getTotalPayment(), 0.001);
		
	}

	@Test
	public void testPartialPeriodsI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);


		ContractRecord contract = newContract(aonContext,  
				getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
                		    {
                			put("TC2", "\"100\"");
                		    }
				}, new String[] { 
					"1250.00 * DIAS_TRABAJADOS / DIAS_MES" 
				}
				, new String[] {
				}, null);
		//@formatter:on
		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDateWeekHours20 =  add(startDate, Calendar.DAY_OF_MONTH, 5);
		
		Date startDateWeekHours25 =  add(endDateWeekHours20, Calendar.DAY_OF_MONTH, 1);
		Date endDateWeekHours25 =  add(startDateWeekHours25, Calendar.DAY_OF_MONTH, 5);
		
		Date startDateWeekHours35 =  add(endDateWeekHours25, Calendar.DAY_OF_MONTH, 1);
		Date endDateWeekHours35 =  add(startDateWeekHours35, Calendar.DAY_OF_MONTH, 5);
		
		Date startDateWeekHours15 =  add(endDateWeekHours35, Calendar.DAY_OF_MONTH, 1);
		Date endDate = getLastDayOfMonth(startDate);
		
		addData(aonContext, contract, startDate, endDate, ContextVariable.AGREEMENT_HOURS, "40.00");
		addData(aonContext, contract, startDate, endDateWeekHours20, ContextVariable.WEEK_HOURS, "20.00");
		addData(aonContext, contract, startDateWeekHours25, endDateWeekHours25, ContextVariable.WEEK_HOURS, "25.00");
		addData(aonContext, contract, startDateWeekHours35, endDateWeekHours35, ContextVariable.WEEK_HOURS, "35.00");
		addData(aonContext, contract, startDateWeekHours15, endDate, ContextVariable.WEEK_HOURS, "15.00");
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		for ( SalaryPayment s : salary.getSalaryPayments() ) 
			System.out.println(s.getDescription() + " = " + s.getAmount() + "," + s.getType());

		List<SalaryData> salaryData = 
		salary.getSalaryDatas().stream()
		.sorted((d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate()) )
		.filter( d -> d.getName().equals(ContextVariable.CGC_BASE.getName())).collect(Collectors.toList());
		
		Assert.assertEquals(4, salaryData.size());
		
		Date endDate50 =  add(startDate, Calendar.DAY_OF_MONTH, 10);
		Date startDate75 =  add(endDate50, Calendar.DAY_OF_MONTH, 1);
		addData(aonContext, contract, startDate, endDate50, ContextVariable.PARTIAL_FACTOR, "0.50");
		addData(aonContext, contract, startDate75, endDate, ContextVariable.PARTIAL_FACTOR, "0.75");
		
		ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		for ( SalaryPayment s : salary.getSalaryPayments() ) 
			System.out.println(s.getDescription() + " = " + s.getAmount() + "," + s.getType());
		
		salaryData = 
		salary.getSalaryDatas().stream()
		.sorted((d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate()) )
		.filter( d -> d.getName().equals(ContextVariable.CGC_BASE.getName())).collect(Collectors.toList());
		
		Assert.assertEquals(2, salaryData.size());
		
		Assert.assertEquals(salaryData.get(0).getStartDate(), startDate);
		Assert.assertEquals(salaryData.get(0).getEndDate(), endDate50);

		Assert.assertEquals(salaryData.get(1).getStartDate(), startDate75);
		Assert.assertEquals(salaryData.get(1).getEndDate(), endDate);
		
	}
	
	
	@Test
	public void testDeductionPeriods() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);


		ContractRecord contract = newContract(aonContext,  
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
                		    {
                			put("PORCENTAJE_CGC", "10.00");
                			put(ContextVariable.TC2.getName(), "100");
                		    }
				}, new String[] { 
						"1250.00 * DIAS_TRABAJADOS / DIAS_MES" 
						}
				, new String[] {
						"VARIABLE;BASE_CGC * PORCENTAJE_CGC/100", 
				}, null);
		//@formatter:on
		
		SQLITTestCase.addPrestITs(aonContext, contract);
		
		// January
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		Date itStartDate = add(startDate, Calendar.DAY_OF_MONTH, 18);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, itStartDate, null, null);
		
		addData(aonContext, contract, startDate, add(itStartDate, DAY_OF_MONTH, -1 ), "VARIABLE", "\"1.00\"");
		addData(aonContext, contract, itStartDate, add(itStartDate, DAY_OF_MONTH, 2 ), "VARIABLE", "\"2.00\"");
		addData(aonContext, contract, add(itStartDate, DAY_OF_MONTH, 3 ), add(itStartDate, DAY_OF_MONTH, 5 ), "VARIABLE", "\"3.00\"");
		addData(aonContext, contract, add(itStartDate, DAY_OF_MONTH, 6 ), endDate, "VARIABLE", "\"4.00\"");
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new RoundSalaryBuilder<Salary>(new SalaryBuilder(), d -> d.setScale(2, RoundingMode.HALF_UP) )).calculate(ctx);
		//Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		for ( SalaryPayment s : salary.getSalaryPayments() ) 
			System.out.println(s.getDescription() + " = " + s.getAmount() + "," + s.getType() + ","+ s.getExpression());

		for ( SalaryDeduction s : salary.getSalaryDeductions() ) 
			System.out.println(s.getDescription() + " = " + s.getAmount() + "," + s.getType());
		
		List<SalaryData> salaryData = 
		salary.getSalaryDatas().stream()
		.sorted((d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate()) )
		.filter( d -> d.getName().equals(ContextVariable.CGC_BASE.getName())).collect(Collectors.toList());
		
		salaryData.forEach( d -> System.out.println(d.getName() +" = " + d.getExpression() + ", " + d.getStartDate()));
		
		
		Assert.assertEquals(1250.00 * 10.00 / 100.00  , salary.getSocialSecurityContributions(), 0.015);
		
	}
	
}
