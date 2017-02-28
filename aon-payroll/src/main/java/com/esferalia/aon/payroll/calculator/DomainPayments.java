package com.esferalia.aon.payroll.calculator;

import java.util.Collection;
import java.util.Iterator;

import com.esferalia.aon.salary.expression.Period;

public class DomainPayments<T extends ISystemPayment> extends CompositePayments<T> {
	
	
	static class PeriodsSystemPaymentIterator extends PeriodsContractPaymentIterator implements ISystemPayment{

		public PeriodsSystemPaymentIterator(ISystemPayment payment,
				Iterator<Period> periodsIt) {
			super(payment, periodsIt);
		}
		
		@Override
		public int getDomain() {
			return ((ISystemPayment)getPayment()).getDomain();
		}
		
	}
	
	public DomainPayments(Collection<T>... payments) {
		super(payments);
	}

	@Override
	protected int getLevel(T payment) {
		return payment.getDomain();
	};
	
	
	@Override
	protected Iterator<IContractPayment> getIterator4(T payment,
			Iterator<Period> periodsIt) {
			return new PeriodsSystemPaymentIterator(payment, periodsIt);
	}
}
