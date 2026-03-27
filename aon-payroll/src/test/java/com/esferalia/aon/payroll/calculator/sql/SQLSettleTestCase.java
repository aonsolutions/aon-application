package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMPENSATION_CAUSE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CONTRACT_COMPLETE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FRIDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.OBJECTIVE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_GROUP;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.THURSDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TUESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WEDNESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORK_COMPLETE;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfYear;
import static java.lang.String.format;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.code.aon.common.enumeration.Month;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.AgreementExtraRecord;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.AgreementPaymentRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.ContractPaymentRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.occam.api.model.Salary.Embargo;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryData;
import com.esferalia.aon.payroll.SalaryDeduction;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedObject;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.util.AonDateUtils;

import static org.junit.jupiter.api.Assertions.*;

public class SQLSettleTestCase extends AbstractSQLTestCase {
	
	private static final double DELTA = 0.0005;

	@Test
	public void testSettleSeniority() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		
		Date contractStart = add(getToday(), Calendar.MONTH, -2);
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				new HashMap<String, String>() {
					{
						put(MONTH_DAYS.getName(), format("%d", 30));
						//put(COMPENSATION_CAUSE.getName(), CONTRACT_COMPLETE.getName());
					}
				}, new String[] { 
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" 
						}, 
				new String[] {
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_IRPF * 0.00/100" 
				}, 
		null);
		//@formatter:off
		
		
		addSSRegimeStuff(aonContext);
		
		Date seniority = add(contractStart, Calendar.YEAR, -2);
		addData(aonContext, contract, seniority, null, COMPENSATION_CAUSE, CONTRACT_COMPLETE.getName());

		ISQLContractSalaryCalculatorContext ctx = 
				getSQLContractSettleContext(connection, seniority, contract);
		
		double fix28Feb = 0; //Math.min(1, Math.abs(get(seniority, DAY_OF_MONTH) - get(getToday(), DAY_OF_MONTH)));
		assertEquals(seniority, ctx.getStartDate());
		for ( ITimedResult<Object> result:  ctx.getExpressionContext().eval("AÑOS_TRABAJADOS", seniority, getToday()))
			assertEquals( (2.00 + 2/12.00 + fix28Feb/12.00), result.getValue());

		double br = (1750.00) * 12.00 / 365; //AonDateUtils.getMax(getToday(), DAY_OF_YEAR);

		for ( ITimedResult<Object> result:  ctx.getExpressionContext().eval("SALARIO_DIA", seniority, getToday())) {
			assertEquals( seniority, result.getPeriod().getStart());
			assertEquals( br , result.getValue());
		}


		for ( ITimedResult<Object> result:  ctx.getExpressionContext().eval("12.00 * AÑOS_TRABAJADOS * SALARIO_DIA", seniority, getToday())) {
			assertEquals( seniority, result.getPeriod().getStart());
			assertEquals( 12.00 * (2.00 + 2/12.00+ fix28Feb/12.00) * br , result.getValue());
		}
			
		for ( ITimedResult<Object> result:  ctx.getExpressionContext().eval("FIN", seniority, getToday())) {
			assertEquals( seniority, result.getPeriod().getStart());
		}

		for ( ITimedResult<Object> result:  ctx.getExpressionContext().eval("CAUSA_INDEMNIZACION", seniority, getToday())) {
			assertEquals( seniority, result.getPeriod().getStart());
		}

		for ( ITimedResult<Object> result:  ctx.getExpressionContext().eval("(CAUSA_INDEMNIZACION == FIN) ? 12.00 * AÑOS_TRABAJADOS * SALARIO_DIA: 0.00", seniority, getToday()))
			System.out.println(result.getPeriod().getStart() + ".." + result.getPeriod().getEnd() + " = " + result.getValue() );
			//assertEquals( 12.00 * (2.00 + 2/12.00) * br , result.getValue());

		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		int days = (int) new Period(seniority, getToday()).daysStream().count();
		assertEquals(days, (int)settle.getTimeUnits());

		assertEquals( 12 * (2.00 + 2/12.00 + fix28Feb/12.00) * br, settle.getTotalPayment(), DELTA);
		assertEquals( 12 * ( 2 + 2/12.00 + fix28Feb/12.00) * br, settle.getTotalLiquid(), DELTA);
	}

	@Test
	public void testSettleContractEnd() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		
		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "(P_0 + P_1 + P_2)";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		
		Date contractStart = add(getToday(), Calendar.MONTH, -2);
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				new HashMap<String, String>() {
					{
						put(MONTH_DAYS.getName(), format("%d", 30));
						put(COMPENSATION_CAUSE.getName(), CONTRACT_COMPLETE.getName());
					}
				}, new String[] { "( P_1 + P_2 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" }, 
						new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * 0.00/100" }, 
						category);
		//@formatter:off
		
		addSSRegimeStuff(aonContext);
		
		int year = get(getToday(), Calendar.YEAR);
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		
		AgreementExtraRecord julyExtra;
		ISQLContractSalaryCalculatorContext extraCtx;
		JooqSalaryBuilder<Salary> jooqSalaryBuilder;
		
		if( get(getToday(), Calendar.MONTH) < Calendar.JULY) {
			julyExtra = getExtra(aonContext, agreement.getId(), "01/07");
			extraCtx = getExtraSalaryCalculatorContext(connection, contract, julyExtra, year, getToday(), getToday());
			jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			new SmartContractSalaryCalculator<Salary>( jooqSalaryBuilder ).calculate(extraCtx);
			jooqSalaryBuilder.execute();
		}
		if( get(getToday(), Calendar.MONTH) >= Calendar.JULY) {
			julyExtra = getExtra(aonContext, agreement.getId(), "01/07");
			extraCtx = getExtraSalaryCalculatorContext(connection, contract, julyExtra, year+1, getToday(), getToday());
			jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			new SmartContractSalaryCalculator<Salary>( jooqSalaryBuilder ).calculate(extraCtx);
			jooqSalaryBuilder.execute();
		}
		
		year = get(getToday(), Calendar.YEAR);
		AgreementExtraRecord decemberExtra = getExtra(aonContext, agreement.getId(), "15/12");
		extraCtx = getExtraSalaryCalculatorContext(connection, contract, decemberExtra, year, getToday(), getToday());
		jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>( jooqSalaryBuilder ).calculate(extraCtx);
		jooqSalaryBuilder.execute();

		ISQLContractSalaryCalculatorContext ctx = 
				getSmartSQLContractSettleContext(connection, contractStart, contract);
		
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		settle.getSalaryPayments().forEach( p -> System.out.println( p.getExpression() + " = " + p.getAmount() + " [ " + p.getType() + " ]") );
		
		double br = (1750.00 * 1.10) * (1 + 1.00 / 12 + 1.00 / 12) * 12 / 365; //AonDateUtils.getMax(getToday(), DAY_OF_YEAR);
		double fix28Feb = 0; //Math.min(1, Math.abs(get(contractStart, DAY_OF_MONTH) - get(getToday(), DAY_OF_MONTH)));
		

		assertEquals( 12 * (2/12.00 + fix28Feb/12.00) * br, settle.getTotalPayment(), DELTA);
		assertEquals( 12 * (2/12.00 + fix28Feb / 12.00) * br , settle.getTotalLiquid(), DELTA);
	}

	@Test
	public void testSettleObjective() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		
		
		Date contractStart = add(getToday(), Calendar.MONTH, -2);
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				new HashMap<String, String>() {
					{
						put(MONTH_DAYS.getName(), format("%d", 30));
						put(COMPENSATION_CAUSE.getName(), OBJECTIVE.getName());
					}
				}, new String[] { "( P_1 + P_2 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" }, 
						new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" }, category);
		//@formatter:off
		
		addSSRegimeStuff(aonContext);
		
		int year = get(getToday(), Calendar.YEAR);
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		
		AgreementExtraRecord julyExtra;
		ISQLContractSalaryCalculatorContext extraCtx;
		JooqSalaryBuilder<Salary> jooqSalaryBuilder;
		
		if( get(getToday(), Calendar.MONTH) < Calendar.JULY) {
			julyExtra = getExtra(aonContext, agreement.getId(), "01/07");
			extraCtx = getExtraSalaryCalculatorContext(connection, contract, julyExtra, year, getToday(), getToday());
			jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			new SmartContractSalaryCalculator<Salary>( jooqSalaryBuilder ).calculate(extraCtx);
			jooqSalaryBuilder.execute();
		}
		if( get(getToday(), Calendar.MONTH) >= Calendar.JULY) {
			julyExtra = getExtra(aonContext, agreement.getId(), "01/07");
			extraCtx = getExtraSalaryCalculatorContext(connection, contract, julyExtra, year+1, getToday(), getToday());
			jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			new SmartContractSalaryCalculator<Salary>( jooqSalaryBuilder ).calculate(extraCtx);
			jooqSalaryBuilder.execute();
		}
		
		year = get(getToday(), Calendar.YEAR);
		AgreementExtraRecord decemberExtra = getExtra(aonContext, agreement.getId(), "15/12");
		extraCtx = getExtraSalaryCalculatorContext(connection, contract, decemberExtra, year, getToday(), getToday());
		jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>( jooqSalaryBuilder ).calculate(extraCtx);
		jooqSalaryBuilder.execute();

		ISQLContractSalaryCalculatorContext ctx = 
				getSmartSQLContractSettleContext(connection, contractStart, contract);
		
		
		Salary settle = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double br = (1750.00 * 1.10) * (1 + 1.00 / 12 + 1.00 / 12) * 12 / 365; //AonDateUtils.getMax(getToday(), DAY_OF_YEAR);
		
		double fix28Feb = 0; //Math.min(1, Math.abs(get(contractStart, DAY_OF_MONTH) - get(getToday(), DAY_OF_MONTH)));
		
		assertEquals( 20 * (2/12.00 + fix28Feb / 12.00 ) * br, settle.getTotalPayment(), DELTA);
		
		System.out.println( (20 * (2/12.00 + fix28Feb / 12.00) * br ) + " = " + settle.getTotalPayment() );
		
		assertEquals( 20 * (2/12.00 + fix28Feb / 12.00) * br, settle.getTotalLiquid(), DELTA);
	}

	@Test
	public void testSettleWorkedDays() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		
		
		Date contractStart = add(getToday(), Calendar.MONTH, -2);
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				new HashMap<String, String>() {
					{
						put(MONTH_DAYS.getName(), format("%d", 30));
						put(COMPENSATION_CAUSE.getName(), ContextVariable.TEMP_COMPLETE.getName());
					}
				}, new String[] { "( P_1 + P_2 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" }, 
						new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" }, 
				category);
		//@formatter:off
		
		addSSRegimeStuff(aonContext);
		
		int year = get(getToday(), Calendar.YEAR);
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		
		AgreementExtraRecord julyExtra;
		ISQLContractSalaryCalculatorContext extraCtx;
		JooqSalaryBuilder<Salary> jooqSalaryBuilder;
		
		if( get(getToday(), Calendar.MONTH) < Calendar.JULY) {
			julyExtra = getExtra(aonContext, agreement.getId(), "01/07");
			extraCtx = getExtraSalaryCalculatorContext(connection, contract, julyExtra, year, getToday(), getToday());
			jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			new SmartContractSalaryCalculator<Salary>( jooqSalaryBuilder ).calculate(extraCtx);
			jooqSalaryBuilder.execute();
		}
		if( get(getToday(), Calendar.MONTH) >= Calendar.JULY) {
			julyExtra = getExtra(aonContext, agreement.getId(), "01/07");
			extraCtx = getExtraSalaryCalculatorContext(connection, contract, julyExtra, year+1, getToday(), getToday());
			jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			new SmartContractSalaryCalculator<Salary>( jooqSalaryBuilder ).calculate(extraCtx);
			jooqSalaryBuilder.execute();
		}
		
		year = get(getToday(), Calendar.YEAR);
		AgreementExtraRecord decemberExtra = getExtra(aonContext, agreement.getId(), "15/12");
		extraCtx = getExtraSalaryCalculatorContext(connection, contract, decemberExtra, year, getToday(), getToday());
		jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>( jooqSalaryBuilder ).calculate(extraCtx);
		jooqSalaryBuilder.execute();
		
		ISQLContractSalaryCalculatorContext ctx = 
				getSmartSQLContractSettleContext(connection, contractStart, contract);
		
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		settle.getSalaryPayments().forEach(p -> System.out.println( p.getExpression() + ":" + p.getAmount() + "," + p.getQuote() ));
		
		double br = (1750.00 * 1.10) * (1 + 1.00 / 12 + 1.00 / 12) * 12 / 365; //AonDateUtils.getMax(getToday(), DAY_OF_YEAR);
		
		long workedDays = new Period(contractStart, getToday()).getDays();
		
		org.junit.assertEquals( 12 * (workedDays/365.00) * br, settle.getTotalPayment(), DELTA);
		
		System.out.println( (12 * (workedDays/365.00) * br) + " = " + settle.getTotalPayment() );
		
		org.junit.assertEquals(12 * (workedDays/365.00) * br, settle.getTotalLiquid(), DELTA);
		
		setData(aonContext, contract, 
			add(getToday(), Calendar.DAY_OF_MONTH,1)
			, null
			, new HashMap<String, String>() {
		    	{
		    	    put("DIAS_VACACIONES_NO_DISFRUTADOS", format("%d", 4));
		    	}
		});

		ctx = getSmartSQLContractSettleContext(connection, contractStart, contract);
		settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		org.junit.assertEquals(12 * (workedDays/365.00) * br + 4 * br, settle.getTotalPayment(), DELTA);
		
		System.out.println( (12 * (workedDays/365.00) * br + 4 * br) + " = " + settle.getTotalPayment() );
		
	}

	@Test
	public void testSettleVacations() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		
		
		Date contractStart = add(getToday(), Calendar.MONTH, -2);
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				new HashMap<String, String>() {
					{
						put(MONTH_DAYS.getName(), format("%d", 30));
						put(COMPENSATION_CAUSE.getName(), OBJECTIVE.getName());
					}
				}, new String[] { "( P_1 + P_2 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES"
						}, 
						new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" }, category);
		//@formatter:off
		
		addSSRegimeStuff(aonContext);
		
		int year = get(getToday(), Calendar.YEAR);
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());

		AgreementExtraRecord julyExtra;
		ISQLContractSalaryCalculatorContext extraCtx;
		JooqSalaryBuilder<Salary> jooqSalaryBuilder;
		
		if( get(getToday(), Calendar.MONTH) < Calendar.JULY) {
			julyExtra = getExtra(aonContext, agreement.getId(), "01/07");
			extraCtx = getExtraSalaryCalculatorContext(connection, contract, julyExtra, year, getToday(), getToday());
			jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			new SmartContractSalaryCalculator<Salary>( jooqSalaryBuilder ).calculate(extraCtx);
			jooqSalaryBuilder.execute();
		}
		if( get(getToday(), Calendar.MONTH) >= Calendar.JULY) {
			julyExtra = getExtra(aonContext, agreement.getId(), "01/07");
			extraCtx = getExtraSalaryCalculatorContext(connection, contract, julyExtra, year+1, getToday(), getToday());
			jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			new SmartContractSalaryCalculator<Salary>( jooqSalaryBuilder ).calculate(extraCtx);
			jooqSalaryBuilder.execute();
		}
		
		year = get(getToday(), Calendar.YEAR);
		AgreementExtraRecord decemberExtra = getExtra(aonContext, agreement.getId(), "15/12");
		extraCtx = getExtraSalaryCalculatorContext(connection, contract, decemberExtra, year, getToday(), getToday());
		jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>( jooqSalaryBuilder ).calculate(extraCtx);
		jooqSalaryBuilder.execute();

		setData(aonContext, contract, 
				add(getToday(), Calendar.DAY_OF_MONTH,1)
				, null
				, new HashMap<String, String>() {
			{
				put("DIAS_VACACIONES_NO_DISFRUTADOS", format("%d", 4));
			}
		});
		
		ISQLContractSalaryCalculatorContext ctx = 
				getSmartSQLContractSettleContext(connection, contractStart, contract);
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double br = (1750.00 * 1.10) * (1 + 1.00 / 12 + 1.00 / 12) * 12 / 365; 
		
