package com.esferalia.aon.payroll.calculator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.esferalia.aon.salary.expression.Period;

public class HierarchyPayments extends HierarchyIterator<IContractPayment> {
	
	
	private Map<String, Integer> visited;
	
	public HierarchyPayments(Iterator<IContractPayment> ... payments) {
		super(payments);
		visited = new HashMap<String, Integer>();
	}
	
	@Override
	protected IContractPayment next(IContractPayment e) {
		if ( visit(e))
			return e;
		else
			return  null;
	}
	
	private boolean visit (IContractPayment e ) {
		String name = e.getName();
		if ( name == null ) { 
			return false;
		} 
		
		Integer visitedLevel = 
			visited.get(name);
		
		Integer currentLevel = 
			getLevel();
		
		if ( visitedLevel == null ){
			visited.put(name, currentLevel);
			return true;
		}
		
		if (visitedLevel == currentLevel ){
			return true;
		}
		
		return false;
	}

}
