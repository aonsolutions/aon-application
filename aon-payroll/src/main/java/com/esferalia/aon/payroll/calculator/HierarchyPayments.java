package com.esferalia.aon.payroll.calculator;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.Period;

public class HierarchyPayments extends HierarchyIterator<IContractPayment> {
	
	public HierarchyPayments(HierarchyIterator<IContractPayment> ... payments) {
		super(payments);
	}
	
	@Override
	protected IContractPayment next(IContractPayment e) {
		return e;
	}

}
