package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.DROP_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_FACTOR;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getMax;
import static java.util.Calendar.DATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mvel2.CompileException;

import com.code.aon.common.AonException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.IrpfOutcome;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.payroll.calculator.IContractCost;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext.IListener;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.expression.ExpressionContext.ExpressionExceptionWrapper;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;
//import com.esferalia.aon.salary.expression.CompileException;
import com.esferalia.aon.watson.util.AonDateUtils;


public class SQLContractSalaryCalculatorContextTestCase extends
		AbstractSQLTestCase {
	
	private static class SuccessException extends Exception {
		
	}

	@Test
	public void testSystemFunction() throws SQLException, AonException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		addSystemData(aonContext, getFirstDayOfYear(getToday()), null, 
				new HashMap<String,String>(){
			{
//				put("BASE_CGC_MIN", "( COTIZACION_MENSUAL ) ? BASE_CGC_MIN_MES : BASE_CGC_MIN_DIA");
//				put("BASE_CGC_MIN_DIA", "[ \"01\":70.84, \"02\":58.75, \"03\":51.10, \"04\":50.73, \"05\":50.73, \"06\":50.73, \"07\":50.73, \"08\":50.73, \"09\":50.73, \"10\":50.73, \"11\":50.73][GRUPO_COTIZACION]  * JORNADAS_REALES");
//				put("BASE_CGC_MIN_MES", "[ \"01\":1629.30,\"02\":1351.20,\"03\":1175.40,\"04\":1166.70,\"05\":1166.70,\"06\":1166.70,\"07\":1166.70,\"08\":1166.70,\"09\":1166.70,\"10\":1166.70,\"11\":1166.70][GRUPO_COTIZACION] * ( DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30 )");
				put("COTIZACION_MENSUAL", "GRUPO_COTIZACION; isdef MODELO_COTIZACION_AGRARIO ? MODELO_COTIZACION_AGRARIO != 2 : VERDADERO()");
			}
		});
		
		Date firstDayOfMonth = getFirstDayOfMonth(getToday());
		ContractRecord contract = newContract(
				aonContext
				,firstDayOfMonth
				, new HashMap<String, String>(){
					{
						put("TC2", "\"100\"");
						put("GRUPO_COTIZACION", "\"10\"");
						put("COTIZACION_MENSUAL", "SISTEMA('COTIZACION_MENSUAL')");
					}
				}
				, new String[] {
					"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
					"COTIZACION_MENSUAL ? 100.00 : 0.00"
						
				}
				, new String[] {
						
				}
				,null
				);
		Date lastDayOfMonth = getLastDayOfMonth(getToday());
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder())
				.calculate(getContractSalaryCalculatorContext(connection, firstDayOfMonth, lastDayOfMonth, lastDayOfMonth, contract));
		
		assertEquals(1100.00, salary.getTotalPayment(), 0.00);
		//salary.getSalaryDatas().forEach( d -> System.out.println( d.getName() + " : " + d.getExpression() ));
		
	}

	@Test
	public void testSystemFunctionIrpfPercent() throws SQLException, AonException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		addSystemData(aonContext, getFirstDayOfYear(getToday()), null, 
				new HashMap<String,String>(){
			{
			}
		});
		
		Date firstDayOfMonth = getFirstDayOfMonth(getToday());
		ContractRecord contract = newContract(
				aonContext
				,firstDayOfMonth
				, new HashMap<String, String>(){
					{
						put("TC2", "\"100\"");
						put("GRUPO_COTIZACION", "\"10\"");
					}
				}
				, new String[] {
					"2000.00 * DIAS_TRABAJADOS / DIAS_MES",
				}
				, new String[] {
				}
				,null
				);
		Date lastDayOfMonth = getLastDayOfMonth(getToday());
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, firstDayOfMonth, lastDayOfMonth, lastDayOfMonth, contract);
		double defaultIrpf = ctx.getExpressionContext().eval("PORCENTAJE_IRPF", firstDayOfMonth, lastDayOfMonth, Double.class ).stream().collect(Collectors.summingDouble( ITimedResult::getValue));
		double systemIrpf = ctx.getExpressionContext().eval("SISTEMA('PORCENTAJE_IRPF')", firstDayOfMonth, lastDayOfMonth, Double.class ).stream().collect(Collectors.summingDouble( ITimedResult::getValue));
		
		assertEquals(defaultIrpf, systemIrpf, 0.00);
	}

	@Test
	public void testWorkedFactorOffDays() throws SQLException, AonException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date firstDayOfMonth = getFirstDayOfMonth(getToday());
		ContractRecord contract = newContract(
				aonContext
				,firstDayOfMonth
				, new HashMap<String, String>(){
					{
						put("TC2", "\"200\"");
						put("HORAS_LUNES", "4");
						put("HORAS_MARTES", "4");
						put("HORAS_MIERCOLES", "4");
						put("HORAS_JUEVES", "4");
						put("HORAS_VIERNES", "4");
						put("HORAS_SABADO", "0");
						put("HORAS_DOMINGO", "0");
					}
				}
				);
		Date lastDayOfMonth = getLastDayOfMonth(getToday());
		
		getContractSalaryCalculatorContext(connection, firstDayOfMonth, lastDayOfMonth, lastDayOfMonth, contract).getExpressionContext()
		.eval(WORKED_FACTOR.getName(), firstDayOfMonth, lastDayOfMonth,Number.class)
		.forEach(r -> assertEquals(0.5, r.getValue().doubleValue(), 0.00));
		
		// all month off 
		addData(aonContext, contract, firstDayOfMonth, lastDayOfMonth, DROP_FACTOR, "1.0");

		getContractSalaryCalculatorContext(connection, firstDayOfMonth, lastDayOfMonth, lastDayOfMonth, contract).getExpressionContext()
		.eval(WORKED_FACTOR.getName(), firstDayOfMonth, lastDayOfMonth,Number.class)
		.forEach(r -> assertEquals(0.5, r.getValue().doubleValue(), 0.00));
	}

	@Test
	public void testSystemCosts() throws SQLException, AonException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemCosts(aonContext);
		addSSRegimeCost(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), "ECSS_E",
				DeductionType.COMMON_CONTINGENCY, "50");
		addSSRegimeCost(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), "ECSS_E",
				DeductionType.COMMON_CONTINGENCY, "100");
		addSSRegimeCost(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), "ECSS_E",
				DeductionType.COMMON_CONTINGENCY, "200");

		ContractRecord contract = newContract(aonContext, new String[] {},
				new String[] {});

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);
		ctx.next();

		int costs = 0;
		for (IContractCost cost : ctx.getContractCosts()) {
			costs++;
			System.out.println(cost.getName() + " '" + cost.getExpression()
					+ "' [ " + cost.getStartDate() + "..." + cost.getEndDate()
					+ "]");
		}

		assertEquals(3, costs, "ECSS_E");

		ctx = new SQLContractSalaryCalculatorContext(connection, start, end,
				end, criteria);
		ctx.next();

		Salary salary = new ContractSalaryCalculator<Salary>(
				new SalaryBuilder()).calculate(ctx);

		assertEquals(350.00, salary.getSalaryCosts().stream().collect(Collectors.summingDouble(cost -> cost.getAmount())),"ECSS_E");

	}

	@Test
	public void testSystemCostsII() throws SQLException, AonException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemCosts(aonContext);
		addSSRegimeCost(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), "CGC_E",
				DeductionType.COMMON_CONTINGENCY, "50");
		addSSRegimeCost(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), "CGC_E",
				DeductionType.COMMON_CONTINGENCY, "100");
		addSSRegimeCost(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), "CGC_P",
				DeductionType.COMMON_CONTINGENCY, "999");
		addSSRegimeCost(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), "NESTR",
				DeductionType.COMMON_CONTINGENCY, "111");
		addCCCCost(aonContext, CCCType.TRAINING, getFirstDayOfYear(getToday()),
				"CGC_E", DeductionType.COMMON_CONTINGENCY, "200");
		addCCCCost(aonContext, CCCType.TRAINING, getFirstDayOfYear(getToday()),
				"CGC_E", DeductionType.COMMON_CONTINGENCY, "300");
		addCCCCost(aonContext, CCCType.TRAINING, getFirstDayOfYear(getToday()),
				"CGC_P", DeductionType.COMMON_CONTINGENCY, "666");

		ContractRecord contract = newContract(aonContext, SSRegimeType.GENERAL,
				CCCType.TRAINING, getFirstDayOfYear(getToday()),
				Collections.emptyMap(), new String[] {}, new String[] {}, null);

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);
		ctx.next();

		int costs = 0;
		for (IContractCost cost : ctx.getContractCosts()) {
			costs++;
			System.out.println(cost.getName() + " '" + cost.getExpression()
					+ "' [ " + cost.getStartDate() + "..." + cost.getEndDate()
					+ "]");
		}

		assertEquals(4, costs);

		ctx = new SQLContractSalaryCalculatorContext(connection, start, end,
				end, criteria);
		ctx.next();

		Salary salary = new ContractSalaryCalculator<Salary>(
				new SalaryBuilder()).calculate(ctx);

		assertEquals(
				500.00 + 666.00 + 111.00,
				salary.getSalaryCosts()
						.stream()
						.collect(
								Collectors.summingDouble(cost -> cost
										.getAmount())));

	}

	@Test
	@Disabled("Deprecated...")
	public void testRedefinedImplicit() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		long customVarI = Math.round(Math.random() * 1000);
		long customVarII = Math.round(Math.random() * 1000);
		long customVarIII = Math.round(Math.random() * 1000);
		long customVarVI = Math.round(Math.random() * 1000);
		addSystemData(aonContext, AonDateUtils.getFirstDayOfYear(getToday()),
				null, new HashMap<String, String>() {
					{
						put("_" + Long.toString(customVarI),
								Long.toString(customVarI));
						put("_" + Long.toString(customVarII),
								Long.toString(customVarII));
						put("_" + Long.toString(customVarIII),
								Long.toString(customVarIII));
						put("_" + Long.toString(customVarVI),
								Long.toString(customVarVI));
					}
				});

		AgreementRecord agreement = newAgreement(aonContext);
		addData(aonContext, agreement,
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
						put("_" + Long.toString(customVarII),
								Long.toString(customVarII * 1000));
						put("_" + Long.toString(customVarVI),
								String.format("%d * X", customVarVI));
					}
				});

		AgreementLevelCategoryRecord category = newAgreementCategory(
				aonContext, agreement);

		addData(aonContext, category,
				AonDateUtils.getFirstDayOfYear(getToday()), null,
				new HashMap<String, String>() {
					{
						put("_" + Long.toString(customVarIII),
								Long.toString(customVarIII * 10000));
					}
				});

		ContractRecord contract = newContract(
				aonContext,
				new String[] {
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						String.format("_%d * DIAS_TRABAJADOS / DIAS_MES",
								customVarVI) }, new String[] {
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
						"BASE_IRPF * PORCENTAJE_IRPF/100" }, category);

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		addData(aonContext, contract, contract.getStartDate(),
				contract.getEndDate(), new HashMap<String, String>() {
					{
						put(WORKED_DAYS.getName(), "666");
						put(MONTH_DAYS.getName(), "33");
						put("_" + Long.toString(customVarI),
								Long.toString(customVarI * 100));
						put("X", "2000");
					}
				});

		final Map<String, ITimedVariable<?>> redefinedMap = new HashMap<String, ITimedVariable<?>>();
		final Map<String, ITimedVariable<?>> implicitMap = new HashMap<String, ITimedVariable<?>>();

		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);
		ctx.setListener(new IListener() {

			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
				// Nothing
			}

			@Override
			public void onRedefinedImplicit(String name,
					ITimedVariable<?> redefined, ITimedVariable<?> implicit) {
				redefinedMap.put(name, redefined);
				implicitMap.put(name, implicit);
			}
		});
		ctx.next();
		new ContractSalaryCalculator<Salary>(new SalaryBuilder())
				.calculate(ctx);

		ITimedVariable<?> userWorkedDays = redefinedMap.get(WORKED_DAYS
				.getName());
		assertEquals(666,userWorkedDays.getValue(userWorkedDays.getPeriod()),String.format("%s", WORKED_DAYS));
		ITimedVariable<?> systemWorkedDays = implicitMap.get(WORKED_DAYS
				.getName());
		assertEquals(systemWorkedDays.getValue(systemWorkedDays.getPeriod()), 33.00,String.format("%s", WORKED_DAYS));

		ITimedVariable<?> userMonthDays = redefinedMap
				.get(MONTH_DAYS.getName());
		assertEquals(33,userMonthDays.getValue(userMonthDays.getPeriod()), String.format("%s", MONTH_DAYS));
		ITimedVariable<?> systemMonthDays = implicitMap.get(MONTH_DAYS
				.getName());
		assertEquals(getMax(getToday(), DATE),systemMonthDays.getValue(systemMonthDays.getPeriod()),String.format("%s", MONTH_DAYS));

		ITimedVariable<?> userCustomDays = redefinedMap.get("_"
				+ Long.toString(customVarI));
		assertEquals(customVarI * 100,((Number) userCustomDays.getValue(userCustomDays.getPeriod())).longValue(), "_" + Long.toString(customVarI));
		ITimedVariable<?> systemCustomDays = implicitMap.get("_"
				+ Long.toString(customVarI));
		assertEquals(customVarI,((Number) systemCustomDays.getValue(systemCustomDays.getPeriod())).longValue(), "_" + Long.toString(customVarI));

		ITimedVariable<?> userCustomIIDays = redefinedMap.get("_"
				+ Long.toString(customVarII));
		assertEquals(customVarII * 1000, ((Number) userCustomIIDays.getValue(userCustomIIDays.getPeriod())).longValue(),"_" + Long.toString(customVarII));
		ITimedVariable<?> systemCustomIIDays = implicitMap.get("_"
				+ Long.toString(customVarII));
		assertEquals(customVarII,((Number) systemCustomIIDays.getValue(systemCustomIIDays.getPeriod())).longValue(),"_" + Long.toString(customVarII));

		ITimedVariable<?> userCustomIIIDays = redefinedMap.get("_"
				+ Long.toString(customVarIII));
		assertEquals(customVarIII * 10000, ((Number) userCustomIIIDays.getValue(userCustomIIIDays.getPeriod())).longValue(),"_" + Long.toString(customVarIII));
		ITimedVariable<?> systemCustomIIIDays = implicitMap.get("_"
				+ Long.toString(customVarIII));
		assertEquals(customVarIII,((Number) systemCustomIIIDays.getValue(systemCustomIIIDays.getPeriod())).longValue(),"_" + Long.toString(customVarIII));
		ITimedVariable<?> userCustomVIDays = redefinedMap.get("_"
				+ Long.toString(customVarVI));
		try {
			assertEquals(customVarVI * 2000, ((Number) userCustomVIDays.getValue(userCustomVIDays.getPeriod())).longValue(),"_" + Long.toString(customVarVI));
		} catch (ExpressionExceptionWrapper e) {

		}
		ITimedVariable<?> systemCustomVIDays = implicitMap.get("_"
				+ Long.toString(customVarVI));
		assertEquals(customVarVI,((Number) systemCustomVIDays.getValue(systemCustomVIDays.getPeriod())).longValue(),"_" + Long.toString(customVarVI) );
	}

	@Test
	public void testSplit() {

		List<Period> worked = new ArrayList<Period>();
		worked.add(new Period(getFirstDayOfMonth(getToday()),
				getLastDayOfMonth(getToday())));

		List<Period> leaves = new ArrayList<Period>();
		leaves.add(new Period(getFirstDayOfMonth(getToday()),
				getFirstDayOfMonth(getToday())));

		List<Period> split = SQLContractSalaryCalculatorContext.SQLNoItContractSalaryCalculatorContext
				.split(worked, leaves);
		
		assertEquals(2, split.size());
		assertEquals(getFirstDayOfMonth(getToday()), split.get(0).getStart());
		assertEquals(getFirstDayOfMonth(getToday()), split.get(0).getEnd());
		assertEquals(add(getFirstDayOfMonth(getToday()),DATE,1), split.get(1).getStart());
		assertEquals(getLastDayOfMonth(getToday()), split.get(1).getEnd());

	
		leaves.add(new Period(getLastDayOfMonth(getToday()),
				getLastDayOfMonth(getToday())));

		split = SQLContractSalaryCalculatorContext.SQLNoItContractSalaryCalculatorContext
				.split(worked, leaves);
		assertEquals(3, split.size());
		assertEquals(getFirstDayOfMonth(getToday()), split.get(0).getStart());
		assertEquals(getFirstDayOfMonth(getToday()), split.get(0).getEnd());
		assertEquals(add(getFirstDayOfMonth(getToday()),DATE,1), split.get(1).getStart());
		assertEquals(add(getLastDayOfMonth(getToday()),DATE,-1), split.get(1).getEnd());
		assertEquals(getLastDayOfMonth(getToday()), split.get(2).getStart());
		assertEquals(getLastDayOfMonth(getToday()), split.get(2).getEnd());
		
		leaves.add(new Period(add(getFirstDayOfMonth(getToday()),DATE, 13),
				add(getFirstDayOfMonth(getToday()),DATE, 19)));

		Collections.sort(leaves); // Must be sort
		split = SQLContractSalaryCalculatorContext.SQLNoItContractSalaryCalculatorContext
				.split(worked, leaves);
		
		assertEquals(5, split.size());
		assertEquals(getFirstDayOfMonth(getToday()), split.get(0).getStart());
		assertEquals(getFirstDayOfMonth(getToday()), split.get(0).getEnd());
		assertEquals(add(getFirstDayOfMonth(getToday()),DATE,1), split.get(1).getStart());
		assertEquals(add(getFirstDayOfMonth(getToday()),DATE, 12), split.get(1).getEnd());
		assertEquals(add(getFirstDayOfMonth(getToday()),DATE, 13), split.get(2).getStart());
		assertEquals(add(getFirstDayOfMonth(getToday()),DATE, 19), split.get(2).getEnd());
		assertEquals(add(getFirstDayOfMonth(getToday()),DATE, 20), split.get(3).getStart());
		assertEquals(add(getLastDayOfMonth(getToday()),DATE, -1), split.get(3).getEnd());
		assertEquals(getLastDayOfMonth(getToday()), split.get(4).getStart());
		assertEquals(getLastDayOfMonth(getToday()), split.get(4).getEnd());

		//
		List<Period> whole = new ArrayList<Period>();
		whole.add(new Period(getFirstDayOfMonth(getToday()),
				getLastDayOfMonth(getToday())));
		split = SQLContractSalaryCalculatorContext.SQLNoItContractSalaryCalculatorContext
				.split(worked, whole);
		assertEquals(1, split.size());
		assertEquals(getFirstDayOfMonth(getToday()), split.get(0).getStart());
		assertEquals(getLastDayOfMonth(getToday()), split.get(0).getEnd());
	
		whole = new ArrayList<Period>();
		whole.add(new Period(add(getFirstDayOfMonth(getToday()), DATE, -1),
				add(getLastDayOfMonth(getToday()), DATE, 1)));
		split = SQLContractSalaryCalculatorContext.SQLNoItContractSalaryCalculatorContext
				.split(worked, whole);
		assertEquals(1, split.size());
		assertEquals(getFirstDayOfMonth(getToday()), split.get(0).getStart());
		assertEquals(getLastDayOfMonth(getToday()), split.get(0).getEnd());

		//--
		List<Period> one = new ArrayList<Period>();
		one.add(new Period(add(getFirstDayOfMonth(getToday()),DATE, 10),
				add(getFirstDayOfMonth(getToday()),DATE, 20)));
		split = SQLContractSalaryCalculatorContext.SQLNoItContractSalaryCalculatorContext
				.split(worked, one);
		assertEquals(3, split.size());
		assertEquals(getFirstDayOfMonth(getToday()), split.get(0).getStart());
		assertEquals(add(getFirstDayOfMonth(getToday()),DATE, 9), split.get(0).getEnd());
		assertEquals(add(getFirstDayOfMonth(getToday()),DATE, 10), split.get(1).getStart());
		assertEquals(add(getFirstDayOfMonth(getToday()),DATE, 20), split.get(1).getEnd());
		assertEquals(add(getFirstDayOfMonth(getToday()),DATE, 21), split.get(2).getStart());
		assertEquals(getLastDayOfMonth(getToday()), split.get(2).getEnd());

		one = new ArrayList<Period>();
		one.add(new Period(add(getFirstDayOfMonth(getToday()),DATE, 0),
				add(getFirstDayOfMonth(getToday()),DATE, 20)));
		split = SQLContractSalaryCalculatorContext.SQLNoItContractSalaryCalculatorContext
				.split(worked, one);
		assertEquals(2, split.size());
		assertEquals(getFirstDayOfMonth(getToday()), split.get(0).getStart());
		assertEquals(add(getFirstDayOfMonth(getToday()),DATE, 20), split.get(0).getEnd());
		assertEquals(add(getFirstDayOfMonth(getToday()),DATE, 21), split.get(1).getStart());
		assertEquals(getLastDayOfMonth(getToday()), split.get(1).getEnd());
		
		one = new ArrayList<Period>();
		one.add(new Period(add(getFirstDayOfMonth(getToday()),DATE, -100),
				add(getFirstDayOfMonth(getToday()),DATE, 20)));
		split = SQLContractSalaryCalculatorContext.SQLNoItContractSalaryCalculatorContext
				.split(worked, one);
		assertEquals(2, split.size());
		assertEquals(getFirstDayOfMonth(getToday()), split.get(0).getStart());
		assertEquals(add(getFirstDayOfMonth(getToday()),DATE, 20), split.get(0).getEnd());
		assertEquals(add(getFirstDayOfMonth(getToday()),DATE, 21), split.get(1).getStart());
		assertEquals(getLastDayOfMonth(getToday()), split.get(1).getEnd());
		
		one = new ArrayList<Period>();
		one.add(new Period(add(getFirstDayOfMonth(getToday()),DATE, -1),
				add(getFirstDayOfMonth(getToday()),DATE, 20)));
		split = SQLContractSalaryCalculatorContext.SQLNoItContractSalaryCalculatorContext
				.split(worked, one);
		assertEquals(2, split.size());
		assertEquals(getFirstDayOfMonth(getToday()), split.get(0).getStart());
		assertEquals(add(getFirstDayOfMonth(getToday()),DATE, 20), split.get(0).getEnd());
		assertEquals(add(getFirstDayOfMonth(getToday()),DATE, 21), split.get(1).getStart());
		assertEquals(getLastDayOfMonth(getToday()), split.get(1).getEnd());

		one = new ArrayList<Period>();
		one.add(new Period(add(getFirstDayOfMonth(getToday()),DATE, 10),
				add(getFirstDayOfMonth(getToday()),DATE, 200)));
		split = SQLContractSalaryCalculatorContext.SQLNoItContractSalaryCalculatorContext
				.split(worked, one);
		assertEquals(2, split.size());
		assertEquals(getFirstDayOfMonth(getToday()), split.get(0).getStart());
		assertEquals(add(getFirstDayOfMonth(getToday()),DATE, 9), split.get(0).getEnd());
		assertEquals(add(getFirstDayOfMonth(getToday()),DATE, 10), split.get(1).getStart());
		assertEquals(getLastDayOfMonth(getToday()), split.get(1).getEnd());
		
		one = new ArrayList<Period>();
		one.add(new Period(add(getFirstDayOfMonth(getToday()),DATE, 10),
				add(getLastDayOfMonth(getToday()),DATE, 1)));
		split = SQLContractSalaryCalculatorContext.SQLNoItContractSalaryCalculatorContext
				.split(worked, one);
		assertEquals(2, split.size());
		assertEquals(getFirstDayOfMonth(getToday()), split.get(0).getStart());
		assertEquals(add(getFirstDayOfMonth(getToday()),DATE, 9), split.get(0).getEnd());
		assertEquals(add(getFirstDayOfMonth(getToday()),DATE, 10), split.get(1).getStart());
		assertEquals(getLastDayOfMonth(getToday()), split.get(1).getEnd());

		List<Period> nu11 = new ArrayList<Period>();
		nu11.add(new Period(getFirstDayOfMonth(getToday()),
				null));
		split = SQLContractSalaryCalculatorContext.SQLNoItContractSalaryCalculatorContext
				.split(worked, nu11);
		assertEquals(1, split.size());
		assertEquals(getFirstDayOfMonth(getToday()), split.get(0).getStart());
		assertEquals(getLastDayOfMonth(getToday()), split.get(0).getEnd());

	}
	
	@Test
	public void testGuaranteee() throws ExpressionException, SQLException{
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext, SSRegimeType.GENERAL,
				CCCType.TRAINING, getFirstDayOfYear(getToday()),
				Collections.emptyMap(), new String[] {}, new String[] {}, null);

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);
		ctx.next();

		try {
			ctx.getExpressionContext().eval("GTZDO(P1+P2)", start, end);
			fail();
		} catch ( CompileException e ) {
		
		}
	
		try {
			ctx.getExpressionContext().eval("GTZDO((P1)+P2)", start, end);
			fail();
		} catch ( CompileException e ) {
		}

		try {
			ctx.getExpressionContext().eval("GTZDO((P1+P2))", start, end);
			fail();
		} catch ( CompileException e ) {
		}

		try {
			ctx.getExpressionContext().eval("GTZDO(P1+(P2))", start, end);
			fail();
		} catch ( CompileException e ) {
		}

		try {
			ctx.getExpressionContext().eval("GTZDO( P1 + P2 ,1)", start, end);
			fail();
		} catch ( CompileException e ) {
		}

		try {
			ctx.getExpressionContext().eval("GTZDO( P1 + P2 ,1, 360)", start, end);
			fail();
		} catch ( CompileException e ) {
		}
		try {
			ctx.getExpressionContext().eval("GTZDO( P1 + (P2 * 100) ,1, 360)", start, end);
			fail();
		} catch ( CompileException e ) {
		}

		try {
			ctx.getExpressionContext().eval("GTZDO( (P1) + (P2 * 100) ,1, 360)", start, end);
			fail();
		} catch ( CompileException e ) {
		}
		try {
			ctx.getExpressionContext().eval("GTZDO( (P1) + (P2 * 100) ,1, 360)", start, end);
			fail();
		} catch ( CompileException e ) {
		}
		try {
			ctx.getExpressionContext().eval("GTZDO()", start, end);
			fail();
		} catch ( CompileException e ) {
		}

		try {
			ctx.getExpressionContext().eval("GTZDO(P,4) + GTZDO(P,1,3)", start, end);
			fail();
		} catch ( CompileException e ) {
		}
	}
	
	@Test
	public void testAddContractBonus() throws SQLException, AonException{
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext, SSRegimeType.GENERAL,
				CCCType.TRAINING, getFirstDayOfYear(getToday()),
				Collections.emptyMap(), new String[] {}, new String[] {}, null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);


		ISQLContractSalaryCalculatorContext ctx = 
		getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);

		try {
			ctx.getExpressionContext().eval("SELF.addBonus('BONIFICACIÓN', 'CUOTA_EMPRESARIAL'); 0.00", startDate, endDate);
			for ( IContractBonus bonus: ctx.getContractBonus()) {
				System.out.println(bonus.getDescription() + " = " + bonus.getExpression());
				throw new SuccessException();
			}
			fail();
		} catch ( SuccessException e ) {
		}
	}
	
	
	@Test
	public void testRegime() throws SQLException, AonException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date firstDayOfMonth = getFirstDayOfMonth(getToday());
		ContractRecord general = newContract(
				aonContext, 
				SSRegimeType.GENERAL, 
				CCCType.PRINCIPAL, 
				firstDayOfMonth, 
				Collections.emptyMap(), 
				new String[] {}, 
				new String[] {}, 
				null);
		Date lastDayOfMonth = getLastDayOfMonth(getToday());
		
		
		getContractSalaryCalculatorContext(connection, firstDayOfMonth, lastDayOfMonth, lastDayOfMonth, general).getExpressionContext()
		.eval(ContextVariable.REGIME.getName(), firstDayOfMonth, lastDayOfMonth)
		.forEach(r -> assertEquals(CCCType.PRINCIPAL, r.getValue()));
		
		getContractSalaryCalculatorContext(connection, firstDayOfMonth, lastDayOfMonth, lastDayOfMonth, general).getExpressionContext()
		.eval(ContextVariable.REGIME.getName() + " == " +ContextVariable.GENERAL.getName() , firstDayOfMonth, lastDayOfMonth, Boolean.class)
		.forEach(r -> assertTrue(r.getValue()));

		ContractRecord artists = newContract(
				aonContext, 
				SSRegimeType.GENERAL, 
				CCCType.ARTIST, 
				firstDayOfMonth, 
				Collections.emptyMap(), 
				new String[] {}, 
				new String[] {}, 
				null);
		
		
		getContractSalaryCalculatorContext(connection, firstDayOfMonth, lastDayOfMonth, lastDayOfMonth, artists).getExpressionContext()
		.eval(ContextVariable.REGIME.getName(), firstDayOfMonth, lastDayOfMonth)
		.forEach(r -> assertEquals(CCCType.ARTIST, r.getValue()));

		getContractSalaryCalculatorContext(connection, firstDayOfMonth, lastDayOfMonth, lastDayOfMonth, artists).getExpressionContext()
		.eval(ContextVariable.REGIME.getName() + " == " +ContextVariable.ARTISTS.getName() , firstDayOfMonth, lastDayOfMonth, Boolean.class)
		.forEach(r -> assertTrue(r.getValue()));

		getContractSalaryCalculatorContext(connection, firstDayOfMonth, lastDayOfMonth, lastDayOfMonth, artists).getExpressionContext()
		.eval("[AGRARIO:true,ARTISTAS:true,HOGAR:true]["+ ContextVariable.REGIME.getName() + "]" , firstDayOfMonth, lastDayOfMonth, Boolean.class)
		.forEach(r -> assertTrue(r.getValue()));
		
}
	

	// ------------------------------------------------------------------------

}
