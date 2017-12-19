package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMPENSATION_CAUSE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CONTRACT_COMPLETE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.OBJECTIVE;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.lang.String.format;
import static java.util.Calendar.DAY_OF_MONTH;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;

import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryData;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedResult;
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
		
		

		Assert.assertEquals( 12 * (2.00 + 2/12.00) * br, settle.getTotalPayment());
		Assert.assertEquals( 12 * ( 2 + 2/12.00) * br, settle.getTotalLiquid());
	}

	@Test
	public void testSettleContractEnd() throws ExpressionException, SQLException, SalaryException {

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
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_IRPF * 0.00/100" }, category);
		//@formatter:off
		
		addSSRegimeStuff(aonContext);
		
		ISQLContractSalaryCalculatorContext ctx = 
				getSQLContractSettleContext(connection, contractStart, contract);
		
		
		Salary settle = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double br = (1750.00 * 1.10) * (1 + 1.00 / 12 + 1.00 / 12) * 12 / 365; //AonDateUtils.getMax(getToday(), DAY_OF_YEAR);

		Assert.assertEquals( 12 * (2/12.00) * br, settle.getTotalPayment());
		Assert.assertEquals( 12 * (2/12.00) * br, settle.getTotalLiquid());
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
		
		ISQLContractSalaryCalculatorContext ctx = 
				getSQLContractSettleContext(connection, contractStart, contract);
		
		
		Salary settle = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double br = (1750.00 * 1.10) * (1 + 1.00 / 12 + 1.00 / 12) * 12 / 365; //AonDateUtils.getMax(getToday(), DAY_OF_YEAR);

		Assert.assertEquals( 20 * (2/12.00) * br, settle.getTotalPayment());
		
		System.out.println( (20 * (2/12.00) * br ) + " = " + settle.getTotalPayment() );
		
		Assert.assertEquals( 20 * (2/12.00) * br, settle.getTotalLiquid());
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
		
		setData(aonContext, contract, 
				add(getToday(), Calendar.DAY_OF_MONTH,1)
				, null
				, new HashMap<String, String>() {
			{
				put("DIAS_VACACIONES_NO_DISFRUTADOS", format("%d", 4));
			}
		});
		
		ISQLContractSalaryCalculatorContext ctx = 
				getSQLContractSettleContext(connection, contractStart, contract);
		
		Salary settle = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double br = (1750.00 * 1.10) * (1 + 1.00 / 12 + 1.00 / 12) * 12 / 365; 
		
		settle.getSalaryDatas().stream().forEach(d->System.out.println(d.getName() + " = "  + d.getExpression() ));
		settle.getSalaryPayments().stream().forEach(p->System.out.println(p.getExpression() + " = "  + p.getAmount() ));

		Assert.assertEquals( 20 * (2/12.00) * br + ( br * 4 ), settle.getTotalPayment());
		
		Assert.assertEquals( br * 4 , settle.getCommonBase());
		
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
				getSQLContractSettleContext(connection, contractStart, contract);
		
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
				getSQLContractSettleContext(connection, contractStart, contract);
		
		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		new ContractSalaryCalculator<ISalary>( jooqSalaryBuilder ).calculate(ctx);
		jooqSalaryBuilder.execute();
		
		Optional<com.esferalia.aon.occam.api.model.Salary> optional = AON.getSalaries(aonContext, props-> props.getContractProperty().eq(contract.getId()) ).
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
				new String[] { "1000.00 * DIAS_TRABAJADOS / DIAS_MES"}, 
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
		
		Assert.assertEquals(( 1000.00 * 12 / 365 * 10.00 ) , settle.getCommonBase());
		Assert.assertEquals(( 1000.00 * 12 / 365  * 10.00 ) * (4.70/100.00), settle.getTotalDeduction());
		
		
	}

	public  void addSSRegimeStuff(AONContext aonContext) {
		
		Date startDate = AonDateUtils.add(getFirstDayOfYear(getToday()), Calendar.YEAR, -2);
		
		addSSRegimeData(aonContext, 
				SSRegimeType.GENERAL, 
				startDate, 
				null, 
				new HashMap<String, String>() {
					{
						put("PORCENTAJE_CGC", format("%f", 4.70));
						put("DIAS_INDEMNIZACION_FIN", format("%d", 12));
						put("BASE_CGC_MAX","(3642.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30))");						
						put("BASE_CGC_MIN","(TIEMPO_COMPLETO ? 1056.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 6.37 * HORAS_NOMINA)");						
					}
				});
		
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
	
	
	
	
	
}
