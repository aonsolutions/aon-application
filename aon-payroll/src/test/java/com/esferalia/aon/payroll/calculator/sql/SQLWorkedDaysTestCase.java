/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.AGREEMENT_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FRIDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PARTIAL_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SATURDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRIKE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SUNDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.THURSDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TUESDAY_HOURS;
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
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getMax;
import static java.lang.String.format;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;
import com.esferalia.aon.watson.util.AonDateUtils;

/**
 * @author rtrepiana
 *
 */
public class SQLWorkedDaysTestCase extends
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
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, getToday(),
				new HashMap<String, String>() {
					{
						put(TC2.getName(),
								format("\"%s\"", random(FULL_TIME).getValue()));
					}
				});

		testWorkedDays(contract, 1d, null);
	
	}

	@Test
	public void testFullTimeWorkDaysII() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, getToday(),
				new HashMap<String, String>() {
					{
						put(TC2.getName(),
								format("\"%s\"", random(FULL_TIME).getValue()));
						put(MONTH_DAYS.getName(),
								format("%d", 20));
					}
				});

		testWorkedDays(contract, 1d, 20.00);
	
	}

	@Test
	public void testFullTimeWorkDaysIII() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, getToday(),
				new HashMap<String, String>() {
					{
						put(TC2.getName(),
								format("\"%s\"", random(FULL_TIME).getValue()));
					}
				});
		
		addData(aonContext, contract, getToday(), getToday(), 
			new HashMap<String, String>() {
			{
				put(STRIKE_DAYS.getName(),format("%d", 1));
			}
		});
				

		Date contractStart = contract.getStartDate();
		long contractStartDayOfMonth = get(contractStart,DAY_OF_MONTH);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);
		ctx.next();
		assertEquals(ctx, 
				(double) ((get(end, DAY_OF_MONTH) - contractStartDayOfMonth + 1 -1) ),  
				contractStart, 
				end,
				null);
	
	}

	@Test
	public void testPartialTimeWorkDaysI() throws ExpressionException,
			SQLException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

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

		testWorkedDays(contract, 0.5, null);
	}

	@Test
	public void testPartialTimeWorkDaysII() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

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

		testWorkedDays(contract, (double)(15d / 35d), null);


	}

	@Test
	public void testPartialTimeWorkDaysIII() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

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

		testWorkedDays(contract, 0.69, null);


	}

	@Test
	public void testPartialTimeWorkDaysIV() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

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
		
		Date change = addDays(start, Math.max(2,(int) (Math.random() * get(end,DAY_OF_MONTH)))); 
		int changeDayOfMonth = get(change,DAY_OF_MONTH);
		
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
				(double) ((get(end, DAY_OF_MONTH)-changeDayOfMonth +1) * 10d / 35d),
				change, 
				end);

	}
	
	
	
	@Test
	public void testContextWorkDaysI() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date start = getFirstDayOfYear(getToday());

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, start,
				new HashMap<String, String>() {
					{
						put(TC2.getName(),
								format("\"%s\"", random(PARTIAL_TIME).getValue()));
					}
				});

		

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		Date end = getLastDayOfMonth(start);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);
		ctx.next();
		
		ctx.getExpressionContext()
				.eval("100.00 * DIAS_TRABAJADOS / DIAS_MES ", start, end)
				.stream()
				.map(result->result.getContext())
				.forEach(context->{
					Assert.assertTrue(String.format(MONTH_DAYS.getName()),context.containsKey(MONTH_DAYS.getName()));
					Assert.assertTrue(String.format(WORKED_DAYS.getName()),context.containsKey(WORKED_DAYS.getName()));
					Assert.assertTrue(String.format(TC2.getName()),context.containsKey(TC2.getName()));
					Assert.assertTrue(String.format(PARTIAL_FACTOR.getName()),context.containsKey(PARTIAL_FACTOR.getName()));
					Assert.assertTrue(String.format(ERE_FACTOR.getName()),context.containsKey(ERE_FACTOR.getName()));
					Assert.assertTrue(String.format(STRIKE_DAYS.getName()),context.containsKey(STRIKE_DAYS.getName()));
					
					Assert.assertTrue(String.format(WEEK_HOURS.getName()),context.containsKey(WEEK_HOURS.getName()));
					Assert.assertTrue(String.format(MONDAY_HOURS.getName()),context.containsKey(MONDAY_HOURS.getName()));
					Assert.assertTrue(String.format(TUESDAY_HOURS.getName()),context.containsKey(TUESDAY_HOURS.getName()));
					Assert.assertTrue(String.format(WEDNESDAY_HOURS.getName()),context.containsKey(WEDNESDAY_HOURS.getName()));
					Assert.assertTrue(String.format(THURSDAY_HOURS.getName()),context.containsKey(THURSDAY_HOURS.getName()));
					Assert.assertTrue(String.format(FRIDAY_HOURS.getName()),context.containsKey(FRIDAY_HOURS.getName()));
					Assert.assertTrue(String.format(SATURDAY_HOURS.getName()),context.containsKey(SATURDAY_HOURS.getName()));
					Assert.assertTrue(String.format(SUNDAY_HOURS.getName()),context.containsKey(SUNDAY_HOURS.getName()));
					
					Assert.assertTrue(String.format(AGREEMENT_HOURS.getName()),context.containsKey(AGREEMENT_HOURS.getName()));
					
				});
		
	
	}

	@Test
	public void testWorkDaysWithIT() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date contractStart = getToday();

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, contractStart,
				new HashMap<String, String>() {
					{
					}
				});

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		Date start = getFirstDayOfMonth(addMonths(contractStart, 1));
		Date end = getLastDayOfMonth(start);
		
		Date startIT = add(start, DAY_OF_MONTH, 13 );
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startIT, null, null);
		
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);
		ctx.next();

		List<ITimedResult<Double>> workedDays = ctx.getExpressionContext()
				.eval(format("%s", WORKED_DAYS), start, end, Double.class);

		Assert.assertEquals(1, workedDays.size());
		assertEquals(
				workedDays.get(0),
				13.00,
				start, 
				add(startIT,DAY_OF_MONTH,-1));
	}
	

	@Test
	public void testWorkDaysWithITII() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date contractStart = getToday();

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, contractStart,
				new HashMap<String, String>() {
					{
					}
				});

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		Date start = getFirstDayOfMonth(addMonths(contractStart, 1));
		Date end = getLastDayOfMonth(start);
		
		Date startIT = add(start, DAY_OF_MONTH, 13 );
		Date endIT = add(startIT, DAY_OF_MONTH, 9 );
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startIT, endIT, null);
		
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);
		ctx.next();

		List<ITimedResult<Double>> workedDays = ctx.getExpressionContext()
				.eval(format("%s", WORKED_DAYS), start, end, Double.class);

		Assert.assertEquals(2, workedDays.size());
		assertEquals(
				workedDays.get(0),
				13.00,
				start, 
				add(startIT,DAY_OF_MONTH,-1));
		assertEquals(
				workedDays.get(1),
				(double) (get(end,DAY_OF_MONTH)-get(endIT, DAY_OF_MONTH)),
				add(endIT,DAY_OF_MONTH,+1), 
				end);
	}
	
	@Test
	public void testWorkDaysWithITIII() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date contractStart = getToday();

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, contractStart,
				new HashMap<String, String>() {
					{
					}
				});

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		Date start = getFirstDayOfMonth(addMonths(contractStart, 1));
		Date end = getLastDayOfMonth(start);
		
		Date startITI = add(start, DAY_OF_MONTH, 5 );
		Date endITI =startITI;
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITI, endITI, null);

		Date startITII = add(start, DAY_OF_MONTH, 7 );
		Date endITII =startITII;
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITII, endITII, null);

		Date startITIII = add(start, DAY_OF_MONTH, 9 );
		Date endITIII =startITIII;
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITIII, endITIII, null);

		Date startITIV = add(start, DAY_OF_MONTH, 11 );
		Date endITIV =startITIV;
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITIV, endITIV, null);

		Date startITV = add(start, DAY_OF_MONTH, 20 );
		Date endITV =add(startITV, DAY_OF_MONTH, 20 );
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITV, endITV, null);

		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);
		ctx.next();

		List<ITimedResult<Double>> workedDays = ctx.getExpressionContext()
				.eval(format("%s", WORKED_DAYS), start, end, Double.class);

		Assert.assertEquals(5, workedDays.size());
		assertEquals(
				workedDays.get(0),
				5.00,
				start, 
				add(startITI,DAY_OF_MONTH,-1));
		assertEquals(
				workedDays.get(1),
				1.00,
				add(endITI,DAY_OF_MONTH,1), 
				add(endITI,DAY_OF_MONTH,1));
		assertEquals(
				workedDays.get(2),
				1.00,
				add(endITII,DAY_OF_MONTH,1), 
				add(endITII,DAY_OF_MONTH,1));
		assertEquals(
				workedDays.get(3),
				1.00,
				add(endITIII,DAY_OF_MONTH,1), 
				add(endITIII,DAY_OF_MONTH,1));
		assertEquals(
				workedDays.get(4),
				8.00,
				add(endITIV,DAY_OF_MONTH,1), 
				add(startITV,DAY_OF_MONTH,-1));
	}

	@Test
	public void testWorkDaysWithITIV() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date contractStart = getToday();

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, contractStart,
				new HashMap<String, String>() {
					{
					}
				});

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		Date start = getFirstDayOfMonth(addMonths(contractStart, 1));
		Date end = getLastDayOfMonth(start);
		
		Date startIT = start;
		Date endIT = null;
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startIT, endIT, null);
		
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);
		ctx.next();
		
		try {
			List<ITimedResult<Double>> workedDays = ctx.getExpressionContext()
					.eval(format("%s", WORKED_DAYS), start, end, Double.class);
			Assert.fail();
		} catch ( UndefinedVariablesException e){
			
		}
	}

	// ------------------------------------------------------------------------
	protected void testWorkedDays(ContractRecord contract, double coefficient, Double monthdays) throws ExpressionException, SQLException {
		Connection connection = getConnection();

		Date contractStart = contract.getStartDate();
		long contractStartDayOfMonth = get(contractStart,DAY_OF_MONTH);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		// First of month of contract, 99% will be partial
		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);
		ctx.next();
		assertEquals(ctx, 
				(double) ((get(end, DAY_OF_MONTH) - contractStartDayOfMonth + 1) * coefficient),  
				contractStart, 
				end,
				monthdays);

		// 95% of cases . Whole month
		start = getFirstDayOfMonth(addMonths(start, 1));
		end = getLastDayOfMonth(start);
		ctx = new SQLContractSalaryCalculatorContext(connection, start, end,
				end, criteria);
		ctx.next();
		assertEquals(ctx, 
				(double) (get(end,DAY_OF_MONTH) * coefficient ), 
				start, 
				end,
				monthdays);

		// Extras. First year, almost all the times will be partial. 
		start = getFirstDayOfYear(contractStart);
		end = getLastDayOfYear(start);
		ctx = new SQLContractSalaryCalculatorContext(connection, start, end,
				end, criteria);
		ctx.next();
		
		assertEquals(ctx, 
				(double) ((get(end, DAY_OF_YEAR) - get(contractStart, DAY_OF_YEAR) + 1) * coefficient ), 
				contractStart, 
				end,
				monthdays);
		
		// Extras. Second year. This will be whole 
		start = getFirstDayOfYear(add(start, YEAR,1));
		end = getLastDayOfYear(start);
		ctx = new SQLContractSalaryCalculatorContext(connection, start, end,
				end, criteria);
		ctx.next();
		assertEquals(ctx, 
				(double) get(end, DAY_OF_YEAR) * coefficient, 
				start, 
				end,
				monthdays);
		
		// Extras. Second/Third year. This will be whole 
		start = addMonths(getFirstDayOfYear(add(start, YEAR,1)), 6) ;
		end = add(addMonths(start, 11), DAY_OF_MONTH, -1);
		ctx = new SQLContractSalaryCalculatorContext(connection, start, end,
				end, criteria);
		ctx.next();
		assertEquals(ctx, 
				(double) ( get(end, DAY_OF_YEAR) + ( get(getLastDayOfYear(start), DAY_OF_YEAR) - get(start, DAY_OF_YEAR)) + 1 ) * coefficient, 
				start, 
				end,
				monthdays);
		
	}

	
	// ------------------------------------------------------------------------
	
	
	protected static <T> T random(T arr[]) {
		return arr[(int) ((int) (Math.random() * arr.length))];
	}
	
	protected void assertEquals(ITimedResult<Double> var, Double value, Date start, Date end){
		Assert.assertEquals(new Period(start, end), var.getPeriod());
		Assert.assertEquals(value, var.getValue(), DELTA);
	}

	protected void assertEquals(List<ITimedResult<Double>> vars, Double value, Date start, Date end){
		Assert.assertEquals(1, vars.size());
		Assert.assertEquals(value, vars.get(0).getValue(), DELTA);
		Assert.assertEquals(new Period(start, end), vars.get(0).getPeriod());
	}
	
	
	protected void assertEquals(SQLContractSalaryCalculatorContext ctx, Double value, Date start, Date end, Double monthDays ) throws UndefinedVariablesException, ExpressionException{
		List<ITimedResult<Double>> workedDays = ctx.getExpressionContext()
				.eval(format("%s", WORKED_DAYS), start, end, Double.class);
		
		int months = 0;
		for( Date date = getFirstDayOfMonth(start); date.compareTo(end)<= 0; date = add(date, MONTH, 1))
			months++;
		
		System.out.printf("%tF..%tF ( %d ): \r\n", start, end , months );
		Assert.assertEquals(months, workedDays.size());
		
		double values = 0.00; 
		for (ITimedResult<Double> workedDay : workedDays){
			double ctxMonthDays = ctx.getVariable(MONTH_DAYS, workedDay.getPeriod(), Number.class).doubleValue() ;
			if ( monthDays != null ) 
				Assert.assertEquals(monthDays, ctxMonthDays, DELTA);
			System.out.printf("\t%tF..%tF : %f ( %f month days)\r\n", workedDay.getPeriod().getStart(), workedDay.getPeriod().getEnd() , workedDay.getValue(), ctxMonthDays );
			
			values += workedDay.getValue() * ( monthDays != null ? getMax(workedDay.getPeriod().getStart(), DAY_OF_MONTH)/monthDays : 1.00 ) ;
		}
		System.out.printf("%f == %f \r\n", values, value );
		
		Assert.assertEquals(value, values, DELTA);
		Assert.assertEquals(start, workedDays.get(0).getPeriod().getStart());
		Assert.assertEquals(end, workedDays.get(months-1).getPeriod().getEnd());
	}
}
