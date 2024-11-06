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
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRIKE_FACTOR;
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
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseActivityRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseCccRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.ScopeRecord;
import com.esferalia.aon.jooq.tables.records.WorkplaceRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.type.ContractLeaveType;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;
import com.esferalia.aon.watson.util.AonDateUtils;

import junit.framework.Assert;

/**
 * @author rtrepiana
 *
 */

public class SQLWorkedDaysTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000000001;

	private static ContractCode PARTIAL_TIME[] = { C200, C209, C230, C239,
			C250, C289, C501, C502, C503, C508, C510, C518, C520, C530, C540,
			C541, C550, C552, };

	private static ContractCode FULL_TIME[] = { C100, C109, C130, C139,
			C150,
			C189, // indefinite fulltime
			C401, C402, C403, C408, C410, C418, C420, C421, C430, C441, C450,
			C452, // partial & temp fulltime
	};

	@Test
	public void testFullTimeWorkDaysI() throws ExpressionException,
			SQLException {

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

		testWorkedDays(contract, 1d, null, null);

	}

	@Test
	public void testFullTimeWorkDaysII() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, getToday(),
				new HashMap<String, String>() {
					{
						put(TC2.getName(),
								format("\"%s\"", random(FULL_TIME).getValue()));
						put(MONTH_DAYS.getName(), format("%d", 30));
					}
				});

		testWorkedDays(contract, 1d, 30.00, null);

	}

	@Test
	public void testFullTimeWorkDaysIII() throws ExpressionException,
			SQLException {

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
						put(STRIKE_FACTOR.getName(), "1.0");
					}
				});

		Date contractStart = contract.getStartDate();
		long contractStartDayOfMonth = get(contractStart, DAY_OF_MONTH);

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
				(double) ((get(end, DAY_OF_MONTH) - contractStartDayOfMonth + 1 - 1)), 
				contractStart.compareTo(end) < 0 ? add(contractStart, DAY_OF_MONTH,1): contractStart, 
				end, 
				null);

	}

	@Test
	public void testPartialTimeWorkDaysI() throws ExpressionException,
			SQLException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext
				, getToday(),
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(),
								format("\"%s\"", random(PARTIAL_TIME)
										.getValue()));
						put(MONDAY_HOURS.getName(), format("%d", 0));
						put(TUESDAY_HOURS.getName(), format("%d", 0));
						put(WEDNESDAY_HOURS.getName(), format("%d", 4));
						put(THURSDAY_HOURS.getName(), format("%d", 0));
						put(FRIDAY_HOURS.getName(), format("%d", 0));
					}
				});
		
		double workDays = getMax(getToday(), DAY_OF_MONTH) - get(getToday(), DAY_OF_MONTH) + 1;
		
		double workedHours = 0.00;
		double agreementHours = 0.00;
		for ( Date day = getToday(); 
				day.compareTo(getLastDayOfMonth(getToday())) <= 0; 
				day = add( day, DAY_OF_MONTH, 1) ){
			int dayOfWeek = get(day, DAY_OF_WEEK );
			if ( dayOfWeek == Calendar.WEDNESDAY )
				workedHours += 4;
			if ( dayOfWeek != Calendar.SATURDAY 
					&& dayOfWeek != Calendar.SUNDAY )
				agreementHours += 8;
		}
		
		if (workedHours == 0 )
			;//testWorkedDays(contract, 4.0/40.00, null, workDays * ( workedHours / agreementHours ));
		else if ( get(getToday(), DAY_OF_MONTH) > 1 )
			testWorkedDays(contract, 4.0/40.00, null, workDays * ( workedHours / agreementHours ));
		else // whole month so  
			testWorkedDays(contract, 4.0/40.00, null, workDays * ( 4.00 / 40.00 ));
	}

	//@Test
	public void testPartialTimeWorkDaysII() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, getToday(),
				new HashMap<String, String>() {
					{
						put(TC2.getName(),
								format("\"%s\"", random(PARTIAL_TIME)
										.getValue()));
						put(WEEK_HOURS.getName(), format("%d", 15));
						put(AGREEMENT_HOURS.getName(), format("%d", 35));
					}
				});


		testWorkedDays(contract, (double) (15d / 35d), null, null);

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
								format("\"%s\"", random(PARTIAL_TIME)
										.getValue()));
						put(PARTIAL_FACTOR.getName(), format(Locale.US,"%f", 0.69));
					}
				});

		testWorkedDays(contract, 0.69, null, null);

	}

	//@Test
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
								format("\"%s\"", random(PARTIAL_TIME)
										.getValue()));
						put(WEEK_HOURS.getName(), format("%d", 15));
						put(AGREEMENT_HOURS.getName(), format("%d", 35));
					}
				});

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		//

		Date start = getFirstDayOfMonth(addMonths(contractStart, 1));
		Date end = getLastDayOfMonth(start);

		Date change = addDays(start,
				Math.max(2, (int) (Math.random() * get(end, DAY_OF_MONTH))));
		int changeDayOfMonth = get(change, DAY_OF_MONTH);

		addData(aonContext, contract, change, null,
				new HashMap<String, String>() {
					private static final long serialVersionUID = 1L;

					{						put(MONDAY_HOURS.getName(), format("%d", 0));
					put(FRIDAY_HOURS.getName(), format("%d", 0));
					put(WEDNESDAY_HOURS.getName(), format("%d", 4));

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
		assertEquals(workedDays.get(0),
				(double) ((changeDayOfMonth - 1) * 15d / 35d), start,
				addDays(change, -1));

		assertEquals(workedDays.get(1), (double) ((get(end, DAY_OF_MONTH)
				- changeDayOfMonth + 1) * 10d / 35d), change, end);

	}


	@Test
	public void testPartialTimeWorkDaysV() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, getToday(),
				new HashMap<String, String>() {
					{
						put(TC2.getName(),
								format("\"%s\"", random(PARTIAL_TIME)
										.getValue()));
						put(AGREEMENT_HOURS.getName(), format("%d", 39));
						
						put(MONDAY_HOURS.getName(), format(Locale.US,"%f", 0.00));
						
						put(TUESDAY_HOURS.getName(), format(Locale.US,"%f", 2.50));
						
						put(WEDNESDAY_HOURS.getName(), format(Locale.US,"%f", 0.00));
						
						put(THURSDAY_HOURS.getName(), format(Locale.US,"%f", 2.50));
						
						put(FRIDAY_HOURS.getName(), format(Locale.US,"%f", 0.00));
						put(SATURDAY_HOURS.getName(), format(Locale.US,"%f", 0.00));
						put(SUNDAY_HOURS.getName(), format(Locale.US,"%f", 0.00));
					}
				});

		double workedHours = 0.00;
		double agreementHours = 0.00;
		for ( Date date = getToday(); getLastDayOfMonth(getToday()).compareTo(date) >= 0 ; date = add(date, DAY_OF_MONTH, 1)){
			int dayOfWeek = get(date,DAY_OF_WEEK);

			if ( dayOfWeek == Calendar.TUESDAY ||
					dayOfWeek  == Calendar.THURSDAY  )
				workedHours += 2.50;

			if ( dayOfWeek != Calendar.SUNDAY &&
					dayOfWeek  != Calendar.SATURDAY  )
				agreementHours += ( 39.00 / 5.00 );
		}
		double workDays = getMax(getToday(), DAY_OF_MONTH) - get(getToday(), DAY_OF_MONTH) + 1;
		
		if (workedHours == 0.00 )
			;//testWorkedDays(contract, 4.0/40.00, null, workDays * ( workedHours / agreementHours ));
		else if ( get(getToday(), DAY_OF_MONTH) > 1)
			testWorkedDays(contract, 5.00/39.00, null, workDays * ( workedHours / agreementHours ) );
		else // whole month
			testWorkedDays(contract, 5.00/39.00, null, workDays * ( 5.00/39.00 ) );

	}

	@Test
	public void testPartialTimeWorkDaysVI() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		double weekHours [] = {
				0.00,
				0.00,
				0.00,
				0.00,
				0.00,
				0.00,
				0.00
		};
		Date startDate = getLastDayOfMonth(getToday());
		weekHours[get(startDate, DAY_OF_WEEK)-1]= 8.00;
		startDate = add(getLastDayOfMonth(getToday()), DAY_OF_MONTH, -1);
		weekHours[get(startDate, DAY_OF_WEEK)-1]= 8.00;

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				startDate,
				new HashMap<String, String>() {
					{
						put(TC2.getName(),
								format("\"%s\"", random(PARTIAL_TIME)
										.getValue()));
						put(AGREEMENT_HOURS.getName(), format("%d", 40));
						
						put(SUNDAY_HOURS.getName(), format(Locale.US,"%f", weekHours[0]));
						put(MONDAY_HOURS.getName(), format(Locale.US,"%f", weekHours[1]));
						put(TUESDAY_HOURS.getName(), format(Locale.US,"%f", weekHours[2]));
						put(WEDNESDAY_HOURS.getName(), format(Locale.US,"%f", weekHours[3]));
						put(THURSDAY_HOURS.getName(), format(Locale.US,"%f", weekHours[4]));
						put(FRIDAY_HOURS.getName(), format(Locale.US,"%f", weekHours[5]));
						put(SATURDAY_HOURS.getName(), format(Locale.US,"%f", weekHours[6]));
					}
				});

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, start, end, end, contract);
		
		List<ITimedResult<Double>> workDays = ctx.getExpressionContext().eval(
				"DIAS_TRABAJADOS", start, end, Double.class);

		Assert.assertEquals(2.00, workDays.get(0).getValue());

	}

	@Test
	public void testPartialTimeWorkDaysVII() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		double weekHours [] = {
				0.00,
				8.00,
				8.00,
				8.00,
				8.00,
				8.00,
				0.00
		};

		// start to work last week of month
		Date startDate = add(getLastDayOfMonth(getToday()),DAY_OF_MONTH,-6);
		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				startDate,
				new HashMap<String, String>() {
					{
						put(TC2.getName(),
								format("\"%s\"", random(PARTIAL_TIME)
										.getValue()));
						put(AGREEMENT_HOURS.getName(), format("%d", 40));
						
						put(SUNDAY_HOURS.getName(), format(Locale.US,"%f", weekHours[0]));
						put(MONDAY_HOURS.getName(), format(Locale.US,"%f", weekHours[1]));
						put(TUESDAY_HOURS.getName(), format(Locale.US,"%f", weekHours[2]));
						put(WEDNESDAY_HOURS.getName(), format(Locale.US,"%f", weekHours[3]));
						put(THURSDAY_HOURS.getName(), format(Locale.US,"%f", weekHours[4]));
						put(FRIDAY_HOURS.getName(), format(Locale.US,"%f", weekHours[5]));
						put(SATURDAY_HOURS.getName(), format(Locale.US,"%f", weekHours[6]));
					}
				});


		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, start, end, end, contract);
		
		List<ITimedResult<Double>> workDays = ctx.getExpressionContext().eval(
				"DIAS_TRABAJADOS", start, end, Double.class);
		
		Assert.assertEquals(7.00, workDays.get(0).getValue());

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
								format("\"%s\"", random(PARTIAL_TIME)
										.getValue()));
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
				.map(result -> result.getContext())
				.forEach(
						context -> {
							Assert.assertTrue(
									String.format(MONTH_DAYS.getName()),
									context.containsKey(MONTH_DAYS.getName()));
							Assert.assertTrue(
									String.format(WORKED_DAYS.getName()),
									context.containsKey(WORKED_DAYS.getName()));
							Assert.assertTrue(String.format(TC2.getName()),
									context.containsKey(TC2.getName()));
							Assert.assertTrue(String.format(PARTIAL_FACTOR
									.getName()), context
									.containsKey(PARTIAL_FACTOR.getName()));
							Assert.assertTrue(
									String.format(ERE_FACTOR.getName()),
									context.containsKey(ERE_FACTOR.getName()));
							Assert.assertTrue(
									String.format(STRIKE_DAYS.getName()),
									context.containsKey(STRIKE_DAYS.getName()));

							Assert.assertTrue(
									String.format(WEEK_HOURS.getName()),
									context.containsKey(WEEK_HOURS.getName()));
							Assert.assertTrue(
									String.format(MONDAY_HOURS.getName()),
									context.containsKey(MONDAY_HOURS.getName()));
							Assert.assertTrue(String.format(TUESDAY_HOURS
									.getName()), context
									.containsKey(TUESDAY_HOURS.getName()));
							Assert.assertTrue(String.format(WEDNESDAY_HOURS
									.getName()), context
									.containsKey(WEDNESDAY_HOURS.getName()));
							Assert.assertTrue(String.format(THURSDAY_HOURS
									.getName()), context
									.containsKey(THURSDAY_HOURS.getName()));
							Assert.assertTrue(
									String.format(FRIDAY_HOURS.getName()),
									context.containsKey(FRIDAY_HOURS.getName()));
							Assert.assertTrue(String.format(SATURDAY_HOURS
									.getName()), context
									.containsKey(SATURDAY_HOURS.getName()));
							Assert.assertTrue(
									String.format(SUNDAY_HOURS.getName()),
									context.containsKey(SUNDAY_HOURS.getName()));

							Assert.assertTrue(String.format(AGREEMENT_HOURS
									.getName()), context
									.containsKey(AGREEMENT_HOURS.getName()));

						});

	}

	//@Test
	public void testContextWorkDaysII() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date start = getFirstDayOfYear(getToday());

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, start,
				new HashMap<String, String>() {
					{
						put(TC2.getName(),
								format("\"%s\"", random(PARTIAL_TIME)
										.getValue()));
						put(MONDAY_HOURS.getName(), format("%d", 8));
						put(TUESDAY_HOURS.getName(), format("%d", 8));
						put(WEDNESDAY_HOURS.getName(), format("%d", 8));
						put(THURSDAY_HOURS.getName(), format("%d", 8));
						put(FRIDAY_HOURS.getName(), format("%d", 8));
					}
				});

		Date sunday = add(start, DAY_OF_MONTH, 6);

		addData(aonContext, contract, start, sunday,
				new HashMap<String, String>() {
					{
						put(WEEK_HOURS.getName(), "10.00");
					}
				});

		Date end = getLastDayOfMonth(start);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);

		List<ITimedResult<Double>> workDays = ctx.getExpressionContext().eval(
				"DIAS_TRABAJADOS", start, end, Double.class);
		Assert.assertEquals(2, workDays.size());

		Assert.assertEquals(7d / 4d, workDays.get(0).getValue());
		Assert.assertEquals(new Period(start, sunday), workDays.get(0)
				.getPeriod());

		Assert.assertEquals(
				(double) get(end, DAY_OF_MONTH) - get(sunday, DAY_OF_MONTH),
				workDays.get(1).getValue());
		Assert.assertEquals(new Period(add(sunday, DAY_OF_MONTH, 1), end),
				workDays.get(1).getPeriod());

	}

	//@Test
	public void testContextWorkDaysIII() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date start = getFirstDayOfYear(getToday());

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, start,
				new HashMap<String, String>() {
					{
						put(TC2.getName(),
								format("\"%s\"", random(PARTIAL_TIME)
										.getValue()));
						put(MONDAY_HOURS.getName(), format("%d", 8));
						put(TUESDAY_HOURS.getName(), format("%d", 8));
						put(WEDNESDAY_HOURS.getName(), format("%d", 8));
						put(THURSDAY_HOURS.getName(), format("%d", 8));
						put(FRIDAY_HOURS.getName(), format("%d", 8));
					}
				});

		Date _6day = add(start, DAY_OF_MONTH, 5);
		Date _11day = add(_6day, DAY_OF_MONTH, 6);

		addData(aonContext, contract, _6day, _11day,
				new HashMap<String, String>() {
					{
						put(WEEK_HOURS.getName(), "10.00");
					}
				});

		Date _15day = add(_11day, DAY_OF_MONTH, 2);
		Date _22dayI = add(_15day, DAY_OF_MONTH, 6);
		
		addData(aonContext, contract, _15day, _22dayI,
				new HashMap<String, String>() {
					{
						put(WEEK_HOURS.getName(), "20.00");
					}
				});
		
		Date end = getLastDayOfMonth(start);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);
		
		List<ITimedResult<Double>> workDays = ctx.getExpressionContext().eval(
				"DIAS_TRABAJADOS", start, end, Double.class);
		Assert.assertEquals(5, workDays.size());

		Assert.assertEquals(5.00, workDays.get(0).getValue());
		Assert.assertEquals(new Period(start, add(start, DAY_OF_MONTH, 4)),
				workDays.get(0).getPeriod());

		Assert.assertEquals(7d / 4d, workDays.get(1).getValue());
		Assert.assertEquals(new Period(_6day, _11day), workDays.get(1)
				.getPeriod());

		Assert.assertEquals(1.00, workDays.get(2).getValue());
		Assert.assertEquals(
				new Period(add(_11day, DAY_OF_MONTH, 1), add(_11day,
						DAY_OF_MONTH, 1)), workDays.get(2).getPeriod());

		Assert.assertEquals(7d / 2d, workDays.get(3).getValue());
		Assert.assertEquals(new Period(_15day, _22dayI), workDays.get(3)
				.getPeriod());

		Assert.assertEquals(
				(double) get(end, DAY_OF_MONTH) - get(_22dayI, DAY_OF_MONTH),
				workDays.get(4).getValue());
		Assert.assertEquals(new Period(add(_22dayI, DAY_OF_MONTH, 1), end),
				workDays.get(4).getPeriod());

	}

	@Test
	public void testContextWorkDaysIV() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date start = getFirstDayOfYear(getToday());

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, start,
				new HashMap<String, String>() {
					{
						put(TC2.getName(),
								format("\"%s\"", random(PARTIAL_TIME)
										.getValue()));
						put(MONDAY_HOURS.getName(), format("%d", 8));
						put(TUESDAY_HOURS.getName(), format("%d", 8));
						put(WEDNESDAY_HOURS.getName(), format("%d", 8));
						put(THURSDAY_HOURS.getName(), format("%d", 8));
						put(FRIDAY_HOURS.getName(), format("%d", 8));
					}
				});

		Date end = getLastDayOfMonth(start);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);

		List<ITimedResult<Double>> workDays = ctx.getExpressionContext().eval(
				"DIAS_TRABAJADOS", start, end, Double.class);
		Assert.assertEquals(1, workDays.size());
		Assert.assertEquals((double) get(end, DAY_OF_MONTH), workDays.get(0)
				.getValue());

		ctx.getExpressionContext().setVariable(ERE_FACTOR, 0.25, start, end);

		workDays = ctx.getExpressionContext().eval("DIAS_TRABAJADOS", start,
				end, Double.class);
		Assert.assertEquals(1, workDays.size());
		Assert.assertEquals(get(end, DAY_OF_MONTH) * 0.75, workDays.get(0)
				.getValue());

	}


	@Test
	public void testWorkDaysWithIT() throws ExpressionException, SQLException {

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

		Date startIT = add(start, DAY_OF_MONTH, 13);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startIT, null,
				null);

		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);
		ctx.next();

		List<ITimedResult<Double>> workedDays = ctx.getExpressionContext()
				.eval(format("%s", WORKED_DAYS), start, end, Double.class);

		Assert.assertEquals(1, workedDays.size());
		assertEquals(workedDays.get(0), 13.00, start,
				add(startIT, DAY_OF_MONTH, -1));
	}

	@Test
	public void testWorkDaysWithITII() throws ExpressionException, SQLException {

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

		Date startIT = add(start, DAY_OF_MONTH, 13);
		Date endIT = add(startIT, DAY_OF_MONTH, 9);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startIT, endIT,
				null);

		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);
		ctx.next();

		List<ITimedResult<Double>> workedDays = ctx.getExpressionContext()
				.eval(format("%s", WORKED_DAYS), start, end, Double.class);

		Assert.assertEquals(2, workedDays.size());
		assertEquals(workedDays.get(0), 13.00, start,
				add(startIT, DAY_OF_MONTH, -1));
		assertEquals(workedDays.get(1),
				(double) (get(end, DAY_OF_MONTH) - get(endIT, DAY_OF_MONTH)),
				add(endIT, DAY_OF_MONTH, +1), end);
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

		Date startITI = add(start, DAY_OF_MONTH, 5);
		Date endITI = startITI;
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITI, endITI,
				null);

		Date startITII = add(start, DAY_OF_MONTH, 7);
		Date endITII = startITII;
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITII,
				endITII, null);

		Date startITIII = add(start, DAY_OF_MONTH, 9);
		Date endITIII = startITIII;
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITIII,
				endITIII, null);

		Date startITIV = add(start, DAY_OF_MONTH, 11);
		Date endITIV = startITIV;
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITIV,
				endITIV, null);

		Date startITV = add(start, DAY_OF_MONTH, 20);
		Date endITV = add(startITV, DAY_OF_MONTH, 20);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITV, endITV,
				null);

		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);
		ctx.next();

		List<ITimedResult<Double>> workedDays = ctx.getExpressionContext()
				.eval(format("%s", WORKED_DAYS), start, end, Double.class);

		Assert.assertEquals(5, workedDays.size());
		assertEquals(workedDays.get(0), 5.00, start,
				add(startITI, DAY_OF_MONTH, -1));
		assertEquals(workedDays.get(1), 1.00, add(endITI, DAY_OF_MONTH, 1),
				add(endITI, DAY_OF_MONTH, 1));
		assertEquals(workedDays.get(2), 1.00, add(endITII, DAY_OF_MONTH, 1),
				add(endITII, DAY_OF_MONTH, 1));
		assertEquals(workedDays.get(3), 1.00, add(endITIII, DAY_OF_MONTH, 1),
				add(endITIII, DAY_OF_MONTH, 1));
		assertEquals(workedDays.get(4), 8.00, add(endITIV, DAY_OF_MONTH, 1),
				add(startITV, DAY_OF_MONTH, -1));
	}

	@Test
	public void testWorkDaysWithITIV() throws ExpressionException, SQLException {

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
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startIT, endIT,
				null);

		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);
		ctx.next();

		try {
			List<ITimedResult<Double>> workedDays = ctx.getExpressionContext()
					.eval(format("%s", WORKED_DAYS), start, end, Double.class);
			Assert.fail();
		} catch (UndefinedVariablesException e) {

		}
	}

	@Test
	public void testFullTimeWorkDaysAdjustI() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
						put(TC2.getName(),
								format("\"%s\"", random(FULL_TIME).getValue()));
						put(MONTH_DAYS.getName(), "30.00");
					}
				});

		Date contractStart = contract.getStartDate();

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		Date startDate = getFirstDayOfMonth(contractStart);
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		assertEquals(ctx, 
				30.00, 
				startDate, 
				endDate, 
				30.00);

	}

	@Test
	public void testFullTimeWorkDaysAdjustII() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
						put(MONTH_DAYS.getName(), "30.00");
					}
				});

		Date contractStart = contract.getStartDate();
		
		addData(aonContext, contract, contractStart, add(contractStart, DAY_OF_MONTH, 5), TC2, random(FULL_TIME).getValue());
		addData(aonContext, contract, add(contractStart, DAY_OF_MONTH, 6), null, TC2, random(FULL_TIME).getValue());

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		Date startDate = getFirstDayOfMonth(contractStart);
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		assertEquals(ctx, 
				6.00, 
				contractStart, 
				add(contractStart, DAY_OF_MONTH, 5), 
				30.00);

		assertEquals(ctx, 
				24.00, 
				add(contractStart, DAY_OF_MONTH, 6),
				endDate,
				30.00);
	}

	@Test
	public void testFullTimeWorkDaysAdjustIII() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
		
		DomainRecord domain = 
		newDomain(aonContext);

		ScopeRecord scope = 
		newScope(aonContext, domain.getId());
		
		EnterpriseActivityRecord enterpriseActivity = 
		newEnterpriseActivity(
				aonContext, 
				domain.getId(), 
				scope.getId(), 
				SSRegimeType.GENERAL);

		EnterpriseCccRecord enterpriseCcc = 
		newEnterpriseCcc(
				aonContext, 
				domain.getId(), 
				scope.getId(), 
				enterpriseActivity.getId(), 
				CCCType.PRINCIPAL, 
				ccc);
		
		WorkplaceRecord workplace = 
		newWorkplace(
		aonContext, 
		domain.getId(), 
		scope.getId(), 
		enterpriseActivity.getEnterprise());
		
		RegistryRecord person = 
		newPerson(
		aonContext, 
		domain.getId(), 
		"66666666M");
		
		Date contractStart = getFirstDayOfYear(getToday()) ;

		 
		newContract(
		aonContext,
		SSRegimeType.GENERAL, 
		CCCType.PRINCIPAL,			
		contractStart,
		add(contractStart, DAY_OF_MONTH, 5),
		new HashMap<String, String>() {
			{
				put(MONTH_DAYS.getName(), "30.00");
				put(TC2.getName(), random(FULL_TIME).getValue() );
			}
		},
		new String[] {
		}, 
		new String[] {						
		},
		null,
		domain.getId(), 			//domainId, 
		person.getId(),				//personId, 
		workplace.getId(),			//workplaceId, 
		enterpriseCcc.getId(),		//enterpriseCccId,
		enterpriseActivity.getId()	//enterpriseActivityId
		);

		@SuppressWarnings("serial")
		ContractRecord contract = 
		newContract(
		aonContext,
		SSRegimeType.GENERAL, 
		CCCType.PRINCIPAL,			
		add(contractStart, DAY_OF_MONTH, 6),
		null,
		new HashMap<String, String>() {
			{
				put(MONTH_DAYS.getName(), "30.00");
				put(TC2.getName(), random(FULL_TIME).getValue() );
				put(ContextVariable.ACTIVE_DAYS.getName(), "6.00" );
			}
		},
		new String[] {
		}, 
		new String[] {						
		},
		null,
		domain.getId(), 			//domainId, 
		person.getId(),				//personId, 
		workplace.getId(),			//workplaceId, 
		enterpriseCcc.getId(),		//enterpriseCccId,
		enterpriseActivity.getId()	//enterpriseActivityId
		);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		Date startDate = getFirstDayOfMonth(contractStart);
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		assertEquals(ctx, 
				24.00, 
				add(contractStart, DAY_OF_MONTH, 6),
				endDate,
				30.00);
	}

	@Test
	public void testFullTimeWorkDaysAdjustIV() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
		
		DomainRecord domain = 
		newDomain(aonContext);

		ScopeRecord scope = 
		newScope(aonContext, domain.getId());
		
		EnterpriseActivityRecord enterpriseActivity = 
		newEnterpriseActivity(
				aonContext, 
				domain.getId(), 
				scope.getId(), 
				SSRegimeType.GENERAL);

		EnterpriseCccRecord enterpriseCcc = 
		newEnterpriseCcc(
				aonContext, 
				domain.getId(), 
				scope.getId(), 
				enterpriseActivity.getId(), 
				CCCType.PRINCIPAL, 
				ccc);
		
		WorkplaceRecord workplace = 
		newWorkplace(
		aonContext, 
		domain.getId(), 
		scope.getId(), 
		enterpriseActivity.getEnterprise());
		
		RegistryRecord person = 
		newPerson(
		aonContext, 
		domain.getId(), 
		"66666666M");
		
		Date contractStart = getFirstDayOfYear(getToday()) ;

		 
		newContract(
		aonContext,
		SSRegimeType.GENERAL, 
		CCCType.PRINCIPAL,			
		contractStart,
		add(contractStart, DAY_OF_MONTH, 5),
		new HashMap<String, String>() {
			{
				put(MONTH_DAYS.getName(), "30.00");
				put(TC2.getName(), random(FULL_TIME).getValue() );
			}
		},
		new String[] {
		}, 
		new String[] {						
		},
		null,
		domain.getId(), 			//domainId, 
		person.getId(),				//personId, 
		workplace.getId(),			//workplaceId, 
		enterpriseCcc.getId(),		//enterpriseCccId,
		enterpriseActivity.getId()	//enterpriseActivityId
		);

		@SuppressWarnings("serial")
		ContractRecord contract = 
		newContract(
		aonContext,
		SSRegimeType.GENERAL, 
		CCCType.PRINCIPAL,			
		add(contractStart, DAY_OF_MONTH, 6),
		null,
		new HashMap<String, String>() {
			{
				put(MONTH_DAYS.getName(), "30.00");
				put(TC2.getName(), random(FULL_TIME).getValue() );
			}
		},
		new String[] {
		}, 
		new String[] {						
		},
		null,
		domain.getId(), 			//domainId, 
		person.getId(),				//personId, 
		workplace.getId(),			//workplaceId, 
		enterpriseCcc.getId(),		//enterpriseCccId,
		enterpriseActivity.getId()	//enterpriseActivityId
		);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		Date startDate = getFirstDayOfMonth(contractStart);
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		assertEquals(ctx, 
				24.00, 
				add(contractStart, DAY_OF_MONTH, 6),
				endDate,
				30.00);
	}

	@Test
	public void testFullTimeWorkDaysAdjustV() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
		
		DomainRecord domain = 
		newDomain(aonContext);

		ScopeRecord scope = 
		newScope(aonContext, domain.getId());
		
		EnterpriseActivityRecord enterpriseActivity = 
		newEnterpriseActivity(
				aonContext, 
				domain.getId(), 
				scope.getId(), 
				SSRegimeType.GENERAL);

		EnterpriseCccRecord enterpriseCcc = 
		newEnterpriseCcc(
				aonContext, 
				domain.getId(), 
				scope.getId(), 
				enterpriseActivity.getId(), 
				CCCType.PRINCIPAL, 
				ccc);
		
		WorkplaceRecord workplace = 
		newWorkplace(
		aonContext, 
		domain.getId(), 
		scope.getId(), 
		enterpriseActivity.getEnterprise());
		
		RegistryRecord person = 
		newPerson(
		aonContext, 
		domain.getId(), 
		"66666666M");
		
		Date contractStart = getFirstDayOfYear(getToday()) ;

		 
		newContract(
		aonContext,
		SSRegimeType.GENERAL, 
		CCCType.PRINCIPAL,			
		contractStart,
		add(contractStart, DAY_OF_MONTH, 5),
		new HashMap<String, String>() {
			{
				put(MONTH_DAYS.getName(), "30.00");
				put(TC2.getName(), random(FULL_TIME).getValue() );
			}
		},
		new String[] {
		}, 
		new String[] {						
		},
		null,
		domain.getId(), 			//domainId, 
		person.getId(),				//personId, 
		workplace.getId(),			//workplaceId, 
		enterpriseCcc.getId(),		//enterpriseCccId,
		enterpriseActivity.getId()	//enterpriseActivityId
		);

		newContract(
		aonContext,
		SSRegimeType.GENERAL, 
		CCCType.PRINCIPAL,			
		add(contractStart, DAY_OF_MONTH, 6),
		add(contractStart, DAY_OF_MONTH, 9),
		new HashMap<String, String>() {
			{
				put(MONTH_DAYS.getName(), "30.00");
				put(TC2.getName(), random(FULL_TIME).getValue() );
			}
		},
		new String[] {
		}, 
		new String[] {						
		},
		null,
		domain.getId(), 			//domainId, 
		person.getId(),				//personId, 
		workplace.getId(),			//workplaceId, 
		enterpriseCcc.getId(),		//enterpriseCccId,
		enterpriseActivity.getId()	//enterpriseActivityId
		);

		@SuppressWarnings("serial")
		ContractRecord contract = 
		newContract(
		aonContext,
		SSRegimeType.GENERAL, 
		CCCType.PRINCIPAL,			
		add(contractStart, DAY_OF_MONTH, 10),
		null,
		new HashMap<String, String>() {
			{
				put(MONTH_DAYS.getName(), "30.00");
				put(TC2.getName(), random(FULL_TIME).getValue() );
			}
		},
		new String[] {
		}, 
		new String[] {						
		},
		null,
		domain.getId(), 			//domainId, 
		person.getId(),				//personId, 
		workplace.getId(),			//workplaceId, 
		enterpriseCcc.getId(),		//enterpriseCccId,
		enterpriseActivity.getId()	//enterpriseActivityId
		);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		Date startDate = getFirstDayOfMonth(contractStart);
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		assertEquals(ctx, 
				20.00, 
				add(contractStart, DAY_OF_MONTH, 10),
				endDate,
				30.00);
	}
	
	@Test
	public void testFullTimeWorkDaysAdjustVI() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
		
		DomainRecord domain = 
		newDomain(aonContext);

		ScopeRecord scope = 
		newScope(aonContext, domain.getId());
		
		EnterpriseActivityRecord enterpriseActivity = 
		newEnterpriseActivity(
				aonContext, 
				domain.getId(), 
				scope.getId(), 
				SSRegimeType.GENERAL);

		EnterpriseCccRecord enterpriseCcc = 
		newEnterpriseCcc(
				aonContext, 
				domain.getId(), 
				scope.getId(), 
				enterpriseActivity.getId(), 
				CCCType.PRINCIPAL, 
				ccc);
		
		WorkplaceRecord workplace = 
		newWorkplace(
		aonContext, 
		domain.getId(), 
		scope.getId(), 
		enterpriseActivity.getEnterprise());
		
		RegistryRecord person = 
		newPerson(
		aonContext, 
		domain.getId(), 
		"66666666M");
		
		Date contractStart = getFirstDayOfYear(getToday()) ;

		 
		for ( int i = 0; i < 29; i ++)
			newContract(
			aonContext,
			SSRegimeType.GENERAL, 
			CCCType.PRINCIPAL,			
			add(contractStart, DAY_OF_MONTH, i),
			add(contractStart, DAY_OF_MONTH, i),
			new HashMap<String, String>() {
				{
					put(MONTH_DAYS.getName(), "30.00");
					put(TC2.getName(), random(FULL_TIME).getValue() );
				}
			},
			new String[] {
			}, 
			new String[] {						
			},
			null,
			domain.getId(), 			//domainId, 
			person.getId(),				//personId, 
			workplace.getId(),			//workplaceId, 
			enterpriseCcc.getId(),		//enterpriseCccId,
			enterpriseActivity.getId()	//enterpriseActivityId
			);


		@SuppressWarnings("serial")
		ContractRecord contract = 
		newContract(
		aonContext,
		SSRegimeType.GENERAL, 
		CCCType.PRINCIPAL,			
		add(contractStart, DAY_OF_MONTH, 29),
		null,
		new HashMap<String, String>() {
			{
				put(MONTH_DAYS.getName(), "30.00");
				put(TC2.getName(), random(FULL_TIME).getValue() );
			}
		},
		new String[] {
		}, 
		new String[] {						
		},
		null,
		domain.getId(), 			//domainId, 
		person.getId(),				//personId, 
		workplace.getId(),			//workplaceId, 
		enterpriseCcc.getId(),		//enterpriseCccId,
		enterpriseActivity.getId()	//enterpriseActivityId
		);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		Date startDate = getFirstDayOfMonth(contractStart);
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		assertEquals(ctx, 
				1.00, 
				add(contractStart, DAY_OF_MONTH, 29),
				endDate,
				30.00);
	}

	@Test
	public void testFullTimeWorkDaysAdjustVII() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
		
		DomainRecord domain = 
		newDomain(aonContext);

		ScopeRecord scope = 
		newScope(aonContext, domain.getId());
		
		EnterpriseActivityRecord enterpriseActivity = 
		newEnterpriseActivity(
				aonContext, 
				domain.getId(), 
				scope.getId(), 
				SSRegimeType.GENERAL);

		EnterpriseCccRecord enterpriseCcc = 
		newEnterpriseCcc(
				aonContext, 
				domain.getId(), 
				scope.getId(), 
				enterpriseActivity.getId(), 
				CCCType.PRINCIPAL, 
				ccc);
		
		WorkplaceRecord workplace = 
		newWorkplace(
		aonContext, 
		domain.getId(), 
		scope.getId(), 
		enterpriseActivity.getEnterprise());
		
		RegistryRecord person = 
		newPerson(
		aonContext, 
		domain.getId(), 
		"66666666M");
		
		Date contractStart = getFirstDayOfYear(getToday()) ;

		 
		for ( int i = 0; i < 30; i ++)
			newContract(
			aonContext,
			SSRegimeType.GENERAL, 
			CCCType.PRINCIPAL,			
			add(contractStart, DAY_OF_MONTH, i),
			add(contractStart, DAY_OF_MONTH, i),
			new HashMap<String, String>() {
				{
					put(MONTH_DAYS.getName(), "30.00");
					put(TC2.getName(), random(FULL_TIME).getValue() );
				}
			},
			new String[] {
			}, 
			new String[] {						
			},
			null,
			domain.getId(), 			//domainId, 
			person.getId(),				//personId, 
			workplace.getId(),			//workplaceId, 
			enterpriseCcc.getId(),		//enterpriseCccId,
			enterpriseActivity.getId()	//enterpriseActivityId
			);


		@SuppressWarnings("serial")
		ContractRecord contract = 
		newContract(
		aonContext,
		SSRegimeType.GENERAL, 
		CCCType.PRINCIPAL,			
		add(contractStart, DAY_OF_MONTH, 30),
		null,
		new HashMap<String, String>() {
			{
				put(MONTH_DAYS.getName(), "30.00");
				put(TC2.getName(), random(FULL_TIME).getValue() );
			}
		},
		new String[] {
		}, 
		new String[] {						
		},
		null,
		domain.getId(), 			//domainId, 
		person.getId(),				//personId, 
		workplace.getId(),			//workplaceId, 
		enterpriseCcc.getId(),		//enterpriseCccId,
		enterpriseActivity.getId()	//enterpriseActivityId
		);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		Date startDate = getFirstDayOfMonth(contractStart);
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		assertEquals(ctx, 
				0.00, 
				add(contractStart, DAY_OF_MONTH, 30),
				endDate,
				30.00);
	}
	@Test
	public void testFullTimeWorkDaysAdjustVIII() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		String ccc = Long.toString(System.currentTimeMillis()).substring(0, 11);
		
		DomainRecord domain = 
		newDomain(aonContext);

		ScopeRecord scope = 
		newScope(aonContext, domain.getId());
		
		EnterpriseActivityRecord enterpriseActivity = 
		newEnterpriseActivity(
				aonContext, 
				domain.getId(), 
				scope.getId(), 
				SSRegimeType.GENERAL);

		EnterpriseCccRecord enterpriseCcc = 
		newEnterpriseCcc(
				aonContext, 
				domain.getId(), 
				scope.getId(), 
				enterpriseActivity.getId(), 
				CCCType.PRINCIPAL, 
				ccc);
		
		WorkplaceRecord workplace = 
		newWorkplace(
		aonContext, 
		domain.getId(), 
		scope.getId(), 
		enterpriseActivity.getEnterprise());
		
		RegistryRecord person = 
		newPerson(
		aonContext, 
		domain.getId(), 
		"66666666M");
		
		Date firstDayOfYear = getFirstDayOfYear(getToday()) ;
		Date contractStart = add(firstDayOfYear, MONTH, -9);
		 
		newContract(
		aonContext,
		SSRegimeType.GENERAL, 
		CCCType.PRINCIPAL,			
		contractStart,
		add(firstDayOfYear, DAY_OF_MONTH, 5),
		new HashMap<String, String>() {
			{
				put(MONTH_DAYS.getName(), "30.00");
				put(TC2.getName(), random(FULL_TIME).getValue() );
			}
		},
		new String[] {
		}, 
		new String[] {						
		},
		null,
		domain.getId(), 			//domainId, 
		person.getId(),				//personId, 
		workplace.getId(),			//workplaceId, 
		enterpriseCcc.getId(),		//enterpriseCccId,
		enterpriseActivity.getId()	//enterpriseActivityId
		);

		newContract(
		aonContext,
		SSRegimeType.GENERAL, 
		CCCType.PRINCIPAL,			
		add(firstDayOfYear, DAY_OF_MONTH, 6),
		add(firstDayOfYear, DAY_OF_MONTH, 9),
		new HashMap<String, String>() {
			{
				put(MONTH_DAYS.getName(), "30.00");
				put(TC2.getName(), random(FULL_TIME).getValue() );
			}
		},
		new String[] {
		}, 
		new String[] {						
		},
		null,
		domain.getId(), 			//domainId, 
		person.getId(),				//personId, 
		workplace.getId(),			//workplaceId, 
		enterpriseCcc.getId(),		//enterpriseCccId,
		enterpriseActivity.getId()	//enterpriseActivityId
		);

		@SuppressWarnings("serial")
		ContractRecord contract = 
		newContract(
		aonContext,
		SSRegimeType.GENERAL, 
		CCCType.PRINCIPAL,			
		add(firstDayOfYear, DAY_OF_MONTH, 10),
		null,
		new HashMap<String, String>() {
			{
				put(MONTH_DAYS.getName(), "30.00");
				put(TC2.getName(), random(FULL_TIME).getValue() );
			}
		},
		new String[] {
		}, 
		new String[] {						
		},
		null,
		domain.getId(), 			//domainId, 
		person.getId(),				//personId, 
		workplace.getId(),			//workplaceId, 
		enterpriseCcc.getId(),		//enterpriseCccId,
		enterpriseActivity.getId()	//enterpriseActivityId
		);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		Date startDate = getFirstDayOfMonth(firstDayOfYear);
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		assertEquals(ctx, 
				20.00, 
				add(firstDayOfYear, DAY_OF_MONTH, 10),
				endDate,
				30.00);
	}

	
	@Test
	public void testContextWorkDaysMinusHolidays() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date startDate = getFirstDayOfYear(getToday());

		AgreementRecord agreement = newAgreement(aonContext);
		AgreementLevelCategoryRecord category = newAgreementCategory(aonContext, agreement);
		
		addData(aonContext, agreement, startDate, new HashMap<String, String>(){
			{
				put("DIAS_NO_TRABAJADOS", "DIAS_VACACIONES");
			}
		});

		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(
				aonContext, 
				startDate,
				new HashMap<String, String>() {
					{
						put(TC2.getName(),format("\"%s\"", random(FULL_TIME) .getValue()));
					}
				},
				new String[0],
				new String[0],
				category
				);
		
		Date holidaysStartDate = add(startDate, Calendar.DAY_OF_MONTH, 9);
		Date holidaysEndDate = add(holidaysStartDate, Calendar.DAY_OF_MONTH, 9);
		
		addData(aonContext, contract, holidaysStartDate, holidaysEndDate, "DIAS_VACACIONES", "10.00");

		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		List<ITimedResult<Double>> results = 
		ctx.getExpressionContext()
		.eval("DIAS_VACACIONES", startDate, endDate, Double.class);
		
		results.forEach( result -> {
			org.junit.Assert.assertEquals(10.00, result.getValue(), 0.00);
			org.junit.Assert.assertEquals(holidaysStartDate, result.getPeriod().getStart());
			org.junit.Assert.assertEquals(holidaysEndDate, result.getPeriod().getEnd());
		});

		results = 
		ctx.getExpressionContext()
		.eval("DIAS_TRABAJADOS", startDate, endDate, Double.class);
		
		org.junit.Assert.assertEquals(2, results.size());
		org.junit.Assert.assertEquals(startDate, results.get(0).getPeriod().getStart());
		org.junit.Assert.assertEquals(add(holidaysStartDate, Calendar.DAY_OF_MONTH, -1), results.get(0).getPeriod().getEnd());
		org.junit.Assert.assertEquals(9.00, results.get(0).getValue(), 0.00);
		
		org.junit.Assert.assertEquals(add(holidaysEndDate, Calendar.DAY_OF_MONTH, 1), results.get(1).getPeriod().getStart());
		org.junit.Assert.assertEquals(endDate, results.get(1).getPeriod().getEnd());
		
		org.junit.Assert.assertEquals(get(endDate, Calendar.DAY_OF_MONTH) - 19, results.get(1).getValue(), 0.00);
		
	}
	
	@Test
	public void testContextWorkDaysMinusHolidaysMonthlyAtFebruary() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date startDate = add(getFirstDayOfYear(getToday()), MONTH, 1);
		

		AgreementRecord agreement = newAgreement(aonContext);
		AgreementLevelCategoryRecord category = newAgreementCategory(aonContext, agreement);
		
		addData(aonContext, agreement, startDate, new HashMap<String, String>(){
			{
				put("DIAS_NO_TRABAJADOS", "DIAS_VACACIONES");
			}
		});

		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(
				aonContext, 
				startDate,
				new HashMap<String, String>() {
					{
						put(MONTH_DAYS.getName(),"30");
						put(TC2.getName(),format("\"%s\"", random(FULL_TIME) .getValue()));
					}
				},
				new String[0],
				new String[0],
				category
				);
		
		Date holidaysStartDate = add(startDate, Calendar.DAY_OF_MONTH, 9);
		Date holidaysEndDate = add(holidaysStartDate, Calendar.DAY_OF_MONTH, 9);
		
		addData(aonContext, contract, holidaysStartDate, holidaysEndDate, "DIAS_VACACIONES", "10.00");

		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		List<ITimedResult<Double>> results = 
		ctx.getExpressionContext()
		.eval("DIAS_VACACIONES", startDate, endDate, Double.class);
		
		results.forEach( result -> {
			org.junit.Assert.assertEquals(10.00, result.getValue(), 0.00);
			org.junit.Assert.assertEquals(holidaysStartDate, result.getPeriod().getStart());
			org.junit.Assert.assertEquals(holidaysEndDate, result.getPeriod().getEnd());
		});

		results = 
		ctx.getExpressionContext()
		.eval("DIAS_TRABAJADOS", startDate, endDate, Double.class);
		
		org.junit.Assert.assertEquals(2, results.size());
		org.junit.Assert.assertEquals(startDate, results.get(0).getPeriod().getStart());
		org.junit.Assert.assertEquals(add(holidaysStartDate, Calendar.DAY_OF_MONTH, -1), results.get(0).getPeriod().getEnd());
		org.junit.Assert.assertEquals(9.00, results.get(0).getValue(), 0.00);
		
		org.junit.Assert.assertEquals(add(holidaysEndDate, Calendar.DAY_OF_MONTH, 1), results.get(1).getPeriod().getStart());
		org.junit.Assert.assertEquals(endDate, results.get(1).getPeriod().getEnd());
		org.junit.Assert.assertEquals(30 - 19, results.get(1).getValue(), 0.00);
		
