package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractDeduction.CONTRACT_DEDUCTION;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

import org.junit.Test;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;

import junit.framework.Assert;

public class SQLSpecialDeductionsTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000001;

	@Test
	public void testBASE_CGC_I() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()), Collections.emptyMap());
		addPayment(aonContext, contract, SalaryType.SETTLE, "500");

		//@formatter:off
		addDeduction(
				aonContext,
				contract,
				"BASE_CGC = /*user*/ 100.00 /**/; BUILDER.setCgcBase(BASE_CGC); REMOVE();",
				getFirstDayOfMonth(getToday()),
				getLastDayOfMonth(getToday()));
		//@formatter:on
		//@formatter:off
		addDeduction(
				aonContext,
				contract,
				"BASE_CGP = /*user*/ 100.00 /**/; BUILDER.setCgpBase(BASE_CGC); REMOVE();",
				getFirstDayOfMonth(getToday()),
				getLastDayOfMonth(getToday()));
		//@formatter:on
		addDeduction(
				aonContext,
				contract,
				"BASE_CGC * 0.50",
				contract.getStartDate(),
				contract.getEndDate());

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		SQLContractSettleCalculatorContext ctx = new SQLContractSettleCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();

		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder(){
			@Override
			public void addDeduction(Double amount, String description,
					java.util.Date start, java.util.Date end,
					IDeduction deduction,
					Map<String, ITimedVariable<?>> context) {
				super.addDeduction(amount, description, start, end, deduction, context);
				System.out.println("amount : " + amount );
			}
		});
		ISalary salary = calculator.calculate(ctx);
		
		
		Assert.assertEquals(String.format("%s :", ContextVariable.TOTAL_PAYMENT), 500.00, salary.getTotalPayment());
		Assert.assertEquals(String.format("%s :", ContextVariable.CGC_BASE), 100.00, salary.getCommonBase());
		Assert.assertEquals(String.format("%s :", ContextVariable.TOTAL_LIQUID), 500.00 - ( 100*0.50) , salary.getTotalLiquid());
	}

	// ------------------------------------------------------------------------

	protected final void addDeduction(AONContext aonContext,
			ContractRecord contract, String expression, Date startDate,
			Date endDate) {
		aonContext
				.getDslContext()
				.insertInto(CONTRACT_DEDUCTION)
				.set(CONTRACT_DEDUCTION.DOMAIN, contract.getDomain())
				.set(CONTRACT_DEDUCTION.CONTRACT, contract.getId())
				.set(CONTRACT_DEDUCTION.START_DATE, contract.getStartDate())
				.set(CONTRACT_DEDUCTION.END_DATE, contract.getEndDate())
				.set(CONTRACT_DEDUCTION.EXPRESSION, expression)
				.set(CONTRACT_DEDUCTION.TYPE,
						(byte) DeductionType.OTHER.ordinal()).execute();

	}
	

	protected final void addPayment(AONContext aonContext,
			ContractRecord contract, SalaryType type,  String expression) {
		aonContext
				.getDslContext()
				.insertInto(CONTRACT_PAYMENT)
				.set(CONTRACT_PAYMENT.DOMAIN, contract.getDomain())
				.set(CONTRACT_PAYMENT.CONTRACT, contract.getId())
				.set(CONTRACT_PAYMENT.START_DATE, contract.getStartDate())
				.set(CONTRACT_PAYMENT.END_DATE, contract.getEndDate())
				.set(CONTRACT_PAYMENT.EXPRESSION, expression)
				.set(CONTRACT_PAYMENT.QUOTE_EXPRESSION, expression)
				.set(CONTRACT_PAYMENT.TYPE,
						(byte) PaymentType.CRA_0001.ordinal())
				.set(CONTRACT_PAYMENT.SALARY_TYPE,
						(byte) type.ordinal()).execute();

	}

}
