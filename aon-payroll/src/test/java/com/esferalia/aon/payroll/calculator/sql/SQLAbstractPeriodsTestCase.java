package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.OCCUPATION;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PARTIAL_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_GROUP;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;
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
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.Period;

public abstract class SQLAbstractPeriodsTestCase extends AbstractSQLTestCase {

	protected static final double DELTA = 0.000000001;
	private static ContractCode PARTIAL_TIME[] = { C200, C209, C230, C239,
				C250, C289, C501, C502, C503, C508, C510, C518, C520, C530, C540,
				C541, C550, C552, };
	private static ContractCode FULL_TIME[] = { C100, C109, C130, C139,
				C150,
				C189, // indefinite fulltime
				C401, C402, C403, C408, C410, C418, C420, C421, C430, C441, C450,
				C452, // partial & temp fulltime
		};

	public SQLAbstractPeriodsTestCase() {
		super();
	}

	public abstract ContextVariable getDaysVariable();
	@Test
	public void testFullTimeQuoteDays() throws ExpressionException,
			SQLException {
			
				Connection connection = getConnection();
				AONContext aonContext = new AONContext(connection);
			
				ContractRecord contract = newContract(aonContext, getToday(),
						new HashMap<String, String>() {
							{
								put(TC2.getName(), C100.getValue());
							}
						});
			
				Date startDate = getFirstDayOfMonth(getToday());
				Date endDate = getLastDayOfMonth(getToday());
			
				List<ITimedResult<Double>> quoteDays = getContractSalaryCalculatorContext(
						connection, startDate, endDate, endDate, contract)
						.getExpressionContext().eval(format("%s", getDaysVariable()),
								startDate, endDate, Double.class);
			
				assertEquals(1, quoteDays.size());
				assertEquals((double) (get(endDate, DAY_OF_MONTH) - (get(getToday(),
								DAY_OF_MONTH) - 1)), quoteDays.get(0).getValue(), DELTA,getDaysVariable().getName());
				assertEquals(quoteDays.get(0).getPeriod(),
						new Period(getToday(), endDate),getDaysVariable().getName());
			
				startDate = getFirstDayOfMonth(add(getToday(), MONTH, 1));
				endDate = getLastDayOfMonth(add(getToday(), MONTH, 1));
			
				quoteDays = getContractSalaryCalculatorContext(connection, startDate,
						endDate, endDate, contract).getExpressionContext().eval(
						format("%s", getDaysVariable()), startDate, endDate, Double.class);
			
				assertEquals(1, quoteDays.size());
				assertEquals((double) (get(endDate, DAY_OF_MONTH)), quoteDays.get(0)
								.getValue(), DELTA,getDaysVariable().getName());
				assertEquals(quoteDays.get(0).getPeriod(),
						new Period(startDate, endDate),getDaysVariable().getName());
			
			}

