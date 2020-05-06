/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;


/**
 * @author rtrepiana
 *
 */
public class SQLEmbargoTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.01;

	private static class SuccessException extends RuntimeException {

	}

	@Test
	public void testSalaryEmbargoI()
			throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();

		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		addEmbargarFunction(aonContext);

		ContractRecord contract = newContract(aonContext,
				new String[] { 
					"1550.00 * DIAS_TRABAJADOS / DIAS_MES"						
				},
				new String[] {}
		);
		
		
		
		addEmbargo(aonContext, contract, "TRACE('%f\r\n', (EMBARGADO));EMBARGAR(IMPORTE_EMBARGO)");
		
		
		addData(aonContext, contract, 
				getFirstDayOfYear(getToday()), 
				null,
				new HashMap<String, String>() {
					{
						put("TC2", "'100'");
						put("IMPORTE_EMBARGO", "300.00");
						put("GRUPO_COTIZACION", "\"01\"");
					}
				});

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);

		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();

		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
			@Override
			public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
					Map<String, ITimedVariable<?>> context) {
				org.junit.Assert.assertEquals(600.00*0.30,amount,0.01);
				super.addEmbargo(id, amount, description, embargo, context);
			}
		};
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();

		start = add(start, Calendar.MONTH,1);
		end = getLastDayOfMonth(start);
		ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);

		jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
			@Override
			public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
					Map<String, ITimedVariable<?>> context) {
				org.junit.Assert.assertEquals(300.00 - 600.00*0.30, amount,DELTA);
				super.addEmbargo(id, amount, description, embargo, context);
			}
		};
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();

		start = add(start, Calendar.MONTH,1);
		end = getLastDayOfMonth(start);
		ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);
		jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
			@Override
			public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
					Map<String, ITimedVariable<?>> context) {
				org.junit.Assert.fail();;
				super.addEmbargo(id, amount, description, embargo, context);
			}
		};
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();

		start = add(start, Calendar.MONTH,1);
		end = getLastDayOfMonth(start);
		ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);
		jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
			@Override
			public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
					Map<String, ITimedVariable<?>> context) {
				org.junit.Assert.fail();;
				super.addEmbargo(id, amount, description, embargo, context);
			}
		};
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();
		
		start = getFirstDayOfMonth(getToday());
		end = getLastDayOfMonth(start);

		ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);


		jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
			@Override
			public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
					Map<String, ITimedVariable<?>> context) {
				org.junit.Assert.assertEquals(600.00*0.30,amount,0.01);
				super.addEmbargo(id, amount, description, embargo, context);
			}
		};
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();
		
	}

	@Test
	public void testSalaryEmbargoII()
			throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();

		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		addEmbargarFunction(aonContext);

		ContractRecord contract = newContract(aonContext,
				new String[] { 
					"1550.00 * DIAS_TRABAJADOS / DIAS_MES"						
				},
				new String[] {}
		);
		
		
		
		addEmbargo(aonContext, contract, "TRACE('%f\r\n', (EMBARGADO));EMBARGAR(IMPORTE_EMBARGO)");
		
		
		addData(aonContext, contract, 
				getFirstDayOfYear(getToday()), 
				null,
				new HashMap<String, String>() {
					{
						put("TC2", "'100'");
						put("IMPORTE_EMBARGO", "160.00");
						put("GRUPO_COTIZACION", "\"01\"");
					}
				});

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);

		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();

		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
			@Override
			public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
					Map<String, ITimedVariable<?>> context) {
				org.junit.Assert.assertEquals(160.00,amount,0.01);
				super.addEmbargo(id, amount, description, embargo, context);
			}
		};
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();


	}

	@Test
	public void testSalaryEmbargoDescription()
			throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();

		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		addEmbargarFunction(aonContext);

		ContractRecord contract = newContract(aonContext,
				new String[] { 
					"1550.00 * DIAS_TRABAJADOS / DIAS_MES"						
				},
				new String[] {}
		);
		
		
		
		addEmbargo(aonContext, contract, "PTE. EMBARGAR: @{PENDIENTE} Eur., (EMBARGADO: @{EMBARGADO} Eur.)", "EMBARGAR(IMPORTE_EMBARGO)") ;
		
		
		addData(aonContext, contract, 
				getFirstDayOfYear(getToday()), 
				null,
				new HashMap<String, String>() {
					{
						put("TC2", "'100'");
						put("IMPORTE_EMBARGO", "300.00");
						put("GRUPO_COTIZACION", "\"01\"");
					}
				});

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);

		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();

		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
			@Override
			public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
					Map<String, ITimedVariable<?>> context) {
				org.junit.Assert.assertEquals("PTE. EMBARGAR: 120.0 Eur., (EMBARGADO: 180.0 Eur.)", description);
				org.junit.Assert.assertEquals(600.00*0.30,amount,0.01);
				super.addEmbargo(id, amount, description, embargo, context);
			}
		};
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();

		start = add(start, Calendar.MONTH,1);
		end = getLastDayOfMonth(start);
		ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);

		jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
			@Override
			public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
					Map<String, ITimedVariable<?>> context) {
				org.junit.Assert.assertEquals("PTE. EMBARGAR: 0.0 Eur., (EMBARGADO: 300.0 Eur.)", description);
				org.junit.Assert.assertEquals(300.00 - 600.00*0.30, amount,DELTA);
				super.addEmbargo(id, amount, description, embargo, context);
			}
		};
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();

		start = add(start, Calendar.MONTH,1);
		end = getLastDayOfMonth(start);
		ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);
		jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
			@Override
			public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
					Map<String, ITimedVariable<?>> context) {
				org.junit.Assert.fail();;
				super.addEmbargo(id, amount, description, embargo, context);
			}
		};
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();
	}

	private void addEmbargarFunction(AONContext aonContext) {
		addSystemData(aonContext,
				getFirstDayOfYear(getToday()), null,
				new HashMap<String, String>() {
					{
						put("SMI","950.00");
						put("PENDIENTE","0.00");
						put("MAX_EMBARGABLE","def(l){ MAX(((l - SMI) * 0.30),0)+MAX(((l - 2 * SMI ) * 0.20),0) + MAX((( l - 3 * SMI ) * 0.10),0) +MAX((( l - 4 * SMI ) * 0.15),0) + MAX((( l - 5 * SMI ) * 0.15),0)}");
						put("EMBARGAR","def (EMBARGO){ PENDIENTE = ( EMBARGO + EMBARGADO ); E=((PENDIENTE > 0) ? MIN(MAX_EMBARGABLE(TOTAL_LIQUIDO), PENDIENTE ) : REMOVE()); SELF.addVariable('PENDIENTE',PENDIENTE - E); SELF.addVariable('EMBARGADO',-1*(EMBARGADO - E)); E;  }");
						
					}
				});
	}


}
