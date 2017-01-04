package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.NON_STRUCTURAL_OVERTIME_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRUCTURAL_OVERTIME_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C100;
import static com.esferalia.aon.payroll.enumeration.LeaveType.MATERNITY;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.stream.Collectors;

import org.junit.Test;

import com.esferalia.aon.jooq.tables.records.BonusConceptRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseActivityRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseCccRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.ScopeRecord;
import com.esferalia.aon.jooq.tables.records.WorkplaceRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.BonusType;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;

import junit.framework.Assert;

public class SQLCretaTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.006;

	// -------------------------------------------------------------------------
	@Test
	public void testCretaStandardActiveFullTime()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
						put(TC2.getName(), String.format("\"%s\"", C100.getValue()));
					}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {						
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				"BASE_IRPF * PORCENTAJE_IRPF/100" 
				},
				null);
		//@formatter:on

		Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		int salaries = calculateAndSave(connection, ctx);

		// Only one salary saved to DB.
		Assert.assertEquals(1, salaries);

		AON.getSalaryData(aonContext,
				props -> props.getContractProperty().eq(contract.getId()))
				.forEach(salary -> {

					// 500 Base de contingencias comunes.
					List<ContextData> datas = salary.getContextData()
							.get(CGC_BASE.getName());
					Assert.assertEquals(1, datas.size());
					Assert.assertEquals(startDate, datas.get(0).getStartDate());
					Assert.assertEquals(endDate, datas.get(0).getEndDate());
					Assert.assertEquals(1750.00,
							Double.parseDouble(datas.get(0).getExpression()));

					// 501 Base de Horas Extras Fuerza Mayor
					datas = salary.getContextData()
							.get(STRUCTURAL_OVERTIME_BASE.getName());
					Assert.assertEquals(1, datas.size());
					Assert.assertEquals(startDate, datas.get(0).getStartDate());
					Assert.assertEquals(endDate, datas.get(0).getEndDate());
					Assert.assertEquals(0.00,
							Double.parseDouble(datas.get(0).getExpression()));

					// 502 Base de Horas Extras
					datas = salary.getContextData()
							.get(NON_STRUCTURAL_OVERTIME_BASE.getName());
					Assert.assertEquals(1, datas.size());
					Assert.assertEquals(startDate, datas.get(0).getStartDate());
					Assert.assertEquals(endDate, datas.get(0).getEndDate());
					Assert.assertEquals(0.00,
							Double.parseDouble(datas.get(0).getExpression()));

					// 601 o 611 Base de Accidentes de Trabajo.
					datas = salary.getContextData().get(CGP_BASE.getName());
					Assert.assertEquals(1, datas.size());
					Assert.assertEquals(startDate, datas.get(0).getStartDate());
					Assert.assertEquals(endDate, datas.get(0).getEndDate());
					Assert.assertEquals(1750.00,
							Double.parseDouble(datas.get(0).getExpression()));

				});
		;

	}

	// -------------------------------------------------------------------------
	@Test
	public void testCretaITMaternityFullTimeI()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemPayments(aonContext);
		addSSRegimePayment(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), PaymentType.CRA_0004,
				"TRACE('DIAS_COTIZADOS=%f\r\n',DIAS_COTIZADOS);0.00",
				"DIAS_COTIZADOS * BASE_REGULADORA", "0.00");
		//@formatter:off
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
						put(MONTH_DAYS.getName(), String.format("%f", 30.00));
						put(TC2.getName(), String.format("\"%s\"", C100.getValue()));
					}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {						
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				"BASE_IRPF * PORCENTAJE_IRPF/100" ,
				"TRACE('DIAS_TRABAJADOS=%f\r\n', DIAS_TRABAJADOS); 0.00;"
				},
				null);
		//@formatter:on

		Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
		Date endDate = getLastDayOfMonth(startDate);

		//@formatter:off
		addIT(aonContext, 
				contract, 
				MATERNITY, 
				getFirstDayOfYear(startDate), 
				null, 
				null/*1750.00/30*/);
		//@formatter:on

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		int salaries = calculateAndSave(connection, ctx);

		// Only one salary saved to DB.
		Assert.assertEquals(1, salaries);

		AON.getSalaryData(aonContext,
				props -> props.getContractProperty().eq(contract.getId()))
				.forEach(salary -> {

					int monthDays = AonDateUtils.getMax(startDate,
							DAY_OF_MONTH);

					// 500 Base de contingencias comunes.
					List<ContextData> datas = salary.getContextData()
							.get(CGC_BASE.getName());
					Assert.assertEquals(1, datas.size());

					Assert.assertEquals(startDate, datas.get(0).getStartDate());
					Assert.assertEquals(endDate, datas.get(0).getEndDate());
					Assert.assertEquals(1750.00,
							Double.parseDouble(datas.get(0).getExpression()),
							DELTA);

				});
		;

	}

	// -------------------------------------------------------------------------
	@Test
	public void testCretaITMaternityFullTimeII()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemPayments(aonContext);
		addSSRegimePayment(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), PaymentType.CRA_0004,
				"TRACE('DIAS_MATERNIDAD=%f\r\n',DIAS_MATERNIDAD);0.00",
				"DIAS_MATERNIDAD * BASE_REGULADORA", 
				"TRACE('BASE_REGULADORA=%f\r\n',BASE_REGULADORA);0.00");
		//@formatter:off
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
						put(TC2.getName(), String.format("\"%s\"", C100.getValue()));
					}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {						
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				"BASE_IRPF * PORCENTAJE_IRPF/100" ,
				"TRACE('DIAS_TRABAJADOS=%f\r\n', DIAS_TRABAJADOS); 0.00;"
				},
				null);
		//@formatter:on

		Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
		Date endDate = getLastDayOfMonth(startDate);

		int monthDays = AonDateUtils.getMax(startDate, DAY_OF_MONTH);

		double br = Math.round((1755.00 / monthDays * 1000.00)) / 1000.00; // DB only has three decimals
		
		//@formatter:off
		addIT(aonContext, 
				contract, 
				MATERNITY, 
				getFirstDayOfYear(startDate), 
				null, 
				br);
		//@formatter:on

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		int salaries = calculateAndSave(connection, ctx);

		// Only one salary saved to DB.
		Assert.assertEquals(1, salaries);

		AON.getSalaryData(aonContext,
				props -> props.getContractProperty().eq(contract.getId()))
				.forEach(salary -> {

					// 500 Base de contingencias comunes.
					List<ContextData> datas = salary.getContextData()
							.get(CGC_BASE.getName());
					Assert.assertEquals(1, datas.size());

					Assert.assertEquals(startDate, datas.get(0).getStartDate());
					Assert.assertEquals(endDate, datas.get(0).getEndDate());
					Assert.assertEquals(br * monthDays,
							Double.parseDouble(datas.get(0).getExpression()),
							DELTA);

				});
		;
		
	}

	// -------------------------------------------------------------------------
	@Test
	public void testCretaITMaternityFullTimeIII()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemPayments(aonContext);
		addSSRegimePayment(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), PaymentType.CRA_0004,
				"TRACE('DIAS_MATERNIDAD=%f\r\n',DIAS_MATERNIDAD);0.00",
				"DIAS_MATERNIDAD * BASE_REGULADORA", "0.00");
		//@formatter:off
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
						put(TC2.getName(), String.format("\"%s\"", C100.getValue()));
					}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				}, 
				new String[] {						
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				"BASE_IRPF * PORCENTAJE_IRPF/100" ,
				"TRACE('BASE_CGC=%f\r\n', BASE_CGC); 0.00;",
				"TRACE('DIAS_TRABAJADOS=%f\r\n', DIAS_TRABAJADOS); 0.00;"
				},
				null);
		//@formatter:on

		Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
		Date endDate = getLastDayOfMonth(startDate);
		Date startIt = add(startDate, DAY_OF_MONTH, 13);

		//@formatter:off
		addIT(aonContext, 
				contract, 
				MATERNITY, 
				startIt, 
				endDate, 
				null/*1750.00/30*/);
		//@formatter:on

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		int salaries = calculateAndSave(connection, ctx);

		// Only one salary saved to DB.
		Assert.assertEquals(1, salaries);

		AON.getSalaryData(aonContext,
				props -> props.getContractProperty().eq(contract.getId()))
				.forEach(salary -> {

					Date endActive = add(startIt, DAY_OF_MONTH, -1);
					int monthDays = AonDateUtils.getMax(startDate,
							DAY_OF_MONTH);

					// 500 Base de contingencias comunes.
					List<ContextData> datas = salary.getContextData()
							.get(CGC_BASE.getName());
					Assert.assertEquals(2, datas.size());

					Assert.assertEquals(startDate, datas.get(0).getStartDate());
					Assert.assertEquals(endActive, datas.get(0).getEndDate());
					Assert.assertEquals(1750.00 * 13 / monthDays,
							Double.parseDouble(datas.get(0).getExpression()),
							DELTA);

					Assert.assertEquals(startIt, datas.get(1).getStartDate());
					Assert.assertEquals(endDate, datas.get(1).getEndDate());
					Assert.assertEquals(1750.00 * (monthDays - 13) / monthDays,
							Double.parseDouble(datas.get(1).getExpression()),
							DELTA);

					// 601 o 611 Base de Accidentes de Trabajo.
					datas = salary.getContextData().get(CGP_BASE.getName());
					Assert.assertEquals(2, datas.size());

					Assert.assertEquals(startDate, datas.get(0).getStartDate());
					Assert.assertEquals(endActive, datas.get(0).getEndDate());
					Assert.assertEquals(1750.00 * 13 / monthDays,
							Double.parseDouble(datas.get(0).getExpression()),
							DELTA);

					Assert.assertEquals(startIt, datas.get(1).getStartDate());
					Assert.assertEquals(endDate, datas.get(1).getEndDate());
					Assert.assertEquals(1750.00 * (monthDays - 13) / monthDays,
							Double.parseDouble(datas.get(1).getExpression()),
							DELTA);

				});
		;

	}

	// -------------------------------------------------------------------------
	@Test
	public void testCustomPeriod() throws ExpressionException, SQLException,
	SalaryException{
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				getToday(), 
			new HashMap<String,String>(){
			{
				put(ContextVariable.TC2.getName(), "'100'");
			}
			},
			new String[] { 
					"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
					"500.00*DIAS_TRABAJADOS/DIAS_MES",
					}, 
			new String[] {
					"BASE_CGC * 0.10", 
					"BASE_CGP * 0.05",
					},
			null
		);
		//@formatter:on

		addSSRegimeData(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), null,
				new HashMap<String, String>() {
					{
						put("PORCENTAJE_CGC_E", "23.60");
					}
				});

		addSSRegimeCost(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), "CGC_E",
				DeductionType.COMMON_CONTINGENCY,
				"( BASE_CGC_E = BASE_CGC) * PORCENTAJE_CGC_E/100");

		//@formatter:off
		BonusConceptRecord concept = addBonusConcept(aonContext, 
				BonusType.SOCIAL_SECURITY,
				"/*read-only*/"+
				"FIN_BONIF=AÑO(INICIO_CONTRATO,2);"+
				"TRAMO(FIN_BONIF);"+
				"0.00"+
				"/**/");
		addBonus(aonContext, contract, concept, null);
		//@formatter:on
	
		// After two years.
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection,
						getFirstDayOfMonth(add(getToday(), YEAR, 2)),
						getLastDayOfMonth(add(getToday(), YEAR, 2)),
						getLastDayOfMonth(add(getToday(), YEAR, 2)), 
						contract);
		
		int salaries = calculateAndSave(connection, ctx);
		Assert.assertEquals(1, salaries);

		// Only one salary saved to DB.
		Map<String, List<ContextData>> datas =
		AON.getSalaryData(
				aonContext, 
				props -> props.getContractProperty().eq(contract.getId())
		)
		.findFirst()
		.map(salary-> salary.getContextData())
		.get()
		;
		
		List<ContextData> cgcBases = datas.get(ContextVariable.CGC_BASE.getName());
		Assert.assertEquals(2, cgcBases.size());
		
		Collections.sort(cgcBases, (d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate()));
		Assert.assertEquals(cgcBases.get(0).getStartDate(), getFirstDayOfMonth(add(getToday(), YEAR, 2)));
		Assert.assertEquals(cgcBases.get(0).getEndDate(), add(getToday(), YEAR, 2));
		Assert.assertEquals(cgcBases.get(1).getStartDate(), add(add(getToday(), YEAR, 2), DAY_OF_MONTH ,1));
		Assert.assertEquals(cgcBases.get(1).getEndDate(), getLastDayOfMonth(add(getToday(), YEAR, 2)));
		
		List<ContextData> cgpBases = datas.get(ContextVariable.CGP_BASE.getName());
		Assert.assertEquals(2, cgpBases.size());
		Collections.sort(cgpBases, (d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate()));
		Assert.assertEquals(cgpBases.get(0).getStartDate(), getFirstDayOfMonth(add(getToday(), YEAR, 2)));
		Assert.assertEquals(cgpBases.get(0).getEndDate(), add(getToday(), YEAR, 2));
		Assert.assertEquals(cgpBases.get(1).getStartDate(), add(add(getToday(), YEAR, 2), DAY_OF_MONTH ,1));
		Assert.assertEquals(cgpBases.get(1).getEndDate(), getLastDayOfMonth(add(getToday(), YEAR, 2)));
		
