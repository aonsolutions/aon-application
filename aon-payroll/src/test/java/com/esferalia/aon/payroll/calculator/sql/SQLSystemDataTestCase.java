package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS;
import static com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SalaryEmbargo.SALARY_EMBARGO;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.payroll.enumeration.SSRegimeType.AGRICULTURAL;
import static com.esferalia.aon.salary.enumeration.DeductionType.COMMON_CONTINGENCY;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.Calendar.DAY_OF_MONTH;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;

import junit.framework.Assert;

public class SQLSystemDataTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000001;

	@Test
	public void testSSRegime()
			throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date firstDayOfYear = getFirstDayOfYear(getToday());

		cleanSystemData(aonContext);

		//@formatter:off
		addCCCData(aonContext,
				CCCType.AGRICULTURAL,
				firstDayOfYear, 
				null, 
				new HashMap<String,String>(){
				{
					put("COTIZACION_MENSUAL","true");
					put("BASE_CGP_MIN","BASE_CGC_MIN");
				}
		});
		addCCCData(aonContext,
				CCCType.AGRICULTURAL,
				firstDayOfYear, 
				null, 
				new HashMap<String,String>(){
				{
					put("CENTINEL","666");
					
					put("REDUCCION_CGC_E_01","8.10");
					put("REDUCCION_CGC_E_02","COTIZACION_MENSUAL ? ((BASE_CGC <= 986.70) ? 6.50 : ((BASE_CGC <= 2595.60) ? (6.50 * ( 1 + (BASE_CGC - 986.70)/ BASE_CGC * 2.52 * 6.15 / 6.50)) : 0.00)) : ((BASE_CGC / JORNADAS_REALES <= 42.90) ? 6.50 : ((BASE_CGC / JORNADAS_REALES <= 112.85 )? (6.50 * ( 1 + (BASE_CGC / JORNADAS_REALES - 42.90) / ( BASE_CGC / JORNADAS_REALES ) * 2.52 * 6.15 / 6.50)) : 0.00))");
					put("PORCENTAJE_CGC_E", "(GRUPO_COTIZACION == \"01\") ? (23.260 - REDUCCION_CGC_E_01) : (16.85 - REDUCCION_CGC_E_02)");

					put("BASE_CGC_MIN_MES","[\"01\":1051.50, \"02\":872.10, \"03\":758.70, \"04\":753.00, \"05\":753.00, \"06\":753.00, \"07\":753.00, \"08\":753.00, \"09\":753.00, \"10\":753.00, \"11\":753.00][GRUPO_COTIZACION] * ( DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30 )");
					put("BASE_CGC_MIN_DIA","[\"01\":45.72, \"02\":37.92, \"03\":32.99, \"04\":32.74, \"05\":32.74, \"06\":32.74, \"07\":32.74, \"08\":32.74, \"09\":32.74, \"10\":32.74, \"11\":32.74][GRUPO_COTIZACION]  * JORNADAS_REALES");
					put("BASE_CGC_MIN","( COTIZACION_MENSUAL ) ? BASE_CGC_MIN_MES : BASE_CGC_MIN_DIA");

					put("BASE_CGC_MAX_MES","2595.60 * ( DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30 )");
					put("BASE_CGC_MAX_DIA","112.85 * JORNADAS_REALES");
					put("BASE_CGC_MAX","( COTIZACION_MENSUAL || JORNADAS_REALES >= 23 ) ? BASE_CGC_MAX_MES : BASE_CGC_MAX_DIA");

				}
		});
		addCCCData(aonContext,
				CCCType.AGRICULTURAL,
				firstDayOfYear, 
				null, 
				new HashMap<String,String>(){
				{
				}
		});
		//@formatter:on

		addSSRegimeCost(aonContext, AGRICULTURAL, firstDayOfYear, "CGC_E",
				COMMON_CONTINGENCY, "BASE_CGC_E * PORCENTAJE_CGC_E/100");

		Date firstDayOfMonth = getFirstDayOfMonth(getToday());

		//@formatter:off
		ContractRecord contract = newContract(
				aonContext, 
				SSRegimeType.GENERAL, 
				CCCType.AGRICULTURAL, 
				firstDayOfYear, 
				new HashMap<String,String>(){
					{
						put("GRUPO_COTIZACION","'09'");

						put("COTIZACION_MENSUAL","false");
//						put("JORNADAS_REALES","1");
					}
				}, 
				new String[] {
				}, 
				new String[] {
						
				}, 
				null /* without category*/
				);
		
		addPayment(aonContext, contract, addConcept(aonContext, "SALARIO_BASE"), "1.00", "1.00", PaymentType.CRA_0001);
		
		//@formatter:on

		Date lastDayOfMonth = getLastDayOfMonth(firstDayOfMonth);

		Set<String> undefined = new HashSet<String>();

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, firstDayOfMonth, lastDayOfMonth, lastDayOfMonth,
				contract);
		SalaryBuilder builder = new SalaryBuilder();
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>(
				builder);
		calculator.setListener(new ContractSalaryCalculator.Listener() {
			@Override
			public void onInvalidData(String variableName, String message) {
				undefined.add(variableName);
			}
		});
		calculator.calculate(ctx);

		Assert.assertEquals(true, undefined.contains("JORNADAS_REALES"));

		addData(aonContext, contract, firstDayOfYear, null,
				new HashMap<String, String>() {
					{
						put("JORNADAS_REALES", "1");
					}
				});

		ctx = getContractSalaryCalculatorContext(connection, firstDayOfMonth,
				lastDayOfMonth, lastDayOfMonth, contract);

		Assert.assertEquals(666,
				ctx.getExpressionContext()
						.eval("CENTINEL", firstDayOfMonth, lastDayOfMonth)
						.get(0).getValue());

		Assert.assertEquals(32.74 * 1.00,
				ctx.getExpressionContext()
						.eval("BASE_CGC_MIN", firstDayOfMonth, lastDayOfMonth)
						.get(0).getValue());

		Assert.assertEquals(32.74 * 1.00,
				ctx.getExpressionContext()
						.eval("BASE_CGP_MIN", firstDayOfMonth, lastDayOfMonth)
						.get(0).getValue());

		builder = new SalaryBuilder();
		ctx = getContractSalaryCalculatorContext(connection, firstDayOfMonth,
				lastDayOfMonth, lastDayOfMonth, contract);
		Salary salary = new ContractSalaryCalculator<Salary>(builder)
				.calculate(ctx);

		Assert.assertEquals("BASE_CGC_MIN", 32.74 * 1.00,
				salary.getCommonBase());
		Assert.assertEquals("BASE_CGP_MIN", 32.74 * 1.00,
				salary.getProfessionalBase());

		Assert.assertEquals("false",
				salary.getSalaryData("COTIZACION_MENSUAL"));
		Assert.assertEquals("32.74", salary.getSalaryData("BASE_CGC_MIN_DIA"));
		Assert.assertEquals("1", salary.getSalaryData("JORNADAS_REALES"));

	}

	// ------------------------------------------------------------------------

	@Test
	public void testSSRegimeArtistI()
			throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date firstDayOfYear = getFirstDayOfYear(getToday());

		cleanSystemData(aonContext);

		//@formatter:off
		addCCCData(aonContext,
				CCCType.ARTIST,
				firstDayOfYear, 
				null, 
				new HashMap<String,String>(){
				{
					put("COTIZACION_MENSUAL","true");
					put("BASE_CGP_MIN","BASE_CGC_MIN");
					put("BASE_CGP_MAX","BASE_CGC_MAX");
				}
		});
		
		addCCCData(aonContext,
				CCCType.ARTIST,
				firstDayOfYear, 
				null, 
				new HashMap<String,String>(){
				{
					
					// ARTIST_BASES
					put("BASE_CGC_MIN", "[\"01\":48.88, \"02\":40.53, \"03\":35.26, \"05\":35.00, \"07\":35.00][GRUPO_COTIZACION] * DIAS_NOMINA");
					
					put("BASE_CGC_MAX_DIA", "(($ in [ [461.00,270.00], [829.00,341.00], [1386.00,407.00], [Double.MAX_VALUE,542.00] ] if $[0] >= TOTAL_DEVENGADO/DIAS_NOMINA)[0][1]) * DIAS_NOMINA");
					put("BASE_CGC_MAX_MES", "MAX(4070.10 - SUM(\"BASE_CGC\"), 0)");
					put("BASE_CGC_MAX", "MIN(BASE_CGC_MAX_DIA , BASE_CGC_MAX_MES)");
					
				}
		});
		
		
		//@formatter:on
		
		Date firstDayOfMonth = getFirstDayOfMonth(getToday());

		//@formatter:off
		
		ContractRecord contract = newContract(
				aonContext, 
				SSRegimeType.GENERAL, 
				CCCType.ARTIST, 
				firstDayOfYear, 
				new HashMap<String,String>(){
					{
						put("GRUPO_COTIZACION","'01'");
					}
				}, 
				new String[] {
				}, 
				new String[] {
						
				}, 
				null /* without category*/
				);
		
		addPayment(aonContext, contract, addConcept(aonContext, "SALARIO_BASE"), "1.00", "1.00", PaymentType.CRA_0001);
		
		//@formatter:on

		Date lastDayOfMonth = getLastDayOfMonth(firstDayOfMonth);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, 
				firstDayOfMonth, 
				lastDayOfMonth, 
				lastDayOfMonth,
				contract);
		
		SalaryBuilder builder = new SalaryBuilder();
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator(builder);
		
		Salary salary = calculator.calculate(ctx);
		
		Assert.assertEquals(48.88 * AonDateUtils.get(lastDayOfMonth,Calendar.DAY_OF_MONTH), salary.getCommonBase());
	}
	
	@Test
	public void testSSRegimeArtistII()
			throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date firstDayOfYear = getFirstDayOfYear(getToday());

		cleanSystemData(aonContext);
		cleanSalaries(aonContext);

		//@formatter:off
		addCCCData(aonContext,
				CCCType.ARTIST,
				firstDayOfYear, 
				null, 
				new HashMap<String,String>(){
				{
					put("COTIZACION_MENSUAL","true");
					put("BASE_CGP_MIN","BASE_CGC_MIN");
					put("BASE_CGP_MAX","BASE_CGC_MAX");
				}
		});
		
		addCCCData(aonContext,
				CCCType.ARTIST,
				firstDayOfYear, 
				null, 
				new HashMap<String,String>(){
				{
					
					// ARTIST_BASES
					put("BASE_CGC_MIN", "[\"01\":48.88, \"02\":40.53, \"03\":35.26, \"05\":35.00, \"07\":35.00][GRUPO_COTIZACION] * DIAS_NOMINA");
					
					put("BASE_CGC_MAX_DIA", "(($ in [ [461.00,270.00], [829.00,341.00], [1386.00,407.00], [Double.MAX_VALUE,542.00] ] if $[0] >= BASE_CGC_BRUTA/DIAS_NOMINA)[0][1]) * DIAS_NOMINA");
					put("BASE_CGC_MAX_MES", "MAX(4070.10 - SUM(\"BASE_CGC\"), 0)");
					put("BASE_CGC_MAX", "MIN(BASE_CGC_MAX_DIA , BASE_CGC_MAX_MES)");
					
				}
		});
		
		
		//@formatter:on
		
		Date firstDayOfMonth = getFirstDayOfMonth(getToday());

		//@formatter:off
		
		ContractRecord contract = newContract(
				aonContext, 
				SSRegimeType.GENERAL, 
				CCCType.ARTIST, 
				firstDayOfYear, 
				new HashMap<String,String>(){
					{
						put("GRUPO_COTIZACION","'03'");
					}
				}, 
				new String[] {
				}, 
				new String[] {
						
				}, 
				null /* without category*/
				);
		
		addPayment(aonContext, contract, addConcept(aonContext, "SALARIO_BASE"), "5000.00 * DIAS_TRABAJADOS / DIAS_MES", "_P", PaymentType.CRA_0001);
		
		//@formatter:on

		Date lastDayOfMonth = getLastDayOfMonth(firstDayOfMonth);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, 
				firstDayOfMonth, 
				lastDayOfMonth, 
				lastDayOfMonth,
				contract);
		
		SalaryBuilder builder = new SalaryBuilder();
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator(builder);
		
		Salary salary = calculator.calculate(ctx);
		
		Assert.assertEquals(4070.10, salary.getCommonBase(), DELTA);
		
	}
	
	@Test
	public void testSSRegimeArtistIII()
			throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date firstDayOfYear = getFirstDayOfYear(getToday());

		cleanSystemData(aonContext);

		//@formatter:off
		addCCCData(aonContext,
				CCCType.ARTIST,
				firstDayOfYear, 
				null, 
				new HashMap<String,String>(){
				{
					put("COTIZACION_MENSUAL","true");
					put("BASE_CGP_MIN","BASE_CGC_MIN");
					put("BASE_CGP_MAX","BASE_CGC_MAX");
				}
		});
		
		addCCCData(aonContext,
				CCCType.ARTIST,
				firstDayOfYear, 
				null, 
				new HashMap<String,String>(){
				{
					
					// ARTIST_BASES
					put("BASE_CGC_MIN", "[\"01\":48.88, \"02\":40.53, \"03\":35.26, \"05\":35.00, \"07\":35.00][GRUPO_COTIZACION] * DIAS_NOMINA");
					
					put("BASE_CGC_MAX_DIA", "(($ in [ [461.00,270.00], [829.00,341.00], [1386.00,407.00], [Double.MAX_VALUE,542.00] ] if $[0] >= BASE_CGC_BRUTA/DIAS_NOMINA)[0][1]) * DIAS_NOMINA");
					put("BASE_CGC_MAX_MES", "MAX(4070.10 - SUM(\"BASE_CGC\"), 0)");
					put("BASE_CGC_MAX", "MIN(BASE_CGC_MAX_DIA , BASE_CGC_MAX_MES)");
					
				}
		});
		
		
		//@formatter:on
		
		Date firstDayOfMonth = getFirstDayOfMonth(getToday());
		Date endDate = AonDateUtils.add(firstDayOfMonth, Calendar.DAY_OF_MONTH, 9);

		//@formatter:off
		
		ContractRecord contract = newContract(
				aonContext, 
				SSRegimeType.GENERAL, 
				CCCType.ARTIST, 
				firstDayOfYear, 
				new HashMap<String,String>(){
					{
						put("GRUPO_COTIZACION","'03'");
					}
				}, 
				new String[] {
				}, 
				new String[] {
						
				}, 
				null /* without category*/
				);
		
		addPayment(aonContext, contract, addConcept(aonContext, "SALARIO_BASE"), "3500.00", "_P", PaymentType.CRA_0001);
		
		//@formatter:on

		//Date lastDayOfMonth = getLastDayOfMonth(firstDayOfMonth);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, 
				firstDayOfMonth, 
				endDate, 
				endDate,
				contract);
		
		SalaryBuilder builder = new SalaryBuilder();
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator(builder);
		
		Salary salary = calculator.calculate(ctx);
		
		salary.getSalaryDatas().forEach(s -> System.out.println(s.getName() + " = " + s.getExpression()));
		
		Assert.assertEquals(270.00 * AonDateUtils.get(endDate,Calendar.DAY_OF_MONTH), salary.getCommonBase(), DELTA);
		
	}
	
	@Test
	public void testSSRegimeArtistIV()
			throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSalaries(aonContext);

		Date firstDayOfYear = getFirstDayOfYear(getToday());

		cleanSystemData(aonContext);

		//@formatter:off
		addCCCData(aonContext,
				CCCType.ARTIST,
				firstDayOfYear, 
				null, 
				new HashMap<String,String>(){
				{
					put("COTIZACION_MENSUAL","true");
					put("BASE_CGP_MIN","BASE_CGC_MIN");
					put("BASE_CGP_MAX","BASE_CGC_MAX");
				}
		});
		
		addCCCData(aonContext,
				CCCType.ARTIST,
				firstDayOfYear, 
				null, 
				new HashMap<String,String>(){
				{
					
					// ARTIST_BASES
					put("BASE_CGC_MIN", "[\"01\":48.88, \"02\":40.53, \"03\":35.26, \"05\":35.00, \"07\":35.00][GRUPO_COTIZACION] * DIAS_NOMINA");
					
					put("BASE_CGC_MAX_DIA", "(($ in [ [461.00,270.00], [829.00,341.00], [1386.00,407.00], [Double.MAX_VALUE,542.00] ] if $[0] >= BASE_CGC_BRUTA/DIAS_NOMINA)[0][1]) * DIAS_NOMINA");
					put("BASE_CGC_MAX_MES", "MAX(4070.10 - SUM(\"BASE_CGC\"), 0)");
					put("BASE_CGC_MAX", "MIN(BASE_CGC_MAX_DIA , BASE_CGC_MAX_MES)");
					
				}
		});
		
		
		//@formatter:on
		
		Date firstDayOfMonth = getFirstDayOfMonth(getToday());
		Date endDate = AonDateUtils.add(firstDayOfMonth, Calendar.DAY_OF_MONTH, 14);

		//@formatter:off
		
		ContractRecord contract = newContract(
				aonContext, 
				SSRegimeType.GENERAL, 
				CCCType.ARTIST, 
				firstDayOfYear,
				endDate,
				new HashMap<String,String>(){
					{
						put("GRUPO_COTIZACION","'05'");
					}
				}, 
				new String[] {
				}, 
				new String[] {
						
				}, 
				null /* without category*/
				);
		
		addPayment(aonContext, contract, addConcept(aonContext, "SALARIO_BASE"), "15000.00", "_P", PaymentType.CRA_0001);
		
		//@formatter:on

		//Date lastDayOfMonth = getLastDayOfMonth(firstDayOfMonth);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, 
				firstDayOfMonth, 
				endDate, 
				endDate,
				contract);
		
		SalaryBuilder builder = new SalaryBuilder();
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator(builder);
		
		Salary salary = calculator.calculate(ctx);
		
		ctx.getExpressionContext().eval("SUM(\"BASE_CGC\")", firstDayOfMonth, endDate).forEach(t -> System.out.println(t.getValue()));;
		
