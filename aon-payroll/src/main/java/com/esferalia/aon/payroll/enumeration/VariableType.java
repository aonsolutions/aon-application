package com.esferalia.aon.payroll.enumeration;


public enum VariableType {
	
	// PRIMITIVE
	INTEGER, 
	DOUBLE, 
	STRING, 
	BOOLEAN,
	DATE,
	
	// DROPS & LISTS
	DROP,
	TC2_DROP, 
	CATEGORY_DROP, 
	QUOTE_GROUP_DROP,
	OCCUPATION_DROP, 
	QUOTE_IT_DROP, 
	
	// LOOKUP
	CNO_LOOKUP,
	TRAINING_CENTER_LOOKUP,
	
	// COMPLEX
	EXPRESSION,
	TABLE,
	
	// OTHER
	UNKNOWN 
	;

}
