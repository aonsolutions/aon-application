package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TOTAL_PAYMENT;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Test;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;

public class SQLAgreementTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000001;

	@Test
	public void testRedefinedPaymentsI() throws ExpressionException, SQLException,
			SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		AgreementRecord agreement = newAgreement(aonContext);
		AgreementLevelCategoryRecord category = newAgreementCategory(aonContext, agreement);

		PaymentConceptRecord conceptP = addConcept(aonContext, "P");

		addPayments(aonContext, agreement, getFirstDayOfYear(getToday()), 
				new Payment [] {
			new Payment(){
				{
					this.concept = conceptP.getId();
					this.expression = "100";
				}
			}
			
		});
		
		ContractRecord contract = newContract(aonContext, getFirstDayOfMonth(getToday()),
				new HashMap<String, String>() {}
				, new String[] {}
				,new String[] {}, 
				category);
		
		
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
		Assert.assertEquals(String.format("%s",TOTAL_PAYMENT), 100.00, salary.getTotalPayment());

		addPayments(aonContext, contract.getDomain(), agreement, getFirstDayOfYear(getToday()), 
				new Payment [] {
			new Payment(){
				{
					this.concept = conceptP.getId();
					this.expression = "666";
				}
			}
			
		});
		
		// First of all ensure that agreement domain and contract domain are different.
		Assert.assertNotSame("DOMAIN", agreement.getDomain(), contract.getDomain());
		
		
		ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals(String.format("%s",TOTAL_PAYMENT), 666.00, salary.getTotalPayment());
		

		addPayments(aonContext, agreement, getFirstDayOfYear(getToday()), 
				new Payment [] {
			new Payment(){
				{
					this.concept = conceptP.getId();
					this.expression = "200";
				}
			}
			
		});

		ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals(String.format("%s",TOTAL_PAYMENT), 666.00, salary.getTotalPayment());

		addPayments(aonContext, contract.getDomain(), agreement, getFirstDayOfYear(getToday()), 
				new Payment [] {
			new Payment(){
				{
					this.concept = conceptP.getId();
					this.expression = "666";
				}
			}
			
		});

		ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals(String.format("%s",TOTAL_PAYMENT), 666.00 * 2, salary.getTotalPayment());

		addPayments(aonContext, contract.getDomain(), agreement, getFirstDayOfYear(getToday()), 
				new Payment [] {
			new Payment(){
				{
					this.concept = conceptP.getId();
					this.expression = "666";
				}
			}
			
		});

		ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals(String.format("%s",TOTAL_PAYMENT), 666.00 * 3, salary.getTotalPayment());
	}


}