	@Test
	public void testFullTimeQuoteDaysStrikeI() throws ExpressionException,
			SQLException {
			
				Connection connection = getConnection();
				AONContext aonContext = new AONContext(connection);
			
				ContractRecord contract = newContract(aonContext, getToday(),
						new HashMap<String, String>() {
							{
								put(TC2.getName(), C100.getValue());
							}
						});
			
				Date startDate = getFirstDayOfMonth(add(getToday(), MONTH, 1));
				Date endDate = getLastDayOfMonth(add(getToday(), MONTH, 1));
			
				Date strikeDayI = add(startDate, DAY_OF_MONTH, 13);
			
				addData(aonContext, contract, strikeDayI, strikeDayI,
						ContextVariable.STRIKE_FACTOR, 1.00);
			
				List<ITimedResult<Double>> quoteDays = getContractSalaryCalculatorContext(
						connection, startDate, endDate, endDate, contract)
						.getExpressionContext().eval(format("%s", getDaysVariable()),
								startDate, endDate, Double.class);
			
				assertEquals(2, quoteDays.size());
				
				assertEquals((double) (get(strikeDayI, DAY_OF_MONTH)-1), 
						quoteDays.get(0).getValue(),
						DELTA,getDaysVariable().getName());
				assertEquals(quoteDays.get(0).getPeriod(),
						new Period(startDate, add(strikeDayI, DAY_OF_MONTH,-1)),getDaysVariable().getName());
			
				assertEquals((double) (get(endDate, DAY_OF_MONTH)-get(strikeDayI, DAY_OF_MONTH)), 
						quoteDays.get(1).getValue(),
						DELTA,getDaysVariable().getName());
				assertEquals(quoteDays.get(1).getPeriod(),
						new Period(add(strikeDayI, DAY_OF_MONTH,1), endDate),getDaysVariable().getName());
			
				Date strikeDayII = add(startDate, DAY_OF_MONTH, 19);
				addData(aonContext, contract, strikeDayII, strikeDayII,
						ContextVariable.STRIKE_FACTOR, 1.00);
				
			
				quoteDays = getContractSalaryCalculatorContext(
						connection, startDate, endDate, endDate, contract)
						.getExpressionContext()
						.eval(format("%s", getDaysVariable()),
								startDate, endDate, Double.class);
			
				assertEquals(3, quoteDays.size());
				assertEquals((double) (get(strikeDayI, DAY_OF_MONTH)-1), 
						quoteDays.get(0).getValue(),
						DELTA,getDaysVariable().getName());
				assertEquals(quoteDays.get(0).getPeriod(),
						new Period(startDate, add(strikeDayI, DAY_OF_MONTH,-1)),getDaysVariable().getName());
				assertEquals((double) ((get(strikeDayII,DAY_OF_MONTH) -1 )-get(strikeDayI, DAY_OF_MONTH)), 
						quoteDays.get(1).getValue(),
						DELTA,getDaysVariable().getName());
				assertEquals(quoteDays.get(1).getPeriod(),
						new Period(add(strikeDayI, DAY_OF_MONTH,1), add(strikeDayII, DAY_OF_MONTH,-1)),getDaysVariable().getName());
				assertEquals((double) (get(endDate,DAY_OF_MONTH)-get(strikeDayII, DAY_OF_MONTH)), 
						quoteDays.get(2).getValue(),
						DELTA,getDaysVariable().getName());
				assertEquals(quoteDays.get(2).getPeriod(),
						new Period(add(strikeDayII, DAY_OF_MONTH,1), endDate),getDaysVariable().getName());
				
			}

	@Test
	public void testFullTimeQuoteDaysStrikeII() throws ExpressionException,
			SQLException {
			
				Connection connection = getConnection();
				AONContext aonContext = new AONContext(connection);
			
				ContractRecord contract = newContract(aonContext, getToday(),
						new HashMap<String, String>() {
							{
								put(TC2.getName(), C100.getValue());
							}
						});
			
				Date startDate = getFirstDayOfMonth(add(getToday(), MONTH, 1));
				Date endDate = getLastDayOfMonth(add(getToday(), MONTH, 1));
			
				addData(aonContext, contract, startDate, startDate,
						ContextVariable.STRIKE_FACTOR, 1.00);
			
				List<ITimedResult<Double>> quoteDays = getContractSalaryCalculatorContext(
						connection, startDate, endDate, endDate, contract)
						.getExpressionContext().eval(format("%s", getDaysVariable()),
								startDate, endDate, Double.class);
			
				assertEquals(1, quoteDays.size());
				
				assertEquals((double) (get(endDate, DAY_OF_MONTH)-1), 
						quoteDays.get(0).getValue(),
						DELTA,getDaysVariable().getName());
				assertEquals(quoteDays.get(0).getPeriod(),
						new Period(add(startDate, DAY_OF_MONTH,1), endDate),getDaysVariable().getName());
			}

