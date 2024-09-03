package com.esferalia.aon.payroll.calculator;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class DomainPaymentsTestCase {

	public static class SimpleSystemPayment extends AbstractContractPayment<SimpleSystemPayment> implements ISystemPayment{
		
		int domain;
		
		public int getDomain() {
			return domain;
		}
		
		public SimpleSystemPayment setDomain(int domain) {
			this.domain = domain;
			return this;
		}
	
	}
	
	
	@Test
	public void testIterator() {

		Date startDate = AonDateUtils.getYearFirstDay(new Date());

		Collection<ISystemPayment> parentPayments = new ArrayList<ISystemPayment>();
		parentPayments.add(new SimpleSystemPayment().setName("BASE")
				.setStartDate(startDate).setDomain(66));
		parentPayments.add(new SimpleSystemPayment().setName("EXTRA")
				.setStartDate(startDate).setDomain(66));
		parentPayments.add(new SimpleSystemPayment().setName("PLUS")
				.setStartDate(startDate).setDomain(66));
		parentPayments.add(new SimpleSystemPayment()
				.setExpression("66").setStartDate(startDate)
				.setDomain(66));

		Collection<ISystemPayment> childPayments = new ArrayList<ISystemPayment>();
		childPayments.add(new SimpleSystemPayment()
				.setExpression("55").setStartDate(startDate)
				.setDomain(55));

		// Test NO override
		Collection<ISystemPayment> payments = new DomainPayments<ISystemPayment>(
				childPayments, parentPayments);
		for (ISystemPayment payment : payments) {
			if (AonStringUtils.equals(payment.getName(), "BASE"))
				assertEquals(payment.getDomain(), 66);
			else if (AonStringUtils.equals(payment.getName(), "EXTRA"))
				assertEquals(payment.getDomain(), 66);
			else if (AonStringUtils.equals(payment.getName(), "PLUS"))
				assertEquals(payment.getDomain(), 66);
			else if (AonStringUtils
					.equals(payment.getExpression(), "66"))
				assertEquals(payment.getDomain(), 66);
			else if (AonStringUtils.equals(payment.getExpression(), "55"))
				assertEquals(payment.getDomain(), 55);
			else
				fail();
		}

		// Test override one 'EXTRA'
		childPayments.add(new SimpleSystemPayment().setName("EXTRA")
				.setStartDate(startDate).setDomain(55));
		payments = new DomainPayments<ISystemPayment>(childPayments,
				parentPayments);
		for (ISystemPayment payment : payments) {
			if (AonStringUtils.equals(payment.getName(), "BASE"))
				assertEquals(payment.getDomain(), 66);
			else if (AonStringUtils.equals(payment.getName(), "EXTRA"))
				assertEquals(payment.getDomain(), 55);
			else if (AonStringUtils.equals(payment.getName(), "PLUS"))
				assertEquals(payment.getDomain(), 66);
			else if (AonStringUtils
					.equals(payment.getExpression(), "66"))
				assertEquals(payment.getDomain(), 66);
			else if (AonStringUtils.equals(payment.getExpression(), "55"))
				assertEquals(payment.getDomain(), 55);
			else
				fail();
		}
		
		// Test override partially
		Date overrideDate = AonDateUtils.addDays(startDate, 10);
		childPayments.add(new SimpleSystemPayment().setName("PLUS")
				.setStartDate(overrideDate).setDomain(55));
		payments = new DomainPayments<ISystemPayment>(childPayments,
				parentPayments);
		for (ISystemPayment payment : payments) {
			if (AonStringUtils.equals(payment.getName(), "BASE"))
				assertEquals(payment.getDomain(), 66);
			else if (AonStringUtils.equals(payment.getName(), "EXTRA"))
				assertEquals(payment.getDomain(), 55);
			else if (AonStringUtils.equals(payment.getName(), "PLUS") && (payment.getStartDate().equals(startDate)))
				assertEquals(payment.getDomain(), 66);
			else if (AonStringUtils.equals(payment.getName(), "PLUS") && (payment.getStartDate().equals(overrideDate)))
				assertEquals(payment.getDomain(), 55);
			else if (AonStringUtils
					.equals(payment.getExpression(), "66"))
				assertEquals(payment.getDomain(), 66);
			else if (AonStringUtils.equals(payment.getExpression(), "55"))
				assertEquals(payment.getDomain(), 55);
			else
				fail();
		}
	}

}
