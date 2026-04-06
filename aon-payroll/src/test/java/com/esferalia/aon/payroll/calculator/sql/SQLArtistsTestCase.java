/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author rtrepiana
 *
 */
public class SQLArtistsTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.01;



	@Test
	public void testCRA0042CRA0050()
			throws ExpressionException, SQLException, SalaryException {
		
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date today = getToday();
		
		int todayDay = AonDateUtils.get(today, Calendar.DAY_OF_MONTH);
		int endMonthDay = AonDateUtils.getMax(today, Calendar.DAY_OF_MONTH);
		
		Date contractStartDate = addDays(today, Math.min(10, endMonthDay - todayDay));
		Date contractEndDate = addDays(contractStartDate, 5);
		
		ContractRecord contract = newContract(
				aonContext,
				SSRegimeType.ARTIST,
				CCCType.ARTIST,
				contractStartDate,
				contractEndDate,
				Collections.emptyMap(),
				new String[] { 
				},
				new String[] { 
				},
				null
				);
		
		addPayment(aonContext, 
				contract, 
				"SALARIO_BASE", 
				"500.00", 
				"_P", 
				"_P", 
				PaymentType.CRA_0001);
		

		addPayment(aonContext, 
				contract, 
				"DIETAS", 
				"111.11", 
				"_P", 
				"0.00", 
				PaymentType.CRA_0042);
		addPayment(aonContext, 
				contract, 
				"DIETAS", 
				"111.11", 
				"_P", 
				"0.00", 
				PaymentType.CRA_0043);
		addPayment(aonContext, 
				contract, 
				"DIETAS", 
				"111.11", 
				"_P", 
				"0.00", 
				PaymentType.CRA_0043);
		addPayment(aonContext, 
				contract, 
				"DIETAS", 
				"111.11", 
				"_P", 
				"0.00", 
				PaymentType.CRA_0046);
		addPayment(aonContext, 
				contract, 
				"DIETAS", 
				"111.11", 
				"_P", 
				"0.00", 
				PaymentType.CRA_0048);
		addPayment(aonContext, 
				contract, 
				"DIETAS", 
				"111.11", 
				"_P", 
				"0.00", 
				PaymentType.CRA_0050);
		

		Date start = getFirstDayOfMonth(today);
		Date end = getLastDayOfMonth(today);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);

		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		
		ISalary salary = calculator.calculate(ctx);
		
		assertEquals(666.66 + 500.00, salary.getCommonBase(), DELTA);
		assertEquals(666.66 + 500.00, salary.getProfessionalBase(), DELTA);
		
		
	}
	

}
