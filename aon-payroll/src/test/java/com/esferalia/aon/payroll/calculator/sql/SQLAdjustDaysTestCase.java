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
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SALARY_DAYS;
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
import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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

import junit.framework.Assert;

/**
 * @author rtrepiana
 *
 */

public class SQLAdjustDaysTestCase extends AbstractSQLTestCase {

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
		
		Date firstDayOfYear = getFirstDayOfYear(getToday());
		
		@SuppressWarnings("serial")
		ContractRecord contract = 
		newContract(aonContext, getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
					    put(MONTH_DAYS.getName(), "30.00");
					    put(TC2.getName(), format("\"%s\"", random(FULL_TIME).getValue()));
					}
				});

		for ( int i = 0; i < 11 ; i++) {
			Date startDate = add(firstDayOfYear, Calendar.MONTH,i);
			Date endDate = getLastDayOfMonth(startDate);
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
        		for ( ContextVariable var : new ContextVariable [] {WORKED_DAYS, QUOTE_DAYS, SALARY_DAYS}) {
        		    Double days = eval(ctx, var.getName());
        		    assertEquals(30.00, days, DELTA);
        		}
		}
		
		for ( int i = 0; i < 11 ; i++) {
			Date startDate = add(firstDayOfYear, Calendar.MONTH,i);
			Date startItDate = add(startDate, Calendar.DAY_OF_MONTH, 10 );
			Date endItDate = add(startItDate, Calendar.DAY_OF_MONTH, 7 );
			addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startItDate, endItDate, null);
		}

		for ( int i = 0; i < 11 ; i++) {
			Date startDate = add(firstDayOfYear, Calendar.MONTH,i);
			Date endDate = getLastDayOfMonth(startDate);
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
        		for ( ContextVariable var : new ContextVariable [] {QUOTE_DAYS, SALARY_DAYS}) {
        		    Double days = eval(ctx, var.getName());
        		    assertEquals(30.00, days, DELTA);
        		}
		}
	}
	
	@Test
	public void testPartialTimeDays540() throws ExpressionException,
			SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date firstDayOfYear = getFirstDayOfYear(getToday());
		
		@SuppressWarnings("serial")
		ContractRecord contract = 
		newContract(aonContext, getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
					    put(MONTH_DAYS.getName(), "30.00");
					    put(PARTIAL_FACTOR.getName(), "0.33");
					    put(TC2.getName(), "\"540\"");
					    put(ContextVariable.MONTHLY_ADJUST.getName(), "true");
					}
				});

		for ( int i = 0; i < 11 ; i++) {
			Date startDate = add(firstDayOfYear, Calendar.MONTH,i);
			Date endDate = getLastDayOfMonth(startDate);
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
        		for ( ContextVariable var : new ContextVariable [] {QUOTE_DAYS, SALARY_DAYS}) {
        		    Double days = eval(ctx, var.getName());
        		    assertEquals(30.00, days, DELTA);
        		}
		}
		
		for ( int i = 0; i < 11 ; i++) {
			Date startDate = add(firstDayOfYear, Calendar.MONTH,i);
			Date startItDate = add(startDate, Calendar.DAY_OF_MONTH, 10 );
			Date endItDate = add(startItDate, Calendar.DAY_OF_MONTH, 7 );
			addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startItDate, endItDate, null);
		}

		for ( int i = 0; i < 11 ; i++) {
			Date startDate = add(firstDayOfYear, Calendar.MONTH,i);
			Date endDate = getLastDayOfMonth(startDate);
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
        		for ( ContextVariable var : new ContextVariable [] {QUOTE_DAYS, SALARY_DAYS}) {
        		    Double days = eval(ctx, var.getName());
        		    assertEquals(30.00, days, DELTA);
        		}
		}
	}
	
	
	private Double eval(ISQLContractSalaryCalculatorContext ctx, String expression )
		throws UndefinedVariablesException, ExpressionException {
	    return ctx.getExpressionContext()
		    .eval(expression, ctx.getStartDate(), ctx.getEndDate(), Number.class).stream()
		    .map(ITimedResult::getValue).collect(Collectors.summingDouble(Number::doubleValue));
	}

	// ------------------------------------------------------------------------

	protected static <T> T random(T arr[]) {
		return arr[(int) ((int) (Math.random() * arr.length))];
	}

}
