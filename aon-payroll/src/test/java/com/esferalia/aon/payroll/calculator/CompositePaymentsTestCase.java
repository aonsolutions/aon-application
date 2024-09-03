package com.esferalia.aon.payroll.calculator;

import static com.esferalia.aon.salary.expression.ExpressionScope.AGREEMENT;
import static com.esferalia.aon.salary.expression.ExpressionScope.CONTRACT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class CompositePaymentsTestCase {

	@Test
	public void testIterator() {

		Date startDate = AonDateUtils.getYearFirstDay(new Date());

		Collection<IContractPayment> agreementPayments = new ArrayList<IContractPayment>();
		agreementPayments.add(new SimpleContractPayment().setName("BASE")
				.setStartDate(startDate).setExpressionScope(AGREEMENT));
		agreementPayments.add(new SimpleContractPayment().setName("EXTRA")
				.setStartDate(startDate).setExpressionScope(AGREEMENT));
		agreementPayments.add(new SimpleContractPayment().setName("PLUS")
				.setStartDate(startDate).setExpressionScope(AGREEMENT));
		agreementPayments.add(new SimpleContractPayment()
				.setExpression("AGREEMENT").setStartDate(startDate)
				.setExpressionScope(AGREEMENT));

		Collection<IContractPayment> contractPayments = new ArrayList<IContractPayment>();
		contractPayments.add(new SimpleContractPayment()
				.setExpression("CONTRACT").setStartDate(startDate)
				.setExpressionScope(CONTRACT));

		// Test NO override
		Collection<IContractPayment> payments = new CompositePayments<IContractPayment>(
				contractPayments, agreementPayments);
		for (IContractPayment payment : payments) {
			if (AonStringUtils.equals(payment.getName(), "BASE"))
				assertEquals(payment.getScope(), AGREEMENT);
			else if (AonStringUtils.equals(payment.getName(), "EXTRA"))
				assertEquals(payment.getScope(), AGREEMENT);
			else if (AonStringUtils.equals(payment.getName(), "PLUS"))
				assertEquals(payment.getScope(), AGREEMENT);
			else if (AonStringUtils
					.equals(payment.getExpression(), "AGREEMENT"))
				assertEquals(payment.getScope(), AGREEMENT);
			else if (AonStringUtils.equals(payment.getExpression(), "CONTRACT"))
				assertEquals(payment.getScope(), CONTRACT);
			else
				fail();
		}

		// Test override one 'EXTRA'
		contractPayments.add(new SimpleContractPayment().setName("EXTRA")
				.setStartDate(startDate).setExpressionScope(CONTRACT));
		payments = new CompositePayments<IContractPayment>(contractPayments,
				agreementPayments);
		for (IContractPayment payment : payments) {
			if (AonStringUtils.equals(payment.getName(), "BASE"))
				assertEquals(payment.getScope(), AGREEMENT);
			else if (AonStringUtils.equals(payment.getName(), "EXTRA"))
				assertEquals(payment.getScope(), CONTRACT);
			else if (AonStringUtils.equals(payment.getName(), "PLUS"))
				assertEquals(payment.getScope(), AGREEMENT);
			else if (AonStringUtils
					.equals(payment.getExpression(), "AGREEMENT"))
				assertEquals(payment.getScope(), AGREEMENT);
			else if (AonStringUtils.equals(payment.getExpression(), "CONTRACT"))
				assertEquals(payment.getScope(), CONTRACT);
			else
				fail();
		}
		
		// Test override partially
		Date overrideDate = AonDateUtils.addDays(startDate, 10);
		contractPayments.add(new SimpleContractPayment().setName("PLUS")
				.setStartDate(overrideDate).setExpressionScope(CONTRACT));
		for (IContractPayment payment : payments) {
			if (AonStringUtils.equals(payment.getName(), "BASE"))
				assertEquals(payment.getScope(), AGREEMENT);
			else if (AonStringUtils.equals(payment.getName(), "EXTRA"))
				assertEquals(payment.getScope(), CONTRACT);
			else if (AonStringUtils.equals(payment.getName(), "PLUS") && (payment.getStartDate().equals(startDate)))
				assertEquals(payment.getScope(), AGREEMENT);
			else if (AonStringUtils.equals(payment.getName(), "PLUS") && (payment.getStartDate().equals(overrideDate)))
				assertEquals(payment.getScope(), CONTRACT);
			else if (AonStringUtils
					.equals(payment.getExpression(), "AGREEMENT"))
				assertEquals(payment.getScope(), AGREEMENT);
			else if (AonStringUtils.equals(payment.getExpression(), "CONTRACT"))
				assertEquals(payment.getScope(), CONTRACT);
			else
				fail();
		}
	}

}
