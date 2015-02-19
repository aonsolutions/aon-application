/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.AGREEMENT_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FRIDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PARTIAL_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WEDNESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WEEK_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C100;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C109;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C130;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C139;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C150;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C189;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C200;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C209;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C230;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C239;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C250;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C289;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C401;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C402;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C403;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C408;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C410;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C418;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C420;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C421;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C430;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C441;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C450;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C452;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C501;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C502;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C503;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C508;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C510;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C518;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C520;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C530;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C540;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C541;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C550;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C552;
import static java.lang.String.format;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.YEAR;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;

/**
 * @author rtrepiana
 *
 */
public class SQLContractSalaryCalculatorContextTestCase extends
		AbstractSQLTestCase {

	private static final double DELTA = 0.000000001;

	private static ContractCode PARTIAL_TIME[] = { 
		C200, C209, C230, C239, C250, C289, 
		C501, C502, C503, C508, C510, C518, C520, C530, C540, C541,C550, C552, 
	};

	private static  ContractCode FULL_TIME[] = { 
		C100, C109, C130, C139, C150, C189, 									// indefinite fulltime
		C401, C402, C403, C408, C410, C418, C420, C421, C430, C441, C450, C452, // partial & temp fulltime
	};

	@Test
	public void testFullTimeWorkDaysI() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection, "", 0);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, getToday(),
				new HashMap<String, String>() {
					{
						put(TC2.getName(),
								format("\"%s\"", random(FULL_TIME).getValue()));
					}
				});

		testWorkedDays(contract, 1d);
	
	}

	@Test
	public void testPartialTimeWorkDaysI() throws ExpressionException,
			SQLException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection, "", 0);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, getToday(),
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(),
								format("\"%s\"", random(PARTIAL_TIME).getValue()));
						put(MONDAY_HOURS.getName(), format("%d", 0));
						put(FRIDAY_HOURS.getName(), format("%d", 0));
						put(WEDNESDAY_HOURS.getName(), format("%d", 4));
					}
				});

		testWorkedDays(contract, 0.5);
	}

	@Test
	public void testPartialTimeWorkDaysII() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection, "", 0);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, getToday(),
				new HashMap<String, String>() {
					{
						put(TC2.getName(),
								format("\"%s\"", random(PARTIAL_TIME).getValue()));
						put(WEEK_HOURS.getName(),
								format("%d", 15));
						put(AGREEMENT_HOURS.getName(),
								format("%d", 35));
					}
				});

		testWorkedDays(contract, (double)(15d / 35d));


	}

	@Test
	public void testPartialTimeWorkDaysIII() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection, "", 0);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, getToday(),
				new HashMap<String, String>() {
					{
						put(TC2.getName(),
								format("\"%s\"", random(PARTIAL_TIME).getValue()));
						put(PARTIAL_FACTOR.getName(),
								format("%f", 0.69));
					}
				});

		testWorkedDays(contract, 0.69);


	}

	@Test
	public void testPartialTimeWorkDaysIV() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection, "", 0);

		Date contractStart = getToday();

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, contractStart,
				new HashMap<String, String>() {
					{
						put(TC2.getName(),
								format("\"%s\"", random(PARTIAL_TIME).getValue()));
						put(WEEK_HOURS.getName(),
								format("%d", 15));
						put(AGREEMENT_HOURS.getName(),
								format("%d", 35));
					}
				});

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		//

		Date start = getFirstDayOfMonth(addMonths(contractStart, 1));
		Date end = getLastDayOfMonth(start);
		
		Date change = addDays(start, Math.max(2,(int) (Math.random() * getDayOfMonth(end)))); 
		int changeDayOfMonth = getDayOfMonth(change);
		
		addData(aonContext, contract, change, null,
				new HashMap<String, String>() {
					private static final long serialVersionUID = 1L;

					{
						put(ContextVariable.WEEK_HOURS.getName(),
								format("%d", 10));
					}
				});


		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);
		ctx.next();
		List<ITimedResult<Double>> workedDays = ctx.getExpressionContext()
				.eval(format("%s", WORKED_DAYS), start, end, Double.class);

		Assert.assertEquals(2, workedDays.size());
		assertEquals(
				workedDays.get(0),
				(double) ((changeDayOfMonth - 1) * 15d / 35d),
				start, 
				addDays(change, -1));

		assertEquals(
				workedDays.get(1),
				(double) ((getDayOfMonth(end)-changeDayOfMonth +1) * 10d / 35d),
				change, 
				end);

	}
	
	
	protected void testWorkedDays(ContractRecord contract, double coefficient) throws ExpressionException, SQLException {
		Connection connection = getConnection();

		Date contractStart = contract.getStartDate();
		long contractStartDayOfMonth = getDayOfMonth(contractStart);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		// First of month of contract, 99% will be partial
		Date start = getFirstDayOfMonth();
		Date end = getLastDayOfMonth();
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);
		ctx.next();
		assertEquals(ctx, 
				(double) ((getDayOfMonth(end) - contractStartDayOfMonth + 1) * coefficient),  
				contractStart, 
				end);

		// 95% of cases . Whole month
		start = getFirstDayOfMonth(addMonths(start, 1));
		end = getLastDayOfMonth(start);
		ctx = new SQLContractSalaryCalculatorContext(connection, start, end,
				end, criteria);
		ctx.next();
		assertEquals(ctx, 
				(double) (getDayOfMonth(end) * coefficient), 
				start, 
				end);

		// Extras. First year, almost all the times will be partial. 
		start = getFirstDayOfYear(contractStart);
		end = getLastDayOfYear(start);
		ctx = new SQLContractSalaryCalculatorContext(connection, start, end,
				end, criteria);
		ctx.next();
		assertEquals(ctx, 
				(double) ((get(end, DAY_OF_YEAR) - get(contractStart, DAY_OF_YEAR) + 1) * coefficient), 
				contractStart, 
				end);
		
		// Extras. Second year. This will be whole 
		start = getFirstDayOfYear(add(start, YEAR,1));
		end = getLastDayOfYear(start);
		ctx = new SQLContractSalaryCalculatorContext(connection, start, end,
				end, criteria);
		ctx.next();
		assertEquals(ctx, 
				(double) get(end, DAY_OF_YEAR) * coefficient, 
				start, 
				end);
		
		// Extras. Second/Third year. This will be whole 
		start = addMonths(getFirstDayOfYear(add(start, YEAR,1)), 6) ;
		end = add(addMonths(start, 11), DAY_OF_MONTH, -1);
		ctx = new SQLContractSalaryCalculatorContext(connection, start, end,
				end, criteria);
		ctx.next();
		assertEquals(ctx, 
				(double) ( get(end, DAY_OF_YEAR) + ( get(getLastDayOfYear(start), DAY_OF_YEAR) - get(start, DAY_OF_YEAR)) + 1 ) * coefficient, 
				start, 
				end);
		
	}
	
	// ------------------------------------------------------------------------
	
	
	protected static <T> T random(T arr[]) {
		return arr[(int) ((int) (Math.random() * arr.length))];
	}
	
	protected void assertEquals(ITimedResult<Double> var, Double value, Date start, Date end){
		Assert.assertEquals(value, var.getValue(), DELTA);
		Assert.assertEquals(new Period(start, end), var.getPeriod());
	}

	protected void assertEquals(List<ITimedResult<Double>> vars, Double value, Date start, Date end){
		Assert.assertEquals(1, vars.size());
		Assert.assertEquals(value, vars.get(0).getValue(), DELTA);
		Assert.assertEquals(new Period(start, end), vars.get(0).getPeriod());
	}

	protected void assertEquals(SQLContractSalaryCalculatorContext ctx, Double value, Date start, Date end) throws UndefinedVariablesException, ExpressionException{
		List<ITimedResult<Double>> workedDays = ctx.getExpressionContext()
				.eval(format("%s", WORKED_DAYS), start, end, Double.class);
		Assert.assertEquals(1, workedDays.size());
		Assert.assertEquals(value, workedDays.get(0).getValue(), DELTA);
		Assert.assertEquals(new Period(start, end), workedDays.get(0).getPeriod());
	}
	
	
}
