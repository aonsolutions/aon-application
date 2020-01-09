/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.junit.Test;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.BonusConceptRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.BonusType;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.IExpressionVariable;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;

/**
 * @author rtrepiana
 *
 */
public class SQLContractSalaryCalculatorTestCase extends AbstractSQLTestCase {

	@Test
	public void testListener()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				new String[] { "NETO(2500.00) ", "( P_2 + P_3 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"BRUTO(4000.00) * DIAS_TRABAJADOS / DIAS_MES",
						"BRUTO(80.00) * DIAS_TRABAJADOS / DIAS_MES" },
				new String[] { "BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
						"BASE_IRPF * PORCENTAJE_IRPF/100" });

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);

		ctx.next();

		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		calculator.setListener(new ContractSalaryCalculator.Listener() {
			@Override
			public void onCheckError(IContractPayment payment, String message) {
				System.out.println(message);
			}
		});

		ISalary salary = calculator.calculate(ctx);

		Assert.assertEquals(1500.00, salary.getTotalPayment());

	}

	@Test
	public void testSalaryData()
			throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();

		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				new String[] { "1500.00 * DIAS_TRABAJADOS / DIAS_MES", },
				new String[] {});

		cleanSystemData(aonContext);
		addSSRegimeData(aonContext,
				SSRegimeType.values()[contract.getSsRegime()],
				getFirstDayOfYear(getToday()), null,
				new HashMap<String, String>() {
					{
						put("COTIZACION_MENSUAL", "true");
						put("REDUCCION_CGC_E_01",
								"TRACE('REDUCCION_CGC_E_01 = %f\r\n', 8.10);8.10");
						put("REDUCCION_CGC_E_02",
								"COTIZACION_MENSUAL ? ((BASE_CGC <= 986.70) ? 6.50 : ((BASE_CGC <= 2595.60) ? (6.50 * ( 1 + (BASE_CGC - 986.70)/ BASE_CGC * 2.52 * 6.15 / 6.50)) : 0.00)) : ((BASE_CGC / JORNADAS_REALES <= 42.90) ? 6.50 : ((BASE_CGC / JORNADAS_REALES <= 112.85 )? (6.50 * ( 1 + (BASE_CGC / JORNADAS_REALES - 42.90) / ( BASE_CGC / JORNADAS_REALES ) * 2.52 * 6.15 / 6.50)) : 0.00))");
						put("PORCENTAJE_CGC_E",
								"(GRUPO_COTIZACION == \"01\") ? (23.60 - REDUCCION_CGC_E_01) : (17.30 -REDUCCION_CGC_E_02)");
					}
				});

		cleanSystemCosts(aonContext);
		addSSRegimeCost(aonContext,
				SSRegimeType.values()[contract.getSsRegime()],
				getFirstDayOfYear(getToday()), "CGC_E",
				DeductionType.COMMON_CONTINGENCY,
				"_CUOTA=(( BASE_CGC_E=( BASE_CGC + ( isdef BASE_MTNAD ? BASE_MTNAD : 0.00 ) ) ) * PORCENTAJE_CGC_E/100); (GRUPO_COTIZACION == \"01\") ? (COTIZACION_MENSUAL ? MIN(_CUOTA,279.00): MIN(_CUOTA, 12.13 * JORNADAS_REALES)) : (COTIZACION_MENSUAL ? MAX(_CUOTA,60.25): MAX(_CUOTA,2.62 * JORNADAS_REALES))");

		addData(aonContext, contract, getFirstDayOfYear(getToday()), null,
				new HashMap<String, String>() {
					{
						put("GRUPO_COTIZACION", "\"01\"");
					}
				});

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);

		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder() {
			@Override
			public void addCost(Double amount, String description,
					IDeduction cost, Map<String, ITimedVariable<?>> context) {
				super.addCost(amount, description, cost, context);

				Map<String, Object> data = new HashMap<String, Object>();
				load(context, data);

				Assert.assertEquals("01", data.get("GRUPO_COTIZACION"));

				Assert.assertEquals(true, data.get("COTIZACION_MENSUAL"));

				Assert.assertEquals(8.10, data.get("REDUCCION_CGC_E_01"));

			}
		});

		Salary salary = calculator.calculate(ctx);

	}

	@Test
	public void testSalaryDataII()
			throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();

		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				new String[] { "1500.00 * DIAS_TRABAJADOS / DIAS_MES", },
				new String[] {});

		cleanSystemData(aonContext);
		addSSRegimeData(aonContext,
				SSRegimeType.values()[contract.getSsRegime()],
				getFirstDayOfYear(getToday()), null,
				new HashMap<String, String>() {
					{
						put("COTIZACION_MENSUAL", "true");
						put("REDUCCION_CGC_E_01",
								"TRACE('REDUCCION_CGC_E_01 = %f\r\n', 8.10);8.10");
						put("REDUCCION_CGC_E_02",
								"COTIZACION_MENSUAL ? ((BASE_CGC <= 986.70) ? 6.50 : ((BASE_CGC <= 2595.60) ? (6.50 * ( 1 + (BASE_CGC - 986.70)/ BASE_CGC * 2.52 * 6.15 / 6.50)) : 0.00)) : ((BASE_CGC / JORNADAS_REALES <= 42.90) ? 6.50 : ((BASE_CGC / JORNADAS_REALES <= 112.85 )? (6.50 * ( 1 + (BASE_CGC / JORNADAS_REALES - 42.90) / ( BASE_CGC / JORNADAS_REALES ) * 2.52 * 6.15 / 6.50)) : 0.00))");
						put("PORCENTAJE_CGC_E",
								"(GRUPO_COTIZACION == \"01\") ? (23.60 - REDUCCION_CGC_E_01) : (17.30 -REDUCCION_CGC_E_02)");
					}
				});

		cleanSystemCosts(aonContext);
		addSSRegimeCost(aonContext,
				SSRegimeType.values()[contract.getSsRegime()],
				getFirstDayOfYear(getToday()), "CGC_E",
				DeductionType.COMMON_CONTINGENCY,
				"_CUOTA=(( BASE_CGC_E=( BASE_CGC + ( isdef BASE_MTNAD ? BASE_MTNAD : 0.00 ) ) ) * PORCENTAJE_CGC_E/100); (GRUPO_COTIZACION == \"01\") ? (COTIZACION_MENSUAL ? MIN(_CUOTA,279.00): MIN(_CUOTA, 12.13 * JORNADAS_REALES)) : (COTIZACION_MENSUAL ? MAX(_CUOTA,60.25): MAX(_CUOTA,2.62 * JORNADAS_REALES))");

		addData(aonContext, contract, getFirstDayOfYear(getToday()), null,
				new HashMap<String, String>() {
					{
						put("GRUPO_COTIZACION", "\"01\"");
					}
				});

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);

		ContractSalaryCalculator<ISalary> calculator = new ContractSalaryCalculator<ISalary>();

		JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder(connection);
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();

		try {
			AON.getSalaries(aonContext,
					props -> props.getContractProperty().eq(contract.getId()))
					.forEach(salary -> {
						Assert.assertEquals(8.10,
								Double.parseDouble(salary.getContextData()
										.get("REDUCCION_CGC_E_01").get(0)
										.getExpression()));
						throw new SuccessException();
					});
		} catch (SuccessException e) {
			return;
		}

		Assert.fail();
	}

	@Test
	public void testSalaryDataIII()
			throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();

		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				new String[] { "1500.00 * DIAS_TRABAJADOS / DIAS_MES", },
				new String[] {});

		cleanSystemData(aonContext);
		addSSRegimeData(aonContext,
				SSRegimeType.values()[contract.getSsRegime()],
				getFirstDayOfYear(getToday()), null,
				new HashMap<String, String>() {
					{
						put(ContextVariable.MONDAY_HOURS.getName(), "8.00");
						put(ContextVariable.TUESDAY_HOURS.getName(), "8.00");
						put(ContextVariable.WEDNESDAY_HOURS.getName(), "8.00");
						put(ContextVariable.THURSDAY_HOURS.getName(), "8.00");
						put(ContextVariable.FRIDAY_HOURS.getName(), "8.00");
						put("BASE_CGP_MIN",
								"TIEMPO_COMPLETO ? 756.6000 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA ");
						put("BASE_CGP_MAX",
								"3606.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)");
						put("BASE_CGC_MIN",
								"[ \"01\":(TIEMPO_COMPLETO ? 1056.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 6.37 * HORAS_NOMINA), \"02\":(TIEMPO_COMPLETO ? 876.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 5.28 * HORAS_NOMINA), \"03\":(TIEMPO_COMPLETO ? 762.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.59 * HORAS_NOMINA), \"04\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA), \"05\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA), \"06\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA), \"07\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA), \"08\":(TIEMPO_COMPLETO ? 25.22 * DIAS_NOMINA : 4.56 * HORAS_NOMINA), \"09\":(TIEMPO_COMPLETO ? 25.22 * DIAS_NOMINA : 4.56 * HORAS_NOMINA), \"10\":(TIEMPO_COMPLETO ? 25.22* DIAS_NOMINA : 4.56 * HORAS_NOMINA), \"11\":(TIEMPO_COMPLETO ? 25.22 * DIAS_NOMINA : 4.56 * HORAS_NOMINA) ] [GRUPO_COTIZACION]");
						put("BASE_CGC_MAX",
								"[ \"01\":(3606.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)), \"02\":(3606.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)), \"03\":(3606.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)), \"04\":(3606.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)), \"05\":(3606.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)), \"06\":(3606.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)), \"07\":(3606.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)), \"08\":(120.20 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)), \"09\":(120.20 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)), \"10\":(120.20 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)), \"11\":(120.20 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)) ] [GRUPO_COTIZACION]");
					}
				});

		cleanSystemCosts(aonContext);

		addData(aonContext, contract, getFirstDayOfYear(getToday()), null,
				new HashMap<String, String>() {
					{
						put("TC2", "'200'");
						put("GRUPO_COTIZACION", "\"01\"");
					}
				});

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);

		ContractSalaryCalculator<ISalary> calculator = new ContractSalaryCalculator<ISalary>();

		JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder(connection);
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();

		try {
			AON.getSalaries(aonContext,
					props -> props.getContractProperty().eq(contract.getId()))
					.forEach(salary -> {
						for (ContextVariable var : new ContextVariable[] {
								ContextVariable.MONDAY_HOURS,
								ContextVariable.TUESDAY_HOURS,
								ContextVariable.WEDNESDAY_HOURS,
								ContextVariable.THURSDAY_HOURS,
								ContextVariable.FRIDAY_HOURS,
								ContextVariable.SATURDAY_HOURS,
								ContextVariable.SUNDAY_HOURS,
//								ContextVariable.WORKED_HOURS,
								ContextVariable.SALARY_HOURS, }) {
							Assert.assertEquals(var.getName(), true, salary.getContextData()
									.containsKey(var.getName()));
							System.out.printf("%s = %s\r\n", var.name(),
									salary.getContextData().get(var.getName())
											.get(0).getExpression());
						}

						throw new SuccessException();
					});
		} catch (SuccessException e) {
			return;
		}

		Assert.fail();
	}

	@Test
	public void testSalaryDataIV()
			throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();

		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				new String[] { 
					"1500.00 * DIAS_TRABAJADOS / DIAS_MES"						
				},
				new String[] {
					"BASE_CGC * PORCENTAJE_20",	
					"BASE_CGC * PORCENTAJE_05",	
				}
		);
		
		cleanSystemData(aonContext);
		addSystemData(aonContext,
				getFirstDayOfYear(getToday()), null,
				new HashMap<String, String>() {
					{
						put("SMI","648.60");
						put("MAX_EMBARGABLE","def(l){ MAX(((l - SMI) * 0.30),0)+MAX(((l - 2 * SMI ) * 0.20),0) + MAX((( l - 3 * SMI ) * 0.10),0) +MAX((( l - 4 * SMI ) * 0.15),0) + MAX((( l - 5 * SMI ) * 0.15),0)}");
						put("EMBARGAR","def (embargo){ ( PENDIENTE = ( embargo - EMBARGADO ) ) > 0 ? MIN(MAX_EMBARGABLE(TOTAL_LIQUIDO), PENDIENTE ) : REMOVE()  }");
					}
				});
		
		
		addEmbargo(aonContext, contract, "EMBARGAR(IMPORTE_EMBARGO)");
		
		
		addData(aonContext, contract, 
				getFirstDayOfYear(getToday()), 
				null,
				new HashMap<String, String>() {
					{
						put("TC2", "'100'");
						put("OCUPACION", "\"a\"");
						put("PORCENTAJE_20", "0.20");
						put("PORCENTAJE_05", "0.05");
						put("IMPORTE_EMBARGO", "1000.00");
						put("GRUPO_COTIZACION", "\"01\"");
						put("OCUPACION_IT", "[\"a\": 0.65, \"b\": 1.00, \"d\": 3.35, \"e\": 1.80, \"f\": 3.35, \"g\": 2.10, \"h\": 1.40]");
					}
				});

		addSSRegimeCost(aonContext, 
				SSRegimeType.GENERAL, 
				getFirstDayOfYear(getToday()), 
				ContextVariable.CGC_ENTERPRISE.getName(), 
				DeductionType.COMMON_CONTINGENCY, 
				"BASE_CGP_E * (isdef PORCENTAJE_IT ? PORCENTAJE_IT : (PORCENTAJE_IT=( isdef OCUPACION ? OCUPACION_IT[OCUPACION] : TARIFA_IT)))/100");
		
		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);

		ContractSalaryCalculator<ISalary> calculator = new ContractSalaryCalculator<ISalary>();

		JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder(connection);
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();

		start = add(start, Calendar.MONTH,1);
		end = getLastDayOfMonth(start);
		ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);

		jooqSalaryBuilder = new JooqSalaryBuilder(connection);
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();

		try {
			AON.getSalaries(aonContext,
					props -> props.getContractProperty().eq(contract.getId()))
					.forEach(salary -> {
						for (String var : new String[] {
								"PORCENTAJE_20",
								"PORCENTAJE_05",
								"IMPORTE_EMBARGO",
								"SMI",
								"EMBARGADO",
								"OCUPACION",
								}) {
							Assert.assertEquals(true, salary.getContextData()
									.containsKey(var));
							System.out.printf("%s = %s\r\n", var,
									salary.getContextData().get(var)
											.get(0).getExpression());
						}
						
						throw new SuccessException();
					});
		} catch (SuccessException e) {
			return;
		}

		Assert.fail();
	}

	@Test
	public void testEmbargoSyntaxError()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				new String[] { 
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				},
				new String[] { 
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
						"BASE_IRPF * PORCENTAJE_IRPF/100" 
				});
		
		// Embargo with fatal syntax error
		addEmbargo(aonContext, contract, "100.00(" );

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);

		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		calculator.setListener(new ContractSalaryCalculator.Listener() {
			@Override
			public void onCompileError(IContractDeduction deduction,
					String message) {
				System.out.println(message);
			}
		});

		ISalary salary = calculator.calculate(ctx);

		Assert.assertEquals(1500.00, salary.getTotalPayment());

	}

	@Test
	public void testBonusSyntaxError()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				new String[] { 
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				},
				new String[] { 
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
						"BASE_IRPF * PORCENTAJE_IRPF/100" 
				});
		
		// Embargo with fatal syntax error
		
		addBonus(aonContext, contract, addBonusConcept(aonContext, BonusType.EMPLOYMENT_PROMOTION, ""), "100.00(" );

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);

		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		calculator.setListener(new ContractSalaryCalculator.Listener() {
			@Override
			public void onCompileError(IContractBonus bonus,
					String message) {
				System.out.println(message);
			}
		});

		ISalary salary = calculator.calculate(ctx);

		Assert.assertEquals(1500.00, salary.getTotalPayment());

	}

	@Test
	public void testDescriptionUndefVariable()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				new String[] { 
				},
				new String[] { 
				});
		
		addPayment(aonContext, 
				contract, 
				"DESCRIPTION WITH UNDEFINED VAR @{CUALESQUIERA}", 
				"666.66 * DIAS_TRABAJADOS / DIAS_MES", 
				"_P", 
				"_P", 
				PaymentType.CRA_0001);
		addPayment(aonContext, 
				contract, 
				"DESCRIPTION WITH UNDEFINED VAR @{CUALESQUIERA ", 
				"666.66 * DIAS_TRABAJADOS / DIAS_MES", 
				"_P", 
				"_P", 
				PaymentType.CRA_0001);
		
		Date start = getFirstDayOfMonth(add(getToday(), Calendar.MONTH, 1));
		Date end = getLastDayOfMonth(start);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);

		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		final List<String> errors = new ArrayList<String>();
		
		calculator.setListener(new ContractSalaryCalculator.Listener() {
			@Override
			public void onCheckError(IContractPayment payment, String message) {
				errors.add(message);
			}
			
			@Override
			public void onCompileError(IContractPayment payment, String message) {
				errors.add(message);
			}
		});
		
		ISalary salary = calculator.calculate(ctx);
		
		for ( String message: errors ) 
			System.out.println("ERROR: " + message);
		
		
		Assert.assertEquals(2, errors.size() );
		Assert.assertEquals(666.66 * 2, salary.getTotalPayment());
		
		
	}
	
	@Test
	public void testConstantWarnningI()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				new String[] { 
						"25.00",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"BRUTO(4000.00) * DIAS_TRABAJADOS / DIAS_MES"
						},
				new String[] { 
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
						"BASE_IRPF * PORCENTAJE_IRPF/100" });

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);

		ctx.next();

		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		calculator.setListener(new ContractSalaryCalculator.Listener() {
			@Override
			public void onCheckError(IContractPayment payment, String message) {
				System.out.println(message);
				if ( payment.getExpression().equals("25.00"))
					throw new SuccessException();
			}
		});

		try {
			ISalary salary = calculator.calculate(ctx);
		} catch ( SuccessException e ){
			return;
		}
		
		Assert.fail();
	}
	
	@Test
	public void testConstantWarnningII()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				new String[] { 
						"4000.00 + 200.00 + 10.00"
						},
				new String[] { 
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
						"BASE_IRPF * PORCENTAJE_IRPF/100" });

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);

		ctx.next();

		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		calculator.setListener(new ContractSalaryCalculator.Listener() {
			@Override
			public void onCheckError(IContractPayment payment, String message) {
				System.out.println(message);
				if ( payment.getExpression().equals("4000.00 + 200.00 + 10.00"))
					throw new SuccessException();
			}
		});

		try {
			ISalary salary = calculator.calculate(ctx);
		} catch ( SuccessException e ){
			return;
		}
		
		Assert.fail();
	}
	// ------------------------------------------------------------------------
	@Test
	public void testConstantWarnningIII()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				new String[] { 
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES"
						},
				new String[] { 
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
						"BASE_IRPF * PORCENTAJE_IRPF/100" });

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);
		
		PaymentConceptRecord cteConcept = addConcept(aonContext, "CTE");
		addPayment(aonContext, contract, start, end, cteConcept, "666.66");

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, start, end, end, contract);

		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		calculator.setListener(new ContractSalaryCalculator.Listener() {
			@Override
			public void onCheckError(IContractPayment payment, String message) {
				if ( payment.getExpression().equals("666.66"))
					Assert.fail();
			}
		});

		ISalary salary = calculator.calculate(ctx);
	}

	@Test
	public void testConstantWarnningIV()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				new String[] { 
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES"
						},
				new String[] { 
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
						"BASE_IRPF * PORCENTAJE_IRPF/100" });

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);
		
		PaymentConceptRecord cteConcept = addConcept(aonContext, "CTE");
		addPayment(aonContext, contract, start, add(end, Calendar.DAY_OF_MONTH, 10), cteConcept, "666.66");

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, start, end, end, contract);

		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		calculator.setListener(new ContractSalaryCalculator.Listener() {
			@Override
			public void onCheckError(IContractPayment payment, String message) {
				if ( payment.getExpression().equals("666.66"))
					throw new SuccessException();
			}
		});

		try {
			ISalary salary = calculator.calculate(ctx);
		} catch ( SuccessException e ){
			return;
		}
		
		Assert.fail();
	}

	@Test
	public void testPaymentConceptVariable()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()),
				Collections.emptyMap(),
				new String[] { 
						},
				new String[] { 
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
						"BASE_IRPF * PORCENTAJE_IRPF/100" },
				null);

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);
		
		PaymentConceptRecord baseConcept = addConcept(aonContext, "SALARIO_BASE");
		addPayment(aonContext, contract, start, null, baseConcept, "1000.00  * DIAS_TRABAJADOS / DIAS_MES ");

		PaymentConceptRecord plusConcept = addConcept(aonContext, "PLUS_SALARIAL");
		addData(aonContext, contract, start, null, "PLUS_SALARIAL", "250.00" );
		addPayment(aonContext, contract, start, null, plusConcept, "PLUS_SALARIAL  * DIAS_TRABAJADOS / DIAS_MES ");

		PaymentConceptRecord pagaExtra = addConcept(aonContext, "PAGA_EXTRA");
		addPayment(aonContext, contract, start, null, pagaExtra, "(SALARIO_BASE + PLUS_SALARIAL)/12");

		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, start, end, end, contract);

		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());
		
		ISalary salary = calculator.calculate(ctx);
		
		Assert.assertEquals( 1250.00 + 1250.00/12, salary.getTotalPayment());
	}

	@Test
	public void testPaymentConceptVariableII()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()),
				Collections.emptyMap(),
				new String[] { 
						},
				new String[] { 
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
						"BASE_IRPF * PORCENTAJE_IRPF/100" },
				null);

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);
		
		PaymentConceptRecord baseConcept = addConcept(aonContext, "SALARIO_BASE");
		addPayment(aonContext, contract, start, null, baseConcept, "1000.00  * DIAS_TRABAJADOS / DIAS_MES ");

		PaymentConceptRecord plusConcept = addConcept(aonContext, "PLUS_SALARIAL");
		addData(aonContext, contract, start, null, "PLUS_SALARIAL", "250.00" );
		addPayment(aonContext, contract, start, null, plusConcept, "250.00  * DIAS_TRABAJADOS / DIAS_MES ");

		PaymentConceptRecord pagaExtra = addConcept(aonContext, "PAGA_EXTRA");
		addPayment(aonContext, contract, start, null, pagaExtra, "(SALARIO_BASE + PLUS_SALARIAL)/12");

		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, start, end, end, contract);

		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());
		
		ISalary salary = calculator.calculate(ctx);
		
		Assert.assertEquals( 1250.00 + 1250.00/12, salary.getTotalPayment());
	}

	@Test
	public void testPaymentConceptVariableIII()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()),
				Collections.emptyMap(),
				new String[] { 
						},
				new String[] { 
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
						"BASE_IRPF * PORCENTAJE_IRPF/100" },
				null);

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);
		
		PaymentConceptRecord baseConcept = addConcept(aonContext, "SALARIO_BASE");
		addPayment(aonContext, contract, start, null, baseConcept, "1000.00  * DIAS_TRABAJADOS / DIAS_MES ");

		PaymentConceptRecord plusConcept = addConcept(aonContext, "PLUS_SALARIAL");
		addData(aonContext, contract, start, null, "PLUS_SALARIAL", "250.00" );
		addPayment(aonContext, contract, start, null, plusConcept, "250.00  * DIAS_TRABAJADOS / DIAS_MES ");
		addPayment(aonContext, contract, start, null, plusConcept, "350.00  * DIAS_TRABAJADOS / DIAS_MES ");

		PaymentConceptRecord pagaExtra = addConcept(aonContext, "PAGA_EXTRA");
		addPayment(aonContext, contract, start, null, pagaExtra, "(SALARIO_BASE + PLUS_SALARIAL)/12");

		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, start, end, end, contract);

		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());
		
		ISalary salary = calculator.calculate(ctx);
		
		Assert.assertEquals( 1600.00 + 1600.00/12, salary.getTotalPayment());
	}

	@Test
	public void testDescriptionRoundVariable()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				new String[] { 
				},
				new String[] { 
				});
		
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "CUALESQUIERA", "100.00/3.00");
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "DIAS", "12");
		addPayment(aonContext, 
				contract, 
				"DESCRIPTION WITH UNDEFINED VAR @{CUALESQUIERA} @{DIAS}", 
				"666.66 * DIAS_TRABAJADOS / DIAS_MES", 
				"_P", 
				"_P", 
				PaymentType.CRA_0001);
		
		
		Date start = getFirstDayOfMonth(add(getToday(), Calendar.MONTH, 1));
		Date end = getLastDayOfMonth(start);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);

		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		
		Salary salary = calculator.calculate(ctx);
		
		for (IPayment payment : salary.getSalaryPayments()) {
			Assert.assertEquals("DESCRIPTION WITH UNDEFINED VAR 33.33 12", payment.getDescription());
		}
		
		Assert.assertEquals(666.66 * 1, salary.getTotalPayment());
		
		
	}
	
	@Test
	public void testDescriptionRoundExpression()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				new String[] { 
				},
				new String[] { 
				});
		
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "CUALESQUIERA", "100.00");
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "DIAS", "12");
		addPayment(aonContext, 
				contract, 
				"DESCRIPTION WITH UNDEFINED VAR @{CUALESQUIERA/3.00} @{DIAS}", 
				"666.66 * DIAS_TRABAJADOS / DIAS_MES", 
				"_P", 
				"_P", 
				PaymentType.CRA_0001);
		
		
		Date start = getFirstDayOfMonth(add(getToday(), Calendar.MONTH, 1));
		Date end = getLastDayOfMonth(start);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);

		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		
		Salary salary = calculator.calculate(ctx);
		
		for (IPayment payment : salary.getSalaryPayments()) {
			Assert.assertEquals("DESCRIPTION WITH UNDEFINED VAR 33.33 12", payment.getDescription());
		}
		
		Assert.assertEquals(666.66 * 1, salary.getTotalPayment());
		
		
	}

	private static void load(Map<String, ITimedVariable<?>> context,
			Map<String, Object> data) {
		for (Entry<String, ITimedVariable<?>> entry : context.entrySet()) {
			ITimedVariable<?> var = entry.getValue();

			Object value = var.getValue(var.getPeriod());
			System.out.println("CONTEXT : " + entry.getKey() + " = " + value);
			data.put(entry.getKey(), value);
			if (var instanceof ITimedResult<?>)
				load(((ITimedResult<?>) var).getContext(), data);
			if (var instanceof IExpressionVariable<?>)
				load(((IExpressionVariable<?>) var).getContext(), data);
		}
	}

	private static class SuccessException extends RuntimeException {

	}

}