//		results = 
//		ctx.getExpressionContext()
//		.eval("100.00 * DIAS_TRABAJADOS / DIAS_MES ", startDate, endDate, Double.class);

	}
	@Test
	public void testContextWorkDaysMinusHolidaysWithSections() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date startDate = getFirstDayOfYear(getToday());

		AgreementRecord agreement = newAgreement(aonContext);
		AgreementLevelCategoryRecord category = newAgreementCategory(aonContext, agreement);
		
		addData(aonContext, agreement, startDate, new HashMap<String, String>(){
			{
				put("DIAS_NO_TRABAJADOS", "DIAS_VACACIONES");
			}
		});

		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(
				aonContext, 
				startDate,
				new HashMap<String, String>() {
					{
						put(TC2.getName(),format("\"%s\"", random(FULL_TIME) .getValue()));
					}
				},
				new String[0],
				new String[0],
				category
				);
		
		Date holidaysStartDate = add(startDate, Calendar.DAY_OF_MONTH, 9);
		Date holidaysEndDate = add(holidaysStartDate, Calendar.DAY_OF_MONTH, 9);
		
		addData(aonContext, contract, holidaysStartDate, holidaysEndDate, "DIAS_VACACIONES", "10.00");

		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		List<ITimedResult<Double>> results = 
		ctx.getExpressionContext()
		.eval("DIAS_VACACIONES", holidaysStartDate, holidaysStartDate, Double.class);
		
		results.forEach( result -> {
			org.junit.Assert.assertEquals(1.00, result.getValue(), 0.00);
			org.junit.Assert.assertEquals(holidaysStartDate, result.getPeriod().getStart());
			org.junit.Assert.assertEquals(holidaysStartDate, result.getPeriod().getEnd());
		});

		results = 
		ctx.getExpressionContext()
		.eval("DIAS_TRABAJADOS", startDate, endDate, Double.class);
		
		org.junit.Assert.assertEquals(2, results.size());
		org.junit.Assert.assertEquals(startDate, results.get(0).getPeriod().getStart());
		org.junit.Assert.assertEquals(add(holidaysStartDate, Calendar.DAY_OF_MONTH, -1), results.get(0).getPeriod().getEnd());
		org.junit.Assert.assertEquals(9.00, results.get(0).getValue(), 0.00);
		
		org.junit.Assert.assertEquals(add(holidaysEndDate, Calendar.DAY_OF_MONTH, 1), results.get(1).getPeriod().getStart());
		org.junit.Assert.assertEquals(endDate, results.get(1).getPeriod().getEnd());
		
		org.junit.Assert.assertEquals(get(endDate, Calendar.DAY_OF_MONTH) - 19, results.get(1).getValue(), 0.00);
		

	}

	@Test
	public void testContextWorkDaysMinusHolidaysBR() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date startDate = getFirstDayOfYear(getToday());

		AgreementRecord agreement = newAgreement(aonContext);
		AgreementLevelCategoryRecord category = newAgreementCategory(aonContext, agreement);
		
		addData(aonContext, agreement, startDate, new HashMap<String, String>(){
			{
				put("DIAS_NO_TRABAJADOS", "DIAS_VACACIONES");
			}
		});

		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(
				aonContext, 
				startDate,
				new HashMap<String, String>() {
					{
						put(MONTH_DAYS.getName(),"30.00");
						put(TC2.getName(),format("\"%s\"", random(FULL_TIME) .getValue()));
					}
				},
				new String []{
					"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
					"25 * DIAS_VACACIONES"
				},
				new String[0],
				category
				);
		
		Date holidaysStartDate = add(startDate, Calendar.DAY_OF_MONTH, 9);
		Date holidaysEndDate = add(holidaysStartDate, Calendar.DAY_OF_MONTH, 9);
		
		addData(aonContext, contract, holidaysStartDate, holidaysEndDate, "DIAS_VACACIONES", "10.00");

		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		List<ITimedResult<Double>> results = 
		ctx.getExpressionContext()
		.eval("DIAS_VACACIONES", startDate, endDate, Double.class);
		
		results.forEach( result -> {
			org.junit.Assert.assertEquals(10.00, result.getValue(), 0.00);
			org.junit.Assert.assertEquals(holidaysStartDate, result.getPeriod().getStart());
			org.junit.Assert.assertEquals(holidaysEndDate, result.getPeriod().getEnd());
		});

		results = 
		ctx.getExpressionContext()
		.eval("DIAS_TRABAJADOS", startDate, endDate, Double.class);
		
		org.junit.Assert.assertEquals(2, results.size());
		org.junit.Assert.assertEquals(startDate, results.get(0).getPeriod().getStart());
		org.junit.Assert.assertEquals(add(holidaysStartDate, Calendar.DAY_OF_MONTH, -1), results.get(0).getPeriod().getEnd());
		org.junit.Assert.assertEquals(9.00, results.get(0).getValue(), 0.00);
		
		org.junit.Assert.assertEquals(add(holidaysEndDate, Calendar.DAY_OF_MONTH, 1), results.get(1).getPeriod().getStart());
		org.junit.Assert.assertEquals(endDate, results.get(1).getPeriod().getEnd());
		
		org.junit.Assert.assertEquals(11.00, results.get(1).getValue(), 0.00);
		
		results =
		ctx.getExpressionContext()
		.eval("DIAS_COTIZADOS", startDate, endDate, Double.class);

		org.junit.Assert.assertEquals(3, results.size());

		org.junit.Assert.assertEquals(startDate, results.get(0).getPeriod().getStart());
		org.junit.Assert.assertEquals(add(holidaysStartDate, Calendar.DAY_OF_MONTH, -1), results.get(0).getPeriod().getEnd());
		org.junit.Assert.assertEquals(9.00, results.get(0).getValue(), 0.00);
		
		org.junit.Assert.assertEquals(holidaysStartDate, results.get(1).getPeriod().getStart());
		org.junit.Assert.assertEquals(holidaysEndDate, results.get(1).getPeriod().getEnd());
		org.junit.Assert.assertEquals(10.00, results.get(1).getValue(), 0.00);

		org.junit.Assert.assertEquals(add(holidaysEndDate, Calendar.DAY_OF_MONTH, 1), results.get(2).getPeriod().getStart());
		org.junit.Assert.assertEquals(endDate, results.get(2).getPeriod().getEnd());
		org.junit.Assert.assertEquals(11.00, results.get(2).getValue(), 0.00);
		
		JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder(connection);
		new SmartContractSalaryCalculator(jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();
		startDate = add(startDate, Calendar.MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startDate, null, null);
		ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		
		results = ctx.getExpressionContext().eval("BASE_REGULADORA", startDate, endDate, Double.class);
		org.junit.Assert.assertEquals((1000.00 * 20 / 30.00 + 25.00 * 10.00) / 30 , results.get(0).getValue(), 0.0001);

	}

	@Test
	public void testContextWorkDaysMinusHolidaysBRI() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date startDate = getFirstDayOfYear(getToday());

		AgreementRecord agreement = newAgreement(aonContext);
		AgreementLevelCategoryRecord category = newAgreementCategory(aonContext, agreement);
		
		addData(aonContext, agreement, startDate, new HashMap<String, String>(){
			{
				put("DIAS_NO_TRABAJADOS", "DIAS_VACACIONES");
			}
		});

		
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(
				aonContext, 
				startDate,
				new HashMap<String, String>() {
					{
						put(MONTH_DAYS.getName(),"30.00");
						put(TC2.getName(),format("\"%s\"", random(FULL_TIME) .getValue()));
					}
				},
				new String []{
					"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
					"25 * DIAS_VACACIONES"
				},
				new String[0],
				category
				);
		
		Date holidaysStartDate = add(startDate, Calendar.DAY_OF_MONTH, 9);
		Date holidaysEndDate = add(holidaysStartDate, Calendar.DAY_OF_MONTH, 9);
		
		addData(aonContext, contract, holidaysStartDate, holidaysEndDate, "DIAS_VACACIONES", "10.00");

		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		List<ITimedResult<Double>> results = 
		ctx.getExpressionContext()
		.eval("DIAS_VACACIONES", startDate, endDate, Double.class);
		
		results.forEach( result -> {
			org.junit.Assert.assertEquals(10.00, result.getValue(), 0.00);
			org.junit.Assert.assertEquals(holidaysStartDate, result.getPeriod().getStart());
			org.junit.Assert.assertEquals(holidaysEndDate, result.getPeriod().getEnd());
		});

		results = 
		ctx.getExpressionContext()
		.eval("DIAS_TRABAJADOS", startDate, endDate, Double.class);
		
		org.junit.Assert.assertEquals(2, results.size());
		org.junit.Assert.assertEquals(startDate, results.get(0).getPeriod().getStart());
		org.junit.Assert.assertEquals(add(holidaysStartDate, Calendar.DAY_OF_MONTH, -1), results.get(0).getPeriod().getEnd());
		org.junit.Assert.assertEquals(9.00, results.get(0).getValue(), 0.00);
		
		org.junit.Assert.assertEquals(add(holidaysEndDate, Calendar.DAY_OF_MONTH, 1), results.get(1).getPeriod().getStart());
		org.junit.Assert.assertEquals(endDate, results.get(1).getPeriod().getEnd());
		
		org.junit.Assert.assertEquals(11.00, results.get(1).getValue(), 0.00);
		
		results =
		ctx.getExpressionContext()
		.eval("DIAS_COTIZADOS", startDate, endDate, Double.class);

		org.junit.Assert.assertEquals(3, results.size());

		org.junit.Assert.assertEquals(startDate, results.get(0).getPeriod().getStart());
		org.junit.Assert.assertEquals(add(holidaysStartDate, Calendar.DAY_OF_MONTH, -1), results.get(0).getPeriod().getEnd());
		org.junit.Assert.assertEquals(9.00, results.get(0).getValue(), 0.00);
		
		org.junit.Assert.assertEquals(holidaysStartDate, results.get(1).getPeriod().getStart());
		org.junit.Assert.assertEquals(holidaysEndDate, results.get(1).getPeriod().getEnd());
		org.junit.Assert.assertEquals(10.00, results.get(1).getValue(), 0.00);

		org.junit.Assert.assertEquals(add(holidaysEndDate, Calendar.DAY_OF_MONTH, 1), results.get(2).getPeriod().getStart());
		org.junit.Assert.assertEquals(endDate, results.get(2).getPeriod().getEnd());
		org.junit.Assert.assertEquals(11.00, results.get(2).getValue(), 0.00);
		
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startDate, null, null);
		ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		
		results = ctx.getExpressionContext().eval("BASE_REGULADORA", startDate, endDate, Double.class);
		org.junit.Assert.assertEquals((1000.00 * 20 / 30.00 + 25.00 * 10.00) / 30 , results.get(0).getValue(), 0.0001);

	}

	// ------------------------------------------------------------------------
	protected void testWorkedDays(ContractRecord contract, double coefficient,
			Double monthdays, Double firstMonthDays) throws ExpressionException, SQLException {
		Connection connection = getConnection();

		Date contractStart = contract.getStartDate();
		long contractStartDayOfMonth = get(contractStart, DAY_OF_MONTH);

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
		if ( monthdays == null || contractStartDayOfMonth > 1)
			if ( firstMonthDays != null )
				assertEquals(ctx,firstMonthDays, contractStart,
						end, monthdays);
			else
				assertEquals(ctx,(((double) get(end, DAY_OF_MONTH)
						- contractStartDayOfMonth + 1) * coefficient), contractStart,
						end, monthdays);
		else
			assertEquals(ctx,(((double) monthdays
					- contractStartDayOfMonth + 1) * coefficient), contractStart,
					end, monthdays);

		// 95% of cases . Whole month
		start = getFirstDayOfMonth(addMonths(start, 1));
		end = getLastDayOfMonth(start);
		ctx = new SQLContractSalaryCalculatorContext(connection, start, end,
				end, criteria);
		ctx.next();
		if ( monthdays != null )
			assertEquals(ctx, Math.min((double) (Math.max(get(end, DAY_OF_MONTH),30) * coefficient), monthdays),
					start, end, monthdays);
		else
			assertEquals(ctx, (double) (get(end, DAY_OF_MONTH) * coefficient),
					start, end, monthdays);

		// Extras. First year, almost all the times will be partial.
		start = getFirstDayOfYear(contractStart);
		end = getLastDayOfYear(start);
		ctx = new SQLContractSalaryCalculatorContext(connection, start, end,
				end, criteria);
		ctx.next();

		double firstYearDays = 0.00;
		
		if ( monthdays == null )
				firstYearDays = (get(end, DAY_OF_YEAR)
					- get(contractStart, DAY_OF_YEAR) + 1);
				
		else {
			int months = 12 - (get(contractStart, MONTH) + 1);
			firstYearDays = ( months  * monthdays ) ;
			int contractStartDay = get(contractStart,DAY_OF_MONTH);
			if ( contractStartDay == 1)
				firstYearDays += monthdays;
			else
				firstYearDays += ( getMax(contractStart,DAY_OF_MONTH) - contractStartDay +1);
		}
		
		if ( firstMonthDays != null ){
			firstYearDays -= ( getMax(contractStart,DAY_OF_MONTH) - get(contractStart,DAY_OF_MONTH) +1);
			assertEquals(
					ctx,
					firstMonthDays + (firstYearDays * coefficient ),
					contractStart, end, monthdays);
		}
		else {
			assertEquals(
						ctx,
						(firstYearDays * coefficient ),
						contractStart, end, monthdays);
		}

		// Extras. Second year. This will be whole
		start = getFirstDayOfYear(add(start, YEAR, 1));
		end = getLastDayOfYear(start);
		ctx = new SQLContractSalaryCalculatorContext(connection, start, end,
				end, criteria);
		ctx.next();
		if ( monthdays == null )
			assertEquals(ctx, (double) get(end, DAY_OF_YEAR) * coefficient, start,
					end, monthdays);
		else 
			assertEquals(ctx, (double) 12 * monthdays * coefficient, start,
					end, monthdays);
			
		// Extras. Second/Third year. This will be whole
		start = addMonths(getFirstDayOfYear(add(start, YEAR, 1)), 6);
		end = add(addMonths(start, 12), DAY_OF_MONTH, -1);
		ctx = new SQLContractSalaryCalculatorContext(connection, start, end,
				end, criteria);
		ctx.next();
		if ( monthdays == null )
			assertEquals(
					ctx,
					(double) (get(end, DAY_OF_YEAR)
							+ (get(getLastDayOfYear(start), DAY_OF_YEAR) - get(
									start, DAY_OF_YEAR)) + 1)
							* coefficient, start, end, monthdays);
		else 
			assertEquals(
					ctx,
					12 *  monthdays * coefficient, start, end, monthdays);

	}

	@Test
	public void testTotalWorkDaysI() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date start = getFirstDayOfYear(getToday());

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, start,
				new HashMap<String, String>() {
					{
						put(PARTIAL_FACTOR.getName(), "0.5");
						put(TC2.getName(),format("\"%s\"", random(PARTIAL_TIME).getValue()));
					}
				});
		
		
		addIT(aonContext, contract, LeaveType.PATERNITY, start, null, null);
		addData(aonContext, contract, start, null, ContextVariable.PATERNITY_FACTOR, 0.5);
		
		Date end = getLastDayOfMonth(start);
		
		ISQLContractSalaryCalculatorContext ctx = 
		getContractSalaryCalculatorContext(connection, start, end, end, contract);

		List<ITimedResult<Double>> totalWorkedDays = ctx.getExpressionContext().eval(ContextVariable.TOTAL_WORKED_DAYS.getName(), start, end, Double.class);
		
		totalWorkedDays.forEach( r -> {
			org.junit.Assert.assertEquals(r.getPeriod().getStart(), start);
			org.junit.Assert.assertEquals(r.getPeriod().getEnd(), end);
			org.junit.Assert.assertEquals(r.getValue(), AonDateUtils.get(end, Calendar.DAY_OF_MONTH) * 0.25, DELTA);
			
		});
		

	}

	@Test
	public void testTotalWorkDaysII() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date start = getFirstDayOfYear(getToday());

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, start,
				new HashMap<String, String>() {
					{
						put(PARTIAL_FACTOR.getName(), "0.5");
						put(TC2.getName(),format("\"%s\"", random(PARTIAL_TIME).getValue()));
					}
				});
		
		
		Date end = getLastDayOfMonth(start);
		
		ISQLContractSalaryCalculatorContext ctx = 
		getContractSalaryCalculatorContext(connection, start, end, end, contract);

		List<ITimedResult<Double>> totalWorkedDays = ctx.getExpressionContext().eval(ContextVariable.TOTAL_WORKED_DAYS.getName(), start, end, Double.class);
		
		totalWorkedDays.forEach( r -> {
			org.junit.Assert.assertEquals(r.getPeriod().getStart(), start);
			org.junit.Assert.assertEquals(r.getPeriod().getEnd(), end);
			org.junit.Assert.assertEquals(AonDateUtils.get(end, Calendar.DAY_OF_MONTH) * 0.5, r.getValue(), DELTA);
			
		});
		

	}

	@Test
	public void testTotalWorkDaysIII() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date start = getFirstDayOfYear(getToday());

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, start,
				new HashMap<String, String>() {
					{
						put(TC2.getName(),format("\"%s\"", random(FULL_TIME).getValue()));
					}
				});
		
		
		Date end = getLastDayOfMonth(start);
		
		ISQLContractSalaryCalculatorContext ctx = 
		getContractSalaryCalculatorContext(connection, start, end, end, contract);

		List<ITimedResult<Double>> totalWorkedDays = ctx.getExpressionContext().eval(ContextVariable.TOTAL_WORKED_DAYS.getName(), start, end, Double.class);
		
		totalWorkedDays.forEach( r -> {
			org.junit.Assert.assertEquals(r.getPeriod().getStart(), start);
			org.junit.Assert.assertEquals(r.getPeriod().getEnd(), end);
			org.junit.Assert.assertEquals(AonDateUtils.get(end, Calendar.DAY_OF_MONTH),r.getValue(),  DELTA);
			
		});
		

	}

	@Test
	public void testTotalWorkDaysIV() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date start = getFirstDayOfYear(getToday());

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, start,
				new HashMap<String, String>() {
					{
						put(TC2.getName(),format("\"%s\"", random(PARTIAL_TIME).getValue()));
					}
				});
		
		
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, start, null, null);
		
		Date end = getLastDayOfMonth(start);
		
		ISQLContractSalaryCalculatorContext ctx = 
		getContractSalaryCalculatorContext(connection, start, end, end, contract);

		List<ITimedResult<Double>> totalWorkedDays = ctx.getExpressionContext().eval(ContextVariable.TOTAL_WORKED_DAYS.getName(), start, end, Double.class);
		
		totalWorkedDays.forEach( r -> {
			org.junit.Assert.assertEquals(r.getPeriod().getStart(), start);
			org.junit.Assert.assertEquals(r.getPeriod().getEnd(), end);
			org.junit.Assert.assertEquals(0.00, r.getValue(), DELTA);
			
		});
	}

	@Test
	public void testTotalWorkDaysV() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date start = getFirstDayOfYear(getToday());

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, start,
				new HashMap<String, String>() {
					{
						put(TC2.getName(),format("\"%s\"", random(FULL_TIME).getValue()));
					}
				});
		
		
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, add(start, Calendar.DAY_OF_MONTH, 10), null, null);
		
		Date end = getLastDayOfMonth(start);
		
		ISQLContractSalaryCalculatorContext ctx = 
		getContractSalaryCalculatorContext(connection, start, end, end, contract);

		List<ITimedResult<Double>> totalWorkedDays = ctx.getExpressionContext().eval(ContextVariable.TOTAL_WORKED_DAYS.getName(), start, end, Double.class);
		
		totalWorkedDays.forEach( r -> {
			org.junit.Assert.assertEquals(r.getPeriod().getStart(), start);
			org.junit.Assert.assertEquals(r.getPeriod().getEnd(), end);
			org.junit.Assert.assertEquals(10, r.getValue() , DELTA);
			
		});
	}

	@Test
	public void testTotalWorkDaysVI() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date start = getFirstDayOfYear(getToday());

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, start,
				new HashMap<String, String>() {
					{
						put(TC2.getName(),format("\"%s\"", random(FULL_TIME).getValue()));
					}
				});
		
		Date maternityStart = add(start, Calendar.DAY_OF_MONTH, 10);
		addIT(aonContext, contract, LeaveType.MATERNITY, maternityStart , null, null);
		addData(aonContext, contract, maternityStart, null, ContextVariable.MATERNITY_FACTOR, 0.5);
		
		Date end = getLastDayOfMonth(start);
		
		ISQLContractSalaryCalculatorContext ctx = 
		getContractSalaryCalculatorContext(connection, start, end, end, contract);

		List<ITimedResult<Double>> totalWorkedDays = ctx.getExpressionContext().eval(ContextVariable.TOTAL_WORKED_DAYS.getName(), start, end, Double.class);
		
		totalWorkedDays.forEach( r -> {
			org.junit.Assert.assertEquals(r.getPeriod().getStart(), start);
			org.junit.Assert.assertEquals(r.getPeriod().getEnd(), end);
			org.junit.Assert.assertEquals(10 +  (AonDateUtils.get(end, Calendar.DAY_OF_MONTH) -10 ) * 0.5, r.getValue() , DELTA);
			
		});
	}

	// ------------------------------------------------------------------------

	protected static <T> T random(T arr[]) {
		return arr[(int) ((int) (Math.random() * arr.length))];
	}

	protected void assertEquals(ITimedResult<Double> var, Double value,
			Date start, Date end) {
		Assert.assertEquals(new Period(start, end), var.getPeriod());
		Assert.assertEquals(value, var.getValue(), DELTA);
	}

	protected void assertEquals(List<ITimedResult<Double>> vars, Double value,
			Date start, Date end) {
		Assert.assertEquals(1, vars.size());
		Assert.assertEquals(value, vars.get(0).getValue(), DELTA);
		Assert.assertEquals(new Period(start, end), vars.get(0).getPeriod());
	}

	protected void assertEquals(SQLContractSalaryCalculatorContext ctx,
			Double value, Date start, Date end, Double monthDays)
			throws UndefinedVariablesException, ExpressionException {
		List<ITimedResult<Double>> workedDays = null;
		try {
			workedDays = ctx.getExpressionContext()
					.eval(format("%s", WORKED_DAYS), start, end, Double.class);
		} catch ( UndefinedVariablesException e) {
			Assert.assertEquals(0.00, value);
			return;
		}

		int months = 0;
		for (Date date = getFirstDayOfMonth(start); date.compareTo(end) <= 0; date = add(
				date, MONTH, 1))
			months++;

		System.out.printf("%tF..%tF ( %d ): \r\n", start, end, months);
		Assert.assertEquals(months, workedDays.size());

		double values = 0.00;
		for (ITimedResult<Double> workedDay : workedDays) {
			double ctxMonthDays = ctx.getVariable(MONTH_DAYS,
					workedDay.getPeriod(), Number.class).doubleValue();
			if (monthDays != null)
				Assert.assertEquals(monthDays, ctxMonthDays, DELTA);
			System.out.printf("\t%tF..%tF : %f ( %f month days)\r\n", workedDay
					.getPeriod().getStart(), workedDay.getPeriod().getEnd(),
					workedDay.getValue(), ctxMonthDays);

			values += workedDay.getValue();
//			values += workedDay.getValue()
//					* (monthDays != null ? getMax(workedDay.getPeriod()
//							.getStart(), DAY_OF_MONTH)
//							/ monthDays : 1.00);
		}

		
		System.out.printf("%f == %f \r\n", values, value );

		Assert.assertEquals(start, workedDays.get(0).getPeriod().getStart());
		Assert.assertEquals(end, workedDays.get(months - 1).getPeriod()
				.getEnd());
		Assert.assertEquals(value, values, DELTA);
	}
}
