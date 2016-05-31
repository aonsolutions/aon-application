package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.SSRegimeType.AGRICULTURAL;
import static com.esferalia.aon.salary.enumeration.DeductionType.COMMON_CONTINGENCY;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;
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

}
