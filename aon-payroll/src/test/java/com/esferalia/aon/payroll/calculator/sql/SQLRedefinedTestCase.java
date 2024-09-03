package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

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


public class SQLRedefinedTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000001;

	@Test
	@Disabled("Deprecated...")
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
				assertEquals(0.0,
						implicit.getValue(implicit.getPeriod()));
				assertEquals(10.0,
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
		
		 assertEquals(1, myListener.count);  
	}

	// ------------------------------------------------------------------------

	
	
	@Test
	@Disabled("Deprecated...")
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
				assertEquals(0.0,
						implicit.getValue(implicit.getPeriod()));
				assertEquals(10.0,
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
		
		 assertEquals(1, myListener.count);  
	}


	@Test
	@Disabled("Deprecated...")
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
				assertEquals(0.0,
						implicit.getValue(implicit.getPeriod()));
				assertEquals(10.0,
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
		
		 assertEquals(1, myListener.count);  
	}
}