//		settle.getSalaryDatas().stream().forEach(d->System.out.println(d.getName() + " = "  + d.getExpression() ));
//		settle.getSalaryPayments().stream().forEach(p->System.out.println(p.getExpression() + " = "  + p.getAmount() ));
		double fix28Feb = 0; //Math.min(1, Math.abs(get(contractStart, DAY_OF_MONTH) - get(getToday(), DAY_OF_MONTH)));

		assertEquals( 20 * (2/12.00 + fix28Feb / 12.00) * br + ( br * 4 ), settle.getTotalPayment(), DELTA);
		
		assertEquals( br * 4 , settle.getCommonBase(), DELTA);
		
	}

	@Test
	public void testSettleVacationsII() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		
		Date contractStart = add(getToday(), Calendar.MONTH, -2);
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				getToday(),
				new HashMap<String, String>() {
					{
						put(TC2.getName(), "\"402\"");
						put(QUOTE_GROUP.getName(), "\"08\"");
						put(MONTH_DAYS.getName(), format("%d", 30));
						put(COMPENSATION_CAUSE.getName(), OBJECTIVE.getName());
					}
				}, 
				new String[] { 
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				"250.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				//"BASE_CGP*(isdef PORCENTAJE_DESMPL?PORCENTAJE_DESMPL:PORCENTAJE_DESMPL=(INDEFINIDO?1.55:1.60))/100"
				"BASE_CGP*(INDEFINIDO?1.55:1.60)/100"
				}, 
				null);
		//@formatter:off
		
		addSSRegimeStuff(aonContext);

		setData(aonContext, contract, 
				add(getToday(), Calendar.DAY_OF_MONTH,1)
				, null
				, new HashMap<String, String>() {
			{
				put("DIAS_VACACIONES_NO_DISFRUTADOS", format("%d", 4));
			}
		});
		
		ISQLContractSalaryCalculatorContext ctx = 
				getSmartSQLContractSettleContext(connection, contractStart, contract);
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double br = (1750.00) * 12 / 365; 
		
