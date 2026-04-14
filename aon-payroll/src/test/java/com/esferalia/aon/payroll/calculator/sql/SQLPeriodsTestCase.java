package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_MIN;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE_MIN;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.Calendar.DAY_OF_MONTH;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryCost;
import com.esferalia.aon.payroll.SalaryData;
import com.esferalia.aon.payroll.SalaryDeduction;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.calculator.RoundSalaryBuilder;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.Period;
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
		
		assertEquals(2, salaryData.size());
		
		assertEquals(salaryData.get(0).getStartDate(), startDate);
		assertEquals(salaryData.get(0).getEndDate(), desmplEndDate);

		assertEquals(salaryData.get(1).getStartDate(), desmplStartDate);
		assertEquals(salaryData.get(1).getEndDate(), endDate);
		
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
		
		assertEquals(2, salaryData.size());
		
		assertEquals(salaryData.get(0).getStartDate(), startDate);
		assertEquals(salaryData.get(0).getEndDate(), desmplEndDate);
		assertEquals(1250.00 / 31.00 * 11, Double.parseDouble(salaryData.get(0).getExpression()), 0.001);

		assertEquals(salaryData.get(1).getStartDate(), desmplStartDate);
		assertEquals(salaryData.get(1).getEndDate(), endDate);
		assertEquals(1250.00 / 31.00 * 20, Double.parseDouble(salaryData.get(1).getExpression()), 0.001);
		
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
		
		assertEquals(2, salaryData.size());
		
		assertEquals(salaryData.get(0).getStartDate(), startDate);
		assertEquals(salaryData.get(0).getEndDate(), desmplEndDate);
		assertEquals(1250.00 / 31.00 * 11, Double.parseDouble(salaryData.get(0).getExpression()), 0.001);

		assertEquals(salaryData.get(1).getStartDate(), desmplStartDate);
		assertEquals(salaryData.get(1).getEndDate(), endDate);
		assertEquals(1250.00 / 31.00 * 20, Double.parseDouble(salaryData.get(1).getExpression()), 0.001);
		
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
		
		assertEquals(2, salaryData.size());
		
		assertEquals(salaryData.get(0).getStartDate(), startDate);
		assertEquals(salaryData.get(0).getEndDate(), desmplEndDate);
		assertEquals(1250.00 / 30.00 * 11, Double.parseDouble(salaryData.get(0).getExpression()), 0.001);

		assertEquals(salaryData.get(1).getStartDate(), desmplStartDate);
		assertEquals(salaryData.get(1).getEndDate(), endDate);
		assertEquals(1250.00 / 30.00 * 19, Double.parseDouble(salaryData.get(1).getExpression()), 0.001);
		
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
		
		assertEquals(4, salaryData.size());
		
		assertEquals(salaryData.get(0).getStartDate(), startDate);
		assertEquals(salaryData.get(0).getEndDate(), add(startITDate, Calendar.DAY_OF_MONTH,-1));
		//assertEquals(1250.00 / 31.00 * 6, Double.parseDouble(salaryData.get(0).getExpression()), 0.001);

		assertEquals(salaryData.get(1).getStartDate(), startITDate);
		assertEquals(salaryData.get(1).getEndDate(), add(startITDate, Calendar.DAY_OF_MONTH,2));

		assertEquals(salaryData.get(2).getStartDate(), add(startITDate, Calendar.DAY_OF_MONTH,3));
		assertEquals(salaryData.get(2).getEndDate(), endITDate);
		
		assertEquals(salaryData.get(3).getStartDate(), add(endITDate, Calendar.DAY_OF_MONTH,1));
		assertEquals(salaryData.get(3).getEndDate(), endDate);
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
		
		assertEquals(2, salaryData.size());
		
		assertEquals(salaryData.get(0).getStartDate(), startDate);
		assertEquals(salaryData.get(0).getEndDate(), tc2EndDate);
		assertEquals(1250.00 / 31.00 * 11 * 0.5, Double.parseDouble(salaryData.get(0).getExpression()), 0.001);

		assertEquals(salaryData.get(1).getStartDate(), tc2StartDate);
		assertEquals(salaryData.get(1).getEndDate(), endDate);
		assertEquals(1250.00 / 31.00 * 20 * 0.5, Double.parseDouble(salaryData.get(1).getExpression()), 0.001);
		
		
		assertEquals(1250.00 * 0.5  , salary.getTotalPayment(), 0.001);
		
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
		
		assertEquals(4, salaryData.size());
		
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
		
		assertEquals(2, salaryData.size());
		
		assertEquals(salaryData.get(0).getStartDate(), startDate);
		assertEquals(salaryData.get(0).getEndDate(), endDate50);

		assertEquals(salaryData.get(1).getStartDate(), startDate75);
		assertEquals(salaryData.get(1).getEndDate(), endDate);
		
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
		
		
		assertEquals(1250.00 * 10.00 / 100.00  , salary.getSocialSecurityContributions(), 0.015);
		
	}
	
	@Test
	public void testDeductionPeriodsBase() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		

		ContractRecord contract = newContract(aonContext,  
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
				{
					put("PORCENTAJE_CGC", "10.00");
                	put(ContextVariable.TC2.getName(), "100");
                	put(ContextVariable.QUOTE_GROUP.getName(), "\"01\"");
                }
				}, new String[] { 
						"1250.00 * DIAS_TRABAJADOS / DIAS_MES" 
						}
				, new String[] {
						"BASE_CGC * PORCENTAJE_CGC / 100.00 ", 
				}, null);
		
		// January
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		addSystemData(aonContext, startDate, null, new HashMap<String, String>(){
			{
				put(ContextVariable.CGC_BASE_MIN.getName(), "GRUPO_COTIZACION; 1000.00 / DIAS_MES * DIAS_NOMINA");
				put(ContextVariable.CGP_BASE_MIN.getName(), "GRUPO_COTIZACION; 1000.00 / DIAS_MES * DIAS_NOMINA");
			}
		});

		Date startExclusionDate = add(startDate, Calendar.DAY_OF_MONTH, 18);
		
		addPayment(aonContext, contract, contract.getStartDate(), null, "PAGA_EXTRA", "100.00","_P","_P", PaymentType.CRA_0004);
		
		addCost(aonContext, contract, startExclusionDate, null,  "-1 * (BASE_CGC * PORCENTAJE_CGC / 100.00)", "EXCLUSION","EXCLUSION_CGC_E");
		
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new RoundSalaryBuilder<Salary>(new SalaryBuilder(), d -> d.setScale(2, RoundingMode.HALF_UP) )).calculate(ctx);
		
		for ( SalaryPayment s : salary.getSalaryPayments() ) 
			System.out.println(s.getDescription() + " = " + s.getAmount() + "," + s.getType() + ","+ s.getExpression());

		for ( SalaryDeduction s : salary.getSalaryDeductions() ) 
			System.out.println(s.getDescription() + " = " + s.getAmount() + "," + s.getType());
		
		List<SalaryData> salaryData = 
		salary.getSalaryDatas().stream()
		.sorted((d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate()) )
		.filter( d -> d.getName().equals(ContextVariable.CGC_BASE.getName())).toList();
		
		salaryData.forEach( d -> System.out.println(d.getName() +" = " + d.getExpression() + ", " + d.getStartDate()));
		assertEquals(2, salaryData.size());
		salaryData.forEach(d -> assertEquals(Double.parseDouble(d.getExpression()), 1350.00 / AonDateUtils.get(endDate, Calendar.DAY_OF_MONTH) * new Period(d.getStartDate(), d.getEndDate()).getDays(), DELTA));
		
		
		assertEquals(1350.00 * 10.00 / 100.00  , salary.getSocialSecurityContributions(), 0.015);
		
		
		
	}

	@Test
	public void testCostPercentsPeriodsI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		addSystemData(aonContext, getFirstDayOfYear(getToday()), null, 
				new HashMap<String,String>(){
					private static final long serialVersionUID = 1L;

			{
				put(CGC_BASE_MIN.getName(), "(1381.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30))");
			}
		});

		ContractRecord contract = newContract(aonContext,  
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>(){{
					put("DIAS_MES", "30.00");
				}
				}
				, new String[] { 
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES" 
						}
				, new String[] {
						"BASE_CGP * ( isdef PORCENTAJE_DESMPL ? PORCENTAJE_DESMPL : PORCENTAJE_DESMPL=(INDEFINIDO ? 1.55 : 1.60 ))/100"
				}, null);
		//@formatter:on
		
		addCost(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "(PORCENTAJE_DESMPL == 0) ? 0.00 : ( BASE_CGP_E * ( isdef PORCENTAJE_DESMPL_E ? PORCENTAJE_DESMPL_E : PORCENTAJE_DESMPL_E=(INDEFINIDO ? 5.50 : (TIEMPO_COMPLETO ? 6.70 : 7.70)))/100)", "0.60 %", "DESMPL_E");
		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		SQLITTestCase.addPrestITs(aonContext, contract);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, add(startDate, Calendar.DAY_OF_MONTH, 9), null, 1000.00 / 30.00);
		
		Date desmplEndDate = add(startDate, Calendar.DAY_OF_MONTH, 10);
		Date desmplStartDate = add(desmplEndDate, Calendar.DAY_OF_MONTH, 1);
		addData(aonContext, contract, startDate, desmplEndDate, ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT, "1.55");
		addData(aonContext, contract, desmplStartDate, endDate, ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT, "1.55");
		addData(aonContext, contract, startDate, desmplEndDate, ContextVariable.UNEMPLOY_ENTERPRISE_PERCENT, "5.50");
		addData(aonContext, contract, desmplStartDate, endDate, ContextVariable.UNEMPLOY_ENTERPRISE_PERCENT, "5.50");
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new RoundSalaryBuilder<Salary>(new SalaryBuilder() {
		}, d -> d) ).calculate(ctx);
		
		for ( SalaryPayment s : salary.getSalaryPayments() ) 
			System.out.println("P " + s.getDescription() + " = " + s.getAmount() + "," + s.getType()+ ", " + s.getQuote());

		double quote = salary.getSalaryPayments().stream().collect(Collectors.summingDouble(p -> p.getQuote()));
		
		for ( SalaryDeduction s : salary.getSalaryDeductions() ) 
			System.out.println(s.getDescription() + " = " + s.getAmount() + "," + s.getType());

		for ( SalaryCost s : salary.getSalaryCosts() ) 
			System.out.println(s.getDescription() + " = " + s.getAmount() + "," + s.getType());
		
		
		
		double cost = salary.getSalaryCosts().stream().collect(Collectors.summingDouble(c -> c.getAmount()));

		double deduction = salary.getSalaryDeductions().stream().collect(Collectors.summingDouble(c -> c.getAmount()));
		
		int monthDays = AonDateUtils.get(endDate, Calendar.DAY_OF_MONTH);
		
		assertEquals(1381.20, salary.getCommonBase(), DELTA);
		assertEquals(1000.00, salary.getProfessionalBase(), 0.009);
		assertEquals(salary.getProfessionalBase() * 1.55 / 100.00, deduction, DELTA);
		assertEquals(salary.getProfessionalBase() * 5.50 / 100.00, cost, DELTA);
		
	}
	
	@Test
	public void testCostPercentsPeriodsII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		addSystemData(aonContext, getFirstDayOfYear(getToday()), null, 
				new HashMap<String,String>(){
					private static final long serialVersionUID = 1L;

			{
				put(CGC_BASE_MIN.getName(), "(1381.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30))");
				put(CGP_BASE_MIN.getName(), "(1381.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30))");
			}
		});

		ContractRecord contract = newContract(aonContext,  
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>(){{
					put("DIAS_MES", "30.00");
				}
				}
				, new String[] { 
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES" 
						}
				, new String[] {
						"BASE_CGP * ( isdef PORCENTAJE_DESMPL ? PORCENTAJE_DESMPL : PORCENTAJE_DESMPL=(INDEFINIDO ? 1.55 : 1.60 ))/100"
				}, null);
		//@formatter:on
		
		addCost(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "(PORCENTAJE_DESMPL == 0) ? 0.00 : ( BASE_CGP_E * ( isdef PORCENTAJE_DESMPL_E ? PORCENTAJE_DESMPL_E : PORCENTAJE_DESMPL_E=(INDEFINIDO ? 5.50 : (TIEMPO_COMPLETO ? 6.70 : 7.70)))/100)", "0.60 %", "DESMPL_E");
		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		SQLITTestCase.addPrestITs(aonContext, contract);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, add(startDate, Calendar.DAY_OF_MONTH, 9), null, 1000.00 / 30.00);
		
		Date desmplEndDate = add(startDate, Calendar.DAY_OF_MONTH, 10);
		Date desmplStartDate = add(desmplEndDate, Calendar.DAY_OF_MONTH, 1);
		addData(aonContext, contract, startDate, desmplEndDate, ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT, "1.55");
		addData(aonContext, contract, desmplStartDate, endDate, ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT, "1.55");
		addData(aonContext, contract, startDate, desmplEndDate, ContextVariable.UNEMPLOY_ENTERPRISE_PERCENT, "5.50");
		addData(aonContext, contract, desmplStartDate, endDate, ContextVariable.UNEMPLOY_ENTERPRISE_PERCENT, "5.50");
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new RoundSalaryBuilder<Salary>(new SalaryBuilder() {
		}, d -> d) ).calculate(ctx);
		
		for ( SalaryPayment s : salary.getSalaryPayments() ) 
			System.out.println("P " + s.getDescription() + " = " + s.getAmount() + "," + s.getType()+ ", " + s.getQuote());

		double quote = salary.getSalaryPayments().stream().collect(Collectors.summingDouble(p -> p.getQuote()));
		
		for ( SalaryDeduction s : salary.getSalaryDeductions() ) 
			System.out.println(s.getDescription() + " = " + s.getAmount() + "," + s.getType());

		for ( SalaryCost s : salary.getSalaryCosts() ) 
			System.out.println(s.getDescription() + " = " + s.getAmount() + "," + s.getType());
		
		
		
		double cost = salary.getSalaryCosts().stream().collect(Collectors.summingDouble(c -> c.getAmount()));

		double deduction = salary.getSalaryDeductions().stream().collect(Collectors.summingDouble(c -> c.getAmount()));
		
		int monthDays = AonDateUtils.get(endDate, Calendar.DAY_OF_MONTH);
		
		assertEquals(1381.20, salary.getCommonBase(), DELTA);
		assertEquals(1381.20, salary.getProfessionalBase(), 0.009);
		assertEquals(salary.getProfessionalBase() * 1.55 / 100.00, deduction, DELTA);
		assertEquals(salary.getProfessionalBase() * 5.50 / 100.00, cost, DELTA);
		
	}
	
}
