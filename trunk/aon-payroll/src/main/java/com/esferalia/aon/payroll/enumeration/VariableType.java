package com.esferalia.aon.payroll.enumeration;

import java.util.Date;


public enum VariableType {
	
	// PRIMITIVE
	INTEGER (Integer.class), 
	DOUBLE (Double.class), 
	STRING (String.class), 
	BOOLEAN (Boolean.class),
	DATE (Date.class),
	
	// DROPS & LISTS
	DROP (null),
	TC2_DROP (null), 
	CATEGORY_DROP (null), 
	QUOTE_GROUP_DROP (null),
	OCCUPATION_DROP (null), 
	QUOTE_IT_DROP (null), 
	
	// LOOKUP
	CNO_LOOKUP (null),
	TRAINING_CENTER_LOOKUP (null),
	
	// COMPLEX
	EXPRESSION (null),
	TABLE (null),
	
	// OTHER
	UNKNOWN  (null)
	;
	
	Class<?> javaType;
	

	private VariableType(Class<?> javaType) {
		this.javaType = javaType;
	}
	
	public Class<?> getJavaType() {
		return javaType;
	}

}