//		settle.getSalaryDatas().stream().forEach(d->System.out.println(d.getName() + " = "  + d.getExpression() ));
//		settle.getSalaryPayments().stream().forEach(p->System.out.println(p.getExpression() + " = "  + p.getAmount() ));

		double fix28Feb = 0; //Math.min(1, Math.abs(get(contractStart, DAY_OF_MONTH) - get(getToday(), DAY_OF_MONTH)));

		assertEquals( br * 4 , settle.getCommonBase(), DELTA);
		assertEquals( 20 * (2/12.00 + fix28Feb / 12.00) * br + ( br * 4 ), settle.getTotalPayment(), DELTA);
		assertEquals( settle.getCommonBase() * 1.60 / 100 , settle.getTotalDeduction(), DELTA);
	
		
	}

	@Test
	public void testSettleVacationsIII() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		
		Date contractStart = add(getToday(), Calendar.MONTH, -2);
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				getToday(),
				new HashMap<String, String>() {
					{
						put(TC2.getName(), "\"402\"");
						put(QUOTE_GROUP.getName(), "\"08\"");
						put(MONTH_DAYS.getName(), format("%d", 30));
						put(COMPENSATION_CAUSE.getName(), OBJECTIVE.getName());
					}
				}, 
				new String[] { 
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				"250.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				"BASE_CGP*(INDEFINIDO?1.55:1.60)/100"
				}, 
				null);
		//@formatter:off
		
		addSSRegimeStuff(aonContext);

		setData(aonContext, contract, 
				add(getToday(), Calendar.DAY_OF_MONTH,1)
				, null
				, new HashMap<String, String>() {
			{
				put("DIAS_VACACIONES_NO_DISFRUTADOS", format("%d", 20));
			}
		});
		
		ISQLContractSalaryCalculatorContext ctx = 
				getSmartSQLContractSettleContext(connection, contractStart, add(getToday(), Calendar.DAY_OF_MONTH, 20), contract);
		
		
		long workedDays = 
		ctx.getExpressionContext().eval("DIAS_TRABAJADOS", ctx.getStartDate(), ctx.getEndDate(), Long.class)
		.stream().collect(Collectors.summingLong(ITimedObject<Long>::getValue))
		;
		
		//Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		org.junit.assertEquals(new Period(contractStart, getToday()).getDays(), workedDays);
	
		
	}

	@Test
	public void testSettleVacations2Month() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		
		
		Date contractStart = add(getToday(), Calendar.MONTH, -2);
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				new HashMap<String, String>() {
					{
						put(MONTH_DAYS.getName(), format("%d", 30));
						put(COMPENSATION_CAUSE.getName(), OBJECTIVE.getName());
					}
				}, new String[] { "( P_1 + P_2 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" }, 
						new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" }, category);
		//@formatter:off
		
		addSSRegimeStuff(aonContext);
		
		int year = get(getToday(), Calendar.YEAR);
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());

		AgreementExtraRecord julyExtra;
		ISQLContractSalaryCalculatorContext extraCtx;
		JooqSalaryBuilder<Salary> jooqSalaryBuilder;
		
		if( get(getToday(), Calendar.MONTH) < Calendar.JULY) {
			julyExtra = getExtra(aonContext, agreement.getId(), "01/07");
			extraCtx = getExtraSalaryCalculatorContext(connection, contract, julyExtra, year, getToday(), getToday());
			jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			new SmartContractSalaryCalculator<Salary>( jooqSalaryBuilder ).calculate(extraCtx);
			jooqSalaryBuilder.execute();
		}
		if( get(getToday(), Calendar.MONTH) >= Calendar.JULY) {
			julyExtra = getExtra(aonContext, agreement.getId(), "01/07");
			extraCtx = getExtraSalaryCalculatorContext(connection, contract, julyExtra, year+1, getToday(), getToday());
			jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			new SmartContractSalaryCalculator<Salary>( jooqSalaryBuilder ).calculate(extraCtx);
			jooqSalaryBuilder.execute();
		}
		
		year = get(getToday(), Calendar.YEAR);
		AgreementExtraRecord decemberExtra = getExtra(aonContext, agreement.getId(), "15/12");
		extraCtx = getExtraSalaryCalculatorContext(connection, contract, decemberExtra, year, getToday(), getToday());
		jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>( jooqSalaryBuilder ).calculate(extraCtx);
		jooqSalaryBuilder.execute();

		Date startNoHolidays = add(getToday(), Calendar.DAY_OF_MONTH,1);
		Date endNoHolidays = AonDateUtils.getLastDayOfMonth(startNoHolidays) ;
		
		int noHolidays = AonDateUtils.get(endNoHolidays, DAY_OF_MONTH) - AonDateUtils.get(startNoHolidays, DAY_OF_MONTH) + 1;
		
		setData(aonContext, contract, 
				startNoHolidays
				, endNoHolidays
				, new HashMap<String, String>() {
			{
				put("DIAS_VACACIONES_NO_DISFRUTADOS", format("%d", noHolidays));
			}
		});
		
		
		setData(aonContext, contract, 
				AonDateUtils.add(endNoHolidays, DAY_OF_MONTH, 1)
				, AonDateUtils.add(endNoHolidays, DAY_OF_MONTH, 10)
				, new HashMap<String, String>() {
			{
				put("DIAS_VACACIONES_NO_DISFRUTADOS", format("%d", 10));
			}
		});

		ISQLContractSalaryCalculatorContext ctx = 
				getSmartSQLContractSettleContext(connection, contractStart, contract);
		
		Salary settle = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		settle.getSalaryPayments().forEach( p -> System.out.println(p.getDescription() +" : " + p.getAmount()+ "," + p.getQuote() ));
		
		double br = (1750.00 * 1.10) * (1 + 1.00 / 12 + 1.00 / 12) * 12 / 365; 
		double fix28Feb = 0; //Math.min(1, Math.abs(get(contractStart, DAY_OF_MONTH) - get(getToday(), DAY_OF_MONTH)));

		assertEquals( 20 * (2/12.00 + fix28Feb/12.00) * br + ( br * (10 + noHolidays) ), settle.getTotalPayment(), DELTA);
		
		assertEquals( br * ( 10 + noHolidays ) , settle.getRawCommonBase(), DELTA);
		// TODO: 
		//assertEquals( br * ( 10 + noHolidays ) , settle.getCommonBase(), DELTA);
		
		SalaryData cgcBases [] = settle.getSalaryDatas()
				.stream()
				.filter(s->s.getName().equals(ContextVariable.CGC_BASE.getName()))
				.sorted((s1,s2)-> s1.getStartDate().compareTo(s2.getStartDate()) )
				.toArray(l-> new SalaryData[l]);
		
		assertEquals(2, cgcBases.length);
		
		assertEquals(startNoHolidays, cgcBases[0].getStartDate());
		assertEquals(endNoHolidays, cgcBases[0].getEndDate());
		// TODO
		//assertEquals(br*noHolidays, Double.parseDouble(cgcBases[0].getExpression()), DELTA);
		
		assertEquals(AonDateUtils.add(endNoHolidays, DAY_OF_MONTH, 1), cgcBases[1].getStartDate());
		assertEquals(AonDateUtils.add(endNoHolidays, DAY_OF_MONTH, 10), cgcBases[1].getEndDate());
		assertEquals(br*10.00, Double.parseDouble(cgcBases[1].getExpression()), DELTA);
		
	}

	@Test
	public void testSettleVacations2Month30() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date firstDayOfYear = getFirstDayOfYear(getToday());
		ContractRecord contract = newContract(
		aonContext, 
		firstDayOfYear,
		new HashMap<String, String>() {
			{
				put(MONTH_DAYS.getName(), format("%d", 30));
				put(COMPENSATION_CAUSE.getName(), OBJECTIVE.getName());
			}
		}, new String[] { "( P_1 + P_2 ) * 0.10 ",
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" }, 
				new String[] {
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				"BASE_IRPF * PORCENTAJE_IRPF/100" }, 
		null);
		
		addSSRegimeStuff(aonContext);

		Date startNoHolidays = AonDateUtils.getLastDayOfMonth(firstDayOfYear) ;
		
		setData(aonContext, contract, 
				startNoHolidays
				, startNoHolidays
				, new HashMap<String, String>() {
			{
				put("DIAS_VACACIONES_NO_DISFRUTADOS", format("%d", 1));
			}
		});
		
		
		setData(aonContext, contract, 
				AonDateUtils.add(startNoHolidays, DAY_OF_MONTH, 1)
				, AonDateUtils.add(startNoHolidays, DAY_OF_MONTH, 10)
				, new HashMap<String, String>() {
			{
				put("DIAS_VACACIONES_NO_DISFRUTADOS", format("%d", 10));
			}
		});

		ISQLContractSalaryCalculatorContext ctx = 
				getSmartSQLContractSettleContext(connection, firstDayOfYear, contract);
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		settle.getSalaryPayments().forEach( p -> System.out.println(p.getDescription() +" : " + p.getAmount()+ "," + p.getQuote() ));
		
		double br = (1750.00 * 1.10) * 12 / 365; 

		org.junit.assertEquals( br * 11 , settle.getRawCommonBase(), DELTA);
		// TODO: 
		//assertEquals( br * ( 10 + noHolidays ) , settle.getCommonBase(), DELTA);
		
		SalaryData cgcBases [] = settle.getSalaryDatas()
				.stream()
				.sorted((s1,s2) -> s1.getName().compareTo(s2.getName()))
				.peek( s -> System.out.println("*" + s.getName() + " = " + s.getExpression() +","+ s.getStartDate() ))
				.filter(s->s.getName().equals(ContextVariable.CGC_BASE.getName()))
				.sorted((s1,s2)-> s1.getStartDate().compareTo(s2.getStartDate()) )
				.toArray(l-> new SalaryData[l]);
		//org.junit.assertEquals(2, cgcBases.length);
		
		org.junit.assertEquals(startNoHolidays, cgcBases[0].getStartDate());
		org.junit.assertEquals(startNoHolidays, cgcBases[0].getEndDate());

		org.junit.assertEquals(br, Double.parseDouble(cgcBases[0].getExpression()), DELTA);
		
		org.junit.assertEquals(AonDateUtils.add(startNoHolidays, DAY_OF_MONTH, 1), cgcBases[1].getStartDate());
		org.junit.assertEquals(AonDateUtils.add(startNoHolidays, DAY_OF_MONTH, 10), cgcBases[cgcBases.length-1].getEndDate());
		
		double cgcBases1 = 0.00;
		for ( int i = 1 ; i < cgcBases.length ; i++ )
		    cgcBases1 += Double.parseDouble(cgcBases[i].getExpression());
		
		org.junit.assertEquals(br*10.00, cgcBases1 , DELTA);
		
	}

	@Test
	@Disabled
	public void testSettleVacations2MonthJOOQ() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		
		
		Date contractStart = add(getToday(), Calendar.MONTH, -2);
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				new HashMap<String, String>() {
					{
						put(MONTH_DAYS.getName(), format("%d", 30));
						put(COMPENSATION_CAUSE.getName(), OBJECTIVE.getName());
					}
				}, new String[] { "( P_1 + P_2 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" }, 
						new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" }, category);
		//@formatter:off
		
		addSSRegimeStuff(aonContext);
		
		int year = get(getToday(), Calendar.YEAR);
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());

		AgreementExtraRecord julyExtra;
		ISQLContractSalaryCalculatorContext extraCtx;
		JooqSalaryBuilder<Salary> jooqSalaryBuilder;
		
		if( get(getToday(), Calendar.MONTH) < Calendar.JULY) {
			julyExtra = getExtra(aonContext, agreement.getId(), "01/07");
			extraCtx = getExtraSalaryCalculatorContext(connection, contract, julyExtra, year, getToday(), getToday());
			jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			new SmartContractSalaryCalculator<Salary>( jooqSalaryBuilder ).calculate(extraCtx);
			jooqSalaryBuilder.execute();
		}
		if( get(getToday(), Calendar.MONTH) >= Calendar.JULY) {
			julyExtra = getExtra(aonContext, agreement.getId(), "01/07");
			extraCtx = getExtraSalaryCalculatorContext(connection, contract, julyExtra, year+1, getToday(), getToday());
			jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			new SmartContractSalaryCalculator<Salary>( jooqSalaryBuilder ).calculate(extraCtx);
			jooqSalaryBuilder.execute();
		}
		
		year = get(getToday(), Calendar.YEAR);
		AgreementExtraRecord decemberExtra = getExtra(aonContext, agreement.getId(), "15/12");
		extraCtx = getExtraSalaryCalculatorContext(connection, contract, decemberExtra, year, getToday(), getToday());
		jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>( jooqSalaryBuilder ).calculate(extraCtx);
		jooqSalaryBuilder.execute();
		
		Date startNoHolidays = add(getToday(), Calendar.DAY_OF_MONTH,1);
		Date endNoHolidays = AonDateUtils.getLastDayOfMonth(startNoHolidays) ;
		
		int noHolidays = AonDateUtils.get(endNoHolidays, DAY_OF_MONTH) - AonDateUtils.get(startNoHolidays, DAY_OF_MONTH) + 1;
		
		setData(aonContext, contract, 
				startNoHolidays
				, endNoHolidays
				, new HashMap<String, String>() {
			{
				put("DIAS_VACACIONES_NO_DISFRUTADOS", format("%d", noHolidays));
			}
		});
		
		
		setData(aonContext, contract, 
				AonDateUtils.add(endNoHolidays, DAY_OF_MONTH, 1)
				, AonDateUtils.add(endNoHolidays, DAY_OF_MONTH, 10)
				, new HashMap<String, String>() {
			{
				put("DIAS_VACACIONES_NO_DISFRUTADOS", format("%d", 10));
			}
		});

		ISQLContractSalaryCalculatorContext ctx = 
				getSmartSQLContractSettleContext(connection, contractStart, contract);
		
		jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new ContractSalaryCalculator<Salary>( jooqSalaryBuilder ).calculate(ctx);
		jooqSalaryBuilder.execute();
		
		Optional<com.esferalia.aon.occam.api.model.Salary> optional = AON.getSalaries(aonContext, props-> props.getContractProperty().eq(contract.getId()).and(props.getIsSettlementProperty().eq(true)) ).
		findFirst();
		
		assertTrue(optional.isPresent());
		
		com.esferalia.aon.occam.api.model.Salary settle = optional.get();
		
		double br = (1750.00 * 1.10) * (1 + 1.00 / 12 + 1.00 / 12) * 12 / 365; 

		assertEquals( 20 * (2/12.00) * br + ( br * (10 + noHolidays) ), settle.getTotalPayment(), DELTA);
		
		assertEquals( br * ( 10 + noHolidays) , settle.getCommonContingenciesBase(), DELTA);
		

		List<ContextData> cgcBases = settle.getContextData().get(ContextVariable.CGC_BASE.getName());
		
		Collections.sort(cgcBases, (d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate()));
		
		assertEquals(2, cgcBases.size());

		assertEquals(startNoHolidays, cgcBases.get(0).getStartDate());
		assertEquals(endNoHolidays, cgcBases.get(0).getEndDate());
		assertEquals(br*noHolidays, Double.parseDouble(cgcBases.get(0).getExpression()), DELTA);

		assertEquals(AonDateUtils.add(endNoHolidays, DAY_OF_MONTH, 1), cgcBases.get(1).getStartDate());
		assertEquals(AonDateUtils.add(endNoHolidays, DAY_OF_MONTH, 10), cgcBases.get(1).getEndDate());
		assertEquals(br*10.00, Double.parseDouble(cgcBases.get(1).getExpression()), DELTA);
		
	}


	@Test
	public void testSettleVacationsCGC() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		cleanSystemDeductions(aonContext);
		cleanSystemPayments(aonContext);

		// @formatter:on
		Date contractStart = add(getToday(), Calendar.MONTH, -2);
		Date endContract = getLastDayOfMonth(getToday());
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				new HashMap<String, String>() {
					{
						put(MONTH_DAYS.getName(), format("%d", 30));
						put(COMPENSATION_CAUSE.getName(), OBJECTIVE.getName());
					}
				}, 
				new String[] {"1000.00 * DIAS_TRABAJADOS / DIAS_MES"}, 
				new String[] {"BASE_CGC * PORCENTAJE_CGC / 100.00 "}, 
				null);
		//@formatter:off
		
		addSSRegimeStuff(aonContext);
		
		Date startNoHolidays = add(endContract, Calendar.DAY_OF_MONTH,1);
		Date endNoHolidays = add(endContract, Calendar.DAY_OF_MONTH,11);
		
		
		setData(aonContext, contract, 
				startNoHolidays
				, endNoHolidays
				, new HashMap<String, String>() {
			{
				put("DIAS_VACACIONES_NO_DISFRUTADOS", format("%d", 10));
			}
		});
		
		
		ISQLContractSalaryCalculatorContext ctx = 
				getSQLContractSettleContext(connection, contractStart, contract);
		
		Salary settle = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		assertEquals(( 1000.00 * 12 / 365 * 10.00 ) , settle.getCommonBase());
		assertEquals(( 1000.00 * 12 / 365 * 10.00 ) * (4.70/100.00), settle.getTotalDeduction());
		
		
	}
	
	
	@Test
	public void testSettleVacationsIRPF() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		cleanSystemDeductions(aonContext);
		cleanSystemPayments(aonContext);

		// @formatter:on
		Date contractStart = getFirstDayOfYear(getToday());
		Date endContract = add(add(contractStart, Calendar.MONTH, 10), Calendar.DAY_OF_MONTH, 5 );
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				new HashMap<String, String>() {
					{
						put(MONTH_DAYS.getName(), format("%d", 30));
						put(COMPENSATION_CAUSE.getName(), OBJECTIVE.getName());
					}
				}, 
				new String[] {"2500.00 * DIAS_TRABAJADOS / DIAS_MES"}, 
				new String[] {"BASE_IRPF * PORCENTAJE_IRPF / 100.00 "}, 
				null);
		//@formatter:off
		
		addSSRegimeStuff(aonContext);
		
		Date startDate = contractStart;
		while ( endContract.after(startDate)) {
			Date endDate = getLastDayOfMonth(startDate);
			ISQLContractSalaryCalculatorContext ctx  =  
			getContractSalaryCalculatorContext(connection, startDate, endDate , endDate, contract);
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(startDate, Calendar.MONTH,1);
		}
		
		
		Date startNoHolidays = add(endContract, Calendar.DAY_OF_MONTH,1);
		Date endNoHolidays = add(endContract, Calendar.DAY_OF_MONTH,11);
		
		
		setData(aonContext, contract, 
				startNoHolidays
				, endNoHolidays
				, new HashMap<String, String>() {
			{
				put("DIAS_VACACIONES_NO_DISFRUTADOS", format("%d", 30));
			}
		});
		
		
		ISQLContractSalaryCalculatorContext ctx = 
				getSQLContractSettleContext(connection, contractStart, contract);
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		assertEquals(( 2500.00 * 12 / 365 * 30.00 ) , settle.getIrpfBase());
		
		Collection<SalaryDeduction> deductions = settle.getSalaryDeductions();
		org.junit.assertEquals(1, deductions.size());
		for ( SalaryDeduction d: deductions )
			System.out.println(d.getDescription() + " = " + d.getAmount());
		
	}

	@Test
	public void testSettleIRPF() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.DECEMBER;
						this.start = "01/12";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		
		Date contractStart = getFirstDayOfYear(getToday());
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				new HashMap<String, String>() {
					{
						put(TC2.getName(), "\"401\"");
						put(QUOTE_GROUP.getName(), "\"10\"");
						put(COMPENSATION_CAUSE.getName(), WORK_COMPLETE.getName());
					}
				}, new String[] { "( P_1 + P_2 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" }, 
						new String[] {
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100.00",
						"TRACE('I.R.P.F : %f\r\n', PORCENTAJE_IRPF)"}, 
						category);
		//@formatter:off
		
		addSSRegimeStuff(aonContext);
		
		// Save a Salaries
		for ( int i = 0; i < 11 ; i++) {
			java.sql.Date startDate = add( contractStart, Calendar.MONTH, i);
			java.sql.Date endDate = getLastDayOfMonth(startDate);
			ISQLContractSalaryCalculatorContext ctx = 
					getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			ContractSalaryCalculator<ISalary> calculator = new ContractSalaryCalculator<ISalary>();
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
		}

		java.sql.Date startDate = add( contractStart, Calendar.MONTH, 11);
		java.sql.Date endDate = getLastDayOfMonth(startDate);
		Salary salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Double irpf = (salary.getTotalIrpf() / salary.getIrpfBase()) * 100.00;
		
		
		Date contractEnd = add( getLastDayOfYear(getToday()), Calendar.MONTH, -1 );

		ISQLContractSalaryCalculatorContext ctx = 
				getSQLContractSettleContext(connection, contractStart, contractEnd, contract);
		
		
		Salary settle = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		Double sirpf = ( settle.getTotalIrpf() / settle.getIrpfBase() ) * 100.00;
		System.out.println(irpf + "(" + salary.getIrpfBase() + ")" + " = " + sirpf + "(" + settle.getIrpfBase() + ")");
		assertTrue(sirpf > 0 );
	}

	@Test
	public void testSettleWithIT() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.DECEMBER;
						this.start = "01/12";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		
		Date contractStart = add(getToday(), Calendar.YEAR, -1);
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				new HashMap<String, String>() {
					{
						put(TC2.getName(), "\"501\"");
						put(MONDAY_HOURS.getName(), format("%d", 4));
						put(TUESDAY_HOURS.getName(), format("%d", 4));
						put(WEDNESDAY_HOURS.getName(), format("%d", 4));
						put(THURSDAY_HOURS.getName(), format("%d", 4));
						put(FRIDAY_HOURS.getName(), format("%d", 4));

						put(QUOTE_GROUP.getName(), "\"10\"");
						put("DIAS_VACACIONES_NO_DISFRUTADOS", "10");
					}
				}, new String[] { "( P_1 + P_2 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" }, 
						new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * 0.00/100" }, category);
		//@formatter:off
		
		//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_1_3",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.60 * %s_4_15",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.60 * %s_16_20",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s_21",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		//@formatter:on

		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, add(getToday(), Calendar.MONTH, -1), null, null);
		
		addSystemData(aonContext
				, AonDateUtils.add(getFirstDayOfYear(getToday()), Calendar.YEAR, -3)
				, null
				, new HashMap<String, String>() {
					{
					put(MONTH_DAYS.getName(), "[ "
					+"\"10\": DIAS_NATURALES_MES,"
					+"\"11\": DIAS_NATURALES_MES][GRUPO_COTIZACION]");
					}
				});
		
		addSSRegimeStuff(aonContext);

		
		ISQLContractSalaryCalculatorContext settleCtx = 
				getSQLContractSettleContext(connection, contractStart, contract);
		
		settleCtx.getExpressionContext().eval("TRACE('SALARIO_DIA:%f\r\n', SALARIO_DIA)", contractStart, getToday())
		;
		
		Salary settle = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(settleCtx);
		

		assertTrue( settle.getTotalPayment() > 0.00);

	}
	

	@Test
	public void testSettleWithExtrasI() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		
		Date contractStart = getFirstDayOfYear(getToday());
		Date contractEnd = getToday();
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				contractEnd,
				new HashMap<String, String>() {
					{
						put(TC2.getName(), "\"100\"");
						put(MONTH_DAYS.getName(), "30");
						put(QUOTE_GROUP.getName(), "\"01\"");
					}
				}, new String[] { "( P_1 + P_2 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" }, 
						new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * 0.00/100" }, 
						category);
		//@formatter:off
		
		
		addSSRegimeStuff(aonContext);

		
		ISQLContractSalaryCalculatorContext settleCtx = 
				getSmartSQLContractSettleContext(connection, contractStart, contract);
		
		settleCtx.getExpressionContext().eval("TRACE('SALARIO_DIA:%f\r\n', SALARIO_DIA)", contractStart, getToday())
		;
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder() {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				System.out.println( description + ":" + amount);
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
			}
		}).calculate(settleCtx);
		
		
		int months = get(getToday(), Calendar.MONTH );
		//int days = Math.min(30, get(getToday(), Calendar.DAY_OF_MONTH ));
		int todayOfMonth = get(getToday(), Calendar.DAY_OF_MONTH );
		int lastdayOfMonth = AonDateUtils.getMax(getToday(), Calendar.DAY_OF_MONTH);
		int days = todayOfMonth == lastdayOfMonth ? 30 : Math.min(30, todayOfMonth );

		double decemberExtra = ( 1750.00 * 1.10 ) * ((months * 30) + days ) / 360;
		int julyExtraMonths = (months >= 6 ? months -6 : months );
		// Settle at 01/07, so we have two extras for July .
		//if ( get(getToday(), Calendar.MONTH ) == 6 && get(getToday(), Calendar.DAY_OF_MONTH ) == 1)
		//julyExtraMonths += 6;

		double julyExtra = ( 1750.00 * 1.10 ) * ((julyExtraMonths  * 30) + days ) / 360 ;
		

		if ( months == Calendar.DECEMBER && days > 15 )
			decemberExtra = 0.00; 
		// 'December Extra...' have been already emitted. 

		assertEquals( decemberExtra + julyExtra, settle.getTotalPayment(), DELTA);

	}

	@Test
	public void testSettleWithExtrasII() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		
		Date contractStart = getFirstDayOfYear(getToday());
		Date contractEnd = getToday();
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				new HashMap<String, String>() {
					{
						put(TC2.getName(), "\"100\"");
						put(MONTH_DAYS.getName(), "30");
						put(QUOTE_GROUP.getName(), "\"01\"");
					}
				}, new String[] { "( P_1 + P_2 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" }, 
						new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * 0.00/100" }, 
						category);
		//@formatter:off
		
		
		addSSRegimeStuff(aonContext);

		
		ISQLContractSalaryCalculatorContext settleCtx = 
				getSmartSQLContractSettleContext(connection, contractStart, contract);
		
		settleCtx.getExpressionContext().eval("TRACE('SALARIO_DIA:%f\r\n', SALARIO_DIA)", contractStart, getToday())
		;
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(settleCtx);
		
		
		int months = get(getToday(), Calendar.MONTH );
		int todayOfMonth = get(getToday(), Calendar.DAY_OF_MONTH );
		int lastdayOfMonth = AonDateUtils.getMax(getToday(), Calendar.DAY_OF_MONTH);
		int days = todayOfMonth == lastdayOfMonth ? 30 : Math.min(30, todayOfMonth );
				
		
		
		double decemberExtra = ( 1750.00 * 1.10 ) * ((months * 30) + days ) / 360;
		int julyExtraMonths = (months >= Calendar.JULY ? months - Calendar.JULY : months );
		// Settle at 01/07, so we have two extras for July .
		//if ( get(getToday(), Calendar.MONTH ) == 6 && get(getToday(), Calendar.DAY_OF_MONTH ) == 1)
		//	julyExtraMonths += 6;
		double julyExtra = ( 1750.00 * 1.10 ) * ((julyExtraMonths * 30) + days ) / 360 ;
		
		if ( months == Calendar.DECEMBER && days > 15 )
			decemberExtra = 0.00; 
		// 'December Extra...' have been already emitted. 
		assertEquals( decemberExtra + julyExtra, settle.getTotalPayment(), DELTA);

	}

	@Test
	public void testSettleWithExtrasIII() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		
		Date contractStart = getFirstDayOfYear(getToday());
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				new HashMap<String, String>() {
				{
					put(TC2.getName(), "\"100\"");
					put(MONTH_DAYS.getName(), "30");
					put(QUOTE_GROUP.getName(), "\"01\"");
				}
				}, 
				new String[] { "( P_1 + P_2 ) * 0.10 ",
					"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
					"250.00 * DIAS_TRABAJADOS / DIAS_MES" 
				}, 
				new String[] {
					"BASE_CGC * 0.10", 
					"BASE_CGP * 0.05",
					"BASE_IRPF * 0.00/100" 
				}, 
				category);
		//@formatter:off		
		
		addSSRegimeStuff(aonContext);
		int year = get(getToday(), Calendar.YEAR);
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord julyExtra = getExtra(aonContext, agreement.getId(), "01/07");
		
		ISQLContractSalaryCalculatorContext extraCtx = getExtraSalaryCalculatorContext(connection, contract, julyExtra, year, getToday(), getToday());
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>( jooqSalaryBuilder ).calculate(extraCtx);
		jooqSalaryBuilder.execute();

		if ( get(getToday(), Calendar.MONTH) >= Calendar.JULY ) { 
			extraCtx = getExtraSalaryCalculatorContext(connection, contract, julyExtra, year +1, getToday(), getToday());
			jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			new SmartContractSalaryCalculator<Salary>( jooqSalaryBuilder ).calculate(extraCtx);
			jooqSalaryBuilder.execute();
		}
		
		
		ISQLContractSalaryCalculatorContext settleCtx = 
				getSmartSQLContractSettleContext(connection, contractStart, contract);
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(settleCtx);
		
		int months = get(getToday(), Calendar.MONTH );

		//int days = Math.min(30, get(getToday(), Calendar.DAY_OF_MONTH ));
		int todayOfMonth = get(getToday(), Calendar.DAY_OF_MONTH );
		int lastdayOfMonth = AonDateUtils.getMax(getToday(), Calendar.DAY_OF_MONTH);
		int days = todayOfMonth == lastdayOfMonth ? 30 : Math.min(30, todayOfMonth );
		
		double decemberExtra = ( 1750.00 * 1.10 ) * ((months * 30) + days ) / 360;

		if ( months == Calendar.DECEMBER && days > 15 )
			decemberExtra = 0.00; 
		// 'December Extra...' have been already emitted. 
		
		assertEquals( decemberExtra , settle.getTotalPayment(), DELTA);
	}
		
	@Test
	public void testSettleWithExtrasIV() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		
		Date contractStart = getFirstDayOfYear(getToday());
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				new HashMap<String, String>() {
				{
					put(TC2.getName(), "\"100\"");
					put(MONTH_DAYS.getName(), "30");
					put(QUOTE_GROUP.getName(), "\"01\"");
				}
				}, 
				new String[] { "( P_1 + P_2 ) * 0.10 ",
					"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
					"250.00 * DIAS_TRABAJADOS / DIAS_MES" 
				}, 
				new String[] {
					"BASE_CGC * 0.10", 
					"BASE_CGP * 0.05",
					"BASE_IRPF * 0.00/100" 
				}, 
				category);
		//@formatter:off		
		
		addSSRegimeStuff(aonContext);
		
		int year = get(getToday(), Calendar.YEAR);
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord julyExtra = getExtra(aonContext, agreement.getId(), "01/07");
		
		ISQLContractSalaryCalculatorContext extraCtx = getExtraSalaryCalculatorContext(connection, contract, julyExtra, year, getToday(), getToday());
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>( jooqSalaryBuilder ).calculate(extraCtx);
		jooqSalaryBuilder.execute();

		if ( get(getToday(), Calendar.MONTH ) >= Calendar.JULY ) {
			extraCtx = getExtraSalaryCalculatorContext(connection, contract, julyExtra, year+1, getToday(), getToday());
			jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			new SmartContractSalaryCalculator<Salary>( jooqSalaryBuilder ).calculate(extraCtx);
			jooqSalaryBuilder.execute();
		}
		

		year = get(getToday(), Calendar.YEAR);
		AgreementExtraRecord decemberExtra = getExtra(aonContext, agreement.getId(), "15/12");
		extraCtx = getExtraSalaryCalculatorContext(connection, contract, decemberExtra, year, getToday(), getToday());
		jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>( jooqSalaryBuilder ).calculate(extraCtx);
		jooqSalaryBuilder.execute();
		
		ISQLContractSalaryCalculatorContext settleCtx = 
				getSmartSQLContractSettleContext(connection, contractStart, contract);
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(settleCtx);
		
		
		
		assertEquals( 0.00 , settle.getTotalPayment(), DELTA);
	}

	@Test
	public void testSettleWithExtrasV() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/01";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.DECEMBER;
						this.start = "01/07";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, });
		
		Date contractStart = getFirstDayOfYear(getToday());
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				new HashMap<String, String>() {
				{
					put(TC2.getName(), "\"100\"");
					put(MONTH_DAYS.getName(), "30");
					put(QUOTE_GROUP.getName(), "\"01\"");
				}
				}, 
				new String[] { "( P_1 + P_2 ) * 0.10 ",
					"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
					"250.00 * DIAS_TRABAJADOS / DIAS_MES" 
				}, 
				new String[] {
					"BASE_CGC * 0.10", 
					"BASE_CGP * 0.05",
					"BASE_IRPF * 0.00/100" 
				}, 
				category);
		//@formatter:off		
		
		addSSRegimeStuff(aonContext);
		
		int year = get(getToday(), Calendar.YEAR);
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord julyExtra = getExtra(aonContext, agreement.getId(), "01/07");
		
		ISQLContractSalaryCalculatorContext extraCtx = getExtraSalaryCalculatorContext(connection, contract, julyExtra, year, getToday(), getToday());
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>( jooqSalaryBuilder ).calculate(extraCtx);
		jooqSalaryBuilder.execute();
		

		Date endDate = add(add(getFirstDayOfYear(getToday()), Calendar.MONTH, 8), Calendar.DATE, 14 ); // 15/09
		
		ISQLContractSalaryCalculatorContext settleCtx = 
				getSmartSQLContractSettleContext(connection, contractStart, endDate, contract);
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(settleCtx);
		
		for ( SalaryPayment p : settle.getSalaryPayments() ) 
			System.out.println(p.getDescription() + "= " + p.getAmount() );
		
		assertEquals( 1750.00 *1.10 / 6 * 2.5 , settle.getTotalPayment(), DELTA);
	}

	// ------------------------------------------------------------------------
	
	@Test
	public void testSettleWithExtrasVI() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JANUARY;
						this.start = "01/12";
						this.end = "31/12";
						this.issue = "01/01";
					}
				}, });
		
		Date contractStart = getFirstDayOfYear(getToday());
		Date contractEnd = getToday();
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				contractEnd,
				new HashMap<String, String>() {
					{
						put(TC2.getName(), "\"100\"");
						put(MONTH_DAYS.getName(), "30");
						put(QUOTE_GROUP.getName(), "\"01\"");
					}
				}, new String[] { "( P_1 + P_2 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" }, 
						new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * 0.00/100" }, 
						category);
		//@formatter:off
		
		
		addSSRegimeStuff(aonContext);

		
		ISQLContractSalaryCalculatorContext settleCtx = 
				getSmartSQLContractSettleContext(connection, contractStart, contract);
		
		settleCtx.getExpressionContext().eval("TRACE('SALARIO_DIA:%f\r\n', SALARIO_DIA)", contractStart, getToday())
		;
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(settleCtx);
		
		
		int months = get(getToday(), Calendar.MONTH );
		int todayOfMonth = get(getToday(), Calendar.DAY_OF_MONTH );
		int lastdayOfMonth = AonDateUtils.getMax(getToday(), Calendar.DAY_OF_MONTH);
		int days = todayOfMonth == lastdayOfMonth ? 30 : Math.min(30, todayOfMonth );
		
		double decemberExtra = ( 1750.00 * 1.10 ) * ((months * 30) + days ) / 360;
		int julyExtraMonths = (months >= Calendar.JULY ? months - Calendar.JULY : months );
		// Settle at 01/07, so we have two extras for July .
		//if ( get(getToday(), Calendar.MONTH ) == 6 && get(getToday(), Calendar.DAY_OF_MONTH ) == 1)
		//	julyExtraMonths += 6;
		double julyExtra = ( 1750.00 * 1.10 ) * ((julyExtraMonths * 30) + days ) / 360 ;
		double bonusExtra = months == 11 ? ( 1750.00 * 1.10 ) * days  / 30 : 0.00;
		
		if ( months == Calendar.DECEMBER && days > 15 )
			decemberExtra = 0.00; 
		// 'December Extra...' have been already emitted. 

		assertEquals( decemberExtra + julyExtra + bonusExtra, settle.getTotalPayment(), DELTA);

	}

	@Test
	public void testSettleWithExtrasVII() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.DECEMBER;
						this.start = "01/07";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/01";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		
		Date contractStart = getFirstDayOfYear(getToday());
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				new HashMap<String, String>() {
				{
					put(TC2.getName(), "\"100\"");
					put(MONTH_DAYS.getName(), "30");
					put(QUOTE_GROUP.getName(), "\"01\"");
				}
				}, 
				new String[] { "( P_1 + P_2 ) * 0.10 ",
					"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
					"250.00 * DIAS_TRABAJADOS / DIAS_MES" 
				}, 
				new String[] {
					"BASE_CGC * 0.10", 
					"BASE_CGP * 0.05",
					"BASE_IRPF * 0.00/100" 
				}, 
				category);
		//@formatter:off		
		
		addSSRegimeStuff(aonContext);
		
		int year = get(getToday(), Calendar.YEAR);
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord julyExtra = getExtra(aonContext, agreement.getId(), "01/07");
		
		ISQLContractSalaryCalculatorContext extraCtx = getExtraSalaryCalculatorContext(connection, contract, julyExtra, year, getToday(), getToday());
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>( jooqSalaryBuilder ).calculate(extraCtx);
		jooqSalaryBuilder.execute();
		

		Date endDate = add(add(getFirstDayOfYear(getToday()), Calendar.MONTH, 8), Calendar.DATE, 14 ); // 15/09
		
		ISQLContractSalaryCalculatorContext settleCtx = 
				getSmartSQLContractSettleContext(connection, contractStart, endDate, contract);
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(settleCtx);
		
		for ( SalaryPayment p : settle.getSalaryPayments() ) 
			System.out.println(p.getDescription() + "= " + p.getAmount() );
		
		assertEquals( 1750.00 *1.10 / 6 * 2.5 , settle.getTotalPayment(), DELTA);
	}

	@Test
	public void testSettleWithExtrasVIII() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.MARCH;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "31/03";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/01";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		
		Date contractStart = getFirstDayOfYear(getToday());
		contractStart = add(contractStart, Calendar.MONTH,4);
		
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				new HashMap<String, String>() {
				{
					put(TC2.getName(), "\"100\"");
					put(MONTH_DAYS.getName(), "30");
					put(QUOTE_GROUP.getName(), "\"01\"");
				}
				}, 
				new String[] { "( P_1 + P_2 ) * 0.10 ",
					"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
					"250.00 * DIAS_TRABAJADOS / DIAS_MES" 
				}, 
				new String[] {
					"BASE_CGC * 0.10", 
					"BASE_CGP * 0.05",
					"BASE_IRPF * 0.00/100" 
				}, 
				category);
		//@formatter:off		
		
		addSSRegimeStuff(aonContext);
		
		int year = get(getToday(), Calendar.YEAR);
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord julyExtra = getExtra(aonContext, agreement.getId(), "01/07");
		
		Date extraDate = add(add(getFirstDayOfYear(getToday()), Calendar.MONTH, 5), Calendar.DATE, 14 ); // 15/06

		ISQLContractSalaryCalculatorContext extraCtx = getExtraSalaryCalculatorContext(connection, contract, julyExtra, year, extraDate, extraDate);
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>( jooqSalaryBuilder ).calculate(extraCtx);
		jooqSalaryBuilder.execute();
		

		Date endDate = add(add(getFirstDayOfYear(getToday()), Calendar.MONTH, 8), Calendar.DATE, 14 ); // 15/09
		
		ISQLContractSalaryCalculatorContext settleCtx = 
				getSmartSQLContractSettleContext(connection, contractStart, endDate, contract);
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(settleCtx);
		
		for ( SalaryPayment p : settle.getSalaryPayments() ) 
			System.out.println(p.getDescription() + "= " + p.getAmount() );
		
		assertEquals( 1750.00 *1.10 / 12 * 4.50 , settle.getTotalPayment(), DELTA);
	}

	@Test
	public void testSettleWithExtrasIX() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "(P_0 + P_1 + P_2)";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		
		Date contractStart = getFirstDayOfYear(getToday());
		contractStart = add(contractStart, Calendar.MONTH,7);
		
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				new HashMap<String, String>() {
				{
					put(TC2.getName(), "\"100\"");
					put(MONTH_DAYS.getName(), "30");
					put(QUOTE_GROUP.getName(), "\"01\"");
				}
				}, 
				new String[] { 
					"( P_1 + P_2 ) * 0.10 ",
					"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
					"250.00 * DIAS_TRABAJADOS / DIAS_MES" 
				}, 
				new String[] {
					"BASE_CGC * 0.10", 
					"BASE_CGP * 0.05",
					"BASE_IRPF * 0.00/100" 
				}, 
				category);
		//@formatter:off		
		
		addSSRegimeStuff(aonContext);
		
		

		Date endDate = add(add(getFirstDayOfYear(getToday()), Calendar.MONTH, 10), Calendar.DATE, 14 ); // 15/11
		
		ISQLContractSalaryCalculatorContext settleCtx = 
				getSmartSQLContractSettleContext(connection, contractStart, endDate, contract);
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(settleCtx);
		
		for ( SalaryPayment p : settle.getSalaryPayments() ) 
			System.out.println(p.getDescription() + "= " + p.getAmount() );
		
		assertEquals( 1750.00 *1.10 / 12 * 3.50 * 2 , settle.getTotalPayment(), DELTA);
	}

	@Test
	@Disabled("Uppps not yet :-(")
	public void testSettleWithExtrasX() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);


		
		Date contractStart =getFirstDayOfYear(getToday());
		Date contractEnd = getToday();
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				contractEnd,
				new HashMap<String, String>() {
					{
						put(TC2.getName(), "\"100\"");
						put(MONTH_DAYS.getName(), "30");
						put(QUOTE_GROUP.getName(), "\"01\"");
					}
				}, new String[] { "( P_1 + P_2 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" }, 
						new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * 0.00/100" }, 
				null);
		//@formatter:off
		PaymentConceptRecord pagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		
		addPayment(aonContext, contract, contractStart, contractEnd, pagaExtra,  "PAGA EXTRA", "P_0 + P_1 + P_2", "_P", "_P", PaymentType.CRA_0004,(byte)Month.JULY.getValue() );
		addPayment(aonContext, contract, contractStart, contractEnd, pagaExtra,  "PAGA EXTRA", "P_0 + P_1 + P_2", "_P", "_P", PaymentType.CRA_0004,(byte)Month.DECEMBER.getValue() );