//		List<ContextData> structuralBases = datas.get(ContextVariable.STRUCTURAL_OVERTIME_BASE.getName());
//		Assert.assertEquals(1, structuralBases.size());
//
//		List<ContextData> nonStructuralBases = datas.get(ContextVariable.NON_STRUCTURAL_OVERTIME_BASE.getName());
//		Assert.assertEquals(1, nonStructuralBases.size());
		
	}
	
	@Test
	public void testCustomPeriodWithIT() throws ExpressionException, SQLException,
	SalaryException{
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfMonth(getToday()), 
			new HashMap<String,String>(){
			{
				put(ContextVariable.TC2.getName(), "'100'");
			}
			},
			new String[] { 
					"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
					"500.00 * DIAS_TRABAJADOS/DIAS_MES",
					}, 
			new String[] {
					"BASE_CGC * 0.10", 
					"BASE_CGP * 0.05",
					},
			null
		);
		//@formatter:on

		addSSRegimeData(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), null,
				new HashMap<String, String>() {
					{
						put("PORCENTAJE_CGC_E", "23.60");
					}
				});

		addSSRegimeCost(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), "CGC_E",
				DeductionType.COMMON_CONTINGENCY,
				"( BASE_CGC_E = BASE_CGC) * PORCENTAJE_CGC_E/100");

		//@formatter:off
		BonusConceptRecord concept = addBonusConcept(aonContext, 
				BonusType.SOCIAL_SECURITY,
				"/*read-only*/"+
				"FIN_BONIF=DIA(AÑO(INICIO_CONTRATO,2),9);"+
				"TRAMO(FIN_BONIF);"+
				"0.00"+
				"/**/");
		addBonus(aonContext, contract, concept, null);
		//@formatter:on
		
		addIT(aonContext, contract, 
				LeaveType.COMMON_DISEASE, 
				getFirstDayOfMonth(add(getToday(), YEAR, 2)), 
				add(getFirstDayOfMonth(add(getToday(), YEAR, 2)), DAY_OF_MONTH, 14 ), 
				100.00);
	
		//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_1_3",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.60 * %s_4_15",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s_16_20",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s_21",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		//@formatter:on

		// After two years.
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection,
						getFirstDayOfMonth(add(getToday(), YEAR, 2)),
						getLastDayOfMonth(add(getToday(), YEAR, 2)),
						getLastDayOfMonth(add(getToday(), YEAR, 2)), 
						contract);
		
		int salaries = calculateAndSave(connection, ctx);
		Assert.assertEquals(1, salaries);

		// Only one salary saved to DB.
		Map<String, List<ContextData>> datas =
		AON.getSalaryData(
				aonContext, 
				props -> props.getContractProperty().eq(contract.getId())
		)
		.findFirst()
		.map(salary-> salary.getContextData())
		.get()
		;
		
		Date startDate = getFirstDayOfMonth(add(getToday(), YEAR, 2));
		Date sectionDate = add(startDate, DAY_OF_MONTH,9);
		Date next2SectionDate = add(sectionDate, DAY_OF_MONTH,1);
		Date itEndDate = add(startDate, DAY_OF_MONTH,14);
		Date workStartDate = add(itEndDate, DAY_OF_MONTH,1);
		Date endDate = getLastDayOfMonth(startDate);
		
		int monthDays = AonDateUtils.get(endDate, Calendar.DAY_OF_MONTH);
		
		
		List<ContextData> cgcBases = datas.get(ContextVariable.CGC_BASE.getName());
		Assert.assertEquals(4, cgcBases.size());
		Collections.sort(cgcBases, (d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate()));
		Assert.assertEquals(cgcBases.get(0).getStartDate(), startDate);
		Assert.assertEquals(cgcBases.get(1).getEndDate(), sectionDate);
		Assert.assertEquals(cgcBases.get(2).getStartDate(), next2SectionDate);
		Assert.assertEquals(cgcBases.get(2).getEndDate(), itEndDate);
		Assert.assertEquals(cgcBases.get(3).getStartDate(), workStartDate);
		Assert.assertEquals(cgcBases.get(3).getEndDate(), endDate);
		
		Assert.assertEquals(100.00 * 3, Double.parseDouble(cgcBases.get(0).getExpression()), DELTA);
		Assert.assertEquals(100.00 * 7, Double.parseDouble(cgcBases.get(1).getExpression()), DELTA);
		Assert.assertEquals(100.00 * 5 , Double.parseDouble(cgcBases.get(2).getExpression()), DELTA);
		Assert.assertEquals(1500.00 * (monthDays -15) / monthDays, Double.parseDouble(cgcBases.get(3).getExpression()), DELTA);
		
		List<ContextData> cgpBases = datas.get(ContextVariable.CGP_BASE.getName());
		Assert.assertEquals(4, cgpBases.size());
		Collections.sort(cgpBases, (d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate()));
		Assert.assertEquals(cgpBases.get(0).getStartDate(), startDate);
		Assert.assertEquals(cgpBases.get(1).getEndDate(), sectionDate);
		Assert.assertEquals(cgpBases.get(2).getStartDate(), next2SectionDate);
		Assert.assertEquals(cgpBases.get(2).getEndDate(), itEndDate);
		Assert.assertEquals(cgpBases.get(3).getStartDate(), workStartDate);
		Assert.assertEquals(cgpBases.get(3).getEndDate(), endDate);

		Assert.assertEquals(100.00 * 3, Double.parseDouble(cgpBases.get(0).getExpression()), DELTA);
		Assert.assertEquals(100.00 * 7, Double.parseDouble(cgpBases.get(1).getExpression()), DELTA);
		Assert.assertEquals(100.00 * 5 , Double.parseDouble(cgpBases.get(2).getExpression()), DELTA);
		Assert.assertEquals(1500.00 * (monthDays -15) / monthDays, Double.parseDouble(cgpBases.get(3).getExpression()), DELTA);

		List<ContextData> prestIts = datas.get(ContextVariable.PREST_IT);
		Assert.assertEquals(4, prestIts.size());
		Collections.sort(prestIts, (d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate()));
		Assert.assertEquals(prestIts.get(0).getStartDate(), startDate);
		Assert.assertEquals(prestIts.get(1).getEndDate(), sectionDate);
		Assert.assertEquals(prestIts.get(2).getStartDate(), next2SectionDate);
		Assert.assertEquals(prestIts.get(2).getEndDate(), itEndDate);
		Assert.assertEquals(prestIts.get(3).getStartDate(), workStartDate);
		Assert.assertEquals(prestIts.get(3).getEndDate(), endDate);

		Assert.assertEquals(0.00, Double.parseDouble(prestIts.get(0).getExpression()), DELTA);
		Assert.assertEquals(100.00 * 7 * 0.60, Double.parseDouble(prestIts.get(1).getExpression()), DELTA);
		Assert.assertEquals(100.00 * 5 * 0.60, Double.parseDouble(prestIts.get(2).getExpression()), DELTA);
		Assert.assertEquals(0.00, Double.parseDouble(prestIts.get(3).getExpression()), DELTA);
		
