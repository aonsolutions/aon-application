package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.GROSS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.IRPF_PERCENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.LIQUID;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TOTAL_PAYMENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_DAYS;
import static com.esferalia.aon.salary.enumeration.PaymentType.CRA_0001;
import static com.esferalia.aon.salary.enumeration.PaymentType.CRA_0004;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfYear;
import static java.lang.String.format;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Ignore;
import org.junit.Test;

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
import com.esferalia.aon.payroll.IrpfOutcome;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator.Listener;
import com.esferalia.aon.payroll.calculator.GenericContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.CompositeSalaryBuilder;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.ExpressionContext.RemovedExpressionVariable;
import com.esferalia.aon.salary.payment.IPayment;

import junit.framework.Assert;

public class SQLExtraTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.0000001;

	//@Test
	public void testSalaryMismatch() throws ExpressionException,
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
						this.issue = "1/07";
					}
				}, 
				});

		PaymentConceptRecord concept = addConcept(aonContext, "P");
		
		ContractRecord contract = newContract(aonContext,
				add(getToday(), Calendar.YEAR, -5) 
				,new HashMap<String, String>() {
				} 
				,new String[] {} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		addPayment(aonContext, contract, concept, String.format("1000 * %s / %s", WORKED_DAYS , MONTH_DAYS ));
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		Date startDate = add(getFirstDayOfYear(getToday()), YEAR,-1);
		for ( int i = 0 ; i < 24; i++ ) {
			Date endDate = getLastDayOfMonth(startDate);
			ISQLContractSalaryCalculatorContext ctx = 
			getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
			JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			new ContractSalaryCalculator<Salary>(jooqSalaryBuilder).calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(startDate, MONTH, 1);
		}
		
		

		addPayment(aonContext, contract, concept, String.format("500 * %s / %s", WORKED_DAYS , MONTH_DAYS ));
		
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, 6);
		calendar.set(DAY_OF_MONTH, 1);
		Date issueDate = new Date(calendar.getTimeInMillis());
		int year = calendar.get(Calendar.YEAR);

		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord julyExtra = getExtra(aonContext, agreement.getId(), "01/07");
		ISQLContractSalaryCalculatorContext extraCtx = getExtraSalaryCalculatorContext(connection, contract, julyExtra, year, issueDate);
		ContractSalaryCalculator<Salary> contractSalaryCalculator = new ContractSalaryCalculator<Salary>(new SalaryBuilder());
		contractSalaryCalculator.setListener(new Listener() {
		});
		Salary extra = contractSalaryCalculator.calculate(extraCtx);

		Assert.assertEquals(String.format("%s",TOTAL_PAYMENT), 1500.00, extra.getTotalPayment(), DELTA );
		
		
	}

	@Test
	public void testProrationFunction() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "1.1 * P";
						this.month = Month.DECEMBER;
						this.start = "01/07";
						this.end = "31/12";
						this.issue = "15/12";
						this.quoteExpression = "PRORRATEAR(_P)";
					}
				}, 
				new Extra() {
					{
						this.expression = "1.2 * P";
						this.month = Month.JULY;
						this.start = "01/01";
						this.end = "30/06";
						this.issue = "01/07";
						this.quoteExpression = "PRORRATEAR()";
					}
				}, 
				new Extra() {
					{
						this.expression = "0.9 * P";
						this.month = Month.MARCH;
						this.start = "01/01 -1";
						this.end = "31/12 -1";
						this.issue = "01/3";
						this.quoteExpression = "PRORRATEAR(_P)";
					}
				}
				});

		PaymentConceptRecord concept = addConcept(aonContext, "P");
		
		ContractRecord contract = newContract(aonContext,
				add(getToday(), Calendar.YEAR, -2) 
				,new HashMap<String, String>() {
				} 
				,new String[] {} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		addPayment(aonContext, contract, concept, 
				String.format("1000 * %s / %s", WORKED_DAYS , MONTH_DAYS )
				);
		// JANUARY
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		Salary salary = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals(((1.2 * 1000.00)/6 + (0.9 * 1000.00)/12), salary.getExtraPayProration(), DELTA);
		
		// JUNE
		startDate = add(startDate, MONTH, 5);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		salary = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals(((1.2 * 1000.00)/6 + (0.9 * 1000.00)/12), salary.getExtraPayProration(), DELTA);
		
		// JULY
		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		salary = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals(((1.1 * 1000.00)/6 + (0.9 * 1000.00)/12), salary.getExtraPayProration(), DELTA);

		// DECEMBER
		startDate = add(startDate, MONTH, 5);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		salary = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals(((1.1 * 1000.00)/6 + (0.9 * 1000.00)/12), salary.getExtraPayProration(), DELTA);
	}

	@Test
	public void testProrationFunctionII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "1.1 * P";
						this.month = Month.DECEMBER;
						this.start = "01/07";
						this.end = "31/12";
						this.issue = "15/12";
						this.quoteExpression = "PRORRATEAR(_P)";
					}
				}, 
				new Extra() {
					{
						this.expression = "1.2 * P";
						this.month = Month.JULY;
						this.start = "01/01";
						this.end = "30/06";
						this.issue = "01/07";
						this.quoteExpression = "PRORRATEAR()";
					}
				}, 
				new Extra() {
					{
						this.expression = "0.6 * P";
						this.month = Month.APRIL;
						this.start = "01/01";
						this.end = "31/03";
						this.issue = "15/04";
						this.quoteExpression = "PRORRATEAR(_P)";
					}
				},
				new Extra() {
					{
						this.expression = "0.7 * P";
						this.month = Month.JULY;
						this.start = "01/04";
						this.end = "30/06";
						this.issue = "15/7";
						this.quoteExpression = "PRORRATEAR()";
					}
				},
				new Extra() {
					{
						this.expression = "0.8 * P";
						this.month = Month.OCTOBER;
						this.start = "01/07";
						this.end = "30/09";
						this.issue = "15/10";
						this.quoteExpression = "PRORRATEAR(_P)";
					}
				},
				new Extra() {
					{
						this.expression = "0.9 * P";
						this.month = Month.JANUARY;
						this.start = "01/10";
						this.end = "31/12";
						this.issue = "15/1 +1";
						this.quoteExpression = "PRORRATEAR()";
					}
				}
				
				});

		PaymentConceptRecord concept = addConcept(aonContext, "P");
		
		ContractRecord contract = newContract(aonContext,
				add(getToday(), Calendar.YEAR, -2) 
				,new HashMap<String, String>() {
				} 
				,new String[] {} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		addPayment(aonContext, contract, concept, 
				String.format("1000 * %s / %s", WORKED_DAYS , MONTH_DAYS )
				);
		// JANUARY
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		Salary salary = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals(((1.2 * 1000.00)/6 + (0.6 * 1000.00)/3), salary.getExtraPayProration(), DELTA);
		
		// MARCH
		startDate = add(startDate, MONTH, 2);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		salary = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals(((1.2 * 1000.00)/6 + (0.6 * 1000.00)/3), salary.getExtraPayProration(), DELTA);
		
		// APRIL
		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		salary = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals(((1.2 * 1000.00)/6 + (0.7 * 1000.00)/3), salary.getExtraPayProration(), DELTA);

		// JUNE
		startDate = add(startDate, MONTH, 2);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		salary = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals(((1.2 * 1000.00)/6 + (0.7 * 1000.00)/3), salary.getExtraPayProration(), DELTA);

		// JULY
		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		salary = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals(((1.1 * 1000.00)/6 + (0.8 * 1000.00)/3), salary.getExtraPayProration(), DELTA);

		// SEPTEMBER
		startDate = add(startDate, MONTH, 2);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		salary = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals(((1.1 * 1000.00)/6 + (0.8 * 1000.00)/3), salary.getExtraPayProration(), DELTA);

		// OCTOBER
		startDate = add(startDate, MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		salary = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals(((1.1 * 1000.00)/6 + (0.9 * 1000.00)/3), salary.getExtraPayProration(), DELTA);

		// DECEMBER
		startDate = add(startDate, MONTH, 2);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		salary = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals(((1.1 * 1000.00)/6 + (0.9 * 1000.00)/3), salary.getExtraPayProration(), DELTA);
	}

	@Test
	public void testPaymentsVariable() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord salarioBaseConcept = addConcept(aonContext, "SALARIO_BASE", CRA_0001);
		PaymentConceptRecord antiguedadConcept = addConcept(aonContext, "ANTIGUEDAD", CRA_0001);
		PaymentConceptRecord plusSalarialConcept = addConcept(aonContext, "PLUS_SALARIAL", CRA_0001);
		

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "SALARIO_BASE";
						this.month = Month.DECEMBER;
						this.start = "01/07";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, 
				new Extra() {
					{
						this.expression = "SALARIO_BASE";
						this.month = Month.JULY;
						this.start = "01/01";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, 
				new Extra() {
					{
						this.expression = "SALARIO_BASE";
						this.month = Month.MARCH;
						this.start = "01/01 -1";
						this.end = "31/12 -1";
						this.issue = "01/3";
					}
				}
				},
				new Payment [] {
					new Payment() {
						{
							this.concept = salarioBaseConcept.getId();
							this.expression = String.format("1000.00 * %s / %s", WORKED_DAYS , MONTH_DAYS );
						}
					},
					new Payment() {
						{
							this.concept = salarioBaseConcept.getId();
							this.expression = String.format("SALARIO_DIARIO * %s ", WORKED_DAYS);
						}
					}
				});

		
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()) 
				,new HashMap<String, String>() {
				} 
				,new String[] {} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		addPayment(aonContext, contract, plusSalarialConcept, 
				String.format("300.00 * %s / %s", WORKED_DAYS , MONTH_DAYS )
				);
		addPayment(aonContext, contract, plusSalarialConcept, 
				String.format("200.00 * %s / %s", WORKED_DAYS , MONTH_DAYS )
				);
		addPayment(aonContext, contract, antiguedadConcept, 
				String.format("SALARIO_BASE * 0.1"  )
				);
		// JANUARY
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals( 1000.00 / 12 * 3, salary.getExtraPayProration(), DELTA);
		
		addPayment(aonContext, contract, startDate, endDate, "ESTANCIA", "500", "_P", "_P", PaymentType.CRA_0042, SalaryType.SALARY);
		
		salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals( 1000.00 / 12 * 3, salary.getExtraPayProration(), DELTA);

		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, 6);
		calendar.set(DAY_OF_MONTH, 1);
		Date issueDate = new Date(calendar.getTimeInMillis());
		
		int year = calendar.get(Calendar.YEAR);

		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord julyExtra = getExtra(aonContext, agreement.getId(), "01/07");

		Salary extra = new SmartContractSalaryCalculator<Salary>( 
				new SalaryBuilder())
				.calculate(getExtraSalaryCalculatorContext(connection, contract, julyExtra, year, issueDate));
		Assert.assertEquals( 1000.00, extra.getTotalPayment(), DELTA);
		
		calendar.set(MONTH, 11);
		calendar.set(DAY_OF_MONTH, 15);
		issueDate = new Date(calendar.getTimeInMillis());
		
		year = calendar.get(Calendar.YEAR);
		
		
		AgreementExtraRecord decemberExtra = getExtra(aonContext, agreement.getId(), "15/12");
		
		extra = new SmartContractSalaryCalculator<Salary>( 
				new SalaryBuilder())
				.calculate(getExtraSalaryCalculatorContext(connection, contract, decemberExtra, year, issueDate));
		Assert.assertEquals( 1000.00, extra.getTotalPayment(), DELTA);
	}

	@Test
	public void testPaymentsVariableIT() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord salarioBaseConcept = addConcept(aonContext, "SALARIO_BASE", CRA_0001);
		PaymentConceptRecord antiguedadConcept = addConcept(aonContext, "ANTIGUEDAD", CRA_0001);
		PaymentConceptRecord plusSalarialConcept = addConcept(aonContext, "PLUS_SALARIAL", CRA_0001);
		

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "SALARIO_BASE";
						this.month = Month.DECEMBER;
						this.start = "01/07";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, 
				new Extra() {
					{
						this.expression = "SALARIO_BASE";
						this.month = Month.JULY;
						this.start = "01/01";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, 
				new Extra() {
					{
						this.expression = "SALARIO_BASE";
						this.month = Month.MARCH;
						this.start = "01/01 -1";
						this.end = "31/12 -1";
						this.issue = "01/3";
					}
				}
				},
				new Payment [] {
					new Payment() {
						{
							this.concept = salarioBaseConcept.getId();
							this.expression = String.format("1000.00 * %s / %s", WORKED_DAYS , MONTH_DAYS );
						}
					},
					new Payment() {
						{
							this.concept = salarioBaseConcept.getId();
							this.expression = String.format("SALARIO_DIARIO * %s ", WORKED_DAYS);
						}
					}
				});

		
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()) 
				,new HashMap<String, String>() {
					{
						put("DIAS_MES", "30.00");	
					}
				} 
				,new String[] {} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		addPayment(aonContext, contract, plusSalarialConcept, 
				String.format("300.00 * %s / %s", WORKED_DAYS , MONTH_DAYS )
				);
		addPayment(aonContext, contract, plusSalarialConcept, 
				String.format("200.00 * %s / %s", WORKED_DAYS , MONTH_DAYS )
				);
		addPayment(aonContext, contract, antiguedadConcept, 
				String.format("SALARIO_BASE * 0.1"  )
				);
		
		
		// JANUARY
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, add(startDate, DAY_OF_MONTH, 15 ), endDate, null);

		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals( 1600.00 / 2, salary.getTotalPayment(), DELTA);
		Assert.assertEquals( 500.00 / 12 * 3, salary.getExtraPayProration(), DELTA);
		
		addPayment(aonContext, contract, startDate, endDate, "ESTANCIA", "500", "_P", "_P", PaymentType.CRA_0042, SalaryType.SALARY);
		
		salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals( 500.00 / 12 * 3, salary.getExtraPayProration(), DELTA);

		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, 6);
		calendar.set(DAY_OF_MONTH, 1);
		Date issueDate = new Date(calendar.getTimeInMillis());
		
		int year = calendar.get(Calendar.YEAR);

		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord julyExtra = getExtra(aonContext, agreement.getId(), "01/07");

		Salary extra = new SmartContractSalaryCalculator<Salary>( 
				new SalaryBuilder())
				.calculate(getExtraSalaryCalculatorContext(connection, contract, julyExtra, year, issueDate));
		Assert.assertEquals( 1000.00/6 * 5.5  , extra.getTotalPayment(), DELTA);
		
		calendar.set(MONTH, 11);
		calendar.set(DAY_OF_MONTH, 15);
		issueDate = new Date(calendar.getTimeInMillis());
		
		year = calendar.get(Calendar.YEAR);
		
		
		AgreementExtraRecord decemberExtra = getExtra(aonContext, agreement.getId(), "15/12");
		
		extra = new SmartContractSalaryCalculator<Salary>( 
				new SalaryBuilder())
				.calculate(getExtraSalaryCalculatorContext(connection, contract, decemberExtra, year, issueDate));
		Assert.assertEquals( 1000.00, extra.getTotalPayment(), DELTA);
	}

	@Test
	public void testFunctions() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "SALARIO_BASE";
						this.month = Month.JULY;
						this.start = "01/01";
						this.end = "30/06";
						this.issue = "15/07";
					}
				}
				});

		PaymentConceptRecord salarioBaseConcept = addConcept(aonContext, "SALARIO_BASE", CRA_0001);
		
		
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()) 
				,new HashMap<String, String>() {
				} 
				,new String[] {} 
				,new String[] {
				} 
				,category);
		//@formatter:off
		
		addSSRegimeDeduction(aonContext, SSRegimeType.GENERAL, contract.getStartDate(), DeductionType.IRPF, "isdef KAIXO ? KAIXO : HIDE()");
		
		addPayment(aonContext, contract, salarioBaseConcept, 
				String.format("1000.00 * %s / %s", WORKED_DAYS , MONTH_DAYS )
				);

		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, 6);
		calendar.set(DAY_OF_MONTH, 1);
		Date issueDate = new Date(calendar.getTimeInMillis());
		
		int year = calendar.get(Calendar.YEAR);

		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord julyExtra = getExtra(aonContext, agreement.getId(), "15/07");

		ISQLContractSalaryCalculatorContext ctx = 
		getExtraSalaryCalculatorContext(connection, contract, julyExtra, year, issueDate);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder());
		calculator.setListener(new GenericContractSalaryCalculator.Listener() {
		    
		   @Override
		   public void onCompileError(IContractDeduction deduction, String message) {
		       fail(message);
		       super.onCheckError(deduction, message);
		   } 
		});
		calculator.calculate(ctx);
		
	}

	@Test
	public void testAllVariable() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "MENSUALIDAD";
						this.month = Month.DECEMBER;
						this.start = "01/07";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, 
				new Extra() {
					{
						this.expression = "MENSUALIDAD";
						this.month = Month.JULY;
						this.start = "01/01";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, 
				new Extra() {
					{
						this.expression = "MENSUALIDAD";
						this.month = Month.MARCH;
						this.start = "01/01 -1";
						this.end = "31/12 -1";
						this.issue = "01/3";
					}
				}
				});

		PaymentConceptRecord salarioBaseConcept = addConcept(aonContext, "SALARIO_BASE", CRA_0001);
		PaymentConceptRecord antiguedadConcept = addConcept(aonContext, "ANTIGUEDAD", CRA_0001);
		PaymentConceptRecord plusSalarialConcept = addConcept(aonContext, "PLUS_SALARIAL", CRA_0001);
		
		
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()) 
				,new HashMap<String, String>() {
				} 
				,new String[] {} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		addPayment(aonContext, contract, salarioBaseConcept, 
				String.format("1000.00 * %s / %s", WORKED_DAYS , MONTH_DAYS )
				);
		addPayment(aonContext, contract, plusSalarialConcept, 
				String.format("300.00 * %s / %s", WORKED_DAYS , MONTH_DAYS )
				);
		addPayment(aonContext, contract, plusSalarialConcept, 
				String.format("200.00 * %s / %s", WORKED_DAYS , MONTH_DAYS )
				);
		addPayment(aonContext, contract, antiguedadConcept, 
				String.format("SALARIO_BASE * 0.1"  )
				);
		// JANUARY
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals( 1600.00 / 12 * 3, salary.getExtraPayProration(), DELTA);
		
		addPayment(aonContext, contract, startDate, endDate, "ESTANCIA", "500", "_P", "_P", PaymentType.CRA_0042, SalaryType.SALARY);
		
		salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals( 1600.00 / 12 * 3, salary.getExtraPayProration(), DELTA);

		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, 6);
		calendar.set(DAY_OF_MONTH, 1);
		Date issueDate = new Date(calendar.getTimeInMillis());
		
		int year = calendar.get(Calendar.YEAR);

		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord julyExtra = getExtra(aonContext, agreement.getId(), "01/07");

		Salary extra = new SmartContractSalaryCalculator<Salary>( 
				new SalaryBuilder())
				.calculate(getExtraSalaryCalculatorContext(connection, contract, julyExtra, year, issueDate));
		Assert.assertEquals( 1600.00, extra.getTotalPayment(), DELTA);
		
		calendar.set(MONTH, 11);
		calendar.set(DAY_OF_MONTH, 15);
		issueDate = new Date(calendar.getTimeInMillis());
		
		year = calendar.get(Calendar.YEAR);
		
		
		AgreementExtraRecord decemberExtra = getExtra(aonContext, agreement.getId(), "15/12");
		
		extra = new SmartContractSalaryCalculator<Salary>( 
				new SalaryBuilder())
				.calculate(getExtraSalaryCalculatorContext(connection, contract, decemberExtra, year, issueDate));
		Assert.assertEquals( 1600.00, extra.getTotalPayment(), DELTA);
	}

	@Test
	public void testAllVariableII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "MENSUALIDAD";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, 
				new Extra() {
					{
						this.expression = "MENSUALIDAD";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, 
				new Extra() {
					{
						this.expression = "MENSUALIDAD";
						this.month = Month.MARCH;
						this.start = "01/01 -1";
						this.end = "31/12 -1";
						this.issue = "01/3";
					}
				}
				});

		PaymentConceptRecord salarioBaseConcept = addConcept(aonContext, "SALARIO_BASE", CRA_0001);
		PaymentConceptRecord antiguedadConcept = addConcept(aonContext, "ANTIGUEDAD", CRA_0001);
		PaymentConceptRecord plusSalarialConcept = addConcept(aonContext, "PLUS_SALARIAL", CRA_0001);
		
		
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()) 
				,new HashMap<String, String>() {
				} 
				,new String[] {} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		addPayment(aonContext, contract, salarioBaseConcept, 
				String.format("1000.00 * %s / %s", WORKED_DAYS , MONTH_DAYS )
				);
		addPayment(aonContext, contract, plusSalarialConcept, 
				String.format("300.00 * %s / %s", WORKED_DAYS , MONTH_DAYS )
				);
		addPayment(aonContext, contract, plusSalarialConcept, 
				String.format("200.00 * %s / %s", WORKED_DAYS , MONTH_DAYS )
				);
		addPayment(aonContext, contract, antiguedadConcept, 
				String.format("SALARIO_BASE * 0.1"  )
				);
		// JANUARY
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals( 1600.00 / 12 * 3, salary.getExtraPayProration(), DELTA);
		
		addPayment(aonContext, contract, startDate, endDate, "ESTANCIA", "500", "_P", "_P", PaymentType.CRA_0042, SalaryType.SALARY);
		
		salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals( 1600.00 / 12 * 3, salary.getExtraPayProration(), DELTA);

		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, 6);
		calendar.set(DAY_OF_MONTH, 1);
		Date issueDate = new Date(calendar.getTimeInMillis());
		
		int year = calendar.get(Calendar.YEAR);

		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord julyExtra = getExtra(aonContext, agreement.getId(), "01/07");

		Salary extra = new SmartContractSalaryCalculator<Salary>( 
				new SalaryBuilder())
				.calculate(getExtraSalaryCalculatorContext(connection, contract, julyExtra, year, issueDate));
		Assert.assertEquals( 1600.00/2.00, extra.getTotalPayment(), DELTA);
		
	}

	@Test
	public void testAllVariableIT() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "MENSUALIDAD";
						this.month = Month.DECEMBER;
						this.start = "01/07";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, 
				new Extra() {
					{
						this.expression = "MENSUALIDAD";
						this.month = Month.JULY;
						this.start = "01/01";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, 
				new Extra() {
					{
						this.expression = "MENSUALIDAD";
						this.month = Month.MARCH;
						this.start = "01/01 -1";
						this.end = "31/12 -1";
						this.issue = "01/3";
					}
				}
				});

		PaymentConceptRecord salarioBaseConcept = addConcept(aonContext, "SALARIO_BASE", CRA_0001);
		PaymentConceptRecord antiguedadConcept = addConcept(aonContext, "ANTIGUEDAD", CRA_0001);
		PaymentConceptRecord plusSalarialConcept = addConcept(aonContext, "PLUS_SALARIAL", CRA_0001);
		
		
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()) 
				,new HashMap<String, String>() {
				} 
				,new String[] {} 
				,new String[] {} 
				,category);
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

		addPayment(aonContext, contract, salarioBaseConcept, 
				String.format("1000.00 * %s / %s", WORKED_DAYS , MONTH_DAYS )
				);
		addPayment(aonContext, contract, plusSalarialConcept, 
				String.format("300.00 * %s / %s", WORKED_DAYS , MONTH_DAYS )
				);
		addPayment(aonContext, contract, plusSalarialConcept, 
				String.format("200.00 * %s / %s", WORKED_DAYS , MONTH_DAYS )
				);
		addPayment(aonContext, contract, antiguedadConcept, 
				String.format("SALARIO_BASE * 0.1"  )
				);
		// JANUARY
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals( 1600.00 / 12 * 3, salary.getExtraPayProration(), DELTA);
		
		//addPayment(aonContext, contract, startDate, endDate, "ESTANCIA", "500", "_P", "_P", PaymentType.CRA_0042, SalaryType.SALARY);
		
		salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals( 1600.00 / 12 * 3, salary.getExtraPayProration(), DELTA);

		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, 6);
		calendar.set(DAY_OF_MONTH, 1);
		Date issueDate = new Date(calendar.getTimeInMillis());
		
		int year = calendar.get(Calendar.YEAR);

		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord julyExtra = getExtra(aonContext, agreement.getId(), "01/07");

		Salary extra = new SmartContractSalaryCalculator<Salary>( 
				new SalaryBuilder())
				.calculate(getExtraSalaryCalculatorContext(connection, contract, julyExtra, year, issueDate));
		Assert.assertEquals( 1600.00, extra.getTotalPayment(), DELTA);
		
		calendar.set(MONTH, 11);
		calendar.set(DAY_OF_MONTH, 15);
		issueDate = new Date(calendar.getTimeInMillis());
		
		year = calendar.get(Calendar.YEAR);
		
		
		AgreementExtraRecord decemberExtra = getExtra(aonContext, agreement.getId(), "15/12");
		
		extra = new SmartContractSalaryCalculator<Salary>( 
				new SalaryBuilder())
				.calculate(getExtraSalaryCalculatorContext(connection, contract, decemberExtra, year, issueDate));
		Assert.assertEquals( 1600.00, extra.getTotalPayment(), DELTA);
		
		
		calendar.set(MONTH,3);
		calendar.set(DAY_OF_MONTH,1);
		Date startIT = new Date(calendar.getTimeInMillis());
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startIT, null, null);
		
		startDate = getFirstDayOfMonth(startIT);
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals( 0.00, salary.getExtraPayProration(), DELTA);
		Assert.assertEquals( 1600.00 + 1600.00/12*3, salary.getCommonBase(), DELTA);

		calendar.set(MONTH, 6);
		calendar.set(DAY_OF_MONTH, 1);
		issueDate = new Date(calendar.getTimeInMillis());
		extra = new SmartContractSalaryCalculator<Salary>( 
				new SalaryBuilder())
				.calculate(getExtraSalaryCalculatorContext(connection, contract, julyExtra, year, issueDate));
		
		Assert.assertEquals( 1600.00/2.00, extra.getTotalPayment(), DELTA);

		calendar.set(MONTH, 11);
		calendar.set(DAY_OF_MONTH, 15);
		issueDate = new Date(calendar.getTimeInMillis());
		extra = new SmartContractSalaryCalculator<Salary>( 
				new SalaryBuilder())
				.calculate(getExtraSalaryCalculatorContext(connection, contract, decemberExtra, year, issueDate));
		
		Assert.assertEquals( 0.00, extra.getTotalPayment(), DELTA);
		
		
	}

	@Test
	public void testAllVariableProrr() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord salarioBaseConcept = addConcept(aonContext, "SALARIO_BASE", CRA_0001);
		PaymentConceptRecord antiguedadConcept = addConcept(aonContext, "ANTIGUEDAD", CRA_0001);
		PaymentConceptRecord plusSalarialConcept = addConcept(aonContext, "PLUS_SALARIAL", CRA_0001);
		
		
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()) 
				,new HashMap<String, String>() {
				} 
				,new String[] {} 
				,new String[] {} 
				,null);
		//@formatter:off
		
		addPayment(aonContext, contract, salarioBaseConcept, 
				String.format("1000.00 * %s / %s", WORKED_DAYS , MONTH_DAYS )
				);
		addPayment(aonContext, contract, plusSalarialConcept, 
				String.format("300.00 * %s / %s", WORKED_DAYS , MONTH_DAYS )
				);
		addPayment(aonContext, contract, plusSalarialConcept, 
				String.format("200.00 * %s / %s", WORKED_DAYS , MONTH_DAYS )
				);
		addPayment(aonContext, contract, antiguedadConcept, 
				String.format("SALARIO_BASE * 0.1"  )
				);
		addPayment(aonContext, contract, "PAGA EXTRA NAVIDAD", 
				"MENSUALIDAD", "_P", "_P", CRA_0004, SalaryType.SALARY);
		addPayment(aonContext, contract, "PAGA EXTRA JULIO", 
				"MENSUALIDAD", "_P", "_P", CRA_0004, SalaryType.SALARY);
		addPayment(aonContext, contract, "PAGA EXTRA BENFICIOS", 
				"MENSUALIDAD", "_P", "_P", CRA_0004, SalaryType.SALARY);
		// JANUARY
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals( 1600.00 + 1600.00 / 12 * 3, salary.getTotalPayment(), DELTA);
		Assert.assertEquals( 1600.00 + 1600.00 / 12 * 3, salary.getCommonBase(), DELTA);
		
		addPayment(aonContext, contract, startDate, endDate, "ESTANCIA", "500", "_P", "_P", PaymentType.CRA_0042, SalaryType.SALARY);
		
		salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals( 1600.00 + 1600.00 / 12 * 3 + 500.00, salary.getTotalPayment(), DELTA);
		Assert.assertEquals( 1600.00 + 1600.00 / 12 * 3 + 500.00, salary.getCommonBase(), DELTA);

	}

	@Test
	public void testPaymentsVariableProrr() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord salarioBaseConcept = addConcept(aonContext, "SALARIO_BASE", CRA_0001);
		PaymentConceptRecord antiguedadConcept = addConcept(aonContext, "ANTIGUEDAD", CRA_0001);
		PaymentConceptRecord plusSalarialConcept = addConcept(aonContext, "PLUS_SALARIAL", CRA_0001);
		
		
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()) 
				,new HashMap<String, String>() {
				} 
				,new String[] {} 
				,new String[] {} 
				,null);
		//@formatter:off
		
		addPayment(aonContext, contract, "PAGA EXTRA NAVIDAD", 
				"SALARIO_BASE", "_P", "_P", CRA_0004, SalaryType.SALARY);
		addPayment(aonContext, contract, "PAGA EXTRA JULIO", 
				"SALARIO_BASE", "_P", "_P", CRA_0004, SalaryType.SALARY);
		addPayment(aonContext, contract, "PAGA EXTRA BENFICIOS", 
				"SALARIO_BASE", "_P", "_P", CRA_0004, SalaryType.SALARY);
		addPayment(aonContext, contract, plusSalarialConcept, 
				String.format("300.00 * %s / %s", WORKED_DAYS , MONTH_DAYS )
				);
		addPayment(aonContext, contract, plusSalarialConcept, 
				String.format("200.00 * %s / %s", WORKED_DAYS , MONTH_DAYS )
				);
		addPayment(aonContext, contract, antiguedadConcept, 
				String.format("SALARIO_BASE * 0.1"  )
				);
		addPayment(aonContext, contract, salarioBaseConcept, 
				String.format("1000.00 * %s / %s", WORKED_DAYS , MONTH_DAYS )
				);
		addPayment(aonContext, contract, salarioBaseConcept, 
				String.format("SALARIO_DIARIO * %s", WORKED_DAYS )
				);
		// JANUARY
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals( 1600.00 + 1000.00 / 12 * 3, salary.getTotalPayment(), DELTA);
		Assert.assertEquals( 1600.00 + 1000.00 / 12 * 3, salary.getCommonBase(), DELTA);
		
		addPayment(aonContext, contract, startDate, endDate, "ESTANCIA", "500", "_P", "_P", PaymentType.CRA_0042, SalaryType.SALARY);
		
		salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals( 1600.00 + 1000.00 / 12 * 3 + 500.00, salary.getTotalPayment(), DELTA);
		Assert.assertEquals( 1600.00 + 1000.00 / 12 * 3 + 500.00, salary.getCommonBase(), DELTA);

	}

	@Test
	public void testPaymentsVariableProrrIT() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord salarioBaseConcept = addConcept(aonContext, "SALARIO_BASE", CRA_0001);
		PaymentConceptRecord antiguedadConcept = addConcept(aonContext, "ANTIGUEDAD", CRA_0001);
		PaymentConceptRecord plusSalarialConcept = addConcept(aonContext, "PLUS_SALARIAL", CRA_0001);
		
		
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()) 
				,new HashMap<String, String>() {
					{
						put("DIAS_MES", "30.0");
					}
				} 
				,new String[] {} 
				,new String[] {} 
				,null);
		//@formatter:off
		
		addPayment(aonContext, contract, "PAGA EXTRA NAVIDAD", 
				"SALARIO_BASE", "_P", "_P", CRA_0004, SalaryType.SALARY);
		addPayment(aonContext, contract, "PAGA EXTRA JULIO", 
				"SALARIO_BASE", "_P", "_P", CRA_0004, SalaryType.SALARY);
		addPayment(aonContext, contract, "PAGA EXTRA BENFICIOS", 
				"SALARIO_BASE", "_P", "_P", CRA_0004, SalaryType.SALARY);
		addPayment(aonContext, contract, plusSalarialConcept, 
				String.format("300.00 * %s / %s", WORKED_DAYS , MONTH_DAYS )
				);
		addPayment(aonContext, contract, plusSalarialConcept, 
				String.format("200.00 * %s / %s", WORKED_DAYS , MONTH_DAYS )
				);
		addPayment(aonContext, contract, antiguedadConcept, 
				String.format("SALARIO_BASE * 0.1"  )
				);
		addPayment(aonContext, contract, salarioBaseConcept, 
				String.format("1000.00 * %s / %s", WORKED_DAYS , MONTH_DAYS )
				);
		addPayment(aonContext, contract, salarioBaseConcept, 
				String.format("SALARIO_DIARIO * %s", WORKED_DAYS )
				);
		// JANUARY
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, add(startDate, DAY_OF_MONTH, 15), endDate, null);
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals( 1600.00/2.00 + 1000.00/2.00 / 12 * 3, salary.getTotalPayment(), DELTA);
		Assert.assertEquals( 1600.00/2.00 + 1000.00/2.00 / 12 * 3, salary.getCommonBase(), DELTA);
		
		addPayment(aonContext, contract, startDate, endDate, "ESTANCIA", "500", "_P", "_P", PaymentType.CRA_0042, SalaryType.SALARY);
		
		salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals( 1600.00/2.00 + 1000.00/2.00 / 12 * 3 + 500.00, salary.getTotalPayment(), DELTA);
		Assert.assertEquals( 1600.00/2.00 + 1000.00/2.00 / 12 * 3 + 500.00, salary.getCommonBase(), DELTA);

	}

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

	@Test
	public void testUndefPaymentsII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "1200.00 * DIAS_TRABAJADOS /DIAS_MES + K";
						this.month = Month.DECEMBER;
						this.start = "01/12";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, 
				new Extra() {
					{
						this.expression = "1200.00 * DIAS_TRABAJADOS /DIAS_MES  + K";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, 
				}
				);


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
		// Payment with same name that it's var :-(
		PaymentConceptRecord conceptK = addConcept(aonContext, "K");
		addPayment(aonContext, contract, conceptK, String.format("K * 5"));
		//addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "K", "100");
		

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
	
	@Test
	public void testUndefPaymentsIII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "1200.00 * DIAS_TRABAJADOS /DIAS_MES + PLUS_SALARIAL";
						this.month = Month.DECEMBER;
						this.start = "01/12";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, 
				new Extra() {
					{
						this.expression = "1200.00 * DIAS_TRABAJADOS /DIAS_MES  + PLUS_SALARIAL";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, 
				}
				);


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
		// Payment with same name that it's var :-(
		

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
	

	@Test
	public void testAutoProrrationI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		
		addBaseCgcMin(aonContext);
		

		ContractRecord contract = newContract(
				aonContext,
				getFirstDayOfYear(getToday()), 
				new HashMap<String, String>() {
					{
						put("TC2", "\"100\"");
						put("GRUPO_COTIZACION", "\"04\"");
					}
				}, 
				new String[] {},
				new String[] {}, 
				null
				);
		//@formatter:off
		
		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		addPayment(aonContext, contract, conceptSalarioBase, String.format("1000 * %s / %s", WORKED_DAYS , MONTH_DAYS ));
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");
		addPayment(aonContext, contract, conceptPlusSalarial, String.format("500 * %s / %s", WORKED_DAYS , MONTH_DAYS ));
		addPayment(aonContext, contract, conceptPlusSalarial, String.format("250 * %s / %s", WORKED_DAYS , MONTH_DAYS ));
		PaymentConceptRecord conceptAntiguedad = addConcept(aonContext, "ANTIGUEDAD");
		addPayment(aonContext, contract, conceptAntiguedad, String.format("125 * %s / %s", WORKED_DAYS , MONTH_DAYS ));

		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA");
		addPayment(aonContext, contract, conceptPagaExtra, "SALARIO_BASE + PLUS_SALARIAL + ANTIGUEDAD", "_P/12", PaymentType.CRA_0004);
		addPayment(aonContext, contract, conceptPagaExtra, "SALARIO_BASE + PLUS_SALARIAL + ANTIGUEDAD", "_P/12", PaymentType.CRA_0004);
		
		
		Date startDate =getFirstDayOfMonth(getToday());
		Date endDate =getLastDayOfMonth(startDate);
		Date issueDate = endDate;
		
		Salary salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, issueDate, contract))
		;
		
		Assert.assertEquals((1000.00 + 500.00 + 250.00 + 125.00) * ( 1.00 + 1.00/6.00 ), salary.getTotalPayment());
		Assert.assertEquals((1000.00 + 500.00 + 250.00 + 125.00) * ( 1.00 + 1.00/6.00 ), salary.getIrpfBase());
		Assert.assertEquals((1000.00 + 500.00 + 250.00 + 125.00) * ( 1.00 + 1.00/6.00 ), salary.getCommonBase());
	}

	@Test
	public void testAutoProrrationII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		
		addBaseCgcMin(aonContext);
		

		ContractRecord contract = newContract(
				aonContext,
				getFirstDayOfYear(getToday()), 
				new HashMap<String, String>() {
					{
						put("TC2", "\"100\"");
						put("GRUPO_COTIZACION", "\"04\"");
					}
				}, 
				new String[] {},
				new String[] {}, 
				null
				);
		//@formatter:off
		
		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		addPayment(aonContext, contract, conceptSalarioBase, String.format("1000 * %s / %s", WORKED_DAYS , MONTH_DAYS ));
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");
		addPayment(aonContext, contract, conceptPlusSalarial, String.format("500 * %s / %s", WORKED_DAYS , MONTH_DAYS ));
		addPayment(aonContext, contract, conceptPlusSalarial, String.format("250 * %s / %s", WORKED_DAYS , MONTH_DAYS ));
		PaymentConceptRecord conceptAntiguedad = addConcept(aonContext, "ANTIGUEDAD");
		addPayment(aonContext, contract, conceptAntiguedad, String.format("125 * %s / %s", WORKED_DAYS , MONTH_DAYS ));

		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA");
		addPayment(aonContext, contract, conceptPagaExtra, "(1000.00 + 500.00 + 250.00 + 125.00)", "_P/12", PaymentType.CRA_0004);
		addPayment(aonContext, contract, conceptPagaExtra, "(1000.00 + 500.00 + 250.00 + 125.00)", "_P/12", PaymentType.CRA_0004);
		
		
		Date startDate =getFirstDayOfMonth(getToday());
		Date endDate =getLastDayOfMonth(startDate);
		Date issueDate = endDate;
		
		Salary salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, issueDate, contract))
		;
		
		Assert.assertEquals((1000.00 + 500.00 + 250.00 + 125.00) * ( 1.00 + 1.00/6.00 ), salary.getTotalPayment());
		Assert.assertEquals((1000.00 + 500.00 + 250.00 + 125.00) * ( 1.00 + 1.00/6.00 ), salary.getIrpfBase());
		Assert.assertEquals((1000.00 + 500.00 + 250.00 + 125.00) * ( 1.00 + 1.00/6.00 ), salary.getCommonBase());
	}

	@Test
	public void testAutoProrrationIII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		
		addBaseCgcMin(aonContext);
		

		ContractRecord contract = newContract(
				aonContext,
				getFirstDayOfYear(getToday()), 
				new HashMap<String, String>() {
					{
						put("TC2", "\"100\"");
						put("GRUPO_COTIZACION", "\"04\"");
						put("BASE_CGC_MIN", "1108.33");
					}
				}, 
				new String[] {},
				new String[] {}, 
				null
				);
		//@formatter:off
		
		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		addPayment(aonContext, contract, conceptSalarioBase, String.format("950 * %s / %s", WORKED_DAYS , MONTH_DAYS ));
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");
		addPayment(aonContext, contract, conceptPlusSalarial, String.format("0.00 * %s / %s", WORKED_DAYS , MONTH_DAYS ));
		addPayment(aonContext, contract, conceptPlusSalarial, String.format("0.00 * %s / %s", WORKED_DAYS , MONTH_DAYS ));
		PaymentConceptRecord conceptAntiguedad = addConcept(aonContext, "ANTIGUEDAD");
		addPayment(aonContext, contract, conceptAntiguedad, String.format("0.00 * %s / %s", WORKED_DAYS , MONTH_DAYS ));

		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "CUALESQUIERA");
		addPayment(aonContext, contract, conceptPagaExtra, "SALARIO_BASE + PLUS_SALARIAL + ANTIGUEDAD", "_P", PaymentType.CRA_0004);
		addPayment(aonContext, contract, conceptPagaExtra, "SALARIO_BASE + PLUS_SALARIAL + ANTIGUEDAD", "_P", PaymentType.CRA_0004);
		
		
		Date startDate =getFirstDayOfMonth(getToday());
		Date endDate =getLastDayOfMonth(startDate);
		Date issueDate = endDate;
		
		Salary salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, issueDate, contract))
		;
		
		Assert.assertEquals((950.00) * ( 1.00 + 1.00/6.00 ), salary.getTotalPayment());
		Assert.assertEquals((950.00) * ( 1.00 + 1.00/6.00 ), salary.getIrpfBase());
		Assert.assertEquals((950.00) * ( 1.00 + 1.00/6.00 ), salary.getCommonBase());
	}

	@Test
	public void testAutoProrrationIV() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		
		addBaseCgcMin(aonContext);
		

		ContractRecord contract = newContract(
				aonContext,
				getFirstDayOfYear(getToday()), 
				new HashMap<String, String>() {
					{
						//put("TC2", "\"100\"");
						put("GRUPO_COTIZACION", "\"04\"");
						put("TIEMPO_COMPLETO", "FALSO()");
						put("COEFICIENTE_PARCIALIDAD", "0.50");
						put("BASE_CGC_MIN", "[\"04\":(1050.00 * COEFICIENTE_PARCIALIDAD)][GRUPO_COTIZACION]");
					}
				}, 
				new String[] {},
				new String[] {}, 
				null
				);
		//@formatter:off
		
		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		addPayment(aonContext, contract, conceptSalarioBase, String.format("900 * %s / %s", WORKED_DAYS , MONTH_DAYS ));
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");
		addPayment(aonContext, contract, conceptPlusSalarial, String.format("0.00 * %s / %s", WORKED_DAYS , MONTH_DAYS ));
		addPayment(aonContext, contract, conceptPlusSalarial, String.format("0.00 * %s / %s", WORKED_DAYS , MONTH_DAYS ));
		PaymentConceptRecord conceptAntiguedad = addConcept(aonContext, "ANTIGUEDAD");
		addPayment(aonContext, contract, conceptAntiguedad, String.format("0.00 * %s / %s", WORKED_DAYS , MONTH_DAYS ));

		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "CUALESQUIERA");
		addPayment(aonContext, contract, conceptPagaExtra, "SALARIO_BASE + PLUS_SALARIAL + ANTIGUEDAD", "_P", PaymentType.CRA_0004);
		addPayment(aonContext, contract, conceptPagaExtra, "SALARIO_BASE + PLUS_SALARIAL + ANTIGUEDAD", "_P", PaymentType.CRA_0004);
		
		
		Date startDate =getFirstDayOfMonth(getToday());
		Date endDate =getLastDayOfMonth(startDate);
		Date issueDate = endDate;
		
		Salary salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, issueDate, contract))
		;
		
		Assert.assertEquals((900.00/2.00) * ( 1.00 + 1.00/6.00 ), salary.getTotalPayment());
		Assert.assertEquals((900.00/2.00) * ( 1.00 + 1.00/6.00 ), salary.getIrpfBase());
		Assert.assertEquals((900.00/2.00) * ( 1.00 + 1.00/6.00 ), salary.getCommonBase(),DELTA);
	}

	@Test
	@Ignore("But...not yet")
	public void testIRPF() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "P";
						this.month = Month.MARCH;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "31/03";
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

		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.JUNE);
		calendar.set(DAY_OF_MONTH, 1);
		Date contractDate = new Date(calendar.getTimeInMillis());

		ContractRecord contract = newContract(aonContext,
				contractDate
				,new HashMap<String, String>() {
					{
						put("PORCENTAJE_IRPF", "6.6");
					}
				} 
				,new String[] {} 
				,new String[] {
					"BASE_IRPF * PORCENTAJE_IRPF/100" 
				} 
				,category);
		//@formatter:off
		PaymentConceptRecord conceptP = addConcept(aonContext, "P");
		addPayment(aonContext, contract, conceptP, String.format("1200.00 * %s / %s", WORKED_DAYS , MONTH_DAYS ));

		Date startDate = contractDate;
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);

		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals(String.format("%s",TOTAL_PAYMENT), 1200.00, salary.getTotalPayment(), DELTA);
		Assert.assertEquals(String.format("%s",CGC_BASE), 1200.00 + (1200.00*2/12), salary.getCommonBase(), DELTA);

		Assert.assertEquals(String.format("%s",IRPF_PERCENT), salary.getIrpfBase() * 6.6/100.00, salary.getTotalIrpf(), DELTA);
		
		calendar.set(MONTH, Calendar.MARCH);
		calendar.set(DAY_OF_MONTH, 31);
		Date issueDate = new Date(calendar.getTimeInMillis());
		
		int year = calendar.get(Calendar.YEAR);
		
		addData(aonContext, contract, issueDate, null, "PORCENTAJE_IRPF", "6.6");
		
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord marchExtra = getExtra(aonContext, agreement.getId(), "31/03");
		ISQLContractSalaryCalculatorContext extraCtx = getExtraSalaryCalculatorContext(connection, contract, marchExtra, year, issueDate);

		Salary extra = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(extraCtx);
		Assert.assertEquals(String.format("%s",TOTAL_PAYMENT), 1200.00/12.00 * 7 , extra.getTotalPayment(), DELTA);
		Assert.assertEquals(String.format("%s",IRPF_PERCENT), 1200.00/2 * 6.6/100.00, extra.getTotalIrpf(), DELTA);
	}

	@Test
	public void testExtrasAtSalaryI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");
		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "1000.00 * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "100.00 * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.month = Month.MARCH;
								this.concept = conceptPagaExtra.getId();
								this.expression = "SALARIO_BASE";
							}
						},
						new Payment() {
							{
								this.month = Month.JULY;
								this.concept = conceptPagaExtra.getId();
								this.expression = "SALARIO_BASE + PLUS_SALARIAL";
							}
						},
						new Payment() {
							{
								this.month = Month.DECEMBER;
								this.concept = conceptPagaExtra.getId();
								this.expression = "SALARIO_BASE + PLUS_SALARIAL";
							}
						}
				});
		
		

		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.JUNE);
		calendar.set(DAY_OF_MONTH, 1);
		Date contractDate = new Date(calendar.getTimeInMillis());

		ContractRecord contract = newContract(
				aonContext
				,new String[] {} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));

		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);
		
		// 
		// MARCH
		//
		startDate = add(startDate, Calendar.MONTH, 2);
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 + 1000.00 * 3 / 12.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);
		
		// 
		// JULY
		//
		startDate = add(startDate, Calendar.MONTH, 4);
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 + 1100.00 * 7 / 12.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);

		// 
		// DECEMBER
		//
		startDate = add(startDate, Calendar.MONTH, 5);
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 + 1100.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);
	}

	@Test
	public void testExtrasAtSalaryII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
					new Extra() {
						{
							this.expression = "SALARIO_BASE + 0.00";
							this.month = Month.MARCH;
							this.start = "01/01";
							this.end = "31/12";
							this.issue = "31/03";
						}
					}, 
					new Extra() {
						{
							this.expression = "SALARIO_BASE + PLUS_SALARIAL";
							this.month = Month.JUNE;
							this.start = "01/07 -1";
							this.end = "30/06";
							this.issue = "30/06";
						}
					}, 
					new Extra() {
						{
							this.expression = "SALARIO_BASE + PLUS_SALARIAL";
							this.month = Month.DECEMBER;
							this.start = "01/01";
							this.end = "31/12";
							this.issue = "31/12";
						}
					}, 
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "1000.00 * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "100.00 * DIAS_TRABAJADOS/DIAS_MES";
							}
						}
				});
		
		

		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.JUNE);
		calendar.set(DAY_OF_MONTH, 1);
		Date contractDate = new Date(calendar.getTimeInMillis());

		ContractRecord contract = newContract(
				aonContext
				,new String[] {} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));

		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);
		
		// 
		// MARCH
		//
		startDate = add(startDate, Calendar.MONTH, 2);
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 + 1000.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);
		
		calendar.set(MONTH, Calendar.MARCH);
		calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date issueDate = new Date(calendar.getTimeInMillis());
		int year = calendar.get(Calendar.YEAR);
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord extra = getExtra(aonContext, agreement.getId(), "31/03");
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder)
		.calculate(getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate));
		jooqSalaryBuilder.execute();
		
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);

		// 
		// JULY
		//
		startDate = add(startDate, Calendar.MONTH, 3);
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 + 1100.00 * 6 /12, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);

		calendar.set(MONTH, Calendar.JUNE);
		calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		issueDate = new Date(calendar.getTimeInMillis());
		year = calendar.get(Calendar.YEAR);
		agreement = getAgreement(aonContext, category.getAgreementLevel());
		extra = getExtra(aonContext, agreement.getId(), "30/06");
		jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder)
		.calculate(getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate));
		jooqSalaryBuilder.execute();

		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);

		// 
		// DECEMBER
		//
		startDate = add(startDate, Calendar.MONTH, 6);
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 + 1100.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);

		calendar.set(MONTH, Calendar.DECEMBER);
		calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		issueDate = new Date(calendar.getTimeInMillis());
		year = calendar.get(Calendar.YEAR);
		agreement = getAgreement(aonContext, category.getAgreementLevel());
		extra = getExtra(aonContext, agreement.getId(), "31/12");
		jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder)
		.calculate(getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate));
		jooqSalaryBuilder.execute();

		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);
	}

	@Test
	public void testExtrasAtSalaryIII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
					new Extra() {
						{
							this.expression = "SALARIO_BASE";
							this.month = Month.MARCH;
							this.start = "01/01";
							this.end = "31/12";
							this.issue = "31/03";
						}
					}, 
					new Extra() {
						{
							this.expression = "SALARIO_BASE + PLUS_SALARIAL";
							this.month = Month.JUNE;
							this.start = "01/07 -1";
							this.end = "30/06";
							this.issue = "30/06";
						}
					}, 
					new Extra() {
						{
							this.expression = "SALARIO_BASE + PLUS_SALARIAL";
							this.month = Month.DECEMBER;
							this.start = "01/01";
							this.end = "31/12";
							this.issue = "31/12";
						}
					}, 
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "1000.00 * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "100.00 * DIAS_TRABAJADOS/DIAS_MES";
							}
						}
				});
		
		

		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.JUNE);
		calendar.set(DAY_OF_MONTH, 1);
		Date contractDate = new Date(calendar.getTimeInMillis());

		ContractRecord contract = newContract(
				aonContext
				,new String[] {} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));

		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);
		
		// 
		// MARCH
		//
		startDate = add(startDate, Calendar.MONTH, 2);
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 + 1000.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);
		
		// 
		// JULY
		//
		startDate = add(startDate, Calendar.MONTH, 3);
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 + 550.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);

		// 
		// DECEMBER
		//
		startDate = add(startDate, Calendar.MONTH, 6);
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 + 1100.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);
	}

	@Test
	public void testExtrasAtSalaryIV() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
					new Extra() {
						{
							this.expression = "SALARIO_BASE";
							this.month = Month.MARCH;
							this.start = "01/01";
							this.end = "31/12";
							this.issue = "31/03";
						}
					}, 
					new Extra() {
						{
							this.expression = "SALARIO_BASE + PLUS_SALARIAL";
							this.month = Month.JUNE;
							this.start = "01/07 -1";
							this.end = "30/06";
							this.issue = "30/06";
						}
					}, 
					new Extra() {
						{
							this.expression = "SALARIO_BASE + PLUS_SALARIAL";
							this.month = Month.DECEMBER;
							this.start = "01/01";
							this.end = "31/12";
							this.issue = "31/12";
						}
					}, 
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "1000.00 * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "100.00 * DIAS_TRABAJADOS/DIAS_MES";
							}
						}
				});
		
		

		Calendar calendar = Calendar.getInstance();
		calendar.set(DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
		calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(DAY_OF_MONTH) - 14);
		Date contractDate = new Date(calendar.getTimeInMillis());

		ContractRecord contract = newContract(
				aonContext
				,contractDate
				,Collections.singletonMap("DIAS_MES", "30.00")
				,new String[] {} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		Date startDate = getFirstDayOfMonth(contractDate);
		Date endDate = getLastDayOfMonth(startDate);
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));

		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 550.00 , salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 550.00 + ( 550.00 /6.00 ) + 500.00/12.00, salary.getCommonBase(), DELTA);
		
		// 
		// MARCH
		//
		startDate = add(startDate, Calendar.MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 + 1000.00 * 10.50 / 12.00 , salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);
		
		calendar.set(MONTH, Calendar.MARCH);
		calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date issueDate = new Date(calendar.getTimeInMillis());
		int year = calendar.get(Calendar.YEAR);
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord extra = getExtra(aonContext, agreement.getId(), "31/03");
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder)
		.calculate(getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate));
		jooqSalaryBuilder.execute();
		
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);

		// 
		// JULY
		//
		startDate = add(startDate, Calendar.MONTH, 3);
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 + 1100.00 * 4.50 / 12.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);

		calendar.set(MONTH, Calendar.JUNE);
		calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		issueDate = new Date(calendar.getTimeInMillis());
		year = calendar.get(Calendar.YEAR);
		agreement = getAgreement(aonContext, category.getAgreementLevel());
		extra = getExtra(aonContext, agreement.getId(), "30/06");
		jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder)
		.calculate(getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate));
		jooqSalaryBuilder.execute();

		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);

		// 
		// DECEMBER
		//
		startDate = add(startDate, Calendar.MONTH, 6);
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 + 1100.00 * 10.50 / 12.00 , salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);

		calendar.set(MONTH, Calendar.DECEMBER);
		calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		issueDate = new Date(calendar.getTimeInMillis());
		year = calendar.get(Calendar.YEAR);
		agreement = getAgreement(aonContext, category.getAgreementLevel());
		extra = getExtra(aonContext, agreement.getId(), "31/12");
		jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder)
		.calculate(getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate));
		jooqSalaryBuilder.execute();

		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);
	}

	@Test
	public void testExtrasAtSalaryV() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
					new Extra() {
						{
							this.expression = "SALARIO_BASE";
							this.month = Month.MARCH;
							this.start = "01/01";
							this.end = "31/12";
							this.issue = "31/03";
						}
					}, 
					new Extra() {
						{
							this.expression = "SALARIO_BASE + PLUS_SALARIAL";
							this.month = Month.JUNE;
							this.start = "01/07 -1";
							this.end = "30/06";
							this.issue = "30/06";
						}
					}, 
					new Extra() {
						{
							this.expression = "SALARIO_BASE + PLUS_SALARIAL";
							this.month = Month.DECEMBER;
							this.start = "01/01";
							this.end = "31/12";
							this.issue = "31/12";
						}
					}, 
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "1000.00 * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "100.00 * DIAS_TRABAJADOS/DIAS_MES";
							}
						}
				});
		
		

		Calendar calendar = Calendar.getInstance();
		calendar.set(DAY_OF_MONTH, 1);
		calendar.set(MONTH, Calendar.FEBRUARY);
		calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(DAY_OF_MONTH) - 14);
		Date contractDate = new Date(calendar.getTimeInMillis());

		ContractRecord contract = newContract(
				aonContext
				,contractDate
				,Collections.singletonMap("DIAS_MES", "30.00")
				,new String[] {} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		Date startIt = add(contractDate, Calendar.DATE, 10);
		Date endIt = add(startIt, Calendar.DATE, 14);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startIt, endIt, null);
		
		Date startDate = getFirstDayOfMonth(contractDate);
		Date endDate = getLastDayOfMonth(startDate);
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));

		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 * 10 / 30 , salary.getTotalPayment(), DELTA);
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), (1100.00 * 10 / 30) + ( (1100.00 * 10 / 30) /6.00 ) + (1000.00 * 10 / 30)/12.00, salary.getCommonBase(), DELTA);
		
		// 
		// MARCH
		//
		startDate = add(startDate, Calendar.MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 * 21/30.00 + 1000.00 * ( 9.00 + 10.00/30.00 + 21.00/30.00 )  / 12.00 , salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), (1100.00 * 21/30.00)  + ( (1100.00 * 21/30.00)/6.00 ) + (1000.00 * 21/30.00)/12.00, salary.getCommonBase(), DELTA);
		
		calendar.set(MONTH, Calendar.MARCH);
		calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date issueDate = new Date(calendar.getTimeInMillis());
		int year = calendar.get(Calendar.YEAR);
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord extra = getExtra(aonContext, agreement.getId(), "31/03");
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder)
		.calculate(getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate));
		jooqSalaryBuilder.execute();
		
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 * 21/30.00, salary.getTotalPayment(), DELTA);
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), (1100.00 * 21/30.00)  + ( (1100.00 * 21/30.00)/6.00 ) + (1000.00 * 21/30.00)/12.00, salary.getCommonBase(), DELTA);

		// 
		// JULY
		//
		startDate = add(startDate, Calendar.MONTH, 3);
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 + 1100.00 * ( 3.00 + 10.00/30.00 + 21.00/30.00 ) / 12.00, salary.getTotalPayment(), DELTA);
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);

		calendar.set(MONTH, Calendar.JUNE);
		calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		issueDate = new Date(calendar.getTimeInMillis());
		year = calendar.get(Calendar.YEAR);
		agreement = getAgreement(aonContext, category.getAgreementLevel());
		extra = getExtra(aonContext, agreement.getId(), "30/06");
		jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder)
		.calculate(getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate));
		jooqSalaryBuilder.execute();

		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00, salary.getTotalPayment(), DELTA);
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);

		// 
		// DECEMBER
		//
		startDate = add(startDate, Calendar.MONTH, 6);
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 + 1100.00 * ( 9.00 + 10.00/30.00 + 21.00/30.00 ) / 12.00 , salary.getTotalPayment(), DELTA);
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);

		calendar.set(MONTH, Calendar.DECEMBER);
		calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		issueDate = new Date(calendar.getTimeInMillis());
		year = calendar.get(Calendar.YEAR);
		agreement = getAgreement(aonContext, category.getAgreementLevel());
		extra = getExtra(aonContext, agreement.getId(), "31/12");
		jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder)
		.calculate(getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate));
		jooqSalaryBuilder.execute();

		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00, salary.getTotalPayment(), DELTA);
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);
	}

	@Test
	public void testExtrasAtSalaryVI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
					new Extra() {
						{
							this.expression = "SALARIO_BASE + 0.00";
							this.month = Month.MARCH;
							this.start = "01/01";
							this.end = "31/12";
							this.issue = "31/03";
						}
					}, 
					new Extra() {
						{
							this.expression = "SALARIO_BASE + PLUS_SALARIAL";
							this.month = Month.JUNE;
							this.start = "01/07 -1";
							this.end = "30/06";
							this.issue = "30/06";
						}
					}, 
					new Extra() {
						{
							this.expression = "SALARIO_BASE + PLUS_SALARIAL";
							this.month = Month.DECEMBER;
							this.start = "01/01";
							this.end = "31/12";
							this.issue = "21/12";
						}
					}, 
					new Extra() {
						{
							this.expression = "(SALARIO_BASE + PLUS_SALARIAL) * 9.00 / 12.00";
							this.month = Month.SEPTEMBER;
							this.start = "01/01";
							this.end = "30/09";
							this.issue = "30/09";
						}
					}, 
					new Extra() {
						{
							this.expression = "(SALARIO_BASE + PLUS_SALARIAL) * 3.00 / 12.00";
							this.month = Month.DECEMBER;
							this.start = "01/10";
							this.end = "31/12";
							this.issue = "31/12";
						}
					}, 
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "1000.00 * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "100.00 * DIAS_TRABAJADOS/DIAS_MES";
							}
						}
				});
		
		

		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.JUNE);
		calendar.set(DAY_OF_MONTH, 1);
		Date contractDate = new Date(calendar.getTimeInMillis());

		ContractRecord contract = newContract(
				aonContext
				,new String[] {} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));

		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00 + 1100.00/12.00, salary.getCommonBase(), DELTA);
		
		// 
		// MARCH
		//
		startDate = add(startDate, Calendar.MONTH, 2);
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 + 1000.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00 + 1100.00/12.00, salary.getCommonBase(), DELTA);
		
		calendar.set(MONTH, Calendar.MARCH);
		calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date issueDate = new Date(calendar.getTimeInMillis());
		int year = calendar.get(Calendar.YEAR);
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord extra = getExtra(aonContext, agreement.getId(), "31/03");
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder)
		.calculate(getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate));
		jooqSalaryBuilder.execute();
		
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00 + 1100.00/12.00, salary.getCommonBase(), DELTA);

		// 
		// JULY
		//
		startDate = add(startDate, Calendar.MONTH, 3);
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 + 1100.00 * 6 /12, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00 + 1100.00/12.00, salary.getCommonBase(), DELTA);

		calendar.set(MONTH, Calendar.JUNE);
		calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		issueDate = new Date(calendar.getTimeInMillis());
		year = calendar.get(Calendar.YEAR);
		agreement = getAgreement(aonContext, category.getAgreementLevel());
		extra = getExtra(aonContext, agreement.getId(), "30/06");
		jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder)
		.calculate(getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate));
		jooqSalaryBuilder.execute();

		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00 + 1100.00/12.00, salary.getCommonBase(), DELTA);

		// 
		// DECEMBER
		//
		startDate = add(startDate, Calendar.MONTH, 6);
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 + (1100.00 * 3 / 12.00), salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00 + 1100.00/12.00, salary.getCommonBase(), DELTA);

		calendar.set(MONTH, Calendar.DECEMBER);
		calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		issueDate = new Date(calendar.getTimeInMillis());
		year = calendar.get(Calendar.YEAR);
		agreement = getAgreement(aonContext, category.getAgreementLevel());
		extra = getExtra(aonContext, agreement.getId(), "21/12");
		jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		SalaryBuilder salaryBuilder = new SalaryBuilder();
		CompositeSalaryBuilder<Salary, ISalaryBuilder<Salary>>  compositeSalaryBuilder = 
				new CompositeSalaryBuilder<Salary, ISalaryBuilder<Salary>>(salaryBuilder, jooqSalaryBuilder);
		
		salary = new SmartContractSalaryCalculator<Salary>(compositeSalaryBuilder)
		.calculate(getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate));
		jooqSalaryBuilder.execute();
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 , salary.getTotalPayment(), DELTA);

		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 + 1100.00 * 3 / 12.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00 + 1100.00/12.00, salary.getCommonBase(), DELTA);
	}

	@Test
	@Ignore("Not yet...")
	public void testExtrasAtSalaryVII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
					new Extra() {
						{
							this.expression = "SALARIO_BASE";
							this.month = Month.DECEMBER;
							this.start = "01/01";
							this.end = "31/12";
							this.issue = "15/12";
						}
					}, 
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "1000.00 * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "NETO(1200.00)";
							}
						}
				});
		
		

		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.JUNE);
		calendar.set(DAY_OF_MONTH, 1);
		Date contractDate = new Date(calendar.getTimeInMillis());

		ContractRecord contract = newContract(
				aonContext
				,new String[] {} 
				,new String[] {
					"BASE_IRPF * PORCENTAJE_IRPF/100"
				} 
				,category);
		//@formatter:off
		
		

		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());

		Assert.assertEquals(ContextVariable.TOTAL_LIQUID.getName(), 1200.00 , salary.getTotalLiquid());

		// 
		// DECEMBER
		//
		endDate = getLastDayOfYear(getToday());
		startDate = getFirstDayOfMonth(endDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 + 1100.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + 1100.00/12.00, salary.getCommonBase(), DELTA);

	}
	
	
	

	@Test
	public void testExtrasAtSalaryVIII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "SALARIO_BASE + PLUS_SALARIAL";
							this.month = Month.JUNE;
							this.start = "01/01";
							this.end = "30/06";
							this.issue = "30/06";
						}
					}, 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "SALARIO_BASE + PLUS_SALARIAL";
							this.month = Month.DECEMBER;
							this.start = "01/07";
							this.end = "31/12";
							this.issue = "31/12";
						}
					}, 
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "1000.00 * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "100.00 * DIAS_TRABAJADOS/DIAS_MES";
							}
						}
				});
		
		

		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.JUNE);
		calendar.set(DAY_OF_MONTH, 1);
		Date contractDate = new Date(calendar.getTimeInMillis());

		ContractRecord contract = newContract(
				aonContext
				,new String[] {
				} 
				,new String[] {} 
				,category);
		
		addPayment(aonContext, 
				contract, 
				contract.getStartDate(), 
				null, 
				conceptPagaExtra, 
				"PAGA EXTRA DE VERANO", 
				"SALARIO_BASE + PLUS_SALARIAL", 
				null, //irpfExpression, 
				null, //quoteExpression, 
				null,
				(byte) Month.JUNE.getValue());
		addPayment(aonContext, 
				contract, 
				contract.getStartDate(), 
				null, 
				conceptPagaExtra, 
				"PAGA EXTRA DE NAVIDAD", 
				"SALARIO_BASE + PLUS_SALARIAL", 
				null, //irpfExpression, 
				null, //quoteExpression, 
				null,
				(byte) Month.DECEMBER.getValue());
		//@formatter:off
		
		
		
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));

		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ), salary.getCommonBase(), DELTA);
		Assert.assertEquals(( 1100.00/6.00 ), salary.getExtraPayProration(), DELTA);
		
	}

	@Test
	public void testExtrasAtSalaryIX() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "SALARIO_BASE + PLUS_SALARIAL";
							this.month = Month.JULY;
							this.start = "01/07 -1";
							this.end = "30/06";
							this.issue = "01/07";
						}
					}, 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "(SALARIO_BASE + PLUS_SALARIAL)";
							this.month = Month.DECEMBER;
							this.start = "01/01";
							this.end = "31/12";
							this.issue = "15/12";
						}
					}, 
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "SALARIO_MENSUAL * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "PLUS_MENSUAL * DIAS_TRABAJADOS/DIAS_MES";
							}
						}
				});
		
		addData(aonContext, category, getFirstDayOfYear(getToday()), null, new HashMap<String,String>(){
			{
				put("PLUS_MENSUAL", "100.00");
				put("SALARIO_MENSUAL", "1000.00");
			}
		});
		

		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.AUGUST);
		calendar.set(DAY_OF_MONTH, 1);
		Date contractStartDate = new Date(calendar.getTimeInMillis()); // 01/08

		ContractRecord contract = newContract(
				aonContext
				,contractStartDate
				,new String[] {} 
				,new String[] {} 
				,category);
		
		//@formatter:off
		
		for ( Date startDate = contractStartDate ; 
			startDate.before(getLastDayOfYear(contractStartDate)) ; 
			startDate = add(startDate, Calendar.MONTH,1) ) 
		{
			Date endDate = getLastDayOfMonth(startDate);
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
		}
		
		addData(aonContext, category, getFirstDayOfYear(getToday()), null, new HashMap<String,String>(){
			{
				put("PLUS_MENSUAL", "150.00");
				put("SALARIO_MENSUAL", "1500.00");
			}
		});
		
		
		calendar.set(MONTH, Calendar.DECEMBER);
		calendar.set(DAY_OF_MONTH, 15);
		Date issueDate = new Date(calendar.getTimeInMillis());
		int year = calendar.get(Calendar.YEAR);

		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord decemberExtra = getExtra(aonContext, agreement.getId(), "15/12");
		ISQLContractSalaryCalculatorContext extraCtx = getExtraSalaryCalculatorContext(connection, contract, decemberExtra, year, issueDate);
		SmartContractSalaryCalculator<Salary> contractSalaryCalculator = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder());
		contractSalaryCalculator.setListener(new Listener() {
		});
		Salary extra = contractSalaryCalculator.calculate(extraCtx);

		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00/12.00 * 5 , extra.getTotalPayment(), 0.005 );
	}

	@Test
	public void testExtrasI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "SALARIO_BASE + PLUS_SALARIAL";
							this.month = Month.JULY;
							this.start = "01/07 -1";
							this.end = "30/06";
							this.issue = "01/07";
						}
					}, 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "(SALARIO_BASE + PLUS_SALARIAL)";
							this.month = Month.DECEMBER;
							this.start = "01/01";
							this.end = "31/12";
							this.issue = "15/12";
						}
					}, 
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "SALARIO_MENSUAL * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "PLUS_MENSUAL * DIAS_LABORALES";
							}
						}
				});
		
		addData(aonContext, category, getFirstDayOfYear(getToday()), null, new HashMap<String,String>(){
			{
				put("PLUS_MENSUAL", "10.00");
				put("SALARIO_MENSUAL", "1000.00");
			}
		});
		

		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.AUGUST);
		calendar.set(DAY_OF_MONTH, 1);
		Date contractStartDate = new Date(calendar.getTimeInMillis()); // 01/08

		ContractRecord contract = newContract(
				aonContext
				,contractStartDate
				, new HashMap<String,String>(){
					{
						put("DIAS_MES", "30.00");
					}
				}				
				,category);
		
		
		for ( Date startDate = contractStartDate ; 
				startDate.before(getLastDayOfYear(contractStartDate)) ; 
				startDate = add(startDate, Calendar.MONTH,1) ) 
		{
			Date endDate = getLastDayOfMonth(startDate);
			addData(aonContext, contract, startDate, endDate, "DIAS_LABORALES", "15.00");
		}
		
		double extraPayProration = 0.00;
		for ( Date startDate = contractStartDate ; 
				startDate.before(getLastDayOfYear(contractStartDate)) ; 
				startDate = add(startDate, Calendar.MONTH,1) ) 
		{
			Date endDate = getLastDayOfMonth(startDate);
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder());
			Salary salary = calculator.calculate(ctx);
			extraPayProration += salary.getExtraPayProration();
		}

		//@formatter:off
		
		
		calendar.set(MONTH, Calendar.DECEMBER);
		calendar.set(DAY_OF_MONTH, 15);
		Date issueDate = new Date(calendar.getTimeInMillis());
		int year = calendar.get(Calendar.YEAR);

		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord decemberExtra = getExtra(aonContext, agreement.getId(), "15/12");
		ISQLContractSalaryCalculatorContext extraCtx = getExtraSalaryCalculatorContext(connection, contract, decemberExtra, year, issueDate);
		SmartContractSalaryCalculator<Salary> contractSalaryCalculator = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder());
		contractSalaryCalculator.setListener(new Listener() {
		});
		Salary extra = contractSalaryCalculator.calculate(extraCtx);

		Assert.assertEquals(ContextVariable.EXTRA_PAY.getName(), ( 1000.00 + 10.00 * 15.00) /12.00 * 5 , extraPayProration / 2.00 , 0.005 );
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), ( 1000.00 + 10.00 * 15.00) /12.00 * 5 , extra.getTotalPayment(), 0.005 );
	}

	@Test
	public void testExtrasII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "SALARIO_BASE + PLUS_SALARIAL";
							this.month = Month.JULY;
							this.start = "01/07 -1";
							this.end = "30/06";
							this.issue = "01/07";
						}
					}, 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "(SALARIO_BASE + PLUS_SALARIAL)";
							this.month = Month.DECEMBER;
							this.start = "01/01";
							this.end = "31/12";
							this.issue = "15/12";
						}
					}, 
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "SALARIO_MENSUAL * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "PLUS_MENSUAL * DIAS_EFECTIVOS";
							}
						}
				});
		
		addData(aonContext, category, getFirstDayOfYear(getToday()), null, new HashMap<String,String>(){
			{
				put("PLUS_MENSUAL", "10.00");
				put("SALARIO_MENSUAL", "1000.00");
			}
		});
		

		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.AUGUST);
		calendar.set(DAY_OF_MONTH, 1);
		Date contractStartDate = new Date(calendar.getTimeInMillis()); // 01/08

		ContractRecord contract = newContract(
				aonContext
				,contractStartDate
				, new HashMap<String,String>(){
					{
						put("DIAS_MES", "30.00");
					}
				}				
				,category);
		
		
		
		double extraPayProration = 0.00;
		for ( Date startDate = contractStartDate ; 
				startDate.before(getLastDayOfYear(contractStartDate)) ; 
				startDate = add(startDate, Calendar.MONTH,1) ) 
		{
			Date endDate = getLastDayOfMonth(startDate);
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder());
			Salary salary = calculator.calculate(ctx);
			extraPayProration += salary.getExtraPayProration();
		}

		//@formatter:off
		
		
		calendar.set(MONTH, Calendar.DECEMBER);
		calendar.set(DAY_OF_MONTH, 15);
		Date issueDate = new Date(calendar.getTimeInMillis());
		int year = calendar.get(Calendar.YEAR);

		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord decemberExtra = getExtra(aonContext, agreement.getId(), "15/12");
		ISQLContractSalaryCalculatorContext extraCtx = getExtraSalaryCalculatorContext(connection, contract, decemberExtra, year, issueDate);
		SmartContractSalaryCalculator<Salary> contractSalaryCalculator = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder());
		contractSalaryCalculator.setListener(new Listener() {
		});
		Salary extra = contractSalaryCalculator.calculate(extraCtx);

		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), extraPayProration / 2.00 , extra.getTotalPayment(), 0.005 );
	}

	@Test
	@Ignore("Not yet...")
	public void testExtrasAtSalaryX() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
					new Extra() {
						{
							this.expression = "SALARIO_BASE + 0.00";
							this.month = Month.MARCH;
							this.start = "01/01";
							this.end = "31/12";
							this.issue = "31/03";
						}
					}, 
					new Extra() {
						{
							this.expression = "SALARIO_BASE + PLUS_SALARIAL";
							this.month = Month.JUNE;
							this.start = "01/07 -1";
							this.end = "30/06";
							this.issue = "30/06";
						}
					}, 
					new Extra() {
						{
							this.expression = "SALARIO_BASE + PLUS_SALARIAL";
							this.month = Month.DECEMBER;
							this.start = "01/01";
							this.end = "31/12";
							this.issue = "31/12";
						}
					}, 
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "1000.00 * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "BRUTO(1100.00 * DIAS_TRABAJADOS/DIAS_MES)";
							}
						}
				});
		
		

		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.JUNE);
		calendar.set(DAY_OF_MONTH, 1);
		Date contractDate = new Date(calendar.getTimeInMillis());

		ContractRecord contract = newContract(
				aonContext
				,new String[] {} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));

		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);
		
		// 
		// MARCH
		//
		startDate = add(startDate, Calendar.MONTH, 2);
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 + 1000.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);
		
		calendar.set(MONTH, Calendar.MARCH);
		calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date issueDate = new Date(calendar.getTimeInMillis());
		int year = calendar.get(Calendar.YEAR);
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord extra = getExtra(aonContext, agreement.getId(), "31/03");
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder)
		.calculate(getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate));
		jooqSalaryBuilder.execute();
		
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);

		// 
		// JULY
		//
		startDate = add(startDate, Calendar.MONTH, 3);
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		System.out.println("//"); 
		System.out.println("// JULY");
		System.out.println("//");
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 + 1100.00 * 6 /12, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);

		calendar.set(MONTH, Calendar.JUNE);
		calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		issueDate = new Date(calendar.getTimeInMillis());
		year = calendar.get(Calendar.YEAR);
		agreement = getAgreement(aonContext, category.getAgreementLevel());
		extra = getExtra(aonContext, agreement.getId(), "30/06");
		jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder)
		.calculate(getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate));
		jooqSalaryBuilder.execute();

		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);

		// 
		// DECEMBER
		//
		startDate = add(startDate, Calendar.MONTH, 6);
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 + 1100.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);

		calendar.set(MONTH, Calendar.DECEMBER);
		calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		issueDate = new Date(calendar.getTimeInMillis());
		year = calendar.get(Calendar.YEAR);
		agreement = getAgreement(aonContext, category.getAgreementLevel());
		extra = getExtra(aonContext, agreement.getId(), "31/12");
		jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder)
		.calculate(getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate));
		jooqSalaryBuilder.execute();

		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);
	}

	@Test
	public void testExtrasAtSalaryXI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
					new Extra() {
						{
							this.expression = "SALARIO_BASE + 0.00";
							this.month = Month.MARCH;
							this.start = "01/01";
							this.end = "31/12";
							this.issue = "31/03";
						}
					}, 
					new Extra() {
						{
							this.expression = "SALARIO_BASE + PLUS_SALARIAL";
							this.month = Month.JUNE;
							this.start = "01/01";
							this.end = "30/06";
							this.issue = "30/06";
						}
					}, 
					new Extra() {
						{
							this.expression = "SALARIO_BASE + PLUS_SALARIAL";
							this.month = Month.DECEMBER;
							this.start = "01/07";
							this.end = "31/12";
							this.issue = "20/12";
						}
					}, 
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "1000.00 * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "100.00 * DIAS_TRABAJADOS/DIAS_MES";
							}
						}
				});
		
		
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());

		for ( AgreementPaymentRecord p: getAgreementPayments(aonContext, agreement.getId()) ) {
			p.setSalaryType((byte) SalaryType.SALARY.ordinal());
			p.update();
		}

		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.JUNE);
		calendar.set(DAY_OF_MONTH, 1);
		Date contractDate = new Date(calendar.getTimeInMillis());

		ContractRecord contract = newContract(
				aonContext
				,new String[] {} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));

		//for ( SalaryPayment payment : salary.getSalaryPayments() ) 
		//	System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);
		
		// 
		// MARCH
		//
		startDate = add(startDate, Calendar.MONTH, 2);
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		//for ( SalaryPayment payment : salary.getSalaryPayments() ) 
		//	System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 + 1000.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);
		
		calendar.set(MONTH, Calendar.MARCH);
		calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date issueDate = new Date(calendar.getTimeInMillis());
		int year = calendar.get(Calendar.YEAR);
		AgreementExtraRecord extra = getExtra(aonContext, agreement.getId(), "31/03");
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder)
		.calculate(getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate));
		jooqSalaryBuilder.execute();
		
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);

		// 
		// JULY
		//
		startDate = add(startDate, Calendar.MONTH, 3);
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		//for ( SalaryPayment payment : salary.getSalaryPayments() ) 
		//	System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 + 1100.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);

		calendar.set(MONTH, Calendar.JUNE);
		calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		issueDate = new Date(calendar.getTimeInMillis());
		year = calendar.get(Calendar.YEAR);
		agreement = getAgreement(aonContext, category.getAgreementLevel());
		extra = getExtra(aonContext, agreement.getId(), "30/06");
		jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder)
		.calculate(getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate));
		jooqSalaryBuilder.execute();

		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);

		// 
		// DECEMBER
		//
		startDate = add(startDate, Calendar.MONTH, 6);
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 /*+ 1100.00*/, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);

		calendar.set(MONTH, Calendar.DECEMBER);
		calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		issueDate = new Date(calendar.getTimeInMillis());
		year = calendar.get(Calendar.YEAR);
		agreement = getAgreement(aonContext, category.getAgreementLevel());
		extra = getExtra(aonContext, agreement.getId(), "20/12");
		jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder)
		.calculate(getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate));
		jooqSalaryBuilder.execute();

		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);
	}

	@Test
	public void testExtrasAtSalaryXII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
					new Extra() {
						{
							this.expression = "/*VERANO*/SALARIO_BASE + PLUS_SALARIAL";
							this.month = Month.JUNE;
							this.start = "01/01";
							this.end = "30/06";
							this.issue = "30/06";
						}
					}, 
					new Extra() {
						{
							this.expression = "/*NAVIDAD*/SALARIO_BASE + PLUS_SALARIAL";
							this.month = Month.DECEMBER;
							this.start = "01/07";
							this.end = "31/12";
							this.issue = "31/12";
						}
					}, 
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "1000.00 * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "100.00 * DIAS_TRABAJADOS/DIAS_MES";
							}
						}
				});
		
		
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());

		ContractRecord contract = newContract(
				aonContext
				,new String[] {} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		Date startDate ;
		for ( startDate = getFirstDayOfYear(getToday()) ;  get(startDate, Calendar.MONTH) < 5; startDate = add(startDate, Calendar.MONTH, 1)  ) {
			Date endDate = getLastDayOfMonth(startDate);
			JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder )
			.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
			jooqSalaryBuilder.execute();
		}
		
		// 
		// EXTRA 
		//
		Date endDate = getLastDayOfMonth(startDate);
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		salary.getSalaryPayments().forEach(p -> System.out.println(p.getDescription() + " = " + p.getAmount() + "(" + p.getQuote() +")"));
		
		org.junit.Assert.assertEquals((1100.00 + 1100.00), (double) salary.getTotalPayment(), 0.01);
		
		endDate = getLastDayOfMonth(startDate);
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder )
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		jooqSalaryBuilder.execute();

		// 
		// EXTRA 
		//
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		salary.getSalaryPayments().forEach(p -> System.out.println(p.getDescription() + " = " + p.getAmount() + "(" + p.getQuote() +")"));
		
		org.junit.Assert.assertEquals((1100.00 + 1100.00), (double) salary.getTotalPayment(), 0.01);
		
		// 
		// EXTRA 
		//
		//addPayment(aonContext, contract, conceptPlusSalarial, "150.00 * DIAS_TRABAJADOS/DIAS_MES");
		//endDate = getLastDayOfMonth(startDate);
		//salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		//.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		//salary.getSalaryPayments().forEach(p -> System.out.println(p.getDescription() + " = " + p.getAmount() + "(" + p.getQuote() +")"));
		
		//org.junit.Assert.assertEquals((1150.00 + 1100.00 + 50.00/6), (double) salary.getTotalPayment(), 0.01);

	}

	@Test
	public void testExtrasAtSalaryXIII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");
		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "/*VERANO*/SALARIO_BASE + PLUS_SALARIAL";
							this.month = Month.JUNE;
							this.start = "01/01";
							this.end = "30/06";
							this.issue = "30/06";
						}
					}, 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "/*NAVIDAD*/SALARIO_BASE + PLUS_SALARIAL";
							this.month = Month.DECEMBER;
							this.start = "01/07";
							this.end = "31/12";
							this.issue = "31/12";
						}
					}, 
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "1000.00 * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "100.00 * DIAS_TRABAJADOS/DIAS_MES";
							}
						}
				});
		
		
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.AUGUST);
		calendar.set(DAY_OF_MONTH, 23);
		Date contractDate = new Date(calendar.getTimeInMillis());

		ContractRecord contract = newContract(
				aonContext
				,contractDate
				,new String[] {} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		addPayment(aonContext, contract, contractDate, getLastDayOfMonth(contractDate), conceptPagaExtra, "/*VERANO*/SALARIO_BASE + PLUS_SALARIAL", "REMOVE()", null, null, null, (byte) Month.JUNE.ordinal());
		addPayment(aonContext, contract, contractDate, getLastDayOfMonth(contractDate), conceptPagaExtra, "/*NAVIDAD*/SALARIO_BASE + PLUS_SALARIAL", "REMOVE()", null, null, null, (byte) Month.DECEMBER.ordinal());
		
		addPayment(aonContext, contract, contractDate, getLastDayOfMonth(contractDate), "PAGAS EXTRAS", "71.50", "_P", "_P", PaymentType.CRA_0004, SalaryType.SALARY);

		Date startDate ;
		for ( startDate = getFirstDayOfMonth(contractDate) ;  get(startDate, Calendar.MONTH) < 11; startDate = add(startDate, Calendar.MONTH, 1)  ) {
			Date endDate = getLastDayOfMonth(startDate);
			JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder )
			.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
			jooqSalaryBuilder.execute();
		}
		
		// 
		// EXTRA 
		//
		Date endDate = getLastDayOfMonth(startDate);
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		salary.getSalaryPayments().forEach(p -> System.out.println(p.getDescription() + " = " + p.getAmount() + "(" + p.getQuote() +")"));
		
		org.junit.Assert.assertEquals((1100.00 + 1100.00/6 * 4), (double) salary.getTotalPayment(), 0.01);
		

	}

	@Test
	public void testExtrasAtSalaryXIV() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");
		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "/*VERANO*/( 1111.11 * 100 ) / (100.00 - 13.43 )";
							this.month = Month.JUNE;
							this.start = "01/01";
							this.end = "30/06";
							this.issue = "30/06";
						}
					}, 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "/*NAVIDAD*/( 1111.11 * 100 ) / (100.00 - 13.43 )";
							this.month = Month.DECEMBER;
							this.start = "01/07";
							this.end = "31/12";
							this.issue = "31/12";
						}
					}, 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "/*BENEFICIOS*/( 1111.11 * 100 ) / (100.00 - 13.43 )";
							this.month = Month.MAY;
							this.start = "01/01 -1";
							this.end = "31/12 -1";
							this.issue = "15/05";
						}
					}, 
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "1000.00 * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "100.00 * DIAS_TRABAJADOS/DIAS_MES";
							}
						}
				});
		
		
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(YEAR, -2);
		calendar.set(DAY_OF_MONTH, 1);
		calendar.set(MONTH, Calendar.JANUARY);
		Date contractDate = new Date(calendar.getTimeInMillis());

		ContractRecord contract = newContract(
				aonContext
				,contractDate
				,new String[] {} 
				,new String[] {
						"BASE_IRPF * 13.43 / 100.00"
				} 
				,category);
		//@formatter:off
		
		//addPayment(aonContext, contract, contractDate, null, conceptPagaExtra, null, "REMOVE()", null, null, PaymentType.CRA_0004, (byte) Month.JUNE.ordinal());
		
		addPayment(aonContext, contract, contractDate, null, conceptPagaExtra, "PAGA VERANO", "( 1111.11 * 100 ) / (100.00 - 13.43 )", "_P", "_P", PaymentType.CRA_0004, (byte)Month.JUNE.ordinal());
		addPayment(aonContext, contract, contractDate, null, conceptPagaExtra, "PAGA NAVIDAD", "( 1111.11 * 100 ) / (100.00 - 13.43 )", "_P", "_P", PaymentType.CRA_0004, (byte)Month.DECEMBER.ordinal());
		addPayment(aonContext, contract, contractDate, null, conceptPagaExtra, "PAGA BENEFICIOS", "( 1111.11 * 100 ) / (100.00 - 13.43 )", "_P", "_P", PaymentType.CRA_0004, (byte)Month.MAY.ordinal());
		
		// 
		// EXTRA 
		//
		calendar = Calendar.getInstance();
		calendar.set(DAY_OF_MONTH, 31);
		calendar.set(MONTH, Calendar.MAY);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date extraDate = new Date(calendar.getTimeInMillis());
		
		ISQLContractSalaryCalculatorContext ctx = 
		getContractSalaryCalculatorContext(connection, 
				getFirstDayOfMonth(extraDate), 
				getLastDayOfMonth(extraDate), 
				getLastDayOfMonth(extraDate), contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		//salary.getSalaryPayments().forEach( p -> System.out.println(p.getExpression() + " : " + p.getAmount() +", " + p.getQuote() ));
		
		org.junit.Assert.assertEquals(1100.00 +  ( 1111.11 * 100 ) / (100.00 - 13.43 ), salary.getTotalPayment(), 0.00);

		org.junit.Assert.assertEquals(( 1111.11 * 100 ) / (100.00 - 13.43 ) / 12 * 3, salary.getExtraPayProration(), 0.00);

		AgreementExtraRecord agreementExtra = getExtra(aonContext, agreement.getId(), "15/05" );
		
		//getExtraSalaryCalculatorContext(connection, contract, agreementExtra, calendar.get(YEAR),  extraDate);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLExtraSalaryCalculatorContext extraCtx = new SQLExtraSalaryCalculatorContext(connection, agreementExtra.getId(), calendar.get(YEAR),
				extraDate, criteria);
		extraCtx.next();
		
		
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder).calculate(extraCtx);
		jooqSalaryBuilder.execute();
		
		Stream<com.esferalia.aon.occam.api.model.Salary> salaries = 
				AON.getSalaries(aonContext, p -> 
				p.getContractProperty().eq(contract.getId())
				.and(p.getIsExtraProperty().eq(true)));
		salaries.forEach( s -> {
			org.junit.Assert.assertEquals(1111.11, s.getTotalLiquid(), 0.00);
		});
		
		ctx = 
		getContractSalaryCalculatorContext(connection, 
				getFirstDayOfMonth(extraDate), 
				getLastDayOfMonth(extraDate), 
				getLastDayOfMonth(extraDate), contract);
		
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		//salary.getSalaryPayments().forEach( p -> System.out.println(p.getExpression() + " : " + p.getAmount() +", " + p.getQuote() ));
		
		org.junit.Assert.assertEquals(1100.00, salary.getTotalPayment(), 0.00);

	}
	
	@Test
	public void testExtrasAtSalaryXV() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");
		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);

		Date contractDate = getFirstDayOfYear(getToday());

		ContractRecord contract = newContract(
				aonContext
				,contractDate
				,new String[] {} 
				,new String[] {} 
				,null);
		//@formatter:off
		
		addPayment(aonContext, contract, contractDate, null, conceptSalarioBase, "SALARIO BASE", "1000.00 * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0001);
		addPayment(aonContext, contract, contractDate, null, conceptPlusSalarial, "PLUS SALARIAL", "200.00 * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0001);
		
		ContractPaymentRecord pagaVerano = addPayment(aonContext, contract, contractDate, null, conceptPagaExtra, "PAGA VERANO", "SI(MES(INICIO_NOMINA)<7,MENSUALIDAD*2,0.00)", "_P", "_P", PaymentType.CRA_0004, (byte)Month.JUNE.ordinal());
		ContractPaymentRecord pagaNavidad = addPayment(aonContext, contract, contractDate, null, conceptPagaExtra, "PAGA NAVIDAD", "SI(MES(INICIO_NOMINA)>6,MENSUALIDAD*2,0.00)", "_P", "_P", PaymentType.CRA_0004, (byte)Month.DECEMBER.ordinal());
		
		Date startDate , endDate , issueDate ;
		
		for ( startDate = getFirstDayOfYear(getToday()); get(startDate, Calendar.MONTH) < 5; startDate = add(startDate, Calendar.MONTH,1)) {
		    issueDate  = endDate = getLastDayOfMonth(startDate);
		    JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<>(connection);
		    ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, issueDate, contract);
		    new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		    jooqSalaryBuilder.execute();
		}
		
		AON.getSalaries(aonContext, p -> p.getContractProperty().eq(contract.getId()))
		.forEach( s -> {
		    org.junit.Assert.assertEquals( 1200.00 , s.getTotalPayment() , 0.00 );
		    org.junit.Assert.assertEquals( 1200.00 / 6, s.getExtraProrationBase() , 0.00 );
		    org.junit.Assert.assertEquals( 1200.00 * 7 / 6, s.getCommonContingenciesBase() , 0.00 );
		    System.out.println( s.getStartDate() + " -. " + s.getExtraProrationBase() );
		});

		{
        		issueDate  = endDate = getLastDayOfMonth(startDate);
        		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<>(connection);
        		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, issueDate, contract);
        		new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
        		jooqSalaryBuilder.execute();
		}
		
		Date juneStartDate = startDate;
		AON.getSalaries(aonContext, p -> p.getContractProperty().eq(contract.getId()).and(p.getStartDateProperty().eq(juneStartDate)) ) 
		.forEach( s -> {
		    org.junit.Assert.assertEquals( 2400.00 , s.getTotalPayment() , 0.00 );
		    org.junit.Assert.assertEquals( 1200.00 / 6, s.getExtraProrationBase() , 0.00 );
		    org.junit.Assert.assertEquals( 1200.00 * 7 / 6, s.getCommonContingenciesBase() , 0.00 );
		    System.out.println( s.getStartDate() + " -. " + s.getExtraProrationBase() + ", " + s.getTotalPayment());
		});
		
		pagaVerano.setDescription("[90] PAGA EXTRA VERANO");
		pagaVerano.setExpression("SI(MES(INICIO_NOMINA)<7,(1000.00 * DIAS_TRABAJADOS / DIAS_MES)*2,0.00) ");
		pagaVerano.update();
		
		pagaNavidad.setDescription("[90] PAGA EXTRA NAVIDAD");
		pagaNavidad.setExpression("SI(MES(INICIO_NOMINA)>6,(1000.00 * DIAS_TRABAJADOS / DIAS_MES)*2,0.00)");
		pagaNavidad.update();
		
		for ( startDate = add(startDate, Calendar.MONTH,1) ; get(startDate, Calendar.MONTH) < 11; startDate = add(startDate, Calendar.MONTH,1)) {
		    issueDate  = endDate = getLastDayOfMonth(startDate);
		    JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<>(connection);
		    ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, issueDate, contract);
		    new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		    jooqSalaryBuilder.execute();
		}
		
		AON.getSalaries(aonContext, p -> p.getContractProperty().eq(contract.getId()).and(p.getStartDateProperty().gt(juneStartDate)))
		.forEach( s -> {
		    org.junit.Assert.assertEquals( 1200.00 , s.getTotalPayment() , 0.00 );
		    org.junit.Assert.assertEquals( 1000.00 / 6, s.getExtraProrationBase() , 0.01 );
		    org.junit.Assert.assertEquals( 1200.00 + 1000.00 / 6, s.getCommonContingenciesBase() , 0.01 );
		    System.out.println( s.getStartDate() + " -. " + s.getExtraProrationBase() );
		});

		{
        		issueDate  = endDate = getLastDayOfMonth(startDate);
        		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<>(connection);
        		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, issueDate, contract);
        		new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
        		jooqSalaryBuilder.execute();
		}

		Date decemberStartDate = startDate;
		AON.getSalaries(aonContext, p -> p.getContractProperty().eq(contract.getId()).and(p.getStartDateProperty().eq(decemberStartDate)) ) 
		.forEach( s -> {
		    org.junit.Assert.assertEquals( 1000.00 / 6, s.getExtraProrationBase() , 0.01 );
		    org.junit.Assert.assertEquals( 1200.00 + 1000.00 / 6, s.getCommonContingenciesBase() , 0.01 );
		    org.junit.Assert.assertEquals( 2200.00 , s.getTotalPayment() , 0.05 );
		    System.out.println( s.getStartDate() + " -. " + s.getExtraProrationBase() + ", " + s.getTotalPayment());
		});
	}

	@Test
	public void testExtrasAtSalaryXVI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");
		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);

		Date contractDate = getFirstDayOfYear(getToday());

		ContractRecord contract = newContract(
				aonContext
				,contractDate
				,new String[] {} 
				,new String[] {} 
				,null);
		//@formatter:off
		
		addPayment(aonContext, contract, contractDate, null, conceptSalarioBase, "SALARIO BASE", "1000.00 * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0001);
		addPayment(aonContext, contract, contractDate, null, conceptPlusSalarial, "PLUS SALARIAL", "200.00 * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0001);
		
		ContractPaymentRecord pagaNavidad = addPayment(aonContext, contract, contractDate, null, conceptPagaExtra, "[90] PAGA EXTRA DICIEMBRE", "SI(MES(INICIO_NOMINA)>6,MENSUALIDAD*2,0.00)", "_P", "_P", PaymentType.CRA_0004, (byte)Month.DECEMBER.ordinal());
		ContractPaymentRecord pagaVerano = addPayment(aonContext, contract, contractDate, null, conceptPagaExtra, "[90] PAGA EXTRA VERANO", "SI(MES(INICIO_NOMINA)<7,MENSUALIDAD*2,0.00)", "_P", "_P", PaymentType.CRA_0004, (byte)Month.JUNE.ordinal());
		
		Date startDate , endDate , issueDate ;
		
		for ( startDate = getFirstDayOfYear(getToday()); get(startDate, Calendar.MONTH) < 5; startDate = add(startDate, Calendar.MONTH,1)) {
		    issueDate  = endDate = getLastDayOfMonth(startDate);
		    JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<>(connection);
		    ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, issueDate, contract);
		    new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		    jooqSalaryBuilder.execute();
		}
		
		AON.getSalaries(aonContext, p -> p.getContractProperty().eq(contract.getId()))
		.forEach( s -> {
		    org.junit.Assert.assertEquals( 1200.00 , s.getTotalPayment() , 0.00 );
		    org.junit.Assert.assertEquals( 1200.00 / 6, s.getExtraProrationBase() , 0.00 );
		    org.junit.Assert.assertEquals( 1200.00 * 7 / 6, s.getCommonContingenciesBase() , 0.00 );
		    System.out.println( s.getStartDate() + " -. " + s.getExtraProrationBase() );
		});

		{
        		issueDate  = endDate = getLastDayOfMonth(startDate);
        		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<>(connection);
        		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, issueDate, contract);
        		new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
        		jooqSalaryBuilder.execute();
		}
		
		Date juneStartDate = startDate;
		AON.getSalaries(aonContext, p -> p.getContractProperty().eq(contract.getId()).and(p.getStartDateProperty().eq(juneStartDate)) ) 
		.forEach( s -> {
		    org.junit.Assert.assertEquals( 2400.00 , s.getTotalPayment() , 0.00 );
		    org.junit.Assert.assertEquals( 1200.00 / 6, s.getExtraProrationBase() , 0.00 );
		    org.junit.Assert.assertEquals( 1200.00 * 7 / 6, s.getCommonContingenciesBase() , 0.00 );
		    System.out.println( s.getStartDate() + " -. " + s.getExtraProrationBase() + ", " + s.getTotalPayment());
		});
		
		
		//pagaVerano.setDescription("[90] PAGA EXTRA VERANO");
		pagaVerano.setExpression("SI(MES(INICIO_NOMINA)<7,(1000.00 * DIAS_TRABAJADOS / DIAS_MES)*2,0.00) ");
		pagaVerano.update();
		
		//pagaNavidad.setDescription("[90] PAGA EXTRA NAVIDAD");
		pagaNavidad.setExpression("SI(MES(INICIO_NOMINA)>6,(1000.00 * DIAS_TRABAJADOS / DIAS_MES)*2,0.00)");
		pagaNavidad.update();


		for ( startDate = add(startDate, Calendar.MONTH,1) ; get(startDate, Calendar.MONTH) < 11; startDate = add(startDate, Calendar.MONTH,1)) {
		    issueDate  = endDate = getLastDayOfMonth(startDate);
		    JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<>(connection);
		    ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, issueDate, contract);
		    new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		    jooqSalaryBuilder.execute();
		}
		
		AON.getSalaries(aonContext, p -> p.getContractProperty().eq(contract.getId()).and(p.getStartDateProperty().gt(juneStartDate)))
		.forEach( s -> {
		    org.junit.Assert.assertEquals( 1200.00 , s.getTotalPayment() , 0.00 );
		    org.junit.Assert.assertEquals( 1000.00 / 6, s.getExtraProrationBase() , 0.01 );
		    org.junit.Assert.assertEquals( 1200.00 + 1000.00 / 6, s.getCommonContingenciesBase() , 0.01 );
		    System.out.println( s.getStartDate() + " -. " + s.getExtraProrationBase() );
		});

		pagaNavidad.setDescription("[90] PAGA EXTRA NAVIDAD");
		//pagaNavidad.setExpression("SI(MES(INICIO_NOMINA)>6,(1000.00 * DIAS_TRABAJADOS / DIAS_MES)*2,0.00)");
		pagaNavidad.update();
		{
        		issueDate  = endDate = getLastDayOfMonth(startDate);
        		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<>(connection);
        		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, issueDate, contract);
        		new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
        		jooqSalaryBuilder.execute();
		}

		Date decemberStartDate = startDate;
		AON.getSalaries(aonContext, p -> p.getContractProperty().eq(contract.getId()).and(p.getStartDateProperty().eq(decemberStartDate)) ) 
		.forEach( s -> {
		    org.junit.Assert.assertEquals( 1000.00 / 6, s.getExtraProrationBase() , 0.01 );
		    org.junit.Assert.assertEquals( 1200.00 + 1000.00 / 6, s.getCommonContingenciesBase() , 0.01 );
		    org.junit.Assert.assertEquals( 2200.00 , s.getTotalPayment() , 0.05 );
		    System.out.println( s.getStartDate() + " -. " + s.getExtraProrationBase() + ", " + s.getTotalPayment());
		});
	}

	@Test
	public void testExtrasAtSalaryXVII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);

		Date contractDate = getFirstDayOfYear(getToday());

		ContractRecord contract = newContract(
				aonContext
				,contractDate
				,new String[] {} 
				,new String[] {} 
				,null);
		//@formatter:off
		
		addPayment(aonContext, contract, contractDate, null, conceptSalarioBase, "SALARIO BASE", "1200.00 * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0001);
		
		ContractPaymentRecord pagaNavidad = addPayment(aonContext, contract, contractDate, null, conceptPagaExtra, "PAGA EXTRA", "SALARIO_BASE", "_P", "_P", PaymentType.CRA_0004, (byte)Month.DECEMBER.ordinal());
		ContractPaymentRecord pagaVerano = addPayment(aonContext, contract, contractDate, null, conceptPagaExtra, "PAGA EXTRA", "SALARIO_BASE", "_P", "_P", PaymentType.CRA_0004, (byte)Month.JUNE.ordinal());
		
		Date startDate , endDate , issueDate ;
		
		for ( startDate = getFirstDayOfYear(getToday()); get(startDate, Calendar.MONTH) < 5; startDate = add(startDate, Calendar.MONTH,1)) {
		    issueDate  = endDate = getLastDayOfMonth(startDate);
		    JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<>(connection);
		    ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, issueDate, contract);
		    new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		    jooqSalaryBuilder.execute();
		}
		
		AON.getSalaries(aonContext, p -> p.getContractProperty().eq(contract.getId()))
		.forEach( s -> {
		    org.junit.Assert.assertEquals( 1200.00 , s.getTotalPayment() , 0.00 );
		    org.junit.Assert.assertEquals( 1200.00 / 6, s.getExtraProrationBase() , 0.00 );
		    org.junit.Assert.assertEquals( 1200.00 + 1200.00/6.00, s.getCommonContingenciesBase() , 0.00 );
		    System.out.println( s.getStartDate() + " -. " + s.getExtraProrationBase()  + "," + s.getTotalPayment());
		});

		{
        		issueDate  = endDate = getLastDayOfMonth(startDate);
        		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<>(connection);
        		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, issueDate, contract);
        		new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
        		jooqSalaryBuilder.execute();
		}
		
		Date juneStartDate = startDate;
		AON.getSalaries(aonContext, p -> p.getContractProperty().eq(contract.getId()).and(p.getStartDateProperty().eq(juneStartDate)) ) 
		.forEach( s -> {
		    org.junit.Assert.assertEquals( 1800.00 , s.getTotalPayment() , 0.00 );
		    org.junit.Assert.assertEquals( 1200.00 / 6, s.getExtraProrationBase() , 0.00 );
		    org.junit.Assert.assertEquals( 1200.00 + 1200.00/6.00, s.getCommonContingenciesBase() , 0.00 );
		    System.out.println( s.getStartDate() + " -. " + s.getExtraProrationBase() + ", " + s.getTotalPayment());
		});
		
		

		for ( startDate = add(startDate, Calendar.MONTH,1) ; get(startDate, Calendar.MONTH) < 11; startDate = add(startDate, Calendar.MONTH,1)) {
		    issueDate  = endDate = getLastDayOfMonth(startDate);
		    JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<>(connection);
		    ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, issueDate, contract);
		    new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
		    jooqSalaryBuilder.execute();
		}
		
		AON.getSalaries(aonContext, p -> p.getContractProperty().eq(contract.getId()).and(p.getStartDateProperty().gt(juneStartDate)))
		.forEach( s -> {
		    org.junit.Assert.assertEquals( 1200.00 , s.getTotalPayment() , 0.00 );
		    org.junit.Assert.assertEquals( 1200.00 / 6, s.getExtraProrationBase() , 0.01 );
		    org.junit.Assert.assertEquals( 1200.00 + 1200.00/6.00, s.getCommonContingenciesBase() , 0.01 );
		    System.out.println( s.getStartDate() + " -. " + s.getExtraProrationBase() );
		});

		{
        		issueDate  = endDate = getLastDayOfMonth(startDate);
        		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<>(connection);
        		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, issueDate, contract);
        		new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
        		jooqSalaryBuilder.execute();
		}

		Date decemberStartDate = startDate;
		AON.getSalaries(aonContext, p -> p.getContractProperty().eq(contract.getId()).and(p.getStartDateProperty().eq(decemberStartDate)) ) 
		.forEach( s -> {
		    org.junit.Assert.assertEquals( 1200.00 / 6, s.getExtraProrationBase() , 0.01 );
		    org.junit.Assert.assertEquals( 1200.00 + 1200.00 / 6, s.getCommonContingenciesBase() , 0.01 );
		    org.junit.Assert.assertEquals( 1200.00 + 1200.00 , s.getTotalPayment() , 0.05 );
		    System.out.println( s.getStartDate() + " -. " + s.getExtraProrationBase() + ", " + s.getTotalPayment());
		});
		
		AON.deleteSalaries(aonContext, p -> p.getContractProperty().eq(contract.getId()).and(p.getStartDateProperty().eq(decemberStartDate)) );
		pagaNavidad.setDescription("PAGA EXTRA NAVIDAD");
		pagaNavidad.update();
		{
    		issueDate  = endDate = getLastDayOfMonth(startDate);
    		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<>(connection);
    		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, issueDate, contract);
    		new SmartContractSalaryCalculator<ISalary>(jooqSalaryBuilder).calculate(ctx);
    		jooqSalaryBuilder.execute();
		}

		AON.getSalaries(aonContext, p -> p.getContractProperty().eq(contract.getId()).and(p.getStartDateProperty().eq(decemberStartDate)) ) 
		.forEach( s -> {
		    org.junit.Assert.assertEquals( 1200.00 / 6, s.getExtraProrationBase() , 0.01 );
		    org.junit.Assert.assertEquals( 1200.00 + 1200.00 / 6, s.getCommonContingenciesBase() , 0.01 );
		    org.junit.Assert.assertEquals( 1200.00 + 1200.00 , s.getTotalPayment() , 0.05 );
		    System.out.println( s.getStartDate() + " -. " + s.getExtraProrationBase() + ", " + s.getTotalPayment());
		});
	}

	@Test
	public void testExtrasAtSalaryIT() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");
		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "/*VERANO*/1100.00 * DIAS_TRABAJADOS / DIAS_MES";
							this.month = Month.JUNE;
							this.start = "01/01";
							this.end = "30/06";
							this.issue = "30/06";
						}
					}, 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "/*NAVIDAD*/1100.00 * DIAS_TRABAJADOS / DIAS_MES";
							this.month = Month.DECEMBER;
							this.start = "01/07";
							this.end = "31/12";
							this.issue = "31/12";
						}
					}, 
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "1000.00 * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "100.00 * DIAS_TRABAJADOS/DIAS_MES";
							}
						}
				});
		
		
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		
		Date firstDayOfYear = getFirstDayOfYear(getToday());

		ContractRecord contract = newContract(
				aonContext
				,firstDayOfYear
				,new String[] {} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		Date startDate ;
		for ( startDate = firstDayOfYear ;  get(startDate, Calendar.MONTH) < Calendar.JUNE; startDate = add(startDate, Calendar.MONTH, 1)  ) {
			Date endDate = getLastDayOfMonth(startDate);
			JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder )
			.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
			jooqSalaryBuilder.execute();
		}
		
		
		
		// 
		// EXTRA 
		//
		Date endDate = getLastDayOfMonth(startDate);

		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startDate, null, null);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		salary.getSalaryPayments().forEach(p -> System.out.println(p.getDescription() + " = " + p.getAmount() + "(" + p.getQuote() +")"));
		
		org.junit.Assert.assertEquals((1100.00 / 6 * 5 ), (double) salary.getTotalPayment(), 0.01);

		for ( startDate = firstDayOfYear ;  get(startDate, Calendar.MONTH) < Calendar.DECEMBER; startDate = add(startDate, Calendar.MONTH, 1)  ) {
			endDate = getLastDayOfMonth(startDate);
			JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection) {
			};
			new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder )
			.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
			jooqSalaryBuilder.execute();
		}
		
		endDate = getLastDayOfMonth(startDate);

		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		salary.getSalaryPayments().forEach(p -> System.out.println(p.getDescription() + " = " + p.getAmount() + "(" + p.getQuote() +")"));
		
		org.junit.Assert.assertEquals((0.00), (double) salary.getTotalPayment(), 0.01);

	}
	

	@Test
	public void testExtrasAtSalaryITI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");
		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);

		
		Date firstDayOfYear = getFirstDayOfYear(getToday());

		ContractRecord contract = newContract(
				aonContext
				,firstDayOfYear
				,new String[] {
						"1000.00 * DIAS_TRABAJADOS/DIAS_MES",
						"100.00 * DIAS_TRABAJADOS/DIAS_MES",
				} 
				,new String[] {} 
				,null);
		//@formatter:off
		
		addPayment(aonContext, contract, contract.getStartDate(), null, conceptPagaExtra, "PAGA EXTRA VERANO", "/*VERANO*/1100.00 * DIAS_TRABAJADOS / DIAS_MES", "_P", "PRORRATEAR(_P)", CRA_0004, Month.JULY);
		addPayment(aonContext, contract, contract.getStartDate(), null, conceptPagaExtra, "PAGA EXTRA NAVIDAD", "/*NAVIDAD*/1100.00 * DIAS_TRABAJADOS / DIAS_MES", "_P", "PRORRATEAR(_P)", CRA_0004, Month.DECEMBER);

		Date startDate ;
		for ( startDate = firstDayOfYear ;  get(startDate, Calendar.MONTH) < Calendar.JULY; startDate = add(startDate, Calendar.MONTH, 1)  ) {
			Date endDate = getLastDayOfMonth(startDate);
			JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection) {
				public void addPayment(Double amount, Double quote, Double tax, String description, java.util.Date startDate, java.util.Date endDate, IPayment payment, Map<String,ITimedVariable<?>> context) {
					super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
					//System.out.println( "1-." +  description + ", " + amount + ", " + quote );
				};
			};
			new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder )
			.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
			jooqSalaryBuilder.execute();
		}
		
		
		// 
		// EXTRA 
		//
		Date endDate = getLastDayOfMonth(startDate);

		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startDate, null, null);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		salary.getSalaryPayments().forEach(p -> System.out.println(p.getDescription() + " = " + p.getAmount() + "(" + p.getQuote() +")"));
		
		org.junit.Assert.assertEquals((1100.00 / 12 * 6 ), (double) salary.getTotalPayment(), 0.01);

		for (  ;  get(startDate, Calendar.MONTH) < Calendar.DECEMBER; startDate = add(startDate, Calendar.MONTH, 1)  ) {
			endDate = getLastDayOfMonth(startDate);
			JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection) {
				public void addPayment(Double amount, Double quote, Double tax, String description, java.util.Date startDate, java.util.Date endDate, IPayment payment, Map<String,ITimedVariable<?>> context) {
					super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
					//System.out.println( "2-." +  description + ", " + amount + ", " + quote );
				};
			};
			new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder )
			.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
			jooqSalaryBuilder.execute();
		}
		
		AON.getSalaries(aonContext, p -> p.getContractProperty().eq(contract.getId())).forEach( s -> {
			//System.out.println( s.getStartDate() +".." + s.getEndDate());
			//s.getPayments().forEach( p -> System.out.println("\t" + p.getDescription() + " = " + p.getAmount() +"(" + p.getQuote() +")"));
		});
		
		endDate = getLastDayOfMonth(startDate);

		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		salary.getSalaryPayments().forEach(p -> System.out.println(p.getDescription() + " = " + p.getAmount() + "(" + p.getQuote() +")"));
		
		org.junit.Assert.assertEquals((1100.00 / 12 * 6 ), (double) salary.getTotalPayment(), 0.01);

	}

	@Test
	public void testExtrasAtSalaryOverrideI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");
		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "/*VERANO*/1100.00 * DIAS_TRABAJADOS / DIAS_MES";
							this.month = Month.JUNE;
							this.start = "01/01";
							this.end = "30/06";
							this.issue = "30/06";
						}
					}, 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "/*NAVIDAD*/1100.00 * DIAS_TRABAJADOS / DIAS_MES";
							this.month = Month.DECEMBER;
							this.start = "01/07";
							this.end = "31/12";
							this.issue = "31/12";
						}
					}, 
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "1000.00 * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "100.00 * DIAS_TRABAJADOS/DIAS_MES";
							}
						}
				});
		
		
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		
		Date firstDayOfYear = getFirstDayOfYear(getToday());

		ContractRecord contract = newContract(
				aonContext
				,firstDayOfYear
				,new String[] {} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		Date startDate ;
		for ( startDate = firstDayOfYear ;  get(startDate, Calendar.MONTH) < Calendar.JUNE; startDate = add(startDate, Calendar.MONTH, 1)  ) {
			Date endDate = getLastDayOfMonth(startDate);
			JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder )
			.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
			jooqSalaryBuilder.execute();
			
			addData(aonContext, contract, startDate, endDate, "PAGA_EXTRA_30_6", "10.00");
		}
		
		
		
		// 
		// EXTRA 
		//
		Date endDate = getLastDayOfMonth(startDate);
		
		
		Date startIt = add(startDate, Calendar.DAY_OF_MONTH, 5 );
		Date endIt = add(startIt, Calendar.DAY_OF_MONTH, 1 );
		
		addIT(aonContext, contract, LeaveType.OCCUPATIONAL_DISEASE, startIt, endIt, null);
		
		addData(aonContext, contract, startDate, endDate, "PAGA_EXTRA_30_6", "10.00");

		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		salary.getSalaryPayments().forEach(p -> System.out.println(p.getDescription() + " = " + p.getAmount() + "(" + p.getQuote() +")"));
		
		org.junit.Assert.assertEquals((1100.00 / 30 * 28  + 60.00), (double) salary.getTotalPayment(), 0.01);
		
		org.junit.Assert.assertEquals((1100.00 / 30 * 28  + 10.00), (double) salary.getCommonBase(), 0.01);

	}

	@Test
	public void testExtrasAtSalaryOverrideII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");
		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] {},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "1000.00 * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "100.00 * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPagaExtra.getId();
								this.expression = "/*VERANO*/1100.00 * DIAS_TRABAJADOS / DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPagaExtra.getId();
								this.expression = "/*NAVIDAD*/1100.00 * DIAS_TRABAJADOS / DIAS_MES";
							}
						}
				});
		
		
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);

		Date firstDaySeptember = new Date( calendar.getTimeInMillis() );

		ContractRecord contract = newContract(
				aonContext
				,firstDaySeptember
				,new String[] {} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		// September
		Date startDate = firstDaySeptember;
		Date endDate = getLastDayOfMonth(startDate);
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder )
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		jooqSalaryBuilder.execute();
		
		// October
		startDate = add(startDate,Calendar.MONTH,1);
		endDate = getLastDayOfMonth(startDate);
		jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder )
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		jooqSalaryBuilder.execute();
		
		double totalPayment = 
		AON.getSalaries(aonContext,  p -> p.getContractProperty().eq(contract.getId()))
		.collect(Collectors.summingDouble(s -> s.getTotalPayment()));
		org.junit.Assert.assertEquals(1100.00 * ( 7.00/6.00 ) * 2.00, totalPayment, 0.001 );
		
		// November
		startDate = add(startDate,Calendar.MONTH,1);
		addPayment(
				aonContext, 
				contract, 
				startDate, 
				null, 
				conceptPagaExtra, 
				"PAGA EXTRA NAVIDAD", 
				"/*NAVIDAD*/1100.00 * DIAS_TRABAJADOS / DIAS_MES", 
				"_P", 
				"PRORRATEAR(_P)", 
				CRA_0004,
				(byte)Month.DECEMBER.ordinal());
		addPayment(
				aonContext, 
				contract, 
				startDate, 
				null, 
				conceptPagaExtra, 
				"PAGA EXTRA VERANO", 
				"/*VERANO*/1100.00 * DIAS_TRABAJADOS / DIAS_MES", 
				"_P", 
				"PRORRATEAR(_P)", 
				CRA_0004,
				(byte)Month.JULY.ordinal());
	
		endDate = getLastDayOfMonth(startDate);

		jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder )
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		jooqSalaryBuilder.execute();
		
		Date firstDayNovember = startDate;
		totalPayment = 
		AON.getSalaries(aonContext,  p -> 
			p.getContractProperty().eq(contract.getId())
				.and(p.getStartDateProperty().eq(firstDayNovember)))
		.collect(Collectors.summingDouble(s -> s.getTotalPayment()));
		org.junit.Assert.assertEquals(1100.00, totalPayment, 0.001 );

		// December
		startDate = add(startDate,Calendar.MONTH,1);
		endDate = getLastDayOfMonth(startDate);
		jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder )
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		jooqSalaryBuilder.execute();

		Date firstDayDecember = startDate;
		totalPayment = 
		AON.getSalaries(aonContext,  p -> 
			p.getContractProperty().eq(contract.getId())
				.and(p.getStartDateProperty().eq(firstDayDecember)))
		.collect(Collectors.summingDouble(s -> s.getTotalPayment()));
		org.junit.Assert.assertEquals(1100.00 + 1100.00/12.00 * 2.00, totalPayment, 0.001 );
		
		
	}

	@Test
	public void testExtrasAtSalaryWithoutAgreementI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");
		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);

		ContractRecord contract = newContract(
				aonContext
				,new String[] {} 
				,new String[] {} 
				,null);
		//@formatter:off
		
		addPayment(aonContext, contract, conceptSalarioBase, "1000.00 * DIAS_TRABAJADOS/DIAS_MES");
		addPayment(aonContext, contract, conceptPlusSalarial, "100.00 * DIAS_TRABAJADOS/DIAS_MES");
		
		Date extraDate = contract.getStartDate();
		addPayment(aonContext, contract, getFirstDayOfMonth(extraDate), getLastDayOfMonth(extraDate), conceptPagaExtra, "PAGA EXTRAORDINARIA", "SALARIO_BASE", "_P", "_P", PaymentType.CRA_0004, (byte) Month.MARCH.ordinal());
		addPayment(aonContext, contract, getFirstDayOfMonth(extraDate), getLastDayOfMonth(extraDate), conceptPagaExtra, "PAGA EXTRAORDINARIA", "SALARIO_BASE + PLUS_SALARIAL", "_P", "_P", PaymentType.CRA_0004, (byte) Month.JULY.ordinal());
		addPayment(aonContext, contract, getFirstDayOfMonth(extraDate), getLastDayOfMonth(extraDate), conceptPagaExtra, "PAGA EXTRAORDINARIA", "1100.00", "_P", "_P", PaymentType.CRA_0004, (byte) Month.DECEMBER.ordinal());
		extraDate = add(extraDate, MONTH, 1);
		addPayment(aonContext, contract, getFirstDayOfMonth(extraDate), getLastDayOfMonth(extraDate), conceptPagaExtra, "PAGA EXTRAORDINARIA", "SALARIO_BASE", "_P", "_P", PaymentType.CRA_0004, (byte) Month.MARCH.ordinal());
		addPayment(aonContext, contract, getFirstDayOfMonth(extraDate), getLastDayOfMonth(extraDate), conceptPagaExtra, "PAGA EXTRAORDINARIA", "1100.00", "_P", "_P", PaymentType.CRA_0004, (byte) Month.JULY.ordinal());
		addPayment(aonContext, contract, getFirstDayOfMonth(extraDate), getLastDayOfMonth(extraDate), conceptPagaExtra, "PAGA EXTRAORDINARIA", "SALARIO_BASE + PLUS_SALARIAL", "_P", "_P", PaymentType.CRA_0004, (byte) Month.DECEMBER.ordinal());
		extraDate = add(extraDate, MONTH, 1);
		addPayment(aonContext, contract, getFirstDayOfMonth(extraDate), getLastDayOfMonth(extraDate), conceptPagaExtra, "PAGA EXTRAORDINARIA", "SALARIO_BASE", "_P", "_P", PaymentType.CRA_0004, (byte) Month.MARCH.ordinal());
		addPayment(aonContext, contract, getFirstDayOfMonth(extraDate), getLastDayOfMonth(extraDate), conceptPagaExtra, "PAGA EXTRAORDINARIA", "1100.00", "_P", "_P", PaymentType.CRA_0004, (byte) Month.JULY.ordinal());
		addPayment(aonContext, contract, getFirstDayOfMonth(extraDate), getLastDayOfMonth(extraDate), conceptPagaExtra, "PAGA EXTRAORDINARIA", "SALARIO_BASE + PLUS_SALARIAL", "_P", "_P", PaymentType.CRA_0004, (byte) Month.DECEMBER.ordinal());
		extraDate = add(extraDate, MONTH, 1);
		addPayment(aonContext, contract, getFirstDayOfMonth(extraDate), getLastDayOfMonth(extraDate), conceptPagaExtra, "PAGA EXTRAORDINARIA", "SALARIO_BASE", "_P", "_P", PaymentType.CRA_0004, (byte) Month.MARCH.ordinal());
		addPayment(aonContext, contract, getFirstDayOfMonth(extraDate), getLastDayOfMonth(extraDate), conceptPagaExtra, "PAGA EXTRAORDINARIA", "1100.00", "_P", "_P", PaymentType.CRA_0004, (byte) Month.JULY.ordinal());
		addPayment(aonContext, contract, getFirstDayOfMonth(extraDate), getLastDayOfMonth(extraDate), conceptPagaExtra, "PAGA EXTRAORDINARIA", "SALARIO_BASE + PLUS_SALARIAL", "_P", "_P", PaymentType.CRA_0004, (byte) Month.DECEMBER.ordinal());
		extraDate = add(extraDate, MONTH, 1);
		addPayment(aonContext, contract, getFirstDayOfMonth(extraDate), getLastDayOfMonth(extraDate), conceptPagaExtra, "PAGA EXTRAORDINARIA", "SALARIO_BASE", "_P", "_P", PaymentType.CRA_0004, (byte) Month.MARCH.ordinal());
		addPayment(aonContext, contract, getFirstDayOfMonth(extraDate), getLastDayOfMonth(extraDate), conceptPagaExtra, "PAGA EXTRAORDINARIA", "1100.00", "_P", "_P", PaymentType.CRA_0004, (byte) Month.JULY.ordinal());
		addPayment(aonContext, contract, getFirstDayOfMonth(extraDate), getLastDayOfMonth(extraDate), conceptPagaExtra, "PAGA EXTRAORDINARIA", "SALARIO_BASE + PLUS_SALARIAL", "_P", "_P", PaymentType.CRA_0004, (byte) Month.DECEMBER.ordinal());
		extraDate = add(extraDate, MONTH, 1);
		addPayment(aonContext, contract, getFirstDayOfMonth(extraDate), getLastDayOfMonth(extraDate), conceptPagaExtra, "PAGA EXTRAORDINARIA", "SALARIO_BASE", "_P", "_P", PaymentType.CRA_0004, (byte) Month.MARCH.ordinal());
		addPayment(aonContext, contract, getFirstDayOfMonth(extraDate), getLastDayOfMonth(extraDate), conceptPagaExtra, "PAGA EXTRAORDINARIA", "1100.00", "_P", "_P", PaymentType.CRA_0004, (byte) Month.JULY.ordinal());
		addPayment(aonContext, contract, getFirstDayOfMonth(extraDate), getLastDayOfMonth(extraDate), conceptPagaExtra, "PAGA EXTRAORDINARIA", "SALARIO_BASE + PLUS_SALARIAL", "_P", "_P", PaymentType.CRA_0004, (byte) Month.DECEMBER.ordinal());
		extraDate = add(extraDate, MONTH, 1);
		addPayment(aonContext, contract, getFirstDayOfMonth(extraDate), null, conceptPagaExtra, "PAGA EXTRAORDINARIA", "SALARIO_BASE", "_P", "_P", PaymentType.CRA_0004, (byte) Month.MARCH.ordinal());
		addPayment(aonContext, contract, getFirstDayOfMonth(extraDate), null, conceptPagaExtra, "PAGA EXTRAORDINARIA", "SALARIO_BASE + PLUS_SALARIAL", "_P", "_P", PaymentType.CRA_0004, (byte) Month.JULY.ordinal());
		addPayment(aonContext, contract, getFirstDayOfMonth(extraDate), null, conceptPagaExtra, "PAGA EXTRAORDINARIA", "1100.00", "_P", "_P", PaymentType.CRA_0004, (byte) Month.DECEMBER.ordinal());
		

		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));

		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);
		
		// 
		// MARCH
		//
		startDate = add(startDate, Calendar.MONTH, 2);
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 + 1000.00 * 3 / 12.00, salary.getTotalPayment());
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);
		
		// 
		// JULY
		//
		startDate = add(startDate, Calendar.MONTH, 4);
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 + 1100.00 * 7 / 12.00, salary.getTotalPayment(), 10.00);
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);

		// 
		// DECEMBER
		//
		startDate = add(startDate, Calendar.MONTH, 5);
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1100.00 + 1100.00, salary.getTotalPayment(),  10.00);
		Assert.assertEquals(ContextVariable.CGC_BASE.getName(), 1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA);
	}

	@Test
	public void testAnualExtrasAtSalaryWithoutAgreementI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE", PaymentType.CRA_0001);
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL", PaymentType.CRA_0001);
		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);


		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.JUNE);
		calendar.set(DAY_OF_MONTH, 1);
		Date contractDate = new Date(calendar.getTimeInMillis());

		ContractRecord contract = newContract(
				aonContext
				,new String[] {} 
				,new String[] {} 
				,null);
		//@formatter:off
		
		addPayment(aonContext, contract, conceptSalarioBase, "1000.00 * DIAS_TRABAJADOS/DIAS_MES");
		addPayment(aonContext, contract, conceptPlusSalarial, "100.00 * DIAS_TRABAJADOS/DIAS_MES");
		
		addPayment(aonContext, contract, contract.getStartDate(), null , conceptPagaExtra, "PAGA EXTRAORDINARIA VERANO", "MENSUALIDAD", "_P", "_P", PaymentType.CRA_0004, (byte) Month.JUNE.ordinal());
		addPayment(aonContext, contract, contract.getStartDate(), null , conceptPagaExtra, "PAGA EXTRAORDINARIA NAVIDAD", "MENSUALIDAD", "_P", "_P", PaymentType.CRA_0004, (byte) Month.DECEMBER.ordinal());
		

		int month ;
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		for ( month = 0 ; month < 10 ; month++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		startDate = add(endDate, DAY_OF_MONTH, 1);
		endDate = getLastDayOfMonth(startDate);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());		
		
		assertEquals(1100.00*2, salary.getTotalPayment(), DELTA);
		
	}

	@Test
	public void testBiAnualExtrasAtSalaryWithoutAgreementI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE", PaymentType.CRA_0001);
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL", PaymentType.CRA_0001);
		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);


		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.JUNE);
		calendar.set(DAY_OF_MONTH, 1);
		Date contractDate = new Date(calendar.getTimeInMillis());

		ContractRecord contract = newContract(
				aonContext
				,new String[] {} 
				,new String[] {} 
				,null);
		//@formatter:off
		
		addPayment(aonContext, contract, conceptSalarioBase, "1000.00 * DIAS_TRABAJADOS/DIAS_MES");
		addPayment(aonContext, contract, conceptPlusSalarial, "100.00 * DIAS_TRABAJADOS/DIAS_MES");
		
		addPayment(aonContext, contract, contract.getStartDate(), null , conceptPagaExtra, "PAGA EXTRAORDINARIA VERANO", "SI(MES(INICIO)< 7,MENSUALIDAD*2, 0.00)", "_P", "_P", PaymentType.CRA_0004, (byte) Month.JUNE.ordinal());
		addPayment(aonContext, contract, contract.getStartDate(), null , conceptPagaExtra, "PAGA EXTRAORDINARIA NAVIDAD", "SI(MES(INICIO) > 6,MENSUALIDAD*2, 0.00)", "_P", "_P", PaymentType.CRA_0004, (byte) Month.DECEMBER.ordinal());
		

		int month ;
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		for ( month = 0 ; month < 11 ; month++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());		
		
		assertEquals(1100.00*2, Math.round(salary.getTotalPayment()), DELTA);
		assertEquals(1100.00/6.00, salary.getExtraPayProration(), DELTA);
		
	}

	@Test
	public void testBiAnualExtrasAtSalaryWithoutAgreementII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE", PaymentType.CRA_0001);
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL", PaymentType.CRA_0001);
		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);


		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.JUNE);
		calendar.set(DAY_OF_MONTH, 1);
		Date contractDate = new Date(calendar.getTimeInMillis());

		ContractRecord contract = newContract(
				aonContext
				,new String[] {} 
				,new String[] {} 
				,null);
		//@formatter:off
		
		addPayment(aonContext, contract, conceptSalarioBase, "1000.00 * DIAS_TRABAJADOS/DIAS_MES");
		addPayment(aonContext, contract, conceptPlusSalarial, "100.00 * DIAS_TRABAJADOS/DIAS_MES");
		
		addPayment(aonContext, contract, contract.getStartDate(), null , conceptPagaExtra, "PAGA EXTRAORDINARIA VERANO", "SI(MES(INICIO)< 7,MENSUALIDAD*2, 0.00)", "_P", "_P", PaymentType.CRA_0004, (byte) Month.JUNE.ordinal());
		addPayment(aonContext, contract, contract.getStartDate(), null , conceptPagaExtra, "PAGA EXTRAORDINARIA NAVIDAD", "SI(MES(INICIO) > 6,MENSUALIDAD*2, 0.00)", "_P", "_P", PaymentType.CRA_0004, (byte) Month.DECEMBER.ordinal());
		

		int month ;
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		for ( month = 0 ; month < 5 ; month++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());		
		
		assertEquals(startDate, salary.getStartDate());
		assertEquals(1100.00*2, Math.round(salary.getTotalPayment()), DELTA);
		assertEquals(1100.00/6.00, salary.getExtraPayProration(), DELTA);
		
	}


	@Test
	public void testBiAnualExtrasAtSalaryWithoutAgreementIII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE", PaymentType.CRA_0001);
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL", PaymentType.CRA_0001);
		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);


		Calendar calendar = Calendar.getInstance();
		
		calendar.set(MONTH, Calendar.MARCH);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(DAY_OF_MONTH, 1);
		Date contractDate = new Date(calendar.getTimeInMillis());

		ContractRecord contract = newContract(
				aonContext
				,contractDate
				,new String[] {} 
				,new String[] {} 
				,null);
		//@formatter:off
		
		addPayment(aonContext, contract, conceptSalarioBase, "1000.00 * DIAS_TRABAJADOS/DIAS_MES");
		addPayment(aonContext, contract, conceptPlusSalarial, "100.00 * DIAS_TRABAJADOS/DIAS_MES");
		
		addPayment(aonContext, contract, contract.getStartDate(), null , conceptPagaExtra, "PAGA EXTRAORDINARIA VERANO", "SI(MES(INICIO)< 7,MENSUALIDAD*2, 0.00)", "_P", "_P", PaymentType.CRA_0004, (byte) Month.JUNE.ordinal());
		addPayment(aonContext, contract, contract.getStartDate(), null , conceptPagaExtra, "PAGA EXTRAORDINARIA NAVIDAD", "SI(MES(INICIO) > 6,MENSUALIDAD*2, 0.00)", "_P", "_P", PaymentType.CRA_0004, (byte) Month.DECEMBER.ordinal());
		

		int month ;
		Date startDate = getFirstDayOfMonth(contractDate);
		Date endDate = getLastDayOfMonth(startDate);
		for ( month = 0 ; month < 9 ; month++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());		
		
		assertEquals(1100.00*2, Math.round(salary.getTotalPayment()), DELTA);
		assertEquals(1100.00/6.00, salary.getExtraPayProration(), DELTA);
		
	}
	
	@Test
	public void testBiAnualExtrasAtSalaryWithoutAgreementIV() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE", PaymentType.CRA_0001);
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL", PaymentType.CRA_0001);
		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);


		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.JUNE);
		calendar.set(DAY_OF_MONTH, 1);
		Date contractDate = new Date(calendar.getTimeInMillis());

		ContractRecord contract = newContract(
				aonContext
				,new String[] {} 
				,new String[] {} 
				,null);
		//@formatter:off
		
		addPayment(aonContext, contract, conceptSalarioBase, "1000.00 * DIAS_TRABAJADOS/DIAS_MES");
		addPayment(aonContext, contract, conceptPlusSalarial, "100.00 * DIAS_TRABAJADOS/DIAS_MES");
		
		addPayment(aonContext, contract, contract.getStartDate(), null , conceptPagaExtra, "PAGA EXTRAORDINARIA VERANO", "SI(MES(INICIO)< 7,MENSUALIDAD*2, 0.00)", "_P", "_P", PaymentType.CRA_0004, (byte) Month.JUNE.ordinal());
		addPayment(aonContext, contract, contract.getStartDate(), null , conceptPagaExtra, "PAGA EXTRAORDINARIA NAVIDAD", "SI(MES(INICIO) > 6,MENSUALIDAD*2, 0.00)", "_P", "_P", PaymentType.CRA_0004, (byte) Month.DECEMBER.ordinal());
		

		int month ;
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		for ( month = 0 ; month <= 5 ; month++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		
		endDate = add(startDate, DAY_OF_MONTH, -1);
		startDate = getFirstDayOfMonth(endDate);

		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());		
		
		assertEquals(startDate, salary.getStartDate());
		assertEquals(1100.00*2, Math.round(salary.getTotalPayment()), DELTA);
		assertEquals(1100.00/6.00, salary.getExtraPayProration(), DELTA);
		
	}



	@Test
	public void testBiAnualExtrasAtSalaryWithoutAgreementV() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE", PaymentType.CRA_0001);
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL", PaymentType.CRA_0001);
		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);


		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.JUNE);
		calendar.set(DAY_OF_MONTH, 1);
		Date contractDate = new Date(calendar.getTimeInMillis());

		ContractRecord contract = newContract(
				aonContext
				,new String[] {} 
				,new String[] {} 
				,null);
		//@formatter:off
		
		addPayment(aonContext, contract, conceptSalarioBase, "1000.00 * DIAS_TRABAJADOS/DIAS_MES");
		addPayment(aonContext, contract, conceptPlusSalarial, "100.00 * DIAS_TRABAJADOS/DIAS_MES");
		
		addPayment(aonContext, contract, contract.getStartDate(), null , conceptPagaExtra, "PAGA EXTRAORDINARIA VERANO", "SI(MES(INICIO)< 7,MENSUALIDAD*2, 0.00)", "_P", "_P", PaymentType.CRA_0004, (byte) Month.JUNE.ordinal());
		addPayment(aonContext, contract, contract.getStartDate(), null , conceptPagaExtra, "PAGA EXTRAORDINARIA NAVIDAD", "SI(MES(INICIO) > 6,MENSUALIDAD*2, 0.00)", "_P", "_P", PaymentType.CRA_0004, (byte) Month.DECEMBER.ordinal());
		

		int month ;
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		for ( month = 0 ; month <= 11 ; month++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		
		endDate = add(startDate, DAY_OF_MONTH, -1);
		startDate = getFirstDayOfMonth(endDate);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());		
		
		assertEquals(1100.00*2, Math.round(salary.getTotalPayment()), DELTA);
		assertEquals(1100.00/6.00, salary.getExtraPayProration(), DELTA);
		
	}

	@Test
	public void testBiAnualExtrasAtSalaryWithoutAgreementVI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE", PaymentType.CRA_0001);
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL", PaymentType.CRA_0001);
		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);


		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.JUNE);
		calendar.set(DAY_OF_MONTH, 1);
		Date contractDate = new Date(calendar.getTimeInMillis());

		ContractRecord contract = newContract(
				aonContext
				,new String[] {} 
				,new String[] {} 
				,null);
		//@formatter:off
		
		addPayment(aonContext, contract, conceptSalarioBase, "1000.00 * DIAS_TRABAJADOS/DIAS_MES");
		addPayment(aonContext, contract, conceptPlusSalarial, "100.00 * DIAS_TRABAJADOS/DIAS_MES");
		
		addPayment(aonContext, contract, contract.getStartDate(), null , conceptPagaExtra, "PAGA EXTRAORDINARIA VERANO", "SI(MES(INICIO)< 7,MENSUALIDAD*2, 0.00)", "_P", "_P", PaymentType.CRA_0004, (byte) Month.JUNE.ordinal());
		addPayment(aonContext, contract, contract.getStartDate(), null , conceptPagaExtra, "PAGA EXTRAORDINARIA NAVIDAD", "SI(MES(INICIO) > 6,MENSUALIDAD*2, 0.00)", "_P", "_P", PaymentType.CRA_0004, (byte) Month.DECEMBER.ordinal());
		

		Date endDate = getLastDayOfYear(getToday());
		Date startDate = getFirstDayOfMonth(endDate);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());		
		
		assertEquals(1100.00*2, Math.round(salary.getTotalPayment()), DELTA);
		assertEquals(1100.00/6.00, salary.getExtraPayProration(), DELTA);
		
	}
	
	@Test
	public void testAnualExtrasAtSalaryWithoutAgreementITI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE", PaymentType.CRA_0001);
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL", PaymentType.CRA_0001);
		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);


		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.JUNE);
		calendar.set(DAY_OF_MONTH, 1);
		Date contractDate = new Date(calendar.getTimeInMillis());

		ContractRecord contract = newContract(
				aonContext
				,new String[] {} 
				,new String[] {} 
				,null);
		//@formatter:off
		
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), ContextVariable.MONTH_DAYS, "30.00");
		
		addPayment(aonContext, contract, conceptSalarioBase, "1000.00 * DIAS_TRABAJADOS/DIAS_MES");
		addPayment(aonContext, contract, conceptPlusSalarial, "100.00 * DIAS_TRABAJADOS/DIAS_MES");
		
		addPayment(aonContext, contract, contract.getStartDate(), null , conceptPagaExtra, "PAGA EXTRAORDINARIA VERANO", "MENSUALIDAD", "_P", "_P", PaymentType.CRA_0004, (byte) Month.JUNE.ordinal());
		addPayment(aonContext, contract, contract.getStartDate(), null , conceptPagaExtra, "PAGA EXTRAORDINARIA NAVIDAD", "MENSUALIDAD", "_P", "_P", PaymentType.CRA_0004, (byte) Month.DECEMBER.ordinal());
		

		int month ;
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		for ( month = 0 ; month < 11 ; month++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, add(startDate,Calendar.DAY_OF_MONTH,10), add(startDate,Calendar.DAY_OF_MONTH,20), null);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());		
		
		assertEquals(1100.00/6.00 * 20 / 30, salary.getExtraPayProration(), DELTA);
		assertEquals(1100.00*20/30 + 1100.00*11/12 + 1100.00/12.00*20/30, salary.getTotalPayment(), 0.005);
		
	}

	@Test
	public void testBiAnualExtrasAtSalaryWithoutAgreementITI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE", PaymentType.CRA_0001);
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL", PaymentType.CRA_0001);
		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);


		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.JUNE);
		calendar.set(DAY_OF_MONTH, 1);
		Date contractDate = new Date(calendar.getTimeInMillis());

		ContractRecord contract = newContract(
				aonContext
				,new String[] {} 
				,new String[] {} 
				,null);
		//@formatter:off
		
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), ContextVariable.MONTH_DAYS, "30.00");
		
		addPayment(aonContext, contract, conceptSalarioBase, "1000.00 * DIAS_TRABAJADOS/DIAS_MES");
		addPayment(aonContext, contract, conceptPlusSalarial, "100.00 * DIAS_TRABAJADOS/DIAS_MES");
		
		addPayment(aonContext, contract, contract.getStartDate(), null , conceptPagaExtra, "PAGA EXTRAORDINARIA VERANO", "SI(MES(INICIO)< 7,MENSUALIDAD*2, 0.00)", "_P", "_P", PaymentType.CRA_0004, (byte) Month.JUNE.ordinal());
		addPayment(aonContext, contract, contract.getStartDate(), null , conceptPagaExtra, "PAGA EXTRAORDINARIA NAVIDAD", "SI(MES(INICIO) > 6,MENSUALIDAD*2, 0.00)", "_P", "_P", PaymentType.CRA_0004, (byte) Month.DECEMBER.ordinal());
		

		int month ;
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		for ( month = 0 ; month < 11 ; month++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, add(startDate,Calendar.DAY_OF_MONTH,10), add(startDate,Calendar.DAY_OF_MONTH,20), null);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());		
		
		assertEquals(1100.00/6.00 * 20 / 30, salary.getExtraPayProration(), DELTA);
		assertEquals(1100.00*20/30 + 1100.00*5/6 + 1100.00/6.00*20/30, salary.getTotalPayment(), 0.005);
		
	}

	@Test
	public void testBiAnualExtrasAtSalaryWithoutAgreementERTEI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE", PaymentType.CRA_0001);
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL", PaymentType.CRA_0001);
		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);


		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.JUNE);
		calendar.set(DAY_OF_MONTH, 1);
		Date contractDate = new Date(calendar.getTimeInMillis());

		ContractRecord contract = newContract(
				aonContext
				,new String[] {} 
				,new String[] {} 
				,null);
		//@formatter:off
		
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), ContextVariable.MONTH_DAYS, "30.00");
		
		addPayment(aonContext, contract, conceptSalarioBase, "1000.00 * DIAS_TRABAJADOS/DIAS_MES");
		addPayment(aonContext, contract, conceptPlusSalarial, "100.00 * DIAS_TRABAJADOS/DIAS_MES");
		
		addPayment(aonContext, contract, contract.getStartDate(), null , conceptPagaExtra, "PAGA EXTRAORDINARIA VERANO", "SI(MES(INICIO)< 7,MENSUALIDAD*2, 0.00)", "_P", "_P", PaymentType.CRA_0004, (byte) Month.JUNE.ordinal());
		addPayment(aonContext, contract, contract.getStartDate(), null , conceptPagaExtra, "PAGA EXTRAORDINARIA NAVIDAD", "SI(MES(INICIO) > 6,MENSUALIDAD*2, 0.00)", "_P", "_P", PaymentType.CRA_0004, (byte) Month.DECEMBER.ordinal());
		
		
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		addData(aonContext, contract, add(startDate, Calendar.MONTH,8), getLastDayOfMonth(add(startDate, Calendar.MONTH,9)), ContextVariable.ERE_FACTOR_FORCE_OFF, "1.0");

		int month ;
		for ( month = 0 ; month < 11 ; month++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {

				
				@Override
				public void addPayment(Double amount, Double quote, Double tax, String description,
						java.util.Date startDate, java.util.Date endDate, IPayment payment,
						Map<String, ITimedVariable<?>> context) {
					if ( "PAGA EXTRAORDINARIA NAVIDAD".equals(description) )
						System.out.println(description + " : " + quote + " ( " + amount + ") "  );
					super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				}
			};
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		
		
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());		
		
		assertEquals(1100.00/6.00, salary.getExtraPayProration(), DELTA);
		assertEquals(1100.00 + 1100.00*8/12.00, salary.getTotalPayment(), 0.005);
		
	}	
	@Test
	public void testExtraConstantsI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "SALARIO_BASE + PLUS_SALARIAL + ANTIGUEDAD";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, 
				new Extra() {
					{
						this.expression = "SALARIO_BASE + PLUS_SALARIAL + ANTIGUEDAD";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, 
				});

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()) 
				,new HashMap<String, String>() {
				} 
				,new String[] {
				} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		PaymentConceptRecord salarioBaseConcept = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord plusSalarialConcept = addConcept(aonContext, "PLUS_SALARIAL");
		PaymentConceptRecord antiguedadConcept = addConcept(aonContext, "ANTIGUEDAD");
		
		addData(aonContext, contract, contract.getStartDate(), null, "PLUS_SALARIAL", "100.00 * 1.00");

		addPayment(aonContext, contract, contract.getStartDate(), null, salarioBaseConcept, "SALARIO BASE", "1000.00 * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0000);
		addPayment(aonContext, contract, contract.getStartDate(), null, plusSalarialConcept, "PLUS SALARIAL", "PLUS_SALARIAL", "_P", "_P", PaymentType.CRA_0000);
		addPayment(aonContext, contract, contract.getStartDate(), null, antiguedadConcept, "ANTIGUEDAD", "10.00", "_P", "_P", PaymentType.CRA_0000);
		
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.DECEMBER);
		calendar.set(DAY_OF_MONTH, 15);
		Date issueDate = new Date(calendar.getTimeInMillis());
		int year = calendar.get(Calendar.YEAR);
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord extra = getExtra(aonContext, agreement.getId(), "15/12");
		
		ISalary salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate))
		.getSalary();
		
		Assert.assertEquals(1110.00, salary.getTotalPayment(), DELTA);
		

	}

	@Test
	public void testExtraMismatchVariablesI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "SALARIO_BASE + PLUS_SALARIAL + ANTIGUEDAD";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, 
				new Extra() {
					{
						this.expression = "SALARIO_BASE + PLUS_SALARIAL + ANTIGUEDAD";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, 
				});

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()) 
				,new HashMap<String, String>() {
				} 
				,new String[] {
				} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		PaymentConceptRecord salarioBaseConcept = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord plusSalarialConcept = addConcept(aonContext, "PLUS_SALARIAL");
		PaymentConceptRecord antiguedadConcept = addConcept(aonContext, "ANTIGUEDAD");
		
		addData(aonContext, contract, contract.getStartDate(), null, "ANTIGUEDAD", "10.00");
		addData(aonContext, contract, contract.getStartDate(), null, "PLUS_SALARIAL", "100.00");
		
		
		addPayment(aonContext, contract, contract.getStartDate(), null, salarioBaseConcept, "SALARIO BASE", "1000.00 * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0000);
		addPayment(aonContext, contract, contract.getStartDate(), null, plusSalarialConcept, "PLUS SALARIAL", "PLUS_SALARIAL * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0000);
		addPayment(aonContext, contract, contract.getStartDate(), null, antiguedadConcept, "ANT1GUEDAD", "ANTIGUEDAD  * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0000);
		
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.DECEMBER);
		calendar.set(DAY_OF_MONTH, 15);
		Date issueDate = new Date(calendar.getTimeInMillis());
		int year = calendar.get(Calendar.YEAR);
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord extra = getExtra(aonContext, agreement.getId(), "15/12");
		
		ISalary salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate))
		.getSalary();
		
		Assert.assertEquals(1110.00, salary.getTotalPayment(), DELTA);
		

	}
	
	@Test
	public void testExtraAndGrossI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "666.66";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, 
				new Extra() {
					{
						this.expression = "666.66";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, 
				});

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()) 
				,new HashMap<String, String>() {
				} 
				,new String[] {
						String.format("%s(1000 * %s / %s)", GROSS, WORKED_DAYS , MONTH_DAYS )	
				} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.DECEMBER);
		calendar.set(DAY_OF_MONTH, 15);
		Date issueDate = new Date(calendar.getTimeInMillis());
		int year = calendar.get(Calendar.YEAR);
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord extra = getExtra(aonContext, agreement.getId(), "15/12");
		
		ISalary salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate))
		.getSalary();
		
		Assert.assertEquals(666.66, salary.getTotalPayment(), DELTA);
		

	}

	@Test
	public void testExtraAndLiquidI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = String.format("666.66 * %s / %s", WORKED_DAYS , MONTH_DAYS );
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, 
				new Extra() {
					{
						this.expression = String.format("666.66 * %s / %s", WORKED_DAYS , MONTH_DAYS );
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, 
				});

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()) 
				,new HashMap<String, String>() {
				} 
				,new String[] {
						String.format("%s(1000 * %s / %s)", LIQUID, WORKED_DAYS , MONTH_DAYS )	
				} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.DECEMBER);
		calendar.set(DAY_OF_MONTH, 15);
		Date issueDate = new Date(calendar.getTimeInMillis());
		int year = calendar.get(Calendar.YEAR);
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord extra = getExtra(aonContext, agreement.getId(), "15/12");
		
		ISalary salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate))
		.getSalary();
		
		Assert.assertEquals(666.66, salary.getTotalPayment(), DELTA);
		
		calendar.set(MONTH, Calendar.JULY);
		calendar.set(DAY_OF_MONTH, 1);
		issueDate = new Date(calendar.getTimeInMillis());
		year = calendar.get(Calendar.YEAR);
		extra = getExtra(aonContext, agreement.getId(), "01/07");
		
		salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate))
		.getSalary();
		
		Assert.assertEquals(666.66/2.00, salary.getTotalPayment(), DELTA);

	}


	@Test
	public void testExtraByMonthlyQuoteI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "SALARIO_BASE + PLUS_SALARIAL + ANTIGUEDAD";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, 
				new Extra() {
					{
						this.expression = "(SALARIO_BASE + PLUS_SALARIAL) + ANTIGUEDAD";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, 
				});

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()) 
				,new HashMap<String, String>() {
				} 
				,new String[] {
				} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		PaymentConceptRecord salarioBaseConcept = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord plusSalarialConcept = addConcept(aonContext, "PLUS_SALARIAL");
		PaymentConceptRecord antiguedadConcept = addConcept(aonContext, "ANTIGUEDAD");
		
		addData(aonContext, contract, contract.getStartDate(), null, "PLUS", "100.00");
		
		
		addPayment(aonContext, contract, contract.getStartDate(), null, salarioBaseConcept, "SALARIO BASE", "1000.00 * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0000);
		addPayment(aonContext, contract, contract.getStartDate(), null, plusSalarialConcept, "PLUS SALARIAL", "PLUS * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0000);
		addPayment(aonContext, contract, contract.getStartDate(), null, antiguedadConcept, "ANT1GUEDAD", "10.00  * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0000);
		
		int month ;
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		for ( month = 0 ; month < 12 ; month++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		
		setData(aonContext, contract, "PLUS", "200.00");
		
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.DECEMBER);
		calendar.set(DAY_OF_MONTH, 15);
		Date issueDate = new Date(calendar.getTimeInMillis());
		int year = calendar.get(Calendar.YEAR);
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord extra = getExtra(aonContext, agreement.getId(), "15/12");
		
		ISalary salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate))
		.getSalary();
		
		Assert.assertEquals(1110.00, salary.getTotalPayment(), DELTA);
		

	}
	
	@Test
	public void testExtraByMonthlyQuoteII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "(1000.00 + PLUS + 10) * DIAS_TRABAJADOS / DIAS_MES";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, 
				new Extra() {
					{
						this.expression = "(SALARIO_BASE + PLUS_SALARIAL) + ANTIGUEDAD";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, 
				});

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()) 
				,new HashMap<String, String>() {
				} 
				,new String[] {
				} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		PaymentConceptRecord salarioBaseConcept = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord plusSalarialConcept = addConcept(aonContext, "PLUS_SALARIAL");
		PaymentConceptRecord antiguedadConcept = addConcept(aonContext, "ANTIGUEDAD");
		
		addData(aonContext, contract, contract.getStartDate(), null, "PLUS", "100.00");
		
		
		addPayment(aonContext, contract, contract.getStartDate(), null, salarioBaseConcept, "SALARIO BASE", "1000.00 * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0000);
		addPayment(aonContext, contract, contract.getStartDate(), null, plusSalarialConcept, "PLUS SALARIAL", "PLUS * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0000);
		addPayment(aonContext, contract, contract.getStartDate(), null, antiguedadConcept, "ANT1GUEDAD", "10.00  * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0000);
		
		Date startMaternity = add(getFirstDayOfYear(getToday()), MONTH,2);
		Date endMaternity = getLastDayOfMonth(add(startMaternity, MONTH,4));
		addIT(aonContext, contract, LeaveType.MATERNITY, startMaternity, endMaternity, null);
//		addData(aonContext, contract, startMaternity, endMaternity, ContextVariable.ERE_FACTOR_FORCE_OFF, "1.00");
		
		int month ;
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		for ( month = 0 ; month < 12 ; month++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		
		cleanData(aonContext, contract, "PLUS");
		addData(aonContext, contract, contract.getStartDate(), null, "PLUS", "200.00");
		
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.DECEMBER);
		calendar.set(DAY_OF_MONTH, 15);
		Date issueDate = new Date(calendar.getTimeInMillis());
		int year = calendar.get(Calendar.YEAR);
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord extra = getExtra(aonContext, agreement.getId(), "15/12");
		
		ISalary salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder() {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				System.out.println(description + " = " + amount ) ;
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
			}
		})
		.calculate(getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate))
		.getSalary();
		
		Assert.assertEquals(1110.00 - (1110.00/12 * 5), salary.getTotalPayment(), DELTA);
		

	}

	@Test
	public void testExtraByMonthlyQuoteIII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		PaymentConceptRecord pagaExtraConcept = addConcept(aonContext, "PAGA_EXTRA");

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.concept = pagaExtraConcept.getId();
						this.expression = "(1000.00 + PLUS + 10) * DIAS_TRABAJADOS / DIAS_MES";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, 
				new Extra() {
					{
						this.concept = pagaExtraConcept.getId();
						this.expression = "(SALARIO_BASE + PLUS_SALARIAL) + ANTIGUEDAD";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, 
				});

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()) 
				,new HashMap<String, String>() {
				} 
				,new String[] {
				} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		PaymentConceptRecord salarioBaseConcept = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord plusSalarialConcept = addConcept(aonContext, "PLUS_SALARIAL");
		PaymentConceptRecord antiguedadConcept = addConcept(aonContext, "ANTIGUEDAD");
		
		addData(aonContext, contract, contract.getStartDate(), null, "PLUS", "100.00");
		
		
		addPayment(aonContext, contract, contract.getStartDate(), null, salarioBaseConcept, "SALARIO BASE", "1000.00 * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0000);
		addPayment(aonContext, contract, contract.getStartDate(), null, plusSalarialConcept, "PLUS SALARIAL", "PLUS * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0000);
		addPayment(aonContext, contract, contract.getStartDate(), null, antiguedadConcept, "ANT1GUEDAD", "10.00  * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0000);
		
		
		int month ;
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		for ( month = 0 ; month < 6 ; month++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		
		cleanData(aonContext, contract, "PLUS");
		addData(aonContext, contract, contract.getStartDate(), null, "PLUS", "200.00");
		
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.DECEMBER);
		calendar.set(DAY_OF_MONTH, 15);
		Date issueDate = new Date(calendar.getTimeInMillis());
		int year = calendar.get(Calendar.YEAR);
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord extra = getExtra(aonContext, agreement.getId(), "15/12");
		
		ISalary salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder() {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				System.out.println(description + " = " + amount ) ;
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
			}
		})
		.calculate(getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate))
		.getSalary();
		
		Assert.assertEquals(1110.00/12 * 6 + 1210.00/12 * 6, salary.getTotalPayment(), DELTA);
	

	}

	@Test
	@Ignore("Rollback")
	public void testProrratedBaseI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()) 
				,Collections.emptyMap() 
				,new String[] {} 
				,new String[] {} 
				,null);
		
		PaymentConceptRecord pagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord salarioBase = addConcept(aonContext, "SALARIO_BASE", PaymentType.CRA_0001);
		PaymentConceptRecord plusSalarial = addConcept(aonContext, "PLUS_SALARIAL", PaymentType.CRA_0001);
		
		addPayment(aonContext, contract, salarioBase, "1000.00 *DIAS_TRABAJADOS /DIAS_MES");
		addPayment(aonContext, contract, plusSalarial, "200.00 *DIAS_TRABAJADOS /DIAS_MES");
		addPayment(aonContext, contract, pagaExtra, "PRORRATEAR(MENSUALIDAD)", "_P", PaymentType.CRA_0004);
		addPayment(aonContext, contract, pagaExtra, "PRORRATEAR(MENSUALIDAD)", "_P", PaymentType.CRA_0004);
		//@formatter:off
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		Salary salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract))
		;
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());

		Assert.assertEquals(1200.00 + 1200.00 / 6 , salary.getIrpfBase(), DELTA);
		Assert.assertEquals(1200.00 + 1200.00 / 6 , salary.getCommonBase(), DELTA);
		Assert.assertEquals(1200.00 + 1200.00 / 6 , salary.getRemuneration(), DELTA);
		Assert.assertEquals(1200.00 / 6 , salary.getExtraPayProration(), DELTA);
		
		

	}

	@Test
	public void testProrratedBaseII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()) 
				,new HashMap<String, String>(){
				{
					put("BASE_CGC_MIN", "1050.00");
				}
				}
				,new String[] {} 
				,new String[] {} 
				,null);
		
		PaymentConceptRecord pagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord salarioBase = addConcept(aonContext, "SALARIO_BASE", PaymentType.CRA_0001);
		PaymentConceptRecord plusSalarial = addConcept(aonContext, "PLUS_SALARIAL", PaymentType.CRA_0001);
		
		addPayment(aonContext, contract, pagaExtra, "SALARIO_BASE", "_P", PaymentType.CRA_0004);
		addPayment(aonContext, contract, pagaExtra, "SALARIO_BASE", "_P", PaymentType.CRA_0004);
		addPayment(aonContext, contract, salarioBase, "100.00 *DIAS_TRABAJADOS /DIAS_MES");
		//@formatter:off
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		Salary salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract))
		;
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());

		Assert.assertEquals(100.00 + 100.00 / 6 , salary.getIrpfBase(), DELTA);
		Assert.assertEquals(1050.00 , salary.getCommonBase(), DELTA);
		Assert.assertEquals(100.00 + 100.00 / 6 , salary.getRemuneration(), DELTA);

	}

	@Test
	public void testProrratedBaseIII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()) 
				,new HashMap<String, String>(){
				{
					put("BASE_CGC_MIN", "75.00");
				}
				}
				,new String[] {} 
				,new String[] {} 
				,null);
		
		PaymentConceptRecord pagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord salarioBase = addConcept(aonContext, "SALARIO_BASE", PaymentType.CRA_0001);
		PaymentConceptRecord plusSalarial = addConcept(aonContext, "PLUS_SALARIAL", PaymentType.CRA_0001);
		
		addPayment(aonContext, contract, pagaExtra, "70.00", "_P", PaymentType.CRA_0004);
		addPayment(aonContext, contract, pagaExtra, "70.00", "_P", PaymentType.CRA_0004);
		addPayment(aonContext, contract, salarioBase, "100.00 *DIAS_TRABAJADOS /DIAS_MES");
		//@formatter:off
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		Salary salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract))
		;
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());

		Assert.assertEquals(100.00 + 70.00 / 6 , salary.getIrpfBase(), DELTA);
		Assert.assertEquals(100.00 + 70.00 / 6 , salary.getCommonBase(), DELTA);
		Assert.assertEquals(100.00 + 70.00 / 6 , salary.getRemuneration(), DELTA);

	}

	@Test
	public void testProrratedBaseIV() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()) 
				,new HashMap<String, String>(){
				{
					put("BASE_CGC_MIN", "75.00");
				}
				}
				,new String[] {} 
				,new String[] {} 
				,null);
		
		PaymentConceptRecord pagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord salarioBase = addConcept(aonContext, "SALARIO_BASE", PaymentType.CRA_0001);
		PaymentConceptRecord plusSalarial = addConcept(aonContext, "PLUS_SALARIAL", PaymentType.CRA_0001);
		
		addPayment(aonContext, contract, pagaExtra, "10.00", "_P", PaymentType.CRA_0004);
		addPayment(aonContext, contract, pagaExtra, "10.00", "_P", PaymentType.CRA_0004);
		addPayment(aonContext, contract, salarioBase, "100.00 *DIAS_TRABAJADOS /DIAS_MES");
		//@formatter:off
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		Salary salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract))
		;
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());

		Assert.assertEquals(100.00 + 20.00 , salary.getIrpfBase(), DELTA);
		Assert.assertEquals(100.00 + 20.00, salary.getCommonBase(), DELTA);
		Assert.assertEquals(100.00 + 20.00 , salary.getRemuneration(), DELTA);

	}

	@Test
	public void testProrratedBaseV() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()) 
				,new HashMap<String, String>(){
				{
					put("BASE_CGC_MIN", "1050.00");
				}
				}
				,new String[] {} 
				,new String[] {} 
				,null);
		
		PaymentConceptRecord pagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord salarioBase = addConcept(aonContext, "SALARIO_BASE", PaymentType.CRA_0001);
		PaymentConceptRecord plusSalarial = addConcept(aonContext, "PLUS_SALARIAL", PaymentType.CRA_0001);
		
		addPayment(aonContext, contract, pagaExtra, "SALARIO_BASE/12", "_P", PaymentType.CRA_0004);
		addPayment(aonContext, contract, pagaExtra, "SALARIO_BASE/12", "_P", PaymentType.CRA_0004);
		addPayment(aonContext, contract, salarioBase, "100.00 *DIAS_TRABAJADOS /DIAS_MES");
		//@formatter:off
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		Salary salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract))
		;
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());

		Assert.assertEquals(100.00 + 100.00 / 6 , salary.getIrpfBase(), DELTA);
		Assert.assertEquals(1050.00 , salary.getCommonBase(), DELTA);
		Assert.assertEquals(100.00 + 100.00 / 6 , salary.getRemuneration(), DELTA);

	}

	@Test
	public void testProrratedBaseVI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()) 
				,new HashMap<String, String>(){
				{
					put("BASE_CGC_MIN", "1050.00");
				}
				}
				,new String[] {} 
				,new String[] {} 
				,null);
		
		PaymentConceptRecord pagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord salarioBase = addConcept(aonContext, "SALARIO_BASE", PaymentType.CRA_0001);
		PaymentConceptRecord plusSalarial = addConcept(aonContext, "PLUS_SALARIAL", PaymentType.CRA_0001);
		
		addPayment(aonContext, contract, pagaExtra, "SALARIO_BASE/6", "_P", PaymentType.CRA_0004);
		addPayment(aonContext, contract, salarioBase, "100.00 *DIAS_TRABAJADOS /DIAS_MES");
		//@formatter:off
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		Salary salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract))
		;
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());

		Assert.assertEquals(100.00 + 100.00 / 6 , salary.getIrpfBase(), DELTA);
		Assert.assertEquals(1050.00 , salary.getCommonBase(), DELTA);
		Assert.assertEquals(100.00 + 100.00 / 6 , salary.getRemuneration(), DELTA);

	}

	@Test
	@Ignore("Not Yet")
	public void testProrratedBaseVII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()) 
				,new HashMap<String, String>(){
				{
					put("BASE_CGC_MIN", "1050.00");
				}
				}
				,new String[] {} 
				,new String[] {} 
				,null);
		
		PaymentConceptRecord pagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord salarioBase = addConcept(aonContext, "SALARIO_BASE", PaymentType.CRA_0001);
		PaymentConceptRecord plusSalarial = addConcept(aonContext, "PLUS_SALARIAL", PaymentType.CRA_0001);
		
		addPayment(aonContext, contract, contract.getStartDate(), null, pagaExtra, "PAGA JULIO", "SALARIO_BASE/12", "_P", "_P", PaymentType.CRA_0004, (byte)6);
		addPayment(aonContext, contract, contract.getStartDate(), null, pagaExtra, "PAGA NAVIDAD", "SALARIO_BASE/12", "_P", "_P", PaymentType.CRA_0004, (byte)11);
		addPayment(aonContext, contract, salarioBase, "100.00 *DIAS_TRABAJADOS /DIAS_MES");
		//@formatter:off
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		Salary salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract))
		;
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());

		Assert.assertEquals(100.00 , salary.getIrpfBase(), DELTA);
		Assert.assertEquals(1050.00 , salary.getCommonBase(), DELTA);
		Assert.assertEquals(100.00, salary.getRemuneration(), DELTA);
		Assert.assertEquals(100.00/6.00, salary.getExtraPayProration(), DELTA);

	}

	@Test
	public void testProrratedBaseVIII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		
		addSystemData(aonContext, getFirstDayOfYear(getToday()), null, new HashMap<String, String>(){
			{
				put("BASE_CGC_MIN", "38.89.00 * DIAS_NOMINA");
			}
			});
		
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()) 
				,new HashMap<String, String>(){
				{
				}
				}
				,new String[] {} 
				,new String[] {} 
				,null);
		
		PaymentConceptRecord pagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord salarioBase = addConcept(aonContext, "SALARIO_BASE", PaymentType.CRA_0001);
		PaymentConceptRecord plusSalarial = addConcept(aonContext, "PLUS_SALARIAL", PaymentType.CRA_0001);
		
		addPayment(aonContext, contract, salarioBase, "1125.90 *DIAS_TRABAJADOS /DIAS_MES");
		addPayment(aonContext, contract, plusSalarial, "PLUS_MENSUAL *DIAS_TRABAJADOS /DIAS_MES");
		addPayment(aonContext, contract, pagaExtra, "SALARIO_BASE + PLUS_SALARIAL", "_P", PaymentType.CRA_0004);
		addPayment(aonContext, contract, pagaExtra, "SALARIO_BASE + PLUS_SALARIAL", "_P", PaymentType.CRA_0004);
		//@formatter:off
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		Salary salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract))
		;
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());

		Assert.assertEquals(1125.90 + 1125.90 /12 *2 , salary.getIrpfBase(), DELTA);
		Assert.assertEquals(1125.90 + 1125.90 /12 *2 , salary.getCommonBase(), DELTA);
		Assert.assertEquals(1125.90 + 1125.90 /12 *2 , salary.getRemuneration(), DELTA);

	}


	@Test
	@Ignore("Not Yet")
	public void testExtrasWithOutPaymentsI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "SALARIO_BASE + PLUS_SALARIAL";
							this.month = Month.JULY;
							this.start = "01/07 -1";
							this.end = "30/06";
							this.issue = "01/07";
						}
					}, 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "(SALARIO_BASE + PLUS_SALARIAL)";
							this.month = Month.DECEMBER;
							this.start = "01/01";
							this.end = "31/12";
							this.issue = "15/12";
						}
					}, 
				});
		
		Date firstDayOfYear = getFirstDayOfYear(getToday());
		Date contractStartDate = add(firstDayOfYear, DAY_OF_MONTH, - 666);
		
		ContractRecord contract = newContract(
				aonContext
				,contractStartDate
				, new HashMap<String,String>(){
					{
						put("DIAS_MES", "30.00");
					}
				}				
				,category);
		
		addPayment(aonContext, contract, firstDayOfYear, null, conceptSalarioBase, "1000.00 * DIAS_TRABAJADOS/DIAS_MES");
		addPayment(aonContext, contract, firstDayOfYear, null, conceptPlusSalarial, "100.00 * DIAS_TRABAJADOS/DIAS_MES");
		

		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord julyExtra = getExtra(aonContext, agreement.getId(), "01/07");

		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, 6);
		calendar.set(DAY_OF_MONTH, 1);
		Date issueDate = new Date(calendar.getTimeInMillis());
		int year = calendar.get(Calendar.YEAR);
		
		ISQLContractSalaryCalculatorContext extraCtx = getExtraSalaryCalculatorContext(connection, contract, julyExtra, year, issueDate);
		SmartContractSalaryCalculator<Salary> contractSalaryCalculator = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder());
		contractSalaryCalculator.setListener(new Listener() {
		});
		Salary extra = contractSalaryCalculator.calculate(extraCtx);

		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), ( 1000.00 + 100.00 )  , extra.getTotalPayment(), 0.005 );
	}

	@Test
	public void testExtrasWithUnamedI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "SALARIO_BASE + PLUS_SALARIAL";
							this.month = Month.JULY;
							this.start = "01/07 -1";
							this.end = "30/06";
							this.issue = "01/07";
						}
					}, 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "(SALARIO_BASE + PLUS_SALARIAL)";
							this.month = Month.DECEMBER;
							this.start = "01/01";
							this.end = "31/12";
							this.issue = "15/12";
						}
					}, 
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "SALARIO_MENSUAL * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "PLUS_MENSUAL * DIAS_TRABAJADOS/DIAS_MES";
							}
						}
				});
		
		addData(aonContext, category, getFirstDayOfYear(getToday()), null, new HashMap<String,String>(){
			{
				put("PLUS_MENSUAL", "10.00");
				put("SALARIO_MENSUAL", "1000.00");
			}
		});
		

		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.AUGUST);
		calendar.set(DAY_OF_MONTH, 1);
		Date contractStartDate = new Date(calendar.getTimeInMillis()); // 01/08
		
		calendar.set(MONTH, Calendar.NOVEMBER);
		calendar.set(DAY_OF_MONTH, 30);
		Date contractEndDate = new Date(calendar.getTimeInMillis()); // 30/10

		ContractRecord contract = newContract(
				aonContext
				,contractStartDate
				,contractEndDate
				,new HashMap<String,String>(){
					{
						put("DIAS_MES", "30.00");
					}
				}
				,new String [] {}
				,new String [] {}
				,category);
		
		addPayment(aonContext, contract, contractStartDate, null, "100.00 * DIAS_TRABAJADOS / DIAS_MES");

		//@formatter:off
		
		
		calendar.set(MONTH, Calendar.DECEMBER);
		calendar.set(DAY_OF_MONTH, 15);
		Date issueDate = new Date(calendar.getTimeInMillis());
		int year = calendar.get(Calendar.YEAR);

		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord decemberExtra = getExtra(aonContext, agreement.getId(), "15/12");
		ISQLContractSalaryCalculatorContext extraCtx = getExtraSalaryCalculatorContext(connection, contract, decemberExtra, year, issueDate);
		SmartContractSalaryCalculator<Salary> contractSalaryCalculator = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder());
		contractSalaryCalculator.setListener(new Listener() {
		});
		Salary extra = contractSalaryCalculator.calculate(extraCtx);

		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1010.00/12.00 * 4 , extra.getTotalPayment(), 0.005 );
	}

	@Test
	public void testExtrasWithConstantsI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "IMPORTE_PAGA_JULIO";
							this.month = Month.JULY;
							this.start = "01/07 -1";
							this.end = "30/06";
							this.issue = "01/07";
						}
					}, 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "IMPORTE_PAGA_DICIEMBRE";
							this.month = Month.DECEMBER;
							this.start = "01/01";
							this.end = "31/12";
							this.issue = "15/12";
						}
					}, 
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "SALARIO_MENSUAL * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "PLUS_MENSUAL * DIAS_TRABAJADOS/DIAS_MES";
							}
						}
				});
		
		addData(aonContext, category, getFirstDayOfYear(getToday()), null, new HashMap<String,String>(){
			{
				put("PLUS_MENSUAL", "10.00");
				put("SALARIO_MENSUAL", "1000.00");
				put("IMPORTE_PAGA_JULIO", "1333.00");
				put("IMPORTE_PAGA_DICIEMBRE", "1333.00");
			}
		});
		

		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.AUGUST);
		calendar.set(DAY_OF_MONTH, 1);
		Date contractStartDate = new Date(calendar.getTimeInMillis()); // 01/08
		
		calendar.set(MONTH, Calendar.NOVEMBER);
		calendar.set(DAY_OF_MONTH, 30);
		Date contractEndDate = new Date(calendar.getTimeInMillis()); // 30/10

		ContractRecord contract = newContract(
				aonContext
				,contractStartDate
				,contractEndDate
				,new HashMap<String,String>(){
					{
						put("DIAS_MES", "30.00");
					}
				}
				,new String [] {}
				,new String [] {}
				,category);
		
		//@formatter:off
		
		
		for ( Date startDate = contractStartDate ; 
				startDate.before(contractEndDate) ; 
				startDate = add(startDate, Calendar.MONTH,1) ) 
			{
				Date endDate = getLastDayOfMonth(startDate);
				ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
						connection, startDate, endDate, endDate, contract);
				Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
				Assert.assertEquals(ContextVariable.EXTRA_PAY.getName(), 1333.00/12.00 * 2.00 , salary.getExtraPayProration(), 0.005 );
			}

		calendar.set(MONTH, Calendar.DECEMBER);
		calendar.set(DAY_OF_MONTH, 15);
		Date issueDate = new Date(calendar.getTimeInMillis());
		int year = calendar.get(Calendar.YEAR);

		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord decemberExtra = getExtra(aonContext, agreement.getId(), "15/12");
		ISQLContractSalaryCalculatorContext extraCtx = getExtraSalaryCalculatorContext(connection, contract, decemberExtra, year, issueDate);
		SmartContractSalaryCalculator<Salary> contractSalaryCalculator = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder());
		contractSalaryCalculator.setListener(new Listener() {
		});
		Salary extra = contractSalaryCalculator.calculate(extraCtx);

		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1333.00/12.00 * 4 , extra.getTotalPayment(), 0.005 );
	}

	@Test
	public void testExtrasWithConstantsII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "IMPORTE_PAGA_JULIO";
							this.month = Month.JULY;
							this.start = "01/07 -1";
							this.end = "30/06";
							this.issue = "01/07";
						}
					}, 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "IMPORTE_PAGA_DICIEMBRE";
							this.month = Month.DECEMBER;
							this.start = "01/01";
							this.end = "31/12";
							this.issue = "15/12";
						}
					}, 
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "SALARIO_MENSUAL * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "PLUS_MENSUAL * DIAS_TRABAJADOS/DIAS_MES";
							}
						}
				});
		
		addData(aonContext, category, getFirstDayOfYear(getToday()), null, new HashMap<String,String>(){
			{
				put("PLUS_MENSUAL", "10.00");
				put("SALARIO_MENSUAL", "1000.00");
				put("IMPORTE_PAGA_JULIO", "1333.00");
				put("IMPORTE_PAGA_DICIEMBRE", "1333.00");
			}
		});
		

		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.AUGUST);
		calendar.set(DAY_OF_MONTH, 1);
		Date contractStartDate = new Date(calendar.getTimeInMillis()); // 01/08
		
		calendar.set(MONTH, Calendar.AUGUST);
		calendar.set(DAY_OF_MONTH, 31);
		Date contractEndDate = new Date(calendar.getTimeInMillis()); // 31/08

		ContractRecord contract = newContract(
				aonContext
				,contractStartDate
				,contractEndDate
				,new HashMap<String,String>(){
					{
						put("DIAS_MES", "30.00");
					}
				}
				,new String [] {}
				,new String [] {}
				,category);
		
		//@formatter:off
		
		
		for ( Date startDate = contractStartDate ; 
				startDate.before(contractEndDate) ; 
				startDate = add(startDate, Calendar.MONTH,1) ) 
			{
				Date endDate = getLastDayOfMonth(startDate);
				ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
						connection, startDate, endDate, endDate, contract);
				Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
				Assert.assertEquals(ContextVariable.EXTRA_PAY.getName(), 1333.00/12.00 * 2.00 , salary.getExtraPayProration(), 0.005 );
			}

		calendar.set(MONTH, Calendar.DECEMBER);
		calendar.set(DAY_OF_MONTH, 15);
		Date issueDate = new Date(calendar.getTimeInMillis());
		int year = calendar.get(Calendar.YEAR);

		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord decemberExtra = getExtra(aonContext, agreement.getId(), "15/12");
		ISQLContractSalaryCalculatorContext extraCtx = getExtraSalaryCalculatorContext(connection, contract, decemberExtra, year, issueDate);
		SmartContractSalaryCalculator<Salary> contractSalaryCalculator = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder());
		contractSalaryCalculator.setListener(new Listener() {
		});
		Salary extra = contractSalaryCalculator.calculate(extraCtx);

		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1333.00/12.00, extra.getTotalPayment(), 0.005 );
	}

	@Test
	public void testExtrasWithConstantsIII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "IMPORTE_PAGA_JULIO";
							this.month = Month.JULY;
							this.start = "01/07 -1";
							this.end = "30/06";
							this.issue = "01/07";
						}
					}, 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "IMPORTE_PAGA_DICIEMBRE";
							this.month = Month.DECEMBER;
							this.start = "01/01";
							this.end = "31/12";
							this.issue = "15/12";
						}
					}, 
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "SALARIO_MENSUAL * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "PLUS_MENSUAL * DIAS_TRABAJADOS/DIAS_MES";
							}
						}
				});
		
		addData(aonContext, category, getFirstDayOfYear(getToday()), null, new HashMap<String,String>(){
			{
				put("PLUS_MENSUAL", "10.00");
				put("SALARIO_MENSUAL", "1000.00");
				put("IMPORTE_PAGA_JULIO", "1333.00");
				put("IMPORTE_PAGA_DICIEMBRE", "1333.00");
			}
		});
		

		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.AUGUST);
		calendar.set(DAY_OF_MONTH, 1);
		Date contractStartDate = new Date(calendar.getTimeInMillis()); // 01/08
		
		calendar.set(MONTH, Calendar.AUGUST);
		calendar.set(DAY_OF_MONTH, 15);
		Date contractEndDate = new Date(calendar.getTimeInMillis()); // 15/08

		ContractRecord contract = newContract(
				aonContext
				,contractStartDate
				,contractEndDate
				,new HashMap<String,String>(){
					{
						put("DIAS_MES", "30.00");
					}
				}
				,new String [] {}
				,new String [] {}
				,category);
		
		//@formatter:off
		
		
		for ( Date startDate = contractStartDate ; 
				startDate.before(contractEndDate) ; 
				startDate = add(startDate, Calendar.MONTH,1) ) 
			{
				Date endDate = getLastDayOfMonth(startDate);
				ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
						connection, startDate, endDate, endDate, contract);
				Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
				Assert.assertEquals(ContextVariable.EXTRA_PAY.getName(), (1333.00/12.00)/2.00 * 2.00 , salary.getExtraPayProration(), 0.005 );
			}

		calendar.set(MONTH, Calendar.DECEMBER);
		calendar.set(DAY_OF_MONTH, 15);
		Date issueDate = new Date(calendar.getTimeInMillis());
		int year = calendar.get(Calendar.YEAR);

		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord decemberExtra = getExtra(aonContext, agreement.getId(), "15/12");
		ISQLContractSalaryCalculatorContext extraCtx = getExtraSalaryCalculatorContext(connection, contract, decemberExtra, year, issueDate);
		SmartContractSalaryCalculator<Salary> contractSalaryCalculator = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder());
		contractSalaryCalculator.setListener(new Listener() {
		});
		Salary extra = contractSalaryCalculator.calculate(extraCtx);

		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), (1333.00/12.00)/2.00, extra.getTotalPayment(), 0.005 );
	}

	// ------------------------------------------------------------------------
	
	@Test
	public void testNoExtraI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "SALARIO_BASE + PLUS_SALARIAL + ANTIGUEDAD";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, 
				new Extra() {
					{
						this.expression = "SALARIO_BASE + PLUS_SALARIAL + ANTIGUEDAD";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, 
				new Extra() {
					{
						this.expression = "SALARIO_BASE + PLUS_SALARIAL + ANTIGUEDAD";
						this.month = Month.MARCH;
						this.start = "01/01 -1";
						this.end = "31/12 -1";
						this.issue = "31/03";
					}
				}, 
				});

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()) 
				,new HashMap<String, String>() {
				} 
				,new String[] {
				} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		PaymentConceptRecord salarioBaseConcept = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord plusSalarialConcept = addConcept(aonContext, "PLUS_SALARIAL");
		PaymentConceptRecord antiguedadConcept = addConcept(aonContext, "ANTIGUEDAD");
		
		addData(aonContext, contract, contract.getStartDate(), null, "PLUS_SALARIAL", "100.00");

		addPayment(aonContext, contract, contract.getStartDate(), null, salarioBaseConcept, "SALARIO BASE", "1000.00 * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0000);
		addPayment(aonContext, contract, contract.getStartDate(), null, plusSalarialConcept, "PLUS SALARIAL", "PLUS_SALARIAL", "_P", "_P", PaymentType.CRA_0000);
		addPayment(aonContext, contract, contract.getStartDate(), null, antiguedadConcept, "ANTIGUEDAD", "10.00", "_P", "_P", PaymentType.CRA_0000);
		
		
		Date startDate = add(getFirstDayOfYear(getToday()), MONTH,2);
		Date endDate = getLastDayOfMonth(startDate);
		
		Salary salary =
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract))
		;
		
		org.junit.Assert.assertEquals(1110.00, salary.getTotalPayment(), DELTA);
		org.junit.Assert.assertEquals(1110.00/12.00 * 3.00, salary.getExtraPayProration(), DELTA);

	}
	
	
	@Test
	public void testSameMonthExtras() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "/*OTOÑO*/(SALARIO_BASE + PLUS_SALARIAL)";
							this.month = Month.SEPTEMBER;
							this.start = "01/01";
							this.end = "30/09";
							this.issue = "30/09";
						}
					}, 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							//(MES(INICIO_NOMINA) > 9 )?INPUT("(/*user*/SALARIO_BASE + ANTIGUEDAD/**/) * 3.00 / 12.00 ",PAGA_EXTRA_HELP):HIDE()
							this.expression = "/*OTOÑO*/(SALARIO_BASE + PLUS_SALARIAL)";
							this.month = Month.DECEMBER;
							this.start = "01/10";
							this.end = "31/12";
							this.issue = "31/12";
						}
					}, 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "/*NAVIDAD*/(SALARIO_BASE + PLUS_SALARIAL)";
							this.month = Month.DECEMBER;
							this.start = "01/07";
							this.end = "31/12";
							this.issue = "21/12";
						}
					}, 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "/*VERANO*/(SALARIO_BASE + PLUS_SALARIAL)";
							this.month = Month.JUNE;
							this.start = "01/01";
							this.end = "30/06";
							this.issue = "30/06";
						}
					}, 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "/*MARZO*/(SALARIO_BASE + PLUS_SALARIAL)";
							this.month = Month.MARCH;
							this.start = "01/01 -1";
							this.end = "31/12 -1 ";
							this.issue = "31/03";
						}
					}, 
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "SALARIO_MENSUAL * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "PLUS_MENSUAL * DIAS_LABORALES";
							}
						}
				});
		
		addData(aonContext, category, getFirstDayOfYear(getToday()), null, new HashMap<String,String>(){
			{
				put("PLUS_MENSUAL", "10.00");
				put("SALARIO_MENSUAL", "1000.00");
			}
		});
		

		Date firstDayOfYear = getFirstDayOfYear(getToday()); 
		Date contractStartDate = firstDayOfYear; // 01/01

		ContractRecord contract = newContract(
				aonContext
				,contractStartDate
				, new HashMap<String,String>(){
					{
						put("DIAS_MES", "30.00");
					}
				}				
				,category);
		
		
		Date lastDayOfYear = getLastDayOfYear(contractStartDate);
		for ( Date startDate = firstDayOfYear ; 
				startDate.before(lastDayOfYear) ; 
				startDate = add(startDate, Calendar.MONTH,1) ) 
		{
			Date endDate = getLastDayOfMonth(startDate);
			addData(aonContext, contract, startDate, endDate, "DIAS_LABORALES", "15.00");
		}
		
		for ( Date startDate = firstDayOfYear ; 
				startDate.before(lastDayOfYear) ; 
				startDate = add(startDate, Calendar.MONTH,1) ) 
		{
			Date endDate = getLastDayOfMonth(startDate);
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);

			JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			new ContractSalaryCalculator<Salary>(jooqSalaryBuilder).calculate(ctx);
			jooqSalaryBuilder.execute();
		}

		//@formatter:off
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.DECEMBER);
		calendar.set(DAY_OF_MONTH, 21);
		Date issueDate = new Date(calendar.getTimeInMillis());
		int year = calendar.get(Calendar.YEAR);

		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord decemberExtra = getExtra(aonContext, agreement.getId(), "21/12");
		ISQLContractSalaryCalculatorContext extraCtx = getExtraSalaryCalculatorContext(connection, contract, decemberExtra, year, issueDate);
		SmartContractSalaryCalculator<Salary> contractSalaryCalculator = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder());
		contractSalaryCalculator.setListener(new Listener() {
		});
		Salary extra = contractSalaryCalculator.calculate(extraCtx);
		
		extra.getSalaryPayments().forEach( p -> System.out.println(p.getDescription() +" : " + p.getAmount() ));

		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), ( 1000.00 + 10.00 * 15.00) / 12 * 6, extra.getTotalPayment(), 0.005 );
	}

	@Test
	public void testExtraOverrideI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "SALARIO_BASE + PLUS_SALARIAL + ANTIGUEDAD";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, 
				new Extra() {
					{
						this.expression = "(SALARIO_BASE + PLUS_SALARIAL) + ANTIGUEDAD";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, 
				});

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()) 
				,new HashMap<String, String>() {
				} 
				,new String[] {
				} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		PaymentConceptRecord salarioBaseConcept = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord plusSalarialConcept = addConcept(aonContext, "PLUS_SALARIAL");
		PaymentConceptRecord antiguedadConcept = addConcept(aonContext, "ANTIGUEDAD");
		
		addData(aonContext, contract, contract.getStartDate(), null, "PLUS", "100.00");
		
		
		addPayment(aonContext, contract, contract.getStartDate(), null, salarioBaseConcept, "SALARIO BASE", "1000.00 * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0000);
		addPayment(aonContext, contract, contract.getStartDate(), null, plusSalarialConcept, "PLUS SALARIAL", "PLUS * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0000);
		addPayment(aonContext, contract, contract.getStartDate(), null, antiguedadConcept, "ANT1GUEDAD", "10.00  * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0000);
		
		int month ;
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		for ( month = 0 ; month < 12 ; month++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		
		Date firstDayOfYear = getFirstDayOfYear(getToday());
		Date overrideStartDate = add(firstDayOfYear, MONTH, 1);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "PAGA_EXTRA_15_12", "55.55");
		overrideStartDate = add(firstDayOfYear, MONTH, 3);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "PAGA_EXTRA_15_12", "33.33");
		overrideStartDate = add(firstDayOfYear, MONTH, 6);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "PAGA_EXTRA_15_12", "66.66");
		
		setData(aonContext, contract, "PLUS", "200.00");
		
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.DECEMBER);
		calendar.set(DAY_OF_MONTH, 15);
		Date issueDate = new Date(calendar.getTimeInMillis());
		int year = calendar.get(Calendar.YEAR);
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord extra = getExtra(aonContext, agreement.getId(), "15/12");
		
		ISalary salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate))
		.getSalary();
		
		Assert.assertEquals(1110.00/ 12 * 9 + ( 55.55+33.33+66.66 ) , salary.getTotalPayment(), DELTA);
		

	}
	
	@Test
	public void testExtraOverrideII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "SALARIO_BASE + PLUS_SALARIAL + ANTIGUEDAD";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "31/12";
						this.concept = conceptPagaExtra.getId();
					}
				}, 
				new Extra() {
					{
						this.expression = "(SALARIO_BASE + PLUS_SALARIAL) + ANTIGUEDAD";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "30/06";
						this.concept = conceptPagaExtra.getId();
					}
				}, 
				});

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()) 
				,new HashMap<String, String>() {
				} 
				,new String[] {
				} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		PaymentConceptRecord salarioBaseConcept = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord plusSalarialConcept = addConcept(aonContext, "PLUS_SALARIAL");
		PaymentConceptRecord antiguedadConcept = addConcept(aonContext, "ANTIGUEDAD");
		
		addData(aonContext, contract, contract.getStartDate(), null, "PLUS", "100.00");
		
		
		addPayment(aonContext, contract, contract.getStartDate(), null, salarioBaseConcept, "SALARIO BASE", "1000.00 * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0000);
		addPayment(aonContext, contract, contract.getStartDate(), null, plusSalarialConcept, "PLUS SALARIAL", "PLUS * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0000);
		addPayment(aonContext, contract, contract.getStartDate(), null, antiguedadConcept, "ANT1GUEDAD", "10.00  * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0000);
		
		int month ;
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		for ( month = 0 ; month < 11 ; month++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}

		//endDate = add(startDate, DAY_OF_MONTH, -1);
		//startDate = getFirstDayOfMonth(endDate);
		
		Date firstDayOfYear = getFirstDayOfYear(getToday());
		Date overrideStartDate = add(firstDayOfYear, MONTH, 1);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "PAGA_EXTRA_31_12", "55.55");
		overrideStartDate = add(firstDayOfYear, MONTH, 3);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "PAGA_EXTRA_31_12", "33.33");
		overrideStartDate = add(firstDayOfYear, MONTH, 6);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "PAGA_EXTRA_31_12", "66.66");
		
		
		Salary salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract))
		;
		
		salary.getSalaryPayments().forEach(p -> System.out.println(p.getDescription() +" = " + p.getAmount() + "," + p.getQuote() ));
		
		Assert.assertEquals(1110.00  + 1110.00/ 12 * 9 + ( 55.55+33.33+66.66 ) , salary.getTotalPayment(), DELTA);

	}
	
	

	@Test
	public void testExtraProrratedAndNot() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "/*NAVIDAD*/(SALARIO_BASE + PLUS_SALARIAL)";
							this.quoteExpression = "PRORRATEAR(_P)";
							this.month = Month.DECEMBER;
							this.start = "01/07";
							this.end = "31/12";
							this.issue = "21/12";
						}
					}, 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "/*VERANO*/(SALARIO_BASE + PLUS_SALARIAL)";
							this.quoteExpression = "PRORRATEAR(_P)";
							this.month = Month.JUNE;
							this.start = "01/01";
							this.end = "30/06";
							this.issue = "30/06";
						}
					}
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "SALARIO_MENSUAL * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "PLUS_MENSUAL * DIAS_TRABAJADOS/DIAS_MES";
							}
						}
				});
		
		addData(aonContext, category, getFirstDayOfYear(getToday()), null, new HashMap<String,String>(){
			{
				put("PLUS_MENSUAL", "10.00");
				put("SALARIO_MENSUAL", "1000.00");
			}
		});
		

		Date firstDayOfYear = getFirstDayOfYear(getToday()); 
		Date contractStartDate = firstDayOfYear; // 01/01

		ContractRecord contract = newContract(
				aonContext
				,contractStartDate
				, new HashMap<String,String>(){
					{
						put("DIAS_MES", "30.00");
					}
				}				
				,category);
		
		
		Date lastDayOfYear = getLastDayOfYear(contractStartDate);
		
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.FEBRUARY);
		calendar.set(DAY_OF_MONTH, 1);
		Date startProrratedDate = new Date(calendar.getTimeInMillis());
		
		calendar.set(MONTH, Calendar.MARCH);
		calendar.set(DAY_OF_MONTH, 31);
		Date endProrratedDate = new Date(calendar.getTimeInMillis());

		addPayment(aonContext, contract, startProrratedDate, endProrratedDate, conceptPagaExtra, "/*NAVIDAD*/(SALARIO_BASE + PLUS_SALARIAL)", "SALARIO_BASE + PLUS_SALARIAL" , "_P", "_P", PaymentType.CRA_0004);
		addPayment(aonContext, contract, startProrratedDate, endProrratedDate, conceptPagaExtra, "/*VERANO*/(SALARIO_BASE + PLUS_SALARIAL)", "SALARIO_BASE + PLUS_SALARIAL" , "_P", "_P", PaymentType.CRA_0004);

		
		for ( Date startDate = firstDayOfYear ; 
				get(startDate, Calendar.MONTH) < 5  ; 
				startDate = add(startDate, Calendar.MONTH,1) ) 
		{
			Date endDate = getLastDayOfMonth(startDate);
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);

			JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection) {
				
				@Override
				public void addPayment(Double amount, Double quote, Double tax, String description,
						java.util.Date startDate, java.util.Date endDate, IPayment payment,
						Map<String, ITimedVariable<?>> context) {
					System.out.println( startDate + "[" + description + " ]: " + amount + ", " + quote );
					super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				}
			};
			new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder).calculate(ctx);
			jooqSalaryBuilder.execute();
		}

		//@formatter:off
		
		calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.JUNE);
		calendar.set(DAY_OF_MONTH, 30);
		Date issueDate = new Date(calendar.getTimeInMillis());
		int year = calendar.get(Calendar.YEAR);

		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord extra = getExtra(aonContext, agreement.getId(), "30/06");
		ISQLContractSalaryCalculatorContext extraCtx = getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate);
		SmartContractSalaryCalculator<Salary> contractSalaryCalculator = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder());
		contractSalaryCalculator.setListener(new Listener() {
		});
		Salary salary = contractSalaryCalculator.calculate(extraCtx);
		
		salary.getSalaryPayments().forEach( p -> System.out.println(p.getDescription() +" : " + p.getAmount() ));

		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1010.00 /  6 * 4 , salary.getTotalPayment(), 0.005 );
	}

	@Test
	public void testExtraDuplicated() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");
		PaymentConceptRecord conceptPagaMarzo = addConcept(aonContext, "PAGA_MARZO", PaymentType.CRA_0004);
		PaymentConceptRecord conceptPagaVerano = addConcept(aonContext, "PAGA_VERANO", PaymentType.CRA_0004);
		PaymentConceptRecord conceptPagaNavidad  = addConcept(aonContext, "PAGA_NAVIDAD", PaymentType.CRA_0004);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
					new Extra() {
						{
							this.concept = conceptPagaNavidad.getId();
							this.expression = "/*NAVIDAD*/(SALARIO_BASE + PLUS_SALARIAL)";
							this.quoteExpression = "PRORRATEAR(_P)";
							this.month = Month.DECEMBER;
							this.start = "01/07";
							this.end = "31/12";
							this.issue = "21/12";
						}
					}, 
					new Extra() {
						{
							this.concept = conceptPagaVerano.getId();
							this.expression = "/*VERANO*/(SALARIO_BASE + PLUS_SALARIAL)";
							this.quoteExpression = "PRORRATEAR(_P)";
							this.month = Month.JUNE;
							this.start = "01/01";
							this.end = "30/06";
							this.issue = "30/06";
						}
					}, 
					new Extra() {
						{
							this.concept = conceptPagaMarzo.getId();
							this.expression = "/*MARZO*/(IMPORTE_PAGA_MARZO * DIAS_TRABAJADOS / DIAS_MES)";
							this.quoteExpression = "PRORRATEAR(_P)";
							this.month = Month.MARCH;
							this.start = "01/04 -1";
							this.end = "31/03";
							this.issue = "31/03";
						}
					}, 
					new Extra() {
						{
							this.concept = conceptPagaMarzo.getId();
							this.expression = "/*MARZO*/(IMPORTE_PAGA_BENIFICIOS * DIAS_TRABAJADOS / DIAS_MES)";
							this.quoteExpression = "PRORRATEAR(_P)";
							this.month = Month.MARCH;
							this.start = "01/04 -1";
							this.end = "31/03";
							this.issue = "31/03";
						}
					}
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "SALARIO_MENSUAL * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "PLUS_MENSUAL * DIAS_TRABAJADOS/DIAS_MES";
							}
						}
				});
		
		addData(aonContext, category, getFirstDayOfYear(getToday()), null, new HashMap<String,String>(){
			{
				put("PLUS_MENSUAL", "10.00");
				put("SALARIO_MENSUAL", "1000.00");
				put("IMPORTE_PAGA_MARZO", "1000.00");
			}
		});
		

		Date firstDayOfYear = getFirstDayOfYear(getToday()); 
		Date contractStartDate = firstDayOfYear; // 01/01

		ContractRecord contract = newContract(
				aonContext
				,contractStartDate
				, new HashMap<String,String>(){
					{
						put("DIAS_MES", "30.00");
					}
				}				
				,category);
		
		
		Date startDate = firstDayOfYear;
		while ( get(startDate, Calendar.MONTH) < Calendar.MARCH  )  
		{
			Date endDate = getLastDayOfMonth(startDate);
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);

			JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection) {
				
				@Override
				public void addPayment(Double amount, Double quote, Double tax, String description,
						java.util.Date startDate, java.util.Date endDate, IPayment payment,
						Map<String, ITimedVariable<?>> context) {
					//System.out.println( startDate + "[" + description + " ]: " + amount + ", " + quote );
					super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				}
			};
			new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder).calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(startDate, Calendar.MONTH,1);
		}

		//@formatter:off
		

		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
			connection, startDate, endDate, endDate, contract);

		SalaryBuilder salaryBuilder = new SalaryBuilder() {
		
        		@Override
        		public void addPayment(Double amount, Double quote, Double tax, String description,
        				java.util.Date startDate, java.util.Date endDate, IPayment payment,
        				Map<String, ITimedVariable<?>> context) {
        			System.out.println( startDate + "[" + description + " ]: " + amount + ", " + quote );
        			super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
        		}
		};
		
		Salary salary = new SmartContractSalaryCalculator<Salary>(salaryBuilder).calculate(ctx);

		//salary.getSalaryPayments().forEach( p -> System.out.println(p.getDescription() +" : " + p.getAmount() ));

		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1010.00 + 1000.00 / 12 * 3.00, salary.getTotalPayment(), 0.005);
	}
	
	@Test
	public void testExtraDuplicatedII() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");
		PaymentConceptRecord conceptPagaMarzo = addConcept(aonContext, "PAGA_MARZO", PaymentType.CRA_0004);
		PaymentConceptRecord conceptPagaVerano = addConcept(aonContext, "PAGA_VERANO", PaymentType.CRA_0004);
		PaymentConceptRecord conceptPagaNavidad  = addConcept(aonContext, "PAGA_NAVIDAD", PaymentType.CRA_0004);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
					new Extra() {
						{
							this.concept = conceptPagaNavidad.getId();
							this.expression = "/*NAVIDAD*/(SALARIO_BASE + PLUS_SALARIAL)";
							this.quoteExpression = "PRORRATEAR(_P)";
							this.month = Month.DECEMBER;
							this.start = "01/07";
							this.end = "31/12";
							this.issue = "21/12";
						}
					}, 
					new Extra() {
						{
							this.concept = conceptPagaVerano.getId();
							this.expression = "/*VERANO*/(SALARIO_BASE + PLUS_SALARIAL)";
							this.quoteExpression = "PRORRATEAR(_P)";
							this.month = Month.JUNE;
							this.start = "01/01";
							this.end = "30/06";
							this.issue = "30/06";
						}
					}, 
					new Extra() {
						{
							this.concept = conceptPagaMarzo.getId();
							this.expression = "/*MARZO*/(IMPORTE_PAGA_MARZO * DIAS_TRABAJADOS / DIAS_MES)";
							this.quoteExpression = "PRORRATEAR(_P)";
							this.month = Month.MARCH;
							this.start = "01/03 -1";
							this.end = "28/02";
							this.issue = "31/03";
						}
					}, 
					new Extra() {
						{
							this.concept = conceptPagaMarzo.getId();
							this.expression = "/*MARZO*/(IMPORTE_PAGA_BENIFICIOS * DIAS_TRABAJADOS / DIAS_MES)";
							this.quoteExpression = "PRORRATEAR(_P)";
							this.month = Month.MARCH;
							this.start = "01/03 -1";
							this.end = "28/02";
							this.issue = "31/03";
						}
					}
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "SALARIO_MENSUAL * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "PLUS_MENSUAL * DIAS_TRABAJADOS/DIAS_MES";
							}
						}
				});
		
		addData(aonContext, category, getFirstDayOfYear(getToday()), null, new HashMap<String,String>(){
			{
				put("PLUS_MENSUAL", "10.00");
				put("SALARIO_MENSUAL", "1000.00");
				put("IMPORTE_PAGA_MARZO", "1000.00");
			}
		});
		

		Date firstDayOfYear = getFirstDayOfYear(getToday()); 
		Date contractStartDate = firstDayOfYear; // 01/01

		ContractRecord contract = newContract(
				aonContext
				,contractStartDate
				, new HashMap<String,String>(){
					{
						put("DIAS_MES", "30.00");
					}
				}				
				,category);
		
		
		Date startDate = firstDayOfYear;
		while ( get(startDate, Calendar.MONTH) < Calendar.MARCH  )  
		{
			Date endDate = getLastDayOfMonth(startDate);
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);

			JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection) {
				
				@Override
				public void addPayment(Double amount, Double quote, Double tax, String description,
						java.util.Date startDate, java.util.Date endDate, IPayment payment,
						Map<String, ITimedVariable<?>> context) {
					//System.out.println( startDate + "[" + description + " ]: " + amount + ", " + quote );
					super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				}
			};
			new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder).calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(startDate, Calendar.MONTH,1);
		}

		//@formatter:off
		

		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
			connection, startDate, endDate, endDate, contract);

		SalaryBuilder salaryBuilder = new SalaryBuilder() {
		
        		@Override
        		public void addPayment(Double amount, Double quote, Double tax, String description,
        				java.util.Date startDate, java.util.Date endDate, IPayment payment,
        				Map<String, ITimedVariable<?>> context) {
        			System.out.println( startDate + "[" + description + " ]: " + amount + ", " + quote );
        			super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
        		}
		};
		
		Salary salary = new SmartContractSalaryCalculator<Salary>(salaryBuilder).calculate(ctx);

		//salary.getSalaryPayments().forEach( p -> System.out.println(p.getDescription() +" : " + p.getAmount() ));

		Assert.assertEquals(ContextVariable.TOTAL_PAYMENT.getName(), 1010.00 + 1000.00 / 12 * 2.00, salary.getTotalPayment(), 0.005);

		Assert.assertEquals(1010.00 / 12.00 * 2 + 1000.00 / 12.00 , salary.getExtraPayProration(), 0.005);
	}

	protected void addBaseCgcMin(AONContext aonContext) {
		addSystemData(aonContext, getFirstDayOfYear(getToday()), null,
				new HashMap<String,String>() {
					{
						put("BASE_CGC_MIN",
							"[ \"01\":(TIEMPO_COMPLETO ? 1056.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 6.37 * HORAS_NOMINA), "
							+"\"02\":(TIEMPO_COMPLETO ? 876.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 5.28 * HORAS_NOMINA), "
							+"\"03\":(TIEMPO_COMPLETO ? 762.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.59 * HORAS_NOMINA), "
							+"\"04\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA), "
							+"\"05\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA), "
							+"\"06\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA), "
							+"\"07\":(TIEMPO_COMPLETO ? 756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 4.56 * HORAS_NOMINA), "
							+"\"08\":(TIEMPO_COMPLETO ? 25.22 * DIAS_NOMINA : 4.56 * HORAS_NOMINA), \"09\":(TIEMPO_COMPLETO ? 25.22 * DIAS_NOMINA : 4.56 * HORAS_NOMINA), "
							+"\"10\":(TIEMPO_COMPLETO ? 25.22* DIAS_NOMINA : 4.56 * HORAS_NOMINA), \"11\":(TIEMPO_COMPLETO ? 25.22 * DIAS_NOMINA : 4.56 * HORAS_NOMINA) ] [GRUPO_COTIZACION]");
					}
				});
	}
	// ------------------------------------------------------------------------
	
	protected ContextVariable getPeriodVariable() {
		return MONTH_DAYS;
	}
	
}
