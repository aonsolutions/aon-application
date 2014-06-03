package com.esferalia.aon.payroll.calculator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class HierarchyDeductions<T extends IContractDeduction >  extends HierarchyIterator<T> {

	private Set<String> names;

	public HierarchyDeductions(Iterator<T>... childs) {
		super(childs);
		names = new HashSet<String>();
	}

	
	@Override
	protected T next(T e) {
		String name = e.getName();
		if ( name == null ) 
			return e;
		if ( names.add(name))
			return e;
		else
			return  null;
	}

	
	
}
