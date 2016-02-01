package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.NON_STRUCTURAL_OVERTIME_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRUCTURAL_OVERTIME_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C100;
import static com.esferalia.aon.payroll.enumeration.LeaveType.MATERNITY;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.stream.Collectors;

import org.junit.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseActivityRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseCccRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.ScopeRecord;
import com.esferalia.aon.jooq.tables.records.WorkplaceRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
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
				"TRACE('DIAS_MATERNIDAD=%d\r\n',DIAS_MATERNIDAD);0.00",
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
				"TRACE('DIAS_MATERNIDAD=%d\r\n',DIAS_MATERNIDAD);0.00",
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
				"TRACE('DIAS_MATERNIDAD=%d\r\n',DIAS_MATERNIDAD);0.00",
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
