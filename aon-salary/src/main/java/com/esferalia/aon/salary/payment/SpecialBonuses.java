package com.esferalia.aon.salary.payment;

import java.util.List;

public class SpecialBonuses extends CompositePayment {

	@Override
	public void addPayment(IPayment p) {
		addPayment(p,false);
	}

	public List<IPayment> getValues() {
		return getPayments();
	}
	
}