	@Test
	public void testFullTimeQuoteDaysStrikeIII() throws ExpressionException,
			SQLException {
			
				Connection connection = getConnection();
				AONContext aonContext = new AONContext(connection);
			
				ContractRecord contract = newContract(aonContext, getToday(),
						new HashMap<String, String>() {
							{
								put(TC2.getName(), C100.getValue());
							}
						});
			
				Date startDate = getFirstDayOfMonth(add(getToday(), MONTH, 1));
				Date endDate = getLastDayOfMonth(add(getToday(), MONTH, 1));
			
				addData(aonContext, contract, endDate, endDate,
						ContextVariable.STRIKE_FACTOR, 1.00);
			
				List<ITimedResult<Double>> quoteDays = getContractSalaryCalculatorContext(
						connection, startDate, endDate, endDate, contract)
						.getExpressionContext().eval(format("%s", getDaysVariable()),
								startDate, endDate, Double.class);
			
				assertEquals(1, quoteDays.size());
				
				assertEquals((double) (get(endDate, DAY_OF_MONTH)-1), 
						quoteDays.get(0).getValue(),
						DELTA,getDaysVariable().getName());
				assertEquals(quoteDays.get(0).getPeriod(),
						new Period(startDate, add(endDate, DAY_OF_MONTH,-1)),getDaysVariable().getName());
			}

	@Test
	public void testFullTimeQuoteDaysQuoteGroupI()
			throws ExpressionException, SQLException {
			
				Connection connection = getConnection();
				AONContext aonContext = new AONContext(connection);
			
				ContractRecord contract = newContract(aonContext, getToday(),
						new HashMap<String, String>() {
							{
								put(TC2.getName(), C100.getValue());
								put(QUOTE_GROUP.getName(), "'05'");
							}
						});
			
				Date startDate = getFirstDayOfMonth(add(getToday(), MONTH, 1));
				Date endDate = getLastDayOfMonth(add(getToday(), MONTH, 1));
			
				Date group07Date = add(startDate, DAY_OF_MONTH, 13);
			
				addData(aonContext, contract, group07Date, null,
						ContextVariable.QUOTE_GROUP, "'07'");
				List<ITimedResult<Double>> quoteDays = getContractSalaryCalculatorContext(
						connection, startDate, endDate, endDate, contract)
						.getExpressionContext().eval(format("%s", getDaysVariable()),
								startDate, endDate, Double.class);
			
				assertEquals(2, quoteDays.size());
				assertEquals((double) (get(group07Date, DAY_OF_MONTH)-1), 
						quoteDays.get(0).getValue(),
						DELTA,getDaysVariable().getName());
				assertEquals(quoteDays.get(0).getPeriod(),
						new Period(startDate, add(group07Date, DAY_OF_MONTH,-1)),getDaysVariable().getName());
				assertEquals((double) (get(endDate, DAY_OF_MONTH) - get(group07Date, DAY_OF_MONTH) +1 ), 
						quoteDays.get(1).getValue(),
						DELTA,getDaysVariable().getName());
				assertEquals(quoteDays.get(1).getPeriod(),
						new Period(group07Date, endDate),getDaysVariable().getName());
			}

	@Test
	public void testFullTimeQuoteDaysQuoteGroupII()
			throws ExpressionException, SQLException {
			
				Connection connection = getConnection();
				AONContext aonContext = new AONContext(connection);
			
				ContractRecord contract = newContract(aonContext, getToday(),
						new HashMap<String, String>() {
							{
								put(TC2.getName(), C100.getValue());
								put(QUOTE_GROUP.getName(), "'05'");
							}
						});
			
				Date startDate = getFirstDayOfMonth(add(getToday(), MONTH, 1));
				Date endDate = getLastDayOfMonth(add(getToday(), MONTH, 1));
			
				Date group07Date = add(startDate, DAY_OF_MONTH, 13);
			
				addData(aonContext, contract, group07Date, null,
						ContextVariable.QUOTE_GROUP, "'05'");
				List<ITimedResult<Double>> quoteDays = getContractSalaryCalculatorContext(
						connection, startDate, endDate, endDate, contract)
						.getExpressionContext().eval(format("%s", getDaysVariable()),
								startDate, endDate, Double.class);
			
				assertEquals(1, quoteDays.size());
				assertEquals((double) get(endDate, DAY_OF_MONTH), 
						quoteDays.get(0).getValue(),
						DELTA,getDaysVariable().getName());
				assertEquals(quoteDays.get(0).getPeriod(),
						new Period(startDate, endDate),getDaysVariable().getName());
			}
	

