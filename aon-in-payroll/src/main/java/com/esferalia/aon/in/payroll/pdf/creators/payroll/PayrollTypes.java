package com.esferalia.aon.in.payroll.pdf.creators.payroll;

import java.util.Locale;
import java.util.ResourceBundle;

public class PayrollTypes {

	public static enum Type {
	    SALARY,
	    ARREARS_WAGE,
	    SETTLEMENT,
	    EXTRA_HOURS;
	}
	 
	public static String toString(Type type,Locale lang) {
	
		ResourceBundle words = ResourceBundle.getBundle("com.esferalia.aon.in.payroll.pdf.creators.payroll._default.bundles.PayrollTypesBundle",lang);
		switch (type) {
			case SALARY: return words.getString("SALARIO");
			case ARREARS_WAGE: return words.getString("ATRASOS");
			case SETTLEMENT: return words.getString("FINIQUITO");
			case EXTRA_HOURS: return words.getString("HORAS EXTRA");
			default: break;
		}
		
		return "";
	}
	
}
