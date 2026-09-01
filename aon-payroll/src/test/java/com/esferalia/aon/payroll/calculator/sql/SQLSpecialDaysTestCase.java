package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.DIRECT_PAY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.LACK_PERIOD;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.LEAVE_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_DAYS;
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
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.lang.String.format;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryData;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.calculator.RoundSalaryBuilder;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.util.AonDateUtils;

public class SQLSpecialDaysTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000001;
	
	private static ContractCode FULL_TIME[] = { C100, C109, C130, C139,
			C150,
			C189, // indefinite fulltime
			C401, C402, C403, C408, C410, C418, C420, C421, C430, C441, C450,
			C452, // partial & temp fulltime
	};
	
	@Test
	public void testTotalWorkedDaysI() throws ExpressionException, SQLException,
			SalaryException {
		
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
					{
						put("DIAS_MES", "30");
						put("GRUPO_COTIZACION","'04'");
						put("COEFICIENTE_PARCIALIDAD","1");
					}
				}
				, new String[] { 
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {}, 
				null);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		// Add InactivityDays Period -> 14/12/2019 - 24/12/2019
		Calendar startDateIDay = Calendar.getInstance();
		startDateIDay.set(Calendar.DAY_OF_MONTH, 13);
		
		Calendar endDateIDay = Calendar.getInstance();
		endDateIDay.set(Calendar.DAY_OF_MONTH, 23);

		addInactivityContractData(aonContext, contract, startDateIDay.getTime(), endDateIDay.getTime());
		
		// Calculate Salary for all month with inactivities
		ISQLContractSalaryCalculatorContext ctx = 
		getContractSalaryCalculatorContext(
				connection, 
				new java.sql.Date(startDate.getTime()), 
				new java.sql.Date(endDate.getTime()), 
				new java.sql.Date(endDate.getTime()),
				contract);
		
		List<ITimedResult<Number>> days = ctx.getExpressionContext().eval("DIAS_TRABAJADOS_TOTALES", startDate, endDate, Number.class);
		
		assertEquals(1, days.size());
		assertEquals(get(endDate, Calendar.DAY_OF_MONTH)-11, days.get(0).getValue().intValue());
		
	}
	

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
		
		assertEquals(1000.00, salary.getTotalPayment(), DELTA);
		
		// Add InactivityDays Period -> 14/12/2019 - 24/12/2019
		Calendar startDateIDay = Calendar.getInstance();
		startDateIDay.set(Calendar.DAY_OF_MONTH, 13);
		
		Calendar endDateIDay = Calendar.getInstance();
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
		
		int lastDayOfMonth = AonDateUtils.get(endDate, Calendar.DAY_OF_MONTH);
		
		assertEquals(1000.00 * (lastDayOfMonth - 11) / 30, salary.getTotalPayment(), DELTA);
		
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
		
		assertEquals(1, salary.getSalaryDatas().stream().filter(sd -> sd.getName().equals("DIAS_COTIZADOS")).count(), DELTA);
		
		// Add InactivityDays Period -> 13/12/2019 - 20/12/2019
		Calendar startDateIDay = Calendar.getInstance();
		startDateIDay.set(Calendar.DAY_OF_MONTH, 13);
		
		Calendar endDateIDay = Calendar.getInstance();
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
		
		assertEquals(2, salary.getSalaryDatas().stream().filter(sd -> sd.getName().equals("DIAS_COTIZADOS")).count(), DELTA);
		
		// Add InactivityDays Period -> 24/12/2019 - 27/12/2019
		Calendar startDateIDay2 = Calendar.getInstance();
		startDateIDay2.set(Calendar.DAY_OF_MONTH, 24);
		
		Calendar endDateIDay2 = Calendar.getInstance();
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
		
		int lastDayOfMonth = AonDateUtils.get(endDate, Calendar.DAY_OF_MONTH);
		
		assertEquals(3, salary.getSalaryDatas().stream().filter(sd -> sd.getName().equals("DIAS_COTIZADOS")).count(), DELTA);
		assertEquals(1000.00 * (lastDayOfMonth - 12) /30, salary.getTotalPayment(), DELTA);
		assertEquals(1000.00 * (lastDayOfMonth - 12) /30, salary.getCommonBase(), DELTA);
		
	}
	
	@Test
	public void testInactivityDaysNoPayPermission() throws ExpressionException, SQLException,
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
					}
				}
				, new String[] { 
						"1050.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {}, 
				null);
		
		Date startDate = AonDateUtils.getFirstDayOfYear(getToday());
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

		// Add InactivityDays Period -> 13/12/2019 - 20/12/2019
		Calendar startDateIDay = Calendar.getInstance();
		startDateIDay.set(Calendar.MONTH, Calendar.JANUARY);
		startDateIDay.set(Calendar.DAY_OF_MONTH, 13);
		
		Calendar endDateIDay = Calendar.getInstance();
		endDateIDay.set(Calendar.MONTH, Calendar.JANUARY);
		endDateIDay.set(Calendar.DAY_OF_MONTH, 31);

		addInactivityContractData(aonContext, contract, startDateIDay.getTime(), endDateIDay.getTime(), ContextVariable.NOT_PAID_PERMISSION);
		
		addPayment(aonContext, contract, "PERMISO NO RETRIBUIDO", "((CAUSA_INACTIVIDAD == PERMISO_NO_RETRIBUIDO) ? DIAS_INACTIVIDAD * 0.00 : __HIDE_)", "_P", "BASE_CGC_MIN", PaymentType.CRA_0001, SalaryType.SALARY);
		
		// Calculate Salary for all month with inactivities
		ctx = getContractSalaryCalculatorContext(
				connection, 
				new java.sql.Date(startDate.getTime()), 
				new java.sql.Date(endDate.getTime()), 
				new java.sql.Date(endDate.getTime()),
				contract);
		
		builder = new SalaryBuilder();
		calculator = new SmartContractSalaryCalculator(builder);
		
		Salary salary = calculator.calculate(ctx);
		
		for ( SalaryPayment p : salary.getSalaryPayments() )
			System.out.println(p.getExpression() + " :" + p.getQuote());
		
		assertEquals(1050.00, salary.getCommonBase(), DELTA);
		
	}

	@Test
	public void testDropDaysI() throws ExpressionException, SQLException,
			SalaryException {
		
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		cleanSalaries(aonContext);
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
		
		addPayment(aonContext, contract, "DIAS DE AUSENCIA", "DIAS_AUSENCIA * 0.00", "_P", "/*fixBaseCgcMin*/MAX(0.00, BASE_CGC_MIN - (isdef BASE_CGC_BRUTA ? BASE_CGC_BRUTA : 0.00))", PaymentType.CRA_0001, SalaryType.SALARY);
		
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
		
		assertEquals(1500.00, salary.getTotalPayment(), DELTA);
		
		// Add DropDays Period -> 14/12/2019 - 24/12/2019
		Calendar startDateIDay = Calendar.getInstance();
		startDateIDay.set(Calendar.DAY_OF_MONTH, 13);
		
		Calendar endDateIDay = Calendar.getInstance();
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
			System.out.println(p.getDescription() + " = " + p.getAmount() + " = " + p.getQuote() );
		
		assertEquals(1500.00 * 19 / 30, salary.getTotalPayment(), DELTA);
		assertEquals(3, salary.getSalaryDatas().stream().filter(sd -> sd.getName().equals("BASE_CGC")).count(), DELTA);
		assertEquals(1500.00 * 19 / 30 + 35.00 * 11, salary.getCommonBase(), DELTA);
		
	}
	
	@Test
	public void testPartialDropDaysI() throws ExpressionException, SQLException,
			SalaryException {
		
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		cleanSalaries(aonContext);
		cleanSystemData(aonContext);
		
		addSystemData(
				aonContext, 
				AonDateUtils.getFirstDayOfYear(getToday()), 
				null, 
				new HashMap<String,String>(){
					{
						// 1381.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD
						put("BASE_CGC_MIN", "[\"04\": (1381.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD) ][GRUPO_COTIZACION]");
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
		
		addPayment(aonContext, contract, "DIAS DE AUSENCIA", "DIAS_AUSENCIA * 0.00", "_P", "/*fixBaseCgcMin*/MAX(0.00, BASE_CGC_MIN - (isdef BASE_CGC_BRUTA ? BASE_CGC_BRUTA : 0.00))", PaymentType.CRA_0001, SalaryType.SALARY);
		
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
		
		assertEquals(1500.00, salary.getTotalPayment(), DELTA);
		
		// Add DropDays Period -> 14/12/2019 - 24/12/2019
		Calendar startDateIDay = Calendar.getInstance();
		startDateIDay.set(Calendar.DAY_OF_MONTH, 13);
		
		Calendar endDateIDay = Calendar.getInstance();
		endDateIDay.set(Calendar.DAY_OF_MONTH, 23);

		addDropContractData(aonContext, contract, startDateIDay.getTime(), endDateIDay.getTime(), "0.4");
		
		// Calculate Salary for all month with drop
		ctx = getContractSalaryCalculatorContext(
				connection, 
				new java.sql.Date(startDate.getTime()), 
				new java.sql.Date(endDate.getTime()), 
				new java.sql.Date(endDate.getTime()),
				contract);
		
		builder = new SalaryBuilder() {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description, Date startDate,
					Date endDate, IPayment payment, Map<String, ITimedVariable<?>> context) {
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				System.out.println(description + ": " + amount + ", " + quote + " [" + startDate +".." + endDate + "]" );
			}
		};
		calculator = new SmartContractSalaryCalculator(builder);
		
		salary = calculator.calculate(ctx);
		
		int monthDays = get( endDate, Calendar.DAY_OF_MONTH);
		
		assertEquals(1500.00 * (monthDays - 11) / 30 + 1500.00 * 11 * 0.60 / 30 , salary.getTotalPayment(), DELTA);
		assertEquals(3, salary.getSalaryDatas().stream().filter(sd -> sd.getName().equals("BASE_CGC")).count(), DELTA);
		assertEquals(1500.00 * (monthDays - 11) / 30  + 1381.20 * 11 / 30 , salary.getCommonBase(), DELTA);
		
	}

	@Test
	public void testDropDaysAdjustI() throws ExpressionException, SQLException,
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
				getFirstDayOfMonth(getToday()),
				add(getFirstDayOfMonth(getToday()), Calendar.DAY_OF_MONTH,1),
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
		
		addPayment(aonContext, contract, "DIAS DE AUSENCIA", "DIAS_AUSENCIA * 0.00", "_P", "BASE_CGC_MIN", PaymentType.CRA_0001, SalaryType.SALARY);
		
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
		
		assertEquals(1500.00 /30 * get(contract.getEndDate(), Calendar.DAY_OF_MONTH), salary.getTotalPayment(), DELTA);
		
		addDropContractData(aonContext, contract, getFirstDayOfMonth(getToday()), contract.getEndDate());
		
		// Calculate Salary for all month with drop
		ctx = getContractSalaryCalculatorContext(
				connection, 
				new java.sql.Date(startDate.getTime()), 
				new java.sql.Date(endDate.getTime()), 
				new java.sql.Date(endDate.getTime()),
				contract);
		
		builder = new SalaryBuilder();
		calculator = new SmartContractSalaryCalculator<Salary>(builder);
		
		salary = calculator.calculate(ctx);
		
		for(SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getDescription() + " = " + p.getAmount() + " = " + p.getQuote());
		
		int activeDays = 0;
		int dropDays = AonDateUtils.get(contract.getEndDate(), Calendar.DAY_OF_MONTH); // not adjust

		assertEquals(1500.00 * activeDays / 30, salary.getTotalPayment(), DELTA);
		assertEquals(1, salary.getSalaryDatas().stream().filter(sd -> sd.getName().equals("BASE_CGC")).count(), DELTA);
		assertEquals(1500.00 * activeDays / 30 + 35.00 * dropDays, salary.getCommonBase(), DELTA);
		
	}
	
	@Test
	public void testDropDaysAdjustII() throws ExpressionException, SQLException,
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
				getFirstDayOfMonth(getToday()),
				add(getFirstDayOfMonth(getToday()), Calendar.DAY_OF_MONTH,14),
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
		
		addPayment(aonContext, contract, "DIAS DE AUSENCIA", "DIAS_AUSENCIA * 0.00", "_P", "BASE_CGC_MIN", PaymentType.CRA_0001, SalaryType.SALARY);
		
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
		
		assertEquals(1500.00 /30 * get(contract.getEndDate(), Calendar.DAY_OF_MONTH), salary.getTotalPayment(), DELTA);
		
		addDropContractData(aonContext, contract, getFirstDayOfMonth(getToday()), add(getFirstDayOfMonth(getToday()), Calendar.DAY_OF_MONTH,1) );
		
		// Calculate Salary for all month with drop
		ctx = getContractSalaryCalculatorContext(
				connection, 
				new java.sql.Date(startDate.getTime()), 
				new java.sql.Date(endDate.getTime()), 
				new java.sql.Date(endDate.getTime()),
				contract);
		
		builder = new SalaryBuilder();
		calculator = new SmartContractSalaryCalculator<Salary>(builder);
		
		salary = calculator.calculate(ctx);
		
		for(SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getDescription() + " = " + p.getAmount() + " = " + p.getQuote());
		
		int dropDays = 2; // not adjust
		int activeDays = 15 -dropDays;

		assertEquals(1500.00 * activeDays / 30, salary.getTotalPayment(), DELTA);
		assertEquals(2, salary.getSalaryDatas().stream().filter(sd -> sd.getName().equals("BASE_CGC")).count(), DELTA);
		assertEquals(1500.00 * activeDays / 30 + 35.00 * dropDays, salary.getCommonBase(), DELTA);
		
	}

	@Test
	public void testInactivityDaysBaseCgp() throws ExpressionException, SQLException,
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
						put("BASE_CGP_MIN", "1424.40 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)");
						put("BASE_CGC_MIN", "[\"03\":1435.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)][GRUPO_COTIZACION]");
					}
				});
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
					{
						put("DIAS_MES", "30");
						put("GRUPO_COTIZACION","'03'");
					}
				}
				, new String[] { 
						"3000.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {}, 
				null);
		
		
		// Add InactivityDays Period -> 01 - 01
		java.sql.Date inactivityDay = addMonths(AonDateUtils.getFirstDayOfYear(getToday()), 3);
		
		
		addInactivityContractData(aonContext, contract, inactivityDay, inactivityDay, ContextVariable.NOT_PAID_PERMISSION);
		
		PaymentConceptRecord  unpaid = addConcept(aonContext, "UNPAID");
		addPayment(aonContext, contract, unpaid, "PERMISO NO RETRIBUIDO", "((CAUSA_INACTIVIDAD == PERMISO_NO_RETRIBUIDO) ? DIAS_INACTIVIDAD * 0.00 : __HIDE_)", "_P", "BASE_CGC_MIN", PaymentType.CRA_0001);
		
		// Calculate Salary for all month with inactivities
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, 
				getFirstDayOfMonth(inactivityDay), 
				getLastDayOfMonth(inactivityDay), 
				getLastDayOfMonth(inactivityDay),
				contract);
		
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator(new SalaryBuilder());
		
		Salary salary = calculator.calculate(ctx);
		
		for ( SalaryPayment p : salary.getSalaryPayments() )
			System.out.println(p.getExpression() + " :" + p.getQuote());
		
		SalaryData baseCgp = salary.getSalaryDatas().stream().filter( data -> data.getName().equals("BASE_CGP_E"))
		.peek( data -> System.out.println(data.getName() + " = " + data.getExpression() + " [" + data.getStartDate() + ".." + data.getEndDate() + "]"))
		.filter( data -> data.getStartDate().equals(inactivityDay) && data.getEndDate().equals(inactivityDay))
		.findAny().orElseThrow();
		
		
		assertEquals(1424.40 / 30, Double.valueOf(baseCgp.getExpression()), DELTA);
		
		SalaryData baseCgc = salary.getSalaryDatas().stream().filter( data -> data.getName().equals("BASE_CGC_E"))
		.peek( data -> System.out.println(data.getName() + " = " + data.getExpression() + " [" + data.getStartDate() + ".." + data.getEndDate() + "]"))
		.filter( data -> data.getStartDate().equals(inactivityDay) && data.getEndDate().equals(inactivityDay))
		.findAny().orElseThrow();

		assertEquals(1435.20 / 30, Double.valueOf(baseCgc.getExpression()), DELTA);
	}

	@Test
	public void testInactivityDaysBaseCgpRoundI() throws ExpressionException, SQLException,
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
						put("BASE_CGP_MIN", "1424.40 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)");
						put("BASE_CGC_MIN", "[\"03\":1435.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)][GRUPO_COTIZACION]");
					}
				});
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
					{
						put("DIAS_MES", "30");
						put("GRUPO_COTIZACION","'03'");
					}
				}
				, new String[] { 
						"3000.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {}, 
				null);
		
		
		// Add InactivityDays Period -> 01 - 01
		java.sql.Date startDate = AonDateUtils.getFirstDayOfMonth(getToday());
		java.sql.Date endDate = AonDateUtils.getLastDayOfMonth(getToday());
		
		
		addInactivityContractData(aonContext, contract, startDate, endDate, ContextVariable.NOT_PAID_PERMISSION);
		
		PaymentConceptRecord  unpaid = addConcept(aonContext, "UNPAID");
		addPayment(aonContext, contract, unpaid, "PERMISO NO RETRIBUIDO", "((CAUSA_INACTIVIDAD == PERMISO_NO_RETRIBUIDO) ? DIAS_INACTIVIDAD * 0.00 : __HIDE_)", "_P", "/*fixBaseCgcMin*/BASE_CGC_MIN", PaymentType.CRA_0001);
		
		// Calculate Salary for all month with inactivities
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, 
				startDate, 
				endDate, 
				endDate,
				contract);
		
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator(new RoundSalaryBuilder<Salary>(new SalaryBuilder(), d -> d.setScale(2, RoundingMode.HALF_UP) ));
		
		Salary salary = calculator.calculate(ctx);
		
		for ( SalaryPayment p : salary.getSalaryPayments() )
			System.out.println(p.getExpression() + " :" + p.getQuote());
		
		SalaryData baseCgp = salary.getSalaryDatas().stream().filter( data -> data.getName().equals("BASE_CGP_E"))
		.peek( data -> System.out.println(data.getName() + " = " + data.getExpression() + " [" + data.getStartDate() + ".." + data.getEndDate() + "]"))
		.filter( data -> data.getStartDate().equals(startDate) && data.getEndDate().equals(endDate))
		.findAny().orElseThrow();
		
		
		assertEquals(1424.40, Double.valueOf(baseCgp.getExpression()), DELTA);
		assertEquals(1424.40, salary.getProfessionalBase(), DELTA);
		
		SalaryData baseCgc = salary.getSalaryDatas().stream().filter( data -> data.getName().equals("BASE_CGC_E"))
		.peek( data -> System.out.println(data.getName() + " = " + data.getExpression() + " [" + data.getStartDate() + ".." + data.getEndDate() + "]"))
		.filter( data -> data.getStartDate().equals(startDate) && data.getEndDate().equals(endDate))
		.findAny().orElseThrow();

		assertEquals(1435.20 , Double.valueOf(baseCgc.getExpression()), DELTA);
		assertEquals(1435.20 , salary.getCommonBase(), DELTA);
	}

	@Test
	public void testInactivityDaysBaseCgpRoundII() throws ExpressionException, SQLException,
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
						put("BASE_CGP_MIN", "1424.40 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)");
						put("BASE_CGC_MIN", "[\"03\":1435.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)][GRUPO_COTIZACION]");
					}
				});
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
					{
						put("DIAS_MES", "30");
						put("GRUPO_COTIZACION","'03'");
					}
				}
				, new String[] { 
						"3000.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {}, 
				null);
		
		
		// Add InactivityDays Period -> 01 - 01
		java.sql.Date startDate = AonDateUtils.getFirstDayOfMonth(getToday());
		java.sql.Date endDate = AonDateUtils.getLastDayOfMonth(getToday());
		
		java.sql.Date startInactivityDate = addDays(startDate, 10);
		java.sql.Date endInactivityDate = addDays(startDate, 19);
		
		
		addInactivityContractData(aonContext, contract, startInactivityDate, endInactivityDate, ContextVariable.NOT_PAID_PERMISSION);
		
		PaymentConceptRecord  unpaid = addConcept(aonContext, "UNPAID");
		addPayment(aonContext, contract, unpaid, "PERMISO NO RETRIBUIDO", "((CAUSA_INACTIVIDAD == PERMISO_NO_RETRIBUIDO) ? DIAS_INACTIVIDAD * 0.00 : __HIDE_)", "_P", "/*fixBaseCgcMin*/BASE_CGC_MIN", PaymentType.CRA_0001);
		
		// Calculate Salary for all month with inactivities
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, 
				startDate, 
				endDate, 
				endDate,
				contract);
		
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator(new RoundSalaryBuilder<Salary>(new SalaryBuilder(), d -> d.setScale(2, RoundingMode.HALF_UP) ));
		
		Salary salary = calculator.calculate(ctx);
		
		for ( SalaryPayment p : salary.getSalaryPayments() )
			System.out.println(p.getExpression() + " :" + p.getQuote());
		
		SalaryData baseCgp = salary.getSalaryDatas().stream().filter( data -> data.getName().equals("BASE_CGP_E"))
		.peek( data -> System.out.println(data.getName() + " = " + data.getExpression() + " [" + data.getStartDate() + ".." + data.getEndDate() + "]"))
		.filter( data -> data.getStartDate().equals(startInactivityDate) && data.getEndDate().equals(endInactivityDate))
		.findAny().orElseThrow();
		
		
		assertEquals(1424.40 / 30 * 10, Double.valueOf(baseCgp.getExpression()), DELTA);
		assertEquals(1424.40 / 30 * 10 + 3000.00 / 30 * (get(endDate, Calendar.DAY_OF_MONTH) - 10 ), salary.getProfessionalBase(), DELTA);
		
		SalaryData baseCgc = salary.getSalaryDatas().stream().filter( data -> data.getName().equals("BASE_CGC_E"))
		.peek( data -> System.out.println(data.getName() + " = " + data.getExpression() + " [" + data.getStartDate() + ".." + data.getEndDate() + "]"))
		.filter( data -> data.getStartDate().equals(startInactivityDate) && data.getEndDate().equals(endInactivityDate))
		.findAny().orElseThrow();

		assertEquals(1435.20 / 30.00 * 10 , Double.valueOf(baseCgc.getExpression()), DELTA);
		assertEquals(1435.20 / 30.00 * 10 + 3000.00 / 30 * (get(endDate, Calendar.DAY_OF_MONTH) - 10 ) , salary.getCommonBase(), DELTA);
	}

	@Test
	public void testDropDaysUnjustified() throws ExpressionException, SQLException,
			SalaryException {
		
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		cleanSalaries(aonContext);
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
				, new String[] {
						"BASE_CGC * 4.7 / 100",
						"BASE_CGP * 1.55 / 100",
						"BASE_CGP * 0.10 / 100",
						"BASE_CGC * 0.15 / 100",
				}, 
				null);
		
		PaymentConceptRecord  unpaid = addConcept(aonContext, "UNPAID");
		addPayment(aonContext, contract, unpaid, "AUSENCIA INJUSTIFICADA", "CAUSA_AUSENCIA == AUSENCIA_NO_JUSTIFICADA ? DIAS_AUSENCIA * 0.00 : __HIDE_", "_P", "/*fixBaseCgcMin*/MAX(0.00, BASE_CGC_MIN - (isdef BASE_CGC_BRUTA ? BASE_CGC_BRUTA : 0.00))", PaymentType.CRA_0001);
		addPayment(aonContext, contract, unpaid, "DIAS DE AUSENCIA", "isdef CAUSA_AUSENCIA ? __HIDE_ :  DIAS_AUSENCIA * 0.00", "_P", "/*fixBaseCgcMin*/MAX(0.00, BASE_CGC_MIN - (isdef BASE_CGC_BRUTA ? BASE_CGC_BRUTA : 0.00))", PaymentType.CRA_0001);
		
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
		
		assertEquals(1500.00, salary.getTotalPayment(), DELTA);
		
		// Add DropDays Period -> 14/12/2019 - 24/12/2019
		Calendar startDateIDay = Calendar.getInstance();
		startDateIDay.set(Calendar.DAY_OF_MONTH, 13);
		
		Calendar endDateIDay = Calendar.getInstance();
		endDateIDay.set(Calendar.DAY_OF_MONTH, 23);

		addDropContractData(aonContext, contract, startDateIDay.getTime(), endDateIDay.getTime(), ContextVariable.DROP_NOT_JUSTIFIED);
		
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
			System.out.println(p.getDescription() + " = " + p.getAmount() + " = " + p.getQuote() );
		
		assertEquals(1500.00 * 19 / 30, salary.getTotalPayment(), DELTA);
		assertEquals(2, salary.getSalaryDatas().stream().filter(sd -> sd.getName().equals("BASE_CGC")).count(), DELTA);
		assertEquals(1500.00 * 19 / 30 + 35.00 * 11, salary.getCommonBase(), DELTA);
		
		assertEquals(1500.00 * 19 / 30 * ( 4.70 + 1.55 + 0.10 + 0.15) / 100, salary.getSocialSecurityContributions(), DELTA);
		
	}

	@Test
	@Disabled
	public void testDropDaysWithConstantI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		java.sql.Date startContract = add(getFirstDayOfMonth(getToday()), MONTH, -2 );
		//@formatter:off
		ContractRecord contract = newContract(
				aonContext,
				startContract,
				new HashMap<String,String>(){
					{
						put(MONTH_DAYS.getName(), "30");
						put("BASE_CGC_MIN","1424.50 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)");

					}
				},
				new String[] {
				}, 
				new String[] {
				}, 
				null);
		//@formatter:on

		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT,PaymentType.CRA_0000);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 1.00 * %s_1_3 * (isdef %s ? %s : 1.00)",  COMMON_DISEASE_DAYS, LEAVE_FACTOR, LEAVE_FACTOR),
				String.format("BASE_REGULADORA * %s * (isdef %s ? %s : 1.00)",  QUOTE_DAYS, LEAVE_FACTOR, LEAVE_FACTOR)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 1.00 * %s_4_15 * (isdef %s ? %s : 1.00)",  COMMON_DISEASE_DAYS, LEAVE_FACTOR, LEAVE_FACTOR),
				String.format("BASE_REGULADORA * %s * (isdef %s ? %s : 1.00)",  QUOTE_DAYS, LEAVE_FACTOR, LEAVE_FACTOR));
		addPayment(aonContext, contract, "DIAS DE AUSENCIA", "DIAS_AUSENCIA * 0.00", "_P",
				"/*fixBaseCgcMin*/MAX(0.00, BASE_CGC_MIN - (isdef BASE_CGC_BRUTA ? BASE_CGC_BRUTA : 0.00))",
				PaymentType.CRA_0001, SalaryType.SALARY);		

		java.sql.Date startDate = getFirstDayOfMonth(getToday());
		java.sql.Date endDate = getLastDayOfMonth(startDate);
		addPayment(aonContext, contract, startDate, endDate, "SALARIO BASE", "1750.00", "_P", "_P", PaymentType.CRA_0001, SalaryType.SALARY);

		java.sql.Date startDropDate = add(startDate, DAY_OF_MONTH,22);
		java.sql.Date endDropDate = add(startDropDate, DAY_OF_MONTH,0);
		
		addDropContractData(aonContext, contract, startDropDate, endDropDate);
		
		java.sql.Date endITDate = add(startDate, DAY_OF_MONTH,6);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startDate,
				endITDate, 1750.00 / 30.00);


		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>(); 

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments())
			System.out.println(payment.getName() + " = " + payment.getAmount() + ", " + payment.getQuote()
					+ " (" + payment.getExpression() + ")");

		assertEquals(1750.00 / 30.00 * 28.00 + 1424.50 / 30.00 * 2, salary.getCommonBase(), DELTA);
		assertEquals(1750.00 / 30.00 * 28.00, salary.getTotalPayment(), DELTA);

