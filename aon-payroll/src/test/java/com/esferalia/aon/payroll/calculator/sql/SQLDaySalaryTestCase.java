/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C100;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedResult;

/**
 * @author rtrepiana
 *
 */
public class SQLDaySalaryTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000000001;


	@Test
	public void testDaySalaryI() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date contractStartDate = getFirstDayOfMonth(getToday());
		//@formatter:off
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				contractStartDate,
				new HashMap<String, String>() {
					{
						put(TC2.getName(), String.format("\"%s\"", C100.getValue()));
					}
				},
				new String[] {
				"22000.00 / 12 * DIAS_TRABAJADOS / DIAS_MES" ,
				"220.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"22.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"2.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				}, 
				new String[] {						
				"BASE_CGC * 4.60 / 100.00", 
				"BASE_CGC * 1.50 / 100.00", 
				"BASE_CGC * 0.10 / 100.00", 
				"BASE_IRPF * PORCENTAJE_IRPF/100.00" 
				},
				null);
		//@formatter:on
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		ISQLContractSalaryCalculatorContext ctx = 
				getSQLContractSettleContext(
				connection, 
				startDate, 
				contract);
		
		List<ITimedResult<Double>> daySalaries = 
				ctx.getExpressionContext().eval("SALARIO_DIA", startDate, endDate, Double.class);
		assertEquals(1, daySalaries.size());
		assertEquals( ( 22000.00 / 12  + 220.00 + 22 + 2 ) * 12 /365 , daySalaries.get(0).getValue(), DELTA);
	}

	@Test
	public void testDaySalaryII() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date contractStartDate = add(getFirstDayOfMonth(getToday()), Calendar.DAY_OF_MONTH, 10);
		//@formatter:off
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				contractStartDate,
				new HashMap<String, String>() {
					{
						put(TC2.getName(), String.format("\"%s\"", C100.getValue()));
					}
				},
				new String[] {
				"22000.00 / 12 * DIAS_TRABAJADOS / DIAS_MES" ,
				"220.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"22.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"2.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				}, 
				new String[] {						
				"BASE_CGC * 4.60 / 100.00", 
				"BASE_CGC * 1.50 / 100.00", 
				"BASE_CGC * 0.10 / 100.00", 
				"BASE_IRPF * PORCENTAJE_IRPF/100.00" 
				},
				null);
		//@formatter:on
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		ISQLContractSalaryCalculatorContext ctx = 
				getSQLContractSettleContext(
				connection, 
				startDate,
				endDate,
				contract);
		
		List<ITimedResult<Double>> daySalaries = 
				ctx.getExpressionContext().eval("SALARIO_DIA", startDate, endDate, Double.class);
		assertEquals(1, daySalaries.size());
		assertEquals( ( 22000.00 / 12  + 220.00 + 22 + 2 ) * 12 /365 , daySalaries.get(0).getValue(), DELTA);
	}


}