//		addPayment(aonContext, contract, contractStart, contractEnd, pagaExtra,  "PAGA EXTRA", "P_0 + P_1 + P_2", "_P", "_P", PaymentType.CRA_0004,(byte)Month.MARCH.getValue() );
		
		JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder)
		.calculate(getContractSalaryCalculatorContext(connection, contractStart, getLastDayOfMonth(contractStart),getLastDayOfMonth(contractStart) , contract));
		jooqSalaryBuilder.execute();
		
//		 AON.getSalaries(aonContext, p->p.getContractProperty().eq(contract.getId())).findAny().ifPresent(s -> {
//			 System.out.println(s.getStartDate());
//			 s.getPayments().forEach( p -> System.out.println( "SALARY :" + p.getDescription() + " = " + p.getAmount() + "," + p.getQuote() ) );
//		 });
		
		
		addSSRegimeStuff(aonContext);

		
		ISQLContractSalaryCalculatorContext settleCtx = 
				getSmartSQLContractSettleContext(connection, contractStart, contract);
		
		settleCtx.getExpressionContext().eval("TRACE('SALARIO_DIA:%f\r\n', SALARIO_DIA)", contractStart, getToday())
		;
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder() {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				System.out.println( description + ":" + amount + "_" + startDate );
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
			}
		}).calculate(settleCtx);
		
		
		int months = get(getToday(), Calendar.MONTH );
		int days = Math.min(30, get(getToday(), Calendar.DAY_OF_MONTH ));
		double decemberExtra = ( 1750.00 * 1.10 ) * ((months * 30) + days ) / 360;
		int julyExtraMonths = (months >= 6 ? months -6 : months );
		// Settle at 01/07, so we have two extras for July .
		//if ( get(getToday(), Calendar.MONTH ) == 6 && get(getToday(), Calendar.DAY_OF_MONTH ) == 1)
		//julyExtraMonths += 6;

		double julyExtra = ( 1750.00 * 1.10 ) * ((julyExtraMonths  * 30) + days ) / 360 ;
		

		if ( months == Calendar.DECEMBER && days > 15 )
			decemberExtra = 0.00; 
		// 'December Extra...' have been already emitted. 

		assertEquals( decemberExtra + julyExtra, settle.getTotalPayment(), DELTA);

	}


	@Test
	@Disabled
	public void testSettleWithExtrasXI() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date contractStart = getFirstDayOfYear(getToday());
		
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				new HashMap<String, String>() {
				{
					put(TC2.getName(), "\"100\"");
					put(MONTH_DAYS.getName(), "30");
					put(QUOTE_GROUP.getName(), "\"01\"");
				}
				}, 
				new String[] { 
				}, 
				new String[] {
					"BASE_CGC * 0.10", 
					"BASE_CGP * 0.05",
					"BASE_IRPF * 0.00/100" 
				}, 
				null);
		//@formatter:off		
		
		addSSRegimeStuff(aonContext);

		PaymentConceptRecord pagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord salarioBase = addConcept(aonContext, "SALARIO_BASE", PaymentType.CRA_0001);
		
		addPayment(aonContext, contract, salarioBase, "950.00 * DIAS_TRABAJADOS / DIAS_MES");
		addPayment(aonContext, contract, contractStart, null, pagaExtra, "PAGA EXTRAORDINARIA", "SALARIO_BASE", "_P", "_P/12", PaymentType.CRA_0004, (byte)Month.JUNE.ordinal());
		addPayment(aonContext, contract, contractStart, null, pagaExtra, "PAGA EXTRAORDINARIA", "SALARIO_BASE", "_P", "_P/12", PaymentType.CRA_0004, (byte)Month.DECEMBER.ordinal());
		
		for ( int i = 0 ; i <= 2 ; i++ ) {
			Date startDate = add(contractStart, Calendar.MONTH, i);
			Date endDate = getLastDayOfMonth(startDate);
			SalaryBuilder salaryBuilder = new SalaryBuilder();
			new SmartContractSalaryCalculator<Salary>(salaryBuilder)
			.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate , endDate , contract));
			Salary salary = salaryBuilder.getSalary();
			org.junit.assertEquals(950.00/6.00, salary.getExtraPayProration(), DELTA);
		}
		
		;
		
		Date endDate = getLastDayOfMonth((add(contractStart, Calendar.MONTH, 2))); // 31/03
		
		ISQLContractSalaryCalculatorContext settleCtx = 
				getSmartSQLContractSettleContext(connection, contractStart, endDate, contract);
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(settleCtx);
		
		for ( SalaryPayment p : settle.getSalaryPayments() ) 
			System.out.println(p.getDescription() + "= " + p.getAmount() );
		
		assertEquals( 950.00/6.00 * 3, settle.getTotalPayment(), DELTA);
	}

	@Test
	public void testSettleWithExtrasXII() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date contractStart = getFirstDayOfYear(getToday());
		
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				new HashMap<String, String>() {
				{
					put(TC2.getName(), "\"100\"");
					put(MONTH_DAYS.getName(), "30");
					put(QUOTE_GROUP.getName(), "\"01\"");
				}
				}, 
				new String[] { 
				}, 
				new String[] {
					"BASE_CGC * 0.10", 
					"BASE_CGP * 0.05",
					"BASE_IRPF * 0.00/100" 
				}, 
				null);
		//@formatter:off		
		
		addSSRegimeStuff(aonContext);

		PaymentConceptRecord pagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord salarioBase = addConcept(aonContext, "SALARIO_BASE", PaymentType.CRA_0001);
		
		addPayment(aonContext, contract, salarioBase, "950.00 * DIAS_TRABAJADOS / DIAS_MES");
		addPayment(aonContext, contract, contractStart, null, pagaExtra, "PAGA EXTRAORDINARIA", "SALARIO_BASE", "_P", "_P/12", PaymentType.CRA_0004, (byte)Month.JUNE.ordinal());
		addPayment(aonContext, contract, contractStart, null, pagaExtra, "PAGA EXTRAORDINARIA", "SALARIO_BASE", "_P", "_P/12", PaymentType.CRA_0004, (byte)Month.DECEMBER.ordinal());
		
		for ( int i = 0 ; i <= 2 ; i++ ) {
			Date startDate = add(contractStart, Calendar.MONTH, i);
			Date endDate = getLastDayOfMonth(startDate);
			JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder)
			.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate , endDate , contract));
			jooqSalaryBuilder.execute();
		}
		
		AON.getSalaries(aonContext, p-> p.getContractProperty().eq(contract.getId()))
		.forEach( salary -> {
			org.junit.assertEquals(950.00/6.00, salary.getExtraProrationBase(), DELTA);
		});
		;
		
		Date endDate = getLastDayOfMonth((add(contractStart, Calendar.MONTH, 2))); // 31/03
		
		ISQLContractSalaryCalculatorContext settleCtx = 
				getSmartSQLContractSettleContext(connection, contractStart, endDate, contract);
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(settleCtx);
		
		for ( SalaryPayment p : settle.getSalaryPayments() ) 
			System.out.println(p.getDescription() + "= " + p.getAmount() );
		
		assertEquals( 950.00/6.00 * 3, settle.getTotalPayment(), 0.005);
	}

	@Test
	public void testSettleWithExtrasXIII() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date contractStart = getFirstDayOfYear(getToday());
		
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				new HashMap<String, String>() {
				{
					put(TC2.getName(), "\"100\"");
					put(MONTH_DAYS.getName(), "30");
					put(QUOTE_GROUP.getName(), "\"01\"");
				}
				}, 
				new String[] { 
				}, 
				new String[] {
					"BASE_CGC * 0.10", 
					"BASE_CGP * 0.05",
					"BASE_IRPF * 0.00/100" 
				}, 
				null);
		//@formatter:off		
		
		addSSRegimeStuff(aonContext);

		PaymentConceptRecord pagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord salarioBase = addConcept(aonContext, "SALARIO_BASE", PaymentType.CRA_0001);
		
		addPayment(aonContext, contract, salarioBase, "950.00 * DIAS_TRABAJADOS / DIAS_MES");
		addPayment(aonContext, contract, contractStart, null, pagaExtra, "PAGA EXTRAORDINARIA", "SALARIO_BASE", "_P", "_P/12", PaymentType.CRA_0004, (byte)Month.JUNE.ordinal());
		addPayment(aonContext, contract, contractStart, null, pagaExtra, "PAGA EXTRAORDINARIA", "SALARIO_BASE", "_P", "_P/12", PaymentType.CRA_0004, (byte)Month.DECEMBER.ordinal());
		addPayment(aonContext, contract, contractStart, null, pagaExtra, "PAGA EXTRAORDINARIA", "SALARIO_BASE", "_P", "_P/12", PaymentType.CRA_0004, (byte)Month.MARCH.ordinal());
		
		for ( int i = 0 ; i <= 2 ; i++ ) {
			Date startDate = add(contractStart, Calendar.MONTH, i);
			Date endDate = getLastDayOfMonth(startDate);
			JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder)
			.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate , endDate , contract));
			jooqSalaryBuilder.execute();
		}
		
		AON.getSalaries(aonContext, p-> p.getContractProperty().eq(contract.getId()))
		.forEach( salary -> {
			org.junit.assertEquals(950.00/12.00 *3, salary.getExtraProrationBase(), DELTA);
		});
		;
		
		Date endDate = getLastDayOfMonth((add(contractStart, Calendar.MONTH, 2))); // 31/03
		
		ISQLContractSalaryCalculatorContext settleCtx = 
				getSmartSQLContractSettleContext(connection, contractStart, endDate, contract);
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(settleCtx);
		
		for ( SalaryPayment p : settle.getSalaryPayments() ) 
			System.out.println(p.getDescription() + "= " + p.getAmount() );
		
		assertEquals( 950.00/12.00 * 6, settle.getTotalPayment(), 0.005);
	}

	@Test
	public void testSettleWithExtrasXIV() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date contractStart = getFirstDayOfYear(getToday());
		
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				new HashMap<String, String>() {
				{
					put(TC2.getName(), "\"100\"");
					put(MONTH_DAYS.getName(), "30");
					put(QUOTE_GROUP.getName(), "\"01\"");
				}
				}, 
				new String[] { 
				}, 
				new String[] {
					"BASE_CGC * 0.10", 
					"BASE_CGP * 0.05",
					"BASE_IRPF * 0.00/100" 
				}, 
				null);
		//@formatter:off		
		
		addSSRegimeStuff(aonContext);

		PaymentConceptRecord pagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord salarioBase = addConcept(aonContext, "SALARIO_BASE", PaymentType.CRA_0001);
		
		addPayment(aonContext, contract, salarioBase, "950.00 * DIAS_TRABAJADOS / DIAS_MES");
		addPayment(aonContext, contract, contractStart, null, pagaExtra, "PAGA EXTRAORDINARIA", "SALARIO_BASE", "_P", "_P/12", PaymentType.CRA_0004, (byte)Month.JUNE.ordinal());
		addPayment(aonContext, contract, contractStart, null, pagaExtra, "PAGA EXTRAORDINARIA", "SALARIO_BASE", "_P", "_P/12", PaymentType.CRA_0004, (byte)Month.DECEMBER.ordinal());
		addPayment(aonContext, contract, contractStart, null, pagaExtra, "PAGA EXTRAORDINARIA", "SALARIO_BASE", "_P", "_P/12", PaymentType.CRA_0004, (byte)Month.FEBRUARY.ordinal());
		
		for ( int i = 0 ; i <= 2 ; i++ ) {
			Date startDate = add(contractStart, Calendar.MONTH, i);
			Date endDate = getLastDayOfMonth(startDate);
			JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder)
			.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate , endDate , contract));
			jooqSalaryBuilder.execute();
		}
		
		AON.getSalaries(aonContext, p-> p.getContractProperty().eq(contract.getId()))
		.forEach( salary -> {
			org.junit.assertEquals(950.00/12.00 *3, salary.getExtraProrationBase(), DELTA);
		});
		;
		
		Date endDate = getLastDayOfMonth((add(contractStart, Calendar.MONTH, 2))); // 31/03
		
		ISQLContractSalaryCalculatorContext settleCtx = 
				getSmartSQLContractSettleContext(connection, contractStart, endDate, contract);
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(settleCtx);
		
		for ( SalaryPayment p : settle.getSalaryPayments() ) 
			System.out.println(p.getDescription() + "= " + p.getAmount() );
		
		// Three for June, another three for December and only one for February.
		assertEquals( 950.00/12.00 * 7, settle.getTotalPayment(), 0.005);
	}

	@Test
	@Disabled("Fails day 29")
	public void testSettleWithExtrasXV() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date contractStart = getFirstDayOfYear(getToday());
		
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				new HashMap<String, String>() {
				{
					put(TC2.getName(), "\"100\"");
					put(MONTH_DAYS.getName(), "30");
					put(QUOTE_GROUP.getName(), "\"01\"");
				}
				}, 
				new String[] { 
				}, 
				new String[] {
					"BASE_CGC * 0.10", 
					"BASE_CGP * 0.05",
					"BASE_IRPF * 0.00/100" 
				}, 
				null);
		//@formatter:off		
		
		addSSRegimeStuff(aonContext);

		PaymentConceptRecord pagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord salarioBase = addConcept(aonContext, "SALARIO_BASE", PaymentType.CRA_0001);
		
		addPayment(aonContext, contract, salarioBase, "950.00 * DIAS_TRABAJADOS / DIAS_MES");
		addPayment(aonContext, contract, contractStart, null, pagaExtra, "PAGA EXTRAORDINARIA JUNIO", "SALARIO_BASE", "_P", "_P/12", PaymentType.CRA_0004, (byte)Month.JUNE.ordinal());
		addPayment(aonContext, contract, contractStart, null, pagaExtra, "PAGA EXTRAORDINARIA DICIEMBRE", "SALARIO_BASE", "_P", "_P/12", PaymentType.CRA_0004, (byte)Month.DECEMBER.ordinal());
		addPayment(aonContext, contract, contractStart, null, pagaExtra, "PAGA EXTRAORDINARIA FEBRERO", "SALARIO_BASE", "_P", "_P/12", PaymentType.CRA_0004, (byte)Month.FEBRUARY.ordinal());
		
		Date endDate = getLastDayOfMonth((add(contractStart, Calendar.MONTH, 2))); // 31/03
		
		ISQLContractSalaryCalculatorContext settleCtx = 
				getSmartSQLContractSettleContext(connection, contractStart, endDate, contract);
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder() {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				System.out.println(description + "= " + amount + ", " + startDate + "..." + endDate );
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
			}
		}).calculate(settleCtx);
		
		for ( SalaryPayment p : settle.getSalaryPayments() ) 
			System.out.println(p.getDescription() + "= " + p.getAmount() );
		
		assertEquals( 950.00/12.00 * 7, settle.getTotalPayment(), 0.005);
	}

	@Test
	public void testSettleWithExtrasXX() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "(P_0) + (P_1) + (P_2)";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "31/12";
					}
				}, new Extra() {
					{
						this.expression = "(P_0 + P_1 + P_2)";
						this.month = Month.JUNE;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "30/06";
					}
				}, });
		
		Date contractStart = getFirstDayOfYear(getToday());
		//contractStart = add(contractStart, Calendar.MONTH,7);
		Date contractEnd = add(add(contractStart, Calendar.MONTH, 7), Calendar.DATE, 14 ); 
		
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				contractEnd,
				new HashMap<String, String>() {
				{
					put(TC2.getName(), "\"100\"");
					put(MONTH_DAYS.getName(), "30");
					put(QUOTE_GROUP.getName(), "\"01\"");
				}
				}, 
				new String[] { 
					"( P_1 + P_2 ) * 0.10 ",
					"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
					"250.00 * DIAS_TRABAJADOS / DIAS_MES" 
				}, 
				new String[] {
					"BASE_CGC * 0.10", 
					"BASE_CGP * 0.05",
					"BASE_IRPF * 0.00/100" 
				}, 
				category);
		//@formatter:off		
		
		addSSRegimeStuff(aonContext);
		
		
		for ( int i = 0 ; i < 8 ; i++ ) {
			Date startDate = add(contractStart, Calendar.MONTH, i);
			Date endDate = getLastDayOfMonth(startDate);
			JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder)
			.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate , endDate , contract));
			jooqSalaryBuilder.execute();
			System.out.println(startDate +".." + endDate);
		}

		
		System.out.println("*" +contractStart +".." + contractEnd);
		
		ISQLContractSalaryCalculatorContext settleCtx = 
				getSmartSQLContractSettleContext(connection, contractStart, contractEnd, contract);
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(settleCtx);
		
		for ( SalaryPayment p : settle.getSalaryPayments() ) 
			System.out.println(p.getDescription() + "= " + p.getAmount() );
		
		assertEquals( 
				Math.round(1750.00 * 1.10 / 12 * 1000.00 ) / 1000.00 * 7.5 
				+Math.round(1750.00 * 1.10 / 12 * 1000.00 ) / 1000.00 * 1.5  , 
				settle.getTotalPayment(), 0.001);
	}

	@Test
	public void testSettleWithExtrasOverride() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		PaymentConceptRecord extraConcept = addConcept(aonContext, "PAGA_EXTRA");
		

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.concept = extraConcept.getId();
						this.expression = "/* PAGA NAVIDAD */(P_0) + (P_1) + (P_2)";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "31/12";
					}
				}, new Extra() {
					{
						this.concept = extraConcept.getId();
						this.expression = "/* PAGA VERANO */(P_0 + P_1 + P_2)";
						this.month = Month.JUNE;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "30/06";
					}
				}, });
		
		Date contractStart = getFirstDayOfYear(getToday());
		//contractStart = add(contractStart, Calendar.MONTH,7);
		Date contractEnd = add(add(contractStart, Calendar.MONTH, 7), Calendar.DATE, 14 ); 
		
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				contractEnd,
				new HashMap<String, String>() {
				{
					put(TC2.getName(), "\"100\"");
					put(MONTH_DAYS.getName(), "30");
					put(QUOTE_GROUP.getName(), "\"01\"");
				}
				}, 
				new String[] { 
					"( P_1 + P_2 ) * 0.10 ",
					"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
					"250.00 * DIAS_TRABAJADOS / DIAS_MES" 
				}, 
				new String[] {
					"BASE_CGC * 0.10", 
					"BASE_CGP * 0.05",
					"BASE_IRPF * 0.00/100" 
				}, 
				category);
		//@formatter:off		
		
		addPayment(aonContext, contract, extraConcept, "/* PAGA VERANO */REMOVE()", "PRORRATEAR()", (byte)Month.JUNE.ordinal());
		addPayment(aonContext, contract, extraConcept, "/* PAGA NAVIDAD */REMOVE()", "PRORRATEAR()", (byte)Month.DECEMBER.ordinal());

		addPayment(aonContext, contract, extraConcept, "/* PAGA VERANO */(P_0 + P_1 + P_2)", "PRORRATEAR()", (byte)Month.JUNE.ordinal());
		addPayment(aonContext, contract, extraConcept, "/* PAGA NAVIDAD */(P_0 + P_1 + P_2)", "PRORRATEAR()", (byte)Month.DECEMBER.ordinal());
		
		addSSRegimeStuff(aonContext);
		
		
		for ( int i = 0 ; i < 8 ; i++ ) {
			Date startDate = add(contractStart, Calendar.MONTH, i);
			Date endDate = getLastDayOfMonth(startDate);
			JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder)
			.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate , endDate , contract));
			jooqSalaryBuilder.execute();
			System.out.println(startDate +".." + endDate);
		}

		
		System.out.println("*" +contractStart +".." + contractEnd);
		
		ISQLContractSalaryCalculatorContext settleCtx = 
				getSmartSQLContractSettleContext(connection, contractStart, contractEnd, contract);
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(settleCtx);
		
		for ( SalaryPayment p : settle.getSalaryPayments() ) 
			System.out.println(p.getDescription() + "= " + p.getAmount() );
		
		assertEquals( 
				Math.round(1750.00 * 1.10 / 12 * 1000.00 ) / 1000.00 * 7.5 
				+Math.round(1750.00 * 1.10 / 12 * 1000.00 ) / 1000.00 * 1.5  , 
				settle.getTotalPayment(), 0.005);
	}

	@Test
	public void testSettleWithExtrasWhitoutConceptXXI() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date contractStart = getFirstDayOfYear(getToday());
		
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				new HashMap<String, String>() {
				{
					put(TC2.getName(), "\"100\"");
					put(MONTH_DAYS.getName(), "30");
					put(QUOTE_GROUP.getName(), "\"01\"");
				}
				}, 
				new String[] { 
				}, 
				new String[] {
					"BASE_CGC * 0.10", 
					"BASE_CGP * 0.05",
					"BASE_IRPF * 0.00/100" 
				}, 
				null);
		//@formatter:off		
		
		addSSRegimeStuff(aonContext);

		//PaymentConceptRecord pagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord salarioBase = addConcept(aonContext, "SALARIO_BASE", PaymentType.CRA_0001);
		
		addPayment(aonContext, contract, salarioBase, "950.00 * DIAS_TRABAJADOS / DIAS_MES");
		addPayment(aonContext, contract, contractStart, null, "PAGA EXTRAORDINARIA", "SALARIO_BASE", "_P", "_P/12", PaymentType.CRA_0004, Month.JUNE);
		addPayment(aonContext, contract, contractStart, null, "PAGA EXTRAORDINARIA", "SALARIO_BASE", "_P", "_P/12", PaymentType.CRA_0004, Month.DECEMBER);
		
		for ( int i = 0 ; i <= 2 ; i++ ) {
			Date startDate = add(contractStart, Calendar.MONTH, i);
			Date endDate = getLastDayOfMonth(startDate);
			JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder)
			.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate , endDate , contract));
			jooqSalaryBuilder.execute();
		}
		
		AON.getSalaries(aonContext, p-> p.getContractProperty().eq(contract.getId()))
		.forEach( salary -> {
			org.junit.assertEquals(950.00/6.00, salary.getExtraProrationBase(), DELTA);
		});
		;
		
		Date endDate = getLastDayOfMonth((add(contractStart, Calendar.MONTH, 2))); // 31/03
		
		ISQLContractSalaryCalculatorContext settleCtx = 
				getSmartSQLContractSettleContext(connection, contractStart, endDate, contract);
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(settleCtx);
		
		for ( SalaryPayment p : settle.getSalaryPayments() ) 
			System.out.println(p.getDescription() + "= " + p.getAmount() );
		
		assertEquals( 950.00/6.00 * 3, settle.getTotalPayment(), 0.005);
	}

	@Test
	public void testSettleWithExtrasMismatchOverriden() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		PaymentConceptRecord pagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord pagaMarzo = addConcept(aonContext, "PAGA_MARZO", PaymentType.CRA_0004);
		PaymentConceptRecord pagaSeptiembre = addConcept(aonContext, "PAGA_SEPTIEMBRE", PaymentType.CRA_0004);
		
		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.concept = pagaExtra.getId();
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "31/12";
						this.month = Month.DECEMBER;
						this.expression = "/*DICIEMBRE*/(P_0) + (P_1) + (P_2)";
					}
				}, new Extra() {
					{
						this.concept = pagaExtra.getId();
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "30/06";
						this.month = Month.JUNE;
						this.expression = "/*JUNIO*/(P_0 + P_1 + P_2)";
					}
				}, new Extra() {
					{
						this.concept = pagaMarzo.getId();
						this.start = "01/03 -1";
						this.end = "28/02";
						this.issue = "15/03";
						this.month = Month.MARCH;
						this.expression = "/*MARZO*/(P_0 + P_1 + P_2)";
					}
				}, new Extra() {
					{
						this.concept = pagaSeptiembre.getId();
						this.start = "01/09 -1";
						this.end = "31/08";
						this.issue = "15/09";
						this.month = Month.SEPTEMBER;
						this.expression = "/*SEPTIEMBRE*/(P_0 + P_1 + P_2)";
					}
				}, });
		
		Date firstDayOfYear = getFirstDayOfYear(getToday());
		Date contractStart = add(firstDayOfYear, Calendar.YEAR, -1);
		Date contractEnd = add(add(firstDayOfYear, Calendar.MONTH, 3), Calendar.DATE, 5 ); 
		
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				contractEnd,
				new HashMap<String, String>() {
				{
					put(TC2.getName(), "\"100\"");
					put(MONTH_DAYS.getName(), "30");
					put(QUOTE_GROUP.getName(), "\"01\"");
				}
				}, 
				new String[] { 
					"( P_1 + P_2 ) * 0.10 ",
					"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
					"250.00 * DIAS_TRABAJADOS / DIAS_MES" 
				}, 
				new String[] {
//					"BASE_CGC * 0.10", 
//					"BASE_CGP * 0.05",
//					"BASE_IRPF * 0.00/100" 
				}, 
				category);
		//@formatter:off	
		
		
		addPayment(aonContext, contract, firstDayOfYear, null, pagaSeptiembre, "PAGA SEPTIEMBRE", "REMOVE()", null, null, PaymentType.CRA_0004);
		addPayment(aonContext, contract, firstDayOfYear, null, pagaMarzo, "PAGA MARZO", "/*MARZO*/(P_0 + P_1 + P_2)", null, null, PaymentType.CRA_0004);
		
		addSSRegimeStuff(aonContext);
		
		
		for ( int i = 0 ; i < 16 ; i++ ) {
			Date startDate = add(contractStart, Calendar.MONTH, i);
			Date endDate = getLastDayOfMonth(startDate);
			JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder)
			.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate , endDate , contract));
			jooqSalaryBuilder.execute();
			System.out.println(startDate +".." + endDate);
		}

		
		System.out.println("*" +contractStart +".." + contractEnd);
		
		ISQLContractSalaryCalculatorContext settleCtx = 
				getSmartSQLContractSettleContext(connection, contractStart, contractEnd, contract);
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(settleCtx);
		
		for ( SalaryPayment p : settle.getSalaryPayments() ) 
			System.out.println(p.getDescription() + "= " + p.getAmount() );
		
		assertEquals( 
				1750.00 * 1.10 / 12.00 * 9.00 			/*JULY..MARCH JUNIO*/
				+ 1750.00 * 1.10 / 12.00 * 3.00 		/*JANUARY..MARCH DICIEMBRE */
				+ 1750.00 * 1.10 / 12.00 / 30.00 * 6 	/*APRIL JUNIO */ 
				+ 1750.00 * 1.10 / 12.00 / 30.00 * 6 	/*APRIL DICIEMBRE*/
				
				+ 1750.00 * 1.10 / 12.00 * 4 			/*SEPTEMBER..DECEMBER SEPTIEMBRE*/
				,
				settle.getTotalPayment(), 0.05);
	}


	// ------------------------------------------------------------------------

	@Test
	public void testSettleWithExtrasAndManualPayments() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		
		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		
		Date contractStart = getFirstDayOfYear(getToday());
		Date contractEnd = getToday();
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				contractEnd,
				new HashMap<String, String>() {
					{
						put(TC2.getName(), "\"100\"");
						put(MONTH_DAYS.getName(), "30");
						put(QUOTE_GROUP.getName(), "\"01\"");
					}
				}, new String[] { "( P_1 + P_2 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" }, 
						new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * 0.00/100" }, 
						category);
		//@formatter:off
		
		
		addSSRegimeStuff(aonContext);
		
		addSettlePayment(aonContext, contract, getFirstDayOfMonth(getToday()) , null, "MANUAL PAYMENT", "-100.00", "_P", "0.00", PaymentType.CRA_0000);

		
		ISQLContractSalaryCalculatorContext settleCtx = 
				getSmartSQLContractSettleContext(connection, contractStart, contract);
		
		//settleCtx.getExpressionContext().eval("TRACE('SALARIO_DIA:%f\r\n', SALARIO_DIA)", contractStart, getToday())
		;
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder() {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				//System.out.println( description + ":" + amount);
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
			}
		}).calculate(settleCtx);
		
		for ( SalaryPayment p : settle.getSalaryPayments() ) 
			System.out.println(p.getDescription() + "= " + p.getAmount() );
		
		double manualPayment = -100.00;
		int months = get(getToday(), Calendar.MONTH );
		//int days = Math.min(30,get(getToday(), Calendar.DAY_OF_MONTH ));
		int todayOfMonth = get(getToday(), Calendar.DAY_OF_MONTH );
		int lastdayOfMonth = AonDateUtils.getMax(getToday(), Calendar.DAY_OF_MONTH);
		int days = todayOfMonth == lastdayOfMonth ? 30 : Math.min(30, todayOfMonth );
		double decemberExtra = ( 1750.00 * 1.10 ) * ((months * 30) + days ) / 360;
		int julyExtraMonths = (months >= 6 ? months -6 : months );
		// Settle at 01/07, so we have two extras for July .
		//if ( get(getToday(), Calendar.MONTH ) == 6 && get(getToday(), Calendar.DAY_OF_MONTH ) == 1)
		//	julyExtraMonths += 6;
		double julyExtra = ( 1750.00 * 1.10 ) * ((julyExtraMonths  * 30) + days ) / 360 ;
		
		if ( months == Calendar.DECEMBER && days > 15 )
			decemberExtra = 0.00; 
		// 'December Extra...' have been already emitted. 

		assertEquals( decemberExtra + julyExtra + manualPayment, settle.getTotalPayment(), DELTA);

	}

	@Test
	public void testSettleWithExtrasAtSalaryI() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		PaymentConceptRecord pagaExtraConcept = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.concept = pagaExtraConcept.getId();
						this.expression = "P_0 + P_1 + P_2 /*DICIEMBRE*/";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "31/12";
					}
				}, new Extra() {
					{
						this.concept = pagaExtraConcept.getId();
						this.expression = "P_0 + P_1 + P_2 /*JUNIO*/";
						this.month = Month.JUNE;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "30/06";
					}
				}, });
		
		Date contractStart = getFirstDayOfYear(getToday());
		Date contractEnd = add(add(contractStart,MONTH, 5), DAY_OF_MONTH, 14); // 15/06 
		
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				contractEnd,
				new HashMap<String, String>() {
					{
						put(TC2.getName(), "\"100\"");
						put(MONTH_DAYS.getName(), "30");
						put(QUOTE_GROUP.getName(), "\"01\"");
					}
				}, new String[] { "( P_1 + P_2 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" }, 
						new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * 0.00/100" }, 
						category);
		//@formatter:off
		
		
		addSSRegimeStuff(aonContext);

		
		Date juneStartDate = add(contractStart, MONTH, 5);
		Date juneEndDate = getLastDayOfMonth(juneStartDate);
		
		JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder)
		.calculate(getContractSalaryCalculatorContext(connection, juneStartDate, juneEndDate , juneEndDate, contract));
		jooqSalaryBuilder.execute();
		
		AON.getSalaries(aonContext, props -> props.getContractProperty().eq(contract.getId()) )
		.forEach(s -> assertEquals(1750*1.1*15/30  + 1750*1.1/12*5.5, s.getTotalPayment(), DELTA))
		;
		

		ISQLContractSalaryCalculatorContext settleCtx = 
				getSmartSQLContractSettleContext(connection, contractStart, contractEnd, contract);
		;
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(settleCtx);
		
		for ( SalaryPayment p : settle.getSalaryPayments() ) {
			System.out.println(p.getDescription() + ": " + p.getAmount() );
		}
		
		
		int months = get(getToday(), Calendar.MONTH );
		int days = Math.min(30,get(getToday(), Calendar.DAY_OF_MONTH ));
				
		
		
		double decemberExtra = ( 1750.00 * 1.10 ) / 12  * 5.5 ;

		assertEquals( decemberExtra , settle.getTotalPayment(), DELTA);

	}

	@Test
	public void testSettleWithExtrasAtSalaryII() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		PaymentConceptRecord pagaExtraConcept = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.concept = pagaExtraConcept.getId();
						this.expression = "P_0 + P_1 + P_2 /*DICIEMBRE*/";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "31/12";
					}
				}, new Extra() {
					{
						this.concept = pagaExtraConcept.getId();
						this.expression = "P_0 + P_1 + P_2 /*JUNIO*/";
						this.month = Month.JUNE;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "30/06";
					}
				}, });
		
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		for ( AgreementPaymentRecord p : getAgreementPayments(aonContext, agreement.getId()) ) {
			p.setSalaryType((byte)SalaryType.SALARY.ordinal());
			p.update();
		}
		
		Date contractStart = getFirstDayOfYear(getToday());
		Date contractEnd = add(add(contractStart,MONTH, 5), DAY_OF_MONTH, 14); // 15/06 
		
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				contractEnd,
				new HashMap<String, String>() {
					{
						put(TC2.getName(), "\"100\"");
						put(MONTH_DAYS.getName(), "30");
						put(QUOTE_GROUP.getName(), "\"01\"");
					}
				}, new String[] { "( P_1 + P_2 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" }, 
						new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * 0.00/100" }, 
						category);
		//@formatter:off
		
		
		addSSRegimeStuff(aonContext);

		
		Date juneStartDate = add(contractStart, MONTH, 5);
		Date juneEndDate = getLastDayOfMonth(juneStartDate);
		
		JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder)
		.calculate(getContractSalaryCalculatorContext(connection, juneStartDate, juneEndDate , juneEndDate, contract));
		jooqSalaryBuilder.execute();
		
		AON.getSalaries(aonContext, props -> props.getContractProperty().eq(contract.getId()) )
		.forEach(s -> assertEquals(1750*1.1*15/30  + 1750*1.1/12*5.5, s.getTotalPayment(), DELTA))
		;
		

		ISQLContractSalaryCalculatorContext settleCtx = 
				getSmartSQLContractSettleContext(connection, contractStart, contractEnd, contract);
		;
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(settleCtx);
		
		for ( SalaryPayment p : settle.getSalaryPayments() ) {
			System.out.println(p.getDescription() + ": " + p.getAmount() );
		}
		
		
		int months = get(getToday(), Calendar.MONTH );
		int days = Math.min(30,get(getToday(), Calendar.DAY_OF_MONTH ));
				
		
		
		double decemberExtra = ( 1750.00 * 1.10 ) / 12  * 5.5 ;

		assertEquals( decemberExtra , settle.getTotalPayment(), DELTA);

	}
	
	@Test
	public void testSettleWithContractExtrasI() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on

		PaymentConceptRecord pagaExtraConcept = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		
		Date contractStart = getFirstDayOfYear(getToday());
		Date contractEnd = getToday();
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				contractEnd,
				new HashMap<String, String>() {
					{
						put(TC2.getName(), "\"100\"");
						put(MONTH_DAYS.getName(), "30");
						put(QUOTE_GROUP.getName(), "\"01\"");
					}
				}, new String[] { "( P_1 + P_2 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" }, 
						new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * 0.00/100" }, 
						null);
		
		addPayment(aonContext, contract, contract.getStartDate(), contract.getEndDate(), pagaExtraConcept, "PAGA EXTRAORDINARIA JULIO", "P_0 + P_1 + P_2", "_P", "_P", PaymentType.CRA_0004, (byte) Month.JULY.ordinal());
		addPayment(aonContext, contract, contract.getStartDate(), contract.getEndDate(), pagaExtraConcept, "PAGA EXTRAORDINARIA DICIEMBRE", "P_0 + P_1 + P_2", "_P", "_P", PaymentType.CRA_0004, (byte) Month.DECEMBER.ordinal());
		//@formatter:off
		
		
		addSSRegimeStuff(aonContext);

		
		ISQLContractSalaryCalculatorContext settleCtx = 
				getSmartSQLContractSettleContext(connection, contractStart, contract);
				
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder() {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				System.out.println( description + ":" + amount + "," + startDate + "..." + endDate  );
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
			}
		}).calculate(settleCtx);
		
		
		int months = get(getToday(), Calendar.MONTH );
		int todayOfMonth = get(getToday(), Calendar.DAY_OF_MONTH );
		int lastdayOfMonth = AonDateUtils.getMax(getToday(), Calendar.DAY_OF_MONTH);
		int days = todayOfMonth == lastdayOfMonth ? 30 : Math.min(30, todayOfMonth );
		
