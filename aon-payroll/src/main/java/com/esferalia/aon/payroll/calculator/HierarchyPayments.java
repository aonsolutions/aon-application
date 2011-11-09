package com.esferalia.aon.payroll.calculator;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sun.security.action.GetLongAction;

import com.esferalia.aon.salary.expression.Period;

public class HierarchyPayments extends HierarchyIterator<IContractPayment> {
	
	private static class Visited {
		private Integer 		level;
		private List<Period> 	periods;
		
		public Visited(Integer level) {
			this.level = level;
			this.periods = new ArrayList<Period>();
		}
		
		public boolean add(Period period){
			for (Period visited : periods) {
				if ( visited.intersects(period) ) {
					return false;
				}
			}
			periods.add(period);
			return true;
		}

		public boolean add(Date start, Date end){
			return add( new Period(start, end) );
		}

		public boolean add(IContractPayment payment){
			return add(payment.getStartDate(), payment.getEndDate());
		}
		
	}
	
	private Map<String, Visited> visitedMap;
	
	public HierarchyPayments(Iterator<IContractPayment> ... payments) {
		super(payments);
		visitedMap = new HashMap<String, Visited>();
		
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
			return true;
		} 
		
		Visited visited = 
			visitedMap.get(name);
		
		Integer currentLevel = 
			getLevel();

		if ( visited == null ){
			visited = new Visited(currentLevel);
			visited.add(e);
			visitedMap.put(name, visited );
			return true;
		}
		
		
		if (visited.level == currentLevel ){
			return visited.add(e);
			//return true;
		}
		
		return false;
	}

}
