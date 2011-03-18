package com.esferalia.aon.payroll.calculator;

import java.util.Iterator;


public class HierarchyDeductions  extends HierarchyIterator<IContractDeduction> {


	public HierarchyDeductions(Iterator<IContractDeduction>... childs) {
		super(childs);
	}

	
	@Override
	protected IContractDeduction next(IContractDeduction e) {
		return e;
	}

	
	
}
