package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C100;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C109;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C130;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C139;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C150;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C189;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C401;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C402;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C403;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C408;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C410;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C418;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C420;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C421;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C430;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C441;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C450;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C452;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.lang.String.format;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.junit.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;

import junit.framework.Assert;

public class SQLSpecialDaysTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000001;
	
	private static ContractCode FULL_TIME[] = { C100, C109, C130, C139,
			C150,
			C189, // indefinite fulltime
			C401, C402, C403, C408, C410, C418, C420, C421, C430, C441, C450,
			C452, // partial & temp fulltime
	};
	
	@Test
	public void testInactivityDaysI() throws ExpressionException, SQLException,
			SalaryException {
		
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
					{
						put("GRUPO_COTIZACION","'04'");
						put("DIAS_MES", "30");
					}
				}
				, new String[] { 
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {}, 
				null);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		// Calculate Salary for all month without inactivities
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, 
				new java.sql.Date(startDate.getTime()), 
				new java.sql.Date(endDate.getTime()), 
				new java.sql.Date(endDate.getTime()),
				contract);
		
		SalaryBuilder builder = new SalaryBuilder();
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator(builder);
		
		Salary salary = calculator.calculate(ctx);
		
		Assert.assertEquals(1000.00, salary.getTotalPayment(), DELTA);
		
		// Add InactivityDays Period -> 14/12/2019 - 24/12/2019
		Calendar startDateIDay = Calendar.getInstance();
		startDateIDay.set(Calendar.MONTH, 11);
		startDateIDay.set(Calendar.YEAR, 2019);
		startDateIDay.set(Calendar.DAY_OF_MONTH, 13);
		
		Calendar endDateIDay = Calendar.getInstance();
		endDateIDay.set(Calendar.MONTH, 11);
		endDateIDay.set(Calendar.YEAR, 2019);
		endDateIDay.set(Calendar.DAY_OF_MONTH, 23);

		addInactivityContractData(aonContext, contract, startDateIDay.getTime(), endDateIDay.getTime());
		
		// Calculate Salary for all month with inactivities
		ctx = getContractSalaryCalculatorContext(
				connection, 
				new java.sql.Date(startDate.getTime()), 
				new java.sql.Date(endDate.getTime()), 
				new java.sql.Date(endDate.getTime()),
				contract);
		
		builder = new SalaryBuilder();
		calculator = new SmartContractSalaryCalculator(builder);
		
		salary = calculator.calculate(ctx);
		
		Assert.assertEquals(1000.00 * 20 / 30, salary.getTotalPayment(), DELTA);
		
	}
	
	@Test
	public void testInactivityDaysII() throws ExpressionException, SQLException,
			SalaryException {
		
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
					{
						put("GRUPO_COTIZACION","'04'");
						put("DIAS_MES", "30");
					}
				}
				, new String[] { 
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {}, 
				null);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		// Calculate Salary for all month without inactivities
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, 
				new java.sql.Date(startDate.getTime()), 
				new java.sql.Date(endDate.getTime()), 
				new java.sql.Date(endDate.getTime()),
				contract);
		
		SalaryBuilder builder = new SalaryBuilder();
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator(builder);
		
		Salary salary = calculator.calculate(ctx);
		
		Assert.assertEquals(1, salary.getSalaryDatas().stream().filter(sd -> sd.getName().equals("DIAS_COTIZADOS")).count(), DELTA);
		
		// Add InactivityDays Period -> 13/12/2019 - 20/12/2019
		Calendar startDateIDay = Calendar.getInstance();
		startDateIDay.set(Calendar.MONTH, 11);
		startDateIDay.set(Calendar.YEAR, 2019);
		startDateIDay.set(Calendar.DAY_OF_MONTH, 13);
		
		Calendar endDateIDay = Calendar.getInstance();
		endDateIDay.set(Calendar.MONTH, 11);
		endDateIDay.set(Calendar.YEAR, 2019);
		endDateIDay.set(Calendar.DAY_OF_MONTH, 20);

		addInactivityContractData(aonContext, contract, startDateIDay.getTime(), endDateIDay.getTime());
		
		// Calculate Salary for all month with inactivities
		ctx = getContractSalaryCalculatorContext(
				connection, 
				new java.sql.Date(startDate.getTime()), 
				new java.sql.Date(endDate.getTime()), 
				new java.sql.Date(endDate.getTime()),
				contract);
		
		builder = new SalaryBuilder();
		calculator = new SmartContractSalaryCalculator(builder);
		
		salary = calculator.calculate(ctx);
		
		Assert.assertEquals(2, salary.getSalaryDatas().stream().filter(sd -> sd.getName().equals("DIAS_COTIZADOS")).count(), DELTA);
		
		// Add InactivityDays Period -> 24/12/2019 - 27/12/2019
		Calendar startDateIDay2 = Calendar.getInstance();
		startDateIDay2.set(Calendar.MONTH, 11);
		startDateIDay2.set(Calendar.YEAR, 2019);
		startDateIDay2.set(Calendar.DAY_OF_MONTH, 24);
		
		Calendar endDateIDay2 = Calendar.getInstance();
		endDateIDay2.set(Calendar.MONTH, 11);
		endDateIDay2.set(Calendar.YEAR, 2019);
		endDateIDay2.set(Calendar.DAY_OF_MONTH, 27);

		addInactivityContractData(aonContext, contract, startDateIDay2.getTime(), endDateIDay2.getTime());
		
		// Calculate Salary for all month with inactivities
		ctx = getContractSalaryCalculatorContext(
				connection, 
				new java.sql.Date(startDate.getTime()), 
				new java.sql.Date(endDate.getTime()), 
				new java.sql.Date(endDate.getTime()),
				contract);
		
		builder = new SalaryBuilder();
		calculator = new SmartContractSalaryCalculator(builder);
		
		salary = calculator.calculate(ctx);
		
		Assert.assertEquals(3, salary.getSalaryDatas().stream().filter(sd -> sd.getName().equals("DIAS_COTIZADOS")).count(), DELTA);
		Assert.assertEquals(1000.00 * 19 /30, salary.getTotalPayment(), DELTA);
		Assert.assertEquals(1000.00 * 19 /30, salary.getCommonBase(), DELTA);
		
	}
	
	@Test
	public void testDropDaysI() throws ExpressionException, SQLException,
			SalaryException {
		
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		
		addSystemData(
				aonContext, 
				AonDateUtils.getFirstDayOfYear(getToday()), 
				null, 
				new HashMap<String,String>(){
					{
						put("BASE_CGC_MIN", "[\"04\":1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)][GRUPO_COTIZACION]");
					}
				});
		
		
		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
					{
						put("GRUPO_COTIZACION","'04'");
						put("DIAS_MES", "30");
						put(TC2.getName(), format("\"%s\"", FULL_TIME[0].getValue()));
					}
				}
				, new String[] { 
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {}, 
				null);
		
		addPayment(aonContext, contract, "DIAS DE AUSENCIA", "DIAS_AUSENCIA * 0.00", "_P", "BASE_CGC_MIN", PaymentType.CRA_0000, SalaryType.SALARY);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		// Calculate Salary for all month without dropdays
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, 
				new java.sql.Date(startDate.getTime()), 
				new java.sql.Date(endDate.getTime()), 
				new java.sql.Date(endDate.getTime()),
				contract);
		
		SalaryBuilder builder = new SalaryBuilder();
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator(builder);
		
		Salary salary = calculator.calculate(ctx);
		
		Assert.assertEquals(1500.00, salary.getTotalPayment(), DELTA);
		
		// Add DropDays Period -> 14/12/2019 - 24/12/2019
		Calendar startDateIDay = Calendar.getInstance();
		startDateIDay.set(Calendar.MONTH, 11);
		startDateIDay.set(Calendar.YEAR, 2019);
		startDateIDay.set(Calendar.DAY_OF_MONTH, 13);
		
		Calendar endDateIDay = Calendar.getInstance();
		endDateIDay.set(Calendar.MONTH, 11);
		endDateIDay.set(Calendar.YEAR, 2019);
		endDateIDay.set(Calendar.DAY_OF_MONTH, 23);

		addDropContractData(aonContext, contract, startDateIDay.getTime(), endDateIDay.getTime());
		
		// Calculate Salary for all month with drop
		ctx = getContractSalaryCalculatorContext(
				connection, 
				new java.sql.Date(startDate.getTime()), 
				new java.sql.Date(endDate.getTime()), 
				new java.sql.Date(endDate.getTime()),
				contract);
		
		builder = new SalaryBuilder();
		calculator = new SmartContractSalaryCalculator(builder);
		
		salary = calculator.calculate(ctx);
		
		for(SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getDescription() + " = " + p.getAmount() + " = " + p.getQuote());
		
		Assert.assertEquals(1500.00 * 20 / 30, salary.getTotalPayment(), DELTA);
		Assert.assertEquals(3, salary.getSalaryDatas().stream().filter(sd -> sd.getName().equals("BASE_CGC")).count(), DELTA);
		Assert.assertEquals(1500.00 * 20 / 30 + 35.00 * 10, salary.getCommonBase(), DELTA);
		
	}
	
	// --------------------------------------------------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------------------------------------------------

	private void addInactivityContractData(AONContext aonContext, ContractRecord contract, Date startDate, Date endDate) {
		aonContext.getDslContext().insertInto(CONTRACT_DATA)
			.set(CONTRACT_DATA.DOMAIN, contract.getDomain())
			.set(CONTRACT_DATA.NAME, "DIAS_INACTIVIDAD")
			.set(CONTRACT_DATA.CONTRACT, contract.getId())
			.set(CONTRACT_DATA.EXPRESSION, getDaysBetweenDates(startDate, endDate).toString())
			.set(CONTRACT_DATA.START_DATE, new java.sql.Date(startDate.getTime()))
			.set(CONTRACT_DATA.END_DATE, new java.sql.Date(endDate.getTime()))
			.execute();
	}
	
	private void addDropContractData(AONContext aonContext, ContractRecord contract, Date startDate, Date endDate) {
		aonContext.getDslContext().insertInto(CONTRACT_DATA)
			.set(CONTRACT_DATA.DOMAIN, contract.getDomain())
			.set(CONTRACT_DATA.NAME, "DIAS_AUSENCIA")
			.set(CONTRACT_DATA.CONTRACT, contract.getId())
			.set(CONTRACT_DATA.EXPRESSION, getDaysBetweenDates(startDate, endDate).toString())
			.set(CONTRACT_DATA.START_DATE, new java.sql.Date(startDate.getTime()))
			.set(CONTRACT_DATA.END_DATE, new java.sql.Date(endDate.getTime()))
			.execute();
	}
	
	private Integer getDaysBetweenDates(Date startDate, Date endDate) {
		if(null == startDate || null == endDate)
			return 0;
		
		return (endDate.getDate() - startDate.getDate()) + 1;
	}
}