//		List<ContextData> structuralBases = datas.get(ContextVariable.STRUCTURAL_OVERTIME_BASE.getName());
//		Assert.assertEquals(1, structuralBases.size());
//		Assert.assertEquals(startDate, structuralBases.get(0).getStartDate());
//		Assert.assertEquals(endDate, structuralBases.get(0).getEndDate());
//
//		List<ContextData> nonStructuralBases = datas.get(ContextVariable.NON_STRUCTURAL_OVERTIME_BASE.getName());
//		Assert.assertEquals(1, nonStructuralBases.size());
//		Assert.assertEquals(startDate, nonStructuralBases.get(0).getStartDate());
//		Assert.assertEquals(endDate, nonStructuralBases.get(0).getEndDate());
		
	}

	@Test
	public void testCustomPeriodHextraBaseIT() throws ExpressionException, SQLException,
	SalaryException{
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()), 
			new HashMap<String,String>(){
			{
				put(ContextVariable.TC2.getName(), "'100'");
			}
			},
			new String[] { 
					"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
					"500.00 * DIAS_TRABAJADOS/DIAS_MES",
					}, 
			new String[] {
					"BASE_CGC * 0.10", 
					"BASE_CGP * 0.05",
					},
			null
		);
		//@formatter:on
		
		addIT(aonContext, contract, 
				LeaveType.COMMON_DISEASE, 
				getFirstDayOfMonth(getToday()), 
				add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH, 14 ), 
				100.00);
	
		//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_1_3",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.60 * %s_4_15",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s_16_20",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s_21",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		//@formatter:on
		
		//@formatter:off
		BonusConceptRecord concept = addBonusConcept(aonContext, 
				BonusType.SOCIAL_SECURITY,
				"/*read-only*/"+
				"FIN_BONIF=DIA(INICIO_NOMINA,9);"+
				"TRAMO(FIN_BONIF);"+
				"0.00"+
				"/**/");
		addBonus(aonContext, contract, concept, null);
		//@formatter:on

		addPayment(aonContext, contract, "", "100.00", "_P", "_P", PaymentType.CRA_0002);
		
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection,
						getFirstDayOfMonth(getToday()),
						getLastDayOfMonth(getToday()),
						getLastDayOfMonth(getToday()), 
						contract);

		int salaries = calculateAndSave(connection, ctx);
		Assert.assertEquals(1, salaries);

		// Only one salary saved to DB.
		Map<String, List<ContextData>> datas =
		AON.getSalaryData(
				aonContext, 
				props -> props.getContractProperty().eq(contract.getId())
		)
		.findFirst()
		.map(salary-> salary.getContextData())
		.get()
		;
		Date startDate = getFirstDayOfMonth(getToday());
		Date sectionDate = add(startDate, DAY_OF_MONTH,9);
		Date next2SectionDate = add(sectionDate, DAY_OF_MONTH,1);
		Date itEndDate = add(startDate, DAY_OF_MONTH,14);
		Date workStartDate = add(itEndDate, DAY_OF_MONTH,1);
		Date endDate = getLastDayOfMonth(startDate);
		
		int monthDays = AonDateUtils.get(endDate, Calendar.DAY_OF_MONTH);
		
		
		List<ContextData> cgcBases = datas.get(ContextVariable.CGC_BASE.getName());
		Assert.assertEquals(4, cgcBases.size());
		Collections.sort(cgcBases, (d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate()));
		Assert.assertEquals(cgcBases.get(0).getStartDate(), startDate);
		Assert.assertEquals(cgcBases.get(1).getEndDate(), sectionDate);
		Assert.assertEquals(cgcBases.get(2).getStartDate(), next2SectionDate);
		Assert.assertEquals(cgcBases.get(2).getEndDate(), itEndDate);
		Assert.assertEquals(cgcBases.get(3).getStartDate(), workStartDate);
		Assert.assertEquals(cgcBases.get(3).getEndDate(), endDate);
		
		Assert.assertEquals(100.00 * 3, Double.parseDouble(cgcBases.get(0).getExpression()), DELTA);
		Assert.assertEquals(100.00 * 7, Double.parseDouble(cgcBases.get(1).getExpression()), DELTA);
		Assert.assertEquals(100.00 * 5 , Double.parseDouble(cgcBases.get(2).getExpression()), DELTA);
		
		Assert.assertEquals(1500.00 * (monthDays -15) / monthDays, Double.parseDouble(cgcBases.get(3).getExpression()), DELTA);
		
		List<ContextData> cgpBases = datas.get(ContextVariable.CGP_BASE.getName());
		Assert.assertEquals(4, cgpBases.size());
		Collections.sort(cgpBases, (d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate()));
		Assert.assertEquals(cgpBases.get(0).getStartDate(), startDate);
		Assert.assertEquals(cgpBases.get(1).getEndDate(), sectionDate);
		Assert.assertEquals(cgpBases.get(2).getStartDate(), next2SectionDate);
		Assert.assertEquals(cgpBases.get(2).getEndDate(), itEndDate);
		Assert.assertEquals(cgpBases.get(3).getStartDate(), workStartDate);
		Assert.assertEquals(cgpBases.get(3).getEndDate(), endDate);

		Assert.assertEquals(100.00 * 3, Double.parseDouble(cgpBases.get(0).getExpression()), DELTA);
		Assert.assertEquals(100.00 * 7, Double.parseDouble(cgpBases.get(1).getExpression()), DELTA);
		Assert.assertEquals(100.00 * 5 , Double.parseDouble(cgpBases.get(2).getExpression()), DELTA);
		
		Assert.assertEquals(100.00 +(1500.00 * (monthDays -15) / monthDays), Double.parseDouble(cgpBases.get(3).getExpression()), DELTA);

		List<ContextData> prestIts = datas.get(ContextVariable.PREST_IT);
		Assert.assertEquals(4, prestIts.size());
		Collections.sort(prestIts, (d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate()));
		Assert.assertEquals(prestIts.get(0).getStartDate(), startDate);
		Assert.assertEquals(prestIts.get(1).getEndDate(), sectionDate);
		Assert.assertEquals(prestIts.get(2).getStartDate(), next2SectionDate);
		Assert.assertEquals(prestIts.get(2).getEndDate(), itEndDate);
		Assert.assertEquals(prestIts.get(3).getStartDate(), workStartDate);
		Assert.assertEquals(prestIts.get(3).getEndDate(), endDate);

		List<ContextData> structuralBases = datas.get(ContextVariable.STRUCTURAL_OVERTIME_BASE.getName());
		Assert.assertEquals(1, structuralBases.size());
		Assert.assertEquals(structuralBases.get(0).getStartDate(), startDate);
		Assert.assertEquals(structuralBases.get(0).getEndDate(), endDate);
		Assert.assertEquals(0.00 , Double.parseDouble(structuralBases.get(0).getExpression()), DELTA);

		List<ContextData> nonStructuralBases = datas.get(ContextVariable.NON_STRUCTURAL_OVERTIME_BASE.getName());
		Assert.assertEquals(1, nonStructuralBases.size());
		Assert.assertEquals(nonStructuralBases.get(0).getStartDate(), workStartDate);
		Assert.assertEquals(nonStructuralBases.get(0).getEndDate(), endDate);
		Assert.assertEquals(100.00 , Double.parseDouble(nonStructuralBases.get(0).getExpression()), DELTA);
	}

	@Test
	public void test31QuoteIT() throws ExpressionException, SQLException,
	SalaryException{
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);
		
		Date startDate = getFirstDayOfYear(getToday());
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()), 
			new HashMap<String,String>(){
			{
				put(ContextVariable.TC2.getName(), "'100'");
				put(ContextVariable.MONTH_DAYS.getName(), "30");
			}
			},
			new String[] { 
					"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
					"500.00 * DIAS_TRABAJADOS/DIAS_MES",
					}, 
			new String[] {
					"BASE_CGC * 0.10", 
					"BASE_CGP * 0.05",
					},
			null
		);
		//@formatter:on

		
		Date itDate = startDate;

		addIT(aonContext, contract, 
				LeaveType.COMMON_DISEASE, 
				itDate, 
				itDate, 
				100.00);
	
		//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_1_3",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.60 * %s_4_15",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s_16_20",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s_21",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		//@formatter:on

		// After two years.
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection,
						startDate,
						getLastDayOfMonth(startDate),
						getLastDayOfMonth(startDate), 
						contract);
		
		int salaries = calculateAndSave(connection, ctx);
		Assert.assertEquals(1, salaries);

		// Only one salary saved to DB.
		Map<String, List<ContextData>> datas =
		AON.getSalaryData(
				aonContext, 
				props -> props.getContractProperty().eq(contract.getId())
		)
		.findFirst()
		.map(salary-> salary.getContextData())
		.get()
		;
		
		Date workStartDate = add(itDate, DAY_OF_MONTH,1);
		Date endDate = getLastDayOfMonth(startDate);
		
		
		List<ContextData> cgcBases = datas.get(ContextVariable.CGC_BASE.getName());

		Assert.assertEquals(2, cgcBases.size());
		Collections.sort(cgcBases, (d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate()));
		Assert.assertEquals(cgcBases.get(0).getStartDate(), startDate);
		Assert.assertEquals(cgcBases.get(0).getEndDate(), itDate);
		Assert.assertEquals(cgcBases.get(1).getStartDate(), workStartDate);
		Assert.assertEquals(cgcBases.get(1).getEndDate(), endDate);
		
		Assert.assertEquals(0.00, Double.parseDouble(cgcBases.get(0).getExpression()));
		Assert.assertEquals(1500.00, Double.parseDouble(cgcBases.get(1).getExpression()), DELTA);
		

		List<ContextData> cgpBases = datas.get(ContextVariable.CGP_BASE.getName());

		Assert.assertEquals(2, cgpBases.size());
		Collections.sort(cgpBases, (d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate()));
		Assert.assertEquals(cgpBases.get(0).getStartDate(), startDate);
		Assert.assertEquals(cgpBases.get(0).getEndDate(), itDate);
		Assert.assertEquals(cgpBases.get(1).getStartDate(), workStartDate);
		Assert.assertEquals(cgpBases.get(1).getEndDate(), endDate);
		
		Assert.assertEquals(0.00, Double.parseDouble(cgpBases.get(0).getExpression()));
		Assert.assertEquals(1500.00, Double.parseDouble(cgpBases.get(1).getExpression()), DELTA);
	}

	// -------------------------------------------------------------------------
	@Test
	public void testCretaChangeTime()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
						put(TC2.getName(), String.format("\"%s\"", C100.getValue()));
					}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {						
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				"BASE_IRPF * PORCENTAJE_IRPF/100" 
				},
				null);
		//@formatter:on

		Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
		Date endDate = getLastDayOfMonth(startDate);

		addData(aonContext, contract, add(startDate, DAY_OF_MONTH, 10), null,
				new HashMap<String, String>() {
					{
						put(ContextVariable.OCCUPATION.getName(), "\"g\"");
					}
				});

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		int salaries = calculateAndSave(connection, ctx);

		// Only one salary saved to DB.
		Assert.assertEquals(1, salaries);

		AON.getSalaryData(aonContext,
				props -> props.getContractProperty().eq(contract.getId()))
				.forEach(salary -> {
					Date noOcupationEnd = add(startDate, DAY_OF_MONTH, 9);
					Date ocupationStart = add(startDate, DAY_OF_MONTH, 10);

					int monthDays = AonDateUtils.get(endDate, DAY_OF_MONTH);

					// 500 Base de contingencias comunes.
					List<ContextData> datas = salary.getContextData()
							.get(CGC_BASE.getName());
					Assert.assertEquals(2, datas.size());

					Assert.assertEquals(startDate, datas.get(0).getStartDate());
					Assert.assertEquals(noOcupationEnd,
							datas.get(0).getEndDate());
					Assert.assertEquals(1750.00 * 10 / monthDays,
							Double.parseDouble(datas.get(0).getExpression()), DELTA);

					Assert.assertEquals(ocupationStart,
							datas.get(1).getStartDate());
					Assert.assertEquals(endDate, datas.get(1).getEndDate());
					Assert.assertEquals(1750.00 * (monthDays - 10) / monthDays,
							Double.parseDouble(datas.get(1).getExpression()), DELTA);

					// 601 o 611 Base de Accidentes de Trabajo.
					datas = salary.getContextData().get(CGP_BASE.getName());
					Assert.assertEquals(2, datas.size());
					Assert.assertEquals(startDate, datas.get(0).getStartDate());
					Assert.assertEquals(noOcupationEnd,
							datas.get(0).getEndDate());
					Assert.assertEquals(1750.00 * 10 / monthDays,
							Double.parseDouble(datas.get(0).getExpression()), DELTA);

					Assert.assertEquals(ocupationStart,
							datas.get(1).getStartDate());
					Assert.assertEquals(endDate, datas.get(1).getEndDate());
					Assert.assertEquals(1750.00 * (monthDays - 10) / monthDays,
							Double.parseDouble(datas.get(1).getExpression()), DELTA);

				});
		;
	}
	
	@Test
	public void testSalaryDAOI()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
		
		DomainRecord domain = newDomain(aonContext);
		
		ScopeRecord scope = newScope(aonContext, domain.getId());

		EnterpriseActivityRecord enterpriseActivity = newEnterpriseActivity(
				aonContext, 
				domain.getId(), 
				scope.getId(), 
				SSRegimeType.GENERAL);

		EnterpriseCccRecord enterpriseCcc = newEnterpriseCcc(aonContext, 
				domain.getId(), 
				scope.getId(), 
				enterpriseActivity.getId(), 
				CCCType.PRINCIPAL,
				ccc );

		WorkplaceRecord workplace = newWorkplace(aonContext, 
				domain.getId(), 
				scope.getId(), 
				enterpriseActivity.getEnterprise());

		RegistryRecord person = newPerson(
				aonContext, 
				domain.getId(),
				"00000000A");

		String deductions  [] = new String[] {						
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				"BASE_IRPF * PORCENTAJE_IRPF/100" 
				};
		
		Map<String, String> data = 
		new HashMap<String, String>() {
			{
				put(TC2.getName(), String.format("\"%s\"", C100.getValue()));
			}
		};
		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = AonDateUtils.add(startDate, DAY_OF_MONTH,1);
		
		List<ContractRecord> contracts = new ArrayList<ContractRecord>();
		for ( int i = 0; i < 10 ; i++ ) {
			//@formatter:off
			contracts.add(newContract(aonContext, 
					SSRegimeType.GENERAL, 
					CCCType.PRINCIPAL, 
					startDate, 
					endDate, 
					data, 
					new String[] {
					"1000.00 * DIAS_TRABAJADOS / DIAS_MES"
					}, 
					deductions,
					null, 
					domain.getId(), 
					person.getId(), 
					workplace.getId(), 
					enterpriseCcc.getId(), 
					enterpriseActivity.getId()));
			//@formatter:on

			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = AonDateUtils.add(startDate, DAY_OF_MONTH,2);
		}
		
		
		for( int i = 1; i < 10 ; i++) {
			person = newPerson(
					aonContext, 
					domain.getId(),
					String.format("%sA", new String(new char[8]).replace("\0", Integer.toString(i))));
			contracts.add(newContract(aonContext, 
					SSRegimeType.GENERAL, 
					CCCType.PRINCIPAL, 
					getFirstDayOfYear(getToday()), 
					null, 
					data, 
					new String[] {
					String.format("%f * DIAS_TRABAJADOS / DIAS_MES", 1000.00 * i)
					}, 
					deductions,
					null, 
					domain.getId(), 
					person.getId(), 
					workplace.getId(), 
					enterpriseCcc.getId(), 
					enterpriseActivity.getId()));
		}

		
		startDate = getFirstDayOfMonth(getToday());
		endDate = getLastDayOfMonth(startDate);
		
		

		for ( ContractRecord contract: contracts ){
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			calculateAndSave(connection, ctx );
		}
		
		
		Map<String, Long> employeDocuments = new HashMap<String, Long>(); 
		//@formatter:off
		AON.getSalaryData(aonContext, 
				p-> p.getCCCProperty().eq(ccc)
		)
		.forEach(salary-> { 
			System.out.println(salary.getEmployeeDocument() );
			for ( ContextData ctxData : salary.getContextData().get(ContextVariable.CGC_BASE.getName()) )
				System.out.println(ContextVariable.CGC_BASE.getName() + " = " + ctxData.getExpression() + "[" + ctxData.getStartDate() + ".." + ctxData.getEndDate() + "]" );
			employeDocuments.put(salary.getEmployeeDocument(),salary.getContextData(ContextVariable.CGC_BASE.getName(), Collectors.counting()));
			
		})
		;
		//@formatter:on
		
		for ( String employeeDocument: employeDocuments.keySet())
			System.out.println(employeeDocument);
		
		Assert.assertEquals(10, employeDocuments.size());

		Assert.assertEquals((Long)10L, employeDocuments.get("00000000A"));

		for( int i = 1; i < 10 ; i++) {
			Assert.assertEquals((Long)1L, employeDocuments.get(String.format("%sA", new String(new char[8]).replace("\0", Integer.toString(i)))));
		}
	}
	// -------------------------------------------------------------------------

	private static int calculateAndSave(Connection connection,
			ISQLContractSalaryCalculatorContext ctx) throws SalaryException {
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
		JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder(connection);
		new ContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		return jooqSalaryBuilder.execute();
	}

}