//		double extra = ( 1750.00 * 1.10 ) * ((months * 30) + days ) / 360.00;
		
		// December 
		double extra = ( 1750.00 * 1.10 / 12 ) * ( months + days / 30.00);
		
		// July
		months = months < 7 ? months : months -7 ;
		extra +=  1750.00 * 1.10 / 12  * ( months + days / 30.00);
		

		assertEquals( extra, settle.getTotalPayment(), DELTA);

	}
	
	@Test
	public void testSettleWithContractExtrasII() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on

		PaymentConceptRecord pagaExtraConcept = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		
		Date contractStart = add(getFirstDayOfYear(getToday()), Calendar.YEAR, -2);
		Date contractEnd = getToday();
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				contractEnd,
				new HashMap<String, String>() {
					{
						put(TC2.getName(), "\"100\"");
						put(MONTH_DAYS.getName(), "30");
						put(QUOTE_GROUP.getName(), "\"01\"");
					}
				}, new String[] { "( P_1 + P_2 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" }, 
						new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * 0.00/100" }, 
						null);
		
		addPayment(aonContext, contract, contract.getStartDate(), contract.getEndDate(), pagaExtraConcept, "PAGA EXTRAORDINARIA JULIO", "P_0 + P_1 + P_2", "_P", "_P", PaymentType.CRA_0004, (byte) Month.JULY.ordinal());
		addPayment(aonContext, contract, contract.getStartDate(), contract.getEndDate(), pagaExtraConcept, "PAGA EXTRAORDINARIA DICIEMBRE", "P_0 + P_1 + P_2", "_P", "_P", PaymentType.CRA_0004, (byte) Month.DECEMBER.ordinal());
		//@formatter:off
		
		
		addSSRegimeStuff(aonContext);

		
		ISQLContractSalaryCalculatorContext settleCtx = 
				getSmartSQLContractSettleContext(connection, contractStart, contract);
				
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder() {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				System.out.println( description + ":" + amount + "," + startDate);
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
			}
		}).calculate(settleCtx);
		
		
		int decemberExtramonths = get(getToday(), Calendar.MONTH );
		int todayOfMonth = get(getToday(), Calendar.DAY_OF_MONTH );
		int lastdayOfMonth = AonDateUtils.getMax(getToday(), Calendar.DAY_OF_MONTH);
		int days = todayOfMonth == lastdayOfMonth ? 30 : Math.min(30, todayOfMonth );
		int julyExtraMonths = (decemberExtramonths < 7 ? decemberExtramonths + 5 : decemberExtramonths -7);
		
		double decemberExtra = ( 1750.00 * 1.10 ) * ((decemberExtramonths * 30) + days ) / 360;
		double julyExtra = ( 1750.00 * 1.10 ) * ((julyExtraMonths * 30) + days ) / 360;
		

		assertEquals( decemberExtra + julyExtra, settle.getTotalPayment(), DELTA);

	}

	@Test
	public void testSettleWithContractExtrasIII() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on

		PaymentConceptRecord pagaExtraConcept = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		
		Date contractStart = add(getFirstDayOfYear(getToday()), Calendar.YEAR, -2);
		Date contractEnd = getToday();
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				contractEnd,
				new HashMap<String, String>() {
					{
						put(TC2.getName(), "\"100\"");
						put(MONTH_DAYS.getName(), "30");
						put(QUOTE_GROUP.getName(), "\"01\"");
					}
				}, new String[] { "( P_1 + P_2 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" }, 
						new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * 0.00/100" }, 
						null);
		
		for ( Date date = contract.getStartDate() ; date.compareTo(contract.getEndDate()) <= 0; date = add(date, MONTH, 1) ) {
			addPayment(aonContext, contract, date, getLastDayOfMonth(date), pagaExtraConcept, "PAGA EXTRAORDINARIA JULIO", "P_0 + P_1 + P_2", "_P", "_P", PaymentType.CRA_0004, (byte) Month.JULY.ordinal());
			addPayment(aonContext, contract, date, getLastDayOfMonth(date), pagaExtraConcept, "PAGA EXTRAORDINARIA DICIEMBRE", "P_0 + P_1 + P_2", "_P", "_P", PaymentType.CRA_0004, (byte) Month.DECEMBER.ordinal());
		}
		//@formatter:off
		
		
		addSSRegimeStuff(aonContext);

		
		ISQLContractSalaryCalculatorContext settleCtx = 
				getSmartSQLContractSettleContext(connection, contractStart, contract);
				
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder() {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				System.out.println( description + ":" + amount + "," + startDate);
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
			}
		}).calculate(settleCtx);
		
		
		int decemberExtramonths = get(getToday(), Calendar.MONTH );
		int todayOfMonth = get(getToday(), Calendar.DAY_OF_MONTH );
		int lastdayOfMonth = AonDateUtils.getMax(getToday(), Calendar.DAY_OF_MONTH);
		int days = todayOfMonth == lastdayOfMonth ? 30 : Math.min(30, todayOfMonth );
		int julyExtraMonths = (decemberExtramonths < 7 ? decemberExtramonths + 5 : decemberExtramonths -7);
		
		double decemberExtra = ( 1750.00 * 1.10 ) * ((decemberExtramonths * 30) + days ) / 360;
		double julyExtra = ( 1750.00 * 1.10 ) * ((julyExtraMonths * 30) + days ) / 360;
		

		assertEquals( decemberExtra + julyExtra, settle.getTotalPayment(), DELTA);

	}

	@Test
	public void testSettleWithContractExtrasIV() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on

		PaymentConceptRecord pagaExtraConcept = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		
		Date contractStart = add(getFirstDayOfYear(getToday()), Calendar.YEAR, -2);
		Date contractEnd = getToday();
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				contractEnd,
				new HashMap<String, String>() {
					{
						put(TC2.getName(), "\"100\"");
						put(MONTH_DAYS.getName(), "30");
						put(QUOTE_GROUP.getName(), "\"01\"");
					}
				}, new String[] { "( P_1 + P_2 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" }, 
						new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * 0.00/100" }, 
						null);
		
		for ( Date date = contract.getStartDate() ; date.compareTo(contract.getEndDate()) <= 0; date = add(date, MONTH, 1) ) {
			addPayment(aonContext, contract, date, getLastDayOfMonth(date), pagaExtraConcept, "PAGA EXTRAORDINARIA JULIO", "P_0 + P_1 + P_2", "_P", "_P", PaymentType.CRA_0004, (byte) Month.JULY.ordinal());
			addPayment(aonContext, contract, date, getLastDayOfMonth(date), pagaExtraConcept, "PAGA EXTRAORDINARIA DICIEMBRE", "P_0 + P_1 + P_2", "_P", "_P", PaymentType.CRA_0004, (byte) Month.DECEMBER.ordinal());
			JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			new SmartContractSalaryCalculator<Salary>( jooqSalaryBuilder ).calculate( getContractSalaryCalculatorContext(connection, date, getLastDayOfMonth(date), getLastDayOfMonth(date), contract));
			jooqSalaryBuilder.execute();

		}
		//@formatter:off
		
		
		addSSRegimeStuff(aonContext);

		
		ISQLContractSalaryCalculatorContext settleCtx = 
				getSmartSQLContractSettleContext(connection, contractStart, contract);
				
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder() {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				System.out.println( "***" +description + ":" + amount + "," + startDate);
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
			}
		}).calculate(settleCtx);
		
		
		int currentMonth = get(getToday(), Calendar.MONTH ) ;
		int todayOfMonth = get(getToday(), Calendar.DAY_OF_MONTH );
		int lastdayOfMonth = AonDateUtils.getMax(getToday(), Calendar.DAY_OF_MONTH);
		int days = todayOfMonth == lastdayOfMonth ? 30 : Math.min(30, todayOfMonth );
		int julyExtraMonths = (currentMonth < 7 ? currentMonth + 5: currentMonth -7);

		//decemberExtramonths = decemberExtramonths == 11 ? 0 : decemberExtramonths;
		
		double decemberExtra = currentMonth == Calendar.DECEMBER ? 0.00 : ( 1750.00 * 1.10 ) * ((currentMonth * 30) + days ) / 360;
		double julyExtra = currentMonth == Calendar.JULY ? 0.00 :  ( 1750.00 * 1.10 ) * ((julyExtraMonths * 30) + days ) / 360;
		
		
		for (SalaryPayment payment : settle.getSalaryPayments()) {
			System.out.println(payment.getDescription() + " = " + payment.getAmount() + ", " );
		}
		
		assertEquals( decemberExtra + julyExtra, settle.getTotalPayment(), 0.05);

	}

	@Test
	public void testSettleWithContractExtrasV() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on

		PaymentConceptRecord pagaExtraConcept = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		
		Date contractStart = add(getFirstDayOfYear(getToday()), Calendar.YEAR, -2);
		Date contractEnd = getToday();
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				contractEnd,
				new HashMap<String, String>() {
					{
						put(TC2.getName(), "\"100\"");
						put(MONTH_DAYS.getName(), "30");
						put(QUOTE_GROUP.getName(), "\"01\"");
					}
				}, new String[] { "( P_1 + P_2 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" }, 
						new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * 0.00/100" }, 
						null);
		
		for ( Date date = contract.getStartDate() ; date.compareTo(contract.getEndDate()) <= 0; date = add(date, MONTH, 1) ) {
			Date endDate = getLastDayOfMonth(date); 
			if ( endDate.compareTo(contract.getEndDate()) >=  0) 
				endDate = null;
			ContractPaymentRecord extraJuly = addPayment(aonContext, contract, date, endDate, pagaExtraConcept, "PAGA EXTRAORDINARIA JULIO", "P_0 + P_1 + P_2", "_P", "_P", PaymentType.CRA_0004, (byte) Month.JULY.ordinal());
			ContractPaymentRecord extraDecember = addPayment(aonContext, contract, date, endDate, pagaExtraConcept, "PAGA EXTRAORDINARIA DICIEMBRE", "P_0 + P_1 + P_2", "_P", "_P", PaymentType.CRA_0004, (byte) Month.DECEMBER.ordinal());
			JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			new SmartContractSalaryCalculator<Salary>( jooqSalaryBuilder ).calculate( getContractSalaryCalculatorContext(connection, date, getLastDayOfMonth(date), getLastDayOfMonth(date), contract));
			jooqSalaryBuilder.execute();
			extraJuly.setDescription("PAGA EXTRAORDINARIA VERANO");
			extraDecember.setDescription("PAGA EXTRAORDINARIA NAVIDAD");
			extraJuly.update();
			extraDecember.update();

		}
		//@formatter:off
		
		
		addSSRegimeStuff(aonContext);

		
		ISQLContractSalaryCalculatorContext settleCtx = 
				getSmartSQLContractSettleContext(connection, contractStart, contract);
				
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder() {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				System.out.println( description + ":" + amount + "," + startDate);
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
			}
		}).calculate(settleCtx);
		
		
		int decemberExtramonths = get(getToday(), Calendar.MONTH );
		int todayOfMonth = get(getToday(), Calendar.DAY_OF_MONTH );
		int lastdayOfMonth = AonDateUtils.getMax(getToday(), Calendar.DAY_OF_MONTH);
		int days = todayOfMonth == lastdayOfMonth ? 30 : Math.min(30, todayOfMonth );
		int julyExtraMonths = (decemberExtramonths < 7 ? decemberExtramonths + 5: decemberExtramonths -7);
		
		double decemberExtra = ( 1750.00 * 1.10 ) * ((decemberExtramonths * 30) + days ) / 360;
		double julyExtra = ( 1750.00 * 1.10 ) * ((julyExtraMonths * 30) + days ) / 360;
		

		assertEquals( decemberExtra + julyExtra, settle.getTotalPayment(), 0.05);

	}

	@Test
	public void testSettleWithContractExtrasProrrI() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on

		PaymentConceptRecord pagaExtraConcept = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		
		Date contractStart = getFirstDayOfYear(getToday());
		Date contractEnd = getToday();
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				contractEnd,
				new HashMap<String, String>() {
					{
						put(TC2.getName(), "\"100\"");
						put(MONTH_DAYS.getName(), "30");
						put(QUOTE_GROUP.getName(), "\"01\"");
					}
				}, new String[] { "( P_1 + P_2 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" }, 
						new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * 0.00/100" }, 
						null);
		
		addPayment(aonContext, contract, contract.getStartDate(), contract.getEndDate(), pagaExtraConcept, "PAGA EXTRAORDINARIA JULIO", "P_0 + P_1 + P_2", "_P", "_P", PaymentType.CRA_0004);
		addPayment(aonContext, contract, contract.getStartDate(), contract.getEndDate(), pagaExtraConcept, "PAGA EXTRAORDINARIA DICIEMBRE", "P_0 + P_1 + P_2", "_P", "_P", PaymentType.CRA_0004);
		//@formatter:off
		
		
		addSSRegimeStuff(aonContext);
				
		ISQLContractSalaryCalculatorContext settleCtx = 
				getSmartSQLContractSettleContext(connection, contractStart, contract);
				
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder() {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				System.out.println( description + ":" + amount + "," + startDate);
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
			}
		}).calculate(settleCtx);
		
		
		
		
		int months = get(getToday(), Calendar.MONTH );
		int days = Math.min(30, get(getToday(), Calendar.DAY_OF_MONTH ));
		
		double extra = ( 1750.00 * 1.10 ) * ((months * 30) + days ) / 360;
		

		assertEquals( 0.00, settle.getTotalPayment(), DELTA);

	}

	@Test
	public void testSettleVacationsEREI() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		Date contractStart = add(getToday(), Calendar.MONTH, -2);
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				getToday(),
				new HashMap<String, String>() {
					{
						put(MONTH_DAYS.getName(), format("%d", 30));
//						put(COMPENSATION_CAUSE.getName(), OBJECTIVE.getName());
					}
				}, new String[] { "( P_1 + P_2 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES"
						}, 
						new String[] {
//						"BASE_CGC * 0.10", 
//						"BASE_CGP * 0.05",
//						"BASE_IRPF * PORCENTAJE_IRPF/100" 
						}, 
				null);
		//@formatter:off
		
		 addSSRegimeStuff(aonContext);
		// VACACIONES RETRIBUIDAS NO DISFRUTADAS
