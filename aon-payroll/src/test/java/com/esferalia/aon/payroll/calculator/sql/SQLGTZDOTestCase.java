package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.Calendar.DATE;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Test;

import com.code.aon.common.enumeration.Month;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;

public class SQLGTZDOTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000001;

	@Test
	public void testGtzdo4All() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:off
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.expression = "0.00";
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
		
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
				}, new String[] { 
						"( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
						"GTZDO(P_1 + P_2)" ,
						}
				, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" }, category);
		//@formatter:on
		
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT.getName());
		addPayment(aonContext, contract, prestIT, String.format("50 * 0.60 * %s_4_15",  COMMON_DISEASE_DAYS));
		addPayment(aonContext, contract, prestIT, String.format("50 * 0.60 * %s_16_20",  COMMON_DISEASE_DAYS));
		addPayment(aonContext, contract, prestIT, String.format("50 * 0.75 * %s_21",  COMMON_DISEASE_DAYS));
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = getToday();
		Date endIt = AonDateUtils.add(getToday(), Calendar.DATE, 100);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				endIt, 
				null);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double workedDays = get(startIt, DATE) -1 ;
		double monthDays = AonDateUtils.getMax(getToday(), DATE);
		double itDays = monthDays - workedDays;
		
		//@formatter:off
		Assert.assertEquals(
				(( 1500.00 + 250.00 ) * 1.10) * workedDays / monthDays 
				+ (1500.00 + 250.00 ) * itDays / monthDays, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testGtzdo4Partial() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:off
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.expression = "0.00";
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
		
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
				}, new String[] { 
						"( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
						"GTZDO(P_1 + P_2,1,3)" ,
						}
				, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" }, category);
		//@formatter:on
		
		
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT.getName());
		addPayment(aonContext, contract, prestIT, String.format("50 * 0.60 * %s_4_15",  COMMON_DISEASE_DAYS));
		addPayment(aonContext, contract, prestIT, String.format("50 * 0.65 * %s_16_20",  COMMON_DISEASE_DAYS));
		addPayment(aonContext, contract, prestIT, String.format("50 * 0.75 * %s_21",  COMMON_DISEASE_DAYS));
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = getToday();
		Date endIt = AonDateUtils.add(getToday(), Calendar.DATE, 100);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				endIt, 
				null);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double workedDays = get(startIt, DATE) -1 ;
		double monthDays = AonDateUtils.getMax(getToday(), DATE);
		double itDays = monthDays - workedDays;
		
		//@formatter:off
		Assert.assertEquals(
				(( 1500.00 + 250.00 ) * 1.10) * workedDays / monthDays 
				+ (1500.00 + 250.00 ) * Math.min(3,itDays) / monthDays
				+ (50 * 0.60 * Math.min(12,Math.max(0, itDays-3)))
				+ (50 * 0.65 * Math.min(5,Math.max(0, itDays-15)))
				+ (50 * 0.75 * Math.min(monthDays-20,Math.max(0, itDays-20)))
				, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testGtzdo4PartialII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:off
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.expression = "0.00";
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
		
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
				}, new String[] { 
						"( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
						"GTZDO(P_1 + P_2,4,30)" ,
						}
				, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" }, category);
		//@formatter:on
		
		
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT.getName());
		addPayment(aonContext, contract, prestIT, String.format("50 * 0.60 * %s_4_15",  COMMON_DISEASE_DAYS));
		addPayment(aonContext, contract, prestIT, String.format("50 * 0.65 * %s_16_20",  COMMON_DISEASE_DAYS));
		addPayment(aonContext, contract, prestIT, String.format("50 * 0.75 * %s_21",  COMMON_DISEASE_DAYS));
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = getToday();
		Date endIt = AonDateUtils.add(getToday(), Calendar.DATE, 100);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				endIt, 
				null);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double workedDays = get(startIt, DATE) -1 ;
		double monthDays = AonDateUtils.getMax(getToday(), DATE);
		double itDays = monthDays - workedDays;
		
		//@formatter:off
		Assert.assertEquals(
				(( 1500.00 + 250.00 ) * 1.10) * workedDays / monthDays 
				+ (1500.00 + 250.00 ) * Math.min(30,itDays-3) / monthDays
				, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testGtzdo4PartialIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:off
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.expression = "0.00";
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
		
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
				}, new String[] { 
						"( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
						"GTZDO(P_1,1,3)" ,
						"GTZDO(P_1 + P_2,4)" ,
						}
				, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" }, category);
		//@formatter:on
		
		
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT.getName());
		addPayment(aonContext, contract, prestIT, String.format("50 * 0.60 * %s_4_15",  COMMON_DISEASE_DAYS));
		addPayment(aonContext, contract, prestIT, String.format("50 * 0.65 * %s_16_20",  COMMON_DISEASE_DAYS));
		addPayment(aonContext, contract, prestIT, String.format("50 * 0.75 * %s_21",  COMMON_DISEASE_DAYS));
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = getToday();
		Date endIt = AonDateUtils.add(getToday(), Calendar.DATE, 100);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				endIt, 
				null);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double workedDays = get(startIt, DATE) -1 ;
		double monthDays = AonDateUtils.getMax(getToday(), DATE);
		double itDays = monthDays - workedDays;
		
		//@formatter:off
		Assert.assertEquals(
				(( 1500.00 + 250.00 ) * 1.10) * workedDays / monthDays 
					+ (1500.00 ) * Math.min(3,itDays) / monthDays
				+ (1500.00 + 250.00 ) * Math.max(0,itDays-3) / monthDays
				, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	
	@Test
	public void testGtzdo4PartialIV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:off
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.expression = "0.00";
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
		
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
				}, new String[] { 
						"( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
						"GTZDO(P_1,1,3)" ,
						"GTZDO(P_1 + P_2,4,10)" ,
						"GTZDO(P_1 * 0.9,11)" ,
						}
				, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" }, category);
		//@formatter:on
		
		
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT.getName());
		addPayment(aonContext, contract, prestIT, String.format("50 * 0.60 * %s_4_15",  COMMON_DISEASE_DAYS));
		addPayment(aonContext, contract, prestIT, String.format("50 * 0.65 * %s_16_20",  COMMON_DISEASE_DAYS));
		addPayment(aonContext, contract, prestIT, String.format("50 * 0.75 * %s_21",  COMMON_DISEASE_DAYS));
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = getToday();
		Date endIt = AonDateUtils.add(getToday(), Calendar.DATE, 100);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				endIt, 
				null);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double workedDays = get(startIt, DATE) -1 ;
		double monthDays = AonDateUtils.getMax(getToday(), DATE);
		double itDays = monthDays - workedDays;
		
		//@formatter:off
		Assert.assertEquals(
				(( 1500.00 + 250.00 ) * 1.10) * workedDays / monthDays 
					+ (1500.00 ) * Math.min(3,itDays) / monthDays
				+ (1500.00 + 250.00 ) * Math.min(7,Math.max(0,itDays-3)) / monthDays
				+ (1500.00 * 0.9) * Math.max(0,itDays-10) / monthDays
				, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	// ------------------------------------------------------------------------

	protected final void addIT(AONContext aonContext, ContractRecord contract,
			LeaveType type, Date startDate, Date endDate, Double regulatoryBase) {
		aonContext.getDslContext()
				.insertInto(CONTRACT_LEAVE)
				.set(CONTRACT_LEAVE.DOMAIN, contract.getDomain())
				.set(CONTRACT_LEAVE.CONTRACT, contract.getId())
				.set(CONTRACT_LEAVE.START_DATE, startDate)
				.set(CONTRACT_LEAVE.END_DATE, endDate)
				.set(CONTRACT_LEAVE.DAILY_REG_BASE, regulatoryBase)
				// .set(CONTRACT_LEAVE.DAILY_CGC_BASE, regulatoryBase)
				// .set(CONTRACT_LEAVE.DAILY_CGP_BASE, regulatoryBase)
				.set(CONTRACT_LEAVE.DISCHARGE_CAUSE, (byte) type.ordinal())
				.execute();

	}

	protected final void addPayment(AONContext aonContext,
			ContractRecord contract, String expression, String quoteExpression) {
		aonContext
				.getDslContext()
				.insertInto(CONTRACT_PAYMENT)
				.set(CONTRACT_PAYMENT.DOMAIN, contract.getDomain())
				.set(CONTRACT_PAYMENT.CONTRACT, contract.getId())
				.set(CONTRACT_PAYMENT.START_DATE, contract.getStartDate())
				.set(CONTRACT_PAYMENT.END_DATE, contract.getEndDate())
				.set(CONTRACT_PAYMENT.EXPRESSION, expression)
				.set(CONTRACT_PAYMENT.QUOTE_EXPRESSION, quoteExpression)
				.set(CONTRACT_PAYMENT.TYPE,
						(byte) PaymentType.CRA_0001.ordinal())
				.set(CONTRACT_PAYMENT.SALARY_TYPE,
						(byte) SalaryType.SALARY.ordinal()).execute();

	}

}
