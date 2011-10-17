package com.esferalia.aon.payroll.irpf.sql;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Calendar;

import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.payroll.calculator.IContractPayment;

public class HierarchyMonthPayments extends HierarchyMonthIterator<IContractPayment> {
	
	private double var = 0.00;
	private Map<String, Integer> visited;
	
	public HierarchyMonthPayments(Iterator<IContractPayment> ... payments) {
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

		Calendar calendar = 
			Calendar.getInstance();
		calendar.setTime(e.getStartDate());
		int start = calendar.get(Calendar.MONTH);

		int end = 11 ;
		Date endDate = e.getEndDate();
		if ( endDate != null ){
			calendar.setTime(endDate);
			end = calendar.get(Calendar.MONTH);
		}
		
		int monthBit = 0x0000;
		for ( int i = start; i <= end ; i++ ){
			monthBit |= 0x0001 << i;
		}

		int monthMask = visited.containsKey(name) ? 
			visited.get(name) : 0x00;
		
		int level = getLevel();
			
		if ( level == 0 ){
			visited.put(name, monthMask | monthBit);
			/*
			System.out.printf ("+ [%x] %s : %s %tF..%tF.\r\n", 
					monthBit,
					name,
					e.getExpression(),
					e.getStartDate(),
					e.getEndDate());
					*/
			return true;
		}

		if ( name == null ) { 
			return false;
		} 				

		if ( (monthMask & monthBit ) == monthBit ){
			/*
			System.out.printf ("- [%x] %s : %s %tF..%tF.\r\n", 
					monthBit,
					name,
					e.getExpression(),
					e.getStartDate(),
					e.getEndDate());
				*/
			return false;
		}		
		/*
		var += Double.valueOf(e.getExpression());
		System.out.printf ("+ %s : %s %tF..%tF.\r\n",
				name,
				var,
				e.getStartDate(),
				e.getEndDate());
		visited.put(name, monthMask | monthBit  );*/
		return true;
	}

}
