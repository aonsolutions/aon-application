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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.junit.Test;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.IExpressionVariable;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;

/**
 * @author rtrepiana
 *
 */
public class SQLContractSalaryCalculatorTestCase extends
		AbstractSQLTestCase {


	@Test
	public void testListener() throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		ContractRecord contract = newContract(aonContext, 
				new String[]{
				"NETO(2500.00) ",
				"( P_2 + P_3 ) * 0.10 ",
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				"BRUTO(4000.00) * DIAS_TRABAJADOS / DIAS_MES",
				"BRUTO(80.00) * DIAS_TRABAJADOS / DIAS_MES"
				},
				new String[]{
				"BASE_CGC * 0.10",
				"BASE_CGP * 0.05",
				"BASE_ESTR * 0.10",
				"BASE_NESTR * 0.20",
				"BASE_IRPF * PORCENTAJE_IRPF/100"
				}
				);

		
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
		
		calculator.setListener(new ContractSalaryCalculator.Listener(){
			@Override
			public void onCheckError(IContractPayment payment, String message) {
				System.out.println(message);
			}
		});

		ISalary salary = calculator.calculate(ctx);
		
		Assert.assertEquals(1500.00, salary.getTotalPayment());
		
	}

	@Test
	public void testSalaryData() throws ExpressionException, SQLException, SalaryException {
		
		Connection connection = getConnection();

		AONContext aonContext = new AONContext(connection);
		
		ContractRecord contract = newContract(aonContext, 
				new String[]{
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				},
				new String[]{
				}
				);
		
		cleanSystemData(aonContext);
		addSSRegimeData(aonContext, 
				SSRegimeType.values()[contract.getSsRegime()], 
				getFirstDayOfYear(getToday()), 
				null, 
				new HashMap<String, String>() {
			{
				put("COTIZACION_MENSUAL", "true");
				put("REDUCCION_CGC_E_01", "TRACE('REDUCCION_CGC_E_01 = %f\r\n', 8.10);8.10");
				put("REDUCCION_CGC_E_02", "COTIZACION_MENSUAL ? ((BASE_CGC <= 986.70) ? 6.50 : ((BASE_CGC <= 2595.60) ? (6.50 * ( 1 + (BASE_CGC - 986.70)/ BASE_CGC * 2.52 * 6.15 / 6.50)) : 0.00)) : ((BASE_CGC / JORNADAS_REALES <= 42.90) ? 6.50 : ((BASE_CGC / JORNADAS_REALES <= 112.85 )? (6.50 * ( 1 + (BASE_CGC / JORNADAS_REALES - 42.90) / ( BASE_CGC / JORNADAS_REALES ) * 2.52 * 6.15 / 6.50)) : 0.00))");
				put("PORCENTAJE_CGC_E", "(GRUPO_COTIZACION == \"01\") ? (23.60 - REDUCCION_CGC_E_01) : (17.30 -REDUCCION_CGC_E_02)");
			}
		} );
		
		cleanSystemCosts(aonContext);
		addSSRegimeCost(aonContext, 
				SSRegimeType.values()[contract.getSsRegime()], 
				getFirstDayOfYear(getToday()), 
				"CGC_E", 
				DeductionType.COMMON_CONTINGENCY, 
				"_CUOTA=(( BASE_CGC_E=( BASE_CGC + ( isdef BASE_MTNAD ? BASE_MTNAD : 0.00 ) ) ) * PORCENTAJE_CGC_E/100); (GRUPO_COTIZACION == \"01\") ? (COTIZACION_MENSUAL ? MIN(_CUOTA,279.00): MIN(_CUOTA, 12.13 * JORNADAS_REALES)) : (COTIZACION_MENSUAL ? MAX(_CUOTA,60.25): MAX(_CUOTA,2.62 * JORNADAS_REALES))");
		
		addData(aonContext, contract, getFirstDayOfYear(getToday()), null, new HashMap<String, String>() {
			{
				put("GRUPO_COTIZACION", "\"01\"");
			}
		} );
		
		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);
		
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, start, end, end, contract);

		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder() {
			@Override
			public void addCost(Double amount, String description,
					IDeduction cost, Map<String, ITimedVariable<?>> context) {
				super.addCost(amount, description, cost, context);
				
				Map<String, Object> data = new HashMap<String,Object>();
				load(context, data);

 				Assert.assertEquals("01", data.get("GRUPO_COTIZACION"));
 				
				Assert.assertEquals(true,data.get("COTIZACION_MENSUAL"));
				
				Assert.assertEquals(8.10,data.get("REDUCCION_CGC_E_01"));
				
			}
		});
		

		Salary salary = calculator.calculate(ctx);
		
		
	}
	
	@Test
	public void testSalaryDataII() throws ExpressionException, SQLException, SalaryException {
		
		Connection connection = getConnection();

		AONContext aonContext = new AONContext(connection);
		
		ContractRecord contract = newContract(aonContext, 
				new String[]{
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				},
				new String[]{
				}
				);
		
		cleanSystemData(aonContext);
		addSSRegimeData(aonContext, 
				SSRegimeType.values()[contract.getSsRegime()], 
				getFirstDayOfYear(getToday()), 
				null, 
				new HashMap<String, String>() {
			{
				put("COTIZACION_MENSUAL", "true");
				put("REDUCCION_CGC_E_01", "TRACE('REDUCCION_CGC_E_01 = %f\r\n', 8.10);8.10");
				put("REDUCCION_CGC_E_02", "COTIZACION_MENSUAL ? ((BASE_CGC <= 986.70) ? 6.50 : ((BASE_CGC <= 2595.60) ? (6.50 * ( 1 + (BASE_CGC - 986.70)/ BASE_CGC * 2.52 * 6.15 / 6.50)) : 0.00)) : ((BASE_CGC / JORNADAS_REALES <= 42.90) ? 6.50 : ((BASE_CGC / JORNADAS_REALES <= 112.85 )? (6.50 * ( 1 + (BASE_CGC / JORNADAS_REALES - 42.90) / ( BASE_CGC / JORNADAS_REALES ) * 2.52 * 6.15 / 6.50)) : 0.00))");
				put("PORCENTAJE_CGC_E", "(GRUPO_COTIZACION == \"01\") ? (23.60 - REDUCCION_CGC_E_01) : (17.30 -REDUCCION_CGC_E_02)");
			}
		} );
		
		cleanSystemCosts(aonContext);
		addSSRegimeCost(aonContext, 
				SSRegimeType.values()[contract.getSsRegime()], 
				getFirstDayOfYear(getToday()), 
				"CGC_E", 
				DeductionType.COMMON_CONTINGENCY, 
				"_CUOTA=(( BASE_CGC_E=( BASE_CGC + ( isdef BASE_MTNAD ? BASE_MTNAD : 0.00 ) ) ) * PORCENTAJE_CGC_E/100); (GRUPO_COTIZACION == \"01\") ? (COTIZACION_MENSUAL ? MIN(_CUOTA,279.00): MIN(_CUOTA, 12.13 * JORNADAS_REALES)) : (COTIZACION_MENSUAL ? MAX(_CUOTA,60.25): MAX(_CUOTA,2.62 * JORNADAS_REALES))");
		
		addData(aonContext, contract, getFirstDayOfYear(getToday()), null, new HashMap<String, String>() {
			{
				put("GRUPO_COTIZACION", "\"01\"");
			}
		} );
		
		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);
		
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, start, end, end, contract);

		ContractSalaryCalculator<ISalary> calculator = new ContractSalaryCalculator<ISalary>();

		JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder(connection);
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();
		
		
		try {
			AON.getSalaries(aonContext, props-> props.getContractProperty().eq(contract.getId()))
			.forEach(salary-> {
				Assert.assertEquals(8.10,Double.parseDouble(salary.getContextData().get("REDUCCION_CGC_E_01").get(0).getExpression()));
				throw new SuccessException();
			});
		} catch ( SuccessException e ){
			return;
		}
		
		Assert.fail();
	}

	private static void load(Map<String, ITimedVariable<?>> context, Map<String, Object> data){
		for (Entry<String, ITimedVariable<?>> entry : context.entrySet()) {
			ITimedVariable<?> var = entry.getValue();
			
			Object value = var.getValue(var.getPeriod());
			System.out.println("CONTEXT : "+ entry.getKey() + " = " + value);
			data.put(entry.getKey(), value);
			if ( var instanceof ITimedResult<?> )
				load(((ITimedResult<?>)var).getContext(), data);
			if ( var instanceof IExpressionVariable<?> )
				load(((IExpressionVariable<?>)var).getContext(), data);
		}
	}
	
	private static class SuccessException extends RuntimeException {
		
	}
	
	
}
