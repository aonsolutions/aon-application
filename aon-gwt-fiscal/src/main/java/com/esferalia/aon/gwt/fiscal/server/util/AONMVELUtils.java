package com.esferalia.aon.gwt.fiscal.server.util;

import org.mvel2.MVEL;

public class AONMVELUtils {
	
	public static Double mathExpression(String expression) {
		try {
			Number n = (Number) MVEL.eval( expression ); 
			return n!=null?n.doubleValue():null;
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}
	
}