	@Test
	public void testFullTimeQuoteDaysOcupationI() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext, getToday(),
				new HashMap<String, String>() {
					{
						put(TC2.getName(), C100.getValue());
					}
				});

		Date startDate = getFirstDayOfMonth(add(getToday(), MONTH, 1));
		Date endDate = getLastDayOfMonth(add(getToday(), MONTH, 1));

		Date occupationADate = add(startDate, DAY_OF_MONTH, 13);

		addData(aonContext, contract, occupationADate, null,
				OCCUPATION, "'a'");

		List<ITimedResult<Double>> quoteDays = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract)
				.getExpressionContext().eval(format("%s", getDaysVariable()),
						startDate, endDate, Double.class);

		assertEquals(2, quoteDays.size());
		assertEquals((double) (get(occupationADate, DAY_OF_MONTH)-1), 
				quoteDays.get(0).getValue(),
				DELTA,getDaysVariable().getName());
		assertEquals(quoteDays.get(0).getPeriod(),
				new Period(startDate, add(occupationADate, DAY_OF_MONTH,-1)),getDaysVariable().getName());
		assertEquals((double) (get(endDate, DAY_OF_MONTH) - get(occupationADate, DAY_OF_MONTH) +1 ), 
				quoteDays.get(1).getValue(),
				DELTA,getDaysVariable().getName());
		assertEquals(quoteDays.get(1).getPeriod(),
				new Period(occupationADate, endDate),getDaysVariable().getName());
	}
	
	@Test
	public void testFullTimeQuoteDaysOcupationII() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext, getToday(),
				new HashMap<String, String>() {
					{
						put(TC2.getName(), C100.getValue());
						put(OCCUPATION.getName(), "'c'");
					}
				});

		Date startDate = getFirstDayOfMonth(add(getToday(), MONTH, 1));
		Date endDate = getLastDayOfMonth(add(getToday(), MONTH, 1));

		Date occupationADate = add(startDate, DAY_OF_MONTH, 13);

		addData(aonContext, contract, occupationADate, null,
				OCCUPATION, "'a'");

		List<ITimedResult<Double>> quoteDays = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract)
				.getExpressionContext().eval(format("%s", getDaysVariable()),
						startDate, endDate, Double.class);

		assertEquals(2, quoteDays.size());
		assertEquals((double) (get(occupationADate, DAY_OF_MONTH)-1), 
				quoteDays.get(0).getValue(),
				DELTA,getDaysVariable().getName());
		assertEquals(quoteDays.get(0).getPeriod(),
				new Period(startDate, add(occupationADate, DAY_OF_MONTH,-1)),getDaysVariable().getName());
		assertEquals((double) (get(endDate, DAY_OF_MONTH) - get(occupationADate, DAY_OF_MONTH) +1 ), 
				quoteDays.get(1).getValue(),
				DELTA,getDaysVariable().getName());
		assertEquals(quoteDays.get(1).getPeriod(),
				new Period(occupationADate, endDate),getDaysVariable().getName());
	}

	@Test
	public void testFullTimeQuoteDaysOcupationIII() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext, getToday(),
				new HashMap<String, String>() {
					{
						put(TC2.getName(), C100.getValue());
						put(OCCUPATION.getName(), "'a'");
					}
				});

		Date startDate = getFirstDayOfMonth(add(getToday(), MONTH, 1));
		Date endDate = getLastDayOfMonth(add(getToday(), MONTH, 1));

		Date occupationADate = add(startDate, DAY_OF_MONTH, 13);

		addData(aonContext, contract, occupationADate, null,
				OCCUPATION, "'a'");

		List<ITimedResult<Double>> quoteDays = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract)
				.getExpressionContext().eval(format("%s", getDaysVariable()),
						startDate, endDate, Double.class);

		assertEquals(1, quoteDays.size());
		assertEquals((double) get(endDate, DAY_OF_MONTH), 
				quoteDays.get(0).getValue(),
				DELTA,getDaysVariable().getName());
		assertEquals(quoteDays.get(0).getPeriod(),
				new Period(startDate, endDate),getDaysVariable().getName());
	}
	
	@Test
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
		assertEquals((double) (get(factorChangeDate, DAY_OF_MONTH)-1), 
				quoteDays.get(0).getValue(),
				DELTA,getDaysVariable().getName());
		assertEquals(quoteDays.get(0).getPeriod(),
				new Period(startDate, add(factorChangeDate, DAY_OF_MONTH,-1)),getDaysVariable().getName());
		assertEquals((double) (get(endDate, DAY_OF_MONTH) - get(factorChangeDate, DAY_OF_MONTH) +1 ), 
				quoteDays.get(1).getValue(),
				DELTA,getDaysVariable().getName());
		assertEquals(quoteDays.get(1).getPeriod(),
				new Period(factorChangeDate, endDate),getDaysVariable().getName());
	}
	
	@Test
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
		assertEquals((double) get(endDate, DAY_OF_MONTH), 
				quoteDays.get(0).getValue(),
				DELTA,getDaysVariable().getName());
		assertEquals(quoteDays.get(0).getPeriod(),
				new Period(startDate, endDate),getDaysVariable().getName());
	}
	
	
	@Test
	public void testQuoteDaysTC2I() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext, getToday(),
				new HashMap<String, String>() {
					{
						put(TC2.getName(), C200.getValue());
						put(ContextVariable.MONDAY_HOURS.getName(),"8");
						put(ContextVariable.TUESDAY_HOURS.getName(),"8");
						put(ContextVariable.WEDNESDAY_HOURS.getName(),"8");
						put(ContextVariable.THURSDAY_HOURS.getName(),"8");
						put(ContextVariable.FRIDAY_HOURS.getName(),"8");
					}
				});

		Date startDate = getFirstDayOfMonth(add(getToday(), MONTH, 1));
		Date endDate = getLastDayOfMonth(add(getToday(), MONTH, 1));

		Date tc2ChangeDate = add(startDate, DAY_OF_MONTH, 13);

		addData(aonContext, contract, tc2ChangeDate, null,
				TC2, C209.getValue());

		List<ITimedResult<Double>> quoteDays = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract)
				.getExpressionContext().eval(format("%s", getDaysVariable()),
						startDate, endDate, Double.class);

		assertEquals(2, quoteDays.size());
		assertEquals((double) (get(tc2ChangeDate, DAY_OF_MONTH)-1), 
				quoteDays.get(0).getValue(),
				DELTA,getDaysVariable().getName());
		assertEquals(quoteDays.get(0).getPeriod(),
				new Period(startDate, add(tc2ChangeDate, DAY_OF_MONTH,-1)),getDaysVariable().getName());
		assertEquals((double) (get(endDate, DAY_OF_MONTH) - get(tc2ChangeDate, DAY_OF_MONTH) +1 ), 
				quoteDays.get(1).getValue(),
				DELTA,getDaysVariable().getName());
		assertEquals(quoteDays.get(1).getPeriod(),
				new Period(tc2ChangeDate, endDate),getDaysVariable().getName());
	}
}