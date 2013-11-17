package com.esferalia.aon.gwt.payroll.shared;

import java.util.Comparator;





public class  ItemComparator<E extends Enum> implements Comparator<Item<E>> {
	@Override
	public int compare(Item<E> p0, Item<E> p1) {
		
		Integer id0= p0.getId();
		Integer id1= p1.getId()	;
		
		
		if ( id0 == null && id1 != null)
			return -1; // p0 < p1  
		
		if ( id0 != null && id1 == null)
			return 1; // p0 > p1  

		if ( id0 < 0 && id1 >= 0)
			return 1; // p0 > p1
		
		if ( id1 < 0 && id0 >= 0)
			return -1; // p0 < p1
			
		if ( id0 != null && id1 != null)
			return Math.abs(id0) - Math.abs(id1); // p0 - p1  

		E type0 = p0.getType();
		E type1 = p1.getType();

		if ( type0 != null && type1 == null )
			return 1;
		if ( type0 == null && type1 != null )
			return -1;
		
		int compareTo = type0 == type1 ? 0 : type0.compareTo(type1);
		if (compareTo != 0) {
			return compareTo;
		}
		String description0 = p0.getDescription();
		String description1 = p1.getDescription();
		if (description0 == null) {
			return description1 == null ? 0 : -1;
		}
		return description0.compareTo(description1);
	}
}