//		startDate = add(startDate, MONTH, 1);
//		endDate = getLastDayOfMonth(startDate);
//		ctx = getContractSalaryCalculatorContext(
//				connection, startDate, endDate, endDate, contract);
//
//		calculator.setSalaryBuilder(new SalaryBuilder());
//		salary = calculator.calculate(ctx);
//
//		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
//				.getSalaryPayments())
//			System.out.println(payment.getName() + " = " + payment.getAmount() + ", " + payment.getQuote()
//					+ " (" + payment.getExpression() + ")");
//
//		assertEquals(1750.00, salary.getCommonBase(), DELTA);
//		assertEquals(1750.00/30.00 * get(endDate, DAY_OF_MONTH), salary.getTotalPayment(), DELTA);
	}

	@Test
	public void testDropDaysAndQuoteDaysI() throws ExpressionException, SQLException,
			SalaryException {
		
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		cleanSalaries(aonContext);
		cleanSystemData(aonContext);
		
		
		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
					{
						put(TC2.getName(), format("\"%s\"", FULL_TIME[0].getValue()));
					}
				}
				, new String[] { 
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {}, 
				null);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		
		// Add DropDays Period -> 14 - 24
		Calendar startDateIDay = Calendar.getInstance();
		startDateIDay.set(Calendar.DAY_OF_MONTH, 13);
		
		Calendar endDateIDay = Calendar.getInstance();
		endDateIDay.set(Calendar.DAY_OF_MONTH, 23);

		addDropContractData(aonContext, contract, startDateIDay.getTime(), endDateIDay.getTime());
		
		// Calculate Salary for all month with drop
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, 
				new java.sql.Date(startDate.getTime()), 
				new java.sql.Date(endDate.getTime()), 
				new java.sql.Date(endDate.getTime()),
				contract);
		
		double monthDays = ctx.getExpressionContext().eval("DIAS_MES", startDate, endDate, Number.class).stream().map(ITimedResult::getValue).collect(Collectors.summingDouble(Number::doubleValue));
		double quoteDays = ctx.getExpressionContext().eval("DIAS_COTIZADOS", startDate, endDate, Number.class).stream().peek(d -> System.out.println(d.getValue() + "," + d.getPeriod().getStart() + ".." + d.getPeriod().getEnd())).map(ITimedResult::getValue).collect(Collectors.summingDouble(Number::doubleValue));
		assertEquals(monthDays, quoteDays, DELTA);
	}
	
	@Test
	public void testDropDaysAndQuoteDaysII() throws ExpressionException, SQLException,
			SalaryException {
		
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		cleanSalaries(aonContext);
		cleanSystemData(aonContext);
		
		
		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
					{
						put(TC2.getName(), format("\"%s\"", FULL_TIME[0].getValue()));
					}
				}
				, new String[] { 
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {}, 
				null);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		
		// Add DropDays Period -> 14 - 24
		Calendar startDateIDay = Calendar.getInstance();
		startDateIDay.set(Calendar.DAY_OF_MONTH, 13);
		
		Calendar endDateIDay = Calendar.getInstance();
		endDateIDay.set(Calendar.DAY_OF_MONTH, 23);

		addDropContractData(aonContext, contract, startDateIDay.getTime(), endDateIDay.getTime(), "0.5");
		
		// Calculate Salary for all month with drop
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, 
				new java.sql.Date(startDate.getTime()), 
				new java.sql.Date(endDate.getTime()), 
				new java.sql.Date(endDate.getTime()),
				contract);
		
		double monthDays = ctx.getExpressionContext().eval("DIAS_MES", startDate, endDate, Number.class).stream().map(ITimedResult::getValue).collect(Collectors.summingDouble(Number::doubleValue));
		double quoteDays = ctx.getExpressionContext().eval("DIAS_COTIZADOS", startDate, endDate, Number.class).stream().peek(d -> System.out.println(d.getValue() + "," + d.getPeriod().getStart() + ".." + d.getPeriod().getEnd())).map(ITimedResult::getValue).collect(Collectors.summingDouble(Number::doubleValue));
		assertEquals(monthDays, quoteDays, DELTA);
	}
	// --------------------------------------------------------------------------------------------------------------------------

	protected static void addInactivityContractData(AONContext aonContext, ContractRecord contract, Date startDate, Date endDate) {
		aonContext.getDslContext().insertInto(CONTRACT_DATA)
			.set(CONTRACT_DATA.DOMAIN, contract.getDomain())
			.set(CONTRACT_DATA.NAME, "DIAS_INACTIVIDAD")
			.set(CONTRACT_DATA.CONTRACT, contract.getId())
			.set(CONTRACT_DATA.EXPRESSION, getDaysBetweenDates(startDate, endDate).toString())
			.set(CONTRACT_DATA.START_DATE, new java.sql.Date(startDate.getTime()))
			.set(CONTRACT_DATA.END_DATE, new java.sql.Date(endDate.getTime()))
			.execute();
	}
	
	protected static void addInactivityContractData(AONContext aonContext, ContractRecord contract, Date startDate, Date endDate, ContextVariable offType) {
		addInactivityContractData(aonContext, contract, startDate, endDate);
		aonContext.getDslContext().insertInto(CONTRACT_DATA)
		.set(CONTRACT_DATA.DOMAIN, contract.getDomain())
		.set(CONTRACT_DATA.NAME, "CAUSA_INACTIVIDAD")
		.set(CONTRACT_DATA.CONTRACT, contract.getId())
		.set(CONTRACT_DATA.EXPRESSION, offType.getName())
		.set(CONTRACT_DATA.START_DATE, new java.sql.Date(startDate.getTime()))
		.set(CONTRACT_DATA.END_DATE, new java.sql.Date(endDate.getTime()))
		.execute();
		
	}

	protected static void addDropContractData(AONContext aonContext, ContractRecord contract, Date startDate, Date endDate) {
		aonContext.getDslContext().insertInto(CONTRACT_DATA)
			.set(CONTRACT_DATA.DOMAIN, contract.getDomain())
			.set(CONTRACT_DATA.NAME, "COEFICIENTE_AUSENCIA")
			.set(CONTRACT_DATA.CONTRACT, contract.getId())
			.set(CONTRACT_DATA.EXPRESSION, "1.0")
			.set(CONTRACT_DATA.START_DATE, new java.sql.Date(startDate.getTime()))
			.set(CONTRACT_DATA.END_DATE, new java.sql.Date(endDate.getTime()))
			.execute();
	}
	
	protected static void addDropContractData(AONContext aonContext, ContractRecord contract, Date startDate, Date endDate, ContextVariable dropType) {
		addDropContractData(aonContext, contract, startDate, endDate);
		aonContext.getDslContext().insertInto(CONTRACT_DATA)
			.set(CONTRACT_DATA.DOMAIN, contract.getDomain())
			.set(CONTRACT_DATA.NAME, "CAUSA_AUSENCIA")
			.set(CONTRACT_DATA.CONTRACT, contract.getId())
			.set(CONTRACT_DATA.EXPRESSION, dropType.getName())
			.set(CONTRACT_DATA.START_DATE, new java.sql.Date(startDate.getTime()))
			.set(CONTRACT_DATA.END_DATE, new java.sql.Date(endDate.getTime()))
			.execute();
	}

	protected static void addDropContractData(AONContext aonContext, ContractRecord contract, Date startDate, Date endDate, String factor) {
		aonContext.getDslContext().insertInto(CONTRACT_DATA)
			.set(CONTRACT_DATA.DOMAIN, contract.getDomain())
			.set(CONTRACT_DATA.NAME, "COEFICIENTE_AUSENCIA")
			.set(CONTRACT_DATA.CONTRACT, contract.getId())
			.set(CONTRACT_DATA.EXPRESSION, factor)
			.set(CONTRACT_DATA.START_DATE, new java.sql.Date(startDate.getTime()))
			.set(CONTRACT_DATA.END_DATE, new java.sql.Date(endDate.getTime()))
			.execute();
	}

	private static Integer getDaysBetweenDates(Date startDate, Date endDate) {
		if(null == startDate || null == endDate)
			return 0;
		
		return (endDate.getDate() - startDate.getDate()) + 1;
	}
	

}
