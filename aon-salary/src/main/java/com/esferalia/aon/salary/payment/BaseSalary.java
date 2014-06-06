package com.esferalia.aon.salary.payment;

import java.util.List;

import com.code.aon.AonVersion;

public class BaseSalary extends CompositePayment {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void addPayment(IPayment p) {
		addPayment(p,false);
	}

	public List<IPayment> getValues() {
		return getPayments();
	}
	
}