//		addSSRegimePayment(aonContext, 
//				SSRegimeType.GENERAL, 
//				contract.getStartDate(), 
//				PaymentType.CRA_0006, 
//				"DIAS_VACACIONES_NO_DISFRUTADOS * ( SALARIO_DIA + SALARIO_VARIABLE_DIA )",
//				"_P" ,
//				"_P", 
//				SalaryType.SETTLE);
		
		setData(aonContext, contract, 
				add(getToday(), Calendar.DAY_OF_MONTH,1)
				, null
				, new HashMap<String, String>() {
			{
				put("DIAS_VACACIONES_NO_DISFRUTADOS", format("%d", 4));
			}
		});
		
		setData(aonContext, contract, 
				getFirstDayOfMonth(getToday())
				, getToday()
				, new HashMap<String, String>() {
			{
				put("COEFICIENTE_ERE_FZA_EXONERADO", format("%f", 1.0));
			}
		});
		PaymentConceptRecord ere = addConcept(aonContext,"ERE_FZA_EXONERADO");
		addPayment(aonContext, contract, ere, "0.00" , "DIAS_ERE_FZA_EXONERADO* BASE_REGULADORA");
		
		ISQLContractSalaryCalculatorContext ctx = 
				getSmartSQLContractSettleContext(connection, contract.getStartDate(), contract.getEndDate(), contract);
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double br = (1750.00 * 1.10)  * 12 / 365; 
		
