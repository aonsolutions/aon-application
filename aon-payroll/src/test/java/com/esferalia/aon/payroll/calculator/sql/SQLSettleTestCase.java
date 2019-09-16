package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS;
import static com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
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

import org.apache.commons.math3.util.Precision;
import org.junit.Test;

import com.code.aon.common.enumeration.Month;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.AgreementExtraRecord;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
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
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.util.AonDateUtils;

import junit.framework.Assert;

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
		
		Assert.assertEquals(seniority, ctx.getStartDate());
		for ( ITimedResult<Object> result:  ctx.getExpressionContext().eval("AÑOS_TRABAJADOS", seniority, getToday()))
			Assert.assertEquals( (2.00 + 2/12.00), result.getValue());

		double br = (1750.00) * 12.00 / 365; //AonDateUtils.getMax(getToday(), DAY_OF_YEAR);

		for ( ITimedResult<Object> result:  ctx.getExpressionContext().eval("SALARIO_DIA", seniority, getToday())) {
			Assert.assertEquals( seniority, result.getPeriod().getStart());
			Assert.assertEquals( br , result.getValue());
		}


		for ( ITimedResult<Object> result:  ctx.getExpressionContext().eval("12.00 * AÑOS_TRABAJADOS * SALARIO_DIA", seniority, getToday())) {
			Assert.assertEquals( seniority, result.getPeriod().getStart());
			Assert.assertEquals( 12.00 * (2.00 + 2/12.00) * br , result.getValue());
		}
			
		for ( ITimedResult<Object> result:  ctx.getExpressionContext().eval("FIN", seniority, getToday())) {
			Assert.assertEquals( seniority, result.getPeriod().getStart());
		}

		for ( ITimedResult<Object> result:  ctx.getExpressionContext().eval("CAUSA_INDEMNIZACION", seniority, getToday())) {
			Assert.assertEquals( seniority, result.getPeriod().getStart());
		}

		for ( ITimedResult<Object> result:  ctx.getExpressionContext().eval("(CAUSA_INDEMNIZACION == FIN) ? 12.00 * AÑOS_TRABAJADOS * SALARIO_DIA: 0.00", seniority, getToday()))
			System.out.println(result.getPeriod().getStart() + ".." + result.getPeriod().getEnd() + " = " + result.getValue() );
			//Assert.assertEquals( 12.00 * (2.00 + 2/12.00) * br , result.getValue());

		Salary settle = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double noHolidyas = getNoHolidays(contractStart, getToday());

		Assert.assertEquals( 12 * (2.00 + 2/12.00) * br + noHolidyas * br , settle.getTotalPayment());
		Assert.assertEquals( 12 * ( 2 + 2/12.00) * br + noHolidyas * br * 0.85, settle.getTotalLiquid());
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
		
		double noHolidays = getNoHolidays(contractStart, getToday());

		Assert.assertEquals( 12 * (2/12.00) * br + noHolidays * br, settle.getTotalPayment(), DELTA);
		Assert.assertEquals( 12 * (2/12.00) * br + noHolidays * br * 0.85 , settle.getTotalLiquid(), DELTA);
	}

	private double getNoHolidays(Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		int days = AonDateUtils.get(endDate, Calendar.DAY_OF_YEAR) - AonDateUtils.get(startDate, Calendar.DAY_OF_YEAR) + 1;
		if(days < 0)
			days = AonDateUtils.get(endDate, Calendar.DAY_OF_YEAR);
		
		return  Precision.round(days * 30.00 / 365, 2);
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
						"BASE_CGP * 0.05"
//						,"BASE_IRPF * PORCENTAJE_IRPF/100" 
						}, category);
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
		double noHolidays = getNoHolidays(contractStart, getToday());

		Assert.assertEquals( 20 * (2/12.00) * br + noHolidays * br, settle.getTotalPayment());
		
		System.out.println( (20 * (2/12.00) * br ) + " = " + settle.getTotalPayment() );
		
		Assert.assertEquals( 20 * (2/12.00) * br + noHolidays * br * 0.85, settle.getTotalLiquid());
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

		Assert.assertEquals( 20 * (2/12.00) * br + ( br * 4 ), settle.getTotalPayment());
		
		Assert.assertEquals( br * 4 , settle.getCommonBase());
		
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

		Assert.assertEquals( br * 4 , settle.getCommonBase(), DELTA);
		Assert.assertEquals( 20 * (2/12.00) * br + ( br * 4 ), settle.getTotalPayment(), DELTA);
		Assert.assertEquals( settle.getCommonBase() * 1.60 / 100 , settle.getTotalDeduction(), DELTA);
	
		
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
		
		double br = (1750.00 * 1.10) * (1 + 1.00 / 12 + 1.00 / 12) * 12 / 365; 

		Assert.assertEquals( 20 * (2/12.00) * br + ( br * (10 + noHolidays) ), settle.getTotalPayment(), DELTA);
		
		Assert.assertEquals( br * ( 10 + noHolidays) , settle.getCommonBase(), DELTA);
		
		SalaryData cgcBases [] = settle.getSalaryDatas()
				.stream()
				.filter(s->s.getName().equals(ContextVariable.CGC_BASE.getName()))
				.sorted((s1,s2)-> s1.getStartDate().compareTo(s2.getStartDate()) )
				.toArray(l-> new SalaryData[l]);
		
		Assert.assertEquals(2, cgcBases.length);
		
		Assert.assertEquals(startNoHolidays, cgcBases[0].getStartDate());
		Assert.assertEquals(endNoHolidays, cgcBases[0].getEndDate());
		Assert.assertEquals(br*noHolidays, Double.parseDouble(cgcBases[0].getExpression()), DELTA);
		
		Assert.assertEquals(AonDateUtils.add(endNoHolidays, DAY_OF_MONTH, 1), cgcBases[1].getStartDate());
		Assert.assertEquals(AonDateUtils.add(endNoHolidays, DAY_OF_MONTH, 10), cgcBases[1].getEndDate());
		Assert.assertEquals(br*10.00, Double.parseDouble(cgcBases[1].getExpression()), DELTA);
		
	}


	@Test
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
		
		Assert.assertTrue(optional.isPresent());
		
		com.esferalia.aon.occam.api.model.Salary settle = optional.get();
		
		double br = (1750.00 * 1.10) * (1 + 1.00 / 12 + 1.00 / 12) * 12 / 365; 

		Assert.assertEquals( 20 * (2/12.00) * br + ( br * (10 + noHolidays) ), settle.getTotalPayment(), DELTA);
		
		Assert.assertEquals( br * ( 10 + noHolidays) , settle.getCommonContingenciesBase(), DELTA);
		

		List<ContextData> cgcBases = settle.getContextData().get(ContextVariable.CGC_BASE.getName());
		
		Collections.sort(cgcBases, (d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate()));
		
		Assert.assertEquals(2, cgcBases.size());

		Assert.assertEquals(startNoHolidays, cgcBases.get(0).getStartDate());
		Assert.assertEquals(endNoHolidays, cgcBases.get(0).getEndDate());
		Assert.assertEquals(br*noHolidays, Double.parseDouble(cgcBases.get(0).getExpression()));

		Assert.assertEquals(AonDateUtils.add(endNoHolidays, DAY_OF_MONTH, 1), cgcBases.get(1).getStartDate());
		Assert.assertEquals(AonDateUtils.add(endNoHolidays, DAY_OF_MONTH, 10), cgcBases.get(1).getEndDate());
		Assert.assertEquals(br*10.00, Double.parseDouble(cgcBases.get(1).getExpression()));
		
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
		
		
//		setData(aonContext, contract, 
//				startNoHolidays
//				, endNoHolidays
//				, new HashMap<String, String>() {
//			{
//				put("DIAS_VACACIONES_NO_DISFRUTADOS", format("%d", 10));
//			}
//		});
		
		
//		ISQLContractSalaryCalculatorContext ctx = 
//				getSQLContractSettleContext(connection, contractStart, contract);
		
		ISQLContractSalaryCalculatorContext ctx = 
				getSQLContractSettleContext(connection, contractStart, endContract, contract);
		
		Salary settle = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double noHolidays = getNoHolidays(contractStart, endContract);
		
		Assert.assertEquals(( 1000.00 * 12 / 365 * noHolidays ) , settle.getCommonBase());
		Assert.assertEquals(( 1000.00 * 12 / 365 * noHolidays ) * (4.70/100.00), settle.getTotalDeduction(), DELTA);
		
