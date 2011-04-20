package com.esferalia.aon.payroll.calculator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class HierarchyDeductions  extends HierarchyIterator<IContractDeduction> {

	private Set<String> names;

	public HierarchyDeductions(Iterator<IContractDeduction>... childs) {
		super(childs);
		names = new HashSet<String>();
	}

	
	@Override
	protected IContractDeduction next(IContractDeduction e) {
		String name = e.getName();
		if ( name == null ) 
			return e;
		if ( names.add(name))
			return e;
		else
			return  null;
	}

	
	
}