//		settle.getSalaryDatas().stream().forEach(d->System.out.println(d.getName() + " = "  + d.getExpression() ));
		settle.getSalaryPayments().stream().forEach(p->System.out.println(p.getExpression() + " = "  + p.getAmount() ));

		assertEquals( ( br * 4 ), settle.getTotalPayment(), DELTA);
		
		assertEquals( br * 4 , settle.getCommonBase(), DELTA);
		
	}
	
	@Disabled("Too difficult for SALARIO_DIA")
	@Test
	public void testSettleVacationsEREII() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		Date contractStart = add(getToday(), Calendar.MONTH, -2);
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				getToday(),
				new HashMap<String, String>() {
					{
						put(MONTH_DAYS.getName(), format("%d", 30));
//						put(COMPENSATION_CAUSE.getName(), OBJECTIVE.getName());
					}
				}, new String[] { "( P_1 + P_2 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES"
						}, 
						new String[] {
//						"BASE_CGC * 0.10", 
//						"BASE_CGP * 0.05",
//						"BASE_IRPF * PORCENTAJE_IRPF/100" 
						}, 
				null);
		//@formatter:off
		
		// addSSRegimeStuff(aonContext);
		// VACACIONES RETRIBUIDAS NO DISFRUTADAS
		addSSRegimePayment(aonContext, 
				SSRegimeType.GENERAL, 
				contract.getStartDate(), 
				PaymentType.CRA_0006, 
				"DIAS_VACACIONES_NO_DISFRUTADOS * ( SALARIO_DIA + SALARIO_VARIABLE_DIA )",
				"_P" ,
				"_P", 
				SalaryType.SETTLE);
		
		setData(aonContext, contract, 
				add(getToday(), Calendar.DAY_OF_MONTH,1)
				, null
				, new HashMap<String, String>() {
			{
				put("DIAS_VACACIONES_NO_DISFRUTADOS", format("%d", 4));
			}
		});
		
		setData(aonContext, contract, 
				getFirstDayOfMonth(getToday())
				, getToday()
				, new HashMap<String, String>() {
			{
				put("COEFICIENTE_ERE_FZA_EXONERADO", format("%f", 0.50));
			}
		});
		PaymentConceptRecord ere = addConcept(aonContext,"ERE_FZA_EXONERADO");
		addPayment(aonContext, contract, ere, "TRACE('BASE_REGULADORA=%f\r\n', DIAS_ERE_FZA_EXONERADO*BASE_REGULADORA);0.00" , "DIAS_ERE_FZA_EXONERADO* BASE_REGULADORA");
		

		ISQLContractSalaryCalculatorContext ctx = 
				getSmartSQLContractSettleContext(connection, contract.getStartDate(), contract.getEndDate(), contract);
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double br = (1750.00 * 1.10)  * 12 / 365; 
		