//		Assert.assertEquals(( 1000.00 * 12 / 365 * 10.00 ) , settle.getCommonBase());
//		Assert.assertEquals(( 1000.00 * 12 / 365 * 10.00 ) * (4.70/100.00), settle.getTotalDeduction());
		
		
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
				new String[] {"2000.00 * DIAS_TRABAJADOS / DIAS_MES"}, 
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
		
		
//		setData(aonContext, contract, 
//				startNoHolidays
//				, endNoHolidays
//				, new HashMap<String, String>() {
//			{
//				put("DIAS_VACACIONES_NO_DISFRUTADOS", format("%d", 30));
//			}
//		});
		
		
//		ISQLContractSalaryCalculatorContext ctx = 
//				getSQLContractSettleContext(connection, contractStart, contract);
		
		ISQLContractSalaryCalculatorContext ctx = 
				getSQLContractSettleContext(connection, contractStart, endContract, contract);
		
		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double noHolidays = getNoHolidays(contractStart, endContract);
		
		Assert.assertEquals(( 2000.00 * 12 / 365 * noHolidays ) , settle.getIrpfBase());
//		Assert.assertEquals(( 2000.00 * 12 / 365 * 30.00 ), settle.getIrpfBase());
		
		Collection<SalaryDeduction> deductions = settle.getSalaryDeductions();
		org.junit.Assert.assertEquals(1, deductions.size());
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
		Assert.assertTrue(sirpf > 0 );
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
		

		Assert.assertTrue( settle.getTotalPayment() > 0.00);

	}
	
	@Test
	public void testSettleQuoteDays() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);

		Date contractStart = add(getToday(), Calendar.MONTH, -2);
		Date contractEnd = add(getToday(), Calendar.MONTH, 1);
		ContractRecord contract = newContract(aonContext, 
				contractStart, 
				contractEnd, 
				new HashMap<String, String>() {
					{}
				}, 
				new String[] { "( P_1 + P_2 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES"
						}, 
				new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" }, 
				null);

		addSSRegimeStuff(aonContext);

		ISQLContractSalaryCalculatorContext ctx = 
				getSmartSQLContractSettleContext(connection, contractStart, contractEnd, contract);

		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);

		double br = (1750.00 * 1.10) * 12 / 365; 
		double noHolidays = getNoHolidays(contractStart, contractEnd);
		
		Assert.assertEquals(  br * noHolidays, settle.getTotalPayment(), DELTA);
		Assert.assertEquals( br * noHolidays , settle.getCommonBase(), DELTA);

		SalaryData salaryQuoteDatas [] = settle.getSalaryDatas()
				.stream()
				.filter(s->s.getName().equals(ContextVariable.QUOTE_DAYS.getName()))
				.sorted((s1,s2)-> s1.getStartDate().compareTo(s2.getStartDate()) )
				.toArray(l-> new SalaryData[l]);

		SalaryData salaryBCGCDatas [] = settle.getSalaryDatas()
				.stream()
				.filter(s->s.getName().equals(ContextVariable.CGC_BASE.getName()))
				.sorted((s1,s2)-> s1.getStartDate().compareTo(s2.getStartDate()) )
				.toArray(l-> new SalaryData[l]);

