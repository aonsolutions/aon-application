package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.BR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SALARY_START;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TOTAL_LIQUID;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TOTAL_PAYMENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_DAYS;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getMax;
import static java.lang.String.format;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Test;

import com.code.aon.common.enumeration.Month;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.AgreementPayment;
import com.esferalia.aon.jooq.tables.PaymentConcept;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;

public class SQLExtraTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000001;

	@Test
	public void testDuplicatePaymentsI() throws ExpressionException, SQLException,
			SalaryException {
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
		
		ContractRecord contract = newContract(aonContext, getFirstDayOfMonth(getToday()),
				new HashMap<String, String>() {}
				, new String[] {}
				,new String[] {}, 
				category);
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
		
		Assert.assertEquals(String.format("%s",TOTAL_PAYMENT), 1875.00, salary.getTotalPayment());
		
		Assert.assertEquals(String.format("%s",ContextVariable.CGC_BASE), 1875.00 + (1875.00*2/12), salary.getCommonBase());
		

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
		
		Assert.assertEquals(String.format("%s",TOTAL_PAYMENT), 1875.00, salary.getTotalPayment());
		
		Assert.assertEquals(String.format("%s",ContextVariable.CGC_BASE), 1875.00 + (1750.00*2/12), salary.getCommonBase());
		

	}
	// ------------------------------------------------------------------------
	protected final  PaymentConceptRecord addConcept(AONContext aonContext, String code) {
		DomainRecord domain = newDomain(aonContext);
		return aonContext
				.getDslContext()
				.insertInto(PAYMENT_CONCEPT)
				.set(PAYMENT_CONCEPT.DOMAIN, domain.getId())
				.set(PAYMENT_CONCEPT.CODE, code)
				.set(PAYMENT_CONCEPT.TYPE,
						(byte) PaymentType.CRA_0001.ordinal())
				.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "_P")
				.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, "_P")
				.returning().fetchOne();

	}

	protected final void addPayment(AONContext aonContext,
			ContractRecord contract, 
			PaymentConceptRecord concept, 
			String expression) {
		aonContext
				.getDslContext()
				.insertInto(CONTRACT_PAYMENT)
				.set(CONTRACT_PAYMENT.DOMAIN, contract.getDomain())
				.set(CONTRACT_PAYMENT.PAYMENT_CONCEPT, concept.getId())
				.set(CONTRACT_PAYMENT.CONTRACT, contract.getId())
				.set(CONTRACT_PAYMENT.START_DATE, contract.getStartDate())
				.set(CONTRACT_PAYMENT.END_DATE, contract.getEndDate())
				.set(CONTRACT_PAYMENT.EXPRESSION, expression)
				.set(CONTRACT_PAYMENT.QUOTE_EXPRESSION, "_P")
				.set(CONTRACT_PAYMENT.IRPF_EXPRESSION, "_P")
				.set(CONTRACT_PAYMENT.TYPE,
						(byte) PaymentType.CRA_0001.ordinal())
				.set(CONTRACT_PAYMENT.SALARY_TYPE,
						(byte) SalaryType.SALARY.ordinal()).execute();

	}

}
