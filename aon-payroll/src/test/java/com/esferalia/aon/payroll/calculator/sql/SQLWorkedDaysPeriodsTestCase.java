/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.PARTIAL_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C200;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.lang.String.format;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.Period;


/**
 * @author rtrepiana
 *
 */
public class SQLWorkedDaysPeriodsTestCase extends SQLAbstractPeriodsTestCase {
	
	@Override
	public ContextVariable getDaysVariable() {
		return WORKED_DAYS;
	}

	@Test
	@Override
	public void testPartialTimeQuoteDaysfactorI() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext, getToday(),
				new HashMap<String, String>() {
					{
						put(TC2.getName(), C200.getValue());
						put(PARTIAL_FACTOR.getName(), "0.50");
					}
				});

		Date startDate = getFirstDayOfMonth(add(getToday(), MONTH, 1));
		Date endDate = getLastDayOfMonth(add(getToday(), MONTH, 1));

		Date factorChangeDate = add(startDate, DAY_OF_MONTH, 13);

		addData(aonContext, contract, factorChangeDate, null,
				PARTIAL_FACTOR, 0.25);

		List<ITimedResult<Double>> quoteDays = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract)
				.getExpressionContext().eval(format("%s", getDaysVariable()),
						startDate, endDate, Double.class);

		assertEquals(2, quoteDays.size());
		assertEquals((double) (get(factorChangeDate, DAY_OF_MONTH)-1)*0.50, 
				quoteDays.get(0).getValue(),
				DELTA,getDaysVariable().getName());
		assertEquals( 
				quoteDays.get(0).getPeriod(),
				new Period(startDate, add(factorChangeDate, DAY_OF_MONTH,-1)),getDaysVariable().getName());
		assertEquals((double) (get(endDate, DAY_OF_MONTH) - get(factorChangeDate, DAY_OF_MONTH) +1 ) * 0.25, 
				quoteDays.get(1).getValue(),
				DELTA,getDaysVariable().getName());
		assertEquals(quoteDays.get(1).getPeriod(),
				new Period(factorChangeDate, endDate),getDaysVariable().getName());
	}
	
	@Test
	@Override
	public void testPartialTimeQuoteDaysfactorII() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext, getToday(),
				new HashMap<String, String>() {
					{
						put(TC2.getName(), C200.getValue());
						put(PARTIAL_FACTOR.getName(), "0.50");
					}
				});

		Date startDate = getFirstDayOfMonth(add(getToday(), MONTH, 1));
		Date endDate = getLastDayOfMonth(add(getToday(), MONTH, 1));

		Date factorNOChangeDate = add(startDate, DAY_OF_MONTH, 13);

		addData(aonContext, contract, factorNOChangeDate, null,
				PARTIAL_FACTOR, 0.50);

		List<ITimedResult<Double>> quoteDays = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract)
				.getExpressionContext().eval(format("%s", getDaysVariable()),
						startDate, endDate, Double.class);

		assertEquals(1, quoteDays.size());
		assertEquals(				(double) get(endDate, DAY_OF_MONTH) * 0.50, 
				quoteDays.get(0).getValue(),
				DELTA,getDaysVariable().getName());
		assertEquals(
				quoteDays.get(0).getPeriod(),
				new Period(startDate, endDate),getDaysVariable().getName());
	}
}
