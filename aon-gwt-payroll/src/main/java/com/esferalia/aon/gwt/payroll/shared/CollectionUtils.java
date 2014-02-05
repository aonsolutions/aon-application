package com.esferalia.aon.gwt.payroll.shared;

import java.util.Collection;

public class CollectionUtils {

	public static boolean isEmpty(Collection<?> collection){
		return collection == null || collection.isEmpty();
	}
	
}