//		System.out.println("CONTRACT PERIOD -> StartDate : " + contractStart + ", EndDate : " + contractEnd + ", salaryQuoteDatas lenght : " + salaryQuoteDatas.length);
//		for(SalaryData salaryQuoteData : salaryQuoteDatas){
//			System.out.println(salaryQuoteData.getName() + " : " + salaryQuoteData.getExpression() + ", Start : " + salaryQuoteData.getStartDate() + ", endDate : " + salaryQuoteData.getEndDate());
//		}
//
//		System.out.println("BASE CGC CONTRACT PERIOD-> StartDate : " + contractStart + ", EndDate : " + contractEnd + ", salaryBCGCDatas lenght : " + salaryBCGCDatas.length);
//		for(SalaryData salaryBCGCData : salaryBCGCDatas){
//			System.out.println(salaryBCGCData.getName() + " : " + salaryBCGCData.getExpression() + ", Start : " + salaryBCGCData.getStartDate() + ", endDate : " + salaryBCGCData.getEndDate());
//		}

		Assert.assertEquals(addDays(contractEnd,1), salaryBCGCDatas[salaryBCGCDatas.length-1].getStartDate());
		Assert.assertEquals(addDays(contractEnd, (int)Math.ceil(noHolidays)), salaryQuoteDatas[salaryQuoteDatas.length-1].getEndDate());

	}
	
	@Test
	public void testSettleQuoteDaysII() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date contractStart = add(getToday(), Calendar.MONTH, -2);
		Date contractEnd = getToday();
		ContractRecord contract = newContract(aonContext, 
				contractStart, 
				contractEnd, 
				new HashMap<String, String>() {
					{}
				}, 
				new String[] { "( P_1 + P_2 ) * 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES"
						}, 
				new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" }, 
				null);

		addSSRegimeStuff(aonContext);

		ISQLContractSalaryCalculatorContext ctx = 
				getSmartSQLContractSettleContext(connection, contractStart, contractEnd, contract);

		Salary settle = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);

		double br = (1750.00 * 1.10) * 12 / 365; 
		double noHolidays = getNoHolidays(contractStart, contractEnd);

		Assert.assertEquals(  br * noHolidays, settle.getTotalPayment(), DELTA);
		Assert.assertEquals( br * noHolidays , settle.getCommonBase(), DELTA);

		SalaryData salaryQuoteDatas [] = settle.getSalaryDatas()
				.stream()
				.filter(s->s.getName().equals(ContextVariable.QUOTE_DAYS.getName()))
				.sorted((s1,s2)-> s1.getStartDate().compareTo(s2.getStartDate()) )
				.toArray(l-> new SalaryData[l]);

		SalaryData salaryBCGCDatas [] = settle.getSalaryDatas()
				.stream()
				.filter(s->s.getName().equals(ContextVariable.CGC_BASE.getName()))
				.sorted((s1,s2)-> s1.getStartDate().compareTo(s2.getStartDate()) )
				.toArray(l-> new SalaryData[l]);

		System.out.println("CONTRACT PERIOD -> StartDate : " + contractStart + ", EndDate : " + contractEnd);
		for(SalaryData salaryQuoteData : salaryQuoteDatas){
			System.out.println(salaryQuoteData.getName() + " : " + salaryQuoteData.getExpression() + ", Start : " + salaryQuoteData.getStartDate() + ", endDate : " + salaryQuoteData.getEndDate());
		}

		System.out.println("BASE CGC CONTRACT PERIOD-> StartDate : " + contractStart + ", EndDate : " + contractEnd + ", salaryBCGCDatas lenght : " + salaryBCGCDatas.length);
		for(SalaryData salaryBCGCData : salaryBCGCDatas){
			System.out.println(salaryBCGCData.getName() + " : " + salaryBCGCData.getExpression() + ", Start : " + salaryBCGCData.getStartDate() + ", endDate : " + salaryBCGCData.getEndDate());
		}

		Assert.assertEquals(addDays(contractEnd, 1), salaryBCGCDatas[salaryBCGCDatas.length-1].getStartDate());
		Assert.assertEquals(addDays(getToday(), (int)Math.ceil(noHolidays)), salaryQuoteDatas[salaryQuoteDatas.length-1].getEndDate());

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
		int days = Math.min(30, get(getToday(), Calendar.DAY_OF_MONTH ));
		double decemberExtra = ( 1750.00 * 1.10 ) * ((months * 30) + days ) / 360;
		int julyExtraMonths = (months >= 6 ? months -6 : months );
		// Settle at 01/07, so we have two extras for July .
		if ( get(getToday(), Calendar.MONTH ) == 6 && get(getToday(), Calendar.DAY_OF_MONTH ) == 1)
			julyExtraMonths += 6;

		double julyExtra = ( 1750.00 * 1.10 ) * ((julyExtraMonths  * 30) + days ) / 360 ;
		

		if ( months == Calendar.DECEMBER && days > 15 )
			decemberExtra = 0.00; 
		// 'December Extra...' have been already emitted. 
		
		double noHolidyas = getNoHolidays(contractStart, getToday());
		double daySalary = (1750 * 1.10 + 1750 * 1.10 / 6) * 12 / 365;

		Assert.assertEquals( decemberExtra + julyExtra + noHolidyas * daySalary, settle.getTotalPayment(), DELTA);

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
		int days = Math.min(30,get(getToday(), Calendar.DAY_OF_MONTH ));
				
		
		
		double decemberExtra = ( 1750.00 * 1.10 ) * ((months * 30) + days ) / 360;
		int julyExtraMonths = (months >= Calendar.JULY ? months - Calendar.JULY : months );
		// Settle at 01/07, so we have two extras for July .
		if ( get(getToday(), Calendar.MONTH ) == 6 && get(getToday(), Calendar.DAY_OF_MONTH ) == 1)
			julyExtraMonths += 6;
		double julyExtra = ( 1750.00 * 1.10 ) * ((julyExtraMonths * 30) + days ) / 360 ;
		
		if ( months == Calendar.DECEMBER && days > 15 )
			decemberExtra = 0.00; 
		// 'December Extra...' have been already emitted.
		
		double noHolidays = getNoHolidays(contractStart, getToday());
		double daySalary = (1750 * 1.10 + 1750 * 1.10 / 6) * 12 / 365;
		
		Assert.assertEquals( decemberExtra + julyExtra + noHolidays * daySalary, settle.getTotalPayment(), DELTA);

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
		int days = Math.min(30, get(getToday(), Calendar.DAY_OF_MONTH ));
		
		double decemberExtra = ( 1750.00 * 1.10 ) * ((months * 30) + days ) / 360;

		if ( months == Calendar.DECEMBER && days > 15 )
			decemberExtra = 0.00; 
		// 'December Extra...' have been already emitted. 
		
		double noHolidyas = getNoHolidays(contractStart, getToday());
		double salaryDay = (1750 * 1.10 + 1750 * 1.10 / 6) * 12 / 365;
		
		Assert.assertEquals( decemberExtra + noHolidyas * salaryDay , settle.getTotalPayment(), DELTA);
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
		
		double noHolidays = getNoHolidays(contractStart, getToday());
		double daySalary = (1750 * 1.10 + 1750 * 1.10 / 6) * 12 / 365;
		
		Assert.assertEquals( 0.00 + noHolidays * daySalary , settle.getTotalPayment(), DELTA);
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
		
		double noHolidyas = getNoHolidays(contractStart, endDate);
		double daySalary = (1750 * 1.10 + 1750 * 1.10 / 6) * 12 / 365;
		
		Assert.assertEquals( 1750.00 *1.10 / 6 * 2.5 + noHolidyas * daySalary , settle.getTotalPayment(), DELTA);
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
		int days = Math.min(30,get(getToday(), Calendar.DAY_OF_MONTH ));
				
		
		
		double decemberExtra = ( 1750.00 * 1.10 ) * ((months * 30) + days ) / 360;
		int julyExtraMonths = (months >= Calendar.JULY ? months - Calendar.JULY : months );
		// Settle at 01/07, so we have two extras for July .
		if ( get(getToday(), Calendar.MONTH ) == 6 && get(getToday(), Calendar.DAY_OF_MONTH ) == 1)
			julyExtraMonths += 6;
		double julyExtra = ( 1750.00 * 1.10 ) * ((julyExtraMonths * 30) + days ) / 360 ;
		double bonusExtra = months == 11 ? ( 1750.00 * 1.10 ) * days  / 30 : 0.00;
		
		if ( months == Calendar.DECEMBER && days > 15 )
			decemberExtra = 0.00; 
		// 'December Extra...' have been already emitted. 
		
		double noHolidays = getNoHolidays(contractStart, getToday());
		double daySalary = (1750 * 1.10 + 1750 * 1.10 / 4) * 12 / 365;

		Assert.assertEquals( decemberExtra + julyExtra + bonusExtra + noHolidays * daySalary, settle.getTotalPayment(), DELTA);

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
		
		double noHolidyas = getNoHolidays(contractStart, endDate);
		double daySalary = (1750.00 * 1.10 + 1750.00 * 1.10 / 6) * 12 / 365;
		
		Assert.assertEquals( 1750.00 *1.10 / 6 * 2.5 + noHolidyas * daySalary , settle.getTotalPayment(), DELTA);
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
		
		double noHolidays = getNoHolidays(contractStart, endDate);
		double daySalary = (1750.00 * 1.10 + 1750.00 * 1.10 / 6) * 12 / 365;
		
		
		Assert.assertEquals( 1750.00 *1.10 / 12 * 4.50 + noHolidays * daySalary, settle.getTotalPayment(), DELTA);
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
		
		addPayment(aonContext, contract, getFirstDayOfMonth(getToday()) , null, "MANUAL PAYMENT", "-100.00", "_P", "0.00", PaymentType.CRA_0000);

		
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
		int days = Math.min(30,get(getToday(), Calendar.DAY_OF_MONTH ));
		double decemberExtra = ( 1750.00 * 1.10 ) * ((months * 30) + days ) / 360;
		int julyExtraMonths = (months >= 6 ? months -6 : months );
		// Settle at 01/07, so we have two extras for July .
		if ( get(getToday(), Calendar.MONTH ) == 6 && get(getToday(), Calendar.DAY_OF_MONTH ) == 1)
			julyExtraMonths += 6;
		double julyExtra = ( 1750.00 * 1.10 ) * ((julyExtraMonths  * 30) + days ) / 360 ;
		
		if ( months == Calendar.DECEMBER && days > 15 )
			decemberExtra = 0.00; 
		// 'December Extra...' have been already emitted.
		
		double noHolidays = getNoHolidays(contractStart, getToday());
		double daySalary = (1750 * 1.10 + 1750 * 1.10 / 6) * 12 / 365;

		Assert.assertEquals( decemberExtra + julyExtra + manualPayment + noHolidays * daySalary, settle.getTotalPayment(), DELTA);

	}

	@Test
	public void testSettleWithITII() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		
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
						"BASE_IRPF * 0.00/100" }, 
				null);
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

		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, add(getToday(), Calendar.MONTH, -1), getFirstDayOfMonth(getToday()) , 99.99);
		
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
		
		
		ISQLContractSalaryCalculatorContext salaryCtx = 
		getContractSalaryCalculatorContext(connection, 
				getFirstDayOfMonth(getToday()), 
				getToday(), 
				getToday(), 
				contract);
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>( jooqSalaryBuilder ).calculate(salaryCtx);
		jooqSalaryBuilder.execute();
		
		
		int cgcBases = 
		AON.getSalaryData
		(aonContext, props -> props.getContractProperty().eq(contract.getId()))
		.map(s -> s.getContextData().get(ContextVariable.CGC_BASE.getName()))
		.collect(Collectors.summingInt(list -> list.size() ));
		
		org.junit.Assert.assertEquals(2, cgcBases);

		Double cgcBase = 
		AON.getSalaryData
		(aonContext, props -> props.getContractProperty().eq(contract.getId()))
		.map(s -> { return s.getContextData().get(ContextVariable.CGC_BASE.getName()).stream()
				  .collect(Collectors.summingDouble(d -> Double.parseDouble(d.getExpression())));
				}
		)
		.collect(Collectors.summingDouble(d -> d))
		;
		
		double salaryDay = 
		cgcBase 
		/ get(getToday(), Calendar.DAY_OF_MONTH) 
		* get(getLastDayOfMonth(getToday()), Calendar.DAY_OF_MONTH)
		* 12 
		/ 365
		;
		
		addPayment(aonContext, contract, "100.00 * DIAS_TRABAJADOS / DIAS_MES");
		
		ISQLContractSalaryCalculatorContext settleCtx = 
				getSQLContractSettleContext(connection, contractStart, contract);
		
		settleCtx.getExpressionContext().eval("SALARIO_DIA", contractStart, getToday(), Double.class)
		.forEach(r -> Assert.assertEquals(salaryDay , r.getValue(), DELTA));
		
		cleanSalaries(aonContext, contract);
		
		settleCtx = 
				getSQLContractSettleContext(connection, contractStart, contract);
		
		double salaryDayII = 
		(1750.00 * 1.1 + 100.00 )* 0.5   * 12
		/ 365 
		;

		settleCtx.getExpressionContext().eval("SALARIO_DIA", contractStart, getToday(), Double.class)
		.forEach(r -> Assert.assertEquals(salaryDayII , r.getValue(), DELTA));

		Salary settle = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(settleCtx);
		

		Assert.assertTrue( settle.getTotalPayment() > 0.00);

	}
	// ------------------------------------------------------------------------

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
						put("BASE_CGC_MAX","(3642.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30))");						
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
	
	// ------------------------------------------------------------------------

	protected void setData(AONContext aonContext,
			ContractRecord contract, Date startDate, Date endDate,
			Map<String, String> datas) {
		AbstractSQLTestCase.addData(aonContext, contract, startDate, endDate, datas);
	}
	
	protected void addPayment(AONContext aonContext, ContractRecord contract, Date startDate, Date endDate, String description,
			String expression, String irpfExpression, String quoteExpression, PaymentType type) {
		addPayment(aonContext, contract, startDate, endDate, description, expression, irpfExpression, quoteExpression, type, SalaryType.SETTLE);
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
	
	protected void cleanSalaries (AONContext aonContext, ContractRecord contract ) {
		aonContext.getDslContext().delete(SALARY_DATA).execute();
		aonContext.getDslContext().delete(SALARY_COST).execute();
		aonContext.getDslContext().delete(SALARY_DEDUCTION).execute();
		aonContext.getDslContext().delete(SALARY_PAYMENT).execute();
		aonContext.getDslContext().delete(SALARY_BONUS).execute();
		aonContext.getDslContext().delete(SALARY).execute();
	}
	
}
