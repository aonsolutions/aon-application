package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;

import org.junit.BeforeClass;
import org.junit.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.IrpfOutcome;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext.IListener;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;

import junit.framework.Assert;

public class SQLRedefinedTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000001;

	@Test
	public void testIrpfNo()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:off
		ContractRecord contract = newContract(aonContext,  
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
					}
				}, new String[] { 
						"NETO(3000.00) * DIAS_TRABAJADOS / DIAS_MES" ,
						}
				, new String[] {
						"TRACE('IRPF = %f\r\n', PORCENTAJE_IRPF) ; PORCENTAJE_IRPF" 
				}, 
				null);
		//@formatter:on

		class MyListener implements  IListener {

			private int count = 0; 
			
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
			}

			@Override
			public void onRedefinedImplicit(String name,
					ITimedVariable<?> redefined, ITimedVariable<?> implicit) {
				System.out.printf(
						"La variable del sistema '%s' con valor '%f' esta redefinida con el valor '%f'.\r\n",
						name, implicit.getValue(implicit.getPeriod()),
						redefined.getValue(redefined.getPeriod()));
				Assert.assertEquals(0.0,
						implicit.getValue(implicit.getPeriod()));
				Assert.assertEquals(10.0,
						redefined.getValue(redefined.getPeriod()));
				
				count++;
			}

		};
		
		MyListener myListener = new MyListener();
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract, myListener);

		new ContractSalaryCalculator<Salary>(new SalaryBuilder())
				.calculate(ctx);
		
		 Assert.assertEquals(0, myListener.count);  
	}

	@Test
	public void testSistema()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:off
		ContractRecord contract = newContract(aonContext,  
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
						put("PORCENTAJE_IRPF", "SISTEMA('PORCENTAJE_IRPF')");
						put("PORCENTAJE_CGC", " SISTEMA ( 'PORCENTAJE_CGC'   )  ");
						put("PORCENTAJE_FP", " SISTEMA ( \"PORCENTAJE_FP\"   ) ; ");
					}
				}, new String[] { 
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES" ,
						}
				, new String[] {
						"TRACE('IRPF = %f\r\n', PORCENTAJE_IRPF) ; PORCENTAJE_IRPF" ,
						"TRACE('CGC = %f\r\n', PORCENTAJE_CGC) ; PORCENTAJE_CGC",
						"TRACE('FP = %f\r\n', PORCENTAJE_FP) ; PORCENTAJE_FP"
				}, 
				null);
		//@formatter:on

		class MyListener implements  IListener {

			private int count = 0; 
			
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
			}

			@Override
			public void onRedefinedImplicit(String name,
					ITimedVariable<?> redefined, ITimedVariable<?> implicit) {
				System.out.printf(
						"La variable del sistema '%s' con valor '%f' esta redefinida con el valor '%f'.\r\n",
						name, implicit.getValue(implicit.getPeriod()),
						redefined.getValue(redefined.getPeriod()));
				
				count++;
			}

		};
		
		MyListener myListener = new MyListener();
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract, myListener);

		new ContractSalaryCalculator<Salary>(new SalaryBuilder())
				.calculate(ctx);
		
		 Assert.assertEquals(0, myListener.count);  
	}

	@Test
	public void testLiquid()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:off
		ContractRecord contract = newContract(aonContext,  
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
						put(ContextVariable.IRPF_PERCENT.getName(), "10.00");
					}
				}, new String[] { 
						"NETO(100.00) * DIAS_TRABAJADOS / DIAS_MES" ,
						}
				, new String[] {
						"TRACE('IRPF = %f\r\n', PORCENTAJE_IRPF) ; PORCENTAJE_IRPF" 
				}, 
				null);
		//@formatter:on

		class MyListener implements  IListener {

			private int count = 0; 
			
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
			}

			@Override
			public void onRedefinedImplicit(String name,
					ITimedVariable<?> redefined, ITimedVariable<?> implicit) {
				System.out.printf(
						"La variable del sistema '%s' con valor '%f' esta redefinida con el valor '%f'.\r\n",
						name, implicit.getValue(implicit.getPeriod()),
						redefined.getValue(redefined.getPeriod()));
				Assert.assertEquals(0.0,
						implicit.getValue(implicit.getPeriod()));
				Assert.assertEquals(10.0,
						redefined.getValue(redefined.getPeriod()));
				
				count++;
			}

		};
		
		MyListener myListener = new MyListener();
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract, myListener);

		new ContractSalaryCalculator<Salary>(new SalaryBuilder())
				.calculate(ctx);
		
		 Assert.assertEquals(1, myListener.count);  
	}

	// ------------------------------------------------------------------------

	
	
	@Test
	public void testGross()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:off
		ContractRecord contract = newContract(aonContext,  
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
						put(ContextVariable.IRPF_PERCENT.getName(), "10.00");
					}
				}, new String[] { 
						"BRUTO(100.00) * DIAS_TRABAJADOS / DIAS_MES" ,
						}
				, new String[] {
						"TRACE('IRPF = %f\r\n', PORCENTAJE_IRPF) ; PORCENTAJE_IRPF" 
				}, 
				null);
		//@formatter:on

		class MyListener implements  IListener {

			private int count = 0; 
			
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
			}

			@Override
			public void onRedefinedImplicit(String name,
					ITimedVariable<?> redefined, ITimedVariable<?> implicit) {
				System.out.printf(
						"La variable del sistema '%s' con valor '%f' esta redefinida con el valor '%f'.\r\n",
						name, implicit.getValue(implicit.getPeriod()),
						redefined.getValue(redefined.getPeriod()));
				Assert.assertEquals(0.0,
						implicit.getValue(implicit.getPeriod()));
				Assert.assertEquals(10.0,
						redefined.getValue(redefined.getPeriod()));
				
				count++;
			}

		};
		
		MyListener myListener = new MyListener();
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract, myListener);

		new ContractSalaryCalculator<Salary>(new SalaryBuilder())
				.calculate(ctx);
		
		 Assert.assertEquals(1, myListener.count);  
	}


	@Test
	public void testIrpf()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:off
		ContractRecord contract = newContract(aonContext,  
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
						put(ContextVariable.WEEK_HOURS.getName(), "10.00");
					}
				}, new String[] { 
						"100.00 * DIAS_TRABAJADOS / DIAS_MES" ,
						}
				, new String[] {
						"TRACE('IRPF = %f\r\n', PORCENTAJE_IRPF) ; PROCENTAJE_IRPF" 
				}, 
				null);
		//@formatter:on

		class MyListener implements  IListener {

			private int count = 0; 
			
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
			}

			@Override
			public void onRedefinedImplicit(String name,
					ITimedVariable<?> redefined, ITimedVariable<?> implicit) {
				System.out.printf(
						"La variable del sistema '%s' con valor '%f' esta redefinida con el valor '%f'.\r\n",
						name, implicit.getValue(implicit.getPeriod()),
						redefined.getValue(redefined.getPeriod()));
				Assert.assertEquals(0.0,
						implicit.getValue(implicit.getPeriod()));
				Assert.assertEquals(10.0,
						redefined.getValue(redefined.getPeriod()));
				
				count++;
			}

		};
		
		MyListener myListener = new MyListener();
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract, myListener);

		new ContractSalaryCalculator<Salary>(new SalaryBuilder())
				.calculate(ctx);
		
		 Assert.assertEquals(1, myListener.count);  
	}
}
