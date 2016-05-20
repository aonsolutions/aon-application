package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TOTAL_PAYMENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_DAYS;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.lang.String.format;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Test;

import com.code.aon.common.enumeration.Month;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.AgreementExtra;
import com.esferalia.aon.jooq.tables.records.AgreementExtraRecord;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.AgreementPaymentRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.event.AgreementLevelVetoableBeanListener;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;

public class SQLExtraTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.0000001;

	@Test
	public void testDuplicatePaymentsI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "P";
						this.month = Month.DECEMBER;
						this.start = "01/12";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, 
				new Extra() {
					{
						this.expression = "P";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, 
				});

		PaymentConceptRecord concept = addConcept(aonContext, "P");

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfMonth(getToday()) 
				,new HashMap<String, String>() {
				} 
				,new String[] {} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		addPayment(aonContext, contract, concept, String.format("SIN_DEFINIR * %s / %s", WORKED_DAYS , MONTH_DAYS ));
		addPayment(aonContext, contract, concept, String.format("1000 * %s / %s", WORKED_DAYS , MONTH_DAYS ));
		addPayment(aonContext, contract, concept, String.format("500 * %s / %s", WORKED_DAYS , MONTH_DAYS ));
		addPayment(aonContext, contract, concept, String.format("250 * %s / %s", WORKED_DAYS , MONTH_DAYS ));
		addPayment(aonContext, contract, concept, String.format("125 * %s / %s", WORKED_DAYS , MONTH_DAYS ));
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		

		Salary salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		Assert.assertEquals(String.format("%s",TOTAL_PAYMENT), 1875.00, salary.getTotalPayment(), DELTA);
		
		Assert.assertEquals(String.format("%s",ContextVariable.CGC_BASE), 1875.00 + (1875.00*2/12), salary.getCommonBase(), DELTA);
		

	}

	@Test
	public void testDuplicatePaymentsII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptP = addConcept(aonContext, "P");
		
		PaymentConceptRecord conceptX = addConcept(aonContext, "X");
		
		AgreementRecord agreement = newAgreement(aonContext);
		AgreementLevelCategoryRecord category = newAgreementCategory(aonContext, agreement);
		
		Date startDate = getFirstDayOfYear(getToday());
		addPayments(aonContext, agreement, startDate, new Payment []{
				new Payment(){
					{
						this.concept = conceptP.getId();
						this.expression =  format("SIN_DEFINIR * %s / %s", WORKED_DAYS , MONTH_DAYS );
					}
				}});
		
		addPayments(aonContext, agreement, startDate, new Payment []{
				new Payment(){
					{
						this.concept = conceptP.getId();
						this.expression =  "X";
					}
				}});
		addExtras(aonContext, agreement, startDate, new Extra[] { new Extra() {
			{
				this.expression = "P";
				this.month = Month.DECEMBER;
				this.start = "01/12";
				this.end = "31/12";
				this.issue = "15/12";
			}
		}, new Extra() {
			{
				this.expression = "P";
				this.month = Month.JULY;
				this.start = "01/07 -1";
				this.end = "30/06";
				this.issue = "01/07";
			}
		}});
		
		addPayments(aonContext, agreement, startDate, new Payment []{
				new Payment(){
					{
						this.concept = conceptX.getId();
						this.expression =  format("DESCONOCIDO * %s / %s", WORKED_DAYS , MONTH_DAYS );
					}
				}});
		addPayments(aonContext, agreement, startDate, new Payment []{
				new Payment(){
					{
						this.concept = conceptP.getId();
						this.expression =  format("1000.00 * %s / %s", WORKED_DAYS , MONTH_DAYS );
					}
				}});
		addPayments(aonContext, agreement, startDate, new Payment []{
				new Payment(){
					{
						this.concept = conceptP.getId();
						this.expression =  format("500.00 * %s / %s", WORKED_DAYS , MONTH_DAYS );
					}
				}});
		addPayments(aonContext, agreement, startDate, new Payment []{
				new Payment(){
					{
						this.concept = conceptP.getId();
						this.expression =  format("125.00 * %s / %s", WORKED_DAYS , MONTH_DAYS );
					}
				}});
		
		addPayments(aonContext, agreement, startDate, new Payment []{
				new Payment(){
					{
						this.concept = conceptX.getId();
						this.expression =  format("25.00 * %s / %s", WORKED_DAYS , MONTH_DAYS );
					}
				}});
		addPayments(aonContext, agreement, startDate, new Payment []{
				new Payment(){
					{
						this.concept = conceptX.getId();
						this.expression =  format("100.00 * %s / %s", WORKED_DAYS , MONTH_DAYS );
					}
				}});

		
		ContractRecord contract = newContract(aonContext, getFirstDayOfMonth(getToday()),
				new HashMap<String, String>() {}
				, new String[] {}
				,new String[] {}, 
				category);
		//@formatter:off
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();

		Salary salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		Assert.assertEquals(String.format("%s",TOTAL_PAYMENT), 1875.00, salary.getTotalPayment(), DELTA);
		
		Assert.assertEquals(String.format("%s",ContextVariable.CGC_BASE), 1875.00 + (1750.00*2/12), salary.getCommonBase(), DELTA);
		

	}

	@Test
	public void testIssueDateI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptP = addConcept(aonContext, "P");
		
		PaymentConceptRecord conceptS = addConcept(aonContext, "S");
		
		PaymentConceptRecord conceptA = addConcept(aonContext, "A");


		AgreementRecord agreement = newAgreement(aonContext);
		
		AgreementLevelCategoryRecord category = newAgreementCategory(aonContext, agreement);
		
		Date startYear = getFirstDayOfYear(getToday());
		
		addPayment(aonContext, agreement, startYear, new Payment(){
			{
				this.concept = conceptS.getId();
				this.expression =  format("1000.00 * %s / %s ", WORKED_DAYS, getPeriodVariable());
			}
		});
		
		addPayment(aonContext, agreement, startYear, new Payment(){
			{
				this.concept = conceptA.getId();
				this.expression =  format("100.00 * %s / %s ", WORKED_DAYS, getPeriodVariable());
			}
		});
		

		AgreementPaymentRecord payment = addPayment(aonContext, agreement, startYear, new Payment(){
					{
						this.concept = conceptP.getId();
						this.expression =  format("TRACE('%1$s = %%f ', %1$s);TRACE('%2$s = %%d\r\n', %2$s);S + A ",WORKED_DAYS, getPeriodVariable());
					}
				});
		
		
		AgreementExtraRecord agreementExtra = addExtra(aonContext, payment, startYear,
		new Extra() {
			{
				this.expression = "P";
				this.month = Month.JULY;
				this.start = "01/07 -1";
				this.end = "30/06";
				this.issue = "15/07";
			}
		});		
		
		ContractRecord contract = newContract(aonContext, 
				startYear,
				new HashMap<String, String>() {}
				,new String[] {}
				,new String[] {}, 
				category);
		//@formatter:off

		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, 6);
		calendar.set(DAY_OF_MONTH, 1);
		Date startDate = new Date(calendar.getTimeInMillis());
		
		calendar.add(YEAR, 1);
		calendar.set(MONTH, 6);
		calendar.set(DAY_OF_MONTH, 15);
		Date issueDate = new Date(calendar.getTimeInMillis());
		
		calendar.set(MONTH, 5);
		calendar.set(DAY_OF_MONTH, 30);
		Date endDate = new Date(calendar.getTimeInMillis());
		
		int year = calendar.get(Calendar.YEAR);

		ISQLContractSalaryCalculatorContext extraCtx = getExtraSalaryCalculatorContext(connection, contract, agreementExtra, year, issueDate);

		Salary extra = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(extraCtx);
		
		Assert.assertEquals(String.format("%s",TOTAL_PAYMENT), 1100.00, extra.getTotalPayment(), DELTA );
		
		for ( Date date = startDate; date.compareTo(endDate) <= 0; date = add(date, MONTH, 1) ) {
			ISQLContractSalaryCalculatorContext ctx = 
					getContractSalaryCalculatorContext(connection, date, getLastDayOfMonth(date), getLastDayOfMonth(date), contract);
			Salary salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
			Assert.assertEquals( 1100.00/12, salary.getExtraPayProration(), DELTA );
		}
		
	}
	
	@Test
	public void testIssueDateII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptP = addConcept(aonContext, "P");
		
		PaymentConceptRecord conceptS = addConcept(aonContext, "S");
		
		PaymentConceptRecord conceptA = addConcept(aonContext, "A");

		AgreementRecord agreement = newAgreement(aonContext);
		
		AgreementLevelCategoryRecord category = newAgreementCategory(aonContext, agreement);
		
		Date startYear = getFirstDayOfYear(getToday());
		
		addPayment(aonContext, agreement, startYear, new Payment(){
			{
				this.concept = conceptS.getId();
				this.expression =  format("1000.00 * %s / %s ", WORKED_DAYS, getPeriodVariable());
			}
		});
		
		addPayment(aonContext, agreement, startYear, new Payment(){
			{
				this.concept = conceptA.getId();
				this.expression =  format("100.00 * %s / %s ", WORKED_DAYS, getPeriodVariable());
			}
		});
		
		AgreementPaymentRecord payment = addPayment(aonContext, agreement, startYear, new Payment(){
					{
						this.concept = conceptP.getId();
						this.expression =  format("S + A");
					}
				});
		
		
		AgreementExtraRecord agreementExtra = addExtra(aonContext, payment, startYear,
		new Extra() {
			{
				this.expression = "P";
				this.month = Month.DECEMBER;
				this.start = "01/01";
				this.end = "31/12";
				this.issue = "15/12";
			}
		});
		
		
		ContractRecord contract = newContract(aonContext, 
				startYear,
				new HashMap<String, String>() {}
				,new String[] {}
				,new String[] {}, 
				category);
		//@formatter:off
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(YEAR, 1);
		calendar.set(MONTH, 0);
		calendar.set(DAY_OF_MONTH, 1);
		Date startDate = new Date(calendar.getTimeInMillis());
		
		calendar.add(YEAR, 1);
		calendar.set(MONTH, 11);
		calendar.set(DAY_OF_MONTH, 15);
		Date issueDate = new Date(calendar.getTimeInMillis());
		
		calendar.set(DAY_OF_MONTH, 31);
		Date endDate = new Date(calendar.getTimeInMillis());

		int year = calendar.get(Calendar.YEAR);

		ISQLContractSalaryCalculatorContext extraCtx = getExtraSalaryCalculatorContext(connection, contract, agreementExtra, year, issueDate);

		Salary extra = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(extraCtx);
		
		Assert.assertEquals(String.format("%s",TOTAL_PAYMENT), 1100.00, extra.getTotalPayment(), DELTA );
		
		for ( Date date = startDate; date.compareTo(endDate) <= 0; date = add(date, MONTH, 1) ) {
			ISQLContractSalaryCalculatorContext ctx = 
					getContractSalaryCalculatorContext(connection, date, getLastDayOfMonth(date), getLastDayOfMonth(date), contract);
			Salary salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
			Assert.assertEquals( 1100.00/12, salary.getExtraPayProration(), DELTA );
		}

	}
	
	@Test
	public void testIssueDateIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptP = addConcept(aonContext, "P");
		PaymentConceptRecord conceptS = addConcept(aonContext, "S");
		PaymentConceptRecord conceptA = addConcept(aonContext, "A");

		AgreementRecord agreement = newAgreement(aonContext);
		
		AgreementLevelCategoryRecord category = newAgreementCategory(aonContext, agreement);
		
		Date startYear = getFirstDayOfYear(getToday());
		
		addPayment(aonContext, agreement, startYear, new Payment(){
			{
				this.concept = conceptS.getId();
				this.expression =  format("1000.00 * %s / %s ", WORKED_DAYS, getPeriodVariable());
			}
		});
		
		addPayment(aonContext, agreement, startYear, new Payment(){
			{
				this.concept = conceptA.getId();
				this.expression =  format("100.00 * %s / %s ", WORKED_DAYS, getPeriodVariable());
			}
		});
		
		AgreementPaymentRecord payment = addPayment(aonContext, agreement, startYear, new Payment(){
					{
						this.concept = conceptP.getId();
						this.expression =  format("S + A");
					}
				});
		
		
		AgreementExtraRecord agreementExtra = addExtra(aonContext, payment, startYear,
		new Extra() {
			{
				this.expression = "P";
				this.month = Month.JULY;
				this.start = "01/07 -1";
				this.end = "30/06";
				this.issue = "15/07";
			}
		});
		
		
		ContractRecord contract = newContract(aonContext, 
				startYear,
				new HashMap<String, String>() {}
				,new String[] {}
				,new String[] {}, 
				category);
		//@formatter:off
		
		
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(YEAR, -1);
		calendar.set(MONTH, 6);
		calendar.set(DAY_OF_MONTH, 1);
		Date startDate = new Date(calendar.getTimeInMillis());
		
		calendar.add(YEAR, 1);
		calendar.set(MONTH, 6);
		calendar.set(DAY_OF_MONTH, 15);
		Date issueDate = new Date(calendar.getTimeInMillis());
		
		calendar.set(MONTH, 5);
		calendar.set(DAY_OF_MONTH, 30);
		Date endDate = new Date(calendar.getTimeInMillis());

		int year = calendar.get(Calendar.YEAR);

		ISQLContractSalaryCalculatorContext ctx = getExtraSalaryCalculatorContext(connection, contract, agreementExtra, year, issueDate);

		
		Salary salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		Assert.assertEquals(String.format("%s",TOTAL_PAYMENT), 1100.00 /2, salary.getTotalPayment(), DELTA );
		
		
	}
	
	@Test
	public void testIssueDateIV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptP = addConcept(aonContext, "P");
		
		PaymentConceptRecord conceptS = addConcept(aonContext, "S");
		
		PaymentConceptRecord conceptA = addConcept(aonContext, "A");

		AgreementRecord agreement = newAgreement(aonContext);
		
		AgreementLevelCategoryRecord category = newAgreementCategory(aonContext, agreement);
		
		Date startYear = getFirstDayOfYear(getToday());
		
		addPayment(aonContext, agreement, startYear, new Payment(){
			{
				this.concept = conceptS.getId();
				this.expression =  format("1000.00 * %s / %s ", WORKED_DAYS, getPeriodVariable());
			}
		});
		
		addPayment(aonContext, agreement, startYear, new Payment(){
			{
				this.concept = conceptA.getId();
				this.expression =  format("100.00 * %s / %s ", WORKED_DAYS, getPeriodVariable());
			}
		});
		
		AgreementPaymentRecord payment = addPayment(aonContext, agreement, startYear, new Payment(){
					{
						this.concept = conceptP.getId();
						this.expression =  format("S + A");
					}
				});
		
		
		AgreementExtraRecord agreementExtra = addExtra(aonContext, payment, startYear,
		new Extra() {
			{
				this.expression = "P";
				this.month = Month.DECEMBER;
				this.start = "01/01";
				this.end = "31/12";
				this.issue = "15/12";
			}
		});
		
		
		ContractRecord contract = newContract(aonContext, 
				add(startYear, MONTH, 9 ),
				new HashMap<String, String>() {}
				,new String[] {}
				,new String[] {}, 
				category);
		//@formatter:off
		
		
		

		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, 0);
		calendar.set(DAY_OF_MONTH, 1);
		Date startDate = new Date(calendar.getTimeInMillis());
		
		calendar.set(MONTH, 11);
		calendar.set(DAY_OF_MONTH, 15);
		Date issueDate = new Date(calendar.getTimeInMillis());
		
		calendar.set(DAY_OF_MONTH, 31);
		Date endDate = new Date(calendar.getTimeInMillis());

		int year = calendar.get(Calendar.YEAR);

		ISQLContractSalaryCalculatorContext ctx = getExtraSalaryCalculatorContext(connection, contract, agreementExtra, year, issueDate);

		Salary salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		Assert.assertEquals(String.format("%s",TOTAL_PAYMENT), 1100.00 * 3/12, salary.getTotalPayment(), DELTA );
		

	}

	@Test
	public void testIssueDateV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptP = addConcept(aonContext, "P");
		PaymentConceptRecord conceptS = addConcept(aonContext, "S");
		PaymentConceptRecord conceptA = addConcept(aonContext, "A");

		AgreementRecord agreement = newAgreement(aonContext);
		
		AgreementLevelCategoryRecord category = newAgreementCategory(aonContext, agreement);
		
		Date startYear = getFirstDayOfYear(getToday());
		
		addPayment(aonContext, agreement, startYear, new Payment(){
			{
				this.concept = conceptS.getId();
				this.expression =  format("1000.00 * %s / %s ", WORKED_DAYS, getPeriodVariable());
			}
		});
		
		addPayment(aonContext, agreement, startYear, new Payment(){
			{
				this.concept = conceptA.getId();
				this.expression =  format("100.00 * %s / %s ", WORKED_DAYS, getPeriodVariable());
			}
		});
		
		AgreementPaymentRecord payment = addPayment(aonContext, agreement, startYear, new Payment(){
					{
						this.concept = conceptP.getId();
						this.expression =  format("S + A");
					}
				});
		
		
		AgreementExtraRecord agreementExtra = addExtra(aonContext, payment, startYear,
		new Extra() {
			{
				this.expression = "P";
				this.month = Month.JULY;
				this.start = "01/01";
				this.end = "30/06";
				this.issue = "15/07";
			}
		});
		
		
		ContractRecord contract = newContract(aonContext, 
				startYear,
				new HashMap<String, String>() {}
				,new String[] {}
				,new String[] {}, 
				category);
		//@formatter:off
		
		
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, 0);
		calendar.set(DAY_OF_MONTH, 1);
		Date startDate = new Date(calendar.getTimeInMillis());
		
		calendar.set(MONTH, 6);
		calendar.set(DAY_OF_MONTH, 15);
		Date issueDate = new Date(calendar.getTimeInMillis());
		
		calendar.set(MONTH, 5);
		calendar.set(DAY_OF_MONTH, 30);
		Date endDate = new Date(calendar.getTimeInMillis());

		int year = calendar.get(Calendar.YEAR);

		ISQLContractSalaryCalculatorContext ctx = getExtraSalaryCalculatorContext(connection, contract, agreementExtra, year, issueDate);

		Salary salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		Assert.assertEquals(String.format("%s",TOTAL_PAYMENT), 1100.00, salary.getTotalPayment(), DELTA );
		

	}

	@Test
	public void testIssueDateVI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptP = addConcept(aonContext, "P");
		
		PaymentConceptRecord conceptS = addConcept(aonContext, "S");
		
		PaymentConceptRecord conceptA = addConcept(aonContext, "A");

		AgreementRecord agreement = newAgreement(aonContext);
		
		AgreementLevelCategoryRecord category = newAgreementCategory(aonContext, agreement);
		
		Date startYear = getFirstDayOfYear(getToday());
		
		addPayment(aonContext, agreement, startYear, new Payment(){
			{
				this.concept = conceptS.getId();
				this.expression =  format("1000.00 * %s / %s ", WORKED_DAYS, getPeriodVariable());
			}
		});
		
		addPayment(aonContext, agreement, startYear, new Payment(){
			{
				this.concept = conceptA.getId();
				this.expression =  format("100.00 * %s / %s ", WORKED_DAYS, getPeriodVariable());
			}
		});
		
		AgreementPaymentRecord payment = addPayment(aonContext, agreement, startYear, new Payment(){
					{
						this.concept = conceptP.getId();
						this.expression =  format("S + A");
					}
				});
		
		
		AgreementExtraRecord agreementExtra = addExtra(aonContext, payment, startYear,
		new Extra() {
			{
				this.expression = "P";
				this.month = Month.DECEMBER;
				this.start = "01/07";
				this.end = "31/12";
				this.issue = "15/12";
			}
		});
		
		
		ContractRecord contract = newContract(aonContext, 
				add(startYear, MONTH, 9 ),
				new HashMap<String, String>() {}
				,new String[] {}
				,new String[] {}, 
				category);
		//@formatter:off
		
		

		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, 6);
		calendar.set(DAY_OF_MONTH, 1);
		Date startDate = new Date(calendar.getTimeInMillis());
		
		calendar.set(MONTH, 11);
		calendar.set(DAY_OF_MONTH, 15);
		Date issueDate = new Date(calendar.getTimeInMillis());
		
		calendar.set(MONTH, 11);
		calendar.set(DAY_OF_MONTH, 31);
		Date endDate = new Date(calendar.getTimeInMillis());

		int year = calendar.get(Calendar.YEAR);

		ISQLContractSalaryCalculatorContext ctx = getExtraSalaryCalculatorContext(connection, contract, agreementExtra, year, issueDate);

		Salary salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		Assert.assertEquals(String.format("%s",TOTAL_PAYMENT), 1100.00 * 3/6, salary.getTotalPayment(), DELTA );
		

	}

	@Test
	public void testIssueDateVII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptP = addConcept(aonContext, "P");
		
		PaymentConceptRecord conceptS = addConcept(aonContext, "S");
		
		PaymentConceptRecord conceptA = addConcept(aonContext, "A");


		AgreementRecord agreement = newAgreement(aonContext);
		
		AgreementLevelCategoryRecord category = newAgreementCategory(aonContext, agreement);
		
		Date startYear = getFirstDayOfYear(getToday());
		
		addPayment(aonContext, agreement, startYear, new Payment(){
			{
				this.concept = conceptS.getId();
				this.expression =  format("1000.00 * %s / %s ", WORKED_DAYS, getPeriodVariable());
			}
		});
		
		addPayment(aonContext, agreement, startYear, new Payment(){
			{
				this.concept = conceptA.getId();
				this.expression =  format("100.00 * %s / %s ", WORKED_DAYS, getPeriodVariable());
			}
		});
		

		AgreementPaymentRecord payment = addPayment(aonContext, agreement, startYear, new Payment(){
					{
						this.concept = conceptP.getId();
						this.expression =  format("TRACE('%1$s = %%f ', %1$s);TRACE('%2$s = %%d\r\n', %2$s);S + A ",WORKED_DAYS, getPeriodVariable());
					}
				});
		
		
		AgreementExtraRecord agreementExtra = addExtra(aonContext, payment, startYear,
		new Extra() {
			{
				this.expression = "P";
				this.month = Month.JULY;
				this.start = "01/07 -1";
				this.end = "30/06";
				this.issue = "15/07";
			}
		});		
		
		ContractRecord contract = newContract(aonContext, 
				startYear,
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(),"200");
						put(ContextVariable.WEEK_HOURS.getName(),"20");
					}
				}
				,new String[] {}
				,new String[] {}, 
				category);
		//@formatter:off

		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, 6);
		calendar.set(DAY_OF_MONTH, 1);
		Date startDate = new Date(calendar.getTimeInMillis());
		
		calendar.add(YEAR, 1);
		calendar.set(MONTH, 6);
		calendar.set(DAY_OF_MONTH, 15);
		Date issueDate = new Date(calendar.getTimeInMillis());
		
		calendar.set(MONTH, 5);
		calendar.set(DAY_OF_MONTH, 30);
		Date endDate = new Date(calendar.getTimeInMillis());

		int year = calendar.get(Calendar.YEAR);

		ISQLContractSalaryCalculatorContext extraCtx = getExtraSalaryCalculatorContext(connection, contract, agreementExtra, year, issueDate);

		Salary extra = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(extraCtx);
		
		Assert.assertEquals(String.format("%s",TOTAL_PAYMENT), 1100.00 * 0.5, extra.getTotalPayment(), DELTA );
		
		for ( Date date = startDate; date.compareTo(endDate) <= 0; date = add(date, MONTH, 1) ) {
			ISQLContractSalaryCalculatorContext ctx = 
					getContractSalaryCalculatorContext(connection, date, getLastDayOfMonth(date), getLastDayOfMonth(date), contract);
			Salary salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
			Assert.assertEquals( (1100.00/12)*0.5, salary.getExtraPayProration(), DELTA );
		}
		
	}

	@Test
	public void testIssueDateVIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptP = addConcept(aonContext, "P");
		
		PaymentConceptRecord conceptS = addConcept(aonContext, "S");
		
		PaymentConceptRecord conceptA = addConcept(aonContext, "A");


		AgreementRecord agreement = newAgreement(aonContext);
		
		AgreementLevelCategoryRecord category = newAgreementCategory(aonContext, agreement);
		
		Date startYear = getFirstDayOfYear(getToday());
		
		addPayment(aonContext, agreement, startYear, new Payment(){
			{
				this.concept = conceptS.getId();
				this.expression =  format("1000.00 * %s / %s ", WORKED_DAYS, getPeriodVariable());
			}
		});
		
		addPayment(aonContext, agreement, startYear, new Payment(){
			{
				this.concept = conceptA.getId();
				this.expression =  format("100.00 * %s / %s ", WORKED_DAYS, getPeriodVariable());
			}
		});
		

		AgreementPaymentRecord payment = addPayment(aonContext, agreement, startYear, new Payment(){
					{
						this.concept = conceptP.getId();
						this.expression =  format("TRACE('%1$s = %%f ', %1$s);TRACE('%2$s = %%d\r\n', %2$s);S + A ",WORKED_DAYS, getPeriodVariable());
					}
				});
		
		
		AgreementExtraRecord agreementExtra = addExtra(aonContext, payment, startYear,
		new Extra() {
			{
				this.expression = "P";
				this.month = Month.JULY;
				this.start = "01/07 -1";
				this.end = "30/06";
				this.issue = "15/07";
			}
		});		
		
		ContractRecord contract = newContract(aonContext, 
				startYear,
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(),"200");
						put(ContextVariable.MONDAY_HOURS.getName(),"0");
						put(ContextVariable.TUESDAY_HOURS.getName(),"0");
						put(ContextVariable.WEDNESDAY_HOURS.getName(),"0");
						put(ContextVariable.THURSDAY_HOURS.getName(),"8");
						put(ContextVariable.FRIDAY_HOURS.getName(),"8");
					}
				}
				,new String[] {}
				,new String[] {}, 
				category);
		//@formatter:off

		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, 6);
		calendar.set(DAY_OF_MONTH, 1);
		Date startDate = new Date(calendar.getTimeInMillis());
		
		calendar.add(YEAR, 1);
		calendar.set(MONTH, 6);
		calendar.set(DAY_OF_MONTH, 15);
		Date issueDate = new Date(calendar.getTimeInMillis());
		
		calendar.set(MONTH, 5);
		calendar.set(DAY_OF_MONTH, 30);
		Date endDate = new Date(calendar.getTimeInMillis());
		
		int year = calendar.get(Calendar.YEAR);

		ISQLContractSalaryCalculatorContext extraCtx = getExtraSalaryCalculatorContext(connection, contract, agreementExtra, year, issueDate);
		
		Salary extra = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(extraCtx);
		
		Assert.assertEquals(String.format("%s",TOTAL_PAYMENT), 1100.00 * (16/40.00), extra.getTotalPayment(), DELTA );
		
		for ( Date date = startDate; date.compareTo(endDate) <= 0; date = add(date, MONTH, 1) ) {
			ISQLContractSalaryCalculatorContext ctx = 
					getContractSalaryCalculatorContext(connection, date, getLastDayOfMonth(date), getLastDayOfMonth(date), contract);
			Salary salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
			Assert.assertEquals( (1100.00/12)*(16/40.00), salary.getExtraPayProration(), DELTA );
		}
		
	}

	@Test
	public void testDuplicatePaymentsIX() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P";
						this.month = Month.DECEMBER;
						this.start = "01/12";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });

		PaymentConceptRecord concept = addConcept(aonContext, "P");

		ContractRecord contract = newContract(
				aonContext,
				getFirstDayOfYear(getToday()), 
				new HashMap<String, String>() {
				
				}, new String[] {}, new String[] {}, category);
		//@formatter:off
		
		addPayment(aonContext, contract, concept, String.format("REMOVE()"));
		addPayment(aonContext, contract, concept, String.format("SIN_DEFINIR * %s / %s", WORKED_DAYS , MONTH_DAYS ));
		addPayment(aonContext, contract, concept, String.format("1000 * %s / %s", WORKED_DAYS , MONTH_DAYS ));
		addPayment(aonContext, contract, concept, String.format("500 * %s / %s", WORKED_DAYS , MONTH_DAYS ));
		addPayment(aonContext, contract, concept, String.format("250 * %s / %s", WORKED_DAYS , MONTH_DAYS ));
		addPayment(aonContext, contract, concept, String.format("125 * %s / %s", WORKED_DAYS , MONTH_DAYS ));
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, 6);
		calendar.set(DAY_OF_MONTH, 1);
		Date startDate = new Date(calendar.getTimeInMillis());
		
		calendar.add(YEAR, 1);
		calendar.set(MONTH, 6);
		calendar.set(DAY_OF_MONTH, 30);
		Date issueDate = new Date(calendar.getTimeInMillis());
		
		calendar.set(MONTH, 5);
		calendar.set(DAY_OF_MONTH, 30);
		Date endDate = new Date(calendar.getTimeInMillis());
		
		int year = calendar.get(Calendar.YEAR);

		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord julyExtra = getExtra(aonContext, agreement.getId(), "01/07");

		ISQLContractSalaryCalculatorContext extraCtx = getExtraSalaryCalculatorContext(connection, contract, julyExtra, year, issueDate);

		Salary salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(extraCtx);
		
		Assert.assertEquals(String.format("%s",TOTAL_PAYMENT), 1875.00, salary.getTotalPayment(), DELTA);
		

	}

	//@Test
	public void testDuplicatePaymentsX() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P + S";
						this.month = Month.DECEMBER;
						this.start = "01/12";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P + S";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });

		PaymentConceptRecord concept = addConcept(aonContext, "P");

		ContractRecord contract = newContract(
				aonContext,
				getFirstDayOfYear(getToday()), 
				new HashMap<String, String>() {
				
				}, new String[] {}, new String[] {}, category);
		//@formatter:off
		
		addPayment(aonContext, contract, concept, String.format("1000 * %s / %s", WORKED_DAYS , MONTH_DAYS ));
		addPayment(aonContext, contract, concept, String.format("500 * %s / %s", WORKED_DAYS , MONTH_DAYS ));
		addPayment(aonContext, contract, concept, String.format("250 * %s / %s", WORKED_DAYS , MONTH_DAYS ));
		addPayment(aonContext, contract, concept, String.format("125 * %s / %s", WORKED_DAYS , MONTH_DAYS ));
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, 6);
		calendar.set(DAY_OF_MONTH, 1);
		Date startDate = new Date(calendar.getTimeInMillis());
		
		calendar.add(YEAR, 1);
		calendar.set(MONTH, 6);
		calendar.set(DAY_OF_MONTH, 30);
		Date issueDate = new Date(calendar.getTimeInMillis());
		
		calendar.set(MONTH, 5);
		calendar.set(DAY_OF_MONTH, 30);
		Date endDate = new Date(calendar.getTimeInMillis());

		int year = calendar.get(Calendar.YEAR);
		
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord julyExtra = getExtra(aonContext, agreement.getId(), "01/07");

		ISQLContractSalaryCalculatorContext extraCtx = getExtraSalaryCalculatorContext(connection, contract, julyExtra, year, issueDate);

		Salary salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(extraCtx);
		
		Assert.assertEquals(String.format("%s",TOTAL_PAYMENT), 1875.00, salary.getTotalPayment(), DELTA);
		

	}

	@Test
	public void testUndefPaymentsI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "P + K + F";
						this.month = Month.DECEMBER;
						this.start = "01/12";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, 
				new Extra() {
					{
						this.expression = "P + K + F";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, 
				});


		ContractRecord contract = newContract(aonContext,
				getFirstDayOfMonth(getToday()) 
				,new HashMap<String, String>() {
				} 
				,new String[] {} 
				,new String[] {} 
				,category);
		//@formatter:off
		PaymentConceptRecord conceptP = addConcept(aonContext, "P");
		addPayment(aonContext, contract, conceptP, String.format("1200.00 * %s / %s", WORKED_DAYS , MONTH_DAYS ));
		
		PaymentConceptRecord conceptK = addConcept(aonContext, "K");
		addPayment(aonContext, contract, conceptK, String.format("KMS * 0.19 "));

		PaymentConceptRecord conceptF = addConcept(aonContext, "F");
		addPayment(aonContext, contract, conceptF, String.format("FUNC()"));

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);

		Salary salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals(String.format("%s",TOTAL_PAYMENT), 1200.00, salary.getTotalPayment(), DELTA);
		Assert.assertEquals(String.format("%s",ContextVariable.CGC_BASE), 1200.00 + (1200.00*2/12), salary.getCommonBase(), DELTA);

	}

	// ------------------------------------------------------------------------
	
	protected ContextVariable getPeriodVariable() {
		return MONTH_DAYS;
	}
}