//		settle.getSalaryDatas().stream().forEach(d->System.out.println(d.getName() + " = "  + d.getExpression() ));
		settle.getSalaryPayments().stream().forEach(p->System.out.println(p.getExpression() + " = "  + p.getAmount() ));

		assertEquals( ( br * 4 ), settle.getTotalPayment(), DELTA);
		
		assertEquals( br * 4 , settle.getCommonBase(), DELTA);
		
	}

	@Test
	public void testSettleWithEmbargoSave() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		Date contractStart = add(getToday(), Calendar.MONTH, -2);
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				new HashMap<String, String>() {
					{
						put(MONTH_DAYS.getName(), format("%d", 30));
						//put(COMPENSATION_CAUSE.getName(), OBJECTIVE.getName());
					}
				}, new String[] { "( P_1 + P_2 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" }, 
						new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05" }, null);
		//@formatter:off
		
		addSSRegimeStuff(aonContext);
		
		int year = get(getToday(), Calendar.YEAR);
		
		AgreementExtraRecord julyExtra;
		ISQLContractSalaryCalculatorContext extraCtx;
		JooqSalaryBuilder<Salary> jooqSalaryBuilder;
		

		ISQLContractSalaryCalculatorContext ctx = 
				getSmartSQLContractSettleContext(connection, contractStart, contract);
		
		addSettleEmbargo(aonContext, contract, "EMBARGO FIJO", "6.66");
		
		jooqSalaryBuilder = new JooqSalaryBuilder<>(aonContext.getDslContext());
		Salary settle = new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();
		
		Embargo embargo = 
		AON.getSalaries(aonContext, 
		p -> p.getContractProperty().eq(contract.getId()))
		.map(salary -> salary.getEmbargos())
		.flatMap(List::stream)
		.findFirst().orElseThrow(AssertionError::new )
		;
		
		System.out.println(embargo.getDescription() + " = " + embargo.getAmount() );
	}

	@Test
	public void testSettleWithExtrasOverrideI() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		PaymentConceptRecord pagaExtraConcept = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
						this.concept = pagaExtraConcept.getId();
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
						this.concept = pagaExtraConcept.getId();
					}
				}, });
		
		Date contractStart = add(getFirstDayOfYear(getToday()), Calendar.YEAR, -2 );
		Date contractEnd = getToday();
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				contractEnd,
				new HashMap<String, String>() {
					{
						put(TC2.getName(), "\"100\"");
						put(MONTH_DAYS.getName(), "30");
						put(QUOTE_GROUP.getName(), "\"01\"");
					}
				}, new String[] { 
						"( P_1 + P_2 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" }, 
						new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * 0.00/100" }, 
						category);
		//@formatter:off
		
		
		addSSRegimeStuff(aonContext);
		
		addPayment(aonContext, contract, contractStart, getLastDayOfYear(contractStart), pagaExtraConcept, "PAGA_EXTRA", "REMOVE()", null, null, PaymentType.CRA_0004);
		addPayment(aonContext, contract, contractStart, getLastDayOfYear(contractStart), pagaExtraConcept, "PAGA_EXTRA", "REMOVE()", null, null, PaymentType.CRA_0004);

		
		ISQLContractSalaryCalculatorContext settleCtx = 
				getSmartSQLContractSettleContext(connection, contractStart, contract);
		
		settleCtx.getExpressionContext().eval("TRACE('SALARIO_DIA:%f\r\n', SALARIO_DIA)", contractStart, getToday())
		;
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder() {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				System.out.println( description + ":" + amount + "," + startDate + ".." + endDate );
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
			}
		}).calculate(settleCtx);
		
		
		int months = get(getToday(), Calendar.MONTH );
		int todayOfMonth = get(getToday(), Calendar.DAY_OF_MONTH );
		int lastdayOfMonth = AonDateUtils.getMax(getToday(), Calendar.DAY_OF_MONTH);
		int days = todayOfMonth == lastdayOfMonth ? 30 : Math.min(30, todayOfMonth );
		double decemberExtra = ( 1750.00 * 1.10 ) * ((months * 30) + days ) / 360;
		int julyExtraMonths = (months >= 6 ? months -6 : months + 6 );
		// Settle at 01/07, so we have two extras for July .
		//if ( get(getToday(), Calendar.MONTH ) == 6 && get(getToday(), Calendar.DAY_OF_MONTH ) == 1)
		//julyExtraMonths += 6;

		double julyExtra = ( 1750.00 * 1.10 ) * ((julyExtraMonths  * 30) + days ) / 360 ;
		

		if ( months == Calendar.DECEMBER && days > 15 )
			decemberExtra = 0.00; 
		// 'December Extra...' have been already emitted. 

		assertEquals( decemberExtra + julyExtra, settle.getTotalPayment(), DELTA);

	}

	@Test
	public void testSettleTotalBasesVariables() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		
		Date contractStart = getFirstDayOfYear(getToday());
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				getToday(),
				new HashMap<String, String>() {
					{
						put(TC2.getName(), "\"402\"");
						put(QUOTE_GROUP.getName(), "\"08\"");
						put(MONTH_DAYS.getName(), format("%d", 30));
					}
				}, 
				new String[] { 
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				"250.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				"TOTAL_BASE_CGC_E; TOTAL_BASE_CGP_E;  TOTAL_BASE_CGC; TOTAL_BASE_CGP*(INDEFINIDO?1.55:1.60)/100"
				}, 
				null);
		//@formatter:off
		
		addSSRegimeStuff(aonContext);

		setData(aonContext, contract, 
				add(getToday(), Calendar.DAY_OF_MONTH,1)
				, null
				, new HashMap<String, String>() {
			{
				put("DIAS_VACACIONES_NO_DISFRUTADOS", format("%d", 4));
			}
		});
		
		ISQLContractSalaryCalculatorContext ctx = 
				getSmartSQLContractSettleContext(connection, contractStart, contract);
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double br = (1750.00) * 12 / 365; 
		
		settle.getSalaryDatas().stream().forEach(d->System.out.println(d.getName() + " = "  + d.getExpression() ));
		
		for ( ContextVariable ctxVar: new ContextVariable [] {
			ContextVariable.CGC_BASE, 
			ContextVariable.CGP_BASE,
			ContextVariable.TOTAL_CGC_BASE,
			ContextVariable.TOTAL_CGP_BASE,
			ContextVariable.TOTAL_CGC_BASE_ENTERPRISE,
			ContextVariable.TOTAL_CGP_BASE_ENTERPRISE
			}
			) {
        		SalaryData salaryData = getSalaryData(settle, ctxVar);
        		org.junit.assertEquals(ctxVar.getName(), add(getToday(), Calendar.DAY_OF_MONTH,1), salaryData.getStartDate());
        		org.junit.assertEquals(ctxVar.getName(), add(getToday(), Calendar.DAY_OF_MONTH,4), salaryData.getEndDate());
		}

		assertEquals( br * 4 , settle.getCommonBase(), DELTA);
		assertEquals( br * 4 , settle.getTotalPayment(), DELTA);
		assertEquals( settle.getCommonBase() * 1.60 / 100 , settle.getTotalDeduction(), DELTA);
	
		
	}

	// ------------------------------------------------------------------------
	
	public SalaryData getSalaryData(Salary salary, ContextVariable var) {
		return 
	    	salary.getSalaryDatas()
		.stream().filter( d -> d.getName().equals(var.getName()))
		.findAny().orElseThrow(AssertionError::new);
	}

	public  void addSSRegimeStuff(AONContext aonContext) {
		
		Date startDate = AonDateUtils.add(getFirstDayOfYear(getToday()), Calendar.YEAR, -3);
		
		addSSRegimeData(aonContext, 
				SSRegimeType.GENERAL, 
				startDate, 
				null, 
				new HashMap<String, String>() {
					{
						put("PORCENTAJE_CGC", format(Locale.US,"%f", 4.70));
						put("DIAS_INDEMNIZACION_FIN", format("%d", 12));
						put("BASE_CGC_MAX","TRACE('DIAS_NOMINA = %f\r\n', 3642.00 * DIAS_NOMINA/30);(3642.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30))");						
						put("BASE_CGC_MIN","(TIEMPO_COMPLETO ? 1056.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 6.37 * HORAS_NOMINA)");						
					}
				});
		
		// INDEMNIZACION POR FIN DE OBRA
		addSSRegimePayment(aonContext, 
				SSRegimeType.GENERAL, 
				startDate, 
				PaymentType.CRA_0054, 
				"(CAUSA_INDEMNIZACION == FIN_OBRA) ? DIAS_INDEMNIZACION_FIN(INICIO_CONTRATO) * AÑOS_TRABAJADOS * SALARIO_DIA : REMOVE()",
				null ,
				"_P", 
				SalaryType.SETTLE);

		// INDEMNIZACION POR CESE
		addSSRegimePayment(aonContext, 
				SSRegimeType.GENERAL, 
				startDate, 
				PaymentType.CRA_0054, 
				"(CAUSA_INDEMNIZACION == FIN) ? DIAS_INDEMNIZACION_FIN(FIN_CONTRATO) * AÑOS_TRABAJADOS * SALARIO_DIA : REMOVE()",
				null ,
				null, 
				SalaryType.SETTLE);
		
		//INDEMNIZACION POR DESPIDO IMPROCEDENTE (< 13 DE FEBRERO DE 2012)
		addSSRegimePayment(aonContext, 
				SSRegimeType.GENERAL, 
				add(getFirstDayOfYear(getToday()),Calendar.YEAR,-1), 
				PaymentType.CRA_0054, 
				"(CAUSA_INDEMNIZACION == IMPROCEDENTE) ? MIN(45 * AÑOS_TRABAJADOS * SALARIO_DIA,SALARIO_DIA * 365 / 12 * 42 ): REMOVE()",
				null ,
				null, 
				SalaryType.SETTLE);
		
		//INDEMNIZACION POR DESPIDO IMPROCEDENTE (>= 13 DE FEBRERO DE 2012)
		addSSRegimePayment(aonContext, 
				SSRegimeType.GENERAL, 
				startDate, 
				PaymentType.CRA_0054, 
				"(CAUSA_INDEMNIZACION==IMPROCEDENTE)?MIN(33*AÑOS_TRABAJADOS*SALARIO_DIA,ABS(SALARIO_DIA*365/12*24-INDEMNIZACION)):REMOVE()",
				null ,
				null, 
				SalaryType.SETTLE);
		
		//INDEMNIZACION POR DESPIDO POR CAUSAS OBJETIVAS
		addSSRegimePayment(aonContext, 
				SSRegimeType.GENERAL, 
				startDate, 
				PaymentType.CRA_0054, 
				" (CAUSA_INDEMNIZACION == PROCEDENTE ) ? MIN(20 * AÑOS_TRABAJADOS * SALARIO_DIA, SALARIO_DIA * 365 / 12 * 12 ) : REMOVE()",
				null ,
				null, 
				SalaryType.SETTLE);
		// INDEMNIZACION POR FIN DE CONTRATO TEMPORAL
		addSSRegimePayment(aonContext, 
			SSRegimeType.GENERAL, 
			startDate, 
			PaymentType.CRA_0054, 
			" (CAUSA_INDEMNIZACION == FIN_TEMPORAL ) ? DIAS_INDEMNIZACION_FIN(INICIO_CONTRATO) * SALARIO_DIA * DIAS_TRABAJADOS / 365 : REMOVE()",
			null ,
			null, 
			SalaryType.SETTLE);

		// VACACIONES RETRIBUIDAS NO DISFRUTADAS
		addSSRegimePayment(aonContext, 
				SSRegimeType.GENERAL, 
				startDate, 
				PaymentType.CRA_0006, 
				"DIAS_VACACIONES_NO_DISFRUTADOS * ( SALARIO_DIA + SALARIO_VARIABLE_DIA )",
				"_P" ,
				"_P", 
				SalaryType.SETTLE);
		
	}
	
	@Test
	@Disabled
	public void testSettleSalaryDayWithVacations() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		Date contractStart = add(getToday(), Calendar.MONTH, -1);
		contractStart = getFirstDayOfMonth(contractStart);
		ContractRecord contract = newContract(aonContext, 
				contractStart,
				new HashMap<String, String>() {
					{
						put(MONTH_DAYS.getName(), format("%d", 30));
//						put(COMPENSATION_CAUSE.getName(), OBJECTIVE.getName());
					}
				}, new String[] { "( P_1 + P_2 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES"
						}, 
						new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" }
				, null);
		//@formatter:off
		
		addSSRegimeStuff(aonContext);
		

		JooqSalaryBuilder jooqSalaryBuilder  =  new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>( jooqSalaryBuilder )
		.calculate(getContractSalaryCalculatorContext(connection, contractStart, getLastDayOfMonth(contractStart), getLastDayOfMonth(contractStart), contract));
		jooqSalaryBuilder.execute();
		
		addPayment(aonContext, contract, getFirstDayOfMonth(getToday()), "500.00");

		jooqSalaryBuilder  =  new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>( jooqSalaryBuilder )
		.calculate(getContractSalaryCalculatorContext(connection, getFirstDayOfMonth(getToday()), getToday(), getToday(), contract));
		jooqSalaryBuilder.execute();

		
		setData(aonContext, contract, 
				add(getToday(), Calendar.DAY_OF_MONTH,1)
				, null
				, new HashMap<String, String>() {
			{
				put("DIAS_VACACIONES_NO_DISFRUTADOS", format("%d", 4));
			}
		});
		
		ISQLContractSalaryCalculatorContext ctx = 
				getSmartSQLContractSettleContext(connection, contractStart, contract);
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		long days = get(getToday(), Calendar.DAY_OF_MONTH);
		double salaryDay = ((1750.00 * 1.10) / 30 * days   + 500.00 ) / days ; 
		
		assertEquals( ( salaryDay * 4 ), settle.getTotalPayment(), DELTA);
		
		assertEquals( salaryDay * 4 , settle.getCommonBase(), DELTA);
		
	}
	// ------------------------------------------------------------------------

	protected void setData(AONContext aonContext,
			ContractRecord contract, Date startDate, Date endDate,
			Map<String, String> datas) {
		AbstractSQLTestCase.addData(aonContext, contract, startDate, endDate, datas);
	}
	
	protected void addSettlePayment(AONContext aonContext, ContractRecord contract, Date startDate, Date endDate, String description,
			String expression, String irpfExpression, String quoteExpression, PaymentType type) {
		addPayment(aonContext, contract, startDate, endDate, description, expression, irpfExpression, quoteExpression, type, SalaryType.SETTLE);
	}
	
	protected void addSettleEmbargo(AONContext aonContext, ContractRecord contract, String description, String expression) {
		addEmbargo(aonContext, contract, "EMBARGO FIJO", "6.66");		
	}
	
	protected ISQLContractSalaryCalculatorContext getSmartSQLContractSettleContext(Connection connection, Date contractStart,
			ContractRecord contract) throws SQLException, ExpressionException {
		return getSmartSQLContractSettleContext(connection, contractStart, getToday(), contract);
	}

	protected ISQLContractSalaryCalculatorContext getSmartSQLContractSettleContext(Connection connection, Date contractStart,
			Date endDate, ContractRecord contract) throws SQLException, ExpressionException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		ISQLContractSalaryCalculatorContext ctx = new SmartSQLContractSettleCalculatorContext(connection, contractStart,
				endDate, endDate, criteria);
		ctx.next();
		return ctx;
	}

	
}
