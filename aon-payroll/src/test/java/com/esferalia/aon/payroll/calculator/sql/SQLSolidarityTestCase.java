/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryData;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;

/**
 * @author rtrepiana
 *
 */
public class SQLSolidarityTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.049999;

	@Test
	public void tesMonthlyBasesI() throws ExpressionException, SQLException,
			SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);

		//@formatter:off
		ContractRecord contract = newContract(
			aonContext, 
			getFirstDayOfYear(getToday()), 
			Collections.singletonMap(ContextVariable.QUOTE_GROUP.getName(), "\"05\""),
			new String[] { 
					"7500.00 * DIAS_TRABAJADOS / DIAS_MES",
			}, 
			new String[] {
			},
			null
		);
		//@formatter:on

		addSolidarityBases(aonContext);

		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		ISQLContractSalaryCalculatorContext ctx = 
		getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		salary.getSalaryDeductions().forEach( d -> System.out.println(d.getExpression() + " : " + d.getAmount() ));
		
		List<SalaryData> base497 = salary.getSalaryDatas().stream()
		.filter( data ->  Objects.equals("BASE_SOLIDARIDAD_I", data.getName()))
		.toList();
		
		assertEquals(1, base497.size());
		assertEquals(472.05, Double.parseDouble(base497.get(0).getExpression()), DELTA);
		
		List<SalaryData> base498 = salary.getSalaryDatas().stream()
		.filter( data ->  Objects.equals("BASE_SOLIDARIDAD_II", data.getName()))
		.toList();
		
		assertEquals(1, base498.size());
		assertEquals(1888.2, Double.parseDouble(base498.get(0).getExpression()), DELTA);
		
		List<SalaryData> base499 = salary.getSalaryDatas().stream()
		.filter( data ->  Objects.equals("BASE_SOLIDARIDAD_III", data.getName()))
		.toList();
		
		assertEquals(1, base499.size());
		assertEquals(419.25 , Double.parseDouble(base499.get(0).getExpression()), DELTA);

	}

	@Test
	public void tesMonthlyBasesITI() throws ExpressionException, SQLException,
			SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);

		//@formatter:off
		ContractRecord contract = newContract(
			aonContext, 
			getFirstDayOfYear(getToday()), 
			Collections.singletonMap(ContextVariable.QUOTE_GROUP.getName(), "\"05\""),
			new String[] { 
					"8500.00 * DIAS_TRABAJADOS / DIAS_MES",
			}, 
			new String[] {
			},
			null
		);
		//@formatter:on
		
		addSolidarityBases(aonContext);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		Date startItDate = add(startDate, Calendar.DAY_OF_MONTH, 10);
		Date enfFirstPeriod = add(startItDate, Calendar.DAY_OF_MONTH, -1);
		
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startItDate , startItDate, null);

		ISQLContractSalaryCalculatorContext ctx = 
		getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
//		salary.getSalaryDeductions().forEach( d -> System.out.println(d.getExpression() + " : " + d.getAmount() ));
		salary.getSalaryDatas().forEach( d -> System.out.println(d.getName() + " : " + d.getExpression() ));
		
		List<SalaryData> base497 = salary.getSalaryDatas().stream()
		.filter( data ->  Objects.equals("BASE_SOLIDARIDAD_I", data.getName()))
		.peek( data -> assertEquals(startDate, data.getStartDate()) )
		.toList();
		
		assertEquals(1, base497.size());
		
		double monthDays = AonDateUtils.get(endDate, Calendar.DAY_OF_MONTH); 

		assertEquals(4720.5 / 30.00 * monthDays * 0.10 , Double.parseDouble(base497.get(0).getExpression()), DELTA);
		
		List<SalaryData> base498 = salary.getSalaryDatas().stream()
		.filter( data ->  Objects.equals("BASE_SOLIDARIDAD_II", data.getName()))
		.peek( data -> assertEquals(startDate, data.getStartDate()) )
		.toList();
		
		assertEquals(1, base498.size());
		assertEquals(4720.5 / 30.00 * monthDays * 0.40  , Double.parseDouble(base498.get(0).getExpression()), DELTA);
		
		List<SalaryData> base499 = salary.getSalaryDatas().stream()
		.filter( data ->  Objects.equals("BASE_SOLIDARIDAD_III", data.getName()))
		.peek( data -> assertEquals(startDate, data.getStartDate()) )
		.toList();
		
