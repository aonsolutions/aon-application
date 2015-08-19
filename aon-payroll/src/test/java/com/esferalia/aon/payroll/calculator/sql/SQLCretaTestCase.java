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
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;

public class SQLCretaTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.006;

	// -------------------------------------------------------------------------
	@Test
	public void testCretaStandardActiveFullTime() throws ExpressionException, SQLException,
			SalaryException {
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
		
		AON.getSalaryData(aonContext, props->
			props.getContractProperty().eq(contract.getId())
		)
		.forEach(salary->{

			// 500 Base de contingencias comunes.
			List<ContextData> datas = salary.getContextData().get(CGC_BASE.getName());
			Assert.assertEquals(1, datas.size());
			Assert.assertEquals(startDate,datas.get(0).getStartDate());
			Assert.assertEquals(endDate,datas.get(0).getEndDate());
			Assert.assertEquals(1750.00,Double.parseDouble(datas.get(0).getExpression()));
			
			// 501 Base  de  Horas  Extras  Fuerza Mayor 
			datas = salary.getContextData().get(STRUCTURAL_OVERTIME_BASE.getName());
			Assert.assertEquals(1, datas.size());
			Assert.assertEquals(startDate,datas.get(0).getStartDate());
			Assert.assertEquals(endDate,datas.get(0).getEndDate());
			Assert.assertEquals(0.00,Double.parseDouble(datas.get(0).getExpression()));

			// 502 Base  de  Horas  Extras  
			datas = salary.getContextData().get(NON_STRUCTURAL_OVERTIME_BASE.getName());
			Assert.assertEquals(1, datas.size());
			Assert.assertEquals(startDate,datas.get(0).getStartDate());
			Assert.assertEquals(endDate,datas.get(0).getEndDate());
			Assert.assertEquals(0.00,Double.parseDouble(datas.get(0).getExpression()));
			
			// 601 o 611 Base de Accidentes de Trabajo.
			datas = salary.getContextData().get(CGP_BASE.getName());
			Assert.assertEquals(1, datas.size());
			Assert.assertEquals(startDate,datas.get(0).getStartDate());
			Assert.assertEquals(endDate,datas.get(0).getEndDate());
			Assert.assertEquals(1750.00,Double.parseDouble(datas.get(0).getExpression()));

		});
		;
		
		

	}

	// -------------------------------------------------------------------------
	@Test
	public void testCretaITMaternityFullTimeI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		cleanSystemPayments(aonContext);
		addSSRegimePayment(aonContext, SSRegimeType.GENERAL, getFirstDayOfYear(getToday()), PaymentType.CRA_0004 , 
				"TRACE('DIAS_MATERNIDAD=%d\r\n',DIAS_MATERNIDAD);0.00", 
				"DIAS_MATERNIDAD * BASE_REGULADORA" ,
				"0.00");
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
		
		AON.getSalaryData(aonContext, props->
			props.getContractProperty().eq(contract.getId())
		)
		.forEach(salary->{

			int monthDays = AonDateUtils.getMax(startDate, DAY_OF_MONTH);
  
			// 500 Base de contingencias comunes.
			List<ContextData> datas = salary.getContextData().get(CGC_BASE.getName());
			Assert.assertEquals(1, datas.size());
			
			Assert.assertEquals(startDate,datas.get(0).getStartDate());
			Assert.assertEquals(endDate,datas.get(0).getEndDate());
			Assert.assertEquals(1750.00,Double.parseDouble(datas.get(0).getExpression()), DELTA);
			

		});
		;
		
		

	}

	// -------------------------------------------------------------------------
	@Test
	public void testCretaITMaternityFullTimeII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		cleanSystemPayments(aonContext);
		addSSRegimePayment(aonContext, SSRegimeType.GENERAL, getFirstDayOfYear(getToday()), PaymentType.CRA_0004 , 
				"TRACE('DIAS_MATERNIDAD=%d\r\n',DIAS_MATERNIDAD);0.00", 
				"DIAS_MATERNIDAD * BASE_REGULADORA" ,
				"0.00");
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

		//@formatter:off
		addIT(aonContext, 
				contract, 
				MATERNITY, 
				getFirstDayOfYear(startDate), 
				null, 
				1755.00 / monthDays);
		//@formatter:on
		
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		
		int salaries = calculateAndSave(connection, ctx);
		
		
		// Only one salary saved to DB. 
		Assert.assertEquals(1, salaries);
		
		AON.getSalaryData(aonContext, props->
			props.getContractProperty().eq(contract.getId())
		)
		.forEach(salary->{

  
			// 500 Base de contingencias comunes.
			List<ContextData> datas = salary.getContextData().get(CGC_BASE.getName());
			Assert.assertEquals(1, datas.size());
			
			Assert.assertEquals(startDate,datas.get(0).getStartDate());
			Assert.assertEquals(endDate,datas.get(0).getEndDate());
			Assert.assertEquals(1755.00,Double.parseDouble(datas.get(0).getExpression()), DELTA);
			

		});
		;
		
		

	}
	// -------------------------------------------------------------------------
	@Test
	public void testCretaITMaternityFullTimeIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		cleanSystemPayments(aonContext);
		addSSRegimePayment(aonContext, SSRegimeType.GENERAL, getFirstDayOfYear(getToday()), PaymentType.CRA_0004 , 
				"TRACE('DIAS_MATERNIDAD=%d\r\n',DIAS_MATERNIDAD);0.00", 
				"DIAS_MATERNIDAD * BASE_REGULADORA" ,
				"0.00");
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
		
		AON.getSalaryData(aonContext, props->
			props.getContractProperty().eq(contract.getId())
		)
		.forEach(salary->{

			Date endActive = add(startIt, DAY_OF_MONTH,-1);
			int monthDays = AonDateUtils.getMax(startDate, DAY_OF_MONTH);
  
			// 500 Base de contingencias comunes.
			List<ContextData> datas = salary.getContextData().get(CGC_BASE.getName());
			Assert.assertEquals(2, datas.size());
			
			Assert.assertEquals(startDate,datas.get(0).getStartDate());
			Assert.assertEquals(endActive,datas.get(0).getEndDate());
			Assert.assertEquals(1750.00 * 13 / monthDays,Double.parseDouble(datas.get(0).getExpression()), DELTA);
			
			Assert.assertEquals(startIt,datas.get(1).getStartDate());
			Assert.assertEquals(endDate,datas.get(1).getEndDate());
			Assert.assertEquals(1750.00 * (monthDays -13)/monthDays,Double.parseDouble(datas.get(1).getExpression()), DELTA);

			// 601 o 611 Base de Accidentes de Trabajo.
			datas = salary.getContextData().get(CGP_BASE.getName());
			Assert.assertEquals(2, datas.size());

			Assert.assertEquals(startDate,datas.get(0).getStartDate());
			Assert.assertEquals(endActive,datas.get(0).getEndDate());
			Assert.assertEquals(1750.00 * 13 / monthDays,Double.parseDouble(datas.get(0).getExpression()), DELTA);
			
			Assert.assertEquals(startIt,datas.get(1).getStartDate());
			Assert.assertEquals(endDate,datas.get(1).getEndDate());
			Assert.assertEquals(1750.00 * (monthDays -13)/monthDays,Double.parseDouble(datas.get(1).getExpression()), DELTA);

		});
		;
		
		

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
