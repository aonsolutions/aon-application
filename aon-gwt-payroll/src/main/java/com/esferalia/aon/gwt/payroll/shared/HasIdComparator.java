package com.esferalia.aon.gwt.payroll.shared;

import java.util.Comparator;

public class HasIdComparator<T extends HasId<V>, V extends Comparable<V>> implements Comparator<T> {

	
	
	
	@Override
	public int compare(T o1, T o2) {
		V id1 = o1.getId();
		V id2 = o2.getId();
		
		if ( id1== id2 )
			return 0;
		if ( id1 == null)
			return -1;
		if ( id2 == null) 
			return 1;
		
		return id1.compareTo(id2);
	}


}
