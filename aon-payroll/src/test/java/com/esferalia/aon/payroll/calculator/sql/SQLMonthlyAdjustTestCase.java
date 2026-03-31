/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;

/**
 * @author rtrepiana
 *
 */
public class SQLMonthlyAdjustTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000000001;


	@Test
	public void testDropDay() throws ExpressionException,
			SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		
		addSSRegimeData(
			aonContext, 
			SSRegimeType.GENERAL, 
			getFirstDayOfYear(getToday())
			, null
			, new HashMap<String,String>() {
			    {
				put("BASE_HORARIA","false"); 
				
				put(ContextVariable.CGC_BASE_MIN.getName(), 
				"[ \"01\":(MAX(8.33, (BASE_HORARIA ? 9.82 * HORAS_TRABAJADAS : 1629.30 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD))) "
				+ ",\"02\":(MAX(8.14, (BASE_HORARIA ? 8.14 * HORAS_TRABAJADAS : 1351.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD))) "
				+ ",\"03\":(MAX(7.08, (BASE_HORARIA ? 7.08 * HORAS_TRABAJADAS : 1175.40 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD))) "
				+ ",\"04\":(MAX(7.03, (BASE_HORARIA ? 7.03 * HORAS_TRABAJADAS : 1166.70 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD))) "
				+ ",\"05\":(MAX(7.03, (BASE_HORARIA ? 7.03 * HORAS_TRABAJADAS : 1166.70 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD))) "
				+ ",\"06\":(MAX(7.03, (BASE_HORARIA ? 7.03 * HORAS_TRABAJADAS : 1166.70 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD))) "
				+ ",\"07\":(MAX(7.03, (BASE_HORARIA ? 7.03 * HORAS_TRABAJADAS : 1166.70 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD))) "
				+ ",\"08\":(MAX(7.03, (BASE_HORARIA ? 7.03 * HORAS_TRABAJADAS : (MODALIDAD_MENSUAL ? 1166.70 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD : 38.89 * DIAS_NOMINA * COEFICIENTE_PARCIALIDAD)))) "
				+ ",\"09\":(MAX(7.03, (BASE_HORARIA ? 7.03 * HORAS_TRABAJADAS : (MODALIDAD_MENSUAL ? 1166.70 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD : 38.89 * DIAS_NOMINA * COEFICIENTE_PARCIALIDAD)))) "
				+ ",\"10\":(MAX(7.03, (BASE_HORARIA ? 7.03 * HORAS_TRABAJADAS : (MODALIDAD_MENSUAL ? 1166.70 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD : 38.89 * DIAS_NOMINA * COEFICIENTE_PARCIALIDAD)))) "
				+ ",\"11\":(MAX(7.03, (BASE_HORARIA ? 7.03 * HORAS_TRABAJADAS : (MODALIDAD_MENSUAL ? 1166.70 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD : 38.89 * DIAS_NOMINA * COEFICIENTE_PARCIALIDAD))))"
				+ "]"
				+ "[GRUPO_COTIZACION]" );
				put(ContextVariable.MONTH_DAYS.getName(), 
				"[ \"01\":30"
				+ ", \"02\":30"
				+ ", \"03\":30"
				+ ", \"04\":30"
				+ ", \"05\":30"
				+ ", \"06\":30"
				+ ", \"07\":30"
				+ ", \"08\": DIAS_NATURALES_MES"
				+ ", \"09\": DIAS_NATURALES_MES"
				+ ", \"10\": DIAS_NATURALES_MES"
				+ ", \"11\": DIAS_NATURALES_MES"
				+ "]"
				+ "[GRUPO_COTIZACION]");
			    }
			});
		addSSRegimePayment(
			aonContext, 
			SSRegimeType.GENERAL, 
			getFirstDayOfYear(getToday()), 
			PaymentType.CRA_0001, "/*read_only*/DIAS_AUSENCIA * 0.00/**/", "BASE_CGC_MIN", "_P");
		
		Date contractStartDate = getFirstDayOfYear(getToday());
		//@formatter:off
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				contractStartDate,
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), "\"100\"");
						put(ContextVariable.QUOTE_GROUP.getName(), "\"01\"");
					}
				},
				new String[] {
				"2000.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"200.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				}, 
				new String[] {						
				},
				null);
		//@formatter:on
		
		
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		Date dropDay = add(startDate, Calendar.DAY_OF_MONTH, 11); 
		addData(aonContext, contract, dropDay, dropDay, ContextVariable.DROP_FACTOR, "100.00");

		Salary salary = 
		new SmartContractSalaryCalculator<Salary>( new SalaryBuilder() {
		    @Override
		    public void addPayment(Double amount, Double quote, Double tax, String description,
		            java.util.Date startDate, java.util.Date endDate, IPayment payment,
		            Map<String, ITimedVariable<?>> context) {
		        System.out.println( description + " = " + amount+ ", " + quote + " [" + startDate +".." + endDate + "] ");
		        super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
		    }
		})
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		assertEquals(2200.00 / 30 * 29 + 1629.30/30.00, salary.getCommonBase() , DELTA );
		assertEquals(2200.00 / 30 * 29, salary.getTotalPayment() , DELTA ); // ???
		
	}

	@Test
	public void test31DaysMonth() throws ExpressionException,
			SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		
		addSSRegimeData(
			aonContext, 
			SSRegimeType.GENERAL, 
			getFirstDayOfYear(getToday())
			, null
			, new HashMap<String,String>() {
			    {
				put(ContextVariable.MONTH_DAYS.getName(), 
				"[ \"01\":30"
				+ ", \"02\":30"
				+ ", \"03\":30"
				+ ", \"04\":30"
				+ ", \"05\":30"
				+ ", \"06\":30"
				+ ", \"07\":30"
				+ ", \"08\": DIAS_NATURALES_MES"
				+ ", \"09\": DIAS_NATURALES_MES"
				+ ", \"10\": DIAS_NATURALES_MES"
				+ ", \"11\": DIAS_NATURALES_MES"
				+ "]"
				+ "[GRUPO_COTIZACION]");
			    }
			});

		Date contractStartDate = getFirstDayOfYear(getToday());
		//@formatter:off
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				contractStartDate,
				new HashMap<String, String>() {
					{
					}
				},
				new String[] {
				"2000.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"200.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				}, 
				new String[] {						
				},
				null);
		//@formatter:on
		
		
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		Date periodDay = add(startDate, Calendar.DAY_OF_MONTH, 11); 
		addData(aonContext, contract, startDate, periodDay, ContextVariable.QUOTE_GROUP, "\"01\"");
		addData(aonContext, contract, add(periodDay, Calendar.DAY_OF_MONTH,1), endDate, ContextVariable.QUOTE_GROUP, "\"02\"");

		Salary salary = 
		new SmartContractSalaryCalculator<Salary>( new SalaryBuilder() {
		    @Override
		    public void addPayment(Double amount, Double quote, Double tax, String description,
		            java.util.Date startDate, java.util.Date endDate, IPayment payment,
		            Map<String, ITimedVariable<?>> context) {
		        System.out.println( description + " = " + amount+ ", " + quote + " [" + startDate +".." + endDate + "] ");
		        super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
		    }
		})
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		assertEquals(2200.00 , salary.getCommonBase() , DELTA );
		assertEquals(2200.00 , salary.getTotalPayment() , DELTA ); // ???
		
	}


}