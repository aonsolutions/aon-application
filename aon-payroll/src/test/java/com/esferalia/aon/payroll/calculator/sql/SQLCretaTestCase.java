package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS;
import static com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SalaryEmbargo.SALARY_EMBARGO;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.NON_STRUCTURAL_OVERTIME_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.OCCUPATIONAL_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PATERNITY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_GROUP;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRIKE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRIKE_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRUCTURAL_OVERTIME_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C100;
import static com.esferalia.aon.payroll.enumeration.LeaveType.MATERNITY;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

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
import com.esferalia.aon.payroll.calculator.CollectSalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.tgss.creta.Bases;
import com.esferalia.aon.payroll.tgss.creta.Bases.EmptyBasesException;
import com.esferalia.aon.payroll.tgss.creta.TrabajadoresTramos;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.BonusType;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.mchange.util.AssertException;

import junit.framework.Assert;
import net.aonsolutions.core.tgss.creta.jaxb.Utils;
import net.aonsolutions.core.tgss.creta.jaxb.bases.Dato;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.DatoSolicitado;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Tramo;

public class SQLCretaTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.006;
	
	private static class Sucessfull extends RuntimeException {
		
	}

	// -------------------------------------------------------------------------
	@Test
	public void testCretaStandardActiveFullTime()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSalaries(aonContext);

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

		cleanSalaries(aonContext);
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

		cleanSalaries(aonContext);
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

		cleanSalaries(aonContext);
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
					Collections.sort(datas, (d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate()) );

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
					Collections.sort(datas, (d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate()) );

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

		cleanSalaries(aonContext);
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

		cleanSalaries(aonContext);
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

		cleanSalaries(aonContext);
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

		cleanSalaries(aonContext);
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

		cleanSalaries(aonContext);

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
					Collections.sort(datas, (d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate()) );
					
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
					Collections.sort(datas, (d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate()) );
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
		cleanSalaries(aonContext);

		
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
	@Test
	public void testCretaTrabajadoresYTramosNormal()
			throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSalaries(aonContext);
		cleanSystemPayments(aonContext);

		String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, ccc, ContractCode.C100, "03");
		

		Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
		Date endDate = getLastDayOfMonth(startDate);
		
		List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);
		
		Assert.assertEquals(1, tramos.size());
		
		Tramo tramo = tramos.get(0); 
		Assert.assertEquals("01", tramo.getFechaDesde().getDia());
		Assert.assertEquals(Integer.toString(get(endDate, DAY_OF_MONTH)), tramo.getFechaHasta().getDia());
		assertTramoActivoNormalTiempoCompleto(tramo);

	}

	@Test
	public void testCretaTrabajadoresYTramosGrupoDiario()
			throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSalaries(aonContext);
		cleanSystemPayments(aonContext);

		String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, ccc, ContractCode.C100, "10");
		

		Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
		Date endDate = getLastDayOfMonth(startDate);
		
		List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);
		
		Assert.assertEquals(1, tramos.size());
		
		Tramo tramo = tramos.get(0); 
		Assert.assertEquals("01", tramo.getFechaDesde().getDia());
		Assert.assertEquals(Integer.toString(get(endDate, DAY_OF_MONTH)), tramo.getFechaHasta().getDia());
		assertTramoActivoNormalTiempoCompletoDiario(tramo);

	}

	@Test
	public void testCretaTrabajadoresYTramosTiempoCompletoNo()
			throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSalaries(aonContext);
		cleanSystemPayments(aonContext);

		String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, ccc, ContractCode.C100);
		
		addData(aonContext, 
				contract, 
				contract.getStartDate(), 
				contract.getEndDate(), 
				ContextVariable.FULL_TIME,
				"FALSO()");

		Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
		Date endDate = getLastDayOfMonth(startDate);
		
		List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);
		
		Assert.assertEquals(1, tramos.size());
		
		Tramo tramo = tramos.get(0); 
		Assert.assertEquals("01", tramo.getFechaDesde().getDia());
		Assert.assertEquals(Integer.toString(get(endDate, DAY_OF_MONTH)), tramo.getFechaHasta().getDia());
		assertTramoActivoNormalTiempoParcial(tramo);

	}

	@Test
	public void testCretaTrabajadoresYTramosTiempoParcial()
			throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSalaries(aonContext);
		cleanSystemPayments(aonContext);

		String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, ccc, ContractCode.C200);

		Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
		Date endDate = getLastDayOfMonth(startDate);
		
		List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);
		
		Assert.assertEquals(1, tramos.size());
		
		Tramo tramo = tramos.get(0); 
		Assert.assertEquals("01", tramo.getFechaDesde().getDia());
		Assert.assertEquals(Integer.toString(get(endDate, DAY_OF_MONTH)), tramo.getFechaHasta().getDia());
		assertTramoActivoNormalTiempoParcial(tramo);

	}

	@Test
	public void testCretaTrabajadoresYTramosIT15Days()
			throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSalaries(aonContext);
		cleanSystemPayments(aonContext);


		String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, ccc);
		

		Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
		Date endDate = getLastDayOfMonth(startDate);
		
		Date startIT = add(startDate, DAY_OF_MONTH, 10);
		Date endIT = add(startIT, DAY_OF_MONTH, 14);
		
		//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIT, 
				endIT, 
				null/*1750.00/30*/);
		//@formatter:on

		List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);
		
		
		Assert.assertEquals(3, tramos.size());
		
		//Activo
		Tramo tramo0 = tramos.get(0); 
		Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
		Assert.assertEquals("10", tramo0.getFechaHasta().getDia());
		assertTramoActivoNormalTiempoCompleto(tramo0);
		
		Tramo tramo1 = tramos.get(1); 
		Assert.assertEquals("11", tramo1.getFechaDesde().getDia());
		Assert.assertEquals("25", tramo1.getFechaHasta().getDia());
		assertTramoIT15PrimerosDias(tramo1);
		
		Tramo tramo2 = tramos.get(2); 
		Assert.assertEquals("26", tramo2.getFechaDesde().getDia());
		Assert.assertEquals(Integer.toString(endDate.getDate()), tramo2.getFechaHasta().getDia());
		assertTramoActivoNormalTiempoCompleto(tramo2);

		

	}

	@Test
	public void testCretaTrabajadoresYTramosDosIT15Days()
			throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSalaries(aonContext);
		cleanSystemPayments(aonContext);


		String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, ccc);
		

		Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
		Date endDate = getLastDayOfMonth(startDate);
		
		Date startIT = add(startDate, DAY_OF_MONTH, 10);
		Date endIT = add(startIT, DAY_OF_MONTH, 4);
		
		//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIT, 
				endIT, 
				null/*1750.00/30*/);
		//@formatter:on

		startIT = add(endIT, DAY_OF_MONTH, 1);
		endIT = add(startIT, DAY_OF_MONTH, 4);
		
		//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIT, 
				endIT, 
				null/*1750.00/30*/);
		//@formatter:on

		List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);
		
		
		Assert.assertEquals(3, tramos.size());
		
		//Activo
		Tramo tramo0 = tramos.get(0); 
		Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
		Assert.assertEquals("10", tramo0.getFechaHasta().getDia());
		assertTramoActivoNormalTiempoCompleto(tramo0);
		
		Tramo tramo1 = tramos.get(1); 
		Assert.assertEquals("11", tramo1.getFechaDesde().getDia());
		Assert.assertEquals("20", tramo1.getFechaHasta().getDia());
		assertTramoIT15PrimerosDias(tramo1);
		
		Tramo tramo2 = tramos.get(2); 
		Assert.assertEquals("21", tramo2.getFechaDesde().getDia());
		Assert.assertEquals(Integer.toString(endDate.getDate()), tramo2.getFechaHasta().getDia());
		assertTramoActivoNormalTiempoCompleto(tramo2);

		

	}

	@Test
	public void testCretaTrabajadoresYTramosIT4Days()
			throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSalaries(aonContext);
		cleanSystemPayments(aonContext);


		String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, ccc);
		

		Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
		Date endDate = getLastDayOfMonth(startDate);
		
		Date startIT = add(startDate, DAY_OF_MONTH, 10);
		Date endIT = add(startIT, DAY_OF_MONTH, 3);
		
		//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIT, 
				endIT, 
				null/*1750.00/30*/);
		//@formatter:on

		List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);
		
		
		Assert.assertEquals(3, tramos.size());
		
		//Activo
		Tramo tramo0 = tramos.get(0); 
		Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
		Assert.assertEquals("10", tramo0.getFechaHasta().getDia());
		assertTramoActivoNormalTiempoCompleto(tramo0);
		
		Tramo tramo1 = tramos.get(1); 
		Assert.assertEquals("11", tramo1.getFechaDesde().getDia());
		Assert.assertEquals("14", tramo1.getFechaHasta().getDia());
		assertTramoIT15PrimerosDias(tramo1);
		
		Tramo tramo2 = tramos.get(2); 
		Assert.assertEquals("15", tramo2.getFechaDesde().getDia());
		Assert.assertEquals(Integer.toString(endDate.getDate()), tramo2.getFechaHasta().getDia());
		assertTramoActivoNormalTiempoCompleto(tramo2);

		

	}

	@Test
	public void testCretaTrabajadoresYTramosIT25Days()
			throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSalaries(aonContext);
		cleanSystemPayments(aonContext);


		String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, ccc);
		

		Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
		Date endDate = getLastDayOfMonth(startDate);
		
		Date startIT = add(startDate, DAY_OF_MONTH, 2);
		Date endIT = add(startIT, DAY_OF_MONTH, 24);
		
		//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIT, 
				endIT, 
				null/*1750.00/30*/);
		//@formatter:on

		List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);
		
		
		Assert.assertEquals(4, tramos.size());
		
		//Activo
		Tramo tramo0 = tramos.get(0); 
		Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
		Assert.assertEquals("02", tramo0.getFechaHasta().getDia());
		assertTramoActivoNormalTiempoCompleto(tramo0);
		
		Tramo tramo1 = tramos.get(1); 
		Assert.assertEquals("03", tramo1.getFechaDesde().getDia());
		Assert.assertEquals("17", tramo1.getFechaHasta().getDia());
		assertTramoIT15PrimerosDias(tramo1);
		
		Tramo tramo2 = tramos.get(2); 
		Assert.assertEquals("18", tramo2.getFechaDesde().getDia());
		Assert.assertEquals("27", tramo2.getFechaHasta().getDia());
		assertTramoITPagoDelegado(tramo2);

		Tramo tramo3 = tramos.get(3); 
		Assert.assertEquals("28", tramo3.getFechaDesde().getDia());
		Assert.assertEquals(Integer.toString(endDate.getDate()), tramo3.getFechaHasta().getDia());
		assertTramoActivoNormalTiempoCompleto(tramo3);
		

	}

	@Test
	public void testCretaTrabajadoresYTramosIT99Days()
			throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSalaries(aonContext);
		cleanSystemPayments(aonContext);


		String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, ccc);
		

		Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
		Date endDate = getLastDayOfMonth(startDate);
		
		Date startIT = add(startDate, DAY_OF_MONTH, 5);
		Date endIT = null; //add(startIT, DAY_OF_MONTH, 24);
		
		//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIT, 
				endIT, 
				null/*1750.00/30*/);
		//@formatter:on

		List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);
		
		
		Assert.assertEquals(3, tramos.size());
		
		//Activo
		Tramo tramo0 = tramos.get(0); 
		Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
		Assert.assertEquals("05", tramo0.getFechaHasta().getDia());
		assertTramoActivoNormalTiempoCompleto(tramo0);
		
		Tramo tramo1 = tramos.get(1); 
		Assert.assertEquals("06", tramo1.getFechaDesde().getDia());
		Assert.assertEquals("20", tramo1.getFechaHasta().getDia());
		assertTramoIT15PrimerosDias(tramo1);
		
		Tramo tramo2 = tramos.get(2); 
		Assert.assertEquals("21", tramo2.getFechaDesde().getDia());
		Assert.assertEquals(Integer.toString(endDate.getDate()), tramo2.getFechaHasta().getDia());
		assertTramoITPagoDelegado(tramo2);

		

	}
	
	@Test
	public void testCretaTrabajadoresYTramosIT3Days()
			throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSalaries(aonContext);
		cleanSystemPayments(aonContext);


		String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, ccc);
		

		Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
		Date endDate = getLastDayOfMonth(startDate);
		
		Date startIT = add(startDate, DAY_OF_MONTH, 10);
		Date endIT = add(startIT, DAY_OF_MONTH, 2);
		
		//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIT, 
				endIT, 
				null/*1750.00/30*/);
		//@formatter:on

		List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);
		
		
		Assert.assertEquals(3, tramos.size());
		
		//Activo
		Tramo tramo0 = tramos.get(0); 
		Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
		Assert.assertEquals("10", tramo0.getFechaHasta().getDia());
		assertTramoActivoNormalTiempoCompleto(tramo0);
		
		Tramo tramo1 = tramos.get(1); 
		Assert.assertEquals("11", tramo1.getFechaDesde().getDia());
		Assert.assertEquals("13", tramo1.getFechaHasta().getDia());
		assertTramoIT15PrimerosDias(tramo1);
		
		Tramo tramo2 = tramos.get(2); 
		Assert.assertEquals("14", tramo2.getFechaDesde().getDia());
		Assert.assertEquals(Integer.toString(endDate.getDate()), tramo2.getFechaHasta().getDia());
		assertTramoActivoNormalTiempoCompleto(tramo2);

		

	}

	@Test
	public void testCretaTrabajadoresYTramosIT16Days()
			throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSalaries(aonContext);
		cleanSystemPayments(aonContext);


		String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, ccc);
		

		Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
		Date endDate = getLastDayOfMonth(startDate);
		
		Date startIT = add(startDate, DAY_OF_MONTH, 10);
		Date endIT = add(startIT, DAY_OF_MONTH, 15);
		
		//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIT, 
				endIT, 
				null/*1750.00/30*/);
		//@formatter:on

		List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);
		
		
		Assert.assertEquals(4, tramos.size());
		
		//Activo
		Tramo tramo0 = tramos.get(0); 
		Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
		Assert.assertEquals("10", tramo0.getFechaHasta().getDia());
		assertTramoActivoNormalTiempoCompleto(tramo0);
		
		Tramo tramo1 = tramos.get(1); 
		Assert.assertEquals("11", tramo1.getFechaDesde().getDia());
		Assert.assertEquals("25", tramo1.getFechaHasta().getDia());
		assertTramoIT15PrimerosDias(tramo1);
		
		Tramo tramo2 = tramos.get(2); 
		Assert.assertEquals("26", tramo2.getFechaDesde().getDia());
		Assert.assertEquals("26", tramo2.getFechaHasta().getDia());
		assertTramoITPagoDelegado(tramo2);

		Tramo tramo3 = tramos.get(3); 
		Assert.assertEquals("27", tramo3.getFechaDesde().getDia());
		Assert.assertEquals(Integer.toString(endDate.getDate()), tramo3.getFechaHasta().getDia());
		assertTramoActivoNormalTiempoCompleto(tramo3);

		

	}

	@Test
	public void testCretaTrabajadoresYTramosATEP()
			throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSalaries(aonContext);
		cleanSystemPayments(aonContext);


		String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, ccc);
		

		Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
		Date endDate = getLastDayOfMonth(startDate);
		
		Date startIT = add(startDate, DAY_OF_MONTH, 10);
		Date endIT = add(startIT, DAY_OF_MONTH, 4);
		
		//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.OCCUPATIONAL_DISEASE, 
				startIT, 
				endIT, 
				null/*1750.00/30*/);
		//@formatter:on

		List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);
		
		Assert.assertEquals(3, tramos.size());
		
		//Activo
		Tramo tramo0 = tramos.get(0); 
		Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
		Assert.assertEquals("10", tramo0.getFechaHasta().getDia());
		assertTramoActivoNormalTiempoCompleto(tramo0);
		
		Tramo tramo1 = tramos.get(1); 
		Assert.assertEquals("11", tramo1.getFechaDesde().getDia());
		Assert.assertEquals("15", tramo1.getFechaHasta().getDia());
		assertTramoITATEPPagoDelegado(tramo1);
		
		Tramo tramo2 = tramos.get(2); 
		Assert.assertEquals("16", tramo2.getFechaDesde().getDia());
		Assert.assertEquals(Integer.toString(endDate.getDate()), tramo2.getFechaHasta().getDia());
		assertTramoActivoNormalTiempoCompleto(tramo2);

	}

	@Test
	public void testCretaTrabajadoresYTramosMaternidad()
			throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSalaries(aonContext);
		cleanSystemPayments(aonContext);


		String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, ccc);
		

		Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
		Date endDate = getLastDayOfMonth(startDate);
		
		Date startIT = add(startDate, DAY_OF_MONTH, 10);
		
		//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.MATERNITY, 
				startIT, 
				null, 
				null/*1750.00/30*/);
		//@formatter:on

		List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);
		
		Assert.assertEquals(2, tramos.size());
		
		// Activo
		Tramo tramo0 = tramos.get(0); 
		Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
		Assert.assertEquals("10", tramo0.getFechaHasta().getDia());
		assertTramoActivoNormalTiempoCompleto(tramo0);
		
		// Maternidad
		Tramo tramo1 = tramos.get(1); 
		Assert.assertEquals("11", tramo1.getFechaDesde().getDia());
		Assert.assertEquals(Integer.toString(endDate.getDate()), tramo1.getFechaHasta().getDia());
		assertTramoMaternidadTiempoCompleto(tramo1);		

	}

	@Test
	public void testCretaTrabajadoresYTramosRiesgoYMaternidad()
			throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSalaries(aonContext);
		cleanSystemPayments(aonContext);


		String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, ccc);
		

		Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
		Date endDate = getLastDayOfMonth(startDate);
		
		Date startIT = add(startDate, DAY_OF_MONTH, 4);
		Date endIT = add(startIT, DAY_OF_MONTH, 5);
		
		//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIT, 
				endIT, 
				null);
		//@formatter:on

		Date startRisk = add(endIT, DAY_OF_MONTH, 1);
		Date endRisk = add(startRisk, DAY_OF_MONTH, 9);
		
		//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.PREGNANCY_RISK, 
				startRisk, 
				endRisk, 
				null);
		//@formatter:on

		Date startMtndad = add(endRisk, DAY_OF_MONTH, 1);

		//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.MATERNITY, 
				startMtndad, 
				null, 
				null);
		//@formatter:on

		List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);
		
		for ( Tramo t : tramos ) 
			System.out.println("Tramo : " + t.getFechaDesde().getMes() + "/" + t.getFechaDesde().getDia() 
					+ "..." + t.getFechaHasta().getMes() + "/" + t.getFechaHasta().getDia());
		
		Assert.assertEquals(4, tramos.size());
		
		// Activo
		Tramo tramo0 = tramos.get(0); 
		Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
		Assert.assertEquals("04", tramo0.getFechaHasta().getDia());
		assertTramoActivoNormalTiempoCompleto(tramo0);
		
		// IT
		Tramo tramo1 = tramos.get(1); 
		Assert.assertEquals("05", tramo1.getFechaDesde().getDia());
		Assert.assertEquals("10", tramo1.getFechaHasta().getDia());
		assertTramoIT15PrimerosDias(tramo1);

		// Risk 
		Tramo tramo2 = tramos.get(2); 
		Assert.assertEquals("11", tramo2.getFechaDesde().getDia());
		Assert.assertEquals("20", tramo2.getFechaHasta().getDia());
		assertTramoMaternidadTiempoCompleto(tramo2);
		
		// Mtndad
		Tramo tramo3 = tramos.get(3); 
		Assert.assertEquals("21", tramo3.getFechaDesde().getDia());
		Assert.assertEquals(Integer.toString(endDate.getDate()), tramo3.getFechaHasta().getDia());
		assertTramoMaternidadTiempoCompleto(tramo3);

	}

	@Test
	public void testCretaTrabajadoresYTramosMaternidadTiempoParcial()
			throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSalaries(aonContext);
		cleanSystemPayments(aonContext);


		String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, ccc);
		

		Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
		Date endDate = getLastDayOfMonth(startDate);
		
		Date startIT = add(startDate, DAY_OF_MONTH, 10);
		
		//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.PATERNITY, 
				startIT, 
				null, 
				null/*1750.00/30*/);
		//@formatter:on
		
		addData(aonContext, 
				contract, 
				contract.getStartDate(), 
				contract.getEndDate(), 
				ContextVariable.PATERNITY_FACTOR,
				"0.33");

		
		
		List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);
		
		Assert.assertEquals(2, tramos.size());
		
		// Activo
		Tramo tramo0 = tramos.get(0); 
		Assert.assertEquals("01", tramo0.getFechaDesde().getDia());
		Assert.assertEquals("10", tramo0.getFechaHasta().getDia());
		assertTramoActivoNormalTiempoCompleto(tramo0);
		
		// Paternidad
		Tramo tramo1 = tramos.get(1); 
		Assert.assertEquals("11", tramo1.getFechaDesde().getDia());
		Assert.assertEquals(Integer.toString(endDate.getDate()), tramo1.getFechaHasta().getDia());
		assertTramoMaternidadTiempoParcial(tramo1);		

	}
	
	
	@Test
	public void testCretaBasesFromSalariesMaternidad()
			throws ExpressionException, SQLException, SalaryException, JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSalaries(aonContext);
		cleanSystemPayments(aonContext);


		String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, ccc);
		

		Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
		Date endDate = getLastDayOfMonth(startDate);
		

		//@formatter:off
		addIT(aonContext, 
				contract, 
				LeaveType.PATERNITY, 
				startDate, 
				null, 
				null);
		//@formatter:on

		getBases(connection, contract, startDate, endDate, ccc);
		

	}

	@Test
	public void testCretaContratosFormacion()
			throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSalaries(aonContext);
		cleanSystemPayments(aonContext);

		String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, ccc, ContractCode.C100, "03");
		

		Date startDate = add(getFirstDayOfMonth(getToday()), MONTH, 1);
		Date endDate = getLastDayOfMonth(startDate);
		
		List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);
		
		Assert.assertEquals(1, tramos.size());
		
		Tramo tramo = tramos.get(0); 
		Assert.assertEquals("01", tramo.getFechaDesde().getDia());
		Assert.assertEquals(Integer.toString(get(endDate, DAY_OF_MONTH)), tramo.getFechaHasta().getDia());
		assertTramoActivoNormalTiempoCompleto(tramo);

	}


	
	@Test
	public void testManualPeriodsI() throws ExpressionException, SQLException,
	SalaryException{
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSalaries(aonContext);
		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()), 
			new HashMap<String,String>(){
			{
				put(ContextVariable.TC2.getName(), "'100'");
				put(ContextVariable.MONTH_DAYS.getName(), "30.00");
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

		Date startDateI = contract.getStartDate(); //add(getFirstDayOfMonth(getToday()), MONTH, 1)
		;
		Date endDateI = add( startDateI, DAY_OF_MONTH,9);
		Date startDateII = add( endDateI, DAY_OF_MONTH,1);
		Date endDateII = add( startDateII, DAY_OF_MONTH,9);
		Date startDateIII = add( endDateII, DAY_OF_MONTH,1);
		Date endDateIII= getLastDayOfMonth(startDateI);
		
		ISQLContractSalaryCalculatorContext ctxI = 
				getContractSalaryCalculatorContext(connection,
						startDateI,
						endDateI,
						endDateI,
						contract);
		ISQLContractSalaryCalculatorContext ctxII = 
				getContractSalaryCalculatorContext(connection,
						startDateII,
						endDateII,
						endDateII,
						contract);
		ISQLContractSalaryCalculatorContext ctxIII = 
				getContractSalaryCalculatorContext(connection,
						startDateIII,
						endDateIII,
						endDateIII,
						contract);
		

		CollectSalaryBuilder<ISalary> collectSalaryBuilder = new CollectSalaryBuilder<ISalary>();
		
		new ContractSalaryCalculator<ISalary>(collectSalaryBuilder).calculate(ctxI);
		new ContractSalaryCalculator<ISalary>(collectSalaryBuilder).calculate(ctxII);
		
		ctxIII.getExpressionContext().putVariable(ContextVariable.ACTIVE_DAYS, collectSalaryBuilder.getAllActiveDaysVar());
		new ContractSalaryCalculator<ISalary>(collectSalaryBuilder).calculate(ctxIII);

		JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder(connection);
		collectSalaryBuilder.collect(jooqSalaryBuilder);
		
		
		
		int salaries = jooqSalaryBuilder.execute();
		
		// Only one salary saved to DB.
		Assert.assertEquals(1, salaries);
		
		AON.getSalaryData(aonContext,
				props -> props.getContractProperty().eq(contract.getId()))
				.forEach(salary -> {

					// 500 Base de contingencias comunes.
					List<ContextData> datas = salary.getContextData()
							.get(CGC_BASE.getName());
					Assert.assertEquals(3, datas.size());
					Collections.sort(datas, (d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate()) );
					// I
					Assert.assertEquals(startDateI, datas.get(0).getStartDate());
					Assert.assertEquals(endDateI, datas.get(0).getEndDate());
					Assert.assertEquals(1500.00 * 10 / 30.00,
							Double.parseDouble(datas.get(0).getExpression()), DELTA);
					// II
					Assert.assertEquals(startDateII, datas.get(1).getStartDate());
					Assert.assertEquals(endDateII, datas.get(1).getEndDate());
					Assert.assertEquals(1500.00 * 10 / 30.00,
							Double.parseDouble(datas.get(1).getExpression()), DELTA);

					// III
					Assert.assertEquals(startDateIII, datas.get(2).getStartDate());
					Assert.assertEquals(endDateIII, datas.get(2).getEndDate());
					Assert.assertEquals(1500.00 * 10 / 30.00,
							Double.parseDouble(datas.get(2).getExpression()), DELTA);

				});
		;
	}

	@Test
	public void testStrike() throws ExpressionException, SQLException,
			SalaryException, IOException, JAXBException {
		
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date contractStart = getFirstDayOfYear(getToday());
		
		String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);

		ContractRecord contract = newContract(aonContext, ccc, ContractCode.C100, "03");

		addData(aonContext, contract, contractStart, contract.getStartDate(), ContextVariable.MONTH_DAYS, "30.00");

		
		Date startDate = add(getFirstDayOfYear(getToday()), MONTH, 2);
		
		Date strikeDay = add(startDate, DAY_OF_MONTH, 10);

		addData(aonContext, contract, strikeDay, strikeDay,
				new HashMap<String, String>() {
					{
						put(STRIKE_DAYS.getName(), "1.00");
						put(STRIKE_FACTOR.getName(), "1.00");
					}
				});
		
		addPayment(aonContext, contract, "1200.00 * DIAS_TRABAJADOS / DIAS_MES");
		
		
		Date endDate = getLastDayOfMonth(startDate);

		List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);
		
		Assert.assertEquals(2, tramos.size());
		Assert.assertEquals(1, Integer.parseInt(tramos.get(0).getFechaDesde().getDia()));
		Assert.assertEquals(10, Integer.parseInt(tramos.get(0).getFechaHasta().getDia()));
		Assert.assertEquals(10, Integer.parseInt(tramos.get(0).getDiasCotizados()));

		Assert.assertEquals(12, Integer.parseInt(tramos.get(1).getFechaDesde().getDia()));
		Assert.assertEquals(31, Integer.parseInt(tramos.get(1).getFechaHasta().getDia()));
		Assert.assertEquals(19, Integer.parseInt(tramos.get(1).getDiasCotizados()));

	}

	@Test
	public void testCretaITAdjust1Day()
			throws ExpressionException, SQLException, SalaryException, JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSalaries(aonContext);
		cleanSystemPayments(aonContext);
		
		
		String ccc = UUID.randomUUID().toString().substring(0, 11);
		ContractRecord contract = newContract(aonContext, ccc, ContractCode.C100, "09");
		
		Date startIt = getLastDayOfMonth(add(getToday(), MONTH, Calendar.JULY - get(getToday(), MONTH)));
		
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startIt, null, null);
		
		
		Date startDate = getFirstDayOfMonth(startIt);
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		int salaries = calculateAndSave(connection, ctx);

		// Only one salary saved to DB.
		//Assert.assertEquals(1, salaries);

		AON.getSalaryData(aonContext,
				props -> props.getContractProperty().eq(contract.getId()).and(props.getCCCProperty().eq(ccc)))
				.forEach(salary -> {
					
					Date endActive = add(startIt, DATE, -1);
					
					for ( Entry<String, List<ContextData>> entry : salary.getContextData().entrySet() ) {
						System.out.print(entry.getKey() + ": " );
						for ( ContextData data: entry.getValue())
							System.out.print(data.getExpression() + "(" + data.getStartDate() + ".." + data.getEndDate()  + "),") ;
						System.out.println();
					}
					
					// 500 Base de contingencias comunes.
					List<ContextData> datas = salary.getContextData()
							.get(CGC_BASE.getName());
					
					Assert.assertEquals(2, datas.size());
					Assert.assertEquals(startDate, datas.get(0).getStartDate());
					Assert.assertEquals(endActive, datas.get(0).getEndDate());
					Assert.assertEquals(1750.00,
							Double.parseDouble(datas.get(0).getExpression()));
					Assert.assertEquals(endDate, datas.get(1).getStartDate());
					Assert.assertEquals(endDate, datas.get(1).getEndDate());
					Assert.assertEquals(0.00,
							Double.parseDouble(datas.get(1).getExpression()));

					// 601 o 611 Base de Accidentes de Trabajo.
					datas = salary.getContextData().get(CGP_BASE.getName());
					Assert.assertEquals(2, datas.size());
					Assert.assertEquals(startDate, datas.get(0).getStartDate());
					Assert.assertEquals(endActive, datas.get(0).getEndDate());
					Assert.assertEquals(1750.00,
							Double.parseDouble(datas.get(0).getExpression()));
					Assert.assertEquals(endDate, datas.get(1).getStartDate());
					Assert.assertEquals(endDate, datas.get(1).getEndDate());
					Assert.assertEquals(0.00,
							Double.parseDouble(datas.get(1).getExpression()));

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


				});
		;
		
		cleanSalaries(aonContext);
		
		List<Tramo> tramos = getTramos(connection, contract, startDate, endDate, ccc);
		Assert.assertEquals(2, tramos.size());
		
		Assert.assertEquals("01", tramos.get(0).getFechaDesde().getDia());
		Assert.assertEquals("07", tramos.get(0).getFechaDesde().getMes());
		Assert.assertEquals("30", tramos.get(0).getFechaHasta().getDia());
		Assert.assertEquals("07", tramos.get(0).getFechaHasta().getMes());
		Assert.assertEquals("30", tramos.get(0).getDiasCotizados());
		assertTramoActivoNormalTiempoCompletoDiario( tramos.get(0) );
		
		Assert.assertEquals("31", tramos.get(1).getFechaDesde().getDia());
		Assert.assertEquals("07", tramos.get(1).getFechaDesde().getMes());
		Assert.assertEquals("31", tramos.get(1).getFechaHasta().getDia());
		Assert.assertEquals("07", tramos.get(1).getFechaHasta().getMes());
		Assert.assertEquals("0", tramos.get(1).getDiasCotizados());
		assertTramoIT15PrimerosDiasDiario( tramos.get(1) );
		
		
		cleanSalaries(aonContext);
		
		List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> bases = getBases(connection, contract, startDate, endDate, ccc);
		Assert.assertEquals(2, bases.size());
		
		net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo1 = bases.get(0);
		Assert.assertEquals("01", tramo1.getFechaDesde().getDia());
		Assert.assertEquals("07", tramo1.getFechaDesde().getMes());
		Assert.assertEquals("30", tramo1.getFechaHasta().getDia());
		Assert.assertEquals("07", tramo1.getFechaHasta().getMes());
		assertDato(tramo1.getDatosTramo().getDato(), "I", "51", "M");
		assertDato(tramo1.getDatosTramo().getDato(), "C", "500", "175000");
		assertDato(tramo1.getDatosTramo().getDato(), "C", "601", "175000");
		
		net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo tramo2 = bases.get(1);
		Assert.assertEquals("31", tramo2.getFechaDesde().getDia());
		Assert.assertEquals("07", tramo2.getFechaDesde().getMes());
		Assert.assertEquals("31", tramo2.getFechaHasta().getDia());
		Assert.assertEquals("07", tramo2.getFechaHasta().getMes());
		assertDato(tramo2.getDatosTramo().getDato(), "I", "51", "M");
		

	}


	protected ContractRecord newContract(AONContext aonContext, String ccc) {
		return newContract(aonContext, ccc, ContractCode.C100, "03", CCCType.PRINCIPAL);
	}

	protected ContractRecord newContract(AONContext aonContext, String ccc, ContractCode contractCode) {
		return newContract(aonContext, ccc, contractCode, "03", CCCType.PRINCIPAL);
	}

	protected ContractRecord newContract(AONContext aonContext, String ccc, ContractCode contractCode, String quoteGroup ) {
		return newContract(aonContext, ccc, contractCode, quoteGroup, CCCType.PRINCIPAL);
	}

	protected ContractRecord newContract(AONContext aonContext, String ccc, ContractCode contractCode, String quoteGroup, CCCType cccType ) {
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


		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				SSRegimeType.GENERAL, 
				CCCType.PRINCIPAL,			
				getFirstDayOfYear(getToday()),
				null,
				new HashMap<String, String>() {
					{
						//put(MONTH_DAYS.getName(), String.format("%f", 30.00));
						put(QUOTE_GROUP.getName(), String.format("'%s'", quoteGroup));
						put(TC2.getName(), String.format("\"%s\"", contractCode.getValue()));
						put(MONTH_DAYS.getName(), String.format("{'%s':30}[%s]", quoteGroup, QUOTE_GROUP.getName()));
						
					}
				},
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
//				"TRACE('GRUPO_COTIZACION=%s\r\n', GRUPO_COTIZACION); 0.00;"
				}, 
				new String[] {						
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				"BASE_IRPF * PORCENTAJE_IRPF/100" ,
				"TRACE('DIAS_TRABAJADOS=%f\r\n', DIAS_TRABAJADOS); 0.00;"
				},
				null
				,
				domain.getId(), 			//domainId, 
				person.getId(),				//personId, 
				workplace.getId(),			//workplaceId, 
				enterpriseCcc.getId(),		//enterpriseCccId,
				enterpriseActivity.getId()	//enterpriseActivityId
				);
		
		PaymentConceptRecord prestIT = addConcept(aonContext, "PREST_IT");
		
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
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s",  OCCUPATIONAL_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);

		PaymentConceptRecord mtnad = addConcept(aonContext, "MTNAD");

		addPayment(aonContext, contract, mtnad, 
				String.format("0.00 * %s",  MATERNITY_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);

		addPayment(aonContext, contract, mtnad, 
				String.format("0.00 * %s",  PATERNITY_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		//@formatter:on
		return contract;
	}

	// -------------------------------------------------------------------------
	private List<Tramo> getTramos ( Connection connection, ContractRecord contract, Date startDate, Date endDate, String ccc) throws ExpressionException, SQLException, SalaryException, JAXBException, IOException {
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		int salaries = calculateAndSave(connection, ctx);

		// Only one salary saved to DB.
		Assert.assertEquals(1, salaries);

		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		String mes = Integer.toString(calendar.get(MONTH)+1);
		String anho = Integer.toString(calendar.get(YEAR));
		
		
		
		PipedInputStream trabajadoresTramosIs = new PipedInputStream();
		
		new Thread( () ->  {
								try { 
									PipedOutputStream trabajadoresTramosOs = new PipedOutputStream(trabajadoresTramosIs);
									TrabajadoresTramos.generate(connection, 
											"0000", 	//autorizado, 
											mes, 		//desdeAnhoMes, 
											anho , 		//desdeAnho, 
											mes, 		//hastaMes, 
											anho , 		//hastaAnho, 
											mes, 		//ctrlMes, 
											anho , 		//ctrlAnho, 
											"L00",		//tipo, 
											new String[]
											{
											"0111" + "" + ccc
											}, 			//cccs
											trabajadoresTramosOs);
									trabajadoresTramosOs.close();
								} catch ( JAXBException | IOException e ){
									throw new AssertException(e.getMessage());
								} finally {
									
								}
							}
		).start();
		
		net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos trabajadoresTramos = Utils
				.unmarshal(
						net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos.class,
						trabajadoresTramosIs);
		
		trabajadoresTramosIs.close();
		
		return
			trabajadoresTramos
			.getLiquidacion()
			.getLiquidacionMes()
			.get(0)
			.getTrabajadores()
			.getTrabajador()
			.get(0)
			.getTramos()
			.getTramo()
			;
		
	}

	// -------------------------------------------------------------------------
	private List<net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo> getBases ( Connection connection, ContractRecord contract, Date startDate, Date endDate, String ccc) throws ExpressionException, SQLException, SalaryException, JAXBException, IOException, EmptyBasesException, XMLStreamException, FactoryConfigurationError {
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		int salaries = calculateAndSave(connection, ctx);

		// Only one salary saved to DB.
		Assert.assertEquals(1, salaries);

		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		String mes = Integer.toString(calendar.get(MONTH)+1);
		String anho = Integer.toString(calendar.get(YEAR));
		
		
		
		PipedInputStream trabajadoresTramosIs = new PipedInputStream();
		
		new Thread( () ->  {
								try { 
									PipedOutputStream trabajadoresTramosOs = new PipedOutputStream(trabajadoresTramosIs);
									TrabajadoresTramos.generate(connection, 
											"0000", 	//autorizado, 
											mes, 		//desdeAnhoMes, 
											anho , 		//desdeAnho, 
											mes, 		//hastaMes, 
											anho , 		//hastaAnho, 
											mes, 		//ctrlMes, 
											anho , 		//ctrlAnho, 
											"L00",		//tipo, 
											new String[]
											{
											"0111" + "" + ccc
											}, 			//cccs
											trabajadoresTramosOs);
									trabajadoresTramosOs.close();
								} catch ( JAXBException | IOException e ){
									throw new AssertException(e.getMessage());
								} finally {
									
								}
							}
		).start();
		
		
		
		ByteArrayOutputStream basesOs = new ByteArrayOutputStream();

		Bases.generate(connection, 
				true, 							//comments, 
				false,							//skipExisting, 
				false,							//acceptPrevBases, 
				null,							//nafs, 
				new String [] {},				//defaultsValues, 
				trabajadoresTramosIs, 
				null, 							//respuestaIs, 
				basesOs,						//os, 
				new Bases.BasesCallback [] {}	//cbs
				);
		
		System.out.println(basesOs.toString());
		
		ByteArrayInputStream basesIs = new ByteArrayInputStream(basesOs.toByteArray());
		
		net.aonsolutions.core.tgss.creta.jaxb.bases.Bases bases = Utils
				.unmarshal(
						net.aonsolutions.core.tgss.creta.jaxb.bases.Bases.class,
						basesIs);
		
		basesIs.close();
		basesOs.close();
		
		return bases
				.getLiquidacion()
				.get(0)
				.getLiquidacionMes()
				.get(0)
				.getTrabajadores()
				.getTrabajador()
				.get(0)
				.getTramos()
				.getTramo()
				;
	}
	// -------------------------------------------------------------------------
	
	private static void assertTramoIT15PrimerosDias(Tramo tramo) {
		List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
		
		assertDatosSolicitado(datoSolicitados, "C", "500", "B");
		try {
			assertDatosSolicitado(datoSolicitados, "C", "603", "B");
		} catch ( AssertException e ) {
			assertDatosSolicitado(datoSolicitados, "C", "613", "B");
		}
	}
	private static void assertTramoIT15PrimerosDiasDiario(Tramo tramo) {
		assertTramoIT15PrimerosDias(tramo);
		List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
		assertDatosSolicitado(datoSolicitados, "I", "51", "P");
	}

	private static void assertTramoITPagoDelegado(Tramo tramo) {
		assertTramoIT15PrimerosDias(tramo);
		List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
		assertDatosSolicitado(datoSolicitados, "C", "563", "B");
	}

	private static void assertTramoITATEPPagoDelegado(Tramo tramo) {
		assertTramoIT15PrimerosDias(tramo);
		List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
		assertDatosSolicitado(datoSolicitados, "C", "663", "B");
	}

	private static void assertTramoActivoNormal(Tramo tramo) {
		List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
		
		assertDatosSolicitado(datoSolicitados, "C", "500", "B");
		assertDatosSolicitado(datoSolicitados, "C", "501", "P");
		assertDatosSolicitado(datoSolicitados, "C", "502", "P");
		try {
			assertDatosSolicitado(datoSolicitados, "C", "601", "B");
		} catch ( AssertException e ) {
			assertDatosSolicitado(datoSolicitados, "C", "611", "B");
		}
	}

	private static void assertTramoActivoNormalTiempoCompleto(Tramo tramo) {
		assertTramoActivoNormal(tramo);
	}
	
	private static void assertTramoActivoNormalTiempoParcial(Tramo tramo) {
		assertTramoActivoNormal(tramo);
		List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
		assertDatosSolicitado(datoSolicitados, "H", "01", "B");
		assertDatosSolicitado(datoSolicitados, "C", "537", "P");
		assertDatosSolicitado(datoSolicitados, "H", "02", "P");
	}

	private static void assertTramoActivoNormalTiempoCompletoDiario(Tramo tramo) {
		assertTramoActivoNormal(tramo);
		List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
		assertDatosSolicitado(datoSolicitados, "I", "51", "P");
	}

	private static void assertTramoMaternidadTiempoCompleto(Tramo tramo) {
		List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();

		assertDatosSolicitado(datoSolicitados, "C", "509", "B");
		try {
			assertDatosSolicitado(datoSolicitados, "C", "603", "B");
		} catch ( AssertException e ) {
			assertDatosSolicitado(datoSolicitados, "C", "613", "B");
		}
	}

	private static void assertTramoMaternidadTiempoParcial(Tramo tramo) {
		List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
		// PARTE JORNADA TRABAJADA 
		assertDatosSolicitado(datoSolicitados, "C", "500", "B");
		assertDatosSolicitado(datoSolicitados, "C", "501", "P");
		assertDatosSolicitado(datoSolicitados, "C", "537", "P");
		assertDatosSolicitado(datoSolicitados, "H", "01", "B");
		assertDatosSolicitado(datoSolicitados, "H", "02", "P");
		try {
			assertDatosSolicitado(datoSolicitados, "C", "601", "B");
		} catch ( AssertException e ) {
			assertDatosSolicitado(datoSolicitados, "C", "611", "B");
		}

		// PARTE JORNADA EN SITUACIÓN DE DESCANSO 
		assertDatosSolicitado(datoSolicitados, "C", "535", "B");
		try {
			assertDatosSolicitado(datoSolicitados, "C", "635", "B");
		} catch ( AssertException e ) {
			assertDatosSolicitado(datoSolicitados, "C", "634", "B");
		}
	}

	private static void assertDatosSolicitado( List<DatoSolicitado> datoSolicitados, String tipoDato, String codigo, String  indicadorObligatoriedad) {
		for ( DatoSolicitado datoSolicitado: datoSolicitados ){
			if ( datoSolicitado.getCodigo().equals(codigo) ) {
				Assert.assertEquals(tipoDato, datoSolicitado.getTipoDato());
				Assert.assertEquals(indicadorObligatoriedad, datoSolicitado.getIndicadorObligatoriedad());
				return;
			}
		}
		
		throw new AssertException("Dato Solicitado " + codigo + " Not Found");
	}
	
	private static void assertDato( List<Dato> datos, String tipoDato, String codigo, String  valor) {
		for ( Dato dato: datos ){
			if ( dato.getCodigo().equals(codigo) ) {
				Assert.assertEquals(tipoDato, dato.getTipoDato());
				Assert.assertEquals(valor, dato.getValor());
				return;
			}
		}
		
		throw new AssertException("Dato " + codigo + " Not Found");
	}

	private static void assertNoDato( List<Dato> datos, String tipoDato, String codigo) {
		for ( Dato dato: datos ){
			if ( dato.getCodigo().equals(codigo) ) {
				throw new AssertException("Dato " + codigo + " Found");
			}
		}
		return;
		
	}

	private static int calculateAndSave(Connection connection,
			ISQLContractSalaryCalculatorContext ctx) throws SalaryException {
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
		JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder(connection);
		new ContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		return jooqSalaryBuilder.execute();
	}
	
	private static final void cleanSalaries(AONContext aonContext) {
		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=0");
		
		aonContext.getDslContext().delete(SALARY_BONUS).execute();
		aonContext.getDslContext().delete(SALARY_EMBARGO).execute();
		aonContext.getDslContext().delete(SALARY_COST).execute();
		aonContext.getDslContext().delete(SALARY_DEDUCTION).execute();
		aonContext.getDslContext().delete(SALARY_PAYMENT).execute();
		aonContext.getDslContext().delete(SALARY_DATA).execute();
		aonContext.getDslContext().delete(SALARY).execute();

		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=1");
	}
	

}