//		salary.getSalaryDatas().forEach(s -> System.out.println(s.getName() + " = " + s.getExpression()));
		
		Assert.assertEquals(4070.10, salary.getCommonBase(), DELTA);
		
	}
	
	@Test
	public void testSSRegimeArtistV()
			throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSalaries(aonContext);

		Date firstDayOfYear = getFirstDayOfYear(getToday());

		cleanSystemData(aonContext);

		//@formatter:off
		addCCCData(aonContext,
				CCCType.ARTIST,
				firstDayOfYear, 
				null, 
				new HashMap<String,String>(){
				{
					put("COTIZACION_MENSUAL","true");
					put("BASE_CGP_MIN","BASE_CGC_MIN");
					put("BASE_CGP_MAX","BASE_CGC_MAX");
				}
		});
		
		addCCCData(aonContext,
				CCCType.ARTIST,
				firstDayOfYear, 
				null, 
				new HashMap<String,String>(){
				{
					
					// ARTIST_BASES
					put("BASE_CGC_MIN", "[\"01\":48.88, \"02\":40.53, \"03\":35.26, \"05\":35.00, \"07\":35.00][GRUPO_COTIZACION] * DIAS_NOMINA");
					
					put("BASE_CGC_MAX_DIA", "(($ in [ [461.00,270.00], [829.00,341.00], [1386.00,407.00], [Double.MAX_VALUE,542.00] ] if $[0] >= BASE_CGC_BRUTA/DIAS_NOMINA)[0][1]) * DIAS_NOMINA");
					put("BASE_CGC_MAX_MES", "MAX(4070.10 - SUM(\"BASE_CGC\"), 0)");
					put("BASE_CGC_MAX", "MIN(BASE_CGC_MAX_DIA , BASE_CGC_MAX_MES)");
					
				}
		});
		
		
		//@formatter:on
		
		Date firstDayOfMonth = getFirstDayOfMonth(getToday());
		Date lastDayOfMonth = getLastDayOfMonth(getToday());
		Date endDate = AonDateUtils.add(firstDayOfMonth, Calendar.DAY_OF_MONTH, 14);

		//@formatter:off
		
		// --------------------------------------- FIRST CONTRACT
		ContractRecord contract = newContract(
				aonContext, 
				SSRegimeType.GENERAL, 
				CCCType.ARTIST, 
				firstDayOfYear,
				endDate,
				new HashMap<String,String>(){
					{
						put("GRUPO_COTIZACION","'05'");
					}
				}, 
				new String[] {
				}, 
				new String[] {
						
				}, 
				null /* without category*/
				);
		
		addPayment(aonContext, contract, addConcept(aonContext, "SALARIO_BASE"), "3500.00", "_P", PaymentType.CRA_0001);
		
		//@formatter:on

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, 
				firstDayOfMonth, 
				endDate, 
				endDate,
				contract);
		
		calculateAndSave(connection, ctx);
		
		// --------------------------------------- SECOND CONTRACT
		Date startDate = add(endDate, DAY_OF_MONTH, 5);
		
		contract = newContract(
				aonContext, 
				SSRegimeType.GENERAL, 
				CCCType.ARTIST, 
				startDate,
				lastDayOfMonth,
				new HashMap<String,String>(){
					{
						put("GRUPO_COTIZACION","'05'");
					}
				}, 
				new String[] {
				}, 
				new String[] {
						
				}, 
				null /* without category*/
				);
		
		addPayment(aonContext, contract, addConcept(aonContext, "SALARIO_BASE"), "3500.00", "_P", PaymentType.CRA_0001);
		
		//@formatter:on

		ctx = getContractSalaryCalculatorContext(
				connection, 
				startDate, 
				lastDayOfMonth, 
				lastDayOfMonth,
				contract);
		
		SalaryBuilder builder = new SalaryBuilder();
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator(builder);
		
		Salary salary = calculator.calculate(ctx);
		
		Assert.assertEquals(4070.10 - 3500, salary.getCommonBase(), DELTA);
		
	}
	
	@Test
	public void testSSRegimeArtistVI()
			throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSalaries(aonContext);

		Date firstDayOfYear = getFirstDayOfYear(getToday());

		cleanSystemData(aonContext);

		//@formatter:off
		addCCCData(aonContext,
				CCCType.ARTIST,
				firstDayOfYear, 
				null, 
				new HashMap<String,String>(){
				{
					put("COTIZACION_MENSUAL","true");
					put("BASE_CGP_MIN","BASE_CGC_MIN");
					put("BASE_CGP_MAX","BASE_CGC_MAX");
				}
		});
		
		addCCCData(aonContext,
				CCCType.ARTIST,
				firstDayOfYear, 
				null, 
				new HashMap<String,String>(){
				{
					
					// ARTIST_BASES
					put("BASE_CGC_MIN", "[\"01\":48.88, \"02\":40.53, \"03\":35.26, \"05\":35.00, \"07\":35.00][GRUPO_COTIZACION] * DIAS_NOMINA");
					
					put("BASE_CGC_MAX_DIA", "(($ in [ [461.00,270.00], [829.00,341.00], [1386.00,407.00], [Double.MAX_VALUE,542.00] ] if $[0] >= BASE_CGC_BRUTA/DIAS_NOMINA)[0][1]) * DIAS_NOMINA");
					put("BASE_CGC_MAX_MES", "MAX(4070.10 - SUM(\"BASE_CGC\"), 0)");
					put("BASE_CGC_MAX", "MIN(BASE_CGC_MAX_DIA , BASE_CGC_MAX_MES)");
					
				}
		});
		
		
		//@formatter:on
		
		// 01/12
		Date firstOfNovember = getFirstDayOfMonth(getToday());
		firstOfNovember = AonDateUtils.add(firstOfNovember, Calendar.MONTH, 10);
		
		// 15/12
		Date midOfNovember = getFirstDayOfMonth(getToday());
		midOfNovember = AonDateUtils.add(midOfNovember, Calendar.MONTH, 10);
		midOfNovember = AonDateUtils.add(midOfNovember, Calendar.DAY_OF_MONTH, 14);
		
		// 16/12
		Date stOfNovember = getFirstDayOfMonth(getToday());
		stOfNovember = AonDateUtils.add(stOfNovember, Calendar.MONTH, 10);
		stOfNovember = AonDateUtils.add(stOfNovember, Calendar.DAY_OF_MONTH, 15);
		
		// 25/12
		Date tfOfNovember = getFirstDayOfMonth(getToday());
		tfOfNovember = AonDateUtils.add(tfOfNovember, Calendar.MONTH, 10);
		tfOfNovember = AonDateUtils.add(tfOfNovember, Calendar.DAY_OF_MONTH, 24);
		
		// 26/12
		Date tsOfNovember = getFirstDayOfMonth(getToday());
		tsOfNovember = AonDateUtils.add(tsOfNovember, Calendar.MONTH, 10);
		tsOfNovember = AonDateUtils.add(tsOfNovember, Calendar.DAY_OF_MONTH, 25);
		
		// 30/12
		Date lastOfNovember = getFirstDayOfMonth(getToday());
		lastOfNovember = AonDateUtils.add(lastOfNovember, Calendar.MONTH, 10);
		lastOfNovember = AonDateUtils.getLastDayOfMonth(lastOfNovember);
		
		System.out.println(firstOfNovember);
		System.out.println(midOfNovember);
		System.out.println(stOfNovember);
		System.out.println(tfOfNovember);
		System.out.println(tsOfNovember);
		System.out.println(lastOfNovember);
		System.out.println();
		
		
		//@formatter:off
		
		// --------------------------------------- FIRST CONTRACT
		ContractRecord contract = newContract(
				aonContext, 
				SSRegimeType.GENERAL, 
				CCCType.ARTIST, 
				firstOfNovember,
				midOfNovember,
				new HashMap<String,String>(){
					{
						put("GRUPO_COTIZACION","'03'");
					}
				}, 
				new String[] {
				}, 
				new String[] {
						
				}, 
				null /* without category*/
				);
		
		final Integer contractId = contract.getId();
		
		addPayment(aonContext, contract, addConcept(aonContext, "SALARIO_BASE"), "1000.00", "_P", PaymentType.CRA_0001);
		addPayment(aonContext, contract, addConcept(aonContext, "PLUS_SALARIAL"), "2950.00", "_P", PaymentType.CRA_0001);
		
		//@formatter:on

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, 
				firstOfNovember, 
				midOfNovember, 
				midOfNovember,
				contract);
		
		calculateAndSave(connection, ctx);
		
		AON.getSalaries(aonContext, p -> p.getContractProperty().eq(contractId))
			.forEach(s -> Assert.assertEquals(3950.00, s.getCommonContingenciesBase(), DELTA));
		
		// --------------------------------------- SECOND CONTRACT
		ContractRecord contract2 = newContract(
				aonContext, 
				SSRegimeType.GENERAL, 
				CCCType.ARTIST, 
				stOfNovember,
				tfOfNovember,
				new HashMap<String,String>(){
					{
						put("GRUPO_COTIZACION","'03'");
					}
				}, 
				new String[] {
				}, 
				new String[] {
						
				}, 
				null /* without category*/
				);
		
		final Integer contract2Id = contract2.getId();
		final Date filterDate = stOfNovember;
		
		addPayment(aonContext, contract2, addConcept(aonContext, "SALARIO_BASE"), "4000.00", "_P", PaymentType.CRA_0001);
		
		//@formatter:on

		ISQLContractSalaryCalculatorContext ctx2 = getContractSalaryCalculatorContext(
				connection, 
				stOfNovember, 
				tfOfNovember, 
				tfOfNovember,
				contract2);
		
		calculateAndSave(connection, ctx2);
		
		AON.getSalaries(aonContext, p -> p.getContractProperty().eq(contract2Id).and(p.getStartDateProperty().eq(filterDate)))
			.forEach(s -> Assert.assertEquals(4070.10 - 3950.00, s.getCommonContingenciesBase(), DELTA));
	
		
		// --------------------------------------- THIRD CONTRACT
		ContractRecord contract3 = newContract(
				aonContext, 
				SSRegimeType.GENERAL, 
				CCCType.ARTIST, 
				tsOfNovember,
				lastOfNovember,
				new HashMap<String,String>(){
					{
						put("GRUPO_COTIZACION","'03'");
					}
				}, 
				new String[] {
				}, 
				new String[] {
						
				}, 
				null /* without category*/
				);
		
		addPayment(aonContext, contract3, addConcept(aonContext, "SALARIO_BASE"), "933.33", "_P", PaymentType.CRA_0001);
		
		//@formatter:on

		ctx = getContractSalaryCalculatorContext(
				connection, 
				tsOfNovember, 
				lastOfNovember, 
				lastOfNovember,
				contract3);
		
		SalaryBuilder builder = new SalaryBuilder();
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator(builder);
		
		Salary salary = calculator.calculate(ctx);
		
		Assert.assertEquals(0.00, salary.getCommonBase(), DELTA);
		
	}
	
	// -----------------------------------------------------------------------------------------------------
	
	
	private static int calculateAndSave(Connection connection,
			ISQLContractSalaryCalculatorContext ctx) throws SalaryException {
		JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder(connection);
		new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		return jooqSalaryBuilder.execute();
	}
}
