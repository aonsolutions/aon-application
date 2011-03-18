package com.esferalia.aon.payroll.calculator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class HierarchyPayments extends HierarchyIterator<IContractPayment> {
	
	private Set<String> names;
	
	public HierarchyPayments(Iterator<IContractPayment> ... payments) {
		super(payments);
		names = new HashSet<String>();
	}
	
	@Override
	protected IContractPayment next(IContractPayment e) {
		String name = e.getName();
		if ( name == null ) 
			return e;
		if ( names.add(name))
			return e;
		else
			return  null;
	}

}