//		salary.getSalaryDatas().stream().sorted((d1, d2) -> d1.getName().compareTo(d2.getName()))
//				.forEach(d -> System.out.println(d.getName() + "=" + d.getExpression()));
		
		double dailyBase = 8500.00 / monthDays ;
		assertEquals(1, base499.size());
		assertEquals(
				(dailyBase * ( monthDays -1 /*it day*/ )  )
				- ( 4720.5 / 30.00 * monthDays ) 
				- Double.parseDouble(base497.get(0).getExpression()) 
				- Double.parseDouble(base498.get(0).getExpression())
				, 
				Double.parseDouble(base499.get(0).getExpression()), DELTA);

	}

	@Test
	public void tesMonthlyBasesII() throws ExpressionException, SQLException,
			SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);

		//@formatter:off
		ContractRecord contract = newContract(
			aonContext, 
			getFirstDayOfYear(getToday()), 
			Collections.singletonMap(ContextVariable.QUOTE_GROUP.getName(), "\"05\""),
			new String[] { 
					"(4720.5 + 472.05 + 1888.2) * DIAS_TRABAJADOS / DIAS_MES",
			}, 
			new String[] {
			},
			null
		);
		//@formatter:on

		addSolidarityBases(aonContext);

		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		ISQLContractSalaryCalculatorContext ctx = 
		getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		salary.getSalaryDeductions().forEach( d -> System.out.println(d.getExpression() + " : " + d.getAmount() ));
		
		List<SalaryData> base497 = salary.getSalaryDatas().stream()
		.filter( data ->  Objects.equals("BASE_SOLIDARIDAD_I", data.getName()))
		.toList();
		
		assertEquals(1, base497.size());
		assertEquals(472.05, Double.parseDouble(base497.get(0).getExpression()), DELTA);
		
		List<SalaryData> base498 = salary.getSalaryDatas().stream()
		.filter( data ->  Objects.equals("BASE_SOLIDARIDAD_II", data.getName()))
		.toList();
		
		assertEquals(1, base498.size());
		assertEquals(1888.2, Double.parseDouble(base498.get(0).getExpression()), DELTA);
		
		List<SalaryData> base499 = salary.getSalaryDatas().stream()
		.filter( data ->  Objects.equals("BASE_SOLIDARIDAD_III", data.getName()))
		.toList();
		
		assertEquals(0, base499.size());

	}

	@Test
	public void tesMonthlyBasesIII() throws ExpressionException, SQLException,
			SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);

		//@formatter:off
		ContractRecord contract = newContract(
			aonContext, 
			getFirstDayOfYear(getToday()), 
			Collections.singletonMap(ContextVariable.QUOTE_GROUP.getName(), "\"05\""),
			new String[] { 
					"(4720.5 + 472.05 + 666.0 ) * DIAS_TRABAJADOS / DIAS_MES",
			}, 
			new String[] {
			},
			null
		);
		//@formatter:on

		addSolidarityBases(aonContext);

		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		ISQLContractSalaryCalculatorContext ctx = 
		getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		salary.getSalaryDeductions().forEach( d -> System.out.println(d.getExpression() + " : " + d.getAmount() ));
		
		List<SalaryData> base497 = salary.getSalaryDatas().stream()
		.filter( data ->  Objects.equals("BASE_SOLIDARIDAD_I", data.getName()))
		.toList();
		
		assertEquals(1, base497.size());
		assertEquals(472.05, Double.parseDouble(base497.get(0).getExpression()), DELTA);
		
		List<SalaryData> base498 = salary.getSalaryDatas().stream()
		.filter( data ->  Objects.equals("BASE_SOLIDARIDAD_II", data.getName()))
		.toList();
		
		assertEquals(1, base498.size());
		assertEquals(666.00, Double.parseDouble(base498.get(0).getExpression()), DELTA);
		
		List<SalaryData> base499 = salary.getSalaryDatas().stream()
		.filter( data ->  Objects.equals("BASE_SOLIDARIDAD_III", data.getName()))
		.toList();
		
		assertEquals(0, base499.size());

	}

	@Test
	public void tesMonthlyBasesIV() throws ExpressionException, SQLException,
			SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);

		//@formatter:off
		ContractRecord contract = newContract(
			aonContext, 
			getFirstDayOfYear(getToday()), 
			Collections.singletonMap(ContextVariable.QUOTE_GROUP.getName(), "\"05\""),
			new String[] { 
					"(4720.5 + 472.05) * DIAS_TRABAJADOS / DIAS_MES",
			}, 
			new String[] {
			},
			null
		);
		//@formatter:on

		addSolidarityBases(aonContext);

		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		ISQLContractSalaryCalculatorContext ctx = 
		getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		salary.getSalaryDeductions().forEach( d -> System.out.println(d.getExpression() + " : " + d.getAmount() ));
		
		List<SalaryData> base497 = salary.getSalaryDatas().stream()
		.filter( data ->  Objects.equals("BASE_SOLIDARIDAD_I", data.getName()))
		.toList();
		
		assertEquals(1, base497.size());
		assertEquals(472.05, Double.parseDouble(base497.get(0).getExpression()), DELTA);
		
		List<SalaryData> base498 = salary.getSalaryDatas().stream()
		.filter( data ->  Objects.equals("BASE_SOLIDARIDAD_II", data.getName()))
		.toList();
		
		assertEquals(0, base498.size());
		
		List<SalaryData> base499 = salary.getSalaryDatas().stream()
		.filter( data ->  Objects.equals("BASE_SOLIDARIDAD_III", data.getName()))
		.toList();
		
		assertEquals(0, base499.size());

	}

	@Test
	public void tesMonthlyBasesV() throws ExpressionException, SQLException,
			SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);

		//@formatter:off
		ContractRecord contract = newContract(
			aonContext, 
			getFirstDayOfYear(getToday()), 
			Collections.singletonMap(ContextVariable.QUOTE_GROUP.getName(), "\"05\""),
			new String[] { 
					"(4720.5 + 372.05) * DIAS_TRABAJADOS / DIAS_MES",
			}, 
			new String[] {
			},
			null
		);
		//@formatter:on

		addSolidarityBases(aonContext);

		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		ISQLContractSalaryCalculatorContext ctx = 
		getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		salary.getSalaryDeductions().forEach( d -> System.out.println(d.getExpression() + " : " + d.getAmount() ));
		
		List<SalaryData> base497 = salary.getSalaryDatas().stream()
		.filter( data ->  Objects.equals("BASE_SOLIDARIDAD_I", data.getName()))
		.toList();
		
		assertEquals(1, base497.size());
		assertEquals(372.05, Double.parseDouble(base497.get(0).getExpression()), DELTA);
		
		List<SalaryData> base498 = salary.getSalaryDatas().stream()
		.filter( data ->  Objects.equals("BASE_SOLIDARIDAD_II", data.getName()))
		.toList();
		
		assertEquals(0, base498.size());
		
		List<SalaryData> base499 = salary.getSalaryDatas().stream()
		.filter( data ->  Objects.equals("BASE_SOLIDARIDAD_III", data.getName()))
		.toList();
		
		assertEquals(0, base499.size());

	}

	@Test
	public void tesMonthlyBasesVI() throws ExpressionException, SQLException,
			SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);

		//@formatter:off
		ContractRecord contract = newContract(
			aonContext, 
			getFirstDayOfYear(getToday()), 
			Collections.singletonMap(ContextVariable.QUOTE_GROUP.getName(), "\"05\""),
			new String[] { 
					"(4720.05) * DIAS_TRABAJADOS / DIAS_MES",
			}, 
			new String[] {
			},
			null
		);
		//@formatter:on

		addSolidarityBases(aonContext);

		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		ISQLContractSalaryCalculatorContext ctx = 
		getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		salary.getSalaryDeductions().forEach( d -> System.out.println(d.getExpression() + " : " + d.getAmount() ));
		
		List<SalaryData> base497 = salary.getSalaryDatas().stream()
		.filter( data ->  Objects.equals("BASE_SOLIDARIDAD_I", data.getName()))
		.toList();
		
		assertEquals(0, base497.size());
		
		List<SalaryData> base498 = salary.getSalaryDatas().stream()
		.filter( data ->  Objects.equals("BASE_SOLIDARIDAD_II", data.getName()))
		.toList();
		
		assertEquals(0, base498.size());
		
		List<SalaryData> base499 = salary.getSalaryDatas().stream()
		.filter( data ->  Objects.equals("BASE_SOLIDARIDAD_III", data.getName()))
		.toList();
		
		assertEquals(0, base499.size());

	}
	private static void addSolidarityBases(AONContext aonContext) {
		addSolidarityBases(aonContext, getFirstDayOfYear(getToday()));
	}

	protected static  void addSolidarityBases(AONContext aonContext, Date startDate) {
		addSSRegimeData(aonContext, SSRegimeType.GENERAL,
				startDate, 
				null,
				new HashMap<String, String>() {
					{
						put("BASE_CGC_MAX", 
						"[ \"01\":(4720.50 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)) "
						+ ",\"02\":(4720.50 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)) "
						+ ",\"03\":(4720.50 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)) "
						+ ",\"04\":(4720.50 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)) "
						+ ",\"05\":(4720.50 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)) "
						+ ",\"06\":(4720.50 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)) "
						+ ",\"07\":(4720.50 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)) "
						+ ",\"08\":(157.35 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)) "
						+ ",\"09\":(157.35 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)) "
						+ ",\"10\":(157.35 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)) "
						+ ",\"11\":(157.35 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA))] [GRUPO_COTIZACION]");
						
						put("BASE_SOLIDARIDAD_I",  
						"_SUM_BASE_CGC_MAX = SUM(BASE_CGC_MAX); "
						+ "_SUM_BASE_CGC_BRUTA = SUM(BASE_CGC_BRUTA); "
						+ "_SUM_BASE_CGC_MAX_10 = ROUND(_SUM_BASE_CGC_MAX * 0.10,2);"
						+ "_EXCESO=ROUND(_SUM_BASE_CGC_BRUTA - _SUM_BASE_CGC_MAX,2);"
						+ "_EXCESO > 0.00 ? MIN(_EXCESO, _SUM_BASE_CGC_MAX_10) : REMOVE()"
						);

						put("BASE_SOLIDARIDAD_II", 
						"_SUM_BASE_CGC_MAX = SUM(BASE_CGC_MAX); "
						+ "_SUM_BASE_CGC_BRUTA = SUM(BASE_CGC_BRUTA); "
						+ "_SUM_BASE_CGC_MAX_10 = _SUM_BASE_CGC_MAX * 0.10;"
						+ "_SUM_BASE_CGC_MAX_50 = _SUM_BASE_CGC_MAX * 0.50;"
						+ "_SUM_BASE_CGC_MAX_10_50 = ROUND( _SUM_BASE_CGC_MAX_50 - _SUM_BASE_CGC_MAX_10,2);"
						+ "_EXCESO=ROUND( _SUM_BASE_CGC_BRUTA - _SUM_BASE_CGC_MAX - _SUM_BASE_CGC_MAX_10,2); "
						+ "_EXCESO > 0.00 ? MIN( _EXCESO, _SUM_BASE_CGC_MAX_10_50 ): REMOVE() "
						);

						put("BASE_SOLIDARIDAD_III", 
						"_SUM_BASE_CGC_MAX = SUM(BASE_CGC_MAX); "
						+ "_SUM_BASE_CGC_BRUTA = SUM(BASE_CGC_BRUTA); "
						+ "_SUM_BASE_CGC_MAX_50 = _SUM_BASE_CGC_MAX * 0.50 ;"
						+ "_EXCESO=ROUND(_SUM_BASE_CGC_BRUTA - _SUM_BASE_CGC_MAX - _SUM_BASE_CGC_MAX_50, 2);"
						+ "_EXCESO > 0.00 ? _EXCESO : REMOVE() "
						);

						put("PORCENTAJE_SOLIDARIDAD_I", "ROUND(0.92 * 4.70 / 28.30,2)"); 
						put("PORCENTAJE_SOLIDARIDAD_II", "ROUND(1.00 * 4.70 / 28.30,2)"); 
						put("PORCENTAJE_SOLIDARIDAD_III", "ROUND(1.17 * 4.70 / 28.30,2)"); 
						
						put("PORCENTAJE_SOLIDARIDAD_I_E", "0.92 - PORCENTAJE_SOLIDARIDAD_I"); 
						put("PORCENTAJE_SOLIDARIDAD_II_E", "1.00 - PORCENTAJE_SOLIDARIDAD_II"); 
						put("PORCENTAJE_SOLIDARIDAD_III_E", "1.17 -PORCENTAJE_SOLIDARIDAD_III"); 
					}
				});
		
		addSSRegimeDeduction(aonContext, SSRegimeType.GENERAL, startDate, DeductionType.SOLIDARITY, "SOLIDARIDAD_I",
				"Solidaridad. Exceso hasta el 10% base máxima",
				"BASE_SOLIDARIDAD_I * PORCENTAJE_SOLIDARIDAD_I / 100.00");
		addSSRegimeDeduction(aonContext, SSRegimeType.GENERAL, startDate, DeductionType.SOLIDARITY, "SOLIDARIDAD_II",
				"Solidaridad. Exceso desde el 10% hasta el 50% base máxima",
				"BASE_SOLIDARIDAD_II * PORCENTAJE_SOLIDARIDAD_II / 100.00");
		addSSRegimeDeduction(aonContext, SSRegimeType.GENERAL, startDate, DeductionType.SOLIDARITY, "SOLIDARIDAD_III",
				"Solidaridad. Exceso superior al 50% de la base máxima",
				"BASE_SOLIDARIDAD_III * PORCENTAJE_SOLIDARIDAD_III / 100.00");

		addSSRegimeCost(aonContext, SSRegimeType.GENERAL, startDate, DeductionType.SOLIDARITY, "SOLIDARIDAD_I_E",
				"Solidaridad. Exceso hasta el 10% base máxima",
				"BASE_SOLIDARIDAD_I * PORCENTAJE_SOLIDARIDAD_I_E / 100.00");
		addSSRegimeCost(aonContext, SSRegimeType.GENERAL, startDate, DeductionType.SOLIDARITY, "SOLIDARIDAD_II_E",
				"Solidaridad. Exceso desde el 10% hasta el 50% base máxima",
				"BASE_SOLIDARIDAD_II * PORCENTAJE_SOLIDARIDAD_II_E / 100.00");
		addSSRegimeCost(aonContext, SSRegimeType.GENERAL, startDate, DeductionType.SOLIDARITY, "SOLIDARIDAD_III_E",
				"Solidaridad. Exceso superior al 50% de la base máxima",
				"BASE_SOLIDARIDAD_III * PORCENTAJE_SOLIDARIDAD_III_E / 100.00");
	}

	
}
