package com.esferalia.aon.payroll.calculator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;


public class HierarchyDeductions<T extends IContractDeduction >  extends HierarchyIterator<T> {

	private Map<String, Integer> names;

	public HierarchyDeductions(Iterator<T>... childs) {
		super(childs);
		names = new HashMap<>();
	}

	
	@Override
	protected T next(T e) {
		String name = e.getName();
		if ( name == null ) 
			return e;
		if ( add(name))
			return e;
		else
			return  null;
	}

	
	private boolean add(String name) {
	    if ( !names.containsKey(name) ) {
		names.put(name, getLevel());
		return true;
	    }  else {
		int currLevel = getLevel();
		int prevLevel = names.get(name);
		return prevLevel == currLevel;
	    }
	}
	
	
}
